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

import atg.servlet.*;
import atg.nucleus.ServiceException;
import atg.nucleus.naming.ParameterName;
import atg.userprofiling.Profile;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.PipelineConstants;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.profile.CommercePropertyManager;
import atg.commerce.util.NoLockNameException;
import atg.commerce.util.TransactionLockFactory;
import atg.commerce.util.TransactionLockService;
import atg.core.util.ResourceUtils;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;

import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;
import atg.service.pipeline.servlet.*;
import atg.repository.RepositoryItem;

/**
 * The <code>RepriceOrder</code> class extends PipelineChainInvocation and provides
 * the objects necessary for executing the repricing PipelineChain as convenient properties.
 * Typically, the execution of the pipeline chain for repricing requires the Order, the Profile,
 * the OrderManager, and the user's PricingModelHolder. While the PipelineChainInvocation
 * is flexible enough to handle this, RepriceOrder is conveniently configured to reference these
 * objects so that the template designer need not supply the same input parameters everytime
 * this DynamoServlet is invoked.
 *
 * <p>The RepriceOrder takes one required input parameter:
  *
 * <p>pricingOp
 * <br>This required parameter should be set to the pricing operation that will be executed.
 * The operations which are acceptable are defined in the
 * <code>atg.commerce.pricing.PricingConstants</code> interface. They include:
 * <UL>
 * <LI>PricingConstants.OP_REPRICE_ORDER_TOTAL -> <code>ORDER_TOTAL</code>
 * <LI>PricingConstants.OP_REPRICE_ORDER_SUBTOTAL -> <code>ORDER_SUBTOTAL</code>
 * <LI>PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_SHIPPING -> <code>ORDER_SUBTOTAL_SHIPPING</code>
 * <LI>PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_TAX -> <code>ORDER_SUBTOTAL_TAX</code>
 * <LI>PricingConstants.OP_REPRICE_ITEMS -> <code>ITEMS</code>
 * <LI>PricingConstants.OP_REPRICE_SHIPPING -> <code>SHIPPING</code>
 * <LI>PricingConstants.OP_REPRICE_ORDER -> <code>ORDER</code>
 * <LI>PricingConstants.OP_REPRICE_TAX -> <code>TAX</code>
 * <LI>PricingConstants.OP_NO_REPRICE -> <code>NO_REPRICE</code>
 * </UL>
 *
 * <p>The RepriceOrder takes one optional input parameter:
  *
 * <p>priceList
 * <br>This parameter can be used to specify a price list that is used in the pricing
 * operation
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/RepriceOrder.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.service.pipeline.servlet.PipelineChainInvocation
 */
