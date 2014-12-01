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
import atg.commerce.fulfillment.*;
import atg.repository.*;
import atg.commerce.states.*;

import java.util.*;

/**
 * This processor is executed as part of the HardgoodShipper to ship shipping groups
 * that are in a PENDING_SHIPMENT state. It will return a HashMap object with orders
 * whose shipping groups are ready to ship.
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcRetrieveOrderPendingShipMap.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcRetrieveOrderPendingShipMap implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcRetrieveOrderPendingShipMap.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcRetrieveOrderPendingShipMap() {
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
   * Returns a HashMap object with orders whose shipping groups are ready to ship
   *
   * This method requires that an OrderFulfiller object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an OrderFulfiller object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    HardgoodFulfiller of = (HardgoodFulfiller) map.get(PipelineConstants.ORDERFULFILLER);
    HardgoodShipper hs = (HardgoodShipper) map.get(PipelineConstants.HARDGOODSHIPPER);

    HashMap ordersWithShips = retrieveOrderPendingShipMap(of, hs);

    if (ordersWithShips == null)
        return STOP_CHAIN_EXECUTION;

    map.put(PipelineConstants.ORDERPENDINGSHIPMAP, ordersWithShips);
    
    return SUCCESS;
  }


  //-------------------------------------
  /**
   * Returns a HashMap whose ids are the order ids of orders that contain shipping groups and
   * the values are the shipping group ids that are ready for shipment.  The default
   * implementation uses the order repository to return all shipping groups whose state is
   * PENDING_SHIPMENT.
   *
   * @beaninfo
   *          description: Finds all orders with groups that are pending shipment.
   * @return a HashMap where the keys are the order ids, and the values are a Set of the
   * shipping group ids that belong.
   **/
  protected HashMap retrieveOrderPendingShipMap(HardgoodFulfiller of, HardgoodShipper hs)
  {

    ShippingGroupStates sgs = of.getShippingGroupStates();
    OrderStates os = of.getOrderStates();

    HashMap returnMap = new HashMap(7);
    Repository orderRepository = null;
    try {
        orderRepository = of.getOrderManager().getOrderTools().getOrderRepository();
    }
    catch (NullPointerException npe) {
        return null;
    }
    
    if (orderRepository == null)
      return null;
    
    try {
      RepositoryView shippingView = orderRepository.getView(hs.getShippingGroupViewName());
      QueryBuilder qb = shippingView.getQueryBuilder();

      QueryExpression state =
	qb.createConstantQueryExpression(sgs.getStateString(sgs.getStateValue(ShippingGroupStates.PENDING_SHIPMENT)));

      QueryExpression stateProperty = qb.createPropertyQueryExpression(hs.getShippingGroupStatePropertyName());

      Query qStateEquality = qb.createComparisonQuery( stateProperty, state, QueryBuilder.EQUALS);

      QueryExpression orderState =
        qb.createConstantQueryExpression(os.getStateString(os.getStateValue(OrderStates.PROCESSING)));

      QueryExpression orderStateProperty = 
          qb.createPropertyQueryExpression(qb.createPropertyQueryExpression(hs.getShippingGroupOrderPropertyName()), 
                                           hs.getOrderStatePropertyName());
      
      Query qOrderStateEquality = qb.createComparisonQuery( orderStateProperty, orderState, QueryBuilder.EQUALS);

      QueryExpression date = qb.createConstantQueryExpression(new Date());

      QueryExpression shipOnDateProperty = 
          qb.createPropertyQueryExpression(hs.getShippingGroupShipOnDatePropertyName());

      Query qAfterDate = qb.createComparisonQuery( shipOnDateProperty, date, QueryBuilder.LESS_THAN_OR_EQUALS);

      Query qNullDate = qb.createIsNullQuery(shipOnDateProperty);

      Query[] oredQueries = { qAfterDate, qNullDate };

      Query beforeOrNullDate = qb.createOrQuery(oredQueries);

      Query[] andedQueries = { qStateEquality, qOrderStateEquality, beforeOrNullDate};

      Query dateAndState = qb.createAndQuery(andedQueries);

      // sort by date.
      String orderbyProperty = hs.getShippingGroupSubmittedDatePropertyName();
      SortDirective sd = new SortDirective(orderbyProperty, SortDirective.DIR_ASCENDING);
      SortDirectives sds = new SortDirectives();
      sds.addDirective(sd);
      
      RepositoryItem[] items = shippingView.executeQuery(dateAndState, sds);

      if (items == null || items.length == 0)
          return null;

      RepositoryItem currentShipGroup = null;
      RepositoryItem currentOrder = null;

      String orderId, shipGroupId = null;
      Set currentSet = null;
      
      for (int i=0; i < items.length; i++) {
	currentShipGroup = items[i];
	currentOrder = (RepositoryItem) currentShipGroup.getPropertyValue("order");

	orderId = currentOrder.getRepositoryId();
	currentSet = (Set) returnMap.get(orderId);
	if (currentSet == null) {
            currentSet = new HashSet();
	}
	currentSet.add(currentShipGroup.getRepositoryId());
	returnMap.put(orderId, currentSet);
      }
      
    }
    catch (RepositoryException re) {
        of.logError(re);
        return null;
    }
    
    return returnMap;

  }

}
