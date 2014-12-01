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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;

import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.OrderTools;
import atg.commerce.order.PipelineConstants;
import atg.commerce.promotion.PromotionTools;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

/**
 * This processor moves any used promotions in the user's profile from the activePromotions
 * list to the usedPromotions list. If the promotion is marked as "use once" then it is moved.
 * If it is allowed to be used as many times as the user wishes then it is not moved.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcMoveUsedPromotions.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcMoveUsedPromotions 
extends ApplicationLoggingImpl
implements PipelineProcessor 
{
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcMoveUsedPromotions.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcMoveUsedPromotions() {
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
  // property: promotionTools
  //-------------------------------------
  private PromotionTools mPromotionTools = null;

  /**
   * Returns the promotionTools
   */
  public PromotionTools getPromotionTools() {
    return mPromotionTools;
  }

  /**
   * Sets the promotionTools
   */
  public void setPromotionTools(PromotionTools pPromotionTools) {
    mPromotionTools = pPromotionTools;
  }
 
  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcMoveUsedPromotions";

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
  // property: pricingModelComponentName
  //-------------------------------------

  //-----------------------------------------------
  /**
   * This method moves the used promotions in an order to the usedPromotions list in their profile.
   *
   * This method requires that an Order and optionally a Locale object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order and optionally a Locale object
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
    OrderManager orderManager = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);
    MutableRepository mutRep;
    
    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (orderManager == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderManagerParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));

    Collection orderPromos = null;
    Collection taxPromos = null;
    Collection itemPromos = null;
    Collection shippingPromos = null;
    
    // SmcG: COMMERCE-144600 now using sets instead of lists since we only want to consume a promotion once if it has
    // been applied for an order.  Note if a promotion has been granted more than once to a customer then it would appear as
    // two separate promotionStatus items and so would still be handled separately (and correctly) by sets.
    orderPromos = new HashSet(10);
    taxPromos = new HashSet(10);
    itemPromos = new HashSet(10);
    shippingPromos = new HashSet(10);
    
    PromotionTools promotionTools = getPromotionTools();
    OrderTools orderTools = orderManager.getOrderTools();

    try {
      mutRep = (MutableRepository) orderTools.getProfileRepository();
    }
    catch (ClassCastException e) {
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidRepositoryParameter",
                                      MY_RESOURCE_NAME, sResourceBundle), e);
    }
    
    // consume the promotions in the order
    MutableRepositoryItem profile = null;
    if (order.getProfileId() != null)
      profile = mutRep.getItemForUpdate(order.getProfileId(), orderTools.getDefaultProfileType());
    // if profile is null, then all calls below become no-ops
    
    getPromotionsToConsume(map, order, orderPromos, taxPromos, itemPromos, shippingPromos);
    
    if (isLoggingDebug())
      logDebug("promotions used in order: orderPromos=" + orderPromos + "; itemPromos=" + itemPromos + "; shippingPromos=" + shippingPromos + "; taxPromos=" + taxPromos);

    if (! orderPromos.isEmpty()) {
      for (Object promoObj : orderPromos) {
        RepositoryItem promo = (RepositoryItem) promoObj;
        if (isLoggingDebug())
          logDebug("Consume order promotion " + promo + " from profile " + profile);
        promotionTools.consumePromotion(profile, promo);
      }
    }

    if (! itemPromos.isEmpty()) {
      for (Object promoObj : itemPromos) {
        RepositoryItem promo = (RepositoryItem) promoObj;
        if (isLoggingDebug())
          logDebug("Consume item promotion " + promo + " from profile " + profile);
        promotionTools.consumePromotion(profile, promo);
      }
    }

    if (! shippingPromos.isEmpty()) {
      for (Object promoObj : shippingPromos) {
        RepositoryItem promo = (RepositoryItem) promoObj;
        if (isLoggingDebug())
          logDebug("Consume shipping promotion " + promo + " from profile " + profile);
        promotionTools.consumePromotion(profile, promo);
      }
    }

    if (! taxPromos.isEmpty()) {
      for (Object promoObj : taxPromos) {
        RepositoryItem promo = (RepositoryItem) promoObj;
        if (isLoggingDebug())
          logDebug("Consume tax promotion " + promo + " from profile " + profile);
        promotionTools.consumePromotion(profile, promo);
      }
    }
    
    return SUCCESS;
  }
  
  
  /**
   * This API is called to populate the given collections with the promotions that should be consumed. 
   * <p>
   * Implementations should add the promotions to the collections provided.
   * 
   * @param pParameters the pipeline's incoming parameter map
   * @param pOrder the order
   * @param pOrderPromotions the collection to which order level promotions should be added
   * @param pTaxPromotions the collection to which tax level promotions should be added
   * @param pItemPromotions the collection to which item level promotions should be added
   * @param pShippingPromotions the collection to which shipping level promotions should be added
   */
  protected void getPromotionsToConsume(Map pParameters, Order pOrder, Collection pOrderPromotions,
         Collection pTaxPromotions, Collection pItemPromotions,
         Collection pShippingPromotions)
  {
    getPromotionTools().getOrderPromotions(pOrder, pOrderPromotions, pTaxPromotions, pItemPromotions, pShippingPromotions);
    
  }
}
