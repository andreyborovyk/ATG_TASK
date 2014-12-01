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

package atg.service.email;

import javax.mail.Address;
import javax.mail.SendFailedException;

/**
 * Indicates that an error occurred while sending email.
 *
 * @author Bill Rooney
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/service/email/EmailInvalidSenderException.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class EmailInvalidSenderException extends EmailException {
  /** Class version string */
  public static String CLASS_VERSION =
    "$Id: //product/DAS/version/10.0.3/Java/atg/service/email/EmailInvalidSenderException.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Member variables
  //-------------------------------------

  /** The root exception that caused this exception **/
  //Throwable mRootCause;
  
  Throwable mRootCause;

  String mInvalidSenderAddress;

  //-------------------------------------
  // Constructors
  //-------------------------------------

  public EmailInvalidSenderException () {
    super ();
  }
  public EmailInvalidSenderException (String s) {
    super (s);
  }
  public EmailInvalidSenderException (String s, Throwable e) {
    super (s);
    mRootCause = e;
  }
  public EmailInvalidSenderException (String s, Throwable e, String pInvalidSenderAddress) {
    super (s);
    mRootCause = e;
    mInvalidSenderAddress = pInvalidSenderAddress;
  }

  //-------------------------------------
  // Methods
  //-------------------------------------

  public Address[] getInvalidAddresses() {
    if (mRootCause instanceof SendFailedException)
      return ((SendFailedException)(mRootCause)).getInvalidAddresses();
    else
      return null;
  }

  public Address[] getValidSentAddresses() {
    if (mRootCause instanceof SendFailedException)
      return ((SendFailedException)(mRootCause)).getValidSentAddresses();
    else
      return null;
  }

  public Address[] getValidUnsentAddresses() {
    if (mRootCause instanceof SendFailedException)
      return ((SendFailedException)(mRootCause)).getValidUnsentAddresses();
    else
      return null;
  }

  public void setInvalidSenderAddress(String pInvalidSenderAddress) {
    mInvalidSenderAddress = pInvalidSenderAddress;
  }

  public String getInvalidSenderAddress() {
    return mInvalidSenderAddress;
  }

  //-------------------------------------
  /**
   * Returns the root exception that caused this exception.
   **/
  public Throwable getRootCause() {
    return mRootCause;
  }

}
