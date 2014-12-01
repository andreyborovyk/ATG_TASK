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

package atg.portal.gear.poll.taglib;

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
 * The GetGearPollTag will retrieve a poll item for a given gear ID.
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/taglib/GetGearPollTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class GetGearPollTag extends TagSupport
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/taglib/GetGearPollTag.java#2 $$Change: 651448 $";

  protected static RqlStatement mGearParamStatement;
  protected static RqlStatement mPollParamOrderbyStatement;
  static {
    try {
      mGearParamStatement = RqlStatement.parseRqlStatement("gear = ?0");
      mPollParamOrderbyStatement = RqlStatement.parseRqlStatement("poll = ?0 ORDER BY sortOrder");
    }
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
  // property: gearId

  String mGearId;

  public void setGearId(String pGearId) {
    mGearId = pGearId;
  }

  public String getGearId() {
    return mGearId;
  }

  //---------------------------------------------------------------------------
  // property: poll
  RepositoryItem mPoll;

  public void setPoll(RepositoryItem pPoll) {
    mPoll = pPoll;
  }

  public RepositoryItem getPoll() {
    return mPoll;
  }

  public void setPoll(String pGearId) {
    mPoll=null;
    try {
      RepositoryView grView = getPollRepository().getView("gearPoll");
      Object [] params = new Object [1];
      params[0] = pGearId;
      RepositoryItem[] mPollList = mGearParamStatement.executeQuery(grView, params);
      if (mPollList!=null) {
         mPoll= (RepositoryItem) mPollList[0].getPropertyValue("poll");
      }
    }
    catch (RepositoryException e) {
      pageContext.getServletContext().log("Poll Gear: Unable to get poll item", e);
    }

  }



  //-------------------------------------
  /**
   * property: pollRepository
   */
  protected static Repository mPollRepository;

  protected void initPollRepository()
  {
    // perform JNDI lookup
    try {
      mPollRepository = (Repository) NucleusComponents.lookup("dynamo:/atg/portal/gear/poll/PollRepository");
    }
    catch (javax.naming.NamingException e) {
      pageContext.getServletContext().log("Poll Gear: Unable to get poll repository");
    }
  }

  protected Repository getPollRepository()
  {
    return mPollRepository;
  }

  public void setPollRepository(Repository pPollRepository) {
    mPollRepository = pPollRepository;
  }

  /**
   *  Class methods
   */

  //-------------------------------------
  public  RepositoryItem[] getResponseList() {

    RepositoryItem[] respList = null;
    try {
      RepositoryView respView = getPollRepository().getView("pollResponse");
      Object [] params = new Object [1];
      params[0] = getPollId();
      respList = mPollParamOrderbyStatement.executeQuery(respView, params);
    }
    catch (RepositoryException e) {
    }
    return respList;

  }

  //-------------------------------------
  public String getPollId() {
    if (mPoll!=null) {
       return mPoll.getRepositoryId();
    } else {
       return null;
    }
  }

  //-------------------------------------
  public String getQuestionText() {
    if (mPoll!=null) {
       return(String) mPoll.getPropertyValue("questionText");
    } else {
       return null;
    }
  }

  //-------------------------------------
  public String getPollTitle() {
    if (mPoll!=null) {
       return(String) mPoll.getPropertyValue("title");
    } else {
       return null;
    }
  }

  //-------------------------------------
  /**
   * 
   */
  public int doStartTag() {


	if (mPollRepository==null) {
          initPollRepository();
        }

    
    if(getPollRepository() != null) {

       try {
          setPoll(getGearId());
       }
       catch (Exception e) {
         pageContext.getServletContext().log("GetGearPoll tag: Unable to get poll",e);
         return SKIP_BODY;
       }
       pageContext.setAttribute(getId(), this);
       return EVAL_BODY_INCLUDE;
    }
    else {
      pageContext.getServletContext().log("GetGearPollTag - could not initialize repository.");
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
    setGearId(null);
    mPoll=null;  // to avoid ambiguous setPoll(null) call...

  }

} // end of class
