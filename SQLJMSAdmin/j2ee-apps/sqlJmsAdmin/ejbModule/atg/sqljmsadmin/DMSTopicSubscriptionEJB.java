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

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.ejb.RemoveException;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

/****************************************
 * the implementation for the DMSTopicSubscription entity bean.
 * 
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id:
 *          //product/DAS/main/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule
 *          /atg/sqljmsadmin/DMSTopicSubscriptionEJB.java#8 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSTopicSubscriptionEJB extends DMSEntityBean {
  // ----------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSTopicSubscriptionEJB.java#2 $$Change: 651448 $";

  // ----------------------------------------
  // Constants
  // ----------------------------------------

  // find by primary key query
  private static final String kFindByPrimaryKeyQuery = "SELECT SUBSCRIBER_ID FROM DMS_TOPIC_SUB WHERE SUBSCRIBER_ID = ?";

  // find by topic query
  private static final String kFindByTopicQuery = "SELECT SUBSCRIBER_ID FROM DMS_TOPIC_SUB WHERE TOPIC_ID = ?";

  // find by client query
  private static final String kFindByClientQuery = "SELECT SUBSCRIBER_ID FROM DMS_TOPIC_SUB WHERE CLIENT_ID = ?";

  // remove topic subscription
  private static final String kRemoveTopicSubscription = "DELETE FROM DMS_TOPIC_SUB WHERE SUBSCRIBER_ID = ?";

  // load topic subscription
  private static final String kLoadTopicSubscription = "SELECT SUBSCRIBER_ID, SUBSCRIBER_NAME, CLIENT_ID, TOPIC_ID, DURABLE FROM DMS_TOPIC_SUB WHERE SUBSCRIBER_ID = ?";

  // ----------------------------------------
  // Member Variables
  // ----------------------------------------

  // pending messages
  private DMSTopicEntry[] mPendingMessages;
  private boolean mPendingMessagesStale = true;

  // unhandled messages
  private DMSTopicEntry[] mUnhandledMessages;
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
  // Client
  private Long mClientId;
  private DMSClient mClient;

  /**
   * get Client
   * 
   * @return the Client
   */
  public DMSClient getClient() throws DMSAdminException, RemoteException {
    if (mClient == null && mClientId != null) {
      try {
        mClient = mHomeFactory.getClientHome().findByClientId(mClientId);
      }
      catch (NamingException e) {
        throw new DMSAdminException(e);
      }
      catch (FinderException e) {
        mClient = null;
      }
    }

    return mClient;
  }

  // ----------------------------------------
  // Topic
  private Long mTopicId;
  private DMSTopic mTopic;

  /**
   * get Topic
   * 
   * @return the Topic
   */
  public DMSTopic getTopic() throws DMSAdminException, RemoteException {
    if (mTopic == null && mTopicId != null) {
      try {
        mTopic = mHomeFactory.getTopicHome().findByPrimaryKey(mTopicId);
      }
      catch (NamingException e) {
        throw new DMSAdminException(e);
      }
      catch (FinderException e) {
        throw new DMSAdminException(e);
      }
    }

    return mTopic;
  }

  // ----------------------------------------
  // TopicEntries
  private DMSTopicEntry[] mTopicEntries;

  /**
   * get TopicEntries
   * 
   * @return the TopicEntries
   */
  public DMSTopicEntry[] getTopicEntries() throws DMSAdminException,
      RemoteException {
    if (mTopicEntries == null && mId != null) {
      try {
        Collection<Object> topicEntries = new ArrayList<Object>();
        for (Object topicEntry : mHomeFactory.getTopicEntryHome()
            .findBySubscription(mId)) {
          topicEntries.add(PortableRemoteObject.narrow(topicEntry,
              DMSTopicEntry.class));
        }

        mTopicEntries = topicEntries.toArray(new DMSTopicEntry[0]);
      }
      catch (NamingException e) {
        throw new DMSAdminException(e);
      }
      catch (FinderException e) {
        throw new DMSAdminException(e);
      }
    }

    return mTopicEntries;
  }

  // ----------------------------------------
  // Durable
  private boolean mDurable;

  /**
   * get Durable
   * 
   * @return the Durable
   */
  public boolean isDurable() {
    return mDurable;
  }

  // ----------------------------------------
  // Constructors
  // ----------------------------------------

  // ----------------------------------------
  /**
   * Constructs an instanceof DMSTopicSubscriptionEJB
   */
  public DMSTopicSubscriptionEJB() {

  }

  // ----------------------------------------
  // Object Methods
  // ----------------------------------------

  // ----------------------------------------
  /**
   * get the pending messages from this subscription
   */
  public DMSTopicEntry[] pendingMessages() throws RemoteException,
      DMSAdminException {
    if (mPendingMessagesStale) {
      ArrayList messages = new ArrayList(11);

      // pending messages have a read state of some value other than zero
      DMSTopicEntry[] topicEntries = getTopicEntries();

      for (int i = 0; i < topicEntries.length; i++) {
        if (topicEntries[i].getReadState() != 0) {
          messages.add(topicEntries[i]);
        }
      }

      mPendingMessages = (DMSTopicEntry[]) messages
          .toArray(new DMSTopicEntry[0]);
      mPendingMessagesStale = false;
    }

    return mPendingMessages;
  }

  // ----------------------------------------
  /**
   * get the unhandled messages from this subscription
   */
  public DMSTopicEntry[] unhandledMessages() throws RemoteException,
      DMSAdminException {
    if (mUnhandledMessagesStale) {
      ArrayList messages = new ArrayList(11);

      // unhandled messages have a read state value of zero
      DMSTopicEntry[] topicEntries = getTopicEntries();

      for (int i = 0; i < topicEntries.length; i++) {
        if (topicEntries[i].getReadState() == 0) {
          messages.add(topicEntries[i]);
        }
      }

      mUnhandledMessages = (DMSTopicEntry[]) messages
          .toArray(new DMSTopicEntry[0]);
      mUnhandledMessagesStale = false;
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
   * find by topic
   */
  public Collection ejbFindByTopic(Long pTopicId) throws DMSAdminException,
      DMSAdminException {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet results = null;
    ArrayList topicSubscriptionKeys = new ArrayList(11);

    try {
      connection = getConnection();
      statement = connection.prepareStatement(kFindByTopicQuery);
      statement.setLong(1, pTopicId.longValue());
      results = statement.executeQuery();

      // put all the topic subscription keys in a collection
      while (results.next()) {
        topicSubscriptionKeys.add(new Long(results.getLong(1)));
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

    return topicSubscriptionKeys;
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
    ArrayList topicSubscriptionKeys = new ArrayList(11);

    try {
      connection = getConnection();
      statement = connection.prepareStatement(kFindByClientQuery);
      statement.setLong(1, pClientId.longValue());
      results = statement.executeQuery();

      // put all the topic subscription keys in a collection
      while (results.next()) {
        topicSubscriptionKeys.add(new Long(results.getLong(1)));
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

    return topicSubscriptionKeys;
  }

  // ----------------------------------------
  /**
   * remove this bean
   */
  public void ejbRemove() throws EJBException, RemoveException, RemoteException {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet results = null;

    try {
      // ugh, load in the properties, just so we can delete them.
      // this would probably be more effecient in sql
      ejbLoad();

      // remove any topic entries associated with this topic
      // subscription
      getTopicEntries();
      for (int i = 0; i < mTopicEntries.length; i++) {
        mTopicEntries[i].remove();
      }

      // remove this topic subscription
      connection = getConnection();
      statement = connection.prepareStatement(kRemoveTopicSubscription);
      statement.setLong(1, mId.longValue());
      statement.executeUpdate();
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
      statement = connection.prepareStatement(kLoadTopicSubscription);
      statement.setLong(1, id.longValue());
      results = statement.executeQuery();

      // if no topic subscription with given id, throw an exception
      if (!results.next()) {
        throw new EJBException(MessageFormat.format(
            Constants.NO_ENTITY_WITH_ID, new Object[] { "DMSTopicSubscription",
                "" + id }));
      }

      mId = new Long(results.getLong(1));
      mName = results.getString(2);
      mClientId = new Long(results.getLong(3));
      mTopicId = new Long(results.getLong(4));
      mDurable = results.getBoolean(5);

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
    mClientId = null;
    mClient = null;
    mTopicId = null;
    mTopic = null;
    mTopicEntries = null;
    mDurable = false;
    mPendingMessagesStale = true;
    mUnhandledMessagesStale = true;
  }

  // ----------------------------------------
  // Static Methods
  // ----------------------------------------

} // end of class
