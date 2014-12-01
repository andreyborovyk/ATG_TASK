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
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.CommerceException;
import atg.repository.RepositoryItem;
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;

import java.util.*;

/**
 * This processor allocates an electronic item by calling the ClaimableManager
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcCreateElectronicGood.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcCreateElectronicGood implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcCreateElectronicGood.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcCreateElectronicGood() {
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
   * Allocates an electronic item by calling the ClaimableManager
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
    ShippingGroupCommerceItemRelationship sgcir = 
        (ShippingGroupCommerceItemRelationship) map.get(PipelineConstants.SHIPPINGGROUPCOMMERCEITEMREL);

    if (pOrder == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    
    Double amount = 
        (Double)DynamicBeans.getPropertyValue(sgcir.getCommerceItem().getAuxiliaryData().getCatalogRef(),
                                              of.getGiftCertificateAmountProperty());
    
    String purchaser = pOrder.getProfileId();
    RepositoryItem electronicClaimable = createElectronicGood(of, amount, amount, 
                                                              purchaser, new Date());

    map.put(PipelineConstants.ELECTRONICGOOD, electronicClaimable);

    return SUCCESS;
  }


  /**
   * This method is used to create the electronic good that is being
   * fulfilled.  In this implementation that means create a gift certificate
   * in the claimable repository.  The gift certificate will then be initialized
   * with the specified amount, amountRemaining, purchaserId and date.
   *
   *
   * @param pAmount the amount of the gift certificate
   * @param pAmountRemaining the amount left on a gift certificate
   * @param pPurchaserId profile id of the person who purchased the gift certificate
   * @param pPurchaseDate the date the gift certificate was purchased
   * @return the created electronic good
   * @exception CommerceException if an error occurs
   */
  protected RepositoryItem createElectronicGood(ElectronicFulfiller of, 
                                                Double pAmount,
                                                Double pAmountRemaining,
                                                String pPurchaserId,
                                                Date pPurchaseDate)
      throws CommerceException
    {
        if (of.isLoggingDebug())
            of.logDebug("going to try and create an electronic good");
        
        RepositoryItem electronicClaimable = 
            of.getClaimableManager().createClaimableGiftCertificate();
        
        if (electronicClaimable == null) {
            throw new CommerceException(Constants.UNABLE_TO_CREATE_GOOD);
        }
        
        of.getClaimableManager().initializeClaimableGiftCertificate(electronicClaimable,
                                                                    pAmount,
                                                                    pAmountRemaining,
                                                                    pPurchaserId,
                                                                    pPurchaseDate);
        return electronicClaimable;
    }

}
