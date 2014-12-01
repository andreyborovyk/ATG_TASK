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
import javax.naming.*;
import java.rmi.*;
import javax.ejb.*;

import atg.sqljmsadmin.*;

/****************************************
 * get a topic entry by primary key
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSTopicEntryTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSTopicEntryTag
    extends DMSGenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSTopicEntryTag.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    // topic entry home
    private DMSTopicEntryHome mTopicEntryHome;

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // TopicSubscriptionId
    private Long mTopicSubscriptionId;
    /**
     * set TopicSubscriptionId
     * @param pTopicSubscriptionId the TopicSubscriptionId
     */
    public void setTopicSubscriptionId(Long pTopicSubscriptionId) { mTopicSubscriptionId = pTopicSubscriptionId; }
    /**
     * get TopicSubscriptionId
     * @return the TopicSubscriptionId
     */
    public Long getTopicSubscriptionId() { return mTopicSubscriptionId; }

    //----------------------------------------
    // MessageId
    private Long mMessageId;
    /**
     * set MessageId
     * @param pMessageId the MessageId
     */
    public void setMessageId(Long pMessageId) { mMessageId = pMessageId; }
    /**
     * get MessageId
     * @return the MessageId
     */
    public Long getMessageId() { return mMessageId; }

    //----------------------------------------
    // TopicEntry
    private DMSTopicEntry mTopicEntry;
    /**
     * set TopicEntry
     * @param pTopicEntry the TopicEntry
     */
    public void setTopicEntry(DMSTopicEntry pTopicEntry) { mTopicEntry = pTopicEntry; }
    /**
     * get TopicEntry
     * @return the TopicEntry
     */
    public DMSTopicEntry getTopicEntry() { return mTopicEntry; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof DMSTopicEntryTag
     */
    public DMSTopicEntryTag()
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
	if(mTopicSubscriptionId == null || mMessageId == null)
	    return SKIP_BODY;

	boolean success = false;
	
	try {
	    if(mTopicEntryHome == null)
		mTopicEntryHome = mHomeFactory.getTopicEntryHome();

	    DMSTopicEntryPrimaryKey primaryKey = 
		new DMSTopicEntryPrimaryKey(mTopicSubscriptionId, mMessageId);

	    mTopicEntry = mTopicEntryHome.findByPrimaryKey(primaryKey);
	    success = true;
	}
	catch(FinderException e) {
	    setException(e);
	}
	catch(DMSAdminException e) {
	    setException(e);
	    throw new JspException(e.getMessage());
	}
	catch(RemoteException e) {
	    setException(e);
	    throw new JspException(e.getMessage());
	}
	catch(NamingException e) {
	    setException(e);
	    throw new JspException(e.getMessage());
	}
	finally {
	    if(!success)
		mTopicEntry = null;
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
	mTopicEntry = null;
	return EVAL_PAGE;
    }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	super.release();
	mTopicSubscriptionId = null;
	mMessageId = null;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
