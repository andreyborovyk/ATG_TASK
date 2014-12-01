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

import atg.core.util.MapEntry;
import atg.core.util.StringUtils;
import atg.naming.NameContext;
import atg.nucleus.dms.DASMessage;
import atg.nucleus.dms.DASMessageSource;
import atg.nucleus.dms.FormSubmissionMessage;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.sessionsaver.SessionSaverServlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * This form handler performs basic error checking operations.  It typically
 * is sub-classed by form handlers that do more stuff.
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/GenericFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo description: A form handler
 * attribute: functionalComponentCategory Form Handlers
 * attribute: featureComponentCategory
 * attribute: icon /atg/ui/common/images/formhandlercomp.gif
 */
public class GenericFormHandler extends EmptyFormHandler {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/GenericFormHandler.java#2 $$Change: 651448 $";


  //----------------------------------------------
  // Constants
  //----------------------------------------------

  /**
   * The name of a parameter that represents whether to use forwards.
   * The form handler defaults to using redirects. Set this to
   * "true" to use forwards instead.
   */
  public static final String PARAM_USE_FORWARDS = "atg.formHandlerUseForwards";
  /**
   * The name of a parameter that represents whether to defer the execution of
   * forwards and redirects until after all form handling has been completed.
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
   * Set this parameter to "true" to defer forwards/redirects until after the afterSet method
   * has been called.
   */
  public static final String PARAM_DEFER_FORWARD_OR_REDIRECT = "atg.formHandlerDeferForwardOrRedirect";

  //----------------------------------------------
  // Member variables
  //----------------------------------------------

  /**
   * The list of exceptions that have occurred since the last form submission *
   */
  Vector mExceptions = null;

  /**
   * The list of unchecked exceptions that have occurred since the last form submission *
   */
  List mUncheckedExceptions = null;

  /**
   * The list of exceptions indexed by the name of the property that
   * generated them
   */
  Dictionary mErrorContainer = null;

  DynamoHttpServletRequest mCurrentRequest = null;

  //----------------------------------------------
  /**
   * Called before any setX methods on this form are set when a form
   * that modifies properties of this form handler is submitted.
   */
  public boolean beforeSet(DynamoHttpServletRequest request,
                           DynamoHttpServletResponse response)
    throws DropletFormException {
    resetFormExceptions();
    if (mCurrentRequest != null)
      if (isLoggingWarning())
        logWarning("Simultaneous use of the form handler by multiple requests.");
    mCurrentRequest = request;
    return true;
  }

  /**
   * Clear out the current request we are processing.
   */
  public boolean afterSet(DynamoHttpServletRequest request,
                          DynamoHttpServletResponse response)
    throws DropletFormException {
    mCurrentRequest = null;

    if (mSendMessages) {
      if (isLoggingDebug()) {
        logDebug("sending FormSubmission message messagePort = " + getMessagePort() + " messageType = " + getMessageType());

      }

      DASMessage msg = (DASMessage) createFormSubmissionMessage(request, response);
      if (mMessageSource != null) {
        mMessageSource.fireFormSubmissionMessage(msg, getMessagePort(), getMessageType());
      }
    }

    return true;
  }

  //----------------------------------------------
  /**
   * Called when an exception occurs while processing form submissions
   */
  public void handleFormException(DropletFormException exception,
                                  DynamoHttpServletRequest request,
                                  DynamoHttpServletResponse response) {
    addFormException(exception);
  }

  //----------------------------------------------
  /**
   * Called when an unchecked exception occurs while processing form submissions
   */
  public void handleUncheckedFormException(Throwable exception,
                                           DynamoHttpServletRequest request,
                                           DynamoHttpServletResponse response) {
    addUncheckedFormException(exception);
  }

  //--------- Property: sendMessages -----------
  boolean mSendMessages = false;

  /**
   * Sets the property sendMessages to indicate if messages are to be sent.
   *
   * @beaninfo description: indicate if messages will be sent
   */
  public void setSendMessages(boolean pSendMessages) {
    mSendMessages = pSendMessages;
  }

  /**
   * @return The value of the property sendMessages.
   */
  public boolean getSendMessages() {
    return mSendMessages;
  }


  //--------- Property: messageSource -----------
  DASMessageSource mMessageSource;

