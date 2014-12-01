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

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.awt.Point;
import java.text.*;

import atg.servlet.*;

import atg.naming.*;
import atg.nucleus.naming.*;
import atg.core.util.NumberTable;
import atg.core.util.ResourceUtils;
import atg.core.util.BeanUtils;

import atg.beans.DynamicBeans;
import atg.beans.DynamicPropertyMapper;
import atg.beans.DynamicPropertyDescriptor;
import atg.beans.PropertyNotFoundException;

/**
 * Caches the information needed to make efficient reflection calls
 * to Droplet handler methods.  A handle corresponds either to a property
 * or a method of the form: 
 * <pre>handlePropertyName(ServletRequest, ServletResponse)</pre>
 * <p>
 * Each descriptor is associated with a particular java class.  If the
 * referenced component's class changes, a new descriptor has to be created.
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletDescriptor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class DropletDescriptor extends BaseDropletDescriptor {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletDescriptor.java#2 $$Change: 651448 $";

  final static boolean kDebug = false;
  
  static final String MY_RESOURCE_NAME = "atg.droplet.DropletResources";

  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  private static final String HANDLE = "handle";
  Method cvtMethod = null;
  Method cvtNumberMethod = null;
  Method setMethod = null;
  Method setIndexedMethod = null;
  Method getMethod = null;
  Method getIndexedMethod = null;
  Method handleMethod = null;
  static Class [] mHandlerTypes = {ServletRequest.class,ServletResponse.class};
  Class beanClass;
  DynamicPropertyMapper propertyMapper;

  /* 
   * XXX this should be a dictionary that uses better hashcodes for
   * strings and maybe should not be synchronized by default.
   */
  static Hashtable sDropletDescriptors = new Hashtable();

  static Dictionary sTypeMapping = null; 

  /**
   * Returns the string property value for the specified component and
   * the specified path.  This version performs escaping of HTML specific
   * characters such as &lt; and &gt;.
   *
   * @param pPropertyPath the path name to the property in the form:
   * <pre>{component path}/{component path}.{property}.{property}</pre>
   * @return the value of the property
   *
   * @exception ServletException thrown if an error occurs trying to
   * find the component, or trying to get one of the property
   * values. 
   */
  public static String getPropertyHtmlStringValue(DynamoHttpServletRequest pRequest,
  					          String pPropertyPath) 
        throws ServletException {
    String str = getPropertyStringValue(pRequest, pPropertyPath);
    return ServletUtil.escapeHtmlString(str);
  }

  /**
   * Returns the string property value for the specified component and
   * the specified path.  This version can also be used to call the
   * beforeGet method of classes that implement the DropletFormHandler
   * interface if this is the first time that a property has been
   * accessed for this request.  It also escapes HTML specific characters
   * such as &lgt; and &gt;.
   *
   * @param pPropertyPath the path name to the property in the form:
   * <pre>{component path}/{component path}.{property}.{property}</pre>
   * @param pCallBeforeGet true if the beforeGet method of DropletFormHandler
   * components should be called if this is the first time that a property
   * of this component has been accessed for this request.
   * @return the value of the property
   *
   * @exception ServletException thrown if an error occurs trying to
   * find the component, or trying to get one of the property
   * values. 
   */
  public static String getPropertyHtmlStringValue(DynamoHttpServletRequest pRequest,
  						  DynamoHttpServletResponse pResponse,
  					          String pPropertyPath,
  					          boolean pCallBeforeGet,
  					          TagConverter pConverter,
  					          Properties pConverterArgs) 
        throws ServletException {
    String str = getPropertyStringValue(pRequest, pResponse, pPropertyPath, pCallBeforeGet,
    					pConverter, pConverterArgs);
    return ServletUtil.escapeHtmlString(str);
  }

  /**
   * Returns the string property value for the specified component and
   * the specified path.  This version can also be used to call the
   * beforeGet method of classes that implement the DropletFormHandler
   * interface if this is the first time that a property has been
   * accessed for this request.  It also escapes HTML specific characters
   * such as &lgt; and &gt;. This version uses  PropertyName instead of
   * a string to refer to the property.
   *
   * @param pPropertyName the path name to the property in the form:
   * <pre>{component path}/{component path}.{property}.{property}</pre>
   * @param pCallBeforeGet true if the beforeGet method of DropletFormHandler
   * components should be called if this is the first time that a property
   * of this component has been accessed for this request.
   * @return the value of the property
   *
   * @exception ServletException thrown if an error occurs trying to
   * find the component, or trying to get one of the property
   * values. 
   */
  public static String getPropertyHtmlStringValue(DynamoHttpServletRequest pRequest,
  						  DynamoHttpServletResponse pResponse,
  					          PropertyName pPropertyName,
  					          boolean pCallBeforeGet,
  					          TagConverter pConverter,
  					          Properties pConverterArgs) 
        throws ServletException {
    String str = getPropertyStringValue(pRequest, pResponse, pPropertyName, pCallBeforeGet,
    					pConverter, pConverterArgs);
    return ServletUtil.escapeHtmlString(str);
  }


  /**
   * Returns the string property value for the specified component and
   * the specified path.
   *
   * @param pPropertyPath the path name to the property in the form:
   * <pre>{component path}/{component path}.{property}.{property}</pre>
   * @return the value of the property
   *
   * @exception ServletException thrown if an error occurs trying to
   * find the component, or trying to get one of the property
   * values. 
   */
  public static String getPropertyStringValue(DynamoHttpServletRequest pRequest,
  					      String pPropertyPath) 
        throws ServletException {
    Object obj = getPropertyValue(pRequest, pPropertyPath);
    return ServletUtil.toString(obj);
  }

  /**
   * Returns the string property value for the specified component and
   * the specified path.  This version can also be used to call the
   * beforeGet method of classes that implement the DropletFormHandler
   * interface if this is the first time that a property has been
   * accessed for this request.
   *
   * @param pPropertyPath the path name to the property in the form:
   * <pre>{component path}/{component path}.{property}.{property}</pre>
   * @param pCallBeforeGet true if the beforeGet method of DropletFormHandler
   * components should be called if this is the first time that a property
   * of this component has been accessed for this request.
   * @return the value of the property
   *
   * @exception ServletException thrown if an error occurs trying to
   * find the component, or trying to get one of the property
   * values. 
   */
  public static String getPropertyStringValue(DynamoHttpServletRequest pRequest,
  					      DynamoHttpServletResponse pResponse,
  					      String pPropertyPath,
  					      boolean pCallBeforeGet,
  					      TagConverter pConverter,
  					      Properties pConverterArgs) 
        throws ServletException {
    Object obj = getPropertyValue(pRequest, pResponse, pPropertyPath, pCallBeforeGet,
    				  pConverter, pConverterArgs);
    return ServletUtil.toString(obj);
  }

  /**
   * Returns the string property value for the specified component and
   * the specified path.  This version can also be used to call the
   * beforeGet method of classes that implement the DropletFormHandler
   * interface if this is the first time that a property has been
   * accessed for this request.
   *
   * @param pPropertyName the PropertyName for the property in the form.
   * @param pCallBeforeGet true if the beforeGet method of DropletFormHandler
   * components should be called if this is the first time that a property
   * of this component has been accessed for this request.
   * @return the value of the property
   *
   * @exception ServletException thrown if an error occurs trying to
   * find the component, or trying to get one of the property
   * values. 
   */
  public static String getPropertyStringValue(DynamoHttpServletRequest pRequest,
  					      DynamoHttpServletResponse pResponse,
  					      PropertyName pPropertyName,
  					      boolean pCallBeforeGet,
  					      TagConverter pConverter,
  					      Properties pConverterArgs) 
        throws ServletException {
    Object obj = getPropertyValue(pRequest, pResponse, pPropertyName, pCallBeforeGet,
    				  pConverter, pConverterArgs);
    return ServletUtil.toString(obj);
  }

  /** 
   * Returns true if the specified value matches the value of the property
   * specified in pPropertyPath.  A match occurs if the property is a scalar
   * if the string value of the property equals the string provided.  For
   * arrays, a match occurs if any element of the array matches the string
   * value provided.
   */
  public static boolean matchesPropertyValue(DynamoHttpServletRequest pRequest,
  				     String pPropertyPath, String pValue) 
       throws ServletException {
    return matchesPropertyValue(pRequest, null, pPropertyPath, pValue, false,
    				null, null);
  }

  /** 
   * Returns true if the specified value matches the value of the property
   * specified in pPropertyPath.  A match occurs if the property is a scalar
   * if the string value of the property equals the string provided.  For
   * arrays, a match occurs if any element of the array matches the string
   * value provided.
   * <p>
   * This version of this method can be used to call the beforeGet method
   * of the component if this is the first time that the components value
   * has been accessed in this request.
   *
   * @param pCallBeforeGet true if the beforeGet method of DropletFormHandler
   * components should be called if this is the first time that a property
   * of this component has been accessed for this request.
   */
  public static boolean matchesPropertyValue(DynamoHttpServletRequest pRequest,
  					     DynamoHttpServletResponse pResponse,
  				     String pPropertyPath, String pValue,
  				     boolean pCallBeforeGet,
  				     TagConverter pConverter,
  				     Properties pConverterArgs) 
       throws ServletException {
    Object obj = getPropertyValue(pRequest, pResponse, PropertyName.getPropertyName(pPropertyPath), pCallBeforeGet,
    				  pConverter, pConverterArgs);
    return ServletUtil.valuesMatchIgnoreCase(obj, pValue);
  }

  /** 
   * Returns true if the specified value matches the value of the property
   * specified in pPropertyName.  A match occurs if the property is a scalar
   * if the string value of the property equals the string provided.  For
   * arrays, a match occurs if any element of the array matches the string
   * value provided.
   * <p>
   * This version of this method can be used to call the beforeGet method
   * of the component if this is the first time that the components value
   * has been accessed in this request.
   *
   * @param pPropertyName the property name to match against.
   * @param pCallBeforeGet true if the beforeGet method of DropletFormHandler
   * components should be called if this is the first time that a property
   * of this component has been accessed for this request.
   */
  public static boolean matchesPropertyValue(DynamoHttpServletRequest pRequest,
  					     DynamoHttpServletResponse pResponse,
  				     PropertyName pPropertyName, String pValue,
  				     boolean pCallBeforeGet,
  				     TagConverter pConverter,
  				     Properties pConverterArgs) 
       throws ServletException {
    Object obj = getPropertyValue(pRequest, pResponse, pPropertyName, pCallBeforeGet,
    				  pConverter, pConverterArgs);
    return ServletUtil.valuesMatchIgnoreCase(obj, pValue);
  }


    /**
   * Returns the object property value for the specified component and
   * the specified path.
   *
   * @param pPropertyPath the path name to the property in the form:
   * <pre>{component path}.{property}.{property}</pre>
   * @return the value of the property
   *
   * @exception ServletException thrown if an error occurs trying to
   * find the component, or trying to get one of the property
   * values. 
   */
  public static Object getPropertyValue(DynamoHttpServletRequest pRequest, 	
  					PropertyName pPropertyPath) 
          throws ServletException {
    return getPropertyValue(pRequest, null, pPropertyPath, false, null, null);
  }

  //----------------------------------------------
  /**
   * Returns the object property value for the specified component and
   * the specified path.   This version can be used to call the beforeGet
   * method of a DropletFormHandler component.
   *
   * @param pPropertyPath the path name to the property in the form:
   * <pre>{component path}.{property}.{property}</pre>
   * @param pCallBeforeGet true if the beforeGet method of DropletFormHandler
   * components should be called if this is the first time that a property
   * of this component has been accessed for this request.
   *
   * @return the value of the property
   *
   * @exception ServletException thrown if an error occurs trying to
   * find the component, or trying to get one of the property
   * values. 
   */
  public static Object getPropertyValue(DynamoHttpServletRequest pRequest, 	
  					DynamoHttpServletResponse pResponse,
  					PropertyName pPropertyPath,
  					boolean pCallBeforeGet,
  					TagConverter pConverter,
  					Properties pConverterArgs)
          throws ServletException {

    /* 
     * If this PropertyName refers to an invalid component, just bag it 
     * right now.
     */
    if (pPropertyPath == null)
      throw new ServletException(ResourceUtils.getMsgResource("dropletDescriptorInvalidPropPath",
								MY_RESOURCE_NAME, sResourceBundle));

    ComponentName componentName = pPropertyPath.getComponentName();

    Object obj = pRequest.resolveName(componentName);
    if (obj == null) { 

      Object[] patternArgs = { componentName };
      throw new ServletException(ResourceUtils.getMsgResource("dropletDescriptorCantFindComponent",
							      MY_RESOURCE_NAME, sResourceBundle,
							      patternArgs));
    }

    if (pCallBeforeGet)
      callBeforeGet(obj, pRequest, pResponse);
    

    PropertyName[] subNames = pPropertyPath.getSubNames();
    String name;
    int [] dims;

    /*
     * This means that we just have the component, and no property name
     */
    if ((subNames == null) || 
	(subNames.length == 0))
      return obj;

     for (int i = 0; (i < subNames.length)
	    && (obj != null); i++) {
      PropertyName propName = subNames[i];
      name = propName.getName();

      if (propName.hasDimensions()) {
	Object[] rawDims = propName.getDimensions();
	
	dims = new int[rawDims.length];
	
	for (int j = 0; j < rawDims.length; j++) {
	  Object dim = rawDims[j];
  
	  if (dim instanceof Integer) 
	    dims[j] = ((Integer) dim).intValue();
	  else { 
	      String stringValue;
	      if (dim instanceof ParameterName)
		stringValue = pRequest.getParameter((ParameterName) dim);
	      else if (dim instanceof PropertyName)
		stringValue = DropletDescriptor.getPropertyStringValue(pRequest, (PropertyName) dim);
	      else stringValue = null;

	      if (stringValue == null) {
		Object[] patternArgs = { pPropertyPath };
		throw new DropletException(ResourceUtils.getMsgResource
				   ("dropletNamesInvalidDimensions", 
				    MY_RESOURCE_NAME, sResourceBundle, patternArgs));
	      }

	      try {
		dims[j] = Integer.parseInt(stringValue);
	      }
	      catch (NumberFormatException e) {
		Object[] patternArgs = { pPropertyPath };
		throw new DropletException(ResourceUtils.getMsgResource
					   ("dropletNamesInvalidDimension", 
					    MY_RESOURCE_NAME, sResourceBundle,
					    patternArgs), "invalidArrayDimension");  
	      }  
	  }	
	}
      }
      else dims = null;
      
      DropletDescriptor d = getDropletDescriptor(obj.getClass(), name);
      
      try {
        if (kDebug)
          System.out.println("+++ Droplet getting property value path=" +
                              pPropertyPath + " component=" + obj);

	

	if (dims != null)
          obj = DropletDescriptor.resolveDimensions(d, name, 
          					    pPropertyPath.getName(), obj, dims);
        else if (d.propertyMapper != null) {
          try {
            obj = d.propertyMapper.getPropertyValue(obj, name);
          }
          catch (PropertyNotFoundException exc) {
            Object[] patternArgs = {name,
                                     obj.getClass().toString(),
                                     pPropertyPath};
            throw new ServletException(ResourceUtils.getMsgResource("dropletDescriptorCantGetPropValueGet",
								    MY_RESOURCE_NAME, sResourceBundle,
								    patternArgs), exc);
          }
        }
        else if (d.getMethod != null)
          obj = d.getMethod.invoke(obj);
        else {
	  Object[] patternArgs = {name,
				   obj.getClass().toString(),
				   pPropertyPath};
	  throw new ServletException(ResourceUtils.getMsgResource("dropletDescriptorCantGetPropValueGet",
								  MY_RESOURCE_NAME, sResourceBundle,
								  patternArgs));
	}
      }
      catch (IllegalAccessException e) {

	Object[] patternArgs = {d.getMethod.toString(),
				 pPropertyPath};
	throw new ServletException(ResourceUtils.getMsgResource("dropletDescriptorCantAccessMethodGet",
								MY_RESOURCE_NAME, sResourceBundle,
								patternArgs),
                                            e);
      }
      catch (IllegalArgumentException e) {
        System.out.println("*** illegal arg follows...");
        e.printStackTrace();
	
	Object[] patternArgs = {pPropertyPath, e.toString()};
	throw new ServletException(ResourceUtils.getMsgResource("dropletDescriptorIllegalArgGet",
								MY_RESOURCE_NAME, sResourceBundle,
								patternArgs), e);
      }
      catch (InvocationTargetException e) {
        Throwable th = e.getTargetException();

	/*
	 * Runtime exceptions can be thrown by the get<name> method.  In this
	 * case, we want to just refire them.
	 */
        if (th instanceof RuntimeException)
          throw (RuntimeException) th;

	Object[] patternArgs = {pPropertyPath,
				 e.getTargetException().toString()};
	throw new ServletException(ResourceUtils.getMsgResource("dropletDescriptorTargetExceptionGet",
								MY_RESOURCE_NAME, sResourceBundle,
								patternArgs), e);
      }
    }

    if (pConverter != null)
      return pConverter.convertObjectToString(pRequest, obj, pConverterArgs);

    return obj;
  }

  //----------------------------------------------
  /**
   * Returns the object property value for the specified component and
   * the specified path.
   *
   * @param pPropertyPath the path name to the property in the form:
   * <pre>{component path}.{property}.{property}</pre>
   * @return the value of the property
   *
   * @exception ServletException thrown if an error occurs trying to
   * find the component, or trying to get one of the property
   * values. 
   */
  public static Object getPropertyValue(DynamoHttpServletRequest pRequest, 	
  					String pPropertyPath) 
          throws ServletException {
    return getPropertyValue(pRequest, null, PropertyName.getPropertyName(pPropertyPath), false, null, null);
  }

  //----------------------------------------------
  /**
   * Returns the string property value for the specified component and
   * the specified path.
   *
   * @param pPropertyPath the path name to the property in the form:
   * <pre>{component path}/{component path}.{property}.{property}</pre>
   * @return the value of the property
   *
   * @exception ServletException thrown if an error occurs trying to
   * find the component, or trying to get one of the property
   * values. 
   */
  public static String getPropertyStringValue(DynamoHttpServletRequest pRequest,
  					      PropertyName pPropertyPath) 
        throws ServletException {
    Object obj = getPropertyValue(pRequest, pPropertyPath);
    return ServletUtil.toString(obj);
  }

  //----------------------------------------------
  /**
   * Returns the object property value for the specified component and
   * the specified path.   This version can be used to call the beforeGet
   * method of a DropletFormHandler component.
   *
   * @param pPropertyPath the path name to the property in the form:
   * <pre>{component path}.{property}.{property}</pre>
   * @param pCallBeforeGet true if the beforeGet method of DropletFormHandler
   * components should be called if this is the first time that a property
   * of this component has been accessed for this request.
   *
   * @return the value of the property
   *
   * @exception ServletException thrown if an error occurs trying to
   * find the component, or trying to get one of the property
   * values. 
   */
  public static Object getPropertyValue(DynamoHttpServletRequest pRequest, 	
  					DynamoHttpServletResponse pResponse,
  					String pPropertyPath,
  					boolean pCallBeforeGet,
  					TagConverter pConverter,
  					Properties pConverterArgs)
          throws ServletException {
    return getPropertyValue(pRequest, 
			    pResponse,
			    PropertyName.getPropertyName(pPropertyPath),
			    pCallBeforeGet,
			    pConverter,
			    pConverterArgs);
  }

  //----------------------------------------------
  /**
   * Calls the beforeGet method for the specified component
   */
  static void callBeforeGet(Object obj, DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) 
  throws ServletException {
    Vector beforeGetObjects = initializeBeforeGetContainer(pRequest,DROPLET_BEFORE_GET_ATTRIBUTE);
    Vector objBeforeGetObjects = initializeBeforeGetContainer(pRequest,OBJECT_BEFORE_GET_ATTRIBUTE);
    if (obj instanceof DropletFormHandler && !beforeGetObjects.contains(obj)) {
    	((DropletFormHandler) obj).beforeGet(pRequest, pResponse);
        beforeGetObjects.addElement(obj);
      }
    else if (obj instanceof ObjectFormHandler && !objBeforeGetObjects.contains(obj)) {
        Object [] params = DropletEventServlet.getAlternateFormParams(pRequest);
        if (params != null) {
          ((ObjectFormHandler)obj).beforeGet(params[0], params[1]);
        	objBeforeGetObjects.addElement(obj);
        }
        else {
          ((ObjectFormHandler)obj).beforeGet(pRequest, pResponse);
          objBeforeGetObjects.addElement(obj);
        }
    }
  }
   
  /**
   * @param pRequest
   * @return
   */
  private static Vector initializeBeforeGetContainer(DynamoHttpServletRequest pRequest,String pAttribute) {
    Vector beforeGetObjects = (Vector) pRequest.getAttribute(pAttribute);
    if (beforeGetObjects == null) {
      pRequest.setAttribute(pAttribute, 
                            beforeGetObjects = new Vector(3));
    }
    return beforeGetObjects;
  }

  //----------------------------------------------
  /**
   * This method sets the property value specified by the pPropertyPath
   * parameter to the value specified by the pValue parameter.  
   *
   * @param pReq the request (which is passed to handleXXX methods)
   * @param pRes the response (which is passed to handleXXX methods)
   * @param pPropertyPath the path name to the property in the form:
   * <pre>{component path}.{property}.{property}</pre>
   * @param pValue the value of the property
   *
   * @exception ServletException thrown if an error occurs trying to
   * find the component, or trying to get one of the intermediate property
   * values, or trying to set the final property value.  A ServletException
   * can also be thrown by one of the handleXXX methods, in which case
   * that exception is propagated up through this call.
   * @exception IOException thrown if the handleXXX methods throws an
   * IOException
   */
  public static boolean setPropertyValue(DynamoHttpServletRequest pReq, 
                                         DynamoHttpServletResponse pRes, 
                                         String pPropertyPath, 
                                         Object pValue) 
          throws ServletException, IOException {
    return setPropertyValue(pReq, pRes, pPropertyPath, pValue, null, null);
  }

  //----------------------------------------------
  /**
   * This method sets the property value specified by the pPropertyPath
   * parameter to the value specified by the pValue parameter.  
   *
   * @param pReq the request (which is passed to handleXXX methods)
   * @param pRes the response (which is passed to handleXXX methods)
   * @param pPropertyPath the path name to the property in the form:
   * <pre>{component path}.{property}.{property}</pre>
   * @param pValue the value of the property
   *
   * @exception ServletException thrown if an error occurs trying to
   * find the component, or trying to get one of the intermediate property
   * values, or trying to set the final property value.  A ServletException
   * can also be thrown by one of the handleXXX methods, in which case
   * that exception is propagated up through this call.
   * @exception IOException thrown if the handleXXX methods throws an
   * IOException
   */
  public static boolean setPropertyValue(DynamoHttpServletRequest pReq, 
                                         DynamoHttpServletResponse pRes, 
                                         String pPropertyPath, 
                                         Object pValue,
                                         TagConverter pConverter,
                                         Properties pConverterArgs) 
          throws ServletException, IOException {
    int ind;
    Method method = null;

    if (pConverter != null && pValue instanceof String)
      pValue = pConverter.convertStringToObject(pReq, (String)pValue, pConverterArgs);

    if (pPropertyPath == null)      
      throw new ServletException(ResourceUtils.getMsgResource("dropletDescriptorInvalidPropPath",
							      MY_RESOURCE_NAME, sResourceBundle));

    String componentName = DropletNames.getComponentPath(pPropertyPath);
    if (componentName == null) {

      Object[] patternArgs = {pPropertyPath};				 
      throw new ServletException(ResourceUtils.getMsgResource("dropletDescriptorInvalidPropPathPattern",
							      MY_RESOURCE_NAME, sResourceBundle,
							      patternArgs));      
				  
    }

    Object obj = pReq.resolveName(componentName);
    if (obj == null) { 
      Object[] patternArgs = { componentName };
      throw new ServletException(ResourceUtils.getMsgResource("dropletDescriptorCantFindComponent",
							      MY_RESOURCE_NAME, sResourceBundle,
							      patternArgs));
    }

    String path = pPropertyPath;
    String name;
    int [] dims;

    /*
     * Replace [param:xxx] with [3]
     */
    path = evalDynamicDimensions(path, pReq);

    /*
     * This returns -1 if we just get a component name, not a property name.
     * Can't implement the "set" operation on this kind of thing though.
     */
    ind = path.indexOf('.', componentName.length());
    if (ind == -1) {
      Object[] patternArgs = {pPropertyPath};				 
      throw new ServletException(ResourceUtils.getMsgResource("dropletDescriptorInvalidPropPathPattern",
							      MY_RESOURCE_NAME, sResourceBundle, 
							      patternArgs));
    }

    /*
     * Strip off the component name
     */
    path = path.substring(ind+1);

    do {
      ind = path.indexOf('.');
      if (ind == -1) {
        name = path;
        path = null;
      }
      else {
        name = path.substring(0,ind);
        path = path.substring(ind+1);
      }

      int dimsInd = name.indexOf('[');
      if (dimsInd != -1) {
        /*
         * Returns the integer array of dimensions given a 
         * path name
         */
        dims = DropletNames.getDimensions(name.substring(dimsInd), pPropertyPath);
        name = name.substring(0, dimsInd);
      }
      else dims = null;

      DropletDescriptor d = getDropletDescriptor(obj.getClass(), name);
      try {
        if (kDebug)
          System.out.println("+++ Droplet setting property value path=" +
                              pPropertyPath + " component=" + obj + " to value " + pValue);

        /*
         * On the last entry, we do a set
         */
        if (path == null) {
          if (dims == null) {
            if (d.propertyMapper != null) {
              /* Use the property editor to convert these values */
              if (pValue instanceof String) {
                try {
                  DynamicPropertyDescriptor dpd = DynamicBeans.getPropertyDescriptor(obj, name);
                  if (dpd != null) {
                    PropertyEditor ed = dpd.createPropertyEditor();
                    if (ed != null) {
                      ed.setAsText((String) pValue);
                      pValue = ed.getValue();
                    }
                  }
                }
                catch (IntrospectionException exc) {}
              }
              d.propertyMapper.setPropertyValue(obj, name, pValue);
            }
            else if (d.setMethod != null) {
              method = d.setMethod;
              Object [] args = new Object[1];
              args[0] = pValue;

              if (pValue instanceof String) {
                if (d.cvtMethod != null && pValue != null) {
                  Object newValue = d.cvtMethod.invoke(null, args);
                  args[0] = newValue;
                }
              }
              d.setMethod.invoke(obj, args);
            }
          }
          else {
            if (d.setIndexedMethod != null && dims.length == 1) {
              Object [] args = new Object[2];
              args[0] = NumberTable.getInteger(dims[0]);
              args[1] = pValue;

              d.setIndexedMethod.invoke(obj, args);
            }
            else {
              if (dims.length == 1) {
		Object[] patternArgs = {pPropertyPath};	       
		throw new DropletException(ResourceUtils.getMsgResource
					   ("dropletDescriptorCantSetElementsArrayProp", 
					    MY_RESOURCE_NAME, sResourceBundle, patternArgs));
	      }
              else {
		Object[] patternArgs = {pPropertyPath};	       
		throw new DropletException(ResourceUtils.getMsgResource
					   ("dropletDescriptorCantSetElementsMultiArrayProp", 
					    MY_RESOURCE_NAME, sResourceBundle, patternArgs));
	      }
            }
          }
          if (d.handleMethod != null) {
            method = d.handleMethod;
            Object handleArgs[] = new Object[2];
            handleArgs[0] = pReq;
            handleArgs[1] = pRes;

            if (kDebug)
              System.out.println("+++ Droplet: handling: " + obj +
                                 " method=" + d.handleMethod);

            Object ret = d.handleMethod.invoke(obj, handleArgs);
            if (ret != null && ret instanceof Boolean && 
               !((Boolean) ret).booleanValue()) {
              if (kDebug)
                System.out.println("+++ Droplet: aborting request for method: " +
                                     d.handleMethod);
                return false;
            }
          }
        }
        else {
          if (dims != null)
            obj = DropletDescriptor.resolveDimensions(d, name, pPropertyPath, 
            					      obj, dims);
          else if (d.propertyMapper != null) {
            obj = d.propertyMapper.getPropertyValue(obj, name);
          }
          else if (d.getMethod != null) {
            method = d.getMethod;
            obj = d.getMethod.invoke(obj);
          }
          else {
	    Object[] patternArgs = {name,
				     obj.getClass().toString(),
				     pPropertyPath};
	    throw new ServletException(ResourceUtils.getMsgResource
				       ("dropletDescriptorCantGetPropValueSet", 
					MY_RESOURCE_NAME, sResourceBundle, patternArgs));
	  }

          if (obj == null) {
	    Object[] patternArgs = {name, pPropertyPath};
	    throw new ServletException(ResourceUtils.getMsgResource
				       ("dropletDescriptorCantSetNullProperty", 
					MY_RESOURCE_NAME, sResourceBundle, patternArgs));
          }
        }
      }
      catch (IllegalAccessException e) {
	Object[] patternArgs = {(method == null ? "null" : method.toString()),
				 pPropertyPath};
	throw new ServletException(ResourceUtils.getMsgResource
				   ("dropletDescriptorCantAccessMethodSet", 
				    MY_RESOURCE_NAME, sResourceBundle, patternArgs), e);
      }
      catch (IllegalArgumentException e) {	
	Object[] patternArgs = {pPropertyPath, e.toString()};
	throw new ServletException(ResourceUtils.getMsgResource
				   ("dropletDescriptorIllegalArgSet", 
				    MY_RESOURCE_NAME, sResourceBundle, patternArgs), e);
      }
      catch (InvocationTargetException e) {
        Throwable th = e.getTargetException();

        /*
         * If the handle method throws the ServletException or the IOException
         * it should just be re-thrown so that programmers can catch their
         * own errors.
         */
        if (th instanceof ServletException)
          throw (ServletException) th;
        if (th instanceof IOException)
          throw (IOException) th;
        if (th instanceof RuntimeException)
          throw (RuntimeException) th;
	
	Object[] patternArgs = {pPropertyPath,
				 e.getTargetException().toString()};
	throw new ServletException(ResourceUtils.getMsgResource
				   ("dropletDescriptorTargetExceptionSet", 
				    MY_RESOURCE_NAME, sResourceBundle, patternArgs), e.getTargetException());
      }
      /*
       * A PropertyMapper reported an error with this property name
       */
      catch (PropertyNotFoundException exc) {
        Object[] patternArgs = {name,
                                 obj.getClass().toString(),
                                 pPropertyPath,
                                 exc.toString()};
        throw new ServletException(ResourceUtils.getMsgResource
				   ("dropletDescriptorCantSetPropNotFound", 
				    MY_RESOURCE_NAME, sResourceBundle, patternArgs), exc);
      }
    } while (path != null);

    return true;
  }

  //----------------------------------------------
  /**
   * Applies the array of dimensions onto the component specified.  For
   * each element in pDims, we take that element of the pComponent array/Vector.
   *
   * @param pPath the path name of this reference (used for error messages)
   * @param pComponent the current component
   * @param pDims the array of dimensions 

   * @exception DropletException if the component is not an array or vector
   * or if an array out of bounds exception occurs while processing
   * the dimensions list.
   */
  static Object resolveDimensions(DropletDescriptor d, 
  				  String pName, String pPath, Object pComponent, 
  				  int [] pDims) 
      throws DropletException, InvocationTargetException, 
             IllegalAccessException, IllegalArgumentException {
    int start = 0;

    /*
     * Do the first dimension by hand to deal with indexed properties
     * if we were given a descriptor.  Otherwise, just resolve the 
     * dimensions for array values only.
     */
    if (d != null) {
      if (d.getIndexedMethod != null) {
        start = 1;
        Object [] args = new Object[1];
        args[0] = NumberTable.getInteger(pDims[0]);
        pComponent = d.getIndexedMethod.invoke(pComponent, args);
      }
      else if (d.propertyMapper != null) {
        try {
          pComponent = d.propertyMapper.getPropertyValue(pComponent, pName);
        }
        catch (PropertyNotFoundException exc) {
          Object[] patternArgs = {pName, 
                                   pComponent.getClass().toString(), 
                                   pPath};
          throw new DropletException(ResourceUtils.getMsgResource
				     ("dropletDescriptorCantGetPropValueSet", 
				      MY_RESOURCE_NAME, sResourceBundle, patternArgs));
        }
      }
      else if (d.getMethod != null) {
        pComponent = d.getMethod.invoke(pComponent);
      } 
      else {
	Object[] patternArgs = {pName, 
				 pComponent.getClass().toString(), 
				 pPath};
	throw new DropletException(ResourceUtils.getMsgResource
				   ("dropletDescriptorCantGetPropValueSet", 
				    MY_RESOURCE_NAME, sResourceBundle, patternArgs));
      }
    }

    try {
      for (int i = start; i < pDims.length; i++) {
        if (pComponent == null) {
          Object[] args = {pPath};
          throw new DropletException(MessageFormat.format(sResourceBundle.getString("dropletDescriptorNullArrayValue"),
                                                args));
        }
        if (pComponent.getClass().isArray()) {
          pComponent = Array.get(pComponent, pDims[i]);
        }
        else if (pComponent instanceof List) {
          pComponent = ((List)pComponent).get(pDims[i]);
        }
        else {
	  Object[] patternArgs = {pPath};
	  throw new DropletException(ResourceUtils.getMsgResource
				     ("dropletDescriptorComponentNotArrayOrList", 
				      MY_RESOURCE_NAME, sResourceBundle, patternArgs));
	}
      }
    }
    catch (IndexOutOfBoundsException e) {
      Object[] patternArgs = {pPath};
      /*
       * Throw this as a DropletFormException so that EventSender will
       * catch it and let a form handler component deal with this error.
       */
      throw new DropletFormException(ResourceUtils.getMsgResource
				     ("dropletDescriptorArrayOutOfBounds", 
				      MY_RESOURCE_NAME, sResourceBundle, patternArgs),
				     pPath, "arrayOutOfBounds");	
    }
    return pComponent;
  }

  /**
   * Goes through the dimensions string and replaces the param: and property:
   * values with their current values.  The process of resolving dimension
   * values is done in two steps so that we can use the evaluated 
   * propertyPath (i.e. the path with [param:xxx] changed to [0]) as a key 
   * into the Form's table of tags, even though it is slightly less
   * efficient this way.
   */
  public static String evalDynamicDimensions(PropertyName pPropertyPath, 
					     DynamoHttpServletRequest pRequest) 
       throws ServletException {
    StringBuffer buffer = new StringBuffer();
    evalDynamicDimensions(pPropertyPath, pRequest, buffer); 
    return buffer.toString();
  }

  static void evalDynamicDimensions(PropertyName pName,
				    DynamoHttpServletRequest pRequest,
				    StringBuffer pBuffer) 
				    throws ServletException 
  {
    ComponentName compName = pName.getComponentName();

    if (compName != null)
      pBuffer.append(compName.getName());
    else {
      pBuffer.append(pName.getName());
    }
    
    if (pName.hasSubNames()) {
      PropertyName subNames[] = pName.getSubNames();

      for (int i = 0; i < subNames.length; i++) {
	pBuffer.append(".");
	evalDynamicDimensions(subNames[i], pRequest, pBuffer);
      }
    }  
    if (pName.hasDimensions()) {
      Object[] dimensions = pName.getDimensions();
      
      for (int i = 0; i < dimensions.length; i++) {
	pBuffer.append("[");
	Object dimension = dimensions[i];
	if (dimension instanceof Integer) 
	  pBuffer.append(dimension);
	else if (dimension instanceof ParameterName) {
	  pBuffer.append(pRequest.getParameter((ParameterName) dimension));  
	}
	else if (dimension instanceof PropertyName) {
	  pBuffer.append(DropletDescriptor.getPropertyStringValue(pRequest, 
								  (PropertyName) dimension));
	}
	pBuffer.append("]");
      }
    } 
    return;
  }
				    
  /**
   * Goes through the dimensions string and replaces the param: and property:
   * values with their current values.  The process of resolving dimension
   * values is done in two steps so that we can use the evaluated 
   * propertyPath (i.e. the path with [param:xxx] changed to [0]) as a key 
   * into the Form's table of tags, even though it is slightly less
   * efficient this way.
   */
  public static String evalDynamicDimensions(String pPropertyPath, 
  			   DynamoHttpServletRequest pRequest) 
       throws ServletException {
    if (pPropertyPath.indexOf('[') == -1)
      return pPropertyPath;

    String path = pPropertyPath;

    int pos = 0;
    int end = 0;
    int start, len;
    boolean isParam;
    String symbol, symbolValue;

    do {
      start = path.indexOf(DROPLET_PARAM_PREFIX, pos);
      if (start != -1) {
        isParam = true;
        len = DROPLET_PARAM_PREFIX.length();
      }
      else {
        start = path.indexOf(DROPLET_BEAN_PREFIX, pos);
        if (start == -1) {
          start = path.indexOf(DROPLET_PROPERTY_PREFIX, pos);
          len = DROPLET_PROPERTY_PREFIX.length();
        }
        else 
          len = DROPLET_BEAN_PREFIX.length();
        isParam = false;
      }
      if (start != -1) {
        end = path.indexOf(']', start);
        if (end == -1) {
	  Object[] patternArgs = {pPropertyPath};
	  throw new DropletException(ResourceUtils.getMsgResource
				     ("dropletDescriptorInvalidDimensions", 
				      MY_RESOURCE_NAME, sResourceBundle, patternArgs));
	}
        symbol = path.substring(start + len, end);
        if (isParam) {
          symbolValue = pRequest.getParameter(symbol);
          if (symbolValue == null){
	    Object[] patternArgs = {symbol, 
				     pPropertyPath};	   
	    throw new DropletException(ResourceUtils.getMsgResource
				       ("dropletDescriptorArrayParameterNotDefined", 
					MY_RESOURCE_NAME, sResourceBundle, patternArgs));
	  }
        }
        else {
          symbolValue = DropletDescriptor.getPropertyStringValue(pRequest, symbol);
          if (symbolValue == null){
	    Object[] patternArgs = {symbol, 
				     pPropertyPath};	   
	    throw new DropletException(ResourceUtils.getMsgResource
				       ("dropletDescriptorArrayPropertyNotDefined",
					MY_RESOURCE_NAME, sResourceBundle, patternArgs));
	  }
        }
        path = path.substring(0, start) + symbolValue + path.substring(end);
        pos = 0;
      }
    } while (start != -1);


    return path;
  }


  /**
   * Returns true if the specified property path name has a value
   * for the specified request.  Beans that don't have a get method
   * for this property or if the get method returns null do not have
   * a property.
   */
  public static boolean hasPropertyValue(DynamoHttpServletRequest pRequest,
  					 String pPropertyName) {
    try {
      Object obj = getPropertyValue(pRequest, pPropertyName);
      return obj != null;
    }
    catch (ServletException exc) {
    }
    return false;
  }

  /**
   * Returns true if the specified property path name has a value
   * for the specified request.  Beans that don't have a get method
   * for this property or if the get method returns null do not have
   * a property.  This version takes a PropertyName to avoid the extra
   * hashtable lookup.
   */
  public static boolean hasPropertyValue(DynamoHttpServletRequest pRequest,
  					 PropertyName pPropertyName) {
    try {
      Object obj = getPropertyValue(pRequest, pPropertyName);
      return obj != null;
    }
    catch (ServletException exc) {
    }
    return false;
  }
 
  /**
   * Returns the property descriptor for the specified class and property
   * name.
   */ 
  public static DropletDescriptor getDropletDescriptor(Class pClass,
      String pPropertyName) 
  throws DropletException {
   return getDropletDescriptor(pClass,ServletRequest.class,ServletResponse.class,pPropertyName);
  }
  /**
   * Returns the property descriptor for the specified class and property
   * name.
   * The handle method returned from this DropletDescriptor must take two parameters
   * of type pParamOne and pParam two.
   * @param pClass
   * @param pHandleParamOne
   * @param pHandleParamTwo
   * @param pPropertyName
   * @return
   * @throws DropletException
   */
  public static DropletDescriptor getDropletDescriptor(Class pClass,Class pHandleParamOne,Class pHandleParamTwo,
  						       String pPropertyName) 
                   throws DropletException {
    Hashtable h = (Hashtable) sDropletDescriptors.get(pClass);
    if (h == null) {
      synchronized (pClass) {
        h = (Hashtable) sDropletDescriptors.get(pClass);
        if (h == null) {
          h = new Hashtable();
          sDropletDescriptors.put(pClass, h);
        }
      }
    }
    String key = null;
    if (pHandleParamOne != null)
      key = pPropertyName + pHandleParamOne.getName();
    else
      key = pPropertyName;
    DropletDescriptor p = (DropletDescriptor) h.get(key);
    if (p == null) {
      synchronized (h) {
        p = (DropletDescriptor) h.get(key);
        if (p == null) {
          /*
           * Do this twice - once unsynchronized and once again synchronized
           * so that we don't have to sync up unless we need to modify the
           * table
           */
          if (pHandleParamOne != null && pHandleParamTwo != null) 
            p = new DropletDescriptor(pClass,pHandleParamOne,pHandleParamTwo, pPropertyName); 
          else
            p = new DropletDescriptor(pClass, pPropertyName); 
          h.put(key, p);
        }
      }
    }
    return p;
  }
