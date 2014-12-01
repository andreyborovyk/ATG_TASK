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
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;

import java.util.*;

/**
 * Check with the special instructions of the shipping group to see what the behavior should be if only
 * part of the group has been allocated.
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcShipAsItemsAreAvailable.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcShipAsItemsAreAvailable implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcShipAsItemsAreAvailable.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcShipAsItemsAreAvailable() {
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
   * Check with the special instructions of the shipping group to see what the behavior should be if only
   * part of the group has been allocated.
   * 
   * @beaninfo
   *          description: Return SUCCESS if the given shipping group should ship as much as possible.
   *                       STOP if all items must be allocated before anything ships.
   *
   * This method requires that a ShippingGroup and OrderFulfiller object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain a ShippingGroup and OrderFulfiller object
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
    ShippingGroup pShippingGroup = (ShippingGroup) map.get(PipelineConstants.SHIPPINGGROUP);
    
    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    
    if(of.isLoggingDebug())
      of.logDebug("Inside shipAsItemsAreAvailable");

    Map specialInstructions = null;
    boolean partialShipment = false;

    specialInstructions = pShippingGroup.getSpecialInstructions();
 
    if(specialInstructions != null) {
      Object allowPartialShipment = specialInstructions.get(of.getPartialShipPropertyName());
      if(allowPartialShipment == null) {
        if(of.isLoggingDebug())
          of.logDebug("SpecialInstruction " + of.getPartialShipPropertyName() + 
                      " was not found for " + pShippingGroup.getId());
        if (!of.isAllowPartialShipmentDefault())
            return STOP_CHAIN_EXECUTION; 
      }
      else if(allowPartialShipment instanceof Boolean) {
          if (!((Boolean)allowPartialShipment).booleanValue())
            return STOP_CHAIN_EXECUTION;
      }
      else if(allowPartialShipment instanceof String) {
        if(((String)allowPartialShipment).compareToIgnoreCase("true") == 0)
          partialShipment = true;
        else
          partialShipment = false;
        if (!partialShipment)
           return STOP_CHAIN_EXECUTION;
      }
      else {    
        if(of.isLoggingDebug())
          of.logDebug("SpecialInstruction " + of.getPartialShipPropertyName() + 
                      " was not a String or Boolean for " + pShippingGroup.getId());
         if (!of.isAllowPartialShipmentDefault())
             return STOP_CHAIN_EXECUTION;
      }
    } else {
      if(of.isLoggingDebug())
        of.logDebug("SpecialInstructions was null for " + pShippingGroup.getId());
      if (!of.isAllowPartialShipmentDefault())
            return STOP_CHAIN_EXECUTION; 
    }

    return SUCCESS;
  }

}