  /**
   * Sets the property messageSource.  The DASMessageSource component
   * which will handle sending the message.
   *
   * @beaninfo description: The message component that will handle sending the message
   */
  public void setMessageSource(DASMessageSource pMessageSource) {
    mMessageSource = pMessageSource;
  }

  /**
   * @return The value of the property messageSource.
   */
  public DASMessageSource getMessageSource() {
    return mMessageSource;
  }

  //--------- Property: formName -----------
  String mFormName;

  /**
   * Sets the property formName.  Set to the name of the form
   * from which the submit was issued.
   *
   * @beaninfo description: The form where the submit was issued from.
   */
  public void setFormName(String pFormName) {
    mFormName = pFormName;
  }

  /**
   * @return The value of the property formName - default to nucleus component name
   */
  public String getFormName() {
    if (StringUtils.isBlank(mFormName))
      mFormName = getAbsoluteName();
    return mFormName;
  }

  //--------- Property: messageType -----------
  String mMessageType;

  /**
   * Sets the property messageType.  The type of the JMS message to be sent.
   * Default is the type specified in the Patch Bay definition file for form
   * submission events. (ie atg.das.FormSubmission)
   *
   * @beaninfo description: The type of the JMS message to be sent.
   */
  public void setMessageType(String pMessageType) {
    mMessageType = pMessageType;
  }

  /**
   * @return The value of the property messageType.
   */
  public String getMessageType() {
    if (StringUtils.isBlank(mMessageType))
      setMessageType("atg.das.FormSubmission");
    return mMessageType;
  }

  //--------- Property: messagePort -----------
  String mMessagePort;

  /**
   * Sets the property messagePort.  JMS port on which the message is sent. Default
   * is the port specified in the Patch Bay definition file for form submission events.
   *
   * @beaninfo description: The JMS port on which the message is sent.
   */
  public void setMessagePort(String pMessagePort) {
    mMessagePort = pMessagePort;
  }

  /**
   * @return The value of the property messagePort.
   */
  public String getMessagePort() {
    if (StringUtils.isBlank(mMessagePort))
      setMessagePort("FormSubmission");
    return mMessagePort;
  }

  
  //--------- Property: useForwards -----------

  //---------------------------------------------------------------------------
  // property: useForwards
  //---------------------------------------------------------------------------

  boolean mUseForwards;

  /**
   * Set whether to use forwards rather then redirects.
   */
  public void setUseForwards(boolean pUseForwards) {
    mUseForwards = pUseForwards;
  }

  /**
   * Get whether to use forwards rather then redirects
   */
  public boolean isUseForwards() {
    return mUseForwards;
  }

  //--------- Property: deferForwardsAndRedirects -----------

  //---------------------------------------------------------------------------
  // property: deferForwardsAndRedirects
  //---------------------------------------------------------------------------

  boolean mDeferForwardsAndRedirects;

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
  

  public Serializable createFormSubmissionMessage(DynamoHttpServletRequest pRequest,
                                                  DynamoHttpServletResponse pResponse) {
    String formName = getFormName();

    if (isLoggingDebug()) {
      logDebug("create FormSubmission message - form name = " + formName);
    }
    FormSubmissionMessage msg = new FormSubmissionMessage(formName);
    return msg;
  }

  //----------------------------------------------
  /**
   * Returns true if an error occurred while processing the form.
   *
   * @beaninfo description: True if an error occurred while processing the form
   */
  public boolean getFormError() {
    return mExceptions != null && mExceptions.size() > 0;
  }

  //----------------------------------------------
  /**
   * Returns the array of exceptions that occurred for display in the page
   *
   * @beaninfo description: List of exceptions that occurred during processing
   */
  public Vector getFormExceptions() {
    if (mExceptions == null) mExceptions = new Vector();
    return mExceptions;
  }

  //----------------------------------------------
  /**
   * Returns true if an error occurred while processing the form.
   *
   * @beaninfo description: True if an error occurred while processing the form
   */
  public boolean hasUncheckedFormExceptions() {
    return mUncheckedExceptions != null && !mUncheckedExceptions.isEmpty();
  }

  //----------------------------------------------
  /**
   * Returns the array of unchecked exceptions that occurred for display in the page
   *
   * @beaninfo description: List of uncheckedexceptions that occurred during processing
   */
  public List getUncheckedFormExceptions() {
    if (mUncheckedExceptions == null) mUncheckedExceptions = new ArrayList();
    return mUncheckedExceptions;
  }

