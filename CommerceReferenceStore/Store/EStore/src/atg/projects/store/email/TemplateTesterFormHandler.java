/*<ATGCOPYRIGHT>
 * Copyright (C) 2009-2011 Art Technology Group, Inc.
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
package atg.projects.store.email;

import java.util.Map;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.ShippingGroup;
import atg.core.util.StringUtils;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.profile.StoreRepositoryProfileItemFinder;
import atg.repository.RepositoryItem;
import atg.userprofiling.Profile;

/**
 * Email templates form handler.
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/email/TemplateTesterFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class TemplateTesterFormHandler extends GenericEmailSenderFormHandler {

  /** Class version string **/
  public static String CLASS_VERSION = 
  "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/email/TemplateTesterFormHandler.java#2 $$Change: 651448 $";

  public static final String USER_PROFILE_TYPE = "user";
  
  //--------------------------------------------------
  // property: NewPassword
  private String mNewPassword;

  /**
   * @return the String
   */
  public String getNewPassword() {
    return mNewPassword;
  }

  /**
   * @param NewPassword the String to set
   */
  public void setNewPassword(String pNewPassword) {
    mNewPassword = pNewPassword;
  }
  
  //--------------------------------------------------
  // property: ProfileItemFinder
  private StoreRepositoryProfileItemFinder mProfileItemFinder;

  /**
   * @return the ProfileItemFinder
   */
  public StoreRepositoryProfileItemFinder getProfileItemFinder() {
    return mProfileItemFinder;
  }

  /**
   * @param ProfileItemFinder the ProfileItemFinder to set
   */
  public void setProfileItemFinder(StoreRepositoryProfileItemFinder pProfileItemFinder) {
    mProfileItemFinder = pProfileItemFinder;
  }
  
  //--------------------------------------------------
  // property: OrderManager
  private OrderManager mOrderManager;

  /**
   * @return the OrderManager
   */
  public OrderManager getOrderManager() {
    return mOrderManager;
  }

  /**
   * @param OrderManager the OrderManager to set
   */
  public void setOrderManager(OrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }
  
  //--------------------------------------------------
  // property: Login
  private String mLogin;

  /**
   * @return the String
   */
  public String getLogin() {
    return mLogin;
  }

  /**
   * @param Login the String to set
   */
  public void setLogin(String pLogin) {
    mLogin = pLogin;
  }
  
  //--------------------------------------------------
  // property: Password
  private String mPassword;

  /**
   * @return the String
   */
  public String getPassword() {
    return mPassword;
  }

  /**
   * @param Password the String to set
   */
  public void setPassword(String pPassword) {
    mPassword = pPassword;
  }
  
  //--------------------------------------------------
  // property: OrderId
  private String mOrderId;

  /**
   * @return the String
   */
  public String getOrderId() {
    return mOrderId;
  }

  /**
   * @param OrderId the String to set
   */
  public void setOrderId(String pOrderId) {
    mOrderId = pOrderId;
  }
  
  //--------------------------------------------------
  // property: ProductId
  private String mProductId;

  /**
   * @return the String
   */
  public String getProductId() {
    return mProductId;
  }

  /**
   * @param ProductId the String to set
   */
  public void setProductId(String pProductId) {
    mProductId = pProductId;
  }
  
  //--------------------------------------------------
  // property: SkuId
  private String mSkuId;

  /**
   * @return the String
   */
  public String getSkuId() {
    return mSkuId;
  }

  /**
   * @param SkuId the String to set
   */
  public void setSkuId(String pSkuId) {
    mSkuId = pSkuId;
  }
  
  //--------------------------------------------------
  // property: ShippingGroupId
  private String mShippingGroupId;

  /**
   * @return the String
   */
  public String getShippingGroupId() {
    return mShippingGroupId;
  }

  /**
   * @param ShippingGroupId the String to set
   */
  public void setShippingGroupId(String pShippingGroupId) {
    mShippingGroupId = pShippingGroupId;
  }
  
  //--------------------------------------------------
  // property: OrderTools
  private StoreOrderTools mOrderTools;

  /**
   * @return the OrderTools
   */
  public StoreOrderTools getOrderTools() {
    return mOrderTools;
  }

  /**
   * @param OrderTools the OrderTools to set
   */
  public void setOrderTools(StoreOrderTools pOrderTools) {
    mOrderTools = pOrderTools;
  }
  
  //--------------------------------------------------
  // property: NewPasswordParameterName
  private String mNewPasswordParameterName = "newpassword";

  /**
   * @return the String
   */
  public String getNewPasswordParameterName() {
    return mNewPasswordParameterName;
  }

  /**
   * @param NewPasswordParameterName the String to set
   */
  public void setNewPasswordParameterName(String pNewPasswordParameterName) {
    mNewPasswordParameterName = pNewPasswordParameterName;
  }
  
  //--------------------------------------------------
  // property: ProductIdParameterName
  private String mProductIdParameterName = "productId";

  /**
   * @return the String
   */
  public String getProductIdParameterName() {
    return mProductIdParameterName;
  }

  /**
   * @param ProductIdParameterName the String to set
   */
  public void setProductIdParameterName(String pProductIdParameterName) {
    mProductIdParameterName = pProductIdParameterName;
  }
  
  //--------------------------------------------------
  // property: SkuIdParameterName
  private String mSkuIdParameterName = "skuId";

  /**
   * @return the String
   */
  public String getSkuIdParameterName() {
    return mSkuIdParameterName;
  }

  /**
   * @param SkuIdParameterName the String to set
   */
  public void setSkuIdParameterName(String pSkuIdParameterName) {
    mSkuIdParameterName = pSkuIdParameterName;
  }
  
  //--------------------------------------------------
  // property: OrderParameterName
  private String mOrderParameterName = "order";

  /**
   * @return the String
   */
  public String getOrderParameterName() {
    return mOrderParameterName;
  }

  /**
   * @param OrderParameterName the String to set
   */
  public void setOrderParameterName(String pOrderParameterName) {
    mOrderParameterName = pOrderParameterName;
  }
  
  //--------------------------------------------------
  // property: ShippingGroupParameterName
  private String mShippingGroupParameterName = "shippingGroup";

  /**
   * @return the String
   */
  public String getShippingGroupParameterName() {
    return mShippingGroupParameterName;
  }

  /**
   * @param ShippingGroupParameterName the String to set
   */
  public void setShippingGroupParameterName(String pShippingGroupParameterName) {
    mShippingGroupParameterName = pShippingGroupParameterName;
  }
  
  //--------------------------------------------------
  // property: OrderRecipientEmail
  private String mOrderRecipientEmail;

  /**
   * @return the String
   */
  public String getOrderRecipientEmail() {
    return mOrderRecipientEmail;
  }

  /**
   * @param orderRecipientEmail the String to set
   */
  public void setOrderRecipientEmail(String pOrderRecipientEmail) {
    mOrderRecipientEmail = pOrderRecipientEmail;
  }
  
  //--------------------------------------------------
  // property: OrderRecipientName
  private String mOrderRecipientName;

  /**
   * @return the String
   */
  public String getOrderRecipientName() {
    return mOrderRecipientName;
  }

  /**
   * @param orderRecipientName the String to set
   */
  public void setOrderRecipientName(String pOrderRecipientName) {
    mOrderRecipientName = pOrderRecipientName;
  }
  
  //--------------------------------------------------
  // property: OrderSenderName
  private String mOrderSenderName;

  /**
   * @return the String
   */
  public String getOrderSenderName() {
    return mOrderSenderName;
  }

  /**
   * @param OrderSenderName the String to set
   */
  public void setOrderSenderName(String pOrderSenderName) {
    mOrderSenderName = pOrderSenderName;
  }
  
  //--------------------------------------------------
  // property: OrderSenderEmail
  private String mOrderSenderEmail;

  /**
   * @return the String
   */
  public String getOrderSenderEmail() {
    return mOrderSenderEmail;
  }
  
  /**
   * @param OrderSenderEmail the String to set
   */
  public void setOrderSenderEmail(String pOrderSenderEmail) {
    mOrderSenderEmail = pOrderSenderEmail;
  }
  
  //--------------------------------------------------
  // property: OrderSubject
  private String mOrderSubject;

  /**
   * @return the String
   */
  public String getOrderSubject() {
    return mOrderSubject;
  }

  /**
   * @param OrderSubject the String to set
   */
  public void setOrderSubject(String pOrderSubject) {
    mOrderSubject = pOrderSubject;
  }
  
  //--------------------------------------------------
  // property: OrderMessage
  private String mOrderMessage;

  /**
   * @return the Type
   */
  public String getOrderMessage() {
    return mOrderMessage;
  }

  /**
   * @param OrderMessage the Type to set
   */
  public void setOrderMessage(String pOrderMessage) {
    mOrderMessage = pOrderMessage;
  }

  //--------------------------------------------------
  // property: EmailLocale
  private String mEmailLocale;
  
  public String getEmailLocale() {
    return mEmailLocale;
  }

  public void setEmailLocale(String pEmailLocale) {
    mEmailLocale = pEmailLocale;
  }

  //--------------------------------------------------
  // property: EmailLocale
  private String mEmailLocaleName = "locale";
  
  public String getEmailLocaleName() {
    return mEmailLocaleName;
  }

  public void setEmailLocaleName(String pEmailLocaleName) {
    mEmailLocaleName = pEmailLocaleName;
  }

  /* (non-Javadoc)
   * @see atg.projects.store.catalog.GenericEmailSenderFormHandler#getProfile()
   */
  @Override
  public Profile getProfile() {
    Profile profile = super.getProfile();
    
    //find profile using profile finder
    RepositoryItem repProfile = getProfileItemFinder().findByLogin(getLogin(), getPassword(), USER_PROFILE_TYPE);
    if(repProfile != null ) {
      profile.setDataSource(repProfile);
    }
    
    return profile;
  }

  /* (non-Javadoc)
   * @see atg.projects.store.catalog.GenericEmailSenderFormHandler#collectParams()
   */
  @Override
  protected Map collectParams() {
    Map emailParams = super.collectParams();
    
    // add params for testing purposes
    emailParams.put(getNewPasswordParameterName(), getNewPassword());
    emailParams.put(getProductIdParameterName(), getProductId());
    emailParams.put(getSkuIdParameterName(), getSkuId());
    // external recipient name
    emailParams.put(getRecipientNameParamName(), getRecipientName());
    
    // check for external locale
    String emailLocaleStr = getEmailLocale();
    if(!StringUtils.isEmpty(emailLocaleStr)) {
      emailParams.put(getEmailLocaleName(), emailLocaleStr);
    }
    
    // load order 
    String orderId = getOrderId();
    if(!StringUtils.isEmpty(orderId)) { 
      Order order;
      try {
        order = getOrderManager().loadOrder(orderId);
        emailParams.put(getOrderParameterName(), order);
        
        String shippingGroupId = getShippingGroupId();
        if(!StringUtils.isEmpty(shippingGroupId)) {
          ShippingGroup sg = order.getShippingGroup(shippingGroupId);
          emailParams.put(getShippingGroupParameterName(), sg);
        }
        
      } catch (CommerceException e) {
        logError(e);
      }
      
    }
    
    return emailParams;
  }
}
