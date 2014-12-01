/*<ATGCOPYRIGHT>
 * Copyright (C) 2008-2011 Art Technology Group, Inc.
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

package atg.projects.store.search;

import atg.search.formhandlers.SearchRequestProcessor;
import atg.search.routing.command.search.SearchRequest;
import atg.search.routing.command.search.QueryRequest;
import atg.search.client.SearchClientException;
import atg.servlet.DynamoHttpServletRequest;

/**
 * Store implementations of SearchRequestProcessor
 * <p>
 * This processor determines if search request of type
 * "View All". If yes, then overrides pageSize with new <code>ViewAllPageSize</code> value.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/Search/Query/src/atg/projects/store/search/StoreRequestProcessor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class StoreRequestProcessor implements SearchRequestProcessor {

  //-------------------------------------
  // Class version string
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/Search/Query/src/atg/projects/store/search/StoreRequestProcessor.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  public static final String S_VIEW_ALL = "viewAll";
  public static final String S_TRUE = "true";

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // Property: ViewAllPageSize
  private Integer mViewAllPageSize = 10000;

  /**
   *
   * @return page size for "view all" search request
   */
  public Integer getViewAllPageSize() {
    return mViewAllPageSize;
  }

  /**
   *
   * @param pViewAllPageSize maximum results per page when "view all" request issued
   */
  public void setViewAllPageSize(Integer pViewAllPageSize) {
    mViewAllPageSize = pViewAllPageSize;
  }

  /**
   * If this request is "View All" request then
   * new page size sets.
   *
   * @param pSearchRequest query request
   * @param pDynamoHttpServletRequest Dynamo HTTP request
   * @param pCallback callback function
   * @return modified query request
   * @throws SearchClientException If an exception occurs while processing the Search Request.
   */
  public SearchRequest processSearchRequest(SearchRequest pSearchRequest, DynamoHttpServletRequest pDynamoHttpServletRequest, Object pCallback) throws SearchClientException {
    boolean isViewAllRequest = false;
    String viewAll = pDynamoHttpServletRequest.getQueryParameter(S_VIEW_ALL);

    if(viewAll != null) {
      isViewAllRequest = viewAll.equalsIgnoreCase(S_TRUE);
    }

    if(mViewAllPageSize != null && mViewAllPageSize > 0 && isViewAllRequest) {
      QueryRequest request = (QueryRequest)pSearchRequest;
      request.setPageSize(getViewAllPageSize());
    }

    return pSearchRequest;
  }
}
