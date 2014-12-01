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
import atg.userprofiling.Profile;
import atg.servlet.DynamoHttpServletRequest;
import atg.commerce.order.*;
import atg.commerce.CommerceException;
import atg.beans.*;
import atg.repository.*;
import java.util.*;
import atg.core.util.ResourceUtils;
import atg.nucleus.ServiceException;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;

/**
 * The <code>ElectronicShippingGroupInitializer</code> implements the ShippingGroupInitializer interface.
 * The <code>initializeShippingGroups</code> method is used to create an ElectronicShippingGroup
 * for the user based on the existence of an email address in the Profile.
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/ElectronicShippingGroupInitializer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ElectronicShippingGroupInitializer extends GenericService 
  implements ShippingGroupInitializer, ShippingGroupMatcher
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/ElectronicShippingGroupInitializer.java#2 $$Change: 651448 $";
    
  static final String MY_RESOURCE_NAME = "atg.commerce.order.purchase.PurchaseProcessResources";
  static final String USER_RESOURCE_NAME = "atg.commerce.order.purchase.UserMessages";
  static final String LOCALE_PARAM = "locale";

  static final String DEFAULT_ELECTRONIC_SHIPPING_GROUP_NAME = "defaultElectronicShippingGroupName";
  
  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

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
  // property: ShippingGroupManager
  //---------------------------------------------------------------------------
  ShippingGroupManager mShippingGroupManager;

  /**
   * Specifies the ShippingGroupManager to use in creating ShippingGroups.
   * @param pShippingGroupManager a <code>ShippingGroupManager</code> valuec
   */
  public void setShippingGroupManager(ShippingGroupManager pShippingGroupManager) {
    mShippingGroupManager = pShippingGroupManager;
  }

  /**
   * Returns the ShippingGroupManager to use in creating ShippingGroups.
   * @return a <code>ShippingGroupManager</code> value
   */
  public ShippingGroupManager getShippingGroupManager() {
    return mShippingGroupManager;
  }

  //---------------------------------------------------------------------------
  // property: ElectronicShippingGroupType
  //---------------------------------------------------------------------------
  String mElectronicShippingGroupType;

  /**
   * Specifies the ShippingGroup type used to create ElectronicShippingGroups.
   * @param pElectronicShippingGroupType a <code>String</code> value
   */
  public void setElectronicShippingGroupType(String pElectronicShippingGroupType) {
    mElectronicShippingGroupType = pElectronicShippingGroupType;
  }

  /**
   * Returns the ShippingGroup type used to create ElectronicShippingGroups.
   * @return a <code>String</code> value
   */
  public String getElectronicShippingGroupType() {
    return mElectronicShippingGroupType;
  }

  //---------------------------------------------------------------------------
  // property: DefaultShippingGroupName
  //---------------------------------------------------------------------------
  String mDefaultShippingGroupName;

  /**
   * Specifies the name of the default ShippingGroup.
   * @param pDefaultShippingGroupName a <code>String</code> value
   */
  public void setDefaultShippingGroupName(String pDefaultShippingGroupName) {
    mDefaultShippingGroupName = pDefaultShippingGroupName;
  }

  /**
   * Returns the name of the default ShippingGroup. if no value is set
   * for this in config file, then this invokes the method
   * {@link #nameDefaultShippingGroup(DynamoHttpServletRequest)} to retrieve
   * the value from Message Resources file which also allows to internationalize
   * the name. This property is not used at all for ElectronicShippingGroupInitializer,
   * but has been provided to be consistent with other Initializers.
   * @return a <code>String</code> value
   */
  public String getDefaultShippingGroupName() {
    if ( mDefaultShippingGroupName != null) {
      return mDefaultShippingGroupName;
    } // end of if ()
    
    return nameDefaultShippingGroup(ServletUtil.getCurrentRequest());
  }

  /**
   * Returns the name of the default ShippingGroup. This is only used when
   * the user's default shipping address is not amongst the secondary named
   * addresses. The name of the default ShippingGroup is retreived from
   * UserMessages so it can be customized by editing the value in UserMessages.
   * @return a <code>String</code> value
   */
  public String nameDefaultShippingGroup(DynamoHttpServletRequest pRequest) {
    ResourceBundle userResources =
      atg.core.i18n.LayeredResourceBundle.getBundle(USER_RESOURCE_NAME, getUserLocale(pRequest));
    String defaultShippingGroupName =
      ResourceUtils.getUserMsgResource(DEFAULT_ELECTRONIC_SHIPPING_GROUP_NAME,
                                       USER_RESOURCE_NAME,
                                       userResources);
    return defaultShippingGroupName;
  }
  
  //---------------------------------------------------------------------------
  // property: EmailAddressPropertyName
  //---------------------------------------------------------------------------
  String mEmailAddressPropertyName;

  /**
   * Specifies the Profile property in which the email address is located.
   * @param pEmailAddressPropertyName a <code>String</code> value
   */
  public void setEmailAddressPropertyName(String pEmailAddressPropertyName) {
    mEmailAddressPropertyName = pEmailAddressPropertyName;
  }

  /**
   * Returns the Profile property in which the email address is located.
   * @return a <code>String</code> value
   */
  public String getEmailAddressPropertyName() {
    return mEmailAddressPropertyName;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>ElectronicShippingGroupInitializer</code> instance.
   *
   */
  public ElectronicShippingGroupInitializer () {}
  
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  /**
   * <code>initializeShippingGroups</code> executes the <code>initializeElectronic</code>
   * method.
   *
   * @param pProfile a <code>Profile</code> value
   * @param pShippingGroupMapContainer a <code>ShippingGroupMapContainer</code> value
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @exception ShippingGroupInitializationException if an error occurs
   */
  public void initializeShippingGroups (Profile pProfile,
                                        ShippingGroupMapContainer pShippingGroupMapContainer,
                                        DynamoHttpServletRequest pRequest)
    throws ShippingGroupInitializationException
  {
    initializeElectronic(pProfile,
                         pShippingGroupMapContainer,
                         pRequest);
  }

  /**
   * <code>initializeElectronic</code> creates an <code>ElectronicShippingGroup</code> based
   * on the <code>Profile</code> property given by <code>getEmailAddressPropertyName</code>. The
   * <code>ElectronicShippingGroup</code> is added to the <code>ShippingGroupMapContainer</code>
   * and made the default <code>ShippingGroup</code>.
   *
   * @param pProfile a <code>Profile</code> value
   * @param pShippingGroupMapContainer a <code>ShippingGroupMapContainer</code> value
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @exception ShippingGroupInitializationException if an error occurs
   */
  protected void initializeElectronic(Profile pProfile,
                                      ShippingGroupMapContainer pShippingGroupMapContainer,
                                      DynamoHttpServletRequest pRequest)
    throws ShippingGroupInitializationException
  {
    String emailPropertyName = getEmailAddressPropertyName();
    if (emailPropertyName == null)
      throw new ShippingGroupInitializationException(ResourceUtils.getMsgResource("missingEmailAddressPropertyName",
                                                                                MY_RESOURCE_NAME, sResourceBundle));
    

    ShippingGroupManager sgm = getShippingGroupManager();
    if (sgm == null)
      throw new ShippingGroupInitializationException(ResourceUtils.getMsgResource("missingShippingGroupManager",
                                                                                MY_RESOURCE_NAME, sResourceBundle));
    ElectronicShippingGroup shippingGroup = null;
    try {
      String emailAddress = (String) DynamicBeans.getSubPropertyValue(pProfile, emailPropertyName);
      shippingGroup = (ElectronicShippingGroup) sgm.createShippingGroup(getElectronicShippingGroupType());
      shippingGroup.setEmailAddress(emailAddress);
      if (shippingGroup != null) {
        pShippingGroupMapContainer.addShippingGroup(emailAddress, shippingGroup);
        pShippingGroupMapContainer.setDefaultShippingGroupName(emailAddress);
      }
    } catch (Exception exc) {
      throw new ShippingGroupInitializationException (exc);
    }
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
    if ( pRequest != null) {

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
    } // end of if ()
    return Locale.getDefault();
  }

  public String matchShippingGroup(ShippingGroup pShippingGroup, 
                                   ShippingGroupMapContainer pShippingGroupMapContainer) {

    if (!(pShippingGroup instanceof ElectronicShippingGroup)) return null;

    String emailAddress = ((ElectronicShippingGroup) pShippingGroup).getEmailAddress();

    Set shippingGroupNames = pShippingGroupMapContainer.getShippingGroupNames();
    if (shippingGroupNames == null) return null;

    Iterator nameIter = shippingGroupNames.iterator();
    String shippingGroupName = null;
    boolean found = false;
    while (nameIter.hasNext() && !found) {
      shippingGroupName = (String) nameIter.next();
      ShippingGroup shippingGroup = pShippingGroupMapContainer.getShippingGroup(shippingGroupName);
 
      if (shippingGroup instanceof ElectronicShippingGroup) {
        String shipAddress = ((ElectronicShippingGroup) shippingGroup).getEmailAddress();
        if (shipAddress != null) found = shipAddress.equals(emailAddress);
      }
    }

    if (found) return shippingGroupName;
    else return null;
  }
        
  public String getNewShippingGroupName(ShippingGroup pShippingGroup) {

    if (!(pShippingGroup instanceof ElectronicShippingGroup)) return null;
    return ((ElectronicShippingGroup) pShippingGroup).getEmailAddress();
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
   * <LI>shippingGroupManager
   * </UL>
   **/
  protected void copyConfiguration() {
    if (mConfiguration != null) {
      if (getShippingGroupManager() == null) {
        setShippingGroupManager(mConfiguration.getShippingGroupManager());
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
