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

package atg.taglib.core;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/****************************************
 * this is a generic tag that should be extended
 * by any other tag class that will render its body
 * at most once. subclasses should override the doStartTag
 * method, and should also override the release method, but still 
 * call super.release() to ensure that the BodyContent, PageContext and
 * parent tag all get cleared. Since tags can be pulled, state needs
 * to be reset after tag usage.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/GenericTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class GenericTag
    implements Tag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/GenericTag.java#2 $$Change: 651448 $";

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
    // PageContext
    private PageContext mPageContext;
    /**
     * set PageContext
     * @param pPageContext the PageContext
     */
    public void setPageContext(PageContext pPageContext) { mPageContext = pPageContext; }
    /**
     * get PageContext
     * @return the PageContext
     */
    public PageContext getPageContext() { return mPageContext; }

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
     * @return EVAL_BODY_INCLUDE if the body should be evaluated, SKIP_BODY 
     * if the body should not be evaluated
     * @exception JspException if there was an error
     */
    public int doStartTag()
	throws JspException
    {
	return SKIP_BODY;
    }

    //----------------------------------------
    /**
     * finish executing this tag
     * @return EVAL_PAGE so that the rest of the page gets evaluated
     * @exception JspException if there was an error
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
	setPageContext(null);
	setParent(null);
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class

