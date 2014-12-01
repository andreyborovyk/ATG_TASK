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

import atg.portal.framework.*;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.droplet.TransactionalFormHandler;
import atg.droplet.DropletFormException;

/**
 * A base form handler for the XmlProtocol gear.
 *
 * @author J Marino
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/BaseFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class BaseFormHandler extends TransactionalFormHandler{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/BaseFormHandler.java#2 $$Change: 651448 $";


    // The attribute of the gear id.  This value is picked up by GearEnvironmentImpl
    public static final String PAF_GEAR_ATTR = "atg.paf.Gear";

    protected static String ADAPTOR_CONFIG; //               = "dynamo:/atg/portal/gear/xmlprotocol/AdaptorConfig";

    // Configuration parameters for the gear
    public static final String PARAM_USER_ID                      = "userID";

    public static final String PARAM_INSTANCE_USERID              = "instanceUserID";
    public static final String PARAM_INSTANCE_PASSWORD            = "instancePassword";
    public static final String PARAM_URL_HEADLINES                = "headlinesUrl";
    public static final String PARAM_URL_CATEGORIES               = "categoriesUrl";
    public static final String PARAM_URL_AUTHENTICATION           = "authenticationUrl";
    public static final String PARAM_URL_ARTICLE                  = "articleUrl";

    public static final String PARAM_FEED_ADAPTER_CLASS           = "feedAdaptor";
    public static final String PARAM_STYLESHEET_FULL_HEADLINES    = "fullHeadlinesStylesheetUrl";
    public static final String PARAM_STYLESHEET_FULL_CATEGORIES   = "fullCategoriesStylesheetUrl";
    public static final String PARAM_STYLESHEET_FULL_ARTICLE      = "fullArticleStylesheetUrl";
    public static final String PARAM_STYLESHEET_SHARED_HEADLINES  = "sharedHeadlinesStylesheetUrl";
    public static final String PARAM_STYLESHEET_SHARED_CATEGORIES = "sharedCategoriesStylesheetUrl";

    public static final String CATEGORY_DELIMETER = ",";

    public static final String PARAM_CATEGORIES              = "categories";
    public static final String PARAM_NUM_SHARED_HEADLINES    = "numSharedHeadlines";
    public static final String PARAM_NUM_FULL_HEADLINES      = "numFullHeadlines";
    public static final String PARAM_LAST_ADAPATOR           = "lastAdaptor";


    GearEnvironment mGearEnv;
    public void setGearEnv(GearEnvironment pGearEnv) {
      mGearEnv = pGearEnv;
    }
    public GearEnvironment getGearEnv() {
      return mGearEnv;
    }
    String mGearId;
    public String getGearId()
    {
        return mGearId;
    }
    public void setGearId(String pGearId)
    {
        mGearId = pGearId;
    }

    String mGearURL;
    public String getGearURL()
    {
        return mGearURL;
    }
    public void setGearURL(String pGearURL)
    {
        mGearURL = pGearURL;
    }

    String mErrorUrl;
    public String getErrorUrl(){
        return mErrorUrl;
    }
    public void setErrorUrl(String pErrorUrl){
        mErrorUrl = pErrorUrl;
    }

    String mSuccesUrl;
    public String getSuccessUrl(){
      return mSuccesUrl;
    }
    public void setSuccessUrl(String pSuccessUrl){
      mSuccesUrl=pSuccessUrl;
    }

    /**
     * Logs pMessage to the error logging service and adds a form exception
     * which can be displayed with the ErrorMessageForEach droplet.
     *
     * @param pMessage the error message to log
     */

    protected void error(String pMessage)
    {
        if (isLoggingError())
        {
            logError(pMessage);
        }
        String absPath = getAbsoluteName();
        DropletFormException exc = new DropletFormException(pMessage, absPath);
        addFormException(exc);
    }

}
