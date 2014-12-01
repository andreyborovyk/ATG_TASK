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

package atg.commerce.gifts;

import atg.core.util.StringUtils;
import atg.core.util.NullableHashtable;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteGroupManager;
import atg.repository.*;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.Vector;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import java.io.IOException;
import java.beans.PropertyEditor;
import atg.droplet.GenericFormHandler;

/**
 * <p>
 * This search form handler is used to support giftlist searches.  Giftlist
 * searching will search the giftlist repository for published giftlists
 * that match the search criteria.  This formhandler is configurable to
 * search most giftlist properties.  At a minimum, doNameSearch
 * and doAdvancedSearch are boolean properties that specify what type of
 * searches to perform.  HandleSearch looks at these values to build the
 * search query and executes on the Repository view.
 *
 * <p>
 * <b><i>Name</i></b><br>
 * Name searching uses name properties and an input search string to
 * do text pattern matching.  Values entered will be used to build the
 * subquery for name searching of giftlist owners.  The name search query
 * will be constructed using an OR clause of both first and last names.
 *
 * <p>
 * <b><i>Advanced</i></b><br>
 * Advanced searches provide possible search options for each property
 * specified in AdvancedSearchPropertyNames.  For example, enumerated types
 * will be defined in the repository with a set number of values.  Advanced
 * searching will retrieve these values from the definition to display
 * in a select box.  The advanced query will be built from options selected
 * to further refine the gift list search. Name searches can also be built into the
 * query here, to enable searches on a first name basis or last name bases if so 
 * desired. If this sort of name searching is used within the form its likely that
 * the above Name search will not be used and only the advanced sort properties
 * firstName and lastName will be used to enable the name searching.
 *
 * <p>
 * A note on firstName and lastName Advanced search properties. As firstName 
 * and lastName are not properties that exist in the gift list repository 
 * definition, we therefore must map to such properties. Therefore the properties
 * firstNamePropertyName, lastNamePropertyName and statePropertyName exist to perform
 * this function. We can supply advanced search properties of firstName, lastName
 * and state, and these will be mapped to configured properties 
 * firstNamePropertyName, lastNamePropertyName and statePropertyName.
 *  
 *  
 * @beaninfo
 *   description: A form handler which allows one to search gift lists
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory Gifts
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 * @author Kerwin D. Moy
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/gifts/SearchFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public
class SearchFormHandler
extends GenericFormHandler
{
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/gifts/SearchFormHandler.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  /** sort direction for a descending search result */
  public  final static String SORT_DIRECTION_DESCENDING="desc";
  /** sort direction for a ascending search result */
  public  final static String SORT_DIRECTION_ASCENDING="asc";
  
  //Constants used for the value of siteScope
  public final static String ALL = "all";
  public final static String CURRENT = "current";
  
  //-------------------------------------
  // Member Variables
  //-------------------------------------

  //-------------------------------------
  // Member Variable: TotalItemCount
  
  protected int mTotalItemCount=0;
  
  /**
   * The total number of objects in the search results.
   * <p>
   * The value should relfect the total number of items in the complete
   * result set without the indexing constraints. The client side uses this
   * value to determine how many pages can be requested
   * 
   * @return Returns the total number of objects in the search results.
   */
  public int getTotalItemCount(){
    return mTotalItemCount;
  }
 
  /**
   * Sets property TotalItemCount
   * @param pTotalItemCount  the property to set the total number of objects in the search results.
   * @beaninfo description:  the property to set the total number of objects in the search results.
   **/ 
  public void setTotalItemCount(int pTotalItemCount){
    mTotalItemCount = pTotalItemCount;
  }

  //-------------------------------------
  // Member Variable: CurrentPage  
  protected int mCurrentPage;
  
  /**
   * Returns the currently requested page number. Page numbers
   * are zero based. 
   * <p>
   * This value is supplied by the client side and is used to 
   * calculate the query's starting and ending indexes.
   * <p>
   * Note that this value must be request scoped because
   * the client side can request pages in ansynchronous random order.
   * 
   * @return The value of the variable CurrentPage
   */
  public int getCurrentPage() {
    return mCurrentPage;
  }

  /**
   * Sets property CurrentPage
   * @param pCurrentPage  the property to set the currently requested page number.
   * @beaninfo description:  the property to set the currently requested page number.
   **/  
  public void setCurrentPage(int pCurrentPage) {
    mCurrentPage = pCurrentPage;
  }
  
  
  //-------------------------------------
  // Properties
  //-------------------------------------
  protected String mSearchSuccessURL;
  /**
   * @return Returns the searchSuccessURL.
   */
  public String getSearchSuccessURL()
  {
    return mSearchSuccessURL;
  }

  /**
   * @param pSearchSuccessURL The searchSuccessURL to set.
   * @beaninfo description:  the property to store the success url for search
   */
  public void setSearchSuccessURL(String pSearchSuccessURL)
  {
    mSearchSuccessURL = pSearchSuccessURL;
  }

  protected String mSearchErrorURL;
  /**
   * @return Returns the searchErrorURL.
   */
  public String getSearchErrorURL()
  {
    return mSearchErrorURL;
  }

  /**
   * @param pSearchErrorURL The searchErrorURL to set.
   * @beaninfo description:  the property to store the error url for search
   */
  public void setSearchErrorURL(String pSearchErrorURL)
  {
    mSearchErrorURL = pSearchErrorURL;
  }
  
  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "Giftlist Search";

  /**
   * Sets property LoggingIdentifier
   * @param pLoggingIdentifier the property which identifies this object as the Giftlist Search.
   * @beaninfo description:  the property which identifies this object as the Giftlist Search.
   **/
  public void setLoggingIdentifier(String pLoggingIdentifier) {
    mLoggingIdentifier = pLoggingIdentifier;
  }

  /**
   * Returns property LoggingIdentifier
   * @return The value of the property LoggingIdentifier.
   **/
  public String getLoggingIdentifier() {
    return mLoggingIdentifier;
  }
  
  //-------------------------------------
  // property: firstNamePropertyName
  protected String mFirstNamePropertyName;

  /**
   * Sets property firstNamePropertyName
   * @param pFirstNamePropertyName the property to store the owner firstName property.
   * @beaninfo description:  the property to store the owner firstName property.
   **/
  public void setFirstNamePropertyName(String pFirstNamePropertyName) {
    mFirstNamePropertyName = pFirstNamePropertyName;
  }

  /**
   * Returns property firstNamePropertyName
   * @return The value of the property firstNamePropertyName.
   **/
  public String getFirstNamePropertyName() {
    return mFirstNamePropertyName;
  }
  
  //-------------------------------------
  // property: lastNamePropertyName
  protected String mLastNamePropertyName;

  /**
   * Sets property lastNamePropertyName
   * @param pLastNamePropertyName the property to store the owner lastName property.
   * @beaninfo description:  the property to store the owner lastName property.
   **/
  public void setLastNamePropertyName(String pLastNamePropertyName) {
    mLastNamePropertyName = pLastNamePropertyName;
  }

  /**
   * Returns property lastNamePropertyName
   * @return The value of the property lastNamePropertyName.
   **/
  public String getLastNamePropertyName() {
    return mLastNamePropertyName;
  }  

  //-------------------------------------
  // property: statePropertyName
  String mStatePropertyName = "owner.billingAddress.state";

  /**
   * Sets property statePropertyName
   * @param pStatePropertyName the property to store the owner state property.
   * @beaninfo description:  the property to store the owner state property.
   **/
  public void setStatePropertyName(String pStatePropertyName) {
    mStatePropertyName = pStatePropertyName;
  }

  /**
   * Returns property statePropertyName
   * @return The value of the property statePropertyName.
   **/
  public String getStatePropertyName() {
    return mStatePropertyName;
  }
  
  //-------------------------------------
  // property: NameSearchPropertyNames
  String [] mNameSearchPropertyNames;

  /**
   * Sets property NameSearchPropertyNames
   * @param pNameSearchPropertyNames  the property to store name search property names.
   * @beaninfo description:  the property to store name search property names.
   **/
  public void setNameSearchPropertyNames(String [] pNameSearchPropertyNames) {
    mNameSearchPropertyNames = pNameSearchPropertyNames;
  }

  /**
   * Returns property NameSearchPropertyNames
   * @return The value of the property NameSearchPropertyNames.
   **/
  public String [] getNameSearchPropertyNames() {
    return mNameSearchPropertyNames;
  }
      
  //-------------------------------------
  // property: UseContainsForNames
  protected boolean mUseContainsForNames = true;

  /**
   * Sets property UseContainsForNames
   * @param pUseContainsForNames  the property to store the boolean value of which query operation to use when performing name search. true for CONTAINS, false for STARTS_WITH.
   * @beaninfo description:  the property to store the boolean value of which query operation to use when performing name search. true for CONTAINS, false for STARTS_WITH.
   **/
  public void setUseContainsForNames(boolean pUseContainsForNames) {
    mUseContainsForNames = pUseContainsForNames;
  }

  /**
   * Returns property UseContainsForNames
   * @return The value of the property UseContainsForNames.
   **/
  public boolean isUseContainsForNames() {
    return mUseContainsForNames;
  }
        
  //-------------------------------------
  // property: DoNameSearch
  boolean mDoNameSearch = true;

  /**
   * Sets property DoNameSearch
   * @param pDoNameSearch  the property to store the boolean value of whether or not to perform a name search.
   * @beaninfo description:  the property to store the boolean value of whether or not to perform a name search.
   **/
  public void setDoNameSearch(boolean pDoNameSearch) {
    mDoNameSearch = pDoNameSearch;
  }

  /**
   * Returns property DoNameSearch
   * @return The value of the property DoNameSearch.
   **/
  public boolean isDoNameSearch() {
    return mDoNameSearch;
  }
  
  //-------------------------------------
  // property: siteScope
  private String mSiteScope = ALL;
  /**
   * Sets the property siteScope.  This property expects the following values: 
   * "all", "current", or existing shareable type ID. The property
   * specifies whether gift lists are not site-limited, limited to one site or 
   * to shareable correspondingly.
   */
  public void setSiteScope(String pSiteScope) {
    mSiteScope = pSiteScope;
  }
  /**
   * gets the property siteScope
   */
  public String getSiteScope() {
    return mSiteScope;
  }
  
  //-------------------------------------
  // property: siteIds
  String[] mSiteIds = null;

  /**
   * Sets property siteIds
   * @param pSiteIds the property which holds site IDs in which context search happens.
   * @beaninfo description:  the property which holds site IDs in which context search happens.
   **/
  public void setSiteIds(String[] pSiteIds) {
    mSiteIds = pSiteIds;
  }

  /**
   * Returns property siteId
   * @return The value of the property SiteId.
   **/
  public String[] getSiteIds() {
    return mSiteIds;
  }

  //-------------------------------------
  // property: SearchInput
  String mSearchInput = "";

  /**
   * Sets property SearchInput
   * @param pStringInput the property to store the input search string to parse and search.
   * @beaninfo description:  the property to store the input search string to parse and search.
   **/
  public void setSearchInput(String pSearchInput) {
    mSearchInput = pSearchInput;
  }

  /**
   * Returns property SearchInput
   * @return The value of the property SearchInput trimming any extra space.
   **/
  public String getSearchInput() {
    return mSearchInput;
  }

  //-------------------------------------
  // property: giftlistRepository
  Repository mGiftlistRepository;

  /**
   * Sets property giftlistRepository
   * @param pGiftlistRepository  the property to store the name of the giftlist repository.
   * @beaninfo description:  the property to store the name of the giftlist repository.
   **/
  public void setGiftlistRepository(Repository pGiftlistRepository) {
    mGiftlistRepository = pGiftlistRepository;
  }

  /**
   * Returns property GiftlistRepository
   * @return The value of the property GiftlistRepository
   **/
  public Repository getGiftlistRepository() {
    return mGiftlistRepository;
  }
  
  //-------------------------------------
  // property: giftlistManager
  GiftlistManager mGiftlistManager;

  /**
   * Sets property giftlistManager
   * @param pGiftlistManager  gift list manager.
   * @beaninfo description: gift list manager.
   **/
  public void setGiftlistManager(GiftlistManager pGiftlistManager) {
    mGiftlistManager = pGiftlistManager;
  }

  /**
   * Returns property GiftlistManager
   * @return gift list manager
   **/
  public GiftlistManager getGiftlistManager() {
    return mGiftlistManager;
  }
  
  //-------------------------------------
  // property: siteGroupManager
  private SiteGroupManager mSiteGroupManager = null;

  /**
   * Sets property SiteGroupManager.
   * @param pSiteGroupManager  the siteGroupManager 
   * @beaninfo description:  the siteGroupManager 
   */
  public void setSiteGroupManager(SiteGroupManager pSiteGroupManager) {
    mSiteGroupManager = pSiteGroupManager;
  }
  /**
   * Returns property siteGroupManager.
   * return property siteGroupManager.
   */
  public SiteGroupManager getSiteGroupManager() {
    return mSiteGroupManager;
  }

  //-------------------------------------
  // property: ItemTypes
  String [] mItemTypes;

  /**
   * Sets property ItemTypes
   * @param pItemTypes the property to store the item types to search in the repository (e.g. gift-list).
   * @beaninfo description:  the property to store the item types to search in the repository (e.g. gift-list).
   **/
  public void setItemTypes(String [] pItemTypes) {
    mItemTypes = pItemTypes;
  }

  /**
   * Returns property ItemTypes
   * @return The value of the property itemTypes
   **/
  public String [] getItemTypes() {
    return mItemTypes;
  }

  //-------------------------------------
  // property: QueryOptions
  QueryOptions mQueryOptions = null;

  /**
   * Sets property QueryOptions
   * @param pQueryOptions the property to store specific query options to used when generating the search string.
   * @beaninfo description:  the property to store specific query options to used when generating the search string.
   **/
  public void setQueryOptions(QueryOptions pQueryOptions) {
    mQueryOptions = pQueryOptions;
  }

  /**
   * Returns property QueryOptions
   * @return The value of the property queryOptions
   **/
  public QueryOptions getQueryOptions() {
    return mQueryOptions;
  }

  //-------------------------------------
  // property: SortDirection 
  protected String mSortDirection;
  
  /**
   * Sets property SortDirection
   * @param pSortDirection the property to store the sort direction.
   * @beaninfo description:  the property to store the sort direction, used to construct the SortDirectives that are attached to the search operation.
   **/  
  public void setSortDirection(String pSortDirection){
    mSortDirection = pSortDirection;
  }

  /**
   * Returns property SortDirection
   * @return The value of the property SortDirection
   **/  
  public String getSortDirection(){
    return mSortDirection;
  }
 
  //-------------------------------------
  // property: SortField  
  protected String mSortField;
  
  /**
   * Sets property SortField
   * @param pSortField  the property to set the field by which the search results are to be sorted on.
   * @beaninfo description:  the property to set the field by which the search results are to be sorted on.
   **/
  public void setSortField(String pSortField){
    mSortField = pSortField;
  }
  
  /**
   * Returns the property SortField
   * @return the SortField
   */
  public String getSortField(){
    return mSortField;
  }
  
  //-------------------------------------
  // property: SortCaseSensitive  
  protected boolean mSortCaseSensitive;
  
  /**
   * Sets property SortCaseSensitive
   * @param pSortCaseSensitive  the property to set if the sort should be case sensitive.
   * @beaninfo description:  the property to set if the sort should be case sensitive.
   **/
  public void setSortCaseSensitive(boolean pSortCaseSensitive){
    mSortCaseSensitive = pSortCaseSensitive;
  }
  
  /**
   * Returns the property SortCaseSensitive
   * @return the SortCaseSensitive
   */
  public boolean getSortCaseSensitive(){
    return mSortCaseSensitive;
  }  
 
  //-------------------------------------
  // property: ResultsPerPage
  
  protected int mResultsPerPage;
  
  /**
   * Returns the number of objects to be queried at
   * one time. 
   * <p>
   * This value, along with the current page is used
   * to calculate the starting and ending indexes of the query. 
   * 
   * @return The value of the property ResultsPerPage
   */
  public int getResultsPerPage() {
    return mResultsPerPage;
  }

  /**
   * Sets property ResultsPerPage
   * @param pResultsPerPage  the property to set the number of objects to be queried at one time.
   * @beaninfo description:  the property to set the number of objects to be queried at one time.
   **/
  public void setResultsPerPage(int pResultsPerPage) {
    mResultsPerPage = pResultsPerPage;
  }        
  
  //-------------------------------------
  // property: DoPagedSearch
  boolean mDoPagedSearch;

  /**
   * Sets property DoPagedSearch
   * @param pDoPagedSearch  the property to store the boolean value of whether or not to perform a paged search.
   * @beaninfo description:  the property to store the boolean value of whether or not to perform a paged search.
   **/
  public void setDoPagedSearch(boolean pDoPagedSearch) {
    mDoPagedSearch = pDoPagedSearch;
  }

  /**
   * Returns property DoPagedSearch
   * @return The value of the property DoPagedSearch.
   **/
  public boolean isDoPagedSearch() {
    return mDoPagedSearch;
  }
  
  //-------------------------------------
  // property: SearchResults
  Collection mSearchResults;

  /**
   * Returns property SearchResults
   * @return The value of the property SearchResults
   **/
  public Collection getSearchResults() {
    return mSearchResults;
  }

  //-------------------------------------
  // property: SearchResultsByItemType
  HashMap mSearchResultsByItemType = new HashMap();

  /**
   * Returns property SearchResultsByItemType
   * @return The value of the property SearchResultsByItemType
   **/
  public HashMap getSearchResultsByItemType() {
    return mSearchResultsByItemType;
  }

  //-------------------------------------
  // property: DoAdvancedSearch
  boolean mDoAdvancedSearch = false;

  /**
   * Sets property DoAdvancedSearch
   * @param pDoAdvancedSearch  the property to store the boolean value of whether or not to perform a advanced search.
   * @beaninfo description:  the property to store the boolean value of whether or not to perform a advanced search.
   **/
  public void setDoAdvancedSearch(boolean pDoAdvancedSearch) {
    mDoAdvancedSearch = pDoAdvancedSearch;
  }

  /**
   * Returns property DoAdvancedSearch
   * @return The value of the property DoAdvancedSearch
   **/
  public boolean isDoAdvancedSearch() {
    return mDoAdvancedSearch;
  }

  //-------------------------------------
  // property: AdvancedSearchPropertyNames
  String [] mAdvancedSearchPropertyNames;

  /**
   * Sets property AdvancedSearchPropertyNames
   * @param pAdvancedSearchPropertyNames  the property to store advanced search property names.
   * @beaninfo description:  the property to store advanced search property names.
   **/
  public void setAdvancedSearchPropertyNames(String [] pAdvancedSearchPropertyNames) {
    mAdvancedSearchPropertyNames = pAdvancedSearchPropertyNames;
  }

  /**
   * Returns property AdvancedSearchPropertyNames
   * @return The value of the property AdvancedSearchPropertyNames
   **/
  public String [] getAdvancedSearchPropertyNames() {
    return mAdvancedSearchPropertyNames;
  }

  //-------------------------------------
  // property: DoPublishedSearch
  boolean mDoPublishedSearch = false;

  /**
   * Sets property DoPublishedSearch
   * @param pDoPublishedSearch  the property to store the boolean value of whether or not to perform a published search.
   * @beaninfo description:  the property to store the boolean value of whether or not to perform a published search.
   **/
  public void setDoPublishedSearch(boolean pDoPublishedSearch) {
    mDoPublishedSearch = pDoPublishedSearch;
  }

  /**
   * Returns property DoPublishedSearch
   * @return The value of the property DoPublishedSearch
   **/
  public boolean isDoPublishedSearch() {
    return mDoPublishedSearch;
  }

  //-------------------------------------
  // property: PublishedSearchPropertyNames
  String [] mPublishedSearchPropertyNames;

  /**
   * Sets property PublishedSearchPropertyNames
   * @param pPublishedSearchPropertyNames  the property to store published search property names.
   * @beaninfo description:  the property to store published search property names.
   **/
  public void setPublishedSearchPropertyNames(String [] pPublishedSearchPropertyNames) {
    mPublishedSearchPropertyNames = pPublishedSearchPropertyNames;
  }

  /**
   * Returns property PublishedSearchPropertyNames
   * @return The value of the property PublishedSearchPropertyNames
   **/
  public String [] getPublishedSearchPropertyNames() {
    return mPublishedSearchPropertyNames;
  }

  //-------------------------------------
  // property: PropertyValuesByType

  /**
   * Returns property PropertyValuesByType
   * @return The value of the property PropertyValuesByType
   **/
  public HashMap getPropertyValuesByType() {

    HashMap values = new HashMap();

    for (int i=0; i<getAdvancedSearchPropertyNames().length; i++) {
      Collection resultSet =
        generateSearchValues(getAdvancedSearchPropertyNames()[i]);
      values.put(getAdvancedSearchPropertyNames()[i], resultSet);
    }
    return values;
  }

  //-------------------------------------
  // property: PropertyValues

  Hashtable mPropertyValues = null;

  /**
   * Sets property PropertyValues
   * @param pPropertyValues the property to store selected property values for advanced searching.
   * @beaninfo description:  the property to store selected property values for advanced searching.
   **/

  public void setPropertyValues(Hashtable pPropertyValues) {
    mPropertyValues = pPropertyValues;
  }

  /**
   * Returns property PropertyValues
   * @return The value of the property PropertyValues
   **/
  public Hashtable getPropertyValues() {
    if (mPropertyValues == null){
      mPropertyValues = new NullableHashtable();
      for (int i=0; i<getAdvancedSearchPropertyNames().length; i++) {
        mPropertyValues.put(getAdvancedSearchPropertyNames()[i], null);
      }
      return mPropertyValues;
    }
    else return mPropertyValues;
  }

  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instanceof SearchFormHandler
   */
  public SearchFormHandler() {
  }

  /**
   * Called prior to the search taking place. 
   * @param pRequest
   * @param pResponse
   * @throws ServletException
   * @throws IOException
   */
  public void preSearch(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse)
  throws ServletException, IOException 
  {
    
  }
  /**
   * For each item type specified in itemTypes, call generateSearchResultSet
   * to generate a subResultSet for that item type based on query parameters.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleSearch(DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    //If any form errors found, redirect to error URL:
    if (!checkFormRedirect(null, getSearchErrorURL(), pRequest, pResponse))
      return false;
         
    preSearch(pRequest, pResponse);
    //If any form errors found, redirect to error URL:
    if (!checkFormRedirect(null, getSearchErrorURL(), pRequest, pResponse))
      return false;
    
    mSearchResults = doSearch(pRequest, pResponse);
    //If any form errors found, redirect to error URL:
    if (!checkFormRedirect(null, getSearchErrorURL(), pRequest, pResponse))
      return false;
    
    postSearch(pRequest, pResponse);
    
    if(!getFormError())
      setSearchInput(null);
    //If NO form errors are found, redirect to the success URL.
    //If form errors are found, redirect to the error URL.
    return checkFormRedirect (getSearchSuccessURL(),
                              getSearchErrorURL(),
                              pRequest,
                              pResponse);
  }


  /**
   * Performs the search operation and generates the result set. 
   * @param pRequest
   * @param pResponse
   * @returns collection of search results
   * @throws ServletException
   * @throws IOException
   */
  public Collection doSearch(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse)
  throws ServletException, IOException 
  {
    Collection resultSet = new LinkedHashSet();
    for (int c=0; c<getItemTypes().length; c++) {
     Collection subResultSet = generateResultSet(getItemTypes()[c]);
     if (subResultSet != null) {
        resultSet.addAll(subResultSet);
      }
    }
    return resultSet;
    
  }
  
  
  /**
   * Called after a search. 
   * @param pRequest
   * @param pResponse
   * @throws ServletException
   * @throws IOException
   */
  public void postSearch(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse)
  throws ServletException, IOException 
  {
    
  }

  /**
   * executeCountQuery Executes a count query and stores the results in the totalItemCount
   * @param pView
   * @param pQuery
   */
  protected void executeCountQuery(RepositoryView pView, Query pQuery)
  throws RepositoryException
  {
    setTotalItemCount(pView.executeCountQuery(pQuery));
  }  
  
  /**
   * calculateStartIndex Calculates the starting index used for the query.
   * <p>
   * By default, the starting index is the current * resultsPerPage 
   * 
   * @return the startIndex
   */
  protected Integer calculateStartIndex(){
    Integer startIndex = Integer.valueOf(getCurrentPage() * getResultsPerPage());
    return startIndex;
  }
  /**
   * calculateEndIndex Calculates the ending  index used for the query
   * <p>
   * By default, the starting index is the (current * resultsPerPage) + resultsPerPage  
   * @return the endIndex
   */
  protected Integer calculateEndIndex(){
    Integer endIndex  = Integer.valueOf((getCurrentPage() * getResultsPerPage()) + getResultsPerPage());  
    return endIndex;
  }  
  
  /**
   * buildSortDirectives is called to construct the SortDirectives object that will be used
   * in the repository query. 
   * <p>
   * By default this method uses the sortDirection and sortField properties 
   * to build a SortDirectives object.
   *  
   * @return SortDirectives
   */
  protected SortDirectives buildSortDirectives() {
    SortDirectives sortDirectives = new SortDirectives();
    String sortField = getSortField();
    
    if ("firstName".equalsIgnoreCase(sortField))
      sortField = getFirstNamePropertyName();
    else if ("lastName".equalsIgnoreCase(sortField))
      sortField = getLastNamePropertyName();
    
    if (SORT_DIRECTION_ASCENDING.equalsIgnoreCase(getSortDirection()))
      sortDirectives.addDirective(new SortDirective(sortField,SortDirective.DIR_ASCENDING,getSortCaseSensitive()));
    else 
      sortDirectives.addDirective(new SortDirective(sortField,SortDirective.DIR_DESCENDING,getSortCaseSensitive()));

    return sortDirectives;
  }  

  /**
   * buildQueryOptions is called to set the query options on the property mQueryOptions
   */  
  protected QueryOptions buildQueryOptions(){
      if (isLoggingDebug()) {
          logDebug("setting query options: startIndex = " + calculateStartIndex() + " , endIndex = " + calculateEndIndex());
        }
      if(!StringUtils.isBlank(getSortField())) {
        return new QueryOptions(calculateStartIndex(), calculateEndIndex(), buildSortDirectives(), null);
      }
      return null;
  }    
  
  /**
   * generateSearchValues is used by the advanced searching feature.
   * For the property supplied in the pSearchType parameter, this
   * method will return a list of available search options for that
   * property.  Will search through all itemTypes and collect all values
   * for the property.  For example, an enumerated property will be defined
   * in the repository item descriptor with a set of values.  This method
   * will get possible values from the property descriptor.  Other properties
   * may simply be a string type which will require a simple text query.
   *
   * @param pSearchType the search type. Typically corresponds to an
   * property descriptor name in the giftlist repository
   * @return options or null if no values could be found
   */
  protected Collection generateSearchValues(String pSearchType) {
    try {
      
      Set resultSet = new HashSet();
      for (int i=0; i<getItemTypes().length; i++){
        RepositoryItemDescriptor itemDesc =
          getGiftlistRepository().getItemDescriptor(getItemTypes()[i]);
        if (itemDesc.hasProperty(pSearchType)) {
          RepositoryPropertyDescriptor pd = (RepositoryPropertyDescriptor) itemDesc.getPropertyDescriptor(pSearchType);

          /* if type = enumerated, get list of values from property descriptor */
          if (pd.getTypeName().equalsIgnoreCase("enumerated")) {
            PropertyEditor pe = pd.createPropertyEditor();
            if (pe != null) {
              Collection subResultSet = Arrays.asList(pe.getTags());
              if (subResultSet != null) {
                resultSet.addAll(subResultSet);
              }
            }
          }

          else
            resultSet = null;
        }
        
        else return null;

      }
      return resultSet;
    }
    catch (RepositoryException exc) {
      if (isLoggingError())
        logError(exc);
    }
    return null;
  }

  /**
   * For the supplied item type return a sub-result set that will be combined into
   * the total result set.  generateResultSet calls generateSearchQuery to
   * build the query string based on options.  The resulting query is executed
   * on the Repository View and the result set is returned.
   *
   * @param pItemType the type of item to search for. Typically corresponds to an
   * item descriptor name in the giftlist repository
   * @return null if no items could be found
   */
  protected Collection generateResultSet(String pItemType) {
    try {
      RepositoryView view = getGiftlistRepository().getView(pItemType);
      QueryBuilder qb = view.getQueryBuilder();
      Query q = generateSearchQuery(pItemType, qb);
      QueryOptions options = getQueryOptions();
      if (isLoggingDebug())
        logDebug("search query=" + q);
      if (q != null) {
        if(isDoPagedSearch()) {
          executeCountQuery(view, q);
        }
        if(options == null) {
      	  if(isDoPagedSearch()) {
      	    options = buildQueryOptions();
      	  }
        }
        RepositoryItem [] items = view.executeQuery(q, options);
        if (items != null) {
          return Arrays.asList(items);
        }
      }
    }
    catch (RepositoryException exc) {
      if (isLoggingError())
        logError(exc);
    }
    return null;
  }

  /**
   * Return the query that should be executed on the Repository View.  This
   * method attempts to build its query from name and advanced
   * search subqueries.  It calls each sub method to build the sub query.
   * The method attempts to build an AND query between text and
   * advanced search queries.
   *
   * @param pItemType the type of item to search for. Typically corresponds to an
   * item descriptor name in the giftlist repository
   * @param pQueryBuilder the builder that should be used to construct the query
   * @exception RepositoryException if an error occured while forming the query
   */
  protected Query generateSearchQuery(String pItemType, QueryBuilder pQueryBuilder)
    throws RepositoryException
  {
    Query nameSearchQuery = null;
    Query advancedQuery = null;
    Query publishedQuery = null;
    Query siteQuery = null;

    if (isDoNameSearch()) {
      nameSearchQuery = generateNameSearchQuery(pItemType, pQueryBuilder, getSearchInput());
    }

    if (isDoAdvancedSearch()) {
      advancedQuery = generateAdvancedSearchQuery(pItemType,pQueryBuilder);
    }

    if (isDoPublishedSearch()) {
      publishedQuery = generatePublishedSearchQuery(pItemType, pQueryBuilder);
    }
    
    if (!ALL.equals(getSiteScope())){
      siteQuery = generateSiteConstrainedQuery(getSiteIds(), pQueryBuilder);
    }

    if (isLoggingDebug()) {
      logDebug("nameSearchQuery=" + nameSearchQuery + "; advancedQuery=" + advancedQuery +
               "; publishedQuery=" + publishedQuery + "; siteQuery=" + siteQuery);
    }
    
    List<Query> subQueries = new ArrayList<Query>();

    if (advancedQuery != null){
      subQueries.add(advancedQuery);
    }
    if (nameSearchQuery != null){
      subQueries.add(nameSearchQuery);
    }
    if (publishedQuery != null){
      subQueries.add(publishedQuery);
    }
    if (siteQuery != null){
      subQueries.add(siteQuery);
    }
    if (subQueries.size()>0){
     
      Query [] qArray = new Query[subQueries.size()];
      
      Query q = pQueryBuilder.createAndQuery(subQueries.toArray(qArray));
      return q;
    }
    return null;
  }

  /**
   * Return a query which represents a name search query. This method builds a sub-query
   * for each attribute named in the <code>nameSearchPropertyNames</code> property. Each
   * sub-query is OR'd together to form the total query.
   * @param pItemType the type of item to search for. Typically corresponds to an
   * item descriptor name in the giftlist repository
   * @param pQueryBuilder the builder that should be used to construct the query
   * @param pInput the search string to use for the full name search
   * @exception RepositoryException if an error occured while forming the query
   */
  protected Query generateNameSearchQuery(String pItemType, QueryBuilder pQueryBuilder, String pInput)
    throws RepositoryException
  {
    if (pInput == null)
      return null;
    
    /* method variables */    
    Query q;
    
    StringTokenizer st = new StringTokenizer(pInput);
    int num = getNameSearchPropertyNames().length;
    if (num > 0) {
      List subQueries = new ArrayList(num);
      while(st.hasMoreTokens()){
        QueryExpression searchString = pQueryBuilder.createConstantQueryExpression(st.nextToken());
        for (int c=0; c<num; c++) {
          QueryExpression property = pQueryBuilder.createPropertyQueryExpression(getNameSearchPropertyNames()[c]);
          if (isUseContainsForNames()) {
            q = pQueryBuilder.createPatternMatchQuery(property, searchString, QueryBuilder.CONTAINS, true);
          }
          else {
            q = pQueryBuilder.createPatternMatchQuery(property, searchString, QueryBuilder.STARTS_WITH, true);
          }
          subQueries.add(q);
        }
      }
      Query [] qArray = new Query[subQueries.size()];
      return pQueryBuilder.createOrQuery((Query [])subQueries.toArray(qArray));
    }
    return null;
  }


  /**
   * Generates a published search query to verify that lists are both public
   * and published.
   *
   * @param pItemType the type of item to search for. Typically corresponds to an
   * item descriptor name in the catalog repository
   * @param pQueryBuilder the builder that should be used to construct the query
   * @exception RepositoryException if an error occured while forming the query
   */

  protected Query generatePublishedSearchQuery(String pItemType, QueryBuilder pQueryBuilder)
    throws RepositoryException
  {
    /* method variables */
    Vector subQueries = new Vector();
    String property = "";
    Boolean value = Boolean.TRUE;
    Query q;

    for (int c = 0; c<getPublishedSearchPropertyNames().length; c++){
      property = getPublishedSearchPropertyNames()[c];
      QueryExpression pProperty = pQueryBuilder.createPropertyQueryExpression(property);
      QueryExpression pValue = pQueryBuilder.createConstantQueryExpression(value);
      q = pQueryBuilder.createComparisonQuery(pProperty, pValue, QueryBuilder.EQUALS);
      subQueries.add(q);
    }
    if (subQueries.size() > 0){
      Query [] qArray = new Query[subQueries.size()];
      return pQueryBuilder.createAndQuery((Query [])subQueries.toArray(qArray));
    }
    else
      return null;
  }
  /**
   * Return a query which represents an advanced search based on selected
   * property values.  This method builds a sub-query for each property
   * found in mAdvancedSearchPropertyNames.  If the property type is
   * another repository item, we need to search below this level for the
   * property value.  To do this, we append .displayName to the property
   * before searching.  All advanced property sub-queries are AND'd together
   * to form the final complete query string.
   *
   * @param pItemType the type of item to search for. Typically corresponds to an
   * item descriptor name in the catalog repository
   * @param pQueryBuilder the builder that should be used to construct the query
   * @exception RepositoryException if an error occured while forming the query
   */
  protected Query generateAdvancedSearchQuery(String pItemType, QueryBuilder pQueryBuilder)
    throws RepositoryException
  {
    /* method variables */
    Vector subQueries = new Vector();
    String advancedProperty = "";
    Object value;
    Query q;

    /* for each advanced search property, build the subquery if required */
    for (int c = 0; c<getAdvancedSearchPropertyNames().length; c++){
      advancedProperty = getAdvancedSearchPropertyNames()[c];
      if("eventDate".equalsIgnoreCase(advancedProperty)) {
        Object propertyObject = (Object)getPropertyValues().get(advancedProperty);
        if (propertyObject instanceof Date) {
          Date eventDate = (Date)propertyObject;
          //convert date to timestamp
          Timestamp timeStamp = new Timestamp(eventDate.getTime());
          value = timeStamp;
        }
        else {
          value = null;
        }
        
      }
      else {
        value = (String)getPropertyValues().get(advancedProperty);
        }
      if ( value == null )
        logDebug( "SearchFormHandler value is null for property = " + advancedProperty );
      /* if value is not empty and value is not a String or has a string length greater than zero, then proceed to build subquery */
      if ( value != null && (value instanceof String || ((String) value.toString()).length() > 0 )){
        RepositoryItemDescriptor itemDesc = getGiftlistRepository().getItemDescriptor(pItemType);
        RepositoryPropertyDescriptor pd = (RepositoryPropertyDescriptor)itemDesc.getPropertyDescriptor(advancedProperty);

        /* if property descriptor is found and type = enumerated,
           gnerate a propertyQE=searchString query */
        if ((pd != null) &&
            (pd.getTypeName().equalsIgnoreCase("enumerated")) ){
          QueryExpression propertyQE = pQueryBuilder.createPropertyQueryExpression(advancedProperty);
          QueryExpression searchString = pQueryBuilder.createConstantQueryExpression((String) value);
          q = pQueryBuilder.createComparisonQuery(propertyQE, searchString, QueryBuilder.EQUALS);
          if (((String) value.toString()).length() > 0) subQueries.add(q);
        }
        
        else if("eventDate".equalsIgnoreCase(advancedProperty)) {
          QueryExpression propertyQE = pQueryBuilder.createPropertyQueryExpression(advancedProperty);
          QueryExpression searchString = pQueryBuilder.createConstantQueryExpression(value);
          q = pQueryBuilder.createComparisonQuery(propertyQE, searchString, QueryBuilder.EQUALS);
          subQueries.add(q);          
        }

        else if("firstName".equalsIgnoreCase(advancedProperty)) {
            advancedProperty = getFirstNamePropertyName();
            QueryExpression propertyQE = pQueryBuilder.createPropertyQueryExpression(advancedProperty);
            QueryExpression searchString = pQueryBuilder.createConstantQueryExpression(value);
            if (isUseContainsForNames()) {
              q = pQueryBuilder.createPatternMatchQuery(propertyQE, searchString, QueryBuilder.CONTAINS, true);
            }
            else {
              q = pQueryBuilder.createPatternMatchQuery(propertyQE, searchString, QueryBuilder.STARTS_WITH, true);
            }            
            subQueries.add(q);     
        }
        
        else if("lastName".equalsIgnoreCase(advancedProperty)) {
            advancedProperty = getLastNamePropertyName();
            QueryExpression propertyQE = pQueryBuilder.createPropertyQueryExpression(advancedProperty);
            QueryExpression searchString = pQueryBuilder.createConstantQueryExpression(value);
            if (isUseContainsForNames()) {
              q = pQueryBuilder.createPatternMatchQuery(propertyQE, searchString, QueryBuilder.CONTAINS, true);
            }
            else {
              q = pQueryBuilder.createPatternMatchQuery(propertyQE, searchString, QueryBuilder.STARTS_WITH, true);
            }     
            subQueries.add(q);     
        }        
        

        /* else build a text query */
        else {
          /* if advancedProperty = state, then use statePropertyName in query.
             state is a property of owner address */
          if ("state".equalsIgnoreCase(advancedProperty))
            advancedProperty = getStatePropertyName();
          QueryExpression propertyQE = pQueryBuilder.createPropertyQueryExpression(advancedProperty);          
          QueryExpression searchString = pQueryBuilder.createConstantQueryExpression(value);
          q = pQueryBuilder.createPatternMatchQuery(propertyQE, searchString, QueryBuilder.CONTAINS, true);
          subQueries.add(q);
        }
      }
    }
    if (subQueries.size() > 0){
      Query [] qArray = new Query[subQueries.size()];
      return pQueryBuilder.createAndQuery((Query [])subQueries.toArray(qArray));
    }
    else
      return null;
  }
  
  
  /**
   * Returns a query that represents a site search. This method builds a query 
   * based on settings of GiftlistManager. If GiftlistManager is not site limited
   * null will be returned. If GiftlistManager is limited to one site or a sharable type
   * then a query with corresponding constraints on the siteId property will be returned.
   * 
   * @param pSiteIds the list of site IDs to which gift list should belong
   * @param pQueryBuilder the builder that should be used to construct the query
   * @return query 
   * @throws RepositoryException
   */
  protected Query generateSiteConstrainedQuery(String[] pSiteIds, QueryBuilder pQueryBuilder) throws RepositoryException{
    Query q = null;        
    
    GiftlistManager glm = getGiftlistManager();
    String siteIdProperty = glm.getGiftlistTools().getSiteProperty();
    if (pSiteIds == null || pSiteIds.length ==0){
      String currentSiteId = SiteContextManager.getCurrentSiteId();
      if (currentSiteId != null) {
        pSiteIds = new String[]{SiteContextManager.getCurrentSiteId()};
      }
    }
    
    if (!ALL.equals(getSiteScope())){
      Collection<String> siteIds = null;
      if (pSiteIds != null) {
        if (CURRENT.equals(getSiteScope())){
          siteIds = Arrays.asList(pSiteIds);
        }else{
          // search should be limited to sites that share the specified sharable type with the
          // given site
          siteIds = new ArrayList<String>();
          for (String siteId : pSiteIds){
            Collection<String> sharingIds = getSiteGroupManager().getSharingSiteIds(siteId,getSiteScope());
            if(sharingIds != null)
              siteIds.addAll(sharingIds);
          }
        }
      }
      QueryExpression pProperty = pQueryBuilder.createPropertyQueryExpression(siteIdProperty);
      if (siteIds == null) {
        // No current site
        q = pQueryBuilder.createIsNullQuery(pProperty);
      } else {
        Query[] subQueries = new Query[siteIds.size() + 1];
        subQueries[0] = pQueryBuilder.createIsNullQuery(pProperty);
        int i = 1;
        for (String site : siteIds){
          QueryExpression pValue = pQueryBuilder.createConstantQueryExpression(site);
          Query subQuery = pQueryBuilder.createComparisonQuery(pProperty, pValue, QueryBuilder.EQUALS);
          subQueries[i++] = subQuery;
        }
        q = pQueryBuilder.createOrQuery(subQueries);
      }
    }    
    return q;    
  }


} // end of class