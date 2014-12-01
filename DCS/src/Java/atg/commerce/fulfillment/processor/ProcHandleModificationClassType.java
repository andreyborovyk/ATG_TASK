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

import atg.nucleus.GenericService;
import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;
import atg.commerce.*;
import atg.commerce.fulfillment.*;
import atg.commerce.inventory.InventoryManager;
import atg.commerce.messaging.*;
import atg.commerce.order.Order;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;

import java.util.*;
import javax.jms.*;
import java.text.*;

/**
 * This processor will route the modification execution based on the modification class type
 * Works for both ModifyOrder and ModifyOrderNotification JMS messages
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcHandleModificationClassType.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcHandleModificationClassType extends GenericService implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcHandleModificationClassType.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  private final int MODIFICATION_NOT_SUPPORTED = 2;
    
  //-----------------------------------------------
  public ProcHandleModificationClassType() {
  }

  //---------------------------------------------------------------------------
  // property:UpdateInventoryOnCancelOrder
  //---------------------------------------------------------------------------
  private boolean mUpdateInventoryOnCancelOrder = true;
  public void setUpdateInventoryOnCancelOrder(boolean pUpdateInventoryOnCancelOrder) {
    mUpdateInventoryOnCancelOrder = pUpdateInventoryOnCancelOrder;
  }

  /**
   * If this is true, then cancel an order whose inventory has already been allocated 
   * will result in a call to InventoryManager.inventoryWasUpdated
   * This defaults to true
   * @beaninfo description: If this is true, then cancel an order whose inventory has already been 
   *                        allocated will result in a call to InventoryManager.inventoryWasUpdated
   **/
  public boolean isUpdateInventoryOnCancelOrder() {
    return mUpdateInventoryOnCancelOrder;
  }

  //---------------------------------------------------------------------------
  // property:InventoryManager
  //---------------------------------------------------------------------------
  private InventoryManager mInventoryManager;
  public void setInventoryManager(InventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }

  /**
   * The InventoryManager that is notified when inventory is updated
   * @beaninfo description: The InventoryManager that is notified when inventory is updated
   **/
  public InventoryManager getInventoryManager() {
    return mInventoryManager;
  }

  
  /**
   * A map of the modification chains to run
   **/
  private Properties mChainToRunMap;
  
  public void setChainToRunMap(Properties pChainToRunMap) {
      mChainToRunMap = pChainToRunMap;
  }

  public Properties getChainToRunMap() {
      return mChainToRunMap;
  }



  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The processor completed
   * 2 - The modification is not supported
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = {SUCCESS, MODIFICATION_NOT_SUPPORTED};
    return ret;
  } 

  //-----------------------------------------------
  /**
   * This processor will route the order modification execution based on the modification class type
   * Works for both ModifyOrder and ModifyOrderNotification JMS messages
   *
   * This method requires that a Commerce message and OrderFulfiller object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain a Commerce message and OrderFulfiller object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    CommerceMessage pMessage = (CommerceMessage) map.get(PipelineConstants.COMMERCEMESSAGE);
    FulfillerSystem of = (FulfillerSystem) map.get(PipelineConstants.ORDERFULFILLER);
    Order pOrder = (Order) map.get(PipelineConstants.ORDER);
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
    
    if (pMessage == null) {
        if (of.isLoggingError())
            of.logError(Constants.COMMERCE_MESSAGE_NULL);
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidMessageParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    }
    
    Modification[] modifications = null;
    String id = null;

    if (pMessage instanceof ModifyOrder) {
        modifications = ((ModifyOrder) pMessage).getModifications();
        id = ((ModifyOrder) pMessage).getId();
    }
    else {
        modifications = ((ModifyOrderNotification) pMessage).getModifications();
        id = ((ModifyOrderNotification) pMessage).getId();
    }

    // see if any modifications concern us ("borrowed" from hanldeModifyOrderNotification)
    if (modifications == null) {
        if(of.isLoggingError())
            of.logError(MessageFormat.format(Constants.NO_MODIFICATIONS, id));
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidModificationParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    }

    // remember any inventory updates that may have happened so we can notify the 
    // InventoryManager
    Set inventoryUpdates = new HashSet();
    
    // process the modification array from the message
    for(int i = 0; i < modifications.length; i++) {

        HashMap pMap = new HashMap(7);
        pMap.put(PipelineConstants.ORDER, pOrder);
        pMap.put(PipelineConstants.ORDERFULFILLER, of);
        pMap.put(PipelineConstants.COMMERCEMESSAGE, pMessage);
        pMap.put(PipelineConstants.MODIFICATION, modifications[i]);
        pMap.put(PipelineConstants.MODIFICATIONLIST, performedModifications);
        
        if(of instanceof OrderFulfiller) {
          if (pMessage instanceof ModifyOrder) {
             if(modifications[i] instanceof IdTargetModification)
                handleIdTargetModification((String)getChainToRunMap().get("PerformIdTargetModification"), of, pMap);
             else // do not support anything else 
                return MODIFICATION_NOT_SUPPORTED;
          }
          else { // ModifyOrderNotification message
             if(modifications[i] instanceof IdTargetModification)
                handleIdTargetModification((String)getChainToRunMap().get("HandleIdTargetModification"), of, pMap);
             else if(modifications[i].getModificationType() == Modification.SHIPPING_GROUP_UPDATE)
                handleShipGroupUpdateModification((String)getChainToRunMap().get("HandleShipGroupUpdateModification"),
                                                  of, pMap);
             else if(modifications[i].getModificationType() == Modification.PAYMENT_GROUP_UPDATE)
                handlePayGroupUpdateModification((String)getChainToRunMap().get("HandlePayGroupUpdateModification"), 
                                                 of, pMap);
             else 
                return MODIFICATION_NOT_SUPPORTED;
          }
        } // HardgoodFulfiller
        else if(of instanceof HardgoodFulfiller) {
          if (pMessage instanceof ModifyOrder)
              handleIdTargetModification((String)getChainToRunMap().get("HardgoodPerformIdTargetModification"), of, pMap);
          else { // ModifyOrderNotification
              handleShipGroupUpdateModification((String)getChainToRunMap().get("HardgoodShipGroupUpdateModification"), of, pMap);
          }
        }
        else { // ElectronicFulfiller
          if (pMessage instanceof ModifyOrder){
            if(performedModifications == null)
              map.put(PipelineConstants.MODIFICATIONLIST, new ArrayList());
            map.put(PipelineConstants.MODIFICATION, modifications[i]);
            return MODIFICATION_NOT_SUPPORTED;
          }
		  // ModifyOrderNotification
          handleShipGroupUpdateModification((String)getChainToRunMap().get("SoftgoodShipGroupUpdateModification"), of, pMap);
        }
        
        Set updates = (Set) pMap.get(PipelineConstants.INVENTORYUPDATES);
        if(updates != null)
          inventoryUpdates.addAll(updates);
    }
    
    if((inventoryUpdates.size() > 0) &&
        (isUpdateInventoryOnCancelOrder())) {
      // notify the inventory manager of the updates
      InventoryManager im = getInventoryManager();
      if(im == null) {
        if(isLoggingError())
          logError(ResourceUtils.getMsgResource("NullInventoryManager", MY_RESOURCE_NAME, sResourceBundle));
      } else {
        List itemIds = new ArrayList();
        itemIds.addAll(inventoryUpdates);
        im.inventoryWasUpdated(itemIds); 
      }
    }
    
    return SUCCESS;
    
  }
    
  /**
   * Handles a modification of the IdTargetModification class type from both the ModifyOrder
   * and ModifyOrderNotification messages
   */
  protected void handleIdTargetModification(String chainToRun, FulfillerSystem of, HashMap pMap) 
      throws Exception
  {
      PipelineResult result = null;
      // execute the id target modification chain passing the Hashmap object
      result = of.getFulfillmentPipelineManager().runProcess(chainToRun, pMap);
      
  }
    
  /**
   * Handles a modification of the ShippingGroupUpdate type from the ModifyOrderNotification message
   */
  protected void handleShipGroupUpdateModification(String chainToRun, FulfillerSystem of, HashMap pMap) 
      throws Exception
  {   
      PipelineResult result = null;
      // execute the  modification chain passing the Hashmap object
      result = of.getFulfillmentPipelineManager().runProcess(chainToRun, pMap);
      
  }
    
    
  /**
   * Handles a modification of the PaymentGroupUpdate type from the ModifyOrderNotification message
   */
  protected void handlePayGroupUpdateModification(String chainToRun, FulfillerSystem of, HashMap pMap) 
      throws Exception
  {
      PipelineResult result = null;
      // execute the id target modification chain passing the Hashmap object
      result = of.getFulfillmentPipelineManager().runProcess(chainToRun, pMap);
        
  }
    
}
