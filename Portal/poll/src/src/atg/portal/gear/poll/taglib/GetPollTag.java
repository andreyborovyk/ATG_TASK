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


package atg.portal.gear.poll.taglib;

import atg.portal.gear.poll.PollResult;
import atg.repository.*;
import atg.repository.rql.RqlStatement;
import atg.dtm.*;
import atg.portal.nucleus.NucleusComponents;

import java.io.IOException;
import java.util.Set;
import java.util.Map;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.transaction.*;


/**
 * 
 * The GetPollTag provides access to all poll data, including the question, and the list
 * of responses,counts and percentages, using a poll ID (vs a gear ID)
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/taglib/GetPollTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class GetPollTag  extends TagSupport
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/taglib/GetPollTag.java#2 $$Change: 651448 $";


  protected static RqlStatement mGearParamStatement;
  protected static RqlStatement mPollParamOrderbyStatement;
  static {
    try {
      mPollParamOrderbyStatement = RqlStatement.parseRqlStatement("poll = ?0 ORDER BY sortOrder");
      mGearParamStatement = RqlStatement.parseRqlStatement("gear = ?0");
    }
    catch(RepositoryException e) {
    }
  }

    //----------------------------------------
    // Properties
    //----------------------------------------


  //---------------------------------------------------------------------------
  // property: pollId

  String mPollId;

  public void setPollId(String pPollId) {
    mPollId = pPollId;
  }

    public String getPollId() {
        if (mPollId != null)        
            return mPollId;
        else if (getPoll() != null)
            return getPoll().getRepositoryId();
        else return null;
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
      pageContext.getServletContext().log(" Poll Gear: Unable to get Poll repository");

    }
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

  protected Repository getPollRepository()
  {
    return mPollRepository;
  }

  public void setPollRepository(Repository pPollRepository) {
    mPollRepository = pPollRepository;
  }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------
    
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

    //----------------------------------------

    public PollResult[] getPollResultList() {

    RepositoryItem[] responses = null;
    PollResult []resultSet=null;
    int totalVotes=0;

    // holders for item properties to avoid null pointer exceptions
    String strRespText=null;
    String strShortName=null;
    String strRespCount=null;
    String strBarColor=null;

    try {
      RepositoryView respView = getPollRepository().getView("pollResponse");
      Object [] params = new Object [1];
      params[0] = getPollId();
      responses = mPollParamOrderbyStatement.executeQuery(respView, params);


      if (responses!=null) {
         resultSet=new PollResult[responses.length];
         for (int i=0; i < responses.length; i++) {
           if (responses[i].getPropertyValue("responseText")!=null) {
	      strRespText=responses[i].getPropertyValue("responseText").toString();
   	   }
           if (responses[i].getPropertyValue("shortName")!=null) {
	      strShortName=responses[i].getPropertyValue("shortName").toString();
	   }
           if (responses[i].getPropertyValue("responseCount")!=null) {
	      strRespCount=responses[i].getPropertyValue("responseCount").toString();
	   }
           if (responses[i].getPropertyValue("barColor")!=null) {
	      strBarColor=responses[i].getPropertyValue("barColor").toString();
	   }
           PollResult currResult=new PollResult(responses[i].getRepositoryId(),strRespText,strShortName,strRespCount, "0",strBarColor);
           resultSet[i]=currResult;

	   // add up counts to get total
	   totalVotes+=( (Integer) responses[i].getPropertyValue("responseCount")).intValue();
         }
         // now that we have total, update the percentages
         if (totalVotes > 0) {
           for (int i=0; i < responses.length; i++) {
             resultSet[i].setPercentage((resultSet[i].getCount()*100)/totalVotes);
           }
         }
      } 

    }
    catch (RepositoryException e) {
    }
    return resultSet;


    }

  public void setPollByGearId(String pGearId) {
    try {
      RepositoryView grView = getPollRepository().getView("gearPoll");
      Object [] params = new Object [1];
      params[0] = pGearId;
      RepositoryItem[] mPollList = mGearParamStatement.executeQuery(grView, params);
      if (mPollList!=null) {
         setPoll((RepositoryItem) mPollList[0].getPropertyValue("poll"));
      }
    }
    catch (RepositoryException e) {
        pageContext.getServletContext().log("Poll Gear: Unable to get poll item", e);
    }
    
  }

  public void setPollByPollId(String pPollId) {
    try {
        setPoll(mPollRepository.getItem(getPollId(),"poll"));
    }
    catch (RepositoryException e) {
        pageContext.getServletContext().log("Poll Gear: Unable to get poll item", e);
    }
    
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
	// get the poll repository
	if (mPollRepository==null) {
          initPollRepository();
        }

        if(getPollRepository() == null) {
	    throw new JspException("GetPollTag - could not open repository");
        }
           
	// if pollId not set, get it using the gear Id
	if (mPollId!=null) 
            setPollByPollId(mPollId);        
        else if (mGearId!=null) 
	    setPollByGearId(mGearId);
        else
              pageContext.getServletContext().log("GetPollTag error: either pollId or gearId must be set");
	
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
	setPollId(null);
        setPoll(null);
        setGearId(null);
    }

} // end of class
