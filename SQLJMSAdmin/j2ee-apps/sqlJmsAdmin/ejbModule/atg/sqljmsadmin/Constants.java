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

package atg.sqljmsadmin;

import java.util.*;

/****************************************
 * non-public constants such as message strings read 
 * from the resource file
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/Constants.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class Constants
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/Constants.java#2 $$Change: 651448 $";

    static ResourceBundle sResources =
	ResourceBundle.getBundle("atg.sqljmsadmin.Resources");

    //----------------------------------------
    // Constants
    //----------------------------------------

    public static final String NO_ENTITY_WITH_ID =
	getStringResource("NO_ENTITY_WITH_ID");

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof Constants
     */
    public Constants()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * get a string resource
     */
    public static String getStringResource(String pResourceName)
	throws MissingResourceException
    {
	try {
	    String returnString = sResources.getString(pResourceName);
	    
	    if(returnString == null) {
		String error = "ERROR: Unable to load resource " + pResourceName;
		throw new MissingResourceException(error, "atg.sqljmsadmin.Constants",
						   pResourceName);
	    }
	    else {
		return returnString;
	    }
	}
	catch(MissingResourceException e) {
	    System.err.println("ERROR: Unable to load resource " +
			       pResourceName + ": " +
			       e);
	    throw e;
	}
    }

} // end of class
