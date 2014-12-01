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
 * A class for using strings as keys for the LRU cache
 *
 * @author J Marino
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/cache/StringKey.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class StringKey extends ElementKey{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/cache/StringKey.java#2 $$Change: 651448 $";


 int mSize;
 boolean mSizeComputed=false;
 String mKey;

 public StringKey(String pKey) {
  mKey = pKey;
 }

 public boolean equals (Object pObject){
  if (!(pObject instanceof StringKey)){
    return false;
  }
  StringKey obj = (StringKey) pObject;
  return objectsEqual (mKey, obj.mKey);
 }

  public int getSize(){
    if (!mSizeComputed){
      int ret =16;
      ret += objectSize (mKey);
      mSize = ret;
      mSizeComputed = true;
    }
    return mSize;
  }

  public int hashCode(){
    int ret =0;
    ret = (ret *37) + objectHashCode(mKey);
    return ret;
  }

  public String toString(){
    return mKey;
  }

}