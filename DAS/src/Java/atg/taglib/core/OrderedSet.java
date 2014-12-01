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

/****************************************
 * This class is an implementation of the Set interface
 * designed to easily allow one to convert an object array
 * to a set. no checks are made to guarantee element uniqueness.
 * this set is immutable, thus all mutation methods will throw 
 * an UnsupportedOperationException.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/OrderedSet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class OrderedSet
    implements Set
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/OrderedSet.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    // array containing values
    private Object[] mValues;

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof OrderedSet
     */
    public OrderedSet(Object[] pObjects)
    {
	mValues = pObjects;
    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * add an element
     */
    public boolean add(Object pObject)
    {
	throw new UnsupportedOperationException();
    }

    //----------------------------------------
    /**
     * add all elements 
     */
    public boolean addAll(Collection pCollection)
    {
	throw new UnsupportedOperationException();
    }

    //----------------------------------------
    /**
     * clear this set
     */
    public void clear()
    {
	throw new UnsupportedOperationException();
    }

    //----------------------------------------
    /**
     * test to see if this set contains the specified element
     */
    public boolean contains(Object pObject)
    {
	for(int i=0; i < mValues.length; i++) {
	    if(pObject.equals(mValues[i]))
		return true;
	}

	return false;
    }

    //----------------------------------------
    /**
     * test to see if this set contains all the specified elements
     */
    public boolean containsAll(Collection pCollection)
    {
	Iterator itemIterator = pCollection.iterator();

	while(itemIterator.hasNext()) {
	    Object currentItem = itemIterator.next();
	    int i;

	    for(i=0; i < mValues.length; i++) {
		if(currentItem.equals(mValues[i]))
		    break;
	    }
	    
	    if(i == mValues.length)
		return false;
	}

	return true;
    }

    //----------------------------------------
    /**
     * retains only the items in the specified collection
     */
    public boolean retainAll(Collection pCollection)
    {
	throw new UnsupportedOperationException();
    }

    //----------------------------------------
    /**
     * compares the specified object with this set
     */
    public boolean equals(Object pObject)
    {
	if(pObject instanceof OrderedSet) {
	    OrderedSet set = (OrderedSet) pObject;
	    
	    if(set.size() != this.size())
		return false;

	    Object[] objects = set.toArray();

	    for(int i=0; i < mValues.length; i++) {
		if(!mValues[i].equals(objects[i]))
		    return false;
	    }

	    return true;
	}
	else {
	    return false;
	}
    }

    //----------------------------------------
    /**
     * get the hashcode for this set
     */
    public int hashCode()
    {
	return super.hashCode();
    }

    //----------------------------------------
    /**
     * test to see if this set is empty
     */
    public boolean isEmpty()
    {
	if(mValues.length == 0)
	    return true;
	else
	    return false;
    }

    //----------------------------------------
    /**
     * return an iterator over the items in this set
     */
    public Iterator iterator()
    {
	return Arrays.asList(mValues).iterator();
    }

    //----------------------------------------
    /**
     * remove an object from this set
     */
    public boolean remove(Object pObject)
    {
	throw new UnsupportedOperationException();
    }

    //----------------------------------------
    /**
     * remove all the elements in the collection
     */
    public boolean removeAll(Collection pCollection)
    {
	throw new UnsupportedOperationException();
    }

    //----------------------------------------
    /**
     * get the size of the set
     */
    public int size()
    {
	return mValues.length;
    }

    //----------------------------------------
    /**
     * return an array representation of the set
     */
    public Object[] toArray()
    {
	return mValues;
    }

    //----------------------------------------
    /**
     * return an array containing all the elements in the set whose type
     * is that of the specified array
     */
    public Object[] toArray(Object[] pObjects)
    {
	if(pObjects.length == 0)
	    return pObjects;

	Class arrayTypeClass = pObjects[0].getClass();

	if(pObjects.length < mValues.length) {
	    Vector objectVector = new Vector(mValues.length);
	    for(int i=0; i < mValues.length; i++) {
		if(arrayTypeClass.isInstance(mValues[i]))
		    objectVector.add(mValues[i]);
	    }
	    return objectVector.toArray();
	}
	else {
	    for(int i=0; i < mValues.length; i++) {
		if(arrayTypeClass.isInstance(mValues[i]))
		    pObjects[i] = mValues[i];
	    }
	    return pObjects;
	}
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
