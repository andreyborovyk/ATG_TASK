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

import javax.servlet.ServletException;
import java.io.IOException;


/**
 * The ComponentExists droplet conditionally renders one of its output parameters
 * depending on whether or not a specified Nucleus path currently refers to a
 * non-null object.  It it used to query whether a particular component has been
 * instantiated yet.
 * <p>
 * If the specified component exists, the droplet renders the output parameter
 * named <code>true</code>; otherwise it renders the output parameter named
 * <code>false</code>.
 * <P>
 * For Example:
 * <blockquote>
 * <code>
 * &lt;droplet bean="/atg/dynamo/droplet/ComponentExists"&gt;
 * &lt;param name="path" value="/atg/modules/MyModule"&gt;
 * &lt;oparam name="true"&gt;MyModule has been defined&lt;/oparam&gt;
 * &lt;oparam name="false"&gt;MyModule has not been defined&lt;/oparam&gt;
 * &lt;/droplet&gt;
 * </code>
 * </blockquote>
 *
 * @author Matt Landau
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/ComponentExists.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class ComponentExists extends DynamoServlet
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/ComponentExists.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  public final static ParameterName PATH = ParameterName.getParameterName("path");
  public final static ParameterName TRUE  = ParameterName.getParameterName("true");
  public final static ParameterName FALSE = ParameterName.getParameterName("false");

  /**
   * Render the ComponentExists switch
   **/
  
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    String path = pRequest.getParameter(PATH);
    Object obj = pRequest.resolveName(path, false);
    
    if (obj != null) {
      if (isLoggingDebug())
        logDebug("Component " + path + " exists.");
      pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
    }
    else {
      if (isLoggingDebug())
        logDebug("Component " + path + " does not exist.");
      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
    }
  }
}
