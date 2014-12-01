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
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;
import atg.commerce.CommerceException;
import atg.commerce.inventory.InventoryException;
import atg.repository.RepositoryItem;

import java.util.*;
import java.text.*;

/**
 * This processor allocates all hardgood itemRelationships in the shipping group
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcAllocateItemRelationship.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcAllocateItemRelationship implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcAllocateItemRelationship.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcAllocateItemRelationship() {
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

  /**
   * The chain to run from within this processor
   **/
  private String mChainToRun;
  
  public String getChainToRun() {
      return mChainToRun;
  }

  public void setChainToRun(String pChainToRun) {
      mChainToRun = pChainToRun;
  }


  //-----------------------------------------------
  /**
   * Allocates all hardgood items Relationships in the shipping group
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
    HardgoodFulfiller of = (HardgoodFulfiller) map.get(PipelineConstants.ORDERFULFILLER);
    ShippingGroup pShippingGroup = (ShippingGroup) map.get(PipelineConstants.SHIPPINGGROUP);
    List performedModifications = (List) map.get(PipelineConstants.MODIFICATIONLIST);

    if (pOrder == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));

    ShippingGroupStates sgs = of.getShippingGroupStates();
    ShipItemRelationshipStates sirs = of.getShipItemRelationshipStates();
    ShippingGroupCommerceItemRelationship sgcir = null;
    
    try {
      List skuIds = of.getOrderManager().getUniqueCatalogRefIds(pOrder);
      of.getInventoryManager().acquireInventoryLocks(skuIds);
    }
    catch(InventoryException ie) {
      if(of.isLoggingError())
          of.logError(ie);
      of.getOrderFulfillmentTools().setShippingGroupState(pShippingGroup, 
                                                          sgs.getStateValue(ShippingGroupStates.PENDING_MERCHANT_ACTION),
                                                          ie.getMessage(),
                                                          performedModifications);
      throw ie;
    }
    
    // Get the list of item relationships from the shipping group
    List shippingGroupItemRelationships = pShippingGroup.getCommerceItemRelationships();
    Iterator shippingGroupItemRelIterator = shippingGroupItemRelationships.iterator();
    
    int oldState;
    boolean shippable = true, allocated = false;

    while (shippingGroupItemRelIterator.hasNext()) {
      // For each of these item relationships get the item.
      sgcir = (ShippingGroupCommerceItemRelationship) shippingGroupItemRelIterator.next();

      // Keep the old state and state detail information for the item
      oldState = sgcir.getState();

      if((oldState == sirs.getStateValue(ShipItemRelationshipStates.INITIAL)) ||        // initial state of the item
         (oldState == sirs.getStateValue(ShipItemRelationshipStates.FAILED)) ||         // maybe someone corrected the problem
         (oldState == sirs.getStateValue(ShipItemRelationshipStates.ITEM_NOT_FOUND)) || // maybe someone added it
         (oldState == sirs.getStateValue(ShipItemRelationshipStates.OUT_OF_STOCK)) ||   // maybe someone change stockLevel
         (oldState == sirs.getStateValue(ShipItemRelationshipStates.DISCONTINUED)) ||   // maybe it's made a comeback
         (oldState == sirs.getStateValue(ShipItemRelationshipStates.PRE_ORDERED)) ||    // the item was on the preorder
         (oldState == sirs.getStateValue(ShipItemRelationshipStates.BACK_ORDERED)))     // the item was on the backorder
      {
        //attempt to allocate
        allocated = allocateShippingGroupItem(of, pOrder, sgcir, performedModifications);
        // for shippable to be true, allocated must never be false
        // if allocated is false then there exists at least one item
        // that was not allocated meaning the group is not shippable
        shippable = allocated & shippable;

        // if the item failed, reset the shipping group to PENDING_MERCHANT_ACTION
        // if the item is out of stock, it is only considered failed if the outOfStockIsError
        // property is true.
        if((sgcir.getState() == sirs.getStateValue(ShipItemRelationshipStates.FAILED)) ||
           (sgcir.getState() == sirs.getStateValue(ShipItemRelationshipStates.ITEM_NOT_FOUND)) ||
           (sgcir.getState() == sirs.getStateValue(ShipItemRelationshipStates.DISCONTINUED)) ||
           ((sgcir.getState() == sirs.getStateValue(ShipItemRelationshipStates.OUT_OF_STOCK)) && of.isOutOfStockIsError())) {
            int pendingState = sgs.getStateValue(ShippingGroupStates.PENDING_MERCHANT_ACTION);
            CommerceItem item = sgcir.getCommerceItem();
            String id = null, name = null;
            if(item != null) {
                id = item.getCatalogRefId();
                RepositoryItem catalogRef = (RepositoryItem) item.getAuxiliaryData().getCatalogRef();
                if(catalogRef != null)
                    name = (String) catalogRef.getPropertyValue(of.getCatalogRefDisplayNamePropertyName());
            }
            if(pShippingGroup.getState() != pendingState) {
                String msg = Constants.ITEM_ALLOCATE_FAILED;
                of.getOrderFulfillmentTools().setShippingGroupState(pShippingGroup, pendingState,
                                                                    MessageFormat.format(msg, name, id),
                                                                    performedModifications);
            }
        }
      }
      else if(oldState == sirs.getStateValue(ShipItemRelationshipStates.PENDING_DELIVERY))
      {
        // do nothing
      }
      else if((oldState == sirs.getStateValue(ShipItemRelationshipStates.PENDING_REMOVE)) ||
              (oldState == sirs.getStateValue(ShipItemRelationshipStates.DELIVERED)))
      {
        if(of.isLoggingError())
            of.logError(MessageFormat.format(Constants.STATE_DOES_NOT_ALLOW_ALLOCATION, sgcir.getId()));
      }
    }

    if (!shippable)
        pResult.addError("ShippingGroup", 
                         "ShippingGroup couldn't allocate because its items didn't allocate");
    
    return SUCCESS;

  }
    
    
  /**
   * Allocates the ItemRelationship from the shipping group
   */
   protected boolean allocateShippingGroupItem(HardgoodFulfiller of, Order pOrder,
                                               ShippingGroupCommerceItemRelationship sgcir,
                                               List pModificationList) {

    try {
        HashMap pMap = new HashMap(4);
        pMap.put(PipelineConstants.ORDER, pOrder);
        pMap.put(PipelineConstants.ORDERFULFILLER, of);
        pMap.put(PipelineConstants.SHIPPINGGROUPCOMMERCEITEMREL, sgcir);
        pMap.put(PipelineConstants.MODIFICATIONLIST, pModificationList);
        PipelineResult result = 
            of.getFulfillmentPipelineManager().runProcess(getChainToRun(), pMap);
        if (result.hasErrors())
            return false;
    }
    catch(RunProcessException e) {
        Throwable t = e.getSourceException();
        if (t instanceof CommerceException) {
            if(of.isLoggingError())
                of.logError(e);
            try {
                of.getTransactionManager().getTransaction().setRollbackOnly();
            }
            catch (javax.transaction.SystemException se) {
                // Hopefully this will never happen.
                of.logError(se);
            }
        }   
        return false;
    }
    
    return true;
   }
    
}
