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

import atg.beans.DynamicBeans;
import atg.repository.*;
import atg.commerce.order.*;
import atg.commerce.states.*;
import atg.commerce.pricing.*;
import atg.commerce.CommerceException;
import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;
import atg.beans.PropertyNotFoundException;

import java.beans.IntrospectionException;
import java.util.*;
import atg.core.util.Range;

/**
 * This processor saves the PriceInfo objects into the OrderRepository from the Order, CommerceItem
 * and ShippingGroup objects.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSavePriceInfoObjects.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.commerce.pricing.AmountInfo
 * @see atg.commerce.pricing.OrderPriceInfo
 * @see atg.commerce.pricing.ItemPriceInfo
 * @see atg.commerce.pricing.ShippingPriceInfo
 * @see atg.commerce.pricing.TaxPriceInfo
 */
public class ProcSavePriceInfoObjects extends SavedProperties implements PipelineProcessor {

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSavePriceInfoObjects.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  private final int SUCCESS = 1;

  /**
   * The possible "saveModes" for saving the PriceInfo objects:
   * 
   * ALL - Saves all PriceInfo objects associated with the order.
   * ALL_NO_AUDIT - Saves all PriceInfo objects associated with the order, but does not
   *                save the audit trail information associated with those PriceInfos.
   *                Audit trail information includes PricingAdjustments and 
   *                DetailedItemPriceInfos
   * ORDER - Saves only the OrderPriceInfo object (no ItemPriceInfos, ShippingPriceInfos,
   *         or TaxPriceInfos)
   * ORDER_NO_AUDIT - Saves only the OrderPriceInfo object, and does not save the audit
   *                  trail information associated with that object
   * NONE - Saves no PriceInfo objects
   **/

  protected static final String ALL = "ALL";
  protected static final String ALL_NO_AUDIT = "ALL_NO_AUDIT";
  protected static final String ORDER = "ORDER";
  protected static final String ORDER_NO_AUDIT = "ORDER_NO_AUDIT";
  protected static final String NONE = "NONE";
  protected static final String[] VALID_SAVE_MODES = {ALL, ALL_NO_AUDIT, ORDER, ORDER_NO_AUDIT, NONE};

  //-----------------------------------------------
  public ProcSavePriceInfoObjects() {
  }

  //-----------------------------------------------
  // Member variables
  //-----------------------------------------------
  protected boolean mVerifiedSaveModes = false;

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
  // property: savePriceInfosInIncompleteOrders
  //-------------------------------------
  /**
   * Returns property savePriceInfosInIncompleteOrders
   *
   * @return returns property savePriceInfosInIncompleteOrders
   */
  public boolean getSavePriceInfosInIncompleteOrders() {
    OrderStates os = StateDefinitions.ORDERSTATES;
    String incompleteState = os.getStateString(os.getStateValue(OrderStates.INCOMPLETE));
    Map orderStateSaveModes = getOrderStateSaveModes();
      
    return ALL.equals(orderStateSaveModes.get(incompleteState));
  }

  /**
   * Sets property savePriceInfosInIncompleteOrders
   *
   * @param pSavePriceInfosInIncompleteOrders the value to set for property savePriceInfosInIncompleteOrders
   * @deprecated Use orderStateSaveModes instead
   */
  public void setSavePriceInfosInIncompleteOrders(boolean pSavePriceInfosInIncompleteOrders) {
    if (isLoggingWarning())
      logWarning(ResourceUtils.getMsgResource("SettingSavePriceInfosInIncompleteOrders",
                                              MY_RESOURCE_NAME, sResourceBundle));

    OrderStates os = StateDefinitions.ORDERSTATES;
    String incompleteState = os.getStateString(os.getStateValue(OrderStates.INCOMPLETE));
    Map orderStateSaveModes = getOrderStateSaveModes();
      
    orderStateSaveModes.remove(incompleteState);

    if (pSavePriceInfosInIncompleteOrders == true) {
      orderStateSaveModes.put(incompleteState, ALL);
      if (isLoggingWarning())
        logWarning(ResourceUtils.getMsgResource("SettingIncompleteAll",
                                                MY_RESOURCE_NAME, sResourceBundle));
    }
    else {
      orderStateSaveModes.put(incompleteState, NONE);
      if (isLoggingWarning())
        logWarning(ResourceUtils.getMsgResource("SettingIncompleteNone",
                                                MY_RESOURCE_NAME, sResourceBundle));
    }
  }

  //-------------------------------------
  // property: orderStateSaveModes
  //-------------------------------------
  private Map mOrderStateSaveModes;

  /**
   * Mapping of order states to the "saveMode" that indicates the level of pricing
   * information that should be saved for an order in that state.
   *
   * @return mapping of order states to the "saveMode" that indicates the level of
   *         pricing information that should be saved for an order in that state.
   */
  public Map getOrderStateSaveModes() {
    return mOrderStateSaveModes;
  }

  /**
   * Mapping of order states to the "saveMode" that indicates the level of pricing
   * information that should be saved for an order in that state.
   *
   * The possible "saveModes" for saving the PriceInfo objects:
   * 
   * ALL - Saves all PriceInfo objects associated with the order.
   * ALL_NO_AUDIT - Saves all PriceInfo objects associated with the order, but does not
   *                save the audit trail information associated with those PriceInfos.
   *                Audit trail information includes PricingAdjustments and 
   *                DetailedItemPriceInfos
   * ORDER - Saves only the OrderPriceInfo object (no ItemPriceInfos, ShippingPriceInfos,
   *         or TaxPriceInfos)
   * ORDER_NO_AUDIT - Saves only the OrderPriceInfo object, and does not save the audit
   *                  trail information associated with that object
   * NONE - Saves no PriceInfo objects
   *
   * @param pOrderStateSaveModes mapping of order states to the "saveMode" that 
   *        indicates the level of pricing information that should be saved for an 
   *        order in that state.
   */
  public void setOrderStateSaveModes(Map pOrderStateSaveModes) {
    mOrderStateSaveModes = pOrderStateSaveModes;
  }

