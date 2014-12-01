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
 *</ATGCOPYRIGHT>
 */

package atg.commerce.payment;

import atg.payment.avs.*;

/**
 * This class is a dummy address verification processor. It purpose is to be a placeholder
 * for a real address verification processor. The implementations of all the methods in this
 * class construct a new AddressVerificationStatus object with dummy success data and return
 * it to the caller.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/DummyAddressVerificationProcessor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.payment.avs.AddressVerificationProcessor
 */
public class DummyAddressVerificationProcessor implements AddressVerificationProcessor
{
  //-----------------------------------
  /* Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/DummyAddressVerificationProcessor.java#2 $$Change: 651448 $";

  //-----------------------------------
  public DummyAddressVerificationProcessor() {
  }
  
  //-----------------------------------
  /**
   * Perform address verification on the information specified in AddressVerificationInfo
   * @param pAvsInfo the AvsInfo reference which contains all the address values
   * @return a AvsStatus object detailing the results of the verification
   */
  public AddressVerificationStatus verifyAddress(AddressVerificationInfo pAvsInfo)
  {
    return new AddressVerificationStatusImpl(Long.toString(System.currentTimeMillis()), true, "", new java.util.Date());
  }
}
