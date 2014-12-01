/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2011 Art Technology Group, Inc.
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

import atg.commerce.util.PlaceList;

import atg.nucleus.naming.ParameterName;

import atg.projects.store.logging.LogUtils;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;

import java.util.*;

import javax.servlet.ServletException;


/**
 * <p>
 * This droplet country returns a list of countries filtered on the permitted/restricted
 * country codes configured in the corresponding component.
 * <p>
 * This droplet takes the following optional input parameters:
 * <ul>
 * <li>countryCode
 * </ul>
 * <p>
 * This droplet renders the following oparams:
 * <ul>
 * If parameter countryCode is not null, then true or false
 * <li>true: countryCode is restricted
 * <li>false:countryCode is permitted
 * <li>
 * If parameter countryCode is null
 * <li>output -
 * </ul>
 * <p>
 * This droplet sets the following output parameters:
 * <ul>
 * <li>countries - filtered list of countries.
 * </ul>
 * <p>
 * <p>
 * @author ATG
 * @updated 21st August 2006
 * @version 1.0
 * </p>
 */
public class CountryRestrictionsDroplet extends DynamoServlet {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/droplet/CountryRestrictionsDroplet.java#2 $$Change: 651448 $";


  /**
   * Resource bundle name.
   */
  private static final String RESOURCE_BUNDLE_NAME = "atg.commerce.util.CountryStateResources";

  /**
   * Country code parameter name.
   */
  private static final ParameterName COUNTRY_CODE = ParameterName.getParameterName("countryCode");

  /**
   * User locale parameter name.
   */
  private static final ParameterName USER_LOCALE = ParameterName.getParameterName("userLocale");

  /**
   * Countries parameter name.
   */
  private static final String COUNTRIES_PARAM = "countries";

  /**
   * Country details parameter name.
   */
  private static final String COUNTRY_DETAIL_PARAM = "countryDetail";

  // oparams
  /**
   * Oparam: output.
   */
  public static final String OUTPUT_OPARAM = "output";

  /**
   * Oparam: true.
   */
  private static final String TRUE_OPARAM = "true";

  /**
   * Oparam: false.
   */
  private static final String FALSE_OPARAM = "false";

  //public static final String EMPTY_OPARAM = "empty";
  /**
   * Master country list.
   */
  private PlaceList mMasterCountryList;

  /**
   * Permitted country codes.
   */
  private List mPermittedCountryCodes;

  /**
   * Restricted country codes.
   */
  private List mRestrictedCountryCodes;

  /**
   * @return master country list.
   */
  public PlaceList getMasterCountryList() {
    return mMasterCountryList;
  }

  /**
   * @param pMasterCountryList - master country list.
   */
  public void setMasterCountryList(PlaceList pMasterCountryList) {
    mMasterCountryList = pMasterCountryList;
  }

  /**
   * @return permitted country list.
   */
  public List getPermittedCountryCodes() {
    return mPermittedCountryCodes;
  }

  /**
   * @param pPermittedCountryCodes - permitted country list.
   */
  public void setPermittedCountryCodes(List pPermittedCountryCodes) {
    mPermittedCountryCodes = pPermittedCountryCodes;

    if (isLoggingDebug()) {
      logDebug(LogUtils.formatMajor("++++++inside setPermittedCountryCodes+++++++"));

      if (pPermittedCountryCodes != null) {
        Iterator it = pPermittedCountryCodes.iterator();

        while (it.hasNext()) {
          String code = (String) it.next();
          logDebug(LogUtils.formatMajor("code = " + code));
        }
      }

      logDebug(LogUtils.formatMajor("++++++exiting setPermittedCountryCodes+++++++"));
    }
  }

  /**
   * @return restricted country codes.
   */
  public List getRestrictedCountryCodes() {
    return mRestrictedCountryCodes;
  }

