/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

package atg.commerce.profile;

import atg.commerce.states.OrderStates;
import atg.commerce.states.StateDefinitions;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.OrderHolder;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.CommerceException;
import atg.commerce.promotion.PromotionTools;
import atg.nucleus.naming.ParameterName;
import atg.userprofiling.ProfileFormHandler;
import atg.scenario.userprofiling.ScenarioProfileFormHandler;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.repository.*;
import atg.servlet.RequestLocale;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import java.util.Locale;

/**
 * This form handler is a subclass of the DPS ProfileFormHandler to perform
 * some operations that are specific to Commerce. The additional functionality includes:
 * <P>
 * <UL>
 * <LI>During a login operation the active promotions from the anonymous user are copied into
 * the list of active promotions of the persistent user.
 * <LI>After the login operation is finished the session-scope PricingModelHolder is reinitialized
 * to cache all the promotions that the persistent user has accumulated.
 * <LI>During the registration and login process any shopping carts (i.e. Orders) which were created
 * while they were anonymous (and thus not persistent) are changed to be persistent.
 * <LI>If the user logs in any old shopping carts (i.e. Orders in an INCOMPLETE state) are loaded into
 * the user's session.
 *
 * @beaninfo
 *   description: A form handler that manages the current user's profile
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 * @author Bob Mason
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/profile/CommerceProfileFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public
class CommerceProfileFormHandler
extends ScenarioProfileFormHandler
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/profile/CommerceProfileFormHandler.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  static final ParameterName LOCALE_PARAM = ParameterName.getParameterName("locale");

  //-------------------------------------
  // Member Variables
  //-------------------------------------

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // property: ShoppingCart
  OrderHolder mShoppingCart;

  /**
   * Sets property ShoppingCart
   **/
  public void setShoppingCart(OrderHolder pShoppingCart) {
    mShoppingCart = pShoppingCart;
  }

  /**
   * Returns property ShoppingCart
   **/
  public OrderHolder getShoppingCart() {
    return mShoppingCart;
  }

  //-------------------------------------
  // property: UserPricingModels
  PricingModelHolder mUserPricingModels;

  /**
   * Sets property UserPricingModels
   **/
  public void setUserPricingModels(PricingModelHolder pUserPricingModels) {
    mUserPricingModels = pUserPricingModels;
  }

  /**
   * Returns property UserPricingModels
   **/
  public PricingModelHolder getUserPricingModels() {
    return mUserPricingModels;
  }

  //-------------------------------------
  // property: OrderManager
  OrderManager mOrderManager;

  /**
   * Sets property OrderManager
   **/
  public void setOrderManager(OrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  /**
   * Returns property OrderManager
   **/
  public OrderManager getOrderManager() {
    return mOrderManager;
  }

  //-------------------------------------
  // property: PromotionTools
  PromotionTools mPromotionTools;

  /**
   * Sets property PromotionTools
   **/
  public void setPromotionTools(PromotionTools pPromotionTools) {
    mPromotionTools = pPromotionTools;
  }

  /**
   * Returns property PromotionTools
   **/
  public PromotionTools getPromotionTools() {
    return mPromotionTools;
  }

  //-------------------------------------
  // property: DefaultLocale
  Locale mDefaultLocale;

  /**
   * Sets property DefaultLocale
   **/
  public void setDefaultLocale(Locale pDefaultLocale) {
    mDefaultLocale = pDefaultLocale;
  }

  /**
   * Returns property DefaultLocale. If the property value is null, then
   * JVM's default locale is returned.
   **/
  public Locale getDefaultLocale() {
    if (mDefaultLocale != null)
      return mDefaultLocale;
    else
      return Locale.getDefault();
  }

  //-------------------------------------
  // Constructors
  //-------------------------------------
  /**
   * Constructs an instanceof CommerceProfileFormHandler
   */
  public CommerceProfileFormHandler() {
  }

  /**
   * If the property being added is "activePromotions", then use promotionTools
   * to add it, otherwise call the super class.
   * 
   * @param pGuestUser the user to copy values from
   * @param pAuthenticatedUser the user to copy values to
   * @param pPropertyName the name of a multi-valued property whose values
   * will be copied from the guest user and added to the authenticated user
   **/
  public void addProperty(String pPropertyName,
                           RepositoryItem pGuestUser,
                           MutableRepositoryItem pAuthenticatedUser)
    throws RepositoryException 
  {
    CommerceProfileTools profileTools = (CommerceProfileTools) getProfileTools();
    PromotionTools tools = profileTools.getPromotionTools();
    
    if(tools == null)
      return;
      
    if (pPropertyName.equalsIgnoreCase(tools.getActivePromotionsProperty())) {
      profileTools.addActivePromotions(pGuestUser, pAuthenticatedUser);
    }
    else {
      super.addProperty(pPropertyName, pGuestUser, pAuthenticatedUser);
    }
  }

  /**
   * Returns the locale associated with the request. The method first searches
   * for a request paramater named <code>locale</code>. This value can be
   * either a java.util.Locale object or a String which represents the locale.
   * Next if the <code>useRequestLocale</code> property is true, then the locale
   * of the request will be returned. Finally, if the locale cannot be determined,
   * the the <code>defaultLocale</code> property is used.
   */
  public Locale getUserLocale(DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    // this method call is preserved for backward compatability. The logic is now in
    // CommerceProfileTools.
    CommerceProfileTools profileTools = (CommerceProfileTools) getProfileTools();
    return profileTools.getUserLocale(pRequest, pResponse);
  }

  /**
   * After registration, any transient orders are made persistent through the
   * <code>persistShoppingCarts</code> method of the CommerceProfileTools component
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void postCreateUser(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    super.postCreateUser(pRequest, pResponse);

    Boolean isCreated = (Boolean)pRequest.getObjectParameter(HANDLE_CREATE_PARAM);
    OrderHolder cart = getShoppingCart();
    RepositoryItem profile = getProfile();    
    CommerceProfileTools profileTools = (CommerceProfileTools) getProfileTools();
    if ((isCreated != null) && (isCreated.booleanValue())) {
     profileTools.postCreateUser(pRequest, pResponse, profile, cart);
    }
  }

  /**
   * After logging in the user's session cached promotions are reloaded into the PricingModelHolder.
   * In addition any non-transient orders are made persistent and old shopping carts are loaded
   * from the database.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void postLoginUser(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    super.postLoginUser(pRequest, pResponse);

    OrderHolder shoppingCart = getShoppingCart();
    Boolean isLoggedIn = (Boolean)pRequest.getObjectParameter(HANDLE_LOGIN_PARAM);
    PricingModelHolder holder = getUserPricingModels();
    CommerceProfileTools profileTools = (CommerceProfileTools) getProfileTools();
    RepositoryItem profile = getProfile();
    if ((isLoggedIn != null) && (isLoggedIn.booleanValue())) {
      profileTools.postLoginUser(pRequest, pResponse, profile, shoppingCart, holder);
    } 
  }

} // end of class
