/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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

package atg.commerce.order.purchase;

import atg.commerce.CommerceException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderTools;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.droplet.DropletFormException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.transaction.Transaction;

/**
 * The ExpressCheckoutFormHAndler is used to expedite the checking out of an Order. This
 * supports creating a maximum of one Profile derived HardgoodShippingGroup and one Profile derived
 * CreditCard, followed by committing the Order.
 *
 * If the ExpressCheckoutFormHandler.PaymentGroupNeeded property is true then
 * a CreditCard PaymentGroup will automatically be taken from the Profile. If false,
 * then the user may supply the CreditCard information in a form through the
 * ExpressCheckoutFormHandler.PaymentGroup property.
 *
 * If the ExpressCheckoutFormHandler.ShippingGroupNeeded property is true then
 * a HardgoodShippingGroup will automatically be taken from the Profile. If false,
 * then the user may supply the HardgoodShippingGroup information in a form through the
 * ExpressCheckoutFormHandler.ShippingGroup property.
 *
 * If the ExpressCheckoutFormHandler.CommitOrder property is true, then the
 * ExpressCheckoutFormHandler.ExpressCheckout handler will commit the Order.
 * Set to false in order to display a confirmation page before committing the Order.
 *
 * @beaninfo
 *   description: A formhandler which facilitates express checkout of a ready ShoppingCart.
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/ExpressCheckoutFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.droplet.GenericFormHandler
 * @see atg.commerce.order.purchase.PurchaseProcessFormHandler
 */

