/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

package atg.epub.portlet.OtherPubPortlet;

import atg.portlet.DispatchPortlet;

import java.io.IOException;

import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSecurityException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.UnavailableException;

/**
 * A portlet class that includes a jsp page based on a request param.
 *
 * @author Jeff Banister
 * @version $Id: //product/PubPortlet/version/10.0.3/classes.jar/atg/epub/portlet/OtherPubPortlet/OtherPubPortlet.java#2 $
 */

public 
class OtherPubPortlet
  extends DispatchPortlet
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //product/PubPortlet/version/10.0.3/classes.jar/atg/epub/portlet/OtherPubPortlet/OtherPubPortlet.java#2 $$Change: 651448 $";


  //-------------------------------------
  // Constants
  //-------------------------------------
  
  //-------------------------------------
  // Member Variables
  //-------------------------------------

  //-------------------------------------
  // Properties
  //-------------------------------------


  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instanceof OtherPubPortlet
   */
  public OtherPubPortlet() { 
  }

  /**
   * Called by the portlet service to indicate that a portlet should
   * handle a render request. Derived classes should override
   * this method to handle the servicing of a render request.
   * @param pRenderRequest the render request 
   * @param pRenderResponse the render response
   * @exception PortletException if the portlet has problems fulfilling the
   * rendering request
   * @exception UnavailableException if the portlet is unavailable to perform
   * render at this time
   * @exception PortletSecurityException if the portlet cannot fullfill this
   * request because of security reasons
   * @exception java.io.IOException if the streaming causes an I/O problem
   *
   *  This method shows an example of using a request parameter to override the default
   *  page for the portlet.
   */
  public void handleRender(RenderRequest pRenderRequest,
                           RenderResponse pRenderResponse)
    throws PortletException, IOException {

    PortletContext portletContext = getPortletContext();

    String path = getPath(pRenderRequest,pRenderResponse);

    String includePage=pRenderRequest.getParameter("jspName");
    if (includePage==null) {
       includePage=path;
    }
    
    if(isLoggingDebug())
      logDebug("includePage = " + includePage);
    //set a preferred content type
    pRenderResponse.setContentType(pRenderRequest.getResponseContentType());

    //Dispatch
    if(portletContext != null) {
      if(path != null) {
        PortletRequestDispatcher portletRequestDispatcher =
          portletContext.getRequestDispatcher(includePage);
        portletRequestDispatcher.include(pRenderRequest,pRenderResponse);
      }
    }
  }
} // end of class
