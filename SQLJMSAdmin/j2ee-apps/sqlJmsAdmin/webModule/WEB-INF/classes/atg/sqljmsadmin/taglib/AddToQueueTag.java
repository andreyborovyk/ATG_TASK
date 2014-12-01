/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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

package atg.sqljmsadmin.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.rmi.RemoteException;

import atg.sqljmsadmin.*;

/****************************************
 * add some entries to a queue
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/AddToQueueTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class AddToQueueTag
    extends DMSGenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/AddToQueueTag.java#2 $$Change: 651448 $";

    //----------------------------------------
    // TEI
    //----------------------------------------
    public static class AddToQueueTEI 
	extends TagExtraInfo
    {
	public VariableInfo[] getVariableInfo(TagData pTagData)
	{
	    return new VariableInfo[] {
		new VariableInfo(pTagData.getId(),
				 "atg.sqljmsadmin.taglib.AddToQueueTag",
				 true,
				 VariableInfo.NESTED)
		    };
	}
    }

    //----------------------------------------
    // Constants
    //----------------------------------------

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // MessageIds
    private Long[] mMessageIds;
    /**
     * set MessageIds
     * @param pMessageIds the MessageIds
     */
    public void setMessageIds(Long[] pMessageIds) { mMessageIds = pMessageIds; }
    /**
     * get MessageIds
     * @return the MessageIds
     */
    public Long[] getMessageIds() { return mMessageIds; }

    //----------------------------------------
    // Queue
    private DMSQueue mQueue;
    /**
     * set Queue
     * @param pQueue the Queue
     */
    public void setQueue(DMSQueue pQueue) { mQueue = pQueue; }
    /**
     * get Queue
     * @return the Queue
     */
    public DMSQueue getQueue() { return mQueue; }

    //----------------------------------------
    // DuplicateMessages
    private boolean mDuplicateMessages;
    /**
     * set DuplicateMessages
     * @param pDuplicateMessages the DuplicateMessages
     */
    public void setDuplicateMessages(boolean pDuplicateMessages) { mDuplicateMessages = pDuplicateMessages; }
    /**
     * get DuplicateMessages
     * @return the DuplicateMessages
     */
    public boolean isDuplicateMessages() { return mDuplicateMessages; }

    //----------------------------------------
    // DuplicatePrimaryKey
    private boolean mDuplicatePrimaryKey;
    /**
     * set DuplicatePrimaryKey
     * @param pDuplicatePrimaryKey the DuplicatePrimaryKey
     */
    public void setDuplicatePrimaryKey(boolean pDuplicatePrimaryKey) { mDuplicatePrimaryKey = pDuplicatePrimaryKey; }
    /**
     * get DuplicatePrimaryKey
     * @return the DuplicatePrimaryKey
     */
    public boolean isDuplicatePrimaryKey() { return mDuplicatePrimaryKey; }
    
    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof AddToQueueTag
     */
    public AddToQueueTag()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * start this tag
     */
    public int doStartTag()
	throws JspException
    {
	if(mMessageIds == null || mMessageIds.length == 0 || mQueue == null) {
	    return SKIP_BODY;
	}

	try {
	    mQueue.addQueueEntries(mMessageIds, mDuplicateMessages);
	}
	catch(RemoteException e) {
	    setException(e);
	    throw new JspException(e.getMessage());
	}
	catch(DMSAdminException e) {
	    if(e.getNestedException() instanceof javax.ejb.DuplicateKeyException)
		mDuplicatePrimaryKey = true;
	    else {
		setException(e.getNestedException());
		throw new JspException(e.getMessage());
	    }
	}

	pageContext.setAttribute(getId(), this);

	return EVAL_BODY_INCLUDE;
    }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	super.release();
	mMessageIds = null;
	mQueue = null;
	mDuplicateMessages = false;
	mDuplicatePrimaryKey = false;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
