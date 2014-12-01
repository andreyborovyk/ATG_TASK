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
import atg.commerce.CommerceException;
import atg.commerce.profile.CommerceProfileTools;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.payment.creditcard.ExtendableCreditCardTools;
import atg.servlet.*;
import atg.transaction.*;
import atg.beans.*;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.droplet.DropletFormException;


import java.util.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.transaction.Transaction;

/**
 * The <code>CreateCreditCardFormHandler</code> class is used to create a CreditCard
 * PaymentGroup. This CreditCard is optionally added to a PaymentGroupMapContainer
 * and copied to the Profile.
 *
 * @beaninfo
 *   description: A formhandler which allows the user to create a CreditCard.
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CreateCreditCardFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.droplet.GenericFormHandler
 * @see atg.commerce.order.purchase.PurchaseProcessFormHandler
 */
public class CreateCreditCardFormHandler
  extends PurchaseProcessFormHandler
  implements CreatePaymentGroupFormHandler
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CreateCreditCardFormHandler.java#2 $$Change: 651448 $";

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------
  static final String MSG_ERROR_IN_VALIDATION = "errorInValidation";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //---------------------------------------------------------------------------
  // property: ExtendableCreditCardTools
  //---------------------------------------------------------------------------
  ExtendableCreditCardTools mCreditCardTools;

  /**
   * Set the ExtendableCreditCardTools property
   * @param pCreditCardTools a <code>ExtendableCreditCardTools</code> value
   * @beaninfo description: The component to use for dealing with credit cards
   */
  public void setCreditCardTools(ExtendableCreditCardTools pCreditCardTools) {
    mCreditCardTools = pCreditCardTools;
  }

  /**
   * Return the CreditCardTools property.
   * @return a <code>ExtendableCreditCardTools</code> value
   */
  public ExtendableCreditCardTools getCreditCardTools() {
    return mCreditCardTools;
  }
  
  //---------------------------------------------------------------------------
  // property: AddToContainer
  //---------------------------------------------------------------------------
  boolean mAddToContainer;

  /**
   * Set the AddToContainer property.
   * @param pAddToContainer a <code>boolean</code> value
   * @beaninfo description: Should the CreditCard be added to a PaymentGroupMapContainer?
   */
  public void setAddToContainer(boolean pAddToContainer) {
    mAddToContainer = pAddToContainer;
  }

  /**
   * Return the AddToContainer property.
   * @return a <code>boolean</code> value
   */
  public boolean isAddToContainer() {
    return mAddToContainer;
  }

  //---------------------------------------------------------------------------
  // property: CopyToProfile
  //---------------------------------------------------------------------------
  boolean mCopyToProfile;

  /**
   * Set the CopyToProfile property.
   * @param pCopyToProfile a <code>boolean</code> value
   * @beaninfo description: Should the CreditCard be copied to the Profile?
   */
  public void setCopyToProfile(boolean pCopyToProfile) {
    mCopyToProfile = pCopyToProfile;
  }

  /**
   * Return the CopyToProfile property.
   * @return a <code>boolean</code> value
   */
  public boolean isCopyToProfile() {
    return mCopyToProfile;
  }

  //---------------------------------------------------------------------------
  // property: Container
  //---------------------------------------------------------------------------
  PaymentGroupMapContainer mContainer;

  /**
   * Set the Container property.
   * @param pContainer a <code>PaymentGroupMapContainer</code> value
   * @beaninfo description: The container to add the CreditCard to.
   */
  public void setContainer(PaymentGroupMapContainer pContainer) {
    mContainer = pContainer;
  }

  /**
   * Return the Container property.
   * @return a <code>PaymentGroupMapContainer</code> value
   */
  public PaymentGroupMapContainer getContainer() {
    return mContainer;
  }

  //---------------------------------------------------------------------------
  // property: CreditCardName
  //---------------------------------------------------------------------------
  String mCreditCardName;

  /**
   * Set the CreditCardName property.
   * @param pCreditCardName a <code>String</code> value
   * @beaninfo description: The name of the new CreditCard.
   */
  public void setCreditCardName(String pCreditCardName) {
    mCreditCardName = pCreditCardName;
  }

  /**
   * Return the CreditCardName property.
   * @return a <code>String</code> value
   */
  public String getCreditCardName() {
    return mCreditCardName;
  }

  //---------------------------------------------------------------------------
  // property: CreditCardType
  //---------------------------------------------------------------------------
  String mCreditCardType;

  /**
   * Set the CreditCardType property.
   * @param pCreditCardType a <code>String</code> value
   * @beaninfo description: The PaymentGroup type for CreditCards.
   */
  public void setCreditCardType(String pCreditCardType) {
    mCreditCardType = pCreditCardType;
  }

  /**
   * Return the CreditCardType property.
   * @return a <code>String</code> value
   */
  public String getCreditCardType() {
    return mCreditCardType;
  }

  //---------------------------------------------------------------------------
  // property: NewCreditCardSuccessURL
  //---------------------------------------------------------------------------
  String mNewCreditCardSuccessURL;

  /**
   * Set the NewCreditCardSuccessURL property.
   * @param pNewCreditCardSuccessURL a <code>String</code> value
   * @beaninfo description: URL for redirection upon success.
   */
  public void setNewCreditCardSuccessURL(String pNewCreditCardSuccessURL) {
    mNewCreditCardSuccessURL = pNewCreditCardSuccessURL;
  }

  /**
   * Return the NewCreditCardSuccessURL property.
   * @return a <code>String</code> value
   */
  public String getNewCreditCardSuccessURL() {
    return mNewCreditCardSuccessURL;
  }

  //---------------------------------------------------------------------------
  // property: NewCreditCardErrorURL
  //---------------------------------------------------------------------------
  String mNewCreditCardErrorURL;

  /**
   * Set the NewCreditCardErrorURL property.
   * @param pNewCreditCardErrorURL a <code>String</code> value
   * @beaninfo description: URL for redirection upon failure.
   */
  public void setNewCreditCardErrorURL(String pNewCreditCardErrorURL) {
    mNewCreditCardErrorURL = pNewCreditCardErrorURL;
  }

  /**
   * Return the NewCreditCardErrorURL property.
   * @return a <code>String</code> value
   */
  public String getNewCreditCardErrorURL() {
    return mNewCreditCardErrorURL;
  }

  //---------------------------------------------------------------------------
  // property: CreditCard
  //---------------------------------------------------------------------------
  CreditCard mCreditCard = null;

  /**
   * Set the CreditCard property.
   * @param pCreditCard a <code>CreditCard</code> value
   * @beaninfo description: The new CreditCard.
   */
  public void setCreditCard(CreditCard pCreditCard) {
    mCreditCard = pCreditCard;
  }

  /**
   * Return the CreditCard property. This method exposes the CreditCard as a JavaBean property so that
   * it may be edited directly from a .jhtml page. If this is null, then the PaymentGroupManager
   * is used to create a new CreditCard.
   *
   * @return a <code>CreditCard</code> value
   */
  public CreditCard getCreditCard() {
    if (mCreditCard == null) {
      try {
        mCreditCard = (CreditCard) getPaymentGroupManager().createPaymentGroup(getCreditCardType());
      } catch (CommerceException exc) {
        if (isLoggingError()) logError(exc);
      }
    }
    return mCreditCard;
  }

  boolean mAssignNewCreditCardAsDefault;
  boolean mValidateCreditCard;

  /**
   * @return the assignNewCreditCardAsDefault
   */
  public boolean isAssignNewCreditCardAsDefault() {
    return mAssignNewCreditCardAsDefault;
  }

  /**
   * @param pAssignNewCreditCardAsDefault The assignNewCreditCardAsDefault to set.
   */
  public void setAssignNewCreditCardAsDefault(
      boolean pAssignNewCreditCardAsDefault) {
    mAssignNewCreditCardAsDefault = pAssignNewCreditCardAsDefault;
  }

  /**
   * @return the validateCreditCard
   */
  public boolean isValidateCreditCard() {
    return mValidateCreditCard;
  }

  /**
   * @param pValidateCreditCard The validateCreditCard to set.
   */
  public void setValidateCreditCard(boolean pValidateCreditCard) {
    mValidateCreditCard = pValidateCreditCard;
  }

  boolean mGenerateNickname;

  /**
   * @return the generateNickname
   */
  public boolean isGenerateNickname() {
    return mGenerateNickname;
  }

  /**
   * @param pGenerateNickname The generateNickname to set.
   */
  public void setGenerateNickname(boolean pGenerateNickname) {
    mGenerateNickname = pGenerateNickname;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>CreateCreditCardFormHandler</code> instance.
   *
   */
  public CreateCreditCardFormHandler() {}

  //--------------------------------------------------
  // Methods
  //--------------------------------------------------

  /**
   * <code>handleNewCreditCard</code> is used to create a new CreditCard.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleNewCreditCard(DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CreateCreditCardOrderFormHandler.handleNewCreditCard";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
  tr = ensureTransaction();

  if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

  //Check if any form errors exist.  If they do, redirect to Error URL:
  if (!checkFormRedirect(null, getNewCreditCardErrorURL(), pRequest, pResponse))
    return false;

  preCreateCreditCard(pRequest, pResponse);

  //Check if any form errors exist.  If they do, redirect to Error URL:
  if (!checkFormRedirect(null, getNewCreditCardErrorURL(), pRequest, pResponse))
    return false;

  createCreditCard(pRequest, pResponse);

  //Check if any form errors exist.  If they do, redirect to Error URL:
  if (!checkFormRedirect(null, getNewCreditCardErrorURL(), pRequest, pResponse))
    return false;
  postCreateCreditCard(pRequest, pResponse);

  return checkFormRedirect (getNewCreditCardSuccessURL(),
          getNewCreditCardErrorURL(), pRequest, pResponse);
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
   * <code>preCreateCreditCard</code> is for work that must happen before
   * a new CreditCard is created.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preCreateCreditCard(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>postCreateCreditCard</code> is for work that must happen after
   * a new CreditCard is created.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postCreateCreditCard(DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>createCreditCard</code> creates a new CreditCard. The CreditCardType
   * property gives the type of PaymentGroup to create. The CreditCardName property
   * gives the name of the new PaymentGroup, as it will be referenced in the
   * PaymentGroupMapContainer. If <code>isAddToContainer</code> is true then
   * the CreditCard is added to the PaymentGroupMapContainer and made the default
   * PaymentGroup. If <code>isCopyToProfile</code> is true then the CreditCard is copied
   * to the Profile.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @see atg.commerce.profile.CommerceProfileTools.getUniqueCreditCardNickname()
   */
  public void createCreditCard(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse) {

    CreditCard creditCard = getCreditCard();
    PaymentGroupMapContainer container = getContainer();
    String name = getCreditCardName();

    if (StringUtils.isEmpty(name)
        && isGenerateNickname()) {
      name = getCommerceProfileTools().getUniqueCreditCardNickname(creditCard, getProfile(),null);
      setCreditCardName(name);
    }

    try {
      if (isValidateCreditCard()) {
        validateCreditCard(getCreditCard(),
                            pRequest,
                            pResponse);
      }
    }
    catch (ServletException se) {
      try {
        String msg = formatUserMessage(MSG_ERROR_IN_VALIDATION, pRequest, pResponse);
        String propertyPath = generatePropertyPath("creditCard");
        addFormException(new DropletFormException(msg, se, propertyPath, MSG_ERROR_IN_VALIDATION));
      }
      catch (Exception exception) { // exceptions thrown by exception-handling
        if (isLoggingError()) logError(exception);
      }
    }
    catch (IOException ioe) {
      try {
        String msg = formatUserMessage(MSG_ERROR_IN_VALIDATION, pRequest, pResponse);
        String propertyPath = generatePropertyPath("creditCard");
        addFormException(new DropletFormException(msg, ioe, propertyPath, MSG_ERROR_IN_VALIDATION));
      }
      catch (Exception exception) { // exceptions thrown by exception-handling
        if (isLoggingError()) logError(exception);
      }
    }

    if (getFormError()) {
      return;
    }

    // add to Container
    if (isAddToContainer()) {
      if (isAssignNewCreditCardAsDefault()) {
        container.setDefaultPaymentGroupName(name);
      }
      container.addPaymentGroup(name, creditCard);
    }

    // copy to Profile
    if (isCopyToProfile()) {
      getCommerceProfileTools().copyCreditCardToProfile(creditCard, getProfile(),name);
    }
  }

  /**
   * This method validates the credit card and if there is any errors adds the
   * form exceptions.
   * @param pCreditCard
   * @throws IOException
   * @throws ServletException
   */
  public void validateCreditCard (CreditCard pCreditCard,
                                  DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse)
  throws ServletException, IOException {

    int statusCode = getCreditCardTools().verifyCreditCard(pCreditCard);
    if (statusCode == 0) {
      return;
    }
    String msg = getCreditCardTools().getStatusCodeMessage(statusCode, getUserLocale(pRequest, pResponse));
    String propertyPath = generatePropertyPath("validateCreditCard()");
    addFormException(new DropletFormException(msg, propertyPath, new Integer(statusCode).toString()));
  }


  // -------------------------------
  // Centralized configuration
  // -------------------------------

  /**
   * Copy property settings from the optional
   * <code>PurchaseProcessConfiguration</code> component. Property
   * values that were configured locally are preserved.
   *
   * Configures the following properties (if not already set):
   * <UL>
   * <LI>container (from paymentGroupMapContainer)
   * </UL>
   **/
  protected void copyConfiguration() {
    super.copyConfiguration();
    if (getConfiguration() != null) {
      if (getContainer() == null) {
        setContainer(getConfiguration().getPaymentGroupMapContainer());
      }
    }
  }

}   // end of class
