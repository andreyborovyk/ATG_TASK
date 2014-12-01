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
import atg.core.util.ResourceUtils;
import atg.core.exception.BadValueException;

import atg.naming.*;
import atg.nucleus.*;
import atg.nucleus.logging.*;
import atg.servlet.*;
import atg.core.net.URLUtils;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.net.URLEncoder;
import java.text.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * This class implements functionality of an HTML form tag.  A FormTag
 * has Java Bean properties for each HTML attribute.  It stores its content
 * as Strings, Servlets and other Tags. 
 * 
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/FormTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public
class FormTag extends EventSender {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/FormTag.java#2 $$Change: 651448 $";
  
  static final String MY_RESOURCE_NAME = "atg.droplet.DropletResources";

  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  //-------------------------------------
  public final static String TAG_NAME = "form";

  //-------------------------------------
  /** Values for the setMethod property */
  public final static String METHOD_GET = "get";
  public final static String METHOD_POST = "post";

  /** 
   * These are the strings to put into hidden tags to mark special
   * values of the fields default, type, and path.
   */
  /** Is the default the same as the static value */
  public final static String DEFAULT_IS_STATIC = " ";

  /** Is the default the same as the static value... sometimes
   * MS IE puts in space followed by a null... no idea why. See bug 62211. */
  public final static String BOGUS_DEFAULT_IS_STATIC_ON_IE = " \0";

  /** Is the default null? */
  public final static String DEFAULT_IS_NULL = ".";
  /** Is the submit value null? */
  public final static String SUBMIT_VALUE_IS_NULL = ".";
  /** Is the type null? */
  final static String TYPE_IS_NULL = ".";
  /** Is the path name the same as the name of the tag? */
  final static String PATH_IS_NAME = ".";

  /** The number of form elements we should have before we print a warning */
  final static int MAX_NUM_TAGS = 501;

  /** Flat used by dsp jsp to set whether our output is XML. */
  public final static String OUTPUT_IS_XML = "atg.taglib.dspjsp.OutputIsXML";
  

  //-------------------------------------
  // Member variables

  //-------------------------------------
  /**
   * A unique string name to use for identifying this form
   */
  public String mFormId = null;

  /**
   * If not null, the path name of a component to synchronize on before
   * delivering form arguments
   */
  public String mSyncPath = null;
  
  //-------------------------------------
  /* The DropletEventServlet for the current request */
  DropletEventServlet mServlet = null;
  
  //-------------------------------------
  /** A hashtable that maps property names to FormEventReceiverss */
  Dictionary mFormEventReceivers = new Hashtable();

  //-------------------------------------
  /** True if this form needs to handle events */
  boolean mNeedsEvents = false;

  /** 
   * A vector that stores FormEventReceivers in the order in which they 
   * were added.
   */
  Vector mTagsIndex = new Vector();

  //-------------------------------------
  /** 
   * A list of the hash table keys of the FormEventReceivers stored 
   * If null, the mFormEventReceivers list is the order to use.
   */
  SortedTags mTagOrder = null; 
  
  //---------------------------
  /** The listing of action **/
  Set mActionURL = Collections.synchronizedSet(new HashSet());

  //-------------------------------------
  String mAction = null;

  //-------------------------------------
  String mEncType = null;

  //-------------------------------------
  String mMethod = null;


  //-------------------------------------
  boolean mRequiresSessionConfirmation = true;
  
  public FormTag (String pFormId, DropletEventServlet pServlet) {
    this(pFormId, pServlet, null);
  }

  //-------------------------------------
  public FormTag (String pFormId, DropletEventServlet pServlet, String pSyncPath) {
    super(pServlet, pServlet.getReportDropletExceptions());
    mFormId = pFormId;
    mSyncPath = pSyncPath;
    mServlet = pServlet;    

    if (mFormId != null) {
      pServlet.addEventSender(mFormId, this);
    }

    /*
     * This is the current form for all tags below it
     */
    setForm(this);
  }

  //-------------------------------------
  public FormTag () {
    /*
     * This is the current form for all tags below it
     */
    setForm(this);
  }
  
  //-------------------------------------
  /**
   * Adds a new action/anchor to the list
   * @param pActionURL the action to be added
   */
  public void addActionURL (String pActionURL){
    mActionURL.add (pActionURL);
  }
  
