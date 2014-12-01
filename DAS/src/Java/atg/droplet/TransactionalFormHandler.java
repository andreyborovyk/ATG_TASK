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

package atg.droplet;

import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
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
 * @author Bob Mason
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/TransactionalFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public 
class TransactionalFormHandler
extends GenericFormHandler
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/TransactionalFormHandler.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------

  String mRequestDemarcationKey = "TransactionFormHandler:" + 
                System.identityHashCode(this);

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
  /**
   * Sets property TransactionDemarcation
   * Previously, this method stored the TransactionDemarcation
   * object as a member variable.  Since this component can be session scoped,
   * multiple requests using the same session scoped form handler can collide
   * with their use of the form handler.  Now, this method stores the
   * attribute in the current request.
   **/
  protected void setTransactionDemarcation(TransactionDemarcation pTransactionDemarcation) {
    DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
    if (req != null) req.setAttribute(mRequestDemarcationKey, pTransactionDemarcation);
    else if (isLoggingError())
      logError("Unable to set transaction demarcation as there is no current request");
  }

  /**
   * Returns property TransactionDemarcation. This object that is used to manage transactions in the
   * <code>beforeSet</code> and <code>afterSet</code> methods. This is created in the
   * <code>beforeSet</code> method and set to null in <code>afterSet</code>.
   **/
  protected TransactionDemarcation getTransactionDemarcation() {
    DynamoHttpServletRequest req = ServletUtil.getCurrentRequest();
    if (req != null) 
      return ((TransactionDemarcation) req.getAttribute(mRequestDemarcationKey));
    return null;
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
   * Constructs an instanceof TransactionalFormHandler
   */
  public TransactionalFormHandler() {
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
    boolean result;
    try {
      if (isEnsureTransaction()) {
        TransactionDemarcation td = new TransactionDemarcation();
        pRequest.setAttribute(mRequestDemarcationKey, td);
        try {
          td.begin(sTransactionManager);
        }
        catch (TransactionDemarcationException exc) {
          if (isLoggingError())
            logError(exc);
        }
      }
    }
    finally {
      result = super.beforeSet(pRequest, pResponse);
    }
    return result;
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
      TransactionDemarcation td;
      if (isEnsureTransaction() && 
          ((td = (TransactionDemarcation) pRequest.getAttribute(mRequestDemarcationKey)) != null)) {
        td.end(isTransactionMarkedAsRollback());
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
  protected boolean isTransactionMarkedAsRollback()
  {
    boolean isMarked = false;
    if (isRollbackTransaction()) {
      isMarked = true;
    }
    else if (isRollbackTransactionOnUnhandledException() && hasUncheckedFormExceptions()) {
      isMarked = true;
    }
    else {
      try {
        TransactionManager tm = sTransactionManager;
        if (tm != null) {
          int transactionStatus = tm.getStatus();
          if (transactionStatus == javax.transaction.Status.STATUS_MARKED_ROLLBACK)
            isMarked = true;
        }
      }
      catch (SystemException exc) {
        if (isLoggingError())
          logError(exc);
      }
    }
    return isMarked;
  }
  
  /** 
   * Obtain the status of the transaction associated with the current thread.
   * @return The transaction status. If no transaction is associated with the current thread, this method returns the Status.NoTransaction value
   *         Logs an error and returns unknown status if {@link javax.transaction.TransactionManager#getStatus()} throws a SystemException. 
   */
  protected int getTransactionStatus() {
    try {
      return (sTransactionManager == null) ? javax.transaction.Status.STATUS_NO_TRANSACTION : sTransactionManager.getStatus();
    } catch (SystemException exc) {
      if (isLoggingError()) logError(exc);
      return javax.transaction.Status.STATUS_UNKNOWN;
    }
  }
  
  /**
   * If the transaction is marked as rollback, then redirect to the FailureURL,
   * otherwise allow the super-class behavior to perform.
   * @return If redirect (for whatever reason) to a new page occurred,
   *         return false.  If NO redirect occurred, return true.
   * @param pSuccessURL The URL to redirect to if there were no form errors.
   *                     If a null value is passed in, no redirect occurs.
   * @param pFailureURL The URL to redirect to if form errors were found.
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

  //---------------------------------
  /** 
   * This method commits the current transaction (or rolls it back if 
   * you have called setRollbackTransaction(true) since the transaction
   * started). 
   */
  public void commitTransaction()
  {
    TransactionDemarcation td;
    try {
      if ((td = getTransactionDemarcation()) != null)
       td.end(isTransactionMarkedAsRollback());
    }
    catch (TransactionDemarcationException exc) {
      if (isLoggingError())
        logError(exc);
    }
    finally {
      setTransactionDemarcation(null);
      setRollbackTransaction(false);
    }
  }

  //-------------------------------------
  // property: isRollbackTransactionOnUnhandledException (default is false)
  private boolean mRollbackTransactionOnUnhandledException = false;

  /**
   * Gets the value of the isRollbackTransactionOnUnhandledException property.
   *
   * @return true if the transaction should rollback if there was an unhandled exception thrown from a handle method.
   */
  public boolean isRollbackTransactionOnUnhandledException() {
    return mRollbackTransactionOnUnhandledException;
  }

  /**
   * Sets the value of the isRollbackTransactionOnUnhandledException property.
   *
   * @param pRollbackTransactionOnUnhandledException
   *         true if the transaction should rollback if there was an
   *         unhandled exception thrown from a handle method.
   */
  public void setRollbackTransactionOnUnhandledException(boolean pRollbackTransactionOnUnhandledException) {
    mRollbackTransactionOnUnhandledException = pRollbackTransactionOnUnhandledException;
  }

} // end of class
