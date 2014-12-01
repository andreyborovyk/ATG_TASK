/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import atg.commerce.CommerceException;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.messaging.SourceSinkTemplate;
import atg.commerce.order.AuxiliaryData;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.RelationshipTypes;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.SubSkuCommerceItem;
import atg.commerce.states.CommerceItemStates;
import atg.commerce.states.OrderStates;
import atg.commerce.states.PaymentGroupStates;
import atg.commerce.states.ShipItemRelationshipStates;
import atg.commerce.states.ShippingGroupStates;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.idgen.IdGenerator;
import atg.service.idgen.IdGeneratorException;

/**
 * This class contains convenience methods for various tasks that
 * occur frequently during the order fulfillment process.  Examples
 * include changing the states of objects, preparing messages.
 * OrderFulfiller and HardgoodFulfiller both use this class.
 *
 * @see OrderFulfiller
 * @see HardgoodFulfiller
 *
 * @author Tareef Kawaf
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/OrderFulfillmentTools.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $*/
public class OrderFulfillmentTools
{
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/OrderFulfillmentTools.java#2 $$Change: 651448 $";

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------
  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";
  
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  //---------------------------------------------------------------------------
  // property: FulfillerPortNameMap
  //---------------------------------------------------------------------------

  Properties mFulfillerPortNameMap;

  public void setFulfillerPortNameMap(Properties pFulfillerPortNameMap) {
    mFulfillerPortNameMap = pFulfillerPortNameMap;
  }

  /**
   * This mapping defines what ports that messages designated for
   * certain fulfillers should use.
   * @beaninfo hidden: false
   **/
  public Properties getFulfillerPortNameMap() {
    return mFulfillerPortNameMap;
  }


  //---------------------------------------------------------------------------
  // property:FulfillerShippingGroupClassMap
  //---------------------------------------------------------------------------

  private Properties mFulfillerShippingGroupClassMap;
  private HashMap mFulfillerShippingGroupClassHashMap;
  public void setFulfillerShippingGroupClassMap(Properties pFulfillerShippingGroupClassMap) {
    mFulfillerShippingGroupClassMap = pFulfillerShippingGroupClassMap;
    mFulfillerShippingGroupClassHashMap = new HashMap();
    Enumeration fulfillers = pFulfillerShippingGroupClassMap.propertyNames();
    String fulfiller = null;
    while(fulfillers.hasMoreElements()) {
      fulfiller = (String) fulfillers.nextElement();
      String classes = pFulfillerShippingGroupClassMap.getProperty(fulfiller);
      List classList = stringToList(classes);
      mFulfillerShippingGroupClassHashMap.put(fulfiller, classList);
    }
  }

  /**
   * This mapping defines which shipping group classes can be
   * fulfilled by each fulfiller.  This map is consulted before a
   * FulfillOrderFragment message is sent to ensure the fulfiller does
   * not get a shipping group that it cannot handle.
   * @beaninfo hidden: false
   **/
  public Properties getFulfillerShippingGroupClassMap() {
    return mFulfillerShippingGroupClassMap;
  }

  public HashMap getFulfillerShippingGroupClassHashMap() {
    return mFulfillerShippingGroupClassHashMap;
  }

  //---------------------------------------------------------------------------
  // property: DefaultFulfiller
  //---------------------------------------------------------------------------

  String mDefaultFulfiller = "HardgoodFulfiller";

  public void setDefaultFulfiller(String pDefaultFulfiller) {
    mDefaultFulfiller = pDefaultFulfiller;
  }

  /**
   * This is the default fulfiller.  If a repository item does not
   * specify what fulfiller should be used, this is what is used.
   * @beaninfo hidden: false
   **/
  public String getDefaultFulfiller() {
    return mDefaultFulfiller;
  }

  //---------------------------------------------------------------------------
  // property: OrderManager
  //---------------------------------------------------------------------------

  OrderManager mOrderManager;

