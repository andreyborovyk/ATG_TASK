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

package atg.servlet.exittracking;

import atg.nucleus.servlet.HttpServletService;
import atg.nucleus.ServiceException;
import atg.core.net.URLUtils;
import atg.core.util.ResourceUtils;

import java.io.IOException;
import java.util.Vector;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletException;

/**
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/servlet/exittracking/ExitTrackingServlet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ExitTrackingServlet
extends HttpServletService
implements ExitTrackingHandler, ExitTrackingEventSource
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = 
  "$Id: //product/DAS/version/10.0.3/Java/atg/servlet/exittracking/ExitTrackingServlet.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  static final String NO_OUTLINK_KEY = "NO_OUTLINK";
  static final String SENDING_REDIRECT_KEY = "SENDING_REDIRECT";
  static final String MISSING_HANDLERPARAMETERNAME_KEY ="MISSING_HANDLERPARAMETERNAME";
  static final String MISSING_HANDLERURL_KEY ="MISSING_HANDLERURL";
  
  static final String MY_RESOURCES = "atg.servlet.exittracking.ExitTrackingResource";
  static final String MISSING_RESOURCE_ERROR = "Missing ResourceBundle: " + MY_RESOURCES; 

  //-------------------------------------
  // Member Variables
  /** The ResourceBundle which manages my text messages */
  ResourceBundle mResources;

  /** The list of listeners */
  Vector mListeners;

  //-------------------------------------
  // Properties
  
  //-------------------------------------
  // property: HandlerURL
  String mHandlerURL;

  /**
   * Sets property HandlerURL
   **/
  public void setHandlerURL(String pHandlerURL) {
    mHandlerURL = pHandlerURL;
  }

  /**
   * Returns property HandlerURL
   **/
  public String getHandlerURL() {
    return mHandlerURL;
  }  

  //-------------------------------------
  // property: HandlerParameter
  String mHandlerParameterName;

  /**
   * Sets property HandlerParameterName
   **/
  public void setHandlerParameterName(String pHandlerParameterName) {
    mHandlerParameterName = pHandlerParameterName;
  }

  /**
   * Returns property HandlerParameterName
   **/
  public String getHandlerParameterName() {
    return mHandlerParameterName;
  }  

  //-------------------------------------
  // property: ExitTrackingListeners
  ExitTrackingListener [] mExitTrackingListeners;

  /**
   * Returns property ExitTrackingListeners
   **/
  public synchronized ExitTrackingListener [] getExitTrackingListeners() {
    ExitTrackingListener [] listeners = new ExitTrackingListener[getListeners().size()];
    for (int c=0; c < listeners.length; c++)
      listeners[c] = (ExitTrackingListener)getListeners().elementAt(c);
    
    return listeners;
  }  

  /**
   * Constructs an ExitTrackingServlet
   */ 
  public ExitTrackingServlet() {
    try {
      mResources = ResourceBundle.getBundle(MY_RESOURCES, atg.service.dynamo.LangLicense.getLicensedDefault());
    } 
    catch (MissingResourceException exc) {
    }
  }

  /**
   * Perfrom any necessary initializations to startup service.<BR>
   * If a schedule is provided, then it is started.
   */
  public void doStartService () throws ServiceException {
    if (mResources == null) 
      throw new ServiceException(MISSING_RESOURCE_ERROR);
    
    if (mHandlerURL == null) {
      String err = ResourceUtils.getMsgResource(MISSING_HANDLERURL_KEY,
						MY_RESOURCES, mResources);
      throw new ServiceException(err);
    }
    
    if (mHandlerParameterName == null) {
      String err = ResourceUtils.getMsgResource(MISSING_HANDLERPARAMETERNAME_KEY,
						MY_RESOURCES, mResources);

      throw new ServiceException(err);
    }

    setServletInfo(mResources.getString(SERVLET_INFO_KEY));   
    setServiceInfo(mResources.getString(SERVICE_INFO_KEY));   
  }

  /**
   * Returns an encoded URL that can be decoded by the exit tracking handler.<BR>
   * @param pURL The URL to encode if necessary
   */
  public String performExitTracking(String pURL) {
    StringBuffer sb = new StringBuffer(mHandlerURL);
    sb.append("?");
    sb.append(mHandlerParameterName);
    sb.append("=");
    sb.append(URLEncoder.encode(pURL));
    return sb.toString();
  }

  /**
   * Returns true if the supplied URL should be exit tracked<BR>
   * If the handlerURL parameter is null, then this will return false
   * @param pURL The URL to check if it should be exit tracked
   */
  public boolean shouldExitTrack(String pURL) {
    return (! URLUtils.isRelative(pURL));
  }

  /**
   * Returns the property ExitTrackingParameterName.  This is part of the
   * ExitTrackingHandler interface.
   */
  public String getExitTrackingParameterName() {
    return mHandlerParameterName;
  }

  /**
   * 
   * @param pRequest the servlet request
   * @param pResponse the servlet response
   * @exception ServletException if a servlet exception has occurred 
   * @exception IOException if an I/O exception has occurred
   */
  public void service(HttpServletRequest pRequest,
                      HttpServletResponse pResponse) 
       throws ServletException, IOException
  {
    long startTime = getRequestStartTime ();
    String outlink = pRequest.getParameter(mHandlerParameterName);
    if (outlink != null) {
      try {        
        if (isLoggingDebug())
          logDebug(mResources.getString(SENDING_REDIRECT_KEY) + outlink);

        /* 
         * We need to forward any query arguments that are passed to the
         * exittracking.dyn script to the outlink.  This is to properly deal
         * with forms to outlink sites.
         */
        String queryString = pRequest.getQueryString();
        if (queryString != null) {
          int stix = queryString.indexOf(getExitTrackingParameterName());
          if (stix != -1) {
            String newString;
            int eix = queryString.indexOf("&", stix);
            if (eix == -1) {
              if (stix == 0) newString = "";
              else newString = queryString.substring(0,stix-1);
            }
            else {
              if (stix == 0) newString = "";
              else newString = queryString.substring(0,stix-1);
              newString += queryString.substring(eix);
            }
            queryString = newString;
          }
          if (queryString.length() != 0) {
            if (outlink.indexOf("?") != -1)
              outlink = outlink + "&" + queryString;
            else
              outlink = outlink + "?" + queryString;
          }
        }

        pResponse.sendRedirect(outlink);

	/* notify the listeners that the redirect happened */
	notifyListeners(new ExitTrackingEvent(pRequest, mHandlerURL, mHandlerParameterName));

        return;
      }
      finally {
        notifyHandledRequest (startTime);
      }
    }
    else {
      ServletOutputStream out = pResponse.getOutputStream();
      pResponse.setHeader("Content-Type", "text/plain");
      String err = ResourceUtils.getMsgResource(NO_OUTLINK_KEY, MY_RESOURCES, mResources);
      out.println(err);
      if (isLoggingError())
        logError(err);
    }
  }

  //-------------------------------------
  /**
   *
   * Returns the list of exit tracking listeners, creating it if it doesn't
   * exist
   **/
  Vector getListeners ()
  {
    synchronized (this) {
      if (mListeners == null)
        mListeners = new Vector ();
    }

    return mListeners;
  }

  //-------------------------------------
  /**
   *
   * Returns true if anyone is listening for exit tracking events
   **/
  synchronized boolean hasListeners ()
  {
    return (mListeners != null &&
            getListeners().size () > 0);
  }

  //-------------------------------------
  /**
   *
   * Sends an unbound event to all of the listeners
   **/
  synchronized void notifyListeners (ExitTrackingEvent pEvent)
  {
    if (hasListeners ()) {
      Vector v = getListeners ();
      int len = v.size ();
      for (int i = 0; i < len; i++) {
        ((ExitTrackingListener) v.elementAt (i)).
          performExitTracking(pEvent);
      }
    }
  }

  //-------------------------------------
  // ExitTrackingEventSource methods
  //-------------------------------------
  /**
   *
   * Adds the specified listener to the list of listeners that will be
   * notified whenever exit tracking occurs.
   **/
  public synchronized void 
  addExitTrackingListener (ExitTrackingListener pListener)
  {
    getListeners().addElement (pListener);
  }

  //-------------------------------------
  /**
   *
   * Removes the specified listener from the list of listeners that
   * will be notified whenever exit tracking occurs.
   **/
  public synchronized void 
  removeExitTrackingListener (ExitTrackingListener pListener)
  {
    if (hasListeners ())
      getListeners().removeElement (pListener);
  }
}
