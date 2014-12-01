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
 * renders body if Object attribute is null
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/IfNullTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class IfNullTag
    extends BooleanConditionalTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/IfNullTag.java#2 $$Change: 651448 $";

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
    // Value
    private Object mValue;
    /**
     * set Value
     * @param pValue the Value
     */
    public void setValue(Object pValue) { mValue = pValue; }
    public void setValue(String pValue) 
    {
	if(pValue == null || pValue.equals("null"))
	    mValue = null;
	else
	    mValue = pValue;
    }
    /**
     * get Value
     * @return the Value
     */
    public Object getValue() { return mValue; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof IfNullTag
     */
    public IfNullTag()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    // GenericTag methods
    //----------------------------------------

    //----------------------------------------
    /**
     * override the doStartTag method
     * render the body if the object attribute is null
     */
    public int doStartTag()
	throws JspException
    {
	if(isChildOfExclusiveIf() && getExclusiveIfTag().isTesting() == false)
	    return SKIP_BODY;

	if(getValue() == null) {
	    doneTesting();
	    return EVAL_BODY_INCLUDE;
	}
	else {
	    return SKIP_BODY;
	}
    }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	super.release();
	setValue(null);
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
