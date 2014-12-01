/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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

package atg.projects.b2bstore.order;

// Java classes
import java.util.Map;

// DAS classes
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.repository.RepositoryItem;
import atg.core.util.ResourceUtils;

// DPS classes
import atg.userprofiling.Profile;

// DSS classes
import atg.process.ProcessExecutionContext;
import atg.process.ProcessException;
import atg.process.action.ActionImpl;

// DCS classes

/**
 * This Action handles updating user profile with avg order amount whenever
 * he places new order. It gets the value of Order's priceinfo from the Scenario
 * and updates the profile by calculating average amt using it.
 *
 * @author Manoj Potturu
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/order/AverageOrderAction.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class AverageOrderAction extends ActionImpl
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/order/AverageOrderAction.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  public static final String PARAM_ORDER_PRICE = "priceInfo";
  public static final String NUM_OF_ORDERS = "numOfOrders";
  public static final String AVG_ORDER_AMT = "avgOrderAmt";
  public static final String MY_RESOURCE_NAME = "atg.projects.b2bstore.order.AverageOrderActionResources";
  public static final String USER_PROPERTY_NOT_FOUND = "propertyNotFoundInProfile";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, java.util.Locale.getDefault());

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------


  //-------------------------------------
  // ActionImpl overrides
  //-------------------------------------


  public void initialize(Map pParameters)
    throws ProcessException
  {
    // Get the order's total amount and update the variable.
    storeRequiredParameter(pParameters, PARAM_ORDER_PRICE, Double.class);
  }

  protected void executeAction(ProcessExecutionContext pContext)
    throws ProcessException
  {
    RepositoryItem profile = null;
    // Get the profile on which the action is to be executed
    profile = pContext.getSubject();

    try {
      // Get the current order's total
      Double orderTotal = (Double)getParameterValue(PARAM_ORDER_PRICE, pContext);
      // Get the total no. of orders user places including this from the Profile
      Integer numOfOrders = (Integer)DynamicBeans.getPropertyValue(profile, NUM_OF_ORDERS);
      // Get the avg. order amt of the user from the Profile
      Double orgAvgTotal = (Double)DynamicBeans.getPropertyValue(profile, AVG_ORDER_AMT);
      // orgAvgTotal may be null if this is the first order of any user, make it 0
      if (orgAvgTotal == null) {
        orgAvgTotal = new Double(0);
      } // end of if ()
      // Calculate the new average order amount.
      double AvgTotal = calculateAverage(orgAvgTotal, orderTotal, numOfOrders);
      
      // Set the new average order amount to the Profile
      DynamicBeans.setPropertyValue(profile, AVG_ORDER_AMT, new Double(AvgTotal));
    }
    catch(PropertyNotFoundException pnfe) {
      String msg = ResourceUtils.getMsgResource(USER_PROPERTY_NOT_FOUND, MY_RESOURCE_NAME, sResourceBundle);
      throw new ProcessException(msg);
    }
      
  }

  /**
   * Calculates the Average value of all order amounts of user. 
   * Algorithm: numOfOrders give total no of orders for the given user including the
   * current one. Get the existing avg amount from the profile, multiply it by
   * (numOfOrders - 1) to get the total order amount excluding the current one. Then
   * add it to the current order amt and divide it by total number of orders(numOfOrders).
   * @param pOrgAvgTotal is the current average order total
   * @param pOrderTotal is the order amt of the current order
   * @param numOfOrders is the total no of orders placed by the user
   * @return the average order amt including the current order amt.
   *
   **/
  protected double calculateAverage(Double pOrgAvgTotal, Double pOrderTotal, Integer pNumOfOrders)
  {

    double orgAvgTotal = pOrgAvgTotal.doubleValue();
    double orderTotal = pOrderTotal.doubleValue();
    int numOfOrders = pNumOfOrders.intValue();
    
    double avgOrderAmt = (orgAvgTotal * (numOfOrders - 1) + orderTotal) / numOfOrders;

    return avgOrderAmt;
   
  }
}   // end of class


