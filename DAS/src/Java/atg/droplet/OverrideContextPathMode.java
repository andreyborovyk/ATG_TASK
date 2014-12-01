/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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

import java.io.IOException;

import javax.servlet.ServletException;
import atg.servlet.*;




/**
 * This droplet overrides the encodeContextPathMode for the course of
 * rendering its output parmater.
 *
 * <P>
 * This servlet renders its <i>output</i> parameter
 *
 * <dl>
 *
 * <dt>mode
 * <dd>The <i>mode</i> parameter must be a
 * one of 'never' (never prepend the contextRoot), 'always' (always
 * prepend it) or 'ifNeeded' (only prepend if the URL does not
 * already begin with the current contextPath).
 *
 * <dt>servletMode
 * <dd>The <i>servletMode</i> parameter controls prepending of
 * the servletPath. It must be a
 * one of 'never' (never prepend the servletRoot), 'always' (always
 * prepend it).
 *
 * </dl>
 * <P>
 *
 * Created: March 04 2002
 *
 * @author Charles Morehead
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/OverrideContextPathMode.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public
class OverrideContextPathMode extends DynamoServlet
 {
   
   //-------------------------------------
   // Class version string " 
   public static String CLASS_VERSION = 
   "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/OverrideContextPathMode.java#2 $$Change: 651448 $"; 

   //-------------------------------------
   // Constants 

   public final static String MODE = "mode";
   public final static String SERVLET_MODE = "servletMode";
   public final static String OUTPUT = "output";

   //-------------------------------------

   /**
    * Render the droplet.
    */
   public void service(DynamoHttpServletRequest pReq, 
                       DynamoHttpServletResponse pRes) 
     throws ServletException, IOException 
  {    
    String strMode = pReq.getParameter(MODE);
    

    if (strMode == null) {
      logError("'mode' parameter required.");
      return;
    }

    int modeRestore = pReq.getEncodeContextPathMode();
    int modeTarget = getContextModeForString(strMode);


    if (modeTarget == -1) {
      return;
    }


    Boolean boolEncodeServlet = pReq.getEncodeServletPath() ?
      Boolean.TRUE : Boolean.FALSE;
    
    String strServletMode = pReq.getParameter("servletMode");

    if (strServletMode != null) {
      boolEncodeServlet = getServletModeForString(strServletMode);
      if (boolEncodeServlet == null) {
        return;
      }
    }

    boolean bEncodeServletRestore = pReq.getEncodeServletPath();

    try {
      pReq.setEncodeContextPathMode(modeTarget);
      pReq.setEncodeServletPath(boolEncodeServlet.booleanValue());
      pReq.serviceLocalParameter(OUTPUT, pReq, pRes);
    }
    finally {
      pReq.setEncodeContextPathMode(modeRestore);
      pReq.setEncodeServletPath(bEncodeServletRestore);
    }
  }


   
   int getContextModeForString(String pMode) {
     int modeTarget = -1;
     String strMode = pMode;
     if (strMode.equals("always") || strMode.equals("1")) {
       modeTarget = DynamoHttpServletRequest.ENCODE_CONTEXT_PATH;
     }
     else if (strMode.equals("never") || strMode.equals("none") ||
              strMode.equals("0")) {
       modeTarget = DynamoHttpServletRequest.ENCODE_NONE;      
     }
     else if (strMode.equals("ifNeeded") || strMode.equals("ifNotThere") ||
              strMode.equals("2")) {
       modeTarget = DynamoHttpServletRequest.ENCODE_IF_NOT_THERE;
     }
     
     if (modeTarget == -1) {
       logError("invalid 'mode' parameter: " + strMode +
                ". Use 'always', 'never', or 'ifNeeded'");
     }

     return modeTarget;
   }


   Boolean getServletModeForString(String pMode) {
     Boolean modeTarget = null;
     String strMode = pMode;
     
     if (strMode == null) {
       // do nothign.
     }
     else if (strMode.equals("always") || strMode.equals("1")) {
       modeTarget = Boolean.TRUE;
     }
     else if (strMode.equals("never") || strMode.equals("none") ||
              strMode.equals("0")) {
       modeTarget = Boolean.FALSE;
     }

     if (modeTarget == null)
     {
       logError("invalid 'serveletMode' parameter: " + strMode +
                ". Use 'always' or 'never'");
     }

     return modeTarget;
   }
   
}
