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
package atg.projects.store.gifts;

import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;


/**
 * User defined property descriptor that returns the amount remaining for a gift list item.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/gifts/GiftlistQuantityRemaining.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class GiftlistQuantityRemaining extends RepositoryPropertyDescriptor {
  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/gifts/GiftlistQuantityRemaining.java#2 $$Change: 651448 $";

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
