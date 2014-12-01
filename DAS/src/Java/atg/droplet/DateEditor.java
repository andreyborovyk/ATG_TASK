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
package atg.droplet;

import atg.servlet.*;

import atg.core.util.ResourceUtils;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

import java.io.IOException;
import java.util.MissingResourceException;


/**
 * This class allows for the manipulation within a form of a
 * java.util.Date property in another component.
 *
 * <p>
 * A component created with this class should be request scoped.
 * Therefore everytime a page with a form that uses this component
 * will always have the current time.  If you want the component to
 * have the time at the beginning of the session, then make the
 * component session scoped.
 *
 * <p>
 * An example of creating a date and setting it in the
 * java.util.Date.property follows:
 * <pre>
 &lt;input type="text" priority=100 bean="DateEditor.Year">
 &lt;input type="text" priority=100 bean="DateEditor.Month">
 &lt;input type="text" priority=100 bean="DateEditor.Day">
 &lt;input type="text" priority=100 bean="DateEditor.Hours">
 &lt;input type="text" priority=100 bean="DateEditor.Minutes">
 &lt;input type="hidden" value="" bean="DateEditor.submit">
 * </pre>
 *
 * Note the above example assumes these date manipulation fields are
 * within a form that calls another bean.  The hidden input is to call
 * the submit method of the DateEditor bean to ensure that the desired
 * java.util.Date property is updated.
 *
 * <p>
 * Another example that allows the manipulation of the current value
 * of the java.util.Date.property follows:
 * <pre>
 &lt;setvalue bean="DateEditor.Date"
   value="bean:NewsRepositoryFormHandler.value.goLiveDate">

 &lt;input type="text" priority=100 bean="DateEditor.Year">
 &lt;input type="text" priority=100 bean="DateEditor.Month">
 &lt;input type="text" priority=100 bean="DateEditor.Day">
 &lt;input type="text" priority=100 bean="DateEditor.Hours">
 &lt;input type="text" priority=100 bean="DateEditor.Minutes">
 &lt;input type="hidden" value="" bean="DateEditor.submit">
 * </pre>
 *
 * NOTE: Do NOT set the properties Year, Month, Day, Hours, Minutes
 * directly in the .properties field or using the Dynamo Developer
 * Workbench.  Do this only if you want the DateEditor always to have
 * these values.  Currently whenever the DateEditor is created it
 * always has the current date and time.
 *
 *
 * @see GenericFormHandler
 * @author Frank Kim
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/DateEditor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo
 *   description: A form handler
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 **/
public class DateEditor extends GenericFormHandler {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/DateEditor.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Member variables

  /** Used for converting between a Date object and a set of integer
   *  fields such as YEAR, MONTH, DAY, HOUR, and so on.
   **/
  private Calendar mCalendar;

  /** Resource bundle **/
  static final String MY_RESOURCES_NAME
  = "atg.droplet.DateEditorResources";
  static final String MY_USER_RESOURCES_NAME
  = "atg.droplet.DateEditorUserResources";
  // ResourceUtils.getBundle could throw a MissingResourceException
  // but that should NEVER happen.
  static ResourceBundle sResourceBundle
  = ResourceUtils.getBundle(MY_RESOURCES_NAME,
                            Locale.getDefault());

  //-------------------------------------
  // Properties

  //-------------------------------------
  // property: DatePropertyPathName
  private String mDatePropertyPathName;

  /**
   * Sets property DatePropertyPathName.<p>
   *
   * WARNING: Do not put the DatePropertyPathName in the JHTML file
   *          since this can allow it to be set from a client, thus
   *          exposing a security risk.
   *
   * @param pDatePropertyPathName absolute path name of the Date property
   *
   * @beaninfo
   *   description: absolute path name of the Date property
   *                e.g. /atg/demo/QFunds/NewsRepositoryFormHandler.value.date
   *   displayName: DatePropertyPathName
   **/
  public void setDatePropertyPathName (String pDatePropertyPathName) {
    mDatePropertyPathName = pDatePropertyPathName;
  }

