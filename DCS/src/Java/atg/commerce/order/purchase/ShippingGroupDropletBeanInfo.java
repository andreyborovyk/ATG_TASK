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
 * This BeanInfo describes the <tt>ShippingGroupDroplet</tt>.<p>
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/ShippingGroupDropletBeanInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ShippingGroupDropletBeanInfo extends DropletBeanInfo
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/ShippingGroupDropletBeanInfo.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  public final static String FUNCTIONAL_COMPONENT_CATEGORY = "Servlet Beans";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  private final static ParamDescriptor [] sOutputDescriptors = {
    new ParamDescriptor("shippingGroups", "The Map of the user's named ShippingGroups.", Map.class, true, false),
    new ParamDescriptor("shippingInfos", "The Map of the user's CommerceItemShippingInfo Lists keyed by CommerceIdentifier id.", Map.class, true, false),
    new ParamDescriptor("order", "The user's current Order.", Order.class, true, false)
      };

  private final static ParamDescriptor [] sParamDescriptors = {
    new ParamDescriptor("output", "Output parameter which contains the shippingGroups, cisiMap, and order parameters.", DynamoServlet.class, true, true, sOutputDescriptors),
    new ParamDescriptor("order", "Provides the user's current Order.", Order.class, true, false),
    new ParamDescriptor("clearShippingGroups", "Should the user's Map of ShippingGroups be cleared?", Boolean.class, true, false),
    new ParamDescriptor("clearShippingInfos", "Should the user's Map of CommerceItemShippingInfos be cleared?", Boolean.class, true, false),
    new ParamDescriptor("clear", "Should both Maps be cleared?", Boolean.class, true, false),
    new ParamDescriptor("shippingGroupTypes", "Comma separated list of ShippingGroup types to initialize.", String.class, true, false),
    new ParamDescriptor("initShippingGroups", "Should ShippingGroups be initialized for the current user?", Boolean.class, true, false),
    new ParamDescriptor("initShippingInfos", "Should the CommerceItemShippingInfos be created for the user's current Order?", Boolean.class, true, false),
    new ParamDescriptor("createOneInfoPerUnit", "Determines if one CommerceItemShippingInfo is created for each unit in each commerce item", Boolean.class, true, false),
  };

  private final static BeanDescriptor sBeanDescriptor =
    createBeanDescriptor(ShippingGroupDroplet.class,
                         null,
                         "This Dynamo Servlet Bean is used to initialize the user's ShippingGroups and CommerceItemShippingInfos.",
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
