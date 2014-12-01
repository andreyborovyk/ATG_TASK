/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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
package atg.projects.store.order;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.csr.order.CSRCommitOrderFormHandler;
import atg.commerce.order.CreditCard;

import atg.core.util.ResourceUtils;
import atg.droplet.DropletFormException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import atg.projects.store.order.purchase.StoreCommitOrderProcessHelper;

/**
 * Extends the default CSRCommitOrderFormHandler to implement custom postCommitOrder functionality.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/DCS-CSR/src/atg/projects/store/order/StoreCSRCommitOrderFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
*/

public class StoreCSRCommitOrderFormHandler extends CSRCommitOrderFormHandler{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/DCS-CSR/src/atg/projects/store/order/StoreCSRCommitOrderFormHandler.java#2 $$Change: 651448 $";

  /**
   * Commit Order Helper.
   */
  protected StoreCommitOrderProcessHelper mCommitOrderHelper;

  /**
   * @return the Order Helper component.
   */
  public StoreCommitOrderProcessHelper getCommitOrderHelper() {
    return mCommitOrderHelper;
  }

  /**
   * @param pCommitOrderHelper the order helper component to set.
   */
  public void setCommitOrderHelper(StoreCommitOrderProcessHelper pCommitOrderHelper) {
    mCommitOrderHelper = pCommitOrderHelper;
  }

  /**
   * Called after all processing is done by the handleCommitOrder method. This
   * method is responsible for populating the profile with the attributes on
   * the profile, such as itemsBought, lastPurchaseDate, numberOfOrders, etc.
   *
   * @param pRequest
   *            the request object
   * @param pResponse
   *            the response object
   * @exception ServletException
   *                if an error occurs
   * @exception IOException
   *                if an error occurs
   * @see atg.commerce.order.purchase.CommitOrderFormHandler#handleCommitOrder
   */
  public void postCommitOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    if (getFormError()) {
      // An error occurred, do not do any of this.
      return;
    }

    StoreOrderImpl order = (StoreOrderImpl) getShoppingCart().getLast();
    try {
      getCommitOrderHelper().doPostCommitOrderProcessing(order, getProfile());
    }
    catch (RepositoryException re) {
      throw new ServletException(re);
    }
    super.postCommitOrder(pRequest, pResponse);
  }
}
