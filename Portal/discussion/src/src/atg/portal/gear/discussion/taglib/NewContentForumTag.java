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
import atg.dtm.*;
import atg.userprofiling.Profile;
import atg.portal.nucleus.NucleusComponents;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.transaction.*;

/**
 * 
 * The NewContentForumTag is used to create a new
 * forum record, then update a specific item in another 
 * repository with the ID of the new forum. This is to be used 
 * initially for the document exchange gear, but could potentially
 * be used to relate a discussion board to any other content 
 * item (hence the name).
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/NewContentForumTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class NewContentForumTag  extends TagSupport
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/NewContentForumTag.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // property: forumName
    private String mForumName;

    public void setForumName(String pForumName) 
	{ mForumName = pForumName; }

    public String getForumName() 
	{ return mForumName; }


    //----------------------------------------
    // property: user
    private Profile mUser;

    public void setUser(Profile pUser) 
	{ mUser = pUser; }

    public Profile getUser() 
	{ return mUser; }


    //----------------------------------------
    // property: forumType
    private String mForumType;

    public void setForumType(String pForumType) 
	{ mForumType = pForumType; }

    public String getForumType() 
	{ return mForumType; }


    //----------------------------------------
    // property: gearId
    private String mGearId;

    public void setGearId(String pGearId) 
	{ mGearId = pGearId; }

    public String getGearId() 
	{ return mGearId; }

    //----------------------------------------
    // property: contentReposPath
    private String mContentReposPath;

    public void setContentReposPath(String pContentReposPath) 
	{ mContentReposPath = pContentReposPath; }

    public String getContentReposPath() 
	{ return mContentReposPath; }


    //----------------------------------------
    // property: itemDescriptorName
    private String mItemDescriptorName;

    public void setItemDescriptorName(String pItemDescriptorName) 
	{ mItemDescriptorName = pItemDescriptorName; }

    public String getItemDescriptorName() 
	{ return mItemDescriptorName; }


    //----------------------------------------
    // property: idPropertyName
    private String mIdPropertyName;

    public void setIdPropertyName(String pIdPropertyName) 
	{ mIdPropertyName = pIdPropertyName; }

    public String getIdPropertyName() 
	{ return mIdPropertyName; }


    //----------------------------------------
    // property: contentId
    private String mContentId;

    public void setContentId(String pContentId) 
	{ mContentId = pContentId; }

    public String getContentId() 
	{ return mContentId; }


    //----------------------------------------
    // property: newForumId
    private String mNewForumId;

    public void setNewForumId(String pNewForumId) 
	{ mNewForumId = pNewForumId; }

    public String getNewForumId() 
	{ return mNewForumId; }






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

  //-------------------------------------
  /**
   * property: contentRepository
   */
  protected MutableRepository mContentRepository;

  protected void initContentRepository()
  {
    // perform JNDI lookup
    try {
      javax.naming.Context ctx = new javax.naming.InitialContext();
      mContentRepository = (MutableRepository) ctx.lookup(getContentReposPath());
    }
    catch (javax.naming.NamingException e) {
      pageContext.getServletContext().log(" Discussion Gear: Unable to get content repository");

    }
  }

  protected MutableRepository getContentRepository()
  {
    return mContentRepository;
  }

  public void setContentRepository(MutableRepository pContentRepository) {
    mContentRepository = pContentRepository;
  }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

   protected boolean insertForum() {

   boolean retval=true;
   MutableRepositoryItem newForum=null;

   // perform JNDI lookup
   TransactionManager tm=null;
   TransactionDemarcation td = new TransactionDemarcation ();

   try {
      tm  = (TransactionManager) NucleusComponents.lookup("dynamo:/atg/dynamo/transaction/TransactionManager");
   }
   catch (javax.naming.NamingException e) {
	pageContext.getServletContext().log("NewContentForumTag - unable to get transaction mgr:", e);
	retval= false;
   }

   try { 
     try { 

	 td.begin (tm);

         try {

             newForum = mDiscussionRepository.createItem("messageBoard");
	    
	     //truncate the forumName to 32 chars to make sure it fits
	     //into all databases
	     String forumName = getForumName();
	     int len = forumName.length();
	     if (len > 32)
		 forumName = forumName.substring(0, 32);

             newForum.setPropertyValue("name", forumName);
             newForum.setPropertyValue("description", "content related board");
             newForum.setPropertyValue("boardType", "document");
             newForum.setPropertyValue("owner", getUser().getDataSource());

	     mDiscussionRepository.addItem(newForum);
	     setNewForumId(newForum.getRepositoryId());

	    
         } 
         catch (atg.repository.RepositoryException e) {
             pageContext.getServletContext().log(("NewContentForum - Unable to add messageBoard: id is " + e.getId() + "itemdescriptor is " + e.getItemDescriptorName()), e);
	     System.out.println(e);
	     retval= false;
         }

	 // update content item with new ID
	 // System.out.println("NewContentForumTag - updating content item: ");
	 // System.out.println("repository: " + getContentReposPath());
	 // System.out.println("itemDescriptor: " + getItemDescriptorName());
	 // System.out.println("contentID: " + getContentId());
	 // System.out.println("property: " + getIdPropertyName());
	 // System.out.println("new forum ID: " + newForum.getRepositoryId());

	 if ( getContentRepository() != null ) {
           try {
	      MutableRepositoryItem contentItem = mContentRepository.getItemForUpdate(getContentId(),getItemDescriptorName());
	       contentItem.setPropertyValue(getIdPropertyName(),newForum.getRepositoryId());
	        mContentRepository.updateItem(contentItem);
	   }
           catch (atg.repository.RepositoryException e) {
             pageContext.getServletContext().log("NewContentForum - Unable to update content item forum ID", e);
	     retval= false;
           }
	 }

     } 
     catch (Exception e) {
         pageContext.getServletContext().log("NewContentForum - Repository error:", e);
	 retval= false;
     }
     finally {
        td.end ();
     }
   } 
   catch (TransactionDemarcationException e) {
      pageContext.getServletContext().log("NewContentForumTag - transaction error...",e);
      retval= false;
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
	    throw new JspException("NewContentForumTag - could not open discussion repository");
        }
           
	// get the content repository
        initContentRepository();

        if(getContentRepository() == null) {
	    throw new JspException("NewContentForumTag - could not open content repository");
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
	setForumName(null);
	setUser(null);
	setForumType(null);
	setGearId(null);
	setContentReposPath(null);
	setItemDescriptorName(null);
	setIdPropertyName(null);
	setContentId(null);
	setSuccess(false);
    }

} // end of class
