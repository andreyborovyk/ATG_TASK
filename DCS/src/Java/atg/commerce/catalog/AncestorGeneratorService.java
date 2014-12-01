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

package atg.commerce.catalog;

import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.repository.*;
import atg.adapter.gsa.GSARepository;
import atg.dtm.*;
import javax.transaction.TransactionManager;
import java.util.*;
import atg.service.scheduler.*;
import atg.core.util.ResourceUtils;

/**
 * Available service functions:
 * AGS_GENANCESTORS  - generate ancestors (for products and skus)
 * <p>
 * The primary public method in this class, <i>performService</i>, generates
 * the set of ancestor categories for each category and product in the
 * product catalog, and updates the <i>ancestorCategories</I> property
 * for each category and product to contain the appropriate set.  This
 * set is used in hierarchical search.
 * <P>
 * Methods <i>generateCategoryAncestors</> and <i>generateProductAncestors</i>
 * can be called to update the ancestors for only categories or products, respectively.
 * <P>
 * @see SearchFormHandler
 * <P>
 * @author Lew Lasher
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/catalog/AncestorGeneratorService.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class AncestorGeneratorService
  extends CMSService
{

  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/catalog/AncestorGeneratorService.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------

  static final String MY_RESOURCE_NAME = "atg.commerce.catalog.UserResources";

  //-------------------------------------
  // Member Variables
  //-------------------------------------

  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  int mUpdateJobId = -1;

  //-------------------------------------
  // Properties
  //-------------------------------------
  public String getServiceName()
  {
    return CMSConstants.TYPE_ANCESTOR;
  }
 
  /**
   * Get property <code>jobName</code>
   *
   * @beaninfo description: The name of this scheduled job
   * @return <code>jobName</code>
   **/
  public String getJobName()
  {

    if(super.getJobName() == null)
      return ResourceUtils.getMsgResource("ancestorGeneratorJobName", MY_RESOURCE_NAME,
                                                    sResourceBundle);
    else
      return super.getJobName();
  }


  /**
   * Get property <code>jobDescription</code>
   *
   * @beaninfo description: A description of this scheduled job
   * @return <code>jobDescription</code>
   **/
  public String getJobDescription()
  {
    if(super.getJobDescription() == null)
      return ResourceUtils.getMsgResource("ancestorGeneratorJobDescription",MY_RESOURCE_NAME,
                                                    sResourceBundle);
    else
      return super.getJobDescription();
  }


  //-------------------------------------
  // property: Scheduler
  Scheduler mScheduler;

  /**
   * Sets property Scheduler
   * @beaninfo expert: true
   **/
  public void setScheduler(Scheduler pScheduler) {
    mScheduler = pScheduler;
  }

  /**
   * Returns property Scheduler
   **/
  public Scheduler getScheduler() {
    return mScheduler;
  }

  //-------------------------------------
  // property: UpdateSchedule

  /**
   * Sets property UpdateSchedule
   * @beaninfo description: the schedule on which the repository contents will be rescanned
   **/
  public void setUpdateSchedule(Schedule pUpdateSchedule) {
    setSchedule(pUpdateSchedule);
  }

  /**
   * Returns property UpdateSchedule
   **/
  public Schedule getUpdateSchedule() {
   return(getSchedule());
  }

  //-------------------------------------
  // property: ancestorCategoriesPropertyName
  String mAncestorCategoriesPropertyName;

  /**
   * Sets property ancestorCategoriesPropertyName
   **/
  public void setAncestorCategoriesPropertyName(String pAncestorCategoriesPropertyName) {
    mAncestorCategoriesPropertyName = pAncestorCategoriesPropertyName;
  }

  /**
   * Returns property ancestorCategoriesPropertyName
   **/
  public String getAncestorCategoriesPropertyName() {
    return mAncestorCategoriesPropertyName;
  }

  //-------------------------------------
  // property: childCategoriesPropertyName
  String mChildCategoriesPropertyName;

  /**
   * Sets property childCategoriesPropertyName
   **/
  public void setChildCategoriesPropertyName(String pChildCategoriesPropertyName) {
    mChildCategoriesPropertyName = pChildCategoriesPropertyName;
  }

  /**
   * Returns property childCategoriesPropertyName
   **/
  public String getChildCategoriesPropertyName() {
    return mChildCategoriesPropertyName;
  }

  //-------------------------------------
  // property: childProductsPropertyName
  String mChildProductsPropertyName;

  /**
   * Sets property childProductsPropertyName
   **/
  public void setChildProductsPropertyName(String pChildProductsPropertyName) {
    mChildProductsPropertyName = pChildProductsPropertyName;
  }


  /**
   * Returns property childProductsPropertyName
   **/
  public String getChildProductsPropertyName() {
    return mChildProductsPropertyName;
  }


  //---------------------------------------------------------------------------
  // property:IdPropertyName
  //---------------------------------------------------------------------------

  private String mIdPropertyName;
  public void setIdPropertyName(String pIdPropertyName) {
    mIdPropertyName = pIdPropertyName;
  }

  /**
   * The name of the id property for categories, products, and skus
   **/
  public String getIdPropertyName() {
    return mIdPropertyName;
  }

  //-------------------------------------
  // property: catalogTools
  CatalogTools mCatalogTools;
  /**
   * Sets property catalogTools
   **/
  public void setCatalogTools(CatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }
  /**
   * Returns property catalogTools
   **/
  public CatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  //-------------------------------------
  // property: catalogs
  String [] mCatalogs;

  /**
   * Sets property catalogs
   **/
  public void setCatalogs(String [] pCatalogs) {
    mCatalogs = pCatalogs;
  }

  /**
   * Returns property catalogs
   **/
  public String [] getCatalogs() {
    return mCatalogs;
  }

  //-------------------------------------
  // property: categoryItemName
  String mCategoryItemName;
  /**
   * Sets property categoryItemName
   **/
  public void setCategoryItemName(String pCategoryItemName) {
    mCategoryItemName = pCategoryItemName;
  }
  /**
   * Returns property categoryItemName
   **/
  public String getCategoryItemName() {
    return mCategoryItemName;
  }

  //-------------------------------------
  // property: productItemName
  String mProductItemName;
  /**
   * Sets property productItemName
   **/
  public void setProductItemName(String pProductItemName) {
    mProductItemName = pProductItemName;
  }
  /**
   * Returns property productItemName
   **/
  public String getProductItemName() {
    return mProductItemName;
  }

  //-------------------------------------
  // property: transactionManager
  TransactionManager mTransactionManager;
  /**
   * Sets property transactionManager
   **/
  public void setTransactionManager(TransactionManager pTransactionManager) {
    mTransactionManager = pTransactionManager;
  }

  /**
   * Returns property transactionManager
   **/
  public TransactionManager getTransactionManager() {
    return mTransactionManager;
  }

  //-------------------------------------
  // property: maxItemsPerTransaction
  int mMaxItemsPerTransaction = 1000;

  /**
   * Sets property maxItemsPerTransaction
   **/
  public void setMaxItemsPerTransaction(int pMaxItemsPerTransaction) {
    mMaxItemsPerTransaction = pMaxItemsPerTransaction;
  }

  /**
   * Returns property maxItemsPerTransaction
   **/
  public int getMaxItemsPerTransaction() {
    return mMaxItemsPerTransaction;
  }

  //-------------------------------------
  // property: categoryStack
  Stack mCategoryStack;

  /**
   * Sets property categoryStack
   **/
  public void setCategoryStack(Stack pCategoryStack) {
    mCategoryStack = pCategoryStack;
  }

  /**
   * Returns property useShortTransactions
   **/
  public Stack getCategoryStack() {
    return mCategoryStack;
  }

  /*
   * This private list is used to remember which ancestors have been generated
   * It is a map of category ids.  We use a map so the look up is fast
   * The values are always ignored (and set to null)
   */
  private Map mFinishedCategoryIds = new HashMap();

  //-------------------------------------
  // Instance methods
  //-------------------------------------


  /**
   * Performs this services functions
   * @param pRepository - Catalog repository to operate against
   * @param pCatalogIds - a collection of catalogs to process
   * @param pServiceFunctions - a list of strings that identify service functions. used to
   *                            conditionally executed specific functions of a service
   * @return boolean - service success or failure

  **/
  public boolean performService(Repository pRepository, Collection pCatalogIds, List pServiceFunctions)
  {
    //set the default functions if none are passed in
    List serviceFunctions = pServiceFunctions;
    String [] functionsToPerformByDefault = getFunctionsToPerformByDefault();
    if(pServiceFunctions == null)
    {
      serviceFunctions =  new java.util.ArrayList()  ;
      if(functionsToPerformByDefault != null && functionsToPerformByDefault.length > 0)
      {
        for(int i = 0; i <  functionsToPerformByDefault.length; i++)
        {
          serviceFunctions.add(functionsToPerformByDefault[i]);
        }
      }
    }
   Iterator servicerator = serviceFunctions.iterator();

   while(servicerator.hasNext())
   {
     String serviceFunction = (String) servicerator.next();

     if(serviceFunction.equals(CMSConstants.AGS_GENANCESTORS))
        generateAncestors(pRepository);
   }
    return true;

  }
  
  /**
   * Generate and update the values of the ancestorCategories property for all
   * categories and products in all specified product catalogs.
   *
   * @beaninfo hidden: false
   */

  public synchronized void generateAncestors()
  {
    generateCategoryAncestors();
    generateProductAncestors();
  }
  
  /**
   * Generate and update the values of the ancestorCategories property for all
   * categories and products in all specified product catalogs.
   *
   * @param pRepository - Catalog repository to operate against
   * @beaninfo hidden: false
   */

  public synchronized void generateAncestors(Repository pRepository)
  {
    generateCategoryAncestors(pRepository);
    generateProductAncestors(pRepository);
  }

  /**
   * Generate and update the values of the ancestorCategories property for all
   * categories in all specified product catalogs.
   * 
   */
  public synchronized void generateCategoryAncestors()
  {
    generateCategoryAncestors(null);
  }

  /**
   * Generate and update the values of the ancestorCategories property for all
   * categories in all specified product catalogs.
   * 
   * @param pRepository - Catalog repository to operate against
   */
  public synchronized void generateCategoryAncestors(Repository pRepository)
  {
    CatalogTools catalogTools = getCatalogTools();
    String [] catalogs = getCatalogs();
    if(isLoggingInfo())
    {
      String msgArgs[] = {ResourceUtils.getMsgResource("PROCESS_GEN_CAT_ANCESTORS", MY_RESOURCE_NAME, sResourceBundle), new Date(System.currentTimeMillis()).toString() };
      logInfo(ResourceUtils.getMsgResource("PROCESS_STARTED", MY_RESOURCE_NAME, sResourceBundle, msgArgs));
    }

    long startTime = System.currentTimeMillis();
    if (pRepository != null)
    {
      if (isLoggingInfo())
        logInfo("Generating category ancestors against repository: " + ((GenericService)pRepository).getName());
      generateCategoryAncestorsForCurrentCatalog(pRepository);
    }
    else 
    {
      if (catalogs == null || catalogs.length == 0)
      {
        Repository catalog = catalogTools.getCatalog();
        generateCategoryAncestorsForCurrentCatalog(catalog);
      }
      else
      {
        for (int catalogIndex=0; catalogIndex<catalogs.length; catalogIndex++)
        {
          String name = catalogs[catalogIndex];
          Repository currentCatalog = catalogTools.findCatalog(name);
          if (currentCatalog == null)
          {
            if (isLoggingError())
            {
              logError(
                ResourceUtils.getMsgResource(
                  "COULDNT_FIND_CATALOG", MY_RESOURCE_NAME, sResourceBundle)
              + name);
            }
          }
          else
          {
            generateCategoryAncestorsForCurrentCatalog(currentCatalog);
          }
        }
      }
    }

    long endTime = System.currentTimeMillis();
    long seconds = (endTime - startTime) / 1000;
    if(isLoggingInfo())
    {
      String msgArgs[] = { ResourceUtils.getMsgResource("PROCESS_GEN_CAT_ANCESTORS", MY_RESOURCE_NAME, sResourceBundle), new Date(System.currentTimeMillis()).toString() };
      logInfo(ResourceUtils.getMsgResource("PROCESS_ENDED", MY_RESOURCE_NAME, sResourceBundle, msgArgs));

      String msgArgs1[] = { ResourceUtils.getMsgResource("PROCESS_GEN_CAT_ANCESTORS", MY_RESOURCE_NAME, sResourceBundle), new Long(seconds).toString() };
      logInfo(ResourceUtils.getMsgResource("PROCESS_TIME_INSECS", MY_RESOURCE_NAME, sResourceBundle, msgArgs1));
    }


  }

  /**
   * Generate and update the values of the ancestorCategories property for all
   * products in all specified product catalogs.
   * 
   */
  public synchronized void generateProductAncestors()
  {
    generateProductAncestors(null);
  }

  /**
   * Generate and update the values of the ancestorCategories property for all
   * products in all specified product catalogs.
   * 
   * @param pRepository - Catalog repository to operate against
   */
  public synchronized void generateProductAncestors(Repository pRepository)
  {
    CatalogTools catalogTools = getCatalogTools();
    String [] catalogs = getCatalogs();
    if(isLoggingInfo())
    {
      String msgArgs[] = {ResourceUtils.getMsgResource("PROCESS_GEN_PRD_ANCESTORS", MY_RESOURCE_NAME, sResourceBundle), new Date(System.currentTimeMillis()).toString() };
      logInfo(ResourceUtils.getMsgResource("PROCESS_STARTED", MY_RESOURCE_NAME, sResourceBundle, msgArgs));
    }

    long startTime = System.currentTimeMillis();
    if (pRepository != null)
    {
      if (isLoggingInfo())
        logInfo("Generating product ancestors against repository: " + ((GenericService)pRepository).getName());
      generateProductAncestorsForCurrentCatalog(pRepository);
    }
    else
    {
      if (catalogs == null || catalogs.length == 0)
      {
        Repository catalog = catalogTools.getCatalog();
        generateProductAncestorsForCurrentCatalog(catalog);
      }
      else
      {
        for (int catalogIndex=0; catalogIndex<catalogs.length; catalogIndex++)
        {
          String name = catalogs[catalogIndex];
          Repository currentCatalog = catalogTools.findCatalog(name);
          if (currentCatalog == null)
          {
            if (isLoggingError())
            {
              logError(
                ResourceUtils.getMsgResource(
                  "COULDNT_FIND_CATALOG", MY_RESOURCE_NAME, sResourceBundle)
              + name);
            }
          }
          else
          {
            generateProductAncestorsForCurrentCatalog(currentCatalog);
          }
        }
      }
    }

    long endTime = System.currentTimeMillis();
    long seconds = (endTime - startTime) / 1000;
    if(isLoggingInfo())
    {
      String msgArgs[] = { ResourceUtils.getMsgResource("PROCESS_GEN_PRD_ANCESTORS", MY_RESOURCE_NAME, sResourceBundle), new Date(System.currentTimeMillis()).toString() };
      logInfo(ResourceUtils.getMsgResource("PROCESS_ENDED", MY_RESOURCE_NAME, sResourceBundle, msgArgs));

      String msgArgs1[] = { ResourceUtils.getMsgResource("PROCESS_GEN_PRD_ANCESTORS", MY_RESOURCE_NAME, sResourceBundle), new Long(seconds).toString() };
      logInfo(ResourceUtils.getMsgResource("PROCESS_TIME_INSECS", MY_RESOURCE_NAME, sResourceBundle, msgArgs1));
    }



  }

  /**
   * Generate and update the values of the ancestorCategories property for all
   * categories in the current product catalog.
   * @param pCatalog current catalog to operate on
   */

  protected void generateCategoryAncestorsForCurrentCatalog(Repository pCatalog)
  {
    TransactionDemarcation td = new TransactionDemarcation();

    try
    {
      td.begin(getTransactionManager(), td.REQUIRED);

      // Iterate over all the categories in the catalog

      RepositoryView categoryView = getCategoryRepositoryView(pCatalog);
      QueryBuilder queryBuilder = categoryView.getQueryBuilder();
      Query getAllQuery = queryBuilder.createUnconstrainedQuery();

      // since we do this in batches, we need to order the results
      String sortProp = getIdPropertyName();
      SortDirective sd = new SortDirective(sortProp, SortDirective.DIR_ASCENDING);
      SortDirectives sds = new SortDirectives();
      sds.addDirective(sd);

      int numCategories = categoryView.executeCountQuery(getAllQuery);

      td.end();

      int startIndex = 0;

      /**
       * To keep things manageable for large catalogs, handle the
       * iterations in groups of <i>maxItemsPerTransaction</I>
      **/

      while (startIndex < numCategories)
      {
        int endIndex = startIndex + getMaxItemsPerTransaction();
        if (endIndex > numCategories)
          endIndex = numCategories;

        td.begin(getTransactionManager(), td.REQUIRED);
        RepositoryItem [] categories =
          categoryView.executeQuery(getAllQuery, startIndex, endIndex, sds);
        td.end();

        td.begin(getTransactionManager(), td.REQUIRED);

        /**
         * For each category, find its ancestors, and make the
         * resulting set the value of its <i>ancestorCategories</i> property
         **/

        if (categories != null)
        {
          // reset the list of finished ids
          mFinishedCategoryIds.clear();
          for (int catIndex=0; catIndex<categories.length; catIndex++)
          {
            RepositoryItem thisCategory = categories[catIndex];
            Set ancestors = topLevelGenerateAncestorsForCategory(thisCategory, pCatalog);
          } // End for()
          mFinishedCategoryIds.clear();
        } // End if (categories != null)

        td.end();

        startIndex = endIndex;
      }         // End while (startIndex < numCategories)
    }           // End try {}

    catch (TransactionDemarcationException exc)
    {
      if (isLoggingError())
      {
        logError(exc);
      }
    }
    catch (RepositoryException exc)
    {
      if (isLoggingError())
      {
        logError(exc);
      }
    }
    finally
    {
      try
      {
        td.end();
      }
      catch (TransactionDemarcationException exc)
      {
        if (isLoggingError())
        {
          logError(exc);
        }
      }
    }
  }

  /**
   * Generate and update the values of the ancestorCategories property for all
   * products in the current product catalog.
   * @param pCatalog current catalog to operate on
   */

  protected void generateProductAncestorsForCurrentCatalog(Repository pCatalog)
  {
    TransactionDemarcation td = new TransactionDemarcation();

    try
    {
      td.begin(getTransactionManager(), td.REQUIRED);

      // Iterate over all the products in the catalog

      RepositoryView productView = getProductRepositoryView(pCatalog);
      QueryBuilder queryBuilder = productView.getQueryBuilder();
      Query getAllQuery = queryBuilder.createUnconstrainedQuery();

      // since we do this in batches, we need to order the results
      String sortProp = getIdPropertyName();
      SortDirective sd = new SortDirective(sortProp, SortDirective.DIR_ASCENDING);
      SortDirectives sds = new SortDirectives();
      sds.addDirective(sd);

      int numProducts = productView.executeCountQuery(getAllQuery);

      td.end();

      int startIndex = 0;

      /**
       * To keep things manageable for large catalogs, handle the
       * iterations in groups of <i>maxItemsPerTransaction</I>
      **/

      while (startIndex < numProducts)
      {
        int endIndex = startIndex + getMaxItemsPerTransaction();
        if (endIndex > numProducts)
          endIndex = numProducts;

        td.begin(getTransactionManager(), td.REQUIRED);
        RepositoryItem [] products =
          productView.executeQuery(getAllQuery, startIndex, endIndex, sds);
        td.end();

        td.begin(getTransactionManager(), td.REQUIRED);

        /**
         * For each product, find its parent categories, take the union of all
         * those categories' ancestor sets, and also include the parent categories
         * themselves, and make the resulting set the value of that product's
         * <i>ancestorCategories</i> property
         **/

        if (products != null)
        {
          for (int prodIndex=0; prodIndex<products.length; prodIndex++)
          {
            RepositoryItem thisProduct = products[prodIndex];
            updateAncestorsForProduct(thisProduct, pCatalog);
          }     // End for()
        }       // End if products != null

        td.end();

        startIndex = endIndex;
      }         // End while (startIndex < numProducts)
    }           // End try {}

    catch (TransactionDemarcationException exc)
    {
      if (isLoggingError())
      {
        logError(exc);
      }
    }
    catch (RepositoryException exc)
    {
      if (isLoggingError())
      {
        logError(exc);
      }
    }
    finally
    {
      try
      {
        td.end();
      }
      catch (TransactionDemarcationException exc)
      {
        if (isLoggingError())
        {
          logError(exc);
        }
      }
    }
  }

  /**
   * Generate the set of ancestor categories for a given product and set its
   * <i>ancestorCategories</i> property to that set
   * <P>
   * N.B. This assumes that all categories already have their <i>ancestorCategories</I>
   * property set correctly.
   * @param pProduct the product whose <i>ancestorCategories</I> property should be set
   * @param pCatalog current catalog to operate on
   **/

  protected void updateAncestorsForProduct(RepositoryItem pProduct, Repository pCatalog)
    throws RepositoryException
  {
      /**
       * Initialize the set of ancestors for this product
       **/

      Collection newAncestors = generateEmptySet();

      /**
       * Find the product's immediate parent categories by querying for all
       * categories whose children include that product
       **/

      RepositoryView categoryView = getCategoryRepositoryView(pCatalog);
      QueryBuilder queryBuilder = categoryView.getQueryBuilder();

      QueryExpression propertyExpression =
        queryBuilder.createPropertyQueryExpression(getChildProductsPropertyName());
      QueryExpression thisProductExpression =
        queryBuilder.createConstantQueryExpression(pProduct.getRepositoryId());
      Query includesQuery =
        queryBuilder.createIncludesQuery(propertyExpression, thisProductExpression);

      RepositoryItem [] parentCategories = categoryView.executeQuery(includesQuery);

      if (parentCategories != null)
      {
        /**
         * For each immediate parent category, add (a) that category, and
         * (b) all the ancestors for that category, to the accumulating set
         * of ancestors for this product
         **/

        for (int parentIndex=0; parentIndex<parentCategories.length; parentIndex ++)
        {
          RepositoryItem thisParent = parentCategories[parentIndex];

          newAncestors.add(thisParent);

          Collection thisParentsAncestorCategories =
            (Collection)
            thisParent.getPropertyValue(getAncestorCategoriesPropertyName());

          if (thisParentsAncestorCategories != null)
          {
            newAncestors.addAll(thisParentsAncestorCategories);
          }
        }
      }

      /**
       * Set this product's <i>ancestorCategories</I> property to the accumulated set
       * of categories
       **/

      setAncestorsForProduct(pProduct, newAncestors, pCatalog);
  }

  /**
   * Helper method to return an empty set.  This method could be rewritten to make it
   * easy to modify this class if we decided that the <i>ancestorCategories</I>
   * property should be implemented as a List instead of a Set
   * @returns a newly-constructed Set with nothing in it
   **/

  protected Set generateEmptySet()
  {
    return new HashSet();
  }

  /**
   * Helper method to set the <i>ancestorCategories</I> property for a category
   * @param pCategory the category whose </I>ancestorCategories</I> property should be set
   * @param pAncestors what to set it to
   * @param pCatalog current catalog to operate on
   **/

  protected void setAncestorsForCategory(RepositoryItem pCategory, Collection pAncestors, Repository pCatalog)
    throws RepositoryException
  {
    final Collection currentAncestors = ( Collection )pCategory.getPropertyValue( this.getAncestorCategoriesPropertyName() );

    if ( !currentAncestors.equals( pAncestors ) ){
      MutableRepository repository = (MutableRepository) pCatalog;
      MutableRepositoryItem mutableCategory =
        repository.getItemForUpdate(
          pCategory.getRepositoryId(), getCategoryItemName());
      mutableCategory.setPropertyValue(getAncestorCategoriesPropertyName(), pAncestors);
      repository.updateItem(mutableCategory);
    }
  }

  /**
   * Helper method to set the <i>ancestorCategories</I> property for a product
   * @param pProduct the product whose </I>ancestorCategories</I> property should be set
   * @param pAncestors what to set it to
   * @param pCatalog current catalog to operate on
   **/

  protected void setAncestorsForProduct(RepositoryItem pProduct, Collection pAncestors, Repository pCatalog)
    throws RepositoryException
  {
    final Collection currentAncestors =	 ( Collection )pProduct.getPropertyValue( this.getAncestorCategoriesPropertyName() );
    
    if ( !currentAncestors.equals( pAncestors ) ){      
      MutableRepository repository = (MutableRepository) pCatalog;
      MutableRepositoryItem mutableProduct =
        repository.getItemForUpdate(
          pProduct.getRepositoryId(), getProductItemName());
      mutableProduct.setPropertyValue(getAncestorCategoriesPropertyName(), pAncestors);
      repository.updateItem(mutableProduct);
    }
  }

  /**
   * Generate the set of all ancestor categories for a given category, using the
   * recursive method <B>generateAncestorsForCategory</B>
   * <P>
   * Also detect cycles of parentage
   * @param pCategory the category whose ancestors should be found
   * @param pCatalog current catalog to operate on
   * @return a Set of all the ancestor categories
   * @see generateAncestorsForCategory
   **/
  protected Set topLevelGenerateAncestorsForCategory(RepositoryItem pCategory, Repository pCatalog)
    throws RepositoryException
  {
    setCategoryStack(new Stack());
    return generateAncestorsForCategory(pCategory, pCatalog);
  }

  /**
   * Recursive subroutine for topLevelGenerateAncestorsForCategory
   * @param pCategory the category whose ancestors should be found
   * @param pCatalog current catalog to operate on
   * @return a Set of all the ancestor categories
   * @see topLevelGenerateAncestorsForCategory
   **/

  protected Set generateAncestorsForCategory(RepositoryItem pCategory, Repository pCatalog)
    throws RepositoryException
  {
    /**
     * Initialize the set of ancestors to be none.
     **/
    Set result = generateEmptySet();

    if(isLoggingDebug())
      logDebug("Generate ancestors for : " + pCategory.getRepositoryId());

    // have the ancestors for this category been calculated yet?
    if(mFinishedCategoryIds.containsKey(pCategory.getRepositoryId())) {
      if(isLoggingDebug())
        logDebug("We are done: " + pCategory.getRepositoryId());
      result = (Set) pCategory.getPropertyValue(getAncestorCategoriesPropertyName());
      return result;
    }

    /**
     * Detect cycles of parentage that would cause infinite recursion, by looking on the
     * stack to see whether we have already started looking for ancestors for this
     * category.
     **/

    Stack categoryStack = getCategoryStack();
    if (categoryStack.contains(pCategory))
    {
      return result;
    }

    // Add the current category to the stack
    categoryStack.push(pCategory);

    /**
     * Find the category's immediate parent categories by querying for all
     * categories whose children include this category
     **/

    RepositoryView categoryView = getCategoryRepositoryView(pCatalog);
    QueryBuilder queryBuilder = categoryView.getQueryBuilder();

    QueryExpression propertyExpression =
      queryBuilder.createPropertyQueryExpression(getChildCategoriesPropertyName());
    QueryExpression thisCategoryExpression =
      queryBuilder.createConstantQueryExpression(pCategory.getRepositoryId());
    Query includesQuery =
      queryBuilder.createIncludesQuery(propertyExpression, thisCategoryExpression);

    RepositoryItem [] parentCategories = categoryView.executeQuery(includesQuery);

    /**
     * Add these parents to the accumulating set of ancestors, and
     * recurse on each parent, to add the ancestors of each parent, also,
     * to the accumulating set.
     **/

    if (parentCategories != null)
    {
      for (int parentIndex=0; parentIndex<parentCategories.length; parentIndex++)
      {
        RepositoryItem thisParent = parentCategories[parentIndex];
        result.add(thisParent);
        Set thisParentsAncestors = generateAncestorsForCategory(thisParent, pCatalog);
        result.addAll(thisParentsAncestors);
      }
    }

    // Restore the category stack
    categoryStack.pop();

    // this category is finished... this is useful info
    //finished.put(pCategory.getRepositoryId(), null);
    mFinishedCategoryIds.put(pCategory.getRepositoryId(), null);
    setAncestorsForCategory(pCategory, result, pCatalog);
    /**
     * Return the accumulated set of ancestor categories.
     **/
    return result;
  }

  /**
   * Helper method to get the RepositoryView for finding category items
   * @param pCatalog current catalog to operate on
   * @return the RepositoryView
   **/

  protected RepositoryView getCategoryRepositoryView(Repository pCatalog)
    throws RepositoryException
  {
      RepositoryView view = pCatalog.getView(getCategoryItemName());
      return view;
  }

  /**
   * Helper method to get the RepositoryView for finding product items
   * @param pCatalog current catalog to operate on
   * @return the RepositoryView
   **/

  protected RepositoryView getProductRepositoryView(Repository pCatalog)
    throws RepositoryException
  {
      RepositoryView view = pCatalog.getView(getProductItemName());
      return view;
  }

}
