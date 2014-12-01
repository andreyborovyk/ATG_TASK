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
 * An implementation of the Map interface that
 * doesn't sort, but does maintain the order of
 * its keys and elements. Mutating this Map in any
 * way by removing or adding mappings will not be
 * reflected in the ordered keys and elements originally
 * used.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/OrderedMap.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class OrderedMap
    implements Map
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/OrderedMap.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    // set containing ordered keys
    private Set mKeys;

    // map containing original ordering
    private Map mUnorderedMap;

    // set containing ordered values
    private Set mValues;

    // should the values variable be set?
    private boolean mValuesAreSet;

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof OrderedMap
     * @param pKeys an ordered set of keys corresponding
     * to keys contained in an ordered map
     * @param pMap the map with unordered keys corresponding
     * to the ordered keys in pKeys
     */
    public OrderedMap(Set pKeys, Map pMap)
    {
	mKeys = pKeys;
	mUnorderedMap = pMap;
	mValuesAreSet = false;
    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * clear this map
     */
    public void clear()
    {
	mUnorderedMap.clear();
    }

    //----------------------------------------
    /**
     * test to see if a key is in the map
     */
    public boolean containsKey(Object pKey)
    {
	return mUnorderedMap.containsKey(pKey);
    }

    //----------------------------------------
    /**
     * test to see if a value is in the map
     */
    public boolean containsValue(Object pValue)
    {
	return mUnorderedMap.containsValue(pValue);
    }

    //----------------------------------------
    /**
     * test to see if this map is equal to another
     */
    public boolean equals(Object pObject)
    {
	return mUnorderedMap.equals(pObject);
    }

    //----------------------------------------
    /**
     * get the value corresponding to the key
     */
    public Object get(Object pKey)
    {
	return mUnorderedMap.get(pKey);
    }

    //----------------------------------------
    /**
     * get the hashcode for this map
     */
    public int hashCode()
    {
	return mUnorderedMap.hashCode();
    }

    //----------------------------------------
    /**
     * test to see if this map is empty
     */
    public boolean isEmpty()
    {
	return mUnorderedMap.isEmpty();
    }

    //----------------------------------------
    /**
     * return a set view of the keys in this map
     */
    public Set keySet()
    {
	return mKeys;
    }

    //----------------------------------------
    /**
     * insert a mapping
     */
    public Object put(Object pKey, Object pValue)
    {
	return mUnorderedMap.put(pKey, pValue);
    }

    //----------------------------------------
    /**
     * copy a map into this map
     */
    public void putAll(Map pMap)
    {
	mUnorderedMap.putAll(pMap);
    }

    //----------------------------------------
    /**
     * remove a mapping
     */
    public Object remove(Object pKey)
    {
	return mUnorderedMap.remove(pKey);
    }

    //----------------------------------------
    /**
     * return the number of key-value pairs in this map
     */
    public int size()
    {
	return mUnorderedMap.size();
    }

    //----------------------------------------
    /**
     * return a set view of the mappings in this in this map
     */
    public Set entrySet()
    {
	return mUnorderedMap.entrySet();
    }

    //----------------------------------------
    /**
     * return a collection containing all the values in this map
     */
    public Collection values()
    {
	if(mValuesAreSet == false)
	    sortValues();

	return mValues;
    }

    //----------------------------------------
    /**
     * resort the values set according to the keys set
     */
    protected void sortValues()
    {
	Iterator keysEnum = mKeys.iterator();
	Vector valuesVector = new Vector(size());

	while(keysEnum.hasNext()) {
	    valuesVector.add(mUnorderedMap.get(keysEnum.next()));
	}

	mValues = new OrderedSet(valuesVector.toArray());

	mValuesAreSet = true;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