  /**
   * Returns the absolute path name of the Date property.
   *
   * @return absolute path name of the Date property
   **/
  public String getDatePropertyPathName () { return mDatePropertyPathName; }

  //-------------------------------------
  // property: Date
  private Date mDate;

  /**
   * Sets property Date.  After setting the Date property it uses the
   * mCalendar member to set all the integer component fields of the
   * date and then sets the IsDateSet property to true.
   *
   * @param pDate specific instant in time, with millisecond precision
   *
   * @beaninfo
   *   description: specific instant in time, with millisecond precision
   *   displayName: Date
   **/
  public void setDate (Date pDate) {
    if (isLoggingDebug()) {
      logDebug("DateEditor.setDate: pDate " + pDate.toString());
      if (mDate != null) {
        logDebug("DateEditor.setDate: mDate before " + mDate.toString());
      }
    }

    mDate = pDate;
    mCalendar.setTime(pDate);
    mYear    = mCalendar.get(Calendar.YEAR);
    mMonth   = mCalendar.get(Calendar.MONTH);
    mDay     = mCalendar.get(Calendar.DAY_OF_MONTH);
    mHours   = mCalendar.get(Calendar.HOUR_OF_DAY);
    mMinutes = mCalendar.get(Calendar.MINUTE);
    mSeconds = mCalendar.get(Calendar.SECOND);
    mIsDateSet = true;

    if (isLoggingDebug()) {
      logDebug("DateEditor.setDate: mDate after " + mDate.toString());
    }
  }

  /**
   * Returns the date expressed by all the integer component fields.
   *
   * @return date expressed by all the integer component fields
   **/
  public Date getDate () {
    if (mIsDateSet == false) {
        mCalendar.set(mYear, mMonth, mDay, mHours, mMinutes, mSeconds);
        Date tmpDate = mCalendar.getTime();

        // Don't update the Date property.  Just return the date
        // expressed by all the integer coomponent fields.  We don't
        // update the Date property to keep the getDate method simple
        // and without side effects.
        return tmpDate;
    }
    return mDate;
  }

  //-------------------------------------
  // property: IsDateSet
  private boolean mIsDateSet;

  /**
   * Gets property IsDateSet
   *
   * @return true if the property Date is up to date; false otherwise
   *
   * @beaninfo
   *   description: indicates whether the property Date
   *                member is up to date (forgive the pun)
   *   displayName: IsDateSet
   **/
  public boolean getIsDateSet () {
    return mIsDateSet;
  }

  //-------------------------------------
  // property: Year
  private int mYear;

  /**
   * Sets property Year.  Sets the IsDateSet property to false since
   * the Date property is no longer up to date.
   *
   * @param pYear year value
   *
   * @beaninfo
   *   description: year
   *   displayName: Year
   **/
  public void setYear (int pYear) {
    mYear = pYear;
    mIsDateSet = false;
  }

  /**
   * Returns the property Year.
   *
   * @return property Year
   **/
  public int getYear () { return mYear; }

  //-------------------------------------
  // property: Month
  int mMonth;

  /**
   * Sets property Month.  Sets the IsDateSet property to false since
   * the Date property is no longer up to date.
   *
   * @param pMonth month value between 0-11 if UsingZeroBasedMonth is true,
   *               otherwise between 1-12
   *
   * @beaninfo
   *   description: month of the year
   *   displayName: Month
   **/
  public void setMonth (int pMonth) {
    if (mUsingZeroBasedMonth)
      mMonth = pMonth;
    else
      mMonth = pMonth - 1; // 1-12 changed to 0-11
    mIsDateSet = false;
  }

  /**
   * Returns property Month.  Value range is between 0-11 if
   * UsingZeroBasedMonth property is true, otherwise the range is
   * between 1-12.
   *
   * @return property Month
   **/
  public int getMonth () {
    if (mUsingZeroBasedMonth)
      return mMonth;

    return mMonth + 1; // 0-11 changed to 1-12
  }

  //-------------------------------------
  // property: Day
  private int mDay;

