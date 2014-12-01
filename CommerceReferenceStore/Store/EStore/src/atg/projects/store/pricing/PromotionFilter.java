/*<ATGCOPYRIGHT>
 * Copyright (C) 2009-2011 Art Technology Group, Inc.
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

package atg.projects.store.pricing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import atg.commerce.pricing.PricingModelHolder;
import atg.nucleus.GenericService;
import atg.projects.store.collections.filter.SiteGroupFilter;
import atg.service.collections.filter.FilterException;

/**
 * Filters out a collection of promotion repository items using the filter
 * mFilter.
 * 
 * @author ckearney
 */
public class PromotionFilter extends GenericService{
  
  /** Class version */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/pricing/PromotionFilter.java#1 $$Change: 651448 $";
  
  //-----------------------------------
  // PROPERTIES
  //-----------------------------------
  
  //-----------------------------------
  // property: mFilter
  private SiteGroupFilter mFilter;
  
  /**
   * Filter the promotions by siteGroup
   * @return mFilter
   */
  public SiteGroupFilter getFilter() {
    return mFilter;
  }

  /**
   * Sets the property mFilter
   * @param pFilter A SiteGroupFilter
   */
  public void setFilter(SiteGroupFilter pFilter) {
    mFilter = pFilter;
  }

  //-----------------------------------
  // property: mSitePromotions
  Collection mSiteGroupPromotions;
  
  /**
   * @return mSiteGroupPromotions A collection of promotions
   * valid for the current site group
   */
  public Collection getSiteGroupPromotions() {
    initalizeSiteGroupPromotions();
    return mSiteGroupPromotions;
  }
  
  //-----------------------------------
  // property: mPricingModelHolder
  private PricingModelHolder mPricingModelHolder;
  
  /**
   * @return An object holding the pricing models
   */
  public PricingModelHolder getPricingModelHolder() {
    return mPricingModelHolder;
  }

  /**
   * Sets the pricing model holder
   * @param pPricingModelHolder The new pricing model holder
   */
  public void setPricingModelHolder(PricingModelHolder pPricingModelHolder) {
    mPricingModelHolder = pPricingModelHolder;
  }
  
  //-----------------------------------
  // METHODS
  //-----------------------------------
  
  /**
   * Populates the mSiteGroupPromotions Collection
   */
  protected void initalizeSiteGroupPromotions() {
    // All promotions
    Collection unfilteredCollection = mPricingModelHolder.getAllPromotions();
    if(unfilteredCollection == null || unfilteredCollection.size() == 0){
      mSiteGroupPromotions = new ArrayList();
      return;
    }
    
    // The promotions that apply to this site group
    if(mSiteGroupPromotions == null){
      mSiteGroupPromotions = new HashSet();
    }
    mSiteGroupPromotions.clear();
    
    // Filter all promotions by siteGroup
    try {
      mSiteGroupPromotions = getFilter().generateFilteredCollection(unfilteredCollection, null, null);
    }
    catch (FilterException e) {
      if(isLoggingError()){
        logError(e);
      }
      mSiteGroupPromotions = new ArrayList();
    }
  }
}
