/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2011 Art Technology Group, Inc.
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

package atg.projects.store.order;

import atg.commerce.CommerceException;

import atg.commerce.claimable.ClaimableManager;

import atg.commerce.order.*;

import atg.projects.store.logging.LogUtils;
import atg.projects.store.payment.StoreStoreCredit;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds utility methods for manipulating PaymentGroups.
 *
 * @author ATG
 * @version $Id: StorePaymentGroupManager.java,v 1.3 2004/07/29 18:36:11
 *          dbrandt Exp $
 */
public class StorePaymentGroupManager extends PaymentGroupManager {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/StorePaymentGroupManager.java#2 $$Change: 651448 $";

  /**
   * Claimable manager.
   */
  private ClaimableManager mClaimableManager;

  /**
   * Order manager.
   */
  private OrderManager mOrderManager;

  /**
   * Store order tools.
   */
  private StoreOrderTools mStoreOrderTools;

  /**
   * Store credit payment type.
   */
  private String mStoreCreditPaymentType;

  /**
   * @return the claimable manager.
   */
  public ClaimableManager getClaimableManager() {
    return mClaimableManager;
  }

  /**
   * Sets the claimable manager.
   * @param pClaimableManager - claimable manager.
   */
  public void setClaimableManager(ClaimableManager pClaimableManager) {
    mClaimableManager = pClaimableManager;
  }

  /**
   * @return the order manager.
   */
  public OrderManager getOrderManager() {
    return mOrderManager;
  }

