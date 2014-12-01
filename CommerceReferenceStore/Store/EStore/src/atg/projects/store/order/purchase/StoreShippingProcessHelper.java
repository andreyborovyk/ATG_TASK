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

package atg.projects.store.order.purchase;

import atg.commerce.CommerceException;

import atg.commerce.order.*;
import atg.commerce.order.purchase.*;

import atg.commerce.pricing.PricingModelHolder;

import atg.commerce.profile.CommercePropertyManager;

import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;

import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.GiftWrapCommerceItem;
import atg.projects.store.order.StoreOrderTools;

import atg.projects.store.profile.StoreProfileTools;
import atg.projects.store.profile.StorePropertyManager;

import atg.repository.*;
import atg.nucleus.ServiceMap;
import atg.service.pipeline.RunProcessException;
import atg.service.pipeline.PipelineResult;
import atg.userprofiling.address.AddressTools;

import javax.servlet.ServletException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.beans.IntrospectionException;
import java.io.IOException;

/**
 * Helper methods for handling shipping information
 *
 * @author ATG
 * @version $Version$
 */
public class StoreShippingProcessHelper extends StorePurchaseProcessHelper {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/purchase/StoreShippingProcessHelper.java#2 $$Change: 651448 $";

  //---------------------------------------------------------------------------
  // Constants
  //---------------------------------------------------------------------------
  /**
   * New address constant.
   */
  public static String NEW_ADDRESS = "NEW";

  /**
   * Country delimiter.
   */
  public static String COUNTRY_DELIM = "|";

  /**
   * Invalid state for method message key.
   */
  public static String MSG_INVALID_STATE_FOR_METHOD = "invalidStateForMethod";

  /**
   * Restricted shipping message key.
   */
  public static String MSG_RESTRICTED_SHIPPING = "restrictedShipping";

  /**
   * Duplicate nickname message key.
   */
  public static String MSG_DUPLICATE_NICKNAME = "duplicateNickname";

  /**
   * Invalid city address message key.
   */
  public static String MSG_INVALID_CITY_ADDRESS = "invalidCityAddress";

  /**
   * Invalid Street Address message key
   */
  public static String MSG_INVALID_STREET_ADDRESS = "invalidStreetAddress";

  /**
   * Error updating shipping groups message key.
   */
  public static String MSG_ERROR_UPDATE_SHIPPINGGROUP = "errorUpdateShippingGroup";


  //---------------------------------------------------------------------------
  // property: commerceItemShippingInfoTools
  //---------------------------------------------------------------------------
  private CommerceItemShippingInfoTools mCommerceItemShippingInfoTools;

  /**
   * Returns the tools component containing the API for modifying
   * CommerceItemShippingInfos
   * @return CommerceItemShippingInfoTools
   */
  public CommerceItemShippingInfoTools getCommerceItemShippingInfoTools()
  {
    return mCommerceItemShippingInfoTools;
  }

  /**
   * Commerce Item Shipping Info Tools to set
   * @param pCommerceItemShippingInfoTools
   */
  public void setCommerceItemShippingInfoTools(
      CommerceItemShippingInfoTools pCommerceItemShippingInfoTools)
  {
    mCommerceItemShippingInfoTools = pCommerceItemShippingInfoTools;
  }

  //---------------------------------------------------------------------------
  // property: invalidCityPatterns
  //---------------------------------------------------------------------------
  private Pattern[] mInvalidCityPatterns;
  /**
   * @param pInvalidCityPatterns - Invalid city patterns.
   */
  protected void setInvalidCityPatterns(Pattern[] pInvalidCityPatterns) {
    mInvalidCityPatterns = pInvalidCityPatterns;
  }

  /**
   * @return mInvalidCityPatterns - Invalid city patterns.
   */
  protected Pattern[] getInvalidCityPatterns() {
    return mInvalidCityPatterns;
  }

  /**
   * @param pInvalidCityStrings - invalid city strings.
   */
  public void setInvalidCityStrings(String[] pInvalidCityStrings) {
    ArrayList patterns = new ArrayList();

    for (int i = 0; i < pInvalidCityStrings.length; i++) {
      String regexString = pInvalidCityStrings[i];
      Pattern pattern = Pattern.compile(regexString);
      patterns.add(pattern);
    }

    mInvalidCityPatterns = (Pattern[]) patterns.toArray(new Pattern[patterns.size()]);
  }

  //---------------------------------------------------------------------------
  // property: invalidStreetStrings
  //---------------------------------------------------------------------------
  private String[] mInvalidStreetStrings;
  /**
   * @return invalid streets strings.
   */
  public String[] getInvalidStreetStrings() {
    return mInvalidStreetStrings;
  }

  /**
   * @param pInvalidStreetStrings - invalid streets strings.
   */
  public void setInvalidStreetStrings(String[] pInvalidStreetStrings) {
    mInvalidStreetStrings = pInvalidStreetStrings;
  }

  //---------------------------------------------------------------------------
  // property: Invalid states for shipping method
  //---------------------------------------------------------------------------
  private Properties mInvalidStatesForShipMethod;

  /**
   * Expects a pipe-dilimited string containing 2-digit state abbreviations
   * that are invalid for a particular shipping method. The key is the name of
   * the shipping method the value is the pipe-delimited list of states.
   *
   * @param pInvalidStatesForShipMethod - invalid states for shipping methods
   */
  public void setInvalidStatesForShipMethod(Properties pInvalidStatesForShipMethod) {
    mInvalidStatesForShipMethod = pInvalidStatesForShipMethod;
  }

