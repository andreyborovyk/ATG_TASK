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

package atg.projects.b2bstore.order;

// Commerce
import atg.commerce.order.*;
import atg.commerce.fulfillment.*;
import atg.commerce.*;
import atg.commerce.states.*;
import atg.commerce.messaging.*;

// Java classes
import javax.jms.*;
import javax.transaction.*;
import javax.transaction.xa.*;
import java.util.ResourceBundle.*;
import java.util.*;

/**
 * This class will contain methods needed to cancel an order by sending messages to the fulfillment
 * subsystem.
 *
 * @author Cynthia Harris
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/order/OrderCanceller.java#2 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @deprecated Use the atg.commerce.order.CancelOrderService instead
 * 
 */
public class OrderCanceller extends SourceSinkTemplate
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/order/OrderCanceller.java#2 $$Change: 651448 $";

  //---------------------------------------------------------------------------
  // property:MessageSourceName
  //---------------------------------------------------------------------------

  /** the name used by this class when it acts as a message source **/
  private String mMessageSourceName = "OrderCanceller";

  //---------------------------------------------------------------------------
  /**
   * Sets the name used by this class when it acts as a message source 
   * so that it's messages can be identified.
   **/
  public void setMessageSourceName(String pMessageSourceName) {
    mMessageSourceName = pMessageSourceName;
  }

  //---------------------------------------------------------------------------
  /**
   * Gets the name used by this class when it acts as a message source 
   * so that it's messages can be identified.
   **/
  public String getMessageSourceName() {
    return mMessageSourceName;
  }

  /** Port name for sending modify order messages */
  String mModifyPort = null;

  //-------------------------------------
  /**
   * Sets Port name for sending modify order messages
   **/
  public void setModifyPort(String pModifyPort) {
    mModifyPort = pModifyPort;
  }

  //-------------------------------------
  /**
   * Returns Port name for sending modify order messages
   **/
  public String getModifyPort() {
    return mModifyPort;
  }

  //-------------------------------------
  /**
   * Assemble and send a message to cancel the order
   * @param orderId the id of the order to cancel.
   **/
  public void sendCancelOrder(String pOrderId) {

    Modification[] mods = new Modification[1];
    ModifyOrder message = new ModifyOrder();
    
    message.setOrderId(pOrderId);
    message.setSource(getMessageSourceName());
    message.setOriginalSource(getMessageSourceName());
    message.setOriginalId(message.getId());        
    
    GenericRemove gr = new GenericRemove();
    gr.setTargetType(Modification.TARGET_ORDER);
    gr.setTargetId(pOrderId);
    mods[0] = gr;
    
    message.setModifications(mods);

    try {
      sendCommerceMessage(message, getModifyPort());
    } catch(JMSException j) {
      logError(j);
    }    
  }

} // end of class
