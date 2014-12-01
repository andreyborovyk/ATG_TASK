/*<ATGCOPYRIGHT>
 * Copyright (C) 1999-2011 Art Technology Group, Inc.
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

package atg.service.email;

import atg.droplet.DropletFormException;
import atg.droplet.GenericFormHandler;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;
import java.io.IOException;
import java.util.Locale;
import javax.servlet.ServletException;

/**
 * This class allows an application to send an email using data
 * entered by the user in an html form.  It refers to an SMTPEmailSender
 * service to perform the send operation.
 * <p>
 * A typical form would look like this:
 * <xmp>
 
 * <form action="`request.getRequestURI()`" method="POST">
 *   From: <input type="text" bean="EmailFormHandler.sender"> <br>
 *   To: <input type="text" bean="EmailFormHandler.recipient"> <br>
 *   Subject: <input type="text" bean="EmailFormHandler.subject"> <p>
 *   Body: <input type="textarea" bean="EmailFormHandler.body"><p>
 *   <input type="submit" bean="EmailFormHandler.sendEmail">
 * </form>
 * </xmp>
 * 
 * @see atg.service.email.SMTPEmailSender
 * @author Pierre Billon
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/service/email/EmailFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 *
 **/

public
class EmailFormHandler
extends GenericFormHandler
{
  //-------------------------------------
  // Class version string

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/service/email/EmailFormHandler.java#2 $$Change: 651448 $";

  /** Resource bundle that holds messages to dispay to the end user */
  public static String RESOURCE_BUNDLE = "atg.service.email.UserMessages";

  static final ParameterName LOCALE_PARAM = ParameterName.getParameterName("locale");

  protected static final String MSG_NO_SENDER_EMAIL = "NoSenderEmail";
  protected static final String MSG_INVALID_SENDER_EMAIL = "InvalidSenderEmail";
  protected static final String MSG_NO_RECIPIENT_EMAIL = "NoRecipientEmail";
  protected static final String MSG_INVALID_RECIPIENT_EMAIL = "InvalidRecipientEmail";
  protected static final String MSG_EMPTY_MESSAGE = "EmptyMessage";
  protected static final String MSG_NO_EMAIL_HOST_NAME = "NoEmailHostName";

  //-------------------------------------
  // Properties

  //-------------------------------------
  // property: emailSenderService
  SMTPEmailSender mEmailSender;

  /**
   * Sets property emailSenderService, wich will be used to send the email.
   * Note that it will be possible to send email only if the hostname and 
   * port number properties of that component are properly configured.
   *
   * @beaninfo
   *   description: service used to send the email
   *   displayName: Email Sender
   **/
  public void setEmailSenderService(SMTPEmailSender pEmailSender) {
    mEmailSender = pEmailSender;
  }

  /**
   * Returns property emailSenderService.
   **/
  public SMTPEmailSender getEmailSenderService() {
    return mEmailSender;
  }


  //-------------------------------------
  // property: sendSuccessURL
  String mSendSuccessURL;

  /**
   * Sets property sendSuccessURL, identifying the page this method
   * will redirect to if the email is successfully sent.  If null,
   * no redirection is performed.
   * @beaninfo
   *   description: URL to redirect to if email send successfully
   *   displayName: Send Success URL
   **/
  public void setSendSuccessURL(String pSendSuccessURL) {
    mSendSuccessURL = pSendSuccessURL;
  }

  /**
   * Returns property sendSuccessURL.
   **/
  public String getSendSuccessURL() {
    return mSendSuccessURL;
  }


  //-------------------------------------
  // property: sendErrorURL
  String mSendErrorURL;

  /**
   * Sets property sendErrorURL, identifying the page this method
   * will redirect to if there is an error sending the email.  If null,
   * no redirection is performed.
   * @beaninfo
   *   description: URL to redirect to if email is not send successfully
   *   displayName: Send Error URL
   **/
  public void setSendErrorURL(String pSendErrorURL) {
    mSendErrorURL = pSendErrorURL;
  }

  /**
   * Returns property sendErrorURL
   **/
  public String getSendErrorURL() {
    return mSendErrorURL;
  }

  //-------------------------------------
  // property: sender
  String mSender;

  /**
   * Sets property sender, used as the From: tag of the email. This field
   * should contain a valid email address, either in the form <i>email-address@hostname</i>,
   * or in the form <i>user name &lt;email-address@hostname&gt;</i>.
   * @beaninfo
   *   description: email's sender tag
   *   displayName: Email Sender
   **/
  public void setSender(String pSender) {
    mSender = pSender;
  }

  /**
   * Returns property sender.
   **/
  public String getSender() {
    return mSender;
  }

  //-------------------------------------
  // property: recipient
  String mRecipient;

  /**
   * Sets property recipient, used as the <i>To:</i> tag of the email. This field
   * should contain a valid email address, either in the form <i>email-address@hostname</i>,
   * or in the form <i>user name &lt;email-address@hostname&gt;</i>.
   * @beaninfo
   *   description: email's recipient tag
   *   displayName: Email Recipient
   **/
  public void setRecipient(String pRecipient) {
    mRecipient = pRecipient;
  }

  /**
   * Returns property recipient.
   **/
  public String getRecipient() {
    return mRecipient;
  }


  //-------------------------------------
  // property: subject
  String mSubject;

  /**
   * Sets property subject, used as the <i>Subject:</i> tag of the email.
   * @beaninfo
   *   description: email's subject tag
   *   displayName: Email Subject
   **/
  public void setSubject(String pSubject) {
    mSubject = pSubject;
  }

  /**
   * Returns property subject.
   **/
  public String getSubject() {
    return mSubject;
  }

  //-------------------------------------
  // property: body
  String mBody;

  /**
   * Sets property body, the content of the email.
   * @beaninfo
   *   description: body of the email
   *   displayName: Body
   **/
  public void setBody(String pBody) {
    mBody = pBody;
  }

  /**
   * Returns property body.
   **/
  public String getBody() {
    return mBody;
  }

  //-------------------------------------
  // property: DefaultLocale
  Locale mDefaultLocale;

  /**
   * Sets property DefaultLocale for localizing user messages
   **/
  public void setDefaultLocale(Locale pDefaultLocale) {
    mDefaultLocale = pDefaultLocale;
  }

  /**
   * Returns property DefaultLocale. If the property value is null, then
   * JVM's default locale is returned.
   **/
  public Locale getDefaultLocale() {
    if (mDefaultLocale != null)
      return mDefaultLocale;
    else
      return Locale.getDefault();
  }

  //-------------------------------------
  // property: UseRequestLocale
  boolean mUseRequestLocale = true;

  /**
   * Sets property UseRequestLocale which determines whether to use
   * the requestLocale for localizing user messages
   **/
  public void setUseRequestLocale(boolean pUseRequestLocale) {
    mUseRequestLocale = pUseRequestLocale;
  }

  /**
   * Returns property UseRequestLocale
   **/
  public boolean isUseRequestLocale() {
    return mUseRequestLocale;
  }

                
  //-------------------------------------
  // Constructors
  //-------------------------------------
  /**
   * Constructs an EmailFormHandler object.
   */
  public EmailFormHandler () {
    // bean constructor
  }

                
  //-------------------------------------
  // Protected Methods
  //-------------------------------------

  //-------------------------------------
  /**
   *
   * Perform the actual send operation. A derived class will
   * typically override this method, and either send the email itself, or
   * call this method after performing some editing on the message.
   *
   * @return a boolean value indicating whether the send operation was successful
   **/
  protected boolean sendMail(DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    // Check validity of entered fields
    if (validate(pRequest,pResponse) == false)
       return false;

    // Entered fields being ok, send email
    try {
      getEmailSenderService().sendEmailMessage(getSender(), 
                                               getRecipient(), 
                                               getSubject(), 
                                               getBody());
    }
    catch (EmailException exc) {
      if ( isLoggingError() ) {
        logError("Failed to send meail message:" +
                 "Remember to set /atg/dynamo/service/SMTPEmail.emailHandlerHostName and " +
                 "/atg/dynamo/service/SMTPEmail.emailHandlerPort", exc);
      }

      String errMsg = EmailUserMessage.format(MSG_NO_EMAIL_HOST_NAME, getUserLocale(pRequest, pResponse));
      addFormException(new DropletFormException(errMsg, exc, "errorSendingEmail"));
      return false;
    }
    // Clear form fields before returning to page
    setSender(null);
    setRecipient(null);
    setSubject(null);
    setBody(null);

    return true;
  }
  
  //-------------------------------------
  /**
   *
   * Validate the email's fields for correctness.<p>
   * Currently only checks that the sender and recipient addresses
   * contain a '@' and that both the subject andd body aren't empty.
   * <br>
   * This protected method is intended to be overriden by a subclass
   * to provide customized error checking.
   *
   * @return a boolean value indicating whether the email message is ok.
   **/
  protected boolean validate(DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    boolean isError = false;

    if (getSender() == null) {
      String errMsg = EmailUserMessage.format(MSG_NO_SENDER_EMAIL, getUserLocale(pRequest, pResponse));
      addFormException(new DropletFormException(errMsg,
                                                getAbsoluteName() + ".sender",
                                                "noSender"));
      isError = true;
    }
    else if (getSender().indexOf("@") == -1) {
      String errMsg = EmailUserMessage.format(MSG_INVALID_SENDER_EMAIL, getUserLocale(pRequest, pResponse));
      addFormException(new DropletFormException(errMsg,                                                
                                                getAbsoluteName() + ".sender",
                                                "invalidSender"));
      isError = true;
    }


    if (getRecipient() == null) {
      String errMsg = EmailUserMessage.format(MSG_NO_RECIPIENT_EMAIL, getUserLocale(pRequest, pResponse));
      addFormException(new DropletFormException(errMsg,
                                                new String(getAbsoluteName() + ".sender"),
                                                "noRecipient"));
      isError = true;
    }
    else if (getRecipient().indexOf("@") == -1) {
      String errMsg = EmailUserMessage.format(MSG_INVALID_RECIPIENT_EMAIL, getUserLocale(pRequest, pResponse));
      addFormException(new DropletFormException(errMsg,
                                                new String(getAbsoluteName() + ".sender"),
                                                "invalidRecipient"));
      isError = true;
    }
    if ((getSubject() == null || getSubject().trim().length() == 0) && 
        (getBody() == null || getBody().trim().length() == 0)) {
      String errMsg = EmailUserMessage.format(MSG_EMPTY_MESSAGE, getUserLocale(pRequest, pResponse));
      addFormException(new DropletFormException(errMsg,
                                                new String(getAbsoluteName() + ".sender"),
                                                "emptyEmail"));
      isError = true;
    }
    // Return true if there is no error
    return (!isError);
  }
  
                
  //-------------------------------------
  // Public Methods
  //-------------------------------------

  //-------------------------------------
  /**
   *
   * This method is typically associated with the form's submit button.  If the
   * email is successfully sent, it redirects the user to the url identified by
   * the <code>sendSuccessURL</code> property, if this property is set. If the email 
   * is not sent, it redirects the user to the url identified by the
   * <code>sendErrorURL</code> property, if this property is set.
   * <p>
   * The sending operation is broken down into three steps:
   * <ol>
   * <li>The email's tags are validated by the <code>validate()</code> method
   * <li>The email is sent by the <code>sendMail()</code> method
   * <li>The user is redirected on success/failure by the <code>handleSendEmail()</code> handler
   * </ol><br>
   * The <code>sendMail()</code> and <code>validate()</code> methods are protected methods that developers
   * are encouraged to override in a derived class to perform customized error checking
   * or to modify the email tags prior to sending the email.
   * <p>
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @return a boolean value, indicating that a redirect was not performed
   * @exception IOException
   * @exception ServletException
   **/
  public boolean handleSendEmail (DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException
  {
    // Send out the email
    if (sendMail(pRequest, pResponse) == true) {
      if (getSendSuccessURL() != null) {
        pResponse.sendLocalRedirect(getSendSuccessURL(), pRequest);
      }
      return true;
    }
    else {
      if (getSendErrorURL() != null) {
        pResponse.sendLocalRedirect(getSendErrorURL(), pRequest);
        return false;
      }
      return true;
    }
  }

  /**
   * Returns the locale associated with the request. The method first searches
   * for a request paramater named <code>locale</code>. This value can be
   * either a java.util.Locale object or a String which represents the locale.
   * Next if the <code>useRequestLocale</code> property is true, then the locale
   * of the request will be returned. Finally, if the locale cannot be determined,
   * the the <code>defaultLocale</code> property is used.
   */
  public Locale getUserLocale(DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    Object obj = pRequest.getObjectParameter(LOCALE_PARAM);
    if (obj instanceof Locale)
      return (Locale)obj;
    else if (obj instanceof String)
      return RequestLocale.getCachedLocale((String)obj);
    else if (isUseRequestLocale()) {
      RequestLocale requestLocale = pRequest.getRequestLocale();
      if (requestLocale != null)
        return requestLocale.getLocale();
    }

    return getDefaultLocale();
  }


  //-------------------------------------
  // java.lang.Object method overrides
  //-------------------------------------
  /**
   * @return a String representation of this object
   */
  public String toString () 
  {
    StringBuilder buf = new StringBuilder ();
    buf.append (getClass ().getName ());
    buf.append ('[');
    buf.append ("emailSenderService: ");
    if(getEmailSenderService() == null) buf.append("<null>");
    else buf.append("<ok>");
    buf.append (", sender: ");
    buf.append (getSender());
    buf.append (", recipient: ");
    buf.append (getRecipient());
    buf.append (", subject: ");
    buf.append (getSubject());
    buf.append (", body: ");
    buf.append (getBody());
    buf.append (']');
    return buf.toString ();
  }

  //-------------------------------------
}
