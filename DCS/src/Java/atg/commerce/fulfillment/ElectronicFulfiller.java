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

package atg.commerce.fulfillment;

// Commerce classes
import atg.commerce.*;
import atg.commerce.order.*;
import atg.commerce.states.*;
import atg.commerce.claimable.ClaimableManager;
import atg.commerce.messaging.*;
import atg.service.pipeline.*;

// Dynamo classes
import atg.dms.patchbay.*;
import atg.nucleus.*;
import atg.dtm.*;
import atg.repository.*;
import atg.service.email.*;
import atg.core.util.ResourceUtils;
import atg.userprofiling.email.*;

// Java classes
import javax.jms.*;
import java.text.MessageFormat;
import javax.transaction.*;
import javax.transaction.xa.*;
import java.util.*;

import java.io.Serializable;
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;

/**
 *
 * This fulfiller is used to fulfill shipping groups that contain electronic goods.
 * In the out of the box implementation this means gift certificates.  The two messages that
 * are supported are
 *
 * <P>
 *
 * <UL>
 *    <LI>FOF - FulfillOrderFragment messages can be received and then will get routed to
 *        the handleFulfillOrderFragment method.  This will essentially try to
 *        create the electronic good and then deliver the electronic good.  
 *    <LI>MON - If a modify order notification message with a shipping group update message
 *        is received, the specified shipping groups will attempt to be reprocessed.
 * </UL>
 *
 * @author Ashley J. Streb
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/ElectronicFulfiller.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see FulfillOrderFragment
 * @see ModifyOrder
 * @see ModifyOrderNotification
 */
