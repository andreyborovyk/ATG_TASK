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

import atg.repository.*;
import atg.repository.rql.RqlStatement;
import atg.dtm.*;
import atg.portal.nucleus.NucleusComponents;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.transaction.*;

/**
 * This tag sets the counters to zero for each response of a given poll.
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/taglib/ClearPollResultsTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ClearPollResultsTag  extends TagSupport
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/taglib/ClearPollResultsTag.java#2 $$Change: 651448 $";


  protected static RqlStatement mClearResponseStatement;
  static {
    try {
      mClearResponseStatement = RqlStatement.parseRqlStatement("poll = ?0 ");
    }
    catch(RepositoryException e) {
    }
  }

    //----------------------------------------
    // Properties
    //----------------------------------------


    //----------------------------------------
    // property: pollId
    private String mPollId;

    public void setPollId(String pPollId) 
	{ mPollId = pPollId; }

    public String getPollId() 
	{ return mPollId; }


    //----------------------------------------
    // property: success
    private  boolean mSuccess;

    public void setSuccess(boolean pSuccess) 
	{ mSuccess = pSuccess; }

    public boolean getSuccess() 
	{ return mSuccess; }

  //-------------------------------------
  /**
   * property: pollRepository
   */
  protected static MutableRepository mPollRepository;

  protected void initPollRepository()
  {
    // perform JNDI lookup
    try {
      mPollRepository = (MutableRepository) NucleusComponents.lookup("dynamo:/atg/portal/gear/poll/PollRepository");
    }
    catch (javax.naming.NamingException e) {
      pageContext.getServletContext().log(" Poll Gear: Unable to get poll repository");

    }
  }

  protected MutableRepository getPollRepository()
  {
    return mPollRepository;
  }

  public void setPollRepository(MutableRepository pPollRepository) {
    mPollRepository = pPollRepository;
  }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

   protected boolean clearPoll() {


   boolean retval=true;
   // perform JNDI lookup
   TransactionManager tm=null;
   TransactionDemarcation td = new TransactionDemarcation ();

   try {
      tm  = (TransactionManager) NucleusComponents.lookup("dynamo:/atg/dynamo/transaction/TransactionManager");
   }
   catch (javax.naming.NamingException e) {
	pageContext.getServletContext().log("ClearPollResultsTag - unable to get transaction mgr:", e);
        retval=false;
   }

   try { 
     try { 

	 td.begin (tm);


	  Integer zeroCount=new Integer(0);


          // clear counts on all reponses for this poll
          try {
              RepositoryView gbView = getPollRepository().getView("pollResponse");
              Object [] params = new Object [1];
              params[0] = getPollId();
              RepositoryItem []respList = mClearResponseStatement.executeQuery(gbView, params);

              if (respList !=null) {
                 for (int i=0; i < respList.length; i++) {
                    MutableRepositoryItem mItem=mPollRepository.getItemForUpdate(respList[i].getRepositoryId(),"pollResponse");
                    mItem.setPropertyValue("responseCount",zeroCount);
		    mPollRepository.updateItem(mItem);
                 }
              }
         }
         catch (atg.repository.RepositoryException e) {
           pageContext.getServletContext().log("ClearPollResults - error clearing pollResponse counts", e);
         }
     } 
     catch (Exception e) {
         pageContext.getServletContext().log("ClearPollResults - Repository error:", e);
         retval=false;
     }
     finally {
        td.end ();
     }
   } 
   catch (TransactionDemarcationException e) {
      pageContext.getServletContext().log("ClearPollResultsTag - transaction error...",e);
      retval=false;
   }


    return retval; 
    

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
	setSuccess(false);

	// get the poll repository
	if (mPollRepository==null) {
          initPollRepository();
        }

        if(getPollRepository() == null) {
	    throw new JspException("ClearPollResultsTag - could not open repository");
        }
           
	if (clearPoll()) {
          setSuccess(true);
        }

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
	setSuccess(false);
    }

} // end of class
