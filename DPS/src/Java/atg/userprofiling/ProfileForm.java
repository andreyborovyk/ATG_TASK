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

import atg.beans.DynamicBeanInfo;
import atg.beans.DynamicBeanMap;
import atg.beans.DynamicBeans;
import atg.beans.DynamicPropertyDescriptor;
import atg.beans.DynamicPropertyMapper;
import atg.beans.PropertyNotFoundException;
import atg.core.util.CaseInsensitiveHashtable;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.droplet.GenericFormHandler;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.ServiceException;
import atg.nucleus.naming.ParameterName;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryImpl;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryPropertyDescriptor;
import atg.security.IdentityManager;
import atg.security.SecurityException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;
import atg.servlet.security.UserLoginManager;

import java.beans.IntrospectionException;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;



/**
 * This class is a base class used to handle actions involving
 * Profiles. This includes actions such as creating a new profile
 * editing an exsisting profile and deleting a profile.
 *
 * @author Andrew Rickard
 * @version $Id: //product/DPS/version/10.0.3/Java/atg/userprofiling/ProfileForm.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ProfileForm 
extends GenericFormHandler
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DPS/version/10.0.3/Java/atg/userprofiling/ProfileForm.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------

  /** Name of the ID form parameter */
  public final static String ID_NAME = "ID";
  /** Name of the confirmation password form parameter */
  public final static String CONFIRMPASSWORD_PARAM = "CONFIRMPASSWORD";
  /** Name of the old password form parameter */
  public final static String OLDPASSWORD_PARAM = "OLDPASSWORD";
  /** This is the type of the profile that is created by the create operation.*/
  public final static String DEFAULT_CREATE_PROFILE_TYPE = "user";
  /** This is the type of Profile that we revert to when the logout operation completes.*/
  public final static String DEFAULT_LOGOUT_PROFILE_TYPE = DEFAULT_CREATE_PROFILE_TYPE;
  /** This is the type of Profile, when performing a login operation.*/
  public final static String DEFAULT_LOGIN_PROFILE_TYPE = DEFAULT_CREATE_PROFILE_TYPE;
  /** The property name of the repository id*/
  protected final static String REPOSITORY_ID_PROPERTY_NAME = "REPOSITORYID";

  /** 
   * Constant to indicate that the state of the form has no errors. 
   * Used in the decision process during the form handling. Returned by
   * the <code>checkFormError</code> method. 
   */
  public final static int STATUS_SUCCESS = 0;

  /** 
   * Constant to indicate that the state of the form errors, but that
   * the form should not perform a redirect.
   * Used in the decision process during the form handling. Returned by
   * the <code>checkFormError</code> method. 
   */
  public final static int STATUS_ERROR_STAY = 1;

  /** 
   * Constant to indicate that the state of the form errors, but that
   * the form should perform a redirect.
   * Used in the decision process during the form handling. Returned by
   * the <code>checkFormError</code> method. 
   */
  public final static int STATUS_ERROR_REDIRECT = 2;

  /**
   * Constant to indicate that the handle method was successful
   */
  public final static Boolean HANDLE_SUCCESS = Boolean.TRUE;

  /**
   * Constant to indicate that the handle method failed
   */
  public final static Boolean HANDLE_FAILURE = Boolean.FALSE;

  /** 
   * Constant for the handleCreate method. This is a local parameter
   * which is set if the user was created from the request successfully.
   */
  public final static String HANDLE_CREATE = "HANDLE_CREATE";

  /** 
   * ParameterName for the String HANDLE_CREATE
   */
  public final static ParameterName HANDLE_CREATE_PARAM = ParameterName.getParameterName(HANDLE_CREATE);

  /** 
   * Constant for the handleLogin method. This is a local parameter
   * which is set if the user was obtained from the request successfully.
   */
  public final static String HANDLE_LOGIN = "HANDLE_LOGIN";

  /** 
   * ParameterName for the String HANDLE_LOGIN
   */
  public final static ParameterName HANDLE_LOGIN_PARAM = ParameterName.getParameterName(HANDLE_LOGIN);
  
  /** 
   * Object to be used to reprosent null property values. We convert property values
   * from java null to NULL_SENTINEL to allow http forms to be submitted with blank 
   * string values to set property values to null.
   */ 
  // using new String is deliberate here as we don't want the intern'ed constant
  // "null" we really do want a new, unique instance
  public final static Object NULL_SENTINEL = new String("null");

  //-------------------------------------
  // ResourceBundle Name
  protected static final String RESOURCE_BUNDLE_NAME = "atg.userprofiling.ProfileResources";
 
  // Resource Message keys

  // Server messages
  protected static final String MSG_INVALID_ADD_PROPERTY               = "invalidAddProperty";
  protected static final String MSG_NO_TYPE_CONVERTER                  = "noTypeConverter";
  protected static final String MSG_WARNING_PROFILE_TOOLS              = "warningProfileTools";

  // User and Server messages
  protected static final String MSG_NO_SUCH_PROFILE_PROPERTY           = "noSuchProfileProperty";
  protected static final String MSG_INVALID_LOGIN                      = "invalidLogin";
  protected static final String MSG_ERR_DELETING_PROFILE               = "errorDeletingProfile";
  protected static final String MSG_MISSING_PROFILE_TOOLS              = "missingProfileTools";
  protected static final String MSG_MISSING_PROFILE                    = "missingProfile";

  // User Messages
  protected static final String MSG_ERR_CREATING_PROFILE               = "errorCreatingProfile";
  protected static final String MSG_USER_ALREADY_EXISTS                = "userAlreadyExists";
  protected static final String MSG_ERR_UPDATING_PROFILE               = "errorUpdatingProfile";
  protected static final String MSG_MISSING_REQUIRED_PROPERTY          = "missingRequiredProperty";
  protected static final String MSG_READ_ONLY_PROFILE_PROPERTY         = "readOnlyProfileProperty";
  protected static final String MSG_TYPE_CONVERSION_ERR                = "typeConversionError";
  protected static final String MSG_ILLEGAL_ARGUMENT                   = "illegalArgument";
  protected static final String MSG_MISSING_LOGIN                      = "missingLogin";
  protected static final String MSG_INVALID_PASSWORD                   = "invalidPassword";
  protected static final String MSG_MISSING_PASSWORD                   = "missingPassword";
  protected static final String MSG_MISSING_OLD_PASSWORD               = "missingOldPassword";
  protected static final String MSG_PASSWORDS_DO_NOT_MATCH             = "passwordsDoNotMatch";
  protected static final String MSG_PASSWORD_SAME_AS_OLD_PASSWORD      = "passwordEqualsOldPassword";
  protected static final String MSG_PERMISSION_DEFINED_PASSWORD_CHANGE = "permissionDefinedPasswordChange";

  // ResourceBundle
  protected static ResourceBundle sResourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
 
  //-------------------------------------

  //-------------------------------------
  // Member Variables
  //-------------------------------------

  //-------------------------------------
  // Properties
  //-------------------------------------
      
  //-------------------------------------
  // property: ExtractDefaultValuesFromProfile
  protected boolean mExtractDefaultValuesFromProfile = true;
    
  /**
   * Sets property ExtractDefaultValuesFromProfile
   * @beaninfo description: flag indicating whether to default values from the user's profile
   **/
  public void setExtractDefaultValuesFromProfile(boolean pExtractDefaultValuesFromProfile) {
    mExtractDefaultValuesFromProfile = pExtractDefaultValuesFromProfile;
  }

  /**
   * Returns property ExtractDefaultValuesFromProfile. If this value is true
   * and the form handler does not have a value for a "values property" then the
   * value is extracted out of the user's profile.
   **/
  public boolean isExtractDefaultValuesFromProfile() {
    return mExtractDefaultValuesFromProfile;
  }  
    
  //--------- Property RepositoryId -----------
  protected String mRepositoryId; 

  /**
   * Sets the property RepositoryId
   * description: the RepositoryId of the Profile to manipulate
   */
  public void setRepositoryId(String id) {
    mRepositoryId = id;
  }
  /**
   * @return The value of the property RepositoryId.
   */
  public String getRepositoryId() {
    return mRepositoryId;
  }

  public Profile getProfile() {
    return null;
  }

  public RepositoryItem getProfileItem() {
    if (getProfile() != null) return getProfile();
    try {
      return getProfileTools().getProfileRepository().getItem(getRepositoryId(),
                                                              getCreateProfileType());
    }
    catch (RepositoryException exc) {
      return null;
    }
  }

  //--------- Property: ProfileTools -----------
  protected ProfileTools mProfileTools;
  /**
   * Sets the property ProfileTools.
   * @beaninfo expert: true
   * description: the ProfileTools used to manipulate the profile
   */
  public void setProfileTools(ProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }
  /**
   * @return The value of the property ProfileTools.
   */
  public ProfileTools getProfileTools() {
    return mProfileTools;
  }
    
  //--------- Property: TransactionDemarcation -----------
  /**
   * Gets a new TransactionDemarcation.
   * @beaninfo hidden: true
   * @return The value of the property TransactionDemarcation.
   */
  protected TransactionDemarcation getTransactionDemarcation() {
    return new TransactionDemarcation();
  }
    
  //--------- Property: CreateProfileType -----------
  protected String mCreateProfileType = DEFAULT_CREATE_PROFILE_TYPE;
  /**
   * Sets the property CreateProfileType.  This is the type of the profile
   * that is created by the create operation.
   * @beaninfo expert: true
   * description: the type of the profile created
   */
  public void setCreateProfileType(String pCreateProfileType) {
    mCreateProfileType = pCreateProfileType;
  }
  /**
   * @return The value of the property CreateProfileType.
   */
  public String getCreateProfileType() {
    return mCreateProfileType;
  }

  //--------- Property: LogoutProfileType -----------
  protected String mLogoutProfileType = DEFAULT_LOGOUT_PROFILE_TYPE;
  /**
   * Sets the property LogoutProfileType.  This is the type of Profile
   * that we revert to when the logout operation completes.
   * @beaninfo expert: true
   * description: the type of the profile reverted to after logout
   */
  public void setLogoutProfileType(String pLogoutProfileType) {
    mLogoutProfileType = pLogoutProfileType;
  }
  /**
   * @return The value of the property LogoutProfileType.
   */
  public String getLogoutProfileType() {
    return mLogoutProfileType;
  }

  //-------------------------------------
  // property: LoginProfileType
  protected String mLoginProfileType = DEFAULT_LOGIN_PROFILE_TYPE;

  /**
   * Sets the property LoginProfileType.  This is the type of Profile
   * that the user should be when performing a login.
   * @beaninfo expert: true
   * description: the type of the profile, when performing a login
   **/
  public void setLoginProfileType(String pLoginProfileType) {
    mLoginProfileType = pLoginProfileType;
  }

  /**
   * @return The value of the property LoginProfileType
   **/
  public String getLoginProfileType() {
    return mLoginProfileType;
  }
  
  //--------- Property: Value -----------
  protected ProfileFormHashtable mValue = new ProfileFormHashtable(this);
  /**
   * @return The value of the property Value.  This is a dictionary
   * that stores the pending values for an operation on the current
   * Profile (e.g. login, update, create).
   * @beaninfo
   * description: dictionary storing the pending values of an update to the
   * current Profile
   */
  public Dictionary getValue() {
    return mValue;
  }

  public Map getValueMap() {
    Map wrapper = new ProfileFormHashtableWrapper(getValue());
    return wrapper;
  }
  
  //--------- Property: ExpireSessionOnLogout -----------
  protected boolean mExpireSessionOnLogout = true;
  /**
   * Sets the property ExpireSessionOnLogout.  If this is true, when
   * the logout operation completes, the current session is expired.
   * The default is true.
   * @beaninfo description: flag indicating whether to expire this session
   * automatically upon logout
   */
  public void setExpireSessionOnLogout(boolean pExpireSessionOnLogout) {
    mExpireSessionOnLogout = pExpireSessionOnLogout;
  }
  /**
   * @return The value of the property ExpireSessionOnLogout.
   */
  public boolean getExpireSessionOnLogout() {
    return mExpireSessionOnLogout;
  }


  //-------------------------------------
  // property: CheckForRequiredParameters
  protected boolean mCheckForRequiredParameters=true;

  /**
   * Sets property CheckForRequiredParameters. If this is true, 
   * then during the create or update operation form exceptions will be
   * added for each form parameter which is required and has a null value. 
   * @beaninfo description: flag indicating whether to treat a missing
   * required parameters as an error
   */
  public void setCheckForRequiredParameters(boolean pCheckForRequiredParameters) {
    mCheckForRequiredParameters = pCheckForRequiredParameters;
  }

  /**
   * Returns property CheckForRequiredParameters
   **/
  public boolean getCheckForRequiredParameters() {
    return mCheckForRequiredParameters;
  }  
    
  //--------- Property: CheckForRequiredProperties -----------
  protected boolean mCheckForRequiredProperties = true;
  /**
   * Sets the property CheckForRequiredProperties.  If this is true, 
   * then during the create operation form exceptions will be
   * added for each property which is required and has a null value. 
   * @beaninfo description: flag indicating whether to treat a missing
   * required property as an error during the create process
   */
  public void setCheckForRequiredProperties(boolean pCheckForRequiredProperties) {
    mCheckForRequiredProperties = pCheckForRequiredProperties;
  }
  /**
   * @return The value of the property CheckForRequiredProperties.
   */
  public boolean getCheckForRequiredProperties() {
    return mCheckForRequiredProperties;
  }
   
  //--------- Property: CheckForRequiredPropertiesAfterUpdate -----------
  protected boolean mCheckForRequiredPropertiesAfterUpdate = false;
  /**
   * Sets the property CheckForRequiredPropertiesAfterUpdate.  If this is true, 
   * then during the update operation form exceptions will be
   * added for each property which is required and has a null value. 
   * @beaninfo description: flag indicating whether to treat a missing
   * required property as an error during the update process
   */
  public void setCheckForRequiredPropertiesAfterUpdate(boolean pCheckForRequiredPropertiesAfterUpdate) {
    mCheckForRequiredPropertiesAfterUpdate = pCheckForRequiredPropertiesAfterUpdate;
  }
  /**
   * @return The value of the property CheckForRequiredPropertiesAfterUpdate.
   */
  public boolean getCheckForRequiredPropertiesAfterUpdate() {
    return mCheckForRequiredPropertiesAfterUpdate;
  }
 
  //------------------------------------------------
  // Navigation URL properties for the various operations
  //------------------------------------------------
    
  //--------- Property: UpdateSuccessURL -----------
  protected String mUpdateSuccessURL;
  /**
   * Sets the property UpdateSuccessURL.
   * @beaninfo description: URL to which to redirect upon a successful update
   */
  public void setUpdateSuccessURL(String pUpdateSuccessURL) {
    mUpdateSuccessURL = pUpdateSuccessURL;
  }
  /**
   * @return The value of the property UpdateSuccessURL.
   */
  public String getUpdateSuccessURL() {
    return mUpdateSuccessURL;
  }
    
  //--------- Property: UpdateErrorURL -----------
  protected String mUpdateErrorURL;
  /**
   * Sets the property UpdateErrorURL.
   * @beaninfo description: URL to which to redirect upon a failed update
   */
  public void setUpdateErrorURL(String pUpdateErrorURL) {
    mUpdateErrorURL = pUpdateErrorURL;
  }
  /**
   * @return The value of the property UpdateErrorURL.
   */
  public String getUpdateErrorURL() {
    return mUpdateErrorURL;
  }
    
  //--------- Property: CreateSuccessURL -----------
  protected String mCreateSuccessURL;
  /**
   * Sets the property CreateSuccessURL.
   * @beaninfo description: URL to which to redirect upon a successful
   * profile creation.
   */
  public void setCreateSuccessURL(String pCreateSuccessURL) {
    mCreateSuccessURL = pCreateSuccessURL;
  }
  /**
   * @return The value of the property CreateSuccessURL.
   */
  public String getCreateSuccessURL() {
    return mCreateSuccessURL;
  }
    
  //--------- Property: CreateErrorURL -----------
  protected String mCreateErrorURL;
  /**
   * Sets the property CreateErrorURL.
   * @beaninfo description: URL to which to redirect upon a failed
   * profile creation.
   */
  public void setCreateErrorURL(String pCreateErrorURL) {
    mCreateErrorURL = pCreateErrorURL;
  }
  /**
   * @return The value of the property CreateErrorURL.
   */
  public String getCreateErrorURL() {
    return mCreateErrorURL;
  }
    
  //--------- Property: ChangePasswordSuccessURL -----------
  protected String mChangePasswordSuccessURL;
  /**
   * Sets the property ChangePasswordSuccessURL.
   * @beaninfo description: URL to which to redirect upon a successful
   * password change.
   */
  public void setChangePasswordSuccessURL(String pChangePasswordSuccessURL) {
    mChangePasswordSuccessURL = pChangePasswordSuccessURL;
  }
  /**
   * @return The value of the property ChangePasswordSuccessURL.
   */
  public String getChangePasswordSuccessURL() {
    return mChangePasswordSuccessURL;
  }
    
  //--------- Property: ChangePasswordErrorURL -----------
  protected String mChangePasswordErrorURL;
  /**
   * Sets the property ChangePasswordErrorURL.
   * @beaninfo description: URL to which to redirect upon a failed
   * password change.
   */
  public void setChangePasswordErrorURL(String pChangePasswordErrorURL) {
    mChangePasswordErrorURL = pChangePasswordErrorURL;
  }
  /**
   * @return The value of the property ChangePasswordErrorURL.
   */
  public String getChangePasswordErrorURL() {
    return mChangePasswordErrorURL;
  }
    
  //--------- Property: LoginSuccessURL -----------
  protected String mLoginSuccessURL;
  /**
   * Sets the property LoginSuccessURL.
   * @beaninfo description: URL to which to redirect upon a successful
   * login.
   */
  public void setLoginSuccessURL(String pLoginSuccessURL) {
    mLoginSuccessURL = pLoginSuccessURL;
  }
  /**
   * @return The value of the property LoginSuccessURL.
   */
  public String getLoginSuccessURL() {
    return mLoginSuccessURL;
  }

  //--------- Property: LoginErrorURL -----------
  protected String mLoginErrorURL;
  /**
   * Sets the property LoginErrorURL.
   * @beaninfo description: URL to which to redirect upon a failed
   * login.
   */
  public void setLoginErrorURL(String pLoginErrorURL) {
    mLoginErrorURL = pLoginErrorURL;
  }
  /**
   * @return The value of the property LoginErrorURL.
   */
  public String getLoginErrorURL() {
    return mLoginErrorURL;
  }
    
  //--------- Property: LogoutSuccessURL -----------
  protected String mLogoutSuccessURL;
  /**
   * Sets the property LogoutSuccessURL.
   * @beaninfo description: URL to which to redirect upon a successful
   * logout.
   */
  public void setLogoutSuccessURL(String pLogoutSuccessURL) {
    mLogoutSuccessURL = pLogoutSuccessURL;
  }
  /**
   * @return The value of the property LogoutSuccessURL.
   */
  public String getLogoutSuccessURL() {
    return mLogoutSuccessURL;
  }
    
  //--------- Property: LogoutErrorURL -----------
  protected String mLogoutErrorURL;
  /**
   * Sets the property LogoutErrorURL.
   * @beaninfo description: URL to which to redirect upon a failed
   * logout.
   */
  public void setLogoutErrorURL(String pLogoutErrorURL) {
    mLogoutErrorURL = pLogoutErrorURL;
  }
  /**
   * @return The value of the property LogoutErrorURL.
   */
  public String getLogoutErrorURL() {
    return mLogoutErrorURL;
  }
    
    
  //--------- Property: DeleteSuccessURL -----------
  protected String mDeleteSuccessURL;
  /**
   * @return The value of property DeleteSuccessURL
   */
  public String getDeleteSuccessURL() {
    return mDeleteSuccessURL;
  }
    
  /**
   * Sets the property DeleteSuccessURL
   */
  public void setDeleteSuccessURL(String url) {
    mDeleteSuccessURL = url;
  }

  //--------- Property: DeleteErrorURL -----------
  protected String mDeleteErrorURL;
  /**
   * @return The value of property DeleteErrorURL
   */
  public String getDeleteErrorURL() {
    return mDeleteErrorURL;
  }
  /**
   * Sets the property DeleteErrorURL
   */
  public void setDeleteErrorURL(String url) {
    mDeleteErrorURL = url;
  }

  //-------------------------------------
  // property: ConfirmPassword
  protected boolean mConfirmPassword = false;
    
  /**
   * Sets property ConfirmPassword
   * @beaninfo description: flag indicating whether or not password must be confirmed
   **/
  public void setConfirmPassword(boolean pConfirmPassword) {
    mConfirmPassword = pConfirmPassword;
  }
    
  /**
   * Returns property ConfirmPassword
   **/
  public boolean isConfirmPassword() {
    return mConfirmPassword;
  }  
    
  //-------------------------------------
  // property: ConfirmOldPassword
  protected boolean mConfirmOldPassword = true;
    
  /**
   * Sets property ConfirmPassword
   * @beaninfo description: flag indicating whether or not password must be confirmed
   **/
  public void setConfirmOldPassword(boolean pConfirmOldPassword) {
    mConfirmOldPassword = pConfirmOldPassword;
  }
    
  /**
   * Returns property ConfirmPassword
   **/
  public boolean isConfirmOldPassword() {
    return mConfirmOldPassword;
  }  

  /** 
   * Property indicating whether this form handler is acting on
   * an LDAP profile.
   **/
  protected boolean mUsingLdapProfile = false;

  //----------------------------------------
  /**
   * Sets the UsingLdapProfile property
   * @param pUsingLdapProfile if true, then we're acting upon an 
   * LDAP-based profile, or a composite profile that uses LDAP as
   * part of its makeup
   * @beaninfo description: flag indicating whether we are operating
   * on a profile who has at least one property residing in an LDAP
   * system. Right now, the only use for this flag is to combine
   * a first name and last name property into a full name property used
   * by LDAP
   **/
  public void setUsingLdapProfile(boolean pUsingLdapProfile) {
    mUsingLdapProfile = pUsingLdapProfile;
  }

  /**
   * Gets the UsingLdapProfile property
   * @return true if we're acting upon an LDAP-based profile, or 
   * a composite profile that uses LDAP as part of its makeup, false
   * otherwise. Default is false
   **/
  public boolean isUsingLdapProfile() {
    return mUsingLdapProfile;
  }
  
  /** 
   * Property indicating whether to compare property values in copyPropertiesOnLogin method
   * 
   **/
  protected boolean mCompareValueInCopyPropertiesOnLogin = true;

  //----------------------------------------
  /**
   * Sets the CompareValueInCopyPropertiesOnLogin property
   * @param pCompareValueInCopyPropertiesOnLogin if true, copyPropertiesOnLogin method
   * will compare property values between anonymous profiles
   * and persistent profiles. If the value is not the same,
   * it will copy the property to persistent profiles.
   * @beaninfo description: flag indicating whether copyPropertiesOnLogin 
   * method will compare property values between anonymous profiles
   * and persistent profiles. If the value is not the same,
   * it will copy the property to persistent profiles. The default value is true.
   **/
  public void setCompareValueInCopyPropertiesOnLogin(boolean pCompareValueInCopyPropertiesOnLogin) {
    mCompareValueInCopyPropertiesOnLogin = pCompareValueInCopyPropertiesOnLogin;
  }

  /**
   * Gets the CompareValueInCopyPropertiesOnLogin property
   * @return true if copyPropertiesOnLogin method
   * will compare property values between anonymous profiles
   * and persistent profiles. If the value is not the same,
   * it will copy the property to persistent profiles.
   * The default value is true.
   **/
  public boolean isCompareValueInCopyPropertiesOnLogin() {
    return mCompareValueInCopyPropertiesOnLogin;
  }

  //-------------------------------------
  // property: PropertiesToCopyOnLogin
  /** properties to copy from anonymous profiles to persistent profiles on login */
  protected String[] mPropertiesToCopyOnLogin;

  /**
   * Set property PropertiesToCopyOnLogin
   *
   * @beaninfo description: properties to copy from anonymous profiles
   * to persistent profiles on login
   * @param pPropertiesToCopyOnLogin new value to set
   **/
  public void setPropertiesToCopyOnLogin(String[] pPropertiesToCopyOnLogin)
  {mPropertiesToCopyOnLogin = pPropertiesToCopyOnLogin;}

  /**
   * Get property PropertiesToCopyOnLogin
   * @return PropertiesToCopyOnLogin
   */
  public String[] getPropertiesToCopyOnLogin()
  {return mPropertiesToCopyOnLogin;}

  //-------------------------------------
  // property: PropertiesToAddOnLogin
  /** Array, Map, and Collection properties to add from anonymous profiles to persistent profiles on login */
  protected String[] mPropertiesToAddOnLogin;

  /**
   * Set property PropertiesToAddOnLogin
   *
   * @beaninfo description: Array, Map, and Collection properties to add from anonymous profiles
   * to persistent profiles on login
   * @param pPropertiesToAddOnLogin new value to set
   **/
  public void setPropertiesToAddOnLogin(String[] pPropertiesToAddOnLogin)
  {mPropertiesToAddOnLogin = pPropertiesToAddOnLogin;}

  /**
   * Get property PropertiesToAddOnLogin
   * @return PropertiesToAddOnLogin
   */
  public String[] getPropertiesToAddOnLogin()
  {return mPropertiesToAddOnLogin;}

  //-------------------------------------
  // property: TrimProperties
  protected String [] mTrimProperties;

  /**
   * Sets property TrimProperties. This names the attributes
   * that should be trimmed before updating in the user's profile.
   * @beaninfo description: The list of attributes that should be 
   * trimmed before updating in the user's profile.
   **/
  public void setTrimProperties(String [] pTrimProperties) {
	mTrimProperties = pTrimProperties;
  }

  /**
   * Returns property TrimProperties
   **/
  public String [] getTrimProperties() {
	return mTrimProperties;
  }    


  //---------- Property: BadPasswordDelay ----------
  /** the number of milliseconds to delay after a bad password is submitted.  Default is one second. **/
  protected long mBadPasswordDelay = 1000;

  /**
   * Set property <code>BadPasswordDelay</code>
   * @param pBadPasswordDelay new value to set
   **/
  public void setBadPasswordDelay(long pBadPasswordDelay)
  {mBadPasswordDelay = pBadPasswordDelay;}

  /**
   * Get property <code>BadPasswordDelay</code>
   *
   * @beaninfo description: the number of milliseconds to delay after a bad password is submitted.  Default is one second.
   * @return <code>BadPasswordDelay</code>
   **/
  public long getBadPasswordDelay()
  {return mBadPasswordDelay;}

  //--------- Property: UserLoginManager -----------
  /**
   * This is the UserLoginManager used throughout the application to
   * manage authentication.  In reality we don't use any of the
   * "real" features of this manager, but instead use its IdentityManager
   * interface implementation -- which is just a proxy for the
   * central IdentityManager for the application.<p>
   *
   * This odd state of affairs is done because virtually everyone
   * has overridden ProfileFormHandler to produce their own variant,
   * and it's common practice to also create a new Nucleus object
   * for the new form handler.  Since this is done, we cannot change
   * the property name or location without breaking almost every
   * application in existance.  So instead we have extended the
   * UserLoginManager's capabilities in place.
   */
  protected UserLoginManager mUserLoginManager;

  /**
   * Sets the property UserLoginManager, used to manage authentication.
   * @beaninfo description: the UserLoginManager to use to manage
   * authentication.
   */
  public void setUserLoginManager(UserLoginManager pUserLoginManager) {
    mUserLoginManager = pUserLoginManager;
  }

  /**
   * Returns the value of the property UserLoginManager, used to manage
   * authentication.
   */
  public UserLoginManager getUserLoginManager() {
    return mUserLoginManager;
  }

  /** A List of EventListeners that care about ProfileSwapEvents **/
  protected List mSwapEventListeners = new ArrayList(1);

  /**
   * Adds a ProfileSwapEventListener to the list of listeners that
   * care about ProfileSwapEvents
   * @beaninfo description: A list of EventListeners that care aboue
   * ProfileSwapEvents
   * @param pListener the ProfileSwapEventListener to add
   **/
  public synchronized void addSwapEventListener
  (ProfileSwapEventListener pListener)
  {
    if(pListener != null)
      mSwapEventListeners.add(pListener);
  }

  /**
   * Removes a ProfileSwapEventListener to the list of listeners that
   * care about ProfileSwapEvents
   * @param pListener the ProfileSwapEventListener to remove
   **/
  public synchronized void removeSwapEventListener
  (ProfileSwapEventListener pListener)
  {
    if(pListener != null)
      mSwapEventListeners.remove(pListener);
  }

  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs a new ProfileForm.  This class is typically constructed
   * as a Dynamo component using a .properties file.  It can be
   * a request scoped or session scoped component.  Use session scope if you
   * have a form that is split across several pages.
   */
  public ProfileForm () {
    // bean constructor
  }   

  /**
   * Tests whether pProperty should be trimmed or not
   * @param pProperty the name of the property to check to be trimmed
   * @return true if pProperty should be trimmed
   */
  protected boolean isTrimProperty(String pProperty) {
      String [] trimProperties = getTrimProperties();
      
      if(trimProperties != null) {
	  int len = trimProperties.length;
	  for(int i = 0 ; i < len ; i++) {
	      String trimProperty = trimProperties[i];
	      
	      if(trimProperty != null) {
		  if(trimProperty.equalsIgnoreCase(pProperty))
		      return true;
	      }
	  }
      }
      return false;
  }

  //-------------------------------------
  /**
   * Returns the transaction manager for the repository that we are using
   */
  protected TransactionManager getTransactionManager() 
  {
    Repository r = getProfileTools().getProfileRepository();
    if (r instanceof RepositoryImpl) 
      return ((RepositoryImpl) r).getTransactionManager();
    return null;
  }

  /**
   * This method ensures that a transaction exists before returning.
   * If there is no transaction, a new one is started and returned.  In
   * this case, you must call commitTransaction when the transaction
   * completes.
   */
  protected Transaction ensureTransaction()
  {
     try {
      TransactionManager tm = getTransactionManager();
      /*
       * If we are not configured with a transaction manager, we just
       * have to skip this.
       */
      if (tm == null) return null;

      Transaction t = tm.getTransaction();
      if (t == null) {
        tm.begin();
        return tm.getTransaction();
      }
      return null;
    }
    catch (NotSupportedException exc) {}
    catch (SystemException exc) {
      if (isLoggingError())
        logError(exc);
    }
    return null;
  }

  /** Commits the current transaction */
  protected void commitTransaction(Transaction pTransaction)
  {
    try {
      pTransaction.commit();
    }
    catch (RollbackException exc) {}
    catch (HeuristicMixedException exc) {}
    catch (HeuristicRollbackException exc) {}
    catch (SystemException exc) {
      if (isLoggingError())
        logError(exc);
    }
  }

  //-------------------------------------
  // GenericService methods
  //-------------------------------------

  /**
   * Start the service - make sure we have ProfileTools
   **/
  public void doStartService()
    throws ServiceException
  {
    // if profileTools is not set, try a default value
    if (getProfileTools() == null) {
      if (isLoggingWarning())
	logWarning(ResourceUtils.getMsgResource(MSG_WARNING_PROFILE_TOOLS,
						    RESOURCE_BUNDLE_NAME,
						    sResourceBundle));
      setProfileTools((ProfileTools)
        resolveName("/atg/userprofiling/ProfileTools"));
    }

    // if profileTools is still null, fail out
    if (getProfileTools() == null) {
	throw new ServiceException(
	  ResourceUtils.getMsgResource(MSG_MISSING_PROFILE_TOOLS,
				       RESOURCE_BUNDLE_NAME,
				       sResourceBundle));
    }

    super.doStartService();
  }

  //-------------------------------------
  // Form Handlers
  //-------------------------------------

  //----------- Submit: Create ----------------
  /**
   * Takes the current set of values set as members of the value property 
   * and, if there were no errors in submitting the form, creates a new
   * profile, sets these values in the new profile and set the RepositoryId
   * property. The new type of profile created is defined by the 
   * createProfileType property.
   * <p>
   * If any errors occur in the process, form errors will be added.
   * If there were no errors encountered while submitting the form, we
   * optionally redirect to the value of the property createSuccessURL.
   * Otherwise, we optionally redirect to createErrorURL.
   */
  public boolean handleCreate(DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    try {
      if (tm != null) td.begin(tm, TransactionDemarcation.REQUIRED);

      int status = checkFormError(getCreateErrorURL(), pRequest, pResponse);
      if (status != STATUS_SUCCESS) return status == STATUS_ERROR_STAY;

      RepositoryItem preCreateDataSource = getCurrentDataSource();
      
      preCreateUser(pRequest, pResponse);

      status = checkFormError(getCreateErrorURL(), pRequest, pResponse);
      if (status != STATUS_SUCCESS) return status == STATUS_ERROR_STAY;
 
      // Create the new Profile
      RepositoryItem newUser = createUser(pRequest,pResponse);
      
      status = checkFormError(getCreateErrorURL(), pRequest, pResponse);
      if (status != STATUS_SUCCESS) return status == STATUS_ERROR_STAY;
 
      if (newUser != null) {
        // Now update RepositoryId to point to this item
        setRepositoryId(newUser.getRepositoryId());	

        //Set something in the request so we know if the
        // user was created successfully
        pRequest.setParameter(HANDLE_CREATE, HANDLE_SUCCESS);
      }
      else{
          pRequest.setParameter(HANDLE_CREATE, HANDLE_FAILURE);
      }

      status = checkFormError(getCreateErrorURL(), pRequest, pResponse);
      if (status != STATUS_SUCCESS) return status == STATUS_ERROR_STAY;

      // Send a ProfileSwapEvent...do this before postCreate so that
      // we can setup scenario stuff...required by ABTest
      sendProfileSwapEvent(ProfileSwapEvent.CREATE,
                           preCreateDataSource,
                           getCurrentDataSource());
      
      postCreateUser(pRequest, pResponse);

      // In case errors occurred
      if ((status = checkFormError(getCreateErrorURL(), pRequest, pResponse)) != STATUS_SUCCESS) 
        return status == STATUS_ERROR_STAY;
      
      // Check to make sure our swap didn't cause any form errors
      if ((status = checkFormError(getLoginErrorURL(), pRequest, pResponse)) != STATUS_SUCCESS) 
        return status == STATUS_ERROR_STAY;
          
      if (!checkFormSuccess(getCreateSuccessURL(), pRequest, pResponse))
        return false;
      return true;
    }
    catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    }
    finally {
      try { if (tm != null) td.end(); }
      catch (TransactionDemarcationException e) { }
    }
  }

  /**
   * Operation called just before the user creation process is started
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void preCreateUser(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
  }

  /**
   * Operation called just after the user creation process is finished
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void postCreateUser(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
  }

  /**
   * This method is called from createUser to create the MutableRepositoryItem
   * to use in the creation of a user.
   * @return a MutableRepositoryItem used when creating the profile
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected MutableRepositoryItem createProfileItem(DynamoHttpServletRequest pRequest,
						    DynamoHttpServletResponse pResponse) 
      throws ServletException, IOException 
  {
      ProfileTools ptools = getProfileTools();
      MutableRepository repository = ptools.getProfileRepository();
      MutableRepositoryItem mutableItem = null;

      try {
	  RepositoryItem currentUser = repository.createItem(getCreateProfileType());
	  
	  if (! (currentUser instanceof MutableRepositoryItem)) {
	    String currentUserId = currentUser.getRepositoryId();
	    mutableItem = repository.getItemForUpdate(currentUserId, getCreateProfileType());
	  } else
	    mutableItem = (MutableRepositoryItem) currentUser;	  
      } catch (RepositoryException e) {
	  addFormException(new DropletException(formatUserMessage(MSG_ERR_CREATING_PROFILE, pRequest), 
                                                e, MSG_ERR_CREATING_PROFILE));
	  if (isLoggingError())
	      logError(e); 
      }
      return mutableItem;
  }

  /**
   * By examing the values submitted in the form, a user is attempted to be
   * created and then added persistently to the Profile Repository. 
   * If any errors occur in the process, form errors will be added.
   * @return the new user profile or null if the user could not be created
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected RepositoryItem createUser(DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
  	  	  	
  	try {
  		ProfileTools ptools = getProfileTools();
  		MutableRepository repository = ptools.getProfileRepository();
  		
  		// Check for any missing required properties
  		if (getCheckForRequiredProperties()) {
  			checkForRequiredProperties(repository, pRequest, pResponse);
  		}
  		
  		// check required submit properties
  		if (getCheckForRequiredParameters())
  			checkForRequiredParameters(pRequest, pResponse);
  		
  		
  		// The PropertyManager keeps tract of the name of well known properties
  		PropertyManager pmgr = ptools.getPropertyManager();
  		
  		
  		// If we still do not have any form errors then continue on with the creation process
  		if (!getFormError()) {
  			String loginPropertyName = pmgr.getLoginPropertyName();
  			String login = getStringValueProperty(loginPropertyName);
  			
  			//Check to trim it
  			if(isTrimProperty(loginPropertyName))
  				login = login.trim();
  			
  			if (userAlreadyExists(login, repository, pRequest, pResponse)) {
  				addFormException(new DropletException(formatUserMessage(MSG_USER_ALREADY_EXISTS,
  						login, 
							pRequest),
							MSG_USER_ALREADY_EXISTS));    	    
  			}
  		}
  		
  		if (!getFormError()) {
  			
  			// Run password through the password checker
  			if (ptools.getPasswordRuleChecker()!=null && ptools.getPasswordRuleChecker().isEnabled()) {
  				
  				String passwordPropertyName = pmgr.getPasswordPropertyName();
  				String password = getStringValueProperty(passwordPropertyName);
  				
  				String loginPropertyName = pmgr.getLoginPropertyName();
  				String login = getStringValueProperty(loginPropertyName);
  				
  				Map map = new Hashtable();
  				map.put(loginPropertyName, login);
  				
  				boolean passedRules = ptools.getPasswordRuleChecker().checkRules(password, map);
  				if (!passedRules) 
  					addFormException(new DropletException(ptools.getPasswordRuleChecker().getLastRuleCheckedDescription()));
  				
  			}  		
  		}

  		if (!getFormError()) {
  				//Get us an Item
  				MutableRepositoryItem mutableItem = createProfileItem(pRequest,pResponse);
  				
  				if(mutableItem != null) {
  					// Update the repository item
  					updateProfileAttributes(mutableItem, getValue(), pRequest, pResponse);
  					if(isUsingLdapProfile())
  						updateLDAPProfileAttributes(mutableItem);
  				}
  				
  				// Check for any missing required properties
  				//if (getCheckForRequiredProperties()) {
  				//checkForRequiredProperties(mutableItem, pRequest, pResponse);
  				//}
  				
  				// if no form errors so make sure we add this person into the persistent store
  				if (!getFormError()) {
  					RepositoryItem dbUser;
  					dbUser = addUser(mutableItem, repository, pRequest, pResponse);
  					
  					if (isLoggingDebug())
  						logDebug("createUser: user created; item=" + dbUser);
  					
  					// return the new user
  					return dbUser;			
  				}
  			}
  		
  	} catch (RepositoryException exc) {
  		addFormException(new DropletException(formatUserMessage(MSG_ERR_CREATING_PROFILE, pRequest), exc, MSG_ERR_CREATING_PROFILE ));
  		if (isLoggingError())
  			logError(exc);
  	}
  	
  	return null;
  }

  /**
   * NOTE: This method has been ressurected for use in 5.0.
   * One can use the <code>checkForRequiredParameters</code> or alternative 
   * <code>checkForRequiredProperties</code> method which takes in a RepositoryItem to validate.
   * @param pProfileRepository the repository which should contain the desired profile template
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception RepositoryException if there was an error while accessing the Profile Repository
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void checkForRequiredProperties(Repository pProfileRepository,
                                            DynamoHttpServletRequest pRequest,
                                            DynamoHttpServletResponse pResponse) 
       throws RepositoryException, ServletException, IOException 
  {    
    RepositoryItemDescriptor rdesc = pProfileRepository.getItemDescriptor(getCreateProfileType());
    DynamicPropertyDescriptor [] pdescs = rdesc.getPropertyDescriptors();
    int length = pdescs.length;
    for (int i = 0; i < length; i++) {
      if (pdescs[i].isRequired()) {
        String key = pdescs[i].getName();
        // Special case ID, since we should not accept that as an input to the form
        if (key.equalsIgnoreCase(ID_NAME))
          continue;
        Object value = getValueProperty(key);
        
        // Check for a missing value
        if (valueIsEmpty(value)) {
          String paramName = pdescs[i].getDisplayName();
          if(paramName == null)
            paramName = key;
          String msg = formatUserMessage(MSG_MISSING_REQUIRED_PROPERTY, paramName, pRequest);
          String propertyPath = generatePropertyPath(paramName);
          addFormException(new DropletFormException(msg, propertyPath, MSG_MISSING_REQUIRED_PROPERTY));
        }
      }
    }   
  }

  /**
   * This method is used to verify that certain request parameters have been submitted in the
   * form. Currently this method only checks to make sure that the <code>confirmpassword</code>
   * form parameter is included in the request if the <code>confirmPassword</code> property is
   * set to true. If any errors occur form exceptions are added.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception RepositoryException if there was an error while accessing the Profile Repository
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void checkForRequiredParameters(DynamoHttpServletRequest pRequest,
                                            DynamoHttpServletResponse pResponse)
       throws RepositoryException, ServletException, IOException 
  {
    // Should we be looking for a password confirmation value
    if ((isConfirmPassword()) &&
        (getStringValueProperty(CONFIRMPASSWORD_PARAM) == null)) {
      String msg = formatUserMessage(MSG_MISSING_REQUIRED_PROPERTY, CONFIRMPASSWORD_PARAM, pRequest);
      String propertyPath = generatePropertyPath(CONFIRMPASSWORD_PARAM);
      addFormException(new DropletFormException(msg, propertyPath, MSG_MISSING_REQUIRED_PROPERTY));
    }
  }
  
  /**
   * Iterates over all the properties in the profile template and if any of them
   * are flagged as required then we check to make sure there is an associated 
   * non-<code>null</code> property value in the RepositoryItem.
   * If any errors occur form exceptions are added.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception RepositoryException if there was an error while accessing the Profile Repository
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void checkForRequiredProperties(RepositoryItem pItem, 
                                            DynamoHttpServletRequest pRequest,
                                            DynamoHttpServletResponse pResponse)
       throws RepositoryException, ServletException, IOException 
  {       
    RepositoryItemDescriptor rdesc = pItem.getItemDescriptor();
    DynamicPropertyDescriptor [] pdescs = rdesc.getPropertyDescriptors();
    int length = pdescs.length;
    for (int i = 0; i < length; i++) {
      if (pdescs[i].isRequired()) {
        String key = pdescs[i].getName();
        // Special case ID, since we should not accept that as an input to the form
        if (key.equalsIgnoreCase(ID_NAME))
          continue;
        Object value = pItem.getPropertyValue(key);
        
        // Check for a missing value
        if (valueIsEmpty(value)) {
          String paramName = pdescs[i].getDisplayName();
          if(paramName == null)
            paramName = key;
          String msg = formatUserMessage(MSG_MISSING_REQUIRED_PROPERTY, paramName, pRequest);
          String propertyPath = generatePropertyPath(paramName);
          addFormException(new DropletFormException(msg, propertyPath, MSG_MISSING_REQUIRED_PROPERTY));
        }
      }
    }       
  }	   

  /**
   * Returns true if the supplied value is empty.
   */
  protected boolean valueIsEmpty(Object pValue) {
    if (pValue == null)
      return true;
    else if (pValue instanceof String) 
      return (((String)pValue).trim().length() == 0);
    else if (pValue instanceof Collection)
      return ((Collection)pValue).isEmpty();
    else if (pValue.getClass().isArray())
      return (Array.getLength(pValue) == 0);
    else
      return false;
  }
  
  /**
   * Returns true if a user already exists with the given login name
   * @param pPossibleLogin the login which a user wants to use
   * @param pProfileRepository the repository to search for the pre-existing user
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception RepositoryException if there was an error while accessing the Profile Repository
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected boolean userAlreadyExists(String pPossibleLogin,
                                      Repository pProfileRepository,
                                      DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse) 
       throws RepositoryException, ServletException, IOException 
  {
    // Since in normal circumstances we can use the global profile tools
    // this implementation will just cheat and use the one configured for
    // this instance. However if someone needed to do some special query
    // inside the Profile Repository they are allowed too...

    // Look for the user to make sure they do not exist
    RepositoryItem user = getProfileTools().getItem(pPossibleLogin, null, getLoginProfileType());

    if (user != null)
      return true;
    else
      return false;
  }

  /**
   * Returns the RepositoryItem which represents the newly added user in the 
   * Profile Repository.
   * @param pUser the potential new user to add to the Profile Respository
   * @param pProfileRepository the repository to add the user to
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception RepositoryException if there was an error while accessing the Profile Repository
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected RepositoryItem addUser(MutableRepositoryItem pUser,
                                   MutableRepository pProfileRepository,
                                   DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse) 
       throws RepositoryException, ServletException, IOException 
  {
    if (pUser.isTransient())
      return pProfileRepository.addItem(pUser);
    else {
      pProfileRepository.updateItem(pUser);
      return pProfileRepository.getItem(pUser.getRepositoryId(), 
                                        getCreateProfileType());
    }
  }

    
  //----------- Submit: Update ----------------
  /**
   * Takes the current set of values set as members of the value property
   * and, if there were no errors in submitting the form, modifies the
   * profile described by the RepositoryId property with these values.
   * <p>
   * If any errors occur in the process, form errors will be added.
   * If there were no errors encountered while submitting the form, we
   * optionally redirect to the value of the property updateSuccessURL.
   * Otherwise, we optionally redirect to updateErrorURL.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleUpdate(DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    try {
      if (tm != null) td.begin(tm, TransactionDemarcation.REQUIRED);

      int status = checkFormError(getUpdateErrorURL(), pRequest, pResponse);
      if (status != STATUS_SUCCESS) return status == STATUS_ERROR_STAY;
                  
      preUpdateUser(pRequest, pResponse);

      status = checkFormError(getUpdateErrorURL(), pRequest, pResponse);
      if (status != STATUS_SUCCESS) return status == STATUS_ERROR_STAY;
      
      // Update the user based on the request and response object
      updateUser(pRequest, pResponse);	
      postUpdateUser(pRequest, pResponse);
                  
      // In case errors occurred during the conversion process
      if ((status = checkFormError(getUpdateErrorURL(), pRequest, pResponse)) != STATUS_SUCCESS) 
        return status == STATUS_ERROR_STAY;
          
      if (!checkFormSuccess(getUpdateSuccessURL(), pRequest, pResponse))
        return false;
      return true;
    }
    catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    }
    finally {
      try { if (tm != null) td.end(); }
      catch (TransactionDemarcationException e) { }
    }
  }

  /**
   * Operation called just before the user's profile is updated
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void preUpdateUser(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
  }

  /**
   * Operation called just after the user's profile is updated
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void postUpdateUser(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
  }

  /**
   * Updates the user based on the current profile id. Values store in the
   * <code>value</code> property container are saved into the user profile.
   * If any errors occur in the process, form errors will be added.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void updateUser(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
       throws ServletException, IOException 
  {	
    try {
      MutableRepository repository = getProfileTools().getProfileRepository();
      MutableRepositoryItem mutableItem = repository.getItemForUpdate(getRepositoryId(), getCreateProfileType());
	    
      // check required submit properties
      if (getCheckForRequiredParameters())
        checkForRequiredParameters(pRequest, pResponse);
      
      if(!getFormError()) {
	  // Update the profile with the current form submission values
	  updateProfileAttributes(mutableItem, getValue(), pRequest, pResponse);
	  if(isUsingLdapProfile())
	    updateLDAPProfileAttributes(mutableItem);
	    
	  // Check for any missing required properties
	  if (getCheckForRequiredPropertiesAfterUpdate()) {
	      checkForRequiredProperties(mutableItem, pRequest, pResponse);
	  }

	  if (!getFormError()) {
	      if (isLoggingDebug())
		  logDebug("updating the item in the repository");
	      // save the update into the Repository
	      repository.updateItem(mutableItem);
	  }
      }
    }
    catch (RepositoryException exc) {
      addFormException(new DropletException(formatUserMessage(MSG_ERR_UPDATING_PROFILE, pRequest), 
                                            exc, MSG_ERR_UPDATING_PROFILE));
      if (isLoggingError())
        logError(exc);
    }

  }
    
  //----------- Submit: Change Password ----------------
  /**
   * A special form handler user to update a user's password profile attribute.
   * This specific handler is available because it allows the form to check
   * that user changing the password knows what the original password value is.
   * One can optionally also ask for a confirmation password value to verify that
   * the user did not mistype the new password entry. The name of the password 
   * profile attribute is defined in the PropertyManager. 
   * <p>
   * If any errors occur in the process, form errors will be added.
   * If there were no errors encountered while submitting the form, we
   * optionally redirect to the value of the property changePasswordSuccessURL.
   * Otherwise, we optionally redirect to changePasswordErrorURL.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleChangePassword(DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {  
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    try {
      if (tm != null) td.begin(tm, TransactionDemarcation.REQUIRED);

      int status = checkFormError(getChangePasswordErrorURL(), pRequest, pResponse);
      if (status != STATUS_SUCCESS) return status == STATUS_ERROR_STAY;
          
      preChangePassword(pRequest, pResponse);
      
      status = checkFormError(getChangePasswordErrorURL(), pRequest, pResponse);
      if (status != STATUS_SUCCESS) return status == STATUS_ERROR_STAY;

      // Change the password
      changePassword(pRequest,pResponse); 
      
      postChangePassword(pRequest, pResponse);
              
      // In case errors occurred during the conversion process
      if ((status = checkFormError(getChangePasswordErrorURL(), pRequest, pResponse)) != STATUS_SUCCESS) 
        return status == STATUS_ERROR_STAY;
          
      if (!checkFormSuccess(getChangePasswordSuccessURL(), pRequest, pResponse))
        return false;
      return true;
    }
    catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    }
    finally {
      try { if (tm != null) td.end(); }
      catch (TransactionDemarcationException e) { }
    }
  }

  /**
   * Operation called just before the user's password is changed
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void preChangePassword(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
  }

  /**
   * Operation called just after the user's password is changed
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void postChangePassword(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
  }

  /**
   * Changes the users password. 
   * If any errors occur in the process, form errors will be added.
   *
   * @return true if the password is changed correcty
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected boolean changePassword(DynamoHttpServletRequest pRequest,
  		DynamoHttpServletResponse pResponse) 
  throws ServletException, IOException 
	{
    /*
     * This method is to be replaced by ProfileTools.changePassword()
     * If any major work needs to be done on this method, please remove this method 
     * and use ProfileTools.changePassword in its place.
     */

  	ProfileTools ptools = getProfileTools();
  	PropertyManager pmgr = ptools.getPropertyManager();
  	String loginPropertyName = pmgr.getLoginPropertyName();
  	String passwordPropertyName = pmgr.getPasswordPropertyName();
  	String lastPasswordUpdatePropertyName = pmgr.getLastPasswordUpdatePropertyName();
  	String generatedPwdPropertyName = pmgr.getGeneratedPasswordPropertyName();
  	
  	try {
  		boolean changePassword = true;
  		String password = getStringValueProperty(passwordPropertyName);
  		String oldPassword = getStringValueProperty(OLDPASSWORD_PARAM);      
  		
        if (oldPassword == null || oldPassword.trim().length() == 0) {
          oldPassword = null;

          if (isConfirmOldPassword()) {
            changePassword = false;
            if (isLoggingDebug())
              logDebug("handleChangePassword: missing old password");
            String msg = formatUserMessage(MSG_MISSING_OLD_PASSWORD, pRequest);
            String propertyPath = generatePropertyPath(passwordPropertyName);
            addFormException(new DropletFormException(msg, propertyPath,
                MSG_MISSING_OLD_PASSWORD));
          }
        }
        
  		if ((password == null) || (password.equals(""))) {
  			changePassword = false;
  			if (isLoggingDebug())
  				logDebug("handleChangePassword: missing password");
  			String msg = formatUserMessage(MSG_MISSING_PASSWORD, pRequest);
  			String propertyPath = generatePropertyPath(passwordPropertyName);
  			addFormException(new DropletFormException(msg, propertyPath, MSG_MISSING_PASSWORD));
  		}
  		
  		if ((changePassword) && (isConfirmPassword())) {
  			String confirmPassword = getStringValueProperty(CONFIRMPASSWORD_PARAM);
  			if ((confirmPassword == null) || (confirmPassword.equals("")) ||
  					(! password.equals(confirmPassword))) {
  				changePassword = false;
  				String msg = formatUserMessage(MSG_PASSWORDS_DO_NOT_MATCH, pRequest);
  				addFormException(new DropletException(msg, MSG_PASSWORDS_DO_NOT_MATCH));
  			}
  		}

  		// make sure that old and new passwords are not equal
  		if (changePassword) {
  			if (oldPassword!=null && password!=null && password.equals(oldPassword)) {
  				changePassword = false;
  				String msg = formatUserMessage(MSG_PASSWORD_SAME_AS_OLD_PASSWORD, pRequest);
  				addFormException(new DropletException(msg, MSG_PASSWORD_SAME_AS_OLD_PASSWORD));
  			}
  		}
  		
  		// Run password through the password checker
  		if (changePassword && ptools.getPasswordRuleChecker()!=null && ptools.getPasswordRuleChecker().isEnabled()) {

  			MutableRepository repository = ptools.getProfileRepository();
  			MutableRepositoryItem mutableItem = repository.getItemForUpdate(getRepositoryId(), getCreateProfileType());
  			
  			String login = null;
  			try {
  				login = (String) DynamicBeans.
					getSubPropertyValue(mutableItem, loginPropertyName);
  			}
  			catch (PropertyNotFoundException exc) {	}

  			Map map = null;
  			if (login!=null) {
  				map = new Hashtable();
  				map.put(loginPropertyName, login);
  			}
  				
  			boolean passedRules = ptools.getPasswordRuleChecker().checkRules(password, map);
  			
  			if (!passedRules) {
  				changePassword = false;
  				
  				addFormException(new DropletException(ptools.getPasswordRuleChecker().getLastRuleCheckedDescription()));
  			}
  		}

  		if (changePassword) {
  			MutableRepository repository = ptools.getProfileRepository();
  			MutableRepositoryItem mutableItem = repository.getItemForUpdate(getRepositoryId(), getCreateProfileType());
  			
  			String login = null;
  			try {
  				login = (String) DynamicBeans.
					getSubPropertyValue(mutableItem, loginPropertyName);
  			}
  			catch (PropertyNotFoundException exc) {
  				String msg = formatUserMessage
					(MSG_NO_SUCH_PROFILE_PROPERTY, loginPropertyName, pRequest);
  				String propertyPath = generatePropertyPath(loginPropertyName);
  				addFormException(new DropletFormException
  						(msg, exc, propertyPath, MSG_NO_SUCH_PROFILE_PROPERTY));
  				if (isLoggingError())
  					logError(exc);
  			}
  			
  			String currentPassword = (String) DynamicBeans.
				getSubPropertyValue(mutableItem, passwordPropertyName);
  			String oldPasswordValue = null;
  			
  			if(isConfirmOldPassword())
  				oldPasswordValue = pmgr.generatePassword(login, oldPassword);
  			else
  				oldPasswordValue = currentPassword;
  			
  			
  			if (currentPassword == null || currentPassword.trim().length() == 0)
  				currentPassword = null;
  			
  			if ((currentPassword == null && oldPasswordValue == null) ||
  					(currentPassword != null && oldPasswordValue != null && 
  							currentPassword.equals(oldPasswordValue))) {

  				DynamicBeans.setSubPropertyValue(mutableItem, 
  						passwordPropertyName, 
							pmgr.generatePassword(login, password));

  				// set the password last update property to now
  				DynamicBeans.setSubPropertyValue(mutableItem, 
  						lastPasswordUpdatePropertyName, 
							Calendar.getInstance().getTime());

  				// flag that this passwords was not generated
  				DynamicBeans.setSubPropertyValue(mutableItem, 
  						generatedPwdPropertyName, 
							Boolean.valueOf(false));
  				
  				// update and set the previous password array property
  				if (ptools.getPreviousNPasswordManager()!=null)
  					ptools.getPreviousNPasswordManager().updatePreviousPasswordsProperty(mutableItem, currentPassword);
  				
  				// remove the passwordexpired flag from session
    			pRequest.getSession().setAttribute("passwordexpired", 
    					Boolean.valueOf(false));  				
  				
  				if (!getFormError()) {
  					if (isLoggingDebug())
  						logDebug("updating the password in the repository");
  					repository.updateItem(mutableItem);
  					
  					return true;
  				}    
  			}
  			else {
  				String msg = formatUserMessage(MSG_PERMISSION_DEFINED_PASSWORD_CHANGE, pRequest);
  				addFormException(new DropletException(msg, MSG_PERMISSION_DEFINED_PASSWORD_CHANGE));
  			}            
  		}       
  	}
  	catch (RepositoryException exc) {
  		String msg = formatUserMessage(MSG_ERR_UPDATING_PROFILE, pRequest);
  		addFormException(new DropletException(msg, exc, MSG_ERR_UPDATING_PROFILE));      
  		if (isLoggingError())
  			logError(exc);
  	}
  	catch (PropertyNotFoundException exc) {
  		String msg = formatUserMessage(MSG_NO_SUCH_PROFILE_PROPERTY, passwordPropertyName, pRequest);
  		String propertyPath = generatePropertyPath(passwordPropertyName);
  		addFormException(new DropletFormException(msg, exc, propertyPath, MSG_NO_SUCH_PROFILE_PROPERTY));
  		if (isLoggingError())
  			logError(exc);
  	}	
  	
  	return false;
	}

  //----------- Submit: Login ----------------
  /**
   * By examing the values submitted in the form, looks for a user
   * with the supplied login and password values. The names of the 
   * login and password profile attributes are defined in the PropertyManager. 
   * <p>
   * If any errors occur in the process, form errors will be added.
   * If there were no errors encountered while submitting the form, we
   * optionally redirect to the value of the property loginSuccessURL.
   * Otherwise, we optionally redirect to loginErrorURL.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleLogin(DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
  	TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    try {
      if (tm != null) td.begin(tm, TransactionDemarcation.REQUIRED);

      int status = checkFormError(getLoginErrorURL(), pRequest, pResponse);
      if (status != STATUS_SUCCESS) return status == STATUS_ERROR_STAY;
      preLoginUser(pRequest, pResponse);

      status = checkFormError(getLoginErrorURL(), pRequest, pResponse);
      if (status != STATUS_SUCCESS) return status == STATUS_ERROR_STAY;

      // Find the user
      RepositoryItem authenticatedUser = findUser(pRequest, pResponse);
      RepositoryItem preLoginDataSource = getCurrentDataSource();
      
      if (authenticatedUser != null) {
        try {
          MutableRepository repository = getProfileTools().getProfileRepository();
          RepositoryItem guest = getProfile();
          // copy anonymous profile traits to the profile object we just loaded from the DB
          copyPropertiesOnLogin(guest, authenticatedUser);
          // add anonymous profile traits to the profile object we just loaded from the DB
          addPropertiesOnLogin(guest, authenticatedUser);
          // Now update RepositoryId to point to this item
          setRepositoryId(authenticatedUser.getRepositoryId());
          // Set something in the request so we know if the
          // user was found
          pRequest.setParameter(HANDLE_LOGIN, HANDLE_SUCCESS);
        }
        catch (RepositoryException exc) {
          if (isLoggingError())
            logError(exc);
          addFormException(new DropletException(formatUserMessage(MSG_ERR_UPDATING_PROFILE, pRequest),
                                                exc, MSG_ERR_UPDATING_PROFILE));
        }
      }
      else{
          pRequest.setParameter(HANDLE_LOGIN, HANDLE_FAILURE);
      }

      // Send a ProfileSwapEvent...do this before postLogin so that
      // we can setup scenario stuff...required by ABTest
      sendProfileSwapEvent(ProfileSwapEvent.LOGIN,
                           preLoginDataSource,
                           getCurrentDataSource());
      
      postLoginUser(pRequest, pResponse);
      
      // In case errors occurred
      if ((status = checkFormError(getLoginErrorURL(), pRequest, pResponse)) != STATUS_SUCCESS) 
        return status == STATUS_ERROR_STAY;
      
      // Check to make sure our swap didn't cause any form errors
      if ((status = checkFormError(getLoginErrorURL(), pRequest, pResponse)) != STATUS_SUCCESS) 
        return status == STATUS_ERROR_STAY;

      if (!checkFormSuccess(getLoginSuccessURL(), pRequest, pResponse))
        return false;
      return true;
    }
    catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    }
    finally {
      try { if (tm != null) td.end(); }
      catch (TransactionDemarcationException e) { }
    }
  }

  //-------------------------------------
  /**
   * Copy the properties from the current session to the specied
   * repository item. The properties copied are those specified by the
   * property <code>propertiesToCopyOnLogin</code>. Subclasses can
   * override this method to provide special handling. 
   */
  protected void copyPropertiesOnLogin(RepositoryItem pGuestUser, RepositoryItem pAuthenticatedUser)
    throws RepositoryException
  {
    if ((pAuthenticatedUser == null) || (pGuestUser == null))
      return;

    int length = 0;
    if (getPropertiesToCopyOnLogin() != null)
      length = getPropertiesToCopyOnLogin().length;
    if (length > 0) {
      // make sure we have a mutable item
      MutableRepository repository = getProfileTools().getProfileRepository();
      MutableRepositoryItem mutableItem = null;
      try {
        mutableItem = (MutableRepositoryItem)pAuthenticatedUser;
      }
      catch (ClassCastException exc) {
        String id = pAuthenticatedUser.getRepositoryId();
        mutableItem = repository.getItemForUpdate(id, getCreateProfileType());
      }

      // copy each property (get + set)

      for (int i=0; i<length; i++) {
        String name = getPropertiesToCopyOnLogin()[i];
	try {
	  Object value = DynamicBeans.getSubPropertyValue(pGuestUser, name);
    if (isCompareValueInCopyPropertiesOnLogin()){
      Object valueInMutableItem = DynamicBeans.getSubPropertyValue(mutableItem, name);
      
      if (value != null && valueInMutableItem != null){
        if (!value.equals(valueInMutableItem)){
          DynamicBeans.setSubPropertyValue(mutableItem, name, value);
        }
      }else{  // value == null || valueInMutableItem == null
        if (valueInMutableItem != value){
          DynamicBeans.setSubPropertyValue(mutableItem, name, value);
        }
      }
      
    }else{
	    DynamicBeans.setSubPropertyValue(mutableItem, name, value);
    }
	}
	catch (PropertyNotFoundException exc) {
	  throw new RepositoryException(exc);	  
	}
      }
      // save the item in the repository
      repository.updateItem(mutableItem);
    }
  }
  
  //-------------------------------------
  /**
   * Add the Array, Map, and Collection properties from the current anonymous user to the 
   * authenticated user. The properties added are those specified by the
   * property <code>propertiesToAddOnLogin</code>. Subclasses can
   * override this method to provide special handling. 
   */
  protected void addPropertiesOnLogin(RepositoryItem pGuestUser, RepositoryItem pAuthenticatedUser)
    throws RepositoryException
  {
    if ((pAuthenticatedUser == null) || (pGuestUser == null))
      return;

    String [] properties = getPropertiesToAddOnLogin();

    if (properties != null) {
      int length = properties.length;

      if (length > 0) {
        // make sure we have a mutable item
        MutableRepository repository = getProfileTools().getProfileRepository();
        MutableRepositoryItem mutableItem = null;
        try {
          mutableItem = (MutableRepositoryItem)pAuthenticatedUser;
        }
        catch (ClassCastException exc) {
          String id = pAuthenticatedUser.getRepositoryId();
          mutableItem = repository.getItemForUpdate(id, getCreateProfileType());
        }

        // add each property (get + combine + set)
        for (int i=0; i<length; i++) {
          String name = properties[i];          
          addProperty(name, pGuestUser, mutableItem);
        }
        // save the item in the repository
        repository.updateItem(mutableItem);
      }
    }
  }

  /**
   * Add the given named property, of type Array, Map, and Collection, from the current anonymous user to the 
   * authenticated user.
   */
  protected void addProperty(String pPropertyName, RepositoryItem pGuestUser, 
                             MutableRepositoryItem pAuthenticatedUser)
       throws RepositoryException
  {
    try {
      getProfileTools().addMultiPropertyValues
        (pPropertyName, pGuestUser, pAuthenticatedUser);
    }
    catch (PropertyNotFoundException exc) {
      throw new RepositoryException(exc);
    }
  }

  
  /**
   * Operation called just before the user is found to be logged in
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void preLoginUser(DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
  }

  /**
   * Operation called just after the user is found to be logged in
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void postLoginUser(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
  }

  /**
   * Checks the supplied login and password and attempts to find
   * a profile which matches the supplied values.    
   * If any errors occur in the process, form errors will be added.
   * @return the user profile or null if the user could not be found
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected RepositoryItem findUser(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    ProfileTools ptools = getProfileTools();
    PropertyManager pmgr = ptools.getPropertyManager();
    String loginPropertyName = pmgr.getLoginPropertyName();
    String login = getStringValueProperty(loginPropertyName);
    String passwordPropertyName = pmgr.getPasswordPropertyName();
    String password = null; 
	
    //Check to trim
    if((login != null) && isTrimProperty(loginPropertyName))
	login = login.trim();

    if (passwordPropertyName != null) {
      password = getStringValueProperty(passwordPropertyName);
      
      //Check to trim
      if((password != null) && isTrimProperty(passwordPropertyName))
	password = password.trim();
    }
	
    if (isLoggingDebug())
      logDebug("findUser: login=" + login);
    
    if (login == null || login.length() == 0) {
      if (isLoggingDebug())
        logDebug("findUser: missing login");
      String msg = formatUserMessage(MSG_MISSING_LOGIN, pRequest);
      String propertyPath = generatePropertyPath(loginPropertyName);
      addFormException(new DropletFormException(msg, propertyPath, MSG_MISSING_LOGIN));
    }
	
    // For now we'll not require a password for login.  
    if (password == null || password.length() == 0)
      password = null;
	
    try {
      if (!getFormError()) {
        if (isLoggingDebug())
          logDebug("findUser: no form errors, looking for user=" + login);
        
        RepositoryItem item = findUser(login, password, ptools.getProfileRepository(), pRequest, pResponse);
        
        if (item != null) {
          if (isLoggingDebug())
            logDebug("findUser: user " + login + " found.");
	  return item;
	}
        else {
          // see if user profile exists; if it doesn't, complain
          if (!ptools.locateUserFromLogin(login, new Profile(), getLoginProfileType())) {
            if (isLoggingDebug())
              logDebug("findUser: invalid login");
            addFormException(new DropletException(formatUserMessage(MSG_INVALID_LOGIN, login, pRequest), 
                                                  MSG_INVALID_LOGIN));
          }
          else {
            if (isLoggingDebug()) 
              logDebug("waiting " + getBadPasswordDelay() + " milliseconds because of bad password");
            try {
              Thread.sleep(getBadPasswordDelay());
            }
            catch (InterruptedException e) {
              if (isLoggingWarning())
                logWarning("bad password wait interrupted");
            }

            if (isLoggingDebug())
              logDebug("findUser: invalid password");
            addFormException(new DropletException(formatUserMessage(MSG_INVALID_PASSWORD, pRequest), 
                                                  MSG_INVALID_PASSWORD));
          }
        }
      }
    }
    catch (RepositoryException exc) {
      addFormException(new DropletException(formatUserMessage(MSG_INVALID_LOGIN, login, pRequest), 
                                            exc,MSG_INVALID_LOGIN));
      if (isLoggingError()) {
        Object [] args = new Object[1];
        if (login != null)
          args[1] = login;
        else
          args[1] = "null";
        logError(ResourceUtils.getMsgResource(MSG_INVALID_LOGIN,
                                              RESOURCE_BUNDLE_NAME,
                                              sResourceBundle,
                                              args), exc);
      }
    }

    return null;
  }

  /**
   * Returns the user profile or null if the user could not be found with the
   * given login and password.
   * @param pLogin the login name for the person
   * @param pPassword the password for the person, optionally null if no password
   * checking should be performed.
   * @param pProfileRepository the repository which should contain the desired profile template
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception RepositoryException if there was an error while accessing the Profile Repository
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected RepositoryItem findUser(String pLogin, 
                                    String pPassword,
                                    Repository pProfileRepository,
                                    DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse) 
       throws RepositoryException, ServletException, IOException 
  {
    IdentityManager identityManager = mUserLoginManager.getIdentityManager(pRequest);
    try {
      // check authentication using the identity manager.  we don't
      // actually use the identity manager to log in the user here
      // for backwards compatibility reasons.  we'll do the real
      // login later in the form handler.
      if (!identityManager.checkAuthenticationByPassword(pLogin, pPassword))
        return null;
      else
        // Authentication succeeded, return the associated profile item.
        return getProfileTools().getItem(pLogin, null, getLoginProfileType());
    }
    catch (SecurityException e) {
      // because the API doesn't support throwing a security exception,
      // we have to pick one that it does support.  RepositoryException
      // is the closest match, indicating a failure in the underlying
      // persistence or query system.
      throw new RepositoryException(e);
    }
  }

  //----------- Submit: Logout ----------------
  /**
   * Changes the type of the current profile to logoutProfileType.
   * If logoutProfileType is set to null, we leave the Profile as null.
   * If expireSessionOnLogout is set to true, we expire the current 
   * session as well when we logout.
   * <p>
   * If there were no errors encountered while submitting the form, we
   * optionally redirect to the value of the property logoutSuccessURL.
   * Otherwise, we optionally redirect to logoutErrorURL.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleLogout(DynamoHttpServletRequest pRequest,				
                              DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    try {
      if (tm != null) td.begin(tm, TransactionDemarcation.REQUIRED);

      int status = checkFormError(getLogoutErrorURL(), pRequest, pResponse);
      if (status != STATUS_SUCCESS) return status == STATUS_ERROR_STAY;

      RepositoryItem preLogoutDataSource = getCurrentDataSource();
      preLogoutUser(pRequest, pResponse);
      
      status = checkFormError(getLogoutErrorURL(), pRequest, pResponse);
      if (status != STATUS_SUCCESS) return status == STATUS_ERROR_STAY;

      // Send a ProfileSwapEvent...do this before postLogout so that
      // we can setup scenario stuff...required by ABTest
      sendProfileSwapEvent(ProfileSwapEvent.LOGOUT,
                           preLogoutDataSource,
                           getCurrentDataSource());

      postLogoutUser(pRequest, pResponse);
      
      String logoutSuccessURL = getLogoutSuccessURL();
      if (logoutSuccessURL == null)
        logoutSuccessURL = pRequest.getRequestURI();
      
      if (logoutSuccessURL != null) {
        pRequest.setParameterDelimiter(DynamoHttpServletRequest.DEFAULT_PARAMETER_DELIMITER);
        pRequest.addQueryParameter(ProfileRequestServlet.LOGOUT_PARAM, "true");
        String logoutURI = pRequest.encodeURL(logoutSuccessURL);
        if (!checkFormSuccess(logoutURI, pRequest, pResponse))
          return false;
      }
      return true;
    }
    catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    }
    finally {
      try { if (tm != null) td.end(); }
      catch (TransactionDemarcationException e) { }
    }
  }

  /**
   * Operation called just before the user is logged out
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void preLogoutUser(DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
  }

  /**
   * Operation called just after the user is logged out
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void postLogoutUser(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
	  // Do all the work for expiring sessions here...
	  if(getExpireSessionOnLogout()) {
		  HttpSession session = pRequest.getSession(false);
		  if (session != null) {
			  if (isLoggingDebug())
				  logDebug("handleLogout: session invalidated");

			  ServletUtil.invalidateSessionNameContext(pRequest, session);
			  ServletUtil.invalidateSession(pRequest, session);

			  //bug 72198, we need to set attribute here in order to disallow session 
			  //back up operation in SessionSaverServlet
			  if ( ServletUtil.isWebLogic() )
				  pRequest.setAttribute(DynamoHttpServletRequest.SESSION_INVALIDATED, Boolean.valueOf(true));

		  }
	  }
	  else {
		  if (getLogoutProfileType() == null) {
			  if (isLoggingDebug())
				  logDebug("handleLogout: logoutProfileType==null, thus setting data source to null");
			  setRepositoryId(null);
		  } else {
			  if (isLoggingDebug())
				  logDebug("handleLogout: creating new profile for user");

			  try {
				  RepositoryItem item = getProfileTools().getProfileRepository().createItem(getLogoutProfileType());
				  setRepositoryId(item.getRepositoryId());

				  // BUG 66561
				  // After logout, the profile is now anonymous.  However
				  // since the session was not expired it still has the
				  // security status of the previously logged in profile.
				  // Therefore explicitly set the security status to ANONYMOUS
				  setSecurityStatusAnonymous();
			  }
			  catch (RepositoryException exc) {
				  if (isLoggingError())
					  logError(exc);
			  }
		  }
	  }
  }

  /**
   * Set the security status of the profile to be anonymous.
   **/
  private void setSecurityStatusAnonymous()
  {
    ProfileTools profileTools = getProfileTools();
    Profile profile = getProfile();
    int securityStatusAnonymous =
      profileTools.getPropertyManager().getSecurityStatusAnonymous();

    try {
      profileTools.setSecurityStatus(profile,
                                     securityStatusAnonymous);
    }
    catch (RepositoryException exc) {
      // not a critical issue if we can't set the security
      // status for this anonymous profile so just log a warning
      if (isLoggingWarning())
        logWarning(exc);
    }
  }

  //----------- Submit: valueDictionary ----------------
  /**
   * Calls setValueDitionaryByParameters to update the property value with
   * the values set in pRequests parameters. preValueDictionaryByParameters
   * is called before setting the values from  pRequest. postValueDictionaryByParameters
   * is called following the set of the values.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public synchronized boolean handleValueDictionaryByParameters(DynamoHttpServletRequest pRequest,
								DynamoHttpServletResponse pResponse) 
      throws ServletException, IOException 
    {
	preValueDictionaryByParameters(pRequest,pResponse);
	setValueDictionaryByParameters(pRequest,pResponse);
	postValueDictionaryByParameters(pRequest,pResponse);
	return true;
    }
  
  /**
   * Operation called just before handleValueDictionaryParameters
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void preValueDictionaryByParameters(DynamoHttpServletRequest pRequest,
						DynamoHttpServletResponse pResponse) 
      throws ServletException, IOException 
  {
  }
  
  /**
   * Operation called just after handleValueDictionaryParameters
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void postValueDictionaryByParameters(DynamoHttpServletRequest pRequest,
					DynamoHttpServletResponse pResponse) 
      throws ServletException, IOException 
  {
  } 

  //----------- Submit: Delete ----------------
  /**
   * Handles the removal of a user from the Profile Repository based on
   * the profile id as defined by the repositoryId property
   * <p>  
   * If any errors occur in the process, form errors will be added.
   * If there were no errors encountered while submitting the form, we
   * optionally redirect to the value of the property deleteSuccessURL.
   * Otherwise, we optionally redirect to deleteErrorURL.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public synchronized boolean handleDelete(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    try {
      if (tm != null) td.begin(tm, TransactionDemarcation.REQUIRED);

      int status = checkFormError(getDeleteErrorURL(), pRequest, pResponse);
      if (status != STATUS_SUCCESS) return status == STATUS_ERROR_STAY;

      RepositoryItem preDeleteDataSource = getCurrentDataSource();
      preDeleteUser(pRequest, pResponse);

      status = checkFormError(getDeleteErrorURL(), pRequest, pResponse);
      if (status != STATUS_SUCCESS) return status == STATUS_ERROR_STAY;

      // Delete user
      removeUser(pRequest, pResponse);

      // Send a ProfileSwapEvent...do this before postDelete so that
      // we can setup scenario stuff...required by ABTest
      sendProfileSwapEvent(ProfileSwapEvent.DELETE,
                           preDeleteDataSource,
                           getCurrentDataSource());

      postDeleteUser(pRequest, pResponse);
          
      // In case errors occurred during the conversion process
      if ((status = checkFormError(getDeleteErrorURL(), pRequest, pResponse)) != STATUS_SUCCESS) 
        return status == STATUS_ERROR_STAY;
      
      if (!checkFormSuccess(getDeleteSuccessURL(), pRequest, pResponse))
        return false;
      return true;
    }
    catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    }
    finally {
      try { if (tm != null) td.end(); }
      catch (TransactionDemarcationException e) { }
    }
  }

  /**
   * Operation called just before the user is removed from the Profile Repository
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void preDeleteUser(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
  }

  /**
   * Operation called just after the user is removed from the Profile Repository
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void postDeleteUser(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
  }

  /**
   * Removes the user with the given profile id as defined by the
   * repositoryId property.
   * If any errors occur in the process, form errors will be added.
   * @return true if the delete was successful 
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected boolean removeUser(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException
  {
    try {
      ProfileTools ptools = getProfileTools();
      MutableRepository repository = ptools.getProfileRepository();
	    
      if (!getFormError()) {
        if (isLoggingDebug())
          logDebug("deleting the item in the repository id="+getRepositoryId());
        repository.removeItem(getRepositoryId(), getCreateProfileType());
	setRepositoryId(null);

        return true;
      }
    }
    catch (RepositoryException exc) {
      Object [] args = new Object[1];
      if (getRepositoryId() != null)
        args[0] = getRepositoryId();
      else
        args[0] = "null";

      addFormException(new DropletException(formatUserMessage(MSG_ERR_DELETING_PROFILE, 
                                                              args, pRequest), 
                                            exc,MSG_ERR_DELETING_PROFILE));
      if (isLoggingError())
        logError(ResourceUtils.getMsgResource(MSG_ERR_DELETING_PROFILE,
                                              RESOURCE_BUNDLE_NAME,
                                              sResourceBundle, args), exc);
    }
	
    return false;
  }

  /**
   * Get the multi-value Property Name to use in a handleAddMulti call.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return the name of the multi-value property to addmulti
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected String getAddMultiPropertyName(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
       throws ServletException, IOException 
  {
    String parameterPointer = pRequest.getParameter(pRequest.getParameter("addMultiProperty"));
    String [] parameterPointerArray = null;
    String propertyNamePtr = null;
    String propertyName = null;
    
    if(parameterPointer != null) {
      parameterPointerArray =  StringUtils.splitStringAtCharacterWithQuoting(parameterPointer,',');
      
      if(parameterPointerArray.length >= 1 )
        propertyNamePtr = parameterPointerArray[0];
      
      if(propertyNamePtr != null)    
        propertyName = pRequest.getParameter(propertyNamePtr);
    }
    return propertyName;
  }

  /**
   * Get the multi-value Property Value to use in a handleAddMulti call.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return the value of the multi-value property to addmulti
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected Object getAddMultiPropertyValue(DynamoHttpServletRequest pRequest,
                                            DynamoHttpServletResponse pResponse)
       throws ServletException, IOException 
  {
    String parameterPointer = pRequest.getParameter(pRequest.getParameter("addMultiProperty"));
    String [] parameterPointerArray =  null;
    String propertyValuePtr = null;
    Object propertyValue = null;
    
    if(parameterPointer != null) {
      parameterPointerArray =  StringUtils.splitStringAtCharacterWithQuoting(parameterPointer,',');
      
      if(parameterPointerArray.length >= 2 )
        propertyValuePtr = parameterPointerArray[1];
      
      if(propertyValuePtr != null)    
        propertyValue = pRequest.getParameter(propertyValuePtr);
    }
    
    return (propertyValue);
  }
    
  /**
   * Handles the updating of multi-value attributes
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public synchronized boolean handleAddMulti(DynamoHttpServletRequest pRequest,
                                             DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {	
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    try {
      if (tm != null) td.begin(tm, TransactionDemarcation.REQUIRED);

      String propertyName  = getAddMultiPropertyName(pRequest,pResponse);
      Object propertyValue = getAddMultiPropertyValue(pRequest,pResponse);
      
      if(isLoggingDebug())
        logDebug("propertyName = " + propertyName + "; propertyValue = " + propertyValue);
                  
      preAddMulti(pRequest,pResponse);
      if((propertyName != null) && (propertyValue != null))
        addMulti(propertyName,propertyValue,pRequest,pResponse);
      postAddMulti(pRequest,pResponse);
    }
    catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    }
    finally {
      try { if (tm != null) td.end(); }
      catch (TransactionDemarcationException e) { }
    }
    return true;
  }
    
  /**
   * Operation called just before addMulti
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void preAddMulti(DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
  }
    
  /**
   * Operation called just after addMulti()
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void postAddMulti(DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
  }
    
  /**
   * Handles the updating of multi-value attributes pPropertyName with pPropertyValue
   * @param pPropertyName The name of the property to append to
   * @param pPropertyValue The value to append to the pPropery 
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void addMulti(String pPropertyName,
                          Object pPropertyValue,
                          DynamoHttpServletRequest pRequest,
                          DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    RepositoryItemDescriptor itemDescriptor = null;
    itemDescriptor = getDescriptor();
    DynamicPropertyDescriptor pdesc = 
      itemDescriptor.getPropertyDescriptor(pPropertyName);
    
    if (pdesc == null) {
      String msg = 
	formatUserMessage
	(MSG_NO_SUCH_PROFILE_PROPERTY, pPropertyName, pRequest);
      String propertyPath = generatePropertyPath(pPropertyName);
      addFormException
	(new DropletFormException
	  (msg, propertyPath, MSG_NO_SUCH_PROFILE_PROPERTY));
    } else {      
      Class pclass = pdesc.getPropertyType();
      
      if((pclass.isArray()) ||
	 (java.util.Collection.class.isAssignableFrom(pclass))) {
	Class type = null;
	if(pclass.isArray())
	  type = pclass.getComponentType();
	else 
	  type = pdesc.getComponentPropertyType();
	
	try {
	  //New Item to Add
	  Object newItem = null;
	  if(pPropertyValue != null)
	    newItem = getValueFromObject(type,null,pPropertyValue); 
	  else 
	    newItem = null;
	  
	  if(newItem == null) {
	    String msg = formatUserMessage(MSG_ILLEGAL_ARGUMENT, pPropertyValue, type, pRequest);
	    String propertyPath = generatePropertyPath(pPropertyName);
	    addFormException(new DropletFormException(msg, null, propertyPath,
						      MSG_ILLEGAL_ARGUMENT));
	    return;
	  }
	  
	  //Get a new value
	  Object newValue = null;
	  if(pclass.isArray()) {
	    Object oldValue = DynamicBeans.getSubPropertyValue(getValue(),pPropertyName); 
	    int len = 0 ;
	    
	    if(oldValue != null) 
	      len = ((Object [])oldValue).length;
	    
	    //Create new Array
	    newValue = Array.newInstance(type,len + 1);
	    
	    if(newValue != null) {
	      //Copy OldValue->newValue
	      for(int i = 0 ; i < len ; i++) {
		Object item1 = ((Object [])oldValue)[i];
		Object item2;
		if(item1 != null)
		  item2 = getValueFromObject(type,null,item1);
		else
		  item2 = null;
		
		try {
		  ((Object [])newValue)[i] = item2;
		} catch(ClassCastException e) {
		  //((Object [])newValue)[i] = null;
		}
	      }
	      
	      //Add the new item
	      try {
		((Object []) newValue)[len] = newItem;
	      } catch(ClassCastException e) {
		//((Object [])newValue)[len] = null;
	      }   
	    }      
	  } else {
	    //Collections
	    if(!pclass.isInterface()) {
	      try {
		newValue = pclass.newInstance();
	      } catch(InstantiationException iae) {
		//Ignore
	      } catch(IllegalAccessException ilae) {
		//Ignore
	      } 
	    } else if(java.util.List.class.isAssignableFrom(pclass)) {
	      newValue = new ArrayList();
	    } else if(java.util.Set.class.isAssignableFrom(pclass)) {
	      newValue = new HashSet();
	    } else {
	      newValue = new ArrayList();
	    }
	    
	    if(newValue != null) {
	      Object oldValue = DynamicBeans.getSubPropertyValue(getValue(),pPropertyName);
	      if(oldValue != null) {
		if(oldValue instanceof Collection) {
		  Iterator it = ((Collection)oldValue).iterator();
		  while(it.hasNext()) {
		    ((Collection)newValue).add
		      (getValueFromObject(type,null,it.next()));
		  }
		}	
		else if(oldValue.getClass().isArray()) {
		  for(int i = 0 ; i < ((Object[])oldValue).length ; i++) {
		    ((Collection)newValue).add(getValueFromObject(type,null,((Object[])oldValue)[i]));
		  }
		}
	      } 
	      
	      try {
		((Collection)newValue).add(newItem);
	      } catch(ClassCastException e) {
		
	      }
	    }
	    
	  }	
	  //Set new Value
	  setValueProperty(pPropertyName,newValue);
	} catch (PropertyNotFoundException exc) {
	  String msg = formatUserMessage(MSG_NO_SUCH_PROFILE_PROPERTY, pPropertyName, pRequest);
	  String propertyPath = generatePropertyPath(pPropertyName);
	  addFormException(new DropletFormException(msg, exc, propertyPath, MSG_NO_SUCH_PROFILE_PROPERTY));
	} catch (IllegalArgumentException exc) {
	  String msg = formatUserMessage(MSG_TYPE_CONVERSION_ERR, pPropertyValue, type, pRequest);
	  String propertyPath = generatePropertyPath(pPropertyName);
	  addFormException(new DropletFormException(msg, exc, propertyPath,
						    MSG_TYPE_CONVERSION_ERR));
	}   
	
	
      }
    }
  }
  
    
  /**
   * This method take a string pStrValue and converts it to an object of type pClass returning 
   * the converted object. Assumes the pClass is an array of objects
   * @param pClass the type of the return value.
   * @param pComponentClass the type of the component.
   * @param pStrValue the string value to convert.
   * @return pStrValue converted to array object of type pClass
   * @exception IllegalArgumentException if couldn't find a property editor for pClass
   */
  protected Object getValueFromArrayString(Class pClass,Class pComponentClass,String pStrValue) 
       throws IllegalArgumentException 
  {
    PropertyEditor peditor = null;
    Object value = null;
    
    peditor = PropertyEditorManager.findEditor(pClass);
    if(peditor != null) {	    
      peditor.setAsText(pStrValue);
      value = peditor.getValue();	    
    } else {      
      //Try building it yourself
      String [] strValues = StringUtils.splitStringAtCharacterWithQuoting(pStrValue,','); 
      if(strValues != null) {
        getValueFromStringArray(pClass,pComponentClass,strValues);
      }
    }
    
    return value;
  }

  /**
   * This method take a string pStrValue and converts it to an object of type pClass returning 
   * the converted object.
   * @param pClass the type of the return value.
   * @param pComponentClass the type of the component
   * @param pStrValue the string value to convert.
   * @return pStrValue converted to object of type pClass
   * @exception IllegalArgumentException if couldn't find a property editor for pClass
   */
  protected Object getValueFromString(Class pClass,Class pComponentClass,String pStrValue) 
       throws IllegalArgumentException 
  {
    PropertyEditor peditor = null;
    Object value = null;
    
    peditor = PropertyEditorManager.findEditor(pClass);
    if(peditor != null) {	    
      peditor.setAsText(pStrValue);
      value = peditor.getValue();	    
    } else {
      if(pClass.isArray())		
        value = getValueFromArrayString(pClass,pComponentClass,pStrValue);
      else if (isLoggingWarning()) {
        Object [] args = { pClass.getName() };
        logWarning(ResourceUtils.getMsgResource(MSG_NO_TYPE_CONVERTER,
                                                RESOURCE_BUNDLE_NAME,
                                                sResourceBundle,args));
      }
    }
    return value;
  }
  
  /**
   * This method take an array of strings pStrValue and converts it to an object 
   * of type pClass returning	 the converted object. Assumes the pClass is an array 
   * of objects
   * @param pClass the type of the return value.
   * @param pComponentClass the type of the component.
   * @param pStrValues the array of string values to convert.
   * @return pStrValue converted to array object of type pClass
   * @exception IllegalArgumentException if couldn't find a property editor for pClass
   */
  protected Object getValueFromStringArray(Class pClass,Class pComponentClass,String [] pStrValues) 
       throws IllegalArgumentException 
  {
    Object value = null;
    
    if(pClass.isArray()) {
	value = Array.newInstance(pComponentClass,pStrValues.length);
	for(int i = 0 ; i < pStrValues.length ; i++) {
	    Object arrayElementValue = null;		    
	    arrayElementValue = getValueFromString(pComponentClass,null,pStrValues[i]);
	    ((Object [])value)[i] = arrayElementValue;	        
	}
    } else if(java.util.Collection.class.isAssignableFrom(pClass)) {
	
	if(!pClass.isInterface()) {
	    try {
		value = pClass.newInstance();
	    } catch(InstantiationException iae) {
		//Ignore
	    } catch(IllegalAccessException ilae) {
		//Ignore
	    } 
	} else if(java.util.List.class.isAssignableFrom(pClass)) {
	    value = new ArrayList();
	} else if(java.util.Set.class.isAssignableFrom(pClass)) {
	    value = new HashSet();
	} else {
	    value = new ArrayList();
	}
	
	if(value != null && pStrValues != null) {
	    for(int i = 0 ; i < pStrValues.length ; i++) {
		Object elementValue = null;		    
		elementValue = getValueFromString(pComponentClass,null,pStrValues[i]);
		((Collection)value).add(elementValue);	        
	    }
	}
    }
    return value;
  }

  /**
   * This method take an array of Objects  pOldValue and converts it to an object 
   * of type pClass returning the converted object. Assumes the pClass is an array 
   * of objects
   * @param pClass the type of the return value.
   * @param pComponentClass the type of the component.
   * @param pOldValue the array of object values to convert.
   * @return pOldValue converted to array object of type pClass
   * @exception IllegalArgumentException if couldn't find a property editor for pClass
   */
  protected Object getValueFromObjectArray(Class pClass,Class pComponentClass,Object [] pOldValue) 
       throws IllegalArgumentException 
  {
    if(pOldValue == null) return null;
    Object newValue = null;
    
    // We need some kind of conversion if the classes don't match
    if (! pClass.isAssignableFrom(pOldValue.getClass())) {
      Class valueClass= null;
      Class valueType = null;
      
      valueClass  = pOldValue.getClass();
      if(valueClass != null) {		
        valueType = valueClass.getComponentType();
        
        if(valueType != null) {
          if(valueType == java.lang.String.class) {
            
            //Array of Strings
            newValue = getValueFromStringArray(pClass,pComponentClass,(String [])pOldValue);
          }
        }
      }
    }
    else {
      newValue = pOldValue;
    }
    return newValue;
  }

  /**
   * This method take an Object pOldValue and converts it to an object 
   * of type pClass returning the converted object. 
   * @param pClass the type of the return value.
   * @param pComponentClass the type of the component.
   * @param pOldValue the object to convert.
   * @return pOldValue converted to an object of type pClass
   * @exception IllegalArgumentException if couldn't find a property editor for pClass
   */
  protected Object getValueFromObject(Class pClass,Class pComponentClass,Object pOldValue) 
       throws IllegalArgumentException 
  {
    if(pOldValue == null) return null;
    Object newValue = null;

    // We need some kind of conversion if the classes don't match
    if (! pClass.isAssignableFrom(pOldValue.getClass())) {
      if (pOldValue instanceof String) {        
        if(((String)pOldValue).trim().length() == 0) {
          newValue = null;
        } else { 		   
          newValue = getValueFromString(pClass,pComponentClass,(String)pOldValue);					    
        }        
      } else if (pOldValue.getClass().isArray()) {
        newValue = getValueFromObjectArray(pClass,pComponentClass,(Object [])pOldValue);
      } else if ((pClass == java.sql.Date.class) &&
               (pOldValue instanceof java.util.Date)) {
        // if we want a java.sql.Date and we have a java.util.Date then perform a magic conversion
        java.util.Date oldDate = (java.util.Date)pOldValue;
        newValue = new java.sql.Date(oldDate.getTime());
      }
      else if ((pClass == java.sql.Timestamp.class) &&
               (pOldValue instanceof java.util.Date)) {
        java.util.Date oldDate = (java.util.Date)pOldValue;
        newValue = new java.sql.Timestamp(oldDate.getTime());
      }
      else if (isLoggingWarning()) {
        Object [] args = new Object[1];
        if (pOldValue != null)
          args[0] = pOldValue.getClass().getName();
        else
          args[0] = "null";
        logWarning(ResourceUtils.getMsgResource(MSG_NO_TYPE_CONVERTER,
                                                RESOURCE_BUNDLE_NAME,
                                                sResourceBundle,args));
      }
    } else {
      newValue = pOldValue;
    }
    
    return newValue;
  }

  /**
   * Takes the value of the form inputed password attribute confirms
   * it if nescessary and finally generates a new password as
   * a function of the PropertyManager.generatePassword() method.
   * @param pValue the value of the password passed from the form.
   * @return the new value of the password attribute.
   */
  protected Object updatePasswordValue(Object pValue,
                             DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    ProfileTools ptools = getProfileTools();
    PropertyManager pmgr = ptools.getPropertyManager();
    String password = (String)pValue;
    
    // If we are suppose to confirm the password, make sure the two match.
    if (isConfirmPassword()) {
      String confirmPassword = getStringValueProperty(CONFIRMPASSWORD_PARAM);
      if ((confirmPassword == null) ||
          (confirmPassword.equals("")) ||
          (! password.equals(confirmPassword)))
        addFormException(new DropletException(formatUserMessage(MSG_PASSWORDS_DO_NOT_MATCH, pRequest),
                                              MSG_PASSWORDS_DO_NOT_MATCH));
    }
    if(pmgr != null && (password != null) || (!"".equals(password))) {
      // Look up the login property value in the value dictionary
      String loginPropertyName = pmgr.getLoginPropertyName();
      String login = getStringValueProperty(loginPropertyName);

      // If it's not there, look it up in the item itself
      if (login == null) {
	RepositoryItem profile = getProfileItem();
	if (profile != null) {
	  try {
	    login = (String) DynamicBeans.
	      getSubPropertyValue(profile, loginPropertyName);
	  }
	  catch (PropertyNotFoundException exc) {
	    String msg = formatUserMessage
	      (MSG_NO_SUCH_PROFILE_PROPERTY, loginPropertyName, pRequest);
	    String propertyPath = generatePropertyPath(loginPropertyName);
	    addFormException(new DropletFormException
	      (msg, exc, propertyPath, MSG_NO_SUCH_PROFILE_PROPERTY));
	    if (isLoggingError())
	      logError(exc);
	  }
	}
      }
      
      pValue = pmgr.generatePassword(login, password);
    }

    return pValue;
  }

  /**
   * Takes the current values of the 'value' property and sticks them
   * into the repository item given.  Adds form errors if there were any problems.
   * @param pMutableUser the user to update the profile attributes by the form values
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception RepositoryException if there was an error while accessing the Profile Repository
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void updateProfileAttributes(MutableRepositoryItem pMutableUser,
                                         Dictionary pValues,
                                         DynamoHttpServletRequest pRequest,
                                         DynamoHttpServletResponse pResponse) 
       throws RepositoryException, ServletException, IOException 
  {
    updateProfileAttributes(pMutableUser, pValues, pRequest, pResponse, null);
  }

  /**
   * Takes the current values of the 'value' property and sticks them
   * into the repository item given.  Adds form errors if there were any problems.
   * @param pMutableUser the user to update the profile attributes by the form values
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @param pPropertyPathPrefix when this method is called recursively for 
   * subproperties, this argument contains the property path up to that point; 
   * initially, it is null
   * @exception RepositoryException if there was an error while accessing the Profile Repository
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io */
  protected void updateProfileAttributes(MutableRepositoryItem pMutableUser,
					 Dictionary pValues,
					 DynamoHttpServletRequest pRequest,
					 DynamoHttpServletResponse pResponse,
					 String pPropertyPathPrefix)
       throws RepositoryException, ServletException, IOException 
  {
    ProfileTools ptools = getProfileTools();
    PropertyManager pmgr = ptools.getPropertyManager();
    String passwordPropertyName = null;
    /* List of RepositoryItems that need to be added once we are finished */
    //ArrayList itemsToAdd = null; 
    if (pmgr != null) {
      passwordPropertyName = pmgr.getPasswordPropertyName();
    }
       	
    Dictionary valueDict = pValues;
    for (Enumeration e = valueDict.keys(); e.hasMoreElements(); ) {
      
      String key = (String) e.nextElement();
      if (key.equalsIgnoreCase(CONFIRMPASSWORD_PARAM))
        continue;
      
      Object value = valueDict.get(key);
      if (value == NULL_SENTINEL) 
        value = null;

      if (isLoggingDebug()) {
	      String valueStr = null;
	      if((value != null) && (value.getClass().isArray())) {
	          StringBuffer buff  = new StringBuffer();
	          buff.append("[");
	          for(int i = 0 ; i < ((Object[])value).length ; i++) {
		          buff.append(((Object[])value)[i]);
		          if(i < (((Object[])value).length-1)) 
		              buff.append(",");
	          }
	          buff.append("]");
	          valueStr = buff.toString();
	      } 
        else if(value != null) {
	        valueStr = value.toString();
	      }
	      
	      logDebug("updateProfileAttributes: looking for property info(" + key + "," + valueStr + ")");		
      }

      RepositoryItemDescriptor itemDescriptor = pMutableUser.getItemDescriptor();
      RepositoryPropertyDescriptor pdesc = (RepositoryPropertyDescriptor) 
                                itemDescriptor.getPropertyDescriptor(key);
      
      if (pdesc == null) {
      	
      	 /* Handled before this dictionary was processed */
        if (key.equalsIgnoreCase(REPOSITORY_ID_PROPERTY_NAME))
          continue;
          
        String msg = formatUserMessage(MSG_NO_SUCH_PROFILE_PROPERTY, key, pRequest);
        String propertyPath = generatePropertyPath(key);
        addFormException(new DropletFormException(msg, propertyPath, MSG_NO_SUCH_PROFILE_PROPERTY));
      } 
      else {      
        String propertyName = pdesc.getName();
        if (propertyName == null)
          propertyName = key;

	      String fullPropertyName = (pPropertyPathPrefix == null) ? propertyName : 
	        pPropertyPathPrefix + '.' + propertyName;
        
        try {
          /*
           * Deal with hierarchical properties here.  If this property is
           * hierarchical, the value we have for it must be a dictionary
           * of other values.  Recursively call this method with that
           * dictionary.
           */
          if (pdesc.getPropertyItemDescriptor() != null) {
            if (value == null)  {
              pMutableUser.setPropertyValue(key, null);
              continue;
            }
            if (!(value instanceof Dictionary)) {
              if (isLoggingError())
                logError("invalid value supplied for hierarchical property value: " + value);
              continue;
            }

	          RepositoryItem nonMutableItem = (RepositoryItem) pMutableUser.getPropertyValue(key);
	          MutableRepositoryItem item = null;
	          if (nonMutableItem != null) {
	            if (nonMutableItem instanceof MutableRepositoryItem)
		            item = (MutableRepositoryItem) nonMutableItem;
	            else
		            item = ((MutableRepository) nonMutableItem.getRepository()).
		              getItemForUpdate(nonMutableItem.getRepositoryId(), 
				               nonMutableItem.getItemDescriptor().getItemDescriptorName());
	          }

            /*
             * Trying to set properties of a value which is not yet set.
             * We're just going to create a new item here and add the values.
             */
            boolean newItem = false;
            RepositoryItemDescriptor pt = pdesc.getPropertyItemDescriptor();
            MutableRepository pmr = (MutableRepository) pt.getRepository();
            // Pull out the REPOSITORY_ID_PROPERTY_NAME - and get the item that corresponds to it and set that as the value for this property.
            Dictionary subValueDict = (Dictionary) value;
            Object newId = subValueDict.get(REPOSITORY_ID_PROPERTY_NAME);
            if (newId != null) {
              if (newId == NULL_SENTINEL) {
                pMutableUser.setPropertyValue(key, null);
              }
              else {
                item = pmr.getItemForUpdate(newId.toString(),
                         pt.getItemDescriptorName());
                if (item == null) {
                  Object[] args = { newId, pt.getItemDescriptorName() };    
                  addFormException(new DropletFormException(ResourceUtils.getMsgResource
                            ("noRepositoryItemWithType", RESOURCE_BUNDLE_NAME, sResourceBundle, args),
                              getAbsoluteName() + ".value." + key, "noRepositoryItemWithType"));
                  continue;
                }
                else {
                  pMutableUser.setPropertyValue(key, item);
                }
              }
            }
            else{
              if (item == null) {
                item = pmr.createItem(pt.getItemDescriptorName());
                pMutableUser.setPropertyValue(key, item);
                newItem = true;
              }
            }
            
            // NOTE: item is null if we have set repositoryId to 'null', and its in the subValueDict
            // as NULL_SENTINEL.  so check here.  that means if you set it like that, any properties
            // you might have set into the map are ignored.
            if(item != null) {
              updateProfileAttributes(item, (Dictionary)value, pRequest, pResponse, fullPropertyName);

              if(isUsingLdapProfile())
	              updateLDAPProfileAttributes(item);

             /* 
               * Once we've set all properties on this item, we add/update it
               */
              if (newItem) 
                pmr.addItem(item);
              else
                pmr.updateItem(item);
            }

            continue;
          }

	        // We do this after dealing with hierarchical properties,
	        // because a property of a non-writable property may itself
	        // be writable
	        if (! pdesc.isWritable()) {
	          String name = pdesc.getDisplayName();
	          if (name == null)
	            name = key;
	          String msg = formatUserMessage(MSG_READ_ONLY_PROFILE_PROPERTY, name, pRequest);
	          String propertyPath = generatePropertyPath(key);
	          addFormException(new DropletFormException(msg, propertyPath, MSG_READ_ONLY_PROFILE_PROPERTY));
	        }
          else {
	          //Get the value of this attribute
	          value = getValueFromObject(pdesc.getPropertyType(),pdesc.getComponentPropertyType(), value);
          
	          // If we are updating the password property then we need to special case it
	          // because we might need to store the hash of the property
	          if ((fullPropertyName.equalsIgnoreCase(passwordPropertyName))&& (!value.equals(""))) {
	            Object passwordValue = null;
	            if ((passwordValue = updatePasswordValue(value, pRequest, pResponse)) != null)
		            value = passwordValue;	      	      
	        }
          
	        // Trim Strings 
	        if((value instanceof String) && (isTrimProperty(propertyName))) {
	          value = ((String)value).trim();
	        }
        
	        // Turn all empty strings into nulls.
	        if ((value instanceof String) && (value.equals("")))
	          value=null;
        
	        // Turn all empty arrays into nulls.
	        if (valueIsEmpty(value))
	          value=null;
          
	        if (isLoggingDebug()) {
	            String valueStr = null;
	            if((value != null) && (value.getClass().isArray())) {
		            StringBuffer buff  = new StringBuffer();
		            buff.append("[");
		            for(int i = 0 ; i < ((Object[])value).length ; i++) {
		              buff.append(((Object[])value)[i]);
		              if(i < (((Object[])value).length-1)) 
		                buff.append(",");
		            }
		            buff.append("]");
		            valueStr = buff.toString();
	            } 
              else if(value != null) {
		            valueStr = value.toString();
	            }
	            logDebug("updateProfileAttributes: setting profile.setPropertyValue(" + propertyName + "," + valueStr + ")");
	          }
          
	          pMutableUser.setPropertyValue(propertyName, value);
          }
        } 
        catch (IllegalArgumentException exc) {
          String msg = formatUserMessage(MSG_ILLEGAL_ARGUMENT, propertyName, pRequest);
          String propertyPath = generatePropertyPath(key);
          addFormException(new DropletFormException(msg, exc, propertyPath, MSG_ILLEGAL_ARGUMENT));
        }
      }
    }
  }

  /**
   * Updates profile attributes pertaining to LDAP. Specifically, this
   * sets the "fullName" property out of the first and last name
   * of the given item. This is so that forms that create LDAP items
   * don't have to have a fullName field in addition to firstName and 
   * lastName (fullName is a required property in LDAP). The other way
   * to do this would be to ask for just the users full name
   * and then split that up in this method to set the first name
   * and last name property for the user as well
   * @param pItem the item to change values on
   **/
  public void updateLDAPProfileAttributes(MutableRepositoryItem pItem)
    throws RepositoryException
  {
    if(pItem == null)
      return;

    PropertyManager pmgr = getProfileTools().getPropertyManager();
    String fullNameProp = pmgr.getFullNamePropertyName();
    String firstNameProp = pmgr.getFirstNamePropertyName();
    String lastNameProp = pmgr.getLastNamePropertyName();
    RepositoryItemDescriptor itemDesc = pItem.getItemDescriptor();
    
    // If we don't have all the name properties we need, then just
    // return
    if(!(itemDesc.hasProperty(fullNameProp) &&
	 itemDesc.hasProperty(firstNameProp) &&
	 itemDesc.hasProperty(lastNameProp)))
      return;

    Object firstName = pItem.getPropertyValue(firstNameProp);
    Object lastName = pItem.getPropertyValue(lastNameProp);

    if(firstName == null || lastName == null)
      return;

    pItem.setPropertyValue(fullNameProp, firstName + " " + lastName);
  }
      

  /**
   * Returns a Nucleus property path which can be used in a DropletFormException
   */
  protected String generatePropertyPath(String pPropertyName) {
    return getAbsoluteName() + ".value." + pPropertyName;
  }
    
  //---------------------------------------------
  // Utility methods
  //---------------------------------------------
    
  /** 
   * Return the value of a property from the <code>values</code>
   * Dictionary as a String 
   */
  protected String getStringValueProperty(String pName) {
    Object obj = getValueProperty(pName);
    if (obj == null) return null;
    return obj.toString();
  }

  /** 
   * Return the value of a property from the <code>values</code>
   * Dictionary
   */
  protected Object getValueProperty(String pName) {
    Object value = getValue();
    String name;
    int ix;
    do {
      ix = pName.indexOf('.');
      if (ix == -1) name = pName;
      else {
        name = pName.substring(0, ix);
	pName = pName.substring(ix+1);
      }
      if (value instanceof Dictionary)
	value = ((Dictionary) value).get(name);
      else
	value = null;
    }
    while (value != null && ix != -1);
    if(value == NULL_SENTINEL) return null;
    return value;
  }

  /**
   * Set the value of a property into the <code>values</code>
   * Dictionary. If pValue is null then the property defined
   * by pName is removed from the <code>values</code>
   * Dictionary.
   */
  protected void setValueProperty(String pName, Object pValue) {
    if (pValue == null){
      pValue = NULL_SENTINEL;
    }
    getValue().put(pName, pValue);
  }

  /** 
   * Return the RepositoryItemDescriptor that describes the profiles that this formHandler edits.
   */
  protected RepositoryItemDescriptor getDescriptor() 
  {
    try {
      ProfileTools ptools = getProfileTools();
      if (ptools == null) return null;
      MutableRepository repository = ptools.getProfileRepository();
      if (repository == null) return null;
      return repository.getItemDescriptor(getCreateProfileType());
    }
    /* This should rarely happen */
    catch (RepositoryException exc) {
      if (isLoggingError())
        logError("Unable to retrieve item descriptor: " + getCreateProfileType());
    }
    return null;
  }
  
  /**
   * Check to see if there were errors during the submit operation.  
   * If so, redirect to the supplied error url if possible.
   * @return the status of the form
   * @see STATUS_SUCCESS
   * @see STATUS_ERROR_STAY
   * @see STATUS_ERROR_REDIRECT
   * @param pErrorURL the URL to redirect to if there are any form errors
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected int checkFormError(String pErrorURL, 
                               DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    if (getProfileTools() == null) {
    	addFormException(new DropletException(formatUserMessage(MSG_MISSING_PROFILE_TOOLS, pRequest)));
    }
    if (getFormError()) {
      String redirectURL = pErrorURL;
      if (redirectURL != null) {
        if (isLoggingDebug())
          logDebug("error - redirecting to: " + redirectURL);
        pResponse.sendLocalRedirect(redirectURL, pRequest);
        return STATUS_ERROR_REDIRECT;
      }
      return STATUS_ERROR_STAY;
    }
    return STATUS_SUCCESS;
  }
    
  /**
   * If the form was successfully submitted, see if we should redirect
   * after processing the form data.  
   * @return false is returned is we redirect
   * @param pSuccessURL the URL to redirect to if the form was successfully submitted
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected boolean checkFormSuccess(String pSuccessURL, 
                                     DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    String redirectURL = pSuccessURL;
    if (redirectURL != null) {
      if (isLoggingDebug())
        logDebug("success - redirecting to: " + redirectURL);
      pResponse.sendLocalRedirect(redirectURL, pRequest);
      return false;
    }
	
    if (isLoggingDebug()) {
      logDebug("Successfully completed: " + pSuccessURL + " contents of value are:");
      Dictionary valuesDict = getValue();
      for (Enumeration e = valuesDict.keys(); e.hasMoreElements(); ) {
        String key = (String)e.nextElement();
        Object value = getValueProperty(key);
        if (value != null)
          logDebug("name=" + key + " value=" + value + " class=" + value.getClass().getName());
        else
          logDebug("name=" + key + " value=null");
      }
    }
    return true;
  }

  /**
   * Returns an array of PropertyNames that setValueDictionaryByParameters should process. 
   * @param pRequest the servlets request
   * @param pResponse the servlets response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected String [] getProcessPropertyNames(DynamoHttpServletRequest pRequest,
                                              DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {    
    return pRequest.getParameterValues("_processPropertyName");
  }

  /**
   * Goes through each pRequests Parameters looking for parameters
   * named after profile attributes and sets the values of these 
   * parameters in the value dictionary.
   * @param pRequest the servlets request
   * @param pResponse the servlets response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void setValueDictionaryByParameters(DynamoHttpServletRequest pRequest,
                                                DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    RepositoryItemDescriptor itemDesc = null;
    
    itemDesc = getDescriptor();	    
    if(itemDesc != null) {				
      String [] processProperties =
	getProcessPropertyNames(pRequest,pResponse);
      
      if(processProperties != null) {
        for(int i = 0 ; i < processProperties.length ; i++) {
          DynamicPropertyDescriptor dPropDesc = null;
          
          dPropDesc = itemDesc.getPropertyDescriptor(processProperties[i]);
          if(dPropDesc != null) {
            String dPropName = null;
            Class dPropClass = dPropDesc.getPropertyType();
            
            dPropName = dPropDesc.getName();
            if(dPropName != null) {
              Dictionary valueDict = getValue();
              Object value = null;
              if(dPropClass.isArray()) {
                String [] dPropValues = null;
                dPropValues = pRequest.getParameterValues(dPropName);
                if(dPropValues == null) 
                  dPropValues = (String [])Array.newInstance(java.lang.String.class,0);
                value = dPropValues;
              } 
	      else if(Collection.class.isAssignableFrom(dPropClass)) {
		String[] dPropValues = pRequest.getParameterValues(dPropName);
		Class componentClass = dPropDesc.getComponentPropertyType();
		Collection coll = null;
		if(dPropValues == null)
		  value = null;
		else if(List.class.isAssignableFrom(dPropClass)) {
		  coll = new ArrayList();
		  for(int j = 0; j < dPropValues.length; j++) {
		    Object obj = getValueFromString
		      (componentClass, null, dPropValues[j]);
		    coll.add(obj);
		  }
		  value = coll;
		}
		else if(Set.class.isAssignableFrom(dPropClass)) {
		  coll = new HashSet();
		  for(int j = 0; j < dPropValues.length; j++) {
		    Object obj = getValueFromString
		      (componentClass, null, dPropValues[j]);
		    coll.add(obj);
		  }
		  value = coll;
		}
	      }
	      else {
                String dPropValue = null;
                dPropValue = pRequest.getParameter(dPropName);
                value = dPropValue;
              }
              
              // Add item to dictionary, if dPropValue is null, 
	      // it will be removed
              setValueProperty(dPropName,value);            
	    }
          }
        }			       
      }
    }
  }

  /**
   * Returns the Locale for the user given the request
   * @param pRequest the request object which can be used to extract the user's locale
   */
  protected Locale getUserLocale(DynamoHttpServletRequest pRequest) {
    //Locale userLocale = null;
    if (pRequest != null) {
      RequestLocale reqLocale = pRequest.getRequestLocale();
      if (reqLocale != null)
        return reqLocale.getLocale();
    }
    return null;
  }
  
  /**
   * Retrives the current data source for the profile being acted
   * upon in this form
   * @return the current data source for the profile being acted
   * upon in this form
   **/
  private RepositoryItem getCurrentDataSource() {
    if(getProfile() != null)
      return getProfile().getDataSource();
    return getProfileItem();
  }

  /**
   * Sends a ProfileSwapEvent using the given arguments
   * @param pEventType the type of operation that was performed when
   * the profiles were swapped
   * @param pPreSwapItem the data source of the profile before the
   * swap was performed
   * @param pPostSwapItem the data source of the profile after the
   * swap was performed
   **/
  public void sendProfileSwapEvent(int pEventType,
                                   RepositoryItem pPreSwapItem,
                                   RepositoryItem pPostSwapItem)
  {
    ProfileSwapEvent event =
      new ProfileSwapEvent(this, pPreSwapItem, pPostSwapItem, pEventType);
    synchronized(mSwapEventListeners) {
      Iterator iter = mSwapEventListeners.iterator();
      while(iter.hasNext()) {
        ProfileSwapEventListener listener =
          (ProfileSwapEventListener)iter.next();
        listener.receiveSwapEvent(event);
      }
    }
  }

  /**
   * Utility method to format a message with no arguments using the Locale of the user
   * @param pKey the identifier for the message to retrieve out of the ResourceBundle
   * @param pRequest the request object which can be used to extract the user's locale
   * @return the formatted message
   * @see ProfileUserMessage
   **/
  protected String formatUserMessage(String pKey, DynamoHttpServletRequest pRequest)
  {
    return ProfileUserMessage.format(pKey, getUserLocale(pRequest));
  }

  /**
   * Utility method to format a message with one argument using the Locale of the user
   * @param pKey the identifier for the message to retrieve out of the ResourceBundle
   * @param pParam the first (and only argument) in the message
   * @return the formatted message
   * @see ProfileUserMessage
   **/
  protected String formatUserMessage(String pKey, Object pParam, DynamoHttpServletRequest pRequest)
  {
    return ProfileUserMessage.format(pKey, pParam, getUserLocale(pRequest));
  }

  /**
   * Utility method to format a message with two arguments using our resource bundle.
   * @param pKey the identifier for the message to retrieve out of the ResourceBundle
   * @param pParam1 the first parameter in the message
   * @param pParam2 the second parameter in the message
   * @return the formatted message
   * @see ProfileUserMessage
   **/
  protected String formatUserMessage(String pKey, Object pParam1, Object pParam2, DynamoHttpServletRequest pRequest)
  {
    Object[] params = { pParam1, pParam2, };
    return ProfileUserMessage.format(pKey, params, getUserLocale(pRequest));
  }

  /**
   * Utility method to format a message with two arguments using our resource bundle.
   * @param pKey the identifier for the message to retrieve out of the ResourceBundle
   * @param pParams a set of parameters to use in the formatting.
   * @return the formatted message
   * @see ProfileUserMessage
   **/
  protected String formatUserMessage(String pKey, Object [] pParams, DynamoHttpServletRequest pRequest)
  {
    return ProfileUserMessage.format(pKey, pParams, getUserLocale(pRequest));
  }

  
  /**
   * This registers the mapping between the FormHashtable class and
   * the "dynamic beans" mechanism which allows to set/get these values
   * from dynamo server pages.
   */
  static {
    DynamicBeans.registerPropertyMapper(ProfileFormHashtable.class, 
                                        new ProfileFormHashtablePropertyMapper());
  }
  
  public class ProfileFormHashtableWrapper implements Map
  {
    private Dictionary mOrig;
    private DynamicPropertyMapper mPropertyMapper = new ProfileFormHashtablePropertyMapper();

    public ProfileFormHashtableWrapper(Dictionary pOrig)
    {
      mOrig = pOrig;
    }

    public int size()
    {
      return mOrig.size();
    }

    public void clear()
    {
      // Ignore, we shouldn't need it
    }

    public boolean isEmpty()
    {
      return mOrig.isEmpty();
    }

    public boolean containsKey(Object key)
    {
      return mOrig.get(key) != null;
    }

    public boolean containsValue(Object value)
    {
      // Ignore, shouldn't need it
      return false;
    }

    public Collection values()
    {
      // Ignore, shouldn't need this
      return Collections.EMPTY_LIST;
    }

    public void putAll(Map t)
    {
      // Ignore, shouldn't need this
    }

    public Set entrySet()
    {
      // Ignore, shouldn't need this
      return Collections.EMPTY_SET;
    }

    public Set keySet()
    {
      // Ignore, shouldn't need this
      return Collections.EMPTY_SET;
    }

    public Object get(Object key)
    {
      try
      {
        Object value = mPropertyMapper.getPropertyValue(mOrig, (String)key);
        if (value instanceof RepositoryItem)
        {
          value = new DynamicBeanMap(value, true);
        }
        if (value instanceof ProfileFormHashtable)
        {
          value = ((ProfileFormHashtable)value).getItem();
          if (value == null) return Collections.EMPTY_MAP;
          value = new DynamicBeanMap(value, true);
        }
        return value;
      }
      catch (PropertyNotFoundException e)
      {
      }
      return null;
    }

    public Object remove(Object key)
    {
      return mOrig.remove(key);
    }

    public Object put(Object key, Object value)
    {
      return mOrig.put(key, value);
    }
  }
}

