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
import javax.rmi.*;
import javax.naming.*;

/****************************************
 * implementation for Dummy sesion bean
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DummyEJB.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DummyEJB
    implements SessionBean
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DummyEJB.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    // session context
    private SessionContext mSessionContext;

    // home factory
    private DMSHomeFactory mHomeFactory;

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof DummyEJB
     */
    public DummyEJB()
    {
	mHomeFactory = new DMSHomeFactory();
    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * create 
     */
    public void ejbCreate()
    {
    }

    //----------------------------------------
    /**
     * set the session context
     */
    public void setSessionContext(SessionContext pSessionContext)
	throws EJBException, RemoteException
    {
	mSessionContext = pSessionContext;
    }

    //----------------------------------------
    /**
     * remove this session bean
     */
    public void ejbRemove()
	throws EJBException, RemoteException
    {
    }

    //----------------------------------------
    /**
     * activate this session bean
     */
    public void ejbActivate()
	throws EJBException, RemoteException
    {
    }

    //----------------------------------------
    /**
     * passivate this session bean
     */
    public void ejbPassivate()
	throws EJBException, RemoteException
    {
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
