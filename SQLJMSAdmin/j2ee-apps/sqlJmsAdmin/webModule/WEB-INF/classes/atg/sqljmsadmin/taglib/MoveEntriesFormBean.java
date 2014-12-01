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
 * store data from a move entries form submission
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/MoveEntriesFormBean.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class MoveEntriesFormBean
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/MoveEntriesFormBean.java#2 $$Change: 651448 $";

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
    // HandleForm
    private boolean mHandleForm;
    /**
     * set HandleForm
     * @param pHandleForm the HandleForm
     */
    public void setHandleForm(boolean pHandleForm) { mHandleForm = pHandleForm; }
    /**
     * get HandleForm
     * @return the HandleForm
     */
    public boolean isHandleForm() { return mHandleForm; }

    //----------------------------------------
    // Destination
    private String mDestination;
    /**
     * set Destination
     * @param pDestination the Destination
     */
    public void setDestination(String pDestination) { mDestination = pDestination; }
    /**
     * get Destination
     * @return the Destination
     */
    public String getDestination() { return mDestination; }

    //----------------------------------------
    // DestinationId
    private Long mDestinationId;
    /**
     * set DestinationId
     * @param pDestinationId the DestinationId
     */
    public void setDestinationId(Long pDestinationId) { mDestinationId = pDestinationId; }
    /**
     * get DestinationId
     * @return the DestinationId
     */
    public Long getDestinationId() { return mDestinationId; }

    //----------------------------------------
    // DestinationType
    private Long mDestinationType;
    /**
     * set DestinationType
     * @param pDestinationType the DestinationType
     */
    public void setDestinationType(Long pDestinationType) { mDestinationType = pDestinationType; }
    /**
     * get DestinationType
     * @return the DestinationType
     */
    public Long getDestinationType() { return mDestinationType; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof MoveEntriesFormBean
     */
    public MoveEntriesFormBean()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
