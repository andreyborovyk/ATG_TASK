/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

package atg.commerce.gifts;

import atg.repository.*;
import atg.droplet.TransactionalFormHandler;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.profile.CommerceProfileTools;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.ItemRemovedFromOrder;
import atg.commerce.util.PipelineUtils;
import atg.commerce.util.PipelineErrorHandler;

import atg.core.util.DateDoodads;
import atg.core.util.ResourceUtils;
import atg.core.util.Address;
import atg.nucleus.naming.ParameterName;
import atg.userprofiling.*;
import atg.service.pipeline.*;

import java.io.IOException;
import javax.servlet.ServletException;
import java.util.ResourceBundle;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import java.util.Hashtable;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.beans.PropertyEditor;


/**
 * This class provides convenient form handling methods for operating on
 * the current customer's giftlists.  It can be used to create new giftlists
 * edit giftlists and add items to the giftlist during the shopping process.
 *
 * <p>
 *
 * Giftlist management is the core functionality of this form handler.
 * it controls creating, updating and item management to all of the customer's
 * wishlist/giftlist registries.  This includes creating, updating, publishing,
 * and deleting gift registries as well as adding items to the lists
 * This functionality is invoked via the various handleXXX methods of
 * the form handler.
 *
 * <p>
 *
 * All handle methods in this form handler mirror a similiar pattern.
 * Each handleXXX process, some have an associated preXXX and postXXX method.
 * For example, the <code>handleAddItemToGiftlist</code> has an associate
 * <code>preAddItemToGiftlist</code> and <code>postAddItemToGiftlist</code>.
 * These pre/post methods provide an easy way for a user to extend the
 * functionality of this form handler.
 *
 * @author Kerwin D. Moy
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/gifts/GiftlistFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo
 *   description: A form handler which can be used to manipulate the user's gift list
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory Gifts
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 * @see atg.droplet.GenericFormHandler
 *
 */