//------------------------
/* 
 * Define a hashtable with a property mapper which is case insensitive and
 * does not give hard errors for properties that are not currently defined.
 * <p>
 * This cannot be an inner class because it confuses the component
 * browser when it tries to serialize the value.  Instead, we need
 * the reference to our parent type made explicitly.
 */
class ProfileFormHashtable extends CaseInsensitiveHashtable {
  static final long serialVersionUID = 3465219641568516969L;

  transient ProfileForm mForm;
  transient RepositoryItemDescriptor mItemType;
  transient RepositoryItem mItem;

  ProfileFormHashtable(ProfileForm pForm) {
    mForm = pForm;
  }
  ProfileFormHashtable(ProfileForm pForm, RepositoryItemDescriptor pItemType,
                        RepositoryItem pItem) {
    mForm = pForm;
    mItemType = pItemType;
    mItem = pItem;
  }
  ProfileForm getProfileForm() {
    return mForm;
  }
  RepositoryItemDescriptor getItemType() {
    if (mItemType == null) return mForm.getDescriptor();
    return mItemType;
  }
  RepositoryItem getItem() {
    /* 
     * If we are at the parent level, return the item at the parent.
     * Otherwise, we return null here even if mItem is null.
     */
    if (mItemType == null) 
      return mForm.getProfileItem();
    else
      return mItem;
  }
}

