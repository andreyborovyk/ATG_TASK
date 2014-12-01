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
import java.io.Serializable;
import java.util.Map;

/**
 * The <code>CostCenterMapContainer</code> interface is used to represent a container
 * of a Map of payment names [key] to CostCenters [value].
 *
 * @author Paul O'Brien
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/purchase/CostCenterMapContainer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public interface CostCenterMapContainer extends Serializable
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/purchase/CostCenterMapContainer.java#2 $$Change: 651448 $";
    
  /**
   * The <code>getCostCenterMap</code> method returns the Map of named CostCenters.
   *
   * @return a <code>Map</code> value
   */
  public Map getCostCenterMap();

  /**
   * The <code>getCostCenter</code> method returns the CostCenter corresponding to the
   * supplied name.
   *
   * @param pCostCenterName a <code>String</code> value
   * @return a <code>CostCenter</code> value
   */
  public CostCenter getCostCenter(String pCostCenterName);
  
  /**
   * The <code>getDefaultCostCenterName</code> method retrieves the name of the
   * default CostCenter.
   *
   * @return a <code>String</code> value
   */
  public String getDefaultCostCenterName();

  /**
   * The <code>addCostCenter</code> method adds the supplied name and CostCenter
   * to the Map.
   *
   * @param pCostCenterName a <code>String</code> value
   * @param pCostCenter a <code>CostCenter</code> value
   */
  public void addCostCenter(String pCostCenterName, CostCenter pCostCenter);

  /**
   * The <code>removeAllCostCenters</code> method clears the Map.
   *
   */
  public void removeAllCostCenters();

  /**
   * The <code>setDefaultCostCenterName</code> method marks the name of the
   * default CostCenter.
   *
   * @param pDefaultCostCenterName a <code>String</code> value
   */
  public void setDefaultCostCenterName(String pDefaultCostCenterName);
  
  /**
   * The <code>removeCostCenter</code> method removes the CostCenter corresponding to
   * the supplied name.
   *
   * @param pCostCenterName a <code>String</code> value
   */
  public void removeCostCenter(String pCostCenterName);
  
}   // end of class
