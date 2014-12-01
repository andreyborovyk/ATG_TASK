/*<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
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

package atg.service.cache;

import java.util.*;
import java.io.*;

import atg.nucleus.GenericService;


/**
 *
 * This service is a general-purpose LRU cache.
 * <p>
 * Clients of this service must first implement <code>CacheAdapter</code>,
 * which will be called by this class to maintain elements as related to
 * keys. The Cache class will then take care of maintaining the cache.
 * <p>
 * Most recent changes address multithreading problems.  Specifically, when multiple threads are
 * all requesting the object for a single key for the first time, the adapter is only asked to do the work once.
 * Likewise, the same object is guaranteed to be returned to each thread in this scenario.<p>
 * 
 * This cache class may be used in non-pass-through manner, by simply not setting the cacheAdapter
 * property.  Memory restrictions will be ignored, and get() calls will return null 
 *
 * Previously, if multiple threads all asked for an object that wasn't in cache and all used the same key,
 * each thread would receive functionally similar but unique objects from the cache.  This is a performance hit,
 * and can also cause havoc with applications that expect all threads to receive a single object from cache.
 *
 * @author Lew Miller (stolen from Nathan Abramson)
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/service/cache/Cache.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo
 *   description: Generic LRU Cache
 *   attribute: functionalComponentCategory Caching
 *   attribute: featureComponentCategory
 **/

public class Cache extends GenericService implements Serializable
{
  private static final long serialVersionUID = -6528156632490377511L;

  //-------------------------------------
  // Class version string

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/service/cache/Cache.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Cache entry

  static class Entry
  {
    Object key;
    Object value;
    Entry prev;
    Entry next;
    long creationTime;

    public Entry ()
    {
      // bean constructor
    }

    public Entry (Object key,
                  Object value)
    {
      this (key, value, null, null);
    }

    public Entry (Object key,
                  Object value,
                  Entry prev,
                  Entry next)
    {
      this.key = key;
      this.value = value;
      this.prev = prev;
      this.next = next;
      this.creationTime = System.currentTimeMillis ();
    }

    public void remove ()
    {
      if (prev != null) prev.next = next;
      if (next != null) next.prev = prev;
      prev = null;
      next = null;
    }

    public void insertBefore (Entry elem)
    {
      if (next != elem) {
        remove ();
        prev = elem.prev;
        next = elem;
        if (prev != null) prev.next = this;
        if (next != null) next.prev = this;
      }
    }


  } // end class Entry

    /**
     * Object used to wait for a thread to finish getting a value.
     *  tip o' the hat to Jim Frost
     */
    class ValueWaiter
    {
  private boolean haveValue;
  private Object value;

  /**
   * Waits for a value to be set.
   */
  public synchronized Object waitForValue()
  {
      if (!haveValue) {
    try {
        wait();
    } catch (InterruptedException e) {
        //never happens, not useful.
    }
      }
      return value;
  }

  /**
   * Sets a value and notifies every waiting thread.
   */
  public synchronized void setValue(Object newValue)
  {
      value = newValue;
      haveValue = true;
      notifyAll();
  }
    }

    /**
     * this method just gets an object from a cacheadapter,
     * but in addition sets waiter semaphores to prevent the duplication
     * of cache adapter work.
     *
     * @param pKey the key to use for fetching from the CacheAdapter
     * @param pCacheIt whether to put the object in the cache.
     *
     * @return the object from the appropriate cacheAdapter
     */
    public Object setWaiterAndGetValue(Object pKey, boolean pCacheIt) throws Exception {

      
      if (mAdapter == null) return null;
      
  // No valid entry in the cache corresponding to the given key,
  // so get one from the adapter, put it in the cache and then
  // return it.

  boolean doFetch;
  Object value = null;
  ValueWaiter waitObject = null;
  synchronized (inProgress) {
      waitObject = (ValueWaiter) inProgress.get(pKey);
      if (waitObject == null) { // we're the first
    doFetch = true;
    waitObject = new ValueWaiter();
    inProgress.put(pKey, waitObject);
      }
      else
    doFetch = false;
  }

  if (doFetch) {
      try {
    value = mAdapter.getCacheElement(pKey);

    if (pCacheIt) {
        if (value != null) // populate the cache, if possible
      put (pKey, value);
    }
      } finally {
    inProgress.remove(pKey); // clear in-progress flag
    waitObject.setValue(value); // wake up waiting threads
      }
  }
  else // someone's already looking, wait for them to finish
      value = waitObject.waitForValue();


  return value;

    }


  //-------------------------------------
  // Properties

