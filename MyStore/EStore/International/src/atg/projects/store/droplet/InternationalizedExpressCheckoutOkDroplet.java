/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2010 Art Technology Group, Inc.
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

import java.util.List;

import atg.core.util.StringUtils;
import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.projects.store.multisite.InternationalStoreSitePropertiesManager;
import atg.repository.RepositoryItem;

/**
 * Internationalized version of base ExpressCheckoutOkDroplet.
 * When validating user's shipping address and credit card takes also into account
 * whether default shipping and billing addresses are allowed for shipping and billing 
 * correspondingly in the international store.
 * 
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/src/atg/projects/store/droplet/InternationalizedExpressCheckoutOkDroplet.java#3 $$Change: 635816 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
*/

public class InternationalizedExpressCheckoutOkDroplet extends ExpressCheckoutOkDroplet {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/src/atg/projects/store/droplet/InternationalizedExpressCheckoutOkDroplet.java#3 $$Change: 635816 $";

  //-------------------------------
  // Constants
  //-------------------------------

  //-------------------------------
  // Properties
  //-------------------------------
  
  //-------------------------------------
  // property: StoreSitePropertiesManager
  //-------------------------------------
  private InternationalStoreSitePropertiesManager mStoreSitePropertiesManager;

  /**
   * @return the InternationalStoreSitePropertiesManager
   */
  public InternationalStoreSitePropertiesManager getStoreSitePropertiesManager() {
    return mStoreSitePropertiesManager;
  }

  /**
   * @param StoreSitePropertiesManager the InternationalStoreSitePropertiesManager to set
   */
  public void setStoreSitePropertiesManager(InternationalStoreSitePropertiesManager pStoreSitePropertiesManager) {
    mStoreSitePropertiesManager = pStoreSitePropertiesManager;
  }

  /**
   * Checks if profile's default credit card is valid for Express Checkout.
   * It is considered valid if billing address's country is in billable countries list
   * specified in store configuration.
   * 
   * @param pProfile profile repository item
   * @return true if profile's default credit card is valid for Express Checkout
   */
  public boolean validateCreditCardForExpressCheckout(RepositoryItem pProfile) {

    String creditCardPropertyName = getStorePropertyManager().getDefaultCreditCardPropertyName();
    RepositoryItem card = (RepositoryItem) pProfile.getPropertyValue(creditCardPropertyName);
    RepositoryItem billingAddress = null;
    String countryCode = null;

    if (card != null) {
      billingAddress = (RepositoryItem) card.getPropertyValue(getStorePropertyManager().getCreditCardBillingAddressPropertyName());
      if (billingAddress != null) {
        countryCode = (String) billingAddress.getPropertyValue(getStorePropertyManager().getAddressCountryPropertyName());
      }
      
      Site currentSite = SiteContextManager.getCurrentSiteContext().getSite(); 
      InternationalStoreSitePropertiesManager propManager = getStoreSitePropertiesManager();
      
      List billableCountries = 
        (List) currentSite.getPropertyValue(propManager.getBillableCountriesPropertyName());
      List nonBillableCountries = 
        (List) currentSite.getPropertyValue(propManager.getNonBillableCountriesPropertyName());
      return isCountryValid(countryCode, billableCountries,
                            nonBillableCountries);
    }

    return false;
  }

  /**
   * Checks if profile's default shipping address is valid for Express Checkout.
   * It is considered valid if shipping address's country is in shippable countries list
   * specified in store configuration.
   * 
   * @param pProfile profile repository item
   * @return true if profile's default shipping address is valid for Express Checkout
   */
  public boolean validateShippingAddressForExpressCheckout(RepositoryItem pProfile) {
    String shippingAddressPropertyName = getStorePropertyManager().getShippingAddressPropertyName();
    RepositoryItem shippingAddress = (RepositoryItem) pProfile.getPropertyValue(shippingAddressPropertyName);
    if (shippingAddress != null) {
      String countryCode = (String) shippingAddress.getPropertyValue(getStorePropertyManager().getAddressCountryPropertyName());
      Site currentSite = SiteContextManager.getCurrentSiteContext().getSite();
      InternationalStoreSitePropertiesManager propManager = getStoreSitePropertiesManager();
      
      List shippableCountries = 
        (List) currentSite.getPropertyValue(propManager.getShippableCountriesPropertyName());
      List nonShippableCountries = 
        (List)currentSite.getPropertyValue(propManager.getNonShippableCountriesPropertyName());
      return isCountryValid(countryCode, shippableCountries,
                            nonShippableCountries);

    }
    return false;
  }

  /**
   * Helper method that checks that given country code is in list of allowed countries,
   * if list of allowed countries is not specified then checks that given country code
   * is not in the list of restricted countries.
   * 
   * @param pCountryCode country code to check
   * @param pCountryList allowed countries list
   * @param pRestrictedCountryList restricted countries list
   * @return true if country code is in list of allowed countries,if list of allowed 
   *         countries is not specified then returns true if given country code
   *         is not in the list of restricted countries.
   */
  public boolean isCountryValid(String pCountryCode, List pCountryList, List pRestrictedCountryList) {
    boolean valid = false;

    if (!StringUtils.isEmpty(pCountryCode)) {

      if (pCountryList != null && !(pCountryList.isEmpty())) {
        //Allowed country codes are specified
        if (pCountryList.contains(pCountryCode)) {
          valid = true;
        }
      } else {
        if (pRestrictedCountryList != null && !(pRestrictedCountryList.isEmpty())) {
          //Restricted country codes are specified
          if (!pRestrictedCountryList.contains(pCountryCode)) {
            valid = true;
          }
        } else {
          // both allowed and restricted country lists are not specified
          // so assume that all countries are allowed
          valid = true;
        }
      }
    }
    return valid;

  }

}
