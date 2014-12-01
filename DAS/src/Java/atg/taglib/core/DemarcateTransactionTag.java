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

package atg.taglib.core;


import javax.transaction.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/****************************************
 *  Starts a new transaction and executes its body. If the transaction
 *  has not been marked for rollback, then the transaction is commited,
 *  otherwise it is rolled back. If the transaction is created
 *  successfully, then the 
 *  success property of the tag is set to true. If there was an error
 *  creating the new transaction, then
 *  the success property of the tag is set to false, and the exception
 *  property of the tag is set to the value of the Exception thrown
 *  during the UserTransaction.begin() method call.
 *<p>
 *  If a transaction is already in place when this tag is executed, then
 *  the body of this tag is not rendered.
 *<p>
 *  If an error occurs while in the body of this tag, the
 *  demarcateTransaction tag will not know this, and will try to
 *  commit the transaction anyway.  The tag programmer must insert a
 *  java try/catch block within the body of the tag in order to catch
 *  any exceptions and mark the current transaction as rollback
 *  only. This problem will be rectified in jsp 1.2 when a tag has the
 *  ability to receive notification of exceptions thrown in its body.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/DemarcateTransactionTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DemarcateTransactionTag
  extends TransactionTag
  implements TryCatchFinally
{
    //-------------------------------------
    // Class version string

    public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/DemarcateTransactionTag.java#2 $$Change: 651448 $";


    //-------------------------------------
    // Member variables
    //-------------------------------------


    //-------------------------------------
    // Properties
    //-------------------------------------

    //----------------------------------------
    // CommitException
    private Throwable mCommitException;
    /**
     * set CommitException
     * @param pCommitException the CommitException
     */
    public void setCommitException(Throwable pCommitException) { mCommitException = pCommitException; }
    /**
     * get CommitException
     * @return the CommitException
     */
    public Throwable getCommitException() { return mCommitException; }
 

  /**
   *
   * Constructor
   **/
    public DemarcateTransactionTag()
    {
    }

    //----------------------------------------
    // Object methods
    //----------------------------------------

    //-------------------------------------
    // Tag interface methods
    //-------------------------------------

    //-------------------------------------
    /**
     *
     * This performs the actual work of the tag.  
     **/

    public int doStartTag ()
	throws JspException
    {
      UserTransaction ut=getUserTransaction();
	if (ut!=null){
	    try{
		ut.begin();
		setSuccess(true);
	    }catch (Exception e){
		setSuccess(false);
		setException(e);
	    }
	}
	else {
	    return SKIP_BODY;
	}
	getPageContext().setAttribute(getId(),this);
	return EVAL_BODY_INCLUDE;
      
    }

    public int doEndTag ()
	throws JspException 
    {
	return EVAL_PAGE;
    } 

  //----------------------------------------
  /**
   * if there is an exception during the transaction,
   * mark the transaction for rollback only
   */
  public void doCatch(Throwable pException) 
    throws Throwable
  {
    setException(pException);

    if (!(pException instanceof atg.servlet.jsp.EndOfPageException)){
      UserTransaction ut = getUserTransaction();
      if(ut != null) {
        ut.setRollbackOnly();
      }
    }

    throw pException;
  }

  //----------------------------------------
  /**
   * execute this after doCatch is called
   */
  public void doFinally()
  {
    if (isSuccess()){
      UserTransaction ut=getUserTransaction();
      if (ut!=null){
	try{
	  int xaStatus = ut.getStatus();
	  
	  if(xaStatus == Status.STATUS_MARKED_ROLLBACK) {
	    ut.rollback();
	  }
	  else {
	    ut.commit();
	  }
	} 
	catch (Exception e) {
	  setCommitException(e);
	}
      }
    }

    setSuccess(false);
    setException(null);
  }

  //----------------------------------------
  /**
   * release this tag
   */
  public void release()
  {
    setId(null);
  }
}










