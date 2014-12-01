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
import java.io.Serializable;
import java.util.Map;
import java.util.List;

/**
 * The <code>CommerceIdentifierPaymentInfoContainer</code> interface is used to represent
 * a container of a Map of CommerceIdentifiers [key] to CommerceIdentifierPaymentInfo Lists [value].
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CommerceIdentifierPaymentInfoContainer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public interface CommerceIdentifierPaymentInfoContainer extends Serializable
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CommerceIdentifierPaymentInfoContainer.java#2 $$Change: 651448 $";
    
  /**
   * The <code>getCommerceIdentifierPaymentInfoMap</code> method returns the Map of
   * CommerceIdentifiers to CommerceIdentifierPaymentInfo Lists.
   *
   * @return a <code>Map</code> value
   */
  public Map getCommerceIdentifierPaymentInfoMap ();

  /**
   * The <code>getCommerceIdentifierPaymentInfos</code> method returns the List of
   * CommerceIdentifierPaymentInfos corresponding to the supplied CommerceIdentifierId.
   *
   * @param pCommerceIdentifierId a <code>String</code> value
   * @return a <code>List</code> value
   */
  public List getCommerceIdentifierPaymentInfos (String pCommerceIdentifierId);

  /**
   * The <code>getAllCommerceIdentifierPaymentInfos</code> method returns one List of all
   * CommerceIdentifierPaymentInfos in the entire Map.
   *
   * @return a <code>List</code> value
   */
  public List getAllCommerceIdentifierPaymentInfos ();

  /**
   * The <code>addCommerceIdentifierPaymentInfo</code> method adds the supplied
   * CommerceIdentifierId and CommerceIdentifierPaymentInfo to the Map.
   *
   * @param pCommerceIdentifierId a <code>String</code> value
   * @param pCommerceIdentifierPaymentInfo a <code>CommerceIdentifierPaymentInfo</code> value
   */
  public void addCommerceIdentifierPaymentInfo (String pCommerceIdentifierId,
                                                CommerceIdentifierPaymentInfo pCommerceIdentifierPaymentInfo);

  /**
   * The <code>removeAllCommerceIdentifierPaymentInfos</code> method clears the Map.
   *
   */
  public void removeAllCommerceIdentifierPaymentInfos ();

  /**
   * The <code>removeCommerceIdentifierPaymentInfos</code> method removes the
   * CommerceIdentifierPaymentInfo corresponding to the supplied CommerceIdentifierId.
   *
   * @param pCommerceIdentifierId a <code>String</code> value
   */
  public void removeCommerceIdentifierPaymentInfos (String pCommerceIdentifierId);

}   // end of interface
