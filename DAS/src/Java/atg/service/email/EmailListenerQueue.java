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

import atg.service.queue.EventQueueElement;

import java.util.Map;

/**
 *
 * <p>This is a queueing version of the interface
 * atg.service.email.EmailListener.
 * Calls made to this class are queued, then passed
 * on to the listeners of this interface
 *
 * <p>This class was <I>ORIGINALLY</I> generated automatically by
 * atg.service.queue.EventQueueGenerator using the command
 * java atg.service.queue.EventQueueGenerator atg.service.email.EmailListener.
 * <STRONG>HOWEVER, this class has been supplemented with the EmailSender
 * interface, which provides an easy mechanism for sending email messages.
 * Any messages genertated via these EmailSender methods are also queued through
 * the sendEmailEvent method.</STRONG>
 *
 * @author Bob Mason
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/service/email/EmailListenerQueue.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo
 *   description: A queuing e-mail listener
 *   attribute: functionalComponentCategory E-Mail
 *   attribute: featureComponentCategory
 **/

public class EmailListenerQueue
extends atg.service.queue.EventQueue
implements atg.service.email.EmailListener, EmailSender, TrackableEmailSender
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/service/email/EmailListenerQueue.java#2 $$Change: 651448 $";

  //-------------------------------------
  // The object representation of a method call

  class ParamBlock implements EventQueueElement {
    int callType;
    atg.service.email.EmailEvent param0;
    public void clearValues () {
      param0 = null;
    }
    public void copyTo (EventQueueElement pElem) {
      ParamBlock b = (ParamBlock) pElem;
      b.callType = callType;
      b.param0 = param0;
    }
  }

  protected EventQueueElement createElement ()
  {
    return new ParamBlock ();
  }

  //-------------------------------------
  // Event listener list

  atg.service.email.EmailListener [] queueListeners =
    new atg.service.email.EmailListener [4];
  int queueListenerCount;

  //-------------------------------------
  // Constructor
  public EmailListenerQueue () {
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
    emailEvent.setFrom(pFrom);
    emailEvent.setRecipient(pRecipient);
    emailEvent.setSubject(pSubject);
    emailEvent.setBody(pBody);

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
    emailEvent.setFrom(pFrom);
    emailEvent.setRecipient(pRecipient);
    emailEvent.setSubject(pSubject);
    emailEvent.setBody(pBody);
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
    emailEvent.setFrom(pFrom);
    emailEvent.setRecipientList(pRecipients);
    emailEvent.setSubject(pSubject);
    emailEvent.setBody(pBody);

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
    emailEvent.setFrom(pFrom);
    emailEvent.setRecipientList(pRecipients);
    emailEvent.setSubject(pSubject);
    emailEvent.setBody(pBody);
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
    emailEvent.setFrom(pFrom);
    emailEvent.setRecipient(pRecipient);
    emailEvent.setSubject(pSubject);
    emailEvent.setBody(pBody);
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
    emailEvent.setFrom(pFrom);
    emailEvent.setRecipientList(pRecipients);
    emailEvent.setSubject(pSubject);
    emailEvent.setBody(pBody);
    emailEvent.setExtraHeaders(pExtraHeaders);
    emailEvent.setTrackingData(pTrackingData);

    // send the event
    sendEmailEvent(emailEvent);
  }

  //-------------------------------------
  // Queue event listener methods
  //-------------------------------------

  /**
   * @beaninfo
   *   description: Event triggered when e-mail is sent
   */
  public synchronized void
  addEmailListener (atg.service.email.EmailListener pListener)
  {
    if (queueListenerCount >= queueListeners.length) {
      atg.service.email.EmailListener [] newa = new atg.service.email.EmailListener [queueListeners.length * 2];
      System.arraycopy (queueListeners, 0, newa, 0, queueListeners.length);
      queueListeners = newa;
    }
    queueListeners [queueListenerCount++] = pListener;
  }

  public synchronized void
  removeEmailListener (atg.service.email.EmailListener pListener)
  {
    for (int i = 0; i < queueListenerCount; i++) {
      if (queueListeners [i] == pListener) {
        System.arraycopy (queueListeners, i + 1,
                          queueListeners, i,
                          queueListenerCount - i - 1);
        queueListeners [--queueListenerCount] = null;
        break;
      }
    }
  }

  /**
   * @beaninfo
   *   description: Number of objects listening for e-mail events
   */
  public synchronized int getEmailListenerCount ()
  {
    return queueListenerCount;
  }

  /**
   * @beaninfo
   *   description: Objects that are listening for e-mail events
   */
  public synchronized atg.service.email.EmailListener [] getEmailListeners ()
  {
    atg.service.email.EmailListener [] ret =
      new atg.service.email.EmailListener [queueListenerCount];
    System.arraycopy (queueListeners, 0,
                      ret, 0,
                      queueListenerCount);
    return ret;
  }

  //-------------------------------------
  // atg.service.email.EmailListener methods
  //-------------------------------------

  // Stub for method sendEmailEvent
  public synchronized void
  sendEmailEvent (
    atg.service.email.EmailEvent arg0)
  throws
    atg.service.email.EmailException
  {
    ParamBlock p = (ParamBlock) getQueueHead ();
    p.callType = 0;
    p.param0 = arg0;
    addElement ();
  }

  //-------------------------------------
  // Dispatcher
  //-------------------------------------

  protected void
  dispatchElement (EventQueueElement pElem)
  {
    ParamBlock b = (ParamBlock) pElem;
    try {
      switch (b.callType) {
      case 0:
        for (int i = 0; i < queueListenerCount; i++)
          queueListeners [i].sendEmailEvent (b.param0);
        break;
      }
    }
    catch (Throwable exc) {
      handleDispatchError (exc);
    }
  }
}
