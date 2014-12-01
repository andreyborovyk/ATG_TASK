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
import java.util.List;

/**
 * The <code>CommerceIdentifierCostCenterContainer</code> interface is used to represent
 * a container of a Map of CommerceIdentifiers [key] to CommerceIdentifierCostCenter Lists [value].
 *
 * @author Paul O'Brien
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/purchase/CommerceIdentifierCostCenterContainer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public interface CommerceIdentifierCostCenterContainer extends Serializable
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/purchase/CommerceIdentifierCostCenterContainer.java#2 $$Change: 651448 $";
    
  /**
   * The <code>getCommerceIdentifierCostCenterMap</code> method returns the Map of
   * CommerceIdentifiers to CommerceIdentifierCostCenter Lists.
   *
   * @return a <code>Map</code> value
   */
  public Map getCommerceIdentifierCostCenterMap ();

  /**
   * The <code>getCommerceIdentifierCostCenters</code> method returns the List of
   * CommerceIdentifierCostCenters corresponding to the supplied CommerceIdentifierId.
   *
   * @param pCommerceIdentifierId a <code>String</code> value
   * @return a <code>List</code> value
   */
  public List getCommerceIdentifierCostCenters (String pCommerceIdentifierId);

  /**
   * The <code>getAllCommerceIdentifierCostCenters</code> method returns one List of all
   * CommerceIdentifierCostCenters in the entire Map.
   *
   * @return a <code>List</code> value
   */
  public List getAllCommerceIdentifierCostCenters ();

  /**
   * The <code>addCommerceIdentifierCostCenter</code> method adds the supplied
   * CommerceIdentifierId and CommerceIdentifierCostCenter to the Map.
   *
   * @param pCommerceIdentifierId a <code>String</code> value
   * @param pCommerceIdentifierCostCenter a <code>CommerceIdentifierCostCenter</code> value
   */
  public void addCommerceIdentifierCostCenter (String pCommerceIdentifierId,
					       CommerceIdentifierCostCenter pCommerceIdentifierCostCenter);

  /**
   * The <code>removeAllCommerceIdentifierCostCenters</code> method clears the Map.
   *
   */
  public void removeAllCommerceIdentifierCostCenters ();

  /**
   * The <code>removeCommerceIdentifierCostCenters</code> method removes the
   * CommerceIdentifierCostCenters corresponding to the supplied CommerceIdentifierId.
   *
   * @param pCommerceIdentifierId a <code>String</code> value
   */
  public void removeCommerceIdentifierCostCenters (String pCommerceIdentifierId);

}   // end of class
