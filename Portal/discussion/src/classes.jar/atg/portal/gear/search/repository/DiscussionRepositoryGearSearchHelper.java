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
import atg.portal.framework.search.*;
import atg.portal.framework.search.repository.*;
import atg.repository.Query;

import java.util.Properties;

/**
 * This is the implementation of RepositoryGearSearchHelper for the Discussion
 * Gear.
 * @author Malay Desai
 * @version $Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/search/repository/DiscussionRepositoryGearSearchHelper.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class DiscussionRepositoryGearSearchHelper implements RepositoryGearSearchHelper 
{

  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/search/repository/DiscussionRepositoryGearSearchHelper.java#2 $$Change: 651448 $";

  /**
   * Returns the associated gear definition key for this implementation.
   */
  public GearDefinitionKey getGearDefinitionKey() 
  {
    return new GearDefinitionKey("Discussion", null, null);
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
    return "/atg/portal/gear/discussion/DiscussionRepository";
  }

  /**
   * Returns the name of the item type (item-descriptor) that will be searched
   */
  public String getItemType() 
  {
    return "discussionSearch";
  }
  

  /**
   * Returns QueryParams that will be added to the link for the gear.
   */
  public Properties getQueryParams(SearchResultFields pResult) 
  {

    
    String forumId = (String) (pResult.getFields().get("forum_id"));
    String topicId = (String) (pResult.getFields().get("topic_id"));

    Properties params = new Properties();
    params.setProperty("action", "thread_list");
    params.setProperty("startRange", "0");
    params.setProperty("rangeSize", "10");
    params.setProperty("forumID", forumId);
    params.setProperty("topicID", topicId);
    
    return params;
    
  }

  /**
   * Returns one line search result description
   */
  public String getDescription(SearchResultFields pResult) 
  {
    String desc = (String) (pResult.getFields().get("subject"));
    return desc;
  }

  /**
   * Returns short summary for the search result
   */
  public String getSummary(SearchResultFields pResult) 
  {
    String summary = (String) (pResult.getFields().get("content"));
    return summary;
  }

  /**
   * Returns the list of property names that should be searched in the given
   * item-descriptor. If none is returned, all properties will be searched.
   */
  public String[] getProperties() 
  {
    String[] props = {"discussionSearch.subject", "discussionSearch.content"};   
    return props;
  }

  /**
   * Returns the name of the property that stores the gearId associated with
   * each item-descriptor.
   */
  public String getGearIdProperty() 
  {
    return "gear_id";
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
    return null;
  }
  
}



