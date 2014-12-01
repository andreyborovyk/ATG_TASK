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

package atg.j2ee.idgen;

import java.rmi.RemoteException;
import java.sql.*;
import javax.sql.*;
import javax.ejb.*;
import javax.naming.*;
import javax.rmi.PortableRemoteObject;

/****************************************
 * implementation for id generator
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/idgenEjb/atg/j2ee/idgen/IdGeneratorEJB.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class IdGeneratorEJB
    implements SessionBean
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/idgenEjb/atg/j2ee/idgen/IdGeneratorEJB.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    // default id space name
    private static final String kDefaultIdSpace = "__default__";

    // data source name
    private static final String kDataSourceName = "java:comp/env/jdbc/IdGeneratorDataSource";

    // update increment statement
    private static final String kUpdateIncrement = 
	"UPDATE DAS_ID_GENERATOR SET SEED = SEED + 2 WHERE ID_SPACE_NAME = ?";

    // select next long
    private static final String kNextLongId =
	"SELECT SEED + BATCH_SIZE - 1 FROM DAS_ID_GENERATOR WHERE ID_SPACE_NAME = ?";

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof IdGeneratorEJB
     */
    public IdGeneratorEJB()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * generate a long id from the default id space
     */
    public Long generateLongId()
    {
	return generateLongId(kDefaultIdSpace);
    }

    //----------------------------------------
    /**
     * generate a long id from a specified id space
     */
    public Long generateLongId(String pIdSpace)
    {
	return generateId(pIdSpace);
    }

    //----------------------------------------
    /**
     * generate a string id from the default id space
     */
    public String generateStringId()
    {
	return generateStringId(kDefaultIdSpace);
    }

    //----------------------------------------
    /**
     * generate a string id from a specified id space
     */
    public String generateStringId(String pIdSpace)
    {
	Long id = generateId(pIdSpace);
	
	if(id == null)
	    return null;
	else
	    return id.toString();
    }

    //----------------------------------------
    /**
     * generate an int id from the default id space
     */
    public Integer generateIntId()
    {
	return generateIntId(kDefaultIdSpace);
    }

    //----------------------------------------
    /**
     * generate an int id from the specified id space
     */
    public Integer generateIntId(String pIdSpace)
    {
	Long id = generateId(pIdSpace);
	
	if(id == null)
	    return null;
	else
	    return new Integer((int) id.longValue());
    }

    //----------------------------------------
    /**
     * generate an id
     */
    protected Long generateId(String pIdSpace)
    {
	Connection connection = null;
	PreparedStatement statement = null;
	ResultSet results = null;

	Long id = null;

	boolean success = false;

	try {
	    connection = getConnection();

	    statement = connection.prepareStatement(kUpdateIncrement);
	    statement.setString(1, pIdSpace);
	    statement.executeUpdate();
	    statement.close();

	    statement = connection.prepareStatement(kNextLongId);
	    statement.setString(1, pIdSpace);
	    results = statement.executeQuery();

	    if(results.next())
		id = new Long(results.getLong(1));
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
		if(connection != null) {
		    connection.close();
		}
	    }
	    catch(SQLException e) {
		throw new EJBException(e);
	    }
	}

	return id;
    }

    //----------------------------------------
    /**
     * get a connection
     */
    protected Connection getConnection()
	throws NamingException, SQLException
    {
	InitialContext context = new InitialContext();
	DataSource dataSource = (DataSource) context.lookup(kDataSourceName);
	return dataSource.getConnection();
    }

    //----------------------------------------
    /**
     * create an id generator
     */
    public void ejbCreate()
	throws CreateException
    {
    }

    //----------------------------------------
    // session bean methods

    //----------------------------------------
    /**
     * remove this session bean
     */
    public void ejbRemove()
    {
    }

    //----------------------------------------
    /**
     * activate this session bean
     */
    public void ejbActivate()
    {
    }

    //----------------------------------------
    /**
     * passivate this session bean
     */
    public void ejbPassivate()
    {
    }

    //----------------------------------------
    /**
     * set the session context for this session bean
     */
    public void setSessionContext(SessionContext pContext)
    {
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
