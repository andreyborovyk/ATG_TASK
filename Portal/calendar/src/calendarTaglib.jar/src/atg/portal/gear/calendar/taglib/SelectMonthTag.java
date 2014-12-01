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
import java.text.SimpleDateFormat;
import java.text.ParseException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * JSP Tag to provide support for selecting a month/year for the
 * monthly calendar table view.  Basically all it does is produce
 * an array of months, from which the user can choose in a drop-down list.
 * Each month is represented by a Date for midnight at the beginning
 * of the month.  There are also public methods in this tag for
 * formatting the Date in two ways: for display in the drop-down list
 * and as text to be submitted in a form.
 *
 * @author Lew Lasher
 * @version $Id: //app/portal/version/10.0.3/calendar/calendarTaglib.jar/src/atg/portal/gear/calendar/taglib/SelectMonthTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class SelectMonthTag extends TagSupport {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/calendar/calendarTaglib.jar/src/atg/portal/gear/calendar/taglib/SelectMonthTag.java#2 $$Change: 651448 $";

  String                mId;

  ResourceBundle        mBundle;
  String                mGearId;
  int                mEndYear;

  /**
   * The calendar range is this many months ahead and behind
   * the current month
   */
  final static int      PAST_MONTH_RANGE = 6;

  SimpleDateFormat displayMonthFormat;
  SimpleDateFormat submitMonthFormat;

  final static String   RESOURCE_BUNDLE = "atg.portal.gear.calendar.CalendarResources";

  public SelectMonthTag()
  {
  }

  public String getId() {return mId;}

  public void setId(String pId) {mId = pId;}

  public String getGearId() {return mGearId;}

  public void   setGearId(String pGearId) {mGearId = pGearId;}

  public int getEndYear()
  { if (mEndYear<=0) {
      mEndYear=2010;
    }
    return mEndYear;
  }

  public void   setEndYear(int pEndYear) {mEndYear = pEndYear;}


  /**
   * @return List of Date objects for the calendar range
   */
  public List   getMonthRange() {


   // get current year to figure out range
    Calendar rightNow = Calendar.getInstance();
    int currentYear=rightNow.get(Calendar.YEAR);
    int currentMonth=rightNow.get(Calendar.MONTH);

    GregorianCalendar endDate = new GregorianCalendar(getEndYear(),12,31);



    ArrayList result = new ArrayList();
    GregorianCalendar cal = new GregorianCalendar();

    cal.add(cal.MONTH, -PAST_MONTH_RANGE);

    while(cal.before(endDate)) {
      result.add(cal.getTime());
      cal.add(cal.MONTH, 1);
    }
    return result;
  }

  public String formatMonthYearForDisplay(Date pDate)
  {
      return displayMonthFormat.format(pDate);
  }

  public String formatMonthYearForSubmit(Date pDate)
  {
      return submitMonthFormat.format(pDate);
  }

  public Date parseMonthText(String pText)
  {
    Date result;

    try
    {
      result = submitMonthFormat.parse(pText);
    }
    catch (ParseException e)
    {
      result = new Date();
      String monthYearOnly = formatMonthYearForSubmit(result);
      return parseMonthText(monthYearOnly);
    }

    return result;
  }

  //
  // TagSupport methods
  //

  /** Clean up resources so object could be used again. */
  public void   release()
  {
    mId = null;
    mBundle = null;

    super.release();
  }

  public int    doStartTag() throws JspException
  {
    pageContext.setAttribute(getId(), this);
    Locale locale = pageContext.getRequest().getLocale();
    mBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE, locale);
    displayMonthFormat = new SimpleDateFormat("MMMM yyyy", locale);
    // Must match the format in MonthTag
    submitMonthFormat = new SimpleDateFormat("M-yyyy", locale);

    return EVAL_BODY_INCLUDE;
  }

  public int doEndTag() throws JspException {
      return EVAL_PAGE;
  }


}
