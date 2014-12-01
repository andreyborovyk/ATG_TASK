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

package atg.portal.gear.discussion;

import atg.repository.*;
import atg.dtm.*;
import atg.userprofiling.Profile;
import atg.portal.nucleus.NucleusComponents;


import atg.droplet.GenericFormHandler;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.portal.framework.*;
import atg.repository.*;
import atg.repository.rql.RqlStatement;
import atg.dtm.*;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;
import atg.userdirectory.*;
import atg.portal.nucleus.NucleusComponents;
import atg.nucleus.naming.ParameterName;

import java.util.Locale;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.transaction.*;


/**
 * This is the form-handler for posting new discussion board entries
 *
 * This form handler now supports creating a new forum. This is a feature
 * that is used by docexch for posting messages related to documents before
 * a forum is actually created.  In this case, the forum is created, the related
 * content repository item (identified via parameters) is updated with the new ID,
 * then the post will be added.
 * 
 * @beaninfo
 *   description: A form handler used to create a new discussion entry
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory Portal
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/AddPostFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 *
 * @see GenericFormHandler
 *
 */
public class AddPostFormHandler extends GenericFormHandler
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/AddPostFormHandler.java#2 $$Change: 651448 $";

    /** Name of the locale parameter */
    static final ParameterName LOCALE_PARAM = ParameterName.getParameterName("locale");


    //----------------------------------------
    // Properties
    //----------------------------------------


  //---------------------------------------------------------------------------
  // property: subject
  String mSubject;

  public void setSubject(String pSubject) {
    mSubject = pSubject;
  }

  public String getSubject() {
    return mSubject;
  }

  //---------------------------------------------------------------------------
  // property: message
  String mMessage;

  public void setMessage(String pMessage) {
    mMessage = pMessage;
  }

  public String getMessage() {
    return mMessage;
  }

  //---------------------------------------------------------------------------
  // property: forumID
  String mForumID;

  public void setForumID(String pForumID) {
    mForumID = pForumID;
  }

  public String getForumID() {
    return mForumID;
  }

  //---------------------------------------------------------------------------
  // property: parentID
  String mParentID;

  public void setParentID(String pParentID) {
    mParentID = pParentID;
  }

  public String getParentID() {
    return mParentID;
  }

  //---------------------------------------------------------------------------
  // property: ultimateID
  String mUltimateID;

  public void setUltimateID(String pUltimateID) {
    mUltimateID = pUltimateID;
  }

  public String getUltimateID() {
    return mUltimateID;
  }

  //---------------------------------------------------------------------------
  // property: trFlag
  String mTrFlag;

  public void setTrFlag(String pTrFlag) {
    mTrFlag = pTrFlag;
  }

  public String getTrFlag() {
    return mTrFlag;
  }

  //---------------------------------------------------------------------------
  // property: userID
  String mUserID;

  public void setUserID(String pUserID) {
    mUserID = pUserID;
  }

  public String getUserID() {
    return mUserID;
  }

    //----------------------------------------
    // property: profile
    private Profile mProfile;

    public void setProfile(Profile pProfile) 
	{ mProfile = pProfile; }

    public Profile getProfile() 
	{ return mProfile; }


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
    // property: success
    private  boolean mSuccess;

    public void setSuccess(boolean pSuccess) 
	{ mSuccess = pSuccess; }

    public boolean getSuccess() 
	{ return mSuccess; }


    // These properties are used to create a new forum upon posting
    // formerly handled by the NewContentForum tag, used by docexch

    //----------------------------------------
    // property: createRelatedContentForum
    private  boolean mCreateRelatedContentForum;

    public void setCreateRelatedContentForum(boolean pCreateRelatedContentForum) 
	{ mCreateRelatedContentForum = pCreateRelatedContentForum; }

    public boolean getCreateRelatedContentForum() 
	{ return mCreateRelatedContentForum; }

    //----------------------------------------
    // property: forumName
    private String mForumName;

    public void setForumName(String pForumName) 
	{ mForumName = pForumName; }

    public String getForumName() 
	{ return mForumName; }


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
   * property: discussionRepository
   */
  protected static MutableRepository mDiscussionRepository;

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
      logError(" Discussion Gear: Unable to get content repository");

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

   protected boolean createForum() {

   boolean retval=true;
   MutableRepositoryItem newForum=null;

   // perform JNDI lookup
   TransactionDemarcation td = new TransactionDemarcation ();
   try { 
     try { 

	 td.begin (getTransactionManager());

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
             newForum.setPropertyValue("owner", getProfile().getDataSource());

	     mDiscussionRepository.addItem(newForum);
	     setNewForumId(newForum.getRepositoryId());
	     setForumID(newForum.getRepositoryId());

	    
         } 
         catch (atg.repository.RepositoryException e) {
             logError(("NewContentForum - Unable to add messageBoard: id is " + e.getId() + "itemdescriptor is " + e.getItemDescriptorName()), e);
      	     addFormException( new DropletException("generic-error"));
	     retval= false;
         }

	 // update content item with new ID
	 // System.out.println("AddPostFormHandler - updating content item: ");
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
             logError("NewContentForum - Unable to update content item forum ID", e);
             addFormException( new DropletException("generic-error"));
	     retval= false;
           }
	 } 

     } 
     catch (Exception e) {
         logError("NewContentForum - Repository error:", e);
         addFormException( new DropletException("generic-error"));
	 retval= false;
     }
     finally {
        td.end ();
     }
   } 
   catch (TransactionDemarcationException e) {
      logError("NewContentForumTag - transaction error...",e);
      addFormException( new DropletException("generic-error"));
      retval= false;
   }

   return retval;

   }

   protected boolean insertPost() {
     MutableRepositoryItem newPostMessage;

   boolean retval=true;

   // Subject required if this is a new topic, message text required if it's a response
   if ( "R".equals(getTrFlag()) ) {
      if (getMessage() == null ) { 
	 addFormException( new DropletException("reply-required"));
         return false;
      } else if (getMessage().trim().length()<=0) {
	 addFormException( new DropletException("reply-required"));
         return false;
      }
   } else {
      if (getSubject() == null ) { 
	 addFormException( new DropletException("subject-required"));
         return false;
      } else if (getSubject().trim().length()<=0) {
	 addFormException( new DropletException("subject-required"));
         return false;
      }
   }

   TransactionDemarcation td = new TransactionDemarcation ();

   try { 
     try { 

	 td.begin (getTransactionManager());

	 if(getUltimateID() == null)
	   setUltimateID("null");
	 if(getParentID() == null)
	   setParentID("null");



         MutableRepositoryItem masterTopic = null;
         if(!getUltimateID().equals("null")) {
           masterTopic = mDiscussionRepository.getItemForUpdate(getUltimateID(),"messageThread");
         }

         MutableRepositoryItem forum = mDiscussionRepository.getItemForUpdate(getForumID(),"messageBoard");

         RepositoryItem parent = null;
         if(!getParentID().equals("null")) {
            parent = mDiscussionRepository.getItem(getParentID(),"messageThread");
         }

         newPostMessage = mDiscussionRepository.createItem("messageThread");
         newPostMessage.setPropertyValue("subject", cleanseText(getSubject()));
         if (getMessage() != null) { 
           newPostMessage.setPropertyValue("content", cleanseText(getMessage()));
	 }
         newPostMessage.setPropertyValue("messageBoard", forum);
         newPostMessage.setPropertyValue("parentThread", parent);
         newPostMessage.setPropertyValue("ultimateThread", masterTopic);
         if ( "R".equals(getTrFlag()) ) {
            newPostMessage.setPropertyValue("topicResponseFlag", "response");
	 } else {
            newPostMessage.setPropertyValue("topicResponseFlag", "topic");
	 }
         newPostMessage.setPropertyValue("user", getProfile().getDataSource());
         mDiscussionRepository.addItem(newPostMessage);

	 // update the number of kids on the master topic (ulitmate thread id)
         if ( "R".equals(getTrFlag()) ) {
	    int numKids = 1;
            if (masterTopic.getPropertyValue("childrenQty") != null) {
	       numKids= ((Integer) masterTopic.getPropertyValue("childrenQty")).intValue()+1;	
	    }
	    masterTopic.setPropertyValue("childrenQty", (new Integer(numKids)));
	    mDiscussionRepository.updateItem(masterTopic);
	 }
	 // update the last_post_time and number of posts on the forum record
         int numPosts = ((Integer) forum.getPropertyValue("numPosts")).intValue()+1;
	 forum.setPropertyValue("numPosts", (new Integer(numPosts)));
	 forum.setPropertyValue("lastPostTime", newPostMessage.getPropertyValue("creationDate"));
	 mDiscussionRepository.updateItem(forum);
     } 
     catch (atg.repository.RepositoryException e) {
      if (isLoggingError()) {
         logError(("Unable to add repository element. id is " + e.getId() + "itemdescriptor is " + e.getItemDescriptorName()), e);
      }
	 retval=false;
	 addFormException( new DropletException("generic-error"));
     }
     finally {
        td.end ();
     }
   } 
   catch (TransactionDemarcationException e) {
    if (isLoggingError()) {
      logError("AddPostTag - transaction error...",e);
    }
      retval=false;
      addFormException( new DropletException("generic-error"));
   }


    return retval; 
    

   }
  /**
   *  This function strips html tags from the message. Any test between an open brace "<" and   *  a corresponding close brace ">" will be stripped, inclusive of the braces themselves.
   **/
  private String cleanseText(String inputStr)
  {
   StringBuffer strBuf=new StringBuffer(inputStr.length());
   int bufIdx=0;
   int numOpenTag=0;
   boolean copyChars=true;
   boolean skipThisChar=false;

   for (int i=0;i<inputStr.length();i++) {

      skipThisChar=false;

      if (inputStr.charAt(i) == '<') {
        numOpenTag++;
        copyChars=false;
      }

      if (inputStr.charAt(i) == '>') {
        numOpenTag--;
        if (numOpenTag <=0) {
           copyChars=true;
           numOpenTag=0;
           skipThisChar=true;
        }
      }

      if (!skipThisChar && copyChars) {
        strBuf.append(inputStr.charAt(i));
      }

   }

   // replace all newlines with break tags to maintain paragraph spacing
   String tmpString = strBuf.toString();
   StringBuffer newString=new StringBuffer(tmpString.length());
   String breakTag="<BR>";

   int idx=-1;

   idx=tmpString.indexOf('\n');
   if (idx>-1) {
     newString.append(tmpString.substring(0,idx));
     newString.append(breakTag);
   } else {
     newString.append(tmpString);
   }

   int lastIdx;
   while (idx>0 ) {
     lastIdx=idx;
     idx=tmpString.indexOf('\n',idx+1);
     if (idx>-1) {
          newString.append(tmpString.substring(lastIdx+1,idx));
          newString.append(breakTag);
     } else {
          newString.append(tmpString.substring(lastIdx+1));
     }
   }

   return newString.toString();
  }



    //----------------------------------------
    /**
     * start executing this tag
     * @return EVAL_BODY_INCLUDE so that the body contents gets evaluated once
     * @exception JspException if there was a jsp error
     */
  public boolean handleAdd(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
    {
	setSuccess(false);

        if(getDiscussionRepository() == null) {
	 return false;
        }
	
	if ( getCreateRelatedContentForum() ) {
	   initContentRepository();
	   if (!createForum()) {
	    return checkFormRedirect (getSuccessUrl() , getFailureUrl(), pRequest, pResponse);
	   }
	}

	if (insertPost()) {
          setSuccess(true);
 	}
	return checkFormRedirect (getSuccessUrl() , getFailureUrl(), pRequest, pResponse);
    }

} // end of class
