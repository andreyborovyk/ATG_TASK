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

import atg.beans.PropertyNotFoundException;
import atg.core.util.NumberTable;
import atg.core.util.ResourceUtils;
import atg.naming.NameContext;
import atg.nucleus.logging.ApplicationLogging;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/EventSender.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public abstract 
class EventSender extends ComplexTag implements DropletConstants {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/EventSender.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.droplet.DropletResources";

  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  ApplicationLogging mLogger = null;

  boolean mReportDropletExceptions = true;

  /**
   * Should we report droplet exceptions or just give them to the form
   * handler 
   */
  boolean getReportDropletExceptions() {
    return mReportDropletExceptions;
  }

  public EventSender() {
  }

  public EventSender(ApplicationLogging pLogger,
  		     boolean pReportDropletExceptions) {
    mLogger = pLogger;
    mReportDropletExceptions = pReportDropletExceptions;
  }

  //-------------------------------------
  /**
   * Subclasses override this method to implement the specific event
   * delivery behavior.
   */
  abstract boolean sendEvents (DynamoHttpServletRequest pReq,
                               DynamoHttpServletResponse pRes) 
         throws DropletException, ServletException, IOException;

  //-------------------------------------
  /** Returns the list of event receivers registered for this event sender */
  abstract Enumeration getEventReceivers();

  //-------------------------------------
  /** Returns the count of event receivers for this event sender */
  abstract int getNumEventReceivers();

  //-------------------------------------
  public Object getRequestValue(BaseDropletDescriptor d, 
  			        DynamoHttpServletRequest pReq, int pInd, 
  				boolean pMethodIsGet, String pKey, 
  				EventReceiver pMh,
  				Object pArgs[])
        throws InvocationTargetException, IllegalAccessException,
               DropletFormException {

    String strValue = null; 
    Object value = null;
    /*
     * If we don't already have an object, use the specified converter
     * to convert the String to an Object
     */
    boolean converted = false;

    if (pMh.isImage())
      return getImageRequestValue(d, pReq, pInd, pMethodIsGet, pKey,
      				  pMh, pArgs);

    if (pInd == 0) {
      // Get the object parameter here, to pick up UploadedFile objects
      value = pReq.getObjectParameter (pKey);

      // If the object parameter was a String, use the string value
      if (value instanceof String) {
        strValue = (String) value;
        value = null;
      }
      /* If it comes in as an object (e.g. UploadedFile), it is already 
         converted to its normal type */
      else if (value != null)
        converted = true;
    }
    else {
      /*
       * Get the i'th parameter value from the request for this argument
       */
      if (pMethodIsGet)
        strValue = pReq.getQueryParameter(pKey, pInd);
      else
        strValue = pReq.getPostParameter(pKey, pInd);
    }

    /*
     * If this was a submit tag that had a submitvalue specified, and this
     * value was submitted, we replace the "value" with the submitvalue
     * at this stage.  This way, it will get converted properly.
     */
    if (strValue != null && pMh.getSubmitValue() != null) {
      strValue = pMh.getSubmitValue();
      if (mLogger != null && mLogger.isLoggingDebug())
       mLogger.logDebug("name=" + pKey + " has submitvalue=" + strValue); 
    }
    else if (mLogger != null && mLogger.isLoggingDebug())
      mLogger.logDebug("name=" + pKey + " has value='" + strValue + "'");

    if (strValue == null && value == null) {
      strValue = pMh.getDefault();
    }

    try {
      if (strValue != null) {
        TagConverter cvt = pMh.getConverter();
        if (cvt != null) {
          converted = true;
          value = cvt.convertStringToObject(pReq, strValue, pMh.getConverterArgs());
	  /* 
	   * This is a supplementary conversion that converts 
	   * Number objects to the specific class of number expected by
	   * the form handler.  We probably should add a feature where we
	   * can tell the TagConverter the specific type that we'd like it
	   * to produce instead of doing this here.
	   */
	  if (value instanceof Number) 
	    value = d.cvtNumberToProperty((Number) value, pArgs);

          /* 
           * If the converter did no conversion, we still need to do
           * the conversion below.  This is true for the required converter
           * (gnat 12985).
           */
          if (strValue == value) 
            converted = false;

          if (mLogger != null && mLogger.isLoggingDebug())
           mLogger.logDebug("Parameter name=" + pKey + " used converter: " + cvt.getName() + 
                            " to convert: " + strValue + " to " + value);
        }
      }
    }
    catch (TagConversionException exc) {
      throw new DropletFormException(exc.getMessage(), 
      				     exc.getRootCause() == null ? exc : exc.getRootCause(), 
      				     pMh.getPropertyPath(), exc.getErrorCode());
    }

    /*
     * Only convert if there was no object value.  We may have converted
     * the strValue into a null value so we need to use this flag instead
     * of testing for a null value explicitly.
     */
    if (!converted) {
      /*
       * If we have a convert method, call it to convert the string value
       * to the object's type value.  Otherwise, we assume that the routine
       * is expecting a string itself.
       */
      try {
	value = d.cvtStringToProperty(strValue, pArgs);
      }
      /*
       * Suppress number format exceptions and send them as
       * DropletFormExceptions
       * Note: NumberFormatExceptions will come to us two different
       * ways, in their own form and buried in an InvocationTargetException.
       */
      catch (NumberFormatException e) {
        throw new DropletFormException("Invalid value for a number", e, pMh.getPropertyPath(), "numberFormatError");
      }
      catch (IllegalArgumentException e) {
        throw new DropletFormException("Invalid value", e, pMh.getPropertyPath(), "illegalArgumentError");
      }
      catch (InvocationTargetException e) {
        Throwable th = e.getTargetException();
        if (th instanceof NumberFormatException) 
          throw new DropletFormException("Invalid value for a number", th, pMh.getPropertyPath(), "numberFormatError");
        else if(th instanceof IllegalArgumentException) 
	      throw new DropletFormException("You entered an invalid value for a property", th, pMh.getPropertyPath(), "illegalArgumentError");
	    else throw e;
      } 
    }

    return value;
  }