  /**
   * Sets property Day.  Sets the IsDateSet property to false since
   * the Date property is no longer up to date.
   *
   * @param pDay day value between 1-31, of course depending upon
   *             the month
   *
   * @beaninfo
   *   description: day of the month. range is 1 to 31 inclusive, of
   *                course depending upon the month
   *   displayName: Day
   **/
  public void setDay (int pDay) {
    mDay = pDay;
    mIsDateSet = false;
  }

  /**
   * Returns property Day.
   *
   * @return property Day
   **/
  public int getDay () { return mDay; }

  //-------------------------------------
  // property: Hours
  private int mHours;

  /**
   * Sets property Hours.  Sets the IsDateSet property to
   * false since the Date property is no longer up to
   * date.
   *
   * @param pHours hour value between 0-23 if the UsingAmPm property
   *               is false, 1-12 otherwise
   *
   * @beaninfo
   *   description: hour of the day. range is 0 to 23 inclusive if the
   *                UsingAmPm property is false, 1-12 otherwise.
   *   displayName: Hours
   **/
  public void setHours (int pHours) {

    if (mUsingAmPm){
      if (isPM() && (pHours != 12))
        // 1 PM to 11 PM
        pHours += 12;
      else if (isAM() && (pHours == 12))
        // 12 AM
        pHours = 0;
    }
    mHours = pHours;
    mIsDateSet = false;
  }
  
  /**
   * Returns property Hours.
   *
   * @return property Hours
   **/
  public int getHours () { 
    
    if (mUsingAmPm) {
      if (mHours > 12)
        // 1 PM to 11 PM
        return mHours - 12;
      else if (mHours == 0)
        // 12 AM
        return 12;
    }
    
    // 24 hour or 1 AM - 12 PM
    return mHours; 
  }

  //-------------------------------------
  // property: Minutes
  private int mMinutes;

  /**
   * Sets property Minutes.  Sets the IsDateSet property
   * to false since the Date property is no longer up to
   * date.
   *
   * @param pMinutes minutes value between 0-59
   *
   * @beaninfo
   *   description: minutes of the hour. range is 0 to 59 inclusive.
   *   displayName: Minutes
   **/
  public void setMinutes (int pMinutes) {
    mMinutes = pMinutes;
    mIsDateSet = false;
  }

  /**
   * Returns property Minutes
   *
   * @return property Minutes
   **/
  public int getMinutes () { return mMinutes; }

  //-------------------------------------
  // property: Seconds
  private int mSeconds;

  /**
   * Sets property Seconds.  Sets the IsDateSet property
   * to false since the Date property is no longer up to
   * date.
   *
   * @param pSeconds seconds value between 0-59
   *
   * @beaninfo
   *   description: seconds of the minute. range is 0 to 59 inclusive.
   *   displayName: Seconds
   **/
  public void setSeconds (int pSeconds) {
    mSeconds = pSeconds;
    mIsDateSet = false;
  }

  /**
   * Returns property Seconds
   *
   * @return property Seconds
   **/
  public int getSeconds () { return mSeconds; }

  //-------------------------------------
  // property: EarliestAcceptableDate
  private Date mEarliestAcceptableDate;

  /**
   * Sets property EarliestAcceptableDate
   *
   * @param pEarliestAcceptableDate earliest acceptable date in milliseconds
   *
   * @beaninfo
   *   description: earliest date that is allowed to be set in the property
   *                pointed to by DatePropertyPathName
   *   displayName: EarliestAcceptableDate
   **/
  public void setEarliestAcceptableDate (Date pEarliestAcceptableDate) {
    mEarliestAcceptableDate = pEarliestAcceptableDate;
  }

  /**
   * Returns property EarliestAcceptableDate
   *
   * @return returns earliest date that is allowed to be set in the
   *         property pointed to by DatePropertyPathName
   **/
  public Date getEarliestAcceptableDate () {
    return mEarliestAcceptableDate;
  }

  //-------------------------------------
  // property: UsingZeroBasedMonth
  // default is true
  private boolean mUsingZeroBasedMonth = true;

