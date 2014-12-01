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
import atg.repository.RepositoryItem;

/**
 * The <code>UserInvoiceRequestAuthorizer</code> authorizes an invoice request
 * payment type for a particular user.
 *
 * @author Ernesto Mireles
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/order/UserInvoiceRequestAuthorizer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class UserInvoiceRequestAuthorizer
  extends GenericService
  implements UserPaymentTypeAuthorizer
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/order/UserInvoiceRequestAuthorizer.java#2 $$Change: 651448 $";
    
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
  // property: InvoiceRequestAuthorizedProperty
  //---------------------------------------------------------------------------
  String mInvoiceRequestAuthorizedProperty;

  /**
   * Set the InvoiceRequestAuthorizedProperty property.
   * @param pInvoiceRequestAuthorizedProperty a <code>String</code> value
   */
  public void setInvoiceRequestAuthorizedProperty(String pInvoiceRequestAuthorizedProperty) {
    mInvoiceRequestAuthorizedProperty = pInvoiceRequestAuthorizedProperty;
  }

  /**
   * Return the InvoiceRequestAuthorizedProperty property.
   * @return a <code>String</code> value
   */
  public String getInvoiceRequestAuthorizedProperty() {
    return mInvoiceRequestAuthorizedProperty;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>UserInvoiceRequestAuthorizer</code> instance.
   *
   */
  public UserInvoiceRequestAuthorizer () {}
  
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  /**
   * The <code>authorizePaymentType</code> method authorizes an invoice request
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
      Boolean value = (Boolean) DynamicBeans.getPropertyValue(pProfile, getInvoiceRequestAuthorizedProperty());
      if (value != null)
        return value.booleanValue();
      else {
	logError("Property " + getInvoiceRequestAuthorizedProperty() + " for Profile ID " + pProfile.getRepositoryId() +
		 "is null");
        return false;
      }
    
    } catch (PropertyNotFoundException pnfe) {
      logError(pnfe);
      throw new PaymentTypeAuthorizationException(pnfe);
    }
  }
}   // end of class
