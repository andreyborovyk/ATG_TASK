/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.naming.*;
import java.rmi.*;
import javax.ejb.*;

import atg.sqljmsadmin.*;

/****************************************
 * get a message by primary key
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSMessageTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSMessageTag
    extends DMSGenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSMessageTag.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    // message home
    private DMSMessageHome mMessageHome;

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // MessageId
    private String mMessageId;
    /**
     * set MessageId
     * @param pMessageId the MessageId
     */
    public void setMessageId(String pMessageId) { mMessageId = pMessageId; }
    /**
     * get MessageId
     * @return the MessageId
     */
    public String getMessageId() { return mMessageId; }

    //----------------------------------------
    // Message
    private DMSMessage mMessage;
    /**
     * set Message
     * @param pMessage the Message
     */
    public void setMessage(DMSMessage pMessage) { mMessage = pMessage; }
    /**
     * get Message
     * @return the Message
     */
    public DMSMessage getMessage() { return mMessage; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof DMSMessageTag
     */
    public DMSMessageTag()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * start this tag
     */
    public int doStartTag()
	throws JspException
    {
	boolean success = false;
	
	try {
	    if(mMessageHome == null)
		mMessageHome = mHomeFactory.getMessageHome();

	    mMessage = mMessageHome.findByPrimaryKey(Long.valueOf(mMessageId));
	    success = true;
	}
	catch(FinderException e) {
	    setException(e);
	}
	catch(DMSAdminException e) {
	    setException(e);
	    throw new JspException(e.getMessage());
	}
	catch(RemoteException e) {
	    setException(e);
	    throw new JspException(e.getMessage());
	}
	catch(NamingException e) {
	    setException(e);
	    throw new JspException(e.getMessage());
	}
	finally {
	    if(!success)
		mMessage = null;
	}

	pageContext.setAttribute(getId(), this);

	return EVAL_BODY_INCLUDE;
    }

    //----------------------------------------
    /**
     * end this tag
     */
    public int doEndTag()
	throws JspException
    {
	mMessage = null;
	return EVAL_PAGE;
    }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	super.release();
	mMessageId = null;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
