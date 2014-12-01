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
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;

import java.util.*;
import javax.jms.*;
import java.text.*;

/**
 * This processor executes removing of an order by setting the order's and its components
 * states to PENDING_REMOVE and sending the modifyOrder message to the fulfiller
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcSendModifyOrderForRemoval.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcSendModifyOrderForRemoval implements PipelineProcessor {
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcSendModifyOrderForRemoval.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private static final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcSendModifyOrderForRemoval() {
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
   * This processor executes removing of an order by setting the order's and its components
   * states to PENDING_REMOVE and sending the modifyOrder message to the fulfiller
   *
   * This method requires that an Order and OrderFulfiller object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order and OrderFulfiller object
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
    List performedModifications = (List) map.get(PipelineConstants.MODIFICATIONLIST);

    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    
    if (pOrder == null) {
        if (of.isLoggingError())
            of.logError(Constants.ORDER_IS_NULL);
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    }

    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    OrderStates os = of.getOrderStates();
    ShippingGroupStates sgs = of.getShippingGroupStates();
    CommerceItemStates cis = of.getCommerceItemStates();
    HashMap fulfillerToShippingGroups = tools.getFulfillersForShippingGroups(pOrder.getShippingGroups());
    Iterator fulfillerIter = fulfillerToShippingGroups.keySet().iterator();

    // change the state of the order
    String msg = Constants.REMOVE_ORDER_REQUESTED;
    tools.setOrderState(pOrder, os.getStateValue(OrderStates.PENDING_REMOVE),
                        MessageFormat.format(msg, pOrder.getId()),
                        performedModifications);

    // then process each CommerceItem
    List commerceItems = pOrder.getCommerceItems();
    Iterator cItemIter = commerceItems.iterator();
    while(cItemIter.hasNext()) {
      CommerceItem commerceItem = (CommerceItem) cItemIter.next();
      if(commerceItem.getState() != cis.getStateValue(CommerceItemStates.REMOVED)) {
        msg = Constants.REMOVE_ORDER_REQUESTED;
        tools.setItemState(commerceItem, cis.getStateValue(CommerceItemStates.PENDING_REMOVE),
                           MessageFormat.format(msg, pOrder.getId()),
                           performedModifications);
      }
    }
    
    // process each fulfiller
    while(fulfillerIter.hasNext()) {
      String fulfiller = (String) fulfillerIter.next();
      String fulfillerPort = tools.getFulfillerPort(fulfiller);
      List shippingGroups = (List) fulfillerToShippingGroups.get(fulfiller);
      List newModifications = new ArrayList();

      // for each shipping group, create a modification that sets its state to REMOVED
      Iterator shippingGroupIter = shippingGroups.iterator();
      while(shippingGroupIter.hasNext()) {
        ShippingGroup sg = (ShippingGroup) shippingGroupIter.next();

        // we already checked if anything shipped
        // nothing has been sent to this fulfiller yet
        if(sg.getState() == sgs.getStateValue(ShippingGroupStates.INITIAL)) {
          msg = Constants.REMOVE_ORDER_REQUESTED;
          tools.setShippingGroupState(sg, sgs.getStateValue(ShippingGroupStates.REMOVED),
                                      MessageFormat.format(msg, pOrder.getId()),
                                      performedModifications);
          break;
        } else {
          GenericRemove removeMod = tools.createGenericRemoveModification(Modification.TARGET_SHIPPING_GROUP,
                                                                          sg.getId(),
                                                                          Modification.TARGET_ORDER,
                                                                          pOrder.getId());
          newModifications.add(removeMod);
        }
      }

      try {
        tools.sendModifyOrder(pOrder.getId(), newModifications, of, fulfillerPort, null);
      } catch(JMSException j) {
          if(of.isLoggingError())
              of.logError(j);
          throw j;
      }
    }

    return SUCCESS;
  }
}
