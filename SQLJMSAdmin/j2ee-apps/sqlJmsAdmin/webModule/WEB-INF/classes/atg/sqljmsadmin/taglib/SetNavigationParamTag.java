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

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/****************************************
 * set a param/value pair in a navigation state
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/SetNavigationParamTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class SetNavigationParamTag
    extends TagSupport
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/SetNavigationParamTag.java#2 $$Change: 651448 $";

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
    // Navigation
    private DMSNavigationState mNavigation;
    /**
     * set Navigation
     * @param pNavigation the Navigation
     */
    public void setNavigation(DMSNavigationState pNavigation) { mNavigation = pNavigation; }
    /**
     * get Navigation
     * @return the Navigation
     */
    public DMSNavigationState getNavigation() { return mNavigation; }

    //----------------------------------------
    // Param
    private String mParam;
    /**
     * set Param
     * @param pParam the Param
     */
    public void setParam(String pParam) { mParam = pParam; }
    /**
     * get Param
     * @return the Param
     */
    public String getParam() { return mParam; }

    //----------------------------------------
    // ParamValue
    private Object mParamValue;
    /**
     * set ParamValue
     * @param pParamValue the ParamValue
     */
    public void setParamValue(Object pParamValue) { mParamValue = pParamValue; }
    /**
     * get ParamValue
     * @return the ParamValue
     */
    public Object getParamValue() { return mParamValue; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof SetNavigationParamTag
     */
    public SetNavigationParamTag()
    {
	
    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * start this tag
     */
    public int doStartTag()
	throws JspException
    {
	if(mNavigation != null && mParam != null && mParamValue != null) {
	    mNavigation.addParam(mParam, mParamValue);
	}

	return EVAL_BODY_INCLUDE;
    }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	super.release();
	mNavigation = null;
	mParam = null;
	mParamValue = null;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
