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
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/AddForumBean.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 *
 *  This class is used as a form input bean to capture form fields on the addForum.jsp page.
 */
public class AddForumBean
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/AddForumBean.java#2 $$Change: 651448 $";

  //---------------------------------------------------------------------------
  // property: forumName
  String mForumName;

  public void setForumName(String pForumName) {
    mForumName = pForumName;
  }

  public String getForumName() {
    return mForumName;
  }

  //---------------------------------------------------------------------------
  // property: forumDescription
  String mForumDescription;

  public void setForumDescription(String pForumDescription) {
    mForumDescription = pForumDescription;
  }

  public String getForumDescription() {
    return mForumDescription;
  }

  //---------------------------------------------------------------------------
  // property: forumType
  String mForumType;

  public void setForumType(String pForumType) {
    mForumType = pForumType;
  }

  public String getForumType() {
    return mForumType;
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
  
} // end of class
