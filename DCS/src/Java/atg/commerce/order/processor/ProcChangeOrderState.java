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
import atg.commerce.order.Order;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.OrderStates;
import atg.core.util.ResourceUtils;

import java.util.*;

/**
 * This processor changes the state of the order to the state in the newOrderState property.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcChangeOrderState.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ProcChangeOrderState extends ApplicationLoggingImpl implements PipelineProcessor
{
  //-------------------------------------
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcChangeOrderState.java#2 $$Change: 651448 $";
    
  //-------------------------------------
  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  public static final int SUCCESS = 1;

  //-------------------------------------
  // property: orderStates
  OrderStates mOrderStates;

  /**
   * Return the orderStates property.
   * @return
   */
  public OrderStates getOrderStates() {
    return mOrderStates;
  }

  /**
   * Set the orderStates property.
   * @param pOrderStates
   */
  public void setOrderStates(OrderStates pOrderStates) {
    mOrderStates = pOrderStates;
  }

  //-------------------------------------
  // property: orderStatePropertyName
  String mOrderStatePropertyName;

  /**
   * Return the orderStatePropertyName property.
   * @return
   */
  public String getOrderStatePropertyName() {
    return mOrderStatePropertyName;
  }

  /**
   * Set the orderStatePropertyName property.
   * @param pOrderStatePropertyName
   */
  public void setOrderStatePropertyName(String pOrderStatePropertyName) {
    mOrderStatePropertyName = pOrderStatePropertyName;
  }

  //-------------------------------------
  // property: newOrderState
  String mNewOrderState;

  /**
   * Return the newOrderState property.
   * @return
   */
  public String getNewOrderState() {
    return mNewOrderState;
  }

  /**
   * Set the newOrderState property.
   * @param pNewOrderState
   */
  public void setNewOrderState(String pNewOrderState) {
    mNewOrderState = pNewOrderState;
  }

  //-------------------------------------
  // property: newStateDetail
  //-------------------------------------
  private String mNewStateDetail;

  /**
   * Returns property newStateDetail
   *
   * @return returns property newStateDetail
   */
  public String getNewStateDetail() {
    return mNewStateDetail;
  }

  /**
   * Sets property newStateDetail
   *
   * @param pNewStateDetail the value to set for property newStateDetail
   */
  public void setNewStateDetail(String pNewStateDetail) {
    mNewStateDetail = pNewStateDetail;
  }

  //-------------------------------------
  /**
   * Creates a new <code>ProcChangeOrderState</code> instance.
   */
  public ProcChangeOrderState() {
  }

  //-------------------------------------
  /**
   * This method attempts to get the necessary arguments
   * to change an order state.  It does this by trying to
   * extract the order from the list of parameters handed to this
   * method.  It then calls the <code>changeOrderState</code>
   * method.
   *
   * @param pParam list of params to pipeline
   * @param pResult the pipeline result object
   * @return code indicating state
   * @exception Exception if an error occurs
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    Map pParams = (Map)pParam;
    Order order = (Order)pParams.get(PipelineConstants.ORDER);
    
    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                                         MY_RESOURCE_NAME, sResourceBundle));
    
    changeOrderState(order);
    changeOrderStateDetail(order);

    return SUCCESS;
  }
  
  /**
   * Changes the order state to be the state configured in
   * the newOrderState property.
   *
   * @param pOrder an <code>Order</code> value.  This is the order
   * whose state will be changed.
   */
  protected void changeOrderState(Order pOrder) 
    throws Exception
  {
    if (isLoggingDebug())
      logDebug("About to change state of order: " + pOrder.getId() + " to: " + getNewOrderState());
      
    pOrder.setState(getOrderStates().getStateValue(getNewOrderState()));
  }
  
  /**
   * Changes the order state detail to be the string configured in
   * the newStateDetail property. If the newStateDetail property
   * in this class is null, then the state detail will not be changed.
   *
   * @param pOrder an <code>Order</code> value.  This is the order
   * whose state detail will be changed.
   */
  protected void changeOrderStateDetail(Order pOrder) 
    throws Exception
  {
    String newStateDetail = getNewStateDetail();

    if (isLoggingDebug())
      logDebug("About to change state detail of order: " + pOrder.getId() + " to: " + newStateDetail);
      
    if (newStateDetail != null)
      pOrder.setStateDetail(newStateDetail);
  }
  
  /**
   * The return codes that this processor can return.
   * The list of return codes are:
   * <UL>
   *   <LI>1 - The processor completed
   * </UL>
   *
   * @return an <code>int[]</code> of the valid return codes
   */
  public int[] getRetCodes() {
    int[] retCodes = {SUCCESS};
    return retCodes;
  }
} // end of class
