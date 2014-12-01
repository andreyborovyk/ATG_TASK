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
 * copmares two topic entries
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSTopicEntryComparator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSTopicEntryComparator
    implements Comparator
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSTopicEntryComparator.java#2 $$Change: 651448 $";

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
     * Constructs an instanceof DMSTopicEntryComparator
     */
    public DMSTopicEntryComparator()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * compare two dms topic entries
     */
    public int compare(Object pTopicEntry1,
		       Object pTopicEntry2)
    {
	if(! (pTopicEntry1 instanceof DMSTopicEntry) ||
	   ! (pTopicEntry2 instanceof DMSTopicEntry)) {
	    throw new ClassCastException();
	}

	DMSTopicEntry topicEntry1 = (DMSTopicEntry) pTopicEntry1;
	DMSTopicEntry topicEntry2 = (DMSTopicEntry) pTopicEntry2;

	try {
	    // topic entries with messsages come before those without
	    if(topicEntry1.getMessage() == null &&
	       topicEntry2.getMessage() != null)
		return 1;
	    
	    if(topicEntry2.getMessage() == null &&
	       topicEntry1.getMessage() != null)
		return -1;

	    int compare = 0;

	    if(topicEntry1.getMessage() == null &&
	       topicEntry2.getMessage() == null)
		compare = 0;
	    else {
		compare = 
		    topicEntry1.getMessage().getTimestamp().
		    compareTo(topicEntry2.getMessage().getTimestamp());
	    }
		
	    if(compare == 0) {
		compare =
		    topicEntry1.getId().getSubscriptionId().
		    compareTo(topicEntry2.getId().getSubscriptionId());
		
		if(compare == 0) {
		    return
			topicEntry1.getId().getMessageId().
			compareTo(topicEntry2.getId().getMessageId());
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
     * test DMSTopicEntryComparator for equality
     */
    public boolean equals(Object pTopicEntryComparator)
    {
	if(pTopicEntryComparator instanceof DMSTopicEntryComparator)
	    return true;
	else
	    return false;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
