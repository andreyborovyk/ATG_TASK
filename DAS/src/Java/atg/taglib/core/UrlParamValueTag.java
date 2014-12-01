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

import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/****************************************
 * The core:UrlParamValue tag is used to pull a parameter value out of an http
 * request object. This tag has one String attribute called param, and it
 * outputs one String tag property called paramValue.
 * <p>
 * example:
 * <p>
 * <code>
 * <pre>
 * &lt;core:UrlParamValue id="nameParam" param="name"&gt;
 * 
 *   Hello &lt;%= nameParam.getParamValue() %&gt;, welcome to our lovely
 *   eCommerce site!
 * 
 * &lt;/core:UrlParamValue&gt;
 * </pre>
 * </code> 
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/UrlParamValueTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class UrlParamValueTag
    extends GenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/UrlParamValueTag.java#2 $$Change: 651448 $";

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
    private String mValue;
    /**
     * set Value
     * @param pValue the Value
     */
    public void setValue(String pValue) { mValue = pValue; }
    /**
     * get Value
     * @return the Value
     */
    public String getValue() { return mValue; }

    //----------------------------------------
    // BooleanValue
    /**
     * get BooleanValue
     * @return the BooleanValue
     */
    public boolean isBooleanValue() { return getBooleanObjectValue().booleanValue(); }

    //----------------------------------------
    // ByteValue
    /**
     * get ByteValue
     * @return the ByteValue
     */
    public byte getByteValue() { return getByteObjectValue().byteValue(); }

    //----------------------------------------
    // CharValue
    /**
     * get CharValue
     * @return the CharValue
     */
    public char getCharValue() { return getValue().charAt(0); }

    //----------------------------------------
    // ShortValue
    /**
     * get ShortValue
     * @return the ShortValue
     */
    public short getShortValue() { return getShortObjectValue().shortValue(); }

    //----------------------------------------
    // IntValue
    /**
     * get IntValue
     * @return the IntValue
     */
    public int getIntValue() { return getIntegerObjectValue().intValue(); }

    //----------------------------------------
    // LongValue
    /**
     * get LongValue
     * @return the LongValue
     */
    public long getLongValue() { return getLongObjectValue().longValue(); }

    //----------------------------------------
    // FloatValue
    /**
     * get FloatValue
     * @return the FloatValue
     */
    public float getFloatValue() { return getFloatObjectValue().floatValue(); }

    //----------------------------------------
    // DoubleValue
    /**
     * get DoubleValue
     * @return the DoubleValue
     */
    public double getDoubleValue() { return getDoubleObjectValue().doubleValue(); }

    //----------------------------------------
    // BooleanObjectValue
    /**
     * get BooleanObjectValue
     * @return the BooleanObjectValue
     */
    public Boolean getBooleanObjectValue() { return Boolean.valueOf(getValue()); }

    //----------------------------------------
    // ByteObjectValue
    /**
     * get ByteObjectValue
     * @return the ByteObjectValue
     */
    public Byte getByteObjectValue() { return Byte.valueOf(getValue()); }

    //----------------------------------------
    // CharacterObjectValue
    /**
     * get CharacterObjectValue
     * @return the CharacterObjectValue
     */
    public Character getCharacterObjectValue() { return new Character(getValue().charAt(0)); }

    //----------------------------------------
    // ShortObjectValue
    /**
     * get ShortObjectValue
     * @return the ShortObjectValue
     */
    public Short getShortObjectValue() { return Short.valueOf(getValue()); }

    //----------------------------------------
    // IntegerObjectValue
    /**
     * get IntegerObjectValue
     * @return the IntegerObjectValue
     */
    public Integer getIntegerObjectValue() { return Integer.valueOf(getValue()); }

    //----------------------------------------
    // LongObjectValue
    /**
     * get LongObjectValue
     * @return the LongObjectValue
     */
    public Long getLongObjectValue() { return Long.valueOf(getValue()); }

    //----------------------------------------
    // FloatObjectValue
    /**
     * get FloatObjectValue
     * @return the FloatObjectValue
     */
    public Float getFloatObjectValue() { return Float.valueOf(getValue()); }

    //----------------------------------------
    // DoubleObjectValue
    /**
     * get DoubleObjectValue
     * @return the DoubleObjectValue
     */
    public Double getDoubleObjectValue() { return Double.valueOf(getValue()); }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof UrlParamValueTag
     */
    public UrlParamValueTag()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * execute the tag
     */
    public int doStartTag()
	throws JspException
    {
	if(getParam() == null)
	    return SKIP_BODY;
	
	ServletRequest request = getPageContext().getRequest();
	setValue(request.getParameter(getParam()));

	getPageContext().setAttribute(getId(), this);
	return EVAL_BODY_INCLUDE;
    }

    //----------------------------------------
    /**
     * end this tag
     */
    public int doEndTag()
	throws JspException
    {
	setValue(null);

	return EVAL_PAGE;
    }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	super.release();

	setValue(null);
	setId(null);
	setParam(null);
    }

    //----------------------------------------
    /**
     * overridee the toString method to return the param value
     */
    public String toString()
    {
	return getValue();
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
