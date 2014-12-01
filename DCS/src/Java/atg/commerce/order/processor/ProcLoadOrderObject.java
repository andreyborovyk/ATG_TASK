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

import atg.repository.*;
import atg.commerce.order.*;
import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;
import atg.beans.*;

import java.util.HashMap;

/**
 * This processor loads the Order object from the OrderRepository.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcLoadOrderObject.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.commerce.order.Order
 */
public class ProcLoadOrderObject extends LoadProperties implements PipelineProcessor {  
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcLoadOrderObject.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcLoadOrderObject() {
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
  // property: orderItemDescriptorName
  //-------------------------------------
  private String mOrderItemDescriptorName = "order";

  /**
   * Returns the orderItemDescriptorName name. This is the order repository item name in
   * the OrderRepository.
   */
  public String getOrderItemDescriptorName() {
    return mOrderItemDescriptorName;
  }

  /**
   * Sets the orderItemDescriptorName name. This is the order repository item name in
   * the OrderRepository.
   */
  public void setOrderItemDescriptorName(String pOrderItemDescriptorName) {
    mOrderItemDescriptorName = pOrderItemDescriptorName;
  }

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcLoadOrderObject";

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
   * This method loads the Order object from the OrderRepository.
   * It does this by constructing a new Order instance based on the class mapped to
   * the repository item type of the Order. It then iterates through the properties
   * listed in the loadProperties property inherited by this class, setting the values in the
   * object. Finally, it adds the Order and Order repository item to the pParam HashMap.
   *
   * This method requires that an Order id, OrderRepository, and OrderManager object
   * be supplied in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order id, OrderRepository, and OrderManager object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    String orderId = (String) map.get(PipelineConstants.ORDERID);
    Order order = (Order) map.get(PipelineConstants.ORDER);
    MutableRepositoryItem mutItem = (MutableRepositoryItem) map.get(PipelineConstants.ORDERREPOSITORYITEM);
    MutableRepository mutRep = (MutableRepository) map.get(PipelineConstants.ORDERREPOSITORY);
    OrderManager orderManager = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);
    Boolean invalidateCache = (Boolean) map.get(PipelineConstants.INVALIDATECACHE);
    
    // check for null parameters
    if (orderId == null && order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderIdParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (mutRep == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidRepositoryParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (orderManager == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderManagerParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (order != null && mutItem == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidRepositoryItemParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (invalidateCache == null)
      invalidateCache = Boolean.FALSE;
    
    OrderTools orderTools = orderManager.getOrderTools();
    
    if (order != null)
      orderId = order.getId();

    if (invalidateCache.booleanValue()) {
      try {
        ItemDescriptorImpl d = (ItemDescriptorImpl) mutRep.getItemDescriptor(getOrderItemDescriptorName());
        invalidateCache(d, mutItem);
      }
      catch (RepositoryException e) {
        if (isLoggingWarning())
          logWarning("Unable to invalidate item descriptor " + getOrderItemDescriptorName() + ":" + orderId);
      }
    }

    if (mutItem == null) {
      mutItem = mutRep.getItemForUpdate(orderId, getOrderItemDescriptorName());
      if (mutItem == null) {
        String[] msgArgs = { orderId };
        throw new InvalidParameterException(ResourceUtils.getMsgResource("NonExistentOrderIdParameter",
                                        MY_RESOURCE_NAME, sResourceBundle, msgArgs));
      }
    }

    RepositoryItemDescriptor desc = mutItem.getItemDescriptor();
    String className = orderTools.getMappedBeanName(desc.getItemDescriptorName());
    String[] loadProperties = getLoadProperties();
    CommerceIdentifier ci;
    String mappedPropName;
    Object value;
    
    if (order != null)
      ci = order;
    else
      ci = (CommerceIdentifier) Class.forName(className).newInstance();

    DynamicBeans.setPropertyValue(ci, "id", mutItem.getRepositoryId());    
    if (ci instanceof ChangedProperties)
      ((ChangedProperties) ci).setRepositoryItem(mutItem);
    
    // this is where the properties are loaded from the repository into the order
    for (int i = 0; i < loadProperties.length; i++) {
      mappedPropName = getMappedPropertyName(loadProperties[i]);
      if (desc.hasProperty(loadProperties[i])) {
        value = mutItem.getPropertyValue(loadProperties[i]);
        if (isLoggingDebug())
          logDebug("load property[" + loadProperties[i] + ":" + value + ":" 
                      + ci.getClass().getName() + ":" + ci.getId() + "]");
        OrderRepositoryUtils.setPropertyValue((Order) ci, ci, mappedPropName, value);
      }
    }

    if (ci instanceof ChangedProperties)
      ((ChangedProperties) ci).clearChangedProperties();
    
    map.put(PipelineConstants.ORDERREPOSITORYITEM, mutItem);
    if (order == null)
      map.put(PipelineConstants.ORDER, ci);
    
    return SUCCESS;
  }

  //-----------------------------------------------
  /**
   * This method invalidates the item from the cache if invalidateCache is true
   */
  protected void invalidateCache(ItemDescriptorImpl desc, MutableRepositoryItem mutItem) {
    try {
      desc.removeItemFromCache(mutItem.getRepositoryId());
    }
    catch (RepositoryException e) {
      if (isLoggingWarning())
        logWarning("Unable to invalidate item descriptor " + desc.getItemDescriptorName() + ":" + mutItem.getRepositoryId());
    }
  }
}