  /**
   * Expects a pipe-dilimited string containing 2-digit state abbreviations
   * that are invalid for a particular shipping method. The key is the name of
   * the shipping method the value is the pipe-delimited list of states.
   *
   * @return invalid states for shipping method
   */
  public Properties getInvalidStatesForShipMethod() {
    return mInvalidStatesForShipMethod;
  }

  //---------------------------------------------------------------------------
  // property: Validate shipping restrictions
  //---------------------------------------------------------------------------
  private boolean mValidateShippingRestriction;

  /**
   * @return true if shipping restrictions should be validated,
   * false - otherwise.
   */
  public boolean getValidateShippingRestriction() {
    return mValidateShippingRestriction;
  }

  /**
   * @param pValidateShippingRestriction - true if shipping restrictions
   * should be validated.
   */
  public void setValidateShippingRestriction(boolean pValidateShippingRestriction) {
    mValidateShippingRestriction = pValidateShippingRestriction;
  }

  //---------------------------------------------------------------------------
  // property: Validate shipping groups
  // flag indicating if the shipping groups should be validated after being applied to the order
  // The default setting is true.
  //---------------------------------------------------------------------------
  protected boolean mValidateShippingGroups = true;

  /**
   * Sets property ValidateShippingGroups
   **/
  public void setValidateShippingGroups(boolean pValidateShippingGroups) {
    mValidateShippingGroups = pValidateShippingGroups;
  }

  /**
   * Returns property ValidateShippingGroups.
   **/
  public boolean isValidateShippingGroups() {
    return mValidateShippingGroups;
  }

  //---------------------------------------------------------------------------
  // property: ValidateShippingGroupsChainId
  //---------------------------------------------------------------------------
  String mValidateShippingGroupsChainId;

  /**
   * Set the ValidateShippingGroupsChainId property.
   * @param pValidateShippingGroupsChainId a <code>String</code> value
   * @beaninfo description: ChainId to execute for validating ShippingGroups.
   */
  public void setValidateShippingGroupsChainId(String pValidateShippingGroupsChainId) {
    mValidateShippingGroupsChainId = pValidateShippingGroupsChainId;
  }

  /**
   * Return the ValidateShippingGroupsChainId property.
   * @return a <code>String</code> value
   */
  public String getValidateShippingGroupsChainId() {
    return mValidateShippingGroupsChainId;
  }

  //---------------------------------------------------------------------------
  // property: consolidateShippingInfos
  //---------------------------------------------------------------------------
  protected boolean mConsolidateShippingInfosBeforeApply=false;
  /**
   * @return Returns the consolidateShippingInfosBeforeApply.
   */
  public boolean isConsolidateShippingInfosBeforeApply()
  {
    return mConsolidateShippingInfosBeforeApply;
  }

  /**
   * Sets the consolidateShippingInfosBeforeApply property.
   * <p>
   * This property is used to control is the CommerceItemShippingInfo objects are
   * consolidated prior to being applied to the order.
   * <p>
   * @see #consolidateShippingInfos(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
   * @param pConsolidateShippingInfosBeforeApply The consolidateShippingInfosBeforeApply to set.
   */
  public void setConsolidateShippingInfosBeforeApply(
      boolean pConsolidateShippingInfosBeforeApply)
  {
    mConsolidateShippingInfosBeforeApply = pConsolidateShippingInfosBeforeApply;
  }

  //---------------------------------------------------------------------------
  // property: ApplyDefaultShippingGroup
  //---------------------------------------------------------------------------
  boolean mApplyDefaultShippingGroup;

  /**
   * Set the ApplyDefaultShippingGroup property.
   * @param pApplyDefaultShippingGroup a <code>boolean</code> value
   * @beaninfo description: Should the default ShippingGroup apply?
   */
  public void setApplyDefaultShippingGroup(boolean pApplyDefaultShippingGroup) {
    mApplyDefaultShippingGroup = pApplyDefaultShippingGroup;
  }

  /**
   * Return the ApplyDefaultShippingGroup property.
   * @return a <code>boolean</code> value
   */
  public boolean isApplyDefaultShippingGroup() {
    return mApplyDefaultShippingGroup;
  }

  //---------------------------------------------------------------------------
  // property: NonProfileShippingAddressesMap
  //---------------------------------------------------------------------------
  Map<String, Address> mNonProfileShippingAddressesMap = new HashMap<String, Address>();
  
  public Map<String, Address> getNonProfileShippingAddressesMap() {
    return mNonProfileShippingAddressesMap;
  }

  public void setNonProfileShippingAddressesMap(
      Map<String, Address> pNonProfileShippingAddressesMap) {
    mNonProfileShippingAddressesMap = pNonProfileShippingAddressesMap;
  }  

  //---------------------------------------------------------------------------
  // Utility Methods
  //---------------------------------------------------------------------------

  /**
   * Returns the number of non-gift hardgood shipping groups
   * with commerce item relationships.
   *
   * @param pOrder Order whose non-gift hardgood shipping group count is to be retrieved
   * @return the number of non-empty non-gift hardgood shipping groups
   */
  public int getNonGiftHardgoodShippingGroupCount(Order pOrder) {
    return getShippingGroupManager().getNonGiftHardgoodShippingGroupCount(pOrder);
  }

  /**
   * Returns true if the order has at least one hargood shipping group
   * with commerce item relationships. Both non-gift and gift shipping
   * groups are considered.
   *
   * @param pOrder Order to be checked
   * @return true if the order has at least one non-empty hardgood shipping group.
   */
  public boolean isAnyHardgoodShippingGroups(Order pOrder) {
    return getShippingGroupManager().isAnyHardgoodShippingGroups(pOrder);
  }

