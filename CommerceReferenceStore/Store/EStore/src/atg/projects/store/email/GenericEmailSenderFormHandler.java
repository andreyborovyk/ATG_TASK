/*<ATGCOPYRIGHT>
 * Copyright (C) 2009-2011 Art Technology Group, Inc.
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

/*<ATGCOPYRIGHT>
 * Copyright (C) 2009-2011 Art Technology Group, Inc.
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
package atg.projects.store.email;

import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.droplet.GenericFormHandler;
import atg.projects.store.StoreConfiguration;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.email.TemplateEmailException;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Generic form handler for email sending.
 * 
 * @author ATG
 * @version $Id: //hosting-store/Store/main/estore/src/atg/projects/store/catalog/EmailAFriendFormHandler.java#15
 *          $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class GenericEmailSenderFormHandler extends GenericFormHandler {
  
  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/email/GenericEmailSenderFormHandler.java#2 $$Change: 651448 $";

  public static final String MSG_ACTION_SUCCESS = "E-mail has been sent successfully. Check your inbox.";
  
  /**
   * Store configuration.
   */
  private StoreConfiguration mStoreConfiguration;
  /**
   * Success URL.
   */
  private String mSuccessURL;
  /**
   * Error URL.
   */
  private String mErrorURL;
  /**
   * Template URL.
   */
  String mTemplateUrl = null;
  /**
   * Recipient name.
   */
  private String mRecipientName = null;
  /**
   * Recipient e-mail.
   */
  private String mRecipientEmail = null;
  /**
   * Sender name.
   */
  private String mSenderName = null;
  /**
   * Sender e-mail.
   */
  private String mSenderEmail = null;
  /**
   * Message.
   */
  private String mMessage = null;
  
  /**
   * Site id.
   */
  private String mSiteId = null;

  public GenericEmailSenderFormHandler() {
    super();
  }
  
  //--------------------------------------------------
  // property: Subject
  private String mSubject;

  /**
   * @return the String
   */
  public String getSubject() {
    return mSubject;
  }

  /**
   * @param Subject the String to set
   */
  public void setSubject(String pSubject) {
    mSubject = pSubject;
  }

  /**
   * @return the URL of the success page.
   */
  public String getSuccessURL() {
    return mSuccessURL;
  }

  /**
   * Sets the URL of the success page.
   * 
   * @param pSuccessURL -
   *          the URL of the success page
   */
  public void setSuccessURL(String pSuccessURL) {
    if (StringUtils.isBlank(pSuccessURL)) {
      mSuccessURL = null;
    } else {
      mSuccessURL = pSuccessURL;
    }
  }

  /**
   * @return the URL of the error page.
   */
  public String getErrorURL() {
    return mErrorURL;
  }

  /**
   * Sets the URL of the error page.
   * 
   * @param pErrorURL -
   *          the URL of the error page
   */
  public void setErrorURL(String pErrorURL) {
    if (StringUtils.isBlank(pErrorURL)) {
      mErrorURL = null;
    } else {
      mErrorURL = pErrorURL;
    }
  }

  /**
   * Gets the value of the RecipientName: field.
   * 
   * @return the value of the RecipientName: field.
   */
  public String getRecipientName() {
    return mRecipientName;
  }

  /**
   * Sets the value of the RecipientName: field.
   * 
   * @param pRecipientName -
   *          the value of the RecipientName: field.
   */
  public void setRecipientName(String pRecipientName) {
    mRecipientName = pRecipientName;
  }

  /**
   * Gets the value of the RecipientEmail: field.
   * 
   * @return the value of the RecipientEmail: field.
   */
  public String getRecipientEmail() {
    return mRecipientEmail;
  }

  /**
   * Sets the value of the RecipientEmail: field.
   * 
   * @param pRecipientEmail -
   *          the value of the RecipientEmail: field.
   */
  public void setRecipientEmail(String pRecipientEmail) {
    mRecipientEmail = pRecipientEmail.trim();
  }

  /**
   * Gets the value of the SenderName: field.
   * 
   * @return the value of the SenderName: field.
   */
  public String getSenderName() {
    return mSenderName;
  }

  /**
   * Sets the value of the SenderName: field.
   * 
   * @param pSenderName -
   *          the value of the SenderName: field.
   */
  public void setSenderName(String pSenderName) {
    mSenderName = pSenderName;
  }

  /**
   * Gets the value of the SenderEmail: field.
   * 
   * @return the value of the SenderEmail: field.
   */
  public String getSenderEmail() {
    return mSenderEmail;
  }

  /**
   * Sets the value of the SenderEmail: field.
   * 
   * @param pSenderEmail -
   *          the value of the SenderEmail: field.
   */
  public void setSenderEmail(String pSenderEmail) {
    mSenderEmail = pSenderEmail.trim();
  }
  
  /**
   * Gets the value of the SiteId: field.
   * 
   * @return the value of the SiteId: field.
   */
  public String getSiteId() {
    return mSiteId;
  }

  /**
   * Sets the value of the SiteId: field.
   * 
   * @param pSiteId -
   *          the value of the SiteId: field.
   */
  public void setSiteId(String pSiteId) {
    mSiteId = pSiteId;
  }

  /**
   * Gets the value of the Message: field.
   * 
   * @return the value of the Message: field.
   */
  public String getMessage() {
    return mMessage;
  }

  /**
   * Sets the value of the Message: field.
   * 
   * @param pMessage -
   *          the value of the Message: field.
   */
  public void setMessage(String pMessage) {
    mMessage = pMessage;
  }

  /**
   * Sets the URL for the email template used to send the email. This is
   * configured in the component property file.
   * 
   * @param pTemplateUrl -
   *          the URL
   */
  public void setTemplateUrl(String pTemplateUrl) {
    mTemplateUrl = pTemplateUrl;
  }

  /**
   * Gets the URL for the email template used to send the email. This is
   * configured in the component property file.
   * 
   * @return the URL
   */
  public String getTemplateUrl() {
    return mTemplateUrl;
  }
  
  //--------------------------------------------------
  // property: mTemplateUrlName
  private String mTemplateUrlName;

  /**
   * @return the String
   */
  public String getTemplateUrlName() {
    return mTemplateUrlName;
  }

  /**
   * @param mTemplateUrlName the String to set
   */
  public void setTemplateUrlName(String pTemplateUrlName) {
    mTemplateUrlName = pTemplateUrlName;
  }
  
  /**
   * Recipient name parameter name.
   */
  private String mRecipientNameParamName = null;

  /**
   * E-mail recipient parameter name.
   */
  private String mRecipientEmailParamName = null;

  /**
   * Sender parameter name.
   */
  private String mSenderNameParamName = null;

  /**
   * E-mail sender parameter name.
   */
  private String mSenderEmailParamName = null;
  
  /**
   * Gets the name of the Name value used for the To: field. This is configured
   * in the component property file.
   * 
   * @return the name of the Name value used for the To: field.
   */
  public String getRecipientNameParamName() {
    return mRecipientNameParamName;
  }

  /**
   * Sets the name of the Name value used for the To: field. This is configured
   * in the component property file.
   * 
   * @param pRecipientNameParamName -
   *          the name of the Name value used for the To: field.
   */
  public void setRecipientNameParamName(String pRecipientNameParamName) {
    mRecipientNameParamName = pRecipientNameParamName;
  }

  /**
   * Gets the name of the Email value used for the To: field. This is configured
   * in the component property file.
   * 
   * @return the name of the Email value used for the To: field.
   */
  public String getRecipientEmailParamName() {
    return mRecipientEmailParamName;
  }

  /**
   * Sets the name of the Email value used for the To: field. This is configured
   * in the component property file.
   * 
   * @param pRecipientEmailParamName -
   *          the name of the Email value used for the To: field.
   */
  public void setRecipientEmailParamName(String pRecipientEmailParamName) {
    mRecipientEmailParamName = pRecipientEmailParamName;
  }

  /**
   * Gets the name of the Name value used for the From: field. This is
   * configured in the component property file.
   * 
   * @return the name of the Name value used for the from field.
   */
  public String getSenderNameParamName() {
    return mSenderNameParamName;
  }

  /**
   * Sets the name of the Name value used for the From: field. This is
   * configured in the component property file.
   * 
   * @param pSenderNameParamName -
   *          the name of the Name value used for the From: field.
   */
  public void setSenderNameParamName(String pSenderNameParamName) {
    mSenderNameParamName = pSenderNameParamName;
  }

  /**
   * Gets the name of the Email value used for the From: field. This is
   * configured in the component property file.
   * 
   * @return the name of the Email value used for the From: field.
   */
  public String getSenderEmailParamName() {
    return mSenderEmailParamName;
  }

  /**
   * Sets the name of the Email value used for the From: field. This is
   * configured in the component property file.
   * 
   * @param pSenderEmailParamName -
   *          the name of the Email value used for the From: field.
   */
  public void setSenderEmailParamName(String pSenderEmailParamName) {
    mSenderEmailParamName = pSenderEmailParamName;
  }

  /**
   * Message parameter name.
   */
  private String mMessageParamName = null;
  
  /**
   * Gets the name of the parameter used for the Message: field. This is
   * configured in the component property file.
   * 
   * @return the name of the parameter used for the Message: field.
   */
  public String getMessageParamName() {
    return mMessageParamName;
  }

  /**
   * Sets the name of the parameter used for the Message: field. This is
   * configured in the component property file.
   * 
   * @param pMessageParamName -
   *          the name of the parameter used for the Comment: field.
   */
  public void setMessageParamName(String pMessageParamName) {
    mMessageParamName = pMessageParamName;
  }
  
  /**
   * SiteId parameter name.
   */
  private String mSiteIdParamName = null;
  
  /**
   * Gets the name of the parameter used for the SiteId: field. This is
   * configured in the component property file.
   * 
   * @return the name of the parameter used for the SiteId: field.
   */
  public String getSiteIdParamName() {
    return mSiteIdParamName;
  }

  /**
   * Sets the name of the parameter used for the SiteId: field. This is
   * configured in the component property file.
   * 
   * @param pSiteIdParamName -
   *          the name of the parameter used for the Comment: field.
   */
  public void setSiteIdParamName(String pSiteIdParamName) {
    mSiteIdParamName = pSiteIdParamName;
  }


  
  /**
   * @return the ProfileTools.
   */
  public ProfileTools getProfileTools() {
    return getProfile().getProfileTools();
  }

  // --------------------------------------------------
  // property: EmailTools
  private StoreEmailTools mEmailTools;

  /**
   * @return the StoreEmailTools
   */
  public StoreEmailTools getEmailTools() {
    return mEmailTools;
  }

  /**
   * @param EmailTools
   *          the StoreEmailTools to set
   */
  public void setEmailTools(StoreEmailTools pEmailTools) {
    mEmailTools = pEmailTools;
  }
  
  /**
   * Profile.
   */
  private Profile mProfile = null;

  /**
   * Sets The user profile associated with the email. The default profile is
   * used here. This is configured in the component property file.
   * 
   * @param pProfile -
   *          the user profile of the logged in user.
   */
  public void setProfile(Profile pProfile) {
    mProfile = pProfile;
  }

  /**
   * Gets the user profile associated with the email. The default profile is
   * used here. This is configured in the component property file.
   * 
   * @return the user profile of the logged in user.
   */
  public Profile getProfile() {
    return mProfile;
  }
  
  /**
   * Resource bundle name.
   */
  private String mResourceBundleName;

  
  /**
   * Resource bundle.
   */
  private ResourceBundle mResourceBundle;
  
  /**
   * @return the location of the resources bundle.
   */
  public String getResourceBundleName() {
    return mResourceBundleName;
  }

  /**
   * @param pResourceBundleName -
   *          the location of the resource bundle.
   */
  public void setResourceBundleName(String pResourceBundleName) {
    mResourceBundleName = pResourceBundleName;
  }
  
  /**
   * @return the request specific resources bundle.
   */
  public ResourceBundle getResourceBundle(DynamoHttpServletRequest pRequest) {
    if (mResourceBundle == null) {
      mResourceBundle = LayeredResourceBundle.getBundle(getResourceBundleName(), 
                                                        pRequest.getRequestLocale().getLocale());
    }

    return mResourceBundle;
  }
  
  //--------------------------------------------------
  // property: SubjectParamName
  private String mSubjectParamName;

  /**
   * @return the String
   */
  public String getSubjectParamName() {
    return mSubjectParamName;
  }

  /**
   * @param SubjectParamName the String to set
   */
  public void setSubjectParamName(String pSubjectParamName) {
    mSubjectParamName = pSubjectParamName;
  }
  
  //--------------------------------------------------
  // property: ActionResult
  private String mActionResult;

  /**
   * @return the String
   */
  public String getActionResult() {
    return mActionResult;
  }

  /**
   * @param ActionResult the String to set
   */
  public void setActionResult(String pActionResult) {
    mActionResult = pActionResult;
  }

  /**
   * Handles the form submit and sends the email.
   * 
   * @param pRequest -
   *          http request
   * @param pResponse -
   *          http response
   * @return true on success, false - otherwise
   * @throws java.lang.Exception
   *           if error occurs
   */
  public boolean handleSend(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws Exception {
    if (isLoggingDebug()) {
      logDebug("GenericEmailSenderFormHandler - [handleSend] = Entered in method \n");
    }

    if (!checkFormRedirect(null, getErrorURL(), pRequest, pResponse))
      return false;

    boolean isValidRecipientEmailFormat = getEmailTools().validateEmailAddress(getRecipientEmail());

    if (!isValidRecipientEmailFormat) {
      String msg = ResourceUtils.getMsgResource(StoreEmailTools.MSG_INVALID_RECIPIENT_EMAIL_FORMAT,
                                                getResourceBundleName(),
                                                getResourceBundle(pRequest));

      addFormException(new DropletFormException(msg, null));

    }

    boolean isValidSenderEmailFormat = getEmailTools().validateEmailAddress(
        getSenderEmail());

    if (!isValidSenderEmailFormat) {
      String msg = ResourceUtils.getMsgResource(StoreEmailTools.MSG_INVALID_SENDER_EMAIL_FORMAT,
                                                getResourceBundleName(),
                                                getResourceBundle(pRequest));

      addFormException(new DropletFormException(msg, null));
    }
    
    if (!checkFormRedirect(null, getErrorURL(), pRequest, pResponse))
      return false;

    try {
      // send email
      getEmailTools().sendEmail(getProfile(), collectParams());
      
      //set action result message
      setActionResult(MSG_ACTION_SUCCESS);
      
    } catch (TemplateEmailException e) {
      processException(e, "emailFormHandler.send.TemplateEmailException",
                       pRequest, pResponse);
    } catch (RepositoryException re) {
      processException(re, "emailFormHandler.send.RepositoryException",
                       pRequest, pResponse);
    }

    if (isLoggingDebug()) {
      logDebug("GenericEmailSenderFormHandler - [handleSend] = After sendEmail()");
    }

    return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
  }
  
  /**
   * Collect parameters for e-mail templates
   * @return map of parameters
   */
  protected Map collectParams() {
    // collect params from form handler to map and pass them into tools class
    Map emailParams = new HashMap();
    emailParams.put(getTemplateUrlName(), getTemplateUrl());
    emailParams.put(getSenderNameParamName(), getSenderName());
    emailParams.put(getSenderEmailParamName(), getSenderEmail());
    emailParams.put(getRecipientNameParamName(), getRecipientName());
    emailParams.put(getRecipientEmailParamName(), getRecipientEmail());
    emailParams.put(getMessageParamName(), getMessage());
    emailParams.put(getSubjectParamName(), getSubject());
    emailParams.put(getSiteIdParamName(), getSiteId());
    
    return emailParams;
  }

  /**
   * Add a user error message to the form exceptions.
   * 
   * @param pException - exception to process
   * @param pMsgId - message id
   * @param pRequest - http request
   * @param pResponse - http response
   */
  public void processException(Throwable pException, String pMsgId,
      DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
    String msg = ResourceUtils.getMsgResource(pMsgId,
                                              getResourceBundleName(),
                                              getResourceBundle(pRequest));

    // If there is a message in the exception then add that
    if (pException != null) {
      String msg2 = pException.getLocalizedMessage();

      if (!StringUtils.isBlank(msg2)) {
        msg += (" " + msg2);
      }
    }

    DropletException de;

    if (pException == null) {
      de = new DropletException(msg);
    } else {
      de = new DropletException(msg, pException, pMsgId);
    }

    addFormException(de);

    if (isLoggingDebug()) {
      logDebug(pException);
    }
  }
}