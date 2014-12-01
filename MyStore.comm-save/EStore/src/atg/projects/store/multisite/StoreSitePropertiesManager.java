/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2010 Art Technology Group, Inc.
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
package atg.projects.store.multisite;

/**
 * Properties manager for SiteRepository.
 *
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/multisite/StoreSitePropertiesManager.java#3 $$Change: 635816 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class StoreSitePropertiesManager {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/multisite/StoreSitePropertiesManager.java#3 $$Change: 635816 $";


  
  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // Property: catalogIdPropertyName
  //-------------------------------------
  protected String mCatalogIdPropertyName = "catalogId";

  public String getCatalogIdPropertyName() {
    return mCatalogIdPropertyName;
  }

  public void setCatalogIdPropertyName(String pCatalogIdPropertyName) {
    mCatalogIdPropertyName = pCatalogIdPropertyName;
  }

  //-------------------------------------
  // Property: listPriceListIdPropertyName
  //-------------------------------------
  protected String mListPriceListIdPropertyName = "listPriceListId";

  /**
   * @return the listPriceListIdPropertyName
   */
  public String getListPriceListIdPropertyName() {
    return mListPriceListIdPropertyName;
  }

  /**
   * @param pListPriceListIdPropertyName the listPriceListIdPropertyName to set
   */
  public void setListPriceListIdPropertyName(String pListPriceListIdPropertyName) {
    mListPriceListIdPropertyName = pListPriceListIdPropertyName;
  }

  //-------------------------------------
  // Property: salePriceListIdPropertyName
  //-------------------------------------
  protected String mSalePriceListIdPropertyName ="salePriceListId";

  /**
   * @return the salePriceListIdPropertyName
   */
  public String getSalePriceListIdPropertyName() {
    return mSalePriceListIdPropertyName;
  }

  /**
   * @param pSalePriceListIdPropertyName the salePriceListIdPropertyName to set
   */
  public void setSalePriceListIdPropertyName(String pSalePriceListIdPropertyName) {
    mSalePriceListIdPropertyName = pSalePriceListIdPropertyName;
  }
  
  //-------------------------------------
  // Property: resourceBundlePropertyName
  //-------------------------------------  
  protected String mResourceBundlePropertyName = "resourceBundle";
  
  /**
   * @return the resourceBundlePropertyName
   */
  public String getResourceBundlePropertyName() {
    return mResourceBundlePropertyName;
  }

  /**
   * @param pResourceBundlePropertyName the resourceBundlePropertyName to set
   */
  public void setResourceBundlePropertyName(String pResourceBundlePropertyName) {
    mResourceBundlePropertyName = pResourceBundlePropertyName;
  }

  //-------------------------------------
  // Property: newProductThresholdDaysPropertyName
  //-------------------------------------
  protected String mNewProductThresholdDaysPropertyName ="newProductThresholdDays";

  /**
   * @return the newProductThresholdDaysPropertyName
   */
  public String getNewProductThresholdDaysPropertyName() {
    return mNewProductThresholdDaysPropertyName;
  }

  /**
   * @param pNewProductThresholdDaysPropertyName the newProductThresholdDaysPropertyName to set
   */
  public void setNewProductThresholdDaysPropertyName(
      String pNewProductThresholdDaysPropertyName) {
    mNewProductThresholdDaysPropertyName = pNewProductThresholdDaysPropertyName;
  }
  
  //-------------------------------------
  // Property: defaultPageSizePropertyName
  //-------------------------------------  
  protected String mDefaultPageSizePropertyName = "defaultPageSize";

  /**
   * @return the defaultPageSizePropertyName
   */
  public String getDefaultPageSizePropertyName() {
    return mDefaultPageSizePropertyName;
  }

  /**
   * @param pDefaultPageSizePropertyName the defaultPageSizePropertyName to set
   */
  public void setDefaultPageSizePropertyName(String pDefaultPageSizePropertyName) {
    mDefaultPageSizePropertyName = pDefaultPageSizePropertyName;
  }
  
  //-------------------------------------
  // property: CssFilePropertyName
  //-------------------------------------
  protected String mCssFilePropertyName = "cssFile";

  /**
   * @return the String
   */
  public String getCssFilePropertyName() {
    return mCssFilePropertyName;
  }

  /**
   * @param CssFilePropertyName the String to set
   */
  public void setCssFilePropertyName(String pCssFilePropertyName) {
    mCssFilePropertyName = pCssFilePropertyName;
  }
  
  //-------------------------------------
  // property: DefaultCountryPropertyName
  //-------------------------------------
  protected String mDefaultCountryPropertyName = "defaultCountry";

  /**
   * @return the String
   */
  public String getDefaultCountryPropertyName() {
    return mDefaultCountryPropertyName;
  }

  /**
   * @param DefaultCountryPropertyName the String to set
   */
  public void setDefaultCountryPropertyName(String pDefaultCountryPropertyName) {
    mDefaultCountryPropertyName = pDefaultCountryPropertyName;
  }
  
  //-------------------------------------
  // property: EmailAFriendEnabledPropertyName
  //-------------------------------------
  protected String mEmailAFriendEnabledPropertyName = "emailAFriendEnabled";

  /**
   * @return the String
   */
  public String getEmailAFriendEnabledPropertyName() {
    return mEmailAFriendEnabledPropertyName;
  }

  /**
   * @param EmailAFriendEnabledPropertyName the String to set
   */
  public void setEmailAFriendEnabledPropertyName(String pEmailAFriendEnabledPropertyName) {
    mEmailAFriendEnabledPropertyName = pEmailAFriendEnabledPropertyName;
  }
  
  //-------------------------------------
  // property: BackInStockFromAddressPropertyName
  //-------------------------------------
  protected String mBackInStockFromAddressPropertyName = "backInStockFromAddress";

  /**
   * @return the String
   */
  public String getBackInStockFromAddressPropertyName() {
    return mBackInStockFromAddressPropertyName;
  }

  /**
   * @param BackInStockFromAddressPropertyName the String to set
   */
  public void setBackInStockFromAddressPropertyName(String pBackInStockFromAddressPropertyName) {
    mBackInStockFromAddressPropertyName = pBackInStockFromAddressPropertyName;
  }
  
  //-------------------------------------
  // property: NewPasswordFromAddressPropertyName
  //-------------------------------------
  protected String mNewPasswordFromAddressPropertyName = "newPasswordFromAddress";

  /**
   * @return the String
   */
  public String getNewPasswordFromAddressPropertyName() {
    return mNewPasswordFromAddressPropertyName;
  }

  /**
   * @param NewPasswordFromAddressPropertyName the String to set
   */
  public void setNewPasswordFromAddressPropertyName(String pNewPasswordFromAddressPropertyName) {
    mNewPasswordFromAddressPropertyName = pNewPasswordFromAddressPropertyName;
  }
  
  //-------------------------------------
  // property: OrderConfirmationFromAddressPropertyName
  //-------------------------------------
  protected String mOrderConfirmationFromAddressPropertyName = "orderConfirmationFromAddress";

  /**
   * @return the String
   */
  public String getOrderConfirmationFromAddressPropertyName() {
    return mOrderConfirmationFromAddressPropertyName;
  }

  /**
   * @param OrderConfirmationFromAddressPropertyName the String to set
   */
  public void setOrderConfirmationFromAddressPropertyName(String pOrderConfirmationFromAddressPropertyName) {
    mOrderConfirmationFromAddressPropertyName = pOrderConfirmationFromAddressPropertyName;
  }
  
  //-------------------------------------
  // property: OrderShippedFromAddressPropertyName
  //-------------------------------------
  protected String mOrderShippedFromAddressPropertyName = "orderShippedFromAddress";

  /**
   * @return the String
   */
  public String getOrderShippedFromAddressPropertyName() {
    return mOrderShippedFromAddressPropertyName;
  }

  /**
   * @param OrderShippedFromAddressPropertyName the String to set
   */
  public void setOrderShippedFromAddressPropertyName(String pOrderShippedFromAddressPropertyName) {
    mOrderShippedFromAddressPropertyName = pOrderShippedFromAddressPropertyName;
  }
  
  //-------------------------------------
  // property: PromotionEmailAddressPropertyName
  //-------------------------------------
  protected String mPromotionEmailAddressPropertyName = "promotionEmailAddress";

  /**
   * @return the String
   */
  public String getPromotionEmailAddressPropertyName() {
    return mPromotionEmailAddressPropertyName;
  }

  /**
   * @param PromotionEmailAddressPropertyName the String to set
   */
  public void setPromotionEmailAddressPropertyName(String pPromotionEmailAddressPropertyName) {
    mPromotionEmailAddressPropertyName = pPromotionEmailAddressPropertyName;
  }
  
  //-------------------------------------
  // property: BillableCountriesPropertyName
  //-------------------------------------
  protected String mBillableCountriesPropertyName = "billableCountries";

  /**
   * @return the String
   */
  public String getBillableCountriesPropertyName() {
    return mBillableCountriesPropertyName;
  }

  /**
   * @param BillableCountriesPropertyName the String to set
   */
  public void setBillableCountriesPropertyName(String pBillableCountriesPropertyName) {
    mBillableCountriesPropertyName = pBillableCountriesPropertyName;
  }
  
  //-------------------------------------
  // property: ShippableCountriesPropertyName
  //-------------------------------------
  protected String mShippableCountriesPropertyName = "shippableCountries";

  /**
   * @return the String
   */
  public String getShippableCountriesPropertyName() {
    return mShippableCountriesPropertyName;
  }

  /**
   * @param ShippableCountriesPropertyName the String to set
   */
  public void setShippableCountriesPropertyName(String pShippableCountriesPropertyName) {
    mShippableCountriesPropertyName = pShippableCountriesPropertyName;
  }
  
  //-------------------------------------
  // property: NonShippableCountriesPropertyName
  //-------------------------------------
  protected String mNonShippableCountriesPropertyName = "nonShippableCountries";

  /**
   * @return the String
   */
  public String getNonShippableCountriesPropertyName() {
    return mNonShippableCountriesPropertyName;
  }

  /**
   * @param NonShippableCountriesPropertyName the String to set
   */
  public void setNonShippableCountriesPropertyName(String pNonShippableCountriesPropertyName) {
    mNonShippableCountriesPropertyName = pNonShippableCountriesPropertyName;
  }
  
  //-------------------------------------
  // property: NonBillableCountriesPropertyName
  //-------------------------------------
  protected String mNonBillableCountriesPropertyName = "nonBillableCountries";

  /**
   * @return the String
   */
  public String getNonBillableCountriesPropertyName() {
    return mNonBillableCountriesPropertyName;
  }

  /**
   * @param NonBillableCountriesPropertyName the String to set
   */
  public void setNonBillableCountriesPropertyName(String pNonBillableCountriesPropertyName) {
    mNonBillableCountriesPropertyName = pNonBillableCountriesPropertyName;
  }
}
