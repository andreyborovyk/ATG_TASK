/*<ATGCOPYRIGHT>
 * Copyright (C) 2005-2010 Art Technology Group, Inc.
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

package atg.repository.search.indexing.accessor;

import java.util.HashMap;

import atg.commerce.search.producer.PricePropertyAccessor;
import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.projects.store.multisite.InternationalStoreSitePropertiesManager;

/**
 * Store extension for getting the price of an item from the appropriate
 * price list using property accessor mechanism. 
 *  
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/Search/Index/src/atg/repository/search/indexing/accessor/InternationalPricePropertyAccessor.java#3 $$Change: 635816 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */

public class InternationalPricePropertyAccessor extends PricePropertyAccessor {

  /**
   * Class version string
   */
  public static final String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/Search/Index/src/atg/repository/search/indexing/accessor/InternationalPricePropertyAccessor.java#3 $$Change: 635816 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  protected static String SALE_PRICELIST_ID = "SalePriceListId";
  protected static String LIST_PRICELIST_ID = "ListPriceListId";

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
   * This method find the appropriate priceList Ids for a international store.
   * the pricelist ids are present in the store configuration file.
   * @param pPropertyToParse The name of the property specified in xml.
   * @return Object is having the priceListID.
   */
  protected HashMap getPriceListIds(String pPropertyToParse){ 

    HashMap priceListIdMap = new HashMap();
    InternationalStoreSitePropertiesManager propertiesManager = getStoreSitePropertiesManager();
    Site currentSite = SiteContextManager.getCurrentSiteContext().getSite();
    
    priceListIdMap.put(SALE_PRICELIST_ID,
        (String)currentSite.getPropertyValue(propertiesManager.getSalePriceListIdPropertyName()));
    priceListIdMap.put(LIST_PRICELIST_ID,
        (String)currentSite.getPropertyValue(propertiesManager.getListPriceListIdPropertyName()));
    return priceListIdMap;   
  }
}
