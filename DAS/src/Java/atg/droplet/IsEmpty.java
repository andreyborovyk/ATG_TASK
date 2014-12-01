/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.Dictionary;


/**
 * The IsEmpty droplet conditionally servlet renders one of its parameters based on
 * the value of its <i>value</i> parameter.  If the value is null or if the object is "empty"
 * then the output parameter <code>true</code> is rendered, otherwise the output parameter 
 * <code>false</code> is serviced. 
 * <P>
 * Empty is defined by the following logic:
 * <UL>
 * <LI>if the value is a Collection, and Collection.isEmpty returns true
 * <LI>if the value is an Object array, and Object [].length == 0
 * <LI>if the value is a Map, and Map.isEmpty returns true
 * <LI>if the value is a Dictionary, and Map.isEmpty returns true
 * <LI> if the value is a String, and String.equals("") returns true
 * </UL>
 * <P>
 * For Example:
 * <blockquote>
 * <code>
 * &lt;droplet bean="/atg/dynamo/droplet/IsEmpty"&gt;
 * &lt;param name="value" value="param:someobject"&gt;
 * &lt;oparam name="true"&gt;Some object is empty&lt;/oparam&gt;
 * &lt;oparam name="false"&gt;Some object is NOT empty&lt;/oparam&gt;
 * &lt;/droplet&gt;
 * </code>
 * </blockquote>
 *
 * @author Bob Mason
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/IsEmpty.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public 
class IsEmpty
 extends DynamoServlet
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/IsEmpty.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  public final static ParameterName VALUE = ParameterName.getParameterName("value");
  public final static ParameterName TRUE  = ParameterName.getParameterName("true");
  public final static ParameterName FALSE = ParameterName.getParameterName("false");

  //-------------------------------------
  // Member Variables
  //-------------------------------------

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instanceof IsEmpty
   */
  public IsEmpty() {
  }

  /**
   * Render the IsEmpty switch
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException 
  {
    Object value = pRequest.getObjectParameter(VALUE);
    if (isLoggingDebug())
      logDebug("value=" + value);

    if (value == null) {
      if (isLoggingDebug())
        logDebug("value is null");
      pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
      return;
    }
    else if (value instanceof Collection) {
      if (isLoggingDebug())
        logDebug("value is Collection; isEmpty=" + ((Collection)value).isEmpty());
      if (((Collection)value).isEmpty()) {
        pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
        return;
      }
    } 
    else if (value.getClass().isArray()) {
      int length = java.lang.reflect.Array.getLength(value);
      if (isLoggingDebug())
        logDebug("value is array; length=" + length);
      if (length == 0) {
        pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
        return;
      }
    }
    else if (value instanceof Map) {
      if (isLoggingDebug())
        logDebug("value is Map; isEmpty=" + ((Map)value).isEmpty());
      if (((Map)value).isEmpty()) {
        pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
        return;
      }
    }
    else if (value instanceof Dictionary) {
      if (isLoggingDebug())
        logDebug("value is Dictionary; isEmpty=" + ((Dictionary)value).isEmpty());
      if (((Dictionary)value).isEmpty()) {
        pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
        return;
      }
    }
    else if (value instanceof String) {
      if (isLoggingDebug())
        logDebug("value is String; equals(\"\")=" + value.equals(""));
      if (value.equals("")) {
        pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
        return;
      }
    }
    pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
  }
} // end of class