  /** The adapter which knows how to get objects not found in the cache */
    CacheAdapter mAdapter = null;

    /** A removal listener that gets called when objects are booted from cache,
     * in addition to the call to the CacheAdapter */
    CacheRemovalListener mRemovalListener = null;

  /**
    The maximum number of entries in the cache.<br>
    0 = Cache nothing, but continue to get objects from the CacheAdapter<br>
   -1 = Unlimited */
    int mMaximumCacheEntries = -1;

  /**
    The maximum number of bytes in the cache.<br>
    0 = Cache nothing, but continue to get objects from the CacheAdapter<br>
   -1 = Unlimited */
      int mMaximumCacheSize = -1;

  /**
    The maximum number of bytes in a single cache entry.<br>
    0 = Cache nothing, but continue to get objects from the CacheAdapter<br>
   -1 = Unlimited */
    int mMaximumEntrySize = -1;

  /**
    The maximum number of msec that an entry will live in the cache.<br>
    0 = Cache nothing, but continue to get objects from the CacheAdapter<br>
   -1 = Unlimited */
   long mMaximumEntryLifetime = -1;

   /**
    * Whether this cache should invalidate by part of the key.  When set to true, MultiPartCacheKey 
    * objects should be passed in as keys.
    */
   boolean mEnablePartialKeyInvalidation = false;

   /**
    * The number of key parts this cache expects.  Only used if enablePartialKeyInvalidation == true;
    */
   int mNumKeyParts = 0;
   
   /**
    * Which key parts should be used for invalidation.  For example, setting to [0, 2, 3] would use 
    * first, third, and fourth key parts for invalidation.  Leaving this null means that all parts
    * are available for invalidation.  Empty array means no parts are available.
    */
   int [] mKeyPartsForInvalidation = null;

  //-------------------------------------
  // Statistics

  /** The number of accesses */
  int mAccessCount = 0;

  /** The number of hits */
  int mHitCount = 0;

  /** The number of invalidations due to timeouts */
  int mTimeoutCount = 0;

  /** The number of bytes taken up in memory */
  int mUsedMemory = 0;

  //-------------------------------------
  // Member variables

  /** The mapping from key to value */
  UnhashcodingHashtable mMap = new UnhashcodingHashtable();

  /** The partial key mapping.  Array of partial key -> HashSet hashtables.  HashSets contain full key */
  UnhashcodingHashtable [] mKeyPartMaps = null;

  /** list of what's being d/l'ed */
  Hashtable inProgress = new Hashtable();

  /** The LRU */
  Entry mLRU = new Entry ();

  /** The MRU */
  Entry mMRU = new Entry ();
  
  //-------------------------------------
  /**
   *
   * Constructor
   **/
  public Cache ()
  {
    mLRU.next = mMRU;
    mMRU.prev = mLRU;
  }

  //-------------------------------------


  /**
   * Checks for the presence of the object with the specified key, without
   * invoking the CacheAdapter.  No exceptions!
   */
  public boolean contains(Object pKey){

      return mMap.containsKey(pKey);
  }

  /**
   * Returns false unless all specified keys refer to objects in the cache.
   */
  public boolean contains(Object[] pKeys){
      if(null == pKeys)
    return false;

      for(int i = 0; i < pKeys.length; i++) {

    if(false == mMap.containsKey(pKeys[i]))
       return false;
      }

      return true;
  }

    /**
     * meant to be a debugging aid, will print current key/value pairs the mMap hashtable
     * to the supplied PrintStream, a prime example of which is System.out
     */

    public void dump(PrintStream aStream)
    {
  if(null == aStream){
      return;
  }

  String buffer = dump();
  aStream.print(buffer);
  aStream.flush();
    }

    public String dump()
    {
  String retString = "";

  Enumeration keys = mMap.keys();

  // output will be a stream of keys & values, delimited by carriage returns ('\r')
  // which may seem odd, the intent is that it's a unique enough character which
  // can be parsed out if the desire is to do any further formatting
  //
  while(keys.hasMoreElements()){

      Object key = keys.nextElement();
      Entry entry = (Entry)mMap.get(key);
      retString = retString + key + "\n" + entry.value + "\n";
  }

  System.out.println(retString);
  return retString;
    }


  /**
   * Searches the cache for the object with the specified key.  If not
   * found, then it uses the CacheAdapter to retrieve one.  Returns
   * null if unable to obtain the cached object.
   **/

