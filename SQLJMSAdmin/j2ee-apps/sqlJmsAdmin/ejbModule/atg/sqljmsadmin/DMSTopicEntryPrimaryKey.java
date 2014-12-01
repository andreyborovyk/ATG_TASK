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

import java.io.Serializable;

/****************************************
 * the primary key class for the DMSTopicEntry
 * entity bean.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSTopicEntryPrimaryKey.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSTopicEntryPrimaryKey
    implements Serializable
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSTopicEntryPrimaryKey.java#2 $$Change: 651448 $";

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
    // SubscriptionId
    private Long mSubscriptionId;
    /**
     * set SubscriptionId
     * @param pSubscriptionId the SubscriptionId
     */
    public void setSubscriptionId(Long pSubscriptionId) { mSubscriptionId = pSubscriptionId; }
    /**
     * get SubscriptionId
     * @return the SubscriptionId
     */
    public Long getSubscriptionId() { return mSubscriptionId; }

    //----------------------------------------
    // MessageId
    private Long mMessageId;
    /**
     * set MessageId
     * @param pMessageId the MessageId
     */
    public void setMessageId(Long pMessageId) { mMessageId = pMessageId; }
    /**
     * get MessageId
     * @return the MessageId
     */
    public Long getMessageId() { return mMessageId; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof DMSTopicEntryPrimaryKey
     */
    public DMSTopicEntryPrimaryKey(Long pSubscriptionId,
				   Long pMessageId)
    {
	mSubscriptionId = pSubscriptionId;
	mMessageId = pMessageId;
    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * to string
     */
    public String toString()
    {
	return new String("" + mSubscriptionId + "," + mMessageId);
    }

  //----------------------------------------
  /**
   * hash code
   */
  public int hashCode()
  {
    return (new String("" + getMessageId() + getSubscriptionId())).hashCode();
  }

  //----------------------------------------
  /**
   * equals
   */
  public boolean equals(Object pPrimaryKey)
  {
    if(pPrimaryKey instanceof DMSTopicEntryPrimaryKey &&
       pPrimaryKey.hashCode() == this.hashCode())
      return true;
    else
      return false;
  }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
