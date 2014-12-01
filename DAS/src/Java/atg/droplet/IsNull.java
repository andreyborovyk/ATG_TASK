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


/**
 * The IsNull droplet conditionally servlet renders one of its parameters based on
 * the value of its <i>value</i> parameter.  If the value is null then the output
 * parameter <code>true</code> is rendered, otherwise the output parameter <code>false</code>
 * is serviced.
 * <P>
 * For Example:
 * <blockquote>
 * <code>
 * &lt;droplet bean="/atg/dynamo/droplet/IsNull"&gt;
 * &lt;param name="value" value="param:someobject"&gt;
 * &lt;oparam name="true"&gt;Some object is null&lt;/oparam&gt;
 * &lt;oparam name="false"&gt;Some object is NOT null&lt;/oparam&gt;
 * &lt;/droplet&gt;
 * </code>
 * </blockquote>
 *
 * @author Bob Mason
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/IsNull.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public
class IsNull
 extends DynamoServlet
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/IsNull.java#2 $$Change: 651448 $";

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
   * Constructs an instanceof IsNull
   */
  public IsNull() {
  }

  /**
   * Render the isNull switch
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    Object value = pRequest.getObjectParameter(VALUE);
    if (isLoggingDebug())
      logDebug("value=" + value);

    if (value == null) {
      pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
      return;
    }

    pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
  }
} // end of class
