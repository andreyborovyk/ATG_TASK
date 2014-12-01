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

import atg.commerce.CommerceException;
import atg.commerce.order.*;
import atg.core.util.ResourceUtils;
import atg.servlet.*;
import atg.droplet.*;
import atg.repository.*;
import java.io.IOException;
import javax.transaction.Transaction;
import javax.servlet.ServletException;

/**
 * The PaymentAddressFormHandler is used to change a PaymentGroup's address. Not all
 * PaymentGroup types have a billing address, so we require them to implement a
 * PaymentAddressContainer interface.
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/PaymentAddressFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.droplet.GenericFormHandler
 * @see atg.commerce.order.purchase.PurchaseProcessFormHandler
 * @beaninfo
 *   description: A formhandler which allows the user to change an address.
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 */

public class PaymentAddressFormHandler extends PurchaseProcessFormHandler 
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/PaymentAddressFormHandler.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  public static final String MSG_MISSING_PAYMENT_GROUP = "missingPaymentGroup";
  public static final String MSG_MISSING_PAYMENT_GROUP_KEY = "missingPaymentGroupKey";
  public static final String MSG_MISSING_ADDRESS_ID = "missingAddressId";
  public static final String MSG_MISSING_ADDRESS = "missingAddress";
  public static final String ERROR_MISSING_PROFILE_REPOSITORY = "missingProfileRepository";
  public static final String ERROR_MISSING_PAYMENT_GROUP_MAP = "missingPaymentGroupMap";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //---------------------------------------------------------------------------
  // property: PaymentGroup
  //---------------------------------------------------------------------------
  PaymentAddressContainer mPaymentGroup;

  /**
   * Set the PaymentGroup property.
   * @param pPaymentGroup a <code>PaymentAddressContainer</code> value
   * @beaninfo description: The PaymentAddressContainer.
   */
  public void setPaymentGroup(PaymentAddressContainer pPaymentGroup) {
    mPaymentGroup = pPaymentGroup;
  }

  /**
   * Return the PaymentGroup property.
   * @return a <code>PaymentAddressContainer</code> value
   */
  public PaymentAddressContainer getPaymentGroup() {
    return mPaymentGroup;
  }

  //---------------------------------------------------------------------------
  // property: PaymentGroupKey
  //---------------------------------------------------------------------------
  String mPaymentGroupKey;

  /**
   * Set the PaymentGroupKey property.
   * @param pPaymentGroupKey a <code>String</code> value
   * @beaninfo description: The key of the PaymentGroup in the PaymentGroupFormHandler.
   */
  public void setPaymentGroupKey(String pPaymentGroupKey) {
    mPaymentGroupKey = pPaymentGroupKey;
  }

  /**
   * Return the PaymentGroupKey property.
   * @return a <code>String</code> value
   */
  public String getPaymentGroupKey() {
    return mPaymentGroupKey;
  }

  //---------------------------------------------------------------------------
  // property: AddressId
  //---------------------------------------------------------------------------
  String mAddressId;

  /**
   * Set the AddressId property.
   * @param pAddressId a <code>String</code> value
   * @beaninfo description: The id of the Address item.
   */
  public void setAddressId(String pAddressId) {
    mAddressId = pAddressId;
  }

  /**
   * Return the AddressId property.
   * @return a <code>String</code> value
   */
  public String getAddressId() {
    return mAddressId;
  }

  //---------------------------------------------------------------------------
  // property: AddressItemDescriptorName
  //---------------------------------------------------------------------------
  String mAddressItemDescriptorName;

  /**
   * Set the AddressItemDescriptorName property.
   * @param pAddressItemDescriptorName a <code>String</code> value
   * @beaninfo description: The name of the Address item descriptor.
   */
  public void setAddressItemDescriptorName(String pAddressItemDescriptorName) {
    mAddressItemDescriptorName = pAddressItemDescriptorName;
  }

  /**
   * Return the AddressItemDescriptorName property.
   * @return a <code>String</code> value
   */
  public String getAddressItemDescriptorName() {
    return mAddressItemDescriptorName;
  }

  //---------------------------------------------------------------------------
  // property: SetAddressSuccessURL
  //---------------------------------------------------------------------------
  String mSetAddressSuccessURL;

  /**
   * Set the SetAddressSuccessURL property.
   * @param pSetAddressSuccessURL a <code>String</code> value
   * @beaninfo description: URL for redirection upon success.
   */
  public void setSetAddressSuccessURL(String pSetAddressSuccessURL) {
    mSetAddressSuccessURL = pSetAddressSuccessURL;
  }

  /**
   * Return the SetAddressSuccessURL property.
   * @return a <code>String</code> value
   */
  public String getSetAddressSuccessURL() {
    return mSetAddressSuccessURL;
  }

  //---------------------------------------------------------------------------
  // property: SetAddressErrorURL
  //---------------------------------------------------------------------------
  String mSetAddressErrorURL;

  /**
   * Set the SetAddressErrorURL property.
   * @param pSetAddressErrorURL a <code>String</code> value
   * @beaninfo description: URL for redirection upon error.
   */
  public void setSetAddressErrorURL(String pSetAddressErrorURL) {
    mSetAddressErrorURL = pSetAddressErrorURL;
  }

  /**
   * Return the SetAddressErrorURL property.
   * @return a <code>String</code> value
   */
  public String getSetAddressErrorURL() {
    return mSetAddressErrorURL;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>PaymentAddressFormHandler</code> instance.
   *
   */
  public PaymentAddressFormHandler () {}
  
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  /**
   * <code>preSetAddress</code> is used for work that must happen before the
   * address is set.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preSetAddress(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>postSetAddress</code> is used for work that must happen after the address
   * is set.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postSetAddress(DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>handleSetAddress</code> sets the address provided by the AddressId property
   * onto the PaymentAddressContainer.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleSetAddress (DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    Transaction tr = null;
    try {
      tr = ensureTransaction();    

      if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

      if (!checkFormRedirect(null, getSetAddressErrorURL(), pRequest, pResponse))
        return false;
      preSetAddress(pRequest, pResponse);
      if (!checkFormRedirect(null, getSetAddressErrorURL(), pRequest, pResponse))
        return false;
      setAddress(pRequest, pResponse);
      if (!checkFormRedirect(null, getSetAddressErrorURL(), pRequest, pResponse))
        return false;
      postSetAddress(pRequest, pResponse);

      return checkFormRedirect(getSetAddressSuccessURL(), getSetAddressErrorURL(), pRequest, pResponse);
    }
    finally {
      if (tr != null) commitTransaction(tr);
    }
  }

  /**
   * <code>setAddress</code> takes the address RepositoryItem given by the AddressId property
   * and AddressItemDescriptorName, and copies it into the PaymentAddressContainer.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void setAddress(DynamoHttpServletRequest pRequest,
                         DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    Repository repository = getProfileRepository();
    PaymentGroupMapContainer container = getPaymentGroupMapContainer();
    
    if (repository == null) {
      if (isLoggingError()) {
        logError(ResourceUtils.getMsgResource(ERROR_MISSING_PROFILE_REPOSITORY,
                                              getResourceBundleName(), getResourceBundle()));
      }
      return;
    }

    if (container == null) {
      if (isLoggingError()) {
        logError(ResourceUtils.getMsgResource(ERROR_MISSING_PAYMENT_GROUP_MAP,
                                              getResourceBundleName(), getResourceBundle()));
      }
      return;
    }

    String addressId = getAddressId();
    if (addressId == null) {
      String msg = formatUserMessage(MSG_MISSING_ADDRESS_ID, pRequest, pResponse);
      String propertyPath = generatePropertyPath("addressId");
      addFormException (new DropletFormException(msg, propertyPath, MSG_MISSING_ADDRESS_ID));
      return;
    }
    String pgKey = getPaymentGroupKey();
    if (pgKey == null) {
      String msg = formatUserMessage(MSG_MISSING_PAYMENT_GROUP_KEY, pRequest, pResponse);
      String propertyPath = generatePropertyPath("paymentGroupKey");
      addFormException (new DropletFormException(msg, propertyPath, MSG_MISSING_PAYMENT_GROUP_KEY));
      return;
    }
    PaymentAddressContainer pac = (PaymentAddressContainer) container.getPaymentGroup(pgKey);
    if (pac == null) {
      String msg = formatUserMessage(MSG_MISSING_PAYMENT_GROUP, pRequest, pResponse);
      String propertyPath = generatePropertyPath("paymentGroup");
      addFormException (new DropletFormException(msg, propertyPath, MSG_MISSING_PAYMENT_GROUP));
      return;
    }
    
    try {
      RepositoryItem addressItem = repository.getItem (addressId, getAddressItemDescriptorName());
      if (addressItem != null) {
        OrderTools.copyAddress(addressItem, pac.getBillingAddress());
      } else {
        String msg = formatUserMessage(MSG_MISSING_ADDRESS, pRequest, pResponse);
        String propertyPath = generatePropertyPath("addressId");
        addFormException (new DropletFormException(msg, propertyPath, MSG_MISSING_ADDRESS));
        return;
      }
    } catch (RepositoryException re) {
      if (isLoggingError()) {
        logError(re);
      }
    } catch (CommerceException ce) {
      if (isLoggingError()) {
        logError(ce);
      }
    }
  }
}   // end of class
