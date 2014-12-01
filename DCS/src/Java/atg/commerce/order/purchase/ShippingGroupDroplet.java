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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupManager;
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
 * The <code>ShippingGroupDroplet</code> is a DynamoServlet that is used to initialize
 * ShippingGroups and CommerceItemShippingInfo objects for use by the ShippingGroupFormHandler.
 * The ShippingGroupDroplet is composed of the following containers:
 * <p>
 * ShippingGroupMapContainer - container for the user's authorized ShippingGroups
 * <p>
 * CommerceItemShippingInfoContainer - container for the user's CommerceItemShippingInfo
 * objects for a particular Order's CommerceItems
 * <p>
 * The service method ensures the following:
 * <p>
 * ShippingGroup initialization - the user's ShippingGroups are added to
 * the ShippingGroupMapContainer. Once this is done the user can begin to select from these
 * during the shipping checkout process.
 * <p>
 * CommerceItemShippingInfo initialization - the Order CommerceItems of interest
 * are used to instantiate CommerceItemShippingInfo objects, and these are added
 * to the CommerceItemShippingInfoContainer.
 * <p>
 * A complete description of the parameters to the ShippingGroupDroplet are:
 *
 * <dl>
 *
 * <dt>order
 * <dd>This parameter may be used to override the component's default setting for
 * the user's order. The user's Order is also exposed as an output parameter.
 *
 * <dt>clearShippingInfos
 * <dd>When set to true this parameter will clear the user's CommerceItemShippingInfoContainer.
 * This should be done at least once per new order.
 *
 * <dt>clearShippingGroups
 * <dd>When set to true this parameter will clear the user's ShippingGroupMapContainer.
 *
 * <dt>clear
 * <dd>When set to true this parameter will clear both the CommerceItemShippingInfoContainer
 * and the ShippingGroupMapContainer.
 *
 * <dt>initShippingGroups
 * <dd>When this parameter is set to true, the designated ShippingGroup types will be initialized.
 *
 * <dt>shippingGroupTypes
 * <dd>This comma separated list of ShippingGroup types is used to determine which
 * ShippingGroupInitializer components are executed. Each possible ShippingGroup type is
 * configured to reference a ShippingGroupInitializer component in the
 * ShippingGroupInitializers ServiceMap.
 *
 * <dt>initShippingInfos
 * <dd>When set to true this parameter will create a CommerceItemShippingInfo for each CommerceItem
 * in the Order. These are then added to the CommerceItemShippingInfoContainer. Each
 * CommerceItemShippingInfo initially references the default ShippingGroup in the
 * ShippingGroupMapContainer.
 *
 * <dt>initBasedOnOrder
 * <dd>When set to true this parameter will create a CommerceItemShippingInfo for each
 * ShippingGroupCommerceItemRelationships in the order.
 *
 * <dt>createOneInfoPerUnit
 * <dd>When set to true one CommerceItemShippingInfo is created for each individual unit contained by a commerce item.
 * For example, a commerce item with quantity of five will have five CommerceItemShippingInfos created.
 *
 * <dt>shippingGroups
 * <dd>This output parameter is set to the Map referenced by the ShippingGroupMapContainer.
 *
 * <dt>shippingInfos
 * <dd>This output parameter is set to the Map refereneced by the CommerceItemShippingInfoMap.
 *
 * </dl>
 *
 * @author Charles Chen
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/ShippingGroupDroplet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ShippingGroupDroplet
  extends DynamoServlet
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/ShippingGroupDroplet.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.purchase.PurchaseProcessResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  public static final String SHIPPING_GROUPS = "shippingGroups";
  public static final String CISI_MAP = "shippingInfos";
  public static final String OUTPUT = "output";
  public static final String ORDER = "order";

  public static final ParameterName INIT_SHIPPING_GROUPS = ParameterName.getParameterName("initShippingGroups");
  public static final ParameterName SHIPPING_GROUP_TYPES = ParameterName.getParameterName("shippingGroupTypes");
  public static final ParameterName INIT_SHIPPING_INFOS = ParameterName.getParameterName("initShippingInfos");
  public static final ParameterName INIT_BASED_ON_ORDER = ParameterName.getParameterName("initBasedOnOrder");
  public static final ParameterName CLEAR_ALL = ParameterName.getParameterName("clear");
  public static final ParameterName CLEAR_SHIPPING_GROUPS = ParameterName.getParameterName("clearShippingGroups");
  public static final ParameterName CLEAR_SHIPPING_INFOS = ParameterName.getParameterName("clearShippingInfos");
  public static final ParameterName CREATE_ONE_INFO_PER_UNIT = ParameterName.getParameterName("createOneInfoPerUnit");
  
  
  protected CommerceItemShippingInfoTools mCommerceItemShippingInfoTools;
  /**
   * Returns the tools component containing the API for modifying
   * CommerceItemShippingInfos 
   * @return
   */
  public CommerceItemShippingInfoTools getCommerceItemShippingInfoTools()
  {
    return mCommerceItemShippingInfoTools;
  }

  public void setCommerceItemShippingInfoTools(
      CommerceItemShippingInfoTools pCommerceItemShippingInfoTools)
  {
    mCommerceItemShippingInfoTools = pCommerceItemShippingInfoTools;
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


  boolean mCreateOneInfoPerUnit=false;
  /**
   * @return Returns the createOneInfoPerUnit.
   */
  public boolean isCreateOneInfoPerUnit() {
    return mCreateOneInfoPerUnit;
  }

  /**
   * This property controls if one CommerceItemShippingInfo is created for each item unit.
   * (e.g. an item with quantity 5 will end up with 5 CommerceItemShippingInfos,
   * each with a quantity of 1).
   * @param pCreateOneInfoPerUnit The createOneInfoPerUnit to set.
   */
  public void setCreateOneInfoPerUnit(boolean pCreateOneInfoPerUnit) {
    mCreateOneInfoPerUnit = pCreateOneInfoPerUnit;
  }



  //---------------------------------------------------------------------------
  // property: ShippingGroupTypes
  //---------------------------------------------------------------------------
  String mShippingGroupTypes;

  /**
   * Set the ShippingGroupTypes property.
   * @param pShippingGroupTypes a <code>String</code> value
   */
  public void setShippingGroupTypes(String pShippingGroupTypes) {
    mShippingGroupTypes = pShippingGroupTypes;
  }

  /**
   * Return the ShippingGroupTypes property.
   * @return a <code>String</code> value
   */
  public String getShippingGroupTypes() {
    return mShippingGroupTypes;
  }

  //---------------------------------------------------------------------------
  // property: ShippingGroupManager
  //---------------------------------------------------------------------------
  ShippingGroupManager mShippingGroupManager;

  /**
   * Set the ShippingGroupManager property.
   * @param pShippingGroupManager a <code>ShippingGroupManager</code> value
   */
  public void setShippingGroupManager(ShippingGroupManager pShippingGroupManager) {
    mShippingGroupManager = pShippingGroupManager;
  }

  /**
   * Return the ShippingGroupManager property.
   * @return a <code>ShippingGroupManager</code> value
   */
  public ShippingGroupManager getShippingGroupManager() {
    return mShippingGroupManager;
  }

  //---------------------------------------------------------------------------
  // property: ShippingGroupInitializers
  //---------------------------------------------------------------------------
  ServiceMap mShippingGroupInitializers;

  /**
   * Set the ShippingGroupInitializers property.
   * @param pShippingGroupInitializers a <code>ServiceMap</code> value
   */
  public void setShippingGroupInitializers(ServiceMap pShippingGroupInitializers) {
    mShippingGroupInitializers = pShippingGroupInitializers;
  }

  /**
   * Return the ShippingGroupInitializers property.
   * @return a <code>ServiceMap</code> value
   */
  public ServiceMap getShippingGroupInitializers() {
    return mShippingGroupInitializers;
  }

  //---------------------------------------------------------------------------
  // property: InitShippingGroups
  //---------------------------------------------------------------------------
  boolean mInitShippingGroups;

  /**
   * Set the InitShippingGroups property.
   * @param pInitShippingGroups a <code>boolean</code> value
   */
  public void setInitShippingGroups(boolean pInitShippingGroups) {
    mInitShippingGroups = pInitShippingGroups;
  }

  /**
   * Return the InitShippingGroups property.
   * @return a <code>boolean</code> value
   */
  public boolean isInitShippingGroups() {
    return mInitShippingGroups;
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
  // property: CommerceItemShippingInfoContainer
  //---------------------------------------------------------------------------
  CommerceItemShippingInfoContainer mCommerceItemShippingInfoContainer;

  /**
   * Set the CommerceItemShippingInfoContainer property.
   * @param pCommerceItemShippingInfoContainer a <code>CommerceItemShippingInfoContainer</code> value
   */
  public void setCommerceItemShippingInfoContainer(CommerceItemShippingInfoContainer pCommerceItemShippingInfoContainer) {
    mCommerceItemShippingInfoContainer = pCommerceItemShippingInfoContainer;
  }

  /**
   * Return the CommerceItemShippingInfoContainer property.
   * @return a <code>CommerceItemShippingInfoContainer</code> value
   */
  public CommerceItemShippingInfoContainer getCommerceItemShippingInfoContainer() {
    return mCommerceItemShippingInfoContainer;
  }

  //---------------------------------------------------------------------------
  // property: ShippingGroupMapContainer
  //---------------------------------------------------------------------------
  ShippingGroupMapContainer mShippingGroupMapContainer;

  /**
   * Set the ShippingGroupMapContainer property.
   * @param pShippingGroupMapContainer a <code>ShippingGroupMapContainer</code> value
   */
  public void setShippingGroupMapContainer(ShippingGroupMapContainer pShippingGroupMapContainer) {
    mShippingGroupMapContainer = pShippingGroupMapContainer;
  }

  /**
   * Return the ShippingGroupMapContainer property.
   * @return a <code>ShippingGroupMapContainer</code> value
   */
  public ShippingGroupMapContainer getShippingGroupMapContainer() {
    return mShippingGroupMapContainer;
  }

  //---------------------------------------------------------------------------
  // property: GiftlistManager
  //---------------------------------------------------------------------------
  GiftlistManager mGiftlistManager;

  /**
   * Set the GiftlistManager property.
   * @param pGiftlistManager a <code>GiftlistManager</code> value
   */
  public void setGiftlistManager(GiftlistManager pGiftlistManager) {
    mGiftlistManager = pGiftlistManager;
  }

  /**
   * Return the GiftlistManager property.
   * @return a <code>GiftlistManager</code> value
   */
  public GiftlistManager getGiftlistManager() {
    return mGiftlistManager;
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
  // property: ClearShippingInfos
  //---------------------------------------------------------------------------
  boolean mClearShippingInfos;

  /**
   * Set the ClearShippingInfos property.
   * @param pClearShippingInfos a <code>boolean</code> value
   */
  public void setClearShippingInfos(boolean pClearShippingInfos) {
    mClearShippingInfos = pClearShippingInfos;
  }

  /**
   * Return the ClearShippingInfos property.
   * @return a <code>boolean</code> value
   */
  public boolean isClearShippingInfos() {
    return mClearShippingInfos;
  }

  //---------------------------------------------------------------------------
  // property: ClearShippingGroups
  //---------------------------------------------------------------------------
  boolean mClearShippingGroups;

  /**
   * Set the ClearShippingGroups property.
   * @param pClearShippingGroups a <code>boolean</code> value
   */
  public void setClearShippingGroups(boolean pClearShippingGroups) {
    mClearShippingGroups = pClearShippingGroups;
  }

  /**
   * Return the ClearShippingGroups property.
   * @return a <code>boolean</code> value
   */
  public boolean isClearShippingGroups() {
    return mClearShippingGroups;
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
  // property: InitShippingInfos
  //---------------------------------------------------------------------------
  boolean mInitShippingInfos;

  /**
   * Set the InitShippingInfos property.
   * @param pInitShippingInfos a <code>boolean</code> value
   */
  public void setInitShippingInfos(boolean pInitShippingInfos) {
    mInitShippingInfos = pInitShippingInfos;
  }

  /**
   * Return the InitShippingInfos property.
   * @return a <code>boolean</code> value
   */
  public boolean isInitShippingInfos() {
    return mInitShippingInfos;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>ShippingGroupDroplet</code> instance.
   *
   */
  public ShippingGroupDroplet () {}

  //--------------------------------------------------
  // Initialization Code
  //--------------------------------------------------

  /**
   * <code>initializeUserShippingMethods</code> is used to add the user's
   * shipping methods to the ShippingGroupMapContainer. For each entry in the
   * ShippingGroupTypes, its corresponding ShippingGroupInitializer is obtained
   * from the ShippingGroupInitializers ServiceMap and its initializeShippingGroups
   * method executed.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pProfile a <code>Profile</code> value
   */
  protected void initializeUserShippingMethods (DynamoHttpServletRequest pRequest,
                                                Profile pProfile)
  {
    String shippingGroupTypes = StringUtils.removeWhiteSpace(getShippingGroupTypes());
    String [] types = StringUtils.splitStringAtCharacter(shippingGroupTypes,','); 
    getCommerceItemShippingInfoTools().initializeUserShippingMethods(getShippingGroupMapContainer(),pProfile,
        types,getShippingGroupInitializers());
  }

  /**
   * <code>initializeCommerceItemShippingInfos</code> creates a CommerceItemShippingInfo
   * for each of the Order's non-gift CommerceItems, and then adds these to the
   * CommerceItemShippingInfoContainer. By default each new CommerceItemShippingInfo
   * references the default ShippingGroup in the ShippingGroupMapContainer.
   * @see CommerceItemShippingInfoTools#initCommerceItemShippingInfoToDefaultShippingGroup(CommerceItemShippingInfoContainer, ShippingGroupMapContainer, Order, CommerceItem)
   *
   * @param pProfile a <code>Profile</code> value
   * @param pOrder an <code>Order</code> value
   */
  protected void initializeCommerceItemShippingInfos (Profile pProfile, Order pOrder)
  {
    
    getCommerceItemShippingInfoTools().initializeCommerceItemShippingInfosToDefaultShipping(pOrder,
        getCommerceItemShippingInfoContainer(),
        getShippingGroupMapContainer(),
        isCreateOneInfoPerUnit());
    return;
  }

  /**
   * Initializes the contains based on the contents of the order
   * @see CommerceItemShippingInfoTools#initializeBasedOnOrder(Order, CommerceItemShippingInfoContainer, ShippingGroupMapContainer, Collection, boolean)
   * @param pProfile
   * @param pOrder
   */
  protected void initializeBasedOnOrder (Profile pProfile, Order pOrder)
  {
     try
    {
       getCommerceItemShippingInfoTools().initializeBasedOnOrder(pOrder,
           getCommerceItemShippingInfoContainer(),getShippingGroupMapContainer(),
           getShippingGroupInitializers().values(),
           isCreateOneInfoPerUnit());
    } 
    catch (CommerceException e)
    {
      if(isLoggingError())
        logError(e);
    }
  }  

  
  /**
   * Creates a CommerceItemShippingInfo for the given item, relationship type, quantity and shipping group name.
   * <p>
   * @see CommerceItemShippingInfoTools#createCommerceItemShippingInfo(CommerceItemShippingInfoContainer, ShippingGroupMapContainer, CommerceItem, String, Long, String)
   * 
   */
  protected CommerceItemShippingInfo createCommerceItemShippingInfo(CommerceItem pItem, String pRelationshipType, Long pQuantity, String pShippingGroupName)
  {
    return getCommerceItemShippingInfoTools().createCommerceItemShippingInfo(
        getCommerceItemShippingInfoContainer(),getShippingGroupMapContainer(),
        pItem,pRelationshipType,pQuantity,null,pShippingGroupName);
  }

  /**
   * Adds a shipping group to the map container.
   * @param pShippingGroup
   * @return name used as the key in the map. If null then the shipping group is not added to the map.
   * @deprecated
   */
  protected String addShippingGroupToMap(ShippingGroup pShippingGroup)
  {
    return getShippingGroupMapContainer().addShippingGroupToMap(pShippingGroup, getShippingGroupInitializers().values());
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

    String shippingGroupTypes = pRequest.getParameter(SHIPPING_GROUP_TYPES);
    if (shippingGroupTypes != null) setShippingGroupTypes(shippingGroupTypes);

    String initShippingGroups = pRequest.getParameter(INIT_SHIPPING_GROUPS);
    if (initShippingGroups != null) setInitShippingGroups(Boolean.valueOf(initShippingGroups).booleanValue());

    String initShippingInfos = pRequest.getParameter(INIT_SHIPPING_INFOS);
    if (initShippingInfos != null) setInitShippingInfos(Boolean.valueOf(initShippingInfos).booleanValue());

    String initBasedOnOrder = pRequest.getParameter(INIT_BASED_ON_ORDER);
    if (initBasedOnOrder != null) setInitBasedOnOrder(Boolean.valueOf(initBasedOnOrder).booleanValue());

    String clearShippingGroups = pRequest.getParameter(CLEAR_SHIPPING_GROUPS);
    if (clearShippingGroups != null) setClearShippingGroups(Boolean.valueOf(clearShippingGroups).booleanValue());

    String clearShippingInfos = pRequest.getParameter(CLEAR_SHIPPING_INFOS);
    if (clearShippingInfos != null) setClearShippingInfos(Boolean.valueOf(clearShippingInfos).booleanValue());

    String clearAll = pRequest.getParameter(CLEAR_ALL);
    if (clearAll != null) setClearAll(Boolean.valueOf(clearAll).booleanValue());

    String createOneInfoPerUnit = pRequest.getParameter(CREATE_ONE_INFO_PER_UNIT);
    if (createOneInfoPerUnit != null) setCreateOneInfoPerUnit(Boolean.valueOf(createOneInfoPerUnit).booleanValue());


  }

  /**
   * The <code>getDefaultShippingGroupName</code> method returns the default
   * ShippingGroup based on the ShippingGroupMapContainer's default ShippingGroup
   * name.
   *
   * @return a <code>String</code> value
   */
  public ShippingGroup getDefaultShippingGroup() {
    return getShippingGroupMapContainer().getShippingGroup(getShippingGroupMapContainer().getDefaultShippingGroupName());
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
   * <LI>commerceItemShippingInfoContainer
   * <LI>giftlistManager
   * <LI>profile
   * <LI>shippingGroupMapContainer
   * </UL>
   **/
  protected void copyConfiguration() {
    if (mConfiguration != null) {
      if (getCommerceItemShippingInfoContainer() == null) {
        setCommerceItemShippingInfoContainer(mConfiguration.getCommerceItemShippingInfoContainer());
      }
      if (getGiftlistManager() == null) {
        setGiftlistManager(mConfiguration.getGiftlistManager());
      }
      if (getProfile() == null && mConfiguration.getProfile() instanceof Profile) {
        setProfile((Profile)mConfiguration.getProfile());
      }
      if (getShippingGroupMapContainer() == null) {
        setShippingGroupMapContainer(mConfiguration.getShippingGroupMapContainer());
      }
      if (getShippingGroupManager() == null) {
        setShippingGroupManager(mConfiguration.getShippingGroupManager());
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
   * This <code>service</code> method is used to initialize ShippingGroups and
   * CommerceItemShippingInfo objects. Initialization is based on input
   * parameters describing which ShippingGroups and CommerceItemShippingInfo
   * objects, if any, should be created and added to the appropriate container.
   *
   * The resulting Collections of ShippingGroups and CommerceItemShippingInfos
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

    if (isClearAll() || isClearShippingGroups()) {
      getCommerceItemShippingInfoTools().clearShippingGroups(getCommerceItemShippingInfoContainer(),getShippingGroupMapContainer());
      if (isLoggingDebug())
        logDebug("ShippingGroupDroplet removing all ShippingGroups");
    }

    if (isClearAll() || isClearShippingInfos()) {
       getCommerceItemShippingInfoTools().clearCommerceItemShippingInfos(getCommerceItemShippingInfoContainer(),getShippingGroupMapContainer());
      if (isLoggingDebug())
        logDebug("ShippingGroupDroplet removing all ShippingInfos");
    }


    // initialize ShippingGroups
    if(isInitShippingGroups())
      initializeUserShippingMethods(pRequest, getProfile());

    //we clear out any items in the map that may have been deleted as
    //a result of being removed from an order (and deleted from the repository because of cascade delete)
    //you may end up with these in the map if you don't clear the map before you init, leaving old, possibly deleted
    //shipping groups behind. Since they cannot be used anyway and will cause errors when you attempt to access them,
    //we unconditionally get rid of them here.
    removeDeletedShippingGroups();

    // initialize CommerceItemShippingInfos for Order
    if (isInitBasedOnOrder()) 
    {
      initializeBasedOnOrder(getProfile(),getOrder());
      //After initializing based on the order, if there is no cisi's, the call the initializeCommerceItemShippingInfos()
      //method to create cisi's for each commerece item.
      if (!isAnyShippingInfoExistsInContainter()) {
        if (isLoggingDebug())
          logDebug("CommerceItemShippingInfoContainer does not have any cisi.");
        initializeCommerceItemShippingInfos(getProfile(), getOrder());
      }
    } 
    else 
    {
      if (isInitShippingInfos())
        initializeCommerceItemShippingInfos(getProfile(), getOrder());
    }

    pRequest.setParameter(SHIPPING_GROUPS, getShippingGroupMapContainer().getShippingGroupMap());
    pRequest.setParameter(CISI_MAP, getCommerceItemShippingInfoContainer().getCommerceItemShippingInfoMap());
    pRequest.setParameter(ORDER, getOrder());
    pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
  }

  /**
   * <code>consolidateShippingInfos</code> consolidates CommerceItemShippingInfos
   * by ensuring there is no redundant data.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @see CommerceItemShippingInfoTools#consolidateShippingInfos(CommerceItemShippingInfoContainer)
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void consolidateShippingInfos(DynamoHttpServletRequest pRequest,
                                          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    
    try
    {
      getCommerceItemShippingInfoTools().consolidateShippingInfos(getCommerceItemShippingInfoContainer());
    } 
    catch (CommerceException e)
    {
      if(isLoggingError())
        logError(e);
    }
    return;
    
  }

  /**
   * <code>mergeCommerceItemShippingInfos</code> merges 2 CommerceItemShippingInfos
   * into one by combining their quantities.
   *  @see CommerceItemShippingInfoTools#mergeCommerceItemShippingInfos(CommerceItemShippingInfo, CommerceItemShippingInfo)
   * @param cisi1 a <code>CommerceItemShippingInfo</code> value
   * @param cisi2 a <code>CommerceItemShippingInfo</code> value
   * @return a <code>CommerceItemShippingInfo</code> value
   */
  protected CommerceItemShippingInfo mergeCommerceItemShippingInfos(CommerceItemShippingInfo cisi1,
                                                                    CommerceItemShippingInfo cisi2) {
    try
    {
      return getCommerceItemShippingInfoTools().mergeCommerceItemShippingInfos(cisi1,cisi2);
    } 
    catch (CommerceException e)
    {
      // TODO Auto-generated catch block
      if(isLoggingError())
        logError(e);
      return null;
    }
  }

  /**
   * Looks through the shipping groups in the container and removes any that have a backing repository item
   * that has been removed from the repository.
   * <p>
   * This can happen when a shipping group was applied to an order and then "unapplied", causing the cascade
   * delete on the repository item to remove it from the repository.
   * @see CommerceItemShippingInfoTools#removeDeletedShippingGroups(ShippingGroupMapContainer)
   */
  protected void removeDeletedShippingGroups()
  {
    
    getCommerceItemShippingInfoTools().removeDeletedShippingGroups(getShippingGroupMapContainer());
    
    return;
  }

  /**
  *
  *  This method checks to see if there is any shipping infos in the container. If there is any cisi in the container, this method
  *  returns true otherwise false. The cisi could be in any level. The cisi could be in the Order, CommerceItem, ShippingGroup
  *  or Tax level. If any cisi found this method returns true and if no cisi found, then this method returns false.
  *
  * @return true/false based on the the existance of any cisi in the container
  *
  */
 public boolean isAnyShippingInfoExistsInContainter () {

  if (isLoggingDebug()) {
    logDebug ("Entering isAnyPaymentInfoExistsInContainter()");
  }
  boolean shippingInfoExists = false;
  CommerceItemShippingInfoContainer cisic = getCommerceItemShippingInfoContainer();
  Map allcisi = cisic.getCommerceItemShippingInfoMap();

  if (allcisi == null || allcisi.isEmpty()) {
    if (isLoggingDebug()) {
      logDebug ("CommerceItemShippingInfoContainer does not have any cisi.");
    }
    return false;
  }

  Iterator cisii = allcisi.keySet().iterator();
  while (cisii.hasNext()) {
    String commerceItemId = (String) cisii.next();
    List cisiList = cisic.getCommerceItemShippingInfos(commerceItemId);
    if (cisiList != null
        && cisiList.size() > 0) {
      if (isLoggingDebug()) {
        logDebug ("cisi list is found.");
      }
      shippingInfoExists = true;
      break;
    }
  }//end of while
  return shippingInfoExists;
 }



} //ShippingGroupDroplet.java

