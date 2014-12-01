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

package atg.b2bcommerce.order.purchase;

import atg.b2bcommerce.order.*;
import atg.commerce.order.*;
import atg.commerce.order.purchase.PurchaseProcessFormHandler;
import atg.repository.*;
import atg.beans.*;
import atg.droplet.*;
import atg.nucleus.ServiceException;
import atg.servlet.*;
import atg.commerce.CommerceException;
import atg.commerce.pricing.*;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import java.util.*;
import javax.transaction.*;

/**
 * <p>The <code>CostCenterFormHandler</code> is used to associate CostCenters with the various
 * Order pieces. This component is used during the Order checkout process, and any
 * Order successfully processed by the CostCenterFormHandler is ready for the next
 * checkout phase, which may be confirmation. 
 *
 * <p>The CostCenterFormHandler is composed of the following containers:
 *
 * <br>CostCenterMapContainer - container for the user's potential CostCenters
 *
 * <br>CommerceIdentifierCostCenterContainer - container for the user's CommerceIdentifierCostCenter
 * associations for a particular Order's CommerceIdentifiers
 *
 * <p>There are 2 main handler methods in the CostCenterFormHandler, namely
 * handleSplitCostCenters and handleApplyCostCenters:
 *
 * <ol>
 * <li>handleSplitCostCenters splits extra CommerceIdentifierCostCenter objects by
 * quantity. In a form the user might request to split 50 of an original CommerceItem quantity
 * of 100 onto a separate CostCenter. This will create a separate CommerceIdentifierCostCenter
 * object, and adjust the quantity of both the original and the new CommerceIdentifierCostCenter
 * objects to add up to the original CommerceItem total quantity.
 *
 * <li>handleApplyCostCenters is used when the CommerceIdentifierCostCenter associations
 * created by the user are scrutinized and the appropriate business methods are called
 * in the CostCenterManager. Validation takes place, via a configurable Pipeline chain.
 * </ol>
 *
 * <p>In order to conveniently manipulate CostCenters and CommerceIdentifierCostCenter Lists,
 * you can set the <code>ListId</code> and <code>CostCenterId</code> properties. This will
 * automatically expose the corresponding CostCenter and List in the <code>CurrentCostCenter</code>
 * and <code>CurrentCostCenter</code> properties.
 *
 * @author Paul O'Brien
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/purchase/CostCenterFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.droplet.GenericFormHandler
 * @see atg.commerce.order.purchase.PurchaseProcessFormHandler
 * @beaninfo
 *   description: A formhandler for editing an order's CostCenters.
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 */
public class CostCenterFormHandler
  extends PurchaseProcessFormHandler
  implements CostCenterConstants
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/purchase/CostCenterFormHandler.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  private static final String USER_MESSAGES = "atg.b2bcommerce.order.UserMessages";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //---------------------------------------------------------------------------
  // property: ListId
  //---------------------------------------------------------------------------
  String mListId;

  /**
   * Set the ListId property.
   * @param pListId a <code>String</code> value
   * @beaninfo description: The id of the current CommerceIdentifierCostCenter List.
   */
  public void setListId(String pListId) {
    mListId = pListId;
    setCurrentList(getCommerceIdentifierCostCenterContainer().getCommerceIdentifierCostCenters(pListId));
  }

  /**
   * Return the ListId property.
   * @return a <code>String</code> value
   */
  public String getListId() {
    return mListId;
  }

  //---------------------------------------------------------------------------
  // property: CurrentList
  //---------------------------------------------------------------------------
  List mCurrentList = null;

  /**
   * Set the CurrentList property.
   * @param pCurrentList a <code>List</code> value
   * @beaninfo description: The current CommerceIdentifierCostCenter List.
   */
  public void setCurrentList(List pCurrentList) {
    mCurrentList = pCurrentList;
  }

  /**
   * Return the CurrentList property.
   * @return a <code>List</code> value
   */
  public List getCurrentList() {
    return mCurrentList;  
  }

  //---------------------------------------------------------------------------
  // property: CostCenterId
  //---------------------------------------------------------------------------
  String mCostCenterId;

  /**
   * Set the CostCenterId property.
   * @param pCostCenterId a <code>String</code> value
   * @beaninfo description: The id of the current CostCenter.
   */
  public void setCostCenterId(String pCostCenterId) {
    mCostCenterId = pCostCenterId;
    setCurrentCostCenter(getCostCenter(pCostCenterId));
  }

  /**
   * Return the CostCenterId property.
   * @return a <code>String</code> value
   */
  public String getCostCenterId() {
    return mCostCenterId;
  }

  //---------------------------------------------------------------------------
  // property: CurrentCostCenter
  //---------------------------------------------------------------------------
  CostCenter mCurrentCostCenter = null;

  /**
   * Set the CurrentCostCenter property.
   * @param pCurrentCostCenter a <code>CostCenter</code> value
   * @beaninfo description: The current CostCenter.
   */
  public void setCurrentCostCenter(CostCenter pCurrentCostCenter) {
    mCurrentCostCenter = pCurrentCostCenter;
  }

  /**
   * Return the CurrentCostCenter property.
   * @return a <code>CostCenter</code> value
   */
  public CostCenter getCurrentCostCenter() {
    return mCurrentCostCenter;
  }

  //---------------------------------------------------------------------------
  // property: CostCenterManager
  //---------------------------------------------------------------------------
  CostCenterManager mCostCenterManager;

  /**
   * Set the CostCenterManager property.
   * @param pCostCenterManager a <code>CostCenterManager</code> value
   * @beaninfo description: The CostCenterManager.
   */
  public void setCostCenterManager(CostCenterManager pCostCenterManager) {
    mCostCenterManager = pCostCenterManager;
  }

  /**
   * Return the CostCenterManager property.
   * @return a <code>CostCenterManager</code> value
   */
  public CostCenterManager getCostCenterManager() {
    return mCostCenterManager;
  }
  
  //---------------------------------------------------------------------------
  // property: ApplyCostCentersSuccessURL
  //---------------------------------------------------------------------------
  String mApplyCostCentersSuccessURL;

  /**
   * Set the ApplyCostCentersSuccessURL property.
   * @param pApplyCostCentersSuccessURL a <code>String</code> value
   * @beaninfo description: URL for redirection.
   */
  public void setApplyCostCentersSuccessURL(String pApplyCostCentersSuccessURL) {
    mApplyCostCentersSuccessURL = pApplyCostCentersSuccessURL;
  }

  /**
   * Return the ApplyCostCentersSuccessURL property.
   * @return a <code>String</code> value
   */
  public String getApplyCostCentersSuccessURL() {
    return mApplyCostCentersSuccessURL;
  }

  //---------------------------------------------------------------------------
  // property: ApplyCostCentersErrorURL
  //---------------------------------------------------------------------------
  String mApplyCostCentersErrorURL;

  /**
   * Set the ApplyCostCentersErrorURL property.
   * @param pApplyCostCentersErrorURL a <code>String</code> value
   * @beaninfo description: URL for redirection.
   */
  public void setApplyCostCentersErrorURL(String pApplyCostCentersErrorURL) {
    mApplyCostCentersErrorURL = pApplyCostCentersErrorURL;
  }

  /**
   * Return the ApplyCostCentersErrorURL property.
   * @return a <code>String</code> value
   */
  public String getApplyCostCentersErrorURL() {
    return mApplyCostCentersErrorURL;
  }

  //---------------------------------------------------------------------------
  // property: SplitCostCentersSuccessURL
  //---------------------------------------------------------------------------
  String mSplitCostCentersSuccessURL;

  /**
   * Set the SplitCostCentersSuccessURL property.
   * @param pSplitCostCentersSuccessURL a <code>String</code> value
   * @beaninfo description: URL for redirection.
   */
  public void setSplitCostCentersSuccessURL(String pSplitCostCentersSuccessURL) {
    mSplitCostCentersSuccessURL = pSplitCostCentersSuccessURL;
  }

  /**
   * Return the SplitCostCentersSuccessURL property.
   * @return a <code>String</code> value
   */
  public String getSplitCostCentersSuccessURL() {
    return mSplitCostCentersSuccessURL;
  }

  //---------------------------------------------------------------------------
  // property: SplitCostCentersErrorURL
  //---------------------------------------------------------------------------
  String mSplitCostCentersErrorURL;

  /**
   * Set the SplitCostCentersErrorURL property.
   * @param pSplitCostCentersErrorURL a <code>String</code> value
   * @beaninfo description: URL for redirection.
   */
  public void setSplitCostCentersErrorURL(String pSplitCostCentersErrorURL) {
    mSplitCostCentersErrorURL = pSplitCostCentersErrorURL;
  }

  /**
   * Return the SplitCostCentersErrorURL property.
   * @return a <code>String</code> value
   */
  public String getSplitCostCentersErrorURL() {
    return mSplitCostCentersErrorURL;
  }

  //---------------------------------------------------------------------------
  // property: CommerceIdentifierCostCenterContainer
  //---------------------------------------------------------------------------
  CommerceIdentifierCostCenterContainer mCommerceIdentifierCostCenterContainer;

  /**
   * Set the CommerceIdentifierCostCenterContainer property.
   * @param pCommerceIdentifierCostCenterContainer a <code>CommerceIdentifierCostCenterContainer</code> value
   * @beaninfo description: Container for CommerceIdentifierCostCenter objects.
   */
  public void setCommerceIdentifierCostCenterContainer(CommerceIdentifierCostCenterContainer pCommerceIdentifierCostCenterContainer) {
    mCommerceIdentifierCostCenterContainer = pCommerceIdentifierCostCenterContainer;
  }

  /**
   * Return the CommerceIdentifierCostCenterContainer property.
   * @return a <code>CommerceIdentifierCostCenterContainer</code> value
   */
  public CommerceIdentifierCostCenterContainer getCommerceIdentifierCostCenterContainer() {
    return mCommerceIdentifierCostCenterContainer;
  }

  //---------------------------------------------------------------------------
  // property: CostCenterMapContainer
  //---------------------------------------------------------------------------
  CostCenterMapContainer mCostCenterMapContainer;

  /**
   * Set the CostCenterMapContainer property.
   * @param pCostCenterMapContainer a <code>CostCenterMapContainer</code> value
   * @beaninfo description: Container for CostCenters.
   */
  public void setCostCenterMapContainer(CostCenterMapContainer pCostCenterMapContainer) {
    mCostCenterMapContainer = pCostCenterMapContainer;
  }

  /**
   * Return the CostCenterMapContainer property.
   * @return a <code>CostCenterMapContainer</code> value
   */
  public CostCenterMapContainer getCostCenterMapContainer() {
    return mCostCenterMapContainer;
  }

  //---------------------------------------------------------------------------
  // property: ValidateCostCentersChainId
  //---------------------------------------------------------------------------
  String mValidateCostCentersChainId;

  /**
   * Set the ValidateCostCentersChainId property.
   * @param pValidateCostCentersChainId a <code>String</code> value
   * @beaninfo description: Chain to execute in validating CostCenters.
   */
  public void setValidateCostCentersChainId(String pValidateCostCentersChainId) {
    mValidateCostCentersChainId = pValidateCostCentersChainId;
  }

  /**
   * Return the ValidateCostCentersChainId property.
   * @return a <code>String</code> value
   */
  public String getValidateCostCentersChainId() {
    return mValidateCostCentersChainId;
  }

  //---------------------------------------------------------------------------
  // property: getOrderFromId
  //---------------------------------------------------------------------------
  boolean mGetOrderFromId = false;

  /**
   * Set the GetOrderFromId property.
   * @param pGetOrderFromId a <code>boolean</code> value
   * @beaninfo description: boolean describing how to find the order to operate on.
   */
  public void setGetOrderFromId(boolean pGetOrderFromId) {
    mGetOrderFromId = pGetOrderFromId;
  }

  /**
   * Return the GetOrderFromId property.
   * @return a <code>boolean</code> value
   */
  public boolean isGetOrderFromId() {
    return mGetOrderFromId;
  }

  //---------------------------------------------------------------------------
  // property: OrderId
  //---------------------------------------------------------------------------
  String mOrderId;

  /**
   * Set the OrderId property.
   * @param pOrderId a <code>String</code> value
   * @beaninfo description: The Order Id.
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

  //---------------------------------------------------------------------------
  // property: Order
  //---------------------------------------------------------------------------
  Order mOrder;

  /**
   * Set the Order property.
   * @param pOrder an <code>Order</code> value
   */
  public void setOrder(Order pOrder) {
    mOrder = pOrder;
  }

  /**
   * Overriding getOrder to use the getOrderFromId property
   * Return the Order property.
   * @return an <code>Order</code> value
   */
  public Order getOrder() {
    if (mOrder != null) {
      return mOrder;
    }
    else if (isGetOrderFromId()) {
      try {
	setOrder(getOrderManager().loadOrder(getOrderId()));
	return mOrder;
      }
      catch (CommerceException e) {
	logError(e);
	return null;
      }
    }
    else {
      return getShoppingCart().getCurrent();
    }
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>CostCenterFormHandler</code> instance.
   *
   */
  public CostCenterFormHandler() {}

  //-------------------------------------
  // splitCostCenters code
  //-------------------------------------

  /**
   * <code>handleSplitCostCenters</code> is used when the user is ready to
   * split a particular CommerceIdentifierCostCenter by quantity.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleSplitCostCenters(DynamoHttpServletRequest pRequest,
					DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CostCenterFormHandler.handleSplitCostCenters";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod))) {
      Transaction tr = null;
      try {
        tr = ensureTransaction();    

        if (getDefaultLocale() == null) setDefaultLocale(getUserLocale(pRequest, pResponse));

        if (!checkFormRedirect(null, getSplitCostCentersErrorURL(), pRequest, pResponse))
          return false;

        preSplitCostCenters(pRequest, pResponse);

        if (!checkFormRedirect(null, getSplitCostCentersErrorURL(), pRequest, pResponse))
          return false;
      
        splitCostCenters(pRequest, pResponse);

        if (!checkFormRedirect(null, getSplitCostCentersErrorURL(), pRequest, pResponse))
          return false;
      
        postSplitCostCenters(pRequest, pResponse);

        return checkFormRedirect (getSplitCostCentersSuccessURL(), 
                                  getSplitCostCentersErrorURL(), pRequest, pResponse);
      }
      finally {
        if (tr != null) commitTransaction(tr);
      }
    }
    else {
      return false;
    }
  }
  
  /**
   * <code>preSplitCostCenters</code> is for work that must happen before
   * the CommerceIdentifierCostCenters are split.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean preSplitCostCenters(DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    return true;
  }

  /**
   * <code>postSplitCostCenters</code> is for work that must happen after
   * the CommerceIdentifierCostCenters are split.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean postSplitCostCenters(DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    return true;
  }

  /**
   * <code>splitCostCenters</code> invokes the <code>splitCommerceIdentifierCostCenter</code>
   * method on each CommerceIdentifierCostCenter.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void splitCostCenters(DynamoHttpServletRequest pRequest,
				  DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    CommerceIdentifierCostCenterContainer container = getCommerceIdentifierCostCenterContainer();
    List commerceIdentifierCostCenters = container.getAllCommerceIdentifierCostCenters();
    Iterator iter;
    CommerceIdentifierCostCenter cicc;

    // split commerceIdentifierCostCenters
    iter = commerceIdentifierCostCenters.listIterator();
    while (iter.hasNext()) {
      cicc = (CommerceIdentifierCostCenter) iter.next();
      splitCommerceIdentifierCostCenter (cicc);
    }
  }

  /**
   * <code>splitCommerceIdentifierCostCenter</code> determines which CommerceIdentifierCostCenter
   * objects need to be split, and in what manner. This relies on the
   * CommerceIdentifierCostCenter.SplitQuantity property.
   *
   * @param pCommerceIdentifierCostCenter a <code>CommerceIdentifierCostCenter</code> value
   */
  protected void splitCommerceIdentifierCostCenter(CommerceIdentifierCostCenter pCommerceIdentifierCostCenter) {
    long splitQuantity = pCommerceIdentifierCostCenter.getSplitQuantity();
    long quantity = pCommerceIdentifierCostCenter.getQuantity();

    if (splitQuantity > 0) {
      if (splitQuantity > quantity) {
        ResourceBundle userResources = ResourceBundle.getBundle(USER_MESSAGES, getDefaultLocale());
        String msg = ResourceUtils.getUserMsgResource(MSG_INVALID_SPLIT_QUANTITY, USER_MESSAGES, userResources);
        addFormException(new DropletFormException(msg, MSG_INVALID_SPLIT_QUANTITY));
      } else {
        splitCommerceIdentifierCostCenterByQuantity (pCommerceIdentifierCostCenter, splitQuantity);
      }
    }
  }

  /**
   * <code>splitCommerceIdentifierCostCenterByQuantity</code> splits a CommerceIdentifierCenter
   * by quantity.
   *
   * @param pCommerceIdentifierCostCenter a <code>CommerceIdentifierCostCenter</code> value
   * @param pSplitQuantity a <code>long</code> value
   */
  protected void splitCommerceIdentifierCostCenterByQuantity (CommerceIdentifierCostCenter pCommerceIdentifierCostCenter,
                                                              long pSplitQuantity) {

    // get two object references ready
    CommerceIdentifierCostCenter currentObj = pCommerceIdentifierCostCenter;
    CommerceIdentifierCostCenter newObj = new CommerceIdentifierCostCenter();

    // long based arithmetic for quantity is simple
    long newCurrentQuantity = currentObj.getQuantity() - pSplitQuantity;

    // adjust new CommerceIdentifierCostCenter based on the split properties
    newObj.setCommerceIdentifier(currentObj.getCommerceIdentifier());
    newObj.setRelationshipType(B2BRelationshipTypes.CCQUANTITY_STR);
    newObj.setCostCenterName(currentObj.getSplitCostCenterName());
    newObj.setQuantity(pSplitQuantity);

    // adjust properties on current CommerceIdentifierCostCenter
    currentObj.setSplitCostCenterName(null);
    currentObj.setQuantity(newCurrentQuantity);
    currentObj.setSplitQuantity(0);
    
    // now add new CommerceIdentifierCostCenter to container
    String id = currentObj.getCommerceIdentifier().getId();
    CommerceIdentifierCostCenterContainer container = getCommerceIdentifierCostCenterContainer();
    container.addCommerceIdentifierCostCenter(id, newObj);
  }

  //--------------------------------------------------
  // ApplyCostCenters Code
  //--------------------------------------------------

  /**
   * <code>handleApplyCostCenters</code> is used when the user has supplied the
   * cost center information for this order, and is ready to proceed with the next checkout phase.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   *
   */
  public boolean handleApplyCostCenters(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CostCenterFormHandler.handleApplyCostCenters";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod))) {
      Transaction tr = null;
      try {
        tr = ensureTransaction();

        if (getDefaultLocale() == null) setDefaultLocale(getUserLocale(pRequest, pResponse));

        if (!checkFormRedirect(null, getApplyCostCentersErrorURL(), pRequest, pResponse))
          return false;

        preApplyCostCenters(pRequest, pResponse);

        if (!checkFormRedirect(null, getApplyCostCentersErrorURL(), pRequest, pResponse))
          return false;
      
        applyCostCenters(pRequest, pResponse);

        if (!checkFormRedirect(null, getApplyCostCentersErrorURL(), pRequest, pResponse))
          return false;
      
        postApplyCostCenters(pRequest, pResponse);

        try {
          //Run the appropriate Pipeline Chain:
          runProcessValidateCostCenters((B2BOrder) getOrder(), getUserPricingModels(), getDefaultLocale(), getProfile(),null);

          getOrderManager().updateOrder((B2BOrder) getOrder());
        } catch (Exception exc) {
          if (isLoggingError()) {
            logError(exc);
          }
        }
        return checkFormRedirect (getApplyCostCentersSuccessURL(), 
                                  getApplyCostCentersErrorURL(), pRequest, pResponse);
      }
      finally {
        if (tr != null) commitTransaction(tr);
      }
    }
    else {
      return false;
    }
  }
  
  /**
   * <code>preApplyCostCenters</code> is for work that must happen before
   * the CostCenters are applied.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean preApplyCostCenters(DynamoHttpServletRequest pRequest,
				     DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    return true;
  }

  /**
   * <code>postApplyCostCenters</code> is for work that must happen after
   * the CostCenters are applied.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean postApplyCostCenters(DynamoHttpServletRequest pRequest,
				      DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    return true;
  }

  /**
   * <code>applyCostCenters</code> iterates over the supplied CommerceIdentifierCostCenters
   * for each of the CommerceIdentifiers. Each CommerceIdentifierCostCenter is then used
   * to apply the Order.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void applyCostCenters(DynamoHttpServletRequest pRequest,
				DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    CostCenterManager ccm = getCostCenterManager();
    OrderManager om = getOrderManager();
    B2BOrder order = (B2BOrder) getOrder();
    CommerceIdentifierCostCenterContainer container = getCommerceIdentifierCostCenterContainer();
    List commerceIdentifierCostCenters = container.getAllCommerceIdentifierCostCenters();
    Iterator iter;
    CommerceIdentifierCostCenter cicc;

    try {
      ccm.removeAllCostCentersFromOrder(order);

      iter = commerceIdentifierCostCenters.listIterator();
      while (iter.hasNext()) {
        cicc = (CommerceIdentifierCostCenter) iter.next();
        applyCommerceIdentifierCostCenter(cicc, order);
      }
      //ccm.removeEmptyCostCenters(order);
    } catch (CommerceException exc) {
      if (isLoggingError()) {
        logError(exc);
      }
    }
  }

  /**
   * The <code>addAmountToCostCenter</code> method switches on the RelationshipType
   * of the CommerceIdentifierCostCenter to invoke the appropriate CostCenterManager
   * function.
   *
   * @param type an <code>int</code> value
   * @param ccm a <code>CostCenterManager</code> value
   * @param order a <code>B2BOrder</code> value
   * @param itemId a <code>String</code> value
   * @param ccId a <code>String</code> value
   * @param quantity a <code>long</code> value
   * @param amount a <code>double</code> value
   * @exception CommerceException if an error occurs
   */
  protected void addAmountToCostCenter(int type, CostCenterManager ccm, B2BOrder order,
				       String itemId, String ccId, long quantity, double amount)
    throws CommerceException
    {

      switch (type) {
      case B2BRelationshipTypes.CCQUANTITY:
	ccm.addItemQuantityToCostCenter(order, itemId, ccId, quantity);
        break;
      case B2BRelationshipTypes.CCQUANTITYREMAINING:
        ccm.addRemainingItemQuantityToCostCenter(order, itemId, ccId);
        break;  
      case B2BRelationshipTypes.CCAMOUNT:
        ccm.addItemAmountToCostCenter(order, itemId, ccId, amount);
        break;
      case B2BRelationshipTypes.CCAMOUNTREMAINING:
        ccm.addRemainingItemAmountToCostCenter(order, itemId, ccId);
        break;
      case B2BRelationshipTypes.CCSHIPPINGAMOUNT:
        ccm.addShippingCostAmountToCostCenter(order, itemId, ccId, amount);
        break;
      case B2BRelationshipTypes.CCSHIPPINGAMOUNTREMAINING:
        ccm.addRemainingShippingCostToCostCenter(order, itemId, ccId);
        break;
      case B2BRelationshipTypes.CCORDERAMOUNT:
        ccm.addOrderAmountToCostCenter(order, ccId, amount);
        break;
      case B2BRelationshipTypes.CCORDERAMOUNTREMAINING:
        ccm.addRemainingOrderAmountToCostCenter(order, ccId);
        break;
      case B2BRelationshipTypes.CCTAXAMOUNT:
        ccm.addTaxAmountToCostCenter(order, ccId, amount);
        break;
      case B2BRelationshipTypes.CCTAXAMOUNTREMAINING:
        ccm.addRemainingTaxAmountToCostCenter(order, ccId);
        break;
      default:
        // throw some exception; log some error
      }
    }

  /**
   * <code>applyCommerceIdentifierCostCenter</code> will take the supplied cost center information and
   * update the Order. This invokes business logic in the OrderManager that corresponds to the
   * RelationshipType set in the CommerceIdentifierCostCenter.
   *
   * @param pCommerceIdentifierCostCenter a <code>CommerceIdentifierCostCenter</code> value
   * @param order an <code>B2BOrder</code> value
   */
  protected void applyCommerceIdentifierCostCenter (CommerceIdentifierCostCenter pCommerceIdentifierCostCenter,
						     B2BOrder order)
  {
    if (isLoggingDebug()) {
      logDebug("CostCenterFormHandler applyCommerceIdentifierCostCenter " + pCommerceIdentifierCostCenter);
    }
    
    String costCenterName = pCommerceIdentifierCostCenter.getCostCenterName();
    CostCenter costCenter = getCostCenter (costCenterName);

    if (costCenter == null) {
      // not assigning any CostCenter to this CommerceIdentifier.
      return;
    }

    CostCenterManager ccm = getCostCenterManager();
    String itemId = pCommerceIdentifierCostCenter.getCommerceIdentifier().getId();
    String ccId = costCenter.getId();
    double amount = pCommerceIdentifierCostCenter.getAmount();
    long quantity = pCommerceIdentifierCostCenter.getQuantity();
    int type = B2BRelationshipTypes.stringToType (pCommerceIdentifierCostCenter.getRelationshipType());

    try {
      if (! isCostCenterInOrder (order, ccId)) {
	ccm.addCostCenterToOrder(order, costCenter);
      }

      addAmountToCostCenter(type, ccm, order, itemId, ccId, quantity, amount);
      
    } catch (CommerceException exc) {
      if (isLoggingError()) {
        logError(exc);
      }
    }
  }  

  /**
   * <code>applyDefaultCostCenter</code> will check to see if there is a
   * defaultCostCenter. If so, this CostCenter is added to the Order's
   * remaining amount.
   *
   * This can facilitate simpler applications that only permit one
   * CostCenter per Order, as well as advanced applications that apply
   * a default CostCenter to any remaining Order amount not explicitly
   * covered by other CostCenters.
   *
   * @param order an <code>Order</code> value
   */
  protected void applyDefaultCostCenter (B2BOrder order)
  {
    String name = getCostCenterMapContainer().getDefaultCostCenterName();
    CostCenter costCenter = getCostCenter(name);

    if (costCenter == null) {
      // not assigning any default CostCenter to the Order.
      return;
    }

    if (isLoggingDebug()) {
      logDebug("CostCenterFormHandler applying DefaultCostCenter" + costCenter);
    }
    
    CostCenterManager ccm = getCostCenterManager();
    String ccId = costCenter.getId();
    
    try {
      if (! isCostCenterInOrder (order, ccId)) {
        ccm.addCostCenterToOrder(order, costCenter);
      }
      ccm.addRemainingOrderAmountToCostCenter(order, ccId);
    } catch (CommerceException exc) {
      if (isLoggingError()) {
        logError(exc);
      }
    }
  }  

  /**
   * <code>isCostCenterInOrder</code> is used to determine if the CostCenter
   * is already in the Order.
   *
   * @param pOrder an <code>B2BOrder</code> value
   * @param pCostCenterId a <code>String</code> value
   * @return a <code>boolean</code> value
   */
  protected boolean isCostCenterInOrder (B2BOrder pOrder, String pCostCenterId) {
    boolean status = false;
    try {
      pOrder.getCostCenter(pCostCenterId);
      status = true;
    } catch (CostCenterNotFoundException pgnfe) {
      // CostCenter hasn't been added yet, ignore Exception
    } catch (InvalidParameterException ipe) {
      if (isLoggingError()) {
        logError(ipe);
      }
    }
    return status;
  }

  /**
   * <code>getCostCenter</code> gets the CostCenter with the given name
   * from the CostCenterMapContainer.
   *
   * @param pCostCenterName a <code>String</code> value
   * @return a <code>CostCenter</code> value
   */
  protected CostCenter getCostCenter(String pCostCenterName) {
    if (pCostCenterName == null)
      return null;

    CostCenter costCenter = null;
    CostCenterMapContainer container = getCostCenterMapContainer();
    costCenter = (CostCenter) container.getCostCenter(pCostCenterName);

    return costCenter;
  }

  //-------------------------------------
  // validation of CostCenters
  //-----------------------------------------

  /**
   * <code>runProcessValidateCostCenters</code> runs a configurable Pipeline chain
   * to validate CostCenters or prepare for the next checkout phase.   
   *
   * @param pOrder an <code>B2BOrder</code> value
   * @param pPricingModels a <code>PricingModelHolder</code> value
   * @param pLocale a <code>Locale</code> value
   * @param pProfile a <code>RepositoryItem</code> value
   * @param pExtraParameters a <code>Map</code> value
   * @exception RunProcessException if an error occurs
   */
  protected void runProcessValidateCostCenters(B2BOrder pOrder, PricingModelHolder pPricingModels,
					       Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
    throws RunProcessException
  {
    PipelineResult result = runProcess(getValidateCostCentersChainId(), pOrder, pPricingModels, pLocale,
                                       pProfile, pExtraParameters);
    processPipelineErrors(result);
  }
}

