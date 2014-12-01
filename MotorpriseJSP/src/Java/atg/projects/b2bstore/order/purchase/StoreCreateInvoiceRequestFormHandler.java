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

package atg.projects.b2bstore.order.purchase;

import atg.b2bcommerce.order.purchase.CreateInvoiceRequestFormHandler;
import atg.servlet.*;
import atg.repository.RepositoryItem;
import atg.core.util.StringUtils;
import atg.core.util.ResourceUtils;
import atg.droplet.DropletFormException;
import atg.b2bcommerce.payment.invoicerequest.InvoiceRequest;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * The <code>StoreCreateInvoiceRequestFormHandler</code> class extends the
 * <code>CreateInvoiceRequestFormHandler</code> to override the
 * <code>checkRequiredProperties</code> and <code>nameInvoiceRequest</code>
 * methods. This now accepts either a requisition number or a PO number as
 * sufficient to create and name an <code>InvoiceRequest</code>.
 *
 * @author Ernesto Mireles
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/order/purchase/StoreCreateInvoiceRequestFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class StoreCreateInvoiceRequestFormHandler extends CreateInvoiceRequestFormHandler
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/order/purchase/StoreCreateInvoiceRequestFormHandler.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  public static String RESOURCE_BUNDLE = "atg.projects.b2bstore.order.purchase.UserMessages";
  public static final String INCOMPLETE_INVOICE_REQUEST = "incompleteInvoiceRequest";
  public static final String DEFAULT_INVOICE_REQUEST = "defaultInvoiceRequest";
  public static final String INVOICE_REQUEST_PO = "invoiceRequestPO";
  public static final String INVOICE_REQUEST_REQUISITION = "invoiceRequestRequisition";

    
  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>StoreCreateInvoiceRequestFormHandler</code> instance.
   *
   */
  public StoreCreateInvoiceRequestFormHandler () {}
  
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  /**
   * The <code>nameInvoiceRequest</code> method is used to determine the name of
   * the InvoiceRequest that will be its key in the Container.
   *
   * @param pInvoiceRequest an <code>InvoiceRequest</code> value
   * @param pProfile a <code>RepositoryItem</code> value
   */
  protected void nameInvoiceRequest (InvoiceRequest pInvoiceRequest,
                                     RepositoryItem pProfile)
  {
    ResourceBundle userResources = ResourceBundle.getBundle(RESOURCE_BUNDLE, getDefaultLocale());
    String [] msgArgs = { pInvoiceRequest.getPONumber(), pInvoiceRequest.getRequisitionNumber() };
    String msg;
    if (!(StringUtils.isEmpty(getInvoiceRequest().getPONumber()))) {
      msg = ResourceUtils.getUserMsgResource(INVOICE_REQUEST_PO, RESOURCE_BUNDLE, userResources, msgArgs);
    } else if (!(StringUtils.isEmpty(getInvoiceRequest().getRequisitionNumber()))) {
      msg = ResourceUtils.getUserMsgResource(INVOICE_REQUEST_REQUISITION, RESOURCE_BUNDLE, userResources, msgArgs);
    } else {
      msg = ResourceUtils.getUserMsgResource(DEFAULT_INVOICE_REQUEST, RESOURCE_BUNDLE, userResources, msgArgs);
    }
    setInvoiceRequestName(msg);    
  }

  /**
   * The <code>checkRequiredProperties</code> method is used to check any required
   * properties before the InvoiceRequest is created. This will create a new
   * <code>DropletFormException</code> if any required properties are missing.
   * This implementation requires either a PONumber or a RequisitionNumber.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void checkRequiredProperties (DynamoHttpServletRequest pRequest,
                                          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    // not creating anything without a PONumber
    if ((StringUtils.isEmpty(getInvoiceRequest().getPONumber())) && (StringUtils.isEmpty(getInvoiceRequest().getRequisitionNumber()))) {
      ResourceBundle userResources = ResourceBundle.getBundle(RESOURCE_BUNDLE, getDefaultLocale());
      String msg = ResourceUtils.getUserMsgResource(INCOMPLETE_INVOICE_REQUEST, RESOURCE_BUNDLE, userResources);
      addFormException(new DropletFormException(msg, INCOMPLETE_INVOICE_REQUEST));
    }
  }
}   // end of class
