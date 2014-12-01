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
import atg.core.util.*;
import atg.beans.*;

import java.beans.IntrospectionException;
import java.util.*;

/**
 * This processor loads the ShippingGroup objects from the OrderRepository into the Order object.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcLoadShippingGroupObjects.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.commerce.order.ShippingGroup
 */
public class ProcLoadShippingGroupObjects extends LoadProperties implements PipelineProcessor {

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcLoadShippingGroupObjects.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcLoadShippingGroupObjects() {
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
  // property: shippingGroupsProperty
  //-------------------------------------
  private String mShippingGroupsProperty = "shippingGroups";

  /**
   * Returns the shippingGroupsProperty name. This is the shippingGroups property in the
   * Order repository item.
   */
  public String getShippingGroupsProperty() {
    return mShippingGroupsProperty;
  }

  /**
   * Sets the shippingGroupsProperty name. This is the shippingGroups property in the
   * Order repository item.
   */
  public void setShippingGroupsProperty(String pShippingGroupsProperty) {
    mShippingGroupsProperty = pShippingGroupsProperty;
  }


  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcLoadShippingGroupObjects";

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
   * This method loads the ShippingGroup objects from the OrderRepository into the Order object.
   * It does this by constructing a new ShippingGroup instance based on the class mapped to
   * the repository item type of the ShippingGroup. It then iterates through the properties
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
   * @see atg.commerce.order.ShippingGroup
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

    CommerceIdentifier ci;
    MutableRepositoryItem mutItem;
    RepositoryItemDescriptor desc;
    String className, mappedPropName;
    String[] loadProperties = getLoadProperties();
    Object value;

    List shippingGroups = (List) orderItem.getPropertyValue(getShippingGroupsProperty());
    Iterator iter = shippingGroups.iterator();
    while (iter.hasNext()) {
      mutItem = (MutableRepositoryItem) iter.next();

      // PR 130329-1.  We're not sure how it happens, but sometimes mutItem comes up null.
      // To prevent an ugly NPE, we'll log an error here and continue.
      if (mutItem == null) {
        if (isLoggingError())
	  logError("Null item in shipping groups list of order: " + orderItem.getRepositoryId());
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

      try {
        if (ci instanceof ShippingAddressContainer) {
          String shippingClassType = (String) DynamicBeans.getPropertyValue(ci,"shippingGroupClassType");
          String shippingAddressClassName = orderTools.getShippingGroupShippingAddressClassNameMap().getProperty(shippingClassType);
          if (shippingAddressClassName == null)
            shippingAddressClassName = orderTools.getDefaultShippingGroupAddressType();
          Address address = (Address) Class.forName(shippingAddressClassName).newInstance();
          DynamicBeans.setPropertyValue(address, "repositoryItem", mutItem);
          DynamicBeans.setPropertyValue(ci, "shippingAddress", address);
        }
        else if (DynamicBeans.getBeanInfo(ci).hasProperty("shippingAddress")) {
          Address address = (Address) Class.forName(orderTools.getDefaultShippingGroupAddressType()).newInstance();
          DynamicBeans.setPropertyValue(address, "repositoryItem", mutItem);
          DynamicBeans.setPropertyValue(ci, "shippingAddress", address);
        }
      } catch (IntrospectionException e) {
        throw new ObjectCreationException(ResourceUtils.getMsgResource("UnableToCreateShippingGroupObject",
                                                  MY_RESOURCE_NAME, sResourceBundle), e);
      } catch (PropertyNotFoundException e) {
        throw new ObjectCreationException(ResourceUtils.getMsgResource("UnableToCreateShippingGroupObject",
                                                MY_RESOURCE_NAME, sResourceBundle), e);
      } catch (ClassNotFoundException e) {
        throw new ObjectCreationException(ResourceUtils.getMsgResource("UnableToCreateShippingGroupObject",
                                                MY_RESOURCE_NAME, sResourceBundle), e);
      } catch (InstantiationException e) {
        throw new ObjectCreationException(ResourceUtils.getMsgResource("UnableToCreateShippingGroupObject",
                                                MY_RESOURCE_NAME, sResourceBundle), e);
      } catch (IllegalAccessException e) {
        throw new ObjectCreationException(ResourceUtils.getMsgResource("UnableToCreateShippingGroupObject",
                                                MY_RESOURCE_NAME, sResourceBundle), e);
      }

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

      order.addShippingGroup((ShippingGroup) ci);
    }

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
