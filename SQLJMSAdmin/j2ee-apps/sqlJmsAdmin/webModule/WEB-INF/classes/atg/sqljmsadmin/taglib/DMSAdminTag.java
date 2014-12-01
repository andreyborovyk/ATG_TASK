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
 * this tag exposes the DMSAdmin session bean
 * to a jsp page
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSAdminTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSAdminTag
    extends DMSGenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSAdminTag.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    // DMSAdminHome object
    private DMSAdminHome mAdminHome;

    // DMSAdmin object
    private DMSAdmin mAdmin;

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // Clients
    private DMSClient[] mClients;
    private boolean mClientsStale;
    /**
     * get Clients
     * @return the Clients
     */
    public DMSClient[] getClients() 
    { 
	if(mClientsStale) {
	    boolean success = false;

	    try {
		mClients = mAdmin.getAllClients();
		success = true;
	    }
	    catch(RemoteException e) {
		setException(e);
	    }
	    catch(FinderException e) {
		setException(e);
	    }
	    catch(NamingException e) {
		setException(e);
	    }
	    catch(DMSAdminException e) {
		setException(e);
	    }
	    finally {
		if(!success)
		    mClients = null;
	    }
	}

	return mClients; 
    }

    //----------------------------------------
    // Queues
    private DMSQueue[] mQueues;
    private boolean mQueuesStale;
    /**
     * get Queues
     * @return the Queues
     */
    public DMSQueue[] getQueues() 
    { 
	if(mQueuesStale) {
	    boolean success = false;

	    try {
		mQueues = mAdmin.getAllQueues();
		success = true;
	    }
	    catch(RemoteException e) {
		setException(e);
	    }
	    catch(FinderException e) {
		setException(e);
	    }
	    catch(NamingException e) {
		setException(e);
	    }
	    catch(DMSAdminException e) {
		setException(e);
	    }
	    finally {
		if(!success)
		    mQueues = null;
	    }
	}

	return mQueues; 
    }

    //----------------------------------------
    // Topics
    private DMSTopic[] mTopics;
    private boolean mTopicsStale;
    /**
     * get Topics
     * @return the Topics
     */
    public DMSTopic[] getTopics() 
    { 
	if(mTopicsStale) {
	    boolean success = false;

	    try {
		mTopics = mAdmin.getAllTopics();
		success = true;
	    }
	    catch(RemoteException e) {
		setException(e);
	    }
	    catch(FinderException e) {
		setException(e);
	    }
	    catch(NamingException e) {
		setException(e);
	    }
	    catch(DMSAdminException e) {
		setException(e);
	    }
	    finally {
		if(!success)
		    mTopics = null;
	    }
	}

	return mTopics; 
    }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof DMSAdminTag
     */
    public DMSAdminTag()
    {
    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * start the tag
     */
    public int doStartTag()
	throws JspException
    {
	mClientsStale = true;
	mQueuesStale = true;
	mTopicsStale = true;

	try {
	    if(mAdminHome == null)
		mAdminHome = mHomeFactory.getAdminHome();

	    if(mAdmin == null)
		mAdmin = mAdminHome.create();
	}
	catch(CreateException e) {
	    setException(e);
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

	pageContext.setAttribute(getId(), this);

	return EVAL_BODY_INCLUDE;
    }

    //----------------------------------------
    /**
     * finish executing the tag
     */
    public int doEndTag()
	throws JspException
    {
	return EVAL_PAGE;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
