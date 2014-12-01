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

package atg.sqljmsadmin;

import javax.ejb.*;
import java.rmi.*;
import javax.rmi.PortableRemoteObject;
import javax.naming.*;
import javax.sql.*;
import java.sql.*;

/****************************************
 * a superclass for all the sqljms admin entity beans
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSEntityBean.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public abstract class DMSEntityBean
    implements EntityBean
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSEntityBean.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    // jndi name for datasource
    private final static String kDataSourceName = "java:comp/env/jdbc/datasource";

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    // the entity context
    protected EntityContext mEntityContext;

    // home factory
    protected DMSHomeFactory mHomeFactory;

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof DMSEntityBean
     */
    public DMSEntityBean()
    {
	mHomeFactory = new DMSHomeFactory();
    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * get a db connection
     */
    protected Connection getConnection()
	throws NamingException, SQLException
    {
	InitialContext context = new InitialContext();
	DataSource dataSource = (DataSource) context.lookup(kDataSourceName);
	return dataSource.getConnection();
    }

    //----------------------------------------
    // EntityBean methods
    //----------------------------------------

    //----------------------------------------
    /**
     * set the entity context
     */
    public void setEntityContext(EntityContext pEntityContext)
	throws EJBException, RemoteException
    {
	mEntityContext = pEntityContext;
    }

    //----------------------------------------
    /**
     * unset the entity context
     */
    public void unsetEntityContext()
	throws EJBException, RemoteException
    {
	mEntityContext = null;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
