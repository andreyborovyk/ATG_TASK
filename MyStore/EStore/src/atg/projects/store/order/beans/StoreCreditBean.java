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
package atg.projects.store.order.beans;


/**
 * This bean will be used to display availableAmounts for store credit
 * to users.
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/beans/StoreCreditBean.java#3 $
 */
public class StoreCreditBean {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/beans/StoreCreditBean.java#3 $$Change: 635816 $";

  /**
   * Id.
   */
  private String mId;

  /**
   * Remaining amount.
   */
  private double mAmountRemaining;

  /**
   * Creates a new StoreCreditBean object.
   *
   * @param pId - id
   * @param pAmount - remaining amount
   */
  public StoreCreditBean(String pId, double pAmount) {
    setId(pId);
    setAmountRemaining(pAmount);
  }

  /**
   * @return the Id.
   */
  public String getId() {
    return mId;
  }

  /**
   * @param pId - the Id.
   */
  public void setId(String pId) {
    mId = pId;
  }

  /**
   * @return the remaining amount.
   */
  public double getAmountRemaining() {
    return mAmountRemaining;
  }

  /**
   * @param pAmountRemaining - the remaining amount.
   */
  public void setAmountRemaining(double pAmountRemaining) {
    mAmountRemaining = pAmountRemaining;
  }
}
