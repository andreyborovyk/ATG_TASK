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
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;

import javax.jms.*;
import java.text.*;

import java.util.*;

/**
 * This processor finds all shipping groups in an order that need to be split
 * across multiple fulfillers (shipping groups that are to be fulfilled by more than
 * one fulfiller)
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcRetrieveShippingGroupsToBeSplit.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcRetrieveShippingGroupsToBeSplit implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcRetrieveShippingGroupsToBeSplit.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcRetrieveShippingGroupsToBeSplit() {
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
   * This processor finds all shipping groups in an order that need to be split
   * across multiple fulfillers (shipping groups that are to be fulfilled by more than
   * one fulfiller)
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
   
    if (pOrder == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    
    // Get the list of shipping groups to be split.
    List shippingGroupsToBeSplit = retrieveShippingGroupsToBeSplit(of, pOrder);
    map.put(PipelineConstants.SHIPPINGGROUPIDS, shippingGroupsToBeSplit);

    return SUCCESS;

  }
    

  /**
   * This method will take an order and return a List of the shipping groups that need to be
   * split up into multiple shipping groups.  The default implementation will flag shipping
   * groups to be split if a shipping groups contains items that are fulfilled by multiple
   * fulfillers.  <BR>
   *
   * The default implementation uses OrderFulfillmentTools.isShippingGroupSingleFulfiller
   * to determine if all the items are fulfilled by one fulfiller.
   *
   *
   * @beaninfo
   *          description: Finds the shipping groups in a given order that need
   *                       to be split because the items within the group are
   *                       fulfilled by more than one fulfiller.
   * @param pOrder the order that contains the shipping groups
   * @return a List of the shipping groups that should be split.
   * @see OrderFulfillmentTools#isShippingGroupSingleFulfiller
   **/
  protected List retrieveShippingGroupsToBeSplit(OrderFulfiller of, Order pOrder) 
      throws CommerceException
  {
    List returnList = null;

    Iterator shippingIterator = pOrder.getShippingGroups().iterator();
    ShippingGroup shipGroupToBeTested = null;

    while (shippingIterator.hasNext()) {
	shipGroupToBeTested = (ShippingGroup) shippingIterator.next();
	if (!of.getOrderFulfillmentTools().isShippingGroupSingleFulfiller(shipGroupToBeTested)) {
            if (returnList == null) {
                returnList = new ArrayList(3);
            }
            returnList.add(shipGroupToBeTested);
	}
    }
    return returnList;
  }
  
}
