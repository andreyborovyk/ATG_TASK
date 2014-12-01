/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2011 Art Technology Group, Inc.
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
package atg.projects.store.order.processor;

import atg.commerce.inventory.InventoryException;
import atg.commerce.inventory.InventoryInfo;
import atg.commerce.inventory.InventoryManager;

import atg.commerce.order.*;

import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;

import atg.nucleus.logging.ApplicationLoggingImpl;

import atg.projects.store.inventory.StoreInventoryManager;

import atg.repository.RepositoryItem;

import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import java.text.MessageFormat;

import java.util.*;


/**
 *
 * <p>Determine if the item is available by ensuring that at
 * least 1 is available for preorder, backorder or immediate
 * availability.
 * <p>This does not check the quantity being purchased with the
 * levels available and the threshold levels.  The OMS will do
 * that since it has more up to the minute inventory data.  The
 * order will be accepted as long as the inventory data shows that
 * it can be at least partially fulfilled.
 * @author ATG
 * @version $Revision: #2 $
 */
public class ProcValidateInventoryForCheckout extends ApplicationLoggingImpl implements PipelineProcessor {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/processor/ProcValidateInventoryForCheckout.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------    
  /**
   * Item of stock resource key.
   */
  public static final String ITEM_OUT_OF_STOCK = "itemOutOfStock";

  /**
   * Resource bundle name.
   */
  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /**
   * User messages resource bundle name.
   */
  static final String USER_MSGS_RES_NAME = "atg.commerce.order.UserMessages";

  // Resource message keys
  public static final String MSG_INVALID_ORDER_PARAMETER = "InvalidOrderParameter";

  /** Resource Bundle. **/
  private static java.util.ResourceBundle sResourceBundle = LayeredResourceBundle.getBundle(MY_RESOURCE_NAME,
      atg.service.dynamo.LangLicense.getLicensedDefault());

  /**
   * Success constant.
   */
  private static final int SUCCESS = 1;

  /**
   * Logging identifier.
   */
  String mLoggingIdentifier = "ProcValidateInventoryForCheckout";

  /**
   * Sets property LoggingIdentifier.
   * @param pLoggingIdentifier - logging identifier
   */
  public void setLoggingIdentifier(String pLoggingIdentifier) {
    mLoggingIdentifier = pLoggingIdentifier;
  }

  /**
   * @return property LoggingIdentifier.
   */
  public String getLoggingIdentifier() {
    return mLoggingIdentifier;
  }

  /**
   * Validates that there is inventory for all items in the order.
   *
   * @param pParam a HashMap which must contain an Order and optionally a Locale object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   * @see atg.commerce.pricing.AmountInfo
   * @see atg.commerce.pricing.OrderPriceInfo
   * @see atg.commerce.pricing.TaxPriceInfo
   * @see atg.commerce.pricing.ItemPriceInfo
   * @see atg.commerce.pricing.ShippingPriceInfo
   */
  public int runProcess(Object pParam, PipelineResult pResult)
    throws Exception {
    boolean inventoryAvailable = true;

    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    OrderManager om = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);
    InventoryManager inventoryManager = om.getOrderTools().getInventoryManager();

    if (om == null) {
      throw new InvalidParameterException("OrderManager is null");
    }

    if (order == null) {
      throw new InvalidParameterException(ResourceUtils.getMsgResource(MSG_INVALID_ORDER_PARAMETER, MY_RESOURCE_NAME,
          sResourceBundle));
    }

