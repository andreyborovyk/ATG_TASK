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

import javax.naming.*;
import javax.transaction.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/****************************************
 * the super class for all transaction-related tags
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/TransactionTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class TransactionTag
  extends GenericTag
{
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/TransactionTag.java#2 $$Change: 651448 $";


  //-------------------------------------
  // Member variables
  //-------------------------------------


  //-------------------------------------
  // Properties
  //-------------------------------------
 
 
  //-------------------------------------
  // property id

  private String mId;

  public String getId()
  { return mId; }

  public void setId(String pId)
  { mId = pId; }

  //-------------------------------------
  // boolean property success 

  private boolean mSuccess=false;

  public boolean isSuccess ()
  { return mSuccess; }

  public void setSuccess (boolean pSuccess)
  { mSuccess = pSuccess; }

  //-------------------------------------
  // property exception; 

    private Throwable mException;

    public Throwable getException(){
	return mException;
    }
    public void setException(Throwable pException){
        mException=pException;
    }

  //-------------------------------------
  /**
   *
   * Constructor
   **/
  public TransactionTag()
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
      return SKIP_BODY;
  }

  //-------------------------------------

  public int doEndTag ()throws JspException{
    cleanup();
    return EVAL_PAGE;
  } 

    /**
   * 
   */
  private void cleanup() {
    setSuccess(false);
    setException(null);
    mId = null;
  }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
      cleanup();
    }

   protected UserTransaction getUserTransaction(){
       try{
        return atg.servlet.ServletUtil.getUserTransaction();
       }catch (Exception e){
        setException(e);
        setSuccess(false);
        return null;}
   }

}










