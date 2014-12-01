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



package atg.portal.gear.xmlfeed;




import atg.servlet.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.lang.IndexOutOfBoundsException;


/**
 * <p>
 * This is a utility class for the xmlfeed gear. It contains converter methods
 * between String and Map and queryFormat method, used to generate the dynamic
 * XML source URL.
 * </p>
 *
 * @version $Id: //app/portal/version/10.0.3/xmlfeed/classes.jar/src/atg/portal/gear/xmlfeed/XmlfeedTool.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */


public class XmlfeedTool {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlfeed/classes.jar/src/atg/portal/gear/xmlfeed/XmlfeedTool.java#2 $$Change: 651448 $";



    /** Separator to be used when encoding various xmlfeed user parameters. */
    public static String FIELD_SEPARATOR = "&";

    /**
     * Returns an encoded String using key-value pairs passed as a Map.
     * @param pMap Map cotaining key-value pairs.
     * @return  An encoded String using key-value pairs passed as Map.
     */
    public static String convertMapToParamString(Map pMap) {

	
	

	StringBuffer userParam = new StringBuffer();

	Iterator kees = pMap.keySet().iterator();
	while (kees.hasNext()) {
	    String key = (String)kees.next();
	    String val = (String)pMap.get(key);

	   
	 
	    
	    userParam.append(key).append("=").append(val).append(FIELD_SEPARATOR);
	}

	//remove the last occurance of field separator, we don't need that
	if (userParam.length() > 0) {
	    userParam.deleteCharAt(userParam.length()-1);
	}

	 

	return userParam.toString();
    }


    /**
     * Returns a Map by decoding key-value pairs from the input String.
     * @param pUserParam Encoded string with key-value pairs.
     * @return Map with key-value pairs.
     */
    public static Map convertParamStringToMap(String pUserParam) {
	
	HashMap map = new HashMap();

	StringTokenizer line = new StringTokenizer(pUserParam,FIELD_SEPARATOR);
	while (line.hasMoreTokens()) {
	    
	    String pair = line.nextToken();
	    int sepIndex = pair.indexOf('=');

	    try {
		String key = pair.substring(0, sepIndex);
		String val = pair.substring(sepIndex+1, pair.length());

		map.put(key, val);
	    }

	    catch (IndexOutOfBoundsException e) {
		//need to change this to proper error handling.
		//System.out.println("XmlfeedTool:" + e.getMessage());
	    }


	    
	}
	return map;
    }


  

}

