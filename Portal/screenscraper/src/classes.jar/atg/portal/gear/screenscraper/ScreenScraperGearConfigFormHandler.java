/*<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of thisadd
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

import atg.core.util.ResourceUtils;
import atg.droplet.DropletException;
import atg.portal.alert.GearMessage;
import atg.portal.alert.GearMessagePublisher;
import atg.portal.framework.GearConfigFormHandler;
import atg.portal.framework.GearEnvironment;

import atg.portal.nucleus.NucleusComponents;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.jms.JMSException;


/**
* Form handler which does validation on values before saving them as gear 
* config parameters for the HTML Screen Scraper gears instanceConfig form
* 
* @author Ashish Dwivedi
 * @version $Id: //app/portal/version/10.0.3/screenscraper/src/atg/portal/gear/screenscraper/ScreenScraperGearConfigFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
*/

public
class ScreenScraperGearConfigFormHandler
extends GearConfigFormHandler
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/screenscraper/src/atg/portal/gear/screenscraper/ScreenScraperGearConfigFormHandler.java#2 $$Change: 651448 $";


  public static final String INVALID_HTTP_URL  = "invalidHttpURL";
  public static final String INVALID_URL  = "invalidURL";
  public static final String RESOURCE_BUNDLE_NOT_FOUND  = "resourceBundleNotFound";
  public static final String RESOURCE_BUNDLE_MUST_BE_ENTERED  = "resourceBundleMustBeEntered";
    public static final String LINK_TEXT_MUST_BE_SPECIFIED  = "linkTextMustBeSpecified"; 
  
  GearEnvironment mGearEnv;
  String  mOldSharedURL;
  String  mOldFullURL;


  //-------------------------------------
  // properties
  //-------------------------------------
  
  // property publisher
  GearMessagePublisher mPublisher;
  public GearMessagePublisher getPublisher() {
    return mPublisher;
  }
  public void setPublisher(GearMessagePublisher pPublisher) {
    this.mPublisher = pPublisher;
  }

  // property instanceSharedURL
  String mInstanceSharedURL;  
  public String getInstanceSharedURL () {
   if(mInstanceSharedURL==null) 
       return mGearEnv.getGearInstanceParameter(getConfiguration().getInstanceSharedURLParameterName());
    return mInstanceSharedURL;
  }
  public void setInstanceSharedURL (String pInstanceSharedURL) {
    if (mInstanceSharedURL != pInstanceSharedURL) {
      mInstanceSharedURL = pInstanceSharedURL;
    }
  }

  // property instanceFullURL
  String mInstanceFullURL;
  public String getInstanceFullURL () {
    if(mInstanceSharedURL==null) {
      return mGearEnv.getGearInstanceParameter(getConfiguration().getInstanceFullURLParameterName());
    }
    return mInstanceFullURL;
  }
  public void setInstanceFullURL (String pInstanceFullURL) {
    if (mInstanceFullURL != pInstanceFullURL) {
      mInstanceFullURL = pInstanceFullURL;
    }
  }

  // property resourceBundle
  String mResourceBundleName;
  public String getResourceBundleName () {
    if(mResourceBundleName==null)
       return mGearEnv.getGearInstanceParameter(getConfiguration().getResourceBundleNameParameterName());
    return mResourceBundleName;
  }
  public void setResourceBundleName (String pResourceBundleName) {
    if (mResourceBundleName != pResourceBundleName) {
      mResourceBundleName = pResourceBundleName;
    }
  }

   // property fullModeLinkText
  String mFullModeLinkText;
  public String getFullModeLinkText () {
    if(mResourceBundleName==null)
       return mGearEnv.getGearInstanceParameter(getConfiguration().getFullModeLinkTextParameterName());
    return mFullModeLinkText;
  }
  public void setFullModeLinkText (String pFullModeLinkText) {
    if (mFullModeLinkText != pFullModeLinkText) {
      mFullModeLinkText = pFullModeLinkText;
    }
  }


 // property propertyManager
  Configuration mConfiguration;
  public Configuration getConfiguration () {
    return mConfiguration;
  }
  public void setConfiguration (Configuration pConfiguration) {
    if (mConfiguration != pConfiguration) {
      mConfiguration = pConfiguration;
    }
  }

  //-------------------------------------
  // methods
  //-------------------------------------

  public void preConfirm(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws javax.servlet.ServletException, java.io.IOException {
    /** Overriding this atg.portal.framework.GearConfigFormHandler method*/

    resetFormExceptions();
    mGearEnv = getGearEnvironment(pRequest, pResponse);
    if(getParamType().equals("instance")){
      String sharedUrl = getInstanceSharedURL();
      String fullUrl = getInstanceFullURL();
      String resourceBundle = getResourceBundleName();
      String fullLinkText = getFullModeLinkText();
      ResourceBundle currentBundle =  getCurrentResourceBundle(pRequest, pResponse);
      boolean sharedOK   = checkURL(sharedUrl,currentBundle);
      boolean fullOK     = checkURL(fullUrl,currentBundle);
      boolean fullTextOK = checkFullText(fullLinkText,fullUrl,currentBundle);
      boolean resourceOK = checkResourceName(resourceBundle,currentBundle);
      if(getFormError())
        setRollbackTransaction(true); 
      else {
        saveOldValues();
        Map map = getValues();
        map.put(getConfiguration().getInstanceSharedURLParameterName() , sharedUrl);
        map.put(getConfiguration().getInstanceFullURLParameterName() , fullUrl);
        map.put(getConfiguration().getFullModeLinkTextParameterName() , fullLinkText);
        map.put(getConfiguration().getResourceBundleNameParameterName() , resourceBundle);
        //set the map to the values so that the super class can update or add the values in the db
        setValues(map);
    }
  }
}

  /**
  * Sends alerts
  */

  public void postConfirm(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws javax.servlet.ServletException, java.io.IOException {
    sendAlerts(pRequest, pResponse); 
  }

  /**
   * Sends Alerts 
   */
  
  protected void sendAlerts(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
  {
    if(!getConfiguration().isEnableSendAlerts()) return;
      try{
      GearEnvironment gearEnv =  getGearEnvironment(pRequest, pResponse);
      if(mOldSharedURL !=null && !mOldSharedURL.equals(getInstanceSharedURL())){
        // send the SharedModeURLChangedMessage  
        SharedModeURLChangedMessage message =  new SharedModeURLChangedMessage(mGearEnv);
        message.setGearId(gearEnv.getGear().getId());
        message.setGearName(gearEnv.getGear().getName());
        if(gearEnv.getCommunity()!=null){
          message.setCommunityName(gearEnv.getCommunity().getName());
          message.setCommunityId(gearEnv.getCommunity().getId());
        }
        message.setOldURL(mOldSharedURL);
        message.setNewURL(getInstanceSharedURL());
        getPublisher().writeMessage(message);
       }
      if(mOldFullURL != null && !mOldFullURL.equals(getInstanceFullURL())){
         // send the FullModeURLChangedMessgae     
        FullModeURLChangedMessage message =  new FullModeURLChangedMessage(mGearEnv);
        message.setGearId(gearEnv.getGear().getId());
        message.setGearName(gearEnv.getGear().getName());
        if(gearEnv.getCommunity()!=null){
          message.setCommunityName(gearEnv.getCommunity().getName());
          message.setCommunityId(gearEnv.getCommunity().getId());
        }
        message.setOldURL(mOldFullURL);
        message.setNewURL(getInstanceFullURL());
        getPublisher().writeMessage(message);
      }
      }
      catch (JMSException jmse) {
        if(isLoggingError())
          logError(jmse);
        } 
  }


 
  private void saveOldValues(){
    mOldSharedURL =mGearEnv.getGearInstanceParameter(getConfiguration().getInstanceSharedURLParameterName());
    mOldFullURL = mGearEnv.getGearInstanceParameter(getConfiguration().getInstanceFullURLParameterName());
  }

/**
 * Validates URL, adds form exception if found invalid.
 */
 
  private boolean checkURL(String pUrl, ResourceBundle pCurrentBundle){
   if(pUrl==null || pUrl.trim().equals("")) return true;

    if(!pUrl.trim().toLowerCase().startsWith("http://")){
      addFormException(new DropletException(MessageFormat.format
        (pCurrentBundle.getString(INVALID_HTTP_URL),
         new Object [] { pUrl})));
      return false;
    }
    
    try{
      URL url = new URL(pUrl);
       }
     catch(MalformedURLException ex){
      addFormException(new DropletException(MessageFormat.format
        (pCurrentBundle.getString(INVALID_URL),
         new Object [] { pUrl}),ex));
      return false;
     } 
    
    return true;
}

/**
 * Checks FullModeLinkText is provided if a FullModeUrl is speicified on the form.
 */

 private boolean checkFullText(String pText, String pFullURL, ResourceBundle pCurrentBundle){
    if(pFullURL==null || pFullURL.trim().equals("")) return true;
    
    if(pText==null || pText.trim().equals("")) {
     addFormException(new DropletException(pCurrentBundle.getString(LINK_TEXT_MUST_BE_SPECIFIED)));
      return false;
   }
   return true;
 
 }


/**
 * Checks to see if the resource bundle name given exists in the classpath
 * This check verifies that the class is available in Dynamo classpath
 * and the J2EE classpath. 
 * However, the resource bundle should always be put in the Dynamo classpath 
 * so that it is accesible to both Dynamo components and the JSP tag classes.
 */

  private boolean checkResourceName(String pResourceName, ResourceBundle pCurrentBundle){
    if(pResourceName==null || pResourceName.trim().equals("")){
      addFormException(new DropletException(pCurrentBundle.getString(RESOURCE_BUNDLE_MUST_BE_ENTERED)));
      return false;   
   }
   try{
      java.util.ResourceBundle.getBundle(pResourceName);
    }
    catch (MissingResourceException exc) {
      addFormException(new DropletException(MessageFormat.format
        (pCurrentBundle.getString(RESOURCE_BUNDLE_NOT_FOUND),
         new Object [] { pResourceName}),exc));
      return false;

   }

  return true;
  }

  /**
  * Get the currently configured resource bundle from the Instance
  * config paramater for displaying error messsges 
  */
  
  protected  ResourceBundle getCurrentResourceBundle(DynamoHttpServletRequest pRequest,
                                                     DynamoHttpServletResponse pResponse)
  {
    String resourceBundleParamName= "";
    String resourceBundleName= "";
    ResourceBundle bundle=null;
    try {
      GearEnvironment gearEnv = getGearEnvironment(pRequest, pResponse);
      resourceBundleParamName = getConfiguration().getResourceBundleNameParameterName();
      resourceBundleName = gearEnv.getGearInstanceParameter(resourceBundleParamName);
      if(pRequest.getRequestLocale()!=null)
         bundle = ResourceUtils.getBundle(resourceBundleName, pRequest.getRequestLocale().getLocale());
      else 
         bundle = ResourceUtils.getBundle(resourceBundleName);
    }
    catch (MissingResourceException exc) {
      if (isLoggingError())
	      logError(exc);
    }
    
    return bundle;
    }

  
/**
 * Overriding this method to set the gear environment.
 */

  public void beforeGet(DynamoHttpServletRequest pRequest, 
  		        DynamoHttpServletResponse pResponse)
  {
    mGearEnv = getGearEnvironment(pRequest, pResponse);
    super.beforeGet(pRequest,pResponse);
    
   }
  
  //-------------------------------------
  // Constructors
  //-------------------------------------
  public ScreenScraperGearConfigFormHandler ()
  {
  }

}
