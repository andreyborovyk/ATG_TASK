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
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;

import javax.jms.*;
import java.text.*;

import java.util.*;

/**
 * This processor will verify if the shipping groups passed in the order
 * can be handled by their fulfillers.
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcVerifyShippingGroupsForFulfillers.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcVerifyShippingGroupsForFulfillers implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcVerifyShippingGroupsForFulfillers.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcVerifyShippingGroupsForFulfillers() {
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
   * Verifies if the shipping groups are handled by their fulfillers
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
    Order pOrder = (Order) map.get(PipelineConstants.ORDER);
    OrderFulfiller of = (OrderFulfiller) map.get(PipelineConstants.ORDERFULFILLER);
    String fulfiller = (String) map.get(PipelineConstants.FULFILLER);
    List shippingGroupIds = (List) map.get(PipelineConstants.SHIPPINGGROUPIDS);
    List performedModifications = (List) map.get(PipelineConstants.MODIFICATIONLIST);

    if (pOrder == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
  
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    OrderStates orderStates = of.getOrderStates();

    try {
        tools.verifyShippingGroupsForFulfiller(pOrder, fulfiller, shippingGroupIds);
    }
    catch(FulfillerNotFoundException f) {
        String msg = MessageFormat.format(Constants.FULFILLER_NOT_FOUND, fulfiller);
        if(of.isLoggingError())
          of.logError(msg, f);
        tools.setOrderState(pOrder,
                            orderStates.getStateValue(OrderStates.PENDING_MERCHANT_ACTION),
                            msg,
                            performedModifications);
        throw f;
      }
      catch(InvalidShippingGroupTypeException i) {
        String msg = MessageFormat.format(Constants.INVALID_SHIPPING_GROUP_TYPE, fulfiller);
        if(of.isLoggingError())
           of.logError(msg, i);
        tools.setOrderState(pOrder,
                            orderStates.getStateValue(OrderStates.PENDING_MERCHANT_ACTION),
                            msg,
                            performedModifications);
        throw i;
      }

    return SUCCESS;
  }
    
}