  //--------- Property: RestorableForm -----------
  // property: RestorableForm
  boolean mRestorableForm = false;

  /**
   * Sets the property RestorableForm.  Set this property to true if this
   * form handler can properly process form results on a session which
   * has been restored using the session backup server.  This property is
   * only used by the isValidSession method.  That method gets used to
   * automatically detect an error and redirect to the error page when your
   * session has expired since the form was rendered.
   * <p/>
   * You should set this property to true, if you are using the
   * checkFormRedirect method in your handleX method, and all of the data
   * needed to process this form in your session is restored by the session
   * backup process.
   * <p/>
   * Note that if your form does not rely on any data in the session at
   * all for processing, you do not need to worry about setting this property.
   * Instead, make sure to set the checkForValidSession property to false.
   *
   * @param pRestorableForm new value to set
   * @beaninfo description:
   */
  public void setRestorableForm(boolean pRestorableForm) {
    mRestorableForm = pRestorableForm;
  }

  /**
   * @return The value of the property RestorableForm.
   */
  public boolean isRestorableForm() {
    return mRestorableForm;
  }

  //--------- Property: CheckForValidSession -----------
  // property: CheckForValidSession
  boolean mCheckForValidSession = false;

  /**
   * Sets the property CheckForValidSession.  If your form handler depends
   * on session information to be maintained between when the form was
   * rendered and when the form is submitted, you can set this property to
   * true to have this form handler automatically detect when a form submit
   * occurs on a new session.  This error checking only works if you use the
   * checkFormRedirect method in your handleX method.
   * <p/>
   * If you set this property to true and use the checkFormRedirect method,
   * it will add a form exception with the code "sessionExpired" and redirect
   * to the supplied errorURL if the session expired between when the form was
   * rendered and submitted.
   * <p/>
   * Note that if the session backup feature is enabled, you should be sure to
   * set the RestorableForm property to true if your form can work properly
   * on a restored session.
   *
   * @param pCheckForValidSession new value to set
   * @beaninfo description:
   */
  public void setCheckForValidSession(boolean pCheckForValidSession) {
    mCheckForValidSession = pCheckForValidSession;
  }

  /**
   * @return The value of the property CheckForValidSession.
   */
  public boolean getCheckForValidSession() {
    return mCheckForValidSession;
  }

  //----------------------------------------------
  /**
   * Returns true if this is a valid session for processing the form.
   * This is meant to be called during the form processing stage of the
   * request handling pipeline (i.e. from a set or handle method in the form).
   */
  public boolean isValidSession(DynamoHttpServletRequest pRequest) {
    HttpSession session = pRequest.getSession(false);
    if (session == null) return false;
    return (!session.isNew() || (mRestorableForm && (pRequest.getParameter(SessionSaverServlet.SESSION_RESTORED) != null)));
  }

  //----------------------------------------------
  /**
   * Adds a new exception to the list of FormExceptions.
   */
  public void addFormException(DropletException exc) {
    // validate inputs
    if (exc == null) {
      if (isLoggingError()) {
        logError(new IllegalArgumentException("The argument to addFormException cannot be null"));
      }
      return;
    }

    if (isLoggingDebug()) {
      logDebug("adding form exception: " + exc);
      logDebug("root cause of exception: " + exc.getRootCause());
    }

    if (mExceptions == null) mExceptions = new Vector();
    mExceptions.addElement(exc);

    /*
     * Also add this exception to the list of exceptions that occurred
     * globally for this request
     */
    DynamoHttpServletRequest request = mCurrentRequest;
    if (request != null) {
      NameContext ctx = request.getRequestScope();
      if (ctx != null) {
        Vector v = (Vector) ctx.getElement(DropletConstants.DROPLET_EXCEPTIONS_ATTRIBUTE);
        if (v == null) {
          v = new Vector();
          /* This is for compatibility */
          request.setAttribute(DropletConstants.DROPLET_EXCEPTIONS_ATTRIBUTE, v);
          /* This is so that exceptions are preserved across request scope */
          ctx.putElement(DropletConstants.DROPLET_EXCEPTIONS_ATTRIBUTE, v);
        }
        if (!v.contains(exc))
          v.addElement(exc);
      }
    }
  }

