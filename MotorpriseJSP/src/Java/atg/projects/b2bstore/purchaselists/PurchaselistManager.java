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

package atg.projects.b2bstore.purchaselists;

import atg.commerce.gifts.GiftlistManager;

/**
 * This class used to modify the behavior of the GiftlistManager class so that it
 * fits the Purchase List paradigm. The features formerly in this class have been
 * moved into GiftlistManager.
 *
 * @deprecated use GiftlistManager
 *
 * @author <a href="mailto:jlang@atg.com">Jeremy Lang</a>, ATG Dynamo Innovations
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/purchaselists/PurchaselistManager.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class PurchaselistManager extends GiftlistManager {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/purchaselists/PurchaselistManager.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Properties
  //-------------------------------------

  /**
   * Gets property removeItemFromPurchaseListOnMoveToCart.
   * @beaninfo description: the property that tells Dynamo whether to remove a
   * purchase list item when it is transferred to a user's cart.
   *
   * @deprecated use GiftlistManager.isRemoveItemFromGiftlistOnMoveToCart
   */
  public boolean isRemoveItemFromPurchaseListOnMoveToCart() {
    return isRemoveItemFromGiftlistOnMoveToCart();
  }

  /**
   * Sets property removeItemFromPurchaseListOnMoveToCart.
   * @param pRemoveItemFromPurchaseListOnMoveToCart The value which tells Dynamo whether to remove a
   * purchase list item when it is transferred to a user's cart.
   * @beaninfo description: the property that tells Dynamo whether to remove a
   * purchase list item when it is transferred to a user's cart.
   *
   * @deprecated use GiftlistManager.setRemoveItemFromGiftlistOnMoveToCart
   */
  public void setRemoveItemFromPurchaseListOnMoveToCart(boolean pRemoveItemFromPurchaseListOnMoveToCart) {
    setRemoveItemFromGiftlistOnMoveToCart(pRemoveItemFromPurchaseListOnMoveToCart);
  }

}