  //-------------------------------------
  /**
   * Returns true if the attribute with the specified name exists,
   * false if not.
   */
  public boolean hasActionURL (String pActionURL) {
    boolean bResult;
    if ( numActions () > 0 ) {
      bResult = mActionURL.contains(pActionURL);
      //if an action ends with a "/" or a "." any file in that directory should be allowed
      if (!bResult){ 
        String str;
        // iterate through all URL stored in mActionURL
        Iterator iter = mActionURL.iterator();
        while (iter.hasNext())
        {
          str = (String)iter.next();
          if (str.endsWith("/") | str.endsWith(".")) {
             // now check if the directory of the action URL and the directory of the storedAction url are the same if they are return true
              if (str.equals(pActionURL.substring(0,pActionURL.lastIndexOf("/")+ 1))) return true;
          }
        } // while (iter.hasNext())  
      } // if (!bResult)
      return bResult;
    }
    return true;  // previous pagecompiled page also work
  }

  //-------------------------------------
  /**
   * Returns the number of actions
   */
  public int numActions () {
    return mActionURL.size ();
  }

  //-------------------------------------
  /**
   * Returns the actionsURL
   */
  public Set getActionURL(){
    return mActionURL;
  }

  //-------------------------------------
  // Define all of the legal attributes for the FORM tag 

  //-------------------------------------
  public void setAction(String pAction) {
    mAction = pAction;
  }

  //-------------------------------------
  public String getAction() {
    return mAction;
  }

  //-------------------------------------
  public void setMethod(String pMethod) {
    mMethod = pMethod;
  }

  //-------------------------------------
  public String getMethod() {
    return mMethod;
  }

  //-------------------------------------
  public void setEncType(String pEncType) {
    mEncType = pEncType;
  }

  //-------------------------------------
  public String getEncType() {
    return mEncType;
  }

  //-------------------------------------
  public String getTagName() {
    return TAG_NAME;
  }

  //-------------------------------------
  public FormTag getTag (String pName) {
    return (FormTag) mFormEventReceivers.get(pName);
  }


  /** Return the FormEventReceiver with the specified name.
   * @param pName the name of the FormEventReceiver to return
   * @return the receiver with the specified name, if found, null otherwise.
   */
  public FormEventReceiver getFormEventReceiver(String pName) {
    return (FormEventReceiver)mFormEventReceivers.get(pName);
  }

  //-------------------------------------
  /**
   * Returns true if the form has no stuff in it.
   */
  public boolean needsEvents() {
    return mNeedsEvents || !mFormEventReceivers.isEmpty();
  }

  //-------------------------------------
  /**
   * Makes this form handle events
   */
  public void setNeedsEvents(boolean pNeedsEvents) {
    mNeedsEvents = pNeedsEvents;
  }

  //-------------------------------------
  /** Sets the path name of the component used to synchronize this form. */
  public void setSynchronized(String pSync) {
    mSyncPath = pSync;
  }

  //-------------------------------------
  public String getSynchronized() {
    return mSyncPath;
  }

  //---------------------------------------------------------------------------
  // property: requiresSessionConfirmation

  /** Whether this form requires session confirmation. Defaults to true. */
  public void setRequiresSessionConfirmation(boolean pRequiresSessionConfirmation) {
    mRequiresSessionConfirmation = pRequiresSessionConfirmation;
  }

  /** Whether this form requires session confirmation. Defaults to true. */  
  public boolean getRequiresSessionConfirmation() {
    return mRequiresSessionConfirmation;
  }
  

  //-------------------------------------
  String getEventReceiverName(EventReceiver pReceiver) {
    String name;
    if ((name = pReceiver.getName()) == null) {
      name = pReceiver.getPropertyPath();
    }
    return name;
  }

