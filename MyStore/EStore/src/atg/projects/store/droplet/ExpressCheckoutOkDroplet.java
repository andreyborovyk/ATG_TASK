/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2010 Art Technology Group, Inc.
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

package atg.projects.store.droplet;

import atg.nucleus.naming.ParameterName;

import atg.projects.store.profile.StorePropertyManager;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;

import javax.servlet.ServletException;


/**
 * Checks to see if this user can use ExpressCheckout or not.
 *
 * @author ATG
 * @version $Revision: #3 $
 */
public class ExpressCheckoutOkDroplet extends DynamoServlet {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/droplet/ExpressCheckoutOkDroplet.java#3 $$Change: 635816 $";

  /** Input parameter name profile. */
  public static final ParameterName PROFILE = ParameterName.getParameterName("profile");

  /** Oparam true. */
  public static final ParameterName TRUE = ParameterName.getParameterName("true");

  /** Oparam false. */
  public static final ParameterName FALSE = ParameterName.getParameterName("false");

  /**
   * Store property manager.
   */
  private StorePropertyManager mStorePropertyManager;

  /**
   * @return the mStore property manager.
   */
  public StorePropertyManager getStorePropertyManager() {
    return mStorePropertyManager;
  }

  /**
   * @param pStorePropertyManager - the mStorePropertyManager to set.
   */
  public void setStorePropertyManager(StorePropertyManager pStorePropertyManager) {
    mStorePropertyManager = pStorePropertyManager;
  }

  /**
   * Services true oparam if user can use express checkout, false if not.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    Object profileItem = pRequest.getObjectParameter(PROFILE);
    StorePropertyManager pmgr = getStorePropertyManager();

    if ((profileItem == null) || !(profileItem instanceof RepositoryItem)) {
      if (isLoggingDebug()) {
        logDebug("Bad profile parameter passed: null=" + (profileItem == null) +
          ". If null=false, then wrong object type.");
      }

      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);

      return;
    }

    RepositoryItem profile = (RepositoryItem) profileItem;
    
    // Transient (guest) users can't use express checkout option
    if (profile.isTransient())
    {
      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
      return;
    }

    if (isLoggingDebug()) {
      logDebug("Default shipping address name: " + pmgr.getShippingAddressPropertyName());
    }

    // Validate default shipping address for Express Checkout
    if (!validateShippingAddressForExpressCheckout(profile)) {
      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
      return;
    }

    // Validate default credit card for Express Checkout
    if (!validateCreditCardForExpressCheckout(profile)) {
      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
      return;
    }

    // Return false if default shipping method not set
    String defaultShippingMethod = (String) profile.getPropertyValue(pmgr.getDefaultShippingMethodPropertyName());

    if ((defaultShippingMethod == null) || (defaultShippingMethod.trim().length() == 0)) {
      if (isLoggingDebug()) {
        logDebug("User's " + pmgr.getDefaultShippingMethodPropertyName() + " is empty");
      }

      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);

      return;
    }

    // If all the above checks were successful, then user can use
    //  Express checkout.
    pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
  }
  
  /**
   * Validates profile's default shipping address for Express Checkout.
   * 
   * @param pProfile profile repository item
   * @return true if profile's default shipping address is valid for Express Checkout
   */
  public boolean validateShippingAddressForExpressCheckout(RepositoryItem pProfile) {
    // Return false if default shipping not set
    String shippingAddressPropertyName = getStorePropertyManager().getShippingAddressPropertyName();
    if (pProfile.getPropertyValue(shippingAddressPropertyName) == null) {
      if (isLoggingDebug()) {
        logDebug("User's " + shippingAddressPropertyName + " is null");
      }

      return false;
    }
    return true;
  }

  /**
   * Validates profile's default credit card for Express Checkout.
   * 
   * @param pProfile profile repository item
   * @return true if profile's default credit card is valid for Express Checkout
   */
  public boolean validateCreditCardForExpressCheckout(RepositoryItem pProfile) {
    // Return false if default cc not set
    String defaultCreditCardPropertyName = getStorePropertyManager().getDefaultCreditCardPropertyName();
    if (pProfile.getPropertyValue(defaultCreditCardPropertyName) == null) {
      if (isLoggingDebug()) {
        logDebug("User's " + defaultCreditCardPropertyName + " is null");
      }
      return false;
    }
    return true;
  }
  
  
}
