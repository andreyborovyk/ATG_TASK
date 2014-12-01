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

package atg.projects.store.profile;

import java.util.HashMap;
import atg.nucleus.GenericService;

/**
 * A session scoped bean containing only a map property.
 *
 * @author ATG
 * @version $revision 1.1$
 */
public class SessionBean extends GenericService {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/profile/SessionBean.java#2 $$Change: 651448 $";

  public static final String SKU_ID_TO_GIFTLIST_PROPERTY_NAME = "skuIdToAdd";
  
  public static final String COMMERCE_ITEM_ID_PROPERTY_NAME = "commerceItemId";
  
  public static final String PRODUCT_ID_TO_GIFTLIST_PROPERTY_NAME = "productIdToAdd";
  
  public static final String QUANTITY_TO_ADD_TO_GIFTLIST_PROPERTY_NAME = "quantityToAdd";
  
  public static final String GIFTLIST_ID_PROPERTY_NAME = "giftListIdToAdd";
  
  public static final String REDIRECT_AFTER_LOGIN_URL_PROPERTY_NAME = "redirectAfterSuccessLoginURL";
  
  /**
   * Values map. 
   */
  private HashMap mValues;
    
  /**
   * A map containing miscellaneous session values.
   * @return the values.
   */
  public HashMap getValues() {
    if (mValues == null) {
      mValues = new HashMap();
    }
    return mValues;
  }

  private String mSkuIdToAdd;

  /**
   * <i>skuIdToAdd</i> property contains SKU id to be added into shopping cart.
   * This property added in order to set two form handlers' property from a single HTML element.
   */
  public String getSkuIdToAdd()
  {
    return mSkuIdToAdd;
  }
  
  /**
   * <i>skuIdToAdd</i> property in form of String array.
   */
  public String[] getSkuIdToAddArray()
  {
    return mSkuIdToAdd == null ? null : new String[] {mSkuIdToAdd};
  }

  public void setSkuIdToAdd(String pSkuIdToAdd)
  {
    mSkuIdToAdd = pSkuIdToAdd;
  }
  
  private String[] mSearchSiteIds;

  /**
   * This property contains site IDs using by the user for search. This property should be updated each time the user runs new ATG search.
   * @return site IDs of current search process.
   */
  public String[] getSearchSiteIds()
  {
    if (mSearchSiteIds == null)
    {
      mSearchSiteIds = new String[0];
    }
    return mSearchSiteIds;
  }

  public void setSearchSiteIds(String[] pSearchSiteIds)
  {
    mSearchSiteIds = pSearchSiteIds;
  }
}
