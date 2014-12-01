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

package atg.commerce.catalog;

import atg.core.exception.PropertyNotSetException;
import atg.repository.*;
import atg.repository.rql.RqlQuery;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.droplet.DropletFormException;

import java.util.Arrays;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
import java.io.IOException;
import java.beans.PropertyEditor;

/**
 * <p>
 * This form handler is used to search the catalog repository for products
 * and categories.  The handler can be used to perform four types of
 * searching:  keywords, text, advanced, and hierarchical.  All four types use a boolean
 * to turn that searching feature on and propertyName to specify on which
 * properties to search.  This form handler examines each boolean
 * and if true, appends that subquery to the full query string.  The itemTypes
 * property specify what items in the catalog repository definition to search.
 * For the catalog, these will likely be 'product' or 'category' or both.
 * Configuration files will be used to specify what searching to perform on
 * which property values and item types.
 *
 * <p>
 * <b><i>Keyword</i></b><br>
 * Keyword searching uses keyword property names and an input search string
 * to search product and/or category keywords.  Values entered will be used
 * to build the subQuery for keyword matching.
 * <dl>
 * <dt><b><code>keywordsPropertyNames</code></b>
 * <dd>array of property names to search
 *
 * <dt><b><code>keywords</code></b>
 * <dd>array of keyword strings to search for. If not set, will search list
 * of strings in property <code>searchInput</code>
 *
 * <dt><b><code>searchInput</code></b>
 * <dd>alternate strings to search for. If <code>keywords</code> property
 * is not set, this string will be parsed using the deliminator
 * <code>keywordInputSeparator</code> and the resulting array of strings will
 * be used instead. This property is shared by Text searches.
 *
 * <dt><b><code>keywordInputSeparator</code></b>
 * <dd>a single character that is used as a deliminator to parse
 * <code>searchInput</code>. The default value is a single space.
 *
 * <dt><b><code>toUpperCaseKeywords</code></b>
 * <dd>set this to "true" to force keywords to be converted to upper-case before
 * the query is run.
 *
 * <dt><b><code>toLowerCaseKeywords</code></b>
 * <dd>set this to "true" to force keywords to be converted to lower-case before
 * the query is run.
 * </dl>
 * <p>
 * <b><i>Text</i></b><br>
 * Text searching uses text property names and an input search string to
 * do a text pattern matching on properties.  Values entered will be used
 * to build the subQuery for text searching.
 * <dl>
 *    <dt><b><code>textSearchPropertyNames</code></b>
 *    <dd>list of property names to search. If not specified, the default list
 *    of text properties configured in the repository will be used.
 *
 *    <dt><b><code>searchStringFormat</code></b>
 *    <dd>the format to use i.e. "ORACLE_CONTEXT", "SYBASE_SDS"
 *
 *    <dt><b><code>searchInput</code></b>
 *    <dd>The string to search for. If a repository is configured to use a
 *    full-text search engine, this string can include native search language of
 *    that engine. On the other hand, if a repository is configured to simulate
 *    full-text queries (<code>simulateTextSearchQueries=true</code>), the SQL
 *    LIKE operator will be used to determine whether the target value is a
 *    substring of any of the text properties being searched. This property is
 *    also used for Keyword searches if the <code>keywords</code> property is
 *    not set.
 *
 *    <dt><b><code>minScore</code></b>
 *    <dd>minimum required score that the results must meet or exceed in order
 *    to be returned by the full-text search engine. See vendor docs for more
 *    information on the meaning and use of the score value.
 *
 *    [NOTE: Current repository implementations don't yet support this feature
 *    of full-text searches. It is provided for future use.]
 * </dl>
 * <p>
 * <b><i>Advanced</i></b><br>
 * Advanced searches provide possible search options for each property
 * specified in AdvancedSearchPropertyNames.  For example, enumerated types
 * will be defined in the repository with a set number of values.  Advanced
 * searching will retrieve these values from the definition to display
 * in a select box.  The advanced query will be built from options selected
 * to further refine the catalog search.
 *
 * <p>
 * <b><i>Hierarchical</i></b><br>
 * Hierarchical searches look in a subset of categories, starting from
 * a given category, including also that category's child categories, and
 * also those children's children, i.e., all descendant categories of the
 * original category.  The given category is indicated by the repository
 * ID in the <i>hierarchicalCategoryId</I> property.
 *
 * <p>
 * <b><i>Price</i></b><br>
 * Price searches take a pricePropertyName, which is the name of the property
 * of the product to search for the price on.  This property can be a subreference
 * (i.e., childSKUs.listPrice).  It also takes a priceRelation, which is a comparison
 * operator (i.e., ">", ">=", "=", etc.).  Finally, it takes a price, which must
 * be a valid number.  The search will return the products whose priceProperty contains
 * a value that has the proper relation with the price given.
 *
 * <p>
 * <b><i>Sku</i></b><br>
 * This is an open-ended text search on the childSKUsPropertyName for the "sku" input.
 * All products that contain a sku with an id that contains the given "sku" substring
 * will be returned.
 *
 * @beaninfo
 *   description: A form handler which can be used to perform catalog searches
 *   attribute: componentCategory Searching
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 * @author Bob Mason
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/catalog/SearchFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public
class SearchFormHandler
extends atg.repository.servlet.SearchFormHandler
{
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/catalog/SearchFormHandler.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------

  /** resource bundle name */
  static final String RESOURCE_BUNDLE_NAME = "atg.commerce.catalog.UserResources";

  /** resource bundle */
  static ResourceBundle sResourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  /** droplet exception code: invalid price */
  public static final String INVALID_PRICE = "invalidPrice";

  /** droplet exception code: required property not set */
  public static final String PRICE_PROPERTY_NOT_SET = "pricePropertyNotSet";

  //-------------------------------------
  // Member Variables
  //-------------------------------------

  //-------------------------------------
  // Properties
  //-------------------------------------


  //-------------------------------------
  // property: KeywordSearchPropertyNames
	String[] mDCSKeywordSearchPropertyNames;

 /**
   * Sets property KeywordSearchPropertyNames
   * @param pKeywordSearchPropertyNames The property to store the names of all the
   *  keyword properties.
   * @beaninfo description:  the property to store the names of all the keyword
   *  properties.
   **/
  public void setKeywordsPropertyNames(String [] pKeywordSearchPropertyNames) {
    mDCSKeywordSearchPropertyNames = pKeywordSearchPropertyNames;
  }

  /**
   * Returns property KeywordsPropertyNames which is the set of property names to search
   * @return The value of the property KeywordsPropertyNames.
   **/
  public String [] getKeywordsPropertyNames() {
    return mDCSKeywordSearchPropertyNames;
  }



  //-------------------------------------
  // property: AdvancedSearchPropertNames
	String[] mDCSAdvancedSearchPropertyNames;

  /**
   * Sets property AdvancedSearchPropertyNames
   * @param pAdvancedSearchPropertyNames  the property to store advanced search
   *  property names.
   * @beaninfo description:  the property to store advanced search property
   *  names.
   **/
  public void setAdvancedSearchPropertyNames(
    String [] pAdvancedSearchPropertyNames) {
    mDCSAdvancedSearchPropertyNames = pAdvancedSearchPropertyNames;
  }

  /**
   * Returns property AdvancedSearchPropertyNames which is the set of property
   *  names to search
   * @return The value of the property AdvancedSearchPropertyNames
   **/
  public String [] getAdvancedSearchPropertyNames() {
    return mDCSAdvancedSearchPropertyNames;
  }



  //-------------------------------------
  // property: TextSearchPropertNames
	String[] mDCSTextSearchPropertyNames;

  /**
   * Sets property TextSearchPropertyNames
   * @param pTextSearchPropertyNames  the property to store text search property
   *  names.
   * @beaninfo description:  the property to store text search property names.
   **/
  public void setTextSearchPropertyNames(String [] pTextSearchPropertyNames) {
	  mDCSTextSearchPropertyNames = pTextSearchPropertyNames;
  }

  /**
   * Returns property TextSearchPropertyNames which is the set of property names for text search
   * @return The value of the property TextSearchPropertyNames.
   **/
  public String [] getTextSearchPropertyNames() {
    return mDCSTextSearchPropertyNames;
  }



  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "Catalog Search";

  /**
   * Sets property LoggingIdentifier
   * @param pLoggingIdentifier the property which identifies this object as the Catalog Search.
   * @beaninfo description:  the property which identifies this object as the Catalog Search.
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
  // property: repositoryKeyParamName
  private String mRepositoryKeyParamName = "repositoryKey";

  /**
   * Sets property repositoryKeyParamName
   * @param pRepositoryKeyParamName  the property to store the name of the input parameter for the repository key value.
   * @beaninfo description:  the property to store the name of the input parameter for the repository key value.
   **/
  public void setRepositoryKeyParamName(String pRepositoryKeyParamName) {
    mRepositoryKeyParamName = pRepositoryKeyParamName;
  }

  /**
   * Returns property repositoryKeyParamName
   * @return The value of the property repositoryKeyParamName
   **/
  public String getRepositoryKeyParamName() {
    return mRepositoryKeyParamName;
  }


  //-------------------------------------
  // property: catalogKey
  private String mCatalogKey;

  /**
   * Sets property CatalogKey
   * @param pCatalogKey  the property to store the catalog key used as an index to available catalogs (e.g. multiple languages).
   * @beaninfo description:  the property to store the catalog key used as an index to available catalogs (e.g. multiple languages).
   **/
  public void setCatalogKey(String pCatalogKey) {
    mCatalogKey = pCatalogKey;
  }

  /**
   * Returns property CatalogKey
   * @return The value of the property catalogKey
   **/
  public String getCatalogKey() {
    return mCatalogKey;
  }


  //-------------------------------------
  // property: catalogTools
  CatalogTools mCatalogTools;

  /**
   * Sets property catalogTools.
   * @param pCatalogTools the property to store the global service catalog tools factory to obtain the catalog to search in.
   * @beaninfo description:  the property to store the global service catalog tools factory to obtain the catalog to search in.
   **/
  public void setCatalogTools(CatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  /**
   * Returns property catalogTools.
   * @return The value of the property catalogTools
   **/
   public CatalogTools getCatalogTools() {
       return mCatalogTools;
   }


  //-------------------------------------
  // property: SearchInput
  String mSearchInput = "";

  /**
   * Sets property SearchInput
   * @param pSearchInput the property to store the input search string. Text
   *  search always uses this property and Keyword search will use this (after
   *  parsing using a configurable deliminator) if keyword property is not set.
   * @beaninfo description:  the property to store the input search string to
   *  parse and search.
   **/
  public void setSearchInput(String pSearchInput) {
		mSearchInput = pSearchInput.trim();
    super.setTextInput(mSearchInput);
  }

  /**
   * Returns property SearchInput
   * @return The value of the property SearchInput
   **/
  public String getSearchInput() {
    return mSearchInput;
  }


  /**
   * Returns property Keywords.
   * Overrides parent behavior. Parse searchInput into string array
   * @return The value of the property Keywords which is the target values to
   *  search
   */
  public String[] getKeywords() {
    if ( ((super.getKeywords()==null) || (super.getKeywords().length==0))
         && !StringUtils.isBlank(mSearchInput) )
    {
      return StringUtils.splitStringAtCharacter(mSearchInput,
                                                getKeywordInputSeparator());
    }
    else {
      return super.getKeywords();
    }
  }



  //-------------------------------------
  // property: KeywordInputSeparator
  char mKeywordInputSeparator = ' ';

  /**
   * Sets property KeywordInputSeparator
   * @param pKeywordInputSeparator  the property to store what characters to use
   *  to separate keywords in the SearchInput string.
   * @beaninfo description:  the property to store what characters to use to
   *  separate keywords in the SearchInput string.
   **/
  public void setKeywordInputSeparator(char pKeywordInputSeparator) {
    mKeywordInputSeparator = pKeywordInputSeparator;
  }

  /**
   * Returns property KeywordInputSeparator
   * @return The value of the property KeywordInputSeparator
   **/
  public char getKeywordInputSeparator() {
    return mKeywordInputSeparator;
  }




  //-------------------------------------
  // property: hierarchicalCategoryId

  /**
   * Sets property HierarchicalCategoryId
   * @param pHierarchicalCategoryId the property to store the value of the hierarchical category id to search in.
   * @beaninfo description:  the property to store the value of the hierarchical category id to search in.
   **/
  public void setHierarchicalCategoryId(String pHierarchicalCategoryId)
  {
    setAncestorId(pHierarchicalCategoryId);
  }

  /**
   * Returns property HierarchicalCategoryId
   * @return The value of the property HierarchicalCategoryId which is the
   *  category Id to start the search
   **/
  public String getHierarchicalCategoryId()
  {
    return getAncestorId();
  }


  //-------------------------------------
  // property: ancestorCategoriesPropertyName

  /**
   * Sets property ancestorCategoriesPropertyName
   * @param pAncestorCategoriesPropertyName  the property to store the value of ancestor category property name.
   * @beaninfo description:  the property to store the value of ancestor category property name.
   **/
  public void setAncestorCategoriesPropertyName(String pAncestorCategoriesPropertyName) {
    setAncestorPropertyName(pAncestorCategoriesPropertyName);
  }

  /**
   * Returns property ancestorCategoriesPropertyName
   * @return The value of the property ancestorCategoriesPropertyName which is
   *  the name of ancestor category property
   **/
  public String getAncestorCategoriesPropertyName() {
    return getAncestorPropertyName();
  }




  //-------------------------------------
  // property: PropertyValues (for Advanced searches only)
  HashMap mPropertyValues = new HashMap();

  /**
   * Returns property PropertyValues
   * @return The value of the property PropertyValues which is the key/value
   *  pairs for each property you want to search
   **/
  public HashMap getPropertyValues() {
    return mPropertyValues;
  }



  /**
   * Sets property PropertyValues
   * @param pPropertyValues the property to store selected property values for
   *  advanced searching.
   * @beaninfo description:  the property to store selected property values
   *  for advanced searching.
   **/
  public void setPropertyValues(HashMap pPropertyValues) {
		mPropertyValues = pPropertyValues;
  }


  //-------------------------------------
  // property: PropertyValuesByType (for Advanced searches only)
  HashMap mDCSPropertyValuesByType = new HashMap();

  /**
   * Returns property PropertyValuesByType.
   * Containing one key/value pair for each property named in
   * <code>advancedSearchPropertyValues</code> whose type is either enumerated
   * or RepositoryItem. The key is the name of the property and the value is a
   * Collection of the possible values.
   * @return The value of the property PropertyValuesByType
   * @beaninfo description:  A map of property names to all possible values
   **/
  public HashMap getPropertyValuesByType() {
    if (mDCSPropertyValuesByType.isEmpty()) {
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
    return mDCSPropertyValuesByType;
  }


  /**
   * init with all possible values of each property
   */
  public void setPropertyValuesByType() {
    String[] names = getAdvancedSearchPropertyNames();
    for (int i=0; i<names.length; i++) {
      Collection resultSet = generateSearchValues(names[i]);
      mDCSPropertyValuesByType.put(names[i], resultSet);
    }
  }

  //-------------------------------------
  // property: ChildSKUsPropertyName
  String mChildSKUsPropertyName;

  /**
   * Sets property ChildSKUsPropertyName
   * @param pChildSKUsPropertyName the name of the childSKUs property in the product item descriptor
   * @beaninfo description:  the name of the childSKUs property in the product item descriptor
   **/
  public void setChildSKUsPropertyName(String pChildSKUsPropertyName) {
    mChildSKUsPropertyName = pChildSKUsPropertyName;
  }

  /**
   * Returns property ChildSKUsPropertyName which is the name of the childSKUs property in the product item descriptor
   * @return The name of the childSKUs property in the product item descriptor
   **/
  public String getChildSKUsPropertyName() {
    return mChildSKUsPropertyName;
  }

  //-------------------------------------
  // property: Price
  String mPrice;

  /**
   * Sets property Price
   * @param pPrice the price to search for
   * @beaninfo description:  the price to search for
   **/
  public void setPrice(String pPrice) {
    mPrice = pPrice;
  }

  /**
   * Returns property Price which is the price to search for
   * @return The price to search for
   **/
  public String getPrice() {
    return mPrice;
  }

  //-------------------------------------
  // property: PriceRelation
  String mPriceRelation;

  /**
   * Returns property PriceRelation
   * @return The relation of prices to search for (i.e., ">=", "<=", "=")
   **/
  public String getPriceRelation() {
    return mPriceRelation;
  }

  /**
   * Sets property PriceRelation
   * @param pPriceRelation the relation of prices to search for
   * @beaninfo description:  the relation of prices to search for.
   **/
  public void setPriceRelation(String pPriceRelation) {
    mPriceRelation = pPriceRelation;
  }

  //-------------------------------------
  // property: PricePropertyName
  String mPricePropertyName;

  /**
   * Returns property PricePropertyName
   * @return The name of the price property in the searchable item
   **/
  public String getPricePropertyName() {
    return mPricePropertyName;
  }

  /**
   * Sets property PricePropertyName
   * @param pPricePropertyName the name of the price property in the searchable
   *        item
   * @beaninfo description:  the name of the price property in the searchable
   *           item
   **/
  public void setPricePropertyName(String pPricePropertyName) {
    mPricePropertyName = pPricePropertyName;
  }

  //-------------------------------------
  // property: Sku
  String mSku;

  /**
   * Returns property Sku
   * @return The sku to search for
   **/
  public String getSku() {
    return mSku;
  }

  /**
   * Sets property Sku
   * @param pSku the sku to search for
   * @beaninfo description:  the sku to search for
   **/
  public void setSku(String pSku) {
    mSku = pSku;
  }

  //----------------------------
  // Property: SkuExactMatch
  private boolean mSkuExactMatch = false;

  /**
   * Gets the SkuExactMatch property value.
   * @return The boolean value.
   **/
  public boolean isSkuExactMatch()
  {
    return mSkuExactMatch;
  }
  /**
   * Sets the SkuExactMatch property value.
   * @param pSkuExactMatch The boolean value.
   * @beaninfo description:  the SkuExactMatch to set
   **/
  public void setSkuExactMatch(boolean pSkuExactMatch)
  {
    mSkuExactMatch = pSkuExactMatch;
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
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleSearch(DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    // in case beforeGet() never called
    prepare(pRequest, pResponse);

    // assume that each property exists in all item types
    HashMap target = getAdvancedSearchPropertyValues();
    String[] types = getItemTypes();

    Iterator valSet = getPropertyValues().entrySet().iterator();
    while (valSet.hasNext()) {
      Map.Entry val = (Map.Entry)valSet.next();
      for (int i=0;i<types.length;i++) {
        ((HashMap)target.get(types[i])).put(
          val.getKey(),
          val.getValue());
      }
    }

    return super.handleSearch(pRequest, pResponse);
  }


  /**
   * Called before getX methods on this form are called.
   */
  public void beforeGet(DynamoHttpServletRequest request,
                        DynamoHttpServletResponse response) {
    // do here and in handle() as beforeGet() may never be called
    prepare(request, response);
    super.beforeGet(request, response);
  }




  //-------------------------------------
  // Helper Methods
  //-------------------------------------


  /**
   * convert DCS-stype property names to DAS-style
   */
  private String[] convertPropertyNames(String[] pOldNames) {
    if ( (pOldNames == null) || (pOldNames.length == 0) ) return null;
    String [] types = getItemTypes();
    String[] newNames = new String[ types.length * pOldNames.length ];
    for (int i=0;i<types.length;i++) {
      for (int j=0;j<pOldNames.length;j++) {
        newNames[pOldNames.length*i+j] = types[i] + "." + pOldNames[j];
      }
    }
    return newNames;
  }


  /**
   * Member variable where prepareRepositories stores the last catalog key it saw.
   **/
  
  protected String mLastCatalogKey;
  
  /**
   * Set the catalog key and the repository to search based on the
   * repository key that has been stored in pRequest, but only if
   * the value of the key has changed since the last time
   * prepareRepository was called.
   **/

  protected void prepareRepository(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
  {
    String catalogKey = null;
    
    try {
      catalogKey = getRepositoryKey(pRequest, pResponse);
    }
    catch (IOException e) {
      String propertyPath = getAbsoluteName() + "." + "repositoryKeyParamName";
      addFormException(
          new DropletFormException(
              "IOException caught when getting catalog key", e, propertyPath, "catalogKey"));
    }
    catch (ServletException e) {
      String propertyPath = getAbsoluteName() + "." + "repositoryKeyParamName";
      addFormException(
        new DropletFormException(
            "ServletException caught when getting catalog key", e, propertyPath, "catalogKey"));
    }

    // Figure out whether or not we need to change the repository we
    // are searching.  There are four cases when we need to switch
    // repositories:
    //
    // (1) If the repository has not been set to anything yet.
    //
    // (2) If we are switching from the default repository to an
    //     alternate repository -- i.e., if the last catalog key we
    //     saw was null and the current one is not null.
    //
    // (3) If we are switching from an alternate repository to the
    //     default repository -- i.e., if the last catalog key we
    //     saw was not null and the current one is null.
    //
    // (4) If we are switching between alternate repositories -- i.e.,
    //     neither catalog key is null and they are not equal.
    
    if (getRepositories() == null ||
        (mLastCatalogKey == null && catalogKey != null) ||
        (mLastCatalogKey != null && catalogKey == null) ||
        (mLastCatalogKey != null && mLastCatalogKey.equals(catalogKey) == false))
    {
      if (isLoggingDebug())
        logDebug("Changing catalog key from " + mLastCatalogKey + " to " + catalogKey);

      setCatalogKey(catalogKey);
      Repository [] tmp = new Repository[1];
      tmp[0] = getCatalogTools().findCatalog(catalogKey);
      super.setRepositories(tmp);
      mLastCatalogKey = catalogKey;
    }
    else
    {
      if (isLoggingDebug())
        logDebug("Saw catalog key " + mLastCatalogKey + " again");
    }
  }


  /**
   * Prepare search properties.
   */
  protected void prepare(DynamoHttpServletRequest request,
                         DynamoHttpServletResponse response) {

    // Always check to see if we've switched locales or otherwise changed
    // which repository we are searching.
    
    prepareRepository(request, response);

    // Optimize away the rest of this method if we've been here before, since
    // there's no point parsing and converting all of the search property names
    // more than once.
    
    if (isPrepared()) return;

    // property names
    if (isDoAdvancedSearch()
        && (super.getAdvancedSearchPropertyNames()==null) ) {
      super.setAdvancedSearchPropertyNames(
        convertPropertyNames(mDCSAdvancedSearchPropertyNames));
    }
    if (isDoKeywordSearch()
        && (super.getKeywordSearchPropertyNames()==null) ) {
      super.setKeywordSearchPropertyNames(
        convertPropertyNames(mDCSKeywordSearchPropertyNames));
    }
    if (isDoTextSearch()
        && (super.getTextSearchPropertyNames()==null) ) {
      super.setTextSearchPropertyNames(
        convertPropertyNames(mDCSTextSearchPropertyNames));
    }

    super.prepare(request, response);
  }



  /**
   * Retrieve the key to the catalog repository.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return the key to the catalog repository
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public String getRepositoryKey(DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    Object value = pRequest.getParameter(getRepositoryKeyParamName());

    if ((value != null) && (value instanceof String)){
      return (String) value;
    }
    else {
      return null;
    }
  }



  /**
   * generateSearchValues is used by the advanced searching feature.
   * For each property supplied in the property pSearchType, this
   * method will return a list of available search options for that
   * property.  Will search through all itemTypes and collect all values
   * for properties.  For example, an enumerated property will be defined
   * in the repository item descriptor with a set of values.  This method
   * will get possible values from the property descriptor.  Other properties
   * may need a repository query to obtain those options.  This collection
   * of searchValues can then be displayed in a select box to further
   * refine advanced searching.
   *
   * @param pSearchType the search type. Typically corresponds to an
   * item descriptor name in the catalog repository
   * @return options or null if no values could be found
   */
  protected Collection generateSearchValues(String pSearchType) {
    try {
      Repository catalog = getCatalogTools().findCatalog(getCatalogKey());
      Set resultSet = new HashSet();
      for (int i=0; i<getItemTypes().length; i++){
        RepositoryItemDescriptor itemDesc =
          catalog.getItemDescriptor(getItemTypes()[i]);
        RepositoryPropertyDescriptor pd =
          (RepositoryPropertyDescriptor)itemDesc.getPropertyDescriptor(pSearchType);
        if (pd != null){
          Class propertyType = pd.getPropertyType();
          if (propertyType != null){
            /* if type = enumerated, get list of values from property descriptor */
            if ( pd.getTypeName().equalsIgnoreCase("enumerated")){
              PropertyEditor pe = pd.createPropertyEditor();
              if (pe != null) {
                Collection subResultSet = Arrays.asList(pe.getTags());
                if (subResultSet != null) {
                  resultSet.addAll(subResultSet);
                } // end if subresultset != null
              } // end if pe!= null
            } // end if enumerated property type
            /* else if the propertyType is of repository item,
               query the repository for values */
            else if (RepositoryItem.class.isAssignableFrom(propertyType)){
              RepositoryView itemView = catalog.getView(pSearchType);
              QueryBuilder qb = itemView.getQueryBuilder();
              Query q = qb.createUnconstrainedQuery();
              RepositoryItem [] items = itemView.executeQuery(q);
              if (items != null){
                HashSet hs = new HashSet();
                for (int j = 0; j < items.length; j++){
                  hs.add(items[j].getPropertyValue(getDisplayName()));
                }
                resultSet.addAll(hs);
              } // end if items != null
	    } // end if propertyType is a repository item
	  }// end if propertyType != null
	}// end if pd != null
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
     // First, figure out how many queries we're going to do
     int queryCount = 0;
     boolean doPriceQuery = false;
     boolean doSkuQuery = false;

     if (!StringUtils.isBlank(getPrice()))
     {
       queryCount++;
       doPriceQuery = true;
     }
     if (!StringUtils.isBlank(getSku()))
     {
       queryCount++;
       doSkuQuery = true;
     }


     Query superQuery= super.generateSearchQuery(pRepository, pItemType, pQueryBuilder);
     if(superQuery != null)
       queryCount++;
     
     //build out the anded queries
     Query[] queries = new Query[queryCount];
     queryCount=0;
     
     if(superQuery != null)
       queries[queryCount++] = superQuery;
     
     if (doPriceQuery) {
       try {
         queries[queryCount++] = generatePriceQuery(pRepository, pItemType, pQueryBuilder);
       }
       catch(NumberFormatException nfe) {
         String msg = getMessage(PRICE_PROPERTY_NOT_SET, new Object[]{ getPrice() });
         if (isLoggingError()) logError(msg);
         throw new DropletFormException(msg, getAbsoluteName() + ".generateSearchQuery", INVALID_PRICE);
       }
       catch(PropertyNotSetException pnse) {
         String msg = getMessage(PRICE_PROPERTY_NOT_SET, new Object[]{ pnse.getPropertyName() });
         if (isLoggingError()) logError(msg);
         throw new DropletFormException(msg, getAbsoluteName() + ".generateSearchQuery", PRICE_PROPERTY_NOT_SET);
       }
      }

     if (doSkuQuery) {
       queries[queryCount] = generateSkuQuery(pItemType, pQueryBuilder, getSku().trim());
     }

     if(queries.length == 0)
       return null;
     else if(queries.length == 1)
       return queries[0];
     else
       return pQueryBuilder.createAndQuery(queries);
  }

  /**
   * Returns the repository view for the repository
   * @return repository view 
   */
  public RepositoryView getRepositoryView(Repository pRepository, String pViewName ) 
    throws RepositoryException {
        
     try {
       if ( pRepository == null || StringUtils.isEmpty(pViewName) ) 
         throw new RepositoryException("pRepository was null or no pViewName specified");
       else  
         return pRepository.getView(pViewName);
     }
     catch (RepositoryException exc) {
       if (isLoggingError())
         logError(exc);
     } 
        
     return null;
  }

  /**
   * generate subquery using RQL 
   * @param pRepository
   * @param pItemType
   * @param pQueryBuilder the builder that should be used to construct the query
   * @exception RepositoryException if an error occured while forming the query
   * @exception NumberFormatException if the price cannot be formatted to a double
   * @exception PropertyNotSetException if the pricePropertyName property is not set
   * @return Query instance
   */
  protected Query generatePriceQuery(Repository pRepository, String pItemType, QueryBuilder pQueryBuilder) 
      throws RepositoryException, NumberFormatException, PropertyNotSetException
  {
    RepositoryView view = getRepositoryView(pRepository, pItemType);

    String pricePropertyName = getPricePropertyName();
    if(StringUtils.isEmpty(pricePropertyName)) {
      throw new PropertyNotSetException("pricePropertyName");
    }

    String[] propertyName = StringUtils.splitStringAtCharacter( getPricePropertyName(), '.' );

    // Convert price to double
    Double price = Double.valueOf(getPrice());

    String rqlStatement = null;
    if (propertyName.length == 1) {
    rqlStatement = propertyName[0] + getPriceRelation() + price.toString();
    } else {
      rqlStatement = propertyName[0] + " includes item ( " + propertyName[1] +
                     getPriceRelation() + price.toString() + ")";
    }

    if (isLoggingDebug())
      logDebug("rqlStatement : '" + rqlStatement +   "'; "  );

    //" childSKUs includes item ( listPrice <= 1000 ) ";
    RqlStatement statement = RqlStatement.parseRqlStatement(rqlStatement);
    RqlQuery rqlQuery = statement.getQuery();
    return rqlQuery.createQuery(pQueryBuilder, view, null);
  } 
  
  /**
   * Generates a search for child skus of the product
   * @param pItemType the type of item to search for.
   * @param pQueryBuilder the builder that should be used to construct the query
   * @param pInput the search string to use for the full text search
   * @exception RepositoryException if an error occured while forming the query
   */
  protected Query generateSkuQuery(String pItemType,
                                   QueryBuilder pQueryBuilder,
                                   String pInput)
    throws RepositoryException
  {
    if (!getAllowEmptySearch()) {
      if (StringUtils.isBlank(pInput))
        return null;
    }

    if ( getAllowWildcards() && !isSkuExactMatch() && (pInput.indexOf('*') > -1) ) {
      pInput = StringUtils.replace(pInput, "*", "%");
    }

    QueryExpression searchString =
      pQueryBuilder.createConstantQueryExpression(pInput);
    QueryExpression property =
      pQueryBuilder.createPropertyQueryExpression(getChildSKUsPropertyName());

    Query q = null;

    if(isSkuExactMatch())
    {
      q = pQueryBuilder.createComparisonQuery(searchString, property, QueryBuilder.EQUALS);
    }
    else
    {
      QueryExpression minScore =
        pQueryBuilder.createConstantQueryExpression(getMinScore());

      QueryExpression searchStringFormat = null;
      if (getSearchStringFormat() != null) {
        searchStringFormat = pQueryBuilder.createConstantQueryExpression(
          getSearchStringFormat());
      }

      q = pQueryBuilder.createTextSearchQuery(property, searchString, searchStringFormat, minScore);
    }
    return q;
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
   * Extends the base behavior to check the sku and price search values. 
   */
  protected boolean areSearchValuesEmpty() 
  {
    if (!StringUtils.isBlank(getSku())) 
      return false;
    if (!StringUtils.isBlank(getPrice())) 
      return false;
    
    return super.areSearchValuesEmpty();
  }

  protected String getMessage(String key, Object[] args) {
    String msg = ResourceUtils.getMsgResource(key,
        RESOURCE_BUNDLE_NAME, sResourceBundle, args);
    return msg;
  }
} // end of class



