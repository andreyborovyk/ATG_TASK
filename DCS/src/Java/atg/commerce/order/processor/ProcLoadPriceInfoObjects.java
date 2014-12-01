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

import atg.repository.*;
import atg.commerce.order.*;
import atg.commerce.pricing.*;
import atg.service.pipeline.*;
import atg.core.util.*;
import atg.beans.*;

import java.beans.IntrospectionException;
import java.util.*;

/**
 * This processor loads the PriceInfo objects from the OrderRepository into the Order object.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcLoadPriceInfoObjects.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.commerce.pricing.AmountInfo
 * @see atg.commerce.pricing.OrderPriceInfo
 * @see atg.commerce.pricing.ItemPriceInfo
 * @see atg.commerce.pricing.ShippingPriceInfo
 * @see atg.commerce.pricing.TaxPriceInfo
 */
public class ProcLoadPriceInfoObjects extends LoadProperties implements PipelineProcessor {

  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcLoadPriceInfoObjects.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  private static final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcLoadPriceInfoObjects() {
  }

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - Order object saved successfully
   * 2 - Order object save failed
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = {SUCCESS};
    return ret;
  }

  //-------------------------------------
  // property: pricingAdjustmentProperties
  //-------------------------------------
  private String[] mPricingAdjustmentProperties = null;

  /**
   * Returns the pricingAdjustmentProperties
   */
  public String[] getPricingAdjustmentProperties() {
    return mPricingAdjustmentProperties;
  }

  /**
   * Sets the pricingAdjustmentProperties
   */
  public void setPricingAdjustmentProperties(String[] pPricingAdjustmentProperties) {
    mPricingAdjustmentProperties = pPricingAdjustmentProperties;
  }

  //-------------------------------------
  // property: detailedItemPriceInfoProperties
  //-------------------------------------
  private String[] mDetailedItemPriceInfoProperties = null;

  /**
   * Returns the detailedItemPriceInfoProperties
   */
  public String[] getDetailedItemPriceInfoProperties() {
    return mDetailedItemPriceInfoProperties;
  }

  /**
   * Sets the detailedItemPriceInfoProperties
   */
  public void setDetailedItemPriceInfoProperties(String[] pDetailedItemPriceInfoProperties) {
    mDetailedItemPriceInfoProperties = pDetailedItemPriceInfoProperties;
  }

  //-------------------------------------
  // property: detailsItemPriceInfoProperty
  //-------------------------------------
  private String mDetailsItemPriceInfoProperty = null;

  /**
   * Returns the detailedItemPriceInfoProperties
   * This is the property of DetailedItemPriceInfo that
   * points to the ItemPriceInfo
   */
  public String getDetailsItemPriceInfoProperty() {
    return mDetailsItemPriceInfoProperty;
  }

  /**
   * Sets the detailedItemPriceInfoProperties
   */
  public void setDetailsItemPriceInfoProperty(String pDetailsItemPriceInfoProperty) {
    mDetailsItemPriceInfoProperty = pDetailsItemPriceInfoProperty;
  }

  //-------------------------------------
  // property: adjustmentsProperty
  //-------------------------------------
  private String mAdjustmentsProperty = "adjustments";

  /**
   * Returns the adjustmentsProperty
   */
  public String getAdjustmentsProperty() {
    return mAdjustmentsProperty;
  }

  /**
   * Sets the adjustmentsProperty
   */
  public void setAdjustmentsProperty(String pAdjustmentsProperty) {
    mAdjustmentsProperty = pAdjustmentsProperty;
  }

  //-------------------------------------
  // property: currentPriceDetailsProperty
  //-------------------------------------
  private String mCurrentPriceDetailsProperty = "currentPriceDetails";

  /**
   * Returns the currentPriceDetailsProperty
   */
  public String getCurrentPriceDetailsProperty() {
    return mCurrentPriceDetailsProperty;
  }

  /**
   * Sets the currentPriceDetailsProperty
   */
  public void setCurrentPriceDetailsProperty(String pCurrentPriceDetailsProperty) {
    mCurrentPriceDetailsProperty = pCurrentPriceDetailsProperty;
  }

  //-------------------------------------
  // property: shippingItemsSubtotalPriceInfosProperty
  //-------------------------------------
  private String mShippingItemsSubtotalPriceInfosProperty = "shippingItemsSubtotalPriceInfos";

  /**
   * Returns property shippingItemsSubtotalPriceInfosProperty
   *
   * @return returns property shippingItemsSubtotalPriceInfosProperty
   */
  public String getShippingItemsSubtotalPriceInfosProperty() {
    return mShippingItemsSubtotalPriceInfosProperty;
  }

  /**
   * Sets property shippingItemsSubtotalPriceInfosProperty
   *
   * @param pShippingItemsSubtotalPriceInfosProperty the value to set for property shippingItemsSubtotalPriceInfosProperty
   */
  public void setShippingItemsSubtotalPriceInfosProperty(String pShippingItemsSubtotalPriceInfosProperty) {
    mShippingItemsSubtotalPriceInfosProperty = pShippingItemsSubtotalPriceInfosProperty;
  }

  //---------------------------------------------------------
  // property: TaxableShippingItemsSubtotalPriceInfosProperty
  //---------------------------------------------------------
  private String mTaxableShippingItemsSubtotalPriceInfosProperty = "taxableShippingItemsSubtotalPriceInfos";

  /**
   * Returns property TaxableShippingItemsSubtotalPriceInfosProperty
   *
   * @return returns property TaxableShippingItemsSubtotalPriceInfosProperty
   */
  public String getTaxableShippingItemsSubtotalPriceInfosProperty() {
    return mTaxableShippingItemsSubtotalPriceInfosProperty;
  }

  /**
   * Sets property TaxableShippingItemsSubtotalPriceInfosProperty
   *
   * @param pTaxableShippingItemsSubtotalPriceInfosProperty the value to set for property TaxableShippingItemsSubtotalPriceInfosProperty
   */
  public void setTaxableShippingItemsSubtotalPriceInfosProperty(String pTaxableShippingItemsSubtotalPriceInfosProperty) {
    mTaxableShippingItemsSubtotalPriceInfosProperty = pTaxableShippingItemsSubtotalPriceInfosProperty;
  }

  //---------------------------------------------------------
  // property: NonTaxableShippingItemsSubtotalPriceInfosProperty
  //---------------------------------------------------------
  private String mNonTaxableShippingItemsSubtotalPriceInfosProperty = "nonTaxableShippingItemsSubtotalPriceInfos";

  /**
   * Returns property nonTaxableShippingItemsSubtotalPriceInfosProperty
   *
   * @return returns property nonTaxableShippingItemsSubtotalPriceInfosProperty
   */
  public String getNonTaxableShippingItemsSubtotalPriceInfosProperty() {
    return mNonTaxableShippingItemsSubtotalPriceInfosProperty;
  }

  /**
   * Sets property NonTaxableShippingItemsSubtotalPriceInfosProperty
   *
   * @param pNonTaxableShippingItemsSubtotalPriceInfosProperty the value to set for property NonTaxableShippingItemsSubtotalPriceInfosProperty
   */
  public void setNonTaxableShippingItemsSubtotalPriceInfosProperty(String pNonTaxableShippingItemsSubtotalPriceInfosProperty) {
    mNonTaxableShippingItemsSubtotalPriceInfosProperty = pNonTaxableShippingItemsSubtotalPriceInfosProperty;
  }

  //-------------------------------------
  // property: shippingItemsTaxPriceInfosProperty
  //-------------------------------------
  private String mShippingItemsTaxPriceInfosProperty = "shippingItemsTaxPriceInfos";

  /**
   * Returns property shippingItemsTaxPriceInfosProperty
   *
   * @return returns property shippingItemsTaxPriceInfosProperty
   */
  public String getShippingItemsTaxPriceInfosProperty() {
    return mShippingItemsTaxPriceInfosProperty;
  }

  /**
   * Sets property shippingItemsTaxPriceInfosProperty
   *
   * @param pShippingItemsTaxPriceInfosProperty the value to set for property shippingItemsTaxPriceInfosProperty
   */
  public void setShippingItemsTaxPriceInfosProperty(String pShippingItemsTaxPriceInfosProperty) {
    mShippingItemsTaxPriceInfosProperty = pShippingItemsTaxPriceInfosProperty;
  }

  //-------------------------------------
  // property: orderPriceInfoProperty
  //-------------------------------------
  private String mOrderPriceInfoProperty = "priceInfo";

  /**
   * Returns the orderPriceInfoProperty
   */
  public String getOrderPriceInfoProperty() {
    return mOrderPriceInfoProperty;
  }

  /**
   * Sets the orderPriceInfoProperty
   */
  public void setOrderPriceInfoProperty(String pOrderPriceInfoProperty) {
    mOrderPriceInfoProperty = pOrderPriceInfoProperty;
  }

  //-------------------------------------
  // property: taxPriceInfoProperty
  //-------------------------------------
  private String mTaxPriceInfoProperty = "taxPriceInfo";

  /**
   * Returns the taxPriceInfoProperty
   */
  public String getTaxPriceInfoProperty() {
    return mTaxPriceInfoProperty;
  }

  /**
   * Sets the taxPriceInfoProperty
   */
  public void setTaxPriceInfoProperty(String pTaxPriceInfoProperty) {
    mTaxPriceInfoProperty = pTaxPriceInfoProperty;
  }

  //-------------------------------------
  // property: shippingPriceInfoProperty
  //-------------------------------------
  private String mShippingPriceInfoProperty = "priceInfo";

  /**
   * Returns the shippingPriceInfoProperty
   */
  public String getShippingPriceInfoProperty() {
    return mShippingPriceInfoProperty;
  }

  /**
   * Sets the shippingPriceInfoProperty
   */
  public void setShippingPriceInfoProperty(String pShippingPriceInfoProperty) {
    mShippingPriceInfoProperty = pShippingPriceInfoProperty;
  }

  //-------------------------------------
  // property: itemPriceInfoProperty
  //-------------------------------------
  private String mItemPriceInfoProperty = "priceInfo";

  /**
   * Returns the itemPriceInfoProperty
   */
  public String getItemPriceInfoProperty() {
    return mItemPriceInfoProperty;
  }

  /**
   * Sets the itemPriceInfoProperty
   */
  public void setItemPriceInfoProperty(String pItemPriceInfoProperty) {
    mItemPriceInfoProperty = pItemPriceInfoProperty;
  }

  //-------------------------------------
  // property: commerceItemsProperty
  //-------------------------------------
  private String mCommerceItemsProperty = "commerceItems";

  /**
   * Returns the commerceItemsProperty
   */
  public String getCommerceItemsProperty() {
    return mCommerceItemsProperty;
  }

  /**
   * Sets the commerceItemsProperty
   */
  public void setCommerceItemsProperty(String pCommerceItemsProperty) {
    mCommerceItemsProperty = pCommerceItemsProperty;
  }

  //-------------------------------------
  // property: shippingGroupsProperty
  //-------------------------------------
  private String mShippingGroupsProperty = "shippingGroups";

  /**
   * Returns the shippingGroupsProperty
   */
  public String getShippingGroupsProperty() {
    return mShippingGroupsProperty;
  }

  /**
   * Sets the shippingGroupsProperty
   */
  public void setShippingGroupsProperty(String pShippingGroupsProperty) {
    mShippingGroupsProperty = pShippingGroupsProperty;
  }


  //---------------------------------------------------------------------------
  // property:RangePropertyName
  //---------------------------------------------------------------------------

  private String mRangePropertyName;
  public void setRangePropertyName(String pRangePropertyName) {
    mRangePropertyName = pRangePropertyName;
  }

  /**
   * The name of the Range property in the DetailedItemPriceInfo
   **/
  public String getRangePropertyName() {
    return mRangePropertyName;
  }


  //---------------------------------------------------------------------------
  // property:RangeClass
  //---------------------------------------------------------------------------

  private String mRangeClass;
  public void setRangeClass(String pRangeClass) {
    mRangeClass = pRangeClass;
  }

  /**
   * The type of the Range class to be created in the DetailedItemPriceInfo
   **/
  public String getRangeClass() {
    return mRangeClass;
  }

  //---------------------------------------------------------------------------
  // property:DetailsRangeProperties
  //---------------------------------------------------------------------------

  private String[] mDetailsRangeProperties;
  public void setDetailsRangeProperties(String[] pDetailsRangeProperties) {
    mDetailsRangeProperties = pDetailsRangeProperties;
  }

  /**
   * The list of properties from the DetailedItemPriceInfo's Range
   * property that are stored in the details repository item
   **/
  public String[] getDetailsRangeProperties() {
    return mDetailsRangeProperties;
  }

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcLoadPriceInfoObjects";

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

  //-----------------------------------------------
  /**
   * This method loads the PriceInfo objects from the OrderRepository into the Order object.
   * It does this by constructing a new AmountInfo instance based on the class mapped to
   * the repository item type of the PriceInfo. It then iterates through the properties
   * listed in the loadProperties property inherited by this class, setting the values in the
   * object.
   *
   * This method requires that an Order, order repository item, and OrderManager object
   * be supplied in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order, order repository item, and OrderManager object
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
    MutableRepositoryItem orderItem = (MutableRepositoryItem) map.get(PipelineConstants.ORDERREPOSITORYITEM);
    OrderManager orderManager = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);
    Boolean invalidateCache = (Boolean) map.get(PipelineConstants.INVALIDATECACHE);
    boolean loadOrderPriceInfoFlag = true;
    boolean loadItemPriceInfoFlag = true;
    boolean loadShippingPriceInfoFlag = true;
    boolean loadTaxPriceInfoFlag = true;

    Boolean flag = (Boolean) map.get(PipelineConstants.LOADORDERPRICEINFO);
    if (flag != null)
      loadOrderPriceInfoFlag = flag.booleanValue();
    flag = (Boolean) map.get(PipelineConstants.LOADITEMPRICEINFO);
    if (flag != null)
      loadItemPriceInfoFlag = flag.booleanValue();
    flag = (Boolean) map.get(PipelineConstants.LOADSHIPPINGPRICEINFO);
    if (flag != null)
      loadShippingPriceInfoFlag = flag.booleanValue();
    flag = (Boolean) map.get(PipelineConstants.LOADTAXPRICEINFO);
    if (flag != null)
      loadTaxPriceInfoFlag = flag.booleanValue();

    // check for null parameters
    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (orderItem == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidRepositoryItemParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (orderManager == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderManagerParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (invalidateCache == null)
      invalidateCache = Boolean.FALSE;

    OrderTools orderTools = orderManager.getOrderTools();

    CommerceIdentifier ci;
    MutableRepositoryItem mutItem;

    if (loadOrderPriceInfoFlag)
      loadOrderPriceInfo(order, orderItem, orderManager, invalidateCache);
    if (loadTaxPriceInfoFlag)
      loadTaxPriceInfo(order, orderItem, orderManager, invalidateCache);

    if (loadItemPriceInfoFlag) {
      List repList = (List) orderItem.getPropertyValue(getCommerceItemsProperty());
      List ciList = (List) DynamicBeans.getPropertyValue(order, getCommerceItemsProperty());
      for (int i = 0; i < ciList.size(); i++) {
        mutItem = (MutableRepositoryItem) repList.get(i);
        ci = (CommerceIdentifier) ciList.get(i);
        loadItemPriceInfo(order, ci, mutItem, orderManager, invalidateCache);
      }
    }

    if (loadShippingPriceInfoFlag) {
      List repList = (List) orderItem.getPropertyValue(getShippingGroupsProperty());
      List ciList = (List) DynamicBeans.getPropertyValue(order, getShippingGroupsProperty());
      for (int i = 0; i < ciList.size(); i++) {
        mutItem = (MutableRepositoryItem) repList.get(i);
        ci = (CommerceIdentifier) ciList.get(i);
        loadShippingPriceInfo(order, ci, mutItem, orderManager, invalidateCache);
      }
    }

    if (order instanceof ChangedProperties)
      ((ChangedProperties) order).clearChangedProperties();

    return SUCCESS;
  }

  //-----------------------------------------------
  /**
   * This method invalidates the item from the cache if invalidateCache is true
   */
  protected void invalidateCache(ItemDescriptorImpl desc, MutableRepositoryItem mutItem) {
    try {
      desc.removeItemFromCache(mutItem.getRepositoryId());
    }
    catch (RepositoryException e) {
      if (isLoggingWarning())
        logWarning("Unable to invalidate item descriptor " + desc.getItemDescriptorName() + ":" + mutItem.getRepositoryId());
    }
  }

  //-----------------------------------------------
  /**
   * This method loads the OrderPriceInfo object of an order.  For
   * each order it creates the appropriate AmountInfo (using the
   * <code>OrderTools.beanNameToItemDescriptorMap</code>).  It then 
   * copies the properties from the repository item into this class.
   * It then loads the rest of the object.
   * See the <code>loadProperties</code> for a list
   * of properties that are copied
   *
   * @see #readProperties
   * @see #loadPricingAdjustments
   * @see #loadShippingItemsSubtotalPriceInfos
   * @see #loadTaxableShippingItemsSubtotalPriceInfos
   * @see #loadNonTaxableShippingItemsSubtotalPriceInfos
   *
   * @param order The order whose price info is being loaded
   * @param orderItem The repository item that corresponds to the order
   * @param orderManager The OrderManager that was in the pipeline params
   * @param invalidateCache If true, then the order's price info repository cache
   *                        entry is invalidated
   **/
  protected void loadOrderPriceInfo(Order order, MutableRepositoryItem orderItem,
                              OrderManager orderManager, Boolean invalidateCache) throws Exception
  {
    MutableRepositoryItem piRepItem = (MutableRepositoryItem) orderItem.getPropertyValue(getOrderPriceInfoProperty());
    Object amtInfo = null;

    if (piRepItem == null) {
      if (DynamicBeans.getBeanInfo(order).hasProperty("priceInfoRepositoryItem"))
        DynamicBeans.setPropertyValue(order, "priceInfoRepositoryItem", piRepItem);
      DynamicBeans.setPropertyValue(order, getOrderPriceInfoProperty(), amtInfo);
    }
    else {
      RepositoryItemDescriptor desc = piRepItem.getItemDescriptor();
      if (invalidateCache.booleanValue())
        invalidateCache((ItemDescriptorImpl) desc, piRepItem);
      String className = orderManager.getOrderTools().getMappedBeanName(desc.getItemDescriptorName());
      amtInfo = Class.forName(className).newInstance();
      if (DynamicBeans.getBeanInfo(order).hasProperty("priceInfoRepositoryItem"))
        DynamicBeans.setPropertyValue(order, "priceInfoRepositoryItem", piRepItem);
      DynamicBeans.setPropertyValue(order, getOrderPriceInfoProperty(), amtInfo);
      readProperties(order, amtInfo, getLoadProperties(), piRepItem, desc, orderManager);
      loadPricingAdjustments(order, amtInfo, piRepItem, orderManager, invalidateCache);
      loadShippingItemsSubtotalPriceInfos(order, amtInfo, piRepItem, orderManager, invalidateCache);
      loadTaxableShippingItemsSubtotalPriceInfos(order, amtInfo, piRepItem, orderManager, invalidateCache);
      loadNonTaxableShippingItemsSubtotalPriceInfos(order, amtInfo, piRepItem, orderManager, invalidateCache);
    }
  }

  //-----------------------------------------------
  /**
   * This method loads the TaxPriceInfo object of an order.  For
   * each order it creates the appropriate AmountInfo (using the
   * <code>OrderTools.beanNameToItemDescriptorMap</code>).  It then 
   * copies the properties from the repository item into this class.
   * It then loads the rest of the object.
   * See the <code>loadProperties</code> for a list
   * of properties that are copied
   *
   * @see #readProperties
   * @see #loadPricingAdjustments
   * @see #loadShippingItemsTaxPriceInfos
   *
   * @param order The order whose price info is being loaded
   * @param orderItem The repository item that corresponds to the order
   * @param orderManager The OrderManager that was in the pipeline params
   * @param invalidateCache If true, then the order's tax price info repository cache
   *                        entry is invalidated
   **/
  protected void loadTaxPriceInfo(Order order, MutableRepositoryItem orderItem,
                            OrderManager orderManager, Boolean invalidateCache) throws Exception
  {
    MutableRepositoryItem piRepItem = (MutableRepositoryItem) orderItem.getPropertyValue(getOrderPriceInfoProperty());
    Object amtInfo = null;

    piRepItem = (MutableRepositoryItem) orderItem.getPropertyValue(getTaxPriceInfoProperty());
    if (piRepItem == null) {
      if (DynamicBeans.getBeanInfo(order).hasProperty("taxPriceInfoRepositoryItem"))
        DynamicBeans.setPropertyValue(order, "taxPriceInfoRepositoryItem", piRepItem);
      DynamicBeans.setPropertyValue(order, getTaxPriceInfoProperty(), amtInfo);
    }
    else {
      RepositoryItemDescriptor desc = piRepItem.getItemDescriptor();
      if (invalidateCache.booleanValue())
        invalidateCache((ItemDescriptorImpl) desc, piRepItem);
      String className = orderManager.getOrderTools().getMappedBeanName(desc.getItemDescriptorName());
      amtInfo = Class.forName(className).newInstance();
      if (DynamicBeans.getBeanInfo(order).hasProperty("taxPriceInfoRepositoryItem"))
        DynamicBeans.setPropertyValue(order, "taxPriceInfoRepositoryItem", piRepItem);
      DynamicBeans.setPropertyValue(order, getTaxPriceInfoProperty(), amtInfo);
      readProperties(order, amtInfo, getLoadProperties(), piRepItem, desc, orderManager);
      loadPricingAdjustments(order, amtInfo, piRepItem, orderManager, invalidateCache);
      loadShippingItemsTaxPriceInfos(order, amtInfo, piRepItem, orderManager, invalidateCache);

    }
  }

  //-----------------------------------------------
  /**
   * This method loads an ItemPriceInfo object of a CommerceItem. If the item is a configurable item,
   * then the ItemPriceInfo objects of the subskus of the commerceItem are also loaded.
   * See the <code>loadProperties</code> for a list
   * of properties that are copied
   *
   * @see #readProperties
   * @see #loadPricingAdjustments
   * @see #loadDetailedItemPriceInfos
   *
   * @param order The order whose item's price info is being loaded
   * @param ci The commerce item whose price is being loaded
   * @param mutItem The repository item for the commerce item
   * @param orderManager The OrderManager that was in the pipeline params
   * @param invalidateCache If true, then the item's price info repository cache
   *                        entry is invalidated
   **/
  protected void loadItemPriceInfo(Order order, CommerceIdentifier ci,
                  MutableRepositoryItem mutItem, OrderManager orderManager, Boolean invalidateCache)
                  throws Exception
  {
    MutableRepositoryItem piRepItem = (MutableRepositoryItem) mutItem.getPropertyValue(getItemPriceInfoProperty());
    Object amtInfo = null;

    piRepItem = (MutableRepositoryItem) mutItem.getPropertyValue(getItemPriceInfoProperty());
    if (piRepItem == null) {
      if (DynamicBeans.getBeanInfo(ci).hasProperty("priceInfoRepositoryItem"))
        DynamicBeans.setPropertyValue(ci, "priceInfoRepositoryItem", piRepItem);
      DynamicBeans.setPropertyValue(ci, getItemPriceInfoProperty(), amtInfo);
    }
    else {
      RepositoryItemDescriptor desc = piRepItem.getItemDescriptor();
      if (invalidateCache.booleanValue())
        invalidateCache((ItemDescriptorImpl) desc, piRepItem);
      String className = orderManager.getOrderTools().getMappedBeanName(desc.getItemDescriptorName());
      amtInfo = Class.forName(className).newInstance();
      if (DynamicBeans.getBeanInfo(ci).hasProperty("priceInfoRepositoryItem"))
        DynamicBeans.setPropertyValue(ci, "priceInfoRepositoryItem", piRepItem);
      DynamicBeans.setPropertyValue(ci, getItemPriceInfoProperty(), amtInfo);
      readProperties(order, amtInfo, getLoadProperties(), piRepItem, desc, orderManager);
      loadPricingAdjustments(order, amtInfo, piRepItem, orderManager, invalidateCache);
      loadDetailedItemPriceInfos(order, amtInfo, piRepItem, orderManager, invalidateCache);
    }

    if (ci instanceof ChangedProperties)
      ((ChangedProperties) ci).clearChangedProperties();

    /* If the item is a configurable SKU, then load the priceinfo for the subskus */
    if(ci instanceof CommerceItemContainer) {
      if(isLoggingDebug())
        logDebug("item is configurable sku - iterate thru its subskus to load them " + mutItem);
      loadSubSkuPriceInfo(order, ci, mutItem, orderManager, invalidateCache);
    }
  }

  /**
   * This method iterates thru the subskus of a configurable sku, and then loads
   * the priceinfos of a subskus.
   *
   * @see #loadItemPriceInfo
   *
   * @param order The order whose item's price info is being loaded
   * @param ci The commerce item whose price is being loaded
   * @param configItem The repository item for the commerce item
   * @param orderManager The OrderManager that was in the pipeline params
   * @param invalidateCache If true, then the item's price info repository cache
   *                        entry is invalidated
   */
  protected void loadSubSkuPriceInfo(Order order, CommerceIdentifier ci,
                  MutableRepositoryItem configItem, OrderManager orderManager, Boolean invalidateCache) throws Exception
  {
    /* Get all the subskus for the configurable item, and then load their pricing info by calling loadItemPriceInfo. */
    List repList = (List) configItem.getPropertyValue("commerceItems");
    Iterator itemIter = repList.iterator();
    List subSkuList = (List) DynamicBeans.getPropertyValue(ci, "commerceItems");
    Iterator classItr = subSkuList.iterator();

    while (itemIter.hasNext() && (classItr.hasNext())) {
      MutableRepositoryItem subskuItem = (MutableRepositoryItem) itemIter.next();
      CommerceIdentifier skuClass = (CommerceIdentifier) classItr.next();

      loadItemPriceInfo(order, skuClass, subskuItem, orderManager, invalidateCache);
    }
  }


  //-----------------------------------------------
  /**
   * This method loads a ShippingPriceInfo object of a ShippingGroup
   * See the <code>loadProperties</code> for a list
   * of properties that are copied
   *
   * @see #readProperties
   * @see #loadPricingAdjustments
   *
   * @param order The order whose shipping group's price info is being loaded
   * @param ci The shipping group whose price is being loaded
   * @param mutItem The repository item for the shipping group
   * @param orderManager The OrderManager that was in the pipeline params
   * @param invalidateCache If true, then the shipping group's price info repository cache
   *                        entry is invalidated
   **/
  protected void loadShippingPriceInfo(Order order, CommerceIdentifier ci,
                  MutableRepositoryItem mutItem, OrderManager orderManager, Boolean invalidateCache)
                  throws Exception
  {
    MutableRepositoryItem piRepItem = (MutableRepositoryItem) mutItem.getPropertyValue(getShippingPriceInfoProperty());
    Object amtInfo = null;

    piRepItem = (MutableRepositoryItem) mutItem.getPropertyValue(getShippingPriceInfoProperty());
    if (piRepItem == null) {
      if (DynamicBeans.getBeanInfo(ci).hasProperty("priceInfoRepositoryItem"))
        DynamicBeans.setPropertyValue(ci, "priceInfoRepositoryItem", piRepItem);
      DynamicBeans.setPropertyValue(ci, getShippingPriceInfoProperty(), amtInfo);
    }
    else {
      RepositoryItemDescriptor desc = piRepItem.getItemDescriptor();
      if (invalidateCache.booleanValue())
        invalidateCache((ItemDescriptorImpl) desc, piRepItem);
      String className = orderManager.getOrderTools().getMappedBeanName(desc.getItemDescriptorName());
      amtInfo = Class.forName(className).newInstance();
      if (DynamicBeans.getBeanInfo(ci).hasProperty("priceInfoRepositoryItem"))
        DynamicBeans.setPropertyValue(ci, "priceInfoRepositoryItem", piRepItem);
      DynamicBeans.setPropertyValue(ci, getShippingPriceInfoProperty(), amtInfo);
      readProperties(order, amtInfo, getLoadProperties(), piRepItem, desc, orderManager);
      loadPricingAdjustments(order, amtInfo, piRepItem, orderManager, invalidateCache);
    }

    if (ci instanceof ChangedProperties)
      ((ChangedProperties) ci).clearChangedProperties();
  }

  //-----------------------------------------------
  /**
   * This method is a pass through to the
   * <code>loadSubtotalPriceInfos<code> method.  It passes in the 
   * <code>shippingItemsSubtotalPriceInfosProperty</code> value as
   * the property name
   * @see #loadSubtotalPriceInfos
   **/
  protected void loadShippingItemsSubtotalPriceInfos(Order order, Object obj,
                    MutableRepositoryItem piRepItem, OrderManager orderManager, Boolean invalidateCache)
                    throws Exception
    {
	loadSubtotalPriceInfos(order,obj,piRepItem,orderManager,getShippingItemsSubtotalPriceInfosProperty(), invalidateCache);
    }

  //-----------------------------------------------
  /**
   * This method is a pass through to the
   * <code>loadSubtotalPriceInfos<code> method.  It passes in the 
   * <code>taxableShippingItemsSubtotalPriceInfosProperty</code> value as
   * the property name
   * @see #loadSubtotalPriceInfos
   **/
  protected void loadTaxableShippingItemsSubtotalPriceInfos(Order order, Object obj,
                    MutableRepositoryItem piRepItem, OrderManager orderManager, Boolean invalidateCache)
                    throws Exception
    {
	loadSubtotalPriceInfos(order,obj,piRepItem,orderManager,getTaxableShippingItemsSubtotalPriceInfosProperty(), invalidateCache);
    }

  //-----------------------------------------------
  /**
   * This method is a pass through to the
   * <code>loadSubtotalPriceInfos<code> method.  It passes in the 
   * <code>nonTaxableShippingItemsSubtotalPriceInfosProperty</code> value as
   * the property name
   * @see #loadSubtotalPriceInfos
   **/
  protected void loadNonTaxableShippingItemsSubtotalPriceInfos(Order order, Object obj,
                    MutableRepositoryItem piRepItem, OrderManager orderManager, Boolean invalidateCache)
                    throws Exception
    {
	loadSubtotalPriceInfos(order,obj,piRepItem,orderManager,getNonTaxableShippingItemsSubtotalPriceInfosProperty(), invalidateCache);
    }

  //-----------------------------------------------
  /**
   * This method loads a Map of (shipping group -> price info) objects into an AmountInfo.
   * See the <code>loadProperties</code> for a list
   * of properties that are copied
   *
   * @see #readProperties
   *
   * @param order The order being loaded
   * @param obj The amount info for the order's priceInfo
   * @param piRepItem The repository item for the order's priceInfo
   * @param orderManager The OrderManager in the pipeline params
   * @param pPropertyName The name of the map to load
   * @param invalidateCache If true, then each price info repository cache
   *                        entry is invalidated
   **/
  protected void loadSubtotalPriceInfos(Order order, Object obj,
                    MutableRepositoryItem piRepItem, OrderManager orderManager,String pPropertyName, Boolean invalidateCache)
                    throws Exception
  {
    OrderTools orderTools = orderManager.getOrderTools();
    Map itemMap = (Map) piRepItem.getPropertyValue(pPropertyName);
    Map objMap = new HashMap(7);
    MutableRepositoryItem mutItem;
    RepositoryItemDescriptor desc;
    Object opi;
    String className, key;

    DynamicBeans.setPropertyValue(obj, pPropertyName, objMap);

    Iterator iter = itemMap.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry)iter.next();
      key = (String)entry.getKey();
      mutItem = (MutableRepositoryItem)entry.getValue();
      desc = mutItem.getItemDescriptor();
      if (invalidateCache.booleanValue())
        invalidateCache((ItemDescriptorImpl) desc, mutItem);
      className = orderTools.getMappedBeanName(desc.getItemDescriptorName());
      opi = Class.forName(className).newInstance();
      readProperties(order, opi, getLoadProperties(), mutItem, desc, orderManager);
      loadPricingAdjustments(order, opi, mutItem, orderManager, invalidateCache);
      objMap.put(key, opi);
    }
  }

  //-----------------------------------------------
  /**
   * This method loads a Map of shippingItemsTaxPriceInfos objects into an AmountInfo.
   * See the <code>loadProperties</code> for a list
   * of properties that are copied
   *
   * @see #readProperties
   *
   * @param order The order being loaded
   * @param obj The amount info for the shipping group's priceInfo
   * @param piRepItem The repository item for the shipping group's priceInfo
   * @param orderManager The OrderManager in the pipeline params
   * @param invalidateCache If true, then each price info repository cache
   *                        entry is invalidated
   **/
  protected void loadShippingItemsTaxPriceInfos(Order order, Object obj,
                    MutableRepositoryItem piRepItem, OrderManager orderManager, Boolean invalidateCache)
                    throws Exception
  {
    OrderTools orderTools = orderManager.getOrderTools();
    Map itemMap = (Map) piRepItem.getPropertyValue(getShippingItemsTaxPriceInfosProperty());
    Map objMap = new HashMap(7);
    MutableRepositoryItem mutItem;
    RepositoryItemDescriptor desc;
    Object opi;
    String className, key;

    DynamicBeans.setPropertyValue(obj, getShippingItemsTaxPriceInfosProperty(), objMap);

    Iterator iter = itemMap.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry)iter.next();
      key = (String) entry.getKey();
      mutItem = (MutableRepositoryItem)entry.getValue();
      desc = mutItem.getItemDescriptor();
      if (invalidateCache.booleanValue())
        invalidateCache((ItemDescriptorImpl) desc, mutItem);
      className = orderTools.getMappedBeanName(desc.getItemDescriptorName());
      opi = Class.forName(className).newInstance();
      readProperties(order, opi, getLoadProperties(), mutItem, desc, orderManager);
      loadPricingAdjustments(order, opi, mutItem, orderManager, invalidateCache);
      objMap.put(key, opi);
    }
  }

  //-----------------------------------------------
  /**
   * This method loads a List of PricingAdjustment objects into an AmountInfo.
   * See the <code>pricingAdjusmtentProperties</code> for a list
   * of properties that are copied
   *
   * @see #readProperties
   *
   * @param order The order being loaded
   * @param obj The amount info
   * @param piRepItem The repository item for the amount info
   * @param orderManager The OrderManager in the pipeline params
   * @param invalidateCache If true, then each pricing adjustment repository cache
   *                        entry is invalidated
   **/
  protected void loadPricingAdjustments(Order order, Object obj,
                    MutableRepositoryItem piRepItem, OrderManager orderManager, Boolean invalidateCache)
                    throws Exception
  {
    OrderTools orderTools = orderManager.getOrderTools();
    List repList = (List) piRepItem.getPropertyValue(getAdjustmentsProperty());
    List paList = (List) DynamicBeans.getPropertyValue(obj, getAdjustmentsProperty());
    MutableRepositoryItem mutItem;
    RepositoryItemDescriptor desc;
    Object adj;
    String className;

    for (int i = 0; i < repList.size(); i++) {
      mutItem = (MutableRepositoryItem) repList.get(i);
      if (mutItem == null) {
          if (isLoggingWarning())
              logWarning("Cannot load pricing adjustment for price info " + piRepItem);
          continue;
      }
      desc = mutItem.getItemDescriptor();
      if (invalidateCache.booleanValue())
        invalidateCache((ItemDescriptorImpl) desc, mutItem);
      className = orderTools.getMappedBeanName(desc.getItemDescriptorName());
      adj = Class.forName(className).newInstance();
      readProperties(order, adj, getPricingAdjustmentProperties(), mutItem, desc, orderManager);
      paList.add(adj);
    }
  }

  //-----------------------------------------------
  /**
   * This method loads a List of DetailedItemPriceInfo objects into an
   * AmountInfo.  It also sets DetailedItemPriceInfo.itemPriceInfo to
   * amtInfo.
   * See the <code>detailedItemPriceInfoProperties</code> for a list
   * of properties that are copied
   *
   * @see #readProperties
   * @see #loadPricingAdjustments
   * @see #loadDetailedRange
   *
   * @param order The order being loaded
   * @param amtInfo The amount info for the commerce item
   * @param piRepItem The repository item for the amount info
   * @param orderManager The OrderManager in the pipeline params
   * @param invalidateCache If true, then each pricing adjustment repository cache
   *                        entry is invalidated
   **/
  protected void loadDetailedItemPriceInfos(Order order, Object amtInfo,
                    MutableRepositoryItem piRepItem, OrderManager orderManager, Boolean invalidateCache)
                    throws Exception
  {
    OrderTools orderTools = orderManager.getOrderTools();
    List repList = (List) piRepItem.getPropertyValue(getCurrentPriceDetailsProperty());
    List detList = (List) DynamicBeans.getPropertyValue(amtInfo, getCurrentPriceDetailsProperty());
    MutableRepositoryItem mutItem;
    RepositoryItemDescriptor desc;
    Object det;
    String className;

    for (int i = 0; i < repList.size(); i++) {
      mutItem = (MutableRepositoryItem) repList.get(i);
      desc = mutItem.getItemDescriptor();
      if (invalidateCache.booleanValue())
        invalidateCache((ItemDescriptorImpl) desc, mutItem);
      className = orderTools.getMappedBeanName(desc.getItemDescriptorName());
      det = Class.forName(className).newInstance();
      readProperties(order, det, getDetailedItemPriceInfoProperties(), mutItem, desc, orderManager);
      loadPricingAdjustments(order, det, mutItem, orderManager, invalidateCache);
      loadDetailedRange(order, det, mutItem, orderManager);
      // set the itemPriceInfo
      DynamicBeans.setPropertyValue(det, getDetailsItemPriceInfoProperty(), amtInfo);
      detList.add(det);
    }
  }

  //-----------------------------------------------
  /**
   * This method loads a List of properties from a repository item into an object.
   *
   * @param order The order being loaded
   * @param obj The object that each property is copied into
   * @param loadProperties The list of properties to load
   * @param mutItem The repository item that each property is copied from
   * @param desc The item descriptor for mutItem
   * @param orderManager The OrderManager in the pipeline params
   **/
  protected void readProperties(Order order, Object obj, String[] loadProperties,
                      MutableRepositoryItem mutItem, RepositoryItemDescriptor desc,
                      OrderManager orderManager)
                      throws PropertyNotFoundException, IntrospectionException
  {
    String mappedPropName;
    Object value;

    // this is where the properties are loaded from the repository into the order
    for (int i = 0; i < loadProperties.length; i++) {
      mappedPropName = getMappedPropertyName(loadProperties[i]);
      if (desc.hasProperty(loadProperties[i])) {
        value = mutItem.getPropertyValue(loadProperties[i]);
        if (isLoggingDebug())
          logDebug("load property[" + loadProperties[i] + ":" + value + ":"
                      + obj.getClass().getName() + "]");
        OrderRepositoryUtils.setPropertyValue(order, obj, mappedPropName, value);
      } // if
    } // for
  }

  /**
   * Load the range from the repository item into the
   * DetailedItemPriceInfo's Range property
   * See the <code>detailsRangeProperties</code> for a list
   * of properties that are copied
   *
   * @see #readProperties
   *
   * @param pOrder The order being loaded
   * @param pDetail The detailed item price being loaded
   * @param pMutItem The repository item corresponding to the detail
   * @param pOrderManager The order manager in the pipeline params
   **/
  protected void loadDetailedRange(Order pOrder, Object pDetail,
                                   MutableRepositoryItem pMutItem, OrderManager pOrderManager)
    throws Exception
  {
    String props[] = getDetailsRangeProperties();
    if((props == null) ||
       (props.length == 0))
      return;
    Object low = pMutItem.getPropertyValue(props[0]);
    if(low == null)
      return;
    RepositoryItemDescriptor desc = pMutItem.getItemDescriptor();
    Object range = Class.forName(getRangeClass()).newInstance();
    readProperties(pOrder, range, props, pMutItem, desc, pOrderManager);
    DynamicBeans.setPropertyValue(pDetail, getRangePropertyName(), range);
  }
}