    ResourceBundle bundle = null;
    Locale resourceLocale = (Locale) map.get(PipelineConstants.LOCALE);
    bundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, resourceLocale);

    List items = order.getCommerceItems();

    for (int j = 0; j < items.size(); j++) {
      CommerceItem item = (CommerceItem) items.get(j);

      if (!itemIsAvailable(item, inventoryManager)) {
        String catalogRefId = item.getCatalogRefId();

        if (isLoggingDebug()) {
          logDebug("None of this item in stock, preorderable or backorderable: " + catalogRefId);
        }

        RepositoryItem ciRep = (RepositoryItem) item.getAuxiliaryData().getCatalogRef();
        String name = (String) ciRep.getPropertyValue("displayName");

        Object[] objParams = { name };
        String formattedMsg = MessageFormat.format(bundle.getString(ITEM_OUT_OF_STOCK), objParams);

        addHashedError(pResult, ITEM_OUT_OF_STOCK, catalogRefId, formattedMsg);
        inventoryAvailable = false;
      }
    }

    if (!inventoryAvailable) {
      return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
    }

    return SUCCESS;
  }

  /**
   * <p>Determine if the item is available by ensuring that at
   * least 1 is available for preorder, backorder or immediate
   * availability.
   * <p>This does not check the quantity being purchased with the
   * levels available and the threshold levels.  The OMS will do
   * that since it has more up to the minute inventory data.  The
   * order will be accepted as long as the inventory data shows that
   * it can be at least partially fulfilled.
   * @param pItem - commerce item
   * @param pManager - inventory manager
   * @return a boolean with true for available and false for unavailable
   */
  protected boolean itemIsAvailable(CommerceItem pItem, InventoryManager pManager) {
    boolean available = false;

    // get the item Id we'll use it lots of places
    String itemId = pItem.getCatalogRefId();

    if (isLoggingDebug()) {
      logDebug("itemIsAvailable(): checking inventory statuc for commerce item " + pItem);
    }

    // We should have a StoreInventoryManager as the inventory manager, but just in
    // case, do a check and still work if it is the default inventory manager (we just will
    // not handle preorder as expected).
    if (pManager instanceof StoreInventoryManager) {
      StoreInventoryManager storeInvManager = (StoreInventoryManager) pManager;
      RepositoryItem pProduct = (RepositoryItem) pItem.getAuxiliaryData().getProductRef();

      try {
        int availability = storeInvManager.queryAvailabilityStatus(pProduct, itemId);

        if (isLoggingDebug()) {
          logDebug("itemIsAvailable(): item " + itemId + " availability=" + availability);
        }

        if ((availability == storeInvManager.getAvailabilityStatusInStockValue()) ||
            (availability == storeInvManager.getAvailabilityStatusBackorderableValue()) ||
            (availability == storeInvManager.getAvailabilityStatusPreorderableValue())) {
          available = true;
        } else {
          // For store, default everything else to unavailable
          available = false;
        }
      } catch (InventoryException ie) {
        // If we can't look up the item, then assume no availability
        available = false;
      }
    } else {
      if (isLoggingWarning()) {
        logWarning(
          "ProcValidateInventoryForCheckout: the inventory manager is not a StoreInventoryManager.  Preorder may not be handled as expected.");
      }

      // create the inventory info for the item
      InventoryInfo info = new InventoryInfo(itemId, pManager, this);

      if (isLoggingDebug()) {
        logDebug("itemIsAvailable(): item " + itemId + " availability=" + info.getAvailabilityStatusMsg());
      }

      if (info.getAvailabilityStatus() != null) {
        // get the availability status from the info
        int status = info.getAvailabilityStatus().intValue();

        if ((status == InventoryManager.AVAILABILITY_STATUS_IN_STOCK) ||
            (status == InventoryManager.AVAILABILITY_STATUS_BACKORDERABLE)) {
          available = true;
        } else {
          available = false;
        }
      } else {
        available = false;
      }
    }

    return available;
  }

  /**
   * Returns the valid return codes:
   * 1 - The processor completed.
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes() {
    int[] ret = { SUCCESS };

    return ret;
  }

  /**
   * This method adds an error to the PipelineResult object. This method, rather than
   * just storing a single error object in pResult, stores a Map of errors. This allows more
   * than one error to be stored using the same key in the pResult object. pKey is
   * used to reference a HashMap of errors in pResult. So, calling
   * pResult.getError(pKey) will return an object which should be cast to a Map.
   * Each entry within the map is keyed by pId and its value is pError.
   *
   * @param pResult the PipelineResult object supplied in runProcess()
   * @param pKey the key to use to store the HashMap in the PipelineResult object
   * @param pId the key to use to store the error message within the HashMap in the
   *            PipelineResult object
   * @param pError the error object to store in the HashMap
   * @see atg.service.pipeline.PipelineResult
   * @see #runProcess(Object, PipelineResult)
   */
  protected void addHashedError(PipelineResult pResult, String pKey, String pId, Object pError) {
    Object error = pResult.getError(pKey);

    if (error == null) {
      HashMap map = new HashMap(5);
      pResult.addError(pKey, map);
      map.put(pId, pError);
    } else if (error instanceof Map) {
      Map map = (Map) error;
      map.put(pId, pError);
    }
  }
}
