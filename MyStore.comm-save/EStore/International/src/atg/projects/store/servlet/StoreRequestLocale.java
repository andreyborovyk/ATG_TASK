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
package atg.projects.store.servlet;

import java.util.Locale;

import javax.servlet.http.Cookie;

import atg.core.util.StringUtils;
import atg.multisite.Site;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileRequestLocale;
import java.util.List;

/**
 * The extensions to ootb RequestLocale.
 *
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/src/atg/projects/store/servlet/StoreRequestLocale.java#3 $
*/
public class StoreRequestLocale extends ProfileRequestLocale {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/src/atg/projects/store/servlet/StoreRequestLocale.java#3 $$Change: 635816 $";



  //-------------------------------
  // Constants
  //-------------------------------
  public static final String USER_PREF_LANGUAGE_COOKIE_NAME = "userPrefLanguage";
  public static final String LANG_SELECTION_PARAMETER = "locale";
  public static final String PROFILE_LOCALE_UNSET_VALUE = "unset";
  
  /**
   * Site's default country attribute name .
   */
  public static String DEFAULT_COUNTRY_ATTRIBUTE_NAME = "defaultCountry";
  
  /**
   * Site's default language attribute name .
   */
  public static String DEFAULT_LANGUAGE_ATTRIBUTE_NAME = "defaultLanguage";

  /**
   *
   */
  public static String LANGUAGES_ATTRIBUTE_NAME = "languages";

  
  private static final boolean debug = false;

  //-------------------------------
  // Properties
  //-------------------------------

  //-------------------------------
  // property: Logger
  private static ApplicationLogging mLogger =
    ClassLoggingFactory.getFactory().getLoggerForClass(StoreRequestLocale.class);

  /**
   * @return ApplicationLogging object for logger.
   */
  private ApplicationLogging getLogger()  {
    return mLogger;
  }

  /**
   * Obtains locale from http request.
   *
   * @param pRequest DynamoHttpServletRequest
   * @param pReqLocal Request locale
   * @return Locale object
   */
  public Locale discernRequestLocale(DynamoHttpServletRequest pRequest, RequestLocale pReqLocal) {

    if (getLogger().isLoggingDebug())
      getLogger().logDebug("StoreRequestLocale:discernRequestLocale:Entry");

    Locale locale;

    if(getLogger().isLoggingDebug())
      getLogger().logDebug("StoreRequestLocale:discernRequestLocale:initLocale from LangSelection");

    locale = fillLocaleFromLangSelection(pRequest);
    
    if(locale==null) {
      if(getLogger().isLoggingDebug())
        getLogger().logDebug("StoreRequestLocale:discernRequestLocale:initLocale from storeSelection");
  
      locale = fillLocaleFromStoreSelection(pRequest,pReqLocal);
    }
    
    if(locale==null) {
      if(getLogger().isLoggingDebug())
         getLogger().logDebug("StoreRequestLocale:discernRequestLocale:initLocale from Profile");

       locale = fillLocaleFromProfile(pRequest,pReqLocal);
    }

    if(locale==null) {
      if(getLogger().isLoggingDebug())
        getLogger().logDebug("StoreRequestLocale:discernRequestLocale:initLocale from UserPrefLang");

      locale = fillLocaleFromUserPrefLang(pRequest);
    }

    if(locale==null) {
      if(getLogger().isLoggingDebug())
        getLogger().logDebug("StoreRequestLocale:discernRequestLocale:initLocale from super.discernRequestLocale(pRequest,pReqLocal");

      locale = super.discernRequestLocale(pRequest,pReqLocal);
    }

    if(getLogger().isLoggingDebug()) {
      getLogger().logDebug("StoreRequestLocale:discernRequestLocale:locale="+locale);
      getLogger().logDebug("StoreRequestLocale:discernRequestLocale:Exit");
    }

    return locale;
  }

  /**
   * @param pRequest DynamoHttpServletRequest object.
   * @return Locale based on language selection.
   */
  protected Locale fillLocaleFromLangSelection(DynamoHttpServletRequest pRequest) {

    if(getLogger().isLoggingDebug())
      getLogger().logDebug("StoreRequestLocale:discernRequestLocale:fillLocaleFromLangSelection:Entry");

    Locale locale = null;

    //langSelection parameter set by Affiliates and laguage dropdown
    String langSelection = pRequest.getParameter(LANG_SELECTION_PARAMETER);
    if(langSelection!=null && !langSelection.trim().equals("")){
      //init locale using langSelection
      //update/create persistent cookie "userPrefLang"
      //update profile.locale if different

      locale = RequestLocale.getCachedLocale(langSelection);
      createUserPrefLangCookie(pRequest,langSelection);
      updateProfileLocale(pRequest,locale);
    }
    if(getLogger().isLoggingDebug()) {
      getLogger().logDebug("StoreRequestLocale:fillLocaleFromLangSelection:locale="+locale);
      getLogger().logDebug("StoreRequestLocale:fillLocaleFromLangSelection:Exit");
    }
    return locale;
  }

