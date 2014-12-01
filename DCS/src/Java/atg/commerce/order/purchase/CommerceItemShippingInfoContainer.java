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
 * The <code>CommerceItemShippingInfoContainer</code> interface is used to represent
 * a container of a Map of CommerceItems [key] to CommerceItemShippingInfo Lists [value].
 *
 * @author Charles Chen
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CommerceItemShippingInfoContainer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public interface CommerceItemShippingInfoContainer extends Serializable {

  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CommerceItemShippingInfoContainer.java#2 $$Change: 651448 $";

  /**
   * Get the CommerceItemShippingInfoMap which holds the Map of CommerceItems [key] to 
   * CommerceItemShippingInfo Lists [value]
   *
   * @return a <code>Map</code> value
   */
  public Map getCommerceItemShippingInfoMap();

  /**
   * Get the List of CommerceItemShippingInfos based on the CommerceItemId
   * as the key to the CommerceItemShippingInfoMap
   *
   * @param pCommerceItemId a <code>String</code> value
   * @return a <code>List</code> value
   */
  public List getCommerceItemShippingInfos(String pCommerceItemId);

  /**
   * Get the List of all the CommerceItemShippingInfos from the CommerceItemShippingInfoMap
   *
   * @return a <code>List</code> value
   */
  public List getAllCommerceItemShippingInfos();

  /**
   * Add a CommerceItemInfo to the CommerceItemShippingInfoMap. The key to this 
   * Map will be the CommerceItemId. 
   *
   * @param pCommerceItemId a <code>String</code> value
   * @param pCommerceItemShippingInfo a <code>CommerceItemShippingInfo</code> value
   */
  public void addCommerceItemShippingInfo(String pCommerceItemId, 
                         CommerceItemShippingInfo pCommerceItemShippingInfo);

  /**
   * Remove all the CommerceItemInfos whose key is the input CommerceItemId from
   * the CommerceItemShippingInfoMap. 
   *
   * @param pCommerceItemId a <code>String</code> value
   */  
  public void removeCommerceItemShippingInfos(String pCommerceItemId);

  /**
   * Remove all the CommerceItemInfos from the CommerceItemShippingInfoMap. 
   *
   */
  public void removeAllCommerceItemShippingInfos();

} //CommerceItemShippingInfoContainer.java
