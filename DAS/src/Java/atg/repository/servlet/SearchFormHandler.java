/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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

package atg.repository.servlet;


import atg.beans.DynamicPropertyDescriptor;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.droplet.DropletFormException;
import atg.droplet.GenericFormHandler;
import atg.multisite.SiteGroupManager;
import atg.multisite.SiteManager;
import atg.nucleus.Nucleus;
import atg.repository.ContextFilteringRule;
import atg.repository.ContextFilteringRules;
import atg.repository.GlobalContextType;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryPropertyDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlQuery;
import atg.repository.rql.RqlStatement;
import atg.repository.rql.TokenMgrError;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletException;

/**
 * <p>
 * This form handler can be used to search repositories with any combination of
 * several search types: Keyword, Text, Hierarchical, Advanced. Each of these
 * types are enabled with a boolean parameter. The handler examines each boolean
 * and if true, appends that search's subquery to the full query string.
 * <p>
 * <b><i>General Properties</i></b>
 * <p>
 * Set one or more of the following properties to "true" to enable a search
 * type:
 * <dl>
 * <dt><b><code>doKeywordSearch</code></b>
 * <dd>look in multiple text properties for one or more keywords
 * <dt><b><code>doTextSearch</code></b>
 * <dd>perform a full-text search for a specified search string in one or more
 * text properties
 * <dt><b><code>doHierarchicalSearch</code></b>
 * <dd>find all decendents of a specified repository item
 * <dt><b><code>doAdvancedSearch</code></b>
 * <dd>similar to a Keyword search, except that we test properties for an exact
 * match rather than matching substrings within the property
 * </dl>
 * <p>
 * Multiple repositories and item-types can be simultaneously searched. If
 * multiple item-types are searched, each must exist in all repositories. Search
 * results of the same item from multiple repositories are combined. Set the
 * following properties to define the search source(s):
 * <dl>
 * <dt><b><code>repositories</code></b>
 * <dd>array of repository components to search
 * <dt><b><code>itemTypes</code></b>
 * <dd>array of the names of the items to search
 * </dl>
 * <p>
 * To control the size of the results, use:
 * <dl>
 * <dt><b><code>maxRowCount</code></b>
 * <dd>the maximum number of rows to return. Set to -1 to return all rows. The
 * default value is -1.
 * </dl>
 * <p>
 * To disallow an empty search from being run, use:
 * <dl>
 * <dt><b><code>allowEmptySearch</code></b>
 * <dd>If false, the handler will throw a DropletFormException if none of the
 * search types have a non-null or non-blank (if string) value. The default
 * value is &quot;true&quot;
 * </dl>
 * <p>
 * Queries may also be refined and then rerun. When this option is enabled,
 * subsequent searches effectively search the results of the previous query.
 * <dl>
 * <dt><b><code>allowRefine</code></b>
 * <dd>set to true to turn on query refining. The default value is
 * &quot;false&quot;
 * <dt><b><code>previouslySubmitted</code></b>
 * <dd>will be set to true if the handler has previously handled a query in the
 * current session (handler must be session-scoped). Property is reset to
 * &quot;false&quot; if the query is cleared.
 * <dt>&nbsp;</dt>
 * </dl>
 * <p>
 * <b><i>Keyword Search Properties</i></b>
 * <p>
 * Keyword searching takes a set of property names and a search string(s). Each
 * property will be searched for these strings. You may also search *ALL* string
 * properties (excluding enumerated and non-queriable properties) of an item by
 * simply leaving the property <code>keywordSearchPropertyNames</code> empty.
 * <dl>
 * <dt><b><code>keywordSearchPropertyNames</code></b>
 * <dd>array of property names to search in the format
 * "item-type-name.property-name". Leave empty to search all string properties.
 * <dt><b><code>keywords</code></b>
 * <dd>array of keyword strings to search for. If not set, will use
 * <code>keywordInput</code> property
 * <dt><b><code>keywordInput</code></b>
 * <dd>alternative way to search keywords. Unlike <code>keywords</code>, this
 * property allows logical operators (AND, OR, NOT, &, |, !) and supports
 * parenthesis '(' and ')' to group logic. Quote keyword values with single or
 * double quotes if they include spaces. Quote types may not be mixed. Logical
 * operators are optional; 'OR' will be used by default
 * <dt><b><code>toUpperCaseKeywords</code></b>
 * <dd>set this to "true" to force keywords to be converted to upper-case before
 * the query is run
 * <dt><b><code>toLowerCaseKeywords</code></b>
 * <dd>set this to "true" to force keywords to be converted to lower-case before
 * the query is run
 * <dt>&nbsp;</dt>
 * </dl>
 * <p>
 * <b><i>Text Search Properties</i></b>
 * <p>
 * Text searching takes a list of text property names and an input search string
 * to do text pattern matching. Values entered will be used to build the
 * sub-query for text searching.
 * <dl>
 * <dt><b><code>textSearchPropertyNames</code></b>
 * <dd>list of property names to search in the format
 * "item-type-name.property-name". If not specified, the default list of text
 * properties configured in the repository will be used.
 * <dt><b><code>searchStringFormat</code></b>
 * <dd>the format to use i.e. "ORACLE_CONTEXT", "SYBASE_SDS"
 * <dt><b><code>textInput</code></b>
 * <dd>The string to search for. If a repository is configured to simulate
 * full-text queries (<code>simulateTextSearchQueries=true</code>), the SQL LIKE
 * operator will be used to determine whether the target value is a substring of
 * any of the text properties being searched.
 * <dt><b><code>allowWildcards</code></b>
 * <dd>If true, the wildcard character '*' can be used anywhere in the input
 * string to represent any number of characters. True by default.
 * <dt><b><code>minScore</code></b>
 * <dd>minimum required score that the results must meet or exceed in order to
 * be returned by the full-text search engine. See vendor docs for more
 * information on the meaning and use of the score value. [NOTE: Current
 * repository implementations don't yet support this feature of full-text
 * searches.]
 * <dt>&nbsp;</dt>
 * </dl>
 * <p>
 * <b><i>Hierarchical Search Properties</i></b>
 * <p>
 * Hierarchical searches look in a subset of items, starting from a given item,
 * including also that items's child items, and also those children's children,
 * i.e. all descendants of the original item. The given item is indicated by the
 * repository ID in the <code>ancestorId</code> property.
 * <dl>
 * <dt><b><code>ancestorPropertyName</code></b>
 * <dd>the name of the property in which to search for ancestor repository items
 * <dt><b><code>ancestorId</code></b>
 * <dd>the property that represents the repositoryId of the ancestor item to
 * search in
 * <dt>&nbsp;</dt>
 * </dl>
 * <p>
 * <b><i>Advanced Search Properties</i></b>
 * <p>
 * Advanced searching takes a list of properties and values or ranges of values
 * for those properties and looks for an exact match. Additionally, the Advanced
 * Search can be used to provide all possible search values for properties
 * specified in <code>AdvancedSearchPropertyNames</code>. For example,
 * enumerated types will be defined in the repository with a set number of
 * values. Advanced searching can retrieve these values to be displayed in a
 * select box.
 * <dl>
 * <dt><b><code>advancedSearchPropertyNames</code></b>
 * <dd>list of property names to search. Format: type.property
 * <dt><b><code>advancedSearchPropertyValues</code></b>
 * <dd>propery names mapped to search values. Fortat: type.property=value
 * <dt><b><code>advancedSearchPropertyRanges</code></b>
 * <dd>For each property searched, you can specify a value &quot;range&quot;
 * rather than a single value. Either a maximum or minimum value or both may be
 * specified. Ranges are set by item type, then property name, and finally by
 * "max" or "min". For example,
 * &quot;mySearchHandler.advancedSearchPropertyRanges
 * .product.creationDate.max&quot;
 * <dt><b><code>propertyValuesByType</code></b>
 * <dd>dictionary containing one key/value pair for each property named in
 * <code>advancedSearchPropertyNames</code> whose type is either enumerated,
 * RepositoryItem or a collection of RepositoryItems. The key is the name of the
 * property and the value is a Collection of the possible values. If the
 * property type a collection of RepositoryItems, the value will be a collection
 * of RepositoryItem. Alternatively, if the property type is RepositoryItem a
 * property of each item is returned as described below.
 * <dt><b><code>displayName</code></b>
 * <dd>when a property of an item being searched is another RepositoryItem, by
 * default we search the child item's property named &quot;displayName&quot; if
 * it exists. In the same way, values returned by the property
 * <code>propertyValuesByType</code> will be values of the property
 * &quot;displayName&quot; when the property is a RepositoryItem. Use this
 * property to override the default name of &quot;displayName&quot;
 * <dt>&nbsp;</dt>
 * </dl>
 * <p>
 * <b><i>Form Actions</i></b>
 * <p>
 * In addition to the standard &quot;cancel&quot; action of all form handlers:
 * <dl>
 * <dt><b><code>search</code></b></dt>
 * <dd>execute the search and store the results. The Keyword and Text queries
 * are OR'ed together. The Advanced and Hierarchical queries are AND'ed
 * together, and these combined queries are then AND'ed. That is: ( (Keyword OR
 * Text) AND (Advanced AND Hierarchical).</dd>
 * <dt><b><code>clearQuery</code></b></dt>
 * <dd>this action is used in combination with the property
 * <code>allowRefine</code>. It will force the previous query to be cleared so
 * that the user may begin refining their query anew.</dd>
 * <dt>&nbsp;</dt>
 * </dl>
 * <p>
 * <b><i>Retrieving Results</i></b>
 * <p>
 * Once the query has been performed, the results are available both as a single
 * collection and by item-type:
 * <dl>
 * <dt><b><code>searchResults</code></b></dt>
 * <dd>a Collection of all items that satisfied the search criteria. If
 * searching multiple item types, all items returned by the search appear in the
 * list regardless of their type.</dd>
 * <dt><b><code>searchResultsByItemType</code></b></dt>
 * <dd>a map containing one key/value pair for each item type you searched for.
 * The key is the item-type name and the value is a Collection of items of that
 * type that satisfied the search criteria. This property should not be used in
 * combination with page navigation.</dd>
 * <dt>&nbsp;</dt>
 * </dl>
 * <b><i>Page Navigation</i></b>
 * <p>
 * A set of properties and actions are provided to allow a JHTML developer to
 * create controls which navigate between pages of results. Note that Page
 * Navigation requires that no more than one item type be searched. Use the the
 * following properties to create &quot;Next&quot;, &quot;Previous&quot; or
 * &quot;Go to Page #&quot; buttons.
 * <dl>
 * <dt><b><code>enableCountQuery</code></b>
 * <dd>turns the page navigation functionality on
 * <dt><b><code>maxResultsPerPage</code></b>
 * <dd>this property represents the maximum number of search results to present
 * on a page. If there are more than <code>maxResultsPerPage</code> that fit the
 * search criteria, the next set of results can be displayed when the user hits
 * a "Next" button. Default value is -1 which indicates no maximum.</dd>
 * <dt><b><code>currentResultPageNum</code></b>
 * <dd>the page of the result set is currently being viewed. Be aware that this
 * is a 1 based number so that it is displayable to the user. Set this property
 * to update the set of results for the current page. The default value is 1.</dd>
 * <dt><b><code>resultSetSize</code></b>
 * <dd>represents the total number of items matching the query</dd>
 * <dt><b><code>resultPageCount</code></b>
 * <dd>the number of pages of results. This is calculated as the resultSetSize /
 * maxResultsPerPage + 1 if any remainder exists</dd>
 * <dt><b><code>startIndex</code></b>
 * <dd>the index (0-based, inclusive) of the first record in the full result set
 * which is displayed on the current page. This property is read-only.</dd>
 * <dt><b><code>endIndex</code></b>
 * <dd>the index (0-based, exclusive) of the last record in the full result set
 * which is displayed on the current page. This property is read-only.</dd>
 * <dt><b><code>startCount</code></b>
 * <dd>the index (1-based, inclusive) of the first record in the full result set
 * which is displayed on the current page. This property this can be used to
 * display the current range of results displayed, i.e. "1-10 out of 100". This
 * property is read-only.</dd>
 * <dt><b><code>endCount</code></b>
 * <dd>the index (1-based, inclusive) of the last record in the full result set
 * which is displayed on the current page. This property this can be used to
 * display the current range of results displayed, i.e. "1-10 out of 100". This
 * property is read-only.</dd>
 * <dt>&nbsp;</dt>
 * </dl>
 * <p>
 * <b><i>Redirection</i></b>
 * <p>
 * Properties are available for setting a URL for conditional redirection. If a
 * URL parameter is not set, the redirection will not take place.
 * <dl>
 * <dt><b><code>successURL</code></b>
 * <dd>used if the query is successful and no form errors are encountered</dd>
 * <dt><b><code>errorURL</code></b>
 * <dd>used if query fails or a form error occurs</dd>
 * <dt><b><code>clearQueryURL</code></b>
 * <dd>used if the <code>clearQuery</code> action is called</dd>
 * </dl>
 * <b><i>Site Filter</i></b>
 * <p>
 * If the items being searched have a site membership property, the returned
 * items can be filtered to only include specific sites. The default behavior is
 * to filter on the current site. Either a list of sites can be specified or a
 * <code>siteScope</code> that specifies <code>all</code>, <code>any</code>,
 * <code>none</code>, or a shareable id.
 * <dl>
 * <dt><b><code>siteIds</code></b>
 * <dd>an array of site ID's to filter items by</dd>
 * <dt><b><code>siteScope</code></b>
 * <dd>can be <code>current</code> or null to filter on current site,
 * <code>any</code> to filter items that have at least one site,
 * <code>all</code> to not filter by site</dd>, <code>none</code> to filter on
 * items that don't have site membership, or any shareable id to filter on the
 * sites that share a ShareableType
 * <dt><b><code>isIncludeInactiveSites</code></b>
 * <dd>If filtering on sites, include active sites.</dd>
 * <dt><b><code>isIncludeDisabledSites</code></b>
 * <dd>If filtering on sites, include disabled sites.</dd>
 * </dl>
 * 
 * @version $Id:
 *          //product/DAS/main/Java/atg/repository/servlet/SearchFormHandler.
 *          java#46 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class SearchFormHandler extends GenericFormHandler {

  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //product/DAS/version/10.0.3/Java/atg/repository/servlet/SearchFormHandler.java#2 $$Change: 651448 $";


  // map of repositories to the previous query used to search it
  protected HashMap mPreviousQuery = new HashMap();


  //-------------------------------------
  // Constants
  //-------------------------------------

  /** resource bundle name */
  static final String RESOURCE_BUNDLE_NAME = "atg.repository.servlet.RepositoryServletResources";

  /** resource bundle */
  static ResourceBundle sResourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  /**
   * The default component path to the SiteGroupManager
   */
  static final String DEFAULT_SITE_GROUP_MANAGER_COMPONENT_PATH = "/atg/multisite/SiteGroupManager";

  static final String DEFAULT_SITE_MANAGER_COMPONENT_PATH = "/atg/multisite/SiteManager";

  static final String SINGLE_QUOTE = "'";
  static final String DOUBLE_QUOTE = "\"";

  static final String MAX = "max";
  static final String MIN = "min";

  /** droplet exception code: searched property names don't exist in item */
  public static final String INVALID_SEARCH_PROPERTY_NAMES =
    "invalidSearchPropertyNames";

  /** droplet exception code: failed to generate possible values for a property */
  public static final String GENERATE_VALUES_BY_TYPE_FAILED =
    "generateValuesByTypeFailed";

  /** droplet exception code: no property names specified for searched item */
  public static final String NO_PROPERTY_NAMES_FOR_ITEM =
    "noPropertyNamesForItem";

  /** droplet exception code:  item specified doesn't exist in repository */
  public static final String INVALID_ITEM_TYPE = "invalidItemType";

  /** droplet exception code: query failed during execution */
  public static final String QUERY_FAILURE = "queryFailed";

  /** droplet exception code: syntax problem in keyword search string */
  public static final String INVALID_KEYWORD_SEARCH_STRING =
    "invalidKeywordSearchString";

  /** droplet exception code: cant search multiple item types with navigation*/
  public static final String MULTIPLE_ITEM_TYPES_DISALLOW =
    "multItemTypeDisallow";

  /** droplet exception code: cant combine these properties */
  public static final String INVALID_PROP_COMBINATION =
    "invalidPropCombination";

  /** droplet exception code: cant combine these properties */
  public static final String EMPTY_SEARCH_VALUES_DISALLOW =
    "emptySearchValuesDisallow";

  /** logical operator search string parse failed because quotes didn't match */
  public static final String UNMATCHED_QUOTES = "unmatchedQuotes";

  /**
   * Current Site Search Scope
   */
  public static final String CURRENT_SITE_SCOPE = "current";

  /**
   * All Sites Search Scope
   */
  public static final String ALL_SITES_SCOPE = "all";

  /**
   * Any Site Search Scope
   */
  public static final String ANY_SITE_SCOPE = "any";

  /**
   * None Site Search Scope
   */
  public static final String NONE_SITE_SCOPE = "none";

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // property: Prepared
  boolean mPrepared = false;

  /**
   * Sets property Prepared.
   **/
  protected void setPrepared(boolean pPrepared) {
    mPrepared = pPrepared;
  }

  /**
   * Returns property Prepared
   **/
  protected boolean isPrepared() {
    return mPrepared;
  }


  //-------------------------------------
  // property: previouslySubmitted
  protected boolean mPreviouslySubmitted = false;

  /**
   * Returns property previouslySubmitted.
   * @return True if the handler has already handled a query.
   * @beaninfo description:  a boolean property that determines if the
   *  handler has handled a query in the current session (handler must be
   *  session-scoped). Property is reset to false if the query is cleared.
   **/
  public boolean isPreviouslySubmitted() {
    return mPreviouslySubmitted;
  }



  //-------------------------------------
  // property: allowEmptySearch
  boolean mAllowEmptySearch = true;

  /**
   * Sets property AllowEmptySearch. If "false", a droplet form exception will
   *  be thrown if none of the active search types are passed search values.
   * @param pAllowEmptySearch a boolean property that determines if passing
   *  empty search values is legal.
   * @beaninfo description:  a boolean property that determines if passing
   *  empty search values is legal.
   **/
  public void setAllowEmptySearch(boolean pAllowEmptySearch) {
    mAllowEmptySearch = pAllowEmptySearch;
  }

  /**
   * Returns property AllowEmptySearch
   * @return The value of the property AllowEmptySearch.
   **/
  public boolean getAllowEmptySearch() {
    return mAllowEmptySearch;
  }



  //-------------------------------------
  // property: allowRefine
  boolean mAllowRefine = false;

  /**
   * Sets property AllowRefine. If "true", each query effectively searches the
   *  results of the previous query.
   * @param pAllowRefine a boolean property that determines if queries can be
   *  refined.
   * @beaninfo description:  a boolean property that determines if queries can
   *  be refined.
   **/
  public void setAllowRefine(boolean pAllowRefine) {
    mAllowRefine = pAllowRefine;
  }

  /**
   * Returns property AllowRefine
   * @return The value of the property AllowRefine.
   **/
  public boolean getAllowRefine() {
    return mAllowRefine;
  }


  //-------------------------------------
  // property: DoKeywordSearch
  boolean mDoKeywordSearch = false;

  /**
   * Sets property DoKeywordSearch
   * @param pDoKeywordSearch  the property to store the boolean value of whether
   *  or not to perform a keyword search.
   * @beaninfo description:  the property to store the boolean value of whether
   *  or not to perform a keyword search.
   **/
  public void setDoKeywordSearch(boolean pDoKeywordSearch) {
    mDoKeywordSearch = pDoKeywordSearch;
  }

  /**
   * Returns property DoKeywordSearch
   * @return The value of the property DoKeywordSearch.
   **/
  public boolean isDoKeywordSearch() {
    return mDoKeywordSearch;
  }

  // -------------------------------------
  // property: useIncludesForKeywordQueryOnMultiProperties
  boolean mUseIncludesForKeywordQueryOnMultiProperties = false;

  /**
   * Whether or not to perform an INCLUDES ANY query v/s a LIKE query for
   * keyword search on multi properties
   * 
   * @param mUseIncludesForKeywordQueryOnMultiProperties
   *          the property to store the boolean value of whether or not to
   *          perform an INCLUDES ANY query v/s a LIKE query for keyword
   *          search on multi string properties.
   * @beaninfo description: the property to store the boolean value
   */
  public void setUseIncludesForKeywordQueryOnMultiProperties(
      boolean pUseIncludesForKeywordQueryOnMultiProperties) {
    mUseIncludesForKeywordQueryOnMultiProperties = pUseIncludesForKeywordQueryOnMultiProperties;
  }

  /**
   * Returns property useIncludesForKeywordQueryOnMultiProperties
   * 
   * @return The value of the property DoKeywordSearch.
   */
  public boolean isUseIncludesForKeywordQueryOnMultiProperties() {
    return mUseIncludesForKeywordQueryOnMultiProperties;
  }

  //-------------------------------------
  // property: DoTextSearch
  boolean mDoTextSearch = false;

  /**
   * Sets property DoTextSearch
   * @param pDoTextSearch  the property to store the boolean value of whether or
   *  not to perform a text search.
   * @beaninfo description:  the property to store the boolean value of whether
   *  or not to perform a text search.
   **/
  public void setDoTextSearch(boolean pDoTextSearch) {
    mDoTextSearch = pDoTextSearch;
  }

  /**
   * Returns property DoTextSearch
   * @return The value of the property DoTextSearch.
   **/
  public boolean isDoTextSearch() {
    return mDoTextSearch;
  }


  //-------------------------------------
  // property: DoHierarchicalSearch
  boolean mDoHierarchicalSearch = false;

  /**
   * Sets property DoHierarchicalSearch
   * @param pDoHierarchicalSearch  the property to store the boolean value of
   *  whether or not to perform a hierarchical search.
   * @beaninfo description:  the property to store the boolean value of whether
   *  or not to perform a hierarchical search.
   **/
  public void setDoHierarchicalSearch(boolean pDoHierarchicalSearch) {
    mDoHierarchicalSearch = pDoHierarchicalSearch;
  }

  /**
   * Returns property DoHierarchicalSearch
   * @return The value of the property DoHierarchicalSearch
   **/
  public boolean isDoHierarchicalSearch() {
    return mDoHierarchicalSearch;
  }


  //-------------------------------------
  // property: DoAdvancedSearch
  boolean mDoAdvancedSearch = false;

  /**
   * Sets property DoAdvancedSearch
   * @param pDoAdvancedSearch  the property to store the boolean value of
   *  whether or not to perform a advanced search.
   * @beaninfo description:  the property to store the boolean value of whether
   *  or not to perform a advanced search.
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
  // property: Repositories
  Repository [] mRepositories;

  /**
   * Sets property: repositories
   * @param String[] the property to store the repositories to search.
   * @beaninfo description:  the property to store the repositories to search.
   **/
  public void setRepositories(Repository [] pRepositories) {
    mRepositories = pRepositories;
  }

  /**
   * Returns property Repositories
   * @return The value of the property Repositories
   **/
  public Repository [] getRepositories() {
    return mRepositories;
  }


  //-------------------------------------
  // property: ItemTypes
  String [] mItemTypes;

  /**
   * Sets property ItemTypes
   * @param pItemTypes the property to store the item types to search.
   * @beaninfo description:  the property to store the item types to search.
   **/
  public void setItemTypes(String [] pItemTypes) {
    mItemTypes = pItemTypes;
  }

  /**
   * Returns property ItemTypes
   * @return The value of the property ItemTypes
   **/
  public String [] getItemTypes() {
    return mItemTypes;
  }


  //-------------------------------------
  // property: KeywordSearchPropertyNames
  String [] mKeywordSearchPropertyNames;
  HashMap mKeywordSearchPropertyNamesByItemType = new HashMap();

  /**
   * Sets property KeywordSearchPropertyNames
   * @param pKeywordSearchPropertyNames The property to store the names of all the
   *  keyword properties. Expects names to be qualified by item-type (i.e.
   *  "item-type.property-name".
   * @beaninfo description:  the property to store the names of all the keyword
   *  properties.
   **/
  public void setKeywordSearchPropertyNames(String [] pKeywordSearchPropertyNames) {
    mKeywordSearchPropertyNames = pKeywordSearchPropertyNames;
    mKeywordSearchPropertyNamesByItemType.clear();
  }

  /**
   * Returns property KeywordSearchPropertyNames which is the set of property names
   *  for text search
   * @return The value of the property KeywordSearchPropertyNames.
   **/
  public String [] getKeywordSearchPropertyNames() {
    return mKeywordSearchPropertyNames;
  }

  /**
   * Returns item-types mapped to lists of property names
   */
  HashMap getKeywordSearchPropertyNamesByItemType() {
    return mKeywordSearchPropertyNamesByItemType;
  }


  //-------------------------------------
  // property: TextSearchPropertyNames
  String [] mTextSearchPropertyNames;
  HashMap mTextSearchPropertyNamesByItemType = new HashMap();

  /**
   * Sets property TextSearchPropertyNames
   * @param pTextSearchPropertyNames  the property to store text search property
   *  names.
   * @beaninfo description:  the property to store text search property names.
   **/
  public void setTextSearchPropertyNames(String [] pTextSearchPropertyNames) {
    mTextSearchPropertyNames = pTextSearchPropertyNames;
    mTextSearchPropertyNamesByItemType.clear();
  }

  /**
   * Returns property TextSearchPropertyNames which is the set of property names
   *  for text search
   * @return The value of the property TextSearchPropertyNames
   **/
  public String [] getTextSearchPropertyNames() {
    return mTextSearchPropertyNames;
  }

  /**
   * Returns item-types mapped to lists of property names
   */
  HashMap getTextSearchPropertyNamesByItemType() {
    return mTextSearchPropertyNamesByItemType;
  }


  //-------------------------------------
  // property: AdvancedSearchPropertyNames
  String [] mAdvancedSearchPropertyNames;
  HashMap mAdvancedSearchPropertyNamesByItemType = new HashMap();

  /**
   * Sets property AdvancedSearchPropertyNames
   * @param pAdvancedSearchPropertyNames  the property to store advanced search
   *  property names.
   * @beaninfo description:  the property to store advanced search property
   *  names.
   **/
  public void setAdvancedSearchPropertyNames(
      String [] pAdvancedSearchPropertyNames) {
    mAdvancedSearchPropertyNames = pAdvancedSearchPropertyNames;
    mAdvancedSearchPropertyNamesByItemType.clear();
    mAdvancedSearchPropertyValues.clear();
    mAdvancedSearchPropertyRanges.clear();
  }

  /**
   * Returns property AdvancedSearchPropertyNames
   * @return The value of the property which is the set of property names to
   * search
   **/
  public String [] getAdvancedSearchPropertyNames() {
    return mAdvancedSearchPropertyNames;
  }

  /**
   * Returns item-types mapped to lists of property names
   */
  HashMap getAdvancedSearchPropertyNamesByItemType() {
    return mAdvancedSearchPropertyNamesByItemType;
  }


  //-------------------------------------
  // property: AdvancedSearchPropertyRanges
  HashMap mAdvancedSearchPropertyRanges = new HashMap();

  /**
   * Sets property AdvancedSearchPropertyRanges
   * @param Map of range values (min & max) for Advanced Search property names
   * @beaninfo description:  Map of range values (min & max) for Advanced Search
   *  property names
   **/
  public void setAdvancedSearchPropertyRanges(HashMap pRanges) {
    mAdvancedSearchPropertyRanges = pRanges;
  }

  /**
   * The SiteGroupManager
   */
  SiteGroupManager mSiteGroupManager = null;

  /**
   * Gets the SiteGroupManager
   * 
   * @return the siteGroupManager
   */
  public SiteGroupManager getSiteGroupManager() {
    if (mSiteGroupManager == null) {
      Nucleus nucleus = getNucleus();
      if (nucleus == null) {
        nucleus = Nucleus.getGlobalNucleus();
      }
      mSiteGroupManager = (SiteGroupManager) nucleus
      .resolveName(DEFAULT_SITE_GROUP_MANAGER_COMPONENT_PATH);
    }
    return mSiteGroupManager;
  }

  /**
   * Sets the SiteGroupManager
   * 
   * @param pSiteGroupManager the siteGroupManager to set
   */
  public void setSiteGroupManager(SiteGroupManager pSiteGroupManager) {
    mSiteGroupManager = pSiteGroupManager;
  }

  /**
   * The SiteManager
   */
  SiteManager mSiteManager = null;

  /**
   * Gets the SiteManager
   * 
   * @return the siteManager
   */
  public SiteManager getSiteManager() {
    if (mSiteManager == null) {
      Nucleus nucleus = getNucleus();
      if (nucleus == null) {
        nucleus = Nucleus.getGlobalNucleus();
      }
      mSiteManager = (SiteManager) nucleus
          .resolveName(
          DEFAULT_SITE_MANAGER_COMPONENT_PATH);
    }
    return mSiteManager;
  }

  /**
   * @param pSiteManager the siteManager to set
   */
  public void setSiteManager(SiteManager pSiteManager) {
    mSiteManager = pSiteManager;
  }

  /**
   * The array of site IDs
   */
  String[] mSiteIds = null;

  /**
   * Gets an array of site IDs to filter the search by.
   * 
   * @return the sites
   */
  public String[] getSiteIds() {
    return mSiteIds;
  }

  /**
   * Sets the array of site IDs to filter the search by.
   * 
   * @param pSiteIds the site IDs to set
   */
  public void setSiteIds(String[] pSiteIds) {
    mSiteIds = pSiteIds;
  }

  /**
   * The Search's Site Scope. Null defaults to current.
   */
  String mSiteScope = null;

  /**
   * Gets the Search's Site Scope. Null defaults to current. This property is
   * ignored is the sites property is set. If the value isn't "current" or
   * "all", the searchScope will be interpreted as a ShareableType id.
   * 
   * @return the siteScope
   */
  public String getSiteScope() {
    return mSiteScope;
  }

  /**
   * Sets the Search's Site Scope. Null defaults to current. This property is
   * ignored is the sites property is set. If the value isn't "current" or
   * "all", the searchScope will be interpreted as a ShareableType id.
   * 
   * @param pSiteScope the siteScope to set
   */
  public void setSiteScope(String pSiteScope) {
    mSiteScope = pSiteScope;
  }

  /**
   * Include sites that are inactive
   */
  boolean mIncludeInactiveSites = true;

  /**
   * Whether or not the site filter should include inactive sites.
   * 
   * @return if includeInactiveSites is false, only active sites should be used, true
   *         otherwise.
   */
  public boolean isIncludeInactiveSites() {
    return mIncludeInactiveSites;
  }

  /**
   * Sets whether or not the site filter should include inactive sites.
   * 
   * @param pIncludeInactiveSites the includeInactiveSites to set
   */
  public void setIncludeInactiveSites(boolean pIncludeInactiveSites) {
    mIncludeInactiveSites = pIncludeInactiveSites;
  }

  /**
   * mIncludeDisabledSites; a value of <code>true</code> indicates a preference
   * for a site in a disabled state to be considered for inclusion in the site
   * matching process. Note, that by definition, a disabled site is inactive so
   * in order to include a disabled site, both this flag and the
   * includeInactiveSites should be set to true.
   */
  private boolean mIncludeDisabledSites = true;

  /**
   * Returns the mIncludeDisabledSites property.
   * 
   * @return the mIncludeDisabledSites property.
   */
  public boolean isIncludeDisabledSites() {
    return mIncludeDisabledSites;
  }

  /**
   * Sets the mIncludeDisabledSites property.
   * 
   * @param pIncludeDisabledSites whether or not to include disabled sites in
   *          the matching process
   */
  public void setIncludeDisabledSites(boolean pIncludeDisabledSites) {
    mIncludeDisabledSites = pIncludeDisabledSites;
  }

  /**
   * Returns property AdvancedSearchPropertyRanges
   * @return map of the range values (min & max) for Advanced Search property
   *  names. Structure is map of item-type --> map of property names --> map
   *  of mix & max, i.e. product.creationDate.min = "1/1/1999"
   **/
  public HashMap getAdvancedSearchPropertyRanges() {
    return mAdvancedSearchPropertyRanges;
  }

  /**
   * test if adv search property ranges null or empty
   */
  protected boolean isAdvancedSearchPropertyRangesEmpty() {
    // iterate types
    Iterator r1 = getAdvancedSearchPropertyRanges().values().iterator();
    while (r1.hasNext()) {
      // iterate names
      Iterator r2 = ((Map)r1.next()).values().iterator();
      while (r2.hasNext()) {
        Map tmp = (Map)r2.next();
        if ( (tmp.get(MAX) != null) || (tmp.get(MIN) != null) ) {
          return false;
        }
      }
    }
    return true;
  }



  //-------------------------------------
  // property: PropertyValuesByType (for Advanced searches only)
  HashMap mPropertyValuesByType = new HashMap();

  /**
   * Returns property PropertyValuesByType.
   * Containing one key/value pair for each property named in
   * <code>advancedSearchPropertyNames</code> whose type is either enumerated
   * or RepositoryItem. The key is the name of the property and the value is a
   * Collection of the possible values.
   * @return The value of the property PropertyValuesByType
   * @beaninfo description:  A map of property names to all possible values
   **/
  public HashMap getPropertyValuesByType() {
    if (mPropertyValuesByType.isEmpty()) {
      if (isLoggingDebug()) {
        logDebug("Calling setPropertyValuesByType");
      }
      setPropertyValuesByType();
    }
    else {
      if (isLoggingDebug()) {
        logDebug("PropertyValuesByType already set");
      }
    }
    return mPropertyValuesByType;
  }

  /**
   * Sets property PropertyValuesByType.
   * Contains a map of item-types to maps of property name/values.
   */
  public void setPropertyValuesByType() {
    HashMap valuesByType = new HashMap();
    String[] types = getItemTypes();
    HashMap namesByType = getAdvancedSearchPropertyNamesByItemType();

    for (int i=0; i<types.length; i++) {

      HashMap nameValues = new HashMap();
      ArrayList names = (ArrayList)namesByType.get(types[i]);
      if ((names == null) || (names.size() == 0)) {
        Object[] args = { types[i] };
        String msg = ResourceUtils.getMsgResource(NO_PROPERTY_NAMES_FOR_ITEM,
            RESOURCE_BUNDLE_NAME, sResourceBundle, args);
        if (isLoggingError()) {
          logError(msg);
        }
        addFormException(new DropletFormException(
            msg, getAbsoluteName() + ".propertyValuesByType",
            NO_PROPERTY_NAMES_FOR_ITEM));
      }
      else {
        for (int j=0;j<names.size();j++) {
          Collection resultSet = generateSearchValues(types[i],
              (String)names.get(j));
          nameValues.put(names.get(j), resultSet);
        }
      }
      valuesByType.put(types[i], nameValues);
    }
    mPropertyValuesByType = valuesByType;
  }


  //-------------------------------------
  // property: advancedSearchPropertyValues
  HashMap mAdvancedSearchPropertyValues = new HashMap();

  /**
   * Returns property AdvancedSearchPropertyValues
   * @return The value of the property AdvancedSearchPropertyValues which
   *  is the key/value pairs for each property you want to search
   **/
  public HashMap getAdvancedSearchPropertyValues() {
    return mAdvancedSearchPropertyValues;
  }

  /**
   * Sets property AdvancedSearchPropertyValues
   * @param pAdvancedSearchPropertyValues the property to store selected
   *  property values for advanced searching. The key is the item type and the
   *  value is another map of property name to search values.
   * @beaninfo description:  the property to store selected property values
   *  for advanced searching.
   **/
  public void setAdvancedSearchPropertyValues(HashMap pValues) {
    mAdvancedSearchPropertyValues = pValues;
  }

  /**
   * test if adv search property values are null or empty
   */
  protected boolean isAdvancedSearchPropertyValuesEmpty() {
    // iterate types
    Iterator r1 = getAdvancedSearchPropertyValues().values().iterator();
    while (r1.hasNext()) {
      // iterate names
      Iterator r2 = ((Map)r1.next()).values().iterator();
      while (r2.hasNext()) {
        Object tmp = r2.next();
        if (tmp instanceof String) {
          if (!StringUtils.isBlank((String)tmp)) {
            return false;
          }
        }
        else {
          if (tmp != null) {
            return false;
          }
        }
      }
    }
    return true;
  }


  //-------------------------------------
  // property: displayName (for advanced searches)
  //
  // NOTE: fixes 33194 but still a hack.
  String mDisplayName = "displayName";

  /**
   * Returns property displayName
   * @return The value of the property displayName
   **/
  public String getDisplayName() {
    return mDisplayName;
  }

  /**
   * Sets property displayName
   * @param pDisplayName the property name to use when an advanced search
   *  queries a property that is another RepositoryItem.
   * @beaninfo description:  the property to use when an advanced search
   *  queries a property that is another RepositoryItem.
   **/
  public void setDisplayName(String pDisplayName) {
    mDisplayName = pDisplayName;
  }


  //-------------------------------------
  // property: Keywords
  String [] mKeywords;

  /**
   * Sets property Keywords
   * @param pKeywords the property to store keywords to search for in keyword
   *  property names.  if not set, the property "keywordInput" will be used
   * @beaninfo description:  the property to store keywords to search for in
   *  keyword property names.  if not set, keywords are parsed from input
   *  string.
   **/
  public void setKeywords(String [] pKeywords) {
    ArrayList tmp = new ArrayList(pKeywords.length);
    for (int i=0;i<pKeywords.length;i++) {
      if (!StringUtils.isBlank(pKeywords[i])) {
        tmp.add(pKeywords[i].trim());
      }
    }
    mKeywords = new String[tmp.size()];
    mKeywords = (String[])tmp.toArray(mKeywords);
  }

  /**
   * Returns property Keywords
   * @return The value of the property Keywords which is the target values to
   *  search
   **/
  public String [] getKeywords() {
    return mKeywords;
  }


  //-------------------------------------
  // property: keywordInput
  String mKeywordInput = "";

  /**
   * Sets property KeywordInput
   * @param pKeywordInput the property to store the input search string. Keyword
   *  search will use this if keyword property is not set.
   * @beaninfo description:  the property to store the input search string.
   **/
  public void setKeywordInput(String pKeywordInput) {
    mKeywordInput = (pKeywordInput!=null) ? pKeywordInput.trim() : null;
  }

  /**
   * Returns property KeywordInput
   * @return The value of the property KeywordInput.
   **/
  public String getKeywordInput() {
    return mKeywordInput;
  }


  //-------------------------------------
  // property: ancestorPropertyName
  String mAncestorPropertyName;

  /**
   * Sets property ancestorPropertyName
   * @param pAncestorPropertyName  the property to store the value of ancestor
   *  property name.
   * @beaninfo description:  the property to store the value of ancestor
   *  property name.
   **/
  public void setAncestorPropertyName(String pAncestorPropertyName) {
    mAncestorPropertyName = pAncestorPropertyName;
  }

  /**
   * Returns property ancestorPropertyName
   * @return The value of the property ancestorPropertyName
   **/
  public String getAncestorPropertyName() {
    return mAncestorPropertyName;
  }


  //-------------------------------------
  // property: ancestorId
  String mAncestorId = null;

  /**
   * Sets property AncestorId.
   * @param pAncestorId the property to store the repositoryId of the ancestor
   *  to search in for Hierarchical searches.
   * @beaninfo description:  the property to store the value of the repositoryId
   *  to search in for Hierarchical searches.
   **/
  public void setAncestorId(String pAncestorId)
  {
    mAncestorId = pAncestorId;
  }

  /**
   * Returns property AncestorId
   * @return The value of the property AncestorId
   **/
  public String getAncestorId()
  {
    return mAncestorId;
  }



  //-------------------------------------
  // property: textInput
  String mTextInput;

  /**
   * Sets property TextInput used for Text searches.
   * @param pTextInput the property to store the input search string.
   * @beaninfo description:  the property to store the input search string.
   **/
  public void setTextInput(String pTextInput) {
    mTextInput = (pTextInput!=null) ? pTextInput.trim() : null;
  }

  /**
   * Returns property TextInput
   * @return The value of the property TextInput.
   **/
  public String getTextInput() {
    return mTextInput;
  }


  //-------------------------------------
  // property: SearchStringFormat
  String mSearchStringFormat;

  /**
   * Sets property SearchStringFormat used by Text search. If not set, will use
   *  the default format.
   * @param pSearchStringFormat the property to store any string formatting
   *  information parameters for searching.
   * @beaninfo description:  the property to store any string formatting
   *  information parameters searching.
   **/
  public void setSearchStringFormat(String pSearchStringFormat) {
    mSearchStringFormat = pSearchStringFormat;
  }

  /**
   * Returns property SearchStringFormat
   * @return The value of the property SearchStringFormat.
   **/
  public String getSearchStringFormat() {
    return mSearchStringFormat;
  }


  //-------------------------------------
  // property: allowWildcards
  boolean mAllowWildcards = true;

  /**
   * Sets property AllowWildcards. If "true", the character '*' can be used as
   *  a wildcard in the Text search.
   * @param pAllowWildcards a boolean property that determines if Text searching
   *  should allow the character '*' to be used as a wildcard
   * @beaninfo description:  a boolean property that determines if Text searching
   *  should allow the character '*' to be used as a wildcard.
   **/
  public void setAllowWildcards(boolean pAllowWildcards) {
    mAllowWildcards = pAllowWildcards;
  }

  /**
   * Returns property AllowWildcards
   * @return The value of the property AllowWildcards.
   **/
  public boolean getAllowWildcards() {
    return mAllowWildcards;
  }


  //-------------------------------------
  // property: MinScore
  Integer mMinScore;

  /**
   * Sets property MinScore. Used by Text Searches.
   * Scoring is not yet implemented.
   * @param pMinScore the property to store the minimum scoring used for text
   *  searching.
   * @beaninfo description:  the property to store the minimum scoring used for
   *  text searching.
   **/
  public void setMinScore(Integer pMinScore) {
    mMinScore = pMinScore;
  }

  /**
   * Returns property MinScore
   * @return The value of the property MinScore
   **/
  public Integer getMinScore() {
    return mMinScore;
  }



  //-------------------------------------
  // property: SearchResults
  protected ArrayList mSearchResults = new ArrayList();

  /**
   * Returns property SearchResults
   * @return The value of the property SearchResults
   * @beaninfo description:  this property contains query results after query
   *  completes
   **/
  public Collection getSearchResults() {
    if (isEnableCountQuery()) {
      // constrain the range results to return.
      return mSearchResults.subList(mStartIndex, mEndIndex);
    }
    else {
      return mSearchResults;
    }
  }


  //-------------------------------------
  // property: SearchResultsByItemType
  protected HashMap mSearchResultsByItemType = new HashMap();

  /**
   * Returns property SearchResultsByItemType
   * @return The value of the property SearchResultsByItemType
   * @beaninfo description:  this property contains query results after query
   *  completes. Results are in a dictionary by item-type
   **/
  public HashMap getSearchResultsByItemType() {
    return mSearchResultsByItemType;
  }


  //-------------------------------------
  // property: maxRowCount
  int mMaxRowCount = -1;

  /** Sets the property maxRowCount.
   * The maximum number of rows to return from all combined queries. Set to -1
   * to disable. Default is -1.
   * @param pMaxRowCount the maximum # of rows to get
   * @beaninfo description:  the maximum # of rows to get
   */
  public void setMaxRowCount(int pMaxRowCount) {
    mMaxRowCount = pMaxRowCount;
  }

  /**
   * Returns property maxRowCount
   * @return The property MaxRowCount
   */
  public int getMaxRowCount() {
    return mMaxRowCount;
  }


  //-------------------------------------
  // property: enableCountQuery
  boolean mEnableCountQuery = false;

  /** Sets the property enableCountQuery.
   * If enable CountQuery is true, then we will fill in properties that allow
   * the jhtml programmer to provide next, previous buttons to iterate over
   * the result set.  Default is false.
   * @param pEnableCountQuery the property to enable the jhtml programmer to
   *  provide next and prev buttons to iterate over a result set.
   * @beaninfo description:  the property to enable the jhtml programmer to
   *  provide next and prev buttons to iterate over a result set.
   */
  public void setEnableCountQuery(boolean pEnableCountQuery) {
    mEnableCountQuery = pEnableCountQuery;
  }

  /**
   * Returns property enableCountQuery
   * @return The property enableCountQuery
   */
  public boolean isEnableCountQuery() {
    return mEnableCountQuery;
  }


  //-------------------------------------
  // property: maxResultsPerPage
  int mMaxResultsPerPage = -1;

  /**
   * Sets the property maxResultsPerPage.
   * This property represents the maximum number of search results to present
   * on a page. If there are more than maxResultsPerPage that fit the search
   * query, the next set of maxResultsPerPage can be displayed upon the user
   * hitting the next button. Default value is -1 which indicates no maximum.
   * @param pMaxResultsPerPage  the property to store the maximum number of
   *  results to be displayed per page.
   * @beaninfo description:  the property to store the maximum number of
   *  results to be displayed per page.
   */
  public void setMaxResultsPerPage(int pMaxResultsPerPage) {
    mMaxResultsPerPage = pMaxResultsPerPage;
  }

  /**
   * Returns property maxResultsPerPage
   * @return The maximum number of results to be displayed per page.
   */
  public int getMaxResultsPerPage() {
    return mMaxResultsPerPage;
  }


  //-------------------------------------
  // property: currentResultPageNum
  int mCurrentResultPageNum = 1;

  /**
   * Sets the property currentResultPageNum. This property tells which page of
   * the result set is currently being viewed. The default value is 1, so be
   * aware that this is a 1 based number so it is easy to display to the end
   * user.
   * @param pCurrentResultPageNum  the property to tell which page of the result
   *  set is currently being viewed.
   * @beaninfo description:  the property to tell which page of the result set
   *  is currently being viewed.
   */
  public void setCurrentResultPageNum(int pCurrentResultPageNum) {
    mCurrentResultPageNum = pCurrentResultPageNum;
  }

  /**
   * Returns property CurrentResultPageNum
   * @return The current result page being viewed.
   */
  public int getCurrentResultPageNum() {
    return mCurrentResultPageNum;
  }


  //-------------------------------------
  // property: resultSetSize
  int mResultSetSize = 0;

  /**
   * @param pResultSetSize the property that represents the number of items that
   *  match the query.
   * @beaninfo description:  the property that represents the number of items
   *  that match the query.
   */
  public void setResultSetSize(int pResultSetSize) {
    mResultSetSize = pResultSetSize;
  }

  /**
   * Returns property ResultSetSize
   * @return The number of items that match the query.
   */
  public int getResultSetSize() {
    return mResultSetSize;
  }


  //-------------------------------------
  // property: resultPageCount
  /**
   * Returns the number of results pages which is calculated
   * as the resultSetSize / maxResultsPerPage + 1 if any remainder exists.
   * @return the number of results pages which is calculated as the
   *  resultSetSize/maxResultsPerPage + 1.
   * @beaninfo description:  the number of results pages which is calculated as
   *  the resultSetSize/maxResultsPerPage + 1.
   */
  public int getResultPageCount() {
    int num = (getResultSetSize() / getMaxResultsPerPage());
    if (getResultSetSize() % getMaxResultsPerPage() > 0) {
      num++;
    }
    return num;
  }


  //-------------------------------------
  // property: StartIndex
  protected int mStartIndex = 0;

  /**
   * Returns the property startIndex.
   * This is the index (0-based) into the full result set which is displayed on
   *  the current page.
   * @return the start index (0-based) into the full result set which is
   *  displayed on the current page.
   * @beaninfo  description:  the start index (0-based) into the full result set
   *  which is displayed on the current page.
   */
  public int getStartIndex() {
    return mStartIndex;
  }

  /**
   * Set the startIndex to a particular value.  In general you should not
   * modify the start index without very good reason, e.g. you are
   * implementing a subclass of SearchFormHandler that modifies the search
   * results in some way and needs to make sure the indices represent your
   * modified result set.
   **/

  public void setStartIndex(int pIndex) {
    mStartIndex = pIndex;
  }


  //-------------------------------------
  // property: endIndex
  protected int mEndIndex = 0;

  /**
   * Returns the property EndIndex.
   * This is the index (0-based) into the full result set which is displayed on
   *  the current page.
   * @return the end index (0-based) into the full result set which is displayed
   *  on the current page.
   * @beaninfo description: end index (0-based) into the full result set which
   *  is displayed on the current page.
   **/
  public int getEndIndex() {
    return mEndIndex;
  }


  /**
   * Set the endIndex to a particular value.  In general you should not
   * modify the end index without very good reason, e.g. you are
   * implementing a subclass of SearchFormHandler that modifies the search
   * results in some way and needs to make sure the indices represent your
   * modified result set.
   **/

  public void setEndIndex(int pIndex) {
    mEndIndex = pIndex;
  }



  //-------------------------------------
  // property: StartCount

  /**
   * Returns the property startCount.
   * This is the index (1-based) into the full result set which is displayed on
   *  the current page.
   * @return the start index (1-based) into the full result set which is
   *  displayed on the current page.
   * @beaninfo  description:  the start index (1-based) into the full result set
   *  which is displayed on the current page.
   */
  public int getStartCount() {
    return mStartIndex+1;
  }


  //-------------------------------------
  // property: endCount

  /**
   * Returns the property EndCount.
   * This is the index (1-based) into the full result set which is displayed on
   *  the current page.
   * @return the end index (1-based) into the full result set which is displayed
   *  on the current page.
   * @beaninfo description: end index (1-based) into the full result set which
   *  is displayed on the current page.
   **/
  public int getEndCount() {
    return mEndIndex;
  }


  //-------------------------------------
  // property: ToUpperCaseKeywords
  boolean mToUpperCaseKeywords = false;

  /**
   * Sets property ToUpperCaseKeywords
   * @param pToUpperCaseKeywords  the property to store the boolean value of
   *  whether or not to convert input string to upper case.
   * @beaninfo description:  the property to store the boolean value of whether
   *  or not to convert input string to upper case.
   **/
  public void setToUpperCaseKeywords(boolean pToUpperCaseKeywords) {
    mToUpperCaseKeywords = pToUpperCaseKeywords;
  }

  /**
   * Returns property ToUpperCaseKeywords
   * @return The value of the property ToUpperCaseKeywords
   **/
  public boolean isToUpperCaseKeywords() {
    return mToUpperCaseKeywords;
  }


  //-------------------------------------
  // property: ToLowerCaseKeywords
  boolean mToLowerCaseKeywords = false;

  /**
   * Sets property ToLowerCaseKeywords
   * @param pToLowerCaseKeywords  the property to store the boolean value of
   *  whether or not to convert input string to lower case.
   * @beaninfo description:  the property to store the boolean value of whether
   *  or not to convert input string to lower case.
   **/
  public void setToLowerCaseKeywords(boolean pToLowerCaseKeywords) {
    mToLowerCaseKeywords = pToLowerCaseKeywords;
  }

  /**
   * Returns property ToLowerCaseKeywords
   * @return The value of the property ToLowerCaseKeywords
   **/
  public boolean isToLowerCaseKeywords() {
    return mToLowerCaseKeywords;
  }



  //-------------------------------------
  // property: successURL
  String mSuccessURL;

  /**
   * Sets property SuccessURL. If search is successful, hander redirects client
   *  to this URL.
   * @param pURL a String property that is the URL to redirect to.
   * @beaninfo description:  a String property that is the URL to redirect to.
   **/
  public void setSuccessURL(String pURL) {
    mSuccessURL = pURL;
  }

  /**
   * Returns property SuccessURL
   * @return The value of the property SuccessURL.
   **/
  public String getSuccessURL() {
    return mSuccessURL;
  }



  //-------------------------------------
  // property: errorURL
  String mErrorURL;

  /**
   * Sets property ErrorURL. If search fails, hander redirects client
   *  to this URL. For example, RepositoryException thrown
   * @param pURL a String property that is the URL to redirect to.
   * @beaninfo description:  a String property that is the URL to redirect to.
   **/
  public void setErrorURL(String pURL) {
    mErrorURL = pURL;
  }

  /**
   * Returns property ErrorURL
   * @return The value of the property ErrorURL.
   **/
  public String getErrorURL() {
    return mErrorURL;
  }



  //-------------------------------------
  // property: clearQueryURL
  String mClearQueryURL;

  /**
   * Sets property ClearQueryURL. If the Query is cleared, the form redirects
   *  to this URL.
   * @param pURL a String property that is the URL to redirect to.
   * @beaninfo description:  a String property that is the URL to redirect to.
   **/
  public void setClearQueryURL(String pURL) {
    mClearQueryURL = pURL;
  }

  /**
   * Returns property ClearQueryURL
   * @return The value of the property ClearQueryURL.
   **/
  public String getClearQueryURL() {
    return mClearQueryURL;
  }






  //-------------------------------------
  // Handlers
  //-------------------------------------

  /**
   * For each each item type in that repository, call generateSearchResultSet
   * to generate a subResultSet for that item type based on query parameters.
   * Each subResultSet will be both merged together in resultSet as well as
   * stored in the property SearchResultsByItemType by item type.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return false to stop form processing
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleSearch(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse)
  throws ServletException, IOException
  {
    // beforeSet, beforeGet or handleSearch should catch
    prepare(pRequest, pResponse);

    // check for empty search
    if (!getAllowEmptySearch() && areSearchValuesEmpty()) {
      String msg = ResourceUtils.getMsgResource(EMPTY_SEARCH_VALUES_DISALLOW,
          RESOURCE_BUNDLE_NAME, sResourceBundle);
      if (isLoggingDebug()) {
        logDebug(msg);
      }
      addFormException(new DropletFormException(
          msg, getAbsoluteName() + ".search",
          EMPTY_SEARCH_VALUES_DISALLOW));

      return checkFormRedirect(
          getSuccessURL(), getErrorURL(), pRequest, pResponse);
    }

    // check for multiple item types
    if ( isEnableCountQuery() && (getItemTypes().length > 1) ) {
      String msg = ResourceUtils.getMsgResource(MULTIPLE_ITEM_TYPES_DISALLOW,
          RESOURCE_BUNDLE_NAME, sResourceBundle);
      if (isLoggingError()) {
        logError(msg);
      }
      addFormException(new DropletFormException(
          msg, getAbsoluteName() + ".search",
          MULTIPLE_ITEM_TYPES_DISALLOW));

      return checkFormRedirect(
          getSuccessURL(), getErrorURL(), pRequest, pResponse);
    }

    // clear old results
    mStartIndex = 0;
    mEndIndex = 0;
    setResultSetSize(0);
    mSearchResults.clear();
    mSearchResultsByItemType.clear();
    setCurrentResultPageNum(1);

    // get results for each item type
    for (int c=0; c<getItemTypes().length; c++) {
      Collection subResultSet = generateResultSet(getItemTypes()[c]);
      if (subResultSet != null) {
        mSearchResults.addAll(subResultSet);
        mSearchResultsByItemType.put(getItemTypes()[c], subResultSet);
      }
      if (isLoggingDebug()) {
        int size = (subResultSet != null) ? subResultSet.size() : 0;
        logDebug("resultSet for type: " + getItemTypes()[c] + ", size: "
            + size + ", results: " + subResultSet);
      }
    }

    setResultSetSize(mSearchResults.size());
    mPreviouslySubmitted = true;

    if (isEnableCountQuery()) {
      // constrain the results
      mStartIndex = (getCurrentResultPageNum()-1) * getMaxResultsPerPage();
      mEndIndex = mStartIndex + getMaxResultsPerPage();

      if (mEndIndex > getResultSetSize()) {
        mEndIndex = getResultSetSize();
      }
    }

    return checkFormRedirect(
        getSuccessURL(), getErrorURL(), pRequest, pResponse);
  }


  /**
   * Called when user is iterating through pages of results. Instead of
   *  requerying, we set pointers to start and end of page results.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return false to stop form processing
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleCurrentResultPageNum(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse)
  throws ServletException, IOException
  {
    if (!isEnableCountQuery()) {
      String msg = ResourceUtils.getMsgResource(INVALID_PROP_COMBINATION,
          RESOURCE_BUNDLE_NAME, sResourceBundle);
      if (isLoggingError()) {
        logError(msg);
      }
      addFormException(new DropletFormException(
          msg, getAbsoluteName() + ".currentResultPageNum",
          INVALID_PROP_COMBINATION));

      return checkFormRedirect(
          getSuccessURL(), getErrorURL(), pRequest, pResponse);
    }

    // constrain the result set
    mStartIndex = (getCurrentResultPageNum()-1) * getMaxResultsPerPage();
    mEndIndex = mStartIndex + getMaxResultsPerPage();

    if (mEndIndex > getResultSetSize()) {
      mEndIndex = getResultSetSize();
    }

    return checkFormRedirect(
        getSuccessURL(), getErrorURL(), pRequest, pResponse);
  }


  /**
   * Called if user requests stored query be cleared. Will redirect to
   *  ClearQuery url if non-null and no errors have occured.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return false if we are redirecting
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleClearQuery(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse)
  throws ServletException, IOException
  {
    mPreviousQuery.clear();
    mPreviouslySubmitted = false;
    return checkFormRedirect(getClearQueryURL(), null, pRequest, pResponse);
  }


  /**
   * Called when a form is rendered that references this bean.  This call
   * is made before the service method of the page is invoked.
   */
  @Override
  public void beforeGet(DynamoHttpServletRequest request,
      DynamoHttpServletResponse response)
  {
    // beforeSet, beforeGet or handleSearch should catch
    prepare(request, response);
    super.beforeGet(request, response);
  }


  /**
   * Called before any setX methods on this form are set when a form
   * that modifies properties of this form handler is submitted.
   */
  @Override
  public boolean beforeSet(DynamoHttpServletRequest request,
      DynamoHttpServletResponse response)
  throws DropletFormException
  {
    // beforeSet, beforeGet or handleSearch should catch
    prepare(request, response);

    // prepare query container
    if (getAllowRefine() && mPreviousQuery.isEmpty()) {
      if (isLoggingDebug()) {
        logDebug("initializing query container");
      }

      for (int a=0;a<getRepositories().length;a++) {
        String repName = getRepositories()[a].getRepositoryName();
        mPreviousQuery.put(repName, new HashMap());
        for (int b=0;b<getItemTypes().length;b++) {
          ((HashMap)mPreviousQuery.get(repName)).put(getItemTypes()[b], null);
        }
      }
    }

    // reset properties that wouln't be cleared by form resubmit and
    //  but that should be set on each submission
    if (mPreviouslySubmitted) {
      if (isLoggingDebug()) {
        logDebug("clearing out previously submitted search values");
      }

      // advanced search only
      if (isDoAdvancedSearch()) {
        Iterator r1 = getAdvancedSearchPropertyRanges().values().iterator();
        while (r1.hasNext()) {
          Iterator r2 = ((HashMap)r1.next()).values().iterator();
          while (r2.hasNext()) {
            ((HashMap)r2.next()).clear();
          }
        }

        r1 = getAdvancedSearchPropertyValues().values().iterator();
        while (r1.hasNext()) {
          ((HashMap)r1.next()).clear();
        }
        getPropertyValuesByType().clear();
      }

      // text search only
      if (isDoTextSearch()) {
        setTextInput(null);
      }
    }

    return super.beforeSet(request, response);
  }





  //-------------------------------------
  // Helper Methods
  //-------------------------------------

  // check to see if all search values are empty for enabled search types
  protected boolean areSearchValuesEmpty() {
    if (isDoAdvancedSearch()) {
      if (!isAdvancedSearchPropertyValuesEmpty() ||
          !isAdvancedSearchPropertyRangesEmpty()) {
        return false;
      }
    }

    if (isDoKeywordSearch()) {
      if ( ((getKeywords() != null) && (getKeywords().length != 0))
          || !StringUtils.isBlank(getKeywordInput()) ) {
        return false;
      }
    }

    if (isDoHierarchicalSearch()) {
      if (!StringUtils.isBlank(getAncestorId())) {
        return false;
      }
    }

    if (isDoTextSearch()) {
      if (!StringUtils.isBlank(getTextInput())) {
        return false;
      }
    }

    // all search values are blank
    return true;
  }


  /**
   * Return the query that should be executed on the Repository View.  This
   * method attempts to build its query from keyword, text, advanced, and
   * hierarchical search subqueries.  It calls each sub method to build the sub
   * query. The method attempts to build an OR query between keyword and text
   * search and an AND with advanced or hierarchical search queries
   *
   * @param pRepository the repository to search in
   * @param pItemType the type of item to search for.
   * @param pQueryBuilder the builder that should be used to construct the query
   * @exception RepositoryException if an error occured while forming the query
   */
  protected Query generateSearchQuery(Repository pRepository, String pItemType,
      QueryBuilder pQueryBuilder)
  throws RepositoryException, DropletFormException
  {
    Query keywordsQuery = null;
    Query textSearchQuery = null;
    Query advancedQuery = null;
    Query hierarchicalQuery = null;

    if (isDoKeywordSearch()) {
      keywordsQuery = generateKeywordSearchQuery(pRepository, pItemType,
          pQueryBuilder);
    }
    if (isDoTextSearch()) {
      textSearchQuery = generateTextSearchQuery(pItemType, pQueryBuilder,
          getTextInput());
    }
    if (isDoAdvancedSearch()) {
      advancedQuery = generateAdvancedSearchQuery(pRepository, pItemType,
          pQueryBuilder);
    }
    if (isDoHierarchicalSearch()) {
      hierarchicalQuery = generateHierarchicalSearchQuery(pItemType,
          pQueryBuilder);
    }
    if (isLoggingDebug()) {
      logDebug("keywordsQuery=" + keywordsQuery + "; textSearchQuery="
          + textSearchQuery + "; advancedQuery=" + advancedQuery
          + "; hierarchicalQuery=" + hierarchicalQuery);
    }

    /**
     * The keyword query and the text query are ORed together.
     * The other types of queries (advanced, hierarchical) are ANDed together
     * with each other, and with the result of (keyword-query OR text-query)
     **/

    /**
     * Count how many queries there are for a potential AND
     **/
    int andableQueryCount = 0;

    /**
     * Keyword-query, text-query count as 1 if either is present
     **/
    if ((keywordsQuery != null) || (textSearchQuery != null)) {
      andableQueryCount ++;
    }
    if (advancedQuery != null) {
      andableQueryCount ++;
    }
    if (hierarchicalQuery != null) {
      andableQueryCount ++;
    }

    /**
     * Allocate an array for the potentially-necessary AND
     **/
    Query [] andableQueries = new Query[andableQueryCount];

    /**
     * Fill in that array
     **/
    int queryArrayIndex = 0;

    /**
     * Use up at most one slot in the array for keyword/text,
     * ORing them together if both are present
     **/
    if ((keywordsQuery != null) || (textSearchQuery != null))
    {
      if ((keywordsQuery != null) && (textSearchQuery != null) )
      {
        Query [] qSubArray = {keywordsQuery, textSearchQuery};
        andableQueries[queryArrayIndex++] =
          pQueryBuilder.createOrQuery(qSubArray);
      }
      else if (keywordsQuery != null) {
        andableQueries[queryArrayIndex++] = keywordsQuery;
      }
      else {
        andableQueries[queryArrayIndex++] = textSearchQuery;
      }
    }

    if (advancedQuery != null) {
      andableQueries[queryArrayIndex++] = advancedQuery;
    }

    if (hierarchicalQuery != null) {
      andableQueries[queryArrayIndex++] = hierarchicalQuery;
    }

    if (andableQueryCount == 0) {
      return null;
    }

    /**
     * If there was only one query (counting keyword+text as 1 even if
     * both were present, since they have already been ORed together)
     * then just return that one query
     **/

    if (andableQueryCount == 1) {
      return andableQueries[0];
    }

    /**
     * If in fact there were two or more AND-able queries, construct the AND
     **/

    return pQueryBuilder.createAndQuery(andableQueries);
  }


  /**
   * Return a query which represents a keyword search. If <code>keywords</code>
   * property is set, this method builds a sub-query for each attribute named in
   * the <code>KeywordSearchPropertyNames</code> property. Each sub-query is OR'd
   * together to form the total query. If the attribute named is of a Collection
   * or array type, then an "includes any" query is formed. Single-value
   * attributes use a CONTAINS comparison query.
   *
   * If <code>keywords</code> is empty, <code>keywordInput</code> is used to
   * build an RQL statement instead.
   *
   * @param pRepository the Repository to search in
   * @param pItemType the type of item to search for. Typically corresponds to
   * an item descriptor name
   * @param pQueryBuilder the builder that should be used to construct the query
   * @exception RepositoryException if an error occured while forming the query
   */
  protected Query generateKeywordSearchQuery(Repository pRepository,
      String pItemType, QueryBuilder pQueryBuilder)
  throws RepositoryException, DropletFormException
  {
    ArrayList keywordNames =
      (ArrayList)getKeywordSearchPropertyNamesByItemType().get(pItemType);
    String [] keywords = getKeywords();
    String keywordInput = getKeywordInput();

    if (  (keywordNames == null)
        || ( ((keywords == null) || (keywords.length == 0))
            && StringUtils.isBlank(keywordInput) )  ) {
      return null;
    }

    int num = keywordNames.size();
    List subQueries = new ArrayList(num);

    if ((keywords != null) && (keywords.length > 0)) {
      for (int c=0; c<num; c++) {
        keywords = transformKeywords(keywords);
        Query q = generateSingleKeywordQuery(pRepository, pItemType,
            pQueryBuilder, (String)keywordNames.get(c), keywords);
        subQueries.add(q);
      }
    }
    else {
      for (int c=0; c<num; c++) {
        Query q = generateSingleKeywordQuery(pRepository, pItemType,
            pQueryBuilder, (String)keywordNames.get(c), keywordInput);
        if (q != null) {
          subQueries.add(q);
        }
      }
    }

    if (subQueries.size() == 0) {
      return null;
    }
    else if (subQueries.size() == 1) {
      return (Query)subQueries.get(0);
    }
    else {
      Query [] qArray = new Query[subQueries.size()];
      return pQueryBuilder.createOrQuery((Query [])subQueries.toArray(qArray));
    }

  }


  /**
   * Return a query for the given property name against the set of supplied
   * keywords. If the data type of the property is multi-value, then build an
   * "includes any" query. Otherwise OR together a pattern match CONTAINS query
   * for each keyword value and property combination.
   */
  Query generateSingleKeywordQuery(Repository pRepository,
      String pItemType,
      QueryBuilder pQueryBuilder,
      String pPropertyName,
      String [] pKeywords)
  throws RepositoryException
  {
    QueryExpression propertyQE =
      pQueryBuilder.createPropertyQueryExpression(pPropertyName);
    boolean isMulti =
      isMultiValueProperty(pRepository, pItemType, pPropertyName);

    if (isLoggingDebug()) {
      logDebug("property '" + pPropertyName + "' of type '" + pItemType
          + "'; multi=" + isMulti);
    }
    if (isMulti && isUseIncludesForKeywordQueryOnMultiProperties()) {
      QueryExpression valueQE =
        pQueryBuilder.createConstantQueryExpression(pKeywords);
      return pQueryBuilder.createIncludesAnyQuery(propertyQE, valueQE);
    }
    else {
      int num = pKeywords.length;
      List subQueries = new ArrayList(num);
      for (int i=0; i<num; i++) {
        QueryExpression valueQE =
          pQueryBuilder.createConstantQueryExpression(pKeywords[i]);
        Query q = pQueryBuilder.createPatternMatchQuery(propertyQE, valueQE,
            QueryBuilder.CONTAINS, true);
        subQueries.add(q);
      }
      Query [] qArray = new Query[subQueries.size()];
      return pQueryBuilder.createOrQuery((Query [])subQueries.toArray(qArray));
    }
  }

  /**
   * Parses pKeywordInput and converts into RQL statement where each keyword
   * value is used to create a CONTAINS clause.
   */
  Query generateSingleKeywordQuery(Repository pRepository,
      String pItemType,
      QueryBuilder pQueryBuilder,
      String pPropertyName,
      String pInput)
  throws RepositoryException, DropletFormException
  {
    // +case: left quote     ---> prefix with 'paramName' CONTAINS
    // +case: right quote    ---> append to search value
    // +case: 'val1' 'val2'  ---> add OR between
    // +case: 'val' (        ---> add OR between
    // +case: ) 'val'        ---> add OR between
    // +case: & 'val'        ---> convert to   AND 'val'
    // +case: 'val' |        ---> convert to   'val' OR
    // +case: !'val'         ---> convert to   NOT 'val'
    // +case: !(             ---> convert to   NOT (
    // +case: 'val' NOT      ---> convert to   'val' OR NOT
    // +case: 'val'!         ---> convert to   'val' OR NOT
    // +case: anything after left quote    ---> add to buffer
    // +default: treat like a search value, add quotes

    if (StringUtils.isBlank(pInput)) {
      return null;
    }
    boolean isMulti =
      isMultiValueProperty(pRepository, pItemType, pPropertyName);
    String clause = (isMulti && isUseIncludesForKeywordQueryOnMultiProperties()) ? " INCLUDES " : " CONTAINS ";

    if (isLoggingDebug()) {
      logDebug("property '" + pPropertyName + "' of type '" + pItemType
          + "'; multi=" + isMulti + "; clause="+clause);
    }

    StringBuffer buff = new StringBuffer();

    boolean inQuoted = false;
    boolean wordLast = false;

    int q1 = pInput.lastIndexOf(SINGLE_QUOTE);
    int q2 = pInput.lastIndexOf(DOUBLE_QUOTE);

    String quote = (q1 > q2) ? SINGLE_QUOTE : DOUBLE_QUOTE;

    String delims = " \t\n\r!&|()" + quote;
    StringTokenizer tokens = new StringTokenizer(pInput, delims, true);

    while (tokens.hasMoreTokens()) {
      String token = tokens.nextToken();
      if (token.equals(quote)) {
        if (!inQuoted) {   // start quote
          if (wordLast) {
            buff.append(" OR ");
          }
          buff.append(pPropertyName + clause);
          buff.append(DOUBLE_QUOTE);
          wordLast=false;
        }
        else {            // end quote
          buff.append(DOUBLE_QUOTE);
          wordLast=true;
        }
        inQuoted = !inQuoted;
      }
      else if (token.equals("(")) {
        if (wordLast) {
          buff.append(" OR ");
        }
        buff.append('(');
        wordLast=false;
      }
      else if (token.equals(")")) {
        buff.append(')');
        wordLast=true;
      }
      else if (token.equals("!")) {
        if (wordLast) {
          buff.append(" OR ");
        }
        buff.append(" NOT ");
        wordLast=false;
      }
      else if (token.equals("&")) {
        buff.append(" AND ");
        wordLast=false;
      }
      else if (token.equals("|")) {
        buff.append(" OR ");
        wordLast=false;
      }
      else {
        if (inQuoted) {
          buff.append(token);
        }
        else if (token.equals(" "))
        {
          // eat it
        }
        else if (token.equalsIgnoreCase("OR") ||
            token.equalsIgnoreCase("AND")) {
          buff.append(" "+token+" ");
          wordLast=false;
        }
        else if (token.equalsIgnoreCase("NOT")) {
          if (wordLast) {
            buff.append(" OR ");
          }
          buff.append(" NOT ");
          wordLast=false;
        }
        else {
          if (wordLast) {
            buff.append(" OR ");
          }
          buff.append(pPropertyName + clause);
          buff.append(DOUBLE_QUOTE);
          buff.append(token);
          buff.append(DOUBLE_QUOTE);
          wordLast=true;
        }
      } // end if
    } // wend

    // make sure quotes are matched
    if (inQuoted) {
      String msg = ResourceUtils.getMsgResource(UNMATCHED_QUOTES,
          RESOURCE_BUNDLE_NAME, sResourceBundle);
      if (isLoggingDebug()) {
        logDebug(msg);
      }
      throw new DropletFormException(msg,
          getAbsoluteName() + ".keywordInput",
          UNMATCHED_QUOTES);
    }

    if (isLoggingDebug()) {
      logDebug("Query: "+buff.toString());
    }

    RqlStatement stmt;
    try {
      stmt = RqlStatement.parseRqlStatement(buff.toString());
    }
    catch (RepositoryException re) {
      String msg = ResourceUtils.getMsgResource(INVALID_KEYWORD_SEARCH_STRING,
          RESOURCE_BUNDLE_NAME, sResourceBundle);
      if (isLoggingDebug()) {
        logDebug(msg, re);
      }
      throw new DropletFormException(
          msg, re, getAbsoluteName() + ".keywordInput",
          INVALID_KEYWORD_SEARCH_STRING);
    }
    catch (TokenMgrError tme) {
      String msg = ResourceUtils.getMsgResource(INVALID_KEYWORD_SEARCH_STRING,
          RESOURCE_BUNDLE_NAME, sResourceBundle);
      if (isLoggingDebug()) {
        logDebug(msg, tme);
      }
      throw new DropletFormException(
          msg, tme, getAbsoluteName() + ".keywordInput",
          INVALID_KEYWORD_SEARCH_STRING);
    }
    RqlQuery qry = stmt.getQuery();
    Query q = qry.createQuery(pQueryBuilder,
        pQueryBuilder.getRepositoryView(), null);
    return q;
  }



  /**
   * Return a query which represents a text search query. This method builds a
   * sub-query for each attribute named in the
   * <code>textSearchPropertyNames</code> property. Each sub-query is OR'd
   * together to form the total query. The search string format is configured
   * with the <code>searchStringFormat</code> property.
   * @param pItemType the type of item to search for.
   * @param pQueryBuilder the builder that should be used to construct the query
   * @param pInput the search string to use for the full text search
   * @exception RepositoryException if an error occured while forming the query
   */
  protected Query generateTextSearchQuery(String pItemType,
      QueryBuilder pQueryBuilder,
      String pInput)
  throws RepositoryException
  {
    if (!getAllowEmptySearch()) {
      if (StringUtils.isBlank(pInput)) {
        return null;
      }
    }
    else {
      if(pInput == null) {
        pInput = "";  // protect against NPE when empty searches are allowed
      }
    }

    if ( getAllowWildcards() && (pInput.indexOf('*') > -1) ) {
      pInput = StringUtils.replace(pInput, "*", "%");
    }

    QueryExpression searchString =
      pQueryBuilder.createConstantQueryExpression(pInput);
    QueryExpression minScore =
      pQueryBuilder.createConstantQueryExpression(getMinScore());

    QueryExpression searchStringFormat = null;
    if (getSearchStringFormat() != null) {
      searchStringFormat = pQueryBuilder.createConstantQueryExpression(
          getSearchStringFormat());
    }

    ArrayList searchNames =
      (ArrayList)getTextSearchPropertyNamesByItemType().get(pItemType);

    if (searchNames == null) {
      /**
       * @todo Fix with Bug# 35864 -- scheduled for 5.1.1 patch 1.
       * This would fail if the repository doesn't have an associated full-text
       *  search engine. The default behavior is to simulate a full-text search
       *  with a pattern-match query which requires a property name. UNCOMMENT
       *  WHEN BUG IS FIXED.
       */
      //      return pQueryBuilder.createTextSearchQuery(searchString,
      //                                                  searchStringFormat, minScore);
      return null;
    }
    else {
      int num = searchNames.size();
      if (num > 0) {
        List subQueries = new ArrayList(num);
        for (int c=0; c<num; c++) {
          QueryExpression property =
            pQueryBuilder.createPropertyQueryExpression(
                (String)searchNames.get(c));
          Query q = pQueryBuilder.createTextSearchQuery(property, searchString,
              searchStringFormat, minScore);
          subQueries.add(q);
        }
        Query [] qArray = new Query[subQueries.size()];
        return pQueryBuilder.createOrQuery(
            (Query [])subQueries.toArray(qArray));
      }
    }

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
   * @param pRepository the repository to search
   * @param pItemType the type of item to search for.
   * @param pQueryBuilder the builder that should be used to construct the query
   * @exception RepositoryException if an error occured while forming the query
   */
  protected Query generateAdvancedSearchQuery(Repository pRepository,
      String pItemType, QueryBuilder pQueryBuilder)
  throws RepositoryException, DropletFormException
  {
    Vector subQueries = new Vector();
    String property = "";
    Object value;
    HashMap range;
    Query q;

    ArrayList searchNames =
      (ArrayList)getAdvancedSearchPropertyNamesByItemType().get(pItemType);

    if (searchNames == null) {
      return null;
      //RepositoryItemDescriptor item = pRepository.getItemDescriptor(pItemType);
    }

    /* for each advanced search property, build the subquery if required */
    for (int c=0; c<searchNames.size(); c++) {
      property = (String)searchNames.get(c);
      value = ((HashMap)getAdvancedSearchPropertyValues().get(pItemType))
      .get(property);
      range = (HashMap)
      ((HashMap)getAdvancedSearchPropertyRanges().get(pItemType))
      .get(property);

      Object min = range.get(MIN);
      Object max = range.get(MAX);

      // if value is empty, skip
      if ((value == null) && (min == null) && (max == null)) {
        if (isLoggingDebug()) {
          logDebug("Advanced Search: no range or value for property = "+property);
        }
        continue;
      }

      // also skip empty strings
      if ( (value instanceof String) && StringUtils.isBlank((String)value) ) {
        if (isLoggingDebug()) {
          logDebug("Advanced Search: empty string val for property = "+property);
        }
        continue;
      }

      /* if property is a multivalue property, build an includes any query
           of property includes value */
      if (isMultiValueProperty(pRepository, pItemType, property)) {

        QueryExpression pProperty =
          pQueryBuilder.createPropertyQueryExpression(property);

        if (!value.getClass().isArray()) {
          Object[] tmp = {value};
          value = tmp;
        }
        QueryExpression pValue =
          pQueryBuilder.createConstantQueryExpression(value);
        q = pQueryBuilder.createIncludesAnyQuery(pProperty, pValue);
        subQueries.add(q);
      }
      /* else build a simple comparison query of property=value OR a range query */
      else {
        RepositoryItemDescriptor itemDesc =
          pRepository.getItemDescriptor(pItemType);
        RepositoryPropertyDescriptor pd =
          (RepositoryPropertyDescriptor)itemDesc.getPropertyDescriptor(
              property);
        if (pd != null) {
          Class propertyType = pd.getPropertyType();
          if (propertyType != null){
            if (RepositoryItem.class.isAssignableFrom(propertyType)) {
              property = property+"."+getDisplayName();
            }
            QueryExpression pProperty =
              pQueryBuilder.createPropertyQueryExpression(property);
            if (value != null) {
              QueryExpression pValue =
                pQueryBuilder.createConstantQueryExpression(value);
              q = pQueryBuilder.createComparisonQuery(pProperty, pValue,
                  QueryBuilder.EQUALS);
              subQueries.add(q);
            }
            else {
              Query qMax = null, qMin = null;
              if (max != null) {
                qMax = pQueryBuilder.createComparisonQuery(
                    pProperty, pQueryBuilder.createConstantQueryExpression(max),
                    QueryBuilder.LESS_THAN_OR_EQUALS);
                subQueries.add(qMax);
              }
              if (min != null) {
                qMin = pQueryBuilder.createComparisonQuery(
                    pProperty, pQueryBuilder.createConstantQueryExpression(min),
                    QueryBuilder.GREATER_THAN_OR_EQUALS);
                subQueries.add(qMin);
              }
            }
          } // end if proptype != null
        } // end if pd != null
        else {
          Object[] args = { "advanced", property };
          String msg = ResourceUtils.getMsgResource(
              INVALID_SEARCH_PROPERTY_NAMES,
              RESOURCE_BUNDLE_NAME, sResourceBundle, args);
          if (isLoggingError()) {
            logError(msg);
          }
          throw new DropletFormException(
              msg, getAbsoluteName() + ".advancedSearchPropertyNames",
              INVALID_SEARCH_PROPERTY_NAMES);
        }
      } // end else (not multi-prop
    } // end for loop
    if (subQueries.size() > 0){
      Query [] qArray = new Query[subQueries.size()];
      return pQueryBuilder.createAndQuery(
          (Query [])subQueries.toArray(qArray));
    }
    else {
      return null;
    }
  }


  /**
   * Build a query which represents a hierarchical search, that is, a constraint
   * to look only in a designated item, its child items, and so on,
   * in effect, to all descendants of the designated item.  The designated
   * item is indicated by repository ID in the <I>ancestorId</I> property.  If
   * that repository ID is null or null string, then this method generates a
   * null query.
   * <P>
   * Note: this query assumes that the item type being searched for has a
   * property (as indicated by the value of the<I>ancestorPropertyName</I>
   * property) whose value is a collection of all its ancestor groups.  If the
   * designated item is contained in this collection, then this query returns
   * true.
   * @param pItemType the type of item to search for. Typically corresponds to
   * an item descriptor name
   * @param pQueryBuilder the builder that should be used to construct the query
   * @exception RepositoryException if an error occured while forming the query
   */
  protected Query generateHierarchicalSearchQuery(String pItemType,
      QueryBuilder pQueryBuilder)
  throws RepositoryException
  {
    String ancestorId = getAncestorId();
    if (StringUtils.isBlank(ancestorId)) {
      return null;
    }

    QueryExpression propertyExpression =
      pQueryBuilder.createPropertyQueryExpression(getAncestorPropertyName());
    QueryExpression rootItem =
      pQueryBuilder.createConstantQueryExpression(ancestorId);
    Query includesQuery =
      pQueryBuilder.createIncludesQuery(propertyExpression, rootItem);

    return includesQuery;
  }


  /**
   * Find all queryable string properties in each item and put them in
   *  format "item-type.prop-name".
   * @return  An array of String representing all string properties.
   */
  String[] getStringPropertyNames() {
    Repository[] reps = getRepositories();
    if ( (reps == null) || (reps.length == 0) ) {
      return null;
    }
    Repository rep = reps[0];
    String[] types = getItemTypes();
    ArrayList names = new ArrayList();

    try {
      for (int i=0;i<types.length;i++) {
        RepositoryItemDescriptor itemDesc = rep.getItemDescriptor(types[i]);
        RepositoryPropertyDescriptor contextMembershipDescriptor = itemDesc
            .getContextMembershipProperty();
        String contextMembershipPropertyName = null;
        if (contextMembershipDescriptor != null) {
          contextMembershipPropertyName = contextMembershipDescriptor.getName();
        }

        DynamicPropertyDescriptor[] props = itemDesc.getPropertyDescriptors();
        for (int j=0;j<props.length;j++) {
          Class type = props[j].getPropertyType();
          Class compType = props[j].getComponentPropertyType();

          // if string property
          if ( String.class.isAssignableFrom(type) ||
              ( (compType !=null) && String.class.isAssignableFrom(compType)) )
          {
            if (props[j] instanceof RepositoryPropertyDescriptor) {
              RepositoryPropertyDescriptor repProp =
                (RepositoryPropertyDescriptor)props[j];
              if ( !repProp.isQueryable()
                  || repProp.getTypeName().equalsIgnoreCase("enumerated") ) {
                continue;
              }
            }
            if (props[j].getName().equals(contextMembershipPropertyName)) {
              vlogDebug("Not including contextMembershipProperty {0}",
                  contextMembershipPropertyName);
              continue;
            }

            String name = types[i]+"."+props[j].getName();
            if (isLoggingDebug()) {
              logDebug("adding name to keyword properties: " + name);
            }
            names.add(name);
          }
        }
      }
    }
    catch (RepositoryException e) {
      if (isLoggingDebug()) {
        logDebug("keyword search: failure getting property names");
      }
      return null;
    }
    return (String[]) names.toArray(new String[names.size()]);
  }



  /**
   * Used by the advanced searching feature.
   * Returns a list of available search options for pPropertyName.  Will search
   * through all itemTypes and collect all values for properties.  For example,
   * an enumerated property will be defined in the repository item descriptor
   * with a set of values.  This method will get possible values from the
   * property descriptor.  Other properties may need a repository query to
   * obtain those options.  This collection of searchValues can then be
   * displayed in a select box to further refine advanced searching.
   *
   * @param pItemType item of the property to search for possible values
   * @param pPropertyName name of the property to get possible values for
   * @return options or null if no values could be found
   */
  protected Collection generateSearchValues(String pItemType,
      String pPropertyName) {
    try {
      Repository[] reps = getRepositories();
      RepositoryView itemView;
      Set results1 = new HashSet();  // for values that are a single property
      HashMap results2 = new HashMap(); // for item values

      for (int i=0; i<reps.length; i++){
        RepositoryItemDescriptor itemDesc =
          reps[i].getItemDescriptor(pItemType);
        if (itemDesc == null) {
          throw new RepositoryException("Invalid type: "
              +pItemType+" for repository: "+reps[i].getRepositoryName());
        }
        RepositoryPropertyDescriptor pd =
          (RepositoryPropertyDescriptor)itemDesc.getPropertyDescriptor(
              pPropertyName);
        if (pd != null) {
          Class propertyType = pd.getPropertyType();
          Class componentPropType = pd.getComponentPropertyType();
          if (propertyType != null) {

            /* if type = enumerated, get values from property descriptor */
            if (pd.getTypeName().equalsIgnoreCase("enumerated")){
              PropertyEditor pe = pd.createPropertyEditor();
              if (pe != null) {
                Collection subResultSet = Arrays.asList(pe.getTags());
                if (subResultSet != null) {
                  results1.addAll(subResultSet);
                } // end if subresultset != null
              } // end if pe!= null
            } // end if enumerated property type
            /* else if the propertyType is of repository item,
               query the repository for values */
            else if ( RepositoryItem.class.isAssignableFrom(propertyType) ||
                ((componentPropType != null) &&
                    (RepositoryItem.class.isAssignableFrom(componentPropType))) ) {

              if (RepositoryItem.class.isAssignableFrom(propertyType)) {
                itemView = pd.getPropertyItemDescriptor().getRepositoryView();
              }
              else {
                itemView = pd.getComponentItemDescriptor().getRepositoryView();
              }

              QueryBuilder qb = itemView.getQueryBuilder();
              Query q = qb.createUnconstrainedQuery();
              ContextFilteringRules siteFilterRules = generateSiteFilter();
              if (siteFilterRules != null) {
                // add context membership filters to the query
                q = qb.addContextMembershipFilters(q, siteFilterRules);
              }
              RepositoryItem[] items = itemView.executeQuery(q);

              if (isLoggingDebug()) {
                int cnt = (items == null) ? 0 : items.length;
                logDebug("Number of search values for property: "
                    + pPropertyName + " = " + cnt);
              }

              if (items != null){
                for (int j = 0; j < items.length; j++){
                  // TRICKY: problem carried over from DCS - if prop is Item,
                  //  Item must have property with the name = mDisplayName. Else
                  //  if prop was collection of items, return full item
                  if (RepositoryItem.class.isAssignableFrom(propertyType)) {
                    results1.add(items[j].getPropertyValue(getDisplayName()));
                  }
                  else {
                    // need uniqueness of ID
                    results2.put(items[j].getRepositoryId(), items[j]);
                  }
                }
              } // end if items != null
            } // end if propertyType is a repository item
          } // end if propertyType != null
        } // end if pd != null
        else {
          throw new RepositoryException("Invalid property: "+pPropertyName
              +" for type: "+pItemType);
        }
      }
      if (!results1.isEmpty()) {
        return results1;
      }
      else if (!results2.isEmpty()) {
        return results2.values();
      }
    }
    catch (RepositoryException exc) {
      Object[] args = { pPropertyName, pItemType };
      String msg = ResourceUtils.getMsgResource(GENERATE_VALUES_BY_TYPE_FAILED,
          RESOURCE_BUNDLE_NAME, sResourceBundle, args);
      if (isLoggingError()) {
        logError(msg, exc);
      }
      addFormException(new DropletFormException(
          msg, exc, getAbsoluteName() + ".propertyValuesByType",
          GENERATE_VALUES_BY_TYPE_FAILED));
    }
    return null;
  }

  /**
   * Generates the ContextFilteringRules to filter the query based on the sites.
   * Uses the siteIds property as a list of sites. If siteIds is null, siteScope
   * is used. If siteScope is null, null will be returned. If siteScope is
   * "current", null is returned so the current SiteContext will filter the
   * query. If siteScope is "all", the filter will be set to use ALL sites. If
   * siteScope is any, the filter will be set to ANY. If siteScope is "none",
   * the filter will be set to NONE. All other values of siteScope will be used
   * as a ShareableType id. The list of sites sharing that id will be used to
   * filter the query. If no sites are found sharing that ShareableType id with
   * the current site, the query will be filtered to use ANY site. If
   * includeInactiveSites is false, the site list will be filtered to only contain
   * active sites. This filtering will occurr if the sites property is used or
   * if the siteScope is "any" or a shareable id.
   * 
   * @return The site filter
   */
  protected ContextFilteringRules generateSiteFilter() {
    // Use List of Sites
    String[] sites = getSiteIds();
    if (sites != null && sites.length > 0) {
      sites = filterSiteList(sites);
      // Pass in null since a string array is ambiguous to var args.
      vlogDebug("Filtering Query with list of sites: {0}", sites, null);
      return new ContextFilteringRules(
          getContextFilteringRulesForSites(sites));
    }
    // Use Scope
    String scope = getSiteScope();
    if (!StringUtils.isBlank(scope)) {
      if (CURRENT_SITE_SCOPE.equalsIgnoreCase(scope)) {
        // -Current, return null
        vlogDebug("Using siteScope of {0}", CURRENT_SITE_SCOPE);
        return null;
      }
      if (ALL_SITES_SCOPE.equalsIgnoreCase(scope)) {
        // -ALL
        vlogDebug("Using siteScope of {0}", ALL_SITES_SCOPE);
        return new ContextFilteringRules(GlobalContextType.ALL);
      }
      if (ANY_SITE_SCOPE.equalsIgnoreCase(scope)) {
        vlogDebug("Using siteScope of {0}", ANY_SITE_SCOPE);
        if (!isIncludeInactiveSites()) {
          try {
            RepositoryItem[] activeSites = getSiteManager().getActiveSites();
            if (activeSites == null || activeSites.length == 0) {
              vlogDebug("No active sites found");
              return null;
            }
            String[] siteIds = new String[activeSites.length];
            for (int i = 0; i < activeSites.length; i++) {
              siteIds[i] = activeSites[i].getRepositoryId();
            }
            // Also filter disabled if flag is set
            String[] filteredSites = filterSiteList(siteIds);
            return new ContextFilteringRules(
                getContextFilteringRulesForSites(filteredSites));
          }
          catch (RepositoryException e) {
            vlogError(e, "Error getting all active sites.");
          }
        }
        else if (!isIncludeDisabledSites()) {
          try {
            RepositoryItem[] enabledSites = getSiteManager().getEnabledSites();
            if (enabledSites == null || enabledSites.length == 0) {
              vlogDebug("No enabled sites found");
              return null;
            }
            String[] siteIds = new String[enabledSites.length];
            for (int i = 0; i < enabledSites.length; i++) {
              siteIds[i] = enabledSites[i].getRepositoryId();
            }
            String[] filteredSites = filterSiteList(siteIds);
            return new ContextFilteringRules(
                getContextFilteringRulesForSites(filteredSites));
          }
          catch (RepositoryException e) {
            vlogError(e, "Error getting all enabled sites.");
          }
        }
        return new ContextFilteringRules(GlobalContextType.ANY);
      }
      if (NONE_SITE_SCOPE.equalsIgnoreCase(scope)) {
        vlogDebug("Using siteScope of {0}", NONE_SITE_SCOPE);
        return new ContextFilteringRules(GlobalContextType.NONE);
      }
      // -Shareable
      SiteGroupManager siteGroupManager = getSiteGroupManager();
      if (siteGroupManager != null) {
        Collection<String> shareableSites = siteGroupManager
        .getSharingSiteIds(scope);
        if (shareableSites == null || shareableSites.size() == 0) {
          vlogDebug("No Sites sharing {0} were found. Using ANY site.", scope);
          return new ContextFilteringRules(GlobalContextType.ANY);
        }
        String[] shareableSiteArray = shareableSites.toArray(new String[0]);
        shareableSiteArray = filterSiteList(shareableSiteArray);
        vlogDebug("Found sites {0} for shareable {1}", shareableSiteArray,
            scope);
        return new ContextFilteringRules(
            getContextFilteringRulesForSites(shareableSiteArray));
      }
    }

    // Else return null, which defaults to current site
    vlogDebug("No sites or siteScope set.");
    return null;
  }

  /**
   * Filters the site array used in generateSiteFilter(). This method is called
   * when the sites property is set or a ShareableType id is used to get a list
   * of sites. If includeInactiveSites is false, all inactive sites will be removed
   * from the list. Also, if includeInactiveSites is false, this method will be called
   * for a scope of ANY.
   * 
   * @param pSites The array of site ids
   * @return The filtered site id array
   */
  protected String[] filterSiteList(String[] pSites) {
    String[] filteredSites = pSites;
    if (!isIncludeInactiveSites()) {
      vlogDebug("Filtering Site List to only include active sites.");
      filteredSites = getSiteManager().filterInactiveSites(filteredSites);
    }
    if (!isIncludeDisabledSites()) {
      filteredSites = getSiteManager().filterDisabledSites(filteredSites);
    }
    return filteredSites;
  }
  /**
   * Gets a array of ContextFilteringRules for an array of site ids.
   * 
   * @param pSites The array of site ids
   * @return The array of ContextFilteringRules for the given site ids.
   */
  protected ContextFilteringRule[] getContextFilteringRulesForSites(
      String[] pSites) {
    if (pSites == null || pSites.length == 0) {
      return null;
    }
    ContextFilteringRule[] rules = new ContextFilteringRule[pSites.length];
    for (int i = 0; i < pSites.length; i++) {
      String siteId = pSites[i];
      ContextFilteringRule rule = new ContextFilteringRule(
          siteId);
      rules[i] = rule;
    }
    return rules;
  }


  /**
   * Returns true if the named property of the given types corresponds to a
   * multi-value attribute. Multi-value attributes are objects which are arrays
   * (i.e. Object []), instances of Collection or Map.
   * @param pItemType the type of item to search for.
   */
  boolean isMultiValueProperty(Repository pRepository,
      String pItemType,
      String pPropertyName) {
    if (isLoggingDebug()) {
      logDebug("is property '" + pPropertyName + "' of type '" + pItemType
          + "' multi-value?");
    }
    try {
      RepositoryItemDescriptor itemDescriptor =
        pRepository.getItemDescriptor(pItemType);
      DynamicPropertyDescriptor propertyDescriptor =
        itemDescriptor.getPropertyDescriptor(pPropertyName);
      if (propertyDescriptor != null) {
        Class propertyType = propertyDescriptor.getPropertyType();
        if (isLoggingDebug()) {
          logDebug("property='" + propertyDescriptor.getName() +
              "'; class="+propertyType +
              "; Collection? "+(Collection.class.isAssignableFrom(propertyType))+
              "; Array? "+propertyType.isArray() +
              "; Map? "+(Map.class.isAssignableFrom(propertyType)));
        }
        if (propertyType != null) {
          if (Collection.class.isAssignableFrom(propertyType)) {
            return true;
          }
          else if (propertyType.isArray()) {
            return true;
          }
          else if (Map.class.isAssignableFrom(propertyType)) {
            return true;
          }
        }
      }
    }
    catch (RepositoryException exc) {
      if (isLoggingError()) {
        logError(exc);
      }
    }

    return false;
  }


  /**
   * For the supplied item type return a collection of ==sub-result set that
   * will combined into the total result set.  generateResultSet calls
   * generateSearchQuery to build the query string based on options.  The
   * resulting query is executed on the Repository View and resultset returned.
   *
   * @param pItemType the type of item to search for.
   * @return null if no items could be found
   */
  protected Collection generateResultSet(String pItemType) {
    ArrayList results = new ArrayList();
    int endIndex = -1;
    if (getMaxRowCount() != -1) {
      int newEnd = getMaxRowCount() - mSearchResults.size();
      endIndex = (newEnd < 0) ? 0 : newEnd;
    }

    try {
      for (int i=0;i<getRepositories().length;i++) {
        RepositoryView view = getRepositories()[i].getView(pItemType);

        if (view == null ) {
          Object[] args = { pItemType, getRepositories()[i].getRepositoryName() };
          String msg = ResourceUtils.getMsgResource(INVALID_ITEM_TYPE,
              RESOURCE_BUNDLE_NAME, sResourceBundle, args);
          if (isLoggingError()) {
            logError(msg);
          }
          addFormException(new DropletFormException(
              msg, getAbsoluteName() + ".repositories",
              INVALID_ITEM_TYPE));
          return null;
        }

        QueryBuilder qb = view.getQueryBuilder();
        Query q = generateSearchQuery(getRepositories()[i], pItemType, qb);

        if (getAllowRefine()) {
          HashMap prevQueries = (HashMap)mPreviousQuery.get(
              getRepositories()[i].getRepositoryName());
          Query previous = (Query)prevQueries.get(pItemType);

          if (previous != null) {
            if (isLoggingDebug()) {
              logDebug("using previous query as filter to new query");
            }

            if (q != null) {
              Query[] oldNew = {q, previous};
              q = qb.createAndQuery(oldNew);
            }
            else {
              q = previous;
            }
          }

          // set previous to new
          prevQueries.put(pItemType, q);
        }

        if (isLoggingDebug()) {
          logDebug("combined search query=" + q);
        }

        if (q != null) {
          ContextFilteringRules siteFilter = generateSiteFilter();
          // add context membership filters to the query
          if (siteFilter != null) {
            q = view.getQueryBuilder().addContextMembershipFilters(q,
                siteFilter);
          }
          RepositoryItem[] items = view.executeQuery(q, 0, endIndex, null);
          if (items != null) {
            if (endIndex != -1) {
              endIndex -= items.length;
            }
            results.addAll(Arrays.asList(items));
          }
        }
      }
      return results;
    }
    catch (RepositoryException exc) {
      Object[] args = { pItemType };
      String msg = ResourceUtils.getMsgResource(QUERY_FAILURE,
          RESOURCE_BUNDLE_NAME, sResourceBundle, args);
      if (isLoggingError()) {
        logError(msg, exc);
      }
      addFormException(new DropletFormException(
          msg, exc, getAbsoluteName() + ".itemTypes", QUERY_FAILURE));
    }
    catch (DropletFormException dfe) {
      addFormException(dfe);
    }
    return null;
  }


  /**
   * Split property names into type and name from type.name
   */
  protected void splitPropertyNames(String[] pTypes, String[] pSource,
      HashMap pDest, String pSearchType ) {
    for (int i=0; i<pTypes.length;i++) {
      pDest.put(pTypes[i], new ArrayList());
    }
    String [] nameVal;
    ArrayList names;
    for (int i=0; pSource != null && i<pSource.length; i++) {
      nameVal = StringUtils.splitStringAtCharacter(pSource[i], '.');
      if ( (nameVal.length == 2) && pDest.containsKey(nameVal[0]) ) {
        names = (ArrayList)pDest.get(nameVal[0]);
        names.add(nameVal[1]);
      }
      else {
        if (isLoggingError()) {
          Object[] args = { pSearchType, pSource[i] };
          logError(ResourceUtils.getMsgResource(INVALID_SEARCH_PROPERTY_NAMES,
              RESOURCE_BUNDLE_NAME, sResourceBundle, args));
        }
      }
    }
  }


  /**
   * With the supplied keywords perform any modifications that are required to
   * allow it to be used within a keyword query. This implementation checks the
   * <code>toLowerCaseKeywords</code> and <code>toUpperCaseKeywords</code>
   * properties to determine if String.toLowerCase or String.toUpperCase should
   * be invoked.
   * @param pKeywords the words to transform for querying
   */
  protected String [] transformKeywords(String [] pKeywords) {
    if (pKeywords == null) {
      return null;
    }
    else if (isToLowerCaseKeywords()) {
      int length = pKeywords.length;
      String [] keywords = new String[length];
      for (int c=0; c<length; c++) {
        keywords[c] = pKeywords[c].toLowerCase();
      }
      return keywords;
    }
    else if (isToUpperCaseKeywords()) {
      int length = pKeywords.length;
      String [] keywords = new String[length];
      for (int c=0; c<length; c++) {
        keywords[c] = pKeywords[c].toUpperCase();
      }
      return keywords;
    }
    else {
      return pKeywords;
    }
  }


  /**
   * Prepare search properties.
   */
  protected void prepare(DynamoHttpServletRequest request,
      DynamoHttpServletResponse response) {
    if (isPrepared()) {
      return;
    }

    // parse property names
    if (isDoKeywordSearch()) {
      // if keyword props names empty, search all
      if ( (getKeywordSearchPropertyNames() == null) ||
          (getKeywordSearchPropertyNames().length == 0) ) {
        if (isLoggingDebug()) {
          logDebug("Keyword Search will search all String properties");
        }
        setKeywordSearchPropertyNames(getStringPropertyNames());
      }

      if (getKeywordSearchPropertyNamesByItemType().isEmpty()) {
        splitPropertyNames(getItemTypes(), mKeywordSearchPropertyNames,
            mKeywordSearchPropertyNamesByItemType, "keyword");
      }
    }
    if (isDoTextSearch() &&	getTextSearchPropertyNamesByItemType().isEmpty()) {
      splitPropertyNames(getItemTypes(), mTextSearchPropertyNames,
          mTextSearchPropertyNamesByItemType, "text");
    }
    if (isDoAdvancedSearch()
        && getAdvancedSearchPropertyNamesByItemType().isEmpty()) {
      splitPropertyNames(getItemTypes(), mAdvancedSearchPropertyNames,
          mAdvancedSearchPropertyNamesByItemType, "advanced");

      // also prepare ranges and values
      Set namesByType = mAdvancedSearchPropertyNamesByItemType.entrySet();
      for (Iterator types=namesByType.iterator();types.hasNext();) {
        Map.Entry pair = (Map.Entry)types.next();
        String type = (String)pair.getKey();
        ArrayList nameList = (ArrayList)pair.getValue();
        mAdvancedSearchPropertyValues.put(type, new HashMap());
        HashMap ranges = new HashMap();
        mAdvancedSearchPropertyRanges.put(type, ranges);
        for (Iterator names=nameList.iterator();names.hasNext();) {
          ranges.put(names.next(), new HashMap(2));
        }
      }
    }

    setPrepared(true);
  }



  //-------------------------------------
  // Constructors
  //-------------------------------------
  public SearchFormHandler() {
    // bean constructor
  }

  protected String getMessage(String key, Object[] args) {
    return ResourceUtils.getMsgResource(key,
        RESOURCE_BUNDLE_NAME, sResourceBundle, args);
  }
}