/**
 * Defines a dynamic property mapper for the FormHashtable class
 * so that we can get/set values of our value property.
 */
class ProfileFormHashtablePropertyMapper 
implements DynamicPropertyMapper 
{
  ProfileFormHashtablePropertyMapper() {}
  
  //-------------------------------------
  // METHODS
  //-------------------------------------
  
  /**
   * Gets the value of the dynamic property from the specified object.
   */
  public Object getPropertyValue(Object pBean, String pPropertyName) 
       throws PropertyNotFoundException
  {
    ProfileFormHashtable data = (ProfileFormHashtable)pBean;
    Object value = data.get(pPropertyName);
    if(value == ProfileForm.NULL_SENTINEL) return null;
    //String id = null;
    if (value == null) {
      ProfileForm form = data.getProfileForm();
      //Get it from its Descriptor	   
      RepositoryItemDescriptor desc = data.getItemType();
      RepositoryPropertyDescriptor propDesc  = (RepositoryPropertyDescriptor)
                                desc.getPropertyDescriptor(pPropertyName);
      RepositoryItem item = data.getItem();
      Object propValue = null; 

      /* No property with this name... */
      if (propDesc == null) {
        if (pPropertyName.equalsIgnoreCase(ProfileForm.REPOSITORY_ID_PROPERTY_NAME)) {
          if (item != null) 
          	return item.getRepositoryId();
          else 
          	return null;
        }
        return null;
      }

      if (item != null) {
        propValue = DynamicBeans.getPropertyValue(item, pPropertyName);
      }
      /*
       * Create a new hashtable to create a collector for properties
       * of our property's property.
       */
      if (propDesc.getPropertyItemDescriptor() != null) {
        value = new ProfileFormHashtable(form, propDesc.getPropertyItemDescriptor(),
                                         (RepositoryItem)propValue);
        data.put(pPropertyName, value);
        return value;
      }

      if (form.isExtractDefaultValuesFromProfile()) {
        if (propValue == null) {
          return propDesc.getDefaultValue();
        }
        return propValue;
      }
    }
    return value;
  }
  
  /**
   * Sets the value of the dynamic property from the specified object.
   */
  public void setPropertyValue(Object pBean, String pPropertyName, Object pValue) 
  {
    ProfileFormHashtable data = (ProfileFormHashtable)pBean;
    // if the value is not null then put it in the
    // ProfileFormHashTable, otherwise put NULL_SENTINEL
    if (null != pValue) {
      data.put(pPropertyName, pValue);
    }
    else {
      data.put(pPropertyName, ProfileForm.NULL_SENTINEL);
    }
  }
  
  
  //-------------------------------------
  /**
   * Gets a DynamicBeanInfo that describes the given dynamic bean.
   *
   * @return the DynamicBeanInfo describing the bean.
   * @throws IntrospectionException if no information is available.
   */
  public DynamicBeanInfo getBeanInfo(Object pBean)
       throws IntrospectionException
  {
    ProfileFormHashtable pf = (ProfileFormHashtable) pBean;
    return pf.getItemType();
  }

  
} // end of class
