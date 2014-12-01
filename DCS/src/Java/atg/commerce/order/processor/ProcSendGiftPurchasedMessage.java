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

package atg.commerce.order.processor;

import atg.commerce.order.*;
import atg.commerce.profile.*;
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.gifts.GiftPurchased;
import atg.commerce.gifts.GiftlistHandlingInstruction;
import atg.repository.RepositoryItem;
import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;
import atg.userprofiling.Profile;

import java.util.*;
import java.io.Serializable;

/**
 * This processor sends an event to the scenario engine when a gift has been purchased.
 * A gift is "purchased" when an order is processed.  The event object sent is GiftPurchased.
 *
 * @author Kerwin D. Moy
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSendGiftPurchasedMessage.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.commerce.gifts.GiftPurchased
 * @see atg.commerce.order.processor.EventSender
 */
public class ProcSendGiftPurchasedMessage extends EventSender
{
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSendGiftPurchasedMessage.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  //-----------------------------------------------
  public ProcSendGiftPurchasedMessage() {
  }

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcSendGiftPurchasedMessage";

  /**
   * Sets property LoggingIdentifier
   **/
  public void setLoggingIdentifier(String pLoggingIdentifier) {
    mLoggingIdentifier = pLoggingIdentifier;
  }

  /**
   * Returns property LoggingIdentifier
   **/
  public String getLoggingIdentifier() {
    return mLoggingIdentifier;
  }

  //-----------------------------------------------
  /**
   * This method will loop through all the items in the order calling @see #createEventToSend
   * which will return the event object to be sent.  @see #sendObjectMessage will be
   * called with the event to be sent, along with the @see #getEventType and the
   * @see #getPortName.
   *
   * This method requires that an Order and Profile object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order and Profile object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);

    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));

    List shippingGroupList = order.getShippingGroups();
    GiftlistManager mgr = getGiftlistManager();
    CommerceProfileTools ptools = getProfileTools();
    ShippingGroup sg = null;
    List handlingList = null;
    Serializable eventToSend;
    
    if(shippingGroupList == null)
      return SUCCESS;

    Iterator shippingGroups = shippingGroupList.iterator();
    while(shippingGroups.hasNext()) {
      sg = (ShippingGroup) shippingGroups.next();
      handlingList = sg.getHandlingInstructions();
      if((handlingList == null) || (handlingList.size() == 0)) {
        continue;
      }
      else {
        Iterator handlings = handlingList.iterator();
        HandlingInstruction hi = null;
        while(handlings.hasNext()) {
          hi = (HandlingInstruction) handlings.next();
          if(hi instanceof GiftlistHandlingInstruction) {
            // we only care about gifts
            GiftlistHandlingInstruction ghi = (GiftlistHandlingInstruction) hi;
            String recipientId = mgr.getGiftlistOwner(ghi.getGiftlistId());
            CommerceItem ci = order.getCommerceItem(ghi.getCommerceItemId());
            Profile profile = new Profile();
            if (!(ptools.locateUserFromId(recipientId, profile))){
              // <TBD> error?
              continue;
	    }
            if(ci == null) {
              // <TBD> error?
              continue;
            }
            eventToSend = createEventToSend(order, ci, profile, pParam);            
            sendObjectMessage(eventToSend, getEventType(), getPortName());
          }
          else {

          }
        }
      }
      
    }
    return SUCCESS;
  }

  //-----------------------------------------------
  /**
   * This method creates and populates a GiftPurchased object. The 
   * GiftPurchased object is the message which will be sent to the 
   * scenario engine.
   *
   * @param order the order to set in the message
   * @param pItem the commerce item to set in the message
   * @param profile the profile to set in the message
   * @return the Serializable message object
   */
  public Serializable createEventToSend(Order pOrder, CommerceItem pItem,
              RepositoryItem pProfile)
  { 
    return createEventToSend(pOrder, pItem, pProfile, null);
  }
  
  /**
   * This method creates and populates a GiftPurchased object. The 
   * GiftPurchased object is the message which will be sent to the 
   * scenario engine.
   *
   * @param pOrder the order to set in the message
   * @param pItem the commerce item to set in the message
   * @param pProfile the profile to set in the message
   * @param pParam pipeline parameters map to extract site id property
   * @return the Serializable message object
   */
  public Serializable createEventToSend(Order pOrder, CommerceItem pItem,
                                        RepositoryItem pProfile, Object pParam)
  {
    GiftPurchased message = new GiftPurchased();
    message.setOrder(pOrder);
    message.setProfile(pProfile);
    message.setItem(pItem);
    message.setSource(getMessageSourceName());
    message.setId(getNextMessageId());
    message.setOriginalSource(getMessageSourceName());
    message.setOriginalId(message.getId());
    if (pParam != null){
      message.setSiteId(getSiteId(pParam));
    }
    return message;
  }
  
  //---------------------------------------------------------------------------
  // property:  giftlistManager
  //---------------------------------------------------------------------------
  private GiftlistManager mGiftlistManager = null;

  public void setGiftlistManager(GiftlistManager pGiftlistManager) {
    mGiftlistManager = pGiftlistManager;
  }

  /**
   * All Giftlist manipulations are done with this.
   **/
  public GiftlistManager getGiftlistManager() {
    return mGiftlistManager;
  }

  //---------------------------------------------------------------------------
  // property:  profileTools
  //---------------------------------------------------------------------------
  private CommerceProfileTools mProfileTools = null;

  public void setProfileTools(CommerceProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }

  /**
   * property profileTools
   **/
  public CommerceProfileTools getProfileTools() {
    return mProfileTools;
  }

  //---------------------------------------------------------------------------
  // property: MessageSourceName
  //---------------------------------------------------------------------------
  private String mMessageSourceName = null;

  public void setMessageSourceName(String pMessageSourceName) {
    mMessageSourceName = pMessageSourceName;
  }

  /**
   * This defines the string that the source property of messages will
   * be set to.
   *
   * @beaninfo
   *          description: This is the name of this message source.  All outgoing messages
   *                       will use this as the source.
   **/
  public String getMessageSourceName() {
    return mMessageSourceName;
  }
}
