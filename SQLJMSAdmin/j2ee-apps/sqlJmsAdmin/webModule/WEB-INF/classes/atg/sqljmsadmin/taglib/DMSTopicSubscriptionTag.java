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
 * get a dms topic subscription by id
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSTopicSubscriptionTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSTopicSubscriptionTag
    extends DMSGenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSTopicSubscriptionTag.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    // topic subscription home
    private DMSTopicSubscriptionHome mTopicSubscriptionHome;

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // TopicSubscriptionId
    private String mTopicSubscriptionId;
    /**
     * set TopicSubscriptionId
     * @param pTopicSubscriptionId the TopicSubscriptionId
     */
    public void setTopicSubscriptionId(String pTopicSubscriptionId) { mTopicSubscriptionId = pTopicSubscriptionId; }
    /**
     * get TopicSubscriptionId
     * @return the TopicSubscriptionId
     */
    public String getTopicSubscriptionId() { return mTopicSubscriptionId; }

    //----------------------------------------
    // TopicSubscription
    private DMSTopicSubscription mTopicSubscription;
    /**
     * set TopicSubscription
     * @param pTopicSubscription the TopicSubscription
     */
    public void setTopicSubscription(DMSTopicSubscription pTopicSubscription) { mTopicSubscription = pTopicSubscription; }
    /**
     * get TopicSubscription
     * @return the TopicSubscription
     */
    public DMSTopicSubscription getTopicSubscription() { return mTopicSubscription; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof DMSTopicSubscriptionTag
     */
    public DMSTopicSubscriptionTag()
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
	    if(mTopicSubscriptionHome == null)
		mTopicSubscriptionHome = mHomeFactory.getTopicSubscriptionHome();

	    mTopicSubscription = 
		mTopicSubscriptionHome.findByPrimaryKey(Long.valueOf(mTopicSubscriptionId));
	    success = true;
	}
	catch(FinderException e) {
	    setException(e);
	}
	catch(EJBException e) {
	    e.printStackTrace();
	    setException(e);
	}
	catch(DMSAdminException e) {
	    setException(e);
	}
	catch(RemoteException e) {
	    setException(e);
	}
	catch(NamingException e) {
	    setException(e);
	}
	finally {
	    if(!success)
		mTopicSubscription = null;
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
	mTopicSubscription = null;
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
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
