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

import atg.servlet.*;
import atg.core.util.NumberTable;

import java.io.*;
import java.util.*;
import java.text.*;

import java.lang.reflect.*;

import atg.core.util.ResourceUtils;

/**
 * This servlet renders its <i>output</i> parameter for a subset of
 * elements in its <i>array</i> parameter.  In particular, <i>howMany</i>
 * items starting at (1-based) index <i>start</i> are displayed.  The
 * <i>array</i> parameter can be general Collection (Vectors, Lists, Sets),
 * Iterator, Enumeration, Map, Dictionary or an array.
 *
 * <p>Each iteration will set several parameters: <i>index</i> and
 * <i>count</i> to the current loop index (0-based) and count (1-based),
 * respectively; <i>element</i> to the value of that element of the array;
 * <i>hasPrev</i> and <i>hasNext</i> to indicate whether there are array
 * items before or after the current subset, respectively; plus several
 * others to help render the previous or next range of elements.
 *
 * <p>A complete description of the parameters to the Range droplet are:
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
 * <dt>start
 * <dd>Specifies the starting index (1-based).  For example, to start at
 * the beginning of the array, set <i>start</i> to 1.  If <i>start</i>
 * points past the end of the array, the <i>empty</i> parameter will be
 * rendered.
 *
 * <dt>howMany
 * <dd>Specifies the number of items in the array set to display.  If the
 * combination of <i>start</i> and <i>howMany</i> point past the end of
 * the array, rendering stops after the end of the array is reached.
 *
 * <dt>outputStart
 * <dd>This parameter is rendered before any output tags if the subset of
 * the array being displayed is not empty.
 *
 * <dt>outputEnd
 * <dd>This parameter is rendered after all output tags if the subset of
 * the array being displayed is not empty.
 *
 * <dt>output
 * <dd>This parameter is rendered once for each element in the subset of the
 * array that gets displayed.
 *
 * <dt>empty
 * <dd>This optional parameter is rendered if the array itself, or the
 * requested subset of the array, contains no elements.
 *
 * <dt>index
 * <dd>This parameter is set to the (0-based) index of the current element
 * of the array each time the <i>output</i> parameter is rendered.  For
 * example, if <i>start</i> is set to 1, the value of <i>index</i> for the
 * first iteration is 0.
 *
 * <dt>count
 * <dd>This parameter is set to the (1-based) count of the current element
 * of the array each time the <i>output</i> parameter is rendered.  For
 * example, if <i>start</i> is set to 1, the value of <i>count</i> for the
 * first iteration is 1.
 *
 * <dt>element
 * <dd>This parameter is set to the current element of the array each
 * time the <i>output</i> parameter is rendered.
 *
 * <dt>key
 * <dd>This parameter is set once for each element in the array set if the
 * array parameter refers to a Map or Dictionary.
 *
 * <dt>end
 * <dd>This parameter is set before any of the output parameters are
 * rendered.  It indicates the (1-based) count of the last element in
 * the current array set.
 *
 * <dt>size
 * <dd>This parameter is set before any of the output parameters are
 * rendered.  It indicates the number of elements in the full array.
 *
 * <dt>hasPrev
 * <dd>This parameter is set to true or false before any of the output
 * parameters are rendered.  It indicates whether there are any array
 * items before the current array set.
 *
 * <dt>prevStart
 * <dd>This parameter is set before any of the output parameters are
 * rendered, and only if <i>hasPrev</i> is true.  It indicates the value
 * of <i>start</i> that should be used to display the previous array set.
 *
 * <dt>prevEnd
 * <dd>This parameter is set before any of the output parameters are
 * rendered, and only if <i>hasPrev</i> is true.  It indicates the
 * (1-based) count of the last element in the previous array set.
 *
 * <dt>prevHowMany
 * <dd>This parameter is set before any of the output parameters are
 * rendered, and only if <i>hasPrev</i> is true.  It indicates the
 * number of elements in the previous array set.
 *
 * <dt>hasNext
 * <dd>This parameter is set to true or false before any of the output
 * parameters are rendered.  It indicates whether there are any array
 * items after the current array set.
 *
 * <dt>nextStart
 * <dd>This parameter is set before any of the output parameters are
 * rendered, and only if <i>hasNext</i> is true.  It indicates the value
 * of <i>start</i> that should be used to display the next array set.
 *
 * <dt>nextEnd
 * <dd>This parameter is set before any of the output parameters are
 * rendered, and only if <i>hasNext</i> is true.  It indicates the
 * (1-based) count of the last element in the next array set.
 *
 * <dt>nextHowMany
 * <dd>This parameter is set before any of the output parameters are
 * rendered, and only if <i>hasNext</i> is true.  It indicates the
 * number of elements in the next array set.
 *
 * <dt>reverseOrder
 * <dd>A boolean value that specifies whether the traversal order in the array
 * should be back to front, or front to back.  'true' sorts from back to front.  This
 * parameter only takes effect if the sortProperties are not set.
 *
 * </dl>
 *
 * @author Natalya Cohen
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/Range.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class Range extends DynamoServlet {
  //-------------------------------------
  // Constants
  //-------------------------------------

  static final String MY_RESOURCE_NAME = "atg.droplet.DropletResources";

  /** Class version string **/
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/Range.java#2 $$Change: 651448 $";

  /** Resource bundle **/
  private static java.util.ResourceBundle sResourceBundle =
  java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  // input params
  public final static String ARRAY = "array";
  public final static String START = "start";
  public final static String HOW_MANY = "howMany";
  // for backward compatibility
  public final static String INDEX_NAME = "indexName";
  public final static String ELEMENT_NAME = "elementName";

  // input oparams
  public final static String OUTPUT_START = "outputStart";
  public final static String OUTPUT_END = "outputEnd";
  public final static String OUTPUT = "output";
  public final static String EMPTY = "empty";

  // output params
  public final static String INDEX = "index";
  public final static String COUNT = "count";
  public final static String ELEMENT = "element";
  public final static String KEY = "key";
  public final static String END = "end";
  public final static String SIZE = "size";
  public final static String HAS_PREV = "hasPrev";
  public final static String HAS_NEXT = "hasNext";
  public final static String PREV_START = "prevStart";
  public final static String PREV_END = "prevEnd";
  public final static String PREV_HOW_MANY = "prevHowMany";
  public final static String NEXT_START = "nextStart";
  public final static String NEXT_END = "nextEnd";
  public final static String NEXT_HOW_MANY = "nextHowMany";

  //-------------------------------------
  // Methods
  //-------------------------------------

  //-------------------------------------
  /**
   * Gets the array-like value (e.g., array, Vector, Enumeration)
   * to be used by this droplet from the request.  Subclasses may
   * override this method to extract the array by some means other
   * than simply looking up its parameter value in the request.
   **/
  public Object getArray(DynamoHttpServletRequest pRequest) {
    return pRequest.getObjectParameter(ARRAY);
  }

  //-------------------------------------
  /**
   * Sets the HAS_PREV/NEXT, PREV/NEXT_START, PREV/NEXT_END,
   * PREV/NEXT_HOW_MANY, SIZE, and END parameters based on the input
   * parameter values and the length of the array.
   **/
  public static void setPrevNextParameters(int pStart, int pHowMany, int pLength,
             DynamoHttpServletRequest pRequest)
  {
    if (pStart > 1) {
      pRequest.setParameter(HAS_PREV, Boolean.TRUE);
      int prevStart = Math.max(1, pStart - pHowMany);
      pRequest.setParameter(PREV_START, NumberTable.getInteger(prevStart));
      pRequest.setParameter(PREV_END, NumberTable.getInteger(pStart - 1));
      pRequest.setParameter(PREV_HOW_MANY, NumberTable.getInteger(pStart - prevStart));
    } else
      pRequest.setParameter(HAS_PREV, Boolean.FALSE);

    int nextStart = pStart + pHowMany;
    if (nextStart <= pLength) {
      pRequest.setParameter(HAS_NEXT, Boolean.TRUE);
      pRequest.setParameter(NEXT_START, NumberTable.getInteger(nextStart));
      int nextEnd = Math.min(pLength, nextStart - 1 + pHowMany);
      pRequest.setParameter(NEXT_END, NumberTable.getInteger(nextEnd));
      pRequest.setParameter(NEXT_HOW_MANY, NumberTable.getInteger(nextEnd - nextStart + 1));
    } else
      pRequest.setParameter(HAS_NEXT, Boolean.FALSE);

    pRequest.setParameter(SIZE, NumberTable.getInteger(pLength));

    int end = Math.min(pLength, pStart + pHowMany - 1);
    pRequest.setParameter(END, NumberTable.getInteger(end));
  }

  //-------------------------------------
  // DynamoServlet method overrides
  //-------------------------------------

  //-------------------------------------
  /**
   * Called when a request needs to be processed.
   *
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error
   * occurred processing this request
   * @exception IOException if an error occurred reading data from
   * the request or writing data to the response
   **/
  public void service(DynamoHttpServletRequest pRequest,
                      DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    // get and validate all the input params...
    Object arrayParam = getArray(pRequest);
    if (arrayParam == null) {
      //Sending a null array is okay and should only generate a log message, not an error.
      //The "EMPTY" droplet parameter is there to handle the case where the array is empty/null.
      if (isLoggingDebug())
        logDebug(ResourceUtils.getMsgResource("noArray", MY_RESOURCE_NAME, sResourceBundle));
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
      return;
    }
    arrayParam = ForEach.getSortedArray(this, arrayParam, pRequest);

    int start = 1;
    String startParam = pRequest.getParameter(START);
    if (startParam != null) {
      try {
  start = Integer.parseInt(startParam);
  if (start <= 0) {
    if (isLoggingError()) {
      Object[] args = { NumberTable.getInteger(start) };
      logError(ResourceUtils.getMsgResource("illegalStart", MY_RESOURCE_NAME,
              sResourceBundle, args));
    }
    return;
  }
      } catch (NumberFormatException e) {
  if (isLoggingError()) {
    Object[] args = { startParam };
    logError(ResourceUtils.getMsgResource("illegalStart", MY_RESOURCE_NAME,
            sResourceBundle, args));
  }
  return;
      }
    }

    int howMany;
    String howManyParam = pRequest.getParameter(HOW_MANY);
    if (howManyParam == null) {
      if (isLoggingError())
  logError(ResourceUtils.getMsgResource("noHowMany", MY_RESOURCE_NAME,
                sResourceBundle));
      return;
    }
    try {
      howMany = Integer.parseInt(howManyParam);
      if (howMany < 0) {
  if (isLoggingError()) {
    Object[] args = { NumberTable.getInteger(howMany) };
    logError(ResourceUtils.getMsgResource("illegalHowMany", MY_RESOURCE_NAME,
            sResourceBundle, args));
  }
  return;
      }
    } catch (NumberFormatException e) {
      if (isLoggingError()) {
  Object[] args = { howManyParam };
  logError(ResourceUtils.getMsgResource("illegalHowMany", MY_RESOURCE_NAME,
                sResourceBundle, args));
      }
      return;
    }

    String indexName = pRequest.getParameter(INDEX_NAME);
    if (indexName == null)
      indexName = INDEX;

    String elementName = pRequest.getParameter(ELEMENT_NAME);
    if (elementName == null)
      elementName = ELEMENT;

    // array param is an array
    if (arrayParam.getClass().isArray()) {
      serviceArray(arrayParam, start, howMany, indexName, elementName, pRequest, pResponse);
    }
    // array param is a List
    else if (arrayParam instanceof List) {
      List list = (List) arrayParam;
      serviceList(list, start, howMany, indexName, elementName, pRequest, pResponse);
    }
    // array param is a Collection
    else if (arrayParam instanceof Collection) {
      Collection collection = (Collection)arrayParam;
      serviceCollection(collection, start, howMany, indexName, elementName, pRequest, pResponse);
    }
    // array param is a Iterator
    else if (arrayParam instanceof Iterator) {
      Iterator iterator = (Iterator)arrayParam;
      serviceIterator(iterator, start, howMany, indexName, elementName, pRequest, pResponse);
    }
    // array param is an Enumeration
    else if (arrayParam instanceof Enumeration) {
      Enumeration enumeration = (Enumeration) arrayParam;
      serviceEnumeration(enumeration, start, howMany, indexName, elementName, pRequest, pResponse);
    }
    // array param is a Map
    else if (arrayParam instanceof Map) {
      Map map = (Map)arrayParam;
      serviceMap(map, start, howMany, indexName, elementName, pRequest, pResponse);
    }
    // array param is a Dictionary
    else if (arrayParam instanceof Dictionary) {
      Dictionary dictionary = (Dictionary) arrayParam;
      serviceDictionary(dictionary, start, howMany, indexName, elementName, pRequest, pResponse);

    }
    // unrecognized array param
    else {
      if (isLoggingError()) {
        Object[] args = {arrayParam, arrayParam.getClass()};
  logError(ResourceUtils.getMsgResource("illegalArrayWithClassInfo", MY_RESOURCE_NAME,
                sResourceBundle, args));
      }
    }
  }

  /**
   * Renders the range operation for array objects
   * @param pArray the object to iterate
   * @param pStart Specifies the starting index (1-based).  For example, to start at
   * the beginning of the array, set <i>start</i> to 1.  If <i>start</i>
   * points past the end of the array, the <i>empty</i> parameter will be
   * rendered.
   * @param pHowMany Specifies the number of items in the array set to display.  If the
   * combination of <i>start</i> and <i>howMany</i> point past the end of
   * the array, rendering stops after the end of the array is reached.
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceArray(Object pArray, int pStart, int pHowMany,
                              String pIndexName, String pElementName,
                              DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    serviceRange(pArray, pStart, pHowMany, pIndexName, pElementName, pRequest, pResponse);
  }

  /**
   * Renders the range operation for List objects
   * @param pList the object to iterate
   * @param pStart Specifies the starting index (1-based).  For example, to start at
   * the beginning of the array, set <i>start</i> to 1.  If <i>start</i>
   * points past the end of the array, the <i>empty</i> parameter will be
   * rendered.
   * @param pHowMany Specifies the number of items in the array set to display.  If the
   * combination of <i>start</i> and <i>howMany</i> point past the end of
   * the array, rendering stops after the end of the array is reached.
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceList(List pList, int pStart, int pHowMany,
                             String pIndexName, String pElementName,
                             DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    serviceRange(pList, pStart, pHowMany, pIndexName, pElementName, pRequest, pResponse);
  }

  /**
   * Renders the range operation for Collection objects
   * @param pCollection the object to iterate
   * @param pStart Specifies the starting index (1-based).  For example, to start at
   * the beginning of the array, set <i>start</i> to 1.  If <i>start</i>
   * points past the end of the array, the <i>empty</i> parameter will be
   * rendered.
   * @param pHowMany Specifies the number of items in the array set to display.  If the
   * combination of <i>start</i> and <i>howMany</i> point past the end of
   * the array, rendering stops after the end of the array is reached.
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceCollection(Collection pCollection, int pStart, int pHowMany,
                                   String pIndexName, String pElementName,
                                   DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    Object [] array = pCollection.toArray();
    serviceArray(array, pStart, pHowMany, pIndexName, pElementName, pRequest, pResponse);
  }

  /**
   * Renders the range operation for Interator objects
   * @param pIterator the object to iterate
   * @param pStart Specifies the starting index (1-based).  For example, to start at
   * the beginning of the array, set <i>start</i> to 1.  If <i>start</i>
   * points past the end of the array, the <i>empty</i> parameter will be
   * rendered.
   * @param pHowMany Specifies the number of items in the array set to display.  If the
   * combination of <i>start</i> and <i>howMany</i> point past the end of
   * the array, rendering stops after the end of the array is reached.
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceIterator(Iterator pIterator, int pStart, int pHowMany,
                                 String pIndexName, String pElementName,
                                 DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    if (!pIterator.hasNext())
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    else {
      List list = new ArrayList();
      while (pIterator.hasNext())
        list.add(pIterator.next());
      serviceList(list, pStart, pHowMany, pIndexName, pElementName, pRequest, pResponse);
    }
  }

  /**
   * Renders the range operation for Enumeration objects
   * @param pEnumeration the object to iterate
   * @param pStart Specifies the starting index (1-based).  For example, to start at
   * the beginning of the array, set <i>start</i> to 1.  If <i>start</i>
   * points past the end of the array, the <i>empty</i> parameter will be
   * rendered.
   * @param pHowMany Specifies the number of items in the array set to display.  If the
   * combination of <i>start</i> and <i>howMany</i> point past the end of
   * the array, rendering stops after the end of the array is reached.
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceEnumeration(Enumeration pEnumeration, int pStart, int pHowMany,
                                    String pIndexName, String pElementName,
                                    DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    if (!pEnumeration.hasMoreElements())
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    else {
      List list = new ArrayList();
      while (pEnumeration.hasMoreElements())
        list.add(pEnumeration.nextElement());
      serviceList(list, pStart, pHowMany, pIndexName, pElementName, pRequest, pResponse);
    }
  }

  /**
   * Renders the range operation for Map objects
   * @param pMap the object to iterate
   * @param pStart Specifies the starting index (1-based).  For example, to start at
   * the beginning of the array, set <i>start</i> to 1.  If <i>start</i>
   * points past the end of the array, the <i>empty</i> parameter will be
   * rendered.
   * @param pHowMany Specifies the number of items in the array set to display.  If the
   * combination of <i>start</i> and <i>howMany</i> point past the end of
   * the array, rendering stops after the end of the array is reached.
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceMap(Map pMap, int pStart, int pHowMany,
                            String pIndexName, String pElementName,
                            DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    if (pMap.isEmpty()) {
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    }
    else {
      Set entrySet = pMap.entrySet();
      if (entrySet != null) {
        Object [] entries = entrySet.toArray();
        serviceArray(entries, pStart, pHowMany, pIndexName, pElementName, pRequest, pResponse);
      }
    }
  }

  /**
   * Renders the range operation for Dictionary objects
   * @param pDictionary the object to iterate
   * @param pStart Specifies the starting index (1-based).  For example, to start at
   * the beginning of the array, set <i>start</i> to 1.  If <i>start</i>
   * points past the end of the array, the <i>empty</i> parameter will be
   * rendered.
   * @param pHowMany Specifies the number of items in the array set to display.  If the
   * combination of <i>start</i> and <i>howMany</i> point past the end of
   * the array, rendering stops after the end of the array is reached.
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceDictionary(Dictionary pDictionary, int pStart, int pHowMany,
                                   String pIndexName, String pElementName,
                                   DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    if (pDictionary.isEmpty()) {
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    }
    else {
      int length = pDictionary.size();
      Enumeration keys = pDictionary.keys();

      List listKeys = new ArrayList(length);
      while (keys.hasMoreElements())
        listKeys.add(keys.nextElement());

      length = listKeys.size();
      if (pStart > length)
        pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
      else {
        setPrevNextParameters(pStart, pHowMany, length, pRequest);
        pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
        int end = Math.min(pStart - 1 + pHowMany, length);
        for (int i = pStart - 1; i < end; i++) {
          pRequest.setParameter(pIndexName, NumberTable.getInteger(i));
          pRequest.setParameter(COUNT, NumberTable.getInteger(i + 1));
          Object key = listKeys.get(i);
          pRequest.setParameter(pElementName, pDictionary.get(key));
          pRequest.setParameter(KEY, key);
          pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        }
        pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
      }
    }
  }

  void serviceRange(Object pArrayParam, int pStart, int pHowMany,
                    String pIndexName, String pElementName,
                    DynamoHttpServletRequest pRequest,
                    DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    int length = 0;
    boolean bIsEntryArray = pArrayParam instanceof Map.Entry[];

    List listArrayParam = null;
    Map.Entry[] mapEntryArrayParam = null;

    if (pArrayParam instanceof List) {
      listArrayParam = (List)pArrayParam;
      length = listArrayParam.size();
    }
    else {
      length = Array.getLength(pArrayParam);
    }

    if (bIsEntryArray) {
      mapEntryArrayParam = (Map.Entry[])pArrayParam;
    }

    if ((length == 0) || (pStart > length))
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    else {
      setPrevNextParameters(pStart, pHowMany, length, pRequest);
      pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
      int end = Math.min(pStart - 1 + pHowMany, length);
      for (int i = pStart - 1; i < end; i++) {
        pRequest.setParameter(pIndexName, NumberTable.getInteger(i));
        pRequest.setParameter(COUNT, NumberTable.getInteger(i + 1));
        if (listArrayParam != null) {
          setElementParameter(listArrayParam.get(i), pElementName, pRequest, pResponse);
        }
        else if (mapEntryArrayParam != null) {
          setElementParameter(mapEntryArrayParam[i], pElementName,
                                   pRequest, pResponse);
        }
        else
        {
          setElementParameter(Array.get(pArrayParam, i), pElementName, pRequest, pResponse);
        }
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
      }
      pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
    }
  }

  void setElementParameter(Object pElementParam, String pElementParamName,
                           DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    pRequest.setParameter(pElementParamName, pElementParam);
  }

  void setElementParameter(Map.Entry pElementParam, String pElementParamName,
                           DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    pRequest.setParameter(pElementParamName, pElementParam.getValue());
    pRequest.setParameter(KEY, pElementParam.getKey());
  }

  //-------------------------------------
}

