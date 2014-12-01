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
 * get a dms client by id
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSClientTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSClientTag
    extends DMSGenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DMSClientTag.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    // client home
    private DMSClientHome mClientHome;

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // ClientId
    private String mClientId;
    /**
     * set ClientId
     * @param pClientId the ClientId
     */
    public void setClientId(String pClientId) { mClientId = pClientId; }

    //----------------------------------------
    // Client
    private DMSClient mClient;
    /**
     * get Client
     * @return the Client
     */
    public DMSClient getClient() { return mClient; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof DMSClientTag
     */
    public DMSClientTag()
    {
    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * start executing this tag
     */
    public int doStartTag()
	throws JspException
    {
	boolean success = false;

	try {
	    if(mClientHome == null)
		mClientHome = mHomeFactory.getClientHome();

	    mClient = mClientHome.findByClientId(Long.valueOf(mClientId));
	    success = true;
	}
	catch(FinderException e) {
	    setException(e);
	}
	catch(DMSAdminException e) {
	    setException(e.getNestedException());
	    throw new JspException(e.getMessage());
	}
	catch(NamingException e) {
	    setException(e);
	    throw new JspException(e.getMessage());
	}
	catch(RemoteException e) {
	    setException(e);
	    throw new JspException(e.getMessage());
	}
	finally {
	    if(!success)
		mClient = null;
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
	mClient = null;
	return EVAL_PAGE;
    }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	super.release();
	mClientId = null;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