  //----------------------------------------------
  /**
   * Adds a new exception to the list of FormExceptions.
   */
  public void addUncheckedFormException(Throwable exc) {
    // validate inputs
    if (exc == null) {
      if (isLoggingError()) {
        logError(new IllegalArgumentException("The argument to addUncheckedFormException cannot be null"));
      }
      return;
    }

    if (isLoggingDebug()) {
      logDebug("adding unchecked form exception: " + exc);
      logDebug("root cause of exception (if any): " + exc.getCause());
    }

    if (mUncheckedExceptions == null) {
      mUncheckedExceptions = new ArrayList();
    }
    mUncheckedExceptions.add(exc);

    /*
     * Also add this exception to the list of exceptions that occurred
     * globally for this request
     */
    DynamoHttpServletRequest request = mCurrentRequest;
    if (request != null) {
      NameContext ctx = request.getRequestScope();
      if (ctx != null) {
        List uncheckedExceptions = (List) ctx.getElement(DropletConstants.UNCHECKED_DROPLET_EXCEPTIONS_ATTRIBUTE);
        if (uncheckedExceptions == null) {
          uncheckedExceptions = new ArrayList();
          /* This is for compatibility */
          request.setAttribute(DropletConstants.UNCHECKED_DROPLET_EXCEPTIONS_ATTRIBUTE, uncheckedExceptions);
          /* This is so that exceptions are preserved across request scope */
          ctx.putElement(DropletConstants.UNCHECKED_DROPLET_EXCEPTIONS_ATTRIBUTE, uncheckedExceptions);
        }
        if (!uncheckedExceptions.contains(exc))
          uncheckedExceptions.add(exc);
      }
    }
  }

  //----------------------------------------------
  /**
   * Clears out all form exceptions
   *
   * @beaninfo description: Clears out all form exceptions
   */
  public void resetFormExceptions() {
    // clear the form errors
    if (mExceptions != null)
      mExceptions.removeAllElements();

    // clear the unchecked exceptions
    if (mUncheckedExceptions != null)
      mUncheckedExceptions.clear();
  }


  //----------------------------------------------
  /**
   * Returns the propertyExceptions property.  This is a Dictionary
   * that returns the current form exceptions keyed off of the name of
   * the property that generated that exception.  A DynamicPropertyMapper
   * is registered for these exceptions so that you can access them as
   * properties in a jhtml page.  If there was no exception for a particular
   * property, a null value is returned for that property.
   *
   * @beaninfo description: A dictionary of properties that generated exceptions
   * expert: true
   */
  public Dictionary getPropertyExceptions() {
    if (mErrorContainer == null) mErrorContainer = new ErrorContainer(this);
    return mErrorContainer;
  }

  //--------- Property: CancelURL -----------
  String mCancelURL;

  /**
   * Sets the property CancelURL.  If this value is set to non-null,
   * we redirect to this URL when the handleCancel method is called.
   *
   * @beaninfo description: The URL to proceed to if cancellation is requested
   */
  public void setCancelURL(String pCancelURL) {
    mCancelURL = pCancelURL;
  }

  /**
   * @return The value of the property CancelURL.
   */
  public String getCancelURL() {
    return mCancelURL;
  }


  //--------------------------------------------
  /**
   * This method redirects to the value of the cancelURL property, if
   * that property is set to a non-null value.  You should override this
   * if you want to clear the form values when the form is cancelled for
   * a session scoped form handler.
   */
  public boolean handleCancel(DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    String cancel = getCancelURL();
    if (!StringUtils.isBlank(cancel)) {
      redirectOrForward(pRequest, pResponse, cancel);
      return false;
    }
    return true;
  }


