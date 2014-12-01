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

package atg.projects.b2bstore.order;
import atg.commerce.order.UserPaymentTypeAuthorizer;
import atg.commerce.order.PaymentTypeAuthorizationException;
import atg.userprofiling.Profile;
import atg.nucleus.GenericService;
import atg.beans.*;

/**
 * The <code>UserStoreCreditAuthorizer</code> authorizes a store credit
 * payment type for a particular user.
 *
 * @author Ernesto Mireles
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/order/UserStoreCreditAuthorizer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class UserStoreCreditAuthorizer
  extends GenericService
  implements UserPaymentTypeAuthorizer
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/order/UserStoreCreditAuthorizer.java#2 $$Change: 651448 $";
    
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
  // property: StoreCreditAuthorizedProperty
  //---------------------------------------------------------------------------
  String mStoreCreditAuthorizedProperty;

  /**
   * Set the StoreCreditAuthorizedProperty property.
   * @param pStoreCreditAuthorizedProperty a <code>String</code> value
   */
  public void setStoreCreditAuthorizedProperty(String pStoreCreditAuthorizedProperty) {
    mStoreCreditAuthorizedProperty = pStoreCreditAuthorizedProperty;
  }

  /**
   * Return the StoreCreditAuthorizedProperty property.
   * @return a <code>String</code> value
   */
  public String getStoreCreditAuthorizedProperty() {
    return mStoreCreditAuthorizedProperty;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>UserStoreCreditAuthorizer</code> instance.
   *
   */
  public UserStoreCreditAuthorizer () {}
  
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  /**
   * The <code>authorizePaymentType</code> method authorizes a store credit
   * payment type for the given Profile.
   *
   * @param pProfile a <code>Profile</code> value
   * @return a <code>boolean</code> value
   * @exception PaymentTypeAuthorizationException if an error occurs
   */
  public boolean authorizePaymentType (Profile pProfile)
    throws PaymentTypeAuthorizationException
  {
    try {
      Boolean value = (Boolean) DynamicBeans.getPropertyValue(pProfile, getStoreCreditAuthorizedProperty());
      if (value != null)
        return value.booleanValue();
      else
        throw new PaymentTypeAuthorizationException();
    } catch (PropertyNotFoundException pnfe) {
      logError(pnfe);
      throw new PaymentTypeAuthorizationException(pnfe);
    }
  }
}   // end of class
