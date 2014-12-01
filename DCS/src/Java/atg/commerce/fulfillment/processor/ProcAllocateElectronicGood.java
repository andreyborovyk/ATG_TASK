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
import atg.commerce.order.ElectronicShippingGroup;
import atg.commerce.order.RelationshipTypes;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;
import atg.commerce.CommerceException;
import atg.beans.PropertyNotFoundException;

import java.util.*;

/**
 * This processor allocates softgood shipping group by allocating all its item relationships
 *
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcAllocateElectronicGood.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcAllocateElectronicGood implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcAllocateElectronicGood.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcAllocateElectronicGood() {
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
   * Allocates electronic shipping group by allocating all its item relationships
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
    ElectronicFulfiller of = (ElectronicFulfiller) map.get(PipelineConstants.ORDERFULFILLER);
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
    ElectronicShippingGroup eShippingGroup = (ElectronicShippingGroup) pShippingGroup;
    
    List shippingGroupCommerceItemRel = pShippingGroup.getCommerceItemRelationships();
    Iterator shippingGroupCommerceItemRelIterator = shippingGroupCommerceItemRel.iterator();
    boolean allShipped = true, success = true;

    if (of.isLoggingDebug())
        of.logDebug("There are " + shippingGroupCommerceItemRel.size() +" commerce items in the sg");
    
    // for each ci relationship
    while(shippingGroupCommerceItemRelIterator.hasNext()) {
        sgcir = (ShippingGroupCommerceItemRelationship)shippingGroupCommerceItemRelIterator.next();
        
        if(sgcir.getState() == sirs.getStateValue(ShipItemRelationshipStates.DELIVERED)) {
            if(of.isLoggingDebug())
                of.logDebug("The item " + sgcir.getCommerceItem().getCatalogRefId() + 
                            " has already been sent.");
            continue;
        }
      
        // get the quantity
        long quantity=0;
        
        // find out how many items to allocate from inventory
        int relationshipType = sgcir.getRelationshipType();
        switch(relationshipType)
         {
         case RelationshipTypes.SHIPPINGQUANTITY:
             {              
                 quantity = sgcir.getQuantity();
                 break;
             }
         case RelationshipTypes.SHIPPINGQUANTITYREMAINING:
             {
                 // calculate the quantity
                 quantity = of.getOrderManager().getRemainingQuantityForShippingGroup(sgcir.getCommerceItem());
                 break;
             }
         } //switch
        
        allShipped = true;
        for (int i=0; i<quantity; i++) {
            allShipped = allShipped & allocateElectronicGood(of, pOrder, eShippingGroup, 
                                                             sgcir, performedModifications);
        }

        if (allShipped)
            of.getOrderFulfillmentTools().setItemRelationshipState(sgcir,
                                                                   sirs.getStateValue(ShipItemRelationshipStates.DELIVERED),
                                                                   null, 
                                                                   performedModifications);
        success = success & allShipped;
    }
    
    if (success) {
        of.getOrderFulfillmentTools().setShippingGroupState(pShippingGroup,
                                                            sgs.getStateValue(ShippingGroupStates.NO_PENDING_ACTION),
                                                            null, 
                                                            performedModifications);
    } else {
        of.getOrderFulfillmentTools().setShippingGroupState(pShippingGroup,
                                                            sgs.getStateValue(ShippingGroupStates.PENDING_MERCHANT_ACTION),
                                                            null, 
                                                            performedModifications);
    }
    
    return SUCCESS;
  }
    
    
  /**
   * Allocates the ItemRelationship from the shipping group
   */
   protected boolean allocateElectronicGood(ElectronicFulfiller of, Order pOrder,
                                            ElectronicShippingGroup sg,
                                            ShippingGroupCommerceItemRelationship sgcir,
                                            List pModificationList) throws Exception
   {
       ShipItemRelationshipStates sirs = of.getShipItemRelationshipStates();

       try {
           HashMap pMap = new HashMap(5);
           pMap.put(PipelineConstants.ORDER, pOrder);
           pMap.put(PipelineConstants.ORDERFULFILLER, of);
           pMap.put(PipelineConstants.SHIPPINGGROUP, sg);
           pMap.put(PipelineConstants.SHIPPINGGROUPCOMMERCEITEMREL, sgcir);
           PipelineResult result = 
               of.getFulfillmentPipelineManager().runProcess(getChainToRun(), pMap);
       }
       catch(RunProcessException e) {
           if(of.isLoggingError())
               of.logError(e.toString());
           Throwable t = e.getSourceException();
           if (t instanceof PropertyNotFoundException)
               throw (PropertyNotFoundException)t;
           of.getOrderFulfillmentTools().setItemRelationshipState(sgcir,
                                                                  sirs.getStateValue(ShipItemRelationshipStates.FAILED),
                                                                  t.toString(),
                                                                  pModificationList);
           return false;
       }
       
       return true;
   }
    
}
