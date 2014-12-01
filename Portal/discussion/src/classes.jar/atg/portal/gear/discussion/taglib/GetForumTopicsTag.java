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
import java.util.Set;
import java.util.Map;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * 
 * The getForumTopics tag is will retrieve a list of topics for a particular forum.  A topic
 * is a messageThread repository item whose topicResponseFlag="T".
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/GetForumTopicsTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class GetForumTopicsTag extends TagSupport
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/GetForumTopicsTag.java#2 $$Change: 651448 $";

  protected static RqlStatement mRqlStatement;
  protected static RqlStatement mTopicStatementSubject;
  protected static RqlStatement mTopicStatementUser;
  protected static RqlStatement mTopicStatementChildren;
  protected static RqlStatement mTopicStatementDate;
  static {
    try {
      mRqlStatement = RqlStatement.parseRqlStatement("messageBoard = ?0 AND deleteFlag=0 AND topicResponseFlag = ?1");
      mTopicStatementSubject = 
	RqlStatement.parseRqlStatement("messageBoard = ?0 AND topicResponseFlag = ?1 AND deleteFlag=0 ORDER BY subject");
      mTopicStatementUser = 
	RqlStatement.parseRqlStatement("messageBoard = ?0 AND topicResponseFlag = ?1 AND deleteFlag=0 ORDER BY user.login");
      mTopicStatementChildren = 
	RqlStatement.parseRqlStatement("messageBoard = ?0 AND topicResponseFlag = ?1 AND deleteFlag=0 ORDER BY childrenQty DESC");
      mTopicStatementDate = 
	RqlStatement.parseRqlStatement("messageBoard = ?0 AND topicResponseFlag = ?1 AND deleteFlag=0 ORDER BY creationDate DESC");
    }
    catch(RepositoryException e) {
        System.out.println("GetForumTopicsTag error parsing RqlStatement - " + e.getMessage());
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
  // property: forumID
  String mForumID;

  public void setForumID(String pForumID) {
    mForumID = pForumID;
  }

  public String getForumID() {
    return mForumID;
  }

  //---------------------------------------------------------------------------
  // property: forum
  RepositoryItem mForum;

  public void setForum(RepositoryItem pForum) {
    mForum = pForum;
  }

  public RepositoryItem getForum() {
    return mForum;
  }
 //---------------------------------------------------------------------------
 // property: topicCount
 int mTopicCount=-1;

 public void setTopicCount(int pTopicCount) {
   mTopicCount = pTopicCount;
 }

 public int getTopicCount() {


   if (mTopicCount<0) {
    try {
     RepositoryView mtView = getDiscussionRepository().getView("messageThread");
     Object [] params = new Object [2];
     params[0] = getForumID();
     params[1] = "topic";
     mTopicCount=mRqlStatement.executeCountQuery(mtView, params);
 // System.out.println("getTopicCount - called executeCountQuery for messageThread");
    }
    catch (RepositoryException e) {
     pageContext.getServletContext().log("Unable to process topic count query - " + e.getMessage());
    }
   }

    return mTopicCount;
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
  // property: topicList 

  RepositoryItem[] mTopicList;

  public  RepositoryItem[] getTopicList() {

    mTopicList=null;

     // use pre-parsed statement based on sort order - default=date
     RqlStatement topicStatement = null;
     if ( getSortOrder() != null ) {
         if (getSortOrder().equals("subject")) {
	   topicStatement=mTopicStatementSubject;
	 } else if (getSortOrder().equals("user")) {
	   topicStatement=mTopicStatementUser;
	 } else if (getSortOrder().equals("children")) {
	   topicStatement=mTopicStatementChildren;
	 } else {
	   topicStatement=mTopicStatementDate;
	 }
     } else {
         topicStatement=mTopicStatementDate;
     }

    try {
      RepositoryView mtView = getDiscussionRepository().getView("messageThread");
      Object [] params = new Object [2];
      params[0] = getForumID();
      params[1] = "topic";
      RepositoryItem[] results = topicStatement.executeQuery(mtView, params);

 //System.out.println("getTopicList - topicStatement: " + topicStatement.toString());
 // System.out.println("getTopicList - called executeQuery for messageThread");
 // System.out.println("topicList:");
// for (int j=0; j<results.length; j++) {
//  System.out.println(results[j].getPropertyValue("subject"));
// }

      // retrieve range of topic items and copy to list
      if (results !=null) {
            int counter=0;
	    int firstRec=getStartRangeInt();

	    if (mRangeSize>0) {
	      if (results.length > firstRec + mRangeSize) {
	        counter=mRangeSize;
	      } else if (firstRec < results.length) {
	        counter=results.length - firstRec;
	      }
	    } else {
	      counter=results.length - firstRec;
	    } 

           mTopicList = new RepositoryItem[counter];

           for (int idx1=0, idx2=firstRec; idx1<counter; idx1++,idx2++) {
             mTopicList[idx1] = results[idx2];
           }
        if (mTopicCount<0) {
          setTopicCount(results.length);
        }
      }

    }
    catch (RepositoryException e) {
    }

    return mTopicList;

}

  public void setTopicList(RepositoryItem[] pTopicList) {
    mTopicList = pTopicList;
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

  public void setDiscussionRepository(Repository pDiscussionRepository) {
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
  public String trimText(String pStr, String pLength) {

    int len=0;
    if (pLength != null) {
      len=Integer.parseInt(pLength);
    }

    if ( len>0 && (pStr.length() > len) ) {
       return pStr.substring(0, len) + "...";
    } else {
       return pStr;
    }
  }

  //-------------------------------------
  /**
   * 
   */
  public int doStartTag() {


 // System.out.println("XXXXXXXXXXXXXX  getForumTopics tag doStartTag   XXXXXXXXXXXXXXXXXXXXXXX");

	// get the discussion repository
	if (mDiscussionRepository==null) {
           initDiscussionRepository();
	}

    try {
       setForum(getDiscussionRepository().getItem(getForumID(),"messageBoard"));
    }
    catch (Exception e) {
      pageContext.getServletContext().log(" Discussion:GetForumTopicsTag - Unable to set forum item");
    }
    
    if(getDiscussionRepository() != null) {
      pageContext.setAttribute(getId(), this);
      return EVAL_BODY_INCLUDE;
    }
    else {
      pageContext.getServletContext().log("Discussion:GetForumTopicsTag - could not initialize repository.");
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
    setForumID(null);
    setForum(null);
    setTopicList(null);
    setTopicCount(-1);
    setStartRange(null);
    setRangeSize(0);
    setSortOrder(null);
    
  }

} // end of class
