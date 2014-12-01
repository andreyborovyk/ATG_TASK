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
 * The atg:DefaultCase subtag may only be used within the body of an atg:Switch
 * tag in conjunction with any number of atg:Case tags, or in the body of
 * an atg:ExclusiveIf tag with any number of boolean conditional tags. The
 * atg:DefaultCase tag  
 * doesn't have any parameters or output properties. The body of the
 * atg:DefaultCase tag is rendered if there are no case tags with a value
 * parameter that is equal to the value parameter of the enclosing switch
 * tag, or if none of the boolean conditionals are true in the body of an
 * atg:ExclusiveIf tag.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/DefaultCaseTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DefaultCaseTag
    extends BooleanConditionalTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/DefaultCaseTag.java#2 $$Change: 651448 $";

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
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof DefaultCaseTag
     */
    public DefaultCaseTag()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * render the body of this tag, or not
     */
    public int doStartTag()
	throws JspException
    {
	if(isChildOfExclusiveIf()) {
	    if(getExclusiveIfTag().isTesting()) {
		getExclusiveIfTag().setTesting(false);
		return EVAL_BODY_INCLUDE;
	    }
	}

	return SKIP_BODY;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
