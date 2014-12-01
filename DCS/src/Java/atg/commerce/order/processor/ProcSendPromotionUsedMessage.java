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

import atg.commerce.gifts.GiftPurchased;
import atg.commerce.order.*;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.promotion.*;
import atg.repository.RepositoryItem;
import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;

import java.util.*;
import java.io.Serializable;

/**
 * This processor sends an event to the scenario engine when an promotion is used in an order.
 * A promotion is "used" when an order is processed. The event object sent is PromotionUsed.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSendPromotionUsedMessage.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.commerce.promotion.PromotionUsed
 * @see atg.commerce.order.processor.EventSender
 */
public class ProcSendPromotionUsedMessage extends EventSender
{
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSendPromotionUsedMessage.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  //-----------------------------------------------
  public ProcSendPromotionUsedMessage() {
  }

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcSendPromotionUsedMessage";

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
   * This method will loop through all the promotions calling @see #createEventToSend
   * which will return the event object to be sent.  @see #sendObjectMessage will be
   * called with the event to be sent, along with the @see #getEventType and the
   * @see #getPortName.
   *
   * This method requires that an Order and Profile object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order and Profile object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    RepositoryItem profile = (RepositoryItem) map.get(PipelineConstants.PROFILE);
    
    if (profile == null)
      return SUCCESS;
    
    Order order = (Order) map.get(PipelineConstants.ORDER);
    
    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));

    List orderPromos = new ArrayList();
    List taxPromos = new ArrayList();
    List itemPromos = new ArrayList();
    List shippingPromos = new ArrayList();
    PricingAdjustment adjustment;
    Serializable eventToSend;

    // get all the pricing adjustments in the order that relate to a promotion and then add them all to the same list
    getPromotionTools().getOrderPromotions(order, orderPromos,
                    taxPromos, itemPromos, shippingPromos, true);
    orderPromos.addAll(taxPromos);
    orderPromos.addAll(itemPromos);
    orderPromos.addAll(shippingPromos);

    // iterate through the promos sending a message for each one
    Iterator iter = orderPromos.iterator();
    while (iter.hasNext()) {
      adjustment = (PricingAdjustment) iter.next();
      eventToSend = createEventToSend(order, adjustment, profile, pParam);
      sendObjectMessage(eventToSend, getEventType(), getPortName());
    }
    
    return SUCCESS;
  }

  //-----------------------------------------------
  /**
   * This method creates and populates a PromotionUsed object. The PromotionUsed
   * object is the message which will be sent to the scenario engine.
   *
   * @param order the order to set in the message
   * @param promo the promotion to set in the message
   * @param profile the profile to set in the message
   * @return the Serializable message object
   */
  public Serializable createEventToSend(Order order, RepositoryItem promo,
              RepositoryItem profile)
  {
    PromotionUsed message = new PromotionUsed();
    message.setOrder(order);
    message.setPromotion(promo);
    message.setProfile(profile);
    message.setSource(getMessageSourceName());
    message.setId(getNextMessageId());
    message.setOriginalSource(getMessageSourceName());
    message.setOriginalId(message.getId());
    
    return message;
  }
  
//-----------------------------------------------
  /**
   * This method creates and populates a PromotionUsed object. The PromotionUsed
   * object is the message which will be sent to the scenario engine.
   *
   * @param pOrder the order to set in the message
   * @param pAdjustment the pricing adjustment to set promotion and discount in the message
   * @param pProfile the profile to set in the message
   * @return the Serializable message object
   */
  public Serializable createEventToSend(Order pOrder, PricingAdjustment pAdjustment,
              RepositoryItem pProfile)
  {
    return createEventToSend(pOrder, pAdjustment, pProfile, null);
  }
  
//-----------------------------------------------
  /**
   * This method creates and populates a PromotionUsed object. The PromotionUsed
   * object is the message which will be sent to the scenario engine.
   *
   * @param pOrder the order to set in the message
   * @param pAdjustment the pricing adjustment to set promotion and discount in the message
   * @param pProfile the profile to set in the message
   * @param pParam pipeline parameters map to extract site id property
   * @return the Serializable message object
   */
  public Serializable createEventToSend(Order pOrder, PricingAdjustment pAdjustment,
                                        RepositoryItem pProfile, Object pParam)
  {
    RepositoryItem promo = pAdjustment.getPricingModel();
    
    PromotionUsed message = new PromotionUsed();
    message.setOrder(pOrder);
    message.setPromotion(promo);
    message.setProfile(pProfile);
    // the adjustment is negative.  We want the discount to be
    // positive.
    message.setDiscount(-1 * pAdjustment.getTotalAdjustment());
    message.setSource(getMessageSourceName());
    message.setId(getNextMessageId());
    message.setOriginalSource(getMessageSourceName());
    message.setOriginalId(message.getId());
    if (pParam != null){
      message.setSiteId(getSiteId(pParam));
    }    
    return message;
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

  //---------------------------------------------------------------------------
  // property: MessageSourceName
  //---------------------------------------------------------------------------
  private String mMessageSourceName = null;

  public void setMessageSourceName(String pMessageSourceName) {
    mMessageSourceName = pMessageSourceName;
  }

  /**
   * This defines the string that the source property of messages will
   * be set to.
   *
   * @beaninfo
   *          description: This is the name of this message source.  All outgoing messages
   *                       will use this as the source.
   **/
  public String getMessageSourceName() {
    return mMessageSourceName;
  }
}
