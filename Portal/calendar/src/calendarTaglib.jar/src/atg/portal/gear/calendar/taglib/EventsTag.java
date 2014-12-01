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
import atg.portal.gear.calendar.RepositoryConstants;
import atg.portal.gear.calendar.CalendarEventSource;
import atg.portal.gear.calendar.CalendarException;
import javax.servlet.jsp.PageContext;
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;

/**
 * JSP Tag to create obtain a List of calendar events.
 * Uses a CalendarEventSource object to supply the events.
 * Queries based on a time range, and optionally, by user (profile) ID,
 * and/or a gear ID, to pull "public" events for a gear instance
 * @author Lew Lasher, Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/calendar/calendarTaglib.jar/src/atg/portal/gear/calendar/taglib/EventsTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class EventsTag extends TagSupport implements RepositoryConstants
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/calendar/calendarTaglib.jar/src/atg/portal/gear/calendar/taglib/EventsTag.java#2 $$Change: 651448 $";

  final static String   RESOURCE_BUNDLE = "atg.portal.gear.calendar.CalendarResources";

  public EventsTag() {}

  String                mId;
  public String getId() {return mId;}
  public void setId(String pId) {mId = pId;}

  ResourceBundle        mBundle;

  /**
  * Required attribute: start of the date range to query for
  **/
  protected Date mStartTime;
  public Date getStartTime()
  {
    return mStartTime;
  }
  public void setStartTime(Date pStartTime)
  {
    mStartTime = pStartTime;
  }

  /**
  * Required attribute: end of the date range to query for
  **/
  protected Date mEndTime;
  public Date getEndTime()
  {
    return mEndTime;
  }
  public void setEndTime(Date pEndTime)
  {
    mEndTime = pEndTime;
  }

  /**
  * Optional attribute: user (profile) ID to query for
  **/
  protected String mUserId;
  public String getUserId()
  {
    return mUserId;
  }
  public void setUserId(String pUserId)
  {
    mUserId = pUserId;
  }

  /**
  * Optional attribute: gear ID to query public events for a gear instance
  **/
  protected String mGearId;
  public String getGearId()
  {
    return mGearId;
  }
  public void setGearId(String pGearId)
  {
    mGearId = pGearId;
  }
  /**
  * Required attribute: object to supply events
  **/
  protected CalendarEventSource mEventSource;
  public CalendarEventSource getEventSource()
  {
    return mEventSource;
  }
  public void setEventSource(CalendarEventSource pEventSource)
  {
    mEventSource = pEventSource;
  }

  public List getEvents()
  {
    return (List) getEventSource().getEvents();
  }

  public boolean hasEvents()
  {
    List eventList = getEvents();
    return (eventList != null && eventList.size() > 0);
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

    CalendarEventSource source = getEventSource();

    try
    {
        // Query the event source by date range, and, optionally, by user ID and/or gear ID

        if (getUserId() == null)
            source.loadEvents(getStartTime(), getEndTime());
        else if (getGearId()==null)
            source.loadEvents(getUserId(), getStartTime(), getEndTime());
	else
            source.loadEvents(getGearId(), getUserId(), getStartTime(), getEndTime());

    }
    catch (CalendarException e)
    {
        throw new JspException(e.getMessage());
    }

    return EVAL_BODY_INCLUDE;
  }

  public int doEndTag() throws JspException
  {
    return EVAL_PAGE;
  }

  /**
  * Public accessor method for code in CalendarEventSource that determines whether
  * a given event overlaps a given time range, and, if so, whether the event starts
  * and/or ends within the time range.
  * @see atg.portal.gear.calendar.CalendarEventSource
  * @see atg.portal.gear.calendar.CalendarConstants
  **/

  public int getOverlap(Object pEvent)
  {
    return getEventSource().getOverlap(pEvent, getStartTime(), getEndTime());
  }

  /**
  * Get the value of a given property for a given event
  **/

  public Object getPropertyValue(Object pEvent, String pPropertyName)
    throws PropertyNotFoundException
  {
    return DynamicBeans.getPropertyValue(pEvent, pPropertyName);
  }

}
