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
package atg.projects.store.order;

import java.util.Iterator;
import java.util.List;

import atg.commerce.order.*;

/**
 * Commerce Reference Store shipping group manager.
 *
 * @author ATG
 * @version $Revision: #3 $
  */
public class StoreShippingGroupManager extends ShippingGroupManager {
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/StoreShippingGroupManager.java#3 $$Change: 635816 $";

  //-------------------------------------
  /**
   * Determines if a CommerceItem is a Gift Item or not.
   *
   * @param pItem the CommerceItem to check for.
   * @return true if the CommerceItem is a gift wrap, false otherwise.
   */
  protected boolean isNonGiftItem(CommerceItem pItem)
  {
    return (!(pItem instanceof GiftWrapCommerceItem)); 
  }
  
  /**
   * Returns true if the order has more than one non gift hardgood shipping group
   * with commerce item relationships
   * <p>
   * @return boolean true if the order has more than one non gift hardgood shipping group.
   */
  public boolean isMultipleNonGiftHardgoodShippingGroupsWithRelationships(Order pOrder)
  {
    int count=0;
    List shippingGroups = getNonGiftHardgoodShippingGroups(pOrder);
    Iterator shippingGrouperator = shippingGroups.iterator();
    ShippingGroup sg=null;
    while(shippingGrouperator.hasNext())
    {
      sg = (ShippingGroup) shippingGrouperator.next();
      List rels = sg.getCommerceItemRelationships();
      if(rels != null && rels.size() > 0)
          count++;
    }
    return (count > 1);
  }
}
