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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceIdentifier;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupCommerceItemRelationship;
import atg.commerce.order.PaymentGroupManager;
import atg.commerce.order.PaymentGroupOrderRelationship;
import atg.commerce.order.PaymentGroupRelationship;
import atg.commerce.order.PaymentGroupRelationshipContainer;
import atg.commerce.order.PaymentGroupShippingGroupRelationship;
import atg.commerce.order.RelationshipTypes;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.AmountInfo;
import atg.commerce.pricing.OrderPriceInfo;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.nucleus.ServiceException;
import atg.nucleus.ServiceMap;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userprofiling.Profile;

/**
 * The <code>PaymentGroupDroplet</code> is a DynamoServlet that is used to initialize
 * payment methods and CommerceIdentifierPaymentInfo objects for use by the PaymentGroupFormHandler.
 * The PaymentGroupDroplet is composed of the following containers:
 * <p>
 * PaymentGroupMapContainer - container for the user's named PaymentGroups
 * <p>
 * CommerceIdentifierPaymentInfoContainer - container for the user's CommerceIdentifierPaymentInfo
 * objects for a particular Order's CommerceIdentifiers
 * <p>
 * The service method ensures the following:
 * <p>
 * PaymentGroup initialization - the user's authorized payment methods are added by type to
 * the PaymentGroupMapContainer. Once this is done the user can begin to select from these
 * during the billing checkout process. The PaymentGroupInitializers ServiceMap is a registry
 * of PaymentGroup types to components that implement the PaymentGroupInitializer
 * interface. PaymentGroupInitializer components are ultimately responsible for creating
 * the appropriate PaymentGroups, initializing them as appropriate for the user, and adding
 * them to the PaymentGroupMapContainer.
 * <p>
 * CommerceIdentifierPaymentInfo initialization - the Order CommerceIdentifiers of interest
 * are used to instantiate CommerceIdentifierPaymentInfo objects, and these are added
 * to the CommerceIdentifierPaymentInfoContainer.
 * <p>
 * A complete description of the parameters to the PaymentGroupDroplet are:
 *
 * <dl>
 *
 * <dt>order
 * <dd>This parameter may be used to override the component's default setting for
 * the user's order. For convenience the user's Order is also exposed as an output parameter.
 *
 * <dt>clearPaymentInfos
 * <dd>When set to true this parameter will clear the user's CommerceIdentifierPaymentInfoContainer.
 * This should be done at least once per new order.
 *
 * <dt>clearPaymentGroups
 * <dd>When set to true this parameter will clear the user's PaymentGroupMapContainer.
 *
 * <dt>clear
 * <dd>When set to true this parameter will clear both the CommerceIdentifierPaymentInfoContainer
 * and the PaymentGroupMapContainer.
 *
 * <dt>initPaymentGroups
 * <dd>When this parameter is set to true, the designated PaymentGroup types will be initialized.
 *
 * <dt>paymentGroupTypes
 * <dd>This comma separated list of PaymentGroup types is used to determine which
 * PaymentGroupInitializer components are executed. Each possible PaymentGroup type is
 * configured to reference a PaymentGroupInitializer component in the
 * PaymentGroupInitializers ServiceMap.
 *
 * <dt>initItemPayment
 * <dd>When set to true this parameter will create a CommerceItemPaymentInfo for each CommerceItem
 * in the Order. These are then added to the CommerceIdentifierPaymentInfoContainer. The
 * CommerceItemPaymentInfo is a CommerceIdentifierPaymentInfo used for CommerceItem payment information.
 *
 * <dt>initShippingPayment
 * <dd>When set to true this parameter will create a ShippingGroupPaymentInfo for each ShippingGroup
 * in the Order. These are then added to the CommerceIdentifierPaymentInfoContainer. The
 * ShippingGroupPaymentInfo is a CommerceIdentifierPaymentInfo used for ShippingGroup payment information.
 *
 * <dt>initTaxPayment
 * <dd>When set to true this parameter will create a TaxPaymentInfo and add it to the
 * CommerceIdentifierPaymentInfoContainer. The TaxPaymentInfo is a CommerceIdentifierPaymentInfo
 * used for tax payment information.
 *
 * <dt>initOrderPayment
 * <dd>When set to true this parameter will create an OrderPaymentInfo and add it to the
 * CommerceIdentifierPaymentInfoContainer. The OrderPaymentInfo is a CommerceIdentifierPaymentInfo
 * used for Order payment information.
 *
 * <dt>initBasedOnOrder
 * <dd>When set to true this parameter will create a CommerceIdentifierPaymentInfo for each payment group relationhip
 * in the order. The payment group relationship could be of any of the follwing types such as CommerceItem, ShippingGroup and Order. 
 * If there is any payment group relationship in the order, this paramter creates a CommerceIdentifierPaymentInfo for 
 * each payment group relationship. This parameter creates a TaxPaymentInfo, OrderPaymentInfo, ShippingGroupPaymentInfo 
 * or CommerceItemPaymentInfo based on payment relationship type.
 *
 * <dt>createAllPaymentInfos
 * <dd>When set to true this parameter will check to see if any CommerceIdentifierPaymentInfo exists for each
 * CommerceIdentifier ( Order, CommerceItem, ShippingGroup and Tax) and this parameter will create all possible 
 * combinations (one CommerceIdentifierPaymentInfo per PaymentGroup).
 *
 * <dt>paymentGroups
 * <dd>This output parameter is set to the Map referenced by the PaymentGroupMapContainer.
 *
 * <dt>paymentInfos
 * <dd>This output parameter is set to the Map refereneced by the CommerceIdentifierPaymentInfoMap.
 *
 * </dl>
 *
 * <p>
 * Example Usage with initBasedOnOrder, createAllPaymentInfos and initOrderPayment
 * <p>
 * <pre>
 *  <dsp:droplet name="PaymentGroupDroplet">
 *    <dsp:param name="clear" param="init" />
 *    <dsp:param name="paymentGroupTypes" value="creditCard,storeCredit" />
 *    <dsp:param name="initPaymentGroups" param="init" />
 *    <dsp:param name="initBasedOnOrder" param="init" />
 *    <dsp:param name="initOrderPayment" param="init" />
 *    <dsp:param name="createAllPaymentInfos" value="true" />
 *    <dsp:oparam name="output">
 *    </dsp:oparam>
 *  </dsp:droplet>
 * </pre>
 *
 * The above example initializes Credit Card and Store Credit payment groups and initializes CommerceIdentifierPaymentInfos
 * based on the order relationships. If there are no relationships or payment groups exists in the order, a CommerceIdentifierPaymentInfo is added
 * based on the Profile's default payment group. Also note that the createAllPaymentInfos is true. During the initial page rendering, the
 * PaymentGroupMapContainer and CommerceIdentifierPaymentInfoContainer are initialized and CommerceIdentifierPaymentInfos are created. After the initial rendering
 * if there is any new PaymentGroup is added to the container, then the CommerceIdentifierPaymentInfo is created for the newly created PaymentGroup.
 *
 * <p>
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/PaymentGroupDroplet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class PaymentGroupDroplet
  extends DynamoServlet
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/PaymentGroupDroplet.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.purchase.PurchaseProcessResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  public static final ParameterName INIT_PAYMENT_GROUPS = ParameterName.getParameterName("initPaymentGroups");
  public static final ParameterName PAYMENT_GROUP_TYPES = ParameterName.getParameterName("paymentGroupTypes");
  public static final ParameterName INIT_ITEM_PAYMENT = ParameterName.getParameterName("initItemPayment");
  public static final ParameterName INIT_SHIPPING_PAYMENT = ParameterName.getParameterName("initShippingPayment");
  public static final ParameterName INIT_ORDER_PAYMENT = ParameterName.getParameterName("initOrderPayment");
  public static final ParameterName INIT_TAX_PAYMENT = ParameterName.getParameterName("initTaxPayment");
  public static final ParameterName INIT_BASED_ON_ORDER = ParameterName.getParameterName("initBasedOnOrder");
  public static final ParameterName CLEAR_ALL = ParameterName.getParameterName("clear");
  public static final ParameterName CLEAR_PAYMENT_INFOS = ParameterName.getParameterName("clearPaymentInfos");
  public static final ParameterName CLEAR_PAYMENT_GROUPS = ParameterName.getParameterName("clearPaymentGroups");

  //This input paramters is added to create commerce identifier payment infos for each payment groups.
  //If the input paramter is set, then cipi is created for each unique payment groups.
  public static final ParameterName CREATE_ALL_PAYMENT_INFOS = ParameterName.getParameterName("createAllPaymentInfos");

  public static final String PAYMENT_GROUPS = "paymentGroups";
  public static final String CIPI_MAP = "paymentInfos";
  public static final String ORDER = "order";
  public static final String OUTPUT = "output";

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
  // property: PaymentGroupManager
  //---------------------------------------------------------------------------
  PaymentGroupManager mPaymentGroupManager;

  /**
   * Set the PaymentGroupManager property.
   * @param pPaymentGroupManager a <code>PaymentGroupManager</code> value
   */
  public void setPaymentGroupManager(PaymentGroupManager pPaymentGroupManager) {
    mPaymentGroupManager = pPaymentGroupManager;
  }

  /**
   * Return the PaymentGroupManager property.
   * @return a <code>PaymentGroupManager</code> value
   */
  public PaymentGroupManager getPaymentGroupManager() {
    return mPaymentGroupManager;
  }

  //---------------------------------------------------------------------------
  // property: PaymentGroupTypes
  //---------------------------------------------------------------------------
  String mPaymentGroupTypes;

  /**
   * Set the PaymentGroupTypes property.
   * @param pPaymentGroupTypes a <code>String</code> value
   */
  public void setPaymentGroupTypes(String pPaymentGroupTypes) {
    mPaymentGroupTypes = pPaymentGroupTypes;
  }

  /**
   * Return the PaymentGroupTypes property.
   * @return a <code>String</code> value
   */
  public String getPaymentGroupTypes() {
    return mPaymentGroupTypes;
  }

  //---------------------------------------------------------------------------
  // property: PaymentGroupInitializers
  //---------------------------------------------------------------------------
  ServiceMap mPaymentGroupInitializers;

  /**
   * Set the PaymentGroupInitializers property.
   * @param pPaymentGroupInitializers a <code>ServiceMap</code> value
   */
  public void setPaymentGroupInitializers(ServiceMap pPaymentGroupInitializers) {
    mPaymentGroupInitializers = pPaymentGroupInitializers;
  }

  /**
   * Return the PaymentGroupInitializers property.
   * @return a <code>ServiceMap</code> value
   */
  public ServiceMap getPaymentGroupInitializers() {
    return mPaymentGroupInitializers;
  }

  //---------------------------------------------------------------------------
  // property: InitPaymentGroups
  //---------------------------------------------------------------------------
  boolean mInitPaymentGroups;

  /**
   * Set the InitPaymentGroups property.
   * @param pInitPaymentGroups a <code>boolean</code> value
   */
  public void setInitPaymentGroups(boolean pInitPaymentGroups) {
    mInitPaymentGroups = pInitPaymentGroups;
  }

  /**
   * Return the InitPaymentGroups property.
   * @return a <code>boolean</code> value
   */
  public boolean isInitPaymentGroups() {
    return mInitPaymentGroups;
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
  // property: ClearPaymentInfos
  //---------------------------------------------------------------------------
  boolean mClearPaymentInfos;

  /**
   * Set the ClearPaymentInfos property.
   * @param pClearPaymentInfos a <code>boolean</code> value
   */
  public void setClearPaymentInfos(boolean pClearPaymentInfos) {
    mClearPaymentInfos = pClearPaymentInfos;
  }

  /**
   * Return the ClearPaymentInfos property.
   * @return a <code>boolean</code> value
   */
  public boolean isClearPaymentInfos() {
    return mClearPaymentInfos;
  }

  //---------------------------------------------------------------------------
  // property: ClearPaymentGroups
  //---------------------------------------------------------------------------
  boolean mClearPaymentGroups;

  /**
   * Set the ClearPaymentGroups property.
   * @param pClearPaymentGroups a <code>boolean</code> value
   */
  public void setClearPaymentGroups(boolean pClearPaymentGroups) {
    mClearPaymentGroups = pClearPaymentGroups;
  }

  /**
   * Return the ClearPaymentGroups property.
   * @return a <code>boolean</code> value
   */
  public boolean isClearPaymentGroups() {
    return mClearPaymentGroups;
  }

  //---------------------------------------------------------------------------
  // property: ClearAll
  //---------------------------------------------------------------------------
  boolean mClearAll;

  /**
   * Set the ClearAll property.
   * @param pClearAll a <code>boolean</code> value
   */
  public void setClearAll(boolean pClearAll) {
    mClearAll = pClearAll;
  }

  /**
   * Return the ClearAll property.
   * @return a <code>boolean</code> value
   */
  public boolean isClearAll() {
    return mClearAll;
  }

  //---------------------------------------------------------------------------
  // property: InitItemPayment
  //---------------------------------------------------------------------------
  boolean mInitItemPayment;

  /**
   * Set the InitItemPayment property.
   * @param pInitItemPayment a <code>boolean</code> value
   */
  public void setInitItemPayment(boolean pInitItemPayment) {
    mInitItemPayment = pInitItemPayment;
  }

  /**
   * Return the InitItemPayment property.
   * @return a <code>boolean</code> value
   */
  public boolean isInitItemPayment() {
    return mInitItemPayment;
  }

  //---------------------------------------------------------------------------
  // property: InitShippingPayment
  //---------------------------------------------------------------------------
  boolean mInitShippingPayment;

  /**
   * Set the InitShippingPayment property.
   * @param pInitShippingPayment a <code>boolean</code> value
   */
  public void setInitShippingPayment(boolean pInitShippingPayment) {
    mInitShippingPayment = pInitShippingPayment;
  }

  /**
   * Return the InitShippingPayment property.
   * @return a <code>boolean</code> value
   */
  public boolean isInitShippingPayment() {
    return mInitShippingPayment;
  }

  //---------------------------------------------------------------------------
  // property: InitOrderPayment
  //---------------------------------------------------------------------------
  boolean mInitOrderPayment;

  /**
   * Set the InitOrderPayment property.
   * @param pInitOrderPayment a <code>boolean</code> value
   */
  public void setInitOrderPayment(boolean pInitOrderPayment) {
    mInitOrderPayment = pInitOrderPayment;
  }

  /**
   * Return the InitOrderPayment property.
   * @return a <code>boolean</code> value
   */
  public boolean isInitOrderPayment() {
    return mInitOrderPayment;
  }

  //---------------------------------------------------------------------------
  // property: InitTaxPayment
  //---------------------------------------------------------------------------
  boolean mInitTaxPayment;

  /**
   * Set the InitTaxPayment property.
   * @param pInitTaxPayment a <code>boolean</code> value
   */
  public void setInitTaxPayment(boolean pInitTaxPayment) {
    mInitTaxPayment = pInitTaxPayment;
  }

  /**
   * Return the InitTaxPayment property.
   * @return a <code>boolean</code> value
   */
  public boolean isInitTaxPayment() {
    return mInitTaxPayment;
  }

  //---------------------------------------------------------------------------
  // property: InitBasedOnOrder
  //---------------------------------------------------------------------------
  boolean mInitBasedOnOrder;

  /**
   * Set the InitBasedOnOrder property.  This defines whether the initialization
   * of the maps should be based on the ShippingGroupCommerceItemRelationships
   * currently contained in the order.
   * @param pInitBasedOnOrder a <code>boolean</code> value
   */
  public void setInitBasedOnOrder(boolean pInitBasedOnOrder) {
    mInitBasedOnOrder = pInitBasedOnOrder;
  }

  /**
   * Return the InitBasedOnOrder property.
   * @return a <code>boolean</code> value
   */
  public boolean isInitBasedOnOrder() {
    return mInitBasedOnOrder;
  }

  //---------------------------------------------------------------------------
  // property: CommerceIdentifierPaymentInfoContainer
  //---------------------------------------------------------------------------
  CommerceIdentifierPaymentInfoContainer mCommerceIdentifierPaymentInfoContainer;

  /**
   * Set the CommerceIdentifierPaymentInfoContainer property.
   * @param pCommerceIdentifierPaymentInfoContainer a <code>CommerceIdentifierPaymentInfoContainer</code> value
   */
  public void setCommerceIdentifierPaymentInfoContainer(CommerceIdentifierPaymentInfoContainer pCommerceIdentifierPaymentInfoContainer) {
    mCommerceIdentifierPaymentInfoContainer = pCommerceIdentifierPaymentInfoContainer;
  }

  /**
   * Return the CommerceIdentifierPaymentInfoContainer property.
   * @return a <code>CommerceIdentifierPaymentInfoContainer</code> value
   */
  public CommerceIdentifierPaymentInfoContainer getCommerceIdentifierPaymentInfoContainer() {
    return mCommerceIdentifierPaymentInfoContainer;
  }

  //---------------------------------------------------------------------------
  // property: PaymentGroupMapContainer
  //---------------------------------------------------------------------------
  PaymentGroupMapContainer mPaymentGroupMapContainer;

  /**
   * Set the PaymentGroupMapContainer property.
   * @param pPaymentGroupMapContainer a <code>PaymentGroupMapContainer</code> value
   */
  public void setPaymentGroupMapContainer(PaymentGroupMapContainer pPaymentGroupMapContainer) {
    mPaymentGroupMapContainer = pPaymentGroupMapContainer;
  }

  /**
   * Return the PaymentGroupMapContainer property.
   * @return a <code>PaymentGroupMapContainer</code> value
   */
  public PaymentGroupMapContainer getPaymentGroupMapContainer() {
    return mPaymentGroupMapContainer;
  }

  boolean mCreateAllPaymentInfos = false;

  /**
   * @return Returns the createAllPaymentInfos.
   */
  public boolean isCreateAllPaymentInfos() {
    return mCreateAllPaymentInfos;
  }

  /**
   * @param pCreateAllPaymentInfos The createAllPaymentInfos to set.
   */
  public void setCreateAllPaymentInfos(boolean pCreateAllPaymentInfos) {
    mCreateAllPaymentInfos = pCreateAllPaymentInfos;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>PaymentGroupDroplet</code> instance.
   *
   */
  public PaymentGroupDroplet () {}


  //--------------------------------------------------
  // Initialization Code
  //--------------------------------------------------

  /**
   * <code>initializeUserPaymentMethods</code> is used to add the user's
   * payment methods to the PaymentGroupMapContainer. For each entry in the
   * PaymentGroupTypes, its corresponding PaymentGroupInitializer is obtained
   * from the PaymentGroupInitializers ServiceMap and its initializePaymentGroups
   * method executed.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pProfile a <code>Profile</code> value
   */
  protected void initializeUserPaymentMethods (DynamoHttpServletRequest pRequest,
                                               Profile pProfile)
  {
    if (isClearAll() || isClearPaymentGroups()) {
      getPaymentGroupMapContainer().removeAllPaymentGroups();
      if (isLoggingDebug())
        logDebug("PaymentGroupDroplet removing all PaymentGroups");
    }

    if (isInitPaymentGroups()) {
      String paymentGroupTypes = StringUtils.removeWhiteSpace(getPaymentGroupTypes());
      StringTokenizer st = new StringTokenizer(paymentGroupTypes, ",");
      while (st.hasMoreTokens()) {
        String paymentGroupType = st.nextToken();
        PaymentGroupInitializer initializer = (PaymentGroupInitializer) getPaymentGroupInitializers().get(paymentGroupType);
        if (initializer == null) {
          if (isLoggingError()) {
            String [] msgArgs = { paymentGroupType };
            logError(ResourceUtils.getMsgResource("missingPaymentGroupInitializer",
                                                  MY_RESOURCE_NAME, sResourceBundle, msgArgs));
          }
        } else {
          if (isLoggingDebug())
            logDebug("Have PaymentGroupInitializer " + initializer + " for type " + paymentGroupType);
          try {
            initializer.initializePaymentGroups(pProfile, getPaymentGroupMapContainer(), pRequest);
          } catch (PaymentGroupInitializationException exc) {
            String [] msgArgs = { paymentGroupType };
            logError(ResourceUtils.getMsgResource("errorInitializingPaymentGroup",
                                                  MY_RESOURCE_NAME, sResourceBundle, msgArgs));
            logError(exc);
          }
        }
      }
    }
  }

  /**
   * <code>initializePaymentInfos</code> creates CommerceIdentifierPaymentInfo objects
   * for the Order, the Order's CommerceItems, the Order's ShippingGroups, and the tax.
   * These objects are added to the CommerceIdentifierPaymentInfoContainer.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pOrder an <code>Order</code> value
   */
  protected void initializePaymentInfos (DynamoHttpServletRequest pRequest,
                                         Order pOrder)
  {
    if (isClearAll() || isClearPaymentInfos()) {
      getCommerceIdentifierPaymentInfoContainer().removeAllCommerceIdentifierPaymentInfos();
      if (isLoggingDebug())
        logDebug("PaymentGroupDroplet removing all PaymentInfos");
    }

    if (isInitBasedOnOrder()) {
      initializeBasedOnOrder(pOrder);

      if (!isAnyPaymentInfoExistsInContainter()) {
        if (isLoggingDebug())
          logDebug("CommerceIdentifierPaymentInfoContainer does not have any cipi.");
        initializeCommerceIdentifierPayment(pOrder);
      } else {
        if (isLoggingDebug())
          logDebug("CommerceIdentifierPaymentInfoContainer already has cipi.");
      }
    } else {
      initializeCommerceIdentifierPayment(pOrder);
    }

    if (isCreateAllPaymentInfos()
        && isAnyPaymentInfoExistsInContainter()) {
      //yes we have cipis. then set all possible cipis.
      createAllCommerceIdentifierPaymentInfos();
    } else {
      if (isLoggingDebug())
        logDebug("CommerceIdentifierPaymentInfoContainer does not have any cipi.");
    }
  }

  /**
   *  This method initializes payment infos for preferred commerce identifies such as Order, CommerceItem,ShippingGroup or
   *  Tax. This will be set based on the droplet input parameter.
   *  This droplet has commerceidentifier level input paramter such as initOrderPayment, initTaxPayment, initShippingPayment
   *  and initItemPayment.
   *
   * @param pOrder
   */
  public void initializeCommerceIdentifierPayment (Order pOrder) {
    if (isInitItemPayment())
      initializeItemPayment(pOrder);
    if (isInitShippingPayment())
      initializeShippingPayment(pOrder);
    if (isInitOrderPayment())
      initializeOrderPayment(pOrder);
    if (isInitTaxPayment())
      initializeTaxPayment(pOrder);
  }

  /**
  *
  * This method checks to see if there is any existance of CommerceIdentifierPaymentInfo for each CommerceIdentifier ( Order, CommerceItem,
  * Shipping Group and Tax) and if there is any CommerceIdentifierPaymentInfo exists for a CommerceIdentifier, then this method
  * creates all possible combination of CommerceIdentifierPaymentInfo for each CommerceIdentifier.
  *
  */
 public void createAllCommerceIdentifierPaymentInfos() {

   if (isLoggingDebug()) {
     logDebug ("Entering createAllCommerceIdentifierPaymentInfos()");
   }

   CommerceIdentifierPaymentInfoContainer cipicontainter = getCommerceIdentifierPaymentInfoContainer();
   Map allcipis = cipicontainter.getCommerceIdentifierPaymentInfoMap();

   if (allcipis == null)  {
     if (isLoggingDebug()) {
       logDebug ("CommerceIdentifierPaymentInfoContainer does not have any cipis.");
     }
     allcipis = Collections.EMPTY_MAP; // prevent npe
   }

   Iterator cipiIterator = allcipis.keySet().iterator();
   while (cipiIterator.hasNext()) {
     String commerceIdentifierId = (String) cipiIterator.next();
     List cipiList = cipicontainter.getCommerceIdentifierPaymentInfos(commerceIdentifierId);
     if (cipiList != null
         && cipiList.size() > 0) {
       if (isLoggingDebug()) {
         logDebug ("cipi list is not empty with the commerce identifier ::" + commerceIdentifierId);
       }
       addMissingCommerceIdentifierPaymentInfos(cipiList);
     } else {
       if (isLoggingDebug()) {
         logDebug ("cipi list is empty.");
       }
     }
   }//end of while

 }

 /**
  *
  * This method checks for CommerceIdentifierPaymentInfo entry for each payment group. If there is an entry missing for
  * a payment group, this method add a empty CommerceIdentifierPaymentInfo for that payment group.
  *
  * @param pCommerceIdentifierPaymentInfos
  */
 public void addMissingCommerceIdentifierPaymentInfos (List pCommerceIdentifierPaymentInfos){

   if (isLoggingDebug()) {
     logDebug ("Entering addMissingCommerceIdentifierPaymentInfos()");
   }

   Map paymentGroupMap = getPaymentGroupMapContainer().getPaymentGroupMap();

   if (paymentGroupMap == null || pCommerceIdentifierPaymentInfos == null) {
     return;
   }

   if (paymentGroupMap.size() == 0) {
     return;
   }

   if (pCommerceIdentifierPaymentInfos.size() == 0) {
     return;
   }

   Iterator pgMapIterator = paymentGroupMap.keySet().iterator();
   CommerceIdentifier ci = null;

   while (pgMapIterator.hasNext()) {
     String paymentGroupName = (String) pgMapIterator.next();
     CommerceIdentifierPaymentInfo newcipi = null;

     Iterator cipisIterator = pCommerceIdentifierPaymentInfos.iterator();
     boolean paymentGroupNameFound = false;
     while (cipisIterator.hasNext()) {
       CommerceIdentifierPaymentInfo oldcipi = (CommerceIdentifierPaymentInfo) cipisIterator.next();
       if (oldcipi != null ) {
         ci = oldcipi.getCommerceIdentifier();
         if (oldcipi instanceof OrderPaymentInfo) {
           newcipi = new OrderPaymentInfo();
         } else if (oldcipi instanceof TaxPaymentInfo) {
           newcipi = new TaxPaymentInfo();
         } else if (oldcipi instanceof ShippingGroupPaymentInfo) {
           newcipi = new ShippingGroupPaymentInfo();
         } else if (oldcipi instanceof CommerceItemPaymentInfo){
           newcipi = new CommerceItemPaymentInfo();
         }
         newcipi.setPaymentMethod(paymentGroupName);
         newcipi.setRelationshipType(newcipi.getAmountType());
         newcipi.setCommerceIdentifier(oldcipi.getCommerceIdentifier());
       }// end of oldcipi null check

       if (oldcipi != null
           && !StringUtils.isEmpty(oldcipi.getPaymentMethod())
           && oldcipi.getPaymentMethod().equalsIgnoreCase(paymentGroupName)){
         paymentGroupNameFound = true;
         break;
       }
     }
     if (!paymentGroupNameFound) {
       getCommerceIdentifierPaymentInfoContainer().addCommerceIdentifierPaymentInfo(ci.getId(), newcipi);
     }
   }
 }

  /**
   * The <code>initializeItemPayment</code> method creates a CommerceItemPaymentInfo
   * for each CommerceItem in the Order. A CommerceItemPaymentInfo is a
   * CommerceIdentifierPaymentInfo used for CommerceItem payment information.
   *
   * This also removes any CommerceIdentifierPaymentInfo objects previously associated
   * with each CommerceItem.
   *
   * @param pOrder an <code>Order</code> value
   */
  protected void initializeItemPayment(Order pOrder)
  {
    List pCommerceIdentifiers = pOrder.getCommerceItems();
    Iterator iter = pCommerceIdentifiers.iterator();
    CommerceItem item = null;
    CommerceIdentifierPaymentInfo cipi;

    while (iter.hasNext()) {
      item = (CommerceItem) iter.next();
      getCommerceIdentifierPaymentInfoContainer().removeCommerceIdentifierPaymentInfos(item.getId());

      cipi = new CommerceItemPaymentInfo();
      cipi.setCommerceIdentifier(item);
      cipi.setRelationshipType(cipi.getAmountType());
      cipi.setPaymentMethod(getPaymentGroupMapContainer().getDefaultPaymentGroupName());
      AmountInfo ai = item.getPriceInfo();
      if (ai != null)
        cipi.setAmount(ai.getAmount());
      cipi.setQuantity(item.getQuantity());

      getCommerceIdentifierPaymentInfoContainer().addCommerceIdentifierPaymentInfo(item.getId(), cipi);
    }
  }

  /**
   * The <code>initializeShippingPayment</code> method creates a ShippingGroupPaymentInfo
   * for each ShippingGroup in the Order. A ShippingGroupPaymentInfo is a
   * CommerceIdentifierPaymentInfo used for ShippingGroup payment information.
   *
   * This also removes any CommerceIdentifierPaymentInfo objects previously associated
   * with each ShippingGroup.
   *
   * @param pOrder an <code>Order</code> value
   */
  protected void initializeShippingPayment(Order pOrder)
  {
    List pCommerceIdentifiers = pOrder.getShippingGroups();
    Iterator iter = pCommerceIdentifiers.iterator();
    ShippingGroup sg = null;
    CommerceIdentifierPaymentInfo cipi;

    while (iter.hasNext()) {
      sg = (ShippingGroup) iter.next();
      getCommerceIdentifierPaymentInfoContainer().removeCommerceIdentifierPaymentInfos(sg.getId());

      cipi = new ShippingGroupPaymentInfo();
      cipi.setCommerceIdentifier(sg);
      cipi.setRelationshipType(cipi.getAmountRemainingType());
      cipi.setPaymentMethod(getPaymentGroupMapContainer().getDefaultPaymentGroupName());
      AmountInfo ai = sg.getPriceInfo();
      if (ai != null)
        cipi.setAmount(ai.getAmount());

      getCommerceIdentifierPaymentInfoContainer().addCommerceIdentifierPaymentInfo(sg.getId(), cipi);
    }
  }

  /**
   * The <code>initializeOrderPayment</code> method creates an OrderPaymentInfo, which
   * is a CommerceIdentifierPaymentInfo used for Order payment information.
   *
   * This also removes any CommerceIdentifierPaymentInfo objects previously associated
   * with the Order.
   *
   * @param pOrder an <code>Order</code> value
   */
  protected void initializeOrderPayment(Order pOrder)
  {
    getCommerceIdentifierPaymentInfoContainer().removeCommerceIdentifierPaymentInfos(pOrder.getId());
    CommerceIdentifierPaymentInfo cipi = new OrderPaymentInfo();

    cipi.setCommerceIdentifier(pOrder);
    cipi.setRelationshipType(cipi.getAmountRemainingType());
    cipi.setPaymentMethod(getPaymentGroupMapContainer().getDefaultPaymentGroupName());
    OrderPriceInfo opi = pOrder.getPriceInfo();
    if (opi != null)
      cipi.setAmount(opi.getTotal());

    getCommerceIdentifierPaymentInfoContainer().addCommerceIdentifierPaymentInfo(pOrder.getId(), cipi);
  }

  /**
   * The <code>initializeTaxPayment</code> method creates a TaxPaymentInfo, which
   * is a CommerceIdentifierPaymentInfo used for tax payment information.
   *
   * This also removes any CommerceIdentifierPaymentInfo objects previously associated
   * with the Order.
   *
   * @param pOrder an <code>Order</code> value
   */
  protected void initializeTaxPayment(Order pOrder)
  {
    getCommerceIdentifierPaymentInfoContainer().removeCommerceIdentifierPaymentInfos(pOrder.getId());
    CommerceIdentifierPaymentInfo cipi = new TaxPaymentInfo();

    cipi.setCommerceIdentifier(pOrder);
    cipi.setRelationshipType(cipi.getAmountRemainingType());
    cipi.setPaymentMethod(getPaymentGroupMapContainer().getDefaultPaymentGroupName());
    AmountInfo ai = pOrder.getTaxPriceInfo();
    if (ai != null)
      cipi.setAmount(ai.getAmount());

    getCommerceIdentifierPaymentInfoContainer().addCommerceIdentifierPaymentInfo(pOrder.getId(), cipi);
  }

  /**
   * The <code>initializeBasedOnOrder</code> method creates
   * CommerceIdentifierPaymentInfo objects that reflect the current payment
   * information in the order.
   *
   * @param pOrder an <code>Order</code> value
   */
  protected void initializeBasedOnOrder(Order pOrder)
  {
    // First, find any PaymentGroupCommerceItem relationships
    List commerceItems = pOrder.getCommerceItems();

    if (commerceItems != null) {
      Iterator itemIter = commerceItems.iterator();
      while(itemIter.hasNext()) {
        CommerceItem item = (CommerceItem) itemIter.next();
        initializeCommerceIdentifierPaymentInfos(item);
      }
    }

    // Next, the PaymentGroupShippingGroup relationships
    List shippingGroups = pOrder.getShippingGroups();

    if (shippingGroups != null) {
      Iterator shipIter = shippingGroups.iterator();
      while(shipIter.hasNext()) {
        ShippingGroup shipGroup = (ShippingGroup) shipIter.next();
        initializeCommerceIdentifierPaymentInfos(shipGroup);
      }
    }

    // Last, do the order
    initializeCommerceIdentifierPaymentInfos(pOrder);
  }

  /**
   *
   * This method initializes CommerceIdentifierPaymentInfo with the information available from order's relationship
   * objects.
   *
   * @param pContainer
   */
  public void initializeCommerceIdentifierPaymentInfos(PaymentGroupRelationshipContainer pContainer) {

    List paymentGroupRelationships = pContainer.getPaymentGroupRelationships();
    if (paymentGroupRelationships != null) {
      Iterator relIter = paymentGroupRelationships.iterator();

      while(relIter.hasNext()) {
        PaymentGroupRelationship rel = (PaymentGroupRelationship) relIter.next();

        PaymentGroup paymentGroup = rel.getPaymentGroup();
        if (getPaymentGroupManager().paymentGroupIsModifiable(paymentGroup)) {

    CommerceIdentifierPaymentInfo cipi = null;
          if (rel instanceof PaymentGroupOrderRelationship) {
      int relationshipType = ((PaymentGroupOrderRelationship) rel).getRelationshipType();
            if (relationshipType == RelationshipTypes.TAXAMOUNT ||
                relationshipType == RelationshipTypes.TAXAMOUNTREMAINING)
              cipi = new TaxPaymentInfo();
            else
        cipi = new OrderPaymentInfo();
          }
          else if (rel instanceof PaymentGroupCommerceItemRelationship)
      cipi = new CommerceItemPaymentInfo();
          else if (rel instanceof PaymentGroupShippingGroupRelationship)
      cipi = new ShippingGroupPaymentInfo();

          if (cipi == null) {
            if (isLoggingError()) logError("Could not identify relationship type, so cipi is null.");
          }
          else {
            cipi.setCommerceIdentifier((CommerceIdentifier) pContainer);
            cipi.setRelationshipType(RelationshipTypes.typeToString(rel.getRelationshipType()));

            String paymentGroupName = getPaymentGroupName(paymentGroup);
            if (paymentGroupName == null) {
              paymentGroupName = getNewPaymentGroupName(paymentGroup);
            }
            getPaymentGroupMapContainer().addPaymentGroup(paymentGroupName,paymentGroup);
            cipi.setPaymentMethod(paymentGroupName);
            cipi.setAmount(rel.getAmount());
            if (rel instanceof PaymentGroupCommerceItemRelationship)
              cipi.setQuantity(((PaymentGroupCommerceItemRelationship) rel).getQuantity());
            getCommerceIdentifierPaymentInfoContainer().addCommerceIdentifierPaymentInfo(((CommerceIdentifier) pContainer).getId(), cipi);
          }
        }
      }
    }
  }

  /**
   * This method iterates throug the payment groups and finds the matching payment group and its name.
   * @param pPaymentGroup
   * @return
   */
  public String getPaymentGroupName(PaymentGroup pPaymentGroup) {
    Collection matchers = getPaymentGroupInitializers().values();
    Iterator matcherIter = matchers.iterator();
    String paymentGroupName = null;
    while(paymentGroupName == null && matcherIter.hasNext()) {
      PaymentGroupMatcher matcher = (PaymentGroupMatcher) matcherIter.next();
      paymentGroupName = matcher.matchPaymentGroup(pPaymentGroup, getPaymentGroupMapContainer());
    }

    return paymentGroupName;
  }

  /**
   * This utility method gets the payment group name or nickname for the payment group that is passed in.
   * The nickname generator is available in  the CommerceProfileTools. Based on the rules setup in the nickname generator,
   * the nickname will be generator.
   *
   * Let us assume that a credit card has the following type and credit card number.
   * Credit card type and number is ==> Visa, 4111111111111111
   *
   * The nickname generator uses ( CreditCard.getCreditCardType()+ " - " + last four digits of CreditCard.getCreditCardNumber().
   *
   * @param pPaymentGroup
   * @return
   */
  public String getNewPaymentGroupName(PaymentGroup pPaymentGroup) {
    Collection matchers = getPaymentGroupInitializers().values();
    Iterator matcherIter = matchers.iterator();
    String paymentGroupName = null;
    while(paymentGroupName == null && matcherIter.hasNext()) {
      PaymentGroupMatcher matcher = (PaymentGroupMatcher) matcherIter.next();
      paymentGroupName = matcher.getNewPaymentGroupName(pPaymentGroup);
    }

    return paymentGroupName;
  }

  //--------------------------------------------------
  // Utility Methods
  //--------------------------------------------------

  /**
   * The <code>initializeRequestParameters</code> method gathers the necessary
   * input parameters and uses them to adjust initialization requirements.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   */
  protected void initializeRequestParameters(DynamoHttpServletRequest pRequest) {
    try {
      Order order = (Order) pRequest.getObjectParameter(ORDER);
      if (order != null) setOrder(order);
    } catch (ClassCastException cce) {
      if (isLoggingError()) {
  logError(ResourceUtils.getMsgResource("typeNotOrder", MY_RESOURCE_NAME, sResourceBundle));
        logError(cce);
      }
    }

    String clearAll = pRequest.getParameter(CLEAR_ALL);
    if (clearAll != null) setClearAll(Boolean.valueOf(clearAll).booleanValue());

    String clearPaymentInfos = pRequest.getParameter(CLEAR_PAYMENT_INFOS);
    if (clearPaymentInfos != null) setClearPaymentInfos(Boolean.valueOf(clearPaymentInfos).booleanValue());

    String clearPaymentGroups = pRequest.getParameter(CLEAR_PAYMENT_GROUPS);
    if (clearPaymentGroups != null) setClearPaymentGroups(Boolean.valueOf(clearPaymentGroups).booleanValue());

    String paymentGroupTypes = pRequest.getParameter(PAYMENT_GROUP_TYPES);
    if (paymentGroupTypes != null) setPaymentGroupTypes(paymentGroupTypes);

    String initPaymentGroups = pRequest.getParameter(INIT_PAYMENT_GROUPS);
    if (initPaymentGroups != null) setInitPaymentGroups(Boolean.valueOf(initPaymentGroups).booleanValue());

    String initItemPayment = pRequest.getParameter(INIT_ITEM_PAYMENT);
    if (initItemPayment != null) setInitItemPayment(Boolean.valueOf(initItemPayment).booleanValue());

    String initShippingPayment = pRequest.getParameter(INIT_SHIPPING_PAYMENT);
    if (initShippingPayment != null) setInitShippingPayment(Boolean.valueOf(initShippingPayment).booleanValue());

    String initOrderPayment = pRequest.getParameter(INIT_ORDER_PAYMENT);
    if (initOrderPayment != null) setInitOrderPayment(Boolean.valueOf(initOrderPayment).booleanValue());

    String initTaxPayment = pRequest.getParameter(INIT_TAX_PAYMENT);
    if (initTaxPayment != null) setInitTaxPayment(Boolean.valueOf(initTaxPayment).booleanValue());

    String initBasedOnOrder = pRequest.getParameter(INIT_BASED_ON_ORDER);
    if (initBasedOnOrder != null) setInitBasedOnOrder(Boolean.valueOf(initBasedOnOrder).booleanValue());

    String createAllPaymentInfos = pRequest.getParameter(CREATE_ALL_PAYMENT_INFOS);
    if (createAllPaymentInfos != null) setCreateAllPaymentInfos(Boolean.valueOf(createAllPaymentInfos).booleanValue());

  }

  /**
   * The <code>getDefaultPaymentGroupName</code> method returns the default
   * PaymentGroup based on the PaymentGroupMapContainer's default PaymentGroup
   * name. Any .jhtml form can manipulate this property directly.
   *
   * @return a <code>String</code> value
   */
  public PaymentGroup getDefaultPaymentGroup() {
    return getPaymentGroupMapContainer().getPaymentGroup(getPaymentGroupMapContainer().getDefaultPaymentGroupName());
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
   * <LI>commerceIdentifierPaymentInfoContainer
   * <LI>paymentGroupMapContainer
   * <LI>profile
   * </UL>
   **/
  protected void copyConfiguration() {
    if (mConfiguration != null) {
      if (getCommerceIdentifierPaymentInfoContainer() == null) {
        setCommerceIdentifierPaymentInfoContainer(mConfiguration.getCommerceIdentifierPaymentInfoContainer());
      }
      if (getPaymentGroupMapContainer() == null) {
        setPaymentGroupMapContainer(mConfiguration.getPaymentGroupMapContainer());
      }
      if (getProfile() == null && mConfiguration.getProfile() instanceof Profile) {
        setProfile((Profile)mConfiguration.getProfile());
      }
      if (getPaymentGroupManager() == null) {
        setPaymentGroupManager(mConfiguration.getPaymentGroupManager());
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

  //--------------------------------------------------
  // Service Method
  //--------------------------------------------------

  /**
   * This <code>service</code> method is used to initialize PaymentGroups and
   * CommerceIdentifierPaymentInfo objects. Initialization is based on input
   * parameters describing which PaymentGroups and CommerceIdentifierPaymentInfo
   * objects, if any, should be created and added to the appropriate container.
   *
   * The resulting Collections of PaymentGroups and CommerceIdentifierPaymentInfos
   * are exposed via output parameters.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest,
                      DynamoHttpServletResponse pResponse)
      throws ServletException, IOException
  {
    // get the necessary input parameters
    initializeRequestParameters(pRequest);

    if (getOrder() == null) {
      if (isLoggingError()) {
        logError(ResourceUtils.getMsgResource("nullOrder",
                                              MY_RESOURCE_NAME, sResourceBundle));
      }
      return;
    }
    if (getProfile() == null) {
      if (isLoggingError()) {
        logError(ResourceUtils.getMsgResource("nullProfile",
                                              MY_RESOURCE_NAME, sResourceBundle));
      }
      return;
    }

    // initialize user paymentmethods
    initializeUserPaymentMethods (pRequest, getProfile());
    // initialize CommerceIdentifierPaymentInfos for Order
    initializePaymentInfos(pRequest, getOrder());

    consolidatePaymentInfos(pRequest, pResponse);

    // set output parameters and service output
    pRequest.setParameter(PAYMENT_GROUPS, getPaymentGroupMapContainer().getPaymentGroupMap());
    pRequest.setParameter(CIPI_MAP, getCommerceIdentifierPaymentInfoContainer().getCommerceIdentifierPaymentInfoMap());
    pRequest.setParameter(ORDER, getOrder());
    pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
  }

  /**
   * <code>consolidatePaymentInfos</code> consolidates CommerceIdentifierPaymentInfos
   * by ensuring there is no redundant data.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void consolidatePaymentInfos(DynamoHttpServletRequest pRequest,
                                         DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    CommerceIdentifierPaymentInfo cipi;
    Map tempMap = new HashMap();
    CommerceIdentifierPaymentInfoContainer container = getCommerceIdentifierPaymentInfoContainer();
    Map cipiMap = container.getCommerceIdentifierPaymentInfoMap();
    Map synchronizedMap = Collections.synchronizedMap(cipiMap);
    Set set = synchronizedMap.keySet();

    synchronized(synchronizedMap) {
      Iterator iter = set.iterator();

      while (iter.hasNext()) { // for each CommerceIDentifier

        // clear our helper Map
        tempMap.clear();

        // current commerceIdentifierId
        String commerceIdentifierId = (String) iter.next();

        // List of CommerceIdentifierPaymentInfos for commerceIdentifierId
        List cipiList = (List) cipiMap.get(commerceIdentifierId);

        Iterator listIter = cipiList.listIterator();
        while (listIter.hasNext()) { // for each info object

          // current CommerceIdentifierPaymentInfo
          cipi = (CommerceIdentifierPaymentInfo) listIter.next();

          // if amount is reasonable then proceed
          if ((cipi.getAmount() >= 0) || (cipiList.size() == 1)) {
            // what's paying for this?
            String pg = cipi.getPaymentMethod();
            // is something else paying for this?
            Object obj = tempMap.get(pg);
            if (obj != null) {
              // if yes, then merge the 2 into one CommerceIdentifierPaymentInfo
              cipi = mergeCommerceIdentifierPaymentInfos(cipi,
                                                         (CommerceIdentifierPaymentInfo) obj);
            }
            // now put the info into the helper Map
            tempMap.put(pg, cipi);
          }
        }
        // remove all old CommerceIdentifierPaymentInfos for this CommerceIdentifierId
        container.removeCommerceIdentifierPaymentInfos(commerceIdentifierId);

        // add new CommerceIdentifierPaymentInfos from helper Map
        Iterator valuesIter = tempMap.values().iterator();
        while (valuesIter.hasNext()) {
          cipi = (CommerceIdentifierPaymentInfo) valuesIter.next();
          container.addCommerceIdentifierPaymentInfo (commerceIdentifierId, cipi);
        }
      }
    }
  }

  /**
   * <code>mergeCommerceIdentifierPaymentInfos</code> merges 2 CommerceIdentifierPaymentInfos
   * into one by combining their amounts and quantities.
   *
   * @param cipi1 a <code>CommerceIdentifierPaymentInfo</code> value
   * @param cipi2 a <code>CommerceIdentifierPaymentInfo</code> value
   * @return a <code>CommerceIdentifierPaymentInfo</code> value
   */
  protected CommerceIdentifierPaymentInfo mergeCommerceIdentifierPaymentInfos(CommerceIdentifierPaymentInfo cipi1,
                                                                              CommerceIdentifierPaymentInfo cipi2) {

    if (cipi2.getRelationshipType() == cipi2.getAmountRemainingType())
      cipi1.setRelationshipType(cipi1.getAmountRemainingType());

    // merge 2 CommerceIdentifierPaymentInfos by adding their amounts and quantities
    cipi1.setAmount(cipi1.getAmount() + cipi2.getAmount());
    cipi1.setQuantity(cipi1.getQuantity() + cipi2.getQuantity());
    return cipi1;
  }

  /**
   *
   *  This method checks to see if there is any payment infos. If there is any cipi in the container, this method
   *  returns true otherwise false. The cipi could be in any level. The cipi could be in the Order, CommerceItem, ShippingGroup
   *  or Tax level. If any cipi found this method returns true and if no cipi found, then this method returns false.
   *
   * @return true/false based on the the existance of any cipi in the container
   *
   */
  public boolean isAnyPaymentInfoExistsInContainter () {

   if (isLoggingDebug()) {
     logDebug ("Entering isAnyPaymentInfoExistsInContainter()");
   }
   boolean paymentInfoExists = false;
   CommerceIdentifierPaymentInfoContainer cipicontainter = getCommerceIdentifierPaymentInfoContainer();
   Map allcipi = cipicontainter.getCommerceIdentifierPaymentInfoMap();

   if (allcipi == null || allcipi.isEmpty()) {
     if (isLoggingDebug()) {
       logDebug ("CommerceIdentifierPaymentInfoContainer does not have any cipis.");
     }
     return false;
   }

   Iterator cipiIterator = allcipi.keySet().iterator();
   while (cipiIterator.hasNext()) {
     String commerceIdentifierId = (String) cipiIterator.next();
     List cipiList = cipicontainter.getCommerceIdentifierPaymentInfos(commerceIdentifierId);
     if (cipiList != null
         && cipiList.size() > 0) {
       if (isLoggingDebug()) {
         logDebug ("cipi list is found.");
       }
       paymentInfoExists = true;
       break;
     }
   }//end of while
   return paymentInfoExists;
  }

}   // end of class
