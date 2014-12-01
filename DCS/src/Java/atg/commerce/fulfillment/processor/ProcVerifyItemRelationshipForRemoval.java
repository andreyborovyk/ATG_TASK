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
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;

import java.util.*;
import javax.jms.*;
import java.text.*;

/**
 * This processor verifies if the item relatioship is in a state that allows it to be removed
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcVerifyItemRelationshipForRemoval.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcVerifyItemRelationshipForRemoval implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcVerifyItemRelationshipForRemoval.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcVerifyItemRelationshipForRemoval() {
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
   * This processor verifies if the item relatioship is in a state that allows it to be removed
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
    ShippingGroupCommerceItemRelationship sgcir = 
        (ShippingGroupCommerceItemRelationship) map.get(PipelineConstants.SHIPPINGGROUPCOMMERCEITEMREL);
    Modification pModification = (Modification) map.get(PipelineConstants.MODIFICATION);
    List performedModifications = (List) map.get(PipelineConstants.MODIFICATIONLIST);

    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));

    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    ShipItemRelationshipStates sirs = of.getShipItemRelationshipStates();
    int sgcirState = sgcir.getState();

    if(sgcirState == sirs.getStateValue(ShipItemRelationshipStates.REMOVED) ||
       sgcirState == sirs.getStateValue(ShipItemRelationshipStates.PENDING_REMOVE)) {
      // This shipping group has already been removed.
      return STOP_CHAIN_EXECUTION;
    }
    if((sgcirState == sirs.getStateValue(ShipItemRelationshipStates.DELIVERED)) ||
       (sgcirState == sirs.getStateValue(ShipItemRelationshipStates.PENDING_RETURN))) {
        tools.modificationFailed(pModification, performedModifications, 
                                 Constants.INVALID_SHIP_ITEM_REL_STATE);
        throw new InvalidParameterException(Constants.INVALID_SHIP_ITEM_REL_STATE);
    }
    
    return SUCCESS;
    
  }

}
