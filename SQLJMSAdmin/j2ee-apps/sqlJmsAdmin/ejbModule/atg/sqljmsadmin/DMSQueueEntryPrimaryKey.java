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
 * The primary key class for the DMSQueueEntry 
 * entity bean.
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSQueueEntryPrimaryKey.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSQueueEntryPrimaryKey
  extends Object
  implements Serializable
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSQueueEntryPrimaryKey.java#2 $$Change: 651448 $";

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
    // QueueId
    private Long mQueueId;
    /**
     * set QueueId
     * @param pQueueId the QueueId
     */
    public void setQueueId(Long pQueueId) { mQueueId = pQueueId; }
    /**
     * get QueueId
     * @return the QueueId
     */
    public Long getQueueId() { return mQueueId; }

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
     * Constructs an instanceof DMSQueueEntryPrimaryKey
     */
    public DMSQueueEntryPrimaryKey(Long pQueueId,
				   Long pMessageId)
    {
	mQueueId = pQueueId;
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
	return new String("" + mQueueId + "," + mMessageId);
    }

  //----------------------------------------
  /**
   * hash code
   */
  public int hashCode()
  {
    return (new String("" + getMessageId() + getQueueId())).hashCode();
  }

  //----------------------------------------
  /**
   * equals
   */
  public boolean equals(Object pPrimaryKey)
  {
    if(pPrimaryKey instanceof DMSQueueEntryPrimaryKey &&
       pPrimaryKey.hashCode() == this.hashCode())
      return true;
    else
      return false;
  }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