  public Object get(Object pKey) throws Exception
  {
    Entry e;
    incrementAccessCount();

    // Check to see if this cache is truly caching anything.
    // If not, pass directly through to the CacheAdapter.
    if (mMaximumEntryLifetime == 0 || mMaximumEntrySize == 0 ||
        mMaximumCacheSize == 0 || mMaximumCacheEntries == 0)
      if (mAdapter != null)
        return setWaiterAndGetValue(pKey, false);
      else 
        return null;

    int hashCode = pKey.hashCode ();

    synchronized(this)
    {
      e = (Entry) mMap.get (pKey, hashCode);

      // See if invalidated by time
      if (e != null &&
          getMaximumEntryLifetime() > 0 &&
          System.currentTimeMillis() >=
          e.creationTime + getMaximumEntryLifetime())
      {
        if (isExpiredValueStillGood(pKey, e.value)) {
          e.creationTime = System.currentTimeMillis();
        }
        else {
          incrementTimeoutCount ();
          remove (pKey);
          e = null;
        }
      }

      // If we found a valid entry in the cache, increment the hit
      // count, update the LRU list, and return it.
      if (e != null)
      {
        incrementHitCount ();
        e.insertBefore (mMRU);
        return e.value;
      }

    } // end synchronized


    if (mAdapter != null) return setWaiterAndGetValue(pKey, true);
    else return null;

  } // end get


  /**
    Searches the cache for the objects with the specified keys.  Any
    not found in the cache are retrieved by the CacheAdapter.  Returns
    an array of all retrieved items (null for any which could not be
    retrieved).  The array of objects returned is ordered to
    correspond with the order of the array of keys passed in.
   **/
  public Object[] get(Object[] pKeys) throws Exception
  {

    /*

    NOTE: NEED TO FIX THIS MULTI-GET JUST LIKE THE SINGLE GET- TO
    USE SETWAITERANDGETVALUE

    */


    // Check to see if this cache is truly caching anything.
    // If not, pass directly through to the CacheAdapter.
    if (mMaximumEntryLifetime == 0 || mMaximumEntrySize == 0 ||
        mMaximumCacheSize == 0 || mMaximumCacheEntries == 0)
    {
      for (int i = 0; i < pKeys.length; i++)
        incrementAccessCount();
      if (mAdapter != null)
        return mAdapter.getCacheElements(pKeys);
      else return null;
    }


    Hashtable resultTable = new Hashtable();
    Vector missedKeys = new Vector();
    Object[] results = new Object[pKeys.length];

    for (int i = 0; i < pKeys.length; i++)
    {
      Entry e;
      Object key = pKeys[i];

      incrementAccessCount ();

      synchronized(this)
      {
        e = (Entry) mMap.get (key);

          // See if invalidated by time
        if (e != null &&
            getMaximumEntryLifetime() > 0 &&
            System.currentTimeMillis() >=
            e.creationTime + getMaximumEntryLifetime())
        {
          if (isExpiredValueStillGood(key, e.key)) {
            e.creationTime = System.currentTimeMillis();
          }
          else {
            incrementTimeoutCount ();
            remove (key);
            e = null;
          }
        }

        // If we found a valid entry in the cache, increment the hit
        // count, update the LRU list, and the entry to the results table.
        if (e != null)
        {
          incrementHitCount ();
          e.insertBefore (mMRU);
          resultTable.put(key, e.value);
        }

      } // end synchronized

      // No valid entry corresponding to this key in the cache so add
      // it to the list of missed keys.
      if (e == null)
        missedKeys.addElement(key);

    } // end for


    if (missedKeys.size() > 0 && mAdapter != null)
    {
      // Use the adapter to retrieve any items not found in cache and
      // add them to the results table.  Adapter implementations must
      // return the values in the order of the keys passed in.
      Object[] keys = new Object[missedKeys.size()];
      missedKeys.copyInto(keys);

      Object[] vals = mAdapter.getCacheElements(keys);

      for (int i = 0; i < vals.length; i++) {
        if (vals[i] != null) {
          put (keys[i], vals[i]);
          resultTable.put(keys[i], vals[i]);
        }
      }
    }

    for (int i = 0; i < pKeys.length; i++)
    {
      // Null is returned for any key for which no corresponding
      // value was found, to maintain the one-to-one correspondence
      // between elements in the input array of keys and the elements
      // of the output array of values.
      Object val = resultTable.get(pKeys[i]);
      results[i] = val;

      // put is synchronized and updates the LRU list.
      if (val != null)
        put (pKeys[i], val);
    }

    return results;

  } // end multi-get


  //------------------------------------- 
  /** Return whether the expired value is still good. May be overridden
   * by subclasses.
   * @param pKey the key the corresponds to the value
   * @param pValue the value to check
   */
  protected  boolean isExpiredValueStillGood(Object key, Object pValue) {
    return false;
  }


