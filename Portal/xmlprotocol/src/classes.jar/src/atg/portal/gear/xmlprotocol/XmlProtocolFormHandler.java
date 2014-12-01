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

import atg.core.util.StringUtils;
import atg.portal.framework.*;

import atg.core.util.*;

import atg.servlet.*;
import atg.droplet.*;

import java.util.Locale;
import java.util.ResourceBundle;
import java.io.IOException;
import javax.servlet.ServletException;

/**
 * This form handler sets the gear user parameters for the xmlprotocol gear
 *
 * @author J Marino
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/XmlProtocolFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 *
 */
public class XmlProtocolFormHandler extends BaseFormHandler{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/XmlProtocolFormHandler.java#2 $$Change: 651448 $";


  /** resource bundle name */
  static final String RESOURCE_BUNDLE_NAME = "atg.portal.gear.xmlprotocol.XmlProtocolResource";
  
  
    /**
     * Determines whether to configure default or specific user settings
     */

    String mConfigureDefaults = "false";
    public String getConfigureDefaults(){
      return mConfigureDefaults;
    }
    public void setConfigureDefaults(String pConfigureDefaults){
      mConfigureDefaults=pConfigureDefaults;
    }

    String mCategories;
    public String getCategories()
    {
      return mCategories;
    }
    public void setCategories(String pCategories)
    {
      mCategories = pCategories;
    }

    String mNumSharedHeadlines;
    public String getNumSharedHeadlines()
    {
      return mNumSharedHeadlines;
    }
    public void setNumSharedHeadlines(String pNumSharedHeadlines)
    {
      mNumSharedHeadlines = pNumSharedHeadlines;
    }

    String mNumFullHeadlines;
    public String getNumFullHeadlines()
    {
      return mNumFullHeadlines;
    }
    public void setNumFullHeadlines(String pNumFullHeadlines)
    {
      mNumFullHeadlines = pNumFullHeadlines;
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

   

    String[] mSelectedCategories;
    public void setSelectedCategories(String[] pSelectedCategories) {
      mSelectedCategories = pSelectedCategories;
    }
    public String[] getSelectedCategories() {
      return mSelectedCategories;
    }

    

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

        //loop through array of selected categories and convert it to
        // a delimeted string for storage
        StringBuffer mConcatenatedCategories = new StringBuffer();
        //avoid index out of bounds if no categories selected and report as an error to the user

        if (this.getSelectedCategories() != null && this.getSelectedCategories().length > 0){
          for(int i=0; i<this.getSelectedCategories().length-1; i++){
             mConcatenatedCategories.append(this.getSelectedCategories()[i]+CATEGORY_DELIMETER);
          }
          //get the last element in the array but do not add a delimeter
           mConcatenatedCategories.append(this.getSelectedCategories()[(this.getSelectedCategories()).length-1]);
        }

        else{

          addFormException(new DropletFormException(getMsgString("noCategoriesSelected", getUserLocale(pRequest, pResponse)), getAbsoluteName()));
          return checkFormRedirect(getSuccessUrl(), getErrorUrl(), pRequest,pResponse);
          
        }
        
        if (mConfigureDefaults.equalsIgnoreCase("true")){

          
         
            gearEnv.setGearUserDefaultValue(PARAM_CATEGORIES, mConcatenatedCategories.toString());
          
          gearEnv.setGearUserDefaultValue(PARAM_NUM_SHARED_HEADLINES, mNumSharedHeadlines);
          gearEnv.setGearUserDefaultValue(PARAM_NUM_FULL_HEADLINES, mNumFullHeadlines);
          //store the current adaptor so we can track changes
          gearEnv.setGearUserDefaultValue(PARAM_LAST_ADAPATOR, gearEnv.getGearInstanceParameter(PARAM_FEED_ADAPTER_CLASS));
        }

        else{

          gearEnv.setGearUserParameter(PARAM_CATEGORIES, mConcatenatedCategories.toString());
          gearEnv.setGearUserParameter(PARAM_NUM_SHARED_HEADLINES, this.getNumSharedHeadlines());
          gearEnv.setGearUserParameter(PARAM_NUM_FULL_HEADLINES, this.getNumFullHeadlines());
          //store the current adaptor so we can track changes
          gearEnv.setGearUserParameter(PARAM_LAST_ADAPATOR, gearEnv.getGearInstanceParameter(PARAM_FEED_ADAPTER_CLASS));
        }
        return this.checkFormRedirect(this.getSuccessUrl(),this.getErrorUrl(),pRequest,pResponse);
        
    }

  /**
   * Returns the locale associated with the request.
   * Finally, if the locale cannot be determined,
   * the the default Locale for the JVM is used.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public Locale getUserLocale(DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    
    RequestLocale requestLocale = pRequest.getRequestLocale();
    if (requestLocale != null)
      return requestLocale.getLocale();
    
    
    return Locale.getDefault();
    
  }

  String getMsgString(String pKey, Locale pLocale) 
  {
    ResourceBundle bundle = ResourceUtils.getBundle(RESOURCE_BUNDLE_NAME, pLocale);
    return bundle.getString(pKey);
  }
  

 
}
