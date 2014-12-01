/*<ATGCOPYRIGHT>
 * Copyright (C) 2000-2011 Art Technology Group, Inc.
 * All Rights Reserved.	 No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.	 This notice must be
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

package atg.commerce.order;

import atg.repository.RepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.droplet.GenericFormHandler;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.core.util.ResourceUtils;
import atg.core.util.Address;
import atg.service.pipeline.PipelineManager;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import atg.beans.DynamicBeans;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.CommerceException;
import atg.commerce.pricing.*;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.claimable.ClaimableException;
import atg.beans.PropertyNotFoundException;
import atg.commerce.claimable.ClaimableTools;
import atg.commerce.claimable.ClaimableManager;
import atg.commerce.profile.CommerceProfileTools;
import atg.commerce.profile.CommercePropertyManager;

import java.io.IOException;
import javax.servlet.ServletException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Calendar;
import java.beans.IntrospectionException;
import javax.transaction.*;


/**
 * The FullShoppingCartFormHandler extends the functionality of the @see ShoppingCartFormHandler
 * by giving the additional functionality of handling multiple payment method (GiftCertificates
 * and Credit Cards), multiple location ship to addresses, express checkout and adding an
 * item to a person's gift list. Added handle methods are:
 *
 * <P>
 * <UL>
 *   <LI>@see #handleAddGiftItemToOrder - This method is responsible for adding an item
 *       from a catalog page to the user's gift list.
 *   <LI>@see #handleCreateAddress - This method is responsible for creating secondary
 *       addresses in the user's Profile.  It creates an address with the nickname specified
 *       by newShippingAddressName property.
 *   <LI>@see #handleShipToMultiple - If the user chooses to ship to multiple locations
 *       then the default shipping group in the order needs to be named.  That is, the
 *       ShippingGroups description field needs to be set so that it matches a named address
 *       in the user's profile.  This way, it can be displayed on the selection page and match
 *       up to a named address.  The nickname that it is given is set by the <code>
 *       defaultShippingAddrName</code> specified by the @see propertyManager.
 *   <LI>@see #handleMoveToNewShippingAddress - handle the moving of a particular quantity
 *       of a CommerceItem to a new shippingGroup.  It expects to have the following
 *       properties set in order for this move to occur: originalShippingAddressName,
 *       newShippingAddressName, commerceItemIdToEdit, and quantityToMove.
 *   <LI>@see #handleShipToMultipleDone - handles any processing that needs to be done
 *       when a user is done specifying their multiple ship to locations.  This currently
 *       does not do any processing, but provides a hook if something needs to be done in
 *       the future.
 *  </UL>
 *
 * <P>
 *
 * The FullShoppingCartFormHandler follows the same pattern as the ShoppingCartFormHandler
 * where each handleXXX method provides a hook into additional functionality via preXXX
 * and postXXX method.
 *
 * <P>
 *
 * This FormHandler provides an example of how this can be used.  The @see
 * #preMoveToConfirmation method provides the additional functionality of
 * interacting with the user's saved credit cards and also handling the possibility
 * of multiple payment methods.  First, a call to the super preMoveToConfirmation
 * is called, then the method provides the additional functionality supplied by
 * this FormHandler.
 *
 *
 * @beaninfo
 *   description: A form handler which can be used to modify the shopping cart
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 * @see atg.commerce.order.ShoppingCartFormHandler
 * @see atg.droplet.GenericFormHandler
 * @author Ashley J. Streb
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/FullShoppingCartFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class FullShoppingCartFormHandler
  extends ShoppingCartFormHandler
{

  //-------------------------------------
  // Class version string

  /** Class version String */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/FullShoppingCartFormHandler.java#2 $$Change: 651448 $";
  
  //--------------------------------------------------
  // Constants

  protected static final String MSG_ERROR_MOVING_ITEM_TO_NEW_ADDRESS = "errorMovingItemToNewAddress";
  protected static final String MSG_ADDRESS_ALREADY_EXISTS = "errorAddressExists";
  protected static final String MSG_ERROR_ADDING_ADDRESS = "errorAddingAddress";
  protected static final String MSG_NO_NEW_ADDRESS_ENTERED = "noNewAddressEntered";
  protected static final String MSG_ERROR_UNABLE_TO_ADD_CC = "errorAddingCCToProfile";
  protected static final String MSG_UNABLE_TO_COPY_CREDIT_CARD = "errorCopyingCreditCard";
  protected static final String MSG_NO_NICKNAME = "noNickName";
  protected static final String MSG_NO_CREDIT_CARD_NUMBER = "noCreditCardNumber";
  protected static final String SECONDARY_ADDRESS_PROPERTY_NAME = "secondaryAddresses";
  protected static final String DEFAULT_CREDIT_CARD = "defaultCreditCard";
  protected static final String CONTACT_INFO_ITEM_NAME = "contactInfo";
  protected static final String BILLING_ADDR_PROP_NAME = "My Billing Address";
  protected static final String NO_CREDIT_CARD = "noCreditCard";
  protected static final String UNSUPPORTED_PAYMENTGROUP = "unsupportedPaymentGroup";
  protected static final String UNEQUAL_GC_SIZE = "unequalGiftCertificateListSize";
  protected static final String ERROR_MISSING_BILLING_ADDRESS = "missingProfileDefaultShippingAddress";
  protected static final String ERROR_MISSING_CREDIT_CARD = "missingProfileDefaultCreditCard";
  protected static final String[] CREDIT_CARD_PROPERTIES = {"creditCardNumber"};
  protected static final String[] GIFT_CERTIFICATE_PROPERTIES = {"giftCertificateNumber"};

  protected static final String NEW = "new";

  protected static final String REDUNDANT_CREDIT_CARD="redundantCreditCard";

  //--------------------------------------------------
  // Member Variables

  //--------------------------------------------------
  // Properties

  /**
   * Sets property Configuration, and in the process configures the following properties:
   * <UL>
   * <LI>ClaimableManager
   * <LI>ClaimableTools
   * <LI>PropertyManager
   * <LI>CommerceProfileTools
   * </UL>
   * Plus the properties in the super-class.
   * @beaninfo description: The configuration for the shopping cart form handler
   **/
  public void setConfiguration(ShoppingCartModifierConfiguration pConfiguration) {
    super.setConfiguration(pConfiguration);
    if (getConfiguration() != null) {
      setValidateShippingGroupsChainId(mConfiguration.getValidateShippingGroupsChainId());
      setClaimableManager(mConfiguration.getClaimableManager());
      setClaimableTools(mConfiguration.getClaimableTools());
      setPropertyManager(mConfiguration.getPropertyManager());
      setCommerceProfileTools(mConfiguration.getCommerceProfileTools());

    }
  }

  //---------------------------------------------------------------------------
  // property: giftCertificateProperties


  /**
   * This is a list of properties that will be checked by the system to
   * determine if a gift certificate is "empty.".  By default, it is set
   * to the constant FullShoppingCartFormHandler.GIFT_CERTIFICATE_PROPERTIES.
   */
  String[] mGiftCertificateProperties = GIFT_CERTIFICATE_PROPERTIES;

  /**
   * Set the giftCertificateProperties property.
   */
  public void setGiftCertificateProperties(String[] pGiftCertificateProperties) {
    mGiftCertificateProperties = pGiftCertificateProperties;
  }

  /**
   * Return the giftCertificateProperties property.
   * @beaninfo description: The list of properties that will be checked to determine if a
   *                        gift certificate is empty.
   */
  public String[] getGiftCertificateProperties() {
    return mGiftCertificateProperties;
  }

  //---------------------------------------------------------------------------
  // property: creditCardProperties

  /**
   * This is a list of properties that will be checked by the system to
   * determine if a credit card is "empty.".  By default, it is set
   * to the constant FullShoppingCartFormHandler.CREDIT_CARD_PROPERTIES
   */
  String[] mCreditCardProperties = CREDIT_CARD_PROPERTIES;

  /**
   * Set the creditCardProperties property.
   */
  public void setCreditCardProperties(String[] pCreditCardProperties) {
    mCreditCardProperties = pCreditCardProperties;
  }

  /**
   * Return the creditCardProperties property.
   * @beaninfo description: The list of properties that will be checked to determine if a
   *                        credit card is empty.
   */
  public String[] getCreditCardProperties() {
    return mCreditCardProperties;
  }

  //---------------------------------------------------------------------------
  // property: validateShippingGroupsChainId

  /**
   * The name of the chain that is used to validate shipping groups in an order
   */
  String mValidateShippingGroupsChainId;

  /**
   * Set the validateShippingGroupsChainId property.
   */
  public void setValidateShippingGroupsChainId(String pValidateShippingGroupsChainId) {
    mValidateShippingGroupsChainId = pValidateShippingGroupsChainId;
  }

  /**
   * Return the validateShippingGroupsChainId property.
   * @beaninfo description: The name of the chain used to validate shipping groups in an order
   */
  public String getValidateShippingGroupsChainId() {
    return mValidateShippingGroupsChainId;
  }

  //---------------------------------------------------------------------------
  // property: PropertyManager

  /**
   * Holds common property names in one central location.
   */
  CommercePropertyManager mPropertyManager;

  /**
   * Set the PropertyManager property.
   */
  public void setPropertyManager(CommercePropertyManager pPropertyManager) {
    mPropertyManager = pPropertyManager;
  }

  /**
   * Return the PropertyManager property.
   * @beaninfo description: Holds commong property names in one central location
   */
  public CommercePropertyManager getPropertyManager() {
    return mPropertyManager;
  }


  //---------------------------------------------------------------------------
  // property: CommerceProfileTools

  /**
   * Class that allows manipulation of the Profile object.  Any profile massaging
   * is done through this class.
   */
  CommerceProfileTools mCommerceProfileTools;

  /**
   * Set the CommerceProfileTools property.
   */
  public void setCommerceProfileTools(CommerceProfileTools pCommerceProfileTools) {
    mCommerceProfileTools = pCommerceProfileTools;
  }

  /**
   * Return the CommerceProfileTools property.
   * @beaninfo description: Class that allows manipulation of the Profile object.
   */
  public CommerceProfileTools getCommerceProfileTools() {
    return mCommerceProfileTools;
  }

  //---------------------------------------------------------------------------
  // property: ClaimableManager

  /**
   * Allows interaction with the claimable system.  Specifically, allowing use of
   * gift certificates.
   */
  ClaimableManager mClaimableManager;

  /**
   * Set the ClaimableManager property.
   */
  public void setClaimableManager(ClaimableManager pClaimableManager) {
    mClaimableManager = pClaimableManager;
  }

  /**
   * Return the ClaimableManager property.
   * @beaninfo description: Allos interaction with the claimable system
   */
  public ClaimableManager getClaimableManager() {
    return mClaimableManager;
  }

  //---------------------------------------------------------------------------
  // property: ClaimableTools

  /**
   * Low level tools package that also contains common property names of claimable items.
   */
  ClaimableTools mClaimableTools;

  /**
   * Set the ClaimableTools property.
   */
  public void setClaimableTools(ClaimableTools pClaimableTools) {
    mClaimableTools = pClaimableTools;
  }

  /**
   * Return the ClaimableTools property.
   * @beaninfo description: Low level tools package that also contains common property names of claimable items.
   */
  public ClaimableTools getClaimableTools() {
    return mClaimableTools;
  }

  //-----------------------------------------
  // property: AddressesToCopy
  //-----------------------------------------

  /** Array of addresses that are in order ShippingGroup(s) that need to be copied to Profile */
  String[] mAddressesToCopy;

  /**
   * Set the addresses that need to be copied from the ShippingGroup objects to the
   * Profile "secondary addresses" map.
   *
   * @param pAddressesToCopy the names of the addresses to copy
   */
  public void setAddressesToCopy(String[] pAddressesToCopy) {
    mAddressesToCopy = pAddressesToCopy;
  }

  /**
   * Get the array of addresses to copy.
   *
   * @beaninfo description: The array of address to copy
   * @return the name of addresses to copy
   */
  public String[] getAddressesToCopy() {
    return mAddressesToCopy;
  }

  //---------------------------------------------------------------------------
  // property: SelectedCreditCardName

  /**
   * The selected credit card that the user would like to pay for their order with.
   * This will either be a nickname for a credit card in the user's list of credit cards
   * or it will be set to FullShoppingCartFormHandler.NEW indicating that a new
   * credit card is being used.
   */
  String mSelectedCreditCardName;

  /**
   * Set the SelectedCreditCardName property.
   */
  public void setSelectedCreditCardName(String pSelectedCreditCardName) {
    mSelectedCreditCardName = pSelectedCreditCardName;
  }

  /**
   * Return the SelectedCreditCardName property.
   * @beaninfo description: The selected credit card that the user would like to use.
   */
  public String getSelectedCreditCardName() {
    return mSelectedCreditCardName;
  }

  //-------------------------------------
  // property: NewShippingAddressName
  //-------------------------------------


  /**
   * The nickname of the address that the commerce items should be sent to.
   * This address is then extracted out of the user's profile and copied to
   * the new shippingGroup.
   */
  String mNewShippingAddressName;

  /**
   * Sets property which contains the Shipping Address name
   * where Commerce Items should be shipped to.  Used when moving
   * CommerceItems from one shipping group to another.
   **/
  public void setNewShippingAddressName(String pNewShippingAddressName) {
    mNewShippingAddressName = pNewShippingAddressName;
  }

  /**
   * Returns property NewShippingAddressName
   * @beaninfo description: The nickname of the address that the commerce items should be sent to.
   **/
  public String getNewShippingAddressName() {
    return mNewShippingAddressName;
  }


  //-------------------------------------
  // property: OriginalShippingAddressName
  //-------------------------------------

  /**
   * Indicates the nickname of the address that the user had their commerce items
   * in.  This allows the handleMoveToNewShippingAddress method to short circuit
   * moving items if the original and newShippingAddressName are the same.
   */
  String mOriginalShippingAddressName;

  /**
   * Sets property OriginalShippingAddressName.  This property
   * contains the original value for the Address name
   * a Commerce Item was associated with.  This value
   * is used to determine if the user moved the Commerce
   * Item to a new shipping address.
   **/
  public void setOriginalShippingAddressName(String pOriginalShippingAddressName) {
    mOriginalShippingAddressName = pOriginalShippingAddressName;
  }

  /**
   * Returns property OriginalShippingAddressName
   * @beaninfo description: The origianl value for the address name a CommerceItem was associated with
   **/
  public String getOriginalShippingAddressName() {
    return mOriginalShippingAddressName;
  }

  //-------------------------------------
  // property: QuantityToMove
  //-------------------------------------

  /**
   * The number of a particular commerce item that a user is going to move from one
   * shipping group to another.  This is used during the handleMoveToNewShippingAddress
   * method.
   */
  int mQuantityToMove = 0;

  /**
   * Sets property QuantityToMove.  This contains the quantity
   * of Commerce Items the user wants to move from one shipping
   * group to another.
   **/
  public void setQuantityToMove(int pQuantityToMove) {
    mQuantityToMove = pQuantityToMove;
  }

  /**
   * Returns property QuantityToMove
   * @beaninfo description: The number of a particular commerce item that a user is going to move from
   *                        one shipping group to another
   **/
  public int getQuantityToMove() {
    return mQuantityToMove;
  }

  //---------------------------------
  // property: ShipToAddressName
  //--------------------------------

  /**
   * If a user is going to ship all of their items to a single address, then the addrss
   * named by the shipToAddressName indicates which one it is.  It can either be an existing
   * address in their profile or it can be equal to the value FullShoppingCartFormHandler.NEW
   */
  String mShipToAddressName;

  /**
   * Set the nickname of the address from the secondary address map
   * that will be used to ship the goods to.  This would get set
   * if a user wants to ship all of their items from one particular place
   *
   * @param pShipToAddressName location to ship goods to
   */
  public void setShipToAddressName(String pShipToAddressName) {
    mShipToAddressName = pShipToAddressName;
  }

  /**
   * Get the name of the address to ship the goods to
   *
   * @beaninfo description: If a user is shipping all items to a single address, this is its name
   * @return the location to ship the goods to
   */
  public String getShipToAddressName() {
    return mShipToAddressName;
  }

  //---------------------------------------------------------------------------
  // property: profileAddressNames
  Collection mProfileAddressNames;

  /**
   * Return the profileAddressNames property.
   * @beaninfo description: The names of the address in the profile
   */
  public Collection getProfileAddressNames() {
    return mCommerceProfileTools.getProfileAddressNames(getProfile());
  }

  //-------------------------------------------
  // property: expressCheckoutSuccessURL
  //-------------------------------------------

  String mExpressCheckoutSuccessURL;

  public void setExpressCheckoutSuccessURL( String pURL ) {
    mExpressCheckoutSuccessURL = pURL;
  }

  /**
   * The URL to go to if Express Checkout is successful
   * @beaninfo description: The URL to go to if Express Checkout is successful
   **/
  public String getExpressCheckoutSuccessURL() {
    return mExpressCheckoutSuccessURL;
  }

  //-------------------------------------------
  // property: expressCheckoutErrorURL
  //-------------------------------------------

  String mExpressCheckoutErrorURL;

  public void setExpressCheckoutErrorURL( String pURL ) {
    mExpressCheckoutErrorURL = pURL;
  }

  /**
   * The URL to go to if Express Checkout fails
   * @beaninfo description: The URL to go to if Express Checkout fails
   **/
  public String getExpressCheckoutErrorURL() {
    return mExpressCheckoutErrorURL;
  }

  //-------------------------------------
  // property: CreateAddressSuccessURL
  //--------------------------------------
  String mCreateAddressSuccessURL;

  /**
   * Sets property CreateAddressSuccessURL.  This property
   * is normally set on a jhtml page.	 It indicates
   * which page we should redirect to if NO errors
   * occur when the user pushes the CREATE ADDRESS button.
   **/
  public void setCreateAddressSuccessURL(String pCreateAddressSuccessURL) {
    mCreateAddressSuccessURL = pCreateAddressSuccessURL;
  }

  /**
   * Returns property CreateAddressSuccessURL
   * @beaninfo description: The URL to go to when CreateAddress is successful
   **/
  public String getCreateAddressSuccessURL() {
    return mCreateAddressSuccessURL;
  }

  //-------------------------------------
  // property: CreateAddressErrorURL
  //--------------------------------------
  String mCreateAddressErrorURL;

  /**
   * Sets property CreateAddressErrorURL.  This property
   * is normally set on a jhtml page.	 It indicates
   * which page we should redirect to if NO errors
   * occur when the user pushes the CREATE ADDRESS button.
   **/
  public void setCreateAddressErrorURL(String pCreateAddressErrorURL) {
    mCreateAddressErrorURL = pCreateAddressErrorURL;
  }

  /**
   * Returns property CreateAddressErrorURL
   * @beaninfo description: The URL to go to when CreateAddress fails
   **/
  public String getCreateAddressErrorURL() {
    return mCreateAddressErrorURL;
  }

  //-------------------------------------
  // property: ShipToDoneSuccessURL
  //-------------------------------------
  String mShipToDoneSuccessURL;

  /**
   * Sets property ShipToDoneSuccessURL.
   **/
  public void setShipToDoneSuccessURL(String pShipToDoneSuccessURL) {
    mShipToDoneSuccessURL = pShipToDoneSuccessURL;
  }

  /**
   * Returns property ShipToDoneSuccessURL
   * @beaninfo description: The URL to go to when ShipToDone is successful
   **/
  public String getShipToDoneSuccessURL() {
    return mShipToDoneSuccessURL;
  }

  //-------------------------------------
  // property: ShipToDoneErrorURL
  //-------------------------------------
  String mShipToDoneErrorURL;

  /**
   * Sets property ShipToDoneErrorURL
   **/
  public void setShipToDoneErrorURL(String pShipToDoneErrorURL) {
    mShipToDoneErrorURL = pShipToDoneErrorURL;  }

  /**
   * Returns property ShipToDoneErrorURL
   * @beaninfo description: The URL to go to when ShipToDone fails
   **/
  public String getShipToDoneErrorURL() {
    return mShipToDoneErrorURL;
  }


  //-------------------------------------
  // property: ShipToMultipleErrorURL
  //--------------------------------------
  String mShipToMultipleErrorURL;

  /**
   * Sets property ShipToMultipleErrorURL.  This property
   * is normally set on the a jhtml page.  It indicates
   * which page we should redirect to if errors
   * occur when the user pushes the SHIP TO MULTIPLE DESTINATIONS button
   * on the Edit Shipping info page.
   **/
  public void setShipToMultipleErrorURL(String pShipToMultipleErrorURL) {
    mShipToMultipleErrorURL = pShipToMultipleErrorURL;
  }

  /**
   * Returns property ShipToMultipleErrorURL
   * @beaninfo description: The URL to go to when ShipToMultiple fails
   **/
  public String getShipToMultipleErrorURL() {
    return mShipToMultipleErrorURL;
  }


  //-------------------------------------
  // property: ShipToMultipleDoneSuccessURL
  //--------------------------------------
  String mShipToMultipleDoneSuccessURL;

  /**
   * Sets property ShipToMultipleDoneSuccessURL.  This property
   * is normally set on a jhtml page.	 It indicates
   * which page we should redirect to if NO errors
   * occur when the user is done making all changes
   * on the SHIP TO MULTIPLE page.
   **/
  public void setShipToMultipleDoneSuccessURL(String pShipToMultipleDoneSuccessURL) {
    mShipToMultipleDoneSuccessURL = pShipToMultipleDoneSuccessURL;
  }

  /**
   * Returns property ShipToMultipleDoneSuccessURL
   * @beaninfo description: The URL to go to when ShipToMultipleDone is successful
   **/
  public String getShipToMultipleDoneSuccessURL() {
    return mShipToMultipleDoneSuccessURL;
  }

  //-------------------------------------
  // property: ShipToMultipleDoneErrorURL
  //--------------------------------------
  String mShipToMultipleDoneErrorURL;

  /**
   * Sets property ShipToMultipleDoneErrorURL.  This property
   * is normally set on a jhtml page.	 It indicates
   * which page we should redirect to if errors
   * occur when the user is done making all changes
   * on the SHIP TO MULTIPLE page.
   **/
  public void setShipToMultipleDoneErrorURL(String pShipToMultipleDoneErrorURL) {
    mShipToMultipleDoneErrorURL = pShipToMultipleDoneErrorURL;
  }

  /**
   * Returns property ShipToMultipleDoneErrorURL
   * @beaninfo description: The URL to go to when ShipToMultipleDone fails
   **/
  public String getShipToMultipleDoneErrorURL() {
    return mShipToMultipleDoneErrorURL;
  }


  //-------------------------------------
  // property: ShipToMultipleSuccessURL
  //--------------------------------------
  String mShipToMultipleSuccessURL;

  /**
   * Sets property ShipToMultipleSuccessURL.	This property
   * is normally set on a jhtml page.	 It indicates
   * which page we should redirect to if NO errors
   * occur when the user pushes the SHIP TO MULTIPLE DESTINATIONS button
   * on the Edit Shipping info page.
   **/
  public void setShipToMultipleSuccessURL(String pShipToMultipleSuccessURL) {
    mShipToMultipleSuccessURL = pShipToMultipleSuccessURL;
  }

  /**
   * Returns property ShipToMultipleSuccessURL
   * @beaninfo description: The URL to go to when ShipToMultiple is successful
   **/
  public String getShipToMultipleSuccessURL() {
    return mShipToMultipleSuccessURL;
  }


  //-------------------------------------
  // property: MoveToNewShippingAddressSuccessURL
  //--------------------------------------
  String mMoveToNewShippingAddressSuccessURL;

  /**
   * Sets property MoveToNewShippingAddressSuccessURL.  This property
   * is normally set on a jhtml page.	 It indicates
   * which page we should redirect to if NO errors
   * occur when the user pushes the MOVE TO NEW SHIPPING ADDRESS button.
   **/
  public void setMoveToNewShippingAddressSuccessURL(String pMoveToNewShippingAddressSuccessURL) {
    mMoveToNewShippingAddressSuccessURL = pMoveToNewShippingAddressSuccessURL;
  }

  /**
   * Returns property MoveToNewShippingAddressSuccessURL
   * @beaninfo description: The URL to go to when MoveToNewShippingAddress is successful
   **/
  public String getMoveToNewShippingAddressSuccessURL() {
    return mMoveToNewShippingAddressSuccessURL;
  }

  //-------------------------------------
  // property: MoveToNewShippingAddressErrorURL
  //--------------------------------------
  String mMoveToNewShippingAddressErrorURL;

  /**
   * Sets property MoveToNewShippingAddressErrorURL.	This property
   * is normally set on the a jhtml page.  It indicates
   * which page we should redirect to if errors
   * occur when the user pushes the MOVE TO NEW SHIPPING ADDRESS button.
   **/
  public void setMoveToNewShippingAddressErrorURL(String pMoveToNewShippingAddressErrorURL) {
    mMoveToNewShippingAddressErrorURL = pMoveToNewShippingAddressErrorURL;
  }

  /**
   * Returns property MoveToNewShippingAddressErrorURL
   * @beaninfo description: The URL to go to when MoveToNewShippingAddress fails
   **/
  public String getMoveToNewShippingAddressErrorURL() {
    return mMoveToNewShippingAddressErrorURL;
  }

  //-------------------------------------
  // property: AddGiftItemToOrderSuccessURL
  //-------------------------------------
  String mAddGiftItemToOrderSuccessURL;

  /**
   * Sets property AddGiftItemToOrderSuccessURL
   **/
  public void setAddGiftItemToOrderSuccessURL(String pAddGiftItemToOrderSuccessURL) {
    mAddGiftItemToOrderSuccessURL = pAddGiftItemToOrderSuccessURL;
  }

  /**
   * Returns property AddGiftItemToOrderSuccessURL
   * @beaninfo description: The URL to go to when AddGiftItemToOrder is successful
   **/
  public String getAddGiftItemToOrderSuccessURL() {
    return mAddGiftItemToOrderSuccessURL;
  }

  //-------------------------------------
  // property: AddGiftItemToOrderErrorURL
  //-------------------------------------
  String mAddGiftItemToOrderErrorURL;

  /**
   * Sets property AddGiftItemToOrderErrorURL
   **/
  public void setAddGiftItemToOrderErrorURL(String pAddGiftItemToOrderErrorURL) {
    mAddGiftItemToOrderErrorURL = pAddGiftItemToOrderErrorURL;
  }

  /**
   * Returns property AddGiftItemToOrderErrorURL
   * @beaninfo description: The URL to go to when AddGiftItemToOrder fails
   **/
  public String getAddGiftItemToOrderErrorURL() {
    return mAddGiftItemToOrderErrorURL;
  }


  //--------------------------------------------------
  // Constructors

  /**
   * Empty Constructor
   *
   */
  public FullShoppingCartFormHandler() {
    super();
  }

  //--------------------------------------------------
  // Methods

  //--------------------------------------------
  // Overridden methods from ShoppingCartFormHandler
  //--------------------------------------------



  /**
   * Provide ability to interact with credit cards in user's profile
   * and to handle multiple forms of payment.
   *
   * <P>
   *
   * To see if a credit card needs to be copied to/from the profile
   * the value of <code>selectedCreditCardName</code> is checked.
   * If it has a value then make a call to <copy>copyCreditCardInfo</code>
   *
   * <P>
   *
   * Next, check to see if there have been new credit cards entered
   * into list of credit cards and the user input gift certificate
   * numbers.  If either of these is true, then attempt to place the various
   * payment methods into the user's Order object by calling
   * <code>setPaymentMethodsInOrder</code>.  The method isNewCreditCards()
   * is used to see if there are new credit cards.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preMoveToConfirmation(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    super.preMoveToConfirmation(pRequest, pResponse);

    if (! isStringEmpty(mSelectedCreditCardName) &&
        ! mSelectedCreditCardName.equalsIgnoreCase(NEW)) {
      CreditCard creditCard = (CreditCard)mCreditCardPaymentGroups.get(0);
      copyCreditCardToPaymentGroup(mSelectedCreditCardName, creditCard, pRequest, pResponse);
    }

    try {
     // handleMultiplePaymentMethods, if there are new ones to merge
      if (isNewCreditCards() || !isStringEmpty(mGiftCertificateNumbers))
        setPaymentMethodsInOrder(pRequest, pResponse);
    } catch (Exception exc) {
      processException(exc, MSG_ERROR_MOVE_TO_CONFIRMATION, pRequest, pResponse);
    }

    if (!isStringEmpty(mSelectedCreditCardName) &&
	mSelectedCreditCardName.equalsIgnoreCase(NEW)) {
      CreditCard creditCard = (CreditCard)mCreditCardPaymentGroups.get(0);
      if (creditCard.getCreditCardNumber().equals("")) {
	String msg = formatUserMessage(MSG_NO_CREDIT_CARD_NUMBER, pRequest, pResponse);
	String propertyPath = generatePropertyPath("catalogRefIds");
	addFormException(new DropletFormException(msg, propertyPath, MSG_NO_CREDIT_CARD_NUMBER));
      }
    }
  }

  /**
   * This method will check to see if the user entered a new credit card
   * and additionally see if there were any errors.  If there is a new
   * credit card and there are no errors then it gets copied to the
   * Profile via the copyCreditCardToProfile method.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postMoveToConfirmation(DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    if (! isTransactionMarkedAsRollBack()) {
      if (!isStringEmpty(mSelectedCreditCardName) &&  mSelectedCreditCardName.equalsIgnoreCase(NEW)){
	RepositoryItem profile = getProfile();

	//Obtain the new credit card and the credit card number
        CreditCard creditCard = (CreditCard)mCreditCardPaymentGroups.get(0);
	String creditCardNumber = creditCard.getCreditCardNumber();

	//Obtain the user's map of credit cards
	Map usersCreditCardMap = mCommerceProfileTools.getUsersCreditCardMap(profile);
	//Iterating through the map to check if the new credit number already exists in the user's credit card map
	if(usersCreditCardMap != null){
	  Set ccKeySet = usersCreditCardMap.keySet();
	  Iterator ccKeyIter = ccKeySet.iterator();
	  while(ccKeyIter.hasNext()){
	    String key = (String)ccKeyIter.next();
	    if (creditCardNumber.equals(key)){
	      processException(new CommerceException(formatUserMessage(REDUNDANT_CREDIT_CARD, pRequest, pResponse)), REDUNDANT_CREDIT_CARD, pRequest, pResponse);
	      break;
	    }
	  }
	}
	if (! getFormError())
        copyCreditCardToProfile(creditCard);
      }
    }
  }

  //--------------------------------------------
  // New Methods
  //--------------------------------------------

  //--------------------------------------------
  // AddGiftItemToOrder
  //--------------------------------------------

  public void preAddGiftItemToOrder(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  public void postAddGiftItemToOrder(DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * The same as @see ShoppingCartFormHandler#handleAddItemToOrder with the added
   * affect of adding handling to the shipping group
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleAddGiftItemToOrder (DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
    throws ServletException,
    IOException
  {

    Transaction tr = null;
    try {
      tr = ensureTransaction();

      synchronized(getOrder()) {
	preAddGiftItemToOrder(pRequest, pResponse);
	
	addItemToOrder(pRequest, pResponse, true);
	
	postAddGiftItemToOrder(pRequest, pResponse);

	try {
	  getOrderManager().updateOrder(getOrder());
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
      } // synchronized

      //If NO form errors are found, redirect to the success URL.
      //If form errors are found, redirect to the error URL.
      return checkFormRedirect (getAddGiftItemToOrderSuccessURL(),
                                getAddGiftItemToOrderErrorURL(),
                                pRequest,
                                pResponse);
    }
    finally {
      if (tr != null) commitTransaction(tr);
    }

  }

  //--------------------------------------------
  // CreateAddress
  //--------------------------------------------

  /**
   * Called right before any processing is done by the <code>handleCreateAddress</code>
   * method.
   *
   * Currently is empty.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preCreateAddress(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * Called right after any processing is done by the <code>handleCreateAddress</code>
   * method.
   *
   * Currently is empty.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postCreateAddress(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }


  /**
   * Handles creating a new address object in the user's secondary address
   * property.  It requires that a valid value has been input for the
   * <code>newShippingAddressName</code>.  Two conditions are checked for
   * in this new address name
   *
   * <UL>
   *    <LI> There exists a name and is not null
   *    <LI> A nickname by that name does not already exist
   * </UL>
   *
   * If either of these two conditions exists, then a droplet exceptions is
   * generated and the method returns.  Else, use the <code>CommerceProfileTools</code>
   * to create an entry in the user's secondary address map using the name supplied
   * by <code>newShippingAddressName</code>.  Only an entry is made at this point,
   * no actual address information is inserted. i.e. the key now exists, but the value
   * for the key does not.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @return a value of type 'boolean'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleCreateAddress (DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {

    Transaction tr = null;
    try {
      tr = ensureTransaction();

      boolean noErrors = true;
      //Check if any form errors exist.  If they do, redirect to Error URL:
      if (!checkFormRedirect(null, getCreateAddressErrorURL(), pRequest, pResponse))
        return false;
      synchronized(getOrder()) {
	preCreateAddress(pRequest, pResponse);
	// ensure the user typed in a secondary address
	if (isStringEmpty(mNewShippingAddressName)) {
	  String msg = formatUserMessage(MSG_NO_NEW_ADDRESS_ENTERED, pRequest, pResponse);
	  String propertyPath = generatePropertyPath("newShippingAddressName");
	  addFormException(new DropletFormException(msg, propertyPath, MSG_NO_NEW_ADDRESS_ENTERED));
	  noErrors = false;
	}

	// ensure that the profile does not already have this address nickname
	Collection addressNames = mCommerceProfileTools.getProfileAddressNames(getProfile());
	if (addressNames.contains(mNewShippingAddressName)) {
	  String msg = formatUserMessage(MSG_ADDRESS_ALREADY_EXISTS,
					 mNewShippingAddressName,
					 pRequest, pResponse);
	  String propertyPath = generatePropertyPath("profileAddressNames");
	  addFormException(new DropletFormException(msg, propertyPath, MSG_ADDRESS_ALREADY_EXISTS));
	  noErrors = false;
	}

	// if no errors generated up until now
	if (noErrors) {
	  try {
	    mCommerceProfileTools.createProfileRepositorySecondaryAddress(getProfile(),
									  mNewShippingAddressName,
									  null);
	  } catch (RepositoryException exc) {
	    processException(exc, MSG_ERROR_ADDING_ADDRESS, pRequest, pResponse);
	  }
	  
	}
	postCreateAddress(pRequest, pResponse);
      } // synchronized

      //If NO form errors are found, redirect to the success URL.
      //If form errors are found, redirect to the error URL.
      return checkFormRedirect (getCreateAddressSuccessURL(),
                                getCreateAddressErrorURL(), pRequest, pResponse);
    }
    finally {
      if (tr != null) commitTransaction(tr);
    }
  }


  //-------------------------------------------------
  // ShipToMultiple
  //-------------------------------------------------

  /**
   * Called before any processing has been done by the <code>handleShipToMultiple</code>
   * method.  Currently nothing is being done.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preShipToMultiple(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * Called after processing has been done by the <code>handleShipToMultiple</code>
   * method.  Currently nothing is being done.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postShipToMultiple(DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * Called when the user selects to ship their items to multiple
   * locations.  At this point, there is a precondition that only
   * one shipping group exists in the order.  This shipping group
   * currently will not have a description associated with it
   * so it needs to be set to the default shipping address,
   * as specified by the <code>PropertyManager</code>.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @return a value of type 'boolean'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleShipToMultiple (DynamoHttpServletRequest pRequest,
                                       DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {

    Transaction tr = null;
    try {
      tr = ensureTransaction();
      //Check if any form errors exist.  If they do, redirect to Error URL:
      if (!checkFormRedirect(null, getShipToMultipleErrorURL(), pRequest, pResponse))
        return false;

      synchronized(getOrder()) {
	preShipToMultiple(pRequest, pResponse);
	ShippingGroup sg = getShippingGroup();
	
	if (sg == null) {
	  String msg = formatUserMessage(MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
    String propertyPath = generatePropertyPath("shippingGroup");
	  addFormException(new DropletFormException(msg, propertyPath, MSG_ERROR_UPDATE_ORDER));
	}
	else {
	  sg.setDescription(mPropertyManager.getDefaultShippingAddrName());
	}
	
	postShipToMultiple(pRequest, pResponse);
	
	try {
	  getOrderManager().updateOrder(getOrder());
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
      } // synchronized

      return checkFormRedirect (getShipToMultipleSuccessURL(),
                                getShipToMultipleErrorURL(), pRequest, pResponse);
    }
    finally {
      if (tr != null) commitTransaction(tr);
    }
  }

  //-------------------------------------------------
  // MoveToNewShippingAddress
  //-------------------------------------------------

  /**
   * Called before any processing is done by the <code>handleMoveToNewShippingAddress</code>
   * method.  Currently, nothing is being done.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preMoveToNewShippingAddress(DynamoHttpServletRequest pRequest,
                                          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * Called after any processing is done by the <code>handleMoveToNewShippingAddress</code>
   * method.  Currently, nothing is being done.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postMoveToNewShippingAddress(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * This method handles the moving of a commerce item from one shipping group
   * to another in a user's order.  It assumes that the following variables have
   * been set: <code>originalShippingAddressName, newShippingAddressName,
   * commerceItemIdToEdit and quantityToMove</code>.  The originalShippingAddressName
   * is the description name of the shipping group that the commerce items will
   * be moved from.  The newShippingAddressName is the nickname from the user's secondary
   * address map that contains the address that the items will be moved to.  The
   * commerceItemIdToEdit should be the id of the CommerceItem that will be moved.
   * This method simply makes a call to
   * @see #moveCommerceItemsToNewShippingGroup(String, String, String, int, DynamoHttpServletRequest, DynamoHttpServletResponse)
   * to actually do movement of the commerce items.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @return a value of type 'boolean'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleMoveToNewShippingAddress (DynamoHttpServletRequest pRequest,
                                                 DynamoHttpServletResponse pResponse)
    throws ServletException,  IOException
  {
    Transaction tr = null;
    try {
      tr = ensureTransaction();

      //Check if any form errors exist.  If they do, redirect to Error URL:
      if (!checkFormRedirect(null, getMoveToNewShippingAddressErrorURL(), pRequest, pResponse))
        return false;

      synchronized(getOrder()) {
	preMoveToNewShippingAddress(pRequest, pResponse);
	
	try {
	  moveCommerceItemsToNewShippingGroup(getOriginalShippingAddressName(), getNewShippingAddressName(),
	       getCommerceItemIdToEdit(), getQuantityToMove(), pRequest, pResponse);
	} catch (CommerceException ce) {
	  processException(ce, MSG_ERROR_MOVING_ITEM_TO_NEW_ADDRESS, pRequest, pResponse);
	}
	
	postMoveToNewShippingAddress(pRequest, pResponse);

	try {
	  getOrderManager().updateOrder(getOrder());
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
      } // synchronized

      //If NO form errors are found, redirect to the success URL:
      //If form errors are found, redirect to the error URL.
      return checkFormRedirect (getMoveToNewShippingAddressSuccessURL(),
                                getMoveToNewShippingAddressErrorURL(),
                                pRequest,
                                pResponse);
    }
    finally {
      if (tr != null) commitTransaction(tr);
    }
  }

  //-------------------------------------------------
  // ShipToMultipleDone
  //-------------------------------------------------

  /**
   * Called before any processing is done by the <code>handleShipToMultipleDone</code>
   * method.  Currently, nothing is being done.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preShipToMultipleDone(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
    throws ServletException, IOException

  {
  }

  /**
   * Called after processing by the <code>handleShipToMultipleDone</code> method.
   * Currently, nothing is being done.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postShipToMultipleDone(DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * Called after a user is done specifying the addresses that they want to
   * move their commerce items to.  This makes calls to the appropriate pre/post
   * methods and then returns.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleShipToMultipleDone (DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    Transaction tr = null;
    try {
      tr = ensureTransaction();

      //Check if any form errors exist.  If they do, redirect to Error URL:
      if (!checkFormRedirect(null, getShipToMultipleDoneErrorURL(), pRequest, pResponse))
        return false;

      synchronized(getOrder()) {
	preShipToMultipleDone(pRequest, pResponse);
	
	try {
	  getOrderManager().getShippingGroupManager().removeEmptyShippingGroups(getOrder());
	
	} catch (Exception exc) {
	  processException(exc, MSG_ERROR_MOVE_TO_PAYMENT, pRequest, pResponse);
	}

	//If NO form errors are found, redirect to the success URL.
	//If form errors are found, redirect to the error URL.
	postShipToMultipleDone(pRequest, pResponse);

	try {
	  getOrderManager().updateOrder(getOrder());
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
      } // synchronized

      return checkFormRedirect (getShipToMultipleDoneSuccessURL(), getShipToMultipleDoneErrorURL(),
                                pRequest, pResponse);
    }
    finally {
      if (tr != null) commitTransaction(tr);
    }
  }

  //-------------------------------------------------
  // ShipToDone
  //-------------------------------------------------
  /**
   * Called before any processing is done by the <code>handleShipToDone<code>
   * method.  Currently, it does nothing.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preShipToDone(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * Called after any processing is done by the <code>handleShipToDone</code>
   * method.  Currently, it does nothing.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postShipToDone(DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }


  /**
   * Called after the user has finished inputting their shipping information.
   * If the user is going to be shipping to a single address, then the name
   * of this address should be specified by the <code>shipToAddressName</code>.
   * If there is a name specified for this property than
   * @see #updateShippingAddress(DynamoHttpServletRequest, DynamoHttpServletResponse)
   * is called to copy the address information from the correct location.
   *
   * <p>
   *
   * Additionally, the user could have addresses info in their order which do
   * not exist in their profile yet.  If this is the case, then the names of those
   * addresses should be in the <code>addressesToCopy</code> property.  These
   * addresses will be copied over by the
   * @see #copyAddressesToProfile(DynamoHttpServletRequest, DynamoHttpServletResponse)
   * method.
   *
   * <P>
   *
   * Finally, the shipping groups in the order will attempt to be validated via
   * the runProcessValidateShippingGroups method.  If there are errors, then
   * any added nicknames to the profile during the current submit will be removed.
   * This is because the shipping groups have not been validated and the user will
   * need to chagne their address info before continuing. This prevents "half" addresses
   * from appearing in the user's profile.  Since the nicknames will have been removed the
   * copyAddressesToProfile method will not get called and the new address that the user
   * just input will not get copied to the Profile.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @return a value of type 'boolean'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleShipToDone (DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
        throws ServletException, IOException
  {
    RepositoryItem toAddress;
    Map secondaryAddressMap;
    Address fromAddress;
    boolean errorInAddresses;

    Transaction tr = null;
    try {
      tr = ensureTransaction();

      //Check if any form errors exist.  If they do, redirect to Error URL:
      if (!checkFormRedirect(null, getShipToDoneErrorURL(), pRequest, pResponse))
        return false;

      synchronized(getOrder()) {
	preShipToDone(pRequest, pResponse);
	
	try {
	  // if items are going to a single new address
	  if (mShipToAddressName != null) {
	    updateShippingAddress(pRequest, pResponse);

	    //Check if any form errors exist.  If they do, redirect to Error 
	    //URL:
	    if (!checkFormRedirect(null, getShipToDoneErrorURL(), pRequest, pResponse))
	      return false;
	  }

	  errorInAddresses = runProcessValidateShippingGroups(getOrder(), getUserPricingModels(),
	                getUserLocale(pRequest,pResponse), getProfile(),null);
	  if (errorInAddresses) {
	    removeAddedNickNameEntries();
	  }
	  else {
	    if (mAddressesToCopy != null && mAddressesToCopy.length > 0) {
	      copyAddressesToProfile(pRequest, pResponse);
	    }
	  }
	  
	} catch (Exception exc) {
	  processException(exc, MSG_ERROR_MOVE_TO_PAYMENT, pRequest, pResponse);
	}

	postShipToDone(pRequest, pResponse);
	
	try {
	  getOrderManager().updateOrder(getOrder());
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
      } // synchronized

      //If NO form errors are found, redirect to the success URL.
      //If form errors are found, redirect to the error URL.
      return checkFormRedirect (getShipToDoneSuccessURL(), getShipToDoneErrorURL(),
                                pRequest, pResponse);
    }
    finally {
      if (tr != null) commitTransaction(tr);
    }
  }

  //-------------------------------------------------
  // ExpressCheckout
  //-------------------------------------------------
  /**
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public void preExpressCheckout(DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
  }

  /**
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public void postExpressCheckout(DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
  }

  /**
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * pje
   */
  public boolean handleExpressCheckout( DynamoHttpServletRequest pRequest,
                                        DynamoHttpServletResponse pResponse ) throws IOException, ServletException
  {

    ShippingGroup shippingGroup = getShippingGroup();
    if (shippingGroup == null) {
      String msg = formatUserMessage(MSG_ERROR_MOVE_TO_CONFIRMATION, pRequest, pResponse);
      String propertyPath = generatePropertyPath("shippingGroup");
      addFormException(new DropletFormException(msg, propertyPath ,MSG_ERROR_MOVE_TO_CONFIRMATION));
    }

    PaymentGroup paymentGroup = getPaymentGroup();
    if (paymentGroup == null) {
      String msg = formatUserMessage(MSG_ERROR_MOVE_TO_CONFIRMATION, pRequest, pResponse);
      String propertyPath = generatePropertyPath("paymentGroup");
      addFormException(new DropletFormException(msg, propertyPath, MSG_ERROR_MOVE_TO_CONFIRMATION));
    }

    Transaction tr = null;
    try {
      tr = ensureTransaction();
      // If form errors exist, redirect to error URL
      if ( !checkFormRedirect(null,getExpressCheckoutErrorURL(),pRequest,pResponse) )
        return false;

      synchronized(getOrder()) {
	// Let subclasses get their hands on the request/response before
	// anything's done here
	preExpressCheckout( pRequest, pResponse );
	
	try {
	  // from: ShoppingCartFormHandler.handleMoveToPurchaseInfo()
	  // Validate the changes the user made, and update the Order:
	  modifyOrderByRelationshipId( pRequest, pResponse );
	  
	  // Run the appropriate Pipeline Chain:
	  runProcessMoveToPurchaseInfo( getOrder(), getUserPricingModels(),
                                      getUserLocale(pRequest,pResponse), getProfile(), null );

	  getOrderManager().updateOrder( getOrder() );
	  
	  if (! isTransactionMarkedAsRollBack()) {
	    // Copy the default credit card into the order
	    RepositoryItem defaultCreditCard =
              (RepositoryItem) getProfile().getPropertyValue(mPropertyManager.getDefaultCreditCardPropertyName());
        
	    if ( defaultCreditCard != null )
	      copyCreditCard(defaultCreditCard, (CreditCard) paymentGroup);
	    else {
	      if ( isLoggingError() )
          logError(ResourceUtils.getMsgResource(ERROR_MISSING_CREDIT_CARD,
                                                getResourceBundleName(), getResourceBundle()));
	      return false;
	    }
	    
	    // Copy billing address to PaymentGroup
	    RepositoryItem billingAddress =
	      mCommerceProfileTools.getProfileAddress(getProfile(),mPropertyManager.getDefaultBillingAddrName());

	    if ( billingAddress == null ) {
	      if ( isLoggingError() )
          logError(ResourceUtils.getMsgResource(ERROR_MISSING_BILLING_ADDRESS,
                                                getResourceBundleName(), getResourceBundle()));
	    }
	    else {
	      OrderTools.copyAddress(billingAddress,((CreditCard)paymentGroup).getBillingAddress());
	    }

	    // Copy default shipping address to order
	    MutableRepositoryItem defaultAddress =
	      (MutableRepositoryItem) getProfile().getPropertyValue(mPropertyManager.getShippingAddressPropertyName());
	    if ( null == defaultAddress && isLoggingDebug() )
	      logDebug( "defaultAddress == null" );
	    
	    try {
	      OrderTools.copyAddress( defaultAddress,
                                             ((HardgoodShippingGroup) shippingGroup).getShippingAddress() );
	    }
	    catch( ClassCastException cce ) {
	      processException( cce, MSG_UNABLE_TO_COPY_ADDRESS, pRequest, pResponse );
	      return false;
	    }
	    
	    // Set the default shipping method
	    shippingGroup.setShippingMethod((String) getProfile().getPropertyValue(mPropertyManager.getDefaultShippingMethodPropertyName()) );
        
	    // Confirmation portion
	    runProcessMoveToConfirmation( getOrder(), getUserPricingModels(),
                                          getUserLocale(pRequest,pResponse), getProfile(), null );
	  }
	}
	catch (Exception exc) {
	  processException( exc, MSG_ERROR_MOVE_TO_CONFIRMATION, pRequest, pResponse );
	}
	
	// Let user modify request/respons post checkout
	postExpressCheckout( pRequest, pResponse );

	try {
	  getOrderManager().updateOrder(getOrder());
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
      } // synchronized

      // If no form errors are found, redirect to the success URL,
      // otherwise redirect to the error URL.
      return checkFormRedirect( getExpressCheckoutSuccessURL(), getExpressCheckoutErrorURL(),
                                pRequest, pResponse);
    }
    finally {
      if (tr != null) commitTransaction(tr);
    }
  }


  //-----------------------------------------------
  // Credit Card Utility Functions
  //-----------------------------------------------


  /**
   * Copy the credit card passed in the parameter pCreditCard to the user's
   * profile.  This is done by creating a new Credit Card itemDescriptor
   * object by making use of the @see atg.commmerce.profile.CommerceProfileTools
   * class to create a creditCardItem.  Next, the @see #copyCreditCard(CreditCard, RepositoryItem)
   * is called to perform the actual copying of data.  The resulting object is then
   * updated to the repository and copied into the user's map of credit cards.
   *
   * @param pCreditCard the credit card that is to be copied to a user's profile
   * @deprecated this method has been moved to CommerceProfileTools
   */
  protected void copyCreditCardToProfile(CreditCard pCreditCard)
  {
    RepositoryItem profile = getProfile();
    mCommerceProfileTools.copyCreditCardToProfile(pCreditCard, profile);
  }


  /**
   * Copy the credit card from the user's profile to the payment group.  The credit
   * card item named by pNickname is obtained from the user's profile and then
   * copied into the paymentGroup parameter.  This is done by making calls
   * to @see #copyCreditCard(RepositoryItem, CreditCard)
   *
   * @param pNickname a value of type 'String'
   * @param paymentGroup a value of type 'CreditCard'
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @deprecated this method has been moved to CommerceProfileTools
   */
  protected void copyCreditCardToPaymentGroup(String pNickname,
                                              CreditCard paymentGroup,
                                              DynamoHttpServletRequest pRequest,
                                              DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    mCommerceProfileTools.copyCreditCardToPaymentGroup(pNickname,
                                                       paymentGroup,
                                                       getProfile(),
                                                       getUserLocale(pRequest, pResponse));
  }

  /**
   * Copying a credit card is a two step process.
   *
   * <P>
   *
   * The first is copying the shallow properties of the credit card.  These are objects
   * like String, Integer etc. that can be copied.  This shallow copying is performed
   * by the @see #copyShallowCreditCardProperties method.
   *
   * <P>
   *
   * Next, any post copying is done.  The single object that must have a "deep" coyp
   * performed on it is the billingAddress of the credit card.  The billingAddress
   * is obtained from both objects and then a call is made to the
   * @see OrderManager to perform the copy.
   *
   * <P>
   *
   * If there is additional deep copying that needs to be done, this method should be overriden.
   *
   *
   * @param pFromCreditCard a value of type 'RepositoryItem'
   * @param pToCreditCard a value of type 'CreditCard'
   * @deprecated this method has been moved to CommerceProfileTools
   */
  protected void copyCreditCard(RepositoryItem pFromCreditCard,
                                CreditCard pToCreditCard)
  {
    mCommerceProfileTools.copyCreditCard(pFromCreditCard, pToCreditCard);
  }

  /**
   * Copying a credit card is a two step process.
   *
   * <P>
   *
   * The first is copying the shallow properties of the credit card.  These are objects
   * like String, Integer etc. that can be copied.  This shallow copying is performed
   * by the @see #copyShallowCreditCardProperties method.
   *
   * <P>
   *
   * Next, any post copying is done.  The single object that must have a "deep" copy
   * performed on it is the billingAddress of the credit card.  The billingAddress
   * is obtained from both objects and then a call is made to the
   * @see OrderManager to perform the copy.
   *
   * <P>
   *
   * If there is additional deep copying that needs to be done, this method should be overriden.
   *
   *
   * @param pFromCreditCard a value of type 'RepositoryItem'
   * @param pToCreditCard a value of type 'CreditCard'
   * @deprecated this method has been moved to CommerceProfileTools
   */
  protected void copyCreditCard(CreditCard pFromCreditCard,
                                RepositoryItem pToCreditCard)
  {
    mCommerceProfileTools.copyCreditCard(pFromCreditCard, pToCreditCard);
  }

  /**
   * The shallow properties of a credit card are copied.  This is done by consulting
   * the @see #propertyManager for a String array of properties to copy, as named
   * by the <code>shallowCreditCardPropertyNames</code> property.  These properties
   * are then copied via DynamicBeans
   *
   * @param pFromCreditCard the credit card that the address is copied from
   * @param pToCreditCard the destination credit card for address
   * @exception PropertyNotFoundException if a property listed by shallowCreditCardPropertyNames
   * is not found
   * @deprecated this method has been moved to CommerceProfileTools
   */
  protected void copyShallowCreditCardProperties(Object pFromCreditCard,
                                                 Object pToCreditCard)
    throws PropertyNotFoundException
  {
    mCommerceProfileTools.copyShallowCreditCardProperties(pFromCreditCard, pToCreditCard);
  }

  //-----------------------------------------------
  // Address Utility Functions
  //-----------------------------------------------

  protected void removeAddedNickNameEntries()
    throws RepositoryException
  {
    if (! isStringEmpty(mNewShippingAddressName))
      mCommerceProfileTools.removeProfileRepositoryAddress(getProfile(), mNewShippingAddressName);
  }

  /**
   * This method moves commerce items from one shipping group to another.  In performing
   * this move there are three conditions that are checked:
   *
   * <UL>
   *   <LI> If the address that the items are being moved to exists, then
   *        the @see OrderManager is called to update the relationship
   *        between the shipping groups
   *   <LI> If the destination shipping group does not exist, but the
   *        current shipping group is only being used for the commerce item
   *        then just copy the new address information into the old shipping
   *        group.  Whether or not this single relationship exists is determined
   *        by calling the @see #isSingletonRelationship method.
   *   <LI> If none of the two above cases exist, then a new shipping group must
   *        be created and added to the order.  After the shippingGroup has been
   *        created, the address as named by the <code>pToAddress</code> is
   *        obtained from the Profile and the info is copied.  The
   *        @see #copyProfileAddressToShippingGroup method performs the copying
   * </UL>
   *
   * @param pFromAddress a value of type 'String'
   * @param pToAddress a value of type 'String'
   * @param pCommerceItemIdToMove a value of type 'String'
   * @param pQuantityToMove a value of type 'int'
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception CommerceException if an error occurs
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void moveCommerceItemsToNewShippingGroup(String pFromAddress,
                                                     String pToAddress,
                                                     String pCommerceItemIdToMove,
                                                     int pQuantityToMove,
                                                     DynamoHttpServletRequest pRequest,
                                                     DynamoHttpServletResponse pResponse)
    throws CommerceException, ServletException, IOException
  {
    ShippingGroup toShippingGroup;
    ShippingGroup fromShippingGroup;
    CommerceItem ciToMove;
    Order order;
    SimpleOrderManager orderManager = getOrderManager();
    HandlingInstructionManager handlingInstructionManager = orderManager.getHandlingInstructionManager();

    List shippingGroups = getOrder().getShippingGroups();
    order = getOrder();
    ciToMove = order.getCommerceItem(pCommerceItemIdToMove);
    fromShippingGroup = orderManager.getShippingGroupManager().getShippingGroupByDescription(shippingGroups,
                                                                        pFromAddress);
    toShippingGroup = orderManager.getShippingGroupManager().getShippingGroupByDescription(shippingGroups,
                                                                      pToAddress);

    if (pFromAddress.equals(pToAddress) || pQuantityToMove <= 0 || pQuantityToMove > orderManager.getShippingGroupManager().getShippingGroupCommerceItemRelationship(order, pCommerceItemIdToMove ,fromShippingGroup.getId()).getQuantity()) {
      String msg = formatUserMessage(MSG_INVALID_QUANTITY, pRequest, pResponse);
      addFormException(new DropletException(msg, MSG_INVALID_QUANTITY));
      return;
    }
    if (toShippingGroup != null) {
      // get handling instructions for shipping group and remove from
      // old SG
      List handList = handlingInstructionManager.getHandlingInstructionsForCommerceItem(fromShippingGroup, pCommerceItemIdToMove);
      handlingInstructionManager.removeHandlingInstructionsFromShippingGroup(order, fromShippingGroup.getId(), pCommerceItemIdToMove);

      // just create new pointers, dont need a new SG
      orderManager.moveItemToShippingGroup(order, ciToMove, pQuantityToMove,
                                           fromShippingGroup, toShippingGroup);
      // add each handling instruction to new SG
      HandlingInstruction hi = null;
      Iterator iter = handList.iterator();
      while(iter.hasNext()){
        hi = (HandlingInstruction)iter.next();
        HandlingInstruction newHi = handlingInstructionManager.copyHandlingInstruction(hi);
        newHi.setShippingGroupId(toShippingGroup.getId());
        handlingInstructionManager.addHandlingInstructionToShippingGroup(order, newHi);
      }

    } else if (isSingletonRelationship(fromShippingGroup, ciToMove, order, pQuantityToMove)) {
      // efficiency, keep SG but update address and nickname
      copyProfileAddressToShippingGroupAddress(pToAddress, fromShippingGroup,
                                               pRequest, pResponse);
    } else {
      //Create a new shipping group
      HardgoodShippingGroup newShippingGroup;
      newShippingGroup = (HardgoodShippingGroup)orderManager.getShippingGroupManager().createShippingGroup();

      //Copy Profile Address info to new Shipping Group:
      copyProfileAddressToShippingGroupAddress(pToAddress, newShippingGroup,
                                               pRequest, pResponse);

      // get handling instructions for shipping group and remove from
      // old SG
      List handList = handlingInstructionManager.getHandlingInstructionsForCommerceItem(fromShippingGroup, pCommerceItemIdToMove);
      handlingInstructionManager.removeHandlingInstructionsFromShippingGroup(order, fromShippingGroup.getId(), pCommerceItemIdToMove);

      //Add the new Shipping Group to the list of Shipping
      order.addShippingGroup(newShippingGroup);
      orderManager.moveItemToShippingGroup(order,
                                           ciToMove,
                                           pQuantityToMove,
                                           fromShippingGroup,
                                           newShippingGroup);
      // add each handling instruction to new SG
      HandlingInstruction hi = null;
      Iterator iter = handList.iterator();
      while(iter.hasNext()){
        hi = (HandlingInstruction)iter.next();
        HandlingInstruction newHi = handlingInstructionManager.copyHandlingInstruction(hi);
        newHi.setShippingGroupId(toShippingGroup.getId());
        handlingInstructionManager.addHandlingInstructionToShippingGroup(order, newHi);
      }
    }
  }

  /**
   * This method will determine if the relationship between a given
   * shippingGroup and commerceItem is singleton.  This means
   *
   * <UL>
   *  <LI> The shippingGroup only has a relationship with a single
   *       commerceItem, which is the current one
   *  <LI> The quantity of the commerce item being moved
   *       as indicated by the <code>pQuantityToMove</code>
   *       parameter should equal each other.
   * </UL>
   *
   * @param pShippingGroup shippingGroup the commerceItem currently belongs to
   * @param pCommerceItem the commerceItem to move
   * @param order user's order
   * @param pQuantityToMove the quantity of the commerceItem to move
   * @return true if a singleton relationship exists
   * @exception CommerceException if an error occurs
   */
  protected boolean isSingletonRelationship(ShippingGroup pShippingGroup,
                                            CommerceItem pCommerceItem,
                                            Order order,
                                            int pQuantityToMove)
    throws CommerceException
  {
    int sgCiRelCount = pShippingGroup.getCommerceItemRelationshipCount();
    long ciCount = getOrderManager().getShippingGroupManager().getShippingGroupCommerceItemRelationship(order, pCommerceItem.getId(),pShippingGroup.getId()).getQuantity();

    if (sgCiRelCount == 1 && pQuantityToMove == ciCount)
      return true;

    return false;
  }


  /**
   * The address as specified by the <code>pProfileAddressName</code> is copied
   * from the user's profile to the shippingGroup specified by <code>pShippingGroup</code>
   * The profile address is obtained by using the @see #commerceProfileTools.  The
   * profile is then copied using the @see OrderManager.
   *
   * @param pProfileAddressName the profile address nickname that is the source address
   * @param pShippingGroup the destination shippinGroup where the address is copied to
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void copyProfileAddressToShippingGroupAddress (String pProfileAddressName,
                                                           ShippingGroup pShippingGroup,
                                                           DynamoHttpServletRequest pRequest,
                                                           DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    RepositoryItem profileAddress;

    //Get the Address object from the Shipping Group:
    Address shippingGroupAddress = ((HardgoodShippingGroup)pShippingGroup).getShippingAddress();

    //fetch the Profile address:
    profileAddress = mCommerceProfileTools.getProfileAddress(getProfile(), pProfileAddressName);

    //copy all values from Profile Address to shipping group address:
    if (profileAddress != null) {
      try {
        OrderTools.copyAddress(profileAddress, shippingGroupAddress);
      }
      catch (CommerceException ce) {
        processException(ce, MSG_UNABLE_TO_COPY_ADDRESS, pRequest, pResponse);
      }
    } else {
      String msg = formatUserMessage(MSG_UNABLE_TO_COPY_ADDRESS, pRequest, pResponse);
      String propertyPath = generatePropertyPath("profileAddressNames");
      addFormException(new DropletFormException(msg, propertyPath, MSG_UNABLE_TO_COPY_ADDRESS));
    }
    //Save the address name in the ShippingGroup:
    pShippingGroup.setDescription(pProfileAddressName);
  }


  /**
   * This method does two things: checks to see if a new address has been input
   * by the user and else copies an existing profile address to the shippingGroup.
   * If the property @see #shipToAddressName is equal to @see #NEW then a new
   * profileAddress is created using the @see #commerceProfileTools.  The nickname
   * of the new address is specified using the <code>shipToAddressName</code>
   * property.  After the address has been created, we add it to the list of addresses
   * to be copied via the <code>addressesToCopy</code> property.  This will then allow
   * the address to be copied from the order to the profile (once info has been filled in).
   *
   * <P>
   *
   * If a new shipping address is not specified, that means that the user selected
   * an address that exists in their profile.  This profile address is copied
   * to the order via the @see #copyProfileAddressToShippingGroupAddress method.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  public void updateShippingAddress(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
    throws IOException, ServletException, RepositoryException
  {
    if (mShipToAddressName.equalsIgnoreCase(NEW)) {
      addNewShippingAddress(pRequest, pResponse);
    }
    else {
      copyExistingAddress(pRequest, pResponse);
    }

  }

  /**
   * A shipping address exists in the getShippingGroup(), but not in the Profile so it needs to be propogated
   * there.  Check to see if the shipping address is currently empty; if it is then copy the address to that
   * location, else place it in the secondary address list using the nickname supplied via the
   * newShippingAddressProperty.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   */
  protected void addNewShippingAddress(DynamoHttpServletRequest pRequest,
                                       DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    ShippingGroup sg = getShippingGroup();
    if (sg == null) {
      String msg = formatUserMessage(MSG_ERROR_ADDING_ADDRESS, pRequest, pResponse);
      String propertyPath = generatePropertyPath("shippingGroup");
      addFormException(new DropletFormException(msg, propertyPath, MSG_ERROR_ADDING_ADDRESS));
      return;
    }

    try {
      if (mCommerceProfileTools.isAddressEmpty(mCommerceProfileTools.getDefaultShippingAddress(getProfile()))) {

        mCommerceProfileTools.createProfileRepositoryPrimaryAddress(getProfile(),
                                                                    mPropertyManager.getShippingAddressPropertyName(),
                                                                    ((HardgoodShippingGroup)sg).getShippingAddress());
      } else {
        mCommerceProfileTools.createProfileRepositorySecondaryAddress(getProfile(),
                                                                      mNewShippingAddressName,
                                                                      ((HardgoodShippingGroup)sg).getShippingAddress());
      }

      // set nickname of shipping group
      sg.setDescription(mNewShippingAddressName);
    }
    catch (RepositoryException re) {
      processException(re, MSG_ERROR_ADDING_ADDRESS, pRequest, pResponse);
    }
  }

  /**
   * Copies the address named by the property shipToAddressName to the primary shipping group
   * in the order.  (Shipping group returned from call getShippingGroup())  Then, check
   * to see if there is currently a profile in the user's shippingAddress property (via the
   * the CommerceProfileTools).  If there is not one, then copy the address to that location.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   */
  protected void copyExistingAddress(DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, RepositoryException
  {
    ShippingGroup sg = getShippingGroup();
    if (sg == null) {
      String msg = formatUserMessage(MSG_ERROR_ADDING_ADDRESS, pRequest, pResponse);
      String propertyPath = generatePropertyPath("shippingGroup");
      addFormException(new DropletFormException(msg, propertyPath, MSG_ERROR_ADDING_ADDRESS));
      return;
    }

    copyProfileAddressToShippingGroupAddress (mShipToAddressName,
                                              sg,
                                              pRequest,
                                              pResponse);

    // see if the address needs to be placed in to default shipping group
    if (mCommerceProfileTools.isAddressEmpty(mCommerceProfileTools.getDefaultShippingAddress(getProfile()))) {
      //fetch the Profile address:
      MutableRepositoryItem mutProfile = (MutableRepositoryItem) getProfile();
      MutableRepository mutRepository = (MutableRepository)mutProfile.getRepository();
      RepositoryItem profileAddress = 
        mCommerceProfileTools.getProfileAddress(getProfile(), mShipToAddressName);
      mutProfile.setPropertyValue(mPropertyManager.getShippingAddressPropertyName(),
                                  profileAddress);
      mutRepository.updateItem(mutRepository.getItemForUpdate(mutProfile.getRepositoryId(), mutProfile.getItemDescriptor().getItemDescriptorName()));
    }
  }


  /**
   * Copy all the addresses that currently exist in the Order's list of Shipping Groups to
   * the Profile's list of secondary addresses.  This is only done if the address does not
   * yet exist in the user's list of secondary addresses.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   */
  protected void copyAddressesToProfile(DynamoHttpServletRequest pRequest,
                                        DynamoHttpServletResponse pResponse)
    throws IOException, ServletException
  {
    Address fromAddress;
    RepositoryItem toAddress;

    List shippingGroups = getOrder().getShippingGroups();
    Map secondaryAddressMap = (Map) getProfile().getPropertyValue(mPropertyManager.getSecondaryAddressPropertyName());
    int numAddressToCopy = mAddressesToCopy.length;

    for (int i=0; i<numAddressToCopy; i++) {
      try {
        fromAddress = ((HardgoodShippingGroup)(getOrderManager().getShippingGroupManager().getShippingGroupByDescription(shippingGroups, mAddressesToCopy[i]))).getShippingAddress();
        toAddress = mCommerceProfileTools.getProfileAddress(getProfile(), mAddressesToCopy[i]);

        if (fromAddress == null || toAddress == null) {
          String msg = formatUserMessage(MSG_ERROR_ADDING_ADDRESS, pRequest, pResponse);
          String propertyPath = generatePropertyPath("mAddressesToCopy");
          addFormException(new DropletFormException(msg, propertyPath, MSG_ERROR_ADDING_ADDRESS));
        } else {
          OrderTools.copyAddress(fromAddress, toAddress);
        }
      } catch (CommerceException ce) {
        processException(ce, MSG_ERROR_ADDING_ADDRESS, pRequest, pResponse);
      }
    }
  }

  //---------------------------------------------------
  // Multiple Payment Functions
  //---------------------------------------------------

  /**
   * Method that deals with setting the payment methods that are to be
   * used in an order.  The steps that are done to perform this are:
   *
   * <UL>
   *    <LI> remove any old payment groups from the order
   *    <LI> see if any gift certificates need to be claimed
   *         and made into GiftCertificate payment groups
   *    <LI> merge the payment groups listed in the properties
   *         creditCardPaymentGroup and GiftCertificatePaymentGroup
   *         into the order.
   * </ul>
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   */
  protected void setPaymentMethodsInOrder(DynamoHttpServletRequest pRequest,
                                          DynamoHttpServletResponse pResponse)
    throws CommerceException,
    IOException,
    ServletException
  {
    List giftCertificateAmounts = null;

    try {
      removePaymentGroupsFromOrder(pRequest, pResponse);
      giftCertificateAmounts = createGiftCertificatePaymentGroups(pRequest, pResponse);
      if (isNewCreditCards() || isNewGiftCertificates()) {
        mergePaymentGroupsToOrder(giftCertificateAmounts, pRequest, pResponse);
      }
    } catch (PropertyNotFoundException pnfe) {
      if (isLoggingError())
        logError(pnfe);
    } catch (RepositoryException re) {
      if (isLoggingError())
        logError(re);
    }
  }

  /**
   * Removes all payment groups from the order.
   *
   * @exception CommerceException if an error occurs
   */
  protected void removePaymentGroupsFromOrder(DynamoHttpServletRequest pRequest,
                                              DynamoHttpServletResponse pResponse)
    throws CommerceException, ServletException, IOException
  {
    PaymentGroup pg;
    Order order = getOrder();
    List paymentGroups = order.getPaymentGroups();
    if (paymentGroups != null && paymentGroups.size() > 0) {
      for (int i=0; i<paymentGroups.size(); i++) {
        pg = (PaymentGroup)paymentGroups.get(i);
        synchronized (order) {
          if (isLoggingDebug())
            logDebug("removing payment group: " + pg.getId() + " from order");
          mOrderManager.getPaymentGroupManager().removeAllRelationshipsFromPaymentGroup(order, pg.getId());
          mOrderManager.getPaymentGroupManager().removePaymentGroupFromOrder(order, pg.getId());
          mOrderManager.updateOrder(order);
          i--;
        }
      }
    }
  }


  /**
   * This method knows about two types of payment methods:
   * CreditCards and GiftCertificates.  It checks to see
   * which type the parameter pPaymentGroup is and then
   * calls the corresponding isCreditCardEmpty/isGiftCertificateEmpty
   * method and returns the value from those methods.
   *
   *
   * @param pPaymentGroup payment group to test for isEmpty
   * @return true if the payment group is empty
   * @exception CommerceException if the supplied parameter is not of type
   * CreditCard or GiftCertificate.
   */
  protected boolean isPaymentGroupEmpty(Object pPaymentGroup,
                                        DynamoHttpServletRequest pRequest,
                                        DynamoHttpServletResponse pResponse)
    throws CommerceException, ServletException, IOException
  {
    if (pPaymentGroup instanceof CreditCard) {
      return isCreditCardEmpty((CreditCard)pPaymentGroup);
    } else if (pPaymentGroup instanceof GiftCertificate) {
      return isGiftCertificateEmpty((GiftCertificate)pPaymentGroup);
    } else {
      throw new CommerceException(formatUserMessage(UNSUPPORTED_PAYMENTGROUP, pRequest,
                                                    pResponse));
    }
  }

  /**
   * Checks to see if a CreditCard object is empty.  Empty
   * means that certain necessary fields are missing.  The properties
   * that it checks for are those specified by the creditCardProperties
   * String array.
   *
   * <P>
   *
   * This behavior can be overriden by making additions to the String array
   * creditCardProperties, or if necessary extending this method.
   *
   * @param pCreditCard a value of type 'CreditCard'
   * @return true if the payment group is empty.
   * @deprecated this method has been moved to CommerceProfileTools
   */
  protected boolean isCreditCardEmpty(CreditCard pCreditCard)
  {
    return mCommerceProfileTools.isCreditCardEmpty(pCreditCard);
  }


  /**
   * Checks to see if a GiftCertificate object is empty.  Empty
   * means that certain necessary fields are missing.  The properties
   * that it checks for are those specified by the GiftCertificateProperties
   * String array.
   *
   * <P>
   *
   * This behavior can be overriden by making additions to the String array
   * GiftCertificateProperties, or if necessary extending this method.
   *
   * @param pGiftCertificate a value of type 'GiftCertificate'
   * @return true if the payment group is empty.
   */
  protected boolean isGiftCertificateEmpty(GiftCertificate pGiftCertificate) {
    try {
      if (mGiftCertificateProperties != null) {
        for (int i=0; i<mGiftCertificateProperties.length; i++) {
          Object value = DynamicBeans.getPropertyValue(pGiftCertificate, mGiftCertificateProperties[i]);
          if (!(value == null || value.toString().equals("")))
            return false;
        }
      }
    } catch (PropertyNotFoundException pnfe) {
      if (isLoggingError())
        logError(pnfe);
    }
    return true;
  }


  /**
   * This method will parse a string and attempt to create giftCertificatePaymentGroups
   * out of the tokens of the string.  This is done in the following manner:
   *
   * <UL>
   *   <LI> obtain the possible list of gift certificates specified by the
   *        <code>giftCertificateNumbers</code> property
   *   <LI> tokenize this list into the various tokens, where each token
   *        could be a gift certificate claim code.  We tokenize the input here
   *        because the idea of a user being able to input multiple gift certificate
   *        claim codes on a single line is supported.
   *   <LI> claim the gift certificate using the claimableManager to obtain the gift
   *        certificate.
   *   <LI> Create a new giftCertificate paymentGroup using the @see OrderManager
   *   <LI> copy the amount of the gift certificate into the List that will be returned
   * </UL>
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @return List of the amounts of each giftCertificate that was claimed
   * @exception CommerceException if an error occurs
   * @exception ClaimableException if an error occurs
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   * @exception PropertyNotFoundException if an error occurs
   */
  protected List createGiftCertificatePaymentGroups(DynamoHttpServletRequest pRequest,
                                                    DynamoHttpServletResponse pResponse)
    throws CommerceException,
    ClaimableException,
    IOException,
    ServletException,
    RepositoryException,
    PropertyNotFoundException
  {
    String gcClaimCode;
    List giftCertificateAmounts = null;
    RepositoryItem giftCertificate;
    String gcItemDescriptorName = mClaimableTools.getGiftCertificateItemDescriptorName();

    if (mGiftCertificateNumbers == null) {
      return null;
    }

    StringTokenizer st = new StringTokenizer(mGiftCertificateNumbers.trim());
    int gcCount = st.countTokens();
    if (isLoggingDebug())
      logDebug("There are " + gcCount + " gift certificates to be collected");

    if (gcCount > 0) {
      mGiftCertificatePaymentGroups = new ArrayList(gcCount);
      giftCertificateAmounts = new ArrayList(gcCount);

      while (st.hasMoreTokens()) {
        // claim each gc
        gcClaimCode = st.nextToken();
        if (isLoggingDebug())
          logDebug("Going to claim gift certificate: " + gcClaimCode);

        giftCertificate = mClaimableManager.claimItem(gcClaimCode);
        if (giftCertificate != null &&
            giftCertificate.getItemDescriptor().getItemDescriptorName().equals(gcItemDescriptorName))
        {
          if (isLoggingDebug())
            logDebug("Claimed gift certificate, going to add paymentGroup");
                
          GiftCertificate gcPaymentGroup= (GiftCertificate)mOrderManager.getPaymentGroupManager().createPaymentGroup(mGiftCertificatePaymentTypeName);
          mOrderManager.getPaymentGroupManager().initializeGiftCertificate(gcPaymentGroup, getProfile().getRepositoryId(), gcClaimCode);
          mGiftCertificatePaymentGroups.add(gcPaymentGroup);
          
          ClaimableTools ct = getClaimableTools();
          String amountProperty = ct.getGiftCertificateAmountPropertyName();
          String amtRemainingProperty = ct.getGiftCertificateAmountAuthorizedPropertyName();

          PricingTools pt = this.getCommerceProfileTools().getPricingTools();
	      double total = ((Double)giftCertificate.getPropertyValue(amountProperty)).doubleValue();
 	      double authorized =((Double)giftCertificate.getPropertyValue(amtRemainingProperty)).doubleValue();
          double difference = pt.round(total - authorized);
  	      if (difference > 0)
            giftCertificateAmounts.add(new Double(difference));
          else
            giftCertificateAmounts.add(new Double(0.0D));

        }else {
          // unable to get gift certificate from repository, log error
          String msg = formatUserMessage(NO_GIFTCERTIFICATE_FOUND, gcClaimCode, pRequest, pResponse);
          addFormException(new DropletException(msg, NO_GIFTCERTIFICATE_FOUND));
        }
      }
    }
    return giftCertificateAmounts;
  }


  /**
   * Check two lists of payment groups: giftCertificatePaymentGroups and
   * creditCardPaymentGroups.  Each paymentGroup that exists in these lists
   * is added to the Order's list of paymentGroups.  Gift certificates are
   * prepended to the Order's list of payment methods.  CreditCards are
   * appended onto the list of payment groups.  This ensures that
   * giftCertificates are used before creditCards.
   *
   * @param pGiftCertificateAmounts a list of the amounts that each gift certificate represents
   * @param pRequest servletRequest object
   * @param pResponse servletResponse object
   */
  protected void mergePaymentGroupsToOrder(List pGiftCertificateAmounts,
                                           DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
    throws CommerceException, ServletException, IOException
  {
    int size;
    PaymentGroupOrderRelationship rel;
    PaymentGroup pg;
    Order order = getOrder();

    if (pGiftCertificateAmounts != null && mGiftCertificatePaymentGroups != null) {
      if ((pGiftCertificateAmounts.size() != mGiftCertificatePaymentGroups.size())) {
        if (isLoggingError()) {
          String msg = formatUserMessage(UNEQUAL_GC_SIZE, pRequest, pResponse);
          logError(msg);
        }
        return;
      }
    }
    // go through each payment group list and prepend the gift certificates
    // to the Order's list of paymentgroups.
    // append the list of creditCard objects onto the list of payment groups
    if (isNewGiftCertificates()) {
      size = mGiftCertificatePaymentGroups.size();
      for (int i=0; i<size; i++) {
        synchronized(order) {
          if (isLoggingDebug())
            logDebug("adding giftCertificate to the Order");
          pg = (PaymentGroup)mGiftCertificatePaymentGroups.get(i);
          mOrderManager.getPaymentGroupManager().addPaymentGroupToOrder(order,
                                               pg,
                                               0);

          mOrderManager.addOrderAmountToPaymentGroup(order,
                                                     pg.getId(),
                                                     ((Double)pGiftCertificateAmounts.get(i)).doubleValue());
          mOrderManager.updateOrder(order);

        }
      }
    }

    if (isNewCreditCards()) {
      // we only support the notion of a single credit card, so remove
      // any existing credit card objects from the order.
      removeCreditCardPaymentGroupsFromOrder();
      size = mCreditCardPaymentGroups.size();
      synchronized(order) {
        for (int i=0; i<size; i++) {
          if (isLoggingDebug())
            logDebug("adding CreditCard to the Order");
          pg = (PaymentGroup)mCreditCardPaymentGroups.get(i);
	  mOrderManager.getPaymentGroupManager().addPaymentGroupToOrder(order,
                                               pg);

          mOrderManager.addRemainingOrderAmountToPaymentGroup(order, pg.getId());
          mOrderManager.updateOrder(order);
        }
      }
    }
  }


  /**
   * Determines if a relationship exists between a specified paymentGroup and the
   * specified order object.  If a relationship exists, true is returned.
   *
   * @param pOrder user's order
   * @param pPaymentGroupId paymentGrou checked for relationship
   * @return true if a relationship exists
   * @exception CommerceException if an error occurs
   */
  protected boolean isOrderRelationshipExists(Order pOrder,
                                              String pPaymentGroupId)
    throws CommerceException
  {
    try {
      mOrderManager.getPaymentGroupManager().getPaymentGroupOrderRelationship(pOrder, pPaymentGroupId);
    } catch (RelationshipNotFoundException rnfe) {
      return false;
    }
    return true;
  }

  /**
   * This function removes all the credit card paymentgroups from the order.
   * It is used in this application because we enforce that no more than
   * one CreditCard payment group exists at a time.  If you want to allow
   * from multiple CreditCard payment groups, this should not be called.
   *
   * @exception CommerceException if an error occurs
   */
  protected void removeCreditCardPaymentGroupsFromOrder()
    throws CommerceException
  {
    boolean exists = true;
    PaymentGroup pg;                // reference to the current payment group
    Order order = getOrder();
    List paymentGroups = order.getPaymentGroups();

    if (paymentGroups != null && paymentGroups.size() > 0) {
      for (int i=0; i<paymentGroups.size(); i++) {
        synchronized (order) {
          pg = (PaymentGroup)paymentGroups.get(i);
          if (pg instanceof CreditCard) {
            mOrderManager.getPaymentGroupManager().removeAllRelationshipsFromPaymentGroup(order, pg.getId());
            mOrderManager.getPaymentGroupManager().removePaymentGroupFromOrder(order, pg.getId());
            mOrderManager.updateOrder(order);
            i--;
          }
        }
      }
    }
  }

  /**
   * Check to see if there have been any new credit payment groups added
   * to the List of creditCardPaymentGroups.  If there has, then it needs
   * to be merged to the order's PaymentGroups.
   *
   * <P>
   *
   * Because the list of credit cards defaults to one, return true only
   * if there is at least one CreditCard that is not empty.
   *
   *
   * @return true if there are new credit card payment groups
   */
  protected boolean isNewCreditCards() {
    int size;
    if (mCreditCardPaymentGroups != null && mCreditCardPaymentGroups.size() > 0) {
      size = mCreditCardPaymentGroups.size();
      for (int i=0; i<size; i++) {
	if (!isCreditCardEmpty((CreditCard)mCreditCardPaymentGroups.get(i))) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Check to see if there have been any new gift certificate payment groups
   * added to the List of giftCertificatePaymentGroups.  If there has, then
   * it needs to be merged to the order's PaymentGroups.
   *
   * <P>
   *
   * Ensure that if there are GiftCertificates that they are not empty. i.e. they
   * have information inputted into their necessary fields, as specified by the
   * giftCertificateProperties property.
   *
   * @return true if there are new gift certificate payment groups
   */
  protected boolean isNewGiftCertificates() {
    int size;
    if (mGiftCertificatePaymentGroups != null && mGiftCertificatePaymentGroups.size() > 0) {
      size = mGiftCertificatePaymentGroups.size();
      for (int i=0; i<size; i++) {
        if (!isGiftCertificateEmpty((GiftCertificate)mGiftCertificatePaymentGroups.get(i))) {
          return true;
        }
      }
    }

    return false;
  }

  //-------------------------------------
  // method: runProcessValidateShippingGroups
  //-------------------------------------
  /**
   * Run the pipeline which should be executed when the <code>handleMoveToPurchaseInfo</code> method is invoked
   * @param pOrder the order to reprice
   * @param pPricingModels the set of all pricing models for the user (item, order, shipping, tax)
   * @param pLocale the locale that the order should be priced within
   * @param pProfile the user who owns the order
   * @param pExtraParameters A Map of extra parameters to be used in the pricing
   */
  protected boolean runProcessValidateShippingGroups(Order pOrder,
                                                     PricingModelHolder pPricingModels,
                                                     Locale pLocale, RepositoryItem pProfile,
                                                     Map pExtraParameters)
    throws RunProcessException
  {
    PipelineResult result = runProcess(getValidateShippingGroupsChainId(), pOrder,
                                       pPricingModels, pLocale, pProfile, pExtraParameters);
    processPipelineErrors(result);
    return result.hasErrors();
  }
}   // end of class




