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
import atg.servlet.DynamoServlet;
import atg.droplet.DropletBeanInfo;
import atg.droplet.ParamDescriptor;
import java.beans.BeanDescriptor;
import java.util.Map;
import atg.commerce.order.Order;

/**
 * This BeanInfo describes the <tt>PaymentGroupDroplet</tt>.<p>
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/PaymentGroupDropletBeanInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class PaymentGroupDropletBeanInfo extends DropletBeanInfo
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/PaymentGroupDropletBeanInfo.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  public final static String FUNCTIONAL_COMPONENT_CATEGORY = "Servlet Beans";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  private final static ParamDescriptor [] sOutputDescriptors = {
    new ParamDescriptor("paymentGroups", "The Map of the user's named PaymentGroups.", Map.class, true, false),
    new ParamDescriptor("paymentInfos", "The Map of the user's CommerceIdentifierPaymentInfo Lists keyed by CommerceIdentifier id.", Map.class, true, false),
    new ParamDescriptor("order", "The user's current Order.", Order.class, true, false)
      };

  private final static ParamDescriptor [] sParamDescriptors = {
    new ParamDescriptor("output", "Output parameter which contains the paymentGroups, cipiMap, and order parameters.", DynamoServlet.class, true, true, sOutputDescriptors),
    new ParamDescriptor("order", "Provides the user's current Order.", Order.class, true, false),
    new ParamDescriptor("clearPaymentGroups", "Should the user's Map of PaymentGroups be cleared?", Boolean.class, true, false),
    new ParamDescriptor("clearPaymentInfos", "Should the user's Map of CommerceIdentifierPaymentInfos be cleared?", Boolean.class, true, false),
    new ParamDescriptor("clear", "Should both be cleared?", Boolean.class, true, false),
    new ParamDescriptor("paymentGroupTypes", "Comma separated list of PaymentGroup types to initialize.", String.class, true, false),
    new ParamDescriptor("initPaymentGroups", "Should PaymentGroups be initialized for the current user?", Boolean.class, true, false),
    new ParamDescriptor("initItemPayment", "Should CommerceItemPaymentInfos be created for the user's current Order?", Boolean.class, true, false),
    new ParamDescriptor("initShippingPayment", "Should ShippingGroupPaymentInfos be created for the user's current Order?", Boolean.class, true, false),
    new ParamDescriptor("initOrderPayment", "Should an OrderPaymentInfo be created for the user's current Order?", Boolean.class, true, false),
    new ParamDescriptor("initTaxPayment", "Should a TaxPaymentInfo be created for the user's current Order?", Boolean.class, true, false)
  };

  private final static BeanDescriptor sBeanDescriptor =
    createBeanDescriptor(PaymentGroupDroplet.class,
                         null,
                         "This Dynamo Servlet Bean is used to initialize the user's PaymentGroups and CommerceIdentifierPaymentInfos.",
                         sParamDescriptors);

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  /**
   * Returns the BeanDescriptor for this bean, which will in turn 
   * contain ParamDescriptors for the droplet.
   **/
  public BeanDescriptor getBeanDescriptor() {
    return sBeanDescriptor;
  }
}   // end of class
