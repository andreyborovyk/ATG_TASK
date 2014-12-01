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
import atg.droplet.DropletFormException;
import atg.commerce.CommerceException;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.StringUtils;
import atg.service.pipeline.*;
import java.io.IOException;
import javax.servlet.ServletException;
import java.util.*;
import javax.transaction.Transaction;
import java.text.DateFormat;

/**
 * The SaveOrderFormHandler is used to save the user's current Order based
 * on a descriptive name that the user specifies. A new empty Order is then
 * made the user's current shopping cart. If a descriptive name for the Order
 * is not specified, then one is created based on the user's Locale and
 * date and time.
 *
 * @beaninfo
 *   description: A formhandler which allows the user to save an Order.
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/SaveOrderFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.droplet.GenericFormHandler
 * @see atg.commerce.order.purchase.PurchaseProcessFormHandler
 */

public class SaveOrderFormHandler extends PurchaseProcessFormHandler
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/SaveOrderFormHandler.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  public static final String MSG_DUPLICATE_ORDER_DESCRIPTION = "duplicateOrderDescription";
  
  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //---------------------------------------------------------------------------
  // property: Description

  String mDescription;
  /**
   * Return the Description property.
   * @return a <code>String</code> value
   * @beaninfo description: The Description property of the order set during hanldeSaveOrder.
   */
  public String getDescription() {
    return mDescription;
  }
  /**
   * Set the Description property.
   * @param pDescription a <code>String</code> value
   */
  public void setDescription(String pDescription) {
    mDescription = pDescription;
  }

  //---------------------------------------------------------------------------
  // property: SaveOrderSuccessURL
  //---------------------------------------------------------------------------
  String mSaveOrderSuccessURL;

  /**
   * Set the SaveOrderSuccessURL property.
   * @param pSaveOrderSuccessURL a <code>String</code> value
   * @beaninfo description: Success URL for redirection.
   */
  public void setSaveOrderSuccessURL(String pSaveOrderSuccessURL) {
    mSaveOrderSuccessURL = pSaveOrderSuccessURL;
  }

  /**
   * Return the SaveOrderSuccessURL property.
   * @return a <code>String</code> value
   */
  public String getSaveOrderSuccessURL() {
    return mSaveOrderSuccessURL;
  }

  //---------------------------------------------------------------------------
  // property: SaveOrderErrorURL
  //---------------------------------------------------------------------------
  String mSaveOrderErrorURL;

  /**
   * Set the SaveOrderErrorURL property.
   * @param pSaveOrderErrorURL a <code>String</code> value
   * @beaninfo description: Failure URL for redirection.
   */
  public void setSaveOrderErrorURL(String pSaveOrderErrorURL) {
    mSaveOrderErrorURL = pSaveOrderErrorURL;
  }

  /**
   * Return the SaveOrderErrorURL property.
   * @return a <code>String</code> value
   */
  public String getSaveOrderErrorURL() {
    return mSaveOrderErrorURL;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>SaveOrderFormHandler</code> instance.
   *
   */
  public SaveOrderFormHandler () {}
  
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  /**
   * Empty method that can be overriden to provide additional functionality
   * if desired.  Called before any processing is done by the
   * handleSaveOrder method.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preSaveOrder(DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * Empty method that can be overriden to provide additional functionality
   * if desired.  Called after all processing is done by the
   * handleSaveOrder method.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postSaveOrder(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * This method is used to save the user's order based on the provided String
   * description, or absent this description based on the user's Locale representing
   * the date and time.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if the request was properly handled
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleSaveOrder(DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "SaveOrderOrderFormHandler.handleSaveOrder";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
	tr = ensureTransaction();
	
	if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));
	
	if (!checkFormRedirect(null, getSaveOrderErrorURL(), pRequest, pResponse))
	  return false;
	
	preSaveOrder(pRequest, pResponse);
	
	if (!checkFormRedirect(null, getSaveOrderErrorURL(), pRequest, pResponse))
	  return false;
	
	saveOrder(pRequest, pResponse);
	
	if (!checkFormRedirect(null, getSaveOrderErrorURL(), pRequest, pResponse))
	  return false;
	
	postSaveOrder(pRequest, pResponse);
	
	return checkFormRedirect (getSaveOrderSuccessURL(), getSaveOrderErrorURL(), pRequest, pResponse);
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
   * This method sets the current Order's description and saves it. Subsequently a
   * new empty Order is made the current one.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void saveOrder(DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    try {
      Order oldOrder = getShoppingCart().getCurrent();

      if (oldOrder != null) {

        String description = getDescription();
        if (checkDuplicateDescription(description)) {
          if (StringUtils.isEmpty(description)) {
            description = DateFormat.getDateTimeInstance(DateFormat.FULL,
                                                         DateFormat.FULL,
                                                         getUserLocale()).format(new Date());
          }
          oldOrder.setDescription(description);
	        oldOrder.setExplicitlySaved(true);
          getOrderManager().updateOrder(oldOrder);
          getShoppingCart().getSaved().add(oldOrder);
          getShoppingCart().setCurrent(null);
          runProcessSendScenarioEvent(oldOrder, OrderSaved.TYPE);
        }
        else
        {
          String msg = formatUserMessage(MSG_DUPLICATE_ORDER_DESCRIPTION, pRequest, pResponse);
          String propertyPath = generatePropertyPath("description");
          addFormException(new DropletFormException(msg, propertyPath, MSG_DUPLICATE_ORDER_DESCRIPTION));
        }

      }
    }
    catch (CommerceException exc) {
      if (isLoggingError())
        logError(exc);
      throw new ContainerServletException(exc);
    }
    catch (RunProcessException exc) {
      if (isLoggingError())
        logError(exc);
      throw new ContainerServletException(exc);
    }

  }

  /**
   * Checks for duplicate Descriptions among the Saved orders in the OrderHolder.
   *
   * @param pDescription a <code>String</code> value
   * @return true if no duplicates are found
   */
  protected boolean checkDuplicateDescription(String pDescription)
  {
    Iterator iterator = getShoppingCart().getSaved().iterator();
    while (iterator.hasNext()) {
      Order order = (Order)iterator.next();
      if (order != null) {
        if (order.getDescription() == null ||
            !order.getDescription().equals(pDescription)) {
          continue;
        }
        else
        {
          return false;
        }
      }
    }
    return true;
  }
  
  /**
   * The <code>runProcessSendScenarioEvent</code> method sends a scenario event.
   *
   * @param pOrder an <code>Order</code> value
   * @param pType a <code>String</code> value
   * @exception RunProcessException if an error occurs
   */
  protected void runProcessSendScenarioEvent(Order pOrder, 
                                             String pType)
    throws RunProcessException
  {
    runProcessSendScenarioEvent(pOrder, pType, null);
  }
  

  /**
   * The <code>runProcessSendScenarioEvent</code> method sends a scenario event.
   *
   * @param pOrder an <code>Order</code> value
   * @param pType a <code>String</code> value
   * @param pSiteId The site ID associated with a scenario event
   * @exception RunProcessException if an error occurs
   */
  protected void runProcessSendScenarioEvent(Order pOrder, 
                                             String pType,
                                             String pSiteId)
    throws RunProcessException
  {
    // Send a scenario event on a successfully added item.
    PipelineResult result;
    HashMap params = new HashMap();

    params.put(PipelineConstants.ORDER, pOrder);
    params.put(PipelineConstants.EVENT, pType);
    if (pSiteId != null){
      params.put(PipelineConstants.SITEID, pSiteId);
    }    

    result = getPipelineManager().runProcess(PipelineConstants.SENDSCENARIOEVENT, params);
    processPipelineErrors(result);

  }

}   // end of class
