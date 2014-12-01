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


import java.io.IOException;
import javax.servlet.ServletException;
import java.net.URL;
import java.net.MalformedURLException;

import javax.naming.NamingException;

import atg.core.util.StringUtils;
import atg.portal.framework.*;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.droplet.TransactionalFormHandler;
import atg.droplet.DropletFormException;

import atg.service.resourcepool.ResourcePool;
import atg.service.resourcepool.ResourcePoolException;
import atg.nucleus.GenericContext;
import atg.nucleus.ServiceListener;
import atg.nucleus.ServiceEvent;
import atg.nucleus.ServiceException;

import atg.portal.gear.xmlprotocol.cache.*;


/**
 * This form handler sets the gear instance parameters for the xmlprotocol gear
 *
 * @author J Marino
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/XmlProtocolInstanceFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class XmlProtocolInstanceFormHandler extends BaseFormHandler{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/XmlProtocolInstanceFormHandler.java#2 $$Change: 651448 $";



    String mOldAdaptor;
    String mInstanceUserID;
    public String getInstanceUserID(){
      return mInstanceUserID;
    }
    public void setInstanceUserID(String pInstanceUserID){
      mInstanceUserID=pInstanceUserID;
    }

    String mInstancePassword;

    public String getInstancePassword(){
      return mInstancePassword;
    }
    public void setInstancePassword(String pInstancePassword){
      mInstancePassword=pInstancePassword;
    }


    String mAuthenticationUrl;
    public String getAuthenticationUrl(){
      return mAuthenticationUrl;
    }
    public void setAuthenticationUrl(String pAuthenticationUrl){
      mAuthenticationUrl=pAuthenticationUrl;
    }

    String mHeadlinesUrl;
    public String getHeadlinesUrl(){
      return mHeadlinesUrl;
    }
    public void setHeadlinesUrl(String pHeadlinesUrl){
      mHeadlinesUrl=pHeadlinesUrl;
    }

    String mCategoriesUrl;
    public String getCategoriesUrl(){
      return mCategoriesUrl;
    }
    public void setCategoriesUrl(String pCategoriesUrl){
      mCategoriesUrl=pCategoriesUrl;
    }

    String mFeedAdaptor;
    public String getFeedAdaptor(){
      return mFeedAdaptor;
    }
    public void setFeedAdaptor(String pFeedAdaptor){
      mFeedAdaptor=pFeedAdaptor;
    }

    String mFullHeadlinesStylesheetUrl;
    public String getFullHeadlinesStylesheetUrl(){
      return mFullHeadlinesStylesheetUrl;
    }
    public void setFullHeadlinesStylesheetUrl(String pFullHeadlinesStylesheetUrl){
      mFullHeadlinesStylesheetUrl=pFullHeadlinesStylesheetUrl;
    }

    String mFullCategoriesStylesheetUrl;
    public String getFullCategoriesStylesheetUrl(){
      return mFullCategoriesStylesheetUrl;
    }
    public void setFullCategoriesStylesheetUrl(String pFullCategoriesStylesheetUrl){
      mFullCategoriesStylesheetUrl=pFullCategoriesStylesheetUrl;
    }

    String mFullArticleStylesheetUrl;
    public String getFullArticleStylesheetUrl(){
      return mFullArticleStylesheetUrl;
    }
    public void setFullArticleStylesheetUrl(String pFullArticleStylesheetUrl){
      mFullArticleStylesheetUrl=pFullArticleStylesheetUrl;
    }

    String mSharedHeadlinesStylesheetUrl;
    public String getSharedHeadlinesStylesheetUrl(){
      return mSharedHeadlinesStylesheetUrl;
    }
    public void setSharedHeadlinesStylesheetUrl(String pSharedHeadlinesStylesheetUrl){
      mSharedHeadlinesStylesheetUrl=pSharedHeadlinesStylesheetUrl;
    }

    String mSharedCategoriesStylesheetUrl;
    public String getSharedCategoriesStylesheetUrl(){
      return mSharedCategoriesStylesheetUrl;
    }
    public void setSharedCategoriesStylesheetUrl(String pSharedCategoriesStylesheetUrl){
      mSharedCategoriesStylesheetUrl=pSharedCategoriesStylesheetUrl;
    }

    String mArticleUrl;
    public String getArticleUrl(){
      return mArticleUrl;
    }
    public void setArticleUrl(String pArticleUrl){
      mArticleUrl=pArticleUrl;
    }

    /**
     * We need to track the last configured service provider adaptor to determine
     * when user-level personalization settings should not be applied.  For example,
     * if a community administrator changes the service provider for the gear,
     * then we need to make sure that user personalization settings for the previous
     * service provider are not applied.  We could do this by changing all user personalization
     * settings for a particular community.  For large communities, this could take an
     * extremely long time so we adopt a different approach.  Namely,
     */


    //-------------------------------------
    // Methods


    /**
     * Sets the appropriate user parameters from the form properties.
     */
    public boolean handleSubmit(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)  throws IOException, ServletException{
        String gearId = getGearId();
        if (StringUtils.isEmpty(gearId)){
            String msg = "Null gear id.";
            error(msg);
            String errorUrl = getErrorUrl();
            pResponse.sendLocalRedirect(errorUrl, pRequest);
            return false;
        }

        // Since a form handler doesn't have access to a gear environment, we
        // construct our own here.
        pRequest.setAttribute(PAF_GEAR_ATTR, gearId);
        GearEnvironment gearEnv;
        try{
          gearEnv = EnvironmentFactory.getGearEnvironment(pRequest, pResponse);

        }catch (EnvironmentException e) {
          if (isLoggingError()){
            logError(e);
          }
          String msg = "Internal Error: Gear environment is null";
          error(msg);
          String errorUrl = getErrorUrl();
          pResponse.sendLocalRedirect(errorUrl, pRequest);
          return false;
        }
        //save old adaptor info for when we flush cacvhes

        mOldAdaptor = gearEnv.getGearInstanceParameter(PARAM_FEED_ADAPTER_CLASS);
        gearEnv.setGearInstanceParameter(PARAM_URL_HEADLINES, this.getHeadlinesUrl());
        gearEnv.setGearInstanceParameter(PARAM_URL_CATEGORIES, this.getCategoriesUrl());
        gearEnv.setGearInstanceParameter(PARAM_URL_ARTICLE, this.getArticleUrl());
        gearEnv.setGearInstanceParameter(PARAM_URL_AUTHENTICATION, this.getAuthenticationUrl());
        gearEnv.setGearInstanceParameter(PARAM_FEED_ADAPTER_CLASS, this.getFeedAdaptor());
        gearEnv.setGearInstanceParameter(PARAM_STYLESHEET_FULL_HEADLINES, this.getFullHeadlinesStylesheetUrl());
        gearEnv.setGearInstanceParameter(PARAM_STYLESHEET_FULL_CATEGORIES, this.getFullCategoriesStylesheetUrl());
        gearEnv.setGearInstanceParameter(PARAM_STYLESHEET_FULL_ARTICLE, this.getFullArticleStylesheetUrl());

        gearEnv.setGearInstanceParameter(PARAM_STYLESHEET_SHARED_HEADLINES, this.getSharedHeadlinesStylesheetUrl());
        gearEnv.setGearInstanceParameter(PARAM_STYLESHEET_SHARED_CATEGORIES, this.getSharedCategoriesStylesheetUrl());
        gearEnv.setGearInstanceParameter(PARAM_INSTANCE_USERID, this.getInstanceUserID());
        gearEnv.setGearInstanceParameter(PARAM_INSTANCE_PASSWORD, this.getInstancePassword());
        //this.flushCaches();   //Flushes caches and clear connection pools
        return this.checkFormRedirect(this.getSuccessUrl(),this.getErrorUrl(),pRequest,pResponse);

//      return true;
    }

   /**
    * This method clears all connection pools and flushes caches.
    */
