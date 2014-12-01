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
 * The remote interface for the DMSMessage entity bean.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSMessage.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public interface DMSMessage
    extends EJBObject
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSMessage.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Id
    /**
     * get Id
     * @return the Id
     */
    public Long getId()
	throws RemoteException;

    //----------------------------------------
    // MessageClass
    /**
     * get MessageClass
     * @return the MessageClass
     */
    public String getMessageClass()
	throws RemoteException;

    //----------------------------------------
    // HasProperties
    /**
     * get HasProperties
     * @return the HasProperties
     */
    public boolean isHasProperties()
	throws RemoteException;

    //----------------------------------------
    // ReferenceCount
    /**
     * get ReferenceCount
     * @return the ReferenceCount
     */
    public int getReferenceCount()
	throws RemoteException;

    public void setReferenceCount(int pReferenceCount)
	throws RemoteException;

    //----------------------------------------
    // Timestamp
    /**
     * get Timestamp
     * @return the Timestamp
     */
    public Date getTimestamp()
	throws RemoteException;

    //----------------------------------------
    // CorrelationId
    /**
     * get CorrelationId
     * @return the CorrelationId
     */
    public String getCorrelationId()
	throws RemoteException;

    //----------------------------------------
    // ReplyTo
    /**
     * get ReplyTo
     * @return the ReplyTo
     */
    public Long getReplyTo()
	throws RemoteException;

    //----------------------------------------
    // Destination
    /**
     * get Destination
     * @return the Destination
     */
    public Long getDestination()
	throws RemoteException;

    public void setDestination(Long pDestination)
	throws RemoteException;

    //----------------------------------------
    // DeliveryMode
    /**
     * get DeliveryMode
     * @return the DeliveryMode
     */
    public int getDeliveryMode()
	throws RemoteException;

    //----------------------------------------
    // Redelivered
    /**
     * get Redelivered
     * @return the Redelivered
     */
    public boolean isRedelivered()
	throws RemoteException;

    //----------------------------------------
    // Type
    /**
     * get Type
     * @return the Type
     */
    public String getType()
	throws RemoteException;

    //----------------------------------------
    // Expiration
    /**
     * get Expiration
     * @return the Expiration
     */
    public Date getExpiration()
	throws RemoteException;

    //----------------------------------------
    // Priority
    /**
     * get Priority
     * @return the Priority
     */
    public int getPriority()
	throws RemoteException;

    //----------------------------------------
    // SmallBody
    /**
     * get SmallBody
     * @return the SmallBody
     */
    public byte[] getSmallBody()
	throws RemoteException;

    //----------------------------------------
    // LargeBody
    /**
     * get LargeBody
     * @return the LargeBody
     */
    public byte[] getLargeBody()
	throws RemoteException;

    //----------------------------------------
    // Properties
    /**
     * get Properties
     * @return the Properties
     */
    public DMSMessageProperty[] getProperties()
	throws RemoteException, DMSAdminException;

} // end of class
