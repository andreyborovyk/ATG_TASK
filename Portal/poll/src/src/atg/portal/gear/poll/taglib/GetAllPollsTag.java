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
import java.util.Map;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * This tag will retrieve a list of all poll items in the repository.
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/taglib/GetAllPollsTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class GetAllPollsTag extends TagSupport
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/taglib/GetAllPollsTag.java#2 $$Change: 651448 $";

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
  // property: pollCount
  int mPollCount;

  public void setPollCount(int pPollCount) {
    mPollCount = pPollCount;
  }

  public int getPollCount() {
    return mPollCount;
  }

  //---------------------------------------------------------------------------
  // property: pollList

  RepositoryItem[] mPollList;

  public  RepositoryItem[] getPollList() {

      mPollList=null;

      try {

        RepositoryView view = mPollRepository.getView("poll");
        Query query = view.getQueryBuilder().createUnconstrainedQuery();
        mPollList = view.executeQuery(query);

      }
      catch (RepositoryException e1) {
        pageContext.getServletContext().log("Unable to process query - " + e1.getMessage());
      }

    return mPollList;

  }

  public void setPollList(RepositoryItem[] pPollList) {
    mPollList = pPollList;
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
      pageContext.getServletContext().log(" GetAllPolls Tag: Unable to get poll repository");

    }
  }

  protected Repository getPollRepository()
  {
    return mPollRepository;
  }

  protected void setPollRepository(Repository pPollRepository) {
    mPollRepository = pPollRepository;
  }

  /**
   *  Class methods
   */

  //-------------------------------------
  /**
   * 
   */
  public int doStartTag() {


	if (mPollRepository==null) {
          initPollRepository();
        }

    if(getPollRepository() != null) {
         pageContext.setAttribute(getId(), this);
         return EVAL_BODY_INCLUDE;
    }
    else {
      pageContext.getServletContext().log("GetAllPolls Tag - could not initialize repository.");
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
    setPollCount(0);
    setPollList(null);

  }

} // end of class
