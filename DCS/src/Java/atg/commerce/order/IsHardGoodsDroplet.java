/*<ATGCOPYRIGHT>
 * Copyright (C) 2000-2011 Art Technology Group, Inc.
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

package atg.commerce.order;

import atg.servlet.DynamoServlet;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.Order;

import atg.core.util.ResourceUtils;
import atg.nucleus.naming.ParameterName;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;

/**
 * Take as an object parameter an Order.  Then, determine if the order
 * contains any items that will be shipped via Hardgood shipping groups.
 * If there are, then the true oparam will be rendered, else false
 * will be rendered.
 *
 * For Example:
 * <blockquote>
 * <code>
 * &lt;droplet bean="/atg/dynamo/droplet/IsHardsGoodsDroplet"&gt;
 * &lt;param name="order" value="param:someorder"&gt;
 * &lt;oparam name="true"&gt;Order contains items in a Hardgood shipping group&lt;/oparam&gt;
 * &lt;oparam name="false"&gt;No items in a Hardgood shipping group&lt;/oparam&gt;
 * &lt;/droplet&gt;
 * </code>
 * </blockquote>
 *
 *
 * @author Ashley J. Streb
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/IsHardGoodsDroplet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class IsHardGoodsDroplet
  extends DynamoServlet
{

  //-------------------------------------
  // Class version string
  //-------------------------------------
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/IsHardGoodsDroplet.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------
  
  public static final ParameterName ORDER = ParameterName.getParameterName("order");
  public static final ParameterName TRUE = ParameterName.getParameterName("true");
  public static final ParameterName FALSE = ParameterName.getParameterName("false");
  
  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";
  
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Empty Constructor 
   */

  public IsHardGoodsDroplet() {
  }

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------
  
  public void service(DynamoHttpServletRequest pRequest,
                      DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
    
  {
    List shippingGroups;
    int size;
    try {
      Order order = (Order)pRequest.getObjectParameter(ORDER);
      if (order == null) {
        if (isLoggingError())
          logError(ResourceUtils.getMsgResource("orderObjectNull", MY_RESOURCE_NAME, 
                   sResourceBundle));
        return;
      }
      
      shippingGroups = order.getShippingGroups();
      for (int i=0; i<shippingGroups.size(); i++) {
        ShippingGroup sg = (ShippingGroup)shippingGroups.get(i);
        if (sg instanceof HardgoodShippingGroup) {
          if (sg.getCommerceItemRelationshipCount() > 0) {
            pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
            return;
          }
        }
      }
    } catch (ClassCastException cce) {
      if (isLoggingError())
        logError(ResourceUtils.getMsgResource("typeNotOrder", MY_RESOURCE_NAME, sResourceBundle));
    }
      
    pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
  }

} // end of class