  //-------------------------------------
  /*
   * Like the method above, but does this operation for image submissions.
   * These submits get the string ".x" and ".y" appended onto the name
   * of the value so we need to do this a little differently.
   */
  public Object getImageRequestValue(BaseDropletDescriptor d, 
  			        DynamoHttpServletRequest pReq, int pInd, 
  				boolean pMethodIsGet, String pKey, 
  				EventReceiver pMh,
  				Object pArgs[])
        throws InvocationTargetException, IllegalAccessException {

    String strValue, strValueX, strValueY; 
    Object value;

    /*
     * Get the i'th parameter value from the request for this argument
     */
    if (pMethodIsGet) 
      strValueX = pReq.getQueryParameter(pKey + ".x", pInd);
    else
      strValueX = pReq.getPostParameter(pKey + ".x", pInd);


    if (strValueX == null) strValue = pMh.getDefault();
    else {
      if (pMethodIsGet)
        strValueY = pReq.getQueryParameter(pKey + ".y", pInd);
      else
        strValueY = pReq.getPostParameter(pKey + ".y", pInd);
      strValue = strValueX + ", " + strValueY;
    }

    /*
     * Substitute the SubmitValue if the regular value is defined
     */
    if (strValue != null && pMh.getSubmitValue() != null) {
      strValue = pMh.getSubmitValue();
    }

    value = d.cvtStringToProperty(strValue, pArgs);

    return value;
  }