  //-------------------------------------
  /**
   * Puts an entry into the cache, placing that entry at the front of
   * the LRU list.
   **/
  public synchronized void put(Object pKey, Object pValue)
  {
    // Check to see if this cache is truly caching anything.
    // If not, simply return.
    if (mMaximumEntryLifetime == 0 || mMaximumEntrySize == 0 ||
        mMaximumCacheSize == 0 || mMaximumCacheEntries == 0)
      return;

    // if we are doing multi-part key caching, check to see if this key is legitimate
    if (mEnablePartialKeyInvalidation 
        && MultiPartCacheKey.class.isAssignableFrom(pKey.getClass())
        && ((MultiPartCacheKey) pKey).getKeyPartCount() != mNumKeyParts)
      throw new IllegalArgumentException("atg.service.cache.MultiPartCacheKey: attempt to add a key with too many or too few parts");
    
    remove (pKey, pValue);
    Entry e = new Entry (pKey, pValue);

    // Check entry size
    if (getMaximumEntrySize () > 0 && mAdapter != null)
    {
      int memsize = mAdapter.getCacheElementSize(pValue, pKey) +
                    mAdapter.getCacheKeySize(pKey);
      if (memsize > getMaximumEntrySize ())
        return;
    }

    // Check total cache size
    if (getMaximumCacheSize () > 0 && mAdapter != null)
    {
      int memsize = mAdapter.getCacheElementSize(pValue, pKey) +
                    mAdapter.getCacheKeySize(pKey);
      if (memsize > getMaximumCacheSize ()) return;

      while (getUsedMemory () + memsize > getMaximumCacheSize ())
      {
        Entry lru = mLRU.next;
        if (lru == mMRU) return;
        remove (lru.key);
      }

      mUsedMemory += memsize;
    }

    // Check total number of cache entries
    if (getMaximumCacheEntries () > 0)
    {
      while (getNumCacheEntries() + 1 > getMaximumCacheEntries ())
      {
        Entry lru = mLRU.next;
        if (lru == mMRU) return;
        remove (lru.key);
      }
    }

    mMap.put (pKey, e);
    e.insertBefore (mMRU);

    // add to any partial key maps
    if (mEnablePartialKeyInvalidation && 
        MultiPartCacheKey.class.isAssignableFrom(pKey.getClass()) &&
        mNumKeyParts > 0 && 
        (mKeyPartsForInvalidation == null || mKeyPartsForInvalidation.length > 0)) {
      
      // create base key part maps if necessary
      if (mKeyPartMaps == null) {
        mKeyPartMaps = new UnhashcodingHashtable [mNumKeyParts];
        
        for (int i = 0; i < mKeyPartMaps.length; i++)
          mKeyPartMaps[i] = null;
        
        if (mKeyPartsForInvalidation == null) {
          for (int i = 0; i < mKeyPartMaps.length; i++)
            mKeyPartMaps[i] = new UnhashcodingHashtable();
        } else
          for (int i = 0; i < mKeyPartsForInvalidation.length; i++)
            if (mKeyPartsForInvalidation[i] >= 0 && mKeyPartsForInvalidation[i] < mKeyPartMaps.length)
              mKeyPartMaps[mKeyPartsForInvalidation[i]] = new UnhashcodingHashtable();
      }
      
      // put key in key part maps
      MultiPartCacheKey multiKey = (MultiPartCacheKey) pKey;
      for (int i = 0; i < mKeyPartMaps.length; i++)
        if (mKeyPartMaps[i] != null) {
          Object partialKey = multiKey.getKeyPart(i);
          Set mappedSet = (Set) mKeyPartMaps[i].get(partialKey);
          if (mappedSet == null) {
            mappedSet = new HashSet();
            mKeyPartMaps[i].put(partialKey, mappedSet);
          }
          mappedSet.add(pKey);
        }
    }
  }


  //-------------------------------------
  /**
   * Invalidates the entry with the given key.
   * (Called on get when the entry has timed out, on put if the entry
   * is pre-existing in the cache, and also on put for each lru entry
   * until max size and max entries requirements are met.)
   * @return true if the item was found and removed 
   **/
  public boolean remove (Object pKey)
  {
    return remove(pKey, null);
  }
  
