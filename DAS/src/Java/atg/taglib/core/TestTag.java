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
 * Always renders the body, but exposes several output properties based
 * on an examination of the value:
 * <p>
 * example:
 * <p>
 * <code>
 * <pre>
 * &lt;core:Test id="testMyObject" value="&lt;%= myObject %&gt;"&gt; 
 *   I wonder what my object's hashcode is. Oh, it's &lt;%= testMyObject.getHashcode() %&gt;
 * &lt;/core:Test&gt;
 * </pre>
 * </code>
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/TestTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class TestTag
    extends GenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/TestTag.java#2 $$Change: 651448 $";

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
    public void setValue(Object pValue) {
      mValue = pValue;
      clearTestValues();
    }
    /**
     * get Value
     * @return the Value
     */
    public Object getValue() { return mValue; }

    //----------------------------------------
    // Null
    /**
     * get Null
     * @return the Null
     */
    public boolean isNull() { return (getValue() == null) ? true : false; }

    //----------------------------------------
    // Array
    private boolean mArray;
    private boolean mArraySet;
    /**
     * get Array
     * @return the Array
     */
    public boolean isArray() 
    { 
	if(mArraySet)
	    return mArray;

	if(mValue == null)
	    mArray = false;
	else if(mValue.getClass().isArray())
	    mArray = true;
	else
	    mArray = false;

	mArraySet = true;
	return mArray;
    }

    //----------------------------------------
    // Collection
    private boolean mCollection;
    private boolean mCollectionSet;
    /**
     * get Collection
     * @return the Collection
     */
    public boolean isCollection() 
    {
	if(mCollectionSet)
	    return mCollection;

	if(mValue == null)
	    mCollection = false;
	else if(mValue instanceof Collection)
	    mCollection = true;
	else
	    mCollection = false;

	mCollectionSet = true;
	return mCollection;
    }

    //----------------------------------------
    // ArrayOrCollection
    private boolean mArrayOrCollection;
    private boolean mArrayOrCollectionSet;
    /**
     * get ArrayOrCollection
     * @return the ArrayOrCollection
     */
    public boolean isArrayOrCollection() 
    { 
	if(mArrayOrCollectionSet)
	    return mArrayOrCollection;
	
	if(isArray() || isCollection())
	    mArrayOrCollection = true;
	else
	    mArrayOrCollection = false;
	
	mArrayOrCollectionSet = true;
	return mArrayOrCollection;
    }

    //----------------------------------------
    // Size
    /**
     * get Size
     * @return the Size
     */
    public int getSize() 
    { 
	if(isArray()) {
	    if(mValue instanceof Object[])
		return ((Object[]) mValue).length;
	    else if(mValue instanceof boolean[])
		return ((boolean[]) mValue).length;
	    else if(mValue instanceof byte[])
		return ((byte[]) mValue).length;
	    else if(mValue instanceof char[])
		return ((char[]) mValue).length;
	    else if(mValue instanceof short[])
		return ((short[]) mValue).length;
	    else if(mValue instanceof int[])
		return ((int[]) mValue).length;
	    else if(mValue instanceof long[])
		return ((long[]) mValue).length;
	    else if(mValue instanceof float[])
		return ((float[]) mValue).length;
	    else if(mValue instanceof double[])
		return ((double[]) mValue).length;
	    else
		return 0;
	}
	else if(isCollection()) {
	    return ((Collection) mValue).size();
	}
	else if(mValue instanceof Map) {
	    return ((Map) mValue).size();
	}
	else if(mValue instanceof String) {
	    return ((String) mValue).length();
	}
	
	return -1;
    }

    //----------------------------------------
    // Empty
    /**
     * get Empty
     * @return the Empty
     */
    public boolean isEmpty() 
    { 
	if(isNull()) {
	    return true;
	}
	else if(isArrayOrCollection() || mValue instanceof String) {
	    if(getSize() == 0)
		return true;
	    else
		return false;
	}
	else if(mValue instanceof Iterator) {
	    if(((Iterator) mValue).hasNext())
		return false;
	    else 
		return true;
	}
	else if(mValue instanceof Enumeration) {
	    if(((Enumeration) mValue).hasMoreElements())
		return false;
	    else
		return true;
	}
	else if(mValue instanceof Map) {
	    if(((Map) mValue).isEmpty())
		return true;
	    else
		return false;
	}
	
	return false;
    }

    //----------------------------------------
    // ValueClass
    private Class mValueClass;
    private boolean mValueClassSet;
    /**
     * get ValueClass
     * @return the ValueClass
     */
    public Class getValueClass() 
    {
	if(mValueClassSet)
	    return mValueClass;

	if(mValue == null)
	    mValueClass = null;
	else
	    mValueClass = mValue.getClass();

	mValueClassSet = true;
	return mValueClass;
    }

    //----------------------------------------
    // Hashcode
    private int mHashcode;
    private boolean mHashcodeSet;
    /**
     * get Hashcode
     * @return the Hashcode
     */
    public int getHashcode() 
    {
	if(mHashcodeSet)
	    return mHashcode;

	if(mValue == null)
	    mHashcode = 0;
	else
	    mHashcode = mValue.hashCode();

	mHashcodeSet = true;
	return mHashcode;
    }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof TestTag
     */
    public TestTag()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * start this tag
     */
    public int doStartTag()
	throws JspException
    {
	getPageContext().setAttribute(getId(), this);

	return EVAL_BODY_INCLUDE;
    }


    protected void clearTestValues() {
      	mArraySet = false;
	mCollectionSet = false;
	mArrayOrCollectionSet = false;
	mValueClassSet = false;
	mHashcodeSet = false;
    }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	super.release();

	mId = null;
	mValue = null;
        clearTestValues();
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
