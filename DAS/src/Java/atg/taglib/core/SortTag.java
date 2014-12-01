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
 * The core:Sort tag takes an input in the form of an array of
 * Objects/primitives, Collection, Iterator, Enumeration or Map,
 * sorts it and exports the resulting sorted array of Objects/primitives,
 * Collection, Iterator, Enumeration or Map. The
 * 
 * to ascending by value of the object, otherwise the sorting is done in
 * <p>
 * By default, the values are sorted into an
 * object of the same type as the values themselves. For instance, if the
 * values attribute is a Collection, the resulting sorted values would be
 * in a Collection, and would be accessible in the body of the tag through
 * the sortedCollection property of the tag. The same applies to
 * primitive arrays. If the values attribute is an array of longs, then
 * the resulting sorted values would be accessible in the body of the tag
 * through the sortedLongArray property of the tag.
 * <p>
 * The resulting sorted values may be retrieved in a form other than that
 * of the original. For instance, if the set of values to be sorted was
 * passed in as a Collection, one could access the sorted values through
 * the sortedIterator property. 
 * <p>
 * If one tries to retrieve a sorted array
 * of primitives as either a Collection, Iterator, Enumeration,
 * Map or Object array, then the values in these objects will
 * be the Object versions of the primitives. For instance if one sets the
 * values attribute to an array of int primitives and then gets the
 * sortedCollection property of the tag, the Collection will contain
 * Integer objects corresponding to the int primitives. 
 * <p>
 * If one tries to get a sorted Collection, Iterator, Enumeration, Object
 * array or primitive array as a sorted Map, then the keys of the Map
 * will contain the Objects in the original set of values, or  
 * the primitives in Object form in the case of an array of primitives. 
 * <p>
 * If one tries to get a sorted Map as a sorted Collection,
 * Iterator, Enumeration, or Object array, then the sorted Collection,
 * Iterator, Enumeration, or Object array will contain the keys of
 * the Map.
 * <p>
 * If one tries to get a sorted Collection, Iterator, Enumeration, Object
 * array or Map as a primitive array, the primitive array
 * will be null.
 * <p>
 * If the values attribute is set to an array of primitives, then one can
 * also retrieve the sorted primitives in a wider or narrower form. For
 * instance if the values attribute is set to an array of int primitives,
 * then one could access the sorted values through the sortedShortArray
 * property, or through the sortedLongArray property. Getting the sorted
 * values in a narrower form will result in value truncation.
 * <p>
 * Getting sorted values in a form other than that of the original values
 * attribute will always have some sort of performance cost associated
 * with it.
 * <p>
 * example:
 * <p>
 * <code>
 * <pre>
 * &lt;core:Sort id="sorter" values="&lt;%= foo.getBar() %&gt;"&gt;
 * 
 *   &lt;core:OrderBy property="lastName"/&gt;
 *   &lt;core:OrderByReverse property="firstName"/&gt;
 * 
 *   &lt;ol&gt;
 *   &lt;core:ForEach id="people" values="&lt;%= sorter.getSortedArray() %&gt;"&gt;
 *     &lt;li&gt;Name: &lt;%= ((Person) people.getElement()).getLastName() %&gt;
 *   &lt;/core:ForEach&gt;
 *   &lt;/ol&gt;
 * &lt;/core:Sort&gt;
 * </pre>
 * </code>
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/SortTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class SortTag
    extends GenericTag
    implements TagAttributeTypes
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/SortTag.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    // flag indicating whether or not a sort needs to be performed
    // before any of the sorted properties may be accessed.    
    private boolean mSorted;

    // flags indicating whether non-primitive values types
    // contain the most recently sorted values
    private boolean mObjectArrayInSync;
    private boolean mCollectionInSync;
    private boolean mIteratorInSync;
    private boolean mEnumerationInSync;
    private boolean mMapInSync;

    // flags indicating whether or not primitive types
    // contain sorted values narrowed or widened from 
    // other boolean types
    private boolean mByteArrayInSync;
    private boolean mCharArrayInSync;
    private boolean mShortArrayInSync;
    private boolean mIntArrayInSync;
    private boolean mLongArrayInSync;
    private boolean mFloatArrayInSync;
    private boolean mDoubleArrayInSync;

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // Orderings
    private Vector mOrderings;
    /**
     * get Orderings
     * @return the Orderings
     */
    public Vector getOrderings() { return mOrderings; }

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
    // Reverse
    private boolean mReverse;
    /**
     * set Reverse
     * @param pReverse the Reverse
     */
    public void setReverse(boolean pReverse) { mReverse = pReverse; }
    public void setReverse(String pReverse) { mReverse = Boolean.valueOf(pReverse).booleanValue(); }
    /**
     * get Reverse
     * @return the Reverse
     */
    public boolean isReverse() { return mReverse; }

    //----------------------------------------
    // SortedArray
    private Object[] mSortedArray;
    /**
     * set SortedArray
     * @param pSortedArray the SortedArray
     */
    public void setSortedArray(Object[] pSortedArray) { mSortedArray = pSortedArray; }
    /**
     * get SortedArray
     * @return the SortedArray
     */
    public Object[] getSortedArray() 
    { 
	if(mSorted == false)
	    sort();

	if(mObjectArrayInSync == false)
	    translateValuesType(kObjectArrayValuesType);

	return mSortedArray; 
    }

    //----------------------------------------
    // SortedCollection
    private Collection mSortedCollection;
    /**
     * set SortedCollection
     * @param pSortedCollection the SortedCollection
     */
    public void setSortedCollection(Collection pSortedCollection) { mSortedCollection = pSortedCollection; }
    /**
     * get SortedCollection
     * @return the SortedCollection
     */
    public Collection getSortedCollection() 
    { 
	if(mSorted == false)
	    sort();

	if(mCollectionInSync == false)
	    translateValuesType(kCollectionValuesType);

	return mSortedCollection; 
    }

    //----------------------------------------
    // SortedMap
    private Map mSortedMap;
    /**
     * set SortedMap
     * @param pSortedMap the SortedMap
     */
    public void setSortedMap(Map pSortedMap) { mSortedMap = pSortedMap; }
    /**
     * get SortedMap
     * @return the SortedMap
     */
    public Map getSortedMap() 
    { 
	if(mSorted == false)
	    sort();

	if(mMapInSync == false)
	    translateValuesType(kMapValuesType);

	return mSortedMap; 
    }

    //----------------------------------------
    // SortedEnumeration
    private Enumeration mSortedEnumeration;
    /**
     * set SortedEnumeration
     * @param pSortedEnumeration the SortedEnumeration
     */
    public void setSortedEnumeration(Enumeration pSortedEnumeration) { mSortedEnumeration = pSortedEnumeration; }
    /**
     * get SortedEnumeration
     * @return the SortedEnumeration
     */
    public Enumeration getSortedEnumeration() 
    { 
	if(mSorted == false)
	    sort();

	if(mEnumerationInSync == false)
	    translateValuesType(kEnumerationValuesType);

	return mSortedEnumeration; 
    }

    //----------------------------------------
    // SortedIterator
    private Iterator mSortedIterator;
    /**
     * set SortedIterator
     * @param pSortedIterator the SortedIterator
     */
    public void setSortedIterator(Iterator pSortedIterator) { mSortedIterator = pSortedIterator; }
    /**
     * get SortedIterator
     * @return the SortedIterator
     */
    public Iterator getSortedIterator() 
    { 
	if(mSorted == false)
	    sort();

	if(mIteratorInSync == false) {
	    translateValuesType(kIteratorValuesType);
	}

	return mSortedIterator; 
    }

    //----------------------------------------
    // SortedBooleanArray
    private boolean[] mSortedBooleanArray;
    /**
     * set SortedBooleanArray
     * @param pSortedBooleanArray the SortedBooleanArray
     */
    public void setSortedBooleanArray(boolean[] pSortedBooleanArray) { mSortedBooleanArray = pSortedBooleanArray; }
    /**
     * get SortedBooleanArray
     * @return the SortedBooleanArray
     */
    public boolean[] getSortedBooleanArray() 
    { 
	if(!mSorted)
	    sort();

	return mSortedBooleanArray; 
    }

    //----------------------------------------
    // SortedByteArray
    private byte[] mSortedByteArray;
    /**
     * set SortedByteArray
     * @param pSortedByteArray the SortedByteArray
     */
    public void setSortedByteArray(byte[] pSortedByteArray) { mSortedByteArray = pSortedByteArray; }
    /**
     * get SortedByteArray
     * @return the SortedByteArray
     */
    public byte[] getSortedByteArray() 
    { 
	if(!mSorted)
	    sort();

	if(mByteArrayInSync == false)
	    translateValuesType(kByteArrayValuesType);

	return mSortedByteArray; 
    }

    //----------------------------------------
    // SortedCharArray
    private char[] mSortedCharArray;
    /**
     * set SortedCharArray
     * @param pSortedCharArray the SortedCharArray
     */
    public void setSortedCharArray(char[] pSortedCharArray) { mSortedCharArray = pSortedCharArray; }
    /**
     * get SortedCharArray
     * @return the SortedCharArray
     */
    public char[] getSortedCharArray() 
    { 
	if(!mSorted)
	    sort();

	if(mCharArrayInSync == false)
	    translateValuesType(kCharArrayValuesType);

	return mSortedCharArray; 
    }

    //----------------------------------------
    // SortedShortArray
    private short[] mSortedShortArray;
    /**
     * set SortedShortArray
     * @param pSortedShortArray the SortedShortArray
     */
    public void setSortedShortArray(short[] pSortedShortArray) { mSortedShortArray = pSortedShortArray; }
    /**
     * get SortedShortArray
     * @return the SortedShortArray
     */
    public short[] getSortedShortArray() 
    { 
	if(!mSorted)
	    sort();

	if(mShortArrayInSync == false)
	    translateValuesType(kShortArrayValuesType);

	return mSortedShortArray; 
    }

    //----------------------------------------
    // SortedIntArray
    private int[] mSortedIntArray;
    /**
     * set SortedIntArray
     * @param pSortedIntArray the SortedIntArray
     */
    public void setSortedIntArray(int[] pSortedIntArray) { mSortedIntArray = pSortedIntArray; }
    /**
     * get SortedIntArray
     * @return the SortedIntArray
     */
    public int[] getSortedIntArray() 
    { 
	if(!mSorted)
	    sort();

	if(mIntArrayInSync == false)
	    translateValuesType(kIntArrayValuesType);

	return mSortedIntArray; 
    }

    //----------------------------------------
    // SortedLongArray
    private long[] mSortedLongArray;
    /**
     * set SortedLongArray
     * @param pSortedLongArray the SortedLongArray
     */
    public void setSortedLongArray(long[] pSortedLongArray) { mSortedLongArray = pSortedLongArray; }
    /**
     * get SortedLongArray
     * @return the SortedLongArray
     */
    public long[] getSortedLongArray() 
    { 
	if(!mSorted)
	    sort();

	if(mLongArrayInSync == false)
	    translateValuesType(kLongArrayValuesType);

	return mSortedLongArray; 
    }

    //----------------------------------------
    // SortedFloatArray
    private float[] mSortedFloatArray;
    /**
     * set SortedFloatArray
     * @param pSortedFloatArray the SortedFloatArray
     */
    public void setSortedFloatArray(float[] pSortedFloatArray) { mSortedFloatArray = pSortedFloatArray; }
    /**
     * get SortedFloatArray
     * @return the SortedFloatArray
     */
    public float[] getSortedFloatArray() 
    { 
	if(!mSorted)
	    sort();

	if(mFloatArrayInSync == false)
	    translateValuesType(kFloatArrayValuesType);

	return mSortedFloatArray; 
    }

    //----------------------------------------
    // SortedDoubleArray
    private double[] mSortedDoubleArray;
    /**
     * set SortedDoubleArray
     * @param pSortedDoubleArray the SortedDoubleArray
     */
    public void setSortedDoubleArray(double[] pSortedDoubleArray) { mSortedDoubleArray = pSortedDoubleArray; }
    /**
     * get SortedDoubleArray
     * @return the SortedDoubleArray
     */
    public double[] getSortedDoubleArray() 
    { 
	if(!mSorted)
	    sort();

	if(mDoubleArrayInSync == false)
	    translateValuesType(kDoubleArrayValuesType);

	return mSortedDoubleArray; 
    }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof SortTag
     */
    public SortTag()
    {
	mSorted = false;

	mObjectArrayInSync = false;
	mCollectionInSync = false;
	mIteratorInSync = false;
	mEnumerationInSync = false;
	mMapInSync = false;

	mByteArrayInSync = false;
	mCharArrayInSync = false;
	mShortArrayInSync = false;
	mIntArrayInSync = false;
	mLongArrayInSync = false;
	mFloatArrayInSync = false;
	mDoubleArrayInSync = false;
    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * determine the type of the values attribute
     */
    public int doStartTag()
    {
	if(getValues() instanceof Collection)
	    setValuesType(kCollectionValuesType);
	else if(getValues() instanceof Object[])
	    setValuesType(kObjectArrayValuesType);
	else if(getValues() instanceof Iterator)
	    setValuesType(kIteratorValuesType);
	else if(getValues() instanceof Enumeration)
	    setValuesType(kEnumerationValuesType);
	else if(getValues() instanceof Map)
	    setValuesType(kMapValuesType);
	else if(getValues() instanceof boolean[])
	    setValuesType(kBooleanArrayValuesType);
	else if(getValues() instanceof byte[])
	    setValuesType(kByteArrayValuesType);
	else if(getValues() instanceof char[])
	    setValuesType(kCharArrayValuesType);
	else if(getValues() instanceof short[])
	    setValuesType(kShortArrayValuesType);
	else if(getValues() instanceof int[])
	    setValuesType(kIntArrayValuesType);
	else if(getValues() instanceof long[])
	    setValuesType(kLongArrayValuesType);
	else if(getValues() instanceof float[])
	    setValuesType(kFloatArrayValuesType);
	else if(getValues() instanceof double[])
	    setValuesType(kDoubleArrayValuesType);
	else {
	    setValuesType(kInvalidValuesType);
	    return SKIP_BODY;
	}

	getPageContext().setAttribute(getId(), this);
	return EVAL_BODY_INCLUDE;
    }

    //----------------------------------------
    /**
     * end tag
     */
    public int doEndTag()
	throws JspException
    {
	mSorted = false;

	mObjectArrayInSync = false;
	mCollectionInSync = false;
	mIteratorInSync = false;
	mEnumerationInSync = false;
	mMapInSync = false;

	mByteArrayInSync = false;
	mCharArrayInSync = false;
	mShortArrayInSync = false;
	mIntArrayInSync = false;
	mLongArrayInSync = false;
	mFloatArrayInSync = false;
	mDoubleArrayInSync = false;

  setComparator(null);

	if(mOrderings != null)
	    mOrderings.removeAllElements();

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
	setComparator(null);
	setReverse(false);

	setSortedArray(null);
	setSortedCollection(null);
	setSortedMap(null);
	setSortedEnumeration(null);
	setSortedIterator(null);
	setSortedBooleanArray(null);
	setSortedByteArray(null);
	setSortedCharArray(null);
	setSortedShortArray(null);
	setSortedIntArray(null);
	setSortedLongArray(null);
	setSortedFloatArray(null);
	setSortedDoubleArray(null);
    }

    //----------------------------------------
    /**
     * perform the sort
     */
    protected void sort() 
    {
	// if we don't have a comparator, but orderbys have been defined,
	// create a new comparator based on the orderbys
	if(mComparator == null && 
	   mOrderings != null && 
	   mOrderings.size() != 0) {
	    mComparator = new SortTagComparator(this);
	}

	switch(getValuesType()) {
	case kObjectArrayValuesType:
	    setSortedArray((Object[]) getValues());
	    sortObjectArray();
	    break;

	case kCollectionValuesType:
	    setSortedCollection((Collection) getValues());
	    sortCollection();
	    break;

	case kEnumerationValuesType:
	    setSortedEnumeration((Enumeration) getValues());
	    sortEnumeration();
	    break;

	case kIteratorValuesType:
	    setSortedIterator((Iterator) getValues());
	    sortIterator();
	    break;

	case kMapValuesType:
	    setSortedMap((Map) getValues());
	    sortMap();
	    break;

	case kBooleanArrayValuesType: 
	    {
		setSortedBooleanArray((boolean[]) getValues());
		int arraySize = mSortedBooleanArray.length;
		boolean[] sortedBooleanArray = new boolean[arraySize];

		int falses = 0;

		for(int i=0; i < arraySize; i++) {
		    if(mSortedBooleanArray[i] == false)
			sortedBooleanArray[falses++] = false;
		}
		for(int i=falses; i < arraySize; i++) {
		    sortedBooleanArray[i] = true;
		}
		
		setSortedBooleanArray(sortedBooleanArray);

		// reverse the orderings
		if(isReverse()) {
		    boolean[] reversedBooleanArray = new boolean[arraySize];

		    for(int i=0; i < arraySize; i++) 
			reversedBooleanArray[i] = mSortedBooleanArray[arraySize - i - 1];
		    
		    setSortedBooleanArray(reversedBooleanArray);
		}
	    }
	    break;

	case kByteArrayValuesType:
	    setSortedByteArray((byte[]) getValues());
	    Arrays.sort(mSortedByteArray);

	    // if reverse ordering, well, then reverse the ordering
	    if(isReverse()) {
		int arraySize = mSortedByteArray.length;
		byte[] reversedByteArray = new byte[arraySize];

		for(int i=0; i < arraySize; i++)
		    reversedByteArray[i] = mSortedByteArray[arraySize - i - 1];

		setSortedByteArray(reversedByteArray);
	    }

	    mByteArrayInSync = true;
	    break;

	case kCharArrayValuesType:
	    setSortedCharArray((char[]) getValues());
	    Arrays.sort(mSortedCharArray);

	    // if reverse ordering, well, then reverse the ordering
	    if(isReverse()) {
		int arraySize = mSortedCharArray.length;
		char[] reversedCharArray = new char[arraySize];

		for(int i=0; i < arraySize; i++)
		    reversedCharArray[i] = mSortedCharArray[arraySize - i - 1];

		setSortedCharArray(reversedCharArray);
	    }

	    mCharArrayInSync = true;
	    break;

	case kShortArrayValuesType:
	    setSortedShortArray((short[]) getValues());
	    Arrays.sort(mSortedShortArray);

	    // if reverse ordering, well, then reverse the ordering
	    if(isReverse()) {
		int arraySize = mSortedShortArray.length;
		short[] reversedShortArray = new short[arraySize];

		for(int i=0; i < arraySize; i++)
		    reversedShortArray[i] = mSortedShortArray[arraySize - i - 1];

		setSortedShortArray(reversedShortArray);
	    }

	    mShortArrayInSync = true;
	    break;

	case kIntArrayValuesType:
	    setSortedIntArray((int[]) getValues());
	    Arrays.sort(mSortedIntArray);

	    // if reverse ordering, well, then reverse the ordering
	    if(isReverse()) {
		int arraySize = mSortedIntArray.length;
		int[] reversedIntArray = new int[arraySize];

		for(int i=0; i < arraySize; i++)
		    reversedIntArray[i] = mSortedIntArray[arraySize - i - 1];

		setSortedIntArray(reversedIntArray);
	    }

	    mIntArrayInSync = true;
	    break;

	case kLongArrayValuesType:
	    setSortedLongArray((long[]) getValues());
	    Arrays.sort(mSortedLongArray);

	    // if reverse ordering, well, then reverse the ordering
	    if(isReverse()) {
		int arraySize = mSortedLongArray.length;
		long[] reversedLongArray = new long[arraySize];

		for(int i=0; i < arraySize; i++)
		    reversedLongArray[i] = mSortedLongArray[arraySize - i - 1];

		setSortedLongArray(reversedLongArray);
	    }

	    mLongArrayInSync = true;
	    break;

	case kFloatArrayValuesType:
	    setSortedFloatArray((float[]) getValues());
	    Arrays.sort(mSortedFloatArray);

	    // if reverse ordering, well, then reverse the ordering
	    if(isReverse()) {
		int arraySize = mSortedFloatArray.length;
		float[] reversedFloatArray = new float[arraySize];

		for(int i=0; i < arraySize; i++)
		    reversedFloatArray[i] = mSortedFloatArray[arraySize - i - 1];

		setSortedFloatArray(reversedFloatArray);
	    }

	    mFloatArrayInSync = true;
	    break;

	case kDoubleArrayValuesType:
	    setSortedDoubleArray((double[]) getValues());
	    Arrays.sort(mSortedDoubleArray);

	    // if reverse ordering, well, then reverse the ordering
	    if(isReverse()) {
		int arraySize = mSortedDoubleArray.length;
		double[] reversedDoubleArray = new double[arraySize];

		for(int i=0; i < arraySize; i++)
		    reversedDoubleArray[i] = mSortedDoubleArray[arraySize - i - 1];

		setSortedDoubleArray(reversedDoubleArray);
	    }
	    
	    mDoubleArrayInSync = true;
	    break;

	case kInvalidValuesType:
	    return;
	}

	mSorted = true;
    }

    //----------------------------------------
    /**
     * sort an Object array
     *
     */
    protected void sortObjectArray()
    {
	// use comparator
	if(getComparator() != null)
	    Arrays.sort(mSortedArray, getComparator());

	// use natural ordering
	else
	    Arrays.sort(mSortedArray);	    

	// reverse the ordering if need be
	if(isReverse())
	    flipObjectArray();

	mObjectArrayInSync = true;
    }

    //----------------------------------------
    /**
     * flip the order of the Object array
     */
    protected void flipObjectArray()
    {
	int arraySize = mSortedArray.length;
	Object[] reverseSorted = new Object[arraySize];
	    
	for(int i=0; i < arraySize; i++) {
	    reverseSorted[i] = mSortedArray[arraySize - i - 1];
	}

	setSortedArray(reverseSorted);
    }

    //----------------------------------------
    /**
     * sort a Collection
     */
    protected void sortCollection()
    {
	// turn collection into Object array, then sort
	collectionToObjectArray();
	sortObjectArray();
    }

    //----------------------------------------
    /**
     * turn the collection into an object array
     */
    protected void collectionToObjectArray()
    {
	setSortedArray(mSortedCollection.toArray());
    }

    //----------------------------------------
    /**
     * synchronize values in collection with values in object array
     */
    protected void objectArrayToCollection()
    {
	int arraySize = mSortedArray.length;
	Vector objectVector = new Vector(arraySize);
	
	for(int i=0; i < arraySize; i++)
	    objectVector.add(mSortedArray[i]);

	setSortedCollection(objectVector);
	
	mCollectionInSync = true;
    }

    //----------------------------------------
    /**
     * sort an Enumeration
     */
    protected void sortEnumeration()
    {
	// turn enumeration into Object array, then sort
	enumerationToObjectArray();
	sortObjectArray();
    }

    //----------------------------------------
    /**
     * convert the Enumeration into an Object array
     */
    protected void enumerationToObjectArray()
    {
	Vector objectVector = new Vector();
	
	while(mSortedEnumeration.hasMoreElements())
	    objectVector.add(mSortedEnumeration.nextElement());

	setSortedArray(objectVector.toArray());
    }

    //----------------------------------------
    /**
     * synchronize values in Enumeration with values in Object array
     */
    protected void objectArrayToEnumeration()
    {
	int arraySize = mSortedArray.length;
	Vector objectVector = new Vector(arraySize);
	
	for(int i=0; i < arraySize; i++)
	    objectVector.add(mSortedArray[i]);

	setSortedEnumeration(objectVector.elements());
	
	mEnumerationInSync = true;
    }

    //----------------------------------------
    /**
     * sort an Iterator
     */
    protected void sortIterator()
    {
	// turn iterator into Object array, then sort
	iteratorToObjectArray();
	sortObjectArray();
    }

    //----------------------------------------
    /**
     * convert the Iterator to an Object array
     */
    protected void iteratorToObjectArray()
    {
	Vector objectVector = new Vector();

	while(mSortedIterator.hasNext()) {
	    Object nextObject = mSortedIterator.next();
	    objectVector.add(nextObject);
	}

	setSortedArray(objectVector.toArray());
    }

    //----------------------------------------
    /**
     * synchronize values in Iterator with values in Object array
     */
    protected void objectArrayToIterator()
    {
	objectArrayToCollection();
	setSortedIterator(mSortedCollection.iterator());
	
	mIteratorInSync = true;
    }

    //----------------------------------------
    /**
     * sort a Map
     */
    protected void sortMap()
    {
	// turn map into object array, then sort
	mapToObjectArray();
	sortObjectArray();
    }

    //----------------------------------------
    /**
     * convert the map into an object array
     */
    protected void mapToObjectArray()
    {
	setSortedArray(mSortedMap.keySet().toArray());
    }

    //----------------------------------------
    /**
     * synchronize values in Map with values in Object array
     */
    protected void objectArrayToMap()
    {
	Hashtable tempMap = new Hashtable(mSortedArray.length);

	for(int i=0; i < mSortedArray.length; i++)
	    tempMap.put(mSortedArray[i], mSortedArray[i]);

	setSortedMap(new OrderedMap(new OrderedSet(mSortedArray),
				    tempMap));
	mMapInSync = true;
    }

    //----------------------------------------
    /**
     * convert an unordered map into an ordered map 
     */
    protected void mapToMap()
    {
	setSortedMap(new OrderedMap(new OrderedSet(mSortedArray),
				    mSortedMap));
	mMapInSync = true;
    }

    //----------------------------------------
    /**
     * booleans to objecyt array
     */
    protected void booleansToObjectArray()
    {
	Boolean[] booleanArray = new Boolean[mSortedBooleanArray.length];

	for(int i=0; i < booleanArray.length; i++)
	    booleanArray[i] = new Boolean(mSortedBooleanArray[i]);

	setSortedArray(booleanArray);
	
	mObjectArrayInSync = true;
    }

    //----------------------------------------
    /**
     * booleans to bytes
     */
    protected void booleansToBytes()
    {
	byte[] array = new byte[mSortedBooleanArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = mSortedBooleanArray[i] ? (byte) 1 : (byte) 0;
	
	setSortedByteArray(array);
	
	mByteArrayInSync = true;
    }

    //----------------------------------------
    /**
     * booleans to chars
     */
    protected void booleansToChars()
    {
	char[] array = new char[mSortedBooleanArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = mSortedBooleanArray[i] ? 't' : 'f';
	
	setSortedCharArray(array);
	
	mCharArrayInSync = true;
    }

    //----------------------------------------
    /**
     * booleans to shorts
     */
    protected void booleansToShorts()
    {
	short[] array = new short[mSortedBooleanArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = mSortedBooleanArray[i] ? (short) 1 : (short) 0;
	
	setSortedShortArray(array);
	
	mShortArrayInSync = true;
    }

    //----------------------------------------
    /**
     * booleans to ints
     */
    protected void booleansToInts()
    {
	int[] array = new int[mSortedBooleanArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = mSortedBooleanArray[i] ? (int) 1 : (int) 0;
	
	setSortedIntArray(array);
	
	mIntArrayInSync = true;
    }

    //----------------------------------------
    /**
     * booleans to longs
     */
    protected void booleansToLongs()
    {
	long[] array = new long[mSortedBooleanArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = mSortedBooleanArray[i] ? (long) 1 : (long) 0;
	
	setSortedLongArray(array);
	
	mLongArrayInSync = true;
    }

    //----------------------------------------
    /**
     * booleans to floats
     */
    protected void booleansToFloats()
    {
	float[] array = new float[mSortedBooleanArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = mSortedBooleanArray[i] ? (float) 1.0 : (float) 0.0;
	
	setSortedFloatArray(array);
	
	mFloatArrayInSync = true;
    }

    //----------------------------------------
    /**
     * booleans to doubles
     */
    protected void booleansToDoubles()
    {
	double[] array = new double[mSortedBooleanArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = mSortedBooleanArray[i] ? (double) 1.0 : (double) 0.0;
	
	setSortedDoubleArray(array);
	
	mDoubleArrayInSync = true;
    }

    //----------------------------------------
    /**
     * bytes to object array
     */
    protected void bytesToObjectArray()
    {
	Byte[] bytesArray = new Byte[mSortedByteArray.length];

	for(int i=0; i < bytesArray.length; i++)
	    bytesArray[i] = new Byte(mSortedByteArray[i]);

	setSortedArray(bytesArray);

	mObjectArrayInSync = true;
    }

    //----------------------------------------
    /**
     * bytes to chars
     */
    protected void bytesToChars()
    {
	char[] array = new char[mSortedByteArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (char) mSortedByteArray[i];

	setSortedCharArray(array);

	mCharArrayInSync = true;
    }

    //----------------------------------------
    /**
     * bytes to shorts
     */
    protected void bytesToShorts()
    {
	short[] array = new short[mSortedByteArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (short) mSortedByteArray[i];

	setSortedShortArray(array);

	mShortArrayInSync = true;
    }

    //----------------------------------------
    /**
     * bytes to ints
     */
    protected void bytesToInts()
    {
	int[] array = new int[mSortedByteArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (int) mSortedByteArray[i];

	setSortedIntArray(array);

	mIntArrayInSync = true;
    }

    //----------------------------------------
    /**
     * bytes to longs
     */
    protected void bytesToLongs()
    {
	long[] array = new long[mSortedByteArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (long) mSortedByteArray[i];

	setSortedLongArray(array);

	mLongArrayInSync = true;
    }

    //----------------------------------------
    /**
     * bytes to floats
     */
    protected void bytesToFloats()
    {
	float[] array = new float[mSortedByteArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (float) mSortedByteArray[i];

	setSortedFloatArray(array);

	mFloatArrayInSync = true;
    }

    //----------------------------------------
    /**
     * bytes to doubles
     */
    protected void bytesToDoubles()
    {
	double[] array = new double[mSortedByteArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (double) mSortedByteArray[i];

	setSortedDoubleArray(array);

	mDoubleArrayInSync = true;
    }

    //----------------------------------------
    /**
     * chars to object array
     */
    protected void charsToObjectArray()
    {
	Character[] objectArray = new Character[mSortedCharArray.length];

	for(int i=0; i < objectArray.length; i++)
	    objectArray[i] = new Character(mSortedCharArray[i]);

	setSortedArray(objectArray);
    }

    //----------------------------------------
    /**
     * chars to bytes
     */
    protected void charsToBytes()
    {
	byte[] array = new byte[mSortedCharArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (byte) mSortedCharArray[i];

	setSortedByteArray(array);

	mByteArrayInSync = true;
    }

    //----------------------------------------
    /**
     * chars to shorts
     */
    protected void charsToShorts()
    {
	short[] array = new short[mSortedCharArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (short) mSortedCharArray[i];

	setSortedShortArray(array);

	mShortArrayInSync = true;
    }

    //----------------------------------------
    /**
     * chars to ints
     */
    protected void charsToInts()
    {
	int[] array = new int[mSortedCharArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (int) mSortedCharArray[i];

	setSortedIntArray(array);

	mIntArrayInSync = true;
    }

    //----------------------------------------
    /**
     * chars to longs
     */
    protected void charsToLongs()
    {
	long[] array = new long[mSortedCharArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (long) mSortedCharArray[i];

	setSortedLongArray(array);

	mLongArrayInSync = true;
    }

    //----------------------------------------
    /**
     * chars to floats
     */
    protected void charsToFloats()
    {
	float[] array = new float[mSortedCharArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (float) mSortedCharArray[i];

	setSortedFloatArray(array);

	mFloatArrayInSync = true;
    }

    //----------------------------------------
    /**
     * chars to doubles
     */
    protected void charsToDoubles()
    {
	double[] array = new double[mSortedCharArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (double) mSortedCharArray[i];

	setSortedDoubleArray(array);

	mDoubleArrayInSync = true;
    }

    //----------------------------------------
    /**
     * shorts to object array
     */
    protected void shortsToObjectArray()
    {
	Short[] objectArray = new Short[mSortedShortArray.length];

	for(int i=0; i < objectArray.length; i++)
	    objectArray[i] = new Short(mSortedShortArray[i]);

	setSortedArray(objectArray);

	mObjectArrayInSync = true;
    }

    //----------------------------------------
    /**
     * shorts to bytes
     */
    protected void shortsToBytes()
    {
	byte[] array = new byte[mSortedShortArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (byte) mSortedShortArray[i];

	setSortedByteArray(array);

	mByteArrayInSync = true;
    }

    //----------------------------------------
    /**
     * shorts to chars
     */
    protected void shortsToChars()
    {
	char[] array = new char[mSortedShortArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (char) mSortedShortArray[i];

	setSortedCharArray(array);

	mCharArrayInSync = true;
    }

    //----------------------------------------
    /**
     * shorts to ints
     */
    protected void shortsToInts()
    {
	int[] array = new int[mSortedShortArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (int) mSortedShortArray[i];

	setSortedIntArray(array);

	mIntArrayInSync = true;
    }

    //----------------------------------------
    /**
     * shorts to longs
     */
    protected void shortsToLongs()
    {
	long[] array = new long[mSortedShortArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (long) mSortedShortArray[i];

	setSortedLongArray(array);

	mLongArrayInSync = true;
    }

    //----------------------------------------
    /**
     * shorts to floats
     */
    protected void shortsToFloats()
    {
	float[] array = new float[mSortedShortArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (float) mSortedShortArray[i];

	setSortedFloatArray(array);

	mFloatArrayInSync = true;
    }

    //----------------------------------------
    /**
     * shorts to doubles 
     */
    protected void shortsToDoubles()
    {
	double[] array = new double[mSortedShortArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (double) mSortedShortArray[i];

	setSortedDoubleArray(array);

	mDoubleArrayInSync = true;
    }

    //----------------------------------------
    /**
     * ints to object array
     */
    protected void intsToObjectArray()
    {
	Integer[] objectArray = new Integer[mSortedIntArray.length];

	for(int i=0; i < objectArray.length; i++)
	    objectArray[i] = new Integer(mSortedIntArray[i]);

	setSortedArray(objectArray);
	
	mObjectArrayInSync = true;
    }

    //----------------------------------------
    /**
     * ints to bytes
     */
    protected void intsToBytes()
    {
	byte[] array = new byte[mSortedIntArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (byte) mSortedIntArray[i];

	setSortedByteArray(array);

	mByteArrayInSync = true;
    }

    //----------------------------------------
    /**
     * ints to chars
     */
    protected void intsToChars()
    {
	char[] array = new char[mSortedIntArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (char) mSortedIntArray[i];

	setSortedCharArray(array);

	mCharArrayInSync = true;
    }

    //----------------------------------------
    /**
     * ints to shorts
     */
    protected void intsToShorts()
    {
	short[] array = new short[mSortedIntArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (short) mSortedIntArray[i];

	setSortedShortArray(array);

	mShortArrayInSync = true;
    }

    //----------------------------------------
    /**
     * ints to longs
     */
    protected void intsToLongs()
    {
	long[] array = new long[mSortedIntArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (long) mSortedIntArray[i];

	setSortedLongArray(array);

	mLongArrayInSync = true;
    }

    //----------------------------------------
    /**
     * ints to floats
     */
    protected void intsToFloats()
    {
	float[] array = new float[mSortedIntArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (float) mSortedIntArray[i];

	setSortedFloatArray(array);

	mFloatArrayInSync = true;
    }

    //----------------------------------------
    /**
     * ints to doubles
     */
    protected void intsToDoubles()
    {
	double[] array = new double[mSortedIntArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (double) mSortedIntArray[i];

	setSortedDoubleArray(array);

	mDoubleArrayInSync = true;
    }

    //----------------------------------------
    /**
     * longs to object array
     */
    protected void longsToObjectArray()
    {
	Long[] objectArray = new Long[mSortedLongArray.length];

	for(int i=0; i < objectArray.length; i++)
	    objectArray[i] = new Long(mSortedLongArray[i]);

	setSortedArray(objectArray);

	mObjectArrayInSync = true;
    }

    //----------------------------------------
    /**
     * longs to bytes
     */
    protected void longsToBytes()
    {
	byte[] array = new byte[mSortedLongArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (byte) mSortedLongArray[i];

	setSortedByteArray(array);

	mByteArrayInSync = true;
    }

    //----------------------------------------
    /**
     * longs to chars
     */
    protected void longsToChars()
    {
	char[] array = new char[mSortedLongArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (char) mSortedLongArray[i];

	setSortedCharArray(array);

	mCharArrayInSync = true;
    }

    //----------------------------------------
    /**
     * longs to shorts
     */
    protected void longsToShorts()
    {
	short[] array = new short[mSortedLongArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (short) mSortedLongArray[i];

	setSortedShortArray(array);

	mShortArrayInSync = true;
    }

    //----------------------------------------
    /**
     * longs to ints
     */
    protected void longsToInts()
    {
	int[] array = new int[mSortedLongArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (int) mSortedLongArray[i];

	setSortedIntArray(array);

	mIntArrayInSync = true;
    }

    //----------------------------------------
    /**
     * longs to floats
     */
    protected void longsToFloats()
    {
	float[] array = new float[mSortedLongArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (float) mSortedLongArray[i];

	setSortedFloatArray(array);

	mFloatArrayInSync = true;
    }

    //----------------------------------------
    /**
     * longs to doubles
     */
    protected void longsToDoubles()
    {
	double[] array = new double[mSortedLongArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (double) mSortedLongArray[i];

	setSortedDoubleArray(array);

	mDoubleArrayInSync = true;
    }

    //----------------------------------------
    /**
     * floats to object array
     */
    protected void floatsToObjectArray()
    {
	Float[] objectArray = new Float[mSortedFloatArray.length];

	for(int i=0; i < objectArray.length; i++)
	    objectArray[i] = new Float(mSortedFloatArray[i]);

	setSortedArray(objectArray);

	mObjectArrayInSync = true;
    }

    //----------------------------------------
    /**
     * floats to bytes
     */
    protected void floatsToBytes()
    {
	byte[] array = new byte[mSortedFloatArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (byte) mSortedFloatArray[i];

	setSortedByteArray(array);

	mByteArrayInSync = true;
    }

    //----------------------------------------
    /**
     * floats to chars
     */
    protected void floatsToChars()
    {
	char[] array = new char[mSortedFloatArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (char) mSortedFloatArray[i];

	setSortedCharArray(array);

	mCharArrayInSync = true;
    }

    //----------------------------------------
    /**
     * floats to shorts
     */
    protected void floatsToShorts()
    {
	short[] array = new short[mSortedFloatArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (short) mSortedFloatArray[i];

	setSortedShortArray(array);

	mShortArrayInSync = true;
    }

    //----------------------------------------
    /**
     * floats to ints
     */
    protected void floatsToInts()
    {
	int[] array = new int[mSortedFloatArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (int) mSortedFloatArray[i];

	setSortedIntArray(array);

	mIntArrayInSync = true;
    }

    //----------------------------------------
    /**
     * floats to longs
     */
    protected void floatsToLongs()
    {
	long[] array = new long[mSortedFloatArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (long) mSortedFloatArray[i];

	setSortedLongArray(array);

	mLongArrayInSync = true;
    }

    //----------------------------------------
    /**
     * floats to doubles
     */
    protected void floatsToDoubles()
    {
	double[] array = new double[mSortedFloatArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (double) mSortedFloatArray[i];

	setSortedDoubleArray(array);

	mDoubleArrayInSync = true;
    }

    //----------------------------------------
    /**
     * doubles to object array
     */
    protected void doublesToObjectArray()
    {
	Double[] objectArray = new Double[mSortedDoubleArray.length];

	for(int i=0; i < objectArray.length; i++)
	    objectArray[i] = new Double(mSortedDoubleArray[i]);

	setSortedArray(objectArray);

	mObjectArrayInSync = true;
    }

    //----------------------------------------
    /**
     * doubles to bytes
     */
    protected void doublesToBytes()
    {
	byte[] array = new byte[mSortedDoubleArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (byte) mSortedDoubleArray[i];

	setSortedByteArray(array);

	mByteArrayInSync = true;
    }

    //----------------------------------------
    /**
     * doubles to chars
     */
    protected void doublesToChars()
    {
	char[] array = new char[mSortedDoubleArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (char) mSortedDoubleArray[i];

	setSortedCharArray(array);

	mCharArrayInSync = true;
    }

    //----------------------------------------
    /**
     * doubles to shorts
     */
    protected void doublesToShorts()
    {
	short[] array = new short[mSortedDoubleArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (short) mSortedDoubleArray[i];

	setSortedShortArray(array);

	mShortArrayInSync = true;
    }

    //----------------------------------------
    /**
     * doubles to ints
     */
    protected void doublesToInts()
    {
	int[] array = new int[mSortedDoubleArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (int) mSortedDoubleArray[i];

	setSortedIntArray(array);

	mIntArrayInSync = true;
    }

    //----------------------------------------
    /**
     * doubles to longs
     */
    protected void doublesToLongs()
    {
	long[] array = new long[mSortedDoubleArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (long) mSortedDoubleArray[i];

	setSortedLongArray(array);

	mLongArrayInSync = true;
    }

    //----------------------------------------
    /**
     * doubles to floats
     */
    protected void doublesToFloats()
    {
	float[] array = new float[mSortedDoubleArray.length];
	
	for(int i=0; i < array.length; i++)
	    array[i] = (float) mSortedDoubleArray[i];

	setSortedFloatArray(array);

	mFloatArrayInSync = true;
    }

    //----------------------------------------
    /**
     * convert between a byte array and other types
     */
    protected void translateValuesType(byte pValuesType)
    {
	switch(getValuesType()) {
	case kCollectionValuesType:
	    switch(pValuesType) {
	    case kCollectionValuesType:
		// collectoin to collection
		objectArrayToCollection();
		break;
	    case kEnumerationValuesType:
		// collection to enumeration
		objectArrayToEnumeration();
		break;
	    case kIteratorValuesType:
		// collection to iterator
		objectArrayToIterator();
		break;
	    case kMapValuesType:
		// collection to map
		objectArrayToMap();
		break;
	    }
	    break;
	case kObjectArrayValuesType:
	    switch(pValuesType) {
	    case kCollectionValuesType:
		// object array to collection
		objectArrayToCollection();
		break;
	    case kEnumerationValuesType:
		// object array to enumeration
		objectArrayToEnumeration();
		break;
	    case kIteratorValuesType:
		// object array to iterator
		objectArrayToIterator();
		break;
	    case kMapValuesType:
		// object array to map
		objectArrayToMap();
		break;
	    }
	    break;
	case kEnumerationValuesType:
	    switch(pValuesType) {
	    case kEnumerationValuesType:
		// enumeration to enumeration
		objectArrayToEnumeration();
		break;
	    case kCollectionValuesType:
		// enumeration to collection
		objectArrayToCollection();
		break;
	    case kIteratorValuesType:
		// enumeration to iterator
		objectArrayToIterator();
		break;
	    case kMapValuesType:
		// enumeration to map
		objectArrayToMap();
		break;
	    }
	    break;
	case kIteratorValuesType:
	    switch(pValuesType) {
	    case kCollectionValuesType:
		// iterator to collection
		objectArrayToCollection();
		break;
	    case kIteratorValuesType:
		// iterator to iterator
		objectArrayToIterator();
		break;
	    case kEnumerationValuesType:
		// iterator to enumeration
		objectArrayToEnumeration();
		break;
	    case kMapValuesType:
		// iterator to map
		objectArrayToMap();
		break;
	    }
	    break;
	case kMapValuesType:
	    switch(pValuesType) {
	    case kMapValuesType:
		// map to map
		mapToMap();
		break;
	    case kCollectionValuesType:
		// map to collection
		objectArrayToCollection();
		break;
	    case kEnumerationValuesType:
		// map to enumeration
		objectArrayToEnumeration();
		break;
	    case kIteratorValuesType:
		// map to iterator
		objectArrayToIterator();
		break;
	    }
	    break;
	case kBooleanArrayValuesType:
	    switch(pValuesType) {
	    case kCollectionValuesType:
		// booleans to collection
		booleansToObjectArray();
		objectArrayToCollection();
		break;
	    case kObjectArrayValuesType:
		// booleans to object array
		booleansToObjectArray();
		break;
	    case kEnumerationValuesType:
		// booleans to enumeration
		booleansToObjectArray();
		objectArrayToEnumeration();
		break;
	    case kIteratorValuesType:
		// booleans to iterator
		booleansToObjectArray();
		objectArrayToIterator();
		break;
	    case kMapValuesType:
		// booleans to map
		booleansToObjectArray();
		objectArrayToMap();
		break;
	    case kByteArrayValuesType:
		// booleans to chars
		booleansToBytes();
		break;
	    case kCharArrayValuesType:
		// booleans to chars
		booleansToChars();
		break;
	    case kShortArrayValuesType:
		// booleans to shorts
		booleansToShorts();
		break;
	    case kIntArrayValuesType:
		// booleans to ints
		booleansToInts();
		break;
	    case kLongArrayValuesType:
		// booleans to longs
		booleansToLongs();
		break;
	    case kFloatArrayValuesType:
		// booleans to floats
		booleansToFloats();
		break;
	    case kDoubleArrayValuesType:
		// booleans to doubles
		booleansToDoubles();
		break;
	    }
	    break;
	case kByteArrayValuesType:
	    switch(pValuesType) {
	    case kCollectionValuesType:
		// bytes to collection
		bytesToObjectArray();
		objectArrayToCollection();
		break;
	    case kObjectArrayValuesType:
		// bytes to object array
		bytesToObjectArray();
		break;
	    case kEnumerationValuesType:
		// bytes to enumeration
		bytesToObjectArray();
		objectArrayToEnumeration();
		break;
	    case kIteratorValuesType:
		// bytes to iterator
		bytesToObjectArray();
		objectArrayToIterator();
		break;
	    case kMapValuesType:
		// bytes to map
		bytesToObjectArray();
		objectArrayToMap();
		break;
	    case kCharArrayValuesType:
		// bytes to chars
		bytesToChars();
		break;
	    case kShortArrayValuesType:
		// bytes to shorts
		bytesToShorts();
		break;
	    case kIntArrayValuesType:
		// bytes to ints
		bytesToInts();
		break;
	    case kLongArrayValuesType:
		// bytes to longs
		bytesToLongs();
		break;
	    case kFloatArrayValuesType:
		// bytes to floats
		bytesToFloats();
		break;
	    case kDoubleArrayValuesType:
		// bytes to doubles
		bytesToDoubles();
		break;
	    }
	    break;
	case kCharArrayValuesType:
	    switch(pValuesType) {
	    case kCollectionValuesType:
		// chars to collection
		charsToObjectArray();
		objectArrayToCollection();
		break;
	    case kObjectArrayValuesType:
		// chars to object array
		charsToObjectArray();
		break;
	    case kEnumerationValuesType:
		// chars to enumeration
		charsToObjectArray();
		objectArrayToEnumeration();
		break;
	    case kIteratorValuesType:
		// chars to iterator
		charsToObjectArray();
		objectArrayToIterator();
		break;
	    case kMapValuesType:
		// chars to map
		charsToObjectArray();
		objectArrayToMap();
		break;
	    case kByteArrayValuesType:
		// chars to bytes
		charsToBytes();
		break;
	    case kShortArrayValuesType:
		// chars to shorts
		charsToShorts();
		break;
	    case kIntArrayValuesType:
		// chars to ints
		charsToInts();
		break;
	    case kLongArrayValuesType:
		// chars to longs
		charsToLongs();
		break;
	    case kFloatArrayValuesType:
		// chars to floats
		charsToFloats();
		break;
	    case kDoubleArrayValuesType:
		// chars to doubles
		charsToDoubles();
		break;
	    }
	    break;
	case kShortArrayValuesType:
	    switch(pValuesType) {
	    case kCollectionValuesType:
		// shorts to collection
		shortsToObjectArray();
		objectArrayToCollection();
		break;
	    case kObjectArrayValuesType:
		// shorts to objects
		shortsToObjectArray();
		break;
	    case kEnumerationValuesType:
		// shorts to enumeration
		shortsToObjectArray();
		objectArrayToEnumeration();
		break;
	    case kIteratorValuesType:
		// shorts to iterator
		shortsToObjectArray();
		objectArrayToIterator();
		break;
	    case kMapValuesType:
		// shorts to map
		shortsToObjectArray();
		objectArrayToMap();
		break;
	    case kByteArrayValuesType:
		// shorts to bytes
		shortsToBytes();
		break;
	    case kCharArrayValuesType:
		// shorts to chars
		shortsToChars();
		break;
	    case kIntArrayValuesType:
		// shorts to ints
		shortsToInts();
		break;
	    case kLongArrayValuesType:
		// shorts to longs
		shortsToLongs();
		break;
	    case kFloatArrayValuesType:
		// shorts to floats
		shortsToFloats();
		break;
	    case kDoubleArrayValuesType:
		// shorts to doubles
		shortsToDoubles();
		break;
	    }
	    break;
	case kIntArrayValuesType:
	    switch(pValuesType) {
	    case kCollectionValuesType:
		// ints to collection
		intsToObjectArray();
		objectArrayToCollection();
		break;
	    case kObjectArrayValuesType:
		// ints to object array
		intsToObjectArray();
		break;
	    case kEnumerationValuesType:
		// ints to enumeration
		intsToObjectArray();
		objectArrayToEnumeration();
		break;
	    case kIteratorValuesType:
		// ints to iterator
		intsToObjectArray();
		objectArrayToIterator();
		break;
	    case kMapValuesType:
		// ints to map
		intsToObjectArray();
		objectArrayToMap();
		break;
	    case kByteArrayValuesType:
		// ints to bytes
		intsToBytes();
		break;
	    case kCharArrayValuesType:
		// ints to chars
		intsToChars();
		break;
	    case kShortArrayValuesType:
		// ints to shorts
		intsToShorts();
		break;
	    case kLongArrayValuesType:
		// ints to longs
		intsToLongs();
		break;
	    case kFloatArrayValuesType:
		// ints to floats
		intsToFloats();
		break;
	    case kDoubleArrayValuesType:
		// ints to doubles
		intsToDoubles();
		break;
	    }
	    break;
	case kLongArrayValuesType:
	    switch(pValuesType) {
	    case kCollectionValuesType:
		// longs to collection
		longsToObjectArray();
		objectArrayToCollection();
		break;
	    case kObjectArrayValuesType:
		// longs to object array
		longsToObjectArray();
		break;
	    case kEnumerationValuesType:
		// longs to enumeration
		longsToObjectArray();
		objectArrayToEnumeration();
		break;
	    case kIteratorValuesType:
		// longs to iterator
		longsToObjectArray();
		objectArrayToIterator();
		break;
	    case kMapValuesType:
		// longs to map
		longsToObjectArray();
		objectArrayToMap();
		break;
	    case kByteArrayValuesType:
		// longs to bytes 
		longsToBytes();
		break;
	    case kCharArrayValuesType:
		// longs to chars
		longsToChars();
		break;
	    case kShortArrayValuesType:
		// longs to shorts
		longsToShorts();
		break;
	    case kIntArrayValuesType:
		// longs to ints
		longsToInts();
		break;
	    case kFloatArrayValuesType:
		// longs to floats
		longsToFloats();
		break;
	    case kDoubleArrayValuesType:
		// longs to doubles
		longsToDoubles();
		break;
	    }
	    break;
	case kFloatArrayValuesType:
	    switch(pValuesType) {
	    case kCollectionValuesType:
		// floats to collection
		floatsToObjectArray();
		objectArrayToCollection();
		break;
	    case kObjectArrayValuesType:
		// floats to object array
		floatsToObjectArray();
		break;
	    case kEnumerationValuesType:
		// floats enumeration
		floatsToObjectArray();
		objectArrayToEnumeration();
		break;
	    case kIteratorValuesType:
		// floats to iterator
		floatsToObjectArray();
		objectArrayToIterator();
		break;
	    case kMapValuesType:
		// floats to map
		floatsToObjectArray();
		objectArrayToMap();
		break;
	    case kByteArrayValuesType:
		// floats to bytes
		floatsToBytes();
		break;
	    case kCharArrayValuesType:
		// floats to chars
		floatsToChars();
		break;
	    case kShortArrayValuesType:
		// floats to shorts
		floatsToShorts();
		break;
	    case kIntArrayValuesType:
		// floats to ints
		floatsToInts();
		break;
	    case kLongArrayValuesType:
		// floats to longs
		floatsToLongs();
		break;
	    case kDoubleArrayValuesType:
		// floats to doubles
		floatsToDoubles();
		break;
	    }
	    break;
	case kDoubleArrayValuesType:
	    switch(pValuesType) {
	    case kCollectionValuesType:
		// doubles to collection
		doublesToObjectArray();
		objectArrayToCollection();
		break;
	    case kObjectArrayValuesType:
		// doubles to object array
		doublesToObjectArray();
		break;
	    case kEnumerationValuesType:
		// doubles to enumeration
		doublesToObjectArray();
		objectArrayToEnumeration();
		break;
	    case kIteratorValuesType:
		// doubles to iterator
		doublesToObjectArray();
		objectArrayToIterator();
		break;
	    case kMapValuesType:
		// doubles to map
		doublesToObjectArray();
		objectArrayToMap();
		break;
	    case kByteArrayValuesType:
		// doubles to bytes
		doublesToBytes();
		break;
	    case kCharArrayValuesType:
		// doubles to chars
		doublesToChars();
		break;
	    case kShortArrayValuesType:
		// doubles to shorts
		doublesToShorts();
		break;
	    case kIntArrayValuesType:
		// doubles to ints
		doublesToInts();
		break;
	    case kLongArrayValuesType:
		// doubles to longs
		doublesToLongs();
		break;
	    case kFloatArrayValuesType:
		// doubles to floats
		doublesToFloats();
		break;
	    }
	    break;
	}
    }

    //----------------------------------------
    /**
     * add an ordering to the vector
     */
    public void addOrdering(Ordering pOrdering)
    {
	if(mOrderings == null)
	    mOrderings = new Vector(7);

	mOrderings.add(pOrdering);
    }
    
} // end of class
