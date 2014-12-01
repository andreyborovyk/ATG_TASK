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

package atg.taglib.core;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/****************************************
 * Executes a redirect to the specified URL.  May be a relative URL.
 * <p>
 * <code>
 * &lt;core:Redirect url="foo/bar.html"/&gt;
 * </code>
 * 
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/RedirectTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class RedirectTag
    extends GenericTag
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/templates/DAS/taglib/coreTaglib/1.0/src/atg/taglib/core/RedirectTag.java#2 $$Change: 651448 $";

    //----------------------------------------
    // Constants
    //----------------------------------------

  // the request attribute to contain the url that this tag
  // is trying to redirect to. this is useful to have in the request
  // if this redirect fails. then code later on can try to re-redirect
  public static final String CORE_REDIRECT_URL_NAME = "das_core_redirect_url";

    //----------------------------------------
    // Member Variables
    //----------------------------------------

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // Url
    private String mUrl;
    /**
     * set Url
     * @param pUrl the Url
     */
    public void setUrl(String pUrl) { mUrl = pUrl; }
    /**
     * get Url
     * @return the Url
     */
    public String getUrl() { return mUrl; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof RedirectTag
     */
    public RedirectTag()
    {

    }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

    //----------------------------------------
    /**
     * execute this tag
     */
    public int doStartTag()
	throws JspException
    {
	if(getUrl() == null)
	    return SKIP_BODY;
	
	try {
	  getPageContext().getRequest().setAttribute(CORE_REDIRECT_URL_NAME, getUrl());
	  ((HttpServletResponse) getPageContext().getResponse()).sendRedirect(getUrl());
	}
	catch(IOException e) {
          atg.servlet.ServletUtil.handleJspTagIOException(e);
	}

	return EVAL_BODY_INCLUDE;
    }

    //----------------------------------------
    /**
     * end the tag
     */
    public int doEndTag()
	throws JspException
    {
	return SKIP_PAGE;
    }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	setUrl(null);
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
