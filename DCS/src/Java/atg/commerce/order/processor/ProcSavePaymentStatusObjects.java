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
import atg.repository.*;
import atg.commerce.order.*;
import atg.commerce.CommerceException;
import atg.service.pipeline.*;
import atg.core.util.*;
import atg.beans.PropertyNotFoundException;
import atg.payment.PaymentStatus;
import atg.payment.creditcard.CreditCardStatus;
import atg.payment.giftcertificate.GiftCertificateStatus;
import atg.payment.storecredit.StoreCreditStatus;

import java.util.*;

/**
 * This processor saves the PaymentStatus objects into the OrderRepository from the PaymentGroup object.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSavePaymentStatusObjects.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.payment.PaymentStatus
 */
public class ProcSavePaymentStatusObjects extends SavedProperties implements PipelineProcessor {
  
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSavePaymentStatusObjects.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcSavePaymentStatusObjects() {
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
  // property: paymentStatusDescName
  //-------------------------------------
  private String mPaymentStatusDescName = "paymentStatus";

  /**
   * Returns property paymentStatusDescName
   *
   * @return returns property paymentStatusDescName
   */
  public String getPaymentStatusDescName() {
    return mPaymentStatusDescName;
  }

  /**
   * Sets property paymentStatusDescName
   *
   * @param pPaymentStatusDescName the value to set for property paymentStatusDescName
   */
  public void setPaymentStatusDescName(String pPaymentStatusDescName) {
    mPaymentStatusDescName = pPaymentStatusDescName;
  }

  //-------------------------------------
  // property: creditCardStatusDescName
  //-------------------------------------
  private String mCreditCardStatusDescName = "creditCardStatus";

  /**
   * Returns the creditCardStatusDescName
   */
  public String getCreditCardStatusDescName() {
    return mCreditCardStatusDescName;
  }

  /**
   * Sets the creditCardStatusDescName
   */
  public void setCreditCardStatusDescName(String pCreditCardStatusDescName) {
    mCreditCardStatusDescName = pCreditCardStatusDescName;
  }

  //-------------------------------------
  // property: giftCertificateStatusDescName
  //-------------------------------------
  private String mGiftCertificateStatusDescName = "giftCertificateStatus";

  /**
   * Returns the giftCertificateStatusDescName
   */
  public String getGiftCertificateStatusDescName() {
    return mGiftCertificateStatusDescName;
  }

  /**
   * Sets the giftCertificateStatusDescName
   */
  public void setGiftCertificateStatusDescName(String pGiftCertificateStatusDescName) {
    mGiftCertificateStatusDescName = pGiftCertificateStatusDescName;
  }

  //-------------------------------------
  // property: storeCreditStatusDescName
  //-------------------------------------
  private String mStoreCreditStatusDescName = "storeCreditStatus";

  /**
   * Returns the storeCreditStatusDescName
   */
  public String getStoreCreditStatusDescName() {
    return mStoreCreditStatusDescName;
  }

  /**
   * Sets the storeCreditStatusDescName
   */
  public void setStoreCreditStatusDescName(String pStoreCreditStatusDescName) {
    mStoreCreditStatusDescName = pStoreCreditStatusDescName;
  }

  //-------------------------------------
  // property: authorizationStatusProperty
  //-------------------------------------
  private String mAuthorizationStatusProperty = "authorizationStatus";

  /**
   * Returns the authorizationStatusProperty
   */
  public String getAuthorizationStatusProperty() {
    return mAuthorizationStatusProperty;
  }

  /**
   * Sets the authorizationStatusProperty
   */
  public void setAuthorizationStatusProperty(String pAuthorizationStatusProperty) {
    mAuthorizationStatusProperty = pAuthorizationStatusProperty;
  }

  //-------------------------------------
  // property: debitStatusProperty
  //-------------------------------------
  private String mDebitStatusProperty = "debitStatus";

  /**
   * Returns the debitStatusProperty
   */
  public String getDebitStatusProperty() {
    return mDebitStatusProperty;
  }

  /**
   * Sets the debitStatusProperty
   */
  public void setDebitStatusProperty(String pDebitStatusProperty) {
    mDebitStatusProperty = pDebitStatusProperty;
  }

  //-------------------------------------
  // property: creditStatusProperty
  //-------------------------------------
  private String mCreditStatusProperty = "creditStatus";

  /**
   * Returns the creditStatusProperty
   */
  public String getCreditStatusProperty() {
    return mCreditStatusProperty;
  }

  /**
   * Sets the creditStatusProperty
   */
  public void setCreditStatusProperty(String pCreditStatusProperty) {
    mCreditStatusProperty = pCreditStatusProperty;
  }

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcSavePaymentStatusObjects";

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
   * This method saves the PaymentStatus objects into the OrderRepository from the PaymentGroup object.
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
   * @see atg.payment.PaymentStatus
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    Repository repository = (Repository) map.get(PipelineConstants.ORDERREPOSITORY);
    OrderManager orderManager = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);
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
    
