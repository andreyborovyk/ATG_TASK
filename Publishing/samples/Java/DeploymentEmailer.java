/*<ATGCOPYRIGHT>
 * Copyright (C) 2000-2011 Art Technology Group, Inc.
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
package atg.deployment.common.event;

import java.util.Date;

import atg.service.email.SMTPEmailSender;
import atg.service.email.EmailException;
import atg.deployment.DeploymentConstants;

/**
 *
 * A deployment event listener that sends email out when a deployment
 * succeeds or fails. <P>
 *
 * When registered with the deployment server as a deployment event
 * listener, this listener will wait for a deployment completion event
 * or any event that is an error.  Upon receiving either, the listener
 * will use the SMTPEmailSender service to send an email to the
 * specified address.  The useShortMessage property can be set
 * accordingly for either a brief message (ideal for pagers) or a
 * detailed listing of properties from the deployment event. <P>
 *
 * Multiple email listeners can be registered and customized to suit
 * varying needs.
 *
 * The email listener properties file will look similar to:
 * <PRE>
 *  $class=atg.deployment.common.event.DeploymentEmailer
 *  
 *  SMTPEmailSender=/atg/dynamo/service/SMTPEmail
 *  
 *  fromAddress=important-guy@thebigcompany.com
 *  toAddress=another-important-guy@thebigcompany.com
 *
 *  useShortMessage=true
 * </PRE>
 *
 * The listener can conveniently be turned off and on via the active
 * property in the ComponentBrowser.
 *
 * @see DeploymentEventListener
 *
 * @author pagan lord craig
 * @version $Id: //product/Publishing/version/10.0.3/pws/classes.jar/src/atg/deployment/common/event/DeploymentEmailer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class DeploymentEmailer
  implements DeploymentEventListener
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/Publishing/version/10.0.3/pws/classes.jar/src/atg/deployment/common/event/DeploymentEmailer.java#2 $$Change: 651448 $";

  /*--------------------------------------------------------*/
  // Constants

  /*--------------------------------------------------------*/
  // Member variables

  /*--------------------------------------------------------*/
  // Constructor

  /** 
   * Constructs the DeploymentEmailer with no special arguments.
   * (It's more of a passive class.)
   **/
  public DeploymentEmailer ()
  {
  }


  /*--------------------------------------------------------*/
  // Properties
  //
  //   SMTPEmailSender
  //
  //   fromAddress
  //   toAddress
  //
  //   useShortMessage
  //
  //   active
  //

  /** property: SMTPEmailSender */
  SMTPEmailSender mSMTPEmailSender;
  /**
   * The SMTPEmailSender service to send emails through.
   * @beaninfo description: The SMTPEmailSender service to send emails through.
   * @param pSMTPEmailSender new value to set
   **/
  public void setSMTPEmailSender (SMTPEmailSender pSMTPEmailSender)
  {
    mSMTPEmailSender = pSMTPEmailSender;
  }
  /**
   * The SMTPEmailSender service to send emails through.
   * @beaninfo description: The SMTPEmailSender service to send emails through.
   * @return SMTPEmailSender
   **/
  public SMTPEmailSender getSMTPEmailSender ()
  {
    return mSMTPEmailSender;
  }


  /** property: fromAddress */
  String mFromAddress;
  /**
   * The email address deployment notifications should claim to be from.
   * @beaninfo description: The email address deployment notifications should claim to be from.
   * @param pFromAddress new value to set
   **/
  public void setFromAddress (String pFromAddress)
  {
    mFromAddress = pFromAddress;
  }
  /**
   * The email address deployment notifications should claim to be from.
   * @beaninfo description: The email address deployment notifications should claim to be from.
   * @return String
   **/
  public String getFromAddress ()
  {
    return mFromAddress;
  }


  /** property: toAddress */
  String mToAddress;
  /**
   * The email address to send deployment notifications to.
   * @beaninfo description: The email address to send deployment notifications to.
   * @param pAddress new value to set
   **/
  public void setToAddress (String pToAddress)
  {
    mToAddress = pToAddress;
  }
  /**
   * The email address to send deployment notifications to.
   * @beaninfo description: The email address to send deployment notifications to.
   * @return String
   **/
  public String getToAddress ()
  {
    return mToAddress;
  }


  /** property: useShortMessage */
  boolean mUseShortMessage = false;
  /**
   * Whether to send a short, pager-sized message or a longer, more verbose message.
   * @beaninfo description: Whether to use a short, pager-sized message or a longer, more verbose message.
   * @param pUseShortMessage new value to set
   **/
  public void setUseShortMessage (boolean pUseShortMessage)
  {
    mUseShortMessage = pUseShortMessage;
  }
  /**
   * Whether to send a short, pager-sized message or a longer, more verbose message.
   * @beaninfo description: Whether to use a short, pager-sized message or a longer, more verbose message.
   * @return boolean
   **/
  public boolean getUseShortMessage ()
  {
    return mUseShortMessage;
  }


  /** property: active */
  boolean mActive = true;
  /**
   * Whether or not the listener should send email notifications.
   * @beaninfo description: Whether or not the listener should send email notifications.
   * @param pActive new value to set
   **/
  public void setActive (boolean pActive)
  {
    mActive = pActive;
  }
  /**
   * Whether or not the listener should send email notifications.
   * @beaninfo description: Whether or not the listener should send email notifications.
   * @return boolean
   **/
  public boolean isActive ()
  {
    return mActive;
  }


  /*--------------------------------------------------------*/
  // Public Methods
  //
  //   deploymentEvent
  //

  /**
   * DeploymentEventListener method called for event firing.
   **/
  public void deploymentEvent (DeploymentEvent pEvent)
  {
    // We only do our thing if we have an email sender, addresses, we
    // are active, and if the state is with us.
    //
    // BUG: Should we internalize some of the yucky javax.mail stuff
    // rather than depend on having an SMTPEmailSender?
    //
    // NOTE: we have no logging interface so we cannot log warnings if
    // parameters are not set properly!
    if (getSMTPEmailSender() != null &&
        getFromAddress() != null &&
        getToAddress() != null &&
        isActive() &&
        (pEvent.getNewState() == DeploymentEvent.DEPLOYMENT_COMPLETE ||
         pEvent.getNewState() == DeploymentEvent.EVENT_INTERRUPT ||
         DeploymentEvent.stateToString(pEvent.getNewState()).startsWith("ERROR")))
      {
        // Gather the portion of our email.
        String subject = DeploymentEvent.stateToString(pEvent.getNewState());
        String body = null;
        // We have two quick-config message types, short and long.
        if (getUseShortMessage())
          {
            // Short message consists of the target name and the state
            // string.
            body =
              pEvent.getTarget()+" : "+
              DeploymentEvent.stateToString(pEvent.getNewState());
            // If this was an error/interrupt, we can attempt to include
            // the error message in with the email.  Since this is the
            // short form, it might get truncated anyway.
            if (pEvent.getNewState() == DeploymentEvent.EVENT_INTERRUPT ||
                DeploymentEvent.stateToString(pEvent.getNewState()).startsWith("ERROR"))
              body = body+" : "+pEvent.getErrorMessage();
          }
        else
          {
            // Long message consists of everything.  Including the
            // kitchen sink.
            body =
              "target : "+pEvent.getTarget()+"\n"+
              "new state : "+DeploymentEvent.stateToString(pEvent.getNewState())+"\n"+
              ""+(pEvent.getErrorMessage() == null ? "" : "error message : "+pEvent.getErrorMessage()+"\n")+
              "deployment ID : "+pEvent.getDeploymentID()+"\n"+
              "deployment begin timestamp : "+new Date(pEvent.getDeploymentBeginTimestamp())+"\n"+
              "deployment project IDs : "+stringArrayToString(pEvent.getDeploymentProjectIDs())+"\n"+
              "deployment type : "+(pEvent.getDeploymentType() == DeploymentConstants.TYPE_FULL ? "TYPE_FULL" : "TYPE_INCREMENTAL")+"\n"+
              "deployment mode : "+(pEvent.getDeploymentMode() == DeploymentConstants.MODE_ONLINE ? "MODE_ONLINE" : "MODE_SWITCH")+"\n"+
              "deployment server : "+pEvent.getDeploymentServer()+"\n"+
              "deployment scheduled : "+pEvent.isDeploymentScheduled()+"\n"+
              "deployment create initiator : "+pEvent.getDeploymentCreateInitiator()+"\n"+
              ""+(pEvent.getDeploymentStopInitiator() == null ? "" : "deployment stop initiator : "+pEvent.getDeploymentStopInitiator()+"\n")+
              "";

          }

        // With our email pieces composed, fire the email off.
        try {
          getSMTPEmailSender().sendEmailMessage(getFromAddress(),
                                                getToAddress(),
                                                subject,
                                                body);
        } catch (EmailException ee) {
          // Since we aren't a service with a logging interface of our
          // own, we're going to try and do something a little
          // under-handed here.  We're going to force the email
          // service to log our exception for us.
          if (getSMTPEmailSender().isLoggingError())
            getSMTPEmailSender().logError(ee);
        }
      }
  }


  /*--------------------------------------------------------*/
  // Protected Methods
  //
  //   stringArrayToString
  //

  /**
   * Prints the elements of the array into a single String.
   **/
  String stringArrayToString (String[] pArray)
  {
    if (pArray == null)
      return null;

    StringBuffer sb = new StringBuffer("[ ");
    for (int i = 0; i < pArray.length; i++)
      {
        if (i > 0)
          sb.append(", ");
        sb.append(pArray[i]);
      }
    sb.append(" ]");
    return sb.toString();
  }

} // End of class DeploymentEmailer