  //-------------------------------------
  /**
   * Invalidates the entry with the given key.
   * (Called on get when the entry has timed out, on put if the entry
   * is pre-existing in the cache, and also on put for each lru entry
   * until max size and max entries requirements are met.)
   * @return true if the item was found and removed 
   **/
  public synchronized boolean remove (Object pKey, Object pNewValue)
  {
    Entry e = (Entry) mMap.remove (pKey);
    if (e != null)
    {
      if (mAdapter != null)
        mUsedMemory -= mAdapter.getCacheElementSize(e.value, e.key) +
           mAdapter.getCacheKeySize(e.key);
   
      e.remove ();
      
      // remove from any partial key mappings
      if (mEnablePartialKeyInvalidation && 
          MultiPartCacheKey.class.isAssignableFrom(pKey.getClass()) &&
          mKeyPartMaps != null &&
          ((MultiPartCacheKey) pKey).getKeyPartCount() == mKeyPartMaps.length) {
        
        MultiPartCacheKey multiKey = (MultiPartCacheKey) pKey;
        for (int i = 0; i < mKeyPartMaps.length; i++)
          if (mKeyPartMaps[i] != null) {
            Object partialKey = multiKey.getKeyPart(i);
            Set mappedSet = (Set) mKeyPartMaps[i].get(partialKey);
            mappedSet.remove(pKey);
            if (mappedSet.isEmpty())
              mKeyPartMaps[i].remove(partialKey);
          }
      }

      // The adapter *may* want to do some extra clean up when
      // elements are removed.
      if (mAdapter != null) mAdapter.removeCacheElement(e.value, e.key);
      
      if (mRemovalListener != null) 
        mRemovalListener.notifyCachedObjectRemoved(e.key, e.value, pNewValue);
      
      return true;
    }
    else return false;
    
  }

  //-------------------------------------
  /**
   * Flushes all entries from the cache, leaving it empty.
   **/
  public synchronized void flush()
  {
    mMap.clear ();
    mLRU.next = mMRU;
    mMRU.prev = mLRU;
    mUsedMemory = 0;
  }

  //-------------------------------------
  /**
   * Removes by partial key.
   * @param pPartialKeyIndex index of the partial key to remove, should be >= 0 and < numKeyParts
   * @param pPartialKeyValue value of the partial key to remove
   * @return the number of items removed
   **/
  public synchronized int removeByPartialKey(int pPartialKeyIndex, Object pPartialKeyValue) {
    
    if (!mEnablePartialKeyInvalidation || mKeyPartMaps == null || pPartialKeyValue == null) return 0;
    
    if (pPartialKeyIndex < 0 || pPartialKeyIndex >= mKeyPartMaps.length)
      throw new IllegalArgumentException("atg.service.cache.MultiPartCacheKey: attempt to invalidate with a key with too many or too few parts");
      
    if (mKeyPartMaps[pPartialKeyIndex] == null)
      return 0;
      
    Set keySet = (Set) mKeyPartMaps[pPartialKeyIndex].get(pPartialKeyValue);
    if (keySet == null) return 0;

    // local copy of the Set to avoid removing from something we are iterating through
    keySet = new HashSet(keySet);
    
    Iterator keyIter = keySet.iterator();
    int removeCount = 0;
    while (keyIter.hasNext()) {
      Object key = keyIter.next();
      if (remove(key)) removeCount++;
    }
    
    return removeCount;
  }

  //-------------------------------------
  // Properties
  //-------------------------------------

  /**
   * The Adapter used to create entries in the cache
   * @beaninfo
   *   description: The Adapter used to create entries in the cache
   */
  public CacheAdapter getCacheAdapter()
  {
    return mAdapter;
  }

  /**
   * The Adapter used to create entries in the cache
   * @beaninfo
   *   description: The Adapter used to create entries in the cache
   */
  public void setCacheAdapter(CacheAdapter pAdapter)
  {
    mAdapter = pAdapter;
  }

  /**
   * The CacheRemovalListener notified when objects are removed from this cache
   * @beaninfo
   *   description: The CacheRemovalListener notified when objects are removed from this cache
   */
  public CacheRemovalListener getCacheRemovalListener()
  {
    return mRemovalListener;
  }

  /**
   * The CacheRemovalListener notified when objects are removed from this cache
   * @beaninfo
   *   description: The CacheRemovalListener notified when objects are removed from this cache
   */
  public void setCacheRemovalListener(CacheRemovalListener pRemovalListener)
  {
    mRemovalListener = pRemovalListener;
  }

  //-------------------------------------
  /**
   * Returns the maximum number of elements in the cache.<br>
   * 0 = Cache nothing, but continue to get objects from the CacheAdapter<br>
   * -1 = Unlimited
   **/
  public int getMaximumCacheEntries ()
  {
    return mMaximumCacheEntries;
  }

