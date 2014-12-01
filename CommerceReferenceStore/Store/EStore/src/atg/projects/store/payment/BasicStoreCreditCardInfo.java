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
package atg.projects.store.payment;

import atg.commerce.order.Order;

import atg.core.util.Address;

import atg.payment.creditcard.GenericCreditCardInfo;

import atg.projects.store.payment.creditcard.StoreCreditCardInfo;


/**
 * This is a non-repository item backed implementation of the StoreCreditCardInfo
 * interface. This class is used to validate credit card information that is
 * entered in profile maintenance.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/payment/BasicStoreCreditCardInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class BasicStoreCreditCardInfo extends GenericCreditCardInfo implements StoreCreditCardInfo {
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/payment/BasicStoreCreditCardInfo.java#2 $$Change: 651448 $";

  /**
   * Credit card number.
   */
  protected String mCreditCardNumber = null;

  /**
   * Expiration month.
   */
  protected String mExpirationMonth = null;

  /**
   * Expiration day of month.
   */
  protected String mExpirationDayOfMonth = null;

  /**
   * Expiration year.
   */
  protected String mExpirationYear = null;

  /**
   * Credit card type.
   */
  protected String mCreditCardType = null;

  /**
   * Amount.
   */
  protected double mAmount = 0;

  /**
   * Paymant id.
   */
  protected String mPaymentId = null;

  /**
   * Currency code.
   */
  protected String mCurrencyCode = null;

  /**
   * Billing address.
   */
  protected Address mBillingAddress = null;

  /**
   * Card verification number.
   */
  protected String mCardVerificationNumber = null;

  /**
   * @return the credit card number.
   */
  public String getCreditCardNumber() {
    return mCreditCardNumber;
  }

  /**
   * @param pCreditCardNumber - the credit card number to set.
   */
  public void setCreditCardNumber(String pCreditCardNumber) {
    mCreditCardNumber = pCreditCardNumber;
  }

  /**
   * @return the credit card type.
   */
  public String getCreditCardType() {
    return mCreditCardType;
  }

  /**
   * @param pCreditCardType - the credit card type to set.
   */
  public void setCreditCardType(String pCreditCardType) {
    mCreditCardType = pCreditCardType;
  }

  /**
   * @return the expiration month.
   */
  public String getExpirationMonth() {
    return mExpirationMonth;
  }

  /**
   * @param pExpirationMonth - the expiration month to set.
   */
  public void setExpirationMonth(String pExpirationMonth) {
    mExpirationMonth = pExpirationMonth;
  }

  /**
   * @return the expiration year.
   */
  public String getExpirationYear() {
    return mExpirationYear;
  }

  /**
   * @param pExpirationYear - the expiration year to set.
   */
  public void setExpirationYear(String pExpirationYear) {
    mExpirationYear = pExpirationYear;
  }

  /**
   * @return order.
   */
  public Order getOrder() {
    return null;
  }

  /**
   * @return the card verification number.
   */
  public String getCardVerificationNumber() {
    return mCardVerificationNumber;
  }

  /**
   * @param pCardVerificationNumber - the card verification number to set.
   */
  public void setCardVerificationNumber(String pCardVerificationNumber) {
    mCardVerificationNumber = pCardVerificationNumber;
  }

  /**
   * @return the amount.
   */
  public double getAmount() {
    return mAmount;
  }

  /**
   * @param pAmount - the amount to set.
   */
  public void setAmount(double pAmount) {
    mAmount = pAmount;
  }

  /**
   * @return the expiration day of month.
   */
  public String getExpirationDayOfMonth() {
    return mExpirationDayOfMonth;
  }

  /**
   * @param pExpirationDayOfMonth - the expiration day of month to set.
   */
  public void setExpirationDayOfMonth(String pExpirationDayOfMonth) {
    mExpirationDayOfMonth = pExpirationDayOfMonth;
  }

  /**
   * @return the payment id.
   */
  public String getPaymentId() {
    return mPaymentId;
  }

  /**
   * @param pPaymentId - the payment id to set.
   */
  public void setPaymentId(String pPaymentId) {
    mPaymentId = pPaymentId;
  }

  /**
   * @return the currency code.
   */
  public String getCurrencyCode() {
    return mCurrencyCode;
  }

  /**
   * @param pCurrencyCode - the currency code to set.
   */
  public void setCurrencyCode(String pCurrencyCode) {
    mCurrencyCode = pCurrencyCode;
  }

  /**
   * @return the billing address.
   */
  public Address getBillingAddress() {
    return mBillingAddress;
  }

  /**
   * @param pBillingAddress - the billing address to set.
   */
  public void setBillingAddress(Address pBillingAddress) {
    mBillingAddress = pBillingAddress;
  }
}
