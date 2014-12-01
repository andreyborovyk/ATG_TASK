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

/**
 * An interface that describes a mechanism for managing sending email 
 * events.
 *
 * @author Bob Mason
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/service/email/EmailSender.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public interface EmailSender 
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = 
  "$Id: //product/DAS/version/10.0.3/Java/atg/service/email/EmailSender.java#2 $$Change: 651448 $";

  /**
   * Sends a message with the given parameters
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
       throws EmailException;

  /**
   * Sends a message with the given parameters
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
       throws EmailException;


  /**
   * Sends a message with the given parameters
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
       throws EmailException;

  /**
   * Sends a message with the given parameters
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
       throws EmailException;

  /**
   * Sends a message with the given parameters
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
       throws EmailException;

  /**
   * Sends a message with the given parameters
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
       throws EmailException;
}
