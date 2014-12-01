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

import atg.repository.*;
import java.util.*;

/**
 * SQL Repository-based event source for calendar event data.
 * @author Lew Lasher
 * @version $Id: //app/portal/version/10.0.3/calendar/classes.jar/src/atg/portal/gear/calendar/CalendarRepositoryEventSource.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class CalendarRepositoryEventSource
    extends CalendarEventSource
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/calendar/classes.jar/src/atg/portal/gear/calendar/CalendarRepositoryEventSource.java#2 $$Change: 651448 $";


    protected Repository mRepository;
    public Repository getRepository()
    {
        return mRepository;
    }
    public void setRepository(Repository pRepository)
    {
        mRepository = pRepository;
    }

    /**
    * Name of the repository item for events
    **/

    protected String mItemDescriptorName;
    public String getItemDescriptorName()
    {
        return mItemDescriptorName;
    }
    public void setItemDescriptorName(String pItemDescriptorName)
    {
        mItemDescriptorName = pItemDescriptorName;
    }

    protected String mStartTimePropertyName;
    public String getStartTimePropertyName()
    {
        return mStartTimePropertyName;
    }
    public void setStartTimePropertyName(String pStartTimePropertyName)
    {
        mStartTimePropertyName = pStartTimePropertyName;
    }

    protected String mEndTimePropertyName;
    public String getEndTimePropertyName()
    {
        return mEndTimePropertyName;
    }
    public void setEndTimePropertyName(String pEndTimePropertyName)
    {
        mEndTimePropertyName = pEndTimePropertyName;
    }

    protected String mProfileIdPropertyName;
    public String getProfileIdPropertyName()
    {
        return mProfileIdPropertyName;
    }
    public void setProfileIdPropertyName(String pProfileIdPropertyName)
    {
        mProfileIdPropertyName = pProfileIdPropertyName;
    }

    protected String mGearIdPropertyName;
    public String getGearIdPropertyName()
    {
        return mGearIdPropertyName;
    }
    public void setGearIdPropertyName(String pGearIdPropertyName)
    {
        mGearIdPropertyName = pGearIdPropertyName;
    }

    protected String mPublicEventPropertyName;
    public String getPublicEventPropertyName()
    {
        return mPublicEventPropertyName;
    }
    public void setPublicEventPropertyName(String pPublicEventPropertyName)
    {
        mPublicEventPropertyName = pPublicEventPropertyName;
    }

    protected String mSortPropertyName;
    public String getSortPropertyName()
    {
        return mSortPropertyName;
    }
    public void setSortPropertyName(String pSortPropertyName)
    {
        mSortPropertyName = pSortPropertyName;
    }

    /**
    * Query the event source by date range
    **/

    public void loadEvents(Date pStartTime, Date pEndTime)
        throws CalendarException
    {
        if (isLoggingDebug())
          logDebug("Loading calendar events from SQL repository for date range from " + pStartTime + " to " + pEndTime);

        try
        {
            RepositoryItem [] events =
                getEventsFromRepository(pStartTime, pEndTime);

            initEvents();

            if (events != null)
            {
                List eventList = mEvents;
                for (int i=0; i<events.length; i++)
                  eventList.add(events[i]);
            }

            if (isLoggingDebug())
                logDebug("Loaded " + getEvents().size() + " events from SQL repository");
        }
        catch (RepositoryException e)
        {
            if (isLoggingError())
                logError("Error when attempting to load events from SQL repository: " + e.getMessage());
            throw new CalendarException(e.getMessage());
        }
    }

    /**
    * Query the event source by date range and by user ID
    **/

    public void loadEvents(String pUserId, Date pStartTime, Date pEndTime)
        throws CalendarException
    {
        if (isLoggingDebug())
          logDebug("Loading calendar events from SQL repository for user ID " + pUserId + " for date range from " + pStartTime + " to " + pEndTime);

        try
        {
            RepositoryItem [] events =
                getEventsFromRepository(pUserId, pStartTime, pEndTime);

            initEvents();

            if (events != null)
            {
                List eventList = mEvents;
                for (int i=0; i<events.length; i++)
                  eventList.add(events[i]);
            }

            if (isLoggingDebug())
                logDebug("Loaded " + getEvents().size() + " events from SQL repository");
        }
        catch (RepositoryException e)
        {
            if (isLoggingError())
                logError("Error when attempting to load events from SQL repository: " + e.getMessage());
            throw new CalendarException(e.getMessage());
        }
    }

    /**
    * Query the event source by date range, user ID and gear ID
    **/

    public void loadEvents(String pGearId, String pUserId, Date pStartTime, Date pEndTime)
        throws CalendarException
    {
        if (isLoggingDebug())
          logDebug("Loading calendar events from SQL repository for user ID " + pUserId + " gear ID: " + pGearId + " for date range from " + pStartTime + " to " + pEndTime);

        try
        {
            RepositoryItem [] events =
                getEventsFromRepository(pGearId, pUserId, pStartTime, pEndTime);

            initEvents();

            if (events != null)
            {
                List eventList = mEvents;
                for (int i=0; i<events.length; i++)
                  eventList.add(events[i]);
            }

            if (isLoggingDebug())
                logDebug("Loaded " + getEvents().size() + " events from SQL repository");
        }
        catch (RepositoryException e)
        {
            if (isLoggingError())
                logError("Error when attempting to load events from SQL repository: " + e.getMessage());
            throw new CalendarException(e.getMessage());
        }
    }

    /**
    * time range only
    **/
    protected RepositoryItem [] getEventsFromRepository(Date pStartTime, Date pEndTime)
        throws RepositoryException
    {
        RepositoryView rv = getRepository().getView(getItemDescriptorName());
        QueryBuilder qb = rv.getQueryBuilder();

        Query query = buildQuery(qb, pStartTime, pEndTime);
        SortDirectives sortDirectives = buildSortDirectives();
        RepositoryItem [] result = rv.executeQuery(query, sortDirectives);

        return result;
    }

    /**
    * time range and user ID
    **/
    protected RepositoryItem [] getEventsFromRepository(
        String pUserId, Date pStartTime, Date pEndTime)
        throws RepositoryException
    {
        RepositoryView rv = getRepository().getView(getItemDescriptorName());
        QueryBuilder qb = rv.getQueryBuilder();

        Query query = buildQuery(qb, pUserId, pStartTime, pEndTime);
        SortDirectives sortDirectives = buildSortDirectives();
        if (isLoggingDebug())
                logDebug("Event Query: " + query.toString());
        RepositoryItem [] result = rv.executeQuery(query, sortDirectives);

        return result;
    }

    /**
    * time range, user ID, gear ID
    **/
    protected RepositoryItem [] getEventsFromRepository(
        String pGearId, String pUserId, Date pStartTime, Date pEndTime)
        throws RepositoryException
    {
        RepositoryView rv = getRepository().getView(getItemDescriptorName());
        QueryBuilder qb = rv.getQueryBuilder();

        Query query = buildQuery(qb, pGearId, pUserId, pStartTime, pEndTime);
        SortDirectives sortDirectives = buildSortDirectives();
        if (isLoggingDebug())
                logDebug("Event Query: " + query.toString());
        RepositoryItem [] result = rv.executeQuery(query, sortDirectives);

        return result;
    }

    /**
    * Build query for time range only
    **/

    protected Query buildQuery(QueryBuilder pQueryBuilder, Date pStartTime, Date pEndTime)
        throws RepositoryException
    {
        Query timeQuery = buildTimeQuery(pQueryBuilder, pStartTime, pEndTime);
        return timeQuery;
    }

    /**
    * Build query for user ID and time range
    **/

    protected Query buildQuery(QueryBuilder pQueryBuilder, String pUserId, Date pStartTime, Date pEndTime)
        throws RepositoryException
    {
        Query timeQuery = buildTimeQuery(pQueryBuilder, pStartTime, pEndTime);
        Query profileQuery = buildProfileQuery(pQueryBuilder, pUserId);

        Query [] queriesToAndTogether = { timeQuery, profileQuery } ;

        return pQueryBuilder.createAndQuery(queriesToAndTogether);
    }

    /**
    * Build query for gear ID, user ID and time range
    **/
    protected Query buildQuery(QueryBuilder pQueryBuilder, String pGearId, String pUserId, Date pStartTime, Date pEndTime)
        throws RepositoryException
    {
        Query timeQuery = buildTimeQuery(pQueryBuilder, pStartTime, pEndTime);
        Query profileQuery = buildProfileQuery(pQueryBuilder, pUserId, pGearId);
        Query gearQuery = buildGearQuery(pQueryBuilder, pGearId);

        Query [] queriesToAndTogether = { timeQuery, profileQuery } ;
        Query userRange =  pQueryBuilder.createAndQuery(queriesToAndTogether);

        Query [] gearAndRange = { timeQuery, gearQuery } ;
        Query gearRange =  pQueryBuilder.createAndQuery(gearAndRange);

	Query [] userGearRange = {userRange, gearRange};

        return pQueryBuilder.createOrQuery(userGearRange);
    }

    protected Query buildTimeQuery(QueryBuilder pQueryBuilder, Date pStartTime, Date pEndTime)
        throws RepositoryException
    {
        QueryExpression startTimeProperty =
            pQueryBuilder.createPropertyQueryExpression(getStartTimePropertyName());
        QueryExpression endTimeProperty =
            pQueryBuilder.createPropertyQueryExpression(getEndTimePropertyName());
        QueryExpression startTimeValue =
            pQueryBuilder.createConstantQueryExpression(pStartTime);
        QueryExpression endTimeValue =
            pQueryBuilder.createConstantQueryExpression(pEndTime);

        Query query1 = pQueryBuilder.createComparisonQuery(
            startTimeProperty, endTimeValue, pQueryBuilder.LESS_THAN);
        Query query2 = pQueryBuilder.createComparisonQuery(
            endTimeProperty, startTimeValue, pQueryBuilder.GREATER_THAN);
        Query [] bothQueries = { query1, query2 };
        return pQueryBuilder.createAndQuery(bothQueries);
    }

    protected Query buildProfileQuery(QueryBuilder pQueryBuilder, String pUserId)
        throws RepositoryException
    {
        QueryExpression propertyNameExpression =
            pQueryBuilder.createPropertyQueryExpression(getProfileIdPropertyName());
        QueryExpression propertyValueExpression =
            pQueryBuilder.createConstantQueryExpression(pUserId);
        Query query =
            pQueryBuilder.createComparisonQuery(
                propertyNameExpression, propertyValueExpression, pQueryBuilder.EQUALS);
        return query;
    }

    protected Query buildProfileQuery(QueryBuilder pQueryBuilder, String pUserId, String pGearId)
        throws RepositoryException
    {
        QueryExpression propertyNameExpression =
            pQueryBuilder.createPropertyQueryExpression(getProfileIdPropertyName());
        QueryExpression propertyValueExpression =
            pQueryBuilder.createConstantQueryExpression(pUserId);
        Query userQuery =
            pQueryBuilder.createComparisonQuery(
                propertyNameExpression, propertyValueExpression, pQueryBuilder.EQUALS);

        QueryExpression propertyNameExpression2 =
            pQueryBuilder.createPropertyQueryExpression(getGearIdPropertyName());
        QueryExpression propertyValueExpression2 =
            pQueryBuilder.createConstantQueryExpression(pGearId);
        Query gearQuery =
            pQueryBuilder.createComparisonQuery(
                propertyNameExpression2, propertyValueExpression2, pQueryBuilder.EQUALS);

	 Query [] bothQueries = { userQuery, gearQuery };
	 return pQueryBuilder.createAndQuery(bothQueries);
    }

    protected Query buildGearQuery(QueryBuilder pQueryBuilder, String pGearId)
        throws RepositoryException
    {
        QueryExpression propertyNameExpression =
            pQueryBuilder.createPropertyQueryExpression(getGearIdPropertyName());
        QueryExpression propertyValueExpression =
            pQueryBuilder.createConstantQueryExpression(pGearId);
        Query gearQuery =
            pQueryBuilder.createComparisonQuery(
                propertyNameExpression, propertyValueExpression, pQueryBuilder.EQUALS);
        QueryExpression propertyNameExpression2 =
            pQueryBuilder.createPropertyQueryExpression(getPublicEventPropertyName());
        QueryExpression propertyValueExpression2 =
            pQueryBuilder.createConstantQueryExpression(Boolean.TRUE);
        Query publicQuery =
            pQueryBuilder.createComparisonQuery(
                propertyNameExpression2, propertyValueExpression2, pQueryBuilder.EQUALS);
	 Query [] bothQueries = { gearQuery, publicQuery };
	 return pQueryBuilder.createAndQuery(bothQueries);
    }

    protected SortDirectives buildSortDirectives()
    {
        SortDirectives result = new SortDirectives();
        result.addDirective(new SortDirective(getSortPropertyName()));
        return result;
    }

}
