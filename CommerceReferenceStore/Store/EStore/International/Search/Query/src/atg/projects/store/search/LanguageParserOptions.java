/*<ATGCOPYRIGHT>
 * Copyright (C) 2007-2011 Art Technology Group, Inc.
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

package atg.projects.store.search;

import atg.nucleus.logging.LoggingSupport;
import atg.search.routing.utils.Language;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;

import java.util.Locale;

/**
 * This class builds language constraint for search query.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/International/Search/Query/src/atg/projects/store/search/LanguageParserOptions.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class LanguageParserOptions extends LoggingSupport {

  //-------------------------------------
  /**
   * Class version string
   */
  //-------------------------------------
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/International/Search/Query/src/atg/projects/store/search/LanguageParserOptions.java#2 $Change: 407408 $";

  //-------------------------------------
  // Constants
  //-------------------------------------

  /**
   * Default request locale path
   */
  public static final String REQUEST_LOCALE_PATH = "/atg/dynamo/servlet/RequestLocale";

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // property: RequestLocalePath
  String mRequestLocalePath = REQUEST_LOCALE_PATH;

  /**
   * Sets new path to the RequestLocale component.
   *
   * @param pRequestLocalePath path to the RequestLocale component.
   */
  public void setRequestLocalePath(String pRequestLocalePath) {
    mRequestLocalePath = pRequestLocalePath;
  }

  /**
   * Returns path to the RequestLocale component.
   *
   * @return path to the RequestLocale component.
   */
  public String getRequestLocalePath() {
    return mRequestLocalePath;
  }

  /**
   * Returns a <code>Language</code> for the locale's language that is appropriate
   * for display to the user. Actual Locale object retireves from
   * the current request.
   *
   * @return language that used by ATG Search
   */
  public Language getLanguage() {
    Language lang = Language.valueOf(getCurrentLocale());

    if(lang == null) {
      lang = Language.valueOf(Locale.getDefault());
    }

    return lang;
  }

  /**
   * @return locale from current request
   */
  protected Locale getCurrentLocale() {
    DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
    RequestLocale requestLocale = (RequestLocale) request.resolveName(getRequestLocalePath());
    Locale locale = null;

    if(requestLocale != null) {
      locale = requestLocale.getLocale();
    }

    if(locale == null) {
      locale = Locale.getDefault();
    }

    if(isLoggingDebug()) {
      logDebug("Current locale: " + locale.toString());
    }

    return locale;
  }
}