  //-------------------------------------
  /**
   * Sets the maximum number of elements in the cache. <br>
   * 0 = Cache nothing, but continue to get objects from the CacheAdapter<br>
   * -1 = Unlimited
   * @beaninfo
   *   description: Sets the maximum number of elements in the cache.(0=no caching, -1=unlimited)
   */
  public void setMaximumCacheEntries (int pMaximumCacheEntries)
  {
    mMaximumCacheEntries = pMaximumCacheEntries;
  }

  //-------------------------------------
  /**
   * Returns the maximum memory size of the cache. <br>
   * 0 = Cache nothing, but continue to get objects from the CacheAdapter<br>
   * -1 = Unlimited
   **/
  public int getMaximumCacheSize ()
  {
    return mMaximumCacheSize;
  }

  //-------------------------------------
  /**
   * Sets the maximum memory size of the cache. <br>
   * 0 = Cache nothing, but continue to get objects from the CacheAdapter<br>
   * -1 = Unlimited
   * @beaninfo
   *   description: Sets the maximum memory size of the cache (0=no caching, -1=unlimited)
   *
   */
  public void setMaximumCacheSize (int pMaximumCacheSize)
  {
    mMaximumCacheSize = pMaximumCacheSize;
  }

  //-------------------------------------
  /**
   * Returns the maximum memory size of a single entry in the cache. <br>
   * 0 = Cache nothing, but continue to get objects from the CacheAdapter<br>
   * -1 = Unlimited
   **/
  public int getMaximumEntrySize ()
  {
    return mMaximumEntrySize;
  }

  //-------------------------------------
  /**
   * Sets the maximum memory size of a single entry in the cache. <br>
   * 0 = Cache nothing, but continue to get objects from the CacheAdapter<br>
   * -1 = Unlimited
   * @beaninfo
   *   description: Sets the maximum memory size of an entry in the cache (0=no caching, -1=unlimited)
   *
   */
  public void setMaximumEntrySize (int pMaximumEntrySize)
  {
    mMaximumEntrySize = pMaximumEntrySize;
  }

  //-------------------------------------
  /**
   * Returns the maximum number of msec that an entry will live in the cache.<br>
   * 0 = Cache nothing, but continue to get objects from the CacheAdapter<br>
   * -1 = Unlimited
   **/
  public long getMaximumEntryLifetime ()
  {
    return mMaximumEntryLifetime;
  }

  //-------------------------------------
  /**
   * Sets the maximum number of msec that an entry will live in the cache.<br>
   * 0 = Cache nothing, but continue to get objects from the CacheAdapter<br>
   * -1 = Unlimited
   * @beaninfo
   *   description: Sets the maximum number of msec that an entry will live in the cache.
   *                (0=cache nothing, -1=unlimited)
   */
  public void setMaximumEntryLifetime (long pMaximumEntryLifetime)
  {
    mMaximumEntryLifetime = pMaximumEntryLifetime;
  }

  //-------------------------------------
  /**
    * Whether this cache should invalidate by part of the key.  When set to true, MultiPartCacheKey 
    * objects should be passed in as keys.
   **/
  public boolean getEnablePartialKeyInvalidation ()
  {
    return mEnablePartialKeyInvalidation;
  }

  //-------------------------------------
  /**
   * Whether this cache should invalidate by part of the key.  When set to true, MultiPartCacheKey 
   * objects should be passed in as keys.
   * @beaninfo
   *   description: whether this cache should invalidate by part of the key.
   */
  public void setEnablePartialKeyInvalidation (boolean pEnablePartialKeyInvalidation)
  {
    mEnablePartialKeyInvalidation = pEnablePartialKeyInvalidation;
  }
  
  //-------------------------------------
  /**
    * The number of key parts this cache expects.
    * Only used if enablePartialKeyInvalidation == true;
   **/
  public int getNumKeyParts ()
  {
    return mNumKeyParts;
  }

  //-------------------------------------
  /**
   * The number of key parts this cache expects.
   * Only used if enablePartialKeyInvalidation == true;
   * @beaninfo
   *   description: the number of key parts this cache expects
   */
  public void setNumKeyParts (int pNumKeyParts)
  {
    mNumKeyParts = pNumKeyParts;
  }
  
  //-------------------------------------
  /**
    * Which key parts should be used for invalidation.  For example, setting to [0, 2, 3] would use 
    * first, third, and fourth key parts for invalidation.  Leaving this null means that all parts
    * are available for invalidation.  Empty array means no parts are available.
   **/
  public int [] getKeyPartsForInvalidation ()
  {
    return mKeyPartsForInvalidation;
  }

