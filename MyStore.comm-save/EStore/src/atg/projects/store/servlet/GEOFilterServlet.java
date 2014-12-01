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

import atg.core.util.StringUtils;

import atg.projects.store.logging.LogUtils;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import atg.servlet.pipeline.InsertableServletImpl;

import java.io.IOException;

import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletException;


/**
 * This servlet performs rudimentary GEOFiltering functionality by checking the
 * request's locale and rerouting the user's destination based upon that.
 *
 * @author ATG
 * @version $Revision: #3 $
 */
public class GEOFilterServlet extends InsertableServletImpl {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/servlet/GEOFilterServlet.java#3 $$Change: 635816 $";

  /**
   * If true, this servlet will perform GEOFiltering If false, it will simply
   * pass all requests through untouched.
   */
  private boolean mEnabled = true;

  /**
   * Stores a map of country codes to redirect URLs.
   */
  private Properties mCountryRedirectURLs;

  /**
   * Default URL to redirect to.
   */
  private String mDefaultRedirectURL;

  /**
   * Returns the current "enabled" state of the servlet. If true, GEOFiltering
   * is enabledd.  If false, no GEOFiltering is done.
   *
   * @beaninfo description: If true, specified IP addresses are being blocked.
   * @return true if enabled, false - otherwise
   */
  public boolean isEnabled() {
    return mEnabled;
  }

  /**
   * Changes the current "enabled" state of the servlet. If true, GEOFiltering
   * is enabledd. If false, no GEOFiltering is done.
   * @param pEnabled - true to enable, false - otherwise
   */
  public void setEnabled(boolean pEnabled) {
    mEnabled = pEnabled;
  }

  /**
   * Stores a map of country codes to redirect URLs.
   *
   * @param pCountryRedirectURLs - country redirect URLs
   */
  public void setCountryRedirectURLs(Properties pCountryRedirectURLs) {
    mCountryRedirectURLs = pCountryRedirectURLs;
  }

  /**
   * Stores a map of country codes to redirect URLs.
   *
   * @return country redirect URLs
   */
  public Properties getCountryRedirectURLs() {
    return mCountryRedirectURLs;
  }

  /**
   * Default URL to redirect to if the country code is not the US and if the
   * country code does not have an explicit redirect location set in
   * <code>countryRedirectURLs</code>.
   *
   * @param pDefaultRedirectURL - default redirect URL
   */
  public void setDefaultRedirectURL(String pDefaultRedirectURL) {
    mDefaultRedirectURL = pDefaultRedirectURL;
  }

  /**
   * Default URL to redirect to if the country code is not the US and if the
   * country code does not have an explicit redirect location set in
   * <code>countryRedirectURLs</code>.
   *
   * @return default redirect URL
   */
  public String getDefaultRedirectURL() {
    return mDefaultRedirectURL;
  }

  /**
   * Look at the locale header in the request and determine if we need to
   * redirect based upon that. This servlet makes the locale determination
   * based <b>purely </b> on the Accept-Language header. This is the preferred
   * locale as set by the client.
   * <p>
   * If the locale country is not US then redirect according to the url
   * fetched from the <code>countryRedirectURLs</code>. If the country does
   * not have a URL mapped to it, redirect to the
   * <code>defaultRedirectURL</code>.
   *
   * @param pRequest - http request
   * @param pResponse - http response
   * @throws IOException if IO error occurs
   * @throws ServletException if servlet error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws IOException, ServletException {
    // easy disable switch
    if (!mEnabled) {
      super.service(pRequest, pResponse);

      return;
    }

    // find out where they are coming from
    // This locale is based purely upon the Accept-Language header
    Locale clientLocale = pRequest.getLocale();

    // debug the locale
    if (isLoggingDebug()) {
      superDebug(clientLocale, true);
    }

    // get the country code
    String countryCode = clientLocale.getCountry();

    // if no country code just let them through
    if (StringUtils.isEmpty(countryCode)) {
      if (isLoggingDebug()) {
        logDebug("Country Code is empty, just letting them through");
      }

      super.service(pRequest, pResponse);

      return;
    } else if (countryCode.equals("US")) {
      // US customer. Let them in
      super.service(pRequest, pResponse);

      return;
    } else {
      String redirectURL = null;

      // see if the country code is in the map
      Properties redirects = getCountryRedirectURLs();

      if (redirects != null) {
        redirectURL = (String) redirects.get(countryCode);
      }

      // if we don't have one yet, use the default
      if (StringUtils.isEmpty(redirectURL)) {
        redirectURL = getDefaultRedirectURL();
      }

      if (!StringUtils.isEmpty(redirectURL)) {
        if (isLoggingDebug()) {
          logDebug("Redirecting to " + redirectURL);
        }

        pResponse.sendRedirect(redirectURL);

        return;
      } else {
        if (isLoggingError()) {
          logError(LogUtils.formatMajor("No redirect url found for country " + countryCode + " - allowing site access"));
        }
      }
    }

    super.service(pRequest, pResponse);
  }

  /**
   * Massive debugging information on the locale.
   *
   * @param pLocale - the locale to debug
   * @param pOmitDisplayOfAll - omits the display of all available locales
   */
  private void superDebug(Locale pLocale, boolean pOmitDisplayOfAll) {
    if (isLoggingDebug()) {
      StringBuffer buf = new StringBuffer();

      buf.append("\n");
      buf.append("country = " + pLocale.getCountry()).append("\n");
      buf.append("displayCountry = " + pLocale.getDisplayCountry()).append("\n");
      buf.append("displayLanguage = " + pLocale.getDisplayLanguage()).append("\n");
      buf.append("displayName = " + pLocale.getDisplayName()).append("\n");
      buf.append("displayVariant = " + pLocale.getDisplayVariant()).append("\n");
      buf.append("ISO3Country = " + pLocale.getISO3Country()).append("\n");
      buf.append("ISO3Language = " + pLocale.getISO3Language()).append("\n");
      buf.append("language = " + pLocale.getLanguage()).append("\n");
      buf.append("variant = " + pLocale.getVariant()).append("\n");
      buf.append("toString = " + pLocale.toString()).append("\n");

      if (!pOmitDisplayOfAll) {
        buf.append("Interesting constants = ").append("\n");
        buf.append("US = " + Locale.US).append("\n");
        buf.append("UK = " + Locale.UK).append("\n");
        buf.append("CAN = " + Locale.CANADA).append("\n");
        buf.append("FR CAN = " + Locale.CANADA_FRENCH).append("\n");
        buf.append("\n");

        buf.append("\n").append("All Countries:").append("\n");

        String[] allLocales = Locale.getISOCountries();

        for (int m = 0; (allLocales != null) && (m < allLocales.length); m++) {
          buf.append(allLocales[m]).append("\n");
        } // for all avaliable locales
      }

      logDebug(buf.toString());
    }
  }
}