  //-------------------------------------
  /**
   * This method is called by the event receiver.  When it is added to 
   * some content, it searches up the hierarchy until it finds a Form 
   * and it adds itself to us.
   */
  public synchronized void addFormEventReceiver(FormEventReceiver pReceiver) {
    String name = getEventReceiverName(pReceiver);
    FormEventReceiver oldReceiver;

    if ((oldReceiver = (FormEventReceiver) mFormEventReceivers.get(name)) != null) {
      if (oldReceiver == pReceiver) return;

      /*
       * This will notify the EventReceiver (input, select, etc) that 
       * it no longer has us as the form.  It will then remove itself
       * as our event receiver so that we don't have to do so explicitly.  
       */
      oldReceiver.setForm(null);
    }
    mFormEventReceivers.put(name, pReceiver);

    mTagsIndex.addElement(pReceiver);
      			    
    /*
     * By default, the handler order is based on the order in which
     * it was added.  We only need to update the handler order
     * when we see the first guy that has a non-default priority.
     */
    if (pReceiver.getPriority() != PRIORITY_DEFAULT || mTagOrder != null)
      updateTagOrder(name);
  }

  //-------------------------------------
  public synchronized void removeEventReceiver(EventReceiver pEventReceiver) {
    String name = getEventReceiverName(pEventReceiver);
    /*
     * Be silent on errors since this EventReceiver could have been removed
     * already if this EventReceiver was deleted already due to a duplicate 
     * name entry
     */
    mFormEventReceivers.remove(name);
    mTagsIndex.removeElement(pEventReceiver);
    if (mTagOrder != null) mTagOrder.removeElement(name);
  }

  //-------------------------------------
  /**
   * Adds a new tag to this form.  This method is used by the page compiler
   * to add/update an entry in the event receiver table.
   */
  public FormEventReceiver addTag (DynamoHttpServletRequest pRequest,
  				   DynamoHttpServletResponse pResponse,
  				   String pName, 
  		                   String pPropertyPath, String pType,
  		      		   String pDefault, int pPriority) 
                              throws IOException, ServletException {
    return addTag(pRequest, pResponse, pName, pPropertyPath, pType,
    	          pDefault, pPriority, null, null, null);
  }

  //-------------------------------------
  /**
   * This version is to remain compatible with old compiled pages that
   * needed to explictily tell us whether to use the Writer or OutputStream.
   * Now, we can just always use the OutputStream and it will work even
   * if the request output is going through a Writer.
   */
  public FormEventReceiver addTag (DynamoHttpServletRequest pRequest,
  				   DynamoHttpServletResponse pResponse,
  				   String pName, 
  		                   String pPropertyPath, String pType,
  		      		   String pDefault, int pPriority,
  		      		   String pSubmitValue,
  		      		   boolean pUseWriter) 
                             throws IOException, ServletException {
     return addTag(pRequest, pResponse, pName, pPropertyPath, pType, pDefault,
     	           pPriority, pSubmitValue, null, null);
  }

  //-------------------------------------
  /**
   * Adds a new tag to this form.  This method is used by the page compiler
   * to add/update an entry in the event receiver table.  This version of
   * the method takes a "submitvalue" (null for no submit).
   */
  public FormEventReceiver addTag (DynamoHttpServletRequest pRequest,
  				   DynamoHttpServletResponse pResponse,
  				   String pName, 
  		                   String pPropertyPath, String pType,
  		      		   String pDefault, int pPriority,
  		      		   String pSubmitValue,
  		      		   TagConverter pConverter,
  		      		   Properties pConverterArgs) 
                              throws IOException, ServletException {
     return addTag(pRequest, pResponse, pName, pPropertyPath, pType, pDefault,
     	           pPriority, pSubmitValue, pConverter, pConverterArgs, false);
  }


