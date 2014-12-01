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
 * Exposes the specified Object value as a new scripting variable with
 * the specified "castClass". The new value type is assigned dynamically
 * through the TagExtraInfo. This tag breaks with the convention of the
 * other tags in that it exports a variable directly to the tag body
 * rather than as a property of the tag.
 * <p>
 * <code>
 * <pre>
 * &lt;core:Cast id="specificFoo" value="&lt;%= foo.getBar() %&gt;" 
 *           castClass="foo.bar.SpecificTypeOfFoo"&gt;
 *   Here is the price of our brand new model of Foo: &lt;%= specificFoo.getPrice() %&gt;
 * &lt;/core:Cast&gt;
 * </pre>
 * </code>
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/CastTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class CastTag
    extends GenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/CastTag.java#2 $$Change: 651448 $";

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
    // CastClass
    private String mCastClass;
    /**
     * set CastClass
     * @param pCastClass the CastClass
     */
    public void setCastClass(String pCastClass) { mCastClass = pCastClass; }
    /**
     * get CastClass
     * @return the CastClass
     */
    public String getCastClass() { return mCastClass; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof CastTag
     */
    public CastTag()
    {
    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * execute this tag
     */
    public int doStartTag()
	throws JspException
    {
	if(getValue() != null) {
	    getPageContext().setAttribute(getId(), getValue());
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

	setId(null);
	setValue(null);
	setCastClass(null);
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
