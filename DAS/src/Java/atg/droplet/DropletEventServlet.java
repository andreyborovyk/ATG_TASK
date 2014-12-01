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

import atg.core.util.IntVector;
import atg.core.exception.BadValueException;
import atg.core.net.URLUtils;

import atg.naming.*;
import atg.nucleus.*;

import atg.servlet.*;
import atg.servlet.pagecompile.taglib.TagNameEncoderService;
import atg.servlet.pipeline.*;
import atg.service.perfmonitor.*;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletEventServlet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public
class DropletEventServlet
extends PipelineableServletImpl 
implements DropletConstants {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletEventServlet.java#2 $$Change: 651448 $";

  protected static final String INVOKE_FORM_HANDLER = "Invoke Form Handler";
  
  public static final String HANDLE_TYPE_ONE = "atg.droplet.handleTypeOne";
  public static final String HANDLE_TYPE_TWO = "atg.droplet.handleTypeTwo";
  public static final String HANDLE_OBJECT_ONE = "atg.droplet.handleObjectOne";
  public static final String HANDLE_OBJECT_TWO = "atg.droplet.handleObjectTwo";
  
  
  //-------------------------------------
  /**
   * Dictionary that maps URLs to Droplet form objects.  Once added, a 
   * form exists forever.
   */
  Dictionary mEventSenders = new Hashtable();

  /**
   * Are DropletExceptions rendered into the page (true) or just given to the
   * handleXXX method (false)
   */
  boolean mReportDropletExceptions = true;

  void addEventSender(String pId, EventSender pSender) {
    mEventSenders.put(pId, pSender);
  }

  //-------------------------------------
  EventSender getEventSender(String pId) {
    return (EventSender) mEventSenders.get(pId);
  }
  
  private String mSkipDynamoFormHandlerParam;

  /**
   *
   *
   */
  public void setSkipDynamoFormHandlerParam( String pSkip ) {
    mSkipDynamoFormHandlerParam = pSkip;
  }

  public String getSkipDynamoFormHandlerParam() {
    return mSkipDynamoFormHandlerParam;
  }


  //---------------------------------------------------------------------------
  // property: enforceSessionConfirmation

  boolean mEnforceSessionConfirmation = true;

  /** Whether to enforce session confirmation number matching. */ 
  public void setEnforceSessionConfirmation(boolean pEnforceSessionConfirmation) {
    mEnforceSessionConfirmation = pEnforceSessionConfirmation;
  }

  /** Whether to enforce session confirmation number matching. */   
  public boolean isEnforceSessionConfirmation() {
    return mEnforceSessionConfirmation;
  }


  //---------------------------------------------------------------------------
  // property: warnOnSessionConfirmationFailure

  boolean mWarnOnSessionConfirmationFailure = true;

  /** Whether to warn on session confirmation failure. */
  public void setWarnOnSessionConfirmationFailure(boolean pWarnOnSessionConfirmationFailure) {
    mWarnOnSessionConfirmationFailure = pWarnOnSessionConfirmationFailure;
  }

  /** Whether to warn on session confirmation failure. */  
  public boolean isWarnOnSessionConfirmationFailure() {
    return mWarnOnSessionConfirmationFailure;
  }

  

  //------------------------------------------------------

  private String[] mDynamoHandlerPrefixes = null;
  
  public void setDynamoHandlerPrefixes(String[] pDynamoHandlerPrefixes) {
    mDynamoHandlerPrefixes = pDynamoHandlerPrefixes;
  }
  
  public String[] getDynamoHandlerPrefixes() {
    return mDynamoHandlerPrefixes;
  }

  //------------------------------------------------------

  /**
   *  Form security that enable security check while form submit (added for bug 35050)
   */

  boolean mFormSecurity = true;

  /*
   * Sets the FormSecurity property.
   *
   * Should we check the form submit URL/anchor href  with the actionURL/Href list
   *
   */
  public void setFormSecurity(boolean pSecurity) {
    mFormSecurity = pSecurity;
  }

  /**
   * Gets the FormSecurity property.
   *
   * Should we check the form submit URL/anchor href  with the actionURL/Href list
   *
   */
  public boolean isFormSecurity() {
    return mFormSecurity;
  }
  
  //-------------------------------------
  
  /** The Strings of XML mime-types */
  String [] mXmlMimeTypes;
  
  //-------------------------------------
  /**
   * Returns the array of Strings of XML mime types. 
   * @beaninfo
   *   description: The XML MIME types
   **/
  public String [] getXmlMimeTypes ()
  {
    return mXmlMimeTypes;
  }

  //-------------------------------------
  /**
   * Sets the array of Strings of XML mime types.
   **/
  public void setXmlMimeTypes (String [] pXmlMimeTypes)
  {
    mXmlMimeTypes = pXmlMimeTypes;
  }
  
  
  
  // ------------------------------------
  
  String [] mHandlerTypes = null;
  /**
   * Sets the array of parameter types that should be accepted for handleXXX
   * methods in FormHandler.
   * The first element in the array corresponds to the type of 
   * the first method parameter, the second element corresponds to the
   * type of the second parameter.
   * If this value is set to null then the default parameter types
   * ,<code>javax.servlet.ServletRequest</code> and <code>javax.servlet.ServletResponse</code>
   * will be used instead.
   */
  public void setHandlerTypes(String [] pTypes) {
    mHandlerTypes = pTypes;
  }
  public String [] getHandlerTypes() {
    return mHandlerTypes;
  }

  // ------------------------------------
  
  private TagNameEncoderService mTagNameEncoderService;
  /** Sets the TagNameEncoderService. */
  public void setTagNameEncoderService(TagNameEncoderService pTagNameEncoderService) {
    mTagNameEncoderService = pTagNameEncoderService;
  }
  /** Gets the TagNameEncoderService. */
  public TagNameEncoderService getTagNameEncoderService() {
    return mTagNameEncoderService;
  }
  
  //--------- Property: deferForwardsAndRedirects -----------

  //---------------------------------------------------------------------------
  // property: deferForwardsAndRedirects
  //---------------------------------------------------------------------------

  private boolean mDeferForwardsAndRedirects = true;
  /**
   * Set whether to defer forwards and redirects until after form handling has completed, or to
   * execute forwards and redirects imediately.
   * <p>
   * Deferred forwards and redirects allow form handlers (often those that manage their own 
   * transactions) to forward/redirect after the afterSet method has been called (once the 
   * transaction has completed).  A common design pattern is to create and end a transaction 
   * in before and after set, respectively.  If a form handler requests a forward
   * inside that transaction, then we don't necessarily want to render the forward request in 
   * the same transaction.  This is especially true if the form handler has rolled 
   * back the transaction.  Also, deferring the forward until after the transaction 
   * completes insulates the work the form handler performed from a rollback set 
   * by the forwarded page.  Likewise, when a form handler redirects, it is often valuable
   * to defer executing a redirect until after the transaction completes to prevent the 
   * subsequent request from being processed before a transaction completes.
   * <p>
   * Set this property to "true" to defer forwards and redirects until after the form handling
   * has completed.
   */
  public void setDeferForwardsAndRedirects(boolean pDeferForwardsAndRedirects) {
    mDeferForwardsAndRedirects = pDeferForwardsAndRedirects;
  }

  /**
   * Get whether to defer forwards and redirects until after form handling has completed, or to
   * execute forwards and redirects imediately.
   * <p>
   * Deferred forwards and redirects allow form handlers (often those that manage their own 
   * transactions) to forward/redirect after the afterSet method has been called (once the 
   * transaction has completed).  A common design pattern is to create and end a transaction 
   * in before and after set, respectively.  If a form handler requests a forward
   * inside that transaction, then we don't necessarily want to render the forward request in 
   * the same transaction.  This is especially true if the form handler has rolled 
   * back the transaction.  Also, deferring the forward until after the transaction 
   * completes insulates the work the form handler performed from a rollback set 
   * by the forwarded page.  Likewise, when a form handler redirects, it is often valuable
   * to defer executing a redirect until after the transaction completes to prevent the 
   * subsequent request from being processed before a transaction completes.
   * <p>
   * If this property is set to "true", forwards and redirects will be deferred until 
   * after the form handling has completed.
   */
  public boolean isDeferForwardsAndRedirects() {
    return mDeferForwardsAndRedirects;
  }
  
  //-------------------------------------
  /**
   * Looks up the form defined by the id specified.  If no form exists,
   * a new form is created.  In either case a reference to the form is
   * returned.
   *
   * @param pId the unique string id for the form, typically defined by the
   * the URI of the page containing the page.
   */
  public FormTag addForm(String pId) {
    return addForm(pId, null);
  }

  //-------------------------------------
  /**
   * Looks up the form defined by the id specified.  If no form exists,
   * a new form is created.  In either case a reference to the form is
   * returned.
   *
   * @param pId the unique string id for the form, typically defined by the
   * the URI of the page containing the page.
   * @param pSyncPath if not null, the path name of a component to synchronize
   * with when processing the form submission.  This means that only one
   * form will be submitted to this component at a time.
   */
  public synchronized FormTag addForm(String pId, String pSyncPath) {
    FormTag f = null;
    Object obj = getEventSender(pId);

    if (obj != null) {
      f = (FormTag) obj;

      /* 
       * Update the sync path if it has changed.  This is not a dynamic
       * element so it can't vary from form to form, but a user might
       * change it in a form without restarting the server.  We should
       * just use the latest value for all subsequent forms.
       */
      if (pSyncPath != null) {
        String oldSync = f.getSynchronized();
        if (oldSync == null || !oldSync.equals(pSyncPath))
          f.setSynchronized(pSyncPath);
      }
    }

    if (f == null) {
      f = new FormTag(pId, this, pSyncPath);
    }
    return f;
  }


  //-------------------------------------
  /**
   * Remove the form specified by its id.
   *
   * @param pId The form id.
   * @return the removed FormTag, or null if
   * nothing removed.
   */
  public synchronized FormTag removeForm(String pId) {
    return (FormTag)mEventSenders.remove(pId);
  }


  /** Get the property paths fo the specified form.
   * @param pId The form id. 
   */
  public synchronized String[] getPropertyPathsForForm(String pId) {
    String[] result = null;
    FormTag formTag = (FormTag) getEventSender(pId);
    if (formTag != null) {
      result = formTag.getFormEventProperties();
    }
    return result;
  }

  //-------------------------------------
  public synchronized AnchorTag addAnchor(DynamoHttpServletRequest pReq,
  					  DynamoHttpServletResponse pRes,
  					  String pId, 
  					  String pPropertyPath) 
       throws ServletException {
    return addAnchor(pReq, pRes, pId, pPropertyPath, null, null);
  }


  //-------------------------------------
  public synchronized AnchorTag addAnchor(DynamoHttpServletRequest pReq,
  					  DynamoHttpServletResponse pRes,
  					  String pId, 
  					  String pPropertyPath,
  					  TagConverter pConverter,
  					  Properties pConverterArgs)
       throws ServletException {
    AnchorTag a = null;
    Object obj = getEventSender(pId);

    if (obj != null) {
      a = (AnchorTag) obj;
      /*
       * The first anchor that gets submitted for a particular property
       * path uses just the base ID of the tag.  The second and
       * subsequent anchor tags have the property path appended to the
       * id as its identifying id.  This makes the id short for the
       * normal case, but still allows a single anchor tag (like in 
       * a ForEach loop)
       */
      if (!a.getPropertyPath().equals(pPropertyPath)) {
        pId = pId + ":_D:" + pPropertyPath;
        a = (AnchorTag) getEventSender(pId);
      }
    }

    if (a == null) {
      a = new AnchorTag(pId, this, pConverter, pConverterArgs);
      a.setPropertyPath(pPropertyPath);
    }

    addQueryParameter(pReq, DropletConstants.DROPLET_ARGUMENTS, pId);

    return a;
  }
  
  /**
   * Adds a query paremeter for the next request, using the 
   * TagNameEncoderService.  If the service is null, the request is
   * assumed to be a DynamoHttpServletRequest.
   */
  public void addQueryParameter(DynamoHttpServletRequest pRequest, String pName, String pValue) {
    TagNameEncoderService tnes = getTagNameEncoderService();
 
    if(tnes == null) {
      pRequest.addQueryParameter(pName, pValue);
    }
    else
      tnes.addActionParameter(pRequest, pName, pValue);
  }
  // ------------------------------------
  /**
   * Initializes this services after it is created by Nucleus.
   */
  public void doStartService() {
    initializeHandlerTypes();
  }
  Class mHandlerTypeOne = ServletRequest.class;
  Class mHandlerTypeTwo = ServletResponse.class;
  boolean mHasAlternateHandlerTypes = false;
  protected void initializeHandlerTypes() {
    try {
      if (mHandlerTypes != null
          && mHandlerTypes[0] != null
          && mHandlerTypes[1] != null) {
        mHandlerTypeOne = Class.forName(mHandlerTypes[0]);
        mHandlerTypeTwo = Class.forName(mHandlerTypes[1]);
        mHasAlternateHandlerTypes = true;
      }
    }
    catch (ClassNotFoundException e) {
     if (isLoggingError()) 
       logError("Cannot find class for form handler param." +e.getMessage());
    }
      
  }
  /**
   * Returns true if altername parameters for handleXXX methods have been specified.
   * The Class definition for each paramter type must exist in the classpath
   * otherwise the default parameter types <code>ServletRequest , ServletResponse</code>
   * are used.
   * @return true if an altername handleXXX method param type has been specified.
   */
  public boolean hasAlternateHandlerTypes() {
    return mHasAlternateHandlerTypes;
  }
  //-------------------------------------
  /**
   * Looks up the handler for the any possible Droplet events in this
   * request and delivers them.
   */
  public boolean sendEvents(DynamoHttpServletRequest pRequest, 
  			    DynamoHttpServletResponse pResponse)
      throws DropletException, ServletException, IOException {
    
    /*
     */
    String args = pRequest.getParameter(DROPLET_ARGUMENTS);

    if (args == null || pRequest.getFormEventsSent()) return true;

    String skipParamName = getSkipDynamoFormHandlerParam();
    boolean dynamoPrefix = requestMatchesDynamoPrefixes(args);

    if ( skipParamName != null && pRequest.getParameter(skipParamName) != null &&
      null == pRequest.getAttribute(DropletEventServlet.HANDLE_TYPE_ONE) && !dynamoPrefix )
    {
      // If a skipParamName is set, and there is a parameter by that
      // name, and this is a dynamo request. Used with 'paf_ps' param
      // to avoid calling the form handlers twice on jsr168 portals
      // Pr: 86020, 85936
      return true;
    }
    
    EventSender sender = (EventSender) getEventSender(args);

    if (sender != null) {
      pRequest.setFormEventsSent(true);
      
      /*
      *  check form secutiy
      */
      if (mFormSecurity) {

        if ((sender instanceof FormTag)) {
          FormTag form = (FormTag)sender;
          if (!form.hasActionURL(pRequest.getRequestURI())) {
              if (isLoggingWarning())
                logWarning("A form has been submitted with a URL which does not match the action value the form was rendered with.  The form name is:"
                          + args + " and the submitted URL is: " + pRequest.getRequestURI() +
                          ".The form events are being ignored since this may impose a security problem if the action URL is protected by the server." );
              return true;
          }
        }
        else {    // anchor
          AnchorTag anchor = (AnchorTag)sender;
          if (!anchor.hasHref(pRequest.getRequestURI())) {
              if (isLoggingWarning())
                logWarning("A anchor tag has been submitted with a URL which does not match the href value the anchor tag was rendered with.  The anchor name is:"
                          + args + " and the submitted URL is: " + pRequest.getRequestURI() +
                          ".The anchor events are being ignored since this may impose a security problem if the action URL is protected by the server." );
              return true;
          }
        }
      }
      
      try {
        // Add parameters indicating alternate handleXXX types
        if (mHasAlternateHandlerTypes) {
          pRequest.setAttribute(HANDLE_TYPE_ONE,mHandlerTypeOne);
          pRequest.setAttribute(HANDLE_TYPE_TWO,mHandlerTypeTwo);
        }
        PerformanceMonitor.startOperation(INVOKE_FORM_HANDLER, args);
        return sender.sendEvents(pRequest, pResponse);
      } finally {
        try {
          PerformanceMonitor.endOperation(INVOKE_FORM_HANDLER, args);
        } catch (PerfStackMismatchException e) {
          logDebug(e);
        }
        // Cleanup
        if (mHasAlternateHandlerTypes) {
          pRequest.removeAttribute(HANDLE_TYPE_ONE);
          pRequest.removeAttribute(HANDLE_TYPE_TWO);
        }
      }
    }

    return true;
  }

  public void service(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) 
  throws ServletException, IOException {
    
    /*
     * Allow subsequent objects in the request to get at this guy
     */
    pRequest.setAttribute(DROPLET_EVENT_ATTRIBUTE, this);
    
    if (!sendEvents(pRequest, pResponse))
      return;
    
    
    
    // If we're the first element encountered in the request (as
    // opposed to the PageTag), then we are responsible for calling
    // the afterGet's since we'll be the last element in the request
    boolean claimedAfterGets = false;
    if (!pRequest.isAfterGetsClaimed ()) {
      pRequest.setAfterGetsClaimed (true);
      claimedAfterGets = true;
    }
    
    try {
      passRequest(pRequest, pResponse);
    }
    finally {
      if (claimedAfterGets) {
        /* 
         * We need to call the after Get methods even if an exception occurred
         * This is particularly true now that transaction commits are done 
         * in the afterGet method.
         */
        invokeDropletAfterGets(pRequest, pResponse);
        invokeObjectAfterGets(pRequest, pResponse);
      }
    }
  }
 // -----------------------------
  /**
   * Invokes the afterGet methods for formhandlers that implement
   * DropletFormHandler
   * @param pRequest
   * @param pResponse
   */
  public static void invokeDropletAfterGets(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
    Vector bgets = (Vector) pRequest.getAttribute(DROPLET_BEFORE_GET_ATTRIBUTE);
    if (bgets != null) {
      int size = bgets.size();
      for (int i = 0; i < size; i++) {
        ((DropletFormHandler) bgets.elementAt(i)).afterGet(pRequest, pResponse);
      }
    }
  }
  
  // -----------------------------
  /**
   * Invokes the afterGet methods for formhandlers that implement
   * ObjectFormHandler
   * @param pRequest
   * @param pResponse
   */
  public static void invokeObjectAfterGets(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
    Vector bgets = (Vector) pRequest.getAttribute(OBJECT_BEFORE_GET_ATTRIBUTE);
    if (bgets != null) {
      int size = bgets.size();
      for (int i = 0; i < size; i++) {
        Object [] params = getAlternateFormParams(pRequest);
        if (params != null)
          ((ObjectFormHandler) bgets.elementAt(i)).afterGet(params[0],params[1]);
        else
          ((ObjectFormHandler) bgets.elementAt(i)).afterGet(pRequest,pResponse);
      }
    }
  }


  /**
   *
   * Creates and returns a new Servlet that will administer this
   * service.  
   **/
  protected Servlet createAdminServlet ()
  {
    return new DropletEventAdminServlet(this, getNucleus());
  }

  /*
   * Sets the ReportDropletExceptions property.
   *
   * Should we report droplet exceptions to the reader of the page or
   * just send them to the handleXXX method so that the form handler can
   * deal with them.  
   */
  public void setReportDropletExceptions(boolean pReport) {
    mReportDropletExceptions = pReport;
  }

  /**
   * Gets the ReportDropletExceptions property.
   *
   * Should we report droplet exceptions to the reader of the page or
   * just send them to the handleXXX method so that the form handler can
   * deal with them.  
   */
  public boolean getReportDropletExceptions() {
    return mReportDropletExceptions;
  }
  // ----------------------------------
  /**
   * Returns the types of any alternate form parameters.
   * Typically forms' handleXXX methods take DynamoHttpServletRequest/DynamoHttpServletResponse.
   * 
   * In some environments, such as JSR168 Portals, alternate parameter types
   * may be required.
   * @param pRequest
   * @return
   */
  public static Class [] getAlternateFormParamTypes(ServletRequest pRequest) {
    if (!hasAlternateFormParamTypes(pRequest))
        return null;
    Class [] params = {(Class)pRequest.getAttribute(HANDLE_TYPE_ONE),(Class)pRequest.getAttribute(HANDLE_TYPE_TWO)};
    return params;
  }
 
  //----------------------------------
  /**
   * Sets the actual objects that will be used as arguments when invoking
   * form handler methods such as handleXXX,beforeGet,afterGet,beforeSet,afterSet
   * @see getAlternateFormParams
   */
  public static void setAlternateFormParams(Object pParamOne, Object pParamTwo,ServletRequest pRequest) {
    if (pRequest == null) return;
    pRequest.setAttribute(HANDLE_OBJECT_ONE, pParamOne);
    pRequest.setAttribute(HANDLE_OBJECT_TWO, pParamTwo);
  }
  //----------------------------------
  /**
   * Returns the actual objects that will be used as arguments when invoking form handler methods.
   * @see setAlternateFormParams
   */
    public static Object [] getAlternateFormParams(ServletRequest pRequest) {
    if (!hasAlternateFormParamTypes(pRequest))
      return null;
    Object [] params = {pRequest.getAttribute(HANDLE_OBJECT_ONE),pRequest.getAttribute(HANDLE_OBJECT_TWO)};
    return params;
  }
  // ----------------------------------
  /**
   * Sets the alternate parameter types for form handling. This method must be called before
   * the DropletEventServlet is invoked in the pipeline.
   * @param pParamOne
   * @param pParamTwo
   * @param pRequest
   */
  public static void setAlternateFormParamTypes(Class pParamOne, Class pParamTwo,ServletRequest pRequest) {
    pRequest.setAttribute(HANDLE_TYPE_ONE, pParamOne);
    pRequest.setAttribute(HANDLE_TYPE_TWO, pParamTwo);
  }
  
  /**
   * Checks the given request object for altername form parameter attributes.
   * @param pRequest
   * @return
   */
  public static boolean hasAlternateFormParamTypes(ServletRequest pRequest) {
    return (pRequest.getAttribute(HANDLE_TYPE_ONE) != null 
        && pRequest.getAttribute(HANDLE_TYPE_TWO) != null);
  }

  /**
   * 
   * @param pRequest
   * @return
   */
  public boolean requestMatchesDynamoPrefixes( String pArgs ) {
    String[] prefixes = getDynamoHandlerPrefixes();
    if (prefixes != null && prefixes.length > 0) {
      for (int i = 0; i < prefixes.length; i++) {
        if (pArgs.startsWith(prefixes[i]))
          return true;                  
      }            
    }
    
    return false;        
  }

  /** Validate the session confirmation number. A return value of false
   * means that the droplet event processing should be denied. True means
   * allow event processing.
   *
   * 
   */
  public boolean validateSessionConfirmationNumber(DynamoHttpServletRequest pReq) {
    String strSessionConfirm = 
      pReq.getParameter(DropletConstants.DROPLET_SESSION_CONF);
    if (isLoggingDebug()) {
      logDebug("Session confirmation number:" + strSessionConfirm);
    }
    if (strSessionConfirm == null) {
      if (isWarnOnSessionConfirmationFailure() && isLoggingWarning() && pReq.isRequestedSessionIdValid()) {
        logWarning("Missing session confirmation number: " + getAdditionalRequestDetails(pReq));
      }
      if (isEnforceSessionConfirmation()) {
        return false;
      }
    }
    else if (!strSessionConfirm.equals(Long.toString(pReq.getSessionConfirmationNumber()))) {
      if (isWarnOnSessionConfirmationFailure() && isLoggingWarning() && pReq.isRequestedSessionIdValid()) {
        pReq.logWarning("Session confirmation number mismatch(" +
                        strSessionConfirm + " != " + pReq.getSessionConfirmationNumber() +
                        "): " + getAdditionalRequestDetails(pReq));
      }

      if (isEnforceSessionConfirmation()) {
        return false;
      }
    }

    return true;
  }

  /** Get some additional request details. Used when logging session confirmation
   * warnings.
   *
   * @param pReq the request from which to get additional details 
   */
  protected String getAdditionalRequestDetails(DynamoHttpServletRequest pReq) {
    StringBuffer strbuf = new StringBuffer();
    strbuf.append("Request URI: " + pReq.getRequestURI());
    String strReferer = pReq.getHeader("Referer");
    if (strReferer != null) {
      strbuf.append(", Referer: " + strReferer);
    }
    return strbuf.toString();
  }

  /**
   * This class implements the admin interface for droplets.  It displays
   * the list of pages that are known to the system and for a selected
   * page it will list the tags, property info, etc. that is stored there.
   */
  class DropletEventAdminServlet extends PipelineableServletAdminServlet {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletEventServlet.java#2 $$Change: 651448 $";

    public DropletEventAdminServlet (PipelineableServlet pService, Nucleus pNucleus) {
      super (pService, pNucleus);
    }
    
    protected void printAdmin (HttpServletRequest pRequest,
                               HttpServletResponse pResponse,
                               ServletOutputStream pOut)
         throws ServletException, IOException
    {
      if (mService instanceof DropletEventServlet) {
        Dictionary senders = ((DropletEventServlet) mService).mEventSenders;
        EventSender sender;
        pOut.println("<h2>Droplet Forms + anchors </h2>");
        pOut.println("<table border><tr><td>URL</td><td>num event tags</td></tr>");
        for (Enumeration e = senders.keys(); e.hasMoreElements(); ) {
          String serviceName = formatServiceName(pRequest.getPathInfo(), 
                                                 pRequest);
          String key = (String) e.nextElement();
          sender = (EventSender) senders.get(key);
          pOut.print("<tr><td>");
          pOut.print("<a href=\"");
          pOut.print(serviceName);
          pOut.print("?expandEventSender=");
          pOut.print(URLUtils.escapeUrlString(key));
          pOut.print("\">");
          pOut.print(key);
          pOut.print("</a></td><td>");
          pOut.print(sender.getNumEventReceivers());
          pOut.println("</td></tr>");
        }
        pOut.println("</table>");
        String senderName = pRequest.getParameter("expandEventSender");
        if (senderName != null) {
          pOut.print("<h2>Details for sender: ");
          pOut.print(senderName);
          pOut.print("</h2>");
          sender = (EventSender) senders.get(senderName);
          if (sender != null) {
            pOut.println("<table border><tr><td>name</td><td>property</td><td>submitvalue</td><td>is image</td></tr>");
            for (Enumeration e = sender.getEventReceivers(); e.hasMoreElements(); ) {
              EventReceiver rcv = (EventReceiver) e.nextElement();
              pOut.print("<tr><td>");
              pOut.print(ServletUtil.toString(rcv.getName()));
              pOut.print("</td><td>");
              pOut.print(ServletUtil.toString(rcv.getPropertyPath()));
              pOut.print("</td><td>");
              pOut.print(ServletUtil.toString(rcv.getSubmitValue()));
              pOut.print("</td><td>");
              pOut.print(rcv.isImage());
              pOut.println("</td></tr>");
            }
            pOut.println("</table>");
          }
        }
      }
      super.printAdmin (pRequest, pResponse, pOut);
    }
  } // end inner-class DropletEventAdminServlet
}
