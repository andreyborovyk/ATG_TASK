 /*<ATGCOPYRIGHT>
 * Copyright (C) 2000-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.commerce.catalog.custom;

import atg.adapter.gsa.GSAItemDescriptor;
import atg.adapter.gsa.GSARepository;
import atg.adapter.version.VersionRepository;
import atg.commerce.catalog.CMSService;
import atg.repository.*;
import atg.core.util.ResourceUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.security.ThreadSecurityManager;
import atg.security.User;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.versionmanager.Branch;
import atg.versionmanager.VersionManager;
import atg.versionmanager.Workspace;
import atg.versionmanager.exceptions.VersionException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javax.transaction.SystemException;

/**
 * <p>
 * This CMSService performs the following service functions. It shares a common tree-walk
 * algorithm. The idea being to try and mimimize database access. There are some times 
 * when an item is retrieved more than once from the db, but the code tries to minimze this.
 * 
 *
 * Available service functions:
 * AGS_GENPROPERTIES - generate catalog, catalogs, and AncestorCategories (for catalogs, products, skus)
 * AGS_GENCATALOGS   - generate catalog and catalogs (for catalogs, products and skus)
 * AGS_GENPARENTCATS - generate parent categories (for products)
 * AGS_GENANCESTORS  - generate ancestors (for products and skus)
 * CUS_UPDATECATALOGS - generate the CUS catalog/category/catalogFolders info 
 * 
 * @author Jon Turgeon
 * @beaninfo 
 *   description: A service that needs to be extended to update the items. Currently the AGS
 *                and CUS extend this. In the case of AGS products and skus are done
 *    
 *   displayname: CatalogUpdateService
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/catalog/custom/TreeWalkUpdateService.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public abstract class TreeWalkUpdateService 
  extends CMSService 
{
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = 
    "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/catalog/custom/TreeWalkUpdateService.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  public static final String PROCESS_NAME_PREFIX = "TRE-";
  public static final String PUBLISHING_USER = "publishing";
  protected static final String GENERIC_LOOP = "GENERIC_LOOP";
  private static final String PERFORM_MONITOR_NAME="TreeWalkUpdateService";

  //-------------------------------------
  // property: includeDynamicChildren
  boolean mIncludeDynamicChildren = false;
  
  /**
   * Returns the value of the IncludeDynamicChildren property
   * @beaninfo description: True if dynamic parent-child relationships should be accounted for. 
   */
  public boolean isIncludeDynamicChildren() {
    return mIncludeDynamicChildren;
  }
  
  private Boolean mTempTablesAvailable = null;
  
  public boolean isTempTablesAvailble(Connection pDBConnection) {
    if (mTempTablesAvailable != null)
      return mTempTablesAvailable;
    
    String tempTableTestSQL =  "select * from DCS_PRD_CHLDSKU_TEMP ";

    //Just catch and swallow the error
    try {
      PreparedStatement psToexec = pDBConnection.prepareStatement(tempTableTestSQL);
      psToexec.executeQuery();
      psToexec.clearParameters();
      psToexec.close();
    } catch (SQLException e) {
      if (isLoggingWarning())
        logWarning("Temp tables used for CMS not created, performance could be improved if these tables are created.");

      setDatabaseProductName("NotAvailable");
      mTempTablesAvailable = false;
      return mTempTablesAvailable;
    }
   
    mTempTablesAvailable = true;
    return mTempTablesAvailable;
  }
  
  /**
   * Sets property IncludeDynamicChildren
   */
  public void setIncludeDynamicChildren(boolean pIncludeDynamicChildren) {
    mIncludeDynamicChildren = pIncludeDynamicChildren;
  }
  
  // property: catalogTools
  CustomCatalogTools mCatalogTools;
  /**
   * Sets property catalogTools
   **/
  public void setCatalogTools(CustomCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }
  /**
   * Returns property catalogTools
   * @beaninfo description: Used to obtain catalog repository information
   **/
  public CustomCatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  //---------------------------------------------------------------------------
  // property:CatalogProperties
  //---------------------------------------------------------------------------
  
  private CatalogProperties mCatalogProperties;
  public void setCatalogProperties(CatalogProperties pCatalogProperties) {
    mCatalogProperties = pCatalogProperties;
  }

  /**
   * A helper class that holds commonly accessed reposority property 
   * and item names
   * @beaninfo description: A class that holds commonly accessed reposority property
   *         and item names
   **/
  public CatalogProperties getCatalogProperties() {
    return mCatalogProperties;
  }
  
  private String mNameToLog;
  
  public String getNameToLog() {
    if (mNameToLog == null)
      return "TreeWalkUpdateService";
    
    return mNameToLog;
  }

  //--------------------------------------
  // Property: completedItems
  private Map<ShadowMapKey, ShadowBean> mCompletedItems;
  
  public Map<ShadowMapKey, ShadowBean> getCompletedItems() {
    return mCompletedItems;
  }

  public void setCompletedItems(Map<ShadowMapKey, ShadowBean> pCompletedItems) {
    mCompletedItems = pCompletedItems;
  }

  //--------------------------------------
  // Property: DynamicProdcts
  private Map<String, Collection<String>> mDynamicProductsMap;
  
  public Map<String, Collection<String>> getDynamicProductsMap() {
    return mDynamicProductsMap;
  }

  public void setDynamicProductsMap(Map<String, Collection<String>> pDynamicProductsMap) {
    mDynamicProductsMap = pDynamicProductsMap;
  }

  //-------------------------------------
  // @deprecated
  // Property: computeSubCatalogs

  /**
   * Sets boolean indicating if the subCatalogs property of the catalog item
   * should be computed.
   * @deprecated catalog.subCatalogs is never computed by this service
   **/
  public void setComputeSubCatalogs(boolean pComputeSubCatalogs) {}

  /**
   * Returns property computeSubCatalogs
   * @beaninfo description: whether to maintain catalog's subCatalogs property
   * @deprecated catalog.subCatalogs is never computed by this service
   */
  public boolean isComputeSubCatalogs() { return false; }

  //-------------------------------------
  // property: maxItemsPerTransaction
  private int mMaxItemsPerTransaction = 250;

  /**
   * Sets the maximum number of repository items that will be handled 
   * within a single transaction
   **/
  public void setMaxItemsPerTransaction(int pMaxItemsPerTransaction) {
    mMaxItemsPerTransaction = pMaxItemsPerTransaction;
  }

  /**
   * Returns property maxItemsPerTransaction
   * @beaninfo 
   *    expert: true 
   *    description: The maximum number of items that will be updated in one transaction
   **/
  public int getMaxItemsPerTransaction() {
    return mMaxItemsPerTransaction;
  }

  //-------------------------------------
  // property projectWorkflow
  private String mProjectWorkflow;

  /**
   * retrieve the workflow
   * @return The publishing workflow name
   **/
  public String getProjectWorkflow(){
    return mProjectWorkflow;
  }

  /**
   * set the workflow
   * @param pProjectWorkflow - The publishing workflow name
   **/
  public void setProjectWorkflow(String pProjectWorkflow){
    mProjectWorkflow=pProjectWorkflow;
  }

  private List<String> mServiceFunctions;

  public List<String> getServiceFunctions() {
    return mServiceFunctions;
  }

  public void setServiceFunctions(List<String> pServiceFunctions) {
    mServiceFunctions = pServiceFunctions;
  }
  
  private boolean mCatalogFoundInTreeStep = false;
  

  public boolean isCatalogFoundInTreeStep() {
    return mCatalogFoundInTreeStep;
  }

  public void setCatalogFoundInTreeStep(boolean pCatalogFoundInTreeStep) {
    mCatalogFoundInTreeStep = pCatalogFoundInTreeStep;
  }

  private Collection<String> mCatalogIds;
  
  public Collection<String> getCatalogIds() {
    return mCatalogIds;
  }

  public void setCatalogIds(Collection<String> pCatalogIds) {
    mCatalogIds = pCatalogIds;
  }

  private Repository mRepository;
  
  public Repository getRepository() {
    return mRepository;
  }

  public void setRepository(Repository pRepository) {
    mRepository = pRepository;
  }
  
  private String mDatabaseProductName;
  
  public String getDatabaseProductName() {
    return mDatabaseProductName;
  }

  public void setDatabaseProductName(String pDatabaseProductName) {
    mDatabaseProductName = pDatabaseProductName;
  }
  
  /**
   * Performs this services functions. pRepository and pCatalogIds are parameters only
   * for compliance with CMSService. These parameters are not used - the repository is
   * defined by the catalogRepository property, and we do not support processing of
   * individual catalogs - the entire repository will be processed.
   * @beaninfo description: Calls any functions in this service that are contained in the
   *           pServiceFunctions parameter.
   * @param pRepository - Not used. Repository is defined in catalogRepositoryProperty
   * @param pCatalogIds - Not used. Entire repository is always processed
   * @param pServiceFunctions - a list of strings that identify service functions. used to
   *          conditionally executed specific functions of a service
   * @return boolean - service success or failure
   **/
  public boolean performService(Repository pRepository, Collection pCatalogIds, List pServiceFunctions) 
  {
    String perfName = "performService";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    
    long startTime = System.currentTimeMillis();

    preService(pRepository, pCatalogIds, pServiceFunctions);
    
    if (isLoggingInfo())
      logInfo(PERFORM_MONITOR_NAME + ":" + perfName + " starting.");
      
    try {
      setCatalogIds(pCatalogIds);
      
      //Set the member variable mServiceFunctions so we do not need to pass it around to all the methods
      setServiceFunctions(pServiceFunctions);
      
      if (isLoggingDebug())
        logDebug(getServiceName() + " performService called with service functions " + getServiceFunctions());
      
      if(pRepository == null)
        setRepository(getCatalogTools().getCatalog());
      else
        setRepository(pRepository);
      
      if(getRepository() == null) {
        if(isLoggingError())
          logError("CATALOG REPOSITORY IS NULL, processing halted.");
      } else {
        if(isLoggingDebug())
          logDebug("CATALOG REPOSITORY IS " + getRepository());
       
        //Given time constraints this will have to be shelved till we have more time to explore
        mDatabaseProductName="NotAvailable";
        
        updateCatalog();
      }
    } finally {
      try {
        if (getCompletedItems() != null)
          getCompletedItems().clear();
        
        //End the performance monitoring of the current operation
        PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfName);
      } catch (PerfStackMismatchException e) {
        if (isLoggingWarning()) {
          logWarning(PERFORM_MONITOR_NAME + " failed: " + e);
        }
      }
    }

    if (isLoggingInfo())
      logInfo(PERFORM_MONITOR_NAME + ":" + perfName + " finished in " + (System.currentTimeMillis() - startTime) / 1000 + " seconds.");
    
    return true;
  }

  /**
   * The main method does some basic setup and logging, calls processTree and
   * then does some cleanup.
   *    
   * When running in versioned environment, in addition to the above functionality, it creates a
   * project using configured workflow and make the associated workspace current development line
   * before making any changes to the catalog.
   *
   * @beaninfo descripton: Batch-updates all incrementally-computed properties of the catalog and
   *    category repository items
   */
  protected abstract void updateCatalog();

  protected void updateCatalog (MutableRepository pMutableRepository) {
    setRepository(pMutableRepository);
    
    updateCatalog();
  }
  
  /**
   * Process tree. This method sets up the queries needed to get the 
   * base levels for catalogs, categories and folders. It then calls
   * subProcessTree with each of the base levels (catalog, category and folder).
   * 
   */
  protected void processTree() {
    String perfName = "processTree";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    
    //Default the sets
    setCompletedItems(new HashMap<ShadowMapKey, ShadowBean>());
    
    TransactionDemarcation transactionDemarcation = new TransactionDemarcation();

    try {
      // Query for base counts of catalogs, categories and folders in the repository
      if (isLoggingInfo())
        logInfo(ResourceUtils.getMsgResource("EXECUTING_COUNT_QUERIES", MY_RESOURCE_NAME, sResourceBundle));

      // since we do this in batches, we need to order the results
      String sortProp = getCatalogProperties().getIdPropertyName();
      SortDirective sd = new SortDirective(sortProp, SortDirective.DIR_ASCENDING);
      SortDirectives sds = new SortDirectives();
      sds.addDirective(sd);
      
      // *** START of the base Catalogs ***//
      //Get all child categories that do not have any child categories or child cataglogs
      //All of the Service functions require the base catalogs (catalogs with no categories beneath them.
      transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      RepositoryView catalogView = getCatalogProperties().getCatalogRepositoryView(getRepository());
      QueryBuilder catalogQueryBuilder = catalogView.getQueryBuilder();
      QueryExpression rootCatagoriesQE = catalogQueryBuilder.createPropertyQueryExpression(getCatalogProperties().getRootCategoriesPropertyName());
      Query getCatalogsWithNoCategoryChildrenQuery = catalogQueryBuilder.createIsNullQuery(rootCatagoriesQE);
      
      QueryExpression rootSubCatalogs = catalogQueryBuilder.createPropertyQueryExpression(getCatalogProperties().getRootSubCatalogsPropertyName());
      Query getCatalogsWithNoCatalogChildrenQuery = catalogQueryBuilder.createIsNullQuery(rootSubCatalogs);
      
      Query[] catalogElements = {getCatalogsWithNoCategoryChildrenQuery, getCatalogsWithNoCatalogChildrenQuery};
      Query baseCatalogQuery = catalogQueryBuilder.createAndQuery(catalogElements);
      
      int numCatalogs = catalogView.executeCountQuery(baseCatalogQuery);
      
      if (isLoggingDebug()) {
        logDebug("Num of base catalogs:" + numCatalogs);
      }
      
      //After the count queries restart the transaction
      transactionDemarcation.end();

      if (isLoggingInfo())
        logInfo(PERFORM_MONITOR_NAME + ":" + perfName + " starting to process base catalogs.");

      //Process the base catalogs
      subProcessTree(numCatalogs, baseCatalogQuery, catalogView,
          sds, getCatalogProperties().getCatalogItemName()); 

      if (isLoggingInfo())
        logInfo(PERFORM_MONITOR_NAME + ":" + perfName + " ended processing of base catalogs.");
      // *** END of the base Catalogs ***//
      
      // *** START of the base Categories ***//
      //All of the Service functions require the base categories (categories with no categories beneath them).
      int numCategories = 0;
      
      if (getCatalogIds() == null) {
        transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
        
        RepositoryView categoryView = getCatalogProperties().getCategoryRepositoryView(getRepository());
        QueryBuilder queryBuilder = categoryView.getQueryBuilder();
        QueryExpression childCategories = null;
        Query[] elements = null;
        Query categoriesWithNoCategoryChildrenQuery = null;
        QueryExpression subCatalogs = null;
        Query categoriesWithNoCatalogChildrenQuery = null;
        
        childCategories = queryBuilder.createPropertyQueryExpression(getCatalogProperties().getFixedChildCategoriesPropertyName());
        categoriesWithNoCategoryChildrenQuery = queryBuilder.createIsNullQuery(childCategories);
        subCatalogs = queryBuilder.createPropertyQueryExpression(getCatalogProperties().getSubCatalogsPropertyName());
        categoriesWithNoCatalogChildrenQuery = queryBuilder.createIsNullQuery(subCatalogs);

        elements = new Query[2];
        elements[0] = categoriesWithNoCategoryChildrenQuery;
        elements[1] = categoriesWithNoCatalogChildrenQuery;

        Query baseCategoryQuery = queryBuilder.createAndQuery(elements);

        numCategories = categoryView.executeCountQuery(baseCategoryQuery);
        
        if (isLoggingDebug()) {
          logDebug("Num of base categories:" + numCategories);
          logDebug("Query:" + baseCategoryQuery.getQueryRepresentation());
        }
  
        //After the count queries restart the transaction
        transactionDemarcation.end();

        if (isLoggingInfo())
          logInfo(PERFORM_MONITOR_NAME + ":" + perfName + " starting to process base categories.");
        
        //Process the base categories
        subProcessTree(numCategories, baseCategoryQuery, categoryView,
            sds, getCatalogProperties().getCategoryItemName()); 
      } else {
        //Ok, so they passed a catalog id, or multiple for that matter. Find the leaf
        //category nodes using recursion. Since the query to get the list of
        //categories happens in subProcessTree, we need to make the call there.
        RepositoryItem[] repositoryItems = null;
        repositoryItems = getCategoryLeafNodeArray(); 

        if (isLoggingInfo())
          logInfo(PERFORM_MONITOR_NAME + ":" + perfName + " starting to process base categories.");

        //Process the base categories
        subProcessTree(repositoryItems.length, null, null,
            sds, getCatalogProperties().getCategoryItemName()); 
      }

      if (isLoggingInfo())
        logInfo(PERFORM_MONITOR_NAME + ":" + perfName + " ended processing base categories.");
      // *** END of the base Categories ***//
      
      // *** START of the base CatalogFolders ***//
      //This gets all base catalog folders that do not have any catalogs defined and no child folders,
      //This is needed for site membership.
      transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      
      RepositoryView catalogFolderView = getCatalogProperties().getCatalogFolderRepositoryView(getRepository());
      QueryBuilder queryCatalogFolderBuilder = catalogFolderView.getQueryBuilder();

      QueryExpression childCatalogFolders = queryCatalogFolderBuilder.createPropertyQueryExpression(getCatalogProperties().getChildItemsPropertyName());
      Query getCatalogFoldersWithNoChildItemsQuery = queryCatalogFolderBuilder.createIsNullQuery(childCatalogFolders);
      
      QueryExpression childFolders = queryCatalogFolderBuilder.createPropertyQueryExpression(getCatalogProperties().getChildFoldersPropertyName());
      Query getFoldersWithNoChildFoldersQuery = queryCatalogFolderBuilder.createIsNullQuery(childFolders);

      Query[] catalogFolderElements = {getCatalogFoldersWithNoChildItemsQuery, getFoldersWithNoChildFoldersQuery};
      Query baseCatalogFolderQuery = queryCatalogFolderBuilder.createAndQuery(catalogFolderElements);
      
      int numFolders = catalogFolderView.executeCountQuery(baseCatalogFolderQuery);
      
      if (isLoggingDebug()) {
        logDebug("Num of base folders:" + numFolders);
      }
      
      //After the count queries restart the transaction
      transactionDemarcation.end();

      if (isLoggingInfo())
        logInfo(PERFORM_MONITOR_NAME + ":" + perfName + " starting to process base catalog folders.");
      
      //Process the base folders
      subProcessTree(numFolders, baseCatalogFolderQuery, catalogFolderView,
          sds, getCatalogProperties().getCatalogFolderItemName());
      // *** END of the base CatalogFolders ***//      

      if (isLoggingInfo())
        logInfo(PERFORM_MONITOR_NAME + ":" + perfName + " ended processing of base catalog folders.");

      if (isLoggingInfo())
        logInfo(PERFORM_MONITOR_NAME + ":" + perfName + " starting to update repository for catalogs and categories.");
      
      //Now do the actual update to the db...
      updateRepository();
      
      if (isLoggingInfo())
        logInfo(PERFORM_MONITOR_NAME + ":" + perfName + " update to repository ended for catalogs and categories.");

      if (isLoggingInfo())
        logInfo(PERFORM_MONITOR_NAME + ":" + perfName + " starting to update repository for products and skus.");
      
      //Now update the products, this method really is just a stub function that should be 
      //overridden if products should be updated. AGS does override, CUS does not. 
      updateProducts();
      
      //Now update the skus, this method really is just a stub function that should be 
      //overridden if products should be updated. AGS does override, CUS does not. 
      updateSkus();

      if (isLoggingInfo())
        logInfo(PERFORM_MONITOR_NAME + ":" + perfName + " update to repository ended for products and skus.");

    } catch (TransactionDemarcationException exc) {
      if (isLoggingError())
        logError("Transaction Demarcation Exception", exc);
    } catch (RepositoryException re) {
      if (isLoggingError())
        logError("Repository Exception", re);
    } finally {
      try {
        transactionDemarcation.end();
        
        //End the performance monitoring of the current operation
        PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfName);
      } catch (PerfStackMismatchException e) {
        if (isLoggingWarning()) {
          logWarning(PERFORM_MONITOR_NAME + " failed: " + e);
        }
      } catch (TransactionDemarcationException tde) {
        if (isLoggingError())
          logError("Transaction Demarcation Exception", tde);
      }
    }
  }
 
  /**
   * Sub process tree. This method executes the query then steps
   * through each of the base level items calling treeStep which
   * does most of the work.
   * 
   * @param pNumItems the number of base level items to be processed
   * @param pBaseQuery the query used to get the base level items
   * @param pRepositoryView the repository view to use to get the
   *  base level items
   * @param pSDS the sorting directive, this is needed to chunk up the items
   * @param pItemType
   * 
   * @throws RepositoryException the repository exception
   * @throws TransactionDemarcationException the transaction demarcation exception
   */
  protected void subProcessTree(int pNumItems, Query pBaseQuery, RepositoryView pRepositoryView,
      SortDirectives pSDS, String pItemType) 
  throws RepositoryException, TransactionDemarcationException {

    String perfName = "subProcessTree";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
    
    try {
      int startIndex = 0;
    
      // To keep things manageable for large catalogs, handle the
      // iterations in groups of <i>maxItemsPerTransaction</I>
      while (startIndex < pNumItems) {
        int endIndex = startIndex + getMaxItemsPerTransaction();
        if (endIndex > pNumItems)
          endIndex = pNumItems;
    
        if (isLoggingDebug()) {
          logDebug("subProcessTree: Getting the items (this currently includes catalogs, categories and catalog folders) between " + startIndex + " and " + endIndex);
        }
        
        RepositoryItem[] repositoryItems = null;
        transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
        
        //If the catalog ids are passed in then use recursion to find the leaf nodes (lowest level categories), 
        //else just do a simple query to get the base nodes
        if (pItemType.equals(getCatalogProperties().getCategoryItemName())
            && getCatalogIds() != null) {
          repositoryItems = getCategoryLeafNodeArray(); 
        } else {
          QueryOptions qops = new QueryOptions(startIndex,endIndex,pSDS,null);
          qops.setDoNotCacheQuery(true);
          repositoryItems = pRepositoryView.executeQuery(pBaseQuery, qops);
        }
        
        transactionDemarcation.end();
        
        processTreeItems(repositoryItems);
    
        startIndex = endIndex; 
      }
    } finally {
      transactionDemarcation.end();
      
      try {
        //End the performance monitoring of the current operation
        PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfName);
      } catch (PerfStackMismatchException e) {
        if (isLoggingWarning()) {
          logWarning(PERFORM_MONITOR_NAME + " failed: " + e);
        }
      }
    }
  }
   
  
  /**
   * Steps through each base item given calling treeStep which handles most
   * of the actual work
   * @param pRepositoryItems base level items to step through
   * @throws RepositoryException
   * @throws TransactionDemarcationException
   */
  public void processTreeItems(RepositoryItem[] pRepositoryItems) 
    throws RepositoryException, TransactionDemarcationException {
    
    TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
    
    try {
      // For each of the repository array elements (these are the lowest level base catalogs, categories or
      // folders within the repository passed in.
      if (pRepositoryItems != null) {
        for (int i = 0; i < pRepositoryItems.length; i++) {
          //Get the transaction
          transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
          
          if (isLoggingDebug()) {
            String msgArgs[] = { pRepositoryItems[i].toString() };
            logDebug(ResourceUtils.getMsgResource("BEGIN_PROCESS", MY_RESOURCE_NAME, sResourceBundle, msgArgs));
          } 
  
          if (isLoggingDebug())
            logDebug("calling catalogTreeStep for:" + (RepositoryItem) pRepositoryItems[i]);
  
          List<String> treeStepList = new ArrayList<String>();
  
          //Reset the flag for this iteration
          setCatalogFoundInTreeStep(false);
          
          //This step handles recursing through the tree, this can handle catalogs, categories and catalog folders
          treeStep((RepositoryItem) pRepositoryItems[i], (RepositoryItem) pRepositoryItems[i], null, treeStepList);
          
          if (isLoggingDebug())
            logDebug("Adding " + (RepositoryItem) pRepositoryItems[i] + " to completed items (this currently includes catalogs, categories and catalog folders).");
  
          if (isLoggingDebug()) {
            String msgArgs[] = { pRepositoryItems[i].toString() };
            logDebug(ResourceUtils.getMsgResource("END_PROCESS", MY_RESOURCE_NAME, sResourceBundle, msgArgs));
          }
  
          //End each transaction per each lowest level category
          transactionDemarcation.end();
        }
      }
    } finally {
      transactionDemarcation.end();
    }
  }
  /**
   * This is a recursive method that traverses up the catalog tree, starting
   * with a baseItem, and visiting all its parent categories, catalogs and folders
   * compiling all the incrementally-computed properties.
   * 
   * @param pBaseItem - The repository item that was the starting point of the current
   * walk up the catalog tree.
   * @param pCurrentItem - The repository item that represents the current point of the
   * tree walk
   * @param pPreviousItem - The repository item that represents the most recent point of the
   * tree walk
   * @param pTreeStepList this is a list of repository items as they are processed,
   *  it is used to try and find circular references within the catalog/category tree.
   * 
   * @throws RepositoryException the repository exception
   * @throws TransactionDemarcationException the transaction demarcation exception
   * 
   * @beaninfo description: Recursively-called method that walks up catalog
   * tree, computing properties along the way
   */
  protected ShadowBean treeStep(RepositoryItem pBaseItem,
      RepositoryItem pCurrentItem, RepositoryItem pPreviousItem, List<String> pTreeStepList)
      throws RepositoryException, TransactionDemarcationException {

    String perfName = "treeStep";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    
    if (isLoggingDebug()) 
      logDebug("Start of treeStep.  pBaseItem = " + pBaseItem + ", pCurrentItem = " + pCurrentItem
        + ", pPreviousItem = " + pPreviousItem);

    //This object will be used to track changes that are not necessarily a direct parent attribute
    ShadowBean shadowBean = null;
    
    try {
      CatalogProperties props = getCatalogProperties(); 
      
      //Keep track if in the traversal we hit a catalog, this is for processing of dynamic children
      if (getCatalogTools().isCatalog(pCurrentItem))
        setCatalogFoundInTreeStep(true);

      ShadowMapKey id = new ShadowMapKey(pCurrentItem.getRepositoryId(), pCurrentItem.getItemDescriptor().getItemDescriptorName());
      if (!getCompletedItems().containsKey(id)) {
        shadowBean = new ShadowBean();
        if (!getCatalogTools().isCatalogFolder(pCurrentItem))
          shadowBean.setId((String) pCurrentItem.getPropertyValue(props.getIdPropertyName()));
        
        if (getCatalogTools().isCatalog(pCurrentItem) || getCatalogTools().isCatalogFolder(pCurrentItem)) {
          //Always grab the sites from the repository and add them to the tracker object
          Set<String> sites = (Set<String>) pCurrentItem.getPropertyValue(props.getSitesPropertyName());
          if (shadowBean.getSites() == null)
            shadowBean.setSites(new HashSet<String>());
          for (String site:sites) {
            shadowBean.getSites().add(site);
          }
        }
          
        shadowBean.setTypeOfItem(pCurrentItem.getItemDescriptor().getItemDescriptorName());
        
        //Now add the object to the shadow bean
        getCompletedItems().put(id, shadowBean);      
      } else {
        shadowBean = (ShadowBean) getCompletedItems().get(id);
      }
      
      if (isLoggingDebug())
        logDebug("shadowBean:" + shadowBean);
      
      // Check to see if we have seen this catalog,category or folder
      // already in this iteration. If we have, we have a loop, the catalog structure is illegal
      if (pTreeStepList != null && pTreeStepList.contains(pCurrentItem.getRepositoryId())) {
        String msgArgs[] = { pCurrentItem.getItemDescriptor().getItemDescriptorName(), pCurrentItem.getItemDisplayName(), getNameToLog() };
        
        if (isLoggingError()) {
          //Since this treeStep is now used with catalogs, categories and folders we need to know which is which, so 
          //send the item descriptor of the current item to the error message.
          logError(ResourceUtils.getMsgResource(GENERIC_LOOP, MY_RESOURCE_NAME, sResourceBundle, msgArgs));
        }
  
        throw new CatalogLoopException(ResourceUtils.getMsgResource(GENERIC_LOOP, MY_RESOURCE_NAME, sResourceBundle, msgArgs));
      }
      
      //Add the current item to the list as we walk up the tree
      pTreeStepList.add(pCurrentItem.getRepositoryId());
      
      // For each parent catalog/category we find, we need to add the parent to the
      // ancestor list of the base catalog, then recursively call this method
      // with the parent as the "pCurrentItem"
      //Check to see the pBaseItem is either a catalog or a category
      RepositoryView repositoryCategoryView = 
        getCatalogProperties().getCategoryRepositoryView(getRepository());
      RepositoryView repositoryCatalogView = 
        getCatalogProperties().getCatalogRepositoryView(getRepository());
      
      //this is code that may not be used, adding for site memebership in catalog folders 
      RepositoryView repositoryCatalogFolderView = 
        getCatalogProperties().getCatalogFolderRepositoryView(getRepository());
      
      //Create the 3 query builders, one to check for parent categories and one to check for parent catalogs
      QueryBuilder parentCategoriesQueryBuilder = repositoryCategoryView.getQueryBuilder();
      QueryBuilder parentCatalogQueryBuilder = repositoryCatalogView.getQueryBuilder();
      QueryBuilder parentCatalogFolderQueryBuilder = repositoryCatalogFolderView.getQueryBuilder();
      
      //Get the current Item
      QueryExpression currentItem = parentCategoriesQueryBuilder.createConstantQueryExpression(pCurrentItem);
      
      RepositoryItem[] combinedCategoriesAndCatalogs = null;
      
      //If the current type is a catalog then only check for a match in catalogs, if it is a 
      //category then only check for matches in categories
      QueryExpression childQECategoryCritieria = null;
      QueryExpression childQECatalogCritieria = null;
      QueryExpression childQECatalogFolderCritieria = null;
      
      //We need to check if the current item (catalog or category) the query for the item
      //in both the catalogs and categories
      if (getCatalogTools().isCatalog(pCurrentItem)) {
        childQECategoryCritieria = 
          parentCategoriesQueryBuilder.createPropertyQueryExpression(props.getSubCatalogsPropertyName());
  
        childQECatalogCritieria = 
          parentCatalogQueryBuilder.createPropertyQueryExpression(props.getRootSubCatalogsPropertyName());
        
        childQECatalogFolderCritieria = 
          parentCatalogFolderQueryBuilder.createPropertyQueryExpression(props.getChildItemsPropertyName());
      } else if (getCatalogTools().isCategory(pCurrentItem)) {
        childQECategoryCritieria = 
          parentCategoriesQueryBuilder.createPropertyQueryExpression(props.getFixedChildCategoriesPropertyName());

        childQECatalogCritieria = 
          parentCatalogQueryBuilder.createPropertyQueryExpression(props.getRootCategoriesPropertyName());
      } else if (getCatalogTools().isCatalogFolder(pCurrentItem)) {
        childQECatalogFolderCritieria = 
          parentCatalogFolderQueryBuilder.createPropertyQueryExpression(props.getChildFoldersPropertyName());
      }
      
      Query parentCategoriesQuery = null;
      Query parentCatalogsQuery = null;
      Query parentCatalogFolderQuery = null;
      RepositoryItem[] parentCategories = null;
      RepositoryItem[] parentCatalogs = null; 
      RepositoryItem[] parentCatalogFolders = null;
      
      if (childQECategoryCritieria != null) {
        parentCategoriesQuery = 
          parentCategoriesQueryBuilder.createComparisonQuery(childQECategoryCritieria, currentItem, QueryBuilder.EQUALS);
        
        if (isLoggingDebug())
          logDebug("parentCategoriesQuery:" + parentCategoriesQuery.getQueryRepresentation());
        
        //Get any parent categroies
        parentCategories = repositoryCategoryView.executeQuery(parentCategoriesQuery);
      }

      if (childQECatalogCritieria != null) {
        
        parentCatalogsQuery =
          parentCatalogQueryBuilder.createComparisonQuery(childQECatalogCritieria, currentItem, QueryBuilder.EQUALS);
        //Now we have the parent categories get the parent catalogs so we can process all at the same time
        if (isLoggingDebug())
          logDebug("parentCatalogsQuery:" + parentCatalogsQuery.getQueryRepresentation());
        
        parentCatalogs = repositoryCatalogView.executeQuery(parentCatalogsQuery);
      }
      
      if (childQECatalogFolderCritieria != null) {
        parentCatalogFolderQuery = 
          parentCatalogFolderQueryBuilder.createComparisonQuery(childQECatalogFolderCritieria, currentItem, QueryBuilder.EQUALS);
        //Now we have the parent categories get the parent catalogs so we can process all at the same time
        parentCatalogFolders = repositoryCatalogFolderView.executeQuery(parentCatalogFolderQuery);
      }
      
      //Now combine the 2 arrays into 1 so we can step through them at the same time
      int combinedLength = 0; 
      int parentCategoriesLength = 0;
      int parentCatalogsLength = 0;
      int parentCatalogFoldersLength = 0;
      
      if (parentCategories != null) {
        combinedLength = combinedLength + parentCategories.length;
        parentCategoriesLength = parentCategories.length;
      }
        
      if (parentCatalogs != null) {
        combinedLength = combinedLength + parentCatalogs.length;
        parentCatalogsLength = parentCatalogs.length;
      }
      
      if (parentCatalogFolders != null) {
        combinedLength = combinedLength + parentCatalogFolders.length;
        parentCatalogFoldersLength = parentCatalogFolders.length;
      }
        
      combinedCategoriesAndCatalogs = new RepositoryItem[combinedLength];
      
      if (parentCategories != null) 
        System.arraycopy(parentCategories, 0, combinedCategoriesAndCatalogs, 0, parentCategoriesLength);
      if (parentCatalogs != null)
        System.arraycopy(parentCatalogs, 0, combinedCategoriesAndCatalogs, parentCategoriesLength, parentCatalogsLength);
      if (parentCatalogFolders != null)
        System.arraycopy(parentCatalogFolders, 0, combinedCategoriesAndCatalogs, parentCatalogsLength + parentCategoriesLength, parentCatalogFoldersLength);
      
      if (isLoggingDebug()) {
        logDebug("combinedCategoriesAndCatalogs.length:"+ combinedCategoriesAndCatalogs.length);
      }
      
      //Step through the combined parent items if there are any parent items
      if (combinedCategoriesAndCatalogs.length > 0 ){
        for (int i = 0; i < combinedCategoriesAndCatalogs.length; i++) {
          //Get the next parent item from the current item
          RepositoryItem parentItem = (RepositoryItem) combinedCategoriesAndCatalogs[i];
  
          if (isLoggingDebug()) {
            logDebug("stepping parentItem:" + parentItem);
            logDebug("stepping pCurrentItem:" + pCurrentItem);
            logDebug("stepping shadowBean:" + shadowBean);
          }
          
          //Now update the computed data values
          goingUpUpdateItem(parentItem, pCurrentItem, shadowBean);
          
          //use recursion to get to the top
          ShadowBean parentShadowBean = treeStep(pBaseItem, parentItem, pCurrentItem, pTreeStepList);
  
          shadowBean.copyMembers(parentShadowBean);
          if (!getCatalogTools().isCatalogFolder(pCurrentItem))
            buildParentCategoriesForCatalog(parentItem, pCurrentItem, shadowBean);
          
          //Now update the computed data values
          goingDownUpdateItem(parentItem, pCurrentItem, shadowBean);
          
          //Now remove the pCurrentItem from the tree step list since it has been processed, if there
          //is a loop it would have already been caught while walking up the tree
          pTreeStepList.remove(pCurrentItem.getRepositoryId());
        }  
      } else {
        //Now update the computed data values
        goingUpUpdateItem(pCurrentItem, pCurrentItem, shadowBean);
          
        shadowBean.cloneToPrior(shadowBean);
  
        //We are at the top so go ahead and start writing out the values
        goingDownUpdateItem(pCurrentItem, pCurrentItem, shadowBean);

        //Now remove the pCurrentItem from the tree step list since it has been processed, if there
        //is a loop it would have already been caught while walking up the tree
        pTreeStepList.remove(pCurrentItem.getRepositoryId());
      }
      
      //If we are to include Dynamic Children then set them now, this
      //method can set up recursion to do all the children of the dynamic child.
      if (isIncludeDynamicChildren() 
          && (isCatalogFoundInTreeStep()) ) {
        updateDynamicChildren(pCurrentItem, shadowBean, true);
      }
    } catch (CatalogLoopException e) {
      //Just log the error and keep on processing
      if (isLoggingError())
        logError(e);
    } finally {
      try {
        //End the performance monitoring of the current operation
        PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfName);
      } catch (PerfStackMismatchException e) {
        if (isLoggingWarning()) {
          logWarning(PERFORM_MONITOR_NAME + " failed: " + e);
        }
      }
    }
    
    if (isLoggingDebug())
      logDebug("End of treeStep.  pBaseItem = " + pBaseItem + ", pCurrentItem = " + pCurrentItem
        + ", pPreviousItem = " + pPreviousItem);
    
    return shadowBean;
  }

  /**
   * updateDynamicChildren
   * @param pItem
   * @param pShadowBean
   * @param pFirstTime
   * @throws RepositoryException
   * @throws TransactionDemarcationException 
   */
  protected void updateDynamicChildren(RepositoryItem pItem, ShadowBean pShadowBean, boolean pFirstTime) 
  throws RepositoryException, TransactionDemarcationException {
    
    String perfName = "updateDynamicChildren";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);

    if (isLoggingDebug())
      logDebug(perfName + ": starting pItem:" + pItem);

    try {
      //If we are to include Dynamic Children then set them now
      if (getCatalogTools().isCategory(pItem)) {
        //This gets all of the child from the implementing classes, for AGS this is possibly category, products and skus,
        //for CUS it is simpler, just the categories.
        List<RepositoryItem> children = getDynamicChildrenList(pItem, pFirstTime);

        if(children != null && children.size() > 0) {
          for (RepositoryItem child:children) {
            ShadowBean shadowBean = null;

            ShadowMapKey id = new ShadowMapKey(child.getRepositoryId(), child.getItemDescriptor().getItemDescriptorName());
            if (!getCompletedItems().containsKey(id)) {
              shadowBean = new ShadowBean();
              shadowBean.setId((String) child.getPropertyValue(getCatalogProperties().getIdPropertyName()));
  
              shadowBean.setTypeOfItem(child.getItemDescriptor().getItemDescriptorName());
              
              //Now add the object to the shadow bean
              getCompletedItems().put(id, shadowBean);     
            } else
              shadowBean = (ShadowBean) getCompletedItems().get(id);
            
            shadowBean.copyMembers(pShadowBean);
            buildParentCategoriesForCatalog(pItem, child, shadowBean);
            
            if (isLoggingDebug())
              logDebug("shadowBean:" + shadowBean);
            
            //Now update the computed data values
            goingDownUpdateItem(pItem, child, shadowBean);
         
            //recurse to update any children of this category
            updateDynamicChildren(child, shadowBean, false);
          }
        }
        
        //Now check for child products
        //If this category has dynamic children then add to the list
        List<String> dynamicProducts = getDynamicProducts(getRepository(), pItem); 
        if (dynamicProducts != null
            && dynamicProducts.size() > 0) {
          if (getDynamicProductsMap() == null) 
            setDynamicProductsMap(new HashMap<String, Collection<String>>());
          
          //Now iterate through the products adding the categroy to its list
          for (String product:dynamicProducts) {
            //See if we hav hit this before and if so just add to it
            if (getDynamicProductsMap().containsKey(product)) { 
              getDynamicProductsMap().get(product).add(pItem.getRepositoryId());
            } else {
              Collection<String> categories = new HashSet<String>();
              categories.add(pItem.getRepositoryId());
              getDynamicProductsMap().put(product, categories);
            }
          }
        }
      }
    } finally {
      try {
        //End the performance monitoring of the current operation
        PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfName);
      } catch (PerfStackMismatchException e) {
        if (isLoggingWarning()) {
          logWarning(PERFORM_MONITOR_NAME + " failed: " + e);
        }
      }
    }
    
    if (isLoggingDebug())
      logDebug(perfName + ": ending pItem:" + pItem + " with pTrackChanges of:" + pShadowBean);
  }
 
  /**
   * 
   * @param pRepository
   * @param pItem
   * @return
   * @throws RepositoryException
   */
  protected List<String> getDynamicProducts(Repository pRepository, RepositoryItem pItem) throws RepositoryException {
    return null;
  }
  
  /**
   * getDynamicChildrenList
   * @param pItem
   * @param pFirstTime
   * @return
   * @throws RepositoryException
   */
  protected List<RepositoryItem> getDynamicChildrenList(RepositoryItem pItem, boolean pFirstTime) throws RepositoryException {
    //This is a little tricky, the firt time this is called we really need to only get the dynamicChildCategories since the
    //standard tree walk will get the fixed. When we need to get both is after the first time since the standard tree
    //walk did not handle these categories.
    String perfName = "getDynamicChildrenList";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    
    List<RepositoryItem> children = new ArrayList<RepositoryItem>();
    
    try {
      if (isLoggingDebug())
        logDebug(perfName + ": starting with pItem:" + pItem + " pFirstTime:" + pFirstTime);
      
      if (getCatalogTools().isCategory(pItem)) {
        List<RepositoryItem> fixedChildCategories = null;
        List<RepositoryItem> dynamicChildCategories = null;
        
        if (!pFirstTime) {
          fixedChildCategories = (List<RepositoryItem>) pItem.getPropertyValue(getCatalogProperties().getFixedChildCategoriesPropertyName());
        }
        
        dynamicChildCategories = (List<RepositoryItem>) pItem.getPropertyValue(getCatalogProperties().getDynamicChildCategoriesPropertyName());
  
        if (fixedChildCategories != null)
          children.addAll(fixedChildCategories);
  
        
        if (dynamicChildCategories != null)
          children.addAll(dynamicChildCategories);
      } else if (getCatalogTools().isProduct(pItem)) {
        List<RepositoryItem> childSKUs = (List<RepositoryItem>) pItem.getPropertyValue(getCatalogProperties().getChildSkusPropertyName());
  
        if (childSKUs != null)
          children.addAll(childSKUs);
      }
  
      if (isLoggingDebug())
        logDebug(perfName + ": ending with pItem:" + pItem + " with children:" + children);

    } finally {
      try {
        // End the performance monitoring of the current operation
        PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfName);
      } catch (PerfStackMismatchException e) {
        if (isLoggingWarning()) {
          logWarning(PERFORM_MONITOR_NAME + " failed: " + e);
        }
      }
    }
    
    return children;
  }
  
  /**
   * updateProducts
   * @throws RepositoryException the repository exception
   */
  protected void updateProducts()
    throws RepositoryException, TransactionDemarcationException {
    
  }

  /**
   * 
   */
  protected void updateSkusViaBatch() {
    
  }
  
  /**
   * 
   */
  protected void updateProductsViaBatch() {
  }

  /**
   * 
   * @throws RepositoryException
   * @throws TransactionDemarcationException
   */
  protected void updateSkus()
    throws RepositoryException, TransactionDemarcationException {
  }
  
  /**
   * This method sets all the properties calculated by this service
   * the are pulled up from the bottom of the tree. Currently the only
   * value pulled up is the AllRootCategories. This is the first category 
   * while walking down each branch.
   * 
   * @param pParentItem the parent item that will be updated.
   * @param pCurrentItem the current item is used to pull the data
   * @param pShadowBean
   * from for the pParentItem update.
   * 
   * @throws RepositoryException the repository exception
   */
  protected abstract void goingUpUpdateItem(RepositoryItem pParentItem, RepositoryItem pCurrentItem, 
      ShadowBean pShadowBean)
    throws RepositoryException;

  /**
   * This method sets all the properties calculated by this service
   * that are pulled down from the top of the tree. Most of the attributes that
   * are updated are pulled down from the top. Since this method can be called 
   * passing catalogs, categories or folders we need to check what the parent
   * item and child items are. Since all 3 item types (catalog, category and folder)
   * all update site we do not need to check the type to do the update.
   * 
   * Catalog attributes being updated are:
   *  DirectAncestorCatalogsAndSelf, IndirectAncestorCatalogs, AncestorCategories,
   *  Sites
   *  
   * Category attributes being updated are:
   *  ComputedCatalogs, ParentCatalog, AncestorCategories, Sites
   *  
   * Folder attributes being updated are:
   *  Sites  
   * 
   * @param pParentItem the parent item is the item that the data will be pulled from
   * @param pCurrentItem the current item is the item that will be updated with
   *  data from the pParentItem.
   * @param trackChanges
   * 
   * @throws RepositoryException the repository exception
   * 
   */
  protected abstract void goingDownUpdateItem(RepositoryItem pParentItem, RepositoryItem pCurrentItem,
      ShadowBean trackChanges)
    throws RepositoryException;

  /**
   * This method checks to see if the calculated value is different from the
   * current value. If so, it sets the property to the new value, it tries to
   * modify the collection in one step to minimize the number of DB trips the
   * repository takes for the update.
   * 
   * @param pItem the item
   * @param pPropertyName the property name
   * @param pValue the value
   * 
   * @throws RepositoryException the repository exception
   */
  protected void setPropertyValueAddAll(RepositoryItem pItem, String pPropertyName, Collection pValue, ShadowBean pShadowBean)
      throws RepositoryException {

    String perfName = "getDynamicProducts";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);

    try {
      if (isLoggingDebug()) 
        logDebug("setPropertyValueAddAll:pItem:" + pItem + " hashcode:" + pShadowBean.hashCode() + " property:" + pPropertyName + " value:" + pValue);
      
      //The Sites property, this is sort of a special property where
      //there are current values that we are leaving there and only adding to from the 
      //items parents.
      if (pPropertyName.equals(getCatalogProperties().getSitesPropertyName())) {
        if (pShadowBean.getSites() == null)
          pShadowBean.setSites(new HashSet((Set<String>) pValue));
        else {
          //Since we are adding to a list make sure there are no duplicates, also just add to the existing 
          //collection for better performance in the repository upadate
          Set<String> sites = pShadowBean.getSites();
          Iterator<String> valuesIterator = pValue.iterator();
      
          while (valuesIterator.hasNext()) {
            String passedInSite = valuesIterator.next();
            
            //Now see if it is already in the current item collection
            if (!sites.contains(passedInSite)) {
              sites.add(passedInSite);
            }
          }
        }
      }
      
      //The AllRootCategories property
      if (pPropertyName.equals(getCatalogProperties().getAllRootCategoriesPropertyName())) {
        //This is the first time this has been set so create the object
        if (pShadowBean.getAllRootCategories() == null)
          pShadowBean.setAllRootCategories(new HashSet(new HashSet((Set<String>) pValue)));
        else
          pShadowBean.getAllRootCategories().addAll(pValue);
      }
  
      //The AncestorCategories property
      if (pPropertyName.equals(getCatalogProperties().getAncestorCategoriesPropertyName())) {
        if (pShadowBean.getAncestorCategories() == null)
          pShadowBean.setAncestorCategories(new ArrayList((List<String>) pValue));
        else {
          //Since we are adding to a list make sure there are no duplicates, also just add to the existing 
          //collection for better performance in the repository upadate
          Collection<String> currentValues = pShadowBean.getAncestorCategories();
          Iterator<String> valuesIterator = pValue.iterator();
      
          while (valuesIterator.hasNext()) {
            String itereatorObject = valuesIterator.next();
            
            //Now see if it is already in the current item collection
            if (!currentValues.contains(itereatorObject)) {
              currentValues.add(itereatorObject);
            }
          }
        }
      }
  
      //The ComputedCatalogs property
      if (pPropertyName.equals(getCatalogProperties().getComputedCatalogsPropertyName())) {
        if (pShadowBean.getComputedCatalogs() == null)
          pShadowBean.setComputedCatalogs(new HashSet((Set<String>) pValue));
        else
          pShadowBean.getComputedCatalogs().addAll(pValue);
      }
  
      //The DirectAncestorCatalogsAndSelf property
      if (pPropertyName.equals(getCatalogProperties().getDirectAncestorCatalogsAndSelfPropertyName())) {
        if (pShadowBean.getDirectAncestorCatalogsAndSelf() == null)
          pShadowBean.setDirectAncestorCatalogsAndSelf(new ArrayList((List<String>) pValue));
        else {
          //Since we are adding to a list make sure there are no duplicates, also just add to the existing 
          //collection for better performance in the repository upadate
          Collection<String> currentValues = pShadowBean.getDirectAncestorCatalogsAndSelf();
          Iterator<String> valuesIterator = pValue.iterator();
      
          while (valuesIterator.hasNext()) {
            String itereatorObject = valuesIterator.next();
            
            //Now see if it is already in the current item collection
            if (!currentValues.contains(itereatorObject)) {
              currentValues.add(itereatorObject);
            }
          }
        }
      }
      
      //The IndirectAncestorCatalogs property
      if (pPropertyName.equals(getCatalogProperties().getIndirectAncestorCatalogsPropertyName())) {
        if (pShadowBean.getIndirectAncestorCatalogs() == null)
          pShadowBean.setIndirectAncestorCatalogs(new ArrayList((List<String>) pValue));
        else {
          //Since we are adding to a list make sure there are no duplicates, also just add to the existing 
          //collection for better performance in the repository upadate
          Collection<String> currentValues = pShadowBean.getIndirectAncestorCatalogs();
          Iterator<String> valuesIterator = pValue.iterator();
      
          while (valuesIterator.hasNext()) {
            String itereatorObject = valuesIterator.next();
            
            //Now see if it is already in the current item collection
            if (!currentValues.contains(itereatorObject)) {
              currentValues.add(itereatorObject);
            }
          }
        }
      }
      
      //The ParentCategoriesForCatalog property
      if (pPropertyName.equals(getCatalogProperties().getParentCategoriesForCatalogPropertyName())) {
        if (pShadowBean.getParentCategoriesForCatalog() == null)
          pShadowBean.setParentCategoriesForCatalog(new HashMap((Map<String, Collection<String>>) pValue));
        else {
          pShadowBean.getParentCategoriesForCatalog().putAll((HashMap<String, Collection<String>>) pValue);
        }
      }
    } finally {
      try {
        // End the performance monitoring of the current operation
        PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfName);
      } catch (PerfStackMismatchException e) {
        if (isLoggingWarning()) {
          logWarning(PERFORM_MONITOR_NAME + " failed: " + e);
        }
      }
    }
  }
  
  //utility method for stringIzing collections for logging messages(or whatever)
  protected String stringIt(Collection pObject)
  {
    if(pObject == null)
      return " not valued" ;

    StringBuffer workBuffer = new StringBuffer();
    Iterator workerator = pObject.iterator();
    while(workerator.hasNext())
      workBuffer.append(workerator.next().toString() + " ");

    return workBuffer.toString();

  }

  //utility method for stringIzng arrays for logging messages(or whatever)
  protected String stringIt(Object pObject[])
  {
    if(pObject == null)
      return " not valued" ;
    StringBuffer workBuffer = new StringBuffer();

    for(int i=0; i< pObject.length; i++)
      workBuffer.append(pObject[i].toString() + " ");

    return workBuffer.toString();
  }

  
  /**
   * Creates and returns new workspace
   * @param pWorkspaceId- Id used to create the workspace.
   * @return returns object of type Workspace
   * @exception RepositoryException if unable to retrieve the workspace corresponding to pWorkspaceId
   **/
  protected Workspace getWorkspace(String pWorkspaceId) 
    throws RepositoryException 
  {
    if (pWorkspaceId == null){
      String msgArgs[] = {ResourceUtils.getMsgResource(CUS_WS_NOT_CREATED, MY_RESOURCE_NAME, sResourceBundle) };
      throw new RepositoryException(ResourceUtils.getMsgResource(CUS_WS_REQUIRED, MY_RESOURCE_NAME, sResourceBundle,msgArgs));
    }
    VersionManager vm = ((VersionRepository)getCatalogTools().getCatalog()).getVersionManager();
    if (vm == null){
      String msgArgs[] = {ResourceUtils.getMsgResource(CUS_WS_NOT_CREATED, MY_RESOURCE_NAME, sResourceBundle) };
      throw new RepositoryException(ResourceUtils.getMsgResource(CUS_VM_NOT_SET, MY_RESOURCE_NAME, sResourceBundle,msgArgs));
    }
    Workspace ws = null;
    try {
      //retrieves the workspace
      ws = vm.getWorkspaceByName(pWorkspaceId);
    }catch (VersionException ve) {
      String msgArgs[]={pWorkspaceId};
      throw new RepositoryException(ResourceUtils.getMsgResource(CUS_UNABLE_TO_GET_WS, MY_RESOURCE_NAME, sResourceBundle,msgArgs), ve);
    }
    //checks is workspace is already checked in
    if (ws != null && ws.isCheckedIn()){
      String msgArgs[]={ ws.getName()};
      throw new RepositoryException(ResourceUtils.getMsgResource(CUS_USING_CHECKED_IN_WS, MY_RESOURCE_NAME, sResourceBundle,msgArgs));
    }
    if (ws != null) return ws;

    Branch parent = null;
    try {
      parent = vm.getBranchByName("main");
    } 
    catch (VersionException ve) {
      String msgArgs[] = {ResourceUtils.getMsgResource(CUS_WS_NOT_CREATED, MY_RESOURCE_NAME, sResourceBundle), ResourceUtils.getMsgResource(CUS_INVALID_PARENT_BRANCH, MY_RESOURCE_NAME, sResourceBundle) };
      throw new RepositoryException(ResourceUtils.getMsgResource(CUS_WS_NOT_CREATED_INVALID_PARENT_BRANCH, MY_RESOURCE_NAME, sResourceBundle,msgArgs) + ve);
    }

    if (parent == null)
      throw new RepositoryException(ResourceUtils.getMsgResource(CUS_INVALID_PARENT_BRANCH, MY_RESOURCE_NAME, sResourceBundle));

    try {
      //creates a new workspace
      ws = vm.createWorkspace(pWorkspaceId, parent);
    }catch (VersionException ve) {
      String msgArgs[] = {ResourceUtils.getMsgResource(CUS_WS_NOT_CREATED, MY_RESOURCE_NAME, sResourceBundle), pWorkspaceId };
      throw new RepositoryException(ResourceUtils.getMsgResource(CUS_NAME_ALREADY_IN_USE, MY_RESOURCE_NAME, sResourceBundle, msgArgs), ve);
    }
    return ws;
  }

  /**
   * Reset the user identity to the previous one
   * @param pPreviousUser - the previous thread user
   **/
  protected void unassumeUserIdentity(User pPreviousUser)
  {
    ThreadSecurityManager.setThreadUser(pPreviousUser);
  }

  /**
   * @return This method generates and returns the name of the process.
   **/
  protected String getProcessName(){
    Calendar calendar = new GregorianCalendar();
    calendar.setTimeInMillis(System.currentTimeMillis());
    String processName = PROCESS_NAME_PREFIX + calendar.get(Calendar.YEAR) + calendar.get(Calendar.MONTH) +
        calendar.get(Calendar.DATE) + calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) +
        calendar.get(Calendar.SECOND);
    return processName;
  }

  /**
   * Returns an array of category leaf nodes items for the given repository using the catalog id's
   * passed in. 
   *
   * @return RepositoryItem[] - array of catalog items
   **/
  public RepositoryItem[] getCategoryLeafNodeArray() 
  throws RepositoryException {
    
    String perfName = "getCategoryLeafNodeArray";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    
    Collection<RepositoryItem> returnCategoryCollection = new ArrayList<RepositoryItem>();

    try {
      //first get all catalogs specified. if pCatalogsIds is null then do
      //all catalogs
      RepositoryView catalogView = mCatalogProperties.getCatalogRepositoryView(getRepository());
      QueryBuilder queryBuilder = catalogView.getQueryBuilder();
      
      String[] catalogIDs = (String[]) getCatalogIds().toArray(new String[getCatalogIds().size()]);
      
      Query catalogsQuery = queryBuilder.createIdMatchingQuery(catalogIDs);
      RepositoryItem[] catalogs = catalogView.executeQuery(catalogsQuery);
      
      Collection<RepositoryItem> catalogCollection = Arrays.asList(catalogs);
    
      //Now that we have the passed in catalogs, find all the category leaf nodes
      for (RepositoryItem catalog:catalogCollection) {
        returnCategoryCollection.addAll(recurseTreeAndGetCategories(catalog));
      }
      
      if (isLoggingDebug())
        logDebug("Collection of base leaf node categories:" + returnCategoryCollection);
  
    } finally {
      try {
        // End the performance monitoring of the current operation
        PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfName);
      } catch (PerfStackMismatchException e) {
        if (isLoggingWarning()) {
          logWarning(PERFORM_MONITOR_NAME + " failed: " + e);
        }
      }
    }
    
    //Pass back the array
    return (RepositoryItem[]) returnCategoryCollection.toArray(new RepositoryItem[returnCategoryCollection.size()]);
  }

  /**
  * Uses recursion to walk down the tree from the item (either a category or catalog)
  * to build a collection of catalogs
  *
  * @param pItem - this is either a catalog or a category
  * @return Collection<RepositoryItem> - collection of catalog items
  **/
  protected Collection<RepositoryItem> recurseTreeAndGetCategories(RepositoryItem pItem) throws RepositoryException {
    String perfName = "recurseTreeAndGetCategories";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    
    if(isLoggingDebug())
      logDebug(perfName + ": " + pItem.getItemDescriptor().getItemDescriptorName()  + " " + pItem.getRepositoryId() + " - starting.");
    
    Collection<RepositoryItem> returnLeafNodeCategories = new HashSet<RepositoryItem>();
    Collection<RepositoryItem> subCategories = null;
    Collection<RepositoryItem> subCatalogs = null;
    Collection<RepositoryItem> children = new HashSet<RepositoryItem>();
    
    try {
      //If pItem is a catalog then add the rootSubCatalogs else it is a category so add the subCatalogs
      if (getCatalogTools().isCatalog(pItem)) {
        subCatalogs = (Collection<RepositoryItem>) pItem.getPropertyValue(mCatalogProperties.getRootSubCatalogsPropertyName());
        subCategories = (Collection<RepositoryItem>) pItem.getPropertyValue(mCatalogProperties.getRootCategoriesPropertyName());
      } else {
        subCatalogs = (Collection<RepositoryItem>) pItem.getPropertyValue(mCatalogProperties.getSubCatalogsPropertyName());
        subCategories = (Collection<RepositoryItem>) pItem.getPropertyValue(mCatalogProperties.getFixedChildCategoriesPropertyName());
      }
  
      //Now add both catalogs and categories so we can recurse
      if (subCatalogs != null)
        children.addAll(subCatalogs);
      
      if (subCategories != null)
        children.addAll(subCategories);
      
      if(children.size() == 0) {
        returnLeafNodeCategories.add(pItem);
      }
      
      //Now recurse on down the tree looking for any catalogs
      for (RepositoryItem item:children) {
        if(isLoggingDebug())
          logDebug(perfName + ":  " + item.getItemDescriptor().getItemDescriptorName()  + " " + item.getRepositoryId() + " - recursing.");
        
        //Check and see if this item is a category and if it is at the bottom of the stack
        if (getCatalogTools().isCategory(item)
            && ((Collection<RepositoryItem>) item.getPropertyValue(mCatalogProperties.getSubCatalogsPropertyName())).size() == 0 
            && ((Collection<RepositoryItem>) item.getPropertyValue(mCatalogProperties.getFixedChildCategoriesPropertyName())).size() == 0) {
          returnLeafNodeCategories.add(item);
        } else 
          returnLeafNodeCategories.addAll(recurseTreeAndGetCategories(item));
      }
  
      if(isLoggingDebug()) {
        logDebug(perfName + ": Returning categories:" + returnLeafNodeCategories);
        logDebug(perfName + ": " + pItem.getItemDescriptor().getItemDescriptorName()  + " " + pItem.getRepositoryId() + " - finished.");
      }
    } finally {
      try {
        // End the performance monitoring of the current operation
        PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfName);
      } catch (PerfStackMismatchException e) {
        if (isLoggingWarning()) {
          logWarning(PERFORM_MONITOR_NAME + " failed: " + e);
        }
      }
    }
    
    return returnLeafNodeCategories;
  }

  /**
   * 
   * @param pParentItem
   * @param pCurrentItem
   * @param pShadowBean
   * @throws RepositoryException
   */
  protected void buildParentCategoriesForCatalog(RepositoryItem pParentItem, RepositoryItem pCurrentItem, ShadowBean pShadowBean) throws RepositoryException {
    String perfName = "buildParentCategoriesForCatalog";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    
    try {
      if (isLoggingDebug())
        logDebug("buildParentCategoriesForCatalog: starting with pShadowBean:" + pShadowBean);
      
      CatalogProperties props = getCatalogProperties(); 
      
      if (getCatalogTools().isCatalog(pParentItem)) {
        if (getCatalogTools().isCatalog(pCurrentItem)) {
          String catalogId = (String) pParentItem.getPropertyValue(props.getIdPropertyName());          
          if (pShadowBean.getParentCategoriesForCatalog().containsKey(catalogId)) {
            //Since it is already there then add to the array
            Collection<String> categories = pShadowBean.getParentCategoriesForCatalog().get(catalogId);
            if (categories == null) {
              categories = new HashSet<String>();
            }
            pShadowBean.updateCategories(catalogId, categories);
          } else {
            //This is the first time we add this catalog
            Collection<String> categories = new HashSet<String>();
            pShadowBean.updateCategories(catalogId, categories);
          }
        } else if (getCatalogTools().isCategory(pCurrentItem)) {
          //So since the parent is a catalog and the current item is a category go ahead and add an item to the parentCategoriesForCatalog Map.
          String catalogId = (String) pParentItem.getPropertyValue(props.getIdPropertyName());
          //Check to see if this catalog is already there!
          if (pShadowBean.getParentCategoriesForCatalog().containsKey(catalogId)) {
            //Since it is already there then add to the array
            Collection<String> categories = pShadowBean.getParentCategoriesForCatalog().get(catalogId);
            if (categories != null)
              categories.add(pCurrentItem.getRepositoryId());
            else {
              categories = new HashSet<String>();
              categories.add(pCurrentItem.getRepositoryId());
            }
            pShadowBean.updateCategories(catalogId, categories);
          } else {
            //This is the first time we add this catalog
            Collection<String> categories = new HashSet<String>();
            categories.add(pCurrentItem.getRepositoryId());
            pShadowBean.updateCategories(catalogId, categories);
          }
          
          pShadowBean.updateCategory(pCurrentItem);
        }
      } else if (getCatalogTools().isCategory(pParentItem)) {
        if (getCatalogTools().isCategory(pCurrentItem)) {
          //See if any of the entries in the map use the parent category, if so replace it with the child
          pShadowBean.updateCategory(pCurrentItem);
        } else {
          //remove the parent category
          pShadowBean.removeCategory(pParentItem);
        }
      }
      
      if (isLoggingDebug())
        logDebug("buildParentCategoriesForCatalog: ending with pShadowBean:" + pShadowBean);
    } finally {
      try {
        // End the performance monitoring of the current operation
        PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfName);
      } catch (PerfStackMismatchException e) {
        if (isLoggingWarning()) {
          logWarning(PERFORM_MONITOR_NAME + " failed: " + e);
        }
      }
    }
  }
  
  /**
   * This method will iterate through the completed items map and
   * call updateRepositoryItem to update the database.
   * 
   * @throws RepositoryException
   * @throws TransactionDemarcationException
   */
  protected void updateRepository() throws RepositoryException, TransactionDemarcationException {
    TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
    int transactionCount = 0;
    
    try {
      //Go ahead and override theh CatalogChangeListener from executing, it will
      //still fire but will return without doing any processing.
      ThreadLocalUpdateOverride.set(true);
      
      //Get the map of objects to update in a form that we can iterate through them to update
      Set set = getCompletedItems().entrySet();
      
      //Get the transaction
      transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      
      Iterator i = set.iterator();
      while(i.hasNext()){
        Map.Entry me = (Map.Entry)i.next();
        
        //Now get the associated ShadowBean that cached the update values till this final update
        ShadowBean shadowBean  = (ShadowBean) me.getValue();
        
        ShadowMapKey shadowMapKey = (ShadowMapKey) me.getKey();
        RepositoryItem itemToUpdate = getRepository().getItem(shadowMapKey.getId(), shadowBean.getTypeOfItem());

        //If we have reached the max number of transactions the go ahead a restart.
        if (transactionCount > getMaxItemsPerTransaction()) {
          if (isLoggingInfo())
            logInfo("updateRepository: " + getMaxItemsPerTransaction() + " transactions reached, restarting.");
          
          transactionDemarcation.end();
          transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
          transactionCount=0;
        }
        
        updateRepositoryItem(itemToUpdate, shadowBean);
        transactionCount++;
      }    
    } finally {
      transactionDemarcation.end();
      ThreadLocalUpdateOverride.set(false);
    }
  }
  
  /**
   * This method takes the values from the shadow bean and aplies them to the repository
   * item and then issues the update. 
   * 
   * @param pItemToUpdate
   * @param pShadowBean
   * @throws RepositoryException
   * @throws TransactionDemarcationException
   */
  protected void updateRepositoryItem(RepositoryItem pItemToUpdate, ShadowBean pShadowBean) 
  throws RepositoryException, TransactionDemarcationException {
    MutableRepository mutableCatalogRepository = (MutableRepository) getRepository();
    
    //Get and convert the repositoryItem into one that can be updated!
    MutableRepositoryItem mutableRepositoryItem = (MutableRepositoryItem) pItemToUpdate;
    

    if (isLoggingDebug())
      logDebug("updateRepositoryItem:mutableRepositoryItem:" + mutableRepositoryItem + " ShadowBean:" + pShadowBean);
    
    //Now let the ShadowBean tell us which values to update
    if (pShadowBean.getAllRootCategories() != null) {
      //Get the current items
      Set<RepositoryItem> currentAllRootCategories = (Set<RepositoryItem>) mutableRepositoryItem.getPropertyValue(getCatalogProperties().getAllRootCategoriesPropertyName());
      Set<RepositoryItem> currentAllRootCategoriesToRemove = new HashSet<RepositoryItem>();
      
      //First remove any extras
      for (RepositoryItem category:currentAllRootCategories) {
        if (!pShadowBean.getAllRootCategories().contains(category.getRepositoryId()))
          currentAllRootCategoriesToRemove.add(category);
      }
      
      currentAllRootCategories.removeAll(currentAllRootCategoriesToRemove);

      //Now add any missing
      for (String id:pShadowBean.getAllRootCategories()) {
        RepositoryItem category = getRepository().getItem(id, getCatalogProperties().getCategoryItemName());
        if (!currentAllRootCategories.contains(category))
          currentAllRootCategories.add(category);
      }
    }
    
    //Now let the Sites tell us which values to update
    if (pShadowBean.getSites() != null) {
      //Get the current items
      Set<String> currentSites = (Set<String>) mutableRepositoryItem.getPropertyValue(getCatalogProperties().getSitesPropertyName());
      Set<String> sitesToRemove = new HashSet<String>();
      
      //First do the removes
      for (String site:currentSites) {
        if (!pShadowBean.getSites().contains(site))
          sitesToRemove.add(site);
      }
      
      if (sitesToRemove.size() > 0)
        currentSites.removeAll(sitesToRemove);
      
      //Now the adds
      for (String site:pShadowBean.getSites()) {
        if (!currentSites.contains(site))
          currentSites.add(site);
      }
    }
    
    //Now let the AncestorCategories tell us which values to update
    if (pShadowBean.getAncestorCategories() != null) {
      //Get the current items
      List<RepositoryItem> currentAncestorCategories = null;
      List<RepositoryItem>  currentAncestorCategoriesToRemove = new ArrayList<RepositoryItem> ();
      currentAncestorCategories = (List<RepositoryItem>) mutableRepositoryItem.getPropertyValue(getCatalogProperties().getAncestorCategoriesPropertyName());
      
      //First remove any extras
      for (RepositoryItem category:currentAncestorCategories) {
        if (!pShadowBean.getAncestorCategories().contains(category.getRepositoryId()))
          currentAncestorCategoriesToRemove.add(category);
      }
      
      currentAncestorCategories.removeAll(currentAncestorCategoriesToRemove);

      //Now add any missing
      for (String id:pShadowBean.getAncestorCategories()) {
        RepositoryItem category = getRepository().getItem(id, getCatalogProperties().getCategoryItemName());
        if (!currentAncestorCategories.contains(category))
          currentAncestorCategories.add(category);
      }
    }
    
    //Now let the ComputedCatalogs tell us which values to update
    if (pShadowBean.getComputedCatalogs() != null) {
      //Get the current items
      Set<RepositoryItem> currentComputedCatalogs = (Set<RepositoryItem>) mutableRepositoryItem.getPropertyValue(getCatalogProperties().getComputedCatalogsPropertyName());
      Set<RepositoryItem> computedCatalogsToRemove = new HashSet<RepositoryItem>();
      
      //First remove any extras
      for (RepositoryItem catalog:currentComputedCatalogs) {
        if (!pShadowBean.getComputedCatalogs().contains(catalog.getRepositoryId()))
          computedCatalogsToRemove.add(catalog);
      }

      if (computedCatalogsToRemove.size()>0)
        currentComputedCatalogs.removeAll(computedCatalogsToRemove);
      
      //Now add any missing
      for (String id:pShadowBean.getComputedCatalogs()) {
        RepositoryItem catalog = getRepository().getItem(id, getCatalogProperties().getCatalogItemName());
        if (!currentComputedCatalogs.contains(catalog))
          currentComputedCatalogs.add(catalog);
      }
    }
    
    //Now let the DirectAncestorCatalogsAndSelf tell us which values to update
    if (pShadowBean.getDirectAncestorCatalogsAndSelf() != null) {
      //Get the current items
      List<RepositoryItem> currentDirectAncestorCatalogsAndSelf = 
        (List<RepositoryItem>) mutableRepositoryItem.getPropertyValue(getCatalogProperties().getDirectAncestorCatalogsAndSelfPropertyName());
      List<RepositoryItem>  currentDirectAncestorCatalogsAndSelfToRemove = new ArrayList<RepositoryItem> ();
      
      //First remove any extras
      for (RepositoryItem catalog:currentDirectAncestorCatalogsAndSelf) {
        if (!pShadowBean.getDirectAncestorCatalogsAndSelf().contains(catalog.getRepositoryId()))
          currentDirectAncestorCatalogsAndSelfToRemove.add(catalog);
      }

      currentDirectAncestorCatalogsAndSelf.removeAll(currentDirectAncestorCatalogsAndSelfToRemove);
      
      //Now add any missing
      for (String id:pShadowBean.getDirectAncestorCatalogsAndSelf()) {
        RepositoryItem catalog = getRepository().getItem(id, getCatalogProperties().getCatalogItemName());
        if (!currentDirectAncestorCatalogsAndSelf.contains(catalog))
          currentDirectAncestorCatalogsAndSelf.add(catalog);
      }
    }

    //Now let the InDirectAncestorCatalogs tell us which values to update
    List<RepositoryItem> indirectAncestorCatalogs = new ArrayList<RepositoryItem>();
    if (pShadowBean.getIndirectAncestorCatalogs() != null) {
      
      //Get the current items
      List<RepositoryItem> currentIndirectAncestorCatalogs = 
        (List<RepositoryItem>) mutableRepositoryItem.getPropertyValue(getCatalogProperties().getIndirectAncestorCatalogsPropertyName());
      List<RepositoryItem>  currentIndirectAncestorCatalogsToRemove = new ArrayList<RepositoryItem> ();
      
      //First remove any extras
      for (RepositoryItem catalog:currentIndirectAncestorCatalogs) {
        if (!pShadowBean.getIndirectAncestorCatalogs().contains(catalog.getRepositoryId()))
          currentIndirectAncestorCatalogsToRemove.add(catalog);
      }
      
      currentIndirectAncestorCatalogs.removeAll(currentIndirectAncestorCatalogsToRemove);
      

      //Now add any missing
      for (String id:pShadowBean.getIndirectAncestorCatalogs()) {
        RepositoryItem catalog = getRepository().getItem(id, getCatalogProperties().getCatalogItemName());
        if (!currentIndirectAncestorCatalogs.contains(catalog))
          currentIndirectAncestorCatalogs.add(catalog);
      }
    }

    //This is a bit more complicated than the normal update. The user is able to enter in values for
    //parentCategroiesForCatalog. If they are valid we are to leave them. If they are not then
    //we need to update them. This means we needed all the possible values for categories for a given
    //catalog and not just one of them. So our data bean has a map using the catalog as the key with 
    //values of a set for all possible categories. The respository has only 
    //Calced parentCategoriesForCatalog
    if (getCatalogTools().isProduct(mutableRepositoryItem)
        || getCatalogTools().isCategory(mutableRepositoryItem)) {
      if (pShadowBean.getPriorParentCategoriesForCatalog() != null) {
        //Get the current Map of values, first check and see if there are any entries which might save us a bit of time        
        Map<String, RepositoryItem> currentParentCategoriesForCatalog = 
          (Map<String, RepositoryItem>) mutableRepositoryItem.getPropertyValue(getCatalogProperties().getParentCategoriesForCatalogPropertyName());
       
        //There are no values in the DB so just grab the calced values
        if (currentParentCategoriesForCatalog.size() == 0) {
          Set pccSet = pShadowBean.getPriorParentCategoriesForCatalog().entrySet();
          
          Iterator pccI = pccSet.iterator();
          while(pccI.hasNext()){
            Map.Entry pccME = (Map.Entry)pccI.next();
            String catalogId = (String) pccME.getKey();
            Collection<String> categories = (Collection<String>) pccME.getValue();
          
            //Check and see if the current item is in the list, if so remove it
            Collection<String> itemsToRemove = new HashSet<String>();
            for (String catId:categories) {
              if (catId.equals(mutableRepositoryItem.getRepositoryId()))
                itemsToRemove.add(catId);
            }
            
            if (itemsToRemove.size()>0)
              categories.removeAll(itemsToRemove);
            
            if (categories!=null
                && categories.size() > 0) {
              //Just grab a category and put it in.
              RepositoryItem tempItem = getRepository().getItem(categories.iterator().next(), getCatalogProperties().getCategoryItemName());
              currentParentCategoriesForCatalog.put(catalogId, tempItem);
            }
          }
        } else {
          //Compare the 2
          //Iterate through the current values 
          Set set = pShadowBean.getPriorParentCategoriesForCatalog().entrySet();
      
          Iterator i = set.iterator();
          while(i.hasNext()){
            Map.Entry me = (Map.Entry)i.next();
            String catalogId = (String) me.getKey();
            Collection<String> categoryItemIds = (Collection<String>) me.getValue();
            
            //Check and see if the current item is in the list, if so remove it
            Collection<String> itemsToRemove = new HashSet<String>();
            for (String catId:categoryItemIds) {
              if (catId.equals(mutableRepositoryItem.getRepositoryId()))
                itemsToRemove.add(catId);
            }
            
            if (itemsToRemove.size()>0)
              categoryItemIds.removeAll(itemsToRemove);
            
            //Ok so now we have the collection make sure they match
            if (currentParentCategoriesForCatalog.containsKey(catalogId)) {
              //See if the category is one on the list
              RepositoryItem category = currentParentCategoriesForCatalog.get(catalogId);

              if (!categoryItemIds.contains(category.getRepositoryId())) {
                //Remove the bad value               
                currentParentCategoriesForCatalog.remove(catalogId);
              
                //Just grab one too add a valid value back
                if (categoryItemIds!=null
                    && categoryItemIds.size() > 0) {
                  RepositoryItem tempItem = getRepository().getItem(categoryItemIds.iterator().next(), getCatalogProperties().getCategoryItemName());
                  currentParentCategoriesForCatalog.put(catalogId, tempItem);
                }
              }
            } else {
              //Just grab one
              if (categoryItemIds!=null
                  && categoryItemIds.size() > 0) {
                RepositoryItem tempItem = getRepository().getItem(categoryItemIds.iterator().next(), getCatalogProperties().getCategoryItemName());
                currentParentCategoriesForCatalog.put(catalogId, tempItem);
              }
            }
          }
          
          //Now iterate through the current map to make sure there are not any extra
          Set setCurrent = currentParentCategoriesForCatalog.entrySet();
          Collection<String> itemsToRemove = new HashSet<String>();
          
          Iterator iCurrent = setCurrent.iterator();
          while(iCurrent.hasNext()){
            //Get the catalog ID
            Map.Entry currentME = (Map.Entry)iCurrent.next();
            String catalogId = (String) currentME.getKey();
            //Now check and see if it needs to be removed
            if (!pShadowBean.getPriorParentCategoriesForCatalog().containsKey(catalogId)) {
              itemsToRemove.add(catalogId);
            }
          }

          //Now chuck the extras
          for (String catalogId:itemsToRemove) {
            currentParentCategoriesForCatalog.remove(catalogId);
          }
        }
      }
    }

    mutableCatalogRepository.updateItem(mutableRepositoryItem);
  }
  
  /**
   * This nested class is to track values in the recursion tree that are
   * not necessarily derived directly from the items parents.
   * 
   */
  protected class ShadowBean {
    private String mTypeOfItem;
     
    public String getTypeOfItem() {
      return mTypeOfItem;
    }

    public void setTypeOfItem(String pTypeOfItem) {
      mTypeOfItem = pTypeOfItem;
    }

    private String mId;
    
    public String getId() {
      return mId;
    }

    public void setId(String pId) {
      mId = pId;
    }

    private String mParentCatalog = null;
    
    public String getParentCatalog() {
      return mParentCatalog;
    }

    public void setParentCatalog(String pParentCatalog) {
      mParentCatalog = pParentCatalog;
    }

    private List<String> mDirectAncestorCatalogsAndSelf;
    
    public List<String> getDirectAncestorCatalogsAndSelf() {
      return mDirectAncestorCatalogsAndSelf;
    }

    public void setDirectAncestorCatalogsAndSelf(List<String> pDirectAncestorCatalogsAndSelf) {
      mDirectAncestorCatalogsAndSelf = pDirectAncestorCatalogsAndSelf;
    }
    
    private List<String> mInDirectAncestorCatalogs;
    
    public List<String> getIndirectAncestorCatalogs() {
      return mInDirectAncestorCatalogs;
    }

    public void setIndirectAncestorCatalogs(List<String> pInDirectAncestorCatalogs) {
      mInDirectAncestorCatalogs = pInDirectAncestorCatalogs;
    }
    
    private Set<String> mComputedCatalogs;

    public Set<String> getComputedCatalogs() {
      return mComputedCatalogs;
    }

    public void setComputedCatalogs(Set<String> pComputedCatalogs) {
      mComputedCatalogs = pComputedCatalogs;
    }
    
    private String mComputedCatalog = null;
    
    public String getComputedCatalog() {
      return mComputedCatalog;
    }

    public void setComputedCatalog(String pComputedCatalog) {
      mComputedCatalog = pComputedCatalog;
    }
    
    private List<String> mAncestorCategories;
    
    public List<String> getAncestorCategories() {
      return mAncestorCategories;
    }

    public void setAncestorCategories(List<String> pAncestorCategories) {
      mAncestorCategories = pAncestorCategories;
    }
    
    private Set<String> mAllRootCategories;
    
    public Set<String> getAllRootCategories() {
      return mAllRootCategories;
    }
    
    public void setAllRootCategories(Set<String> pAllRootCategories) {
      mAllRootCategories = pAllRootCategories;
    }

    private Set<String> mSites;

    public Set<String> getSites() {
      return mSites;
    }

    public void setSites(Set<String> pSites) {
      mSites = pSites;
    }
    
    private String mLastCatalogId;
    
    public String getLastCatalogId() {
      return mLastCatalogId;
    }

    public void setLastCatalogId(String pLastCatalogId) {
      mLastCatalogId = pLastCatalogId;
    }

    private  Map<String, Collection<String>> mPriorParentCategoriesForCatalog = new HashMap<String, Collection<String>>();
    
    public Map<String, Collection<String>> getPriorParentCategoriesForCatalog() {
      return mPriorParentCategoriesForCatalog;
    }

    public void setPriorParentCategoriesForCatalog(HashMap<String, Collection<String>> pPriorParentCategoriesForCatalog) {
      mPriorParentCategoriesForCatalog = pPriorParentCategoriesForCatalog;
    }

    private Map<String, Collection<String>> mParentCategoriesForCatalog = new HashMap<String, Collection<String>>();

    public Map<String, Collection<String>> getParentCategoriesForCatalog() {
      return mParentCategoriesForCatalog;
    }

    public void setParentCategoriesForCatalog(HashMap<String, Collection<String>> pParentCategoriesForCatalog) {
      mParentCategoriesForCatalog = pParentCategoriesForCatalog;
    }
    
    private void cloneToPrior(ShadowBean pTrackChanges) {
      //See if any of the entries in the map use the parent category, if so replace it with the child
      Set set = pTrackChanges.getParentCategoriesForCatalog().entrySet();
      
      Iterator i = set.iterator();
      while(i.hasNext()){
        Map.Entry me = (Map.Entry)i.next();
        String catalogId = (String) me.getKey();
        Collection<String> categories = (Collection<String>) me.getValue();
        Collection<String> tempCategories = null;
        if (getPriorParentCategoriesForCatalog().containsKey(catalogId)) {
          tempCategories = new HashSet<String>(getPriorParentCategoriesForCatalog().get(catalogId));
        } else
          tempCategories = new HashSet<String>();
        tempCategories.addAll(categories);        
        getPriorParentCategoriesForCatalog().put(catalogId, tempCategories);
      }
    }
    
    public void updateCategory(RepositoryItem pCurrentItem) {
      //
      Set set = getParentCategoriesForCatalog().entrySet();
      
      Iterator i = set.iterator();
      while(i.hasNext()){
        Map.Entry me = (Map.Entry)i.next();
        String catalogId = (String) me.getKey();
        Collection<String> tempCategories = (Collection<String>) me.getValue();
        if (tempCategories != null) {
          tempCategories.clear();
          //Now add the current item
          tempCategories.add(pCurrentItem.getRepositoryId());
          getParentCategoriesForCatalog().put(catalogId, tempCategories);
        }
      }
    }
    
    
    public void removeCategory(RepositoryItem pCurrentItem) {
      //
      Set set = getParentCategoriesForCatalog().entrySet();
      
      Iterator i = set.iterator();
      while(i.hasNext()){
        Map.Entry me = (Map.Entry)i.next();
        String catalogId = (String) me.getKey();
        Collection<String> tempCategories = (Collection<String>) me.getValue();
        
        if (tempCategories.contains(pCurrentItem.getRepositoryId())) {
          tempCategories.remove(pCurrentItem.getRepositoryId());
          getParentCategoriesForCatalog().put(catalogId, tempCategories);
        }
      }
    }
    
    public void updateCategories(String pCatalogId, Collection<String> pCategories) {
      //Make sure and just add to the list
      Collection<String> currentCategories = (Collection<String>) getParentCategoriesForCatalog().get(pCatalogId);
      if (currentCategories == null)
        currentCategories = new HashSet<String>();
      
      currentCategories.addAll(pCategories);
      getParentCategoriesForCatalog().put(pCatalogId, currentCategories);
      
      //Now add these categories to the other parent catalogs
      Set set = getParentCategoriesForCatalog().entrySet();

      Iterator i = set.iterator();
      while(i.hasNext()){
        Map.Entry me = (Map.Entry)i.next();
        String catalogId = (String) me.getKey();
        Collection<String> tempCategories = (Collection<String>) me.getValue();
        if (tempCategories == null || tempCategories.size()==0) {
          tempCategories = new HashSet<String>();
          tempCategories.addAll(pCategories);
          getParentCategoriesForCatalog().put(catalogId, tempCategories);
        }
      }

      setLastCatalogId(pCatalogId);
    }
    
    public void copyMembers(ShadowBean pShadowBean) {
      //Before we do this just clone the map
      cloneToPrior(pShadowBean); 
      
      //Copy all of the passed in parentCategoriesForCatalog into this object
      //Since this is actually a map of arrays we need to do a couple of things,
      //first see if there are any matches of the key, then if there is combine the
      //categories. This is a bit odd since this method can be called multiple
      //time for this object we need to append the priorParentStuff in that case.
      Set set = pShadowBean.getParentCategoriesForCatalog().entrySet();

      Iterator i = set.iterator();
      while(i.hasNext()){
        Map.Entry me = (Map.Entry)i.next();
        //Get the catalogId and the associated categories
        String catalogId = (String) me.getKey();
        Collection<String> tempCategories = new HashSet<String>();
        tempCategories.addAll(((Collection<String>) me.getValue()));

        //Now see if this key is in our map, if so combine the sets, else just put it in the map
        if (getParentCategoriesForCatalog().containsKey(catalogId)
            && tempCategories != null) {
          //Now combine the sets
          Collection<String> currentCategories = getParentCategoriesForCatalog().get(catalogId);
          currentCategories.addAll(tempCategories);
          getParentCategoriesForCatalog().put(catalogId, currentCategories);
        } else {
          HashSet<String> parentCategories = new HashSet<String>();
          parentCategories.addAll(pShadowBean.getParentCategoriesForCatalog().get(catalogId));
          getParentCategoriesForCatalog().put(catalogId, parentCategories);
        }
      }
      //Copy the lastCatalogId from the passed in object
      if (pShadowBean.getLastCatalogId() != null) {
        String lastCatalogId = new String(pShadowBean.getLastCatalogId());
        setLastCatalogId(lastCatalogId);
      }
    }
    
    /**
     * 
     */
    public String toString() {
      String toStringValue = "\n     " + getId() + " hashCode:" + hashCode();
      toStringValue = toStringValue + "\n     ParentCatalog:" + getParentCatalog();
      toStringValue = toStringValue + "\n     AncestorCategories:" + getAncestorCategories();
      toStringValue = toStringValue + "\n     ComputedCatalogs:" + getComputedCatalogs();
      toStringValue = toStringValue + "\n     Sites:" + getSites();
      toStringValue = toStringValue + "\n     LastCatalogId:" + getLastCatalogId();
      
      if (getParentCategoriesForCatalog() != null) {
        Set set = getParentCategoriesForCatalog().entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()){
          Map.Entry me = (Map.Entry)i.next();
          toStringValue = toStringValue + ("\n     Catalog:" + me.getKey() + " -- Category:" + me.getValue());
        }
        
        toStringValue = toStringValue + "\n     Prior Values";
        set = getPriorParentCategoriesForCatalog().entrySet();
        i = set.iterator();
        while(i.hasNext()){
          Map.Entry me = (Map.Entry)i.next();
          toStringValue = toStringValue + ("\n     Catalog:" + me.getKey() + " -- Category:" + me.getValue());
        }
      }
      
      if (toStringValue == null)
        toStringValue = "ShadowBean:" + hashCode() + ":Currently the calced values are null";
      
      return toStringValue;
    }
  }

  
  /**
   * This class will update a range of repository items
   * @author jturgeon
   *
   */
  protected abstract class UpdateRangeViaGSA implements Runnable {
    String mStartId;
    String mEndId;

    public String getStartId() {
      return mStartId;
    }

    public void setStartId(String pStartId) {
      mStartId = pStartId;
    }

    public String getEndId() {
      return mEndId;
    }

    public void setEndId(String pEndId) {
      mEndId = pEndId;
    }
    
    private RepositoryView mRepositoryView;

    public RepositoryView getRepositoryView() {
      return mRepositoryView;
    }

    public void setRepositoryView(RepositoryView pRepositoryView) {
      mRepositoryView = pRepositoryView;
    }

    private boolean mFirstRange;
    public boolean isFirstRange() {
      return mFirstRange;
    }

    public void setFirstRange(boolean pFirstRange) {
      mFirstRange = pFirstRange;
    }

    UpdateRangeViaGSA(String pStartId, String pEndId,  RepositoryView pRepositoryView) {
      setStartId(pStartId);
      setEndId(pEndId);
      setRepositoryView(pRepositoryView);
    }

    public void run() {
      String methodName = "run";
      
      if (isLoggingInfo())
        logInfo("Update thread started for range " + getStartId() + " to " + getEndId());
     
      TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
      
      try {
        transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
        
        //Get all of the skus
        QueryBuilder queryBuilder = getRepositoryView().getQueryBuilder();
        QueryExpression idQE = queryBuilder.createPropertyQueryExpression(getCatalogProperties().getIdPropertyName());
        QueryExpression startIdQE = queryBuilder.createConstantQueryExpression(getStartId());
        QueryExpression endIdQE = queryBuilder.createConstantQueryExpression(getEndId());
        Query greaterThanQuery = null;
        if (isFirstRange())
          greaterThanQuery = queryBuilder.createComparisonQuery(idQE, startIdQE, QueryBuilder.GREATER_THAN_OR_EQUALS);
        else
          greaterThanQuery = queryBuilder.createComparisonQuery(idQE, startIdQE, QueryBuilder.GREATER_THAN);
        Query[] combinedQueries;
        Query combinedQuery;
        combinedQueries = new Query[2];
        Query lessThanQuery = queryBuilder.createComparisonQuery(idQE, endIdQE, QueryBuilder.LESS_THAN_OR_EQUALS);
        combinedQueries[0] = greaterThanQuery;
        combinedQueries[1] = lessThanQuery;
        combinedQuery = queryBuilder.createAndQuery(combinedQueries);
          
        //This is soley so the repository will not cache these queries!
        QueryOptions qops = new QueryOptions(0, -1, null, null);
        qops.setDoNotCacheQuery(true);
        RepositoryItem[] itemsToProcess = getRepositoryView().executeQuery(combinedQuery, qops);
        
        //For each itme process it
        if (itemsToProcess != null) {
          if(isLoggingDebug())
            logDebug(methodName + ": There are " + itemsToProcess.length + " item to process");
  
          //Iterate through the array of skus to update
          for (RepositoryItem item:itemsToProcess) {
            //Create a shadowBean
            ShadowBean shadowBean = buildUpdateBean(item);
  
            //Now update the DB
            updateRepositoryItem(item, shadowBean);
          }
        }
      } catch (RepositoryException e) {
        if (isLoggingError())
          logError("Repository exception, attempting rollback. ", e);
        
        try {
          transactionDemarcation.getTransaction().rollback();
        } catch (IllegalStateException e1) {
          if (isLoggingError())
            logError("IllegalStateException exception during attempt to rollback. ", e);
        } catch (SystemException e1) {
          if (isLoggingError())
            logError("SystemException exception during attempt to rollback. ", e);
        }
      } catch (TransactionDemarcationException e) {
        if (isLoggingError())
          logError("TransactionDemarcationException exception, attempting rollback. ", e);
        
        try {
          transactionDemarcation.getTransaction().rollback();
        } catch (IllegalStateException e1) {
          if (isLoggingError())
            logError("IllegalStateException exception during attempt to rollback. ", e);
        } catch (SystemException e1) {
          if (isLoggingError())
            logError("SystemException exception during attempt to rollback. ", e);
        }
      } finally {
        if (isLoggingInfo()) {
          if (!getEndId().equals(new String()))
            logInfo("Update thread ended for range " + getStartId() + " to " + getEndId());
          else
            logInfo("Update thread ended for range " + getStartId() + " to end of dataset");
        }
        
        try {
          transactionDemarcation.end();
        } catch (TransactionDemarcationException e) {
          if (isLoggingError())
            logError("TransactionDemarcationException in ending transaction, attempting rollback. ", e);
          try {
            transactionDemarcation.getTransaction().rollback();
          } catch (IllegalStateException e1) {
            if (isLoggingError())
              logError("IllegalStateException exception during attempt to rollback. ", e);
          } catch (SystemException e1) {
            if (isLoggingError())
              logError("SystemException exception during attempt to rollback. ", e);
          }
        }
      }
    }
    
    //This will do build the bean for the update
    public abstract ShadowBean buildUpdateBean(RepositoryItem pRepositoryItem)
      throws RepositoryException;
  }
  
  /**
   * This class will update a range of repository items
   * @author jturgeon
   *
   */
  protected abstract class UpdateRange implements Runnable {
    private GSAItemDescriptor mRepositoryItemDescriptor;

    public GSAItemDescriptor getRepositoryItemDescriptor() {
      return mRepositoryItemDescriptor;
    }

    public void setRepositoryItemDescriptor(GSAItemDescriptor pRepositoryItemDescriptor) {
      mRepositoryItemDescriptor = pRepositoryItemDescriptor;
    }

    private String mDeleteSiteCheckSQL;
    private String mInsertSiteCheckSQL;
    
    private String mDeleteCatalogsCheckSQL;
    private String mInsertCatalogsCheckSQL;
    
    private String mInsertProductAncestorCategoriesSQL;
    private String mDeleteProductAncestorCategoriesSQL;
    
    private String mInsertProductParentCategoriesForCatalogSQL;
    private String mDeleteProductParentCategoriesForCatalogCheckSQL;
    
    public String getInsertProductAncestorCategoriesSQL() {
      return mInsertProductAncestorCategoriesSQL;
    }

    public void setInsertProductAncestorCategoriesSQL(String pInsertProductAncestorCategoriesSQL) {
      mInsertProductAncestorCategoriesSQL = pInsertProductAncestorCategoriesSQL;
    }

    public String getInsertProductParentCategoriesForCatalogSQL() {
      return mInsertProductParentCategoriesForCatalogSQL;
    }

    public void setInsertProductParentCategoriesForCatalogSQL(String pInsertProductParentCategoriesForCatalogSQL) {
      mInsertProductParentCategoriesForCatalogSQL = pInsertProductParentCategoriesForCatalogSQL;
    }

    public String getDeleteSiteCheckSQL() {
      return mDeleteSiteCheckSQL;
    }

    public void setDeleteSiteCheckSQL(String pDeleteSiteCheckSQL) {
      mDeleteSiteCheckSQL = pDeleteSiteCheckSQL;
    }

    public String getInsertSiteCheckSQL() {
      return mInsertSiteCheckSQL;
    }

    public void setInsertSiteCheckSQL(String pInsertSiteCheckSQL) {
      mInsertSiteCheckSQL = pInsertSiteCheckSQL;
    }

    public String getDeleteCatalogsCheckSQL() {
      return mDeleteCatalogsCheckSQL;
    }

    public void setDeleteCatalogsCheckSQL(String pDeleteCatalogsCheckSQL) {
      mDeleteCatalogsCheckSQL = pDeleteCatalogsCheckSQL;
    }

    public String getInsertCatalogsCheckSQL() {
      return mInsertCatalogsCheckSQL;
    }

    public void setInsertCatalogsCheckSQL(String pInsertCatalogsCheckSQL) {
      mInsertCatalogsCheckSQL = pInsertCatalogsCheckSQL;
    }

    public String getDeleteProductAncestorCategoriesSQL() {
      return mDeleteProductAncestorCategoriesSQL;
    }

    public void setDeleteProductAncestorCategoriesSQL(String pDeleteProductAncestorCategoriesSQL) {
      mDeleteProductAncestorCategoriesSQL = pDeleteProductAncestorCategoriesSQL;
    }

    public String getDeleteProductParentCategoriesForCatalogCheckSQL() {
      return mDeleteProductParentCategoriesForCatalogCheckSQL;
    }

    public void setDeleteProductParentCategoriesForCatalogCheckSQL(String pDeleteProductParentCategoriesForCatalogCheckSQL) {
      mDeleteProductParentCategoriesForCatalogCheckSQL = pDeleteProductParentCategoriesForCatalogCheckSQL;
    }

    private String mStartId;
    private String mEndId;
    
    public String getStartId() {
      return mStartId;
    }

    public void setStartId(String pStartId) {
      mStartId = pStartId;
    }

    public String getEndId() {
      return mEndId;
    }

    public void setEndId(String pEndId) {
      mEndId = pEndId;
    }
    
    private RepositoryView mRepositoryView;

    public RepositoryView getRepositoryView() {
      return mRepositoryView;
    }

    public void setRepositoryView(RepositoryView pRepositoryView) {
      mRepositoryView = pRepositoryView;
    }

    private Set<String> mIdsForSites;
    private Set<String> mIdsForCatalogs;
    private Set<String> mIdsForCategories;
    private Set<String> mIdsForParentCategoryForCatalog;
    private Set<String> mIdsForDynamicProducts;
    

    public Set<String> getIdsForDynamicProducts() {
      return mIdsForDynamicProducts;
    }

    public void setIdsForDynamicProducts(Set<String> pIdsForDynamicProducts) {
      mIdsForDynamicProducts = pIdsForDynamicProducts;
    }

    public Set<String> getIdsForSites() {
      return mIdsForSites;
    }

    public void setIdsForSites(Set<String> pIdsForSites) {
      mIdsForSites = pIdsForSites;
    }

    public Set<String> getIdsForCatalogs() {
      return mIdsForCatalogs;
    }

    public void setIdsForCatalogs(Set<String> pIdsForCatalogs) {
      mIdsForCatalogs = pIdsForCatalogs;
    }

    public Set<String> getIdsForCategories() {
      return mIdsForCategories;
    }

    public void setIdsForCategories(Set<String> pIdsForCategories) {
      mIdsForCategories = pIdsForCategories;
    }

    public Set<String> getIdsForParentCategoryForCatalog() {
      return mIdsForParentCategoryForCatalog;
    }

    public void setIdsForParentCategoryForCatalog(Set<String> pIdsForParentCategoryForCatalog) {
      mIdsForParentCategoryForCatalog = pIdsForParentCategoryForCatalog;
    }

    protected void getChangeIds(Connection pDBConnection, String pSQLToExecute, Set<String> pIds, List<String> pParams) 
    throws SQLException {
      PreparedStatement psToexec = pDBConnection.prepareStatement(pSQLToExecute);
      int paramIndex=1;
      for(String param:pParams) {
        psToexec.setString(paramIndex, pParams.get(paramIndex-1));
        paramIndex++;
      }
      
      ResultSet rs = psToexec.executeQuery();
      
      while (rs.next()) {
        String id = rs.getString(1);
        
        if (!pIds.contains(id)) {
          pIds.add(id);
        }
      }

      rs.close();
      psToexec.clearParameters();
      psToexec.close();
    }
    
    UpdateRange(String pStartId, String pEndId,  RepositoryView pRepositoryView) {
      setStartId(pStartId);
      setEndId(pEndId);
      setRepositoryView(pRepositoryView);
    }

    public void run() {
      String methodName = "run";
      
      if (isLoggingInfo()) {
        if (!getEndId().equals(new String("")))
          logInfo("Update thread started for range " + getStartId() + " to " + getEndId());
        else
          logInfo("Update thread started for range " + getStartId() + " to end of dataset");
      }

      TransactionDemarcation transactionDemarcationUpdate = new TransactionDemarcation();
      GSARepository repository = (GSARepository) getRepository();
      boolean handleRangesInMemory = repository.getHandleRangesInMemory();
      Connection dbConnection = null;
      
      try {
        Set<String> idsForSites = new HashSet<String>();
        Set<String> idsForCatalogs = new HashSet<String>();
        Set<String> idsForCategories = new HashSet<String>();
        Set<String> idsForParentCategoryForCatalog = new HashSet<String>();
        Set<String> idsForDynamicProducts = new HashSet<String>();
        Set<String> idsCombined = new HashSet<String>();
  
        dbConnection = repository.getDataSource().getConnection();
        dbConnection.setAutoCommit(false);
        repository.setHandleRangesInMemory(false);
        
        //This next section uses direct sql to figure out which skus in this batch actually need
        //to be processed. This reduces the amount of work that the standard way of using the
        //GSA which would need to pull back each object to see if it needs to change. This way
        //we know up front which items need to change and then we iterate through just those items.
        if (getDeleteSiteCheckSQL() != null) {
          List<String> params = new ArrayList<String>();
          params.add(getStartId());
          params.add(getEndId());          
          getChangeIds(dbConnection, getDeleteSiteCheckSQL(), idsForSites, params);
        }
        
        if (getInsertSiteCheckSQL() != null){
          List<String> params = new ArrayList<String>();
          params.add(getStartId());
          params.add(getEndId()); 
          params.add(getStartId());
          params.add(getEndId()); 
          getChangeIds(dbConnection, getInsertSiteCheckSQL(), idsForSites, params);
        }

        if (getDeleteCatalogsCheckSQL() != null) {
          List<String> params = new ArrayList<String>();
          params.add(getStartId());
          params.add(getEndId());    
          getChangeIds(dbConnection, getDeleteCatalogsCheckSQL(), idsForCatalogs, params);
        }
        
        if (getInsertCatalogsCheckSQL() != null) {
          List<String> params = new ArrayList<String>();
          params.add(getStartId());
          params.add(getEndId());  
          params.add(getStartId());
          params.add(getEndId()); 
          getChangeIds(dbConnection, getInsertCatalogsCheckSQL(), idsForCatalogs, params);
        }

        if (getDeleteProductAncestorCategoriesSQL() != null ) {
          List<String> params = new ArrayList<String>();
          params.add(getStartId());
          params.add(getEndId());    
          getChangeIds(dbConnection, getDeleteProductAncestorCategoriesSQL(), idsForCategories, params);
        }

        if (getInsertProductAncestorCategoriesSQL() != null) {
          List<String> params = new ArrayList<String>();
          params.add(getStartId());
          params.add(getEndId());
          params.add(getStartId());
          params.add(getEndId());    
          getChangeIds(dbConnection, getInsertProductAncestorCategoriesSQL(), idsForCategories, params);
        }

        if (getDeleteProductParentCategoriesForCatalogCheckSQL() != null) {
          List<String> params = new ArrayList<String>();
          params.add(getStartId());
          params.add(getEndId());    
          getChangeIds(dbConnection, getDeleteProductParentCategoriesForCatalogCheckSQL(), idsForParentCategoryForCatalog, params);
        }

        if (getInsertProductParentCategoriesForCatalogSQL() != null) {
          List<String> params = new ArrayList<String>();
          params.add(getStartId());
          params.add(getEndId());  
          getChangeIds(dbConnection, getInsertProductParentCategoriesForCatalogSQL(), idsForParentCategoryForCatalog, params);
        }
        
        //Build the dynamic list!
        if (getDynamicProductsMap() != null) {
          Set dynamicSet = getDynamicProductsMap().entrySet();
          
          Iterator dynamicSetIterator = dynamicSet.iterator();
          while(dynamicSetIterator.hasNext()){
            Map.Entry dynamicSetME = (Map.Entry)dynamicSetIterator.next();
            String productId = (String) dynamicSetME.getKey();
          
            if (productId.compareTo(getStartId()) >= 0 && productId.compareTo(getEndId())<=0) {
              idsForDynamicProducts.add(productId);
            }
          }
        }
        
        //Now that we have built the list of changes go ahead and do the repository thing
        if(isLoggingDebug()) {
          logDebug(methodName + ": There are " + idsForSites.size() + " site items to process");
          logDebug(methodName + ": There are " + idsForCatalogs.size() + " catalog items to process");
          logDebug(methodName + ": There are " + idsForCategories.size() + " category items to process");
          logDebug(methodName + ": There are " + idsForParentCategoryForCatalog.size() + " parentCat items to process");
          logDebug(methodName + ": There are " + idsForDynamicProducts.size() + " dynamic products items to process");
        }

        if (idsForSites.size() + idsForCatalogs.size() + idsForCategories.size()
                + idsForParentCategoryForCatalog.size() + idsForDynamicProducts.size() > 0) {
            loadUpdateRangeData(dbConnection, getStartId(), getEndId());            
        }

        dbConnection.commit();
        
        transactionDemarcationUpdate.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
        
        if (idsForSites.size() + idsForCatalogs.size() + idsForCategories.size()
            + idsForParentCategoryForCatalog.size() + idsForDynamicProducts.size() > 0) {

            idsCombined.addAll(idsForSites);

            for (String ids : idsForCatalogs) {
              if (!idsCombined.contains(ids)) {
                idsCombined.add(ids);
              }
            }

            for (String ids : idsForCategories) {
              if (!idsCombined.contains(ids)) {
                idsCombined.add(ids);
              }
            }

            for (String ids : idsForParentCategoryForCatalog) {
              if (!idsCombined.contains(ids)) {
                idsCombined.add(ids);
              }
            }

            if (isIncludeDynamicChildren()) {
              for (String ids : idsForDynamicProducts) {
                if (!idsCombined.contains(ids)) {
                  idsCombined.add(ids);
                }
              }
            }
            
            setIdsForSites(idsForSites);
            setIdsForCatalogs(idsForCatalogs);
            setIdsForCategories(idsForCategories);
            setIdsForParentCategoryForCatalog(idsForParentCategoryForCatalog);
            setIdsForDynamicProducts(idsForDynamicProducts);
            
            // Iterate through the array of skus to update
            for (String itemId : idsCombined) {
              // Get the repositoryItem
              RepositoryItem itemToUpdate = repository.getItem(itemId, getRepositoryItemDescriptor()
                  .getItemDescriptorName());

              //First check if this item is actually there
              if (itemToUpdate != null) {
                if (isLoggingDebug())
                  logDebug("Building UpdateBean:" + itemToUpdate.getRepositoryId());
  
                // Create a shadowBean
                ShadowBean shadowBean = buildUpdateBean(dbConnection, itemToUpdate);
                
                if (isLoggingDebug())
                  logDebug("Finished Building UpdateBean:" + itemToUpdate.getRepositoryId());
  
                // Now update the DB
                updateRepositoryItem(itemToUpdate, shadowBean);
                
                if (isLoggingDebug())
                  logDebug("Finished update of UpdateBean:" + itemToUpdate.getRepositoryId());
              } else {
                //The item has not been retrieved, this use case was where the incremental deploy only
                //half deleted data leaving the site information dangling. This should never happen if
                //the deploy is working properly
                if (!idsForDynamicProducts.contains(itemId) && isLoggingError()) {
                  logError("Item with item id:" + itemId + " has child data records with no parent record. Processing is being skipped.");
                }
              }
            }
            
            if (isLoggingInfo())
                logInfo("Finished update of data.");
            }
        } catch (TransactionDemarcationException e) {
          if (isLoggingError())
            logError("TransactionDemarcationException exception, attempting rollback. ", e);
        } catch (SQLException e) {
          if (isLoggingError())
            logError(e);
        } catch (RepositoryException e) {
          if (isLoggingError())
            logError(e);
        } finally {
            try {
                transactionDemarcationUpdate.end();
              } catch (TransactionDemarcationException e) {
                if (isLoggingError())
                  logError("TransactionDemarcationException in ending transaction, attempting rollback. ", e);
                try {
                  transactionDemarcationUpdate.getTransaction().rollback();
                } catch (IllegalStateException e1) {
                  if (isLoggingError())
                    logError("IllegalStateException exception during attempt to rollback. ", e);
                } catch (SystemException e1) {
                  if (isLoggingError())
                    logError("SystemException exception during attempt to rollback. ", e);
                }
            }
              
              
          try {
              dbConnection.close();
            } catch (SQLException e) {
              if (isLoggingError())
                logError(e);
            }
          repository.setHandleRangesInMemory(handleRangesInMemory);
        }
    }
    
    //This will do build the bean for the update
    public abstract ShadowBean buildUpdateBean(Connection pDBConnection, RepositoryItem pRepositoryItem)
      throws RepositoryException;
    
    //This will load the update range data
    public abstract void loadUpdateRangeData(Connection pDBConnection, String pStartId, String pEndId) throws RepositoryException, SQLException;
    
  }

  //Since a GSA Item can have the same id within differnt item descriptors the key needs more than a single
  //string.
  protected class ShadowMapKey {
    private String mId;
    private String mItemDescriptor;
    
    public String getId() {
      return mId;
    }

    public void setId(String pId) {
      mId = pId;
    }

    public String getItemDescriptor() {
      return mItemDescriptor;
    }

    public void setItemDescriptor(String pItemDescriptor) {
      mItemDescriptor = pItemDescriptor;
    }

    public ShadowMapKey(String id, String itemDescriptor) {
      super();
      mId = id;
      mItemDescriptor = itemDescriptor;
    }
    
    public int hashCode() {
      int hashCode = 0;
      
      hashCode += mId.hashCode() * 17;
      hashCode += mItemDescriptor.hashCode();
      
      return hashCode;
    }
  
    public boolean equals(Object obj) { 
        if (obj == null) 
            return false; 
        if (obj == this) 
            return true; 
        if (obj.getClass() != getClass()) 
            return false; 
  
        ShadowMapKey compareTo = (ShadowMapKey) obj; 
        if (mId.equals(compareTo.mId) && mItemDescriptor.equals(compareTo.mItemDescriptor))
          return true;
        
        return false;
    } 
  }
}

