/*<ATGCOPYRIGHT>
* Copyright (C) 200-2011 Art Technology Group, Inc.
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

import atg.servlet.*;
import atg.nucleus.ServiceException;
import atg.commerce.order.*;
import atg.nucleus.naming.ParameterName;
import atg.core.util.NumberTable;
import atg.core.util.ResourceUtils;

import java.io.*;
import javax.servlet.ServletException;
import java.util.List;
import java.util.Iterator;

/**This servlet takes Shipping Group as an <i>input</i> parameter and 
 * returns the total price of all the commerce items in the Shipping
 * Group according to the quantity.
 *
 *<dt><code>subtotal</code>
 *<dd>This parameter is set to the subtotal of all commerce items in
 * the Shipping Group
 *
 *<dt><code>output</code>
 *<dd>This parameter is rendered once
 *
 * <P>
 * An example of Using ShippinGroupSubtotal to get
 * subtotal of shipping group follows:
 * <pre>
 * &lt;DROPLET BEAN="ShippinGroupSubtotal" &gt;
 * &lt;PARAM NAME="sg" VALUE="param;SGroup" &gt;
 * &lt;OPARAM NAME="output" &gt;
 * &lt;VALUEOF PARAM="subtotal" CURRENCY/&gt;
 * &lt;/OPARAM&gt;
 * &lt;/DROPLET&gt;
 * </pre>
 *
 * @author Manoj Potturu <mpotturu@atg.com>
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/order/ShippingGroupSubtotal.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
 
public class 
  ShippingGroupSubtotal extends DynamoServlet { 

  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/order/ShippingGroupSubtotal.java#2 $$Change: 651448 $";

  //--------------------------------------
  // Constants

  public static final ParameterName SG = ParameterName.getParameterName("sg");
  public final static String SUBTOTAL = "subtotal";
  public final static String OUTPUT = "output";

  public final static String MISSING_SHIPPING_GROUP = "missingShippinGroup";
  public final static String MY_RESOURCE_NAME = "atg.projects.b2bstore.order.DropletResources";
  
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, java.util.Locale.getDefault());

  public void  service(DynamoHttpServletRequest pRequest,
                       DynamoHttpServletResponse pResponse)
    throws IOException, ServletException{
    
    ShippingGroup sg = (ShippingGroup) pRequest.getObjectParameter(SG);
    
    if (sg == null) {
      if (isLoggingError())
        logError(ResourceUtils.getMsgResource(MISSING_SHIPPING_GROUP, MY_RESOURCE_NAME, sResourceBundle));
    }

    List ciRelationshipList = null;
    double subTotal = 0;
    
    ciRelationshipList  = sg.getCommerceItemRelationships();

    if ((ciRelationshipList != null) && (ciRelationshipList.size() != 0)) {
      Iterator ciRelationships = ciRelationshipList.iterator();
      ShippingGroupCommerceItemRelationship sgci = null;
    
      while (ciRelationships.hasNext()) {
        sgci = (ShippingGroupCommerceItemRelationship) ciRelationships.next();
        subTotal += sgci.getAmountByAverage();
      } // end of while ()
    
    } // end of  if-else

    pRequest.setParameter(SUBTOTAL, NumberTable.getDouble(subTotal));
    pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);

  }

}












