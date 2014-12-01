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

package atg.commerce.inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import atg.commerce.fulfillment.UpdateInventory;
import atg.commerce.fulfillment.UpdateInventoryImpl;
import atg.commerce.messaging.SourceSinkTemplate;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.service.idgen.IdGenerator;
import atg.service.idgen.IdGeneratorException;
import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;

/**
 * <p> This is a repository based implementation of InventoryManager.  It
 * implements all the methods defined by the InventoryManager API. It
 * is a thin wrapper around a repository that contains the inventory
 * information.  This allows a maximum amount of flexibility for
 * potential third party integrators.  Integrators can simply
 * implement a repository containing the required properties for
 * cooperation with the RepositoryInventoryManager. The Repository
 * InventoryManager can then be configured to extract inventory
 * manager information from the third party repository.
 *
 * <p> This class also is a message source.  It can send UpdateInventory
 * messages if there is new inventory available for a previously unavailable
 * item.
 *
 * @beaninfo
 *   description: An InventoryManager implementation that uses a repository instance to access
 *                inventory data
 *   attribute: functionalComponentCategory Inventory
 *   attribute: featureComponentCategory
 *
 * @author Graham Mather
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/inventory/RepositoryInventoryManager.java#2 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $*/
public class RepositoryInventoryManager
extends SourceSinkTemplate implements InventoryManager
{

  //-------------------------------------
  // Class version string

  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/inventory/RepositoryInventoryManager.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME="atg.commerce.inventory.Resources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  //-------------------------------------
  // Member Variables
  //-------------------------------------
  private SkuLinkComparator mSkuLinkComparator = new SkuLinkComparator();

  //-------------------------------------
  // Constants
  static final String DEFAULT_INVENTORY_NAME = "RepositoryInventoryManager";

  static String DEFAULT_INVENTORY_ROW_LOCK_SQL = "UPDATE dcs_inventory SET inventory_lock = ? WHERE catalog_ref_id = ?";
  static String DEFAULT_LOCK_NAME = "inventoryManager";

  static final String DEFAULT_STOCK_LEVEL_PROPERTY_NAME = "stockLevel";
  static final String DEFAULT_BACKORDER_LEVEL_PROPERTY_NAME = "backorderLevel";
  static final String DEFAULT_PREORDER_LEVEL_PROPERTY_NAME = "preorderLevel";

  static final String DEFAULT_STOCK_THRESHOLD_PROPERTY_NAME = "stockThreshold";
  static final String DEFAULT_BACKORDER_THRESHOLD_PROPERTY_NAME = "backorderThreshold";
  static final String DEFAULT_PREORDER_THRESHOLD_PROPERTY_NAME = "preorderThreshold";

  static final String DEFAULT_AVAILABILITY_STATUS_PROPERTY_NAME = "availabilityStatus";
  static final String DEFAULT_AVAILABILITY_DATE_PROPERTY_NAME = "availabilityDate";

  static final String DEFAULT_BUNDLE_LINKS_PROPERTY_NAME = "bundleLinks";

  static final String DEFAULT_CATALOG_REF_ID_PROPERTY_NAME = "catalogRefId";

  static final String DEFAULT_SKU_LINK_BACKORDERED_PROPERTY_NAME = "backordered";
  static final String DEFAULT_SKU_LINK_PREORDERED_PROPERTY_NAME = "preordered";

  static final String DEFAULT_SKU_LINK_ITEM_PROPERTY_NAME = "item";
  static final String DEFAULT_SKU_LINK_QUANTITY_PROPERTY_NAME = "quantity";

  static final long DEFAULT_STOCK_LEVEL = -1;
  static final long DEFAULT_BACKORDER_LEVEL = 0;
  static final long DEFAULT_PREORDER_LEVEL = 0;
  //-------------------------------------
  // Properties

  //-------------------------------------
  // property: StockLevelPropertyName
  String mStockLevelPropertyName = DEFAULT_STOCK_LEVEL_PROPERTY_NAME;

  /**
   * Sets property StockLevelPropertyName
   **/
  public void setStockLevelPropertyName(String pStockLevelPropertyName) {
    mStockLevelPropertyName = pStockLevelPropertyName;
  }

  /**
   * Returns property StockLevelPropertyName
   **/
  public String getStockLevelPropertyName() {
    return mStockLevelPropertyName;
  }


  //-------------------------------------
  // property: BackorderLevelPropertyName
  String mBackorderLevelPropertyName = DEFAULT_BACKORDER_LEVEL_PROPERTY_NAME;

  /**
   * Sets property BackorderLevelPropertyName
   **/
  public void setBackorderLevelPropertyName(String pBackorderLevelPropertyName) {
    mBackorderLevelPropertyName = pBackorderLevelPropertyName;
  }

  /**
   * Returns property BackorderLevelPropertyName
   **/
  public String getBackorderLevelPropertyName() {
    return mBackorderLevelPropertyName;
  }

  //-------------------------------------
  // property: PreorderLevelPropertyName
  String mPreorderLevelPropertyName = DEFAULT_PREORDER_LEVEL_PROPERTY_NAME;

  /**
   * Sets property PreorderLevelPropertyName
   **/
  public void setPreorderLevelPropertyName(String pPreorderLevelPropertyName) {
    mPreorderLevelPropertyName = pPreorderLevelPropertyName;
  }

  /**
   * Returns property PreorderLevelPropertyName
   **/
  public String getPreorderLevelPropertyName() {
    return mPreorderLevelPropertyName;
  }

  //-------------------------------------
  // property: StockThresholdPropertyName
  String mStockThresholdPropertyName = DEFAULT_STOCK_THRESHOLD_PROPERTY_NAME;

  /**
   * Sets property StockThresholdPropertyName
   **/
  public void setStockThresholdPropertyName(String pStockThresholdPropertyName) {
    mStockThresholdPropertyName = pStockThresholdPropertyName;
  }

  /**
   * Returns property StockThresholdPropertyName
   **/
  public String getStockThresholdPropertyName() {
    return mStockThresholdPropertyName;
  }

  //-------------------------------------
  // property: BackorderThresholdPropertyName
  String mBackorderThresholdPropertyName = DEFAULT_BACKORDER_THRESHOLD_PROPERTY_NAME;

  /**
   * Sets property BackorderThresholdPropertyName
   **/
  public void setBackorderThresholdPropertyName(String pBackorderThresholdPropertyName) {
    mBackorderThresholdPropertyName = pBackorderThresholdPropertyName;
  }

  /**
   * Returns property BackorderThresholdPropertyName
   **/
  public String getBackorderThresholdPropertyName() {
    return mBackorderThresholdPropertyName;
  }

  //-------------------------------------
  // property: PreorderThresholdPropertyName
  String mPreorderThresholdPropertyName = DEFAULT_PREORDER_THRESHOLD_PROPERTY_NAME;

  /**
   * Sets property PreorderThresholdPropertyName
   **/
  public void setPreorderThresholdPropertyName(String pPreorderThresholdPropertyName) {
    mPreorderThresholdPropertyName = pPreorderThresholdPropertyName;
  }

  /**
   * Returns property PreorderThresholdPropertyName
   **/
  public String getPreorderThresholdPropertyName() {
    return mPreorderThresholdPropertyName;
  }

  //-------------------------------------
  // property: AvailabilityStatusPropertyName
  String mAvailabilityStatusPropertyName = DEFAULT_AVAILABILITY_STATUS_PROPERTY_NAME;

  /**
   * Sets property AvailabilityStatusPropertyName
   **/
  public void setAvailabilityStatusPropertyName(String pAvailabilityStatusPropertyName) {
    mAvailabilityStatusPropertyName = pAvailabilityStatusPropertyName;
  }

  /**
   * Returns property AvailabilityStatusPropertyName
   **/
  public String getAvailabilityStatusPropertyName() {
    return mAvailabilityStatusPropertyName;
  }

  //-------------------------------------
  // property: AvailabilityDatePropertyName
  String mAvailabilityDatePropertyName = DEFAULT_AVAILABILITY_DATE_PROPERTY_NAME;

  /**
   * Sets property AvailabilityDatePropertyName
   **/
  public void setAvailabilityDatePropertyName(String pAvailabilityDatePropertyName) {
    mAvailabilityDatePropertyName = pAvailabilityDatePropertyName;
  }

  /**
   * Returns property AvailabilityDatePropertyName
   **/
  public String getAvailabilityDatePropertyName() {
    return mAvailabilityDatePropertyName;
  }

  //-------------------------------------
  // property: CatalogRefIdPropertyName
  String mCatalogRefIdPropertyName;

  /**
   * Sets property CatalogRefIdPropertyName
   **/
  public void setCatalogRefIdPropertyName(String pCatalogRefIdPropertyName) {
    mCatalogRefIdPropertyName = pCatalogRefIdPropertyName;
  }

  /**
   * Returns property CatalogRefIdPropertyName
   **/
  public String getCatalogRefIdPropertyName() {
    return mCatalogRefIdPropertyName;
  }

  //---------------------------------------------------------------------------
  // property:BundleLinksPropertyName
  //---------------------------------------------------------------------------

  private String mBundleLinksPropertyName = DEFAULT_BUNDLE_LINKS_PROPERTY_NAME;
  public void setBundleLinksPropertyName(String pBundleLinksPropertyName) {
    mBundleLinksPropertyName = pBundleLinksPropertyName;
  }

  /**
   * The name of the property bundleLinks
   **/
  public String getBundleLinksPropertyName() {
    return mBundleLinksPropertyName;
  }


  //---------------------------------------------------------------------------
  // property:SkuLinkItemPropertyName
  //---------------------------------------------------------------------------

  private String mSkuLinkItemPropertyName = DEFAULT_SKU_LINK_ITEM_PROPERTY_NAME;
  public void setSkuLinkItemPropertyName(String pSkuLinkItemPropertyName) {
    mSkuLinkItemPropertyName = pSkuLinkItemPropertyName;
  }

  /**
   * the name of the item property in a sku link.
   **/
  public String getSkuLinkItemPropertyName() {
    return mSkuLinkItemPropertyName;
  }


  //---------------------------------------------------------------------------
  // property:SkuLinkQuantityPropertyName
  //---------------------------------------------------------------------------

  private String mSkuLinkQuantityPropertyName = DEFAULT_SKU_LINK_QUANTITY_PROPERTY_NAME;
  public void setSkuLinkQuantityPropertyName(String pSkuLinkQuantityPropertyName) {
    mSkuLinkQuantityPropertyName = pSkuLinkQuantityPropertyName;
  }

  /**
   * The name of the quantity property in a sku link
   **/
  public String getSkuLinkQuantityPropertyName() {
    return mSkuLinkQuantityPropertyName;
  }

  //-------------------------------------
  // property: AvailabilityStatusDerivedValue
  //-------------------------------------
  int mAvailabilityStatusDerivedValue = AVAILABILITY_STATUS_DERIVED;
  public void setAvailabilityStatusDerivedValue(int pAvailabilityStatusDerivedValue) {
    mAvailabilityStatusDerivedValue = pAvailabilityStatusDerivedValue;
  }
  /**
   * The integer value for the availabilityStatus of DERIVED
   **/
  public int getAvailabilityStatusDerivedValue() {
    return mAvailabilityStatusDerivedValue;
  }
  //-------------------------------------
  // property: AvailabilityStatusInStockValue
  //-------------------------------------
  int mAvailabilityStatusInStockValue = AVAILABILITY_STATUS_IN_STOCK;
  public void setAvailabilityStatusInStockValue(int pAvailabilityStatusInStockValue) {
    mAvailabilityStatusInStockValue = pAvailabilityStatusInStockValue;
  }
  /**
   * The integer value for the availabilityStatus of IN_STOCK
   **/
  public int getAvailabilityStatusInStockValue() {
    return mAvailabilityStatusInStockValue;
  }
  //-------------------------------------
  // property: AvailabilityStatusBackorderableValue
  //-------------------------------------
  int mAvailabilityStatusBackorderableValue = AVAILABILITY_STATUS_BACKORDERABLE;
  public void setAvailabilityStatusBackorderableValue(int pAvailabilityStatusBackorderableValue) {
    mAvailabilityStatusBackorderableValue = pAvailabilityStatusBackorderableValue;
  }
  /**
   * The integer value for the availabilityStatus of BACKORDERABLE
   **/
  public int getAvailabilityStatusBackorderableValue() {
    return mAvailabilityStatusBackorderableValue;
  }
  //-------------------------------------
  // property: AvailabilityStatusPreorderableValue
  //-------------------------------------
  int mAvailabilityStatusPreorderableValue = AVAILABILITY_STATUS_PREORDERABLE;
  public void setAvailabilityStatusPreorderableValue(int pAvailabilityStatusPreorderableValue) {
    mAvailabilityStatusPreorderableValue = pAvailabilityStatusPreorderableValue;
  }
  /**
   * The integer value for the availabilityStatus of PREORDERABLE
   **/
  public int getAvailabilityStatusPreorderableValue() {
    return mAvailabilityStatusPreorderableValue;
  }
  //-------------------------------------
  // property: AvailabilityStatusOutOfStockValue
  //-------------------------------------
  int mAvailabilityStatusOutOfStockValue = AVAILABILITY_STATUS_OUT_OF_STOCK;
  public void setAvailabilityStatusOutOfStockValue(int pAvailabilityStatusOutOfStockValue) {
    mAvailabilityStatusOutOfStockValue = pAvailabilityStatusOutOfStockValue;
  }
  /**
   * The integer value for the availabilityStatus of OUT_OF_STOCK
   **/
  public int getAvailabilityStatusOutOfStockValue() {
    return mAvailabilityStatusOutOfStockValue;
  }


  //-------------------------------------
  // property: AvailabilityStatusDiscontinuedValue
  //-------------------------------------
  int mAvailabilityStatusDiscontinuedValue = AVAILABILITY_STATUS_DISCONTINUED;
  public void setAvailabilityStatusDiscontinuedValue(int pAvailabilityStatusDiscontinuedValue) {
    mAvailabilityStatusDiscontinuedValue = pAvailabilityStatusDiscontinuedValue;
  }
  /**
   * The integer value for the availabilityStatus of DISCONTINUED
   **/
  public int getAvailabilityStatusDiscontinuedValue() {
    return mAvailabilityStatusDiscontinuedValue;
  }


  //---------------------------------------------------------------------------
  // property:DefaultStockLevel
  //---------------------------------------------------------------------------

  private long mDefaultStockLevel = DEFAULT_STOCK_LEVEL;
  public void setDefaultStockLevel(long pDefaultStockLevel) {
    mDefaultStockLevel = pDefaultStockLevel;
  }

  /**
   * The default value if the stock level of an item is not defined
   **/
  public long getDefaultStockLevel() {
    return mDefaultStockLevel;
  }

  //---------------------------------------------------------------------------
  // property:DefaultBackorderLevel
  //---------------------------------------------------------------------------

  private long mDefaultBackorderLevel = DEFAULT_BACKORDER_LEVEL;
  public void setDefaultBackorderLevel(long pDefaultBackorderLevel) {
    mDefaultBackorderLevel = pDefaultBackorderLevel;
  }

  /**
   * The default value if the backorder level of an item is not defined
   **/
  public long getDefaultBackorderLevel() {
    return mDefaultBackorderLevel;
  }

  //---------------------------------------------------------------------------
  // property:DefaultPreorderLevel
  //---------------------------------------------------------------------------

  private long mDefaultPreorderLevel = DEFAULT_PREORDER_LEVEL;
  public void setDefaultPreorderLevel(long pDefaultPreorderLevel) {
    mDefaultPreorderLevel = pDefaultPreorderLevel;
  }

  /**
   * The default value if the preorder level of an item is not defined
   **/
  public long getDefaultPreorderLevel() {
    return mDefaultPreorderLevel;
  }

  //-------------------------------------
  // property: Repository
  MutableRepository mRepository;

  //---------------------------------------------------------------------------
  // property:MessageIdGenerator
  //---------------------------------------------------------------------------

  private IdGenerator mMessageIdGenerator;
  public void setMessageIdGenerator(IdGenerator pMessageIdGenerator) {
    mMessageIdGenerator = pMessageIdGenerator;
  }

  /**
   * The service that generates Ids for all messages.
   **/
  public IdGenerator getMessageIdGenerator() {
    return mMessageIdGenerator;
  }


  //---------------------------------------------------------------------------
  // property:MessageIdSpaceName
  //---------------------------------------------------------------------------

  private String mMessageIdSpaceName;
  public void setMessageIdSpaceName(String pMessageIdSpaceName) {
    mMessageIdSpaceName = pMessageIdSpaceName;
  }

  /**
   * The name of the idspace to get our message ids from
   **/
  public String getMessageIdSpaceName() {
    return mMessageIdSpaceName;
  }

  /**
   * Sets property Repository
   **/
  public void setRepository(MutableRepository pRepository) {
    mRepository = pRepository;
  }

  /**
   * Returns property Repository
   **/
  public MutableRepository getRepository() {
    return mRepository;
  }

  //-------------------------------------
  // property: ItemType
  String mItemType;

  /**
   * Sets property ItemType
   **/
  public void setItemType(String pItemType) {
    mItemType = pItemType;
  }

  /**
   * Returns property ItemType
   **/
  public String getItemType() {
    return mItemType;
  }

  //-------------------------------------
  // property: CatalogRefIdMatchQuery
  RqlStatement mCatalogRefIdMatchQuery;

  /**
   * Sets property CatalogRefIdMatchQuery
   **/
  public void setCatalogRefIdMatchQuery(RqlStatement pCatalogRefIdMatchQuery) {
    mCatalogRefIdMatchQuery = pCatalogRefIdMatchQuery;
  }

  /**
   * Returns property CatalogRefIdMatchQuery
   **/
  public RqlStatement getCatalogRefIdMatchQuery() {
    return mCatalogRefIdMatchQuery;
  }

  //-------------------------------------
  // property: CatalogRefRepository
  Repository mCatalogRefRepository;

  /**
   * Sets property CatalogRefRepository
   **/
  public void setCatalogRefRepository(Repository pCatalogRefRepository) {
    mCatalogRefRepository = pCatalogRefRepository;
  }

  /**
   * Returns property CatalogRefRepository
   **/
  public Repository getCatalogRefRepository() {
    return mCatalogRefRepository;
  }

  //-------------------------------------
  // property: CatalogRefItemType
  String mCatalogRefItemType;

  /**
   * Sets property CatalogRefItemType
   **/
  public void setCatalogRefItemType(String pCatalogRefItemType) {
    mCatalogRefItemType = pCatalogRefItemType;
  }

  /**
   * Returns property CatalogRefItemType
   **/
  public String getCatalogRefItemType() {
    return mCatalogRefItemType;
  }

  //---------------------------------------------------------------------------
  // property: LockManager
  //---------------------------------------------------------------------------

  ClientLockManager mClientLockManager=null;

  /**
   * @deprecated locks are no longer used
   **/
  public void setClientLockManager(ClientLockManager pClientLockManager) {
    mClientLockManager = pClientLockManager;
  }

  /**
   * @deprecated locks are no longer used
   **/
  public ClientLockManager getClientLockManager() {
    return mClientLockManager;
  }


  //---------------------------------------------------------------------------
  // property:UpdateInventoryPort
  //---------------------------------------------------------------------------

  private String mUpdateInventoryPort;
  public void setUpdateInventoryPort(String pUpdateInventoryPort) {
    mUpdateInventoryPort = pUpdateInventoryPort;
  }

  /**
   * The port to which all UpdateInventory messages are sent.
   **/
  public String getUpdateInventoryPort() {
    return mUpdateInventoryPort;
  }


  //---------------------------------------------------------------------------
  // property:InventoryEventPort
  //---------------------------------------------------------------------------

  private String mInventoryEventPort;
  public void setInventoryEventPort(String pInventoryEventPort) {
    mInventoryEventPort = pInventoryEventPort;
  }

  /**
   *  The port to which all inventory scenario events are sent.
   **/
  public String getInventoryEventPort() {
    return mInventoryEventPort;
  }

  /**
   * Return the RepositoryItem which represents the catalog ref (aka SKU)
   * @exception RepositoryException if there was a problem accessing the item
   */
  protected RepositoryItem getCatalogRefItem(String pId)
       throws RepositoryException
  {
    return getCatalogRefRepository().getItem(pId, getCatalogRefItemType());
  }


  //---------------------------------------------------------------------------
  // property:MaximumRetriesPerRowLock
  //---------------------------------------------------------------------------

  private int mMaximumRetriesPerRowLock = 5 ;
  public void setMaximumRetriesPerRowLock(int pMaximumRetriesPerRowLock) {
    mMaximumRetriesPerRowLock = pMaximumRetriesPerRowLock;
  }

  public int getMaximumRetriesPerRowLock() {
    return mMaximumRetriesPerRowLock;
  }


  //---------------------------------------------------------------------------
  // property:MillisecondDelayBeforeLockRetry
  //---------------------------------------------------------------------------

  private int mMillisecondDelayBeforeLockRetry = 500;
  public void setMillisecondDelayBeforeLockRetry(int pMillisecondDelayBeforeLockRetry) {
    mMillisecondDelayBeforeLockRetry = pMillisecondDelayBeforeLockRetry;
  }

  public int getMillisecondDelayBeforeLockRetry() {
    return mMillisecondDelayBeforeLockRetry;
  }


  //---------------------------------------------------------------------------
  // property:InventoryRowLockProperty
  //---------------------------------------------------------------------------

  private String mInventoryRowLockProperty = "inventoryLock";
  public void setInventoryRowLockProperty(String pInventoryRowLockProperty) {
    mInventoryRowLockProperty = pInventoryRowLockProperty;
  }

  /**
   * The name of the property of inventory that is used to grab a row lock
   **/
  public String getInventoryRowLockProperty() {
    return mInventoryRowLockProperty;
  }


  //---------------------------------------------------------------------------
  // property:InventoryRowLockSQL
  //---------------------------------------------------------------------------

  private String mInventoryRowLockSQL = DEFAULT_INVENTORY_ROW_LOCK_SQL;
  public void setInventoryRowLockSQL(String pInventoryRowLockSQL) {
    mInventoryRowLockSQL = pInventoryRowLockSQL;
  }

  /**
   * The sql command that is used to grab the row lock
   **/
  public String getInventoryRowLockSQL() {
    return mInventoryRowLockSQL;
  }

  /**
   * Return the MutableRepositoryItem which represents the inventory information
   * @exception RepositoryException if there was a problem accessing the item
   */
  protected MutableRepositoryItem getInventoryItemForUpdate(String pId)
       throws RepositoryException
  {
    int retryCount = 0;
    int mostTries = getMaximumRetriesPerRowLock();
    int retryInterval = getMillisecondDelayBeforeLockRetry();
    RepositoryException lastException = null;

    try {
      lock(pId);
    }
    catch(java.sql.SQLException se) {
      throw new RepositoryException(se);
    }

    RepositoryItem item = getInventoryItem(pId);
    MutableRepositoryItem mutItem = null;
    if (item instanceof MutableRepositoryItem)
      mutItem = (MutableRepositoryItem)item;
    else  if (item != null)
      mutItem = getRepository().getItemForUpdate(item.getRepositoryId(), getItemType());
    else
      return null;

    return mutItem;
  }

  /**
   * Return the RepositoryItem which represents the inventory information
   * @return The RepositoryItem
   * @return null if the item does not exist.
   * @exception RepositoryException if there was a problem accessing the item
   */
  protected RepositoryItem getInventoryItem(String pId)
       throws RepositoryException
  {
    if (getRepository() != null) {
      if (getCatalogRefIdMatchQuery() != null) {
        RepositoryView view = getRepository().getView(getItemType());
        Object [] params = {pId};
        RepositoryItem [] items = getCatalogRefIdMatchQuery().executeQuery(view, params);
        if ((items != null) && (items.length > 0))
          return items[0];
        else
          return null;
      }
      else
        return getRepository().getItem(pId, getItemType());
    }
    return null;
  }


  //---------------------------------------------------------------------------
  // property:InventoryName
  //---------------------------------------------------------------------------

  private String mInventoryName = DEFAULT_INVENTORY_NAME;
  public void setInventoryName(String pInventoryName) {
    mInventoryName = pInventoryName;
  }

  /**
   * A displayable name for this inventory manager.
   **/
  public String getInventoryName() {
    return mInventoryName;
  }

  //-------------------------------------------
  // InventoryManager implementation

  /**
   * Check the availability status (queryAvailabilityStatus).  If it
   * is not IN_STOCK, return INVENTORY_STATUS_FAIL.  If it is in
   * stock, but there is less than pHowMany, return
   * INVENTORY_STATUS_INSUFFICIENT_SUPPLY.  Decrement the stockLevel
   * by the amount allocated.  If the item passed in is a bundle
   * purchaseBundle is called.
   *
   * @param pId The id of the SKU being purchased.
   * @param pHowMany The amount of the SKU being purchased.
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND if the SKU isn't found.
   * @return INVENTORY_STATUS_ITEM_FAIL if the item has the wrong availability status.
   * @return INVENTORY_STATUS_INSUFFICIENT_SUPPLY if there aren't enough of the SKU
   *     to purchase the requested amount.
   * @return INVENTORY_STATUS_SUCCEED if the items were purchased successfully
   * @exception InventoryException
   * @see #queryAvailabilityStatus
   * @see #purchaseBundle
   **/
  public int purchase(String pId, long pHowMany)
       throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside purchase: " + pHowMany + " of sku " + pId);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // bundles are handled differently
      if(isBundle(pId)) {
        return purchaseBundle(pId, pHowMany);
      }

      MutableRepositoryItem item = getInventoryItemForUpdate(pId);
      if (item == null)
  return INVENTORY_STATUS_ITEM_NOT_FOUND;

      // check that the availability status allows it to be purchased
      int availabilityStatus = queryAvailabilityStatus(pId);
      if(availabilityStatus != getAvailabilityStatusInStockValue()) {
        if(isLoggingDebug())
          logDebug("Item was not IN_STOCK");

        return INVENTORY_STATUS_FAIL;
      }

      int success = decrementSKULevel(item, pHowMany,
                                      getStockLevelPropertyName(),
                                      getStockThresholdPropertyName());

      updateItem(item);
      return success;

    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /**
   * Check the availability status (queryAvailabilityStatus).  If it
   * is not IN_STOCK, return INVENTORY_STATUS_FAIL.  If it is in
   * stock, but there is less than pHowMany, return
   * INVENTORY_STATUS_INSUFFICIENT_SUPPLY.  Decrement the stockLevel
   * by the amount allocated and increment the backorderLevel by the
   * same. If the item is a bundle purchaseBundleOffBackorder is
   * called.
   *
   * @param pId The id of the SKU being purchased.
   * @param pHowMany The amount of the SKU being purchased.
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND if the SKU isn't found.
   * @return INVENTORY_STATUS_ITEM_FAIL if the item has the wrong availability status.
   * @return INVENTORY_STATUS_INSUFFICIENT_SUPPLY if there aren't enough of the SKU
   *     to purchase the requested amount.
   * @return INVENTORY_STATUS_SUCCEED if the items were purchased successfully
   * @exception InventoryException
   * @see #queryAvailabilityStatus
   * @see #purchaseBundleOffBackorder
   **/
  public int purchaseOffBackorder(String pId, long pHowMany)
       throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside purchaseOffBackorder: " + pHowMany + " of sku " + pId);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // bundles are handled differently
      if(isBundle(pId)) {
        return purchaseBundleOffBackorder(pId, pHowMany);
      }

      MutableRepositoryItem item = getInventoryItemForUpdate(pId);
      if (item == null)
  return INVENTORY_STATUS_ITEM_NOT_FOUND;

      // check that the availability status allows it to be purchased
      int availabilityStatus = queryAvailabilityStatus(pId);
      if(availabilityStatus != getAvailabilityStatusInStockValue()) {
        if(isLoggingDebug())
          logDebug(pId + " is not in stock... can't purchase.");
        return INVENTORY_STATUS_FAIL;
      }

      Long backvalue = (Long)getPropertyValue(item, getBackorderLevelPropertyName());

      long backlevel = 0;
      if(backvalue == null) {
        backlevel = getDefaultBackorderLevel();
      } else {
        backlevel = backvalue.longValue();
      }

      int success = decrementSKULevel(item, pHowMany,
                                      getStockLevelPropertyName(),
                                      getStockThresholdPropertyName());

      if(success == INVENTORY_STATUS_SUCCEED) {
        if(backlevel >= 0)
          item.setPropertyValue(getBackorderLevelPropertyName(), Long.valueOf(backlevel + pHowMany));
      }

      updateItem(item);
      return success;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /**
   * Check the availability status (queryAvailabilityStatus).  If it
   * is not IN_STOCK, return INVENTORY_STATUS_FAIL.  If it is in
   * stock, but there is less than pHowMany, return
   * INVENTORY_STATUS_INSUFFICIENT_SUPPLY.  Decrement the stockLevel
   * by the amount allocated and increment the preorderLevel by the
   * same.  If the item is a bundle purchaseBundleOffPreorder is
   * called.
   *
   * @param pId The id of the SKU being purchased.
   * @param pHowMany The amount of the SKU being purchased.
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND if the SKU isn't found.
   * @return INVENTORY_STATUS_ITEM_FAIL if the item has the wrong availability status.
   * @return INVENTORY_STATUS_INSUFFICIENT_SUPPLY if there aren't enough of the SKU
   *     to purchase the requested amount.
   * @return INVENTORY_STATUS_SUCCEED if the items were purchased successfully
   * @exception InventoryException
   * @see #queryAvailabilityStatus
   * @see #purchaseBundleOffPreorder
   **/
  public int purchaseOffPreorder(String pId, long pHowMany)
       throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside purchaseOffPreorder: " + pHowMany + " of sku " + pId);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // bundles are handled differently
      if(isBundle(pId)) {
        return purchaseBundleOffPreorder(pId, pHowMany);
      }

      MutableRepositoryItem item = getInventoryItemForUpdate(pId);
      if (item == null)
  return INVENTORY_STATUS_ITEM_NOT_FOUND;

      // check that the availability status allows it to be purchased
      int availabilityStatus = queryAvailabilityStatus(pId);
      if(availabilityStatus != getAvailabilityStatusInStockValue()) {
        return INVENTORY_STATUS_FAIL;
      }

      Long prevalue = (Long)getPropertyValue(item, getPreorderLevelPropertyName());
      long prelevel = 0;

      if(prevalue == null) {
        prelevel = getDefaultPreorderLevel();
      }
      else {
        prelevel = prevalue.longValue();
      }
      int success = decrementSKULevel(item, pHowMany,
                                      getStockLevelPropertyName(),
                                      getStockThresholdPropertyName());

      if(success == INVENTORY_STATUS_SUCCEED) {
        if(prelevel >= 0)
          item.setPropertyValue(getPreorderLevelPropertyName(), Long.valueOf(prelevel + pHowMany));
      }

      updateItem(item);
      return success;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /**
   * decrement the item's preorder count,
   * If the item is a bundle preorderBundle is called.
   *
   * @param pId The id of the SKU being preordered.
   * @param pHowMany The amount of the SKU being preordered.
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND if the SKU doesn't correspond to any item
   * @return INVENTORY_STATUS_SUCCEED if the item was preordered successfully.
   * @return INVENTORY_STATUS_INSUFFICIENT_SUPPLY if the preorderLevel is too low
   *     to preorder the requested amount.
   * @exception InventoryException
   * @see #preorderBundle
   **/
  public int preorder(String pId, long pHowMany)
       throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside preorder: " + pHowMany + " of sku " + pId);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // bundles are handled differently
      if(isBundle(pId)) {
        return preorderBundle(pId, pHowMany);
      }

      MutableRepositoryItem item = getInventoryItemForUpdate(pId);
      if (item == null)
  return INVENTORY_STATUS_ITEM_NOT_FOUND;

      int success = decrementSKULevel(item, pHowMany,
                                      getPreorderLevelPropertyName(),
                                      getPreorderThresholdPropertyName());

      updateItem(item);
      return success;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /**
   * decrement the item's backorder count,
   * If the item is a bundle backorderBundle is called.
   *
   * @param pId The id of the SKU being backordered.
   * @param pHowMany The amount of the SKU being backordered..
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND if the SKU doesn't correspond to any item
   * @return INVENTORY_STATUS_SUCCEED if the item was backordered successfully.
   * @return INVENTORY_STATUS_INSUFFICIENT_SUPPLY if the preorderLevel is too low
   *     to preorder the requested amount.
   * @exception InventoryException
   * @see #backorderBundle
   **/
  public int backorder(String pId, long pHowMany)
       throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside backorder: " + pHowMany + " of sku " + pId);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // bundles are handled differently
      if(isBundle(pId)) {
        return backorderBundle(pId, pHowMany);
      }

      MutableRepositoryItem item = getInventoryItemForUpdate(pId);
      if (item == null)
  return INVENTORY_STATUS_ITEM_NOT_FOUND;

      int success = decrementSKULevel(item, pHowMany,
                                      getBackorderLevelPropertyName(),
                                      getBackorderThresholdPropertyName());


      updateItem(item);
      return success;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /**
   * Decrement the given level by the given amount.  Does no checks on
   * the availabilityStatus of the item in the repository. If the
   * given threshold is reached, then thresholdHasBeenReached will be
   * called.  This method assumes the item is not a bundle.
   *
   * @param pItem The id of the SKU being changed.
   * @param pHowMany The amount the level should be decreased by.
   * @param pLevelName The name of the level that should be decreased.
   *                   (one of stockLevel, backorderLevel, preorderLevel)
   * @param pThresholdName The name of the threshold associated with pLevelName
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND if the SKU doesn't correspond to any item
   * @return INVENTORY_STATUS_SUCCEED if the level was decremented successfully
   * @return INVENTORY_STATUS_INSUFFICIENT_SUPPLY if the level was too low to decrement by
   *          the given quantity
   * @exception InventoryException
   * @see #thresholdHasBeenReached
   **/
  protected int decrementSKULevel(MutableRepositoryItem pItem, long pHowMany,
                                  String pLevelName, String pThresholdName)
    throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside decrementSKULevel: decrement " + pLevelName +
               " by " + pHowMany + " for " + pItem.getRepositoryId());

    Long value = (Long)getPropertyValue(pItem, pLevelName);
    long currentLevel = 0;
    if (value == null) {
      // get the default value for this level
      if(pLevelName.equals(getStockLevelPropertyName()))
        currentLevel = getDefaultStockLevel();
      else if(pLevelName.equals(getBackorderLevelPropertyName()))
        currentLevel = getDefaultBackorderLevel();
      else if(pLevelName.equals(getPreorderLevelPropertyName()))
        currentLevel = getDefaultPreorderLevel();
    }
    else {
      currentLevel = value.longValue();
    }
    if(isLoggingDebug())
      logDebug("Current value of " + pLevelName + ": " + currentLevel);
    if(currentLevel < 0) {
      // this means there is an infinite supply
      if(isLoggingDebug())
        logDebug("Decrement of " + pHowMany + " from " + pLevelName + " was successful. (infinite)");
      return INVENTORY_STATUS_SUCCEED;
    }

    long level = currentLevel - pHowMany;
    if (level < 0) {
      if(isLoggingDebug())
        logDebug("Insufficient supply for decrement of " + pLevelName + " by " + pHowMany);
      return INVENTORY_STATUS_INSUFFICIENT_SUPPLY;
    }
    pItem.setPropertyValue(pLevelName, Long.valueOf(level));

    // check the threshold value
    Long threshvalue = (Long)getPropertyValue(pItem, pThresholdName);
    if(threshvalue != null) {
      long threshold = threshvalue.longValue();
      if(level < threshold)
        thresholdHasBeenReached(pItem.getRepositoryId(), pLevelName, pThresholdName, level, threshold);
    }
    if(isLoggingDebug())
      logDebug("Decrement of " + pHowMany + " from " + pLevelName + " was successful.");
    return INVENTORY_STATUS_SUCCEED;
  }

  /**
   * set the item's stock level to pNumber,
   * If the item is a bundle, this method does nothing
   * and returns INVENTORY_STATUS_SUCCEED.
   *
   * @param pId The id of the SKU whose stockLevel is being set
   * @param pNumber The amount to set the stockLevel to.
   * @return INVENTORY_STATUS_SUCCEED if the item's stock level was set successfully.
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND if no item could be found for the SKU.
   * @exception InventoryException
   **/
  public int setStockLevel(String pId, long pNumber)
       throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside setStockLevel: " + pId + " stock = " + pNumber);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // bundles are handled differently
      if(isBundle(pId)) {
        return INVENTORY_STATUS_SUCCEED;
      }

      MutableRepositoryItem item = getInventoryItemForUpdate(pId);
      if (item == null)
  return INVENTORY_STATUS_ITEM_NOT_FOUND;

      item.setPropertyValue(getStockLevelPropertyName(), Long.valueOf(pNumber));

      updateItem(item);
      return INVENTORY_STATUS_SUCCEED;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /**
   * set the item's backorder level to pNumber,
   * If the item is a bundle, this method does nothing
   * and returns INVENTORY_STATUS_SUCCEED.
   *
   * @param pId The id of the SKU whose backorderLevel is being set
   * @param pNumber The amount to set the backorderLevel to.
   * @return INVENTORY_STATUS_SUCCEED if the item's backorder level was set successfully.
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND if no item could be found for the SKU.
   * @exception InventoryException
   **/
  public int setBackorderLevel(String pId, long pNumber)
       throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside setBackorderLevel: " + pId + " backorder = " + pNumber);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // bundles are handled differently
      if(isBundle(pId)) {
        return INVENTORY_STATUS_SUCCEED;
      }

      MutableRepositoryItem item = getInventoryItemForUpdate(pId);
      if (item == null)
  return INVENTORY_STATUS_ITEM_NOT_FOUND;

      item.setPropertyValue(getBackorderLevelPropertyName(), Long.valueOf(pNumber));

      updateItem(item);
      return INVENTORY_STATUS_SUCCEED;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }


  /**
   * set the item's preorder level to pNumber,
   * If the item is a bundle, this method does nothing
   * and returns INVENTORY_STATUS_SUCCEED.
   *
   * @param pId The id of the SKU whose preorderLevel is being set
   * @param pNumber The amount to set the preorderLevel to.
   * @return INVENTORY_STATUS_SUCCEED if the item's preorder level was set successfully.
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND if no item could be found for the SKU.
   * @exception InventoryException
   **/
  public int setPreorderLevel(String pId, long pNumber)
       throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside setPreorderLevel: " + pId + " preorder = " + pNumber);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // bundles are handled differently
      if(isBundle(pId)) {
        return INVENTORY_STATUS_SUCCEED;
      }

      MutableRepositoryItem item = getInventoryItemForUpdate(pId);
      if (item == null)
  return INVENTORY_STATUS_ITEM_NOT_FOUND;

      item.setPropertyValue(getPreorderLevelPropertyName(), Long.valueOf(pNumber));

      updateItem(item);
      return INVENTORY_STATUS_SUCCEED;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /*
   * Used internally
   */
  private int increaseStockLevel(MutableRepositoryItem pItem, long pNumber)
    throws InventoryException
  {
    Long value = (Long)getPropertyValue(pItem, getStockLevelPropertyName());
    if (value != null && value.longValue() != -1) {
      long level = value.longValue() + pNumber;
      pItem.setPropertyValue(getStockLevelPropertyName(), Long.valueOf(level));
    }
    return INVENTORY_STATUS_SUCCEED;
  }

  /**
   * increase the item's stock level by pNumber,
   * If the item is a bundle, this method does nothing
   * and returns INVENTORY_STATUS_SUCCEED.  If the stock level is not set, there
   * is no effect.
   *
   * @param pId The id of the SKU whose stockLevel is being increased.
   * @param pNumber The amount to increase the stockLevel to.
   * @return INVENTORY_STATUS_SUCCEED if the item's stock level was set successfully.
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND if no item could be found for the SKU.
   * @exception InventoryException
   */
  public int increaseStockLevel(String pId, long pNumber)
       throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside increaseStockLevel: " + pId + " stock by " + pNumber);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // bundles are handled differently
      if(isBundle(pId)) {
        return INVENTORY_STATUS_SUCCEED;
      }

      MutableRepositoryItem item = getInventoryItemForUpdate(pId);
      if (item == null)
  return INVENTORY_STATUS_ITEM_NOT_FOUND;

      int success = increaseStockLevel(item, pNumber);

      updateItem(item);
      return success;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /**
   * decrease the item's stock level by pNumber,
   * If the item is a bundle, this method does nothing
   * and returns INVENTORY_STATUS_SUCCEED.  This method is to be
   * used for inventory adjustments, not for purchases.  For purchases, use
   * the purchase(String pId, long pHowMany) method.
   *
   * @param pId The id of the SKU whose stockLevel is being decreased.
   * @param pNumber The amount to decrease the stockLevel to.
   * @return INVENTORY_STATUS_SUCCEED if the item's stock level was set successfully.
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND if no item could be found for the SKU.
   * @return INVENTORY_STATUS_INSUFFICIENT_SUPPLY if removing the desired number from the
   *     inventory level of the desired item would make that level too low.
   * @exception InventoryException
   **/
  public int decreaseStockLevel(String pId, long pNumber)
       throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside decreaseStockLevel: " + pId + " stock by " + pNumber);

    TransactionDemarcation td = new TransactionDemarcation();
    int success = INVENTORY_STATUS_FAIL;
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // bundles are handled differently
      if(isBundle(pId)) {
        return INVENTORY_STATUS_SUCCEED;
      }

      MutableRepositoryItem item = getInventoryItemForUpdate(pId);
      if (item == null)
  return INVENTORY_STATUS_ITEM_NOT_FOUND;

      Long value = (Long)getPropertyValue(item, getStockLevelPropertyName());
      if (value != null) {
  long level = value.longValue() - pNumber;
  if (level < 0) {
          updateItem(item);
    return INVENTORY_STATUS_INSUFFICIENT_SUPPLY;
        }
  item.setPropertyValue(getStockLevelPropertyName(), Long.valueOf(level));
  success = INVENTORY_STATUS_SUCCEED;
      }

      updateItem(item);
      return success;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /*
   * Used internally
   */
  private int increaseBackorderLevel(MutableRepositoryItem pItem, long pNumber)
    throws InventoryException
  {
    Long value = (Long)getPropertyValue(pItem, getBackorderLevelPropertyName());
    if (value != null && value.longValue() != -1) {
      long level = value.longValue() + pNumber;
      pItem.setPropertyValue(getBackorderLevelPropertyName(), Long.valueOf(level));
    }
    return INVENTORY_STATUS_SUCCEED;
  }

  /**
   * increase the item's backorder level by pNumber,
   * If the item is a bundle, this method does nothing
   * and returns INVENTORY_STATUS_SUCCEED. If the backorder level is not set, there
   * is no effect.
   *
   * @param pId The id of the SKU whose backorderLevel is being increased.
   * @param pNumber The amount to increase the backorderLevel to.
   * @return INVENTORY_STATUS_SUCCEED if the item's backorder level was set successfully.
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND if no item could be found for the SKU.
   * @exception InventoryException
   **/
  public int increaseBackorderLevel(String pId, long pNumber)
       throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside increaseBackorderLevel: " + pId + " backorder by " + pNumber);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // bundles are handled differently
      if(isBundle(pId)) {
        return INVENTORY_STATUS_SUCCEED;
      }

      MutableRepositoryItem item = getInventoryItemForUpdate(pId);
      if (item == null)
  return INVENTORY_STATUS_ITEM_NOT_FOUND;

      int success;
      success = increaseBackorderLevel(item, pNumber);

      updateItem(item);
      return success;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /**
   * decrease the item's backorder level by pNumber,
   * If the item is a bundle, this method does nothing
   * and returns INVENTORY_STATUS_SUCCEED.
   *
   * @param pId The id of the SKU whose backorderLevel is being decreased.
   * @param pNumber The amount to decrease the backorderLevel to.
   * @return INVENTORY_STATUS_SUCCEED if the item's backorder level was set successfully.
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND if no item could be found for the SKU.
   * @return INVENTORY_STATUS_INSUFFICIENT_SUPPLY if removing the desired number from the
   *     inventory level of the desired item would make that level too low.
   * @exception InventoryException
   **/
  public int decreaseBackorderLevel(String pId, long pNumber)
       throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside decreaseBackorderLevel: " + pId + " backorder by " + pNumber);

    TransactionDemarcation td = new TransactionDemarcation();
    int success = INVENTORY_STATUS_FAIL;
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // bundles are handled differently
      if(isBundle(pId)) {
        return INVENTORY_STATUS_SUCCEED;
      }

      MutableRepositoryItem item = getInventoryItemForUpdate(pId);
      if (item == null)
  return INVENTORY_STATUS_ITEM_NOT_FOUND;

      Long value = (Long)getPropertyValue(item, getBackorderLevelPropertyName());
      if (value != null) {
  long level = value.longValue() - pNumber;
  if (level < 0) {
          updateItem(item);
    return INVENTORY_STATUS_INSUFFICIENT_SUPPLY;
        }
  item.setPropertyValue(getBackorderLevelPropertyName(), Long.valueOf(level));
  success = INVENTORY_STATUS_SUCCEED;
      }

      updateItem(item);
      return success;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /*
   * Used internally
   */
  private int increasePreorderLevel(MutableRepositoryItem pItem, long pNumber)
    throws InventoryException
  {
    Long value = (Long)getPropertyValue(pItem, getPreorderLevelPropertyName());
    if (value != null && value.longValue() != -1) {
      long level = value.longValue() + pNumber;
      pItem.setPropertyValue(getPreorderLevelPropertyName(), Long.valueOf(level));
    }
    return INVENTORY_STATUS_SUCCEED;
  }

  /**
   * increase the item's preorder level by pNumber,
   * If the item is a bundle, this method does nothing
   * and returns INVENTORY_STATUS_SUCCEED. If the preorder level is not set, there
   * is no effect.
   *
   * @param pId The id of the SKU whose preorderLevel is being increased.
   * @param pNumber The amount to increase the preorderLevel to.
   * @return INVENTORY_STATUS_SUCCEED if the item's preorder level was set successfully.
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND if no item could be found for the SKU.
   * @exception InventoryException
   **/
  public int increasePreorderLevel(String pId, long pNumber)
       throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside increasePreorderLevel: " + pId + " preorder by " + pNumber);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // bundles are handled differently
      if(isBundle(pId)) {
        return INVENTORY_STATUS_SUCCEED;
      }

      MutableRepositoryItem item = getInventoryItemForUpdate(pId);
      if (item == null)
  return INVENTORY_STATUS_ITEM_NOT_FOUND;

      int success = increasePreorderLevel(item, pNumber);

      updateItem(item);
      return success;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /**
   * decrease the item's preorder level by pNumber,
   * If the item is a bundle, this method does nothing
   * and returns INVENTORY_STATUS_SUCCEED.
   *
   * @param pId The id of the SKU whose preorderLevel is being decreased.
   * @param pNumber The amount to decrease the preorderLevel to.
   * @return INVENTORY_STATUS_SUCCEED if the item's preorder level was set successfully.
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND if no item could be found for the SKU.
   * @return INVENTORY_STATUS_INSUFFICIENT_SUPPLY if removing the desired number from the
   *     inventory level of the desired item would make that level too low.
   * @exception InventoryException
   **/
  public int decreasePreorderLevel(String pId, long pNumber)
       throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside decreasePreorderLevel: " + pId + " preorder by " + pNumber);

    TransactionDemarcation td = new TransactionDemarcation();
    int success = INVENTORY_STATUS_FAIL;
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // bundles are handled differently
      if(isBundle(pId)) {
        return INVENTORY_STATUS_SUCCEED;
      }

      MutableRepositoryItem item = getInventoryItemForUpdate(pId);
      if (item == null)
  return INVENTORY_STATUS_ITEM_NOT_FOUND;

      Long value = (Long)getPropertyValue(item, getPreorderLevelPropertyName());
      if (value != null) {
  long level = value.longValue() - pNumber;
  if (level < 0) {
          updateItem(item);
    return INVENTORY_STATUS_INSUFFICIENT_SUPPLY;
        }
  item.setPropertyValue(getPreorderLevelPropertyName(), Long.valueOf(level));
  success = INVENTORY_STATUS_SUCCEED;
      }

      updateItem(item);
      return success;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /**
   * Set an item's availability status.  Does not check if the item is a bundle.
   *
   * @param pId the id of the item involved
   * @param pStatus the new status (one of AVAILABILITY_STATUS_IN_STOCK,
   *                                       AVAILABILITY_STATUS_BACKORDERABLE,
   *                                       AVAILABILITY_STATUS_PREORDERABLE,
   *                                       AVAILABILITY_STATUS_OUT_OF_STOCK,
   *                                       AVAILABILITY_STATUS_DISCONTINUED,
   *                                    or AVAILABILITY_STATUS_DERIVED)
   * @return INVENTORY_STATUS_SUCCEED, INVENTORY_STATUS_ITEM_NOT_FOUND, or INVENTORY_STATUS_FAIL
   * @exception InventoryException
   */
  public int setAvailabilityStatus(String pId, int pStatus) throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside setAvailabilityStatus: " + pId + " status = " + pStatus);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      MutableRepositoryItem item = getInventoryItemForUpdate(pId);
      if (item == null)
  return INVENTORY_STATUS_ITEM_NOT_FOUND;

      if(!(pStatus == AVAILABILITY_STATUS_IN_STOCK) &&
         !(pStatus == AVAILABILITY_STATUS_BACKORDERABLE) &&
         !(pStatus == AVAILABILITY_STATUS_PREORDERABLE) &&
         !(pStatus == AVAILABILITY_STATUS_OUT_OF_STOCK) &&
   !(pStatus == AVAILABILITY_STATUS_DISCONTINUED) &&
         !(pStatus == AVAILABILITY_STATUS_DERIVED)) {
        if(isLoggingError()) {
          logError(MessageFormat.format(Constants.INVALID_AVAILABILITY_STATUS, pId, Integer.toString(pStatus)));
        }
        updateItem(item);
        return INVENTORY_STATUS_FAIL;
      }

      item.setPropertyValue(getAvailabilityStatusPropertyName(), Integer.valueOf(pStatus));

      updateItem(item);
      return INVENTORY_STATUS_SUCCEED;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /**
   * Set an item's availability date.  Does nothing if the item is a bundle.
   *
   * @param pId the id of the item involved
   * @param pDate The new availability date for the item.
   * @return INVENTORY_STATUS_SUCCEED, INVENTORY_STATUS_ITEM_NOT_FOUND, or INVENTORY_STATUS_FAIL
   * @exception InventoryException
   */
  public int setAvailabilityDate(String pId, Date pDate) throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside setAvailabilityDate: " + pId + " Date = " + pDate);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // bundles are handled differently
      if(isBundle(pId)) {
        return INVENTORY_STATUS_SUCCEED;
      }

      MutableRepositoryItem item = getInventoryItemForUpdate(pId);
      if (item == null)
  return INVENTORY_STATUS_ITEM_NOT_FOUND;

      item.setPropertyValue(getAvailabilityDatePropertyName(), pDate);

      updateItem(item);
      return INVENTORY_STATUS_SUCCEED;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /*
   * Returns the availability status of the item.  If the status in
   * the repository is DERIVED, then deriveAvailabilityStatus
   * is called, otherwise the value is returned.  If the item is a
   * bundle, then deriveBundleAvailabilityStatus
   *
   * @param pId The id of the SKU whose status is being queried
   * @return AVAILABILITY_STATUS_IN_STOCK
   * @return AVAILABILITY_STATUS_OUT_OF_STOCK
   * @return AVAILABILITY_STATUS_PREORDERABLE
   * @return AVAILABILITY_STATUS_BACKORDERABLE
   * @return AVAILABILITY_STATUS_DISCONTINUED
   * @exception InventoryException
   * @see #deriveAvailabilityStatus
   * @see #deriveBundleAvailabilityStatus
   **/
  public int queryAvailabilityStatus(String pId)
       throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside queryAvailabilityStatus: " + pId);

    if(isBundle(pId)) {
      return deriveBundleAvailabilityStatus(pId);
    }
    Number value = (Number)getInventoryInfo(pId, getAvailabilityStatusPropertyName());
    if (value != null) {
      int status = value.intValue();
      if(status == getAvailabilityStatusDerivedValue()) {
        return deriveAvailabilityStatus(pId);
      } else {
        // don't derive the value
        return value.intValue();
      }
    }

    throw new InventoryException("queryAvailabilityStatus(" + pId + "): " + getAvailabilityStatusPropertyName() + "=\"" + value + "\"");
  }

  /**
   * query the number of items in stock.  If the item is a bundle,
   * then queryBundleStockLevel
   *
   * @param pId The id of the SKU whose stockLevel is returned
   * @return The stockLevel of the SKU
   * @exception InventoryException
   * @see #queryBundleStockLevel
   **/
  public long queryStockLevel(String pId)
       throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside queryStockLevel: " + pId);

    // bundles are handled differently
    if(isBundle(pId)) {
      return queryBundleStockLevel(pId);
    }

    Number value = (Number)getInventoryInfo(pId, getStockLevelPropertyName());
    long level;
    if (value != null)
      level = value.longValue();
    else
      level = getDefaultStockLevel();

    if(isLoggingDebug())
      logDebug("stockLevel = " + level);

    return level;
  }

  /**
   * query the number of items available for backorder.  If the item
   * is a bundle, then queryBundleBackorderLevel
   *
   * @param pId The id of the SKU whose backorderLevel is returned
   * @return The backorderLevel of the SKU
   * @exception InventoryException
   * @see #queryBundleBackorderLevel
   **/
  public long queryBackorderLevel(String pId)
       throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside queryBackorderLevel: " + pId);

    // bundles are handled differently
    if(isBundle(pId)) {
      return queryBundleBackorderLevel(pId);
    }

    Number value = (Number)getInventoryInfo(pId, getBackorderLevelPropertyName());
    long level;
    if (value != null)
      level = value.longValue();
    else
      level = getDefaultBackorderLevel();

    if(isLoggingDebug())
      logDebug("backorderLevel = " + level);

    return level;
  }

  /**
   * query the number of items in available in preorder.  If the item
   * is a bundle, then queryBundlePreorderLevel
   *
   * @param pId The id of the SKU whose preorderLevel is returned
   * @return The preorderLevel of the SKU
   * @exception InventoryException
   * @see #queryBundlePreorderLevel
   **/
  public long queryPreorderLevel(String pId)
       throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside queryPreorderLevel: " + pId);

    // bundles are handled differently
    if(isBundle(pId)) {
      return queryBundlePreorderLevel(pId);
    }

    Number value = (Number)getInventoryInfo(pId, getPreorderLevelPropertyName());
    long level;
    if (value != null)
      level = value.longValue();
    else
      level = getDefaultPreorderLevel();

    if(isLoggingDebug())
      logDebug("preorderLevel = " + level);

    return level;
  }

  /**
   * query the availability date of an item.  If the item is a bundle
   * then queryBundleAvailabilityDate
   *
   * @param pId The id of the SKU whose availabilityDate is returned
   * @return The availabilityDate of the SKU
   * @exception InventoryException
   * @see #queryBundleAvailabilityDate
   **/
  public Date queryAvailabilityDate(String pId) throws InventoryException
  {
    // bundles are handled differently
    if(isBundle(pId)) {
      return queryBundleAvailabilityDate(pId);
    }

    Date date = (Date)getInventoryInfo(pId, getAvailabilityDatePropertyName());

    if(isLoggingDebug())
      logDebug("AvailabilityDate for " + pId + " is " + date);

    return date;
  }

  /**
   * With the given id, will attempt to find the object which holds the inventory information.
   * When the object is found it returns the object associated with the given property name.
   *
   * @param pId the id of the repository item to find
   * @param pPropertyName the property value to return
   * @exception InventoryException if there was an error while attempting to return the
   * inventory information. An error can occur if no item can be found with the given id; if the
   * value from the named property is null, or a general RepositoryException occurs.
   * @exception MissingInventoryItemException used if there is no item with the given id
   **/
  private Object getInventoryInfo(String pId, String pPropertyName)
       throws InventoryException
  {
    try {
      RepositoryItem item = getInventoryItem(pId);
      if (item == null)
        throw new MissingInventoryItemException(MessageFormat.format(Constants.NO_SUCH_ITEM,new Object[] {pId}));

      return getPropertyValue(item, pPropertyName);
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
  }

  /**
   * Returns the object associated with the given property name.
   *
   * @param pItem the repository item to fetch the value from
   * @param pPropertyName the property value to return
   * @return The object, null if pItem is null or pPropertyName is null.
   * @exception InventoryException if there was an error while attempting to return the
   * inventory information. An error can occur if no item can be found with the given id; if the
   * value from the named property is null, or a general RepositoryException occurs.
   **/
  private Object getPropertyValue(RepositoryItem pItem, String pPropertyName)
       throws InventoryException
  {
    if ((pItem != null) && (pPropertyName != null))
      return pItem.getPropertyValue(pPropertyName);
    else
      return null;
  }

  /**
   * Send an UpdateInventory message out the UpdateInventoryPort.
   * No changes are made to the inventory.
   *
   * @param pItemIds The items with recently added inventory
   * @return INVENTORY_STATUS_SUCCESS
   * @exception MissingInventoryItemException if a given id does not exist.
   * @exception InventoryException
   * @see javax.jms.JMSException
   **/
  public int inventoryWasUpdated(List pItemIds)
    throws InventoryException
  {
    UpdateInventory ui = new UpdateInventoryImpl();
    String[] items = (String [])pItemIds.toArray(new String[pItemIds.size()]);

    try {
      for(int i=0;i < items.length;i++) {
        RepositoryItem item = getInventoryItem(items[i]);
        if(item == null) {
          String msg = MessageFormat.format(Constants.NO_SUCH_ITEM,new Object[] {items[i]});
          if(isLoggingWarning())
            logWarning(msg);
          throw new MissingInventoryItemException(msg);
        }
      }
    }
    catch(RepositoryException r) {
      if(isLoggingError())
        logError(r);
      throw new InventoryException(r);
    }

    ui.setItemIds(items);
    ui.setSource(getInventoryName());
    ui.setId(getNextMessageId());

    if(isLoggingDebug())
      logDebug("Sending an UpdateInventory message to: " + getUpdateInventoryPort());

    try {
      sendCommerceMessage(ui, getUpdateInventoryPort());
    } catch(javax.jms.JMSException j) {
      if(isLoggingError())
        logError(j);
      throw new InventoryException(j);
    }

    return INVENTORY_STATUS_SUCCEED;
  }

  /**
   * Set an item's stock level threshold.
   *
   * @param pId the id of the SKU involved
   * @param pNumber the number to set the threshold to
   * @return INVENTORY_STATUS_SUCCEED if the item's stock threshold was set successfully.
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND if no item could be found for the SKU.
   * @exception InventoryException
   **/
  public int setStockThreshold (String pId, long pNumber) throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside setStockThreshold: " + pId + " threshold = " + pNumber);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // bundles are handled differently
      if(isBundle(pId)) {
        return INVENTORY_STATUS_SUCCEED;
      }

      MutableRepositoryItem item = getInventoryItemForUpdate(pId);
      if (item == null)
  return INVENTORY_STATUS_ITEM_NOT_FOUND;

      item.setPropertyValue(getStockThresholdPropertyName(), Long.valueOf(pNumber));

      updateItem(item);
      return INVENTORY_STATUS_SUCCEED;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /**
   * Set an item's backorder level threshold.
   *
   * @param pId the id of the SKU involved
   * @param pNumber the number to set the threshold to
   * @return INVENTORY_STATUS_SUCCEED if the item's stock threshold was set successfully.
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND if no item could be found for the SKU.
   * @exception InventoryException
   **/
  public int setBackorderThreshold (String pId, long pNumber) throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside setBackorderThreshold: " + pId + " threshold = " + pNumber);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // bundles are handled differently
      if(isBundle(pId)) {
        return INVENTORY_STATUS_SUCCEED;
      }

      MutableRepositoryItem item = getInventoryItemForUpdate(pId);
      if (item == null)
  return INVENTORY_STATUS_ITEM_NOT_FOUND;

      item.setPropertyValue(getBackorderThresholdPropertyName(), Long.valueOf(pNumber));

      updateItem(item);
      return INVENTORY_STATUS_SUCCEED;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /**
   * Set an item's preorder level threshold.
   *
   * @param pId the id of the SKU involved
   * @param pNumber the number to set the threshold to
   * @return INVENTORY_STATUS_SUCCEED if the item's stock threshold was set successfully.
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND if no item could be found for the SKU.
   * @exception InventoryException
   **/
  public int setPreorderThreshold (String pId, long pNumber) throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside setPreorderThreshold: " + pId + " threshold = " + pNumber);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // bundles are handled differently
      if(isBundle(pId)) {
        return INVENTORY_STATUS_SUCCEED;
      }

      MutableRepositoryItem item = getInventoryItemForUpdate(pId);
      if (item == null)
  return INVENTORY_STATUS_ITEM_NOT_FOUND;

      item.setPropertyValue(getPreorderThresholdPropertyName(), Long.valueOf(pNumber));

      updateItem(item);
      return INVENTORY_STATUS_SUCCEED;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /**
   * Return the threshold associated with stockLevel.
   *
   * @param pId the id of the SKU involved
   * @return The value of the threshold
   * @exception InventoryException
   **/
  public long queryStockThreshold(String pId) throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside queryStockThreshold: " + pId);

    // bundles are handled differently
    if(isBundle(pId)) {
      return queryBundleStockThreshold(pId);
    }

    Number value = (Number)getInventoryInfo(pId, getStockThresholdPropertyName());
    if (value != null)
      return value.longValue();
    else
      return 0;
  }

  /**
   * Return the threshold associated with backorderLevel
   *
   * @param pId the id of the SKU involved
   * @return The value of the threshold
   * @exception InventoryException
   **/
  public long queryBackorderThreshold(String pId) throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside queryBackorderThreshold: " + pId);

    // bundles are handled differently
    if(isBundle(pId)) {
      return queryBundleBackorderThreshold(pId);
    }

    Number value = (Number)getInventoryInfo(pId, getBackorderThresholdPropertyName());
    if (value != null)
      return value.longValue();
    else
      return 0;
  }

  /**
   * Return the threshold associated with preorderLevel
   *
   * @param pId the id of the SKU involved
   * @return The value of the threshold
   * @exception InventoryException
   **/
  public long queryPreorderThreshold(String pId) throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside queryPreorderThreshold: " + pId);

    // bundles are handled differently
    if(isBundle(pId)) {
      return queryBundlePreorderThreshold(pId);
    }

    Number value = (Number)getInventoryInfo(pId, getPreorderThresholdPropertyName());
    if (value != null)
      return value.longValue();
    else
      return 0;
  }

  /**
   * This gets called when some level sinks below the threshold.  It creates an
   * InventoryThresholdReached object and sends it over the inventory event port
   *
   * @param pId The id of the SKU at a critical inventory level.
   * @param pLevelPropertyName The name of the property that is at a critical level
   * @param pThresholdPropertyName The name of the threshold that has been reached.
   * @param pCurrentValue The current value of pLevelPropertyName
   * @param pThreshold The vale of pThresholdPropertyName
   * @exception InventoryException
   * @see InventoryThresholdReached
   * @see #getInventoryEventPort
   **/
  protected void thresholdHasBeenReached(String pId, String pLevelPropertyName,
                                         String pThresholdPropertyName,
                                         long pCurrentValue, long pThreshold)
    throws InventoryException
  {
    InventoryThresholdReached itr = new InventoryThresholdReached();

    itr.setInventoryId(pId);
    itr.setLevelPropertyName(pLevelPropertyName);
    itr.setThresholdPropertyName(pThresholdPropertyName);
    itr.setCurrentValue(pCurrentValue);
    itr.setThresholdValue(pThreshold);
    itr.setId(getNextMessageId());

    if(isLoggingDebug())
      logDebug(itr.toString());

    try {
      sendObjectMessage(itr, itr.getType(), getInventoryEventPort());
    } catch(javax.jms.JMSException j) {
      if(isLoggingError())
        logError("Could not send message: " + itr.getType());
      throw new InventoryException(j);
    }

  }

  /**
   * Derive the availabilityStatus based on the properties of the item
   * Always derives the status, does not check the current value of
   * availabilityStatus.
   * Uses these rules: (a negative level indicates infinite supply)
   * <code>
   *    if(stockLevel != 0)
   *      return IN_STOCK;
   *    else if(backorderLevel != 0)
   *      return BACKORDERABLE;
   *    else if(preorderLevel != 0)
   *      return PREORDERABLE;
   *    else
   *      return OUT_OF_STOCK;
   * </code>
   * @param pId The sku we are deriving the status for
   * @return The derived status
   * @exception InventoryException
   **/
  protected int deriveAvailabilityStatus(String pId)
    throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside deriveAvailabilityStatus: " + pId);

    Number value = null;
    long stockLevel, backorderLevel, preorderLevel;

    // if the stockLevel is not zero, return IN_STOCK
    value = (Number)getInventoryInfo(pId, getStockLevelPropertyName());
    if(value == null) {
      stockLevel = getDefaultStockLevel();
    }
    else {
      stockLevel = value.longValue();
    }
    if(stockLevel != 0) {
      if(isLoggingDebug())
        logDebug("item is in stock");

      return getAvailabilityStatusInStockValue();
    }

    // else, if backorderLevel is not zero, return BACKORDERABLE
    value = (Number)getInventoryInfo(pId, getBackorderLevelPropertyName());
    if(value == null) {
      backorderLevel = getDefaultBackorderLevel();
    }
    else {
      backorderLevel = value.longValue();
    }
    if (backorderLevel != 0) {
      if(isLoggingDebug())
        logDebug("item is backorderable");

      return getAvailabilityStatusBackorderableValue();
    }

    // else, if preorderLevel is not zero, return PREORDERABLE
    value = (Number)getInventoryInfo(pId, getPreorderLevelPropertyName());
    if(value == null) {
      preorderLevel = getDefaultPreorderLevel();
    }
    else {
      preorderLevel = value.longValue();
    }
    if (preorderLevel != 0) {
      if(isLoggingDebug())
        logDebug("item is preorderable");

      return getAvailabilityStatusPreorderableValue();
    }

    if(isLoggingDebug())
      logDebug("no quantities for any level");

    // else return OUT_OF_STOCK
    return getAvailabilityStatusOutOfStockValue();
  }

  /**
   * Returns the next unique Id for commerce messages.
   **/
  public String getNextMessageId()
    throws InventoryException
  {
    IdGenerator idGen = getMessageIdGenerator();
    String space = getMessageIdSpaceName();
    if (idGen == null)
      return null;

    // generate an id in our id space and return it
    try
    {
      return idGen.generateStringId(space);
    }
    catch (IdGeneratorException ie)
    {
      if(isLoggingError())
        logError(ie);
      return null;
    }
  }

  /**
   * Update the item in the inventory
   *
   * @param pItem The inventory's RepositoryItem
   **/
  protected void updateItem(MutableRepositoryItem pItem) throws RepositoryException
  {
    try {
      MutableRepository rep = getRepository();

      // so when this gets commited, the lock field is not set anymore
      String lockPropertyName = getInventoryRowLockProperty();
      pItem.setPropertyValue(lockPropertyName, null);
      rep.updateItem(pItem);
    }
    catch(RepositoryException r) {
      throw r;
    }
  }

  /**
   * To allow multiple instance of Dynamo to simultaneously edit inventory
   * items, we need to make sure the repository item we load is current.  To help
   * ensure this, write to the row in the database.  The database will give the thread
   * the row lock for the table.  Other changes from other machines or Dynamos need to
   * wait until we commit our changes.  This method effectively locks the row.
   * If the id does not exist, the lock call automatically works.
   *
   * @see #getMillisecondDelayBeforeLockRetry
   * @see #getMaximumRetriesPerRowLock
   * @see #getInventoryRowLockProperty
   * @see #getInventoryRowLockSQL
   *
   * @param pItemId The catalog ref id whose inventory we want to change
   * @exception java.sql.SQLException
   **/
  protected void lock(String pItemId) throws java.sql.SQLException
  {
    if(isLoggingDebug())
      logDebug("Getting lock for " + pItemId);

    int retryCount = 0;
    int mostTries = getMaximumRetriesPerRowLock();
    int retryInterval = getMillisecondDelayBeforeLockRetry();
    SQLException lastException = null;
    String lock = DEFAULT_LOCK_NAME;

    Connection c = null;
    PreparedStatement ps = null;
    String sql = getInventoryRowLockSQL();

    int rowcount = 0;
    TransactionManager transactionManager = ((atg.adapter.gsa.GSARepository)getRepository()).getTransactionManager();
    Transaction t = null;
    
    try{
      t = transactionManager.getTransaction();
      if (t == null) {
        transactionManager.begin();
      }
    }
    catch (Throwable exc) {
      if (isLoggingError())
          logError("This attemp to lock " + pItemId + " failed: " + exc.getLocalizedMessage());
      throw new SQLException(exc.getLocalizedMessage());
    }
   
  
    while(retryCount < mostTries) {
      try {
        c = ((atg.adapter.gsa.GSARepository)getRepository()).getDataSource().getConnection();
        ps = c.prepareStatement(sql);
        ps.setString(1, lock);
        ps.setString(2, pItemId);
        rowcount = ps.executeUpdate();
        ps.close();
        //        close the connection
        c.close();
        lastException = null;
        break;
      }
      catch (SQLException s) {
        if(isLoggingDebug())
          logDebug("This attemp to lock " + pItemId + " failed: " + s.getMessage());

        lastException = s;
        retryCount++;
       
      }
      finally{
        try {
          if (t == null)
            transactionManager.commit();
        }
        catch (Throwable exc) {
          if (isLoggingError())
            logError("This attemp to lock " + pItemId + " failed: " + exc.getLocalizedMessage());
          
          lastException = new SQLException(exc.getLocalizedMessage());
          retryCount++;
        }
      }

      
      if(lastException != null){
        try {
          Thread.sleep(retryInterval);
        }
        catch (InterruptedException i) {
          if(isLoggingDebug())
            logDebug("Thread was interrupted.", i);
          // just catch this and continue
        }
      }
      
    }
     
    if(lastException != null)
      throw lastException;
  }

  /**
   * Release the lock for the given id.
   * In this implementation, unlock does nothing since we are relying
   * on the database row locks.  They will be released when the transaction
   * ends.
   **/
  protected void unlock(String pItemId) throws java.sql.SQLException
  {
    return;
  }

  /**
   * For each item in the list, call <code>lock</code>
   * The locks are acquired in sorted order.  Any locks for
   * ids contained within a bundle are also acquired in the
   * correct order.
   * If one of the ids does not exist, it is ignored.
   *
   * @param pItemIds The list of catalog ref ids
   **/
  public void acquireInventoryLocks(List pItemIds) throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Acquiring locks");

    SortedSet ids = new TreeSet();
    int i = 0;
    List skus = new ArrayList(pItemIds);

    while(i<skus.size()) {
      String id = (String) skus.get(i);

      if(isBundle(id)) {
        skus.addAll(getBundledIds(id));
      }
      else {
        ids.add(id);
      }
      i++;
    }

    Iterator sortedIter = ids.iterator();

    while(sortedIter.hasNext()) {
      String id = (String) sortedIter.next();
      try {
        lock(id);
      }
      catch(SQLException s) {
        throw new InventoryException(s);
      }
    }
  }

  /**
   * Release locks for the inventory items that apply to the given
   * ids.
   * This implementation does nothing since we are relying on the
   * database row locks, which are released when the transaction ends.
   **/
  public void releaseInventoryLocks(List pItemIds) throws InventoryException
  {
    return;
  }


  //--------------------------------------------------------------------------
  //--------------------------------------------------------------------------
  //--------------------------------------------------------------------------
  //-------------------------------------
  // bundle specific code
  //-------------------------------------

  /**
   * Return the list of catalog ref ids contained in the bundle.
   **/
  public List getBundledIds(String pBundleId) throws InventoryException
  {
    List ids = new ArrayList();
    Collection bundledItemCol = getBundleLinks(pBundleId);

    // for each item
    Iterator bundledItems = bundledItemCol.iterator();
    while(bundledItems.hasNext()) {
      RepositoryItem bundledItem = (RepositoryItem) bundledItems.next();
      RepositoryItem sku = (RepositoryItem) getPropertyValue(bundledItem, getSkuLinkItemPropertyName());
      ids.add(sku.getRepositoryId());
    }

    return ids;
  }

  /**
   * Return the List of bundle links for the given id.
   *
   * @param pId The id of the sku in question
   * @return A list of bundle links.
   **/
  protected List getBundleLinks(String pId)
       throws InventoryException
  {
    try {
      RepositoryItem item = getCatalogRefItem(pId);
      return getBundleLinks(item);
    } catch(RepositoryException r) {
      throw new InventoryException(r);
    }
  }

  /**
   * Return the List of bundle links for the given id.
   *
   * @param pBundle The sku.
   * @return A list of bundle links.
   **/
  protected List getBundleLinks(RepositoryItem pBundle)
       throws InventoryException
  {
    if (pBundle != null) {
      Object links = pBundle.getPropertyValue(getBundleLinksPropertyName());
      if (isLoggingDebug())
        logDebug("Bundle Links (" + pBundle.getRepositoryId() + "): " + links);
      return (List)links;
    }
    return null;
  }

  /**
   * Checks to see if the given id refers to a bundle.
   * If bundles should be treated just like all other items
   * This method should be overwritten to return false.
   * The criteria for determining if a sku is a bundle, is if
   * the sku has a nonempty bundleLinks list.
   *
   * @param pId The id of the sku in question
   * @return true if the item is a bundle, false otherwise
   **/
  protected boolean isBundle(String pId)
       throws InventoryException
  {
    List bundleLinks = getBundleLinks(pId);
    boolean bundle = ((bundleLinks != null)&& (bundleLinks.size() > 0));
    return bundle;
  }

  /**
   * Derives the availability status for a bundle.  Assumes the given
   * sku represents a bundle.
   * <code>
   * for each item
   *     queryAvailabilityStatus(item)
   *     if any items are AVAILABILITY_STATUS_OUT_OF_STOCK
   *         return AVAILABILITY_STATUS_OUT_OF_STOCK
   *     else if any are AVAILABILITY_STATUS_PREORDERABLE
   *         return AVAILABILITY_STATUS_PREORDERABLE
   *     else if any are AVAILABILITY_STATUS_BACKORDERABLE
   *         return AVAILABILITY_STATUS_BACKORDERABLE
   *     else
   *         return AVAILABILITY_STATUS_IN_STOCK
   * </code>
   *
   * @param pId the id of the bundle
   * @return the availability status according to the above rules.
   * @exception InventoryException
   **/
  protected int deriveBundleAvailabilityStatus(String pBundleId)
    throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside deriveBundleAvailabilityStatus: " + pBundleId);

    long stockLevel = queryBundleStockLevel(pBundleId);
    if(stockLevel != 0)
      return getAvailabilityStatusInStockValue();

    long backorderLevel = queryBundleBackorderLevel(pBundleId);
    if(backorderLevel != 0)
      return getAvailabilityStatusBackorderableValue();

    long preorderLevel = queryBundlePreorderLevel(pBundleId);
    if(preorderLevel != 0)
      return getAvailabilityStatusPreorderableValue();

    return getAvailabilityStatusOutOfStockValue();
  }

  /**
   * Purchases a bundle by purchase each of its bundle items.
   * <code>
   * foreach item
   * queryAvailabilityStatus(bundle)
   * if AVAILABILITY_STATUS_IN_STOCK
   *     for each item
   *         purchase(item, itemQuantity)
   *         if purchase returns INVENTORY_STATUS_INSUFFICIENT_SUPPLY
   *             for each item already purchased
   *                 increaseStockLevel
   *             return INVENTORY_STATUS_INSUFFICIENT_SUPPLY
   *         if purchase returns INVENTORY_STATUS_FAILED
   *             for each item already purchased
   *                 increaseStockLevel
   *             return INVENTORY_STATUS_FAILED
   *     return INVENTORY_STATUS_SUCCEED
   * else
   *     return INVENTORY_STATUS_FAILED
   * </code>
   *
   * @param pBundleId The id of the bundle
   * @param pHowMany the quantity of the bundle to purchase
   * @return The success flag of this operation.
   * @exception InventoryException
   **/
  protected int purchaseBundle(String pBundleId, long pHowMany)
    throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside purchaseBundle: " + pHowMany + " of sku " + pBundleId);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      RepositoryItem bundle = getCatalogRefItem(pBundleId);

      int status = decrementBundleLevel(bundle,
                                        pHowMany,
                                        getStockLevelPropertyName(),
                                        getStockThresholdPropertyName());

      return status;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /**
   * Backorders a bundle. Does the following for the bundle:
   * <code>
   * foreach item
   *         backorder(item, itemQuantity)
   *         if backorder returns INVENTORY_STATUS_INSUFFICIENT_SUPPLY
   *             for each item already backordered
   *                 increaseBackorderLevel
   *             return INVENTORY_STATUS_INSUFFICIENT_SUPPLY
   *         if backorder returns INVENTORY_STATUS_FAILED
   *             for each item already backordered
   *                 increaseBackorderLevel
   *             return INVENTORY_STATUS_FAILED
   *     return INVENTORY_STATUS_SUCCEED
   * else
   *     return INVENTORY_STATUS_FAILED
   * </code>
   *
   * @param pBundleId The id of the bundle we are backordering
   * @param pHowMany the quantity of the bundle to backorder
   * @return a success flag
   **/
  protected int backorderBundle(String pBundleId, long pHowMany)
    throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside backorderBundle: " + pHowMany + " of sku " + pBundleId);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      RepositoryItem bundle = getCatalogRefItem(pBundleId);
      if (bundle == null)
        return INVENTORY_STATUS_ITEM_NOT_FOUND;

      int status = decrementBundleLevel(bundle,
                                        pHowMany,
                                        getBackorderLevelPropertyName(),
                                        getBackorderThresholdPropertyName());

      return status;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /**
   * Preorders a bundle. Does the following for the bundle:
   * <code>
   * foreach item
   *         preorder(item, itemQuantity)
   *         if preorder returns INVENTORY_STATUS_INSUFFICIENT_SUPPLY
   *             for each item already preordered
   *                 increasePreorderLevel
   *             return INVENTORY_STATUS_INSUFFICIENT_SUPPLY
   *         if preorder returns INVENTORY_STATUS_FAILED
   *             for each item already preordered
   *                 increasePreorderLevel
   *             return INVENTORY_STATUS_FAILED
   *     return INVENTORY_STATUS_SUCCEED
   * else
   *     return INVENTORY_STATUS_FAILED
   * </code>
   *
   * @param pBundleId The id of the bundle we are backordering
   * @param pHowMany the quantity of the bundle to backorder
   * @return a success flag
   * @exception InventoryException
   **/
  protected int preorderBundle(String pBundleId, long pHowMany)
    throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside preorderBundle: " + pHowMany + " of sku " + pBundleId);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      RepositoryItem bundle = getCatalogRefItem(pBundleId);
      if (bundle == null)
        return INVENTORY_STATUS_ITEM_NOT_FOUND;

      int status = decrementBundleLevel(bundle,
                                        pHowMany,
                                        getPreorderLevelPropertyName(),
                                        getPreorderThresholdPropertyName());

      return status;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /**
   * Decrement the given property for each of the SKUs within a bundle.
   * It is an all or nothing operation.  If any of the SKUs fails to
   * decrement, no changes to any SKU will be made.  Each sku will be
   * decreased by pHowMany times the number of that SKU in the bundle.
   *
   * @param pBundle The bundle being changed.
   * @param pHowMany The amount to decrease the bundle by.
   * @return INVENTORY_STATUS_INSUFFICIENT_SUPPLY If the level for any of
   *         the included SKUs is insufficient.
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND If any of the SKUs was not found.
   * @return INVENTORY_STATUS_FAIL If any of the SKUs failed to decrease
   * @return INVENTORY_STATUS_SUCCEED If all of the SKUs were updated.
   * @exception InventoryException
   **/
  private int decrementBundleLevel(RepositoryItem pBundle, long pHowMany,
                                   String pPropertyName, String pThresholdName)
    throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside decrementBundleLevel: " + pBundle+ ", " + pHowMany);

    try {
      Collection bundledItemCol = getBundleLinks(pBundle);

      // check the availability status of the bundle
      int availStatus = deriveBundleAvailabilityStatus(pBundle.getRepositoryId());
      int status=0;

      if(pPropertyName.equals(getStockLevelPropertyName()))
        if(queryBundleStockLevel(pBundle.getRepositoryId()) == 0)
          return INVENTORY_STATUS_INSUFFICIENT_SUPPLY;
      if(pPropertyName.equals(getBackorderLevelPropertyName()))
        if(queryBundleBackorderLevel(pBundle.getRepositoryId()) == 0)
          return INVENTORY_STATUS_INSUFFICIENT_SUPPLY;
      if(pPropertyName.equals(getPreorderLevelPropertyName()))
        if(queryBundlePreorderLevel(pBundle.getRepositoryId()) == 0)
          return INVENTORY_STATUS_INSUFFICIENT_SUPPLY;

      List allocatedItems = new ArrayList();

      // purchase each item
      // remember items that were successfully purchase, just in case there was an error
      Iterator bundledItems = bundledItemCol.iterator();
      while(bundledItems.hasNext()) {
        RepositoryItem bundledItem = (RepositoryItem) bundledItems.next();
        // get the quantity included in this bundle times the number of bundles
        Long value = (Long) bundledItem.getPropertyValue(getSkuLinkQuantityPropertyName());
        if(value == null) {
          replaceSkuLinkLevel(allocatedItems, pPropertyName, pHowMany);
          return INVENTORY_STATUS_FAIL;
        }
        long quantity = value.longValue() * pHowMany;

        RepositoryItem sku = (RepositoryItem) bundledItem.getPropertyValue(getSkuLinkItemPropertyName());
        // above sku is from product catalog, below inventoryItem is from Inventory
        MutableRepositoryItem inventoryItem = null;
        if(isBundle(sku.getRepositoryId())) {
          status = decrementBundleLevel(sku, quantity, pPropertyName, pThresholdName);
        }
        else {
          inventoryItem = getInventoryItemForUpdate(sku.getRepositoryId());
          // decrement the stock level
          status = decrementSKULevel(inventoryItem, quantity,
                                     pPropertyName,
                                     pThresholdName);
          if(status == INVENTORY_STATUS_SUCCEED) {
            allocatedItems.add(bundledItem);
          }
          updateItem(inventoryItem);
        }
        if(status == INVENTORY_STATUS_INSUFFICIENT_SUPPLY) {
          replaceSkuLinkLevel(allocatedItems, pPropertyName, pHowMany);
          return status;
        }
        else if(status == INVENTORY_STATUS_FAIL) {
          replaceSkuLinkLevel(allocatedItems, pPropertyName, pHowMany);
          return status;
        }
        else if(status == INVENTORY_STATUS_ITEM_NOT_FOUND) {
          replaceSkuLinkLevel(allocatedItems, pPropertyName, pHowMany);
          return status;
        }
      }

      return INVENTORY_STATUS_SUCCEED;
    }
    catch(RepositoryException r) {
      throw new InventoryException(r);
    }
  }

  /**
   * Purchases the bundle and increments the preorderLevel of each
   * item in the bundle.
   *
   * @param pBundleId The id of the bundle
   * @param pHowMany the quantity of the bundle to purchase
   * @return The success flag of this operation.
   * @exception InventoryException
   **/
  protected int purchaseBundleOffPreorder(String pBundleId, long pHowMany)
    throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside purchaseBundleOffBackorder: " + pHowMany + " of sku " + pBundleId);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      RepositoryItem bundle = getCatalogRefItem(pBundleId);
      if (bundle == null)
        return INVENTORY_STATUS_ITEM_NOT_FOUND;

      int status = purchaseBundleOffPreorderNoLocks(bundle, pHowMany);

      return status;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /**
   * Purchases the bundle then increments the backorderLevel
   * of each item in the bundle.
   *
   * @param pBundleId The id of the bundle
   * @param pHowMany the quantity of the bundle to purchase
   * @return The success flag of this operation.
   * @exception InventoryException
   **/
  protected int purchaseBundleOffBackorder(String pBundleId, long pHowMany)
    throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside purchaseBundleOffBackorder: " + pHowMany + " of sku " + pBundleId);

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
      RepositoryItem bundle = getCatalogRefItem(pBundleId);
      if (bundle == null)
        return INVENTORY_STATUS_ITEM_NOT_FOUND;

      int status = purchaseBundleOffBackorderNoLocks(bundle, pHowMany);

      return status;
    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
    catch(TransactionDemarcationException t) {
      throw new InventoryException(t);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        if(isLoggingError())
          logError(tde);
      }
    }
  }

  /**
   * Decrement the stockLevel for each of the SKUs within a bundle.
   * It is an all or nothing operation.  If any of the SKUs fails to
   * decrement, no changes to any SKU will be made.  Each sku will be
   * decreased by pHowMany times the number of that SKU in the bundle.
   * It then increases the backorder level for each item.
   *
   * @param pBundle The bundle being changed.
   * @param pHowMany The amount to decrease the bundle by.
   * @return INVENTORY_STATUS_INSUFFICIENT_SUPPLY If the level for any of
   *         the included SKUs is insufficient.
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND If any of the SKUs was not found.
   * @return INVENTORY_STATUS_FAIL If any of the SKUs failed to decrease
   * @return INVENTORY_STATUS_SUCCEED If all of the SKUs were updated.
   * @exception InventoryException
   **/
  private int purchaseBundleOffBackorderNoLocks(RepositoryItem pBundle, long pHowMany)
    throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside purchaseBundleOffBackorderNoLocks: " + pHowMany + " of sku " + pBundle);

    try {
      Collection bundledItemCol = getBundleLinks(pBundle);

      // check the availability status of the bundle
      int availStatus = deriveBundleAvailabilityStatus(pBundle.getRepositoryId());
      int status=0;
      if(availStatus == getAvailabilityStatusInStockValue()) {
        List allocatedItems = new ArrayList();
        //List preorderedItems = new ArrayList();
        List backorderedItems = new ArrayList();

        // purchase each item
        // remember items that were successfully purchase, just in case there was an error
        Iterator bundledItems = bundledItemCol.iterator();
        while(bundledItems.hasNext()) {
          RepositoryItem bundledItem = (RepositoryItem) bundledItems.next();
          // get the quantity included in this bundle times the number of bundles
          Long value = (Long) bundledItem.getPropertyValue(getSkuLinkQuantityPropertyName());
          if(value == null) {
            replaceSkuLinkLevel(allocatedItems, getStockLevelPropertyName(), pHowMany);
            return INVENTORY_STATUS_FAIL;
          }
          long quantity = value.longValue() * pHowMany;

          RepositoryItem sku = (RepositoryItem) bundledItem.getPropertyValue(getSkuLinkItemPropertyName());
          // above sku is from product catalog, below inventoryItem is from Inventory
          MutableRepositoryItem inventoryItem = null;
          if(isBundle(sku.getRepositoryId())) {
            status = purchaseBundleOffBackorderNoLocks(sku, quantity);
          }
          else {
            inventoryItem = getInventoryItemForUpdate(sku.getRepositoryId());

            // decrement the stock level
            status = decrementSKULevel(inventoryItem, quantity,
                                       getStockLevelPropertyName(),
                                       getStockThresholdPropertyName());
            if(status == INVENTORY_STATUS_SUCCEED) {
              int backorderStatus = increaseBackorderLevel(inventoryItem, quantity);
              if(backorderStatus != INVENTORY_STATUS_SUCCEED) {
                replaceSkuLinkLevel(allocatedItems, getStockLevelPropertyName(), pHowMany);
                replaceSkuLinkLevel(backorderedItems, getStockLevelPropertyName(), 0-pHowMany);
                updateItem(inventoryItem);
                return backorderStatus;
              }
              backorderedItems.add(bundledItem);
            }
            updateItem(inventoryItem);
          }
          if(status == INVENTORY_STATUS_SUCCEED) {
            allocatedItems.add(bundledItem);
          }
          else {
            replaceSkuLinkLevel(allocatedItems, getStockLevelPropertyName(), pHowMany);
            return status;
          }
        }

        return INVENTORY_STATUS_SUCCEED;
      }
      else {
        return INVENTORY_STATUS_FAIL;
      }

    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
  }

  /**
   * Decrement the stockLevel for each of the SKUs within a bundle.
   * It is an all or nothing operation.  If any of the SKUs fails to
   * decrement, no changes to any SKU will be made.  Each sku will be
   * decreased by pHowMany times the number of that SKU in the bundle.
   * It then increases the preorder level of each item.
   *
   * @param pBundle The bundle being changed.
   * @param pHowMany The amount to decrease the bundle by.
   * @return INVENTORY_STATUS_INSUFFICIENT_SUPPLY If the level for any of
   *         the included SKUs is insufficient.
   * @return INVENTORY_STATUS_ITEM_NOT_FOUND If any of the SKUs was not found.
   * @return INVENTORY_STATUS_FAIL If any of the SKUs failed to decrease
   * @return INVENTORY_STATUS_SUCCEED If all of the SKUs were updated.
   * @exception InventoryException
   **/
  private int purchaseBundleOffPreorderNoLocks(RepositoryItem pBundle, long pHowMany)
    throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside purchaseBundleOffPreorderNoLocks: " + pHowMany + " of sku " + pBundle);

    try {
      Collection bundledItemCol = getBundleLinks(pBundle);

      // check the availability status of the bundle
      int availStatus = deriveBundleAvailabilityStatus(pBundle.getRepositoryId());
      int status=0;
      if(availStatus == getAvailabilityStatusInStockValue()) {
        List allocatedItems = new ArrayList();
        List preorderedItems = new ArrayList();

        // purchase each item
        // remember items that were successfully purchase, just in case there was an error
        Iterator bundledItems = bundledItemCol.iterator();
        while(bundledItems.hasNext()) {
          RepositoryItem bundledItem = (RepositoryItem) bundledItems.next();
          // get the quantity included in this bundle times the number of bundles
          Long value = (Long) bundledItem.getPropertyValue(getSkuLinkQuantityPropertyName());
          if(value == null) {
            replaceSkuLinkLevel(allocatedItems, getStockLevelPropertyName(), pHowMany);
            return INVENTORY_STATUS_FAIL;
          }
          long quantity = value.longValue() * pHowMany;

          RepositoryItem sku = (RepositoryItem) bundledItem.getPropertyValue(getSkuLinkItemPropertyName());
          // above sku is from product catalog, below inventoryItem is from Inventory
          MutableRepositoryItem inventoryItem = null;
          if(isBundle(sku.getRepositoryId())) {
            status = purchaseBundleOffPreorderNoLocks(sku, quantity);
          }
          else {
            inventoryItem = getInventoryItemForUpdate(sku.getRepositoryId());

            // decrement the stock level
            status = decrementSKULevel(inventoryItem, quantity,
                                       getStockLevelPropertyName(),
                                       getStockThresholdPropertyName());
            if(status == INVENTORY_STATUS_SUCCEED) {
              int preorderStatus = increasePreorderLevel(inventoryItem, quantity);
              if(preorderStatus != INVENTORY_STATUS_SUCCEED) {
                replaceSkuLinkLevel(allocatedItems, getStockLevelPropertyName(), pHowMany);
                replaceSkuLinkLevel(preorderedItems, getStockLevelPropertyName(), 0-pHowMany);
                updateItem(inventoryItem);
                return preorderStatus;
              }
              preorderedItems.add(bundledItem);
            }
            updateItem(inventoryItem);
          }
          if(status == INVENTORY_STATUS_SUCCEED) {
            allocatedItems.add(bundledItem);
          }
          else {
            replaceSkuLinkLevel(allocatedItems, getStockLevelPropertyName(), pHowMany);
            return status;
          }
        }

        return INVENTORY_STATUS_SUCCEED;
      }
      else {
        return INVENTORY_STATUS_FAIL;
      }

    }
    catch (RepositoryException e) {
      throw new InventoryException(e);
    }
  }

  /**
   * Return a sorted list of the items within a bundle (by id)
   * including all items within nested bundles
   *
   * @param pItem The item that is a bundle
   * @return An iterator of the items, sorted by id.
   * @exception InventoryException
   **/
  private SortedSet getNestedBundledItemsSorted(RepositoryItem pItem)
    throws InventoryException
  {
    List bundleLinks = getBundleLinks(pItem);

    if(bundleLinks == null)
      return null;

    SortedSet bundledIds = new TreeSet(mSkuLinkComparator);
    ListIterator links = bundleLinks.listIterator();

    while(links.hasNext()) {
      RepositoryItem link = (RepositoryItem) links.next();
      if(isBundle(link.getRepositoryId())) {
        List nestedIds = getBundleLinks(link);
        bundleLinks.addAll(nestedIds);
      }
      bundledIds.add(link);
    }

    return bundledIds;
  }

  /**
   * replace the stock for each of the given sku-links.
   * does not update the repository
   *
   * @param pSkuLinkList The list of SKU links to replace
   * @param pLevelPropertyName the name of the property to increment
   * @param pHowMany The number of bundles to replace
   * @exception InventoryException
   **/
  private void replaceSkuLinkLevel(List pBundledItems, String pLevelPropertyName, long pHowMany)
    throws InventoryException
  {
    for (int c=0; c<pBundledItems.size(); c++) {
      RepositoryItem bundledItem = (RepositoryItem) pBundledItems.get(c);
      RepositoryItem sku = (RepositoryItem) getPropertyValue(bundledItem, getSkuLinkItemPropertyName());
      // get the quantity included in this bundle
      Long value = (Long) bundledItem.getPropertyValue(getSkuLinkQuantityPropertyName());
      if(value == null) {
        if(isLoggingError())
          logError("Could not get quantity from sku link + " + bundledItem.getRepositoryId());
        continue;
      }
      long quantity = value.longValue() * pHowMany;

      try {
        MutableRepositoryItem inventoryItem = getInventoryItemForUpdate(sku.getRepositoryId());

        // decrement the stock level
        if(pLevelPropertyName.equals(getStockLevelPropertyName())) {
          int status = increaseStockLevel(inventoryItem, quantity);
          if(status != INVENTORY_STATUS_SUCCEED) {
            if(isLoggingError())
              logError("Unable to replace inventory for " + sku.getRepositoryId());
          }
        }
        else if(pLevelPropertyName.equals(getBackorderLevelPropertyName())) {
          int status = increaseBackorderLevel(inventoryItem, quantity);
          if(status != INVENTORY_STATUS_SUCCEED) {
            if(isLoggingError())
              logError("Unable to replace inventory for " + sku.getRepositoryId());
          }
        }
        else if(pLevelPropertyName.equals(getPreorderLevelPropertyName())) {
          int status = increasePreorderLevel(inventoryItem, quantity);
          if(status != INVENTORY_STATUS_SUCCEED) {
            if(isLoggingError())
              logError("Unable to replace inventory for " + sku.getRepositoryId());
          }
        }
      }
      catch (RepositoryException exc) {
        throw new InventoryException(exc);
      }
    }
  }


  /**
   * Cycles through the items in a bundle and returns the greatest
   * quantity of bundles that could be allocated given each items
   * stock level.  Takes into account the quantity of each item in the
   * bundle.  If the bundle calls for 5 of item A and 1 of item B and
   * item A has a stock level of 10 and item B has a stock level of
   * 10.  This method will return 2 since that is the number of
   * bundles that those stock levels can support.
   *
   * @param pId The id of the bundle
   * @return the largest quantity of the bundle that can be allocated
   * @exception InventoryException
   **/
  protected long queryBundleStockLevel(String pId)
       throws InventoryException
  {
    return queryBundleLevel(pId, getStockLevelPropertyName());
  }

  /**
   * Cycles through the items in a bundle and returns the greatest
   * quantity of bundles that could be allocated given each items
   * backorder level.  Takes into account the quantity of each item in
   * the bundle.  If the bundle calls for 5 of item A and 1 of item B
   * and item A has a backorder level of 10 and item B has a backorder
   * level of 10.  This method will return 2 since that is the number
   * of bundles that those backorder levels can support.
   *
   * @param pId The id of the bundle
   * @return the largest quantity of the bundle that can be backordered
   * @exception InventoryException
   **/
  protected long queryBundleBackorderLevel(String pId)
       throws InventoryException
  {
    return queryBundleLevel(pId, getBackorderLevelPropertyName());
  }

  /**
   * Cycles through the items in a bundle and returns the greatest
   * quantity of bundles that could be preordered given each items
   * backorder level.  Takes into account the quantity of each item in
   * the bundle.  If the bundle calls for 5 of item A and 1 of item B
   * and item A has a preorder level of 10 and item B has a preorder
   * level of 10.  This method will return 2 since that is the number
   * of bundles that those preorder levels can support.
   *
   * @param pId The id of the bundle
   * @return the largest quantity of the bundle that can be preordered
   * @exception InventoryException
   **/
  protected long queryBundlePreorderLevel(String pId)
       throws InventoryException
  {
    return queryBundleLevel(pId, getPreorderLevelPropertyName());
  }

  /**
   * Currently all bundles have thresholds of 0.  If this behavior should
   * change, override this method.
   *
   * @param pId The id of the bundle
   * @return 0
   * @exception InventoryException
   **/
  protected long queryBundleStockThreshold(String pId)
       throws InventoryException
  {
    return 0;
  }

  /**
   * Currently all bundles have thresholds of 0.  If this behavior should
   * change, override this method.
   *
   * @param pId The id of the bundle
   * @return 0
   * @exception InventoryException
   **/
  protected long queryBundleBackorderThreshold(String pId)
       throws InventoryException
  {
    return 0;
  }

  /**
   * Currently all bundles have thresholds of 0.  If this behavior should
   * change, override this method.
   *
   * @param pId The id of the bundle
   * @return 0
   * @exception InventoryException
   **/
  protected long queryBundlePreorderThreshold(String pId)
       throws InventoryException
  {
    return 0;
  }

  /**
   * Cycles through the items in a bundle and returns the greatest
   * quantity of bundles that could be allocated given each items
   * pPropertyName level.  Takes into account the quantity of each
   * item in the bundle.  If the bundle calls for 5 of item A and 1 of
   * item B and item A has a level of 10 and item B has a level of 10.
   * This method will return 2 since that is the number of bundles
   * that those levels can support.
   *
   * @param pId The id of the bundle
   * @param pPropertyName The name of the property to check
   * @return the largest quantity of the bundle that can be processed
   * @exception InventoryException
   * @exception MissingInventoryException
   **/
  private long queryBundleLevel(String pBundleId, String pPropertyName)
    throws InventoryException
  {
    if(isLoggingDebug())
      logDebug("Inside queryBundleLevel: " + pPropertyName + " for " + pBundleId);

    try {
      RepositoryItem bundle = getCatalogRefItem(pBundleId);
      if (bundle == null)
        throw new MissingInventoryItemException(MessageFormat.format(Constants.NO_SUCH_ITEM,new Object[] {pBundleId}));

      Collection bundledItemCol = getBundleLinks(bundle);

      long smallestSoFar = -1;

      // for each item
      Iterator bundledItems = bundledItemCol.iterator();
      while(bundledItems.hasNext()) {
        RepositoryItem bundledItem = (RepositoryItem) bundledItems.next();
        RepositoryItem sku = (RepositoryItem) getPropertyValue(bundledItem, getSkuLinkItemPropertyName());
        // get the quantity included in this bundle times the number of bundles
        Long value = (Long) bundledItem.getPropertyValue(getSkuLinkQuantityPropertyName());
        if(value == null) {
          // there is no default value for a bundled quantity
          return 0;
        }
        long bundleQuantity = value.longValue();
        RepositoryItem inventoryItem = getInventoryItem(sku.getRepositoryId());

        // get the level of the item
        long skuQuantity = 0;

        if(pPropertyName.equals(getStockLevelPropertyName()))
          skuQuantity = queryStockLevel(sku.getRepositoryId());
        else if(pPropertyName.equals(getBackorderLevelPropertyName()))
          skuQuantity = queryBackorderLevel(sku.getRepositoryId());
        else if(pPropertyName.equals(getPreorderLevelPropertyName()))
          skuQuantity = queryPreorderLevel(sku.getRepositoryId());
        else return 0; // should never happen.

        long canBeBundled = 0;

        if(skuQuantity >= bundleQuantity) {
          canBeBundled = skuQuantity/bundleQuantity;
        }
        else if(skuQuantity == -1) {
          canBeBundled = -1;
        }

        // -1 is actually a very big number (infinity)
        if((canBeBundled != -1) && (canBeBundled < smallestSoFar)) {
          smallestSoFar = canBeBundled;
        }
        else if(smallestSoFar == -1) {
          smallestSoFar = canBeBundled;
        }
      }

      return smallestSoFar;
    }
    catch(RepositoryException r) {
      if(isLoggingError())
        logError(r);
      throw new InventoryException(r);
    }
  }

  /**
   * Cycles through each item in a bundle and calls
   * queryAvailabilityDate on each.  This method returns
   * the latest of those dates.
   *
   * @param pBundleId The id of the bundle
   * @return The availabilityDate of the bundle
   * @exception InventoryException
   * @exception MissingInventoryItemException
   * @see #queryAvailabilityDate
   **/
  public Date queryBundleAvailabilityDate(String pBundleId) throws InventoryException
  {
    try {
      RepositoryItem bundle = getCatalogRefItem(pBundleId);
      if (bundle == null)
        throw new MissingInventoryItemException(MessageFormat.format(Constants.NO_SUCH_ITEM,new Object[] {pBundleId}));
      Collection bundledItemCol = getBundleLinks(bundle);

      Date latestSoFar = null;

      // for each item
      Iterator bundledItems = bundledItemCol.iterator();
      while(bundledItems.hasNext()) {
        RepositoryItem bundledItem = (RepositoryItem) bundledItems.next();
        RepositoryItem sku = (RepositoryItem) getPropertyValue(bundledItem, getSkuLinkItemPropertyName());
        RepositoryItem inventoryItem = getInventoryItem(sku.getRepositoryId());
        // get the availabilityDate of the item
        Date skuDate = queryAvailabilityDate(sku.getRepositoryId());
        if(skuDate != null)
          if(latestSoFar != null) {
            if(skuDate.compareTo(latestSoFar) > 0)
              latestSoFar = skuDate;
          }
          else {
            latestSoFar = skuDate;
          }
      }
      return latestSoFar;
    } catch(RepositoryException r) {
      if(isLoggingError())
        logError(r);
      throw new InventoryException(r);
    }
  }

  /**
   * get the locks for all the items in a bundle
   * return the list of locks
   *
   * @param pBundle The bundle
   * @return The list of locks for each item contained in the bundle
   * @exception InventoryException
   * @exception LockManagerException
   * @deprecated Locks are no longer used within the RepositoryInventoryManager
   **/
  private List getLocksForBundle(RepositoryItem pBundle)
    throws InventoryException, DeadlockException
  {
    return null;
  }

}
