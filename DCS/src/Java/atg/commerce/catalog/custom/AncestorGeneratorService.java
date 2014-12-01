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

import atg.nucleus.ServiceException;
import atg.repository.*;
import atg.repository.dp.DerivedPropertyDescriptor;
import atg.adapter.gsa.GSAId;
import atg.adapter.gsa.GSAItemDescriptor;
import atg.adapter.gsa.GSAPropertyDescriptor;
import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.SQLQuery;
import atg.adapter.gsa.SQLStatement;
import atg.adapter.gsa.query.Clause;
import atg.commerce.catalog.CMSConstants;
import atg.commerce.catalog.custom.TreeWalkUpdateService.ShadowMapKey;
import atg.core.jdbc.ResultSetGetter;
import atg.core.util.ResourceUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * This CMSService performs the following service functions
 *
 * Available service functions:
 * AGS_GENPROPERTIES - generate catalog, catalogs, and AncestorCategories (for catalogs, products, skus)
 * AGS_GENCATALOGS   - generate catalog and catalogs (for catalogs, products and skus)
 * AGS_GENPARENTCATS - generate parent categories
 * AGS_GENANCESTORS  - generate ancestors (for products and skus)
 * AGS_GENPROPERTIES_FOR_CATEGORY - only generate category and above
 * AGS_GENPROPERTIES_FOR_PRODUCT - only generate product and above
 *
 * @see this.getAvailableFunctions
 * @see this.performService
 *
 * <i>generateAncestors</i>, generates the set of ancestor categories for each
 * category and product in each catalog
 * in the catalog repository, and updates the <i>ancestorCategories</i> property.
 * <P>
 *
 * Methods <i>generateCategoryAncestors</i> and <i>generateProductAncestors</i>
 * can be called to update the ancestors for only categories or products, respectively.
 * <P>
 *
 * <i>generateCatalogAndCatalogs</i>, generates the catalog and catalogs properties
 * for all categories, products and skus in each catalog
 *
 * <i>generateCatalogParentCategoriesForProducts</i>, generates the parent category
 * at the catalog level. This is necessary when more than one category in a catalog
 * has the same product as a childProduct.  
 * <P>
 * Methods <i>generateCatalogsForProducts</i> , <i>generateCatalogsForSkus</i>
 * and <i>generateCatalogAndCatalogsForCategories</i>
 * can be called to update the catalogs and catalogs properties
 * for only categories, products or skus, respectively.
 * <P>
 * This set is used in hierarchical search.
 * You may limit this to specific catalogs by provide a Collection of catalog ids.
 * @see atg.commerce.catalog.SearchFormHandler
 * <P>
 *
 * @author Jon Turgeon
 * @beaninfo
 *   description: A service used to generate the ancestor categories, catalog and catalogs
 *                properties for each category, product and sku in the product catalog
 *   displayname: AncestorGeneratorService
 *   attribute: functionalComponentCategory Catalog Maintenance
 *   attribute: featureComponentCategory
 * @version $Change: 651448 $$DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class AncestorGeneratorService 
  extends TreeWalkUpdateService 
{
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/catalog/custom/AncestorGeneratorService.java#2 $$Change: 651448 $";

  public String getServiceName()
  {
    return CMSConstants.TYPE_ANCESTOR;
  }
  //-------------------------------------
  // Constants
  //-------------------------------------
  private static final String PERFORM_MONITOR_NAME="AncestorGeneratorService";
  
  public String getNameToLog() {
    return "AncestorGeneratorService";
  }
  
  //-------------------------------------
  // property: UpdateSitesProperty
  boolean mUpdateSitesProperty = true;
  
  /**
   * Returns the value of the UpdateSitesProperty property
   * @beaninfo description: True if the sites property should be calculated.. 
   */
  public boolean isUpdateSitesProperty() {
    return mUpdateSitesProperty;
  }
  
  /**
   * Sets property UpdateSitesProperty
   */
  public void setUpdateSitesProperty(boolean pUpdateSitesProperty) {
    mUpdateSitesProperty = pUpdateSitesProperty;
  }
  
  //-------------------------------------
  // property: UpdateCatalogsProperty
  boolean mUpdateCatalogsProperty = true;
  
  /**
   * Returns the value of the UpdateCatalogsProperty property
   * @beaninfo description: True if the catalogs property should be calculated.. 
   */
  public boolean isUpdateCatalogsProperty() {
    return mUpdateCatalogsProperty;
  }
  
  /**
   * Sets property UpdateCatalogsProperty
   */
  public void setUpdateCatalogsProperty(boolean pUpdateCatalogsProperty) {
    mUpdateCatalogsProperty = pUpdateCatalogsProperty;
  }

  //default to 1
  private int mNumberOfConcurrentUpdateThreads = 1;
  
  public int getNumberOfConcurrentUpdateThreads() {
    return mNumberOfConcurrentUpdateThreads;
  }

  public void setNumberOfConcurrentUpdateThreads(int pNumberOfConcurrentUpdateThreads) {
    mNumberOfConcurrentUpdateThreads = pNumberOfConcurrentUpdateThreads;
  }

  //default to 1
  private int mNumberOfHoursToTimeOutThreads = 1;
  
  public int getNumberOfHoursToTimeOutThreads() {
    return mNumberOfHoursToTimeOutThreads;
  }

  public void setNumberOfHoursToTimeOutThreads(int pNumberOfHoursToTimeOutThreads) {
    mNumberOfHoursToTimeOutThreads = pNumberOfHoursToTimeOutThreads;
  }

  private ExecutorService mProductExecutor;

  protected void setProductExecutor(ExecutorService pExecutorService) {
    mProductExecutor = pExecutorService;
  }
  
  protected ExecutorService getProductExecutor() {
    return mProductExecutor;
  }
  
  private ExecutorService mSkuExecutor;

  protected void setSkuExecutor(ExecutorService pExecutorService) {
    mSkuExecutor = pExecutorService;
  }
  
  protected ExecutorService getSkuExecutor() {
    return mSkuExecutor;
  }
  
  private boolean mShutDownInProcess = false;
  
  protected void setShutDownInProcess(boolean pShutDownInProcess) {
    mShutDownInProcess = pShutDownInProcess;
  }
  
  protected boolean isShutDownInProcess() {
    return mShutDownInProcess;
  }

  private String mDeleteProductCatalogsCheck;
  
  public void setProductDeleteCatalogsCheck(String pDeleteProductCatalogsCheck) {
    mDeleteProductCatalogsCheck = pDeleteProductCatalogsCheck;
  }

  private GSATableAndColumnNames mTableAndColumnNames;

  protected void setTableAndColumnNames(GSATableAndColumnNames pTableAndColumnNames) {
    mTableAndColumnNames = pTableAndColumnNames;
  }
  
  protected GSATableAndColumnNames getTableAndColumnNames() {
    return mTableAndColumnNames;
  }
  
  /**
   * This method returns the sql that compares the category level 
   * catalogs with their child products. If the product has eny extra
   * catalogs then they are flagged for delete further down
   * in the code
   * <p>
   * 
   * @return the sql to check if any product catalogs need to be removed
   * @throws RepositoryException 
   */
  public String getDeleteProductCatalogsCheck() throws RepositoryException {
    //If it has been over-riden or previousely set, then just return it
    if (mDeleteProductCatalogsCheck!=null)
      return mDeleteProductCatalogsCheck;

    //Build the replacement array
    Object[] arguments = {
        getTableAndColumnNames().getProductComputedCatalogsTableName(),
        getTableAndColumnNames().getProductIdColName(),
        getTableAndColumnNames().getProductComputedCatalogIdColName(),
        getTableAndColumnNames().getParentCategoriesTableName(),
        getTableAndColumnNames().getParentCategoriesIdColName(),
        getTableAndColumnNames().getCategoryIdColName(),
        getTableAndColumnNames().getCategoryComputedCatalogsTableName()
    };
    
    //Now do the replacement
    mDeleteProductCatalogsCheck = MessageFormat.format(
        "select computedCatalogs.{1} from {0} computedCatalogs " +
        "where computedCatalogs.{1} >= ? " +
        "  and computedCatalogs.{1} <= ? " +
        "  and not exists (select 1 " +
        "                  from  {3} parentCategories, {6} prntCatCmpCats " +
        "                  where parentCategories.{5} = prntCatCmpCats.{5} " +
        "                    and parentCategories.{4} = computedCatalogs.{1} " +
        "                    and prntCatCmpCats.{2} = computedCatalogs.{2})",
        arguments);

    return mDeleteProductCatalogsCheck;
  }
  
  private String mDeleteCatalogsCheck;
  
  public void setDeleteCatalogsCheck(String pDeleteCatalogsCheck) {
    mDeleteCatalogsCheck = pDeleteCatalogsCheck;
  }

  /**
   * This method returns the sql that compares the product level 
   * catalogs with their child skus. If the sku has eny extra
   * catalogs then they are flagged for delete further down
   * in the code
   * <p>
   * 
   * @return the sql to check if any sku catalogs need to be removed
   * @throws RepositoryException 
   */
  public String getDeleteCatalogsCheck() throws RepositoryException {
    //If it has been over-riden or previousely set, then just return it
    if (mDeleteCatalogsCheck!=null)
      return mDeleteCatalogsCheck;

    //Now we get a bit tricky again, if the database is Oracle then use temp tables
    //this is useful when there is large data volumes but should not hurt when
    //data volumes are small.
    String parentProductsTableName = getTableAndColumnNames().getParentProductsTableName();
    String productComputedCatalogsTableName = getTableAndColumnNames().getProductComputedCatalogsTableName();
    if (getDatabaseProductName().equalsIgnoreCase("oracle")) {
      parentProductsTableName = "DCS_PRD_CHLDSKU_TEMP";
      productComputedCatalogsTableName = "DCS_PRD_CATALOGS_TEMP";
    }   

    //Build the replacement array
    Object[] arguments = {
        getTableAndColumnNames().getSkuComputedCatalogsTableName(),
        getTableAndColumnNames().getSkuIdColName(),
        getTableAndColumnNames().getSkuComputedCatalogsIdColName(),
        parentProductsTableName,
        getTableAndColumnNames().getParentProductsIdColName(),
        getTableAndColumnNames().getProductIdColName(),
        productComputedCatalogsTableName
    };
    
    //Now do the replacement
    mDeleteCatalogsCheck = MessageFormat.format(
        "select computedCatalogs.{1} from {0} computedCatalogs " +
        "where computedCatalogs.{1} >= ? " +
        "  and computedCatalogs.{1} <= ? " +
        "  and not exists (select 1 " +
        "                  from  {3} parentProducts, {6} prntCatCmpCats " +
        "                  where parentProducts.{4} = prntCatCmpCats.{5} " +
        "                    and parentProducts.{1} = computedCatalogs.{1} " +
        "                    and prntCatCmpCats.{2} = computedCatalogs.{2})",
        arguments);

    return mDeleteCatalogsCheck;
  }

  private String mInsertProductCatalogsCheck;
  
  public void setInsertProductCatalogsCheck(String pInsertProductCatalogsCheck) {
    mInsertProductCatalogsCheck = pInsertProductCatalogsCheck;
  }

  /**
   * This method returs the sql that compares the category level
   * catalogs with their child products. If the product is mising any
   * catalogs then they are flagged for addition further down
   * in the code.
   * <p>
   * 
   * @return the sql used to determine if any catalogs need to be added to the sku
   * @throws RepositoryException 
   */
  public String getInsertProductCatalogsCheck() throws RepositoryException {
    //If it has been over-riden or previousely set, then just return it
    if (mInsertProductCatalogsCheck!=null)
      return mInsertProductCatalogsCheck;
    
    //Build the replacement array
    Object[] arguments = {
        getTableAndColumnNames().getProductComputedCatalogsTableName(),
        getTableAndColumnNames().getProductIdColName(),
        getTableAndColumnNames().getProductComputedCatalogIdColName(),
        getTableAndColumnNames().getParentCategoriesTableName(),
        getTableAndColumnNames().getParentCategoriesIdColName(),
        getTableAndColumnNames().getCategoryIdColName(),
        getTableAndColumnNames().getCategoryComputedCatalogsTableName()
    };
    
    //Now do the replacement
    mInsertProductCatalogsCheck = MessageFormat.format(
        "select prdChldProduct.{4} " +
        "from  {3} prdChldProduct, {6} prdCat " +
        "where prdChldProduct.{5} = prdCat.{5} " +
        "  and prdChldProduct.{4} >= ? and prdChldProduct.{4} <= ? " +
        "  and not exists (select 1 from {0} productCatalogs " +
        "                  where productCatalogs.{2} = prdCat.{2} "+ 
        "                  and   productCatalogs.{1} = prdChldProduct.{4}" +
        "                  and   productCatalogs.{1} >= ? " +
        "                  and   productCatalogs.{1} <= ? )",
        arguments);
    
    return mInsertProductCatalogsCheck;
  }
  
  private String mInsertCatalogsCheck;
  
  public void setInsertCatalogsCheck(String pInsertCatalogsCheck) {
    mInsertCatalogsCheck = pInsertCatalogsCheck;
  }  
 
  /**
   * This method returs the sql that compares the product level
   * catalogs with their child skus. If the sku is mising any
   * catalogs then they are flagged for addition further down
   * in the code.
   * <p>
   * 
   * @return the sql used to determine if any catalogs need to be added to the sku
   * @throws RepositoryException 
   */
  public String getInsertCatalogsCheck() throws RepositoryException {
    //If it has been over-riden or previousely set, then just return it
    if (mInsertCatalogsCheck!=null)
      return mInsertCatalogsCheck;
    
    //Now we get a bit tricky again, if the database is Oracle then use temp tables
    //this is useful when there is large data volumes but should not hurt when
    //data volumes are small.
    String parentProductsTableName = getTableAndColumnNames().getParentProductsTableName();
    String productComputedCatalogsTableName = getTableAndColumnNames().getProductComputedCatalogsTableName();
    if (getDatabaseProductName().equalsIgnoreCase("oracle")) {
      parentProductsTableName = "DCS_PRD_CHLDSKU_TEMP";
      productComputedCatalogsTableName = "DCS_PRD_CATALOGS_TEMP";
    }  

    //Build the replacement array
    Object[] arguments = {
        getTableAndColumnNames().getSkuComputedCatalogsTableName(),
        getTableAndColumnNames().getSkuIdColName(),
        getTableAndColumnNames().getSkuComputedCatalogsIdColName(),
        parentProductsTableName,
        getTableAndColumnNames().getParentProductsIdColName(),
        getTableAndColumnNames().getProductIdColName(),
        productComputedCatalogsTableName
    };
    
    //Now do the replacement
    mInsertCatalogsCheck = MessageFormat.format(
        "select prdChldSku.{1} " +
        "from  {3} prdChldSku, {6} prdCat " +
        "where prdChldSku.{4} = prdCat.{5} " +
        "  and prdChldSku.{1} >= ? and prdChldSku.{1} <= ? " +
        "  and not exists (select 1 from {0} skuCatalogs " +
        "                  where skuCatalogs.{2} = prdCat.{2} "+ 
        "                  and   skuCatalogs.{1} = prdChldSku.{1}" +
        "                  and   skuCatalogs.{1} >= ? " +
        "                  and skuCatalogs.{1} <= ?)",
        arguments);
    
    return mInsertCatalogsCheck;
  }

  private String mDeleteProductSiteCheck;
  
  public void setDeleteProductSiteCheck(String pDeleteProductSiteCheck) {
    mDeleteProductSiteCheck = pDeleteProductSiteCheck;
  }
  
  /**
   * This method returns the sql that compares the category level 
   * sites with their child product. If the product has eny extra
   * sites then they are flagged for delete further down
   * in the code
   * <p>
   *
   * @return the sql used to check and see if any sites need to be removed from a group of skus
   * @throws RepositoryException 
   */
  public String getDeleteProductSiteCheck() throws RepositoryException {
    //If it has been over-riden or previousely set, then just return it
    if (mDeleteProductSiteCheck!=null)
      return mDeleteProductSiteCheck;
    
    //Build the replacement array
    Object[] arguments = {
        getTableAndColumnNames().getProductSitesTableName(),
        getTableAndColumnNames().getProductIdColName(),
        getTableAndColumnNames().getProductSiteIdColName(),
        getTableAndColumnNames().getParentCategoriesTableName(),
        getTableAndColumnNames().getParentCategoriesIdColName(),
        getTableAndColumnNames().getCategoryIdColName(),
        getTableAndColumnNames().getCategorySitesTableName()
    };
    
    //Now do the replacement
    mDeleteProductSiteCheck = MessageFormat.format(
        "select productSites.{1} from {0} productSites " +
        "where  productSites.{1} >= ? and productSites.{1} <= ? " +
        " and not exists (select 1 " +
        "                 from  {3} prdChldProduct, " + 
        "                       {6} categorySites " +
        "                 where prdChldProduct.{5} = categorySites.{5} " +
        "                   and productSites.{2} = categorySites.site_id " +
        "                   and productSites.{1} = prdChldProduct.{4})",
        arguments);
    
    return mDeleteProductSiteCheck;    
  }
  
  private String mDeleteSiteCheck;
  
  public void setDeleteSiteCheck(String pDeleteSiteCheck) {
    mDeleteSiteCheck = pDeleteSiteCheck;
  }

  /**
   * This method returns the sql that compares the product level 
   * sites with their child skus. If the sku has eny extra
   * sites then they are flagged for delete further down
   * in the code
   * <p>
   *
   * @return the sql used to check and see if any sites need to be removed from a group of skus
   * @throws RepositoryException 
   */
  public String getDeleteSiteCheck() throws RepositoryException {
    //If it has been over-riden or previousely set, then just return it
    if (mDeleteSiteCheck!=null)
      return mDeleteSiteCheck;
    
    //Now we get a bit tricky again, if the database is Oracle then use temp tables
    //this is useful when there is large data volumes but should not hurt when
    //data volumes are small.
    String parentProductsTableName = getTableAndColumnNames().getParentProductsTableName();
    String productSitesTableName = getTableAndColumnNames().getProductSitesTableName();
    if (getDatabaseProductName().equalsIgnoreCase("oracle")) {
      parentProductsTableName = "DCS_PRD_CHLDSKU_TEMP";
      productSitesTableName = "DCS_PRODUCT_SITES_TEMP";
    }  
    
    //Build the replacement array
    Object[] arguments = {
        getTableAndColumnNames().getSkuSitesTableName(),
        getTableAndColumnNames().getSkuIdColName(),
        getTableAndColumnNames().getSkuSiteIdColName(),
        parentProductsTableName,
        getTableAndColumnNames().getParentProductsIdColName(),
        getTableAndColumnNames().getProductIdColName(),
        productSitesTableName
    };
    
    //Now do the replacement
    mDeleteSiteCheck = MessageFormat.format(
        "select skuSites.{1} from {0} skuSites " +
        "where  skuSites.{1} >= ? and skuSites.{1} <= ? " +
        " and not exists (select 1 " +
        "                 from  {3} prdChldSku, " + 
        "                       {6} prdSites " +
        "                 where prdChldSku.{5} = prdSites.{5} " +
        "                   and skuSites.{2} = prdSites.site_id " +
        "                   and skuSites.{1} = prdChldSku.{1})",
        arguments);
    
    return mDeleteSiteCheck;    
  }


  private String mInsertProductSiteCheck;
  
  public void setInsertProductSiteCheck(String pInsertProductSiteCheck) {
    mInsertProductSiteCheck = pInsertProductSiteCheck;
  }

  /**
   * This method returns the sql that compares the category level
   * sites with their child products. If the product is mising any
   * sites then they are flagged for addition further down
   * in the code.
   * <p>
   * 
   * 
   * @return the sql used to check and see if any sites need to be added within a group of skus
   * @throws RepositoryException 
   */
  public String getInsertProductSiteCheck() throws RepositoryException {
    //If it has been over-riden or previousely set, then just return it
    if (mInsertProductSiteCheck!=null)
      return mInsertProductSiteCheck;

    //Build the replacement array
    Object[] arguments = {
        getTableAndColumnNames().getProductSitesTableName(),
        getTableAndColumnNames().getProductIdColName(),
        getTableAndColumnNames().getProductSiteIdColName(),
        getTableAndColumnNames().getParentCategoriesTableName(),
        getTableAndColumnNames().getParentCategoriesIdColName(),
        getTableAndColumnNames().getCategoryIdColName(),
        getTableAndColumnNames().getCategorySitesTableName()
    };
    
    //Now do the replacement
    mInsertProductSiteCheck = MessageFormat.format(
        "select prdChldproduct.{4} " +
        "from  {3} prdChldproduct, " + 
        "      {6} categorySites " +
        "where prdChldproduct.{5} = categorySites.{5} " +
        "  and prdChldproduct.{4} >= ? and prdChldproduct.{4} <= ? " +
        "  and not exists (select 1 from {0} productSites " + 
        "                where productSites.{2} = categorySites.{2} " +
        "                and   productSites.{1} = prdChldproduct.{4}" +
        "                and   productSites.{1} >= ? " +
        "                and   productSites.{1} <= ? )",
        arguments);
    
    return mInsertProductSiteCheck; 
  }
  
  private String mInsertSiteCheck;
  
  public void setInsertSiteCheck(String pInsertSiteCheck) {
    mInsertSiteCheck = pInsertSiteCheck;
  }

  /**
   * This method returs the sql that compares the product level
   * sites with their child skus. If the sku is mising any
   * sites then they are flagged for addition further down
   * in the code.
   * <p>
   * 
   * 
   * @return the sql used to check and see if any sites need to be added within a group of skus
   * @throws RepositoryException 
   */
  public String getInsertSiteCheck() throws RepositoryException {
    //If it has been over-riden or previousely set, then just return it
    if (mInsertSiteCheck!=null)
      return mInsertSiteCheck;
    
    //Now we get a bit tricky again, if the database is Oracle then use temp tables
    //this is useful when there is large data volumes but should not hurt when
    //data volumes are small.
    String parentProductsTableName = getTableAndColumnNames().getParentProductsTableName();
    String productSitesTableName = getTableAndColumnNames().getProductSitesTableName();
    if (getDatabaseProductName().equalsIgnoreCase("oracle")) {
      parentProductsTableName = "DCS_PRD_CHLDSKU_TEMP";
      productSitesTableName = "DCS_PRODUCT_SITES_TEMP";
    }  
    
    //Build the replacement array
    Object[] arguments = {
        getTableAndColumnNames().getSkuSitesTableName(),
        getTableAndColumnNames().getSkuIdColName(),
        getTableAndColumnNames().getSkuSiteIdColName(),
        parentProductsTableName,
        getTableAndColumnNames().getParentProductsIdColName(),
        getTableAndColumnNames().getProductIdColName(),
        productSitesTableName
    };
    
    //Now do the replacement
    mInsertSiteCheck = MessageFormat.format(
        "select prdChldSku.{1} " +
        "from  {3} prdChldSku, " + 
        "      {6} prdSites " +
        "where prdChldSku.{5} = prdSites.{5} " +
        "  and prdChldSku.{1} >= ? and prdChldSku.{1} <= ? " +
        "  and not exists (select 1 from {0} skuSites " + 
        "                where skuSites.{2} = prdSites.{2} " +
        "                and   skuSites.{1} = prdChldSku.{1} " +
        "                and   skuSites.{1} >= ? " +
        "                and   skuSites.{1} <= ? )",
        arguments);
    
    return mInsertSiteCheck; 
  }
  
  private String mSitesSQL;
  
  public void setSitesSQL(String pSitesSQL) {
    mSitesSQL = pSitesSQL;
  }

  /**
   * This SQL gets a list of sites from the skus parent products
   * <p>
   * 
   * @return the sql to get the list of sites for a sku from the skus parent products
   * @throws RepositoryException 
   */
  public String getSitesSQL() throws RepositoryException {
    //If it has been over-riden or previousely set, then just return it
    if (mSitesSQL!=null)
      return mSitesSQL;
    
    //Build the replacement array
    Object[] arguments = {
        getTableAndColumnNames().getProductSitesTableName(),
        getTableAndColumnNames().getSkuIdColName(),
        getTableAndColumnNames().getParentProductsTableName(),
        getTableAndColumnNames().getParentProductsIdColName(),
        getTableAndColumnNames().getProductIdColName()
    };
    
    //Now do the replacement
    mSitesSQL = MessageFormat.format(
        "select prdSites.site_id, prdChldSku.{1} from {0} prdSites, {2} prdChldSku where prdSites.{4} = prdChldSku.{3} and prdChldSku.{1} >= ? and prdChldSku.{1} <= ?",
        arguments);
    
    if (isLoggingDebug())
      logDebug(mSitesSQL);
    
    return mSitesSQL; 
  }
  
  private String mProductSitesSQL;
  
  public void setProductSitesSQL(String pProductSitesSQL) {
    mProductSitesSQL = pProductSitesSQL;
  }

  /**
   * This SQL gets a list of sites from the skus parent products
   * <p>
   * 
   * @return the sql to get the list of sites for a sku from the skus parent products
   * @throws RepositoryException 
   */
  public String getProductSitesSQL() throws RepositoryException {
    //If it has been over-riden or previousely set, then just return it
    if (mProductSitesSQL!=null)
      return mProductSitesSQL;

    //Build the replacement array
    Object[] arguments = {
        getTableAndColumnNames().getCategorySitesTableName(),
        getTableAndColumnNames().getProductIdColName(),
        getTableAndColumnNames().getParentCategoriesTableName(),
        getTableAndColumnNames().getParentCategoriesIdColName(),
        getTableAndColumnNames().getCategoryIdColName()
    };
    
    //Now do the replacement
    mProductSitesSQL = MessageFormat.format(
        "select catSites.site_id, catChldPrd.{3} from {0} catSites, {2} catChldPrd where catSites.{4} = catChldPrd.{4} and catChldPrd.{3} >= ? and catChldPrd.{3} <= ?",
        arguments);
    
    return mProductSitesSQL; 
  }
  
  private String mProductAncestorCategoriesSQL;
  
  public void setProductAncestorCategoriesSQL(String pProductAncestorCategoriesSQL){
    mProductAncestorCategoriesSQL = pProductAncestorCategoriesSQL;
  }
  
  /**
   * This method returns the sql that compares the category level
   * ancestor categories with their child products. If the product has any extra
   * ancestor categories then they are flagged for delete further down
   * in the code.
   * <p>
   * 
   * 
   * @return the sql used to check and see if any sites need to be added within a group of skus
   * @throws RepositoryException 
   */
  public String getProductAncestorCategoriesSQL() throws RepositoryException {
    //If it has been over-riden or previously set, then just return it
    if (mProductAncestorCategoriesSQL!=null)
      return mProductAncestorCategoriesSQL;
    
    //Build the replacement array
    Object[] arguments = {
        getTableAndColumnNames().getProductAncestorCategoriesTableName(),
        getTableAndColumnNames().getProductIdColName(),
        getTableAndColumnNames().getParentCategoriesTableName(),
        getTableAndColumnNames().getCategoryAncestorCategoriesTableName(),
        getTableAndColumnNames().getCategoryIdColName(),
        getTableAndColumnNames().getParentCategoriesIdColName(),
        getTableAndColumnNames().getProductAncestorCategoriesIdColName()
    };
    
    //Now do the replacement
    mProductAncestorCategoriesSQL = MessageFormat.format(
        "select catAncCats.anc_category_id, catChldPrd1.child_prd_id " +
        " from dcs_cat_anc_cats  catAncCats, " +
        "      dcs_cat_chldprd catChldPrd1 " +
        "where catAncCats.category_id = catChldPrd1.category_id " + 
        "  and catChldPrd1.child_prd_id >= ? and catChldPrd1.child_prd_id <= ? " +
        "union " +
        "select catChldPrd2.category_id, catChldPrd2.child_prd_id " + 
        " from dcs_cat_chldprd catChldPrd2 " +
        "where catChldPrd2.child_prd_id >= ? and catChldPrd2.child_prd_id <= ? ",
        arguments);

    return mProductAncestorCategoriesSQL; 
  }
  
  private String mCatalogSQL;
  
  public void setCatalogSQL(String pCatalogSQL) {
    mCatalogSQL = pCatalogSQL;
  }

  /**
   * This SQL gets a list of catalogs from the skus parent products
   * <p> 
   * @return the sql to get the list of catalogs for a sku from the skus parent products
   * @throws RepositoryException 
   */
  public String getCatalogsSQL() throws RepositoryException {
    //If it has been over-riden or previousely set, then just return it
    if (mCatalogSQL!=null)
      return mCatalogSQL;
    
    //Build the replacement array
    Object[] arguments = {
        getTableAndColumnNames().getSkuIdColName(),
        getTableAndColumnNames().getSkuComputedCatalogsIdColName(),
        getTableAndColumnNames().getParentProductsTableName(),
        getTableAndColumnNames().getParentProductsIdColName(),
        getTableAndColumnNames().getProductIdColName(),
        getTableAndColumnNames().getProductComputedCatalogsTableName()
    };

    //Now do the replacement
    mCatalogSQL = MessageFormat.format(
        "select prdCat.{1}, prdChldSku.{0} from {5} prdCat, {2} prdChldSku where prdCat.{4} = prdChldSku.{3} and prdChldSku.{0} >= ? and prdChldSku.{0} <= ? ",
        arguments);
    
    if (isLoggingDebug())
      logDebug(mCatalogSQL);
    
    return mCatalogSQL;
  }

  private String mProductCatalogSQL;
  
  public void setProductCatalogSQL(String pProductCatalogSQL) {
    mProductCatalogSQL = pProductCatalogSQL;
  }

  /**
   * This SQL gets a list of catalogs from the products parent categories
   * <p> 
   * @return the sql to get the list of catalogs for a product from the products parent categories
   * @throws RepositoryException 
   */
  public String getProductCatalogsSQL() throws RepositoryException {
    //If it has been over-riden or previousely set, then just return it
    if (mProductCatalogSQL!=null)
      return mProductCatalogSQL;
    
    //Build the replacement array
    Object[] arguments = {
        getTableAndColumnNames().getProductIdColName(),
        getTableAndColumnNames().getProductComputedCatalogIdColName(),
        getTableAndColumnNames().getParentCategoriesTableName(),
        getTableAndColumnNames().getParentCategoriesIdColName(),
        getTableAndColumnNames().getCategoryIdColName(),
        getTableAndColumnNames().getCategoryComputedCatalogsTableName()
    };
    
    //Now do the replacement
    mProductCatalogSQL = MessageFormat.format(
        "select catCatalogs.{1}, catChldPrd.{3} from {5} catCatalogs, {2} catChldPrd where catCatalogs.{4} = catChldPrd.{4} and catChldPrd.{3} >= ? and catChldPrd.{3} <= ?",
        arguments);
    
    return mProductCatalogSQL;
  }

  private String mParentCategoriesForCatalogSQL;
  
  public void setParentCategoriesForCatalogSQL(String pParentCategoriesForCatalogSQL) {
    mParentCategoriesForCatalogSQL = pParentCategoriesForCatalogSQL;
  }
  
  /**
   * This SQL gets a list of parent categories for Catalog from the products parent category
   * <p> 
   * @return the sql to get the list of catalogs for a sku from the skus parent products
   * @throws RepositoryException 
   */
  public String getProductParentCategoriesForCatalogsSQL() throws RepositoryException {
    //If it has been over-riden or previousely set, then just return it
    if (mParentCategoriesForCatalogSQL!=null)
      return mParentCategoriesForCatalogSQL;
    
    //Build the replacement array
    Object[] arguments = {
        getTableAndColumnNames().getProductIdColName(),
        getTableAndColumnNames().getCatalogIdColName(),
        getTableAndColumnNames().getParentCategoriesTableName(),
        getTableAndColumnNames().getParentCategoriesIdColName(),
        getTableAndColumnNames().getCategoryIdColName(),
        getTableAndColumnNames().getCategoryComputedCatalogsTableName()
    };

    //Now do the replacement
    mParentCategoriesForCatalogSQL = MessageFormat.format(
        "select catCatalogs.{1}, catChldPrd.{4}, catChldPrd.{3} " +
        "  from {2} catChldPrd, " +
        "       {5} catCatalogs " +
        " where catChldPrd.{4} = catCatalogs.{4} " +
        "   and catChldPrd.{3} >= ? and catChldPrd.{3} <= ?" ,
        arguments);
    
    return mParentCategoriesForCatalogSQL;
  }
  
  private String mDeleteProductAncestorCategoriesCheck;
  
  public void setDeleteProductAncestorCategoriesCheck(String pDeleteProductAncestorCategoriesCheck){
    mDeleteProductAncestorCategoriesCheck = pDeleteProductAncestorCategoriesCheck;
  }
  
  /**
   * This method returns the sql that compares the category level
   * ancestor categories with their child products. If the product has any extra
   * ancestor categories then they are flagged for delete further down
   * in the code.
   * <p>
   * 
   * 
   * @return the sql used to check and see if any sites need to be added within a group of skus
   * @throws RepositoryException 
   */
  public String getDeleteProductAncestorCategoriesCheck() throws RepositoryException {
    //If it has been over-riden or previousely set, then just return it
    if (mDeleteProductAncestorCategoriesCheck!=null)
      return mDeleteProductAncestorCategoriesCheck;
    
    //Build the replacement array
    Object[] arguments = {
        getTableAndColumnNames().getProductAncestorCategoriesTableName(),
        getTableAndColumnNames().getProductIdColName(),
        getTableAndColumnNames().getParentCategoriesTableName(),
        getTableAndColumnNames().getCategoryAncestorCategoriesTableName(),
        getTableAndColumnNames().getCategoryIdColName(),
        getTableAndColumnNames().getParentCategoriesIdColName(),
        getTableAndColumnNames().getProductAncestorCategoriesIdColName(),
        getTableAndColumnNames().getCategoryAncestorCategoriesAncCategoryIdColunmName()
    };

    //Now do the replacement
    mDeleteProductAncestorCategoriesCheck = MessageFormat.format(
        "select prdAncCats.{1} from {0} prdAncCats " +
        "where prdAncCats.{1} >= ? " +
        "  and prdAncCats.{1} <= ? " +
        "  and not exists (select 1 " + 
        "              from  {3} cacAncCats, " +
        "                    {2} catChildPrd1 " +
        "              where cacAncCats.{4} = catChildPrd1.{4} " +  
        "                and catChildPrd1.{5} = prdAncCats.{1} " + 
        "                and cacAncCats.{7} = prdAncCats.{4} " +
        "              union " +
        "              select 1 from {2} catChldPrd2 " +
        "               where catChldPrd2.{5} = prdAncCats.{1} " +
        "                 and catChldPrd2.{4} = prdAncCats.{4})",
        arguments);

    return mDeleteProductAncestorCategoriesCheck; 
  }
  
  private String mInsertProductAncestorCategoriesCheck;
  
  public void setInsertProductAncestorCategoriesCheck(String pInsertProductAncestorCategoriesCheck) {
    mInsertProductAncestorCategoriesCheck = pInsertProductAncestorCategoriesCheck;
  }
  
  /**
   * This method returns the sql that compares the category level
   * ancestor categories with their child products. If the product is missing any
   * ancestor categories then they are flagged for insert further down
   * in the code.
   * <p>
   * 
   * 
   * @return the sql used to check and see if any sites need to be added within a group of skus
   * @throws RepositoryException 
   */
  public String getInsertProductAncestorCategoriesCheck() throws RepositoryException {
    //If it has been over-riden or previousely set, then just return it
    if (mInsertProductAncestorCategoriesCheck!=null)
      return mInsertProductAncestorCategoriesCheck;

    //Build the replacement array
    Object[] arguments = {
        getTableAndColumnNames().getProductAncestorCategoriesTableName(),
        getTableAndColumnNames().getProductIdColName(),
        getTableAndColumnNames().getParentCategoriesTableName(),
        getTableAndColumnNames().getCategoryAncestorCategoriesTableName(),
        getTableAndColumnNames().getCategoryIdColName(),
        getTableAndColumnNames().getParentCategoriesIdColName(),
        getTableAndColumnNames().getProductAncestorCategoriesIdColName(),
        getTableAndColumnNames().getCategoryAncestorCategoriesAncCategoryIdColunmName()
    };
   
    //Now do the replacement
    mInsertProductAncestorCategoriesCheck = MessageFormat.format(
        "select catChldPrd1.{5} " +
        "from   {3} catAncCats, " +
        "       {2} catChldPrd1 " +
        "where  catAncCats.{4} = catChldPrd1.{4} " +
        "  and  catChldPrd1.{5} >= ? and catChldPrd1.{5} <= ? " +
        "  and  not exists (select 1 from {0} prdAncCats " +
        "                  where prdAncCats.{1} = catChldPrd1.{5}" +
        "                    and prdAncCats.{4} = catAncCats.{7}) " +
        "union " +
        "select catChldPrd2.{5} " +
        "from   {2}  catChldPrd2 " +
        "where  catChldPrd2.{5} >= ? and catChldPrd2.{5} <= ? " +
        "  and  not exists (select 1 from {0} prdAncCats " +
        "                 where prdAncCats.{1} = catChldPrd2.{5}" +
        "                   and prdAncCats.{4} = catChldPrd2.{4})", 
        arguments);

    return mInsertProductAncestorCategoriesCheck; 

  }
  
  private String mDeleteProductParentCategoriesForCatalogCheck;
  
  public void setDeleteProductParentCategoriesForCatalogCheck(String pDeleteProductParentCategoriesForCatalogCheck) {
    mDeleteProductParentCategoriesForCatalogCheck = pDeleteProductParentCategoriesForCatalogCheck;
  }
  
  /**
   * This method returns the sql that compares the category level
   * parent categories for catalog with their child products. If the product has any extra
   * parent categories for catalog then they are flagged for delete further down
   * in the code.
   * <p>
   * 
   * 
   * @return the sql used to check and see if any sites need to be added within a group of skus
   * @throws RepositoryException 
   */
  public String getDeleteProductParentCategoriesForCatalogCheck() throws RepositoryException {
    //If it has been over-riden or previousely set, then just return it
    if (mDeleteProductParentCategoriesForCatalogCheck!=null)
      return mDeleteProductParentCategoriesForCatalogCheck;
    
    //Build the replacement array
    Object[] arguments = {
        getTableAndColumnNames().getProductParentCategoriesForCatalogTableName(),
        getTableAndColumnNames().getProductIdColName(),
        getTableAndColumnNames().getParentCategoriesTableName(),
        getTableAndColumnNames().getCategoryParentCategoriesForCatalogTableName(),
        getTableAndColumnNames().getCategoryIdColName(),
        getTableAndColumnNames().getParentCategoriesIdColName(),
        getTableAndColumnNames().getProductParentCategoriesForCatalogIdColName(),
        getTableAndColumnNames().getCategoryParentCategoriesForCatalogIdColName(),
        getTableAndColumnNames().getCatalogIdColName()
    };
    
    //Now do the replacement
    mDeleteProductParentCategoriesForCatalogCheck = MessageFormat.format(
        "select prdPrntCats.{1} " +
        "from {0} prdPrntCats " +
        "where prdPrntCats.{1} >= ? " +
        "  and prdPrntCats.{1} <= ? " +
        "  and not exists (select 1 " + 
        "                  from  {3} catPrntCats, " +
        "                        {2} catChldProd " +
        "                  where catPrntCats.{4} = catChldProd.{4} " +
        "                    and catChldProd.{5} = prdPrntCats.{1} " +
        "                    and catPrntCats.{4} = prdPrntCats.{4} " +
        "                    and catPrntCats.{8} = prdPrntCats.{8}) ",
        arguments);
    
    return mDeleteProductParentCategoriesForCatalogCheck;
  }
  
  private String mInsertProductParentCategoriesForCatalogCheck;
  
  public void setInsertProductParentCategoriesForCatalogCheck(String pInsertProductParentCategoriesForCatalogCheck) {
    mInsertProductParentCategoriesForCatalogCheck = pInsertProductParentCategoriesForCatalogCheck;
  }
  
  /**
   * This method returns the sql that compares the category level
   * parent categories for catalog with their child products. If the product needs any extra
   * parent categories for catalog then they are flagged for insert further down
   * in the code.
   * <p>
   * 
   * 
   * @return the sql used to check and see if any sites need to be added within a group of skus
   * @throws RepositoryException 
   */
  public String getInsertProductParentCategoriesForCatalogCheck() throws RepositoryException {
    //If it has been over-riden or previousely set, then just return it
    if (mInsertProductParentCategoriesForCatalogCheck!=null)
      return mInsertProductParentCategoriesForCatalogCheck;

    //Build the replacement array
    Object[] arguments = {
        getTableAndColumnNames().getParentCategoriesIdColName(),
        getTableAndColumnNames().getParentCategoriesTableName(),
        getTableAndColumnNames().getCategoryComputedCatalogsTableName(),
        getTableAndColumnNames().getProductParentCategoriesForCatalogTableName(),
        getTableAndColumnNames().getProductIdColName(),
        getTableAndColumnNames().getCategoryIdColName(),
        getTableAndColumnNames().getCatalogIdColName()
    };

    //Now do the replacement
    mInsertProductParentCategoriesForCatalogCheck = MessageFormat.format(
        "select catChldPrd.{0} " +
        "from  {1} catChldPrd, " +
        "      {2} catCatalogs " +
        "where catChldPrd.{5} = catCatalogs.{5} " +
        "and catChldPrd.{0} >= ? and catChldPrd.{0} <= ? " +
        "and not exists (select 1 " +                   
        "                 from  {3} prdPrntCats " +                   
        "                where  prdPrntCats.{6} = catCatalogs.{6} " +                     
        "                  and  prdPrntCats.{4} = catChldPrd.{0} " +
        "                  and  prdPrntCats.{5} = catCatalogs.{5}) " +
        "",
        arguments);
    
    return mInsertProductParentCategoriesForCatalogCheck;
  }
  
  /**
   * Method called prior to start of maintenance
   * @param pRepository the catalogs repository
   * @param pCatalogIds the catalog item ids. note this is only relevent for
   *                     custom catalog services
   * @param pServiceFunctions the service functions to perform
   * @return boolean - return true if maintenance should continue
   *                   return false to prevent maintenance
   *
   * 
   **/
   public boolean preService(Repository pRepository, Collection pCatalogIds, List pServiceFunctions)
   {
     super.preService(pRepository, pCatalogIds, pServiceFunctions);

     setProductExecutor(Executors.newFixedThreadPool(getNumberOfConcurrentUpdateThreads()));
     setSkuExecutor(Executors.newFixedThreadPool(getNumberOfConcurrentUpdateThreads()));
     
     //In case it has been shutdown and restarted.
     setShutDownInProcess(false);
     
     return true;
   }
  
  /**
   *
   * @exception ServiceException if an error occurred during the operation
   **/
  public void doStopService()
    throws ServiceException {
    //This is for the scenario if the shutdown happens while the executor is loading.
    setShutDownInProcess(true);
    
    //First we need to kill any threads
    getProductExecutor().shutdownNow();
    setProductExecutor(null);

    getSkuExecutor().shutdownNow();
    setSkuExecutor(null);
    
    //Now call the ancestor
    super.doStopService();
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
  protected void updateCatalog() {

    long startTime = System.currentTimeMillis();
    
    if(isLoggingInfo()) 
    {
      String msgArgs[] = { ResourceUtils.getMsgResource("PROCESS_UPDATE_CATALOG", MY_RESOURCE_NAME, sResourceBundle), new Date(startTime).toString() };
      logInfo(ResourceUtils.getMsgResource("PROCESS_STARTED", MY_RESOURCE_NAME, sResourceBundle, msgArgs));
    }

    //Do the real work
    try {
      setTableAndColumnNames(new GSATableAndColumnNames());
      processTree();
  
      if(isLoggingInfo())
      {
        long endTime = System.currentTimeMillis();
        long seconds = (endTime - startTime) / 1000;
        
        String msgArgs[] = { ResourceUtils.getMsgResource("PROCESS_UPDATE_CATALOG", MY_RESOURCE_NAME, sResourceBundle), new Date(endTime).toString() };
        
        logInfo(ResourceUtils.getMsgResource("PROCESS_ENDED", MY_RESOURCE_NAME, sResourceBundle, msgArgs));
  
        String msgArgs1[] = { ResourceUtils.getMsgResource("PROCESS_UPDATE_CATALOG", MY_RESOURCE_NAME, sResourceBundle), Long.valueOf(seconds).toString() };
        logInfo(ResourceUtils.getMsgResource("PROCESS_TIME_INSECS", MY_RESOURCE_NAME, sResourceBundle, msgArgs1));
      }
    } catch (RepositoryException e) {
      if (isLoggingError())
        logError("Repository Exception getting table and column names.", e);
    }
  }
  
  /**
   * This method sets all the properties calculated by this service
   * that are pulled down from the top of the tree. Most of the attributes that
   * are updated are pulled down from the top. Since this method can be called 
   * passing catalogs, categories we need to check what the parent
   * item and child items are. 
   * 
   * Attributes being updated are:
   * category.directAncestorCatalogsAndSelf
   * category.indirectAncestorCatalogs
   * category.computedCatalogs
   * category.ancestorCategories
   * category.parentCategoriesForCatalog
   * category.sites
   * product.computedCatalogs
   * product.parentCategoriesForCatalog
   * product.sites
   * sku.computedCatalogs
   * sku.sites
   * 
   * @param pParentItem the parent item is the item that the data will be pulled from
   * @param pCurrentItem the current item is the item that will be updated with 
   *  data from the pParentItem.
   * @param pShadowBean
   * 
   * @throws RepositoryException the repository exception
   * 
   */
  protected void goingDownUpdateItem(RepositoryItem pParentItem, RepositoryItem pCurrentItem, ShadowBean pShadowBean)
    throws RepositoryException {
    
    CatalogProperties props = getCatalogProperties(); 

    //Since the repository items are not updated till the very end we need to get prior values from the tracking object!
    ShadowMapKey parentId = new ShadowMapKey(pParentItem.getRepositoryId(), pParentItem.getItemDescriptor().getItemDescriptorName());
    ShadowBean parentShadowBean = 
      (ShadowBean) getCompletedItems().get(parentId);

    //In the case we are at the top of the tree the parent and current items are the same.
    if (parentShadowBean == null)
      parentShadowBean = pShadowBean;
    
    if (isLoggingDebug()) {
      logDebug("Inside goingDownUpdateItem with pParentItem:" + pParentItem + " hashCode:" + parentShadowBean.hashCode());
      logDebug("Inside goingDownUpdateItem with pCurrentItem:" + pCurrentItem + " hashCode:" + pShadowBean.hashCode());
    }
    
    if (pCurrentItem != null) {
      if (getCatalogTools().isCatalogFolder(pParentItem)) {
        if (getCatalogTools().isCatalog(pCurrentItem)) {
          // So the parent is a catalog and the current item is a catalog, set the direct
          // catalog attribute
          List<String> directAncestorCatalogsAndSelf = new ArrayList<String>();
          directAncestorCatalogsAndSelf.add(pCurrentItem.getRepositoryId());
          setPropertyValueAddAll(pCurrentItem, props.getDirectAncestorCatalogsAndSelfPropertyName(),
              directAncestorCatalogsAndSelf, pShadowBean);
        }
      } else if (getCatalogTools().isCatalog(pParentItem)) {
        if (getCatalogTools().isCatalog(pCurrentItem)) {
          // So the parent is a catalog and the current item is a catalog, set the direct
          // catalog attribute
          List<String> directAncestorCatalogsAndSelf = null;
          if (parentShadowBean.getDirectAncestorCatalogsAndSelf() != null)
            directAncestorCatalogsAndSelf = new ArrayList<String>(parentShadowBean.getDirectAncestorCatalogsAndSelf());
          else
            directAncestorCatalogsAndSelf = new ArrayList<String>();

          directAncestorCatalogsAndSelf.add(pCurrentItem.getRepositoryId());
          setPropertyValueAddAll(pCurrentItem, props.getDirectAncestorCatalogsAndSelfPropertyName(),
              directAncestorCatalogsAndSelf, pShadowBean);
          
          List<String> indirectAncestorCatalogs = null;
          if (parentShadowBean.getIndirectAncestorCatalogs() != null)
            indirectAncestorCatalogs = new ArrayList<String>(parentShadowBean.getIndirectAncestorCatalogs());
          else
            indirectAncestorCatalogs = new ArrayList<String>();

          setPropertyValueAddAll(pCurrentItem, props.getIndirectAncestorCatalogsPropertyName(),
              indirectAncestorCatalogs, pShadowBean);
        } else if (getCatalogTools().isCategory(pCurrentItem)) {
          // So the parent is a catalog and the current item is a category
          List<String> directAncestorCatalogsAndSelf = null;
          if (parentShadowBean.getDirectAncestorCatalogsAndSelf() == null)
            directAncestorCatalogsAndSelf = new ArrayList<String>();
          else
            directAncestorCatalogsAndSelf = new ArrayList<String>(parentShadowBean.getDirectAncestorCatalogsAndSelf());

          List<String> indirectAncestorCatalogs = null;
          if (parentShadowBean.getIndirectAncestorCatalogs() == null)
            indirectAncestorCatalogs = new ArrayList<String>();
          else
            indirectAncestorCatalogs = new ArrayList<String>(parentShadowBean.getIndirectAncestorCatalogs());

          // Check the isUpdateCatalogsProperty property and only update
          // catalogs property if this is true. This is sort of for backwards compatabilty and a slight performance gain
          // as an update will not occur, nothing is really saved in the calculation.
          if (isUpdateCatalogsProperty()
              && (getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES_FOR_PRODUCT)
                  || getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES) 
                  || getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES_FOR_CATEGORY)
                  || getServiceFunctions().contains(CMSConstants.AGS_GENCATALOGS))) {
            Set<String> tempSet = new HashSet<String>();
            tempSet.addAll(directAncestorCatalogsAndSelf);
            tempSet.addAll(indirectAncestorCatalogs);
            if (!tempSet.contains(pParentItem.getRepositoryId()) && pParentItem != pCurrentItem)
              tempSet.add(pParentItem.getRepositoryId());
            setPropertyValueAddAll(pCurrentItem, props.getComputedCatalogsPropertyName(), tempSet, pShadowBean);
          }

          if (getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES_FOR_PRODUCT)
              || getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES) 
              || getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES_FOR_CATEGORY)
              || getServiceFunctions().contains(CMSConstants.AGS_GENANCESTORS)) {
            // Parent is a Catalog, so just clone the parents categories
            List<String> ancestorCategories = (List<String>) parentShadowBean.getAncestorCategories();
            if (ancestorCategories == null)
              ancestorCategories = new ArrayList<String>();
            setPropertyValueAddAll(pCurrentItem, props.getAncestorCategoriesPropertyName(), ancestorCategories,
                pShadowBean);
          }
        }

        if (getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES_FOR_PRODUCT)
            || getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES) 
            || getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES_FOR_CATEGORY)
            || getServiceFunctions().contains(CMSConstants.AGS_GENANCESTORS)) {
          // Parent is a Catalog, so just clone the parents categories
          List<String> ancestorCategories = null;
          if (parentShadowBean.getAncestorCategories() == null)
            ancestorCategories = new ArrayList<String>();
          else
            ancestorCategories = new ArrayList<String>(parentShadowBean.getAncestorCategories());
          setPropertyValueAddAll(pCurrentItem, props.getAncestorCategoriesPropertyName(), ancestorCategories,
              pShadowBean);
        }
      } else if (getCatalogTools().isCategory(pParentItem)) {
        if (getCatalogTools().isCatalog(pCurrentItem)) {
          // So the parent is a category and the current item is a catalog, set the direct
          // catalog attribute, since the parent was a category then start out as an empty set
          List<String> directAncestorCatalogsAndSelf = new ArrayList<String>();
          directAncestorCatalogsAndSelf.add(pCurrentItem.getRepositoryId());
          setPropertyValueAddAll(pCurrentItem, props.getDirectAncestorCatalogsAndSelfPropertyName(),
              directAncestorCatalogsAndSelf, pShadowBean);

          // So the parent is a category and the current item is a catalog, set the direct catalog attribute
          //If all they are trying to do is calc the parent categories and nothing else
          //we need to grab the computed catalogs from the db, else get them from the
          //computed shadow object.
          Set<String> indirectAncestorCatalogs = null;
          if (getServiceFunctions().contains(CMSConstants.AGS_GENPARENTCATS)
              && !(getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES_FOR_PRODUCT)
                  || getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES) 
                  || getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES_FOR_CATEGORY)
                  || getServiceFunctions().contains(CMSConstants.AGS_GENCATALOGS))) {
            Set<RepositoryItem> computedCatalogs =
              (Set<RepositoryItem>) pParentItem.getPropertyValue(props.getComputedCatalogsPropertyName());
            indirectAncestorCatalogs = new HashSet<String>(computedCatalogs == null ? 0 : computedCatalogs.size());
            if(computedCatalogs != null) {
              for (RepositoryItem catalog : computedCatalogs) {
                indirectAncestorCatalogs.add(catalog.getRepositoryId());
              }
            }
          } else {
            indirectAncestorCatalogs = parentShadowBean.getComputedCatalogs();
          }
          
          if (getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES_FOR_PRODUCT)
              || getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES)
              || getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES_FOR_CATEGORY)
              || getServiceFunctions().contains(CMSConstants.AGS_GENCATALOGS))
            setPropertyValueAddAll(pCurrentItem, props.getIndirectAncestorCatalogsPropertyName(),
                new ArrayList<String>(indirectAncestorCatalogs), pShadowBean);
        } else if (getCatalogTools().isCategory(pCurrentItem)) {
          // So the parent is a category and the current item is a category.
          // Check the isUpdateCatalogsProperty property and only update
          // catalogs property if this is true. This is sort of for backwards compatabilty and a slight performance gain
          // as an update will not occur, nothing is really saved in the calculation.
          if (isUpdateCatalogsProperty()
              && (getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES_FOR_PRODUCT)
                  || getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES) 
                  || getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES_FOR_CATEGORY)
                  || getServiceFunctions().contains(CMSConstants.AGS_GENCATALOGS))) {
            Set<String> catalogs = null;
            if (parentShadowBean.getComputedCatalogs() == null)
              catalogs = new HashSet<String>();
            else
              catalogs = new HashSet<String>(parentShadowBean.getComputedCatalogs());
            setPropertyValueAddAll(pCurrentItem, props.getComputedCatalogsPropertyName(), catalogs, pShadowBean);
          }
        }

        if (getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES_FOR_PRODUCT)
            || getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES) 
            || getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES_FOR_CATEGORY)
            || getServiceFunctions().contains(CMSConstants.AGS_GENANCESTORS)) {
          // Parent is a Category, so just clone the parents categories and add the parent category
          List<String> parentAncestorCategories = null;
          if (parentShadowBean.getAncestorCategories() == null)
            parentAncestorCategories = new ArrayList<String>();
          else
            parentAncestorCategories = new ArrayList<String>(parentShadowBean.getAncestorCategories());
          if (pParentItem != pCurrentItem)
            parentAncestorCategories.add(pParentItem.getRepositoryId());
          setPropertyValueAddAll(pCurrentItem, props.getAncestorCategoriesPropertyName(), parentAncestorCategories,
              pShadowBean);
        }
      }

      //Copy down the sites from the parent. Check the isUpdateSitesProperty property and only update
      //sites if this is true. This is sort of for backwards compatabilty and a slight performance gain 
      //as an update will not occur, nothing is really saved in the calculation.
      if (isUpdateSitesProperty()) {
        Set<String> sitesForPreviousItem = null;
        if (parentShadowBean.getSites() == null)
          sitesForPreviousItem =  new HashSet<String>();
        else
          sitesForPreviousItem =  new HashSet<String>(parentShadowBean.getSites());
        setPropertyValueAddAll(pCurrentItem, props.getSitesPropertyName(), sitesForPreviousItem, pShadowBean); 
      }
      
      //This completed items is used for the entire catalog repository to enhance the performance by stopping the 
      //recursion if this item (catalog or category) has been processed.
      ShadowMapKey id = new ShadowMapKey(pCurrentItem.getRepositoryId(), pCurrentItem.getItemDescriptor().getItemDescriptorName());
      getCompletedItems().put(id, pShadowBean);
    }
  }

  /**
   * This checks the repository and the map that holds the dynamic products 
   * 
   * @param pProduct
   * @return
   * @throws RepositoryException 
   */
  protected Collection<String> getCategoriesForProduct(RepositoryItem pProduct) throws RepositoryException {
    //This will get the fixed categories
    Collection<String> categories = new HashSet<String>();
  
    Set<RepositoryItem> parentCategories = (Set<RepositoryItem>) pProduct.getPropertyValue(getCatalogProperties().getParentCategoriesPropertyName()); 
    
    if (parentCategories != null) {
      for (RepositoryItem tempItem:parentCategories) {
        categories.add(tempItem.getRepositoryId());
      }
    }
    
    //now see if we need to do dynamic
    if (isIncludeDynamicChildren()
        && getDynamicProductsMap() != null
        && getDynamicProductsMap().containsKey(pProduct.getRepositoryId())) { 
      
      Collection<String> categoriesForDynamicProduct = getDynamicProductsMap().get(pProduct.getRepositoryId());
      
      if (categoriesForDynamicProduct != null)
        categories.addAll(categoriesForDynamicProduct);
    }
    
    return categories;
  }
  
  
  /**
   * 
   */
  @Override
  protected void updateProducts()
    throws RepositoryException, TransactionDemarcationException {
   
    //If the right service functions are not selected then just return
    if (!(getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES_FOR_PRODUCT)
        || getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES)
        || getServiceFunctions().contains(CMSConstants.AGS_GENPARENTCATS)
        || getServiceFunctions().contains(CMSConstants.AGS_GENCATALOGS)))
      return;
    
    String methodName = "updateProducts";
    TransactionDemarcation transactionDemarcation = new TransactionDemarcation();

    try {
      //Start the transaction
      transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      //NOTE: This is kind of inefficient but I was unable to come up with something better
      //When catalogIds are passed in ideally we should just get those products related to
      //those catalogs. Well we are calculating computedCatalogs so we can not use that.
      //So, since the map that holds all the completed catalogs and categories holds only
      //items that have been processed (if the catalogId is passed in then only those items
      //that are in the catalogs will be in the map) we will check and see if the category 
      //that the product is in the map. If it is then process it! For speed we will check and
      //see if catalogIds were passed in first before checking the map.
      
      //Get all of the products
      RepositoryView productView = getCatalogProperties().getProductRepositoryView(getRepository());
      QueryBuilder queryBuilder = productView.getQueryBuilder();
      Query getProductsQuery = queryBuilder.createUnconstrainedQuery();
      
      // since we do this in batches, we need to order the results
      String sortProp = getCatalogProperties().getIdPropertyName();
      SortDirective sd = new SortDirective(sortProp, SortDirective.DIR_ASCENDING);
      SortDirectives sds = new SortDirectives();
      sds.addDirective(sd);

      int numProducts = productView.executeCountQuery(getProductsQuery);

      transactionDemarcation.end();
      
      //Build the array of data ranges, the plus 2 is for the first row and the last row
      String[] rangeStrings = new String[(numProducts/getMaxItemsPerTransaction()) + 2];
      for (int i=0; i < rangeStrings.length; i++) {
        rangeStrings[i] = new String();
      }
      
      if (isLoggingInfo())
        logInfo(methodName + ": starting to process products.");

      RepositoryItemDescriptor itemDescriptor = getRepository().getItemDescriptor(getCatalogProperties().getProductItemName());
      
      //OK, this is getting a little tricky, for really large datasets if we try to build all
      //of the data pk setps in one step the transaction will probably take too long. So we will
      //batch this as well in chunks that will probably execute in a timeframe that will be good 
      //with the timeout
      int sizeOfBatch = 50000;
      int rangeStringStartIndex = 0;
      for (int i=0; i <= (numProducts/sizeOfBatch); i++) {
        if (isLoggingInfo())
          logInfo("Product range builder loop");
        
        QueryBuilder tempQueryBuilder = productView.getQueryBuilder();
        Query tempQuery;
        
        if (rangeStringStartIndex == 0) {
          tempQuery = queryBuilder.createUnconstrainedQuery();
        } else {
          QueryExpression idQE = tempQueryBuilder.createPropertyQueryExpression(getCatalogProperties().getIdPropertyName());
          QueryExpression startIdQE = queryBuilder.createConstantQueryExpression(rangeStrings[rangeStringStartIndex - 1]);
          tempQuery = queryBuilder.createComparisonQuery(idQE, startIdQE, QueryBuilder.GREATER_THAN_OR_EQUALS);         
        }

        rangeStringStartIndex = generateAndExecuteRange(sizeOfBatch, tempQuery, rangeStrings, numProducts, rangeStringStartIndex, itemDescriptor,
            productView, getProductExecutor(), "product");
      }

      /**
       * To keep things manageable for large catalogs, handle the
       * iterations in groups of <i>maxItemsPerTransaction</I>
       **/
      //We have loaded up the queue now let it free
      try {
        getProductExecutor().shutdown();
        getProductExecutor().awaitTermination(getNumberOfHoursToTimeOutThreads(), TimeUnit.HOURS);
      } catch (InterruptedException ignored) {
        if (isLoggingWarning())
          logWarning("Thread InterruptedException." + ignored);
      }
    } finally {
      if (isLoggingInfo())
        logInfo(methodName + ": finished processing product requests.");
    }
  }

  /**
   * This method batches updates to the database in chunks. This takes care of
   * 2 different issues. The first is memory usage and the second is performance/
   * <p>
   * @param pSizeOfBatch
   * @param pQuery
   * @param pRangeStrings
   * @param pNumOfItems
   * @param pRangeStringStartIndex
   * @param pRepositoryItemDescriptor
   * @param pRepositoryView
   * @param pExecutorService
   * @return
   * @throws RepositoryException
   * @throws TransactionDemarcationException
   */
  protected int generateAndExecuteRange(int pSizeOfBatch, Query pQuery, String[] pRangeStrings, int pNumOfItems, 
      int pRangeStringStartIndex, RepositoryItemDescriptor pRepositoryItemDescriptor,
      RepositoryView pRepositoryView, ExecutorService pExecutorService,
      String pType) 
    throws RepositoryException, TransactionDemarcationException {
    // since we do this in batches, we need to order the results
    String sortProp = getCatalogProperties().getIdPropertyName();
    SortDirective sd = new SortDirective(sortProp, SortDirective.DIR_ASCENDING);
    SortDirectives sds = new SortDirectives();
    sds.addDirective(sd);

    // build the query
    GSAItemDescriptor itemDescriptor = (GSAItemDescriptor) pRepositoryItemDescriptor;
    SQLQuery sq = new SQLQuery(itemDescriptor, (Clause) pQuery, sds);

    ResultSet rs = null;
    GSARepository repository = (GSARepository) getRepository();
    boolean rangesInMemory = repository.getHandleRangesInMemory();
    boolean firstRange = false;
    if (pRangeStringStartIndex == 0) 
      firstRange = true;
    
    Connection dbConnection = null;
    boolean autoCommit=false;
    int idx = 0;
    
    try {
      if (isLoggingInfo())
        logInfo("Execution of rangebean query starting at:" + pRangeStringStartIndex + " using query:" + pQuery.getQueryRepresentation() + " with starting index:" + pRangeStringStartIndex);
      
      dbConnection = repository.getDataSource().getConnection();
      repository.setHandleRangesInMemory(false);
      autoCommit = dbConnection.getAutoCommit();
      dbConnection.setAutoCommit(false);
      
      ResultSetGetter[] getters = itemDescriptor.getIdGetters();

      // run the query
      rs = sq.executeQuery(dbConnection);

      if (isLoggingInfo())
        logInfo("Execution of rangebean query completed.");
      
      //Get the first row of this step, first time will be 1
      if (rs.first() && pRangeStringStartIndex == 0) {
        int colNum = 1;
        int idColCnt = itemDescriptor.getPrimaryTable().getIdColumnCount();
        Object[] idvals = new Object[idColCnt];
        while (colNum <= idColCnt) {
          idvals[colNum - 1] = getters[colNum - 1].getObject(rs, colNum++);
        }
        GSAId rawId = itemDescriptor.generateGSAId(idvals);
        pRangeStrings[idx + pRangeStringStartIndex] = rawId.toString();
        idx++;
      }
      
      //Now the part where we take big steps through the result set, 
      //use the max transactions as the stepping size.
      while (rs.relative(getMaxItemsPerTransaction())
          && (idx*getMaxItemsPerTransaction() < pSizeOfBatch)) {
        int colNum = 1;
        int idColCnt = itemDescriptor.getPrimaryTable().getIdColumnCount();
        Object[] idvals = new Object[idColCnt];
        while (colNum <= idColCnt) {
          idvals[colNum - 1] = getters[colNum - 1].getObject(rs, colNum++);
        }
        
        //If it is the last one then just don't load it so the query for the
        //update will go the the end of the data
        if (idx + pRangeStringStartIndex < pNumOfItems) {
          GSAId rawId = itemDescriptor.generateGSAId(idvals);
          pRangeStrings[idx + pRangeStringStartIndex] = rawId.toString();
        }
        
        //Well, we have the data needed to start this range update so there is no time like the present!
        if (pType.equals("product"))
          pExecutorService.execute(new UpdateProductRange(pRangeStrings[idx + pRangeStringStartIndex - 1], 
              pRangeStrings[idx + pRangeStringStartIndex], pRepositoryView, firstRange));
        else
          pExecutorService.execute(new UpdateSkuRange(pRangeStrings[idx + pRangeStringStartIndex - 1], 
              pRangeStrings[idx + pRangeStringStartIndex], pRepositoryView, firstRange));
          
        if (firstRange)
          firstRange = false;
        
        idx++;
      }
      
      //Get the last row if no other rows have been processed
      if ((idx*getMaxItemsPerTransaction() < pSizeOfBatch) && rs.last()) {
        int colNum = 1;
        int idColCnt = itemDescriptor.getPrimaryTable().getIdColumnCount();
        Object[] idvals = new Object[idColCnt];
        while (colNum <= idColCnt) {
          idvals[colNum - 1] = getters[colNum - 1].getObject(rs, colNum++);
        }
        
        //If it is the last one then just don't load it so the query for the
        //update will go the the end of the data
        if (idx + pRangeStringStartIndex <= pNumOfItems) {
          GSAId rawId = itemDescriptor.generateGSAId(idvals);
          pRangeStrings[idx + pRangeStringStartIndex] = rawId.toString();
          
          //Well, we have the data needed to start this range update so there is no time like the present!
          if (pType.equals("product"))
            pExecutorService.execute(new UpdateProductRange(pRangeStrings[idx + pRangeStringStartIndex - 1], 
                pRangeStrings[idx + pRangeStringStartIndex], pRepositoryView, firstRange));
          else
            pExecutorService.execute(new UpdateSkuRange(pRangeStrings[idx + pRangeStringStartIndex - 1], 
                pRangeStrings[idx + pRangeStringStartIndex], pRepositoryView, firstRange));
          
          if (firstRange)
            firstRange = false;
        }
      }
    } catch (SQLException e) {
      throw new RepositoryException(e);
    } finally {
      close(rs);
      close(sq);
      repository.setHandleRangesInMemory(rangesInMemory);
      try {
        dbConnection.commit();
        dbConnection.setAutoCommit(autoCommit);
        dbConnection.close();
      } catch (SQLException e) {
        throw new RepositoryException(e);
      }
    }
    
    return idx + pRangeStringStartIndex;
  }
  
  /**
   * Close a result set
   * <p> 
   * @param pResultSet
   */
  protected void close(ResultSet pResultSet)
  {
    if (pResultSet != null)
      {
        try { 
          pResultSet.close(); 
        } catch (SQLException sqle) { 
          if (isLoggingError())
            logError(sqle);
        }
      }
  }
  
  /**
   * Close an SQLStatement logging any SQLExceptions. It is okay to pass a
   * null here.
   * <p>
   * @param pStatement statement to close, may be null
   **/
  protected final void close(SQLStatement pStatement)
  {
    if (pStatement != null)
      {
        try { 
          pStatement.close(); 
        } catch (SQLException sqle) { 
          if (isLoggingError())
            logError(sqle);
        }
      }
  }
  
  /**
   * @throws TransactionDemarcationException 
   * 
   */
  @Override
  protected void updateSkus()
    throws RepositoryException, TransactionDemarcationException {
   
    //If the right service functions are not selected then just return
    if (!(getServiceFunctions().contains(CMSConstants.AGS_GENPROPERTIES)
        || getServiceFunctions().contains(CMSConstants.AGS_GENCATALOGS)))
      return;
    
    String methodName = "updateSkus";
    TransactionDemarcation transactionDemarcation = new TransactionDemarcation();

    try {
      transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      //NOTE: This is kind of inefficient but I was unable to come up with something better
      //When catalogIds are passed in ideally we should just get those products related to
      //those catalogs. Well we are calculating computedCatalogs so we can not use that.
      //So, since the map that holds all the completed catalogs and categories holds only
      //items that have been processed (if the catalogId is passed in then only those items
      //that are in the catalogs will be in the map) we will check and see if the category 
      //that the product is in the map. If it is then process it! For speed we will check and
      //see if catalogIds were passed in first before checking the map.
      
      //Get all of the sku
      RepositoryView skuView = getCatalogProperties().getSkuRepositoryView(getRepository());
      QueryBuilder queryBuilder = skuView.getQueryBuilder();
      Query skuQuery = queryBuilder.createUnconstrainedQuery();
      
      // since we do this in batches, we need to order the results
      String sortProp = getCatalogProperties().getIdPropertyName();
      SortDirective sd = new SortDirective(sortProp, SortDirective.DIR_ASCENDING);
      SortDirectives sds = new SortDirectives();
      sds.addDirective(sd);

      if (isLoggingInfo())
        logInfo(methodName + ": starting to process skus.");
      
      int numSkus = skuView.executeCountQuery(skuQuery);
      
      transactionDemarcation.end();
      
      //Build the array of data ranges, the plus 2 is for the first row and the last row
      String[] rangeStrings = new String[(numSkus/getMaxItemsPerTransaction()) + 2];
      for (int i=0; i < rangeStrings.length; i++) {
        rangeStrings[i] = new String();
      }
      
      RepositoryItemDescriptor itemDescriptor = getRepository().getItemDescriptor(getCatalogProperties().getSkuItemName());
      
      //OK, this is getting a little tricky, for really large datasets if we try to build all
      //of the data pk steps in one step the transaction will probably take too long. So we will
      //batch this as well in chunks that will probably execute in a timeframe that will be good 
      //with the timeout
      int sizeOfBatch = 50000;
      int rangeStringStartIndex = 0;
      for (int i=0; i <= (numSkus/sizeOfBatch); i++) {
        if (isLoggingInfo())
          logInfo("Sku range builder loop");
        
        QueryBuilder tempQueryBuilder = skuView.getQueryBuilder();
        Query tempQuery;
        
        if (rangeStringStartIndex == 0) {
          tempQuery = queryBuilder.createUnconstrainedQuery();
        } else {
          QueryExpression idQE = tempQueryBuilder.createPropertyQueryExpression(getCatalogProperties().getIdPropertyName());
          QueryExpression startIdQE = queryBuilder.createConstantQueryExpression(rangeStrings[rangeStringStartIndex - 1]);
          tempQuery = queryBuilder.createComparisonQuery(idQE, startIdQE, QueryBuilder.GREATER_THAN_OR_EQUALS);         
        }
        
        rangeStringStartIndex = generateAndExecuteRange(sizeOfBatch, tempQuery, rangeStrings, numSkus, rangeStringStartIndex, itemDescriptor,
            skuView, getSkuExecutor(), "sku");
      }
      
      if (isLoggingInfo())
        logInfo("Sku Range calculation completed.");
      
      //We have loaded up the queue now let it free
      try {
        getSkuExecutor().shutdown();
        getSkuExecutor().awaitTermination(getNumberOfHoursToTimeOutThreads(), TimeUnit.HOURS);
      } catch (InterruptedException ignored) {
        if (isLoggingWarning())
          logWarning("Thread InterruptedException." + ignored);
      }
      
      if (isLoggingInfo())
        logInfo(methodName + ": finished thread.");
      
    } finally {
      if (isLoggingInfo())
        logInfo(methodName + ": finished processing skus.");
    }
  }

  /**
   * 
   * @param pRepository
   * @param pItem
   * @return
   * @throws RepositoryException
   */
  protected List<String> getDynamicProducts(Repository pRepository, RepositoryItem pItem) throws RepositoryException {
    //This is a little tricky, the firt time this is called we really need to only get the dynamicChildCategories since the
    //standard tree walk will get the fixed. When we need to get both is after the first time since the standard tree
    //walk did not handle these categories.
    
    String perfName = "getDynamicProducts";
    List<String> children = new ArrayList<String>();;

    if (isLoggingDebug())
      logDebug(perfName + ": starting with pItem:" + pItem);

    if (getCatalogTools().isCategory(pItem)) {
      List<RepositoryItem> dynamicChildProducts = null;

      dynamicChildProducts = (List<RepositoryItem>) pItem.getPropertyValue(getCatalogProperties()
          .getDynamicChildProductsPropertyName());

      if (dynamicChildProducts != null)
        for (RepositoryItem tempItem:dynamicChildProducts)
          children.add(tempItem.getRepositoryId());
    }

    if (isLoggingDebug())
      logDebug(perfName + ": ending with pItem:" + pItem + " with children:" + children);

    return children;
  }
  
  /**
   * This method sets all the properties calculated by this service
   * the are pulled up from the bottom of the tree. Currently the only
   * value pulled up is the AllRootCategories. This is the first category 
   * while walking down each branch. For the AGS we do not need to update
   * anything on the way up.
   * 
   * @param pParentItem the parent item that will be updated.
   * @param pCurrentItem the current item is used to pull the data
   * from for the pParentItem update.
   * @param shadowBean
   * 
   * @throws RepositoryException the repository exception
   */
  protected void goingUpUpdateItem(RepositoryItem pParentItem, RepositoryItem pCurrentItem,
      ShadowBean shadowBean)
    throws RepositoryException {
  }

  /**
   * This class will update a range of products
   */
  private class UpdateProductRange extends UpdateRange {
    private Map<String, Set<String>> prodSites = new HashMap<String,Set<String>>();      
    private Map<String, Set<String>> prodCatalogs = new HashMap<String,Set<String>>();
    private Map<String, List<String>> prodAncestorCategories = new HashMap<String,List<String>>();
    private Map<String,HashMap<String, Collection<String>>> prodParentCategories = new HashMap<String,HashMap<String, Collection<String>>>();
    
    UpdateProductRange(String pStartId, String pEndId, RepositoryView pRepositoryView, boolean pFirstRange) throws RepositoryException {
      super(pStartId, pEndId, pRepositoryView);
      
      if (isLoggingInfo())
        logInfo("Creating UpdateProductRange object:" + pStartId + " - " + pEndId);
      
      //Save off the repository descriptor
      setRepositoryItemDescriptor((GSAItemDescriptor) getRepository().getItemDescriptor(getCatalogProperties().getProductItemName()));
      
      String deleteCatalogsCheck = getDeleteProductCatalogsCheck();
      String insertCatalogsCheck = getInsertProductCatalogsCheck();  
      String deleteSiteCheck = getDeleteProductSiteCheck();
      String insertSiteCheck = getInsertProductSiteCheck();
      String insertAncestorCategoriesCheck = getInsertProductAncestorCategoriesCheck();
      String deleteAncestorCategoriesCheck = getDeleteProductAncestorCategoriesCheck();
      String insertParentCategoriesForCatalog = getInsertProductParentCategoriesForCatalogCheck();
      String deleteParentCategoriesForCatalog = getDeleteProductParentCategoriesForCatalogCheck();
      
      if (!pFirstRange) {
        deleteCatalogsCheck = deleteCatalogsCheck.replaceAll(">=", ">");
        insertCatalogsCheck = insertCatalogsCheck.replaceAll(">=", ">");
        deleteSiteCheck = deleteSiteCheck.replaceAll(">=", ">");
        insertSiteCheck = insertSiteCheck.replaceAll(">=", ">");
        insertAncestorCategoriesCheck = insertAncestorCategoriesCheck.replaceAll(">=", ">");
        deleteAncestorCategoriesCheck = deleteAncestorCategoriesCheck.replaceAll(">=", ">");
        insertParentCategoriesForCatalog = insertParentCategoriesForCatalog.replaceAll(">=", ">");
        deleteParentCategoriesForCatalog = deleteParentCategoriesForCatalog.replaceAll(">=", ">");
      } 
      
      //This SQL checks to see if any product/catalogs rows will need to be changed because of the update (rows that should not be there)
      setDeleteCatalogsCheckSQL(deleteCatalogsCheck);
      if (isLoggingDebug())
        logDebug(deleteCatalogsCheck);

      //This SQL checks to see if any product/catalogs rows will need to be changed because of the update (rows that should be there)
      setInsertCatalogsCheckSQL(insertCatalogsCheck);
      if (isLoggingDebug())
        logDebug(insertCatalogsCheck);
      
      //This SQL checks to see if any product/sites rows will need to be changed because of the update (rows that should not be there)
      setDeleteSiteCheckSQL(deleteSiteCheck);
      if (isLoggingDebug())
        logDebug(deleteSiteCheck);
      
      //This SQL checks to see if any product/sites rows will need to be changed because of the update (rows that should be there)
      setInsertSiteCheckSQL(insertSiteCheck);
      if (isLoggingDebug())
        logDebug(insertSiteCheck);
      
      //This SQL checks to see if any product/ancestor categories rows will need to be changed because of the update (rows that should be there)
      setDeleteProductAncestorCategoriesSQL(deleteAncestorCategoriesCheck);
      if (isLoggingDebug())
        logDebug(deleteAncestorCategoriesCheck);
      
      //This SQL checks to see if any product/ancestor categories rows will need to be changed because of the update (rows that should be there)
      setInsertProductAncestorCategoriesSQL(insertAncestorCategoriesCheck);
      if (isLoggingDebug())
        logDebug(insertAncestorCategoriesCheck);
      
      //This SQL checks to see if any product/parent categories for catalog rows will need to be changed because of the update (rows that should be there)
      setDeleteProductParentCategoriesForCatalogCheckSQL(deleteParentCategoriesForCatalog);
      if (isLoggingDebug())
        logDebug(deleteParentCategoriesForCatalog);
      
      //This SQL checks to see if any product/parent categories for catalog rows will need to be changed because of the update (rows that should be there)
      setInsertProductParentCategoriesForCatalogSQL(insertParentCategoriesForCatalog);
      if (isLoggingDebug())
        logDebug(insertParentCategoriesForCatalog);
    }

    @Override
    public void loadUpdateRangeData(Connection pDBConnection, String pStartId, String pEndId) throws RepositoryException, SQLException {
        PreparedStatement psToexec = pDBConnection.prepareStatement(getProductSitesSQL()); 
        psToexec.setString(1, pStartId);
        psToexec.setString(2, pEndId);
        ResultSet rs = psToexec.executeQuery();
           
        while (rs.next()) {
            String siteId = new String(rs.getString(1));
            String prodId = new String(rs.getString(2));

            if (prodSites.containsKey(prodId))
                prodSites.get(prodId).add(siteId);
            else {
                Set<String> sites = new HashSet<String>();
                sites.add(siteId);
                prodSites.put(prodId, sites);
            }
        }

        rs.close();
        psToexec.clearParameters();
        psToexec.close();
        
        psToexec = pDBConnection.prepareStatement(getProductAncestorCategoriesSQL()); 
        psToexec.setString(1, pStartId);
        psToexec.setString(2, pEndId);
        psToexec.setString(3, pStartId);
        psToexec.setString(4, pEndId);
        rs = psToexec.executeQuery();
           
        while (rs.next()) {
            String ancCat = new String(rs.getString(1));
            String prodId = new String(rs.getString(2));

            if (prodAncestorCategories.containsKey(prodId))
                prodAncestorCategories.get(prodId).add(ancCat);
            else {
                List<String> ancCats = new ArrayList<String>();
                ancCats.add(ancCat);
                prodAncestorCategories.put(prodId, ancCats);
            }
        }

        rs.close();
        psToexec.clearParameters();
        psToexec.close();

        psToexec = pDBConnection.prepareStatement(getProductCatalogsSQL()); 
        psToexec.setString(1, pStartId);
        psToexec.setString(2, pEndId);
        rs = psToexec.executeQuery();
           
        while (rs.next()) {
            String calatogId = new String(rs.getString(1));
            String prodId = new String(rs.getString(2));

            if (prodCatalogs.containsKey(prodId))
                prodCatalogs.get(prodId).add(calatogId);
            else {
                Set<String> catalogs = new HashSet<String>();
                catalogs.add(calatogId);
                prodCatalogs.put(prodId, catalogs);
            }
        }

        rs.close();
        psToexec.clearParameters();
        psToexec.close();
 
        psToexec = pDBConnection.prepareStatement(getProductParentCategoriesForCatalogsSQL()); 
        psToexec.setString(1, pStartId);
        psToexec.setString(2, pEndId);
        rs = psToexec.executeQuery();
           
        while (rs.next()) {
            String calatogId = new String(rs.getString(1));
            String categoryId = new String(rs.getString(2));
            String prodId = new String(rs.getString(3));

            if (prodParentCategories.containsKey(prodId))
                if (prodParentCategories.get(prodId).containsKey(calatogId))
                    prodParentCategories.get(prodId).get(calatogId).add(categoryId);
                else {
                    Collection<String> tempCats = new HashSet<String>();
                    tempCats.add(categoryId);
                    prodParentCategories.get(prodId).put(calatogId, tempCats);
                }
            else {
                HashMap<String, Collection<String>> tempMap = new HashMap <String,Collection<String>>();
                Collection<String> tempCats = new HashSet<String>();
                tempCats.add(categoryId);
                tempMap.put(calatogId, tempCats);
                prodParentCategories.put(prodId, tempMap);
            }
        }

        rs.close();
        psToexec.clearParameters();
        psToexec.close();
    }
    
    @Override
    public ShadowBean buildUpdateBean(Connection pDBConnection, RepositoryItem pProduct) throws RepositoryException {
      //Create a shadowBean for the product
      ShadowBean productShadowBean = new ShadowBean();
      String productID = pProduct.getRepositoryId();
      productShadowBean.setId(productID);
      productShadowBean.setParentCategoriesForCatalog(null);
      productShadowBean.setPriorParentCategoriesForCatalog(null);
      
      GSARepository repository = (GSARepository) getRepository();
      boolean handleRangesInMemory = repository.getHandleRangesInMemory();
      
      try {
        repository.setHandleRangesInMemory(false);
        Set<String> sites = prodSites.get(productID);
        if (sites == null)
            sites = new HashSet<String>();
        
        Set<String> computedCatalogs = prodCatalogs.get(productID);
        if (computedCatalogs == null)
            computedCatalogs = new HashSet<String>();
        
        List<String> ancestorCategories = prodAncestorCategories.get(productID);
        if (ancestorCategories == null)
            ancestorCategories = new ArrayList<String>();
        
        HashMap<String, Collection<String>> parentCategoriesForCatalog = prodParentCategories.get(productID);
        if (parentCategoriesForCatalog == null)
            parentCategoriesForCatalog = new HashMap<String, Collection<String>>();
            
        //if the site needs to be updated
        if (getIdsForSites().contains(pProduct.getRepositoryId())
            || getIdsForDynamicProducts().contains(pProduct.getRepositoryId())) {

           //Ok so we have now handled the static stuff, now the wonderful dynamic properties
          if (isIncludeDynamicChildren() && getDynamicProductsMap() != null && getDynamicProductsMap().containsKey(pProduct.getRepositoryId())) {
            String inString = new String("");
            Collection<String> categories = getDynamicProductsMap().get(pProduct.getRepositoryId());

            if (categories.size() > 0) {
              for (String categoryId:categories) {
                if (inString.equals(""))
                  inString = "'" + categoryId + "'";
                else
                  inString = inString + ",'" + categoryId + "'";
              }
              
              //Build the replacement array
              Object[] arguments = {
                  getTableAndColumnNames().getCategorySitesTableName(),
                  getTableAndColumnNames().getCategoryIdColName(),
                  getTableAndColumnNames().getCategorySitesSiteColumnName()
              };
              
              //Now do the replacement
              String SQLToExecute = MessageFormat.format(
                  "select categorySites.{2} from {0} categorySites " +
                  "where categorySites.{1} in (",
                  arguments);
              
              SQLToExecute = SQLToExecute + inString + ")";
 
              PreparedStatement psToexec = pDBConnection.prepareStatement(SQLToExecute); 
              ResultSet rs = psToexec.executeQuery();
             
              while (rs.next()) {
                String siteId = new String(rs.getString(1));
                if (!sites.contains(siteId))
                  sites.add(siteId);
              }
      
              rs.close();
              psToexec.clearParameters();
              psToexec.close();            
            }
          }

          if (productShadowBean.getSites() == null)
            productShadowBean.setSites(sites);
          else
            productShadowBean.getSites().addAll(sites);
        }

        //if the ancestor categories needs to be updated
        if (getIdsForCategories().contains(productID)
            || getIdsForDynamicProducts().contains(productID)) {

          //Ok so we have now handled the static stuff, now the wonderful dynamic properties
          if (isIncludeDynamicChildren() && getDynamicProductsMap() != null && getDynamicProductsMap().containsKey(pProduct.getRepositoryId())) {
            String inString = new String("");
            Collection<String> categories = getDynamicProductsMap().get(pProduct.getRepositoryId());

            if (categories.size() > 0) {
              for (String categoryId:categories) {
                if (inString.equals(""))
                  inString = "'" + categoryId + "'";
                else
                  inString = inString + ",'" + categoryId + "'";
              }
  
              //Build the replacement array
              Object[] arguments = {
                  getTableAndColumnNames().getCategoryAncestorCategoriesTableName(),
                  getTableAndColumnNames().getCategoryIdColName(),
                  getTableAndColumnNames().getCategoryAncestorCategoriesAncCategoryIdColunmName()
              };
              
              //Now do the replacement
              String SQLToExecute = MessageFormat.format(
                  "select catAncCats.{2} from {0}  catAncCats " +
                  "where catAncCats.{1} in (",
                  arguments);

              SQLToExecute = SQLToExecute + inString + ")";

              PreparedStatement psToexec = pDBConnection.prepareStatement(SQLToExecute); 
              ResultSet rs = psToexec.executeQuery();
             
              while (rs.next()) {
                String categoryId = new String(rs.getString(1));
                if (!ancestorCategories.contains(categoryId))
                  ancestorCategories.add(categoryId);
              }
      
              rs.close();
              psToexec.clearParameters();
              psToexec.close();
            }
          }

          //Ok so we have now handled the static stuff, now the wonderful dynamic properties
          if (getDynamicProductsMap() != null && getDynamicProductsMap().containsKey(pProduct.getRepositoryId())) {
            Collection<String> categories = getDynamicProductsMap().get(pProduct.getRepositoryId());

            for (String categoryId:categories) {
              if (!ancestorCategories.contains(categoryId))
                ancestorCategories.add(categoryId);
            }
          }
          
          if (productShadowBean.getAncestorCategories() == null)
            productShadowBean.setAncestorCategories(ancestorCategories);
          else
            productShadowBean.getAncestorCategories().addAll(ancestorCategories);
        }
        
        //Now see if the parent category for catalog need to be updates
        if (getIdsForParentCategoryForCatalog().contains(pProduct.getRepositoryId())
            || getIdsForDynamicProducts().contains(pProduct.getRepositoryId())) {

          //Ok so we have now handled the static stuff, now the wonderful dynamic properties
          if (isIncludeDynamicChildren() && getDynamicProductsMap() != null && getDynamicProductsMap().containsKey(pProduct.getRepositoryId())) {
            String inString = new String("");
            Collection<String> categories = getDynamicProductsMap().get(pProduct.getRepositoryId());

            if (categories.size() > 0) {
              for (String categoryId:categories) {
                if (inString.equals(""))
                  inString = "'" + categoryId + "'";
                else
                  inString = inString + ",'" + categoryId + "'";
              }
  
              //Build the replacement array
              Object[] arguments = {
                  getTableAndColumnNames().getCatalogIdColName(),
                  getTableAndColumnNames().getCategoryIdColName(),
                  getTableAndColumnNames().getCategoryParentCategoriesForCatalogTableName(),
                  getTableAndColumnNames().getCategoryComputedCatalogsTableName()
              };
              
              //Now do the replacement
              String SQLToExecute = MessageFormat.format(
                  "select catPrntCats.{0}, catPrntCats.{1} " +
                  "from {2} catPrntCats where catPrntCats.{1} in (",
                  arguments);
              
              SQLToExecute = SQLToExecute + inString + ")";
              
              SQLToExecute = SQLToExecute + MessageFormat.format(
                  " union select catCmptdCats.{0}, catCmptdCats.{1} " +
                  "from {3} catCmptdCats where catCmptdCats.{1} in (",
                  arguments);
              
              SQLToExecute = SQLToExecute + inString + ")";
              PreparedStatement psToexec = pDBConnection.prepareStatement(SQLToExecute); 
              ResultSet rs = psToexec.executeQuery();
             
              while (rs.next()) {
                String catalogId = new String(rs.getString(1));
                Set<String> category = new HashSet<String>();
                
                if (!parentCategoriesForCatalog.containsKey(catalogId)) {
                  category.add(new String(rs.getString(2)));
                  parentCategoriesForCatalog.put(catalogId, category);
                } else {
                  parentCategoriesForCatalog.get(catalogId).add(new String(rs.getString(2)));
                }
              }
      
              rs.close();
              psToexec.clearParameters();
              psToexec.close();
            }
          }

          if (productShadowBean.getParentCategoriesForCatalog() == null)
            productShadowBean.setParentCategoriesForCatalog(parentCategoriesForCatalog);
          else
            productShadowBean.getParentCategoriesForCatalog().putAll(parentCategoriesForCatalog);
          
          productShadowBean.setPriorParentCategoriesForCatalog(parentCategoriesForCatalog);
          
        }
        
        //Now check for computed catalogs
        if (getIdsForCatalogs().contains(pProduct.getRepositoryId())
            || (getIdsForDynamicProducts() != null 
                && getIdsForDynamicProducts().contains(pProduct.getRepositoryId()))) {
          
          //Ok so we have now handled the static stuff, now the wonderful dynamic properties
          if (isIncludeDynamicChildren() && getDynamicProductsMap() != null && getDynamicProductsMap().containsKey(pProduct.getRepositoryId())) {
            String inString = new String("");
            Collection<String> categories = getDynamicProductsMap().get(pProduct.getRepositoryId());

            if (categories.size() > 0) {
              for (String categoryId:categories) {
                if (inString.equals(""))
                  inString = "'" + categoryId + "'";
                else
                  inString = inString + ",'" + categoryId + "'";
              }
              
              //Build the replacement array
              Object[] arguments = {
                  getTableAndColumnNames().getCatalogIdColName(),
                  getTableAndColumnNames().getCategoryIdColName(),
                  getTableAndColumnNames().getCategoryComputedCatalogsTableName()
              };
              
              //Now do the replacement
              String SQLToExecute = MessageFormat.format(
                  "select catCatalogs.{0} " +
                  "from {2} catCatalogs where catCatalogs.{1} in (",
                  arguments);
              
              SQLToExecute = SQLToExecute + inString + ")";

              PreparedStatement psToexec = pDBConnection.prepareStatement(SQLToExecute); 
              ResultSet rs = psToexec.executeQuery();
             
              while (rs.next()) {
                String catalogId = new String(rs.getString(1));
                if (!computedCatalogs.contains(catalogId)) {
                  computedCatalogs.add(catalogId);
                }
              }
      
              rs.close();
              psToexec.clearParameters();
              psToexec.close();
            }
          }

          if (productShadowBean.getComputedCatalogs() == null)
            productShadowBean.setComputedCatalogs(computedCatalogs);
          else
            productShadowBean.getComputedCatalogs().addAll(computedCatalogs);
        }
      } catch (SQLException e) {
        if (isLoggingError())
          logError(e);
      } finally {
          repository.setHandleRangesInMemory(handleRangesInMemory);
      }

      return productShadowBean; 
    }
  }
  
  /**
   * This class will update a range of skus
   */
  private class UpdateSkuRange extends UpdateRange {
    private Map<String, Set<String>> skuSites = new HashMap<String,Set<String>>();
    private Map<String, Set<String>> skuCatalogs = new HashMap<String,Set<String>>();
      
    UpdateSkuRange(String pStartId, String pEndId, RepositoryView pRepositoryView, boolean pFirstRange) throws RepositoryException {
      super(pStartId, pEndId, pRepositoryView);

      if (isLoggingInfo())
        logInfo("Creating UpdateSkuRange object:" + pStartId + " - " + pEndId);
      
      //Save off the repository descriptor
      setRepositoryItemDescriptor((GSAItemDescriptor) getRepository().getItemDescriptor(getCatalogProperties().getSkuItemName()));

      String deleteCatalogsCheck = getDeleteCatalogsCheck();
      String insertCatalogsCheck = getInsertCatalogsCheck();
      String deleteSiteCheck = getDeleteSiteCheck();
      String insertSiteCheck = getInsertSiteCheck();
      
      if (!pFirstRange) {
        deleteCatalogsCheck = deleteCatalogsCheck.replaceAll(">=", ">");
        insertCatalogsCheck = insertCatalogsCheck.replaceAll(">=", ">");
        deleteSiteCheck = deleteSiteCheck.replaceAll(">=", ">");
        insertSiteCheck = insertSiteCheck.replaceAll(">=", ">");
      } 
      
      //This SQL checks to see if any sku/catalogs rows will need to be changed because of the update (rows that should not be there)
      setDeleteCatalogsCheckSQL(deleteCatalogsCheck);
      if (isLoggingDebug())
        logDebug(deleteCatalogsCheck);
      
      //This SQL checks to see if any sku/catalogs rows will need to be changed because of the update (rows that should be there)
      setInsertCatalogsCheckSQL(insertCatalogsCheck);
      if (isLoggingDebug())
        logDebug(insertCatalogsCheck);      
      
      //This SQL checks to see if any sku/sites rows will need to be changed because of the update (rows that should not be there)
      setDeleteSiteCheckSQL(deleteSiteCheck);
      if (isLoggingDebug())
        logDebug(deleteSiteCheck); 
      
      //This SQL checks to see if any sku/sites rows will need to be changed because of the update (rows that should be there)
      setInsertSiteCheckSQL(insertSiteCheck);
      if (isLoggingDebug())
        logDebug(insertSiteCheck);      
    }

    //This will load the update range data
    @Override
    public void loadUpdateRangeData(Connection pDBConnection, String pStartId, String pEndId) throws RepositoryException, SQLException {
        PreparedStatement psToexec = pDBConnection.prepareStatement(getSitesSQL()); 
        psToexec.setString(1, pStartId);
        psToexec.setString(2, pEndId);
        ResultSet rs = psToexec.executeQuery();
           
        while (rs.next()) {
            String siteId = new String(rs.getString(1));
            String skuId = new String(rs.getString(2));

            if (skuSites.containsKey(skuId))
                skuSites.get(skuId).add(siteId);
            else {
                Set<String> sites = new HashSet<String>();
                sites.add(siteId);
                skuSites.put(skuId, sites);
            }
        }

        rs.close();
        psToexec.clearParameters();
        psToexec.close();
        
        psToexec = pDBConnection.prepareStatement(getCatalogsSQL()); 
        psToexec.setString(1, pStartId);
        psToexec.setString(2, pEndId);
        rs = psToexec.executeQuery();
           
        while (rs.next()) {
            String catalogId = new String(rs.getString(1));
            String skuId = new String(rs.getString(2));

            if (skuCatalogs.containsKey(skuId))
                skuCatalogs.get(skuId).add(catalogId);
            else {
                Set<String> catalogs = new HashSet<String>();
                catalogs.add(catalogId);
                skuCatalogs.put(skuId, catalogs);
            }
        }

        rs.close();
        psToexec.clearParameters();
        psToexec.close();
    }
    
    @Override
    public ShadowBean buildUpdateBean(Connection pDBConnection, RepositoryItem pSku)  throws RepositoryException {
      //Create a shadowBean for the sku
      ShadowBean skuShadowBean = new ShadowBean();
      String sku_id = (String) pSku.getPropertyValue(getCatalogProperties().getIdPropertyName());
      skuShadowBean.setId(sku_id);
      
      GSARepository repository = (GSARepository) getRepository();
      boolean handleRangesInMemory = repository.getHandleRangesInMemory();
      
      try {
        repository.setHandleRangesInMemory(false);
        
        Set<String> sites = skuSites.get(sku_id);
        if (sites == null)
            sites = new HashSet<String>();
        
        Set<String> computedCatalogs = skuCatalogs.get(sku_id);
        if (computedCatalogs == null)
            computedCatalogs = new HashSet<String>();
        
        //if the site needs to be updated
        if (getIdsForSites().contains(pSku.getRepositoryId())) {
          if (skuShadowBean.getSites() == null)
            skuShadowBean.setSites(sites);
          else
            skuShadowBean.getSites().addAll(sites);
        }
        
        //Now see if the computed catalogs need to be updates
        if (getIdsForCatalogs().contains(pSku.getRepositoryId())) {
          if (skuShadowBean.getComputedCatalogs() == null)
            skuShadowBean.setComputedCatalogs(computedCatalogs);
          else
            skuShadowBean.getComputedCatalogs().addAll(computedCatalogs);
        }
      } finally {
        repository.setHandleRangesInMemory(handleRangesInMemory);
      }

      return skuShadowBean; 
    }
  }
  
  /**
   * This is an nested helper class to get the names of columns and tables from the GSA
   * @author jturgeon
   *
   */
  private class GSATableAndColumnNames {
    private String mProductIdColName;

    public String getProductIdColName() {
      return mProductIdColName;
    }
    
    private String mCategoryIdColName;
    
    public String getCategoryIdColName() {
      return mCategoryIdColName;
    }

    private String mProductComputedCatalogsTableName;
    
    public String getProductComputedCatalogsTableName() {
      return mProductComputedCatalogsTableName;
    }
    
    private String mProductComputedCatalogIdColName;

    public String getProductComputedCatalogIdColName() {
      return mProductComputedCatalogIdColName;
    }
    
    private String mParentCategoriesTableName;
    
    public String getParentCategoriesTableName() {
      return mParentCategoriesTableName;
    }
    
    private String mParentCategoriesIdColName;

    public String getParentCategoriesIdColName() {
      return mParentCategoriesIdColName;
    }

    private String mCategoryComputedCatalogsTableName;
    
    public String getCategoryComputedCatalogsTableName() {
      return mCategoryComputedCatalogsTableName;
    }

    private String mSkuIdColName;
    
    public String getSkuIdColName() {
      return mSkuIdColName;
    }
    
    private String mProductSitesTableName;
    
    public String getProductSitesTableName() {
      return mProductSitesTableName;
    }

    private String mProductSiteIdColName;
    
    public String getProductSiteIdColName() {
      return mProductSiteIdColName;
    }

    private String mSkuSitesTableName;
    
    public String getSkuSitesTableName() {
      return mSkuSitesTableName;
    }
    
    private String mSkuSiteIdColName;
    
    public String getSkuSiteIdColName() {
      return mSkuSiteIdColName;
    }

    private String mSkuComputedCatalogsTableName;
    
    public String getSkuComputedCatalogsTableName() {
      return mSkuComputedCatalogsTableName;
    }
    
    private String mSkuComputedCatalogsIdColName;
    
    public String getSkuComputedCatalogsIdColName() { 
      return mSkuComputedCatalogsIdColName;
    }

    private String mParentProductsTableName;
    
    public String getParentProductsTableName() {
      return mParentProductsTableName;
    }
    
    private String mParentProductsIdColName;
    
    public String getParentProductsIdColName() {
      return mParentProductsIdColName;
    }
    
    private String mCatalogIdColName;
    
    public String getCatalogIdColName() {
      return mCatalogIdColName;
    }
    
    private String mCategorySitesTableName;
    
    public String getCategorySitesTableName() {
      return mCategorySitesTableName;
    }

    private String mProductAncestorCategoriesTableName;
    
    public String getProductAncestorCategoriesTableName() {
      return mProductAncestorCategoriesTableName;
    }
    
    private String mProductAncestorCategoriesIdColName;
    
    public String getProductAncestorCategoriesIdColName() {
      return mProductAncestorCategoriesIdColName;
    }

    private String mCategoryAncestorCategoriesTableName;
    
    public String getCategoryAncestorCategoriesTableName() {
      return mCategoryAncestorCategoriesTableName;
    }

    private String mCategoryParentCategoriesForCatalogTableName;
    
    public String getCategoryParentCategoriesForCatalogTableName() {
      return mCategoryParentCategoriesForCatalogTableName;
    }
    
    private String mCategoryParentCategoriesForCatalogIdColName;
    
    public String getCategoryParentCategoriesForCatalogIdColName() {
      return mCategoryParentCategoriesForCatalogIdColName;
    }

    private String mProductParentCategoriesForCatalogTableName;
    
    public String getProductParentCategoriesForCatalogTableName() {
      return mProductParentCategoriesForCatalogTableName;
    }
    
    private String mProductParentCategoriesForCatalogIdColName;
    
    public String getProductParentCategoriesForCatalogIdColName() {
      return mProductParentCategoriesForCatalogIdColName;
    }

    private String mCategorySitesSiteColumnName;
    
    public String getCategorySitesSiteColumnName() {
      return mCategorySitesSiteColumnName;
    }
    
    private String mCategoryAncestorCategoriesAncCategoryIdColunmName;
    
    public String getCategoryAncestorCategoriesAncCategoryIdColunmName() {
      return mCategoryAncestorCategoriesAncCategoryIdColunmName;
    }
    
    private String mCategoryParentCategoriesTableName;
    
    public String getCategoryParentCategoriesTableName() {
      return mCategoryParentCategoriesTableName;
    }
    
    //Default constructor gets all the table and column names
    public GSATableAndColumnNames() throws RepositoryException {
      super();

      //Get the Item descriptors
      GSAItemDescriptor productItemDescriptor = (GSAItemDescriptor) getRepository().getItemDescriptor(getCatalogProperties().getProductItemName());
      GSAItemDescriptor categoryItemDescriptor = (GSAItemDescriptor) getRepository().getItemDescriptor(getCatalogProperties().getCategoryItemName());
      GSAItemDescriptor catalogItemDescriptor = (GSAItemDescriptor) getRepository().getItemDescriptor(getCatalogProperties().getCatalogItemName());
      GSAItemDescriptor skuItemDescriptor = (GSAItemDescriptor) getRepository().getItemDescriptor(getCatalogProperties().getSkuItemName());
      
      //Get the product property descriptor
      GSAPropertyDescriptor productDesc = (GSAPropertyDescriptor) productItemDescriptor.getIdProperty();

      //Get the category property descriptor
      GSAPropertyDescriptor categoryDesc = (GSAPropertyDescriptor) categoryItemDescriptor.getIdProperty();

      //Get the computed catalogs property descriptor
      GSAPropertyDescriptor productComputedCatalogsDesc = (GSAPropertyDescriptor)
        productItemDescriptor.getRepositoryPropertyDescriptor(getCatalogProperties().getComputedCatalogsPropertyName());

      //Get the sku property descriptor
      GSAPropertyDescriptor skuDesc = (GSAPropertyDescriptor) skuItemDescriptor.getIdProperty();
      
      //Get the parentProducts property descriptor
      GSAPropertyDescriptor parentCategoriesDesc = (GSAPropertyDescriptor)
        productItemDescriptor.getRepositoryPropertyDescriptor(getCatalogProperties().getParentCategoriesPropertyName());

      //Category Parent Categories
      GSAPropertyDescriptor categoryParentCategoriesDesc = (GSAPropertyDescriptor)
        categoryItemDescriptor.getRepositoryPropertyDescriptor(getCatalogProperties().getFixedParentCategoriesPropertyName());
      
      //Get the parentProducts property descriptor
      GSAPropertyDescriptor categoryComputedCatalogsDesc = (GSAPropertyDescriptor)
        categoryItemDescriptor.getRepositoryPropertyDescriptor(getCatalogProperties().getComputedCatalogsPropertyName());

      //Get the catalog property descriptor
      GSAPropertyDescriptor catalogDesc = (GSAPropertyDescriptor) catalogItemDescriptor.getIdProperty();

      //Get the sites property descriptor
      GSAPropertyDescriptor productSitesDesc = (GSAPropertyDescriptor)
        productItemDescriptor.getRepositoryPropertyDescriptor(getCatalogProperties().getSitesPropertyName());
      
      //Get the computed sites property descriptor
      GSAPropertyDescriptor skuSitesDesc = (GSAPropertyDescriptor)
        skuItemDescriptor.getRepositoryPropertyDescriptor(getCatalogProperties().getSitesPropertyName());

      //Get the computed catalogs property descriptor
      GSAPropertyDescriptor skuComputedCatalogsDesc = (GSAPropertyDescriptor)
        skuItemDescriptor.getRepositoryPropertyDescriptor(getCatalogProperties().getComputedCatalogsPropertyName());

      //Get the parentProducts property descriptor
      GSAPropertyDescriptor parentProductsDesc = (GSAPropertyDescriptor)
        skuItemDescriptor.getRepositoryPropertyDescriptor(getCatalogProperties().getParentProductsPropertyName());

      //Get the parentCategories property descriptor
      GSAPropertyDescriptor categorySitesDesc = (GSAPropertyDescriptor)
        categoryItemDescriptor.getRepositoryPropertyDescriptor(getCatalogProperties().getSitesPropertyName());

      //Get the product ancestor categories property descriptor
      GSAPropertyDescriptor productAncestorCategoriesDesc = (GSAPropertyDescriptor)
        productItemDescriptor.getRepositoryPropertyDescriptor(getCatalogProperties().getAncestorCategoriesPropertyName());

      //Get the categories ancestor categories property descriptor
      GSAPropertyDescriptor categoryAncestorCategoriesDesc = (GSAPropertyDescriptor)
        categoryItemDescriptor.getRepositoryPropertyDescriptor(getCatalogProperties().getAncestorCategoriesPropertyName());

      //Get the categories parent categories for catalog property descriptor
      GSAPropertyDescriptor categoryParentCategoriesForCatalogDesc = (GSAPropertyDescriptor)
        categoryItemDescriptor.getRepositoryPropertyDescriptor(getCatalogProperties().getParentCategoriesForCatalogPropertyName());

      //Get the product ancestor categories property descriptor
      GSAPropertyDescriptor productParentCategoriesForCatalog = (GSAPropertyDescriptor)
        productItemDescriptor.getRepositoryPropertyDescriptor(getCatalogProperties().getParentCategoriesForCatalogPropertyName());

      //Now get the table and column names
      mProductIdColName = productDesc.getColumnNames()[0];
      mCategoryIdColName = categoryDesc.getColumnNames()[0];
      mProductComputedCatalogsTableName = productComputedCatalogsDesc.getTable().getName();
      mProductComputedCatalogIdColName = productComputedCatalogsDesc.getColumnNames()[0];
      mParentCategoriesTableName = parentCategoriesDesc.getTable().getName();
      mParentCategoriesIdColName = parentCategoriesDesc.getTable().getIdColumnNames()[0];
      mCategoryComputedCatalogsTableName = categoryComputedCatalogsDesc.getTable().getName();
      mSkuIdColName = skuDesc.getColumnNames()[0];
      mProductSitesTableName = productSitesDesc.getTable().getName();
      mProductSiteIdColName = productSitesDesc.getColumnNames()[0];
      mSkuSitesTableName = skuSitesDesc.getTable().getName();
      mSkuSiteIdColName = skuSitesDesc.getColumnNames()[0];
      mSkuComputedCatalogsTableName = skuComputedCatalogsDesc.getTable().getName();
      mSkuComputedCatalogsIdColName = skuComputedCatalogsDesc.getColumnNames()[0];
      mParentProductsTableName = parentProductsDesc.getTable().getName();
      mParentProductsIdColName = parentProductsDesc.getColumnNames()[0];
      mCatalogIdColName = catalogDesc.getColumnNames()[0];
      mCategorySitesTableName = categorySitesDesc.getTable().getName();
      mCategorySitesSiteColumnName = categorySitesDesc.getColumnNames()[0];
      mProductAncestorCategoriesTableName = productAncestorCategoriesDesc.getTable().getName();
      mProductAncestorCategoriesIdColName = productAncestorCategoriesDesc.getColumnNames()[0];
      mCategoryAncestorCategoriesTableName = categoryAncestorCategoriesDesc.getTable().getName();
      mCategoryAncestorCategoriesAncCategoryIdColunmName = categoryAncestorCategoriesDesc.getColumnNames()[0];
      mCategoryParentCategoriesForCatalogTableName = categoryParentCategoriesForCatalogDesc.getTable().getName();
      mCategoryParentCategoriesForCatalogIdColName = categoryParentCategoriesForCatalogDesc.getColumnNames()[0];
      mProductParentCategoriesForCatalogTableName = productParentCategoriesForCatalog.getTable().getName();
      mProductParentCategoriesForCatalogIdColName = productParentCategoriesForCatalog.getColumnNames()[0];
      mCategoryParentCategoriesTableName = categoryParentCategoriesDesc.getTable().getName();
    }
  }
}