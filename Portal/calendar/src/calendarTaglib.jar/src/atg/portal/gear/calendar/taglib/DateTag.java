/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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

import java.util.*;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * JSP Tag to represent a date within a month view of a calendar.
 * @author Lew Lasher
 * @version $Id: //app/portal/version/10.0.3/calendar/calendarTaglib.jar/src/atg/portal/gear/calendar/taglib/DateTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class DateTag extends TagSupport {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/calendar/calendarTaglib.jar/src/atg/portal/gear/calendar/taglib/DateTag.java#2 $$Change: 651448 $";

  String                mId;
  /** Month for the view 0 = jan, 11 = dec */
  int                   mMonth = -1;
  /** Year for the view */
  int                   mYear;

  Date mDate;

  ResourceBundle        mBundle;
  String                mGearId;

  final static String   RESOURCE_BUNDLE = "atg.portal.gear.calendar.CalendarResources";

//  RepositoryEventSource  mEventSource = new RepositoryEventSource();

  public DateTag() {}

  public String getId() {return mId;}

  public void setId(String pId) {mId = pId;}

  // Not sure whether this needs to know the gear ID

  public String getGearId() {return mGearId;}
  public void   setGearId(String pGearId) {mGearId = pGearId;}

  protected Date mBeginningOfDay;
  protected Date mEndOfDay;

  public Date getDate()
  {
    return mDate;
  }

  public void setDate(Date pDate)
  {
    mDate = pDate;
  }

  // Returns an integer for the date of the month for the current Date
  public int getDayOfMonth()
  {
    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime(getDate());
    return cal.get(cal.DATE);
  }


  //
  // TagSupport methods
  //

  /** Clean up resources so object could be used again. */
  public void release() {

    mId = null;
    mBundle = null;

    super.release();
  }

  public int doStartTag() throws JspException
  {
    pageContext.setAttribute(getId(), this);
    mBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE,
                                       pageContext.getRequest().getLocale());

    GregorianCalendar dateRequested = new GregorianCalendar();
    dateRequested.setTime(getDate());
    GregorianCalendar exactDate = new GregorianCalendar(
      dateRequested.get(Calendar.YEAR),
      dateRequested.get(Calendar.MONTH),
      dateRequested.get(Calendar.DAY_OF_MONTH));

    mBeginningOfDay = exactDate.getTime();
    exactDate.add(Calendar.DATE, 1);
    mEndOfDay = exactDate.getTime();

    return EVAL_BODY_INCLUDE;
  }

  public int doEndTag() throws JspException
  {
    return EVAL_PAGE;
  }

  public Date getStartTime()
  {
    return mBeginningOfDay;
  }

  public Date getEndTime()
  {
    return mEndOfDay;
  }

}
