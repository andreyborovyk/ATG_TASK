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
 * The <code>ShippingGroupInitializer</code> interface is used to initialize
 * ShippingGroup(s) for the user to be used during the purchase process. This is
 * designed to be implemented on a per ShippingGroup type basis. The
 * initializeShippingGroups method is invoked by the ShippingGroupDroplet.
 * Different instances of this interface may be registered with the
 * ShippingGroupDroplet.ShippingGroupInitializers ServiceMap, keyed by ShippingGroup type.
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/ShippingGroupInitializer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public interface ShippingGroupInitializer
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/ShippingGroupInitializer.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------
  
  /**
   * The <code>initializeShippingGroups</code> method is used to initialize
   * one or more ShippingGroups for the given user. These ShippingGroups are
   * added to the ShippingGroupMapContainer. As a convenience, this method
   * will be invoked from the ShippingGroupDroplet and passed the
   * DynamoHttpServletRequest, from which any component of any scope may
   * be obtained.
   *
   * @param pProfile a <code>Profile</code> value
   * @param pShippingGroupMapContainer a <code>ShippingGroupMapContainer</code> value
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @exception ShippingGroupInitializationException if an error occurs
   */
  public void initializeShippingGroups (Profile pProfile,
                                        ShippingGroupMapContainer pShippingGroupMapContainer,
                                        DynamoHttpServletRequest pRequest)
    throws ShippingGroupInitializationException;
  
}   // end of interface
