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

package atg.userprofiling;

import atg.repository.*;
import atg.droplet.*;
import atg.servlet.*;
import atg.beans.*;

import atg.core.util.StringUtils;

import java.util.*;
import java.beans.*;
import java.io.IOException;
import javax.servlet.ServletException;


/**
 * This class provided a form handler for the JHTML Profile
 * Administration. It can be used to add new profiles, edit
 * exsitsing profiles as well as other administrative tasks.
 *
 * @author Andrew Rickard
 * @version $Id: //product/DPS/version/10.0.3/Java/atg/userprofiling/ProfileAdminFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ProfileAdminFormHandler extends ProfileForm
{
    //-------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
	"$Id: //product/DPS/version/10.0.3/Java/atg/userprofiling/ProfileAdminFormHandler.java#2 $$Change: 651448 $";
    
    //-------------------------------------
    // Constants
    //-------------------------------------
   
          
    //-------------------------------------
    // Properties
    //-------------------------------------
       
    //-------------------------------------
    // Constructors
    //-------------------------------------
    
    /**
     * Constructs an instanceof ProfileAdminFormHandler
     */
    public ProfileAdminFormHandler() {
	
    }

    public void preCreateUser(DynamoHttpServletRequest pRequest,
			      DynamoHttpServletResponse pResponse) 
	throws ServletException, IOException {
	
	if(isLoggingDebug())
	    logDebug("preCreateUser");	
	
	setValueDictionaryByParameters(pRequest,pResponse);	
    }
    public void preUpdateUser(DynamoHttpServletRequest pRequest,
			      DynamoHttpServletResponse pResponse) 
	throws ServletException, IOException {
	
	if(isLoggingDebug())
	    logDebug("preUpdateUser");	
	
	setValueDictionaryByParameters(pRequest,pResponse);	
    }
    public void preChangePassword(DynamoHttpServletRequest pRequest,
				  DynamoHttpServletResponse pResponse) 
	throws ServletException, IOException {
	
	if(isLoggingDebug())
	    logDebug("preChangePassword");	
	
	setValueDictionaryByParameters(pRequest,pResponse);
	setValueProperty(CONFIRMPASSWORD_PARAM,pRequest.getParameter(CONFIRMPASSWORD_PARAM));
	setValueProperty(OLDPASSWORD_PARAM,pRequest.getParameter(OLDPASSWORD_PARAM));
    }   

    public void preAddMulti(DynamoHttpServletRequest pRequest,
			    DynamoHttpServletResponse pResponse) 
	throws ServletException, IOException {
	
	if(isLoggingDebug())
	    logDebug("preAddMulti");	
	
	setValueDictionaryByParameters(pRequest,pResponse);	
    }   
} // end of class
