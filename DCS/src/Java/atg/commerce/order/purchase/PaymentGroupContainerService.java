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
import atg.nucleus.GenericService;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The PaymentGroupContainerService is convenient designed for a session scoped
 * component. This implements both the PaymentGroupMapContainer and the
 * CommerceIdentifierPaymentInfoContainer interfaces. This service is
 * responsible for keeping track of both the user's authorized PaymentGroups,
 * as well as the CommerceIdentifierPaymentInfo objects for a user's Order.
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/PaymentGroupContainerService.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class PaymentGroupContainerService
  extends GenericService
  implements PaymentGroupMapContainer, CommerceIdentifierPaymentInfoContainer
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/PaymentGroupContainerService.java#2 $$Change: 651448 $";
    
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
  // property: CommerceIdentifierPaymentInfoMap
  //---------------------------------------------------------------------------
  Map mCommerceIdentifierPaymentInfoMap = new ConcurrentHashMap();

  /**
   * Set the CommerceIdentifierPaymentInfoMap property.
   * @param pCommerceIdentifierPaymentInfoMap a <code>Map</code> value
   */
  public void setCommerceIdentifierPaymentInfoMap(Map pCommerceIdentifierPaymentInfoMap) {
    mCommerceIdentifierPaymentInfoMap = pCommerceIdentifierPaymentInfoMap;
  }

  /**
   * Return the CommerceIdentifierPaymentInfoMap property.
   * @return a <code>Map</code> value
   */
  public Map getCommerceIdentifierPaymentInfoMap() {
    return mCommerceIdentifierPaymentInfoMap;
  }

  //---------------------------------------------------------------------------
  // property: PaymentGroupMap
  //---------------------------------------------------------------------------
  Map mPaymentGroupMap = new HashMap();

  /**
   * Set the PaymentGroupMap property.
   * @param pPaymentGroupMap a <code>Map</code> value
   */
  public void setPaymentGroupMap(Map pPaymentGroupMap) {
    mPaymentGroupMap = pPaymentGroupMap;
  }

  /**
   * Return the PaymentGroupMap property.
   * @return a <code>Map</code> value
   */
  public Map getPaymentGroupMap() {
    return mPaymentGroupMap;
  }

  //---------------------------------------------------------------------------
  // property: DefaultPaymentGroupName
  //---------------------------------------------------------------------------
  String mDefaultPaymentGroupName = null;

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>PaymentGroupContainerService</code> instance.
   *
   */
  public PaymentGroupContainerService () {}

  //--------------------------------------------------
  // CommerceIdentifierPaymentInfoContainer Implementation
  //--------------------------------------------------

  /**
   * The <code>getCommerceIdentifierPaymentInfos</code> method returns the List of
   * CommerceIdentifierPaymentInfos corresponding to the supplied CommerceIdentifierId.
   *
   * @param pCommerceIdentifierId a <code>String</code> value
   * @return a <code>List</code> value
   */
  public List getCommerceIdentifierPaymentInfos (String pCommerceIdentifierId) {
    List cipiList = (List) getCommerceIdentifierPaymentInfoMap().get(pCommerceIdentifierId);
    if (cipiList == null) {
      cipiList = new ArrayList();
      getCommerceIdentifierPaymentInfoMap().put(pCommerceIdentifierId, cipiList);
    }
    return cipiList;
  }

  /**
   * The <code>getAllCommerceIdentifierPaymentInfos</code> method returns one List of all
   * CommerceIdentifierPaymentInfos in the entire Map.
   *
   * @return a <code>List</code> value
   */
  public List getAllCommerceIdentifierPaymentInfos () {
    List commerceIdentifierPaymentInfos = new ArrayList();
    Collection values = getCommerceIdentifierPaymentInfoMap().values();
    Iterator iter = values.iterator();
    while (iter.hasNext()) {
      Collection collection = (Collection) iter.next();
      commerceIdentifierPaymentInfos.addAll(collection);
    }
    return commerceIdentifierPaymentInfos;
  }

  /**
   * The <code>addCommerceIdentifierPaymentInfo</code> method adds the supplied
   * CommerceIdentifierId and CommerceIdentifierPaymentInfo to the Map.
   *
   * @param pCommerceIdentifierId a <code>String</code> value
   * @param pCommerceIdentifierPaymentInfo a <code>CommerceIdentifierPaymentInfo</code> value
   */
  public void addCommerceIdentifierPaymentInfo (String pCommerceIdentifierId,
                                                CommerceIdentifierPaymentInfo pCommerceIdentifierPaymentInfo) {
    getCommerceIdentifierPaymentInfos(pCommerceIdentifierId).add(pCommerceIdentifierPaymentInfo);
    if (isLoggingDebug()) {
      logDebug("PaymentGroupContainerService addCommerceIdentifierPaymentInfo " + pCommerceIdentifierId + " cipi " + pCommerceIdentifierPaymentInfo);
    }
  }

  /**
   * The <code>removeAllCommerceIdentifierPaymentInfos</code> method clears the Map.
   *
   */
  public void removeAllCommerceIdentifierPaymentInfos () {
    getCommerceIdentifierPaymentInfoMap().clear();
  }

  /**
   * The <code>removeCommerceIdentifierPaymentInfos</code> method removes the
   * CommerceIdentifierPaymentInfo corresponding to the supplied CommerceIdentifierId.
   *
   * @param pCommerceIdentifierId a <code>String</code> value
   */
  public void removeCommerceIdentifierPaymentInfos (String pCommerceIdentifierId) {
    getCommerceIdentifierPaymentInfoMap().put(pCommerceIdentifierId, new ArrayList());
  }

  //--------------------------------------------------
  // PaymentGroupMapContainer Implementation
  //--------------------------------------------------

  /**
   * The <code>getPaymentGroup</code> method returns the PaymentGroup corresponding
   * to the supplied payment name.
   *
   * @param pPaymentGroupName a <code>String</code> value
   * @return a <code>PaymentGroup</code> value
   */
  public PaymentGroup getPaymentGroup(String pPaymentGroupName) {
    return (PaymentGroup) getPaymentGroupMap().get(pPaymentGroupName);
  }
  
  /**
   * The <code>addPaymentGroup</code> method adds the supplied payment name
   * and PaymentGroup to the Map.
   *
   * @param pPaymentGroupName a <code>String</code> value
   * @param pPaymentGroup a <code>PaymentGroup</code> value
   */
  public void addPaymentGroup(String pPaymentGroupName, PaymentGroup pPaymentGroup) {
    getPaymentGroupMap().put(pPaymentGroupName, pPaymentGroup);
    if (isLoggingDebug()) {
      logDebug("PaymentGroupContainerService addPaymentGroup " + pPaymentGroupName + " paymentgroup " + pPaymentGroup);
    }
  }
  
  /**
   * The <code>removeAllPaymentGroups</code> method clears the Map.
   *
   */
  public void removeAllPaymentGroups() {
    getPaymentGroupMap().clear();
  }
  
  /**
   * The <code>removePaymentGroup</code> method removes the PaymentGroup corresponding
   * to the supplied payment name.
   *
   * @param pPaymentGroupName a <code>String</code> value
   */
  public void removePaymentGroup(String pPaymentGroupName) {
    getPaymentGroupMap().remove(pPaymentGroupName);
  }

  /**
   * Set the DefaultPaymentGroupName property. Part of PaymentGroupMapContainer implementation.
   * @param pDefaultPaymentGroupName a <code>String</code> value
   */
  public void setDefaultPaymentGroupName(String pDefaultPaymentGroupName) {
    if (isLoggingDebug()) {
      logDebug("PaymentGroupContainerService setDefaultPaymentGroupName " + pDefaultPaymentGroupName);
    }
    mDefaultPaymentGroupName = pDefaultPaymentGroupName;
  }

  /**
   * Return the DefaultPaymentGroupName property. Part of PaymentGroupMapContainer implementation.
   * @return a <code>String</code> value
   */
  public String getDefaultPaymentGroupName() {
    return mDefaultPaymentGroupName;
  }
}   // end of class
