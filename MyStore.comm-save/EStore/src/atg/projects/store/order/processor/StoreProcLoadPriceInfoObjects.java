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
package atg.projects.store.order.processor;

import atg.beans.DynamicBeans;

import atg.commerce.order.*;
import atg.commerce.order.processor.ProcLoadPriceInfoObjects;

import atg.repository.ItemDescriptorImpl;
import atg.repository.MutableRepositoryItem;
import atg.repository.RemovedItemException;
import atg.repository.RepositoryItemDescriptor;


/**
 * This class is overridden to include loading of TaxPriceInfo for commerce items
 * and shipping groups.
 * Commerce item's TaxPriceInfo objects are stored with the CommerceItems'
 * ItemPriceInfo. Shipping group's TaxPriceInfo is stored with the ShippingGroup
 * ShippingPriceInfo.
 *
 * @see atg.commerce.order.processor.ProcLoadPriceInfoObjects
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/processor/StoreProcLoadPriceInfoObjects.java#3 $$Change: 635816 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class StoreProcLoadPriceInfoObjects extends ProcLoadPriceInfoObjects {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/processor/StoreProcLoadPriceInfoObjects.java#3 $$Change: 635816 $";

  /**
   * Price information repository item name.
   */
  String mPriceInfoRepositoryItemName;

  /**
   * Tax price information repository item name.
   */
  String mTaxPriceInfoRepositoryItemName;

  /**
   * @return price information repository item name.
   */
  public String getPriceInfoRepositoryItemName() {
    return mPriceInfoRepositoryItemName;
  }

  /**
   * @param pPriceInfoRepositoryItemName - price information repository item name.
   */
  public void setPriceInfoRepositoryItemName(String pPriceInfoRepositoryItemName) {
    mPriceInfoRepositoryItemName = pPriceInfoRepositoryItemName;
  }

  /**
   * @return tax price repository item name.
   */
  public String getTaxPriceInfoRepositoryItemName() {
    return mTaxPriceInfoRepositoryItemName;
  }

  /**
   * @param pTaxPriceInfoRepositoryItemName - tax price repository price name.
   */
  public void setTaxPriceInfoRepositoryItemName(String pTaxPriceInfoRepositoryItemName) {
    mTaxPriceInfoRepositoryItemName = pTaxPriceInfoRepositoryItemName;
  }

  /**
   * <p>
   * Overriding to load tax price info for items.
   * The only code added to the OOB implementation is a call
   * to the loadItemTaxPriceInfo method.
   *
   * The complete OOB code is available in the $DYNAMO_ROOT/DCS/src/Java
   * directory of the ATG install.
   *
   * @see atg.commerce.order.processor.ProcLoadPriceInfoObjects#readProperties
   * @see atg.commerce.order.processor.ProcLoadPriceInfoObjects#loadPricingAdjustments
   * @see atg.commerce.order.processor.ProcLoadPriceInfoObjects#loadDetailedItemPriceInfos
   *
   * @param pOrder The order
   * @param ci The commerce identifier object
   * @param pMutItem The repository item
   * @param pOrderManager The order manager
   * @param pInvalidateCache Boolean to invalidate cache
   * @throws Exception if an error occurs
   * @see atg.commerce.order.processor.ProcLoadPriceInfoObjects#loadItemPriceInfo(atg.commerce.order.Order, atg.commerce.order.CommerceIdentifier, atg.repository.MutableRepositoryItem, atg.commerce.order.OrderManager, java.lang.Boolean)
   */
  protected void loadItemPriceInfo(Order pOrder, CommerceIdentifier ci, MutableRepositoryItem pMutItem,
    OrderManager pOrderManager, Boolean pInvalidateCache)
    throws Exception {
    MutableRepositoryItem piRepItem = (MutableRepositoryItem) pMutItem.getPropertyValue(getItemPriceInfoProperty());

    Object amtInfo = null;

    piRepItem = (MutableRepositoryItem) pMutItem.getPropertyValue(getItemPriceInfoProperty());

    if (piRepItem == null) {
      if (DynamicBeans.getBeanInfo(ci).hasProperty(getPriceInfoRepositoryItemName())) {
        DynamicBeans.setPropertyValue(ci, getPriceInfoRepositoryItemName(), piRepItem);
      }

      DynamicBeans.setPropertyValue(ci, getItemPriceInfoProperty(), amtInfo);
    } else {
      RepositoryItemDescriptor desc = piRepItem.getItemDescriptor();

      if (pInvalidateCache.booleanValue()) {
        invalidateCache((ItemDescriptorImpl) desc, piRepItem);
      }

      String className = pOrderManager.getOrderTools().getMappedBeanName(desc.getItemDescriptorName());
      amtInfo = Class.forName(className).newInstance();

      if (DynamicBeans.getBeanInfo(ci).hasProperty(getPriceInfoRepositoryItemName())) {
        DynamicBeans.setPropertyValue(ci, getPriceInfoRepositoryItemName(), piRepItem);
      }

      DynamicBeans.setPropertyValue(ci, getItemPriceInfoProperty(), amtInfo);

      readProperties(pOrder, amtInfo, getLoadProperties(), piRepItem, desc, pOrderManager);

      loadPricingAdjustments(pOrder, amtInfo, piRepItem, pOrderManager, pInvalidateCache);

      // Added to load TaxPriceInfo items. Only extension to this method.
      loadTaxPriceInfo(pOrder, amtInfo, piRepItem, pOrderManager, pInvalidateCache);

      loadDetailedItemPriceInfos(pOrder, amtInfo, piRepItem, pOrderManager, pInvalidateCache);
    }

    if (ci instanceof ChangedProperties) {
      ((ChangedProperties) ci).clearChangedProperties();
    }

    /* If the item is a configurable SKU, then load the priceinfo for the subskus */
    if (ci instanceof CommerceItemContainer) {
      if (isLoggingDebug()) {
        logDebug("item is configurable sku - iterate thru its subskus to load them " + pMutItem);
      }

      loadSubSkuPriceInfo(pOrder, ci, pMutItem, pOrderManager, pInvalidateCache);
    }
  }

  /**
   * <p>
   * Load the ItemPriceInfo's or ShippingPriceInfo's TaxPriceInfo. This method is nearly identical
   * to the OOB loadTaxPriceInfo method in the superclass, but the TaxPriceInfo
   * is loaded from the Commerce item's / Shipping group's PriceInfo rather than the
   * Order.
   *
   * <p>
   * The complete OOB code is available in the $DYNAMO_ROOT/DCS/src/Java
   * directory of the ATG install.
   *
   * @see atg.commerce.order.processor.ProcLoadPriceInfoObjects#loadTaxPriceInfo
   * @param pOrder - the order
   * @param pAmountInfo - the ItemPriceInfo object
   * @param pPriceInfoRepItem  - the ItemPriceInfo repository item
   * @param pOrderManager - order manager
   * @param pInvalidateCache - invalidateCache boolean for cache invalidation
   * @throws Exception if an error occurs
   */
  protected void loadTaxPriceInfo(Order pOrder, Object pAmountInfo, MutableRepositoryItem pPriceInfoRepItem,
    OrderManager pOrderManager, Boolean pInvalidateCache)
    throws Exception {
    if (isLoggingDebug()) {
      logDebug("Inside loadItemTaxPriceInfo");
    }

    // Get the TaxPriceInfo repository item from the incoming ItemPriceInfo.
    MutableRepositoryItem tpiRepItem = (MutableRepositoryItem) pPriceInfoRepItem.getPropertyValue(getTaxPriceInfoProperty());
    Object amtInfo = null;

    // If tpiRepItem not present, then 
    if (tpiRepItem == null) {
      if (isLoggingDebug()) {
        logDebug("piRepItem is null");
      }

      if (DynamicBeans.getBeanInfo(pAmountInfo).hasProperty(getTaxPriceInfoRepositoryItemName())) {
        DynamicBeans.setPropertyValue(pAmountInfo, getTaxPriceInfoRepositoryItemName(), tpiRepItem);
      }

      DynamicBeans.setPropertyValue(pAmountInfo, getTaxPriceInfoProperty(), amtInfo);
    } else {
      if (isLoggingDebug()) {
        logDebug("piRepItem is not null");
      }

      RepositoryItemDescriptor tpiDesc = tpiRepItem.getItemDescriptor();

      if (pInvalidateCache.booleanValue()) {
        invalidateCache((ItemDescriptorImpl) tpiDesc, tpiRepItem);
      }

      String className = pOrderManager.getOrderTools().getMappedBeanName(tpiDesc.getItemDescriptorName());

      amtInfo = Class.forName(className).newInstance();

      if (DynamicBeans.getBeanInfo(pAmountInfo).hasProperty(getTaxPriceInfoRepositoryItemName())) {
        DynamicBeans.setPropertyValue(pAmountInfo, getTaxPriceInfoRepositoryItemName(), tpiRepItem);
      }

      if (isLoggingDebug()) {
        logDebug("tpiRepitem: " + tpiRepItem.getRepositoryId());
        logDebug("tpiDesc: " + tpiDesc.getItemDescriptorName());
        logDebug("className: " + className);
        logDebug("pAmountInfo: " + pAmountInfo.toString());
      }

      // DynamicBeans.setPropertyValue(pAmountInfo, getItemPriceInfoProperty(), amtInfo);
      try {
        DynamicBeans.setPropertyValue(pAmountInfo, getTaxPriceInfoProperty(), amtInfo);

        readProperties(pOrder, amtInfo, getLoadProperties(), tpiRepItem, tpiDesc, pOrderManager);
      } catch (RemovedItemException rie) {
        if (isLoggingWarning()) {
          logWarning("Problem loading taxPriceInfo.", rie);
        }
      }
    }

    if (pAmountInfo instanceof ChangedProperties) {
      ((ChangedProperties) pAmountInfo).clearChangedProperties();
    }
  }

  
  /**
   * Overrides the base method to load ShippingPriceInfo's TaxPriceInfo.
   */
  protected void loadShippingPriceInfo(Order pOrder, CommerceIdentifier ci,
      MutableRepositoryItem pMutItem, OrderManager pOrderManager,
      Boolean pInvalidateCache) throws Exception {
    super.loadShippingPriceInfo(pOrder, ci, pMutItem, pOrderManager, pInvalidateCache);
    
    Object amtInfo = DynamicBeans.getPropertyValue(ci, getShippingPriceInfoProperty());
    MutableRepositoryItem piRepItem = (MutableRepositoryItem) pMutItem.getPropertyValue(getShippingPriceInfoProperty());
    if (piRepItem != null) {
      loadTaxPriceInfo(pOrder, amtInfo, piRepItem, pOrderManager, pInvalidateCache);
    }
    
    if (ci instanceof ChangedProperties)
      ((ChangedProperties) ci).clearChangedProperties();
  }
  
}
