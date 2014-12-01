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

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.PipelineConstants;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.StringUtils;
import atg.droplet.DropletFormException;
import atg.service.pipeline.PipelineResult;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.transaction.Transaction;

/**
 * The CommitOrderFormHandler is used to submit the Order for final confirmation.
 * This calls the OrderManager's processOrder method. If there are no errors with
 * processing the Order, then the current Order in the user's ShoppingCart will be
 * set to null and the submitted Order will become the ShoppingCart's last Order.
 *
 *
 * @beaninfo
 *   description: A formhandler which allows the user to commit an Order.
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CommitOrderFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.droplet.GenericFormHandler
 * @see atg.commerce.order.purchase.PurchaseProcessFormHandler
 */
public class CommitOrderFormHandler extends PurchaseProcessFormHandler
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CommitOrderFormHandler.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------
  
  public static final String MSG_COMMIT_ERROR = "errorCommittingOrder";
  public static final String MSG_ORDER_ALREADY_SUBMITTED = "orderAlreadySubmitted";
  public static final String MSG_ORDER_EMPTY = "orderEmpty";
  public static final String MSG_ORDER_NOT_CURRENT = "commitOrderNotCurrent";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //-------------------------------------
  // property: SalesChannel
  //-------------------------------------
  String mSalesChannel;
  
  /**
   * Returns the salesChannel.
   * 
   * Returns the sales channel which is used to submit the order. This could be "Web", "Call Center",
   * or "Scheduled Orders". These values are defined in the order repository.
   * 
   * @return salesChannel
   */
  public String getSalesChannel() {
    return mSalesChannel;
  }

  /**
   * Sets the salesChannel.
   * 
   * Sets the sales channel which is used to submit the order. This could be "Web", "Call Center",
   * or "Scheduled Orders". These values are defined in the order repository.
   * 
   * @param pSalesChannel
   */
  public void setSalesChannel(String pSalesChannel) {
    mSalesChannel = pSalesChannel;
  }

  //-------------------------------------
  // property: OrderId
  //-------------------------------------
  String mOrderId;

  /**
   * Set the OrderId property.
   * @param pOrderId a <code>String</code> value
   * @beaninfo description: The id of the order used during modifications.
   */
  public void setOrderId(String pOrderId) {
    mOrderId = pOrderId;
  }

  /**
   * Return the OrderId property.
   * @return a <code>String</code> value
   */
  public String getOrderId() {
    return mOrderId;
  }

  //-------------------------------------
  // property: SiteId
  //-------------------------------------
  String mSiteId;

  /**
   * Sets the site ID to be recorded in the order
   *
   * @param pSiteId a <code>String</code> value
   */
  public void setSiteId(String pSiteId) {
    mSiteId = pSiteId;
  }

  /**
   * Returns the site ID to be recorded in the order
   *
   * @return a <code>String</code> value
   */
  public String getSiteId() {
    return mSiteId;
  }

  //-------------------------------------
  // property: allowEmptyOrders
  //-------------------------------------
  // default to true for backward compatibility
  boolean mAllowEmptyOrders = true;

  /**
   * Set the allowEmptyOrders property.
   * @param pAllowEmptyOrders if <code>false</code>, <code>handleCommit</code>
   *            adds a form exception if called for an order with no items
   * @beaninfo description: True if committing an empty order is allowed
   */
  public void setAllowEmptyOrders(boolean pAllowEmptyOrders) {
    mAllowEmptyOrders = pAllowEmptyOrders;
  }

  /**
   * Return the allowEmptyOrders property.
   * @return a <code>boolean</code> value
   */
  public boolean getAllowEmptyOrders() {
    return mAllowEmptyOrders;
  }

  //-------------------------------------
  // property: CommitOrderSuccessURL
  //-------------------------------------
  String mCommitOrderSuccessURL;

  /**
   * Sets property CommitOrderSuccessURL
   *
   * @param pCommitOrderSuccessURL a <code>String</code> value
   * @beaninfo description: The URL to go to when CommitOrder is successful.
   */
  public void setCommitOrderSuccessURL(String pCommitOrderSuccessURL) {
    mCommitOrderSuccessURL = pCommitOrderSuccessURL;
  }

  /**
   * Returns property CommitOrderSuccessURL
   * @return a <code>String</code> value
   *
   */
  public String getCommitOrderSuccessURL() {
    return mCommitOrderSuccessURL;
  }

  //-------------------------------------
  // property: CommitOrderErrorURL
  //-------------------------------------
  String mCommitOrderErrorURL;

  /**
   * Sets property CommitOrderErrorURL
   *
   * @param pCommitOrderErrorURL a <code>String</code> value
   * @beaninfo description: The URL to go to when CommitOrder fails.
   */
  public void setCommitOrderErrorURL(String pCommitOrderErrorURL) {
    mCommitOrderErrorURL = pCommitOrderErrorURL;
  }

  /**
   * Returns property CommitOrderErrorURL
   * @return a <code>String</code> value
   *
   */
  public String getCommitOrderErrorURL() {
    return mCommitOrderErrorURL;
  }
  

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>CommitOrderFormHandler</code> instance.
   *
   */
  public CommitOrderFormHandler () {}
  
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  /**
   * Called before any processing is done by the handleCommitOrder method.
   * The default implementation does nothing.  This method is called from
   * within a synchronized block that synchronizes on the current order object.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preCommitOrder(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * Called after all processing is done by the handleCommitOrder method.
   * The default implementation does nothing.  This method is called from
   * within a synchronized block that synchronizes on the current order
   * object.
   * <p>
   * By the time this method is called the order has been committed to
   * the database by calling <tt>OrderManager.processOrder</tt>. If an
   * application modifies the order within its own <tt>postCommitOrder</tt>
   * method, that method is responsible for saving the new state of the
   * order by calling <tt>OrderManager.updateOrder</tt>.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postCommitOrder(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * This method is used to submit the current order.  It calls the <tt>preCommitOrder</tt>
   * method, allowing the application to do any required work before the order is
   * submitted.  Next it ensures that the user is not trying to double submit the
   * order by seeing if the shopping cart last order id is is equal to the current
   * order id.
   * <p>
   * Assuming the order is not a duplicate submission, the form handler calls
   * <tt>OrderManager.processOrder</tt> to actually place the order.  If placing
   * the order succeeds, then the form handler sets the shopping cart lastOrder
   * to the current (just submitted) order and the currentOrder to null.   Finally,
   * it calls the postCommitOrder method for any application-specific post-processing.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return a <code>boolean</code> value
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleCommitOrder(DynamoHttpServletRequest pRequest,
                                         DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CommitOrderFormHandler.handleCommit";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        
        if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));
        
        if (!checkFormRedirect(null, getCommitOrderErrorURL(), pRequest, pResponse))
          return false;
        
        Order order = getOrder();

        // Synchronize the entire preCommitOrder, commit, postCommitOrder block
        // to avoid the possibility of database deadlock if the pre- or post-
        // commit method also synchronizes on the order and does something that
        // requires a lock from the database.
        
        synchronized (order) {
      
          // make sure they are not trying to double submit an order, submit
          // from a page reached by the back button, or submit an empty order
          Order lastOrder = getShoppingCart().getLast();
          String orderId = getOrderId();
          if (orderId != null && lastOrder != null && orderId.equals(lastOrder.getId())) {
            // Trying to submit the previous order again
            String msg = formatUserMessage(MSG_ORDER_ALREADY_SUBMITTED, pRequest, pResponse);
            String propertyPath = generatePropertyPath("orderId");
            addFormException(new DropletFormException(msg, propertyPath ,MSG_ORDER_ALREADY_SUBMITTED));
          }
          else if (orderId != null && !orderId.equals(order.getId())) {
            // Trying to submit some order other than the current one,
            // probably because the user hit the browser's Back button
            String msg = formatUserMessage(MSG_ORDER_NOT_CURRENT, pRequest, pResponse);
            String propertyPath = generatePropertyPath("orderId");
            addFormException(new DropletFormException(msg, propertyPath, MSG_ORDER_NOT_CURRENT));
          }
          else if (!getAllowEmptyOrders() && getShoppingCart().isCurrentEmpty()) {
            // Trying to submit an empty order
            String msg = formatUserMessage(MSG_ORDER_EMPTY, pRequest, pResponse);
            String propertyPath = generatePropertyPath("allowEmptyOrders");
            addFormException(new DropletFormException(msg, propertyPath, MSG_ORDER_EMPTY));
          }
        
          if (!checkFormRedirect(null, getCommitOrderErrorURL(), pRequest, pResponse))
            return false;
          
          preCommitOrder(pRequest, pResponse);
        
          if (!checkFormRedirect(null, getCommitOrderErrorURL(), pRequest, pResponse))
            return false;

          commitOrder (order, pRequest, pResponse);
          
          if (!checkFormRedirect(null, getCommitOrderErrorURL(), pRequest, pResponse))
            return false;
        
          postCommitOrder(pRequest, pResponse);
          
        } // end of synchronization 

        // Redirect to success or error url based on whether form errors exist
        return checkFormRedirect (getCommitOrderSuccessURL(), getCommitOrderErrorURL(), pRequest, pResponse);
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
   * 
   * This method is called between <code>preCommitOrder</code> and <code>postCommitOrder</code>. This method
   * calls the <code>getProcessOrderMap(Locale)</code> to get the process order map and calls the Order Manager 
   * to process the order.
   * 
   * @param pOrder
   * @param pRequest
   * @param pResponse
   * @throws ServletException
   * @throws IOException
   */
  public void commitOrder(Order pOrder, 
                          DynamoHttpServletRequest pRequest,
                          DynamoHttpServletResponse pResponse)
  throws ServletException, IOException
  {
    try {
      PipelineResult result = getOrderManager().processOrder(pOrder, getProcessOrderMap(getUserLocale()));
      if (! processPipelineErrors(result) &&
          (! isTransactionMarkedAsRollBack())) {
        if (getShoppingCart() != null) {
          getShoppingCart().setLast(pOrder);
          getShoppingCart().setCurrent(null);
        }
      }
    }
    catch (Exception exc) {
      processException(exc, MSG_COMMIT_ERROR, pRequest, pResponse); 
    }
  }

  /**
   * 
   * This method calls the <code>OrderManager.getProcessOrderMap(pLocale, map)</> to get the process order map
   * and adds site ID and sales channel to the map if specified.
   *   
   * @return
   */
  public HashMap getProcessOrderMap (Locale pLocale) 
  throws CommerceException {
    HashMap pomap = getOrderManager().getProcessOrderMap(pLocale, null);
    if (!StringUtils.isEmpty(getSiteId())) {
      pomap.put(PipelineConstants.SITEID, getSiteId());
    }
    if (!StringUtils.isEmpty(getSalesChannel())) {
      pomap.put(PipelineConstants.SALESCHANNEL, getSalesChannel());
    }
    return pomap;
  }
  
}   // end of class