  //-------------------------------------
  /**
   * Which key parts should be used for invalidation.  For example, setting to [0, 2, 3] would use 
   * first, third, and fourth key parts for invalidation.  Leaving this null means that all parts
   * are available for invalidation.  Empty array means no parts are available.
   * @beaninfo
   *   description: which key parts should be used for invalidation
   */
  public void setKeyPartsForInvalidation (int [] pKeyPartsForInvalidation)
  {
    mKeyPartsForInvalidation = pKeyPartsForInvalidation;
  }
  
  //-------------------------------------
  // Statistics
  //-------------------------------------
  /**
  *
  * Returns the number of entries currently in the cache
  * @beaninfo
  *   description: Returns the number of entries currently in the cache
  */
  public int getSize ()
  {
    return getNumCacheEntries();
  }
  
  /**
   *
   * Returns the number of entries currently in the cache
   * @beaninfo
   *   description: Returns the number of entries currently in the cache
   */
  public int getNumCacheEntries ()
  {
    return mMap.size ();
  }

  //-------------------------------------
  /**
   * Returns the ratio of cache entries to maximum cache entries.
   * @beaninfo
   *  description: Returns the ratio of cache entries to maximum cache entries.
   */
  public double getUsedCapacity ()
  {
    if (getMaximumCacheEntries () == 0) return 0.0;
    else return (((double) getNumCacheEntries ()) /
                 ((double) getMaximumCacheEntries ()));
  }

  //-------------------------------------
  /**
   * Returns the amount of memory taken up by the entries in the cache
   * (includes keys).
   * @beaninfo
   *  description: Returns the amount of memory taken up by the entries in the cache
   * (includes keys).
   */
  public int getUsedMemory ()
  {
    return mUsedMemory;
  }

  //-------------------------------------
  /**
   *
   * Returns the ratio of memory size to maximum memory size
   * @beaninfo
   *   description: Returns the ratio of memory size to maximum memory size
   */
  public double getUsedMemoryCapacity ()
  {
    if (getMaximumCacheSize () == 0) return 0.0;
    else return (((double) getUsedMemory ()) /
                 ((double) getMaximumCacheSize ()));
  }

  //-------------------------------------
  /**
   *
   * Increments the accessCount statistic
   **/
  synchronized void incrementAccessCount ()
  {
    mAccessCount++;
    // Need not be synchronized if not bothering with this?
    //if (getCacheManager () != null)
    //  getCacheManager ().incrementAccessCount ();
  }

  //-------------------------------------
  /**
   *
   * Returns the total number of accesses
   * @beaninfo
   *   description: Returns the total number of accesses
   */
  public int getAccessCount ()
  {
    return mAccessCount;
  }

  //-------------------------------------
  /**
   *
   * Increments the hitCount statistic
   */
  synchronized void incrementHitCount ()
  {
    mHitCount++;
    // Need not be synchronized if not bothering with this?
    //if (getCacheManager () != null)
    //  getCacheManager ().incrementHitCount ();
  }

  //-------------------------------------
  /**
   *
   * Returns the number of cache hits
   * @beaninfo
   *   description: Returns the number of cache hits
   **/
  public int getHitCount ()
  {
    return mHitCount;
  }

  //-------------------------------------
  /**
   *
   * Returns the number of cache hits / total number of cache accesses
   * @beaninfo
   *   description: Returns the number of cache hits / total number of cache accesses
   **/
  public double getHitRatio ()
  {
    if (getAccessCount () == 0) return 0.0;
    else return (((double) getHitCount ()) / ((double) getAccessCount ()));
  }

  //-------------------------------------
  /**
   *
   * Increments the timeoutCount statistic
   **/
  synchronized void incrementTimeoutCount ()
  {
    mTimeoutCount++;
    // Need not be synchronized if not bothering with this?
    //if (getCacheManager () != null)
    //  getCacheManager ().incrementTimeoutCount ();
  }

  //-------------------------------------
  /**
   *
   * Returns the number of times entries were invalidated because they
   * were out-of-date.
   * @beaninfo
   *   description: Returns the number of times entries were invalidated because they
   *                were out-of-date.
   **/
  public int getTimeoutCount ()
  {
    return mTimeoutCount;
  }

  //-------------------------------------
  /**
   *
   * Returns all elements in this Cache.
   * @beaninfo
   *   description: Returns all elements in this Cache.
   *               
   **/  
  
