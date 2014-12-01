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

package atg.projects.store.scenario.action;

import atg.commerce.scenario.FillRelatedItemsSlotActionConfiguration;
import atg.multisite.SiteGroupManager;

/**
 * EStore implementation of Nucleus component to contain all necessary properties for fillRelatedItemsSlotAction scenario action.
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/scenario/action/StoreFillRelatedItemsSlotActionConfiguration.java#3 $$Change: 635816 $ 
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class StoreFillRelatedItemsSlotActionConfiguration extends FillRelatedItemsSlotActionConfiguration {
  /**
   * Class version
   */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/scenario/action/StoreFillRelatedItemsSlotActionConfiguration.java#3 $$Change: 635816 $";

  private String mShareableTypeId;
  private String mSitesPropertyName;
  private SiteGroupManager mSiteGroupManager;

  /**
   * This property contains a shareable ID to be used when determining sharing sites.
   * @return current shareable type ID
   */
  public String getShareableTypeId() {
    return mShareableTypeId;
  }

  public void setShareableTypeId(String pShareableTypeId) {
    mShareableTypeId = pShareableTypeId;
  }

  /**
   * This property contains a product's property name with reference to its sites.
   * @return 'siteIds' property name
   */
  public String getSitesPropertyName() {
    return mSitesPropertyName;
  }

  public void setSitesPropertyName(String pSitesPropertyName) {
    mSitesPropertyName = pSitesPropertyName;
  }

  /**
   * This property contains a reference to {@link SiteGroupManager} to be used when determining sharing sites.
   * @return current SiteGroupManager instance
   */
  public SiteGroupManager getSiteGroupManager() {
    return mSiteGroupManager;
  }

  public void setSiteGroupManager(SiteGroupManager pSiteGroupManager) {
    mSiteGroupManager = pSiteGroupManager;
  }
}
