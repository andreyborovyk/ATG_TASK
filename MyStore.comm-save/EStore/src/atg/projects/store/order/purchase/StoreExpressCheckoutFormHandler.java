/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2010 Art Technology Group, Inc.
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

import atg.commerce.CommerceException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CreditCard;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.purchase.ExpressCheckoutFormHandler;

import atg.core.util.ResourceUtils;

import atg.droplet.DropletFormException;

import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.StoreShippingGroupManager;
import atg.projects.store.profile.StorePropertyManager;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;


/**
 * This class is needed to get the billing address from the credit card
 * rather than the user's default billing address.
 *
 * @author ATG
 * @version $Version$
 * @see ExpressCheckoutFormHandler
 */
public class StoreExpressCheckoutFormHandler extends ExpressCheckoutFormHandler {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/purchase/StoreExpressCheckoutFormHandler.java#3 $$Change: 635816 $";

  /**
   * Store property manager.
   */
  private StorePropertyManager mStorePropertyManager;

  /**
   * Store express checkout process helper.
   */
  private StoreExpressCheckoutProcessHelper mExpressCheckoutHelper;

  private CheckoutProgressStates mCheckoutProgressStates;

  public CheckoutProgressStates getCheckoutProgressStates()
  {
    return mCheckoutProgressStates;
  }

  public void setCheckoutProgressStates(CheckoutProgressStates pCheckoutProgressStates)
  {
    mCheckoutProgressStates = pCheckoutProgressStates;
  }

  /**
   * @return the Store property manager property.
   */
  public StorePropertyManager getStorePropertyManager() {
    return mStorePropertyManager;
  }

  /**
   * @param pStorePropertyManager -
   * Store property manager.
   */
  public void setStorePropertyManager(StorePropertyManager pStorePropertyManager) {
    mStorePropertyManager = pStorePropertyManager;
  }

  /**
   * @return the Store express checkout property.
   */
  public StoreExpressCheckoutProcessHelper getExpressCheckoutHelper() {
    return mExpressCheckoutHelper;
  }

  /**
   * @param pExpressCheckoutHelper -
   * Store express checkout helper.
   */
  public void setExpressCheckoutHelper(StoreExpressCheckoutProcessHelper pExpressCheckoutHelper) {
    mExpressCheckoutHelper = pExpressCheckoutHelper;
  }

  /**
   * Property BillingHelper
   */
  private StoreBillingProcessHelper mBillingHelper;

  public StoreBillingProcessHelper getBillingHelper() {
    return mBillingHelper;
  }

  public void setBillingHelper(StoreBillingProcessHelper pBillingHelper) {
    mBillingHelper = pBillingHelper;
  }

  /**
   * Property AutoApplyStoreCredits
   */
  boolean mAutoApplyStoreCredits = false;

  /**
   * Set the AutoApplyStoreCredits property.
   * @param pAutoApplyStoreCredits a <code>boolean</code> value
   * @beaninfo description: Should profile's store credits be auto-applied to the order?
   *           Set to true for auto-applying.
   */
  public void setAutoApplyStoreCredits(boolean pAutoApplyStoreCredits) {
    mAutoApplyStoreCredits = pAutoApplyStoreCredits;
  }

  /**
   * Return the AutoApplyStoreCredits property.
   * @return a <code>boolean</code> value
   */
  public boolean isAutoApplyStoreCredits() {
    return mAutoApplyStoreCredits;
  }

