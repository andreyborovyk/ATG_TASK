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

import java.util.*;

/**
 * Source for calendar event data that merely combines events from other event sources.
 * This allows intermixing events from more than one repository, or mixing events from
 * a repository with events from a relational view.
 * @author Lew Lasher
 * @version $Id: //app/portal/version/10.0.3/calendar/classes.jar/src/atg/portal/gear/calendar/CalendarMergedEventSource.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class CalendarMergedEventSource extends CalendarEventSource
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/calendar/classes.jar/src/atg/portal/gear/calendar/CalendarMergedEventSource.java#2 $$Change: 651448 $";

    public CalendarMergedEventSource()
    {
    }

    /**
    * Set of event sources to combine
    **/

    protected CalendarEventSource [] mEventSources;
    public CalendarEventSource [] getEventSources()
    {
        return mEventSources;
    }
    public void setEventSources(CalendarEventSource [] pEventSources)
    {
        mEventSources = pEventSources;
    }

    /**
    * Query the combined data sources by time range
    **/

    public void loadEvents(Date pStartTime, Date pEndTime)
        throws CalendarException
    {
        CalendarEventSource [] sources = getEventSources();
        int numSources = sources.length;

        for (int i=0; i<numSources; i++)
        {
            sources[i].loadEvents(pStartTime, pEndTime);
        }

        List mergedEventList = new ArrayList();

        for (int i=0; i<numSources; i++)
        {
            addSorted(mergedEventList, sources[i].getEvents());
        }


        setEvents(mergedEventList);
    }

    /**
    * Query the combined data sources by time range and by user ID
    **/

    public void loadEvents(String pUserId, Date pStartTime, Date pEndTime)
        throws CalendarException
    {
        CalendarEventSource [] sources = getEventSources();
        int numSources = sources.length;

        for (int i=0; i<numSources; i++)
        {
            sources[i].loadEvents(pUserId, pStartTime, pEndTime);
        }

        List mergedEventList = new ArrayList();

        for (int i=0; i<numSources; i++)
        {
            addSorted(mergedEventList, sources[i].getEvents());
        }

        setEvents(mergedEventList);
    }

    /**
    * Query the combined data sources by time range, user ID and gear ID
    **/

    public void loadEvents(String pGearId, String pUserId, Date pStartTime, Date pEndTime)
        throws CalendarException
    {
        CalendarEventSource [] sources = getEventSources();
        int numSources = sources.length;

        for (int i=0; i<numSources; i++)
        {
            sources[i].loadEvents(pGearId, pUserId, pStartTime, pEndTime);
        }

        List mergedEventList = new ArrayList();

        for (int i=0; i<numSources; i++)
        {
            addSorted(mergedEventList, sources[i].getEvents());
        }

        setEvents(mergedEventList);
    }

    /**
    * Merge two event lists, keeping in order by start date
    **/

    public void addSorted(List pList1, List pList2)
    {
        ListIterator iter = pList1.listIterator();
        int listIndex = -1;

        Iterator addIter = pList2.iterator();

        while (addIter.hasNext())
        {
            Object eventToAdd = addIter.next();

            if (! iter.hasNext())
            {
                iter.add(eventToAdd);
            }
            else
            {
                Object testEvent = iter.next();
                boolean inserted = false;

                while (getStartDate(eventToAdd).after(getStartDate(testEvent)) && ! inserted)
                {
                    if (iter.hasNext())
                        testEvent = iter.next();
                    else
                    {
                        iter.add(eventToAdd);
                        inserted = true;
                    }
                }

                if (! inserted)
                {
                    iter.previous();
                    iter.add(eventToAdd);
                }
            }
        }
    }

}