public class RepriceOrder extends atg.service.pipeline.servlet.PipelineChainInvocation
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/RepriceOrder.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------
  private static final String MY_RESOURCE_NAME = "atg.commerce.order.purchase.PurchaseProcessResources";
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  public final static ParameterName PRICING_OP = ParameterName.getParameterName("pricingOp");
  public final static ParameterName PRICE_LIST = ParameterName.getParameterName("priceList");
  public final static String ORDER_MANAGER = "OrderManager";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------
  // Property: CommercePropertyManager defines properties of the profile for Commerce
  CommercePropertyManager mCommercePropertyManager;
      
  /**
  * Set the CommercePropertyManager property.
  * @param pCommercePropertyManager the Commerce profile property manager
  * @beaninfo description: the claimable manager - used for store credits
  */
  public void setCommercePropertyManager(CommercePropertyManager pCommercePropertyManager) {
      mCommercePropertyManager = pCommercePropertyManager;
  }
      
  /**
  * Return the CommercePropertyManager property.
  */
  public CommercePropertyManager getCommercePropertyManager() {
      return mCommercePropertyManager;
  }
  //---------------------------------------------------------------------------
  // property: configuration
  //---------------------------------------------------------------------------
  PurchaseProcessConfiguration mConfiguration;

  /**
   * Sets property Configuration
   * @param pConfiguration a <code>PurchaseProcessConfiguration</code> value
   */
  public void setConfiguration(PurchaseProcessConfiguration pConfiguration) {
    mConfiguration = pConfiguration;
  }

  /**
   * Returns property Configuration
   * @return a <code>PurchaseProcessConfiguration</code> value
   */
  public PurchaseProcessConfiguration getConfiguration() {
    return mConfiguration;
  }

  //---------------------------------------------------------------------------
  // property: Order
  //---------------------------------------------------------------------------
  Order mOrder;

  /**
   * Set the Order property.
   * @param pOrder an <code>Order</code> value
   */
  public void setOrder(Order pOrder) {
    mOrder = pOrder;
  }

  /**
   * Return the Order property.
   * @return an <code>Order</code> value
   */
  public Order getOrder() {
    return mOrder;
  }

  //---------------------------------------------------------------------------
  // property: UserPricingModels
  //---------------------------------------------------------------------------
  PricingModelHolder mUserPricingModels;

  /**
   * Set the UserPricingModels property.
   * @param pUserPricingModels a <code>PricingModelHolder</code> value
   */
  public void setUserPricingModels(PricingModelHolder pUserPricingModels) {
    mUserPricingModels = pUserPricingModels;
  }

  /**
   * Return the UserPricingModels property.
   * @return a <code>PricingModelHolder</code> value
   */
  public PricingModelHolder getUserPricingModels() {
    return mUserPricingModels;
  }

  //---------------------------------------------------------------------------
  // property: OrderManager
  //---------------------------------------------------------------------------
  OrderManager mOrderManager;

  /**
   * Set the OrderManager property.
   * @param pOrderManager an <code>OrderManager</code> value
   */
  public void setOrderManager(OrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  /**
   * Return the OrderManager property.
   * @return an <code>OrderManager</code> value
   */
  public OrderManager getOrderManager() {
    return mOrderManager;
  }

  //---------------------------------------------------------------------------
  // property: Profile
  //---------------------------------------------------------------------------
  Profile mProfile;

  /**
   * Set the Profile property.
   * @param pProfile a <code>Profile</code> value
   */
  public void setProfile(Profile pProfile) {
    mProfile = pProfile;
  }

  /**
   * Return the Profile property.
   * @return a <code>Profile</code> value
   */
  public Profile getProfile() {
    return mProfile;
  }

  //---------------------------------------------------------------------------
  // property:TransactionLockFactory
  //---------------------------------------------------------------------------
  private TransactionLockFactory mTransactionLockFactory;
  public void setTransactionLockFactory(TransactionLockFactory pTransactionLockFactory) {
    mTransactionLockFactory = pTransactionLockFactory;
  }

  /**
   * The factory used to get the service that is used to get and release locks
   * before modifying the order
   **/
  public TransactionLockFactory getTransactionLockFactory() {
    return mTransactionLockFactory;
  }

  //-------------------------------------
  // ResourceBundle support
  //-------------------------------------

  /**
   * Returns the error message ResourceBundle
   */
  protected ResourceBundle getResourceBundle() {
    return sResourceBundle;
  }

  /**
   * Returns the name of the error message ResourceBundle
   */
  protected String getResourceBundleName() {
    return MY_RESOURCE_NAME;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>RepriceOrder</code> instance.
   *
   */
  public RepriceOrder () {}
  
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  /**
   * <code>getParamObject</code> is used to return the Object parameter passed as an argument
   * to the PipelineManager.runProcess method. This automatically includes the <code>Order</code>,
   * <code>Profile</code>, <code>OrderManager</code>, <code>PricingModelHolder</code>,
   * and the pricingOperation. Additional parameters may be supplied by using the
   * ExtraParametersMap.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return an <code>Object</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public Object getParamObject(DynamoHttpServletRequest pRequest, 
                               DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException 
  {
    int size = 5;
    if (getExtraParametersMap() != null) {
      size = getExtraParametersMap().size() + size;
    }
    HashMap paramObject = new HashMap(size);

    paramObject.put(PricingConstants.PRICING_OPERATION_PARAM, pRequest.getObjectParameter(PRICING_OP));
    paramObject.put(PricingConstants.ORDER_PARAM, getOrder());
    paramObject.put(PricingConstants.PRICING_MODELS_PARAM, getUserPricingModels());
    paramObject.put(PricingConstants.PROFILE_PARAM, getProfile());
    paramObject.put(PipelineConstants.ORDERMANAGER, getOrderManager());

    if (getExtraParametersMap() != null) {
      Set entrySet = getExtraParametersMap().entrySet();
      Iterator iter = entrySet.iterator();
      while (iter.hasNext()) {
        Map.Entry entry = (Map.Entry) iter.next();
        String extraParameterName = (String) entry.getKey();
        String requestParameterName = (String) entry.getValue();
        Object extraParameterValue = pRequest.getObjectParameter(requestParameterName);
        paramObject.put(extraParameterName, extraParameterValue);
      }
    }

    
    //if the price list was specified, create an extra parameters map if necessary. 
    RepositoryItem priceList = (RepositoryItem)pRequest.getObjectParameter(PRICE_LIST);
    if(priceList != null)
    {
        String priceListPropertyName = getCommercePropertyManager().getPriceListPropertyName(); 
        Map extraParameters = (Map)paramObject.get(PricingConstants.EXTRA_PARAMETERS_PARAM);
        if(extraParameters == null)
        {
            extraParameters = new HashMap();
            extraParameters.put(priceListPropertyName,priceList);
            paramObject.put(PricingConstants.EXTRA_PARAMETERS_PARAM,extraParameters);
        }
        else if(extraParameters.get(priceListPropertyName) == null)
            extraParameters.put(priceListPropertyName,priceList);
    }
    return paramObject;
  }

  // -------------------------------
  // Centralized configuration
  // -------------------------------

  /**
   * Copy property settings from the optional
   * <code>PurchaseProcessConfiguration</code> component. Property
   * values that were configured locally are preserved.
   *
   * Configures the following properties (if not already set):
   * <UL>
   * <LI>defaultPipelineManager (from pipelineManager)
   * <LI>orderManager
   * <LI>profile
   * <LI>userPricingModels
   * </UL>
   **/
  protected void copyConfiguration() {
    if (mConfiguration != null) {
      if (getDefaultPipelineManager() == null) {
        setDefaultPipelineManager(mConfiguration.getPipelineManager());
      }
      if (getOrderManager() == null) {
        setOrderManager(mConfiguration.getOrderManager());
      }
      if (getProfile() == null && mConfiguration.getProfile() instanceof Profile) {
        setProfile((Profile)mConfiguration.getProfile());
      }
      if (getUserPricingModels() == null) {
        setUserPricingModels(mConfiguration.getUserPricingModels());
      }
      if (getCommercePropertyManager() == null) {
          setCommercePropertyManager(mConfiguration.getCommercePropertyManager());
        }
    }
  }

  // -------------------------------
  // GenericService overrides
  // -------------------------------

  /**
   * Perform one-time startup operations, including copying property
   * settings from the optional <code>PurchaseProcessConfiguration</code>
   * component.
   */
  public void doStartService()
       throws ServiceException
  {
    super.doStartService();
    copyConfiguration();
  }
  
  
  /**
   * Extends the super class method to synchronizes on the order object before 
   * calling the reprice order chain.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    {
      TransactionLockFactory tlf = getTransactionLockFactory();
      TransactionLockService lockService = null;
      if(tlf != null)
        lockService = tlf.getServiceInstance(this);
      else 
        if(isLoggingWarning())
          logWarning(ResourceUtils.getMsgResource("missingTransactionLockFactory",
                                                  getResourceBundleName(), getResourceBundle()));
      
      try {
        if(lockService != null)
          lockService.acquireTransactionLock();
      }
      catch (NoLockNameException exc) {
        if(isLoggingError())
          logError(exc);
      }
      catch (DeadlockException de) {
      
        // We are going to log the exception here and then ignore it because
        // the worst that should happen is that the user will get a concurrent
        // update exception if two threads try to modify the same order, and we
        // can recover from that.

        if (isLoggingError())
          logError(de);
      }

      try {
        Order order = getOrder();
        if(order != null){
          synchronized(order){
            super.service(pRequest, pResponse);
          }
        }else{
          super.service(pRequest, pResponse);
        }
      }

      // Release the transaction lock in a finally clause in case any of the code
      // above throws exceptions.  We don't want to end up holding the lock forever
      // in this case.
    
      finally
      {
        try {
          if(lockService != null)
            lockService.releaseTransactionLock();
        }
        catch (LockManagerException lme) {
          if (isLoggingError())
            logError(lme);
        }
      }
        
    }
  }

}   // end of class
