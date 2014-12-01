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

package atg.commerce.order.processor;

import java.util.HashMap;
import java.util.Iterator;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.commerce.pricing.TaxPriceInfo;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

/**
 * Validates that all the PriceInfo objects in the Order have been priced using the same
 * currency code. The currency code in the OrderPriceInfo object is the one which must
 * be matched. The code checks the TaxPriceInfo object's currency code in the Order and
 * all the ShippingPriceInfo and ItemPriceInfo currency codes in all the ShippingGroups
 * and CommerceItems, respectively.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidateCurrencyCodes.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcValidateCurrencyCodes extends ApplicationLoggingImpl implements PipelineProcessor {  
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidateCurrencyCodes.java#2 $$Change: 651448 $";

  static final String RESOURCE_NAME = "atg.commerce.order.OrderResources";
  static final String USER_MSGS_RES_NAME = "atg.commerce.order.UserMessages";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = LayeredResourceBundle.getBundle(RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  private static java.util.ResourceBundle sUserResourceBundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, java.util.Locale.getDefault());

  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcValidateCurrencyCodes() {
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

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcValidateCurrencyCodes";

  /**
   * Sets property LoggingIdentifier
   **/
  public void setLoggingIdentifier(String pLoggingIdentifier) {
    mLoggingIdentifier = pLoggingIdentifier;
  }

  /**
   * Returns property LoggingIdentifier
   **/
  public String getLoggingIdentifier() {
    return mLoggingIdentifier;
  }

  //-----------------------------------------------
  /**
   * Validates that all the PriceInfo objects in the Order have been priced using the same
   * currency code. The currency code in the OrderPriceInfo object is the one which must
   * be matched. The code checks the TaxPriceInfo object's currency code in the Order and
   * all the ShippingPriceInfo and ItemPriceInfo currency codes in all the ShippingGroups
   * and CommerceItems, respectively.
   *
   * To do additional validation for currency codes, override this method to validate
   * and then call this class' runProcess() method.
   *
   * This method requires that an Order and optionally a Locale object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order and optionally a Locale object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   * @see atg.commerce.pricing.AmountInfo
   * @see atg.commerce.pricing.OrderPriceInfo
   * @see atg.commerce.pricing.TaxPriceInfo
   * @see atg.commerce.pricing.ItemPriceInfo
   * @see atg.commerce.pricing.ShippingPriceInfo
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    java.util.Locale locale = (java.util.Locale) map.get(PipelineConstants.LOCALE);
    java.util.ResourceBundle resourceBundle;
    
    if (locale == null)
      resourceBundle = sUserResourceBundle;
    else
      resourceBundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, locale);

    // check for null parameters
    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      RESOURCE_NAME, sResourceBundle));

    OrderPriceInfo opi = order.getPriceInfo();
    if (opi == null) {
      pResult.addError("NullOrderPriceInfo", resourceBundle.getString("NullOrderPriceInfo"));
      return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
    }
      
    TaxPriceInfo tpi = order.getTaxPriceInfo();
    if (tpi == null) {
      pResult.addError("NullTaxPriceInfo", resourceBundle.getString("NullTaxPriceInfo"));
      return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
    }
      
    String currencyCode = opi.getCurrencyCode();
    ShippingGroup sg;
    CommerceItem ci;
    Iterator iter;
    
    if (! currencyCode.equalsIgnoreCase(tpi.getCurrencyCode()))
      pResult.addError("InconsistentTaxCurrencyCode", resourceBundle.getString("InconsistentTaxCurrencyCode"));

    ItemPriceInfo ipi;
    iter = order.getCommerceItems().iterator();
    while (iter.hasNext()) {
      ci = (CommerceItem) iter.next();
      ipi = ci.getPriceInfo();
      if (ipi == null) {
        pResult.addError("NullItemPriceInfo", resourceBundle.getString("NullItemPriceInfo"));
        return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
      }
      if (! currencyCode.equalsIgnoreCase(ipi.getCurrencyCode())) {
        pResult.addError("InconsistentItemCurrencyCode", resourceBundle.getString("InconsistentItemCurrencyCode"));
        break;
      }
    }

    ShippingPriceInfo spi;
    iter = order.getShippingGroups().iterator();
    while (iter.hasNext()) {
      sg = (ShippingGroup) iter.next();
      spi = sg.getPriceInfo();
      if (spi == null) {
        pResult.addError("NullShippingPriceInfo", resourceBundle.getString("NullShippingPriceInfo"));
        return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
      }
      if (! currencyCode.equalsIgnoreCase(spi.getCurrencyCode())) {
        pResult.addError("InconsistentShipCurrencyCode", resourceBundle.getString("InconsistentShipCurrencyCode"));
        break;
      }
    }

    return SUCCESS;
  }
}
