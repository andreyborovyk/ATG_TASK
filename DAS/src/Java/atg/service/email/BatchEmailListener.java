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

import java.util.Vector;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.MessagingException;
import atg.service.scheduler.*;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.core.util.ResourceUtils;

/** 
 * An EmailListener that batches up EmailEvent objects and 
 * periodically sends out corresponding pieces of email using 
 * an EmailMessageSender.  Each batch of email messages is sent
 * using a single connection to the mail server.
 * 
 * <p>Two properties control how often the batch sends are 
 * performed - <code>maxBatchSize</code> and 
 * <code>sendSchedule</code>.  If <code>maxBatchSize</code> is
 * specified, a send is performed whenever the number of 
 * batched email events reaches <code>maxBatchSize</code>.  
 * Also, if <code>sendSchedule</code> is specified, sends are 
 * performed according to the schedule.  At least one of 
 * these properties must be specified for the service to 
 * function properly.
 * 
 * <p>Since the <code>sendEmailEvent</code> method may result 
 * in a batch send of all the collected events (if the number
 * of events reaches <code>maxBatchSize</code>), you should not
 * use the BatchEmailListener service directly, but rather in
 * conjunction with the EmailListenerQueue (i.e., configure 
 * the EmailListenerQueue to have BatchEmailListener as a 
 * listener).
 * 
 * @author Natalya Cohen
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/service/email/BatchEmailListener.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class BatchEmailListener extends GenericService 
implements EmailListener, Schedulable
{
  //-------------------------------------
  // Constants
  //-------------------------------------

  /** Class version string **/
  public static String CLASS_VERSION = 
  "$Id: //product/DAS/version/10.0.3/Java/atg/service/email/BatchEmailListener.java#2 $$Change: 651448 $";

  /** Resource name **/
  static final String MY_RESOURCE_NAME = "atg.service.ServiceResources";

  /** Resource bundle **/
  private static java.util.ResourceBundle sResourceBundle = 
  java.util.ResourceBundle.getBundle("atg.service.ServiceResources", atg.service.dynamo.LangLicense.getLicensedDefault());

  //-------------------------------------
  // Member variables
  //-------------------------------------

  /** EmailMessageSender used to send email **/
  EmailMessageSender mEmailMessageSender;
  
  /** Number of EmailEvents to batch for each send **/
  int mMaxBatchSize = 0;

  /** Scheduler responsible for scheduling batch sends **/
  Scheduler mScheduler;

  /** Schedule for performing batch sends **/
  Schedule mSendSchedule;

  /** Schedule for clearing the dead email queue **/
  Schedule mClearDeadEmailSchedule;

  /** A list of EmailEvents to be sent **/
  Vector mEmailEvents = new Vector();

  /** A Dictionary with key-value pairs consisting of
   *  EmailEvent/EmailException objects. Any EmailEvent
   *  that was unable to be delivered is put into here
   **/
  Dictionary mDeadEmailEvents = new Hashtable();

  /** The job id of the scheduled task */
  int mJobId = -1;

  /** The job name for the scheduled task **/
  String mJobName = "BatchEmailListener";

  /** The job description for the scheduled task **/
  String mJobDescription = "Batch sending of email";

  /** The job id of the dead email scheduled task */
  int mDeadJobId = -1;

  /** The job name for the dead email scheduled task **/
  String mDeadJobName = "DeadEmailFlusher";

  /** The job description for the dead email scheduled task **/
  String mDeadJobDescription = "Flushes dead email";

  //-------------------------------------
  // Methods
  //-------------------------------------

  //-------------------------------------
  /** 
   * Returns the EmailMessageSender used to send email. 
   **/
  public EmailMessageSender getEmailMessageSender() {
    return mEmailMessageSender;
  }

  //-------------------------------------
  /** 
   * Sets the EmailMessageSender used to send email. 
   **/
  public void setEmailMessageSender(EmailMessageSender pSender) {
    mEmailMessageSender = pSender;
  }
  
  //-------------------------------------
  /** 
   * Returns the maximum number of EmailEvents that will be 
   * batched before a send is performed.  If zero, no maximum
   * is assumed - the events will keep accumulating until 
   * sent by some other means (e.g., via the schedule).
   **/
  public int getMaxBatchSize() {
    return mMaxBatchSize;
  }

  //-------------------------------------
  /** 
   * Sets the maximum number of EmailEvents that will be 
   * batched before a send is performed.  If zero, no maximum
   * is assumed - the events will keep accumulating until 
   * sent by some other means (e.g., via the schedule).
   **/
  public void setMaxBatchSize(int pMaxBatchSize) {
    mMaxBatchSize = pMaxBatchSize;
  }

  //-------------------------------------
  /** 
   * Returns the Scheduler responsible for scheduling batch 
   * sends.
   **/
  public Scheduler getScheduler() {
    return mScheduler;
  }

  //-------------------------------------
  /** 
   * Sets the Scheduler responsible for scheduling batch sends.
   **/
  public void setScheduler(Scheduler pScheduler) {
    mScheduler = pScheduler;
  }

  //-------------------------------------
  /** 
   * Returns the Schedule for perfoming batch sends.
   **/
  public Schedule getSendSchedule() {
    return mSendSchedule;
  }

  //-------------------------------------
  /** 
   * Sets the Schedule for perfoming batch sends.
   **/
  public void setSendSchedule(Schedule pSendSchedule) {
    mSendSchedule = pSendSchedule;
  }

  //-------------------------------------
  /** 
   * Returns the Schedule for clearing the Dead Email Queue.
   **/
  public Schedule getClearDeadEmailSchedule() {
    return mClearDeadEmailSchedule;
  }

  //-------------------------------------
  /** 
   * Sets the Schedule for clearing the Dead Email Queue.
   **/
  public void setClearDeadEmailSchedule(Schedule pClearDeadEmailSchedule) {
    mClearDeadEmailSchedule = pClearDeadEmailSchedule;
  }

  //----------------------------------------
  /* Defines whether or not we collect dead email
   */
  boolean mCollectDeadEmail = false;
  public void setCollectDeadEmail(boolean p) { mCollectDeadEmail = p; }
  public boolean isCollectDeadEmail() { return mCollectDeadEmail; }

  //-------------------------------------
  /**
   * Gets the number of dead letters (letters that couldn't be sent
   * due to errors)
   **/
  public int getDeadEmailCount(){
      return mDeadEmailEvents.size();
  }

  //-------------------------------------
  /** Returns a Dictionary object with each row
   * consisting of an undelivered EmailEvent and its 
   * corresponding EmailException
   **/
  public Dictionary getDeadEmailDictionary(){
      return (Dictionary)((Hashtable)mDeadEmailEvents).clone();
  }

  //-------------------------------------
  /** Returns an Enumeration of undelivered EmailEvent objects **/
  public Enumeration getDeadEmailEvents(){
      return mDeadEmailEvents.keys();
  }

  //------------------------------------------
  /** Returns an Enumeration of EmailExceptions that belong
   * to undelivered EmailEvents
   **/
  public Enumeration getDeadEmailExceptions(){
      return mDeadEmailEvents.elements();
  }
	
  //-------------------------------------
  /** 
   * Adds an EmailEvent to the internal batch queue.  Returns the
   * number of elements in the queue, counting the currently added
   * event.
   **/
  synchronized int addEmailEvent(EmailEvent pEvent) {
    mEmailEvents.addElement(pEvent);
    return mEmailEvents.size();
  }

  //-------------------------------------
  /** 
   * Empties the internal batch queue into an EmailEvent array, and
   * returns the array.  All the elements in the returned array are
   * removed from the queue.
   **/
  synchronized EmailEvent[] emptyEmailEvents() {
    int emailEventCount = mEmailEvents.size();
    if (emailEventCount == 0) 
      return null;

    EmailEvent[] emailEvents = new EmailEvent[emailEventCount];
    mEmailEvents.copyInto(emailEvents);
    mEmailEvents.removeAllElements();

    return emailEvents;
  }

  //------------------------------------
  /**
   * Empties the internal dead email batch queue into an EmailEvent
   * array, and returns the array. The internal dead email queue is
   * then cleared
   **/
  EmailEvent[] emptyDeadEmailEvents() {
    if(getDeadEmailCount() == 0)
      return null;
    
    synchronized(mDeadEmailEvents){
      int emailEventCount = getDeadEmailCount();
      EmailEvent[] emailEvents = new EmailEvent[emailEventCount];
      Enumeration deadEmailEvents = getDeadEmailEvents();
      for(int i = 0;i<emailEventCount;i++)
        emailEvents[i] = (EmailEvent)deadEmailEvents.nextElement();
      
      clearDeadEmailEvents();
      return emailEvents;
    }
  }

  /** 
   * Clears the current dead email queue
   **/
  public void clearDeadEmailEvents() {
    ((Hashtable)mDeadEmailEvents).clear();
  }

  //-------------------------------------
  /** 
   * Sends all the email currently in the batch queue.
   **/
  void sendEmail() {
      
      if (mEmailEvents.size() == 0)
	  return;

      Transport trans = null;
      try {
	// open a connection to the mail server
        trans = mEmailMessageSender.openConnection();
         
        if(trans != null){
          
	    EmailEvent[] emailEvents = emptyEmailEvents();

	    // send a message for each of the collected events
	    for (int i = 0; i < emailEvents.length; i++) {
	        // if an error occurs here, don't break out of the 
	        // loop - try to send the rest of the messages
	        try {
	            Message msg = emailEvents[i].getMessage();
	            mEmailMessageSender.sendEmailMessage(msg, trans);
	        }
	        catch (EmailException e) {
		    // Stick the Event and its Exception
		    // onto the dead event queue
		    if(isCollectDeadEmail())
			mDeadEmailEvents.put(emailEvents[i], e);

	            if (isLoggingError()){
		    Object [] args = { e.toString() }; 
	      	  logError(ResourceUtils.getMsgResource("emailEventNotSent", MY_RESOURCE_NAME, sResourceBundle, args));
		    }
	        }
	    }
        }
    }
    catch (EmailException e) {
        if (isLoggingError())
            logError(e.toString());
    }
    finally {
        // close the connection
        if (trans != null) {
            try { mEmailMessageSender.closeConnection(trans); }
            catch (EmailException e) { }
        }
    }
  }

    //----------------------------------
    /** Sends all of the email currently in the dead letter queue.
     * Be aware that some emails may be undeliverable and might end
     * up right back in the dead letter queue. As such, it is advised
     * to use either the <code>getDeadEmailDictionary</code> or
     * <code>getDeadEmailExceptions</code> method to examine the reason
     * why the email wasn't sent before using this method.
     **/
    public void sendDeadEmail()
    {
      if (mDeadEmailEvents.size() == 0)
	  return;

      Transport trans = null;
      try {
	// open a connection to the mail server
        trans = mEmailMessageSender.openConnection();
         
        if(trans != null){
          
	    EmailEvent[] emailEvents = emptyDeadEmailEvents();

	    // send a message for each of the collected events
	    for (int i = 0; i < emailEvents.length; i++) {
	        // if an error occurs here, don't break out of the 
	        // loop - try to send the rest of the messages
	        try {
	            Message msg = emailEvents[i].getMessage();
	            mEmailMessageSender.sendEmailMessage(msg, trans);
	        }
	        catch (EmailException e) {
		    // Stick the Event and its Exception
		    // onto the dead event queue
	            mDeadEmailEvents.put(emailEvents[i], e);

	            if (isLoggingError()){
		    Object [] args = { e.toString() }; 
	      	  logError(ResourceUtils.getMsgResource("emailEventNotSent", MY_RESOURCE_NAME, sResourceBundle, args));
		    }
	        }
	    }
        }
    }
    catch (EmailException e) {
        if (isLoggingError())
            logError(e.toString());
    }
    finally {
        // close the connection
        if (trans != null) {
            try { mEmailMessageSender.closeConnection(trans); }
            catch (EmailException e) { }
        }
    }
  }

  //-------------------------------------
  // EmailListener implementation
  //-------------------------------------

  //-------------------------------------
  /**
   * Adds the given email event to the internal batch queue.  
   * If the number of events reaches <code>maxBatchSize</code>, 
   * this also causes all the collected events to be sent as 
   * email messages.
   **/
  public void sendEmailEvent(EmailEvent pEvent)
       throws EmailException 
  {
    int emailEventCount = addEmailEvent(pEvent);
    if ((mMaxBatchSize > 0) && 
	(emailEventCount >= mMaxBatchSize))
      sendEmail();
  }
  
  //-------------------------------------
  // Schedulable implementation
  //-------------------------------------

  //-------------------------------------
  /**
   * Called when a scheduled job tied to this object occurs.
   **/
  public void performScheduledTask(Scheduler pScheduler, 
				   ScheduledJob pJob)
  {
    if (isLoggingDebug())
      logDebug("performing scheduled job: sendEmail");
    if(pJob.getJobId() == mJobId)
	sendEmail();
    if(pJob.getJobId() == mDeadJobId)
	clearDeadEmailEvents();
  }

  //-------------------------------------
  // GenericService overrides
  //-------------------------------------

  //-------------------------------------
  /**
   * Called after the service has been created, placed into the 
   * naming hierarchy, and initialized with its configured property
   * values.
   *
   * @exception ServiceException if the service had a problem 
   * starting up
   **/
  public void doStartService() throws ServiceException {
    // start the scheduled job
    if ((mScheduler != null) && (mSendSchedule != null)) {
      if (isLoggingDebug())
	logDebug("starting scheduled job: " + mJobName);

      ScheduledJob job =
	new ScheduledJob(mJobName,
			 mJobDescription,
			 getAbsoluteName(),
			 mSendSchedule,
			 this,
			 ScheduledJob.REUSED_THREAD);
      
      mJobId = mScheduler.addScheduledJob(job);
    }

    if(mClearDeadEmailSchedule != null){
	if(isLoggingDebug())
	    logDebug("starting scheduled job: " + mDeadJobName);

	ScheduledJob deadJob = new ScheduledJob(mDeadJobName,
					    mDeadJobDescription,
					    getAbsoluteName(),
					    mClearDeadEmailSchedule,
					    this,
					    ScheduledJob.REUSED_THREAD);
	mDeadJobId = mScheduler.addScheduledJob(deadJob);
    }
  }

  //-------------------------------------
  /**
   * Called when the service is required to shut down.
   *
   * @exception ServiceException if the service had a problem 
   * shutting down
   **/
  public void doStopService() throws ServiceException {
    // send any remaining email
    sendEmail();

    // stop the scheduled job
    if ((mScheduler != null) && (mJobId != -1)) {
      if (isLoggingDebug())
	logDebug("stopping scheduled job: " + mJobName);
      
      mScheduler.removeScheduledJob(mJobId);
      mJobId = -1;
    }

    if((mScheduler != null) && (mDeadJobId != -1)){
	if(isLoggingDebug())
	    logDebug("stopping scheduled job: " + mDeadJobName);
	
        mScheduler.removeScheduledJob(mDeadJobId);
	mDeadJobId = -1;
    }
  }

  //-------------------------------------
}
