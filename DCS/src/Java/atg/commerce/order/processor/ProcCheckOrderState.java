/*<ATGCOPYRIGHT>
 * Copyright (C) 2000-2011 Art Technology Group, Inc.
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
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.commerce.order.*;
import atg.commerce.states.*;
import atg.core.util.ResourceUtils;

import java.util.*;

/**
 * This processor returns the value in successReturnValue if the order state is in the
 * statesList property. Otherwise it returns the value in failureReturnValue.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcCheckOrderState.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ProcCheckOrderState extends ApplicationLoggingImpl implements PipelineProcessor
{
  //-------------------------------------
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcCheckOrderState.java#2 $$Change: 651448 $";
    
  //-------------------------------------
  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  public final int FAILURE = 1;

  //-------------------------------------
  // property: successReturnValue
  //-------------------------------------
  private int mSuccessReturnValue = STOP_CHAIN_EXECUTION_AND_COMMIT;
  
  /**
   * Returns property successReturnValue
   *
   * @return returns property successReturnValue
   */
  public int getSuccessReturnValue() {
    return mSuccessReturnValue;
  }

  /**
   * Sets property successReturnValue
   *
   * @param pSuccessReturnValue the value to set for property successReturnValue
   */
  public void setSuccessReturnValue(int pSuccessReturnValue) {
    mSuccessReturnValue = pSuccessReturnValue;
  }

  //-------------------------------------
  // property: failureReturnValue
  //-------------------------------------
  private int mFailureReturnValue = FAILURE;
  
  /**
   * Returns property failureReturnValue
   *
   * @return returns property failureReturnValue
   */
  public int getFailureReturnValue() {
    return mFailureReturnValue;
  }

  /**
   * Sets property failureReturnValue
   *
   * @param pFailureReturnValue the value to set for property failureReturnValue
   */
  public void setFailureReturnValue(int pFailureReturnValue) {
    mFailureReturnValue = pFailureReturnValue;
  }

  //-------------------------------------
  // property: statesList
  //-------------------------------------
  private String[] mStatesList;

  /**
   * Returns property StatesList
   *
   * @return returns property StatesList
   */
  public String[] getStatesList() {
    return mStatesList;
  }

  /**
   * Sets property StatesList
   *
   * @param pStatesList the value to set for property StatesList
   */
  public void setStatesList(String[] pStatesList) {
    mStatesList = pStatesList;
  }

  //--------------------------------------------------
  public ProcCheckOrderState() {
  }
  
  //--------------------------------------------------
  /**
   * This method returns the value in successReturnValue if the order state is in the
   * statesList property. Otherwise it returns the value in failureReturnValue.
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    Map params = (Map)pParam;
    Order order = (Order) params.get(PipelineConstants.ORDER);      

    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                                         MY_RESOURCE_NAME, sResourceBundle));
      
    String orderState = StateDefinitions.ORDERSTATES.getStateString(order.getState());
    if (isLoggingDebug())
      logDebug("Order state for order: " + order.getId() + " is " + orderState);
      
    String[] states = getStatesList();
    for (int i = 0; i < states.length; i++) {
      if (orderState.equalsIgnoreCase(states[i]))
        return getSuccessReturnValue();
    }
    
    return getFailureReturnValue();
  }

  //--------------------------------------------------
  public int[] getRetCodes() {
    int[] retCodes = {getSuccessReturnValue(), getFailureReturnValue()};
    return retCodes;
  }
} // end of class
