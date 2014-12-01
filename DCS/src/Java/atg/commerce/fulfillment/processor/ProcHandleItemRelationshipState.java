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
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.CommerceException;
import atg.commerce.states.*;
import atg.commerce.inventory.InventoryManager;

import javax.jms.*;
import java.util.*;

/**
 * This processor routes the item allocation procedure based on the relationship's state
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcHandleItemRelationshipState.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcHandleItemRelationshipState implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcHandleItemRelationshipState.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int PURCHASE_NORMAL = 1;
  private final int PURCHASE_PREORDER = 2;
  private final int PURCHASE_BACKORDER = 3;
  
  //-----------------------------------------------
  public ProcHandleItemRelationshipState() {
  }

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The processor to purchase in stock items
   * 2 - The processor to purchase pre-ordered items
   * 3 - The processor to purchase back-ordered items
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = { PURCHASE_NORMAL, PURCHASE_PREORDER, 
                  PURCHASE_BACKORDER };
    return ret;
  } 

  //-----------------------------------------------
  /**
   * Gets the relationship's state to purchase in stock, pre-ordered or back-ordered items 
   *
   * This method requires that a OrderFulfiller object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain a OrderFulfiller object
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

    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    
    InventoryManager inventory = of.getInventoryManager();
    ShipItemRelationshipStates sirs = of.getShipItemRelationshipStates();

    if(inventory == null)
       throw new CommerceException(Constants.NO_INVENTORY_MANAGER);
    
    int currentState = sgcir.getState();

    if (currentState == sirs.getStateValue(ShipItemRelationshipStates.BACK_ORDERED))
        return PURCHASE_BACKORDER;
    else if (currentState == sirs.getStateValue(ShipItemRelationshipStates.PRE_ORDERED))
        return PURCHASE_PREORDER;

    return PURCHASE_NORMAL;

  }
    
}
