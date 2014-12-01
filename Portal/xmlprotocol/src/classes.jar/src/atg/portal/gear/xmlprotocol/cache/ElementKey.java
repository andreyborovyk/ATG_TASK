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
 * An abstract class for keys for the LRU cache
 *
 * @author J Marino
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/cache/ElementKey.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public abstract class ElementKey {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/cache/ElementKey.java#2 $$Change: 651448 $";


  /**
   * Retrieves the size of the element key
   *
   * @returns int the size of the key in bytes
   */
  public abstract int getSize();

  public abstract boolean equals(Object pObject);

  public abstract int hashCode();

  public boolean objectsEqual (Object pObject1, Object pObject2){
    return pObject1 == pObject2 ||
      (pObject1 != null && pObject2 != null && pObject1.equals(pObject2));
  }

  public int objectHashCode(Object pObject){
    return (pObject == null) ? 0: pObject.hashCode();
  }

  public int objectSize(Object pObject){
    if (pObject == null){
      return 0;
    }else if (pObject instanceof String){
      String obj = (String) pObject;
      return 8+(obj.length()*2);
    }else{
      return 8;
    }
  }
  /**
   * Returns key value as a string
   * @returns String representation of the key
   */
  public abstract String toString();


}