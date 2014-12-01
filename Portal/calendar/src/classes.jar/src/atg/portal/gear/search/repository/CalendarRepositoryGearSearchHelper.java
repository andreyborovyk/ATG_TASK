//<ATGCOPYRIGHT>
// Copyright (C) 2002-2011 Art Technology Group, Inc.
// All Rights Reserved.  No use, copying or distribution of this
// work may be made except in accordance with a valid license
// agreement from Art Technology Group.  This notice must be
// included on all copies, modifications and derivatives of this
// work.
//
// Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES 
// ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, 
// INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
// LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
// MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
//
// "Dynamo" is a trademark of Art Technology Group, Inc.
//</ATGCOPYRIGHT>*/

package atg.portal.gear.search.repository;

import atg.portal.framework.GearDefinition;
import atg.portal.framework.Utilities;
import atg.portal.framework.search.*;
import atg.portal.framework.search.repository.*;
import atg.repository.*;

import java.util.Properties;
import java.lang.Boolean;
import javax.naming.NamingException;

/**
 * This is the implementation of RepositoryGearSearchHelper for the Calendar
 * Publisher Gear.
 * @author Malay Desai
 * @version $Id: //app/portal/version/10.0.3/calendar/classes.jar/src/atg/portal/gear/search/repository/CalendarRepositoryGearSearchHelper.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class CalendarRepositoryGearSearchHelper implements RepositoryGearSearchHelper 
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/calendar/classes.jar/src/atg/portal/gear/search/repository/CalendarRepositoryGearSearchHelper.java#2 $$Change: 651448 $";

  /**
   * Returns the associated gear definition key for this implementation.
   */
  public GearDefinitionKey getGearDefinitionKey() 
  {
    return new GearDefinitionKey("Calendar Gear", null, null);
  }

  SearchContext mSearchContext;
  
  /**
   * Sets the value of SearchContext
   */
  public void setSearchContext(SearchContext pSearchContext) 
  {
    mSearchContext = pSearchContext;
  }
  

  /**
   * Gets the value of SearchContext
   */
  public SearchContext getSearchContext() 
  {
    return mSearchContext;
  }
  
  /**
   * Returns the full repository path for the gear's repository.
   */
  public String getRepositoryPath() 
  {
    return "/atg/portal/gear/calendar/CalendarRepository";
  }

  /**
   * Returns the name of the item type (item-descriptor) that will be searched
   */
  public String getItemType() 
  {
    return "detailEvent";
  }

  /**
   *  Returns QueryParams that will be added to the link for the gear
   */
  public Properties getQueryParams(SearchResultFields pResult) 
  {
    String eventId = (String) (pResult.getFields().get("repositoryId"));

    Properties params = new Properties();
    params.setProperty("action", "show_event");
    params.setProperty("eventType", "detail-event");
    params.setProperty("back", "month_shared");
    params.setProperty("event_id", eventId);
    
    return params;
  }

  /**
   * Returns one line search result description
   */
  public String getDescription(SearchResultFields pResult) 
  {
    String desc = (String) (pResult.getFields().get("name"));
    return desc;
  }

  /**
   * Returns short summary for the search result
   */
  public String getSummary(SearchResultFields pResult) 
  {
    String summary = (String) (pResult.getFields().get("description"));
    return summary;
    
  }

  /**
   * Returns the list of property names that should be searched in the given
   * item-descriptor. If none is returned, all properties will be searched.
   */
  public String[] getProperties() 
  {

    
    String[] props = {"detailEvent.name", "detailEvent.description",
                      "detailEvent.owner", "detailEvent.address1", "detailEvent.address2",
                      "detailEvent.address3", "detailEvent.city", "detailEvent.state",
                      "detailEvent.country", "detailEvent.postalCode",
                      "detailEvent.contactName", "detailEvent.contactPhone",
                      "detailEvent.contactEmail"};
    return props;
    
    
  }

  /**
   * Returns the name of the property that stores the gearId associated with
   * each item-descriptor.
   */
  public String getGearIdProperty() 
  {
    return "gearId";
  }

  /**
   * Returns a Query that will be 'AND'ed with the final query to carry out
   * the search for this gear definition. This is an advanced feature to
   * restrict the search query. This query must be created on the same
   * item-descriptor defined in getItemType method. Return a null if not using
   * this feature.
   */
  public Query advancedQuery() 
  {
    Query q = null;
    
    try {
      
      
      Repository calendarRepository = (Repository) Utilities.lookup("dynamo:/atg/portal/gear/calendar/CalendarRepository");  
      RepositoryItemDescriptor eventDesc = calendarRepository.getItemDescriptor("detailEvent");     
      RepositoryView eventView = eventDesc.getRepositoryView();    
      QueryBuilder calQb = eventView.getQueryBuilder();     
      
      QueryExpression trueExp = calQb.createConstantQueryExpression(Boolean.TRUE);
      QueryExpression propExp = calQb.createPropertyQueryExpression("publicEvent");
      q = calQb.createComparisonQuery(propExp, trueExp, QueryBuilder.EQUALS);
      
      
      
      
    }

    catch (RepositoryException ex) {
      System.err.println(ex);
    }

    catch (NamingException nex) {
      System.err.println("Error finding CalendarRepository: " + nex);
    }
    
    return q;
    
  }
  
}
