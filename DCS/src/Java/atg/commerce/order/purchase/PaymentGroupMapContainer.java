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

/**
 * The <code>PaymentGroupMapContainer</code> interface is used to represent a container
 * of a Map of payment names [key] to PaymentGroups [value].
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/PaymentGroupMapContainer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public interface PaymentGroupMapContainer extends Serializable
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/PaymentGroupMapContainer.java#2 $$Change: 651448 $";
    
  /**
   * The <code>getPaymentGroupMap</code> method returns the Map of payment names
   * to PaymentGroups.
   *
   * @return a <code>Map</code> value
   */
  public Map getPaymentGroupMap();

  /**
   * The <code>getPaymentGroup</code> method returns the PaymentGroup corresponding
   * to the supplied payment name.
   *
   * @param pPaymentGroupName a <code>String</code> value
   * @return a <code>PaymentGroup</code> value
   */
  public PaymentGroup getPaymentGroup(String pPaymentGroupName);
  
  /**
   * The <code>addPaymentGroup</code> method adds the supplied payment name
   * and PaymentGroup to the Map.
   *
   * @param pPaymentGroupName a <code>String</code> value
   * @param pPaymentGroup a <code>PaymentGroup</code> value
   */
  public void addPaymentGroup(String pPaymentGroupName, PaymentGroup pPaymentGroup);

  /**
   * The <code>removeAllPaymentGroups</code> method clears the Map.
   *
   */
  public void removeAllPaymentGroups();

  /**
   * The <code>removePaymentGroup</code> method removes the PaymentGroup corresponding
   * to the supplied payment name.
   *
   * @param pPaymentGroupName a <code>String</code> value
   */
  public void removePaymentGroup(String pPaymentGroupName);
  
  /**
   * The <code>setDefaultPaymentGroupName</code> method marks the name of the
   * default PaymentGroup.
   *
   * @param pDefaultPaymentGroupName a <code>String</code> value
   */
  public void setDefaultPaymentGroupName(String pDefaultPaymentGroupName);
  
  /**
   * The <code>getDefaultPaymentGroupName</code> method retrieves the name of the
   * default PaymentGroup.
   *
   * @return a <code>String</code> value
   */
  public String getDefaultPaymentGroupName();

}   // end of interface