  //----------------------------------------------
  /**
   * If NO form errors are found, redirect to the SuccessURL.
   * If form errors are found, redirect to the FailureURL.
   *
   * @param pNoErrorsURL The URL to redirect to if there were no form errors.
   *                     If a null value is passed in, no redirect occurs.
   * @param pErrorsURL   The URL to redirect to if form errors were found.
   *                     If a null value is passed in, no redirect occurs.
   * @param pRequest     the servlet's request
   * @param pResponse    the servlet's response
   * @return If redirect (for whatever reason) to a new page occurred,
   *         return false.  If NO redirect occurred, return true.
   * @throws ServletException if there was an error while executing the code
   * @throws IOException      if there was an error with servlet io
   */
  public boolean checkFormRedirect(String pSuccessURL,
                                   String pFailureURL,
                                   DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException {
    if (getCheckForValidSession() && !isValidSession(pRequest)) {
      addFormException(new DropletException("Your session expired since this form was displayed - please try again.", "sessionExpired"));
    }

    //If form errors were found:
    if (getFormError()) {

      //If FailureURL was not passed in, stay on same page, return true:
      if (StringUtils.isBlank(pFailureURL)) {
        if (isLoggingDebug()) {
          logDebug("error - staying on same page.");
        }
        return true;
      }

      //If FailureURL was passed in, redirect and return false:
      else {
        if (isLoggingDebug()) {
          logDebug("error - redirecting to: " + pFailureURL);
        }
        redirectOrForward(pRequest, pResponse, pFailureURL);
        return false;
      }
    }

    //If no form errors were found:
    else {

      //If SuccessURL was not passed in, stay on same page, return true:
      if (StringUtils.isBlank(pSuccessURL)) {
        if (isLoggingDebug()) {
          logDebug("no form errors - staying on same page.");
        }
        return true;
      }

      //If SuccessURL was passed in, redirect and return false:
      else {
        if (isLoggingDebug()) {
          logDebug("no form errors - redirecting to: " + pSuccessURL);
        }
        redirectOrForward(pRequest, pResponse, pSuccessURL);
        return false;
      }
    }
  }


  /**
   * Forward or redirect, as required.
   *
   * @param pRequest  the servlet's request
   * @param pResponse the servlet's response
   * @param pURL      the url to redirect or forward to.
   */
  protected void redirectOrForward(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse,
                                   String pURL) throws IOException, ServletException {
    boolean bUseForwards = isUseForwards();
    String strUseForwards = pRequest.getParameter(PARAM_USE_FORWARDS);
    if (strUseForwards != null) {
      if (strUseForwards.equalsIgnoreCase("true")) {
        bUseForwards = true;
      } else {
        bUseForwards = false;
      }
    }

    if (bUseForwards) {
      RequestDispatcher rd = pRequest.getRequestDispatcher(pURL);
      rd.forward(pRequest, pResponse);
    } else {
      pResponse.sendLocalRedirect(pURL, pRequest);
    }
  }


} //Class GenericFormHandler

//----------------------------------------------

/**
 * This class implements a container of read-only properties that
 * let the caller query error conditions for each property in the
 * system.  This is to implement error displays next to the form
 * field that caused the error.
 */
class ErrorContainer extends FormHashtable {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/GenericFormHandler.java#2 $$Change: 651448 $";

  static final long serialVersionUID = -1656307579973941948L;

  transient GenericFormHandler mFormHandler;

  ErrorContainer(GenericFormHandler pFormHandler) {
    mFormHandler = pFormHandler;
  }

  public Object get(Object pName) {
    if (!(pName instanceof String)) return null;
    if (mFormHandler != null && mFormHandler.mExceptions != null) {
      for (int i = 0; i < mFormHandler.mExceptions.size(); i++) {
        DropletException exc = (DropletException) mFormHandler.mExceptions.elementAt(i);
        if (exc instanceof DropletFormException) {
          DropletFormException fexc = (DropletFormException) exc;
          if (((String) pName).equalsIgnoreCase(fexc.getPropertyName()))
            return fexc;
        }
      }
    }
    return null;
  }

  public Object put(Object pKey, Object pName) {
    throw new IllegalArgumentException("can't put into read-only property container");
  }

  public void clear() {
    throw new IllegalArgumentException("can't clear read-only property container");
  }

  public int size() {
    int ct = 0;
    if (mFormHandler == null || mFormHandler.mExceptions == null) return 0;
    for (int i = 0; i < mFormHandler.mExceptions.size(); i++) {
      if (mFormHandler.mExceptions.elementAt(i) instanceof DropletFormException)
        ct++;
    }
    return ct;
  }

  public Object remove(Object pKey) {
    throw new IllegalArgumentException("can't modify into read-only property container");
  }