    List statList, mutItemList;
    String[] statusProperties = { getAuthorizationStatusProperty(), getDebitStatusProperty(), getCreditStatusProperty() };
    Object paymentStatus;
    MutableRepositoryItem groupMutItem = null, mutItem = null;
    Iterator iter2;
    boolean changed = false;
    
    Iterator iter = order.getPaymentGroups().iterator();
    for (PaymentGroup group = null; iter.hasNext(); ) {
      group = (PaymentGroup) iter.next();
      
      groupMutItem = null;
      if (group instanceof ChangedProperties)
        groupMutItem = ((ChangedProperties) group).getRepositoryItem();
    
      if (groupMutItem == null) {
        groupMutItem = mutRep.getItemForUpdate(group.getId(),
            orderTools.getMappedItemDescriptorName(group.getClass().getName()));
        if (group instanceof ChangedProperties)
          ((ChangedProperties) group).setRepositoryItem(groupMutItem);
      }
      
      for (int i = 0; i < statusProperties.length; i++) {
        mutItemList = (List) groupMutItem.getPropertyValue(statusProperties[i]);
        statList = (List) DynamicBeans.getPropertyValue(group, statusProperties[i]);
        if (statList.size() == 0) {
          mutItemList.clear();
          changed = true;
          continue;
        }
        
        if (ensureList(order, mutItemList, statList, mutRep, orderManager))
          changed = true;

        int j = 0;
        for (iter2 = statList.iterator(); iter2.hasNext(); j++) {
          paymentStatus = iter2.next();
          mutItem = (MutableRepositoryItem) mutItemList.get(j);
          
          if (saveStatusProperties(order, paymentStatus, mutItem, mutRep, orderManager, orderTools))
            changed = true;

          if (! order.isTransient() && mutItem.isTransient()) {
            if (isLoggingDebug())
              logDebug("add to repository[" + mutItem.getItemDescriptor().getItemDescriptorName() + ":" + mutItem.getRepositoryId() + "]");
            mutRep.addItem(mutItem);
            changed = true;
          }
          
          try {
            mutRep.updateItem(mutItem);
          }
          catch (ConcurrentUpdateException e) {
            String[] msgArgs = { order.getId(), mutItem.getItemDescriptor().getItemDescriptorName() };
            throw new CommerceException(ResourceUtils.getMsgResource("ConcurrentUpdateAttempt",
                                            MY_RESOURCE_NAME, sResourceBundle, msgArgs), e);
          }

          if (changed) {
            map.put(PipelineConstants.CHANGED, Boolean.TRUE);
            if (isLoggingDebug())
              logDebug("Set changed flag to true in ProcSavePaymentStatusObjects");
          }
        } // for

      } // for
    } // for
    
