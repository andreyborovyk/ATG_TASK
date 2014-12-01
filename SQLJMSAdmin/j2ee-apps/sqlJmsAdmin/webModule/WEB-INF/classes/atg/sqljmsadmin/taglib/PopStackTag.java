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

import java.util.Stack;

/****************************************
 * pop a navigation state off the stack
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/PopStackTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class PopStackTag
    extends TagSupport
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/PopStackTag.java#2 $$Change: 651448 $";

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
    // Stack
    private Stack mStack;
    /**
     * set Stack
     * @param pStack the Stack
     */
    public void setStack(Stack pStack) { mStack = pStack; }
    /**
     * get Stack
     * @return the Stack
     */
    public Stack getStack() { return mStack; }

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
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof PopStack
     */
    public PopStackTag()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * start tag
     */
    public int doStartTag()
	throws JspException
    {
	pageContext.setAttribute(getId(), this);

	if(mStack == null)
	    return SKIP_BODY;

	if(mStack.empty())
	    mNavigation = null;
	else {
	    mNavigation = (DMSNavigationState) mStack.pop();
	}

	return EVAL_BODY_INCLUDE;
    }

    //----------------------------------------
    /**
     * end tag
     */
    public int doEndTag()
	throws JspException
    {
	mNavigation = null;
	return EVAL_PAGE;
    }

    //----------------------------------------
    /**
     * release tag
     */
    public void release()
    {
	super.release();
	mStack = null;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
