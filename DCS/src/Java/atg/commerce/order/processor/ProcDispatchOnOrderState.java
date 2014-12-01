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

package atg.commerce.order.processor;

import java.util.*;

import atg.service.pipeline.*;
import atg.commerce.order.*;
import atg.commerce.states.*;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.nucleus.GenericService;

/**
 * Dispatch to the next processor element based on the string value
 * of the order state.
 * <p>
 * This processor allows you to specify a list of order states and
 * an associated integer return value for each one.  If the order being
 * processed through the pipeline is in one of the specified states, the
 * corresponding value is returned.  Otherwise, a default value is returned.
 * <p>
 * For example, a pipeline might branch based on whether an order requires
 * approval, has been approved, or has been rejected by configuring an instance
 * of this processor and including it in the pipeline as follows:
 * <code>
 *   &lt;pipelinelink name="DispatchOnOrderState"&gt;
 *     &lt;processor jndi="/atg/commerce/order/processor/DispatchOnOrderState"/&gt;
 *     &lt;transition returnvalue="1" link="waitForApproval"/&gt;
 *     &lt;transition returnvalue="2" link="notifyOrderApproved"/&gt;
 *     &lt;transition returnvalue="3" link="notifyOrderRejected"/&gt;
 *     &lt;transition returnvalue="4" link="continueProcessing"/&gt;
 *   &lt;/pipelinelink&gt;
 * </code>
 * <p>
 * The mapping of order states to transition values is set through the
 * <code>returnValues</code> property of this pipeline processor, e.g.:
 * <p>
 * <code>
 *   returnValues=\
 *     PENDING_APPROVAL=1,\
 *     APPROVED=2,\
 *     FAILED_APPROVAL=3
 *
 *    defaultReturnValue=4
 * </code>
 * <p>
 * You can assign the same return value to multiple state values if you
 * want them to follow the same pipeline processor transition.  
 * <p>
 * This processor assumes that the type of the pipeline arguments object
 * is compatible with and may be cast to java.util.Map.
 *
 * @author Matt Landau
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcDispatchOnOrderState.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.commerce.order.processor.ProcDispatchOnProperty
 **/

