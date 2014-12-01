/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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

package atg.projects.store.scenario.action;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import atg.commerce.scenario.FillRelatedItemsSlotAction;
import atg.multisite.SiteGroupManager;
import atg.process.ProcessException;
import atg.repository.RepositoryItem;

/**
 * EStore implementation of a fillRelatedItemsSlotAction scenario action.
 * This implementation filters related items based on order specified and current site (only products from sharing sites are added to slot). 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/scenario/action/StoreFillRelatedItemsSlotAction.java#2 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class StoreFillRelatedItemsSlotAction extends FillRelatedItemsSlotAction {
  /**
   * Class version
   */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/scenario/action/StoreFillRelatedItemsSlotAction.java#2 $$Change: 651448 $";
  
  private String mShareableTypeId;
  private String mSitesPropertyName;
  private SiteGroupManager mSiteGroupManager;

  @Override
  public void configure(Object pConfiguration) throws ProcessException {
    super.configure(pConfiguration);
    // EStore specific configuration
    StoreFillRelatedItemsSlotActionConfiguration configuration = (StoreFillRelatedItemsSlotActionConfiguration) pConfiguration;
    mShareableTypeId = configuration.getShareableTypeId();
    mSitesPropertyName = configuration.getSitesPropertyName();
    mSiteGroupManager = configuration.getSiteGroupManager();
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void checkRelatedItems(List pOrderProducts, List pRelatedItems, Set pRelatedItemSet) {
    for (RepositoryItem relatedProduct: (List<RepositoryItem>) pRelatedItems) {
      // Add into resulting collection only products that satisfy some specific rules
      if (checkRelatedItem(pOrderProducts, relatedProduct)) {
        pRelatedItemSet.add(relatedProduct);
      }
    }
  }
  
  /**
   * This method checks the product specified to see if it should be added into related items slot.
   * Only products from shared sites will be added to this slot.
   * @param pOrderProducts - products in the current order.
   * @param pRelatedProduct - product to be tested.
   * @return <code>true</code> if the product specified should be added into slot and <code>false</code> otherwise.
   */
  @SuppressWarnings("unchecked")
  protected boolean checkRelatedItem(List<RepositoryItem> pOrderProducts, RepositoryItem pRelatedProduct) {
    // Do not display products that already in the shopping cart
    if (pOrderProducts.contains(pRelatedProduct)) {
      return false;
    }
    Collection<String> sharingSiteIds = mSiteGroupManager.getSharingSiteIds(mShareableTypeId);
    // No sharing sites, i.e. no multisite, so all products are good
    if (sharingSiteIds == null) {
      return true;
    }
    // Clone sites collection to leave the product intact
    Collection<String> productSites = new HashSet<String>((Collection<String>) pRelatedProduct.getPropertyValue(mSitesPropertyName));
    // If there is something to remove then these collections intercept, i.e. we should display the product specified
    // If there is nothing to remove then these collections do not intercept, i.e. the product is from unshared site, do not display it
    return productSites.removeAll(sharingSiteIds);
  }
}
