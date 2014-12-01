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
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.service.email;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import atg.core.util.ResourceUtils;
import atg.nucleus.TimedOperationService;

/**
 *
 * This service can be used to send an email message with the SMTP protocol
 *
 * @author Bob Mason
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/service/email/SMTPEmailSender.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo
 *   description: Sends e-mail messages via SMTP
 *   attribute: functionalComponentCategory E-Mail
 *   attribute: featureComponentCategory
 **/

public class SMTPEmailSender
extends TimedOperationService
  implements EmailListener, EmailSender, TrackableEmailSender, EmailMessageSender
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/service/email/SMTPEmailSender.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.service.ServiceResources";

  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  //-------------------------------------
  // Constants

  /** This tells the JavaMail Session class which protocol to use **/
  static String PROTOCOL_NAME = "smtp";

  //-------------------------------------
  // Properties
  boolean mTestMode = false;
  boolean mDummyMode = false;

  String mCharSet = null;


  String [] mInvalidSenderExceptionTags;

  String [] mDefaultRecipients;
  String mDefaultFrom;
  String mDefaultCc;
  String mDefaultBcc;
  String mDefaultSubject;
  String [] mDefaultExtraHeaders;
  String mDefaultBody;

  String mEmailHandlerHostName = "127.0.0.1";
  int mEmailHandlerPort = 25;

  String mUsername;
  String mPassword;

  String mSourceHostName;

  int mRetryConnectionCount = 2;
  int mWaitForConnectionMillis = 5000;

  //-------------------------------------
  // Member Variables
  String mDefaultSourceHostName;

  /**
   * Constructs a SMTPEmailSender
   */
  public SMTPEmailSender () {
    // this isn't really needed anymore, but leave it in s.t.
    // getSourceHostName() returns meaningful results
    try {
      InetAddress iaddr = InetAddress.getLocalHost();
      if (iaddr != null)
        mDefaultSourceHostName = iaddr.getHostName();

    }
    catch (UnknownHostException exc) {
      if (isLoggingError())
      {
        Object[] mPatternArgs = { exc.toString() };
        logError(
          ResourceUtils.getMsgResource("emailSMTPEmailSenderCouldNotConfigure",
                                       MY_RESOURCE_NAME,
                                       sResourceBundle, mPatternArgs));
      }
    }
  }


  //-------------------------------------
  // SMTP Email Properties
  //-------------------------------------

  /**
   */
  public String [] getInvalidSenderExceptionTags() {
    return mInvalidSenderExceptionTags;
  }

  /**
   */
  public void setInvalidSenderExceptionTags(String [] pInvalidSenderExceptionTags) {
    mInvalidSenderExceptionTags = pInvalidSenderExceptionTags;
  }
  /**
   * Returns the default list of Recipients that should receive email
   *
   * @beaninfo
   *   description: Default list of e-mail recipients
   */
  public String [] getDefaultRecipients() {
    return mDefaultRecipients;
  }

  /**
   * Sets the default list of Recipients that should receive email
   */
  public void setDefaultRecipients(String [] pRecipients) {
    mDefaultRecipients = pRecipients;
  }

  /**
   * Returns whether this component is in test mode, i.e. it will not
   * send messages but it will attempt to connect to the SMTP mail
   * server and it will output messages and headers to standard
   * error.
   *
   * @return true if this component is in test mode
   *
   * @beaninfo
   *   description: TestMode == true will not send msg but will attempt
   *   to connect to the mail server, mail message and headers are outputted
   *   to standard error
   */
  public boolean getTestMode() {
    return mTestMode;
  }

  /**
   * Sets this component in test mode, i.e. it will not send messages
   * but it will attempt to connect to the SMTP mail server and it
   * will output messages and headers to standard error.
   *
   * @param pTestMode true if this component is to be in test mode
   */
  public void setTestMode(boolean pTestMode) {
    mTestMode = pTestMode;
  }

  /**
   * Returns whether this component is in dummy mode, i.e. it will not
   * send messages, it will not attempt to connect to the SMTP mail
   * server but it will output messages and headers to standard
   * error.
   *
   * @return true if this component is in dummy mode
   *
   * @beaninfo
   *   description: DummyMode == true will not send msg and will not attempt
   *   to connect to the mail server, mail message and headers are outputted
   *   to standard error
   */
  public boolean getDummyMode() {
    return mDummyMode;
  }

  /**
   * Sets this component in dummy mode, i.e. it will not send
   * messages, it will attempt to connect to the SMTP mail server but
   * it will output messages and headers to standard error.
   *
   * @param pDummyMode true if this component is to be in dummy mode
   */
  public void setDummyMode(boolean pDummyMode) {
    mDummyMode = pDummyMode;
  }

  /**
   * Returns the default charSet
   *
   * @beaninfo
   *   description: Default charSet
   */
  public String getCharSet() {
    return mCharSet;
  }

  /**
   * Sets the default charSet.
   */
  public void setCharSet(String pCharSet) {
    mCharSet = pCharSet;
  }


  /**
   * Returns the default sender (From:) field.
   *
   * @beaninfo
   *   description: Default sender (From:) field
   */
  public String getDefaultFrom() {
    return mDefaultFrom;
  }

  /**
   * Sets the default sender (From:) field.
   */
  public void setDefaultFrom(String pFrom) {
    mDefaultFrom = pFrom;
  }


  /**
   * Returns the default sender (Cc:) field.
   *
   * @beaninfo
   *   description: Default sender (Cc:) field
   */
  public String getDefaultCc() {
    return mDefaultCc;
  }

  /**
   * Sets the default sender (Cc:) field.
   */
  public void setDefaultCc(String pCc) {
    mDefaultCc = pCc;
  }


  /**
   * Returns the default sender (Bcc:) field.
   *
   * @beaninfo
   *   description: Default sender (Bcc:) field
   */
  public String getDefaultBcc() {
    return mDefaultBcc;
  }

  /**
   * Sets the default sender (Bcc:) field.
   */
  public void setDefaultBcc(String pBcc) {
    mDefaultBcc = pBcc;
  }

  /**
   * Returns the default subject field.
   *
   * @beaninfo
   *   description: Default subject field
   */
  public String getDefaultSubject() {
    return mDefaultSubject;
  }

  /**
   * Sets the default subject field.
   */
  public void setDefaultSubject(String pSubject) {
    mDefaultSubject = pSubject;
  }

  /**
   * Returns the default list of extra headers that should be used in the email
   * (e.g. <code>"X-URL: http://foo.bar.com/"</code>)
   *
   * @beaninfo
   *   description: Default list of extra headers to be included in e-mail messages
   */
  public String [] getDefaultExtraHeaders() {
    return mDefaultExtraHeaders;
  }

  /**
   * Sets the default list of extra headers that should be used in the email
   * (e.g. <code>"X-URL: http://foo.bar.com/"</code>)
   */
  public void setDefaultExtraHeaders(String [] pExtraHeaders) {
    mDefaultExtraHeaders = pExtraHeaders;
  }

  /**
   * Returns the default message body, that should be prepended to all emails
   *
   * @beaninfo
   *   description: Message body that should be prepended to all e-mail messages
   */
  public String getDefaultBody() {
    return mDefaultBody;
  }

  /**
   * Sets the default message body, that should be prepended to all emails
   */
  public void setDefaultBody(String pBody) {
    mDefaultBody = pBody;
  }

  /**
   * Returns the SMTP host used to send email.  This defaults to the
   * localhost's host name
   *
   * @beaninfo
   *   description: The host name of the SMTP server
   */
  public String getEmailHandlerHostName() {
    return mEmailHandlerHostName;
  }

  /**
   * Sets the SMTP host used to send email.
   */
  public void setEmailHandlerHostName(String pHost) {
    mEmailHandlerHostName = pHost;
  }

  /**
   * Returns the SMTP port used to send email.  This defaults to 25.
   *
   * @beaninfo
   *   description: The port number used by the SMTP server
   */
  public int getEmailHandlerPort() {
    return mEmailHandlerPort;
  }

  /**
   * Sets the SMTP port used to send email. <BR>
   * <font size=-1>You should not need to change this property for standard
   * SMTP configurations.</font>
   */
  public void setEmailHandlerPort(int pPort) {
    mEmailHandlerPort = pPort;
  }

  /**
   * Returns the user name used to authenticate on the SMTP server.  This
   * defaults to no user name.
   *
   * @return user name used to authenticate on the SMTP server
   *
   * @beaninfo
   *   description: The user name used to authenticate on the SMTP server
   */
  public String getUsername() { return mUsername; }

  /**
   * Sets the user name used to authenticate on the SMTP server.
   *
   * @param pUsername user name used to authenticate on the SMTP server
   */
  public void setUsername(String pUsername) { mUsername = pUsername; }

  /**
   * Returns the password used to authenticate on the SMTP server.  This
   * defaults to no password.
   *
   * @return password used to authenticate on the SMTP server
   *
   * @beaninfo
   *   description: The password used to authenticate on the SMTP server
   */
  public String getPassword() { return mPassword; }

  /**
   * Sets the password used to authenticate on the SMTP server.
   *
   * @param pPassword password used to authenticate on the SMTP server
   */
  public void setPassword(String pPassword) { mPassword = pPassword; }

  /**
   * Returns the hostname from which the email is coming from
   *
   * @beaninfo
   *   description: Host name from which the e-mail originates
   */
  public String getSourceHostName() {
    if (mSourceHostName == null)
      return mDefaultSourceHostName;
    return mSourceHostName;
  }

  /**
   * Sets the hostname from which the email is coming from
   */
  public void setSourceHostName(String pHostName) {
    mSourceHostName = pHostName;
  }

  /**
   * If the default body property is set, returns a new body, with the
   * prepended body content. Otherwise returns the String passed in
   */
  String getBodyContent(String pBody) {
    if (getDefaultBody() == null) {
      return pBody;
    }
    else {
      StringBuffer bodyBuf = new StringBuffer();
      bodyBuf.append(getDefaultBody()).append("\n").append(pBody);
      return bodyBuf.toString();
    }
  }

  public int getRetryConnectionCount(){
    return mRetryConnectionCount;
  }

  public void setRetryConnectionCount(int pRetryConnectionCount){
    if(pRetryConnectionCount > 0)
      mRetryConnectionCount = pRetryConnectionCount;
    else
      mRetryConnectionCount = 2;
  }

  public int getWaitForConnectionMillis(){
    return mWaitForConnectionMillis;
  }

  public void setWaitForConnectionMillis(int pWait){
    if(pWait > 0)
      mWaitForConnectionMillis = pWait;
    else
      mWaitForConnectionMillis = 5000;
  }

  /**
   * Sends a message via SMTP with the given body, and using the default
   * properties for the From, Recipients, and Subject fields. The message
   * content is prepended by any value for the default body property.
   * @param pBody The message content
   * @exception EmailException if there is any problem while sending mail
   */
  public void sendEmailMessage(String pBody) throws EmailException {
    EmailEvent emailEvent = new EmailEvent();
    emailEvent.setCharSet(getCharSet());
    emailEvent.setFrom(getDefaultFrom());
    emailEvent.setCc(getDefaultCc());
    emailEvent.setBcc(getDefaultBcc());
    emailEvent.setRecipientList(getDefaultRecipients());
    emailEvent.setSubject(getDefaultSubject());
    emailEvent.setBody(getBodyContent(pBody));

    // send the event
    sendEmailEvent(emailEvent);
  }

  //-------------------------------------
  // EmailSender interface
  //-------------------------------------

  /**
   * Sends a message via SMTP with the given parameters. The message
   * content is prepended by any value for the default body property.
   * @param pFrom Whom the mail is coming from
   * @param pRecipient The recipient that should receive the message
   * @param pSubject Subject line of the email message
   * @param pBody The message content
   * @exception EmailException if there is any problem while sending mail
   */
  public void sendEmailMessage(String pFrom,
                               String pRecipient,
                               String pSubject,
                               String pBody)
    throws EmailException
  {
    EmailEvent emailEvent = new EmailEvent();
    emailEvent.setCharSet(getCharSet());
    emailEvent.setFrom(pFrom);
    emailEvent.setRecipient(pRecipient);
    emailEvent.setSubject(pSubject);
    emailEvent.setBody(getBodyContent(pBody));

    // send the event
    sendEmailEvent(emailEvent);
  }

  /**
   * Sends a message via SMTP with the given parameters. The message
   * content is prepended by any value for the default body property.
   * @param pFrom Whom the mail is coming from
   * @param pRecipient The recipient that should receive the message
   * @param pSubject Subject line of the email message
   * @param pBody The message content
   * @param pExtraHeader An extra headers that should be used
   * in the email (e.g. <code>"X-URL: http://foo.bar.com/"</code>)
   * @exception EmailException if there is any problem while sending mail
   */
  public void sendEmailMessage(String pFrom,
                               String pRecipient,
                               String pSubject,
                               String pBody,
                               String pExtraHeader)
    throws EmailException
  {
    EmailEvent emailEvent = new EmailEvent();
    emailEvent.setCharSet(getCharSet());
    emailEvent.setFrom(pFrom);
    emailEvent.setRecipient(pRecipient);
    emailEvent.setSubject(pSubject);
    emailEvent.setBody(getBodyContent(pBody));
    String [] headers = new String[1];
    headers[0] = pExtraHeader;
    emailEvent.setExtraHeaders(headers);

    // send the event
    sendEmailEvent(emailEvent);
  }

  /**
   * Sends a message via SMTP with the given parameters. The message
   * content is prepended by any value for the default body property.
   * @param pFrom Whom the mail is coming from
   * @param pRecipients The list of recipients that should receive email
   * @param pSubject Subject line of the email message
   * @param pBody The message content
   * @exception EmailException if there is any problem while sending mail
   */
  public void sendEmailMessage(String pFrom,
                               String [] pRecipients,
                               String pSubject,
                               String pBody)
    throws EmailException
  {
    EmailEvent emailEvent = new EmailEvent();
    emailEvent.setCharSet(getCharSet());
    emailEvent.setFrom(pFrom);
    emailEvent.setRecipientList(pRecipients);
    emailEvent.setSubject(pSubject);
    emailEvent.setBody(getBodyContent(pBody));

    // send the event
    sendEmailEvent(emailEvent);
  }

  /**
   * Sends a message via SMTP with the given parameters. The message
   * content is prepended by any value for the default body property.
   * @param pFrom Whom the mail is coming from
   * @param pRecipient The recipient that should receive the message
   * @param pSubject Subject line of the email message
   * @param pBody The message content
   * @param pExtraHeaders The list of extra headers that should be used
   * in the email (e.g. <code>"X-URL: http://foo.bar.com/"</code>)
   * @exception EmailException if there is any problem while sending mail
   */
  public void sendEmailMessage(String pFrom,
                               String pRecipient,
                               String pSubject,
                               String pBody,
                               String [] pExtraHeaders)
    throws EmailException
  {
    sendEmailMessage(pFrom, pRecipient, pSubject, pBody, pExtraHeaders, null);
  }

  /**
   * Sends a message via SMTP with the given parameters. The message
   * content is prepended by any value for the default body property.
   * @param pFrom Whom the mail is coming from
   * @param pRecipients The list of recipients that should receive email
   * @param pSubject Subject line of the email message
   * @param pBody The message content
   * @param pExtraHeader An extra headers that should be used
   * in the email (e.g. <code>"X-URL: http://foo.bar.com/"</code>)
   * @exception EmailException if there is any problem while sending mail
   */
  public void sendEmailMessage(String pFrom,
                               String [] pRecipients,
                               String pSubject,
                               String pBody,
                               String pExtraHeader)
    throws EmailException
  {
    EmailEvent emailEvent = new EmailEvent();
    emailEvent.setCharSet(getCharSet());
    emailEvent.setFrom(pFrom);
    emailEvent.setRecipientList(pRecipients);
    emailEvent.setSubject(pSubject);
    emailEvent.setBody(getBodyContent(pBody));
    String [] headers = new String[1];
    headers[0] = pExtraHeader;
    emailEvent.setExtraHeaders(headers);

    // send the event
    sendEmailEvent(emailEvent);
  }

  /**
   * Sends a message via SMTP with the given parameters. The message
   * content is prepended by any value for the default body property.
   * @param pFrom Whom the mail is coming from
   * @param pRecipients The list of recipients that should receive email
   * @param pSubject Subject line of the email message
   * @param pBody The message content
   * @param pExtraHeaders The list of extra headers that should be used
   * in the email (e.g. <code>"X-URL: http://foo.bar.com/"</code>)
   * @exception EmailException if there is any problem while sending mail
   */
  public void sendEmailMessage(String pFrom,
                               String [] pRecipients,
                               String pSubject,
                               String pBody,
                               String [] pExtraHeaders)
    throws EmailException
  {
    sendEmailMessage(pFrom, pRecipients, pSubject, pBody, pExtraHeaders, null);
  }

  //-------------------------------------
  // TrackableEmailSender interface
  //-------------------------------------

  /**
   * Sends a message via SMTP with the given parameters. The message
   * content is prepended by any value for the default body property.
   * @param pFrom Whom the mail is coming from
   * @param pRecipient The recipient that should receive the message
   * @param pSubject Subject line of the email message
   * @param pBody The message content
   * @param pExtraHeaders The list of extra headers that should be used
   * in the email (e.g. <code>"X-URL: http://foo.bar.com/"</code>)
   * @param pTrackingData NV pair data to associate with the out-bound email.
   * @exception EmailException if there is any problem while sending mail
   */
  public void sendEmailMessage(String pFrom,
                               String pRecipient,
                               String pSubject,
                               String pBody,
                               String [] pExtraHeaders,
			       Map pTrackingData)
    throws EmailException
  {
    EmailEvent emailEvent = new EmailEvent();
    emailEvent.setCharSet(getCharSet());
    emailEvent.setFrom(pFrom);
    emailEvent.setRecipient(pRecipient);
    emailEvent.setSubject(pSubject);
    emailEvent.setBody(getBodyContent(pBody));
    emailEvent.setExtraHeaders(pExtraHeaders);
    emailEvent.setTrackingData(pTrackingData);

    // send the event
    sendEmailEvent(emailEvent);
  }

  /**
   * Sends a message via SMTP with the given parameters. The message
   * content is prepended by any value for the default body property.
   * @param pFrom Whom the mail is coming from
   * @param pRecipients The list of recipients that should receive email
   * @param pSubject Subject line of the email message
   * @param pBody The message content
   * @param pExtraHeaders The list of extra headers that should be used
   * in the email (e.g. <code>"X-URL: http://foo.bar.com/"</code>)
   * @param pTrackingData NV pair data to associate with the out-bound email.
   * @exception EmailException if there is any problem while sending mail
   */
  public void sendEmailMessage(String pFrom,
                               String [] pRecipients,
                               String pSubject,
                               String pBody,
                               String [] pExtraHeaders,
			       Map pTrackingData)
    throws EmailException
  {
    EmailEvent emailEvent = new EmailEvent();
    emailEvent.setCharSet(getCharSet());
    emailEvent.setFrom(pFrom);
    emailEvent.setRecipientList(pRecipients);
    emailEvent.setSubject(pSubject);
    emailEvent.setBody(getBodyContent(pBody));
    emailEvent.setExtraHeaders(pExtraHeaders);
    emailEvent.setTrackingData(pTrackingData);

    // send the event
    sendEmailEvent(emailEvent);
  }

  //-------------------------------------
  // EmailMessageSender interface
  //-------------------------------------

  //-------------------------------------
  /**
   * Opens a connection to the mail server using a separate thread.
   *
   * @return Transport object representing the open connection
   * @exception EmailException if the connection could not be
   * opened
   **/
  public Transport openConnection() throws EmailException {

    // if we are in dummy mode don't try to connect to the mail server
    if (mDummyMode)
      return null;

    Thread currentThread = Thread.currentThread();
    ConnectionThread connectionRetriever = new ConnectionThread();
    connectionRetriever.setParentThread(currentThread);
    connectionRetriever.setParentSender(this);
    connectionRetriever.start();

    try{
      Thread.sleep(getWaitForConnectionMillis());
    }
    catch(InterruptedException ie){
      MessagingException exc =
        connectionRetriever.getMessagingException();

      if(exc != null){
        if(isLoggingError()){
          logError(exc.toString());
        }
        throw new EmailException(exc.toString(), exc);
      }

      Transport trans = connectionRetriever.getTrans();
      if(trans.isConnected())
        return trans;

      if(isLoggingDebug())
        logDebug("Thread was interrupted, but no exception or error was set");
    }
    finally{
      // stop the thread
      if(!connectionRetriever.isDone())
        connectionRetriever.stop();
    }

    if(isLoggingError()){
      logError(
        ResourceUtils.getMsgResource("emailSMTPEmailConnectionFailed",
                                     MY_RESOURCE_NAME, sResourceBundle));
    }

    throw
      new EmailException(
        ResourceUtils.getMsgResource("emailSMTPEmailConnectionFailed",
                                     MY_RESOURCE_NAME, sResourceBundle));
  }

  //-------------------------------------
  /**
   * Sends an email message.  The parameters of the email are
   * specified within the Message object.  The Transport object
   * identifies the connection over which the message will be
   * sent; the connection must have already been opened.
   *
   * @exception EmailException if the connection is not open,
   * or if the message could not be sent
   **/
  public void sendEmailMessage(Message pMessage, Transport pTransport)
    throws EmailException
  {
    long startTime = getRequestStartTime();
    // Commenting out the check for connection below since it creates
    // new thread per call which SLOWS down VM big time
    // if transport is not connected we hopefully get an exception.
    //
    // make sure the Transport is connected
    //if (!pTransport.isConnected()) {
    //Object[] args = { pTransport.toString() };
    //throw new EmailException
    //(ResourceUtils.getMsgResource("emailSMTPEmailSenderConnectionNotOpen",
    //MY_RESOURCE_NAME, sResourceBundle, args));
    //}
    try {
      // set the time just before we send the message off
      pMessage.setSentDate(new Date());

      // IMPORTANT: we must call saveChanges on the message, since
      // Transport.sendMessage doesn't do this (though Transport.send
      // does) - without this, the headers won't be properly set up
      pMessage.saveChanges();

      if (mTestMode || mDummyMode) {
        printMessage(pMessage);
      } else {
        // send the message
        pTransport.sendMessage(pMessage, pMessage.getAllRecipients());
      }
    }
    catch (MessagingException sfe) {
      Exception ex = sfe;
      do {
          // Check for a list of tags to search to determine if failure was caused
          // by an invalid sender address. We have to parse the exception to find
          // this out.
          if (mInvalidSenderExceptionTags != null) {
            // unused int start, end;
            String badAddress;

            String exceptionString = ex.toString();

            if (isLoggingDebug())
              logDebug("Searching for invalid sender tags in excption: " +exceptionString);

            for (int i = 0; i < mInvalidSenderExceptionTags.length; i++) {

              if (isLoggingDebug())
                logDebug("Looking for invalidSender tag: " 
                         +mInvalidSenderExceptionTags[i]);

              // Look anywhere in the exception since they can be nested.
              MessageFormat mf = new MessageFormat(mInvalidSenderExceptionTags[i]);

              try {

                Object[] objs = mf.parse(exceptionString);
                                
                badAddress = (String) objs[1];
                if (isLoggingDebug()) {
                  for (int x = 0; x < objs.length; x++) 
                    logDebug("Parsed arg obj[" +x +"] = " +objs[x]);
                  
                  logDebug("Determined bad sender address: " +badAddress);
                }
                throw new EmailInvalidSenderException(sfe.toString(), sfe, badAddress);
              } catch (java.text.ParseException pe) {
                if (isLoggingDebug())
                  logDebug("Tag not found in exception");
              }
            }
          }

        if (ex instanceof SendFailedException) {
          SendFailedException sfex = (SendFailedException)ex;

          Address[] invalid = sfex.getInvalidAddresses();
          if (invalid != null) {
            if (isLoggingError()) logError("    ** Invalid Addresses");
            if (invalid != null) {
              for (int i = 0; i < invalid.length; i++)
                if (isLoggingError()) logError("         " + invalid[i]);
            }
          }
          Address[] validUnsent = sfex.getValidUnsentAddresses();
          if (validUnsent != null) {
            if (isLoggingError()) logError("    ** ValidUnsent Addresses");
            if (validUnsent != null) {
              for (int i = 0; i < validUnsent.length; i++)
                if (isLoggingError()) logError("         "+validUnsent[i]);
            }
          }
          Address[] validSent = sfex.getValidSentAddresses();
          if (validSent != null) {
            if (isLoggingError()) logError("    ** ValidSent Addresses");
            if (validSent != null) {
              for (int i = 0; i < validSent.length; i++)
                if (isLoggingError()) logError("         "+validSent[i]);
            }
          }
        }
      } while ((ex instanceof MessagingException) &&
               (ex = ((MessagingException)ex).getNextException()) != null);

      if (isLoggingError())
        logError(sfe.toString());
      throw new EmailException(sfe.toString(), sfe);
    } finally {
      notifyHandledRequest(startTime);
    }
  }

  //-------------------------------------
  /**
   * Sends an email message.  The parameters of the email are
   * specified within the Message object.  A new connection to
   * the mail server will be opened, the message sent, and the
   * connection closed.
   *
   * @exception EmailException if the message could not be sent
   **/
  public void sendEmailMessage(Message pMessage)
    throws EmailException
  {
    // Do not establish connection with transport if in dummy mode
    if (mDummyMode)
      sendEmailMessage(pMessage, null);
    else {
      Transport transport = openConnection();
      sendEmailMessage(pMessage, transport);
      closeConnection(transport);
    }
  }

  //-------------------------------------
  /**
   * Closes the connection identified by the Transport object.
   *
   * @exception EmailException if the connection could not be
   * closed
   **/
  public void closeConnection(Transport pTransport) throws EmailException {
    try {
      if (pTransport != null && pTransport.isConnected())
        pTransport.close();
    }
    catch (MessagingException exc) {
      // if there is an error closing the connection, it's supposed
      // to still get closed, so do nothing here
    }
  }

  //-------------------------------------
  // EmailListener interface
  //-------------------------------------

  /**
   * Takes the given event and sends if off as an email message
   *
   * @exception EmailException if the email could not be sent
   */
  public void sendEmailEvent(EmailEvent pEvent)
    throws EmailException
  {
    // pr31360 -- use defaultFrom if event lacks from field
    // Only sets the event's from property if it is null or empty
    // and the default from is neither null nor empty.
    if (pEvent != null){
      String from = pEvent.getFrom();
      if (from == null || from.equals("")){
          pEvent.setFrom(mDefaultFrom);
      }
      /*
      String cc = pEvent.getCc();
      if (cc == null || cc.equals("")){
          pEvent.setCc(mDefaultCc);
      }

      String bcc = pEvent.getBcc();
      if (bcc == null || bcc.equals("")){
          pEvent.setBcc(mDefaultBcc);
      }
      */
    }
    sendEmail(pEvent);
  }

  //-------------------------------------
  // Internals
  //-------------------------------------

  /**
   * Sends a message via SMTP with the given parameters
   * @param pEvent the event which should be sent as an email message
   * @exception EmailException if there is any problem while sending mail
   */
  void sendEmail(EmailEvent pEvent) throws EmailException {
    Message msg = null;
    try {
      msg = pEvent.getMessage();
    }
    catch (EmailException exc) {
      if (isLoggingError())
        logError(exc.toString());
      throw exc;
    }

    // call the EmailMessageSender method to send the message
    sendEmailMessage(msg);
  }

  //-------------------------------------
  // Internal class
  // Used to create a connection to the SMTP server so if something
  // goes wrong, we won't hang

  private class ConnectionThread extends Thread{

    Transport mTrans = null;
    Thread mParentThread  = null;
    boolean mDone = false;
    MessagingException mMessagingException = null;
    SMTPEmailSender mParentSender;

    /**
     * Defines whether the connection has succeeded
     **/
    public boolean isDone()
    {
      return mDone;
    }

    /**
     * Sets the transport for this thread
     **/
    public void setTrans(Transport pTrans){
      mTrans = pTrans;
    }

    /**
     * Gets the transport for this thread
     **/
    public Transport getTrans(){
      return mTrans;
    }

    /**
     * Sets the parent thread for this thread
     **/
    public void setParentThread(Thread pParentThread){
      mParentThread = pParentThread;
    }

    /**
     * Gets the parent thread for this thread
     **/
    public Thread getParentThread(){
      return mParentThread;
    }

    /**
     * Gets the SMTP email sender for this thread
     **/
    public SMTPEmailSender getParentSender(){
      return mParentSender;
    }

    /**
     * Sets the SMTP email sender for this thread
     **/
    public void setParentSender(SMTPEmailSender pParentSender){
      mParentSender = pParentSender;
    }

    /**
     * Gets any MessagingException that occurs
     **/
    public  MessagingException getMessagingException(){
      return mMessagingException;
    }

    /**
     * Sets any MessagingException that occurs
     **/
    public void setMessagingException(MessagingException pMessagingException){
      mMessagingException = pMessagingException;
    }

    public void run(){
      try{
        //obtain a Session object
        Session session =
          Session.getDefaultInstance(System.getProperties(), null);
        session.setDebug(isLoggingDebug());

        // connect through the Transport object
        Transport trans =
          session.getTransport(SMTPEmailSender.PROTOCOL_NAME);
        if(trans != null){
          for(int i=0;i<getParentSender().getRetryConnectionCount();i++){
            if(isLoggingDebug()){
              logDebug("Attempting to open a connection...");
            }

            try{
              trans.connect(getParentSender().getEmailHandlerHostName(),
                            getParentSender().getEmailHandlerPort(),
                            getParentSender().getUsername(),
                            getParentSender().getPassword());
            }
            catch(MessagingException exc){
              setMessagingException(exc);
            }

            if(trans.isConnected()){
              setMessagingException(null);
              break;
            }
            if(isLoggingDebug())
              logDebug("Connection failed on try " + (i+1) + " of " +
                       getParentSender().getRetryConnectionCount() + ".");
          }
        }
        setTrans(trans);
      }
      catch(MessagingException exc){
        setMessagingException(exc);
      }

      // Interrupt the parent thread so it knows that we either have
      // a connection, or something went wrong
      mDone = true;
      getParentThread().interrupt();
    }
  }

  public void printMessage(Message pMessage) {
    System.err.println("**SMTP.sendEmailMessage: Test mode " +pMessage);
    try {
      System.out.println("Headers");
      java.util.Enumeration ee = ((MimeMessage)pMessage).getAllHeaders();
      while (ee.hasMoreElements()) {
        Header hh = (Header) ee.nextElement();
        System.out.println(hh.getName() + " = " + hh.getValue());
      }
    }
    catch (Exception exc) {
      System.out.println("Error: caught exception " + exc);
    }
    try {
      Flags flags =  ((MimeMessage)pMessage).getFlags();
      Flags.Flag[] sf = flags.getSystemFlags();
      System.out.println("System flags");
      for (int ii = 0; ii < sf.length; ii++)
        System.out.println(sf[ii]);

      String[] s = flags.getUserFlags();
      System.out.println("User flags");
      for (int ii = 0; ii < s.length; ii++)
        System.out.println(s[ii]);
    }
    catch (Exception exc) {
      System.out.println("Error: caught exception " + exc);
    }

    printParts(pMessage);
  }

  public void printParts(Part p) {
    try {
      Object obj = p.getContent();
      if (obj instanceof String) {
        System.out.println("This is a String");
        System.out.println((String)obj);
      }
      else if (obj instanceof Multipart) {
        System.out.println("This is a Multipart");
        Multipart mp = (Multipart)obj;
        int count = mp.getCount();
        for (int i = 0; i < count; i++) {
          printParts(mp.getBodyPart(i));
        }
      }
      else if (obj instanceof InputStream) {
        System.out.println("This is just an input stream");
        InputStream is = (InputStream)obj;
        int c;
        while ((c = is.read()) != -1)
          System.out.write(c);
      }
    } catch (Exception exc) { }
  }

  public static void main( String arguments[] )
  {
    SMTPEmailSender sender = new SMTPEmailSender();

    sender.setEmailHandlerHostName( "mailsvr.atg.com" );
    //sender.setTestMode( true );
    String from = "please_enter_a_valid_from_address@atg.com";
    String to = "please_enter_a_valid_to_address@atg.com";
    if (arguments[0] != null) from = arguments[0];
    if (arguments[1] != null) to = arguments[1];
    System.out.println( "Attempting to sent a message to " 
                        + to + " from " + from );
    try {
      /*
      // send email with attachment
      Message message =
        MimeMessageUtils.createMessage(
          from,
          "test atg.service.email.SMTPEmailSender" // subject
          );

      MimeMessageUtils.setRecipient(message,
                                    Message.RecipientType.TO,
                                    to);

      MimeMessageUtils.setContent(
        message,
        "this is a test2",
        "text/plain",
        new java.io.File("/tmp/does_not_exist"), // attachment
        false // don't inline attachment
        );

      sender.sendEmailMessage(message);
      */

      // send plain text email
      sender.sendEmailMessage(
        from, // from
        to, // to
        "test atg.service.email.SMTPEmailSender", // subject
        "this is a test" // body
        );
      System.out.println("Message sent.");
    }
    catch ( EmailException exc ) {
      System.err.println( exc.getMessage() );
    }
    catch ( Exception exc ) {
      exc.printStackTrace();
    }
  }

}
