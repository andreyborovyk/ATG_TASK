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

import atg.portal.nucleus.NucleusComponents;
import atg.portal.gear.discussion.ForumMessage;
import atg.portal.gear.discussion.NewForumMessage;
import atg.portal.alert.GearMessagePublisher;
import atg.portal.framework.*;
import atg.repository.*;
import atg.repository.rql.RqlStatement;
import atg.dtm.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.transaction.*;

/**
 * 
 * The AddGearForumTag will create a new forum record (messageBoard repositoryItem), and 
 * an associated gearBoard item, which relates the forum to a specific gear instance.
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/AddGearForumTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class AddGearForumTag  extends TagSupport
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/AddGearForumTag.java#2 $$Change: 651448 $";

    final static String MESSAGE_PUBLISHER = "dynamo:/atg/portal/alert/GearMessagePublisher";

  protected static RqlStatement mGearForumStatement;
  static {
    try {
      mGearForumStatement = 
	RqlStatement.parseRqlStatement("gear = ?0 AND messageBoard = ?1");
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
         }
     } catch( Exception e ) {
       pageContext.getServletContext().log("AddGearForum tag - exception sending message: ",e);
     }


    }

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
	pageContext.getServletContext().log("AddGearForumTag - unable to get transaction mgr:", e);
   }

   try { 
     try { 

	 td.begin (tm);

         boolean bForumInGear=false;

         // Check if forum already related to this gear
         RepositoryView gearView = getDiscussionRepository().getView("gearBoards");
         Object [] qryParams = new Object [2];
         qryParams[0] = getGearId();
         qryParams[1] = getForumId();

         int gearForumCount=mGearForumStatement.executeCountQuery(gearView, qryParams);
         if (gearForumCount > 0 ) {
           bForumInGear=true;
           mSuccess=false;
	   setErrorMessage("already-associated-with-gear");
         }

	 if (!bForumInGear) {
            RepositoryItem targetForum = mDiscussionRepository.getItem(getForumId(),"messageBoard");

     	    MutableRepositoryItem newGearBoard;
            newGearBoard = mDiscussionRepository.createItem("gearBoards");
            newGearBoard.setPropertyValue("gear", getGearId());
            newGearBoard.setPropertyValue("messageBoard", targetForum);
            mDiscussionRepository.addItem(newGearBoard);

            //
            // Create the New Forum message, set its properties, and send it.
            //
            NewForumMessage message = new NewForumMessage( getEnv() );
            message.setForumId(getForumId());
            message.setForumName( (String) targetForum.getPropertyValue("name"));
            sendMessage(message);
	 }
     } 
     catch (atg.repository.DuplicateIdException de) {
        mSuccess=false;
	setErrorMessage("already-associated-with-gear");
     }
     catch (atg.repository.RepositoryException e) {
        pageContext.getServletContext().log(("AddGearForum - Unable to add gearBoard: id is " + e.getId() + "itemdescriptor is " + e.getItemDescriptorName()), e);
        mSuccess=false;
	setErrorMessage("unable-to-add-board");
     }
     finally {
        td.end ();
     }
   } 
   catch (TransactionDemarcationException e) {
      pageContext.getServletContext().log("AddGearForumTag - transaction error...",e);
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
	    throw new JspException("AddGearForumTag - could not open repository");
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
	setForumId(null);
	setErrorMessage(null);
	setSuccess(false);
    }

} // end of class