  //-------------------------------------
  /**
   * Adds a new tag to this form.  This method is used by the page compiler
   * to add/update an entry in the event receiver table.  This version of
   * the method takes a "submitvalue" (null for no submit).
   */
  public FormEventReceiver addTag (DynamoHttpServletRequest pRequest,
  				   DynamoHttpServletResponse pResponse,
  				   String pName, 
  		                   String pPropertyPath, String pType,
  		      		   String pDefault, int pPriority,
  		      		   String pSubmitValue,
  		      		   TagConverter pConverter,
  		      		   Properties pConverterArgs,
                                   boolean pUsePostField) 
      throws IOException, ServletException {
      /*
       * If this property path has dimensions specified, we need to first
       * convert the symbolic values of the dimensions to integer values
       */
      //    pPropertyPath = DropletDescriptor.evalDynamicDimensions(pPropertyPath,
      //      pRequest);
      /*
       * If no name is specified, just use the property path name 
       */
      if (pName == null) {
	  pName = pPropertyPath;
      }
      
      // Register for events
      FormEventReceiver mt = addTag(pName,pPropertyPath,pType,pDefault,
				    pPriority,pSubmitValue,pConverter,pConverterArgs);
      
      /* Check if the current requests MimeType is an XML TYPE */
      boolean XMLMimetype = isXMLMimeType(pRequest);
      /*
       * We need to be very careful not to synchronize around this stuff
       * or else we'll potentially tie up the entire server while we flush
       * data to the DRP server and wait for a response.
       */
      ServletOutputStream out = pResponse.getOutputStream();
      if (pUsePostField)
	  out.print("<postfield name=\"");
      else
	  out.print("<input type=\"hidden\" name=\"");
      out.print(DROPLET_EVENT_PREFIX);
      out.print(pName);
      out.print("\" value=\"");
      if (pDefault == null) {
	  if (mt.getDefault() == null)
	      out.print(DEFAULT_IS_STATIC);
	  else
	      out.print(DEFAULT_IS_NULL);
      }
      else if (!pDefault.equals(mt.getDefault()))
	  out.print(URLUtils.escapeUrlString(pDefault));
      else 
	  out.print(DEFAULT_IS_STATIC);
      
      if (pUsePostField || XMLMimetype)
	  out.print("\"/>");
      else
	  out.print("\">");
      
      if (mt.getSubmitValue() != pSubmitValue) {
	  if (pSubmitValue != null) mt.setMayHaveSubmitValue(true);
	  out.print("<input type=\"hidden\" name=\"");
	  out.print(DROPLET_SUBMIT_VALUE_PREFIX);
	  out.print(pName);
	  out.print("\" value=\"");
	  if (pSubmitValue == null)
	      out.print(SUBMIT_VALUE_IS_NULL);
	  else 
	      out.print(URLUtils.escapeUrlString(pSubmitValue));
	  if ( XMLMimetype)
	      out.println("\"/>");
	  else
	      out.println("\">");
      }
      return mt;
  }

  //-------------------------------------
  /**
   * Adds a new tag to this form.  This method is used by the page compiler
   * to add/update an entry in the event receiver table.  This version of
   * the method takes a "submitvalue" (null for no submit).
   */
  public FormEventReceiver addTag(String pName, 
				  String pPropertyPath, String pType,
				  String pDefault, int pPriority,
				  String pSubmitValue,
				  TagConverter pConverter,
				  Properties pConverterArgs) 
      throws ServletException {
      /*
       * If no name is specified, just use the property path name 
       */
      if (pName == null) {
	  pName = pPropertyPath;
      }

      FormEventReceiver mt;
      boolean logWarning = false;
      int size = 0;
      synchronized (this) {
	  if ((mt = (FormEventReceiver) mFormEventReceivers.get(pName)) == null) {
	      mt = new FormEventReceiver(pName, pPropertyPath, 
					 pType, pDefault, pPriority, pSubmitValue,
					 pConverter, pConverterArgs);
	      mFormEventReceivers.put(pName, mt);
	      
	      size = mFormEventReceivers.size();
	      if (size % MAX_NUM_TAGS == MAX_NUM_TAGS -1)
		  logWarning = true;
	      
	      if (mLogger != null && mLogger.isLoggingDebug())
		  mLogger.logDebug("adding new entry for form=" + this + " name=" +
				   pName + " property=" + pPropertyPath);
	      
	      mTagsIndex.addElement(mt);
	      
	      /*
	       * By default, the handler order is based on the order in which
	       * it was added.  We only need to update the handler order
	       * when we see the first guy that has a non-default priority.
	       */
	      if (pPriority != PRIORITY_DEFAULT || mTagOrder != null)
		  updateTagOrder(pName);
	  }
	  /*
	   * Update the converters to reflect the new state.  We're not maintaining
	   * different values for this, but at least we can update them if the
	   * user changes it.
	   */
	  else {
	      mt.setConverter(pConverter);
	      mt.setConverterArgs(pConverterArgs);
	      mt.setType(pType); // gnat 12189 - update type if it has changed.
	  }
      }
      
      if (logWarning && mLogger != null && mLogger.isLoggingWarning())
	  mLogger.logWarning("Added " + size + " form elements to form " + mFormId +
			     " last element has name " + pName + 
			     ".  Form elements are registered permanently so if this table keeps " +
			     "growing, it is a memory leak.  Recode your page to use consistent names for form elements.");
      return mt;     
  }

