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
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.RelationshipTypes;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;

import javax.jms.*;
import java.util.*;

/**
 * This processor gets the itemRelationship's quantity to allocate it from the inventory
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcRetrieveItemRelQuantity.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcRetrieveItemRelQuantity implements PipelineProcessor {
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcRetrieveItemRelQuantity.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private static final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcRetrieveItemRelQuantity() {
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
   * Gets the itemRelationship's quantity to call the inventory for allocation
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
    List performedModifications = (List) map.get(PipelineConstants.MODIFICATIONLIST);

    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    
    if(of.isLoggingDebug())
       of.logDebug("Inside RetrieveItemRelQuantity");

    CommerceItem item = null;
    int relationshipType;
    long quantity = 0;
 
    item = sgcir.getCommerceItem();
    relationshipType = sgcir.getRelationshipType();
 
    // find out how many items to allocate from inventory
    switch(relationshipType)
    {
    case RelationshipTypes.SHIPPINGQUANTITY:
      {       
        quantity = sgcir.getQuantity();
        break;
      }
    case RelationshipTypes.SHIPPINGQUANTITYREMAINING:
      {
        quantity = of.getOrderManager().getRemainingQuantityForShippingGroup(item);
        break;
      }
    } //switch

    map.put(PipelineConstants.SGCIRQUANTITY, Long.valueOf(quantity));

    return SUCCESS;

  }
    
}