  //------------------------------------------
  /**
   * Sends an event to the specified EventReceiver (either InputTag,
   * SelectTag, TextAreaTag or AnchorTag
   */
  boolean sendEvent(DynamoHttpServletRequest pReq,
  		    DynamoHttpServletResponse pRes, EventReceiver pMR, 
  		    Object pSetArgs[],
  		    Vector pBeforeSetObjects)
              throws DropletException, ServletException, IOException {
    Throwable th = null;
    Class cl = null;
    boolean methodIsGet = pReq.getMethod().equals("GET");
    Object component = null;
    Object origComponent = null;

    try {
      String [] pathNames = pMR.getPathNames();
      int [][] pathDims = pMR.getPathDims();
      String path = pMR.getPropertyPath();
      String currentName = null;
      String key = pMR.getName();
      Object value = null;
      DropletDescriptor d = null;
      int i;

      cl = null; /* in case exception is thrown below */
      origComponent = component = pReq.resolveName(pathNames[0]);
      if (component == null) {
	Object[] patternArgs = { pathNames[0],  pReq.getPathInfo() };
	throw new DropletException(ResourceUtils.getMsgResource
				   ("eventSenderCantFindComponent", 
				    MY_RESOURCE_NAME, sResourceBundle,
				    patternArgs), "missingComponent");      
      }

      /*
       * Need to do this before we can throw any DropletFormExceptions so
       * that we call beforeSet before we call handleFormException.
       */
      if (!callBeforeSet(pReq, pRes, origComponent, pBeforeSetObjects)) {
        if (mLogger != null && mLogger.isLoggingDebug())
          mLogger.logDebug("aborting request processing - beforeSet method returns false");
        return false;
      }

      if (pathDims != null && pathDims[0] != null) {
        component = DropletDescriptor.resolveDimensions(null, null, path, 
        					   component, pathDims[0]);
      }

    
      /*
       * Go through and evaluate each entry in the path name of the
       * property path reference
       */
      for (i = 1; i < pathNames.length; i++) {
        cl = component.getClass();
        currentName = pathNames[i];
        
        try {
          // TODO: Check for handleXXX type
          d = DropletDescriptor.getDropletDescriptor(cl,
                                                     (Class)pReq.getAttribute(DropletEventServlet.HANDLE_TYPE_ONE),
                                                     (Class)pReq.getAttribute(DropletEventServlet.HANDLE_TYPE_TWO),
                                                     currentName);
        } catch(DropletException de) {
          if(mLogger != null && mLogger.isLoggingDebug())
            mLogger.logDebug(de);
          return true;
        }
        
        if (i != pathNames.length - 1) {
          if (pathDims != null && pathDims[i] != null) {
            component = DropletDescriptor.resolveDimensions(d, currentName, 
                                                            path, component, pathDims[i]);
          }
          else {
            if (d.propertyMapper != null)
              component = d.propertyMapper.getPropertyValue(component, currentName);
            else 
              component = d.getMethod.invoke(component);
          }
          
          if (component == null) {
            Object[] mPatternArgs = { pMR.getComponentPath(),
                                      pMR.getPropertyPath(),
                                      currentName};
            throw new DropletFormException(ResourceUtils.getMsgResource
                                           ("eventSenderNullProperty", 
                                            MY_RESOURCE_NAME, sResourceBundle,
                                            mPatternArgs), pMR.getPropertyPath(),
                                           "nullPropertyValue");
          }
        }
      }
   
      int pathIndex = i - 1;

      BaseDropletDescriptor bd = d;
      if (d.propertyMapper != null)
        bd = new BaseDropletDescriptor(component, currentName);

      /*
       * If either the property refers to a simple scalar value, or the
       * last path element refers to an element of an array, we retrieve
       * a single scalar value.
       */
      if (!bd.isArray || (pathDims != null && pathDims[pathIndex] != null)) {
        value = getRequestValue(bd, pReq, 0, methodIsGet, key, pMR, pSetArgs);
      }
      else if(bd.isArray && pMR.getConverter() != null && (pMR.getConverter() instanceof ScalarToMultiTagConverter))
      {
        //if it is an array property and is using a tag converter to convert a
        //scalar value into the multi-valued property. 
        value = getRequestValue(bd, pReq, 0, methodIsGet, key, pMR, pSetArgs);
        
      }
      /*
       * If this is an array, for each form argument with this key
       * we convert a value that corresponds to this return value
       */
      else {
        int count, j;

        if (methodIsGet)
          count = pReq.getQueryParameterCount(key);
        else
          count = pReq.getPostParameterCount(key);

        /*
         * Look for a vector valued property
         */
        try {
          Object valueArray = Array.newInstance(bd.propertyType, count);

          for (j = 0; j < count; j++) {
            Object tmp = getRequestValue(bd, pReq, j, methodIsGet, 
                                         key, pMR, pSetArgs);
            Array.set(valueArray, j, tmp);
          }
          value = valueArray;
        }
        catch (NegativeArraySizeException e) {}
        catch (IllegalArgumentException e) {}
        catch (ArrayIndexOutOfBoundsException e) {}
      }

      boolean eventHandled = false;
      /*
       * Call the specified set method of this component
       */
      if (value != null) {
	/*
	 * If the value is the SET_AS_NULL Object, change the
	 * value to null in order to call the set method with null.
	 */
	if (value == TagConverterManager.SET_AS_NULL) value = null;

        if (pathDims != null && pathDims[pathIndex] != null) {
          /*
           * If this is a reference to an element of a 1D array,
           * and this is an indexed property, set just set that
           * element of the array.
           */ 
          if (d.setIndexedMethod != null && pathDims[pathIndex].length == 1) {
            Object args[] = new Object[2];
            args[0] = NumberTable.getInteger(pathDims[pathIndex][0]);
	    args[1] = value;
	    try {
	      d.setIndexedMethod.invoke(component, args);
	    }
	    catch (IllegalArgumentException exc) {
	      throw new IllegalArgumentException (exc.toString () + 
						  ": Attempting to call " +
						  d.setIndexedMethod +
						  " with value " + args[1]);
	    }
	    catch (NullPointerException exc) {
	      throw new IllegalArgumentException (exc.toString () + 
						  ": Attempting to call " +
						  d.setIndexedMethod +
						  " with value " + args[1]);
	    }
            eventHandled = true;
          }
          else {
            if (pathDims[pathIndex].length == 1) {
              if (bd.isList) {
                List l = null;
                if (d.propertyMapper != null)
                  l = (List) d.propertyMapper.getPropertyValue(component, currentName);
                else if (d.getMethod != null)
	          l = (List) d.getMethod.invoke(component);
                if (l != null) {
                  int ix = pathDims[pathIndex][0];
                  if (ix == l.size()) // automatically add new values...
                    l.add(value);
                  else
                    l.set(pathDims[pathIndex][0], value);
                  eventHandled = true;
                }
                else {
                  Object[] patternArgs = { pMR.getPropertyPath()};	
                  throw new DropletException(ResourceUtils.getMsgResource
                                             ("eventSenderCantSetArrayElement", 
                                              MY_RESOURCE_NAME, sResourceBundle, patternArgs));
                }
              }
              else {
                Object[] patternArgs = { pMR.getPropertyPath()};	
                throw new DropletException(ResourceUtils.getMsgResource
                                           ("eventSenderCantSetArrayElement", 
                                            MY_RESOURCE_NAME, sResourceBundle, patternArgs));
              }
	    }
            else {
	      Object[] mPatternArgs = {	pMR.getPropertyPath()};	
	      throw new DropletException(ResourceUtils.getMsgResource
					 ("eventSenderCantSetMultiArrayValues",
					  MY_RESOURCE_NAME, sResourceBundle, mPatternArgs));
	    }
          }
        }
        else if (d.propertyMapper != null) {
          d.propertyMapper.setPropertyValue(component, currentName, value);
          eventHandled = true;
        }
        else if (d.setMethod != null) {
          pSetArgs[0] = value;

          if (mLogger != null && mLogger.isLoggingDebug())
            mLogger.logDebug("setting property component=" + component + " value=" + value + " set method=" + d.setMethod);

          try {
            d.setMethod.invoke(component, pSetArgs);
          }
          catch (IllegalArgumentException exc) {
            throw new IllegalArgumentException (exc.toString () + 
                                                ": Attempting to call " +
                                                d.setMethod +
                                                " with value " + value + 
                                                " class = " +
                                                (value == null ? value : value.getClass()));
          }
          catch (NullPointerException exc) {
            throw new IllegalArgumentException (exc.toString () + 
                                                ": Attempting to call " +
                                                d.setMethod +
                                                " with value " + value +
                                                " class = " +
                                                (value == null ? value : value.getClass()));
          }

          eventHandled = true;
        }
        /*
         * If we have an indexed property, set each array value one at a time
         */
        else if (d.setIndexedMethod != null) {
          int size = Array.getLength(value);
          Object [] args = new Object[2];
          for (int j = 0; j < size; j++) {
            args[0] = NumberTable.getInteger(j);
            args[1] = Array.get(value, j);
            try {
              d.setIndexedMethod.invoke(component, args);
            }
            catch (IllegalArgumentException exc) {
              throw new IllegalArgumentException (exc.toString () + 
                                                  ": Attempting to call " +
                                                  d.setIndexedMethod +
                                                  ", index " +
                                                  args [0] +
                                                  ", with value " +
                                                  args [1]);
            }
            catch (NullPointerException exc) {
              throw new IllegalArgumentException (exc.toString () + 
                                                  ": Attempting to call " +
                                                  d.setIndexedMethod +
                                                  ", index " +
                                                  args [0] +
                                                  ", with value " +
                                                  args [1]);
            }
          }
          eventHandled = true;
        }

        if (d.handleMethod != null) {
          Object handleArgs[] = new Object[2];
          

          // Check if we should use an object other than
          // the dynamo request/response pair when invoking the
          // form handlers handeXXX method
          handleArgs[0] = pReq.getAttribute(DropletEventServlet.HANDLE_OBJECT_ONE);
          handleArgs[1] = pReq.getAttribute(DropletEventServlet.HANDLE_OBJECT_TWO);

          if (handleArgs[0] == null || handleArgs[1] == null) {
            handleArgs[0] = pReq;
            handleArgs[1] = pRes;
          }

          if (mLogger != null && mLogger.isLoggingDebug())
            mLogger.logDebug("calling handle method on component=" + component + " handle method=" + d.handleMethod);

          Object ret = d.handleMethod.invoke(component, handleArgs);
          if (ret != null && ret instanceof Boolean && 
             !((Boolean) ret).booleanValue()) {
            if (mLogger != null && mLogger.isLoggingDebug())
              mLogger.logDebug("aborting event processing - handle method returned false");
            return false;
          }
          eventHandled = true;
        }

        if (!eventHandled) {
	  Object[] mPatternArgs = { currentName,
				    currentName,
				    cl.getName(),
				    value == null ? "null" : value.toString()};
	  throw new DropletException(ResourceUtils.getMsgResource
				     ("eventSenderDropletMissingSet",
				      MY_RESOURCE_NAME, sResourceBundle, mPatternArgs));
	}

      }
      else if (mLogger != null && mLogger.isLoggingDebug())
        mLogger.logDebug("found no value in request for name=" + key);
    }
    catch (IllegalAccessException e) {
      th = e;
    }
    /*
     * These two errors are now handled by the form handler since they
     * can be used for input validation.
     */
    catch (IllegalArgumentException e) {
      addFormException(pReq, pRes, new DropletFormException(e.toString(), 
                      e, pMR.getPropertyPath(), "illegalArgument"), origComponent);
    }
    catch (InvocationTargetException e) {
      /* If it is a runtime exception, just throw it since it could hide some
       * app error.  If it is a checked exception, we need to treat it as
       * something the form handler can catch.
       */
      if ((e.getTargetException() instanceof RuntimeException) 
           || (e.getTargetException() instanceof Error))
        th = e.getTargetException();
      else
        addFormException(pReq, pRes, new 
                 DropletFormException(e.getTargetException().toString(), 
                     e.getTargetException(), pMR.getPropertyPath(), "errorCallingSetMethod"), 
                     origComponent);
	
    }
    catch (PropertyNotFoundException e) {
      th = e;
    }
    catch (DropletFormException e) {
      addFormException(pReq, pRes, e, origComponent);
    }

    if (th != null) {
      // give the formhandler a chance to process any unchecked exceptions
      addUncheckedFormException(pReq, pRes, th, origComponent);

      /*
       * If the handle method throws the ServletException or the IOException
       * it should just be propagated up
       */
      if (th instanceof ServletException)
        throw (ServletException) th;
      if (th instanceof IOException) 
        throw (IOException) th;
      if (th instanceof RuntimeException)
        throw (RuntimeException) th;
      if (th instanceof Error)
        throw (Error) th;
      
      Object[] mPatternArgs = { pMR.getPropertyPath(),
				cl.getName(),
				th.toString()};
      throw new DropletException(ResourceUtils.getMsgResource
				 ("eventSenderDropletFailedToDeliver", 
				  MY_RESOURCE_NAME, sResourceBundle, mPatternArgs));
    }

    return true;
  }