  /**
   * Are these strings equals where matching null strings are considered
   * a match
   */
  boolean equalOrNull(String pStr1, String pStr2) {
    if (pStr1 == pStr2) return true;
    if (pStr1 == null || pStr2 == null) return false;
    return pStr1.equals(pStr2);
  }

  /**
   * Are these strings equalsIgnoreCase where matching null strings are 
   * considered a match
   */
  boolean equalIgnoreCaseOrNull(String pStr1, String pStr2) {
    if (pStr1 == pStr2) return true;
    if (pStr1 == null || pStr2 == null) return false;
    return pStr1.equalsIgnoreCase(pStr2);
  }

  //-------------------------------------
  /** 
   * Called when we change the Droplet handler list.  It updates the
   * mapping that we have to determine the proper handler order based
   * on the priority.
   */
  protected synchronized void updateTagOrder(String pNewName) {
    if (mTagOrder == null) {
      int size = mTagsIndex.size();

      mTagOrder = new SortedTags(size, mFormEventReceivers);

      /*
       * First start off assuming that the order of handlers is the
       * same as the order in which they were added to the list
       */
      for (int i = 0; i < size; i++) {
        mTagOrder.addElement(getEventReceiverName(
        		            (EventReceiver)mTagsIndex.elementAt(i)));
      }
    }
    else {
      mTagOrder.addElement(pNewName);
    }
    mTagOrder.sortTags();
  }

  boolean sendEvents (DynamoHttpServletRequest pReq,
  		      DynamoHttpServletResponse pRes) 
         throws DropletException, ServletException, IOException {
    if (mSyncPath != null) {
      Object obj = pReq.resolveName(mSyncPath);
      if (obj == null) {
	Object[] mPatternArgs = {mSyncPath};
        throw new DropletException(ResourceUtils.getMsgResource
				   ("formTagUnableToFindComponent", 
				    MY_RESOURCE_NAME, sResourceBundle, mPatternArgs));
      }
      if (mLogger != null && mLogger.isLoggingDebug())
        mLogger.logDebug("form synchronizing on object with path=" + mSyncPath);
      synchronized (obj) {
        return doSendEvents(pReq, pRes);
      }
    }
    else return doSendEvents(pReq, pRes);
  }