  /**
   * Returns true if the order has more than one non-gift hardgood shipping group
   * with commerce item relationships.
   * <p>
   * @param pOrder Order to be checked
   * @return boolean true if the order has more than one non-gift hardgood shipping group.
   */
  public boolean isMultipleNonGiftHardgoodShippingGroups(Order pOrder) {
    return getShippingGroupManager().isMultipleNonGiftHardgoodShippingGroups(pOrder);
  }

  /**
   * Returns true if the order has more than one hardgood shipping group
   * with commerce item relationships.
   * <p>
   * @param pOrder Order to be checked
   * @return boolean true if the order has more than one hardgood shipping group.
   */
  public boolean isMultipleHardgoodShippingGroupsWithRelationships(Order pOrder) {
    return getShippingGroupManager().isMultipleHardgoodShippingGroupsWithRelationships(pOrder);
  }

  /**
   * Determines if the total quantity of all non-gift hardgood items is more than one.
   * @param pOrder Order to be checked
   * @return true if the the non-gift hg item quantity is more than one.
   */
  public boolean isMultipleNonGiftHardgoodItems(Order pOrder) {
    return getShippingGroupManager().isMultipleNonGiftHardgoodItems(pOrder);
  }

  /**
   * Determines if there are any non-gift hardgood shipping groups with
   * relationships.
   * <p>
   * @param pOrder Order to be checked
   * @return true if any non-gift hardgood shipping groups with relationships are found
   */
  public boolean isAnyNonGiftHardgoodShippingGroups(Order pOrder) {
    return getShippingGroupManager().isAnyNonGiftHardgoodShippingGroups(pOrder);
  }

  /**
   * Returns the non-gift hardgood shipping groups
   * with commerce item relationships.
   * <p>
   * @param pOrder Order whose non-gift hard good shipping groups are to be retrieved
   * @return a list of non-gift hardgood shipping groups
   * with commerce item relationships
   */
  public List getNonGiftHardgoodShippingGroups(Order pOrder) {
    return getShippingGroupManager().getNonGiftHardgoodShippingGroups(pOrder);
  }

  /**
   * Returns the first non-gift hardgood shipping group with relationships from the order.
   * @param pOrder Order whose non-gift hard good shipping groups is to be retrieved
   * @return the first non-gift hardgood shipping group or null if there isn't one.
   */
  public HardgoodShippingGroup getFirstNonGiftHardgoodShippingGroupWithRels(Order pOrder) {
    return getShippingGroupManager().getFirstNonGiftHardgoodShippingGroupWithRels(pOrder);
  }

  /**
   * Set the profile's default shipping method if it's not already set.
   * @param pProfile - profile
   * @param pShippingMethod - shipping method
   */
  public void saveDefaultShippingMethod(RepositoryItem pProfile, String pShippingMethod) {
    getStoreOrderTools().saveDefaultShippingMethod(pProfile, pShippingMethod);
  }

  /**
   * Copies the new shipping group address to the order's credit card payment group address.
   *
   * @param pOrder Order to copy the billing address to
   * @param pAddress Address to copy as billing address
   */
  public void copyShippingToBilling(Order pOrder, Address pAddress) {
    getStoreOrderTools().copyShippingToBilling(pAddress, pOrder);
  }


  /**
   * Saves address to address book.
   * @param pAddress - address Address to save
   * @param pNickName - nickname - Nickname for the address being saved
   */
  public void saveAddressToAddressBook(Address pAddress, String pNickName, RepositoryItem pProfile)
      throws CommerceException {
    getStoreOrderTools().saveAddressToAddressBook(pProfile, pAddress, pNickName);
  }

  /**
   * Returns a list of all shipping groups that contain gifts.
   * @param pOrder Order whose shipping sroups are to be retrieved
   * @return mGiftShippingGroups a lists of shipping groups for the order
   */
  public List getGiftShippingGroups(Order pOrder) {
    try
    {
      return getGiftlistManager().getGiftShippingGroups(pOrder);
    }
    catch (CommerceException e)
    {
      if(isLoggingError())
        logError(e);
    }
    return null;
  }
  
  


  //---------------------------------------------------------------------------
  // Helper Methods
  //---------------------------------------------------------------------------

