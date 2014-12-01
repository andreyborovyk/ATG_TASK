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

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.projects.store.logging.LogUtils;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;


/**
 * <p>
 * This droplet is a convenience to help prevent JSP compilation failure due to
 * the passing of nulls into a dsp tag. This droplet takes a pipe-delimited
 * list of property names and a repository item. In the case of strings,
 * strings are considered null if they are equivalent to the empty string or
 * are null.
 * </p>
 *
 * <p>
 * This droplet takes the following input parameters:
 *
 * <ul>
 * <li>
 * item - repository item whose properties need checking
 * </li>
 * <li>
 * properties - pipe delimited list of property names to check
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * This droplet renders the following oparams:
 *
 * <ul>
 * <li>
 * all - rendered if all of the properties are null
 * </li>
 * <li>
 * true - rendered if some of the properties are null
 * </li>
 * <li>
 * false - rendered if none of the properties are null
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * This droplet sets the following parameters on output oparam of true
 *
 * <ul>
 * <li>nullProperties - list of properties that were null
 * <li>definedProperties - list of properties that were not null
 * </ul>
 * </p>
 *
 * <p>
 * Example:
 * <PRE>
 *
 * &lt;dsp:droplet bean="/atg/store/droplet/NullPropertiesCheck"&gt;
 * &lt;dsp:param name="item" param="category"&gt;
 * &lt;dsp:param name="properties" value="template.url|auxiliaryMedia.navon.url"&gt;
 * &lt;dsp:oparam name="true"&gt;
 * These properties were null
 * &lt;dsp:valueof param="nullProperties"&gt;
 * &lt;/dsp:valueof&gt;
 * &lt;/dsp:oparam&gt;
 * &lt;dsp:oparam name="false"&gt;
 * &lt;/dsp:oparam&gt;
 * &lt;/dsp:droplet&gt;
 *
 * </PRE>
 * </p>
 *
 * @author ATG
 * @version $Revision: #3 $
 */
public class NullPropertiesCheck extends DynamoServlet {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/droplet/NullPropertiesCheck.java#3 $$Change: 635816 $";

  /**
   * Item parameter name.
   */
  public static final ParameterName ITEM = ParameterName.getParameterName("item");

  /**
   * Properties parameter name.
   */
  public static final ParameterName PROPERTIES = ParameterName.getParameterName("properties");

  /**
   * All parameter name.
   */
  public static final ParameterName ALL = ParameterName.getParameterName("all");

  /**
   * True parameter name.
   */
  public static final ParameterName TRUE = ParameterName.getParameterName("true");

  /**
   * False parameter name.
   */
  public static final ParameterName FALSE = ParameterName.getParameterName("false");

  /**
   * Null properties parameter name.
   */
  public static final String NULL_PROPERTIES = "nullProperties";

  /**
   * Defined properties parameter name.
   */
  public static final String DEFINED_PROPERTIES = "definedProperties";

  /**
   * <p>
   * Performs the tasks outlined in the class description.
   * </p>
   *
   * @param pRequest DynamoHttpServletRequest
   * @param pResponse DynamoHttpServletResponse
   *
   * @throws ServletException if an error occurs
   * @throws IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    String properties = pRequest.getParameter(PROPERTIES);
    RepositoryItem item = (RepositoryItem) pRequest.getObjectParameter(ITEM);

    List nullProperties = new ArrayList();
    List definedProperties = new ArrayList();

    if (StringUtils.isEmpty(properties)) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Null value for property 'properties' passed"));
      }

      return;
    }

    if (item == null) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Null value for property 'item' passed"));
      }

      return;
    }

    try {
      String[] props = properties.split("\\|");

      if (props != null) {
        int len = props.length;
        String propertyName = null;
        Object propertyValu = null;

        for (int i = 0; i < len; i++) {
          propertyName = props[i];

          if (isLoggingDebug()) {
            logDebug("Checking item " + item.getRepositoryId() + " for property " + propertyName);
          }

          if (StringUtils.isEmpty(propertyName)) {
            continue;
          }

          propertyValu = DynamicBeans.getSubPropertyValue(item, propertyName);

          if (isValueEmpty(propertyValu)) {
            nullProperties.add(propertyName);
          } else {
            definedProperties.add(propertyName);
          }
        }

        // for each property to check
      }
    } catch (PropertyNotFoundException pnfe) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Property passed in 'properties' param not found for page" +
            pRequest.getRequestURI()), pnfe);
      }
    }

    if (definedProperties.isEmpty()) {
      // if there were no defined properties then all of them 
      // are null
      if (isLoggingDebug()) {
        logDebug("None of the properties were defined, servicing all param");
      }

      pRequest.setParameter(NULL_PROPERTIES, nullProperties);
      pRequest.setParameter(DEFINED_PROPERTIES, definedProperties);
      pRequest.serviceLocalParameter(ALL, pRequest, pResponse);
    } else if (!nullProperties.isEmpty()) {
      if (isLoggingDebug()) {
        logDebug("Some of the properties were empty, servicing true");
      }

      // if there were some empty properties then return the 
      // empty ones and defined ones.      
      pRequest.setParameter(NULL_PROPERTIES, nullProperties);
      pRequest.setParameter(DEFINED_PROPERTIES, definedProperties);
      pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
    } else {
      // other wise 
      pRequest.setParameter(NULL_PROPERTIES, nullProperties);
      pRequest.setParameter(DEFINED_PROPERTIES, definedProperties);
      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
    }
  }

  /**
   * <p>Returns true if the value represented by the object is empty.
   * <p>For strings they are empty if they equal the empty string '' or are
   * null.
   * <p>Collections are empty if they say they are empty
   * <p>null is always empty
   * @param pValue - value to check
   * @return true if the value represented by the object is empty, false - otherwise
   */
  protected boolean isValueEmpty(Object pValue) {
    // check to see if its an instance of a string
    if (pValue == null) {
      return true;
    } else if (pValue instanceof String) {
      return (StringUtils.isEmpty((String) pValue));
    } else if (pValue instanceof Collection) {
      return ((Collection) pValue).isEmpty();
    } else if (pValue instanceof Object[]) {
      int len = ((Object[]) pValue).length;

      return len == 0;
    }

    return false;
  }
}