  public void setOrderManager(OrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  /**
   * The order manager controls access to the order and all objects
   * within an order.
   * @beaninfo hidden: false
   **/
  public OrderManager getOrderManager() {
    return mOrderManager;
  }


  //---------------------------------------------------------------------------
  // property:CatalogTools
  //---------------------------------------------------------------------------

  private CatalogTools mCatalogTools;
  public void setCatalogTools(CatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  /**
   * The tools for getting items out of the catalog
   * @beaninfo hidden: false
   **/
  public CatalogTools getCatalogTools() {
    return mCatalogTools;
  }


  //---------------------------------------------------------------------------
  // property:GiftCertificateClassType
  //---------------------------------------------------------------------------

  private String mGiftCertificateClassType = "giftCertificate";
  public void setGiftCertificateClassType(String pGiftCertificateClassType) {
    mGiftCertificateClassType = pGiftCertificateClassType;
  }

  /**
   * The PaymentGroupClassType of gift certificates.  Used when crediting, after a cancellation
   * @beaninfo hidden: false
   **/
  public String getGiftCertificateClassType() {
    return mGiftCertificateClassType;
  }

  //---------------------------------------------------------------------------
  // property:MessageIdGenerator
  //---------------------------------------------------------------------------

  private IdGenerator mMessageIdGenerator;
  public void setMessageIdGenerator(IdGenerator pMessageIdGenerator) {
    mMessageIdGenerator = pMessageIdGenerator;
  }

  /**
   * The service that generates Ids for all messages.
   * @beaninfo hidden: false
   **/
  public IdGenerator getMessageIdGenerator() {
    return mMessageIdGenerator;
  }


  //---------------------------------------------------------------------------
  // property:MessageIdSpaceName
  //---------------------------------------------------------------------------

  private String mMessageIdSpaceName;
  public void setMessageIdSpaceName(String pMessageIdSpaceName) {
    mMessageIdSpaceName = pMessageIdSpaceName;
  }

  /**
   * The name of the idspace to get our message ids from
   * @beaninfo hidden: false
   **/
  public String getMessageIdSpaceName() {
    return mMessageIdSpaceName;
  }

  //-------------------------------------
  // states
  //-------------------------------------

  //---------------------------------------------------------------------------
  // property:OrderStates
  //---------------------------------------------------------------------------

  private OrderStates mOrderStates;
  public void setOrderStates(OrderStates pOrderStates) {
    mOrderStates = pOrderStates;
  }

  /**
   * The object containing all the states of an order
   * @beaninfo hidden: false
   **/
  public OrderStates getOrderStates() {
    return mOrderStates;
  }

  //---------------------------------------------------------------------------
  // property:CommerceItemStates
  //---------------------------------------------------------------------------

  private CommerceItemStates mCommerceItemStates;
  public void setCommerceItemStates(CommerceItemStates pCommerceItemStates) {
    mCommerceItemStates = pCommerceItemStates;
  }

  /**
   * The object containing all the states of an item
   * @beaninfo hidden: false
   **/
  public CommerceItemStates getCommerceItemStates() {
    return mCommerceItemStates;
  }

  //---------------------------------------------------------------------------
  // property:ShippingGroupStates
  //---------------------------------------------------------------------------

  private ShippingGroupStates mShippingGroupStates;
  public void setShippingGroupStates(ShippingGroupStates pShippingGroupStates) {
    mShippingGroupStates = pShippingGroupStates;
  }

  /**
   * The object containing all the states of an shipping group
   * @beaninfo hidden: false
   **/
  public ShippingGroupStates getShippingGroupStates() {
    return mShippingGroupStates;
  }

  //---------------------------------------------------------------------------
  // property:PaymentGroupStates
  //---------------------------------------------------------------------------

  private PaymentGroupStates mPaymentGroupStates;
  public void setPaymentGroupStates(PaymentGroupStates pPaymentGroupStates) {
    mPaymentGroupStates = pPaymentGroupStates;
  }

  /**
   * The object containing all the states of an payment group
   * @beaninfo hidden: false
   **/
  public PaymentGroupStates getPaymentGroupStates() {
    return mPaymentGroupStates;
  }

  //---------------------------------------------------------------------------
  // property:ShipItemRelationshipStates
  //---------------------------------------------------------------------------

  private ShipItemRelationshipStates mShipItemRelationshipStates;
  public void setShipItemRelationshipStates(ShipItemRelationshipStates pShipItemRelationshipStates) {
    mShipItemRelationshipStates = pShipItemRelationshipStates;
  }

  /**
   * The object containing all the states of a shipping group / item relationship
   * @beaninfo hidden: false
   **/
  public ShipItemRelationshipStates getShipItemRelationshipStates() {
    return mShipItemRelationshipStates;
  }

  //-------------------------------------
  /**
   * A convenience method which in the reference implementation will return a
   * GenericUpdate Modification object targeting order objects.
   * @param pPropertyName the property of the Order object that was modified
   * @param pOriginalValue the original value of the property that was modified
   * @param pNewValue the new value of the property that was modified
   * @param pTargetId the id of the target to be modified
   * @see GenericUpdate
   **/
  public Modification createOrderUpdateModification(String pPropertyName,
                            Serializable pOriginalValue,
                            Serializable pNewValue,
                            String pTargetId)
  {
    GenericUpdate gu= createGenericUpdateModification(pPropertyName,
                                                      pOriginalValue,
                                                      pNewValue,
                                                      pTargetId,
                                                      Modification.TARGET_ORDER,
                              true);
    return gu;
  }

  //-------------------------------------
  /**
   * A convenience method which in the reference implementation will return a
   * GenericUpdate Modification object targeting shipping groups
   * @param pPropertyName the property of the Order object that was modified
   * @param pOriginalValue the original value of the property that was modified
   * @param pNewValue the new value of the property that was modified
   * @param pTargetId the id of the target to be modified
   * @see GenericUpdate
   **/
  public Modification createShipUpdateModification(String pPropertyName,
                           Serializable pOriginalValue,
                           Serializable pNewValue,
                           String pTargetId)
  {
    // <TBD> we should look at setting the status flag to true or false.  This should be
    // examined in greater detail.
    GenericUpdate gu= createGenericUpdateModification(pPropertyName,
                                                      pOriginalValue,
                                                      pNewValue,
                                                      pTargetId,
                                                      Modification.TARGET_SHIPPING_GROUP,
                              true);
    return gu;
  }

  //-------------------------------------
  /**
   * A convenience method which in the reference implementation will return a
   * GenericUpdate Modification object targeting payment groups
   * @param pPropertyName the property of the Order object that was modified
   * @param pOriginalValue the original value of the property that was modified
   * @param pNewValue the new value of the property that was modified
   * @param pTargetId the id of the target to be modified
   * @see GenericUpdate
   **/
  public Modification createPayUpdateModification(String pPropertyName,
                           Serializable pOriginalValue,
                           Serializable pNewValue,
                           String pTargetId)
  {
    // <TBD> we should look at setting the status flag to true or false.  This should be
    // examined in greater detail.
    GenericUpdate gu= createGenericUpdateModification(pPropertyName,
                                                      pOriginalValue,
                                                      pNewValue,
                                                      pTargetId,
                                                      Modification.TARGET_PAYMENT_GROUP,
                              true);
    return gu;
  }

  //-------------------------------------
  /**
   * A convenience method which in the reference implementation will return a
   * GenericuUpdate Modification object targeting items.
   * @param pPropertyName the property of the Order object that was modified
   * @param pOriginalValue the original value of the property that was modified
   * @param pNewValue the new value of the property that was modified
   * @param pTargetId the id of the target to be modified
   * @see GenericUpdate
   **/
  public Modification createItemUpdateModification(String pPropertyName,
                           Serializable pOriginalValue,
                           Serializable pNewValue,
                           String pTargetId)
  {
    GenericUpdate gu= createGenericUpdateModification(pPropertyName,
                                                      pOriginalValue,
                                                      pNewValue,
                                                      pTargetId,
                                                      Modification.TARGET_ITEM,
                              true);
    return gu;
  }

  //-------------------------------------
  /**
   * A convenience method which in the reference implementation will return a
   * GenericuUpdate Modification object targeting items.
   * @param pPropertyName the property of the Order object that was modified
   * @param pOriginalValue the original value of the property that was modified
   * @param pNewValue the new value of the property that was modified
   * @param pTargetId the id of the target to be modified
   * @see GenericUpdate
   **/
  public Modification createRelationshipUpdateModification(String pPropertyName,
                                                           Serializable pOriginalValue,
                                                           Serializable pNewValue,
                                                           String pTargetId)
  {
    GenericUpdate gu= createGenericUpdateModification(pPropertyName,
                                                      pOriginalValue,
                                                      pNewValue,
                                                      pTargetId,
                                                      Modification.TARGET_RELATIONSHIP,
                              true);
    return gu;
  }

  /**
   * This method creates a GenericUpdate message and sets all the
   * properties accordingly.
   * @param pPropertyName The name of the property being updated.
   * @param pOriginalValue The original value of the property
   * @param pNewValue The new value of the property
   * @param pTargetId The id of the object being updated.
   * @param pTargetType The tpye of the object being updated.
   * @param pSuccess a boolean indicating whether the modification was a successful one or not
   * @return The newly created GenericUpdate object.
   * @see GenericUpdate
   **/
  public GenericUpdate createGenericUpdateModification(String pPropertyName,
                                                       Serializable pOriginalValue,
                                                       Serializable pNewValue,
                                                       String pTargetId,
                                                       int pTargetType,
                               boolean pSuccess)
  {
    GenericUpdate gu= new GenericUpdate();
    gu.setPropertyName(pPropertyName);
    gu.setOriginalValue(pOriginalValue);
    gu.setNewValue(pNewValue);
    gu.setTargetId(pTargetId);
    gu.setTargetType(pTargetType);
    if (pSuccess)
      gu.setModificationStatus(Modification.STATUS_SUCCESS);
    else
      gu.setModificationStatus(Modification.STATUS_FAILED);
    return gu;
  }

  /**
   * This method creates a GenericRemove message and sets all the
   * properties accordingly.
   *
   * @param pTargetType the type of the object being removed
   * @param pTarget the id of the object being removed
   * @param pContainerType the type of the container of the object being removed
   * @param pContainer the id of the container
   * @return The newly created GenericRemove object.
   * @see GenericRemove
   **/
  public GenericRemove createGenericRemoveModification(int pTargetType,
                                                       String pTarget,
                                                       int pContainerType,
                                                       String pContainer)
  {
    GenericRemove gr = new GenericRemove();

    gr.setTargetType(pTargetType);
    gr.setTargetId(pTarget);
    gr.setContainerType(pContainerType);
    gr.setContainerId(pContainer);

    return gr;
  }

  /**
   * This method creates a GenericAdd message and sets all the
   * properties accordingly.  The target is a value, the container is a value
   *
   * @param pTargetType the type of the object being added
   * @param pTarget the id of the object being added
   * @param pContainerType the type of the container of the object being added
   * @param pContainer the id of the container
   * @return The newly created GenericAdd object.
   * @see GenericAdd
   **/
  public GenericAdd createGenericAddValueToValueModification(int pTargetType,
                                                                Serializable pTarget,
                                                                int pContainerType,
                                                                Serializable pContainer)
  {
    GenericAdd ga = new GenericAdd();

    ga.setTargetType(pTargetType);
    ga.setTargetValue(pTarget);
    ga.setContainerType(pContainerType);
    ga.setContainerValue(pContainer);

    return ga;
  }

  /**
   * This method creates a GenericAdd message and sets all the
   * properties accordingly.  The target is a value, the container is an id
   *
   * @param pTargetType the type of the object being added
   * @param pTarget the id of the object being added
   * @param pContainerType the type of the container of the object being added
   * @param pContainer the id of the container
   * @return The newly created GenericAdd object.
   * @see GenericAdd
   **/
  public GenericAdd createGenericAddValueToIdModification(int pTargetType,
                                                             Serializable pTarget,
                                                             int pContainerType,
                                                             String pContainer)
  {
    GenericAdd ga = new GenericAdd();

    ga.setTargetType(pTargetType);
    ga.setTargetValue(pTarget);
    ga.setContainerType(pContainerType);
    ga.setContainerId(pContainer);

    return ga;
  }

  /**
   * This method creates a GenericAdd message and sets all the
   * properties accordingly.  The target is an id, the container is a value
   *
   * @param pTargetType the type of the object being added
   * @param pTarget the id of the object being added
   * @param pContainerType the type of the container of the object being added
   * @param pContainer the id of the container
   * @return The newly created GenericAdd object.
   * @see GenericAdd
   **/
  public GenericAdd createGenericAddIdToValueModification(int pTargetType,
                                                          String pTarget,
                                                          int pContainerType,
                                                          Serializable pContainer)
  {
    GenericAdd ga = new GenericAdd();

    ga.setTargetType(pTargetType);
    ga.setTargetId(pTarget);
    ga.setContainerType(pContainerType);
    ga.setContainerValue(pContainer);

    return ga;
  }

  //------------------------------------
  /**
   * Set the state of the item relationship.  Two modifications are
   * added to pModificationList.  One for the state and one for the
   * state detail.
   *
   * @param pShippingGroupItemRelationship The item to update
   * @param pNewState The state to set the item to.
   * @param pDetail The state detail message to use.  If null, a default will be used.
   * @param pModificationList The place to save the modification
   **/
  public void setItemRelationshipState(ShippingGroupCommerceItemRelationship pShippingGroupItemRelationship,
                                          int pNewState, String pDetail,
                                          List pModificationList)

  {
    ShipItemRelationshipStates sirs = getShipItemRelationshipStates();
    int oldState = pShippingGroupItemRelationship.getState();
    String oldStateDetail = pShippingGroupItemRelationship.getStateDetail();

    String msg = null;

    if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.PENDING_DELIVERY))
    {
      msg = Constants.ITEM_RELATIONSHIP_STATE_PENDING_DELIVERY;
    }
    else if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.PENDING_SUBITEM_DELIVERY))
    {
      msg = Constants.ITEM_RELATIONSHIP_STATE_PENDING_SUBITEM_DELIVERY;
    }
    else if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.DELIVERED))
    {
      msg = Constants.ITEM_RELATIONSHIP_STATE_DELIVERED;
    }
    else if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.FAILED))
    {
      msg = Constants.ITEM_RELATIONSHIP_STATE_FAILED;
    }
    else if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.ITEM_NOT_FOUND))
    {
      msg = Constants.ITEM_RELATIONSHIP_STATE_ITEM_NOT_FOUND;
    }
    else if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.OUT_OF_STOCK))
    {
      msg = Constants.ITEM_RELATIONSHIP_STATE_OUT_OF_STOCK;
    }
    else if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.DISCONTINUED))
    {
      msg = Constants.ITEM_RELATIONSHIP_STATE_DISCONTINUED;
    }
    else if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.PRE_ORDERED))
    {
      msg = Constants.ITEM_RELATIONSHIP_STATE_PREORDERED;
    }
    else if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.BACK_ORDERED))
    {
      msg = Constants.ITEM_RELATIONSHIP_STATE_BACKORDERED;
    }
    else if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.PENDING_REMOVE))
    {
      msg = Constants.ITEM_RELATIONSHIP_STATE_PENDING_REMOVE;
    }
    else if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.REMOVED))
    {
      msg = Constants.ITEM_RELATIONSHIP_STATE_REMOVED;
    }

    // Modify the state and state detail of the item
    pShippingGroupItemRelationship.setState(pNewState);
    if(pDetail == null)
      pShippingGroupItemRelationship.setStateDetail(MessageFormat.format(msg,
              pShippingGroupItemRelationship.getCommerceItem().getId(),
          pShippingGroupItemRelationship.getShippingGroup().getShippingGroupClassType()));
    else
      pShippingGroupItemRelationship.setStateDetail(pDetail);

    // create the new modification object and add it to the list.
    pModificationList.add(createRelationshipUpdateModification("state",
                                                       Integer.valueOf(oldState),
                                                       Integer.valueOf(pShippingGroupItemRelationship.getState()),
                                                       pShippingGroupItemRelationship.getId()));
    pModificationList.add(createRelationshipUpdateModification("stateDetail",
                                                       oldStateDetail,
                                                       pShippingGroupItemRelationship.getStateDetail(),
                                                       pShippingGroupItemRelationship.getId()));

  }

  //------------------------------------
  /**
   * Set the state of the item relationship.  Two modifications are
   * added to pModificationList.  One for the state and one for the
   * state detail.
   *
   * @param pShippingGroupItemRelationship The item to update
   * @param pNewState The state to set the item to.
   * @param pDetail The state detail message to use.  If null, a default will be used.
   * @param pModificationList The place to save the modification
   **/
  public void setSubSkuItemState(SubSkuCommerceItem pItem, ShippingGroup pShippingGroup,
                      int pNewState, String pDetail, List pModificationList)
  {
    ShipItemRelationshipStates sirs = getShipItemRelationshipStates();
    int oldState = pItem.getState();
    String oldStateDetail = pItem.getStateDetail();
    String msg = null;

    // Modify the state and state detail of the item
    pItem.setState(pNewState);

    if(pDetail != null) {
      pItem.setStateDetail(pDetail);
    }
    else {
      if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.PENDING_DELIVERY))
      {
        msg = Constants.ITEM_RELATIONSHIP_STATE_PENDING_DELIVERY;
      }
      else if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.DELIVERED))
      {
        msg = Constants.ITEM_RELATIONSHIP_STATE_DELIVERED;
      }
      else if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.FAILED))
      {
        msg = Constants.ITEM_RELATIONSHIP_STATE_FAILED;
      }
      else if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.ITEM_NOT_FOUND))
      {
        msg = Constants.ITEM_RELATIONSHIP_STATE_ITEM_NOT_FOUND;
      }
      else if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.OUT_OF_STOCK))
      {
        msg = Constants.ITEM_RELATIONSHIP_STATE_OUT_OF_STOCK;
      }
      else if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.DISCONTINUED))
      {
        msg = Constants.ITEM_RELATIONSHIP_STATE_DISCONTINUED;
      }
      else if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.PRE_ORDERED))
      {
        msg = Constants.ITEM_RELATIONSHIP_STATE_PREORDERED;
      }
      else if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.BACK_ORDERED))
      {
        msg = Constants.ITEM_RELATIONSHIP_STATE_BACKORDERED;
      }
      else if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.PENDING_REMOVE))
      {
        msg = Constants.ITEM_RELATIONSHIP_STATE_PENDING_REMOVE;
      }
      else if(pNewState == sirs.getStateValue(ShipItemRelationshipStates.REMOVED))
      {
        msg = Constants.ITEM_RELATIONSHIP_STATE_REMOVED;
      }

      pItem.setStateDetail(MessageFormat.format(msg, pItem.getId(), pShippingGroup.getId()));
    }

    // create the new modification object and add it to the list.
    pModificationList.add(createItemUpdateModification("state",
          Integer.valueOf(oldState), Integer.valueOf(pItem.getState()), pItem.getId()));
    pModificationList.add(createItemUpdateModification("stateDetail",
          oldStateDetail, pItem.getStateDetail(), pItem.getId()));
  }

  //------------------------------------
  /**
   * Set the state of the item.  Two modifications are
   * added to pModificationList.  One for the state and one for the
   * state detail.
   *
   * @param pItem The item to update
   * @param pNewState The state to set the item to.
   * @param pDetail The state detail message to use.  If null, a default will be used.
   * @param pModificationList The place to save the modification
   **/
  public void setItemState(CommerceItem pItem,
                              int pNewState,
                              String pDetail,
                              List pModificationList)

  {
    CommerceItemStates is = getCommerceItemStates();
    int oldState = pItem.getState();
    String oldStateDetail = pItem.getStateDetail();

    String msg = null;

    if(pNewState == is.getStateValue(CommerceItemStates.PENDING_REMOVE))
    {
      msg = Constants.ITEM_STATE_PENDING_REMOVE;
    }
    else if(pNewState == is.getStateValue(CommerceItemStates.REMOVED))
    {
      msg = Constants.ITEM_STATE_REMOVED;
    }

    // Modify the state and state detail of the item
    pItem.setState(pNewState);

    if(pDetail == null)
    pItem.setStateDetail(MessageFormat.format(msg, pItem.getId()));
    else
      pItem.setStateDetail(pDetail);

    // create the new modification object and add it to the list.
    pModificationList.add(createItemUpdateModification("state",
                                                       Integer.valueOf(oldState),
                                                       Integer.valueOf(pItem.getState()),
                                                       pItem.getId()));
    pModificationList.add(createItemUpdateModification("stateDetail",
                                                       oldStateDetail,
                                                       pItem.getStateDetail(),
                                                       pItem.getId()));

  }

  //-------------------------------------
  /**
   * Set the state of the shipping group.  Two modifications are
   * added to pModificationList.  One for the state and one for the
   * state detail.
   *
   * @param pShippingGroup The shipping group to update
   * @param pNewState The state to set the shipping group to.
   * @param pDetail The state detail message to use.  If null, a default will be used.
   * @param pModificationList The place to save the modification
   **/
  public void setShippingGroupState(ShippingGroup pShippingGroup,
                                    int pNewState,
                                    String pDetail,
                                    List pModificationList)

  {
    ShippingGroupStates sgs = getShippingGroupStates();
    int oldState = pShippingGroup.getState();
    String oldStateDetail = pShippingGroup.getStateDetail();
    String msg = null;

    if(pNewState == sgs.getStateValue(ShippingGroupStates.INITIAL))
    {
      msg = Constants.SHIP_GROUP_STATE_INITIAL;
    }
    else if(pNewState == sgs.getStateValue(ShippingGroupStates.PROCESSING))
    {
      msg = Constants.SHIP_GROUP_STATE_PROCESSING;
    }
    else if(pNewState == sgs.getStateValue(ShippingGroupStates.PENDING_REMOVE))
    {
      msg = Constants.SHIP_GROUP_STATE_PENDING_REMOVE;
    }
    else if(pNewState == sgs.getStateValue(ShippingGroupStates.REMOVED))
    {
      msg = Constants.SHIP_GROUP_STATE_REMOVED;
    }
    else if(pNewState == sgs.getStateValue(ShippingGroupStates.FAILED))
    {
      msg = Constants.SHIP_GROUP_STATE_FAILED;
    }
    else if(pNewState == sgs.getStateValue(ShippingGroupStates.NO_PENDING_ACTION))
    {
      msg = Constants.SHIP_GROUP_STATE_NO_PENDING_ACTION;
    }
    else if(pNewState == sgs.getStateValue(ShippingGroupStates.PENDING_MERCHANT_ACTION))
    {
      msg = Constants.SHIP_GROUP_STATE_PENDING_MERCHANT_ACTION;
    }
    else if(pNewState == sgs.getStateValue(ShippingGroupStates.PENDING_SHIPMENT))
    {
      msg = Constants.SHIP_GROUP_STATE_PENDING_SHIPMENT;
    }

    // Modify the state and state detail of the item
    pShippingGroup.setState(pNewState);

    if(pDetail == null)
      pShippingGroup.setStateDetail(MessageFormat.format(msg, pShippingGroup.getId()));
    else
      pShippingGroup.setStateDetail(pDetail);

    // create the new modification object and add it to the list.
    pModificationList.add(createShipUpdateModification("state",
                                                       Integer.valueOf(oldState),
                                                       Integer.valueOf(pShippingGroup.getState()),
                                                       pShippingGroup.getId()));
    pModificationList.add(createShipUpdateModification("stateDetail",
                                                       oldStateDetail,
                                                       pShippingGroup.getStateDetail(),
                                                       pShippingGroup.getId()));

  }


  //-------------------------------------
  /**
   * Set the state of the payment group.  Two modifications are
   * added to pModificationList.  One for the state and one for the
   * state detail.
   *
   * @param pPaymentGroup The shipping group to update
   * @param pNewState The state to set the shipping group to.
   * @param pDetail The state detail message to use.  If null, a default will be used.
   * @param pModificationList The place to save the modification
   **/
  public void setPaymentGroupState(PaymentGroup pPaymentGroup,
                                      int pNewState,
                                      String pDetail,
                                      List pModificationList)

  {
    PaymentGroupStates pgs = getPaymentGroupStates();
    int oldState = pPaymentGroup.getState();
    String oldStateDetail = pPaymentGroup.getStateDetail();
    String msg = null;

    if(pNewState == pgs.getStateValue(PaymentGroupStates.INITIAL))
    {
      msg = Constants.PAYMENT_GROUP_STATE_INITIAL;
    }
    else if(pNewState == pgs.getStateValue(PaymentGroupStates.AUTHORIZED))
    {
      msg = Constants.PAYMENT_GROUP_STATE_AUTHORIZED;
    }
    else if(pNewState == pgs.getStateValue(PaymentGroupStates.SETTLED))
    {
      msg = Constants.PAYMENT_GROUP_STATE_SETTLED;
    }
    else if(pNewState == pgs.getStateValue(PaymentGroupStates.AUTHORIZE_FAILED))
    {
      msg = Constants.PAYMENT_GROUP_STATE_AUTHORIZE_FAILED;
    }
    else if(pNewState == pgs.getStateValue(PaymentGroupStates.SETTLE_FAILED))
    {
      msg = Constants.PAYMENT_GROUP_STATE_SETTLE_FAILED;
    }
    else if(pNewState == pgs.getStateValue(PaymentGroupStates.REMOVED))
    {
      msg = Constants.PAYMENT_GROUP_STATE_REMOVED;
    }

    // Modify the state and state detail of the item
    pPaymentGroup.setState(pNewState);

    if(pDetail == null)
      pPaymentGroup.setStateDetail(MessageFormat.format(msg, pPaymentGroup.getId()));
    else
      pPaymentGroup.setStateDetail(pDetail);

    // create the new modification object and add it to the list.
    pModificationList.add(createPayUpdateModification("state",
                                                       Integer.valueOf(oldState),
                                                       Integer.valueOf(pPaymentGroup.getState()),
                                                       pPaymentGroup.getId()));
    pModificationList.add(createPayUpdateModification("stateDetail",
                                                       oldStateDetail,
                                                       pPaymentGroup.getStateDetail(),
                                                       pPaymentGroup.getId()));

  }

  //-------------------------------------
  /**
   * Set the state of the order.  Two modifications are
   * added to pModificationList. One for the state and one for the
   * state detail.
   *
   * @param pOrder The order to update
   * @param pNewState The state to set the shipping group to.
   * @param pDetail The state detail message to use.  If null, a default will be used.
   * @param pModificationList The place to save the modification
   **/
  public void setOrderState(Order pOrder,
                               int pNewState,
                               String pDetail,
                               List pModificationList)

  {
    OrderStates os = getOrderStates();
    int oldState = pOrder.getState();
    String oldStateDetail = pOrder.getStateDetail();
    String msg = null;

    if(pNewState == os.getStateValue(OrderStates.INCOMPLETE))
    {
      msg = Constants.ORDER_STATE_INCOMPLETE;
    }
    else if(pNewState == os.getStateValue(OrderStates.SUBMITTED))
    {
      msg = Constants.ORDER_STATE_SUBMITTED;
    }
    else if(pNewState == os.getStateValue(OrderStates.PROCESSING))
    {
      msg = Constants.ORDER_STATE_PROCESSING;
    }
    else if(pNewState == os.getStateValue(OrderStates.PENDING_REMOVE))
    {
      msg = Constants.ORDER_STATE_PENDING_REMOVE;
    }
    else if(pNewState == os.getStateValue(OrderStates.REMOVED))
    {
      msg = Constants.ORDER_STATE_REMOVED;
    }
    else if(pNewState == os.getStateValue(OrderStates.NO_PENDING_ACTION))
    {
      msg = Constants.ORDER_STATE_NO_PENDING_ACTION;
    }
    else if(pNewState == os.getStateValue(OrderStates.FAILED))
    {
      msg = Constants.ORDER_STATE_FAILED;
    }
    else if(pNewState == os.getStateValue(OrderStates.PENDING_MERCHANT_ACTION))
    {
      msg = Constants.ORDER_STATE_PENDING_MERCHANT_ACTION;
    }
    else if(pNewState == os.getStateValue(OrderStates.PENDING_CUSTOMER_ACTION))
    {
      msg = Constants.ORDER_STATE_PENDING_CUSTOMER_ACTION;
    }
    else if(pNewState == os.getStateValue(OrderStates.QUOTED))
    {
      msg = Constants.ORDER_STATE_QUOTED;
    }

    // Modify the state and state detail of the item
    pOrder.setState(pNewState);

    if(pDetail == null)
      pOrder.setStateDetail(MessageFormat.format(msg, pOrder.getId()));
    else
      pOrder.setStateDetail(pDetail);

    // create the new modification object and add it to the list.
    pModificationList.add(createOrderUpdateModification("state",
                                                       Integer.valueOf(oldState),
                                                       Integer.valueOf(pOrder.getState()),
                                                       pOrder.getId()));
    pModificationList.add(createOrderUpdateModification("stateDetail",
                                                       oldStateDetail,
                                                       pOrder.getStateDetail(),
                                                       pOrder.getId()));

  }

  //-------------------------------------
  /**
   * This method checks how many fulfillers that the items within the
   * given shipping group are fulfilled by.  If all items use the same
   * fulfiller, the true is returned.  If some items use a different
   * fulfiller than other items, then false is returned.
   *
   * @param pShipGroup The shipping group being checked.
   * @return true if shipping group uses a single fulfiller, false otherwise
   **/
  public boolean isShippingGroupSingleFulfiller(ShippingGroup pShipGroup)
    throws CommerceException
  {
    boolean returnValue = true;
    String currentFulfiller = null;
    List itemList = getOrderManager().getCommerceItemManager().getCommerceItemsFromShippingGroup(pShipGroup);
    Iterator iter = itemList.iterator();
    CommerceItem ci = null;

    while (iter.hasNext() && returnValue) {
      ci = (CommerceItem) iter.next();

      // If this current fulfiller is null that means this is the first iteration or all of
      // the fulfillers are null.  We will assign the value of the catalog ref's fulfillre to
      // the currentFulfiller.
      if (currentFulfiller==null) {
        currentFulfiller=getFulfillerForItem(ci);
      }
      else {
        // If the currentfulfiller is different than the fulfiller specified by the
        // catalogRef then some items have a different fulfiller.
        if (!currentFulfiller.equalsIgnoreCase(getFulfillerForItem(ci)))
          returnValue=false;
      }
    }

    return returnValue;
  }


  //-------------------------------------
  /**
   * This method returns a HashMap whose key is the fulfiller and the value is a List of the
   * ShippingGroups.  This method assumes that every shipping group passed in contains items
   * for one and only one fulfiller.
   * @param pShipGroupList - a List of the shipping groups to be parsed.
   * @return the HashMap
   **/
  public HashMap getFulfillersForShippingGroups(List pShipGroupList)
  {
    HashMap fulfillers = new HashMap(7);
    List itemList = null;
    List list= null;
    Iterator iter = pShipGroupList.iterator();
    ShippingGroup sg=null;
    String fulfiller=null;

    while (iter.hasNext()) {
      sg = (ShippingGroup) iter.next();
      try {
    itemList= getOrderManager().getCommerceItemManager().getCommerceItemsFromShippingGroup(sg);
    if (!itemList.isEmpty()) {
      fulfiller= getFulfillerForItem( (CommerceItem) itemList.get(0));
      if (!fulfillers.containsKey(fulfiller)) {
        list= new ArrayList();
        fulfillers.put(fulfiller, list);
      }
      list= (List) fulfillers.get(fulfiller);
      list.add(sg);
    }
      }
      catch(CommerceException e) {

      }

    }

    return fulfillers;
  }

  /**
   * <p> Maps the fulfiller name to the port on which it should be sent. </p>
   * @param pFulfiller - the name of the fulfiller
   * @return the portname on which the message will be sent.
   **/
  public String getFulfillerPort(String pFulfiller)
  {
    return mFulfillerPortNameMap.getProperty(pFulfiller);
  }


  private void printoutFulfillerPortMap(Properties pFulfillerPortNameMap) {
    Enumeration keys = pFulfillerPortNameMap.keys();
    String key = null;
    String value = null;

    while (keys.hasMoreElements()) {
      key = (String) keys.nextElement();
      value = pFulfillerPortNameMap.getProperty(key);
      System.out.print("Key= " + key + " value= " );
      if (value != null)
    System.out.println(value);
      else
    System.out.println("null");
    }

  }


  //-------------------------------------
  /**
   * Returns a list of shipping group ids from a given list of shipping groups.
   * @param pShippingGroups - a List of ShippingGroups
   * @see atg.commerce.order.ShippingGroup
   **/
  public List extractShippingGroupIds(List pShippingGroups)
  {
    List shippingGroupIds = new ArrayList(pShippingGroups.size());
    Iterator iter = pShippingGroups.iterator();
    ShippingGroup sg = null;

    while (iter.hasNext()) {
      sg= (ShippingGroup) iter.next();
      shippingGroupIds.add(sg.getId());
    }

    return shippingGroupIds;
  }

  /**
   * <p> This method will split the shipping groups such that none of the shipping groups will
   * contain items from multiple shipping groups.  This method assumes that the caller will
   * write the order back to permanent storage. </p>
   * @param pOrder - the order containing the shipping groups
   * @param pShippingGroups - a List of the shipping groups that require some form of splitting
   * @param pModificationList - A place holder for all new modifications
   **/
  public List splitShippingGroupsByFulfiller(Order pOrder,
                         List pShippingGroups,
                                             List pModificationList)
    throws atg.commerce.CommerceException
  {
    HashMap shippingGroupItemMap = null;
    Iterator shipIterator = pShippingGroups.iterator();
    Iterator keysIterator = null;
    ShippingGroup currentShipGroup = null;
    String fulfiller = null;
    List itemIdList = null;
    String[] itemIds = null;
    List returnedGroups = new ArrayList();
    List totalReturnedGroups = new ArrayList();

    // Cycle through the shipping groups that need to be shipped.
    while (shipIterator.hasNext()) {
      currentShipGroup = (ShippingGroup) shipIterator.next();
      // For each shipping group get a HashMap where the keys are the fulfiller names and the
      // values are a List of the item ids.
      shippingGroupItemMap = getItemFulfillerMap(pOrder, currentShipGroup);

      // Iterate through the keys of the hashmap. For every fulfiller cause the shipping group
      // to be split with the item ids specified.
      keysIterator = shippingGroupItemMap.keySet().iterator();
      // We should check to see how many fulfillers are listed.  We need to split the shipping
      // group one less times than the number of fulfillers.  Therefor we will eat up the first
      // fulfiller.
      if (keysIterator.hasNext())
    fulfiller = (String) keysIterator.next();

      while(keysIterator.hasNext()) {
    fulfiller = (String) keysIterator.next();
    //System.out.println("Splitting for fulfiller: " + fulfiller);

    itemIdList = (List) shippingGroupItemMap.get(fulfiller);
    itemIds = (String[]) itemIdList.toArray(new String[itemIdList.size()]);
    //System.out.println("Number of items to go to this fulfiller: " + itemIds.length);

    returnedGroups = getOrderManager().getShippingGroupManager().splitShippingGroup(pOrder,
                                  currentShipGroup.getId(),
                                  itemIds);
        // create modifications
        Iterator newGroups = returnedGroups.iterator();
        while(newGroups.hasNext()) {
          ShippingGroup newSg = (ShippingGroup)newGroups.next();
          if(newSg == currentShipGroup) {
            //this is not the new group
          } else {
            // this is the new group
            GenericAdd ga =
              createGenericAddValueToIdModification(Modification.TARGET_SHIPPING_GROUP,
                                                    newSg,
                                                    Modification.TARGET_ORDER,
                                                    pOrder.getId());
            pModificationList.add(ga);
            // add an update for each relationship in the group
            List rels = newSg.getCommerceItemRelationships();
            Iterator sgcirs = rels.iterator();
            ShippingGroupCommerceItemRelationship changedSgcir = null;
            while(sgcirs.hasNext()) {
              changedSgcir = (ShippingGroupCommerceItemRelationship)sgcirs.next();
              GenericUpdate gu = createGenericUpdateModification("shippingGroup",
                                                                 currentShipGroup,
                                                                 newSg,
                                                                 changedSgcir.getId(),
                                                                 Modification.TARGET_RELATIONSHIP,
                                                                 true);
              pModificationList.add(gu);
            } //while
          } //else
        } // while

      } // while keysIterator
      totalReturnedGroups.addAll(returnedGroups);
    } // while shipIterator
    return totalReturnedGroups;
  } // splitShippingGroupsByFulfiller


  //-------------------------------------
  /**
   * <p> Given an order and a shipping group will generate a HashMap where the keys are the
   * fulfillers an the values are the List of item ids which belong to that fulfiller. </p>
   * @param pOrder - the order containing the shipping gorup
   * @param pShippingGroup - the shipping group passed
   * @return a HashMap where the keys are the fulfillers and the values are Lists of item ids
   * belonging to that shipping group.
   **/
  public HashMap getItemFulfillerMap(Order pOrder,
                     ShippingGroup pShippingGroup)
    throws CommerceException, InvalidParameterException
  {
    HashMap returnMap = new HashMap(7);
    List itemList = getOrderManager().getCommerceItemManager().getCommerceItemsFromShippingGroup(pShippingGroup);
    List itemIdList = null;
    CommerceItem item = null;
    String fulfiller = null;
    Iterator itemIterator = itemList.iterator();

    while (itemIterator.hasNext()) {
      item = (CommerceItem) itemIterator.next();
      fulfiller = getFulfillerForItem(item);
      if (!returnMap.containsKey(fulfiller)) {
    itemIdList = new ArrayList();
    returnMap.put(fulfiller, itemIdList);
      }
      itemIdList = (List) returnMap.get(fulfiller);
      itemIdList.add(item.getId());
    }

    return returnMap;
  }

  //-------------------------------------
  /**
   * Sets the status of the given modification to Modification.STATUS_FAILED
   * then adds it to the given list.
   *
   * @param pModification The modification that failed.
   * @param pModificationList The list to add the modification to
   * @param pStatusText The text to set the modifications statusText property to
   * @see Modification#STATUS_FAILED
   **/
  public void modificationFailed(Modification pModification, List pModificationList, String pStatusText)
  {
    pModification.setModificationStatus(Modification.STATUS_FAILED);
    pModification.setModificationStatusText(pStatusText);
    pModificationList.add(pModification);
  }

  //-------------------------------------
  /**
   * Sets the status of the given modification to Modification.STATUS_SUCCESS
   * then addes it to the given list.
   *
   * @param pModification The modification that failed.
   * @param pModificationList The list to add the modification to
   * @see Modification#STATUS_SUCCESS
   **/
  public void modificationSucceeded(Modification pModification, List pModificationList)
  {
    pModification.setModificationStatus(Modification.STATUS_SUCCESS);
    pModificationList.add(pModification);
  }

  //-------------------------------------
  /**
   * <p> This method returns the fulfiller for a given CommerceItem.  The standard
   * implementation uses the AuxiliaryData and the catalog ref to get a hold of the fulfiller.
   * @param pItem the item whose fulfiller is needed
   * @return the fulfiller name
   **/
  public String getFulfillerForItem(CommerceItem pItem)
    throws CommerceException
  {
    RepositoryItem ref = (RepositoryItem)getCatalogRef(pItem);
    String fulfiller = null;
    if(ref != null) {
      fulfiller =  (String) ref.getPropertyValue(atg.commerce.fulfillment.Constants.FULFILLER_PROPERTY_NAME);
    }

    if((fulfiller == null) || fulfiller.equals(""))
      fulfiller = getDefaultFulfiller();

    return fulfiller;
  }

  //-------------------------------------
  /**
   * Return the fulfiller for the given shipping group.  This method assumes
   * that this shipping group uses one and only one fulfiller.  This method
   * assumes the shipping group is not empty.
   *
   * @param pShippingGroup The shipping group whose fulfiller is needed
   * @return The fulfiller
   **/
  public String getFulfillerForShippingGroup(ShippingGroup pShippingGroup)
  {
    List itemList = null;
    String fulfiller = null;

    try {
      itemList = getOrderManager().getCommerceItemManager().getCommerceItemsFromShippingGroup(pShippingGroup);
      if (!itemList.isEmpty()) {
        fulfiller = getFulfillerForItem( (CommerceItem) itemList.get(0));
      }
    } catch(CommerceException i) {

    }

    return fulfiller;
  }

  /**
   * Using the fulfillerShippingGroupClassMap, this method will verify
   * that each shipping group is of a type that the fulfiller can handle.
   * Assumes the order is not null.
   *
   * @param pOrder The order containing the shipping groups
   * @param pFulfiller The name of the fulfiller
   * @param pShippingGroupIds The list of shipping groups destined for this fulfiller
   * @exception InvalidShippingGroupTypeException Thrown if the shipping group is of
   *            an incorrect type
   **/
  public void verifyShippingGroupsForFulfiller(Order pOrder, String pFulfiller, List pShippingGroupIds)
    throws CommerceException
  {
    // no need to check if there are no shipping groups
    if((pShippingGroupIds == null) ||
       (pShippingGroupIds.size() == 0))
      return;

    HashMap classMap = getFulfillerShippingGroupClassHashMap();
    if(classMap == null)
      throw new FulfillerNotFoundException("FulfillerShippingGroupClassHashMap is null.");
    List shippingGroupClasses = (List) classMap.get(pFulfiller);
    if(shippingGroupClasses == null)
      throw new FulfillerNotFoundException("Fulfiller not found in class map.");

    Iterator shippingGroupIds = pShippingGroupIds.iterator();
    int classNum = 0;
    boolean legal = true;
    try {
      while(shippingGroupIds.hasNext()) {
        String sgId = (String) shippingGroupIds.next();
        ShippingGroup sg = pOrder.getShippingGroup(sgId);
        for(classNum=0; classNum<shippingGroupClasses.size(); classNum++) {
          if(Class.forName((String)shippingGroupClasses.get(classNum)).isInstance(sg)) {
            legal = true;
            break;
          }
          else
            legal = false;
        }
        if(!legal)
          throw new InvalidShippingGroupTypeException("Shipping group of the wrong type");
      }
    }
    catch(ClassNotFoundException c) {
      throw new CommerceException(c);
    }
  }

  /**
   * For debugging purposes, print out all the order information for the given order
   * to standard out
   *
   * @param pOrder The order to print
   **/
  public void printOrder(Order pOrder)
  {
    OrderStates os = getOrderStates();
    ShippingGroupStates sgs = getShippingGroupStates();
    ShipItemRelationshipStates sirs = getShipItemRelationshipStates();
    CommerceItemStates is = getCommerceItemStates();
    PaymentGroupStates pgs = getPaymentGroupStates();

    System.out.println("____________________________");

    if(pOrder == null) {
      System.out.println("Order is null.");
      return;
    }

    System.out.println("Order: " + pOrder.getId() + ", state = " +
                       os.getStateString(pOrder.getState()));
    //System.out.println("       " + pOrder.getStateDetail());

    List groups = pOrder.getShippingGroups();
    Iterator shippingGroups = null;
    if(groups != null) {
      shippingGroups = groups.iterator();
    } else {
      System.out.println("Order contains no shipping groups.");
      return;
    }
    while(shippingGroups.hasNext()) {
      ShippingGroup sg = (ShippingGroup) shippingGroups.next();

      System.out.println("  sg: " + sg.getId() + ", " + sg.getShippingGroupClassType() + ", state = " +
                         sgs.getStateString(sg.getState()));
      //System.out.println("      " + sg.getStateDetail());

      List items = sg.getCommerceItemRelationships();
      Iterator itemRelationships = null;
      if(items != null) {
        itemRelationships = items.iterator();
      } else {
        System.out.println("Shipping group contains no item relationships.");
        continue;
      }

      while(itemRelationships.hasNext()) {
        ShippingGroupCommerceItemRelationship sgcir = (ShippingGroupCommerceItemRelationship) itemRelationships.next();

        if(sgcir == null) {
          System.out.println("Relationship is null.");
          continue;
        }
        long quantity = 0;
        CommerceItem item = sgcir.getCommerceItem();
        if(item == null) {
          System.out.println("ShippingGroupCommerceItemRelationship has no item associated with it.");
          continue;
        }

        if(sgcir.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITY)
          quantity = sgcir.getQuantity();

        if(getOrderManager() == null) {
          System.out.println("OrderManager is null");
          continue;
        }
        if(sgcir.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITYREMAINING)
          quantity = getOrderManager().getShippingGroupManager().getRemainingQuantityForShippingGroup(item);

        AuxiliaryData data = item.getAuxiliaryData();
        if(data == null) {
          System.out.println("Auxiliary data is null.");
          continue;
        }

        try {
          System.out.println("    item: " + item.getId() + ", (fulfiller: " + getFulfillerForItem(item) + "), sku = " +
                             item.getCatalogRefId() + ", quantity = " + quantity
                             + ", state = " +
                             sirs.getStateString(sgcir.getState()));
          //System.out.println("    detail: " + sgcir.getStateDetail());
        } catch(CommerceException c) {
          System.out.println("Didn't work: " + c);
        }
      }
    } // while

    List itemList = pOrder.getCommerceItems();
    Iterator items = null;
    if(itemList != null) {
      items = itemList.iterator();
    } else {
      System.out.println("Order has no items.");
      return;
    }

    while(items.hasNext()) {
      CommerceItem item = (CommerceItem) items.next();
      System.out.println("  Item: " + item.getId() + ", quantity = " + item.getQuantity() + ", state = " +
                         is.getStateString(item.getState()));

    }// while
    System.out.println("____________________________");
  }

  /**
   * This gets a CatalogRef object for a given item.
   *
   * @param pItem The item we want the CatalogRef from
   * @return a CatalogRef object
   **/
  public Object getCatalogRef(CommerceItem pItem)
    throws CommerceException
  {
    Object ref = pItem.getAuxiliaryData().getCatalogRef();
    try {
      CatalogTools tools = getCatalogTools();

      if(ref == null) {
        String refId = pItem.getCatalogRefId();

        // Load the ref using the refId from the repository
        // This shouldn't be necessary since the catalog ref should be
        // loaded during OrderManager.loadOrder
        if (tools instanceof atg.commerce.catalog.custom.CustomCatalogTools)
          ref = ((atg.commerce.catalog.custom.CustomCatalogTools) tools).findSKU(refId, pItem.getCatalogKey(), false);
        else
          ref = tools.findSKU(refId, pItem.getCatalogKey());

        pItem.getAuxiliaryData().setCatalogRef(ref);
      }
    } catch(RepositoryException r) {
      throw new CommerceException(r);
    }
    return ref;
  }

  //-------------------------------------
  /**
   * <p> This method will send an ModifyOrderNotification message </p>
   *
   * @param pOrderId The order that was modified
   * @param pModificationList The modifications that were made
   * @param pModifyOrder The request that initiated these modifications.  This is null
   *                     if these changes were not requested by an incoming message.
   * @param pMessageSource The source of the message.
   * @param pMessagePort the port to send the message out on
   * @param pOriginalMessage if this message is being forwarded, this is the original
   *                         message that was received.  If method call is creating the
   *                         original message, this parameter should be null
   **/
  public void sendModifyOrderNotification(String pOrderId,
                                          List pModificationList,
                                          ModifyOrder pModifyOrder,
                                          SourceSinkTemplate pMessageSource,
                                          String pMessagePort,
                                          ModifyOrderNotification pOriginalMessage)
    throws javax.jms.JMSException
  {
    // if modifications have not been made, no need to send message
    if (pModificationList.size() > 0)
    {
      // we are assuming the modifications have already been made.  We just
      // need to construct the message with the appropriate Modification []
      Modification[] mods = (Modification[])
        pModificationList.toArray(new Modification[pModificationList.size()]);

      ModifyOrderNotification mon = new ModifyOrderNotification();
      mon.setOrderId(pOrderId);
      mon.setModifications(mods);
      mon.setSource(pMessageSource.getMessageSourceName());
      mon.setId(getNextMessageId());

      if(pModifyOrder != null) {
        mon.setModifyOrderId(pModifyOrder.getId());
        mon.setModifyOrderSource(pModifyOrder.getSource());
      }

      // no user
      mon.setUserId(null);

      if(pOriginalMessage == null) {
        mon.setOriginalSource(pMessageSource.getMessageSourceName());
        mon.setOriginalId(mon.getId());
        mon.setOriginalUserId(mon.getUserId());
      } else {
        mon.setOriginalSource(pOriginalMessage.getOriginalSource());
        mon.setOriginalId(pOriginalMessage.getOriginalId());
        mon.setOriginalUserId(pOriginalMessage.getOriginalUserId());
      }

      pMessageSource.sendCommerceMessage(mon, pMessagePort);

    } // else ignore

  } // sendModifyOrderNotification

  //-------------------------------------
  /**
   * <p> This method will send an ModifyOrder message </p>
   *
   * @param pOrderId The order that was modified
   * @param pModificationList The modifications that were made
   * @param pMessageSource The source of the message.
   * @param pMessagePort the port to send the message out on
   * @param pOriginalMessage if this message is being forwarded, this is the original
   *                         message that was received.  If method call is creating the
   *                         original message, this parameter should be null
   **/
  public void sendModifyOrder(String pOrderId,
                              List pModificationList,
                              SourceSinkTemplate pMessageSource,
                              String pMessagePort,
                              ModifyOrder pOriginalMessage)
    throws javax.jms.JMSException
  {
    // if modifications have not been made, no need to send message
    if (pModificationList.size() > 0)
    {
      // we are assuming the modifications have already been made.  We just
      // need to construct the message with the appropriate Modification []
      Modification[] mods = (Modification[])
        pModificationList.toArray(new Modification[pModificationList.size()]);

      ModifyOrder mo = new ModifyOrder();
      mo.setOrderId(pOrderId);
      mo.setModifications(mods);
      mo.setSource(pMessageSource.getMessageSourceName());
      mo.setId(getNextMessageId());

      // no user
      mo.setUserId(null);

      if(pOriginalMessage == null) {
        mo.setOriginalSource(pMessageSource.getMessageSourceName());
        mo.setOriginalId(mo.getId());
        mo.setOriginalUserId(mo.getUserId());
      } else {
        mo.setOriginalSource(pOriginalMessage.getOriginalSource());
        mo.setOriginalId(pOriginalMessage.getOriginalId());
        mo.setOriginalUserId(pOriginalMessage.getOriginalUserId());
      }

      pMessageSource.sendCommerceMessage(mo, pMessagePort);

    } // else ignore

  } // sendModifyOrder

  //-------------------------------------
  /**
   * This method takes a list of shipping groups, and sends a
   * ShippingGroupUpdate modification to the appropriate fulfillers.
   * If this modification is being forwarded, the appropriate
   * attributes are used from the original modification.
   *
   * @param pOrder the order containing all the shipping groups.
   * @param pShippingGroupList The list of shipping groups to send
   * @param pMessageSource The source of the new message.
   * @param pOriginalMessage The original message, null if irrelevant
   * @exception javax.jms.JMSException
   **/
  public void sendShippingGroupUpdateModification(Order pOrder,
                                                     List pShippingGroupList,
                                                     SourceSinkTemplate pMessageSource,
                                                     ModifyOrderNotification pOriginalMessage)
    throws javax.jms.JMSException
  {
    HashMap fulfillerMap = getFulfillersForShippingGroups(pShippingGroupList);

    Iterator fulfillerIterator = fulfillerMap.entrySet().iterator();
    String fulfiller = null;
    String fulfillerPort = null;
    List shippingGroups = null;
    List thisFulfillersShippingGroupIds = null;

    while (fulfillerIterator.hasNext()) {
      Map.Entry entry = (Map.Entry)fulfillerIterator.next();
      fulfiller = (String)entry.getKey();

      fulfillerPort = getFulfillerPort(fulfiller);

      // Send the message for the shipping groups for that fulfiller (from the map)
      shippingGroups = (List) entry.getValue();
      thisFulfillersShippingGroupIds = extractShippingGroupIds(shippingGroups);

      ShippingGroupUpdate sgu = new ShippingGroupUpdate();
      sgu.setShippingGroupIds((String[])
                              thisFulfillersShippingGroupIds.toArray(new String[thisFulfillersShippingGroupIds.size()]));
      List mods = new ArrayList();
      mods.add(sgu);

      sendModifyOrderNotification(pOrder.getId(), mods, null, pMessageSource, fulfillerPort, pOriginalMessage);
    }
  }

  /**
   * Check if all the groups in this object are PENDING_REMOVE
   *
   * @param pOrder The order we are checking
   * @return true if all groups are removed, false otherwise
   **/
  public boolean areAllGroupsPendingRemove(Order pOrder) {
    List shippingGroups = pOrder.getShippingGroups();
    Iterator shipIter = shippingGroups.iterator();
    ShippingGroupStates sgs = getShippingGroupStates();

    while(shipIter.hasNext()) {
      ShippingGroup sg = (ShippingGroup) shipIter.next();

      if(sg.getState() == sgs.getStateValue(ShippingGroupStates.PENDING_REMOVE))
      {
        // nothing to do
        continue;
      }
      else {
        return false;
      }
    }

    return true;
  }

  /**
   * Check if all the groups in this object are REMOVED
   *
   * @param pOrder The order we are checking
   * @return true if all groups are removed, false otherwise
   **/
  public boolean areAllGroupsRemoved(Order pOrder)
  {
    List shippingGroups = pOrder.getShippingGroups();
    Iterator shipIter = shippingGroups.iterator();
    ShippingGroupStates sgs = getShippingGroupStates();

    while(shipIter.hasNext()) {
      ShippingGroup sg = (ShippingGroup) shipIter.next();

      if(sg.getState() == sgs.getStateValue(ShippingGroupStates.REMOVED))
      {
        // nothing to do
        continue;
      }
      else {
        return false;
      }
    }

    return true;
  }


  //------------------------------------
  /**
   * Loop through all the shipping groups in an order, returning true if the state
   * of all those groups is set to NO_PENDING_ACTION, otherwise return false
   *
   * @param pOrder The order we are interested in
   * @return true if all groups are done, false otherwise
   **/
  public boolean checkIfAllGroupsAreShipped(Order pOrder)
  {
    ShippingGroupStates sgs = getShippingGroupStates();
    List shippingGroups = pOrder.getShippingGroups();
    Iterator shippingGroupIterator = shippingGroups.iterator();
    ShippingGroup sg = null;

    boolean allHaveShipped = true;

    // Assume all are ready for shipment.  If we find any shipping groups that are not in a
    // NO_PENDING_ACTION or a REMOVED state then some shipping groups aren't ready for
    // shipment.
    while(shippingGroupIterator.hasNext() && allHaveShipped) {
      sg = (ShippingGroup) shippingGroupIterator.next();
      if( (sg.getState() != sgs.getStateValue(ShippingGroupStates.NO_PENDING_ACTION)) &&
      (sg.getState() != sgs.getStateValue(ShippingGroupStates.REMOVED)) )
    allHaveShipped = false;
    }

    return allHaveShipped;
  }

  //------------------------------------
  /**
   * Loop through all the shipping groups in an order, returning false if the state of one of
   * those groups is set to NO_PENDING_ACTION, otherwise return true
   *
   * @param pOrder The order we are interested in
   * @return true if no groups are done, false otherwise
   **/
  public boolean hasAnyGroupShipped(Order pOrder)
  {
    ShippingGroupStates sgs = getShippingGroupStates();
    List shippingGroups = pOrder.getShippingGroups();
    Iterator shippingGroupIterator = shippingGroups.iterator();
    ShippingGroup sg = null;

    int curstate;

    while(shippingGroupIterator.hasNext()) {
      sg = (ShippingGroup) shippingGroupIterator.next();
      curstate = sg.getState();
      if (curstate == sgs.getStateValue(ShippingGroupStates.NO_PENDING_ACTION))
      {
    // at least one group is still pending, we can stop
    return true;
      } // if
    } // while

    // we finished looking at all the shipping groups and didn't find any that
    // were not NO_PENDING_ACTION
    // They must all be finished
    return false;
  }

  //------------------------------------
  /**
   * Loop through all the shipping groups in an order, returning false if the state of one of
   * those groups is set to PENDING_SHIPMENT, otherwise return true
   *
   * @param pOrder The order we are interested in
   * @return true if no groups are done, false otherwise
   **/
  public boolean isAnyGroupPendingShipment(Order pOrder)
  {
    ShippingGroupStates sgs = getShippingGroupStates();
    List shippingGroups = pOrder.getShippingGroups();
    Iterator shippingGroupIterator = shippingGroups.iterator();
    ShippingGroup sg = null;

    int curstate;

    while(shippingGroupIterator.hasNext()) {
      sg = (ShippingGroup) shippingGroupIterator.next();
      curstate = sg.getState();
      if (curstate == sgs.getStateValue(ShippingGroupStates.PENDING_SHIPMENT))
      {
    // at least one group is still pending, we can stop
    return true;
      } // if
    } // while

    // we finished looking at all the shipping groups and didn't find any that
    // were not NO_PENDING_ACTION
    // They must all be finished
    return false;
  }


   //-------------------------------------
  /**
   * <p> This method verifies that the order to be fulfilled is in a state which allows it to be
   * fulfilled.  If the order is in an incomplete state or a no pending action state then an
   * exception is thrown. </p>
   *
   * @notyet-beaninfo
   *          description: This method verifies that a given order is in a state that
   *                       allows it to be fulfilled.
   * @param pOrder - the order to be verified.
   * @exception atg.commerce.IllegalOrderStateException if the order is in an illegal state
   **/
  public void verifyOrderFulfillment(Order pOrder)
      throws IllegalOrderStateException
  {
    int state = pOrder.getState();
    OrderStates os = getOrderStates();

    if(state == os.getStateValue(OrderStates.INCOMPLETE))
    {
      throw new
        IllegalOrderStateException(MessageFormat.format(Constants.ORDER_PREFULFILLMENT_STATE, pOrder.getId()));
    }
    else if(state == os.getStateValue(OrderStates.PENDING_REMOVE))
    {
      throw new
        IllegalOrderStateException(MessageFormat.format(Constants.ORDER_PENDING_REMOVE_STATE, pOrder.getId()));
    }
    else if(state == os.getStateValue(OrderStates.REMOVED))
    {
      throw new
        IllegalOrderStateException(MessageFormat.format(Constants.ORDER_REMOVED_STATE, pOrder.getId()));
    }
    else if(state == os.getStateValue(OrderStates.NO_PENDING_ACTION))
    {
      throw new
        IllegalOrderStateException(MessageFormat.format(Constants.ORDER_NO_PENDING_STATE, pOrder.getId()));
    }
  }


  //-------------------------------------
  /**
   * This method returns false if there is any work left to be done on
   * any of the objects within an order.  It pays no attention to the
   * actual state of the order, but looks at each of the PaymentGroups
   * and ShippingGroups contained therein.
   **/
  public boolean isOrderFinished(Order pOrder)
  {
    PaymentGroupStates pgs = getPaymentGroupStates();
    ShippingGroupStates sgs = getShippingGroupStates();
    // check payment groups first for efficiency reasons.  They finish last.
    Iterator paymentGroups = pOrder.getPaymentGroups().iterator();
    PaymentGroup pg = null;
    int state;

    while(paymentGroups.hasNext()) {
      pg = (PaymentGroup) paymentGroups.next();
      state = pg.getState();
      if((state != pgs.getStateValue(PaymentGroupStates.SETTLED)) &&
         (state != pgs.getStateValue(PaymentGroupStates.REMOVED)))
        return false;
    }

    Iterator shippingGroups = pOrder.getShippingGroups().iterator();
    ShippingGroup sg = null;
    while(shippingGroups.hasNext()) {
      sg = (ShippingGroup) shippingGroups.next();
      state = sg.getState();
      if((state != sgs.getStateValue(ShippingGroupStates.NO_PENDING_ACTION)) &&
         (state != sgs.getStateValue(ShippingGroupStates.REMOVED)))
        return false;
    }

    return true;
  }

  //-------------------------------------
  /**
   * This method is called to determine whether the shipping group should be settled at this
   * time or not.  In our default implementation this method will return true on the shipment
   * of the first shipping group, or on the shipment of the last shipping group.
   * If the order is already settled, this returns false.
   *
   * @param pOrder - the order to be settled
   * @param pSettleOnFirstShipment true if the order should be settle after the first shipping
   *                               group ship.
   * @return true if the order should be settled now, false otherwise
   **/
  public boolean isOrderSettleable(Order pOrder, boolean pSettleOnFirstShipment) {
    if(hasOrderSettled(pOrder))
      return false;

    ShippingGroupStates sgs = getShippingGroupStates();

    if (pSettleOnFirstShipment) {
      Iterator shippingGroupIterator = pOrder.getShippingGroups().iterator();
      int curstate;
      while (shippingGroupIterator.hasNext()) {
    curstate = ((ShippingGroup) shippingGroupIterator.next()).getState();
    if ( curstate == sgs.getStateValue(ShippingGroupStates.NO_PENDING_ACTION) )
      return true;
      }
    }
    else {
      return checkIfAllGroupsAreShipped(pOrder);
    }
    return false;
  }

  //-------------------------------------
  /**
   * This returns true if every payment group in the order has settled.
   *
   * @param pOrder The order we are checking.
   * @return true if the order has settled, false otherwise
   **/
  public boolean hasOrderSettled(Order pOrder)
  {
    PaymentGroupStates pgs = getPaymentGroupStates();
    List paymentGroupList = pOrder.getPaymentGroups();

    if((paymentGroupList != null) && (paymentGroupList.size() > 0)) {
      Iterator paymentGroups = paymentGroupList.iterator();
      while(paymentGroups.hasNext()) {
        PaymentGroup pg = (PaymentGroup)paymentGroups.next();

        if((pg.getState() != pgs.getStateValue(PaymentGroupStates.SETTLED)) &&
           (pg.getState() != pgs.getStateValue(PaymentGroupStates.REMOVED))) {
          return false;
        }

      }
    } else {
      return false;
    }
    return true;
  }

  /**
   * Returns the next unique Id for commerce messages.
   **/
  public String getNextMessageId()
  {
    IdGenerator idGen = getMessageIdGenerator();
    String space = getMessageIdSpaceName();
    if (idGen == null)
      return null;

    // generate an id in our id space and return it
    try
    {
      return idGen.generateStringId(space);
    }
    catch (IdGeneratorException ie)
    {
      //System.out.println("Error getting message id.");
      return null;
    }
  }

  // take a delimited string and return a list of its tokens.
  private List stringToList(String pStr)
  {
    StringTokenizer st = new StringTokenizer(pStr);
    List l = new ArrayList();
    while(st.hasMoreTokens()) {
      l.add(st.nextToken());
    }
    return l;
  }
  
  /**
   * First this method finds the sku repository item with the information passed in. After finding the sku, the sku's 
   * <code>fulfiller</code> property value is used to return the fulfiller value. If the sku's fulfiller 
   * property is not set, then this method returns the <code>defaultFulfiller</code> configured value, 
   * otherwise the sku's fulfiller property value is returned.
   * <p>
   * If the <code>skuId</code> is passed in, then this method uses the <code>skuId</code> and optional <code>catalogKey</code> to get sku repository item.
   * If the <code>skuId</code> is not passed in, then this method uses the <code>commerceItem</code> to get sku repository item.
   * If both skuId and commerce item values are empty, then this method throws a <code>CommerceException</code>.
   *
   * @param pSkuId -- The sku to be looked at
   * @param pCatalogKey -- The catalog in which the sku is available
   * @param pCommerceItem -- In case if you have access to the commerce item instead of the sku and catalog id, you can pass in this parameter.
   * @return the fulfiller value -- Either returns the default value or sku's fulfiller property value
   * @throws CommerceException -- If there is any exception while finding the sku repository item or sku is not found, then this exception is thrown 
   */
  public String getFulfiller (String pSkuId, String pCatalogKey, CommerceItem pCommerceItem) 
  throws CommerceException {
    RepositoryItem sku = null;
    String fulfiller = null;
    try {
      if (!StringUtils.isEmpty(pSkuId)) {
        sku = (RepositoryItem) getOrderManager().getCommerceItemManager().getSku(pSkuId, pCatalogKey);
      } else if (pCommerceItem != null) {
        sku = (RepositoryItem) getCatalogRef(pCommerceItem);
      }
    } catch (RepositoryException e) {
      throw new CommerceException(e);
    }

    if (sku != null) {
      fulfiller = (String) sku.getPropertyValue(Constants.FULFILLER_PROPERTY_NAME);
      //By default we do not set the fulfiller property. We are setting fulfiller property only for special skus which requires other than the
      //default fulfiller. If there is no fulfiller found, serve the default fulfiller. 
      if (!StringUtils.isEmpty(fulfiller)) {
        return fulfiller;
      }
    } else {
      //if the sku is null, then serve the commerce exception.
      throw new CommerceException (ResourceUtils.getMsgResource("skuNotFound",MY_RESOURCE_NAME,sResourceBundle));
    }
    return getDefaultFulfiller() ;
  }
} // end of class
