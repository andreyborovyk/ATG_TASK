/*<ATGCOPYRIGHT>
 * Copyright (C) 1999-2011 Art Technology Group, Inc.
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

import atg.commerce.fulfillment.scenario.ScenarioEvent;
import atg.commerce.messaging.CommerceMessage;
import atg.commerce.order.*;
import atg.commerce.promotion.ScenarioAddedItemToOrder;

import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;

import atg.repository.RepositoryItem;

import java.util.HashMap;
import java.io.Serializable;

/**
 * This processor sends scenario action events to the scenario server. The events
 * are those such as ItemAddedToOrder and ItemRemovedFromOrder.
 *
 * @author Sam Perman
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSendScenarioEvent.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.commerce.order.processor.EventSender
 * @see atg.commerce.order.ItemAddedToOrder
 * @see atg.commerce.order.ItemRemovedFromOrder
 */
public class ProcSendScenarioEvent extends EventSender {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSendScenarioEvent.java#2 $$Change: 651448 $";

  //-----------------------------------------------
  public ProcSendScenarioEvent() {
  }

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcSendScenarioEvent";

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
   * This method creates and populates various Commerce event objects that
   * represent messages which will be sent to the scenario engine.
   * The objects created here are:
   * <ul>
   * <li>ItemAddedToOrder</li>
   * <li>ItemRemovedFromOrder</li>
   * <li>ItemQuantityChanged</li>
   * <li>ScenarioAddedItemToOrder</li>
   * <li>OrderSaved</li>
   * <li>OrdersMerged</li>
   * </ul>
   * If the event is an ItemQuantityChanged event, then the quantity that is in
   * pParams is the old quantity of the item, prior to the change.
   * <p>
   * Unknown event types are passed along to <code>handleOtherEventToSend</code>.
   *
   * @param pParam the Map which contains all the message parameters
   * @param pResult the PipelineResult object which is supplied to runProcess()
   * @return the Serializable message object
   */
  public Serializable createEventToSend(Object pParam, PipelineResult pResult) throws Exception
  {
    // We don't need to check anything here, we can just send the message on.
    
    HashMap map = (HashMap) pParam;
    
    Order order = (Order) map.get(PipelineConstants.ORDER);
    CommerceItem item = (CommerceItem) map.get(PipelineConstants.COMMERCEITEM);
    String event = (String) map.get(PipelineConstants.EVENT);        
    Long quantity = (Long) map.get(PipelineConstants.QUANTITY);
    Object catalogRefObject = null, productObject = null;
    if ( item != null) {
      catalogRefObject = item.getAuxiliaryData().getCatalogRef();      
      productObject = item.getAuxiliaryData().getProductRef();      
    } // end of if ()
    
    // Fill in whatever is necessary for each event type
    if(event.equals(ItemAddedToOrder.TYPE)) {
      ItemAddedToOrder iato = new ItemAddedToOrder();
      iato.setOrder(order);
      iato.setId(getNextMessageId());
      iato.setCommerceItem(item);
      iato.setQuantity(quantity.longValue());
      iato.setCatalogRef(catalogRefObject);
      iato.setProduct(productObject);
      iato.setProfileId(order.getProfileId());
      iato.setSiteId(getSiteId(pParam));      
      setEventType(iato.getType());

      //System.out.println(iato.toString());
      return iato;
    }
    else if(event.equals(ItemRemovedFromOrder.TYPE)) {
      ItemRemovedFromOrder irfo = new ItemRemovedFromOrder();
      irfo.setOrder(order);
      irfo.setId(getNextMessageId());
      irfo.setCommerceItem(item);
      irfo.setQuantity(quantity.longValue());
      irfo.setCatalogRef(catalogRefObject);
      irfo.setProduct(productObject);
      irfo.setProfileId(order.getProfileId());
      irfo.setSiteId(getSiteId(pParam));
      setEventType(irfo.getType());
      //System.out.println(irfo.toString());
      return irfo;      
    } 
    else if(event.equals(ScenarioAddedItemToOrder.TYPE)) {
      ScenarioAddedItemToOrder saito = new ScenarioAddedItemToOrder();
      saito.setOrder(order);
      saito.setId(getNextMessageId());
      saito.setCommerceItem(item);
      saito.setQuantity(quantity.longValue());
      saito.setCatalogRef(catalogRefObject);
      saito.setProduct(productObject);
      saito.setProfileId(order.getProfileId());
      saito.setSiteId(getSiteId(pParam));
      setEventType(saito.getType());

      //System.out.println(saito.toString());
      return saito;      
    } 
    else if(event.equals(ItemQuantityChanged.TYPE)) {
      ItemQuantityChanged iqc = new ItemQuantityChanged();
      iqc.setOrder(order);
      iqc.setId(getNextMessageId());
      iqc.setCommerceItem(item);
      iqc.setOldQuantity(quantity.longValue());
      iqc.setNewQuantity(item.getQuantity());
      iqc.setCatalogRef(catalogRefObject);
      iqc.setProduct(productObject);
      iqc.setProfileId(order.getProfileId());
      iqc.setSiteId(getSiteId(pParam));
      setEventType(iqc.getType());

      //System.out.println(iqc.toString());
      return iqc;      
    }
    else if(event.equals(OrderSaved.TYPE)) {
      OrderSaved os = new OrderSaved();
      os.setOrder(order);
      os.setId(getNextMessageId());
      os.setSiteId(getSiteId(pParam));
      os.setUserId(order.getProfileId());
      setEventType(os.getType());
      return os;
    }
    else if ( event.equals(ItemSkuChanged.TYPE) ) {
      ItemSkuChanged isc = new ItemSkuChanged();
      isc.setOrder( order );
      isc.setId( getNextMessageId() );
      isc.setCommerceItem( item );
      isc.setCatalogRef( catalogRefObject );
      isc.setProduct( productObject );
      isc.setProfileId( order.getProfileId() );
      isc.setOldSku( (String) map.get(PipelineConstants.SKU) );
      isc.setSiteId(getSiteId(pParam));
      setEventType( isc.getType() );
      return isc;
    }
    else if(event.equals(OrdersMerged.TYPE)) {
      Order source = (Order) map.get(PipelineConstants.SOURCEORDER);
      OrdersMerged om = new OrdersMerged();
      om.setId(getNextMessageId());
      om.setDestinationOrder(order);
      om.setSourceOrder(source);
      Boolean sourceRemoved = (Boolean)map.get(PipelineConstants.SOURCEREMOVED);
      if (sourceRemoved != null) {
        om.setSourceRemoved(sourceRemoved.booleanValue());
      }
      om.setProfileId(order.getProfileId());
      om.setSiteId(getSiteId(pParam));
      setEventType(om.getType());
      return om;
    }
    else {
      return handleOtherEventToSend(pParam,pResult);
    }    
  }
  
  /**
   * If this object is a ScenarioEvent or CommerceMessage, then that message's type is 
   * returned.  Otherwise <code>super.getEventType()</code> is returned.
   * 
   * @param pEventToSend The event that will be sent whose <code>type</code> will be sent
   * @return The type of the event
   */
  public String getEventType(Object pEventToSend)
  {
    if(pEventToSend instanceof ScenarioEvent) {
      ScenarioEvent event = (ScenarioEvent) pEventToSend;
      return event.getType();
    }
    if(pEventToSend instanceof CommerceMessage) {
      CommerceMessage message = (CommerceMessage) pEventToSend;
      return message.getType();
    }
    return mEventType;
  }

  //---------------------------------------------------------------------------
  // property: MessageSourceName
  //---------------------------------------------------------------------------
  private String mMessageSourceName;

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

  /**
   * This method is used to deal with other event type. User should override
   * this method if they want to deal with other event type.
   *
   * @param pParam the Map which contains all the message parameters
   * @param pResult the PipelineResult object which is supplied to runProcess()
   * @return the Serializable message object
   */
  protected Serializable handleOtherEventToSend(Object pParam, PipelineResult pResult) throws Exception
  {
    return null;
  }
  
}