  /**
   * @param pRestrictedCountryCodes - restricted country codes.
   */
  public void setRestrictedCountryCodes(List pRestrictedCountryCodes) {
    mRestrictedCountryCodes = pRestrictedCountryCodes;

    if (isLoggingDebug()) {
      logDebug(LogUtils.formatMajor("++++++inside setRestrictedCountryCodes+++++++"));

      if (pRestrictedCountryCodes != null) {
        Iterator it = pRestrictedCountryCodes.iterator();

        while (it.hasNext()) {
          String code = (String) it.next();
          logDebug(LogUtils.formatMajor("code = " + code));
        }
      }

      logDebug(LogUtils.formatMajor("++++++exiting setRestrictedCountryCodes+++++++"));
    }
  }

  /**
   * Check if country code is permitted.
   *
   * @param pResultCountryList - results country list
   * @param pCountryCode - country code
   *
   * @return true if pCountryCode is permitted, otherwise - false
   */
  public boolean isPermittedCountry(ArrayList pResultCountryList, String pCountryCode) {
    if ((pResultCountryList == null) || (pCountryCode == null)) {
      if (isLoggingError()) {
        logError("Either CountryList or CountryCode is null");
      }

      return false;
    }

    Iterator iterResultCountryLists = pResultCountryList.iterator();

    while (iterResultCountryLists.hasNext()) {
      PlaceList.Place places = (PlaceList.Place) iterResultCountryLists.next();

      if (pCountryCode.equals(places.getCode())) {
        return true;
      }
    }

    return false;
  }

  /**
   * Service method.
   *
   * @param pRequest - http request
   * @param pResponse - http response
   *
   * @throws ServletException if an error occurs
   * @throws IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    List listCountryCds = null;
    ArrayList resultCountryList = null; //Place[]
    ResourceBundle sResourceBundle = null;

    Locale locale = (Locale) pRequest.getObjectParameter(USER_LOCALE);

    if (isLoggingDebug()) {
      logDebug(LogUtils.formatMajor("CountryRestrictionsDroplet::user locale is -> " + locale));
    }

    try {
      if (locale == null) {
        sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);
      } else {
        sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, locale);
      }
    } catch (MissingResourceException ex) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Missing resource bundle -> " + RESOURCE_BUNDLE_NAME + ex.getMessage()));
      }

      sResourceBundle = null;
    }

    listCountryCds = getPermittedCountryCodes();

    if ((listCountryCds != null) && !(listCountryCds.isEmpty())) { //PermittedCountryCodes are specified
      resultCountryList = new ArrayList(listCountryCds.size());

      Iterator iterator = listCountryCds.iterator();

      while (iterator.hasNext()) {
        String code = (String) iterator.next();
        PlaceList.Place place = getMasterCountryList().getPlaceForCode(code);
        resultCountryList.add(new PlaceList.Place(code,
            getDisplayNameForCode(sResourceBundle, code, place.getDisplayName())));
      }
    } else { //RestrictedCountryCodes are specified
      listCountryCds = getRestrictedCountryCodes();

      if ((listCountryCds != null) && !(listCountryCds.isEmpty())) {
        int len = 0;
        PlaceList.Place[] places = getMasterCountryList().getPlaces();

        if (places != null) {
          resultCountryList = new ArrayList(Arrays.asList(places));

          Iterator iterator = listCountryCds.iterator();
          ArrayList codesToDelete = new ArrayList(listCountryCds.size());

          while (iterator.hasNext()) {
            String code = (String) iterator.next();
            codesToDelete.add(code);
          }

          resultCountryList = removeCountryWithCode(sResourceBundle, codesToDelete, resultCountryList);
        }
      } else { // DEFAULT - ALL

        PlaceList.Place[] places = getMasterCountryList().getPlaces();

        if (places != null) {
          PlaceList.Place[] localizedPlaces = new PlaceList.Place[places.length];

          for (int i = 0; i < places.length; i++) {
            String code = places[i].getCode();
            String localizedDisplayName = getDisplayNameForCode(sResourceBundle, code, places[i].getDisplayName());
            localizedPlaces[i] = new PlaceList.Place(code, localizedDisplayName);
          }

          resultCountryList = new ArrayList(Arrays.asList(localizedPlaces));
          places = null;
          localizedPlaces = null;
        }
      }
    } //END RestrictedCountryCodes are specified

    String countryCode = pRequest.getParameter(COUNTRY_CODE);

    if (countryCode != null) { // checks if mCountryCode is in the list of permitted countries

      if ((resultCountryList != null) && isPermittedCountry(resultCountryList, countryCode)) {
        pRequest.setParameter(COUNTRY_DETAIL_PARAM,
          new PlaceList.Place(countryCode, getDisplayNameForCode(sResourceBundle, countryCode, "")));
        pRequest.serviceLocalParameter(FALSE_OPARAM, pRequest, pResponse);
      } else {
        pRequest.setParameter(COUNTRY_DETAIL_PARAM,
          new PlaceList.Place(countryCode, getDisplayNameForCode(sResourceBundle, countryCode, "")));
        pRequest.serviceLocalParameter(TRUE_OPARAM, pRequest, pResponse);
      }
    } else {
      pRequest.setParameter(COUNTRIES_PARAM, resultCountryList);
      pRequest.serviceLocalParameter(OUTPUT_OPARAM, pRequest, pResponse);

      if (isLoggingDebug()) {
        logDebug(LogUtils.formatMajor("++++++inside CountryRestrictionDroplet.service+++++++"));

        Iterator ite = resultCountryList.iterator();

        while (ite.hasNext()) {
          PlaceList.Place pl = (PlaceList.Place) ite.next();
          logDebug(LogUtils.formatMajor("code = " + pl.getCode() + " : " + pl.getDisplayName()));
        }

        logDebug(LogUtils.formatMajor("++++++exiting CountryRestrictionDroplet.service+++++++"));
      }
    }
  }

  /**
   * This method removes the country codes (<i>codesToDelete</i>) from the country list (<i>countryList</i>)
   * and returns the resultant list having localized country names.
   * @param pResourceBundle ResourceBundle Resource Bundle used for country names localization.
   * @param codesToDelete ArrayList Country codes to delete.
   * @param countryList ArrayList List of countries.
   * @return  ArrayList List of countries with desired country codes and their localized country names.
   */
  private ArrayList removeCountryWithCode(ResourceBundle pResourceBundle, ArrayList codesToDelete, ArrayList countryList) {
    ArrayList returnList = new ArrayList(countryList.size());
    Iterator it = countryList.iterator();

    while (it.hasNext()) {
      PlaceList.Place country = (PlaceList.Place) it.next();

      if (!(codesToDelete.contains(country.getCode()))) {
        String localeDisplayName = getDisplayNameForCode(pResourceBundle, country.getCode(), country.getDisplayName());
        returnList.add(new PlaceList.Place(country.getCode(), localeDisplayName));

        if (isLoggingDebug()) {
          logDebug(LogUtils.formatMajor("Country removed from list:" + country.getCode()));
        }
      }
    }

    returnList.trimToSize();

    return returnList;
  }

