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
 * compares two topic subs, orders them by the topic name
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSTopicSubComparator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSTopicSubComparator
    implements Comparator
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSTopicSubComparator.java#2 $$Change: 651448 $";

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
     * Constructs an instanceof DMSTopicSubComparator
     */
    public DMSTopicSubComparator()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * compare two dms topic subs
     */
    public int compare(Object pTopicSub1,
		       Object pTopicSub2)
    {
	if(! (pTopicSub1 instanceof DMSTopicSubscription) ||
	   ! (pTopicSub2 instanceof DMSTopicSubscription)) {
	    throw new ClassCastException();
	}

	DMSTopicSubscription topicSub1 = (DMSTopicSubscription) pTopicSub1;
	DMSTopicSubscription topicSub2 = (DMSTopicSubscription) pTopicSub2;

	try {
	    if(topicSub1.getTopic() == null || 
	       topicSub1.getTopic().getName() == null ||
	       topicSub2.getTopic() == null ||
	       topicSub2.getTopic().getName() == null)
		return 0;

	    int compare = 
		topicSub1.getTopic().getName().compareTo(topicSub2.getTopic().getName());

	    if(compare == 0)
		compare = topicSub1.getName().compareTo(topicSub2.getName());
	    
	    return compare;
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
     * test DMSTopicSubComparator for equality
     */
    public boolean equals(Object pTopicSubComparator)
    {
	if(pTopicSubComparator instanceof DMSTopicSubComparator)
	    return true;
	else
	    return false;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