  //------------------------------------------
  /**
   * Call the beforeSet methods in the DropletFormHandler
   */
  boolean callBeforeSet(DynamoHttpServletRequest pRequest,
  		     DynamoHttpServletResponse pResponse,
  		     Object pObj, 
  		     Vector pBeforeSetObjects) throws DropletFormException {
    
    if (!(pObj instanceof DropletFormHandler) && !(pObj instanceof ObjectFormHandler))
        return true;
    if (!vectorContains(pBeforeSetObjects,pObj)) {
    	try {
        if (mLogger != null && mLogger.isLoggingDebug())
          mLogger.logDebug("calling beforeSet of: " + pObj);
        if (pObj instanceof DropletFormHandler)
        	return ((DropletFormHandler)pObj).beforeSet(pRequest, pResponse);
        else if (pObj instanceof ObjectFormHandler) {
        	Object [] params = DropletEventServlet.getAlternateFormParams(pRequest);
        	if (params != null)
        		return ((ObjectFormHandler)pObj).beforeSet(params[0],params[1]);
          else
            return ((ObjectFormHandler)pObj).beforeSet(pRequest,pResponse);
        }
            
      }
      catch (DropletFormException exc) {
        addFormException(pRequest, pResponse, exc, pObj); 
      }
      finally {
        pBeforeSetObjects.addElement(pObj);
      }
    }
    return true;
  }