  //-------------------------------------
  // property: defaultSaveMode
  //-------------------------------------
  private String mDefaultSaveMode = "ALL";

  /**
   * The saveMode to be used for orders that do not have a corresponding
   * entry in orderStateSaveModes
   *
   * @return the saveMode to be used for orders that do not have a corresponding
   *         entry in orderStateSaveModes
   */
  public String getDefaultSaveMode() {
    return mDefaultSaveMode;
  }

  /**
   * The saveMode to be used for orders that do not have a corresponding
   * entry in orderStateSaveModes
   *
   * @param pDefaultSaveMode he saveMode to be used for orders that do not 
   *        have a corresponding entry in orderStateSaveModes
   */
  public void setDefaultSaveMode(String pDefaultSaveMode) {
    mDefaultSaveMode = pDefaultSaveMode;
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
  // property: shippingItemsSubtotalPriceInfosProperty
  //-------------------------------------
  private String mShippingItemsSubtotalPriceInfosProperty = "shippingItemsSubtotalPriceInfos";

  /**
   * Returns property ShippingItemsSubtotalPriceInfosProperty
   *
   * @return returns property ShippingItemsSubtotalPriceInfosProperty
   */
  public String getShippingItemsSubtotalPriceInfosProperty() {
    return mShippingItemsSubtotalPriceInfosProperty;
  }

  /**
   * Sets property ShippingItemsSubtotalPriceInfosProperty
   *
   * @param pShippingItemsSubtotalPriceInfosProperty the value to set for property ShippingItemsSubtotalPriceInfosProperty
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
   * Returns property ShippingItemsTaxPriceInfosProperty
   *
   * @return returns property ShippingItemsTaxPriceInfosProperty
   */
  public String getShippingItemsTaxPriceInfosProperty() {
    return mShippingItemsTaxPriceInfosProperty;
  }

  /**
   * Sets property ShippingItemsTaxPriceInfosProperty
   *
   * @param pShippingItemsTaxPriceInfosProperty the value to set for property ShippingItemsTaxPriceInfosProperty
   */
  public void setShippingItemsTaxPriceInfosProperty(String pShippingItemsTaxPriceInfosProperty) {
    mShippingItemsTaxPriceInfosProperty = pShippingItemsTaxPriceInfosProperty;
  }

  //-------------------------------------
  // property: orderPriceInfoDescName
  //-------------------------------------
  private String mOrderPriceInfoDescName = "orderPriceInfo";

  /**
   * Returns property orderPriceInfoDescName
   *
   * @return returns property orderPriceInfoDescName
   */
  public String getOrderPriceInfoDescName() {
    return mOrderPriceInfoDescName;
  }

  /**
   * Sets property orderPriceInfoDescName
   *
   * @param pOrderPriceInfoDescName the value to set for property orderPriceInfoDescName
   */
  public void setOrderPriceInfoDescName(String pOrderPriceInfoDescName) {
    mOrderPriceInfoDescName = pOrderPriceInfoDescName;
  }

  //-------------------------------------
  // property: taxPriceInfoDescName
  //-------------------------------------
  private String mTaxPriceInfoDescName = "taxPriceInfo";

  /**
   * Returns property taxPriceInfoDescName
   *
   * @return returns property taxPriceInfoDescName
   */
  public String getTaxPriceInfoDescName() {
    return mTaxPriceInfoDescName;
  }

  /**
   * Sets property taxPriceInfoDescName
   *
   * @param pTaxPriceInfoDescName the value to set for property taxPriceInfoDescName
   */
  public void setTaxPriceInfoDescName(String pTaxPriceInfoDescName) {
    mTaxPriceInfoDescName = pTaxPriceInfoDescName;
  }

  //-------------------------------------
  // property: detailedItemPriceInfoDescName
  //-------------------------------------
  private String mDetailedItemPriceInfoDescName = "detailedItemPriceInfo";

  /**
   * Returns the detailedItemPriceInfoDescName
   */
  public String getDetailedItemPriceInfoDescName() {
    return mDetailedItemPriceInfoDescName;
  }

  /**
   * Sets the detailedItemPriceInfoDescName
   */
  public void setDetailedItemPriceInfoDescName(String pDetailedItemPriceInfoDescName) {
    mDetailedItemPriceInfoDescName = pDetailedItemPriceInfoDescName;
  }

  //-------------------------------------
  // property: pricingAdjustmentDescName
  //-------------------------------------
  private String mPricingAdjustmentDescName = "pricingAdjustment";

  /**
   * Returns the pricingAdjustmentDescName
   */
  public String getPricingAdjustmentDescName() {
    return mPricingAdjustmentDescName;
  }

  /**
   * Sets the pricingAdjustmentDescName
   */
  public void setPricingAdjustmentDescName(String pPricingAdjustmentDescName) {
    mPricingAdjustmentDescName = pPricingAdjustmentDescName;
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
  String mLoggingIdentifier = "ProcSavePriceInfoObjects";

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

  private String getSaveMode(Order pOrder) {
     String saveMode = 
       (String) getOrderStateSaveModes().get(StateDefinitions.ORDERSTATES.getStateString(pOrder.getState()));

     if(saveMode == null) 
       saveMode = getDefaultSaveMode();

     return saveMode;
  }

  /**
   * This method saves the PriceInfo objects into the OrderRepository from the Order, CommerceItem,
   * and ShippingGroup objects. It iterates through the properties listed in the saveProperties
   * property inherited by this class, setting the values in the repository.
   *
   * This method requires that an Order, OrderRepository, and OrderManager object
   * be supplied in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order, OrderRepository, and OrderManager object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   * @see atg.commerce.pricing.AmountInfo
   * @see atg.commerce.pricing.OrderPriceInfo
   * @see atg.commerce.pricing.ItemPriceInfo
   * @see atg.commerce.pricing.ShippingPriceInfo
   * @see atg.commerce.pricing.TaxPriceInfo
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    Repository repository = (Repository) map.get(PipelineConstants.ORDERREPOSITORY);
    OrderManager orderManager = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);
    MutableRepository mutRep;

    // check for null parameters
    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (repository == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidRepositoryParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (orderManager == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderManagerParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));

    // First time we run through, we need to verify that the saveModes are valid
    if (!mVerifiedSaveModes)
      verifySaveModes();

    OrderTools orderTools = orderManager.getOrderTools();

    try {
      mutRep = (MutableRepository) repository;
    }
    catch (ClassCastException e) {
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidRepositoryParameter",
                                      MY_RESOURCE_NAME, sResourceBundle), e);
    }

    String saveMode = 
      (String) getOrderStateSaveModes().get(StateDefinitions.ORDERSTATES.getStateString(order.getState()));
    
    if (isLoggingDebug())
      logDebug("Mapped save mode for order state: " + 
               StateDefinitions.ORDERSTATES.getStateString(order.getState()) + 
               " is " + saveMode);
    
    if (saveMode == null) {
      if (isLoggingDebug())
	      logDebug("No mapped save mode for this order state.  Using default save mode: " + 
                 getDefaultSaveMode());

      saveMode = getDefaultSaveMode();
    }

    if (saveMode.equals(NONE)) return SUCCESS;

    // If you reached this point you will definitely be writing to the DB so set the CHANGED
    // flag to TRUE
    map.put(PipelineConstants.CHANGED, Boolean.TRUE);
    if (isLoggingDebug())
      logDebug("Set changed flag to true in ProcSavePriceInfoObjects");

    MutableRepositoryItem orderRepItem = null, cItemRepItem = null, groupRepItem = null;

    if (order instanceof ChangedProperties)
      orderRepItem = ((ChangedProperties) order).getRepositoryItem();

    if (orderRepItem == null) {
      orderRepItem = mutRep.getItemForUpdate(order.getId(),
                orderTools.getMappedItemDescriptorName(order.getClass().getName()));
      if (order instanceof ChangedProperties)
        ((ChangedProperties) order).setRepositoryItem(orderRepItem);
    }

    saveOrderPriceInfo(order, orderRepItem, mutRep, orderManager);
    
    // Only save remaining priceInfos if saveMode is one of the "ALL" modes
    if (saveMode.equals(ALL) || saveMode.equals(ALL_NO_AUDIT)) {
      saveTaxPriceInfo(order, orderRepItem, mutRep, orderManager);

      Iterator iter = order.getCommerceItems().iterator();
      for (CommerceItem cItem = null; iter.hasNext(); ) {
        cItem = (CommerceItem) iter.next();

        cItemRepItem = null;
        if (cItem instanceof ChangedProperties)
          cItemRepItem = ((ChangedProperties) cItem).getRepositoryItem();

        if (cItemRepItem == null) {
          cItemRepItem = mutRep.getItemForUpdate(cItem.getId(),
                    orderTools.getMappedItemDescriptorName(cItem.getClass().getName()));
          if (cItem instanceof ChangedProperties)
            ((ChangedProperties) cItem).setRepositoryItem(cItemRepItem);
        }

        saveItemPriceInfo(order, cItem, cItemRepItem, mutRep, orderManager);
      }

      iter = order.getShippingGroups().iterator();
      for (ShippingGroup group = null; iter.hasNext(); ) {
        group = (ShippingGroup) iter.next();

        groupRepItem = null;
        if (group instanceof ChangedProperties)
          groupRepItem = ((ChangedProperties) group).getRepositoryItem();

        if (groupRepItem == null) {
          groupRepItem = mutRep.getItemForUpdate(group.getId(),
                    orderTools.getMappedItemDescriptorName(group.getClass().getName()));
          if (group instanceof ChangedProperties)
            ((ChangedProperties) group).setRepositoryItem(groupRepItem);
        }

        saveShippingPriceInfo(order, group, groupRepItem, mutRep, orderManager);
      }
    }

    try {
      mutRep.updateItem(orderRepItem);
    }
    catch (ConcurrentUpdateException e) {
      String[] msgArgs = { order.getId(), orderRepItem.getItemDescriptor().getItemDescriptorName() };
      throw new CommerceException(ResourceUtils.getMsgResource("ConcurrentUpdateAttempt",
                                      MY_RESOURCE_NAME, sResourceBundle, msgArgs), e);
    }

    if (order instanceof ChangedProperties) {
      ChangedProperties cp = (ChangedProperties) order;
      cp.clearChangedProperties();
      cp.setSaveAllProperties(false);
    }
    
    return SUCCESS;
  }

  //-----------------------------------------------
  /**
   * This method saves the OrderPriceInfo object in the Order class.
   *
   * @see #savePriceInfo
   *
   * @param order The order being saved
   * @param orderRepItem The repository item corresponding to the order
   * @param mutRep The repository where the order is being saved
   * @param orderManager The OrderManager from the pipeline params
   **/
  protected void saveOrderPriceInfo(Order order, MutableRepositoryItem orderRepItem,
                        MutableRepository mutRep, OrderManager orderManager)
                        throws RepositoryException, IntrospectionException, PropertyNotFoundException,
                              CommerceException
  {
    MutableRepositoryItem piRepItem = null;
    boolean hasProperty = false;

    if (DynamicBeans.getBeanInfo(order).hasProperty("priceInfoRepositoryItem")) {
      hasProperty = true;
      piRepItem = (MutableRepositoryItem) DynamicBeans.getPropertyValue(order, "priceInfoRepositoryItem");
    }

    if (piRepItem == null) {
      piRepItem = (MutableRepositoryItem) orderRepItem.getPropertyValue(getOrderPriceInfoProperty());
      if (hasProperty)
        DynamicBeans.setPropertyValue(order, "priceInfoRepositoryItem", piRepItem);
    }

    piRepItem = savePriceInfo(order, order, piRepItem, (hasProperty ? "priceInfoRepositoryItem" : null),
                              order.getPriceInfo(), mutRep, orderManager);
    orderRepItem.setPropertyValue(getOrderPriceInfoProperty(), piRepItem);
  }

  //-----------------------------------------------
  /**
   * This method saves the TaxPriceInfo object in the Order class.
   *
   * @see #savePriceInfo
   *
   * @param order The order being saved
   * @param orderRepItem The repository item corresponding to the order
   * @param mutRep The repository where the order is being saved
   * @param orderManager The OrderManager from the pipeline params
   **/
  protected void saveTaxPriceInfo(Order order, MutableRepositoryItem orderRepItem,
                        MutableRepository mutRep, OrderManager orderManager)
                        throws RepositoryException, IntrospectionException, PropertyNotFoundException,
                              CommerceException
  {
    MutableRepositoryItem tpiRepItem = null;
    boolean hasProperty = false;

    if (DynamicBeans.getBeanInfo(order).hasProperty("taxPriceInfoRepositoryItem")) {
      hasProperty = true;
      tpiRepItem = (MutableRepositoryItem) DynamicBeans.getPropertyValue(order, "taxPriceInfoRepositoryItem");
    }

    if (tpiRepItem == null) {
      tpiRepItem = (MutableRepositoryItem) orderRepItem.getPropertyValue(getTaxPriceInfoProperty());
      if (hasProperty)
        DynamicBeans.setPropertyValue(order, "taxPriceInfoRepositoryItem", tpiRepItem);
    }

    tpiRepItem = savePriceInfo(order, order, tpiRepItem, (hasProperty ? "taxPriceInfoRepositoryItem" : null),
                               order.getTaxPriceInfo(), mutRep, orderManager);
    orderRepItem.setPropertyValue(getTaxPriceInfoProperty(), tpiRepItem);
  }

  //-----------------------------------------------
  /**
   * This method saves an ItemPriceInfo object. If the input commerceItem is a configurable sku,
   * then the method loads the ItemPriceInfo of all the subskus of the configurable sku.
   *
   * @see #savePriceInfo
   *
   * @param order The order being saved
   * @param cItem The commerce item object
   * @param cItemRepItem The repository item corresponding to the commerce item
   * @param mutRep The repository where the order is being saved
   * @param orderManager The OrderManager from the pipeline params
   **/
  protected void saveItemPriceInfo(Order order, CommerceItem cItem, MutableRepositoryItem cItemRepItem,
                        MutableRepository mutRep, OrderManager orderManager)
                        throws RepositoryException, IntrospectionException, PropertyNotFoundException,
                              CommerceException
  {
    MutableRepositoryItem ipiRepItem = null;
    boolean hasProperty = false;

    if (DynamicBeans.getBeanInfo(cItem).hasProperty("priceInfoRepositoryItem")) {
      hasProperty = true;
      ipiRepItem = (MutableRepositoryItem) DynamicBeans.getPropertyValue(cItem, "priceInfoRepositoryItem");
    }

    if (ipiRepItem == null) {
      ipiRepItem = (MutableRepositoryItem) cItemRepItem.getPropertyValue(getItemPriceInfoProperty());
      if (hasProperty)
        DynamicBeans.setPropertyValue(cItem, "priceInfoRepositoryItem", ipiRepItem);
    }

    ipiRepItem = savePriceInfo(order, cItem, ipiRepItem, (hasProperty ? "priceInfoRepositoryItem" : null),
                               cItem.getPriceInfo(), mutRep, orderManager);
    cItemRepItem.setPropertyValue(getItemPriceInfoProperty(), ipiRepItem);

    try {
      mutRep.updateItem(cItemRepItem);
    }
    catch (ConcurrentUpdateException e) {
      String[] msgArgs = { order.getId(), cItemRepItem.getItemDescriptor().getItemDescriptorName() };
      throw new CommerceException(ResourceUtils.getMsgResource("ConcurrentUpdateAttempt",
                                      MY_RESOURCE_NAME, sResourceBundle, msgArgs), e);
    }

    if (cItem instanceof ChangedProperties) {
      ChangedProperties cp = (ChangedProperties) cItem;
      cp.clearChangedProperties();
      cp.setSaveAllProperties(false);
    }

    /* If the item is a configurable SKU, then save the priceinfo for the subskus */
    if(cItem instanceof CommerceItemContainer) {
      if(isLoggingDebug())
        logDebug("Saving all subskus for a configurable sku" + cItemRepItem);
      saveSubSkuPriceInfo(order, cItem, cItemRepItem, mutRep, orderManager);
    }
  }

  //-----------------------------------------------
  /**
   * This method saves ItemPriceInfo objects for skus of a configurable sku.
   *
   * @see #savePriceInfo
   *
   * @param order The order being saved
   * @param cItem The configurable commerce item whose price is being saved
   * @param configRepItem The repository item corresponding to the commerce item
   * @param mutRep The repository where the order is being saved
   * @param orderManager The OrderManager from the pipeline params
   **/
  protected void saveSubSkuPriceInfo(Order order, CommerceItem cItem, MutableRepositoryItem configRepItem,
                        MutableRepository mutRep, OrderManager orderManager)
                        throws RepositoryException, IntrospectionException, PropertyNotFoundException,
                              CommerceException
  {
    /* Iterate thru the configurable sku and get all its subskus. For each subsku, save its itemPriceInfo. */
    List repList = (List) configRepItem.getPropertyValue("commerceItems");
    Iterator itemIter = repList.iterator();
    List subSkuList = (List) DynamicBeans.getPropertyValue(cItem, "commerceItems");
    Iterator classItr = subSkuList.iterator();

    while (itemIter.hasNext() && (classItr.hasNext())) {

      MutableRepositoryItem subskuItem = (MutableRepositoryItem) itemIter.next();
      CommerceItem skuClass = (CommerceItem) classItr.next();


      saveItemPriceInfo(order, skuClass, subskuItem, mutRep, orderManager);

    }
  }

  //-----------------------------------------------
  /**
   * This method saves a ShippingPriceInfo object.
   *
   * @see #savePriceInfo
   *
   * @param order The order being saved
   * @param group The shipping group whose price is being saved
   * @param groupRepItem The repository item corresponding to the shipping group
   * @param mutRep The repository where the order is being saved
   * @param orderManager The OrderManager from the pipeline params
   **/
  protected void saveShippingPriceInfo(Order order, ShippingGroup group, MutableRepositoryItem groupRepItem,
                        MutableRepository mutRep, OrderManager orderManager)
                        throws RepositoryException, IntrospectionException, PropertyNotFoundException,
                              CommerceException
  {
    MutableRepositoryItem spiRepItem = null;
    boolean hasProperty = false;

    if (DynamicBeans.getBeanInfo(group).hasProperty("priceInfoRepositoryItem")) {
      hasProperty = true;
      spiRepItem = (MutableRepositoryItem) DynamicBeans.getPropertyValue(group, "priceInfoRepositoryItem");
    }

    if (spiRepItem == null) {
      spiRepItem = (MutableRepositoryItem) groupRepItem.getPropertyValue(getShippingPriceInfoProperty());
      if (hasProperty)
        DynamicBeans.setPropertyValue(group, "priceInfoRepositoryItem", spiRepItem);
    }

    spiRepItem = savePriceInfo(order, group, spiRepItem, (hasProperty ? "priceInfoRepositoryItem" : null),
                               group.getPriceInfo(), mutRep, orderManager);
    groupRepItem.setPropertyValue(getShippingPriceInfoProperty(), spiRepItem);

    try {
      mutRep.updateItem(groupRepItem);
    }
    catch (ConcurrentUpdateException e) {
      String[] msgArgs = { order.getId(), groupRepItem.getItemDescriptor().getItemDescriptorName() };
      throw new CommerceException(ResourceUtils.getMsgResource("ConcurrentUpdateAttempt",
                                      MY_RESOURCE_NAME, sResourceBundle, msgArgs), e);
    }

    if (group instanceof ChangedProperties) {
      ChangedProperties cp = (ChangedProperties) group;
      cp.clearChangedProperties();
      cp.setSaveAllProperties(false);
    }
  }
  //-----------------------------------------------
  /**
   * This method saves an individual PriceInfo object.  It is called
   * by all the other save methods.  It will call the following methods 
   * as relevant:
   *
   * @see #writeProperties
   * @see #savePricingAdjustments
   * @see #saveDetailedItemPriceInfos
   * @see #saveShippingItemsSubtotalPriceInfos
   * @see #saveTaxableShippingItemsSubtotalPriceInfos
   * @see #saveNonTaxableShippingItemsSubtotalPriceInfos
   * @see #saveShippingItemsTaxPriceInfos
   *
   * @param order The order being saved
   * @param ci The commerce object whose price info is being saved
   * @param piRepItem The repository item corresponding to the commerce object
   * @param repItemPropName The property of piRepItem that stores the price info
   * @param pPriceInfo The price info object that will be saved to piRepItem.repItemPropName
   * @param mutRep The repository where the order is being saved
   * @param orderManager The OrderManager from the pipeline params
   **/
  protected MutableRepositoryItem savePriceInfo(Order order, CommerceIdentifier ci,
                               MutableRepositoryItem piRepItem,
                               String repItemPropName, AmountInfo pPriceInfo,
                               MutableRepository mutRep, OrderManager orderManager)
                               throws RepositoryException, IntrospectionException, PropertyNotFoundException,
                                      CommerceException
  {
    OrderTools orderTools = orderManager.getOrderTools();

    if (pPriceInfo == null && piRepItem == null) {
      return null;
    }
    else if (pPriceInfo == null && piRepItem != null) {
      if (repItemPropName != null)
        DynamicBeans.setPropertyValue(ci, repItemPropName, null);
      return null;
    }
    if (piRepItem == null) {
      piRepItem = mutRep.createItem(orderTools.getMappedItemDescriptorName(pPriceInfo.getClass().getName()));
      if (isLoggingDebug())
        logDebug("create in repository[" + piRepItem.getItemDescriptor().getItemDescriptorName() + ":" + piRepItem.getRepositoryId() + "]");
      if (repItemPropName != null && DynamicBeans.getBeanInfo(ci).hasProperty(repItemPropName))
        DynamicBeans.setPropertyValue(ci, repItemPropName, piRepItem);
    }
       
    writeProperties(order, getSavedProperties(), pPriceInfo, piRepItem, mutRep, orderManager);

    String saveMode = getSaveMode(order);

    // Only save audit trail information if configured to do so.  If in one of the
    // "NO_AUDIT" save modes, we do not save this information.
    if (saveMode.equals(ALL) || saveMode.equals(ORDER)) {
      savePricingAdjustments(order, pPriceInfo, piRepItem, mutRep, orderManager);


      if (piRepItem.getItemDescriptor().hasProperty(getCurrentPriceDetailsProperty()))
        saveDetailedItemPriceInfos(order, pPriceInfo, piRepItem, mutRep, orderManager);
    }

    if (piRepItem.getItemDescriptor().hasProperty(getShippingItemsSubtotalPriceInfosProperty()))
      saveShippingItemsSubtotalPriceInfos(order, pPriceInfo, piRepItem, mutRep, orderManager);
    if (piRepItem.getItemDescriptor().hasProperty(getTaxableShippingItemsSubtotalPriceInfosProperty()))
      saveTaxableShippingItemsSubtotalPriceInfos(order, pPriceInfo, piRepItem, mutRep, orderManager);
    if (piRepItem.getItemDescriptor().hasProperty(getNonTaxableShippingItemsSubtotalPriceInfosProperty()))
      saveNonTaxableShippingItemsSubtotalPriceInfos(order, pPriceInfo, piRepItem, mutRep, orderManager);
    if (piRepItem.getItemDescriptor().hasProperty(getShippingItemsTaxPriceInfosProperty()))
      saveShippingItemsTaxPriceInfos(order, pPriceInfo, piRepItem, mutRep, orderManager);

    return piRepItem;
  }

  //-----------------------------------------------
  /**
   * This method is a pass through to the
   * <code>saveSubtotalPriceInfos<code> method.  It passes in the 
   * <code>shippingItemsSubtotalPriceInfosProperty</code> value as
   * the property name
   * @see #saveSubtotalPriceInfos
   **/
  protected void saveShippingItemsSubtotalPriceInfos(Order order, AmountInfo pPriceInfo, MutableRepositoryItem mutItem,
                               MutableRepository mutRep, OrderManager orderManager)
                               throws RepositoryException, IntrospectionException, PropertyNotFoundException,
                                      CommerceException
  {
    saveSubtotalPriceInfos(order,pPriceInfo,mutItem,mutRep,orderManager,getShippingItemsSubtotalPriceInfosProperty());
  }

  //-----------------------------------------------
  /**
   * This method is a pass through to the
   * <code>saveSubtotalPriceInfos<code> method.  It passes in the 
   * <code>taxableShippingItemsSubtotalPriceInfosProperty</code> value as
   * the property name
   * @see #saveSubtotalPriceInfos
   **/
  protected void saveTaxableShippingItemsSubtotalPriceInfos(Order order, AmountInfo pPriceInfo, 
                                                            MutableRepositoryItem mutItem,
                               MutableRepository mutRep, OrderManager orderManager)
                               throws RepositoryException, IntrospectionException, PropertyNotFoundException,
                                      CommerceException
  {
    saveSubtotalPriceInfos(order,pPriceInfo,mutItem,mutRep,orderManager,getTaxableShippingItemsSubtotalPriceInfosProperty());
  }

  //-----------------------------------------------
  /**
   * This method is a pass through to the
   * <code>saveSubtotalPriceInfos<code> method.  It passes in the 
   * <code>nonTaxableShippingItemsSubtotalPriceInfosProperty</code> value as
   * the property name
   * @see #saveSubtotalPriceInfos
   **/
  protected void saveNonTaxableShippingItemsSubtotalPriceInfos(Order order, AmountInfo pPriceInfo, 
                                                               MutableRepositoryItem mutItem,
                               MutableRepository mutRep, OrderManager orderManager)
                               throws RepositoryException, IntrospectionException, PropertyNotFoundException,
                                      CommerceException
  {
    saveSubtotalPriceInfos(order,pPriceInfo,mutItem,mutRep,orderManager,getNonTaxableShippingItemsSubtotalPriceInfosProperty());
  }


  //-----------------------------------------------
  /**
   * This method saves a Map of (shipping group -> price info) objects into the repository
   * See the <code>savedProperties</code> for a list
   * of properties that are copied
   *
   * @see #writeProperties
   * @see #savePricingAdjustments
   * @see #saveDetailedItemPriceInfos
   *
   * @param order The order being loaded
   * @param pPriceInfo The amount info being saved
   * @param mutItem The repository item for the pPriceInfo
   * @param mutRep The repository being updated
   * @param orderManager The OrderManager in the pipeline params
   * @param pPropertyName The name of the map to load
   **/
  protected void saveSubtotalPriceInfos(Order order, AmountInfo pPriceInfo, MutableRepositoryItem mutItem,
                               MutableRepository mutRep, OrderManager orderManager,String pPropertyName)
                               throws RepositoryException, IntrospectionException, PropertyNotFoundException,
                                      CommerceException
  {
    Map shipSubtotalMap = (Map) DynamicBeans.getPropertyValue(pPriceInfo, pPropertyName);
    Map shipSubtotalItemMap = (Map) mutItem.getPropertyValue(pPropertyName);
    MutableRepositoryItem opiMutItem;
    OrderPriceInfo opi;
    Iterator iter;
    String key;

    if (shipSubtotalMap == null || shipSubtotalMap.size() == 0) {
      shipSubtotalItemMap.clear();
      return;
    }
    else {
      Set objSet = shipSubtotalMap.keySet();
      Set itemSet = shipSubtotalItemMap.keySet();

      iter = objSet.iterator();
      while (iter.hasNext()) {
        key = (String) iter.next();
        if (! itemSet.contains(key)) {
          opiMutItem = mutRep.createItem(getOrderPriceInfoDescName());
          if (isLoggingDebug())
            logDebug("create in repository[" + opiMutItem.getItemDescriptor().getItemDescriptorName() + ":" + opiMutItem.getRepositoryId() + "]");
          shipSubtotalItemMap.put(key, opiMutItem);
        }
      }

      iter = itemSet.iterator();
      while (iter.hasNext()) {
        key = (String) iter.next();
        if (! objSet.contains(key)) {
          RepositoryItem value = (RepositoryItem) shipSubtotalItemMap.get(key);
          iter.remove();
          mutRep.removeItem(value.getRepositoryId(), value.getItemDescriptor().getItemDescriptorName());
        }
      }
    }

    
    iter = shipSubtotalItemMap.keySet().iterator();
    while (iter.hasNext()) {
      key = (String) iter.next();
      opiMutItem = (MutableRepositoryItem) shipSubtotalItemMap.get(key);
      opi = (OrderPriceInfo) shipSubtotalMap.get(key);

      writeProperties(order, getSavedProperties(), opi, opiMutItem, mutRep, orderManager);

      String saveMode = getSaveMode(order);

      if (saveMode.equals(ALL) || saveMode.equals(ORDER)) {
        savePricingAdjustments(order, opi, opiMutItem, mutRep, orderManager);

        if (opiMutItem.getItemDescriptor().hasProperty(getCurrentPriceDetailsProperty()))
          saveDetailedItemPriceInfos(order, opi, opiMutItem, mutRep, orderManager);
      }
    } // while
  }

  //-----------------------------------------------
  /**
   * This method saves a Map of ShippingItemsTaxPriceInfos objects into the repository
   * See the <code>savedProperties</code> for a list
   * of properties that are copied
   *
   * @see #writeProperties
   * @see #savePricingAdjustments
   * @see #saveDetailedItemPriceInfos
   *
   * @param order The order being loaded
   * @param pPriceInfo The amount info being saved
   * @param mutItem The repository item for the pPriceInfo
   * @param mutRep The repository being updated
   * @param orderManager The OrderManager in the pipeline params
   * @param pPropertyName The name of the map to load
   **/
  protected void saveShippingItemsTaxPriceInfos(Order order, AmountInfo pPriceInfo, MutableRepositoryItem mutItem,
                               MutableRepository mutRep, OrderManager orderManager)
                               throws RepositoryException, IntrospectionException, PropertyNotFoundException,
                                      CommerceException
  {
    Map shipTaxMap = (Map) DynamicBeans.getPropertyValue(pPriceInfo, getShippingItemsTaxPriceInfosProperty());
    Map shipTaxItemMap = (Map) mutItem.getPropertyValue(getShippingItemsTaxPriceInfosProperty());
    MutableRepositoryItem tpiMutItem;
    TaxPriceInfo tpi;
    Iterator iter;
    String key;

    if (shipTaxMap == null || shipTaxMap.size() == 0) {
      shipTaxItemMap.clear();
      return;
    }
    else {
      Set objSet = shipTaxMap.keySet();
      Set itemSet = shipTaxItemMap.keySet();

      iter = objSet.iterator();
      while (iter.hasNext()) {
        key = (String) iter.next();
        if (! itemSet.contains(key)) {
          tpiMutItem = mutRep.createItem(getTaxPriceInfoDescName());
          if (isLoggingDebug())
            logDebug("create in repository[" + tpiMutItem.getItemDescriptor().getItemDescriptorName() + ":" + tpiMutItem.getRepositoryId() + "]");
          shipTaxItemMap.put(key, tpiMutItem);
        }
      }

      iter = itemSet.iterator();
      while (iter.hasNext()) {
        key = (String) iter.next();
        if (! objSet.contains(key)) {
          RepositoryItem value = (RepositoryItem) shipTaxItemMap.get(key);
          iter.remove();
          mutRep.removeItem(value.getRepositoryId(), value.getItemDescriptor().getItemDescriptorName());
        }
      }
    }

    iter = shipTaxItemMap.keySet().iterator();
    while (iter.hasNext()) {
      key = (String) iter.next();
      tpiMutItem = (MutableRepositoryItem) shipTaxItemMap.get(key);
      tpi = (TaxPriceInfo) shipTaxMap.get(key);

      writeProperties(order, getSavedProperties(), tpi, tpiMutItem, mutRep, orderManager);

      String saveMode = getSaveMode(order);
      if (saveMode.equals(ALL) || saveMode.equals(ORDER)) {
        savePricingAdjustments(order, tpi, tpiMutItem, mutRep, orderManager);

        if (tpiMutItem.getItemDescriptor().hasProperty(getCurrentPriceDetailsProperty()))
          saveDetailedItemPriceInfos(order, tpi, tpiMutItem, mutRep, orderManager);
      }

    } // while
  }

  //-----------------------------------------------
  /**
   * This method saves a List of PricingAdjustment objects in an AmountInfo.
   * See the <code>savedProperties</code> for a list
   * of properties that are copied
   *
   * @see #writeProperties
   *
   * @param order The order being saved
   * @param pPriceInfo The price info being saved
   * @param mutItem The repository item corresponding to the price info
   * @param mutRep The repository being updated
   * @param orderManager The OrderManager from the pipeline params
   */
  protected void savePricingAdjustments(Order order, AmountInfo pPriceInfo, MutableRepositoryItem mutItem,
                               MutableRepository mutRep, OrderManager orderManager)
                               throws RepositoryException, IntrospectionException, PropertyNotFoundException,
                                      CommerceException
  {
    List adjList = (List) DynamicBeans.getPropertyValue(pPriceInfo, getAdjustmentsProperty());
    List adjItemList = (List) mutItem.getPropertyValue(getAdjustmentsProperty());
    MutableRepositoryItem adjMutItem;
    PricingAdjustment adj;

    if (adjList.size() == 0) {
      adjItemList.clear();
      return;
    }
    else if (adjItemList.size() < adjList.size()) {
      for (int i = 0, count = adjList.size() - adjItemList.size(); i < count; i++) {
        adjMutItem = mutRep.createItem(getPricingAdjustmentDescName());
        if (isLoggingDebug())
          logDebug("create in repository[" + adjMutItem.getItemDescriptor().getItemDescriptorName() + ":" + adjMutItem.getRepositoryId() + "]");
        adjItemList.add(adjMutItem);
      }
    }
    else if (adjItemList.size() > adjList.size()) {
      for (int i = 0, diff = adjItemList.size() - adjList.size(); i < diff; i++)
        adjItemList.remove(adjItemList.size() - 1);
    }

    int j = 0;
    for (Iterator iter = adjItemList.iterator(); iter.hasNext(); j++) {
      adjMutItem = (MutableRepositoryItem) iter.next();
      adj = (PricingAdjustment) adjList.get(j);

      writeProperties(order, getPricingAdjustmentProperties(), adj, adjMutItem, mutRep, orderManager);
    } // for
  }

  //-----------------------------------------------
  /**
   * This method saves a List of DetailedItemPriceInfo objects in an ItemPriceInfo.
   * See the <code>detailedItemPriceInfoProperties</code> for a list
   * of properties that are copied
   *
   * @see #writeProperties
   * @see #savePricingAdjustments
   * @see #saveDetailedRange
   *
   * @param order The order being loaded
   * @param amtInfo The amount info for the commerce item
   * @param piRepItem The repository item for the amount info
   * @param orderManager The OrderManager in the pipeline params
   * @param invalidateCache If true, then each pricing adjustment repository cache
   *                        entry is invalidated
   **/
  protected void saveDetailedItemPriceInfos(Order order, AmountInfo pPriceInfo, MutableRepositoryItem mutItem,
                               MutableRepository mutRep, OrderManager orderManager)
                               throws RepositoryException, IntrospectionException, PropertyNotFoundException,
                                      CommerceException
  { 	
    List detList = (List) DynamicBeans.getPropertyValue(pPriceInfo, getCurrentPriceDetailsProperty());
    List detItemList = (List) mutItem.getPropertyValue(getCurrentPriceDetailsProperty());
    MutableRepositoryItem detMutItem;
    DetailedItemPriceInfo det;

    if (detList.size() == 0) {
      detItemList.clear();
      return;
    }
    else if (detItemList.size() < detList.size()) {
      for (int i = 0, count = detList.size() - detItemList.size(); i < count; i++) {
        detMutItem = mutRep.createItem(getDetailedItemPriceInfoDescName());
        if (isLoggingDebug())
          logDebug("create in repository[" + detMutItem.getItemDescriptor().getItemDescriptorName() + ":" + detMutItem.getRepositoryId() + "]");
        detItemList.add(detMutItem);
      }
    }
    else if (detItemList.size() > detList.size()) {
      for (int i = 0, diff = detItemList.size() - detList.size(); i < diff; i++)
        detItemList.remove(detItemList.size() - 1);
    }

    int j = 0;
    for (Iterator iter = detItemList.iterator(); iter.hasNext(); j++) {
      detMutItem = (MutableRepositoryItem) iter.next();
      det = (DetailedItemPriceInfo) detList.get(j);

      writeProperties(order, getDetailedItemPriceInfoProperties(), det, detMutItem, mutRep, orderManager);

      String saveMode = getSaveMode(order);
      if (saveMode.equals(ALL) || saveMode.equals(ORDER))
        savePricingAdjustments(order, det, detMutItem, mutRep, orderManager);

      saveDetailedRange(order, det, detMutItem, mutRep, orderManager);
    } // for
  }

  //-----------------------------------------------
  /**
   * This method saves a List of properties to a repository item.
   *
   * @param order The order being loaded
   * @param propertyList The list of properties to write
   * @param obj The object that each property is copied from
   * @param mutItem The repository item that each property is copied into
   * @param mutRep The repository that is updated
   * @param orderManager The OrderManager in the pipeline params
   **/
  protected void writeProperties(Order order, String[] propertyList, Object obj,
                            MutableRepositoryItem mutItem, MutableRepository mutRep,
                            OrderManager orderManager)
                            throws RepositoryException, IntrospectionException, CommerceException
  {
    OrderTools orderTools = orderManager.getOrderTools();
    String mappedPropName;
    Object value;

    for (int i = 0; i < propertyList.length; i++) {
      mappedPropName = getMappedPropertyName(propertyList[i]);

      if (! OrderRepositoryUtils.hasProperty(order, obj, mappedPropName))
        continue;

      try {
        value = OrderRepositoryUtils.getPropertyValue(order, obj, mappedPropName);
      }
      catch (PropertyNotFoundException e) {
        continue; // should never happen because we already checked for existence
      }
      
      if (isLoggingDebug())
        logDebug("save property[" + propertyList[i] + ":" + value + ":" +
                              obj.getClass().getName() + ":" + mutItem.getRepositoryId() + "]");
      OrderRepositoryUtils.saveRepositoryItem(mutRep, mutItem, propertyList[i], value, orderTools);
    } // for

    if (! order.isTransient() && mutItem.isTransient()) {
      if (isLoggingDebug())
        logDebug("add to repository[" + mutItem.getItemDescriptor().getItemDescriptorName() + ":" + mutItem.getRepositoryId() + "]");
      mutRep.addItem(mutItem);      
    }
    
    try {
      mutRep.updateItem(mutItem);      
    }
    catch (ConcurrentUpdateException e) {
      String[] msgArgs = { order.getId(), mutItem.getItemDescriptor().getItemDescriptorName() };
      throw new CommerceException(ResourceUtils.getMsgResource("ConcurrentUpdateAttempt",
                                      MY_RESOURCE_NAME, sResourceBundle, msgArgs), e);
    }
  }

  /**
   * Save the range property in a detailed item price info
   * See the <code>detailsRangeProperties</code> for a list
   * of properties that are copied
   *
   * @see #writeProperties
   *
   * @param pOrder The order being loaded
   * @param pDetail The detailed item price being loaded
   * @param pDetMutItem The repository item corresponding to the detail
   * @param pMutRep The repository that is updated
   * @param pOrderManager The order manager in the pipeline params
   **/
  protected void saveDetailedRange(Order pOrder, DetailedItemPriceInfo pDetail,
                                    MutableRepositoryItem pDetMutItem, MutableRepository pMutRep,
                                    OrderManager pOrderManager)
    throws RepositoryException, IntrospectionException, PropertyNotFoundException,
           CommerceException
  {
    Range range = pDetail.getRange();
    if(range == null)
      return;

    writeProperties(pOrder, getDetailsRangeProperties(), range,
                    pDetMutItem, pMutRep,
                    pOrderManager);
  }

  /**
   * Verify that each of the saveModes in orderStateSaveModes, and 
   * defaultSaveMode, are valid saveModes
   *
   */
  protected void verifySaveModes() throws CommerceException {
    List validSaveModes = new ArrayList();
    for (int i=0; i < VALID_SAVE_MODES.length; i++)
      validSaveModes.add(VALID_SAVE_MODES[i]);

    ArrayList saveModes = new ArrayList(getOrderStateSaveModes().values());
    if (saveModes != null) saveModes.add(getDefaultSaveMode());
    else {
      saveModes = new ArrayList(1);
      saveModes.add(getDefaultSaveMode());
    }

    Iterator saveModeIter = saveModes.iterator();
    while(saveModeIter.hasNext()) {
      String saveMode = (String) saveModeIter.next();
      if (!validSaveModes.contains(saveMode)) {
        String[] msgArgs = { saveMode };
        throw new CommerceException(ResourceUtils.getMsgResource("InvalidSaveMode",
                                      MY_RESOURCE_NAME, sResourceBundle, msgArgs));
      }
    }

    mVerifiedSaveModes = true;
  }
}