  /**
   * @param pRequest DynamoHttpServletRequest object.
   * @param pReqLocal Request locale
   * @return Locale based on store selection.
   */
  protected Locale fillLocaleFromStoreSelection(DynamoHttpServletRequest pRequest,RequestLocale pReqLocal) {

    if(getLogger().isLoggingDebug())
      getLogger().logDebug("StoreRequestLocale:discernRequestLocale:fillLocaleFromStoreSelection:Entry");

    Locale locale = null;
    
    String  storeLocaleCode = getStoreLocaleCode(pRequest,pReqLocal);

    if(!StringUtils.isEmpty(storeLocaleCode)) {
      if(getLogger().isLoggingDebug())
        getLogger().logDebug("StoreRequestLocale:discernRequestLocale:fillLocaleFromStore:storeDefaultLanguage is not null="+storeLocaleCode);
  
      //init locale using storeDefaultLanguage
      //create persistent cookie "userPrefLang" to storeDefaultLanguage
      //update profile.local if different
  
      locale = RequestLocale.getCachedLocale(storeLocaleCode);
      createUserPrefLangCookie(pRequest,storeLocaleCode);
      updateProfileLocale(pRequest,locale);
    }
    else {
      if(getLogger().isLoggingDebug())
        getLogger().logDebug("StoreRequestLocale:discernRequestLocale:fillLocaleFromStore:storeDefaultLanguage is null.");
    }
    
    if(getLogger().isLoggingDebug()) {
      getLogger().logDebug("StoreRequestLocale:fillLocaleFromStoreSelection:locale="+locale);
      getLogger().logDebug("StoreRequestLocale:fillLocaleFromStoreSelection:Exit");
    }
    return locale;
  }

  /**
   * Determines the locale code to use for the store.
   * If the current user profile language is in the list of site languages then the locale code
   * is constructed from the profile language & site default country code; otherwise if the user
   * profile language is not in the list of site languages then the locale code is constructed
   * from the site default language & site default country code.
   *
   * @param pRequest DynamoHttpServletRequest object.
   * @param pReqLocal Request locale
   * @return Locale Code
   */
  private String getStoreLocaleCode(DynamoHttpServletRequest pRequest,RequestLocale pReqLocal) {
    String storeLocaleCode = "";

    List storeLanguages = null;
    String storeDefaultLanguage = "";
    String storeDefaultCountry = "";

    Site site = null;
    SiteContext currentSiteContext = null;

    String profileLanguage = "";
    Locale profileLocale = null;


    currentSiteContext = SiteContextManager.getCurrentSiteContext();
    if(currentSiteContext!=null) {
      site = currentSiteContext.getSite();

      if(site!=null) {
        storeLanguages = (List)site.getPropertyValue(LANGUAGES_ATTRIBUTE_NAME);
        storeDefaultLanguage = (String)site.getPropertyValue(DEFAULT_LANGUAGE_ATTRIBUTE_NAME);
        storeDefaultCountry = (String)site.getPropertyValue(DEFAULT_COUNTRY_ATTRIBUTE_NAME);

        profileLocale = localeFromProfileAttribute(pRequest,pReqLocal);
        if(profileLocale!=null) {
          profileLanguage = profileLocale.getLanguage();

          // Create locale code from the current profile language and site default country code
          if (storeLanguages!=null && storeLanguages.contains(profileLanguage)) {
            storeLocaleCode = profileLanguage+"_"+storeDefaultCountry;
          }
        }

        if(StringUtils.isEmpty(storeLocaleCode)) {
          // Create locale code from the site default language and country code
          if(!StringUtils.isEmpty(storeDefaultLanguage) && !StringUtils.isEmpty(storeDefaultCountry)) {
            storeLocaleCode = storeDefaultLanguage+"_"+storeDefaultCountry;
          }
        }
      }
    }

    return storeLocaleCode;
  }

  /**
   * @param pRequest DynamoHttpServletRequest object.
   * @return Locale based on user preffered language.
   */
  protected Locale fillLocaleFromUserPrefLang(DynamoHttpServletRequest pRequest) {

    if(getLogger().isLoggingDebug()) {
      getLogger().logDebug("StoreRequestLocale:discernRequestLocale:fillLocaleFromUserPrefLang:Entry");
    }
    Locale locale = null;

    String userPrefLang = pRequest.getCookieParameter(USER_PREF_LANGUAGE_COOKIE_NAME);
    if(userPrefLang!=null && !userPrefLang.trim().equals("")) {
      //init locale using userPrefLang
      //update profile.locale if different

      locale = RequestLocale.getCachedLocale(userPrefLang);
      updateProfileLocale(pRequest,locale);
    }
    if(getLogger().isLoggingDebug()) {
      getLogger().logDebug("StoreRequestLocale:fillLocaleFromUserPrefLang:locale="+locale);
      getLogger().logDebug("StoreRequestLocale:fillLocaleFromUserPrefLang:Exit");
    }
    return locale;
  }

