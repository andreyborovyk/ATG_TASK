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

package atg.sqljmsadmin.taglib;

import java.util.Hashtable;
import java.util.Enumeration;

/****************************************
 * store navigation info between requests
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSNavigationState.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSNavigationState
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSNavigationState.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // ReferringPage
    private String mReferringPage;
    /**
     * set ReferringPage
     * @param pReferringPage the ReferringPage
     */
    public void setReferringPage(String pReferringPage) { mReferringPage = pReferringPage; }
    /**
     * get ReferringPage
     * @return the ReferringPage
     */
    public String getReferringPage() { return mReferringPage; }

    //----------------------------------------
    // Params
    private Hashtable mParams;
    /**
     * set Params
     * @param pParams the Params
     */
    public void setParams(Hashtable pParams) 
    { 
	mParams = (Hashtable) pParams.clone();
    }
    /**
     * add a param/value pair
     */
    public void addParam(String pParamName, Object pParamValue) 
    { 
	mParams.put(pParamName, pParamValue.toString());
    }
    /**
     * get Params
     * @return the Params
     */
    public Hashtable getParams() { return mParams; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof DMSNavigationState
     */
    public DMSNavigationState()
    {
	mParams = new Hashtable(3);
    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * reset the params
     */
    public void reset()
    {
	mReferringPage = null;
	mParams.clear();
    }

    //----------------------------------------
    /**
     * clone this navigation state
     */
    public Object clone()
    {
	DMSNavigationState clonedState = new DMSNavigationState();
	clonedState.setReferringPage(new String(mReferringPage));
	
	Enumeration paramKeys = mParams.keys();
	
	while(paramKeys.hasMoreElements()) {
	    String key = (String) paramKeys.nextElement();
	    clonedState.addParam(key, mParams.get(key));
	}
	
	return clonedState;
    }

    //----------------------------------------
    /**
     * to string
     */
    public String toString()
    {
	String stateString = new String("" + this.hashCode() + " " + mReferringPage);
	
	Enumeration keys = mParams.keys();
	
	while(keys.hasMoreElements()) {
	    String key = (String) keys.nextElement();
	    String value = (String) mParams.get(key);
	    stateString.concat(" " + key + "=" + value);
	}

	return stateString;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
