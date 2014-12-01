/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
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

package atg.projects.store.catalog.comparison;

import atg.commerce.catalog.CatalogTools;
import atg.commerce.catalog.comparison.ProductComparisonList;
import atg.commerce.catalog.custom.CustomCatalogTools;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * 
 * Extends atg.commerce.catalog.comparison.ProductComparisonList and gets the 
 * default category for the current site.
 *
 * @author ckearney
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/catalog/comparison/StoreProductComparisonList.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class StoreProductComparisonList extends ProductComparisonList {
  
  //-----------------------------------
  // STATIC
  //-----------------------------------
  
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/catalog/comparison/StoreProductComparisonList.java#2 $$Change: 651448 $";
  
  //-----------------------------------
  // MEMBERS
  //-----------------------------------
  
  //-----------------------------------
  // mSiteManager
  private SiteManager mSiteManager;
  
  /**
   * Returns the site manager
   * @return mSiteManager
   */
  public SiteManager getSiteManager(){
    return mSiteManager;
  }
  
  /**
   * Sets the SiteManager
   * @param pSiteManager The SiteManager
   */
  public void setSiteManager(SiteManager pSiteManager){
    mSiteManager = pSiteManager;
  }
  
  //-----------------------------------
  // PUBLIC METHODS
  //-----------------------------------
  
  /**
   * @see ProductComparisonList.add(String pProductId, String pCategoryId,
   *  String pSkuId, String pCatalogKey, String pSiteId)
   *  
   *  Adds a product to the comparison list, if the pCategoryId is null try
   *  to determine the default categoryId for product pProductId on site
   *  pSiteId.
   */
  public boolean add(String pProductId, String pCategoryId, String pSkuId, String pCatalogKey, String pSiteId)
    throws RepositoryException
  {
    if(pCategoryId == null){
      pCategoryId = defaultCategory(pProductId, pSiteId);
    }
    
    return super.add(pProductId, pCategoryId, pSkuId, pCatalogKey, pSiteId);
  }
  
  /**
   * @see ProductComparisonList.contains(String pProductId, String pCategoryId,
   *  String pSkuId, String pCatalogKey, String pSiteId, boolean pMatchSku) 
   *  
   *  Determines if a product exists in the comparison list, if the pCategoryId
   *  is null try to determine the default categoryId for product pProductId
   *  on site pSiteId.
   */
  public boolean contains(String pProductId, String pCategoryId, String pSkuId, String pCatalogKey, String pSiteId, boolean pMatchSku) 
    throws RepositoryException  
  {
    if(pCategoryId == null){
      pCategoryId = defaultCategory(pProductId, pSiteId);
    }
    
    return super.contains(pProductId, pCategoryId, pSkuId, pCatalogKey, pSiteId, pMatchSku);
  }
  
  //-----------------------------------
  // PROTECTED METHODS
  //-----------------------------------
  
  /**
   * Returns the default categorys for pProductId on pSiteId
   * @param pProductId A productId
   * @param pSiteId A siteId
   * @return The default categoryId for product pProductId on site pSiteId
   */
  protected String defaultCategory(String pProductId, String pSiteId) {
    
    if(pProductId == null){
      return null;
    }
    
    // If pSiteId is null use the site we are currently on
    if(pSiteId == null){
      pSiteId = !(pSiteId == null) ? pSiteId : SiteContextManager.getCurrentSiteId();
    }
    
    CatalogTools catalogTools = getCatalogTools();
    
    try{
      if(catalogTools instanceof CustomCatalogTools)
      {        
        // site repository item
        RepositoryItem site = getSiteManager().getSite(pSiteId);
        if(site == null){
          if(isLoggingDebug()){
            logDebug("Cannot get default category for product " + pProductId + 
              " on site " + pSiteId + " the site may not exist");
          }
          return null;
        }
        
        // get the catalog repository item from the site id
        RepositoryItem catalog = ((CustomCatalogTools) catalogTools).getCatalogForSite(site);
        if(catalog == null){
          if(isLoggingDebug()){
            logDebug("Cannot get default category for product " + pProductId + 
              " on site " + pSiteId + " the catalog may not exist");
          }
          return null;
        }
        
        // product repository item
        RepositoryItem product = ((CustomCatalogTools) catalogTools).findProduct(pProductId);
        if(product == null){
          if(isLoggingDebug()){
            logDebug("Cannot get default category for product " + pProductId + 
              " on site " + pSiteId + " the product may not exist");
          }
          return null;
        }
                
        //get the category from the product and catalog
        RepositoryItem category = ((CustomCatalogTools)catalogTools).getParentCategory(product, catalog);
        if(category == null){
          if(isLoggingDebug()){
            logDebug("Cannot get default category for product " + pProductId + 
              " on site " + pSiteId + " the category may not exist");
          }
          return null;
        }
        
        // if we have a category repository item gets its id
        if(category != null){
          return category.getRepositoryId(); 
        }
      }
    }
    catch(RepositoryException e){
      if(isLoggingError()){
        logError(e.getMessage());
      }
    }
    
    return null;
  }

}
