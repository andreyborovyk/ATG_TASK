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
 * the implementation for the DMSClient entity bean.
 * 
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id:
 *          //product/DAS/main/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule
 *          /atg/sqljmsadmin/DMSClientEJB.java#8 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSClientEJB extends DMSEntityBean {
  // ----------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSClientEJB.java#2 $$Change: 651448 $";

  // ----------------------------------------
  // Constants
  // ----------------------------------------

  // findByPrimaryKey query string
  private static final String kFindByPrimaryKeyQuery = "SELECT CLIENT_NAME FROM DMS_CLIENT WHERE CLIENT_NAME = ?";

  // find by client id
  private static final String kFindByClientIdQuery = "SELECT CLIENT_NAME FROM DMS_CLIENT WHERE CLIENT_ID = ?";

  // find by queue
  private static final String kFindByQueueQuery = "SELECT DMS_CLIENT.CLIENT_NAME FROM DMS_QUEUE_RECV, DMS_CLIENT WHERE DMS_QUEUE_RECV.QUEUE_ID = ? AND DMS_CLIENT.CLIENT_ID = DMS_QUEUE_RECV.CLIENT_ID AND DMS_QUEUE_RECV.CLIENT_ID <> ?";

  // findAll query string
  private static final String kFindAllQuery = "SELECT CLIENT_NAME FROM DMS_CLIENT";

  // remove client string
  private static final String kRemoveClient = "DELETE FROM DMS_CLIENT WHERE CLIENT_NAME = ?";

  // remove queue receiver string
  private static final String kRemoveQueueReceiver = "DELETE FROM DMS_QUEUE_RECV WHERE CLIENT_ID = ?";

  // load client string
  private static final String kLoadClient = "SELECT CLIENT_NAME, CLIENT_ID FROM DMS_CLIENT WHERE CLIENT_NAME = ?";

  // ----------------------------------------
  // Member Variables
  // ----------------------------------------

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
  // Subscriptions
  private DMSTopicSubscription[] mSubscriptions;

  /**
   * get Subscriptions
   * 
   * @return the Subscriptions
   */
  public DMSTopicSubscription[] getSubscriptions() throws DMSAdminException,
      RemoteException {
    if (mSubscriptions == null && mId != null) {
      try {
        Collection<Object> subscriptions = new ArrayList<Object>();
        for (Object subscription : mHomeFactory.getTopicSubscriptionHome()
            .findByClient(mId)) {
          subscriptions.add(PortableRemoteObject.narrow(subscription,
              DMSTopicSubscription.class));
        }

        mSubscriptions = subscriptions.toArray(new DMSTopicSubscription[0]);

      }
      catch (NamingException e) {
        throw new DMSAdminException(e);
      }
      catch (FinderException e) {
        throw new DMSAdminException(e);
      }
    }

    return mSubscriptions;
  }

  // ----------------------------------------
  // Queues
  private DMSQueue[] mQueues;

  /**
   * get Queues
   * 
   * @return the Queues
   */
  public DMSQueue[] getQueues() throws DMSAdminException, RemoteException {
    if (mQueues == null && mId != null) {
      try {
        Collection<Object> queues = new ArrayList<Object>();
        for (Object queue : mHomeFactory.getQueueHome().findByClient(mId)) {
          queues.add(PortableRemoteObject.narrow(queue, DMSQueue.class));
        }

        mQueues = queues.toArray(new DMSQueue[0]);
      }
      catch (NamingException e) {
        throw new DMSAdminException(e);
      }
      catch (FinderException e) {
        throw new DMSAdminException(e);
      }
    }

    return mQueues;
  }

  // ----------------------------------------
  // Constructors
  // ----------------------------------------

  // ----------------------------------------
  /**
   * Constructs an instanceof DMSClientEJB
   */
  public DMSClientEJB() {
    // Empty Method
  }

  // ----------------------------------------
  // Object Methods
  // ----------------------------------------

  // ----------------------------------------
  /**
   * find by primary key
   */
  public String ejbFindByPrimaryKey(String pPrimaryKey) throws FinderException,
      DMSAdminException {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet results = null;

    try {
      connection = getConnection();
      statement = connection.prepareStatement(kFindByPrimaryKeyQuery);
      statement.setString(1, pPrimaryKey);

      // perform the query - return the name, or
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
   * find by client id
   */
  public String ejbFindByClientId(Long pClientId) throws FinderException,
      DMSAdminException {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet results = null;
    String primaryKey = null;

    try {
      connection = getConnection();
      statement = connection.prepareStatement(kFindByClientIdQuery);
      statement.setLong(1, pClientId.longValue());

      // perform the query - return the name, or
      // an ObjectNotFoundException
      results = statement.executeQuery();

      if (!results.next()) {
        throw new ObjectNotFoundException();
      }

      primaryKey = results.getString(1);
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

    return primaryKey;
  }

  // ----------------------------------------
  /**
   * find by queue, except for the givin client id
   */
  public Collection ejbFindByQueue(Long pQueueId, Long pClientId)
      throws FinderException, DMSAdminException, RemoteException {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet results = null;
    ArrayList clientKeys = new ArrayList(11);

    try {
      connection = getConnection();
      statement = connection.prepareStatement(kFindByQueueQuery);
      statement.setLong(1, pQueueId.longValue());
      statement.setLong(2, pClientId.longValue());

      // perform the query - return the name, or
      // an ObjectNotFoundException
      results = statement.executeQuery();

      while (results.next()) {
        String clientName = results.getString(1);
        clientKeys.add(clientName);
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

    return clientKeys;
  }

  // ----------------------------------------
  /**
   * find all clients
   */
  public Collection ejbFindAll() throws FinderException, DMSAdminException {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet results = null;
    ArrayList allClientKeys = new ArrayList(11);

    try {
      connection = getConnection();
      statement = connection.prepareStatement(kFindAllQuery);
      results = statement.executeQuery();

      // put all the client primary keys in a collection
      while (results.next()) {
        allClientKeys.add(results.getString(1));
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

    return allClientKeys;
  }

  // ----------------------------------------
  /**
   * remove this bean and any subscriptions and queues associated with it
   */
  public void ejbRemove() throws EJBException, RemoveException, RemoteException {
    Connection connection = null;
    PreparedStatement statement = null;
    PreparedStatement deleteQueueReceiverStatement = null;

    try {
      // ugh, load in the properties, just so we can delete them.
      // this would probably be more effecient in sql
      ejbLoad();

      // remove all subscriptions associated with this client
      getSubscriptions();
      for (int i = 0; i < mSubscriptions.length; i++) {
        mSubscriptions[i].remove();
      }

      // remove all queues associated with this client
      // unless there are other clients associated with the queue
      DMSClientHome clientHome = (DMSClientHome) mEntityContext.getEJBObject()
          .getEJBHome();
      getQueues();
      for (int i = 0; i < mQueues.length; i++) {
        if (clientHome.findByQueue(mQueues[i].getId(), mId).size() == 0) {
          mQueues[i].remove();
        }
      }

      connection = getConnection();

      // remove client receivers
      deleteQueueReceiverStatement = connection
          .prepareStatement(kRemoveQueueReceiver);
      deleteQueueReceiverStatement.setLong(1, mId.longValue());
      deleteQueueReceiverStatement.executeUpdate();

      // remove this client
      statement = connection.prepareStatement(kRemoveClient);
      statement.setString(1, mName);
      statement.executeUpdate();
    }
    catch (FinderException e) {
      throw new EJBException(e);
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
        if (statement != null) {
          statement.close();
        }
        if (deleteQueueReceiverStatement != null) {
          deleteQueueReceiverStatement.close();
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
      String id = (String) mEntityContext.getPrimaryKey();
      connection = getConnection();
      statement = connection.prepareStatement(kLoadClient);
      statement.setString(1, id);
      results = statement.executeQuery();

      // if no client with given id, throw an exception
      if (!results.next()) {
        throw new EJBException(MessageFormat.format(
            Constants.NO_ENTITY_WITH_ID, new Object[] { "DMSClient", "" + id }));
      }

      mName = results.getString(1);
      mId = new Long(results.getLong(2));
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
    // this is a read-only bean. don't store any changes.
  }

  // ----------------------------------------
  /**
   * clear the state of this bean
   */
  protected void clearState() {
    mId = null;
    mName = null;
    mSubscriptions = null;
    mQueues = null;
  }

  // ----------------------------------------
  // Static Methods
  // ----------------------------------------

} // end of class
