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

package atg.b2bcommerce.order.purchase;

import atg.b2bcommerce.order.*;
import atg.b2bcommerce.profile.B2BCommercePropertyManager;
import atg.commerce.order.*;
import atg.commerce.CommerceException;
import atg.beans.*;
import atg.userprofiling.Profile;
import atg.servlet.*;
import atg.repository.RepositoryItem;
import atg.core.util.ResourceUtils;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;


/**
 * <p>The <code>CostCenterDroplet</code> is a DynamoServlet that is used to initialize
 * CostCenters and CommerceIdentifierCostCenter objects for use by the CostCenterFormHandler.
 * the CostCenterDroplet is composed of the following containers:
 * <p>
 * CostCenterMapContainer - container for the user's named CostCenters
 * <p>
 * CommerceIdentifierCostCenterContainer - container for the user's CommerceIdentifierCostCenter
 * objects for a particular Order's CommerceIdentifiers
 * <p>
 * The service method ensures the following:
 * <p>
 * CostCenter initialization - the user's CostCenters are created and added to
 * the CostCenterMapContainer. Once this is done the user can begin to select from these
 * during the checkout process.
 * <p>
 * CommerceIdentifierCostCenter initialization - the Order CommerceIdentifiers of interest
 * are used to instantiate CommerceIdentifierCostCenter objects, and these are added
 * to the CommerceIdentifierCostCenterContainer.
 * <p>
 * A complete description of the parameters to the CostCenterDroplet are:
 *
 * <dl>
 *
 * <dt>order
 * <dd>This parameter may be used to override the component's default setting for
 * the user's order. For convenience the user's Order is also exposed as an output parameter.
 *
 * <dt>clearCostCenterContainer
 * <dd>When set to true this parameter will clear the user's CommerceIdentifierCostCenterContainer.
 * This should be done at least once per new order.
 *
 * <dt>clearCostCenterMap
 * <dd>When set to true this parameter will clear the user's CostCenterMapContainer.
 *
 * <dt>clear
 * <dd>When set to true this parameter will clear both the CommerceIdentifierCostCenterContainer
 * and the CostCenterMapContainer.
 *
 * <dt>initCostCenters
 * <dd>When this parameter is set to true, the user's CostCenters will be initialized and added
 * to the CostCenterMapContainer.
 *
 * <dt>loadCostCenters
 * <dd>When set to true this parameter will create a CommerceIdentifierCostCenter for each CostCenter
 * relationship in the Order. These are then added to the CommerceIdentifierCostCenterContainer.
 *
 * <dt>initItemCostCenters
 * <dd>When set to true this parameter will create a CommerceIdentifierCostCenter for each CommerceItem
 * in the Order. These are then added to the CommerceIdentifierCostCenterContainer.
 *
 * <dt>initShippingCostCenters
 * <dd>When set to true this parameter will create a CommerceIdentifierCostCenter for each ShippingGroup
 * in the Order. These are then added to the CommerceIdentifierCostCenterContainer.
 *
 * <dt>initTaxCostCenters
 * <dd>When set to true this parameter will create a CommerceIdentifierCostCenter for the tax and add it to the
 * CommerceIdentifierCostCenterContainer.
 *
 * <dt>initOrderCostCenters
 * <dd>When set to true this parameter will create a CommerceIdentifierCostCenter for the Order and add it to the
 * CommerceIdentifierCostCenterContainer.
 *
 * <dt>costCenters
 * <dd>This output parameter is set to the Map referenced by the CostCenterMapContainer.
 *
 * <dt>ciccMap
 * <dd>This output parameter is set to the Map refereneced by the CommerceIdentifierCostCenterMap.
 *
 * <dt>useAmount
 * <dd>When set to true this parameter will create CostCenterCommerceItem relationships of type CCAMOUNT.
 * Otherwise the relationships are of type CCQUANTITY.
 *
 * </dl>
 *
 * @author Paul O'Brien
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/purchase/CostCenterDroplet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class CostCenterDroplet
  extends DynamoServlet
  implements CostCenterConstants
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/purchase/CostCenterDroplet.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  static final String MY_RESOURCE_NAME = "atg.b2bcommerce.order.OrderResources";

  /** Resource Bundle **/
  protected static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //---------------------------------------------------------------------------
  // property: getOrderFromId
  //---------------------------------------------------------------------------
  boolean mGetOrderFromId = false;

  /**
   * Set the GetOrderFromId property.
   * @param pGetOrderFromId a <code>boolean</code> value
   * @beaninfo description: boolean describing how to find the order to operate on.
   */
  public void setGetOrderFromId(boolean pGetOrderFromId) {
    mGetOrderFromId = pGetOrderFromId;
  }

  /**
   * Return the GetOrderFromId property.
   * @return a <code>boolean</code> value
   */
  public boolean isGetOrderFromId() {
    return mGetOrderFromId;
  }

  //---------------------------------------------------------------------------
  // property: OrderId
  //---------------------------------------------------------------------------
  String mOrderId;

  /**
   * Set the OrderId property.
   * @param pOrderId a <code>String</code> value
   * @beaninfo description: The Order Id.
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
  // property: OrderManager
  //---------------------------------------------------------------------------
  SimpleOrderManager mOrderManager;

  /**
   * Set the OrderManager property.
   * @param pOrderManager a <code>SimpleOrderManager</code> value
   * @beaninfo description: The Order Manager
   */
  public void setOrderManager(SimpleOrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  /**
   * Return the OrderManager property.
   * @return a <code>SimpleOrderManager</code> value
   */
  public SimpleOrderManager getOrderManager() {
    return mOrderManager;
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
   * Overriding getOrder to use the getOrderFromId property
   * Return the Order property.
   * @return an <code>Order</code> value
   */
  public Order getOrder() {
    if (mOrder != null) {
      return mOrder;
    }
    else if (isGetOrderFromId()) {
      try {
	setOrder(getOrderManager().loadOrder(getOrderId()));
	return mOrder;
      }
      catch (CommerceException e) {
	logError(e);
	return null;
      }
    }
    else {
      return mOrder;
    }
  }

  //---------------------------------------------------------------------------
  // property: Profile
  //---------------------------------------------------------------------------
  RepositoryItem mProfile;

  /**
   * Set the Profile property.
   * @param pProfile a <code>Profile</code> value
   */
  public void setProfile(RepositoryItem pProfile) {
    mProfile = pProfile;
  }

  /**
   * Return the Profile property.
   * @return a <code>Profile</code> value
   */
  public RepositoryItem getProfile() {
    return mProfile;
  }

  //---------------------------------------------------------------------------
  // property: ClearCostCenterContainer
  //---------------------------------------------------------------------------
  boolean mClearCostCenterContainer;

  /**
   * Set the ClearCostCenterContainer property.
   * @param pClearCostCenterContainer a <code>boolean</code> value
   */
  public void setClearCostCenterContainer(boolean pClearCostCenterContainer) {
    mClearCostCenterContainer = pClearCostCenterContainer;
  }

  /**
   * Return the ClearCostCenterContainer property.
   * @return a <code>boolean</code> value
   */
  public boolean isClearCostCenterContainer() {
    return mClearCostCenterContainer;
  }

  //---------------------------------------------------------------------------
  // property: ClearCostCenterMap
  //---------------------------------------------------------------------------
  boolean mClearCostCenterMap;

  /**
   * Set the ClearCostCenterMap property.
   * @param pClearCostCenterMap a <code>boolean</code> value
   */
  public void setClearCostCenterMap(boolean pClearCostCenterMap) {
    mClearCostCenterMap = pClearCostCenterMap;
  }

  /**
   * Return the ClearCostCenterMap property.
   * @return a <code>boolean</code> value
   */
  public boolean isClearCostCenterMap() {
    return mClearCostCenterMap;
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
  // property: InitCostCenters
  //---------------------------------------------------------------------------
  boolean mInitCostCenters;

  /**
   * Set the InitCostCenters property.
   * @param pInitCostCenters a <code>boolean</code> value
   */
  public void setInitCostCenters(boolean pInitCostCenters) {
    mInitCostCenters = pInitCostCenters;
  }

  /**
   * Return the InitCostCenters property.
   * @return a <code>boolean</code> value
   */
  public boolean isInitCostCenters() {
    return mInitCostCenters;
  }

  //---------------------------------------------------------------------------
  // property: InitItemCostCenters
  //---------------------------------------------------------------------------
  boolean mInitItemCostCenters;

  /**
   * Set the InitItemCostCenters property.
   * @param pInitItemCostCenters a <code>boolean</code> value
   */
  public void setInitItemCostCenters(boolean pInitItemCostCenters) {
    mInitItemCostCenters = pInitItemCostCenters;
  }

  /**
   * Return the InitItemCostCenters property.
   * @return a <code>boolean</code> value
   */
  public boolean isInitItemCostCenters() {
    return mInitItemCostCenters;
  }

  //---------------------------------------------------------------------------
  // property: LoadCostCenters
  //---------------------------------------------------------------------------
  boolean mLoadCostCenters;

  /**
   * Set the LoadCostCenters property.
   * @param pLoadCostCenters a <code>boolean</code> value
   */
  public void setLoadCostCenters(boolean pLoadCostCenters) {
    mLoadCostCenters = pLoadCostCenters;
  }

  /**
   * Return the LoadCostCenters property.
   * @return a <code>boolean</code> value
   */
  public boolean isLoadCostCenters() {
    return mLoadCostCenters;
  }

  //---------------------------------------------------------------------------
  // property: SingleCostCenter
  //---------------------------------------------------------------------------
  String mSingleCostCenter;

  /**
   * Set the SingleCostCenter property.
   * @param pSingleCostCenter a <code>CostCenter</code> value
   */
  public void setSingleCostCenter(String pSingleCostCenter) {
    mSingleCostCenter = pSingleCostCenter;
  }

  /**
   * Return the SingleCostCenter property.
   * @return a <code>CostCenter</code> value
   */
  public String getSingleCostCenter() {
    return mSingleCostCenter;
  }

  //----------------------------------------------------------------------------
  // property: useAmount
  //----------------------------------------------------------------------------
  boolean mUseAmount = false;

  /**
   * Set the useAmount property.  If "true", then by default CostCenterCommerceItem
   * relationships are created of type CCAMOUNT.  If false, they are created
   * of type CCQUANTITY.
   * @param pUseAmount a <code>boolean</code> value
   */
  public void setUseAmount(boolean pUseAmount) {
    mUseAmount = pUseAmount;
  }

  /**
   * Return the useAmount property.
   * @return a <code>String</code> value
   */
  public boolean isUseAmount() {
    return mUseAmount;
  } 
   
  //---------------------------------------------------------------------------
  // property: InitShippingCostCenters
  //---------------------------------------------------------------------------
  boolean mInitShippingCostCenters;

  /**
   * Set the InitShippingCostCenters property.
   * @param pInitShippingCostCenters a <code>boolean</code> value
   */
  public void setInitShippingCostCenters(boolean pInitShippingCostCenters) {
    mInitShippingCostCenters = pInitShippingCostCenters;
  }

  /**
   * Return the InitShippingCostCenters property.
   * @return a <code>boolean</code> value
   */
  public boolean isInitShippingCostCenters() {
    return mInitShippingCostCenters;
  }

  //---------------------------------------------------------------------------
  // property: InitOrderCostCenters
  //---------------------------------------------------------------------------
  boolean mInitOrderCostCenters;

  /**
   * Set the InitOrderCostCenters property.
   * @param pInitOrderCostCenters a <code>boolean</code> value
   */
  public void setInitOrderCostCenters(boolean pInitOrderCostCenters) {
    mInitOrderCostCenters = pInitOrderCostCenters;
  }

  /**
   * Return the InitOrderCostCenters property.
   * @return a <code>boolean</code> value
   */
  public boolean isInitOrderCostCenters() {
    return mInitOrderCostCenters;
  }

  //---------------------------------------------------------------------------
  // property: InitTaxCostCenters
  //---------------------------------------------------------------------------
  boolean mInitTaxCostCenters;

  /**
   * Set the InitTaxCostCenters property.
   * @param pInitTaxCostCenters a <code>boolean</code> value
   */
  public void setInitTaxCostCenters(boolean pInitTaxCostCenters) {
    mInitTaxCostCenters = pInitTaxCostCenters;
  }

  /**
   * Return the InitTaxCostCenters property.
   * @return a <code>boolean</code> value
   */
  public boolean isInitTaxCostCenters() {
    return mInitTaxCostCenters;
  }

  //---------------------------------------------------------------------------
  // property: CommerceIdentifierCostCenterContainer
  //---------------------------------------------------------------------------
  CommerceIdentifierCostCenterContainer mCommerceIdentifierCostCenterContainer;

  /**
   * Set the CommerceIdentifierCostCenterContainer property.
   * @param pCommerceIdentifierCostCenterContainer a <code>CommerceIdentifierCostCenterContainer</code> value
   */
  public void setCommerceIdentifierCostCenterContainer(CommerceIdentifierCostCenterContainer pCommerceIdentifierCostCenterContainer) {
    mCommerceIdentifierCostCenterContainer = pCommerceIdentifierCostCenterContainer;
  }

  /**
   * Return the CommerceIdentifierCostCenterContainer property.
   * @return a <code>CommerceIdentifierCostCenterContainer</code> value
   */
  public CommerceIdentifierCostCenterContainer getCommerceIdentifierCostCenterContainer() {
    return mCommerceIdentifierCostCenterContainer;
  }

  //---------------------------------------------------------------------------
  // property: CostCenterMapContainer
  //---------------------------------------------------------------------------
  CostCenterMapContainer mCostCenterMapContainer;

  /**
   * Set the CostCenterMapContainer property.
   * @param pCostCenterMapContainer a <code>CostCenterMapContainer</code> value
   */
  public void setCostCenterMapContainer(CostCenterMapContainer pCostCenterMapContainer) {
    mCostCenterMapContainer = pCostCenterMapContainer;
  }

  /**
   * Return the CostCenterMapContainer property.
   * @return a <code>CostCenterMapContainer</code> value
   */
  public CostCenterMapContainer getCostCenterMapContainer() {
    return mCostCenterMapContainer;
  }

  //---------------------------------------------------------------------------
  // property: CostCenterManager
  //---------------------------------------------------------------------------
  CostCenterManager mCostCenterManager;

  /**
   * Set the CostCenterManager property.
   * @param pCostCenterManager a <code>CostCenterManager</code> value
   */
  public void setCostCenterManager(CostCenterManager pCostCenterManager) {
    mCostCenterManager = pCostCenterManager;
  }

  /**
   * Return the CostCenterManager property.
   * @return a <code>CostCenterManager</code> value
   */
  public CostCenterManager getCostCenterManager() {
    return mCostCenterManager;
  }

  //---------------------------------------------------------------------------
  // property: PropertyManager
  //---------------------------------------------------------------------------
  B2BCommercePropertyManager mPropertyManager;

  /**
   * Set the PropertyManager property.
   * @param pPropertyManager a <code>B2BCommercePropertyManager</code> value
   */
  public void setPropertyManager(B2BCommercePropertyManager pPropertyManager) {
    mPropertyManager = pPropertyManager;
  }

  /**
   * Return the PropertyManager property.
   * @return a <code>B2BCommercePropertyManager</code> value
   */
  public B2BCommercePropertyManager getPropertyManager() {
    return mPropertyManager;
  }

  //---------------------------------------------------------------------------
  // property: CostCentersPropertyName
  //---------------------------------------------------------------------------
  String mCostCentersPropertyName;

  /**
   * Set the CostCentersPropertyName property.
   * @param pCostCentersPropertyName a <code>String</code> value
   */
  public void setCostCentersPropertyName(String pCostCentersPropertyName) {
    mCostCentersPropertyName = pCostCentersPropertyName;
  }

  /**
   * Return the CostCentersPropertyName property.
   * @return a <code>String</code> value
   */
  public String getCostCentersPropertyName() {
    return mCostCentersPropertyName;
  }

  //---------------------------------------------------------------------------
  // property: DefaultCostCenterPropertyName
  //---------------------------------------------------------------------------
  String mDefaultCostCenterPropertyName;

  /**
   * Set the DefaultCostCenterPropertyName property.
   * @param pDefaultCostCenterPropertyName a <code>String</code> value
   */
  public void setDefaultCostCenterPropertyName(String pDefaultCostCenterPropertyName) {
    mDefaultCostCenterPropertyName = pDefaultCostCenterPropertyName;
  }

  /**
   * Return the DefaultCostCenterPropertyName property.
   * @return a <code>String</code> value
   */
  public String getDefaultCostCenterPropertyName() {
    return mDefaultCostCenterPropertyName;
  }

  //---------------------------------------------------------------------------
  // property: ContainsCCOrderRel
  //---------------------------------------------------------------------------
  boolean mContainsCCOrderRel;

  /**
   * Set the ContainsCCOrderRel property.
   * @param pContainsCCOrderRel a <code>boolean</code> value
   */
  public void setContainsCCOrderRel(boolean pContainsCCOrderRel) {
    mContainsCCOrderRel = pContainsCCOrderRel;
  }

  /**
   * Return the ContainsCCOrderRel property.
   * @return a <code>boolean</code> value
   */
  public boolean isContainsCCOrderRel() {
    return mContainsCCOrderRel;
  }

  //---------------------------------------------------------------------------
  // property: RelationshipCount
  //---------------------------------------------------------------------------
  int mRelationshipCount;

  /**
   * Set the RelationshipCount property.
   * @param pRelationshipCoiunt a <code>int</code> value
   */
  public void setRelationshipCount(int pRelationshipCount) {
    mRelationshipCount = pRelationshipCount;
  }

  /**
   * Return the RelationshipCount property.
   * @return a <code>int</code> value
   */
  public int getRelationshipCount() {
    return mRelationshipCount;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>CostCenterDroplet</code> instance.
   *
   */
  public CostCenterDroplet () {}

  //--------------------------------------------------
  // Initialization Code
  //--------------------------------------------------

  /**
   * <code>initializeUserCostCenters</code> is used to add the user's
   * CostCenters to the CostCenterMapContainer. This invokes the
   * <code>initializeCostCenters</code> method.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pProfile a <code>Profile</code> value
   */
  protected void initializeUserCostCenters (DynamoHttpServletRequest pRequest,
					    RepositoryItem pProfile)
  {
    if (isClearAll() || isClearCostCenterMap()) {
      getCostCenterMapContainer().removeAllCostCenters();
      if (isLoggingDebug())
        logDebug("CostCenterDroplet removing all CostCenters from map");
    }
    
    if (isInitCostCenters()) {
      initializeCostCenters(pProfile);
    }
  }

  protected String mInitializeCostCenterIdentifier=null;
  /**
   * Returns the cost center identifier that was created by <code>initializeCostCenters</code> and assigned 
   * as the cost center name to all the <code>CommerceIdentifierCostCenter</code> objects when they were first initialized.
   * 
   * @return the cost center identifier that was created by <code>initializeCostCenters</code> and assigned 
   * as the cost center name to all the <code>CommerceIdentifierCostCenter</code> objects when they were first initialized.
   */
  public String getInitializeCostCenterIdentifier()
  {
      return mInitializeCostCenterIdentifier;
  }
  /**
   * <code>initializeCostCenters</code> uses the CostCenterManager to create
   * the user's CostCenters based on the Profile properties given by
   * <code>getCostCentersPropertyName</code> and <code>getDefaultCostCenterPropertyName</code>.
   * <p>
   * All of these CostCenters are added to the <code>CostCenterMapContainer</code>.
   *
   * @param pProfile a <code>Profile</code> value
   */
  protected void initializeCostCenters (RepositoryItem pProfile)
  {
      B2BCommercePropertyManager cpmgr = (B2BCommercePropertyManager)getPropertyManager();
      CostCenter costCenter = null;

      try
      {
          String costCenterKey=null;
          List costCenters = (List) DynamicBeans.getSubPropertyValue(pProfile, getCostCentersPropertyName());
          Iterator iter = costCenters.iterator();
          while (iter.hasNext()) 
          {
              RepositoryItem item = (RepositoryItem) iter.next();
              costCenterKey = (String) item.getPropertyValue(cpmgr.getCostCenterIdentifierPropertyName());
              String costCenterDescription = (String) item.getPropertyValue(cpmgr.getCostCenterDescriptionPropertyName());
              costCenter = getCostCenterManager().createCostCenter(costCenterKey);
              costCenter.setDescription(costCenterDescription);
              getCostCenterMapContainer().addCostCenter(costCenterKey, costCenter);
          }

          RepositoryItem defaultCostCenter = (RepositoryItem) DynamicBeans.getSubPropertyValue(pProfile, getDefaultCostCenterPropertyName());
          if (defaultCostCenter != null)
          {
              String defaultCostCenterKey = (String) defaultCostCenter.getPropertyValue(cpmgr.getCostCenterIdentifierPropertyName());
              //if the default cost center wasn't already added, add it now. 
              if(getCostCenterMapContainer().getCostCenter(defaultCostCenterKey) == null)
              {   
                  String costCenterDescription = (String) defaultCostCenter.getPropertyValue(cpmgr.getCostCenterDescriptionPropertyName());
                  costCenter = getCostCenterManager().createCostCenter(defaultCostCenterKey);
                  costCenter.setDescription(costCenterDescription);
                  getCostCenterMapContainer().addCostCenter(defaultCostCenterKey, costCenter);
              }
              //set the default cost center name 
              getCostCenterMapContainer().setDefaultCostCenterName(defaultCostCenterKey);
              mInitializeCostCenterIdentifier = defaultCostCenterKey;
          }
          else
              mInitializeCostCenterIdentifier = costCenterKey;
      
      } catch (PropertyNotFoundException pnfe) {
          if (isLoggingError()) {
              logError(pnfe);
      }
      } catch (CommerceException exc) {
        if (isLoggingError()) {
            logError(exc);
      }
    }
  }

  /**
   * <code>initializeCommerceIdentifierCostCenters</code> creates CommerceIdentifierCostCenter objects
   * for the Order, the Order's CommerceItems, and the Order's ShippingGroups.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pOrder an <code>Order</code> value
   */
  protected void initializeCommerceIdentifierCostCenters (DynamoHttpServletRequest pRequest,
                                                          Order pOrder)
  {
    if (isClearAll() || isClearCostCenterContainer()) {
      getCommerceIdentifierCostCenterContainer().removeAllCommerceIdentifierCostCenters();
      if (isLoggingDebug())
        logDebug("CostCenterDroplet removing all CostCenters from container");
    }
    
    setRelationshipCount(0);
    setContainsCCOrderRel(false);
    
    if (isLoadCostCenters()) {
      loadItemCostCenters(pOrder);
      loadShippingCostCenters(pOrder);
      loadOrderCostCenters(pOrder);
    }
    else {
     if (isInitItemCostCenters())
      initializeItemCostCenters(pOrder);
     if (isInitShippingCostCenters())
      initializeShippingCostCenters(pOrder);
     if (isInitOrderCostCenters())
      initializeOrderCostCenters(pOrder);
     if (isInitTaxCostCenters())
      initializeTaxCostCenters(pOrder);
    }
     
  }

  /**
   * The <code>initializeItemCostCenters</code> method creates a
   * <code>CommerceIdentifierCostCenter</code> for each <code>CommerceItem</code>
   * in the <code>Order</code>, and adds them to the <code>CommerceIdentifierCostCenterContainer</code>.
   *
   * @param pOrder an <code>Order</code> value
   */
  protected void initializeItemCostCenters(Order pOrder)
  {
    List pCommerceIdentifiers = pOrder.getCommerceItems();
    Iterator iter = pCommerceIdentifiers.iterator();
    CommerceItem item = null;
    CommerceIdentifierCostCenter cicc;

    while (iter.hasNext()) {
      setRelationshipCount(getRelationshipCount() + 1);
      item = (CommerceItem) iter.next();

      cicc = new CommerceIdentifierCostCenter();
      cicc.setCommerceIdentifier(item);
      if (isUseAmount()) {
        cicc.setRelationshipType(B2BRelationshipTypes.CCAMOUNT_STR);
        if(item.getPriceInfo() != null)
            cicc.setAmount(item.getPriceInfo().getAmount());
        else
            cicc.setAmount(0);
      } else {
	cicc.setRelationshipType(B2BRelationshipTypes.CCQUANTITY_STR);
	cicc.setQuantity(item.getQuantity());
      }
      cicc.setCostCenterName(mInitializeCostCenterIdentifier);

      getCommerceIdentifierCostCenterContainer().addCommerceIdentifierCostCenter(item.getId(), cicc);
    }
  }

  /**
   * The <code>loadItemCostCenters</code> method creates a
   * <code>CommerceIdentifierCostCenter</code> for each <code>CostCenterCommerceItemRelationship</code>
   * in the <code>Order</code>, and adds them to the <code>CommerceIdentifierCostCenterContainer</code>.
   *
   * @param pOrder an <code>Order</code> value
   */
  protected void loadItemCostCenters(Order pOrder)
  {
    List pRels = pOrder.getRelationships();
    List pRelationships = pOrder.getRelationships();
    Iterator iter = pRelationships.iterator();
    Relationship rel = null;
    CommerceIdentifierCostCenter cicc;

    while (iter.hasNext()) {
      rel = (Relationship) iter.next();
      if (rel instanceof CostCenterCommerceItemRelationship) {
	setRelationshipCount(getRelationshipCount() + 1);
	CostCenterCommerceItemRelationship cccirel = (CostCenterCommerceItemRelationship) rel;
	cicc = new CommerceIdentifierCostCenter();
        cicc.setCommerceIdentifier(cccirel.getCommerceItem());
        if (isUseAmount()) {
          cicc.setRelationshipType(B2BRelationshipTypes.CCAMOUNT_STR);
          cicc.setAmount(cccirel.getAmount());
        } else {
	  cicc.setRelationshipType(B2BRelationshipTypes.CCQUANTITY_STR);
          cicc.setQuantity(cccirel.getQuantity());
        }
        cicc.setCostCenterName(cccirel.getCostCenter().getIdentifier());

        getCommerceIdentifierCostCenterContainer().addCommerceIdentifierCostCenter(cccirel.getCommerceItem().getId(), cicc);
      }
    }
  }
  
  /**
   * The <code>initializeItemCostCenters</code> method creates a
   * <code>CommerceIdentifierCostCenter</code> for each <code>ShippingGroup</code>
   * in the <code>Order</code>, and adds them to the <code>CommerceIdentifierCostCenterContainer</code>.
   *
   * @param pOrder an <code>Order</code> value
   */
  protected void initializeShippingCostCenters(Order pOrder)
  {
    List pCommerceIdentifiers = pOrder.getShippingGroups();
    Iterator iter = pCommerceIdentifiers.iterator();
    ShippingGroup sg = null;
    CommerceIdentifierCostCenter cicc;

    while (iter.hasNext()) {
      setRelationshipCount(getRelationshipCount() + 1);
      sg = (ShippingGroup) iter.next();

      cicc = new CommerceIdentifierCostCenter();
      cicc.setCommerceIdentifier(sg);
      cicc.setRelationshipType(B2BRelationshipTypes.CCSHIPPINGAMOUNT_STR);
      cicc.setCostCenterName(mInitializeCostCenterIdentifier);
      if(sg.getPriceInfo() != null)
          cicc.setAmount(sg.getPriceInfo().getAmount());
      else
          cicc.setAmount(0);
          

      getCommerceIdentifierCostCenterContainer().addCommerceIdentifierCostCenter(sg.getId(), cicc);
    }
  }

  /**
   * The <code>loadShippingCostCenters</code> method creates a
   * <code>CommerceIdentifierCostCenter</code> for each <code>CostCenterShippingGroup</code>
   * in the <code>Order</code>, and adds them to the <code>CommerceIdentifierCostCenterContainer</code>.
   *
   * @param pOrder an <code>Order</code> value
   */
  protected void loadShippingCostCenters(Order pOrder)
  {
    List pRels = pOrder.getRelationships();
    List pRelationships = pOrder.getRelationships();
    Iterator iter = pRelationships.iterator();
    Relationship rel = null;
    CommerceIdentifierCostCenter cicc;

    while (iter.hasNext()) {
      rel = (Relationship) iter.next();
      if (rel instanceof CostCenterShippingGroupRelationship) {
	setRelationshipCount(getRelationshipCount() + 1);
	CostCenterShippingGroupRelationship ccsgrel = (CostCenterShippingGroupRelationship) rel;
	cicc = new CommerceIdentifierCostCenter();
        cicc.setCommerceIdentifier(ccsgrel.getShippingGroup());
	cicc.setRelationshipType(B2BRelationshipTypes.CCSHIPPINGAMOUNT_STR);
	cicc.setAmount(ccsgrel.getAmount());
        cicc.setCostCenterName(ccsgrel.getCostCenter().getIdentifier());

        getCommerceIdentifierCostCenterContainer().addCommerceIdentifierCostCenter(ccsgrel.getShippingGroup().getId(), cicc);
      }
    }
  }
  
  /**
   * The <code>initializeItemCostCenters</code> method creates a
   * <code>CommerceIdentifierCostCenter</code> for the <code>Order</code>,
   * and adds it to the <code>CommerceIdentifierCostCenterContainer</code>.
   *
   * @param pOrder an <code>Order</code> value
   */
  protected void initializeOrderCostCenters(Order pOrder)
  {
    setRelationshipCount(getRelationshipCount() + 1);
    setContainsCCOrderRel(true);
    CommerceIdentifierCostCenter cicc = new CommerceIdentifierCostCenter();
    cicc.setCommerceIdentifier(pOrder);
    cicc.setRelationshipType(B2BRelationshipTypes.CCORDERAMOUNT_STR);
    cicc.setCostCenterName(mInitializeCostCenterIdentifier);
    if(pOrder.getPriceInfo() != null)
        cicc.setAmount(pOrder.getPriceInfo().getAmount());
    else
        cicc.setAmount(0);

    getCommerceIdentifierCostCenterContainer().addCommerceIdentifierCostCenter(pOrder.getId(), cicc);
  }

  /**
   * The <code>loadShippingCostCenters</code> method creates a
   * <code>CommerceIdentifierCostCenter</code> for each <code>CostCenterOrderRelationship</code>
   * in the <code>Order</code>, and adds them to the <code>CommerceIdentifierCostCenterContainer</code>.
   *
   * @param pOrder an <code>Order</code> value
   */
  protected void loadOrderCostCenters(Order pOrder)
  {
    List pRels = pOrder.getRelationships();
    List pRelationships = pOrder.getRelationships();
    Iterator iter = pRelationships.iterator();
    Relationship rel = null;
    CommerceIdentifierCostCenter cicc;

    while (iter.hasNext()) {
      rel = (Relationship) iter.next();
      if (rel instanceof CostCenterOrderRelationship) {
	setContainsCCOrderRel(true);
	setRelationshipCount(getRelationshipCount() + 1);
	CostCenterOrderRelationship ccorel = (CostCenterOrderRelationship) rel;
	cicc = new CommerceIdentifierCostCenter();
	cicc.setCommerceIdentifier(ccorel.getOrder());
	cicc.setRelationshipType(B2BRelationshipTypes.CCORDERAMOUNT_STR);
	cicc.setAmount(ccorel.getAmount());
	cicc.setCostCenterName(ccorel.getCostCenter().getIdentifier());

        getCommerceIdentifierCostCenterContainer().addCommerceIdentifierCostCenter(ccorel.getOrder().getId(), cicc);
      }
    }
  }
  
  /**
   * The <code>initializeItemCostCenters</code> method creates a
   * <code>CommerceIdentifierCostCenter</code> for the tax,
   * and adds it to the <code>CommerceIdentifierCostCenterContainer</code>.
   *
   * @param pOrder an <code>Order</code> value
   */
  protected void initializeTaxCostCenters(Order pOrder)
  {
    setRelationshipCount(getRelationshipCount() + 1);
    CommerceIdentifierCostCenter cicc = new CommerceIdentifierCostCenter();
    cicc.setCommerceIdentifier(pOrder);
    cicc.setRelationshipType(B2BRelationshipTypes.CCTAXAMOUNT_STR);
    cicc.setCostCenterName(mInitializeCostCenterIdentifier);
    if(pOrder.getTaxPriceInfo() != null)
        cicc.setAmount(pOrder.getTaxPriceInfo().getAmount());
    else
        cicc.setAmount(0);

    getCommerceIdentifierCostCenterContainer().addCommerceIdentifierCostCenter(pOrder.getId(), cicc);
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
    Order order = (Order) pRequest.getObjectParameter(ORDER);
    if (order != null)
      setOrder(order);
    
    String clearAll = pRequest.getParameter(CLEAR_ALL);
    if (clearAll != null) setClearAll(Boolean.valueOf(clearAll).booleanValue());

    String clearCostCenterContainer = pRequest.getParameter(CLEAR_COST_CENTER_CONTAINER);
    if (clearCostCenterContainer != null) setClearCostCenterContainer(Boolean.valueOf(clearCostCenterContainer).booleanValue());

    String clearCostCenterMap = pRequest.getParameter(CLEAR_COST_CENTER_MAP);
    if (clearCostCenterMap != null) setClearCostCenterMap(Boolean.valueOf(clearCostCenterMap).booleanValue());

    String initCostCenters = pRequest.getParameter(INIT_CC);
    if (initCostCenters != null) setInitCostCenters(Boolean.valueOf(initCostCenters).booleanValue());    

    String loadCostCenters = pRequest.getParameter(LOAD_COSTCENTERS);
    if (loadCostCenters != null) setLoadCostCenters(Boolean.valueOf(loadCostCenters).booleanValue());
           
    String initItemCostCenters = pRequest.getParameter(INIT_ITEM_COSTCENTERS);
    if (initItemCostCenters != null) setInitItemCostCenters(Boolean.valueOf(initItemCostCenters).booleanValue());
           
    String initShippingCostCenters = pRequest.getParameter(INIT_SHIPPING_COSTCENTERS);
    if (initShippingCostCenters != null) setInitShippingCostCenters(Boolean.valueOf(initShippingCostCenters).booleanValue());
    
    String initOrderCostCenters = pRequest.getParameter(INIT_ORDER_COSTCENTERS);
    if (initOrderCostCenters != null) setInitOrderCostCenters(Boolean.valueOf(initOrderCostCenters).booleanValue());

    String initTaxCostCenters = pRequest.getParameter(INIT_TAX_COSTCENTERS);
    if (initTaxCostCenters != null) setInitTaxCostCenters(Boolean.valueOf(initTaxCostCenters).booleanValue());

    String useAmount = pRequest.getParameter(USE_AMOUNT);
    if (useAmount != null) setUseAmount(Boolean.valueOf(useAmount).booleanValue());

  }

  /**
   * The <code>getDefaultCostCenterName</code> method returns the default CostCenter
   * based on the CostCenterMapContainer's default CostCenter name. Any .jhtml
   * form can manipulate this item directly.
   *
   * @return a <code>String</code> value
   */
  public CostCenter getDefaultCostCenter() {
    return getCostCenterMapContainer().getCostCenter(getCostCenterMapContainer().getDefaultCostCenterName());
  }

  //--------------------------------------------------
  // Service Method
  //--------------------------------------------------

  /**
   * The <code>service</code> method is used to initialize CostCenters and
   * CommerceIdentifierCostCenter objects. Initialization is based on input
   * parameters describing which CommerceIdentifiers in the Order will be
   * associated with CostCenters.
   *
   * The resulting Collections of CostCenters and CommerceIdentifierCostCenters
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
        logError(ResourceUtils.getMsgResource("CCDropletNullOrder",
                                              MY_RESOURCE_NAME, sResourceBundle));
      }
      return;
    }
    if (getProfile() == null) {
      if (isLoggingError()) {
        logError(ResourceUtils.getMsgResource("CCDropletNullProfile",
                                              MY_RESOURCE_NAME, sResourceBundle));
      }
      return;
    }

    // initialize user's default costcenters
    initializeUserCostCenters (pRequest, getProfile());

    // initialize the CommerceIdentifierCostCenters
    initializeCommerceIdentifierCostCenters(pRequest, getOrder());

    // consolidate the CommerceIdentifierCostCenters
    consolidateCommerceIdentifierCostCenters(pRequest, pResponse);

    pRequest.setParameter(COST_CENTERS, getCostCenterMapContainer().getCostCenterMap());
    pRequest.setParameter(CICC_MAP, getCommerceIdentifierCostCenterContainer().getCommerceIdentifierCostCenterMap());
    pRequest.setParameter(ORDER, getOrder());
    if (getRelationshipCount() == 1 && isContainsCCOrderRel()) {
      CommerceIdentifierCostCenterContainer container = getCommerceIdentifierCostCenterContainer();
      Map ciccMap = container.getCommerceIdentifierCostCenterMap();
      List ciccList = (List) ciccMap.get(getOrder().getId());
      Iterator listIter = ciccList.listIterator();
      while (listIter.hasNext()) {
	CommerceIdentifierCostCenter cicc = (CommerceIdentifierCostCenter) listIter.next();
        setSingleCostCenter(cicc.getCostCenterName());
      }
      pRequest.serviceParameter(ONE_COST_CENTER, pRequest, pResponse);
    }
    else
      pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
  }

  /**
   * <code>consolidateCommerceIdentifierCostCenters</code> consolidates CommerceIdentifierCostCenters
   * by ensuring there is no redundant data. CommerceIdentifierCostCenters that have zero
   * amounts and quantities are removed, and those that refer to the same CommerceIdentifier are
   * merged into one CommerceIdentifierCostCenter.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void consolidateCommerceIdentifierCostCenters(DynamoHttpServletRequest pRequest,
                                                          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    CommerceIdentifierCostCenter cicc;
    Map tempMap = new HashMap();
    CommerceIdentifierCostCenterContainer container = getCommerceIdentifierCostCenterContainer();
    Map ciccMap = container.getCommerceIdentifierCostCenterMap();
    Map synchronizedMap = Collections.synchronizedMap(ciccMap);
    Set set = synchronizedMap.keySet();

    synchronized(synchronizedMap) {
      Iterator iter = set.iterator();

      while (iter.hasNext()) { // for each CommerceIDentifier

        // clear our helper Map
        tempMap.clear();

        // current commerceIdentifierId
        String commerceIdentifierId = (String) iter.next();

        // List of CommerceIdentifierCostCenters for commerceIdentifierId
        List ciccList = (List) ciccMap.get(commerceIdentifierId);

        Iterator listIter = ciccList.listIterator();
        while (listIter.hasNext()) { // for each info object

          // current CommerceIdentifierCostCenter
          cicc = (CommerceIdentifierCostCenter) listIter.next();

          // if this object is reasonable [e.g. has a positive amount or quantity
          // or is the only one in the list] then proceed.
          if ((cicc.getAmount() > 0) || (cicc.getQuantity() > 0) || (ciccList.size() == 1)) {
            // what's my CostCenter?
            String costCenterName = cicc.getCostCenterName();
            // does something else have the same CostCenter?
            Object obj = tempMap.get(costCenterName);
            if (obj != null) {
              // if yes, then merge the 2 into one CommerceIdentifierCostCenter
              cicc = mergeCommerceIdentifierCostCenters(cicc,
                                                         (CommerceIdentifierCostCenter) obj);
            }
            // now put the info into the helper Map
            tempMap.put(costCenterName, cicc);
          }
        }
        // remove all old CommerceIdentifierCostCenters for this CommerceIdentifierId
        container.removeCommerceIdentifierCostCenters(commerceIdentifierId);

        // add new CommerceIdentifierCostCenters from helper Map
        Iterator valuesIter = tempMap.values().iterator();
        while (valuesIter.hasNext()) {
          cicc = (CommerceIdentifierCostCenter) valuesIter.next();
          container.addCommerceIdentifierCostCenter (commerceIdentifierId, cicc);
        }
      }
    }
  }

  /**
   * <code>mergeCommerceIdentifierCostCenters</code> merges 2 CommerceIdentifierCostCenters
   * into one by combining their amounts and quantities.
   *
   * @param cicc1 a <code>CommerceIdentifierCostCenter</code> value
   * @param cicc2 a <code>CommerceIdentifierCostCenter</code> value
   * @return a <code>CommerceIdentifierCostCenter</code> value
   */
  protected CommerceIdentifierCostCenter mergeCommerceIdentifierCostCenters(CommerceIdentifierCostCenter cicc1,
                                                                              CommerceIdentifierCostCenter cicc2) {
    // merge 2 CommerceIdentifierCostCenters by adding their amounts and quantities
    cicc1.setAmount(cicc1.getAmount() + cicc2.getAmount());
    cicc1.setQuantity(cicc1.getQuantity() + cicc2.getQuantity());
    return cicc1;
  }
}   // end of class
