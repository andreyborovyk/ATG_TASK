/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2010 Art Technology Group, Inc.
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
package atg.projects.store.inventory;

import atg.commerce.inventory.InventoryException;
import atg.commerce.inventory.RepositoryInventoryManager;
import atg.projects.store.catalog.StoreCatalogProperties;
import atg.projects.store.profile.StorePropertyManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * This class is an extension of the RepositoryInventoryManager. It will be responsible for
 * writing to the inventory repository. This should only happen from the fulfillment server.
 *
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/inventory/StoreInventoryManager.java#3 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class StoreInventoryManager extends RepositoryInventoryManager {

  public static final String PARAM_SITE_ID = "siteId";
  
  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/inventory/StoreInventoryManager.java#3 $$Change: 635816 $";

  /**
   * RQL query for duplicating item.
   */
  public static String RQL_QUERY_DUPLICATE_BACK_IN_STOCK_ITEM = "catalogRefId = ?0 AND emailAddress = ?1 AND productId = ?2";

  /**
   * Default threshold level constant.
   */
  public static final long DEFAULT_THRESHOLD_LEVEL = 0;

  /**
   * Default threshold level.
   */
  private long mDefaultThresholdLevel = DEFAULT_THRESHOLD_LEVEL;

  /**
   * Order repository.
   */
  private Repository mOrderRepository = null;

  /**
   * Catalog properties.
   */
  private StoreCatalogProperties mCatalogProperties;
  
  //--------------------------------------------------
  // property: PropertyManager
  private StorePropertyManager mPropertyManager;

  /**
   * @return the StorePropertyManager
   */
  public StorePropertyManager getPropertyManager() {
    return mPropertyManager;
  }

  /**
   * @param PropertyManager the StorePropertyManager to set
   */
  public void setPropertyManager(StorePropertyManager pPropertyManager) {
    mPropertyManager = pPropertyManager;
  }
  
  /**
   * Default value if the stock level of an item is not defined.
   *
   * @param pDefaultThresholdLevel default threshold level
   */
  public void setDefaultThresholdLevel(long pDefaultThresholdLevel) {
    mDefaultThresholdLevel = pDefaultThresholdLevel;
  }

  /**
   * The default value if the stock level of an item is not defined.
   * @return default threshold level
   */
  public long getDefaultThresholdLevel() {
    return mDefaultThresholdLevel;
  }

  /**
   * @return order repository.
   */
  public Repository getOrderRepository() {
    return mOrderRepository;
  }

  /**
   * @param pOrderRepository - order repository.
   */
  public void setOrderRepository(Repository pOrderRepository) {
    mOrderRepository = pOrderRepository;
  }

  /**
   * @return catalog properties.
   */
  public StoreCatalogProperties getCatalogProperties() {
    return mCatalogProperties;
  }

  /**
   * @param pCatalogProperties -  catalog properties to set.
   */
  public void setCatalogProperties(StoreCatalogProperties pCatalogProperties) {
    mCatalogProperties = pCatalogProperties;
  }
  
  
  /**
   * This method will send an "UpdateInventory" message if the item changes from Out of stock to In stock with this
   * method.
   *
   * @param  skuId The id of the SKU whose stockLevel is being set
   * @param  quantity The amount to set the stockLevel to.
   * @return INVENTORY_STATUS_SUCCEED if the item's stock level was set successfully. INVENTORY_STATUS_ITEM_NOT_FOUND
   *         if no item could be found for the SKU.
   * @throws InventoryException if there was an error while attempting to return
   * the inventory information
   */
  public int setStockLevel(String skuId, long quantity)
    throws InventoryException {
    if (isLoggingDebug()) {
      logDebug("setStockLevel " + skuId + ", " + quantity);
    }

    int oldStatus = queryAvailabilityStatus(skuId);
    int result = super.setStockLevel(skuId, quantity);
    int newStatus = queryAvailabilityStatus(skuId);

    if ((oldStatus == AVAILABILITY_STATUS_OUT_OF_STOCK) && (newStatus == AVAILABILITY_STATUS_IN_STOCK)) {
      List list = new ArrayList(1);
      list.add(skuId);
      inventoryWasUpdated(list);
    }

    return result;
  }

  /**
   * This method will send an "UpdateInventory" message if the item changes from Out of stock to In stock with this
   * method.
   *
   * @param  skuId The id of the SKU whose stockLevel is being increased.
   * @param  quantity The amount to increase the stockLevel to.
   * @return INVENTORY_STATUS_SUCCEED if the item's stock level was set successfully. INVENTORY_STATUS_ITEM_NOT_FOUND
   *         if no item could be found for the SKU.
   * @throws InventoryException if there was an error while attempting to return
   * the inventory information
   */
  public int increaseStockLevel(String skuId, long quantity)
    throws InventoryException {
    if (isLoggingDebug()) {
      logDebug("increaseStockLevel " + skuId + ", " + quantity);
    }

    int oldStatus = queryAvailabilityStatus(skuId);
    int result = super.increaseStockLevel(skuId, quantity);
    int newStatus = queryAvailabilityStatus(skuId);

    if ((oldStatus == AVAILABILITY_STATUS_OUT_OF_STOCK) && (newStatus == AVAILABILITY_STATUS_IN_STOCK)) {
      List list = new ArrayList(1);
      list.add(skuId);
      inventoryWasUpdated(list);
    }

    return result;
  }

  /**
   * Derive the availabilityStatus based on the properties of the item.  Always derives the status, does not check the
   * current value of availabilityStatus. Uses these rules: (a negative level indicates infinite supply)
   * <code>if (stockLevel > stockThreshold) return IN_STOCK;
   * else if (backorderLevel > backorderThreshold) return BACKORDERABLE;
   * else if (preorderLevel > preorderThreshold) return PREORDERABLE;
   * else return OUT_OF_STOCK; </code>
   *
   * @param  skuId The sku we are deriving the status for
   * @return The derived status
   * @throws InventoryException if there was an error while attempting to return
   * the inventory information
   */
  protected int deriveAvailabilityStatus(String skuId)
    throws InventoryException {
    if (isLoggingDebug()) {
      logDebug("Inside deriveAvailabilityStatus: " + skuId);
    }

    RepositoryItem inventory;

    try {
      inventory = getInventoryItem(skuId);
    } catch (RepositoryException e) {
      throw new InventoryException(e);
    }

    long stockLevel;
    long backorderLevel;
    long preorderLevel;
    long stockThreshold;
    long backorderThreshold;
    long preorderThreshold;

    stockLevel = getInventoryLevel(inventory, getStockLevelPropertyName(), getDefaultStockLevel());
    stockThreshold = getInventoryLevel(inventory, getStockThresholdPropertyName(), getDefaultThresholdLevel());
    backorderLevel = getInventoryLevel(inventory, getBackorderLevelPropertyName(), getDefaultBackorderLevel());
    backorderThreshold = getInventoryLevel(inventory, getBackorderThresholdPropertyName(), getDefaultThresholdLevel());
    preorderLevel = getInventoryLevel(inventory, getPreorderLevelPropertyName(), getDefaultPreorderLevel());
    preorderThreshold = getInventoryLevel(inventory, getPreorderThresholdPropertyName(), getDefaultThresholdLevel());

    if (isLoggingDebug()) {
      logDebug("stockLevel=" + stockLevel);
      logDebug("stockThreshold=" + stockThreshold);
      logDebug("backorderLevel=" + backorderLevel);
      logDebug("backorderThreshold=" + backorderThreshold);
      logDebug("preorderLevel=" + preorderLevel);
      logDebug("preorderThreshold=" + preorderThreshold);
    }

    if ((stockLevel == -1) || (stockLevel > stockThreshold)) {
      if (isLoggingDebug()) {
        logDebug("item is in stock");
      }

      return getAvailabilityStatusInStockValue();
    } else if ((backorderLevel == -1) || (backorderLevel > backorderThreshold)) {
      if (isLoggingDebug()) {
        logDebug("item is backorderable");
      }

      return getAvailabilityStatusBackorderableValue();
    } else if ((preorderLevel == -1) || (preorderLevel > preorderThreshold)) {
      if (isLoggingDebug()) {
        logDebug("item is preorderable");
      }

      return getAvailabilityStatusPreorderableValue();
    } else {
      if (isLoggingDebug()) {
        logDebug("no quantities for any level");
      }

      return getAvailabilityStatusOutOfStockValue();
    }
  }

  /**
   * Derive the availabilityStatus based on the properties of the product and the item.
   * Always derives the status, does not check the current value of availabilityStatus.
   *
   * Preorderability is checked first.  Preorderability can be set based just on product
   * properties or on a combination of product properties and inventory levels.  The end
   * date for preorderability can be set to a fixed date or use stockLevel to determine when
   * it ends.
   *
   * If an item is not preorderable, then the inventory levels are checked to see if it
   * is available, backorderable or unavailable.
   *
   * @param  pProduct The product we are deriving the status for
   * @param  pSkuId The sku we are deriving the status for
   * @return The derived status
   * @throws InventoryException if there was an error while attempting to return
   * the inventory information
   */
  public int queryAvailabilityStatus(RepositoryItem pProduct, String pSkuId)
    throws InventoryException {
    if (isLoggingDebug()) {
      logDebug("Inside newDeriveAvailabilityStatus: " + pSkuId);
    }

    RepositoryItem inventory;

    try {
      inventory = getInventoryItem(pSkuId);
    } catch (RepositoryException e) {
      throw new InventoryException(e);
    }

    StoreCatalogProperties catalogProps = getCatalogProperties();
    Boolean preorderable = (Boolean) pProduct.getPropertyValue(catalogProps.getPreorderablePropertyName());
    Date preorderEndDate = (Date) pProduct.getPropertyValue(catalogProps.getPreorderEndDatePropertyName());
    Boolean useInventoryForPreorder = (Boolean) pProduct.getPropertyValue(catalogProps.getUseInventoryForPreorderPropertyName());
    long stockLevel = getInventoryLevel(inventory, getStockLevelPropertyName(), getDefaultStockLevel());
    long stockThreshold = getInventoryLevel(inventory, getStockThresholdPropertyName(), getDefaultThresholdLevel());
    long backorderLevel = getInventoryLevel(inventory, getBackorderLevelPropertyName(), getDefaultBackorderLevel());
    long backorderThreshold = getInventoryLevel(inventory, getBackorderThresholdPropertyName(),
        getDefaultThresholdLevel());
    long preorderLevel = getInventoryLevel(inventory, getPreorderLevelPropertyName(), getDefaultPreorderLevel());
    long preorderThreshold = getInventoryLevel(inventory, getPreorderThresholdPropertyName(), getDefaultThresholdLevel());

    if (isLoggingDebug()) {
      logDebug("preorderable=" + preorderable);
      logDebug("preorderEndDate=" + preorderEndDate);
      logDebug("useInventoryForPreorder=" + useInventoryForPreorder);
      logDebug("stockLevel=" + stockLevel);
      logDebug("stockThreshold=" + stockThreshold);
      logDebug("backorderLevel=" + backorderLevel);
      logDebug("backorderThreshold=" + backorderThreshold);
      logDebug("preorderLevel=" + preorderLevel);
      logDebug("preorderThreshold=" + preorderThreshold);
    }

    // Check to see if the item is marked as pre-orderable.  If so, then it
    // either can be pre-ordered or else is has "sold out" of the pre-order limit.
    if (preorderable != null && preorderable.booleanValue() &&
        (((preorderEndDate != null) && preorderEndDate.after(new Date())) ||
        ((preorderEndDate == null) &&
        (!useInventoryForPreorder.booleanValue() || ((stockLevel == 0) && (preorderLevel != 0)))))) {
      if (!useInventoryForPreorder.booleanValue() || (preorderLevel == -1) || (preorderLevel > preorderThreshold)) {
        if (isLoggingDebug()) {
          logDebug("item is preorderable");
        }

        return getAvailabilityStatusPreorderableValue();
      } else {
        if (isLoggingDebug()) {
          logDebug("item is out of stock (preorders used up)");
        }

        return getAvailabilityStatusOutOfStockValue();
      }
    }

    // Once we've reached here the item cannot be pre-orderable, so we can ignore the preorderLevel.
    if ((stockLevel == -1) || (stockLevel > stockThreshold)) {
      if (isLoggingDebug()) {
        logDebug("item is in stock");
      }

      return getAvailabilityStatusInStockValue();
    } else if ((backorderLevel > backorderThreshold) || (backorderLevel == -1)) {
      if (isLoggingDebug()) {
        logDebug("item is backorderable");
      }

      return getAvailabilityStatusBackorderableValue();
    } else {
      if (isLoggingDebug()) {
        logDebug("item is out of stock (normal stock)");
      }

      return getAvailabilityStatusOutOfStockValue();
    }
  }

  /**
   * Get the availabilityDate for a product.
   *
   * @param  pProduct The product we are getting the availability date for
   * @return The availabilityDate which may be null
   */
  public Date getPreorderAvailabilityDate(RepositoryItem pProduct) {
    if (isLoggingDebug()) {
      logDebug("Inside getPreorderAvailabilityDate");
    }

    return (Date) pProduct.getPropertyValue(getCatalogProperties().getPreorderEndDatePropertyName());
  }

  /**
   * Get the availabilityDate from the inventory data for a sku item.
   *
   * @param  pSkuId The sku we are getting the availability date for
   * @return The availabilityDate which may be null
   * @throws InventoryException if there was an error while attempting to return
   * the inventory information
   */
  public Date getBackorderAvailabilityDate(String pSkuId)
    throws InventoryException {
    if (isLoggingDebug()) {
      logDebug("Inside getBackorderAvailabilityDate: " + pSkuId);
    }

    RepositoryItem inventory;

    try {
      inventory = getInventoryItem(pSkuId);
    } catch (RepositoryException e) {
      throw new InventoryException(e);
    }

    return (Date) inventory.getPropertyValue(getAvailabilityDatePropertyName());
  }

  /**
   * Given the inventory item of note get the inventory level for the particular property.
   *
   * @param inventory - The inventory repository Item to work with
   * @param inventoryLevelPropertyName - the property name of the inventory property that denotes the level
   * @param defaultLevel a default level if the property is unset
   * @return Returns the inventory level stored for the property or the default if not found.
   * @throws atg.commerce.inventory.InventoryException if there was an error while attempting
   *  to return the inventory information
   */
  protected long getInventoryLevel(RepositoryItem inventory, String inventoryLevelPropertyName, long defaultLevel)
    throws InventoryException {
    long level = 0;
    Long value = (Long) getPropertyValue(inventory, inventoryLevelPropertyName);

    if (value == null) {
      level = defaultLevel;
    } else {
      level = value.longValue();
    }

    return level;
  }

  /**
   * Returns the object associated with the given property name.
   *
   * @param  item the repository item to fetch the value from
   * @param  propertyName the property value to return
   * @return The object, null if pItem is null or pPropertyName is null.
   * @throws InventoryException if there was an error while attempting to return the inventory information. An
   *         error can occur if no item can be found with the given id; if the value from the named property is
   *         null, or a general RepositoryException occurs.
   */
  protected Object getPropertyValue(RepositoryItem item, String propertyName)
    throws InventoryException {
    if ((item != null) && (propertyName != null)) {
      return item.getPropertyValue(propertyName);
    } else {
      return null;
    }
  }
  
  /**
   * Check to see if backInStoreNotifyItem already exists for this combination
   * of CatalogRefId and email.
   * 
   * @param pRepository Repository where to check if item exists
   * @param pCatalogRefId repository id
   * @param pEmail string that represents email
   * @param pProductId product id
   * @param pItemDescriptor item descriptor
   * @return true if item exists
   * @throws RepositoryException if there was an error while creating repository item
   */
  public boolean isBackInStockItemExists(MutableRepository pRepository, String pCatalogRefId, String pEmail, String pProductId) 
  throws RepositoryException {
    boolean isExist = false;
    
    RepositoryView view = pRepository.getView(getPropertyManager().getBackInStockNotifyItemDescriptorName());
    Object[] params = new Object[] { pCatalogRefId, pEmail, pProductId };
    RqlStatement statement = RqlStatement.parseRqlStatement(RQL_QUERY_DUPLICATE_BACK_IN_STOCK_ITEM);
    RepositoryItem[] items = statement.executeQuery(view, params);

    isExist = (items != null) && (items.length > 0);

    return isExist;
  }
  
  /**
   * Creates the required item in the repository.
   * 
   * @param pRepository Repository where to create item
   * @param pCatalogRefId repository id
   * @param pEmail string that represents email
   * @param pProductId product id
   * @throws RepositoryException if there was an error while creating repository item
   */
  protected void createBackInStockNotifyItem(MutableRepository pRepository, String pCatalogRefId, String pEmail, String pProductId)
      throws RepositoryException {
    createBackInStockNotifyItem(pRepository, pCatalogRefId, pEmail, pProductId, null, null);
  }
  
  protected void createBackInStockNotifyItem(MutableRepository pRepository, String pCatalogRefId, String pEmail, String pProductId,
      String pLocale, String pSiteId) throws RepositoryException 
  {
    String itemDescriptor = getPropertyManager()
        .getBackInStockNotifyItemDescriptorName();
    String skuIdProp = getPropertyManager().getBisnSkuIdPropertyName();
    String emailProp = getPropertyManager().getBisnEmailPropertyName();
    String productIdProp = getPropertyManager().getBisnProductIdPropertyName();
    
    MutableRepositoryItem item = pRepository.createItem(itemDescriptor);
    item.setPropertyValue(skuIdProp, pCatalogRefId);
    item.setPropertyValue(emailProp, pEmail);
    item.setPropertyValue(productIdProp, pProductId);
    item.setPropertyValue(getPropertyManager().getLocalePropertyName(), pLocale);
    item.setPropertyValue(PARAM_SITE_ID, pSiteId);
    pRepository.addItem(item);
  }
}
