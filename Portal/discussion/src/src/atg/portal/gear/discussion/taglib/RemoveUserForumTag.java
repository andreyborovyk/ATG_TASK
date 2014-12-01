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
 * The RemoveUserForumTag will delete the user/gear/forum relationship record (userGearBoard item)
 * Used by the userConfig page to remove forums from a user-customized gear.
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/RemoveUserForumTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class RemoveUserForumTag  extends TagSupport
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/RemoveUserForumTag.java#2 $$Change: 651448 $";


  protected static RqlStatement mUserBoardsStatement;
  static {
    try {
      mUserBoardsStatement = 
	RqlStatement.parseRqlStatement("messageBoard = ?0 AND user = ?1 AND gear = ?2");
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
    // property: userId
    private String mUserId;

    public void setUserId(String pUserId) 
	{ mUserId = pUserId; }

    public String getUserId() 
	{ return mUserId; }

    //----------------------------------------
    // property: gearId
    private String mGearId;

    public void setGearId(String pGearId) 
	{ mGearId = pGearId; }

    public String getGearId() 
	{ return mGearId; }


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

   protected boolean deleteUserForum() {

   boolean retval=true;

   // perform JNDI lookup
   TransactionManager tm=null;
   TransactionDemarcation td = new TransactionDemarcation ();

   try {
      tm  = (TransactionManager) NucleusComponents.lookup("dynamo:/atg/dynamo/transaction/TransactionManager");
   }
   catch (javax.naming.NamingException e) {
	pageContext.getServletContext().log("RemoveUserForumTag - unable to get transaction mgr:", e);
	return false;
   }

   try { 
     try { 

	 td.begin (tm);

	 // delete the user/gear/forum relationship record (userGearBoard item)
           try {
      		RepositoryView cfView = getDiscussionRepository().getView("userGearBoards");
  		Object [] params = new Object [3];
  		params[0] = getForumId();
  		params[1] = getUserId();
  		params[2] = getGearId();
    		RepositoryItem []userBoard = mUserBoardsStatement.executeQuery(cfView, params);

  		if (userBoard !=null) {
     		      mDiscussionRepository.removeItem(userBoard[0].getRepositoryId(),"userGearBoards");
     		}
	   }
           catch (atg.repository.RepositoryException e) {
             pageContext.getServletContext().log("RemoveUserForum - error removing gearBoard items", e);
		retval=false;
           }

     } 
     catch (Exception e) {
         pageContext.getServletContext().log("RemoveUserForum - Repository error:", e);
	 retval=false;
     }
     finally {
        td.end ();
     }
   } 
   catch (TransactionDemarcationException e) {
      pageContext.getServletContext().log("RemoveUserForumTag - transaction error...",e);
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
	    throw new JspException("RemoveUserForumTag - could not open repository");
        }
           
	if (deleteUserForum()) {
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
	setGearId(null);
	setUserId(null);
	setSuccess(false);
    }

} // end of class
