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


package atg.portal.gear.xmlfeed.taglib;

import atg.portal.gear.xmlfeed.BaseQueryProcessor;
import atg.servlet.ServletUtil;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.http.*;

/**
 * @author Malay Desai
 * @version $Id: //app/portal/version/10.0.3/xmlfeed/xmlfeedTaglib.jar/src/atg/portal/gear/xmlfeed/taglib/GetProcessorTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * 
 */

public class GetProcessorTag extends TagSupport
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlfeed/xmlfeedTaglib.jar/src/atg/portal/gear/xmlfeed/taglib/GetProcessorTag.java#2 $$Change: 651448 $";

   
    //----------------------------------------
    // Properties
    //----------------------------------------

    
    //----------------------------------------
    // property: className
    private String mClassName;

    public void setClassName(String pClassName) 
	{ mClassName = pClassName; }

    public String getClassName() 
	{ return mClassName; }
 


    //----------------------------------------
    /**
     * start executing this tag
     * 
     *
     */
    public int doStartTag()
	throws JspException
    {
	try {
	    BaseQueryProcessor processor = (BaseQueryProcessor) 
		Class.forName(getClassName()).newInstance();
	   
	    processor.initialize(ServletUtil.getDynamoRequest(pageContext.getRequest()),
                                 (HttpServletResponse)pageContext.getResponse());   
	    pageContext.setAttribute(getId(), processor);

	   	return EVAL_BODY_INCLUDE;
	}

	catch (Exception e) {
	    System.out.println("GetProcessorTag.doStartTag():"+e.getMessage());
	    pageContext.getServletContext().log(e.getMessage());
	    return SKIP_BODY;
	}
	    
    }

    //----------------------------------------
    /**
     * release the tag
     */
    public void release()
    {
	super.release();
	setClassName(null);
    }

} // end of class
