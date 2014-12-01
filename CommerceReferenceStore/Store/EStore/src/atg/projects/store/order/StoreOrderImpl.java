/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2011 Art Technology Group, Inc.
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
package atg.projects.store.order;

import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.RepositoryContactInfo;
import atg.commerce.order.ShippingGroup;
import atg.core.util.Address;

import atg.repository.RemovedItemException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Extension to order class.
 * @author ATG
 * @version $Revision: #2 $
 */
public class StoreOrderImpl extends OrderImpl {
  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/StoreOrderImpl.java#2 $$Change: 651448 $";

  // WARNING: These are also in the /atg/commerce/order/OrderTools.properties
  /** Gift message to key name. */
  public static final String GIFT_MESSAGE_TO_KEY = "giftMessageTo";

  /** Gift message key name. */
  public static final String GIFT_MESSAGE_KEY = "giftMessage";

  /** Gift message from key name. */
  public static final String GIFT_MESSAGE_FROM_KEY = "giftMessageFrom";

  /**
   * Sample commerce item type.
   */
  public static final String SAMPLE_COMMERCE_ITEM_TYPE = "sampleCommerceItem";

  private transient volatile boolean mShouldAddGiftNote;

  public boolean isShouldAddGiftNote() {
    return mShouldAddGiftNote;
  }

  public void setShouldAddGiftNote(boolean pShouldAddGiftNote) {
    mShouldAddGiftNote = pShouldAddGiftNote;
  }

  /**
   * @return the code for the coupon redeemed for this order.
   */
  public String getCouponCode() {
    return (String) getPropertyValue(StorePropertyNameConstants.COUPONCODE);
  }

  /**
   * Sets the code for the coupon redeemed for this order.
   * @param pCouponCode - the code for the coupon redeemed for this order
   */
  public void setCouponCode(String pCouponCode) {
    setPropertyValue(StorePropertyNameConstants.COUPONCODE, pCouponCode);
  }

  /**
   * @return the SAP order id.
   */
  public String getOmsOrderId() {
    return (String) getPropertyValue(StorePropertyNameConstants.OMSORDERID);
  }

  /**
   * Sets the SAP order id.
   * @param pOmsId - the SAP order id
   */
  public void setOmsOrderId(String pOmsId) {
    setPropertyValue(StorePropertyNameConstants.OMSORDERID, pOmsId);
  }

  /**
   * @return list of OMS segments.
   */
  public List getOmsSegments() {
    return (List) getPropertyValue(StorePropertyNameConstants.OMSSEGMENTS);
  }

  /**
    * Sets the shipment tracking information objects for the order.
    * @param pTrackingInfos - the shipment tracking information objects for the order
    */
  public void setTrackingInfos(List pTrackingInfos) {
    setPropertyValue(StorePropertyNameConstants.TRACKINGINFOS, pTrackingInfos);
  }

  /**
   * Gets the shipment tracking information objects for the order.
   * @return the shipment tracking information objects for the order
   */
  public List getTrackingInfos() {
    return (List) getPropertyValue(StorePropertyNameConstants.TRACKINGINFOS);
  }

  /**
   * Sets the OMS segments.
   * @param pOmsSegment - list of OMS segments
   */
  public void addOmsSegment(String pOmsSegment) {
    List segments = getOmsSegments();

    if (segments == null) {
      segments = new ArrayList();
    }

    segments.add(pOmsSegment);
    setChanged(true);
  }

  /**
   * Removes all OMS segments from order.
   */
  public void removeAllOmsSegments() {
    List omsSegments = getOmsSegments();

    if ((omsSegments != null) && (omsSegments.size() > 0)) {
      setPropertyValue(StorePropertyNameConstants.OMSSEGMENTS, new LinkedList());
      setChanged(true);
    }
  }
  
  /**
   * Checks to see if this order is gift wrapped or not.
   * @return true if gift wrapped, false - otherwise
   */
  public boolean getContainsGiftWrap() {
    List items = getCommerceItems();
    int itemCount = getCommerceItemCount();

    for (int i = 0; i < itemCount; i++) {
      if (items.get(i) instanceof GiftWrapCommerceItem) {
        return true;
      }
    }

    return false;
  }

  /**
   * Checks to see if this order has a gift message special instruction.
   * @return true if order contains gift message, false - otherwise
   */
  public boolean getContainsGiftMessage() {
    Map specialInstructions = getSpecialInstructions();

    return (specialInstructions != null) && specialInstructions.containsKey(GIFT_MESSAGE_TO_KEY);
  }

  /**
   * Sets the gift message.
   * @param pMessageTo the message to field
   * @param pMessage the message field
   * @param pMessageFrom the message from field
   */
  public void setGiftMessage(String pMessageTo, String pMessage, String pMessageFrom) {
    Map specialInstructions = getSpecialInstructions();

    specialInstructions.put(GIFT_MESSAGE_TO_KEY, pMessageTo);
    specialInstructions.put(GIFT_MESSAGE_KEY, pMessage);
    specialInstructions.put(GIFT_MESSAGE_FROM_KEY, pMessageFrom);
  }

  /**
   * Determines if user has entered gift message.
   * @return true if gift message was populated, false - otherwise
   */
  public boolean getGiftMessagePopulated() {
    if (!getContainsGiftMessage()) {
      return false;
    }

    String giftMessageTo = (String) getSpecialInstructions().get(GIFT_MESSAGE_TO_KEY);
    String giftMessageFrom = (String) getSpecialInstructions().get(GIFT_MESSAGE_FROM_KEY);
    String giftMessage = (String) getSpecialInstructions().get(GIFT_MESSAGE_KEY);

    if ((giftMessageTo == null) || (giftMessageTo.trim().length() == 0)) {
      return false;
    }

    if ((giftMessageFrom == null) || (giftMessageFrom.trim().length() == 0)) {
      return false;
    }

    if ((giftMessage == null) || (giftMessage.trim().length() == 0)) {
      return false;
    }

    return true;
  }
  
  /**
   * Removes an Address associated with this orders shipping group
   * 
   * @param pShippingGroupId The shipping groups id from which to remove the
   * address
   * @return A boolean indicating success or failure
   */
  public boolean removeAddress(String pShippingGroupId){
    
    ShippingGroup orderShippingGroup =  null;
    try{
      orderShippingGroup = getShippingGroup(pShippingGroupId);
    }
    catch(Exception e){return false;}
    
    // If the shipping group is found, set its Address to an empty address
    if(orderShippingGroup != null){
      if(orderShippingGroup instanceof HardgoodShippingGroup){
        Address emptyAddress = new Address();
        ((HardgoodShippingGroup)orderShippingGroup).setShippingAddress(emptyAddress);
        return true;
      }
    }
    
    return false;
  }

  /**
   * Method that renders the general order information in a readable string format.
   * @return string represantation of the class
   */
  public String toString() {
    StringBuffer sb = new StringBuffer("Order[");

    try {
      sb.append("type:").append(getOrderClassType()).append("; ");
      sb.append("id:").append(getId()).append("; ");
      sb.append("state:").append(getStateAsString()).append("; ");
      sb.append("transient:").append(isTransient()).append("; ");
      sb.append("profileId:").append(getProfileId()).append("; ");
    } catch (RemovedItemException exc) {
      sb.append("removed");
    }

    sb.append("]");

    return sb.toString();
  }
}
