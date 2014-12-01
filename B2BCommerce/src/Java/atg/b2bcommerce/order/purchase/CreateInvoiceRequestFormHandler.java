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

import atg.commerce.CommerceException;
import atg.commerce.order.*;
import atg.commerce.order.purchase.*;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.droplet.DropletFormException;
import atg.servlet.*;
import atg.transaction.*;
import atg.core.util.StringUtils;
import atg.repository.*;
import atg.beans.*;
import atg.core.util.ResourceUtils;
import java.util.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.transaction.Transaction;
import atg.b2bcommerce.payment.invoicerequest.InvoiceRequest;

/**
 * The <code>CreateInvoiceRequestFormHandler</code> class is used to create an
 * InvoiceRequest PaymentGroup. This InvoiceRequest is optionally added to a
 * PaymentGroupMapContainer.
 *
 * @beaninfo
 *   description: A formhandler which allows the user to create an InvoiceRequest.
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
*
 * @author Ernesto Mireles
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/purchase/CreateInvoiceRequestFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.droplet.GenericFormHandler
 * @see atg.commerce.order.purchase.PurchaseProcessFormHandler
 */
public class CreateInvoiceRequestFormHandler
  extends PurchaseProcessFormHandler
  implements CreatePaymentGroupFormHandler
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/purchase/CreateInvoiceRequestFormHandler.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  public static final String MSG_INCOMPLETE_INVOICE_REQUEST = "incompleteInvoiceRequest";
  public static final String NAME_INVOICE_REQUEST = "nameInvoiceRequest";
  private static final String USER_MESSAGES = "atg.b2bcommerce.order.UserMessages";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //---------------------------------------------------------------------------
  // property: AddToContainer
  //---------------------------------------------------------------------------
  boolean mAddToContainer;

  /**
   * Set the AddToContainer property.
   * @param pAddToContainer a <code>boolean</code> value
   * @beaninfo description: Should the InvoiceRequest be added to a PaymentGroupMapContainer?
   */
  public void setAddToContainer(boolean pAddToContainer) {
    mAddToContainer = pAddToContainer;
  }

  /**
   * Return the AddToContainer property.
   * @return a <code>boolean</code> value
   */
  public boolean isAddToContainer() {
    return mAddToContainer;
  }

  //---------------------------------------------------------------------------
  // property: Container
  //---------------------------------------------------------------------------
  PaymentGroupMapContainer mContainer;

  /**
   * Set the Container property.
   * @param pContainer a <code>PaymentGroupMapContainer</code> value
   * @beaninfo description: The container to add the InvoiceRequest to.
   */
  public void setContainer(PaymentGroupMapContainer pContainer) {
    mContainer = pContainer;
  }

  /**
   * Return the Container property.
   * @return a <code>PaymentGroupMapContainer</code> value
   */
  public PaymentGroupMapContainer getContainer() {
    return mContainer;
  }

  //---------------------------------------------------------------------------
  // property: InvoiceRequestName
  //---------------------------------------------------------------------------
  String mInvoiceRequestName;

  /**
   * Set the InvoiceRequestName property.
   * @param pInvoiceRequestName a <code>String</code> value
   * @beaninfo description: The name of the new InvoiceRequest.
   */
  public void setInvoiceRequestName(String pInvoiceRequestName) {
    mInvoiceRequestName = pInvoiceRequestName;
  }

  /**
   * Return the InvoiceRequestName property.
   * @return a <code>String</code> value
   */
  public String getInvoiceRequestName() {
    return mInvoiceRequestName;
  }
  
  //---------------------------------------------------------------------------
  // property: InvoiceRequestType
  //---------------------------------------------------------------------------
  String mInvoiceRequestType;

  /**
   * Set the InvoiceRequestType property.
   * @param pInvoiceRequestType a <code>String</code> value
   * @beaninfo description: The PaymentGroup type for InvoiceRequests.
   */
  public void setInvoiceRequestType(String pInvoiceRequestType) {
    mInvoiceRequestType = pInvoiceRequestType;
  }

  /**
   * Return the InvoiceRequestType property.
   * @return a <code>String</code> value
   */
  public String getInvoiceRequestType() {
    return mInvoiceRequestType;
  }

  //---------------------------------------------------------------------------
  // property: NewInvoiceRequestSuccessURL
  //---------------------------------------------------------------------------
  String mNewInvoiceRequestSuccessURL;

  /**
   * Set the NewInvoiceRequestSuccessURL property.
   * @param pNewInvoiceRequestSuccessURL a <code>String</code> value
   * @beaninfo description: URL for redirection upon success.
   */
  public void setNewInvoiceRequestSuccessURL(String pNewInvoiceRequestSuccessURL) {
    mNewInvoiceRequestSuccessURL = pNewInvoiceRequestSuccessURL;
  }

  /**
   * Return the NewInvoiceRequestSuccessURL property.
   * @return a <code>String</code> value
   */
  public String getNewInvoiceRequestSuccessURL() {
    return mNewInvoiceRequestSuccessURL;
  }

  //---------------------------------------------------------------------------
  // property: NewInvoiceRequestErrorURL
  //---------------------------------------------------------------------------
  String mNewInvoiceRequestErrorURL;

  /**
   * Set the NewInvoiceRequestErrorURL property.
   * @param pNewInvoiceRequestErrorURL a <code>String</code> value
   * @beaninfo description: URL for redirection upon failure.
   */
  public void setNewInvoiceRequestErrorURL(String pNewInvoiceRequestErrorURL) {
    mNewInvoiceRequestErrorURL = pNewInvoiceRequestErrorURL;
  }

  /**
   * Return the NewInvoiceRequestErrorURL property.
   * @return a <code>String</code> value
   */
  public String getNewInvoiceRequestErrorURL() {
    return mNewInvoiceRequestErrorURL;
  }

  //---------------------------------------------------------------------------
  // property: InvoiceRequest
  //---------------------------------------------------------------------------
  InvoiceRequest mInvoiceRequest;

  /**
   * Set the InvoiceRequest property.
   * @param pInvoiceRequest an <code>InvoiceRequest</code> value
   * @beaninfo description: The new InvoiceRequest.
   */
  public void setInvoiceRequest(InvoiceRequest pInvoiceRequest) {
    mInvoiceRequest = pInvoiceRequest;
  }

  /**
   * Return the InvoiceRequest property. This method exposes the InvoiceRequest as a JavaBean property so that
   * it may be edited directly from a .jhtml page. If this is null, then the PaymentGroupManager
   * is used to create a new InvoiceRequest.
   * @return an <code>InvoiceRequest</code> value
   */
  public InvoiceRequest getInvoiceRequest() {
    if (mInvoiceRequest == null) {
      try {
        mInvoiceRequest = (InvoiceRequest) getPaymentGroupManager().createPaymentGroup(getInvoiceRequestType());
      } catch (CommerceException exc) {
        if (isLoggingError()) logError(exc);
      }
    }
    return mInvoiceRequest;
  }

  //---------------------------------------------------------------------------
  // property: BillingAddressPropertyName
  //---------------------------------------------------------------------------
  String mBillingAddressPropertyName;

  /**
   * Set the BillingAddressPropertyName property.
   * @param pBillingAddressPropertyName a <code>String</code> value
   * @beaninfo description: The Profile property name of the billing address.
   */
  public void setBillingAddressPropertyName(String pBillingAddressPropertyName) {
    mBillingAddressPropertyName = pBillingAddressPropertyName;
  }

  /**
   * Return the BillingAddressPropertyName property.
   * @return a <code>String</code> value
   */
  public String getBillingAddressPropertyName() {
    return mBillingAddressPropertyName;
  }

  //---------------------------------------------------------------------------
  // property: InvoiceRequestProperties
  //---------------------------------------------------------------------------
  Properties mInvoiceRequestProperties;

  /**
   * Set the InvoiceRequestProperties property.
   * @param pInvoiceRequestProperties a <code>Properties</code> value
   * @beaninfo description: A Map of InvoiceRequest properties to Profile properties.
   */
  public void setInvoiceRequestProperties(Properties pInvoiceRequestProperties) {
    mInvoiceRequestProperties = pInvoiceRequestProperties;
  }

  /**
   * Return the InvoiceRequestProperties property.
   * @return a <code>Properties</code> value
   */
  public Properties getInvoiceRequestProperties() {
    return mInvoiceRequestProperties;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>CreateInvoiceRequestFormHandler</code> instance.
   *
   */
  public CreateInvoiceRequestFormHandler() {}
  
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  //--------------------------------------------------
  // Create New InvoiceRequest Code
  //--------------------------------------------------

  /**
   * <code>handleNewInvoiceRequest</code> is used to create a new InvoiceRequest PaymentGroup.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleNewInvoiceRequest(DynamoHttpServletRequest pRequest,
                                       DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CreateInvoiceRequestFormHandler.handleNewInvoiceRequest";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod))) {
      Transaction tr = null;
      try {
        tr = ensureTransaction();

        if (getDefaultLocale() == null) setDefaultLocale(getUserLocale(pRequest, pResponse));

        //Check if any form errors exist.  If they do, redirect to Error URL:
        if (!checkFormRedirect(null, getNewInvoiceRequestErrorURL(), pRequest, pResponse))
          return false;
      
        preCreateInvoiceRequest(pRequest, pResponse);
        checkRequiredProperties(pRequest, pResponse);

        //Check if any form errors exist.  If they do, redirect to Error URL:
        if (!checkFormRedirect(null, getNewInvoiceRequestErrorURL(), pRequest, pResponse))
          return false;

        createInvoiceRequest(pRequest, pResponse);

        //Check if any form errors exist.  If they do, redirect to Error URL:
        if (!checkFormRedirect(null, getNewInvoiceRequestErrorURL(), pRequest, pResponse))
          return false;

        postCreateInvoiceRequest(pRequest, pResponse);

        return checkFormRedirect (getNewInvoiceRequestSuccessURL(), 
                                  getNewInvoiceRequestErrorURL(), pRequest, pResponse);
      }
      finally {
        if (tr != null) commitTransaction(tr);
      }
    }
    else {
      return false;
    }
  }
  
  /**
   * <code>preCreateInvoiceRequest</code> is for work that must happen before
   * a new InvoiceRequest is created.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preCreateInvoiceRequest(DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>postCreateInvoiceRequest</code> is for work that must happen after
   * a new InvoiceRequest is created.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postCreateInvoiceRequest(DynamoHttpServletRequest pRequest,
                                       DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>createInvoiceRequest</code> creates a new InvoiceRequest. The InvoiceRequestType
   * property gives the type of PaymentGroup to create. If <code>isAddToContainer</code>
   * is true then the InvoiceRequest is added to the PaymentGroupMapContainer and made
   * the default PaymentGroup. The item referenced by the Profile property given by
   * <code>getBillingAddressPropertyName</code> is copied into the InvoiceRequest.
   * Any additional Profile properties found in the <code>getInvoiceRequestProperties</code>
   * are dynamically applied to the InvoiceRequest.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void createInvoiceRequest(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
  throws ServletException, IOException
  {
    PaymentGroupMapContainer container = getContainer();

    try {
      InvoiceRequest invoiceRequest = getInvoiceRequest();
      
      // assign properties to new InvoiceRequest 
      try {
        setInvoiceRequestBillingAddress(invoiceRequest, getProfile());
      } catch (PropertyNotFoundException pnfe) {
        if (isLoggingError()) {
          logError(pnfe);
        }
      }
      try {
        setDynamicInvoiceRequestProperties (invoiceRequest, getProfile());
      } catch (PropertyNotFoundException pnfe) {
        if (isLoggingError()) {
          logError(pnfe);
        }
      }

      // add to Container
      nameInvoiceRequest(invoiceRequest, getProfile());
      if (isAddToContainer()) {
        container.setDefaultPaymentGroupName(getInvoiceRequestName());
        container.addPaymentGroup(getInvoiceRequestName(), invoiceRequest);
      }
      
    } catch (CommerceException exc) {
      if (isLoggingError()) {
        logError(exc);
      }
    }
  }

  /**
   * The <code>checkRequiredProperties</code> method is used to check any required
   * properties before the InvoiceRequest is created. This will create a new
   * <code>DropletFormException</code> if any required properties are missing.
   * The current implementation requires a PONumber.
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
    if (StringUtils.isEmpty(getInvoiceRequest().getPONumber())) {
      ResourceBundle userResources = ResourceBundle.getBundle(USER_MESSAGES, getDefaultLocale());
      String msg = ResourceUtils.getUserMsgResource(MSG_INCOMPLETE_INVOICE_REQUEST, USER_MESSAGES, userResources);
      addFormException(new DropletFormException(msg, MSG_INCOMPLETE_INVOICE_REQUEST));
    }
  }
  
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
    String [] msgArgs = { pInvoiceRequest.getPONumber(), pInvoiceRequest.getRequisitionNumber() };
    ResourceBundle userResources = ResourceBundle.getBundle(USER_MESSAGES, getDefaultLocale());
    String msg = ResourceUtils.getUserMsgResource(NAME_INVOICE_REQUEST, USER_MESSAGES, userResources, msgArgs);
    setInvoiceRequestName(msg);
  }
  
  /**
   * The <code>setInvoiceRequestProperties</code> method is used to copy any
   * deep Profile properties directly onto the InvoiceRequest. Configure the InvoiceRequest
   * property names and the corresponding Profile property paths in the
   * InvoiceRequestProperties property.
   *
   * @param pInvoiceRequest an <code>InvoiceRequest</code> value
   * @param pProfile a <code>RepositoryItem</code> value
   * @exception PropertyNotFoundException if an error occurs
   */
  protected void setDynamicInvoiceRequestProperties (InvoiceRequest pInvoiceRequest,
                                                     RepositoryItem pProfile)
    throws PropertyNotFoundException
  {
    Set entries = getInvoiceRequestProperties().entrySet();
    Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      String invoicePropertyName = (String) entry.getKey();
      String profilePropertyPath = (String) entry.getValue();
      Object invoicePropertyValue = DynamicBeans.getSubPropertyValue(pProfile, profilePropertyPath);
      DynamicBeans.setSubPropertyValue(pInvoiceRequest, invoicePropertyName, invoicePropertyValue);
    }
    
  }

  /**
   * The <code>setInvoiceRequestBillingAddress</code> method is used to copy
   * a billing address from the Profile into an InvoiceRequest.
   *
   * @param pInvoiceRequest an <code>InvoiceRequest</code> value
   * @param pProfile a <code>Profile</code> value
   * @exception PropertyNotFoundException if an error occurs
   * @exception CommerceException if an error occurs
   */
  protected void setInvoiceRequestBillingAddress (InvoiceRequest pInvoiceRequest,
                                                  RepositoryItem pProfile)
    throws PropertyNotFoundException, CommerceException
  {
    RepositoryItem item = (RepositoryItem) DynamicBeans.getSubPropertyValue(pProfile, getBillingAddressPropertyName());
    if (item != null) {
      getOrderManager().copyAddress(item, pInvoiceRequest.getBillingAddress());
    }
  }

  // -------------------------------
  // Centralized configuration
  // -------------------------------

  /**
   * Copy property settings from the optional
   * <code>PurchaseProcessConfiguration</code> component. Property
   * values that were configured locally are preserved.
   *
   * Configures the following properties (if not already set):
   * <UL>
   * <LI>container (from paymentGroupMapContainer)
   * </UL>
   **/
  protected void copyConfiguration() {
    super.copyConfiguration();
    if (getConfiguration() != null) {
      if (getContainer() == null) {
        setContainer(getConfiguration().getPaymentGroupMapContainer());
      }
    }
  }
}   // end of class
