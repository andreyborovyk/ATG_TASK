/*<ATGCOPYRIGHT>
 * Copyright (C) 2010-2011 Art Technology Group, Inc.
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
 * Dynamo is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.projects.store.profile;

import java.util.Map;

import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.ForgotPasswordHandler;

/**
 * This is a Store extension of DPS' ForgotPasswordHandler handler.
 * 
 * @see ForgotPasswordHandler
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/International/src/atg/projects/store/profile/StoreForgotPasswordHadler.java#1 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class StoreForgotPasswordHadler extends ForgotPasswordHandler {

  /**
   * Class version
   */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/International/src/atg/projects/store/profile/StoreForgotPasswordHadler.java#1 $$Change: 651448 $";


  /** The property name of the locale in the profile **/
  String mLocalePropertyName;
  
  //-------------------------------------
  /**
   * Returns the property name of the locale in the    * profile.  
   **/
  public String getLocalePropertyName() {
    return mLocalePropertyName;
  }

  //-------------------------------------
  /**
   * Sets the property name of the locale in the profile.  
   **/
  public void setLocalePropertyName(String pPropertyName) {
    mLocalePropertyName = pPropertyName;
  }
  
  /**
   * Puts locale to the map of template parameters. We do this 
   * because profile's locale could be changed at this stage
   * and doesn't match any of site's languages. 
   */
  protected Map generateNewPasswordTemplateParams(
      DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse,
      RepositoryItem pProfile, String pNewPassword) {
    
    Map params = super.generateNewPasswordTemplateParams(pRequest, pResponse, pProfile, pNewPassword);

    if(params != null) {
      params.put(getLocalePropertyName(), ServletUtil.getUserLocale().toString());
    }
    
    return params;
  }

  
}
