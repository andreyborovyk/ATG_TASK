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
 * The superclass for all boolean conditional tags.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/BooleanConditionalTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class BooleanConditionalTag
    extends GenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/BooleanConditionalTag.java#2 $$Change: 651448 $";

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
    // ChildOfExclusiveIf
    private boolean mChildOfExclusiveIf;
    /**
     * set ChildOfExclusiveIf
     * @param pChildOfExclusiveIf the ChildOfExclusiveIf
     */
    public void setChildOfExclusiveIf(boolean pChildOfExclusiveIf) 
    { mChildOfExclusiveIf = pChildOfExclusiveIf; }
    /**
     * get ChildOfExclusiveIf
     * @return the ChildOfExclusiveIf
     */
    public boolean isChildOfExclusiveIf() { return mChildOfExclusiveIf; }

    //----------------------------------------
    // ExclusiveIfTag
    private ExclusiveIfTag mExclusiveIfTag;
    /**
     * set ExclusiveIfTag
     * @param pExclusiveIfTag the ExclusiveIfTag
     */
    public void setExclusiveIfTag(ExclusiveIfTag pExclusiveIfTag) { mExclusiveIfTag = pExclusiveIfTag; }
    /**
     * get ExclusiveIfTag
     * @return the ExclusiveIfTag
     */
    public ExclusiveIfTag getExclusiveIfTag() { return mExclusiveIfTag; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof BooleanConditionalTag
     */
    public BooleanConditionalTag()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * if this tag is a child of an ExclusiveIf tag
     * and the this tag evaluates to true, then mark the
     * ExclusiveIf tag as completed with testing
     */
    public void doneTesting()
    {
	if(isChildOfExclusiveIf())
	    getExclusiveIfTag().setTesting(false);
    }

    //----------------------------------------
    // GenericTag methods
    //----------------------------------------

    //----------------------------------------
    /**
     * override the setParent method
     */
    public void setParent(Tag pParent)
    {
	super.setParent(pParent);

	if(pParent instanceof ExclusiveIfTag) {
	    setChildOfExclusiveIf(true);
	    setExclusiveIfTag((ExclusiveIfTag) pParent);
	}
    }
    
    //----------------------------------------
    /**
     * Cleanup and finish executing this tag
     * @return EVAL_PAGE so that the rest of the page gets evaluated
     * @exception JspException if there was an error
     */
    public int doEndTag()
  throws JspException
    {
      performPerTagCleanup();
      return super.doEndTag();
    }

    //----------------------------------------
    /**
     * override the relase method
     */
    public void release()
    {
	super.release();
	performPerTagCleanup();
    }
    /**
     * 
     */
    protected void performPerTagCleanup() {
      setChildOfExclusiveIf(false);
      setExclusiveIfTag(null);
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
