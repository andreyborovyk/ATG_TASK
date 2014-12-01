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

import java.util.*;

/**
 * This processor loads the CommerceItem objects from the OrderRepository into the Order object.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcLoadCommerceItemObjects.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.commerce.order.CommerceItem
 */
public class ProcLoadCommerceItemObjects extends LoadProperties implements PipelineProcessor {

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcLoadCommerceItemObjects.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  private static final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcLoadCommerceItemObjects() {
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
  // property: commerceItemsProperty
  //-------------------------------------
  private String mCommerceItemsProperty = "commerceItems";

  /**
   * Returns the commerceItemsProperty name. This is the commerceItems property in the
   * Order repository item.
   */
  public String getCommerceItemsProperty() {
    return mCommerceItemsProperty;
  }

  /**
   * Sets the commerceItemsProperty name. This is the commerceItems property in the
   * Order repository item.
   */
  public void setCommerceItemsProperty(String pCommerceItemsProperty) {
    mCommerceItemsProperty = pCommerceItemsProperty;
  }

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcLoadCommerceItemObjects";

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
   * This method loads the CommerceItem objects from the OrderRepository into the Order object.
   * It does this by constructing a new CommerceItem instance based on the class mapped to
   * the repository item type of the CommerceItem. It then iterates through the properties
   * listed in the loadProperties property inherited by this class, setting the values in the
   * object.
   *
   * This method requires that an Order, order repository item, and OrderManager object
   * be supplied in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order, order repository item, and OrderManager object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    MutableRepositoryItem orderItem = (MutableRepositoryItem) map.get(PipelineConstants.ORDERREPOSITORYITEM);
    OrderManager orderManager = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);
    Boolean invalidateCache = (Boolean) map.get(PipelineConstants.INVALIDATECACHE);

    // check for null parameters
    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (orderItem == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidRepositoryItemParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (orderManager == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderManagerParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (invalidateCache == null)
      invalidateCache = Boolean.FALSE;

    OrderTools orderTools = orderManager.getOrderTools();

    loadCommerceItems(order, order, (List) orderItem.getPropertyValue(getCommerceItemsProperty()), orderManager, orderTools, invalidateCache);

    return SUCCESS;
  }

  //-----------------------------------------------
  /**
   * @deprecated use loadCommerceItems(Order, CommerceItemContainer, List, OrderManager, OrderTools, Boolean)
   */
  protected void loadCommerceItems(Order order, CommerceItemContainer itemContainer, 
                                   List commerceItems, OrderManager orderManager,
                                   OrderTools orderTools) throws Exception
  {
    loadCommerceItems(order, itemContainer, commerceItems, orderManager, orderTools, Boolean.FALSE);
  }

  /**
   * This method is the one that actually goes through the work of
   * building commerce item objects out of the data in the order
   * repository.  It first creates a new instance of the commerce item
   * class (using the type configured in
   * <code>OrderTools.beanNameToItemDescriptorMap</code> Once the
   * object is created, the id is set, then each property in
   * <code>loadProperties</code> is copied into the object (this list
   * is usually empty, since most properties directly read from the
   * repository item).  The item is then added to the given order.  If
   * The item is a CommerceItemContainer
   * (e.g. ConfigurableCommerceItem) then this method is recursively
   * called.
   *
   * @param order The order being created
   * @param itemContainer The container for the commerce items being
   *                      created.  This is usually the same object as "order"
   * @param commerceItems The list of commerceItem repository items
   * @param orderManager The OrderManager passed in with the pipeline params
   * @param orderTools The OrderTools passed in with the pipeline params
   * @param invalidateCache If true, then each commerce item's repository cache
   *                        entry is invalidated
   **/
  protected void loadCommerceItems(Order order, CommerceItemContainer itemContainer, 
                                   List commerceItems, OrderManager orderManager,
                                   OrderTools orderTools, Boolean invalidateCache) throws Exception
  {
    CommerceIdentifier ci;
    MutableRepositoryItem mutItem;
    RepositoryItemDescriptor desc;
    String className, mappedPropName;
    String[] loadProperties = getLoadProperties();
    Object value;

    Iterator iter = commerceItems.iterator();
    while (iter.hasNext()) {
      mutItem = (MutableRepositoryItem) iter.next();
      
      if(mutItem == null) {
        if(isLoggingError()) {
          logError("Null commerceItem in order: " + order.getId());
        }
        continue;
      }
      
      desc = mutItem.getItemDescriptor();

      if (invalidateCache.booleanValue())
        invalidateCache((ItemDescriptorImpl) desc, mutItem);

      className = orderTools.getMappedBeanName(desc.getItemDescriptorName());
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
          OrderRepositoryUtils.setPropertyValue(order, ci, mappedPropName, value);
        }
      }

      if (ci instanceof ChangedProperties)
        ((ChangedProperties) ci).clearChangedProperties();

      if(isLoggingDebug())
        logDebug("loading commerce item " + ci.getClass().getName() + ":" + ci.getId() + "class of container is " + itemContainer.getClass().getName());
      itemContainer.addCommerceItem((CommerceItem) ci);

      if (ci instanceof CommerceItemContainer) {
        if(isLoggingDebug())
          logDebug("This item is a commerce item container. Loading the items in the container. The class of container is " + ci.getClass().getName());
        loadCommerceItems(order, (CommerceItemContainer) ci, (List) mutItem.getPropertyValue(getCommerceItemsProperty()), orderManager, orderTools, invalidateCache);
      }
    } // while
  } // method

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
} // class
