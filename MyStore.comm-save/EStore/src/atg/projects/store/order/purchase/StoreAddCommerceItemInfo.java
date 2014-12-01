/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2010 Art Technology Group, Inc.
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
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/purchase/StoreAddCommerceItemInfo.java#3 $$Change: 635816 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class StoreAddCommerceItemInfo extends AddCommerceItemInfo {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/purchase/StoreAddCommerceItemInfo.java#3 $$Change: 635816 $";

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
