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
package atg.projects.store.states;

import atg.commerce.states.OrderStates;


/**
 * This class holds the new Order states.
 *
 * @author ATG
 * @version $Revision: #3 $
 */
public class StoreOrderStates extends OrderStates {
  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/states/StoreOrderStates.java#3 $$Change: 635816 $";

  /**
   * Pending fulfillment constant.
   */
  public static final String PENDING_FULFILLMENT = "pending_fulfillment";

  /**
   * Order acknowledged constant.
   */
  public static final String SAP_ACKNOWLEDGED = "order_acknowledged";

  /**
   * DOCUMENT ME!
   */
  public static final String PENDING_OMS_NOTIFICATION = "pending_oms_notification";

  /**
   * DOCUMENT ME!
   */
  public static final String PENDING_OMS_REMOVE_NOTIFICATION = "pending_oms_remove_notification";

  /**
   * DOCUMENT ME!
   */
  public static final String ACKNOWLEDGED = "acknowledged";
}
