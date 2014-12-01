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
 * 
 * The getUserForumsTag will retrieve a list of forums associated with a user and a 
 * specific gear instance. This differs from the getGearForums tag in that if a user
 * has no forums selected, it returns nothing, whereas getGearForums defaults to returning
 * all forums associated with the gear.
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/GetUserForumsTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class GetUserForumsTag extends TagSupport
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/taglib/GetUserForumsTag.java#2 $$Change: 651448 $";

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

	 // if user ID set, Check if user has forums selected
	 if ( getUser()!=null ) {
  	    String usrQryClause = "gear = ?0 AND user = ?1 ";
    	    usrQryClause += " ORDER BY messageBoard.lastPostTime DESC";
	    // add range clause if range is set 
 	    String usrRangeClause=" RANGE ";
  	    if ( (mStartRange!=null || mRangeSize>0) ) {
     	       if (mStartRange !=null) {
       		  usrRangeClause += mStartRange;
      	       }
      	       usrRangeClause += "+";
     	       usrRangeClause += getRangeSize();
    	       usrQryClause += usrRangeClause;
	    }

      	    RepositoryView userView = getDiscussionRepository().getView("userGearBoards");
     	    RqlStatement usrStatement = RqlStatement.parseRqlStatement(usrQryClause);
      	    Object [] usrParams = new Object [2];
      	    usrParams[0] = getGear();
      	    usrParams[1] = getUser();

            int userForumCount=usrStatement.executeCountQuery(userView, usrParams);
	    if (userForumCount > 0 ) {
    	      mForumCount=userForumCount;
	    }
	 }  

      }
      catch (RepositoryException e) {
        pageContext.getServletContext().log("Unable to process forum count query - " + e.getMessage());
      }
    }
    return mForumCount;
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
  // property: forumList

  RepositoryItem[] mForumList;

  public  RepositoryItem[] getForumList() {

      mForumList=null;

      try {


	 // if user ID set, Check if user has forums selected
	 if ( getUser()!=null ) {
  	    String usrQryClause = "gear = ?0 AND user = ?1 ";
    	    usrQryClause += " ORDER BY messageBoard.lastPostTime DESC";
	    // add range clause if range is set 
 	    String usrRangeClause=" RANGE ";
  	    if ( (mStartRange!=null || mRangeSize>0) ) {
     	       if (mStartRange !=null) {
       		  usrRangeClause += mStartRange;
      	       }
      	       usrRangeClause += "+";
     	       usrRangeClause += getRangeSize();
    	       usrQryClause += usrRangeClause;
	    }

      	    RepositoryView userView = getDiscussionRepository().getView("userGearBoards");
     	    RqlStatement usrStatement = RqlStatement.parseRqlStatement(usrQryClause);
      	    Object [] usrParams = new Object [2];
      	    usrParams[0] = getGear();
      	    usrParams[1] = getUser();

            int userForumCount=usrStatement.executeCountQuery(userView, usrParams);
	    if (userForumCount > 0 ) {
      	      RepositoryItem []gearList = usrStatement.executeQuery(userView, usrParams);
              // retrieve messageBoard items and copy to array
              if (gearList !=null) {
                 int counter=0;
                 if ( (mRangeSize>0) && (gearList.length>mRangeSize) ) {
                    counter=mRangeSize;
                 } else {
                    counter=gearList.length;
                 }
                 mForumList = new RepositoryItem[counter];

                 for (int i=0; i < counter; i++) {
                   mForumList[i] = (RepositoryItem) gearList[i].getPropertyValue("messageBoard");
                 }
                 setForumCount(userForumCount);
               }
    	       return mForumList;
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
    setGear(null);
    setUser(null);
    setForumList(null);
    setStartRange(null);
    setRangeSize(0);

  }

} // end of class
