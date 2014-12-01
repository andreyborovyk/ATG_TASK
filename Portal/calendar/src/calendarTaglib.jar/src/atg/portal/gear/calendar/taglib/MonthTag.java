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

import java.io.IOException;
import java.util.*;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;


/**
 * JSP Tag to iterate over the days in the month and
 * create a monthly calendar table view.
 *
 * @author Hayden Schultz, Lew Lasher
 * @version $Id: //app/portal/version/10.0.3/calendar/calendarTaglib.jar/src/atg/portal/gear/calendar/taglib/MonthTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class MonthTag extends TagSupport {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/calendar/calendarTaglib.jar/src/atg/portal/gear/calendar/taglib/MonthTag.java#2 $$Change: 651448 $";

  String                mId;

  Date                  mMonthRequested ;

  ResourceBundle        mBundle;
  String                mGearId;


  final static String   RESOURCE_BUNDLE = "atg.portal.gear.calendar.CalendarResources";

  // Must match the format in SelectMonthTag
//  SimpleDateFormat selectMonthFormat = new SimpleDateFormat("M-yyyy");

  public List getWeekFullDates()
  {
    return mWeekFullDateList;
  }

  List mWeekFullDateList;
  Date mWeekFullDate;

  protected Date mBeginningOfMonth;
  protected Date mEndOfMonth;
  protected Date mBorderStart;
  protected Date mBorderEnd;

  public MonthTag()
  {
  }

  public String getId() {return mId;}

  public void setId(String pId) {mId = pId;}

// This does not appear to be used
  public String getGearId() {return mGearId;}
  public void   setGearId(String pGearId) {mGearId = pGearId;}

  public Date getMonthRequested()
  {
    return mMonthRequested;
  }

  public void setMonthRequested(Date pMonthRequested)
  {
    mMonthRequested = pMonthRequested;
  }

  //
  // TagSupport methods
  //

  /** Clean up resources so object could be used again. */
  public void release()
  {
    mId = null;
    mBundle = null;

    super.release();
  }

  public int doStartTag() throws JspException
  {
    pageContext.setAttribute(getId(), this);

    mBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE,
                                       pageContext.getRequest().getLocale());

    mWeekFullDateList = new ArrayList();

    if (mMonthRequested == null)
      mMonthRequested = new Date();

    GregorianCalendar calRequested = new GregorianCalendar();
    if (mMonthRequested != null)
      calRequested.setTime(mMonthRequested);

    int monthRequested = calRequested.get(calRequested.MONTH);
    int yearRequested = calRequested.get(calRequested.YEAR);

    GregorianCalendar cal =
      new GregorianCalendar(
        yearRequested, monthRequested, 1, 0, 0, 0);

    mBeginningOfMonth = cal.getTime();
    cal.add(cal.MONTH, 1);
    mEndOfMonth = cal.getTime();

    GregorianCalendar startOfWeekCal =
      new GregorianCalendar(yearRequested, monthRequested, 1, 0, 0, 0);
    int dayOfWeekThatMonthStartsOn =
      startOfWeekCal.get(startOfWeekCal.DAY_OF_WEEK);
    int numDaysBeforeMonth = dayOfWeekThatMonthStartsOn - 1;
    startOfWeekCal.add(Calendar.DATE, - numDaysBeforeMonth);

    Date startOfWeekDate = startOfWeekCal.getTime();
    mBorderStart = startOfWeekDate;

    while (startOfWeekDate.before(mEndOfMonth))
    {
      mWeekFullDateList.add(startOfWeekDate);

      startOfWeekCal.add(startOfWeekCal.DATE, 7);
      startOfWeekDate = startOfWeekCal.getTime();
    }

    mBorderEnd = startOfWeekCal.getTime();

    return EVAL_BODY_INCLUDE;
  }

  public int doEndTag() throws JspException {
      return EVAL_PAGE;
  }

  public boolean contains(Date pDate)
  {
    return ! pDate.before(mBeginningOfMonth) && pDate.before(mEndOfMonth);
  }

  public String getDayName(int i)
  {
    if (i < 0 || i >= 7)
      return "";
    return mBundle.getString("week-day" + (i + 1));
  }

  public String [] getDayNames()
  {
    String result[] = new String[7];
    for(int i=0; i < 7; ++i)
      result[i] = mBundle.getString("week-day" + (i + 1));
    return result;
  }

  public Date getStartTime()
  {
    return mBeginningOfMonth;
  }

  public Date getEndTime()
  {
    return mEndOfMonth;
  }

  public Date getBorderStartTime()
  {
    return mBorderStart;
  }

  public Date getBorderEndTime()
  {
    return mBorderEnd;
  }

}
