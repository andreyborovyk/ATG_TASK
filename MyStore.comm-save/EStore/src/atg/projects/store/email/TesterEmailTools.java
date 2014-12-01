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
package atg.projects.store.email;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import atg.core.util.StringUtils;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.userprofiling.Profile;
import atg.userprofiling.email.TemplateEmailInfoImpl;

/**
 * Utility methods for e-mail temapltes testing.
 * 
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/email/TesterEmailTools.java#3 $$Change: 635816 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class TesterEmailTools extends StoreEmailTools {

  /** Class version string */
  public static String CLASS_VERSION =
    "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/email/TesterEmailTools.java#3 $$Change: 635816 $";

  //-------------------------------------
  // Constants
  // -------------------------------------

  public static final String EMAIL_PARAMETER = "email";
  public static final String ORDER_PARAMETER = "order";
  public static final String SHIPPING_GROUP_PARAMETER = "shippingGroup";
  public static final String NEWPASSWORD_PARAMETER = "newpassword";
  public static final String SKU_ID_PARAMETER = "skuId";
  public static final String LOCALE_PARAMETER = "locale";
  public static final String PROFILE_PARAMETER = "profile";
  public static final String USER_TYPE = "user";
  public static final String RECIPIENT_NAME_PARAMETER = "recipientName";
  public static final String FIRST_NAME_PROP = "firstName";


  /* (non-Javadoc)
   * @see atg.projects.store.catalog.StoreEmailTools#prepareEmailInfoParams(atg.userprofiling.Profile, atg.userprofiling.email.TemplateEmailInfoImpl, java.util.Map)
   */
  public Map prepareEmailInfoParams(Profile pProfile, TemplateEmailInfoImpl pEmailInfo, Map pEmailParams) {
    Map preparedParams = super.prepareEmailInfoParams(pProfile, pEmailInfo, pEmailParams);
    
    // add "new password"
    preparedParams.put(NEWPASSWORD_PARAMETER, pEmailParams.get(NEWPASSWORD_PARAMETER));
    
    // add "shipping group"
    preparedParams.put(SHIPPING_GROUP_PARAMETER, pEmailParams.get(SHIPPING_GROUP_PARAMETER));
    
    // add "order"
    preparedParams.put(ORDER_PARAMETER, pEmailParams.get(ORDER_PARAMETER));
    
    // add "sku Id"
    preparedParams.put(SKU_ID_PARAMETER, pEmailParams.get(SKU_ID_PARAMETER));
    
    // add "locale" 
    preparedParams.put(LOCALE_PARAMETER, pEmailParams.get(LOCALE_PARAMETER));
    
    RepositoryItem profileDataSource = (RepositoryItem) preparedParams.get(PROFILE_PARAMETER);

    // try to set Profile's first name
    if(profileDataSource!=null) {
      try {
        MutableRepositoryItem profile = getProfileTools().getProfileItem(profileDataSource.getRepositoryId());
        profile.setPropertyValue(FIRST_NAME_PROP, pEmailParams.get(RECIPIENT_NAME_PARAMETER));
      } catch (RepositoryException e) {
        e.printStackTrace();
      }
    } else {
      getProfileTools().createNewUser(USER_TYPE, pProfile);
      pProfile.setPropertyValue(FIRST_NAME_PROP, pEmailParams.get(RECIPIENT_NAME_PARAMETER));
      preparedParams.put(PROFILE_PARAMETER, pProfile.getDataSource());
    }

    return preparedParams;
  }

  /* (non-Javadoc)
   * @see atg.projects.store.catalog.StoreEmailTools#addRecipients(atg.userprofiling.Profile, atg.userprofiling.email.TemplateEmailInfoImpl)
   */
  @Override
  protected List addRecipients(Profile pProfile, TemplateEmailInfoImpl pEmailInfo) {
    List recipients = new ArrayList();
    
    if(pProfile != null && pProfile.getDataSource() != null) {
      // we don't want to use profile's email here
      String originalEmail = (String)pProfile.getPropertyValue(EMAIL_PARAMETER);
      pProfile.setPropertyValue(EMAIL_PARAMETER, pEmailInfo.getMessageTo());
      recipients.add(pProfile);
      
      // add null check to prevent NPE since
      // profile.email is required property and 
      // we can't set it to null
      if(!StringUtils.isEmpty(originalEmail)) {
        pProfile.setPropertyValue(EMAIL_PARAMETER, originalEmail);
      }
      
    } else {
      recipients = super.addRecipients(pProfile, pEmailInfo);
    }
      
    return recipients;
  }
  
  
}
