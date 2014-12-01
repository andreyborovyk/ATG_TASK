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

import atg.commerce.CommerceException;
import atg.userprofiling.Profile;
import atg.servlet.DynamoHttpServletRequest;

/**
 * The <code>PaymentGroupInitializer</code> interface is used to initialize
 * PaymentGroup(s) for the user to be used during the purchase process. This is
 * designed to be implemented on a per PaymentGroup type basis. The
 * initializePaymentGroups method is invoked by the PaymentGroupDroplet.
 * Different instances of this interface may be registered with the
 * PaymentGroupDroplet.PaymentGroupInitializers ServiceMap, keyed by PaymentGroup type.
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/PaymentGroupInitializer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public interface PaymentGroupInitializer
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/PaymentGroupInitializer.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------
  
  /**
   * The <code>initializePaymentGroups</code> method is used to initialize
   * one or more PaymentGroups for the given user. These PaymentGroups are
   * added to the PaymentGroupMapContainer. As a convenience, this method
   * will be invoked from the PaymentGroupDroplet and passed the
   * DynamoHttpServletRequest, from which any component of any scope may
   * be obtained.
   *
   * @param pProfile a <code>Profile</code> value
   * @param pPaymentGroupMapContainer a <code>PaymentGroupMapContainer</code> value
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @exception PaymentGroupInitializationException if an error occurs
   */
  public void initializePaymentGroups (Profile pProfile,
                                       PaymentGroupMapContainer pPaymentGroupMapContainer,
                                       DynamoHttpServletRequest pRequest)
    throws PaymentGroupInitializationException;
  
}   // end of interface
