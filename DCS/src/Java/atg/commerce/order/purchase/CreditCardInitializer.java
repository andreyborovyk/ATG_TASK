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

import atg.beans.*;
import atg.commerce.CommerceException;
import atg.commerce.order.*;
import atg.commerce.profile.CommerceProfileTools;
import atg.core.util.ResourceUtils;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.userprofiling.Profile;

import java.util.*;

/**
 * The <code>CreditCardInitializer</code> implements the PaymentGroupInitializer interface.
 * The <code>initializePaymentGroups</code> method is used to create CreditCard
 * PaymentGroups for the user based on their existence in the Profile.
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CreditCardInitializer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class CreditCardInitializer extends GenericService implements PaymentGroupInitializer, PaymentGroupMatcher
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CreditCardInitializer.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.purchase.PurchaseProcessResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());


  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //---------------------------------------------------------------------------
  // property: configuration
  //---------------------------------------------------------------------------
  PurchaseProcessConfiguration mConfiguration;

  /**
   * Sets property Configuration
   * @param pConfiguration a <code>PurchaseProcessConfiguration</code> value
   */
  public void setConfiguration(PurchaseProcessConfiguration pConfiguration) {
    mConfiguration = pConfiguration;
  }

  /**
   * Returns property Configuration
   * @return a <code>PurchaseProcessConfiguration</code> value
   */
  public PurchaseProcessConfiguration getConfiguration() {
    return mConfiguration;
  }

  //---------------------------------------------------------------------------
  // property: PaymentGroupManager
  //---------------------------------------------------------------------------
  PaymentGroupManager mPaymentGroupManager;

  /**
   * Specifies the PaymentGroupManager to use in creating the CreditCards.
   * @param pPaymentGroupManager a <code>PaymentGroupManager</code> value
   */
  public void setPaymentGroupManager(PaymentGroupManager pPaymentGroupManager) {
    mPaymentGroupManager = pPaymentGroupManager;
  }

  /**
   * Returns the PaymentGroupManager to use in creating the CreditCards.
   * @return a <code>PaymentGroupManager</code> value
   */
  public PaymentGroupManager getPaymentGroupManager() {
    return mPaymentGroupManager;
  }

  //---------------------------------------------------------------------------
  // property: CommerceProfileTools
  //---------------------------------------------------------------------------
  CommerceProfileTools mCommerceProfileTools;

  /**
   * Specifies the CommerceProfileTools component used to copy the CreditCards.
   * @param pCommerceProfileTools a <code>CommerceProfileTools</code> value
   */
  public void setCommerceProfileTools(CommerceProfileTools pCommerceProfileTools) {
    mCommerceProfileTools = pCommerceProfileTools;
  }

  /**
   * Returns the CommerceProfileTools component used to copy the CreditCards.
   * @return a <code>CommerceProfileTools</code> value
   */
  public CommerceProfileTools getCommerceProfileTools() {
    return mCommerceProfileTools;
  }

  //---------------------------------------------------------------------------
  // property: CreditCardsPropertyName
  //---------------------------------------------------------------------------
  String mCreditCardsPropertyName;

  /**
   * Specifies the Profile property in which CreditCards are located.
   * @param pCreditCardsPropertyName a <code>String</code> value
   */
  public void setCreditCardsPropertyName(String pCreditCardsPropertyName) {
    mCreditCardsPropertyName = pCreditCardsPropertyName;
  }

  /**
   * Returns the Profile property in which CreditCards are located.
   * @return a <code>String</code> value
   */
  public String getCreditCardsPropertyName() {
    return mCreditCardsPropertyName;
  }

  //---------------------------------------------------------------------------
  // property: CreditCardType
  //---------------------------------------------------------------------------
  String mCreditCardType;

  /**
   * Specifies the PaymentGroup type used to create CreditCards.
   * @param pCreditCardType a <code>String</code> value
   */
  public void setCreditCardType(String pCreditCardType) {
    mCreditCardType = pCreditCardType;
  }

  /**
   * Returns the PaymentGroup type used to create CreditCards.
   * @return a <code>String</code> value
   */
  public String getCreditCardType() {
    return mCreditCardType;
  }

  //---------------------------------------------------------------------------
  // property: DefaultCreditCardPropertyName
  //---------------------------------------------------------------------------
  String mDefaultCreditCardPropertyName;

  /**
   * Specifies the Profile property in which the defaultCreditCard is located.
   * @param pDefaultCreditCardPropertyName a <code>String</code> value
   */
  public void setDefaultCreditCardPropertyName(String pDefaultCreditCardPropertyName) {
    mDefaultCreditCardPropertyName = pDefaultCreditCardPropertyName;
  }

  /**
   * Returns the Profile property in which the defaultCreditCard is located.
   * @return a <code>String</code> value
   */
  public String getDefaultCreditCardPropertyName() {
    return mDefaultCreditCardPropertyName;
  }

  //---------------------------------------------------------------------------
  // property: DefaultPaymentGroupName
  //---------------------------------------------------------------------------
  String mDefaultPaymentGroupName;

  /**
   * Specifies the name that will be used as the key for the defaultCreditCard
   * in the PaymentGroupMapContainer.  This value should not conflict with the
   * nickname of any profile's credit cards.
   * @param pDefaultPaymentGroupName a <code>String</code> value.  This value should
   *        not conflict with the nickname of any profile's credit cards.
   */
  public void setDefaultPaymentGroupName(String pDefaultPaymentGroupName) {
    mDefaultPaymentGroupName = pDefaultPaymentGroupName;
  }

  /**
   * Returns the name that will be used as the key for the defaultCreditCard
   * in the PaymentGroupMapContainer
   * @return a <code>String</code> value
   */
  public String getDefaultPaymentGroupName() {
    return mDefaultPaymentGroupName;
  }

  //---------------------------------------------------------------------------
  // property: paymentGroupMapContainer
  //---------------------------------------------------------------------------
  PaymentGroupMapContainer mPaymentGroupMapContainer;

  /**
   * @return the paymentGroupMapContainer
   */
  public PaymentGroupMapContainer getPaymentGroupMapContainer() {
    return mPaymentGroupMapContainer;
  }

  /**
   * @param pPaymentGroupMapContainer The paymentGroupMapContainer to set.
   */
  public void setPaymentGroupMapContainer(
      PaymentGroupMapContainer pPaymentGroupMapContainer) {
    mPaymentGroupMapContainer = pPaymentGroupMapContainer;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>CreditCardInitializer</code> instance.
   *
   */
  public CreditCardInitializer () {}

  //--------------------------------------------------
  // Methods
  //--------------------------------------------------

  /**
   * <code>initializePaymentGroups</code> executes the <code>initializeCreditCards</code>
   * method.
   *
   * @param pProfile a <code>Profile</code> value
   * @param pPaymentGroupMapContainer a <code>PaymentGroupMapContainer</code> value
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @exception PaymentGroupInitializationException if an error occurs
   */
  public void initializePaymentGroups (Profile pProfile,
                                       PaymentGroupMapContainer pPaymentGroupMapContainer,
                                       DynamoHttpServletRequest pRequest)
    throws PaymentGroupInitializationException
  {
    initializeCreditCards (pProfile,
                           pPaymentGroupMapContainer,
                           pRequest);
  }

  /**
   * <code>initializeCreditCards</code> looks for named credit cards in the Profile
   * in the map property given by <code>getCreditCardsPropertyName</code>.
   * From these CreditCard PaymentGroup objects are created, and added to the
   * PaymentGroupMapContainer by their name as found in the Profile.
   *
   * @param pProfile a <code>Profile</code> value
   * @param pPaymentGroupMapContainer a <code>PaymentGroupMapContainer</code> value
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @exception PaymentGroupInitializationException if an error occurs
   */
  public void initializeCreditCards (Profile pProfile,
                                     PaymentGroupMapContainer pPaymentGroupMapContainer,
                                     DynamoHttpServletRequest pRequest)
    throws PaymentGroupInitializationException
  {

    String ccPropertyName = getCreditCardsPropertyName();
    if (ccPropertyName == null)
      throw new PaymentGroupInitializationException(ResourceUtils.getMsgResource("missingCreditCardsPropertyName",
                                                                                 MY_RESOURCE_NAME, sResourceBundle));
    String defCreditCardPropertyName = getDefaultCreditCardPropertyName();

    RepositoryItem defaultCreditCard = null;

    try {
      // get the user's default credit card
      if (defCreditCardPropertyName != null)
        defaultCreditCard = (RepositoryItem) DynamicBeans.getSubPropertyValue(pProfile, defCreditCardPropertyName);

      boolean foundDefault = false;

      Map creditCardsFound = (Map) DynamicBeans.getSubPropertyValue(pProfile, ccPropertyName);

      if (creditCardsFound != null) {
        Iterator iter = creditCardsFound.entrySet().iterator();
        while (iter.hasNext()) {
          Map.Entry entry = (Map.Entry)iter.next();
          String key = (String)entry.getKey();
          RepositoryItem creditCard = (RepositoryItem)entry.getValue();
          CreditCard cc = copyCreditCard(key, pProfile);
          pPaymentGroupMapContainer.addPaymentGroup(key, cc);

          // is this the default credit card?
          if ((!foundDefault) && (defaultCreditCard != null)) {
            if (creditCard.getRepositoryId().equals(defaultCreditCard.getRepositoryId())) {
              foundDefault = true;
              pPaymentGroupMapContainer.setDefaultPaymentGroupName(key);
            }
          }
        }
      }

      if ((!foundDefault) && (defaultCreditCard != null)) {
        // create CreditCard based on default credit card
        CreditCard cc = (CreditCard) getPaymentGroupManager().createPaymentGroup(getCreditCardType());
        getCommerceProfileTools().copyCreditCard(defaultCreditCard, cc);

        // add the CreditCard to PaymentGroupMapContainer
        if (cc != null) {

          String defaultPaymentGroupName = getDefaultPaymentGroupName();

          pPaymentGroupMapContainer.addPaymentGroup(defaultPaymentGroupName, cc);
          pPaymentGroupMapContainer.setDefaultPaymentGroupName(defaultPaymentGroupName);
        }
      }
    }
    catch (Exception exc) {
      throw new PaymentGroupInitializationException(exc);
    }
  }

  /**
   * <code>copyCreditCard</code> creates a CreditCard PaymentGroup object from a
   * credit card in the Profile, based on the supplied cardname.
   *
   * @param pPaymentMethod a <code>String</code> value
   * @param pProfile a <code>Profile</code> value
   * @return a <code>CreditCard</code> value
   * @exception PaymentGroupInitializationException if an error occurs
   */
  protected CreditCard copyCreditCard (String pPaymentMethod, Profile pProfile)
    throws PaymentGroupInitializationException
  {
    PaymentGroupManager pgm = getPaymentGroupManager();
    if (pgm == null)
      throw new PaymentGroupInitializationException(ResourceUtils.getMsgResource("missingPaymentGroupManager",
                                                                                MY_RESOURCE_NAME, sResourceBundle));

    CommerceProfileTools tools = getCommerceProfileTools();
    if (tools == null)
      throw new PaymentGroupInitializationException(ResourceUtils.getMsgResource("missingCommerceProfileTools",
                                                                                MY_RESOURCE_NAME, sResourceBundle));

    CreditCard cc = null;

    try {
      cc = (CreditCard) pgm.createPaymentGroup(getCreditCardType());
      tools.copyCreditCardToPaymentGroup(pPaymentMethod, cc, pProfile, Locale.getDefault());
    } catch (CommerceException exc) {
      throw new PaymentGroupInitializationException(exc);
    }
    return cc;
  }

  public String matchPaymentGroup(PaymentGroup pPaymentGroup,
                                  PaymentGroupMapContainer pPaymentGroupMapContainer) {

    if (!(pPaymentGroup instanceof CreditCard)) return null;

    Set paymentGroupNames = pPaymentGroupMapContainer.getPaymentGroupMap().keySet();
    if (paymentGroupNames == null) return null;

    Iterator nameIter = paymentGroupNames.iterator();
    String paymentGroupName = null;
    boolean found = false;
    while (nameIter.hasNext() && !found) {
      paymentGroupName = (String) nameIter.next();
      PaymentGroup paymentGroup = pPaymentGroupMapContainer.getPaymentGroup(paymentGroupName);

      if (paymentGroup instanceof CreditCard)
        found = compareCreditCards((CreditCard) paymentGroup, (CreditCard) pPaymentGroup);
    }

    if (found) return paymentGroupName;
    else return null;
  }

  public String getNewPaymentGroupName(PaymentGroup pPaymentGroup) {

    if (!(pPaymentGroup instanceof CreditCard)) return null;
    Map paymentGroupMap = getPaymentGroupMapContainer().getPaymentGroupMap();
    Collection paymentGroupNicknames = null;
    if (paymentGroupMap != null) {
      paymentGroupNicknames = paymentGroupMap.keySet();
    }
    return getPaymentGroupManager().getOrderTools().getProfileTools().getUniqueCreditCardNickname((CreditCard) pPaymentGroup,
                                                                                      paymentGroupNicknames,
                                                                                      null);

  }

  //-------------------------------------
  // return true if each fields in the credit cards are the same
  private boolean compareCreditCards(CreditCard pCreditCardA, CreditCard pCreditCardB)
  {
    if((pCreditCardA == null) && (pCreditCardB == null)) {
      return true;
    }
    if(pCreditCardA == null)
      return false;
    if(pCreditCardB == null)
      return false;

    String aCreditCardNumber = pCreditCardA.getCreditCardNumber();
    String bCreditCardNumber = pCreditCardB.getCreditCardNumber();

    String aCreditCardType = pCreditCardA.getCreditCardType();
    String bCreditCardType = pCreditCardB.getCreditCardType();

    String aExpirationMonth = pCreditCardA.getExpirationMonth();
    String bExpirationMonth = pCreditCardB.getExpirationMonth();

    String aExpirationYear = pCreditCardA.getExpirationYear();
    String bExpirationYear = pCreditCardB.getExpirationYear();

    if((aCreditCardNumber == null) &&
       ((bCreditCardNumber != null)))
      return false;
    if((aCreditCardType == null) &&
       ((bCreditCardType != null)))
      return false;
    if((aExpirationMonth == null) &&
       ((bExpirationMonth != null)))
      return false;
    if((aExpirationYear == null) &&
       ((bExpirationYear != null)))
      return false;

    if(
       (((aCreditCardNumber == null) && (bCreditCardNumber == null)) ||
        (aCreditCardNumber.equals(bCreditCardNumber)))
       &&
       (((aCreditCardType == null) && (bCreditCardType == null)) ||
        (aCreditCardType.equals(bCreditCardType)))
       &&
       (((aExpirationMonth == null) && (bExpirationMonth == null)) ||
        (aExpirationMonth.equals(bExpirationMonth)))
       &&
       (((aExpirationYear == null) && (bExpirationYear == null)) ||
        (aExpirationYear.equals(bExpirationYear)))
       )
    {
      return true;
    }
    else
      return false;
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
   * <LI>commerceProfileTools
   * <LI>paymentGroupManager
   * </UL>
   **/
  protected void copyConfiguration() {
    if (mConfiguration != null) {
      if (getCommerceProfileTools() == null) {
        setCommerceProfileTools(mConfiguration.getCommerceProfileTools());
      }
      if (getPaymentGroupManager() == null) {
        setPaymentGroupManager(mConfiguration.getPaymentGroupManager());
      }
      if (getPaymentGroupMapContainer() == null) {
        setPaymentGroupMapContainer(mConfiguration.getPaymentGroupMapContainer());
      }

    }
  }

  // -------------------------------
  // GenericService overrides
  // -------------------------------

  /**
   * Perform one-time startup operations, including copying property
   * settings from the optional <code>PurchaseProcessConfiguration</code>
   * component.
   */
  public void doStartService()
       throws ServiceException
  {
    super.doStartService();
    copyConfiguration();
  }

}   // end of class
