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
 *  Calls UserTransaction.getStatus().  If the call is successful, then the
 *  success property of the tag is set to true. If the call fails, then
 *  the success property of the tag is set to false, and the exception
 *  property of the tag is set to the value of the Throwable Object thrown
 *  during the UserTransaction.getStatus() method call.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/TransactionStatusTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class TransactionStatusTag
  extends TransactionTag
{
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/TransactionStatusTag.java#2 $$Change: 651448 $";


  //-------------------------------------
  // Member variables
  //-------------------------------------


  //-------------------------------------
  // Properties
  //-------------------------------------
 
 
  //-------------------------------------
  // property status

   private int mStatus=Status.STATUS_UNKNOWN;
 
   public int getStatus(){
       return mStatus;
   }
   public void setStatus(int pStatus){
       mStatus=pStatus;
   }

   public boolean isActive(){
       return mStatus==Status.STATUS_ACTIVE?true:false;
   }

   public boolean isCommitted(){
       return mStatus==Status.STATUS_COMMITTED?true:false;
   }

   public boolean isCommitting(){
       return mStatus==Status.STATUS_COMMITTING?true:false;
   }

   public boolean isMarkedRollback(){
       return mStatus==Status.STATUS_MARKED_ROLLBACK?true:false;
   }

   public boolean isNoTransaction(){
       return mStatus==Status.STATUS_NO_TRANSACTION?true:false;
   }

   public boolean isPrepared(){
       return mStatus==Status.STATUS_PREPARED?true:false;
   }

   public boolean isPreparing(){
       return mStatus==Status.STATUS_PREPARING?true:false;
   }

   public boolean isRolledback(){
       return mStatus==Status.STATUS_ROLLEDBACK?true:false;
   }

   public boolean isRollingBack(){
       return mStatus==Status.STATUS_ROLLING_BACK?true:false;
   }

   public boolean isUnknown(){
       return mStatus==Status.STATUS_UNKNOWN?true:false;
   }

  /**
   *
   * Constructor
   **/
  public TransactionStatusTag()
  {
  }

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
      try {
        setStatus(ut.getStatus());
        setSuccess(true);
        setException(null);
      } catch (Exception e){
        setSuccess(false);
        setException(e);
      }
    }
    getPageContext().setAttribute(getId(),this);
    return EVAL_BODY_INCLUDE;
    }

  public int doEndTag ()throws JspException{
     setSuccess(false);
     setException(null);
     setStatus(Status.STATUS_UNKNOWN);
     return EVAL_PAGE;
  } 

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	try {
	    doEndTag();
	}
	catch(JspException e) {}
    }
    

    //----------------------------------------
    /**
     * override toString to return the status of the tag
     * in a String representation
     */
    public String toString()
    {
	switch(mStatus) {
	case Status.STATUS_ACTIVE:
	    return new String("Active");
	case Status.STATUS_COMMITTED:
	    return new String("Committed");
	case Status.STATUS_COMMITTING:
	    return new String("Committing");
	case Status.STATUS_MARKED_ROLLBACK:
	    return new String("Marked Rollback");
	case Status.STATUS_NO_TRANSACTION:
	    return new String("No Transaction");
	case Status.STATUS_PREPARED:
	    return new String("Prepared");
	case Status.STATUS_PREPARING: 
	    return new String("Preparing");
	case Status.STATUS_ROLLEDBACK:
	    return new String("Rolledback");
	case Status.STATUS_ROLLING_BACK:
	    return new String("Rolling Back");
	case Status.STATUS_UNKNOWN:
	    return new String("Unknown");
	}

	return new String("Unknown");
    }

}










