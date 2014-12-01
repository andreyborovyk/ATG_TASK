/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2010 Art Technology Group, Inc.
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
package atg.projects.store.payment;

import atg.commerce.order.StoreCredit;


/**
 * The default store credit payment group is extended to contain the amount
 * applied to the order.
 *
 * @author ATG
 * @version $Revision: #3 $
 */
public class StoreStoreCredit extends StoreCredit {
  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/payment/StoreStoreCredit.java#3 $$Change: 635816 $";

  /**
   * Amount applied to order.
   */
  private double mAmountAppliedToOrder;

  /**
   * Default constructor.
   */
  public StoreStoreCredit() {
  }

  /**
   * Returns the amountAppliedToOrder property.
   * @return amount that applied to order
   */
  public double getAmountAppliedToOrder() {
    return mAmountAppliedToOrder;
  }

  /**
   * Sets the amountAppliedToOrder property.
   * @param pAmountAppliedToOrder amount that applied to order
   */
  public void setAmountAppliedToOrder(double pAmountAppliedToOrder) {
    mAmountAppliedToOrder = pAmountAppliedToOrder;
  }
}
