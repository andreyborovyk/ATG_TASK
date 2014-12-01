/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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

package atg.userprofiling;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import atg.droplet.DropletException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailInfoImpl;
import atg.userprofiling.email.TemplateEmailSender;

/**
 * This class provides a form handler for handling requests related to forgot password functionality.  
 * The form has an emptySite and siteId Property.  When emptySite is true, a null siteId is passed to
 * the email and any siteId specified in form is ignored.  When emptySite is set to false or not provided, the siteId that
 * is passed through the form is used in the email.  If no siteId is provided, the form defaults to the
 * current site context.
 *
 * @author Patrick Beagan
 * @version $Id: //product/DPS/version/10.0.3/Java/atg/userprofiling/ForgotPasswordHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ForgotPasswordHandler extends ProfileForm {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //product/DPS/version/10.0.3/Java/atg/userprofiling/ForgotPasswordHandler.java#2 $$Change: 651448 $";

  
  
  //-------------------------------------
  // Constants
  //-------------------------------------
  
  // User Messages
  protected static final String MSG_NO_SUCH_PROFILE             = "noSuchProfileError";
  protected static final String MSG_ERR_SENDING_EMAIL             = "errorSendingEmail";
  
  //-------------------------------------
  // Member Variables
  //-------------------------------------
  
  //-------------------------------------
  // Properties
  //-------------------------------------
  boolean mSendEmailInSeparateThread=true;

  //-------------------------------------
  /**
   * Sets boolean indicating whether the email is sent in a separate thread. 
   * @param pUsingPriceLists boolean indicating whether the email is sent in a separate thread.
   * @beaninfo description: boolean indicating whether the email is sent in a separate thread.
   **/
  public void setSendEmailInSeparateThread(boolean pSendEmailInSeparateThread) {
    mSendEmailInSeparateThread = pSendEmailInSeparateThread;
  }

  //-------------------------------------
  /**
   * Returns boolean indicating whether the email is sent in a separate thread.
   **/
  public boolean isSendEmailInSeparateThread() {
      return mSendEmailInSeparateThread;
  }
  

  boolean mPersistEmails=false;

  //-------------------------------------
  /**
   * Sets boolean indicating whether the email is persisted before it is sent.  
   * @param pUsingPriceLists boolean indicating whether the email is persisted before it is sent. 
   * @beaninfo description: boolean indicating whether the email is persisted before it is sent. 
   **/
  public void setPersistEmails(boolean pPersistEmails) {
    mPersistEmails = pPersistEmails;
  }

  //-------------------------------------
  /**
   * Returns boolean indicating whether the email is persisted before it is sent. 
   **/
  public boolean isPersistEmails() {
      return mPersistEmails;
  }
  
  
  
  //--------- Property: TemplateEmailSender -----------
  TemplateEmailSender mTemplateEmailSender = null;
  /**
   * Sets the property TemplateEmailSender
   */
  public void setTemplateEmailSender(TemplateEmailSender pTemplateEmailSender) {
    this.mTemplateEmailSender = pTemplateEmailSender;
  }
  /**
   * @return The value of the property TemplateEmailSender
   */
  public TemplateEmailSender getTemplateEmailSender() {
    return mTemplateEmailSender;
  }
  
  //--------- Property: SiteId  -----------
  /**
   * The Site id for the form.  If this isn't provided, Site will use the SiteContextManager's current site
   */
  protected String mSiteId;
  
  /**
   * Set the Site ID
   * @beaninfo description: Sets the current site id
   */
  public void setSiteId(String pSiteId){
    mSiteId = pSiteId;
  }
  
  /**
   * Site id
   * @return the site id set on the form
   */
  public String getSiteId(){
    return mSiteId;
  }
  
  //--------- Property: emptySite  -----------
  /**
   * If there is no site, set to true.
   */
  protected Boolean mEmptySite;
  
  /**
   * Set the EmptySite
   * @beaninfo description: Sets the emptySite property
   */
  public void setEmptySite(Boolean pEmptySite){
    mEmptySite = pEmptySite;
  }
  
  /**
   * Site id 
   * @return the site id set on the form
   */
  public Boolean getEmptySite(){
    return mEmptySite;
  }
  
  //--------- Property: ForgotPasswordErrorURL -----------
  private String mForgotPasswordErrorURL;
  /**
   * Sets the property ForgotPasswordErrorURL.
   * @beaninfo description: URL to which to redirect upon a failed
   * forgot password.
   */
  public void setForgotPasswordErrorURL(String pForgotPasswordErrorURL) {
    mForgotPasswordErrorURL = pForgotPasswordErrorURL;
  }
  /**
   * @return The value of the property ForgotPasswordErrorURL.
   */
  public String getForgotPasswordErrorURL() {
    return mForgotPasswordErrorURL;
  }
  
  //--------- Property: ForgotPasswordSuccessURL -----------
  private String mForgotPasswordSuccessURL;
  /**
   * Sets the property ForgotPasswordSuccessURL.
   * @beaninfo description: URL to which to redirect upon a successful forgot password submit.
   */
  public void setForgotPasswordSuccessURL(String pForgotPasswordSuccessURL) {
    mForgotPasswordSuccessURL = pForgotPasswordSuccessURL;
  }
  /**
   * @return The value of the property ForgotPasswordSuccessURL.
   */
  public String getForgotPasswordSuccessURL() {
    return mForgotPasswordSuccessURL;
  }
  //--------- Property: TemplateEmailInfo -----------
  TemplateEmailInfoImpl mTemplateEmailInfo = null;
  /**
   * Sets the property TemplateEmailInfo
   */
  public void setTemplateEmailInfo(TemplateEmailInfoImpl pTemplateEmailInfo) {
    this.mTemplateEmailInfo = pTemplateEmailInfo;
  }
  /**
   * @return The value of the property TemplateEmailInfo
   */
  public TemplateEmailInfoImpl getTemplateEmailInfo() {
    return mTemplateEmailInfo;
  }
  
  //--------- Property: NewPasswordParam -----------
  private String mNewPasswordParam = "newpassword";
  /**
   * Sets the property NewPasswordParam
   * @beaninfo description: parameter on JSP/JHTML page email template that displays the new generated password
   */
  public void setNewPasswordParam(String pNewPasswordParam) {
    this.mNewPasswordParam = pNewPasswordParam;
  }
  /**
   * @return The value of the property NewPasswordParam
   * @beaninfo description: parameter on JSP/JHTML page email template that displays the new generated password
   */
  public String getNewPasswordParam() {
    return mNewPasswordParam;
  }
  
  //-------------------------------------
  
  /**
   * 
   * Takes a login or email property and, if there were no errors 
   * in submitting the form, finds the profile associated, generates
   * a new password, updates the profile, and sends an email
   * to the user. 
   * <p>
   * If any errors occur in the process, form errors will be added.
   * If there were no errors encountered while submitting the form, we
   * optionally redirect to the value of the property forgotPasswordSuccessURL.
   * Otherwise, we optionally redirect to forgotPasswordErrorURL. 
   * 
   * @see ProfileTools#generateNewPasswordForProfile(RepositoryItem)
   * @see ProfileTools#sendEmailToUser(MutableRepositoryItem, boolean, boolean, TemplateEmailInfo, Map)
   * @see #generateNewPasswordTemplateParams(DynamoHttpServletRequest, DynamoHttpServletResponse, RepositoryItem, String)
   * 
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleForgotPassword(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse)
  throws ServletException, IOException
  {
    
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    try {
      if (tm != null) td.begin(tm, TransactionDemarcation.REQUIRED);
      
      int status = checkFormError(getForgotPasswordErrorURL(), pRequest, pResponse);
      if (status != STATUS_SUCCESS) return status == STATUS_ERROR_STAY;
      
      preForgotPassword(pRequest, pResponse);
      
      status = checkFormError(getForgotPasswordErrorURL(), pRequest, pResponse);
      if (status != STATUS_SUCCESS) return status == STATUS_ERROR_STAY;
      
      // lookup user based on login or email address
      ProfileTools ptools = getProfileTools();
      PropertyManager pmgr = ptools.getPropertyManager();
      String loginPropertyName = pmgr.getLoginPropertyName();
      String emailPropertyName = pmgr.getEmailAddressPropertyName();
      
      // extract parameters from form
      String login = getStringValueProperty(loginPropertyName);
      String email = getStringValueProperty(emailPropertyName);
      
      //Lets set the site context
      Boolean emptySite = getEmptySite();
      String siteId = getSiteId();
      
      if(emptySite != null){
        if(emptySite){
          setSiteId(null);
        }
        else {
          if(siteId != null){
            setSiteId(siteId);
          }
          else {
            setSiteId(SiteContextManager.getCurrentSiteId());
          }
        }
      } 
      else if (siteId != null){
        setSiteId(siteId.toString());
      }
      else {
        setSiteId(SiteContextManager.getCurrentSiteId());
      }
      
      getTemplateEmailInfo().setSiteId(getSiteId());
      
      // lookup user(s). First by login, then by email
      MutableRepositoryItem[] users = lookupUsers(login, email);
      
      if (users==null) {
        String msg = formatUserMessage(MSG_NO_SUCH_PROFILE, pRequest);
        addFormException(new DropletException(msg, MSG_NO_SUCH_PROFILE));
        
      } else {
        
        try {
            // Most likely only 1 user will be returned
            for (int i=0; i<users.length; i++) {
              String newPassword = ptools.generateNewPasswordForProfile(users[i]);
              ptools.sendEmailToUser(users[i],isSendEmailInSeparateThread(),isPersistEmails(),getTemplateEmailSender(),getTemplateEmailInfo(),
                                        generateNewPasswordTemplateParams(pRequest,pResponse,users[i],newPassword));
            }
        }
        catch (TemplateEmailException exc){
          String msg = formatUserMessage(MSG_ERR_SENDING_EMAIL, pRequest);
          addFormException(new DropletException(msg, exc, MSG_ERR_SENDING_EMAIL));      
          if (isLoggingError())
            logError(exc);
        }
        catch (RepositoryException exc){
          String msg = formatUserMessage(MSG_ERR_SENDING_EMAIL, pRequest);
          addFormException(new DropletException(msg, exc, MSG_ERR_SENDING_EMAIL));      
          if (isLoggingError())
            logError(exc);
        }
      }
      postForgotPassword(pRequest, pResponse);
      
      // In case errors occurred during the conversion process
      if ((status = checkFormError(getForgotPasswordErrorURL(), pRequest, pResponse)) != STATUS_SUCCESS) 
        return status == STATUS_ERROR_STAY;
      
      if (!checkFormSuccess(getForgotPasswordSuccessURL(), pRequest, pResponse))
        return false;
      return true;
    }
    catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    }
    finally {
      try { if (tm != null) td.end(); }
      catch (TransactionDemarcationException e) { }
    }  	
    
  }
  
  /**
   * Generates the parameters map that's used to send the new password email. 
   * <p>
   * By default, this method adds the parameter for the new password using the
   * name configured in the <code>newPasswordParam</code> property. 
   * <p>
   * Extend this method to add additional parameters to the email template. 
   * @param pRequest
   * @param pResponse
   * @param pProfile the profile that had the new password generated. 
   * @param pNewPassword the new password in clear text
   * @return Map of template parameters
   */
  protected Map generateNewPasswordTemplateParams(DynamoHttpServletRequest pRequest,
          DynamoHttpServletResponse pResponse, RepositoryItem pProfile, String pNewPassword)
  {
      HashMap params = new HashMap(1);
      params.put(getNewPasswordParam(),pNewPassword);
      return params; 
  }
  /**
   * Operation called just before the forgot password logic is executed
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void preForgotPassword(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) 
  throws ServletException, IOException 
  {
  }
  
  /**
   * Operation called just after the forgot password logic is executed
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void postForgotPassword(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) 
  throws ServletException, IOException 
  {
  }
  
  /**
   * Lookup an array of users from the given login or email.  
   * First attempts to find the user associated with the given login.  If
   * nothing is returned, attempt to lookup the user(s) associated with the
   * given email.  Return null if no users found. 
   * 
   * @param login - login passed from web form
   * @param email - email address passed from web form
   * @return
   */
  public MutableRepositoryItem[] lookupUsers(String login, String email) {
    
    MutableRepositoryItem[] users = null;
    
    // Lookup user by login
    if (login!=null && !login.equals("")) {
      MutableRepositoryItem user = (MutableRepositoryItem)getProfileTools().getItem(login, null, getLoginProfileType());
      
      if (user!=null) {
        users = new MutableRepositoryItem[1];
        users[0] = user;
      }
    }
    
    // Lookup user(s) by email
    if (users==null && email!=null && !email.equals("")) 
      users = (MutableRepositoryItem[])getProfileTools().getItemsFromEmail(email);
    
    return users;
  }
  
}