public class GiftlistFormHandler
extends TransactionalFormHandler implements PipelineErrorHandler
{
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/gifts/GiftlistFormHandler.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Resource Bundle Name
  /** Name of the resource */
  protected static final String MY_RESOURCE_NAME = "atg.commerce.gifts.GiftlistResources";
  /** Name of the locale parameter */
  protected static final ParameterName LOCALE_PARAM = ParameterName.getParameterName("locale");

  //-------------------------------------
  // Constants
  //-------------------------------------
  // Resource message keys
  public static final String MSG_QUANTITY_LESSTHAN_OR_EQUALTO_ZERO = "quantityLessThanOrEqualToZero";
  public static final String MSG_NO_GIFTLIST_TO_MODIFY = "noGiftlistToModify";
  public static final String MSG_NO_ITEMS_TO_ADD = "noItemsToAdd";
  public static final String MSG_ERROR_ADDING_TO_GIFTLIST = "errorAddingToGiftlist";
  public static final String MSG_NO_SKU_WITH_ID = "noSKUWithId";
  public static final String MSG_ERROR_REMOVING_RROM_GIFTLIST = "errorRemovingFromGiftlist";
  public static final String MSG_NO_ITEM_FOUND = "noItemFound";
  public static final String MSG_INVALID_EVENT_DATE = "invalidEventDate";
  public static final String MSG_ERROR_SAVING_GIFTLIST = "errorSavingGiftlist";
  public static final String MSG_ERROR_DELETING_GIFTLIST = "errorDeletingGiftlist";
  public static final String MSG_ERROR_UPDATING_GIFTLIST_ITEMS = "errorUpdatingGiftlistItems";
  public static final String MSG_INVALID_GIFTLIST_QUANTITY = "invalidGiftlistQuantity";
  public static final String MSG_ERROR_NO_SUCH_GIFTLIST = "errorNoSuchGiftlist";

  // This variable was added to denote that the quantity field was populated with a value that
  // is not valid.  This technique was used due to the difficulty of fixing bug# 54914 without
  // either a major re-write or breaking backwards compatibility.
  public long QUANTITY_NOT_VALID = -9999999;
  
  //-------------------------------------
  // Member Variables
  //-------------------------------------
  /** Resource Bundle **/
  protected static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  protected GiftlistDateUtil dateUtil = new GiftlistDateUtil();

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // property: giftlistManager
  GiftlistManager mGiftlistManager;

  /**
   * Sets property giftlistManager.
   * @param pGiftlistManager the giftlistManager class which provides a high level business layer interface to giftlists.
   * @beaninfo description:  the giftlistManager class which provides a high level business layer interface to giftlists.
   **/
  public void setGiftlistManager(GiftlistManager pGiftlistManager) {
    mGiftlistManager = pGiftlistManager;
  }

  /**
   * Returns property giftlistManager.
   * @return The value of the property GiftlistManager.
   **/
  public GiftlistManager getGiftlistManager() {
    return mGiftlistManager;
  }

  //-------------------------------------
  // property: OrderManager
  OrderManager mOrderManager;

  /**
   * Sets property orderManager.
   * @param pOrderManager the OrderManager class which provides a high level business layer interface to orders.
   * @beaninfo description:  the OrderManager class which provides a high level business layer interface to orders.
   **/
  public void setOrderManager(OrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  /**
   * Returns property orderManager.
   * @return The value of the property OrderManager.
   **/
  public OrderManager getOrderManager() {
    return mOrderManager;
  }

  //-------------------------------------
  // property: catalogTools
  CatalogTools mCatalogTools;

  /**
   * Sets property catalogTools.
   * @param pCatalogTools the catalogTools which provides low level operations on the catalog.
   * @beaninfo description:  the catalogTools which provides low level operations on the catalog.
   **/
  public void setCatalogTools(CatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  /**
    * Returns property catalogTools.
    * @return The value of the property CatalogTools.
   **/
   public CatalogTools getCatalogTools() {
       return mCatalogTools;
   }

  //---------------------------------------------------------------------------
  // property: sendItemRemovedMessages
  //---------------------------------------------------------------------------

  boolean mSendItemRemovedMessages = true;

  /**
   * Specify whether to send ItemRemovedFromOrder messages when moving
   * items from the shopping cart to a gift list or wish list.  The default
   * value is true; applications that require Dynamo 5.1 behavior, where
   * a message was not sent, can set this property to false instead.
   **/
  
  public void setSendItemRemovedMessages(boolean pSendItemRemovedMessages) {
    mSendItemRemovedMessages = pSendItemRemovedMessages;
  }

  /**
   * Query whether to send ItemRemovedFromOrder messages when moving
   * items from the shopping cart to a gift list or wish list.
   **/
  
  public boolean isSendItemRemovedMessages() {
    return mSendItemRemovedMessages;
  }

  
  //---------------------------------------------------------------------------
  // property: pipelineManager
  //---------------------------------------------------------------------------

  PipelineManager mPipelineManager;

  /**
   * Set the pipeline manager used to generate ItemRemovedFromOrder messages
   **/
  
  public void setPipelineManager(PipelineManager pPipelineManager) {
    mPipelineManager = pPipelineManager;
  }

  /**
   * Get the pipeline manager used to generate ItemRemovedFromOrder messages
   **/
  
  public PipelineManager getPipelineManager() {
    return mPipelineManager;
  }


  //-------------------------------------
  // property: ItemRemovedFromOrderEventType
  //-------------------------------------
  String mItemRemovedFromOrderEventType = ItemRemovedFromOrder.TYPE;

  /**
   * Set the JMS message name for the item removed from order message
   **/

  public void setItemRemovedFromOrderEventType(String pItemRemovedFromOrderEventType) {
    mItemRemovedFromOrderEventType = pItemRemovedFromOrderEventType;
  }

  /**
   * Returns the JMS message name for the item removed from order message.
   **/

  public String getItemRemovedFromOrderEventType() {
    return mItemRemovedFromOrderEventType;
  }

  
  //-------------------------------------
  // property: profileTools
  CommerceProfileTools mProfileTools;

  /**
   * Sets property profileTools
   * @param pProfileTools the ProfileTools for Commerce which provides low level operations on the current user's profile.
   * @beaninfo description:  the ProfileTools for Commerce which provides low level operations on the current user's profile.
   **/
  public void setProfileTools(CommerceProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }

  /**
    * Returns property profileTools.
    * @return The value of the property profileTools.
   **/
   public CommerceProfileTools getProfileTools() {
       return mProfileTools;
   }

  //-------------------------------------
  // property: ShoppingCart
  //-------------------------------------
  OrderHolder mShoppingCart;

  /**
   * Sets property ShoppingCart
   * @param pShoppingCart the default shopping cart for the current customer.
   * @beaninfo description:  the default shopping cart for the current customer.
   **/
  public void setShoppingCart(OrderHolder pShoppingCart) {
    mShoppingCart = pShoppingCart;
  }

  /**
   * Returns property ShoppingCart
   * @return The value of the property ShoppingCart.
   **/
  public OrderHolder getShoppingCart() {
    return mShoppingCart;
  }

  //-------------------------------------
  // property: Order
  //-------------------------------------
  /**
   * Returns property Order, if the shoppingCart property is not null, then
   * return the current order of the shopping cart order holder
   * @return The value of the property Order.
   **/
  public Order getOrder() {
    if (getShoppingCart() != null)
      return getShoppingCart().getCurrent();
    else
      return null;
  }

  //-------------------------------------
  // property: DefaultLocale
  Locale mDefaultLocale;

  /**
   * Sets property DefaultLocale
   * @param pDefaultLocale the default locale for the current customer.
   * @beaninfo description:  the default locale for the current customer.
   **/
  public void setDefaultLocale(Locale pDefaultLocale) {
    mDefaultLocale = pDefaultLocale;
  }

  /**
   * Returns property DefaultLocale. If the property value is null, then
   * JVM's default locale is returned.
   * @return The value of the property DefaultLocale.
   **/
  public Locale getDefaultLocale() {
    if (mDefaultLocale != null)
      return mDefaultLocale;
    else
      return Locale.getDefault();
  }

  //-------------------------------------
  // property: UseRequestLocale
  boolean mUseRequestLocale = true;

  /**
   * Sets property UseRequestLocale
   * @param pUseRequestLocale boolean to specify whether to use request locale.
   * @beaninfo description:  boolean to specify whether to use request locale.
   **/
  public void setUseRequestLocale(boolean pUseRequestLocale) {
    mUseRequestLocale = pUseRequestLocale;
  }

  /**
   * Returns property UseRequestLocale
   * @return The value of the property UseRequestLocale.
   **/
  public boolean isUseRequestLocale() {
    return mUseRequestLocale;
  }

  /**
   * Sets the eventName.
   * @param pEventName The property to store the giftlist event name.
   * @beaninfo description:  The property to store the giftlist event name.
   */
  public void setEventName(String pEventName) {
    mEventName = pEventName;
  }

  //-------------------------------------
  // property: eventName
  private String mEventName = null;

  /**
   * Returns the eventName.
   * @return The value of the property eventName.
   */
  public String getEventName() {
    return mEventName;
  }

  //-------------------------------------
  // property: month
  Integer mMonth = null;

  /**
   * Sets property month.
   * @param pMonth The property to store the month value of the event date.
   * @beaninfo description:  The property to store the month value of the event date.
   **/
  public void setMonth(Integer pMonth) {
    mMonth = pMonth;
    updateEventDate();
  }

  /**
   * Returns property month.
   * @return The value of the property month.
   **/
  public Integer getMonth() {
    return (Integer.valueOf(mEventDate.get(Calendar.MONTH)));
  }

  //-------------------------------------
  // property: date
  String mDate;

  /**
   * Sets property date.
   * @param pDate The property to store the day of the month value of the event date.
   * @beaninfo description:  The property to store the day of the month value of the event date.
   **/
  public void setDate(String pDate) {
    mDate = pDate;
    updateEventDate();
  }

  /**
   * Returns property date.
   * @return The value of the property date.
   **/
  public String getDate() {
    return (Integer.valueOf(mEventDate.get(Calendar.DATE)).toString());
  }

  //-------------------------------------
  // property: year
  String mYear;

  /**
   * Sets property year.
   * @param pYear  The property to store the year value of the event date.
   * @beaninfo description:  The property to store the year value of the event date.
   **/
  public void setYear(String pYear) {
    mYear = pYear;
    updateEventDate();
  }

  /**
   * Returns property year.
   * @return The value of the property year.
   **/
  public String getYear() {
    return (Integer.valueOf(mEventDate.get(Calendar.YEAR)).toString());
  }

  //-------------------------------------
  // property: eventDate
  Calendar mEventDate = Calendar.getInstance();

  /**
   * Sets eventDate property.
   * @param pEventDate The property to store the event date as a Calendar object.
   * @beaninfo description:  The property to store the event date as a Calendar object.
   */
  public void setEventDate(Date pEventDate){
    mEventDate.setTime(pEventDate);
    
    //Clear out set date/month/year values
    //This way the mEventDate values are used for all the date values
    mDate = null;
    mMonth = null;
    mYear = null;
    
    //Call updateEventDate to set the Date, Month, and Year values
    updateEventDate();
  }

  /**
   * Returns eventDate property.
   * @return The value of the property eventDate.
   * @exception InvalidDateException if an invalid date was entered for giftlist.
   */
  public Date getEventDate() throws InvalidDateException {
    return mEventDate.getTime();
  }

  //-------------------------------------
  // property:  eventType
  String mEventType;

  /**
   * Sets eventType property.
   * @param pEventType The property to store the event type.
   * @beaninfo description:  The property to store the event type.
   */
  public void setEventType(String pEventType){
    mEventType = pEventType;
  }

  /**
   * Returns eventType property.
   * @return The value of the property eventType.
   */
  public String getEventType(){
    return mEventType;
  }

  //-------------------------------------
  // property:  description

  String mDescription;

  /**
   * Sets property description.
   * @param pDescription  The property to store the giftlist description.
   * @beaninfo description:  The property to store the giftlist description.
   */
  public void setDescription(String pDescription){
    mDescription = pDescription;
  }

  /**
   * Returns property description.
   * @return The value of the property description.
   */
  public String getDescription(){
    return mDescription;
  }

  //-------------------------------------
  // property:  comments
  String mComments;

  /**
   * Sets property comments.
   * @param pComments  The property to store the giftlist comments.
   * @beaninfo description:  The property to store the giftlist comments.
   */
  public void setComments(String pComments){
    mComments = pComments;
  }

  /**
   * Returns property comments.
   * @return The value of the property comments.
   */
  public String getComments(){
    return mComments;
  }

  //-------------------------------------
  // property:  instructions
  String mInstructions;

  /**
   * Sets property instructions.
   * @param pInstructions  The property to store the giftlist instructions.
   * @beaninfo description:  The property to store the giftlist instructions.
   */
  public void setInstructions(String pInstructions){
    mInstructions = pInstructions;
  }

  /**
   * Returns property instructions.
   * @return The value of the property instructions.
   */
  public String getInstructions(){
    return mInstructions;
  }

  //-------------------------------------
  // property: specialInstructions
  HashMap mSpecialInstructions = new HashMap(13);

  /**
   * Sets property specialInstructions.
   * @param pSpecialInstructions  The property to store the giftlist specialInstructions.
   * @beaninfo description:  The property to store the giftlist specialInstructions.
   */
  public void setSpecialInstructions(Map pSpecialInstructions)
  {
    HashMap map;

    if (pSpecialInstructions instanceof HashMap)
      map = (HashMap) pSpecialInstructions;
    else
      map = new HashMap(pSpecialInstructions);

    mSpecialInstructions = map;
  }

  /**
   * Returns property specialInstructions.
   * @return The value of the property specialInstructions.
   */
  public Map getSpecialInstructions() {
    return mSpecialInstructions;
  }

  //-------------------------------------
  // property:  isPublic
  Boolean mIsPublic = Boolean.valueOf(false);

  /**
   * Sets property isPublic
   * @param pIsPublic  The property to store the property isPublic which specifies if a giftlist <b><i>can be</i></b> visible to persons other than the owner.
   * @beaninfo description:  The property to store the property isPublic which specifies if a giftlist <b><i>can be</i></b> visible to persons other than the owner.
   */
   public void setIsPublic(Boolean pIsPublic){
     mIsPublic = pIsPublic;
   }

  /**
   * Returns property isPublic
   * @return The value of the property isPublic.
   */

  public Boolean getIsPublic(){
    return mIsPublic;
  }

  //-------------------------------------
  // property:  isPublished
  Boolean mIsPublished = Boolean.FALSE;

  /**
   * Sets the isPublished flag
   * @param pIsPublished  The property to store the property isPublic which specifies if a giftlist <b><i>is</i></b> visible to persons other than the owner.
   * @beaninfo description:  The property to store the property isPublic which specifies if a giftlist <b><i>is</i></b> visible to persons other than the owner.
   */
  public void setIsPublished(Boolean pIsPublished){
    mIsPublished = pIsPublished;
  }

  /**
   * Returns the IsPublished flag
   * @return The value of the property isPublished.
   */
  public Boolean getIsPublished(){
    return mIsPublished;
  }


  //-------------------------------------
  // property:  giftlistId
  String mGiftlistId = "";

  /**
   * Sets property giftlistId.
   * @param pGiftlistId  The property to store the giftlistId property of the current giftlist.
   * @beaninfo description:  The property to store the giftlistId property of the current giftlist.
   **/
  public void setGiftlistId(String pGiftlistId) {
    mGiftlistId = pGiftlistId;
  }

  /**
   * Returns property giftlistId.
   * @return The value of the property giftlistId.
   **/
  public String getGiftlistId() {
    return mGiftlistId;
  }

  //-------------------------------------
  // property: profile
  RepositoryItem mProfile;

  /**
   * Sets property profile.
   * @param pProfile  The property to store the profile of the current customer.
   * @beaninfo description:  The property to store the profile of the current customer.
   **/
  public void setProfile(RepositoryItem pProfile) {
    mProfile = pProfile;
  }

  /**
   * Returns property Profile
   * @return The value of the property profile.
   **/
  public RepositoryItem getProfile() {
    return mProfile;
  }

  //-------------------------------------
  // property: GiftlistRepository
  Repository mGiftlistRepository;

  /**
   * Sets property GiftlistRepository
   * @param pGiftlistRepository  The property to store the value of the giftlist repository.
   * @beaninfo description:  The property to store the value of the giftlist repository.
   **/
  public void setGiftlistRepository(Repository pGiftlistRepository) {
    mGiftlistRepository = pGiftlistRepository;
  }

  /**
   * Returns property GiftlistRepository
   * @return The value of the property giftlistRepository.
   **/
  public Repository getGiftlistRepository() {
    return mGiftlistRepository;
  }

  //-------------------------------------
  // property: addresses
  Map mAddresses;

  /**
   * Returns property addresses
   * @return The value of the property addresses.
   **/
  public Map getAddresses() {
    return getProfileTools().getProfileAddresses(getProfile());
  }

  //-------------------------------------
  // property: months
  String [] mMonths;

  /**
   * Returns property months
   * @return The value of the property months.
   **/
  public String [] getMonths() {
    return dateUtil.getMonths();
  }

  //-------------------------------------
  // property: dates
  String [] mDates;

  /**
   * Returns property dates
   * @return The value of the property dates.
   **/
  public String [] getDates() {
    return dateUtil.getDates();
  }

  //-------------------------------------
  // property: years
  String [] mYears;

  /**
   * Returns property years
   * @return The value of the property years.
   **/
  public String [] getYears() {
    return dateUtil.getYears();
  }

  //-------------------------------------
  // property: EventTypes
  Collection mEventTypes;

  /**
   * eventType is an enumerated property in the giftlist repository.
   * This will return a Collection of eventTypes found in the repository
   * item descriptor.  This Collection may be used in a select box in order
   * to choose one to store in the giftlist eventType property.
   * @return The value of the property eventTypes.
   **/
  public Collection getEventTypes() {
   GiftlistManager mgr = getGiftlistManager();
   try{
     RepositoryItemDescriptor itemDesc =
        getGiftlistRepository().getItemDescriptor(mgr.getGiftlistTools().getGiftlistItemDescriptor());
      RepositoryPropertyDescriptor pd =
       (RepositoryPropertyDescriptor)itemDesc.getPropertyDescriptor(mgr.getGiftlistTools().getEventTypeProperty());
      //Class propertyType = pd.getPropertyType();
      /* if type = enumerated, get list of values from property descriptor */
      if ( pd.getTypeName().equalsIgnoreCase("enumerated")){
        PropertyEditor pe = pd.createPropertyEditor();
        if (pe != null) {
          return Arrays.asList(pe.getTags());
        }
      }
    }
    catch (RepositoryException exc) {
      if (isLoggingError())
        logError(exc);
    }
    return null;
  }
  
  //-------------------------------------
  // property:siteId
  String mSiteId;
  
  /**
   * Sets property siteId.
   * @param pSiteId  The property to store the site Id for the giftlist.
   * @beaninfo description:  The property to store the site Id for the giftlist.
   */
  public void setSiteId(String pSiteId){
    mSiteId = pSiteId;
  }

  /**
   * Returns property siteId.
   * @return The value of the property siteId.
   */
   public String getSiteId(){
     return mSiteId;
   }

  //-------------------------------------
  // property: shippingAddressId
  String mShippingAddressId;

  /**
   * Sets property shippingAddressId.
   * @param pShippingAddressId  The property to store the selected shipping Address Id for the giftlist.
   * @beaninfo description:  The property to store the selected shipping Address Id for the giftlist.
   */
  public void setShippingAddressId(String pShippingAddressId){
    mShippingAddressId = pShippingAddressId;
  }

  /**
   * Returns property shippingAddressId.
   * @return The value of the property shippingAddressId.
   */
   public String getShippingAddressId(){
     return mShippingAddressId;
   }

  //-------------------------------------
  // property:  isNewAddress
  boolean mIsNewAddress;

  /**
   * Sets property isNewAddress
   * @param pIsNewAddress  The property to store the property IsNewAddress.
   * @beaninfo description:  The property to store the property IsNewAddress.
   */
   public void setIsNewAddress(boolean pIsNewAddress){
     mIsNewAddress = pIsNewAddress;
   }

  /**
   * Returns property IsNewAddress
   * @return The value of the property isNewAddress.
   */

  public boolean getIsNewAddress(){
    return mIsNewAddress;
  }

  //-------------------------------------
  // property: newAddressName
  String mNewAddressName;

  /**
   * Sets property NewAddressName.
   * @param pNewAddressName  The property to store the nickname for the new shipping address.
   * @beaninfo description:  The property to store the nickname for the new shipping address.
   */
  public void setNewAddressName(String pNewAddressName){
    mNewAddressName = pNewAddressName;
  }

  /**
   * Returns property NewAddressName.
   * @return The value of the property newAddressName.
   */
   public String getNewAddressName(){
     return mNewAddressName;
   }

  //-------------------------------------
  // property: newAddress
  Address mNewAddress = new Address();

  /**
   * Sets property newAddress.
   * @param pNewAddress  The property to store the new address.
   * @beaninfo description:  The property to store the new address.
   */
  public void setNewAddress(Address pNewAddress){
    mNewAddress = pNewAddress;
  }

  /**
   * Returns property newAddress.
   * @return The value of the property newAddress.
   */
   public Address getNewAddress(){
     return mNewAddress;
   }

  //-------------------------------------
  // property:  catalogRefIds
  String [] mCatalogRefIds;

  /**
   * Sets property catalogRefIds.
   * @param pCatalogRefIds  The property to store the selected skus to add to the current giftlist.
   * @beaninfo description:  The property to store the selected skus to add to the current giftlist.
   **/
  public void setCatalogRefIds(String [] pCatalogRefIds) {
    mCatalogRefIds = pCatalogRefIds;
  }

  /**
   * Returns property catalogRefIds.
   * @return The value of the property catalogRefIds.
   **/
  public String [] getCatalogRefIds() {
    return mCatalogRefIds;
  }

  //-------------------------------------
  // property: RemoveGiftitemIds
  String [] mRemoveGiftitemIds;

  /**
   * Sets property RemoveGiftitemIds.
   * @param pRemoveGiftitemIds  The property to store the selected skus to remove from the current giftlist.
   * @beaninfo description:  The property to store the selected skus to remove from the current giftlist.
   **/
  public void setRemoveGiftitemIds(String [] pRemoveGiftitemIds) {
    mRemoveGiftitemIds = pRemoveGiftitemIds;
  }

  /**
   * Returns property RemoveGiftitemIds.
   * @return The value of the property RemoveGiftitemIds.
   **/
  public String [] getRemoveGiftitemIds() {
    return mRemoveGiftitemIds;
  }

  //-------------------------------------
  // property:  itemIds
  String [] mItemIds;

  /**
   * Sets property itemIds.
   * @param pItemIds  The property to store the selected commerce item ids to move from shopping cart to giftlist.
   * @beaninfo description:  The property to store the selected commerce item ids to move from shopping cart to giftlist.
   **/
  public void setItemIds(String [] pItemIds) {
    mItemIds = pItemIds;
  }

  /**
   * Returns property itemIds.
   * @return The value of the property itemIds.
   **/
  public String [] getItemIds() {
    return mItemIds;
  }

  //-------------------------------------
  // property:  productId
  String mProductId;

  /**
   * Sets property productId.
   * @param pProductId  The property to store the selected productId to add to the giftlist.
   * @beaninfo description:  The property to store the selected productId to add to the giftlist.
   **/
  public void setProductId(String pProductId) {
    mProductId = pProductId;
  }

  /**
   * Returns property productId.
   * @return The value of the property productId.
   **/
  public String getProductId() {
    return mProductId;
  }

  //-------------------------------------
  // property: Quantity
  long mQuantity = 0;

  /**
   * Sets property Quantity
   * @param pQuantity  The property to store the number of items to add to the giftlist.
   * @beaninfo description:  The property to store the number of items to add to the giftlist.
   **/
  public void setQuantity(long pQuantity) {
    mQuantity = pQuantity;
  }

  /**
   * Returns property Quantity
   * @return The value of the property quantity.
   **/
  public long getQuantity() {
    return mQuantity;
  }

  //-------------------------------------
  // property: CreateGiftlistSuccessURL
  String mCreateGiftlistSuccessURL;

  /**
   * Sets property CreateGiftlistSuccessURL
   * @param pCreateGiftlistSuccessURL  The property to store the success URL for CreateGiftlist.
   * @beaninfo description:  The property to store the success URL for CreateGiftlist.
   **/
  public void setCreateGiftlistSuccessURL(String pCreateGiftlistSuccessURL) {
    mCreateGiftlistSuccessURL = pCreateGiftlistSuccessURL;
  }

  /**
   * Returns property CreateGiftlistSuccessURL
   * @return The value of the property createGiftlistSuccessURL.
   **/
  public String getCreateGiftlistSuccessURL() {
    return mCreateGiftlistSuccessURL;
  }

  //-------------------------------------
  // property: CreateGiftlistErrorURL
  String mCreateGiftlistErrorURL;

  /**
   * Sets property CreateGiftlistErrorURL
   * @param pCreateGiftlistErrorURL  The property to store the Error URL for CreateGiftlist.
   * @beaninfo description:  The property to store the Error URL for CreateGiftlist.
   **/
  public void setCreateGiftlistErrorURL(String pCreateGiftlistErrorURL) {
    mCreateGiftlistErrorURL = pCreateGiftlistErrorURL;
  }

  /**
   * Returns property CreateGiftlistErrorURL
   * @return The value of the property createGiftlistErrorURL.
   **/
  public String getCreateGiftlistErrorURL() {
    return mCreateGiftlistErrorURL;
  }

  //-------------------------------------
  // property: SaveGiftlistSuccessURL
  String mSaveGiftlistSuccessURL;

  /**
   * Sets property SaveGiftlistSuccessURL
   * @param pSaveGiftlistSuccessURL  The property to store the success URL for SaveGiftlist.
   * @beaninfo description:  The property to store the success URL for SaveGiftlist.
   **/
  public void setSaveGiftlistSuccessURL(String pSaveGiftlistSuccessURL) {
    mSaveGiftlistSuccessURL = pSaveGiftlistSuccessURL;
  }

  /**
   * Returns property SaveGiftlistSuccessURL
   * @return The value of the property saveGiftlistSuccessURL.
   **/
  public String getSaveGiftlistSuccessURL() {
    return mSaveGiftlistSuccessURL;
  }

  //-------------------------------------
  // property: SaveGiftlistErrorURL
  String mSaveGiftlistErrorURL;

  /**
   * Sets property SaveGiftlistErrorURL
   * @param pSaveGiftlistErrorURL  The property to store the Error URL for SaveGiftlist.
   * @beaninfo description:  The property to store the error URL for SaveGiftlist.
   **/
  public void setSaveGiftlistErrorURL(String pSaveGiftlistErrorURL) {
    mSaveGiftlistErrorURL = pSaveGiftlistErrorURL;
  }

  /**
   * Returns property SaveGiftlistErrorURL
   * @return The value of the property SaveGiftlistErrorURL.
   **/
  public String getSaveGiftlistErrorURL() {
    return mSaveGiftlistErrorURL;
  }

  //-------------------------------------
  // property: UpdateGiftlistSuccessURL
  String mUpdateGiftlistSuccessURL;

  /**
   * Sets property UpdateGiftlistSuccessURL
   * @param pUpdateGiftlistSuccessURL  The property to store the Success URL for UpdateGiftlist.
   * @beaninfo description:  The property to store the success URL for UpdateGiftlist.
   **/
  public void setUpdateGiftlistSuccessURL(String pUpdateGiftlistSuccessURL) {
    mUpdateGiftlistSuccessURL = pUpdateGiftlistSuccessURL;
  }

  /**
   * Returns property UpdateGiftlistSuccessURL
   * @return The value of the property UpdateGiftlistSuccessURL.
   **/
  public String getUpdateGiftlistSuccessURL() {
    return mUpdateGiftlistSuccessURL;
  }

  //-------------------------------------
  // property: UpdateGiftlistErrorURL
  String mUpdateGiftlistErrorURL;

  /**
   * Sets property UpdateGiftlistErrorURL
   * @param pUpdateGiftlistErrorURL  The property to store the error URL for UpdateGiftlist.
   * @beaninfo description:  The property to store the error URL for UpdateGiftlist.
   **/
  public void setUpdateGiftlistErrorURL(String pUpdateGiftlistErrorURL) {
    mUpdateGiftlistErrorURL = pUpdateGiftlistErrorURL;
  }

  /**
   * Returns property UpdateGiftlistErrorURL
   * @return The value of the property UpdateGiftlistErrorURL.
   **/
  public String getUpdateGiftlistErrorURL() {
    return mUpdateGiftlistErrorURL;
  }

  //-------------------------------------
  // property: UpdateGiftlistItemsSuccessURL
  String mUpdateGiftlistItemsSuccessURL;

  /**
   * Sets property UpdateGiftlistItemsSuccessURL
   * @param pUpdateGiftlistItemsSuccessURL  The property to store the Success URL for UpdateGiftlist.
   * @beaninfo description:  The property to store the success URL for UpdateGiftlist.
   **/
  public void setUpdateGiftlistItemsSuccessURL(String pUpdateGiftlistItemsSuccessURL) {
    mUpdateGiftlistItemsSuccessURL = pUpdateGiftlistItemsSuccessURL;
  }

  /**
   * Returns property UpdateGiftlistItemsSuccessURL
   * @return The value of the property UpdateGiftlistItemsSuccessURL.
   **/
  public String getUpdateGiftlistItemsSuccessURL() {
    return mUpdateGiftlistItemsSuccessURL;
  }

  //-------------------------------------
  // property: UpdateGiftlistItemsErrorURL
  String mUpdateGiftlistItemsErrorURL;

  /**
   * Sets property UpdateGiftlistItemsErrorURL
   * @param pUpdateGiftlistItemsErrorURL  The property to store the error URL for UpdateGiftlist.
   * @beaninfo description:  The property to store the error URL for UpdateGiftlist.
   **/
  public void setUpdateGiftlistItemsErrorURL(String pUpdateGiftlistItemsErrorURL) {
    mUpdateGiftlistItemsErrorURL = pUpdateGiftlistItemsErrorURL;
  }

  /**
   * Returns property UpdateGiftlistItemsErrorURL
   * @return The value of the property UpdateGiftlistItemsErrorURL.
   **/
  public String getUpdateGiftlistItemsErrorURL() {
    return mUpdateGiftlistItemsErrorURL;
  }

  //-------------------------------------
  // property: DeleteGiftlistSuccessURL
  String mDeleteGiftlistSuccessURL;

  /**
   * Sets property DeleteGiftlistSuccessURL
   * @param pDeleteGiftlistSuccessURL  The property to store the success URL for DeleteGiftlist.
   * @beaninfo description:  The property to store the success URL for DeleteGiftlist.
   **/
  public void setDeleteGiftlistSuccessURL(String pDeleteGiftlistSuccessURL) {
    mDeleteGiftlistSuccessURL = pDeleteGiftlistSuccessURL;
  }

  /**
   * Returns property DeleteGiftlistSuccessURL
   * @return The value of the property DeleteGiftlistSuccessURL
   **/
  public String getDeleteGiftlistSuccessURL() {
    return mDeleteGiftlistSuccessURL;
  }

  //-------------------------------------
  // property: DeleteGiftlistErrorURL
  String mDeleteGiftlistErrorURL;

  /**
   * Sets property DeleteGiftlistErrorURL
   * @param pDeleteGiftlistErrorURL  The property to store the error URL for DeleteGiftlist.
   * @beaninfo description:  The property to store the error URL for DeleteGiftlist.
   **/
  public void setDeleteGiftlistErrorURL(String pDeleteGiftlistErrorURL) {
    mDeleteGiftlistErrorURL = pDeleteGiftlistErrorURL;
  }

  /**
   * Returns property DeleteGiftlistErrorURL
   * @return The value of the property DeleteGiftlistErrorURL
   **/
  public String getDeleteGiftlistErrorURL() {
    return mDeleteGiftlistErrorURL;
  }

  //-------------------------------------
  // property: AddItemToGiftlistSuccessURL
  String mAddItemToGiftlistSuccessURL;

  /**
   * Sets property AddItemToGiftlistSuccessURL
   * @param pAddItemToGiftlistSuccessURL  The property to store the success URL for addItemToGiftlist.
   * @beaninfo description:  The property to store the success URL for addItemToGiftlist.
   **/
  public void setAddItemToGiftlistSuccessURL(String pAddItemToGiftlistSuccessURL) {
    mAddItemToGiftlistSuccessURL = pAddItemToGiftlistSuccessURL;
  }

  /**
   * Returns property AddItemToGiftlistSuccessURL
   * @return The value of the property AddItemToGiftlistSuccessURL
   **/
  public String getAddItemToGiftlistSuccessURL() {
    return mAddItemToGiftlistSuccessURL;
  }

  //-------------------------------------
  // property: AddItemToGiftlistErrorURL
  String mAddItemToGiftlistErrorURL;

  /**
   * Sets property AddItemToGiftlistErrorURL
   * @param pAddItemToGiftlistErrorURL  The property to store the error URL for addItemToGiftlist.
   * @beaninfo description:  The property to store the error URL for addItemToGiftlist.
   **/
  public void setAddItemToGiftlistErrorURL(String pAddItemToGiftlistErrorURL) {
    mAddItemToGiftlistErrorURL = pAddItemToGiftlistErrorURL;
  }

  /**
   * Returns property AddItemToGiftlistErrorURL
   * @return The value of the property AddItemToGiftlistErrorURL
   **/
  public String getAddItemToGiftlistErrorURL() {
    return mAddItemToGiftlistErrorURL;
  }

  //-------------------------------------
  // property: MoveItemsFromCartSuccessURL
  String mMoveItemsFromCartSuccessURL;

  /**
   * Sets property MoveItemsFromCartSuccessURL
   * @param pMoveItemsFromCartSuccessURL  The property to store the success URL for MoveItemsFromCart.
   * @beaninfo description:  The property to store the success URL for MoveItemsFromCart.
   **/
  public void setMoveItemsFromCartSuccessURL(String pMoveItemsFromCartSuccessURL) {
    mMoveItemsFromCartSuccessURL = pMoveItemsFromCartSuccessURL;
  }

  /**
   * Returns property MoveItemsFromCartSuccessURL
   * @return The value of the property MoveItemsFromCartSuccessURL
   **/
  public String getMoveItemsFromCartSuccessURL() {
    return mMoveItemsFromCartSuccessURL;
  }

  //-------------------------------------
  // property: MoveItemsFromCartErrorURL
  String mMoveItemsFromCartErrorURL;

  /**
   * Sets property MoveItemsFromCartErrorURL
   * @param pMoveItemsFromCartErrorURL  The property to store the error URL for MoveItemsFromCart.
   * @beaninfo description:  The property to store the error URL for MoveItemsFromCart.
   **/
  public void setMoveItemsFromCartErrorURL(String pMoveItemsFromCartErrorURL) {
    mMoveItemsFromCartErrorURL = pMoveItemsFromCartErrorURL;
  }

  /**
   * Returns property MoveItemsFromCartErrorURL
   * @return The value of the property MoveItemsFromCartErrorURL
   **/
  public String getMoveItemsFromCartErrorURL() {
    return mMoveItemsFromCartErrorURL;
  }

  //-------------------------------------
  // property:  generalSuccessURL
  String mGeneralSuccessURL;

  /**
   * Sets property generalSuccessURL.
   * @param pGeneralSuccessURL  The property to store the general success URL.
   * @beaninfo description:  The property to store the general success URL.
   **/
  public void setGeneralSuccessURL(String pGeneralSuccessURL) {
    mGeneralSuccessURL = pGeneralSuccessURL;
  }

  /**
   * Returns property generalSuccessURL.
   * @return The value of the property generalSuccessURL.
   **/
  public String getGeneralSuccessURL() {
    return mGeneralSuccessURL;
  }


  //-------------------------------------
  // property:  generalFailureURL
  String mGeneralFailureURL;

  /**
   * Sets property generalFailureURL
   * @param pGeneralFailureURL  The property to store the general failure URL.
   * @beaninfo description:  The property to store the general failure URL.
   **/
  public void setGeneralFailureURL(String pGeneralFailureURL) {
    mGeneralFailureURL = pGeneralFailureURL;
  }

  /**
   * Returns property generalFailureURL.
   * @return The value of the property generalFailureURL.
   **/
  public String getGeneralFailureURL() {
    return mGeneralFailureURL;
  }

  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instanceof GiftlistFormHandler
   */
  public GiftlistFormHandler() {
  }

  // -------------------------------------
  // General Item Management
  // -------------------------------------

  //------------------------------------------
  // method:  handleCreateGiftlist
  //------------------------------------------
  /**
   * Called when the customer selects create to create a new giftlist.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */
  public boolean handleCreateGiftlist (DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException, CommerceException
  {
    preCreateGiftlist(pRequest, pResponse);

    createGiftlist();

    postCreateGiftlist(pRequest, pResponse);

    return checkFormRedirect (getCreateGiftlistSuccessURL(), getCreateGiftlistErrorURL(), pRequest, pResponse);

  }

  /**
   * Creates a new giftlist.
   */
  protected void createGiftlist() {
    mEventDate = Calendar.getInstance();
    mEventType = "";
    mComments = "";
    mDescription = "";
    mInstructions = "";
    mShippingAddressId = "";
  }

  /**
   * Operation called just before giftlist creation is started
   * @param pRequest the dynamo request object
   * @param pResponse the dynamo response object
   * @exception ServletException if something went wrong
   * @exception IOException if something went wrong
   */
  public void preCreateGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
  }

  /**
   * Operation called just giftlist creation is finished
   * @param pRequest the dynamo request object
   * @param pResponse the dynamo response object
   * @exception ServletException if something went wrong
   * @exception IOException if something went wrong
   */
  public void postCreateGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
  }

  //------------------------------------------
  // method:  handleSaveGiftlist
  //------------------------------------------
  /**
   * Called when the customer selects save giftlist. 
   * 
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */
    public boolean handleSaveGiftlist (DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException, CommerceException
  {
      
   String pProfileId = (String) getProfile().getPropertyValue("id");

   try{

     //If any form errors found, redirect to error URL:
     if (! checkFormRedirect(null, getSaveGiftlistErrorURL(), pRequest, pResponse))
       return false;
     
     preSaveGiftlist(pRequest, pResponse);

     //If any form errors found, redirect to error URL:
     if (! checkFormRedirect(null, getSaveGiftlistErrorURL(), pRequest, pResponse))
       return false;
     
     if ( mIsNewAddress )
       mShippingAddressId = createNewShippingAddress();

     String giftlistId = saveGiftlist(pProfileId);

     //If any form errors found, redirect to error URL:
     if (! checkFormRedirect(null, getSaveGiftlistErrorURL(), pRequest, pResponse))
       return false;
     
     postSaveGiftlist(pRequest, pResponse);
   }
   catch (InvalidDateException ide){
     processException(ide, MSG_INVALID_EVENT_DATE, pRequest, pResponse);
   }
   catch (CommerceException ce){
     processException(ce, MSG_ERROR_SAVING_GIFTLIST, pRequest, pResponse);
   }
   return checkFormRedirect (getSaveGiftlistSuccessURL(), getSaveGiftlistErrorURL(), pRequest, pResponse);
  }

  //------------------------------------------
  // method:  saveGiftlist
  //------------------------------------------
  
  /**
   * Calls the manager class to create a giftlist in the repository and adds it to the
   * current profile. Returns the newly created gift list id.
   * 
   * @param pProfileId the profile Id
   * @return the Id of the gift list
   * @exception InvalidDateException thrown when an invalid date has been entered for a gift list
   * @exception CommerceException if there was an error with Commerce
   */
  public String saveGiftlist(String pProfileId) throws CommerceException, InvalidDateException {
    return getGiftlistManager().createGiftlist(pProfileId, getIsPublished().booleanValue(), getEventName(), getEventDate(), getEventType(), getDescription(), getComments(), getShippingAddressId(), getInstructions(), getSiteId());
  }

  /**
   * Operation called just before giftlist is saved
   * @param pRequest the dynamo request object
   * @param pResponse the dynamo response object
   * @exception ServletException if something went wrong
   * @exception IOException if something went wrong
   */
  public void preSaveGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
  }

  /**
   * Operation called just after a giftlist is saved
   * @param pRequest the dynamo request object
   * @param pResponse the dynamo response object
   * @exception ServletException if something went wrong
   * @exception IOException if something went wrong
   */
  public void postSaveGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
  }

  //------------------------------------------
  // method:  handleUpdateGiftlist
  //------------------------------------------
  /**
   * Called when the customer selects update giftlist.  This will call the manager class to update the properties of the current giftlist in the repository.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */
    public boolean handleUpdateGiftlist (DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException, CommerceException
  {
   try{
     //If any form errors found, redirect to error URL:
     if (! checkFormRedirect(null, getUpdateGiftlistErrorURL(), pRequest, pResponse))
       return false;
     // pre process update
     preUpdateGiftlist(pRequest, pResponse);

     //If any form errors found, redirect to error URL:
     if (! checkFormRedirect(null, getSaveGiftlistErrorURL(), pRequest, pResponse))
       return false;
     
     // if new address, create and add to address book.
     if ( mIsNewAddress )
       mShippingAddressId = createNewShippingAddress();
     updateGiftlist(pRequest, pResponse);

     //If any form errors found, redirect to error URL:
     if (! checkFormRedirect(null, getSaveGiftlistErrorURL(), pRequest, pResponse))
       return false;
     
     // post process update
     postUpdateGiftlist(pRequest, pResponse);
   }

   catch (InvalidDateException ide){
     processException(ide, MSG_INVALID_EVENT_DATE, pRequest, pResponse);
   }
   catch (CommerceException ce){
     processException(ce, MSG_ERROR_SAVING_GIFTLIST, pRequest, pResponse);
   }

   return checkFormRedirect (getUpdateGiftlistSuccessURL(), getUpdateGiftlistErrorURL(), pRequest, pResponse);
  }

  /**
   * Operation called just before giftlist is updated
   * @param pRequest the dynamo request object
   * @param pResponse the dynamo response object
   * @exception ServletException if something went wrong
   * @exception IOException if something went wrong
   */
  public void preUpdateGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
  }
  
  /**
   * Applies the updates to the gift list
   * @param pRequest
   * @param pResponse
   * @throws InvalidDateException
   * @throws CommerceException
   */
  protected void updateGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
  throws InvalidDateException, CommerceException
  {
    String pProfileId = (String) getProfile().getPropertyValue("id");
    // call manager class to update list.
    getGiftlistManager().updateGiftlist(pProfileId, getGiftlistId(), getIsPublished().booleanValue(), getEventName(), getEventDate(), getEventType(), getDescription(), getComments(), getShippingAddressId(), getInstructions(),getSiteId());
    
  }

  /**
   * Operation called just after a giftlist is updated
   * @param pRequest the dynamo request object
   * @param pResponse the dynamo response object
   * @exception ServletException if something went wrong
   * @exception IOException if something went wrong
   */
  public void postUpdateGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
  }

  //------------------------------------------
  // method:  handleDeleteGiftlist
  //------------------------------------------
  /**
   * Called when the customer pushes delete giftlist.  This will call the manager class to delete the giftlist from the profile and repository.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */
    public boolean handleDeleteGiftlist (DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException, CommerceException
  {
    // call manager class to remove giftlistId from profile
    String id = (String) getProfile().getPropertyValue("id");
    try{
      //If any form errors found, redirect to error URL:
      if (! checkFormRedirect(null, getDeleteGiftlistErrorURL(), pRequest, pResponse))
        return false;
      preDeleteGiftlist(pRequest, pResponse);

      //If any form errors found, redirect to error URL:
      if (! checkFormRedirect(null, getDeleteGiftlistErrorURL(), pRequest, pResponse))
        return false;
      getGiftlistManager().removeGiftlist(id, getGiftlistId());

      //If any form errors found, redirect to error URL:
      if (! checkFormRedirect(null, getDeleteGiftlistErrorURL(), pRequest, pResponse))
        return false;
      postDeleteGiftlist(pRequest, pResponse);
    }
    catch (CommerceException ce){
      processException(ce, MSG_ERROR_DELETING_GIFTLIST, pRequest, pResponse);
    }
    catch (RepositoryException re){
      processException(re, MSG_ERROR_DELETING_GIFTLIST, pRequest, pResponse);
    }
    return checkFormRedirect (getDeleteGiftlistSuccessURL(), getDeleteGiftlistErrorURL(), pRequest, pResponse);
  }

  /**
   * Operation called just before giftlist is deleted
   * @param pRequest the dynamo request object
   * @param pResponse the dynamo response object
   * @exception ServletException if something went wrong
   * @exception IOException if something went wrong
   */
  public void preDeleteGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
  }

  /**
   * Operation called just after a giftlist is deleted
   * @param pRequest the dynamo request object

   * @param pResponse the dynamo response object
   * @exception ServletException if something went wrong
   * @exception IOException if something went wrong
   */
  public void postDeleteGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
  }

  //------------------------------------------
  // method:  handleAddItemToGiftlist
  //------------------------------------------
  /**
   * handleAddItemToGiftlist is called when the user hits the submit
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
    throws ServletException, IOException, CommerceException {

    try{
      //If any form errors found, redirect to error URL:
      if (! checkFormRedirect(null, getAddItemToGiftlistErrorURL(), pRequest, pResponse))
        return false;
      preAddItemToGiftlist(pRequest, pResponse);
      
      if (validateGiftlistId(pRequest, pResponse)) {

        if (! checkFormRedirect(null, getAddItemToGiftlistErrorURL(), pRequest, pResponse))
          return false;
        addItemToGiftlist(pRequest, pResponse);

        if (! checkFormRedirect(null, getAddItemToGiftlistErrorURL(), pRequest, pResponse))
          return false;
        
        postAddItemToGiftlist(pRequest, pResponse);
      }
    }

    catch (InvalidGiftQuantityException exc) {
      processException(exc, MSG_QUANTITY_LESSTHAN_OR_EQUALTO_ZERO, pRequest, pResponse);
    }
    catch (CommerceException oce) {
      processException(oce, MSG_ERROR_ADDING_TO_GIFTLIST, pRequest, pResponse);
    }
    return checkFormRedirect (getAddItemToGiftlistSuccessURL(), getAddItemToGiftlistErrorURL(), pRequest, pResponse);

}
  /**
   * Operation called just before an item is added to a giftlist
   * @param pRequest the dynamo request object
   * @param pResponse the dynamo response object
   * @exception ServletException if something went wrong
   * @exception IOException if something went wrong
   */
  public void preAddItemToGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
  }

  /**
   * Operation called just after an item has been added to a giftlist
   * @param pRequest the dynamo request object
   * @param pResponse the dynamo response object
   * @exception ServletException if something went wrong
   * @exception IOException if something went wrong
   */
  public void postAddItemToGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
  }

  //-------------------------------------
  // method: addItemToGiftlist
  //-------------------------------------
  /**
   * Adds the given item to the selected giftlist
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */

  protected void addItemToGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, 
      IOException, CommerceException 
  {
  GiftlistManager mgr = getGiftlistManager();
  // get all skus selected
  for (String skuId: getCatalogRefIds()) 
  {
    long quantity = getQuantity();
    if (quantity == 0)
    {
      quantity = getQuantity(skuId, pRequest, pResponse);
    }
    mgr.addCatalogItemToGiftlist(skuId, getProductId(), getGiftlistId(), getSiteId(), quantity);
  }
}

  //------------------------------------------
  // method:  handleMoveItemsFromCart
  //------------------------------------------
  /**
   * handleMoveItemsFromCart is called when the user hits the submit
   * button on the move items from cart page.  This handler will call
   * moveItemsFromCart.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */
  public boolean handleMoveItemsFromCart(DynamoHttpServletRequest pRequest,
                                         DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException {

    try{
      //If any form errors found, redirect to error URL:
      if (! checkFormRedirect(null, getMoveItemsFromCartErrorURL(), pRequest, pResponse))
        return false;
      preMoveItemsFromCart(pRequest, pResponse);

      if (validateGiftlistId(pRequest, pResponse)) {
        //If any form errors found, redirect to error URL:
        if (! checkFormRedirect(null, getMoveItemsFromCartErrorURL(), pRequest, pResponse))
          return false;
        moveItemsFromCart(pRequest, pResponse);

        //If any form errors found, redirect to error URL:
        if (! checkFormRedirect(null, getMoveItemsFromCartErrorURL(), pRequest, pResponse))
          return false;
        postMoveItemsFromCart(pRequest, pResponse);
      }
    }

    catch (InvalidGiftQuantityException exc) {
      processException(exc, MSG_QUANTITY_LESSTHAN_OR_EQUALTO_ZERO, pRequest, pResponse);
    }
    catch (CommerceException oce) {
      processException(oce, MSG_ERROR_ADDING_TO_GIFTLIST, pRequest, pResponse);
    }
    return checkFormRedirect (getMoveItemsFromCartSuccessURL(), getMoveItemsFromCartErrorURL(), pRequest, pResponse);

}

  /**
   * Operation called just before an item is is moved from shopping cart
   * @param pRequest the dynamo request object
   * @param pResponse the dynamo response object
   * @exception ServletException if something went wrong
   * @exception IOException if something went wrong
   */
  public void preMoveItemsFromCart(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
  }

  /**
   * Operation called just after an item has been moved from shopping cart
   * @param pRequest the dynamo request object
   * @param pResponse the dynamo response object
   * @exception ServletException if something went wrong
   * @exception IOException if something went wrong
   */
  public void postMoveItemsFromCart(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
  }

  //-------------------------------------
  // method: moveItemsFromCart
  //-------------------------------------
  /**
   * this method moves selected items in itemIds from shopping cart to
   * giftlist.  if a quantity is specified, it will move the number specified
   * and decrease the order by that amount.  By default, it will move
   * the total number in cart to giftlist.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */

  protected void moveItemsFromCart(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException, CommerceException {
  GiftlistManager mgr = getGiftlistManager();
  //String id = (String) getProfile().getPropertyValue("id");
  String displayName = null;
  String description = null;
  String siteId = null;

  try {
    for (int c=0; c<getItemIds().length; c++) {
      String commerceItemId = getItemIds()[c];
      CommerceItem item = getOrder().getCommerceItem(commerceItemId);
      String skuId = item.getCatalogRefId();
      if (skuId == null)
        return;
      
      RepositoryItem sku = getCatalogTools().findSKU(skuId, item.getCatalogKey());
      long quantity = getQuantity(commerceItemId, pRequest, pResponse);
      String productId = item.getAuxiliaryData().getProductId();
      if (productId == null)
        return;
      
      siteId = item.getAuxiliaryData().getSiteId();
      RepositoryItem product = getCatalogTools().findProduct(productId, item.getCatalogKey());
      if (product != null){
        productId = product.getRepositoryId();
        displayName = (String) product.getPropertyValue(mgr.getGiftlistTools().getDisplayNameProperty());
        description = (String) product.getPropertyValue(mgr.getGiftlistTools().getDescriptionProperty());        
      }
      // if item is in giftlist, increment quantity otherwise add
      String giftId = mgr.getGiftlistItemId(getGiftlistId(), skuId, productId, siteId);
      if (quantity == QUANTITY_NOT_VALID) {
        addFormException(MSG_INVALID_GIFTLIST_QUANTITY, pRequest, pResponse);
        return;
      }

      if (giftId != null)
        mgr.increaseGiftlistItemQuantityDesired(getGiftlistId(), giftId, quantity);
      else{
        String itemId = null;
        if (siteId != null){
          itemId = mgr.createGiftlistItem(skuId, sku, productId, product, quantity, displayName, description, siteId);
        }else{
          itemId = mgr.createGiftlistItem(skuId, sku, productId, product, quantity, displayName, description);
        }
        mgr.addItemToGiftlist(getGiftlistId(), itemId);
      }
      // update order quantity
      updateOrder(item, quantity, pRequest, pResponse);
    }
  }
  catch (RepositoryException exc) {
    throw new CommerceException(exc);
  }
  }

  /**
   * Operation called just before items in giftlist are updated
   * @param pRequest the dynamo request object
   * @param pResponse the dynamo response object
   * @exception ServletException if something went wrong
   * @exception IOException if something went wrong
   */
  public void preUpdateGiftlistItems(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
  }

  /**
   * Operation called just after items in giftlist are updated
   * @param pRequest the dynamo request object
   * @param pResponse the dynamo response object
   * @exception ServletException if something went wrong
   * @exception IOException if something went wrong
   */
  public void postUpdateGiftlistItems(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
  }

  //-------------------------------------
  // method: handleUpdateGiftlistItems
  //-------------------------------------
  /**
   * This handle method updates items in the current giftlist either increasing
   * or decreasing quantity desired or if checked, removing the item from the
   * giftlist
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleUpdateGiftlistItems(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {

    try{
      //If any form errors found, redirect to error URL:
      if (! checkFormRedirect(null, getUpdateGiftlistItemsErrorURL(), pRequest, pResponse))
        return false;
      preUpdateGiftlistItems(pRequest, pResponse);

      if (validateGiftlistId(pRequest, pResponse)) {
        if (! checkFormRedirect(null, getUpdateGiftlistItemsErrorURL(), pRequest, pResponse))
          return false;
        updateGiftlistItems(pRequest, pResponse);

        if(! checkFormRedirect(null, getUpdateGiftlistItemsErrorURL(), pRequest, pResponse))
          return false;
        postUpdateGiftlistItems(pRequest, pResponse);
      }
    }

    catch (InvalidGiftQuantityException exc) {
      processException(exc, MSG_QUANTITY_LESSTHAN_OR_EQUALTO_ZERO, pRequest, pResponse);
    }
    catch (CommerceException oce) {
      processException(oce, MSG_ERROR_ADDING_TO_GIFTLIST, pRequest, pResponse);
    }

    //If NO form errors are found, redirect to the success URL.
    //If form errors are found, redirect to the error URL.
    return checkFormRedirect (getUpdateGiftlistItemsSuccessURL(), getUpdateGiftlistItemsErrorURL(), pRequest, pResponse);

  }

  //-------------------------------------
  // method: updateGiftlistItems
  //-------------------------------------
  /**
   * This method is used to update items in a giftlist by either changing
   * its quantity or removing it altogether.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */

  protected void updateGiftlistItems(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException, CommerceException {
  GiftlistManager mgr = getGiftlistManager();
  String pGiftlistId = getGiftlistId();
  List items = mgr.getGiftlistItems(pGiftlistId);
  if (items == null){
    return;
  }
  try{
    for (int i=0; i< items.size(); i++){
      RepositoryItem item = (RepositoryItem) items.get(i);
      String id = (String) item.getPropertyValue("id");
      // get quantity
      long quantity = getQuantity(id, pRequest, pResponse);

      // see if item should be removed
      if ((haveId(id, getRemoveGiftitemIds()))) {
        quantity = 0;
      }

      // Added to fix bug 54914
      if (quantity == QUANTITY_NOT_VALID) {
        addFormException(MSG_INVALID_GIFTLIST_QUANTITY, pRequest, pResponse);
        setRollbackTransaction(true);
        return;
      }
      
      if (quantity > 0) {
        //if the user changed the QTY value:
        if (mgr.getGiftlistItemQuantityDesired(id) != quantity){
          //change the Commerce Item quantity:
          mgr.setGiftlistItemQuantityDesired(pGiftlistId, id, quantity);
        }
      }
      // quantity less than zero, so remove item
      else {
        mgr.removeItemFromGiftlist(pGiftlistId, id);
      }
    }
  }
  catch (RepositoryException ex){
    processException(ex, MSG_ERROR_UPDATING_GIFTLIST_ITEMS, pRequest, pResponse);
    setRollbackTransaction(true);
  }
}

  /**
   * Returns true if the given id is within the supplied id array
   */
  final boolean haveId(String pId, String [] pIds) {
    if ((pIds != null) && (pId != null)) {
      int length = pIds.length;
      for (int c=0; c<length; c++) {
        if (pId.equals(pIds[c])) {
          return true;
        }
      }
    }
    return false;
  }

  //------------------------------------------
  // method:  getQuantity
  //------------------------------------------
  /**
   * Retrieve the quanity that should be used for the given item
   * @param pCatalogRefId catalog reference id
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return the quantity for the item referenced by CatalogRefId
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public long getQuantity(String pCatalogRefId,
                             DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    Object value = pRequest.getParameter(pCatalogRefId);
    if (value != null) {
      try {
        return Long.parseLong(value.toString());
      }
      catch (NumberFormatException exc) {
        if (isLoggingDebug())
          logDebug("Quantity specified in giftlist is not a number: " + value.toString());
        return QUANTITY_NOT_VALID;
      }
    }
    return getQuantity();
  }

  //------------------------------------------
  // method:  validateGiftlistId
  //------------------------------------------
  /**
   * Make sure <code>giftlistId</code> is valid, i.e., such a giftlist exists.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return <code>true</code> if the ID is good, <code>false</code> if
   *         the ID is invalid or there is no such giftlist
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet i/o
   */
  protected boolean validateGiftlistId(DynamoHttpServletRequest pRequest,
                                       DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    boolean ok = true;
    RepositoryItem item = null;

    String giftlistId = getGiftlistId();
    if (isLoggingDebug()) {
      logDebug("Validating giftlist ID: " + giftlistId);
    }

    if (giftlistId != null) {
      try {
        item = getGiftlistManager().getGiftlist(giftlistId);
      }
      catch (CommerceException exc) {
        // Log the exception and then drop it, treating it as a not found condition.
        if (isLoggingError()) logError(exc);
      }
    }
    if (item == null) {
      ok = false;
      String msg = formatUserMessage(MSG_ERROR_NO_SUCH_GIFTLIST, giftlistId, pRequest, pResponse);
      addFormException(new DropletException(msg, MSG_ERROR_NO_SUCH_GIFTLIST));
    }

    return ok;
  }

  //------------------------------------------
  // method:  updateOrder
  //------------------------------------------

  /**
   * updateOrder will update the quantity of the commerceItem passed in.
   * if quantity moved to giftlist from cart equals or is greater than
   * that in cart, it will remove the item from the cart.  otherwise,
   * it will decrease the number by quantity passed in.
   * @param pItem the commerce item to update
   * @param pQuantity the number moved to giftlist
   * @exception InvalidGiftQuantityException if pQuantity <= 0
   */
  protected void updateOrder(CommerceItem pItem, long pQuantity,
                             DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse)
    throws InvalidGiftQuantityException, IOException, ServletException
  {
    // test on pQuantity added by matt landau on 3 aug 2001 to make 
    // behavior match javadoc and method signature.
    
    if (pQuantity <= 0)
      throw new InvalidGiftQuantityException();

    try{
      OrderManager om = getOrderManager();
      Order order = getOrder();
      long itemQty = pItem.getQuantity();
      long quantity = itemQty - pQuantity;
      if (quantity > 0) {
        pItem.setQuantity(quantity);
        om.updateOrder(order);
      }
      else{
        om.getCommerceItemManager().removeItemFromOrder(order, pItem.getId());
        om.updateOrder(order);
        if (isSendItemRemovedMessages())
          sendScenarioEvent(order, pItem, itemQty, getItemRemovedFromOrderEventType());
      }
    }
    catch (CommerceException ce){
      processException(ce, MSG_ERROR_ADDING_TO_GIFTLIST, pRequest, pResponse);
    }
    catch (RunProcessException rpe){
      processException(rpe, MSG_ERROR_ADDING_TO_GIFTLIST, pRequest, pResponse);
    }
  }
  
  //---------------------------------------------------------------------------

  /**
   * Send a message to the Dynamo Scenario Server.
   *
   * @param pOrder
   *    The order being modified
   * @param pItem
   *    The CommerceItem involved in the order modification
   * @param pQuantity
   *    The quantity of the CommerceItem involved
   * @param pMsgType
   *    The JMS message type of the message in question
   **/

  protected void sendScenarioEvent(Order pOrder, CommerceItem pItem, long pQuantity, String pMsgType)
    throws RunProcessException
  {
    sendScenarioEvent(pOrder, pItem,  pQuantity, pMsgType, null);
  }

  //---------------------------------------------------------------------------

  /**
   * Send a message to the Dynamo Scenario Server.
   *
   * @param pOrder
   *    The order being modified
   * @param pItem
   *    The CommerceItem involved in the order modification
   * @param pQuantity
   *    The quantity of the CommerceItem involved
   * @param pMsgType
   *    The JMS message type of the message in question
   * @param pSiteId 
   *    The site ID associated with a scenario event
   **/

  protected void sendScenarioEvent(Order pOrder, CommerceItem pItem, 
                                   long pQuantity, String pMsgType, String pSiteId)
    throws RunProcessException
  {
    PipelineResult result;
    HashMap params = new HashMap();

    params.put(PipelineConstants.ORDER, pOrder);
    params.put(PipelineConstants.COMMERCEITEM, pItem);
    params.put(PipelineConstants.EVENT, pMsgType);
    params.put(PipelineConstants.QUANTITY, Long.valueOf(pQuantity));
    if (pSiteId !=null){
      params.put(PipelineConstants.SITEID, pSiteId);
    }

    result = getPipelineManager().runProcess(PipelineConstants.SENDSCENARIOEVENT, params);
    processPipelineErrors(result);
  }

  //---------------------------------------------------------------------------

  /**
   * generate property path takes a string and creates full path to property.
   * @param pPropertyName the property name to get full path
   * @return the absolute path to the property
   */
  protected String generatePropertyPath(String pPropertyName) {
    return getAbsoluteName() + pPropertyName;
  }

  /**
   * Creates a shipping address in the address book and return the id.
   * @return shippingAddressId
   */
  protected String createNewShippingAddress() {
    CommerceProfileTools ctools = getProfileTools();
    String id = null;

    // create address
    try{
      id = ctools.createProfileRepositorySecondaryAddress(getProfile(), getNewAddressName(), getNewAddress());
    // clear fields
    }
    catch (RepositoryException re){
      if (isLoggingError())
        logError(re);
    }
    mNewAddress = new Address();
    mNewAddressName = "";
    return id;
  }

  // -------------------------------
  // Administrative methods
  // -------------------------------

  /**
   * Add a user error message to the form exceptions, and then display
   * the exception in the logs
   * @param pException the exception to process
   * @param pMsgId the message id
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public void processException(Exception pException, String pMsgId,
                                  DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    if (isLoggingError()) logError(pException);

    String msg = formatUserMessage(pMsgId, pRequest, pResponse);
    addFormException(new DropletException(msg, pException, pMsgId));
  }

  /**
   * Examine the pipeline result looking for any error messages, creates DropletExceptions
   * and adds them as form exceptions
   * @param pResult a <code>PipelineResult</code> value
   * @return true if errors were found
   **/
  
  protected boolean processPipelineErrors(PipelineResult pResult)
  {
    if (pResult == null)
      return false;
    return PipelineUtils.processPipelineErrors(pResult, this, this);
  }

  /**
   * Returns the locale associated with the request. The method first searches
   * for a request paramater named <code>locale</code>. This value can be
   * either a java.util.Locale object or a String which represents the locale.
   * Next if the <code>useRequestLocale</code> property is true, then the locale
   * of the request will be returned. Finally, if the locale cannot be determined,
   * the the <code>defaultLocale</code> property is used.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public Locale getUserLocale(DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    Object obj = pRequest.getObjectParameter(LOCALE_PARAM);
    if (obj instanceof Locale)
      return (Locale)obj;
    else if (obj instanceof String)
      return RequestLocale.getCachedLocale((String)obj);
    else if (isUseRequestLocale()) {
      RequestLocale requestLocale = pRequest.getRequestLocale();
      if (requestLocale != null)
        return requestLocale.getLocale();
    }

    return getDefaultLocale();
  }

  /**
   * Utility method to format a message with no arguments using the Locale of the user
   * @param pKey the identifier for the message to retrieve out of the ResourceBundle
   * @param pRequest the request object which can be used to extract the user's locale
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @return the formatted message
   * @see GiftlistUserMessage
   **/
  public String formatUserMessage(String pKey,
                           DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    return GiftlistUserMessage.format(pKey, getUserLocale(pRequest, pResponse));
  }

  /**
   * Utility method to format a message with one argument using the Locale of the user
   * @param pKey the identifier for the message to retrieve out of the ResourceBundle
   * @param pParam the first (and only argument) in the message
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @return the formatted message
   * @see GiftlistUserMessage
   **/
  public String formatUserMessage(String pKey, Object pParam,
                           DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    return GiftlistUserMessage.format(pKey, pParam, getUserLocale(pRequest, pResponse));
  }

  /**
   * Utility method to format a message with two arguments using our resource bundle.
   * @param pKey the identifier for the message to retrieve out of the ResourceBundle
   * @param pParam1 the first parameter in the message
   * @param pParam2 the second parameter in the message
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @return the formatted message
   * @see GiftlistUserMessage
   **/
  public String formatUserMessage(String pKey, Object pParam1, Object pParam2,
                           DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    Object[] params = { pParam1, pParam2, };
    return GiftlistUserMessage.format(pKey, params, getUserLocale(pRequest, pResponse));
  }

  /**
   * Utility method to format a message with two arguments using our resource bundle.
   * @param pKey the identifier for the message to retrieve out of the ResourceBundle
   * @param pParams a set of parameters to use in the formatting.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @return the formatted message
   * @see GiftlistUserMessage
   **/
  public String formatUserMessage(String pKey, Object [] pParams,
                           DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    return GiftlistUserMessage.format(pKey, pParams, getUserLocale(pRequest, pResponse));
  }
  
  /**
   * Add a pipeline error to the list of form exceptions.
   * 
   * @param pError the error reported by the pipeline.
   * @param pErrorKey the key of the error, used for localization.
   */
  public void handlePipelineError(Object pError, String pErrorKey) {
    addFormException(new DropletException(pError.toString(), pErrorKey));
  }

  /**
   * Adds a form exception with the given message id.  It will format the user message and add it as a
   * DropletException to the list of form exceptions.
   *
   * @param pMsgId    The message id (i.e. the resource string)
   * @param pRequest
   * @param pResponse
   * @throws ServletException
   * @throws IOException
   */
  protected void addFormException(String pMsgId,
                                  DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    String msg = formatUserMessage(pMsgId, pRequest, pResponse);
    addFormException(new DropletException(msg, pMsgId));
  }


  /**
   * Updates the value of the mEventDate property.
   * This needs to be updated whenever the setYear, setMonth, setDate or
   * setEventDate are called; This way the correct values are always returned
   * by the equivelent getter methods.
   */
  protected void updateEventDate() {
    
    //If the mYear, mMonth, or mDate are null then set them to the values of
    //the mEventDate. This way if one value is updated the result is still the
    //expected date.
    if(mYear == null) {
      mYear = Integer.valueOf(mEventDate.get(Calendar.YEAR)).toString();
    }
    if(mMonth == null) {
      mMonth = Integer.valueOf(mEventDate.get(Calendar.MONTH));
    }
    if(mDate == null) {
      mDate = Integer.valueOf(mEventDate.get(Calendar.DATE)).toString();
    }
    
    //Update the eventDate values
    int yy = (Integer.valueOf(mYear)).intValue();
    int mm = mMonth.intValue();
    int dd = (Integer.valueOf(mDate)).intValue();
    int eom = DateDoodads.lastDate(mm, yy - 1900);

    // if day of month entered after last day of month, use that value
    if (dd > eom){
      dd = eom;
        // throw new InvalidDateException(ResourceUtils.getMsgResource("InvalidDate", MY_RESOURCE_NAME, sResourceBundle));
    }
    mEventDate.set(yy, mm, dd);
  }
} // end of class





