/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2010 Art Technology Group, Inc.
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

package atg.projects.store.droplet;

import atg.nucleus.naming.ParameterName;

import atg.projects.store.logging.LogUtils;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;

import java.lang.reflect.Array;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;


/**
 * <p>
 * This droplet determines the indices of a corresponding array if the array was
 * subdivided into separate parts. It was designed with the display of product
 * ranges in mind. It allows the user to display a numerical navigation through
 * the subsets of a given array.
 * <p>
 * This droplet takes the following input parameters:
 * <ul>
 * <li>array - Collection or array to calculate against.
 * <li>subsetsize - The size of the array subsets.
 * </ul>
 * <p>
 * This droplet renders the following oparams:
 * <ul>
 * <li>output - always rendered
 * </ul>
 * <p>
 * This droplet sets the following output parameters:
 * <ul>
 * <li>subsetIndices - list of indices for the given subdivisions
 * </ul>
 * <p>
 * This droplet assumes the base index starts at 1. Those who wish this to start
 * at zero should set the <code>startIndexAtZero</code> value to true.
 * <p>
 * Example:
 *
 * <pre>
 *
 * &lt;dsp:droplet bean="/atg/store/droplet/ArraySubsetHelper"&gt; &lt;dsp:param
 * name="array" param="category.childProducts"&gt; &lt;dsp:param
 * name="elementName" value="subsetIndex"&gt; &lt;dsp:param name="subsetSize"
 * value="10"&gt; &lt;dsp:oparam name="output"&gt; &lt;dsp:droplet
 * name="ForEach"&gt; &lt;dsp:param name="array" param="subsetIndices"&gt;
 * &lt;dsp:oparam name="output"&gt; &lt;dsp:valueof param="count"/&gt;
 * &lt;dsp:valueof param="subsetIndex"/&gt; &lt;/dsp:oparam&gt;
 * &lt;/dsp:oparam&gt; &lt;/dsp:droplet&gt;
 *
 * </pre>
 *
 * @author ATG
 * @version $Revision: #3 $
 */
public class ArraySubsetHelper extends DynamoServlet {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/droplet/ArraySubsetHelper.java#3 $$Change: 635816 $";

  /**
   * Array parameter name.
   */
  public static final ParameterName ARRAY = ParameterName.getParameterName("array");

  /**
   * Subset size parameter name.
   */
  public static final ParameterName SUBSETSIZE = ParameterName.getParameterName("subsetSize");

  /**
   * Output parameter name.
   */
  public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

  /**
   * Subset indices parameter name.
   */
  public static final String SUBSETINDEXES = "subsetIndices";

  /**
   * Should start index at zero.
   */
  private boolean mStartIndexAtZero = false;

  /**
   * @param pStartIndexAtZero - this assumes the indices returned started at zero.
   */
  public void setStartIndexAtZero(boolean pStartIndexAtZero) {
    mStartIndexAtZero = pStartIndexAtZero;
  }

  /**
   * @return If set this assumes the indices returned started at zero. Default is
   * false.
   */
  public boolean isStartIndexAtZero() {
    return mStartIndexAtZero;
  }

  /**
   * This performs the work as described in the class definition.
   * @param pRequest - http request
   * @param pResponse - http response
   * @throws ServletException - if error occurs
   * @throws IOException - if error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    Object arrayParam = pRequest.getObjectParameter(ARRAY);
    String subsetSize = pRequest.getParameter(SUBSETSIZE);

    List indices = new ArrayList();

    if (arrayParam == null) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("No array param passed on page " + pRequest.getRequestURI()));
      }

      return;
    }

    int length = 0;

    if (arrayParam instanceof Object[]) {
      Object[] objectArray = (Object[]) arrayParam;
      length = objectArray.length;
    } else if (arrayParam instanceof Collection) {
      Collection col = (Collection) arrayParam;
      length = col.size();
    } else if (arrayParam.getClass().isArray()) {
      length = Array.getLength(arrayParam);
    } else {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Unrecognized array parameter passed " + arrayParam.getClass().getName()));
      }
    }

    try {
      // take the length and divide by the subset size
      int subsetLen = Integer.parseInt(subsetSize);

      // this gives us the number of full subsets
      int numberOfSets = length / subsetLen;

      // this lets us know if we have an incomplete subset
      int remaining = length % subsetLen;

      // if we have any remaining then add another
      numberOfSets = (remaining > 0) ? (numberOfSets + 1) : numberOfSets;

      if (isLoggingDebug()) {
        logDebug("Number of sets = " + numberOfSets);
      }

      // now 'count' our way up the subsets and place the index
      // of each in our output array
      for (int i = 0; i < numberOfSets; i++) {
        // the index of each subset is i * subsetLen
        int index = i * subsetLen;

        // check to see if we started our numbering at zero
        if (!isStartIndexAtZero()) {
          index += 1;
        }

        indices.add(Integer.toString(index));
      } // for each subset
    } catch (NumberFormatException nfe) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Incorrect parameter passed for subsetSize " + pRequest.getRequestURI()));
      }
    }

    pRequest.setParameter(SUBSETINDEXES, indices);
    pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
  }
}
