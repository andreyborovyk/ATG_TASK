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
package atg.commerce.payment;

import atg.payment.creditcard.*;
import atg.payment.PaymentStatus;
import atg.nucleus.GenericService;

/**
 * This class is a dummy credit card processor. It purpose is to be a placeholder
 * for a real credit card processor. The implementations of all the methods in this
 * class construct a new CreditCardStatus object with dummy success data and return
 * it to the caller.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/DummyCreditCardProcessor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.payment.creditcard.CreditCardProcessor
 */
public class DummyCreditCardProcessor extends GenericService implements CreditCardProcessor, DecreaseCreditCardAuthorizationProcessor
{

  /* Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/DummyCreditCardProcessor.java#2 $$Change: 651448 $";

  public DummyCreditCardProcessor() {
  }

  /**
   * Authorize the amount on the credit card
   *
   * @param pCreditCardInfo the CreditCardInfo reference which contains all the authorization data
   * @return a CreditCardStatus object detailing the results of the authorization
   */
  public CreditCardStatus authorize(CreditCardInfo pCreditCardInfo) {
    if (isLoggingDebug()) {
    	//ensure last four digits of credit card number are only displayed. PCI regulations.
    	logLastFourCreditCardDigits(pCreditCardInfo, "Authorizing");
    }      
    return new CreditCardStatusImpl(Long.toString(System.currentTimeMillis()), pCreditCardInfo.getAmount(),
                true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)));
  }
 
  /**
   * Debit the amount on the credit card after authorization
   *
   * @param pCreditCardInfo the CreditCardInfo reference which contains all the debit data
   * @param pStatus the CreditCardStatus object which contains information about the transaction. This
   *                should be the object which was returned from authorize().
   * @return a CreditCardStatus object detailing the results of the debit
   */
  public CreditCardStatus debit(CreditCardInfo pCreditCardInfo, CreditCardStatus pStatus) {
    if (isLoggingDebug()){
    	//ensure last four digits of credit card number are only displayed. PCI regulations.    
    	logLastFourCreditCardDigits(pCreditCardInfo, "Debiting");  
    }      
    return new CreditCardStatusImpl(Long.toString(System.currentTimeMillis()), pCreditCardInfo.getAmount(),
                true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)));
  }

  /**
   * Credit the amount on the credit card after debiting
   *
   * @param pCreditCardInfo the CreditCardInfo reference which contains all the credit data
   * @param pStatus the CreditCardStatus object which contains information about the transaction. This
   *                should be the object which was returned from debit().
   * @return a CreditCardStatus object detailing the results of the credit
   */
  public CreditCardStatus credit(CreditCardInfo pCreditCardInfo, CreditCardStatus pStatus) {
    if (isLoggingDebug()){
    	//ensure last four digits of credit card number are only displayed. PCI regulations.    
    	logLastFourCreditCardDigits(pCreditCardInfo, "Crediting");  
    }      
    return new CreditCardStatusImpl(Long.toString(System.currentTimeMillis()), pCreditCardInfo.getAmount(),
                true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)));
  }

  /**
   * Credit the amount on the credit card with as a new order
   *
   * @param pCreditCardInfo the CreditCardInfo reference which contains all the credit data
   * @return a CreditCardStatus object detailing the results of the credit
   */
  public CreditCardStatus credit(CreditCardInfo pCreditCardInfo) {
    if (isLoggingDebug()){
    	//ensure last four digits of credit card number are only displayed. PCI regulations.
    	logLastFourCreditCardDigits(pCreditCardInfo, "Crediting");  
    }      
    return new CreditCardStatusImpl(Long.toString(System.currentTimeMillis()), pCreditCardInfo.getAmount(),
                true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)));
  }
  
  
  /**
   * Decreases the authorized amount for the credit card.
   * <p>
   * This implementation does nothing but return a successful CreditCardStatus object.
   * <p>
   * Extend this method to do any credit card specific processing.  
   * @param pCreditCardInfo the CreditCardInfo reference which contains all the credit data
   * @return a CreditCardStatus object detailing the results of the decrease
   */
  public CreditCardStatus decreaseAuthorization(CreditCardInfo pCreditCardInfo, PaymentStatus pAuthStatus)
  {
    if (isLoggingDebug()){
    	//ensure last four digits of credit card number are only displayed. PCI regulations.    
    	logLastFourCreditCardDigits(pCreditCardInfo, "Decreasing authorization");    	
    }      
    return new CreditCardStatusImpl(Long.toString(System.currentTimeMillis()), (pCreditCardInfo.getAmount() * -1),
                true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)));
  }
  
  //Utility method that logs last four digits of credit card number. 
  //In compliance with Payment Card Industry (PCI) regulations.
  private void logLastFourCreditCardDigits(CreditCardInfo pCreditCardInfo, String pLogType)
  {
  	String creditCardNumber = null;
  	String lastFourDigits = null;
  	
  	if ( pCreditCardInfo != null ){
  	  creditCardNumber = pCreditCardInfo.getCreditCardNumber();
    	if ( creditCardNumber != null && creditCardNumber.length() > 4 ){
        lastFourDigits = creditCardNumber.substring(creditCardNumber.length() - 4);          
    	}        
    }
  	logDebug(pLogType + " credit card **** **** **** " + lastFourDigits + " for " + pCreditCardInfo.getAmount());   	
  }
  
}