  /**
   * Sets property UsingZeroBasedMonth.  Default is true.
   *
   * @param pUsingZeroBasedMonth true if the Month property range is
   *                             0-11, false if it is 1-12
   *
   * @beaninfo
   *   description: true if the Month property range is 0-11,
   *                false if it is 1-12
   *   displayName: UsingZeroBasedMonth
   **/
  public void setUsingZeroBasedMonth (boolean pUsingZeroBasedMonth) {
    mUsingZeroBasedMonth = pUsingZeroBasedMonth;
  }

  /**
   * Returns property UsingZeroBasedMonth.
   *
   * @return returns true if the Month property range is 0-11,
   *                 false if the range is 1-12
   **/
  public boolean isUsingZeroBasedMonth () { return mUsingZeroBasedMonth; }

  //-------------------------------------
  // property: LatestAcceptableDate
  private Date mLatestAcceptableDate;

  /**
   * Sets property LatestAcceptableDate.
   *
   * @param pLatestAcceptableDate latest acceptable date in milliseconds
   *
   * @beaninfo
   *   description: latest date that is allowed to be set in the property
   *                pointed to by DatePropertyPathName
   *   displayName: LatestAcceptableDate
   **/
  public void setLatestAcceptableDate (Date pLatestAcceptableDate) {
    mLatestAcceptableDate = pLatestAcceptableDate;
  }

  /**
   * Returns property LatestAcceptableDate.
   *
   * @return returns latest date that is allowed to be set in the
   *         property pointed to by DatePropertyPathName
   **/
  public Date getLatestAcceptableDate () {
    return mLatestAcceptableDate;
  }

  //-------------------------------------
  // property: UsingAmPm
  private boolean mUsingAmPm;

  /**
   * Sets property UsingAmPm.  Default is false.
   *
   * @param pUsingAmPm true if the Hours property should be
   *                   interpreted with the AmPm property
   *
   * @beaninfo
   *   description: true if the Hours property should be
   *                interpreted with the AmPm property
   *   displayName: UsingAmPm
   **/
  public void setUsingAmPm (boolean pUsingAmPm) {
    mUsingAmPm = pUsingAmPm;
  }

  /**
   * Returns property UsingAmPm.
   *
   * @return returns true if the Hours property should be interpreted
   *                 with the AmPm property
   **/
  public boolean isUsingAmPm () {
    return mUsingAmPm;
  }

  //-------------------------------------
  /**
   * Sets whether the time is AM or PM.  Setting this property sets
   * the property UsingAmPm to true.
   *
   * @param pAmPm String object that is either "AM" or "PM"
   *
   * @beaninfo
   *   description: AM or PM
   *   displayName: AmPm
   **/
  public void setAmPm (String pAmPm) {
    setUsingAmPm(true);

    if (isAM() && pAmPm.equalsIgnoreCase("PM")){
      // changing the time from AM to PM
      // so add 12 unless we are already equal to 12
      if (mHours != 12)
        mHours += 12;
    }
    else if (isPM() && (pAmPm.equalsIgnoreCase("AM"))){
      // changing the time from PM to AM
      mHours -= 12;
    }
  }

  /**
   * Returns "AM" or "PM" depending on time.
   *
   * @return returns "AM" or "PM" depending on time
   **/
  public String getAmPm () {
    if (isPM())
      return "PM";
    else
      return "AM";
  }

  /**
   * Sets the time to PM if pPM is true, otherwise sets the time to
   * AM.  Setting this property sets the property UsingAmPm to true.
   *
   * @param pPM true if "PM"; false otherwise
   *
   * @beaninfo
   *   description: AM or PM
   *   displayName: AmPm
   **/
  public void setPm (boolean pPM) {
    if (pPM) 
      setAmPm("PM");
    else 
      setAmPm("AM");
  }

  /**
   * Returns true if the time is PM.
   *
   * @return returns true if time is PM
   **/
  public boolean isPM () {
    return (mHours>=12) ? true : false;
  }

  /**
   * Returns true if the time is AM.
   **/
  private final boolean isAM () {
    return !isPM();
  }

  //-------------------------------------
  // Constructors

