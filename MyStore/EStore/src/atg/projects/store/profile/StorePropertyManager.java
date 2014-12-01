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
package atg.projects.store.profile;

import atg.commerce.profile.CommercePropertyManager;


/**
 * Store-specific extenstions with names of extended profile properties.
 *
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/profile/StorePropertyManager.java#3 $
 */
public class StorePropertyManager extends CommercePropertyManager {
  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/profile/StorePropertyManager.java#3 $$Change: 635816 $";

  /**
   * Back in stock notify item descriptor name.
   */
  String mBackInStockNotifyItemDescriptorName = "backInStockNotifyItem";

  /**
   * Bisn sku id property name.
   */
  String mBisnSkuIdPropertyName = "catalogRefId";

  /**
   * Bisn product id property name.
   */
  String mBisnProductIdPropertyName = "productId";

  /**
   * Bisn e-mail property name.
   */
  String mBisnEmailPropertyName = "emailAddress";

  /**
   * Date of birth property name.
   */
  String mDateOfBirthPropertyName = "dateOfBirth";

  /**
   * E-mail recipient item descriptor name.
   */
  protected String mEmailRecipientItemDescriptorName;

  /**
   * E-mail recipient property name.
   */
  protected String mEmailRecipientPropertyName;

  /**
   * Source code property name.
   */
  protected String mSourceCodePropertyName;
  
  /**
   * Source code property name.
   */
  protected String mUserIdPropertyName;

  /**
   * Date of the last purchase.
   */
  protected String mLastPurchaseDate = "lastPurchaseDate";

  /**
   * Bought items constant.
   */
  protected String mItemsBought = "itemsBought";

  /**
   * Number of orders constant. 
   */
  protected String mNumberOfOrders = "numberOfOrders";

  /** 'receivePromoEmail' property name */
  private String mReceivePromoEmailPropertyName = "receivePromoEmail";
  
  public String getReceivePromoEmailPropertyName() {
    return mReceivePromoEmailPropertyName;
  }

  public void setReceivePromoEmailPropertyName(
      String pReceivePromoEmailPropertyName) {
    mReceivePromoEmailPropertyName = pReceivePromoEmailPropertyName;
  }

  private String mWishlistPropertyName = "wishlist";
  
  public String getWishlistPropertyName()
  {
    return mWishlistPropertyName;
  }
  
  public void setWishlistPropertyName(String pWishlistPropertyName)
  {
    mWishlistPropertyName = pWishlistPropertyName;
  }

  /**
  * @param pDateOfBirthPropertyName - date of birth property name.
  */
  public void setDateOfBirthPropertyName(String pDateOfBirthPropertyName) {
    mDateOfBirthPropertyName = pDateOfBirthPropertyName;
  }

  /**
   * @return date of birth property name.
   */
  public String getDateOfBirthPropertyName() {
    return mDateOfBirthPropertyName;
  }

  /**
   * @param pEmailRecipientItemDescriptorName - e-mail recipient item
   * descriptor name.
   */
  public void setEmailRecipientItemDescriptorName(String pEmailRecipientItemDescriptorName) {
    mEmailRecipientItemDescriptorName = pEmailRecipientItemDescriptorName;
  }

  /**
   * @return mEmailRecipientItemDescriptorName - e-mail recipient
   * item descriptor name.
   */
  public String getEmailRecipientItemDescriptorName() {
    return mEmailRecipientItemDescriptorName;
  }

  /**
   * @param pEmailRecipientPropertyName - e-mail recipient
   * property name.
   */
  public void setEmailRecipientPropertyName(String pEmailRecipientPropertyName) {
    mEmailRecipientPropertyName = pEmailRecipientPropertyName;
  }

  /**
   * @return mEmailRecipientPropertyName - e-mail recipient property name.
   */
  public String getEmailRecipientPropertyName() {
    return mEmailRecipientPropertyName;
  }

  /**
   * @return source code property name.
   */
  public String getSourceCodePropertyName() {
    return mSourceCodePropertyName;
  }

