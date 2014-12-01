/*<ATGCOPYRIGHT>
 * Copyright (C) 2000-2011 Art Technology Group, Inc.
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

package atg.commerce.order;

import atg.commerce.CommerceException;
import atg.commerce.order.ElectronicShippingGroup;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.droplet.DropletException;

import java.io.IOException;
import javax.servlet.ServletException;
import java.util.*;
import javax.transaction.*;

/**
 *
 * This class layers additional functionality on top of the FullShoppingCartFormHandler
 * by allowing another type of good to be added to the shopping cart, soft goods.
 * The FullShoppingCartFormHandler allows users to add goods to their cart that will
 * be added to a hardgood shipping group.  This class adds items to the current order
 * and additionally associates these items with an atg.commerce.order.ElectronicShippingGroup
 * object.
 *
 * <p>
 *
 * Extra information needs to be gathered for an ElectronicShippingGroup since it will
 * be delivered in a different manner than hard goods. i.e. something will be emailed
 * to the user instead of using traditional mail.
 *
 * @beaninfo
 *   description: A form handler which allows one to add softgoods to an order
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 * @author Ashley J. Streb
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/SoftGoodFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class SoftGoodFormHandler
  extends FullShoppingCartFormHandler
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/SoftGoodFormHandler.java#2 $$Change: 651448 $";

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------
  protected static final String MSG_UNABLE_TO_CREATE_SG = "msgUnableToCreateSg";
  protected static final String NO_SG_TO_INIT = "noSGToInit";
  protected static final String MSG_UNABLE_TO_INIT_SG = "msgUnableToInitSG";
  protected static final String MSG_NO_EMAIL_ADDR = "msgNoEmailAddr";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties

  /**
   * Sets property Configuration, and in the process configures the following properties:
   * <UL>
   * <LI>SoftGoodShippingGroupName
   * </UL>
   * Plus the properties in the super-class.
   * @beaninfo description: The configuration for the shopping cart form handler
   **/
  public void setConfiguration(ShoppingCartModifierConfiguration pConfiguration) {
    super.setConfiguration(pConfiguration);
    if (getConfiguration() != null) {
      setSoftGoodShippingGroupName(mConfiguration.getSoftGoodShippingGroupName());
    }
  }

  //---------------------------------------------------------------------------
  // property: softGoodShippingGroupName

  /**
   * The name of the shippingGroup that gets created by OrderManager.  Look
   * at the OrderManager for how the name to class gets mapped.
   */
  String mSoftGoodShippingGroupName;

  /**
   * Set the softGoodShippingGroupName property.
   */
  public void setSoftGoodShippingGroupName(String pSoftGoodShippingGroupName) {
    mSoftGoodShippingGroupName = pSoftGoodShippingGroupName;
  }

  /**
   * Return the softGoodShippingGroupName property.
   * @beaninfo description: The name of the shippingGroup that gets created by OrderManager
   */
  public String getSoftGoodShippingGroupName() {
    return mSoftGoodShippingGroupName;
  }


  //---------------------------------------------------------------------------
  // property: addSoftGoodToOrderErrorURL
  String mAddSoftGoodToOrderErrorURL;

  /**
   * Set the addSoftGoodToOrderErrorURL property.
   */
  public void setAddSoftGoodToOrderErrorURL(String pAddSoftGoodToOrderErrorURL) {
    mAddSoftGoodToOrderErrorURL = pAddSoftGoodToOrderErrorURL;
  }

  /**
   * Return the addSoftGoodToOrderErrorURL property.
   * @beaninfo description: The URL to go to when AddSoftGoodToOrder fails
   */
  public String getAddSoftGoodToOrderErrorURL() {
    return mAddSoftGoodToOrderErrorURL;
  }


  //---------------------------------------------------------------------------
  // property: addSoftGoodToOrderSuccessURL
  String mAddSoftGoodToOrderSuccessURL;

  /**
   * Set the addSoftGoodToOrderSuccessURL property.
   */
  public void setAddSoftGoodToOrderSuccessURL(String pAddSoftGoodToOrderSuccessURL) {
    mAddSoftGoodToOrderSuccessURL = pAddSoftGoodToOrderSuccessURL;
  }

  /**
   * Return the addSoftGoodToOrderSuccessURL property.
   * @beaninfo description: The URL to go to when AddSoftGoodToOrder is successful
   */
  public String getAddSoftGoodToOrderSuccessURL() {
    return mAddSoftGoodToOrderSuccessURL;
  }

  //--------------------------------------------------

  //---------------------------------------------------------------------------
  // property: SoftGoodRecipientEmailAddress

  /**
   * The email address that the goods associated with the shipping group will be
   * mailed to.
   */
  String mSoftGoodRecipientEmailAddress;

  /**
   * Set the SoftGoodRecipientEmailAddress property.
   */
  public void setSoftGoodRecipientEmailAddress(String pSoftGoodRecipientEmailAddress) {
    mSoftGoodRecipientEmailAddress = pSoftGoodRecipientEmailAddress;
  }

  /**
   * Return the SoftGoodRecipientEmailAddress property.
   * @beaninfo description: The URL email address the soft goods get mailed to
   */
  public String getSoftGoodRecipientEmailAddress() {
    return mSoftGoodRecipientEmailAddress;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Empty Constructor
   */
  public SoftGoodFormHandler() {
    super();
  }

  //--------------------------------------------------
  // Methods
  //--------------------------------------------------

  /**
   * Empty method that can be overriden to provide additional functionality
   * if desired.  Called before any processing is done by the
   * handleAddSoftGoodToOrder method.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preAddSoftGoodToOrder(DynamoHttpServletRequest pRequest,
            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * Empty method that can be overriden to provide additional functionality
   * if desired.  Called after all processing is done by the
   * handleAddSoftGoodToOrder method.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postAddSoftGoodToOrder(DynamoHttpServletRequest pRequest,
             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * Does all processing needed to add a soft good to a users order.
   * This means calling the addSoftGoodToOrder method, and surrounding
   * it with the proper pre/post methods.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @return true if the request was properly handled
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleAddSoftGoodToOrder(DynamoHttpServletRequest pRequest,
            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    Transaction tr = null;
    try {
      tr = ensureTransaction();

      //Check if any form errors exist.  If they do, redirect to Error URL:
      if (!checkFormRedirect(null, getAddSoftGoodToOrderErrorURL(),
                             pRequest, pResponse)) {
        return false;

      }

      synchronized(getOrder()) {
	// do any necessary preprocessing
	preAddSoftGoodToOrder(pRequest, pResponse);
	addSoftGoodToOrder(pRequest, pResponse);
	
	// do any necessary postprocessing
	postAddSoftGoodToOrder(pRequest, pResponse);
	
	try {
	  getOrderManager().updateOrder(getOrder());
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
      } // synchronized

      return checkFormRedirect(getAddSoftGoodToOrderSuccessURL(),
                               getAddSoftGoodToOrderErrorURL(),
                               pRequest, pResponse);
    }
    finally {
      if (tr != null) commitTransaction(tr);
    }
  }

  /**
   * To add a soft good to the users order, the OrderManager first needs
   * to create a shipping group named by the softGoodShippingGroupName
   * property.  This shipping group will then be initialized by the
   * initializeShippingGroup method.  Finally, set the property
   * shippingGroup to be the newly created shipping group and then
   * call addItemToOrder method.  Since the addItemToOrder method
   * creates relationships by default between the shippingGroup returned
   * by getShippingGroup and the item being added, this allows
   * the two to be associated.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void addSoftGoodToOrder(DynamoHttpServletRequest pRequest,
            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    ShippingGroup shippingGroup;

    try {
      shippingGroup = getExistingShippingGroup();
      if(shippingGroup == null) {
        shippingGroup = mOrderManager.getShippingGroupManager().createShippingGroup(mSoftGoodShippingGroupName);
        if (!initializeShippingGroup(shippingGroup, pRequest, pResponse)) {
          return;
        }
        mOrderManager.getShippingGroupManager().addShippingGroupToOrder(getOrder(), shippingGroup);
      }
      setShippingGroup(shippingGroup);
      addItemToOrder(pRequest, pResponse, false);
    } catch (CommerceException ce) {
      processException(ce, MSG_ERROR_ADDING_TO_ORDER, pRequest, pResponse);
    }
  }

  /**
   * This method initializes the shipping group that was created by the OrderManager
   * for us.  This means that it checks to make sure that it is not null and then
   * call the method initializeElectronicShippingGroup.
   *
   * @param pShippingGroup a value of type 'ShippingGroup'
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @return true if the shipping group was properly initialized
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected boolean initializeShippingGroup(ShippingGroup pShippingGroup,
              DynamoHttpServletRequest pRequest,
              DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    if (pShippingGroup == null) {
      if (isLoggingError()) {
  String msg = formatUserMessage(NO_SG_TO_INIT, pRequest, pResponse);
  logError(msg);
      }
      return false;
    }

    return initializeElectronicShippingGroup(pShippingGroup, pRequest, pResponse);
  }

  /**
   * This method will initialize an electronic shipping group.  By initialize we mean
   * that the email address specified by the softGoodRecipientEmailAddress property
   * will be placed into the emailAddress property of the electronicShippingGroup.
   *
   * @param pShippingGroup the shippingGroup that the OrderManager created for us
   * and is to be initialized
   * @param pRequest the request object
   * @param pResponse the response object
   * @return true if we were successfully able to initialized the shipping group
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected boolean initializeElectronicShippingGroup(ShippingGroup pShippingGroup,
                  DynamoHttpServletRequest pRequest,
                  DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    ElectronicShippingGroup electronicShippingGroup;

    if (mSoftGoodRecipientEmailAddress == null || mSoftGoodRecipientEmailAddress.equals("")) {
      String msg = formatUserMessage(MSG_NO_EMAIL_ADDR, pRequest, pResponse);
      addFormException(new DropletException(msg, MSG_NO_EMAIL_ADDR));
      return false;
    }

    try {
      electronicShippingGroup = (ElectronicShippingGroup)pShippingGroup;
      electronicShippingGroup.setEmailAddress(mSoftGoodRecipientEmailAddress);
    } catch (ClassCastException cce) {
      processException(cce, MSG_UNABLE_TO_INIT_SG, pRequest, pResponse);
      return false;
    }
    return true;
  }

  // if there already exists an electronic shipping group with the
  // right address, use it.  Assumes the class is ElectronicShippingGroup
  private ShippingGroup getExistingShippingGroup()
  {
    if(mSoftGoodRecipientEmailAddress == null)
      return null;

    Order order = getOrder();
    List shippingGroupList = order.getShippingGroups();
    if((shippingGroupList == null) ||
       (shippingGroupList.size() == 0))
      return null;

    Iterator sgs = shippingGroupList.iterator();
    while(sgs.hasNext()) {
      ShippingGroup sg = (ShippingGroup)sgs.next();
      if(sg instanceof ElectronicShippingGroup) {
        if(mSoftGoodRecipientEmailAddress.equals(((ElectronicShippingGroup)sg).getEmailAddress())) {
          return sg;
        }
      }
    }
    return null;
  }
}   // end of class
