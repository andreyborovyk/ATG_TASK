/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

package atg.repository.servlet;

import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.droplet.*;
import atg.dtm.*;

import javax.transaction.*;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * This form handler adds transaction management capabilities while processing form
 * input. Typically when one is modifying RepositoryItems one should have a transaction
 * in place to enclose all the modifications. The SQL Repository will automatically start
 * and end a transaction for each <code>setPropertyValue</code> call unless a transaction
 * is already in place. This can lead to significant performance problems and inconsistent
 * data, from the application's perspective, because one set could succeed and another
 * might fail. If the <code>ensureTransaction</code> property is true, then transactions
 * are created, if necessary, in the <code>beforeSet</code> operation and committed or rolledback
 * in the <code>afterSet</code> method. Instances of this form handler should be request scoped,
 * or if session scoped, the component should be synchronized.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/repository/servlet/TransactionalRepositoryFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public
class TransactionalRepositoryFormHandler
extends RepositoryFormHandler
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/repository/servlet/TransactionalRepositoryFormHandler.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  private static final String TRANSACTION_NAME = "java:comp/UserTransaction";


  //-------------------------------------
  // Member Variables
  //-------------------------------------
  private static TransactionManager sTransactionManager;

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // property: EnsureTransaction
  private boolean mEnsureTransaction = true;

  /**
   * Sets property EnsureTransaction
   **/
  public void setEnsureTransaction(boolean pEnsureTransaction) {
    mEnsureTransaction = pEnsureTransaction;
  }

  /**
   * Returns property EnsureTransaction, if false then no transaction management is performed
   **/
  public boolean isEnsureTransaction() {
    return mEnsureTransaction;
  }

  //-------------------------------------
  // property: TransactionDemarcation
  private TransactionDemarcation mTransactionDemarcation = null;

  /**
   * Sets property TransactionDemarcation
   **/
  protected void setTransactionDemarcation(TransactionDemarcation pTransactionDemarcation) {
    mTransactionDemarcation = pTransactionDemarcation;
  }

  /**
   * Returns property TransactionDemarcation. This object that is used to manage transactions in the
   * <code>beforeSet</code> and <code>afterSet</code> methods. This is created in the
   * <code>beforeSet</code> method and set to null in <code>afterSet</code>.
   **/
  protected TransactionDemarcation getTransactionDemarcation() {
    return mTransactionDemarcation;
  }

  //-------------------------------------
  // property: RollbackTransaction
  private boolean mRollbackTransaction = false;

  /**
   * Sets property RollbackTransaction
   **/
  protected void setRollbackTransaction(boolean pRollbackTransaction) {
    mRollbackTransaction = pRollbackTransaction;
  }

  /**
   * Returns property RollbackTransaction.  If this is true, then the transaction will be marked
   * for rollback in the <code>afterSet</code> method. This defaults to false and is
   * reset to false in <code>afterSet</code>.
   **/
  protected boolean isRollbackTransaction() {
    return mRollbackTransaction;
  }

  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instanceof TransactionalRepositoryFormHandler
   */
  public TransactionalRepositoryFormHandler() {
    if (sTransactionManager == null) {
      Object obj = Nucleus.getGlobalNucleus().resolveName("/atg/dynamo/transaction/TransactionManager");
      if (obj instanceof TransactionManager)
        sTransactionManager = (TransactionManager)obj;
    }
  }

  /**
   * Creates a new TransactionDemarcation and then begins the transaction. If the
   * <code>ensureTransaction</code> property is false then no transacation management
   * is performed.
   */
  public boolean beforeSet(DynamoHttpServletRequest pRequest,
			   DynamoHttpServletResponse pResponse)
    throws DropletFormException
  {
    boolean ret;
    try {
      if (isEnsureTransaction()) {
        setTransactionDemarcation(new TransactionDemarcation());
        try {
          getTransactionDemarcation().begin(sTransactionManager);
        }
        catch (TransactionDemarcationException exc) {
          if (isLoggingError())
            logError(exc);
        }
      }
    }
    finally {
      ret = super.beforeSet(pRequest, pResponse);
    }
    return ret;
  }

  /**
   * If the TransactionDemarcation is not null, then the transaction is ended.
   * The transaction can be marked for rollback if the property <code>rollbackTransaction</code>
   * is set to true, otherwise the transaction is committed. If the <code>ensureTransaction</code>
   * property is false then no transacation management is performed. The <code>transactionDemarcation</code>
   * property is set to null and <code>rollbackTransaction</code> property is set to false in the finally
   * clause of this method after the <code>super.afterSet</code> method is called.
   */
  public boolean afterSet(DynamoHttpServletRequest pRequest,
			  DynamoHttpServletResponse pResponse)
    throws DropletFormException
  {
    boolean ret;
    try {
      if ((isEnsureTransaction()) && (getTransactionDemarcation() != null)) {
        getTransactionDemarcation().end(isRollbackTransaction());
      }
    }
    catch (TransactionDemarcationException exc) {
      if (isLoggingError())
        logError(exc);
    }
    finally {
      ret = super.afterSet(pRequest, pResponse);
      setTransactionDemarcation(null);
      setRollbackTransaction(false);
    }
    return ret;
  }

  /**
   * Returns true if the transaction associated with the current thread
   * is marked for rollback.
   */
  protected boolean isTransactionMarkedAsRollback() {
    if (isRollbackTransaction()) {
      return true;
    }
    else {
      try {
        TransactionManager tm = sTransactionManager;
        if (tm != null) {
          int transactionStatus = tm.getStatus();
          if (transactionStatus == javax.transaction.Status.STATUS_MARKED_ROLLBACK)
            return true;
        }
      }
      catch (SystemException exc) {
        if (isLoggingError())
          logError(exc);
      }
    }

    return false;
  }

  /**
   * If the transaction is marked as rollback, then redirect to the FailureURL,
   * otherwise allow the super-class behavior to perform.
   * @return If redirect (for whatever reason) to a new page occurred,
   *         return false.  If NO redirect occurred, return true.
   * @param pNoErrorsURL The URL to redirect to if there were no form errors.
   *                     If a null value is passed in, no redirect occurs.
   * @param pErrorsURL The URL to redirect to if form errors were found.
   *                   If a null value is passed in, no redirect occurs.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean checkFormRedirect (String pSuccessURL,
                                    String pFailureURL,
                                    DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    //If form errors were found:
    if (isTransactionMarkedAsRollback()) {
      //If FailureURL was not passed in, stay on same page, return true:
      if (pFailureURL == null) {
        if (isLoggingDebug()) {
          logDebug("Transaction Marked as Rollback - staying on same page.");
        }
        return true;
      }

      //If FailureURL was passed in, redirect and return false:
      else {
        if (isLoggingDebug()) {
          logDebug("Transaction Marked as Rollback - redirecting to: " + pFailureURL);
        }
        pResponse.sendLocalRedirect(pFailureURL, pRequest);
        return false;
      }
    }
    else {
      return super.checkFormRedirect(pSuccessURL, pFailureURL, pRequest, pResponse);
    }
  }


} // end of class