  //-------------------------------------
  boolean doSendEvents (DynamoHttpServletRequest pReq,
  		        DynamoHttpServletResponse pRes) 
         throws DropletException, ServletException, IOException {
    boolean status = true;
    /*
     * If there are no tags registered for this page, return before
     * we end up processing the form arguments
     */
    if (!needsEvents()) {
      return true;
    }

    if (mServlet == null)
      mServlet = (DropletEventServlet) pReq.getAttribute(DROPLET_EVENT_ATTRIBUTE);

    if (mServlet == null)
      return false;
    

    if (getRequiresSessionConfirmation() &&
        !mServlet.validateSessionConfirmationNumber(pReq)) {
      // don't do any processing
      pRes.sendError(
        409, "Session timed out. Please use the back button, reload and try again.");
      return false;
    }

    Vector handlers;
    FormEventReceiver mt;

    if (mTagOrder == null) handlers = mTagsIndex;
    else handlers = mTagOrder;

    Object [] setArgs = new Object[1];
    FormEventReceiver dynMt = null;
    Vector beforeSetObjects = new Vector(3);

    int size = handlers.size();

    // Defer dispatching of forwards and redirects until after
    // form handling completes.  See bugs 48870 and 140798
    DeferredDispatcher deferredDispatcher = null;
    boolean deferralEnabledLocally = mServlet.isDeferForwardsAndRedirects();
    
    if(!deferralEnabledLocally) {
      // Check form to see if forward/redirect deferral is enabled for
      // this request or any event form handlers executed in this request
      String strDeferForwards = pReq.getParameter(GenericFormHandler.PARAM_DEFER_FORWARD_OR_REDIRECT);

      if ("true".equalsIgnoreCase(strDeferForwards)) {
        deferralEnabledLocally = true;
      }else{
        for (int i = 0; i < size && !deferralEnabledLocally; i++) {
          // check each event receiver to see if it has enabled 
          // deferred forwards.
          if (mTagOrder == null) mt = (FormEventReceiver) handlers.elementAt(i);
          else mt = ((SortedTags) handlers).getFormEventReceiverAt(i);
          String [] pathNames = mt.getPathNames();
          Object component = pReq.resolveName(pathNames[0]);

          if(component instanceof GenericFormHandler)
            deferralEnabledLocally = ((GenericFormHandler)component).isDeferForwardsAndRedirects();
        }
      }
    }
    
    if(deferralEnabledLocally){
      deferredDispatcher = DeferredDispatcher.getInstance();
      if(deferredDispatcher.isDeferralEnabled()){
        // someone else has already started deferral
        // they are responsible for the dispatch life-cycle
        deferralEnabledLocally = false;
      }else{
        // Start defering dispatch of redirects and forwards
        deferredDispatcher.enableDeferral();
      }
    }
    
    try{
      try {
        for (int i = 0; i < size; i++) {
          if (mTagOrder == null) mt = (FormEventReceiver) handlers.elementAt(i);
          else mt = ((SortedTags) handlers).getFormEventReceiverAt(i);

          String infoStr;
          /*
           * We look for the DROPLET_EVENT_PREFIX for this tag before we process
           * the values in the form.  If this <input> tag was rendered, for this
           * page submission, the hidden tag should be there.  If not, we skip it.
           * This prevents us from processing entries that we not rendered with
           * this form submission.
           */ 
          if ((infoStr = pReq.getParameter(DROPLET_EVENT_PREFIX + mt.getName())) != null) {
            // unused int len;
            String submitValue;
            boolean dynamicSubmitValue = false;

            /*
             * If this tag has ever been rendered with a submit value, we
             * check to see if there is a special submit value to use 
             */
            if (mt.getMayHaveSubmitValue()) {
              /*
               * If this tag was rendered with an explicit submit value, we use that one.
               * Otherwise, we use the "static" version.
               */
              if ((submitValue = pReq.getParameter(DROPLET_SUBMIT_VALUE_PREFIX + mt.getName())) != null) {
                dynamicSubmitValue = true;
                /*
                 * It is possible that the static version is not null and the value
                 * rendered for this invocation is null
                 */
                if (submitValue.equals(SUBMIT_VALUE_IS_NULL)) submitValue = null;
                else submitValue = URLUtils.unescapeUrlString(submitValue);
              }
              else submitValue = mt.getSubmitValue();
            }
            else submitValue = null;

            /*
             * If we have any "dynamic" content (i.e. stuff that does not
             * match the static version), we have to build a special receiver. 
             */
            if (!isDefaultStaticValue(infoStr) || dynamicSubmitValue) {
              String defaultValue = infoStr;
              /*
               * Choose a character that will get URLEncoded so that we can
               * recognize the defaultValue
               */
              if (isDefaultStaticValue(DEFAULT_IS_STATIC)) defaultValue = mt.getDefault();
              else if (defaultValue.equals(DEFAULT_IS_NULL)) defaultValue = null;
              else defaultValue = URLUtils.unescapeUrlString(defaultValue);

              /*
               * Use one FormEventReceiver for all requests to reduce memory
               * allocations
               */
              if (dynMt == null) 
                dynMt = new FormEventReceiver();

              dynMt.setFormAttributes(mt.getPropertyPath(), mt.getType(), 
                                      defaultValue, mt.getPriority(), submitValue);
              dynMt.setName(mt.getName());
              mt = dynMt;
            }

            try {
              if (!sendEvent(pReq, pRes, mt, setArgs, beforeSetObjects)) 
                return false;
            }
            catch (DropletException e) {
              if (getReportDropletExceptions()) throw e;
              else addFormException(pReq, pRes, e, null);
            }
          }
        }
      }
      finally {
        /*
         * Calls any afterSet methods.  Make sure to call them, but we 
         * may already be returning false for some other reason above so
         * it is not necessary to return again here.
         */
        if (!callAfterSet(pReq, pRes, beforeSetObjects))
          status = false;
      }
    }finally{
      /*
       * If any of the handle methods we were invoking requested a differed forward/redirect,
       * then we will execute the forward here, now that afterSet has been called.
       * Deferred forwards/redirects allow form handlers (often those that manage their own transactions)
       * to forward/redirect after the afterSet method has been called (once the transaction has completed).  
       * A common design pattern is to create and end a transaction in before 
       * and after set, respectively.  If a form handler requests a forward inside that 
       * transaction, then we don't necessarily want to render the forward request in 
       * the same transaction.  This is especially true if the form handler has rolled 
       * back the transaction.  Also, deferring the forward until after the transaction 
       * completes insulates the work the form handler performed from a rollback set 
       * by the forwarded page.
       * Likewise, with redirects, if the redirect is executed immediately, the subsequent
       * request can be rendered before the initiating transaction has been committed.
       */
      if(deferredDispatcher != null && deferralEnabledLocally){
        /* 
         * If we were the one who turned on deferred dispatch, 
         * then we should execute the forward/redirect now, otherwise
         * if someone turned it on before we got here, it is their
         * responsibility to execute the dispatch.
         */
        deferredDispatcher.dispatch();
      }
    }
    return status;
  }

