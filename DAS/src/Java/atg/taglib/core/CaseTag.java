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

import java.util.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/****************************************
 * The core:Case subtag may only be used within the body of the core:Switch
 * tag. The core:Case tag has one attribute called value that is tested for
 * equality with the enclosing core:Switch tag's value attribute. If the value
 * of the core:Case tag's value parameter is equal to the value of the
 * enclosing core:Switch tag's value parameter, then the body of this tag is
 * rendered. Only the first matching core:Case tag's body will be rendered.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/CaseTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class CaseTag
    extends GenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/CaseTag.java#2 $$Change: 651448 $";

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
    /**
     * get Value
     * @return the Value
     */
    public Object getValue() { return mValue; }

    //----------------------------------------
    // ChildOfSwitch
    private boolean mChildOfSwitch;
    /**
     * set ChildOfSwitch
     * @param pChildOfSwitch the ChildOfSwitch
     */
    public void setChildOfSwitch(boolean pChildOfSwitch) { mChildOfSwitch = pChildOfSwitch; }
    /**
     * get ChildOfSwitch
     * @return the ChildOfSwitch
     */
    public boolean isChildOfSwitch() { return mChildOfSwitch; }

    //----------------------------------------
    // SwitchTag
    private SwitchTag mSwitchTag;
    /**
     * set SwitchTag
     * @param pSwitchTag the SwitchTag
     */
    public void setSwitchTag(SwitchTag pSwitchTag) { mSwitchTag = pSwitchTag; }
    /**
     * get SwitchTag
     * @return the SwitchTag
     */
    public SwitchTag getSwitchTag() { return mSwitchTag; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof CaseTag
     */
    public CaseTag()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * override the setParent method
     */
    public void setParent(Tag pParent)
    {
	super.setParent(pParent);

	if(pParent instanceof SwitchTag) {
	    setChildOfSwitch(true);
	    setSwitchTag((SwitchTag) pParent);
	}
    }

    //----------------------------------------
    /**
     * render the body of this tag, or not
     */
    public int doStartTag()
	throws JspException
    {
	if(isChildOfSwitch() &&
	   getSwitchTag().isTesting() &&
	   isEqual()) {
	    getSwitchTag().setTesting(false);
	    return EVAL_BODY_INCLUDE;
	}

	return SKIP_BODY;
    }

    //----------------------------------------
    /**
     * test to see if this object's value attribute is
     * equal to the value attribute of the switch tag.
     * if the switch tag has a comparator, use it. if the
     * both values are of type Comparable, then compare them 
     * that way. if none of the above, then compare using dot equals
     */
    protected boolean isEqual()
    {
	if(getValue() == null && getSwitchTag().getValue() == null)
	    return true;
	else if((getValue() == null && getSwitchTag().getValue() != null) ||
		(getValue() != null && getSwitchTag().getValue() == null))
	    return false;
	else if(getSwitchTag().getComparator() != null) {
	    if(getSwitchTag().getComparator().compare(getValue(),
						      getSwitchTag().getValue()) == 0)
		return true;
	    else
		return false;
	}
	else if(getValue() != null && 
		getSwitchTag().getValue() != null &&
		getValue() instanceof Comparable &&
		getSwitchTag().getValue() instanceof Comparable) {
	    Comparable comparable1 = (Comparable) getValue();
	    Comparable comparable2 = (Comparable) getSwitchTag().getValue();

	    if(comparable1.compareTo(comparable2) == 0)
		return true;
	    else
		return false;
	}
	else {
	    if(getValue().equals(getSwitchTag().getValue()))
		return true;
	    else
		return false;
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
	setChildOfSwitch(false);
	setSwitchTag(null);
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
