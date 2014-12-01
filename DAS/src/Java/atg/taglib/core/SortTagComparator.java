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
import java.beans.*;
import java.lang.reflect.*;

/****************************************
 * A comparator to be used internally by the SortTag. will
 * be dynamically configured based on subtags of the SortTag.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/SortTagComparator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class SortTagComparator
    implements Comparator
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/SortTagComparator.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    // array containing methods
    private Method[] mMethods;

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // SortTag
    private SortTag mSortTag;
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
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof SortTagComparator
     */
    public SortTagComparator(SortTag pSortTag)
    {
	mSortTag = pSortTag;

	mMethods = new Method[mSortTag.getOrderings().size()];
    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * compare two elements
     */
    public int compare(Object pValue1, Object pValue2)
	throws ClassCastException
    {
	// go through each ordering in the sort tag until
	// there is an inequality
	boolean successfulIntrospection;
	Object[] emptyObjectArray = new Object[0];

	for(int i=0; i < mMethods.length; i++) {
	    if(mMethods[i] == null) {
		try {
		    successfulIntrospection = orderingToMethod(pValue1, i);
		}
		catch(IntrospectionException e) {
		    throw new ClassCastException(e.getMessage());
		}
	    }

	    try {
		int result = 0;

		// get the properties of the objects to compare,
		// if the properties are not primitive
		// they must be Comparables, or an exception will be thrown
		Object returnValue1 = mMethods[i].invoke(pValue1, emptyObjectArray);
		Object returnValue2 = mMethods[i].invoke(pValue2, emptyObjectArray);

		if(mMethods[i].getReturnType().getName().equals(java.lang.Boolean.class.getName()) 
		   || mMethods[i].getReturnType().getName().equals("boolean")) {
		    if(returnValue1 instanceof Boolean) {
			boolean booleanValue1 = ((Boolean) returnValue1).booleanValue();
			boolean booleanValue2 = ((Boolean) returnValue2).booleanValue();

			if(booleanValue1 == booleanValue2)
			    result = 0;
			else if(booleanValue1 == false && booleanValue2 == true)
			    result = -1;
			else
			    result = 1;
		    }
		}
		else {
		    // compare the comparables
		    result = ((Comparable) returnValue1).compareTo((Comparable) returnValue2);
		}

		// if non-zero result, return the correct value based on
		// the reverse property of the current ordering
		if(result != 0) {
		    if(((Ordering) mSortTag.getOrderings().get(i)).isReverse()) {
			if(result < 0)
			    return 1;
			else
			    return -1;
		    }
		    else {
			return result;
		    }
		}
		// if zero result, the comparables are "even" so we must go
		// to the next ordering, as long as there are more orderings
		else if(i == (mMethods.length - 1)) {
		    return result;
		}
	    }
	    catch(IllegalAccessException e) {
		throw new ClassCastException(e.getMessage());
	    }
	    catch(IllegalArgumentException e) {
		throw new ClassCastException(e.getMessage());
	    }
	    catch(InvocationTargetException e) {
		throw new ClassCastException(e.getMessage());
	    }
	}

	return 0;
    }

    //----------------------------------------
    /**
     * is this comparator equal to another comparator?
     */
    public boolean equals(Object pComparator)
    {
	return false;
    }

    //----------------------------------------
    /**
     * convert an ordering into a method
     */
    protected boolean orderingToMethod(Object pValue, int pIndex)
	throws IntrospectionException
    {
	BeanInfo beanInfo = Introspector.getBeanInfo(pValue.getClass());
	PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
	Ordering ordering = (Ordering) mSortTag.getOrderings().get(pIndex);
	
	int i;
	for(i=0; i < properties.length; i++) {
	    if(properties[i].getName().equals(ordering.getProperty()))
		break;
	}
	
	if(i < properties.length) {
	    mMethods[pIndex] = properties[i].getReadMethod();
	    return true;
	}
	else {
	    return false;
	}
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
