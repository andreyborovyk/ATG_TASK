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
package atg.projects.store.order.purchase;

import java.util.Map;
import java.util.Set;

import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.HardgoodShippingGroupInitializer;
import atg.commerce.order.purchase.ShippingGroupMapContainer;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.Address;
import atg.projects.store.gifts.StoreGiftlistManager;
import atg.projects.store.profile.StoreAddressTools;
import atg.servlet.ServletUtil;

/**
 * This class performs specific to CRS actions with {@link HardgoodShippingGroup} 
 * in addition to those which are provided in parent class. 
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/purchase/StoreHardgoodShippingGroupInitializer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class StoreHardgoodShippingGroupInitializer extends HardgoodShippingGroupInitializer {

  //-------------------------------------
  // Class version string
  //-------------------------------------
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/purchase/StoreHardgoodShippingGroupInitializer.java#2 $$Change: 651448 $";

  private static final String MY_RESOURCE_NAME = "atg.commerce.gifts.GiftlistResources";

  //-------------------------------------
  // property: ShippingProcessHelper
  //-------------------------------------
  private StoreShippingProcessHelper mShippingProcessHelper;
  
  public StoreShippingProcessHelper getShippingProcessHelper() {
    return mShippingProcessHelper;
  }

  public void setShippingProcessHelper(
      StoreShippingProcessHelper pShippingProcessHelper) {
    mShippingProcessHelper = pShippingProcessHelper;
  }

  // ----------------------------
  /**
   * This method contains some specific logic related to giftlist ShippingGroups.
   * In case if the shipping group description contains {@link StoreGiftlistManager#getGiftShippingGroupDescriptionPrefix()}
   * we extract the giftlist event name, which is located after the prefix, and use it in creation of shippingGroupName.
   * 
   * @param pShippingGroup ShippingGroup for which a name will be created.
   * @return name of the ShippingGroup
   * */
  public String getNewShippingGroupName(ShippingGroup pShippingGroup) {
    String newGiftShippingGroupName = null;

    if (!(pShippingGroup instanceof HardgoodShippingGroup)) {
      return null;
    } else {
      String description = pShippingGroup.getDescription();
      StoreGiftlistManager giftlistManager = (StoreGiftlistManager) getConfiguration().getGiftlistManager();

      if (description != null && description.startsWith(giftlistManager.getGiftShippingGroupDescriptionPrefix())) {
        // Create shippingGroupName in the following way:
        // 1.use the localized value for "Gift"
        String giftShippingGroupNamePrefix = 
          LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, ServletUtil.getUserLocale()).getString("giftShippingGroupNamePrefix");
        // 2.use Giftlist Event Name
        String eventName = description.replace(giftlistManager.getGiftShippingGroupDescriptionPrefix(), "");

        newGiftShippingGroupName = giftShippingGroupNamePrefix + " " + eventName;
      }
    }

    return getShippingGroupManager().getOrderTools().getProfileTools().getUniqueAddressNickname(
        ((HardgoodShippingGroup) pShippingGroup).getShippingAddress(),
        getShippingGroupMapContainer().getShippingGroupNames(), newGiftShippingGroupName);
  }

  /**
   * Check address in the additional map of addresses that haven't stored in profile.
   */
  public String matchShippingGroup(ShippingGroup pShippingGroup,
      ShippingGroupMapContainer pShippingGroupMapContainer) {
    String shippingGroupName = super.matchShippingGroup(pShippingGroup, pShippingGroupMapContainer);
    
    // if address for shipping group not found in ShippingGroupMapContainer,
    // look for map of addresses not stored in profile
    if(shippingGroupName == null) {
      if (!(pShippingGroup instanceof HardgoodShippingGroup))
        return null;
      
      Map<String, Address> addressesMap = getShippingProcessHelper().getNonProfileShippingAddressesMap();

      Address address = ((HardgoodShippingGroup) pShippingGroup).getShippingAddress();
      Set<String> names = addressesMap.keySet();
      for(String name : names) {
        if(StoreAddressTools.compare(address, addressesMap.get(name))) {
          shippingGroupName = name;
          break;
        }
      }
    }
    
    return shippingGroupName;
  }
  
  //-------------------------------------
  // return true if each field in the addresses are the same
  // or if both addresses are null
  protected boolean compareAddresses(Address pAddressA, Address pAddressB)
  {    
    return StoreAddressTools.compare(pAddressA, pAddressB);
  }
}
