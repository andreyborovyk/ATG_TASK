/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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

package atg.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.core.net.URLUtils;
import atg.core.util.ResourceUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.servlet.ServletUtil;

/**
 * This servlet takes a single parameter which is the <i>url</i>
 * parameter and redirects the page that called this servlet to the
 * defined URL.
 * <dl>
 *
 * <dt>url
 * <dd>The parameter that defines the URL, relative or absolute, to
 * which this page that called the servlet will be redirected.
 *
 * </dl>
 *
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/Redirect.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class Redirect extends DynamoServlet {

  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/Redirect.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Member variables

  private final static String URL = "url";
  static final String MY_RESOURCE_NAME = "atg.droplet.DropletResources";
  private static java.util.ResourceBundle sResourceBundle 
  = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  //-------------------------------------
  // Methods

  public void service(DynamoHttpServletRequest pRequest, 
                      DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException {
    
    String url = pRequest.getParameter(URL);
    
    if (url == null) {
      if (isLoggingError()) 
        logError(ResourceUtils.getMsgResource("noURL", 
                                              MY_RESOURCE_NAME, 
                                              sResourceBundle));
      return;
    }

    if (URLUtils.isRelative(url) == true) {
      if (isLoggingDebug())
        logDebug("local redirect URL: " + url);
      pResponse.sendLocalRedirect(url, pRequest);
    }
    else {
      if (isLoggingDebug())
        logDebug("redirect URL: " + url);

      /**
       * If we are just switching protocols or if this is a URL which
       * takes us to an affiliated site, we encode the URL with the
       * session id.  
       **/
      url = pResponse.encodeRedirectURL(url);
      
      if (isLoggingDebug())
        logDebug("redirect URL after possible encoding: " + url);

      pResponse.sendRedirect(url);
    }

    /**
     * Once we're done with the redirect this droplet should abort the
     * current request.  This prevents the page from completing its
     * rendering process which is not necessary since we are done now.
     **/
    if (!ServletUtil.isWebLogic() && !ServletUtil.isJBoss() && !ServletUtil.isWebSphere()) {
      throw new IOException();
    }
  }
}
