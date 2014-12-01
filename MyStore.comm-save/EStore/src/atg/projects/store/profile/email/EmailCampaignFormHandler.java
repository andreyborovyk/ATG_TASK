/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2010 Art Technology Group, Inc.
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
package atg.projects.store.profile.email;

import atg.core.util.UserMessage;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.projects.store.email.StoreEmailTools;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.profile.StoreProfileTools;
import atg.repository.RepositoryException;
import atg.repository.servlet.RepositoryFormHandler;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import java.io.IOException;

import javax.servlet.ServletException;


/**
 * This formhandler is used to create an EmailRecipient repository item
 * when a user signs up to receive marketing email campaigns.
 *
 * @author ATG
 * @version $Id: EmailCampaignFormHandler.java,v 1.7 2004/09/10 12:28:30
 *          sdere Exp $
 */
public class EmailCampaignFormHandler extends RepositoryFormHandler {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/profile/email/EmailCampaignFormHandler.java#3 $$Change: 635816 $";

  /**
   * Resource bundle name.
   */
  static final String RESOURCE_BUNDLE = "atg.commerce.profile.UserMessages";


  /**
   * Registration e-mail.
   */
  private String mRegistrationEmail;

  /**
   * Email Campaign Source Code.
   */
  private String mSourceCode;

  /**
  * The email to use to register.
  * @return registration e-mail
  */
  public String getRegistrationEmail() {
    return mRegistrationEmail;
  }

  /**
   * The email to use to register.
   * @param pRegistrationEmail - registration e-mail
   */
  public void setRegistrationEmail(String pRegistrationEmail) {
    mRegistrationEmail = pRegistrationEmail;
  }

  /**
   * The code that indicates the source of the form input.
   * @return source code
   */
  public String getSourceCode() {
    return mSourceCode;
  }

  /**
   * The code that indicates the source of the form input.
   * @param pSourceCode - source code
   */
  public void setSourceCode(String pSourceCode) {
    mSourceCode = pSourceCode;
  }

   
  //--------------------------------------------------
  // property: EmailTools
  private StoreEmailTools mEmailTools;

  /**
   * @return the StoreEmailTools
   */
  public StoreEmailTools getEmailTools() {
    return mEmailTools;
  }

  /**
   * @param EmailTools the StoreEmailTools to set
   */
  public void setEmailTools(StoreEmailTools pEmailTools) {
    mEmailTools = pEmailTools;
  }

  /**
   * Creates an EmailRecipient with the user's email.
   *
   * @param pRequest - HTTP Request
   * @param pResponse - HTTP Response
   *
   * @return True if success False if failed
   *
   * @throws ServletException - if servlet exception occurs
   * @throws IOException - if I/O exception occurs
   */
  public boolean handleCreateEmailRecipient(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    StoreProfileTools pTools = (StoreProfileTools) getEmailTools().getProfileTools();
    String email = getRegistrationEmail();
    

    if (!getEmailTools().validateEmailAddress(email)) {
      generateFormException(StoreEmailTools.MSG_ERR_BAD_EMAIL, pRequest);

      return checkFormRedirect(getCreateSuccessURL(), getCreateErrorURL(), pRequest, pResponse);
    }

    // check to see if email is already signed up, if so return quietly
    if (!(pTools.retrieveEmailRecipient(email) == null)) {
      if (isLoggingDebug()) {
        logDebug("Email is already registered so no need to do anything");
        logDebug("Exiting quietly");
      }

      return checkFormRedirect(getCreateSuccessURL(), getCreateErrorURL(), pRequest, pResponse);
    }

    if (!getFormError()) {
      try {
        pTools.createEmailRecipient(email, getSourceCode());

      } catch (RepositoryException repositoryExc) {
        generateFormException(
            StoreEmailTools.MSG_ERR_CREATING_EMAIL_RECIPIENT,
            repositoryExc, pRequest);

        if (isLoggingError()) {
          logError(LogUtils.formatMinor(repositoryExc.getMessage()),
              repositoryExc);
        }
      }
    }

    return checkFormRedirect(getCreateSuccessURL(), getCreateErrorURL(), pRequest, pResponse);
  }
  
  /**
   * Create a form exception, by looking up the excption code in a resource
   * file identified by the RESOURCE_BUNDLE constant (defined above).
   *
   * @param whatException - String description of exception
   * @param repositoryExc - RepositoryException
   * @param pRequest - HTTP Request Object
   */
  protected void generateFormException(String whatException, RepositoryException repositoryExc,
    DynamoHttpServletRequest pRequest) {
    //fetch the correct error message
    //based on user's locale.
    String errorStr = UserMessage.getString(RESOURCE_BUNDLE, whatException, ServletUtil.getUserLocale(pRequest));
    //create the DropletException both with the translated user message, 
    //and with the whatException
    addFormException(new DropletFormException(errorStr, repositoryExc, whatException));
  }

  /**
   * Create a form exception, by looking up the excption code in a resource
   * file identified by the RESOURCE_BUNDLE constant (defined above).
   *
   * @param pMsgKey - key to the message in the bundl
   * @param pRequest - HTTP Request Object
   */
  protected void generateFormException(String pMsgKey, DynamoHttpServletRequest pRequest) {
    //fetch the correct error message
    //based on user's locale.
    String errorStr = UserMessage.getString(RESOURCE_BUNDLE, pMsgKey, ServletUtil.getUserLocale(pRequest));
    //create the DropletException with the translated user message
    addFormException(new DropletException(errorStr));
  }
}
