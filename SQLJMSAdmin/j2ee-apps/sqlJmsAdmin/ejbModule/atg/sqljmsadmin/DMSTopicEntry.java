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

import java.sql.Date;
import java.rmi.RemoteException;
import javax.ejb.EJBObject;

/****************************************
 * The remote interface for the DMSTopicEntry entity bean
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSTopicEntry.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public interface DMSTopicEntry
    extends EJBObject
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSTopicEntry.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Id
    /**
     * get Id
     * @return the Id
     */
    public DMSTopicEntryPrimaryKey getId()
	throws RemoteException;

    //----------------------------------------
    // Subscription
    /**
     * get Subscription
     * @return the Subscription
     */
    public DMSTopicSubscription getSubscription()
	throws RemoteException, DMSAdminException;

    //----------------------------------------
    // Message
    /**
     * get Message
     * @return the Message
     */
    public DMSMessage getMessage()
	throws RemoteException, DMSAdminException;

    //----------------------------------------
    // DeliveryDate
    /**
     * get DeliveryDate
     * @return the DeliveryDate
     */
    public Date getDeliveryDate()
	throws RemoteException;

    //----------------------------------------
    // ReadState
    /**
     * get ReadState
     * @return the ReadState
     */
    public int getReadState()
	throws RemoteException;

    //----------------------------------------
    /**
     * cascade delete property, defaults to true
     */
    public void setCascadeDelete(boolean pCascadeDelete)
	throws RemoteException;

} // end of class
