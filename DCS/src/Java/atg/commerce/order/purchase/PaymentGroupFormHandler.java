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
import atg.repository.*;
import atg.beans.*;
import atg.droplet.*;
import atg.servlet.*;
import atg.commerce.CommerceException;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.commerce.pricing.*;
import atg.service.pipeline.*;
import java.io.IOException;
import javax.servlet.ServletException;
import java.util.*;
import javax.transaction.*;
import java.lang.*;
import atg.core.util.StringUtils;
import java.math.*;

/**
 * <p>The <code>PaymentGroupFormHandler</code> is used to associate PaymentGroups with the various
 * Order pieces. This component is used during the Order checkout process, and any
 * Order successfully processed by the PaymentGroupFormHandler is ready for the next
 * checkout phase, which may be confirmation.
 *
 * <p>The PaymentGroupFormHandler is composed of the following containers:
 *
 * <br>PaymentGroupMapContainer - container for the user's authorized PaymentGroups
 *
 * <br>CommerceIdentifierPaymentInfoContainer - container for the user's CommerceIdentifierPaymentInfo
 * objects for a particular Order's CommerceIdentifiers
 *
 * <p>There are 3 main handler methods in the PaymentGroupFormHandler, handleSplitPaymentInfos,
 * handleApplyPaymentGroups and handleSpecifyDefaultPaymentGroup:
 *
 * <ol>
 * <li>handleSplitPaymentInfos splits extra CommerceIdentifierPaymentInfo objects by
 * amount. In a form the user might request to split $50 of an original CommerceIdentifier amount
 * of $100 onto a separate payment method. This will create a separate CommerceIdentifierPaymentInfo
 * object, and adjust the amount of both the original and the new CommerceIdentifierPaymentInfo
 * objects to add up to the original CommerceIdentifier total amount.
 *
 * <li>handleApplyPaymentGroups is used when the CommerceIdentifierPaymentInfo associations created
 * by the user are ready to be applied to the current Order. The associations are scrutinized
 * and the appropriate business methods are called in the OrderManager family. PaymentGroup
 * validation takes place, via a configurable Pipeline chain.
 *
 * <li>handleSpecifyDefaultPaymentGroup is used to specify a particular default PaymentGroup
 * to be used for CommerceIdentifiers that aren't explicitly assigned separate PaymentGroups.
 *
 * </ol>
 *
 * <p>In order to conveniently manipulate PaymentGroups and CommerceIdentifierPaymentInfo Lists,
 * you can set the <code>ListId</code> and <code>PaymentGroupId</code> properties. This will
 * automatically expose the corresponding PaymentGroup and List in the <code>CurrentList</code>
 * and <code>CurrentPaymentGroup</code> properties.
 *
 *
 * @beaninfo
 *   description: A formhandler which provides multiple PaymentGroup support.
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/PaymentGroupFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.droplet.GenericFormHandler
 * @see atg.commerce.order.purchase.PurchaseProcessFormHandler
 */
