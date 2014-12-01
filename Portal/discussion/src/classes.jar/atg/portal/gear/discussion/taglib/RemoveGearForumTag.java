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
 * The RemoveGearForumTag is used to delete a single gear/forum association (gearBoards item)
 * NOTE:  This has been changed to just set the "delete_flag" to 1 if this is a "private" forum,
 * and delete the gearBoard relationship only if it is a public forum. - jb
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/RemoveGearForumTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class RemoveGearForumTag  extends TagSupport
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/RemoveGearForumTag.java#2 $$Change: 651448 $";


  protected static RqlStatement mGearBoardsStatement;
  protected static RqlStatement mGearListsStatement;

  static {
    try {
      mGearBoardsStatement = RqlStatement.parseRqlStatement("messageBoard = ?0 AND gear = ?1");
      mGearListsStatement = RqlStatement.parseRqlStatement("messageBoard = ?0 ");
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

   protected boolean removeGearForum() {

   boolean retval=true;

   // perform JNDI lookup
   TransactionManager tm=null;
   TransactionDemarcation td = new TransactionDemarcation ();

   try {
      tm  = (TransactionManager) NucleusComponents.lookup("dynamo:/atg/dynamo/transaction/TransactionManager");
   }
   catch (javax.naming.NamingException e) {
	pageContext.getServletContext().log("RemoveGearForumTag - unable to get transaction mgr:", e);
	return false;
   }

   try { 
     try { 

	 td.begin (tm);

	 // delete gear/forum relationship record (gearBoard item) if this is a public forum,
	 // otherwise, just set the deleteFlag to "1"
           try {
      		RepositoryView gbView = getDiscussionRepository().getView("gearBoards");
  		Object [] params = new Object [2];
  		params[0] = getForumId();
  		params[1] = getGearId();
    		RepositoryItem []gearBoard = mGearBoardsStatement.executeQuery(gbView, params);

  		if (gearBoard !=null) {
	           RepositoryItem thisBoard = (RepositoryItem) gearBoard[0].getPropertyValue("messageBoard");
                   if ( "public".equals( (String) thisBoard.getPropertyValue("boardType"))) {
     		        mDiscussionRepository.removeItem(gearBoard[0].getRepositoryId(),"gearBoards");
	           } else {
             		MutableRepositoryItem deleteBoard = mDiscussionRepository.getItemForUpdate(thisBoard.getRepositoryId(),"messageBoard");
             		deleteBoard.setPropertyValue("deleteFlag", new Integer(1));
			mDiscussionRepository.updateItem(deleteBoard);
		   }
  
                   // now delete any user/gear/forum relationship records (userGearBoard item)
                   RepositoryView ubView = getDiscussionRepository().getView("userGearBoards");
                   Object [] usrParams = new Object [1];
                   usrParams[0] = thisBoard.getRepositoryId();
                   RepositoryItem []gearList = mGearListsStatement.executeQuery(ubView, usrParams);
                   if (gearList !=null) {
                         for (int i=0; i < gearList.length; i++) {
                         mDiscussionRepository.removeItem(gearList[i].getRepositoryId(),"userGearBoards");
                      }
                   }	
     		}
	   }
           catch (atg.repository.RepositoryException e) {
             pageContext.getServletContext().log("RemoveGearForum - error removing gearBoard items", e);
		retval=false;
           }



     } 
     catch (Exception e) {
         pageContext.getServletContext().log("RemoveGearForum - Repository error:", e);
	 retval=false;
     }
     finally {
        td.end ();
     }
   } 
   catch (TransactionDemarcationException e) {
      pageContext.getServletContext().log("RemoveGearForumTag - transaction error...",e);
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
	    throw new JspException("RemoveGearForumTag - could not open repository");
        }
           
	if (removeGearForum()) {
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
	setSuccess(false);
    }

} // end of class