  //----------------------------------------------
  /**
   * This class implements the Enumeration for stepping through the
   * propertiesExceptions either by elements or keys.  One weird thing
   * is that the array we are referencing can contain both
   * DropletExceptions, which do not have a property name and thus do not
   * have a corresponding entry in the propertiesExceptions table.  We
   * skip those in the hasMoreElements() method.
   */
  abstract class ErrorEnumeration implements Enumeration, Iterator {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/GenericFormHandler.java#2 $$Change: 651448 $";

    int mErrorIndex = 0;

    public boolean hasMoreElements() {
      if (mFormHandler == null || mFormHandler.mExceptions == null)
        return false;
      while (mErrorIndex < mFormHandler.mExceptions.size() &&
        !(mFormHandler.mExceptions.elementAt(mErrorIndex) instanceof DropletFormException))
        mErrorIndex++;
      return mErrorIndex < mFormHandler.mExceptions.size();
    }

    // Iterator methods
    public boolean hasNext() {
      return hasMoreElements();
    }

    public Object next() {
      return nextElement();
    }

    public void remove() {
      throw new IllegalArgumentException("Can't remove values from read-only ErrorContainer");
    }
  }

  abstract class ErrorSet extends AbstractSet {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/GenericFormHandler.java#2 $$Change: 651448 $";

    public int size() {
      return ErrorContainer.this.size();
    }

    public boolean contains(Object o) {
      return containsKey(o);
    }

    public boolean remove(Object o) {
      return ErrorContainer.this.remove(o) != null;
    }

    public void clear() {
      ErrorContainer.this.clear();
    }
  }

  class EntrySet extends ErrorSet {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/GenericFormHandler.java#2 $$Change: 651448 $";

    public Iterator iterator() {
      return new ErrorEnumeration() {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/GenericFormHandler.java#2 $$Change: 651448 $";

        public Object nextElement() {
          if (!hasMoreElements()) throw new NoSuchElementException();
          DropletFormException ex = (DropletFormException) mFormHandler.mExceptions.elementAt(mErrorIndex++);
          return new MapEntry(ex.getPropertyName(), ex);
        }
      };
    }
  }

  class KeySet extends ErrorSet {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/GenericFormHandler.java#2 $$Change: 651448 $";

    public Iterator iterator() {
      return new ErrorEnumeration() {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/GenericFormHandler.java#2 $$Change: 651448 $";

        public Object nextElement() {
          if (!hasMoreElements()) throw new NoSuchElementException();
          DropletFormException ex = (DropletFormException) mFormHandler.mExceptions.elementAt(mErrorIndex++);
          return ex.getPropertyName();
        }
      };
    }
  }

  class ValueCollection extends AbstractCollection {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/GenericFormHandler.java#2 $$Change: 651448 $";

    public Iterator iterator() {
      return new ErrorEnumeration() {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/GenericFormHandler.java#2 $$Change: 651448 $";

        public Object nextElement() {
          if (!hasMoreElements()) throw new NoSuchElementException();
          DropletFormException ex = (DropletFormException) mFormHandler.mExceptions.elementAt(mErrorIndex++);
          return ex;
        }
      };
    }

    public int size() {
      return ErrorContainer.this.size();
    }

    public boolean contains(Object o) {
      return containsValue(o);
    }

    public void clear() {
      ErrorContainer.this.clear();
    }
  }

  public Enumeration keys() {
    return new ErrorEnumeration() {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/GenericFormHandler.java#2 $$Change: 651448 $";

      public Object nextElement() {
        if (!hasMoreElements()) throw new NoSuchElementException();
        return ((DropletFormException) mFormHandler.mExceptions.elementAt(mErrorIndex++)).getPropertyName();
      }
    };
  }

  public Enumeration elements() {
    return new ErrorEnumeration() {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/GenericFormHandler.java#2 $$Change: 651448 $";

      public Object nextElement() {
        if (!hasMoreElements()) throw new NoSuchElementException();
        return mFormHandler.mExceptions.elementAt(mErrorIndex++);
      }
    };
  }

  public boolean isEmpty() {
    return !keys().hasMoreElements();
  }

  // Returns true if this contains a value for this key.  We don't store
  // null values so this is easy.
  public boolean containsKey(Object pKey) {
    return get(pKey) != null;
  }

  public Set entrySet() {
    return new EntrySet();
  }

  public Set keySet() {
    return new KeySet();
  }

  public Collection values() {
    return new ValueCollection();
  }

} //Class ErrorContainer (internal class to GenericFormHander)