  /**
   * This method overrides the super class to apply the address from
   * the credit card rather than from the user's "default billing".
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  protected void ensurePaymentGroup(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws IOException, ServletException {
    // We want to start clean - get rid of any lingering store
    // credits or whatever from previous checkout attempts
    try {
      getPaymentGroupManager().removeAllPaymentGroupsFromOrder(getOrder());
    } catch (CommerceException ce) {
      throw new ServletException(ce);
    }

    // This call creates the payment group
    PaymentGroup paymentGroup = getPaymentGroup();

    if ((paymentGroup == null) || (!(paymentGroup instanceof CreditCard))) {
      String msg = formatUserMessage(MSG_EXPRESS_CHECKOUT_ERROR, pRequest, pResponse);
      addFormException(new DropletFormException(msg, MSG_EXPRESS_CHECKOUT_ERROR));
    }

    // Copy the default credit card into the order
    RepositoryItem defaultCreditCard = (RepositoryItem) getProfile().getPropertyValue(getDefaultCreditCardPropertyName());

    if (defaultCreditCard == null) {
      if (isLoggingError()) {
        logError(ResourceUtils.getMsgResource(ERROR_MISSING_CREDIT_CARD, getResourceBundleName(), getResourceBundle()));
      }
    } else {
      getCommerceProfileTools().copyCreditCard(defaultCreditCard, (CreditCard) paymentGroup);
    }


    if (isAutoApplyStoreCredits()){
      try {

        getBillingHelper().setupStoreCreditPaymentGroupsForOrder(getOrder(), getProfile(), getBillingHelper().getStoreCreditIds(getProfile()));
      } catch (StorePurchaseProcessException exc) {

        String msg = ResourceUtils.getMsgResource(exc.getMessage(), getResourceBundleName(), getResourceBundle(getUserLocale(pRequest, pResponse)), exc.getParams());
        addFormException(new DropletFormException(msg,null));

      } catch (CommerceException ce) {
        processException(ce, StoreBillingProcessHelper.STORE_CREDIT_ERROR, pRequest, pResponse);

      } catch (Exception exc) {
        if (isLoggingError()) {
          logError(LogUtils.formatMajor(""), exc);
          logError(LogUtils.formatMajor("Error while processing Store Credit : " +
             exc.getMessage()));
        }
      }
    }
  }

  /**
   * This extends the base behavior to set the shipping method for all gift
   * shipping groups to the profile's default shipping method.
   *
   * In the case when user switches from multiple shipping to express checkout
   * all non-gift items are moved to default shipping group.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  protected void ensureShippingGroup(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws IOException, ServletException {
    super.ensureShippingGroup(pRequest, pResponse);

    // Move non gift items to default shipping group
    moveNonGiftItemsToDefaultShippingGroup();

    // set shipping method for gift shipping groups
    getExpressCheckoutHelper().ensureShippingMethodOfGiftShippingGroups(getOrder(), getProfile(), getDefaultShippingMethodPropertyName());
  }

  /**
   * Overrides base behavior to return non gift hardgood shipping group. If there is
   * no such shipping group in the order the new one is created and added to the order.
   *
   * @return a <code>ShippingGroup</code> value
   * @beaninfo description: The shipping group used during modifications to shipping groups
   *
   */
  public ShippingGroup getShippingGroup() {
    ShippingGroup sg = getShippingGroupManager().getFirstNonGiftHardgoodShippingGroup(getOrder());
    if (sg == null)
    {
      try {
        sg = getShippingGroupManager().createShippingGroup();
        getOrder().addShippingGroup(sg);
      } catch (CommerceException ex) {
        if (isLoggingError()) {
          logError(ex);
        }
      }
    }
    return sg;
  }

  /**
   * Checks whether order contains multiple non-gift shipping groups with relationships and
   * if so moves items from non gift shipping groups to the default shipping group.
   *
   * @throws ServletException if an error occurs
   */
  public void moveNonGiftItemsToDefaultShippingGroup() throws ServletException{
    // Determine the case when user switches from multiple shipping checkout to express checkout
    // with default shipping address, if so then move all commerce items from other hardgood
    // shipping groups to default one. Gift shipping groups are not modified.
    if (((StoreShippingGroupManager)getShippingGroupManager()).isMultipleNonGiftHardgoodShippingGroupsWithRelationships(getOrder())){
      // get default shipping group
      ShippingGroup defaultShippingGroup = getShippingGroup();
      String defaultShippingGroupId = defaultShippingGroup.getId();

      List shippingGroups = getShippingGroupManager().getNonGiftHardgoodShippingGroups(getOrder());
      for (Iterator sgIter = shippingGroups.iterator();sgIter.hasNext();){
        ShippingGroup sg = (ShippingGroup)sgIter.next();
        if (!defaultShippingGroupId.equals(sg.getId())){
          // not default shipping group so move all items from it to default shipping group
          try {
          List cis = getCommerceItemManager().getCommerceItemsFromShippingGroup(sg);

          for (Iterator cisIter = cis.iterator(); cisIter.hasNext();){
            CommerceItem ci = (CommerceItem)cisIter.next();
            ShippingGroupCommerceItemRelationship rel =
              getShippingGroupManager().getShippingGroupCommerceItemRelationship(getOrder(),
                                                                 ci.getId(),
                                                                 sg.getId());

              getExpressCheckoutHelper().moveItemBetweenShippingGroups(getOrder(), ci.getId(),
                                                                       rel.getQuantity(), sg.getId(), defaultShippingGroupId);

          }
          } catch (CommerceException ex) {
            throw new ServletException(ex);
          }
        }
      }
    }
  }



  @Override
  public void postExpressCheckout(DynamoHttpServletRequest request, DynamoHttpServletResponse response) throws ServletException,
      IOException
  {
    super.postExpressCheckout(request, response);
    if (mCheckoutProgressStates != null && !getFormError())
    {
      mCheckoutProgressStates.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CONFIRM.toString());
    }
  }
}
