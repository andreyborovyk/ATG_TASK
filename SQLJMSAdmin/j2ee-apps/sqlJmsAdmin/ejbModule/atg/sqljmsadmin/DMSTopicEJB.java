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

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.ejb.RemoveException;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

/****************************************
 * the implementation for the DMSTopic entity bean
 * 
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id:
 *          //product/DAS/main/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule
 *          /atg/sqljmsadmin/DMSTopicEJB.java#8 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSTopicEJB extends DMSEntityBean {

  /** serialVersionUID - long */
  private static final long serialVersionUID = 1L;

  // ----------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSTopicEJB.java#2 $$Change: 651448 $";

  // ----------------------------------------
  // Constants
  // ----------------------------------------

  // find by primary key query
  private static final String kFindByPrimaryKeyQuery = "SELECT TOPIC_ID FROM DMS_TOPIC WHERE TOPIC_ID = ?";

  // find all query
  private static final String kFindAllQuery = "SELECT TOPIC_ID FROM DMS_TOPIC";

  // remove this topic
  private static final String kRemoveTopic = "DELETE FROM DMS_TOPIC WHERE TOPIC_ID = ?";

  // load this topic
  private static final String kLoadTopic = "SELECT TOPIC_ID, TOPIC_NAME, TEMP_ID FROM DMS_TOPIC WHERE TOPIC_ID = ?";

  // id space key for message ids in the IdGenerator service
  private static final String kMessageIdSpaceKey = "jms_msg_ids";

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

        Collection<Object> topics = new ArrayList<Object>();
        for (Object object : mHomeFactory.getTopicSubscriptionHome()
            .findByTopic(mId)) {
          try {
            topics.add(PortableRemoteObject.narrow(object,
                DMSTopicSubscription.class));
          }
          catch (ClassCastException e) {
            e.printStackTrace();
          }
        }

        mSubscriptions = topics.toArray(new DMSTopicSubscription[0]);
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
  // Constructors
  // ----------------------------------------

  // ----------------------------------------
  /**
   * Constructs an instanceof DMSTopicEJB
   */
  public DMSTopicEJB() {
    // Empty Constructor Method
  }

  // ----------------------------------------
  // Object Methods
  // ----------------------------------------

  // ----------------------------------------
  /**
   * add some topic entries to this topic
   */
  public void addTopicEntries(Long[] pMessageIds, boolean pDuplicateMessages)
      throws RemoteException, DMSAdminException {
    getSubscriptions();

    try {
      DMSTopicEntryHome topicEntryHome = mHomeFactory.getTopicEntryHome();
      DMSMessageHome messageHome = mHomeFactory.getMessageHome();
      IdGenerator idGenerator = mHomeFactory.getIdGeneratorHome().create();

      // add one entry for each subscription in the topic
      for (int j = 0; j < pMessageIds.length; j++) {

        // find the original message
        DMSMessage message = null;
        Long messageId = pMessageIds[j];

        try {
          message = messageHome.findByPrimaryKey(messageId);
        }
        catch (FinderException e) {
          // Empty block
        }

        // if we're moving from another topic to this topic,
        // we should create a new message for all the topic entries
        // in this topic to refer to

        if (message != null) {
          if (pDuplicateMessages) {
            DMSMessage messageCopy = messageHome.create(idGenerator
                .generateLongId(kMessageIdSpaceKey), message.getMessageClass(),
                message.isHasProperties(), message.getReferenceCount(), message
                    .getTimestamp(), message.getCorrelationId(), message
                    .getReplyTo(), message.getDestination(), message
                    .getDeliveryMode(), message.isRedelivered(), message
                    .getType(), message.getExpiration(), message.getPriority(),
                message.getSmallBody(), message.getLargeBody(), message
                    .getProperties());

            message = messageCopy;
            messageId = message.getId();
          }

          message.setDestination(getId());
          message.setReferenceCount(mSubscriptions.length);
        }

        for (int i = 0; i < mSubscriptions.length; i++) {
          topicEntryHome.create(mSubscriptions[i].getId(), messageId);
        }
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
   * find all
   */
  public Collection<Long> ejbFindAll() throws FinderException,
      DMSAdminException {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet results = null;
    ArrayList<Long> allTopicKeys = new ArrayList<Long>(11);

    try {
      connection = getConnection();
      statement = connection.prepareStatement(kFindAllQuery);
      results = statement.executeQuery();

      // put all the topic keys into a collection
      while (results.next()) {
        allTopicKeys.add(new Long(results.getLong(1)));
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

    return allTopicKeys;
  }

  // ----------------------------------------
  /**
   * remove this bean
   */
  public void ejbRemove() throws EJBException, RemoveException, RemoteException {
    Connection connection = null;
    PreparedStatement statement = null;

    try {
      // ugh, load in the properties, just so we can delete them.
      // this would probably be more effecient in sql
      ejbLoad();

      // remove all subscriptions for this topic
      getSubscriptions();
      for (int i = 0; i < mSubscriptions.length; i++) {
        mSubscriptions[i].remove();
      }

      // remove this topic
      connection = getConnection();
      statement = connection.prepareStatement(kRemoveTopic);
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
    // Empty Activation Method
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
      statement = connection.prepareStatement(kLoadTopic);
      statement.setLong(1, id.longValue());
      results = statement.executeQuery();

      // if no topic with the given id, throw an exception
      if (!results.next()) {
        throw new EJBException(MessageFormat.format(
            Constants.NO_ENTITY_WITH_ID, new Object[] { "DMSTopic", "" + id }));
      }

      mId = new Long(results.getLong(1));
      mName = results.getString(2);
      mTempId = results.getLong(3);

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
    mSubscriptions = null;
  }

  // ----------------------------------------
  // Static Methods
  // ----------------------------------------

} // end of class
