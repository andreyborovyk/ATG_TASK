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

import java.util.Hashtable;
import javax.ejb.*;

import atg.sqljmsadmin.*;

/****************************************
 * This context provides a jsp page with the current
 * dms elements.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSAdminContext.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSAdminContext
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSAdminContext.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    // delete a client
    public static final String kDeleteClient = "deleteClient";

    // delete a queue
    public static final String kDeleteQueue = "deleteQueue";
    
    // delete queue entry
    public static final String kDeleteQueueEntry = "deleteQueueEntry";

    // delete some queue entries
    public static final String kDeleteSomeQueueEntries = "deleteSomeQueueEntries";

    // delete all queue entries
    public static final String kDeleteAllQueueEntries = "deleteAllQueueEntries";

    // move a queue entry
    public static final String kMoveQueueEntry = "moveQueueEntry";
    
    // move some queue entries
    public static final String kMoveSomeQueueEntries = "moveSomeQueueEntries";

    // move all queue entries
    public static final String kMoveAllQueueEntries = "moveAllQueueEntries";

    // delete a topic
    public static final String kDeleteTopic = "deleteTopic";
    
    // delete a topic subscription
    public static final String kDeleteTopicSubscription = "deleteTopicSubscription";
    
    // delete a topic entry
    public static final String kDeleteTopicEntry = "deleteTopicEntry";
    
    // delete all topic entries
    public static final String kDeleteAllTopicEntries = "deleteAllTopicEntries";
    
    // delete some topic entries
    public static final String kDeleteSomeTopicEntries = "deleteSomeTopicEntries";

    // move a topic entry
    public static final String kMoveTopicEntry = "moveTopicEntry";
    
    // move some topic entries
    public static final String kMoveSomeTopicEntries = "moveSomeTopicEntries";

    // move all topic entries
    public static final String kMoveAllTopicEntries = "moveAllTopicEntries";



    // move all unhandled topic entries
    public static final String kMoveAllUnhandledTopicEntries = "moveAllUnhnandledTopicEntries";
    
    // move all unhandled queue entries
    public static final String kMoveAllUnhandledQueueEntries = "moveAllUnhandledQueueEntries";



    // delete all pending queue entries
    public static final String kDeletePendingQueueEntries = "deletePendingQueueEntries";

    // delete some pending queue entries
    public static final String kDeleteSomePendingQueueEntries = "deleteSomePendingQueueEntries";

    // delete all unhandled queue entries
    public static final String kDeleteUnhandledQueueEntries = "deleteUnhandledQueueEntries";

    // delete some unhandled queue entries
    public static final String kDeleteSomeUnhandledQueueEntries = 
	"deleteSomeUnhandledQueueEntries";
    
    // delete all pending topic entries
    public static final String kDeletePendingTopicEntries = "deletePendingTopicEntries";

    // delete some pending topic entries
    public static final String kDeleteSomePendingTopicEntries = "deleteSomePendingTopicEntries";
    
    // delete all unhandled topic entries
    public static final String kDeleteUnhandledTopicEntries = "deleteUnhandledTopicEntries";

    // delete some unhandled topic entries
    public static final String kDeleteSomeUnhandledTopicEntries = 
	"deleteSomeUnhandledTopicEntries";

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // Client
    private DMSClient mClient;
    /**
     * set Client
     * @param pClient the Client
     */
    public void setClient(DMSClient pClient) { mClient = pClient; }
    /**
     * get Client
     * @return the Client
     */
    public DMSClient getClient() { return mClient; }

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
    // Topic
    private DMSTopic mTopic;
    /**
     * set Topic
     * @param pTopic the Topic
     */
    public void setTopic(DMSTopic pTopic) { mTopic = pTopic; }
    /**
     * get Topic
     * @return the Topic
     */
    public DMSTopic getTopic() { return mTopic; }

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
    // QueueEntry
    private DMSQueueEntry mQueueEntry;
    /**
     * set QueueEntry
     * @param pQueueEntry the QueueEntry
     */
    public void setQueueEntry(DMSQueueEntry pQueueEntry) { mQueueEntry = pQueueEntry; }
    /**
     * get QueueEntry
     * @return the QueueEntry
     */
    public DMSQueueEntry getQueueEntry() { return mQueueEntry; }

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
    // Message
    private DMSMessage mMessage;
    /**
     * set Message
     * @param pMessage the Message
     */
    public void setMessage(DMSMessage pMessage) { mMessage = pMessage; }
    /**
     * get Message
     * @return the Message
     */
    public DMSMessage getMessage() { return mMessage; }

    //----------------------------------------
    // PreviousNavigation
    private DMSNavigationState mPreviousNavigation;
    /**
     * set PreviousNavigation
     * @param pPreviousNavigation the PreviousNavigation
     */
    public void setPreviousNavigation(DMSNavigationState pPreviousNavigation) 
    { 
	mPreviousNavigation = pPreviousNavigation; 
    }
    /**
     * get PreviousNavigation
     * @return the PreviousNavigation
     */
    public DMSNavigationState getPreviousNavigation() { return mPreviousNavigation; }

    //----------------------------------------
    // CurrentPage
    private String mCurrentPage;
    /**
     * set CurrentPage
     * @param pCurrentPage the CurrentPage
     */
    public void setCurrentPage(String pCurrentPage) { mCurrentPage = pCurrentPage; }
    /**
     * get CurrentPage
     * @return the CurrentPage
     */
    public String getCurrentPage() { return mCurrentPage; }

    //----------------------------------------
    // Exception
    private Exception mException;
    /**
     * set Exception
     * @param pException the Exception
     */
    public void setException(Exception pException) { mException = pException; }
    /**
     * get Exception
     * @return the Exception
     */
    public Exception getException() 
    { 
	if(mException != null) {
	    if(mException instanceof DMSAdminException)
		return ((DMSAdminException) mException).getNestedException();
	    else if(mException instanceof EJBException) {
		EJBException ejbException = (EJBException) mException;

		Exception rootCause = ejbException.getCausedByException();
		
		if(rootCause != null)
		    return rootCause;
		else
		    return ejbException;
	    }
	}

	return mException; 
    }

    //----------------------------------------
    // Success
    private boolean mSuccess;
    /**
     * set Success
     * @param pSuccess the Success
     */
    public void setSuccess(boolean pSuccess) { mSuccess = pSuccess; }
    /**
     * get Success
     * @return the Success
     */
    public boolean isSuccess() { return mSuccess; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof DMSAdminContext
     */
    public DMSAdminContext()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