  //-------------------------------------
  /**
   * Constructs a DateEditor object with all the fields set to the
   * current time.
   **/
  public DateEditor () {
    mCalendar = Calendar.getInstance();

    mYear    = mCalendar.get(Calendar.YEAR);
    mMonth   = mCalendar.get(Calendar.MONTH);
    mDay     = mCalendar.get(Calendar.DAY_OF_MONTH);
    mHours   = mCalendar.get(Calendar.HOUR_OF_DAY);
    mMinutes = mCalendar.get(Calendar.MINUTE);
    mSeconds = mCalendar.get(Calendar.SECOND);

    mDate = mCalendar.getTime();
    mIsDateSet = true;

    // default earliest acceptable date is Jan. 1, 1900 12:00:00 AM
    mCalendar.set(1900, 0, 1, 0, 0, 0);
    mEarliestAcceptableDate = mCalendar.getTime();

    // latest acceptable date is Jan. 1, 2049 12:00:00 AM
    mCalendar.set(2049, 0, 1, 0, 0, 0);
    mLatestAcceptableDate = mCalendar.getTime();
  }

  //-------------------------------------
  // Methods

  /**
   * Checks that the date is within the required limits.  If it is not
   * it adds a form exception.
   *
   * @param pDate date to check in milliseconds
   * @param pResourceBundleName name of the message resource bundle
   * @param pResourceBundle message resource bundle object
   * @return true if the date is within the required limits; false otherwise
   **/
  public boolean checkDate (Date pDate,
                            String pResourceBundleName,
                            ResourceBundle pResourceBundle) {
    boolean ret = true;

    if ((mEarliestAcceptableDate != null) &&
        (pDate.before(mEarliestAcceptableDate))) {

      Object[] args = {pDate.toString(),
                       mEarliestAcceptableDate.toString()};
      String msg = ResourceUtils.getMsgResource("dateBeforeMinDate",
                                                pResourceBundleName,
                                                pResourceBundle,
                                                args);
      addFormException(new DropletException(msg, "dateBeforeMinDate"));
      ret = false;
    }

    else if ((mLatestAcceptableDate != null) &&
             (pDate.after(mLatestAcceptableDate))) {

      Object[] args = {pDate.toString(),
                       mLatestAcceptableDate.toString()};
      String msg = ResourceUtils.getMsgResource("dateAfterMaxDate",
                                                pResourceBundleName,
                                                pResourceBundle,
                                                args);
      addFormException(new DropletException(msg, "dateAfterMaxDate"));
      ret = false;
    }

    return ret;
  }

  /**
   * Handles the submit transaction of a form.  Sets the date entered
   * by the user into the property pointed to by the
   * DatePropertyPathName.
   *
   * @param pRequest request object to be processed
   * @param pResponse response object for this request
   * @return true if the submit transaction succeeds; false otherwise
   **/
  public boolean handleSubmit (
    DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) {

    if (isLoggingDebug()) {
      logDebug("DateEditor.handleSubmit()");
      logDebug("DateEditor.mYear: " + mYear);
      logDebug("DateEditor.mMonth: " + mMonth);
      logDebug("DateEditor.mDay: " + mDay);
      logDebug("DateEditor.mHours: " + mHours);
      logDebug("DateEditor.mMinutes: " + mMinutes);
      logDebug("DateEditor.mSeconds: " + mSeconds);
    }

    /* For the request, if we can get the user's locale, use that for
       the resource bundle, otherwise use the resource bundle for the
       default locale. */
    RequestLocale reqLocale = pRequest.getRequestLocale();
    Locale userLocale = null;
    if (reqLocale != null)
      userLocale = reqLocale.getLocale();
    else
      userLocale = Locale.getDefault();
    ResourceBundle userResourceBundle
       = ResourceUtils.getBundle(MY_USER_RESOURCES_NAME,
                                 userLocale);

    /* Check if the date is valid.  If it is then set the date in the
       property pointed to by mDatePropertyPathName. */
    Date myDate = getDate();
    if (checkDate(myDate, MY_USER_RESOURCES_NAME, userResourceBundle)
        == true) {

      try {
        ServletUtil.setPropertyValue(pRequest, pResponse,
                                     mDatePropertyPathName, myDate);
      }
      catch (javax.servlet.ServletException ex) {
        /* only a developer error could cause this so just log it */
        String msg = ResourceUtils.getMsgResource("unexpectedServletException",
                                                  MY_RESOURCES_NAME,
                                                  sResourceBundle);
        logError(msg);
      }
      catch (java.io.IOException ex) {
        /* only a developer error could cause this so just log it */
        String msg = ResourceUtils.getMsgResource("unexpectedIOException",
                                                  MY_RESOURCES_NAME,
                                                  sResourceBundle);
        logError(msg);
      }
    }

    return true;
  }

