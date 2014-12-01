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


/**
 * 
 *  This class is used as a form input bean to capture form fields on the discussionPostForm.jsp page.
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/CreatePostBean.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class CreatePostBean
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/CreatePostBean.java#2 $$Change: 651448 $";

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

  //---------------------------------------------------------------------------
  // property: handleForm
  boolean mHandleForm;

  public void setHandleForm(boolean pHandleForm) {
    mHandleForm = pHandleForm;
  }

  public boolean getHandleForm() {
    return mHandleForm;
  }
  
  
  //---------------------------------------------------------------------------
  // property: success
  boolean mSuccess = true;

  public void setSuccess(boolean pSuccess) {
    mSuccess = pSuccess;
  }

} // end of class
