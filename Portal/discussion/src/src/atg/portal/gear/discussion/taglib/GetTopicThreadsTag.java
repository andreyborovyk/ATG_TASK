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
 * The getTopicThreads tag will retrieve a list of responses for a particular topic.  
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/GetTopicThreadsTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class GetTopicThreadsTag extends TagSupport
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/GetTopicThreadsTag.java#2 $$Change: 651448 $";


  protected static RqlStatement mThreadCountStatement;

  static {
    try {
      mThreadCountStatement = 
	RqlStatement.parseRqlStatement("messageBoard = ?0 AND topicResponseFlag = ?1 AND ultimateThread= ?2 AND deleteFlag=0");    }
    catch(RepositoryException e) {
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
  // property: topicID
  String mTopicID;

  public void setTopicID(String pTopicID) {
    mTopicID = pTopicID;
  }

  public String getTopicID() {
    return mTopicID;
  }

  //---------------------------------------------------------------------------
  // property: topic
  RepositoryItem mTopic;

  public void setTopic(RepositoryItem pTopic) {
    mTopic = pTopic;
  }

  public RepositoryItem getTopic() {
    return mTopic;
  }

//---------------------------------------------------------------------------
// property: threadCount
int mThreadCount=-1;

public void setThreadCount(int pThreadCount) {
  mThreadCount = pThreadCount;
}

 public int getThreadCount() {

   if (mThreadCount<0) {
    try {
      RepositoryView mtView = getDiscussionRepository().getView("messageThread");
      Object [] params = new Object [3];
      params[0] = getForumID();
      params[1] = "response";
      params[2] = getTopicID();
      mThreadCount = mThreadCountStatement.executeCountQuery(mtView, params);
    }
    catch (RepositoryException e) {
     pageContext.getServletContext().log("Unable to process thread count query - " + e.getMessage());
    }
   }

    return mThreadCount;
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
  // property: threadList 

  RepositoryItem[] mThreadList;

  public  RepositoryItem[] getThreadList() {

    mThreadList=null;
    
    String qryClause = "messageBoard = ?0 AND topicResponseFlag = ?1 AND ultimateThread= ?2 AND deleteFlag=0 ORDER BY "; 
    if ( getSortOrder() != null ) {
       if (getSortOrder().equals("user")) {
          qryClause += "user.login";
       } else {
          qryClause += getSortOrder();
       }
    } else {
       qryClause += "creationDate DESC";
    }

   // add range clause if range is set
   // String rangeClause=" RANGE ";
   // if ( (mStartRange!=null || mRangeSize>0) ) {
   //    if (mStartRange !=null) {
   //       rangeClause += mStartRange;
   //    }
   //    rangeClause += "+";
   //    rangeClause += getRangeSize();
   //    qryClause += rangeClause;
   // }

    try {
      RepositoryView mtView = getDiscussionRepository().getView("messageThread");
      RqlStatement statement = RqlStatement.parseRqlStatement(qryClause);
      Object [] params = new Object [3];
      params[0] = getForumID();
      params[1] = "response";
      params[2] = getTopicID();
      // params[3] = "subject";
      // params[4] = "DESC";
      RepositoryItem[] results = statement.executeQuery(mtView, params);


      // retrieve range of thread items and copy to list
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

           mThreadList = new RepositoryItem[counter];

           for (int idx1=0, idx2=firstRec; idx1<counter; idx1++,idx2++) {
             mThreadList[idx1] = results[idx2];
           }
        if (mThreadCount<0) {
          setThreadCount(results.length);
        }
      }

    }
    catch (RepositoryException e) {
    }

    return mThreadList;

}

  public void setThreadList(RepositoryItem[] pThreadList) {
    mThreadList = pThreadList;
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


	// get the discussion repository
	if (mDiscussionRepository==null) {
           initDiscussionRepository();
	}

    try {
       setTopic(getDiscussionRepository().getItem(getTopicID(),"messageThread"));
    }
    catch (Exception e) {
      pageContext.getServletContext().log("GetTopicThreads tag: Unable to set topic item");
      return SKIP_BODY;
    }
    
    if(getDiscussionRepository() != null) {
      pageContext.setAttribute(getId(), this);
      return EVAL_BODY_INCLUDE;
    }
    else {
      pageContext.getServletContext().log("Discussion:GetTopicThreadsTag - could not initialize repository.");
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
    setTopicID(null);
    setTopic(null);
    setThreadList(null);
    setSortOrder(null);
    setStartRange(null);
    setRangeSize(0);
    setThreadCount(-1);

  }

} // end of class
