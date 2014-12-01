/*
 * <ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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
 * </ATGCOPYRIGHT>
*/

package atg.projects.store.order.purchase;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.order.purchase.PurchaseProcessFormHandler;
import atg.droplet.DropletFormException;
import atg.projects.store.order.StoreOrderImpl;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * Extension of {@link PurchaseProcessFormHandler} for working with coupons.
 * This form handler processes add/edit/remove operations on order coupons.
 * 
 * @author ATG
 * @version $$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/purchase/StoreCouponFormHandler.java#2 $$$$Change: 651448 $$ 
 * @updated $$DateTime: 2011/06/07 13:55:45 $$$$Author: rbarbier $$
 */
public class StoreCouponFormHandler extends PurchaseProcessFormHandler {
  /**
   * Class version
   */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/purchase/StoreCouponFormHandler.java#2 $$Change: 651448 $";
  
  private String mCouponCode;
  private String mApplyCouponSuccessURL;
  private String mApplyCouponErrorURL;
  private String mEditCouponSuccessURL;
  private String mEditCouponErrorURL;
  private String mRemoveCouponSuccessURL;
  private String mRemoveCouponErrorURL;

  /**
   * This property contains a coupon code to be claimed for current order.
   * @return coupon code to be claimed
   */
  public String getCouponCode() {
    return mCouponCode;
  }
  
  public void setCouponCode(String pCouponCode) {
    mCouponCode = pCouponCode;
  }
  
  /**
   * This property contains an URL the request should be redirected when coupon successfully added
   * @return URL the request should be redirected when coupon successfully added
   */
  public String getApplyCouponSuccessURL() {
    return mApplyCouponSuccessURL;
  }

  public void setApplyCouponSuccessURL(String pApplyCouponSuccessURL) {
    mApplyCouponSuccessURL = pApplyCouponSuccessURL;
  }

  /**
   * This property contains an URL the request should be redirected when there is an error adding coupon
   * @return URL the request should be redirected when there is an error adding coupon
   */
  public String getApplyCouponErrorURL() {
    return mApplyCouponErrorURL;
  }

  public void setApplyCouponErrorURL(String pApplyCouponErrorURL) {
    mApplyCouponErrorURL = pApplyCouponErrorURL;
  }

  /**
   * This property contains an URL the request should be redirected when coupon successfully edited
   * @return URL the request should be redirected when coupon successfully edited
   */
  public String getEditCouponSuccessURL() {
    return mEditCouponSuccessURL;
  }

  public void setEditCouponSuccessURL(String pEditCouponSuccessURL) {
    mEditCouponSuccessURL = pEditCouponSuccessURL;
  }

  /**
   * This property contains an URL the request should be redirected when there is an error editing coupon
   * @return URL the request should be redirected when there is an error editing coupon
   */
  public String getEditCouponErrorURL() {
    return mEditCouponErrorURL;
  }

  public void setEditCouponErrorURL(String pEditCouponErrorURL) {
    mEditCouponErrorURL = pEditCouponErrorURL;
  }

  /**
   * This property contains an URL the request should be redirected when coupon successfully removed
   * @return URL the request should be redirected when coupon successfully removed
   */
  public String getRemoveCouponSuccessURL() {
    return mRemoveCouponSuccessURL;
  }

  public void setRemoveCouponSuccessURL(String pRemoveCouponSuccessURL) {
    mRemoveCouponSuccessURL = pRemoveCouponSuccessURL;
  }

  /**
   * This property contains an URL the request should be redirected when there is an error removing coupon
   * @return URL the request should be redirected when there is an error removing coupon
   */
  public String getRemoveCouponErrorURL() {
    return mRemoveCouponErrorURL;
  }

  public void setRemoveCouponErrorURL(String pRemoveCouponErrorURL) {
    mRemoveCouponErrorURL = pRemoveCouponErrorURL;
  }


  /**
   * Returns the Order property as StoreOrderImpl.
   *
   * @return an <code>Order</code> value
   */
  @Override
  public StoreOrderImpl getOrder() {
    return (StoreOrderImpl)super.getOrder();
  }


  /**
   * Claim the specified coupon, register a form exception if the coupon is invalid or an error occurs.
   * 
   * @param pRequest - current HTTP servlet request
   * @param pResponse - current HTTP servlet response
   *
   * @return true if coupon has been claimed; false otherwise
   *
   * @throws ServletException if an error occurred during claiming the coupon
   * @throws IOException if an error occurred during claiming the coupon
   */
  public boolean handleClaimCoupon(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {

    try {
      /*
       * Attempt to claim the specified coupon
       */
      boolean couponTendered = ((StorePurchaseProcessHelper) getPurchaseProcessHelper()).tenderCoupon(getCouponCode(),
                                                                                                      getOrder(),
                                                                                                      getProfile(),
                                                                                                      getUserPricingModels(),
                                                                                                      getUserLocale());

      /*
       * If the coupon could not be claimed add an error
       */
      if (!couponTendered) {
          String errorMessage = formatUserMessage(StorePurchaseProcessHelper.MSG_UNCLAIMABLE_COUPON,
                                                  pRequest, pResponse);

          addFormException(new DropletFormException(errorMessage,
                                                    "",
                                                    StorePurchaseProcessHelper.MSG_UNCLAIMABLE_COUPON));
      }
    }
    catch (CommerceException commerceException) {
      processException(commerceException,
                       StoreCartProcessHelper.MSG_UNCLAIMABLE_COUPON,
                       pRequest,
                       pResponse);
    }

    return checkFormRedirect(getApplyCouponSuccessURL(),
                             getApplyCouponErrorURL(),
                             pRequest, pResponse);
  }
}
