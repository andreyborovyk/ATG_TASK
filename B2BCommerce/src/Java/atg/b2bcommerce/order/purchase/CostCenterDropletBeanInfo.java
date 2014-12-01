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

package atg.b2bcommerce.order.purchase;
import atg.servlet.DynamoServlet;
import atg.droplet.DropletBeanInfo;
import atg.droplet.ParamDescriptor;
import java.beans.BeanDescriptor;
import atg.commerce.order.Order;
import java.util.*;

/**
 * This BeanInfo describes the <tt>CostCenterDroplet</tt>.<p>
 *
 * @author Ernesto Mireles
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/purchase/CostCenterDropletBeanInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class CostCenterDropletBeanInfo extends DropletBeanInfo
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/purchase/CostCenterDropletBeanInfo.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  public final static String FUNCTIONAL_COMPONENT_CATEGORY = "Servlet Beans";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  private final static ParamDescriptor [] sOutputDescriptors = {
    new ParamDescriptor("costCenters", "The Map of the user's named CostCenters.", Map.class, true, false),
    new ParamDescriptor("ciccMap", "The Map of the user's CommerceIdentifierCostCenter Lists keyed by CommerceIdentifier id.", Map.class, true, false),
    new ParamDescriptor("order", "The user's current Order.", Order.class, true, false)
      };

  private final static ParamDescriptor [] sParamDescriptors = {
    new ParamDescriptor("output", "Output parameter which contains the costCenters, ciccMap, and order parameters.", DynamoServlet.class, true, true, sOutputDescriptors),
    new ParamDescriptor("order", "Provides the user's current Order.", Order.class, true, false),
    new ParamDescriptor("clearCostCenterMap", "Should the user's Map of CostCenters be cleared?", Boolean.class, true, false),
    new ParamDescriptor("clearCostCenterContainer", "Should the user's Map of CommerceIdentifierCostCenters be cleared?", Boolean.class, true, false),
    new ParamDescriptor("clear", "Should both containers be cleared?", Boolean.class, true, false),
    new ParamDescriptor("initCostCenters", "Should the user's CostCenters be initialized?", Boolean.class, true, false),
    new ParamDescriptor("initItemCostCenters", "Should CommerceIdentifierCostCenters be created for the Order's items?", Boolean.class, true, false),
    new ParamDescriptor("initShippingCostCenters", "Should CommerceIdentifierCostCenters be created for the Order's ShippingGroups?", Boolean.class, true, false),
    new ParamDescriptor("useAmount", "Should CostCenterCommerceItem relationships be based on the amount type?", Boolean.class, true, false),
    new ParamDescriptor("initTaxCostCenters", "Should CommerceIdentifierCostCenters be created for the Order's tax?", Boolean.class, true, false),
    new ParamDescriptor("initOrderCostCenters", "Should a CommerceIdentifierCostCenter be created for the entire Order?", Boolean.class, true, false)
  };

  private final static BeanDescriptor sBeanDescriptor =
    createBeanDescriptor(CostCenterDroplet.class,
                         null,
                         "This Dynamo Servlet Bean is used to initialize the user's CostCenters and CommerceIdentifierCostCenters.",
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
