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

package atg.portal.gear.calendar;

import atg.nucleus.GenericService;
import java.util.*;
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;

/**
 * Base class for event sources for calendar event data.
 * Provides for querying for events by date range or by date range plus profile (user) ID.
 * Concrete subclasses must implement these queries by implementing the loadEvents methods.
 * This class also provides for pseudo-queries, also by date range or by date range plus
 * user ID, against any event source, by "manually" filtering the events already loaded into
 * the event source.  In order to do this filtering, however, it is assumed that all events,
 * from whatever concrete event source, contain bean properties with the same property names
 * for start date, end date, and user ID.
 *<P>
 * This class also has methods to get the start date, end date, and user ID from any
 * event.  And methods to determine the overlap, if any, between a given event and a
 * given time range.
 * @see atg.portal.gear.calendar.CalendarSubsetEventSource
 * @author Lew Lasher
 * @version $Id: //app/portal/version/10.0.3/calendar/classes.jar/src/atg/portal/gear/calendar/CalendarEventSource.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

abstract public class CalendarEventSource extends GenericService implements CalendarConstants
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/calendar/classes.jar/src/atg/portal/gear/calendar/CalendarEventSource.java#2 $$Change: 651448 $";


    private Date BEGINNING_OF_TIME = new Date(0);
    private Date END_OF_TIME = new Date(java.lang.Integer.MAX_VALUE);

    protected List mEvents = null;

    /**
    * The name of the property for start date, for filtering events
    **/

    protected String mStartDatePropertyName = defaultStartDatePropertyName;
    public String getStartDatePropertyName()
    {
        return mStartDatePropertyName;
    }
    public void setStartDatePropertyName(String pStartDatePropertyName)
    {
        mStartDatePropertyName = pStartDatePropertyName;
    }

    /**
    * The name of the property for end date, for filtering events
    **/

    protected String mEndDatePropertyName = defaultEndDatePropertyName;
    public String getEndDatePropertyName()
    {
        return mEndDatePropertyName;
    }
    public void setEndDatePropertyName(String pEndDatePropertyName)
    {
        mEndDatePropertyName = pEndDatePropertyName;
    }

    /**
    * The name of the property for identifying users, for filtering events
    **/

    protected String mUserPropertyName = defaultUserPropertyName;
    public String getUserPropertyName()
    {
        return mUserPropertyName;
    }
    public void setUserPropertyName(String pUserPropertyName)
    {
        mUserPropertyName = pUserPropertyName;
    }

    public List getEvents()
    {
        return mEvents;
    }
    public void setEvents(List pEvents)
    {
        mEvents = pEvents;
    }

    public boolean hasEvents()
    {
        Collection events = getEvents();
        if (events == null)
            return false;

        return (events.size() > 0);
    }

    /**
    * Query the event source by date range
    **/
    abstract public void loadEvents(Date pStartTime, Date pEndTime)
        throws CalendarException;

    /**
    * Query the event source by date range and by user ID
    **/
    abstract public void loadEvents(String pUserId, Date pStartTime, Date pEndTime)
        throws CalendarException;

    /**
    * Query the event source by date range, user ID and gear ID
    **/
    abstract public void loadEvents(String gearId, String pUserId, Date pStartTime, Date pEndTime)
        throws CalendarException;

    public List getSubset(String pUserId, Date pStartTime, Date pEndTime)
    {
        return getEventsInRangeForUser(pUserId, pStartTime, pEndTime);
    }

    public List getSubset(Date pStartTime, Date pEndTime)
    {
        return getEventsInRange(pStartTime, pEndTime);
    }

    public List getSubset(String gearId, String pUserId, Date pStartTime, Date pEndTime)
    {
        return getEventsInRange(pStartTime, pEndTime);
    }

    protected void initEvents()
    {
        mEvents = new ArrayList();
    }

    public Iterator getEventIterator()
    {
        return getEvents().iterator();
    }

    public Date getStartDate(Object pObject)
    {
        try
        {
            return (Date) DynamicBeans.getPropertyValue(pObject, getStartDatePropertyName());
        }
        catch (PropertyNotFoundException e)
        {
            return null;
        }
    }

    public Date getEndDate(Object pObject)
    {
        try
        {
            return (Date) DynamicBeans.getPropertyValue(pObject, getEndDatePropertyName());
        }
        catch (PropertyNotFoundException e)
        {
            return null;
        }
    }

    public String getUser(Object pObject)
    {
        try
        {
            return (String) DynamicBeans.getPropertyValue(pObject, getUserPropertyName());
        }
        catch (PropertyNotFoundException e)
        {
            return null;
        }
    }

    /**
    * Filter an event source's events, by date range
    **/

    public List getEventsInRange(Date pStartDate, Date pEndDate)
    {
        List result = new ArrayList();
        Iterator i = getEventIterator();

        // This can be optimized given the knowledge that the current events list is
        // sorted in chronological order

        while (i.hasNext())
        {
            Object event = i.next();
            if (getStartDate(event).before(pEndDate) && getEndDate(event).after(pStartDate))
                result.add(event);
        }

        return result;
    }

    /**
    * Filter an event source's events, by date range and by user ID
    * <P>
    * N.B. currently this ignores the user ID supplied, because the current
    * event sources do not share a common property name for user ID
    **/

    public List getEventsInRangeForUser(String pUserId, Date pStartDate, Date pEndDate)
    {
        List result = new ArrayList();
        Iterator i = getEventIterator();

        // This can be optimized given the knowledge that the current events list is
        // sorted in chronological order

        while (i.hasNext())
        {
            Object event = i.next();
            if (getStartDate(event).before(pEndDate) && getEndDate(event).after(pStartDate)
//                      && getUser(event).equals(pUserId)
               )
                result.add(event);
        }

        return result;
    }

    /**
    * Determine whether the event source has any events that occur within a given time range
    **/

    public boolean anyEventsInRange(Date pStartDate, Date pEndDate)
    {
        List anyInRange = getEventsInRange(pStartDate, pEndDate);
        return ! anyInRange.isEmpty();
    }

    /**
    * Determine whether the event source has any events that occur prior to a given time range
    **/

    public boolean anyEventsBeforeRange(Date pStartDate)
    {
        Collection anyInRange = getEventsInRange(BEGINNING_OF_TIME, pStartDate);
        return ! anyInRange.isEmpty();
    }

    /**
    * Determine whether the event source has any events that occur after a given time range
    **/

    public boolean anyEventsAfterRange(Date pEndDate)
    {
        List anyInRange = getEventsInRange(pEndDate, END_OF_TIME);
        return ! anyInRange.isEmpty();
    }

    /**
    * Determine whether the event source has any events that occur before, after, and/or
    * during given a time range
    **/

    public int getOverlap(Date pStartDate, Date pEndDate)
    {
        if (! anyEventsInRange(pStartDate, pEndDate))
            return RANGE_NONE;

        boolean anyBefore = anyEventsBeforeRange(pStartDate);
        boolean anyAfter = anyEventsAfterRange(pEndDate);

        if (anyBefore && anyAfter)
            return RANGE_MIDDLE;

        if (! anyBefore && !anyAfter)
            return RANGE_START_END;

        if (anyBefore)
            return RANGE_END;
        else
            return RANGE_START;
    }

    /**
    * Determine whether a given event has any overlap with a given time range, and,
    * if so, whether the event starts within the range, ends within the range, or both.
    **/

    public int getOverlap(Object pEvent, Date pStartDate, Date pEndDate)
    {
        Date eventStartDate = getStartDate(pEvent);
        Date eventEndDate = getEndDate(pEvent);

        boolean anyBefore = eventStartDate.before(pStartDate);
        boolean anyAfter = eventEndDate.after(pEndDate);

        if (eventStartDate.after(pEndDate) || eventEndDate.before(pStartDate))
            return RANGE_NONE;
        if (anyBefore && anyAfter)
            return RANGE_MIDDLE;
        if (!anyBefore && !anyAfter)
            return RANGE_START_END;
        if (anyBefore)
            return RANGE_END;
        else
            return RANGE_START;
    }

}
