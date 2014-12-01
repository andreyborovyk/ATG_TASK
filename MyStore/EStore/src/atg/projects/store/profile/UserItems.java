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

package atg.projects.store.profile;

import java.util.HashMap;
import java.util.Map;

import atg.nucleus.GenericService;

/**
 * This class is used as a global component that will hold
 * a Map of profile ids to list of commerce items. Entries are
 * stored in this Map when the user enters the checkout
 * process as a recognized user, but chooses to proceed
 * through checkout as an anonymous user. In this case,
 * the user is logged out, and the cart is lost. As a result
 * the items need to be stored temporarily in this global
 * component so they can be retrieved and added to the new
 * cart after the session has been expired and the new
 * cart is created.
 *
 * @author ATG
 * @version $Revision: #3 $
 */
public class UserItems extends GenericService {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/profile/UserItems.java#3 $$Change: 635816 $";

  /**
   * Item key constant.
   */
  public static final String ITEM_KEY = "itemKey";

  /**
   * Items map.
   */
  Map mItems;
  
  /**
   * @param pItems - map of items.
   */
  public void setItems(Map pItems) {
    mItems = pItems;
  }

  /**
   * @return Map of profile id's to List of AddCommerceItemInfos.
   */
  public Map getItems() {
    if (mItems == null) {
      mItems = new HashMap();
    }
    return mItems;
  }
  
}
