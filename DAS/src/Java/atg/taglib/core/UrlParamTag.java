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
import java.util.Hashtable.*;

/****************************************
 * The core:UrlParam tag is a child tag of the core:CreateUrl tag. It may not be used
 * outside of the body of a core:CreateUrl tag. This tag is used to
 * specify param/value pairs to be used in constructing a new url using
 * the core:CreateUrl tag.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/UrlParamTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class UrlParamTag
    extends GenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/UrlParamTag.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    //----------------------------------------
    // ChildOfCreateUrlTag
    private boolean mChildOfCreateUrlTag;
    /**
     * set ChildOfCreateUrlTag
     * @param pChildOfCreateUrlTag the ChildOfCreateUrlTag
     */
    public void setChildOfCreateUrlTag(boolean pChildOfCreateUrlTag) { mChildOfCreateUrlTag = pChildOfCreateUrlTag; }
    /**
     * get ChildOfCreateUrlTag
     * @return the ChildOfCreateUrlTag
     */
    public boolean isChildOfCreateUrlTag() { return mChildOfCreateUrlTag; }

    //----------------------------------------
    // Param
    private String mParam;
    /**
     * set Param
     * @param pParam the Param
     */
    public void setParam(String pParam) { mParam = pParam; }
    /**
     * get Param
     * @return the Param
     */
    public String getParam() { return mParam; }

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
    // CreateUrlTag
    private CreateUrlTag mCreateUrlTag;
    /**
     * set CreateUrlTag
     * @param pCreateUrlTag the CreateUrlTag
     */
    public void setCreateUrlTag(CreateUrlTag pCreateUrlTag) { mCreateUrlTag = pCreateUrlTag; }
    /**
     * get CreateUrlTag
     * @return the CreateUrlTag
     */
    public CreateUrlTag getCreateUrlTag() { return mCreateUrlTag; }

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof UrlParamTag
     */
    public UrlParamTag()
    {
	
    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * start executing this tag
     */
    public int doStartTag()
	throws JspException
    {
	if(!isChildOfCreateUrlTag())
	    return SKIP_BODY;

	else if(getParam() != null && getValue() != null)
	    getCreateUrlTag().append(getParam(), getValue().toString());

	return EVAL_BODY_INCLUDE;
    }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	super.release();

	cleanup();
    }
    protected void cleanup()
    {
      setChildOfCreateUrlTag(false);
      setCreateUrlTag(null);
      setParam(null);
      setValue(null);
    }

    //----------------------------------------
    /**
     * override the setParent method
     */
    public void setParent(Tag pParent)
    {
	super.setParent(pParent);

	if(pParent instanceof CreateUrlTag) {
	    mChildOfCreateUrlTag = true;
	    mCreateUrlTag = (CreateUrlTag) pParent;
	}
	else {
	    mCreateUrlTag = 
		(CreateUrlTag) TagSupport.findAncestorWithClass(this, atg.taglib.core.CreateUrlTag.class);
	    
	    if(mCreateUrlTag != null)
		mChildOfCreateUrlTag = true;
	}
    }
    public int doEndTag() throws JspException
    {
      int retval = super.doEndTag();
      cleanup();
      return retval;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
