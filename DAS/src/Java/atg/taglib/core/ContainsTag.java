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
 * The atg:Contains tag tests to see if an Object or primitive is equal
 * to any of the Objects or primitives within the values attribute of the
 * atg:Contains tag. If
 * so, the body is rendered, if not, then it's not. This tag can take an
 * optional java.util.Comparator instance attribute. If one
 * is supplied, then the 
 * values attribute is considered to contain the Object or primitive attribute if the
 * Object or primitive attribute is equal to any of the values of the
 * Objects or primitives within
 * the values attribute, as defined by the Comparator instance. The
 * casting and comparison rules for the atg:Contains tag are the same as
 * those used in the <a href="#ifEqual">atg:IfEqual</a> tag.
 * <p>
 * example:
 * <p>
 * <code>
 * <pre>
 * &lt;atg:Contains values="&lt;%= specialCustomerIds %&gt;"
 *           object="&lt;%= profile.getId() %&gt;"&gt;
 *   
 *   Hey &lt;%= profile.getFirstName() %&gt;, you're one of our special
 *   customers. Isn't that special? 
 *   
 * &lt;/atg:Contains&gt;
 * </pre>
 * </code>
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/ContainsTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class ContainsTag
    extends BooleanConditionalTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/ContainsTag.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    // the item is in the set of valuesn
    public static final int CONTAINS = 0;
    
    // the item is not in the set of values
    public static final int NOT_CONTAINS = 1;

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // Comparator
    private Comparator mComparator;
    /**
     * set Comparator
     * @param pComparator the Comparator
     */
    public void setComparator(Comparator pComparator) { mComparator = pComparator; }
    /**
     * get Comparator
     * @return the Comparator
     */
    public Comparator getComparator() { return mComparator; }

    //----------------------------------------
    // CompareElement
    private boolean mCompareElement;
    /**
     * set CompareElement
     * @param pCompareElement the CompareElement
     */
    public void setCompareElement(boolean pCompareElement) { mCompareElement = pCompareElement; }
    /**
     * get CompareElement
     * @return the CompareElement
     */
    public boolean isCompareElement() { return mCompareElement; }

    //----------------------------------------
    // Values
    private Object mValues;
    /**
     * set Values
     * @param pValues the Values
     */
    public void setValues(Object pValues) { mValues = pValues; }
    /**
     * get Values
     * @return the Values
     */
    public Object getValues() { return mValues; }

    //----------------------------------------
    // Object
    private Object mObject;
    /**
     * set Object
     * @param pObject the Object
     */
    public void setObject(Object pObject) { 
	mObject = pObject; 
    }
    /**
     * get Object
     * @return the Object
     */
    public Object getObject() { return mObject; }

    //----------------------------------------
    // Boolean
    private boolean mBoolean;
    /**
     * set Boolean
     * @param pBoolean the Boolean
     */
    public void setBoolean(boolean pBoolean) 
    { 
	mBoolean = pBoolean; 
	setBooleanSet(true);
    }
    /**
     * get Boolean
     * @return the Boolean
     */
    public boolean isBoolean() { return mBoolean; }

    //----------------------------------------
    // BooleanSet
    private boolean mBooleanSet;
    /**
     * set BooleanSet
     * @param pBooleanSet the BooleanSet
     */
    public void setBooleanSet(boolean pBooleanSet) { mBooleanSet = pBooleanSet; }
    /**
     * get BooleanSet
     * @return the BooleanSet
     */
    public boolean isBooleanSet() { return mBooleanSet; }

    //----------------------------------------
    // Byte
    private byte mByte;
    /**
     * set Byte
     * @param pByte the Byte
     */
    public void setByte(byte pByte) 
    { 
	mByte = pByte; 
	setByteSet(true);
    }
    /**
     * get Byte
     * @return the Byte
     */
    public byte getByte() { return mByte; }

    //----------------------------------------
    // ByteSet
    private boolean mByteSet;
    /**
     * set ByteSet
     * @param pByteSet the ByteSet
     */
    public void setByteSet(boolean pByteSet) { mByteSet = pByteSet; }
    /**
     * get ByteSet
     * @return the ByteSet
     */
    public boolean isByteSet() { return mByteSet; }

    //----------------------------------------
    // Char
    private char mChar;
    /**
     * set Char
     * @param pChar the Char
     */
    public void setChar(char pChar) 
    { 
	mChar = pChar; 
	setCharSet(true);
    }
    /**
     * get Char
     * @return the Char
     */
    public char getChar() { return mChar; }

    //----------------------------------------
    // CharSet
    private boolean mCharSet;
    /**
     * set CharSet
     * @param pCharSet the CharSet
     */
    public void setCharSet(boolean pCharSet) { mCharSet = pCharSet; }
    /**
     * get CharSet
     * @return the CharSet
     */
    public boolean isCharSet() { return mCharSet; }

    //----------------------------------------
    // Short
    private short mShort;
    /**
     * set Short
     * @param pShort the Short
     */
    public void setShort(short pShort) 
    { 
	mShort = pShort; 
	setShortSet(true);
    }
    /**
     * get Short
     * @return the Short
     */
    public short getShort() { return mShort; }

    //----------------------------------------
    // ShortSet
    private boolean mShortSet;
    /**
     * set ShortSet
     * @param pShortSet the ShortSet
     */
    public void setShortSet(boolean pShortSet) { mShortSet = pShortSet; }
    /**
     * get ShortSet
     * @return the ShortSet
     */
    public boolean isShortSet() { return mShortSet; }

    //----------------------------------------
    // Int
    private int mInt;
    /**
     * set Int
     * @param pInt the Int
     */
    public void setInt(int pInt) 
    { 
	mInt = pInt; 
	setIntSet(true);
    }
    /**
     * get Int
     * @return the Int
     */
    public int getInt() { return mInt; }

    //----------------------------------------
    // IntSet
    private boolean mIntSet;
    /**
     * set IntSet
     * @param pIntSet the IntSet
     */
    public void setIntSet(boolean pIntSet) { mIntSet = pIntSet; }
    /**
     * get IntSet
     * @return the IntSet
     */
    public boolean isIntSet() { return mIntSet; }

    //----------------------------------------
    // Long
    private long mLong;
    /**
     * set Long
     * @param pLong the Long
     */
    public void setLong(long pLong) 
    { 
	mLong = pLong; 
	setLongSet(true);
    }
    /**
     * get Long
     * @return the Long
     */
    public long getLong() { return mLong; }

    //----------------------------------------
    // LongSet
    private boolean mLongSet;
    /**
     * set LongSet
     * @param pLongSet the LongSet
     */
    public void setLongSet(boolean pLongSet) { mLongSet = pLongSet; }
    /**
     * get LongSet
     * @return the LongSet
     */
    public boolean isLongSet() { return mLongSet; }

    //----------------------------------------
    // Float
    private float mFloat;
    /**
     * set Float
     * @param pFloat the Float
     */
    public void setFloat(float pFloat) 
    { 
	mFloat = pFloat; 
	setFloatSet(true);
    }
    /**
     * get Float
     * @return the Float
     */
    public float getFloat() { return mFloat; }

    //----------------------------------------
    // FloatSet
    private boolean mFloatSet;
    /**
     * set FloatSet
     * @param pFloatSet the FloatSet
     */
    public void setFloatSet(boolean pFloatSet) { mFloatSet = pFloatSet; }
    /**
     * get FloatSet
     * @return the FloatSet
     */
    public boolean isFloatSet() { return mFloatSet; }

    //----------------------------------------
    // Double
    private double mDouble;
    /**
     * set Double
     * @param pDouble the Double
     */
    public void setDouble(double pDouble) 
    { 
	mDouble = pDouble; 
	setDoubleSet(true);
    }
    /**
     * get Double
     * @return the Double
     */
    public double getDouble() { return mDouble; }

    //----------------------------------------
    // DoubleSet
    private boolean mDoubleSet;
    /**
     * set DoubleSet
     * @param pDoubleSet the DoubleSet
     */
    public void setDoubleSet(boolean pDoubleSet) { mDoubleSet = pDoubleSet; }
    /**
     * get DoubleSet
     * @return the DoubleSet
     */
    public boolean isDoubleSet() { return mDoubleSet; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof ContainsTag
     */
    public ContainsTag()
    {
    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * test an iterator for inclusion
     */
    protected int checkForInclusion(Iterator pIterator) 
    {
	// Object
	if(getObject() != null) {

	    // see if a comparator was supplied, if so, use it
	    if(getComparator() != null) {
		while(pIterator.hasNext()) {
		    if(getComparator().compare(pIterator.next(),
					       getObject()) == 0)
			return CONTAINS;
		}
		return NOT_CONTAINS;
	    }

	    // if no comparator was supplied, see if the object is
	    // comparable
	    else if(getObject() instanceof Comparable) {
		Comparable comparable1 = (Comparable) getObject();
		Comparable comparable2;

		while(pIterator.hasNext()) {
		    comparable2 = (Comparable) pIterator.next();

		    if(comparable1.compareTo(comparable2) == 0)
			return CONTAINS;
		}
		return NOT_CONTAINS;
	    }

	    // if no comparator was supplied, and the object is not
	    // comparable, test using good old dot equals
	    else {
		while(pIterator.hasNext()) {
		    if(getObject().equals(pIterator.next()))
			return CONTAINS;
		}
		return NOT_CONTAINS;
	    }
	}
	if(getObject() == null) {
	    // test to see if iterator contains null
	    
	    while(pIterator.hasNext()) {
		if(pIterator.next() == null)
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Boolean
	if(isBooleanSet()) {
	    while(pIterator.hasNext()) {
		if(isBoolean() == ((Boolean) pIterator.next()).booleanValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Byte
	if(isByteSet()) {
	    while(pIterator.hasNext()) {
		if(getByte() == ((Byte) pIterator.next()).byteValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Char
	if(isCharSet()) {
	    while(pIterator.hasNext()) {
		if(getChar() == ((Character) pIterator.next()).charValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}
	    
	// Short
	if(isShortSet()) {
	    while(pIterator.hasNext()) {
		if(getShort() == ((Short) pIterator.next()).shortValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Int
	if(isIntSet()) {
	    while(pIterator.hasNext()) {
		if(getInt() == ((Integer) pIterator.next()).intValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Long
	if(isLongSet()) {
	    while(pIterator.hasNext()) {
		if(getLong() == ((Long) pIterator.next()).longValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Float
	if(isFloatSet()) {
	    while(pIterator.hasNext()) {
		if(getFloat() == ((Float) pIterator.next()).floatValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Double
	if(isDoubleSet()) {
	    while(pIterator.hasNext()) {
		if(getDouble() == ((Double) pIterator.next()).doubleValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	return NOT_CONTAINS;
    }

    //----------------------------------------
    /**
     * test an Enumeration for inclusion
     */
    protected int checkForInclusion(Enumeration pEnumeration)
    {
	// Object
	if(getObject() != null) {

	    // see if a comparator was supplied, if so, use it
	    if(getComparator() != null) {
		while(pEnumeration.hasMoreElements()) {
		    if(getComparator().compare(pEnumeration.nextElement(),
					       getObject()) == 0)
			return CONTAINS;
		}
		return NOT_CONTAINS;
	    }

	    // if no comparator was supplied, see if the object is
	    // comparable
	    else if(getObject() instanceof Comparable) {
		Comparable comparable1 = (Comparable) getObject();
		Comparable comparable2;

		while(pEnumeration.hasMoreElements()) {
		    comparable2 = (Comparable) pEnumeration.nextElement();

		    if(comparable1.compareTo(comparable2) == 0)
			return CONTAINS;
		}
		return NOT_CONTAINS;
	    }

	    // if no comparator was supplied, and the object is not
	    // comparable, test using good old dot equals
	    else {
		while(pEnumeration.hasMoreElements()) {
		    if(getObject().equals(pEnumeration.nextElement()))
			return CONTAINS;
		}
		return NOT_CONTAINS;
	    }
	}
	
	if(getObject() == null){
	    // test to see if enumeration contains null
	    while(pEnumeration.hasMoreElements()) {
		if(pEnumeration.nextElement() == null)
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Boolean
	if(isBooleanSet()) {
	    while(pEnumeration.hasMoreElements()) {
		if(isBoolean() == ((Boolean) pEnumeration.nextElement()).booleanValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Byte
	if(isByteSet()) {
	    while(pEnumeration.hasMoreElements()) {
		if(getByte() == ((Byte) pEnumeration.nextElement()).byteValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Char
	if(isCharSet()) {
	    while(pEnumeration.hasMoreElements()) {
		if(getChar() == ((Character) pEnumeration.nextElement()).charValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}
	    
	// Short
	if(isShortSet()) {
	    while(pEnumeration.hasMoreElements()) {
		if(getShort() == ((Short) pEnumeration.nextElement()).shortValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Int
	if(isIntSet()) {
	    while(pEnumeration.hasMoreElements()) {
		if(getInt() == ((Integer) pEnumeration.nextElement()).intValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Long
	if(isLongSet()) {
	    while(pEnumeration.hasMoreElements()) {
		if(getLong() == ((Long) pEnumeration.nextElement()).longValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Float
	if(isFloatSet()) {
	    while(pEnumeration.hasMoreElements()) {
		if(getFloat() == ((Float) pEnumeration.nextElement()).floatValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Double
	if(isDoubleSet()) {
	    while(pEnumeration.hasMoreElements()) {
		if(getDouble() == ((Double) pEnumeration.nextElement()).doubleValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	return NOT_CONTAINS;
    }

    //----------------------------------------
    /**
     * check an object array for inclusion
     */
    protected int checkForInclusion(Object[] pObjects)
    {
	// Object
	if(getObject() != null) {

	    // see if a comparator was supplied, if so, use it
	    if(getComparator() != null) {
		for(int i=0; i < pObjects.length; i++) {
		    if(getComparator().compare(pObjects[i],
					       getObject()) == 0)
			return CONTAINS;
		}
		return NOT_CONTAINS;
	    }

	    // if no comparator was supplied, see if the object is
	    // comparable
	    else if(getObject() instanceof Comparable) {
		Comparable comparable1 = (Comparable) getObject();
		Comparable comparable2;

		for(int i=0; i < pObjects.length; i++) {
		    comparable2 = (Comparable) pObjects[i];

		    if(comparable1.compareTo(comparable2) == 0)
			return CONTAINS;
		}
		return NOT_CONTAINS;
	    }

	    // if no comparator was supplied, and the object is not
	    // comparable, test using good old dot equals
	    else {
		for(int i=0; i < pObjects.length; i++) {
		    if(getObject().equals(pObjects[i]))
			return CONTAINS;
		}
		return NOT_CONTAINS;
	    }
	}

	if(getObject() == null) {
	    // check to see if array contains null
	    for(int i=0; i < pObjects.length; i++) {
		if(pObjects[i] == null)
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Boolean
	if(isBooleanSet()) {
	    for(int i=0; i < pObjects.length; i++) {
		if(isBoolean() == ((Boolean) pObjects[i]).booleanValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Byte
	if(isByteSet()) {
	    for(int i=0; i < pObjects.length; i++) {
		if(getByte() == ((Byte) pObjects[i]).byteValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Char
	if(isCharSet()) {
	    for(int i=0; i < pObjects.length; i++) {
		if(getChar() == ((Character) pObjects[i]).charValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}
	    
	// Short
	if(isShortSet()) {
	    for(int i=0; i < pObjects.length; i++) {
		if(getShort() == ((Short) pObjects[i]).shortValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Int
	if(isIntSet()) {
	    for(int i=0; i < pObjects.length; i++) {
		if(getInt() == ((Integer) pObjects[i]).intValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Long
	if(isLongSet()) {
	    for(int i=0; i < pObjects.length; i++) {
		if(getLong() == ((Long) pObjects[i]).longValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Float
	if(isFloatSet()) {
	    for(int i=0; i < pObjects.length; i++) {
		if(getFloat() == ((Float) pObjects[i]).floatValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	// Double
	if(isDoubleSet()) {
	    for(int i=0; i < pObjects.length; i++) {
		if(getDouble() == ((Double) pObjects[i]).doubleValue())
		    return CONTAINS;
	    }
	    return NOT_CONTAINS;
	}

	return NOT_CONTAINS;
    }

    //----------------------------------------
    // GenericTag methods
    //----------------------------------------

    //----------------------------------------
    /**
     * render this tag if the values attribute contains
     * the specified value
     */
    public int doStartTag()
	throws JspException
    {
	if(getValues() == null)
	    return SKIP_BODY;

	if(isChildOfExclusiveIf() && getExclusiveIfTag().isTesting() == false)
	    return SKIP_BODY;

	// first, determine the type of the values attribute
	if(getValues() instanceof Collection) {
	    Collection values = (Collection) getValues();

	    if(checkForInclusion(values.iterator()) == CONTAINS) {
		doneTesting();
		return EVAL_BODY_INCLUDE;
	    }
	    else
		return SKIP_BODY;
	}

	// Iterator
	else if(getValues() instanceof Iterator) {
	    Iterator values = (Iterator) getValues();
	    
	    if(checkForInclusion(values) == CONTAINS) {
		doneTesting();
		return EVAL_BODY_INCLUDE;
	    }
	    else
		return SKIP_BODY;
	}

	// Object Array
	else if(getValues() instanceof Object[]) {
	    Object[] values = (Object[]) getValues();

	    if(checkForInclusion(values) == CONTAINS) {
		doneTesting();
		return EVAL_BODY_INCLUDE;
	    }
	    else
		return SKIP_BODY;
	}

	// Enumeration
	else if(getValues() instanceof Enumeration) {
	    Enumeration values = (Enumeration) getValues();
	    if(checkForInclusion(values) == CONTAINS) {
		doneTesting();
		return EVAL_BODY_INCLUDE;
	    }
	    else
		return SKIP_BODY;
	}

	// Map
	else if(getValues() instanceof Map) {
	    Map values = (Map) getValues();

	    if((isCompareElement() ?
		checkForInclusion(values.values().iterator()) :
		checkForInclusion(values.keySet().iterator())) == CONTAINS) {
		doneTesting();
		return EVAL_BODY_INCLUDE;
	    }
	    else
		return SKIP_BODY;
	}

	// boolean array
	else if(getValues() instanceof boolean[]) {
	    boolean[] values = (boolean[]) getValues();
	    
	    if(isBooleanSet()) {
		for(int i=0; i < values.length; i++) {
		    if(isBoolean() == values[i]) {
			doneTesting();
			return EVAL_BODY_INCLUDE;
		    }
		}
	    }
	    return SKIP_BODY;
	}

	// byte array
	else if(getValues() instanceof byte[]) {
	    byte[] values = (byte[]) getValues();

	    if(isByteSet()) {
		for(int i=0; i < values.length; i++) {
		    if(getByte() == values[i]) {
			doneTesting();
			return EVAL_BODY_INCLUDE;
		    }
		}
	    }
	    return SKIP_BODY;
	}

	// char array
	else if(getValues() instanceof char[]) {
	    char[] values = (char[]) getValues();

	    if(isCharSet()) {
		for(int i=0; i < values.length; i++) {
		    if(getChar() == values[i]) {
			doneTesting();
			return EVAL_BODY_INCLUDE;
		    }
		}
	    }
	    return SKIP_BODY;
	}

	// short array
	else if(getValues() instanceof short[]) {
	    short[] values = (short[]) getValues();

	    if(isShortSet()) {
		for(int i=0; i < values.length; i++) {
		    if(getShort() == values[i]) {
			doneTesting();
			return EVAL_BODY_INCLUDE;
		    }
		}
	    }
	    return SKIP_BODY;
	}

	// int array
	else if(getValues() instanceof int[]) {
	    int[] values = (int[]) getValues();

	    if(isIntSet()) {
		for(int i=0; i < values.length; i++) {
		    if(getInt() == values[i]) {
			doneTesting();
			return EVAL_BODY_INCLUDE;
		    }
		}
	    }
	    return SKIP_BODY;
	}

	// long array
	else if(getValues() instanceof long[]) {
	    long[] values = (long[]) getValues();

	    if(isLongSet()) {
		for(int i=0; i < values.length; i++) {
		    if(getLong() == values[i]) {
			doneTesting();
			return EVAL_BODY_INCLUDE;
		    }
		}
	    }
	    return SKIP_BODY;
	}

	// float array
	else if(getValues() instanceof float[]) {
	    float[] values = (float[]) getValues();

	    if(isFloatSet()) {
		for(int i=0; i < values.length; i++) {
		    if(getFloat() == values[i]) {
			doneTesting();
			return EVAL_BODY_INCLUDE;
		    }
		}
	    }
	    return SKIP_BODY;
	}

	// double array
	else if(getValues() instanceof double[]) {
	    double[] values = (double[]) getValues();

	    if(isDoubleSet()) {
		for(int i=0; i < values.length; i++) {
		    if(getDouble() == values[i]) {
			doneTesting();
			return EVAL_BODY_INCLUDE;
		    }
		}
	    }
	    return SKIP_BODY;
	}

	return SKIP_BODY;
    }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	super.release();

	setComparator(null);
	setCompareElement(false);
	setValues(null);
	setObject(null);
	setBooleanSet(false);
	setByteSet(false);
	setCharSet(false);
	setShortSet(false);
	setIntSet(false);
	setLongSet(false);
	setFloatSet(false);
	setDoubleSet(false);
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
