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

package atg.sqljmsadmin;

import java.rmi.*;
import java.sql.*;
import javax.ejb.*;
import javax.sql.*;
import javax.naming.*;

import java.util.Collection;
import java.util.ArrayList;
import java.text.MessageFormat;

/****************************************
 * the implementation for the DMSTopicEntry entity bean
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSTopicEntryEJB.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSTopicEntryEJB
    extends DMSEntityBean
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSTopicEntryEJB.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    // find by primary key query
    public static final String kFindByPrimaryKeyQuery =
	"SELECT SUBSCRIBER_ID, MSG_ID FROM DMS_TOPIC_ENTRY WHERE SUBSCRIBER_ID = ? AND MSG_ID = ?";

    // find by subscription query
    public static final String kFindBySubscriptionQuery =
	"SELECT SUBSCRIBER_ID, MSG_ID FROM DMS_TOPIC_ENTRY WHERE SUBSCRIBER_ID = ?";

    // find by message query
    public static final String kFindByMessageQuery =
	"SELECT SUBSCRIBER_ID, MSG_ID FROM DMS_TOPIC_ENTRY WHERE MSG_ID = ?";

    // remove topic entry
    public static final String kRemoveTopicEntry =
	"DELETE FROM DMS_TOPIC_ENTRY WHERE SUBSCRIBER_ID = ? AND MSG_ID = ?";

    // load topic entry
    public static final String kLoadTopicEntry =
	"SELECT SUBSCRIBER_ID, MSG_ID, DELIVERY_DATE, READ_STATE FROM DMS_TOPIC_ENTRY WHERE SUBSCRIBER_ID = ? AND MSG_ID = ?";

    // create a topic entry
    public static final String kCreateTopicEntry =
	"INSERT INTO DMS_TOPIC_ENTRY (SUBSCRIBER_ID, MSG_ID, DELIVERY_DATE, READ_STATE) VALUES (?,?,?,?)";

    // undelivered
    private static final long kUndelivered = (long) 0;

    // unread entry
    private static final int kUnread = 0;

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // Id
    private DMSTopicEntryPrimaryKey mId;
    /**
     * get Id
     * @return the Id
     */
    public DMSTopicEntryPrimaryKey getId() { return mId; }

    //----------------------------------------
    // Subscription
    private Long mSubscriptionId;
    private DMSTopicSubscription mSubscription;
    /**
     * get Subscription
     * @return the Subscription
     */
    public DMSTopicSubscription getSubscription() 
	throws DMSAdminException, RemoteException
    { 
	if(mSubscription == null && mSubscriptionId != null) {
	    try {
		mSubscription = 
		    mHomeFactory.getTopicSubscriptionHome().findByPrimaryKey(mSubscriptionId);
	    }
	    catch(NamingException e) {
		throw new DMSAdminException(e);
	    }
	    catch(FinderException e) {
		throw new DMSAdminException(e);
	    }
	}

	return mSubscription; 
    }

    //----------------------------------------
    // Message
    private Long mMessageId;
    private DMSMessage mMessage;
    /**
     * get Message
     * @return the Message
     */
    public DMSMessage getMessage() 
	throws DMSAdminException, RemoteException
    { 
	if(mMessage == null && mMessageId != null) {
	    try {
		mMessage = mHomeFactory.getMessageHome().findByPrimaryKey(mMessageId);
	    }
	    catch(NamingException e) {
		throw new DMSAdminException(e);
	    }
	    catch(FinderException e) {
		mMessage = null;
	    }
	}

	return mMessage; 
    }

    //----------------------------------------
    // DeliveryDate
    private Date mDeliveryDate;
    /**
     * get DeliveryDate
     * @return the DeliveryDate
     */
    public Date getDeliveryDate() { return mDeliveryDate; }

    //----------------------------------------
    // ReadState
    private int mReadState;
    /**
     * get ReadState
     * @return the ReadState
     */
    public int getReadState() { return mReadState; }

    //----------------------------------------
    // CascadeDelete
    private boolean mCascadeDelete = true;
    /**
     * set CascadeDelete
     * @param pCascadeDelete the CascadeDelete
     */
    public void setCascadeDelete(boolean pCascadeDelete) { mCascadeDelete = pCascadeDelete; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof DMSTopicEntryEJB
     */
    public DMSTopicEntryEJB()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

  //----------------------------------------
  /**
   * post create
   */
  public void ejbPostCreate(Long pTopicSubscriptionId, Long pMessageId)
  {
  }

    //----------------------------------------
    /**
     * create a topic entry
     */
    public DMSTopicEntryPrimaryKey ejbCreate(Long pTopicSubscriptionId, Long pMessageId)
	throws CreateException
    {
	Connection connection = null;
	PreparedStatement statement = null;
	
	mId = new DMSTopicEntryPrimaryKey(pTopicSubscriptionId, pMessageId); 
	mSubscriptionId = pTopicSubscriptionId;
	mMessageId = pMessageId;
	mDeliveryDate = new Date(kUndelivered);
	mReadState = kUnread;

	try {
	    DMSTopicEntryPrimaryKey primaryKey =
		ejbFindByPrimaryKey(new DMSTopicEntryPrimaryKey(pTopicSubscriptionId, pMessageId));

	    throw new DuplicateKeyException();
	}
	catch(FinderException e) {
	    if(!(e instanceof ObjectNotFoundException))
		throw new CreateException(e.getMessage());
	}
	catch(DMSAdminException e) {
	    throw new CreateException(e.getNestedException().getMessage());
	}

	try {
	    connection = getConnection();
	    statement = connection.prepareStatement(kCreateTopicEntry);
	    statement.setLong(1, pTopicSubscriptionId.longValue());
	    statement.setLong(2, pMessageId.longValue());
	    statement.setLong(3, mDeliveryDate.getTime());
	    statement.setInt(4, mReadState);
	    statement.executeUpdate();
	}
	catch(NamingException e) {
	    throw new CreateException(e.toString());
	}
	catch(SQLException e) {
	    throw new CreateException(e.toString());
	}
	finally {
	    try {
		if(statement != null)
		    statement.close();
		if(connection != null)
		    connection.close();
	    }
	    catch(SQLException e) {
		throw new CreateException(e.toString());
	    }
	}

	return mId; 
    }

    //----------------------------------------
    /**
     * find by primary key
     */
    public DMSTopicEntryPrimaryKey ejbFindByPrimaryKey(DMSTopicEntryPrimaryKey pPrimaryKey)
	throws FinderException, DMSAdminException
    {
	Connection connection = null;
	PreparedStatement statement = null;
	ResultSet results = null;

	try {
	    connection = getConnection();
	    statement = connection.prepareStatement(kFindByPrimaryKeyQuery);
	    statement.setLong(1, pPrimaryKey.getSubscriptionId().longValue());
	    statement.setLong(2, pPrimaryKey.getMessageId().longValue());

	    // perform the query - return the primary key,
	    // or an ObjectNotFoundException
	    results = statement.executeQuery();
	    
	    if(!results.next())
		throw new ObjectNotFoundException();
	}
	catch(NamingException e) {
	    throw new DMSAdminException(e);
	}
	catch(SQLException e) {
	    throw new DMSAdminException(e);
	}
	finally {
	    try {
		if(results != null)
		    results.close();
		if(statement != null)
		    statement.close();
		if(connection != null)
		    connection.close();
	    }
	    catch(SQLException e) {
		throw new DMSAdminException(e);
	    }
	}
	
	return pPrimaryKey;
    }

    //----------------------------------------
    /**
     * find by subscription
     */
    public Collection ejbFindBySubscription(Long pSubscriptionId)
	throws FinderException, DMSAdminException
    {
	Connection connection = null;
	PreparedStatement statement = null;
	ResultSet results = null;
	ArrayList topicEntryKeys = new ArrayList(11);

	try {
	    connection = getConnection();
	    statement = connection.prepareStatement(kFindBySubscriptionQuery);
	    statement.setLong(1, pSubscriptionId.longValue());
	    results = statement.executeQuery();

	    // put all the topic entry keys in a collection
	    while(results.next()) {
		DMSTopicEntryPrimaryKey primaryKey =
		    new DMSTopicEntryPrimaryKey(new Long(results.getLong(1)),
						new Long(results.getLong(2)));
		topicEntryKeys.add(primaryKey);
	    }
	}
	catch(NamingException e) {
	    throw new DMSAdminException(e);
	}
	catch(SQLException e) {
	    throw new DMSAdminException(e);
	}
	finally {
	    try {
		if(results != null)
		    results.close();
		if(statement != null)
		    statement.close();
		if(connection != null)
		    connection.close();
	    }
	    catch(SQLException e) {
		throw new DMSAdminException(e);
	    }
	}

	return topicEntryKeys;
    }

    //----------------------------------------
    /**
     * find by message
     */
    public Collection ejbFindByMessage(Long pMessageId)
	throws FinderException, DMSAdminException
    {
	Connection connection = null;
	PreparedStatement statement = null;
	ResultSet results = null;
	ArrayList topicEntryKeys = new ArrayList(11);

	try {
	    connection = getConnection();
	    statement = connection.prepareStatement(kFindByMessageQuery);
	    statement.setLong(1, pMessageId.longValue());
	    results = statement.executeQuery();

	    // put all the topic entry keys in a collection
	    while(results.next()) {
		DMSTopicEntryPrimaryKey primaryKey =
		    new DMSTopicEntryPrimaryKey(new Long(results.getLong(1)),
						new Long(results.getLong(2)));
		topicEntryKeys.add(primaryKey);
	    }
	}
	catch(NamingException e) {
	    throw new DMSAdminException(e);
	}
	catch(SQLException e) {
	    throw new DMSAdminException(e);
	}
	finally {
	    try {
		if(results != null)
		    results.close();
		if(statement != null)
		    statement.close();
		if(connection != null)
		    connection.close();
	    }
	    catch(SQLException e) {
		throw new DMSAdminException(e);
	    }
	}

	return topicEntryKeys;
    }

    //----------------------------------------
    /**
     * remove this bean
     */
    public void ejbRemove()
	throws EJBException, RemoveException, RemoteException
    {
	Connection connection = null;
	PreparedStatement statement = null;
	ResultSet results = null;

	try {
	    ejbLoad();

	    if(getMessage() != null) {
		// this is the last entry referring to a message, so remove the message
		if(mCascadeDelete && getMessage().getReferenceCount() == 1) {
		    mMessage.remove();
		}
		else {
		    // decrement the message reference count
		    mMessage.setReferenceCount(mMessage.getReferenceCount() - 1);
		}
	    }

	    // remove the topic entry
	    connection = getConnection();
	    statement = connection.prepareStatement(kRemoveTopicEntry);
	    statement.setLong(1, mId.getSubscriptionId().longValue());
	    statement.setLong(2, mId.getMessageId().longValue());
	    statement.executeUpdate();
	}
	catch(DMSAdminException e) {
	    throw new EJBException(e.getNestedException());
	}
	catch(NamingException e) {
	    throw new EJBException(e);
	}
	catch(SQLException e) {
	    throw new EJBException(e);
	}
	finally {
	    try {
		if(results != null)
		    results.close();
		if(statement != null)
		    statement.close();
		if(connection != null)
		    connection.close();
	    }
	    catch(SQLException e) {
		throw new EJBException(e);
	    }
	}
    }

    //----------------------------------------
    /**
     * activate this bean
     */
    public void ejbActivate()
	throws EJBException, RemoteException
    {
    }

    //----------------------------------------
    /**
     * passivate this bean
     */
    public void ejbPassivate()
	throws EJBException, RemoteException
    {
	clearState();
    }

    //----------------------------------------
    /**
     * load this bean
     */
    public void ejbLoad()
	throws EJBException, RemoteException
    {
	Connection connection = null;
	PreparedStatement statement = null;
	ResultSet results = null;

	clearState();

	try {
	    DMSTopicEntryPrimaryKey primaryKey =
		(DMSTopicEntryPrimaryKey) mEntityContext.getPrimaryKey();
	    connection = getConnection();
	    statement = connection.prepareStatement(kLoadTopicEntry);
	    statement.setLong(1, primaryKey.getSubscriptionId().longValue());
	    statement.setLong(2, primaryKey.getMessageId().longValue());
	    results = statement.executeQuery();

	    // if no topic entry with given primary key, throw an exception
	    if(!results.next())
		throw new EJBException(MessageFormat.format(Constants.NO_ENTITY_WITH_ID,
							    new Object[] { "DMSTopicEntry",
									   "" + primaryKey }));
	    
	    mId = new DMSTopicEntryPrimaryKey(new Long(results.getLong(1)),
					      new Long(results.getLong(2)));
	    mDeliveryDate = new Date(results.getLong(3));
	    mReadState = results.getInt(4);

	    // get the subscription associated with this topic entry
	    mSubscriptionId = mId.getSubscriptionId();

	    // get the message associated with this topic entry
	    mMessageId = mId.getMessageId();
	}
	catch(NamingException e) {
	    throw new EJBException(e);
	}
	catch(SQLException e) {
	    throw new EJBException(e);
	}
	finally {
	    try {
		if(results != null)
		    results.close();
		if(statement != null)
		    statement.close();
		if(connection != null)
		    connection.close();
	    }
	    catch(SQLException e) {
		throw new EJBException(e);
	    }
	}
    }

    //----------------------------------------
    /**
     * store this bean
     */
    public void ejbStore()
	throws EJBException, RemoteException
    {
	// this is a read-only bean, don't store changes
    }

    //----------------------------------------
    /**
     * clear the state of this bean
     */
    protected void clearState()
    {
	mId = null;
	mSubscriptionId = null;
	mSubscription = null;
	mMessageId = null;
	mMessage = null;
	mDeliveryDate = null;
	mReadState = 0;
	mCascadeDelete = true;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
