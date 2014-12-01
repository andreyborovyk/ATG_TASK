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
import java.util.*;

/****************************************
 * Always renders the body, but exposes several output properties based
 * on a comparison of the two values. The idea is that the results of a
 * compare tag might be used in several core:If tags. This tag can take
 * an optional java.util.Comparator instance attribute. If a Comparator instance is
 * supplied, then the value of the exported tag properties will be based
 * on the result of the comparison as done through the Comparator object.
 * <p>
 * example:
 * <p>
 * <code>
 * <pre>
 * &lt;core:Compare id="cartCompare"
 *              object1="&lt;%= currentShoppingCart %&gt;" 
 *              object2="&lt;%= previousShoppingCart %&gt;"
 *              comparator="&lt;%= myShoppingCartComparator %&gt;"&gt;
 * 
 *   &lt;core:If value="&lt;%= cartCompare.isLessThan() %&gt;"&gt;
 *     Hmm, you spent more money last time you were here. 
 *     Please buy more stuff.
 *   &lt;/core:If&gt;
 * 
 * &lt;/core:Compare&gt;
 * </pre>
 * </code>
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/CompareTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class CompareTag
    extends GenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/CompareTag.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    // if equal tag
    private IfEqualTag mIfEqualTag;

    // greater than tag
    private IfGreaterThanTag mIfGreaterThanTag;

    // greater than or equal tag
    private IfGreaterThanOrEqualTag mIfGreaterThanOrEqualTag;

    // less than tag
    private IfLessThanTag mIfLessThanTag;

    // less than or equal tag
    private IfLessThanOrEqualTag mIfLessThanOrEqualTag;

    // the jsp exception resulting from calling any internally used tags
    private JspException mException;

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
    // Comparator
    private Comparator mComparator;
    private boolean mComparatorSet;
    /**
     * set Comparator
     * @param pComparator the Comparator
     */
    public void setComparator(Comparator pComparator) 
    { 
	mComparator = pComparator; 
	mComparatorSet = true;
    }
    /**
     * get Comparator
     * @return the Comparator
     */
    public Comparator getComparator() { return mComparator; }

    //----------------------------------------
    // Object1
    private Object mObject1;
    private boolean mObject1Set;
    /**
     * set Object1
     * @param pObject1 the Object1
     */
    public void setObject1(Object pObject1) 
    { 
	mObject1 = pObject1; 
	mObject1Set = true;
    }
    /**
     * get Object1
     * @return the Object1
     */
    public Object getObject1() { return mObject1; }

    //----------------------------------------
    // Object2
    private Object mObject2;
    private boolean mObject2Set;
    /**
     * set Object2
     * @param pObject2 the Object2
     */
    public void setObject2(Object pObject2) 
    { 
	mObject2 = pObject2; 
	mObject2Set = true;
    }
    /**
     * get Object2
     * @return the Object2
     */
    public Object getObject2() { return mObject2; }

    //----------------------------------------
    // Boolean1
    private boolean mBoolean1;
    private boolean mBoolean1Set;
    /**
     * set Boolean1
     * @param pBoolean1 the Boolean1
     */
    public void setBoolean1(boolean pBoolean1) 
    { 
	mBoolean1 = pBoolean1; 
	mBoolean1Set = true;
    }
    /**
     * get Boolean1
     * @return the Boolean1
     */
    public boolean isBoolean1() { return mBoolean1; }

    //----------------------------------------
    // Boolean2
    private boolean mBoolean2;
    private boolean mBoolean2Set;
    /**
     * set Boolean2
     * @param pBoolean2 the Boolean2
     */
    public void setBoolean2(boolean pBoolean2) 
    { 
	mBoolean2 = pBoolean2; 
	mBoolean2Set = true;
    }
    /**
     * get Boolean2
     * @return the Boolean2
     */
    public boolean isBoolean2() { return mBoolean2; }

    //----------------------------------------
    // Char1
    private char mChar1;
    private boolean mChar1Set;
    /**
     * set Char1
     * @param pChar1 the Char1
     */
    public void setChar1(char pChar1) 
    { 
	mChar1 = pChar1; 
	mChar1Set = true;
    }
    /**
     * get Char1
     * @return the Char1
     */
    public char getChar1() { return mChar1; }

    //----------------------------------------
    // Char2
    private char mChar2;
    private boolean mChar2Set;
    /**
     * set Char2
     * @param pChar2 the Char2
     */
    public void setChar2(char pChar2) 
    { 
	mChar2 = pChar2; 
	mChar2Set = true;
    }
    /**
     * get Char2
     * @return the Char2
     */
    public char getChar2() { return mChar2; }

    //----------------------------------------
    // Byte1
    private byte mByte1;
    private boolean mByte1Set;
    /**
     * set Byte1
     * @param pByte1 the Byte1
     */
    public void setByte1(byte pByte1) 
    { 
	mByte1 = pByte1; 
	mByte1Set = true;
    }
    /**
     * get Byte1
     * @return the Byte1
     */
    public byte getByte1() { return mByte1; }

    //----------------------------------------
    // Byte2
    private byte mByte2;
    private boolean mByte2Set;
    /**
     * set Byte2
     * @param pByte2 the Byte2
     */
    public void setByte2(byte pByte2) 
    { 
	mByte2 = pByte2; 
	mByte2Set = true;
    }
    /**
     * get Byte2
     * @return the Byte2
     */
    public byte getByte2() { return mByte2; }

    //----------------------------------------
    // Short1
    private short mShort1;
    private boolean mShort1Set;
    /**
     * set Short1
     * @param pShort1 the Short1
     */
    public void setShort1(short pShort1) 
    { 
	mShort1 = pShort1; 
	mShort1Set = true;
    }
    /**
     * get Short1
     * @return the Short1
     */
    public short getShort1() { return mShort1; }

    //----------------------------------------
    // Short2
    private short mShort2;
    private boolean mShort2Set;
    /**
     * set Short2
     * @param pShort2 the Short2
     */
    public void setShort2(short pShort2) 
    { 
	mShort2 = pShort2; 
	mShort2Set = true;
    }
    /**
     * get Short2
     * @return the Short2
     */
    public short getShort2() { return mShort2; }

    //----------------------------------------
    // Int1
    private int mInt1;
    private boolean mInt1Set;
    /**
     * set Int1
     * @param pInt1 the Int1
     */
    public void setInt1(int pInt1) 
    { 
	mInt1 = pInt1; 
	mInt1Set = true;
    }
    /**
     * get Int1
     * @return the Int1
     */
    public int getInt1() { return mInt1; }

    //----------------------------------------
    // Int2
    private int mInt2;
    private boolean mInt2Set;
    /**
     * set Int2
     * @param pInt2 the Int2
     */
    public void setInt2(int pInt2) 
    { 
	mInt2 = pInt2; 
	mInt2Set = true;
    }
    /**
     * get Int2
     * @return the Int2
     */
    public int getInt2() { return mInt2; }

    //----------------------------------------
    // Long1
    private long mLong1;
    private boolean mLong1Set;
    /**
     * set Long1
     * @param pLong1 the Long1
     */
    public void setLong1(long pLong1) 
    { 
	mLong1 = pLong1; 
	mLong1Set = true;
    }
    /**
     * get Long1
     * @return the Long1
     */
    public long getLong1() { return mLong1; }

    //----------------------------------------
    // Long2
    private long mLong2;
    private boolean mLong2Set;
    /**
     * set Long2
     * @param pLong2 the Long2
     */
    public void setLong2(long pLong2) 
    { 
	mLong2 = pLong2; 
	mLong2Set = true;
    }
    /**
     * get Long2
     * @return the Long2
     */
    public long getLong2() { return mLong2; }

    //----------------------------------------
    // Float1
    private float mFloat1;
    private boolean mFloat1Set;
    /**
     * set Float1
     * @param pFloat1 the Float1
     */
    public void setFloat1(float pFloat1) 
    { 
	mFloat1 = pFloat1; 
	mFloat1Set = true;
    }
    /**
     * get Float1
     * @return the Float1
     */
    public float getFloat1() { return mFloat1; }

    //----------------------------------------
    // Float2
    private float mFloat2;
    private boolean mFloat2Set;
    /**
     * set Float2
     * @param pFloat2 the Float2
     */
    public void setFloat2(float pFloat2) 
    { 
	mFloat2 = pFloat2; 
	mFloat2Set = true;
    }
    /**
     * get Float2
     * @return the Float2
     */
    public float getFloat2() { return mFloat2; }

    //----------------------------------------
    // Double1
    private double mDouble1;
    private boolean mDouble1Set;
    /**
     * set Double1
     * @param pDouble1 the Double1
     */
    public void setDouble1(double pDouble1) 
    { 
	mDouble1 = pDouble1; 
	mDouble1Set = true;
    }
    /**
     * get Double1
     * @return the Double1
     */
    public double getDouble1() { return mDouble1; }

    //----------------------------------------
    // Double2
    private double mDouble2;
    private boolean mDouble2Set;
    /**
     * set Double2
     * @param pDouble2 the Double2
     */
    public void setDouble2(double pDouble2) 
    { 
	mDouble2 = pDouble2; 
	mDouble2Set = true;
    }
    /**
     * get Double2
     * @return the Double2
     */
    public double getDouble2() { return mDouble2; }

    //----------------------------------------
    // Equal
    /**
     * get Equal
     * @return the Equal
     */
    public boolean isEqual() 
    { 
	if(mIfEqualTag == null)
	    mIfEqualTag = new IfEqualTag();

	return processTag(mIfEqualTag);
    }

    //----------------------------------------
    // EqualObjects
    /**
     * get EqualObjects
     * @return the EqualObjects
     */
    public boolean isEqualObjects() 
    {
	if(mObject1Set && mObject2Set) {
	    if(mObject1 == mObject2)
		return true;
	    else 
		return false;
	}
	else {
	    return false;
	}
    }

    //----------------------------------------
    // GreaterThan
    /**
     * get GreaterThan
     * @return the GreaterThan
     */
    public boolean isGreaterThan() 
    {
	if(mIfGreaterThanTag == null)
	    mIfGreaterThanTag = new IfGreaterThanTag();

	return processTag(mIfGreaterThanTag);
    }

    //----------------------------------------
    // GreaterThanOrEqual
    /**
     * get GreaterThanOrEqual
     * @return the GreaterThanOrEqual
     */
    public boolean isGreaterThanOrEqual() 
    {
	if(mIfGreaterThanOrEqualTag == null)
	    mIfGreaterThanOrEqualTag = new IfGreaterThanOrEqualTag();

	return processTag(mIfGreaterThanOrEqualTag);
    }

    //----------------------------------------
    // LessThan
    /**
     * get LessThan
     * @return the LessThan
     */
    public boolean isLessThan()
    {
	if(mIfLessThanTag == null)
	    mIfLessThanTag = new IfLessThanTag();

	return processTag(mIfLessThanTag);
    }

    //----------------------------------------
    // LessThanOrEqual
    /**
     * get LessThanOrEqual
     * @return the LessThanOrEqual
     */
    public boolean isLessThanOrEqual()
    {
	if(mIfLessThanOrEqualTag == null)
	    mIfLessThanOrEqualTag = new IfLessThanOrEqualTag();

	return processTag(mIfLessThanOrEqualTag);
    }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof CompareTag
     */
    public CompareTag()
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
	mException = null;
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
	if(mException != null)
	    throw mException;

	return EVAL_PAGE;
    }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	super.release();

	mId = null;
	mComparator = null;
	mObject1Set = false;
	mObject2Set = false;
	mBoolean1Set = false;
	mBoolean2Set = false;
	mByte1Set = false;
	mByte2Set = false;
	mChar1Set = false;
	mChar2Set = false;
	mShort1Set = false;
	mShort2Set = false;
	mInt1Set = false;
	mInt2Set = false;
	mLong1Set = false;
	mLong2Set = false;
	mFloat1Set = false;
	mFloat2Set = false;
	mDouble1Set = false;
	mDouble2Set = false;
    }

    //----------------------------------------
    /**
     * set all the values in a boolean conditional tag
     */
    protected boolean processTag(EqualityBooleanConditionalTag pTag)
    {
	if(pTag == null)
	    return false;

	if(mComparatorSet)
	    pTag.setComparator(mComparator);
	if(mObject1Set)
	    pTag.setObject1(mObject1);
	if(mObject2Set)
	    pTag.setObject2(mObject2);
	if(mBoolean1Set)
	    pTag.setBoolean1(mBoolean1);
	if(mBoolean2Set)
	    pTag.setBoolean2(mBoolean2);
	if(mByte1Set)
	    pTag.setByte1(mByte1);
	if(mByte2Set)
	    pTag.setByte2(mByte2);
	if(mChar1Set)
	    pTag.setChar1(mChar1);
	if(mChar2Set)
	    pTag.setChar2(mChar2);
	if(mShort1Set)
	    pTag.setShort1(mShort1);
	if(mShort2Set)
	    pTag.setShort2(mShort2);
	if(mInt1Set)
	    pTag.setInt1(mInt1);
	if(mInt2Set)
	    pTag.setInt2(mInt2);
	if(mLong1Set)
	    pTag.setLong1(mLong1);
	if(mLong2Set)
	    pTag.setLong2(mLong2);
	if(mFloat1Set)
	    pTag.setFloat1(mFloat1);
	if(mFloat2Set)
	    pTag.setFloat2(mFloat2);
	if(mDouble1Set)
	    pTag.setDouble1(mDouble1);
	if(mDouble2Set)
	    pTag.setDouble2(mDouble2);

	int returnValue = SKIP_BODY;

	try {
	    returnValue = pTag.doStartTag();

	    pTag.doEndTag();
	    pTag.release();
	}
	catch(JspException e) {
	    mException = e;
	}

	if(returnValue == EVAL_BODY_INCLUDE)
	    return true;
	else
	    return false;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
