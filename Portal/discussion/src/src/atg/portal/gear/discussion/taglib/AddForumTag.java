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

import atg.portal.gear.discussion.AddForumBean;
import atg.portal.gear.discussion.ForumMessage;
import atg.portal.gear.discussion.NewForumMessage;
import atg.portal.alert.GearMessagePublisher;
import atg.portal.framework.*;
import atg.portal.nucleus.NucleusComponents;
import atg.repository.*;
import atg.dtm.*;
import atg.userprofiling.Profile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.transaction.*;
import javax.naming.*;

/**
 * 
 * The AddForumTag will create a new forum record (messageBoard repository item)
 * If the gearID property is set, a new gearBoard item will be created to associate
 * the new forum with a particular gear instance.
 * This tag will also post a JMS message (NewForumMessage) when a new forum is created.
 * 
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/AddForumTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class AddForumTag  extends TagSupport
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/AddForumTag.java#2 $$Change: 651448 $";

    final static String MESSAGE_PUBLISHER = "dynamo:/atg/portal/alert/GearMessagePublisher";

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // property: newForum
    private AddForumBean mNewForum;

    public void setNewForum(AddForumBean pNewForum) 
	{ mNewForum = pNewForum; }

    public AddForumBean getNewForum() 
	{ return mNewForum; }


    //----------------------------------------
    // property: user
    private Profile mUser;

    public void setUser(Profile pUser) 
	{ mUser = pUser; }

    public Profile getUser() 
	{ return mUser; }


    //----------------------------------------
    // property: gear
    private String mGearId;

    public void setGearId(String pGearId) 
	{ mGearId = pGearId; }

    public String getGearId() 
	{ return mGearId; }


    //----------------------------------------
    // property: env
    private GearEnvironment mEnv;

    public void setEnv(GearEnvironment pEnv)
     { mEnv = pEnv; }

    public GearEnvironment getEnv()
     { return mEnv; }

    //----------------------------------------
    // property: success
    private  boolean mSuccess;

    public void setSuccess(boolean pSuccess) 
	{ mSuccess = pSuccess; }

    public boolean getSuccess() 
	{ return mSuccess; }


    //----------------------------------------
    // property: forumId
    private String mForumId;

    public void setForumId(String pForumId) 
	{ mForumId = pForumId; }

    public String getForumId() 
	{ return mForumId; }

    //----------------------------------------
    // property: errorMsg
    private String mErrorMsg;

    public void setErrorMsg(String pErrorMsg) 
	{ mErrorMsg = pErrorMsg; }

    public String getErrorMsg() 
	{ return mErrorMsg; }

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

   //-------------------------------------
   // property: publisher
   private static GearMessagePublisher mPublisher;

   private void setPublisher(GearMessagePublisher pPublisher) {
     mPublisher = pPublisher;
   }
   private GearMessagePublisher getPublisher()
     throws javax.naming.NamingException
   {
     if (mPublisher == null)
     {
         mPublisher = (GearMessagePublisher) NucleusComponents.lookup(MESSAGE_PUBLISHER);
     }
     return mPublisher;
   }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

   public void sendMessage(ForumMessage pMessage)
   {
     try {

         // lookup the publisher service
         GearMessagePublisher publisher = getPublisher();

         if (publisher != null)
         {
           publisher.writeMessage(pMessage);
 // System.out.println("AddForum tag - new forum message written");
 // System.out.println("AddForum tag - community id:" + pMessage.getCommunityId());
 //  System.out.println("AddForum tag - forum Id:" + pMessage.getForumId());
 // System.out.println("AddForum tag - forum Name:" + pMessage.getForumName());
         }
     } catch( Exception e ) {
       pageContext.getServletContext().log("PollHandler tag - exception sending message: ",e);
     }


    }

   //----------------------------------------
   protected  boolean insertForum() {

   MutableRepositoryItem newForum=null;

   // check required name field
   if (getNewForum().getForumName()==null) {
	     setErrorMsg("forum-name-required-message");
	     return false;
   }

   // perform JNDI lookup
   TransactionManager tm=null;
   TransactionDemarcation td = new TransactionDemarcation ();

   try {
      tm  = (TransactionManager) NucleusComponents.lookup("dynamo:/atg/dynamo/transaction/TransactionManager");
   }
   catch (javax.naming.NamingException e) {
	pageContext.getServletContext().log("AddForumTag - unable to get transaction mgr:", e);
	setErrorMsg("database-error-msg");
	return false;
   }

   try { 
     try { 

	 td.begin (tm);

         try {
             newForum = mDiscussionRepository.createItem("messageBoard");
             newForum.setPropertyValue("name", getNewForum().getForumName());
             newForum.setPropertyValue("description", getNewForum().getForumDescription());
             newForum.setPropertyValue("boardType", getNewForum().getForumType());
             newForum.setPropertyValue("owner", getUser().getDataSource());
             mDiscussionRepository.addItem(newForum);

            // set property with ID of new Forum
            setForumId(newForum.getRepositoryId());
         } 
         catch (atg.repository.RepositoryException e) {
             pageContext.getServletContext().log(("AddForum - Unable to add messageBoard: id is " + e.getId() + "itemdescriptor is " + e.getItemDescriptorName()), e);
	     setErrorMsg("database-error-msg");
	     return false;
         }

	 // if gear ID is set, insert into dtg_gear_boards table 

	 if ( getGearId() != null ) {
           try {
     	     MutableRepositoryItem newGearBoard;
             newGearBoard = mDiscussionRepository.createItem("gearBoards");
             newGearBoard.setPropertyValue("gear", getGearId());
             newGearBoard.setPropertyValue("messageBoard", newForum);
             mDiscussionRepository.addItem(newGearBoard);
	   }
           catch (atg.repository.RepositoryException e) {
             pageContext.getServletContext().log(("AddForum - Unable to add gearBoard: id is " + e.getId() + "itemdescriptor is " + e.getItemDescriptorName()), e);
	     setErrorMsg("database-error-msg");
	     return false;
           }
	 }

         //
         // Create the New Forum message, set its properties, and send it.
         //
         NewForumMessage message = new NewForumMessage( getEnv() );
         message.setForumId(getForumId());
         message.setForumName(getNewForum().getForumName());
         sendMessage(message);

     } 
     catch (Exception e) {
         pageContext.getServletContext().log("AddForum - Repository error:", e);
	     setErrorMsg("database-error-msg");
	     return false;
     }
     finally {
        td.end ();
     }
   } 
   catch (TransactionDemarcationException e) {
      pageContext.getServletContext().log("AddForumTag - transaction error...",e);
	     setErrorMsg("database-error-msg");
	     return false;
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
	// if the meeting property hasn't been set, throw an exception
	if(getNewForum() == null)
	    throw new JspException("AddForumTag - no AddForumBean set");

	

	// get the discussion repository
	if (mDiscussionRepository==null) {
           initDiscussionRepository();
	}

        if(getDiscussionRepository() == null) {
	    throw new JspException("AddForumTag - could not open repository");
        }
           
	if (insertForum()) {
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
	setNewForum(null);
	setUser(null);
	setSuccess(false);
    }

} // end of class
