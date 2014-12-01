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

import java.text.MessageFormat;
import java.util.Map;
import javax.mail.internet.MimeMessage;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;

import atg.core.util.ResourceUtils;

/**
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/service/email/EmailEvent.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class EmailEvent {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = 
  "$Id: //product/DAS/version/10.0.3/Java/atg/service/email/EmailEvent.java#2 $$Change: 651448 $";

  /** Resource bundle **/
  static final String MY_RESOURCE_NAME = "atg.service.ServiceResources";

  private static java.util.ResourceBundle sResourceBundle = 
  java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  /** Separator used in extra headers **/
  static char HEADER_NAME_VALUE_SEPARATOR = ':';

  //-------------------------------------
  // Constants

  //-------------------------------------
  // Properties
  String mCharSet;
  String mFrom;
  String mCc;
  String mBcc;
  String [] mRecipientList;
  String mRecipient;
  String mSubject;
  String [] mExtraHeaders;
  Map mTrackingData;
  String mBody;

  // a Message containing all the email parameters
  Message mMessage = null;

  //-------------------------------------
  // Email Properties
  //-------------------------------------
  
  /**
   * Returns the list of recipients that should receive email
   */
  public String [] getRecipientList() {
    return mRecipientList;
  }

  /**
   * Sets the list of recipients that should receive email
   */
  public void setRecipientList(String [] pRecipients) {
    mRecipientList = pRecipients;
  }

  /**
   * Returns the recipient that should receive email
   */
  public String getRecipient() {
    return mRecipient;
  }

  /**
   * Sets the recipient that should receive email
   */
  public void setRecipient(String pRecipient) {
    mRecipient = pRecipient;
  }

  /**
   * Returns the character set.
   */
  public String getCharSet() {
    return mCharSet;
  }

  /**
   * Sets the character set.
   */
  public void setCharSet(String pCharSet) {
    mCharSet = pCharSet;
  }

  /**
   * Returns the sender (From:) field.
   */
  public String getFrom() {
    return mFrom;
  }

  /**
   * Sets the sender (Cc:) field.
   */
  public void setFrom(String pFrom) {
    mFrom = pFrom;
  }


  /**
   * Returns the sender (Cc:) field.
   */
  public String getCc() {
    return mCc;
  }

  /**
   * Sets the sender (Cc:) field.
   */
  public void setCc(String pCc) {
    mCc = pCc;
  }

  /**
   * Returns the sender (Bcc:) field.
   */
  public String getBcc() {
    return mBcc;
  }

  /**
   * Sets the sender (Bcc:) field.
   */
  public void setBcc(String pBcc) {
    mBcc = pBcc;
  }

  /**
   * Returns the subject field.
   */
  public String getSubject() {
    return mSubject;
  }

  /**
   * Sets the subject field.
   */
  public void setSubject(String pSubject) {
    mSubject = pSubject;
  }

  /**
   * Returns the list of extra headers that should be used in the email
   * (e.g. <code>"X-URL: http://foo.bar.com/"</code>) 
   */
  public String [] getExtraHeaders() {
    return mExtraHeaders;
  }

  /**
   * Sets the list of extra headers that should be used in the email
   * (e.g. <code>"X-URL: http://foo.bar.com/"</code>)
   */
  public void setExtraHeaders(String [] pExtraHeaders) {
    mExtraHeaders = pExtraHeaders;
  }  

  /**
   * Gets the NV pair data to associate with the out-bound email.
   */
  public Map getTrackingData() {
    return mTrackingData;
  }

  /**
   * Sets the NV pair data to associate with the out-bound email.
   */
  public void setTrackingData(Map pTrackingData) {
    mTrackingData = pTrackingData;
  }  

  /**
   * Returns the message body.
   */
  public String getBody() {
    return mBody;
  }

  /**
   * Sets the message body.
   */
  public void setBody(String pBody) {
    mBody = pBody;
  }

  /**
   * Returns the Message to be sent.  If this property has not been
   * set explicitly, a Message object is created using the rest of
   * the email parameters.
   * 
   * @see javax.mail.Message
   * @exception EmailException if an error occurs while creating 
   * the Message
   */
  public Message getMessage() throws EmailException {
    return getMessage(null, null);
  }

  /**
   * Returns the Message to be sent.  If this property has not been
   * set explicitly, a Message object is created using the rest of
   * the email parameters.
   *
   * @see javax.mail.Message
   * @exception EmailException if an error occurs while creating 
   * the Message
   */
  public Message getMessage(Session pSession) throws EmailException {
    return getMessage(pSession, null);
  }

  /**
   * Returns the Message to be sent.  If this property has not been
   * set explicitly, a Message object is created using the rest of
   * the email parameters.
   * 
   * @see javax.mail.Message
   * @exception EmailException if an error occurs while creating 
   * the Message
   */
  public Message getMessage(Session pSession, EmailTrackingTools pTools) throws EmailException {
    if (mMessage == null){
      mMessage = createMessage(pSession, pTools);
    }
    return mMessage;
  }

  /**
   * Sets the Message to be sent.  If this method is called with a 
   * non-null Message argument, any other event properties (from, 
   * subject, recipients, body, etc.) will be ignored, and the 
   * parameters/content contained within the Message object will be 
   * used when sending the email.
   * 
   * <p>This property should be used if the other EmailEvent 
   * properties are not sufficient to describe the message headers
   * and/or content: for example, if you wish to send a message with
   * an attachment, or a multipart message.
   * 
   * @see javax.mail.Message
   */
  public void setMessage(Message pMessage) {
    mMessage = pMessage;
  }

  public EmailEvent() {
  }

  public EmailEvent(String pFrom,
                    String pRecipient,
                    String pSubject,
                    String pBody) {
    mFrom = pFrom;
    mRecipient = pRecipient;
    mSubject = pSubject;
    mBody = pBody;    
  }

  public EmailEvent(String pFrom,
                    String [] pRecipients,
                    String pSubject,
                    String pBody) {
    mFrom = pFrom;
    mRecipientList = pRecipients;
    mSubject = pSubject;
    mBody = pBody;
  }

  public EmailEvent(String pFrom,
                    String pRecipient,
                    String pSubject,
                    String pBody,
                    String pExtraHeader) {                    
    mFrom = pFrom;
    mRecipient = pRecipient;
    mSubject = pSubject;
    mBody = pBody;
    mExtraHeaders = new String[1];
    mExtraHeaders[0] = pExtraHeader;
  }

  public EmailEvent(String pFrom,
                    String pRecipient,
                    String pSubject,
                    String pBody,
                    String [] pExtraHeaders) {
    mFrom = pFrom;
    mRecipient = pRecipient;
    mSubject = pSubject;
    mBody = pBody;
    mExtraHeaders = pExtraHeaders;
  }

  public EmailEvent(String pFrom,
                    String [] pRecipients,
                    String pSubject,
                    String pBody,
                    String pExtraHeader) {                    
    mFrom = pFrom;
    mRecipientList = pRecipients;
    mSubject = pSubject;
    mBody = pBody;
    mExtraHeaders = new String[1];
    mExtraHeaders[0] = pExtraHeader;
  }

  public EmailEvent(String pFrom,
                    String [] pRecipients,
                    String pSubject,
                    String pBody,
                    String [] pExtraHeaders) {                    
    mFrom = pFrom;
    mRecipientList = pRecipients;
    mSubject = pSubject;
    mBody = pBody;
    mExtraHeaders = pExtraHeaders;
  }

  public EmailEvent(String pFrom,
                    String pRecipient,
                    String pSubject,
                    String pBody,
                    String [] pExtraHeaders,
		    Map pTrackingData) {
    mFrom = pFrom;
    mRecipient = pRecipient;
    mSubject = pSubject;
    mBody = pBody;
    mExtraHeaders = pExtraHeaders;
    mTrackingData = pTrackingData;
  }

  public EmailEvent(String pFrom,
                    String [] pRecipients,
                    String pSubject,
                    String pBody,
                    String [] pExtraHeaders,
		    Map pTrackingData) {                    
    mFrom = pFrom;
    mRecipientList = pRecipients;
    mSubject = pSubject;
    mBody = pBody;
    mExtraHeaders = pExtraHeaders;
    mTrackingData = pTrackingData;
  }

  public EmailEvent(Message pMessage) {
    mMessage = pMessage;
  }

  //-------------------------------------
  /**
   * Creates a new Message from the given EmailEvent's parameters 
   * (from, subject, recipients, body, etc.).
   *
   * @exception EmailException if the Message could not be created 
   * from the given event parameters
   **/
  protected Message createMessage(Session pSession, EmailTrackingTools pTools) throws EmailException {
    try {
      Session session = pSession;
      if(session == null)
	session = MimeMessageUtils.createSession();
      
      // Store the tracking data in the session.
      EmailTrackingTools tools = pTools;
      if(tools == null)
	tools = EmailTrackingTools.getDefaultInstance();
      tools.storeTrackingData(session, getTrackingData());

      Message msg = MimeMessageUtils.createMessage(session);
	    
      // set the from field
      if (mFrom != null)
	MimeMessageUtils.setFrom(msg, mFrom);
      
      if (mCc != null)
	MimeMessageUtils.setCc(msg, mCc);
      
      if (mBcc != null)
	MimeMessageUtils.setBcc(msg, mBcc);
      
      // set the subject field
      if (mSubject != null) {
          if (msg instanceof MimeMessage)
              ((MimeMessage)msg).setSubject(mSubject, getCharSet());
          else
              msg.setSubject(mSubject);
      }

      // set the to field
      if (mRecipient != null)
	MimeMessageUtils.setRecipient
	  (msg, Message.RecipientType.TO, mRecipient);
      else if (mRecipientList != null)
	MimeMessageUtils.setRecipients
	  (msg, Message.RecipientType.TO, mRecipientList);
      else
	throw new EmailException
	  (ResourceUtils.getMsgResource("emailEventNoRecipient",
					MY_RESOURCE_NAME, sResourceBundle));
      
      // set any extra headers
      if (mExtraHeaders != null) {
	for (int i = 0; i < mExtraHeaders.length; i++) {
	  int ix = mExtraHeaders[i].indexOf(HEADER_NAME_VALUE_SEPARATOR);
	  if (ix == -1) {
	    Object[] args = { mExtraHeaders[i] };
	    throw new EmailException
	      (ResourceUtils.getMsgResource
	       ("emailEventNoHeaderNameValueSeparator", 
		MY_RESOURCE_NAME, sResourceBundle, args));
	  }
	  if (mExtraHeaders[i].length() == (ix + 1)) {
	    Object[] args = { mExtraHeaders[i] };
	    throw new EmailException
	      (ResourceUtils.getMsgResource
	       ("emailEventNoHeaderValue", MY_RESOURCE_NAME, 
		sResourceBundle, args));
	  }
	
	  String headerName = mExtraHeaders[i].substring(0, ix);
	  String headerValue = mExtraHeaders[i].substring(ix + 1);
	  if (headerValue.startsWith(" "))
	    headerValue = headerValue.substring(1);
	
	  msg.addHeader(headerName, headerValue);
	}
      }

      ((MimeMessage)msg).setText(mBody, getCharSet());

      //String ctype = ((MimeMessage)msg).getHeader("Content-Type", " ");

      // System.out.println( "\n\n\tXXXXXXX charSet is set to: " +ctype );
      
      return msg;
    }
    catch (MessagingException exc) {
      throw new EmailException(exc.toString(), exc);
    }
  }

}
