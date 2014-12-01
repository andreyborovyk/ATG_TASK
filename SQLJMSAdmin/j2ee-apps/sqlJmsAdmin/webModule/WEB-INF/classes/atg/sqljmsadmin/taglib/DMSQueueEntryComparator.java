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

package atg.sqljmsadmin.taglib;

import java.util.Comparator;
import java.rmi.RemoteException;

import atg.sqljmsadmin.*;

/****************************************
 * compares two queue entries 
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSQueueEntryComparator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSQueueEntryComparator
    implements Comparator
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSQueueEntryComparator.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

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
     * Constructs an instanceof DMSQueueEntryComparator
     */
    public DMSQueueEntryComparator()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * compare two dms queue entries
     */
    public int compare(Object pQueueEntry1,
		       Object pQueueEntry2)
    {
	if(! (pQueueEntry1 instanceof DMSQueueEntry) ||
	   ! (pQueueEntry2 instanceof DMSQueueEntry)) {
	    throw new ClassCastException();
	}

	DMSQueueEntry queueEntry1 = (DMSQueueEntry) pQueueEntry1;
	DMSQueueEntry queueEntry2 = (DMSQueueEntry) pQueueEntry2;

	try {
	    // queue entrys with messages come before those without
	    if(queueEntry1.getMessage() == null &&
	       queueEntry2.getMessage() != null)
		return 1;

	    if(queueEntry2.getMessage() == null &&
	       queueEntry1.getMessage() != null)
		return -1;

	    int compare = 0;

	    if(queueEntry1.getMessage() == null &&
	       queueEntry2.getMessage() == null)
		compare = 0;
	    else {
		compare = 
		    queueEntry1.getMessage().getTimestamp().
		    compareTo(queueEntry2.getMessage().getTimestamp());
	    }

	    if(compare == 0) {
		compare =
		    queueEntry1.getId().getQueueId().compareTo(queueEntry2.getId().getQueueId());

		if(compare == 0) {
		    return
			queueEntry1.getId().getMessageId().
			compareTo(queueEntry2.getId().getMessageId());
		}
		else {
		    return compare;
		}
	    }
	    else {
		return compare;
	    }
	}
	catch(RemoteException e) {
	    return 0;
	}
	catch(DMSAdminException e) {
	    return 0;
	}
    }

    //----------------------------------------
    /**
     * test DMSQueueEntryComparator for equality
     */
    public boolean equals(Object pQueueEntryComparator)
    {
	if(pQueueEntryComparator instanceof DMSQueueEntryComparator)
	    return true;
	else
	    return false;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
