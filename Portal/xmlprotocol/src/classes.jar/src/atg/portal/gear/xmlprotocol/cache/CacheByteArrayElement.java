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
 * A class that represents a cache element that is a byte[].
 * The contents of the element are wrapped by a class
 * implementing the ElementContents interface.
 *
 * @author J Marino
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/cache/CacheByteArrayElement.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see ElementContents
 */

public class CacheByteArrayElement extends CacheElement {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/cache/CacheByteArrayElement.java#2 $$Change: 651448 $";


 //-------------------------------------
 // Properties

 //The contents wrapper
  ByteArrayElementContents mContents;

 //-------------------------------------
 // Methods

 /**
  * Constructor
  * We have an empty cinstructor to support dynamic class loading by the LRU cache
  */

  public CacheByteArrayElement(){}

  /**
   * Initializes the cache element
   * @parameters pKey the unique id for referencing the element in the cache
   *
   *             pContents the class wrapper for the actual contents.  We need
   *             a wrapper since the cahce supports contents of
   *             primitive data types or objects.  In the case, the contents are a byte[].
   *
   *             pExpireTime the absolute time the contents expire in milliseconds.  If
   *             set to 0 there is no expiration time.
   *
   * @exception IllegalArgumentException if the content warpper is not a ByteArrayElementContents.
   */
  public void init(ElementKey pKey, ElementContents pContents, long pExpireTime) throws IllegalArgumentException{
    mKey = pKey;
    if (pContents instanceof ByteArrayElementContents){
       mContents = (ByteArrayElementContents)pContents;
       mSize = 36 + mKey.getSize() + mContents.getSize();
    }else{
       throw new IllegalArgumentException ("The contents must be of class ByteArrayElementContents");
    }
    mExpirationTime = pExpireTime;
  }

  /**
  * Returns the cached contents in a wrapper object.
  * The contents are retrieved through an accessor method
  * specific to that class.
  *
  * @return ByteArrayElementContents containing the cached contents
  *
  */
  public ByteArrayElementContents getContents(){
    return mContents;
  }

}
