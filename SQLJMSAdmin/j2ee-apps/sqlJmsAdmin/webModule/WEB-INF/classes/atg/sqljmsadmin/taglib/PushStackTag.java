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
 * push a navigation state onto a stack
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/PushStackTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class PushStackTag
    extends TagSupport
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/PushStackTag.java#2 $$Change: 651448 $";

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
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof PushStack
     */
    public PushStackTag()
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
	if(mStack == null || mNavigation == null)
	    return SKIP_BODY;

	DMSNavigationState state = (DMSNavigationState) mNavigation.clone();
	mStack.push(state);

	return EVAL_BODY_INCLUDE;
    }

    //----------------------------------------
    /**
     * release tag
     */
    public void release()
    {
	super.release();
	mNavigation = null;
	mStack = null;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