  /**
   * @param pSourceCodePropertyName - source code
   * property name.
   */
  public void setSourceCodePropertyName(String pSourceCodePropertyName) {
    mSourceCodePropertyName = pSourceCodePropertyName;
  }
  
  /**
   * @return user id property name.
   */
  public String getUserIdPropertyName() {
    return mUserIdPropertyName;
  }

  /**
   * @param pUserIdPropertyName - user id
   * property name.
   */
  public void setUserIdPropertyName(String pUserIdPropertyName) {
    mUserIdPropertyName = pUserIdPropertyName;
  }

  /**
   * @return backInStockNotifyItem - back in stock
   * notify item.
   */
  public String getBackInStockNotifyItemDescriptorName() {
    return mBackInStockNotifyItemDescriptorName;
  }

  /**
   * @param pBackInStockNotifyItemDescriptorName -
   * back in stock notify item descriptor name.
   */
  public void setBackInStockNotifyItemDescriptorName(String pBackInStockNotifyItemDescriptorName) {
    mBackInStockNotifyItemDescriptorName = pBackInStockNotifyItemDescriptorName;
  }

  /**
   * @return bisn e-mail property name.
   */
  public String getBisnEmailPropertyName() {
    return mBisnEmailPropertyName;
  }

  /**
   * @param pBisnEmailPropertyName - bisn e-mail
   * property name.
   */
  public void setBisnEmailPropertyName(String pBisnEmailPropertyName) {
    mBisnEmailPropertyName = pBisnEmailPropertyName;
  }

  /**
   * @return bisn sku id property name.
   */
  public String getBisnSkuIdPropertyName() {
    return mBisnSkuIdPropertyName;
  }

  /**
   * @param pBisnSkuIdPropertyName - bisn
   * sku id property name.
   */
  public void setBisnSkuIdPropertyName(String pBisnSkuIdPropertyName) {
    mBisnSkuIdPropertyName = pBisnSkuIdPropertyName;
  }

  /**
   * @return bisn product id property name.
   */
  public String getBisnProductIdPropertyName() {
    return mBisnProductIdPropertyName;
  }

  /**
   * @param pBisnProductIdPropertyName - bisn
   * property product id property name.
   */
  public void setBisnProductIdPropertyName(String pBisnProductIdPropertyName) {
    mBisnProductIdPropertyName = pBisnProductIdPropertyName;
  }

  /**
   * @return items bought.
   */
  public String getItemsBought() {
    return mItemsBought;
  }

  /**
   * @param pItemsBought - items bought.
   */
  public void setItemsBought(String pItemsBought) {
    mItemsBought = pItemsBought;
  }

  /**
   * @return last purchase date.
   */
  public String getLastPurchaseDate() {
    return mLastPurchaseDate;
  }

  /**
   * @param pLastPurchaseDate - the date of last purchase.
   */
  public void setLastPurchaseDate(String pLastPurchaseDate) {
    mLastPurchaseDate = pLastPurchaseDate;
  }

  /**
   * @return number of orders.
   */
  public String getNumberOfOrders() {
    return mNumberOfOrders;
  }

  /**
   * @param pNumberOfOrders - number of orders.
   */
  public void setNumberOfOrders(String pNumberOfOrders) {
    mNumberOfOrders = pNumberOfOrders;
  }
  
  protected String mNewCreditCard = "newCreditCard";

  public String getNewCreditCard() {
    return mNewCreditCard;
  }

  public void setNewCreditCard(String pNewCreditCard) {
    mNewCreditCard = pNewCreditCard;
  }
  
  protected String mGenderPropertyName = "gender";
  
  public String getGenderPropertyName() {
    return mGenderPropertyName;
  }

  public void setGenderPropertyName(String pGenderPropertyName) {
    mGenderPropertyName = pGenderPropertyName;
  }

  protected String mRefferalSourcePropertyName = "referralSource";
  
  public String getRefferalSourcePropertyName() {
    return mRefferalSourcePropertyName;
  }

  public void setRefferalSourcePropertyName(String pRefferalSourcePropertyName) {
    mRefferalSourcePropertyName = pRefferalSourcePropertyName;
  }
}
