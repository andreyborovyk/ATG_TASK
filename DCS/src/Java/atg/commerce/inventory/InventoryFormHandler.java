/*<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
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

package atg.commerce.inventory;

// atg classes
import atg.servlet.*;
import atg.commerce.fulfillment.*;
import atg.core.util.ResourceUtils;
import atg.droplet.*;
import atg.core.util.DateDoodads;
import atg.repository.*;

// java classes
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;
import java.text.MessageFormat;

/**
 * This form handler can be used for three things:
 * <ul>
 *   <li>Display a subset of inventory items</li>
 *   <li>Change a property value for an inventory item</li>
 *   <li>Tell the InventoryManager that the inventory has been updated.</li>
 * </ul>
 *
 * It contains properties (both arrays of strings) for items
 * that were backordered but are now in stock, and for items that were preordered
 * but are now in stock.  The form handler, through the handleUpdateInventory
 * method, will call the InventoryManager's inventoryWasUpdated method.
 *
 * @see InventoryManager#inventoryWasUpdated
 *
 * @beaninfo
 *   description: Used to display inventory, update inventory, or notify DCS of updates.
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory Inventory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 * @author Sam Perman
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/inventory/InventoryFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class InventoryFormHandler extends GenericFormHandler
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/inventory/InventoryFormHandler.java#2 $$Change: 651448 $";
  static final String MY_RESOURCE_NAME="atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  // level constants
  static final String STOCK_LEVEL = "stockLevel";
  static final String BACKORDER_LEVEL = "backorderLevel";
  static final String PREORDER_LEVEL = "preorderLevel";
  static final String STOCK_THRESHOLD = "stockThreshold";
  static final String BACKORDER_THRESHOLD = "backorderThreshold";
  static final String PREORDER_THRESHOLD = "preorderThreshold";
  static final String AVAILABILITY_STATUS = "availabilityStatus";
  static final String AVAILABILITY_DATE = "availabilityDate";

  // setTypes
  static final String SET = "set";
  static final String INCREASE = "increase";
  static final String DECREASE = "decrease";

  public static final String SKUVIEW = "sku";
  public static final String DEFAULT_PROPERTY_NAME = "displayName";

  //-------------------------------------
  // Properties
  //-------------------------------------

  // properties for handleChangeInventory

  //---------------------------------------------------------------------------
  // property:SKU
  //---------------------------------------------------------------------------

  private String mSKU;
  public void setSKU(String pSKU) {
    mSKU = pSKU;
  }

  /**
   * The id of the sku whose inventory is changing
   **/
  public String getSKU() {
    return mSKU;
  }


  //---------------------------------------------------------------------------
  // property:ChangedProperty
  //---------------------------------------------------------------------------

  private String mChangedProperty;
  public void setChangedProperty(String pChangedProperty) {
    mChangedProperty = pChangedProperty;
  }

  /**
   * The name of the property that is changing for <code>SKU</code>.
   * Valid choices are:
   * <ul>
   *   <li>stockLevel</li>
   *   <li>preorderLevel</li>
   *   <li>backorderLevel</li>
   *   <li>stockThreshold</li>
   *   <li>preorderThreshold</li>
   *   <li>backorderThreshold</li>
   *   <li>availabilityStatus</li>
   *   <li>availabilityDate</li>
   * </ul>
   *
   * If a threshold property is chosen, only a set
   * operation is valid.  If the property is availabilityDate
   * or availabilityStatus, only a set operation is valid.
   **/
  public String getChangedProperty() {
    return mChangedProperty;
  }

  //---------------------------------------------------------------------------
  // property:NewValue
  //---------------------------------------------------------------------------

  private String mNewValue;
  public void setNewValue(String pNewValue) {
    mNewValue = pNewValue;
  }

  /**
   * The value to set <code>changedProperty</code> for <code>SKU</code> to
   **/
  public String getNewValue() {
    return mNewValue;
  }

  //---------------------------------------------------------------------------
  // property:SetType
  //---------------------------------------------------------------------------

  private String mSetType;
  public void setSetType(String pSetType) {
    mSetType = pSetType;
  }

  /**
   * The kind of change to make.  Valid values for this parameter
   * are "set", "increase", and "decrease".  "set" sets the given
   * level to the given value.  "increase" increases the current
   * value of the given level by the given value.  "decrease"
   * decreases the current value of the given level by the given
   * value.
   **/
  public String getSetType() {
    return mSetType;
  }

  // properties for handleUpdateInventory

  //---------------------------------------------------------------------------
  // property:UpdatedItemIdString
  //---------------------------------------------------------------------------

  private String mUpdatedItemIdString;
  public void setUpdatedItemIdString(String pUpdatedItemIdString) {
    mUpdatedItemIdString = pUpdatedItemIdString;
    mUpdatedItemIdList = stringToList(mUpdatedItemIdString);
  }

  /**
   * A space delimited string of sku ids
   **/
  public String getUpdatedItemIdString() {
    return mUpdatedItemIdString;
  }

  //---------------------------------------------------------------------------
  // property:UpdatedItemIdList
  //---------------------------------------------------------------------------

  private List mUpdatedItemIdList;
  public void setUpdatedItemIdList(List pUpdatedItemIdList) {
    mUpdatedItemIdList = pUpdatedItemIdList;
    mUpdatedItemIdString = listToString(mUpdatedItemIdList);
  }

  /**
   * A list of preordered ids.
   **/
  public List getUpdatedItemIdList() {
    return mUpdatedItemIdList;
  }

  // these properties are used when displaying item information

  //---------------------------------------------------------------------------
  // property:LowerBound
  //---------------------------------------------------------------------------

  private String mLowerBound;
  public void setLowerBound(String pLowerBound) {
    mLowerBound = pLowerBound;
    mRefreshCatalogRefIds = true;
  }

  /**
   * The smallest value for <code>propertyName</code> contained in
   * <code>catalogRefIds</code>
   **/
  public String getLowerBound() {
    return mLowerBound;
  }


  //---------------------------------------------------------------------------
  // property:UpperBound
  //---------------------------------------------------------------------------

  private String mUpperBound;
  public void setUpperBound(String pUpperBound) {
    mUpperBound = pUpperBound;
    mRefreshCatalogRefIds = true;
  }

  /**
   * The largest value for <code>propertyName</code> contained in
   * <code>catalogRefIds</code>
   **/
  public String getUpperBound() {
    return mUpperBound;
  }


  //---------------------------------------------------------------------------
  // property:BatchNumber
  //---------------------------------------------------------------------------

  private int mBatchNumber;
  public void setBatchNumber(int pBatchNumber) {
    mBatchNumber = pBatchNumber;
    mRefreshCatalogRefIds = true;
  }

  /**
   * The subset of ids in <code>catalogRefIds</code>
   *
   * @see #getCatalogRefIds
   **/
  public int getBatchNumber() {
    return mBatchNumber;
  }


  //---------------------------------------------------------------------------
  // property:NextBatchNumber
  //---------------------------------------------------------------------------

  /**
   * One more than <code>batchNumber</code>
   **/
  public int getNextBatchNumber() {
    return mBatchNumber+1;
  }


  //---------------------------------------------------------------------------
  // property:PreviousBatchNumber
  //---------------------------------------------------------------------------

  /**
   * One less than <code>batchNumber</code>
   **/
  public int getPreviousBatchNumber() {
    if(mBatchNumber == 0)
      return 0;
    else
      return mBatchNumber-1;
  }


  //---------------------------------------------------------------------------
  // property:BatchSize
  //---------------------------------------------------------------------------

  private int mBatchSize;
  public void setBatchSize(int pBatchSize) {
    mBatchSize = pBatchSize;
    mRefreshCatalogRefIds = true;
  }

  /**
   * <code>catalogRefIds</code> will contain this many ids.
   **/
  public int getBatchSize() {
    return mBatchSize;
  }


  //---------------------------------------------------------------------------
  // property:CatalogRepository
  //---------------------------------------------------------------------------

  private Repository mCatalogRepository;
  public void setCatalogRepository(Repository pCatalogRepository) {
    mCatalogRepository = pCatalogRepository;
    mRefreshCatalogRefIds = true;
  }

  /**
   * This is the repository where product catalog items are stored.
   **/
  public Repository getCatalogRepository() {
    return mCatalogRepository;
  }


  //---------------------------------------------------------------------------
  // property:PropertyName
  //---------------------------------------------------------------------------

  private String mPropertyName = DEFAULT_PROPERTY_NAME;
  public void setPropertyName(String pPropertyName) {
    mPropertyName = pPropertyName;
    mRefreshCatalogRefIds = true;
  }

  /**
   * This is the property of each catalog item in
   * <code>catalogRepository</code> that is bounded by
   * <code>lowerBound</code> and <code>upperBound</code>.
   **/
  public String getPropertyName() {
    return mPropertyName;
  }



  //---------------------------------------------------------------------------
  // property:CatalogRefIds
  //---------------------------------------------------------------------------
  private boolean mRefreshCatalogRefIds = true;
  private String[] mCatalogRefIds;
  public void setCatalogRefIds(String[] pCatalogRefIds) {
    mCatalogRefIds = pCatalogRefIds;
    mRefreshCatalogRefIds = false;
  }

  /**
   * This is an array of catalog ref ids returned from the
   * <code>catalogRepository</code>.
   *
   * It contains all catalog ref ids that are lower than
   * <code>upperBound</code> and higher than <code>lowerBound</code>.
   * The array will contain <code>batchSize</code> elements.  If
   * <code>batchNumber</code> is 1, then the <code>batchNumber</code>
   * + 1 through <code>batchNumber</code> * 2 elements will be
   * returned, and so on.
   **/
  public String[] getCatalogRefIds() {
    if(mRefreshCatalogRefIds) {
      try {
        mCatalogRefIds = makeQuery();
        mRefreshCatalogRefIds = false;
      }
      catch(Exception e) {
        mErrorMessage = e.getMessage();
      }
    }
    return mCatalogRefIds;
  }

  //---------------------------------------------------------------------------
  // property:InventoryManager
  //---------------------------------------------------------------------------

  private InventoryManager mInventoryManager;
  public void setInventoryManager(InventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }

  /**
   * The inventory manager that will be notified of update inventory
   **/
  public InventoryManager getInventoryManager() {
    return mInventoryManager;
  }


  //---------------------------------------------------------------------------
  // property:ErrorMessage
  //---------------------------------------------------------------------------

  private String mErrorMessage;
  public void setErrorMessage(String pErrorMessage) {
    mErrorMessage = pErrorMessage;
  }

  /**
   * If there is an error, this will contain its description.  If
   * there is no error to report, this value is null.
   **/
  public String getErrorMessage() {
    return mErrorMessage;
  }

  //-------------------------------------
  // Methods
  //-------------------------------------

  /**
   * This method will update the <code>changedProperty</code> of the inventory
   * item with catalogRefId of <code>SKU</code> by <code>newValue</code>.
   * The update is according to <code>setType</code> as follows:
   * <ul>
   *   <li> set - set the given property to the given value.</li>
   *   <li> increase - increases the current value of the given property
   *                    by the given value.</li>
   *   <li> decrease - decreases the current value of the given property
   *                   by the given value.</li>
   * </ul>
   **/
  public boolean handleChangeInventory(DynamoHttpServletRequest pRequest,
                                       DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    if(isLoggingDebug())
      logDebug("handling ChangeInventory");

    if (! checkFormRedirect (null, null, pRequest, pResponse)) {
      if(isLoggingDebug())
        logDebug("checkFormRedirect returned false.");
      return false;
    }

    String skuId = getSKU();
    String property = getChangedProperty();
    String value = getNewValue();
    String setType = getSetType();
    InventoryManager im = getInventoryManager();
    int returnStatus = 0;

    try {
      if(property.equals(AVAILABILITY_STATUS)) {
        int status = Integer.parseInt(value);
        returnStatus = im.setAvailabilityStatus(skuId, status);
      }
      else if(property.equals(AVAILABILITY_DATE)) {
        Date date = DateDoodads.stringDate(value);
        returnStatus = im.setAvailabilityDate(skuId, date);
      }
      else {
        long quantity = Long.parseLong(value);
        if(property.equals(STOCK_LEVEL)) {
          if(setType.equals(SET)) {
            returnStatus = im.setStockLevel(skuId, quantity);
          }
          else if(setType.equals(INCREASE)) {
            returnStatus = im.increaseStockLevel(skuId, quantity);
          }
          else if(setType.equals(DECREASE)) {
            returnStatus = im.decreaseStockLevel(skuId, quantity);
          }
        }
        else if(property.equals(BACKORDER_LEVEL)) {
          if(setType.equals(SET)) {
            returnStatus = im.setBackorderLevel(skuId, quantity);
          }
          else if(setType.equals(INCREASE)) {
            returnStatus = im.increaseBackorderLevel(skuId, quantity);
          }
          else if(setType.equals(DECREASE)) {
            returnStatus = im.decreaseBackorderLevel(skuId, quantity);
          }
        }
        else if(property.equals(PREORDER_LEVEL)) {
          if(setType.equals(SET)) {
            returnStatus = im.setPreorderLevel(skuId, quantity);
          }
          else if(setType.equals(INCREASE)) {
            returnStatus = im.increasePreorderLevel(skuId, quantity);
          }
          else if(setType.equals(DECREASE)) {
            returnStatus = im.decreasePreorderLevel(skuId, quantity);
          }
        }
        else if(property.equals(STOCK_THRESHOLD)) {
          returnStatus = im.setStockThreshold(skuId, quantity);
        }
        else if(property.equals(BACKORDER_THRESHOLD)) {
          returnStatus = im.setBackorderThreshold(skuId, quantity);
        }
        else if(property.equals(PREORDER_THRESHOLD)) {
          returnStatus = im.setPreorderThreshold(skuId, quantity);
        }
      }
      if(returnStatus < 0) {
        String msg = null;
        if(returnStatus == InventoryManager.INVENTORY_STATUS_FAIL) {
          msg = MessageFormat.format(Constants.INVENTORY_STATUS_FAIL, skuId);
        }
        else if(returnStatus == InventoryManager.INVENTORY_STATUS_INSUFFICIENT_SUPPLY) {
          msg = MessageFormat.format(Constants.INVENTORY_INSUFFICIENT_SUPPLY, skuId);
        }
        else if(returnStatus == InventoryManager.INVENTORY_STATUS_ITEM_NOT_FOUND) {
          msg = MessageFormat.format(Constants.NO_SUCH_ITEM, skuId);
        }
        if(isLoggingError())
          logError(msg);
        mErrorMessage = msg;
        return true;
      }
      else
        mErrorMessage = null;
    }
    catch(NumberFormatException nfe) {
      String msg = MessageFormat.format(Constants.INVALID_INVENTORY_VALUE, value, property);
      if(isLoggingError())
        logError(msg);
      mErrorMessage = msg;
      return true;
    }
    catch(IllegalArgumentException iae) {
      String msg = MessageFormat.format(Constants.INVALID_INVENTORY_VALUE, value, property);
      // show how to format a date
      if(property.equals(AVAILABILITY_DATE))
        msg = msg + " " + Constants.EXAMPLE_DATE;
      if(isLoggingError())
        logError(msg);
      mErrorMessage = msg;
      return true;
    }
    catch(InventoryException ie) {
      if(isLoggingError())
        logError(ie);
      mErrorMessage = ie.getMessage();
      return true;
    }
    return true;
  }

  /**
   * This handle method will call <code>inventoryManager.inventoryWasUpdated</code>
   * with the ids contained in <code>updatedItemIdList</code>
   *
   * @see InventoryManager#inventoryWasUpdated
   **/
  public boolean handleUpdateInventory(DynamoHttpServletRequest pRequest,
                                       DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    if(isLoggingDebug())
      logDebug("handling UpdateInventory");

    if (! checkFormRedirect (null, null, pRequest, pResponse)) {
      if(isLoggingDebug())
        logDebug("checkFormRedirect returned false.");
      return false;
    }

    InventoryManager im = getInventoryManager();
    if(im == null) {
      if(isLoggingError())
        logError(Constants.INVENTORY_MANAGER_NULL);
      mErrorMessage = Constants.INVENTORY_MANAGER_NULL;
      return true;
    }

    try {
      if(isLoggingDebug())
        logDebug("Calling inventory manager.");
      im.inventoryWasUpdated(getUpdatedItemIdList());
    } catch(Exception e) {
      mErrorMessage = e.getMessage();
      String errorCode = "cantUpdateInventory";
      addFormException(new DropletException(Constants.CANT_UPDATE_INVENTORY, e, errorCode));
    }

    return true;
  }

  //-------------------------------------
  // take a delimited string and return a list of its tokens.
  private List stringToList(String mStr)
  {
    StringTokenizer st = new StringTokenizer(mStr);
    List l = new ArrayList();
    while(st.hasMoreTokens()) {
      l.add(st.nextToken());
    }
    return l;
  }

  // take a list of strings and return a space delimited string
  private String listToString(List pList)
  {
    StringBuffer sb = new StringBuffer();
    Iterator i = pList.iterator();
    while(i.hasNext()) {
      sb.append((String)i.next());
      sb.append(" ");
    }
    return sb.toString();
  }

  /**
   * This private method is used to find catalog ref ids from the product
   * catalog.  It returns an array of all catalog ref ids that are lower
   * than <code>upperBound</code> and higher than <code>lowerBound</code>.
   * The array will contain <code>batchSize</code> elements.  If <code>batchNumber</code>
   * is 1, then the <code>batchNumber</code> + 1 through <code>batchNumber</code> * 2 elements
   * will be returned, and so on.
   **/
  private String[] makeQuery()
    throws Exception
  {
    List returnList = new ArrayList();
    Repository inventoryRepository = getCatalogRepository();

    if(inventoryRepository == null)
      return null;

    int startIndex = mBatchNumber * getBatchSize();
    int endIndex = startIndex + getBatchSize();

    RepositoryView skuView = inventoryRepository.getView(SKUVIEW);

    QueryBuilder qb = skuView.getQueryBuilder();

    Query query = null;

    QueryExpression propertyQuery = qb.createPropertyQueryExpression(getPropertyName());
    QueryExpression lowerBoundQuery = null;
    QueryExpression upperBoundQuery = null;
    Query lowerQuery = null;
    Query upperQuery = null;

    if((mLowerBound != null) &&
       !(mLowerBound.equals(""))) {
      lowerBoundQuery = qb.createConstantQueryExpression(mLowerBound);
      lowerQuery = qb.createComparisonQuery(propertyQuery, lowerBoundQuery, QueryBuilder.GREATER_THAN_OR_EQUALS);
    }
    if((mUpperBound != null) &&
       !(mUpperBound.equals(""))) {
      upperBoundQuery = qb.createConstantQueryExpression(mUpperBound);
      upperQuery = qb.createComparisonQuery(propertyQuery, upperBoundQuery, QueryBuilder.LESS_THAN_OR_EQUALS);
    }

    if((lowerQuery != null) &&
       (upperQuery != null)) {
      Query[] andedQueries = new Query[2];
      andedQueries[0] = lowerQuery;
      andedQueries[1] = upperQuery;
      query = qb.createAndQuery(andedQueries);
    }
    else if(upperQuery != null) {
      query = upperQuery;
    }
    else if(lowerQuery != null) {
      query = lowerQuery;
    }
    else {
      query = qb.createUnconstrainedQuery();
    }

    // sort by property.
    String orderbyProperty = getPropertyName();
    SortDirective sd = new SortDirective(orderbyProperty, SortDirective.DIR_ASCENDING);
    SortDirectives sds = new SortDirectives();
    sds.addDirective(sd);

    RepositoryItem[] items = skuView.executeQuery(query, startIndex, endIndex, sds);

    if (items == null || items.length == 0)
      return null;

    RepositoryItem currentSku = null;

    String skuId = null;

    for (int i=0; i < items.length; i++) {
      currentSku = items[i];
      returnList.add(currentSku.getRepositoryId());
    }

    return (String [])returnList.toArray(new String[returnList.size()]);
  }
} // end of class
