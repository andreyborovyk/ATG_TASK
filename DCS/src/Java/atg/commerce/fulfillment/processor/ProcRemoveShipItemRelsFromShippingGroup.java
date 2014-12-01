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
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;

import java.util.*;
import javax.jms.*;
import java.text.*;

/**
 * This processor removes item Relationships from the shipping group. It will call to
 * the corresponding chain that removes each itemRel from the group.
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcRemoveShipItemRelsFromShippingGroup.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcRemoveShipItemRelsFromShippingGroup implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcRemoveShipItemRelsFromShippingGroup.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcRemoveShipItemRelsFromShippingGroup() {
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
   * Removes all item relationships from the shipping group, the call is made to the
   * chain that is responsible for removing.
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
    HardgoodFulfiller of = (HardgoodFulfiller) map.get(PipelineConstants.ORDERFULFILLER);
    Order pOrder = (Order) map.get(PipelineConstants.ORDER);
    Modification pModification = (Modification) map.get(PipelineConstants.MODIFICATION);
    ShippingGroup pShippingGroup = (ShippingGroup) map.get(PipelineConstants.SHIPPINGGROUP);
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
    ShippingGroupStates sgs = of.getShippingGroupStates();
    boolean success = true;

    // cycle through the item relationships and remove them
    List itemRels = pShippingGroup.getCommerceItemRelationships();
    Iterator itemRelIter = itemRels.iterator();
    ShippingGroupCommerceItemRelationship sgcir = null;
    
    while(itemRelIter.hasNext()) {
	sgcir = (ShippingGroupCommerceItemRelationship) itemRelIter.next();
	// use and here so we return false if any part of the removal did not complete
	success = success & removeShippingGroupItemRelationship(sgcir, 
                                                            map);
    }
    
    
    if (success) {
        String msg = Constants.SHIP_GROUP_STATE_REMOVED;
	tools.setShippingGroupState(pShippingGroup,
				    sgs.getStateValue(ShippingGroupStates.REMOVED),
                                    MessageFormat.format(msg, pShippingGroup.getId()),
				    performedModifications);
    }    
    else  { // one of the items could not be removed... PENDING_MERCHANT_ACTION
        String msg = Constants.ITEM_RELATIONSHIP_FAILED_REMOVE;
	tools.setShippingGroupState(pShippingGroup,
				    sgs.getStateValue(ShippingGroupStates.PENDING_MERCHANT_ACTION), 
                                    MessageFormat.format(msg, pShippingGroup.getId()),
				    performedModifications);
    }

    return SUCCESS;

  }


  //-------------------------------------
  /**
   * Remove the given shipping group by setting its state to REMOVED
   * If the group cannot be removed, then fail the Modification
   * Assume the group is not PENDING_SHIPMENT
   * This method creates a new pipeline param object with the given parameters 
   * and executes the <code>chainToRun</code> pipeline chain.
   * 
   * @deprecated use the version of this method that takes a ShippingGroupCommerceItemRelationship
   *              and an Object as the only two arguments 
   *
   * @param pOrder The order we are removing the shipping group item relationship from
   * @param pShippingGroupCommerceItemRelationship The shipping group item relationship to remove
   * @param pModification The modification request this action is in response to
   * @param pPerformedModifications The list to store our performed modifications in
   * @return true if the shipping group item relationship was successfully removed, false if the removal was not complete
   **/
  protected boolean removeShippingGroupItemRelationship(HardgoodFulfiller of, Order pOrder, 
                                                        ShippingGroupCommerceItemRelationship pShippingGroupItemRelationship,
                                                        Modification pModification,
                                                        List pPerformedModifications)
  {
    HashMap map = new HashMap(5);
    map.put(PipelineConstants.ORDERFULFILLER, of);
    map.put(PipelineConstants.ORDER, pOrder);
    map.put(PipelineConstants.MODIFICATION, pModification);
    map.put(PipelineConstants.SHIPPINGGROUPCOMMERCEITEMREL, pShippingGroupItemRelationship);
    map.put(PipelineConstants.MODIFICATIONLIST, pPerformedModifications);
    return removeShippingGroupItemRelationship(pShippingGroupItemRelationship, 
                                                map);  
  }
  
    //-------------------------------------
    /**
     * Remove the given shipping group by setting its state to REMOVED
     * If the group cannot be removed, then fail the Modification
     * Assume the group is not PENDING_SHIPMENT
     * This method adds the given relationship to the given pParam object 
     * and executes the <code>chainToRun</code> pipeline chain.
     *
     * @param pOrder The order we are removing the shipping group item relationship from
     * @param pShippingGroupCommerceItemRelationship The shipping group item relationship to remove
     * @param pModification The modification request this action is in response to
     * @param pPerformedModifications The list to store our performed modifications in
     * @return true if the shipping group item relationship was successfully removed, false if the removal was not complete
     **/
    protected boolean removeShippingGroupItemRelationship(ShippingGroupCommerceItemRelationship pShippingGroupItemRelationship,
                                                            Object pParam)
    {
      try {
        HashMap map = (HashMap) pParam;
        map.put(PipelineConstants.SHIPPINGGROUPCOMMERCEITEMREL, pShippingGroupItemRelationship);

        HardgoodFulfiller of = (HardgoodFulfiller) map.get(PipelineConstants.ORDERFULFILLER);
    
        PipelineResult result =
          of.getFulfillmentPipelineManager().runProcess(getChainToRun(), map);
      }
      catch(RunProcessException c) {
          return false;
      }

      return true;
  }
}
