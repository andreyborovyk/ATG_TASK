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

package atg.b2bcommerce.order.purchase;

import atg.b2bcommerce.order.*;
import atg.commerce.order.*;
import atg.nucleus.GenericService;
import java.util.*;

/**
 * The CostCenterContainerService is a convenient session scoped component
 * which implements both the CostCenterMapContainer and the
 * CommerceIdentifierCostCenterContainer interfaces. This service is
 * responsible for keeping track of both the user's authorized CostCenters,
 * as well as the CommerceIdentifierCostCenter objects for a user's Order.
 *
 * @author Paul O'Brien
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/purchase/CostCenterContainerService.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class CostCenterContainerService
  extends GenericService
  implements CostCenterMapContainer, CommerceIdentifierCostCenterContainer, CostCenterConstants
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/purchase/CostCenterContainerService.java#2 $$Change: 651448 $";
    
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
  // property: CommerceIdentifierCostCenterMap
  //---------------------------------------------------------------------------
  Map mCommerceIdentifierCostCenterMap = new HashMap();

  /**
   * Set the CommerceIdentifierCostCenterMap property.
   * @param pCommerceIdentifierCostCenterMap a <code>Map</code> value
   */
  public void setCommerceIdentifierCostCenterMap(Map pCommerceIdentifierCostCenterMap) {
    mCommerceIdentifierCostCenterMap = pCommerceIdentifierCostCenterMap;
  }

  /**
   * Return the CommerceIdentifierCostCenterMap property.
   * @return a <code>Map</code> value
   */
  public Map getCommerceIdentifierCostCenterMap() {
    return mCommerceIdentifierCostCenterMap;
  }

  //---------------------------------------------------------------------------
  // property: CostCenterMap
  //---------------------------------------------------------------------------
  Map mCostCenterMap = new HashMap();

  /**
   * Set the CostCenterMap property.
   * @param pCostCenterMap a <code>Map</code> value
   */
  public void setCostCenterMap(Map pCostCenterMap) {
    mCostCenterMap = pCostCenterMap;
  }

  /**
   * Return the CostCenterMap property.
   * @return a <code>Map</code> value
   */
  public Map getCostCenterMap() {
    return mCostCenterMap;
  }

  //---------------------------------------------------------------------------
  // property: DefaultCostCenterName
  //---------------------------------------------------------------------------
  String mDefaultCostCenterName = null;

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>CostCenterContainerService</code> instance.
   *
   */
  public CostCenterContainerService () {}

  //--------------------------------------------------
  // CommerceIdentifierCostCenterContainer Implementation
  //--------------------------------------------------

  /**
   * The <code>getCommerceIdentifierCostCenters</code> method returns the List of
   * CommerceIdentifierCostCenters corresponding to the supplied CommerceIdentifierId.
   *
   * @param pCommerceIdentifierId a <code>String</code> value
   * @return a <code>List</code> value
   */
  public List getCommerceIdentifierCostCenters (String pCommerceIdentifierId) {
    List ciccList = (List) getCommerceIdentifierCostCenterMap().get(pCommerceIdentifierId);
    if (ciccList == null) {
      ciccList = new ArrayList();
      getCommerceIdentifierCostCenterMap().put(pCommerceIdentifierId, ciccList);
    }
    return ciccList;
  }

  /**
   * The <code>getAllCommerceIdentifierCostCenters</code> method returns all the
   * CommerceIdentifierCostCenters in this container.
   *
   * @return a <code>List</code> value
   */
  public List getAllCommerceIdentifierCostCenters () {
    List commerceIdentifierCostCenters = new ArrayList();
    Collection values = getCommerceIdentifierCostCenterMap().values();
    Iterator iter = values.iterator();
    while (iter.hasNext()) {
      Collection collection = (Collection) iter.next();
      commerceIdentifierCostCenters.addAll(collection);
    }
    return commerceIdentifierCostCenters;
  }

  /**
   * The <code>addCommerceIdentifierCostCenter</code> method adds the supplied
   * CommerceIdentifierCostCenter by the supplied name.
   *
   * @param pCommerceIdentifierId a <code>String</code> value
   * @param pCommerceIdentifierCostCenter a <code>CommerceIdentifierCostCenter</code> value
   */
  public void addCommerceIdentifierCostCenter (String pCommerceIdentifierId,
					       CommerceIdentifierCostCenter pCommerceIdentifierCostCenter) {
    getCommerceIdentifierCostCenters(pCommerceIdentifierId).add(pCommerceIdentifierCostCenter);
    if (isLoggingDebug()) {
      logDebug("CostCenterDroplet addCommerceIdentifierCostCenter " + pCommerceIdentifierId + " cicc " + pCommerceIdentifierCostCenter);
    }
  }

  /**
   * The <code>removeAllCommerceIdentifierCostCenters</code> method clears the
   * CommerceIdentifierCostCenterMap.
   *
   */
  public void removeAllCommerceIdentifierCostCenters () {
    getCommerceIdentifierCostCenterMap().clear();
  }

  /**
   * The <code>removeCommerceIdentifierCostCenters</code> method removes the
   * CommerceIdentifierCostCenters corresponding to the supplied CommerceIdentifierId.
   *
   * @param pCommerceIdentifierId a <code>String</code> value
   */
  public void removeCommerceIdentifierCostCenters (String pCommerceIdentifierId) {
    getCommerceIdentifierCostCenterMap().put(pCommerceIdentifierId, new ArrayList());
  }

  //--------------------------------------------------
  // CostCenterMapContainer Implementation
  //--------------------------------------------------

  /**
   * The <code>getCostCenter</code> method returns the CostCenter with the
   * supplied name.
   *
   * @param pCostCenterName a <code>String</code> value
   * @return a <code>CostCenter</code> value
   */
  public CostCenter getCostCenter(String pCostCenterName) {
    return (CostCenter) getCostCenterMap().get(pCostCenterName);
  }
  
  /**
   * The <code>addCostCenter</code> method adds the given CostCenter to the Map by
   * the supplied name.
   *
   * @param pCostCenterName a <code>String</code> value
   * @param pCostCenter a <code>CostCenter</code> value
   */
  public void addCostCenter(String pCostCenterName, CostCenter pCostCenter) {
    getCostCenterMap().put(pCostCenterName, pCostCenter);
    if (isLoggingDebug()) {
      logDebug("CostCenterDroplet addCostCenter " + pCostCenterName + " costcenter " + pCostCenter);
    }
  }
  
  /**
   * The <code>removeAllCostCenters</code> method removes all CostCenters in this container.
   *
   */
  public void removeAllCostCenters() {
    getCostCenterMap().clear();
  }
  
  /**
   * The <code>removeCostCenter</code> method removes the CostCenter corresponding to
   * the supplied name.
   *
   * @param pCostCenterName a <code>String</code> value
   */
  public void removeCostCenter(String pCostCenterName) {
    getCostCenterMap().remove(pCostCenterName);
  }

  /**
   * Set the DefaultCostCenterName property. Part of CostCenterMapContainer implementation.
   * @param pDefaultCostCenterName a <code>String</code> value
   */
  public void setDefaultCostCenterName(String pDefaultCostCenterName) {
    mDefaultCostCenterName = pDefaultCostCenterName;
  }

  /**
   * Return the DefaultCostCenterName property. Part of CostCenterMapContainer implementation.
   * @return a <code>String</code> value
   */
  public String getDefaultCostCenterName() {
    return mDefaultCostCenterName;
  }
}   // end of class
