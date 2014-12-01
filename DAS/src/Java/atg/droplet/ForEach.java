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

import java.io.*;
import java.util.*;
import java.text.*;

import java.lang.reflect.*;

import javax.servlet.*;
import javax.servlet.http.*;

import atg.servlet.*;
import atg.nucleus.ServiceEvent;
import atg.nucleus.ServiceException;
import atg.nucleus.logging.LogListener;
import atg.nucleus.naming.ParameterName;
import atg.core.util.StringUtils;
import atg.core.util.NumberTable;
import atg.core.util.ResourceUtils;
import atg.core.util.MapEntry;
import atg.service.util.SortArray;

/**
 * This servlet renders its <i>output</i> parameter for each element in
 * its <i>array</i> parameter.  The <i>array</i> parameter can be a general
 * Collection (Vectors, Lists, Sets), Iterator, Enumeration, Map, Dictionary 
 * or an array. 
 * <p>
 * Each iteration will set three parameters: <i>index</i> and <i>count</i>
 * to the current loop index (0-based) and count (1-based), respectively,
 * and <i>element</i> to the value of that element of the array.
 *
 * <p>A complete description of the parameters to the ForEach droplet are:
 *
 * <dl>
 *
 * <dt>array
 * <dd>The parameter that defines the list of items to output.  This
 * parameter can be a general Collection (Vectors, Lists, Sets), Iterator,
 * Enumeration, Map, Dictionary or an array. 
 *
 * <dt>elementName
 * <dd>The optional parameter that should be used for the name of the element
 * which is bound into the scope of the output oparam.
 *
 * <dt>indexName
 * <dd>The optional parameter that should be used for the name of the index value
 * which is bound into the scope of the output oparam. 
 *
 * <dt>outputStart
 * <dd>This parameter is rendered before any output tags if the array is not empty.
 *
 * <dt>outputEnd
 * <dd>This parameter is rendered after all output tags if the array is not empty.
 *
 * <dt>output
 * <dd>This parameter is rendered for once for each element in the array.
 *
 * <dt>index
 * <dd>This parameter is set to the index of the current element of the array 
 * each time that the <i>output</i> parameter is rendered.  Its value for the
 * first iteration is 0.
 *
 * <dt>count
 * <dd>This parameter is set to the index of the current element of the array 
 * each time that the <i>output</i> parameter is rendered.  Its value for the
 * first iteration is 1.
 *
 * <dt>element
 * <dd>This parameter is set to the current element of the array each
 * time that the <i>output</i> parameter is rendered.
 * 
 * <dt>size
 * <dd>This parameter is set to the size of the array if the array has a size.
 * Arrays of type Enumeration and Iterator have no size so the size will be
 * set to -1.
 *
 * <dt>empty
 * <dd>This optional parameter is rendered if the array contains no elements.
 *
 * <dt>key
 * <dd>This parameter is set once for each element in the array if the
 * array parameter refers to a Map or Dictionary.
 *
 * <dt>sortProperties
 * <dd>A string that specifies how to sort the list of items.  Sorting can
 * either be performed on properties of Java Beans, Dynamic Beans, or Dates,
 * Numbers, Strings.  To do sorting on Java Beans, this parameter 
 * is specified as a comma separated list of property names.  The first name
 * specifies the primary sort, the second specifies the secondary sort, etc.
 * If the first character of each keyword is a +, this sort is performed
 * in ascending order.  If it has a -, it is a descending order.
 * To sort Numbers, Dates, or Strings, this parameter should contain either
 * a single "+" or a single "-" to indicate ascending or descending order
 * respectively.
 * <p>To sort on the key value if a Map or Dictionary value is used, use
 * the name <i>_key</i> to refer to the key in the sortProperties list.
 *
 * <dt>reverseOrder
 * <dd>A boolean value that specifies whether the traversal order in the array
 * should be back to front, or front to back.  'true' sorts from back to front.  This
 * parameter only takes effect if the sortProperties are not set.
 *
 * </dl>
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/ForEach.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ForEach extends DynamoServlet {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/ForEach.java#2 $$Change: 651448 $";


  public final static String KEY = "key";
  public final static String ELEMENT = "element";
  public final static String INDEX = "index";
  public final static String COUNT = "count";
  public final static String SIZE = "size";
  public final static ParameterName ARRAY = ParameterName.getParameterName("array");
  public final static ParameterName OUTPUT_START = ParameterName.getParameterName("outputStart");
  public final static ParameterName OUTPUT_END = ParameterName.getParameterName("outputEnd");
  public final static ParameterName OUTPUT = ParameterName.getParameterName("output");
  public final static ParameterName DEBUG = ParameterName.getParameterName("debug");
  public final static ParameterName EMPTY = ParameterName.getParameterName("empty");
  public final static ParameterName SORT_PROPERTIES = ParameterName.getParameterName("sortProperties");
  public final static ParameterName REVERSE_ORDER = ParameterName.getParameterName("reverseOrder");
  // for backward compatibility
  public final static ParameterName ELEMENT_NAME = ParameterName.getParameterName("elementName");
  public final static ParameterName INDEX_NAME = ParameterName.getParameterName("indexName");

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
   * This method can be overridden by sub-classes to customize how the
   * element parmeter is set.
   */
  protected void setElementParameter(DynamoHttpServletRequest pReq, 
                                     String pElementName, Object pValue) {
    pReq.setParameter(pElementName, pValue);
  }

  public void service(DynamoHttpServletRequest pRequest, 
                      DynamoHttpServletResponse pResponse) 
                throws ServletException, IOException 
  {
    Object arrayParam = getArray(pRequest);
    if (arrayParam == null) {
      if (isLoggingDebug()) 
        logDebug(ResourceUtils.getMsgResource("noArray", MY_RESOURCE_NAME, sResourceBundle));
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
      return;
    }
    arrayParam = getSortedArray(this, arrayParam, pRequest);
    String elementName = pRequest.getParameter(ELEMENT_NAME);

    if (elementName == null)  
      elementName = ELEMENT;

    String indexName = pRequest.getParameter(INDEX_NAME);

    if (indexName == null)  
      indexName = INDEX;

    if (arrayParam instanceof Map.Entry []) {
      /*
       * This is a special case.  When we get a sorted map, we actually
       * have an array of Map.Entry objects.  We compress out the value
       * and use set the additional key parameter
       */
      serviceMapArray((Map.Entry [])arrayParam, indexName, elementName, pRequest, pResponse);
    }
    else if (arrayParam instanceof Object []) {
      serviceArray((Object [])arrayParam, indexName, elementName, pRequest, pResponse);
    }
    else if ((arrayParam instanceof ArrayList) ||
             (arrayParam instanceof Vector)) {
      serviceIndexedList((List)arrayParam,  indexName, elementName, pRequest, pResponse);
    }
    else if (arrayParam instanceof Collection) {
      serviceCollection((Collection)arrayParam,  indexName, elementName, pRequest, pResponse);
    }
    else if (arrayParam instanceof Iterator) {
      serviceIterator((Iterator)arrayParam, indexName, elementName, pRequest, pResponse);
    }
    else if (arrayParam instanceof Enumeration) {
      serviceEnumeration((Enumeration)arrayParam, indexName, elementName, pRequest, pResponse);
    }
    else if (arrayParam instanceof Map) {
      serviceMap((Map)arrayParam, indexName, elementName, pRequest, pResponse);
    }
    else if (arrayParam instanceof Dictionary) {
      serviceDictionary((Dictionary)arrayParam, indexName, elementName, pRequest, pResponse);
    }
    else if (arrayParam.getClass().isArray()) {
      servicePrimitiveArray(arrayParam, indexName, elementName, pRequest, pResponse);
    }
    else if (arrayParam == null) {
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
      if (isLoggingDebug())
        logDebug("Null value for array parameter in ForEach droplet");
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
   * Renders the foreach operation for array objects
   * @param pArray the object to iterate
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceMapArray(Map.Entry [] pArray, String pIndexName, String pElementName,
                              DynamoHttpServletRequest pRequest, 
                              DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    int length = pArray.length;
    pRequest.setParameter(SIZE, NumberTable.getInteger(length));
    if (length == 0) {
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    }
    else { 
      pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
      for (int i = 0; i < length; i++) {
        pRequest.setParameter(COUNT, NumberTable.getInteger(i + 1));
        pRequest.setParameter(pIndexName, NumberTable.getInteger(i));
        pRequest.setParameter(KEY, pArray[i].getKey());
        setElementParameter(pRequest, pElementName, pArray[i].getValue());        
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
      }
      pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
    }
  }

  /**
   * Renders the foreach operation for array objects
   * @param pArray the object to iterate
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceArray(Object [] pArray, String pIndexName, String pElementName,
                              DynamoHttpServletRequest pRequest, 
                              DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    int length = pArray.length;
    pRequest.setParameter(SIZE, NumberTable.getInteger(length));
    if (length == 0) {
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    }
    else { 
      pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
      for (int i = 0; i < length; i++) {
        pRequest.setParameter(COUNT, NumberTable.getInteger(i + 1));
        pRequest.setParameter(pIndexName, NumberTable.getInteger(i));
        setElementParameter(pRequest, pElementName, pArray[i]);        
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
      }
      pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
    }
  }

  /**
   * Renders the foreach operation for primitive array objects using the
   * reflection methods to access values in the array.  This version is
   * slightly slower than the regular array version, but more general and
   * works for int[] etc. arrays.
   *
   * @param pArray the object to iterate
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void servicePrimitiveArray(Object pArray, String pIndexName, String pElementName,
                              DynamoHttpServletRequest pRequest, 
                              DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    int length = Array.getLength(pArray);
    pRequest.setParameter(SIZE, NumberTable.getInteger(length));
    if (length == 0) {
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    }
    else { 
      pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
      for (int i = 0; i < length; i++) {
        pRequest.setParameter(COUNT, NumberTable.getInteger(i + 1));
        pRequest.setParameter(pIndexName, NumberTable.getInteger(i));
        setElementParameter(pRequest, pElementName, Array.get(pArray,i));        
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
      }
      pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
    }
  }

  /**
   * Renders the foreach operation for indexed List objects,
   * i.e. ArrayList or Vector objects.
   * @param pList the object to iterate
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceIndexedList(List pIndexedList,
                                    String pIndexName, String pElementName,
                                    DynamoHttpServletRequest pRequest, 
                                    DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    int length = pIndexedList.size();
    pRequest.setParameter(SIZE, NumberTable.getInteger(length));
    if (length == 0) {
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    }
    else { 
      pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
      for (int ii = 0; ii < length; ii++) {
        pRequest.setParameter(COUNT, NumberTable.getInteger(ii + 1));
        pRequest.setParameter(pIndexName, NumberTable.getInteger(ii));
        setElementParameter(pRequest, pElementName, pIndexedList.get(ii));
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
      }
      pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
    }
  }

  /**
   * Renders the foreach operation for Collection objects
   * @param pCollection the object to iterate
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceCollection(Collection pCollection, String pIndexName, String pElementName,
                                   DynamoHttpServletRequest pRequest, 
                                   DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    pRequest.setParameter(SIZE, NumberTable.getInteger(pCollection.size()));
    serviceIterator(pCollection.iterator(), pIndexName, pElementName, pRequest, pResponse);
  }

  /**
   * Renders the foreach operation for Iterator objects
   * @param pIterator the object to iterate
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceIterator(Iterator pIterator, String pIndexName, String pElementName,
                                 DynamoHttpServletRequest pRequest, 
                                 DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    if (! pIterator.hasNext()) {
      pRequest.serviceLocalParameter (EMPTY, pRequest, pResponse);
    }
    else {
      pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
      int count = 0;
      while (pIterator.hasNext()) {
        pRequest.setParameter(COUNT, NumberTable.getInteger(count + 1));
        pRequest.setParameter(pIndexName, NumberTable.getInteger(count));
        setElementParameter(pRequest, pElementName, pIterator.next());
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        count++;
      }
      pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
    }
  }

  /**
   * Renders the foreach operation for Enumeration objects
   * @param pEnumeration the object to iterate
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceEnumeration(Enumeration pEnumeration, String pIndexName, String pElementName,
                                    DynamoHttpServletRequest pRequest, 
                                    DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    if (!pEnumeration.hasMoreElements ()) {
      pRequest.serviceLocalParameter (EMPTY, pRequest, pResponse);
    }
    else { 
      pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
      int i = 0;
      while (pEnumeration.hasMoreElements()) {
        pRequest.setParameter(COUNT, NumberTable.getInteger(i + 1));
        pRequest.setParameter(pIndexName, NumberTable.getInteger(i)); 
        setElementParameter(pRequest, pElementName, pEnumeration.nextElement());
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        i++;
      }
      pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
    }
  }

  /**
   * Renders the foreach operation for Map objects
   * @param pMap the object to iterate
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceMap(Map pMap, String pIndexName, String pElementName,
                            DynamoHttpServletRequest pRequest, 
                            DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    pRequest.setParameter(SIZE, NumberTable.getInteger(pMap.size()));
    if (pMap.isEmpty()) {
      pRequest.serviceLocalParameter (EMPTY, pRequest, pResponse);
    }
    else {
      Iterator entryIterator = pMap.entrySet().iterator();
      pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
      int count = 0;
      while (entryIterator.hasNext()) {
        Map.Entry entry = (Map.Entry) entryIterator.next();
        Object key = entry.getKey();
        Object value = entry.getValue();
        pRequest.setParameter(COUNT, NumberTable.getInteger(count + 1));
        pRequest.setParameter(pIndexName, NumberTable.getInteger(count));
        setElementParameter(pRequest, pElementName, value);
        pRequest.setParameter(KEY, key);
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        count++;
      }
      pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
    }
  }

  /**
   * Renders the foreach operation for Dictionary objects
   * @param pDictionary the object to iterate
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceDictionary(Dictionary pDictionary, String pIndexName, String pElementName,
                                   DynamoHttpServletRequest pRequest, 
                                   DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    pRequest.setParameter(SIZE, NumberTable.getInteger(pDictionary.size()));
    if (pDictionary.isEmpty()) {
      pRequest.serviceLocalParameter (EMPTY, pRequest, pResponse);
    }
    else {
      Enumeration keys = pDictionary.keys();
      pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
      int i = 0;
      while (keys.hasMoreElements()) {
        Object key = keys.nextElement();
        Object value = pDictionary.get(key);
        pRequest.setParameter(COUNT, NumberTable.getInteger(i + 1));
        pRequest.setParameter(pIndexName, NumberTable.getInteger(i));
        setElementParameter(pRequest, pElementName, value);
        pRequest.setParameter(KEY, key);
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        i++;
      }
      pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
    }
  }

  static void setSortProperties(String pSortProperties, SortArray pSortArray) {
    StringBuffer sortDirs = new StringBuffer();
    String [] list = StringUtils.splitStringAtCharacter(pSortProperties, ',');
    for (int i = 0; i < list.length; i++) {
      list[i] = list[i].trim();
      if (list[i].startsWith("-")) {
        sortDirs.append("-");
        list[i] = list[i].substring(1);
      }
      else {
        sortDirs.append("+");
        if (list[i].startsWith("+"))
          list[i] = list[i].substring(1);
      }
    }
    pSortArray.setSortDirections(sortDirs.toString());
    pSortArray.setSortProperties(list);
  }

  /**
   * This method needs to be static because it is used by the 
   * Range, Table, etc. droplets which are not derived from this droplet.
   * This takes the servlet, the array parameter, and the request and
   * returns the possibly sorted array parameter.
   */
  public static Object getSortedArray(DynamoServlet pServlet, 
                                      Object pArrayParam,
                                      DynamoHttpServletRequest pReq) {
    //Only allow String based properties 
    String sortProperties = (String)pReq.getLocalParameter(SORT_PROPERTIES);
    String reverseOrder = (String)pReq.getLocalParameter(REVERSE_ORDER);

    SortArray sortArray = null;
    if (sortProperties != null && sortProperties.length() > 0) {
      if (pArrayParam == null) return null;
      sortArray = new SortArray();
      setSortProperties(sortProperties, sortArray);

      /* instance the SortArray so that it can log errors through us */
      try {
        // Copy our logging settings to the embedded SortArray object
        sortArray.setLoggingError(pServlet.isLoggingError());
        sortArray.setLoggingWarning(pServlet.isLoggingWarning());
        sortArray.setLoggingInfo(pServlet.isLoggingInfo());
        sortArray.setLoggingDebug(pServlet.isLoggingDebug());
                                  
        LogListener [] l = pServlet.getLogListeners();
        if (l != null) {
          for (int i = 0; i < l.length; i++)
            sortArray.addLogListener(l[i]);
        }
        ServiceEvent ev = new ServiceEvent (pServlet, sortArray, pServlet.getNucleus (), null);
        sortArray.startService(ev);
      }
      catch (ServiceException exc) {}
      /*
       * Now we need to convert the input parameter to an Object [] in
       * order to do the sorting properly 
       */
      pArrayParam = convertToArray(pArrayParam, sortArray);

      // use the request's locale during sorting 
      RequestLocale requestLocale = pReq.getRequestLocale();
      Locale locale = requestLocale != null
                      ? requestLocale.getLocale()
                      : pReq.getLocale();
      if (locale != null)
        sortArray.setLocale(locale);

      sortArray.setInputArray((Object []) pArrayParam);
      pArrayParam = sortArray.getOutputArray();
    }
    else if (reverseOrder != null && reverseOrder.equalsIgnoreCase("true")) {
      // Reverse the order of the objects in the array.
      Object[] newArray = convertToArray(pArrayParam, null);
      if (newArray != null) {
        int arraylength = newArray.length;
        Object[] reversedArray = new Object[arraylength];
        for (int i = 0, j=arraylength - 1; i < arraylength; i++, j--)
          reversedArray[i] = newArray[j];
        pArrayParam = reversedArray;
      }
    }
    return pArrayParam;
  }

  /**
   * Returns an array given an object of type Map, Collection, Dictionary, Enumeration or
   * Interator. If the pArrayParam is an array it is returned unchanged, if it isn't any of
   * those types null is returned.
   */
  private static Object[] convertToArray(Object pArrayParam, SortArray pSort)
  {
    if (pArrayParam == null) return null;

    if (pArrayParam instanceof Collection) {
      return ((Collection)pArrayParam).toArray();
    }
    else if (pArrayParam instanceof Dictionary) {
      if (pSort != null) 
        pSort.setMapEntry(true);
      Dictionary d = (Dictionary) pArrayParam;
      Enumeration e = d.keys();
      ArrayList l = new ArrayList(d.size());
      while (e.hasMoreElements()) {
        Object key = e.nextElement();
        l.add(new MapEntry(key, d.get(key)));
      }
      return l.toArray(new Map.Entry[l.size()]);
    }
    else if (pArrayParam instanceof Map) {
      if (pSort != null) 
        pSort.setMapEntry(true);
      Map m = (Map) pArrayParam;
      return m.entrySet().toArray(new Map.Entry[m.size()]);
    }
    else if (pArrayParam instanceof Enumeration) {
      LinkedList l = new LinkedList();      
      for (Enumeration e = (Enumeration) pArrayParam; e.hasMoreElements();)
        l.add(e.nextElement());
      return l.toArray();
    }
    else if (pArrayParam instanceof Iterator) {
      LinkedList l = new LinkedList();      
      for (Iterator i = (Iterator) pArrayParam; i.hasNext();)
        l.add(i.next());
      return l.toArray();
    }
    else if (pArrayParam.getClass().isArray()) {
      int len = Array.getLength(pArrayParam);
      Object [] newArray = new Object[len];
      for (int i = 0; i < len; i++)
        newArray[i] = Array.get(pArrayParam, i);
      return newArray;
    }
    
    return null;

  }
}

