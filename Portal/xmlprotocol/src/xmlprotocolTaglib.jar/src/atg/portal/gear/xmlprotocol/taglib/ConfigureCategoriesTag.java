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
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.portal.gear.xmlprotocol.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import java.util.StringTokenizer;
import java.util.ArrayList;

import atg.portal.framework.GearEnvironment;

import atg.portal.gear.xmlprotocol.*;

/**
 * A tag used to configure personalized categories.  The tag can be used to configure
 * default categories to display for all users or personalized categories for a particular user.
 *
 * The tag retrieves a list of available categories from the service provider
 * and then compares that list to either the default categories to display or
 * the categories the particular user has selected to display depending on wether
 * configureDefaults is set to <code>true</code> or <code>false</code>.
 * The default is to configure for an individual user.
 *
 * Note that category ids are stored in a comma delimited string which can potentially cause
 * problems if the string is over 256 bytes since the portal framwork only allows paramaters
 * of the length.  This limit should be adequate in most circumstances since service providers
 * limit searches across multiple categories for performance reasons.
 *
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocolTaglib.jar/src/atg/portal/gear/xmlprotocol/taglib/ConfigureCategoriesTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ConfigureCategoriesTag extends XmlProtocolTag {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocolTaglib.jar/src/atg/portal/gear/xmlprotocol/taglib/ConfigureCategoriesTag.java#2 $$Change: 651448 $";


  public static final String CATEGORY_DELIMETER       = ",";

  /**
   * Constructor
   */
  public ConfigureCategoriesTag() {
    super();
  }

  /**
   * Returns a list of category names retrieved
   *
   * @returns String[] representing the category names
   */
  String[] mCategoryNames;
  public String[] getCategoryNames(){
    return mCategoryNames;
  }
  private void setCategoryNames(String[] pCategoryNames){
    mCategoryNames = pCategoryNames;
  }

  /**
   * Returns a list of unique ids for categories retrieved
   *
   * @returns String[] representing the unique ids
   */
  String[] mCategoryIDs;
  public String[] getCategoryIDs(){
    return mCategoryIDs;
  }
  private void setCategoryIDs(String[] pCategoryIDs){
    mCategoryIDs = pCategoryIDs;
  }

  /**
   * Returns a list corresponding to the categoryID list indicating which categories
   * have already been selected previously
   *
   * @returns String[] representing the list
   */
  String[] mCategoryChecked;
  public String[] getCategoryChecked(){
    return mCategoryChecked;
  }
  private void setCategoryChecked(String[] pCategoryChecked){
    mCategoryChecked = pCategoryChecked;
  }

  /**
   * The URL to retrieve categories from the service provider
   */
  String mCategoriesUrl;
  public String getCategoriesUrl(){
    return mCategoriesUrl;
  }
  public void setCategoriesUrl(String pUrl){
    mCategoriesUrl=pUrl;
  }

  /**
   * Indicates wether we are configuring user selected values or just defaults for
   * all users
   */
  String mConfigureDefaults = "false";
  public String getConfigureDefaults(){
    return mConfigureDefaults;
  }
  public void setConfigureDefaults(String pDefaults){
    mConfigureDefaults = pDefaults;
  }

  /**
   * Indicates if the particular service provider supports selecting (i.e.
   * performin searches over) more than one category at a time
   */
  boolean mSupportsMultipleCategories;
  public boolean supportsMutlipleCategories(){
    return mSupportsMultipleCategories;
  }
  protected void setSupportsMultipleCategories(boolean pSupports){
    mSupportsMultipleCategories = pSupports;
  }

  /**
   * Performs initialization
   */
  protected void init() throws JspException{
   super.init();
   String categoriesUrl=this.getPafEnv().getGearInstanceParameter(PARAM_URL_CATEGORIES);
   //we are editing the deault category list
   if (categoriesUrl!=null){
    this.setCategoriesUrl(categoriesUrl);
   }
  }

  public int doStartTag () throws JspException  {
    long startTime;
    pageContext.setAttribute (getId (), this);
    startTime = System.currentTimeMillis ();
    this.init();

    GearEnvironment pafEnv=this.getPafEnv();
    if (pafEnv == null){
      throw new JspException("Gear Environment is null");
    }

    try{
      //Instantiate the adpator class for handling communications with the news service
      theConversation = (ConversationAdaptor) Class.forName(this.getAdaptorClass()).newInstance();
      if (theConversation == null){
        throw new JspException("The Gear is not configured properly: error instantiating adaptor class");
      }

      theConversation.init(pageContext);
      theConversation.setUserID(this.getUserID());
      theConversation.setPassword(this.getPassword());
      theConversation.setCategoriesUrl(this.getCategoriesUrl());
      theConversation.setAuthenticationUrl(this.getAuthenticationUrl());

      if (this.getParams() != null){
        theConversation.retrieveCategories(this.getParams());
      }else{
        theConversation.retrieveCategories();
      }

      //Build the arrays for Catgory names, unique ids, and whether the user has already selected them
      this.setCategoryNames(theConversation.getCategoryNames());
      this.setCategoryIDs(theConversation.getCategoryIDs());

      //Now go and compare the Category IDs returned from the feed service with ones the user has selected previously
      //We default to specific user params, otherwise we are setting default params for all users
      String categoriesString;

      if (this.getConfigureDefaults().equalsIgnoreCase("true")){
        categoriesString = pafEnv.getGearUserDefaultValue(PARAM_FEED_CATEGORIES);
      }else{
        categoriesString = pafEnv.getGearUserParameter(PARAM_FEED_CATEGORIES);
      }
      if (categoriesString == null){
        categoriesString="";
      }
      //We store the category IDs as a delimited string

      StringTokenizer theTokenizer = new StringTokenizer(categoriesString, CATEGORY_DELIMETER);
      int categoryCount = theTokenizer.countTokens();
      ArrayList userCategories = new ArrayList(categoryCount);
      while(theTokenizer.hasMoreTokens()){
        userCategories.add(theTokenizer.nextToken());
      }
      String[] categoryIDs = this.getCategoryIDs();
      String[] categoryChecked = new String[categoryIDs.length];

      for(int i=0; i<categoryIDs.length;i++){
        if (userCategories.indexOf(categoryIDs[i])<0){
          categoryChecked[i] = "false";
        }else{
          categoryChecked[i] = "true";
        }
      }
      this.setCategoryChecked(categoryChecked);
      this.setSupportsMultipleCategories(theConversation.supportsMultipleCategories());
    }catch(java.lang.Exception e){
       this.logError("XmlProtocol Gear: Error in Configure Categories Tag", e);
       return SKIP_BODY;
    }
    return EVAL_BODY_INCLUDE;
  }
  public void release ()   {
    mConfigureDefaults="false";
    super.release();
    mCategoryChecked=null;
    mCategoryIDs=null;
    mCategoryNames=null;
  }
}
