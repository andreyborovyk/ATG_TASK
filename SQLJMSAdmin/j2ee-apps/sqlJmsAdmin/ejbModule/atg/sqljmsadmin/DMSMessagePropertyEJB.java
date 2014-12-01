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
 * implementation for the DMSMessageProperty entity bean.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSMessagePropertyEJB.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSMessagePropertyEJB
    extends DMSEntityBean
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSMessagePropertyEJB.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    // find by primary key query
    private static final String kFindByPrimaryKeyQuery =
	"SELECT MSG_ID, NAME FROM DMS_MSG_PROPERTIES WHERE MSG_ID = ? AND NAME = ?";
    
    // find by message
    private static final String kFindByMessageQuery =
	"SELECT MSG_ID, NAME FROM DMS_MSG_PROPERTIES WHERE MSG_ID = ?";
    
    // remove this message property
    private static final String kRemoveMessageProperty =
	"DELETE FROM DMS_MSG_PROPERTIES WHERE MSG_ID = ? AND NAME = ?";
    
    // load envelope columns
    private static final String kLoadMessageProperty =
	"SELECT MSG_ID, NAME, DATA_TYPE, VALUE FROM DMS_MSG_PROPERTIES WHERE MSG_ID = ? AND NAME = ?";

    // create a message property
    private static final String kCreateMessageProperty =
	"INSERT INTO DMS_MSG_PROPERTIES (MSG_ID, NAME, DATA_TYPE, VALUE) VALUES (?,?,?,?)";

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // Id
    private DMSMessagePropertyPrimaryKey mId;
    /**
     * get Id
     * @return the Id
     */
    public DMSMessagePropertyPrimaryKey getId() { return mId; }

    //----------------------------------------
    // Name
    private String mName;
    /**
     * get Name
     * @return the Name
     */
    public String getName() { return mName; }

    //----------------------------------------
    // DataType
    private int mDataType;
    /**
     * get DataType
     * @return the DataType
     */
    public int getDataType() { return mDataType; }

    //----------------------------------------
    // Value
    private String mValue;
    /**
     * get Value
     * @return the Value
     */
    public String getValue() { return mValue; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof DMSMessagePropertyEJB
     */
    public DMSMessagePropertyEJB()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

  //----------------------------------------
  /**
   * post create
   */
  public void ejbPostCreate(Long pMessageId, 
			    String pPropertyName,
			    int pDataType,
			    String pValue)
  {
  }

    //----------------------------------------
    /**
     * create a message property
     */
    public DMSMessagePropertyPrimaryKey ejbCreate(Long pMessageId, 
						  String pPropertyName,
						  int pDataType,
						  String pValue)
	throws CreateException
    {
	Connection connection = null;
	PreparedStatement statement = null;

	try {
	    connection = getConnection();
	    statement = connection.prepareStatement(kCreateMessageProperty);
	    statement.setLong(1, pMessageId.longValue());
	    statement.setString(2, pPropertyName);
	    statement.setInt(3, pDataType);
	    statement.setString(4, pValue);

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

	return new DMSMessagePropertyPrimaryKey(pMessageId, pPropertyName);
    }

    //----------------------------------------
    /**
     * find by primary key
     */
    public DMSMessagePropertyPrimaryKey ejbFindByPrimaryKey(DMSMessagePropertyPrimaryKey pPrimaryKey)
	throws FinderException, DMSAdminException
    {
	Connection connection = null;
	PreparedStatement statement = null;
	ResultSet results = null;

	try {
	    connection = getConnection();
	    statement = connection.prepareStatement(kFindByPrimaryKeyQuery);
	    statement.setLong(1, pPrimaryKey.getMessageId().longValue());
	    statement.setString(2, pPrimaryKey.getPropertyName());

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
     * find by message
     */
    public Collection ejbFindByMessage(Long pMessageId)
	throws FinderException, DMSAdminException
    {
	Connection connection = null;
	PreparedStatement statement = null;
	ResultSet results = null;
	ArrayList messagePropertyKeys = new ArrayList(11);

	try {
	    connection = getConnection();
	    statement = connection.prepareStatement(kFindByMessageQuery);
	    statement.setLong(1, pMessageId.longValue());
	    results = statement.executeQuery();

	    // put all the message property keys in a collection
	    while(results.next()) {
		DMSMessagePropertyPrimaryKey primaryKey = 
		    new DMSMessagePropertyPrimaryKey(new Long(results.getLong(1)),
						     results.getString(2));
		messagePropertyKeys.add(primaryKey);
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

	return messagePropertyKeys;
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
	    // get the primary key
	    DMSMessagePropertyPrimaryKey primaryKey = 
		(DMSMessagePropertyPrimaryKey) mEntityContext.getPrimaryKey();

	    // remove the message property
	    connection = getConnection();
	    statement = connection.prepareStatement(kRemoveMessageProperty);
	    statement.setLong(1, primaryKey.getMessageId().longValue());
	    statement.setString(2, primaryKey.getPropertyName());
	    statement.executeUpdate();
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
	    DMSMessagePropertyPrimaryKey primaryKey =
		(DMSMessagePropertyPrimaryKey) mEntityContext.getPrimaryKey();
	    connection = getConnection();
	    statement = connection.prepareStatement(kLoadMessageProperty);
	    statement.setLong(1, primaryKey.getMessageId().longValue());
	    statement.setString(2, primaryKey.getPropertyName());
	    results = statement.executeQuery();

	    // if no message property with given primary key, throw an exception
	    if(!results.next())
		throw new EJBException(MessageFormat.format(Constants.NO_ENTITY_WITH_ID,
							    new Object[] { "DMSMessageProperty",
									   "" + primaryKey }));

	    mId = new DMSMessagePropertyPrimaryKey(new Long(results.getLong(1)),
						   results.getString(2));
	    mName = mId.getPropertyName();
	    mDataType = results.getInt(3);
	    mValue = results.getString(4);
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
     * clear the state
     */
    protected void clearState()
    {
	mId = null;
	mName = null;
	mDataType = 0;
	mValue = null;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
