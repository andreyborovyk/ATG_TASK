/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

package atg.userprofiling;

import atg.core.util.ResourceUtils;
import atg.nucleus.GenericService;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.sessionsaver.Restoreable;

/**
 * This service is used to preserve a user's active profile in the event of
 * a session failover to another Dynamo server. This service backs up the
 * user id of the profile for the session. The id is only saved if the user
 * is not anonymous. If the profile is transient, then by default is nothing is backup.
 * <P>
 * To backup profile attributes of anonymous visitors, developers should subclass this
 * class. JavaBean properties should be added which expose and record the desired attribute.
 * The <code>sessionRestored</code> method can be overrode to then update the final profile
 * with the desired saved attributes.
 * <P>
 * For example the subclass could have functionality similar to this:
 * <P>
 * <PRE>
 * protected byte [] mShoppingCart;
 *
 * public void setShoppingCart(byte [] pShoppingCart) {
 *   mShoppingCart = pShoppingCart;
 * }
 * public byte [] getShoppingCart() {
 *   if (getProfile() != null)
 *     return (byte [])getProfile().getPropertyValue("shoppingCart");
 *   return null;
 * }
 *
 * public void sessionRestored () {
 *   super.sessionRestored();
 *   if ((mShoppingCart != null) && (mShoppingCart.length > 0) &&
 *       (getProfile() != null)) {
 *     try {
 *       getProfileTools().updateProperty("shoppingCart", mShoppingCart, getProfile());
 *     }
 *     catch (RepositoryException exc) {
 *       if (isLoggingError())
 *         logError(exc);
 *     }
 *   }
 * </PRE>
 *
 * @author Bob Mason
 * @version $Id: //product/DPS/version/10.0.3/Java/atg/userprofiling/ProfileSessionFailService.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public 
class ProfileSessionFailService
extends GenericService
implements Restoreable
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DPS/version/10.0.3/Java/atg/userprofiling/ProfileSessionFailService.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------

  static final String MY_RESOURCE_NAME = "atg.userprofiling.ProfileResources";

  //-------------------------------------
  // Member Variables
  //-------------------------------------

  /** Resource bundle **/
  private static java.util.ResourceBundle sResourceBundle = 
  java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  /** The restored value of the profile id */
  protected String mUidForFailover = null;

  /** The restored value of the profile id */
  protected boolean mSecurityStatusFailoverEnabled = false;

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // property: ProfileTools
  ProfileTools mProfileTools;

  /**
   * Sets property ProfileTools
   **/
  public void setProfileTools(ProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }

  /**
   * Returns property ProfileTools
   **/
  public ProfileTools getProfileTools() {
    return mProfileTools;
  }
  
  //-------------------------------------
  // property: Profile
  Profile mProfile;

  /**
   * Sets property Profile
   **/
  public void setProfile(Profile pProfile) {
    mProfile = pProfile;
  }

  /**
   * Returns property Profile
   **/
  public Profile getProfile() {
    return mProfile;
  }

  //-------------------------------------
  // property: UidForFailover

  /**
   * Stores the uidForFailover parameter in the member variable mUidForFailover
   **/
  public void setUidForFailover(String pUidForFailover) {
    mUidForFailover = pUidForFailover;
  }

  /**
   * Returns the user id of the profile. If the <code>profile</code> property
   * is null, or the profile is anonymous then we return null to indicate
   * that this user does not exist in the persistent repository.
   **/
  public String getUidForFailover() {
    if ((getProfile() != null) && (! getProfile().isTransient())) {
      return getProfile().getRepositoryId();
    }
    return null;
  }

  //-------------------------------------
  // property: dataSource

  /**
   * Set the datasource property on the profile associated with this service.
   * @param dataSource  The profile's datasource.
   */
  public void setDataSource(RepositoryItem dataSource) {
    Profile profile = getProfile();
    if(profile != null) {
      profile.setDataSource(dataSource);
    }
    else {
      if(isLoggingError()) {
        logError("getProfile() returned null.  Unable to set datasource property.");
      }
    }
  }

  /**
   * Gets the datasource property of the profile associated with this service.
   * @return  The profile's datasource or null if the profile is null.
   */
  public RepositoryItem getDataSource() {
    Profile profile = getProfile();
    if(profile != null) {
      return profile.getDataSource();
    }
    else {
      if(isLoggingError()) {
        logError("getProfile() returned null.  Unable to get datasource property.");
      }
      return null;
    }
  }

  //-------------------------------------
  // property: failedOverSecurityStatus
  int mFailedOverSecurityStatus = 3;
  /** 
   * After a profile is failed over, the security status of the re-established 
   * profile will be set to this value.  This allows a security conscious site to
   * force relogin upon failover to plug any potential security holes during failover.
   * Setting this value to -1 will allow the actual value to be failed over, but 
   * you will need to add the value to the list of properties to be failed over.
   */ 
  public void setFailedOverSecurityStatus(int pFailedOverSecurityStatus) {
    mFailedOverSecurityStatus = pFailedOverSecurityStatus;
    /*
     * We have to make sure that configuration setting has enabled security status failover feature
     * for future use.
     */
    if(mFailedOverSecurityStatus==-1)  {
        mSecurityStatusFailoverEnabled = true;
    }
  }
  public int getFailedOverSecurityStatus() {
    if (mFailedOverSecurityStatus != -1)
        return mFailedOverSecurityStatus;

     /*
      * When FailedOverSecurityStatus equals -1, then try to restore the
      * security status from the failover properties.
      */
     if (getProfileTools().isEnableSecurityStatus()) {
       String securityStatusProperty =
         getProfileTools().getPropertyManager().getSecurityStatusPropertyName();
       try {
         Integer securityStatus = (Integer) getProfile().getPropertyValue(securityStatusProperty);
         return securityStatus.intValue();
       }
       catch (IllegalArgumentException e) {
         if (isLoggingError()) {
           logError("Security status property '" + securityStatusProperty + "' not set");
         }
       }
     }

     // Default to anonymous
     return getProfileTools().getPropertyManager().getSecurityStatusAnonymous();
  }
  
  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instanceof ProfileSessionFailService
   */
  public ProfileSessionFailService() {
  }

  /**
   * This restoring method takes the mUidForFailover member variable and attempts
   * to load the profile with the given id. If mUidForFailover is null then this means
   * that the user was anonymous and we do not attempt to load the profile from the database.
   **/
  public void sessionRestored () {
    if (mUidForFailover != null) {
      if (isLoggingDebug())
        logDebug("Using uid '" + mUidForFailover + "' to recover profile.");
      if (getProfileTools().locateUserFromId(mUidForFailover, getProfile())) {
        if (isLoggingInfo()) {
          Object[] args = { mUidForFailover };
          String text = ResourceUtils.getMsgResource("profileRecovered", MY_RESOURCE_NAME, 
						     sResourceBundle, args);
          logInfo(text);
        }

        // Set security status.  If failedOverSecurityStatus is -1, then do nothing, which will
        // allow the actual value to be failed over.  Any other value of failedOverSecurityStatus 
        // will override the actual value upon failover. 
        if (getFailedOverSecurityStatus() != -1) {
          if (getProfileTools().isEnableSecurityStatus()) {
            try {
              getProfileTools().setSecurityStatus(getProfile(), getFailedOverSecurityStatus());
                if (isLoggingDebug()){
                    logDebug("Restoring the value of Profile from Session, Security Status restored = " + getFailedOverSecurityStatus());
                }
            }
            catch (RepositoryException e) {
              if (isLoggingError())
                logError(e); 
            }
          }
        }
        
        mUidForFailover = null;
      }
      else {
        if (isLoggingWarning()) {
          Object[] args = { mUidForFailover };
          String text = ResourceUtils.getMsgResource("noProfileFound", MY_RESOURCE_NAME, 
						     sResourceBundle, args);
          logWarning(text);
        }
      }
    }
    else {
      if (isLoggingDebug())
        logDebug("No uid for session failover recovery, user was anonymous");
    }
    /*
     * Since we have restored the Session and if securityStatus failover is enabled
     * we have also restored the failoverSecurityStatus irrespective of the fact whether
     * profile was anonymous, however now we have to make sure that failover is supported
     * for securityStatus for future aswell
     */
    if(mSecurityStatusFailoverEnabled){
       setFailedOverSecurityStatus(-1);
    }
  }

} // end of class
