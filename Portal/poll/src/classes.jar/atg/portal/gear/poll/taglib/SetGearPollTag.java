/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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


package atg.portal.gear.poll.taglib;

import atg.repository.*;
import atg.repository.rql.RqlStatement;
import atg.dtm.*;
import atg.portal.nucleus.NucleusComponents;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.transaction.*;

/**
 * 
 * The SetGearPollTag will create a gear/poll relationship record (gearPoll item).
 * If the keyword "clear" is set as the poll ID, any gearPoll items for the given
 * gear are removed.
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/taglib/SetGearPollTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class SetGearPollTag  extends TagSupport
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/taglib/SetGearPollTag.java#2 $$Change: 651448 $";

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

    //----------------------------------------
    // property: forumId
    private String mForumId;

    public void setForumId(String pForumId) 
	{ mForumId = pForumId; }

    public String getForumId() 
	{ return mForumId; }


    //----------------------------------------
    // property: gearId
    private String mGearId;

    public void setGearId(String pGearId) 
	{ mGearId = pGearId; }

    public String getGearId() 
	{ return mGearId; }


    //----------------------------------------
    // property: pollId
    private String mPollId;

    public void setPollId(String pPollId) 
	{ mPollId = pPollId; }

    public String getPollId() 
	{ return mPollId; }


    //----------------------------------------
    // property: success
    private  boolean mSuccess;

    public void setSuccess(boolean pSuccess) 
	{ mSuccess = pSuccess; }

    public boolean getSuccess() 
	{ return mSuccess; }

    //----------------------------------------
    // property: errorMessage
    private String mErrorMessage;

    public void setErrorMessage(String pErrorMessage) 
	{ mErrorMessage = pErrorMessage; }

    public String getErrorMessage() 
	{ return mErrorMessage; }



  //-------------------------------------
  /**
  //-------------------------------------
  /**
   * property: pollRepository
   */
  protected static MutableRepository mPollRepository;

  protected void initPollRepository()
  {
    // perform JNDI lookup
    try {
      mPollRepository = (MutableRepository) NucleusComponents.lookup("dynamo:/atg/portal/gear/poll/PollRepository");
    }
    catch (javax.naming.NamingException e) {
      pageContext.getServletContext().log(" poll Gear: Unable to get poll repository");

    }
  }

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

  protected boolean setGearPoll() {

   MutableRepositoryItem newForum=null;

   // perform JNDI lookup
   TransactionManager tm=null;
   TransactionDemarcation td = new TransactionDemarcation ();

   try {
      tm  = (TransactionManager) NucleusComponents.lookup("dynamo:/atg/dynamo/transaction/TransactionManager");
   }
   catch (javax.naming.NamingException e) {
	pageContext.getServletContext().log("SetGearPollTag - unable to get transaction mgr:", e);
   }

   try { 
     try { 

	 td.begin (tm);
	    
 	 // check for "clear" selected - if so, remove gearPoll item and get out
	 if (getPollId().equals("clear")) {
      	    RepositoryView gpView = getPollRepository().getView("gearPoll");
      	    Object [] params = new Object [1];
      	    params[0] = getGearId();
      	    RepositoryItem[] pollList = mGearParamStatement.executeQuery(gpView, params);

	    if (pollList!=null) {
                   mPollRepository.removeItem(pollList[0].getRepositoryId(),"gearPoll");
             }
	 } else {
           
      
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
     } 
     catch (atg.repository.RepositoryException e) {
        pageContext.getServletContext().log("SetGearPoll - Unable to add poll" , e);
        mSuccess=false;
	setErrorMessage("Unable to add this poll to gear.");
     }
     finally {
        td.end ();
     }
   } 
   catch (TransactionDemarcationException e) {
      pageContext.getServletContext().log("SetGearPollTag - transaction error...",e);
   }

   return true;
     
  }
    //----------------------------------------
    /**
     * start executing this tag
     * @return EVAL_BODY_INCLUDE so that the body contents gets evaluated once
     * @exception JspException if there was a jsp error
     */
    public int doStartTag()
	throws JspException
    {
	setSuccess(false);

	// get the poll repository
	if (mPollRepository==null) {
          initPollRepository();
        }

        if(getPollRepository() == null) {
	    throw new JspException("SetGearPollTag - could not open repository");
        }
           
	if ( setGearPoll() ) { 
	  setSuccess(true);
	}

	// set this tag as an attribute in its own body
	pageContext.setAttribute(getId(), this);

	return EVAL_BODY_INCLUDE;
    }

    //----------------------------------------
    /**
     * release the tag
     */
    public void release()
    {
	super.release();
	setGearId(null);
	setForumId(null);
	setErrorMessage(null);
	setSuccess(false);
    }

} // end of class
