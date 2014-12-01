/*<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
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

package atg.portal.gear.screenscraper;

import atg.nucleus.logging.ApplicationLoggingImpl;


/**
* All the Configuration info global to the HTML Screen Scraper gear is stored 
* here, including instance config parameter names, alert and caching enabling 
* flags.
* 
* @author Ashish Dwivedi
 * @version $Id: //app/portal/version/10.0.3/screenscraper/src/atg/portal/gear/screenscraper/Configuration.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
*/

public
class Configuration
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/screenscraper/src/atg/portal/gear/screenscraper/Configuration.java#2 $$Change: 651448 $";

  //-------------------------------------
  // properties
  //-------------------------------------
  // property instanceSharedURLParameterName
  String mInstanceSharedURLParameterName="instanceSharedURL";
  public String getInstanceSharedURLParameterName () {
    return mInstanceSharedURLParameterName;
  }
  public void setInstanceSharedURLParameterName (String pInstanceSharedURLParameterName) {
    if (mInstanceSharedURLParameterName != pInstanceSharedURLParameterName) {
      mInstanceSharedURLParameterName = pInstanceSharedURLParameterName;
    }
  }

  // property instanceFullURLParameterName
  String mInstanceFullURLParameterName="instanceFullURL";
  public String getInstanceFullURLParameterName () {
    return mInstanceFullURLParameterName;
  }
  public void setInstanceFullURLParameterName (String pInstanceFullURLParameterName) {
    if (mInstanceFullURLParameterName != pInstanceFullURLParameterName) {
      mInstanceFullURLParameterName = pInstanceFullURLParameterName;
    }
  }

  // property resourceBundleParameterName
  String mResourceBundleNameParameterName="resourceBundleName";
  public String getResourceBundleNameParameterName () {
    return mResourceBundleNameParameterName;
  }
  public void setResourceBundleNameParameterName (String pResourceBundleNameParameterName) {
    if (mResourceBundleNameParameterName != pResourceBundleNameParameterName) {
      mResourceBundleNameParameterName = pResourceBundleNameParameterName;
    }
  }

  // property fullModeLinkTextParameterName
  String mFullModeLinkTextParameterName = "fullModeLinkText";
  public String getFullModeLinkTextParameterName () {
    return mFullModeLinkTextParameterName;
  }
  public void setFullModeLinkTextParameterName (String pFullModeLinkTextParameterName) {
    if (mFullModeLinkTextParameterName != pFullModeLinkTextParameterName) {
      mFullModeLinkTextParameterName = pFullModeLinkTextParameterName;
    }
  }


   // property ScreenScraperWebClientComponentJNDIName
  String mScreenScraperWebClientComponentJNDIName= "dynamo:/atg/portal/gear/screenscraper/ScreenScraperWebClient";
  public String getScreenScraperWebClientComponentJNDIName () {
    return mScreenScraperWebClientComponentJNDIName;
  }
  public void setScreenScraperWebClientComponentJNDIName (String pScreenScraperWebClientComponentJNDIName) {
    if (mScreenScraperWebClientComponentJNDIName != pScreenScraperWebClientComponentJNDIName) {
      mScreenScraperWebClientComponentJNDIName = pScreenScraperWebClientComponentJNDIName;
    }
  }

  /**
   * Whether to send alerts for change in urls for shared and full mode pages.
   */
  
  // property enableSendAlerts
  boolean mEnableSendAlerts=true;
  public boolean isEnableSendAlerts() {
    return this.mEnableSendAlerts;
  }

  public void setEnableSendAlerts(boolean pEnableSendAlerts) {
    this.mEnableSendAlerts = pEnableSendAlerts;
  }
  
  /**
   * Whether to cache web content after retrieval and parsing.
   */
  
    // property disableCaching
  boolean mDisableCaching=false;
  public boolean getDisableCaching () {
    return mDisableCaching;
  }
  public void setDisableCaching (boolean pDisableCaching) {
    if (mDisableCaching != pDisableCaching) {
      mDisableCaching = pDisableCaching;
    }
  }

  //-------------------------------------
  // methods
 /**
  * 
  */
 
  //-------------------------------------
  // Constructors
  //-------------------------------------
  public Configuration ()
  {
  }

}
