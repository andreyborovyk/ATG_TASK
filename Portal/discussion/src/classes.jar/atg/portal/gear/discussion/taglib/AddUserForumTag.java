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


package atg.portal.gear.discussion.taglib;

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
 * The AddUserForum tag is used to associate a discussion forum with a user and gear instance.
 * A record is added to the dtg_user_gear_boards table (userGearBoards item)
 * This is used by the userConfig page, which allows users to select which forums
 * will display in their gear.
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/AddUserForumTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class AddUserForumTag  extends TagSupport
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/AddUserForumTag.java#2 $$Change: 651448 $";

  protected static RqlStatement mGearForumStatement;
  static {
    try {
      mGearForumStatement = 
	RqlStatement.parseRqlStatement("gear = ?0 AND messageBoard = ?1 AND user= ?2");
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
    // property: userId
    private String mUserId;

    public void setUserId(String pUserId) 
	{ mUserId = pUserId; }

    public String getUserId() 
	{ return mUserId; }



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
   * property: discussionRepository
   */
  protected static MutableRepository mDiscussionRepository;

  protected void initDiscussionRepository()
  {
    // perform JNDI lookup
    try {
      mDiscussionRepository = (MutableRepository) NucleusComponents.lookup("dynamo:/atg/portal/gear/discussion/DiscussionRepository");
    }
    catch (javax.naming.NamingException e) {
      pageContext.getServletContext().log(" Discussion Gear: Unable to get discussion repository");

    }
  }

  protected MutableRepository getDiscussionRepository()
  {
    return mDiscussionRepository;
  }

  public void setDiscussionRepository(MutableRepository pDiscussionRepository) {
    mDiscussionRepository = pDiscussionRepository;
  }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

  protected void insertForum() {

   mSuccess=true;


   // perform JNDI lookup
   TransactionManager tm=null;
   TransactionDemarcation td = new TransactionDemarcation ();

   try {
     tm  = (TransactionManager) NucleusComponents.lookup("dynamo:/atg/dynamo/transaction/TransactionManager");
   }
   catch (javax.naming.NamingException e) {
	pageContext.getServletContext().log("AddUserForumTag - unable to get transaction mgr:", e);
   }

   try { 
     try { 

	 td.begin (tm);

         boolean bForumInGear=false;

         // Check if forum already related to this gear
         RepositoryView gearView = getDiscussionRepository().getView("userGearBoards");
         Object [] qryParams = new Object [3];
         qryParams[0] = getGearId();
         qryParams[1] = getForumId();
         qryParams[2] = getUserId();

         int gearForumCount=mGearForumStatement.executeCountQuery(gearView, qryParams);
         if (gearForumCount > 0 ) {
           bForumInGear=true;
           mSuccess=false;
	   setErrorMessage("already-associated-with-gear");
         }

	 if (!bForumInGear) {
            RepositoryItem targetForum = mDiscussionRepository.getItem(getForumId(),"messageBoard");

     	    MutableRepositoryItem newUserGearBoard;
            newUserGearBoard = mDiscussionRepository.createItem("userGearBoards");
            newUserGearBoard.setPropertyValue("gear", getGearId());
            newUserGearBoard.setPropertyValue("user", getUserId());
            newUserGearBoard.setPropertyValue("messageBoard", targetForum);
            mDiscussionRepository.addItem(newUserGearBoard);
         }

     } 
     catch (atg.repository.DuplicateIdException de) {
        mSuccess=false;
	setErrorMessage("already-associated-with-gear");
     }
     catch (atg.repository.RepositoryException e) {
        pageContext.getServletContext().log(("AddUserForum - Unable to add gearBoard: id is " + e.getId() + "itemdescriptor is " + e.getItemDescriptorName()), e);
        mSuccess=false;
	setErrorMessage("unable-to-add-board");
     }
     finally {
        td.end ();
     }
   } 
   catch (TransactionDemarcationException e) {
      pageContext.getServletContext().log("AddUserForumTag - transaction error...",e);
   }

     
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

	// get the discussion repository
	if (mDiscussionRepository==null) {
           initDiscussionRepository();
	}

        if(getDiscussionRepository() == null) {
	    throw new JspException("AddUserForumTag - could not open repository");
        }
           
	insertForum();

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
	setUserId(null);
	setForumId(null);
	setErrorMessage(null);
	setSuccess(false);
    }

} // end of class