  /**
   * Sets the order manager.
   * @param pOrderManager - order manager
   */
  public void setOrderManager(OrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  /**
   * @return the Store order tools.
   */
  public StoreOrderTools getStoreOrderTools() {
    return mStoreOrderTools;
  }

  /**
   * Sets the StoreOrderTools property.
   *
   * @param pStoreOrderTools -
   *            the Store order tools
   */
  public void setStoreOrderTools(StoreOrderTools pStoreOrderTools) {
    mStoreOrderTools = pStoreOrderTools;
  }

  /**
   * @return string representing the store credit payment type.
   */
  public String getStoreCreditPaymentType() {
    return mStoreCreditPaymentType;
  }

  /**
   * Sets the storeCreditPaymentType.
   *
   * @param pStoreCreditPaymentType - store credit payment type
   */
  public void setStoreCreditPaymentType(String pStoreCreditPaymentType) {
    mStoreCreditPaymentType = pStoreCreditPaymentType;
  }

  /**
   * Given an order, return all the store credit payment group ids.
   * @param pOrder - the order
   */
  public void removeStoreCreditPaymentGroups(Order pOrder) {
    List allPaymentGroups = pOrder.getPaymentGroups();
    ArrayList storeCreditIds = new ArrayList();
    int numPayGroups = allPaymentGroups.size();
    PaymentGroup currentPaymentGroup = null;
    String storeCreditType = getStoreCreditPaymentType();

    for (int i = 0; i < numPayGroups; i++) {
      currentPaymentGroup = (PaymentGroup) allPaymentGroups.get(i);

      if (currentPaymentGroup.getPaymentGroupClassType().equals(storeCreditType)) {
        storeCreditIds.add(currentPaymentGroup.getId());
      }
    }

    if (storeCreditIds.size() == 0) {
      return;
    }

    int numStoreCreditPaymentGroups = storeCreditIds.size();

    for (int j = 0; j < numStoreCreditPaymentGroups; j++) {
      try {
        removePaymentGroupFromOrder(pOrder, (String) storeCreditIds.get(j));
      } catch (CommerceException ce) {
        if (isLoggingError()) {
          logError(LogUtils.formatMajor("Could not remove payment group: " + ce));
        }
      }
    }
  }

  /**
   * This method will take an array of store credit ids, and create payment
   * groups and add them to the order. This will only create enough store
   * credit payment groups to cover the order costs. If more store credit ids
   * are passed in than are needed to cover the order, they are not used. On
   * the other hand, if not enough store credit ids are here to cover the
   * order costs, then this will search for a credit card and add the
   * remaining order amount to the credit card.
   *
   * @param pProfile - users profile
   * @param pOrder - the order
   * @param pOnlineCreditIds - online credit ids
   * @return true
   * @throws atg.commerce.CommerceException if an error occurs
   */
  public boolean initializePaymentMethods(RepositoryItem pProfile, Order pOrder, String[] pOnlineCreditIds)
    throws CommerceException {
    List onlineCredits = getOnlineCredits(pOnlineCreditIds);
    ClaimableManager cm = getClaimableManager();
    OrderManager om = getOrderManager();
    StoreOrderTools orderTools = getStoreOrderTools();

    // First get rid of the current store credit payment groups
    removeStoreCreditPaymentGroups(pOrder);

    // Also, remove the "remainingOrderAmount" from the credit card
    // if it exists.
    CreditCard cc = orderTools.getCreditCard(pOrder);

    if (cc != null) {
      try {
        List relationships = getAllPaymentGroupRelationships(pOrder);

        // The initial credit card on the order does not have a
        // relationship
        // with the order. If this is the case, don't try to remove the
        // amount from payment group.
        if ((relationships != null) && (relationships.size() != 0)) {
          om.removeRemainingOrderAmountFromPaymentGroup(pOrder, cc.getId());
        }
      } catch (CommerceException ce) {
        // Assume this is not an issue
        if (isLoggingError()) {
          logError(LogUtils.formatMajor("Failed attempt to remove amount from credit card: " + ce));
        }
      }
    }

    if (onlineCredits == null) {
      if (isLoggingDebug()) {
        logDebug("Online credits null.");
      }

      // Should never happen, this is only called when user is paying
      // with online credit.
      return true;
    }

    StoreStoreCredit storeCreditPaymentGroup = null;
    RepositoryItem creditItem = null;
    String storeCreditPaymentType = getStoreCreditPaymentType();
    String amountRemainingProperty = cm.getClaimableTools().getStoreCreditAmountRemainingPropertyName();

    double orderTotal = pOrder.getPriceInfo().getTotal();
    double remainingAmount = orderTotal;
    double onlineCreditAmount = 0.0;

    // Create and add store credit payment group(s) to order
    try {
      for (int i = 0; i < onlineCredits.size(); i++) {
        if (remainingAmount == 0.0) {
          return true;
        }

        creditItem = (RepositoryItem) onlineCredits.get(i);
        storeCreditPaymentGroup = (StoreStoreCredit) createPaymentGroup(storeCreditPaymentType);
        storeCreditPaymentGroup.setStoreCreditNumber(creditItem.getRepositoryId());
        storeCreditPaymentGroup.setProfileId(pProfile.getRepositoryId());
        addPaymentGroupToOrder(pOrder, storeCreditPaymentGroup);
        String amountAvailableProperty = getClaimableManager().getClaimableTools().getStoreCreditAmountAvailablePropertyName();
        onlineCreditAmount = ((Double)creditItem.getPropertyValue(amountAvailableProperty)).doubleValue();

        if (remainingAmount > onlineCreditAmount) {
          om.addOrderAmountToPaymentGroup(pOrder, storeCreditPaymentGroup.getId(), onlineCreditAmount);

          storeCreditPaymentGroup.setAmountAppliedToOrder(onlineCreditAmount);

          remainingAmount -= onlineCreditAmount;
        } else {
          // this onlineCredit will cover all the remaining costs of
          // the order.
          if (isLoggingDebug()) {
            logDebug("Online credit will pay for rest of order: " + storeCreditPaymentGroup.getId());
          }

          om.addRemainingOrderAmountToPaymentGroup(pOrder, storeCreditPaymentGroup.getId());
          storeCreditPaymentGroup.setAmountAppliedToOrder(remainingAmount);
          remainingAmount = 0.0;
        }
      }

      // Payment sufficient already validated, so add remainder to
      // credit card.
      if (remainingAmount > 0.0) {
        if (cc != null) {
          om.addRemainingOrderAmountToPaymentGroup(pOrder, cc.getId());
        }
      }

      om.updateOrder(pOrder);
    } catch (CommerceException ce) {
      // Make sure we capture in the logs where this happened, then
      // rethrow the exception.
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Exception creating online credit payment group: " + ce));
      }

      throw ce;
    }

    return true;
  }



  /**
   * This will return the online credits selected by the user.
   *
   * @param pOnlineCreditIds - online credit ids
   * @return List of online credits
   */
  public List getOnlineCredits(String[] pOnlineCreditIds) {
    ArrayList onlineCredits = new ArrayList();

    if (pOnlineCreditIds == null) {
      return onlineCredits;
    }

    try {
      RepositoryItem onlineCredit = null;

      ClaimableManager cm = getClaimableManager();
      Repository cr = cm.getClaimableTools().getClaimableRepository();
      String storeCreditItemDescName = cm.getClaimableTools().getStoreCreditItemDescriptorName();

      for (int j = 0; j < pOnlineCreditIds.length; j++) {
        onlineCredit = cr.getItem(pOnlineCreditIds[j], storeCreditItemDescName);
        onlineCredits.add(onlineCredit);
      }
    } catch (RepositoryException re) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error getting store credits." + re));
      }

      // UPDATE: handle exception!
    }

    return onlineCredits;
  }

  @Override
  public boolean isPaymentGroupUsed(Order pOrder, PaymentGroup pPaymentGroup) {
    /*
      If there is nothing to pay, we do not use any payment groups (if any).
      Otherwise we should apply common logic defined by DCS.
    */
    if (pOrder.getPriceInfo().getTotal() > 0) {
      return super.isPaymentGroupUsed(pOrder, pPaymentGroup);
    } else {
      return false;
    }
  }
}
