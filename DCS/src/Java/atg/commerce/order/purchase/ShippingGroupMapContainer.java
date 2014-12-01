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

import java.util.*;
import java.io.Serializable;

/**
 * The <code>ShippingGroupMapContainer</code> interface is used to represent a container
 * of a Map of ShippingGroup names [key] to ShippingGroups [value].
 *
 * @author Charles Chen
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/ShippingGroupMapContainer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public interface ShippingGroupMapContainer extends Serializable {

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/ShippingGroupMapContainer.java#2 $$Change: 651448 $";

  /**
   * Get a ShippingGroupMap which manages the user's ShippingGroup
   *
   * @return a <code>Map</code> value
   */
  public Map getShippingGroupMap();


  /**
   * Get a ShippingGroup based on its ShippingGroupName
   *
   * @param pShippingGroupName a <code>String</code> value
   * @return a <code>ShippingGroup</code> value
   */
  public ShippingGroup getShippingGroup(String pShippingGroupName);

  /**
   * Add a ShippingGroup to the ShippingGroupMap, the key will be its ShippingGroupName
   *
   * @param pShippingGroupName a <code>String</code> value
   * @param pShippingGroup a <code>ShippingGroup</code> value
   */
  public void addShippingGroup(String pShippingGroupName, ShippingGroup pShippingGroup);

  /**
   * Remove all the user's ShippingGroups from the ShippingGroupMap
   *
   */
  public void removeAllShippingGroups();

  /**
   * Remove a ShippingGroup from the ShippingGroupMap based on its ShippingGroupName
   *
   * @param pShippingGroupName a <code>String</code> value
   */
  public void removeShippingGroup(String pShippingGroupName);

  /**
   * Set a default ShippingGroupName for the user's default ShippingGroup
   *
   * @param pDefaultShippingGroupName a <code>String</code> value
   */
  public void setDefaultShippingGroupName(String pDefaultShippingGroupName);

  /**
   * Get the default ShippingGroupName for the user's default ShippingGroup
   *
   * @return a <code>String</code> value
   */
  public String getDefaultShippingGroupName();

  /**
   * Get all the user's ShippingGroupNames
   *
   * @return a <code>Set</code> value
   */
  public Set getShippingGroupNames();

  /**
   * Returns the name that the shipping group is mapped to in the map.
   */
  public String getShippingGroupName(ShippingGroup pShippingGroup, Collection pShippingGroupMatchers);

  /**
   * Returns a new name for a shipping group
   * @param pShippingGroup
   * @param pShippingGroupMatchers
   * @return
   */
  public String getNewShippingGroupName(ShippingGroup pShippingGroup, Collection pShippingGroupMatchers);


  /**
   * Adds a shipping group to the map container.
   * @param pShippingGroup
   * @return name used as the key in the map. If null then the shipping group is not added to the map.
   */
  public String addShippingGroupToMap(ShippingGroup pShippingGroup, Collection pShippingGroupMatchers);

} //ShippingGroupMapContainer.java

