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
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;
import atg.repository.*;

import java.util.*;
import javax.jms.*;
import java.text.*;

/**
 * This processor is called on a reception of a UpdateInventory JMS Message. 
 * Returns a HashMap where the keys are the order ids and the values are a Set of shipping
 * group ids of the shipping groups who have items that could not be previously allocated. 
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcRetrieveOrderWaitingShipMap.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcRetrieveOrderWaitingShipMap implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcRetrieveOrderWaitingShipMap.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcRetrieveOrderWaitingShipMap() {
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
   * Returns a HashMap where the keys are the order ids and the values are a Set of shipping
   * group ids of the shipping groups who have items that could not be previously allocated
   * from the inventory.
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
    ObjectMessage cMessage = (ObjectMessage) map.get(PipelineConstants.MESSAGE);
    HardgoodFulfiller of = (HardgoodFulfiller) map.get(PipelineConstants.ORDERFULFILLER);

    if (cMessage == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidMessageParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    
    UpdateInventory pMessage = (UpdateInventory) cMessage.getObject();
    String[] ids = pMessage.getItemIds();
    
    HashMap orderMap = retrieveWaitingShipMap(of, ids);

    map.put(PipelineConstants.ORDERWAITINGSHIPMAP, orderMap);
    
    return SUCCESS;
  }

  //-------------------------------------
  /**
   * Returns a HashMap where the keys are the order ids and the values are a Set of shipping
   * group ids of the shipping groups who have items that were on preorder and are contained
   * in the pCatalogRefIds that are passed in.
   *
   * @beaninfo
   *          description: Find all orders with preordered items.
   * @param pCatalogRefIds - the array of sku ids that have been moved off preorder
   * @return a hashmap where the keys are the order ids and the values are a Set of shipping
   * group ids.
   **/
  protected HashMap retrieveWaitingShipMap(HardgoodFulfiller of, String[] pCatalogRefIds) {
    ShipItemRelationshipStates sirs = of.getShipItemRelationshipStates();
    String[] states = new String[4];
    states[0] = sirs.getStateString(sirs.getStateValue(ShipItemRelationshipStates.BACK_ORDERED));
    states[1] = sirs.getStateString(sirs.getStateValue(ShipItemRelationshipStates.PRE_ORDERED));
    states[2] = sirs.getStateString(sirs.getStateValue(ShipItemRelationshipStates.OUT_OF_STOCK));
    states[3] = sirs.getStateString(sirs.getStateValue(ShipItemRelationshipStates.DISCONTINUED));
    return retrieveShipMap(of, pCatalogRefIds, states);
  }


  //-------------------------------------
  /**
   * This method will return a HashMap where the Order ids are the
   * keys and the values are a Set of shipping group ids whose
   * shipItemRel state is equal to one of the pState passed in and
   * contain the items listed in pCatalogRefIds.
   *
   * @beaninfo
   *          description: retrieve all orders with items in the given state.
   * @param pCatalogRefIds - the ids of the items we are interested in finding in the shipping
   * groups.
   * @param pState - the array of states of the item relationship that we want to match.
   * @return a hashmap where the keys are the order ids and the values are a Set of shipping group ids.
   **/
  protected HashMap retrieveShipMap(HardgoodFulfiller of, String[] pCatalogRefIds,
                                    String[] pStates)
    {
    
    Repository orderRepository = of.getOrderManager().getOrderTools().getOrderRepository();

    if (pCatalogRefIds == null || pCatalogRefIds.length == 0)
        return null;
    
    if(pStates == null || pStates.length == 0)
        return null;

    try
    {
      RepositoryView shipRelationshipView = orderRepository.getView(of.getShipItemRelViewName());
      QueryBuilder qb = shipRelationshipView.getQueryBuilder();

      // create the constant query expression for backordered items
      QueryExpression stateProperty = qb.createPropertyQueryExpression(of.getShipItemRelStatePropertyName());
      Query[] qOredStateEquality = new Query[pStates.length];
      for(int i=0;i<pStates.length;i++) {
          QueryExpression state = qb.createConstantQueryExpression(pStates[i]);        
          qOredStateEquality[i] = qb.createComparisonQuery( stateProperty, state, QueryBuilder.EQUALS);        
      }

      Query qStateEquality;
      if(pStates.length == 1)
        qStateEquality = qOredStateEquality[0];
      else
        qStateEquality = qb.createOrQuery(qOredStateEquality);

      Query qIncludesAny = qb.createIncludesQuery(qb.createConstantQueryExpression(pCatalogRefIds),
			   qb.createPropertyQueryExpression(qb.createPropertyQueryExpression(of.getShipItemRelCommerceItemPropertyName()), of.getCommerceItemCatalogRefIdPropertyName()));

      // order by commerceItem.shippingGroup.submittedDate
      String orderbyProperty = of.getShipItemRelShippingGroupPropertyName() + "."
        + of.getShippingGroupSubmittedDatePropertyName();
      SortDirective sd = new SortDirective(orderbyProperty, SortDirective.DIR_ASCENDING);
      SortDirectives sds = new SortDirectives();
      sds.addDirective(sd);
      
      Query[] andedArray = new Query[2];
      andedArray[0] = qStateEquality;
      andedArray[1] = qIncludesAny;
      Query combinedQuery = qb.createAndQuery(andedArray);

      RepositoryItem[] items = shipRelationshipView.executeQuery(combinedQuery, sds);
            
      if (items != null && items.length > 0)
	return makeOrderShipGroupHashMap(of, items);
    }
    catch (RepositoryException re) {
      if(of.isLoggingError())
        of.logError(re);
    }
    return null;
  }

  //-------------------------------------
  /**
   * Takes an array of repository items that correspond to shipItemRel repository items.  Will
   * return a HashMap where the keys are the order ids and the values are sets of the shipping
   * groups.
   **/
  protected HashMap makeOrderShipGroupHashMap(HardgoodFulfiller of, RepositoryItem[] pItems)
  {
    HashMap returnMap = new HashMap(7);
    Set currentSet = null;
    RepositoryItem currentRelationship = null;
    RepositoryItem currentShipGroup = null;
    RepositoryItem currentOrder = null;

    String orderId = null;
    
    for (int i=0; i < pItems.length; i++) {
      currentRelationship = pItems[i];      
      currentShipGroup = 
          (RepositoryItem) currentRelationship.getPropertyValue(of.getShipItemRelShippingGroupPropertyName()); 
      currentOrder = 
          (RepositoryItem) currentRelationship.getPropertyValue(of.getShipItemRelOrderPropertyName());
      orderId = currentOrder.getRepositoryId();
      currentSet = (Set) returnMap.get(orderId);
      if (currentSet == null) {
	currentSet = new HashSet();
      }
      currentSet.add(currentShipGroup.getRepositoryId());
      returnMap.put(orderId, currentSet);
    }
    return returnMap;
  }
  
}
