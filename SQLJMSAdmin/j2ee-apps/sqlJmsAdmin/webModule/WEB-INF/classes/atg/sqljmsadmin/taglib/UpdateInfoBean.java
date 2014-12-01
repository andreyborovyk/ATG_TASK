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

/****************************************
 * store info important for performing updates such
 * as deletions and move-ations
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/UpdateInfoBean.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class UpdateInfoBean
    implements Resetable
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/UpdateInfoBean.java#2 $$Change: 651448 $";

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
    // QueueSource
    private boolean mQueueSource;
    /**
     * set QueueSource
     * @param pQueueSource the QueueSource
     */
    public void setQueueSource(boolean pQueueSource) { mQueueSource = pQueueSource; }
    /**
     * get QueueSource
     * @return the QueueSource
     */
    public boolean isQueueSource() { return mQueueSource; }

    //----------------------------------------
    // EntryIds
    private String[] mEntryIds;
    /**
     * set EntryIds
     * @param pEntryIds the EntryIds
     */
    public void setEntryIds(String[] pEntryIds) { mEntryIds = pEntryIds; }
    /**
     * get EntryIds
     * @return the EntryIds
     */
    public String[] getEntryIds() { return mEntryIds; }

    //----------------------------------------
    // EntryId
    private String mEntryId;
    /**
     * set EntryId
     * @param pEntryId the EntryId
     */
    public void setEntryId(String pEntryId) { mEntryId = pEntryId; }
    /**
     * get EntryId
     * @return the EntryId
     */
    public String getEntryId() { return mEntryId; }

    //----------------------------------------
    // Action
    private String mAction;
    /**
     * set Action
     * @param pAction the Action
     */
    public void setAction(String pAction) { mAction = pAction; }
    /**
     * get Action
     * @return the Action
     */
    public String getAction() { return mAction; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof UpdateInfoBean
     */
    public UpdateInfoBean()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * reset 
     */
    public void reset()
    {
	mQueueSource = false;
	mEntryIds = null;
	mEntryId = null;
	mAction = null;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
