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

package atg.sqljmsadmin.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.ejb.*;
import javax.naming.*;
import java.rmi.*;

import atg.sqljmsadmin.*;

/****************************************
 * get a queue by its id
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSQueueTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSQueueTag
    extends DMSGenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSQueueTag.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    // queue home
    private DMSQueueHome mQueueHome;

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // QueueId
    private Long mQueueId;
    /**
     * set QueueId
     * @param pQueueId the QueueId
     */
    public void setQueueId(String pQueueId) { mQueueId = Long.valueOf(pQueueId); }
    public void setQueueId(Long pQueueId) { mQueueId = pQueueId; }
    /**
     * get QueueId
     * @return the QueueId
     */
    public Long getQueueId() { return mQueueId; }

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
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof DMSQueueTag
     */
    public DMSQueueTag()
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
	boolean success = false;
	
	try {
	    if(mQueueHome == null)
		mQueueHome = mHomeFactory.getQueueHome();

	    mQueue = mQueueHome.findByPrimaryKey(mQueueId);
	    success = true;
	}
	catch(FinderException e) {
	    setException(e);
	}
	catch(DMSAdminException e) {
	    setException(e.getNestedException());
	    throw new JspException(e.getMessage());
	}
	catch(NamingException e) {
	    setException(e);
	    throw new JspException(e.getMessage());
	}
	catch(RemoteException e) {
	    setException(e);
	    throw new JspException(e.getMessage());
	}
	finally {
	    if(!success)
		mQueue = null;
	}

	pageContext.setAttribute(getId(), this);

	return EVAL_BODY_INCLUDE;
    }

    //----------------------------------------
    /**
     * end this tag
     */
    public int doEndTag()
	throws JspException
    {
	mQueue = null;
	return EVAL_PAGE;
    }

    //----------------------------------------
    /**
     * release tag
     */
    public void release()
    {
	super.release();
	mQueueId = null;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
