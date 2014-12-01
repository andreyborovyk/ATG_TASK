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

package atg.b2bcommerce.approval.processor;

import atg.service.pipeline.*;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceMap;
import atg.beans.*;
import atg.commerce.order.InvalidParameterException;
import atg.b2bcommerce.approval.*;

import javax.jms.*;
import java.util.*;

/**
 * This processor extracts properties from a JMS message object and populates them into the
 * pParam parameter. In addition it will also add nucleus components to the pParam parameter.
 * This processor assumes that pParam is a java.util.Map object. The ObjectMsgToPipelineParamMap
 * property maps property names to Strings which will be used as the keys in the map. See example:
 *
 * objectMsgToPipelineParamMap=\
 *      order=Order,\
 *      profile=Profile
 *
 * In the above, the property value named "order" will be added to the Map referenced by the
 * key "Order".
 *
 * The nucleusComponentParamMap contains nucleus object references mapped to Strings which will be
 * used as the keys in the map.
 *
 * nucleusComponentParamMap=\
 *       OrderManager=/atg/commerce/order/OrderManager
 *
 * In the above, the nucleus component /atg/commerce/order/OrderManager will be added to the Map
 * referenced by the key "OrderManager".
 *
 * Note that all properties supplied in the objectMsgToPipelineParamMap need to by objects and
 * cannot be primitive types.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/approval/processor/ProcPopulatePipelineParams.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ProcPopulatePipelineParams extends GenericService implements PipelineProcessor
{
  //-------------------------------------
  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/approval/processor/ProcPopulatePipelineParams.java#2 $$Change: 651448 $";

  //--------------------------------------------------
  public final int SUCCESS = 1;
  public final int FAILURE = 2;

  //-------------------------------------
  // property: successReturnValue
  //-------------------------------------
  private int mSuccessReturnValue = SUCCESS;

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
  // property: returnFailureOnPropertyNotFound
  //-------------------------------------
  private boolean mReturnFailureOnPropertyNotFound = false;

  /**
   * Returns property ReturnFailureOnPropertyNotFound
   *
   * @return returns property ReturnFailureOnPropertyNotFound
   */
  public boolean getReturnFailureOnPropertyNotFound() {
    return mReturnFailureOnPropertyNotFound;
  }

  /**
   * Sets property ReturnFailureOnPropertyNotFound
   *
   * @param pReturnFailureOnPropertyNotFound the value to set for property ReturnFailureOnPropertyNotFound
   */
  public void setReturnFailureOnPropertyNotFound(boolean pReturnFailureOnPropertyNotFound) {
    mReturnFailureOnPropertyNotFound = pReturnFailureOnPropertyNotFound;
  }

  //---------------------------------------------------------------------
  // property: ObjectMsgToPipelineParamMap
  Properties mObjectMsgToPipelineParamMap;

  /**
   * Return the ObjectMsgToPipelineParamMap property.
   * @return
   */
  public Properties getObjectMsgToPipelineParamMap() {
    return mObjectMsgToPipelineParamMap;
  }

  /**
   * Set the ObjectMsgToPipelineParamMap property.
   * @param pObjectMsgToPipelineParamMap
   */
  public void setObjectMsgToPipelineParamMap(Properties pObjectMsgToPipelineParamMap) {
    mObjectMsgToPipelineParamMap = pObjectMsgToPipelineParamMap;
  }

  //-------------------------------------
  // property: nucleusComponentParamMap
  //-------------------------------------
  private ServiceMap mNucleusComponentParamMap;

  /**
   * Returns property nucleusComponentParamMap
   *
   * @return returns property nucleusComponentParamMap
   */
  public ServiceMap getNucleusComponentParamMap() {
    return mNucleusComponentParamMap;
  }

  /**
   * Sets property nucleusComponentParamMap
   *
   * @param pNucleusComponentParamMap the value to set for property nucleusComponentParamMap
   */
  public void setNucleusComponentParamMap(ServiceMap pNucleusComponentParamMap) {
    mNucleusComponentParamMap = pNucleusComponentParamMap;
  }

  //--------------------------------------------------
  /**
   * Creates a new <code>ProcPopulatePipelineParams</code> instance.
   */
  public ProcPopulatePipelineParams() {
  }

  //--------------------------------------------------
  public int[] getRetCodes() {
    int[] retCodes = {getSuccessReturnValue(), getFailureReturnValue()};
    return retCodes;
  }

  //--------------------------------------------------
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    Map params = (Map) pParam;

    ObjectMessage message = (ObjectMessage) params.get(ApprovalConstants.OBJECT_MESSAGE);
    if (message == null)
      throw new InvalidParameterException(Constants.INVALID_MESSAGE_PARAMETER);

    Object messageObject = message.getObject();
    if (messageObject == null)
      throw new InvalidParameterException(Constants.INVALID_MESSAGE_OBJECT_PARAMETER);

    ServiceMap serviceMap = getNucleusComponentParamMap();
    Iterator iter = serviceMap.keySet().iterator();
    while (iter.hasNext()) {
      String key = (String) iter.next();
      params.put(key, serviceMap.get(key));
      if (isLoggingDebug())
        logDebug("Added parameter to pipeline params map. Key: " + key);
    }

    if (getObjectMsgToPipelineParamMap() != null &&
        getObjectMsgToPipelineParamMap().size() > 0)
    {
      Enumeration ObjectMessageProperties = getObjectMsgToPipelineParamMap().propertyNames();
      while (ObjectMessageProperties.hasMoreElements()) {
        String omProperty = (String) ObjectMessageProperties.nextElement();

        try {
          // get the value for the pipeline params from the object msg
          Object value = DynamicBeans.getSubPropertyValue(messageObject, omProperty);

          // we get the key for the pipeline params from
          // the value property of the ObjectMsgToPipelineParamMap
          Object pipelineParamKey = getObjectMsgToPipelineParamMap().getProperty(omProperty);
          params.put(pipelineParamKey, value);

          if (isLoggingDebug())
            logDebug("Just set property: " + pipelineParamKey + " to " + value);
        }
        catch (PropertyNotFoundException pnfe) {
          if (isLoggingWarning())
            logWarning(pnfe);

          if (getReturnFailureOnPropertyNotFound())
            return getFailureReturnValue();
        }
      } // if
    }

    return SUCCESS;
  }
} // end of class