  /**
   * Renders the tag and its content.
   */
  public void service(DynamoHttpServletRequest pReq, 
                      DynamoHttpServletResponse pRes) 
     throws ServletException, IOException {
    ServletOutputStream out = pRes.getOutputStream();
    boolean queryArgs = false;
    boolean doEvents = false;
    boolean isGet;

    String formId = mFormId;
    if (needsEvents()) {
      if (formId == null)
        formId = getMyFormId(pReq.getPathInfo(), pReq);

      doEvents = true;
    }

    if (mMethod == null || mMethod.equalsIgnoreCase("get"))
      isGet = true;
    else {
      isGet = false;

      if (doEvents) {
        pReq.addQueryParameter(DROPLET_ARGUMENTS, formId);
        pReq.addQueryParameter(DROPLET_SESSION_CONF,
                               Long.toString(
                                 pReq.getSessionConfirmationNumber())); 
        queryArgs = true;
      }
    }
    out.print("<form");
    if (mMethod != null) {
      out.print(" method=");
      out.print(mMethod);
    }
    if (mAction != null) {
      out.print(" action=\"");
      out.print(pReq.encodeURL(mAction));
      out.print('"');
    }
    else if (queryArgs) {
      out.print(" action=\"");
      out.print(pReq.encodeURL("."));
      out.print('"');
    }
    if (mEncType != null) {
      out.print(" enctype=\"");
      out.print(mEncType);
      out.print('"');
    }
    serviceAttributes(pReq, pRes);
    out.println('>');
    if (isGet && doEvents) {
      out.print("<input type=\"hidden\" name=\"");
      out.print(DROPLET_ARGUMENTS);
      out.print("\" value=\"");
      out.print(formId); 
      if ( isXMLMimeType(pReq))
        out.println("\"/>");
      else
        out.println("\">");

      out.print("<input type=\"hidden\" name=\"");
      out.print(DROPLET_SESSION_CONF);
      out.print("\" value=\"");
      out.print(Long.toString(pReq.getSessionConfirmationNumber())); 
      if ( isXMLMimeType(pReq))
        out.println("\"/>");
      else
        out.println("\">"); 
      
    }
    serviceContent(pReq, pRes);
    out.println("</form>");
  }
  
  /* Check if the requests mimeType is a configured as an XML type.
  */
  public boolean isXMLMimeType(DynamoHttpServletRequest pReq)
  {
    
    Boolean boolXML = (Boolean)pReq.getAttribute(OUTPUT_IS_XML);

    if (Boolean.TRUE.equals(boolXML)) {
      return true;
    }
    
    if (mServlet == null)
    	mServlet = (DropletEventServlet) pReq.getAttribute(DROPLET_EVENT_ATTRIBUTE);
    
    if (mServlet == null)
      return false;
    
    String[] XMLTypes = mServlet.getXmlMimeTypes();
    
    if (XMLTypes == null){
      return false;
    }  
    String currentMimeType = pReq.getMimeType();
    for ( int i =0; i < XMLTypes.length; i++){
      if ( currentMimeType.equals(XMLTypes[i]))
         return true;
    }
    return false;
   }  

