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

package atg.portal.gear.alert;

import atg.nucleus.Nucleus;
import atg.nucleus.naming.ParameterName;
import atg.repository.*;
import atg.servlet.*;
import atg.beans.*;

import java.util.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.transaction.*;

import atg.portal.framework.GearConfigFormHandler;

/**
 *
 * @author Mark Lord
 * @version $Id: //app/portal/version/10.0.3/alert/classes.jar/src/atg/portal/gear/alert/AlertGearConfigFormHandler.java#2 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class AlertGearConfigFormHandler
  extends GearConfigFormHandler 
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = 
    "$Id: //app/portal/version/10.0.3/alert/classes.jar/src/atg/portal/gear/alert/AlertGearConfigFormHandler.java#2 $$Change: 651448 $";

  //Resource Bundle
  static final String RESOURCEBUNDLENAME = "atg.portal.gear.alert.AlertGearConfigFormHandlerResources";

  //Messages
  static final String ERR_MINDISPLAYCOUNT    = "errMinSharedPageDisplayCount";
  static final String ERR_DISPLAYCOUNTFORMAT = "errSharedPageDisplayCountFormatException";


  /**
   * Override this method to do any pre-processing or verification of form input.
   * Processing will be stopped if there are any errors found.  In the extension
   * of this method, if you encounter an error condition, you may use addFailureMessage()
   * or userInfo() to communicate the situation to the user.
   */
  
  public void preConfirm(DynamoHttpServletRequest pRequest,
                         DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    //Check DisplayCountValue is number and is greater than zero
    Map values = getValues();
   
    if(values != null) {
	String DisplayCountValue = (String)values.get("sharedDisplayCount"); 
        if(DisplayCountValue != null) {
	  try { 
            Integer displayCount = Integer.valueOf(DisplayCountValue);
            if ( displayCount.intValue() < 1 ) {
               addFailureMessage(RESOURCEBUNDLENAME,ERR_MINDISPLAYCOUNT,null);
            }      
          } catch(NumberFormatException nfe) {
            addFailureMessage(RESOURCEBUNDLENAME,ERR_DISPLAYCOUNTFORMAT,null);
          }
        }
        
       
    }    
  }
  
}

