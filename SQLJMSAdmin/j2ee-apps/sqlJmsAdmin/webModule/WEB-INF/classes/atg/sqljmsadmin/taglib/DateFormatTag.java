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
import java.text.*;
import java.sql.Date;

/****************************************
 * format a date
 *
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DateFormatTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DateFormatTag
    extends TagSupport
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/WEB-INF/classes/atg/sqljmsadmin/taglib/DateFormatTag.java#2 $$Change: 651448 $";

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
    // Date
    private Date mDate;
    /**
     * set Date
     * @param pDate the Date
     */
    public void setDate(Date pDate) { mDate = pDate; }
    /**
     * get Date
     * @return the Date
     */
    public Date getDate() { return mDate; }

    //----------------------------------------
    // FormattedDate
    private String mFormattedDate;
    /**
     * set FormattedDate
     * @param pFormattedDate the FormattedDate
     */
    public void setFormattedDate(String pFormattedDate) { mFormattedDate = pFormattedDate; }
    /**
     * get FormattedDate
     * @return the FormattedDate
     */
    public String getFormattedDate() { return mFormattedDate; }

    //----------------------------------------
    // Constructors
    //----------------------------------------

    //----------------------------------------
    /**
     * Constructs an instanceof DateFormatTag
     */
    public DateFormatTag()
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
	if(mDate == null)
	    return SKIP_BODY;

	DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, 
							       DateFormat.SHORT);
	mFormattedDate = dateFormat.format(mDate);
	
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
	mFormattedDate = null;
	return EVAL_PAGE;
    }

    //----------------------------------------
    /**
     * release this tag
     */
    public void release()
    {
	super.release();
	mDate = null;
    }

    //----------------------------------------
    /**
     * to string
     */
    public String toString()
    {
	return mFormattedDate;
    }

    //----------------------------------------
    // Static Methods 
    //----------------------------------------

} // end of class
