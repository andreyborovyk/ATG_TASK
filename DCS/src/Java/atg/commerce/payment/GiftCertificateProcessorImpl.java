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

import atg.nucleus.GenericService;
import atg.payment.giftcertificate.*;
import atg.payment.PaymentStatus;
import atg.commerce.claimable.ClaimableManager;


/**
 * This class performs the actual functions of authorizing, debiting and crediting a
 * giftcertificate.  These are all the method that must be implemented in order to provide
 * a payment method.
 * 
 * @author Ashley J. Streb
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/GiftCertificateProcessorImpl.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class GiftCertificateProcessorImpl extends GenericService implements GiftCertificateProcessor,
DecreaseGiftCertificateAuthorizationProcessor
{
  //-------------------------------------
  // Class version string
  //-------------------------------------
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/GiftCertificateProcessorImpl.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------
  
  //---------------------------------------------------------------------------
  // property: ClaimableManager
  /**
   * The claimable manager is responsible for interacting with the repository where
   * the giftcertificates are actually held.  The claimable manager is the service that
   * performs the actual credit/debit on a gift certificate.
   */
  ClaimableManager mClaimableManager;

  /**
   * Set the ClaimableManager that will be used to claim the gift certificats.
   *
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
  public GiftCertificateProcessorImpl() {
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
   * The authorizaton of a gift certificate calls the authorizeClaimableGiftCerificate method in
   * the ClaimableManager.
   *
   * @param pGiftCertificateInfo the GiftCertificateInfo reference which contains all the 
   * authorization data
   * @return a GiftCertificateStatus object detailing the results of the authorization
   */
  public GiftCertificateStatus authorize(GiftCertificateInfo pGiftCertificateInfo)
  {
    // attempt to authorize the amount for the GiftCertificate.
    // catch errors, and log them, else return success.
    try {
      getClaimableManager().authorizeClaimableGiftCertificate(pGiftCertificateInfo.getGiftCertificateNumber(), pGiftCertificateInfo.getAmount());
    } catch (Exception exc) {
      String errorMsg = exc.getMessage() != null ? exc.getMessage() : exc.toString();
      return new GiftCertificateStatusImpl(getNextTransactionId(), pGiftCertificateInfo.getAmount(),
            false, errorMsg, new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (getSecondsUntilExpiration() * 1000)));
    }
    return new GiftCertificateStatusImpl(getNextTransactionId(), pGiftCertificateInfo.getAmount(),
            true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (getSecondsUntilExpiration() * 1000)));
  }
 
  /**
   * Expire a gift certificate authorization
   *
   * @param pGiftCertificateInfo the GiftCertificateInfo reference which contains all the authorization expiration data
   * @return a GiftCertificateStatus object detailing the results of the authorization
   */
  public GiftCertificateStatus expireAuthorization(GiftCertificateInfo pGiftCertificateInfo) {
    // attempt to expire and authorized amount for the GiftCertificate.
    // catch errors, and log them, else return success.
    try {
      getClaimableManager().expireGiftCertificateAuthorization(pGiftCertificateInfo.getGiftCertificateNumber(), pGiftCertificateInfo.getAmount());
    } catch (Exception exc) {
      String errorMsg = exc.getMessage() != null ? exc.getMessage() : exc.toString();
      return new GiftCertificateStatusImpl(getNextTransactionId(), pGiftCertificateInfo.getAmount(),
            false, errorMsg, new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (getSecondsUntilExpiration() * 1000)));
    }
    return new GiftCertificateStatusImpl(getNextTransactionId(), pGiftCertificateInfo.getAmount(),
            true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (getSecondsUntilExpiration() * 1000)));
  }
  
  /**
   * We check to see if the PaymentStatus amount is greater that the amount indicated in the
   * giftCertificateInfo.  If it is greater, than we credit back the difference to the
   * giftcertificate via the ClaimableManager.
   *
   * @param pGiftCertificateInfo the GiftCertificateInfo reference which contains all the debit 
   * data
   * @param pStatus the PaymentStatus object which contains information about the transaction. 
   * This should be the object which was returned from authorize().
   * @return a GiftCertificateStatus object detailing the results of the debit
   */
  public GiftCertificateStatus debit(GiftCertificateInfo pGiftCertificateInfo, 
                                     PaymentStatus pStatus)
  {
    // attempt to debit the amount from the GiftCertificate.
    // catch errors, and log them, else return success.
    // attempt to authorize the amount for the GiftCertificate.
    // catch errors, and log them, else return success.
    try {
      getClaimableManager().debitClaimableGiftCertificate(pGiftCertificateInfo.getGiftCertificateNumber(), pGiftCertificateInfo.getAmount());
    } catch (Exception exc) {
      String errorMsg = exc.getMessage() != null ? exc.getMessage() : exc.toString();
      return new GiftCertificateStatusImpl(getNextTransactionId(), pGiftCertificateInfo.getAmount(),
            false, errorMsg, new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (getSecondsUntilExpiration() * 1000)));
    }
    return new GiftCertificateStatusImpl(getNextTransactionId(), pGiftCertificateInfo.getAmount(),
            true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (getSecondsUntilExpiration() * 1000)));
  }

  /**
   * Credit the gift certificate identified with the parameter 
   * pGiftCertificateInfo.getGiftCertificateNumber() the amount returned by the getAmount call
   * on the same giftCertificateInfo parameter.  The creditClaimableGiftCertificate method
   * will be called on the claimableManager to perform the actual crediting.  
   * @param pGiftCertificateInfo the GiftCertificateInfo reference which contains all the credit data
   * @param pStatus the PaymentStatus object which contains information about the transaction. This
   *                should be the object which was returned from debit().
   * @return a GiftCertificateStatus object detailing the results of the credit
   */
  public GiftCertificateStatus credit(GiftCertificateInfo pGiftCertificateInfo, 
                                      PaymentStatus pStatus)
  {
    try {
      getClaimableManager().creditClaimableGiftCertificate(pGiftCertificateInfo.getGiftCertificateNumber(), pGiftCertificateInfo.getAmount());
    } catch (Exception exc) {
      String errorMsg = exc.getMessage() != null ? exc.getMessage() : exc.toString();
      return new GiftCertificateStatusImpl(getNextTransactionId(), pGiftCertificateInfo.getAmount(),
              false, errorMsg, new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (getSecondsUntilExpiration() * 1000)));
    }
    return new GiftCertificateStatusImpl(getNextTransactionId(), pGiftCertificateInfo.getAmount(),
            true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (getSecondsUntilExpiration() * 1000)));
  }

  /**
   * Credit the gift certificate identified with the parameter 
   * pGiftCertificateInfo.getGiftCertificateNumber() the amount returned by the getAmount call
   * on the same giftCertificateInfo parameter.  The creditClaimableGiftCertificate method
   * will be called on the claimableManager to perform the actual crediting.  
   * @param pGiftCertificateInfo the GiftCertificateInfo reference which contains all the credit data
   * @return a GiftCertificateStatus object detailing the results of the credit
   */
  public GiftCertificateStatus credit(GiftCertificateInfo pGiftCertificateInfo)
  {
    return credit(pGiftCertificateInfo, null);
  }
  
  public GiftCertificateStatus decreaseAuthorization(GiftCertificateInfo pGiftCertificateInfo, PaymentStatus pAuthStatus)
  {
    try 
    {
      if (isLoggingDebug())
        logDebug("Decreasing store credit authorized amount on " + pGiftCertificateInfo.getGiftCertificateNumber() + " for " + pGiftCertificateInfo.getAmount());
      
      getClaimableManager().decreaseAuthorizationForClaimableGiftCertificate(pGiftCertificateInfo.getGiftCertificateNumber(), pGiftCertificateInfo.getAmount());
    } 
    catch (Exception exc) 
    {
      String errorMsg = exc.getMessage() != null ? exc.getMessage() : exc.toString();
      return new GiftCertificateStatusImpl(getNextTransactionId(), (pGiftCertificateInfo.getAmount() * -1),
          false, errorMsg, new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (getSecondsUntilExpiration() * 1000)));
    }
    return new GiftCertificateStatusImpl(getNextTransactionId(), (pGiftCertificateInfo.getAmount() * -1),
        true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (getSecondsUntilExpiration() * 1000)));
  }
  
}
// end of class
