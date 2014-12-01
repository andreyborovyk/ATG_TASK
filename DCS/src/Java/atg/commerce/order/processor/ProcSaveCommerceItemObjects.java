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
 * This processor saves the CommerceItem objects into the OrderRepository from the Order object.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSaveCommerceItemObjects.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.commerce.order.CommerceItem
 */
public class ProcSaveCommerceItemObjects extends SavedProperties implements PipelineProcessor {
  
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSaveCommerceItemObjects.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcSaveCommerceItemObjects() {
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
  String mLoggingIdentifier = "ProcSaveCommerceItemObjects";

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
   * This method saves the CommerceItem objects into the OrderRepository from the Order object.
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
   * @see atg.commerce.order.CommerceItem
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    OrderManager orderManager = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);
    Repository repository = (Repository) map.get(PipelineConstants.ORDERREPOSITORY);
    MutableRepository mutRep;

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
    
    boolean changed = saveCommerceItems(order, order.getCommerceItems(), orderManager, orderTools,
                          mutRep);
    
    if (changed) {
      map.put(PipelineConstants.CHANGED, Boolean.TRUE);
      if (isLoggingDebug())
        logDebug("Set changed flag to true in ProcSaveCommerceItemObjects");
    }
    
    return SUCCESS;
  }

  /**
   * This method iterates across each of the given commerce items and
   * updates the repository.  If the commerce item has never been
   * added to the repository, it will be in this method.  This method will
   * also set the "order" property on the commerceItem repository item.
   *
   * @param order The order being saved
   * @param items The list of CommerceItem objects that will be
   *              saved/added to the repository
   * @param orderManager The OrderManager that was in the pipeline params
   * @param orderTools The OrderTools that was in the pipeline params
   * @param mutRep The repository to which the items are saved
   **/
  protected boolean saveCommerceItems(Order order, List items, OrderManager orderManager,
                      OrderTools orderTools, MutableRepository mutRep) throws Exception
  {
    MutableRepositoryItem mutItem = null;
    Object[] savedProperties;
    Object value;
    String className, mappedPropName, orderItemName, orderDescriptorName;
    boolean changed = false;

    // this is where all the code to store the properties to the repository goes
    Iterator iter = items.iterator();
    for (CommerceItem cItem = null; iter.hasNext(); ) {
      cItem = (CommerceItem) iter.next();

      if (getSaveChangedPropertiesOnly()
          && cItem instanceof ChangedProperties
          && (! ((ChangedProperties) cItem).getSaveAllProperties())) {
        savedProperties =
          ((ChangedProperties) cItem).getChangedProperties().toArray();
      }
      else {
        savedProperties = getSavedProperties();
      }
      
      mutItem = null;
      if (cItem instanceof ChangedProperties)
        mutItem = ((ChangedProperties) cItem).getRepositoryItem();
    
      if (mutItem == null) {
        mutItem = mutRep.getItemForUpdate(cItem.getId(),
            orderTools.getMappedItemDescriptorName(cItem.getClass().getName()));
        if (cItem instanceof ChangedProperties)
          ((ChangedProperties) cItem).setRepositoryItem(mutItem);
      }

      
      for (int i = 0; i < savedProperties.length; i++) {
        mappedPropName = getMappedPropertyName((String) savedProperties[i]);

        if (! OrderRepositoryUtils.hasProperty(order, cItem, mappedPropName))
          continue;

        try {
          value = OrderRepositoryUtils.getPropertyValue(order, cItem, mappedPropName);
        }
        catch (PropertyNotFoundException e) {
          continue; // should never happen because we already checked for existence
        }

        if (isLoggingDebug())
          logDebug("save property[" + (String) savedProperties[i] + ":" + value + ":" +
                                cItem.getClass().getName() + ":" + cItem.getId() + "]");
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
          logDebug("Adding CommerceItem to Repository: " + mutItem.getItemDescriptor().getItemDescriptorName());
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
      
      if (cItem instanceof ChangedProperties) {
        ChangedProperties cp = (ChangedProperties) cItem;      
        if (cp.isChanged())
          changed = true;
        cp.clearChangedProperties();
        cp.setSaveAllProperties(false);
        cp.setChanged(false);
      }

      if (cItem instanceof CommerceItemContainer)
        changed = saveCommerceItems(order, ((CommerceItemContainer) cItem).getCommerceItems(),
                    orderManager, orderTools, mutRep);
    } // for

    return changed;
  }
}

