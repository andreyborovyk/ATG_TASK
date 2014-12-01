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
import atg.commerce.order.ChangedProperties;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.CommerceException;
import atg.commerce.states.*;

import java.util.*;
import javax.jms.*;
import java.text.*;

/**
 * This processor will "ship" the shipping group by setting its states to completion
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcFinishShippingGroup.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcFinishShippingGroup implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcFinishShippingGroup.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
   
  //-----------------------------------------------
  public ProcFinishShippingGroup() {
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
   * Ships the shipping group by setting its states to completion
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
    HardgoodFulfiller of = (HardgoodFulfiller) map.get(PipelineConstants.ORDERFULFILLER);
    Modification pModification = (Modification) map.get(PipelineConstants.MODIFICATION);
    ShippingGroup sg = (ShippingGroup) map.get(PipelineConstants.SHIPPINGGROUP);
    List performedModifications = (List) map.get(PipelineConstants.MODIFICATIONLIST);

    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));

    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    ShippingGroupStates sgs = of.getShippingGroupStates();
    ShipItemRelationshipStates sirs = of.getShipItemRelationshipStates();

    // Get the list of item relationships from the shipping group
    List shippingGroupItemRelationships = sg.getCommerceItemRelationships();
    Iterator shippingGroupItemRelIterator = shippingGroupItemRelationships.iterator();
    ShippingGroupCommerceItemRelationship sgcir = null;

    while (shippingGroupItemRelIterator.hasNext()) {
        // For each of these item relationships get the item.
        sgcir = (ShippingGroupCommerceItemRelationship) shippingGroupItemRelIterator.next();
        
        // skip this one if it is already updated
        if(sgcir.getState() == sirs.getStateValue(ShipItemRelationshipStates.DELIVERED)) {
            continue;
        }
        
        // skip this one if it has been removed
		if(sgcir.getState() == sirs.getStateValue(ShipItemRelationshipStates.REMOVED)) {
		    continue;
		}
        
        if(sgcir.getState() == sirs.getStateValue(ShipItemRelationshipStates.PENDING_DELIVERY)) {
            tools.setItemRelationshipState(sgcir, sirs.getStateValue(ShipItemRelationshipStates.DELIVERED), 
                                           null, performedModifications);
        } else {
            //((ChangedProperties) sgcir).clearChangedProperties();
            if(of.isLoggingError())
                of.logError(MessageFormat.format(Constants.INVALID_DELIVERY_STATE, sgcir.getId()));
            if(pModification != null)
                tools.modificationFailed(pModification,
                                         performedModifications,
                                         MessageFormat.format(Constants.INVALID_DELIVERY_STATE, 
                                        		 sgcir.getId()));
            pResult.addError("BadState", MessageFormat.format(Constants.INVALID_DELIVERY_STATE, sgcir.getId()));
            return STOP_CHAIN_EXECUTION;
        }
    }
    
    // now that the items are delivered, ship them...
    tools.setShippingGroupState(sg,
                                sgs.getStateValue(ShippingGroupStates.NO_PENDING_ACTION),
                                Constants.SHIP_CALL_SUCCEEDED, 
                                performedModifications);
    
    // set the shipping groups shipOnDate to the current Date
    java.util.Date shipDate = new java.util.Date();
    setShippedDate(of, sg, shipDate, performedModifications);
      
    return SUCCESS;
  }

  //-------------------------------------
  /**
   * This method sets the date when the shipping group shipped to the
   * given time.  The property that gets set is <code>actualShipDate</code>
   *
   * @beaninfo
   *          description: Set the date the given shipping group shipped to the given time.
   * @param pShippingGroup The shipping group that shipped.
   * @param pShipDate When the shipping group shipped.
   * @param pModificationList Place to store new modifications.
   **/
  public void setShippedDate(HardgoodFulfiller of, ShippingGroup pShippingGroup, 
                             Date pShipDate, List pModificationList)
  {
    java.util.Date oldTime = pShippingGroup.getActualShipDate();

    pShippingGroup.setActualShipDate(pShipDate);
    Modification m = of.getOrderFulfillmentTools().createShipUpdateModification("actualShipDate",
                                                                                oldTime, pShipDate,
                                                                                pShippingGroup.getId());

    pModificationList.add(m);
  }

}


