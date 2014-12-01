/*<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
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
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.service.util;

import java.util.*;
import java.beans.*;
import java.io.Serializable;

import atg.nucleus.*;
import atg.beans.*;

/**
 * <p>This is a nucleus component that returns aspects of the current
 * date (such as the year, date, month, etc.).  The date can also be
 * set manually, in which case all of those fields are set from the
 * specified date.
 *
 * @author Nathan Abramson
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/service/util/CurrentDate.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo
 *   description: Information about the current date
 *   attribute: functionalComponentCategory Services
 *   attribute: featureComponentCategory Date
 *   attribute: icon /atg/ui/common/images/servicecomp.gif
 **/

public
class CurrentDate
extends GenericService
implements Serializable
{
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/service/util/CurrentDate.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Resources
  //-------------------------------------

  static ResourceBundle sResources =
  ResourceBundle.getBundle("atg.service.util.CurrentDateResources", atg.service.dynamo.LangLicense.getLicensedDefault());

  //-------------------------------------
  // Constants
  //-------------------------------------

  public static final String [] MONTH_NAMES = {
    null,
    sResources.getString ("month1"),
    sResources.getString ("month2"),
    sResources.getString ("month3"),
    sResources.getString ("month4"),
    sResources.getString ("month5"),
    sResources.getString ("month6"),
    sResources.getString ("month7"),
    sResources.getString ("month8"),
    sResources.getString ("month9"),
    sResources.getString ("month10"),
    sResources.getString ("month11"),
    sResources.getString ("month12")
  };

  public static final String [] SHORT_MONTH_NAMES = {
    null,
    sResources.getString ("shortMonth1"),
    sResources.getString ("shortMonth2"),
    sResources.getString ("shortMonth3"),
    sResources.getString ("shortMonth4"),
    sResources.getString ("shortMonth5"),
    sResources.getString ("shortMonth6"),
    sResources.getString ("shortMonth7"),
    sResources.getString ("shortMonth8"),
    sResources.getString ("shortMonth9"),
    sResources.getString ("shortMonth10"),
    sResources.getString ("shortMonth11"),
    sResources.getString ("shortMonth12")
  };

  public static final String [] WEEKDAY_NAMES = {
    null,
    sResources.getString ("weekday1"),
    sResources.getString ("weekday2"),
    sResources.getString ("weekday3"),
    sResources.getString ("weekday4"),
    sResources.getString ("weekday5"),
    sResources.getString ("weekday6"),
    sResources.getString ("weekday7")
  };

  public static final String [] SHORT_WEEKDAY_NAMES = {
    null,
    sResources.getString ("shortWeekday1"),
    sResources.getString ("shortWeekday2"),
    sResources.getString ("shortWeekday3"),
    sResources.getString ("shortWeekday4"),
    sResources.getString ("shortWeekday5"),
    sResources.getString ("shortWeekday6"),
    sResources.getString ("shortWeekday7")
  };

  //-------------------------------------
  // Beaninfo
  //-------------------------------------

  static {
    setupBeanInfo ();
  }

  //-------------------------------------
  // Properties
  //-------------------------------------

  int mYear;
  int mMonth;
  String mMonthName;
  String mShortMonthName;
  int mDate;
  int mHour;
  int mMinute;
  int mSecond;
  int mDayOfWeek;
  String mDayOfWeekName;
  String mShortDayOfWeekName;
  int mDayOfWeekInMonth;
  int mWeekOfMonth;

  Date mDateAsDate;
  Date mHourAsDate;
  Date mMinuteAsDate;
  Date mSecondAsDate;


  //-------------------------------------
  // property: MinimizeDateConstruction
  boolean mMinimizeDateConstruction = false;

  /**
   * Sets property MinimizeDateConstruction
   **/
  public void setMinimizeDateConstruction(boolean pMinimizeDateConstruction) {
    mMinimizeDateConstruction = pMinimizeDateConstruction;
  }

  /**
   * Returns property MinimizeDateConstruction
   **/
  public boolean isMinimizeDateConstruction() {
    return mMinimizeDateConstruction;
  }


  //-------------------------------------
  // Member variables
  //-------------------------------------

  /** Flag if the time has been set explicitly */
  boolean mTimeIsSet = false;
  long mSetTime;
  Date mSetDate = null;
  java.sql.Timestamp mSetTimestamp = null;

  /** The next day when the cached date should be reset */
  long mNextDate;
  public long getNextDate () { return mNextDate; }

  /** The next hour when the cached date should be reset */
  long mNextHour;
  public long getNextHour () { return mNextHour; }

  /** The next minute when the cached date should be reset */
  long mNextMinute;
  public long getNextMinute () { return mNextMinute; }

  /** The next second when the cached date should be reset */
  long mNextSecond;
  public long getNextSecond () { return mNextSecond; }

  //-------------------------------------
  // Constructor
  //-------------------------------------

  public CurrentDate ()
  {
    calculateDate ();
  }

  //-------------------------------------
  /**
   * Returns the time in milliseconds
   * @beaninfo
   *   description: The time in milliseconds
   **/
  public long getTime ()
  {
    return
      (mTimeIsSet) ?
      mSetTime :
      System.currentTimeMillis ();
  }

  //-------------------------------------
  /**
   * Sets the properties to reflect the specified time.  Once this is
   * called, this will no longer track the current time.
   **/
  public void setTime (long pTime)
  {
    setTimeAsDate (new Date (pTime));
  }

  //-------------------------------------
  /**
   * Returns the time in milliseconds as a java.util.Date.
   * @beaninfo
   *   description: The time as a date object
   **/
  public Date getTimeAsDate ()
  {
    if (mTimeIsSet) {
      if (mSetDate != null)
        return mSetDate;
      else
        return new Date(mSetTime);
    }
    else {
      return new Date (System.currentTimeMillis ());
    }
  }

  //-------------------------------------
  /**
   * Returns the time in milliseconds as a java.sql.Timestamp.
   * @beaninfo
   *   description: The time as a timestamp object
   **/
  public java.sql.Timestamp getTimeAsTimestamp ()
  {
    if (mTimeIsSet) {
      if (mSetTimestamp != null)
        return mSetTimestamp;
      else
        return new java.sql.Timestamp (mSetTime);
    }
    else {
      return new java.sql.Timestamp (System.currentTimeMillis ());
    }
  }



  //-------------------------------------
  /**
   * Sets the properties to reflect the specified date.  Once this is
   * called, this will no longer track the current time.
   **/
  public void setTimeAsDate (Date pDate)
  {
    if (pDate != null) {
      mSetTime = pDate.getTime ();
      if (isMinimizeDateConstruction()) {
        mSetDate = new Date(mSetTime);
        mSetTimestamp = new java.sql.Timestamp(mSetTime);
      }
      setupFromDate (pDate);
      mTimeIsSet = true;
    }
    else {
      mSetDate = null;
      mSetTimestamp = null;
      mSetTime = 0;
      mTimeIsSet = false;
    }
  }

  //-------------------------------------
  /**
   * Returns the date as an integer (e.g., 1998)
   * @beaninfo
   *   description: The current year
   **/
  public int getYear ()
  {
    validateDate ();
    return mYear;
  }

  //-------------------------------------
  /**
   * Returns the month as an integer (1-12)
   * @beaninfo
   *   description: The current month (1-12)
   **/
  public int getMonth ()
  {
    validateDate ();
    return mMonth;
  }

  //-------------------------------------
  /**
   * Returns the name of the month ("January", "February", etc.)
   * @beaninfo
   *   description: The full name of the current month
   **/
  public String getMonthName ()
  {
    validateDate ();
    return mMonthName;
  }

  //-------------------------------------
  /**
   * Returns the shortened name of the month ("Jan", "Feb", etc.)
   * @beaninfo
   *   description: The abbreviated name of the current month
   **/
  public String getShortMonthName ()
  {
    validateDate ();
    return mShortMonthName;
  }

  //-------------------------------------
  /**
   * Returns the day of the month (1-31)
   * @beaninfo
   *   description: The day of the month (1-31)
   **/
  public int getDate ()
  {
    validateDate ();
    return mDate;
  }

  //-------------------------------------
  /**
   * Returns the day of the month as a java.util.Date
   * @beaninfo
   *   description: The day of the month as a date object
   **/
  public Date getDateAsDate ()
  {
    validateDate ();
    return mDateAsDate;
  }

  //-------------------------------------
  /**
   * Returns the hour of the day (0-23)
   * @beaninfo
   *   description: The hour of the day (0-23)
   **/
  public int getHour ()
  {
    validateHour ();
    return mHour;
  }

  //-------------------------------------
  /**
   * Returns the hour of the day as a java.util.Date
   * @beaninfo
   *   description: The hour of the day as a date object
   **/
  public Date getHourAsDate ()
  {
    validateHour ();
    return mHourAsDate;
  }

  //-------------------------------------
  /**
   * Returns the minute of the hour (0-59)
   * @beaninfo
   *   description: The minute of the hour (0-59)
   **/
  public int getMinute ()
  {
    validateMinute ();
    return mMinute;
  }

  //-------------------------------------
  /**
   * Returns the minute of the hour as a java.util.Date
   * @beaninfo
   *   description: The minute of the hour as a date object
   **/
  public Date getMinuteAsDate ()
  {
    validateMinute ();
    return mMinuteAsDate;
  }

  //-------------------------------------
  /**
   * Returns the second of the minute (0-59)
   * @beaninfo
   *   description: The second of the minute (0-59)
   **/
  public int getSecond ()
  {
    validateSecond ();
    return mSecond;
  }

  //-------------------------------------
  /**
   * Returns the second of the minute as a java.util.Date
   * @beaninfo
   *   description: The second of the minute as a date object
   **/
  public Date getSecondAsDate ()
  {
    validateSecond ();
    return mSecondAsDate;
  }

  //-------------------------------------
  /**
   * Returns the day of the week (1-7)
   * @beaninfo
   *   description: The day of the week (1-7)
   **/
  public int getDayOfWeek ()
  {
    validateDate ();
    return mDayOfWeek;
  }

  //-------------------------------------
  /**
   * Returns the name of the day of the week ("Sunday", "Monday", etc.)
   * @beaninfo
   *   description: The full name of the day of the week
   **/
  public String getDayOfWeekName ()
  {
    validateDate ();
    return mDayOfWeekName;
  }

  //-------------------------------------
  /**
   * Returns the shortened name of the day of the week ("Sun", "Mon",
   * etc.)
   * @beaninfo
   *   description: The abbreviated name of the day of the week
   **/
  public String getShortDayOfWeekName ()
  {
    validateDate ();
    return mShortDayOfWeekName;
  }

  //-------------------------------------
  /**
   * Returns which occurrence (1 - 5) of the weekday in the month.
   * For example, the second Wednesday returns 2.
   * @beaninfo
   *   description: The occurrence (1-5) of this weekday in the month
   **/
  public int getDayOfWeekInMonth ()
  {
    validateDate ();
    return mDayOfWeekInMonth;
  }

  //-------------------------------------
  /**
   * Returns the week of the month (1-5).
   * @beaninfo
   *   description: The week of the month (1-5)
   **/
  public int getWeekOfMonth ()
  {
    validateDate ();
    return mWeekOfMonth;
  }

  //-------------------------------------
  // Validating the date
  //-------------------------------------
  /**
   * Makes sure the cached date is valid.
   **/
  void validateDate ()
  {
    if (mTimeIsSet) return;
    if (System.currentTimeMillis () >= mNextDate) {
      synchronized (this) {
        if (System.currentTimeMillis () >= mNextDate) {
          calculateDate ();
        }
      }
    }
  }

  //-------------------------------------
  /**
   * Makes sure the cached hour is valid.
   **/
  void validateHour ()
  {
    if (mTimeIsSet) return;
    if (System.currentTimeMillis () >= mNextHour) {
      synchronized (this) {
        if (System.currentTimeMillis () >= mNextHour) {
          calculateDate ();
        }
      }
    }
  }

  //-------------------------------------
  /**
   *
   * Makes sure the cached minute is valid.
   **/
  void validateMinute ()
  {
    if (mTimeIsSet) return;
    if (System.currentTimeMillis () >= mNextMinute) {
      synchronized (this) {
        if (System.currentTimeMillis () >= mNextMinute) {
          calculateDate ();
        }
      }
    }
  }

  //-------------------------------------
  /**
   *
   * Makes sure the cached second is valid.
   **/
  void validateSecond ()
  {
    if (mTimeIsSet) return;
    if (System.currentTimeMillis () >= mNextSecond) {
      synchronized (this) {
        if (System.currentTimeMillis () >= mNextSecond) {
          calculateDate ();
        }
      }
    }
  }

  //-------------------------------------
  /**
   *
   * This calculates the current date and sets up all internal fields
   **/
  synchronized void calculateDate ()
  {
    Date d = new Date (System.currentTimeMillis ());
    setupFromDate (d);
    setupNextFields (d);
  }

  //-------------------------------------
  /**
   *
   * Sets up the fields from the specified date
   **/
  synchronized void setupFromDate (Date pDate)
  {
    Calendar c = new GregorianCalendar ();
    c.setTime (pDate);

    mYear = c.get (Calendar.YEAR);
    mMonth = c.get (Calendar.MONTH) + 1;
    mMonthName =
      (mMonth >= 0 && mMonth < MONTH_NAMES.length) ?
      MONTH_NAMES [mMonth] :
      null;
    mShortMonthName =
      (mMonth >= 0 && mMonth < SHORT_MONTH_NAMES.length) ?
      SHORT_MONTH_NAMES [mMonth] :
      null;
    mDate = c.get (Calendar.DATE);
    mHour = c.get (Calendar.HOUR_OF_DAY);
    mMinute = c.get (Calendar.MINUTE);
    mSecond = c.get (Calendar.SECOND);
    mDayOfWeek = c.get (Calendar.DAY_OF_WEEK);
    mDayOfWeekName =
      (mDayOfWeek >= 0 && mDayOfWeek < WEEKDAY_NAMES.length) ?
      WEEKDAY_NAMES [mDayOfWeek] :
      null;
    mShortDayOfWeekName =
      (mDayOfWeek >= 0 && mDayOfWeek < SHORT_WEEKDAY_NAMES.length) ?
      SHORT_WEEKDAY_NAMES [mDayOfWeek] :
      null;
    mDayOfWeekInMonth = c.get (Calendar.DAY_OF_WEEK_IN_MONTH);
    mWeekOfMonth = c.get (Calendar.WEEK_OF_MONTH);
  }

  //-------------------------------------
  /**
   *
   * Sets up the nextDate, nextHour, nextMinute, nextSecond fields
   * from the given date.
   **/
  synchronized void setupNextFields (Date pDate)
  {
    {
      // Get next date
      Calendar c = new GregorianCalendar (mYear,
                                          mMonth - 1,
                                          mDate,
                                          0,
                                          0,
                                          0);
      mDateAsDate = c.getTime ();
      long t = mDateAsDate.getTime ();
      mNextDate = t + (60L * 60L * 24L * 1000L);
    }

    {
      // Get next hour
      Calendar c = new GregorianCalendar (mYear,
                                          mMonth - 1,
                                          mDate,
                                          mHour,
                                          0,
                                          0);
      mHourAsDate = c.getTime ();
      long t = mHourAsDate.getTime ();
      mNextHour = t + (60L * 60L * 1000L);
    }

    {
      // Get next minute
      Calendar c = new GregorianCalendar (mYear,
                                          mMonth - 1,
                                          mDate,
                                          mHour,
                                          mMinute,
                                          0);
      mMinuteAsDate = c.getTime ();
      long t = mMinuteAsDate.getTime ();
      mNextMinute = t + (60L * 1000L);
    }

    {
      // Get next second
      Calendar c = new GregorianCalendar (mYear,
                                          mMonth - 1,
                                          mDate,
                                          mHour,
                                          mMinute,
                                          mSecond);
      mSecondAsDate = c.getTime ();
      long t = mSecondAsDate.getTime ();
      mNextSecond = t + (1000L);
    }
  }

  //-------------------------------------
  // Setting up beaninfo
  //-------------------------------------
  /**
   *
   * Sets up the beaninfo for this class by setting property editors
   * for the properties.
   **/
  static void setupBeanInfo ()
  {
    // Set up the mapping from property name to property editor
    Dictionary d = new Hashtable ();
    d.put ("month",
           CurrentDateMonthPropertyEditor.class);
    d.put ("monthName",
           CurrentDateMonthNamePropertyEditor.class);
    d.put ("shortMonthName",
           CurrentDateShortMonthNamePropertyEditor.class);
    d.put ("date",
           CurrentDateDatePropertyEditor.class);
    d.put ("dayOfWeek",
           CurrentDateDayOfWeekPropertyEditor.class);
    d.put ("dayOfWeekName",
           CurrentDateDayOfWeekNamePropertyEditor.class);
    d.put ("shortDayOfWeekName",
           CurrentDateShortDayOfWeekNamePropertyEditor.class);
    d.put ("dayOfWeekInMonth",
           CurrentDateDayOfWeekInMonthPropertyEditor.class);
    d.put ("weekOfMonth",
           CurrentDateWeekOfMonthPropertyEditor.class);

    try {
      BeanInfo bi = Introspector.getBeanInfo (CurrentDate.class);
      PropertyDescriptor [] pds = bi.getPropertyDescriptors ();
      for (int i = 0; pds != null && i < pds.length; i++) {
        PropertyDescriptor pd = pds [i];
        if (pd != null) {
          Class peclass = (Class) d.get (pd.getName ());
          if (peclass != null) {
            pd.setPropertyEditorClass (peclass);
          }
        }
      }
    }
    catch (IntrospectionException exc) {
      exc.printStackTrace ();
    }
  }

  //-------------------------------------

}
