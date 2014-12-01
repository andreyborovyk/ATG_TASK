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

package atg.commerce.payment;

import atg.payment.storecredit.*;
import atg.payment.PaymentStatus;
import atg.commerce.claimable.ClaimableManager;
import atg.nucleus.GenericService;

import java.util.Date;

/**
 * This class performs the actual functions of authorizing, debiting and crediting a
 * store credit.  These are all the method that must be implemented in order to provide
 * a payment method.
 * 
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/StoreCreditProcessorImpl.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class StoreCreditProcessorImpl extends GenericService 
implements StoreCreditProcessor, DecreaseStoreCreditAuthorizationProcessor
{
  //-------------------------------------
  // Class version string
  //-------------------------------------
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/StoreCreditProcessorImpl.java#2 $$Change: 651448 $";
    
  //-------------------------------------
  // property: ClaimableManager
  //-------------------------------------
  /**
   * The claimable manager is responsible for interacting with the repository where
   * the store credits are actually held.  The claimable manager is the service that
   * performs the actual credit/debit on a store credit.
   */
  private ClaimableManager mClaimableManager = null;

  /**
   * Set the ClaimableManager that will be used to claim the store credits.
   * @param pClaimableManager the ClaimableManager that will be used
   */
  public void setClaimableManager(ClaimableManager pClaimableManager) {
    mClaimableManager = pClaimableManager;
  }

  /**
   * Get the ClaimableManager property.
   *
   * @return the ClaimableManager
   */
  public ClaimableManager getClaimableManager() {
    return mClaimableManager;
  }

  //-------------------------------------
  // property: secondsUntilExpiration
  //-------------------------------------
  private long mSecondsUntilExpiration = 604800; // default is 1 week
  
  /**
   * Returns property secondsUntilExpiration
   *
   * @return returns property secondsUntilExpiration
   */
  public long getSecondsUntilExpiration() {
    return mSecondsUntilExpiration;
  }

  /**
   * Sets property secondsUntilExpiration
   *
   * @param pSecondsUntilExpiration the value to set for property secondsUntilExpiration
   */
  public void setSecondsUntilExpiration(long pSecondsUntilExpiration) {
    mSecondsUntilExpiration = pSecondsUntilExpiration;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------
  public StoreCreditProcessorImpl() {
  }

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------
  
  /**
   * This method generates a transactionId by returning a the System time in milliseconds.
   * Override this method to generate the id a different way.
   */
  protected synchronized String getNextTransactionId() {
    return Long.toString(System.currentTimeMillis());
  }
  
  /**
   * The authorizaton of a store credit calls the authorizeClaimableStoreCredit method in
   * the ClaimableManager.
   *
   * @param pStoreCreditInfo the StoreCreditInfo reference which contains all the 
   * authorization data
   * @return a StoreCreditStatus object detailing the results of the authorization
   */
  public StoreCreditStatus authorize(StoreCreditInfo pStoreCreditInfo)
  {
    // attempt to authorize the amount for the StoreCredit.
    // catch errors, and log them, else return success.
    try {
      if (isLoggingDebug())
        logDebug("Authorizing store credit " + pStoreCreditInfo.getStoreCreditNumber() + " for " + pStoreCreditInfo.getAmount());
      getClaimableManager().authorizeClaimableStoreCredit(pStoreCreditInfo.getStoreCreditNumber(), pStoreCreditInfo.getAmount());
    } catch (Exception exc) {
      String errorMsg = exc.getMessage() != null ? exc.getMessage() : exc.toString();
      return new StoreCreditStatusImpl(getNextTransactionId(), pStoreCreditInfo.getAmount(),
            false, errorMsg, new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (getSecondsUntilExpiration() * 1000)));
    }
    return new StoreCreditStatusImpl(getNextTransactionId(), pStoreCreditInfo.getAmount(),
            true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (getSecondsUntilExpiration() * 1000)));
  }
 
  /**
   * Expire a store credit authorization
   *
   * @param pStoreCreditInfo the StoreCreditInfo reference which contains all the authorization expiration data
   * @return a StoreCreditStatus object detailing the results of the authorization
   */
  public StoreCreditStatus expireAuthorization(StoreCreditInfo pStoreCreditInfo) {
    // attempt to expire and authorized amount for the StoreCredit.
    // catch errors, and log them, else return success.
    try {
      if (isLoggingDebug())
        logDebug("Expiring authorization on store credit " + pStoreCreditInfo.getStoreCreditNumber() + " for " + pStoreCreditInfo.getAmount());
      getClaimableManager().expireStoreCreditAuthorization(pStoreCreditInfo.getStoreCreditNumber(), pStoreCreditInfo.getAmount());
    } catch (Exception exc) {
      String errorMsg = exc.getMessage() != null ? exc.getMessage() : exc.toString();
      return new StoreCreditStatusImpl(getNextTransactionId(), pStoreCreditInfo.getAmount(),
            false, errorMsg, new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (getSecondsUntilExpiration() * 1000)));
    }
    return new StoreCreditStatusImpl(getNextTransactionId(), pStoreCreditInfo.getAmount(),
            true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (getSecondsUntilExpiration() * 1000)));
  }
  
  /**
   * We check to see if the PaymentStatus amount is greater than the amount indicated in the
   * storeCreditInfo.  If it is greater, than we credit back the difference to the
   * StoreCredit via the ClaimableManager.
   *
   * @param pStoreCreditInfo the StoreCreditInfo reference which contains all the debit 
   * data
   * @param pStatus the PaymentStatus object which contains information about the transaction. 
   * This should be the object which was returned from authorize().
   * @return a StoreCreditStatus object detailing the results of the debit
   */
  public StoreCreditStatus debit(StoreCreditInfo pStoreCreditInfo, 
                                     PaymentStatus pStatus)
  {
    // attempt to debit the amount from the StoreCredit.
    // catch errors, and log them, else return success.
    try {
      if (isLoggingDebug())
        logDebug("Debiting store credit " + pStoreCreditInfo.getStoreCreditNumber() + " for " + pStoreCreditInfo.getAmount());
      getClaimableManager().debitClaimableStoreCredit(pStoreCreditInfo.getStoreCreditNumber(), pStoreCreditInfo.getAmount());
    } catch (Exception exc) {
      String errorMsg = exc.getMessage() != null ? exc.getMessage() : exc.toString();
      return new StoreCreditStatusImpl(getNextTransactionId(), pStoreCreditInfo.getAmount(),
            false, errorMsg, new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (getSecondsUntilExpiration() * 1000)));
    }
    return new StoreCreditStatusImpl(getNextTransactionId(), pStoreCreditInfo.getAmount(),
            true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (getSecondsUntilExpiration() * 1000)));
  }

  /**
   * Credit the store credit identified with the parameter 
   * pStoreCreditInfo.getStoreCreditNumber() the amount returned by the getAmount call
   * on the same storeCreditInfo parameter.  The creditClaimableStoreCredit method
   * will be called on the claimableManager to perform the actual crediting.  
   * @param pStoreCreditInfo the StoreCreditInfo reference which contains all the credit data
   * @param pStatus the PaymentStatus object which contains information about the transaction. This
   *                should be the object which was returned from debit().
   * @return a StoreCreditStatus object detailing the results of the credit
   */
  public StoreCreditStatus credit(StoreCreditInfo pStoreCreditInfo, 
                                      PaymentStatus pStatus)
  {
    try {
      if (isLoggingDebug())
        logDebug("Crediting store credit " + pStoreCreditInfo.getStoreCreditNumber() + " for " + pStoreCreditInfo.getAmount());
      getClaimableManager().creditClaimableStoreCredit(pStoreCreditInfo.getStoreCreditNumber(), pStoreCreditInfo.getAmount());
    } catch (Exception exc) {
      String errorMsg = exc.getMessage() != null ? exc.getMessage() : exc.toString();
      return new StoreCreditStatusImpl(getNextTransactionId(), pStoreCreditInfo.getAmount(),
              false, errorMsg, new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (getSecondsUntilExpiration() * 1000)));
    }
    return new StoreCreditStatusImpl(getNextTransactionId(), pStoreCreditInfo.getAmount(),
            true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (getSecondsUntilExpiration() * 1000)));
  }

  /**
   * Credit the store credit identified with the parameter 
   * pStoreCreditInfo.getStoreCreditNumber() the amount returned by the getAmount call
   * on the same StoreCreditInfo parameter.  The creditClaimableStoreCredit method
   * will be called on the claimableManager to perform the actual crediting.  
   * @param pStoreCreditInfo the StoreCreditInfo reference which contains all the credit data
   * @return a StoreCreditStatus object detailing the results of the credit
   */
  public StoreCreditStatus credit(StoreCreditInfo pStoreCreditInfo)
  {
    return credit(pStoreCreditInfo, null);
  }
  
  /**
   * Decreases the authorized amount for the store credit identified with the parameter 
   * @param pStoreCreditInfo the StoreCreditInfo reference which contains all the credit data
   * @return a StoreCreditStatus object detailing the results of the decrease
   */
  public StoreCreditStatus decreaseAuthorization(StoreCreditInfo pStoreCreditInfo, PaymentStatus pAuthStatus)
  {
    try 
    {
      if (isLoggingDebug())
        logDebug("Decreasing store credit authorized amount on " + pStoreCreditInfo.getStoreCreditNumber() + " for " + pStoreCreditInfo.getAmount());
      
      getClaimableManager().decreaseAuthorizationForClaimableStoreCredit(pStoreCreditInfo.getStoreCreditNumber(), pStoreCreditInfo.getAmount());
    } 
    catch (Exception exc) 
    {
      String errorMsg = exc.getMessage() != null ? exc.getMessage() : exc.toString();
      return new StoreCreditStatusImpl(getNextTransactionId(), (pStoreCreditInfo.getAmount()* -1),
              false, errorMsg, new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (getSecondsUntilExpiration() * 1000)));
    }
    return new StoreCreditStatusImpl(getNextTransactionId(), (pStoreCreditInfo.getAmount()* -1),
            true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (getSecondsUntilExpiration() * 1000)));
  }

}
// end of class