  //-------------------------------------
  /*
   * Returns the name of this FormTag.  The first time a FormTag is
   * rendered with a particular pathInfo, it is registered into the
   * global dispatcher handler and assigned an ID.  This table is static
   * since the form can be requested at any time later on.
   */
  String getMyFormId(String pPathInfo, DynamoHttpServletRequest pReq) {
    String id = pPathInfo;
    int count = 0;

    if (mServlet == null)
    	mServlet = (DropletEventServlet) pReq.getAttribute(DROPLET_EVENT_ATTRIBUTE);
    
    if (mServlet == null)
      return null;

    do {
      EventSender tag = mServlet.getEventSender(id);
      if (tag == this) 
        break;
      /*
       * No form yet with this URL - just add it
       */
      if (tag == null) {
        mServlet.addEventSender(id, this);
        break;
      }
      /*
       * Uh oh.  Need to try a new ID
       */
      id = pPathInfo + "." + count;
    } while (true);

    return id;
  }

  /**
   * Returns an enumeration of the event receivers.
   */
  Enumeration getEventReceivers() {
    return mFormEventReceivers.elements();
  }

  /**
   * Returns the number of event receivers
   */
  int getNumEventReceivers() {
    return mFormEventReceivers.size();
  }

  /** Return the names of the form event servers. */
  public synchronized String[] getFormEventSenderNames() {
    int size = mFormEventReceivers.size();
    String[] result = new String[size];
    Enumeration enumKeys = mFormEventReceivers.keys();
    int idx = 0;
    while (enumKeys.hasMoreElements()) {
      result[idx++] = (String)enumKeys.nextElement();
    }

    return result;
  }


  /** Return the names of the form event servers. */
  public synchronized String[] getFormEventProperties() {
    Enumeration enumValues = mFormEventReceivers.elements();
    ArrayList alist = new ArrayList();
    while (enumValues.hasMoreElements()) {
      String strPropName =
        ((FormEventReceiver)enumValues.nextElement()).getPropertyPath();
      if (strPropName != null)
        alist.add(strPropName);
    }
    return (String[])alist.toArray(new String[alist.size()]);
  }
  
  //-------------------------------------

  // this method is needed to "fix" bug 62211 -- working around
  // an icky IE bug.
  /** is pValue an instance of the "DEFAULT_IS_STATIC" value.
   */
  private boolean isDefaultStaticValue(String pValue) {
    if ((pValue != null) &&
        (pValue.equals(DEFAULT_IS_STATIC) ||
         pValue.equals(BOGUS_DEFAULT_IS_STATIC_ON_IE))) {
      return true;
    }
    return false;
  }

} // end class FormTag

/**
 * Provides an interface to retrieve the elements of the FormEventReceivers
 * dictionary sorted based on the priority field.
 */
class SortedTags extends Vector {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/FormTag.java#2 $$Change: 651448 $";

  static final long serialVersionUID = -5591375103822789687L;


  Dictionary mFormEventReceivers;

  SortedTags(int pSize, Dictionary pFormEventReceivers) {
    super (pSize);
    mFormEventReceivers = pFormEventReceivers;
  }

  FormEventReceiver getFormEventReceiverAt(int pIndex) {
    return (FormEventReceiver) mFormEventReceivers.get (elementAt(pIndex));
  }

  /* 
   * Sorts the handlers.  Using insertion sort here because we need the
   * order to be preserved for like entries, and we want it to be fast
   * for the case where the list is already sorted. 
   */
  void sortTags() {
    int size = size();
    int i, j;

    for (i = 1; i < size; i++) {
      int value = getPriority(i);
      Object obj = elementAt(i);

      for (j = i - 1;  j >= 0 && getPriority(j) < value; j--) {
        setElementAt(elementAt(j), j+1);
      }
      if (i != j+1) setElementAt(obj, j+1);
    }
  }

  /*
   * Returns the priority of the specified Droplet handler
   */
  int getPriority(int pIndex) {
    return getFormEventReceiverAt(pIndex).getPriority();
  }

} // end class SortedTags