  /**
   * @param pRequest DynamoHttpServletRequest object.
   * @param pReqLocal RequestLocale object.
   * @return Locale based on profile.
   */
  protected Locale fillLocaleFromProfile(DynamoHttpServletRequest pRequest,RequestLocale pReqLocal) {

    if(getLogger().isLoggingDebug())
      getLogger().logDebug("StoreRequestLocale:discernRequestLocale:fillLocaleFromProfile:Entry");

    Locale locale = null;

    //Retrieve from Profile
    Locale profileLocale = localeFromProfileAttribute(pRequest,pReqLocal);
    if(profileLocale!=null && !PROFILE_LOCALE_UNSET_VALUE.equals(profileLocale.toString())) {
      locale = RequestLocale.getCachedLocale(profileLocale.toString());
      createUserPrefLangCookie(pRequest,profileLocale.toString());
    }

    if(getLogger().isLoggingDebug()) {
      getLogger().logDebug("StoreRequestLocale:fillLocaleFromProfile:locale="+locale);
      getLogger().logDebug("StoreRequestLocale:fillLocaleFromProfile:Exit");
    }
    return locale;
  }

 
  /**
   * This operation creates cookie for user preffered language.
   * @param pRequest DynamoHttpServletRequest object.
   * @param pLanguage User preferred language.
   * 
   */
  protected void createUserPrefLangCookie(DynamoHttpServletRequest pRequest,String pLanguage) {
    if(getLogger().isLoggingDebug()){
      getLogger().logDebug("StoreRequestLocale:createUserPrefLangCookie:Entry");
      getLogger().logDebug("StoreRequestLocale:createUserPrefLangCookie=pLanguage"+pLanguage);
    }
    Cookie userPrefLanguageCookie = new Cookie(USER_PREF_LANGUAGE_COOKIE_NAME,pLanguage);
    userPrefLanguageCookie.setMaxAge(1567800000);//50*365*24*60*60 - 50 years
    userPrefLanguageCookie.setPath(pRequest.getContextPath() + "/");
    DynamoHttpServletResponse response= atg.servlet.ServletUtil.getDynamoResponse(pRequest,pRequest.getResponse());
    if(getLogger().isLoggingDebug()) {
      getLogger().logDebug("StoreRequestLocale:createUserPrefLangCookie:userPrefLanguageCookie="+userPrefLanguageCookie);
      getLogger().logDebug("StoreRequestLocale:createUserPrefLangCookie=response.isCommitted():Before"+response.isCommitted());
    }
    response.addCookie(userPrefLanguageCookie);
    if(getLogger().isLoggingDebug())
      getLogger().logDebug("StoreRequestLocale:createUserPrefLangCookie=response.isCommitted():After"+response.isCommitted());

    if(getLogger().isLoggingDebug())
      getLogger().logDebug("StoreRequestLocale:createUserPrefLangCookie:Exit");
  }

  /**
   * Updates locale in profile.
   * @param pRequest DynamoHttpServletRequest object.
   * @param locale Profile Locale.
   */
  protected void updateProfileLocale(DynamoHttpServletRequest pRequest,Locale locale) {

    if(getLogger().isLoggingDebug())
      getLogger().logDebug("StoreRequestLocale:updateProfileLocale:Entry");

    String localeString = locale!=null?locale.toString():null;
    if(getLogger().isLoggingDebug())
      getLogger().logDebug("StoreRequestLocale:updateProfileLocale=localString"+localeString);

    if(getProfilePath()!=null) {
      Profile profile = (Profile)pRequest.resolveName(getProfilePath(),true);
      if(profile!=null && getProfileAttributeName()!=null) {
        String profileLanguage = (String)profile.getPropertyValue(getProfileAttributeName());
        if(getLogger().isLoggingDebug())
          getLogger().logDebug("StoreRequestLocale:updateProfileLocale:profileLanguage="+profileLanguage);

        if(localeString!=null && !localeString.equalsIgnoreCase(profileLanguage)) {
          if(getLogger().isLoggingDebug())
            getLogger().logDebug("StoreRequestLocale:updateProfileLocale:updating localeString="+localeString);

          profile.setPropertyValue(getProfileAttributeName(),localeString);
        }
        else if(locale==null) {
          if(getLogger().isLoggingDebug())
            getLogger().logDebug("StoreRequestLocale:updateProfileLocale:updating locale is null so set it to null");

          profile.setPropertyValue(getProfileAttributeName(),null);
        }
      }
    }
    if(getLogger().isLoggingDebug())
      getLogger().logDebug("StoreRequestLocale:updateProfileLocale:Exit");
  }
}

