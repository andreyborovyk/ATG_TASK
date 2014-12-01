/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2010 Art Technology Group, Inc.
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
package atg.projects.store.gifts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.gifts.GiftlistFormHandler;
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.gifts.GiftlistTools;
import atg.commerce.gifts.InvalidDateException;
import atg.commerce.gifts.InvalidGiftQuantityException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.core.util.ResourceUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.projects.store.profile.SessionBean;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.PropertyManager;


/**
 * Extensions to the atg.commerce.gifts.GiftlistFormHandler.
 *
 * @see atg.commerce.gifts.GiftlistFormHandler
 *
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/gifts/StoreGiftlistFormHandler.java#3 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class StoreGiftlistFormHandler extends GiftlistFormHandler {

  /**
   * Event type with this name will be returned last from the getEventTypes method.
   */
  protected static String LAST_EVENT_TYPE_NAME = "Other";
  
  protected static String GIFT_LIST_EVENT_NAME_MAP_KEY = "eventName";
  protected static String GIFT_LIST_MONTH_MAP_KEY = "month";
  protected static String GIFT_LIST_DATE_MAP_KEY = "date";
  protected static String GIFT_LIST_YEAR_MAP_KEY = "year";
  protected static String GIFT_LIST_SHIPPING_ADDRESS_MAP_KEY = "shippingAddressId";
  protected static String GIFT_LIST_EVENT_TYPE_MAP_KEY = "eventType";
  protected static String GIFT_LIST_PUBLISHED_MAP_KEY = "isPublished";
  protected static String GIFT_LIST_DESCRIPTION_MAP_KEY = "description";
  protected static String GIFT_LIST_INSTRUCTIONS_MAP_KEY = "instructions";
  
  // Error Keys
  private static String MSG_ERROR_NO_COLOR_SIZE_SELECTED_WISHLIST = "noColorSizeSelectedWishlist";
  private static String MSG_ERROR_NO_COLOR_SIZE_SELECTED_GIFTLIST = "noColorSizeSelectedGiftlist";
  private static String MSG_ERROR_NO_FINISH_SELECTED_WISHLIST = "noFinishSelectedWishlist";
  private static String MSG_ERROR_NO_FINISH_SELECTED_GIFTLIST = "noFinishSelectedGiftlist";

  /**
   * An extension of standard HashMap Java class.
   * This implementation overrides the {@link HashMap#put(Object, Object)} method to add
   * giftlist-specified behaviour.
   * @see HashMap
   * @author ATG
   * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/gifts/StoreGiftlistFormHandler.java#3 $$Change: 635816 $ 
   * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
   */
  private class ChangeAwareMap extends HashMap<String, Object>
  {
    private static final long serialVersionUID = 1L;
    
    /**
     * This implementation does the following:
     * <ol>
     *  <li>call to super-method to add an item into map;</li>
     *  <li>set formhandler's <i>giftlistId</i> property to be equal to <i>key</i></li>
     *  <li>handle <i>addItemToGiftlist</i> process</li> 
     * </ol>
     * @return old map value stored by the key specified.
     */
    @Override
    public Object put(String key, Object value)
    {
      Object result = super.put(key, value);
      try
      {
        setGiftlistId(key);
        handleAddItemToGiftlist(ServletUtil.getCurrentRequest(), ServletUtil.getCurrentResponse());
      } catch (Exception e)
      {
        e.printStackTrace();
      }
      return result;
    }
  }
  
  //-------------------------------------
  // Constants
  //-------------------------------------
  // Resource message keys
  public static final String MSG_INVALID_DATE = "InvalidDate";

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/gifts/StoreGiftlistFormHandler.java#3 $$Change: 635816 $";

  /**
   * Gift list id constant.
   */
  public static final String GIFTLIST_ID = "giftlistId";

  /**
   * Success URL constant.
   */
  public static final String SUCCESS_URL = "successURL";

  /**
   * Sku id constant.
   */
  public static final String SKU_ID = "skuId";

  /**
   * Quantity constant.
   */
  public static final String QUANTITY = "quantity";

  /**
   * Product id constant.
   */
  public static final String PRODUCT_ID = "productId";

  /**
   * Add item to gift list login URL.
   */
  protected String mAddItemToGiftlistLoginURL;
  
  /**
   * Move items from gift list login URL.
   */
  protected String mMoveItemsFromCartLoginURL;

  /**
   * Profile property manager.
   */
  protected PropertyManager mProfilePropertyManager;

  /**
   * Remove items from gift list successful URL.
   */
  protected String mRemoveItemsFromGiftlistSuccessURL;

  /**
   * Remove items from gift list error URL.
   */
  protected String mRemoveItemsFromGiftlistErrorURL;
  
  private SessionBean mSessionBean;

  private Map<String, Object> giftlistIdToValue = new ChangeAwareMap();
  
  /**
   * Readonly property, contains a Map.
   * Key is a Giftlist ID; value is some value (no matter what).
   * <br>
   * <font color="red"><strong>IMPORTANT!</strong></font>
   * When adding a key-value pair into this property, the following will occur:
   * <ol>
   *  <li>key will be treated as a giftlist ID</li>
   *  <li>a {@link #handleAddItemToGiftlist(DynamoHttpServletRequest, DynamoHttpServletResponse)} method will be called</li>
   * </ol>
   * This property is used by non-JavaScript version of color/size picker only.
   */
  public Map<String, Object> getGiftlistIdToValue()
  {
    return giftlistIdToValue;
  }
  
  /**
   * Sets property AddItemToGiftlistLoginURL.
   * @param pAddItemToGiftlistLoginURL  The property to store the URL for where the user should be redirected
   *        if they attempt to add an item to the giftlist without being logged in.
   * @beaninfo description:  The property to store the URL for where the user should be redirected
   *           if they attempt to add an item to the giftlist without being logged in.
   **/
  public void setAddItemToGiftlistLoginURL(String pAddItemToGiftlistLoginURL) {
    mAddItemToGiftlistLoginURL = pAddItemToGiftlistLoginURL;
  }

  /**
   * @return add item to gift list login URL.
   **/
  public String getAddItemToGiftlistLoginURL() {
    return mAddItemToGiftlistLoginURL;
  }
  
  /**
   * Sets property MoveItemsFromCartLoginURL.
   * @param pMoveItemsFromCartLoginURL  The property to store the URL for where the user should be redirected
   *        if they attempt to move items from cart to the giftlist without being logged in.
   * @beaninfo description:  The property to store the URL for where the user should be redirected
   *           if they attempt to move items from cart to the giftlist without being logged in.
   **/
  public void setMoveItemsFromCartLoginURL(String pMoveItemsFromCartLoginURL) {
    mMoveItemsFromCartLoginURL = pMoveItemsFromCartLoginURL;
  }

  /**
   * @return move items from cart to gift list login URL.
   **/
  public String getMoveItemsFromCartLoginURL() {
    return mMoveItemsFromCartLoginURL;
  }

  /**
   * @param pProfilePropertyManager - the property manager for profiles, used to see if the user is logged in
   * @beaninfo description:  The PropertyManager for profiles, used to see if the user is logged in
   **/
  public void setProfilePropertyManager(PropertyManager pProfilePropertyManager) {
    mProfilePropertyManager = pProfilePropertyManager;
  }

  /**
   * @return profile property manager.
   **/
  public PropertyManager getProfilePropertyManager() {
    return mProfilePropertyManager;
  }

  /**
   * @param pRemoveItemsFromGiftlistSuccessURL - remove items from
   * gift list success URL.
   * @beaninfo description:  The property to store the success URL for removeItemsFromGiftlist.
   **/
  public void setRemoveItemsFromGiftlistSuccessURL(String pRemoveItemsFromGiftlistSuccessURL) {
    mRemoveItemsFromGiftlistSuccessURL = pRemoveItemsFromGiftlistSuccessURL;
  }

  /**
   * @return remove items from gift list success URL.
   **/
  public String getRemoveItemsFromGiftlistSuccessURL() {
    return mRemoveItemsFromGiftlistSuccessURL;
  }

  /**
   * @param pRemoveItemsFromGiftlistErrorURL - remove items from gift list error URL.
   * @beaninfo description:  The property to store the error URL for removeItemsFromGiftlist.
   **/
  public void setRemoveItemsFromGiftlistErrorURL(String pRemoveItemsFromGiftlistErrorURL) {
    mRemoveItemsFromGiftlistErrorURL = pRemoveItemsFromGiftlistErrorURL;
  }

  /**
   * @return remove items from gift list error URL.
   **/
  public String getRemoveItemsFromGiftlistErrorURL() {
    return mRemoveItemsFromGiftlistErrorURL;
  }
  
  //-------------------------------------
  // property: UpdateGiftlistAndItemsSuccessURL
  String mUpdateGiftlistAndItemsSuccessURL;

  /**
   * Sets property UpdateGiftlistAndItemsSuccessURL
   * @param pUpdateGiftlistAndItemsSuccessURL  The property to store the Success URL for UpdateGiftlistAndItems.
   * @beaninfo description:  The property to store the success URL for UpdateGiftlistAndItems.
   **/
  public void setUpdateGiftlistAndItemsSuccessURL(String pUpdateGiftlistAndItemsSuccessURL) {
    mUpdateGiftlistAndItemsSuccessURL = pUpdateGiftlistAndItemsSuccessURL;
  }

  /**
   * Returns property UpdateGiftlistAndItemsSuccessURL
   * @return The value of the property UpdateGiftlistAndItemsSuccessURL.
   **/
  public String getUpdateGiftlistAndItemsSuccessURL() {
    return mUpdateGiftlistAndItemsSuccessURL;
  }

  //-------------------------------------
  // property: UpdateGiftlistAndItemsErrorURL
  String mUpdateGiftlistAndItemsErrorURL;

  /**
   * Sets property UpdateGiftlistAndItemsErrorURL
   * @param pUpdateGiftlistAndItemsErrorURL  The property to store the error URL for UpdateGiftlistAndItems.
   * @beaninfo description:  The property to store the error URL for UpdateGiftlistAndItems.
   **/
  public void setUpdateGiftlistAndItemsErrorURL(String pUpdateGiftlistAndItemsErrorURL) {
    mUpdateGiftlistAndItemsErrorURL = pUpdateGiftlistAndItemsErrorURL;
  }

  /**
   * Returns property UpdateGiftlistAndItemsErrorURL
   * @return The value of the property UpdateGiftlistAndItemsErrorURL.
   **/
  public String getUpdateGiftlistAndItemsErrorURL() {
    return mUpdateGiftlistAndItemsErrorURL;
  }
  
  //-------------------------------------
  // property: MoveToNewGiftListAddressSuccessURL
  String mMoveToNewGiftListAddressSuccessURL;

  /**
   * Sets property MoveToNewGiftListAddressSuccessURL
   * @param pMoveToNewGiftListAddressSuccessURL  The property to store the Success URL for MoveToNewGiftListAddress.
   * @beaninfo description:  The property to store the success URL for MoveToNewGiftListAddress.
   **/
  public void setMoveToNewGiftListAddressSuccessURL(String pMoveToNewGiftListAddressSuccessURL) {
    mMoveToNewGiftListAddressSuccessURL = pMoveToNewGiftListAddressSuccessURL;
  }

  /**
   * Returns property MoveToNewGiftListAddressSuccessURL
   * @return The value of the property MoveToNewGiftListAddressSuccessURL.
   **/
  public String getMoveToNewGiftListAddressSuccessURL() {
    return mMoveToNewGiftListAddressSuccessURL;
  }

  //-------------------------------------
  // property: MoveToNewGiftListAddressErrorURL
  String mMoveToNewGiftListAddressErrorURL;

  /**
   * Sets property MoveToNewGiftListAddressErrorURL
   * @param pMoveToNewGiftListAddressErrorURL  The property to store the error URL for MoveToNewGiftListAddress.
   * @beaninfo description:  The property to store the error URL for MoveToNewGiftListAddress.
   **/
  public void setMoveToNewGiftListAddressErrorURL(String pMoveToNewGiftListAddressErrorURL) {
    mMoveToNewGiftListAddressErrorURL = pMoveToNewGiftListAddressErrorURL;
  }

  /**
   * Returns property MoveToNewGiftListAddressErrorURL
   * @return The value of the property MoveToNewGiftListAddressErrorURL.
   **/
  public String getMoveToNewGiftListAddressErrorURL() {
    return mMoveToNewGiftListAddressErrorURL;
  }
  
  //--------------------------------------
  // property: useWishlist
  /**
   * Identifies whether or not the request comes from adding an item
   * to a giftlist or wishlist.
   */
  private boolean mUseWishlist = false;
  
  public void setUseWishlist(boolean pUseWishlist){
    mUseWishlist = pUseWishlist; 
  }
  
  public boolean isUseWishlist(){
    return mUseWishlist;
  }
  
  //-----------------------------------
  // property: woodfinishPicker
  /**
   * A boolean indicating whether or not a wood finish picker is being
   * used to add the item to the gift/wish list.
   */
  private boolean mWoodFinishPicker = false;
  
  /**
   * Sets mWoodFinishPicker
   * @param pWoodFinishPicker
   */
  public void setWoodFinishPicker(boolean pWoodFinishPicker){
    mWoodFinishPicker = pWoodFinishPicker;
  }
  
  /**
   * Gets mWoodFinishPicker
   * @return
   */
  public boolean isWoodFinishPicker(){
    return mWoodFinishPicker;
  }
  
  public SessionBean getSessionBean()
  {
    return mSessionBean;
  }

  public void setSessionBean(SessionBean pSessionBean)
  {
    mSessionBean = pSessionBean;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection getEventTypes()
  {
    Collection availableEventTypes = super.getEventTypes();
    if (availableEventTypes == null || availableEventTypes.isEmpty())
    {
      return availableEventTypes;
    } else
    {
      return sortEventTypes(availableEventTypes);
    }
  }
  
  /**
   * Sort event types in alphabetical ascending order.
   * The event type with name equal to {@link #LAST_EVENT_TYPE_NAME} will be returned last. 
   * @param pEventTypes - collection of events to be sorted.
   * @return a sorted list of events.
   */
  protected List<String> sortEventTypes(Collection<String> pEventTypes)
  {
    List<String> eventTypes = new ArrayList<String>(pEventTypes);
    Collections.sort(eventTypes, new Comparator<String>()
        {
          public int compare(String o1, String o2)
          {
            if (LAST_EVENT_TYPE_NAME.equals(o1))
            {
              return Integer.MAX_VALUE;
            } else if (LAST_EVENT_TYPE_NAME.equals(o2))
            {
              return Integer.MIN_VALUE;
            } else
            {
              return o1.compareTo(o2);
            }
          }
        });
    return eventTypes;
  }
  
  /**
   * Saves sku and product IDs to be added to wishlist into 'skuIdToAdd' and 'productIdToAdd' transient profile properties correspondingly
   * @param pRedirectURL - the URL to redirect to after login
   * @throws ServletException if anything goes wrong
   * @throws InvalidParameterException 
   * @throws CommerceItemNotFoundException 
   */
  @SuppressWarnings("unchecked") //ok, we know which values (strings) we put on these keys
  private void saveSkuAndProductIntoSession(String pRedirectURL) throws ServletException, CommerceItemNotFoundException, InvalidParameterException
  {
    if (getItemIds()!= null && getItemIds().length > 0){
      String commerceItemId = getItemIds()[0];
      CommerceItem item = getOrder().getCommerceItem(commerceItemId);
      if (item != null){
        getSessionBean().getValues().put(SessionBean.COMMERCE_ITEM_ID_PROPERTY_NAME, commerceItemId);
        getSessionBean().getValues().put(SessionBean.SKU_ID_TO_GIFTLIST_PROPERTY_NAME, item.getCatalogRefId());
        getSessionBean().getValues().put(SessionBean.PRODUCT_ID_TO_GIFTLIST_PROPERTY_NAME,  item.getAuxiliaryData().getProductId());  
      }
    }else{
      getSessionBean().getValues().put(SessionBean.SKU_ID_TO_GIFTLIST_PROPERTY_NAME, getCatalogRefIds()[0]);
      getSessionBean().getValues().put(SessionBean.PRODUCT_ID_TO_GIFTLIST_PROPERTY_NAME, getProductId());
    }
    
    getSessionBean().getValues().put(SessionBean.GIFTLIST_ID_PROPERTY_NAME, getProfile().isTransient() ? null : getGiftlistId());
    getSessionBean().getValues().put(SessionBean.QUANTITY_TO_ADD_TO_GIFTLIST_PROPERTY_NAME, new Long(getQuantity()));
    getSessionBean().getValues().put(SessionBean.REDIRECT_AFTER_LOGIN_URL_PROPERTY_NAME, pRedirectURL);
  }

  protected void preValidateAddItemTogiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException
  {
    //add form exception if quantity less or equal zero
    if (getQuantity() <= 0) 
    {
      String msg = formatUserMessage(MSG_QUANTITY_LESSTHAN_OR_EQUALTO_ZERO, pRequest, pResponse);
      addFormException(new DropletFormException(msg, MSG_ERROR_ADDING_TO_GIFTLIST));
    }
    
    if (getCatalogRefIds() == null || getCatalogRefIds().length == 0 //There should be specified SKU IDs to be added 
        || getCatalogRefIds()[0] == null /* This condition means that 
            catalogRefIds items are explicitly set from http request parameters */)
    {
      addFormException(new DropletException(formatUserMessage(MSG_NO_ITEMS_TO_ADD, pRequest, pResponse)));
    }
    
    if(getCatalogRefIds()[0].trim().length() == 0){
      if(isUseWishlist()){
        if(!isWoodFinishPicker()){
          addFormException(new DropletException(formatUserMessage(MSG_ERROR_NO_COLOR_SIZE_SELECTED_WISHLIST, pRequest, pResponse)));
        }
        else{
          addFormException(new DropletException(formatUserMessage(MSG_ERROR_NO_FINISH_SELECTED_WISHLIST, pRequest, pResponse)));
        }
      }
      else{
        if(!isWoodFinishPicker()){
          addFormException(new DropletException(formatUserMessage(MSG_ERROR_NO_COLOR_SIZE_SELECTED_GIFTLIST, pRequest, pResponse)));
        }
        else{
          addFormException(new DropletException(formatUserMessage(MSG_ERROR_NO_FINISH_SELECTED_GIFTLIST, pRequest, pResponse)));
        }
      }
    }
  }
  
  /**
   * Before adding an item to the giftlist check if user is explicitly logged in.
   * If not store product\sku\giftlist info into the session.
   *
   * @param pRequest DynamoHttpServletRequest
   * @param pResponse DynamoHttpServletResponse
   *
   * @throws ServletException if there was an error while executing the code
   * @throws IOException if there was an error with servlet io
   */
  public void preAddItemToGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    if (!isUserLoggedIn()){
      // No good - the user needs to be explicitly logged in.  Throw a dummy exception (the user won't
      // see it anyway), and redirect to the loginURL
      addFormException(new DropletException("notLoggedIn", "notLoggedIn"));
      setAddItemToGiftlistErrorURL(getAddItemToGiftlistLoginURL());

      //If not logged in, save sku and product to be added into SessionBean properties for future use
      //(after the user has logged in)
      //when the user is logged in, this sku will be added to user's wish list
      try {
        saveSkuAndProductIntoSession(getAddItemToGiftlistSuccessURL());
      } catch (CommerceItemNotFoundException ex) {
        processException(ex, MSG_ERROR_ADDING_TO_GIFTLIST, pRequest, pResponse);
      } catch (InvalidParameterException ex) {
        processException(ex, MSG_ERROR_ADDING_TO_GIFTLIST, pRequest, pResponse);
      }
      
      return;
    }
    
    super.preAddItemToGiftlist(pRequest, pResponse);
  }
  
  /**
   * Checks if user is explicitly logged in.
   * @return true if user is logged in.
   * @throws ServletException
   */
  public boolean isUserLoggedIn() throws ServletException{
    // First, check to see if the user is logged in.
    PropertyManager profilePropertyManager = getProfilePropertyManager();
    Integer securityStatus = (Integer) getProfile()
                                         .getPropertyValue(profilePropertyManager.getSecurityStatusPropertyName());
    int securityStatusCookie = profilePropertyManager.getSecurityStatusCookie();

    return (securityStatus.intValue() > securityStatusCookie);
  }
  
  
  /**
   * Before moving item to the giftlist check if user is explicitly logged in.
   * If not store product\sku\giftlist info into the session.
   *  
   */
  public void preMoveItemsFromCart(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    
    if (!isUserLoggedIn()){
      // No good - the user needs to be explicitly logged in.  Throw a dummy exception (the user won't
      // see it anyway), and redirect to the loginURL
      addFormException(new DropletException("notLoggedIn", "notLoggedIn"));
      setMoveItemsFromCartErrorURL(getMoveItemsFromCartLoginURL());

      //If not logged in, save sku and product to be added into SessionBean properties for future use
      //(after the user has logged in)
      //when the user is logged in, this sku will be added to user's wish list
      try {
        saveSkuAndProductIntoSession(getMoveItemsFromCartSuccessURL());
      } catch (CommerceItemNotFoundException ex) {
        processException(ex, MSG_ERROR_ADDING_TO_GIFTLIST, pRequest, pResponse);
      } catch (InvalidParameterException ex) {
        processException(ex, MSG_ERROR_ADDING_TO_GIFTLIST, pRequest, pResponse);
      }
      
      return;
    }
    super.preMoveItemsFromCart(pRequest, pResponse);
  }

  /**
   * Initializes all the form values from the provided gift list.
   * @param pGiftlist - gift list
   */
  public void setGiftlist(RepositoryItem pGiftlist) {
    GiftlistTools tools = getGiftlistManager().getGiftlistTools();
    //populate the form handler with values form the gift list
    setGiftlistId(pGiftlist.getRepositoryId());
    setEventName((String) pGiftlist.getPropertyValue(tools.getEventNameProperty()));
    setEventType((String) pGiftlist.getPropertyValue(tools.getEventTypeProperty()));
    setDescription((String) pGiftlist.getPropertyValue(tools.getDescriptionProperty()));

    Date eventDate = (Date) pGiftlist.getPropertyValue(tools.getEventDateProperty());
    setIsPublished((Boolean) pGiftlist.getPropertyValue(tools.getPublishedProperty()));
    setEventDate(eventDate);

    RepositoryItem shippingaddress = (RepositoryItem) pGiftlist.getPropertyValue(tools.getShippingAddressProperty());

    if (shippingaddress != null) {
      setShippingAddressId(shippingaddress.getRepositoryId());
    }

    setInstructions((String) pGiftlist.getPropertyValue(tools.getInstructionsProperty()));
  }

  /**
   * Clears the form handler property values.
   * @param pRequest - http request
   * @param pResponse - http response
   * @return true if success, false - otherwise
   * @throws ServletException if servlet error occurs
   * @throws IOException if IO error occurs
   * @throws CommerceException if commerce error occurs
   */
  public boolean handleClearForm(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException {
    //populate the form handler with values form the gift list
    setGiftlistId(null);
    setEventName(null);
    setEventType(null);
    setDescription(null);
    setIsPublished(Boolean.TRUE);
    setEventDate(new Date(System.currentTimeMillis()));
    setShippingAddressId(null);
    setInstructions(null);
    resetFormExceptions();
    return true;
  }

  /**
   * Clears form errors as part of the cancel operation.
   * @param pRequest - http request
   * @param pResponse - http response
   * @throws ServletException if servlet error occurs
   * @throws IOException if IO error occurs
   * @return True if success, False - otherwise
   */
  public boolean handleCancel(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    resetFormExceptions();

    return super.handleCancel(pRequest, pResponse);
  }
  
  /**
   * Stores entered by user data to the sessions-scoped component before moving to
   * Add New Address URL. The stored data can be retrieved later during form initialization.   
   * @param pRequest - http request
   * @param pResponse - http response
   * @throws ServletException if servlet error occurs
   * @throws IOException if IO error occurs
   * @return True if success, False - otherwise
   */
  public boolean handleMoveToNewGiftListAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException
   {
    // ignore not entered required fields
    resetFormExceptions();
    storeNewGiftListData();
    return checkFormRedirect (getMoveToNewGiftListAddressSuccessURL(), getMoveToNewGiftListAddressErrorURL(), pRequest, pResponse);
  }
  
  /**
   * Stores entered by user data to the session-scoped component.
   */
  public void storeNewGiftListData(){
    Map sessionBeanValues = getSessionBean().getValues();
    
    sessionBeanValues.put(GIFT_LIST_EVENT_NAME_MAP_KEY, getEventName());
    sessionBeanValues.put(GIFT_LIST_MONTH_MAP_KEY, getMonth());
    sessionBeanValues.put(GIFT_LIST_DATE_MAP_KEY, getDate());
    sessionBeanValues.put(GIFT_LIST_YEAR_MAP_KEY, getYear());
    sessionBeanValues.put(GIFT_LIST_SHIPPING_ADDRESS_MAP_KEY, getShippingAddressId());
    sessionBeanValues.put(GIFT_LIST_EVENT_TYPE_MAP_KEY, getEventType());
    sessionBeanValues.put(GIFT_LIST_PUBLISHED_MAP_KEY, getIsPublished());
    sessionBeanValues.put(GIFT_LIST_DESCRIPTION_MAP_KEY, getDescription());
    sessionBeanValues.put(GIFT_LIST_INSTRUCTIONS_MAP_KEY, getInstructions());
  }
  
  /**
   * Initializes Gift list form with previously entered data stored in the session-scoped
   * component.
   * 
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true
   */
  public boolean handleInitializeGiftListForm(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse){
    // Initialize gift list form with values previously entered by user in this session
    Map sessionBeanValues = getSessionBean().getValues();
    setEventName((String)sessionBeanValues.get(GIFT_LIST_EVENT_NAME_MAP_KEY));
    setMonth((Integer)sessionBeanValues.get(GIFT_LIST_MONTH_MAP_KEY));
    setDate((String)sessionBeanValues.get(GIFT_LIST_DATE_MAP_KEY));
    setYear((String)sessionBeanValues.get(GIFT_LIST_YEAR_MAP_KEY));
    setShippingAddressId((String)sessionBeanValues.get(GIFT_LIST_SHIPPING_ADDRESS_MAP_KEY));
    setEventType((String)sessionBeanValues.get(GIFT_LIST_EVENT_TYPE_MAP_KEY));
    setIsPublished((Boolean)sessionBeanValues.get(GIFT_LIST_PUBLISHED_MAP_KEY));
    setDescription((String)sessionBeanValues.get(GIFT_LIST_DESCRIPTION_MAP_KEY));
    setInstructions((String)sessionBeanValues.get(GIFT_LIST_INSTRUCTIONS_MAP_KEY));
    return true;
  }

  /**
   * Overriden method of GiftlistFormHandler class. Called when the customer selects create to create a new
   * giftlist. Before invoking parent's save gift list logic, this method calls the getEventDate() method to
   * set the correct value of date in parent class.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */
  public boolean handleSaveGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException {
    try {
      getEventDate();
    } catch (InvalidDateException ex) {
      throw new CommerceException(ResourceUtils.getMsgResource(MSG_INVALID_DATE, MY_RESOURCE_NAME, sResourceBundle));
    }

    return super.handleSaveGiftlist(pRequest, pResponse);
  }
  
  /**
   * Does nothing.
   * This method is needed for the 'handleAddItemToGiftlist' to be called when accessing Giftlist handler from anchor tag.
   * @param pValue - value to be set, not used
   * @throws ServletException never thrown
   * @throws IOException never thrown
   */
  public void setAddItemToGiftlist(boolean pValue) throws ServletException, IOException
  {
    
  }
  
  /**
   * Overridden method of GiftlistFormHandler class. called when the user hits the submit
   * button on a product page to add an item to the giftlist.  This handler
   * looks up the product in the catalog and returns a sku.  It then
   * looks for an existing item in the giftlist with this sku id.  if it
   * exists, the quantity desired is incremented by the quantity specified.
   * if it doesn't exist, it creates the item and adds it to the giftlist
   * specified in giftlist id.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */
  public boolean handleAddItemToGiftlist(DynamoHttpServletRequest pRequest,
                                         DynamoHttpServletResponse pResponse)
      throws ServletException, IOException, CommerceException 
  {
    preValidateAddItemTogiftlist(pRequest, pResponse);
    return super.handleAddItemToGiftlist(pRequest, pResponse);
  }

  /**
   * Overriden method of GiftlistFormHandler class. Called when the customer selects update giftlist. Before
   * invoking parent's gift list update logic, this method calls the getEventDate() method to set the correct
   * value of date in parent class.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */
  public boolean handleUpdateGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException {
    try {
      getEventDate();
    } catch (InvalidDateException ex) {
      throw new CommerceException(ResourceUtils.getMsgResource(MSG_INVALID_DATE, MY_RESOURCE_NAME, sResourceBundle));
    }

    return super.handleUpdateGiftlist(pRequest, pResponse);
  }
  
  /**
   * The combined handler that allows to update gift list and its items at the same time.
   * 
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */
  public boolean handleUpdateGiftlistAndItems(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException {
    
    String pProfileId = (String) getProfile().getRepositoryId();
    try{
      //If any form errors found, redirect to error URL:
      if (! checkFormRedirect(null, getUpdateGiftlistAndItemsErrorURL(), pRequest, pResponse))
        return false;
      // pre process update
      preUpdateGiftlist(pRequest, pResponse);

      // if new address, create and add to address book.
      if ( getIsNewAddress() )
        setShippingAddressId(createNewShippingAddress());

      // call manager class to update list.
      getGiftlistManager().updateGiftlist(pProfileId, getGiftlistId(), getIsPublished().booleanValue(), getEventName(), getEventDate(), getEventType(), getDescription(), getComments(), getShippingAddressId(), getInstructions(),getSiteId());

      // post process update
      postUpdateGiftlist(pRequest, pResponse);
      
      //If any form errors found, redirect to error URL:
      if (! checkFormRedirect(null, getUpdateGiftlistAndItemsErrorURL(), pRequest, pResponse))
        return false;
      
      // update gift list items now
      preUpdateGiftlistItems(pRequest, pResponse);
      
      if (validateGiftlistId(pRequest, pResponse)) {
        updateGiftlistItems(pRequest, pResponse);

        //If any form errors found, redirect to error URL:
        if (! checkFormRedirect(null, getUpdateGiftlistAndItemsErrorURL(), pRequest, pResponse))
          return false;
        
        postUpdateGiftlistItems(pRequest, pResponse);
      }
    }

    catch (InvalidDateException ide){
      processException(ide, MSG_INVALID_EVENT_DATE, pRequest, pResponse);
    }
    catch (CommerceException ce){
      processException(ce, MSG_ERROR_SAVING_GIFTLIST, pRequest, pResponse);
    }
    
    //If NO form errors are found, redirect to the success URL.
    //If form errors are found, redirect to the error URL.
    return checkFormRedirect (getUpdateGiftlistAndItemsSuccessURL(), getUpdateGiftlistAndItemsErrorURL(), pRequest, pResponse);
  }

  /**
   * HandleRemoveItemsFromGiftlist is called when the user hits the "delete"
   * button on the wishlist page.  This handler removes the specified
   * gift Ids from the specified giftlist.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */
  public boolean handleRemoveItemsFromGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException {
    try {
      //If any form errors found, redirect to error URL:
      if (!checkFormRedirect(null, getRemoveItemsFromGiftlistErrorURL(), pRequest, pResponse)) {
        return false;
      }

      if (validateGiftlistId(pRequest, pResponse)) {
        removeItemsFromGiftlist(pRequest, pResponse);

      }
    }
    catch (CommerceException oce) {
      processException(oce, MSG_ERROR_ADDING_TO_GIFTLIST, pRequest, pResponse);
    }

    return checkFormRedirect(getRemoveItemsFromGiftlistSuccessURL(), getRemoveItemsFromGiftlistErrorURL(), pRequest,
      pResponse);
  }

   /**
   * Removes the given items to the selected giftlist.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */
  protected void removeItemsFromGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException {
    GiftlistManager mgr = getGiftlistManager();
    String pGiftlistId = getGiftlistId();
    String[] items = getRemoveGiftitemIds();

    if (items == null) {
      return;
    }

    try {
      for (int i = 0; i < items.length; i++) {
        String id = items[i];
        mgr.removeItemFromGiftlist(pGiftlistId, id);
      }
    } catch (RepositoryException ex) {
      processException(ex, MSG_ERROR_UPDATING_GIFTLIST_ITEMS, pRequest, pResponse);
    }
  }

  @Override
  protected void updateOrder(CommerceItem pItem, long pQuantity, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws InvalidGiftQuantityException, IOException, ServletException
  {
    super.updateOrder(pItem, pItem.getQuantity(), pRequest, pResponse);
  }
}
