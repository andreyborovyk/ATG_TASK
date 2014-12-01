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
 * the implementation for the DMSQueueEntry entity bean.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSQueueEntryEJB.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSQueueEntryEJB
    extends DMSEntityBean
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSQueueEntryEJB.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    // find by primary key
    private static final String kFindByPrimaryKeyQuery =
	"SELECT QUEUE_ID, MSG_ID FROM DMS_QUEUE_ENTRY WHERE QUEUE_ID = ? AND MSG_ID = ?";

    // find by queue query
    private static final String kFindByQueueQuery =
	"SELECT QUEUE_ID, MSG_ID FROM DMS_QUEUE_ENTRY WHERE QUEUE_ID = ?";

    // find by message query
    private static final String kFindByMessageQuery =
	"SELECT QUEUE_ID, MSG_ID FROM DMS_QUEUE_ENTRY WHERE MSG_ID = ?";

    // remove queue entry
    private static final String kRemoveQueueEntry =
	"DELETE FROM DMS_QUEUE_ENTRY WHERE QUEUE_ID = ? AND MSG_ID = ?";

    // load queue entry
    private static final String kLoadQueueEntry = 
	"SELECT QUEUE_ID, MSG_ID, DELIVERY_DATE, HANDLING_CLIENT_ID, READ_STATE FROM DMS_QUEUE_ENTRY WHERE QUEUE_ID = ? AND MSG_ID = ?";

    // create queue entry
    private static final String kCreateQueueEntry =
	"INSERT INTO DMS_QUEUE_ENTRY (QUEUE_ID, MSG_ID, DELIVERY_DATE, HANDLING_CLIENT_ID, READ_STATE) VALUES (?,?,?,?,?)";

    // undelivered
    private static final long kUndelivered = (long) 0;

    // unhandled entry
    private static final long kUnhandled = (long) -1;

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
    private DMSQueueEntryPrimaryKey mId;
    /**
     * get Id
     * @return the Id
     */
    public DMSQueueEntryPrimaryKey getId() { return mId; }

    //----------------------------------------
    // Queue
    private Long mQueueId;
    private DMSQueue mQueue;
    /**
     * get Queue
     * @return the Queue
     */
    public DMSQueue getQueue() 
	throws DMSAdminException, RemoteException
    { 
	if(mQueue == null && mQueueId != null) {
	   try {
	       mQueue = mHomeFactory.getQueueHome().findByPrimaryKey(mQueueId);
	   }
	   catch(NamingException e) {
	       throw new DMSAdminException(e);
	   }
	   catch(FinderException e) {
	       throw new DMSAdminException(e);
	   }
	}

	return mQueue; 
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
	throws RemoteException, DMSAdminException
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
    // Client
    private Long mClientId;
    private DMSClient mClient;
    /**
     * get Client
     * @return the Client
     */
    public DMSClient getClient() 
	throws DMSAdminException, RemoteException
    { 
	if(mClient == null && mClientId != null) {
	    try {
		mClient = mHomeFactory.getClientHome().findByClientId(mClientId);
	    }
	    catch(NamingException e) {
		throw new DMSAdminException(e);
	    }
	    catch(FinderException e) {
		mClient = null;
	    }
	}

	return mClient; 
    }

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
     * Constructs an instanceof DMSQueueEntryEJB
     */
    public DMSQueueEntryEJB()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

  //----------------------------------------
  /**
   * post create
   */
  public void ejbPostCreate(Long pQueueId, Long pMessageId)
  {
  }

    //----------------------------------------
    /**
     * create a queue entry
     */
    public DMSQueueEntryPrimaryKey ejbCreate(Long pQueueId, Long pMessageId)
	throws CreateException
    {
	Connection connection = null;
	PreparedStatement statement = null;

	mId = new DMSQueueEntryPrimaryKey(pQueueId, pMessageId);
	mQueueId = pQueueId;
	mMessageId = pMessageId;
	mDeliveryDate = new Date(kUndelivered);
	mClientId = new Long(kUnhandled);
	mReadState = kUnread;
	

	try {
	    DMSQueueEntryPrimaryKey primaryKey =
		ejbFindByPrimaryKey(new DMSQueueEntryPrimaryKey(pQueueId, pMessageId));

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
	    statement = connection.prepareStatement(kCreateQueueEntry);
	    statement.setLong(1, mQueueId.longValue());
	    statement.setLong(2, mMessageId.longValue());
	    statement.setLong(3, mDeliveryDate.getDate());
	    statement.setLong(4, mClientId.longValue());
	    statement.setInt(5, mReadState);
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
    public DMSQueueEntryPrimaryKey ejbFindByPrimaryKey(DMSQueueEntryPrimaryKey pPrimaryKey)
	throws FinderException, DMSAdminException
    {
	Connection connection = null;
	PreparedStatement statement = null;
	ResultSet results = null;

	try {
	    connection = getConnection();
	    statement = connection.prepareStatement(kFindByPrimaryKeyQuery);
	    statement.setLong(1, pPrimaryKey.getQueueId().longValue());
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
     * find by queue
     */
    public Collection ejbFindByQueue(Long pQueueId)
	throws FinderException, DMSAdminException
    {
	Connection connection = null;
	PreparedStatement statement = null;
	ResultSet results = null;
	ArrayList queueEntryKeys = new ArrayList(11);

	try {
	    connection = getConnection();
	    statement = connection.prepareStatement(kFindByQueueQuery);
	    statement.setLong(1, pQueueId.longValue());
	    results = statement.executeQuery();

	    // put all the queue entry primary keys in a collection
	    while(results.next()) {
		DMSQueueEntryPrimaryKey primaryKey = 
		    new DMSQueueEntryPrimaryKey(new Long(results.getLong(1)),
						new Long(results.getLong(2)));
		queueEntryKeys.add(primaryKey);
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

	return queueEntryKeys;
    }

    //----------------------------------------
    /**
     * find by message
     */
    public DMSQueueEntryPrimaryKey ejbFindByMessage(Long pMessageId)
	throws FinderException, DMSAdminException
    {
	Connection connection = null;
	PreparedStatement statement = null;
	ResultSet results = null;
	DMSQueueEntryPrimaryKey primaryKey = null;

	try {
	    connection = getConnection();
	    statement = connection.prepareStatement(kFindByMessageQuery);
	    statement.setLong(1, pMessageId.longValue());
	    results = statement.executeQuery();

	    // if no queue entry associated with this message, throw
	    // an exception
	    if(!results.next())
		throw new ObjectNotFoundException();

	    primaryKey = new DMSQueueEntryPrimaryKey(new Long(results.getLong(1)),
						     new Long(results.getLong(2)));
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

	return primaryKey;
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
	    // remove the message assocated with this queue entry
	    if(mCascadeDelete == true) {
		ejbLoad();

		try {
		    if(getMessage() != null) {
			mMessage.remove();
		    }
		}
		catch(DMSAdminException e) {
		    if(! (e.getNestedException() instanceof FinderException))
			throw e;
		}
	    }
	    else {
		mId = (DMSQueueEntryPrimaryKey) mEntityContext.getPrimaryKey();
	    }

	    // remove this queue entry
	    connection = getConnection();
	    statement = connection.prepareStatement(kRemoveQueueEntry);
	    statement.setLong(1, mId.getQueueId().longValue());
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
	catch(Exception e) {
	    e.printStackTrace();
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
	    DMSQueueEntryPrimaryKey primaryKey = 
		(DMSQueueEntryPrimaryKey) mEntityContext.getPrimaryKey();
	    connection = getConnection();
	    statement = connection.prepareStatement(kLoadQueueEntry);
	    statement.setLong(1, primaryKey.getQueueId().longValue());
	    statement.setLong(2, primaryKey.getMessageId().longValue());
	    results = statement.executeQuery();

	    // if no queue entry with given primary key, throw an exception
	    if(!results.next())
		throw new EJBException(MessageFormat.format(Constants.NO_ENTITY_WITH_ID,
							    new Object[] { "DMSQueueEntry",
									   "" + primaryKey }));
	    
	    mId = new DMSQueueEntryPrimaryKey(new Long(results.getLong(1)),
					      new Long(results.getLong(2)));
	    mDeliveryDate = new Date(results.getLong(3));
	    mClientId = new Long(results.getLong(4));
	    mReadState = results.getInt(5);

	    // get the queue associated with this queue entry
	    mQueueId = mId.getQueueId();
	    
	    // get the message associated with this queue entry
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
	// this is a read-only bean, don't store any changes
    }

    //----------------------------------------
    /**
     * clear the state of this bean
     */
    protected void clearState()
    {
	mId = null;
	mQueueId = null;
	mQueue = null;
	mMessageId = null;
	mMessage = null;
	mDeliveryDate = null;
	mClientId = null;
	mClient = null;
	mReadState = 0;
	mCascadeDelete = true;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
