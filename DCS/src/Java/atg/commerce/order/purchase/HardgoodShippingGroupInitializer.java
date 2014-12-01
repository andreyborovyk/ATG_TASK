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
import atg.repository.*;
import java.util.*;
import atg.core.util.Address;
import atg.core.util.ResourceUtils;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;

/**
 * The <code>HardgoodShippingGroupInitializer</code> implements the ShippingGroupInitializer interface.
 * The <code>initializeShippingGroups</code> method is used to create HardgoodShippingGroups
 * for the user based on their existence in the Profile.
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/HardgoodShippingGroupInitializer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class HardgoodShippingGroupInitializer extends GenericService implements ShippingGroupInitializer, ShippingGroupMatcher
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/HardgoodShippingGroupInitializer.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.purchase.PurchaseProcessResources";
  static final String USER_RESOURCE_NAME = "atg.commerce.order.purchase.UserMessages";
  static final String LOCALE_PARAM = "locale";

  static final String DEFAULT_HARDGOOD_SHIPPING_GROUP_NAME = "defaultHardgoodShippingGroupName";

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
   * @param pShippingGroupManager a <code>ShippingGroupManager</code> value
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
  // property: Profile
  //---------------------------------------------------------------------------
  Profile mProfile;

  /**
   * Set the Profile property.
   * @param pProfile a <code>Profile</code> value
   */
  public void setProfile(Profile pProfile) {
    mProfile = pProfile;
  }

  /**
   * Return the Profile property.
   * @return a <code>Profile</code> value
   */
  public Profile getProfile() {
    return mProfile;
  }

  //---------------------------------------------------------------------------
  // property: ShippingGroupMapContainer
  //---------------------------------------------------------------------------
  ShippingGroupMapContainer mShippingGroupMapContainer;

  /**
   * Set the ShippingGroupMapContainer property.
   * @param pShippingGroupMapContainer a <code>ShippingGroupMapContainer</code> value
   */
  public void setShippingGroupMapContainer(
      ShippingGroupMapContainer pShippingGroupMapContainer) {
    mShippingGroupMapContainer = pShippingGroupMapContainer;
  }

  /**
   * Return the ShippingGroupMapContainer property.
   * @return a <code>ShippingGroupMapContainer</code> value
   */
  public ShippingGroupMapContainer getShippingGroupMapContainer() {
    return mShippingGroupMapContainer;
  }


  //---------------------------------------------------------------------------
  // property: HardgoodShippingGroupType
  //---------------------------------------------------------------------------
  String mHardgoodShippingGroupType;

  /**
   * Specifies the ShippingGroup type used to create HardgoodShippingGroups.
   * @param pHardgoodShippingGroupType a <code>String</code> value
   */
  public void setHardgoodShippingGroupType(String pHardgoodShippingGroupType) {
    mHardgoodShippingGroupType = pHardgoodShippingGroupType;
  }

  /**
   * Returns the ShippingGroup type used to create HardgoodShippingGroups.
   * @return a <code>String</code> value
   */
  public String getHardgoodShippingGroupType() {
    return mHardgoodShippingGroupType;
  }

  //---------------------------------------------------------------------------
  // property: DefaultShippingGroupName
  //---------------------------------------------------------------------------
  String mDefaultShippingGroupName;

  /**
   * Specifies the name of the default ShippingGroup. This is only used then
   * the user's default shipping address is not amongst the secondary named
   * addresses.
   * @param pDefaultShippingGroupName a <code>String</code> value
   */
  public void setDefaultShippingGroupName(String pDefaultShippingGroupName) {
    mDefaultShippingGroupName = pDefaultShippingGroupName;
  }

  /**
   * Returns the name of the default ShippingGroup. This is only used when
   * the user's default shipping address is not amongst the secondary named
   * addresses. if no value is set for this in config file, then this invokes the method
   * {@link #nameDefaultShippingGroup(DynamoHttpServletRequest)} to retrieve
   * the value from Message Resources file which also allows to internationalize
   * the name.
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
      ResourceUtils.getUserMsgResource(DEFAULT_HARDGOOD_SHIPPING_GROUP_NAME,
                                       USER_RESOURCE_NAME,
                                       userResources);
    return defaultShippingGroupName;
  }



  //---------------------------------------------------------------------------
  // property: DefaultShippingAddressPropertyName
  //---------------------------------------------------------------------------
  String mDefaultShippingAddressPropertyName;

  /**
   * Specifies the Profile property in which the default shipping address is located.
   * @param pDefaultShippingAddressPropertyName a <code>String</code> value
   */
  public void setDefaultShippingAddressPropertyName(String pDefaultShippingAddressPropertyName) {
    mDefaultShippingAddressPropertyName = pDefaultShippingAddressPropertyName;
  }

  /**
   * Returns the Profile property in which the default shipping address is located.
   * @return a <code>String</code> value
   */
  public String getDefaultShippingAddressPropertyName() {
    return mDefaultShippingAddressPropertyName;
  }

  //---------------------------------------------------------------------------
  // property: ShippingAddressesPropertyName
  //---------------------------------------------------------------------------
  String mShippingAddressesPropertyName;

  /**
   * Specifies the Profile property in which shipping addresses are located.
   * @param pShippingAddressesPropertyName a <code>String</code> value
   */
  public void setShippingAddressesPropertyName(String pShippingAddressesPropertyName) {
    mShippingAddressesPropertyName = pShippingAddressesPropertyName;
  }

  /**
   * Returns the Profile property in which shipping addresses are located.
   * @return a <code>String</code> value
   */
  public String getShippingAddressesPropertyName() {
    return mShippingAddressesPropertyName;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>HardgoodShippingGroupInitializer</code> instance.
   *
   */
  public HardgoodShippingGroupInitializer () {}

  //--------------------------------------------------
  // Methods
  //--------------------------------------------------

  /**
   * <code>initializeShippingGroups</code> executes the <code>initializeHardgood</code>
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
    initializeHardgood(pProfile,
                       pShippingGroupMapContainer,
                       pRequest);
  }

  /**
   * <code>initializeHardgood</code> creates a <code>HardgoodShippingGroup</code> for each
   * item returned by the secondary address
   * <code>Profile</code> property given by <code>getShippingAddressesPropertyName</code>.
   * A <code>HardgoodShippingGroup</code> is also created for the item returned by the primary
   * address <code>Profile</code> property given by <code>getDefaultShippingAddressPropertyName</code>,
   * which is made the default <code>ShippingGroup</code> in the <code>ShippingGroupMapContainer</code>.
   *
   * @param pProfile a <code>Profile</code> value
   * @param pShippingGroupMapContainer a <code>ShippingGroupMapContainer</code> value
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @exception ShippingGroupInitializationException if an error occurs
   */
  protected void initializeHardgood(Profile pProfile,
                                    ShippingGroupMapContainer pShippingGroupMapContainer,
                                    DynamoHttpServletRequest pRequest)
    throws ShippingGroupInitializationException
  {
    String defAddrPropertyName = getDefaultShippingAddressPropertyName();
    if (defAddrPropertyName == null)
      throw new ShippingGroupInitializationException(ResourceUtils.getMsgResource("missingDefaultAddressPropertyName",
                                                                                MY_RESOURCE_NAME, sResourceBundle));

    String addrsPropertyName = getShippingAddressesPropertyName();
    if (addrsPropertyName == null)
      throw new ShippingGroupInitializationException(ResourceUtils.getMsgResource("missingShippingAddressesPropertyName",
                                                                                MY_RESOURCE_NAME, sResourceBundle));

    ShippingGroupManager sgm = getShippingGroupManager();
    if (sgm == null)
      throw new ShippingGroupInitializationException(ResourceUtils.getMsgResource("missingShippingGroupManager",
                                                                                MY_RESOURCE_NAME, sResourceBundle));

    ShippingGroup sg = null;

    try {
      // get the user's default shipping address
      RepositoryItem defaultAddressItem = (RepositoryItem) DynamicBeans.getSubPropertyValue(pProfile, defAddrPropertyName);
      boolean foundDefault = false;

      // get all the user's shipping addresses
      Map map = (Map) DynamicBeans.getSubPropertyValue(pProfile, addrsPropertyName);
      if (map != null) {
        Iterator iter = map.entrySet().iterator();

        // for each shipping address...
        while (iter.hasNext()) {
          Map.Entry entry = (Map.Entry)iter.next();
          String addressName = (String)entry.getKey();
          RepositoryItem addressItem = (RepositoryItem)entry.getValue();
          // 1) create a ShippingGroup
          sg = createHardgoodShippingGroup(addressItem, pProfile);

          // 2) add the ShippingGroup to ShippingGroupMapContainer
          if (sg != null)
            pShippingGroupMapContainer.addShippingGroup(addressName, sg);

          // 3) is this the default ShippingGroup?
          if ((!foundDefault) && (defaultAddressItem != null)) {
            if (addressItem == defaultAddressItem) {
              foundDefault = true;
              pShippingGroupMapContainer.setDefaultShippingGroupName(addressName);
            }
          }
        }
        if ((!foundDefault) && (defaultAddressItem != null)) {
          // create ShippingGroup based on default shipping address
          sg = createHardgoodShippingGroup(defaultAddressItem, pProfile);

          // add the ShippingGroup to ShippingGroupMapContainer
          if (sg != null) {

            String defaultShippingGroupName = getDefaultShippingGroupName();

            pShippingGroupMapContainer.addShippingGroup(defaultShippingGroupName, sg);
            pShippingGroupMapContainer.setDefaultShippingGroupName(defaultShippingGroupName);
          }
        }
      }
    } catch (Exception exc) {
      throw new ShippingGroupInitializationException(exc);
    }
  }

  /**
   * <code>createHardgoodShippingGroup</code> creates a HardgoodShippingGroup from a Profile
   * and a RepositoryItem that represents an address.
   *
   * @param pAddressItem a <code>RepositoryItem</code> value
   * @param pProfile a <code>Profile</code> value
   * @return a <code>ShippingGroup</code> value
   * @exception CommerceException if an error occurs
   * @exception PropertyNotFoundException if an error occurs
   * @exception RepositoryException if an error occurs
   */
  protected ShippingGroup createHardgoodShippingGroup(RepositoryItem pAddressItem,
                                                      Profile pProfile)
    throws CommerceException, PropertyNotFoundException, RepositoryException
  {
    ShippingGroupManager sgm = getShippingGroupManager();
    HardgoodShippingGroup shippingGroup = (HardgoodShippingGroup) sgm.createShippingGroup(getHardgoodShippingGroupType());
    OrderTools.copyAddress(pAddressItem, shippingGroup.getShippingAddress());
    return shippingGroup;
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

    if (!(pShippingGroup instanceof HardgoodShippingGroup)) return null;

    Address address = ((HardgoodShippingGroup) pShippingGroup).getShippingAddress();

    Set shippingGroupNames = pShippingGroupMapContainer.getShippingGroupNames();
    if (shippingGroupNames == null) return null;

    Iterator nameIter = shippingGroupNames.iterator();
    String shippingGroupName = null;
    boolean found = false;
    while (nameIter.hasNext() && !found) {
      shippingGroupName = (String) nameIter.next();
      ShippingGroup shippingGroup = pShippingGroupMapContainer.getShippingGroup(shippingGroupName);

      if (shippingGroup instanceof HardgoodShippingGroup) {
        Address shipAddress = ((HardgoodShippingGroup) shippingGroup).getShippingAddress();
        found = compareAddresses(address, shipAddress);
      }
    }

    if (found) return shippingGroupName;
    else return null;
  }

  /**
   * This method returns an unique address nickname from profile and ShippingGroupMapContainer. If the same address nickname
   * exists in the profile or map, this method will get an unique address nick name.
   *
   * @param pShippingGroup
   * @return UniqueNickname returns a unique nickname, based on the profile and map container addresses.   *
   */
  public String getNewShippingGroupName(ShippingGroup pShippingGroup) {

    if (!(pShippingGroup instanceof HardgoodShippingGroup)) return null;
    return getShippingGroupManager().getOrderTools().getProfileTools().getUniqueAddressNickname(((HardgoodShippingGroup) pShippingGroup).getShippingAddress(),
                                                                                                  getShippingGroupMapContainer().getShippingGroupNames(),
                                                                                                  null);
  }

  //-------------------------------------
  // return true if each field in the addresses are the same
  // or if both address are null
  protected boolean compareAddresses(Address pAddressA, Address pAddressB)
  {
    if((pAddressA == null) && (pAddressB == null)) {
      return true;
    }
    if(pAddressA == null)
      return false;
    if(pAddressB == null)
      return false;

    return pAddressA.equals(pAddressB);
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
      if (getProfile() == null
          && mConfiguration.getProfile() instanceof Profile) {
        setProfile((Profile) mConfiguration.getProfile());
      }
      if (getShippingGroupMapContainer() == null) {
        setShippingGroupMapContainer(mConfiguration.getShippingGroupMapContainer());
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
