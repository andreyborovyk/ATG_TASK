/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

package atg.commerce.pricing.processor;

import java.util.Locale;
import java.util.Map;

import atg.commerce.order.Order;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.PricingTools;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;


/**
 * A pipeline processor which is used to reprice an order. The order is repriced
 * depending on the pricing operation command which is supplied. The operations which
 * are supported are defined in the <code>atg.commerce.pricing.PricingConstants</code> 
 * interface. They include:
 * <UL>
 * <LI>PricingConstants.OP_REPRICE_ORDER_TOTAL -> <code>ORDER_TOTAL</code>
 * <LI>PricingConstants.OP_REPRICE_ORDER_SUBTOTAL -> <code>ORDER_SUBTOTAL</code>
 * <LI>PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_SHIPPING -> <code>ORDER_SUBTOTAL_SHIPPING</code>
 * <LI>PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_TAX -> <code>ORDER_SUBTOTAL_TAX</code>
 * <LI>PricingConstants.OP_REPRICE_ITEMS -> <code>ITEMS</code>
 * <LI>PricingConstants.OP_REPRICE_SHIPPING -> <code>SHIPPING</code>
 * <LI>PricingConstants.OP_REPRICE_ORDER -> <code>ORDER</code>
 * <LI>PricingConstants.OP_REPRICE_TAX -> <code>TAX</code>
 * </UL>
 *
 * @see atg.commerce.pricing.PricingConstants
 * 
 * @author Bob Mason
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/processor/PriceOrderTotal.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public 
class PriceOrderTotal extends ApplicationLoggingImpl
implements PipelineProcessor
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/processor/PriceOrderTotal.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  private final int SUCCESS = 1;
  private final int FAILURE = 2;
  private final int [] RET_CODES = {SUCCESS, FAILURE};

  //-------------------------------------
  // Member Variables
  //-------------------------------------

  //-------------------------------------
  // Properties
  //-------------------------------------
  
  //-------------------------------------
  // property: PricingTools
  PricingTools mPricingTools;

  /**
   * Sets property PricingTools
   **/
  public void setPricingTools(PricingTools pPricingTools) {
    mPricingTools = pPricingTools;
  }

  /**
   * Returns property PricingTools
   **/
  public PricingTools getPricingTools() {
    return mPricingTools;
  }
  
  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instanceof PriceOrderTotal
   */
  public PriceOrderTotal() {
  }

  /**
   * Reprice the order by extracting out the necessary parameters from the Map which should
   * be supplied as the param <code>pParam</code>. The following parameters should be in the map.
   * <UL>
   * <LI>PricingConstants.PRICING_OPERATION_PARAM
   * <LI>PricingConstants.ORDER_PARAM
   * <LI>PricingConstants.PRICING_MODELS_PARAM (optional: if executed in the context of a request)
   * <LI>PricingConstants.LOCALE_PARAM (optional)
   * <LI>PricingConstants.PROFILE_PARAM
   * <LI>PricingConstants.EXTRA_PARAMETERS_PARAM (optional)
   * </UL>
   * @param pParam The object should be an instance of Map
   * @param pResult the PipelineResult object which is used to return errors or other data
   * @return an integer value which is mapped to the next processor to execute. 
   * <code>SUCCESS</code>: (1) OR <code>STOP_CHAIN_EXECUTION_AND_ROLLBACK</code>:
   */
  public int runProcess(Object pParam, PipelineResult pResult) 
       throws Exception
  {
    Map params = (Map)pParam;
    String pricingOperation = (String)params.get(PricingConstants.PRICING_OPERATION_PARAM);
    Order order = (Order)params.get(PricingConstants.ORDER_PARAM);
    PricingModelHolder pricingModels = (PricingModelHolder)params.get(PricingConstants.PRICING_MODELS_PARAM);
    Locale locale = (Locale)params.get(PricingConstants.LOCALE_PARAM);
    RepositoryItem profile = (RepositoryItem)params.get(PricingConstants.PROFILE_PARAM);
    Map extraParameters = (Map)params.get(PricingConstants.EXTRA_PARAMETERS_PARAM);
    
    try {
      getPricingTools().performPricingOperation(pricingOperation,
                                                order, pricingModels, locale, profile, extraParameters);
      
      return SUCCESS;
    }
    catch (PricingException exc) {
      if(isLoggingError())
        logError(exc);
      pResult.addError("PriceOrderTotalError", exc.getMessage());
      return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
    }
  }

  /**
   * Returns the valid return codes
   * 1 - Order priced successfully
   * 2 - Order pricing failed
   * @return an array of all the return codes that could be returned by this PipelineProcessor.
   */
  public int[] getRetCodes() {
    return RET_CODES;
  }

} // end of class