public class PaymentGroupFormHandler
  extends PurchaseProcessFormHandler
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/PaymentGroupFormHandler.java#2 $$Change: 651448 $";

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  public static final String MSG_NO_DEFAULT_PAYMENT_GROUP = "noDefaultPaymentGroup";
  public static final String MSG_INVALID_PAYMENT_SPLIT_NUMBER = "invalidPaymentSplitNumber";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------
  boolean mAllowPaymentGroupsWithZeroAmount;

  /**
   * This Flag is added to support backward compatibiity. Just in case if someone wants to
   * allow payment groups with zero amount, they can turn on this flag.
   * If this flag is set to false, then the payment groups with zero amount is not allowed to be added
   * to the order except if the relationships of the cipis are one of the following:
   * <OL>
   * <LI>PAYMENTAMOUNTREMAINING</LI>
   * <LI>SHIPPINGAMOUNTREMAINING</LI>
   * <LI>ORDERAMOUNTREMAINING</LI>
   * <LI>TAXAMOUNTREMAINING</LI>
   * </OL>
   *
   * @return the allowPaymentGroupsWithZeroAmount
   */
  public boolean isAllowPaymentGroupsWithZeroAmount() {
    return mAllowPaymentGroupsWithZeroAmount;
  }

  /**
   *
   * This Flag is added to support backward compatibiity. Just in case if someone wants to
   * allow payment groups with zero amount, they can turn on this flag.
   *
   * If this flag is set to false, then the payment groups with zero amount is not allowed to be added
   * to the order except if the relationships of the cipis are one of the following:
   * <OL>
   * <LI>PAYMENTAMOUNTREMAINING</LI>
   * <LI>SHIPPINGAMOUNTREMAINING</LI>
   * <LI>ORDERAMOUNTREMAINING</LI>
   * <LI>TAXAMOUNTREMAINING</LI>
   * </OL>
   * @param pAllowPaymentGroupsWithZeroAmount The allowPaymentGroupsWithZeroAmount to set.
   */
  public void setAllowPaymentGroupsWithZeroAmount(
      boolean pAllowPaymentGroupsWithZeroAmount) {
    mAllowPaymentGroupsWithZeroAmount = pAllowPaymentGroupsWithZeroAmount;
  }

  //---------------------------------------------------------------------------
  // property: ListId
  //---------------------------------------------------------------------------
  String mListId;

  /**
   * Set the ListId property.
   * @param pListId a <code>String</code> value
   * @beaninfo description: Specifies the id of the CommerceIdentifierPaymentInto List to be referenced by the <code>CurrentList</code> property.
   */
  public void setListId(String pListId) {
    mListId = pListId;
    setCurrentList(getCommerceIdentifierPaymentInfoContainer().getCommerceIdentifierPaymentInfos(pListId));
  }

  /**
   * Return the ListId property.
   * @return a <code>String</code> value
   */
  public String getListId() {
    return mListId;
  }

  //---------------------------------------------------------------------------
  // property: CurrentList
  //---------------------------------------------------------------------------
  List mCurrentList = null;

  /**
   * Set the CurrentList property.
   * @param pCurrentList a <code>List</code> value
   * @beaninfo description: The current CommerceIdentifierPaymentInto List, this is set by specifying the <code>ListId</code> property.
   */
  public void setCurrentList(List pCurrentList) {
    mCurrentList = pCurrentList;
  }

  /**
   * Return the CurrentList property.
   * @return a <code>List</code> value
   */
  public List getCurrentList() {
    return mCurrentList;
  }

  //---------------------------------------------------------------------------
  // property: PaymentGroupId
  //---------------------------------------------------------------------------
  String mPaymentGroupId;

  /**
   * Set the PaymentGroupId property.
   * @param pPaymentGroupId a <code>String</code> value
   * @beaninfo description: Specifies the id of the PaymentGroup to be referenced by the <code>CurrentPaymentGroup</code> property.
   */
  public void setPaymentGroupId(String pPaymentGroupId) {
    mPaymentGroupId = pPaymentGroupId;
    setCurrentPaymentGroup(getPaymentGroup(pPaymentGroupId));
  }

  /**
   * Return the PaymentGroupId property.
   * @return a <code>String</code> value
   */
  public String getPaymentGroupId() {
    return mPaymentGroupId;
  }

  //---------------------------------------------------------------------------
  // property: CurrentPaymentGroup
  //---------------------------------------------------------------------------
  PaymentGroup mCurrentPaymentGroup = null;

  /**
   * Set the CurrentPaymentGroup property.
   * @param pCurrentPaymentGroup a <code>PaymentGroup</code> value
   * @beaninfo description: The current PaymentGroup, this is set by specifying the <code>PaymentGroupId</code> property.
   */
  public void setCurrentPaymentGroup(PaymentGroup pCurrentPaymentGroup) {
    mCurrentPaymentGroup = pCurrentPaymentGroup;
  }

  /**
   * Return the CurrentPaymentGroup property.
   * @return a <code>PaymentGroup</code> value
   */
  public PaymentGroup getCurrentPaymentGroup() {
    return mCurrentPaymentGroup;
  }

  //---------------------------------------------------------------------------
  // property: ApplyPaymentGroupsSuccessURL
  //---------------------------------------------------------------------------
  String mApplyPaymentGroupsSuccessURL;

  /**
   * Set the ApplyPaymentGroupsSuccessURL property.
   * @param pApplyPaymentGroupsSuccessURL a <code>String</code> value
   * @beaninfo description:  Success URL for redirection.
   */
  public void setApplyPaymentGroupsSuccessURL(String pApplyPaymentGroupsSuccessURL) {
    mApplyPaymentGroupsSuccessURL = pApplyPaymentGroupsSuccessURL;
  }

  /**
   * Return the ApplyPaymentGroupsSuccessURL property.
   * @return a <code>String</code> value
   */
  public String getApplyPaymentGroupsSuccessURL() {
    return mApplyPaymentGroupsSuccessURL;
  }

  //---------------------------------------------------------------------------
  // property: ApplyPaymentGroupsErrorURL
  //---------------------------------------------------------------------------
  String mApplyPaymentGroupsErrorURL;

  /**
   * Set the ApplyPaymentGroupsErrorURL property.
   * @param pApplyPaymentGroupsErrorURL a <code>String</code> value
   * @beaninfo description:  Failure URL for redirection.
   */
  public void setApplyPaymentGroupsErrorURL(String pApplyPaymentGroupsErrorURL) {
    mApplyPaymentGroupsErrorURL = pApplyPaymentGroupsErrorURL;
  }

  /**
   * Return the ApplyPaymentGroupsErrorURL property.
   * @return a <code>String</code> value
   */
  public String getApplyPaymentGroupsErrorURL() {
    return mApplyPaymentGroupsErrorURL;
  }

  //---------------------------------------------------------------------------
  // property: SplitPaymentInfosSuccessURL
  //---------------------------------------------------------------------------
  String mSplitPaymentInfosSuccessURL;

  /**
   * Set the SplitPaymentInfosSuccessURL property.
   * @param pSplitPaymentInfosSuccessURL a <code>String</code> value
   * @beaninfo description: Success URL for redirection.
   */
  public void setSplitPaymentInfosSuccessURL(String pSplitPaymentInfosSuccessURL) {
    mSplitPaymentInfosSuccessURL = pSplitPaymentInfosSuccessURL;
  }

  /**
   * Return the SplitPaymentInfosSuccessURL property.
   * @return a <code>String</code> value
   */
  public String getSplitPaymentInfosSuccessURL() {
    return mSplitPaymentInfosSuccessURL;
  }

  //---------------------------------------------------------------------------
  // property: SplitPaymentInfosErrorURL
  //---------------------------------------------------------------------------
  String mSplitPaymentInfosErrorURL;

  /**
   * Set the SplitPaymentInfosErrorURL property.
   * @param pSplitPaymentInfosErrorURL a <code>String</code> value
   * @beaninfo description: Failure URL for redirection.
   */
  public void setSplitPaymentInfosErrorURL(String pSplitPaymentInfosErrorURL) {
    mSplitPaymentInfosErrorURL = pSplitPaymentInfosErrorURL;
  }

  /**
   * Return the SplitPaymentInfosErrorURL property.
   * @return a <code>String</code> value
   */
  public String getSplitPaymentInfosErrorURL() {
    return mSplitPaymentInfosErrorURL;
  }

  //---------------------------------------------------------------------------
  // property: SpecifyDefaultPaymentGroupSuccessURL
  //---------------------------------------------------------------------------
  String mSpecifyDefaultPaymentGroupSuccessURL;

  /**
   * Set the SpecifyDefaultPaymentGroupSuccessURL property.
   * @param pSpecifyDefaultPaymentGroupSuccessURL a <code>String</code> value
   * @beaninfo description: Success URL for redirection.
   */
  public void setSpecifyDefaultPaymentGroupSuccessURL(String pSpecifyDefaultPaymentGroupSuccessURL) {
    mSpecifyDefaultPaymentGroupSuccessURL = pSpecifyDefaultPaymentGroupSuccessURL;
  }

  /**
   * Return the SpecifyDefaultPaymentGroupSuccessURL property.
   * @return a <code>String</code> value
   */
  public String getSpecifyDefaultPaymentGroupSuccessURL() {
    return mSpecifyDefaultPaymentGroupSuccessURL;
  }

  //---------------------------------------------------------------------------
  // property: SpecifyDefaultPaymentGroupErrorURL
  //---------------------------------------------------------------------------
  String mSpecifyDefaultPaymentGroupErrorURL;

  /**
   * Set the SpecifyDefaultPaymentGroupErrorURL property.
   * @param pSpecifyDefaultPaymentGroupErrorURL a <code>String</code> value
   * @beaninfo description: Error URL for redirection.
   */
  public void setSpecifyDefaultPaymentGroupErrorURL(String pSpecifyDefaultPaymentGroupErrorURL) {
    mSpecifyDefaultPaymentGroupErrorURL = pSpecifyDefaultPaymentGroupErrorURL;
  }

  /**
   * Return the SpecifyDefaultPaymentGroupErrorURL property.
   * @return a <code>String</code> value
   */
  public String getSpecifyDefaultPaymentGroupErrorURL() {
    return mSpecifyDefaultPaymentGroupErrorURL;
  }

  //---------------------------------------------------------------------------
  // property: ValidatePaymentGroupsChainId
  //---------------------------------------------------------------------------
  String mValidatePaymentGroupsChainId;

  /**
   * Set the ValidatePaymentGroupsChainId property.
   * @param pValidatePaymentGroupsChainId a <code>String</code> value
   * @beaninfo description:  ChainId used to validate PaymentGroups.
   */
  public void setValidatePaymentGroupsChainId(String pValidatePaymentGroupsChainId) {
    mValidatePaymentGroupsChainId = pValidatePaymentGroupsChainId;
  }

  /**
   * Return the ValidatePaymentGroupsChainId property.
   * @return a <code>String</code> value
   */
  public String getValidatePaymentGroupsChainId() {
    return mValidatePaymentGroupsChainId;
  }

  //---------------------------------------------------------------------------
  // property: ApplyDefaultPaymentGroup
  //---------------------------------------------------------------------------
  boolean mApplyDefaultPaymentGroup;

  /**
   * Set the ApplyDefaultPaymentGroup property.
   * @param pApplyDefaultPaymentGroup a <code>boolean</code> value
   * @beaninfo description: Should the default PaymentGroup apply?
   */
  public void setApplyDefaultPaymentGroup(boolean pApplyDefaultPaymentGroup) {
    mApplyDefaultPaymentGroup = pApplyDefaultPaymentGroup;
  }

  /**
   * Return the ApplyDefaultPaymentGroup property.
   * @return a <code>boolean</code> value
   */
  public boolean isApplyDefaultPaymentGroup() {
    return mApplyDefaultPaymentGroup;
  }

  //---------------------------------------------------------------------------
  // property: DefaultPaymentGroupName
  //---------------------------------------------------------------------------
  String mDefaultPaymentGroupName;

  /**
   * Set the DefaultPaymentGroupName property.
   * @param pDefaultPaymentGroupName a <code>String</code> value
   * @beaninfo description: The name of the default PaymentGroup.
   */
  public void setDefaultPaymentGroupName(String pDefaultPaymentGroupName) {
    mDefaultPaymentGroupName = pDefaultPaymentGroupName;
  }

  /**
   * Return the DefaultPaymentGroupName property.
   * @return a <code>String</code> value
   */
  public String getDefaultPaymentGroupName() {
    return mDefaultPaymentGroupName;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>PaymentGroupFormHandler</code> instance.
   *
   */
  public PaymentGroupFormHandler() {}


  //-------------------------------------
  // splitPaymentInfos code
  //-------------------------------------

  /**
   * <code>handleSplitPaymentInfos</code> is used when the user is ready to
   * split a particular CommerceIdentifierPaymentInfo by amount.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleSplitPaymentInfos(DynamoHttpServletRequest pRequest,
                                          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "PaymentGroupOrderFormHandler.handleSplitPaymentInfos";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
  tr = ensureTransaction();

  if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

  if (!checkFormRedirect(null, getSplitPaymentInfosErrorURL(), pRequest, pResponse))
    return false;

  preSplitPaymentInfos(pRequest, pResponse);

  if (!checkFormRedirect(null, getSplitPaymentInfosErrorURL(), pRequest, pResponse))
    return false;

  splitPaymentInfos(pRequest, pResponse);

  if (!checkFormRedirect(null, getSplitPaymentInfosErrorURL(), pRequest, pResponse))
    return false;

  postSplitPaymentInfos(pRequest, pResponse);

  return checkFormRedirect (getSplitPaymentInfosSuccessURL(),
          getSplitPaymentInfosErrorURL(), pRequest, pResponse);
      }
      finally {
  if (tr != null) commitTransaction(tr);
  if (rrm != null)
    rrm.removeRequestEntry(myHandleMethod);
      }
    }
    else {
      return false;
    }

  }

  /**
   * <code>preSplitPaymentInfos</code> is for work that must happen before
   * the CommerceIdentifierPaymentInfos are split.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preSplitPaymentInfos(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>postSplitPaymentInfos</code> is for work that must happen after
   * the CommerceIdentifierPaymentInfos are split.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postSplitPaymentInfos(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>splitPaymentInfos</code> gets CommerceIdentifierPaymentInfos ready
   * for split.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void splitPaymentInfos(DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    CommerceIdentifierPaymentInfoContainer container = getCommerceIdentifierPaymentInfoContainer();
    List commerceIdentifierPaymentInfos = container.getAllCommerceIdentifierPaymentInfos();
    Iterator iter;
    CommerceIdentifierPaymentInfo cipi;

    // split commerceIdentifierPaymentInfos
    iter = commerceIdentifierPaymentInfos.listIterator();
    while (iter.hasNext()) {
      cipi = (CommerceIdentifierPaymentInfo) iter.next();
      splitCommerceIdentifierPaymentInfo (cipi, pRequest, pResponse);
    }
  }

  /**
   * <code>splitCommerceIdentifierPaymentInfo</code> determines which CommerceIdentifierPaymentInfo
   * objects need to be split, and in what manner. This relies on the
   * CommerceIdentifierPaymentInfo.SplitAmount property.
   *
   * @param pCommerceIdentifierPaymentInfo a <code>CommerceIdentifierPaymentInfo</code> value
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void splitCommerceIdentifierPaymentInfo(CommerceIdentifierPaymentInfo pCommerceIdentifierPaymentInfo,
                                                    DynamoHttpServletRequest pRequest,
                                                    DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    double splitAmount = pCommerceIdentifierPaymentInfo.getSplitAmount();
    double amount = pCommerceIdentifierPaymentInfo.getAmount();

    if (splitAmount > 0) {
      if (splitAmount > amount) {
        String msg = formatUserMessage(MSG_INVALID_PAYMENT_SPLIT_NUMBER,pRequest,pResponse);
        String propertyPath = generatePropertyPath("splitCommerceIdentifierPaymentInfo()");
        addFormException(new DropletFormException(msg, propertyPath, MSG_INVALID_PAYMENT_SPLIT_NUMBER));
      } else {
        splitCommerceIdentifierPaymentInfoByAmount (pCommerceIdentifierPaymentInfo, splitAmount);
      }
    }
    else if (isLoggingDebug()) {
      logDebug("splitCommerceIdentifierPaymentInfo skipping " + pCommerceIdentifierPaymentInfo.getPaymentMethod() +
               ", splitAmount=" + splitAmount);
    }
  }

  /**
   * <code>splitCommerceIdentifierPaymentInfoByAmount</code> splits a CommerceIdentifierPaymentInfo
   * by amount. This creates a new CommerceIdentifierPaymentInfo, adjusts properties of both
   * the original and the new object, and adds the new object to the
   * CommerceIdentifierPaymentInfoContainer.
   *
   * @param pCommerceIdentifierPaymentInfo a <code>CommerceIdentifierPaymentInfo</code> value
   * @param pSplitAmount a <code>double</code> value
   */
  protected void splitCommerceIdentifierPaymentInfoByAmount (CommerceIdentifierPaymentInfo pCommerceIdentifierPaymentInfo,
                                                             double pSplitAmount) {
    // get two object references ready
    CommerceIdentifierPaymentInfo currentInfo = pCommerceIdentifierPaymentInfo;
    CommerceIdentifierPaymentInfo newInfo = createSpecificPaymentInfo(pCommerceIdentifierPaymentInfo);

    // now we need to figure out the new amounts

    // double based arithmetic for amount requires using BigDecimals; the scale of
    // a BigDecimal refers to the number of digits to the right of the decimal point

    BigDecimal amountBD = (new BigDecimal(currentInfo.getAmount())).setScale(2, BigDecimal.ROUND_HALF_UP);
    BigDecimal splitAmountBD = (new BigDecimal(pSplitAmount)).setScale(2, BigDecimal.ROUND_HALF_UP);
    double currentAmount = splitAmountBD.doubleValue();

    // scale() = 2; these are the results we're interested in
    BigDecimal newCurrentAmountBD = amountBD.subtract(splitAmountBD);
    double newCurrentAmount = newCurrentAmountBD.doubleValue();

    // adjust new CommerceIdentifierPaymentInfo based on the split properties
    newInfo.setCommerceIdentifier(currentInfo.getCommerceIdentifier());
    newInfo.setRelationshipType(newInfo.getAmountType());
    newInfo.setPaymentMethod(currentInfo.getSplitPaymentMethod());
    newInfo.setAmount(currentAmount);
    // leave quantity as 0 so that merging works

    // adjust properties on current CommerceIdentifierPaymentInfo
    currentInfo.setAmount(newCurrentAmount);
    currentInfo.setSplitPaymentMethod(null);
    currentInfo.setSplitAmount(0.0);
    if (isLoggingDebug()) {
      logDebug("splitCommerceIdentifierPaymentInfoByAmount adjusted amount in cipi " + currentInfo);
    }

    // now add new CommerceIdentifierPaymentInfo to container
    String id = currentInfo.getCommerceIdentifier().getId();
    CommerceIdentifierPaymentInfoContainer container = getCommerceIdentifierPaymentInfoContainer();
    container.addCommerceIdentifierPaymentInfo(id, newInfo);
    if (isLoggingDebug()) {
      logDebug("splitCommerceIdentifierPaymentInfoByAmount added cipi " + newInfo);
    }

  }

  /**
   * <code>splitCommerceIdentifierPaymentInfoByQuantity</code> splits a CommerceIdentifierPaymentInfo
   * by quantity. This creates a new CommerceIdentifierPaymentInfo, adjusts properties of both
   * the original and the new object, and adds the new object to the
   * CommerceIdentifierPaymentInfoContainer.
   *
   * @param pCommerceIdentifierPaymentInfo a <code>CommerceIdentifierPaymentInfo</code> value
   * @param pSplitQuantity a <code>long</code> value
   *
  protected void splitCommerceIdentifierPaymentInfoByQuantity (CommerceIdentifierPaymentInfo pCommerceIdentifierPaymentInfo,
                                                               long pSplitQuantity) {
    // get two object references ready
    CommerceIdentifierPaymentInfo currentInfo = pCommerceIdentifierPaymentInfo;
    CommerceIdentifierPaymentInfo newInfo = createSpecificPaymentInfo(pCommerceIdentifierPaymentInfo);

    // now we need to figure out the new amount and quantities

    // long based arithmetic for quantity is simple
    long newCurrentQuantity = currentInfo.getQuantity() - pSplitQuantity;

    // double based arithmetic for amount requires using BigDecimals; the scale of
    // a BigDecimal refers to the number of digits to the right of the decimal point

    // scale() = 0
    BigDecimal splitQuantityBD = BigDecimal.valueOf(pSplitQuantity);
    BigDecimal quantityBD = BigDecimal.valueOf(currentInfo.getQuantity());

    // scale() = 3
    // to be safe we want this to be at least as big as the scale of the result we're interested in, namely 2
    BigDecimal quantityRatioBD = splitQuantityBD.divide(quantityBD, 3, BigDecimal.ROUND_HALF_UP);

    // scale() = X, where X is the number of digits to right of decimal point in amount
    BigDecimal amountBD = new BigDecimal(currentInfo.getAmount());

    // scale() = X + 3
    BigDecimal newAmountBD = quantityRatioBD.multiply(amountBD);

    // scale() = X + 3
    BigDecimal currentAmountBD = BigDecimal.valueOf(1).subtract(quantityRatioBD).multiply(amountBD);

    // scale() = 2; finally these are the results we're interested in
    double newAmount = newAmountBD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    double currentAmount = currentAmountBD.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

    // adjust new CommerceIdentifierPaymentInfo based on the split properties
    newInfo.setCommerceIdentifier(currentInfo.getCommerceIdentifier());
    newInfo.setRelationshipType(newInfo.getAmountType());
    newInfo.setPaymentMethod(currentInfo.getSplitPaymentMethod());
    newInfo.setQuantity(pSplitQuantity);
    newInfo.setAmount(newAmount);

    // adjust properties on current CommerceIdentifierPaymentInfo
    currentInfo.setAmount(currentAmount);
    currentInfo.setSplitPaymentMethod(null);
    currentInfo.setSplitAmount(0.0);
    currentInfo.setQuantity(newCurrentQuantity);
    if (isLoggingDebug()) {
      logDebug("splitCommerceIdentifierPaymentInfoByQuantiry adjusted amount and quantity in cipi " + currentInfo);
    }

    // now add new CommerceIdentifierPaymentInfo to container
    String id = currentInfo.getCommerceIdentifier().getId();
    CommerceIdentifierPaymentInfoContainer container = getCommerceIdentifierPaymentInfoContainer();
    container.addCommerceIdentifierPaymentInfo(id, newInfo);
    if (isLoggingDebug()) {
      logDebug("splitCommerceIdentifierPaymentInfoByQuantity added cipi " + newInfo);
    }
  }
  */

  /**
   * The <code>createSpecificPaymentInfo</code> method takes a CommerceIdentifierPaymentInfo
   * and creates a new one whose subtype matches that of the original.
   *
   * @param pCommerceIdentifierPaymentInfo a <code>CommerceIdentifierPaymentInfo</code> value
   * @return a <code>CommerceIdentifierPaymentInfo</code> value
   */
  protected CommerceIdentifierPaymentInfo createSpecificPaymentInfo(CommerceIdentifierPaymentInfo pCommerceIdentifierPaymentInfo) {
    try {
      String cipiClassName = pCommerceIdentifierPaymentInfo.getClass().getName();
      Class cipiClass = Class.forName(cipiClassName);
      return (CommerceIdentifierPaymentInfo) cipiClass.newInstance();
    } catch (ClassNotFoundException cnfe) {
      if (isLoggingError()) {
        logError(cnfe);
      }
    } catch (InstantiationException ie) {
      if (isLoggingError()) {
        logError(ie);
      }
    } catch (IllegalAccessException iae) {
      if (isLoggingError()) {
        logError(iae);
      }
    }
    return new CommerceIdentifierPaymentInfo();

  }

  //--------------------------------------------------
  // ApplyPaymentGroups Code
  //--------------------------------------------------

  /**
   * <code>handleApplyPaymentGroups</code> is used when the user has supplied the
   * payment information for this order, and is ready to proceed with the next checkout phase.
   *
   * This applies the selected PaymentGroups to the various CommerceIdentifiers,
   * applies any defaultPaymentGroup to the Order amount remaining, runs the
   * appropriate validation processor, and updates the Order.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   *
   */
  public boolean handleApplyPaymentGroups(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {

    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "PaymentGroupOrderFormHandler.handleApplyPaymentGroups";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
  tr = ensureTransaction();

  if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

  if (!checkFormRedirect(null, getApplyPaymentGroupsErrorURL(), pRequest, pResponse))
    return false;

  preApplyPaymentGroups(pRequest, pResponse);

  if (!checkFormRedirect(null, getApplyPaymentGroupsErrorURL(), pRequest, pResponse))
    return false;

  applyPaymentGroups(pRequest, pResponse);

        if (!checkFormRedirect(null, getApplyPaymentGroupsErrorURL(), pRequest, pResponse))
    return false;

        postApplyPaymentGroups(pRequest, pResponse);

  try {
      Map parameterMap = createRepriceParameterMap();
      runProcessValidatePaymentGroups(getShoppingCart().getCurrent(),getUserPricingModels(),
            getUserLocale(), getProfile(),parameterMap);

      if (!checkFormRedirect(null, getApplyPaymentGroupsErrorURL(), pRequest, pResponse))
        return false;

          getOrderManager().updateOrder(getShoppingCart().getCurrent());
  } catch (Exception exc) {
    if (isLoggingError()) {
      logError(exc);
    }
  }

  return checkFormRedirect (getApplyPaymentGroupsSuccessURL(),
          getApplyPaymentGroupsErrorURL(), pRequest, pResponse);
      }
      finally {
  if (tr != null) commitTransaction(tr);
  if (rrm != null)
    rrm.removeRequestEntry(myHandleMethod);
      }
    }
    else {
      return false;
    }
  }

  /**
   * <code>preApplyPaymentGroups</code> is for work that must happen before
   * the PaymentGroups are applied to the Order.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preApplyPaymentGroups(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>postApplyPaymentGroups</code> is for work that must happen after
   * the PaymentGroups are applied to the Order.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postApplyPaymentGroups(DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>applyPaymentGroups</code> iterates over the supplied CommerceIdentifierPaymentInfos
   * for each of the CommerceIdentifiers. Each CommerceIdentifierPaymentInfo is then used
   * to update the Order.
   *
   * Once this is done, any defaultPaymentGroup is applied to the remaining
   * Order amount remaining.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void applyPaymentGroups(DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    PaymentGroupManager pgm = getPaymentGroupManager();
    OrderManager om = getOrderManager();
    Order order = getShoppingCart().getCurrent();
    CommerceIdentifierPaymentInfoContainer container = getCommerceIdentifierPaymentInfoContainer();
    List commerceIdentifierPaymentInfos = container.getAllCommerceIdentifierPaymentInfos();
    Iterator iter;
    CommerceIdentifierPaymentInfo cipi;

    try {
      // remove any modifiable PaymentGroups from Order
      List nonModifiablePaymentGroups = pgm.getNonModifiablePaymentGroups(order);
      pgm.removeAllPaymentGroupsFromOrder(order, nonModifiablePaymentGroups);

      // apply all commerceIdentifierPaymentInfos to Order
      iter = commerceIdentifierPaymentInfos.listIterator();
      while (iter.hasNext()) {
        cipi = (CommerceIdentifierPaymentInfo) iter.next();
        if (isAllowPaymentGroupsWithZeroAmount()) {
          applyCommerceIdentifierPaymentInfo(cipi, order);
        } else {
          int type = RelationshipTypes.stringToType (cipi.getRelationshipType());
            switch (type) {
            case RelationshipTypes.PAYMENTAMOUNTREMAINING:
              applyCommerceIdentifierPaymentInfo(cipi, order);
              break;
            case RelationshipTypes.SHIPPINGAMOUNTREMAINING:
              applyCommerceIdentifierPaymentInfo(cipi, order);
              break;
            case RelationshipTypes.ORDERAMOUNTREMAINING:
              applyCommerceIdentifierPaymentInfo(cipi, order);
              break;
            case RelationshipTypes.TAXAMOUNTREMAINING:
              applyCommerceIdentifierPaymentInfo(cipi, order);
              break;
            default:
              if (cipi != null && cipi.getAmount() > 0) {
                applyCommerceIdentifierPaymentInfo(cipi, order);
              }
              break;
            }
        }
      }

      // apply defaultPaymentGroup to Order
      if (isApplyDefaultPaymentGroup())
        applyDefaultPaymentGroup(order);

      // recalculate the PaymentGroup amounts here
      pgm.recalculatePaymentGroupAmounts(order);

      List relationships = order.getRelationships();
      Iterator relIter = relationships.iterator();
      while(relIter.hasNext()) {
  Relationship rel = (Relationship) relIter.next();
        if (rel instanceof PaymentGroupOrderRelationship) {
    PaymentGroupOrderRelationship pgor = (PaymentGroupOrderRelationship) rel;
  }
      }

      //pgm.removeEmptyPaymentGroups(order);
    } catch (CommerceException exc) {
      if (isLoggingError()) {
        logError(exc);
      }
    }
    //getPaymentGroupMapContainer().setDefaultPaymentGroupName(null);
  }

  /**
   * <code>applyCommerceIdentifierPaymentInfo</code> takes the supplied payment information and
   * update the Order. This invokes business logic in the OrderManager that corresponds to the
   * RelationshipType property of the CommerceIdentifierPaymentInfo.
   *
   * @param pCommerceIdentifierPaymentInfo a <code>CommerceIdentifierPaymentInfo</code> value
   * @param order an <code>Order</code> value
   */
  protected void applyCommerceIdentifierPaymentInfo (CommerceIdentifierPaymentInfo pCommerceIdentifierPaymentInfo,
                                                      Order order)
  {
    if (isLoggingDebug()) {
      logDebug("applyCommerceIdentifierPaymentInfo " + pCommerceIdentifierPaymentInfo);
    }

    String paymentMethod = pCommerceIdentifierPaymentInfo.getPaymentMethod();
    PaymentGroup paymentGroup = getPaymentGroup (paymentMethod);

    if (paymentGroup == null) {
      // not assigning any PaymentGroup to this CommerceIdentifier.
      return;
    }

    //  if the payment group is a credit card, then pass in the creditcard verification number.
    if (paymentGroup instanceof CreditCard) {
      ((CreditCard)paymentGroup).setCardVerificationNumber(pCommerceIdentifierPaymentInfo.getCreditCardVerificationNumber());
    }

    OrderManager om = getOrderManager();
    PaymentGroupManager pgm = getPaymentGroupManager();
    ShippingGroupManager sgm = getShippingGroupManager();
    CommerceItemManager cim = getCommerceItemManager();
    String itemId = pCommerceIdentifierPaymentInfo.getCommerceIdentifier().getId();
    String pgId = paymentGroup.getId();
    double amount = pCommerceIdentifierPaymentInfo.getAmount();
    int type = RelationshipTypes.stringToType (pCommerceIdentifierPaymentInfo.getRelationshipType());
    try {
      if (! isPaymentGroupInOrder (order, pgId)) {
        //BUGS-FIXED: COMMERCE-168073 clean up any left over relationships from previous apply process.
        pgm.removeAllRelationshipsFromPaymentGroup(paymentGroup);
        pgm.addPaymentGroupToOrder(order, paymentGroup);
      }
      switch (type) {
      case RelationshipTypes.PAYMENTAMOUNT:
        cim.addItemAmountToPaymentGroup(order, itemId, pgId, amount);
        break;
      case RelationshipTypes.PAYMENTAMOUNTREMAINING:
        cim.addRemainingItemAmountToPaymentGroup(order, itemId, pgId);
        break;
      case RelationshipTypes.SHIPPINGAMOUNT:
        sgm.addShippingCostAmountToPaymentGroup(order, itemId, pgId, amount);
        break;
      case RelationshipTypes.SHIPPINGAMOUNTREMAINING:
        sgm.addRemainingShippingCostToPaymentGroup(order, itemId, pgId);
        break;
      case RelationshipTypes.ORDERAMOUNT:
        om.addOrderAmountToPaymentGroup(order, pgId, amount);
        break;
      case RelationshipTypes.ORDERAMOUNTREMAINING:
        om.addRemainingOrderAmountToPaymentGroup(order, pgId);
        // we cannot have more than one PaymentGroup or this type
        setApplyDefaultPaymentGroup(false);
        break;
      case RelationshipTypes.TAXAMOUNT:
        om.addTaxAmountToPaymentGroup(order, pgId, amount);
        break;
      case RelationshipTypes.TAXAMOUNTREMAINING:
        om.addRemainingTaxAmountToPaymentGroup(order, pgId);
        break;
      }
    } catch (CommerceException exc) {
      if (isLoggingError()) {
        logError(exc);
      }
    }
  }

  /**
   * <code>applyDefaultPaymentGroup</code> checks to see if there is a
   * defaultPaymentGroup. If so, this PaymentGroup is added to the Order's
   * remaining amount.
   *
   * This can facilitate simpler applications that only permit one
   * PaymentGroup per Order, as well as advanced applications that apply
   * a default PaymentGroup to any remaining Order amount not explicitly
   * covered by other PaymentGroups.
   *
   * @param pOrder an <code>Order</code> value
   */
  protected void applyDefaultPaymentGroup (Order pOrder)
  {
    String name = getPaymentGroupMapContainer().getDefaultPaymentGroupName();
    PaymentGroup paymentGroup = getPaymentGroup(name);

    if (paymentGroup == null) {
      // not assigning any default PaymentGroup to the Order.
      if (isLoggingDebug()) {
        logDebug("Not applying DefaultPaymentGroup: no group with name " + name);
      }
      return;
    }

    if (isLoggingDebug()) {
      logDebug("applying DefaultPaymentGroup" + paymentGroup);
    }

    OrderManager om = getOrderManager();
    PaymentGroupManager pgm = getPaymentGroupManager();
    String pgId = paymentGroup.getId();

    try {
      if (! isPaymentGroupInOrder (pOrder, pgId)) {
        //BUGS-FIXED: COMMERCE-168073 clean up any left over relationships from previous apply process.
        pgm.removeAllRelationshipsFromPaymentGroup(paymentGroup);
        pgm.addPaymentGroupToOrder(pOrder, paymentGroup);
      }
      om.addRemainingOrderAmountToPaymentGroup(pOrder, pgId);
    } catch (CommerceException exc) {
      if (isLoggingError()) {
        logError(exc);
      }
    }
  }

  /**
   * <code>isPaymentGroupInOrder</code> is used to determine if the PaymentGroup
   * is already in the Order.
   *
   * @param pOrder an <code>Order</code> value
   * @param pPaymentGroupId a <code>String</code> value
   * @return a <code>boolean</code> value
   * @deprecated  This method is moved to PaymentGroupManager
   *
   */
  protected boolean isPaymentGroupInOrder (Order pOrder, String pPaymentGroupId) {
    return getPaymentGroupManager().isPaymentGroupInOrder(pOrder,pPaymentGroupId);
  }

  /**
   * <code>getPaymentGroup</code> gets the PaymentGroup with the given name
   * from the PaymentGroupMapContainer.
   *
   * @param pPaymentMethod a <code>String</code> value
   * @return a <code>PaymentGroup</code> value
   */
  protected PaymentGroup getPaymentGroup(String pPaymentMethod) {
    if (pPaymentMethod == null)
      return null;

    PaymentGroup paymentGroup = null;
    PaymentGroupMapContainer container = getPaymentGroupMapContainer();
    paymentGroup = container.getPaymentGroup(pPaymentMethod);

    return paymentGroup;
  }


  //-------------------------------------
  // validation of PaymentGroups
  //-------------------------------------

  /**
   * <code>runProcessValidatePaymentGroups</code> runs a configurable Pipeline chain
   * to validate PaymentGroups or to prepare for the next checkout phase.
   *
   * @param pOrder an <code>Order</code> value
   * @param pPricingModels a <code>PricingModelHolder</code> value
   * @param pLocale a <code>Locale</code> value
   * @param pProfile a <code>RepositoryItem</code> value
   * @param pExtraParameters a <code>Map</code> value
   * @exception RunProcessException if an error occurs
   */
  protected void runProcessValidatePaymentGroups(Order pOrder, PricingModelHolder pPricingModels,
                                                 Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
    throws RunProcessException
  {
    PipelineResult result = runProcess(getValidatePaymentGroupsChainId(), pOrder, pPricingModels, pLocale,
                                       pProfile, pExtraParameters);
    processPipelineErrors(result);
  }


  //-------------------------------------
  // specifyDefaultPaymentGroup code
  //-------------------------------------

  /**
   * <code>handleSpecifyDefaultPaymentGroup</code> is used to let the user specify
   * a default PaymentGroup to use for payment.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleSpecifyDefaultPaymentGroup(DynamoHttpServletRequest pRequest,
                                                  DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "PaymentGroupOrderFormHandler.handleSpecifyDefaultPaymentGroup";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
  tr = ensureTransaction();

  if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

  if (!checkFormRedirect(null, getSpecifyDefaultPaymentGroupErrorURL(), pRequest, pResponse))
    return false;

  preSpecifyDefaultPaymentGroup(pRequest, pResponse);

  if (!checkFormRedirect(null, getSpecifyDefaultPaymentGroupErrorURL(), pRequest, pResponse))
    return false;

  specifyDefaultPaymentGroup(pRequest, pResponse);

  if (!checkFormRedirect(null, getSpecifyDefaultPaymentGroupErrorURL(), pRequest, pResponse))
    return false;

  postSpecifyDefaultPaymentGroup(pRequest, pResponse);

  return checkFormRedirect (getSpecifyDefaultPaymentGroupSuccessURL(),
          getSpecifyDefaultPaymentGroupErrorURL(), pRequest, pResponse);
      }
      finally {
  if (tr != null) commitTransaction(tr);
  if (rrm != null)
    rrm.removeRequestEntry(myHandleMethod);
      }
    }
    else {
      return false;
    }
  }

  /**
   * <code>preSpecifyDefaultPaymentGroup</code> is for work that must happen before
   * the default PaymentGroup is set.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preSpecifyDefaultPaymentGroup(DynamoHttpServletRequest pRequest,
                                            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>postSpecifyDefaultPaymentGroup</code> is for work that must happen after
   * the default PaymentGroup is set.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postSpecifyDefaultPaymentGroup(DynamoHttpServletRequest pRequest,
                                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>specifyDefaultPaymentGroup</code> sets the defaultPaymentGroupName in
   * the PaymentGroupMapContainer.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void specifyDefaultPaymentGroup(DynamoHttpServletRequest pRequest,
                                            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    if (StringUtils.isBlank(getDefaultPaymentGroupName())) {
      String msg = formatUserMessage(MSG_NO_DEFAULT_PAYMENT_GROUP, pRequest, pResponse);
      String propertyPath = generatePropertyPath("defaultPaymentGroupName");
      addFormException(new DropletFormException(msg, propertyPath, MSG_NO_DEFAULT_PAYMENT_GROUP));
      return;
    }
    getPaymentGroupMapContainer().setDefaultPaymentGroupName(getDefaultPaymentGroupName());
  }
}
