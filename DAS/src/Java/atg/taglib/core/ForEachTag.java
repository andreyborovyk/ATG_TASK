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
import java.io.*;
import java.util.*;

/****************************************
 * Iterates through an Object or primitive array, Collection,
 * Enumeration, Iterator or Map and renders its body once
 * for each Object or primitive in the array, Collection, Enumeration,
 * Iterator or Map. This tag is able to iterate through all
 * items in the values attribute, or just a subset using the range
 * parameters. This tag incorporates the functionality of the core:Cast by
 * allowing one to optionally export the current value cast up to a
 * specified class.
 * <p>
 * example:
 * <p>
 * <code>
 * <pre>
 * &lt;ol&gt;
 * 
 * &lt;core:ForEach id="people" values="&lt;%= foo.getPeople() %&gt;"
 *              castClass="my.package.SpecificTypeOfPerson"
 * 	     elementId="specificPerson"&gt;
 *   &lt;li&gt;Name: &lt;%= specificPerson.getName() %&gt;
 * &lt;/core:ForEach&gt;
 * 
 * &lt;/ol&gt;
 * </pre>
 * </code>
 * <p>
 * If the startIndex attribute is set while
 * neither rangeCount nor endIndex is set, then the forEach tag will render
 * its body once for each object in the array starting with
 * the object in the startIndex position and ending with the last
 * object. 
 * <p>
 * If the startIndex and rangeCount attributes are set while the endIndex
 * attribute is not set, then the forEach
 * tag will render its body once for each object in the array
 * starting with the object in the startIndex position and ending with
 * the object in startIndex + rangeCount position. 
 * <p>
 * If the startIndex and
 * endIndex attributes are set while the rangeCount attribute is not set, then the
 * forEach tag will render its body once for each object in the
 * array starting with the object in the startIndex position
 * and ending with the object in the endIndex position. 
 * <p>
 * If the endIndex attribute is set while neither the startIndex nor rangeCount
 * attributes are set, then the forEach tag will render its body once for
 * each object in the array starting with the first object and
 * ending with the object in the endIndex position.
 * <p>
 * If the rangeCount and endIndex attributes are set while the startIndex attribute is
 * not set, then the forEach tag will render its body once for each
 * object in the array starting with the object in the
 * endIndex - rangeCount position and ending with the object in the endIndex
 * position.
 * <p>
 * If the rangeCount attribute is set while neither the startIndex nor endIndex
 * attributes are set, then the forEach tag will render its body once for
 * each object in the array starting with the first object and
 * ending with the object in the rangeCount - 1 position.
 * <p>
 * If all three range attributes are set, then the endIndex attribute is ignored.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/ForEachTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class ForEachTag
    extends GenericBodyTag
    implements TagAttributeTypes
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/ForEachTag.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    // the iteration counter
    private int mCounter;

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
    // Step
    private int mStep;
    private boolean mStepSet;
    /**
     * set Step
     * @param pStep the Step
     */
    public void setStep(int pStep) { mStep = pStep; mStepSet = true; }
    /**
     * get Step
     * @return the Step
     */
    public int getStep() { return mStep; }

    //----------------------------------------
    // StartIndex
    private int mStartIndex;
    /**
     * set StartIndex
     * @param pStartIndex the StartIndex
     */
    public void setStartIndex(int pStartIndex) 
    { 
	mStartIndex = pStartIndex; 
	setStartIndexSet(true);
    }
    /**
     * get StartIndex
     * @return the StartIndex
     */
    public int getStartIndex() { return mStartIndex; }

    //----------------------------------------
    // StartIndexSet
    private boolean mStartIndexSet;
    /**
     * set StartIndexSet
     * @param pStartIndexSet the StartIndexSet
     */
    public void setStartIndexSet(boolean pStartIndexSet) { mStartIndexSet = pStartIndexSet; }
    /**
     * get StartIndexSet
     * @return the StartIndexSet
     */
    public boolean isStartIndexSet() { return mStartIndexSet; }

    //----------------------------------------
    // RangeCount
    private int mRangeCount;
    /**
     * set RangeCount
     * @param pRangeCount the RangeCount
     */
    public void setRangeCount(int pRangeCount) 
    { 
	mRangeCount = pRangeCount; 
	setRangeCountSet(true);
    }
    /**
     * get RangeCount
     * @return the RangeCount
     */
    public int getRangeCount() { return mRangeCount; }

    //----------------------------------------
    // RangeCountSet
    private boolean mRangeCountSet;
    /**
     * set RangeCountSet
     * @param pRangeCountSet the RangeCountSet
     */
    public void setRangeCountSet(boolean pRangeCountSet) { mRangeCountSet = pRangeCountSet; }
    /**
     * get RangeCountSet
     * @return the RangeCountSet
     */
    public boolean isRangeCountSet() { return mRangeCountSet; }

    //----------------------------------------
    // EndIndex
    private int mEndIndex;
    /**
     * set EndIndex
     * @param pEndIndex the EndIndex
     */
    public void setEndIndex(int pEndIndex) 
    { 
	mEndIndex = pEndIndex; 
	setEndIndexSet(true);
    }
    /**
     * get EndIndex
     * @return the EndIndex
     */
    public int getEndIndex() { return mEndIndex; }

    //----------------------------------------
    // EndIndexSet
    private boolean mEndIndexSet;
    /**
     * set EndIndexSet
     * @param pEndIndexSet the EndIndexSet
     */
    public void setEndIndexSet(boolean pEndIndexSet) { mEndIndexSet = pEndIndexSet; }
    /**
     * get EndIndexSet
     * @return the EndIndexSet
     */
    public boolean isEndIndexSet() { return mEndIndexSet; }

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
    // KeyCastClass
    private String mKeyCastClass;
    /**
     * set KeyCastClass
     * @param pKeyCastClass the KeyCastClass
     */
    public void setKeyCastClass(String pKeyCastClass) { mKeyCastClass = pKeyCastClass; }
    /**
     * get KeyCastClass
     * @return the KeyCastClass
     */
    public String getKeyCastClass() { return mKeyCastClass; }

    //----------------------------------------
    // ElementId
    private String mElementId;
    /**
     * set ElementId
     * @param pElementId the ElementId
     */
    public void setElementId(String pElementId) { mElementId = pElementId; }
    /**
     * get ElementId
     * @return the ElementId
     */
    public String getElementId() { return mElementId; }

    //----------------------------------------
    // KeyId
    private String mKeyId;
    /**
     * set KeyId
     * @param pKeyId the KeyId
     */
    public void setKeyId(String pKeyId) { mKeyId = pKeyId; }
    /**
     * get KeyId
     * @return the KeyId
     */
    public String getKeyId() { return mKeyId; }

    //----------------------------------------
    // Index
    private int mIndex;
    /**
     * set Index
     * @param pIndex the Index
     */
    public void setIndex(int pIndex) { mIndex = pIndex; }
    /**
     * get Index
     * @return the Index
     */
    public int getIndex() { return mIndex; }

    //----------------------------------------
    // Count
    private int mCount;
    /**
     * set Count
     * @param pCount the Count
     */
    public void setCount(int pCount) { mCount = pCount; }
    /**
     * get Count
     * @return the Count
     */
    public int getCount() { return mCount; }

    //----------------------------------------
    // Key
    private Object mKey;
    /**
     * set Key
     * @param pKey the Key
     */
    public void setKey(Object pKey) { mKey = pKey; }
    /**
     * get Key
     * @return the Key
     */
    public Object getKey() { return mKey; }

    //----------------------------------------
    // Element
    private Object mElement;
    /**
     * set Element
     * @param pElement the Element
     */
    public void setElement(Object pElement) { mElement = pElement; }
    /**
     * get Element
     * @return the Element
     */
    public Object getElement() { return mElement; }

    //----------------------------------------
    // BooleanElement
    private boolean mBooleanElement;
    /**
     * set BooleanElement
     * @param pBooleanElement the BooleanElement
     */
    public void setBooleanElement(boolean pBooleanElement) { mBooleanElement = pBooleanElement; }
    /**
     * get BooleanElement
     * @return the BooleanElement
     */
    public boolean isBooleanElement() { return mBooleanElement; }

    //----------------------------------------
    // ByteElement
    private byte mByteElement;
    /**
     * set ByteElement
     * @param pByteElement the ByteElement
     */
    public void setByteElement(byte pByteElement) { mByteElement = pByteElement; }
    /**
     * get ByteElement
     * @return the ByteElement
     */
    public byte getByteElement() { return mByteElement; }

    //----------------------------------------
    // CharElement
    private char mCharElement;
    /**
     * set CharElement
     * @param pCharElement the CharElement
     */
    public void setCharElement(char pCharElement) { mCharElement = pCharElement; }
    /**
     * get CharElement
     * @return the CharElement
     */
    public char getCharElement() { return mCharElement; }

    //----------------------------------------
    // DoubleElement
    private double mDoubleElement;
    /**
     * set DoubleElement
     * @param pDoubleElement the DoubleElement
     */
    public void setDoubleElement(double pDoubleElement) { mDoubleElement = pDoubleElement; }
    /**
     * get DoubleElement
     * @return the DoubleElement
     */
    public double getDoubleElement() { return mDoubleElement; }

    //----------------------------------------
    // FloatElement
    private float mFloatElement;
    /**
     * set FloatElement
     * @param pFloatElement the FloatElement
     */
    public void setFloatElement(float pFloatElement) { mFloatElement = pFloatElement; }
    /**
     * get FloatElement
     * @return the FloatElement
     */
    public float getFloatElement() { return mFloatElement; }

    //----------------------------------------
    // IntElement
    private int mIntElement;
    /**
     * set IntElement
     * @param pIntElement the IntElement
     */
    public void setIntElement(int pIntElement) { mIntElement = pIntElement; }
    /**
     * get IntElement
     * @return the IntElement
     */
    public int getIntElement() { return mIntElement; }

    //----------------------------------------
    // LongElement
    private long mLongElement;
    /**
     * set LongElement
     * @param pLongElement the LongElement
     */
    public void setLongElement(long pLongElement) { mLongElement = pLongElement; }
    /**
     * get LongElement
     * @return the LongElement
     */
    public long getLongElement() { return mLongElement; }

    //----------------------------------------
    // ShortElement
    private short mShortElement;
    /**
     * set ShortElement
     * @param pShortElement the ShortElement
     */
    public void setShortElement(short pShortElement) { mShortElement = pShortElement; }
    /**
     * get ShortElement
     * @return the ShortElement
     */
    public short getShortElement() { return mShortElement; }

    //----------------------------------------
    // Size
    private int mSize;
    /**
     * set Size
     * @param pSize the Size
     */
    public void setSize(int pSize) { mSize = pSize; }
    /**
     * get Size
     * @return the Size
     */
    public int getSize() { return mSize; }

    //----------------------------------------
    // First
    private boolean mFirst;
    /**
     * set First
     * @param pFirst the First
     */
    public void setFirst(boolean pFirst) { mFirst = pFirst; }
    /**
     * get First
     * @return the First
     */
    public boolean isFirst() { return mFirst; }

    //----------------------------------------
    // Last
    private boolean mLast;
    /**
     * set Last
     * @param pLast the Last
     */
    public void setLast(boolean pLast) { mLast = pLast; }
    /**
     * get Last
     * @return the Last
     */
    public boolean isLast() { return mLast; }

    //----------------------------------------
    // ObjectArray
    private Object[] mObjectArray;
    /**
     * set ObjectArray
     * @param pObjectArray the ObjectArray
     */
    public void setObjectArray(Object[] pObjectArray) { mObjectArray = pObjectArray; }
    /**
     * get ObjectArray
     * @return the ObjectArray
     */
    public Object[] getObjectArray() { return mObjectArray; }

    //----------------------------------------
    // Iterator
    private Iterator mIterator;
    /**
     * set Iterator
     * @param pIterator the Iterator
     */
    public void setIterator(Iterator pIterator) { mIterator = pIterator; }
    /**
     * get Iterator
     * @return the Iterator
     */
    public Iterator getIterator() { return mIterator; }

    //----------------------------------------
    // Enumeration
    private Enumeration mEnumeration;
    /**
     * set Enumeration
     * @param pEnumeration the Enumeration
     */
    public void setEnumeration(Enumeration pEnumeration) { mEnumeration = pEnumeration; }
    /**
     * get Enumeration
     * @return the Enumeration
     */
    public Enumeration getEnumeration() { return mEnumeration; }

    //----------------------------------------
    // Map
    private Map mMap;
    /**
     * set Map
     * @param pMap the Map
     */
    public void setMap(Map pMap) { mMap = pMap; }
    /**
     * get Map
     * @return the Map
     */
    public Map getMap() { return mMap; }

    //----------------------------------------
    // BooleanArray
    private boolean[] mBooleanArray;
    /**
     * set BooleanArray
     * @param pBooleanArray the BooleanArray
     */
    public void setBooleanArray(boolean[] pBooleanArray) { mBooleanArray = pBooleanArray; }
    /**
     * get BooleanArray
     * @return the BooleanArray
     */
    public boolean[] getBooleanArray() { return mBooleanArray; }

    //----------------------------------------
    // ByteArray
    private byte[] mByteArray;
    /**
     * set ByteArray
     * @param pByteArray the ByteArray
     */
    public void setByteArray(byte[] pByteArray) { mByteArray = pByteArray; }
    /**
     * get ByteArray
     * @return the ByteArray
     */
    public byte[] getByteArray() { return mByteArray; }

    //----------------------------------------
    // CharArray
    private char[] mCharArray;
    /**
     * set CharArray
     * @param pCharArray the CharArray
     */
    public void setCharArray(char[] pCharArray) { mCharArray = pCharArray; }
    /**
     * get CharArray
     * @return the CharArray
     */
    public char[] getCharArray() { return mCharArray; }

    //----------------------------------------
    // ShortArray
    private short[] mShortArray;
    /**
     * set ShortArray
     * @param pShortArray the ShortArray
     */
    public void setShortArray(short[] pShortArray) { mShortArray = pShortArray; }
    /**
     * get ShortArray
     * @return the ShortArray
     */
    public short[] getShortArray() { return mShortArray; }

    //----------------------------------------
    // IntArray
    private int[] mIntArray;
    /**
     * set IntArray
     * @param pIntArray the IntArray
     */
    public void setIntArray(int[] pIntArray) { mIntArray = pIntArray; }
    /**
     * get IntArray
     * @return the IntArray
     */
    public int[] getIntArray() { return mIntArray; }

    //----------------------------------------
    // LongArray
    private long[] mLongArray;
    /**
     * set LongArray
     * @param pLongArray the LongArray
     */
    public void setLongArray(long[] pLongArray) { mLongArray = pLongArray; }
    /**
     * get LongArray
     * @return the LongArray
     */
    public long[] getLongArray() { return mLongArray; }

    //----------------------------------------
    // FloatArray
    private float[] mFloatArray;
    /**
     * set FloatArray
     * @param pFloatArray the FloatArray
     */
    public void setFloatArray(float[] pFloatArray) { mFloatArray = pFloatArray; }
    /**
     * get FloatArray
     * @return the FloatArray
     */
    public float[] getFloatArray() { return mFloatArray; }

    //----------------------------------------
    // DoubleArray
    private double[] mDoubleArray;
    /**
     * set DoubleArray
     * @param pDoubleArray the DoubleArray
     */
    public void setDoubleArray(double[] pDoubleArray) { mDoubleArray = pDoubleArray; }
    /**
     * get DoubleArray
     * @return the DoubleArray
     */
    public double[] getDoubleArray() { return mDoubleArray; }

    //----------------------------------------
    // ValuesType
    private byte mValuesType;
    /**
     * set ValuesType
     * @param pValuesType the ValuesType
     */
    public void setValuesType(byte pValuesType) { mValuesType = pValuesType; }
    /**
     * get ValuesType
     * @return the ValuesType
     */
    public byte getValuesType() { return mValuesType; }

    //----------------------------------------
    // InvalidAttributes
    private boolean mInvalidAttributes;
    /**
     * set InvalidAttributes
     * @param pInvalidAttributes the InvalidAttributes
     */
    public void setInvalidAttributes(boolean pInvalidAttributes) { mInvalidAttributes = pInvalidAttributes; }
    /**
     * get InvalidAttributes
     * @return the InvalidAttributes
     */
    public boolean isInvalidAttributes() { return mInvalidAttributes; }

    //----------------------------------------
    // IgnoreEndIndex
    private boolean mIgnoreEndIndex;
    /**
     * set IgnoreEndIndex
     * @param pIgnoreEndIndex the IgnoreEndIndex
     */
    public void setIgnoreEndIndex(boolean pIgnoreEndIndex) { mIgnoreEndIndex = pIgnoreEndIndex; }
    /**
     * get IgnoreEndIndex
     * @return the IgnoreEndIndex
     */
    public boolean isIgnoreEndIndex() { return mIgnoreEndIndex; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof ForEachTag
     */
    public ForEachTag()
    {
	mCounter = 0;
    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * examine the attributes to make sure they are valid
     */
    protected boolean invalidAttributes()
    {
	if(getValues() == null) {
	    setInvalidAttributes(true);
	    return true;
	}

	if(isRangeCountSet() && mRangeCount < 0) {
	    setInvalidAttributes(true);
	    return true;
	}

	if(getStep() == 0) {
	    setInvalidAttributes(true);
	    return true;
	}

	if(getStep() < 0 && 
	   (getValues() instanceof Iterator ||
	    getValues() instanceof Enumeration)) {

	    setInvalidAttributes(true);
	    return true;
	}

	if(isStartIndexSet() && getStartIndex() < 0) {
	    setInvalidAttributes(true);
	    return true;
	}

	if(isEndIndexSet() && getEndIndex() < 0) {
	    setInvalidAttributes(true);
	    return true;
	}

	// make sure the range attributes are legal
	if(isStartIndexSet() && isEndIndexSet()) {
	    if(getStep() < 0 && 
	       getStartIndex() < getEndIndex()) {
		setInvalidAttributes(true);
		return true;
	    }

	    if(getStep() > 0 &&
	       getStartIndex() > getEndIndex()) {
		setInvalidAttributes(true);
		return true;
	    }
	}

	// make sure the correct range attributes are being used
	// for iterators and enumerations
	/*
	if(!isStartIndexSet() && 
	   isRangeCountSet() && 
	   isEndIndexSet() &&
	   (getValues() instanceof Iterator ||
	    getValues() instanceof Enumeration))
	    return true;
	*/

	return false;
    }

    //----------------------------------------
    /**
     * determine the first index and last indices to be used
     */
    protected void normalizeIndices()
    {
	// start index hasn't been set, we need to set it
	if(isStartIndexSet() == false) {
	    
	    // end index and range count set, set startIndex to 
	    // difference between these two values
	    if(isEndIndexSet() && isRangeCountSet()) {
		if(getStep() > 0)
		    mStartIndex = getEndIndex() - (getRangeCount() * getStep()) + 1;
		if(getStep() < 0)
		    mStartIndex = getEndIndex() - (getRangeCount() * getStep()) - 1;
	    }	
	    else {
		// if stepping forwards
		if(getStep() > 0)
		    mStartIndex = 0;
		// if stepping backwards
		if(getStep() < 0)
		    mStartIndex = getSize() - 1;
	    }
	}

	// end index hasn't been set, we need to set it
	if(isEndIndexSet() == false) {
	    
	    // start index and range count set, set endIndex to 
	    // difference between these two values
	    if(isRangeCountSet()) {
		// if stepping forwards
		if(getStep() > 0)
		    mEndIndex = getStartIndex() + (getRangeCount() * getStep()) - 1;

		// if stepping backwards
		if(getStep() < 0)
		    mEndIndex = getStartIndex() + (getRangeCount() * getStep()) + 1;
	    }
	    // just start index set, set endIndex to the max value or to 0
	    else {
		if(getValuesType() == kIteratorValuesType ||
		   getValuesType() == kEnumerationValuesType) {
		    setIgnoreEndIndex(true);
		}
		// if stepping forwards
		else if(getStep() > 0)
		    mEndIndex = getSize() - 1;
		
		// if stepping backwards
		else if(getStep() < 0)
		    mEndIndex = 0;
	    }
	}

	// if, due to a range count being set, the endIndex is actually beyond
	// the last item in the values attribute, set the endIndex to 
	// the last item in the values attribute.
	if((getValuesType() != kIteratorValuesType) &&
	   (getValuesType() != kEnumerationValuesType) &&
	   (getStep() > 0) && 
	   (getEndIndex() > (getSize() - 1)))
	    mEndIndex = getSize() - 1;
	else if((getStep() < 0) && 
		(getEndIndex() < 0))
	    mEndIndex = 0;

	// if, due to a range count being set, the startIndex is actually before
	// the first item in the values attribute, set the startIndex to firstItem
	// in the values attribute
	if((getValuesType() != kIteratorValuesType) &&
	   (getValuesType() != kEnumerationValuesType) &&
	   (getStep() > 0) && 
	   (getStartIndex() < 0))
	    mStartIndex = 0;
	else if((getStep() < 0) && 
		(getStartIndex() >= (getSize() - 1)))
	    mStartIndex = getSize() - 1;
    }

    //----------------------------------------
    /**
     * set the element using the current counter value
     */
    protected boolean setElementUsingCounter()
    {
	boolean error = false;

	// if the start index is after the last element, return true,
	// as in, yes there is a problem
	if(getSize() != -1 && (getStartIndex() >= getSize())) {
	    return true;
	}

	// if the rangeCount is zero then we shouldn't render anything
	if(isRangeCountSet() && getRangeCount() == 0)
	    return true;

	// if negative step and endIndex >= size, we shouldn't render anything
	if(getSize() != -1 && getStep() < 0 && getEndIndex() >= getSize())
	    return true;

	// set the element to current counter
	// iterator and enumeration cases only get evaluated first time
	// all other times they are processed in the doAfterBody method
	// as a special case
	switch(getValuesType()) {
	case kObjectArrayValuesType:
	    setElement(getObjectArray()[mCounter]);
	    break;
	case kMapValuesType:
	    setKey(getObjectArray()[mCounter]);
	    setElement(getMap().get(getKey()));
	    break;
	case kBooleanArrayValuesType:
	    setBooleanElement(getBooleanArray()[mCounter]);
        setElement( new Boolean(getBooleanArray()[mCounter]) );
	    break;
	case kByteArrayValuesType:
	    setByteElement(getByteArray()[mCounter]);
        setElement( new Byte(getByteArray()[mCounter]) );
	    break;
	case kShortArrayValuesType:
	    setShortElement(getShortArray()[mCounter]);
        setElement( new Short(getShortArray()[mCounter]) );
	    break;
	case kCharArrayValuesType:
	    setCharElement(getCharArray()[mCounter]);
        setElement( new Character(getCharArray()[mCounter]) );
	    break;
	case kIntArrayValuesType:
	    setIntElement(getIntArray()[mCounter]);
        setElement( new Integer(getIntArray()[mCounter]) );
	    break;
	case kLongArrayValuesType:
	    setLongElement(getLongArray()[mCounter]);
        setElement( new Long(getLongArray()[mCounter]) );
	    break;
	case kFloatArrayValuesType:
	    setFloatElement(getFloatArray()[mCounter]);
        setElement( new Float(getFloatArray()[mCounter]) );
	    break;
	case kDoubleArrayValuesType:
	    setDoubleElement(getDoubleArray()[mCounter]);
        setElement( new Double(getDoubleArray()[mCounter]) );
	    break;

    // NEXT TWO VALUES, SPECIAL CASE: For kIteratorValuesType and
    // kEnumerationValuesType, this is only called during doInitBody.

    // NEW LOOP INVARIANT:  After doInitBody is called (and hence
    // setElement, for the first time) the Iterator (or Enumeration) is
    // always kept in a state such that Iterator.next (or
    // Enumeration.nextElement) will yield the next element needed, even if
    // getStep()!=1.  In other words, if getStep>1, then we advance the
    // iterator.  This is so that we can call setLast appropriately.
	case kIteratorValuesType:
	    // first move to start index, and do setElement
	    int i;
        for(i=0; i <= getStartIndex() && getIterator().hasNext(); i++) {
            setElement(getIterator().next());
        }
        if(i <= getStartIndex()) {
            error = true;
	    }
        // then move iterator to loop invariant position.
        for (i=0; i < getStep()-1; i++) {
            if ( getIterator().hasNext() ) {
                getIterator().next();
            }
        }
        if ( !getIterator().hasNext() ) {
            setLast(true);
        }
	    break;
	case kEnumerationValuesType:
	    // first move to start index, and do setElement
        int j;
        for(j=0; j <= getStartIndex() && getEnumeration().hasMoreElements(); j++) {
            setElement(getEnumeration().nextElement());
        }
        if(j <= getStartIndex())
            error = true;
        // then move enumeration to loop invariant position
        for (i=0; i < getStep()-1; i++) {
            if ( getEnumeration().hasMoreElements() ) {
                getEnumeration().nextElement();
            }
        }
        if ( !getEnumeration().hasMoreElements() ) {
            setLast(true);
        }
	    break;
	}

	return error;
    }

    //----------------------------------------
    /**
     * start the body
     */
    public int doStartTag()
	throws JspException
    {
	int returnValue = EVAL_BODY_INCLUDE;

	// if the step hasn't been set, default to 1
	if(mStepSet == false)
	    setStep(1);

	if(invalidAttributes()) {
	    returnValue = SKIP_BODY;
	}
	else {
	    // determine the type of the values property
	    if(getValues() instanceof Collection) {
		setObjectArray(((Collection) getValues()).toArray());
		setValuesType(kObjectArrayValuesType);
		setSize(getObjectArray().length);
	    }
	    else if(getValues() instanceof Object[]) {
		setObjectArray((Object[]) getValues());
		setValuesType(kObjectArrayValuesType);
		setSize(getObjectArray().length);
	    }
	    else if(getValues() instanceof Iterator) {
		setIterator((Iterator) getValues());
		setValuesType(kIteratorValuesType);
		setSize(-1);
	    }
	    else if(getValues() instanceof Enumeration) {
		setEnumeration((Enumeration) getValues());
		setValuesType(kEnumerationValuesType);
		setSize(-1);
	    }
	    else if(getValues() instanceof Map) {
		setMap((Map) getValues());
		setObjectArray((Object[]) getMap().keySet().toArray());
		setValuesType(kMapValuesType);
		setSize(getObjectArray().length);
	    }
	    else if(getValues() instanceof boolean[]) {
		setBooleanArray((boolean[]) getValues());
		setValuesType(kBooleanArrayValuesType);
		setSize(getBooleanArray().length);
	    }
	    else if(getValues() instanceof byte[]) {
		setByteArray((byte[]) getValues());
		setValuesType(kByteArrayValuesType);
		setSize(getByteArray().length);
	    }
	    else if(getValues() instanceof char[]) {
		setCharArray((char[]) getValues());
		setValuesType(kCharArrayValuesType);
		setSize(getCharArray().length);
	    }
	    else if(getValues() instanceof short[]) {
		setShortArray((short[]) getValues());
		setValuesType(kShortArrayValuesType);
		setSize(getShortArray().length);
	    }
	    else if(getValues() instanceof int[]) {
		setIntArray((int[]) getValues());
		setValuesType(kIntArrayValuesType);
		setSize(getIntArray().length);
	    }
	    else if(getValues() instanceof long[]) {
		setLongArray((long[]) getValues());
		setValuesType(kLongArrayValuesType);
		setSize(getLongArray().length);
	    }
	    else if(getValues() instanceof float[]) {
		setFloatArray((float[]) getValues());
		setValuesType(kFloatArrayValuesType);
		setSize(getFloatArray().length);
	    }
	    else if(getValues() instanceof double[]) {
		setDoubleArray((double[]) getValues());
		setValuesType(kDoubleArrayValuesType);
		setSize(getDoubleArray().length);
	    }
	    else {
		setValuesType(kInvalidValuesType);
	    }
	}

	if(getSize() == 0)
	    returnValue = SKIP_BODY;

	// set this tag as an attribute in its own body
	getPageContext().setAttribute(getId(), this);

	if(returnValue != SKIP_BODY) {
	    normalizeIndices();

	    setIndex(mCounter);
	    setCount(getIndex() + 1);
	    setFirst(true);

	    if(getValuesType() != kIteratorValuesType &&
	       getValuesType() != kEnumerationValuesType) {
		if((getSize() == 1) || (getStep() >= getSize()))
		    setLast(true);
	    }

	    mCounter = getStartIndex();

	    // set the current element to the first item
	    boolean errorSettingFirstElement = setElementUsingCounter();

	    if(errorSettingFirstElement) {
		returnValue = SKIP_BODY;
	    }
	}
	else {
	    mElement = null;
	    mKey = null;
	}

	// if casting is needed, set the cast object in the page context
	if(getCastClass() != null && getElementId() != null && getElement() != null) {
	    getPageContext().setAttribute(getElementId(), getElement());
	}
	if(getKeyCastClass() != null && getKeyId() != null && getKey() != null)
	    getPageContext().setAttribute(getKeyId(), getKey());

	return returnValue;
    }

    //----------------------------------------
    /**
     * init the body
     */
    public void doInitBody()
	throws JspException
    {
    }

    //----------------------------------------
    /**
     * do after the body
     */
    public int doAfterBody()
	throws JspException
    {
	// test to see if we are done looping, if not done, increment
	// the element by the correct step amount
	mCounter += getStep();

	switch(getValuesType()) {
	case kInvalidValuesType:
	    return SKIP_BODY;
	case kIteratorValuesType:
        if(!getIterator().hasNext() ||
           (!isIgnoreEndIndex() && mCounter > getEndIndex()))
            return SKIP_BODY;
        else {
            setElement(getIterator().next());
            int i;
            for(i=0; i < getStep()-1 && getIterator().hasNext(); i++) {
                getIterator().next();
            }
            if ( !getIterator().hasNext() ) {
                setLast(true);
            }
        }
        break;
    case kEnumerationValuesType:
        if(!getEnumeration().hasMoreElements() ||
           (!isIgnoreEndIndex() && mCounter > getEndIndex()))
            return SKIP_BODY;
        else {
            setElement(getEnumeration().nextElement());
            int i;
            for(i=0; i < getStep()-1 && getEnumeration().hasMoreElements(); i++) {
                getEnumeration().nextElement();
            }
            if ( !getEnumeration().hasMoreElements() ) {
                setLast(true);
            }
        }
        break;
    case kMapValuesType:
	case kBooleanArrayValuesType:
	case kByteArrayValuesType:
	case kCharArrayValuesType:
	case kShortArrayValuesType:
	case kIntArrayValuesType:
	case kLongArrayValuesType:
	case kFloatArrayValuesType:
	case kDoubleArrayValuesType:
	case kObjectArrayValuesType:
	    if(getStep() > 0) {
		if(mCounter > getEndIndex())
		    return SKIP_BODY;
	    }
	    else if(getStep() < 0) {
		if(mCounter < getEndIndex())
		    return SKIP_BODY;
	    }
	    break;
	}

	// set the element(s) to the current counter value if the
	// values attribute is not of type Iterator or Enumeration since
	// they have already been set in the previous step
	if(getValuesType() != kIteratorValuesType &&
	   getValuesType() != kEnumerationValuesType) {
	    setElementUsingCounter();
	}

	// update all the various accounting attributes
	setFirst(false);
	setIndex(getIndex() + 1);
	setCount(getCount() + 1);

    // MJL WORKHERE
	if(!mIgnoreEndIndex) {
	    if( (getStep()==0 && mCounter == getEndIndex())
            || (getStep()>0 && getEndIndex() - mCounter < getStep() )
            || (getStep()<0 && mCounter - getEndIndex() < -1 * getStep() ) ) {
            setLast(true);
        }
	}
    else if ( getValuesType() == kIteratorValuesType ) {
        if ( !getIterator().hasNext() ) {
            setLast(true);
        }
    }
    else if ( getValuesType() == kEnumerationValuesType ) {
        if ( !getEnumeration().hasMoreElements() ) {
            setLast(true);
        }
    }

	// if casting, set the cast object in the page context
	if(getCastClass() != null && getElementId() != null) {
          if ( getElement() == null ) {
            getPageContext().removeAttribute( getElementId() );
          }
          else {
	    getPageContext().setAttribute(getElementId(), getElement());
          }
        }
        if(getKeyCastClass() != null && getKeyId() != null && getKey() != null) 
          getPageContext().setAttribute(getKeyId(), getKey());

	return EVAL_BODY_AGAIN;
    }

    //----------------------------------------
    /**
     * do end tag cleanup
     */
    public int doEndTag()
	throws JspException
    {
	mCounter = 0;
	setIndex(0);
	setCount(0);
	setKey(null);
	setElement(null);

	setBooleanElement(false);
	setByteElement((byte) 0);
	setCharElement((char) 0);
	setDoubleElement((double) 0);
	setFloatElement((float) 0);
	setIntElement(0);
	setLongElement((long) 0);
	setShortElement((short) 0);

	setFirst(false);
	setLast(false);

	setObjectArray(null);
	setIterator(null);
	setEnumeration(null);
	setMap(null);
	setBooleanArray(null);
	setByteArray(null);
	setCharArray(null);
	setShortArray(null);
	setIntArray(null);
	setLongArray(null);
	setFloatArray(null);
	setDoubleArray(null);

	setInvalidAttributes(false);
	setIgnoreEndIndex(false);

	setSize(0);

	setValuesType(kInvalidValuesType);

	return EVAL_PAGE;
    }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	super.release();

	try {
	    doEndTag();
	}
	catch(JspException e) {}

	setId(null);
	setValues(null);
	setStep(0);
	mStepSet = false;
	setStartIndexSet(false);
	setEndIndexSet(false);
	setRangeCountSet(false);
	setCastClass(null);
	setElementId(null);
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
