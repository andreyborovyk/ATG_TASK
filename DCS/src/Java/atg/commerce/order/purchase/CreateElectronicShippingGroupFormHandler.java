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
import java.io.IOException;

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.order.ElectronicShippingGroup;
import atg.commerce.order.processor.ValidateShippingGroupPipelineArgs;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.address.AddressBookException;
import atg.userprofiling.address.AddressMetaInfo;
import atg.userprofiling.address.AddressNicknameGenerator;

/**
 * The <code>CreateElectronicShippingGroupFormHandler</code> class is used to create
 * a ElectronicShippingGroup. This is optionally added to a ShippingGroupMapContainer.
 *
 * @beaninfo
 *   description: A formhandler which allows the user to create an ElectronicShippingGroup.
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CreateElectronicShippingGroupFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.droplet.GenericFormHandler
 * @see atg.commerce.order.purchase.PurchaseProcessFormHandler
 */

public class CreateElectronicShippingGroupFormHandler
extends PurchaseProcessFormHandler
implements CreateShippingGroupFormHandler
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CreateElectronicShippingGroupFormHandler.java#2 $$Change: 651448 $";

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------
  public static final String MSG_GENERATE_NICKNAME = "errorGenerateNickname";
  public static final String MSG_VALIDATE_SHIPPING_GROUP = "errorValidateShippingGroup";
  public static final String MSG_COULD_NOT_GENERATE_VALID_NICKNAME = "couldNotGenerateValidNickname";
  
  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //---------------------------------------------------------------------------
  // property: ElectronicShippingGroup
  //---------------------------------------------------------------------------
  ElectronicShippingGroup mElectronicShippingGroup;

  /**
   * Set the ElectronicShippingGroup property.
   * @param pElectronicShippingGroup an <code>ElectronicShippingGroup</code> value
   * @beaninfo description: The new ElectronicShippingGroup.
   */
  public void setElectronicShippingGroup(ElectronicShippingGroup pElectronicShippingGroup) {
    mElectronicShippingGroup = pElectronicShippingGroup;
  }

  /**
   * Return the ElectronicShippingGroup property. This method exposes the ElectronicShippingGroup
   * as a JavaBean property so that it may be edited directly from a .jhtml page. If this is
   * null, then the ShippingGroupManager is used to create a new ElectronicShippingGroup.
   *
   * @return an <code>ElectronicShippingGroup</code> value
   */
  public ElectronicShippingGroup getElectronicShippingGroup() {
    if (mElectronicShippingGroup == null) {
      try {
        mElectronicShippingGroup = (ElectronicShippingGroup) getShippingGroupManager().createShippingGroup(getElectronicShippingGroupType());
      } catch (CommerceException exc) {
        if (isLoggingError()) logError(exc);
      }
    }
    return mElectronicShippingGroup;
  }

  //---------------------------------------------------------------------------
  // property: EmailAddress
  //---------------------------------------------------------------------------
  String mEmailAddress;

  /**
   * Set the EmailAddress property.
   * It is preferred to use the <code>electronicShippingGroup.emailAddress</code> rather than this property. Out of box, ATG
   * provides only this electronic shipping group property. ATG believes that you may add additional properties to the electronic 
   * shipping group. Instead of adding each additional properties to this form handler, you could access the additional properties 
   * from the <code>electronicShippingGroup</code> property.
   * <p>
   * You could use this property or <code>electronicShippingGroup.emailAddress</code> to set or access the email address.
   * If the <code>emailAddress</code> property is not empty, this email address value is set in the 
   * <code>electronicShippingGroup.emailAddress</code>. If you start using the <code>electronicShippingGroup.emailAddress</code> property,
   * the <code>emailAddress</code> property won't be updated. The <code>emailAddress</code> is a temporary place holder and it does not need to be
   * up-to-date.
   * 
   * @param pEmailAddress a <code>String</code> value
   * @beaninfo description: The email address for the ElectronicShippingGroup.
   */
  public void setEmailAddress(String pEmailAddress) {
    mEmailAddress = pEmailAddress;
  }

  /**
   * Return the EmailAddress property.
   * It is preferred to use the <code>electronicShippingGroup.emailAddress</code> rather than this property. Out of box, ATG
   * provides only this electronic shipping group property. ATG believes that you may add additional properties to the electronic 
   * shipping group. Instead of adding each additional properties to this form handler, you could access the additional properties 
   * from the <code>electronicShippingGroup</code> property.
   * <p>
   * You could use this property or <code>electronicShippingGroup.emailAddress</code> to set or access the email address.
   * If the <code>emailAddress</code> property is not empty, this email address value is set in the 
   * <code>electronicShippingGroup.emailAddress</code>. If you start using the <code>electronicShippingGroup.emailAddress</code> property,
   * the <code>emailAddress</code> property won't be updated. The <code>emailAddress</code> is a temporary place holder and it does not need to be
   * up-to-date.
   * @return a <code>String</code> value
   */
  public String getEmailAddress() {
    return mEmailAddress;
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
  // property: ElectronicShippingGroupType
  //---------------------------------------------------------------------------
  String mElectronicShippingGroupType;

  /**
   * Set the ElectronicShippingGroupType property.
   * @param pElectronicShippingGroupType a <code>String</code> value
   * @beaninfo description: The ShippingGroup type for ElectronicShippingGroups.
   */
  public void setElectronicShippingGroupType(String pElectronicShippingGroupType) {
    mElectronicShippingGroupType = pElectronicShippingGroupType;
  }

  /**
   * Return the ElectronicShippingGroupType property.
   * @return a <code>String</code> value
   */
  public String getElectronicShippingGroupType() {
    return mElectronicShippingGroupType;
  }

  //---------------------------------------------------------------------------
  // property: ElectronicShippingGroupName
  //---------------------------------------------------------------------------
  String mElectronicShippingGroupName;

  /**
   * Set the ElectronicShippingGroupName property.
   * @param pElectronicShippingGroupName a <code>String</code> value
   * @beaninfo description: The name of the new ElectronicShippingGroup.
   */
  public void setElectronicShippingGroupName(String pElectronicShippingGroupName) {
    mElectronicShippingGroupName = pElectronicShippingGroupName;
  }

  /**
   * Return the ElectronicShippingGroupName property.
   * @return a <code>String</code> value
   */
  public String getElectronicShippingGroupName() {
    return mElectronicShippingGroupName;
  }

  //---------------------------------------------------------------------------
  // property: NewElectronicShippingGroupSuccessURL
  //---------------------------------------------------------------------------
  String mNewElectronicShippingGroupSuccessURL;

  /**
   * Set the NewElectronicShippingGroupSuccessURL property.
   * @param pNewElectronicShippingGroupSuccessURL a <code>String</code> value
   * @beaninfo description: URL for redirection upon success.
   */
  public void setNewElectronicShippingGroupSuccessURL(String pNewElectronicShippingGroupSuccessURL) {
    mNewElectronicShippingGroupSuccessURL = pNewElectronicShippingGroupSuccessURL;
  }

  /**
   * Return the NewElectronicShippingGroupSuccessURL property.
   * @return a <code>String</code> value
   */
  public String getNewElectronicShippingGroupSuccessURL() {
    return mNewElectronicShippingGroupSuccessURL;
  }

  //---------------------------------------------------------------------------
  // property: NewElectronicShippingGroupErrorURL
  //---------------------------------------------------------------------------
  String mNewElectronicShippingGroupErrorURL;

  /**
   * Set the NewElectronicShippingGroupErrorURL property.
   * @param pNewElectronicShippingGroupErrorURL a <code>String</code> value
   * @beaninfo description: URL for redirection upon error.
   */
  public void setNewElectronicShippingGroupErrorURL(String pNewElectronicShippingGroupErrorURL) {
    mNewElectronicShippingGroupErrorURL = pNewElectronicShippingGroupErrorURL;
  }

  /**
   * Return the NewElectronicShippingGroupErrorURL property.
   * @return a <code>String</code> value
   */
  public String getNewElectronicShippingGroupErrorURL() {
    return mNewElectronicShippingGroupErrorURL;
  }

  private AddressNicknameGenerator mNicknameGenerator;

  /**
   * Returns the nicknameGenerator
   * @return the nicknameGenerator
   */
  public AddressNicknameGenerator getNicknameGenerator() {
    return mNicknameGenerator;
  }

  /**
   * Sets the nicknameGenerator
   * @param pNicknameGenerator the nicknameGenerator to set
   */
  public void setNicknameGenerator(AddressNicknameGenerator pNicknameGenerator) {
    mNicknameGenerator = pNicknameGenerator;
  }

  private boolean mAssignNewShippingGroupAsDefault=true;
  /**
   * Returns the assignNewShippingGroupAsDefault
   * <p>
   * To support the backward compatibility, this flag is set to <code>true</code>. This flag is used only if the
   * <code>addToContainer</code> flag is set to true.
   * <p>
   * This flag is used to set the new shipping group as default shipping group for the current user. 
   * If the flag is false, the new shipping group will not be set as default shipping group for the user.
   * 
   * @return the assignNewShippingGroupAsDefault
   */
  public boolean isAssignNewShippingGroupAsDefault() {
    return mAssignNewShippingGroupAsDefault;
  }

  /**
   * Sets the assignNewShippingGroupAsDefault
   * <p>
   * To support the backward compatibility, this flag is set to <code>true</code>. This flag is used only if the
   * <code>addToContainer</code> flag is set to true.
   * <p>
   * This flag is used to set the new shipping group as default shipping group for the current user. 
   * If the flag is false, the new shipping group will not be set as default shipping group for the user.
   * 
   * @param pAssignNewShippingGroupAsDefault the assignNewShippingGroupAsDefault to set
   */
  public void setAssignNewShippingGroupAsDefault(
      boolean pAssignNewShippingGroupAsDefault) {
    mAssignNewShippingGroupAsDefault = pAssignNewShippingGroupAsDefault;
  }

  //---------------------------------------------------------------------------
  // property: validateShippingGroupChainId

  /**
   * The name of the chain that is used to validate shipping group
   */
  String mValidateShippingGroupChainId = "validateShippingGroup";

  /**
   * Set the validateShippingGroupChainId property.
   * This chain id is used to validate the shipping group.
   * 
   * @param pValidateShippingGroupChainId
   *    The validateShippingGroupChainId to be used to validate the shipping group
   */
  public void setValidateShippingGroupChainId(String pValidateShippingGroupChainId) {
    mValidateShippingGroupChainId = pValidateShippingGroupChainId;
  }

  /**
   * Return the validateShippingGroupChainId property.
   * This chain id is used to validate the shipping group.
   * @return 
   *    The validateShippingGroupChainId to validate the shipping group
   */
  public String getValidateShippingGroupChainId() {
    return mValidateShippingGroupChainId;
  }
  
  private boolean mValidateShippingGroup=false;
  /**
   * Returns the validateShippingGroup
   * To support the backward compatibility, this flag is set to <code>false</code> .
   * If you prefer to validate the shipping group, you need to enable this flag.
   * 
   * @return the validateShippingGroup
   */
  public boolean isValidateShippingGroup() {
    return mValidateShippingGroup;
  }

  /**
   * Sets the validateShippingGroup
   * To support the backward compatibility, this flag is set to <code>false</code>.
   * If you prefer to validate the shipping group, you need to enable this flag.
   * @param pValidateShippingGroup the validateShippingGroup to set
   */
  public void setValidateShippingGroup(boolean pValidateShippingGroup) {
    mValidateShippingGroup = pValidateShippingGroup;
  }
  

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------


  /**
   * Creates a new <code>CreateElectronicShippingGroupFormHandler</code> instance.
   *
   */
  public CreateElectronicShippingGroupFormHandler () {}

  //--------------------------------------------------
  // Methods
  //--------------------------------------------------

  /**
   * <code>handleNewElectronicShippingGroup</code> is used to create a new ElectronicShippingGroup.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleNewElectronicShippingGroup(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse)
  throws ServletException, IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CreateElectronicShippingGroupOrderFormHandler.handleNewElectronicShippingGroup";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {

      Transaction tr = null;
      try {
        tr = ensureTransaction();

        if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

        //Check if any form errors exist.  If they do, redirect to Error URL:
        if (!checkFormRedirect(null, getNewElectronicShippingGroupErrorURL(), pRequest, pResponse))
          return false;

        preCreateElectronicShippingGroup(pRequest, pResponse);

        //Check if any form errors exist.  If they do, redirect to Error URL:
        if (!checkFormRedirect(null, getNewElectronicShippingGroupErrorURL(), pRequest, pResponse))
          return false;

        createElectronicShippingGroup(pRequest, pResponse);

        //Check if any form errors exist.  If they do, redirect to Error URL:
        if (!checkFormRedirect(null, getNewElectronicShippingGroupErrorURL(), pRequest, pResponse))
          return false;

        postCreateElectronicShippingGroup(pRequest, pResponse);

        return checkFormRedirect (getNewElectronicShippingGroupSuccessURL(),
            getNewElectronicShippingGroupErrorURL(), pRequest, pResponse);
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
   * <code>preCreateElectronicShippingGroup</code> is for work that must happen before
   * a new ElectronicShippingGroup is created.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preCreateElectronicShippingGroup(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse)
  throws ServletException, IOException
  {
  }

  /**
   * <code>postCreateElectronicShippingGroup</code> is for work that must happen after
   * a new ElectronicShippingGroup is created.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postCreateElectronicShippingGroup(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse)
  throws ServletException, IOException
  {
  }

  /**
   * <code>createElectronicShippingGroup</code> creates a new ElectronicShippingGroup.
   * The ElectronicShippingGroupType property gives the type of ShippingGroup to create.
   * The ElectronicShippingGroupName property gives the name of the new ShippingGroup,
   * as it will be referenced in the ShippingGroupMapContainer. If
   * <code>isAddToContainer</code> is true then the ElectronicShippingGroup is added to the
   * ShippingGroupMapContainer and made the default ShippingGroup.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   */
  public void createElectronicShippingGroup(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) {

    // add to Container
    //If we do not want to add to the container, it is waste of time to do
    //all validation and nickname generation 
    if (isAddToContainer()) {
      ElectronicShippingGroup shippingGroup = getElectronicShippingGroup();
      // assign the email address property if the email address is not empty
      // this check is added to remain backward compatibility. If the emailAddress property is not
      // used, then the emailAddress property in the shipping group is not set.
      if (!StringUtils.isEmpty(getEmailAddress())) {
        shippingGroup.setEmailAddress(getEmailAddress());
      }

      if (isValidateShippingGroup()) {
        try {
          validateShippingGroup(pRequest, pResponse);
        } catch (ServletException e) {
          String msg = null;
          try {
            msg = formatUserMessage (MSG_VALIDATE_SHIPPING_GROUP, pRequest, pResponse);
          } catch (Exception e1) {
            //this error is while trying to generate an error message. Thus just log it.
            if (isLoggingError()) logError(e1);
          } 
          //if there is any error while formating user message, we want to still add the error message.
          addFormException(new DropletException(msg, e));
        } catch (IOException e) {
          String msg = null;
          try {
            msg = formatUserMessage (MSG_VALIDATE_SHIPPING_GROUP, pRequest, pResponse);
          } catch (Exception e1) {
            //this error is while trying to generate an error message. Thus just log it.
            if (isLoggingError()) logError(e);
          } 
          //if there is any error while formating user message, we want to still add the error message.
          addFormException(new DropletException(msg, e));
        }
      }

      if (getFormError()) return;

      ShippingGroupMapContainer container = getContainer();
      String name = getElectronicShippingGroupName();

      if (StringUtils.isEmpty(name)) {
        try {
          name = getNicknameGenerator().getNickname(new AddressMetaInfo(), container.getShippingGroupNames());
          if (StringUtils.isEmpty(name)) {
            String msg = null;
            try {
              msg = formatUserMessage (MSG_COULD_NOT_GENERATE_VALID_NICKNAME, pRequest, pResponse);
              addFormException(new DropletException(msg));
            } catch (Exception e1) {
              //this error is while trying to generate an error message. Thus just log it.
              if (isLoggingError()) logError(e1);
            }
          }
        } catch (AddressBookException e) {
          String msg = null;
          try {
            msg = formatUserMessage (MSG_GENERATE_NICKNAME, pRequest, pResponse);
            addFormException(new DropletException(msg));
          } catch (Exception e1) {
            //this error is while trying to generate an error message. Thus just log it.
            if (isLoggingError()) logError(e1);
          } 
        }
      }

      if (getFormError()) return;

      if (isAssignNewShippingGroupAsDefault()) {
        container.setDefaultShippingGroupName(name);
      }
      container.addShippingGroup(name, shippingGroup);
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

  /**
   * This method is invokes the pipeline chain to validate the shipping group.
   * The pipeline chain configured in the <code>validateShippingGroupChainId</code> property 
   * is used to validate the shipping group.
   * <p>
   * The default configured value for <code>validateShippingGroupChainId</code> is <code>validateShippingGroup</code>. 
   */
  protected void validateShippingGroup (DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse)
  throws ServletException, IOException {
    try {
      ValidateShippingGroupPipelineArgs param = new ValidateShippingGroupPipelineArgs();
      param.setShippingGroup(getElectronicShippingGroup());
      param.setLocale(getUserLocale(pRequest, pResponse));
      PipelineResult result = runProcess(getValidateShippingGroupChainId(), param);
      //If there is any errors, this call adds the form exceptions to the form handler
      processPipelineErrors(result);
    } catch (RunProcessException e) {
      String msg = null;
      try {
        msg = formatUserMessage (MSG_VALIDATE_SHIPPING_GROUP, pRequest, pResponse);
      } catch (Exception e1) {
        //this error is while trying to generate an error message. Thus just log it.
        if (isLoggingError()) logError(e);
      }       
      //if there is any error while formating user message, we want to still add the error message.
      addFormException(new DropletException(msg, e));
    }
  } 
}   // end of class
