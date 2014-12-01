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
 * Source for calendar event data that obtains its data by pseudo-queries against
 * another CalendarEventSource.
 * This can be used, for example, so that the other CalendarEventSource can cache
 * the results of a query for a longer period of time (say, a month), and then
 * pseudo-queries can provide subsets of that cached query for smaller periods
 * of time (say, a day).
 * The actual pseudo-queries (filtering) are done in the base class CalendarEventSource
 * (although subclasses may override the methods that do the filtering).
 * @see CalendarEventSource
 * @author Lew Lasher
 * @version $Id: //app/portal/version/10.0.3/calendar/classes.jar/src/atg/portal/gear/calendar/CalendarSubsetEventSource.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class CalendarSubsetEventSource extends CalendarEventSource
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/calendar/classes.jar/src/atg/portal/gear/calendar/CalendarSubsetEventSource.java#2 $$Change: 651448 $";

    public CalendarSubsetEventSource()
    {
    }

    protected CalendarEventSource mParentEventSource;
    public CalendarEventSource getParentEventSource()
    {
        return mParentEventSource;
    }
    public void setParentEventSource(CalendarEventSource pParentEventSource)
    {
        mParentEventSource = pParentEventSource;
    }

    /**
    * Perform pseudo-query (filtering), by time range
    **/

    public void loadEvents(Date pStartTime, Date pEndTime)
    {
// This assumes that parent event source has already had its events loaded

        if (isLoggingDebug())
            logDebug("Obtaining subset of data for time range from " + pStartTime + " to " + pEndTime);

        setEvents(getParentEventSource().getEventsInRange(pStartTime, pEndTime));
    }

    /**
    * Perform pseudo-query (filtering), by time range and by user ID
    **/

    public void loadEvents(String pUserId, Date pStartTime, Date pEndTime)
    {
// This assumes that parent event source has already had its events loaded

        if (isLoggingDebug())
            logDebug("Obtaining subset of data for user " + pUserId + " for time range from " + pStartTime + " to " + pEndTime);

        setEvents(getParentEventSource().getEventsInRangeForUser(pUserId, pStartTime, pEndTime));
    }

    /**
    * Perform pseudo-query (filtering), by time range, user ID and gear ID
    **/

    public void loadEvents(String pGearId, String pUserId, Date pStartTime, Date pEndTime)
    {
// This assumes that parent event source has already had its events loaded

        if (isLoggingDebug())
            logDebug("Obtaining subset of data for user " + pUserId + " gear ID: " + pGearId + " for time range from " + pStartTime + " to " + pEndTime);

        setEvents(getParentEventSource().getEventsInRange(pStartTime, pEndTime));
    }
}
