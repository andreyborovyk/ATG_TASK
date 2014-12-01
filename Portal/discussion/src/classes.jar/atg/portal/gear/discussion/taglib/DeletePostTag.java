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
 * The DeletePostTag is used by the forum admin pages to delete individual
 * posts within a forum. No records are actually removed, we just set the "deleteFlag"
 * property to "1".
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/DeletePostTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class DeletePostTag  extends TagSupport
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/DeletePostTag.java#2 $$Change: 651448 $";

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
    // property: messageId
    private String mMessageId;

    public void setMessageId(String pMessageId) 
	{ mMessageId = pMessageId; }

    public String getMessageId() 
	{ return mMessageId; }

    //----------------------------------------
    // property: success
    private  boolean mSuccess;

    public void setSuccess(boolean pSuccess) 
	{ mSuccess = pSuccess; }

    public boolean getSuccess() 
	{ return mSuccess; }

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

   protected boolean deletePost() {


   boolean retval=true;

   // perform JNDI lookup
   TransactionManager tm=null;
   TransactionDemarcation td = new TransactionDemarcation ();

   try {
      tm  = (TransactionManager) NucleusComponents.lookup("dynamo:/atg/dynamo/transaction/TransactionManager");
   }
   catch (javax.naming.NamingException e) {
	pageContext.getServletContext().log("DeletePostTag - unable to get transaction mgr:", e);
   }

   try { 
     try { 

	 td.begin (tm);

            try {

               MutableRepositoryItem message = mDiscussionRepository.getItemForUpdate(getMessageId(),"messageThread");
	
	       // check if flag is set first
	       if (message.getPropertyValue("deleteFlag").toString().equals("1"))
	          return true;

               message.setPropertyValue("deleteFlag", new Integer(1));
               mDiscussionRepository.updateItem(message);

	       // Must update counters on board and ultimate thread (if response)
	       // If deleting a topic, must reduce the count of posts on the board by the number of responses
	       // (childrenQty) plus the topic itself

               if (message.getPropertyValue("topicResponseFlag").toString().equals("response")) {

		   String mtID=((RepositoryItem) message.getPropertyValue("ultimateThread")).getRepositoryId();
                   MutableRepositoryItem masterTopic = mDiscussionRepository.getItemForUpdate(mtID,"messageThread");
		   int numKids = 0;
		   if (masterTopic.getPropertyValue("childrenQty") != null) {
		      numKids= ((Integer) masterTopic.getPropertyValue("childrenQty")).intValue()-1;
		      if (numKids < 0)
		         numKids=0;
		   }
		   masterTopic.setPropertyValue("childrenQty", (new Integer(numKids)));
		   mDiscussionRepository.updateItem(masterTopic);
  	       }

	       // next, the messageBoard item
	       String forumID=((RepositoryItem) message.getPropertyValue("messageBoard")).getRepositoryId();
               MutableRepositoryItem forum = mDiscussionRepository.getItemForUpdate(forumID,"messageBoard");

	       int decrementAmount = 1; // for the topic itself
               if (message.getPropertyValue("topicResponseFlag").toString().equals("topic")) {
	          if (message.getPropertyValue("childrenQty") != null) {
	             decrementAmount += ((Integer) message.getPropertyValue("childrenQty")).intValue();
		  }
	       }
	       int numPosts = ((Integer) forum.getPropertyValue("numPosts")).intValue()-decrementAmount;
	       if (numPosts < 0)  
	          numPosts=0;
	       forum.setPropertyValue("numPosts", (new Integer(numPosts)));
	       mDiscussionRepository.updateItem(forum);

            } 
            catch (atg.repository.RepositoryException e) {
                pageContext.getServletContext().log("DeletePost - Unable to delete message.", e);
            }
     } 
     catch (Exception e) {
         pageContext.getServletContext().log("DeletePost - Repository error:", e);
         retval=false;
     }
     finally {
        td.end ();
     }
   } 
   catch (TransactionDemarcationException e) {
      pageContext.getServletContext().log("DeletePostTag - transaction error...",e);
      retval=false;
   }

   return retval;

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
	    throw new JspException("DeletePostTag - could not open repository");
        }
           
	if (deletePost()) {
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
	setForumId(null);
	setMessageId(null);
	setSuccess(false);
    }

} // end of class