  /**
   * <code>runProcessValidateShippingGroups</code> runs a configurable Pipeline chain
   * to validate ShippingGroups or prepare for the next checkout phase.
   *
   * @param pOrder an <code>Order</code> value
   * @param pPricingModels a <code>PricingModelHolder</code> value
   * @param pLocale a <code>Locale</code> value
   * @param pProfile a <code>RepositoryItem</code> value
   * @param pExtraParameters a <code>Map</code> value
   * @exception atg.service.pipeline.RunProcessException if an error occurs
   */
  protected PipelineResult runProcessValidateShippingGroups(Order pOrder, PricingModelHolder pPricingModels,
                                                 Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
    throws RunProcessException
  {
    return runProcess(getValidateShippingGroupsChainId(), pOrder, pPricingModels, pLocale,
                                       pProfile, pExtraParameters);
  }

  /**
  * Determines the shipping method in the following order of precendence: shipping group's
  * shipping method, the profile's default shipping method, and the default configured shipping method.
  *
  * @param pProfile Shopper profile
  * @param pShippingGroup shipping group for the order
  * @param pDefaultShippingMethod the default configured shipping method
  * @return shipping method being used for the shipping group
  */
  public String initializeShippingMethod(RepositoryItem pProfile,
                                         ShippingGroup pShippingGroup,
                                         String pDefaultShippingMethod) {

    // if the shipping group has a configured shipping method use it
    if (!StringUtils.isEmpty(pShippingGroup.getShippingMethod()) &&
        !pShippingGroup.getShippingMethod().equals(pShippingGroup.getShippingGroupClassType())) {

      return pShippingGroup.getShippingMethod();
    }

    // if the profile has a default shipping method use it
    StoreOrderTools orderTools = getStoreOrderTools();
    StorePropertyManager pm = (StorePropertyManager) orderTools.getProfileTools().getPropertyManager();
    String defaultMethodName = pm.getDefaultShippingMethodPropertyName();
    String defaultMethod = (String) pProfile.getPropertyValue(defaultMethodName);

    if (!StringUtils.isEmpty(defaultMethod)) {
      return defaultMethod;
    }

    // use the configured default shipping method
    return pDefaultShippingMethod;
  }

  /**
   * Retrieve the collection of all hardgood shipping groups referenced by commerce item infos.
   *
   * @param pShippingGroupMapContainer map of all shipping groups for the profile
   * @param pCommerceItemShippingInfoContainer map of all commerce item infos for the order
   * @return collection of all hardgood shipping groups referenced by commerce item infos
   */
  public Collection getUniqueHardgoodShippingGroups(ShippingGroupMapContainer pShippingGroupMapContainer,
                    CommerceItemShippingInfoContainer pCommerceItemShippingInfoContainer){

    //get the commerce item infos and compile a collection of all hardgood shipping groups they reference
    Collection hardgoodshippingGroups = new ArrayList();
    Set<String> addedShippingGroupIds = new HashSet<String>();
    Collection infos = pCommerceItemShippingInfoContainer.getAllCommerceItemShippingInfos();

    if (infos != null) {
      CommerceItemShippingInfo info;
      ShippingGroup sg;
      Iterator infoerator = infos.iterator();

      while (infoerator.hasNext()) {
        info = (CommerceItemShippingInfo) infoerator.next();
        sg = pShippingGroupMapContainer.getShippingGroup(info.getShippingGroupName());

        if (sg instanceof HardgoodShippingGroup && !addedShippingGroupIds.contains(sg.getId())) {
          hardgoodshippingGroups.add(sg);
          addedShippingGroupIds.add(sg.getId());
        }
      }
    }
    return hardgoodshippingGroups;
  }

  /**
   * Get the List of all the CommerceItemShippingInfos for hardgoods
   * from the CommerceItemShippingInfoMap. If a CommerceItemShippingInfo
   * has no shipping group, assume the item represents hardgoods.
   *
   * @param pShippingGroupMapContainer map of all shipping groups for the profile
   * @param pCommerceItemShippingInfoContainer map of all commerce item infos for the order
   * @return a <code>List</code> value
   */
  public List getAllHardgoodCommerceItemShippingInfos(ShippingGroupMapContainer pShippingGroupMapContainer,
                                                      CommerceItemShippingInfoContainer pCommerceItemShippingInfoContainer) {
    List hardgoodCommerceItemShippingInfos =
        pCommerceItemShippingInfoContainer.getAllCommerceItemShippingInfos();

    Iterator iter = hardgoodCommerceItemShippingInfos.iterator();
    while (iter.hasNext()) {
      CommerceItemShippingInfo info = (CommerceItemShippingInfo) iter.next();
      String nickname = info.getShippingGroupName();

      if ((nickname != null) && !(pShippingGroupMapContainer.getShippingGroup(nickname) instanceof HardgoodShippingGroup)) {
        iter.remove();
      }
    }
    return hardgoodCommerceItemShippingInfos;
  }

  /**
   * This method is called when using multiple shipping groups. This method sets the
   * shipping group name for all the gift wrap commerce item infos in the map.
   * <p>
   * @param pCommerceItemShippinGroupInfos all the commerce item infos that will be applied
   * to the order.
   */
  protected void setGiftWrapItemShippingGroupInfos(List pCommerceItemShippinGroupInfos) {
    //first we must find a non-giftwrap commerce item info and get the shipping group name
    //to which it is pointing.
    Iterator cinfoiter = pCommerceItemShippinGroupInfos.iterator();
    CommerceItemShippingInfo cInfo;
    String shippingGroupName = null;
    String shippingGroupMethod = null;

    while (cinfoiter.hasNext() && (shippingGroupName == null)) {
      cInfo = (CommerceItemShippingInfo) cinfoiter.next();
      if (!(cInfo.getCommerceItem() instanceof GiftWrapCommerceItem)) {
        shippingGroupName = cInfo.getShippingGroupName();
        shippingGroupMethod = cInfo.getShippingMethod();
      }
    }

    //if we found one, look through again and set any gift wrap items to that shipping group
    if (shippingGroupName != null) {
      cinfoiter = pCommerceItemShippinGroupInfos.iterator();

      while (cinfoiter.hasNext()) {
        cInfo = (CommerceItemShippingInfo) cinfoiter.next();
        if (cInfo.getCommerceItem() instanceof GiftWrapCommerceItem) {
          cInfo.setShippingGroupName(shippingGroupName);
          cInfo.setShippingMethod(shippingGroupMethod);
        }
      }
    }
  }

  /**
   * Creates a HardgoodShippingGroup from the profile's default shipping address item.
   *
   * @param pProfile - shopper profile
   * @return  hardgood shipping group
   */
  public HardgoodShippingGroup createShippingGroupFromDefaultAddress(RepositoryItem pProfile) {

    StoreProfileTools profileTools = (StoreProfileTools) getStoreOrderTools().getProfileTools();
    RepositoryItem item = profileTools.getDefaultShippingAddress(pProfile);

    if (item != null) {
      try {
        HardgoodShippingGroup hgsg = (HardgoodShippingGroup) getShippingGroupManager().createShippingGroup();

        if (hgsg != null) {
          Address address = profileTools.getAddressFromRepositoryItem(item);
          hgsg.setShippingAddress(address);

          return hgsg;
        }
      } catch (CommerceException e) {
        if (isLoggingError()) {
          logError(e);
        }
      } catch (RepositoryException e) {
        if (isLoggingError()) {
          logError(e);
        }
      }
    }
    return null;
  }

  /**
   * Changes the shipping group name and shipping method for a collection
   * of items in the CommerceItemShippingInfoContainer.
   * @param pCommerceItemShippingInfos the colection of items to change
   * @param pShippingGroupName if this is not null, each info item's shipping group name is set to this value
   * @param pShippingMethod if this is not null, each info item's shipping method is set to this value.
   */
  public void changeShippingGroupForCommerceItemShippingInfos(Collection pCommerceItemShippingInfos,
                                                              String pShippingGroupName,
                                                              String pShippingMethod) {
    Iterator commerceItemShippingInfoerator = pCommerceItemShippingInfos.iterator();

    while (commerceItemShippingInfoerator.hasNext()) {
      CommerceItemShippingInfo cisi = (CommerceItemShippingInfo) commerceItemShippingInfoerator.next();

      if (pShippingGroupName != null) {
        cisi.setShippingGroupName(pShippingGroupName);
      }

      if (pShippingMethod != null) {
        cisi.setShippingMethod(pShippingMethod);
      }
    }
  }

  /**
   * Tries to find an appropriate hardgood shipping group within the shipping group map container specified.
   * Shipping group is good enough if it has the same shipping address as the address specified.
   * If such shipping group can't be found, this method will create a new one.
   * 
   * @param pProfile current user's profile
   * @param pOrder order to be searched for appropriate shipping group
   * @param pNewShipToAddressName nickname for the new shipping group 
   * @param pAddress address to be used
   * @param pShippingGroupMapContainer an instance of ShippingGroupContainerService
   * @param pSaveShippingAddress true if this address should be saved into profile specified
   * @return shipping group name if found and null if new shipping group has been created
   * @throws CommerceException if something goes wrong
   * @throws IntrospectionException if something goes wrong
   */
  @SuppressWarnings("unchecked") //ok, we know which Map we are using
  public String findOrAddShippingAddress(RepositoryItem pProfile, String pNewShipToAddressName, Address pAddress,
      ShippingGroupMapContainer pShippingGroupMapContainer, boolean pSaveShippingAddress) throws CommerceException, IntrospectionException
  {
    /*
     * We need this because of initializing method in the ShippingGroupDroplet.
     * This droplet iterates over order's commerce items and loads their shipping groups into ShippingGroupContainerService.
     * That's why here we can have good address data mapped by fake (default) name.
     * Find that good data and use as a new address.
     */
    for (Map.Entry<String, ShippingGroup> entry: ((Map<String, ShippingGroup>) pShippingGroupMapContainer.getShippingGroupMap()).entrySet())
    {
      ShippingGroup shippingGroup = entry.getValue();
      if ((shippingGroup instanceof HardgoodShippingGroup) &&
          AddressTools.compareObjects(pAddress, ((HardgoodShippingGroup) shippingGroup).getShippingAddress(), Collections.singleton("CLASS")))
      {
        if (pSaveShippingAddress)
        {
          saveAddressToAddressBook(pAddress, pNewShipToAddressName, pProfile);
        }
        return entry.getKey();
      }
    }
    addShippingAddress(pProfile, pNewShipToAddressName, pAddress, pShippingGroupMapContainer, pSaveShippingAddress);
    return null;
  }
  
  /**
   * Creates a new shipping group and adds it to the shipping group map container.
   * Optionally saves the shipping group address to the profile based on the
   * saveShippingAddress option.
   *
   * @param pAddress  Address to add
   * @param pNewShipToAddressName Address nickname to use for the address being added
   * @param pProfile shopper profile
   * @param pShippingGroupMapContainer map of all shipping groups for the profile
   * @throws CommerceException
   */
  public void addShippingAddress(RepositoryItem pProfile,
                                 String pNewShipToAddressName,
                                 Address pAddress,
                                 ShippingGroupMapContainer pShippingGroupMapContainer,
                                 boolean pSaveShippingAddress)
      throws CommerceException {

    ShippingGroupManager sgm = getShippingGroupManager();
    Map shippingGroupMap = pShippingGroupMapContainer.getShippingGroupMap();

    //create a new shipping group with the new address
    HardgoodShippingGroup hgsg = (HardgoodShippingGroup) sgm.createShippingGroup();
    hgsg.setShippingAddress(pAddress);

    String newShipToAddressName = StringUtils.isBlank(pNewShipToAddressName) ? 
        getStoreOrderTools().getProfileTools().getUniqueShippingAddressNickname(pAddress, pProfile, null) : pNewShipToAddressName;
    if (shippingGroupMap.containsKey(newShipToAddressName)) {
      throw new StorePurchaseProcessException(MSG_DUPLICATE_NICKNAME);
    }

    //put the new shipping group in the container
    pShippingGroupMapContainer.addShippingGroup(newShipToAddressName, hgsg);

    if (pSaveShippingAddress) {
      saveAddressToAddressBook(pAddress, newShipToAddressName, pProfile);
    } else {
      // if we do not save shipping address into profile,
      // we need to save address in container to avoid issues 
      // with missed nicknames.
      // the problem is that in ShippingGroupDroplet we perform cleanup and
      // reinitialize ShippingGroupMapContainer based on profile and order 
      // information. If address isn't in profile, then it takes from
      // order where nickname for address isn't exist.
      //
      // See CRS-164187 for details.
      getNonProfileShippingAddressesMap().put(newShipToAddressName, pAddress);
    }
  }

  /**
   * Edits a shipping group address in the container and saves the changes to the profile if the address is in the
   * profile's address map.
   *
   * @param pEditShippingAddressNickName Nickname for the address to be modified
   * @param pShippingGroupMapContainer map of all shipping groups for the profile
   * @param pEditAddress Address to be modified
   */
   public void modifyShippingAddress(String pEditShippingAddressNickName,
                                     Address pEditAddress,
                                     ShippingGroupMapContainer pShippingGroupMapContainer)  {

    HardgoodShippingGroup hgsg = (HardgoodShippingGroup) pShippingGroupMapContainer.getShippingGroup(pEditShippingAddressNickName);
    if (hgsg != null) {
      hgsg.setShippingAddress(pEditAddress);
    }
  }
  
  /**
   * Modifies shipping address nickname.
   * 
   * @param pProfile shopper profile
   * @param pEditShippingAddressNickName Address Nickname to be modified
   * @param pShippingAddressNewNickName New Address Nickname
   * @param pShippingGroupMapContainer map of all shipping groups for the profile
   */
  public void modifyShippingAddressNickname(RepositoryItem pProfile,
                                            String pEditShippingAddressNickName,
                                            String pShippingAddressNewNickName,
                                            ShippingGroupMapContainer pShippingGroupMapContainer){
    HardgoodShippingGroup hgsg = (HardgoodShippingGroup) pShippingGroupMapContainer.getShippingGroup(pEditShippingAddressNickName);
    //remove the shipping group from the container
    pShippingGroupMapContainer.removeShippingGroup(pEditShippingAddressNickName);
    //put the shipping group in the container under new nickname
    pShippingGroupMapContainer.addShippingGroup(pShippingAddressNewNickName, hgsg);
    
    //change address nickname in the profile's addresses map if it contains this address
    modifyAddressBookNickname(pProfile, pEditShippingAddressNickName, pShippingAddressNewNickName);
  }
  
  /**
   * Modifies the address nick name is in the profile's map.
   * 
   * @param pProfile shopper profile
   * @param pEditShippingAddressNickName Address Nickname to be modified
   * @param pShippingAddressNewNickName Address New Nickname
   */
  public void modifyAddressBookNickname(RepositoryItem pProfile,
                                        String pEditShippingAddressNickName,
                                        String pShippingAddressNewNickName) {
    StoreProfileTools profileTools = (StoreProfileTools) getOrderManager().getOrderTools().getProfileTools();
    try {
      profileTools.changeSecondaryAddressName(pProfile, pEditShippingAddressNickName, pShippingAddressNewNickName);
    } catch (RepositoryException ex) {
      if (isLoggingError())
      		logError(ex);
    }
  }

  /**
  * saveModifiedShippingAddress shipping address processing. If the address nick name is in the profile's map,
  * the updates are applied to that address too.
  *
  * @param pEditShippingAddressNickName Nickname for the address to be modified
  * @param pProfile shopper profile
  * @param pEditAddress map of all shipping groups for the profile
  */
  public void saveModifiedShippingAddressToProfile(RepositoryItem pProfile,
                                                   String pEditShippingAddressNickName,
                                                   Address pEditAddress) {

    StoreProfileTools profileTools = (StoreProfileTools) getOrderManager().getOrderTools().getProfileTools();
    String secondaryAdressProperty = ((CommercePropertyManager)profileTools.getPropertyManager()).getSecondaryAddressPropertyName();
    Map addresses = (Map) pProfile.getPropertyValue(secondaryAdressProperty);

    if (addresses.containsKey(pEditShippingAddressNickName)) {
      RepositoryItem address = profileTools.getProfileAddress(pProfile, pEditShippingAddressNickName);

      if (address != null) {
        try {
          MutableRepositoryItem mutAddress = RepositoryUtils.getMutableRepositoryItem(address);
          MutableRepository mutrep = (MutableRepository) mutAddress.getRepository();
          OrderTools.copyAddress(pEditAddress, mutAddress);
          mutrep.updateItem(mutAddress);
        }
        catch (Exception e) {
          if (isLoggingError()) {
            logError(e);
          }
        }
      }
    }
  }
  
  /**
   * Removes a shipping group from the container.
   *
   * @param pRemoveShippingAddressNickName Nickname for the address to be removed
   * @param pShippingGroupMapContainer map of all shipping groups for the profile
   * @param pRemoveAddress Address to be removed
   */
   public void removeShippingAddress(RepositoryItem pProfile,
                                     String pRemoveShippingAddressNickName,
                                     ShippingGroupMapContainer pShippingGroupMapContainer)  {

    HardgoodShippingGroup hgsg = (HardgoodShippingGroup) pShippingGroupMapContainer.getShippingGroup(pRemoveShippingAddressNickName);
    if (hgsg != null) {
      pShippingGroupMapContainer.removeShippingGroup(pRemoveShippingAddressNickName);
    }  
  }
   
   /**
    * If the shipping address nickname is in the profile's addresses map 
    * removes it from the profile.
    *
    * @param pProfile shopper profile
    * @param pRemoveShippingAddressNickName Nickname for the address to be removed
    */
    public void removeShippingAddressFromProfile(RepositoryItem pProfile,
                                                 String pRemoveShippingAddressNickName) {

      StoreProfileTools profileTools = (StoreProfileTools) getOrderManager().getOrderTools().getProfileTools();
      String secondaryAdressProperty = ((CommercePropertyManager)profileTools.getPropertyManager()).getSecondaryAddressPropertyName();
      Map addresses = (Map) pProfile.getPropertyValue(secondaryAdressProperty);

      if (addresses.containsKey(pRemoveShippingAddressNickName)) {
        try {
          profileTools.removeProfileRepositoryAddress(pProfile, pRemoveShippingAddressNickName, true);
        } catch (RepositoryException ex) {
          if (isLoggingError())
              logError(ex);
        }
      }
    }

  /**
   * <code>applyShippingGroups</code> removes all non-gift ShippingGroups from
   * the Order and then iterates over the supplied CommerceItemShippingInfos
   * for each of the CommerceItems. Each CommerceItemShippingInfo is used
   * to update the Order.
   * <p>
   * If property <code>consolidateShippingInfosBeforeApply</code> is true, the commerce
   * item shipping info objects are first consolidated by calling the <code>consolidateShippingInfos</code>
   * method.
   *
   * @param pShippingGroupMapContainer map of all shipping groups for the profile
   * @param pCommerceItemShippingInfoContainer map of all commerce item infos for the order
   * @param pOrder Order whose shipping groups are to be saved
   * @exception CommerceException if an error occurs
   */
  public void applyShippingGroups(CommerceItemShippingInfoContainer pCommerceItemShippingInfoContainer,
                                  ShippingGroupMapContainer pShippingGroupMapContainer,
                                  Order pOrder)
    throws CommerceException
  {
    getCommerceItemShippingInfoTools().applyCommerceItemShippingInfos(pCommerceItemShippingInfoContainer,pShippingGroupMapContainer,
          pOrder, isConsolidateShippingInfosBeforeApply(), isApplyDefaultShippingGroup());
  }

  /**
   * Saves all addresses in the shipping group container's shipping group map.
   * <p>
   * The key is used as the nick name. The shipping group's address is used for the
   * address.
   * <p>
   * Also sets the default shipping method on the profile
   *
   * @param pProfile shopper profile
   * @param pShippingGroupMapContainer map of all shipping groups for the profile
   */
  protected void saveShippingInfo(RepositoryItem pProfile, ShippingGroupMapContainer pShippingGroupMapContainer) {

    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();

    RepositoryItem profile = pProfile;
    ShippingGroupMapContainer sgmc = pShippingGroupMapContainer;

    Map shippinggroupmap = sgmc.getShippingGroupMap();
    Collection shippingGroupNames = sgmc.getShippingGroupNames();
    Iterator shippingGroupNamesIterator = shippingGroupNames.iterator();
    HardgoodShippingGroup hgsg = null;

    while (shippingGroupNamesIterator.hasNext()) {
      String nickName = (String) shippingGroupNamesIterator.next();
      ShippingGroup sg = (ShippingGroup) shippinggroupmap.get(nickName);

      if (!(sg instanceof HardgoodShippingGroup)) {
        continue;
      }

      hgsg = (HardgoodShippingGroup) shippinggroupmap.get(nickName);

      try {
        orderTools.saveAddressToAddressBook(profile, hgsg.getShippingAddress(), nickName);
      } catch (CommerceException ce) {
        if (isLoggingError()) {
          logError(LogUtils.formatMinor(ce.getMessage()));
        }
      }
    }

    if (hgsg != null) {
      saveDefaultShippingMethod(profile, hgsg.getShippingMethod());
    }
  }

  /**
   * Validates the shipping city against a collection of regular expressions.
   *
   * @param shippingAddress shipping address to validate
   * @throws StorePurchaseProcessException is an error occurs
   */
  public void validateShippingCity(Address shippingAddress)
    throws StorePurchaseProcessException {

    String city = shippingAddress.getCity();

    if (city != null) {
      Pattern[] invalidCityPatterns = getInvalidCityPatterns();

      for (int i = 0; i < invalidCityPatterns.length; i++) {
        Pattern invalidCityPattern = invalidCityPatterns[i];

        if (isLoggingDebug()) {
          logDebug("Validating city \"" + city + "\" against regex \"" + invalidCityPattern.pattern() + "\"");
        }

        Matcher matcher = invalidCityPattern.matcher(city);

        if (matcher.matches()) {
          if (isLoggingDebug()) {
            logDebug("City \"" + city + "\" matches regex \"" + invalidCityPattern.pattern() + "\"");
          }

          Object[] params = {  };

          if (matcher.groupCount() > 0) {
            params = new String[] { matcher.group(1) };
          }

          throw new StorePurchaseProcessException(MSG_INVALID_CITY_ADDRESS,params);
        }
      }
    }
  }

  /**
   * This method will ensure the user isn't trying to use a shipping method
   * that isn't valid for the state. For example, Express shipping to
   * Alaska is not allowed.
   *
   * @param pAddress address to validate against the shipping method
   * @param pShippingMethod shipping method to validate address for
   * @throws StorePurchaseProcessException if an error occurs
   */
  public void validateShippingMethod(Address pAddress, String pShippingMethod)
    throws StorePurchaseProcessException {

    // check the state entered in the address against the shipping method
    String shipState = pAddress.getState();

    if (isLoggingDebug()) {
      logDebug("Checking to see if state " + shipState + " is valid for method " + pShippingMethod);
    }

    Properties props = getInvalidStatesForShipMethod();

    if ((props != null) && !StringUtils.isEmpty(shipState) && (pShippingMethod != null)) {
      // a pipe-delimited list of states should be a safe place to
      // just do an index of check
      String invalidStates = props.getProperty(pShippingMethod);

      if (!StringUtils.isEmpty(invalidStates)) {
        if (invalidStates.indexOf(shipState) > -1) {
          if (isLoggingDebug()) {
            logDebug("Found invalid state " + shipState + " for method " + pShippingMethod);
          }
          Object[] params = { pShippingMethod, shipState };
          throw new StorePurchaseProcessException(MSG_INVALID_STATE_FOR_METHOD, params);
        }
      }
    }
  }

  /**
  * Check shipping restrictions.
  *
  * @param pCommerceItemShippingInfoContainer map of all commerce item infos for the order
  * @param pShippingGroupMapContainer map of all shipping groups for the profile
  * @return a list of pairs os products that are being shipped to restricted countries
  * @throws CommerceException if an error occurs
  */
  public List checkShippingRestrictions(CommerceItemShippingInfoContainer pCommerceItemShippingInfoContainer,
                                        ShippingGroupMapContainer pShippingGroupMapContainer)
    throws CommerceException {
    List commerceItemsHavingNonShippableCountry = new ArrayList();
    List commerceItemShippingInfos = pCommerceItemShippingInfoContainer.getAllCommerceItemShippingInfos();

    String countryCode = null;
    String productDisplayName = null;

    for (Iterator iterCisis = commerceItemShippingInfos.iterator(); iterCisis.hasNext();) {
      CommerceItemShippingInfo cisi = (CommerceItemShippingInfo) iterCisis.next();
      CommerceItem commerceItem = cisi.getCommerceItem();
      RepositoryItem product = (RepositoryItem) commerceItem.getAuxiliaryData().getProductRef();
      productDisplayName = (String) product.getPropertyValue("displayName");

      Set shippableCountries = (Set) product.getPropertyValue("shippableCountries");
      Set nonShippableCountries = (Set) product.getPropertyValue("nonShippableCountries");

      if (isLoggingDebug()) {
        logDebug(" List of Shippable Countries : " + shippableCountries + " for product: " + productDisplayName);
        logDebug(" List of NonShippable Countries : " + nonShippableCountries + " for product: " + productDisplayName);
      }

      ShippingGroup shippingGroup = pShippingGroupMapContainer.getShippingGroup(cisi.getShippingGroupName());

      if (shippingGroup instanceof HardgoodShippingGroup) { //only considering hard goods
        countryCode = ((HardgoodShippingGroup) shippingGroup).getShippingAddress().getCountry();

        if (isLoggingDebug()) {
          logDebug(" ShippingGroup is HardgoodShippingGroup");
          logDebug(" Country : " + countryCode);
        }

        if ((shippableCountries == null) || shippableCountries.isEmpty()) {
          if ((nonShippableCountries == null) || nonShippableCountries.isEmpty()) {
            // nothing is specified , assuming product is shippable
            if (isLoggingDebug()) {
              logDebug(" Product: " + productDisplayName + " is shippable " + " to " + countryCode);
            }
          } else if (nonShippableCountries.contains(countryCode)) {
            commerceItemsHavingNonShippableCountry.add(productDisplayName + COUNTRY_DELIM + countryCode);

            if (isLoggingDebug()) {
              logDebug(" Product: " + productDisplayName + " is not Shippable " + " to " + countryCode);
            }
          } else {
            //countryCode is in the list of Shippable country
            if (isLoggingDebug()) {
              logDebug(" Product: " + productDisplayName + " is shippable " + " to " + countryCode);
            }
          }
        } else {
          if (!shippableCountries.contains(countryCode)) {
            commerceItemsHavingNonShippableCountry.add(productDisplayName + COUNTRY_DELIM + countryCode);

            if (isLoggingDebug()) {
              logDebug(" Product: " + productDisplayName + " is not Shippable " + " to " + countryCode);
            }
          } else {
            //countryCode is in the list of Shippable country
            if (isLoggingDebug()) {
              logDebug(" Product: " + productDisplayName + " is shippable " + " to " + countryCode);
            }
          }
        }
      } //end of while
    } //end of for

    return commerceItemsHavingNonShippableCountry;
  }

  /**
   * Validates against invalid street addresses.
   *
   * @param pContactInfo contact information
   * @return invalid street address patterns
   */
  public List checkForInvalidStreetAddress(ContactInfo pContactInfo) {

    List invalidStreetStrings = new ArrayList();
    String[] invalidStreetAddresses = getInvalidStreetStrings();
    String address1 = pContactInfo.getAddress1();

    if (address1 != null) {
      for (int i = 0; i < invalidStreetAddresses.length; i++) {
        String invalidString = invalidStreetAddresses[i];

        if (isLoggingDebug()) {
          logDebug("Checking if street address contains: " + invalidString);
        }

        address1 = address1.toLowerCase();
        String invalidStringLower = invalidString.toLowerCase();
        if (address1.indexOf(invalidStringLower) != -1) {
          invalidStreetStrings.add(invalidString);
        }
      }
    }
    return invalidStreetStrings;
  }

  /**
   * Trim string.
   * @param inStr - string to trim
   * @return trimmed string
   */
  public String trimmedString(String inStr) {
    if (inStr != null) {
      return inStr.trim();
    } else {
      return inStr;
    }
  }

  /**
   * Trims spaces from address values.
   * @param pAddress - Address whose values are to trimmed
   */
  public Address trimSpacesFromAddressValues(Address pAddress)
    throws ServletException, IOException {

    Address address = pAddress;

    //Save trimmed address values to disallow space(s) as address values.
    address.setFirstName(trimmedString(address.getFirstName()));
    address.setLastName(trimmedString(address.getLastName()));
    address.setAddress1(trimmedString(address.getAddress1()));
    address.setCity(trimmedString(address.getCity()));
    address.setCountry(trimmedString(address.getCountry()));
    address.setState(trimmedString(address.getState()));
    address.setPostalCode(trimmedString(address.getPostalCode()));
    ((ContactInfo) address).setPhoneNumber(trimmedString(((ContactInfo) address).getPhoneNumber()));

    String address2 = trimmedString(address.getAddress2());
    if (address2 != null && address2.equals(""))
       address.setAddress2(null);

    return address;
  }

}
