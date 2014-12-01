/*<ATGCOPYRIGHT>
 * Copyright (C) 1999-2011 Art Technology Group, Inc.
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

package atg.commerce.order.processor;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;

import atg.commerce.catalog.CatalogTools;
import atg.commerce.inventory.InventoryException;
import atg.commerce.inventory.InventoryManager;
import atg.commerce.inventory.LocalizingInventoryManager;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PipelineConstants;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

/**
 * This processor verifies that none of the products in the CommerceItems within the order
 * are not discontinued.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcCheckForDiscontinuedProducts.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcCheckForDiscontinuedProducts extends ApplicationLoggingImpl implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcCheckForDiscontinuedProducts.java#2 $$Change: 651448 $";

  static final String RESOURCE_NAME = "atg.commerce.order.OrderResources";
  static final String USER_MSGS_RES_NAME = "atg.commerce.order.UserMessages";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = LayeredResourceBundle.getBundle(RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  private static java.util.ResourceBundle sUserResourceBundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, java.util.Locale.getDefault());

  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcCheckForDiscontinuedProducts() {
  }

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The processor completed
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = {SUCCESS};
    return ret;
  }

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcValidateProductsNotDiscontinued";

  /**
   * Sets property LoggingIdentifier
   **/
  public void setLoggingIdentifier(String pLoggingIdentifier) {
    mLoggingIdentifier = pLoggingIdentifier;
  }

  /**
   * Returns property LoggingIdentifier
   **/
  public String getLoggingIdentifier() {
    return mLoggingIdentifier;
  }

  //-------------------------------------
  // property: ignoreItemsNotInInventory
  private boolean mIgnoreItemsNotInInventory = false;

  /**
   * Sets property IgnoreItemsNotInInventory
   **/
  public void setIgnoreItemsNotInInventory(boolean pIgnoreItemsNotInInventory) {
    mIgnoreItemsNotInInventory = pIgnoreItemsNotInInventory;
  }

  /**
   * Returns property IgnoreItemsNotInInventory
   **/
  public boolean getIgnoreItemsNotInInventory() {
    return mIgnoreItemsNotInInventory;
  }

  //-----------------------------------------------
  /**
   * This method iterates through the items in the order and first checks to see if the
   * productId exists in the product catalog. Then it checks the inventory to see if it
   * is discontinued. If either condition fails, an error is added to the PipelineResult
   * object.
   *
   * @param pParam a HashMap which must contain an Order and a CatalogTools object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    CatalogTools catalogTools = (CatalogTools) map.get(PipelineConstants.CATALOGTOOLS);
    InventoryManager invManager = (InventoryManager) map.get(PipelineConstants.INVENTORYMANAGER);
    java.util.Locale locale = (java.util.Locale) map.get(PipelineConstants.LOCALE);
    java.util.ResourceBundle resourceBundle;

    if (locale == null)
      resourceBundle = sUserResourceBundle;
    else
      resourceBundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, locale);

    // check for null parameters
    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      RESOURCE_NAME, sResourceBundle));
    if (catalogTools == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidCatalogToolsParameter",
                                      RESOURCE_NAME, sResourceBundle));
    if (invManager == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidInventoryManagerParameter",
                                      RESOURCE_NAME, sResourceBundle));

    if (order.getCommerceItemCount() == 0)
      return SUCCESS;

    RepositoryItem item;
    CommerceItem ci;
    String productId, skuId;

    Iterator iter = order.getCommerceItems().iterator();
    while (iter.hasNext()) {
      ci = (CommerceItem) iter.next();
      productId = ci.getAuxiliaryData().getProductId();
      skuId = ci.getCatalogRefId();

      if (catalogTools instanceof atg.commerce.catalog.custom.CustomCatalogTools)
        item = ((atg.commerce.catalog.custom.CustomCatalogTools) catalogTools).findProduct(productId, ci.getCatalogKey(), false);
      else
        item = catalogTools.findProduct(productId, ci.getCatalogKey());

      if (item == null) {
        if (isLoggingDebug())
          logDebug("Cannot find product: " + productId + " in catalog.");
        String msg = resourceBundle.getString("CantFindProduct");
        pResult.addError(PipelineConstants.CANTFINDPRODUCT + ":" + productId, MessageFormat.format(msg, productId));
      }
      else {
        try {
          int status = 0;
	  if (invManager instanceof LocalizingInventoryManager)
	    status = ((LocalizingInventoryManager) invManager).queryAvailabilityStatus(skuId, locale.toString());
	  else status = invManager.queryAvailabilityStatus(skuId);
          if (status == InventoryManager.AVAILABILITY_STATUS_DISCONTINUED) {
            if (isLoggingDebug())
		      logDebug("Product: " + productId + " has been discontinued.");
            String msg = resourceBundle.getString("ProductDiscontinued");
            pResult.addError(PipelineConstants.PRODUCTDISCONTINUED + ":" + productId, MessageFormat.format(msg, productId));
          }
        }
        catch (InventoryException e) {
          if (! getIgnoreItemsNotInInventory())
            throw e;
        }
      }
    }

    return SUCCESS;
  }
}