    return SUCCESS;
  }
  
  //-----------------------------------------
  protected boolean saveStatusProperties(Order order, Object paymentStatus,
                              MutableRepositoryItem mutItem, MutableRepository mutRep,
                              OrderManager orderManager, OrderTools orderTools)
                              throws Exception
  {
    Object value;
    String mappedPropName;
    Object[] savedProperties = getSavedProperties();
    boolean changed = false;

    for (int i = 0; i < savedProperties.length; i++) {
      mappedPropName = getMappedPropertyName((String) savedProperties[i]);

      if (! OrderRepositoryUtils.hasProperty(order, paymentStatus, mappedPropName))
        continue;

      try {
        value = OrderRepositoryUtils.getPropertyValue(order, paymentStatus, mappedPropName);
      }
      catch (PropertyNotFoundException e) {
        continue; // should never happen because we already checked for existence
      }

      if (isLoggingDebug())
        logDebug("save property[" + (String) savedProperties[i] + ":" + value + ":" +
                              paymentStatus.getClass().getName() + "]");
      OrderRepositoryUtils.saveRepositoryItem(mutRep, mutItem, (String) savedProperties[i], value, orderTools);
      changed = true;
    } //for
    
    return changed;
  }
  
  //-----------------------------------------
  protected boolean ensureList(Order order, List mutItemList, List statList,
                            MutableRepository mutRep, OrderManager orderManager)
            throws CommerceException, RepositoryException
  {
    if (statList.size() == 0 && mutItemList.size() == 0)
      return false;
   
    MutableRepositoryItem mutItem;
    PaymentStatus ps;
    List list;
    String descName;
    OrderTools orderTools = orderManager.getOrderTools();
    HashMap map = new HashMap(8);
    
    Iterator iter = mutItemList.iterator();
    while (iter.hasNext()) {
      mutItem = (MutableRepositoryItem) iter.next();
      descName = mutItem.getItemDescriptor().getItemDescriptorName();
      list = (List) map.get(descName);
      if (list == null) {
        list = new ArrayList(3);
        map.put(descName, list);
      }
      list.add(mutItem);
    }
    
    mutItemList.clear();
    
    for (int i = 0, size = statList.size(); i < size; i++) {
      ps = (PaymentStatus) statList.get(i);
      descName = getPaymentStatusDescriptorName(ps, orderTools);
      list = (List) map.get(descName);

      if (list == null || list.size() == 0) {
        mutItem = mutRep.createItem(descName);
        if (isLoggingDebug())
          logDebug("create in repository[" + mutItem.getItemDescriptor().getItemDescriptorName() + ":" + mutItem.getRepositoryId() + "]");
      }
      else {
        mutItem = (MutableRepositoryItem) list.remove(0);
      }
      
      mutItemList.add(i, mutItem);
    }
    
    return true;
  }

  //-----------------------------------------
  protected String getPaymentStatusDescriptorName(PaymentStatus pPaymentStatus, OrderTools orderTools)
            throws CommerceException
  {
    boolean hasClass = true;

    Class clazz = pPaymentStatus.getClass();
    String descName = orderTools.getBeanNameToItemDescriptorMap().getProperty(clazz.getName());
    while (descName == null) {
      clazz = clazz.getSuperclass();
      if (clazz == null) {
        hasClass = false;
        break;
      }
      descName = orderTools.getBeanNameToItemDescriptorMap().getProperty(clazz.getName());
    }
    if (hasClass == false) {
      descName = getStatusDescriptorName(pPaymentStatus);
      if (descName == null)
        throw new CommerceException(ResourceUtils.getMsgResource("InvalidPaymentStatusClassType",
                                  MY_RESOURCE_NAME, sResourceBundle));
    }

    return descName;
  }
  
  //-----------------------------------------
  /**
   * Return the item descriptor name which refers to the object type.
   * If new status object classes are added this method will need to be
   * overridden to return the item descriptor name for those objects.
   */
  protected String getStatusDescriptorName(PaymentStatus pStatusObject)
              throws CommerceException
  {
    if (pStatusObject instanceof CreditCardStatus)
      return getCreditCardStatusDescName();
    else if (pStatusObject instanceof GiftCertificateStatus)
      return getGiftCertificateStatusDescName();
    else if (pStatusObject instanceof StoreCreditStatus)
      return getStoreCreditStatusDescName();
    else if (pStatusObject instanceof PaymentStatus)
      return getPaymentStatusDescName();
    else 
      return null;
  }    
}
