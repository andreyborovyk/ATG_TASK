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

import atg.servlet.*;
import atg.nucleus.naming.ParameterName;
import atg.core.util.ResourceUtils;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.IOException;
import java.util.*;
import java.lang.reflect.*;


/**
 * This servlet takes an array and a value, and renders the "true" open
 * parameter if the value is in the array, and the "false" open parameter otherwise.
 *
 * The <i>array</i> parameter can be a general
 * Collection (Vectors, Lists, Sets), Iterator, Enumeration, Map, Dictionary 
 * or an array.  
 *
 * <p>A complete description of the parameters to the ArrayIncludesValue droplet
 * are:
 *
 * <dl>
 *
 * <dt>array
 * <dd>The parameter that defines the array to be searched.  
 *
 * <dt>value
 * <dd>The parameter that defines the value to check the array for.
 *
 * <dt>true
 * <dd>This parameter is rendered if the value is found in the array.
 *
 * <dt>false
 * <dd>This parameter is rendered if the value is not found in the array.
 *
 * </dl>
 *
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/ArrayIncludesValue.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ArrayIncludesValue extends DynamoServlet {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/ArrayIncludesValue.java#2 $$Change: 651448 $";


  public final static ParameterName ARRAY = ParameterName.getParameterName("array");
  public final static ParameterName VALUE = ParameterName.getParameterName("value");
  public final static ParameterName TRUE  = ParameterName.getParameterName("true");
  public final static ParameterName FALSE = ParameterName.getParameterName("false");

  static final String MY_RESOURCE_NAME = "atg.droplet.DropletResources";

  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  /**
   * Gets the array-like value (e.g., array, List, Enumeration) 
   * to be used by this droplet from the request.  Subclasses may 
   * override this method to extract the array by some means other 
   * than simply looking up its parameter value in the request.
   **/
  public Object getArray(DynamoHttpServletRequest pReq) {
    return pReq.getObjectParameter(ARRAY);
  }

  /**
   * Gets the value to be used by this droplet from the request.  
   * Subclasses may 
   * override this method to extract the value by some means other 
   * than simply looking up its parameter value in the request.
   **/
  public Object getValue(DynamoHttpServletRequest pReq) {
    return pReq.getObjectParameter(VALUE);
  }

  public void service(DynamoHttpServletRequest pRequest, 
                      DynamoHttpServletResponse pResponse) 
  		throws ServletException, IOException 
  {
    Object arrayParam = getArray(pRequest);
    Object valueParam = getValue(pRequest);

    if (arrayParam == null) {
      if (isLoggingDebug()) 
	logDebug(ResourceUtils.getMsgResource("noArray", MY_RESOURCE_NAME, sResourceBundle));
      return;
    }

    if (valueParam == null) {
      if (isLoggingDebug()) 
	logDebug(ResourceUtils.getMsgResource("noValue", MY_RESOURCE_NAME, sResourceBundle));
      return;
    }

    if (arrayParam instanceof Map.Entry []) {
      /*
       * This is a special case.  When we get a sorted map, we actually
       * have an array of Map.Entry objects.  We compress out the value
       * and use set the additional key parameter
       */
      searchMapArray((Map.Entry [])arrayParam, valueParam, pRequest, pResponse);
    }
    else if (arrayParam instanceof Object []) {
      searchArray((Object [])arrayParam, valueParam, pRequest, pResponse);
    }
    else if (arrayParam instanceof List) {
      searchList((List)arrayParam, valueParam, pRequest, pResponse);
    }
    else if (arrayParam instanceof Collection) {
      searchCollection((Collection)arrayParam, valueParam, pRequest, pResponse);
    }
    else if (arrayParam instanceof Iterator) {
      searchIterator((Iterator)arrayParam, valueParam, pRequest, pResponse);
    }
    else if (arrayParam instanceof Enumeration) {
      searchEnumeration((Enumeration)arrayParam, valueParam, pRequest, pResponse);
    }
    else if (arrayParam instanceof Map) {
      searchMap((Map)arrayParam, valueParam, pRequest, pResponse);
    }
    else if (arrayParam instanceof Dictionary) {
      searchDictionary((Dictionary)arrayParam, valueParam, pRequest, pResponse);
    }
    else if (arrayParam.getClass().isArray()) {
      searchPrimitiveArray(arrayParam, valueParam, pRequest, pResponse);
    }
    else if (arrayParam == null) {
      if (isLoggingDebug())
        logDebug("Null value for array parameter in ArrayIncludesValue droplet");
    }
    else {
      if (isLoggingError()) {
	Object[] args = {arrayParam, arrayParam.getClass()};
	logError(ResourceUtils.getMsgResource("illegalArrayWithClassInfo", MY_RESOURCE_NAME, 
					      sResourceBundle, args));
      }
    }
  }

  /**
   * Performs the search operation for map array objects
   * @param pArray the object to search
   * @param pValue the value to search for
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void searchMapArray(Map.Entry [] pArray, Object pValue,
				DynamoHttpServletRequest pRequest, 
				DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    int length = pArray.length;
    boolean found = false;
    for (int i = 0; i < length && found == false; i++)
      found = pArray[i].getValue().equals(pValue);

    if (found)
      pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
    else pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
  }

  /**
   * Performs the search operation for array objects
   * @param pArray the object to search
   * @param pValue the value to search for
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void searchArray(Object [] pArray, Object pValue,
                              DynamoHttpServletRequest pRequest, 
                              DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    int length = pArray.length;
    boolean found = false;
    for (int i = 0; i < length && found == false; i++)
      found = pArray[i].equals(pValue);

    if (found)
      pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
    else pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
  }

  /**
   * Performs the search operation for primitive array objects using the
   * reflection methods to access values in the array.  This version is
   * slightly slower than the regular array version, but more general and
   * works for int[] etc. arrays.
   *
   * @param pArray the object to search
   * @param pValue the value to search for
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void searchPrimitiveArray(Object pArray, Object pValue,
                              DynamoHttpServletRequest pRequest, 
                              DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    int length = Array.getLength(pArray);
    boolean found = false;
    for (int i = 0; i < length; i++)
      found = Array.get(pArray,i).equals(pValue);

    if (found)
      pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
    else pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);    
  }

  /**
   * Renders the search operation for List objects
   * @param pList the object to search
   * @param pValue the value to search for
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void searchList(List pList, Object pValue,
                             DynamoHttpServletRequest pRequest, 
                             DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    boolean found = pList.contains(pValue);

    if (found)
      pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
    else pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);       
  }

  /**
   * Renders the search operation for Collection objects
   * @param pArray the object to search
   * @param pValue the value to search for
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void searchCollection(Collection pArray, Object pValue,
				  DynamoHttpServletRequest pRequest, 
				  DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    boolean found = pArray.contains(pValue);

    if (found)
      pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
    else pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);       
  }

  /**
   * Renders the search operation for Iterator objects
   * @param pIterator the object to search
   * @param pValue the value to search for
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void searchIterator(Iterator pIterator, Object pValue,
				DynamoHttpServletRequest pRequest, 
				DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    boolean found = false;
    while (pIterator.hasNext())
      found = pIterator.next().equals(pValue);

    if (found)
      pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
    else pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);    
  }

  /**
   * Renders the search operation for Enumeration objects
   * @param pEnumeration the object to search
   * @param pValue the value to search for
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void searchEnumeration(Enumeration pEnumeration, Object pValue,
				   DynamoHttpServletRequest pRequest, 
				   DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    boolean found = false;
    while (pEnumeration.hasMoreElements())
      found = pEnumeration.nextElement().equals(pValue);

    if (found)
      pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
    else pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
  }

  /**
   * Renders the search operation for Map objects
   * @param pMap the object to search
   * @param pValue the value to search for
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void searchMap(Map pMap, Object pValue,
                            DynamoHttpServletRequest pRequest, 
                            DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    boolean found = false;
    Iterator entryIterator = pMap.entrySet().iterator();
    while (entryIterator.hasNext() && !found) {
      Map.Entry entry = (Map.Entry) entryIterator.next();
      found = entry.getValue().equals(pValue);
    }

    if (found)
      pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
    else pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
  }

  /**
   * Renders the search operation for Dictionary objects
   * @param pDictionary the object to search
   * @param pValue the value to search for
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void searchDictionary(Dictionary pDictionary, Object pValue,
                                   DynamoHttpServletRequest pRequest, 
                                   DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    boolean found = false;
    Enumeration keys = pDictionary.keys();
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      found = pDictionary.get(key).equals(pValue);
    }

    if (found)
      pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
    else pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
  }
}





