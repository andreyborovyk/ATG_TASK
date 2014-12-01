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
package atg.projects.store.order.purchase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import atg.commerce.CommerceException;
import atg.commerce.inventory.InventoryException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CreditCard;
import atg.commerce.order.Order;
import atg.core.util.ContactInfo;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.StoreOrderManager;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.profile.StorePropertyManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryUtils;
import atg.userprofiling.PropertyManager;

/**
 * This class performs logic necessary for the commit-order process
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/purchase/StoreCommitOrderProcessHelper.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class StoreCommitOrderProcessHelper extends StorePurchaseProcessHelper{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/purchase/StoreCommitOrderProcessHelper.java#2 $$Change: 651448 $";


  /**
   * validates the credit card verification number on the order, if required.
   * @param pOrder - An <code>Order</code> value.
   * @param pCcv - The credit card verification number
   * @return True if the credit card verification number is valid
   */
  public boolean validateCreditCardVerificationNumber(Order pOrder, String pCcv) {
     StoreOrderTools orderTools = getStoreOrderTools();
     CreditCard card;
     if ((card = orderTools.getCreditCard(pOrder)) != null) {
       //if there's a credit card payment group, make sure the verification number has already been provide. 
       //this value might be missing if express checkout was used to get here.
       if (getStoreConfiguration().isRequireCreditCardVerification()) {
         String currentNumber = orderTools.getCreditCardVerificationNumberFromCard(pOrder);

         if (currentNumber == null) {
           if (!orderTools.validateCreditCardAuthorizationNumber(pCcv)) {
             return false;
           }

           orderTools.setCreditCardVerificationNumber(pOrder, pCcv);
         }
       }
     }
     
     return true;
  }
  
  /**
   * Adds the profile's e-mail address to the credit cart
   * @param pOrder - An <code>Order</code> value
   * @param pProfile - A <code>RepositoryItem</code> value
   */
  public void addEmailToCreditCard(Order pOrder, RepositoryItem pProfile) {
    PropertyManager pm = getStoreOrderTools().getProfileTools().getPropertyManager();
    
    CreditCard card;
    
    if ((card = getStoreOrderTools().getCreditCard(pOrder)) != null) {
      // Cybersource requires that the billingAddress has an email address.
      // Set it here if paying with credit card.
      ContactInfo billingAddress = (ContactInfo) card.getBillingAddress();
      String email = (String) pProfile.getPropertyValue(pm.getEmailAddressPropertyName());

      if ((email != null) && (email.trim().length() > 0)) {
        billingAddress.setEmail(email);
      } else {
        billingAddress.setEmail("user@atg.com");
      }
      
    }
  }
  
  /**
   * Makes sure that the profile id is set correctly on the order.
   * @param pOrder - An <code>Order</code> value.
   * @param pProfile - A <code>RepositoryItem</code> value.
   */
  public void updateProfileIdOnOrder(Order pOrder, RepositoryItem pProfile) {
    String profileId = pProfile.getRepositoryId();
    
    if (!pOrder.getProfileId().equals(profileId)) {
      if (isLoggingDebug()) {
        logDebug("Setting profile_id on order.");
      }

      pOrder.setProfileId(pProfile.getRepositoryId());

      try {
        getOrderManager().updateOrder(pOrder);
      } catch (CommerceException ce) {
        if (isLoggingError()) {
          logError(LogUtils.formatMajor("Could not update order: " + ce));
        }
      }
    }
  }
  
  /**
   * Add the e-mail provided by an anonymous shopper to the profile
   * @param pEmailAddress - E-mail address as a <code>String</code> value.
   * @param pProfile - Profile to add the e-mail address too, a <code>RepositoryItem</code> value.
   */
  public void addEmailToAnonymousUser(String pEmailAddress, RepositoryItem pProfile) {
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();

    PropertyManager pm = orderTools.getProfileTools().getPropertyManager();

    //set the email provided by the anonymous shopper 
    if (pProfile.isTransient()) {
      MutableRepositoryItem mutitem = (MutableRepositoryItem) pProfile;
      mutitem.setPropertyValue(pm.getEmailAddressPropertyName(), pEmailAddress);
    }
  }
  
  /**
   * Makes post commit order processing: setting profile's last purchase date,
   * number of orders and bought items, order's OMS order id, inventory managing.
   * 
   * @param pStoreOrder - The last order, a <code>StoreOrderImpl</code> value.
   * @param pProfile - A <code>RepositoryItem</code> value.
   */
  public void doPostCommitOrderProcessing(StoreOrderImpl pStoreOrder, RepositoryItem pProfile)
    throws RepositoryException {

    // Assign the OMS order id
    getStoreOrderTools().assignOmsOrderId(pStoreOrder);

    // if user submits another order in this session, then make sure the
    // "showSamples" flag isn't set to false by setting to true here.
    StorePropertyManager pm = (StorePropertyManager) getStoreOrderTools().getProfileTools().getPropertyManager();
    MutableRepositoryItem profile = RepositoryUtils.getMutableRepositoryItem(pProfile);

    StoreOrderManager orderManager = (StoreOrderManager) getOrderManager();

    try {
      orderManager.manageInventoryOnCheckout(pStoreOrder);
    } catch (InventoryException e) {
      if (isLoggingError()) {
        logError(e);
      }
    }

    profile.setPropertyValue(pm.getLastPurchaseDate(), pStoreOrder.getSubmittedDate());

    Integer numOrders = (Integer) profile.getPropertyValue(pm.getNumberOfOrders());
    numOrders = (numOrders != null) ? numOrders : new Integer(0);
    profile.setPropertyValue(pm.getNumberOfOrders(), new Integer(numOrders.intValue() + 1));

    List commerceItems = pStoreOrder.getCommerceItems();
    List previousItems = (List) profile.getPropertyValue(pm.getItemsBought());
    HashSet merge = new HashSet(previousItems);

    for (int i = 0; i < commerceItems.size(); i++) {
      merge.add(((CommerceItem) commerceItems.get(i)).getAuxiliaryData().getCatalogRef());
    }

    profile.setPropertyValue(pm.getItemsBought(), new ArrayList(merge));
  }
}