  /**
   * This method returns the localized display name for the country code provided as input.
   * @param pResourceBundle ResourceBundle Resource Bundle used for country name localization.
   * @param pCode String Country code.
   * @param pDefault String Default value if localized country name is not found.
   * @return  String localized country name or the default valie if localized country name is not found.
   */
  private String getDisplayNameForCode(ResourceBundle pResourceBundle, String pCode, String pDefault) {
    String returnString = pDefault;

    if ((pResourceBundle != null) && (pCode != null)) {
      try {
        returnString = pResourceBundle.getString("CountryCode." + pCode);
      } catch (MissingResourceException ex) {
      }
    }

    return returnString;
  }

  /**
   * This static method gets localized country name from the Resource Bundle based on the input locale.
   * (Provided for ShippingGroupFormHandler)
   * @param pCountryCode String Country Code.
   * @param pUserLocale Locale user locale.
   * @return String localized country name.
   */
  public static String getCountryName(String pCountryCode, Locale pUserLocale) {
    ResourceBundle sResourceBundle = null;
    String returnString = pCountryCode;

    try {
      if (pUserLocale == null) {
        sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);
      } else {
        sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, pUserLocale);
      }

      if ((sResourceBundle != null) && (pCountryCode != null)) {
        returnString = sResourceBundle.getString("CountryCode." + pCountryCode);
      }
    } catch (MissingResourceException ex) {
    }

    return returnString;
  }
}