  //------------------------------------------
  /** An implementation of the contains method for a vector that does not
   * call the "equals" method on the object.  We really only care about
   * equality here, not what the class thinks is equals (people can screw
   * that method up and hence the behavior of droplets...)
   */
  boolean vectorContains(Vector pVector, Object pObj) {
    int size = pVector.size();
    for (int i = 0; i < size; i++)
      if (pVector.elementAt(i) == pObj) return true;
    return false;
  }

  //------------------------------------------
  /**
   * Call the afterSet methods of any form components that were registered
   * in this request
   */
  boolean callAfterSet(DynamoHttpServletRequest pRequest,
  		       DynamoHttpServletResponse pResponse,
  		       Vector pBeforeSetObjects) {
    int size = pBeforeSetObjects.size();
    boolean returnCode = true;
    for (int i = 0; i < size; i++) {
      Object handler = 
            pBeforeSetObjects.elementAt(i);
      try {
        if (mLogger != null && mLogger.isLoggingDebug())
          mLogger.logDebug("calling afterSet of: " + handler);
        if (handler instanceof DropletFormHandler){
        	if(!((DropletFormHandler)handler).afterSet(pRequest,pResponse))
        		returnCode = false;
        }
        else if (handler instanceof ObjectFormHandler) {
        	Object [] params = DropletEventServlet.getAlternateFormParams(pRequest);
        	if (params != null) {
            if (!((ObjectFormHandler)handler).afterSet(params[0],params[1]))
              returnCode  = false;
        	}
          else {
            if (!((ObjectFormHandler)handler).afterSet(pRequest,pResponse))
              returnCode  = false;           
          }
        }
      }
      catch (DropletFormException exc) {
        addFormException(pRequest, pResponse, exc, handler);
      }
    }
    return returnCode;
  }

