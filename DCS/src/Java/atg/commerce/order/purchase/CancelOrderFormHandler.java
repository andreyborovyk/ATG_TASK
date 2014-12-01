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

import atg.commerce.order.*;
import atg.servlet.*;
import atg.commerce.CommerceException;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.commerce.messaging.*;
import atg.commerce.states.*;
import atg.commerce.fulfillment.*;
import atg.droplet.DropletFormException;
import atg.core.util.StringUtils;
import atg.repository.RepositoryItem;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.transaction.Transaction;

/**
 * The CancelOrderFormHandler is used to cancel the user's current Order,
 * which deletes the Order from the ShoppingCart.
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CancelOrderFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.droplet.GenericFormHandler
 * @see atg.commerce.order.purchase.PurchaseProcessFormHandler
 * @beaninfo
 *   description: A formhandler which allows the user to cancel an Order.
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 */
public class CancelOrderFormHandler extends PurchaseProcessFormHandler
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CancelOrderFormHandler.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  public static final String MSG_INVALID_ORDER_ID = "invalidOrderIdParameter";
  public static final String MSG_ERROR_LOADING_ORDER = "errorLoadingOrder";
  public static final String MSG_ERROR_SENDING_MESSAGE = "errorSendingMessage";
  public static final String MSG_ERROR_CANCELLING_ORDER= "errorCancellingOrder";
  public static final String MSG_ORDER_NOT_CURRENT = "cancelOrderNotCurrent";
  public static final String MSG_USER_NO_PERMISSION_TO_CANCEL = "userNoPermissionToCancel";
  public static final String MSG_USER_INFO_NOT_AVAILABLE = "userInfoNotAvailable";
  
  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //---------------------------------------------------------------------------
  // property: CancelOrderSuccessURL
  //---------------------------------------------------------------------------
  String mCancelOrderSuccessURL;

  /**
   * Set the CancelOrderSuccessURL property.
   * @param pCancelOrderSuccessURL a <code>String</code> value
   * @beaninfo description: Success URL for redirection.
   */
  public void setCancelOrderSuccessURL(String pCancelOrderSuccessURL) {
    mCancelOrderSuccessURL = pCancelOrderSuccessURL;
  }

  /**
   * Return the CancelOrderSuccessURL property.
   * @return a <code>String</code> value
   */
  public String getCancelOrderSuccessURL() {
    return mCancelOrderSuccessURL;
  }

  //---------------------------------------------------------------------------
  // property: CancelOrderErrorURL
  //---------------------------------------------------------------------------
  String mCancelOrderErrorURL;

  /**
   * Set the CancelOrderErrorURL property.
   * @param pCancelOrderErrorURL a <code>String</code> value
   * @beaninfo description: Error URL for redirection.
   */
  public void setCancelOrderErrorURL(String pCancelOrderErrorURL) {
    mCancelOrderErrorURL = pCancelOrderErrorURL;
  }

  /**
   * Return the CancelOrderErrorURL property.
   * @return a <code>String</code> value
   */
  public String getCancelOrderErrorURL() {
    return mCancelOrderErrorURL;
  }

  //---------------------------------------------------------------------------
  // property: OrderIdToCancel
  //---------------------------------------------------------------------------
  String mOrderIdToCancel;

  /**
   * Set the OrderIdToCancel property.
   * @param pOrderIdToCancel a <code>String</code> value
   * @beaninfo description: The ID of the Order to cancel.
   */
  public void setOrderIdToCancel(String pOrderIdToCancel) {
    mOrderIdToCancel = pOrderIdToCancel;
  }

  /**
   * Return the OrderIdToCancel property.
   * @return a <code>String</code> value
   */
  public String getOrderIdToCancel() {
    return mOrderIdToCancel;
  }

  //---------------------------------------------------------------------------
  // property: DeleteStates
  //---------------------------------------------------------------------------
  String [] mDeleteStates = { "INCOMPLETE" };

  /**
   * Set the DeleteStates property. This property is the list of states for which
   * Order cancellation entails explicit Order deletion. All other states will
   * invoke alternate behavior.
   * @param pDeleteStates a <code>String[]</code> value
   * @beaninfo description: A list of states for which those Orders should be deleted.
   */
  public void setDeleteStates(String [] pDeleteStates) {
    mDeleteStates = pDeleteStates;
  }

  /**
   * Return the DeleteStates property. This property is the list of states for which
   * Order cancellation entails explicit Order deletion. All other states will
   * invoke alternate behavior.
   * @return a <code>String[]</code> value
   */
  public String [] getDeleteStates() {
    return mDeleteStates;
  }

  //---------------------------------------------------------------------------
  // property: IgnoreStates
  //---------------------------------------------------------------------------
  String [] mIgnoreStates = { "PENDING_REMOVE" };

  /**
   * Set the IgnoreStates property. This property is the list of states for which
   * Order cancellation need not be performed. The default is the PENDING_REMOVE
   * state, which indicates the remove has been requested but hasn't finished
   * processing.
   * @param pIgnoreStates a <code>String[]</code> value
   * @beaninfo description: A list of states for which those Orders already pending deletion.
   */
  public void setIgnoreStates(String [] pIgnoreStates) {
    mIgnoreStates = pIgnoreStates;
  }

  /**
   * Return the IgnoreStates property. This property is the list of states for which
   * Order cancellation need not be performed. The default is the PENDING_REMOVE
   * state, which indicates the remove has been requested but hasn't finished
   * processing.
   * @return a <code>String[]</code> value
   */
  public String [] getIgnoreStates() {
    return mIgnoreStates;
  }

  //---------------------------------------------------------------------------
  // property: MessageSender
  //---------------------------------------------------------------------------
  MessageSender mMessageSender;

  /**
   * Set the MessageSender property.
   * @param pMessageSender a <code>MessageSender</code> value
   * @beaninfo description: The MessageSender used to notify Fulfillment of Order cancellations.
   */
  public void setMessageSender(MessageSender pMessageSender) {
    mMessageSender = pMessageSender;
  }

  /**
   * Return the MessageSender property.
   * @return a <code>MessageSender</code> value
   */
  public MessageSender getMessageSender() {
    return mMessageSender;
  }

  //---------------------------------------------------------------------------
  // property: CancelOrderService
  //---------------------------------------------------------------------------
  private CancelOrderService mCancelOrderService;

  /**
   * The Nucleus service used to cancel orders
   * @param pCancelOrderService a <code>CancelOrderService</code> value
   * @beaninfo description: The Nucleus service used to cancel orders
   **/
  public void setCancelOrderService(CancelOrderService pCancelOrderService) {
    mCancelOrderService = pCancelOrderService;
  }

  /**
   * The Nucleus service used to cancel orders
   * @beaninfo description: The Nucleus service used to cancel orders
   **/
  public CancelOrderService getCancelOrderService() {
    return mCancelOrderService;
  }
  
  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>CancelOrderFormHandler</code> instance.
   *
   */
  public CancelOrderFormHandler () {}
  
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  /**
   * <code>preCancelOrder</code> is used for work that must happen before the
   * Order is cancelled.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preCancelOrder(DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse)
    throws ServletException, 
           IOException
  {
  }

  /**
   * <code>postCancelOrder</code> is used for work that must happen after the
   * Order is cancelled.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postCancelOrder(DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse)
    throws ServletException, 
           IOException
  {
  }

  /**
   * The <code>handleCancelOrder</code> method cancels the Order specified
   * by the OrderIdToCancel property.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleCancelOrder(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CancelOrderFormHandler.handleCancelOrder";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
	tr = ensureTransaction();    
	
	if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

	//If any form errors found, redirect to error URL:
	
	if (StringUtils.isEmpty(getOrderIdToCancel())) {
	  String msg = formatUserMessage(MSG_INVALID_ORDER_ID, pRequest, pResponse);
    String propertyPath = generatePropertyPath("orderIdToCancel");
	  addFormException(new DropletFormException(msg, propertyPath ,MSG_INVALID_ORDER_ID));
	}

	if (! checkFormRedirect(null, getCancelOrderErrorURL(), pRequest, pResponse))
	  return false;
	
	preCancelOrder(pRequest, pResponse);

	if (! checkFormRedirect(null, getCancelOrderErrorURL(), pRequest, pResponse))
	  return false;

	try {
	  Order order = getOrderManager().loadOrder(getOrderIdToCancel());

    // Make sure that the current logged in user is the owner of this order
    if ( canUserCancelOrder(order, pRequest, pResponse)) {
      cancelOrder(order, pRequest, pResponse);
    } else {
      String msg = formatUserMessage(MSG_USER_NO_PERMISSION_TO_CANCEL, pRequest, pResponse);
      String propertyPath = generatePropertyPath("orderIdToCancel");
      addFormException(new DropletFormException(msg, propertyPath, MSG_USER_NO_PERMISSION_TO_CANCEL));
    } // end of else

	} catch (CommerceException exc) {
	  processException(exc, MSG_ERROR_LOADING_ORDER, pRequest, pResponse);
	}

	if (! checkFormRedirect(null, getCancelOrderErrorURL(), pRequest, pResponse))
	  return false;

	postCancelOrder(pRequest, pResponse);
	//If NO form errors are found, redirect to the success URL.
	//If form errors are found, redirect to the error URL.
	return checkFormRedirect (getCancelOrderSuccessURL(), 
				  getCancelOrderErrorURL(), 
				  pRequest, 
				  pResponse);
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
   * The <code>handleCancelCurrentOrder</code> method cancels the
   * ShoppingCart's current order. If the <code>OrderIdToCancel</code>
   * property is set, its value is compared to the current order
   * to make sure they match.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleCancelCurrentOrder(DynamoHttpServletRequest pRequest,
                                          DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CancelOrderFormHandler.handleCancelCurrentOrder";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
        tr = ensureTransaction();    
	
        if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

        Order order = getShoppingCart().getCurrent();
        String orderId = getOrderIdToCancel();

        if (!StringUtils.isEmpty(orderId) && !orderId.equals(order.getId())) {
          // Trying to cancel some order other than the current one,
          // probably because the user hit the browser's Back button
          String msg = formatUserMessage(MSG_ORDER_NOT_CURRENT, pRequest, pResponse);
          String propertyPath = generatePropertyPath("orderIdToCancel");
          addFormException(new DropletFormException(msg, propertyPath, MSG_ORDER_NOT_CURRENT));
        }

        if (! checkFormRedirect(null, getCancelOrderErrorURL(), pRequest, pResponse))
          return false;
	
        preCancelOrder(pRequest, pResponse);

        if (! checkFormRedirect(null, getCancelOrderErrorURL(), pRequest, pResponse))
          return false;

        // Do whatever makes sense based on the order's state
        cancelOrder(order, pRequest, pResponse);

        if (! checkFormRedirect(null, getCancelOrderErrorURL(), pRequest, pResponse))
          return false;

        postCancelOrder(pRequest, pResponse);

        //If NO form errors are found, redirect to the success URL.
        //If form errors are found, redirect to the error URL.
        return checkFormRedirect (getCancelOrderSuccessURL(), 
        			  getCancelOrderErrorURL(), 
        			  pRequest, 
        			  pResponse);
      }
      finally {
        if (tr != null) commitTransaction(tr);
        if (rrm != null) rrm.removeRequestEntry(myHandleMethod);
      }
    }
    else {
      return false;
    }
  }

  /**
   * This method calls the <code>CancelOrderService.cancelOrder</code> method.
   * All logic for what to do happens there.
   *
   * @param pOrder the <code>Order</code> to be canceled
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void cancelOrder(Order pOrder,
                          DynamoHttpServletRequest pRequest,
                          DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
    if (isLoggingDebug())
      logDebug("CancelOrderFormHandler.cancelOrder orderId " + pOrder.getId());

    try {
      getCancelOrderService().cancelOrder(pOrder.getId(), getIgnoreStates(), getDeleteStates());
    } catch(CommerceException ce) {
      processException(ce, MSG_ERROR_CANCELLING_ORDER, pRequest, pResponse);
    }

  }

  /**
   * <code>deleteOrder</code> is used to handle an Order whose state is one
   * of the configured DeleteStates of this component. This deletes the
   * specified order if that order is in the ShoppingCart.
   *
   * @param pOrderId the ID of the <code>Order</code> to be deleted
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   * @deprecated This method is not used anymore, See {@link atg.commerce.order.CancelOrderService}
   */
  public void deleteOrder(String pOrderId)
  {
    if (isLoggingDebug())
      logDebug("CancelOrderFormHandler.deleteOrder orderId " + pOrderId);
    getShoppingCart().deleteOrder(pOrderId);
  }

  /**
   * <code>deleteOrder</code> is used to handle an Order whose state is one
   * of the configured DeleteStates of this component. This deletes the
   * Order identified by the <code>orderIdToCancel</code> property if that
   * order is in the ShoppingCart.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   * @deprecated This method is not used anymore, See {@link atg.commerce.order.CancelOrderService}
   */
  public void deleteOrder(DynamoHttpServletRequest pRequest,
                          DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
    if (isLoggingDebug())
      logDebug("CancelOrderFormHandler.deleteOrder orderId " + getOrderIdToCancel());
    getShoppingCart().deleteOrder(getOrderIdToCancel());
  }
  
  /**
   * <code>preserveOrder</code>is used to handle an Order whose state is not
   * one of the configured DeleteStates of this component. This is used to
   * send a ModifyOrder message to Fulfillment with a GenericRemove Modification.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   * @deprecated This method is not used anymore, See {@link atg.commerce.order.CancelOrderService}
   */
  public void preserveOrder(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
    if (isLoggingDebug())
      logDebug("CancelOrderFormHandler.preserveOrder orderId " + getOrderIdToCancel());

    try {
      String orderId = getOrderIdToCancel();
      ModifyOrder message = new ModifyOrder();
      GenericRemove gr = new GenericRemove();
      Modification[] mods = { gr };

      gr.setTargetType(Modification.TARGET_ORDER);
      gr.setTargetId(orderId);
      message.setOrderId(orderId);
      message.setModifications(mods);

      MessageSender sender = getMessageSender();
      sender.sendCommerceMessage(message);

    } catch (Exception exc) {
      processException(exc, MSG_ERROR_SENDING_MESSAGE, pRequest, pResponse);
    }

  }

  /**
   * <code>isDeleteState</code> returns a boolean indicating whether the Order's
   * state is one of the DeleteStates.
   *
   * @param pState an <code>int</code> value
   * @return a <code>boolean</code> value
   * @deprecated This method is not used anymore, See {@link atg.commerce.order.CancelOrderService}
   */
  public boolean isDeleteState(int pState)
  {
    String stateString = StateDefinitions.ORDERSTATES.getStateString(pState);
    String [] deleteStates = getDeleteStates();
    if (deleteStates != null) {
      for (int i = 0; i < deleteStates.length; i++) {
        if (stateString.equals(deleteStates[i]))
          return true;
      }
    }
    return false;
  }

  /**
   * <code>isIgnoreState</code> returns a boolean indicating whether the Order's
   * state is one of the IgnoreStates.
   *
   * @param pState an <code>int</code> value
   * @return a <code>boolean</code> value
   * @deprecated This method is not used anymore, See {@link atg.commerce.order.CancelOrderService}
   */
  public boolean isIgnoreState(int pState)
  {
    String stateString = StateDefinitions.ORDERSTATES.getStateString(pState);
    String [] ignoreStates = getIgnoreStates();
    if (ignoreStates != null) {
      for (int i = 0; i < ignoreStates.length; i++) {
        if (stateString.equals(ignoreStates[i]))
          return true;
      }
    }
    return false;
  }

  /**
   * Checks whether the order is created by the user in the request.
   *
   * @param pOrder is the order to check
   * @param pRequest is the request created for user's session
   * @param pResponse is the response to the user'ns request.
   * @return <code>true</code> if order is created by the user, otherwise returns <code>false</code>
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected boolean canUserCancelOrder(Order pOrder,
                                       DynamoHttpServletRequest pRequest,
                                       DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    // get the user 
    RepositoryItem profile = null;
    if ( (profile = getProfile()) == null) {
      // user may not have been logged-in.
      String msg = formatUserMessage(MSG_USER_INFO_NOT_AVAILABLE, pRequest, pResponse);
      String propertyPath = generatePropertyPath("profile");
      addFormException(new DropletFormException(msg, propertyPath, MSG_USER_INFO_NOT_AVAILABLE));
      return false;
    } // end of if ()

    if (isLoggingDebug())
      logDebug("User's profileId[" + profile.getRepositoryId() +
               "] and  order profileId[" + pOrder.getProfileId() + "]");

    // check whether user is owner of the order.
    if ( profile.getRepositoryId().equals(pOrder.getProfileId())) {
      return true;      
    } // end of if ()
    
    return false;

  }
}   // end of class
