/*<ATGCOPYRIGHT>
 * Copyright (C) 2010 Art Technology Group, Inc.
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

import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroupManager;
import atg.repository.RepositoryItem;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

/**
 * Extends the StorePurchaseProcessHelper to implement express checkout processing.
 * methods.
 * <p>
 */
public class StoreExpressCheckoutProcessHelper extends StorePurchaseProcessHelper {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/purchase/StoreExpressCheckoutProcessHelper.java#3 $$Change: 635816 $";


  /**
   * Ensure that any gift shipping groups have their shipping method set to the default shipping method.
   * @param pOrder - The current order, an <code>Order</code> value.
   * @param pProfile - A profile, a <code>RepositoryItem</code> value.
   * @param pShippingMethodPropertyName - The name of the shipping method property, a <code>String</code> value.
   * @throws IOException - if an error occurs
   * @throws ServletException - if an error occurs
   */
  protected void ensureShippingMethodOfGiftShippingGroups(Order pOrder, RepositoryItem pProfile, String pShippingMethodPropertyName)
  throws IOException, ServletException {

  List giftShippingGroups = ((ShippingGroupManager) getShippingGroupManager()).getGiftShippingGroups(pOrder);

  if ((giftShippingGroups != null) && (giftShippingGroups.size() > 0)) {
    Iterator shippingGrouperator = giftShippingGroups.iterator();
    HardgoodShippingGroup shippingGroup;
    String defaultShippingMethod = (String) pProfile.getPropertyValue(pShippingMethodPropertyName);

    while (shippingGrouperator.hasNext()) {
      shippingGroup = (HardgoodShippingGroup) shippingGrouperator.next();
      // Set the default shipping method
      shippingGroup.setShippingMethod(defaultShippingMethod);
    }
  }
}
  
}
