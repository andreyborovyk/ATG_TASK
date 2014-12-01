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

import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.userprofiling.Profile;
import atg.servlet.DynamoHttpServletRequest;
import atg.commerce.order.*;
import atg.commerce.CommerceException;
import atg.beans.*;
import java.util.*;
import atg.servlet.RequestLocale;
import atg.repository.RepositoryItem;
import atg.commerce.claimable.ClaimableManager;
import atg.core.util.ResourceUtils;

/**
 * The <code>StoreCreditInitializer</code> implements the PaymentGroupInitializer interface.
 * The <code>initializePaymentGroups</code> method is used to create StoreCredit
 * PaymentGroups for the user based on their existence in the ClaimableRepository.
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/StoreCreditInitializer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class StoreCreditInitializer extends GenericService implements PaymentGroupInitializer, PaymentGroupMatcher
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/StoreCreditInitializer.java#2 $$Change: 651448 $";
    
  static final String MY_RESOURCE_NAME = "atg.commerce.order.purchase.PurchaseProcessResources";
  static final String USER_RESOURCE_NAME = "atg.commerce.order.purchase.UserMessages";
  static final String LOCALE_PARAM = "locale";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  public static final String MSG_STORE_CREDIT_NAME = "storeCreditName";
  
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
   * Specifies the PaymentGroupManager to use in creating the StoreCredit.
   * @param pPaymentGroupManager a <code>PaymentGroupManager</code> value
   */
  public void setPaymentGroupManager(PaymentGroupManager pPaymentGroupManager) {
    mPaymentGroupManager = pPaymentGroupManager;
  }

  /**
   * Returns the PaymentGroupManager to use in creating the StoreCredit.
   * @return a <code>PaymentGroupManager</code> value
   */
  public PaymentGroupManager getPaymentGroupManager() {
    return mPaymentGroupManager;
  }

  //---------------------------------------------------------------------------
  // property: StoreCreditType
  //---------------------------------------------------------------------------
  String mStoreCreditType;

  /**
   * Specifies the PaymentGroup type used to create StoreCredits.
   * @param pStoreCreditType a <code>String</code> value
   */
  public void setStoreCreditType(String pStoreCreditType) {
    mStoreCreditType = pStoreCreditType;
  }

  /**
   * Returns the PaymentGroup type used to create StoreCredits.
   * @return a <code>String</code> value
   */
  public String getStoreCreditType() {
    return mStoreCreditType;
  }

  //---------------------------------------------------------------------------
  // property: ClaimableManager
  //---------------------------------------------------------------------------
  ClaimableManager mClaimableManager;

  /**
   * Specifies the ClaimableManager to use in determining the user's StoreCredits.
   * @param pClaimableManager a <code>ClaimableManager</code> value
   */
  public void setClaimableManager(ClaimableManager pClaimableManager) {
    mClaimableManager = pClaimableManager;
  }

  /**
   * Returns the ClaimableManager used in determining the user's StoreCredits.
   * @return a <code>ClaimableManager</code> value
   */
  public ClaimableManager getClaimableManager() {
    return mClaimableManager;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>StoreCreditInitializer</code> instance.
   *
   */
  public StoreCreditInitializer () {}
  
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  /**
   * <code>initializePaymentGroups</code> executes the <code>initializeStoreCredits</code>
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
    initializeStoreCredits (pProfile,
                            pPaymentGroupMapContainer,
                            pRequest);
  }

  /**
   * <code>initializeStoreCredits</code> gathers the user's StoreCredits from the ClaimableManager
   * and places them into the PaymentGroupMapContainer.
   *
   * @param pProfile a <code>Profile</code> value
   * @param pPaymentGroupMapContainer a <code>PaymentGroupMapContainer</code> value
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @exception PaymentGroupInitializationException if an error occurs
   */
  protected void initializeStoreCredits(Profile pProfile,
                                        PaymentGroupMapContainer pPaymentGroupMapContainer,
                                        DynamoHttpServletRequest pRequest)
    throws PaymentGroupInitializationException
  {
    ClaimableManager cm = getClaimableManager();
    if (cm == null)
      throw new PaymentGroupInitializationException(ResourceUtils.getMsgResource("missingClaimableManager",
                                                                                MY_RESOURCE_NAME, sResourceBundle));

    List storeCredits = null;
    try {
      // returns this user's StoreCredits with a positive amount remaining
      storeCredits = cm.getStoreCreditsForProfile(pProfile.getRepositoryId(), false);
    } catch (CommerceException exc) {
      throw new PaymentGroupInitializationException(exc);
    }

    if (storeCredits != null) {
      int count = 0;
      Iterator iter = storeCredits.iterator();
      while (iter.hasNext()) {
        RepositoryItem item = (RepositoryItem) iter.next();
        String key = nameStoreCredit (item, pProfile, pRequest);
        StoreCredit storeCredit = copyStoreCredit(item, pProfile);
        pPaymentGroupMapContainer.addPaymentGroup(key, storeCredit);
      }
    }
  }

  /**
   * <code>nameStoreCredit</code> is used to determine the name that the StoreCredit
   * PaymentGroup will be mapped to in the PaymentGroupMapContainer.
   *
   * @param pRepositoryItem a <code>RepositoryItem</code> value
   * @param pProfile a <code>Profile</code> value
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @return a <code>String</code> value
   */
  protected String nameStoreCredit (RepositoryItem pRepositoryItem, Profile pProfile, DynamoHttpServletRequest pRequest) {
    double amount = ((java.lang.Double) pRepositoryItem.getPropertyValue(getClaimableManager().getClaimableTools().getStoreCreditAmountPropertyName())).doubleValue();
    double amountAuthorized = ((java.lang.Double) pRepositoryItem.getPropertyValue(getClaimableManager().getClaimableTools().getStoreCreditAmountAuthorizedPropertyName())).doubleValue();
    double amountRemaining = amount - amountAuthorized;

    Object [] msgArgs = { new java.lang.Double(amountRemaining) };
    ResourceBundle userResources = atg.core.i18n.LayeredResourceBundle.getBundle(USER_RESOURCE_NAME, getUserLocale(pRequest));
    String msg = ResourceUtils.getUserMsgResource(MSG_STORE_CREDIT_NAME, USER_RESOURCE_NAME, userResources, msgArgs);
    return msg;
  }

  /**
   * <code>copyStoreCredit</code> is used to create a new StoreCredit PaymentGroup
   * and copy information into it.
   *
   * @param pRepositoryItem a <code>RepositoryItem</code> value
   * @param pProfile a <code>Profile</code> value
   * @return a <code>StoreCredit</code> value
   * @exception PaymentGroupInitializationException if an error occurs
   */
  protected StoreCredit copyStoreCredit (RepositoryItem pRepositoryItem, Profile pProfile)
    throws PaymentGroupInitializationException
  {
    PaymentGroupManager pgm = getPaymentGroupManager();
    if (pgm == null)
      throw new PaymentGroupInitializationException(ResourceUtils.getMsgResource("missingPaymentGroupManager",
                                                                                MY_RESOURCE_NAME, sResourceBundle));
    StoreCredit storeCredit = null;

    try {
      storeCredit = (StoreCredit) pgm.createPaymentGroup(getStoreCreditType());
      storeCredit.setStoreCreditNumber(pRepositoryItem.getRepositoryId());
      storeCredit.setProfileId(pProfile.getRepositoryId());
    } catch (CommerceException exc) {
      throw new PaymentGroupInitializationException(exc);
    }
    return storeCredit;
  }

  /**
   * Returns the locale associated with the request. The method first searches
   * for a request paramater named <code>locale</code>. This value can be
   * either a java.util.Locale object or a String which represents the locale.
   * Next the locale of the request will be returned. Finally, if the locale
   * cannot be determined, then the <code>defaultLocale</code> property is used.
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @return a <code>Locale</code> value
   */
  public Locale getUserLocale(DynamoHttpServletRequest pRequest)
  {
    Object obj = pRequest.getObjectParameter(LOCALE_PARAM);
    if (obj instanceof Locale)
      return (Locale)obj;
    else if (obj instanceof String)
      return RequestLocale.getCachedLocale((String)obj);
    else {
      RequestLocale requestLocale = pRequest.getRequestLocale();
      if (requestLocale != null)
        return requestLocale.getLocale();
    }

    return Locale.getDefault();
  }

  public String matchPaymentGroup(PaymentGroup pPaymentGroup, 
                                  PaymentGroupMapContainer pPaymentGroupMapContainer) {

    if (!(pPaymentGroup instanceof StoreCredit)) return null;

    Set paymentGroupNames = pPaymentGroupMapContainer.getPaymentGroupMap().keySet();
    if (paymentGroupNames == null) return null;

    Iterator nameIter = paymentGroupNames.iterator();
    String paymentGroupName = null;
    boolean found = false;
    while (nameIter.hasNext() && !found) {
      paymentGroupName = (String) nameIter.next();
      PaymentGroup paymentGroup = pPaymentGroupMapContainer.getPaymentGroup(paymentGroupName);
 
      if (paymentGroup instanceof StoreCredit)
	found = compareStoreCredits((StoreCredit) pPaymentGroup, (StoreCredit) paymentGroup);
    }

    if (found) return paymentGroupName;
    else return null;
  }
        
  public String getNewPaymentGroupName(PaymentGroup pPaymentGroup) {

    if (!(pPaymentGroup instanceof StoreCredit)) return null;
    return ((StoreCredit) pPaymentGroup).getStoreCreditNumber();
  }
        
  //-------------------------------------
  // return true if each fields in the gift certificates are the same
  private boolean compareStoreCredits(StoreCredit pStoreCreditA, StoreCredit pStoreCreditB)
  {
    if((pStoreCreditA == null) && (pStoreCreditB == null)) {
      return true;
    }
    if(pStoreCreditA == null)
      return false;
    if(pStoreCreditB == null)
      return false;

    String aStoreCreditNumber = pStoreCreditA.getStoreCreditNumber();
    String bStoreCreditNumber = pStoreCreditB.getStoreCreditNumber();

    String aProfileId = pStoreCreditA.getProfileId();
    String bProfileId = pStoreCreditB.getProfileId();

    if((aStoreCreditNumber == null) &&
       ((bStoreCreditNumber != null)))
      return false;
    if((aProfileId == null) &&
       ((bProfileId != null)))
      return false;

    if(
       (((aStoreCreditNumber == null) && (bStoreCreditNumber == null)) ||
        (aStoreCreditNumber.equals(bStoreCreditNumber)))
       &&
       (((aProfileId == null) && (bProfileId == null)) ||
        (aProfileId.equals(bProfileId)))
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
   * <LI>claimableManager
   * <LI>paymentGroupManager
   * </UL>
   **/
  protected void copyConfiguration() {
    if (mConfiguration != null) {
      if (getClaimableManager() == null) {
        setClaimableManager(mConfiguration.getClaimableManager());
      }
      if (getPaymentGroupManager() == null) {
        setPaymentGroupManager(mConfiguration.getPaymentGroupManager());
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
