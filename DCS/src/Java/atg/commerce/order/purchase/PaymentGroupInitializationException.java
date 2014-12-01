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

package atg.commerce.order.purchase;

import atg.commerce.CommerceException;

/**
 * This class represents an exception that is thrown when PaymentGroup
 * initialization fails in a PaymentGroupInitializer.
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/PaymentGroupInitializationException.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class PaymentGroupInitializationException extends CommerceException
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/PaymentGroupInitializationException.java#2 $$Change: 651448 $";

  //-------------------------------------
  /**
   * Constructs a new PaymentGroupInitializationException
   */
  public PaymentGroupInitializationException() {
    super();
  }

  /**
   * Constructs a new PaymentGroupInitializationException with the given 
   * explanation.
   *
   * @param pStr a <code>String</code> value
   */
  public PaymentGroupInitializationException(String pStr) {
    super(pStr);
  }

  /**
   * Constructs a new PaymentGroupInitializationException.
   * @param pSourceException the initial exception which was the root
   * cause of the problem
   **/
  public PaymentGroupInitializationException(Throwable pSourceException) {
    super(pSourceException);
  }

  /**
   * Constructs a new PaymentGroupInitializationException with the given 
   * explanation.
   * @param pStr an explanation of the exception
   * @param pSourceException the initial exception which was the root
   * cause of the problem
   **/
  public PaymentGroupInitializationException(String pStr, Throwable pSourceException) {
    super(pStr, pSourceException);
  }
} // end of class
