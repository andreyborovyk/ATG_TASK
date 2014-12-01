/*
 * <ATGCOPYRIGHT> Copyright (C) 1997-2011 Art Technology Group, Inc. All Rights
 * Reserved. No use, copying or distribution of this work may be made except in
 * accordance with a valid license agreement from Art Technology Group. This
 * notice must be included on all copies, modifications and derivatives of this
 * work. Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT
 * THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES. "Dynamo" is a trademark of Art Technology
 * Group, Inc. </ATGCOPYRIGHT>
 */

package atg.sqljmsadmin;

import atg.j2ee.idgen.IdGenerator;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.ejb.RemoveException;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

/****************************************
 * the implementation for the DMSQueue entity bean.
 * 
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id:
 *          //product/DAS/main/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule
 *          /atg/sqljmsadmin/DMSQueueEJB.java#8 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSQueueEJB extends DMSEntityBean {
  // ----------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSQueueEJB.java#2 $$Change: 651448 $";

  // ----------------------------------------
  // Constants
  // ----------------------------------------

  // findByPrimaryKey query string
  private static final String kFindByPrimaryKeyQuery = "SELECT QUEUE_ID FROM DMS_QUEUE WHERE QUEUE_ID = ?";

  // findByClient query string
  private static final String kFindByClientQuery = "SELECT QUEUE_ID FROM DMS_QUEUE_RECV WHERE CLIENT_ID = ?";

  // findAll query string
  private static final String kFindAllQuery = "SELECT QUEUE_ID FROM DMS_QUEUE";

  // remove queue string
  private static final String kRemoveQueue = "DELETE FROM DMS_QUEUE WHERE QUEUE_ID = ?";

  // remove the queue association
  private static final String kRemoveQueueAssociation = "DELETE FROM DMS_QUEUE_RECV WHERE QUEUE_ID = ?";

  // load queue string
  private static final String kLoadQueue = "SELECT QUEUE_ID, QUEUE_NAME, TEMP_ID FROM DMS_QUEUE WHERE QUEUE_ID = ?";

  // id space key for message ids in the IdGenerator service
  private static final String kMessageIdSpaceKey = "jms_msg_ids";

  // ----------------------------------------
  // Member Variables
  // ----------------------------------------

  // pending messages
  private DMSQueueEntry[] mPendingMessages;
  private boolean mPendingMessagesStale = true;

  // unhandled messages
  private DMSQueueEntry[] mUnhandledMessages;
  private boolean mUnhandledMessagesStale = true;

  // ----------------------------------------
  // Properties
  // ----------------------------------------

  // ----------------------------------------
  // Id
  private Long mId;

  /**
   * get Id
   * 
   * @return the Id
   */
  public Long getId() {
    return mId;
  }

  // ----------------------------------------
  // Name
  private String mName;

  /**
   * get Name
   * 
   * @return the Name
   */
  public String getName() {
    return mName;
  }

  // ----------------------------------------
  // TempId
  private long mTempId;

  /**
   * get TempId
   * 
   * @return the TempId
   */
  public long getTempId() {
    return mTempId;
  }

  // ----------------------------------------
  // QueueEntries
  private DMSQueueEntry[] mQueueEntries;

  /**
   * get QueueEntries
   * 
   * @return the QueueEntries
   */
  public DMSQueueEntry[] getQueueEntries() throws DMSAdminException,
      RemoteException {
    if (mQueueEntries == null && mId != null) {
      try {
        Collection<Object> queueEntries = new ArrayList<Object>();
        for (Object queueEntry : mHomeFactory.getQueueEntryHome().findByQueue(
            mId)) {
          queueEntries.add(PortableRemoteObject.narrow(queueEntry,
              DMSQueueEntry.class));
        }

        mQueueEntries = queueEntries.toArray(new DMSQueueEntry[0]);
      }
      catch (NamingException e) {
        throw new DMSAdminException(e);
      }
      catch (FinderException e) {
        throw new DMSAdminException(e);
      }
    }

    return mQueueEntries;
  }

  // ----------------------------------------
  // Constructors
  // ----------------------------------------

  // ----------------------------------------
  /**
   * Constructs an instanceof DMSQueueEJB
   */
  public DMSQueueEJB() {

  }

  // ----------------------------------------
  // Object Methods
  // ----------------------------------------

  // ----------------------------------------
  /**
   * add some queue entries to this topic
   */
  public void addQueueEntries(Long[] pMessageIds, boolean pDuplicateMessages)
      throws RemoteException, DMSAdminException {
    try {
      DMSQueueEntryHome queueEntryHome = mHomeFactory.getQueueEntryHome();
      DMSMessageHome messageHome = mHomeFactory.getMessageHome();
      IdGenerator idGenerator = mHomeFactory.getIdGeneratorHome().create();

      // add all the messages
      for (int j = 0; j < pMessageIds.length; j++) {
        // find the original message
        DMSMessage message = null;
        Long messageId = pMessageIds[j];

        try {
          message = messageHome.findByPrimaryKey(messageId);
        }
        catch (FinderException e) {
        }

        // if we're moving from a topic to this queue,
        // we should duplicate the message. if there is no message,
        // don't worry about copying it.
        if (message != null) {
          if (pDuplicateMessages) {
            Long idForNewMessage = idGenerator
                .generateLongId(kMessageIdSpaceKey);

            DMSMessage messageCopy = messageHome.create(idForNewMessage,
                message.getMessageClass(), message.isHasProperties(), 1,
                message.getTimestamp(), message.getCorrelationId(), message
                    .getReplyTo(), message.getDestination(), message
                    .getDeliveryMode(), message.isRedelivered(), message
                    .getType(), message.getExpiration(), message.getPriority(),
                message.getSmallBody(), message.getLargeBody(), message
                    .getProperties());

            message = messageCopy;
            messageId = message.getId();
          }

          message.setDestination(getId());
        }

        queueEntryHome.create(getId(), messageId);
      }
    }
    catch (CreateException e) {
      throw new DMSAdminException(e);
    }
    catch (NamingException e) {
      throw new DMSAdminException(e);
    }
  }

  // ----------------------------------------
  /**
   * get the pending messages from this queue
   */
  public DMSQueueEntry[] pendingMessages() throws RemoteException,
      DMSAdminException {
    if (mPendingMessagesStale) {
      ArrayList messages = new ArrayList(11);

      DMSQueueEntry[] queueEntries = getQueueEntries();

      // pending messages have a read state value of something other than zero
      for (int i = 0; i < queueEntries.length; i++) {
        if (queueEntries[i].getReadState() != 0) {
          messages.add(queueEntries[i]);
        }
      }

      mPendingMessages = (DMSQueueEntry[]) messages
          .toArray(new DMSQueueEntry[0]);
    }

    return mPendingMessages;
  }

  // ----------------------------------------
  /**
   * get the unhandled messages from this queue
   */
  public DMSQueueEntry[] unhandledMessages() throws RemoteException,
      DMSAdminException {
    if (mUnhandledMessagesStale) {
      ArrayList messages = new ArrayList(11);

      DMSQueueEntry[] queueEntries = getQueueEntries();

      // unhandled messages have a read state value of zero
      for (int i = 0; i < queueEntries.length; i++) {
        if (queueEntries[i].getReadState() == 0) {
          messages.add(queueEntries[i]);
        }
      }

      mUnhandledMessages = (DMSQueueEntry[]) messages
          .toArray(new DMSQueueEntry[0]);
    }

    return mUnhandledMessages;
  }

  // ----------------------------------------
  /**
   * find by primary key
   */
  public Long ejbFindByPrimaryKey(Long pPrimaryKey) throws FinderException,
      DMSAdminException {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet results = null;

    try {
      connection = getConnection();
      statement = connection.prepareStatement(kFindByPrimaryKeyQuery);
      statement.setLong(1, pPrimaryKey.longValue());

      // perform the query - return the id, or
      // an ObjectNotFoundException
      results = statement.executeQuery();

      if (!results.next()) {
        throw new ObjectNotFoundException();
      }
    }
    catch (NamingException e) {
      throw new DMSAdminException(e);
    }
    catch (SQLException e) {
      throw new DMSAdminException(e);
    }
    finally {
      try {
        if (results != null) {
          results.close();
        }
        if (statement != null) {
          statement.close();
        }
        if (connection != null) {
          connection.close();
        }
      }
      catch (SQLException e) {
        throw new DMSAdminException(e);
      }
    }

    return pPrimaryKey;
  }

  // ----------------------------------------
  /**
   * find by client
   */
  public Collection ejbFindByClient(Long pClientId) throws FinderException,
      DMSAdminException {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet results = null;
    HashSet queueKeys = new HashSet(11);

    try {
      connection = getConnection();
      statement = connection.prepareStatement(kFindByClientQuery);
      statement.setLong(1, pClientId.longValue());
      results = statement.executeQuery();

      // put all the queue ids in a set to avoid duplicates
      while (results.next()) {
        queueKeys.add(new Long(results.getLong(1)));
      }
    }
    catch (NamingException e) {
      throw new DMSAdminException(e);
    }
    catch (SQLException e) {
      throw new DMSAdminException(e);
    }
    finally {
      try {
        if (results != null) {
          results.close();
        }
        if (statement != null) {
          statement.close();
        }
        if (connection != null) {
          connection.close();
        }
      }
      catch (SQLException e) {
        throw new DMSAdminException(e);
      }
    }

    return queueKeys;
  }

  // ----------------------------------------
  /**
   * find all
   */
  public Collection ejbFindAll() throws FinderException, DMSAdminException {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet results = null;
    ArrayList allQueueKeys = new ArrayList(11);

    try {
      connection = getConnection();
      statement = connection.prepareStatement(kFindAllQuery);
      results = statement.executeQuery();

      // put all the queue keys into a collection
      while (results.next()) {
        allQueueKeys.add(new Long(results.getLong(1)));
      }
    }
    catch (NamingException e) {
      throw new DMSAdminException(e);
    }
    catch (SQLException e) {
      throw new DMSAdminException(e);
    }
    finally {
      try {
        if (results != null) {
          results.close();
        }
        if (statement != null) {
          statement.close();
        }
        if (connection != null) {
          connection.close();
        }
      }
      catch (SQLException e) {
        throw new DMSAdminException(e);
      }
    }

    return allQueueKeys;
  }

  // ----------------------------------------
  /**
   * remove this bean
   */
  public void ejbRemove() throws EJBException, RemoveException, RemoteException {
    Connection connection = null;
    PreparedStatement queueRemoveStatement = null;
    PreparedStatement associationRemoveStatement = null;
    ResultSet results = null;

    try {
      // ugh, load in the properties, just so we can delete them.
      // this would probably be more effecient in sql
      ejbLoad();

      // remove all queue entries associated with this queue
      getQueueEntries();
      for (int i = 0; i < mQueueEntries.length; i++) {
        mQueueEntries[i].remove();
      }

      // remove this queue
      connection = getConnection();
      queueRemoveStatement = connection.prepareStatement(kRemoveQueue);
      associationRemoveStatement = connection
          .prepareStatement(kRemoveQueueAssociation);

      associationRemoveStatement.setLong(1, mId.longValue());
      associationRemoveStatement.executeUpdate();

      queueRemoveStatement.setLong(1, mId.longValue());
      queueRemoveStatement.executeUpdate();
    }
    catch (DMSAdminException e) {
      throw new EJBException(e);
    }
    catch (NamingException e) {
      throw new EJBException(e);
    }
    catch (SQLException e) {
      throw new EJBException(e);
    }
    finally {
      try {
        if (results != null) {
          results.close();
        }
        if (queueRemoveStatement != null) {
          queueRemoveStatement.close();
        }
        if (associationRemoveStatement != null) {
          associationRemoveStatement.close();
        }
        if (connection != null) {
          connection.close();
        }
      }
      catch (SQLException e) {
        throw new EJBException(e);
      }
    }
  }

  // ----------------------------------------
  /**
   * activate this bean
   */
  public void ejbActivate() throws EJBException, RemoteException {
  }

  // ----------------------------------------
  /**
   * passivate this bean
   */
  public void ejbPassivate() throws EJBException, RemoteException {
    clearState();
  }

  // ----------------------------------------
  /**
   * load this bean
   */
  public void ejbLoad() throws EJBException, RemoteException {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet results = null;

    clearState();

    try {
      Long id = (Long) mEntityContext.getPrimaryKey();
      connection = getConnection();
      statement = connection.prepareStatement(kLoadQueue);
      statement.setLong(1, id.longValue());
      results = statement.executeQuery();

      // if no queue with the given id, throw an exception
      if (!results.next()) {
        throw new EJBException(MessageFormat.format(
            Constants.NO_ENTITY_WITH_ID, new Object[] { "DMSQueue", "" + id }));
      }

      mId = new Long(results.getLong(1));
      mName = results.getString(2);
      mTempId = results.getLong(3);

      mPendingMessagesStale = true;
      mUnhandledMessagesStale = true;
    }
    catch (NamingException e) {
      throw new EJBException(e);
    }
    catch (SQLException e) {
      throw new EJBException(e);
    }
    finally {
      try {
        if (results != null) {
          results.close();
        }
        if (statement != null) {
          statement.close();
        }
        if (connection != null) {
          connection.close();
        }
      }
      catch (SQLException e) {
        throw new EJBException(e);
      }
    }
  }

  // ----------------------------------------
  /**
   * store this bean
   */
  public void ejbStore() throws EJBException, RemoteException {
    // this is a read-only bean, don't store any changes
  }

  // ----------------------------------------
  /**
   * clear the state of this bean
   */
  protected void clearState() {
    mId = null;
    mName = null;
    mTempId = 0;
    mQueueEntries = null;
    mPendingMessagesStale = true;
    mUnhandledMessagesStale = true;
  }

  // ----------------------------------------
  // Static Methods
  // ----------------------------------------

} // end of class