  /**
   * Test method.
   *
   * @param args array of String arguments from the console
   **/
  static public void main (String [] args) {

    DateEditor de = new DateEditor();

    // test output which should be the current date
    System.out.println("Date: " + de.getDate());

    System.out.println("Year: " + Integer.toString(de.getYear()));
    System.out.println("Month: " + Integer.toString(de.getMonth()));
    System.out.println("Day: " + Integer.toString(de.getDay()));
    System.out.println("Hours: " + Integer.toString(de.getHours()));
    System.out.println("Minutes: " + Integer.toString(de.getMinutes()));
    System.out.println("Seconds: " + Integer.toString(de.getSeconds()));

    // test setting of the year
    de.setYear(1967);
    System.out.println("Date: " + de.getDate());

    // test setting of the hour with a number larger than 24
    de.setHours(28);
    System.out.println("Date: " + de.getDate());
    System.out.println("Hours: " + Integer.toString(de.getHours()));

    // test setting of the hour with AmPm property
    // 1 PM
    de.setUsingAmPm(true);
    de.setHours(1);
    de.setAmPm("PM");
    System.out.println("Date: " + de.getDate());
    System.out.println("Hours: " + Integer.toString(de.getHours()) +
                       " " + de.getAmPm());

    // test setting of the hour with AmPm property
    // 12 PM/noon
    de.setHours(12);
    de.setAmPm("PM");
    System.out.println("Date: " + de.getDate());
    System.out.println("Hours: " + Integer.toString(de.getHours()) +
                       " " + de.getAmPm());

    // test setting of the hour with AmPm property
    // 12 AM/midnight
    de.setHours(12);
    de.setAmPm("AM");
    System.out.println("Date: " + de.getDate());
    System.out.println("Hours: " + Integer.toString(de.getHours()) +
                       " " + de.getAmPm());

    // test setting time from AM to PM
    // 12 PM
    de.setPm(true);
    System.out.println("Date: " + de.getDate());
    System.out.println("Hours: " + Integer.toString(de.getHours()) +
                       " " + de.getAmPm());

    // test setting time from PM to AM
    // 12 AM
    de.setPm(false);
    System.out.println("Date: " + de.getDate());
    System.out.println("Hours: " + Integer.toString(de.getHours()) +
                       " " + de.getAmPm());

    // test setting of the hour with AmPm property
    // 1 AM
    de.setHours(1);
    de.setAmPm("AM");
    System.out.println("Date: " + de.getDate());
    System.out.println("Hours: " + Integer.toString(de.getHours()) +
                       " " + de.getAmPm());

    // test setting time from AM to PM
    // 1 PM
    de.setPm(true);
    System.out.println("Date: " + de.getDate());
    System.out.println("Hours: " + Integer.toString(de.getHours()) +
                       " " + de.getAmPm());

    // test UsingZeroBasedMonth property
    de.setUsingZeroBasedMonth(true);
    System.out.println("\nUsingZeroBasedMonth = true");
    System.out.println("Date: " + de.getDate());
    System.out.println("Month: " + Integer.toString(de.getMonth()));
    de.setUsingZeroBasedMonth(false);
    System.out.println("UsingZeroBasedMonth = false");
    System.out.println("Date: " + de.getDate());
    System.out.println("Month: " + Integer.toString(de.getMonth()));
  }
}
