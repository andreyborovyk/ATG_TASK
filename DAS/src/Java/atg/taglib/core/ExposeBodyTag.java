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

package atg.taglib.core;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.io.*;

/****************************************
 * expose the body of this tag as the scripting variable defined by
 * the id attribute.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/ExposeBodyTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class ExposeBodyTag
    extends GenericBodyTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/ExposeBodyTag.java#2 $$Change: 651448 $";

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
    // Id
    private String mId;
    /**
     * set Id
     * @param pId the Id
     */
    public void setId(String pId) { mId = pId; }
    /**
     * get Id
     * @return the Id
     */
    public String getId() { return mId; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof ExposeBodyTag
     */
    public ExposeBodyTag()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * start the tag
     */
    public int doStartTag()
	throws JspException
    {
	return EVAL_BODY_TAG;
    }

    //----------------------------------------
    /**
     * init the body
     */
    public void doInitBody()
	throws JspException
    {

    }

    //----------------------------------------
    /**
     * do after body
     */
    public int doAfterBody()
	throws JspException
    {
	getPageContext().setAttribute(getId(), getBodyContent().getString());
	return SKIP_BODY;
    }

    //----------------------------------------
    /**
     * do end tag cleanup
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
	super.release();
	mId = null;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
