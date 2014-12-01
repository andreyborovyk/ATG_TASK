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
 * this is a generic tag that should be extended by
 * and tag that will want to manipulate the rendering of
 * its body, like an iterator tag.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/GenericBodyTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class GenericBodyTag
    extends GenericTag
    implements BodyTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/GenericBodyTag.java#2 $$Change: 651448 $";

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
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof GenericBodyTag
     */
    public GenericBodyTag()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

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
     * actions after the body has been evaluated
     */
    public int doAfterBody()
	throws JspException
    {
	return SKIP_BODY;
    }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	super.release();

	setBodyContent(null);
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class

