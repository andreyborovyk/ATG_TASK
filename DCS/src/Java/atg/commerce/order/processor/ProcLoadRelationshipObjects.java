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

import atg.beans.DynamicBeans;
import atg.commerce.order.ChangedProperties;
import atg.commerce.order.CommerceIdentifier;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.OrderRelationship;
import atg.commerce.order.OrderTools;
import atg.commerce.order.PaymentGroupRelationship;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.Relationship;
import atg.commerce.order.ShippingGroupRelationship;
import atg.core.util.ResourceUtils;
import atg.repository.ItemDescriptorImpl;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This processor loads the Relationship objects from the OrderRepository into the Order object.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcLoadRelationshipObjects.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.commerce.order.ShippingGroup
 */
public class ProcLoadRelationshipObjects extends LoadProperties implements PipelineProcessor {
  
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcLoadRelationshipObjects.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcLoadRelationshipObjects() {
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
  // property: relationshipsProperty
  //-------------------------------------
  private String mRelationshipsProperty = "relationships";

  /**
   * Returns the relationshipsProperty name. This is the relationships property in the
   * Order repository item.
   */
  public String getRelationshipsProperty() {
    return mRelationshipsProperty;
  }

  /**
   * Sets the relationshipsProperty name. This is the relationships property in the
   * Order repository item.
   */
  public void setRelationshipsProperty(String pRelationshipsProperty) {
    mRelationshipsProperty = pRelationshipsProperty;
  }

  //-------------------------------------
  // property: paymentGroupProperty
  //-------------------------------------
  private String mPaymentGroupProperty = "paymentGroup";

  /**
   * Returns the paymentGroupProperty name. This is the paymentGroups property in the
   * Order repository item.
   */
  public String getPaymentGroupProperty() {
    return mPaymentGroupProperty;
  }

  /**
   * Sets the paymentGroupProperty name. This is the paymentGroups property in the
   * Order repository item.
   */
  public void setPaymentGroupProperty(String pPaymentGroupProperty) {
    mPaymentGroupProperty = pPaymentGroupProperty;
  }

  //-------------------------------------
  // property: costCenterProperty
  //-------------------------------------
  private String mCostCenterProperty = "costCenter";

  /**
   * Returns the costCenterProperty name. This is the costCenter property in the
   * Order repository item.
   */
  public String getCostCenterProperty() {
    return mCostCenterProperty;
  }

  /**
   * Sets the costCenterProperty name. This is the costCenter property in the
   * Order repository item.
   */
  public void setCostCenterProperty(String pCostCenterProperty) {
    mCostCenterProperty = pCostCenterProperty;
  }

  //-------------------------------------
  // property: shippingGroupProperty
  //-------------------------------------
  private String mShippingGroupProperty = "shippingGroup";

  /**
   * Returns the shippingGroupProperty name. This is the shippingGroups property in the
   * Order repository item.
   */
  public String getShippingGroupProperty() {
    return mShippingGroupProperty;
  }

  /**
   * Sets the shippingGroupProperty name. This is the shippingGroups property in the
   * Order repository item.
   */
  public void setShippingGroupProperty(String pShippingGroupProperty) {
    mShippingGroupProperty = pShippingGroupProperty;
  }

  //-------------------------------------
  // property: commerceItemProperty
  //-------------------------------------
  private String mCommerceItemProperty = "commerceItem";

  /**
   * Returns the commerceItemProperty name. This is the commerceItems property in the
   * Order repository item.
   */
  public String getCommerceItemProperty() {
    return mCommerceItemProperty;
  }

  /**
   * Sets the commerceItemProperty name. This is the commerceItems property in the
   * Order repository item.
   */
  public void setCommerceItemProperty(String pCommerceItemProperty) {
    mCommerceItemProperty = pCommerceItemProperty;
  }

  //-------------------------------------
  // property: orderProperty
  //-------------------------------------
  private String mOrderProperty = "order";

  /**
   * Returns the orderProperty name. This is the order property in the
   * Order repository.
   */
  public String getOrderProperty() {
    return mOrderProperty;
  }

  /**
   * Sets the orderProperty name. This is the order property in the
   * Order repository.
   */
  public void setOrderProperty(String pOrderProperty) {
    mOrderProperty = pOrderProperty;
  }

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcLoadRelationshipObjects";

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
   * This method loads the Relationship objects from the OrderRepository into the Order object.
   * It does this by constructing a new Relationship instance based on the class mapped to
   * the repository item type of the Relationship. It then iterates through the properties
   * listed in the loadProperties property inherited by this class, setting the values in the
   * object.
   *
   * This method requires that an Order, order repository item, OrderRepository and OrderManager object
   * be supplied in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order, order repository item, OrderRepository
   *               and OrderManager object
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
    Repository rep = (Repository) map.get(PipelineConstants.ORDERREPOSITORY);
    MutableRepositoryItem orderItem = (MutableRepositoryItem) map.get(PipelineConstants.ORDERREPOSITORYITEM);
    OrderManager orderManager = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);
    Boolean invalidateCache = (Boolean) map.get(PipelineConstants.INVALIDATECACHE);
        
