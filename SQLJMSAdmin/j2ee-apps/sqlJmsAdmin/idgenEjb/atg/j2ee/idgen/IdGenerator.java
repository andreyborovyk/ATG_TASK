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
import javax.ejb.EJBObject;

/****************************************
 * remote interface for idgenerator. this id generator will
 * generate long, int or string ids
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/idgenEjb/atg/j2ee/idgen/IdGenerator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public interface IdGenerator
    extends EJBObject
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/idgenEjb/atg/j2ee/idgen/IdGenerator.java#2 $$Change: 651448 $";

    //----------------------------------------
    /**
     * generate a long id
     */
    public Long generateLongId()
	throws RemoteException;

    //----------------------------------------
    /**
     * generate a string id
     */
    public String generateStringId()
	throws RemoteException;

    //----------------------------------------
    /**
     * generate a long id for a specific id space
     */
    public Long generateLongId(String pIdSpace)
	throws RemoteException;

    //----------------------------------------
    /**
     * generate a string id for a specific id space
     */
    public String generateStringId(String pIdSpace)
	throws RemoteException;

    //----------------------------------------
    /**
     * generate an int id
     */
    public Integer generateIntId()
	throws RemoteException;

    //----------------------------------------
    /**
     * generate an int id for a specific id space
     */
    public Integer generateIntId(String pIdSpace)
	throws RemoteException;
} 
// end of interface
