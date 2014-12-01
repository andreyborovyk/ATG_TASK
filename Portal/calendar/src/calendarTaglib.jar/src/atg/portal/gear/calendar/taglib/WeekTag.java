/*<ATGCOPYRIGHT>
 * Copyright (C) 2002-2011 Art Technology Group, Inc.
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
 * Dynamo is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.portal.gear.calendar.taglib;

import java.io.IOException;
import java.util.*;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * JSP Tag to create a calendar week view.
 * Basically, all this does is produce a List of the days in a given week.
 *
 * @author Lew Lasher
 * @version $Id: //app/portal/version/10.0.3/calendar/calendarTaglib.jar/src/atg/portal/gear/calendar/taglib/WeekTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class WeekTag extends TagSupport {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/calendar/calendarTaglib.jar/src/atg/portal/gear/calendar/taglib/WeekTag.java#2 $$Change: 651448 $";


  public WeekTag() {}

  public final int NUM_DAYS_PER_WEEK = 7;

  protected String mId;
  public String getId() {return mId;}

  public void setId(String pId) {mId = pId;}

  protected Date mStartDate;
  public Date getStartDate()
  {
    return mStartDate;
  }
  public void setStartDate(Date pStartDate)
  {
    mStartDate = pStartDate;
  }

  protected List mDateList;
  public List getDateList()
  {
    return mDateList;
  }
  public void setDateList(List pDateList)
  {
    mDateList = pDateList;
  }

  public int    doStartTag() throws JspException
  {
// Does this need a try-catch?
    pageContext.setAttribute(getId(), this);

    List dateList = new ArrayList();
    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime(mStartDate);

    for (int i=0; i<NUM_DAYS_PER_WEEK; i++)
    {
      dateList.add(cal.getTime());
      cal.add(cal.DATE, 1);
    }

    setDateList(dateList);

    return EVAL_BODY_INCLUDE;
  }

  public int doEndTag() throws JspException
  {
// Does this method need a try-catch?

      setDateList(null);
      return EVAL_PAGE;
  }

}
