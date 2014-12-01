/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2010 Art Technology Group, Inc.
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

package atg.projects.store.droplet;

import atg.commerce.CommerceException;

import atg.commerce.order.CreditCard;
import atg.commerce.order.Order;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;

import atg.nucleus.naming.ParameterName;

import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.order.StorePaymentGroupManager;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.transaction.TransactionManager;


/**
 * <p>
 * This droplet will create a credit card payment group and add it to the
 * order if it doesn't already exist. The need for this arose because we
 * are removing the credit card payment group if the online credits are
 * sufficient to pay for the order. Otherwise, CyberSource will throw
 * errors because the credit card payment group will exist, and will
 * be validated automatically. However, if the user decides not to
 * place the order, and later comes back to the billing page, we will
 * need a credit card so the form doesn't blow up trying to set properties
 * on a credit card that doesn't exist.
 *
 * @author ATG
 * @version $Revision: #3 $
 */
public class EnsureCreditCardPaymentGroup extends DynamoServlet {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/droplet/EnsureCreditCardPaymentGroup.java#3 $$Change: 635816 $";

  /** Input parameter name profile. */
  public static final ParameterName ORDER = ParameterName.getParameterName("order");

  /**
   * Store order tools.
   */
  private StoreOrderTools mStoreOrderTools;

  /**
   * Store payment group manager.
   */
  protected StorePaymentGroupManager mStorePaymentGroupManager;

  /**
   * Transaction manager.
   */
  TransactionManager mTransactionManager;

  /**
   * @return the mStoreOrderTools.
   */
  public StoreOrderTools getStoreOrderTools() {
    return mStoreOrderTools;
  }

  /**
   * @param pStoreOrderTools - the Store order tools to set.
   */
  public void setStoreOrderTools(StoreOrderTools pStoreOrderTools) {
    mStoreOrderTools = pStoreOrderTools;
  }

  /**
   * @return the Store payment group manager.
   */
  public StorePaymentGroupManager getStorePaymentGroupManager() {
    return mStorePaymentGroupManager;
  }

  /**
   * @param pStorePaymentGroupManager - the Store payment group manager.
   */
  public void setStorePaymentGroupManager(StorePaymentGroupManager pStorePaymentGroupManager) {
    mStorePaymentGroupManager = pStorePaymentGroupManager;
  }

  /**
   * @return the transaction manager.
   */
  public TransactionManager getTransactionManager() {
    return mTransactionManager;
  }

  /**
   * @param pTransactionManager -  the transactional manager.
   */
  public void setTransactionManager(TransactionManager pTransactionManager) {
    mTransactionManager = pTransactionManager;
  }

  /**
   * <p>
   * If the credit card payment group is empty, this droplet will
   * create one and add it to the order. Wrapped in a transaction
   * since we're making order modifications.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    if (isLoggingDebug()) {
      logDebug("Ensuring order contains credit card pg");
    }

    Object orderObject = pRequest.getObjectParameter(ORDER);

    if ((orderObject == null) || !(orderObject instanceof Order)) {
      if (isLoggingDebug()) {
        logDebug("Bad order parameter passed: null=" + (orderObject == null) +
          ". If null=false, then wrong object type.");
      }

      return;
    }

    Order order = (Order) orderObject;

    try {
      TransactionDemarcation td = new TransactionDemarcation();

      try {
        td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

        StoreOrderTools orderTools = getStoreOrderTools();

        CreditCard cc = orderTools.getCreditCard(order);

        if (cc == null) {
          if (isLoggingDebug()) {
            logDebug("Creating credit card pg, current one is null.");
          }

          try {
            CreditCard newCard = (CreditCard) orderTools.createPaymentGroup("creditCard");
            order.addPaymentGroup(newCard);
            getStorePaymentGroupManager().getOrderManager().updateOrder(order);
          } catch (CommerceException ce) {
            if (isLoggingError()) {
              logError(LogUtils.formatMajor("Couldn't create new cc group"), ce);
            }
          }
        } else {
          if (isLoggingDebug()) {
            logDebug("Credit card pg is not null, no need to create.");
            logDebug("Billing address is null: " + (cc.getBillingAddress() == null));
          }
        }
      } finally {
        td.end();
      }
    } catch (TransactionDemarcationException e) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Transaction error"), e);
      }
    }

    if (isLoggingDebug()) {
      logDebug("cc from orderTools is null: " + (getStoreOrderTools().getCreditCard(order) == null));
    }
  }
}
