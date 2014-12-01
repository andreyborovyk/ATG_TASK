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
 * A global configuration bean for classes implementing
 * the Conversation adaptor interface.
 *
 * @author J Marino
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/AdaptorConfig.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class AdaptorConfig {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/AdaptorConfig.java#2 $$Change: 651448 $";


  public AdaptorConfig() {
  }

  /**
   * The URN for retrieving the content cache config object through JNDI
   */
  public String mCacheConfig; //       = "dynamo:/atg/dynamo/service/xml/CacheFactory";
  public String getCacheConfig(){
    return mCacheConfig;
  }
  public void setCacheConfig(String pConfig){
    mCacheConfig = pConfig;
  }

  /**
   * Specifies the length of time in milliseconds to cache an article.
   * A value of -1 denotes disabled caching
   */
  public long mCacheArticles;
  public long getCacheArticles(){
    return mCacheArticles;
  }
  public void setCacheArticles(long pTime){
    mCacheArticles = pTime;
  }

   /**
   * Specifies the length of time in milliseconds to cache a category list.
   * A value of -1 denotes disabled caching
   */
  public long mCacheCategories;
  public long getCacheCategories(){
    return mCacheCategories;
  }
  public void setCacheCategories(long pTime){
    mCacheCategories = pTime;
  }

  /**
   * Specifies the length of time in milliseconds to cache heacdlines.
   * A value of -1 denotes disabled caching
   */
  public long mCacheHeadlines;
  public long getCacheHeadlines(){
    return mCacheHeadlines;
  }
  public void setCacheHeadlines(long pTime){
    mCacheHeadlines = pTime;
  }
  /**
   * The URN for retrieving the Dynamo-supplied XML parser factory through JNDI
   */
  public String mXMLToolsContext;
  public String getXMLToolsContext(){
    return mXMLToolsContext;
  }
  public void setXMLToolsContext(String pContext){
    mXMLToolsContext = pContext;
  }

  /**
   * The number of times to retry connecting to the service provider on failure
   */

   public int mNumRetries;
   public int getNumRetries(){
    return mNumRetries;
   }
   public void setNumRetries(int pNumRetries){
    mNumRetries=pNumRetries;
   }



}