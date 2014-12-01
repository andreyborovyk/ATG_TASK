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

package atg.portal.gear.xmlprotocol;

/**
 * Global configuration object for the content cache
 *
 * @author J Marino
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/CacheConfig.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class CacheConfig {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/CacheConfig.java#2 $$Change: 651448 $";


  public CacheConfig() {
  }

  /**
   * Property specifying the Nucleus cache name for this adaptor.
   */
  public String mCacheName;
  public String getCacheName(){
    return mCacheName;
  }
  public void setCacheName(String pCacheName){
    mCacheName=pCacheName;
  }

  /**
   * Value specifying the max cache size in bytes for content
   */
  public int mMaxCacheSize;
  public int getMaxCacheSize(){
    return mMaxCacheSize;
  }
  public void setMaxCacheSize(int pSize){
    mMaxCacheSize = pSize;
  }



  /**
   * The URN for retrieving the content cache manager through JNDI
   */
  public String mCacheContext;//           = "dynamo:/atg/portal/gear/xmlprotocol/CacheContext";
  public String getCacheContext(){
    return mCacheContext;
  }
  public void setCacheContext(String pContext){
    mCacheContext = pContext;
  }

}