  public synchronized Iterator getAllElements(){
   
   HashMap map;
   int size = mMap.size();
   if (size == 0) {
    map = new HashMap(0);
    return map.values().iterator();
   }
   else
   {
     map = new HashMap(size);
   }
   
   Enumeration keys = mMap.keys();
   while(keys.hasMoreElements()){
      Object key = keys.nextElement();
      Entry entry = (Entry)mMap.get(key);
      map.put(key,entry.value);
   }
   
   return map.values().iterator();
  }

  /**
  *
  * Returns all elements in this Cache.
  * @beaninfo
  *   description: Returns all elements in this Cache.
  *               
  **/  
 
 public synchronized Iterator getAllKeys(){

   HashMap map;
   int size = mMap.size();
   if (size == 0) {
    map = new HashMap(0);
    return map.keySet().iterator();
   }
   else
   {
     map = new HashMap(size);
   }
   
   Enumeration keys = mMap.keys();
   while(keys.hasMoreElements()){
      Object key = keys.nextElement();
      Entry entry = (Entry)mMap.get(key);
      map.put(key,entry.value);
   }
   
   return map.keySet().iterator();
 }
 
  /**
  *
  * Returns all elements in this Cache ordered by entry creation time.
  * @beaninfo
  *   description: Returns all elements in this Cache.
  *               
  **/  

 public synchronized Iterator getValidKeysOrdered(boolean pLatestFirst) {
    ArrayList list;
    int size = mMap.size();
    if (size == 0) {
      list = new ArrayList(0);
      return list.iterator();
    } else {
      list = new ArrayList(size);
    }

    synchronized (this) {
      Enumeration keys = mMap.keys();
      while (keys.hasMoreElements()) {
        Object key = keys.nextElement();
          Entry entry = (Entry) mMap.get(key);
          // See if invalidated by time
          if (entry != null
              && getMaximumEntryLifetime() > 0
              && (System.currentTimeMillis() >= entry.creationTime
                  + getMaximumEntryLifetime())) {
            if (!isExpiredValueStillGood(key, entry.value)) {
              entry = null;
            }
          }
          if (entry != null)
            list.add(key);
      }
      Collections.sort(list, new CreationTimeSorter(pLatestFirst));
    }
    return list.iterator();
  }
 
   /**
   *
   * Returns all elements in this Cache ordered by entry creation time.
   * @beaninfo
   *   description: Returns all elements in this Cache.
   *               
   **/  
  
  public synchronized Iterator getAllKeysOrdered(boolean pLatestFirst){
  
    ArrayList list;
    int size = mMap.size();
    if (size == 0) {
      list = new ArrayList(0);
     return list.iterator();
    }
    else
    {
      list = new ArrayList(size);
    }
    synchronized (this) {
      Enumeration keys = mMap.keys();
      while(keys.hasMoreElements()){
         Object key = keys.nextElement();
         list.add(key);
      }
      
      Collections.sort(list, new CreationTimeSorter(pLatestFirst));
    }
    return list.iterator();
  }

  private class CreationTimeSorter implements Comparator {
  
    private final boolean mLatestFirst;

    public CreationTimeSorter(boolean pLatestFirst) {
      mLatestFirst = pLatestFirst;
    }

    public int compare(Object x, Object y) {

      Object k1 = (Object) x;
      Entry e1 = (Entry) mMap.get(k1);
      Object k2 = (Object) y;
      Entry e2 = (Entry) mMap.get(k2);
      
      if (e1.creationTime > e2.creationTime)
        return mLatestFirst ? -1 : 1;
      if (e1.creationTime < e2.creationTime)
        return mLatestFirst ? 1 : -1;
      return 0;
    }
  
  }
  public static void main(String[] args) throws InterruptedException {
    Cache c = new Cache();
    c.setMaximumEntryLifetime(10l*1000l);
    c.put("first", new Integer(1));
    Thread.sleep(1*1000);
    c.put("second", new Integer(2));
    Thread.sleep(1*1000);
    c.put("third",  new Integer(3));
    Thread.sleep(1*1000);
    c.put("fourth", new Integer(4));
    Thread.sleep(1*1000);
    c.put("fifth", new Integer(5));
    Iterator allKeysOrdered1 = c.getValidKeysOrdered(true);
    while (allKeysOrdered1.hasNext()) {
      Object element = (Object) allKeysOrdered1.next();
      System.out.println("Cache1: "+element);
    }
    Iterator allKeysOrdered2 = c.getValidKeysOrdered(false);
    while (allKeysOrdered2.hasNext()) {
      Object element = (Object) allKeysOrdered2.next();
      System.out.println("Cache2: "+element);
    }
  }
} // end class