    // check for null parameters
    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (rep == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidRepositoryParameter",
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

    MutableRepositoryItem mutItem;
    RepositoryItem valueItem;
    RepositoryItemDescriptor desc;
    CommerceIdentifier ci, ciRef = null;
    Object value;
    String className, mappedPropName;
    String[] loadProperties = getLoadProperties();

    // this is where the properties are loaded from the repository into the Relationship objects
    List relationships = (List) orderItem.getPropertyValue(getRelationshipsProperty());
    Iterator iter = relationships.iterator();
    while (iter.hasNext()) {
      mutItem = (MutableRepositoryItem) iter.next();
      desc = mutItem.getItemDescriptor();

      if (invalidateCache.booleanValue())
        invalidateCache((ItemDescriptorImpl) desc, mutItem);

      className = orderTools.getMappedBeanName(desc.getItemDescriptorName());
      ci = (CommerceIdentifier) Class.forName(className).newInstance();

      DynamicBeans.setPropertyValue(ci, "id", mutItem.getRepositoryId());    
      if (ci instanceof ChangedProperties)
        ((ChangedProperties) ci).setRepositoryItem(mutItem);

      // this is where the properties are loaded from the repository into the order
      for (int i = 0; i < loadProperties.length; i++)
      {
        mappedPropName = getMappedPropertyName(loadProperties[i]);
        if (desc.hasProperty(loadProperties[i])) {
          value = mutItem.getPropertyValue(loadProperties[i]);
          if (isLoggingDebug())
            logDebug("load property[" + loadProperties[i] + ":" + value + ":" 
                        + ci.getClass().getName() + ":" + ci.getId() + "]");
          OrderRepositoryUtils.setPropertyValue(order, ci, mappedPropName, value);
        }
      } // for
      
      if (ci instanceof CommerceItemRelationship)
      {
        value = mutItem.getPropertyValue(getCommerceItemProperty());
        if (value == null) {
          Object [] args = { ci.getId(), order.getId(), getCommerceItemProperty() };
          if(isLoggingError())
            logError(ResourceUtils.getMsgResource("RelationshipReferencesNonExistentItem",
                                                  MY_RESOURCE_NAME, sResourceBundle, args));
          continue;
        }
        ciRef = order.getCommerceItem(((RepositoryItem) value).getRepositoryId());
        DynamicBeans.setPropertyValue(ci, getCommerceItemProperty(), ciRef);
      }
      if (ci instanceof ShippingGroupRelationship)
      {
        value = mutItem.getPropertyValue(getShippingGroupProperty());
        if (value == null) {
          Object [] args = { ci.getId(), order.getId(), getShippingGroupProperty() };
          if(isLoggingError())
            logError(ResourceUtils.getMsgResource("RelationshipReferencesNonExistentItem",
                                                  MY_RESOURCE_NAME, sResourceBundle, args));
          continue;
        }
        ciRef = order.getShippingGroup(((RepositoryItem) value).getRepositoryId());
        DynamicBeans.setPropertyValue(ci, getShippingGroupProperty(), ciRef);
      }
      if (ci instanceof PaymentGroupRelationship)
      {
        value = mutItem.getPropertyValue(getPaymentGroupProperty());
        if (value == null) {
          Object [] args = { ci.getId(), order.getId(), getPaymentGroupProperty() };
          if(isLoggingError())
            logError(ResourceUtils.getMsgResource("RelationshipReferencesNonExistentItem",
                                                  MY_RESOURCE_NAME, sResourceBundle, args));
          continue;
        }
        ciRef = order.getPaymentGroup(((RepositoryItem) value).getRepositoryId());
        DynamicBeans.setPropertyValue(ci, getPaymentGroupProperty(), ciRef);
      }
      if (ci instanceof OrderRelationship)
      {
        DynamicBeans.setPropertyValue(ci, getOrderProperty(), order);
      }
          
      order.addRelationship((Relationship) ci);

      if (ci instanceof ChangedProperties)
        ((ChangedProperties) ci).clearChangedProperties();
    } // while
        
    if (order instanceof ChangedProperties)
      ((ChangedProperties) order).clearChangedProperties();
    
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
