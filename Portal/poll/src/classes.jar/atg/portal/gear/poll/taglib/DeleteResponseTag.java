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
 * The DeleteResponseTag will delete a single response item from a poll.
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/taglib/DeleteResponseTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class DeleteResponseTag  extends TagSupport
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/taglib/DeleteResponseTag.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Properties
    //----------------------------------------


    //----------------------------------------
    // property: responseId
    private String mResponseId;

    public void setResponseId(String pResponseId) 
	{ mResponseId = pResponseId; }

    public String getResponseId() 
	{ return mResponseId; }


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

   protected boolean deleteResponse() {


   boolean retval=true;
   // perform JNDI lookup
   TransactionManager tm=null;
   TransactionDemarcation td = new TransactionDemarcation ();

   try {
      tm  = (TransactionManager) NucleusComponents.lookup("dynamo:/atg/dynamo/transaction/TransactionManager");
   }
   catch (javax.naming.NamingException e) {
	pageContext.getServletContext().log("DeleteResponseTag - unable to get transaction mgr:", e);
        retval=false;
   }

   try { 
     try { 

	 td.begin (tm);

	 // delete pollResponse item
           try {
     	      mPollRepository.removeItem(getResponseId(),"pollResponse");
	   }
           catch (atg.repository.RepositoryException e) {
             pageContext.getServletContext().log("DeleteResponse - error removing pollResponse items", e);
             retval=false;
           }
     } 
     catch (Exception e) {
         pageContext.getServletContext().log("DeleteResponse - Repository error:", e);
         retval=false;
     }
     finally {
        td.end ();
     }
   } 
   catch (TransactionDemarcationException e) {
      pageContext.getServletContext().log("DeleteResponseTag - transaction error...",e);
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
	    throw new JspException("DeleteResponseTag - could not open repository");
        }
           
	if (deleteResponse()) {
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
	setResponseId(null);
	setSuccess(false);
    }

} // end of class
