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
package atg.portal.gear.docexch.taglib;

import atg.repository.*;
import atg.portal.framework.*;
import atg.portal.gear.docexch.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.io.IOException;

/****************************************
 * Renders its body if the document property is availble in the context of the 
 * gear environment property. 
 *
 * @author ATG Portals Team 
 * @version $Id: //app/portal/version/10.0.3/docexch/docexchTaglib.jar/src/atg/portal/gear/docexch/taglib/TestDocInGearTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class TestDocInGearTag
    extends TagSupport
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //app/portal/version/10.0.3/docexch/docexchTaglib.jar/src/atg/portal/gear/docexch/taglib/TestDocInGearTag.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    //----------------------------------------
    // Properties
    //----------------------------------------
    //----------------------------------------------------
    // "input" properties
    //----------------------------------------------------

    /** the gear environment */
    GearEnvironment mGearEnv;

    //-------------------------------------
    /**
     * Sets the gear environment.  This is the "input" property.
     *  Everything else comes from here.
     **/
    public void setGearEnv(GearEnvironment pGearEnv) {
        mGearEnv = pGearEnv;
    }

    //-------------------------------------
    /**
     * Returns the gear environment
     **/
    public GearEnvironment getGearEnv() {
        return mGearEnv;
    }



    /** the doc for which to test access */
    RepositoryItem mDocument;

    //-------------------------------------
    /**
     * Sets the doc for which to test access
     **/
    public void setDocument(RepositoryItem pDocument) {
        mDocument = pDocument;
    }

    //-------------------------------------
    /**
     * Returns the doc for which to test access
     **/
    public RepositoryItem getDocument() {
        return mDocument;
    }

    
    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof IfTag
     */
    public TestDocInGearTag()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    // GenericTag methods
    //----------------------------------------

    //----------------------------------------
    /**
     * override the doStartTag method
     * render the body if the value attribute is true
     */
    public int doStartTag()
	throws JspException
    {   
	if(DocExchUtils.isDocInGear(getDocument(), getGearEnv())) {
	    return EVAL_BODY_INCLUDE;
	}
	else {

            // if we get this far, the user was denied access and should be
            // pointed at the access denied page
            try {
                HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
                if(response != null)
                    response.sendRedirect(response.encodeURL(getGearEnv().getAccessDeniedURI()));
            }
            catch (IOException e) {} // client disconnected
            catch (Exception e) { // bad stuff happened, complain
                Utilities.logError(e);
            }

            return SKIP_PAGE;

	}
    }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	super.release();
	setGearEnv(null);
        setDocument(null);
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
