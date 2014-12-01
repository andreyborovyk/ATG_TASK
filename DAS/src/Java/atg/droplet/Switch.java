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
import atg.nucleus.naming.ParameterName;

import java.io.*;

/**
 * The Switch conditionally servlet renders one of its parameters based on
 * the value of its <i>value</i> parameter.  It takes as parameters:
 * <dl>
 *
 * <dt>value 
 *
 * <dd>The value of the switch.  The switch value is turned into
 * a string and used as the name of the parameter to render.  For example,
 * if the switch's value parameter is a boolean that is true, it renders 
 * the value of its "true" parameter.
 *
 * <dt>value's value
 *
 * <dd>The value of the "value", converted to a String, is used as the name 
 * of the parameter to render for this iteration.  This parameter must be
 * locally defined in the droplet invocation.

 * <dt>unset
 *
 * <dd>The value to render if the value parameter is not set. This parameter
 * must be locally defined in the droplet invocation.
 *
 * <dt>default
 *
 * <dd>the parameter to render if the switch's value parameter is not defined
 * or its value as a String does not match a parameter to the switch. This
 * parameter must be defined in the droplet invocation.
 *
 * </dl>
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/Switch.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class Switch extends DynamoServlet {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/Switch.java#2 $$Change: 651448 $";

  public final static ParameterName VALUE = ParameterName.getParameterName("value");
  public final static ParameterName DEFAULT = ParameterName.getParameterName("default");
  public final static ParameterName UNSET = ParameterName.getParameterName("unset");

  //-------------------------------------

  /**
   * Render the switch
   */
  public void service(DynamoHttpServletRequest pReq, 
                      DynamoHttpServletResponse pRes) 
  		throws ServletException, IOException {
    /*
     * Gets an object parameter and makes sure that it is an array
     * of objects.
     */
    Object value;
    boolean handled = false; 

    /*
     * Get the value of the parameter as a string and use this to try
     * and find a valid parameter to service.
     */
    if ((value = pReq.getParameter(VALUE)) != null) {
      String valueStr = value.toString();
      handled = pReq.serviceLocalParameter(valueStr, pReq, pRes);
      if (isLoggingDebug()) {
        if (handled)
          logDebug("rendered local parameter for value='" + valueStr + "'");
	else
          logDebug("found no local parameter for value='" + valueStr + "'");
      }
    }
    else {
      handled = pReq.serviceLocalParameter(UNSET, pReq, pRes);
      if (handled && isLoggingDebug())
        logDebug("rendered 'unset' parameter for null value");
    }

    /*
     * Did not find a valid parameter, try servicing the default
     * parameter instead
     */
    if (!handled) {
      handled = pReq.serviceLocalParameter(DEFAULT, pReq, pRes);
      if (handled && isLoggingDebug())
        logDebug("rendered 'default' parameter");
    }
  }
}