  /**
   * Adds an exception onto the list of exceptions for this request 
   * If this is the first exception, the list gets created.  If the object
   * responsible for the exception is passed in and it is a DropletFormHandler,
   * it gets an opportunity to handle the exception too.
   */
  void addFormException(DynamoHttpServletRequest pReq,
  		        DynamoHttpServletResponse pRes, 
  		        Exception e, Object pObj) {
    Vector formExceptions = (Vector) pReq.getAttribute(DROPLET_EXCEPTIONS_ATTRIBUTE);

    if (formExceptions == null) {
      formExceptions = new Vector(1);
      /* For compatibility with older versions */
      pReq.setAttribute(DROPLET_EXCEPTIONS_ATTRIBUTE, formExceptions);
      NameContext ctx = pReq.getRequestScope();
      /* For preserving errors when we redirect, we store these in request scope */
      ctx.putElement(DROPLET_EXCEPTIONS_ATTRIBUTE, formExceptions);
    }
    formExceptions.addElement(e);

    /**
     * Give a chance for the form handler to deal with the exception
     * directly
     */
    if (pObj != null && e instanceof DropletFormException) {
    	if (mLogger != null && mLogger.isLoggingDebug())
    		mLogger.logDebug("calling handleFormException of: " + pObj + " with exception=" + e);
    	DropletFormException dfe = (DropletFormException)e;
    	if ( pObj instanceof DropletFormHandler) {
    		((DropletFormHandler) pObj).handleFormException(
    				dfe, pReq, pRes);
    	} else if (pObj instanceof ObjectFormHandler) {
    		Object [] params = DropletEventServlet.getAlternateFormParams(pReq);
    		if (params != null) ((ObjectFormHandler)pObj).handleFormException(dfe,params[0],params[1]);
    	}
    }
  }

