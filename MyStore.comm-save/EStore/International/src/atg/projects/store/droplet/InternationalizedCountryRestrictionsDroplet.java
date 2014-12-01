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

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.projects.store.multisite.InternationalStoreSitePropertiesManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * <p>
 * Internationalized version of base CountryRestrictionsDroplet
 * <p>
 * This droplet takes the following input parameters:
 * <ul>
 * <li>NONE
 * </ul>
 * <p>
 * This droplet renders the following oparams:
 * <ul>
 * <li>output - of CountryRestrictionsDroplet.
 * </ul>
 * <p>
 * This droplet sets the following output parameters:
 * <ul>
 * <li>countries - of CountryRestrictionsDroplet.
 * </ul>
 * <p>
 * @author ATG
 * @updated 23rd August 2006
 * @version 1.0
 */
public class InternationalizedCountryRestrictionsDroplet extends CountryRestrictionsDroplet {

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/src/atg/projects/store/droplet/InternationalizedCountryRestrictionsDroplet.java#3 $$Change: 635816 $";

  //-------------------------------
  // Constants
  //-------------------------------
  protected static final String BILLTO = "billto";
  protected static final String SHIPTO = "shipto";

  // input parameters

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

  //-------------------------------
  // property: Restriction
  private String mRestriction;

  /**
   *
   * @return restriction string
   */
  public String getRestriction(){
    return mRestriction;
  }

  /**
   *
   * @param pRestriction restriction string
   */
  public void setRestriction(String pRestriction){
    mRestriction = pRestriction;
  }

  /**
   * {@inheritDoc}
   */
  public void service(DynamoHttpServletRequest pRequest,
                      DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {

    String restriction = getRestriction();
    List listCountryCds = null;
    Site currentSite = SiteContextManager.getCurrentSiteContext().getSite();
    InternationalStoreSitePropertiesManager propManager = getStoreSitePropertiesManager();

    if (restriction != null && restriction.equalsIgnoreCase(SHIPTO)){//Find Shippable Countries
      listCountryCds = (List) currentSite.getPropertyValue(propManager.getShippableCountriesPropertyName());

      if(listCountryCds != null && !(listCountryCds.isEmpty())){//Shippable country codes are specified
            setPermittedCountryCodes(listCountryCds);
            setRestrictedCountryCodes(null);
        }else{//NonShippable country codes are specified
           listCountryCds = (List) currentSite.getPropertyValue(propManager.getNonShippableCountriesPropertyName());
           if(listCountryCds != null && !(listCountryCds.isEmpty())){
             setPermittedCountryCodes(null);
             setRestrictedCountryCodes(listCountryCds);
           }
         }//END NonShippable country codes are specified
    }//END Find Shippable Countries
    else if(restriction != null && restriction.equalsIgnoreCase(BILLTO)){ //Find Billable Countries
      listCountryCds = (List) currentSite.getPropertyValue(propManager.getBillableCountriesPropertyName());
      if(listCountryCds != null && !(listCountryCds.isEmpty())){//Billable country codes are specified
          setPermittedCountryCodes(listCountryCds);
          setRestrictedCountryCodes(null);
        }else{//NonShippable country codes are specified
          listCountryCds = (List) currentSite.getPropertyValue(propManager.getNonBillableCountriesPropertyName());
          if(listCountryCds != null && !(listCountryCds.isEmpty())){
            setPermittedCountryCodes(null);
            setRestrictedCountryCodes(listCountryCds);
          }
        }//END NonShippable country codes are specified
      }//END Find Billable Countries
    super.service(pRequest, pResponse);
  }
}
