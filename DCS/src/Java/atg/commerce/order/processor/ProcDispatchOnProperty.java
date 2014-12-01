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
import atg.beans.DynamicBeans;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.nucleus.GenericService;

/**
 * Dispatch to the next processor element based on the value of some
 * named property of some designated object in the pipeline arguments
 * map.
 * <p>
 * This processor allows you to specify an object in the pipeline
 * arguments map and the name of some property on that object.  It finds
 * the named property's value, then looks up that value in a dispatch
 * table and returns the corresponding integer value.  This allows you
 * to control pipeline transitions based on a property value.
 * <p>
 * For example, a generic "validatePaymentGroup" pipeline might branch
 * based on the payment group class type by configuring an instance of
 * this processor to examine the "paymentGroupClassType" property and
 * including it in the pipeline as follows:
 * <p>
 * <code>
 *   &lt;pipelinelink name="DispatchOnType"&gt;
 *     &lt;processor jndi="/atg/commerce/order/processor/DispatchOnPaymentType"/&gt;
 *     &lt;transition returnvalue="1" link="ValidateCreditCard"/&gt;
 *     &lt;transition returnvalue="2" link="ValidateStoreCredit"/&gt;
 *     &lt;transition returnvalue="3" link="ValidateGiftCertificate"/&gt;
 *   &lt;/pipelinelink&gt;
 * </code>
 * <p>
 * The mapping of payment types to transition values is set through the
 * <code>returnValues</code> property of this pipeline processor, e.g.:
 * <p>
 * <code>
 *   returnValues=\
 *     creditCard=1,\
 *     storeCredit=2,\
 *     giftCertificate=3,\
 *     myCreditCard=1
 * </code>
 * <p>
 * You can assign the same return value to multiple property values if you
 * want them to follow the same pipeline processor transition.  For example,
 * you could have your own extended credit card payment method use the
 * same validation process as the build-in credit card payment method by
 * assigning both methods the same return value as shown above.
 * <p>
 * This processor assumes that the type of the pipeline arguments object
 * is compatible with and may be cast to java.util.Map.
 
 * @author Matt Landau
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcDispatchOnProperty.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class ProcDispatchOnProperty
  extends GenericService
  implements PipelineProcessor
{
  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcDispatchOnProperty.java#2 $$Change: 651448 $";

  //---------------------------------------------------------------------------
  // Resource bundle info

  static final String RESOURCE_NAME =
      "atg.commerce.order.OrderResources";
  protected static ResourceBundle sDefaultResources =
      LayeredResourceBundle.getBundle(RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  //---------------------------------------------------------------------------
  // property: pipelineKey
  //---------------------------------------------------------------------------

  String mPipelineKey;

  /**
   * Set the lookup key that will identify the target object in the pipeline
   * arguments map.
   **/
  
  public void setPipelineKey(String pPipelineKey) {
    mPipelineKey = pPipelineKey;
  }

  /**
   * Get the lookup key that will identify the target object in the pipeline
   * arguments map.
   **/

  public String getPipelineKey() {
    return mPipelineKey;
  }

  //---------------------------------------------------------------------------
  // property: propertyName
  //---------------------------------------------------------------------------

  String mPropertyName;

  /**
   * Set the name of the property whose value will determine the next
   * processor invoked.
   **/
  
  public void setPropertyName(String pPropertyName) {
    mPropertyName = pPropertyName;
  }

  /**
   * Set the name of the property whose value will determine the next
   * processor invoked.
   **/

  public String getPropertyName() {
    return mPropertyName;
  }

  //---------------------------------------------------------------------------
  // property: returnValues
  //---------------------------------------------------------------------------

  Properties mReturnValues;

  /**
   * Specify the mapping between values of <code>propertyName</code> and
   * return values from <code>runProcess</code>.
   *
   * @param pReturnValues
   *    A properties object that specifies mappings between property
   *    values and return values expressed as strings.  Each value in
   *    the properties object must be parsable as an integer.  The
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

  int mDefaultReturnValue = 0;

  /**
   * Set the default value to return if <code>propertyName's</code> value
   * is not found in the lookup table.
   **/
   
  public void setDefaultReturnValue(int pDefaultReturnValue) {
    mDefaultReturnValue = pDefaultReturnValue;
  }

  /**
   * Get the default value to return if <code>propertyName's</code> value
   * is not found in the lookup table.
   **/

  public int getDefaultReturnValue() {
    return mDefaultReturnValue;
  }

  //---------------------------------------------------------------------------
  // property: warnOnDefaultValue
  //---------------------------------------------------------------------------

  boolean mWarnOnDefaultValue = true;

  /**
   * Specify whether or not to log a warning when returning
   * <code>defaultReturnValue</code> for a property value that was
   * not found in the lookup table.
   **/
  
  public void setWarnOnDefaultValue(boolean pWarnOnDefaultValue) {
    mWarnOnDefaultValue = pWarnOnDefaultValue;
  }

  /**
   * Query whether or not to log a warning when returning
   * <code>defaultReturnValue</code> for a property value that was
   * not found in the lookup table.  The default value is true.
   **/
  
  public boolean isWarnOnDefaultValue() {
    return mWarnOnDefaultValue;
  }

  //---------------------------------------------------------------------------
  // readonly property: valueMap
  //---------------------------------------------------------------------------

  HashMap mValueMap;

  /**
   * Get the mapping from property values to Integer return codes.  This
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
   * Look up the object identified by <code>pipelineKey</code> in the
   * arguments map, retrieve the property named by
   * <code>propertyName</code>, and return a value based on that property's
   * value.
   *
   * @param pParam
   *    Pipeline arguments object, assumed to be of a type that can be
   *    cast to java.util.Map
   * @param pResult
   *    Pipeline results object, not used by this method.
   * @throws ClassCastException
   *    If the type of pParam is not compatible with java.util.Map.
   * @throws InvalidParameterException
   *    If the object named by <code>pipelineKey</code> does not appear
   *    in the parameter map.
   * @throws PropertyNotFoundException
   *    If the object does not have a property with the name specified
   *    by <code>propertyName</code>.
   **/
   
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    Object obj = ((Map)pParam).get(getPipelineKey());

    if (obj == null)
      throw new atg.commerce.order.InvalidParameterException(
        ResourceUtils.getMsgResource(
          "missingPipelineParam",
          RESOURCE_NAME,
          sDefaultResources,
          new Object[] { getPipelineKey() }));

    Object propertyValue = DynamicBeans.getPropertyValue(obj, getPropertyName());
    int ret = getTransitionValue(propertyValue);

    if (isLoggingDebug())
      logDebug("Returning transition value " + ret + " for property value " + propertyValue);

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

  //---------------------------------------------------------------------------

  /**
   * Test method for <code>convertReturnValues</code> and
   * <code>getTransitionValue</code>.
   **/
   
  public static void main(String[] args)
  {
    Properties values = new Properties();
    values.setProperty("creditCard", "1");
    values.setProperty("invoiceRequest", "4");
    values.setProperty("termAccount", "3");
    values.setProperty("invoice", "4");

    ProcDispatchOnProperty processor = new ProcDispatchOnProperty();
    processor.setReturnValues(values);
    processor.setDefaultReturnValue(4);

    int[] codes = processor.getRetCodes();
    System.out.print("getRetCodes returns [");
    for (int n = 0 ; n < codes.length ; n++) {
      System.out.print(codes[n]);
      if (n != codes.length - 1)
        System.out.print(", ");
    }
    System.out.println("]");

    System.out.println("Transition values:");
    Enumeration names = values.propertyNames();

    System.out.println("default => " + processor.getDefaultReturnValue());
    
    while (names.hasMoreElements()) {
      String name = (String)names.nextElement();
      System.out.println(name + " => " + processor.getTransitionValue(name));
    }

    System.out.println("NoSuchName => " + processor.getTransitionValue("NoSuchName"));
  }
}
