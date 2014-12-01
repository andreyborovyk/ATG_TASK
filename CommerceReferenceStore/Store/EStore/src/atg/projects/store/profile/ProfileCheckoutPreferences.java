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
package atg.projects.store.profile;

import atg.nucleus.GenericService;
import atg.projects.store.profile.StoreProfileTools;
import atg.repository.RepositoryItem;


/**
 * This class is used as a session-scoped component that will help to
 * determine user's current checkout preferences on JSP pages like default
 * shipping address nickname and default credit card address 
 * nickname.
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/profile/ProfileCheckoutPreferences.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ProfileCheckoutPreferences extends GenericService{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/profile/ProfileCheckoutPreferences.java#2 $$Change: 651448 $";
  
  //---------------------------------------------------------------------------
  // property: Profile
  //---------------------------------------------------------------------------
  RepositoryItem mProfile;

  /**
   * Set the Profile property.
   * @param pProfile a <code>RepositoryItem</code> value
   */
  public void setProfile(RepositoryItem pProfile) {
    mProfile = pProfile;
  }

  /**
   * Return the Profile property.
   * @return a <code>RepositoryItem</code> value
   */
  public RepositoryItem getProfile() {
    return mProfile;
  }
  
  //-------------------------------------
  // property: ProfileTools
  //-------------------------------------
  private StoreProfileTools mProfileTools = null;

  /**
   * Returns the profileTools
   */
  public StoreProfileTools getProfileTools() {
    return mProfileTools;
  }

  /**
   * Sets the profileTools
   */
  public void setProfileTools(StoreProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }
  
  /**
   * Determines the default shipping address nickname for the current user profile
   * @return the default shipping address nickname for the current user profile
   */
  public String getDefaultShippingAddressNickname(){
    return getProfileTools().getDefaultShippingAddressNickname(getProfile());
  }

  /**
   * Determines the default credit card nickname for the current user profile
   * @return the default credit card nickname for the current user profile
   */
  public String getDefaultCreditCardNickname(){
    return getProfileTools().getDefaultCreditCardNickname(getProfile());
  }

  /**
   * Determines the default shipping method for the current user profile
   * @return the default shipping method for the current user profile
   */
  public String getDefaultShippingMethod(){
    return getProfileTools().getDefaultShippingMethod(getProfile());
  }

}
