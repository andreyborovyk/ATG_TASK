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
 * remove an entity bean
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/RemoveEntityBeanTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class RemoveEntityBeanTag
    extends DMSGenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/RemoveEntityBeanTag.java#2 $$Change: 651448 $";

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
    // EntityBean
    private EJBObject mEntityBean;
    /**
     * set EntityBean
     * @param pEntityBean the EntityBean
     */
    public void setEntityBean(EJBObject pEntityBean) { mEntityBean = pEntityBean; }
    /**
     * get EntityBean
     * @return the EntityBean
     */
    public EJBObject getEntityBean() { return mEntityBean; }

    //----------------------------------------
    // Successful
    private boolean mSuccessful;
    /**
     * set Successful
     * @param pSuccessful the Successful
     */
    public void setSuccessful(boolean pSuccessful) { mSuccessful = pSuccessful; }
    /**
     * get Successful
     * @return the Successful
     */
    public boolean isSuccessful() { return mSuccessful; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof RemoveEntityBeanTag
     */
    public RemoveEntityBeanTag()
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
	if(mEntityBean == null)
	    return SKIP_BODY;
	
	try {
	    mEntityBean.remove();
	    mSuccessful = true;
	}
	catch(RemoteException e) {
	    setException(e);
	    throw new JspException(e.getMessage());
	}
	catch(RemoveException e) {
	    setException(e);
	    throw new JspException(e.getMessage());
	}
	catch(EJBException e) {
	    setException(e.getCausedByException());
	    throw new JspException(e.getMessage());
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
	return EVAL_PAGE;
    }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	super.release();
	mEntityBean = null;
	mSuccessful = false;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
