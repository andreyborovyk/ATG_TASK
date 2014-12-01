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

import atg.commerce.order.*;
import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import java.util.*;

/**
 * This processor calls updateOrder() on the supplied Order and then checks to see
 * if it is transient. If it is transient then addOrder() is called in the OrderManager
 * to make the Order persistent.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcAddOrderToRepository.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.commerce.order.OrderManager#addOrder(Order)
 * @see atg.commerce.order.OrderManager#updateOrder(Order)
 */
public class ProcAddOrderToRepository extends ApplicationLoggingImpl implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcAddOrderToRepository.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcAddOrderToRepository() {
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
  String mLoggingIdentifier = "ProcAddOrderToRepository";

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
   * This method calls updateOrder() on the supplied Order and then checks to see
   * if it is transient. If it is transient then addOrder() is called in the OrderManager
   * to make the Order persistent.
   *
   * This method requires that an Order and OrderManager object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order and OrderManager object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   * @see atg.commerce.order.OrderManager#addOrder(Order)
   * @see atg.commerce.order.OrderManager#updateOrder(Order)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    OrderManager mgr = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);
    Order order = (Order) map.get(PipelineConstants.ORDER);

    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (mgr == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderManagerParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));

    mgr.updateOrder(order);

    if (order.isTransient()) {
      if (isLoggingDebug())
        logDebug("Order " + order.getId() + " is transient. Adding to Order Repository.");
      mgr.addOrder(order); // addOrder also calls updateOrder
  	}

    return SUCCESS;
  }
}
