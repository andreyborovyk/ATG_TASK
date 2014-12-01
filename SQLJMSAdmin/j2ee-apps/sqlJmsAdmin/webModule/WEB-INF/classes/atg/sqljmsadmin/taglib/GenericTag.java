/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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

package atg.sqljmsadmin.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/****************************************
 * a generic starting point for all tags
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/GenericTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public abstract class GenericTag
    implements Tag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/GenericTag.java#2 $$Change: 651448 $";

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
    // Parent
    private Tag mParent;
    /**
     * set Parent
     * @param pParent the Parent
     */
    public void setParent(Tag pParent) { mParent = pParent; }
    /**
     * get Parent
     * @return the Parent
     */
    public Tag getParent() { return mParent; }

    //----------------------------------------
    // BodyContent
    private BodyContent mBodyContent;
    /**
     * set BodyContent
     * @param pBodyContent the BodyContent
     */
    public void setBodyContent(BodyContent pBodyContent) { mBodyContent = pBodyContent; }
    /**
     * get BodyContent
     * @return the BodyContent
     */
    public BodyContent getBodyContent() { return mBodyContent; }

    //----------------------------------------
    // PageContext
    private PageContext pageContext;
    /**
     * set PageContext
     * @param pPageContext the PageContext
     */
    public void setPageContext(PageContext pPageContext) { pageContext = pPageContext; }
    /**
     * get PageContext
     * @return the PageContext
     */
    public PageContext getPageContext() { return pageContext; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof GenericTag
     */
    public GenericTag()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * start executing this tag
     * @return either EVAL_BODY_INCLUDE if the body should be evaluated, or SKIP_BODY
     * if the body should not be evaluated.
     * @exception JspException if there was a jsp error
     */
    public abstract int doStartTag()
	throws JspException;

    //----------------------------------------
    /**
     * finish executing this tag
     * @return EVAL_PAGE so that the rest of the page gets evaluated
     * @exception JspException if there was a jsp error
     */
    public int doEndTag()
	throws JspException
    {
	return EVAL_PAGE;
    }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	mBodyContent = null;
	pageContext = null;
	mParent = null;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
