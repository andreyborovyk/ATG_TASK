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
package atg.projects.store.order.purchase;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.order.purchase.PurchaseProcessFormHandler;
import atg.core.util.StringUtils;
import atg.droplet.DropletFormException;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.StoreOrderManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;


/**
 * This class is used to handle form submissions from the Gift
 * Message page.
 *
 * @author ATG
 * @version $Revision: #2 $
 */
public class GiftMessageFormHandler extends PurchaseProcessFormHandler {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/purchase/GiftMessageFormHandler.java#2 $$Change: 651448 $";

  /**
   * Gift message too long message key.
   */
  protected static final String GIFT_MESSAGE_TOO_LONG = "giftMessageTooLong";

  /**
   * No gift message message key.
   */
  protected static final String MSG_NO_GIFT_MSG = "noGiftMessage";

  /**
   * Error adding gift message message key.
   */
  protected static final String ERROR_ADDING_GIFT_MESSAGE = "errorAddingGiftMessage";

  /**
   * Add gify message success redirect URL.
   */
  protected String mAddGiftMessageSuccessURL;

  /**
   * Add gift message redirect URL.
   */
  protected String mAddGiftMessageErrorURL;

  /**
   * Gift message sender.
   */
  protected String mGiftMessageTo;

  /**
   * Gift message recipient.
   */
  protected String mGiftMessageFrom;

  /**
   * Gift message text.
   */
  protected String mGiftMessage;

  /**
   * Express checkout.
   */
  protected String mExpressCheckout;
  
  private String mCouponCode = null;

  public String getCouponCode() {
    return mCouponCode;
  }

  public void setCouponCode(String pCouponCode) {
    mCouponCode = pCouponCode;
  }

  /**
   * This method will add the gift message to the order's specialInstructions.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   *
   * @return boolean success or failure
   *
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleAddGiftMessage(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    Transaction tr = null;

    try {
      tr = ensureTransaction();
      //Check if session has expired, redirect to sessionExpired URL:
      if (!checkFormRedirect(null, getAddGiftMessageErrorURL(), pRequest, pResponse)) {
        return false;
      }
      
      // Claim coupon before adding gift message
      try {
        boolean couponTendered = ((StorePurchaseProcessHelper) getPurchaseProcessHelper()).tenderCoupon(
            getCouponCode(), (StoreOrderImpl) getOrder(), getProfile(), getUserPricingModels(), getUserLocale());
        if (!couponTendered) {
          String errorMessage = formatUserMessage(StorePurchaseProcessHelper.MSG_UNCLAIMABLE_COUPON, pRequest, pResponse);
          addFormException(new DropletFormException(errorMessage, "", StorePurchaseProcessHelper.MSG_UNCLAIMABLE_COUPON));
        }
      }
      catch (Exception exception) {
        processException(exception, StoreCartProcessHelper.MSG_UNCLAIMABLE_COUPON, pRequest, pResponse);
      }
      
      addGiftMessage(pRequest, pResponse);
    } catch (CommerceException ce) {
      // invalid number given for quantity of item to add
      String msg = formatUserMessage(ERROR_ADDING_GIFT_MESSAGE, pRequest, pResponse);
      addFormException(new DropletFormException(msg, ce, ERROR_ADDING_GIFT_MESSAGE));
      
      if (isLoggingError()) {
        logError("Failed adding gift message to order", ce);
      }
    } finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }

    return checkFormRedirect(getAddGiftMessageSuccessURL(), getAddGiftMessageErrorURL(), pRequest, pResponse);
  }

  /**
   * <p>Adds the gift message to the order.  It gets the gift message from the
   * <code>giftMessageTo</code>
   * <code>giftMessageFrom</code>
   * <code>giftMessage</code> parameters set on the form.
   *
   * @throws CommerceException should anything go wrong
   * @see StoreOrderManager.addGiftMessage
   * @param pRequest - http request
   * @param pResponse - http response
   * @throws java.io.IOException if IO error occurs
   * @throws javax.servlet.ServletException if servlet error occurs
   */
  protected void addGiftMessage(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws IOException, ServletException, CommerceException {
    if (!StringUtils.isBlank(getGiftMessageFrom()) && !StringUtils.isBlank(getGiftMessageTo())) {
      StoreOrderImpl order = (StoreOrderImpl) getOrder();
      StoreOrderManager om = (StoreOrderManager) getOrderManager();

      String message = getGiftMessage();

      if (message.length() > 140) {
        // Default message in case getting from bundle fails:
        String errorMsg = formatUserMessage(GIFT_MESSAGE_TOO_LONG, pRequest, pResponse);
        addFormException(new DropletFormException(errorMsg, "giftMessage", GIFT_MESSAGE_TOO_LONG));
      }

      if (!getFormError()) {
        // Let the message get set even on form error so the
        // user can see the input, and modify it. The limitation
        // is with SAP, and the message will be truncated in
        // fulfillment anyway.
        om.addGiftMessage(order, getGiftMessageTo(), getGiftMessage(), getGiftMessageFrom());
      }
    }
  }

  /**
   * @return add gift message success redirect URL.
   */
  public String getAddGiftMessageSuccessURL() {
    return mAddGiftMessageSuccessURL;
  }

  /**
   * @param pAddGiftMessageSuccessURL -
   * add gift message success resirect URL.
   */
  public void setAddGiftMessageSuccessURL(String pAddGiftMessageSuccessURL) {
    mAddGiftMessageSuccessURL = pAddGiftMessageSuccessURL;
  }

  /**
   * @return add gift message error redirect URL.
   */
  public String getAddGiftMessageErrorURL() {
    return mAddGiftMessageErrorURL;
  }

  /**
   * @param pAddGiftMessageErrorURL -
   * add gift message error redirect URL.
   */
  public void setAddGiftMessageErrorURL(String pAddGiftMessageErrorURL) {
    mAddGiftMessageErrorURL = pAddGiftMessageErrorURL;
  }

  /**
   * @param pGiftMessageTo - gift message sender.
   */
  public void setGiftMessageTo(String pGiftMessageTo) {
    mGiftMessageTo = pGiftMessageTo;
  }

  /**
   * @return mGiftMessageTo - gift message sender.
   */
  public String getGiftMessageTo() {
    return mGiftMessageTo;
  }

  /**
   * @param pGiftMessageFrom - gift message recipient.
   */
  public void setGiftMessageFrom(String pGiftMessageFrom) {
    mGiftMessageFrom = pGiftMessageFrom;
  }

  /**
   * @return gift message recipient.
   */
  public String getGiftMessageFrom() {
    return mGiftMessageFrom;
  }

  /**
   * @param pGiftMessage - gift message text.
   */
  public void setGiftMessage(String pGiftMessage) {
    mGiftMessage = pGiftMessage;
  }

  /**
   * @return gift message text.
   */
  public String getGiftMessage() {
    return mGiftMessage;
  }

  /**
   * @param pExpressCheckout - express checkout.
   */
  public void setExpressCheckout(String pExpressCheckout) {
    mExpressCheckout = pExpressCheckout;
  }

  /**
   * @return express checkout.
   */
  public String getExpressCheckout() {
    return mExpressCheckout;
  }
}
