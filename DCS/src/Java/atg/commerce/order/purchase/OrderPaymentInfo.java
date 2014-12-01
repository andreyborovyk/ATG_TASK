/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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

import atg.commerce.order.*;

/**
 * This is a CommerceIdentifierPaymentInfo whose CommerceIdentifier is an Order.
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/OrderPaymentInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class OrderPaymentInfo extends CommerceIdentifierPaymentInfo
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/OrderPaymentInfo.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  public static final String AMOUNT_TYPE = RelationshipTypes.ORDERAMOUNT_STR;
  public static final String AMOUNTREMAINING_TYPE = RelationshipTypes.ORDERAMOUNTREMAINING_STR;
  
  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Creates a new <code>OrderPaymentInfo</code> instance.
   *
   */
  public OrderPaymentInfo () {}

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  /**
   * <code>getAmountType</code> is used to return the amount relationship type String
   * based on the CommerceIdentifier being the Order.
   *
   * @return a <code>String</code> value
   */
  public String getAmountType () {
    return AMOUNT_TYPE;
  }

  /**
   * <code>getAmountRemainingType</code> is used to return the amount remaining relationship
   * type String based on the CommerceIdentifier being the Order.
   *
   * @return a <code>String</code> value
   */
  public String getAmountRemainingType () {
    return AMOUNTREMAINING_TYPE;
  }

}   // end of class
