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
 * The <code>GiftCertificateInitializer</code> implements the PaymentGroupInitializer interface.
 * The <code>initializePaymentGroups</code> method is used to create GiftCertificate
 * PaymentGroups for the user based on their existence in the ClaimableRepository.
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/GiftCertificateInitializer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class GiftCertificateInitializer extends GenericService implements PaymentGroupInitializer, PaymentGroupMatcher
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/GiftCertificateInitializer.java#2 $$Change: 651448 $";
    
  static final String MY_RESOURCE_NAME = "atg.commerce.order.purchase.PurchaseProcessResources";
  static final String USER_RESOURCE_NAME = "atg.commerce.order.purchase.UserMessages";
  static final String LOCALE_PARAM = "locale";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  public static final String MSG_GIFT_CERTIFICATE_NAME = "giftCertificateName";

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
   * Specifies the PaymentGroupManager to use in creating the GiftCertificate.
   * @param pPaymentGroupManager a <code>PaymentGroupManager</code> value
   */
  public void setPaymentGroupManager(PaymentGroupManager pPaymentGroupManager) {
    mPaymentGroupManager = pPaymentGroupManager;
  }

  /**
   * Returns the PaymentGroupManager to use in creating the GiftCertificate.
   * @return a <code>PaymentGroupManager</code> value
   */
  public PaymentGroupManager getPaymentGroupManager() {
    return mPaymentGroupManager;
  }


  //---------------------------------------------------------------------------
  // property: GiftCertificateType
  //---------------------------------------------------------------------------
  String mGiftCertificateType;

  /**
   * Specifies the PaymentGroup type used to create GiftCertificates.
   * @param pGiftCertificateType a <code>String</code> value
   */
  public void setGiftCertificateType(String pGiftCertificateType) {
    mGiftCertificateType = pGiftCertificateType;
  }

  /**
   * Returns the PaymentGroup type used to create GiftCertificates.
   * @return a <code>String</code> value
   */
  public String getGiftCertificateType() {
    return mGiftCertificateType;
  }

  //---------------------------------------------------------------------------
  // property: ClaimableManager
  //---------------------------------------------------------------------------
  ClaimableManager mClaimableManager;

  /**
   * Specifies the ClaimableManager to use in determining the user's GiftCertificates.
   * @param pClaimableManager a <code>ClaimableManager</code> value
   */
  public void setClaimableManager(ClaimableManager pClaimableManager) {
    mClaimableManager = pClaimableManager;
  }

  /**
   * Returns the ClaimableManager is used in determining the user's GiftCertificates.
   * @return a <code>ClaimableManager</code> value
   */
  public ClaimableManager getClaimableManager() {
    return mClaimableManager;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>GiftCertificateInitializer</code> instance.
   *
   */
  public GiftCertificateInitializer () {}
  
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  /**
   * <code>initializePaymentGroups</code> executes the <code>initializeGiftCertificates</code>
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
    initializeGiftCertificates (pProfile,
                                pPaymentGroupMapContainer,
                                pRequest);
  }

  /**
   * <code>initializeGiftCertificates</code> gathers the user's GiftCertificates from the ClaimableManager
   * and places them into the PaymentGroupMapContainer.
   *
   * @param pProfile a <code>Profile</code> value
   * @param pPaymentGroupMapContainer a <code>PaymentGroupMapContainer</code> value
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @exception PaymentGroupInitializationException if an error occurs
   */
  protected void initializeGiftCertificates(Profile pProfile,
                                            PaymentGroupMapContainer pPaymentGroupMapContainer,
                                            DynamoHttpServletRequest pRequest)
    throws PaymentGroupInitializationException
  {
    ClaimableManager cm = getClaimableManager();
    if (cm == null)
      throw new PaymentGroupInitializationException(ResourceUtils.getMsgResource("missingClaimableManager",
                                                                                MY_RESOURCE_NAME, sResourceBundle));
    
    List giftCertificates = null;
    try {
      // returns this user's GiftCertificates with a positive amount remaining
      giftCertificates = cm.getGiftCertificatesForProfile(pProfile.getRepositoryId(), false);
    } catch (CommerceException exc) {
      throw new PaymentGroupInitializationException(exc);
    }

    if (giftCertificates != null) {
      int count = 0;
      Iterator iter = giftCertificates.iterator();
      while (iter.hasNext()) {
        RepositoryItem item = (RepositoryItem) iter.next();
        String key = nameGiftCertificate (item, pProfile, pRequest);
        GiftCertificate giftCertificate = copyGiftCertificate(item, pProfile);
        pPaymentGroupMapContainer.addPaymentGroup(key, giftCertificate);
      }
    }
  }


  /**
   * <code>nameGiftCertificate</code> is used to determine the name that the GiftCertificate
   * PaymentGroup will be mapped to in the PaymentGroupMapContainer.
   *
   * @param pRepositoryItem a <code>RepositoryItem</code> value
   * @param pProfile a <code>Profile</code> value
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @return a <code>String</code> value
   */
  protected String nameGiftCertificate (RepositoryItem pRepositoryItem, Profile pProfile, DynamoHttpServletRequest pRequest) {
    double amount = ((java.lang.Double) pRepositoryItem.getPropertyValue(getClaimableManager().getClaimableTools().getGiftCertificateAmountPropertyName())).doubleValue();
    double amountAuthorized = ((java.lang.Double) pRepositoryItem.getPropertyValue(getClaimableManager().getClaimableTools().getGiftCertificateAmountAuthorizedPropertyName())).doubleValue();
    double amountRemaining = amount - amountAuthorized;

    Object [] msgArgs = { new java.lang.Double(amountRemaining) };
    ResourceBundle userResources = atg.core.i18n.LayeredResourceBundle.getBundle(USER_RESOURCE_NAME, getUserLocale(pRequest));
    String msg = ResourceUtils.getUserMsgResource(MSG_GIFT_CERTIFICATE_NAME, USER_RESOURCE_NAME, userResources, msgArgs);
    return msg;
  }

  /**
   * <code>copyGiftCertificate</code> is used to create a new GiftCertificate
   * PaymentGroup and copy the necessary information into it.
   *
   * @param pRepositoryItem a <code>RepositoryItem</code> value
   * @param pProfile a <code>Profile</code> value
   * @return a <code>GiftCertificate</code> value
   * @exception PaymentGroupInitializationException if an error occurs
   */
  protected GiftCertificate copyGiftCertificate (RepositoryItem pRepositoryItem, Profile pProfile)
    throws PaymentGroupInitializationException
  {
    PaymentGroupManager pgm = getPaymentGroupManager();
    if (pgm == null)
      throw new PaymentGroupInitializationException(ResourceUtils.getMsgResource("missingPaymentGroupManager",
                                                                                MY_RESOURCE_NAME, sResourceBundle));
    GiftCertificate giftCertificate = null;

    try {
      giftCertificate = (GiftCertificate) pgm.createPaymentGroup(getGiftCertificateType());
      pgm.initializeGiftCertificate(giftCertificate, pProfile.getRepositoryId(), pRepositoryItem.getRepositoryId());
    } catch (CommerceException exc) {
      throw new PaymentGroupInitializationException(exc);
    }
    return giftCertificate;
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

    if (!(pPaymentGroup instanceof GiftCertificate)) return null;

    Set paymentGroupNames = pPaymentGroupMapContainer.getPaymentGroupMap().keySet();
    if (paymentGroupNames == null) return null;

    Iterator nameIter = paymentGroupNames.iterator();
    String paymentGroupName = null;
    boolean found = false;
    while (nameIter.hasNext() && !found) {
      paymentGroupName = (String) nameIter.next();
      PaymentGroup paymentGroup = pPaymentGroupMapContainer.getPaymentGroup(paymentGroupName);
 
      if (paymentGroup instanceof GiftCertificate)
	found = compareGiftCertificates((GiftCertificate) pPaymentGroup, (GiftCertificate) paymentGroup);
    }

    if (found) return paymentGroupName;
    else return null;
  }
        
  public String getNewPaymentGroupName(PaymentGroup pPaymentGroup) {

    if (!(pPaymentGroup instanceof GiftCertificate)) return null;
    return ((GiftCertificate) pPaymentGroup).getGiftCertificateNumber();
  }
        
  //-------------------------------------
  // return true if each fields in the gift certificates are the same
  private boolean compareGiftCertificates(GiftCertificate pGiftCertificateA, GiftCertificate pGiftCertificateB)
  {
    if((pGiftCertificateA == null) && (pGiftCertificateB == null)) {
      return true;
    }
    if(pGiftCertificateA == null)
      return false;
    if(pGiftCertificateB == null)
      return false;

    String aGiftCertificateNumber = pGiftCertificateA.getGiftCertificateNumber();
    String bGiftCertificateNumber = pGiftCertificateB.getGiftCertificateNumber();

    String aProfileId = pGiftCertificateA.getProfileId();
    String bProfileId = pGiftCertificateB.getProfileId();

    if((aGiftCertificateNumber == null) &&
       ((bGiftCertificateNumber != null)))
      return false;
    if((aProfileId == null) &&
       ((bProfileId != null)))
      return false;

    if(
       (((aGiftCertificateNumber == null) && (bGiftCertificateNumber == null)) ||
        (aGiftCertificateNumber.equals(bGiftCertificateNumber)))
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
