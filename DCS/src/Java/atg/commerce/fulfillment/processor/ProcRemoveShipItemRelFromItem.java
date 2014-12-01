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

package atg.commerce.fulfillment.processor;

import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;
import atg.commerce.*;
import atg.commerce.fulfillment.*;
import atg.commerce.messaging.*;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Relationship;
import atg.commerce.order.RelationshipTypes;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;

import java.util.*;

/**
 * This processor will remove the relationship's quantity from the CommerceItem's quantity
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcRemoveShipItemRelFromItem.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcRemoveShipItemRelFromItem implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcRemoveShipItemRelFromItem.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
    
  //-----------------------------------------------
  public ProcRemoveShipItemRelFromItem() {
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

  //-----------------------------------------------
  /**
   * Removes the relationship's quantity from its commerce item's quantity 
   *
   * This method requires that a JMS message and OrderFulfiller object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain a JMS message and OrderFulfiller object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    OrderFulfiller of = (OrderFulfiller) map.get(PipelineConstants.ORDERFULFILLER);
    Order pOrder = (Order) map.get(PipelineConstants.ORDER);
    Modification pModification = (Modification) map.get(PipelineConstants.MODIFICATION);

    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));

    if (pOrder == null) {
        if (of.isLoggingError())
            of.logError(Constants.ORDER_IS_NULL);
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    }
    
    ShippingGroupCommerceItemRelationship sgcir = null;
    long quantity = 0;

    // any exceptions from this relationship are thrown in the previous processor
    GenericUpdate genupMod = (GenericUpdate) pModification;
    Relationship r = pOrder.getRelationship(genupMod.getTargetId());
    sgcir = (ShippingGroupCommerceItemRelationship) r;
    CommerceItem ci = sgcir.getCommerceItem();

    // find out how many items to allocate from inventory
    switch(sgcir.getRelationshipType())
    {
    case RelationshipTypes.SHIPPINGQUANTITY:
      {
        quantity = sgcir.getQuantity();
        break;
      }
    case RelationshipTypes.SHIPPINGQUANTITYREMAINING:
      {
        quantity = of.getOrderManager().getRemainingQuantityForShippingGroup(ci);
        break;
      }
    } //switch

    long itemQuantity = ci.getQuantity();
    long newQuantity = itemQuantity - quantity;
    if(newQuantity < 0) {
        if(of.isLoggingWarning()) {
         of.logWarning("Attempt to decrement item quantity to less than zero");
         of.logWarning("Setting to zero for item " + ci.getId());
        }
      newQuantity = 0;
    }

    if(of.isLoggingDebug())
       of.logDebug("Updating quantity of " + ci.getId() + " to " + newQuantity);
    ci.setQuantity(newQuantity);
  
    return SUCCESS;
  }

}

