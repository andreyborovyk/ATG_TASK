/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2010 Art Technology Group, Inc.
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
package atg.projects.store.gifts;

import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;


/**
 * User defined property descriptor that returns the amount remaining for a gift list item.
 *
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/gifts/GiftlistQuantityRemaining.java#3 $$Change: 635816 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class GiftlistQuantityRemaining extends RepositoryPropertyDescriptor {
  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/gifts/GiftlistQuantityRemaining.java#3 $$Change: 635816 $";

  /**
   * Desired quantity constant.
   */
  public static final String QUANTITY_DESIRED = "quantityDesired";

  /**
   * Purchased quantity constant.
   */
  public static final String QUANTITY_PURCHASED = "quantityPurchased";

  /**
   * Returns the amount remaining for a gift list item.
   *
   * @param pGiftlist gift list repository item
   * @param pValue not used
   * @return  amount remaining for a pGiftlist item.
   */
  public Object getPropertyValue(RepositoryItemImpl pGiftlist, Object pValue) {
    Long quantitypurchased = (Long) pGiftlist.getPropertyValue(QUANTITY_PURCHASED);
    Long quantitydesired = (Long) pGiftlist.getPropertyValue(QUANTITY_DESIRED);
    Long quantityremaining;

    if ((quantitydesired != null) && (quantitypurchased != null) &&
        (quantitydesired.longValue() > quantitypurchased.longValue())) {
      quantityremaining = new Long(quantitydesired.longValue() - quantitypurchased.longValue());
    } else {
      quantityremaining = new Long(0);
    }

    return quantityremaining;
  }
}
