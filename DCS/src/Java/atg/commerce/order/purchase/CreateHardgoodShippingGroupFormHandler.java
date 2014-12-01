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

package atg.commerce.order.purchase;
//ATG
import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.util.AddressValidator;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.nucleus.ServiceMap;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

/**
 * The <code>CreateHardgoodShippingGroupFormHandler</code> class is used to create
 * a HardgoodShippingGroup. This is optionally added to a ShippingGroupMapContainer.
 *
  * @beaninfo
 *   description: A formhandler which allows the user to create an ElectronicShippingGroup.
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
*
 * @author Ernesto Mireles
 * @author Sivakumar Mallaiyasamy
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CreateHardgoodShippingGroupFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.droplet.GenericFormHandler
 * @see atg.commerce.order.purchase.PurchaseProcessFormHandler
 */

public class CreateHardgoodShippingGroupFormHandler
  extends PurchaseProcessFormHandler
  implements CreateShippingGroupFormHandler
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CreateHardgoodShippingGroupFormHandler.java#2 $$Change: 651448 $";

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------
  public static final String COULD_NOT_ADD_SHIPPING_ADDRESS_TO_PROFILE = "couldNotAddShippingAddressToProfile";
  public static final String COULD_NOT_FIND_SHIPPING_GROUP_OR_ADDRESS = "couldNotFindShippingGroupOrAddress";
  public static final String MSG_VALIDATE_SHIPPING_GROUP = "errorValidateShippingGroup";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //---------------------------------------------------------------------------
  // property: HardgoodShippingGroup
  //---------------------------------------------------------------------------
  HardgoodShippingGroup mHardgoodShippingGroup;

  /**
   * Set the HardgoodShippingGroup property.
   * @param pHardgoodShippingGroup a <code>HardgoodShippingGroup</code> value
   * @beaninfo description: The new ElectronicShippingGroup.
   */
  public void setHardgoodShippingGroup(HardgoodShippingGroup pHardgoodShippingGroup) {
    mHardgoodShippingGroup = pHardgoodShippingGroup;
  }

  /**
  *
  * Set the HardgoodShippingGroup property. Since we are creating new shipping group in the getHardgoodShippingGroup() method and using
  * this formhandler as request scope, we do not want to create so many objects. When we set the same shipping group
  * in the second following request, the ShippingGroup instance is not recognized as HardgoodShippingGroup, instead it is recognised
  * as ShippingGroup. Thus adding this method to set the ShippingGroup.
  *
  * @param pHardgoodShippingGroup a <code>HardgoodShippingGroup</code> value
  * @beaninfo description: The new ElectronicShippingGroup.
  */
 public void setHardgoodShippingGroup(ShippingGroup pHardgoodShippingGroup) {
   if (pHardgoodShippingGroup instanceof HardgoodShippingGroup) {
     mHardgoodShippingGroup = (HardgoodShippingGroup) pHardgoodShippingGroup;
   }
 }

  /**
   * Return the HardgoodShippingGroup property. This method exposes the HardgoodShippingGroup
   * as a JavaBean property so that it may be edited directly from a .jhtml page. If this is
   * null, then the ShippingGroupManager is used to create a new HardgoodShippingGroup.
   *
   * @return a <code>HardgoodShippingGroup</code> value
   */
  public HardgoodShippingGroup getHardgoodShippingGroup() {
    if (mHardgoodShippingGroup == null) {
      try {
        mHardgoodShippingGroup = (HardgoodShippingGroup) getShippingGroupManager().createShippingGroup(getHardgoodShippingGroupType());
      } catch (CommerceException exc) {
        if (isLoggingError()) logError(exc);
      }
    }
    return mHardgoodShippingGroup;
  }

  //---------------------------------------------------------------------------
  // property: AddToContainer
  //---------------------------------------------------------------------------
  boolean mAddToContainer;

  /**
   * Set the AddToContainer property.
   * @param pAddToContainer a <code>boolean</code> value
   * @beaninfo description: Should the ElectronicShippingGroup be added to a ShippingGroupMapContainer?
   */
  public void setAddToContainer(boolean pAddToContainer) {
    mAddToContainer = pAddToContainer;
  }

  /**
   * Return the AddToContainer property.
   * @return a <code>boolean</code> value
   */
  public boolean isAddToContainer() {
    return mAddToContainer;
  }

  //---------------------------------------------------------------------------
  // property: Container
  //---------------------------------------------------------------------------
  ShippingGroupMapContainer mContainer;

  /**
   * Set the Container property.
   * @param pContainer a <code>ShippingGroupMapContainer</code> value
   * @beaninfo description: The container to add the ElectronicShippingGroup to.
   */
  public void setContainer(ShippingGroupMapContainer pContainer) {
    mContainer = pContainer;
  }

  /**
   * Return the Container property.
   * @return a <code>ShippingGroupMapContainer</code> value
   */
  public ShippingGroupMapContainer getContainer() {
    return mContainer;
  }

  //---------------------------------------------------------------------------
  // property: HardgoodShippingGroupType
  //---------------------------------------------------------------------------
  String mHardgoodShippingGroupType;

  /**
   * Set the HardgoodShippingGroupType property.
   * @param pHardgoodShippingGroupType a <code>String</code> value
   * @beaninfo description: The ShippingGroup type for ElectronicShippingGroups.
   */
  public void setHardgoodShippingGroupType(String pHardgoodShippingGroupType) {
    mHardgoodShippingGroupType = pHardgoodShippingGroupType;
  }

  /**
   * Return the HardgoodShippingGroupType property.
   * @return a <code>String</code> value
   */
  public String getHardgoodShippingGroupType() {
    return mHardgoodShippingGroupType;
  }

  //---------------------------------------------------------------------------
  // property: HardgoodShippingGroupName
  //---------------------------------------------------------------------------
  String mHardgoodShippingGroupName;

  /**
   * Set the HardgoodShippingGroupName property.
   * @param pHardgoodShippingGroupName a <code>String</code> value
   * @beaninfo description: The name of the new ElectronicShippingGroup.
   */
  public void setHardgoodShippingGroupName(String pHardgoodShippingGroupName) {
    mHardgoodShippingGroupName = pHardgoodShippingGroupName;
  }

  /**
   * Return the HardgoodShippingGroupName property.
   * @return a <code>String</code> value
   */
  public String getHardgoodShippingGroupName() {
    return mHardgoodShippingGroupName;
  }

  //---------------------------------------------------------------------------
  // property: NewHardgoodShippingGroupSuccessURL
  //---------------------------------------------------------------------------
  String mNewHardgoodShippingGroupSuccessURL;

  /**
   * Set the NewHardgoodShippingGroupSuccessURL property.
   * @param pNewHardgoodShippingGroupSuccessURL a <code>String</code> value
   * @beaninfo description: URL for redirection upon success.
   */
  public void setNewHardgoodShippingGroupSuccessURL(String pNewHardgoodShippingGroupSuccessURL) {
    mNewHardgoodShippingGroupSuccessURL = pNewHardgoodShippingGroupSuccessURL;
  }

  /**
   * Return the NewHardgoodShippingGroupSuccessURL property.
   * @return a <code>String</code> value
   */
  public String getNewHardgoodShippingGroupSuccessURL() {
    return mNewHardgoodShippingGroupSuccessURL;
  }

  //---------------------------------------------------------------------------
  // property: NewHardgoodShippingGroupErrorURL
  //---------------------------------------------------------------------------
  String mNewHardgoodShippingGroupErrorURL;

  /**
   * Set the NewHardgoodShippingGroupErrorURL property.
   * @param pNewHardgoodShippingGroupErrorURL a <code>String</code> value
   * @beaninfo description: URL for redirection upon error.
   */
  public void setNewHardgoodShippingGroupErrorURL(String pNewHardgoodShippingGroupErrorURL) {
    mNewHardgoodShippingGroupErrorURL = pNewHardgoodShippingGroupErrorURL;
  }

  /**
   * Return the NewHardgoodShippingGroupErrorURL property.
   * @return a <code>String</code> value
   */
  public String getNewHardgoodShippingGroupErrorURL() {
    return mNewHardgoodShippingGroupErrorURL;
  }

  boolean mAssignNewShippingGroupAsDefault = true;

  /**
   * @return Returns the assignNewShippingGroupAsDefault.
   */
  public boolean isAssignNewShippingGroupAsDefault() {
    return mAssignNewShippingGroupAsDefault;
  }

  /**
   * @param pAssignNewShippingGroupAsDefault The assignNewShippingGroupAsDefault to set.
   */
  public void setAssignNewShippingGroupAsDefault(
      boolean pAssignNewShippingGroupAsDefault) {
    mAssignNewShippingGroupAsDefault = pAssignNewShippingGroupAsDefault;
  }

  //---------------------------------------------------------------------------
  // property: ShippingGroupInitializers
  //---------------------------------------------------------------------------
  ServiceMap mShippingGroupInitializers;

  /**
   * Set the ShippingGroupInitializers property.
   * @param pShippingGroupInitializers a <code>ServiceMap</code> value
   */
  public void setShippingGroupInitializers(ServiceMap pShippingGroupInitializers) {
    mShippingGroupInitializers = pShippingGroupInitializers;
  }

  /**
   * Return the ShippingGroupInitializers property.
   * @return a <code>ServiceMap</code> value
   */
  public ServiceMap getShippingGroupInitializers() {
    return mShippingGroupInitializers;
  }

  boolean mAddToProfile;

  /**
   * @return Returns the addToProfile.
   */
  public boolean isAddToProfile() {
    return mAddToProfile;
  }

  /**
   * @param pAddToProfile The addToProfile to set.
   */
  public void setAddToProfile(boolean pAddToProfile) {
    mAddToProfile = pAddToProfile;
  }

  AddressValidator mAddressValidator;

  /**
   * @return Returns the addressValidator.
   */
  public AddressValidator getAddressValidator() {
    return mAddressValidator;
  }

  /**
   * @param pAddressValidator The addressValidator to set.
   */
  public void setAddressValidator(AddressValidator pAddressValidator) {
    mAddressValidator = pAddressValidator;
  }

  //to keep the backward compatibility, this flag is set to false.
  //If clients want to validate, they need to turn on this flag and set AddressValidator.
  boolean mValidateAddress;

  /**
   * @return Returns the validateAddress.
   */
  public boolean isValidateAddress() {
    return mValidateAddress;
  }

  /**
   * @param pValidateAddress The validateAddress to set.
   */
  public void setValidateAddress(boolean pValidateAddress) {
    mValidateAddress = pValidateAddress;
  }

  boolean mGenerateNickname;

  /**
   * @return the generateNickname
   */
  public boolean isGenerateNickname() {
    return mGenerateNickname;
  }

  /**
   * @param pGenerateNickname The generateNickname to set.
   */
  public void setGenerateNickname(boolean pGenerateNickname) {
    mGenerateNickname = pGenerateNickname;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>CreateHardgoodShippingGroupFormHandler</code> instance.
   *
   */
  public CreateHardgoodShippingGroupFormHandler () {}

  //--------------------------------------------------
  // Methods
  //--------------------------------------------------

  /**
   * <code>handleNewHardgoodShippingGroup</code> is used to create a new HardgoodShippingGroup.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleNewHardgoodShippingGroup(DynamoHttpServletRequest pRequest,
                                                DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CreateHardgoodShippingGroupOrderFormHandler.handleNewHardgoodShippingGroup";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
  tr = ensureTransaction();

  if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

  //Check if any form errors exist.  If they do, redirect to Error URL:
  if (!checkFormRedirect(null, getNewHardgoodShippingGroupErrorURL(), pRequest, pResponse))
    return false;

  preCreateHardgoodShippingGroup(pRequest, pResponse);

  //Check if any form errors exist.  If they do, redirect to Error URL:
  if (!checkFormRedirect(null, getNewHardgoodShippingGroupErrorURL(), pRequest, pResponse))
    return false;

  createHardgoodShippingGroup(pRequest, pResponse);

  //Check if any form errors exist.  If they do, redirect to Error URL:
  if (!checkFormRedirect(null, getNewHardgoodShippingGroupErrorURL(), pRequest, pResponse))
    return false;

  postCreateHardgoodShippingGroup(pRequest, pResponse);

  return checkFormRedirect (getNewHardgoodShippingGroupSuccessURL(),
          getNewHardgoodShippingGroupErrorURL(), pRequest, pResponse);
      }
      finally {
  if (tr != null) commitTransaction(tr);
  if (rrm != null)
    rrm.removeRequestEntry(myHandleMethod);
      }
    }
    else {
      return false;
    }
  }

  /**
   * <code>preCreateHardgoodShippingGroup</code> is for work that must happen before
   * a new HardgoodShippingGroup is created.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preCreateHardgoodShippingGroup(DynamoHttpServletRequest pRequest,
                                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>postCreateHardgoodShippingGroup</code> is for work that must happen after
   * a new HardgoodShippingGroup is created.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postCreateHardgoodShippingGroup(DynamoHttpServletRequest pRequest,
                                              DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * <code>createHardgoodShippingGroup</code> creates a new HardgoodShippingGroup.
   * The HardgoodShippingGroupType property gives the type of ShippingGroup to create.
   * The HardgoodShippingGroupName property gives the name of the new ShippingGroup,
   * as it will be referenced in the ShippingGroupMapContainer. If
   * <code>isAddToContainer</code> is true then the HardgoodShippingGroup is added to the
   * ShippingGroupMapContainer and made the default ShippingGroup.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @see
   */
  public void createHardgoodShippingGroup(DynamoHttpServletRequest pRequest,
                                          DynamoHttpServletResponse pResponse) {

    if (isLoggingDebug()) logDebug("Entering createHardgoodShippingGroup()");

    HardgoodShippingGroup shippingGroup = getHardgoodShippingGroup();
    ShippingGroupMapContainer container = getContainer();
    String name = getHardgoodShippingGroupName();

    //this is not changing the OTB behavior. Currently OTB getShippingGroupInitializers() are not
    //configured. If our clients want to take advantage of the automatic generation of names, they have
    //to configure the getShippingGroupInitializers()

    if (isValidateAddress()) {
      validateShippingGroup();
      if (getFormError()) return;
    }

    if (StringUtils.isBlank(name)
        && isGenerateNickname()
        && getShippingGroupInitializers() != null
        && getShippingGroupInitializers().size() > 0 ) {
      if (isLoggingDebug()) logDebug("Shipping Group Nickname is NULL. Thus getting a nickname from the address.");
      name = getShippingGroupManager().getNewShippingGroupName(shippingGroup,
          getShippingGroupInitializers().values());
      //set this property. so that we could take advantage in the pages with the new name.
      setHardgoodShippingGroupName(name);
    }

    // add to Container
    if (isAddToContainer()) {
      if (isAssignNewShippingGroupAsDefault()) {
        container.setDefaultShippingGroupName(name);
      }
      container.addShippingGroup(name, shippingGroup);
    }

    //This flag is added to provide the backward compatibility.
    if (isLoggingDebug()) logDebug("Add to Profile flag value is ::" + isAddToProfile());

    if (isAddToProfile()) {
      try {
        getOrderManager().getOrderTools().getProfileTools().createProfileRepositorySecondaryAddress(getProfile(),
            name,
            shippingGroup.getShippingAddress());
      } catch (RepositoryException repexec) {
        try {
          processException(repexec, COULD_NOT_ADD_SHIPPING_ADDRESS_TO_PROFILE, pRequest, pResponse);
        }
        catch (Exception exception) { // exceptions thrown by exception-handling
          if (isLoggingError()) logError(exception);
        }
      }
    }//end of isAddToProfile() check..
  }

  /**
   *
   * This method validates the shipping group and if there is any errors adds as form exceptions.
   * This method checks for the emptiness of nickname, <code>ShippingGroup</code>, <code>ShippingGroup.shippingAddress</code>
   * and uses the <code>addressValidator</code> property to validate the address.
   *
   */
  public void validateShippingGroup () {
    HardgoodShippingGroup shippingGroup = getHardgoodShippingGroup();
    //To get the request and response, we can't change the method signature and this code is already published..
    //Thus getting the request and response from the ServletUtil to validate the
    //shipping group
    DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
    DynamoHttpServletResponse res = ServletUtil.getCurrentResponse();

    if (shippingGroup == null || shippingGroup.getShippingAddress() == null) {
      if (isLoggingDebug()) logDebug("Shipping Group or Shipping Address can't be NULL.");
      try {
        String msg = formatUserMessage(COULD_NOT_FIND_SHIPPING_GROUP_OR_ADDRESS,
            req, res);
        String propertyPath = generatePropertyPath("hardgoodShippingGroup");
        addFormException(new DropletFormException(msg, propertyPath));
      }
      catch (Exception exception) { // exceptions thrown by exception-handling
        if (isLoggingError()) logError(exception);
      }
      return;
    }

    try {
      //pass in the right locale to get the right locale error messages.
      Collection errors = getAddressValidator().validateAddress(shippingGroup.getShippingAddress(),getUserLocale(req, res));
      if (errors != null
          && !errors.isEmpty()) {
        if (isLoggingDebug()) logDebug("Errors in the Shipping address validation.");
        String propertyPath = generatePropertyPath("hardgoodShippingGroup");
        for (Object entry: errors) {
          addFormException(new DropletFormException((String)entry, propertyPath));
        }
      }
    } catch (ServletException e) {
      String msg = null;
      String propertyPath = generatePropertyPath("hardgoodShippingGroup");
      try {
        msg = formatUserMessage (MSG_VALIDATE_SHIPPING_GROUP, req, res);
      } catch (Exception e1) {
        // exceptions thrown by exception-handling
        if (isLoggingError()) logError(e1);
      } 
      //if there is any error while formating user message, we want to still add the error message.
      addFormException(new DropletFormException(msg, e, propertyPath));
    } catch (IOException e) {
      String msg = null;      
      String propertyPath = generatePropertyPath("hardgoodShippingGroup");
      try {
        msg = formatUserMessage (MSG_VALIDATE_SHIPPING_GROUP, req, res);
      } catch (Exception e1) {
        // exceptions thrown by exception-handling
        if (isLoggingError()) logError(e1);
      }
      //if there is any error while formating user message, we want to still add the error message.
      addFormException(new DropletFormException(msg, e, propertyPath));
    }
  }
  // -------------------------------
  // Centralized configuration
  // -------------------------------

  /**
   * Copy property settings from the optional
   * <code>PurchaseProcessConfiguration</code> component. Property
   * values that were configured locally are preserved.
   *
   * Configures the following properties (if not already set):
   * <UL>
   * <LI>container (from shippingGroupMapContainer)
   * </UL>
   **/
  protected void copyConfiguration() {
    super.copyConfiguration();
    if (getConfiguration() != null) {
      if (getContainer() == null) {
        setContainer(getConfiguration().getShippingGroupMapContainer());
      }
    }
  }

}   // end of class
