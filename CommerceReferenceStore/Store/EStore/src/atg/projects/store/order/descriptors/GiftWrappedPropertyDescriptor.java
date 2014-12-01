/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2011 Art Technology Group, Inc.
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
package atg.projects.store.order.descriptors;

import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;

import java.util.Iterator;
import java.util.List;


/**
 * Property descriptor that returns a boolean to indicate if Order needs to GiftWrapped.
 *
 * @author ATG
 */
public class GiftWrappedPropertyDescriptor extends RepositoryPropertyDescriptor {
  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/descriptors/GiftWrappedPropertyDescriptor.java#2 $$Change: 651448 $";

  /**
   * Get property value.
   *
   * @param pOrder - order item
   * @param value - value
   *
   * @return property value
   */
  public Object getPropertyValue(RepositoryItemImpl pOrder, Object value) {
    List commerceItems = (List) pOrder.getPropertyValue("commerceItems");

    if ((commerceItems == null) || commerceItems.isEmpty()) {
      return Boolean.FALSE;
    }

    for (Iterator iter = commerceItems.iterator(); iter.hasNext();) {
      RepositoryItem commerceItem = (RepositoryItem) iter.next();
      String type = (String) commerceItem.getPropertyValue("type");

      if ((type != null) && type.equalsIgnoreCase("giftWrapCommerceItem")) {
        return Boolean.TRUE;
      }
    }

    return Boolean.FALSE;
  }
}
