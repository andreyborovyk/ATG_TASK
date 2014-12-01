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

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 * A set of static utility methods for creating and filling in
 * <code>javax.mail.Message</code> objects.  In particular, the 
 * <code>createMessage</code> methods of this class create 
 * <code>javax.mail.internet.MimeMessage</code> objects; other 
 * methods can be used to set the various fields of the message,
 * specify the message content, and so on.
 *
 * <p>Note that the <code>setContent</code> methods in this 
 * class typically call through to 
 * <code>javax.mail.Part.setContent(Object obj, String type)</code> 
 * to set the content of the message or each of its parts.  The
 * type of the content can be any MIME type; however, for the 
 * message to be sent successfully, a 
 * <code>javax.activation.DataContentHandler</code> class for 
 * the specified type should be available to the JavaMail 
 * implementation.  For example, to do 
 * <code>setContent(foobar, "application/x-foobar")</code>, a 
 * <code>DataContentHandler</code> for "application/x-foobar"
 * should be installed.  The Object passed in to the 
 * <code>setContent</code> method should be of type expected by
 * the <code>DataContentHandler</code> associated with the 
 * content type.
 * 
 * <p>If you want to create and send messages with a content 
 * type unknown to the system, you will first need to write a
 * <code>DataContentHandler</code> implementation specific to 
 * your content type.  Once you have your 
 * <code>FoobarDataContentHandler</code>, you will need to 
 * make it available to the Java Activation Framework.  One way
 * to do that is by providing a <code>mailcap</code> file with
 * the new content handler entry in it (see JAF documentation,
 * particularly <code>javax.activation.MailcapCommandMap</code>
 * class documentation, for more information).  Another option
 * is to provide the same information to the
 * <code>DataContentHandlerRegistry</code> component, by 
 * setting its <code>dataContentHandlerMap</code> property. 
 * 
 * <p>Following is the list of MIME types currently supported 
 * by the system, assuming a 
 * <code>DataContentHandlerRegistry</code> component with the
 * standard configuration is used.  For each content type, we 
 * also indicate the type of Object that must be passed in to 
 * <code>setContent</code> for the associated 
 * <code>DataContentHandler</code> to work correctly:
 * <ul>
 * <li>
 * text/plain (<code>String</code>)
 * </li>
 * <li>
 * text/html (<code>String</code>) 
 * </li>
 * </ul> 
 *
 * @see javax.mail.Message
 * @see javax.mail.internet.MimeMessage
 * @see javax.activation.DataContentHandler
 * @see javax.activation.MailcapCommandMap
 * @see DataContentHandlerRegistry
 * @author Natalya Cohen
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/service/email/MimeMessageUtils.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class MimeMessageUtils {
  //-------------------------------------
  // Constants
  //-------------------------------------

  /** Class version string **/
  public static String CLASS_VERSION = 
  "$Id: //product/DAS/version/10.0.3/Java/atg/service/email/MimeMessageUtils.java#2 $$Change: 651448 $";

  /** Resource bundle **/
  private static java.util.ResourceBundle sResourceBundle = 
  java.util.ResourceBundle.getBundle("atg.service.ServiceResources", atg.service.dynamo.LangLicense.getLicensedDefault());

  /** Subtype "mixed" of "multipart" content type **/
  static String MULTIPART_SUBTYPE_MIXED = "mixed";

  //-------------------------------------
  // Methods: message creation
  //-------------------------------------

  //-------------------------------------
  /**
   * Creates a new mail session.
   * @see javax.mail.Session#getInstance(java.util.Properties,javax.mail.Authenticator);
   **/
  public static Session createSession(){
    return Session.getInstance((Properties)System.getProperties().clone(), null);
  }
  
  //-------------------------------------
  /**
   * Gets the default mail session using without using
   * and Authenticator.
   * @see javax.mail.Session#getDefaultInstance(java.util.Properties,javax.mail.Authenticator);
   * @see javax.mail.Authenticator
   **/
  public static Session getDefaultSession(){
    return Session.getDefaultInstance(System.getProperties(), null);
  }
  
  //-------------------------------------
  /**
   * Creates a new, empty Message object.
   **/
  public static Message createMessage() {
    return createMessage(null);
  }

  //-------------------------------------
  /**
   * Creates a new, empty Message object.
   **/
  public static Message createMessage(Session pSession) {
    Session session = pSession;
    if(session == null)
      session = createSession();
    return new MimeMessage(session);
  }

  //-------------------------------------
  /**
   * Creates a new Message object with the given From and Subject 
   * fields.
   * 
   * @exception MessagingException if any of the fields could not 
   * be set
   **/
  public static Message createMessage(String pFrom, 
				      String pSubject) 
       throws MessagingException 
  {
    return createMessage(pFrom, pSubject, (Session)null);
  }

  //-------------------------------------
  /**
   * Creates a new Message object with the given From and Subject 
   * fields.
   * 
   * @exception MessagingException if any of the fields could not 
   * be set
   **/
  public static Message createMessage(String pFrom, 
				      String pSubject,
				      Session pSession) 
       throws MessagingException 
  {
    Message msg = createMessage(pSession);
    setFrom(msg, pFrom);
    msg.setSubject(pSubject);
    return msg;
  }

  //-------------------------------------
  /**
   * Creates a new Message object with the given From ReplyTo 
   * and Subject fields.
   * 
   * @exception MessagingException if any of the fields could not 
   * be set
   **/
  public static Message createMessage(String pFrom, 
				      String pSubject,
                                      String pReplyTo) 
       throws MessagingException 
  {
    return createMessage(pFrom, pSubject, pReplyTo, (Session)null);
  }

  //-------------------------------------
  /**
   * Creates a new Message object with the given From ReplyTo 
   * and Subject fields.
   * 
   * @exception MessagingException if any of the fields could not 
   * be set
   **/
  public static Message createMessage(String pFrom, 
				      String pSubject,
                                      String pReplyTo,
				      Session pSession) 
       throws MessagingException 
  {
    Message msg = createMessage(pFrom, pSubject, pSession);
    setReplyTo(msg, pReplyTo);
    return msg;
  }

  //-------------------------------------
  /**
   * Creates a new Message object with the given From, Subject, 
   * and To fields, and a body given by <code>pTextContent</code>.
   * This is a convenience method that calls 
   * <code>MimeMessage.setText(pTextContent)</code> to set the 
   * message content; the resulting MIME type of the content is 
   * "text/plain."
   * 
   * @see javax.mail.internet.MimeMessage#setText(String)
   * @exception MessagingException if any of the fields or the 
   * message content could not be set
   **/
  public static Message createMessage(String pFrom, 
				      String pSubject,
				      String pTo, 
				      String pTextContent) 
       throws MessagingException 
  {
    return createMessage(pFrom, pSubject, pTo, pTextContent, (Session)null);
  }

  //-------------------------------------
  /**
   * Creates a new Message object with the given From, Subject, 
   * and To fields, and a body given by <code>pTextContent</code>.
   * This is a convenience method that calls 
   * <code>MimeMessage.setText(pTextContent)</code> to set the 
   * message content; the resulting MIME type of the content is 
   * "text/plain."
   * 
   * @see javax.mail.internet.MimeMessage#setText(String)
   * @exception MessagingException if any of the fields or the 
   * message content could not be set
   **/
  public static Message createMessage(String pFrom, 
				      String pSubject,
				      String pTo, 
				      String pTextContent,
				      Session pSession) 
       throws MessagingException 
  {
    Message msg = createMessage(pSession);
    setFrom(msg, pFrom);
    msg.setSubject(pSubject);
    setRecipient(msg, Message.RecipientType.TO, pTo);
    msg.setText(pTextContent);
    return msg;
  }

  //-------------------------------------
  /**
   * Creates a new Message object with the given From, Subject, 
   * and To fields, and the given content.  This method calls
   * <code>MimeMessage.setContent(pContent, pContentType)</code>
   * to set the message content.
   * 
   * @see javax.mail.internet.MimeMessage#setContent(Object, String)
   * @exception MessagingException if any of the fields or the 
   * message content could not be set
   **/
  public static Message createMessage(String pFrom, 
				      String pSubject, 
				      String pTo, 
				      Object pContent, 
				      String pContentType) 
       throws MessagingException 
  {
    return createMessage(pFrom, pSubject, pTo, pContent, pContentType, (Session)null);
  }
  
  //-------------------------------------
  /**
   * Creates a new Message object with the given From, Subject, 
   * and To fields, and the given content.  This method calls
   * <code>MimeMessage.setContent(pContent, pContentType)</code>
   * to set the message content.
   * 
   * @see javax.mail.internet.MimeMessage#setContent(Object, String)
   * @exception MessagingException if any of the fields or the 
   * message content could not be set
   **/
  public static Message createMessage(String pFrom, 
				      String pSubject, 
				      String pTo, 
				      Object pContent, 
				      String pContentType,
				      Session pSession) 
       throws MessagingException 
  {
    Message msg = createMessage(pSession);
    setFrom(msg, pFrom);
    msg.setSubject(pSubject);
    setRecipient(msg, Message.RecipientType.TO, pTo);
    msg.setContent(pContent, pContentType);
    return msg;
  }
  
  //-------------------------------------
  // Methods: setting message fields
  //-------------------------------------

  //-------------------------------------
  /**
   * Sets the ReplyTo field of the message to the InternetAddress 
   * obtained from the given string.
   *
   * @exception MessagingException if the field could not be set
   **/
  public static void setReplyTo(Message pMessage, String pReplyTo) 
       throws MessagingException
  {
    Address[] relyTo = { new InternetAddress(pReplyTo) };
    pMessage.setReplyTo(relyTo);
  }

  /**
   * Sets the From field of the message to the InternetAddress 
   * obtained from the given string.
   *
   * @exception MessagingException if the field could not be set
   **/
  public static void setFrom(Message pMessage, String pFrom) 
       throws MessagingException
  {
    pMessage.setFrom(new InternetAddress(pFrom));
  }

  /**
   * Adds the Cc field of the message to the InternetAddress 
   * obtained from the given string.
   *
   * @exception MessagingException if the field could not be set
   **/
  public static void setCc(Message pMessage, String pCc) 
       throws MessagingException
  {
    setRecipient(pMessage, Message.RecipientType.CC, pCc);
  }


  /**
   * Adds the Bcc field of the message to the InternetAddress 
   * obtained from the given string.
   *
   * @exception MessagingException if the field could not be set
   **/
  public static void setBcc(Message pMessage, String pBcc) 
       throws MessagingException
  {
    setRecipient(pMessage, Message.RecipientType.BCC, pBcc);
  }

  //-------------------------------------
  /**
   * Sets the recipient of the message (either To, Cc, or Bcc field) 
   * to the InternetAddress obtained from the given string.
   *
   * @param pRecipientType the recipient type, given by one of the 
   * values enumerated in Message.RecipientType; e.g., specifying 
   * Message.RecipientType.TO for the recipient type will result in 
   * the To field being set
   * @see javax.mail.Message.RecipientType
   * @exception MessagingException if the field could not be set
   **/
  public static void setRecipient(Message pMessage, 
				  Message.RecipientType pRecipientType,
				  String pRecipient) 
       throws MessagingException
  {
    Address[] addresses = InternetAddress.parse(pRecipient);
    pMessage.setRecipients(pRecipientType, addresses);
  }

  //-------------------------------------
  /**
   * Sets the recipients of the message (either To, Cc, or Bcc 
   * field) to the InternetAddress array obtained from the given 
   * string array.
   *
   * @param pRecipientType the recipient type, given by one of the 
   * values enumerated in Message.RecipientType; e.g., specifying 
   * Message.RecipientType.TO for the recipient type will result in 
   * the To field being set
   * @see javax.mail.Message.RecipientType
   * @exception MessagingException if the field could not be set
   **/
  public static void setRecipients(Message pMessage, 
				   Message.RecipientType pRecipientType,
				   String[] pRecipients) 
       throws MessagingException
  {
    Address[] addresses = new Address[pRecipients.length];
    for (int i = 0; i < addresses.length; i++) 
      addresses[i] = new InternetAddress(pRecipients[i]);
    pMessage.setRecipients(pRecipientType, addresses);
  }

  /**
   * Describe <code>setRecipients</code> method here.
   *
   * @param pMessage a <code>Message</code> value
   * @param pRecipientType a <code>Message.RecipientType</code> value
   * @param pRecipient a <code>String</code> value
   */
  public static void setRecipients(Message pMessage, 
				  Message.RecipientType pRecipientType,
				  String pRecipient) 
       throws MessagingException
  {
    setRecipient(pMessage, pRecipientType, pRecipient); 
  }
   
  //-------------------------------------
  /**
   * Adds a recipient to the message.
   *
   * @param pRecipientType the recipient type, given by one of the 
   * values enumerated in Message.RecipientType; e.g., specifying 
   * Message.RecipientType.TO for the recipient type will result in 
   * the To field being updated
   * @see javax.mail.Message.RecipientType
   * @exception MessagingException if the field could not be set
   **/
  public static void addRecipient(Message pMessage, 
				  Message.RecipientType pRecipientType,
				  String pRecipient) 
       throws MessagingException
  {
    pMessage.addRecipient(pRecipientType, new InternetAddress(pRecipient));
  }

  //-------------------------------------
  /**
   * Adds recipients to the message.
   *
   * @param pRecipientType the recipient type, given by one of the 
   * values enumerated in Message.RecipientType; e.g., specifying 
   * Message.RecipientType.TO for the recipient type will result in 
   * the To field being updated
   * @see javax.mail.Message.RecipientType
   * @exception MessagingException if the field could not be set
   **/
  public static void addRecipients(Message pMessage, 
				   Message.RecipientType pRecipientType,
				   String[] pRecipients) 
       throws MessagingException
  {
    Address[] addresses = new Address[pRecipients.length];
    for (int i = 0; i < addresses.length; i++) 
      addresses[i] = new InternetAddress(pRecipients[i]);
    pMessage.addRecipients(pRecipientType, addresses);
  }
    
  //-------------------------------------
  // Methods: setting message content
  //-------------------------------------

  //-------------------------------------
  /**
   * Sets the message content of a message with multiple content
   * parts.  For example, a message could have two content parts,
   * one of type "text/plain," another of type "text/html," and
   * the message recipient could decide which of the two content
   * parts to present to the user.
   * 
   * <p>Each element in <code>pContentParts</code> represents a 
   * message part, with the content and its MIME type contained
   * within the ContentPart object.
   * 
   * <p>This method sets the message content to a MimeMultipart
   * with a content type of "multipart/mixed;" for each 
   * <code>contentPart</code>,
   * <code>Part.setContent(contentPart.getContent(), 
   * contentPart.getContentType())</code> is called to set the 
   * part's content.
   * 
   * @see javax.mail.Part#setContent(Object, String)
   * @exception MessagingException if the message content could
   * not be set
   **/
  public static void setContent(Message pMessage, 
				ContentPart[] pContentParts)
       throws MessagingException
  {
    setContent(pMessage, pContentParts, (File[]) null, false,
	       MULTIPART_SUBTYPE_MIXED);
  }

  //-------------------------------------
  /**
   * Sets the message content of a message with multiple content
   * parts.  For example, a message could have two content parts,
   * one of type "text/plain," another of type "text/html," and
   * the message recipient could decide which of the two content
   * parts to present to the user.
   * 
   * <p>Each element in <code>pContentParts</code> represents a 
   * message part, with the content and its MIME type contained
   * within the ContentPart object.
   * 
   * <p>This method sets the message content to a MimeMultipart
   * with a content type of 
   * "multipart/<code>pMultipartSubtype</code>"; for each 
   * <code>contentPart</code>,
   * <code>Part.setContent(contentPart.getContent(), 
   * contentPart.getContentType())</code> is called to set the 
   * part's content.
   * 
   * @see javax.mail.Part#setContent(Object, String)
   * @exception MessagingException if the message content could
   * not be set
   **/
  public static void setContent(Message pMessage, 
				ContentPart[] pContentParts, 
				String pMultipartSubtype)
       throws MessagingException
  {
    setContent(pMessage, pContentParts, (File[]) null, false, 
	       pMultipartSubtype);
  }

  //-------------------------------------
  /**
   * Sets the message content of a message with a file attachment.
   * 
   * <p>This method sets the message content to a MimeMultipart
   * (with a content type of "multipart/mixed"); the first part
   * is given by <code>pContent</code>, with the MIME type of 
   * <code>pContentType</code>, and the second part contains the
   * file attachment.  
   * 
   * @param pAttachment the file to attach to the message
   * @param pInlineAttachment if true, the file will be inlined 
   * into the message, rather than attached
   * @exception MessagingException if the message content could
   * not be set
   **/
  public static void setContent(Message pMessage, 
				Object pContent, 
				String pContentType,
				File pAttachment, 
				boolean pInlineAttachment)
       throws MessagingException
  {
    ContentPart[] contentParts = new ContentPart[1];
    contentParts[0] = new ContentPart(pContent, pContentType);
    File[] attachments = new File[1];
    attachments[0] = pAttachment;
    
    setContent(pMessage, contentParts, 
	       attachments, pInlineAttachment,
	       MULTIPART_SUBTYPE_MIXED);
  }

  //-------------------------------------
  /**
   * Sets the message content of a message with multiple content
   * parts and a file attachment.  For example, a message could 
   * have two content parts, one of type "text/plain," another of 
   * type "text/html," and also a file attachment.
   * 
   * <p>Each element in <code>pContentParts</code> represents a 
   * message part, with the content and its MIME type contained
   * within the ContentPart object.
   * 
   * <p>This method sets the message content to a MimeMultipart
   * (with a content type of "multipart/mixed").  The first part 
   * of the multipart is itself a MimeMultipart, with a content 
   * type of "multipart/mixed;" for each <code>contentPart</code>,
   * <code>Part.setContent(contentPart.getContent(), 
   * contentPart.getContentType())</code> is called to set the 
   * part's content.  The second part of the outer multipart 
   * contains the file attachment.
   * 
   * @see javax.mail.Part#setContent(Object, String)
   * @param pAttachment the file to attach to the message
   * @param pInlineAttachment if true, the file will be inlined 
   * into the message, rather than attached
   * @exception MessagingException if the message content could
   * not be set
   **/
  public static void setContent(Message pMessage, 
				ContentPart[] pContentParts, 
				File pAttachment, 
				boolean pInlineAttachment)
       throws MessagingException
  {
    File[] attachments = new File[1];
    attachments[0] = pAttachment;
    
    setContent(pMessage, pContentParts,
	       attachments, pInlineAttachment,
	       MULTIPART_SUBTYPE_MIXED);
  }

  //-------------------------------------
  /**
   * Sets the message content of a message with multiple content
   * parts and a file attachment.  For example, a message could 
   * have two content parts, one of type "text/plain," another of 
   * type "text/html," and also a file attachment.
   * 
   * <p>Each element in <code>pContentParts</code> represents a 
   * message part, with the content and its MIME type contained
   * within the ContentPart object.
   * 
   * <p>This method sets the message content to a MimeMultipart
   * (with a content type of "multipart/mixed").  The first part 
   * of the multipart is itself a MimeMultipart, with a content 
   * type of "multipart/<code>pMultipartSubtype</code>;" for each 
   * <code>contentPart</code>,
   * <code>Part.setContent(contentPart.getContent(), 
   * contentPart.getContentType())</code> is called to set the 
   * part's content.  The second part of the outer multipart 
   * contains the file attachment.
   * 
   * @see javax.mail.Part#setContent(Object, String)
   * @param pAttachment the file to attach to the message
   * @param pInlineAttachment if true, the file will be inlined 
   * into the message, rather than attached
   * @exception MessagingException if the message content could
   * not be set
   **/
  public static void setContent(Message pMessage, 
				ContentPart[] pContentParts, 
				File pAttachment, 
				boolean pInlineAttachment,
				String pMultipartSubtype)
       throws MessagingException
  {
    File[] attachments = new File[1];
    attachments[0] = pAttachment;
    
    setContent(pMessage, pContentParts,
	       attachments, pInlineAttachment,
	       pMultipartSubtype);
  }

  //-------------------------------------
  /**
   * Sets the message content of a message with multiple file 
   * attachments.
   * 
   * <p>This method sets the message content to a MimeMultipart
   * (with a content type of "multipart/mixed"); the first part
   * is given by <code>pContent</code>, with the MIME type of 
   * <code>pContentType</code>, and the rest of the parts 
   * contain the file attachments.
   * 
   * @param pAttachments the files to attach to the message
   * @param pInlineAttachment if true, each of the files will be 
   * inlined into the message, rather than attached
   * @exception MessagingException if the message content could
   * not be set
   **/
  public static void setContent(Message pMessage, 
				Object pContent, 
				String pContentType,
				File[] pAttachments, 
				boolean pInlineAttachments)
       throws MessagingException
  {
    ContentPart[] contentParts = new ContentPart[1];
    contentParts[0] = new ContentPart(pContent, pContentType);

    setContent(pMessage, contentParts,
	       pAttachments, pInlineAttachments);
  }

  //-------------------------------------
  /**
   * Sets the message content of a message with multiple content
   * parts and multiple file attachments.  For example, a message 
   * could have two content parts, one of type "text/plain," 
   * another of type "text/html," and also several file 
   * attachments.
   * 
   * <p>Each element in <code>pContentParts</code> represents a 
   * message part, with the content and its MIME type contained
   * within the ContentPart object.
   * 
   * <p>This method sets the message content to a MimeMultipart
   * (with a content type of "multipart/mixed").  The first part
   * of the multipart is itself a MimeMultipart, with a content
   * type of "multipart/mixed;" for each <code>contentPart</code>,
   * <code>Part.setContent(contentPart.getContent(), 
   * contentPart.getContentType())</code> is called to set the 
   * part's content.  Additional parts of the outer multipart
   * contain the file attachments.
   * 
   * @see javax.mail.Part#setContent(Object, String)
   * @param pAttachments the files to attach to the message
   * @param pInlineAttachments if true, each of the files will be 
   * inlined into the message, rather than attached
   * @exception MessagingException if the message content could
   * not be set
   **/
  public static void setContent(Message pMessage, 
				ContentPart[] pContentParts, 
				File[] pAttachments, 
				boolean pInlineAttachments)
       throws MessagingException
  {
    setContent(pMessage, pContentParts, 
	       pAttachments, pInlineAttachments,
	       MULTIPART_SUBTYPE_MIXED);
  }

  //-------------------------------------
  /**
   * Sets the message content of a message with multiple content
   * parts and multiple file attachments.  For example, a message 
   * could have two content parts, one of type "text/plain," 
   * another of type "text/html," and also several file 
   * attachments.
   * 
   * <p>Each element in <code>pContentParts</code> represents a 
   * message part, with the content and its MIME type contained
   * within the ContentPart object.
   * 
   * <p>This method sets the message content to a MimeMultipart
   * (with a content type of "multipart/mixed").  The first part
   * of the multipart is itself a MimeMultipart, with a content
   * type of "multipart/<code>pMultipartSubtype</code>;" for each 
   * <code>contentPart</code>,
   * <code>Part.setContent(contentPart.getContent(), 
   * contentPart.getContentType())</code> is called to set the 
   * part's content.  Additional parts of the outer multipart
   * contain the file attachments.
   * 
   * @see javax.mail.Part#setContent(Object, String)
   * @param pAttachments the files to attach to the message
   * @param pInlineAttachments if true, each of the files will be 
   * inlined into the message, rather than attached
   * @exception MessagingException if the message content could
   * not be set
   **/
  public static void setContent(Message pMessage, 
				ContentPart[] pContentParts, 
				File[] pAttachments, 
				boolean pInlineAttachments,
				String pMultipartSubtype)
       throws MessagingException
  {
    DataHandler[] dataHandlers = null;

    if(pAttachments != null)
    {
      dataHandlers = new DataHandler[pAttachments.length];
    
      for(int i = 0; i < pAttachments.length; i++)
      {
	dataHandlers[i] = new DataHandler(new FileDataSource(pAttachments[i]));
      }
    }
    
    setContent(pMessage, pContentParts, pInlineAttachments, dataHandlers, pMultipartSubtype);
  }

  //-------------------------------------
  /**
   * Sets the message content of a message with multiple content
   * parts and multiple file attachments.  For example, a message 
   * could have two content parts, one of type "text/plain," 
   * another of type "text/html," and also several file 
   * attachments.
   * 
   * <p>Each element in <code>pContentParts</code> represents a 
   * message part, with the content and its MIME type contained
   * within the ContentPart object.
   * 
   * <p>This method sets the message content to a MimeMultipart
   * (with a content type of "multipart/mixed").  The first part
   * of the multipart is itself a MimeMultipart, with a content
   * type of "multipart/<code>pMultipartSubtype</code>;" for each 
   * <code>contentPart</code>,
   * <code>Part.setContent(contentPart.getContent(), 
   * contentPart.getContentType())</code> is called to set the 
   * part's content.  Additional parts of the outer multipart
   * contain the file attachments.
   * 
   * @see javax.mail.Part#setContent(Object, String)
   * @param pInlineAttachments if true, each of the files will be 
   * inlined into the message, rather than attached
   * @param pAttachments the DataHandlers representing the files
   *                     to attach to the message
   * @exception MessagingException if the message content could
   * not be set
   **/
  public static void setContent(Message pMessage, 
                                ContentPart[] pContentParts, 
                                boolean pInlineAttachments,
                                DataHandler[] pAttachments, 
                                String pMultipartSubtype)
  throws MessagingException
  {
    int numContentParts = 0;
    if (pContentParts != null)
      numContentParts = pContentParts.length;
    
    int numAttachments = 0;
    if (pAttachments != null)
      numAttachments = pAttachments.length;
    
    // 1+ body parts, 1+ attachments
    if ((numContentParts > 0) && (numAttachments > 0)) {
      Multipart mp = new MimeMultipart();
      
      // 1 body part, 1+ attachments
      if (numContentParts == 1) {
        for (int i = 0; i < numContentParts; i++) {
          BodyPart part = new MimeBodyPart();
          part.setContent(pContentParts[i].getContent(), 
                          pContentParts[i].getContentType());
          mp.addBodyPart(part);
        }
        
        for (int i = 0; i < numAttachments; i++) {
          BodyPart part = new MimeBodyPart();
          part.setDataHandler(pAttachments[i]);
          part.setFileName(pAttachments[i].getName());
          part.setDisposition(pInlineAttachments ? Part.INLINE : Part.ATTACHMENT);
          mp.addBodyPart(part);
        }
        
        pMessage.setContent(mp);
      } 
      
      // 2+ body parts, 1+ attachments
      else {
        Multipart mpContent = new MimeMultipart(pMultipartSubtype);
        for (int i = 0; i < numContentParts; i++) {
          BodyPart part = new MimeBodyPart();
          part.setContent(pContentParts[i].getContent(), 
                          pContentParts[i].getContentType());
          mpContent.addBodyPart(part);
        }
        
        BodyPart partContent = new MimeBodyPart();
        partContent.setContent(mpContent);
        mp.addBodyPart(partContent);
        
        for (int i = 0; i < numAttachments; i++) {
          BodyPart part = new MimeBodyPart();
          part.setDataHandler(pAttachments[i]);
          part.setFileName(pAttachments[i].getName());
          part.setDisposition(pInlineAttachments ? Part.INLINE : Part.ATTACHMENT);
          mp.addBodyPart(part);
        }
        
        pMessage.setContent(mp);
      }
    }
    
    // 1+ body parts, 0 attachments
    else if (numContentParts > 0) {
      
      // 1 body part, 0 attachments
      if (numContentParts == 1)
        pMessage.setContent(pContentParts[0].getContent(), 
                            pContentParts[0].getContentType());
      
      // 2+ body parts, 0 attachments
      else {
        Multipart mp = new MimeMultipart(pMultipartSubtype);
        for (int i = 0; i < numContentParts; i++) {
          BodyPart part = new MimeBodyPart();
          part.setContent(pContentParts[i].getContent(), 
                          pContentParts[i].getContentType());
          mp.addBodyPart(part);
        }
        pMessage.setContent(mp);
      }
    }
    
    // 0 body parts, 1+ attachments
    else if (numAttachments > 0) {
      
      // 0 body parts, 1 attachment
      if (numAttachments == 1) {
        pMessage.setDataHandler(pAttachments[0]);
        pMessage.setFileName(pAttachments[0].getName());
        pMessage.setDisposition(pInlineAttachments ? Part.INLINE : Part.ATTACHMENT);
      } 
      
      // 0 body parts, 2+ attachments
      else {
        Multipart mp = new MimeMultipart();
        for (int i = 0; i < numAttachments; i++) {
          BodyPart part = new MimeBodyPart();
          part.setDataHandler(pAttachments[i]);
          part.setFileName(pAttachments[i].getName());
          part.setDisposition(pInlineAttachments ? Part.INLINE : Part.ATTACHMENT);
          mp.addBodyPart(part);
        }
        pMessage.setContent(mp);
      }
    }
  }

  //-------------------------------------
  /**
   * Gets the DataHandler associated with the given message Part, 
   * and sets its CommandMap to the given CommandMap.  If the 
   * given Part is a Multipart, recursively sets the CommandMap
   * for all the DataHandlers associated with the subparts.
   * 
   * <p>You should call this method on a Message if you'd like
   * to extend its DataHandler's CommandMap without changing the
   * default CommandMap for the system.  For example, you might 
   * want to have the CommandMap include some additional content
   * handlers to handle additional content types - say, a 
   * GifDataContentHandler for content of type "image/gif" - and
   * use it to send a Message with "image/gif" content.
   * 
   * @see javax.activation.CommandMap
   * @see javax.activation.DataHandler
   * @see javax.activation.DataContentHandler
   * @exception IOException if an error was encountered while
   * operating on the DataHandler
   * @exception MessagingException if an error was encountered
   * while operating on the message part
   **/
  /*
  static void setCommandMap(Part pPart, CommandMap pCommandMap)
       throws IOException, MessagingException
  {
    Object content = pPart.getContent();
    if (content instanceof Multipart) {
      Multipart mp = (Multipart) content;
      int numParts = mp.getCount();
      for (int i = 0; i < numParts; i++) {
	Part subpart = mp.getBodyPart(i);
	MimeMessageUtils.setCommandMap(subpart, pCommandMap);
      }
    } else {
      DataHandler dh = pPart.getDataHandler();
      dh.setCommandMap(pCommandMap);
    }
  }
  */
}

