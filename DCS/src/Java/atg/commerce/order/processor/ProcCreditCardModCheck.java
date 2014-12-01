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

import atg.service.pipeline.*;
import atg.commerce.order.*;
import atg.payment.creditcard.*;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;

import java.util.*;

/**
 * This processor does a mod check on credit card numbers to see if they are valid.
 * The verifyCreditCard method of the atg.payment.creditcard.CreditCardTools class is
 * called on each credit card number in the Order.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcCreditCardModCheck.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcCreditCardModCheck extends ApplicationLoggingImpl implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcCreditCardModCheck.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcCreditCardModCheck() {
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
  String mLoggingIdentifier = "ProcCreditCardModCheck";

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

  //-------------------------------------
  // property: creditCardTools
  //-------------------------------------
  private ExtendableCreditCardTools mCreditCardTools = null;

  /**
   * Returns property creditCardTools
   *
   * @return returns property creditCardTools
   */
  public ExtendableCreditCardTools getCreditCardTools() {
    return mCreditCardTools;
  }

  /**
   * Sets property creditCardTools
   *
   * @param pCreditCardTools the value to set for property creditCardTools
   */
  public void setCreditCardTools(ExtendableCreditCardTools pCreditCardTools) {
    mCreditCardTools = pCreditCardTools;
  }

  //-----------------------------------------------
  /**
   * This method executes simple credit card validation. If it succeeds SUCCESS is returned, otherwise
   * STOP_CHAIN_EXECUTION_AND_ROLLBACK is returned. The credit card validation which occurs is the
   * functionality in the verifyCreditCard() method in CreditCardTools.
   *
   * This method requires that an Order object be supplied in pParam in a HashMap.
   * Use the PipelineConstants class' static members to key the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   * @see atg.payment.creditcard.CreditCardTools#verifyCreditCard(CreditCardInfo)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    OrderManager om = (OrderManager)map.get(PipelineConstants.ORDERMANAGER);
    
    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));

    int status;
    Iterator iter = order.getPaymentGroups().iterator();
    for (PaymentGroup pg = null; iter.hasNext(); ) {
      pg = (PaymentGroup) iter.next();
      if (! (pg instanceof CreditCard))
        continue;

      // skip the empty ones
      if(!om.getPaymentGroupManager().isPaymentGroupUsed(order, pg))
        continue;

      status = CreditCardTools.verifyCreditCard((CreditCardInfo) pg);
      if (status != CreditCardTools.SUCCESS) {
        if (map.containsKey(PipelineConstants.LOCALE)) {
          Locale locale = (Locale) map.get(PipelineConstants.LOCALE);
          if (isLoggingDebug())
            logDebug("Credit Card verification failed: " + CreditCardTools.getStatusCodeMessage(status, locale));
          addHashedError(pResult, PipelineConstants.CREDITCARDVERIFYFAILED, pg.getId(),
                         CreditCardTools.getStatusCodeMessage(status, locale));
        }
        else {
          if (isLoggingDebug())
            logDebug("Credit Card verification failed: " + CreditCardTools.getStatusCodeMessage(status));
          addHashedError(pResult, PipelineConstants.CREDITCARDVERIFYFAILED, pg.getId(),
                         CreditCardTools.getStatusCodeMessage(status));
        }
      }
    }

    if (pResult.hasErrors())
      return STOP_CHAIN_EXECUTION_AND_ROLLBACK;

    return SUCCESS;
  }

  //--------------------------------------
  /**
   * This method adds an error to the PipelineResult object. This method, rather than
   * just storing a single error object in pResult, stores a Map of errors. This allows more
   * than one error to be stored using the same key in the pResult object. pKey is
   * used to reference a HashMap of errors in pResult. So, calling
   * pResult.getError(pKey) will return an object which should be cast to a Map.
   * Each entry within the map is keyed by pId and its value is pError.
   *
   * @param pResult the PipelineResult object supplied in runProcess()
   * @param pKey the key to use to store the HashMap in the PipelineResult object
   * @param pId the key to use to store the error message within the HashMap in the
   *            PipelineResult object
   * @param pError the error object to store in the HashMap
   * @see atg.service.pipeline.PipelineResult
   * @see #runProcess(Object, PipelineResult)
   */
  protected void addHashedError(PipelineResult pResult, String pKey, String pId, Object pError)
  {
    Object error = pResult.getError(pKey);
    if (error == null) {
      HashMap map = new HashMap(5);
      pResult.addError(pKey, map);
      map.put(pId, pError);
    }
    else if (error instanceof Map) {
      Map map = (Map) error;
      map.put(pId, pError);
    }
  }
}
