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


import atg.droplet.TransactionalFormHandler;
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
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.transaction.*;


/**
 * This is the form-handler for creating or editing a poll.
 *
 * @beaninfo
 *   description: A form handler used to create or edit a poll
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory Portal
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/PollFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 *
 * @see TransactionalFormHandler
 *
 */
public class PollFormHandler extends TransactionalFormHandler
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/PollFormHandler.java#2 $$Change: 651448 $";

   // Resource Bundle Name
   /** Name of the resource */
   static final String MY_RESOURCE_NAME = "atg.portal.gear.poll.pollResources";
   /** Name of the locale parameter */
   static final ParameterName LOCALE_PARAM = ParameterName.getParameterName("locale");

   /** Maximum number of responses for new poll */
   static final int MAX_RESPONSES = 20;

   private static ResourceBundle sResourceBundle = ResourceBundle.getBundle(MY_RESOURCE_NAME,java.util.Locale.getDefault());

  protected static RqlStatement mGearParamStatement;
  static {
    try {
      mGearParamStatement = RqlStatement.parseRqlStatement("gear = ?0");
    }
    catch(RepositoryException e) {
    }
  }
    //----------------------------------------
    // Properties
    //----------------------------------------

  //---------------------------------------------------------------------------
  // property: pollTitle
  String mPollTitle;

  public void setPollTitle(String pPollTitle) {
    mPollTitle = pPollTitle;
  }

  public String getPollTitle() {
    
    if (mPollTitle == null && mPollId != null) {
      try{
        RepositoryItem poll = mPollRepository.getItem(getPollId(),"poll");
        mPollTitle = (String)poll.getPropertyValue("title");
      } 
      catch (atg.repository.RepositoryException e) {
        logError("PollFormHandler:getPollTitle - Unable to get poll item: " + getPollId(), e);
        addFormException( new DropletException(sResourceBundle.getString("admin-repository-error")));
        setRollbackTransaction(true);     
      }        
    }
    return mPollTitle;
  }

  //---------------------------------------------------------------------------
  // property: questionText
  String mQuestionText;

  public void setQuestionText(String pQuestionText) {
    mQuestionText = pQuestionText;
  }

  public String getQuestionText() {
    if (mQuestionText == null && mPollId != null) {
      try{
        RepositoryItem poll = mPollRepository.getItem(getPollId(),"poll");
        mQuestionText = (String)poll.getPropertyValue("questionText");
      } 
      catch (atg.repository.RepositoryException e) {
        logError("PollFormHandler:getQuestionText - Unable to get poll item: " + getPollId(), e);
        addFormException( new DropletException(sResourceBundle.getString("admin-repository-error")));
        setRollbackTransaction(true);     
      }
    }
    return mQuestionText;
  }
  
 //-------------------------------------
 // property: Responses
 private String mResponses[] = new String[MAX_RESPONSES];

 public void setResponses(String pResponses[]) {
   mResponses=pResponses;
 }
 public String[] getResponses(){
    return mResponses;
 }
