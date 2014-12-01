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
 * superclass for OrderBy and OrderByReverse tags
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/OrderingTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class OrderingTag
    extends GenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/OrderingTag.java#2 $$Change: 651448 $";

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
    // Property
    protected String mProperty;
    /**
     * set Property
     * @param pProperty the Property
     */
    public void setProperty(String pProperty) { mProperty = pProperty; }
    /**
     * get Property
     * @return the Property
     */
    public String getProperty() { return mProperty; }

    //----------------------------------------
    // SortTag
    protected SortTag mSortTag;
    /**
     * set SortTag
     * @param pSortTag the SortTag
     */
    public void setSortTag(SortTag pSortTag) { mSortTag = pSortTag; }
    /**
     * get SortTag
     * @return the SortTag
     */
    public SortTag getSortTag() { return mSortTag; }

    //----------------------------------------
    // ChildOfSortTag
    protected boolean mChildOfSortTag;
    /**
     * set ChildOfSortTag
     * @param pChildOfSortTag the ChildOfSortTag
     */
    public void setChildOfSortTag(boolean pChildOfSortTag) { mChildOfSortTag = pChildOfSortTag; }
    /**
     * get ChildOfSortTag
     * @return the ChildOfSortTag
     */
    public boolean isChildOfSortTag() { return mChildOfSortTag; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof OrderingTag
     */
    public OrderingTag()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	super.release();

	mProperty = null;
    }

    //----------------------------------------
    /**
     * set the parent tag
     */
    public void setParent(Tag pParent)
    {
	super.setParent(pParent);

	if(pParent instanceof SortTag) {
	    mSortTag = (SortTag) pParent;
	    mChildOfSortTag = true;
	}
	else {
	    mSortTag = (SortTag) TagSupport.findAncestorWithClass(this, atg.taglib.core.SortTag.class);

	    if(mSortTag != null)
		mChildOfSortTag = true;
	}
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
