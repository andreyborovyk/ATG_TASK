/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2011 Art Technology Group, Inc.
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


/**

 *
 * @author
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/purchase/CheckoutOptionSelections.java#2 $
 */
public class CheckoutOptionSelections {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/purchase/CheckoutOptionSelections.java#2 $$Change: 651448 $";

  /**
   * Prefill shipping address.
   */
  private boolean mPrefillShippingAddress;

  /**
   * Prefilling billing address.
   */
  private boolean mPrefillBillingAddress;

  /**
   * Prefill credit card.
   */
  private boolean mPrefillCreditCard;

  /**
   * Checkout option.
   */
  String mCheckoutOption;

  /**
   * @return prefill shipping address.
   */
  public boolean getPrefillShippingAddress() {
    return mPrefillShippingAddress;
  }

  /**
   * @param pPrefillShippingAddress - prefill shipping address.
   */
  public void setPrefillShippingAddress(boolean pPrefillShippingAddress) {
    mPrefillShippingAddress = pPrefillShippingAddress;
  }

  /**
   * @return prefill billing address.
   */
  public boolean getPrefillBillingAddress() {
    return mPrefillBillingAddress;
  }

  /**
   * @param pPrefillBillingAddress - prefill billing address.
   */
  public void setPrefillBillingAddress(boolean pPrefillBillingAddress) {
    mPrefillBillingAddress = pPrefillBillingAddress;
  }

  /**
   * @return prefill credit card.
   */
  public boolean getPrefillCreditCard() {
    return mPrefillCreditCard;
  }

  /**
   * @param pPrefillCreditCard - prefilled credit card.
   */
  public void setPrefillCreditCard(boolean pPrefillCreditCard) {
    mPrefillCreditCard = pPrefillCreditCard;
  }

  /**
   * @return he checkout option selected at the start of checkout.
   */
  public String getCheckoutOption() {
    return mCheckoutOption;
  }

  /**
   * @param pCheckoutOption - the checkout option selected at the start of checkout.
   */
  public void setCheckoutOption(String pCheckoutOption) {
    mCheckoutOption = pCheckoutOption;
  }
}
