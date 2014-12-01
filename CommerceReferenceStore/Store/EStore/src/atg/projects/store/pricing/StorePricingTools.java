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
package atg.projects.store.pricing;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;

import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.UnitPriceBean;

import atg.projects.store.logging.LogUtils;

import java.util.*;


/**
 * Store extension for the PricingTools.
 *
 * @see atg.commerce.pricing.PricingTools
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/pricing/StorePricingTools.java#2 $
 */
public class StorePricingTools extends PricingTools {
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/pricing/StorePricingTools.java#2 $$Change: 651448 $";

  /**
   * Generates a Map of unit price beans for all items in the order. the key to the map
   * is the commerce item id. each value in the map is a list of UnitPriceBeans.
   * @param pOrder Order
   * @return map of unit price beans
   */
  public Map generateUnitPriceBeans(Order pOrder) {
    Map unitPriceBeans = new HashMap(pOrder.getCommerceItemCount());

    //first generate all the UnitPriceBeans for all items in the order
    Collection commerceItems = pOrder.getCommerceItems();
    Iterator commerceItemerator = commerceItems.iterator();
    List unitbeans = null;

    while (commerceItemerator.hasNext()) {
      CommerceItem item = (CommerceItem) commerceItemerator.next();
      unitbeans = generatePriceBeans(item);

      if ((unitbeans != null) && (unitbeans.size() > 0)) {
        unitPriceBeans.put(item.getId(), unitbeans);
      }
    }

    return unitPriceBeans;
  }
}
