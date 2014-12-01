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
import java.util.Map;

import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PipelineConstants;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

/**
 * This processor validates that all there is at least one CommerceItem, one ShippingGroup, and one
 * PaymentGroup in the Order.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidateOrderForCheckout.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcValidateOrderForCheckout extends ApplicationLoggingImpl implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidateOrderForCheckout.java#2 $$Change: 651448 $";

  static final String RESOURCE_NAME = "atg.commerce.order.OrderResources";
  static final String USER_MSGS_RES_NAME = "atg.commerce.order.UserMessages";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = LayeredResourceBundle.getBundle(RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  private static java.util.ResourceBundle sUserResourceBundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, java.util.Locale.getDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcValidateOrderForCheckout() {
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
  String mLoggingIdentifier = "ProcValidateOrderForCheckout";

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
   * This method validates that at least one CommerceItem, ShippingGroup, and PaymentGroup
   * exist in the Order.
   *
   * To do additional validation for Orders, override this method to validate
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
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    java.util.Locale locale = (java.util.Locale) map.get(PipelineConstants.LOCALE);
    boolean validateCommerceItem = true;
    boolean validateShippingGroup = true;
    boolean validatePaymentGroup = true;
    
    Boolean flag = (Boolean) map.get(PipelineConstants.VALIDATECOMMERCEITEMINORDER);
    if (flag != null)
      validateCommerceItem = flag.booleanValue();
    flag = (Boolean) map.get(PipelineConstants.VALIDATESHIPPINGGROUPINORDER);
    if (flag != null)
      validateShippingGroup = flag.booleanValue();
    flag = (Boolean) map.get(PipelineConstants.VALIDATEPAYMENTGROUPINORDER);
    if (flag != null)
      validatePaymentGroup = flag.booleanValue();

    java.util.ResourceBundle resourceBundle;
    if (locale == null)
      resourceBundle = sUserResourceBundle;
    else
      resourceBundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, locale);

    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      RESOURCE_NAME, sResourceBundle));

    if (validateCommerceItem && order.getCommerceItemCount() == 0) {
      String msg = resourceBundle.getString("NoCommerceItemsInOrder");
      pResult.addError("NoCommerceItemsInOrder", msg);
    }
    
    if (validateShippingGroup && order.getShippingGroupCount() == 0) {
      String msg = resourceBundle.getString("NoShippingGroupsInOrder");
      pResult.addError("NoShippingGroupsInOrder", msg);
    }
 
    if (validatePaymentGroup && order.getPaymentGroupCount() == 0) {
      String msg = resourceBundle.getString("NoPaymentGroupsInOrder");
      pResult.addError("NoPaymentGroupsInOrder", msg);
    }
    
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
