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
import atg.nucleus.naming.ParameterName;
import atg.commerce.order.*;
import atg.b2bcommerce.order.*;
import atg.core.util.ResourceUtils;

import java.io.*;
import javax.servlet.ServletException;
import java.util.List;
import java.util.Iterator;

/**
 * This servlet takes <code>order</code> and <code>type</code> parameter
 * and returns whether the <code>type</code> is split at the item level instead
 * of order level.
 * The parameters are:
 * <dl>
 * <dt> order
 * <dd> The order on which the type of split is to be determined.
 *
 * <dt> type
 * <dd> The possible arguments of this are
 * <P>
 *  -costCenter
 * <br>
 *  -paymentGroup.
 * <P>
 * For costCenter this servlet checks whether Cost Centers are split at item level,
 * <br>
 * For paymentGroup this servlet checks whether Paymnet Groups are split at item level.
 * </dl>
 * The use of the sevlet is shown in the following example:
 *
 * <P>
 * <PRE>
 * &lt;DROPLET BEAN="ItemsSplitByType"&gt;
 * &lt;PARAM NAME="order" VALUE="param:order"&gt;
 * &lt;PARAM NAME="type" VALUE="paymentGroup"&gt;
 * &lt;OPARAM NAME="true"&gt;
 *  .............
 * &lt;/OPARAM&gt;
 * &lt;OPARAM NAME="false"&gt;
 * ............
 * &lt;/OPARAM&gt;
 * &lt;/DROPLET&gt;
 * </PRE>
 * In the above case if the Payment Groups were split at Item level then the oparam of value<br> <i>true</i>
 * would have been rendered, other wise <i>false</i> would have been rendered.
 *
 *
 * @author Manoj Potturu <mpotturu@atg.com>
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/order/ItemsSplitbyType.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $

 **/
public class 
  ItemsSplitbyType extends DynamoServlet { 

  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/order/ItemsSplitbyType.java#2 $$Change: 651448 $";

  //--------------------------------------
  // Constants
  static final ParameterName ord = ParameterName.getParameterName("order");

  public final static String TRUE = "true";
  public final static String FALSE = "false";
  public final static String TYPE = "type";
  public final static String PAYMENTGROUP = "paymentGroup";
  public final static String COSTCENTER = "costCenter";

  public final static String MY_RESOURCE_NAME = "atg.projects.b2bstore.order.DropletResources";
  public final static String MISSING_ORDER = "missingOrder";
  public final static String MISSING_SPLITTYPE = "missingSplittype";
  
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, java.util.Locale.getDefault());

  public void  service(DynamoHttpServletRequest pRequest,
                       DynamoHttpServletResponse pResponse)
    throws IOException, ServletException{
    
    Order order = (Order) pRequest.getObjectParameter(ord);
    if (order == null) {
      if (isLoggingError())
        logError(ResourceUtils.getMsgResource(MISSING_ORDER, MY_RESOURCE_NAME, sResourceBundle));
    }

    String splitType = pRequest.getParameter(TYPE);
    if (splitType == null) {
      if (isLoggingError())
        logError(ResourceUtils.getMsgResource(MISSING_SPLITTYPE, MY_RESOURCE_NAME, sResourceBundle));
    }
    
    checkType(order, pRequest, pResponse);
  }

  /**
   * This method finds whether order is split at item-level for cost centers
   * or payment groups.
   *
   * @param pOrder is the Order which contains items.
   * @param pRequest
   * @param pResponse
   * @throws IOException
   * @throws ServletException
   *
   **/
  protected void checkType(Order pOrder, DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse)
    throws IOException, ServletException {
    List relationshipList = null;

    Class typeClass = null;
    boolean hasLineItems = false;

    // Get the split type parameter from the request.
    String splitType = pRequest.getParameter(TYPE);

    // Get all the relationships in the order
    relationshipList = pOrder.getRelationships();
    
    if ( relationshipList != null || relationshipList.size() != 0) {

      // Iterate through each relationship found in the order
      
      Iterator relationships = relationshipList.iterator();
      Relationship relationship = null;

      while ( relationships.hasNext()) {
        relationship = (Relationship) relationships.next();
        
        if ( splitType.equals(PAYMENTGROUP)) {
          if ( relationship instanceof PaymentGroupCommerceItemRelationship) {
          hasLineItems = true;
          break;
          } // end of if ()
          
        }else if (splitType.equals(COSTCENTER)) {
          if ( relationship instanceof CostCenterCommerceItemRelationship) {
            hasLineItems = true;
            break;
          } // end of if ()
        }
      } // end of while ()
      
    } // end of if ()

    // Order is split at item-level
    if (hasLineItems) {
      pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
    } // end of if ()
    else {
      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
    } // end of else
    

  }
}






