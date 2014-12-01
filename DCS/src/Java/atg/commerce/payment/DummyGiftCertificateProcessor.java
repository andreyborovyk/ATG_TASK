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

import atg.payment.giftcertificate.*;
import atg.payment.PaymentStatus;

import java.util.Date;

/**
 * This class is a dummy gift certificate processor. It purpose is to be a placeholder
 * for a real gift certificate processor. The implementations of all the methods in this
 * class construct a new GiftCertificateStatus object with dummy success data and return
 * it to the caller.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/DummyGiftCertificateProcessor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.payment.giftcertificate.GiftCertificateProcessor
 */
public class DummyGiftCertificateProcessor implements GiftCertificateProcessor {
  //-----------------------------------
  /* Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/DummyGiftCertificateProcessor.java#2 $$Change: 651448 $";

  //-----------------------------------
  public DummyGiftCertificateProcessor() {
  }

  //-----------------------------------
  /**
   * Authorize the gift certificate
   *
   * @param pGiftCertificateInfo the GiftCertificateInfo reference which contains all the authorization data
   * @return a GiftCertificateStatus object detailing the results of the authorization
   */
  public GiftCertificateStatus authorize(GiftCertificateInfo pGiftCertificateInfo) {
    return new GiftCertificateStatusImpl(Long.toString(System.currentTimeMillis()), pGiftCertificateInfo.getAmount(),
                    true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)));
  }
 
  //-----------------------------------
  /**
   * Expire a gift certificate authorization
   *
   * @param pGiftCertificateInfo the GiftCertificateInfo reference which contains all the authorization expiration data
   * @return a GiftCertificateStatus object detailing the results of the authorization
   */
  public GiftCertificateStatus expireAuthorization(GiftCertificateInfo pGiftCertificateInfo) {
    return new GiftCertificateStatusImpl(Long.toString(System.currentTimeMillis()), pGiftCertificateInfo.getAmount(),
                    true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)));
  }

  //-----------------------------------
  /**
   * Debit the amount on the gift certificate after authorization
   *
   * @param pGiftCertificateInfo the GiftCertificateInfo reference which contains all the debit data
   * @param pStatus the PaymentStatus object which contains information about the transaction. This
   *                should be the object which was returned from authorize().
   * @return a GiftCertificateStatus object detailing the results of the debit
   */
  public GiftCertificateStatus debit(GiftCertificateInfo pGiftCertificateInfo, PaymentStatus pStatus) {
    return new GiftCertificateStatusImpl(Long.toString(System.currentTimeMillis()), pGiftCertificateInfo.getAmount(),
                    true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)));
  }

  //-----------------------------------
  /**
   * Credit the amount on the gift certificate after debiting.
   *
   * @param pGiftCertificateInfo the GiftCertificateInfo reference which contains all the credit data
   * @param pStatus the PaymentStatus object which contains information about the transaction. This
   *                should be the object which was returned from debit().
   * @return a GiftCertificateStatus object detailing the results of the credit
   */
  public GiftCertificateStatus credit(GiftCertificateInfo pGiftCertificateInfo, PaymentStatus pStatus) {
    return new GiftCertificateStatusImpl(Long.toString(System.currentTimeMillis()), pGiftCertificateInfo.getAmount(),
                    true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)));
  }

  //-----------------------------------
  /**
   * Credit the amount on the gift certificate without a previous debit.
   *
   * @param pGiftCertificateInfo the GiftCertificateInfo reference which contains all the credit data
   * @return a GiftCertificateStatus object detailing the results of the credit
   */
  public GiftCertificateStatus credit(GiftCertificateInfo pGiftCertificateInfo) {
    return new GiftCertificateStatusImpl(Long.toString(System.currentTimeMillis()), pGiftCertificateInfo.getAmount(),
                    true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)));
  }
}
