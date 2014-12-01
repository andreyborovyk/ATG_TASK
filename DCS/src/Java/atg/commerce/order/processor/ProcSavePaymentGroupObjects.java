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
import atg.commerce.CommerceException;
import atg.service.pipeline.*;
import atg.core.util.*;
import atg.beans.*;

import java.util.*;

/**
 * This processor saves the PaymentGroup objects into the OrderRepository from the Order object.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSavePaymentGroupObjects.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.commerce.order.PaymentGroup
 */
public class ProcSavePaymentGroupObjects extends SavedProperties implements PipelineProcessor {
  
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSavePaymentGroupObjects.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcSavePaymentGroupObjects() {
  }

  //-------------------------------------
  // property: orderPropertyDescName
  //-------------------------------------
  private String mOrderPropertyDescName = "order";

  /**
   * Returns property orderPropertyDescName
   *
   * @return returns property orderPropertyDescName
   */
  public String getOrderPropertyDescName() {
    return mOrderPropertyDescName;
  }

  /**
   * Sets property orderPropertyDescName
   *
   * @param pOrderPropertyDescName the value to set for property orderPropertyDescName
   */
  public void setOrderPropertyDescName(String pOrderPropertyDescName) {
    mOrderPropertyDescName = pOrderPropertyDescName;
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
  String mLoggingIdentifier = "ProcSavePaymentGroupObjects";

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
   * This method saves the PaymentGroup objects into the OrderRepository from the Order object.
   * It iterates through the properties listed in the saveProperties property inherited by this
   * class, setting the values in the repository.
   *
   * This method requires that an Order, OrderRepository, and OrderManager object
   * be supplied in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order, OrderRepository, and OrderManager object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   * @see atg.commerce.order.PaymentGroup
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    OrderManager orderManager = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);
    Repository repository = (Repository) map.get(PipelineConstants.ORDERREPOSITORY);

    MutableRepository mutRep;
    MutableRepositoryItem mutItem = null;
    Object value;
    Object[] savedProperties;
    String className, mappedPropName, orderItemName, orderDescriptorName;
    boolean changed = false;
    
    // check for null parameters
    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (repository == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidRepositoryParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (orderManager == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderManagerParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    
    OrderTools orderTools = orderManager.getOrderTools();

    try {
      mutRep = (MutableRepository) repository;
    }
    catch (ClassCastException e) {
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidRepositoryParameter",
                                      MY_RESOURCE_NAME, sResourceBundle), e);
    }
    
    Iterator iter = order.getPaymentGroups().iterator();
    for (PaymentGroup group = null; iter.hasNext(); ) {
      group = (PaymentGroup) iter.next();

      if (getSaveChangedPropertiesOnly()
          && group instanceof ChangedProperties
          && (! ((ChangedProperties) group).getSaveAllProperties())) {
	savedProperties =
          ((ChangedProperties) group).getChangedProperties().toArray();
      }
      else {
	savedProperties = getSavedProperties();
      }
      
      mutItem = null;
      if (group instanceof ChangedProperties) {
        mutItem = ((ChangedProperties) group).getRepositoryItem();
      }

      if (mutItem == null) {
	mutItem = mutRep.getItemForUpdate(group.getId(),
            orderTools.getMappedItemDescriptorName(group.getClass().getName()));
        if (group instanceof ChangedProperties)
          ((ChangedProperties) group).setRepositoryItem(mutItem);
      }
      
      
      for (int i = 0; i < savedProperties.length; i++) {
        mappedPropName = getMappedPropertyName((String) savedProperties[i]);

        if (! OrderRepositoryUtils.hasProperty(order, group, mappedPropName))
          continue;

        try {
          value = OrderRepositoryUtils.getPropertyValue(order, group, mappedPropName);
        }
        catch (PropertyNotFoundException e) {
          continue; // should never happen because we already checked for existence
        }

        if (isLoggingDebug())
          logDebug("save property[" + (String) savedProperties[i] + ":" + value + ":" +
                                group.getClass().getName() + ":" + group.getId() + "]");
        OrderRepositoryUtils.saveRepositoryItem(mutRep, mutItem, (String) savedProperties[i], value, orderTools);
        changed = true;
      } // for

      orderItemName = getOrderPropertyDescName();
      Object ordervalue = mutItem.getPropertyValue(orderItemName);
      RepositoryItem repItem = (RepositoryItem)ordervalue;
      
      if (ordervalue == null  ||
          (!order.getId().equals(repItem.getRepositoryId())))
      {    	
        className = order.getClass().getName();
        orderDescriptorName = orderTools.getMappedItemDescriptorName(className);
        mutItem.setPropertyValue(orderItemName, mutRep.getItem(order.getId(), orderDescriptorName));
        changed = true;
      }

      if ((! order.isTransient()) && mutItem.isTransient()) {
      	if (isLoggingDebug())
          logDebug("Adding PaymentGroup to Repository: " + mutItem.getItemDescriptor().getItemDescriptorName());
        mutRep.addItem(mutItem);
      }
      
      try {
        mutRep.updateItem(mutItem);
      }
      catch (ConcurrentUpdateException e) {
        String[] msgArgs = { order.getId(), mutItem.getItemDescriptor().getItemDescriptorName() };
        throw new CommerceException(ResourceUtils.getMsgResource("ConcurrentUpdateAttempt",
                                        MY_RESOURCE_NAME, sResourceBundle, msgArgs), e);
      }
      
      if (group instanceof ChangedProperties) {
        ChangedProperties cp = (ChangedProperties) group;
        if (cp.isChanged())
          changed = true;
        cp.clearChangedProperties();
        cp.setSaveAllProperties(false);
        cp.setChanged(false);
      }
    } // for
    
    if (changed) {
      map.put(PipelineConstants.CHANGED, Boolean.TRUE);
      if (isLoggingDebug())
        logDebug("Set changed flag to true in ProcSavePaymentGroupObjects");
    }
    
    return SUCCESS;
  }
}