 /**
   * Adds an unchecked exception onto the list of unchecked exceptions for this request
   * If this is the first exception, the list gets created.  If the object
   * responsible for the exception is passed in and it is a DropletFormHandler,
   * it gets an opportunity to handle the unchecked exception too.
   */
  void addUncheckedFormException(DynamoHttpServletRequest pReq,
                                 DynamoHttpServletResponse pRes,
                                 Throwable th, Object pObj) {
    List uncheckedFormExceptions = (List) pReq.getAttribute(UNCHECKED_DROPLET_EXCEPTIONS_ATTRIBUTE);

    if (uncheckedFormExceptions == null) {
      uncheckedFormExceptions = new ArrayList();
      /* For compatibility with older versions */
      pReq.setAttribute(UNCHECKED_DROPLET_EXCEPTIONS_ATTRIBUTE, uncheckedFormExceptions);
      NameContext ctx = pReq.getRequestScope();
      /* For preserving errors when we redirect, we store these in request scope */
      ctx.putElement(UNCHECKED_DROPLET_EXCEPTIONS_ATTRIBUTE, uncheckedFormExceptions);
    }
    uncheckedFormExceptions.add(th);

    /**
     * Give a chance for the form handler to deal with the exception
     * directly
     */
    if (pObj != null) {
      if (mLogger != null && mLogger.isLoggingDebug())
        mLogger.logDebug("calling handleUncheckedFormException of: " + pObj + " with exception=" + th);
      if (pObj instanceof DropletFormHandler) {
        ((DropletFormHandler) pObj).handleUncheckedFormException(th, pReq, pRes);
      } else if (pObj instanceof ObjectFormHandler) {
        Object[] params = DropletEventServlet.getAlternateFormParams(pReq);
        if (params != null) ((ObjectFormHandler) pObj).handleUncheckedFormException(th, params[0], params[1]);
      }
    }
  }
}

