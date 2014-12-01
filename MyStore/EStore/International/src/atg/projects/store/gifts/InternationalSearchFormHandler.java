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
package atg.projects.store.gifts;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.projects.store.multisite.InternationalStoreSitePropertiesManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * Store extension of the SearchFormHandler from DCS.
 * Filters giftslists with non-shippable addresses.
 *
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/src/atg/projects/store/gifts/InternationalSearchFormHandler.java#3 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class InternationalSearchFormHandler extends StoreSearchFormHandler {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/src/atg/projects/store/gifts/InternationalSearchFormHandler.java#3 $$Change: 635816 $";

  //-------------------------------
  // Constants
  //-------------------------------
  private static final String SHIPPING_ADDRESS = "shippingAddress";  
  private static final String COUNTRY = "country";
  
  //-------------------------------
  // Properties
  //-------------------------------
    
  //-------------------------------------
  // property: StoreSitePropertiesManager
  //-------------------------------------
  protected InternationalStoreSitePropertiesManager mStoreSitePropertiesManager;

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
   * Remove any giftlist whose shipping addresses are non-shippable from
   * the current country store.
   *
   * @param pDynamoHttpServletRequest - http request
   * @param pDynamoHttpServletResponse - http response
   * @throws javax.servlet.ServletException
   * @throws java.io.IOException
   */
  public void postSearch(DynamoHttpServletRequest pDynamoHttpServletRequest, DynamoHttpServletResponse pDynamoHttpServletResponse) throws javax.servlet.ServletException, IOException {
    super.postSearch(pDynamoHttpServletRequest, pDynamoHttpServletResponse);

    Site currentSite = SiteContextManager.getCurrentSiteContext().getSite();
    InternationalStoreSitePropertiesManager propertiesManager = getStoreSitePropertiesManager();
    
    // gt list of shippable and non-shippable countries
    List shippableCountries = (List)currentSite.getPropertyValue(propertiesManager.getShippableCountriesPropertyName());
    List nonShippableCountries = (List) currentSite.getPropertyValue(propertiesManager.getNonShippableCountriesPropertyName());

    Collection giftlists = getSearchResults();

    if(giftlists != null && giftlists.size() > 0) {
      for (Iterator iterator = giftlists.iterator(); iterator.hasNext();) {
        // check giftlist's shipping addresses
        RepositoryItem shippingAddress = (RepositoryItem) ((RepositoryItem) iterator.next()).getPropertyValue(SHIPPING_ADDRESS);

        if (shippingAddress == null) {
          // current gift-list has no shipping address, just do nothing
          // this was asked by CSC team, they do not filter gift-lists by shippable countries
          // we do not have gift-list without addresses, so it's not impact our functionality
          continue;
        }
        
        String country = (String) shippingAddress.getPropertyValue(COUNTRY);

        // check in non-shippable list
        if(nonShippableCountries != null && nonShippableCountries.size() > 0) {
          if(nonShippableCountries.contains(country)) {
            if(isLoggingDebug()) {
              logDebug("Country [" + country + "] is non-shippable for this giftlist");
            }
            iterator.remove();
            // go to next iteration step to prevent double removal of current gift-list
            continue;
          }
        }

        // check in shippable list
        if(shippableCountries != null && shippableCountries.size() > 0) {
          if(!shippableCountries.contains(country)) {
            if(isLoggingDebug()) {
              logDebug("Country [" + country + "] is non-shippable for this giftlist");
            }
            iterator.remove();
          }
        }
      }
    }
  }
}