public class ProcDispatchOnOrderState
  extends GenericService
  implements PipelineProcessor
{
  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcDispatchOnOrderState.java#2 $$Change: 651448 $";

  //---------------------------------------------------------------------------
  // Resource bundle info

  static final String RESOURCE_NAME =
      "atg.commerce.order.OrderResources";
  protected static ResourceBundle sDefaultResources =
      LayeredResourceBundle.getBundle(RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  //---------------------------------------------------------------------------
  // property: returnValues
  //---------------------------------------------------------------------------

  Properties mReturnValues;

  /**
   * Specify the mapping between string values of the order state and
   * return values from <code>runProcess</code>.
   *
   * @param pReturnValues
   *    A properties object that specifies mappings between order
   *    states and return values.  Each value in the properties
   *    object must be parsable as an integer.  The
   *    <code>returnValues</code> object is of type Properties only
   *    to simplify setting the values from a Nucleus configuration
   *    file.
   **/
  
  public void setReturnValues(Properties pReturnValues) {
    mReturnValues = pReturnValues;
  }

  /**
   * Return the mapping between values of <code>propertyName</code> and
   * return values from <code>runProcess</code>.
   **/
  
  public Properties getReturnValues() {
    return mReturnValues;
  }

  //---------------------------------------------------------------------------
  // property: defaultReturnValue
  //---------------------------------------------------------------------------

  int mDefaultReturnValue = 4000;

  /**
   * Set the default value to return if the order's state
   * is not found in the lookup table.
   **/
   
  public void setDefaultReturnValue(int pDefaultReturnValue) {
    mDefaultReturnValue = pDefaultReturnValue;
  }

  /**
   * Get the default value to return if the order's state
   * is not found in the lookup table.  The default value
   * for this property is 4000.
   **/

  public int getDefaultReturnValue() {
    return mDefaultReturnValue;
  }

  //---------------------------------------------------------------------------
  // property: warnOnDefaultValue
  //---------------------------------------------------------------------------

  boolean mWarnOnDefaultValue = false;

  /**
   * Specify whether or not to log a warning when returning
   * <code>defaultReturnValue</code> for an order state that was
   * not found in the lookup table.
   **/
  
  public void setWarnOnDefaultValue(boolean pWarnOnDefaultValue) {
    mWarnOnDefaultValue = pWarnOnDefaultValue;
  }

  /**
   * Query whether or not to log a warning when returning
   * <code>defaultReturnValue</code> for a property value that was
   * not found in the lookup table.  The default value is false.
   **/
  
  public boolean isWarnOnDefaultValue() {
    return mWarnOnDefaultValue;
  }

  //---------------------------------------------------------------------------
  // readonly property: valueMap
  //---------------------------------------------------------------------------

  HashMap mValueMap;

  /**
   * Get the mapping from order states to Integer return codes.  This
   * mapping is built by traversing the <code>returnValues</code> Properties
   * object and building a parallel structure whose keys are strings, but
   * whose values are Integers instead of Strings, as in the Properties
   * object.
   **/
  
  public HashMap getValueMap()
  {
    if (mValueMap == null)
      convertReturnValues(getReturnValues());
    return mValueMap;
  }

  //---------------------------------------------------------------------------
  // property: retCodes
  //---------------------------------------------------------------------------

  int[] mRetCodes;

  /**
   * Get the list of return values this processor can return.  This list is
   * built by traversing the <code>returnValues</code> Properties object and
   * the <code>defaultReturnValue</code> and building an array containing
   * exactly one copy of each value that can be returned.
   **/

  public int[] getRetCodes()
  {
    if (mRetCodes == null)
      convertReturnValues(getReturnValues());
    return mRetCodes;
  }

  //---------------------------------------------------------------------------

  /**
   * Look up the order in the pipeline arguments map, retrieve its order
   * state as a string, and return a value based on that string.
   *
   * @param pParam
   *    Pipeline arguments object, assumed to be of a type that can be
   *    cast to java.util.Map
   * @param pResult
   *    Pipeline results object, not used by this method.
   * @throws ClassCastException
   *    If the type of pParam is not compatible with java.util.Map.
   * @throws InvalidParameterException
   *    If the order object is null or is not of type atg.commerce.Order
   *    in the parameter map.
   **/
   
  public int runProcess(Object pParam, PipelineResult pResult)
    throws ClassCastException, InvalidParameterException
  {
    Map params = (Map)pParam;
    Order order = null;

    try {
      order = (Order) params.get(PipelineConstants.ORDER);      
      if (order == null)
        throw new InvalidParameterException(
          ResourceUtils.getMsgResource(
            "InvalidOrderParameter", RESOURCE_NAME, sDefaultResources));
    }
    catch (ClassCastException cce) {
      String msg = ResourceUtils.getMsgResource(
        "InvalidOrderParameter", RESOURCE_NAME, sDefaultResources);
      throw new InvalidParameterException(msg, cce);
    }
    
    String orderState = StateDefinitions.ORDERSTATES.getStateString(order.getState());

    if (isLoggingDebug())
      logDebug("Order state for order: " + order.getId() + " is " + orderState);
      
    int ret = getTransitionValue(orderState);

    if (isLoggingDebug())
      logDebug("Returning transition value " + ret + " for order state " + orderState);

    return ret;
  }
  
  //---------------------------------------------------------------------------

  /**
   * Given a value for the property specified by <code>propertyName</code>,
   * find the corresponding return value from <code>returnValues</code>, 
   * choosing the default value if the property value was not listed in
   * the lookup table.
   *
   * @param pPropertyValue
   *    The value upon which to dispatch to the next pipeline processor.
   **/
   
  protected int getTransitionValue(Object pPropertyValue)
  {
    Integer obj = (Integer)getValueMap().get(pPropertyValue);

    if (obj == null)
    {
      if (isLoggingWarning() && isWarnOnDefaultValue())
      {
        Integer defVal = Integer.valueOf(getDefaultReturnValue());
        Object[] args = { defVal, pPropertyValue };
        
        logWarning(ResourceUtils.getMsgResource(
          "dispatchOnUnregisteredValue", RESOURCE_NAME, sDefaultResources, args));
      }
      return getDefaultReturnValue();
    }
    else
    {
      return obj.intValue();
    }
  }
  
  //---------------------------------------------------------------------------

  /**
   * Build the <code>valueMap</code> and <code>retCodes</code> objects from
   * the values specified in <code>pReturnValues</code>.
   **/
  
  protected void convertReturnValues(Properties pReturnValues)
  {
    // This method is a little twisted, but since it's only called once
    // when the returnValues property is set I'm not too worried about the
    // weird contortions it goes through to create the sorted array of
    // unique integer return codes and the mapping of property keys to
    // Integer values instead of strings.

    TreeSet valueSet = new TreeSet();
    HashMap valueMap = new HashMap(pReturnValues.size());
    Enumeration names = pReturnValues.propertyNames();

    while (names.hasMoreElements())
    {
      String name = (String)names.nextElement();
      String value = pReturnValues.getProperty(name);
      Integer intValue = Integer.valueOf(value);
      
      valueMap.put(name, intValue);
      valueSet.add(intValue);
    }

    // Now we have the mapping of names to integer values instead of to
    // strings, but we still need to generate the return codes array of
    // integers.

    valueSet.add(Integer.valueOf(getDefaultReturnValue()));
    
    int[] retCodes = new int[valueSet.size()];
    Iterator iter = valueSet.iterator();
    int n = 0;
    
    while (iter.hasNext())
    {
      Integer value = (Integer)iter.next();
      retCodes[n++] = value.intValue();
    }

    mValueMap = valueMap;
    mRetCodes = retCodes;
  }
}
