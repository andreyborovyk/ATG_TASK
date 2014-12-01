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

package atg.projects.store.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import atg.core.net.URLUtils;
import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;


/**
 * This droplet takes a list of language keys, and returns a list of objects associating those
 * keys with their proper display languages.
 *
 * This is useful to list the available languages at the top of a page.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/droplet/DisplayLanguagesDroplet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 *
 */
public class DisplayLanguagesDroplet extends DynamoServlet {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/droplet/DisplayLanguagesDroplet.java#2 $$Change: 651448 $";


  //-----------------------------------
  // JSP File extension
  private static final String JSP_EXTENSION = ".jsp";
  
  //-----------------------------------
  // Index Jsp
  private static final String DEFAULT_PAGE = "index.jsp";

  /**
   * Country code parameter name.
   */
  public static final ParameterName COUNTRY_CODE = ParameterName.getParameterName("countryCode");

  /**
   * Language parameter name.
   */
  public static final ParameterName LANGUAGES = ParameterName.getParameterName("languages");

  /**
   * Display languages parameter name.
   */
  public final static String DISPLAY_LANGUAGES = "displayLanguages";

  /**
   * Current selection parameter name.
   */
  public final static String CURRENT_SELECTION = "currentSelection";

  /**
   * Locale parameter name.
   */
  public final static String LANG_SELECTION = "locale";

  /**
   * Output parameter name.
   */
  public final static String OUTPUT = "output";

  /**
   * Request locale.
   */
  private RequestLocale mRequestLocale;

  /**
   * @param pRequestLocale - request locale.
   */
  public void setRequestLocale(RequestLocale pRequestLocale) {
    mRequestLocale = pRequestLocale;
  }

  /**
   * @return the request locale.
   **/
  public RequestLocale getRequestLocale() {
    return mRequestLocale;
  }

  /**
   * Renders "displayLanguages" output parameter which is a list of objects associating the
   * language codes with display languages.
   *
   * @param pRequest DynamoHttpSevletRequest
   * @param pResponse DynamoHttpServletResponse
   * @throws ServletException if an error occurs
   * @throws IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    List languages = (List) pRequest.getObjectParameter(LANGUAGES);
    String countryCode = pRequest.getParameter(COUNTRY_CODE);
    List displayLanguages = new ArrayList();

    RequestLocale requestLocale = getRequestLocale();
    Locale currentLocale = requestLocale.discernRequestLocale(pRequest, requestLocale);

    int currentSelection = 0;

    if (languages != null) {
      Iterator languageIter = languages.iterator();
      int index = 0;

      while (languageIter.hasNext()) {
        String language = (String) languageIter.next();
        Locale locale = new Locale(language, countryCode);
        String languageDisplayName = locale.getDisplayLanguage(locale);
        // uppercase the first letter in language's display name
        if (!StringUtils.isEmpty(languageDisplayName)){
          languageDisplayName = languageDisplayName.substring(0, 1).toUpperCase(locale)+
                                languageDisplayName.substring(1);
        }
        DisplayLanguage displayLanguage = new DisplayLanguage(languageDisplayName,
            createLinkURL(locale, pRequest), locale.toString());
        displayLanguages.add(displayLanguage);

        if ((currentLocale != null) && (currentLocale.getLanguage() != null) &&
            currentLocale.getLanguage().equalsIgnoreCase(locale.getLanguage())) {
          currentSelection = index;
        } else {
          index++;
        }
      }
    }

    pRequest.setParameter(CURRENT_SELECTION, new Integer(currentSelection));
    pRequest.setParameter(DISPLAY_LANGUAGES, displayLanguages);
    pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
  }

  /**
   * Create URL.
   *
   * @param pLocale - locale
   * @param pRequest - http request
   *
   * @return URL
   */
  public String createLinkURL(Locale pLocale, DynamoHttpServletRequest pRequest) {
    String localeCode = pLocale.getLanguage() + "_" + pLocale.getCountry();

    // use original HTTP request because Dynamo HTTP request
    // does not contain proper query parameters after 
    // JumpServlet forwards request.
    HttpServletRequest originalHttpRequest = pRequest.getRequest();
    
    String linkURL = originalHttpRequest.getRequestURI();
    // Set a default page (index.jsp) to go to if
    // there was none specified in the requestURI
    if(!linkURL.endsWith(JSP_EXTENSION)){
      if(!linkURL.endsWith("/")){
        linkURL += "/";
      }
      linkURL += DEFAULT_PAGE;
    }
    
    Enumeration queryParameterNames = originalHttpRequest.getParameterNames();
    int paramCount = 0;

    if (queryParameterNames != null) {
      while (queryParameterNames.hasMoreElements()) {
        String queryParamName = (String) queryParameterNames.nextElement();

        // We don't want to add parameters starting with "_" to prevent multiple
        // form-submission.  We don't want to pass the "numResults" parameter
        // because the search in the new language will return different results.
        if (!queryParamName.equals(LANG_SELECTION) && !queryParamName.startsWith("_") &&
            !queryParamName.equals("numResults")) {
          String value = originalHttpRequest.getParameter(queryParamName);

          if (value != null) {
            if (paramCount == 0) {
              linkURL = linkURL + "?";
            } else {
              linkURL = linkURL + "&";
            }

            linkURL = linkURL + queryParamName + "=" + URLUtils.escapeUrlString(value);
            paramCount++;
          }
        }
      }
    }

    if (paramCount == 0) {
      linkURL = linkURL + "?";
    } else {
      linkURL = linkURL + "&";
    }

    linkURL = linkURL + LANG_SELECTION + "=" + localeCode;

    return linkURL;
  }

  /**
   * Class for display language.
   */
  public class DisplayLanguage {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/droplet/DisplayLanguagesDroplet.java#2 $$Change: 651448 $";

    /**
     * Display language.
     */
    private String mDisplayLanguage;

    /**
     * URL.
     */
    private String mLinkURL;
    
    /**
     * Locale
     */
    private String mLocale;

    /**
     * Constructor.
     * @param pDisplayLanguage - display language
     * @param pLinkURL - URL
     */
    public DisplayLanguage(String pDisplayLanguage, String pLinkURL, String pLocale) {
      mDisplayLanguage = pDisplayLanguage;
      mLinkURL = pLinkURL;
      mLocale = pLocale;
    }

    /**
     * @return The name of the language in the given language.
     **/
    public String getDisplayLanguage() {
      return mDisplayLanguage;
    }

    /**
     * @return The URL we want the link for this language to go to.
     **/
    public String getLinkURL() {
      return mLinkURL;
    }
    
    /**
     * @return The locale.
     **/
    public String getLocale() {
      return mLocale;
    }
  }
}
