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
 * The ReAddForumTag is a tag extension which will reset the deleteFlag (to "0") on a messageBoard item
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/ReAddForumTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ReAddForumTag  extends TagSupport
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/ReAddForumTag.java#2 $$Change: 651448 $";

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

   protected void deleteForum() {


   // perform JNDI lookup
   TransactionManager tm=null;
   TransactionDemarcation td = new TransactionDemarcation ();

   try {
      tm  = (TransactionManager) NucleusComponents.lookup("dynamo:/atg/dynamo/transaction/TransactionManager");
   }
   catch (javax.naming.NamingException e) {
	pageContext.getServletContext().log("ReAddForumTag - unable to get transaction mgr:", e);
   }

   try { 
     try { 

	 td.begin (tm);

         try {

             MutableRepositoryItem forum = mDiscussionRepository.getItemForUpdate(getForumId(),"messageBoard");
             forum.setPropertyValue("deleteFlag", new Integer(0));
             mDiscussionRepository.updateItem(forum);
         } 
         catch (atg.repository.RepositoryException e) {
             pageContext.getServletContext().log("ReAddForum - Unable to reset forum.", e);
         }


     } 
     catch (Exception e) {
         pageContext.getServletContext().log("ReAddForum - Repository error:", e);
     }
     finally {
        td.end ();
     }
   } 
   catch (TransactionDemarcationException e) {
      pageContext.getServletContext().log("ReAddForumTag - transaction error...",e);
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
	    throw new JspException("ReAddForumTag - could not open repository");
        }
           
	deleteForum();
        setSuccess(true);

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
	setSuccess(false);
    }

} // end of class
