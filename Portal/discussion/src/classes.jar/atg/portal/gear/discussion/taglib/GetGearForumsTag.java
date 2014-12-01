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

package atg.portal.gear.discussion.taglib;

import atg.repository.*;
import atg.repository.rql.RqlStatement;
import atg.portal.nucleus.NucleusComponents;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * The getGearForums tag will retrieve a list of forums that are associated with a particular gear
 * instance.  If the user property is set, it first checks if the user has selected preferences 
 * (userGearBoard items) for this gear instance.
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/GetGearForumsTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class GetGearForumsTag extends TagSupport
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/GetGearForumsTag.java#2 $$Change: 651448 $";

  // pre-parse discreet set of statements to reduce cost of parseRqlStatment calls 
  protected static RqlStatement mGearStatement; 
  protected static RqlStatement mGearStatementDate; 
  protected static RqlStatement mGearStatementName; 
  protected static RqlStatement mGearStatementNumPosts; 
  protected static RqlStatement mUsrStatement; 
  protected static RqlStatement mUsrStatementDate; 
  protected static RqlStatement mUsrStatementName; 
  protected static RqlStatement mUsrStatementNumPosts; 
  protected static RqlStatement mRemovedUsrStatement; 
  protected static RqlStatement mRemovedGearStatement;
  static {
    try {
      mGearStatement = 
	RqlStatement.parseRqlStatement("gear = ?0 AND messageBoard.deleteFlag=0");
      mGearStatementDate = 
	RqlStatement.parseRqlStatement("gear = ?0 AND messageBoard.deleteFlag=0 ORDER BY messageBoard.lastPostTime DESC ");
      mGearStatementName = 
	RqlStatement.parseRqlStatement("gear = ?0 AND messageBoard.deleteFlag=0 ORDER BY messageBoard.name");
      mGearStatementNumPosts = 
	RqlStatement.parseRqlStatement("gear = ?0 AND messageBoard.deleteFlag=0 ORDER BY messageBoard.numPosts DESC ");
      mRemovedGearStatement = 
	RqlStatement.parseRqlStatement("gear = ?0 AND messageBoard.deleteFlag=1 ");
      mUsrStatement = 
	RqlStatement.parseRqlStatement("gear = ?0 AND user = ?1 AND messageBoard.deleteFlag=0 ");
      mUsrStatementDate = 
	RqlStatement.parseRqlStatement("gear = ?0 AND user = ?1 AND messageBoard.deleteFlag=0 ORDER BY messageBoard.lastPostTime DESC ");
      mUsrStatementName = 
	RqlStatement.parseRqlStatement("gear = ?0 AND user = ?1 AND messageBoard.deleteFlag=0 ORDER BY messageBoard.name");
      mUsrStatementNumPosts = 
	RqlStatement.parseRqlStatement("gear = ?0 AND user = ?1 AND messageBoard.deleteFlag=0 ORDER BY messageBoard.numPosts DESC ");
      mRemovedUsrStatement = 
	RqlStatement.parseRqlStatement("gear = ?0 AND user = ?1 AND messageBoard.deleteFlag=1 ");
    }
    catch(RepositoryException e) {
        System.out.println("GetGearForumsTag error parsing RqlStatement - " + e.getMessage());
    }
  }
  //---------------------------------------------------------------------------
  // property: id
  String mId;

  public void setId(String pId) {
    mId = pId;
  }

  public String getId() {
    return mId;
  }

  //---------------------------------------------------------------------------
  // property: forumCount
  int mForumCount=-1;

  public void setForumCount(int pForumCount) {
    mForumCount = pForumCount;
  }

  public int getForumCount() {

   // we may need to get this count before the list is retrieved, so run
   // the count query if the count is not set
   if (mForumCount<0) { 
      try {

         boolean bUserHasBoards=false;

	 // if user ID set, Check if user has forums selected
	 if ( getUser()!=null ) {
	   RqlStatement usrStatement;
             if ( getRemoved() ) {
	       usrStatement = mRemovedUsrStatement;
             } else {
	       usrStatement = mUsrStatement;
             }

      	    RepositoryView userView = getDiscussionRepository().getView("userGearBoards");
      	    Object [] usrParams = new Object [2];
      	    usrParams[0] = getGear();
      	    usrParams[1] = getUser();

             mUserForumCount=usrStatement.executeCountQuery(userView, usrParams);
 // System.out.println("getForumCount - called executeCountQuery for userGearBoards");
	    if (mUserForumCount > 0 ) {
              bUserHasBoards=true;
    	      mForumCount=mUserForumCount;
	    }
	 }  

        if (!bUserHasBoards) {
	  RqlStatement statement;
          if ( getRemoved() ) {
	    statement = mRemovedGearStatement;
          } else {
	    statement = mGearStatement;
          }
           RepositoryView cfView = getDiscussionRepository().getView("gearBoards");
           Object [] params = new Object [1];
           params[0] = getGear();
           mForumCount=statement.executeCountQuery(cfView, params);
 // System.out.println("getForumCount - called executeCountQuery for gearBoards");
        }
      }
      catch (RepositoryException e) {
        pageContext.getServletContext().log("Unable to process forum count query - " + e.getMessage());
      }
    }
    return mForumCount;
  }

  //---------------------------------------------------------------------------
  // property: userUserForumCount
  int mUserForumCount=-1;

  public void setUserForumCount(int pUserForumCount) {
    mUserForumCount = pUserForumCount;
  }

  public int getUserForumCount() {
   if (mUserForumCount<0) { 
      try {

	 // if user ID set, Check if user has forums selected
	 if ( getUser()!=null ) {
	   RqlStatement usrStatement;
             if ( getRemoved() ) {
	       usrStatement = mRemovedUsrStatement;
             } else {
	       usrStatement = mUsrStatement;
             }

      	    RepositoryView userView = getDiscussionRepository().getView("userGearBoards");
      	    Object [] usrParams = new Object [2];
      	    usrParams[0] = getGear();
      	    usrParams[1] = getUser();

             mUserForumCount=usrStatement.executeCountQuery(userView, usrParams);
 // System.out.println("getUserForumCount - called executeCountQuery for userGearBoards");
	    if (mUserForumCount > 0 ) {
    	      mForumCount=mUserForumCount;
	    }
	 }  
      }
      catch (RepositoryException e) {
        pageContext.getServletContext().log("Unable to process forum count query - " + e.getMessage());
      }
    }
    return mUserForumCount;
  }
  //---------------------------------------------------------------------------
  // property: gear
  String mGear;

  public void setGear(String pGear) {
    mGear = pGear;
  }

  public String getGear() {
    return mGear;
  }

  //---------------------------------------------------------------------------
  // property: user
  String mUser;

  public void setUser(String pUser) {
    mUser = pUser;
  }

  public String getUser() {
    return mUser;
  }

  //---------------------------------------------------------------------------
  // property: startRange
  String mStartRange;

  public void setStartRange(String pStartRange) {
    
    // leave this null unless a valid start range
    mStartRange=null;
    if (pStartRange!=null ) {
      if (Integer.parseInt(pStartRange) > 0 ) {
        mStartRange = pStartRange;
      }
    }
  }

  public String getStartRange() {
    return mStartRange;
  }

  //---------------------------------------------------------------------------
  // property: rangeSize
  int mRangeSize;

  public void setRangeSize(int pRangeSize) {
    if (pRangeSize < 0) {
      mRangeSize=0;
    }
    else {
      mRangeSize = pRangeSize;
    }
      
  }

  public int getRangeSize() {
    return mRangeSize;
  }

  public void setRangeSize(String pRangeSizeStr) {

    mRangeSize=0;
    if (pRangeSizeStr !=null) {
       mRangeSize = Integer.parseInt(pRangeSizeStr);
       if (mRangeSize < 0) {
          mRangeSize=0;
       }
    }
  }

  //---------------------------------------------------------------------------
  // property: sortOrder
  String mSortOrder;

  public void setSortOrder(String pSortOrder) {
    mSortOrder = pSortOrder;
  }

  public String getSortOrder() {
    return mSortOrder;
  }

  //---------------------------------------------------------------------------
  // property: removed
  boolean mRemoved;

  public void setRemoved(boolean pRemoved) {
    mRemoved = pRemoved;
  }

  public boolean getRemoved() {
    return mRemoved;
  }

  //---------------------------------------------------------------------------
  // property: forumId
  String mForumId;

  public void setForumId(String pForumId) {
    mForumId = pForumId;
  }

  public String getForumId() {
   // if not set, run query to get id of first forum
   if (mForumId == null ) {
     try {
      if (getUserForumCount() > 0) {
      	  RepositoryView userView = getDiscussionRepository().getView("userGearBoards");
      	  Object [] usrParams = new Object [2];
      	  usrParams[0] = getGear();
      	  usrParams[1] = getUser();
      	  RepositoryItem[] forums = mUsrStatement.executeQuery(userView, usrParams);
	  if (forums !=null) {
	    mForumId=((atg.repository.RepositoryItem) forums[0].getPropertyValue("messageBoard")).getRepositoryId();
	  }
	 // System.out.println("getForumId - called executeQuery for userGearBoards");
      } else {
      	  RepositoryView view = getDiscussionRepository().getView("gearBoards");
      	  Object [] params = new Object [1];
      	  params[0] = getGear();
      	  RepositoryItem[] forums = mGearStatement.executeQuery(view, params);
	  if (forums !=null) {
	    mForumId=((atg.repository.RepositoryItem) forums[0].getPropertyValue("messageBoard")).getRepositoryId();
	  }
	 // System.out.println("getForumId - called executeQuery for gearBoards");
      }
     }
     catch (RepositoryException e1) {
       pageContext.getServletContext().log("Unable to process query - " + e1.getMessage());
     }
    }
    return mForumId;
  }

  //---------------------------------------------------------------------------
  // property: forumList

  RepositoryItem[] mForumList;

  public  RepositoryItem[] getForumList() {

      mForumList=null;
      RepositoryItem []gearList;

      try {

	 // if user ID set, Check if user has forums selected
	 if ( getUser()!=null ) {
	   if (getUserForumCount() > 0 ) {
     	    RqlStatement usrStatement = null;
    	     if ( getRemoved() ) {
                usrStatement=mRemovedUsrStatement;
             } else {
    	       if ( getSortOrder() != null ) {
	         if (getSortOrder().equals("numPosts")) {
		   usrStatement=mUsrStatementNumPosts;
		 } else if (getSortOrder().equals("name")) {
		   usrStatement=mUsrStatementName;
		 } else {
		   usrStatement=mUsrStatementDate;
		 }
               } else {
                  usrStatement=mUsrStatementDate;
               }
             }


      	    RepositoryView userView = getDiscussionRepository().getView("userGearBoards");
      	    Object [] usrParams = new Object [2];
      	    usrParams[0] = getGear();
      	    usrParams[1] = getUser();
      	    gearList = usrStatement.executeQuery(userView, usrParams);
 // System.out.println("getForumList - called executeQuery for userGearBoards");

            // retrieve messageBoard items and copy to array based on range stuff
              if (gearList !=null) {
                 int counter=0;
		 int firstRec=getStartRangeInt();

		 if (gearList.length > firstRec + mRangeSize) {
		   counter=mRangeSize;
		 } else if (firstRec < gearList.length) {
		   counter=gearList.length - firstRec;
		 }

                 mForumList = new RepositoryItem[counter];

                 for (int idx1=0, idx2=firstRec; idx1<counter; idx1++,idx2++) {
                   mForumList[idx1] = (RepositoryItem) gearList[idx2].getPropertyValue("messageBoard");
                 }
               }
    	       return mForumList;
	    }
	 }  

	   // use pre-parsed statement based on sort order and removed flag
     	   RqlStatement gearStatement = null;
    	     if ( getRemoved() ) {
                gearStatement=mRemovedGearStatement;
             } else {
    	       if ( getSortOrder() != null ) {
	         if (getSortOrder().equals("numPosts")) {
		   gearStatement=mGearStatementNumPosts;
		 } else if (getSortOrder().equals("name")) {
		   gearStatement=mGearStatementName;
		 } else {
		   gearStatement=mGearStatementDate;
		 }
               } else {
                  gearStatement=mGearStatementDate;
               }
             }

      	   RepositoryView cfView = getDiscussionRepository().getView("gearBoards");
      	   Object [] params = new Object [1];
      	   params[0] = getGear();
      	   gearList = gearStatement.executeQuery(cfView, params);

 // System.out.println("getForumList - gearStatement: " + gearStatement.toString());
 // System.out.println("getForumList - called executeQuery for gearBoards");

  //System.out.println("gearList:");
//for (int j=0; j<gearList.length; j++) {
//  System.out.println( ((RepositoryItem) gearList[j].getPropertyValue("messageBoard")).getPropertyValue("name"));
// }
        // retrieve messageBoard items and copy to array
        if (gearList !=null) {
            int counter=0;
	    int firstRec=getStartRangeInt();

	    if (mRangeSize>0) {
	      if (gearList.length > firstRec + mRangeSize) {
	        counter=mRangeSize;
	      } else if (firstRec < gearList.length) {
	        counter=gearList.length - firstRec;
	      }
	    } else {
	      counter=gearList.length - firstRec;
	    } 

           mForumList = new RepositoryItem[counter];

           for (int idx1=0, idx2=firstRec; idx1<counter; idx1++,idx2++) {
             mForumList[idx1] = (RepositoryItem) gearList[idx2].getPropertyValue("messageBoard");
           }
	   if (mForumCount<0) {
              setForumCount(gearList.length);
	   }
         }


      }
      catch (RepositoryException e1) {
        pageContext.getServletContext().log("Unable to process query - " + e1.getMessage());
      }

    return mForumList;

  }

  public void setForumList(RepositoryItem[] pForumList) {
    mForumList = pForumList;
  }


  //-------------------------------------
  /**
   * property: discussionRepository
   */
  protected static Repository mDiscussionRepository;

  protected void initDiscussionRepository()
  {
    // perform JNDI lookup
    try {
      mDiscussionRepository = (Repository) NucleusComponents.lookup("dynamo:/atg/portal/gear/discussion/DiscussionRepository");
    }
    catch (javax.naming.NamingException e) {
     pageContext.getServletContext().log(" Discussion Gear: Unable to get discussion repository");

    }
  }

  protected Repository getDiscussionRepository()
  {
    return mDiscussionRepository;
  }

  protected void setDiscussionRepository(Repository pDiscussionRepository) {
    mDiscussionRepository = pDiscussionRepository;
  }

  /**
   *  Class methods
   */

  //-------------------------------------
  public int getStartRangeInt() {
    
        // make sure we never return a negative value
	if (getStartRange() != null) {
	  int retVal = Integer.parseInt(getStartRange());
	  if (retVal < 0) {
	    return 0;
	  } else {
	    return retVal;
	  }
        } else {
	  return 0;
	}
  }

  //-------------------------------------
  public int getPrevStartRangeInt() {
    
        // make sure we never return a negative value
	if (getStartRange() != null) {
	  int retVal = getStartRangeInt()-mRangeSize;
	  if (retVal < 0) {
	    return 0;
	  } else {
	    return retVal;
	  }
        } else {
	  return 0;
	}
  }

  //-------------------------------------
  /**
   * 
   */
  public int doStartTag() {

 // System.out.println("XXXXXXXXXXXXXX  getGearForums tag doStartTag   XXXXXXXXXXXXXXXXXXXXXXX");

	// get the discussion repository
	if (mDiscussionRepository==null) {
           initDiscussionRepository();
	}

    if(getDiscussionRepository() != null) {
         pageContext.setAttribute(getId(), this);
         return EVAL_BODY_INCLUDE;
    }
    else {
      pageContext.getServletContext().log("Discussion Gear - could not initialize repository.");
      return SKIP_BODY;
    }

  }

  //----------------------------------------

  public int doEndTag ()
    throws JspException
    {
      return EVAL_PAGE;
    }


  //----------------------------------------
  /**
   * release this tag
   */
  public void release()
  {
    super.release();
	
    setId(null);
    setForumCount(-1);
    setUserForumCount(-1);
    setGear(null);
    setUser(null);
    setForumList(null);
    setSortOrder(null);
    setStartRange(null);
    setRemoved(false);
    setRangeSize(0);
    setForumId(null);

  }

} // end of class
