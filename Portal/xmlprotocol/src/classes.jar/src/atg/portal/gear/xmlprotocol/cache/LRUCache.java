/*<ATGCOPYRIGHT>

 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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
 * Dynamo is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.portal.gear.xmlprotocol.cache;

import java.util.Map;
import java.util.HashMap;

import atg.nucleus.GenericService;

/**
 * Implements a simple LRU caching scheme.
 * This class can handle virtually any type of information by
 * caching objects of classes that extend the abstract class CacheElement.
 * These classes store the cached content in a wrapper class that implements the ElementContents
 * interface.  Cached content can be generic objects such as strings or primitive data such as
 * byte arrays.  The unique key for finding a particular object must extend the
 * ElementKey abstract class
 *
 * The cache uses a doubly-linked list to track least recently used elements.  In this
 * case, each element contains pointers to previous and next elements in the list.
 *
 * @author J Marino
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/cache/LRUCache.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class LRUCache extends GenericService {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/cache/LRUCache.java#2 $$Change: 651448 $";

  /**
   * The maximum size of the cache in bytes
   */
  int mMaxSize;
  public int getMaxSize(){
    return mMaxSize;
  }
  public void setMaxSize(int pSize){
    mMaxSize = pSize;
  }

  /**
   * The current size of the cache in bytes
   */
  int mCacheSize =0;
  public int getCacheSize(){
    return mCacheSize;
  }
  protected void setCacheSize(int pSize){
    mCacheSize = pSize;
  }

  /**
   * The number of elements in the cache
   */
  int mElementCount = 0;
  public int getElementCount(){
    return mElementCount;
  }
  protected void setElementCount(int pSize){
    mElementCount = pSize;
  }

  /**
   * Represents the number of successful element retrievals
   */
  int mHitCount = 0;
  public int getHitCount(){
    return mHitCount;
  }
  protected void setHitCount(int pSize){
    mHitCount = pSize;
  }

  /**
   * Represents the number of unsuccessful element retrievals
   */
  int mMissCount =0;
  public int getMissCount(){
    return mMissCount;
  }
  protected void setMissCount(int pSize){
    mMissCount = pSize;
  }

  //The classname of the elements that are being cached
  String mCacheElementClassName;

  //The actual internal cache container
  HashMap mTheCache;

  //The head and tail of the linked list
  CacheElement mLRUHead;
  CacheElement mLRUTail;

  //the Current position in the linked list
  CacheElement mCurrentPos;

  /**
   * Constructor
   *
   * @param pCacheElementClass the class representing the elements to be cached
   * @param pMaxSize the maximum size of the cache in bytes
   *
   * @exception ClassNotFoundException if the adaptor class could not be found in the classpath
   * @exception IllegalAccessException if the JVM's security policy is violated
   * @exception InstantiationException if the adaptor class could not be instantiated
   */
  public LRUCache(String pCacheElementClass, int pMaxSize) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    this.init(pCacheElementClass, pMaxSize);
  }

  /**
   * Performs internal initialization and re-initialization for the cache.
   *
   * @param pCacheElementClass the class representing the elements to be cached
   * @param pMaxSize the maximum size of the cache in bytes
   *
   * @exception ClassNotFoundException if the adaptor class could not be found in the classpath
   * @exception IllegalAccessException if the JVM's security policy is violated
   * @exception InstantiationException if the adaptor class could not be instantiated
   */
  protected void init(String pCacheElementClass, int pMaxSize) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    mTheCache = new HashMap();
    mCacheElementClassName = pCacheElementClass;
    mLRUHead =  (CacheElement) Class.forName(pCacheElementClass).newInstance();
    mLRUTail =  (CacheElement) Class.forName(pCacheElementClass).newInstance();

    mLRUHead.init(new StringKey("head"), new ByteArrayElementContents(), 0);
    mLRUTail.init(new StringKey("tail"), new ByteArrayElementContents(), 0);

    //make sure the head and tail point to one another since the list is empty to
    //start with

    mLRUHead.setNext(mLRUTail);
    mLRUTail.setPrevious(mLRUHead);
    mCurrentPos = mLRUHead;
    mCacheSize =0;
    mElementCount =0;
    mMaxSize = pMaxSize;
  }

  /**
   * A method to retrieve an element from the cache.
   *
   * @param  pKey is a key representing a unique ID for the element.
   *
   * @returns ElementContents wrapper containing the actual cached content.
   *          We return the contents in a wrapper since they can be primitives
   *          such as a byte[] or objects.  Null is returned if the content is not found
   *          for it has expired.
   *          <code>
   *          The ElementContents object will need to be cast to the
   *          specific wrapper class before use.  An example is:
   *
   *          LRUCache lru =
   *              new LRUCache("atg.portal.gear.xmlprotocol.CacheByteArrayElement", 100000);
   *          StringKey theKey = new StringKey("key1");
   *          byte[] bytes = {'t','e','s','t'};
   *
   *          CacheByteArrayElement bae =
   *              (CacheByteArrayElement) lru.get(theKey);
   *          ByteArrayElementContents bace = bae.getContents();
   *          byte[] theOutput = bace.getContents();
   *          </code>
   *
   */

   public synchronized CacheElement get(ElementKey pKey){
     CacheElement theElement = (CacheElement) mTheCache.get(pKey);
     if (theElement == null){
       mMissCount++;
       return null;
     }
     if (theElement.isExpired()){
       //mTheCache.remove(pKey);
       this.remove(pKey);
       mMissCount++;
       return null;
     }else{
       //This element is now the most recently used...
       this.removeFromList(theElement);
       this.putTop(theElement);
       mHitCount++;
       return theElement;   //.getContents();
     }
  }

  /**
   * A method to place an element in the cache
   *
   * @param pKey representing a unique id for the element
   * @param pExpirationTime the absolute time in milliseconds
   *        when the element will expire.
   *
   */
   public synchronized void put(ElementKey pKey, ElementContents pContents, long pExpirationTime){
     try{
       //First, if the element is already there remove it since we need to update
       //the content.
       this.remove(pKey);

       //Create a new instance of the element wrapper
       CacheElement theElement = (CacheElement) Class.forName(mCacheElementClassName).newInstance();
       theElement.init(pKey, pContents, pExpirationTime);

       //check the size of the cache - if the size of the element is less than the
       //max cache size, add the element
       if (theElement.getSize() <= mMaxSize){
         mTheCache.put(pKey, theElement);
         this.putTop(theElement);
         mCacheSize += theElement.getSize();
         mElementCount++;
       }
       //get rid of the LRU elements until we are back under the max cache size
       while (mCacheSize > mMaxSize){
         theElement = mLRUTail.getPrevious();
         if (theElement == mLRUHead){
           break;
         }else if (theElement == null){
           break;
         }
         this.remove(theElement.getKey());
       }
     }catch (Exception e){
       this.logError(e);
       e.printStackTrace();
     }
  }

  /**
   * Removes an element from the cache
   *
   * @param pKey the key of the element to remove
   */
  public synchronized void remove(ElementKey pKey){
    CacheElement theElement = (CacheElement) mTheCache.remove(pKey);
    if (theElement != null){
      this.removeFromList(theElement);
      mCacheSize -= theElement.getSize();
      mElementCount--;
    }
  }

  /**
   * Removes all cache elements and reinitializes it.
   */
  public synchronized void removeAll() {
    try {
      this.init(mCacheElementClassName, mMaxSize);
    }catch (Exception e){  //This should not happen!
      this.logError(e);
    }
  }

  //-------------------------------------
  // Utility Methods

  /**
   * Puts the element at the top of the LRU doubly linked list (i.e. most recently used)
   *
   * @param pElement the element to cache
   */
  protected void putTop(CacheElement pElement){
    if (mLRUHead != null){
      pElement.setNext(mLRUHead.getNext());
      mLRUHead.getNext().setPrevious(pElement);
      mLRUHead.setNext(pElement);
    }
    pElement.setPrevious(mLRUHead);
  }

  /**
   * Removes an element from the LRU doubly linked list
   *
   * @param pElement the element to remove
   */
  protected synchronized void removeFromList(CacheElement pElement){
    if (pElement.getNext() != null){
      pElement.getNext().setPrevious(pElement.getPrevious());
    }
    if (pElement.getPrevious() != null){
      pElement.getPrevious().setNext(pElement.getNext());
    }
    pElement.setNext(null);
    pElement.setPrevious(null);
   }

    /**
     * Resets the current position in the linked list to the top
     */
    public void gotoHead(){
      mCurrentPos = mLRUHead;
    }

    /**
     * Resets the current position in the linked list to the bottom
     */
    public void gotoTail(){
      mCurrentPos = mLRUTail;
    }

    /**
     * Gets the next node in the linked list.
     *
     * @returns CacheElement or null if the current position is the tail.
     */
    public CacheElement getNextElement(){
      if (mCurrentPos == mLRUTail){
        return null;
      }else{
        mCurrentPos = mCurrentPos.getNext();
        return mCurrentPos;
      }
    }

    /**
     * Gets the previous node in the linked list.
     *
     * @returns CacheElement or null if the current position is the head.
     */
    public CacheElement getPreviousElement(){
      if (mCurrentPos == mLRUHead){
        return null;
      }else{
        mCurrentPos = mCurrentPos.getPrevious();
        return mCurrentPos;
      }
    }

    /**
     * A method for displaying the LRU list of cache elements for debugging purposes.
     */
    public void listAll(){
      System.out .print("<head> ");
      CacheElement theElement = mLRUHead.getNext();
      while (theElement!=null && theElement!=mLRUTail){
        System.out.print(theElement.getKey().toString()+"<-->");
        theElement = theElement.getNext();
      }
      System.out .println(" <tail>");
    }
}