public class ElectronicFulfiller extends FulfillerSystem
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static final String CLASS_VERSION =  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/ElectronicFulfiller.java#2 $$Change: 651448 $";
      
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------
  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";
  
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  /** Name of template email parameter containing the profile bean of the purchaser **/
  public static final String PURCHASER_PARAM_NAME = "purchaser";

  /** Name of template email parameter containing the gift certificate bean **/
  public static final String GIFT_CERTIFICATE_PARAM_NAME = "giftCertificate";

  /** Name of template email parameter containing the quantity **/
  public static final String QUANTITY_PARAM_NAME = "quantity";

  /** Name of template email parameter containing the quantity **/
  public static final String RECIPIENT_EMAIL_PARAM_NAME = "recipient";

  /** Name of template email parameter containing the quantity **/
  public static final String DEFAULT_GIFT_CERTIFICATE_EMAIL_TEMPLATE = "/Dynamo/commerce/email_templates/GiftCertificate.jhtml";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------
  
  //--------------------------------------------------
  // Properties
  //--------------------------------------------------


  //---------------------------------------------------------------------------
  // property:ProfileRepository
  //---------------------------------------------------------------------------

  private Repository mProfileRepository;
  public void setProfileRepository(Repository pProfileRepository) {
    mProfileRepository = pProfileRepository;
  }

  /**
   * The profile repository used to retrieve profile information.
   **/
  public Repository getProfileRepository() {
    return mProfileRepository;
  }

  //---------------------------------------------------------------------------
  // property:DefaultProfileType
  //---------------------------------------------------------------------------

  private String mDefaultProfileType;
  public void setDefaultProfileType(String pDefaultProfileType) {
    mDefaultProfileType = pDefaultProfileType;
  }

  /**
   * The default profile repository view that is used in retrieving profiles.
   **/
  public String getDefaultProfileType() {
    return mDefaultProfileType;
  }
   
  //---------------------------------------------------------------------------
  // property:UseTemplateEmailSender
  //---------------------------------------------------------------------------

  private boolean mUseTemplateEmailSender = true;
  public void setUseTemplateEmailSender(boolean pUseTemplateEmailSender) {
    mUseTemplateEmailSender = pUseTemplateEmailSender;
  }

  /**
   * If true, is the templateEmailSender, otherwise just use the EmailListener
   * default is true
   **/
  public boolean isUseTemplateEmailSender() {
    return mUseTemplateEmailSender;
  }

  //---------------------------------------------------------------------------
  // property:TemplateEmailSender
  //---------------------------------------------------------------------------

  private TemplateEmailSender mTemplateEmailSender;
  public void setTemplateEmailSender(TemplateEmailSender pTemplateEmailSender) {
    mTemplateEmailSender = pTemplateEmailSender;
  }

  /**
   * The TemplateEmailSender to use for sending email to a
   * single recipient.
   **/
  public TemplateEmailSender getTemplateEmailSender() {
    return mTemplateEmailSender;
  }


  //---------------------------------------------------------------------------
  // property:SeparateEmailThread
  //---------------------------------------------------------------------------

  private boolean mSeparateEmailThread = true;
  public void setSeparateEmailThread(boolean pSeparateEmailThread) {
    mSeparateEmailThread = pSeparateEmailThread;
  }

  /**
   * If true, email will be send through a separate email thread. (true by default)
   **/
  public boolean isSeparateEmailThread() {
    return mSeparateEmailThread;
  }


  //---------------------------------------------------------------------------
  // property:PersistEmails
  //---------------------------------------------------------------------------

  private boolean mPersistEmails = false;
  public void setPersistEmails(boolean pPersistEmails) {
    mPersistEmails = pPersistEmails;
  }

  /**
   * If true, emails will be persisted. (false by default)
   * Note: Currently, the TemplateEmailSender does not support
   * email persistence unless the recipient is a profile.
   * ElectronicFulfiller sends directly to an email address.
   * Therefore, until support is added to the TemplateEmailSender,
   * setting this flag to <code>true</code> will have no effect.
   **/
  public boolean isPersistEmails() {
    return mPersistEmails;
  }

  
  //---------------------------------------------------------------------------
  // property:DefaultTemplateEmailInfo
  //---------------------------------------------------------------------------
  private TemplateEmailInfo mDefaultTemplateEmailInfo ;
  public void setDefaultTemplateEmailInfo(TemplateEmailInfo pDefaultTemplateEmailInfo) {
    mDefaultTemplateEmailInfo = pDefaultTemplateEmailInfo;
  }

  /**
   * The default information used for templated email.  This property is ignore if 
   * <code useTemplateEmailSender</code> is false.
   **/
  public TemplateEmailInfo getDefaultTemplateEmailInfo() {
    return mDefaultTemplateEmailInfo;
  }


  //---------------------------------------------------------------------------
  // property:GiftCertificateEmailTemplate
  //---------------------------------------------------------------------------

  private String mGiftCertificateEmailTemplate = DEFAULT_GIFT_CERTIFICATE_EMAIL_TEMPLATE;
  public void setGiftCertificateEmailTemplate(String pGiftCertificateEmailTemplate) {
    mGiftCertificateEmailTemplate = pGiftCertificateEmailTemplate;
  }

  /**
   * The url to the email template used for gift certificate emails.  This property is ignore if 
   * <code useTemplateEmailSender</code> is false.
   **/
  public String getGiftCertificateEmailTemplate() {
    return mGiftCertificateEmailTemplate;
  }


  //---------------------------------------------------------------------------
  // property:PurchaserParamName
  //---------------------------------------------------------------------------

  private String mPurchaserParamName = PURCHASER_PARAM_NAME;
  public void setPurchaserParamName(String pPurchaserParamName) {
    mPurchaserParamName = pPurchaserParamName;
  }

  /**
   * The name of the parameter passed into the email template for the purchaser
   * Defaults to "purchaser"
   **/
  public String getPurchaserParamName() {
    return mPurchaserParamName;
  }

  
  //---------------------------------------------------------------------------
  // property:GiftCertificateParamName
  //---------------------------------------------------------------------------

  private String mGiftCertificateParamName = GIFT_CERTIFICATE_PARAM_NAME;
  public void setGiftCertificateParamName(String pGiftCertificateParamName) {
    mGiftCertificateParamName = pGiftCertificateParamName;
  }

  /**
   * The name of the parameter passed into the email template for the gift certificate
   * Defaults to "giftCertificate"
   **/
  public String getGiftCertificateParamName() {
    return mGiftCertificateParamName;
  }


  //---------------------------------------------------------------------------
  // property:QuantityParamName
  //---------------------------------------------------------------------------

  private String mQuantityParamName = QUANTITY_PARAM_NAME;
  public void setQuantityParamName(String pQuantityParamName) {
    mQuantityParamName = pQuantityParamName;
  }

  /**
   * The name of the parameter passed into the email template for the quantity
   * Defaults to "quantity"
   **/
  public String getQuantityParamName() {
    return mQuantityParamName;
  }

  

  //---------------------------------------------------------------------------
  // property:RecipientEmailParamName
  //---------------------------------------------------------------------------

  private String mRecipientEmailParamName = RECIPIENT_EMAIL_PARAM_NAME;
  public void setRecipientEmailParamName(String pRecipientEmailParamName) {
    mRecipientEmailParamName = pRecipientEmailParamName;
  }

  /**
   * The name of the parameter passed into the email template for the email address of the recipient.
   * Defaults to "recipient"
   **/
  public String getRecipientEmailParamName() {
    return mRecipientEmailParamName;
  }

  //*****************************************************************************
  // <TBD>                                                                      *
  // defaultFromAddress, defaultSubject, defaultMessageBody, and emailListeners *
  // should all be deprecated                                                   *
  //*****************************************************************************
  
  //---------------------------------------------------------------------------
  // property: DefaultFromAddress
  String mDefaultFromAddress;

  /**
   * Set the DefaultFromAddress property.  This property is ignore if 
   * <code useTemplateEmailSender</code> is true.
   */
  public void setDefaultFromAddress(String pDefaultFromAddress) {
    mDefaultFromAddress = pDefaultFromAddress;
  }

  /**
   * Return the DefaultFromAddress property.  This property is ignore if 
   * <code useTemplateEmailSender</code> is true.
   */
  public String getDefaultFromAddress() {
    return mDefaultFromAddress;
  }

  
  //---------------------------------------------------------------------------
  // property: DefaultSubject
  String mDefaultSubject;

  /**
   * Set the DefaultSubject property.  This property is ignore if 
   * <code useTemplateEmailSender</code> is true.
   */
  public void setDefaultSubject(String pDefaultSubject) {
    mDefaultSubject = pDefaultSubject;
  }

  /**
   * Return the DefaultSubject property.  This property is ignore if 
   * <code useTemplateEmailSender</code> is true.
   */
  public String getDefaultSubject() {
    return mDefaultSubject;
  }

  
  //---------------------------------------------------------------------------
  // property: DefaultMessageBody
  String mDefaultMessageBody;

  /**
   * Set the DefaultMessageBody property.  This property is ignore if 
   * <code useTemplateEmailSender</code> is true.
   */
  public void setDefaultMessageBody(String pDefaultMessageBody) {
    mDefaultMessageBody = pDefaultMessageBody;
  }

  /**
   * Return the DefaultMessageBody property.  This property is ignore if 
   * <code useTemplateEmailSender</code> is true.
   */
  public String getDefaultMessageBody() {
    return mDefaultMessageBody;
  }

  
  //---------------------------------------------------------------------------
  // property: EmailListeners
  List mEmailListeners = new ArrayList(1);
  
  /**
   * The list of email listeners to whom this fulfiller will attempt delivery
   * of the specific electronic good.  This will add the pEmailListener to the list.
   *
   * @param pEmailListener the email listener to add
   */
  public void addEmailListener(EmailListener pEmailListener) {
    mEmailListeners.add(pEmailListener);
  }
  
  /**
   * Removes the email listener from the list of listeners that will attempt
   * to deliver the electronic good on.
   *
   * @param pEmailListener the email listener to remove from the list
   */
  public void removeEmailListener(EmailListener pEmailListener) {
    mEmailListeners.remove(pEmailListener);
  }
  
  /**
   * Return the lis of email listeners that will attempt delivery.
   *
   * @return the list of email listeners
   */
  public List getEmailListeners() {
    return mEmailListeners;
  }

  //---------------------------------------------------------------------------
  // property: ClaimableManager
  ClaimableManager mClaimableManager;

  /**
   * Set the ClaimableManager property.
   */
  public void setClaimableManager(ClaimableManager pClaimableManager) {
    mClaimableManager = pClaimableManager;
  }

  /**
   * Return the ClaimableManager property.
   */
  public ClaimableManager getClaimableManager() {
    return mClaimableManager;
  }

  
  //---------------------------------------------------------------------------
  // property:GiftCertificateAmountProperty
  //---------------------------------------------------------------------------

  private String mGiftCertificateAmountProperty = "listPrice";
  public void setGiftCertificateAmountProperty(String pGiftCertificateAmountProperty) {
    mGiftCertificateAmountProperty = pGiftCertificateAmountProperty;
  }

  /**
   * The name of the property in the sku to be used for the gift
   * certificate amount.  Defaults to "listPrice"
   **/
  public String getGiftCertificateAmountProperty() {
    return mGiftCertificateAmountProperty;
  }


  /**
   * The map of the chains to run to execute pipeline
   **/
  private Properties mChainToRunMap;
  
  public Properties getChainToRunMap() {
      return mChainToRunMap;
  }

  public void setChainToRunMap(Properties pChainToRunMap) {
      mChainToRunMap = pChainToRunMap;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  /**
   * <p> This is called to handle a newly received message. Before this method is called,
   * the message is subjected to basic validity checks, a transaction is established, and
   * an exclusive lock is acquired for the message's key. </p>
   * <p> ElectronicFulfiller handles the following types of messages:
   * <ul>
   *   <li>FulfillOrderFragment - handleFulfillOrderFragment - This is 
   *                                   sent to the  fulfiller with a
   *                                   list of all the shipping groups that this 
   *                                   fulfiller is responsible for. 
   *
   *   <li>ModifyOrder - this will call the handleModifyOrder method
   *   <li>ModifyOrderNotification - this will call the handleModifyOrderNotification method
   *   <li>If an unknown message type comes in, handleNewMessageType is called.</li>
   * </ul>
   *
   * <p> ElectronicFulfiller can be extended to handle extra types of messages by overriding
   * the handleNewMessageType method. </p>
   *
   * @beaninfo
   *          description: This method is called when a new message has arrived
   *                       for the ElectronicFulfiller.
   * @param pPortName The port that this message was received from.
   * @param pMessage The message that was just received.
   * @exception javax.jms.JMSException
   * @see FulfillOrderFragment
   * @see #handleFulfillOrderFragment
   * @see ModifyOrder
   * @see #handleModifyOrder
   * @see ModifyOrderNotification
   * @see #handleModifyOrderNotification
   * @see FulfillerSystem#receive
   * @see FulfillerSystem#handleNewMessageType
   */
  protected boolean handleMessage (String pPortName, ObjectMessage pMessage)
    throws JMSException
  {
    String messageType = pMessage.getJMSType();
    boolean handled = true;

    if (messageType.equals(FulfillOrderFragment.TYPE)) {
      handleFulfillOrderFragment(pPortName, pMessage);
    }
    else if (messageType.equals(ModifyOrder.TYPE)) {
      handleModifyOrder(pPortName, pMessage);
    }
    else if (messageType.equals(ModifyOrderNotification.TYPE)) {
      handleModifyOrderNotification(pPortName, pMessage);
    }
    else {
      handled = false;
    }
    return handled;
  }

  /**
   * <p> This method is called to handle all messages of type
   * ModifyOrder.  Should developers wish to change the behavior of
   * the ElectronicFulfiller class on handling a ModifyOrder message
   * this method should be overridden. This method calls various
   * methods to respond to the ModifyOrder requests. Modifications on
   * orders are ignored since the ElectronicFulfiller does not have the
   * authority to edit orders.
   *
   * <P>
   *
   * Currently, all Modifications will be marked as failed with a
   * MODIFICATION_NOT_SUPPORTED message and then sent out.
   *  
   * @param pPortName The port from which this message was received.
   * @param pMessage The message that was received.
   * @exception javax.jms.JMSException
   * @see ModifyOrder
   * @see IdTargetModification#getTargetType
   * @see Modification
   **/
  public void handleModifyOrder(String pPortName,
                                ObjectMessage pMessage)
    throws JMSException
  {
      if (isLoggingDebug())
          logDebug("Handling a modifyOrder message in ElectricFulfiller");

      // the input params to the chain
      HashMap map = new HashMap(10);
      map.put(PipelineConstants.MESSAGE, pMessage);
      map.put(PipelineConstants.ORDERFULFILLER, this);
      
      try {
          String chainToRun = (String)getChainToRunMap().get("modifyOrderChain");
          // execute the submitOrder pipeline chain
          PipelineResult results = getFulfillmentPipelineManager().runProcess(chainToRun, map);
      }
      catch (RunProcessException e) {
          Throwable p = e.getSourceException();
          if(isLoggingError())
              logError(p);
          // check the thrown exceptions
          if (p instanceof CommerceException) {
              try {
                  getTransactionManager().getTransaction().setRollbackOnly();
              }
              catch (javax.transaction.SystemException se) {
                  // Hopefully this will never happen.
                  if(isLoggingError())
                      logError(se);
              }
          }
          if (p instanceof JMSException) {
              throw (JMSException)p;
          }
      }
  }



  /**
   * <p> This method is called to handle all messages of type 
   * ModifyOrderNotification.  Should developers wish to 
   * change the behavior of the ElectronicFulfiller class on handling a
   * ModifyOrderNotification message this method should be
   * overridden. Currently only one modification type is handled by
   * this method. If the type of modification passed in the Modify Order
   * Notification message is a shipping group update, processMultipleShippingGroups
   * method will be called with the list of shipping group ids from the
   * shipping group update.
   *
   * @param pPortName The port from which this message was received.
   * @param pMessage The message that was received.
   * @exception javax.jms.JMSException
   * @see ModifyOrderNotification
   * @see ShippingGroupUpdate
   **/
  public void handleModifyOrderNotification(String pPortName,
                                            ObjectMessage pMessage)
    throws JMSException
  {
      if (isLoggingDebug())
          logDebug("Handling a modifyOrderNotification message in ElectricFulfiller");

      // the input params to the chain
      HashMap map = new HashMap(10);
      map.put(PipelineConstants.MESSAGE, pMessage);
      map.put(PipelineConstants.ORDERFULFILLER, this);
      
      try {
          String chainToRun = (String)getChainToRunMap().get("modifyOrderNotificationChain");
          // execute the submitOrder pipeline chain
          PipelineResult results = 
              getFulfillmentPipelineManager().runProcess(chainToRun, map);
      }
      catch (RunProcessException e) {
          Throwable p = e.getSourceException();
          if(isLoggingError())
              logError(p);
          // check the thrown exceptions
          if (p instanceof CommerceException) {
              try {
                  getTransactionManager().getTransaction().setRollbackOnly();
              }
              catch (javax.transaction.SystemException se) {
                  // Hopefully this will never happen.
                  if(isLoggingError())
                      logError(se);
              }
          }
          if (p instanceof JMSException) {
              throw (JMSException)p;
          }
      }
  }




  /**
   * <p> This method is called to handle all messages of type
   * FulfillOrderFragment.  Should developers wish to change the
   * behavior of the ElectronicFulfiller class in handling a
   * FulfillOrderFragment message this method should be overriden. It
   * first saves the order with updateOrder, then calls 
   * processShippingGroup for each shipping group in the message.
   * When processing has finished, it calls updateOrder and sends
   * all the changes that were made using OrderFulfillmentTools.sendModifyOrderNotification
   *
   * @beaninfo
   *          description: This method is called to process a newly arrived FulfillOrderFragment message.
   * @param pPortName The port from which the FulfillOrderFragment was received.
   * @param pMessage The message that was received.
   * @exception javax.jms.JMSException
   * @see FulfillOrderFragment
   * @see #updateOrder
   * @see #processShippingGroup
   * @see OrderFulfillmentTools#sendModifyOrderNotification
   **/
  public void handleFulfillOrderFragment(String pPortName,
                                         ObjectMessage pMessage)
    throws JMSException
  {
      if (isLoggingDebug())
          logDebug("Handling a fulfillOrderFragment message");
      
      // the input params to the chain
      HashMap map = new HashMap(10);
      map.put(PipelineConstants.MESSAGE, pMessage);
      map.put(PipelineConstants.ORDERFULFILLER, this);
      
      try {
          String chainToRun = (String)getChainToRunMap().get("fulfillOrderFragmentChain");
          // execute the submitOrder pipeline chain
          PipelineResult results = 
              getFulfillmentPipelineManager().runProcess(chainToRun, map);
      }
      catch (RunProcessException e) {
          Throwable p = e.getSourceException();
          if(isLoggingError())
              logError(p);
          // check the thrown exceptions
          if (p instanceof CommerceException) {
              try {
                  getTransactionManager().getTransaction().setRollbackOnly();
              }
              catch (javax.transaction.SystemException se) {
                  // Hopefully this will never happen.
                  if(isLoggingError())
                      logError(se);
              }
          }
          if (p instanceof JMSException) {
              throw (JMSException)p;
          }
      }
  }

  /**
   * This method will return the key to be used for locking out other messages with the same
   * key while a thread is handling this message.
   *
   * @beaninfo
   *          description: This method will return the key to be used for locking 
   *                       out other messages with the same key while a thread 
   *                       is handling this message.
   * @param pMessage the ObjectMessage containing the CommerceMessage.
   * @return an Object which serves as the key for the message
   * @exception javax.jms.JMSException
   **/
  public Serializable getKeyForMessage(ObjectMessage oMessage)
    throws JMSException
  {
    
    String messageType = oMessage.getJMSType();
    CommerceMessage pMessage = (CommerceMessage) oMessage.getObject();
    
    if (messageType.equals(FulfillOrderFragment.TYPE)) {
      return getOrderIdFromMessage((FulfillOrderFragment)pMessage);
    }
    else if (messageType.equals(ModifyOrder.TYPE)) {
      return ((ModifyOrder) pMessage).getOrderId();
    }
    else if (messageType.equals(ModifyOrderNotification.TYPE)) {
      return ((ModifyOrderNotification) pMessage).getOrderId();
    }
    return null;
  }


  /**
   * This is called only for the FulfillOrderFragment messages
   * @return an Object which serves as the key for the message
   **/
  protected Serializable getOrderIdFromMessage(FulfillOrderFragment cMessage) {
      // see if we get it from the message itself
      if (getLookUpOrderIdFromMessage()) {
          if(isLoggingDebug())
              logDebug("Reading the orderId from the FulfillOrderFragment message");
          String id = cMessage.getOrderId();
          if (id == null) {
              if (getLookUpOrderIdFromOrder()) { 
                  if(isLoggingDebug())
                      logDebug("Reading the orderId from the order object sent in the " + 
                               "FulfillOrderFragment message");
                  if (cMessage.getOrder() == null) {
                      if (isLoggingError())
                          logError(Constants.ORDER_IS_NULL);
                      return null;
                  }
                  return cMessage.getOrder().getId();
              }
          }
          return id;
      }
      else { // get it from the order object sent in the message
          if (getLookUpOrderIdFromOrder()) {
              if(isLoggingDebug())
                  logDebug("Reading the orderId from the order object sent in the " + 
                           "FulfillOrderFragment message");
              if (cMessage.getOrder() == null) {
                  if (isLoggingError())
                      logError(Constants.ORDER_IS_NULL);
                  return null;
              }
              return cMessage.getOrder().getId();
          }
          else
              return null;
      }
  }


  /**
   * This method will process each shipping group in the given array.
   * This iterating through all shipping groups and calling processShippingGroup.
   *
   *
   * @beaninfo
   *          description: This method will process each shipping group in the given array.
   * @param pOrder The order containing the shipping groups.
   * @param pShippingGroupIds The array of shipping group ids to process.
   * @param pPerformedModifications A place to store all new modifications.
   * @return true if everything processed fine, false otherwise.
   **/
  public boolean processMultipleShippingGroups(Order pOrder,
                                               String[] pShippingGroupIds,
                                               List pPerformedModifications)
      throws CommerceException
  {
      
      HashMap pMap = new HashMap(5);
      pMap.put(PipelineConstants.ORDERFULFILLER, this);
      pMap.put(PipelineConstants.ORDER, pOrder);
      pMap.put(PipelineConstants.SHIPPINGGROUPIDS, pShippingGroupIds);
      pMap.put(PipelineConstants.MODIFICATIONLIST, pPerformedModifications);
      
      try {
          String chainToRun = (String)getChainToRunMap().get("processMultipleShippingGroupsChain");
          PipelineResult result = getFulfillmentPipelineManager().runProcess(chainToRun, pMap);
      }
      catch(RunProcessException e) {
          Throwable p = e.getSourceException();
          if(isLoggingError())
              logError(p);
          
          if (p instanceof CommerceException)
              throw (CommerceException)p;

          return false;
      }

      return true;      

  }


  /**
   * This method is called to process each shipping group.  It will iterate through all
   * of the commerce item relationships associated with each shipping group, and check
   * the state of each of these relationships.  If the state is not set to DELIVERED,
   * it will attempt to do two things.
   *
   * <P>
   *
   * <UL>
   *    <LI>Extract out the profile id from the order, the amount of the electronic good
   *        and then call createElectronicGood.
   *    <LI>Deliver the electronic good via the deliverElectronicGood method
   * </UL>
   *
   * <P>
   *
   * After this is done, the state of the ShippingGroupCommerceItem relationship will be
   * set to DELIVERED.
   *
   * @param pOrder a value of type 'Order'
   * @param pShippingGroup a value of type 'ShippingGroup'
   * @param pModificationList a value of type 'List'
   * @return a value of type 'boolean'
   *
   * @deprecated Replaced by the pipeline processor
   */
  protected boolean processShippingGroup(Order pOrder, 
                                         ShippingGroup pShippingGroup, 
                                         List pModificationList) 
  {
    ShippingGroupCommerceItemRelationship sgCiRel;
    List shippingGroupCommerceItemRel;
    Iterator shippingGroupCommerceItemRelIterator;
    String destinationEmailAddress;
    Double amount;
    boolean allShipped = true;
    

    if (isLoggingDebug()) 
      logDebug("Going to attempt and ship item from a single Shipping Group");


    try {
      ShippingGroupStates sgs = getShippingGroupStates();
      ShipItemRelationshipStates sirs = getShipItemRelationshipStates();
      if (pShippingGroup instanceof ElectronicShippingGroup) {
        destinationEmailAddress = ((ElectronicShippingGroup)pShippingGroup).getEmailAddress();
      } else {
        pShippingGroup.setState(StateDefinitions.SHIPPINGGROUPSTATES.getStateValue(ShippingGroupStates.PENDING_MERCHANT_ACTION));
        Object[] args = { pShippingGroup };
        pShippingGroup.setStateDetail(ResourceUtils.getMsgResource("WrongShippingGroupType",
                                                            MY_RESOURCE_NAME,
                                                            sResourceBundle,args));
        return false;
      }
      shippingGroupCommerceItemRel = pShippingGroup.getCommerceItemRelationships();
      shippingGroupCommerceItemRelIterator = shippingGroupCommerceItemRel.iterator();
      
      if (isLoggingDebug())
        logDebug("There are " + shippingGroupCommerceItemRel.size() +" commerce items in the sg");
      
      // for each ci relationship
      while(shippingGroupCommerceItemRelIterator.hasNext()) {
        sgCiRel = (ShippingGroupCommerceItemRelationship)shippingGroupCommerceItemRelIterator.next();
        // if it's already been delivered, don't deliver it again.
        if(sgCiRel.getState() != sirs.getStateValue(ShipItemRelationshipStates.DELIVERED)) {
          // get the quantity
          long quantity=0;

          // find out how many items to allocate from inventory
          int relationshipType = sgCiRel.getRelationshipType();
          switch(relationshipType)
          {
          case RelationshipTypes.SHIPPINGQUANTITY:
            {              
              quantity = sgCiRel.getQuantity();
              break;
            }
          case RelationshipTypes.SHIPPINGQUANTITYREMAINING:
            {
              // calculate the quantity
              quantity = getOrderManager().getRemainingQuantityForShippingGroup(sgCiRel.getCommerceItem());
              break;
            }
          } //switch

          for (int i=0; i<quantity; i++) {
            try {
              amount = (Double)DynamicBeans.getPropertyValue(sgCiRel.getCommerceItem().getAuxiliaryData().getCatalogRef(),
                                                             getGiftCertificateAmountProperty());
            }
            catch(PropertyNotFoundException pe) {
              if (isLoggingError())
                logError(pe);
              
              return false;
            }
            String purchaser = pOrder.getProfileId();
            RepositoryItem electronicClaimable = createElectronicGood(amount, 
                                                                      amount, 
                                                                      purchaser, 
                                                                      new Date());
            try {
              if(isUseTemplateEmailSender()) {
                if(isLoggingDebug())
                  logDebug("Using template email sender to deliver email to " + destinationEmailAddress);
                deliverElectronicGood(pOrder, pShippingGroup, electronicClaimable, 1); 
              }
              else {
                if(isLoggingDebug())
                  logDebug("Using email listener to deliver email to " + destinationEmailAddress);
                deliverElectronicGood(destinationEmailAddress, electronicClaimable.getRepositoryId()); 
              }
              
              getOrderFulfillmentTools().setItemRelationshipState(sgCiRel,
                                                                  sirs.getStateValue(ShipItemRelationshipStates.DELIVERED),
                                                                  null, 
                                                                  pModificationList);
            } catch (CommerceException ce) {
              if(isLoggingError())
                logError(ce);

              getOrderFulfillmentTools().setItemRelationshipState(sgCiRel,
                                                                  sirs.getStateValue(ShipItemRelationshipStates.FAILED),
                                                                  ce.toString(),
                                                                  pModificationList);
              allShipped = false;
            } catch (EmailException ee) {
              if(isLoggingError())
                logError(ee);
              
              getOrderFulfillmentTools().setItemRelationshipState(sgCiRel,
                                                                  sirs.getStateValue(ShipItemRelationshipStates.FAILED),
                                                                  ee.toString(),
                                                                  pModificationList);
              allShipped = false;
            }
          }
        }
        else {
          if(isLoggingDebug())
            logDebug("The item " + sgCiRel.getCommerceItem().getCatalogRefId() + " has already been sent.");
        }
        // update sgCiRel
      }
      if (allShipped) {
        getOrderFulfillmentTools().setShippingGroupState(pShippingGroup,
                                                         sgs.getStateValue(ShippingGroupStates.NO_PENDING_ACTION),
                                                         null, 
                                                         pModificationList);
      } else {
        getOrderFulfillmentTools().setShippingGroupState(pShippingGroup,
                                                         sgs.getStateValue(ShippingGroupStates.PENDING_MERCHANT_ACTION),
                                                         null, 
                                                         pModificationList);
      }
    } catch (ClassCastException cce) {
      if (isLoggingError())
        logError(cce);
      
      return false;
    } catch (CommerceException cce) {
      if (isLoggingError())
        logError(cce);
      
      return false;
    }

    return true;
  }
					       

  /**
   * This method will return the Order from the order repository with the given id.  In the
   * standard implementation this will call the OrderManager and load the order.
   *
   * @beaninfo
   *          description: Load the order using the order manager.
   * @param pId the id of the order to be retrieved
   * @return the order corresponding to the id passed in
   * @exception CommerceException
   **/
  public Order loadOrder(String pId) 
    throws CommerceException 
  {
    Order order = null;
    try {
      order = getOrderManager().loadOrder(pId);
    } catch(atg.commerce.order.InvalidParameterException i) {        
    }
    
    return order;
  }
  

  /**
   * This method is used to create the electronic good that is being
   * fulfilled.  In this implementation that means create a gift certificate
   * in the claimable repository.  The gift certificate will then be initialized
   * with the specified amount, amountRemaining, purchaserId and date.
   *
   *
   * @param pAmount the amount of the gift certificate
   * @param pAmountRemaining the amount left on a gift certificate
   * @param pPurchaserId profile id of the person who purchased the gift certificate
   * @param pPurchaseDate the date the gift certificate was purchased
   * @return the created electronic good
   * @exception CommerceException if an error occurs
   *
   * @deprecated Replaced by the pipeline processor
   */
  protected RepositoryItem createElectronicGood(Double pAmount,
                                                Double pAmountRemaining,
                                                String pPurchaserId,
                                                Date pPurchaseDate)
    throws CommerceException
  {
    if (isLoggingDebug())
      logDebug("going to try and create an electronic good");

    RepositoryItem electronicClaimable = 
      mClaimableManager.createClaimableGiftCertificate();

    if (electronicClaimable == null) {
      throw new CommerceException(Constants.UNABLE_TO_CREATE_GOOD);
    }

    mClaimableManager.initializeClaimableGiftCertificate(electronicClaimable,
                                                         pAmount,
                                                         pAmountRemaining,
                                                         pPurchaserId,
                                                         pPurchaseDate);
    return electronicClaimable;
  }


  /**
   * Uses the TemplateEmailSender to send the gift certificate email
   * This method is used to actually deliver the electronic good.
   * In this implementation this means using the TemplateEmailSender
   *
   * @param pOrder The order used to purchase the gift certificate
   * @param pShippingGroup The shipping group used to ship the gift certificate
   * @param pElectronicClaimable The claimable item
   * @param pQuantity The number of gift certificates
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void deliverElectronicGood(Order pOrder, 
                                       ShippingGroup pShippingGroup, 
                                       RepositoryItem pElectronicClaimable,
                                       long pQuantity) 
    throws CommerceException
  {
    if (isLoggingDebug())
      logDebug("going to try and deliver the electronic good");

    try {
      String emailAddress = ((ElectronicShippingGroup)pShippingGroup).getEmailAddress();
      RepositoryItem purchaser = getProfileForOrder(pOrder);
      TemplateEmailInfo emailInfo = createTemplateEmailInfo(purchaser, pElectronicClaimable, pQuantity, emailAddress);
      List recipients = new ArrayList();
      recipients.add(emailAddress);

      // send the email
      getTemplateEmailSender().sendEmailMessage(emailInfo, 
                                                recipients, 
                                                isSeparateEmailThread(), 
                                                isPersistEmails());
    }
    catch (TemplateEmailException t) {
      throw new CommerceException(t);
    }
    catch (RepositoryException r) {
      throw new CommerceException(r);
    }
    catch (ClassCastException c) {
      throw new CommerceException(c);
    }
  }

  //----------------------------------------
  /**
   * Creates and returns the TemplateEmailInfo to use for sending
   * email within this action.  
   *
   * @param pGiftCertificate The gift certificate being emailed
   * @param pQuantity The number of gift certificates
   **/
  private TemplateEmailInfo createTemplateEmailInfo(RepositoryItem pPurchaser,
                                                    RepositoryItem pGiftCertificate,
                                                    long pQuantity,
                                                    String pRecipientEmail)
  {
    // make a copy of the default email info
    TemplateEmailInfo emailInfo = getDefaultTemplateEmailInfo().copy();

      // set the template url
    String templateName = getGiftCertificateEmailTemplate();
    emailInfo.setTemplateURL(templateName);
    
    Map params = new HashMap();
    params.put(getPurchaserParamName(), pPurchaser);
    params.put(getGiftCertificateParamName(), pGiftCertificate);
    params.put(getQuantityParamName(), Long.valueOf(pQuantity));
    params.put(getRecipientEmailParamName(), pRecipientEmail);
    emailInfo.setTemplateParameters(params);

    return emailInfo;
  }

  /**
   * Uses the profile repository to get the profile RepositoryItem
   * for the profileId in the order.
   **/
  private RepositoryItem getProfileForOrder(Order pOrder)
    throws RepositoryException
  {
    if(pOrder == null)
      return null;
    Repository profileRep = getProfileRepository();
    String profileId = pOrder.getProfileId();
    RepositoryItem profile = profileRep.getItem(profileId, getDefaultProfileType());
    
    return profile;
  }


  /**
   * This method is used to actually deliver the electronic good.
   * In this implementation this means creating an emailEvent using
   * the createEmailObject method of this class and then for each
   * emailListener that is configured in this class calling sendEmailEvent.
   *
   * @param pRecipientEmailAddress the address that will receive the email
   * @param pClaimCode the code that is used to obtain something from the Claimable Repository
   * @exception EmailException if an error occurs, or emailListeners is size 0
   *
   * @deprecated Replaced by the pipeline processor
   */
  protected void deliverElectronicGood(String pRecipientEmailAddress,
                                       String pClaimCode) 
    throws EmailException

  {
    if (isLoggingDebug())
      logDebug("going to try and deliver the electronic good");

    EmailListener emailListener;
    EmailEvent email;
    // email the newly created object
    if (mEmailListeners != null && mEmailListeners.size() != 0) {
      // create email object
      email = createEmailObject(pRecipientEmailAddress,	pClaimCode);
      int listSize = mEmailListeners.size();
      for (int i=0; i<listSize; i++) {
        emailListener = (EmailListener) mEmailListeners.get(i);
        emailListener.sendEmailEvent(email);
      }
    } else {
      throw new EmailException(ResourceUtils.getMsgResource("NoEmailListeners",
                                                            MY_RESOURCE_NAME,
                                                            sResourceBundle));
    }
  }

  
  /**
   * This method will create an EmailEvent that corresponds to the 
   * message to send out.  It will send the message to the supplied
   * email address.  Additionally, it can take a claim code which is
   * tied to the claimable repository.  That is, by default the claim
   * code can be used to obtain something from the claimable repository.
   *
   * <P>
   *
   * The EmailEvent is created with the message body that is
   * returned by the createEmailMessageBody method.  Also, the
   * from address is set to the defaultFromAddress property
   * of this class.
   *
   * @param pRecipientEmailAddress email address that this email will be sent to
   * @param pClaimCode code to claim something from the ClaimableRepository
   * @return a value of type 'EmailEvent'
   *
   * @deprecated Replaced by the pipeline processor
   */
  protected EmailEvent createEmailObject(String pRecipientEmailAddress,
                                         String pClaimCode) 
  {
    EmailEvent email;
    
    if (pRecipientEmailAddress == null) {
      if(isLoggingError())          
        logError(Constants.RECIPIENT_EMAIL_ADDRESS_NULL);
    }
    
    String messageBody = createEmailMessageBody(pClaimCode);
    if (isLoggingDebug())
      logDebug("Sending an email event with from address: " +
               mDefaultFromAddress + " recipientAddress: " +
               pRecipientEmailAddress + " subject: " +
               mDefaultSubject + " body: " + messageBody);

    email = new EmailEvent(mDefaultFromAddress,
                           pRecipientEmailAddress,
                           mDefaultSubject,
                           messageBody);
    return email;
  }
  

  /**
   * Creates the email message body that will be put into the email.  By default
   * it just appends the claim code onto the defaultMessageBody property.
   *
   * @param pClaimCode code that is used to claim things from the ClaimbleRepository
   * @return the email message body
   *
   * @deprecated Replaced by the pipeline processor
   */
  protected String createEmailMessageBody(String pClaimCode) {
    return mDefaultMessageBody + ": " + pClaimCode;
  }

  /**
   * This method will save the order passed in to the repository that is being used.
   *
   * @beaninfo
   *          description: Save the given order using the order manager.
   * @param pOrder - the order to be saved
   * @exception CommerceException
   **/
  public void updateOrder(Order pOrder) 
    throws CommerceException 
  {
    if (isLoggingDebug()) {
      logDebug("Inside updateOrder");
      getOrderFulfillmentTools().printOrder(pOrder);
    }
    
    getOrderManager().updateOrder(pOrder);
  }  

}   // end of class