public class ExpressCheckoutFormHandler extends PurchaseProcessFormHandler
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/ExpressCheckoutFormHandler.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  public static final String MSG_EXPRESS_CHECKOUT_ERROR = "errorMovingToConfirmation";
  public static final String MSG_UPDATE_ORDER_ERROR = "errorUpdatingOrder";
  public static final String MSG_UNABLE_TO_COPY_ADDRESS = "errorCopyingAddress";
  public static final String MSG_REPRICE_ORDER_ERROR = "errorRepricingOrder";
  public static final String MSG_ORDER_ALREADY_SUBMITTED = "orderAlreadySubmitted";
  public static final String ERROR_MISSING_SHIPPING_ADDRESS = "missingProfileDefaultBillingAddress";
  public static final String ERROR_MISSING_BILLING_ADDRESS = "missingProfileDefaultShippingAddress";
  public static final String ERROR_MISSING_CREDIT_CARD = "missingProfileDefaultCreditCard";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------
  String mSalesChannel;
  
  /**
   * Returns the salesChannel.
   * 
   * Returns the sales channel which is used to submit the order. This could be "Web", "Call Center",
   * or "Scheduled Orders". These values are defined in the order repository.
   * 
   * @return salesChannel
   */
  public String getSalesChannel() {
    return mSalesChannel;
  }

  /**
   * Sets the salesChannel.
   * 
   * Sets the sales channel which is used to submit the order. This could be "Web", "Call Center",
   * or "Scheduled Orders". These values are defined in the order repository.
   * 
   * @param pSalesChannel
   */
  public void setSalesChannel(String pSalesChannel) {
    mSalesChannel = pSalesChannel;
  }

  /** shipping group bean to use for modifying the shipping groups */
  ShippingGroup mShippingGroup;
  
  //-------------------------------------
  /**
   * Sets shipping group bean to use for modifying the shipping groups
   *
   * @param pShippingGroup a <code>ShippingGroup</code> value
   */
  public void setShippingGroup(ShippingGroup pShippingGroup) {
    mShippingGroup = pShippingGroup;
  }
  
  /**
   * Returns property ShippingGroup.
   * @return a <code>ShippingGroup</code> value
   * @beaninfo description: The shipping group used during modifications to shipping groups
   *
   */
  public ShippingGroup getShippingGroup() {

    if (mShippingGroup == null) {
      if (getOrder().getShippingGroups() != null &&
          getOrder().getShippingGroups().size() > 0)
      {
        mShippingGroup = (ShippingGroup)getOrder().getShippingGroups().get(0);
      }
      else {
        try {
          getOrder().addShippingGroup(getShippingGroupManager().createShippingGroup());
          mShippingGroup = (ShippingGroup)getOrder().getShippingGroups().get(0);
        }
        catch (CommerceException ce) {
          if (isLoggingError()) {
            logError(ce);
          }
          return null;
        }
      }
    }
    return mShippingGroup;
  }

  /** payment group bean to use for modifiing payment groups */
  PaymentGroup mPaymentGroup;
  
  //-------------------------------------
  /**
   * Sets payment group bean to use for modifiing payment groups
   *
   * @param pPaymentGroup a <code>PaymentGroup</code> value
   */
  public void setPaymentGroup(PaymentGroup pPaymentGroup) {
    mPaymentGroup = pPaymentGroup;
  }
  
  /**
   * Returns property PaymentGroup
   * @return a <code>PaymentGroup</code> value
   * @beaninfo description: The payment group used during modifications to payment groups
   *
   */
  public PaymentGroup getPaymentGroup() {
    if (mPaymentGroup == null) {
      if (getOrder().getPaymentGroups() != null &&
          getOrder().getPaymentGroups().size() > 0) {
        mPaymentGroup = (PaymentGroup)getOrder().getPaymentGroups().get(0);
      }
      else {
        try {
          getOrder().addPaymentGroup(getPaymentGroupManager().createPaymentGroup());
          mPaymentGroup = (PaymentGroup)getOrder().getPaymentGroups().get(0);
        }
        catch (CommerceException ce) {
          if (isLoggingError()) {
            logError(ce);
          }
          return null;
        }
      }
    }

    return mPaymentGroup;
  }

  //---------------------------------------------------------------------------
  // property: PaymentGroupNeeded
  //---------------------------------------------------------------------------
  boolean mPaymentGroupNeeded = false;

  /**
   * Set the PaymentGroupNeeded property.
   * @param pPaymentGroupNeeded a <code>boolean</code> value
   * @beaninfo description: Does the Order need a PaymentGroup?
   */
  public void setPaymentGroupNeeded(boolean pPaymentGroupNeeded) {
    mPaymentGroupNeeded = pPaymentGroupNeeded;
  }

  /**
   * Return the PaymentGroupNeeded property.
   * @return a <code>boolean</code> value
   */
  public boolean isPaymentGroupNeeded() {
    return mPaymentGroupNeeded;
  }

  //---------------------------------------------------------------------------
  // property: ShippingGroupNeeded
  //---------------------------------------------------------------------------
  boolean mShippingGroupNeeded = false;

  /**
   * Set the ShippingGroupNeeded property.
   * @param pShippingGroupNeeded a <code>boolean</code> value
   * @beaninfo description: Does the Order need a ShippingGroup?
   */
  public void setShippingGroupNeeded(boolean pShippingGroupNeeded) {
    mShippingGroupNeeded = pShippingGroupNeeded;
  }

  /**
   * Return the ShippingGroupNeeded property.
   * @return a <code>boolean</code> value
   */
  public boolean isShippingGroupNeeded() {
    return mShippingGroupNeeded;
  }

  //---------------------------------------------------------------------------
  // property: DefaultShippingMethodPropertyName
  //---------------------------------------------------------------------------
  String mDefaultShippingMethodPropertyName;

  /**
   * Set the DefaultShippingMethodPropertyName property.
   * @param pDefaultShippingMethodPropertyName a <code>String</code> value
   * @beaninfo description: Profile property for default shipping method.
   */
  public void setDefaultShippingMethodPropertyName(String pDefaultShippingMethodPropertyName) {
    mDefaultShippingMethodPropertyName = pDefaultShippingMethodPropertyName;
  }

  /**
   * Return the DefaultShippingMethodPropertyName property.
   * @return a <code>String</code> value
   */
  public String getDefaultShippingMethodPropertyName() {
    return mDefaultShippingMethodPropertyName;
  }

  //---------------------------------------------------------------------------
  // property: DefaultShippingAddressPropertyName
  //---------------------------------------------------------------------------
  String mDefaultShippingAddressPropertyName;

  /**
   * Set the DefaultShippingAddressPropertyName property.
   * @param pDefaultShippingAddressPropertyName a <code>String</code> value
   * @beaninfo description:  Profile property for default shipping address.
   */
  public void setDefaultShippingAddressPropertyName(String pDefaultShippingAddressPropertyName) {
    mDefaultShippingAddressPropertyName = pDefaultShippingAddressPropertyName;
  }

  /**
   * Return the DefaultShippingAddressPropertyName property.
   * @return a <code>String</code> value
   */
  public String getDefaultShippingAddressPropertyName() {
    return mDefaultShippingAddressPropertyName;
  }

  //---------------------------------------------------------------------------
  // property: DefaultBillingAddressPropertyName
  //---------------------------------------------------------------------------
  String mDefaultBillingAddressPropertyName;

  /**
   * Set the DefaultBillingAddressPropertyName property.
   * @param pDefaultBillingAddressPropertyName a <code>String</code> value
   * @beaninfo description:  Profile property for default billing address.
   */
  public void setDefaultBillingAddressPropertyName(String pDefaultBillingAddressPropertyName) {
    mDefaultBillingAddressPropertyName = pDefaultBillingAddressPropertyName;
  }

  /**
   * Return the DefaultBillingAddressPropertyName property.
   * @return a <code>String</code> value
   */
  public String getDefaultBillingAddressPropertyName() {
    return mDefaultBillingAddressPropertyName;
  }

  //---------------------------------------------------------------------------
  // property: DefaultCreditCardPropertyName
  //---------------------------------------------------------------------------
  String mDefaultCreditCardPropertyName;

  /**
   * Set the DefaultCreditCardPropertyName property.
   * @param pDefaultCreditCardPropertyName a <code>String</code> value
   * @beaninfo description:  Profile property for default credit card.
   */
  public void setDefaultCreditCardPropertyName(String pDefaultCreditCardPropertyName) {
    mDefaultCreditCardPropertyName = pDefaultCreditCardPropertyName;
  }

  /**
   * Return the DefaultCreditCardPropertyName property.
   * @return a <code>String</code> value
   */
  public String getDefaultCreditCardPropertyName() {
    return mDefaultCreditCardPropertyName;
  }

  //---------------------------------------------------------------------------
  // property: CommitOrder
  //---------------------------------------------------------------------------
  boolean mCommitOrder = false;

  /**
   * Set the CommitOrder property.
   * @param pCommitOrder a <code>boolean</code> value
   * @beaninfo description: Should the order be committed? Set to false for displaying a confirmation page before committing.
   */
  public void setCommitOrder(boolean pCommitOrder) {
    mCommitOrder = pCommitOrder;
  }

  /**
   * Return the CommitOrder property.
   * @return a <code>boolean</code> value
   */
  public boolean isCommitOrder() {
    return mCommitOrder;
  }

  //---------------------------------------------------------------------------
  // property: ExpressCheckoutSuccessURL
  //---------------------------------------------------------------------------
  String mExpressCheckoutSuccessURL;

  /**
   * Set the ExpressCheckoutSuccessURL property.
   * @param pExpressCheckoutSuccessURL a <code>String</code> value
   * @beaninfo description: URL for redirection upon success.
   */
  public void setExpressCheckoutSuccessURL(String pExpressCheckoutSuccessURL) {
    mExpressCheckoutSuccessURL = pExpressCheckoutSuccessURL;
  }

  /**
   * Return the ExpressCheckoutSuccessURL property.
   * @return a <code>String</code> value
   */
  public String getExpressCheckoutSuccessURL() {
    return mExpressCheckoutSuccessURL;
  }

  //---------------------------------------------------------------------------
  // property: ExpressCheckoutErrorURL
  //---------------------------------------------------------------------------
  String mExpressCheckoutErrorURL;

  /**
   * Set the ExpressCheckoutErrorURL property.
   * @param pExpressCheckoutErrorURL a <code>String</code> value
   * @beaninfo description: URL for redirection upon failure.
   */
  public void setExpressCheckoutErrorURL(String pExpressCheckoutErrorURL) {
    mExpressCheckoutErrorURL = pExpressCheckoutErrorURL;
  }

  /**
   * Return the ExpressCheckoutErrorURL property.
   * @return a <code>String</code> value
   */
  public String getExpressCheckoutErrorURL() {
    return mExpressCheckoutErrorURL;
  }

  //---------------------------------------------------------------------------
  // property: PricingOperation
  //---------------------------------------------------------------------------
  String mPricingOperation = "ORDER_TOTAL";

  /**
   * Set the PricingOperation property.
   * @param pPricingOperation a <code>String</code> value
   * @beaninfo description: The PricingOperation to use.
   */
  public void setPricingOperation(String pPricingOperation) {
    mPricingOperation = pPricingOperation;
  }

  /**
   * Return the PricingOperation property.
   * @return a <code>String</code> value
   */
  public String getPricingOperation() {
    return mPricingOperation;
  }

  //---------------------------------------------------------------------------
  // property: OrderId
  //---------------------------------------------------------------------------
  String mOrderId;

  /**
   * Set the OrderId property.
   * @param pOrderId a <code>String</code> value
   * @beaninfo description: ID of the Order to use.
   */
  public void setOrderId(String pOrderId) {
    mOrderId = pOrderId;
  }

  /**
   * Return the OrderId property.
   * @return a <code>String</code> value
   */
  public String getOrderId() {
    return mOrderId;
  }

  //---------------------------------------------------------------------------
  // property: SiteId
  //---------------------------------------------------------------------------
  String mSiteId;

  /**
   * Sets the site ID to be recorded in the order
   *
   * @param pSiteId a <code>String</code> value
   * @beaninfo description: ID of the Site to use.
   */
  public void setSiteId(String pSiteId) {
    mSiteId = pSiteId;
  }

  /**
   * Returns the site ID to be recorded in the order
   *
   * @return a <code>String</code> value
   */
  public String getSiteId() {
    return mSiteId;
  }
  
  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  //-------------------------------------------------
  // ExpressCheckout
  //-------------------------------------------------
  
  /**
   * <code>preExpressCheckout</code> is for work that must happen before expressCheckout.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public void preExpressCheckout(DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>postExpressCheckout</code> is for work that must happen after expressCheckout.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public void postExpressCheckout(DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>handleExpressCheckout</code> is used to commit the Order after creating
   * a CreditCard PaymentGroup and a HardgoodShippingGroup.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return a <code>boolean</code> value
   * @exception IOException if there was an error with servlet io
   * pje
   * @exception ServletException if there was an error while executing the code
   */
  public boolean handleExpressCheckout( DynamoHttpServletRequest pRequest,
                                        DynamoHttpServletResponse pResponse )
    throws IOException, ServletException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "ExpressCheckoutOrderFormHandler.handleExpressCheckout";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

        // If form errors exist, redirect to error URL
        if ( !checkFormRedirect(null,getExpressCheckoutErrorURL(),pRequest,pResponse) )
          return false;

        // Let subclasses get their hands on the request/response before
        // anything's done here
        preExpressCheckout( pRequest, pResponse );

        // If form errors exist, redirect to error URL
        if ( !checkFormRedirect(null,getExpressCheckoutErrorURL(),pRequest,pResponse) )
          return false;

        if (isShippingGroupNeeded())
          ensureShippingGroup(pRequest, pResponse);

        if (isPaymentGroupNeeded())
          ensurePaymentGroup(pRequest, pResponse);

        try {
          runRepricingProcess(pRequest, pResponse);
        } catch (RunProcessException exc) {
          processException (exc, MSG_REPRICE_ORDER_ERROR, pRequest, pResponse);
        }

        try {
          getOrderManager().updateOrder(getOrder());
        } catch (CommerceException exc) {
          processException(exc, MSG_UPDATE_ORDER_ERROR, pRequest, pResponse);
        }

        if (isCommitOrder()) {
          try {
            commitOrder (pRequest, pResponse);
          } catch (CommerceException exc) {
            processException (exc, MSG_EXPRESS_CHECKOUT_ERROR, pRequest, pResponse);
          }
        }

        // If form errors exist, redirect to error URL
        if ( !checkFormRedirect(null,getExpressCheckoutErrorURL(),pRequest,pResponse) )
          return false;

        // Let user modify request/respons post checkout
        postExpressCheckout( pRequest, pResponse );

        // If no form errors are found, redirect to the success URL,
        // otherwise redirect to the error URL.
        return checkFormRedirect( getExpressCheckoutSuccessURL(), getExpressCheckoutErrorURL(),
                                  pRequest, pResponse);
      }
      finally {
        if (tr != null) commitTransaction(tr);
        if (rrm != null)
          rrm.removeRequestEntry(myHandleMethod);
      }
    }
    else {
      return false;
    }
  }

  /**
   * <code>commitOrder</code> commits the Order.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception CommerceException if an error occurs
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void commitOrder (DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse)
    throws CommerceException, ServletException, IOException
  {
    // make sure they are not trying to double submit an order
    Order lastOrder = getShoppingCart().getLast();
    String orderId = getOrderId();
    if (orderId != null && lastOrder != null &&
        orderId.equals(lastOrder.getId())) {
      // invalid number given for quantity of item to add
      String msg = formatUserMessage(MSG_ORDER_ALREADY_SUBMITTED, pRequest, pResponse);
      String propertyPath = generatePropertyPath("orderId");
      addFormException(new DropletFormException(msg, propertyPath, MSG_ORDER_ALREADY_SUBMITTED));
    }
    else {
      Order order = getShoppingCart().getCurrent();
      synchronized (order) {
        PipelineResult result = getOrderManager().processOrder(order, getProcessOrderMap(getUserLocale()));
        if (! processPipelineErrors(result)) {
          if (getShoppingCart() != null) {
            getShoppingCart().setLast(order);
            getShoppingCart().setCurrent(null);
          }
        }
      }
    }
  }

  /**
   * This reprices the Order.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception RunProcessException if an error occurs
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void runRepricingProcess (DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse)
    throws RunProcessException, ServletException, IOException
  {
    if (getRepriceOrderChainId() == null)
      return;

    HashMap params = new HashMap();
    if (getPricingOperation() != null)
      params.put(PricingConstants.PRICING_OPERATION_PARAM, getPricingOperation());
    params.put(PricingConstants.ORDER_PARAM, getOrder());
    params.put(PricingConstants.PRICING_MODELS_PARAM, getUserPricingModels());
    params.put(PricingConstants.LOCALE_PARAM, getUserLocale());
    params.put(PricingConstants.PROFILE_PARAM, getProfile());
    params.put(PipelineConstants.ORDERMANAGER, getOrderManager());
    PipelineResult result = runProcess(getRepriceOrderChainId(), params);
    processPipelineErrors(result);
  }

  
  /**
   * <code>ensureShippingGroup</code> is used to ensure that the Order has a
   * ShippingGroup of type HardgoodShippingGroup.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  protected void ensureShippingGroup (DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse)
    throws IOException, ServletException
  {
    ShippingGroup shippingGroup = getShippingGroup();
    if ((shippingGroup == null) || (!(shippingGroup instanceof HardgoodShippingGroup))) {
      String msg = formatUserMessage(MSG_EXPRESS_CHECKOUT_ERROR, pRequest, pResponse);
      String propertyPath = generatePropertyPath("shippingGroup");
      addFormException(new DropletFormException(msg, propertyPath, MSG_EXPRESS_CHECKOUT_ERROR));
    }

    HardgoodShippingGroup hsg = (HardgoodShippingGroup) shippingGroup;

    // Set the default shipping method
    hsg.setShippingMethod((String) getProfile().getPropertyValue(getDefaultShippingMethodPropertyName()) );

    // Copy default shipping address to order
    RepositoryItem shippingAddress =
      (RepositoryItem) getProfile().getPropertyValue(getDefaultShippingAddressPropertyName());
    if (shippingAddress == null) {
      if ( isLoggingError() )
        logError(ResourceUtils.getMsgResource(ERROR_MISSING_SHIPPING_ADDRESS,
                                              getResourceBundleName(), getResourceBundle()));
    } else {
      try {
        OrderTools.copyAddress( shippingAddress, hsg.getShippingAddress() );
      } catch( CommerceException ce ) {
        processException( ce, MSG_UNABLE_TO_COPY_ADDRESS, pRequest, pResponse );
      }
    }
  }

  /**
   * <code>ensurePaymentGroup</code> is used to ensure that the Order has a PaymentGroup
   * of type CreditCard.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  protected void ensurePaymentGroup (DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse)
    throws IOException, ServletException
  {
    PaymentGroup paymentGroup = getPaymentGroup();
    if ((paymentGroup == null) || (!(paymentGroup instanceof CreditCard))) {
      String msg = formatUserMessage(MSG_EXPRESS_CHECKOUT_ERROR, pRequest, pResponse);
      String propertyPath = generatePropertyPath("paymentGroup");
      addFormException(new DropletFormException(msg, propertyPath, MSG_EXPRESS_CHECKOUT_ERROR));
    }
    
    // Copy the default credit card into the order
    RepositoryItem defaultCreditCard =
      (RepositoryItem) getProfile().getPropertyValue(getDefaultCreditCardPropertyName());
    
    if ( defaultCreditCard == null ) {
      if ( isLoggingError() )
        logError(ResourceUtils.getMsgResource(ERROR_MISSING_CREDIT_CARD,
                                              getResourceBundleName(), getResourceBundle()));
    } else {
      getCommerceProfileTools().copyCreditCard(defaultCreditCard, (CreditCard) paymentGroup);
    
      // Copy billing address to PaymentGroup
      RepositoryItem billingAddress =
        (RepositoryItem) getProfile().getPropertyValue(getDefaultBillingAddressPropertyName());

    
      if ( billingAddress == null ) {
        if ( isLoggingError() )
          logError(ResourceUtils.getMsgResource(ERROR_MISSING_BILLING_ADDRESS,
                                                getResourceBundleName(), getResourceBundle()));
      }
      else {
        try {
          OrderTools.copyAddress(billingAddress,((CreditCard)paymentGroup).getBillingAddress());
        } catch( CommerceException ce ) {
        processException( ce, MSG_UNABLE_TO_COPY_ADDRESS, pRequest, pResponse );
        }
      }
    }
  }
  
  /**
   * 
   * This method calls the <code>OrderManager.getProcessOrderMap(pLocale, map)</> to get the process order map
   * and adds site ID and sales channel to the map if specified.
   *   
   * @return
   */
  public HashMap getProcessOrderMap (Locale pLocale) 
  throws CommerceException {
    HashMap pomap = getOrderManager().getProcessOrderMap(pLocale, null);
    if (!StringUtils.isEmpty(getSiteId())) {
      pomap.put(PipelineConstants.SITEID, getSiteId());
    }
    if (!StringUtils.isEmpty(getSalesChannel())) {
      pomap.put(PipelineConstants.SALESCHANNEL, getSalesChannel());
    }
    return pomap;
  }
  
}   // end of class
