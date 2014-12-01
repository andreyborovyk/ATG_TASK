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

package atg.projects.b2bstore.order;

import javax.servlet.*;
import javax.servlet.http.*;

import atg.commerce.CommerceException;
import atg.commerce.order.*;
import atg.commerce.states.*;
import atg.nucleus.naming.ParameterName;
import atg.nucleus.naming.ComponentName;
import atg.servlet.*;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import java.io.*;
import java.util.List;
import java.util.Locale;
import atg.droplet.*;

/**
 * This servlet cancels the order for the orderId paramteter passed in. 
 * It takes as parameters:
 * <dl>
 *
 * <dt>orderId  
 *
 * <dd>the id of the order to cancel
 *
 * </dl>
 * 
 * It renders the following oparams:
 * <dl>
 * 
 * <dt>success
 *
 * <dd>The oparam success is rendered once if the cancel was successful
 *
 * <dt>error
 *
 * <dd>error will be rendered if an error occurred
 *
 * </dl>
 * 
 * It sets the following output params:
 * <dl>
 *
 * <dt>errorMsg
 *
 * <dd>if an error occurred this will be the detailed error message
 *     for the user.
 *
 * </dl>
 *
 * This droplet has a security feature that allows only the current user to 
 * cancel his own orders.  This feature is enabled by default.  To disable
 * it, set the property enableSecurity=false
 *
 * @author Cynthia Harris
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/order/CancelOrder.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @deprecated Use the atg.commerce.order.purchase.CancelOrderFormHandler instead
 * 
 */
public class CancelOrder extends GenericFormHandler {
  //-------------------------------------
  // Class version string
  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/order/CancelOrder.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  public final static String SUCCESS = "success";


  //-------------------------------------
  // property: orderCanceller
  OrderCanceller mOrderCanceller;
  public void setOrderCanceller(OrderCanceller pOrderCanceller) {
    mOrderCanceller = pOrderCanceller;
  }
  public OrderCanceller getOrderCanceller() {
    return mOrderCanceller;
  }


  //---------- Property:orderManager----------
  private OrderManager mOrderManager;
  /**
   *  set the property<code>orderManager</code>
   *
   * @param pOrderManager a <code>String</code> value
   */
  public void setOrderManager(OrderManager pOrderManager){
    mOrderManager= pOrderManager;
  }
  /**
   * returns the property <code>orderManager</code>
   ** @return a <code>OrderManager</code> value
   */
  public OrderManager getOrderManager(){
    return mOrderManager;
  }
  
  //---------- Property:orderId----------
  private String mOrderId;
  /**
   *  set the property<code>orderId</code>
   *
   * @param pOrderId a <code>String</code> value
   */
  public void setOrderId(String pOrderId){
    mOrderId= pOrderId;
  }
  /**
   * returns the property <code>orderId</code>
   ** @return a <code>String</code> value
   */
  public String getOrderId(){
    return mOrderId;
  }

  //---------- Property:enableSecurity----------
  private boolean mEnableSecurity;
  /**
   *  set the property<code>enableSecurity</code>
   *
   * @param pEnableSecurity a <code>String</code> value
   */
  public void setEnableSecurity(boolean pEnableSecurity){
    mEnableSecurity= pEnableSecurity;
  }
  /**
   * returns the property <code>enableSecurity</code>
   ** @return a <code>boolean</code> value
   */
  public boolean isEnableSecurity(){
    return mEnableSecurity;
  }



  //---------- Property:profilePath----------
  private String mProfilePath;
  /**
   *  set the property<code>profilePath</code>
   *
   * @param pProfilePath a <code>String</code> value
   */
  public void setProfilePath(String pProfilePath){
    mProfilePath= pProfilePath;
  }
  /**
   * returns the property <code>profilePath</code>
   ** @return a <code>String</code> value
   */
  public String getProfilePath(){
    return mProfilePath;
  }




  
  //---------- Property:cancelOrderErrorURL----------
  private String mCancelOrderErrorURL;
  /**
   *  set the property<code>cancelOrderErrorURL</code>
   *
   * @param pCancelOrderErrorURL a <code>String</code> value
   */
  public void setCancelOrderErrorURL(String pCancelOrderErrorURL){
    mCancelOrderErrorURL= pCancelOrderErrorURL;
  }
  /**
   * returns the property <code>cancelOrderErrorURL</code>
   ** @return a <code>String</code> value
   */
  public String getCancelOrderErrorURL(){
    return mCancelOrderErrorURL;
  }



  
  //---------- Property:cancelOrderSuccessURL----------
  private String mCancelOrderSuccessURL;
  /**
   *  set the property<code>cancelOrderSuccessURL</code>
   *
   * @param pCancelOrderSuccessURL a <code>String</code> value
   */
  public void setCancelOrderSuccessURL(String pCancelOrderSuccessURL){
    mCancelOrderSuccessURL= pCancelOrderSuccessURL;
  }
  /**
   * returns the property <code>cancelOrderSuccessURL</code>
   ** @return a <code>String</code> value
   */
  public String getCancelOrderSuccessURL(){
    return mCancelOrderSuccessURL;
  }
  

  protected String getCurrentProfileId (DynamoHttpServletRequest pReq)
  {
    RepositoryItem profile = (RepositoryItem)pReq.resolveName(getProfilePath());
    if (profile != null)
      return profile.getRepositoryId();
    else 
      return "-1";
  }

  
  /**
   * <code>handleSubmit</code>
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  public boolean handleCancelOrder(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
    throws IOException, ServletException{

    String orderId = getOrderId();


    if (orderId==null){
      addFormException(new DropletException("no order specified, can not cancel the order"));
      return checkFormRedirect(getCancelOrderSuccessURL(),getCancelOrderErrorURL(),pRequest,pResponse);
    }
    
    if (isLoggingDebug())
      logDebug("orderId = " + orderId);

    try{
      // Make sure that an order for that orderId exists by asking the OrderManager
      // with the orderExists(orderId) method.  We then make double sure by doing 
      // orderManager.loadOrder(orderId) and making sure it is not null, but that is 
      // optional.  
      Order order = null;
      if (getOrderManager().orderExists(orderId))
        order = getOrderManager().loadOrder(orderId);
    
      if (isEnableSecurity()&&
          !order.getProfileId().equals(getCurrentProfileId(pRequest))){
        addFormException(new DropletException("you do not have persmission to cancel this order"));
        return checkFormRedirect(getCancelOrderSuccessURL(),getCancelOrderErrorURL(),pRequest,pResponse);
      }
      // Now we actually cancel the order 
      getOrderCanceller().sendCancelOrder(orderId);
    }
    catch (CommerceException e) {
      if (isLoggingError())
        logError(e);

      addFormException(new DropletException("could not cancel the order :"));
    }    
      
    return checkFormRedirect(getCancelOrderSuccessURL(),getCancelOrderErrorURL(),pRequest,pResponse);
  }

}
