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
 * This droplet extends the functionality of ForEach by allowing the
 * output to be rendered in a table-like format.  The <i>numColumns</i>
 * parameter specifies the number of columns the array should be
 * partitioned into.  The array is striped across the columns.
 *
 * <p>The <i>output</i> parameter is rendered for each "cell" in the
 * table.  Note that if the array size is not a multiple of the number of
 * columns, <i>output</i> is rendered with a null <i>element</i> parameter
 * for the remaining columns in the last row.  A Switch droplet can be
 * used to conditionally "fill in" the missing items.
 *
 * <p>A complete description of the parameters to the TableForEach droplet
 * are:
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
 * <dt>numColumns
 * <dd>Specifies the number of columns to partition the array into.
 *
 * <dt>outputStart
 * <dd>This parameter is rendered before any output tags if the array is not
 * empty.
 *
 * <dt>outputEnd
 * <dd>This parameter is rendered after all output tags if the array is not
 * empty.
 *
 * <dt>outputRowStart
 * <dd>This parameter is rendered once for each row, before any output tags
 * for that row.
 *
 * <dt>outputRowEnd
 * <dd>This parameter is rendered once for each row, after all output tags
 * for that row.
 *
 * <dt>output
 * <dd>This parameter is rendered once for each element in the array.
 *
 * <dt>empty
 * <dd>This optional parameter is rendered if the array contains no elements.
 *
 * <dt>rowIndex
 * <dd>This parameter is set to the current row index each time one of the
 * output parameters is rendered.
 *
 * <dt>columnIndex
 * <dd>This parameter is set to the current column index each time one of the
 * output parameters is rendered.

 * <dt>index
 * <dd>This parameter is set to the (0-based) index of the current element
 * of the array each time the <i>output</i> parameter is rendered.
 *
 * <dt>count
 * <dd>This parameter is set to the (1-based) count of the current element
 * of the array each time the <i>output</i> parameter is rendered.
 *
 * <dt>element
 * <dd>This parameter is set to the current element of the array each
 * time the <i>output</i> parameter is rendered.  If the array size is not
 * a multiple of the number of columns, <i>element</i> is set to null for
 * the remaining items in the last row.
 *
 * <dt>key
 * <dd>This parameter is set once for each element in the array if the
 * array parameter refers to a Map or Dictionary.
 *
 * <dt>reverseOrder
 * <dd>A boolean value that specifies whether the traversal order in the array
 * should be back to front, or front to back.  'true' sorts from back to front.  This
 * parameter only takes effect if the sortProperties are not set.
 *
 * </dl>
 *
 * @see ForEach
 * @author Natalya Cohen
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/TableForEach.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class TableForEach extends DynamoServlet {
  //-------------------------------------
  // Constants
  //-------------------------------------

  static final String MY_RESOURCE_NAME = "atg.droplet.DropletResources";

  /** Class version string **/
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/TableForEach.java#2 $$Change: 651448 $";

  /** Resource bundle **/
  private static java.util.ResourceBundle sResourceBundle =
  java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  // input params
  public final static String ARRAY = "array";
  public final static String NUM_COLUMNS = "numColumns";
  // for backward compatibility
  public final static String INDEX_NAME = "indexName";
  public final static String ELEMENT_NAME = "elementName";

  // input oparams
  public final static String OUTPUT_START = "outputStart";
  public final static String OUTPUT_END = "outputEnd";
  public final static String OUTPUT_ROW_START = "outputRowStart";
  public final static String OUTPUT_ROW_END = "outputRowEnd";
  public final static String OUTPUT = "output";
  public final static String EMPTY = "empty";

  // output params
  public final static String ROW_INDEX = "rowIndex";
  public final static String COLUMN_INDEX = "columnIndex";
  public final static String INDEX = "index";
  public final static String COUNT = "count";
  public final static String ELEMENT = "element";
  public final static String KEY = "key";

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
   */
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
    // Calling ForEach.getSortedArray could return a different type than
    // passed in; save the original type
    Class originalClass = arrayParam.getClass();
    arrayParam = ForEach.getSortedArray(this, arrayParam, pRequest);

    int numColumns;
    String numColumnsParam = pRequest.getParameter(NUM_COLUMNS);
    if (numColumnsParam == null) {
      if (isLoggingError())
  logError(ResourceUtils.getMsgResource("noNumColumns", MY_RESOURCE_NAME, sResourceBundle));
      return;
    }
    try {
      numColumns = Integer.parseInt(numColumnsParam);
      if (numColumns <= 0) {
        if (isLoggingError()) {
          Object[] args = { NumberTable.getInteger(numColumns) };
          logError(ResourceUtils.getMsgResource("illegalNumColumns", 
                                                MY_RESOURCE_NAME, 
                                                sResourceBundle, args));
        }
        return;
      }
    } catch (NumberFormatException e) {
      if (isLoggingError()) {
        Object[] args = { numColumnsParam };
        logError(ResourceUtils.getMsgResource("illegalNumColumns", 
                                              MY_RESOURCE_NAME, 
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

    // array param is an array, or is converted from a Map
    if (arrayParam.getClass().isArray()) {
      serviceArray(arrayParam, numColumns, indexName, elementName, pRequest, pResponse, originalClass);
    }
    // array param is a List
    else if (arrayParam instanceof List) {
      List list = (List) arrayParam;
      serviceList(list, numColumns, indexName, elementName, pRequest, pResponse);
    }
    // array param is a Collection
    else if (arrayParam instanceof Collection) {
      Collection collection = (Collection)arrayParam;
      serviceCollection(collection, numColumns, indexName, elementName, pRequest, pResponse);
    }
    // array param is a Iterator
    else if (arrayParam instanceof Iterator) {
      Iterator iterator = (Iterator)arrayParam;
      serviceIterator(iterator, numColumns, indexName, elementName, pRequest, pResponse);
    }
    // array param is an Enumeration
    else if (arrayParam instanceof Enumeration) {
      Enumeration enumeration = (Enumeration) arrayParam;
      serviceEnumeration(enumeration, numColumns, indexName, elementName, pRequest, pResponse);
    }
    // array param is a Map
    else if (arrayParam instanceof Map) {
      Map map = (Map)arrayParam;
      serviceMap(map, numColumns, indexName, elementName, pRequest, pResponse);
    }
    // array param is a Dictionary
    else if (arrayParam instanceof Dictionary) {
      Dictionary dictionary = (Dictionary) arrayParam;
      serviceDictionary(dictionary, numColumns, indexName, elementName, pRequest, pResponse);
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
   * @param pNumColumns Specifies the number of columns to partition the array into.
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceArray(Object pArray, int pNumColumns,
                              String pIndexName, String pElementName,
                              DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse,
                              Class pOriginalClass)
       throws ServletException, IOException
  {
    int length = Array.getLength(pArray);
    if (length == 0)
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    else {
      pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
      // treat Maps and dictionaries the same, because the result, if
      // we sort, will be an array of Map.Entries. bug#78159
      boolean wasMap = (Map.class.isAssignableFrom(pOriginalClass) ||
                        Dictionary.class.isAssignableFrom(pOriginalClass));
      int i = 0, irow = 0, icol = 0;
      int lastColumn = pNumColumns - 1;
      for ( ; i < length; i++) {
        /* Set these before we output the first row...*/
        pRequest.setParameter(COLUMN_INDEX, NumberTable.getInteger(icol));
        pRequest.setParameter(pIndexName, NumberTable.getInteger(i));
        pRequest.setParameter(COUNT, NumberTable.getInteger(i + 1));
        if (wasMap) {
          Map.Entry entry = (Map.Entry) Array.get(pArray, i);
          pRequest.setParameter(pElementName, entry.getValue());
          pRequest.setParameter(KEY, entry.getKey());
        } else {
          pRequest.setParameter(pElementName, Array.get(pArray, i));
        }
        if (icol == 0) {
          pRequest.setParameter(ROW_INDEX, NumberTable.getInteger(irow));
          pRequest.serviceLocalParameter(OUTPUT_ROW_START, pRequest, pResponse);
        }
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        if (icol == lastColumn) {
          pRequest.serviceLocalParameter(OUTPUT_ROW_END, pRequest, pResponse);
        }
        icol = (icol + 1) % pNumColumns;
        if (icol == 0) irow++;
      }
      if (icol != 0) {
        // finish up the last row
        for ( ; icol < pNumColumns; icol++, i++) {
          pRequest.setParameter(COLUMN_INDEX, NumberTable.getInteger(icol));
          pRequest.setParameter(pIndexName, NumberTable.getInteger(i));
          pRequest.setParameter(COUNT, NumberTable.getInteger(i + 1));
          pRequest.setParameter(pElementName, null);
          pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        }
        pRequest.serviceLocalParameter(OUTPUT_ROW_END, pRequest, pResponse);
      }
      pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
    }
  }

  /**
   * Renders the range operation for List objects
   * @param pList the object to iterate
   * @param pNumColumns Specifies the number of columns to partition the array into.
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceList(List pList, int pNumColumns,
                             String pIndexName, String pElementName,
                             DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    int length = pList.size();
    if (length == 0)
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    else {
      pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
      int i = 0, irow = 0, icol = 0;
      int lastColumn = pNumColumns - 1;
      for ( ; i < length; i++) {
        /* Set these before we output the first row...*/
        pRequest.setParameter(COLUMN_INDEX, NumberTable.getInteger(icol));
        pRequest.setParameter(pIndexName, NumberTable.getInteger(i));
        pRequest.setParameter(COUNT, NumberTable.getInteger(i + 1));
        pRequest.setParameter(pElementName, pList.get(i));
        if (icol == 0) {
          pRequest.setParameter(ROW_INDEX, NumberTable.getInteger(irow));
          pRequest.serviceLocalParameter(OUTPUT_ROW_START, pRequest, pResponse);
        }
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        if (icol == lastColumn) {
          pRequest.serviceLocalParameter(OUTPUT_ROW_END, pRequest, pResponse);
        }
        icol = (icol + 1) % pNumColumns;
        if (icol == 0) irow++;
      }
      if (icol != 0) {
        // finish up the last row
        for ( ; icol < pNumColumns; icol++, i++) {
          pRequest.setParameter(COLUMN_INDEX, NumberTable.getInteger(icol));
          pRequest.setParameter(pIndexName, NumberTable.getInteger(i));
          pRequest.setParameter(COUNT, NumberTable.getInteger(i + 1));
          pRequest.setParameter(pElementName, null);
          pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        }
        pRequest.serviceLocalParameter(OUTPUT_ROW_END, pRequest, pResponse);
      }
      pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
    }

  }

  /**
   * Renders the range operation for Collection objects
   * @param pCollection the object to iterate
   * @param pNumColumns Specifies the number of columns to partition the array into.
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceCollection(Collection pCollection, int pNumColumns,
                                   String pIndexName, String pElementName,
                                   DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    serviceIterator(pCollection.iterator(), pNumColumns, pIndexName, pElementName, pRequest, pResponse);
  }

  /**
   * Renders the range operation for Iterator objects
   * @param pIterator the object to iterate
   * @param pNumColumns Specifies the number of columns to partition the array into.
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceIterator(Iterator pIterator, int pNumColumns,
                                 String pIndexName, String pElementName,
                                 DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    if (!pIterator.hasNext())
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    else {
      pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
      int i = 0, irow = 0, icol = 0;
      int lastColumn = pNumColumns - 1;
      while (pIterator.hasNext()) {
        if (icol == 0) {
          pRequest.setParameter(ROW_INDEX, NumberTable.getInteger(irow));
          pRequest.serviceLocalParameter(OUTPUT_ROW_START, pRequest, pResponse);
        }
        pRequest.setParameter(COLUMN_INDEX, NumberTable.getInteger(icol));
        pRequest.setParameter(pIndexName, NumberTable.getInteger(i));
        pRequest.setParameter(COUNT, NumberTable.getInteger(i + 1));
        pRequest.setParameter(pElementName, pIterator.next());
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        if (icol == lastColumn) {
          pRequest.serviceLocalParameter(OUTPUT_ROW_END, pRequest, pResponse);
        }
        i++;
        icol = (icol + 1) % pNumColumns;
        if (icol == 0) irow++;
      }
      if (icol != 0) {
        // finish up the last row
        for ( ; icol < pNumColumns; icol++, i++) {
          pRequest.setParameter(COLUMN_INDEX, NumberTable.getInteger(icol));
          pRequest.setParameter(pIndexName, NumberTable.getInteger(i));
          pRequest.setParameter(COUNT, NumberTable.getInteger(i + 1));
          pRequest.setParameter(pElementName, null);
          pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        }
        pRequest.serviceLocalParameter(OUTPUT_ROW_END, pRequest, pResponse);
      }
      pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
    }
  }

  /**
   * Renders the range operation for Enumeration objects
   * @param pEnumeration the object to iterate
   * @param pNumColumns Specifies the number of columns to partition the array into.
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceEnumeration(Enumeration pEnumeration, int pNumColumns,
                                    String pIndexName, String pElementName,
                                    DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    if (!pEnumeration.hasMoreElements())
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    else {
      pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
      int i = 0, irow = 0, icol = 0;
      int lastColumn = pNumColumns - 1;
      while (pEnumeration.hasMoreElements()) {
        if (icol == 0) {
          pRequest.setParameter(ROW_INDEX, NumberTable.getInteger(irow));
          pRequest.serviceLocalParameter(OUTPUT_ROW_START, pRequest, pResponse);
        }
        pRequest.setParameter(COLUMN_INDEX, NumberTable.getInteger(icol));
        pRequest.setParameter(pIndexName, NumberTable.getInteger(i));
        pRequest.setParameter(COUNT, NumberTable.getInteger(i + 1));
        pRequest.setParameter(pElementName, pEnumeration.nextElement());
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        if (icol == lastColumn) {
          pRequest.serviceLocalParameter(OUTPUT_ROW_END, pRequest, pResponse);
        }
        i++;
        icol = (icol + 1) % pNumColumns;
        if (icol == 0) irow++;
      }
      if (icol != 0) {
        // finish up the last row
        for ( ; icol < pNumColumns; icol++, i++) {
          pRequest.setParameter(COLUMN_INDEX, NumberTable.getInteger(icol));
          pRequest.setParameter(pIndexName, NumberTable.getInteger(i));
          pRequest.setParameter(COUNT, NumberTable.getInteger(i + 1));
          pRequest.setParameter(pElementName, null);
          pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        }
        pRequest.serviceLocalParameter(OUTPUT_ROW_END, pRequest, pResponse);
      }
      pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
    }
  }

  /**
   * Renders the range operation for Map objects
   * @param pMap the object to iterate
   * @param pNumColumns Specifies the number of columns to partition the array into.
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceMap(Map pMap, int pNumColumns,
                            String pIndexName, String pElementName,
                            DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    if (pMap.isEmpty()) {
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    }
    else {
      Set keySet = pMap.keySet();
      Iterator keys = keySet.iterator();
      pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
      int i = 0, irow = 0, icol = 0;
      int lastColumn = pNumColumns - 1;
      while (keys.hasNext()) {
        Object key = keys.next();
        Object value = pMap.get(key);

        if (icol == 0) {
          pRequest.setParameter(ROW_INDEX, NumberTable.getInteger(irow));
          pRequest.serviceLocalParameter(OUTPUT_ROW_START, pRequest, pResponse);
        }
        pRequest.setParameter(COLUMN_INDEX, NumberTable.getInteger(icol));
        pRequest.setParameter(pIndexName, NumberTable.getInteger(i));
        pRequest.setParameter(COUNT, NumberTable.getInteger(i + 1));
        pRequest.setParameter(pElementName, value);
        pRequest.setParameter(KEY, key);
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        if (icol == lastColumn) {
          pRequest.serviceLocalParameter(OUTPUT_ROW_END, pRequest, pResponse);
        }
        i++;
        icol = (icol + 1) % pNumColumns;
        if (icol == 0) irow++;
      }
      if (icol != 0) {
        // finish up the last row
        for ( ; icol < pNumColumns; icol++, i++) {
          pRequest.setParameter(COLUMN_INDEX, NumberTable.getInteger(icol));
          pRequest.setParameter(pIndexName, NumberTable.getInteger(i));
          pRequest.setParameter(COUNT, NumberTable.getInteger(i + 1));
          pRequest.setParameter(pElementName, null);
          pRequest.setParameter(KEY, null);
          pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        }
        pRequest.serviceLocalParameter(OUTPUT_ROW_END, pRequest, pResponse);
      }
      pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
    }
  }

  /**
   * Renders the range operation for Dictionary objects
   * @param pDictionary the object to iterate
   * @param pNumColumns Specifies the number of columns to partition the array into.
   * @param pIndexName the parameter name to use for the index
   * @param pElementName the parameter name to use for the element
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException if an application specific error occurred processing this request
   * @exception IOException if an error occurred reading data from the request or writing data to the response
   **/
  protected void serviceDictionary(Dictionary pDictionary, int pNumColumns,
                                   String pIndexName, String pElementName,
                                   DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    if (pDictionary.isEmpty()) {
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    }
    else {
      Enumeration keys = pDictionary.keys();
      pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
      int i = 0, irow = 0, icol = 0;
      int lastColumn = pNumColumns - 1;
      while (keys.hasMoreElements()) {
        Object key = keys.nextElement();
        Object value = pDictionary.get(key);

        if (icol == 0) {
          pRequest.setParameter(ROW_INDEX, NumberTable.getInteger(irow));
          pRequest.serviceLocalParameter(OUTPUT_ROW_START, pRequest, pResponse);
        }
        pRequest.setParameter(COLUMN_INDEX, NumberTable.getInteger(icol));
        pRequest.setParameter(pIndexName, NumberTable.getInteger(i));
        pRequest.setParameter(COUNT, NumberTable.getInteger(i + 1));
        pRequest.setParameter(pElementName, value);
        pRequest.setParameter(KEY, key);
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        if (icol == lastColumn) {
          pRequest.serviceLocalParameter(OUTPUT_ROW_END, pRequest, pResponse);
        }
        i++;
        icol = (icol + 1) % pNumColumns;
        if (icol == 0) irow++;
      }
      if (icol != 0) {
        // finish up the last row
        for ( ; icol < pNumColumns; icol++, i++) {
          pRequest.setParameter(COLUMN_INDEX, NumberTable.getInteger(icol));
          pRequest.setParameter(pIndexName, NumberTable.getInteger(i));
          pRequest.setParameter(COUNT, NumberTable.getInteger(i + 1));
          pRequest.setParameter(pElementName, null);
          pRequest.setParameter(KEY, null);
          pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        }
        pRequest.serviceLocalParameter(OUTPUT_ROW_END, pRequest, pResponse);
      }
      pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
    }
  }
  //-------------------------------------
}

