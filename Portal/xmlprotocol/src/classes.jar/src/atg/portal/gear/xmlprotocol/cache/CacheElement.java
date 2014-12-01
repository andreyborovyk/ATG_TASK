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


 /**
  * An abstract class for an element to be stored in the LRU cache.
  *
  * CacheElement is subclassesed to provide handling for different datatypes,
  * which may be primitives or objects.
  *
  * CacheElement provides methods for performing tasks common to all elements as
  * well as implementing pointers for the doubly linked list used by the cache
  * to determine LRU elements.
  *
 * @author J Marino
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/cache/CacheElement.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  */

public abstract class CacheElement {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/cache/CacheElement.java#2 $$Change: 651448 $";


  //-------------------------------------
  // Properties

  // the unique id for the element in the cache
  ElementKey mKey;
  public ElementKey getKey(){
    return mKey;
  }

  // the size of the element, including its contents
  int mSize;
  public int getSize(){
    return mSize;
  }

  // a pointer to the next element in the doubly linked list maintained by the cache
  CacheElement mNext;
  public void setNext (CacheElement pNext){
    mNext = pNext;
  }
  public CacheElement getNext(){
    return mNext;
  }

  // a pointer to the previous element in the doubly linked list maintained by the cache
  CacheElement mPrevious;
  public CacheElement getPrevious(){
    return mPrevious;
  }
  public void setPrevious (CacheElement pPrevious){
    mPrevious = pPrevious;
  }

  //absolute expiration time of the element in milliseconds
  long mExpirationTime =0;    //default to no expire
  public long getExpirationTimeMillis(){
    return mExpirationTime;
  }

  /**
  * The method signature to initialize cache elements.
  *
  * @parameters pKey the unique id for referencing the element in the cache
  *
  *             pContents the class wrapper for the actual contents.  We need
  *             a wrapper since the cache supports contents of
  *             primitive data types or objects.  In the case, the contents are a byte[].
  *
  *             pExpireTime the absolute time the contents expire in milliseconds.  If
  *             set to 0 there is no expiration time.
  *
  * @exception IllegalArgumentException if the content warpper is not the appropriate type.
  *
  */
  public abstract void init(ElementKey pKey, ElementContents pContents, long ExpirationTime) throws IllegalArgumentException;

  /**
   * Set the absolute expiration time for the element.
   *
   * @param pMillis the expiration time in milliseconds.
   *        0 denotes no expiration.
   */
  public void setExpirationTimeFromNowMillis(long pMillis){
    if (pMillis == 0){
      mExpirationTime = 0;
    }else{
      mExpirationTime =  System.currentTimeMillis() +pMillis;
    }
  }

  /**
   * Set the absolute expiration time for the element in minutes.
   *
   * @param pMillis the expiration time in minutes.
   *        0 denotes no expiration.
   */
  public void setExpirationTimeFromNow(int pMinutes){
    if (pMinutes == 0){
      mExpirationTime = 0;
    }else{
      mExpirationTime =  System.currentTimeMillis() +pMinutes*60*1000;
    }
  }

  /**
   * Returns whether the contents are expired.
   */
  public boolean isExpired(){
    return (this.getExpirationTimeMillis() != 0 &&
       this.getExpirationTimeMillis() <= System.currentTimeMillis());
  }



}
