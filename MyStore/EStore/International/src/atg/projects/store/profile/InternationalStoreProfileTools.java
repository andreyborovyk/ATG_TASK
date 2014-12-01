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

package atg.projects.store.profile;

import java.util.Locale;
import java.util.ResourceBundle;

import atg.core.i18n.LayeredResourceBundle;
import atg.core.i18n.LocaleUtils;
import atg.core.util.StringUtils;
import atg.repository.RepositoryItem;

/**
 * This class implements internationalization support for ProfileTools component.
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/src/atg/projects/store/profile/InternationalStoreProfileTools.java#3 $$Change: 635816 $ 
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class InternationalStoreProfileTools extends StoreProfileTools {
  /**
   * Class version
   */
  public static final String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/src/atg/projects/store/profile/InternationalStoreProfileTools.java#3 $$Change: 635816 $";
  
  /**
   * Resource bundle name with profile-related resources.
   */
  protected static final String PROFILE_RESOURCE_BUNDLE_NAME = "atg.commerce.profile.Resources";
  /**
   * Resource name for default address name.
   */
  protected static final String ADDRESS_NICKNAME_PREFIX = "addressNicknamePrefix";

  @Override
  public String getUniqueShippingAddressNickname(Object pAddress, RepositoryItem pProfile, String pNewNickname) {
    if (StringUtils.isBlank(pNewNickname)) { // Do we actually need to do anything?
      // Always get resource bundles with Locale! This is essential for internationalization.
      Object localePropertyValue = pProfile.getPropertyValue(getPropertyManager().getLocalePropertyName());
      Locale currentUserLocale = LocaleUtils.getCachedLocale(localePropertyValue == null ?
          Locale.getDefault().toString() : localePropertyValue.toString());
      // We will construct address name from this draft. 
      ResourceBundle profileResources = LayeredResourceBundle.getBundle(PROFILE_RESOURCE_BUNDLE_NAME, currentUserLocale);
      pNewNickname = profileResources.getString(ADDRESS_NICKNAME_PREFIX);
    }
    return super.getUniqueShippingAddressNickname(pAddress, pProfile, pNewNickname);
  }
}
