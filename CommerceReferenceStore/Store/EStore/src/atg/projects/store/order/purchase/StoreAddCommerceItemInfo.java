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
package atg.projects.store.order.purchase;

import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.AddCommerceItemInfo;

import java.util.Map;


/**
 * This class extends AddCommerceItemInfo to add shipping information.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/purchase/StoreAddCommerceItemInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class StoreAddCommerceItemInfo extends AddCommerceItemInfo {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/purchase/StoreAddCommerceItemInfo.java#2 $$Change: 651448 $";

  /**
   * Old shipping group id.
   */
  protected String mOldShippingGroupId;

  /**
   * Shipping group.
   */
  protected ShippingGroup mShippingGroup;

  /**
   * Shipping information.
   */
  protected Map mShippingInfo;

  /**
   * Sets the ID of the shipping group used for an old item.
   * @param pOldShippingGroupId - old shipping group id
   */
  public void setOldShippingGroupId(String pOldShippingGroupId) {
    mOldShippingGroupId = pOldShippingGroupId;
  }

  /**
   * @return the ID of the shipping group used for an old item.
   */
  public String getOldShippingGroupId() {
    return mOldShippingGroupId;
  }

  /**
   * Sets the shipping group to use for a new item.
   * @param pShippingGroup - shipping group
   */
  public void setShippingGroup(ShippingGroup pShippingGroup) {
    mShippingGroup = pShippingGroup;
  }

  /**
   * @return the shipping group to use for a new item.
   */
  public ShippingGroup getShippingGroup() {
    return mShippingGroup;
  }

  /**
   * Sets the <code>Map</code> containing information to
   * be associated with a new item's <code>ShippingGroup</code>.
   * @param pShippingInfo - shipping information
   */
  public void setShippingInfo(Map pShippingInfo) {
    mShippingInfo = pShippingInfo;
  }

  /**
   * @return a <code>Map</code> containing information to
   * be associated with a new item's <code>ShippingGroup</code>.
   */
  public Map getShippingInfo() {
    return mShippingInfo;
  }
}
