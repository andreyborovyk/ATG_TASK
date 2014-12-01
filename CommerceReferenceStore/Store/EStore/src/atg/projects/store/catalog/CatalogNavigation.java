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
package atg.projects.store.catalog;

/**
 * Helper bean to store user catalog navigation info. Currently has only one property
 * - a top level category that user is currently viewing.
 * The bean is intended to be used with targeters to let specify targerting rules 
 * based on the category currently viewed.
 *  
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/catalog/CatalogNavigation.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class CatalogNavigation {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/catalog/CatalogNavigation.java#2 $$Change: 651448 $";
  
  private String mTopLevelCategory;

  /**
   * Gets the top level category that user is currently viewing.
   * @return the top level category that user is currently viewing.
   */
  public String getTopLevelCategory(){
   
    return mTopLevelCategory;
  }
  
  /**
   * Sets the top level category that user is currently viewing.
   * @param pTopLevelCategory the top level category that user is currently viewing.
   */
  public void setTopLevelCategory(String pTopLevelCategory)
  {
    mTopLevelCategory = pTopLevelCategory;
  }

}
