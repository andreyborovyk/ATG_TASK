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

package atg.commerce.order.purchase;

import atg.commerce.order.*;
import java.io.Serializable;

/**
 * This helper object represents the association between a CommerceIdentifier and its
 * payment information.
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CommerceIdentifierPaymentInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class CommerceIdentifierPaymentInfo implements Serializable
{
  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CommerceIdentifierPaymentInfo.java#2 $$Change: 651448 $";

  //-------------------------------------
  // properties
  //-------------------------------------

  //---------------------------------------------------------------------------
  // property: CommerceIdentifier
  //---------------------------------------------------------------------------
  CommerceIdentifier mCommerceIdentifier;

  /**
   * Set the CommerceIdentifier property.
   * @param pCommerceIdentifier a <code>CommerceIdentifier</code> value
   */
  public void setCommerceIdentifier(CommerceIdentifier pCommerceIdentifier) {
    mCommerceIdentifier = pCommerceIdentifier;
  }

  /**
   * Return the CommerceIdentifier property.
   * @return a <code>CommerceIdentifier</code> value
   */
  public CommerceIdentifier getCommerceIdentifier() {
    return mCommerceIdentifier;
  }

  //---------------------------------------------------------------------------
  // property: RelationshipType
  //---------------------------------------------------------------------------
  String mRelationshipType;

  /**
   * Set the RelationshipType property.
   * @param pRelationshipType a <code>String</code> value
   */
  public void setRelationshipType(String pRelationshipType) {
    mRelationshipType = pRelationshipType;
  }

  /**
   * Return the RelationshipType property.
   * @return a <code>String</code> value
   */
  public String getRelationshipType() {
    return mRelationshipType;
  }

  //---------------------------------------------------------------------------
  // property: PaymentMethod
  //---------------------------------------------------------------------------
  String mPaymentMethod;

  /**
   * Set the PaymentMethod property.
   * @param pPaymentMethod a <code>String</code> value
   */
  public void setPaymentMethod(String pPaymentMethod) {
    mPaymentMethod = pPaymentMethod;
  }

  /**
   * Return the PaymentMethod property.
   * @return a <code>String</code> value
   */
  public String getPaymentMethod() {
    return mPaymentMethod;
  }

  //---------------------------------------------------------------------------
  // property: Amount
  //---------------------------------------------------------------------------
  double mAmount;

  /**
   * Set the Amount property.
   * @param pAmount a <code>double</code> value
   */
  public void setAmount(double pAmount) {
    mAmount = pAmount;
  }

  /**
   * Return the Amount property.
   * @return a <code>double</code> value
   */
  public double getAmount() {
    return mAmount;
  }

  //---------------------------------------------------------------------------
  // property: Quantity
  //---------------------------------------------------------------------------
  long mQuantity;

  /**
   * Set the Quantity property.
   * @param pQuantity a <code>long</code> value
   */
  public void setQuantity(long pQuantity) {
    mQuantity = pQuantity;
  }

  /**
   * Return the Quantity property.
   * @return a <code>long</code> value
   */
  public long getQuantity() {
    return mQuantity;
  }

  //---------------------------------------------------------------------------
  // property: SplitPaymentMethod
  //---------------------------------------------------------------------------
  String mSplitPaymentMethod;

  /**
   * Set the SplitPaymentMethod property.
   * @param pSplitPaymentMethod a <code>String</code> value
   */
  public void setSplitPaymentMethod(String pSplitPaymentMethod) {
    mSplitPaymentMethod = pSplitPaymentMethod;
  }

  /**
   * Return the SplitPaymentMethod property.
   * @return a <code>String</code> value
   */
  public String getSplitPaymentMethod() {
    return mSplitPaymentMethod;
  }

  //---------------------------------------------------------------------------
  // property: SplitAmount
  //---------------------------------------------------------------------------
  double mSplitAmount;

  /**
   * Set the SplitAmount property.
   * @param pSplitAmount a <code>double</code> value
   */
  public void setSplitAmount(double pSplitAmount) {
    mSplitAmount = pSplitAmount;
  }

  /**
   * Return the SplitAmount property.
   * @return a <code>double</code> value
   */
  public double getSplitAmount() {
    return mSplitAmount;
  }

  //---------------------------------------------------------------------------
  // property: SplitQuantity
  //---------------------------------------------------------------------------
  long mSplitQuantity;

  /**
   * Set the SplitQuantity property.
   * @param pSplitQuantity a <code>long</code> value
   */
  public void setSplitQuantity(long pSplitQuantity) {
    mSplitQuantity = pSplitQuantity;
  }

  /**
   * Return the SplitQuantity property.
   * @return a <code>long</code> value
   */
  public long getSplitQuantity() {
    return mSplitQuantity;
  }

  //-------------------------------------
  // Methods
  //-------------------------------------

  /**
   * <code>getAmountType</code> is used to return the amount relationship type String
   * based on the CommerceIdentifier type.
   *
   * @return a <code>String</code> value
   */
  public String getAmountType () {
    return null;
  }

  /**
   * <code>getAmountRemainingType</code> is used to return the amount remaining relationship
   * type String based on the CommerceIdentifier type.
   *
   * @return a <code>String</code> value
   */
  public String getAmountRemainingType () {
    return null;
  }

  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Creates a new <code>CommerceIdentifierPaymentInfo</code> instance.
   *
   */
  public CommerceIdentifierPaymentInfo () {}

  //-------------------------------------
  /**
   * Method that renders the general order information in a readable string format
   */
  public String toString() {
    StringBuilder sb = new StringBuilder("CommerceIdentifierPaymentInfo[ ");
    sb.append("commerceIdentifier:").append(getCommerceIdentifier()).append("; ");
    sb.append("relationshipType:").append(getRelationshipType()).append("; ");
    sb.append("paymentMethod:").append(getPaymentMethod()).append("; ");
    sb.append("amount:").append(getAmount()).append("; ");
    sb.append("Credit Card verification number:").append(getCreditCardVerificationNumber()).append("; ");
    sb.append("quantity:").append(getQuantity()).append("; ");
    sb.append("splitPaymentMethod:").append(getSplitPaymentMethod()).append("; ");
    sb.append("splitAmount:").append(getSplitAmount()).append("; ");
    sb.append("splitQuantity:").append(getSplitQuantity()).append("; ");
    sb.append("]");

    return sb.toString();
  }

  String mCreditCardVerificationNumber;

  /**
   *
   * Many credit cards have a card verification number printed, not embossed, on the card.   This number is never
   * transferred during card swipes and should be known only by the cardholder.  Each card association has
   * its own name for this number. Visa calls it the Card Verification Value (CVV2), and
   * MasterCard calls it the Card Validation Code (CVC2). Visa and MasterCard print the number
   * on the back of the card. American Express and Discover call it the Card
   * Identification Digits (CID).
   *
   * This number is 3-4 digits. Amex uses 4 digit numbers and other credit cards uses 3 digits.
   *
   * This property is marked as transient. This property should not be allowed to serialize.
   *
   * @return Returns the creditCardVerificationNumber.
   */
  public String getCreditCardVerificationNumber() {
    return mCreditCardVerificationNumber;
  }

  /**
   * Sets the creditCardVerificationNumber
   *
   * Many credit cards have a card verification number printed, not embossed, on the card.   This number is never
   * transferred during card swipes and should be known only by the cardholder.  Each card association has
   * its own name for this number. Visa calls it the Card Verification Value (CVV2), and
   * MasterCard calls it the Card Validation Code (CVC2). Visa and MasterCard print the number
   * on the back of the card. American Express and Discover call it the Card
   * Identification Digits (CID).
   *
   * This number is 3-4 digits. Amex uses 4 digit numbers and other credit cards uses 3 digits.
   *
   * This property is marked as transient. This property should not be allowed to serialize.
   *
   * @param pCreditCardVerificationNumber The creditCardVerificationNumber to set.
   *
   */
  public void setCreditCardVerificationNumber(String pCreditCardVerificationNumber) {
    mCreditCardVerificationNumber = pCreditCardVerificationNumber;
  }

}
