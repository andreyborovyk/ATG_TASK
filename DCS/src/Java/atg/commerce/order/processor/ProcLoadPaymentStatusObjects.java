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
 * This processor loads the PaymentStatus objects from the OrderRepository into the PaymentGroups
 * of the Order object.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcLoadPaymentStatusObjects.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.payment.PaymentStatus
 */
public class ProcLoadPaymentStatusObjects extends LoadProperties implements PipelineProcessor {
  
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcLoadPaymentStatusObjects.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcLoadPaymentStatusObjects() {
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
  // property: authorizationStatusProperty
  //-------------------------------------
  private String mAuthorizationStatusProperty = "authorizationStatus";

  /**
   * Returns the authorizationStatusProperty name. This is the authorizationStatus property in the
   * PaymentGroup repository item.
   */
  public String getAuthorizationStatusProperty() {
    return mAuthorizationStatusProperty;
  }

  /**
   * Sets the authorizationStatusProperty name. This is the authorizationStatus property in the
   * PaymentGroup repository item.
   */
  public void setAuthorizationStatusProperty(String pAuthorizationStatusProperty) {
    mAuthorizationStatusProperty = pAuthorizationStatusProperty;
  }

  //-------------------------------------
  // property: debitStatusProperty
  //-------------------------------------
  private String mDebitStatusProperty = "debitStatus";

  /**
   * Returns the debitStatusProperty name. This is the debitStatus property in the
   * PaymentGroup repository item.
   */
  public String getDebitStatusProperty() {
    return mDebitStatusProperty;
  }

  /**
   * Sets the debitStatusProperty name. This is the debitStatus property in the
   * PaymentGroup repository item.
   */
  public void setDebitStatusProperty(String pDebitStatusProperty) {
    mDebitStatusProperty = pDebitStatusProperty;
  }

  //-------------------------------------
  // property: creditStatusProperty
  //-------------------------------------
  private String mCreditStatusProperty = "creditStatus";

  /**
   * Returns the creditStatusProperty name. This is the creditStatus property in the
   * PaymentGroup repository item.
   */
  public String getCreditStatusProperty() {
    return mCreditStatusProperty;
  }

  /**
   * Sets the creditStatusProperty name. This is the creditStatus property in the
   * PaymentGroup repository item.
   */
  public void setCreditStatusProperty(String pCreditStatusProperty) {
    mCreditStatusProperty = pCreditStatusProperty;
  }

  //-------------------------------------
  // property: paymentGroupsProperty
  //-------------------------------------
  private String mPaymentGroupsProperty = "paymentGroups";

  /**
   * Returns the paymentGroupsProperty name. This is the paymentGroups property in the
   * Order repository item.
   */
  public String getPaymentGroupsProperty() {
    return mPaymentGroupsProperty;
  }

  /**
   * Sets the paymentGroupsProperty name. This is the paymentGroups property in the
   * Order repository item.
   */
  public void setPaymentGroupsProperty(String pPaymentGroupsProperty) {
    mPaymentGroupsProperty = pPaymentGroupsProperty;
  }

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcLoadPaymentStatusObjects";

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
   * This method loads the PaymentStatus objects from the OrderRepository into the PaymentGroups
   * of the Order object. It does this by constructing a new PaymentStatus instance based on the
   * class mapped to the repository item type of the PaymentStatus. It then iterates through the properties
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

    PaymentGroup group;
    MutableRepositoryItem mutItem, pgMutItem;
    List statii, statusList;
    Iterator iter, statIter;
    Object paymentStatus;

    String[] loadProperties = getLoadProperties();
    String[] statusProperties = { getAuthorizationStatusProperty(), getDebitStatusProperty(), getCreditStatusProperty() };
    List paymentGroups = (List) orderItem.getPropertyValue(getPaymentGroupsProperty());
    
    for (int i = 0; i < statusProperties.length; i++) {
      iter = paymentGroups.iterator();

      while (iter.hasNext()) {
        pgMutItem = (MutableRepositoryItem) iter.next();

        if (invalidateCache.booleanValue()) {
          try {
            ItemDescriptorImpl d = (ItemDescriptorImpl) pgMutItem.getItemDescriptor();
            invalidateCache(d, pgMutItem);
          }
          catch (RepositoryException e) {
            if (isLoggingWarning())
              logWarning("Unable to invalidate item descriptor " + pgMutItem.getItemDescriptor().getItemDescriptorName() + ":" + pgMutItem.getRepositoryId());
          }
        }

        group = order.getPaymentGroup(pgMutItem.getRepositoryId());
        statusList = (List) DynamicBeans.getPropertyValue(group, statusProperties[i]);
        statii = (List) pgMutItem.getPropertyValue(statusProperties[i]);
        
        for (statIter = statii.iterator(); statIter.hasNext(); ) {
          mutItem = (MutableRepositoryItem) statIter.next();
          RepositoryItemDescriptor desc = mutItem.getItemDescriptor();

          if (invalidateCache.booleanValue())
            invalidateCache((ItemDescriptorImpl) desc, mutItem);

          paymentStatus = loadStatusProperties(order, mutItem, loadProperties, orderManager, orderTools);
          statusList.add(paymentStatus);
        }
        
        if (group instanceof ChangedProperties)
          ((ChangedProperties) group).clearChangedProperties();
      }
    }
    
    return SUCCESS;
  }
  
  //------------------------------------
  /**
   * This method loads the given list of properties from the repository item into a
   * PaymentStatus object.
   *
   * @param order the Order
   * @param mutItem the paymentStatus repository item to load
   * @param loadProperties the list of property names to load from mutItem
   * @param orderManager an OrderManager instance
   * @param orderTools an OrderTools instance
   * @return the PaymentStatus instance which was constructed
   */
  protected Object loadStatusProperties(Order order,
              MutableRepositoryItem mutItem, String[] loadProperties,
              OrderManager orderManager, OrderTools orderTools) throws Exception
  {
    String mappedPropName;
    Object value;
    
    RepositoryItemDescriptor desc = mutItem.getItemDescriptor();
    String className = orderTools.getMappedBeanName(desc.getItemDescriptorName());
    Object paymentStatus = Class.forName(className).newInstance();
    
    // this is where the properties are loaded from the repository into the order
    for (int i = 0; i < loadProperties.length; i++) {
      mappedPropName = getMappedPropertyName(loadProperties[i]);
      if (! desc.hasProperty(loadProperties[i]))
        continue;

      value = mutItem.getPropertyValue(loadProperties[i]);
      if (isLoggingDebug())
        logDebug("load property[" + loadProperties[i] + ":" + value + ":" 
                    + paymentStatus.getClass().getName() + "]");
      
      OrderRepositoryUtils.setPropertyValue(order, paymentStatus, mappedPropName, value);
    } // for

    return paymentStatus;
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
