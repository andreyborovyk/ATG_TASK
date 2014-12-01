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

package atg.service.email.pop;

import atg.core.util.ResourceUtils;
import atg.service.email.MimeMessageUtils;
import atg.service.email.examiner.EmailExaminer;
import atg.service.email.examiner.AdditionalHeadersEmailExaminer;
import atg.service.email.examiner.ExaminerUtils;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;


import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.util.*;

/**
 * Utility class to to check email at regular intervals, and
 * parse for bounced messages.
 * NOTE: This active checker's only purpose in life is to
 *       scan for bounced messages. A new class (with new
 *       behavior) should be designed to take advantage of
 *       this package's other email features.
 *
 * This type of email retrieval is called "active email checking."
 * Programmatically retrieving email information is called "passive
 * email checking," and is allowed under this schema. In fact, both
 * forms of checking can be activated on the same POP3 instance without
 * interferance.
 *
 * @author R. DeMillo
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/service/email/pop/POP3Service.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo
 *   description: Checks email POP service for bounced returns
 *   attribute: functionalComponentCategory E-Mail
 *   attribute: featureComponentCategory
 *
 * @deprecated Use the atg.service.email.StoreService instead of this class.
 */
public class POP3Service
  extends StoreService
  implements POP3StatusListener {

  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/service/email/pop/POP3Service.java#2 $$Change: 651448 $";

  /** The resource name for this service */
  static final String MY_RESOURCE_NAME = "atg.service.ServiceResources";

  /** The resource bundle for this service */
  static ResourceBundle sResourceBundle =
     ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  /** Specifies the POP3 instance */
  protected POP3 mPop3 = new POP3();

  /** The default attachment directory **/
  protected String mAttachmentDirectory;

  /**
   * Flag indicating if we are using java mail to connect to
   * the pop server, or if we are using our old style server
   * connection.
   **/
  protected boolean mUsingJavaMailProvider = true;

  /**
   * Create a disconnected <code>POP3Service</code> object
   *
   * @beaninfo
   *   description: establishes a service object
   */
  public POP3Service() {
  }

  //----------------------------
  // Property: usingJavaMailProvider
  /**
   * Flag indicating if we are using java mail to connect to
   * the pop server, or if we are using our old style server
   * connection.
   * @return true if we should use the javamail provider, false
   *         if we should be using our old style parser.
   **/
  public boolean isUsingJavaMailProvider(){
    return mUsingJavaMailProvider;
  }
  /**
   * Flag indicating if we are using java mail to connect to
   * the pop server, or if we are using our old style server
   * connection.
   * @param pUsingJavaMailProvider true if we should use the
   *                               javamail provider, false
   *                               if we should be using our
   *                               old style parser.
   **/
  public void setUsingJavaMailProvider(boolean pUsingJavaMailProvider){
    mUsingJavaMailProvider = pUsingJavaMailProvider;
  }
  
  /**
   * Sets the currently active POP3 instance with another. NOTE: This
   * will permenantly lose the original POP3 instance.
   *
   * @param newPop3 The new Pop3 instance
   */
  public void setPOP3Instance(POP3 inPop) {
    mPop3 = inPop;
  }

  /**
   * Returns the current POP3 instance
   *
   * @return The POP3 instance class
   * @see atg.service.email.pop.POP3
   */
  public POP3 getPOP3Instance() {
    return mPop3;
  }

  //----------------------------------------
  /**
   * Set the directory where attachments are stored.
   *
   * @param pAttachmentDirectory The directory where attachments are
   * going to be stored
   */
  public void setAttachmentDirectory(String pAttachmentDirectory ) {
    mAttachmentDirectory = pAttachmentDirectory;
    if(mPop3 != null)
      mPop3.setAttachmentDir(pAttachmentDirectory);
  }
  
  /**
   * Returns the directory where attachments are stored.
   *
   * @return The directory where attachments are stored
   *
   * @beaninfo
   *   description: The directory where attachments are stored
   */
  public String getAttachmentDirectory() {
    return mAttachmentDirectory;
  }

  /**
   * Notify listening objects that an email has been received
   * @param pMsg The POP3MailMessage that was created for this email
   * @param pBounced whether or not this message was bounced
   **/
  public void fireInboundMailEvent (POP3MailMessage pMsg,
				    boolean pBounced) {
    // Make sure we have a proper JavaMailMessage to pass in
    Message message = null;
    try{
      message = createJavaMailMessage(pMsg);
    }
    catch(MessagingException me){
      if(isLoggingError()){
	logError(ResourceUtils.getMsgResource
		 ("unableToCreateMessage",
		  MY_RESOURCE_NAME,
		  sResourceBundle), me);
      }
      // If we can't create a message, then there's no point in
      // continuing
      return;
    }
    fireInboundMailEvent(message, pBounced);
  }

  /****************************************************
   ** Fulfilling the POP3StatusListener inheritence
   ****************************************************/

  /**
   * This method is called whenever POP3 commands are
   * sent to/from the server. This can be used for
   * UI status progress, or other info presentations
   *
   * @param evt COMMAND, RESPONSE, PROGRESS, START, DONE
   */
  public void pop3Status(MailStatusEvent evt) {
    /** no-op for now */
  }

  /**
   * Performs the scheduled task for this service. This includes
   * checking the pop3 server for email, and firing InboundMailEvents
   * and MailBounceEvents.
   * @param pScheduler the scheduler that alerts this service to perform
   * its scheduled task
   * @param pJob the ScheduledJob for this service
   **/
  public void performScheduledTask(Scheduler pScheduler, ScheduledJob pJob)
  {
    // Check if we are using new or old style parsing
    if(isUsingJavaMailProvider()){
      // New style parsing
      super.performScheduledTask(pScheduler, pJob);
      return;
    }

    // Old style parsing
    POP3MailboxInfo mailbox_info;
    
    // Bail if the user/password/host combo is not set
    if ((getHost()     == null) || (getUsername() == null) ||
	(getPassword() == null))
      {
      	if (isLoggingWarning()) {
	  logWarning(ResourceUtils.getMsgResource
		     ("invalidConnectionParams",
		      MY_RESOURCE_NAME,
		      sResourceBundle));
	}
	return; // a no-op
      }
    if (isLoggingDebug())
      logDebug("Retrieving email from " + getHost());
    
    try {
      // Connect  to the POP3 server
      mPop3.connect(getHost());
      // Should not be logged in at this point
      if (mPop3.login(getUsername(), getPassword())) {
	
	mailbox_info = mPop3.status();
	
	if (mailbox_info.getMessageCount() < 1) {
	  if (isLoggingDebug())
	    logDebug("No messages on server");
	} else {
	  // Get the information on the messages and retrieve the
	  // content of each message.
	  POP3MessageInfo msg_info[] = mPop3.listMessages();
	  
	  if (isLoggingDebug())
	    logDebug("There are " +
		     msg_info.length +
		     " messages");
	  
	  for (int i = 0; i < msg_info.length; i++) {
	    
	    try {
	      POP3MailMessage msg = null;
              try {
		msg = mPop3.retrieveMessage(msg_info[i]);
              }
              catch(MalformedEmailException exc) {
                // NOTE - we expect the POP3MailMessage to
                // have read through all of the body in this
                // case
                if(mRemoveMalformedEmail || exc.getMailMessage() == null) {
                  if(isLoggingDebug())
                    logDebug("Removing malformed email");
		  mPop3.deleteMessage(msg_info[i]);                  
                  continue;
                }
                else {
                  // bad encapsulation...
                  if(isLoggingDebug())
                    logDebug("Processing malformed email");
                  msg = exc.getMailMessage();
                }
              }
	      
	      // Check for a bad address, write to the
	      // bad address file and notify the listeners
	      EmailExaminer chosenExaminer = null;
	      Message javaMessage = null;
	      String badAddress = null;
	      // See if we have any email examiners registered. If we don't
	      // then get determine the bad address the old-fashioned way just
	      // in case for some crazy reason people want to use this the
	      // old way
	      if(getEmailExaminers() == null || getEmailExaminers().length == 0) 
		badAddress = msg.isReturnToSender();
	      else {
		// Go through each email examiner and see if any think
		// that this message is bounced
		javaMessage = createJavaMailMessage(msg);
		EmailExaminer[] examiners = getEmailExaminers();
		for(int j = 0; j < examiners.length; j++) {
		  EmailExaminer examiner = examiners[j];
		// If we've found one that thinks it's bounced,
		// then we stop
		  if(examiner.isBouncedEmail(javaMessage)) {
		    chosenExaminer = examiner;
		    break;
		  }
		}
		if(chosenExaminer != null) {
		  if(isLoggingDebug())
		    logDebug(ExaminerUtils.runAllMethods
			     (chosenExaminer, javaMessage));
		  // Get the bad address
		  badAddress = 
		    chosenExaminer.getBouncedEmailAddress(javaMessage);
		}
	      }
	      
	      boolean bouncedMessage = (badAddress != null);
	      
	      // We have a message, so fire an InboundMailEvent
	      if(mFireInboundMailEvents) {
		if(javaMessage == null) 
		  fireInboundMailEvent(msg, bouncedMessage);
		else
		  fireInboundMailEvent
		    (javaMessage, bouncedMessage, chosenExaminer);
	      }
	      
	      // If we received a bounce back note, also fire a
	      // MailBouncedEvent
	      if (bouncedMessage) {
		printStatus(msg.getHeaderValue("Date") +
			    ": Return-to-sender. Sent to: " +
			    badAddress);


                Map additionalHeaders = null;
		Map trackingData = null;
		boolean isSoftBounce = false;
                if (chosenExaminer instanceof AdditionalHeadersEmailExaminer) {
		  AdditionalHeadersEmailExaminer examiner =
		    (AdditionalHeadersEmailExaminer)chosenExaminer;
                  additionalHeaders = examiner.getAdditionalHeaders(javaMessage);
		  trackingData = examiner.getTrackingData(javaMessage);
		  isSoftBounce = examiner.isSoftBouncedEmail(javaMessage);
                }
		
		// Notify the bounce listeners
		if(mFireBouncedMailEvents)
		  fireMailBounceEvent(badAddress,
				      msg.getHeaderValue("Date"),
				      0, // id is a no op right now
				      isSoftBounce,
                                      additionalHeaders,
				      trackingData);
		
		// See if it should be removed from the server
		if (mRemoveBouncedEMail)
		  mPop3.deleteMessage(msg_info[i]);
		
	      } else {
		// Received a standard email.
		printStatus(msg.getHeaderValue("Date") +
			    ": Received email from " +
			    msg.getHeaderValue("From") +
			    " concerning " +
			    msg.getHeaderValue("subject"));
		
		// See if it should be removed from the server
		if (mRemoveEMail)
		  mPop3.deleteMessage(msg_info[i]);
	      }
	    } 
            catch (Exception e) {
	      if (isLoggingError()) {
		logError("Caught exception handling message.");
		logError(e.getMessage());
		e.printStackTrace();
	      }
	    }
	  }
	}
      } else {
	if (isLoggingError()) {
	  logError("Failed to login to POP3 Server " + getHost() + " using username " + getUsername() + " and the supplied password.  Check your login information and make sure that the supplied username and password are correct.");
	}
      }
    } catch (Exception e) {
      if (isLoggingError()) {
	logError("Caught exception.");
	logError(e.getMessage());
	e.printStackTrace();
      }
    }
    
    // Logout and disconnect from the POP3 server
    finally {
      try {
	mPop3.logout();
      } catch (Exception e2) {
	if (isLoggingError())
	  logError(ResourceUtils.getMsgResource("unableToLogout",
						MY_RESOURCE_NAME,
						sResourceBundle), e2);
      }
      
      // Okay now shutdown the connection even if logout was an error.
      // We need this to ensure the connection is shut down correctly.
      try {
	mPop3.disconnect();
      } catch (Exception e2) {
	if (isLoggingError())
	  logError(ResourceUtils.getMsgResource("unableToDisconnect",
						MY_RESOURCE_NAME,
						sResourceBundle), e2);
      }
    }
  }
  
  /**
   * Logs the specified String to logInfo
   * @param s the string to be logged
   **/
  void printStatus(String s) {
    // Send a note to the file or the console
    if (isLoggingInfo())
      logInfo(s);
  }

  /**
   * Utility method to take a POP3MailMessage and construct a
   * <code>javax.mail.Message</code> out of it.
   * @param pPOP3Message the POP3MailMessage we'll use to construct
   * a <code>javax.mail.Message</code>
   * @exception MessagingException if anything goes wrong while
   * trying to create the Message
   **/
  protected Message createJavaMailMessage(POP3MailMessage pPOP3Message)
    throws MessagingException {

    MimeMessage mesg = (MimeMessage) MimeMessageUtils.createMessage();
    Dictionary headers = pPOP3Message.getHeader();

    // Grab all the headers and just call setHeader on the Message for
    // each one we find
    Enumeration e = headers.keys();
    while(e.hasMoreElements()) {
      String key = (String)e.nextElement();
      String value = (String) headers.get(key);
      mesg.setHeader(key, value);
    }

    // Get all of the message bodies
    MessageBody [] bodies = pPOP3Message.getBody();
    if(bodies.length == 1) {
      mesg.setContent(bodies[0].getText(), bodies[0].getUnparsedContentType());
    }
    else if(bodies.length > 1) {
      Multipart mp = new MimeMultipart();
      
      for(int i = 0; i < bodies.length; i++) {
	MessageBody body = bodies[i];
	// If the MessageBody is straight text
	if(body.getType() == MessageBody.TEXT) {
	  MimeBodyPart bodyPart = new MimeBodyPart();
	  String contentType = body.getUnparsedContentType();
	  bodyPart.setContent(body.getText(), contentType);
	  mp.addBodyPart(bodyPart);
	}
	// Otherwise, it's an attachment
	else if(body.getType() == MessageBody.FILE) {
	  MimeBodyPart bodyPart = new MimeBodyPart();
	  javax.activation.DataSource ds = body.getDataSource();
	  bodyPart.setDataHandler(new javax.activation.DataHandler(ds));
	  bodyPart.setDisposition(Part.ATTACHMENT);
	  bodyPart.setFileName(body.getFilename());
	  mp.addBodyPart(bodyPart);
	}
      }
      mesg.setContent(mp);
    }
    mesg.saveChanges();
    return mesg;
  }
}



