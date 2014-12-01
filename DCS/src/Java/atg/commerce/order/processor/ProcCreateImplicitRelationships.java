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

import atg.service.pipeline.*;
import atg.commerce.order.*;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;

import java.util.*;

/**
 * This processor creates relationships in an Order which has one ShippingGroup and/or
 * one PaymentGroup. If the Order contains only one ShippingGroup and it contains
 * no Relationships, then all the CommerceItems are added to the ShippingGroup
 * by adding a ShippingGroupCommerceItemRelationship between each CommerceItem
 * and the ShippingGroup. If the Order contains only one PaymentGroup and
 * it contains no Relationships, then the entire cost of the Order is added to the
 * PaymentGroup by adding a PaymentGroupOrderRelationship between the Order and
 * PaymentGroup.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcCreateImplicitRelationships.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.commerce.order.Relationship
 * @see atg.commerce.order.ShippingGroupRelationship
 * @see atg.commerce.order.CommerceItemRelationship
 * @see atg.commerce.order.PaymentGroupRelationship
 * @see atg.commerce.order.OrderRelationship
 * @see atg.commerce.order.ShippingGroupCommerceItemRelationship
 * @see atg.commerce.order.PaymentGroupCommerceItemRelationship
 * @see atg.commerce.order.PaymentGroupShippingGroupRelationship
 * @see atg.commerce.order.PaymentGroupOrderRelationship
 */
public class ProcCreateImplicitRelationships extends ApplicationLoggingImpl implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcCreateImplicitRelationships.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcCreateImplicitRelationships() {
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
  String mLoggingIdentifier = "ProcCreateImplicitRelationships";

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
   * This method creates relationships in an Order which has one ShippingGroup and/or
   * one PaymentGroup. If the Order contains only one ShippingGroup and it contains
   * no Relationships, then all the CommerceItems are added to the ShippingGroup by
   * adding a ShippingGroupCommerceItemRelationship between each CommerceItem
   * and the ShippingGroup. If the Order contains only one PaymentGroup and
   * it contains no Relationships, then the entire cost of the Order is added to the
   * PaymentGroup by adding a PaymentGroupOrderRelationship between the Order and
   * PaymentGroup.
   *
   * To add the ShippingGroupCommerceItemRelationships, the OrderManager method
   * addRemainingItemQuantityToShippingGroup(Order, String, String) is called once
   * for each CommerceItem.
   *
   * To add the PaymentGroupOrderRelationship, the OrderManager method
   * addRemainingOrderAmountToPaymentGroup(Order, String) is called once
   * for the Order.
   *
   * This method requires that an Order and OrderManager object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order and OrderManager object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   * @see atg.commerce.order.OrderManager#addRemainingItemQuantityToShippingGroup(Order, String, String)
   * @see atg.commerce.order.OrderManager#addRemainingOrderAmountToPaymentGroup(Order, String)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    OrderManager orderManager = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);
    
    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (orderManager == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderManagerParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));

    // If there is only one shippingGroup that has no CommerceItemRelationships,
    // then all the items implicitly belong to that shippingGroup. Create the
    // relationships for them.
    if (order.getShippingGroupCount() == 1) {
      ShippingGroup sg = (ShippingGroup) order.getShippingGroups().get(0);
      if (sg.getCommerceItemRelationshipCount() == 0) {
        Iterator iter = order.getCommerceItems().iterator();
        for (CommerceItem item = null; iter.hasNext(); ) {
          item = (CommerceItem) iter.next();
          orderManager.addRemainingItemQuantityToShippingGroup(order, item.getId(), sg.getId());
        } // for
      } // if
    } // if
    
    // If there is only one paymentGroup that has no Relationships,
    // then all the items, tax, and shipping costs implicitly belong to that paymentGroup.
    // Create the relationships for them.
    if (order.getPaymentGroupCount() == 1) {
      PaymentGroup pg = (PaymentGroup) order.getPaymentGroups().get(0);
      if (pg.getCommerceItemRelationshipCount() == 0 &&
          pg.getShippingGroupRelationshipCount() == 0 &&
          pg.getOrderRelationshipCount() == 0)
      {
        orderManager.addRemainingOrderAmountToPaymentGroup(order, pg.getId());
      }
    }
    
    return SUCCESS;
  }
}