/*
 public void setResponses(List pResponses) {
   mResponses=pResponses;
 }

 public List getResponses(){
    return mResponses;
 }

 */
 public void setResponses(int pIndex, String pResponse)
 {
     mResponses[pIndex]=pResponse;
 }
 public String getResponses(int pIndex)
 {
     return mResponses[pIndex];
 }
  //---------------------------------------------------------------------------
  // property: makeDefaultPoll
  boolean mMakeDefaultPoll;

  public void setMakeDefaultPoll(boolean pMakeDefaultPoll) {
    mMakeDefaultPoll = pMakeDefaultPoll;
  }

  public boolean getMakeDefaultPoll() {
    return mMakeDefaultPoll;
  }

  //---------------------------------------------------------------------------
  // property: pollId
  String mPollId;

  public void setPollId(String pPollId) {
    mPollId = pPollId;
  }

  public String getPollId() {
    return mPollId;
  }

  //----------------------------------------
  // property: gearId
  private String mGearId;

  public void setGearId(String pGearId)
    { mGearId = pGearId; }

  public String getGearId()
    { return mGearId; }


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

  //----------------------------------------
  // property: moreUrl
  private String mMoreUrl;

  public void setMoreUrl(String pMoreUrl)
    { mMoreUrl = pMoreUrl; }

  public String getMoreUrl()
    { return mMoreUrl; }

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


   protected boolean insertPoll() {

   boolean retval=true;

   MutableRepositoryItem newPollItem=null;

   try {
      if((getPollTitle()== null || getPollTitle().trim().length()==0) ||
           (getQuestionText() == null || getQuestionText().trim().length()==0)){
        addFormException( new DropletException(sResourceBundle.getString("blank-fields-error"))); 
        setRollbackTransaction(true);      
        return false;   
       }
      
      if (getQuestionText().length() > 500)
      {
        java.text.MessageFormat message = new java.text.MessageFormat(sResourceBundle.getString("poll-question-too-long"));
        String messageText = message.format(new Object[] {new Integer(getQuestionText().length()), new Integer(500)}); 
        addFormException(new DropletException(messageText));
        setRollbackTransaction(true);
        return false;
      }

      int usableResponseCount = 0;
      for ( int j=0; j < MAX_RESPONSES; j++) {
       if (mResponses[j]!=null) {
         if (mResponses[j].length() > 0) {
          usableResponseCount++; 
         }
       }
      }
      
      if (usableResponseCount < 2) {
        addFormException( new DropletException(sResourceBundle.getString("responses-less-than-two")));
        setRollbackTransaction(true);
        return false;
      }  
   
       newPollItem = mPollRepository.createItem("poll");
       newPollItem.setPropertyValue("title", getPollTitle());
       newPollItem.setPropertyValue("questionText", getQuestionText());
       mPollRepository.addItem(newPollItem);

       // set pollId so it can be accessed from the page
       setPollId(newPollItem.getRepositoryId());
   } 
   catch (atg.repository.RepositoryException e) {
       logError(("CreatePoll - Unable to add poll item: id is " + e.getId() + "itemdescriptor is " + e.getItemDescriptorName()), e);
       addFormException( new DropletException(sResourceBundle.getString("admin-repository-error")));
       setRollbackTransaction(true);
       return false;     
   }

   // insert set of responses
   insertResponses();
   
   if (getMakeDefaultPoll()) {
	if (isLoggingDebug()) {
	    logDebug("Setting poll " + getPollId() + " as default for gear " + getGearId());
	}
      setGearPoll();
   }

   return retval;

   }

  protected void insertResponses() {
        
    for ( int i=0; i < MAX_RESPONSES; i++) {
     if (mResponses[i]!=null) {
       if (mResponses[i].length() > 0) {
	   if (isLoggingDebug()) {
	      logDebug("OOOOO - inserting response: "+i+") " + mResponses[i]);
	   }
        try {
             MutableRepositoryItem newReponseItem=null;
             RepositoryItem poll=mPollRepository.getItem(getPollId(),"poll"); 
             newReponseItem = mPollRepository.createItem("pollResponse");
             newReponseItem.setPropertyValue("responseText", mResponses[i]);
             newReponseItem.setPropertyValue("poll", poll);
             mPollRepository.addItem(newReponseItem);

        } 
        catch (atg.repository.RepositoryException e) {
           logError(("PollFormHandler - Unable to add pollReponse item: id is " + e.getId() + "itemdescriptor is " + e.getItemDescriptorName()), e);
           addFormException( new DropletException(sResourceBundle.getString("admin-repository-error")));
           setRollbackTransaction(true);     
        }
       }
     }
    }
  }

  protected void setGearPoll() {

     try { 
	    
 	 // check for "clear" selected - if so, remove gearPoll item and get out
      
	    // retrieve the poll item for the id we have
            RepositoryItem targetPoll = mPollRepository.getItem(getPollId(),"poll");

	    // if there is a record for this gear - update it, otherwise, create a new one
      	    RepositoryView gpView = getPollRepository().getView("gearPoll");
      	    Object [] params = new Object [1];
      	    params[0] = getGearId();
      	    RepositoryItem[] pollList = mGearParamStatement.executeQuery(gpView, params);

	    if (pollList!=null) {

	     // get item for update
	     MutableRepositoryItem updateGear = mPollRepository.getItemForUpdate(pollList[0].getRepositoryId(),"gearPoll");
	     updateGear.setPropertyValue("poll", targetPoll);
	     mPollRepository.updateItem(updateGear);
	     
	    } else {
     	       MutableRepositoryItem newGearPoll;
               newGearPoll = mPollRepository.createItem("gearPoll");
               newGearPoll.setPropertyValue("gear", getGearId());
               newGearPoll.setPropertyValue("poll", targetPoll);
               mPollRepository.addItem(newGearPoll);
	    }

     } 
     catch (atg.repository.RepositoryException e) {
	if(isLoggingError()) {
	  logError(sResourceBundle.getString("make-default-error"));
	}
	addFormException( new DropletException(sResourceBundle.getString("make-default-error")));
          setRollbackTransaction(true);     
     }
   return;
  }


  /**
 * handle "add" form submission for new poll.
 *
 */

  public boolean handleAdd(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
    {
        if(getPollRepository() == null) {
         if (isLoggingError()) {
           logError(sResourceBundle.getString("null-repository-msg"));
	 }
	 return false;
        }

	insertPoll();

	return checkFormRedirect (getSuccessUrl() , getFailureUrl(), pRequest, pResponse);

    }

/**
 * handle "update" form submission for a poll.
 *
 */

  public boolean handleUpdate(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
    {
        if(getPollRepository() == null) {
         if (isLoggingError()) {
           logError(sResourceBundle.getString("null-repository-msg"));
	 }
	 return false;
        }
        
     if((getPollTitle()== null || getPollTitle().trim().length()==0) ||
           (getQuestionText() == null || getQuestionText().trim().length()==0)){
        addFormException( new DropletException(sResourceBundle.getString("blank-fields-error"))); 
        setRollbackTransaction(true);            
    }        
    if (!checkFormRedirect(null, getFailureUrl(), pRequest, pResponse))
        return false;
            

	if (isLoggingDebug()) {
	    logDebug("in PollFormHandler:handleUpdate() pollId:" + getPollId());
	}

	try {
	   MutableRepositoryItem poll = mPollRepository.getItemForUpdate(getPollId(),"poll");
           poll.setPropertyValue("title", getPollTitle());
           poll.setPropertyValue("questionText", getQuestionText());
	   mPollRepository.updateItem(poll);

        } 
        catch (atg.repository.RepositoryException e) {
	  if (isLoggingError()) {
           logError("PollFormHandler:handleUpdate - Unable to update poll item: " + getPollId(), e);
	  }
           addFormException( new DropletException(sResourceBundle.getString("admin-repository-error")));
           setRollbackTransaction(true);     
        }
    
    if (!checkFormRedirect(null, getFailureUrl(), pRequest, pResponse))
        return false;        

        // insert set of responses
        insertResponses();
        
   if (!checkFormRedirect(null, getFailureUrl(), pRequest, pResponse))
        return false;         
   
        if (getMakeDefaultPoll()) {
     	     if (isLoggingDebug()) {
	         logDebug("Setting poll " + getPollId() + " as default for gear " + getGearId());
	     }
           setGearPoll();
        }
  mResponses = new String[MAX_RESPONSES];;
	return checkFormRedirect (getSuccessUrl() , getFailureUrl(), pRequest, pResponse);

    }

/**
 * handle "addMore" form submission for new poll, which does nothing at this point
 *
 */

  public boolean handleAddMore(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
    {
	if (isLoggingDebug()) {
	    logDebug("in PollFormHandler:handleAddMore()");
	}

	return checkFormRedirect (getMoreUrl() , getMoreUrl(), pRequest, pResponse);

    }
} // end of class
