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
package atg.projects.store.gifts;

import atg.commerce.CommerceException;
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.order.HardgoodShippingGroup;

/**
 * This class performs specific to CRS actions with giftlists in addition to
 * those which are provided in parent class.
 * 
 * @author Vitali Kazlouski
 * @version $Id:
 *          //product/DCS/main/Java/atg/commerce/order/purchase/ShippingGroupDroplet
 *          .java#38 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class StoreGiftlistManager extends GiftlistManager {

  // -------------------------------------
  // Class version string
  // -------------------------------------

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/gifts/StoreGiftlistManager.java#2 $$Change: 651448 $";

  /**
   * Prefix for the HardgoodShippingGroup's description. For more details see
   * {@link #createShippingGroupFromGiftlist(String)}.
   * */
  private String mGiftShippingGroupDescriptionPrefix = "[Gift]";

  /**
   * Returns the description prefix
   * 
   * @return the giftShippingGroupDescriptionPrefix
   */
  public String getGiftShippingGroupDescriptionPrefix() {
    return mGiftShippingGroupDescriptionPrefix;
  }

  /**
   * Use this method to set the description prefix - it is used in
   * {@link #createShippingGroupFromGiftlist(String)}
   * 
   * @param pGiftShippingGroupDescriptionPrefix
   *          the giftShippingGroupDescriptionPrefix to set
   * @see #createShippingGroupFromGiftlist(String)
   */
  public void setGiftShippingGroupDescriptionPrefix(String pGiftShippingGroupDescriptionPrefix) {
    mGiftShippingGroupDescriptionPrefix = pGiftShippingGroupDescriptionPrefix;
  }

  /**
   * This method creates {@link HardgoodShippingGroup} for the giftlist. The
   * description of created shipping group is made up of
   * {@link #getGiftShippingGroupDescriptionPrefix()} + giftlistEventName.
   * 
   * @param pGiftlistId
   *          GiftlistId based on which a ShippingGroup will be created
   * @return an instance of {@link HardgoodShippingGroup}
   */
  public HardgoodShippingGroup createShippingGroupFromGiftlist(String pGiftlistId) throws CommerceException {
    HardgoodShippingGroup shippingGroup = super.createShippingGroupFromGiftlist(pGiftlistId);
    String giftlistEventName = null;
    if (pGiftlistId != null && shippingGroup != null) {
      giftlistEventName = getGiftlistEventName(pGiftlistId);
      shippingGroup.setDescription(getGiftShippingGroupDescriptionPrefix() + giftlistEventName);
    }
    return shippingGroup;
  }

}
