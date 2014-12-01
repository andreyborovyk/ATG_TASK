/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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

package atg.portal.gear.poll;

import atg.portal.alert.GearMessagePublisher;
import atg.portal.framework.*;
import atg.portal.nucleus.NucleusComponents;


import atg.droplet.GenericFormHandler;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.repository.*;
import atg.repository.rql.RqlStatement;
import atg.dtm.*;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;
import atg.userdirectory.*;
import atg.nucleus.naming.ParameterName;
import atg.userprofiling.Profile;

import java.util.Locale;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.transaction.*;


/**
 * This is the form-handler for handling a vote submission for a given poll.
 *
 * @beaninfo
 *   description: A form handler used to record a vote for a poll
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory Portal
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/PollVoteFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 *
 * @see GenericFormHandler
 *
 */
public class PollVoteFormHandler extends GenericFormHandler
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/PollVoteFormHandler.java#2 $$Change: 651448 $";

   // Resource Bundle Name
   /** Name of the resource */
   static final String MY_RESOURCE_NAME = "atg.portal.gear.poll.pollResources";
   /** Name of the locale parameter */
   static final ParameterName LOCALE_PARAM = ParameterName.getParameterName("locale");

   private static ResourceBundle sResourceBundle = ResourceBundle.getBundle(MY_RESOURCE_NAME,java.util.Locale.getDefault());

    //----------------------------------------
    // Properties
    //----------------------------------------

  //---------------------------------------------------------------------------
  // property: pollId
  String mPollId;

  public void setPollId(String pPollId) {
    mPollId = pPollId;
  }

  public String getPollId() {
    return mPollId;
  }

  //---------------------------------------------------------------------------
  // property: pollSelection
  String mPollSelection;

  public void setPollSelection(String pPollSelection) {
    mPollSelection = pPollSelection;
  }

  public String getPollSelection() {
    return mPollSelection;
  }

  //----------------------------------------
  // property: gearId
  private String mGearId;

  public void setGearId(String pGearId)
    { mGearId = pGearId; }

  public String getGearId()
    { return mGearId; }

  //----------------------------------------
  // property: selectionText
  private String mSelectionText;

  public void setSelectionText(String pSelectionText)
    { mSelectionText = pSelectionText; }

  public String getSelectionText()
    { return mSelectionText; }

  //----------------------------------------
  // property: profile
  private Profile mProfile;

  public void setProfile(Profile pProfile) 
	{ mProfile = pProfile; }

  public Profile getProfile() 
	{ return mProfile; }

  //----------------------------------------
  // property: successUrl
  private String mSuccessUrl;

  public void setSuccessUrl(String pSuccessUrl)
    { mSuccessUrl = pSuccessUrl; }

  public String getSuccessUrl()
    { return mSuccessUrl; }


  //----------------------------------------
  // property: failureUrl
  private String mFailureUrl;

  public void setFailureUrl(String pFailureUrl)
    { mFailureUrl = pFailureUrl; }

  public String getFailureUrl()
    { return mFailureUrl; }

  //-------------------------------------
  // property: pollTracker
  private PollTrackerBean mPollTracker;

  public void setPollTracker(PollTrackerBean pPollTracker) {
     mPollTracker = pPollTracker;
  }
  public PollTrackerBean getPollTracker() {
     return mPollTracker;
  }

  //-------------------------------------
  // property: publisher
  private static GearMessagePublisher mPublisher;

  public void setPublisher(GearMessagePublisher pPublisher) {
     mPublisher = pPublisher;
  }
  public GearMessagePublisher getPublisher() {
     return mPublisher;
  }

    /** the gear environment */
    GearEnvironment mGearEnvironment;

    //-------------------------------------
    /**
     * Sets the gear environment
     **/
    public void setGearEnvironment(GearEnvironment pGearEnvironment) {
        mGearEnvironment = pGearEnvironment;
    }

    //-------------------------------------
    /**
     * Returns the gear environment
     **/
    public GearEnvironment getGearEnvironment() {
        return mGearEnvironment;
    }

    /**
     * Gets the gear environment from a servlet request.
     */
    void initGearEnvironment (DynamoHttpServletRequest pReq,
                              DynamoHttpServletResponse pResp)
    {
        pReq.setAttribute("atg.paf.Gear", pReq.getParameter("paf_gear_id"));
        pReq.setAttribute("atg.paf.PortalRepositoryLocation", "dynamo:/atg/portal/framework/PortalRepository");
        GearEnvironment env = null;

        try {
            setGearEnvironment(EnvironmentFactory.getGearEnvironment(pReq, pResp));
        }
        catch (EnvironmentException e) {
            if (isLoggingError())
                logError(e);
        }

    }
  //-------------------------------------
  // property: TransactionManager
  TransactionManager mTransactionManager;

  /**
   * Sets property transactionManager
   * @param pTransactionManager  The beginning and ending of transactions are handled by the transaction manager.
   * @beaninfo description:  The beginning and ending of transactions are handled by the transaction manager.
   */
  public void setTransactionManager(TransactionManager pTransactionManager) {
    mTransactionManager = pTransactionManager;
  }

  /**
   * The beginning and ending of transactions are handled by the
   * transaction manager.
   *
   * @beaninfo description: The transactionManager begins and ends all transactions.
   * @return the transaction manager
   **/
  public TransactionManager getTransactionManager() {
    return mTransactionManager;
  }

  //-------------------------------------
  /**
   * property: pollRepository
   */
  protected static MutableRepository mPollRepository;

  protected MutableRepository getPollRepository()
  {
    return mPollRepository;
  }

  public void setPollRepository(MutableRepository pPollRepository) {
    mPollRepository = pPollRepository;
  }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

   protected boolean postResponse(DynamoHttpServletRequest pRequest) {

   boolean retval=true;

   if (getPollSelection() == null) { 
      addFormException( new DropletException(sResourceBundle.getString("no-selection-msg")));
      return false;
   }

   // perform JNDI lookup
   TransactionDemarcation td = new TransactionDemarcation ();

   try { 
     try { 

	 td.begin (getTransactionManager());

         MutableRepositoryItem pollResponse = mPollRepository.getItemForUpdate(getPollSelection(),"pollResponse");

         // Grab the text of this resonse while we have it, for use in the PollResponse event
         setSelectionText((String) pollResponse.getPropertyValue("responseText"));

	 // update the response count on the pollResponse record
         int numPosts = ((Integer) pollResponse.getPropertyValue("responseCount")).intValue()+1;
	 pollResponse.setPropertyValue("responseCount", (new Integer(numPosts)));
	 mPollRepository.updateItem(pollResponse);
     } 
     catch (atg.repository.RepositoryException e) {

     if (isLoggingError()) {
         logError(sResourceBundle.getString("repository-error-msg"),e);
     }
	 retval=false;
         addFormException( new DropletException(sResourceBundle.getString("user-error-msg")));
     }
     finally {
        td.end ();
     }
   } 
   catch (TransactionDemarcationException e) {
     if (isLoggingError()) {
       logError(sResourceBundle.getString("txn-error-msg"),e);
     }
      retval=false;
      addFormException( new DropletException(sResourceBundle.getString("user-error-msg")));
   }

    return retval; 
    
}


    //-------------------------------------
    /**
     *
     */

   public void sendMessage(PollMessage pMessage)
   {
     try {

         if (getPublisher() != null)
         {
           getPublisher().writeMessage(pMessage);
         }
     } catch( Exception e ) {
      if (isLoggingError()) {
        logError(sResourceBundle.getString("send-message-exception"),e);
      }
     }
    }
    
    //----------------------------------------
    protected void createUserPollEvent() {

       if (getProfile()!=null) {
         //
         // Create the Poll message, set its properties, and send it.
         //
         PollVoteMessage message = new PollVoteMessage( getGearEnvironment() );
         message.setProfileId(getProfile().getRepositoryId());
         message.setPollId(getPollId());
         message.setPollSelection(getPollSelection());
         message.setResponseText(getSelectionText());
         sendMessage(message);
	 if (isLoggingDebug()) {
	   logDebug("PollVoteFormHandler - sent PollVoteMessage: " + message);
	 }
       } else {
         if (isLoggingError()) {
           logError(sResourceBundle.getString("null-profile-msg"));
	 }
       }
    }

 /**
  * Called when a form is rendered that references this bean.  This call
  * is made before the service method of the page is invoked.
  */
   public boolean beforeSet(DynamoHttpServletRequest request,
                            DynamoHttpServletResponse response)
       throws DropletFormException {

       initGearEnvironment(request, response);
       super.beforeGet(request, response);
       return true;
   }


 /**
  * handle "vote" form submission
  * 
  */
  public boolean handleVote(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
    {

        if(getPollRepository() == null) {
         if (isLoggingError()) {
           logError(sResourceBundle.getString("null-repository-msg"));
	 }
	 return false;
        }

	

	if (postResponse(pRequest)) {
	  createUserPollEvent();
	  if (getPollTracker()!=null) {
	    mPollTracker.addPoll(getPollId());
	  }
 	}
	return checkFormRedirect (getSuccessUrl() , getFailureUrl(), pRequest, pResponse);

    }

} // end of class