/**
 * 
 * Constructor for DropletDescriptor
 * @param pClass The Class for which to return a DropletDescriptor
 * @param pPropertyName The property name to return for the given class
 * @throws DropletException
 */
  DropletDescriptor (Class pClass, String pPropertyName) 
  throws DropletException {
    this (pClass,ServletRequest.class,ServletResponse.class,pPropertyName);
  }
  /**
   * 
   * Constructor for DropletDescriptor
   * @param pClass The Class for which to return a DropletDescriptor
   * @param pHandleParamOne The type of the first parameter for the handleXXX method
   * @param pHandleParamTwo The type of the second parameter for the handleXXX method
   * @param pPropertyName The property name to return for the given class
   * @throws DropletException
   */
  DropletDescriptor (Class pClass,Class pHandleParamOne,Class pHandleParamTwo, String pPropertyName) 
     throws DropletException {
    mHandlerTypes[0] = pHandleParamOne;
    mHandlerTypes[1] = pHandleParamTwo;
    int i;

    beanClass = pClass;

    if (sTypeMapping == null) {
      try {
        sTypeMapping = new Hashtable();
        sTypeMapping.put(Boolean.TYPE, Class.forName("java.lang.Boolean"));
        sTypeMapping.put(Byte.TYPE, Class.forName("java.lang.Byte"));
        sTypeMapping.put(Short.TYPE, Class.forName("java.lang.Short"));
        sTypeMapping.put(Integer.TYPE, Class.forName("java.lang.Integer"));
        sTypeMapping.put(Long.TYPE, Class.forName("java.lang.Long"));
        sTypeMapping.put(Float.TYPE, Class.forName("java.lang.Float"));
        sTypeMapping.put(Double.TYPE, Class.forName("java.lang.Double"));
      }
      catch (ClassNotFoundException e) {}
    }

    try {
      boolean foundSomething = false;
      BeanInfo info = BeanUtils.getBeanInfo(pClass);
      PropertyDescriptor [] p = info.getPropertyDescriptors();

      for (i = 0; i < p.length; i++) {
        if (p[i].getName().equalsIgnoreCase(pPropertyName))
          break;
      }
      /*
       * We found a parameter with this name
       */
      if (i != p.length) {
        foundSomething = true;
        PropertyDescriptor prop = p[i];
        getMethod = prop.getReadMethod();
        setMethod = prop.getWriteMethod();
        propertyType = prop.getPropertyType();
        /*
         * This should only happen for indexed properties that do not have
         * a normal get/set for the whole value
         */
        if (propertyType != null) {
          isList = List.class.isAssignableFrom(propertyType);
          isArray = propertyType.isArray() || isList;
          if (isArray) {
            if (propertyType.getComponentType() == null)
              propertyType = java.lang.String.class;
            else
              propertyType = propertyType.getComponentType();
          }
        }
        else isArray = false;

        if (prop instanceof IndexedPropertyDescriptor) {
          setIndexedMethod = ((IndexedPropertyDescriptor)prop).getIndexedWriteMethod();
          getIndexedMethod = ((IndexedPropertyDescriptor)prop).getIndexedReadMethod();
          isArray = true;
          if (propertyType == null) {
            propertyType = ((IndexedPropertyDescriptor) prop).getIndexedPropertyType();
          }
        }
        else {
          setIndexedMethod = null;
          getIndexedMethod = null;
        }

        setCvtType(propertyType, pPropertyName);

        if (kDebug)
          System.out.println("+++ Droplet new property: class=" +
          	pClass.getName() + " type=" + propertyType + " get=" + getMethod +
          	" set=" + setMethod + " cvt=" + cvtMethod + "isArray=" + isArray);
      }
      else if (kDebug)
        System.out.println("+++ Droplet can't find property for: " + pPropertyName);
      MethodDescriptor [] m = info.getMethodDescriptors();

      /*
       * Go through the methods and check for existing of the handleYYY
       * method as well
       */
      boolean foundHandleMethod = false;
      for (i = 0 ; i < m.length; i++) {
        if (m[i].getName().equalsIgnoreCase(HANDLE + pPropertyName)) {
          foundSomething = locateHandleMethod(pHandleParamOne, pHandleParamTwo, i, m);
          if (foundSomething) break;
        }
      }
      /*
       * We should always get a propertyMapper if there is one registered
       */
      if (DynamicBeans.isDynamicBean(pClass)) {
        propertyMapper = DynamicBeans.getPropertyMapper(pClass);
      }
      
      if (!foundSomething && propertyMapper == null) {
        Object[] patternArgs = {pPropertyName, 
            pClass.toString()};	   
        throw new DropletException(ResourceUtils.getMsgResource
            ("dropletDescriptorCantFindProperty", 
                MY_RESOURCE_NAME, sResourceBundle, patternArgs));
      }
    }
    catch (IntrospectionException e) {
      Object[] patternArgs = {pClass.toString()};      
      throw new DropletException(ResourceUtils.getMsgResource
          ("dropletDescriptorCantExamineProperty",
              MY_RESOURCE_NAME, sResourceBundle, patternArgs), e);
    }
  }


  /**
   * Returns the handleYYY method matching the given parameter types pHandleParamOne, pHandleParamTwo.
   * @param pHandleParamOne
   * @param pHandleParamTwo
   * @param i
   * @param foundSomething
   * @param m
   * @return
   */
  private boolean locateHandleMethod(Class pHandleParamOne, Class pHandleParamTwo, int i,  MethodDescriptor[] m) {
    boolean found = false;
       if (i != m.length) {
      Method meth = m[i].getMethod();
      Class params[] = meth.getParameterTypes();
      /*
       * Does this method match the signature we are expecting?
       *
       * XXX should check to see if param[0] is request, 
       * param[1] is servlet response?
       */
      if (params != null && params.length == 2 &&
          pHandleParamOne.isAssignableFrom(params[0]) &&
          pHandleParamTwo.isAssignableFrom(params[1])) {
        found = true;
        handleMethod = meth;
      }
    }
    return found;
  }

  /*
   * Converts the string supplied to the value supplied.  The pArgs is
   * just a temporary array to use to store the arguments for the invoke
   * to avoid allocating memory for each cvt.
   */
  Object cvtStringToProperty(String pStrValue, Object [] pArgs) throws IllegalArgumentException,
  	IllegalAccessException, InvocationTargetException {
    Object value;
    if (cvtMethod != null && pStrValue != null) {
      pArgs[0] = pStrValue;
      value = cvtMethod.invoke(null, pArgs);
    }
    else value = pStrValue;

    return value;
  }


  //------------------------------
  /**
   * Converters the number to a property
   */
  Object cvtNumberToProperty(Number pValue, Object [] pArgs) throws InvocationTargetException, 
      IllegalAccessException, IllegalArgumentException {
     Object retValue;
     if (cvtNumberMethod != null) {
       pArgs[0] = pValue;
       retValue = cvtNumberMethod.invoke(null, pArgs);
     }
     else retValue = pValue;
     return retValue;
  }

  //------------------------------
  /**
   * Sets the cvtMethod member variable that we use to convert
   * a string value to an object of the right parameter type
   * <p>
   * This must be a static member of the class that takes a single String
   * parameter and returns an object of the appropriate type.
   */
  protected void setCvtType(Class pPropertyType, String pName) 
    throws DropletException {
    Class [] cvtArgs;
    Class [] cvtNumberArgs;
    Class cvtType;


    /*
     * No conversion necessary in this case.
     */
    if (pPropertyType == String.class) {
      cvtMethod = null;
      return;
    }

    /*
     * If this is a primitive property type, 
     * we treat this as the corresponding wrapper type
     */
    if ((cvtType = (Class) sTypeMapping.get(pPropertyType)) != null) {
      pPropertyType = cvtType;
    }

    cvtArgs = new Class[1];
    cvtArgs[0] = String.class;

    cvtNumberArgs = new Class[1];
    cvtNumberArgs[0] = Number.class;

    // Get the type converters that trim whitespace
    try {
      if (pPropertyType == Boolean.TYPE ||
          pPropertyType == Boolean.class) {
        cvtMethod = getClass ().getDeclaredMethod ("convertBoolean", cvtArgs);
      }
      else if (pPropertyType == Byte.TYPE ||
               pPropertyType == Byte.class) {
        cvtMethod = getClass ().getDeclaredMethod ("convertByte", cvtArgs);
        cvtNumberMethod = getClass().getDeclaredMethod ("convertNumberToByte", cvtNumberArgs);
      }
      else if (pPropertyType == Double.TYPE ||
               pPropertyType == Double.class) {
        cvtMethod = getClass ().getDeclaredMethod ("convertDouble", cvtArgs);
        cvtNumberMethod = getClass().getDeclaredMethod ("convertNumberToDouble", cvtNumberArgs);
      }
      else if (pPropertyType == Float.TYPE ||
               pPropertyType == Float.class) {
        cvtMethod = getClass ().getDeclaredMethod ("convertFloat", cvtArgs);
        cvtNumberMethod = getClass().getDeclaredMethod ("convertNumberToFloat", cvtNumberArgs);
      }
      else if (pPropertyType == Integer.TYPE ||
               pPropertyType == Integer.class) {
        cvtMethod = getClass ().getDeclaredMethod ("convertInteger", cvtArgs);
        cvtNumberMethod = getClass().getDeclaredMethod ("convertNumberToInteger", cvtNumberArgs);
      }
      else if (pPropertyType == Long.TYPE ||
               pPropertyType == Long.class) {
        cvtMethod = getClass ().getDeclaredMethod ("convertLong", cvtArgs);
        cvtNumberMethod = getClass().getDeclaredMethod ("convertNumberToLong", cvtNumberArgs);
      }
      else if (pPropertyType == Short.TYPE ||
               pPropertyType == Short.class) {
        cvtMethod = getClass ().getDeclaredMethod ("convertShort", cvtArgs);
        cvtNumberMethod = getClass().getDeclaredMethod ("convertNumberToShort", cvtNumberArgs);
      }
      else if (pPropertyType == Class.class) {
      	cvtMethod = getClass ().getDeclaredMethod ("convertClass", cvtArgs);	
      }
      else if (pPropertyType == UploadedFile.class) {
        cvtMethod = getClass ().getDeclaredMethod ("convertUploadedFile", 
                                                   cvtArgs);
      } 
      else {
        cvtMethod = null;
      }
    }
    catch (NoSuchMethodException e) {
      cvtMethod = null;
    }

    if (cvtMethod == null) {
      try {
        cvtMethod = pPropertyType.getDeclaredMethod("valueOf", cvtArgs);
      }
      catch (NoSuchMethodException e) {
        cvtMethod = null;
      }
      catch (SecurityException e) {
        cvtMethod = null;
      }
    }

    /*
     * If we can't find a converter, look for a convertXXX method in this
     * class that will handle the conversion.  Since this method is
     * static, we should be able to call it properly.
     */
    try {
      if (cvtMethod == null) {
        if (pPropertyType == Character.TYPE || 
            pPropertyType == Character.class) {
          pPropertyType = Character.class;
          cvtMethod = getClass().getDeclaredMethod("convertCharacter", cvtArgs);
        }
        else if (pPropertyType == Point.class)
          cvtMethod = getClass().getDeclaredMethod("convertPoint", cvtArgs);
      }
    }
    catch (NoSuchMethodException e) {}

    /*
     * Make sure that the cvt method is really valid 
     */
    if (cvtMethod != null && 
         (cvtMethod.getReturnType() != pPropertyType ||
          !Modifier.isStatic(cvtMethod.getModifiers()))) 
      cvtMethod = null;
  }

  public Method getGetMethod() {
    return getMethod;
  }

  public Method getSetMethod() {
    return setMethod;
  }

  public Method getHandleMethod() {
    return handleMethod;
  }

  //---------------------------
  /** 
   * This method fills in the role of the valueOf method for the Character
   * class.
   */
  public static Character convertCharacter(String pValue) {
    if (pValue == null || pValue.length() == 0) return new Character('\0');
    return new Character(pValue.charAt(0));
  }

  public static Point convertPoint(String pValue) {
    int ind;
    if (pValue == null || pValue.length() == 0 ||
        (ind = pValue.indexOf(',')) == -1) {
      return null;
    }
    try {
      return new Point(Integer.parseInt(pValue.substring(0,ind)),
      		       Integer.parseInt(pValue.substring(ind+1).trim()));
    }
    catch (NumberFormatException e) {}
    return null;
  }

  //---------------------------
  /**
   *
   * These methods perform the valueOf function after trimming whitespace
   **/
  public static Boolean convertBoolean (String pValue) {
    return Boolean.valueOf (pValue.trim ());
  }
  public static Byte convertByte (String pValue) {
    return Byte.valueOf (pValue.trim ());
  }
  public static Double convertDouble (String pValue) {
    return Double.valueOf (pValue.trim ());
  }
  public static Float convertFloat (String pValue) {
    return Float.valueOf (pValue.trim ());
  }
  public static Integer convertInteger (String pValue) {
    return Integer.valueOf (pValue.trim ());
  }
  public static Long convertLong (String pValue) {
    return Long.valueOf (pValue.trim ());
  }
  public static Short convertShort (String pValue) {
    return Short.valueOf (pValue.trim ());
  }
  public static Double convertNumberToDouble (Number pValue) {
    return NumberTable.getDouble(pValue.doubleValue());
  }
  public static Float convertNumberToFloat (Number pValue) {
    return NumberTable.getFloat(pValue.floatValue());
  }
  public static Integer convertNumberToInteger (Number pValue) {
    return NumberTable.getInteger (pValue.intValue ());
  }
  public static Long convertNumberToLong (Number pValue) {
    return NumberTable.getLong (pValue.longValue ());
  }
  public static Short convertNumberToShort (Number pValue) {
    return NumberTable.getShort(pValue.shortValue ());
  }
  public static Byte convertNumberToByte (Number pValue) {
    return NumberTable.getByte(pValue.byteValue ());
  }
  public static Class convertClass(String pValue) {
      PropertyEditor e = PropertyEditorManager.findEditor(Class.class);
      e.setAsText(pValue);
      return (Class)e.getValue();	
  }

  public static UploadedFile convertUploadedFile (String pValue) {
    return null;
  }
}
