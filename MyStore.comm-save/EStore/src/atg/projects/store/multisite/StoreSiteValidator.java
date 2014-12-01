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

package atg.projects.store.multisite;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;

import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;
import atg.service.collections.validator.CollectionObjectValidator;

/**
 * This validator validates that a store item exists in the current site.
 *
 * @author ATG
 * @version $Revision:
 */
public class StoreSiteValidator extends GenericService implements CollectionObjectValidator {

  /**
   * Class version string.
   */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/multisite/StoreSiteValidator.java#3 $$Change: 635816 $";


  /**
   * SiteIdPropertyName
   */
  private String mSiteIdPropertyName = "siteId";

  /**
   * @return the SiteIdPropertyName
   */
  public String getSiteIdPropertyName() {
    return mSiteIdPropertyName;
  }

  /**
   * @param pSiteIdPropertyName the SiteIdPropertyName to set
   */
  public void setSiteIdPropertyName(String pSiteIdPropertyName) {
    mSiteIdPropertyName = pSiteIdPropertyName;
  }

  /**
   * This method validates that a store item exists in the current site.
   *
   * @param pObject - Object to validate
   * @return true if the store item exists in the current site, false - otherwise
   */
  public boolean validateObject(Object pObject) {
    if (!(pObject instanceof RepositoryItem) ) {
      return false;
    }

    String siteId = SiteContextManager.getCurrentSiteId();

    RepositoryItem store = (RepositoryItem) pObject;

    boolean valid = false;

    if (!StringUtils.isEmpty(siteId) &&
        siteId.equals(store.getPropertyValue(getSiteIdPropertyName())) ) {
       valid = true;
    }

    if (isLoggingDebug()) {
      logDebug("Store " + store + " in site = " + valid);
    }

    return valid;
  }
}
