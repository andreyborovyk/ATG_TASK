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

import java.io.ByteArrayInputStream;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.Date;
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
 * implementation for the DMSMessage entity bean.
 * 
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id:
 *          //product/DAS/main/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule
 *          /atg/sqljmsadmin/DMSMessageEJB.java#8 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSMessageEJB extends DMSEntityBean {
  // ----------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSMessageEJB.java#2 $$Change: 651448 $";

  // ----------------------------------------
  // Constants
  // ----------------------------------------

  // find by primary key query
  private static final String kFindByPrimaryKeyQuery = "SELECT MSG_ID FROM DMS_MSG WHERE MSG_ID = ?";

  // remove message
  private static final String kRemoveMessage = "DELETE FROM DMS_MSG WHERE MSG_ID = ?";

  // load message
  private static final String kLoadMessage = "SELECT MSG_ID, MSG_CLASS, HAS_PROPERTIES, REFERENCE_COUNT, TIMESTAMP, CORRELATION_ID, REPLY_TO, DESTINATION, DELIVERY_MODE, REDELIVERED, TYPE, EXPIRATION, PRIORITY, SMALL_BODY, LARGE_BODY FROM DMS_MSG WHERE MSG_ID = ?";

  // create message
  private static final String kCreateMessage = "INSERT INTO DMS_MSG (MSG_ID, MSG_CLASS, HAS_PROPERTIES, REFERENCE_COUNT, TIMESTAMP, CORRELATION_ID, REPLY_TO, DESTINATION, DELIVERY_MODE, REDELIVERED, TYPE, EXPIRATION, PRIORITY, SMALL_BODY, LARGE_BODY) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

  // store message
  private static final String kStoreMessage = "UPDATE DMS_MSG SET DESTINATION=?, REFERENCE_COUNT=? WHERE MSG_ID=?";

  // ----------------------------------------
  // Member Variables
  // ----------------------------------------

  // should we store this bean?
  private boolean mSomethingChanged;

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
  // MessageClass
  private String mMessageClass;

  /**
   * get MessageClass
   * 
   * @return the MessageClass
   */
  public String getMessageClass() {
    return mMessageClass;
  }

  // ----------------------------------------
  // HasProperties
  private boolean mHasProperties;

  /**
   * get HasProperties
   * 
   * @return the HasProperties
   */
  public boolean isHasProperties() {
    return mHasProperties;
  }

  // ----------------------------------------
  // ReferenceCount
  private int mReferenceCount;

  /**
   * set the ref count
   * 
   * @param pReferenceCount the new ref count for this message
   */
  public void setReferenceCount(int pReferenceCount) {
    mReferenceCount = pReferenceCount;
    mSomethingChanged = true;
  }

  /**
   * get ReferenceCount
   * 
   * @return the ReferenceCount
   */
  public int getReferenceCount() {
    return mReferenceCount;
  }

  // ----------------------------------------
  // Timestamp
  private Date mTimestamp;

  /**
   * get Timestamp
   * 
   * @return the Timestamp
   */
  public Date getTimestamp() {
    return mTimestamp;
  }

  // ----------------------------------------
  // CorrelationId
  private String mCorrelationId;

  /**
   * get CorrelationId
   * 
   * @return the CorrelationId
   */
  public String getCorrelationId() {
    return mCorrelationId;
  }

  // ----------------------------------------
  // ReplyTo
  private Long mReplyTo;

  /**
   * get ReplyTo
   * 
   * @return the ReplyTo
   */
  public Long getReplyTo() {
    return mReplyTo;
  }

  // ----------------------------------------
  // Destination
  private Long mDestination;

  /**
   * set Destination
   * 
   * @param pDestination the new destination of this message
   */
  public void setDestination(Long pDestination) {
    mDestination = pDestination;
    mSomethingChanged = true;
  }

  /**
   * get Destination
   * 
   * @return the Destination
   */
  public Long getDestination() {
    return mDestination;
  }

  // ----------------------------------------
  // DeliveryMode
  private int mDeliveryMode;

  /**
   * get DeliveryMode
   * 
   * @return the DeliveryMode
   */
  public int getDeliveryMode() {
    return mDeliveryMode;
  }

  // ----------------------------------------
  // Redelivered
  private boolean mRedelivered;

  /**
   * get Redelivered
   * 
   * @return the Redelivered
   */
  public boolean isRedelivered() {
    return mRedelivered;
  }

  // ----------------------------------------
  // Type
  private String mType;

  /**
   * get Type
   * 
   * @return the Type
   */
  public String getType() {
    return mType;
  }

  // ----------------------------------------
  // Expiration
  private Date mExpiration;

  /**
   * get Expiration
   * 
   * @return the Expiration
   */
  public Date getExpiration() {
    return mExpiration;
  }

  // ----------------------------------------
  // Priority
  private int mPriority;

  /**
   * get Priority
   * 
   * @return the Priority
   */
  public int getPriority() {
    return mPriority;
  }

  // ----------------------------------------
  // SmallBody
  private byte[] mSmallBody;

  /**
   * get SmallBody
   * 
   * @return the SmallBody
   */
  public byte[] getSmallBody() {
    return mSmallBody;
  }

  // ----------------------------------------
  // LargeBody
  private byte[] mLargeBody;

  /**
   * get LargeBody
   * 
   * @return the LargeBody
   */
  public byte[] getLargeBody() {
    return mLargeBody;
  }

  // ----------------------------------------
  // Properties
  private DMSMessageProperty[] mProperties;

  /**
   * get Properties
   * 
   * @return the Properties
   */
  public DMSMessageProperty[] getProperties() throws DMSAdminException,
      RemoteException {
    if (mProperties == null && mId != null) {
      try {
        // load all the message properties
        Collection<Object> messageProperties = new ArrayList<Object>();
        for (Object messageProperty : mHomeFactory.getMessagePropertyHome()
            .findByMessage(mId)) {
          messageProperties.add(PortableRemoteObject.narrow(messageProperty,
              DMSMessageProperty.class));
        }

        mProperties = messageProperties.toArray(new DMSMessageProperty[0]);
      }
      catch (NamingException e) {
        throw new DMSAdminException(e);
      }
      catch (FinderException e) {
        throw new DMSAdminException(e);
      }
    }

    return mProperties;
  }

  // ----------------------------------------
  // Constructors
  // ----------------------------------------

  // ----------------------------------------
  /**
   * Constructs an instanceof DMSMessageEJB
   */
  public DMSMessageEJB() {

  }

  // ----------------------------------------
  // Object Methods
  // ----------------------------------------

  // ----------------------------------------
  /**
   * post create
   */
  public void ejbPostCreate(Long pMessageId, String pMessageClass,
      boolean pHasProperties, int pReferenceCount, Date pTimestamp,
      String pCorrelationId, Long pReplyTo, Long pDestination,
      int pDeliveryMode, boolean pRedelivered, String pType, Date pExpiration,
      int pPriority, byte[] pSmallBody, byte[] pLargeBody,
      DMSMessageProperty[] pProperties) {
  }

  // ----------------------------------------
  /**
   * create a message
   */
  public Long ejbCreate(Long pMessageId, String pMessageClass,
      boolean pHasProperties, int pReferenceCount, Date pTimestamp,
      String pCorrelationId, Long pReplyTo, Long pDestination,
      int pDeliveryMode, boolean pRedelivered, String pType, Date pExpiration,
      int pPriority, byte[] pSmallBody, byte[] pLargeBody,
      DMSMessageProperty[] pProperties) throws CreateException {
    Connection connection = null;
    PreparedStatement statement = null;

    mId = pMessageId;
    mMessageClass = pMessageClass;
    mHasProperties = pHasProperties;
    mReferenceCount = pReferenceCount;
    mTimestamp = pTimestamp;
    mCorrelationId = pCorrelationId;
    mReplyTo = pReplyTo;
    mDestination = pDestination;
    mDeliveryMode = pDeliveryMode;
    mRedelivered = pRedelivered;
    mType = pType;
    mExpiration = pExpiration;
    mPriority = pPriority;
    mSmallBody = pSmallBody;
    mLargeBody = pLargeBody;

    try {
      connection = getConnection();
      statement = connection.prepareStatement(kCreateMessage);
      statement.setLong(1, mId.longValue());
      statement.setString(2, mMessageClass);
      statement.setBoolean(3, mHasProperties);
      statement.setInt(4, mReferenceCount);

      if (pTimestamp != null) {
        statement.setLong(5, mTimestamp.getTime());
      }
      else {
        statement.setLong(5, 0);
      }

      statement.setString(6, mCorrelationId);

      if (pReplyTo != null) {
        statement.setLong(7, mReplyTo.longValue());
      }
      else {
        statement.setLong(7, 0);
      }

      if (pDestination != null) {
        statement.setLong(8, mDestination.longValue());
      }
      else {
        statement.setLong(8, 0);
      }

      statement.setInt(9, mDeliveryMode);
      statement.setBoolean(10, mRedelivered);
      statement.setString(11, mType);

      if (pExpiration != null) {
        statement.setLong(12, mExpiration.getTime());
      }
      else {
        statement.setLong(12, 0);
      }

      statement.setInt(13, mPriority);
      statement.setBytes(14, mSmallBody);

      if (pLargeBody != null) {
        statement.setBinaryStream(15, new ByteArrayInputStream(mLargeBody),
            mLargeBody.length);
      }
      else {
        statement.setBytes(15, null);
      }

      statement.executeUpdate();

      DMSMessagePropertyHome propertyHome = mHomeFactory
          .getMessagePropertyHome();

      mProperties = new DMSMessageProperty[pProperties.length];

      for (int i = 0; i < pProperties.length; i++) {
        mProperties[i] = propertyHome.create(mId, pProperties[i].getName(),
            pProperties[i].getDataType(), pProperties[i].getValue());
      }
    }
    catch (RemoteException e) {
      throw new CreateException(e.toString());
    }
    catch (NamingException e) {
      throw new CreateException(e.toString());
    }
    catch (SQLException e) {
      throw new CreateException(e.toString());
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
        throw new CreateException(e.toString());
      }
    }

    return pMessageId;
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

      // perform the query - return the primary key,
      // or an ObjectNotFoundException
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

      // remove all of this message's properties
      getProperties();
      for (int i = 0; i < mProperties.length; i++) {
        mProperties[i].remove();
      }

      // remove this message
      connection = getConnection();
      statement = connection.prepareStatement(kRemoveMessage);
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
      statement = connection.prepareStatement(kLoadMessage);
      statement.setLong(1, id.longValue());
      results = statement.executeQuery();

      // if no message with given primary key, throw an exception
      if (!results.next()) {
        throw new EJBException(MessageFormat
            .format(Constants.NO_ENTITY_WITH_ID, new Object[] { "DMSMessage",
                "" + id }));
      }

      // load the message
      mId = new Long(results.getLong(1));
      mMessageClass = results.getString(2);
      mHasProperties = results.getBoolean(3);
      mReferenceCount = results.getInt(4);
      mTimestamp = new Date(results.getLong(5));
      mCorrelationId = results.getString(6);
      mReplyTo = new Long(results.getLong(7));
      mDestination = new Long(results.getLong(8));
      mDeliveryMode = results.getInt(9);
      mRedelivered = results.getBoolean(10);
      mType = results.getString(11);
      mExpiration = new Date(results.getLong(12));
      mPriority = results.getInt(13);
      mSmallBody = results.getBytes(14);
      mLargeBody = results.getBytes(15);
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
    if (mSomethingChanged) {
      Connection connection = null;
      PreparedStatement statement = null;

      try {
        Long id = (Long) mEntityContext.getPrimaryKey();
        connection = getConnection();
        statement = connection.prepareStatement(kStoreMessage);
        statement.setLong(1, mDestination.longValue());
        statement.setInt(2, mReferenceCount);
        statement.setLong(3, id.longValue());
        statement.executeUpdate();
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

    mSomethingChanged = false;
  }

  // ----------------------------------------
  /**
   * clear the state of this bean
   */
  protected void clearState() {
    mId = null;
    mMessageClass = null;
    mHasProperties = false;
    mReferenceCount = 0;
    mTimestamp = null;
    mCorrelationId = null;
    mReplyTo = null;
    mDestination = null;
    mDeliveryMode = 0;
    mRedelivered = false;
    mType = null;
    mExpiration = null;
    mPriority = 0;
    mProperties = null;
    mSomethingChanged = false;
  }

  // ----------------------------------------
  // Static Methods
  // ----------------------------------------

} // end of class