/*
    //TODO remove this method!!
   protected void flushCaches(){
    try{
      javax.naming.Context ctx = new javax.naming.InitialContext();
      /*xcv comment this!!!!
      HttpConnectionPool mConnectionPool = null;
      URL url = new URL(this.getAuthenticationUrl());   //we always uses the authentication url as the pool key
      //todo remove this!
      //String baseUrl = url.toString().substring(0, url.toString().indexOf(url.getPath()));
      String baseUrl = u.getProtocol()+"://"+u.getAuthority()

      AdaptorConfig theAdaptor  = (AdaptorConfig) ctx.lookup(ADAPTOR_CONFIG);
      GenericContext genericContext = (GenericContext) ctx.lookup(theAdaptor.getConnectionPoolContext());
      if (genericContext != null){
        //go and see if it has already been created.  If not, create a new pool, configure it, and start it
        mConnectionPool = (HttpConnectionPool) genericContext.getElement(baseUrl);
        if (mConnectionPool != null){
          mConnectionPool.doStopService();
          genericContext.removeElement(baseUrl);
        }
      }

      URL url = new URL(this.getAuthenticationUrl());   //we always uses the authentication url as the pool key
      //todo remove this!
      //xcv String baseUrl = url.toString().substring(0, url.toString().indexOf(url.getPath()));
      //String baseUrl = u.getProtocol()+"://"+u.getAuthority()
      //String baseUrl = this.getAuthenticationUrl();
      String adaptorContext = "dynamo:/" + (Class.forName(mOldAdaptor).getName()).replace('.','/') +"Config";
      AdaptorConfig theAdaptor  = (AdaptorConfig) ctx.lookup(adaptorContext);
      //GenericContext genericContext = (GenericContext) ctx.lookup(theAdaptor.getConnectionPoolContext());

      LRUCache mTheCache = null;
      GenericContext genericContext = (GenericContext) ctx.lookup(theAdaptor.getCacheContext());
      if (genericContext != null){
        mTheCache = (LRUCache) genericContext.getElement(theAdaptor.getCacheName());
        if (mTheCache != null){
          mTheCache.removeAll();
          mTheCache.doStopService();
          genericContext.removeElement(theAdaptor.getCacheName());
        }
      }

    }catch (NamingException e){
        logError("Error getting JNDI context");
    }catch (MalformedURLException e){
        logError("Bad Url");
    }catch (ServiceException e){
        logError("Error flushing internal caches");
    }catch (ClassNotFoundException e){
        logError("Error flushing internal caches");
    }
   }
*/
}
