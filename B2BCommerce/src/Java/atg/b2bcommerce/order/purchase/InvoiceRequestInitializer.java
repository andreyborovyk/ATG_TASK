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

import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.userprofiling.Profile;
import atg.servlet.DynamoHttpServletRequest;
import atg.commerce.order.*;
import atg.commerce.order.purchase.*;
import atg.commerce.CommerceException;
import atg.beans.*;
import java.util.*;
import atg.repository.RepositoryItem;
import atg.commerce.claimable.ClaimableManager;
import atg.b2bcommerce.payment.invoicerequest.InvoiceRequest;

/**
 * The <code>InvoiceRequestInitializer</code> implements the PaymentGroupInitializer interface.
 * The <code>initializePaymentGroups</code> method is used to create an InvoiceRequest
 * PaymentGroup.
 *
 * @author Ernesto Mireles
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/purchase/InvoiceRequestInitializer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class InvoiceRequestInitializer extends GenericService implements PaymentGroupInitializer, PaymentGroupMatcher
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/purchase/InvoiceRequestInitializer.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //---------------------------------------------------------------------------
  // property: configuration
  //---------------------------------------------------------------------------
  PurchaseProcessConfiguration mConfiguration;

  /**
   * Sets property Configuration
   * @param pConfiguration a <code>PurchaseProcessConfiguration</code> value
   */
  public void setConfiguration(PurchaseProcessConfiguration pConfiguration) {
    mConfiguration = pConfiguration;
  }

  /**
   * Returns property Configuration
   * @return a <code>PurchaseProcessConfiguration</code> value
   */
  public PurchaseProcessConfiguration getConfiguration() {
    return mConfiguration;
  }

  //---------------------------------------------------------------------------
  // property: PaymentGroupManager
  //---------------------------------------------------------------------------
  PaymentGroupManager mPaymentGroupManager;

  /**
   * Specifies the PaymentGroupManager to use in creating the CreditCards.
   * @param pPaymentGroupManager a <code>PaymentGroupManager</code> value
   */
  public void setPaymentGroupManager(PaymentGroupManager pPaymentGroupManager) {
    mPaymentGroupManager = pPaymentGroupManager;
  }

  /**
   * Returns the PaymentGroupManager to use in creating the CreditCards.
   * @return a <code>PaymentGroupManager</code> value
   */
  public PaymentGroupManager getPaymentGroupManager() {
    return mPaymentGroupManager;
  }

  //---------------------------------------------------------------------------
  // property: InvoiceRequestType
  //---------------------------------------------------------------------------
  String mInvoiceRequestType;

  /**
   * Specifies the PaymentGroup type used to create CreditCards.
   * @param pInvoiceRequestType a <code>String</code> value
   */
  public void setInvoiceRequestType(String pInvoiceRequestType) {
    mInvoiceRequestType = pInvoiceRequestType;
  }

  /**
   * Returns the PaymentGroup type used to create CreditCards.
   * @return a <code>String</code> value
   */
  public String getInvoiceRequestType() {
    return mInvoiceRequestType;
  }

  //---------------------------------------------------------------------------
  // property: InvoiceRequestName
  //---------------------------------------------------------------------------
  String mInvoiceRequestName;

  /**
   * Specifies the name to use when adding the InvoiceRequest to the container.
   * @param pInvoiceRequestName a <code>String</code> value
   */
  public void setInvoiceRequestName(String pInvoiceRequestName) {
    mInvoiceRequestName = pInvoiceRequestName;
  }

  /**
   * Returns the name to use when adding the InvoiceRequest to the container.
   * @return a <code>String</code> value
   */
  public String getInvoiceRequestName() {
    return mInvoiceRequestName;
  }


  //---------------------------------------------------------------------------
  // property: BillingAddressPropertyName
  //---------------------------------------------------------------------------
  String mBillingAddressPropertyName;

  /**
   * Specifies the Profile property in which the billing address is found.
   * @param pBillingAddressPropertyName a <code>String</code> value
   */
  public void setBillingAddressPropertyName(String pBillingAddressPropertyName) {
    mBillingAddressPropertyName = pBillingAddressPropertyName;
  }

  /**
   * Returns the Profile property in which the billing address is found.
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
   * Specifies a Map of InvoiceRequest properties to be set with the value
   * found in the corresponding Profile property path.
   * @param pInvoiceRequestProperties a <code>Properties</code> value
   */
  public void setInvoiceRequestProperties(Properties pInvoiceRequestProperties) {
    mInvoiceRequestProperties = pInvoiceRequestProperties;
  }

  /**
   * Returns a Map of InvoiceRequest properties to be set with the value
   * found in the corresponding Profile property path.
   * @return a <code>Properties</code> value
   */
  public Properties getInvoiceRequestProperties() {
    return mInvoiceRequestProperties;
  }

  //---------------------------------------------------------------------------
  // property: OrderManager
  //---------------------------------------------------------------------------
  OrderManager mOrderManager;

  /**
   * Specifies the OrderManager to use in copying the billing address from
   * the Profile to the InvoiceRequest.
   * @param pOrderManager an <code>OrderManager</code> value
   */
  public void setOrderManager(OrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  /**
   * Returns the OrderManager to use in copying the billing address from
   * the Profile to the InvoiceRequest.
   * @return an <code>OrderManager</code> value
   */
  public OrderManager getOrderManager() {
    return mOrderManager;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>InvoiceRequestInitializer</code> instance.
   *
   */
  public InvoiceRequestInitializer () {}
  
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  /**
   * <code>initializePaymentGroups</code> executes the <code>initializeInvoiceRequest</code>
   * method.
   *
   * @param pProfile a <code>Profile</code> value
   * @param pPaymentGroupMapContainer a <code>PaymentGroupMapContainer</code> value
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @exception PaymentGroupInitializationException if an error occurs
   */
  public void initializePaymentGroups (Profile pProfile,
                                       PaymentGroupMapContainer pPaymentGroupMapContainer,
                                       DynamoHttpServletRequest pRequest)
    throws PaymentGroupInitializationException
  {
    initializeInvoiceRequest (pProfile,
                              pPaymentGroupMapContainer,
                              pRequest);
  }

  /**
   * <code>initializeInvoiceRequest</code> creates a new InvoiceRequest PaymentGroup
   * and adds it to the PaymentGroupMapContainer.
   *
   * @param pProfile a <code>Profile</code> value
   * @param pPaymentGroupMapContainer a <code>PaymentGroupMapContainer</code> value
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   */
  protected void initializeInvoiceRequest (Profile pProfile,
                                           PaymentGroupMapContainer pPaymentGroupMapContainer,
                                           DynamoHttpServletRequest pRequest)
  {
    PaymentGroupManager pgm = getPaymentGroupManager();
    InvoiceRequest invoiceRequest = null;

    try {
      invoiceRequest = (InvoiceRequest) pgm.createPaymentGroup(getInvoiceRequestType());
      setInvoiceRequestBillingAddress(invoiceRequest, pProfile);
      setDynamicInvoiceRequestProperties (invoiceRequest, pProfile);
    } catch (CommerceException exc) {
      if (isLoggingError()) {
        logError(exc);
      }
    } catch (PropertyNotFoundException exc) {
      if (isLoggingError()) {
        logError(exc);
      }
    }

    String key = getInvoiceRequestName();
    pPaymentGroupMapContainer.setDefaultPaymentGroupName(key);
    pPaymentGroupMapContainer.addPaymentGroup(key, invoiceRequest);
  }
  
  /**
   * The <code>setInvoiceRequestProperties</code> method is used to copy any
   * deep bean properties directly onto the InvoiceRequest. Configure the InvoiceRequest
   * property names and the corresponding bean property paths in the
   * InvoiceRequestProperties property. Typically this bean is a Profile.
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

  public String matchPaymentGroup(PaymentGroup pPaymentGroup, 
                                  PaymentGroupMapContainer pPaymentGroupMapContainer) {

    if (!(pPaymentGroup instanceof InvoiceRequest)) return null;

    Set paymentGroupNames = pPaymentGroupMapContainer.getPaymentGroupMap().keySet();
    if (paymentGroupNames == null) return null;

    Iterator nameIter = paymentGroupNames.iterator();
    String paymentGroupName = null;
    boolean found = false;
    while (nameIter.hasNext() && !found) {
      paymentGroupName = (String) nameIter.next();
      PaymentGroup paymentGroup = pPaymentGroupMapContainer.getPaymentGroup(paymentGroupName);
 
      if (paymentGroup instanceof InvoiceRequest)
	found = compareInvoiceRequests((InvoiceRequest) pPaymentGroup, (InvoiceRequest) paymentGroup);
    }

    if (found) return paymentGroupName;
    else return null;
  }
        
  public String getNewPaymentGroupName(PaymentGroup pPaymentGroup) {

    if (!(pPaymentGroup instanceof InvoiceRequest)) return null;
    return ((InvoiceRequest) pPaymentGroup).getPONumber();
  }
        
  //-------------------------------------
  // return true if each fields in the gift certificates are the same
  private boolean compareInvoiceRequests(InvoiceRequest pInvoiceRequestA, InvoiceRequest pInvoiceRequestB)
  {
    if((pInvoiceRequestA == null) && (pInvoiceRequestB == null)) {
      return true;
    }
    if(pInvoiceRequestA == null)
      return false;
    if(pInvoiceRequestB == null)
      return false;

    String aPONumber = pInvoiceRequestA.getPONumber();
    String bPONumber = pInvoiceRequestB.getPONumber();

    if((aPONumber == null) &&
       ((bPONumber != null)))
      return false;

    if(
       ((aPONumber == null) && (bPONumber == null)) ||
        (aPONumber.equals(bPONumber))
       )
    {
      return true;
    }
    else
      return false;
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
   * <LI>orderManager
   * <LI>paymentGroupManager
   * </UL>
   **/
  protected void copyConfiguration() {
    if (mConfiguration != null) {
      if (getOrderManager() == null) {
        setOrderManager(mConfiguration.getOrderManager());
      }
      if (getPaymentGroupManager() == null) {
        setPaymentGroupManager(mConfiguration.getPaymentGroupManager());
      }
    }
  }

  // -------------------------------
  // GenericService overrides
  // -------------------------------

  /**
   * Perform one-time startup operations, including copying property
   * settings from the optional <code>PurchaseProcessConfiguration</code>
   * component.
   */
  public void doStartService()
       throws ServiceException
  {
    super.doStartService();
    copyConfiguration();
  }

}   // end of class
