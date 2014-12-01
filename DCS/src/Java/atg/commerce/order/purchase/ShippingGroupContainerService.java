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
import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.servlet.sessionsaver.Restoreable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The ShippingGroupContainerService is a convenient session scoped component
 * which implements both the ShippingGroupMapContainer and the
 * CommerceItemShippingInfoContainer interfaces. This component is
 * responsible for keeping track of both the user's authorized ShippingGroups,
 * as well as the CommerceItemShippingInfo objects for a user's Order.
 * 
 * Restoreable is implemented to clear up shipping group references after an session fail over
 * or session migration. In certain situations, shipping groups could be loaded from the order and order and this container may
 * have a reference to the same shipping group instance. On session fail over, if we do not clear the references, the out-dated references
 * will obstruct the check out process.
 * 
 *
 * @author Charles Chen
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/ShippingGroupContainerService.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ShippingGroupContainerService
  extends GenericService
  implements ShippingGroupMapContainer, CommerceItemShippingInfoContainer, Restoreable
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/ShippingGroupContainerService.java#2 $$Change: 651448 $";

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  protected String mShoppingCartPath;

  /**
   * This property is used on session restore and to look up the ShoppingCart on session fail over.
   * Returns the shoppingCartPath.
   * @return shoppingCartPath
   */
  public String getShoppingCartPath() {
    return mShoppingCartPath;
  }

  /**
   * This property is used on session restore and to look up the ShoppingCart on session fail over.
   * Sets the shoppingCartPath.
   * @param pShoppingCartPath
   */
  public void setShoppingCartPath(String pShoppingCartPath) {
    mShoppingCartPath = pShoppingCartPath;
  }

  //---------------------------------------------------------------------------
  // property: CommerceItemShippingInfoMap
  //---------------------------------------------------------------------------
  Map mCommerceItemShippingInfoMap = new ConcurrentHashMap();

  /**
   * Set the CommerceItemShippingInfoMap property.
   * @param pCommerceItemShippingInfoMap a <code>Map</code> value
   */
  public void setCommerceItemShippingInfoMap(Map pCommerceItemShippingInfoMap) {
    mCommerceItemShippingInfoMap = pCommerceItemShippingInfoMap;
  }

  /**
   * Return the CommerceItemShippingInfoMap property.
   * @return a <code>Map</code> value
   */
  public Map getCommerceItemShippingInfoMap() {
    return mCommerceItemShippingInfoMap;
  }

  //---------------------------------------------------------------------------
  // property: ShippingGroupMap
  //---------------------------------------------------------------------------
  Map mShippingGroupMap = new HashMap();

  /**
   * Set the ShippingGroupMap property.
   * @param pShippingGroupMap a <code>Map</code> value
   */
  public void setShippingGroupMap(Map pShippingGroupMap) {
    mShippingGroupMap = pShippingGroupMap;
  }

  /**
   * Return the ShippingGroupMap property.
   * @return a <code>Map</code> value
   */
  public Map getShippingGroupMap() {
    return mShippingGroupMap;
  }

  //---------------------------------------------------------------------------
  // property: DefaultShippingGroupName
  //---------------------------------------------------------------------------
  String mDefaultShippingGroupName = null;

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>ShippingGroupContainerService</code> instance.
   *
   */
  public ShippingGroupContainerService () {}

  //--------------------------------------------------
  // CommerceItemShippingInfoContainer Implementation
  //--------------------------------------------------

  /**
   * Get the List of CommerceItemShippingInfos based on the CommerceItemId
   * as the key to the CommerceItemShippingInfoMap
   *
   * @param pCommerceItemId a <code>String</code> value
   * @return a <code>List</code> value
   */
  public List getCommerceItemShippingInfos (String pCommerceItemId) {
    List cisiList = (List) getCommerceItemShippingInfoMap().get(pCommerceItemId);
    if (cisiList == null) {
      cisiList = new ArrayList();
      getCommerceItemShippingInfoMap().put(pCommerceItemId, cisiList);
    }
    return cisiList;
  }

  /**
   * Get the List of all the CommerceItemShippingInfos from the CommerceItemShippingInfoMap
   *
   * @return a <code>List</code> value
   */
  public List getAllCommerceItemShippingInfos () {
    List commerceItemShippingInfos = new ArrayList();
    Map infoMap = getCommerceItemShippingInfoMap();
    Set keys = infoMap.keySet();
    List keyList = new ArrayList(keys.size());
    keyList.addAll(keys);

    //sort the keyList so we can build the list consistently in the same order between changes to
    //the map
    Collections.sort(keyList);
    Iterator keyiter = keyList.iterator();
    while (keyiter.hasNext())
      commerceItemShippingInfos.addAll((Collection)infoMap.get(keyiter.next()));

    return commerceItemShippingInfos;
  }

  /**
   * Add a CommerceItemInfo to the CommerceItemShippingInfoMap. The key to this
   * Map will be the CommerceItemId.
   *
   * @param pCommerceItemId a <code>String</code> value
   * @param pCommerceItemShippingInfo a <code>CommerceItemShippingInfo</code> value
   */
  public void addCommerceItemShippingInfo (String pCommerceItemId,
                                                CommerceItemShippingInfo pCommerceItemShippingInfo) {
    getCommerceItemShippingInfos(pCommerceItemId).add(pCommerceItemShippingInfo);
    if (isLoggingDebug()) {
      logDebug("ShippingGroupContainerService addCommerceItemShippingInfo " + pCommerceItemId + " cisi " + pCommerceItemShippingInfo);
    }
  }

  /**
   * Remove all the CommerceItemInfos from the CommerceItemShippingInfoMap.
   *
   */
  public void removeAllCommerceItemShippingInfos () {
    if (isLoggingDebug()) {
      logDebug("ShippingGroupContainerService removeAllCommerceItemShippingInfos");
    }
    getCommerceItemShippingInfoMap().clear();
  }

  /**
   * Remove all the CommerceItemInfos whose key is the input CommerceItemId from
   * the CommerceItemShippingInfoMap.
   *
   * @param pCommerceItemId a <code>String</code> value
   */
  public void removeCommerceItemShippingInfos (String pCommerceItemId) {
    getCommerceItemShippingInfoMap().put(pCommerceItemId, new ArrayList());
  }

  //--------------------------------------------------
  // ShippingGroupMapContainer Implementation
  //--------------------------------------------------

  /**
   * Get a ShippingGroup based on its ShippingGroupName
   *
   * @param pShippingGroupName a <code>String</code> value
   * @return a <code>ShippingGroup</code> value
   */
  public ShippingGroup getShippingGroup(String pShippingGroupName) {
    return (ShippingGroup) getShippingGroupMap().get(pShippingGroupName);
  }

  /**
   * Add a ShippingGroup to the ShippingGroupMap, the key will be its ShippingGroupName
   *
   * @param pShippingGroupName a <code>String</code> value
   * @param pShippingGroup a <code>ShippingGroup</code> value
   */
  public void addShippingGroup(String pShippingGroupName, ShippingGroup pShippingGroup) {
    getShippingGroupMap().put(pShippingGroupName, pShippingGroup);
    if (isLoggingDebug()) {
      logDebug("ShippingGroupContainerService addShippingGroup " + pShippingGroupName + " shippinggroup " + pShippingGroup);
    }
  }

  /**
   * Remove all the user's ShippingGroups from the ShippingGroupMap
   *
   */
  public void removeAllShippingGroups() {
    getShippingGroupMap().clear();
  }

  /**
   * Remove a ShippingGroup from the ShippingGroupMap based on its ShippingGroupName
   *
   * @param pShippingGroupName a <code>String</code> value
   */
  public void removeShippingGroup(String pShippingGroupName) {
    getShippingGroupMap().remove(pShippingGroupName);
  }

  /**
   * Set a default ShippingGroupName for the user's default ShippingGroup
   *
   * @param pDefaultShippingGroupName a <code>String</code> value
   */
  public void setDefaultShippingGroupName(String pDefaultShippingGroupName) {
    if (isLoggingDebug()) {
      logDebug("ShippingGroupContainerService setDefaultShippingGroupName " + pDefaultShippingGroupName);
    }
    mDefaultShippingGroupName = pDefaultShippingGroupName;
  }

  /**
   * Get the default ShippingGroupName for the user's default ShippingGroup
   *
   * @return a <code>String</code> value
   */
  public String getDefaultShippingGroupName() {
    return mDefaultShippingGroupName;
  }

  /**
   * Get all the user's ShippingGroupNames
   *
   * @return a <code>Set</code> value
   */
  public Set getShippingGroupNames() {
    return getShippingGroupMap().keySet();

  }

  /**
   * Returns the name that the shipping group is mapped to in the map.
   */
  public String getShippingGroupName(ShippingGroup pShippingGroup, Collection pShippingGroupMatchers) {
    Collection matchers = pShippingGroupMatchers;
    Iterator matcherIter = matchers.iterator();
    String shippingGroupName = null;
    while(shippingGroupName == null && matcherIter.hasNext()) {
      ShippingGroupMatcher matcher = (ShippingGroupMatcher) matcherIter.next();
      shippingGroupName = matcher.matchShippingGroup(pShippingGroup, this);
    }

    return shippingGroupName;
  }

  /**
   * Returns a new name for a shipping group
   * @param pShippingGroup
   * @param pShippingGroupMatchers
   * @return
   */
  public String getNewShippingGroupName(ShippingGroup pShippingGroup,
      Collection pShippingGroupMatchers) {
    Collection matchers = pShippingGroupMatchers;
    Iterator matcherIter = matchers.iterator();
    String shippingGroupName = null;
    while(shippingGroupName == null && matcherIter.hasNext()) {
      ShippingGroupMatcher matcher = (ShippingGroupMatcher) matcherIter.next();
      shippingGroupName = matcher.getNewShippingGroupName(pShippingGroup);
    }

    return shippingGroupName;
  }

  /**
   * Adds a shipping group to the map container.
   * @param pShippingGroup
   * @return name used as the key in the map. If null then the shipping group is not added to the map.
   */
  public String addShippingGroupToMap(ShippingGroup pShippingGroup, Collection pShippingGroupMatchers)
  {
    if(pShippingGroup == null)
      return null;

    String shippingGroupName = getShippingGroupName(pShippingGroup,pShippingGroupMatchers);
    if (StringUtils.isEmpty(shippingGroupName)) {
      shippingGroupName = getNewShippingGroupName(pShippingGroup,pShippingGroupMatchers);
    }

    //if we couldn't come up with a shipping group name then the shipping group is
    //probably bogus. Don't add it to the map.
    if(!StringUtils.isEmpty(shippingGroupName)) {
      addShippingGroup(shippingGroupName,pShippingGroup);
      return shippingGroupName;
    }
    else
      return null;
  }
  
  //-------------------------------------
  /**
   *
   * This method is called after an object has been recovered from a
   * session's backup server, either as a result of session fail over
   * or session migration. If this component could be used in the window or
   * session scope. Once the component is recovered from the session fail over,
   * if there is any shipping groups are loaded from the current order, replace those shipping groups
   * from the order. 
   * 
   * */
  public void sessionRestored () {
    
    //Once the component is recovered from the session fail over,
    //in order to avoid BUG #154809, the shipping groups are replaced from the current order. 
    
    //On a session fail over, the order and this component has different instance of the shipping group.
    //The code below finds the matching shipping groups from the container and replaces those from the current order.
   
    DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
    if (req != null) {
      OrderHolder orderHolder = (OrderHolder) req.resolveName(getShoppingCartPath());
      if (orderHolder != null) {
        Order order = orderHolder.getCurrent();
        if (order != null && order.getShippingGroups() != null) {
          for ( Object entry: order.getShippingGroups()) {
            ShippingGroup osg = (ShippingGroup)entry;
            if (osg != null) {
              if (getShippingGroupMap() != null) {
                for (Object mapEntry: getShippingGroupMap().entrySet()) {
                  Map.Entry sgMapEntry = (Map.Entry)mapEntry;
                  if (sgMapEntry != null 
                      && sgMapEntry.getValue() != null)  {
                    ShippingGroup msg = (ShippingGroup)sgMapEntry.getValue();
                    if (msg != null && msg.getId().equals(osg.getId())) {
                      getShippingGroupMap().put (sgMapEntry.getKey(), osg);
                      break;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

}   // end of class
