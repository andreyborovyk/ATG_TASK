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

import java.rmi.RemoteException;
import javax.ejb.EJBHome;
import javax.ejb.FinderException;
import javax.ejb.CreateException;
import java.sql.Date;

/****************************************
 * the home interface for the DMSMessage entity bean.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSMessageHome.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public interface DMSMessageHome
    extends EJBHome
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSMessageHome.java#2 $$Change: 651448 $";

    //----------------------------------------
    /**
     * create a message
     */
    public DMSMessage create(Long pMessageId,
			     String pMessageClass,
			     boolean pHasProperties,
			     int pReferenceCount,
			     Date pTimestamp,
			     String pCorrelationId,
			     Long pReplyTo,
			     Long pDestination,
			     int pDeliveryMode,
			     boolean pRedelivered,
			     String pType,
			     Date pExpiration,
			     int pPriority,
			     byte[] pSmallBody,
			     byte[] pLargeBody,
			     DMSMessageProperty[] pProperties)
	throws RemoteException, CreateException;

    //----------------------------------------
    /**
     * find by primary key
     */
    public DMSMessage findByPrimaryKey(Long pPrimaryKey)
	throws FinderException, RemoteException, DMSAdminException;

} // end of class
