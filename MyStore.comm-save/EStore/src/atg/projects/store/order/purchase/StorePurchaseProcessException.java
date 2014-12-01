/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2010 Art Technology Group, Inc.
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

package atg.projects.store.order.purchase;

import atg.commerce.CommerceException;

/**
 * This exception indicates that an error occured while performing checkout process
 *
 * @author ATG
 * @version $Version$
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 **/

public class StorePurchaseProcessException extends CommerceException {

  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/purchase/StorePurchaseProcessException.java#3 $$Change: 635816 $";

  //-------------------------------------
  // Properties
  //-------------------------------------

  protected Object[] mParams;

  /**
   * Set the Params property.
   * @param pParams a <code>Params to be used while formatting message</code> value
   */
  public void setParams(Object[] pParams) {
    mParams = pParams;
  }

  /**
   * Return the Params property.
   * @return a <code>Params to be used while formatting message</code> value
   */
  public Object[] getParams() {
    return mParams;
  }

  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs a new StorePurchaseProcessException.
   **/
  public StorePurchaseProcessException() {
    super();
  }

  /**
   * Constructs a new StorePurchaseProcessException with the given
   * explanation.
   **/
  public StorePurchaseProcessException(String pMessage) {
    super(pMessage);
  }

  /**
   * Constructs a new StorePurchaseProcessException with the given
   * explanation.
   *
   * @param pParams list of message paramenters to correctly format the message
   **/
  public StorePurchaseProcessException(String pMessage, Object[] pParams) {
    super(pMessage);
    mParams = pParams;
  }

  /**
   * Constructs a new StorePurchaseProcessException with the given
   * explanation.
   * @param pException the initial exception which was the root
   * cause of the problem
   **/
  public StorePurchaseProcessException(String pMessage, Exception pException) {
    super(pMessage, pException);
  }

  /**
   * Constructs a new StorePurchaseProcessException with the given
   * explanation.
   * @param pParams list of message paramenters to correctly format the message
   * @param pException the initial exception which was the root
   * cause of the problem
   **/
  public StorePurchaseProcessException(String pMessage, Exception pException, Object[] pParams) {
    super(pMessage, pException);
    mParams = pParams;
  }

}