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
 * Dynamo is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.userprofiling;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.ItemDescriptorImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.security.IdentityManager;
import atg.security.SecurityException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;

/**
 * This class provides a convenient form handler for operating on the
 * current user's profile.  It can be used to add new profiles,
 * edit the current profile, login (switch profiles based on login name
 * and password) and logout.
 *
 * @author Jeff Vroom
 * @version $Id: //product/DPS/version/10.0.3/Java/atg/userprofiling/ProfileFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo
 *   description: A form handler that manages the current user's profile
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 */
public class ProfileFormHandler
extends ProfileForm
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DPS/version/10.0.3/Java/atg/userprofiling/ProfileFormHandler.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  // User Messages
  static final String MSG_ERR_UPDATING_SECURITY_STATUS = "errorUpdatingSecurityStatus";
  
  //-------------------------------------
  // Member Variables
  //-------------------------------------

  /** List of objects listening for "profile update" events **/
  Vector mProfileUpdateListeners;

  //-------------------------------------
  // Properties
  //-------------------------------------

  //--------- Property: Profile -----------
  Profile mProfile;

  /**
   * Sets the property Profile. This is set to the user Profile to edit.
   * @beaninfo description: the user Profile to be edited
   */
  public void setProfile(Profile pProfile) {
    mProfile = pProfile;
  }

  /**
   * Returns the value of the property Profile.
   */
  public Profile getProfile() {
    return mProfile;
  }

  //----------- Property: RepositoryId -----------
  /**
   * Returns the RepositoryId from the current Profile property
   */
  public String getRepositoryId() {
    return getProfile().getRepositoryId();
  }

  /** This is the repository id of the user that is about to logout **/
  /** NOTE: This is not meant to be used outside of this context in 
      any custom manner, and thus no accessor methods are provided **/
  String mLogoutRepositoryId;

  //-------------------------------------
  // property: verifyPasswordSuccessURL
  String mVerifyPasswordSuccessURL;
  public void setVerifyPasswordSuccessURL(String pVerifyPasswordSuccessURL) {
    mVerifyPasswordSuccessURL = pVerifyPasswordSuccessURL;
  }

  public String getVerifyPasswordSuccessURL() {
    return mVerifyPasswordSuccessURL;
  }

  //-------------------------------------
  // property: testPassword
  String mTestPassword;
  public void setTestPassword(String pTestPassword) {
    mTestPassword = pTestPassword;
  }

  public String getTestPassword() {
    return mTestPassword;
  }

  //-------------------------------------
  // property: clearValuesOnUpdate
  public boolean mClearValuesOnUpdate;
  /**
   * Sets the property clearValuesOnUpdate
   * @beaninfo description: set to true if form data should be cleared after update.
   */
  public void setClearValuesOnUpdate(boolean pClearValuesOnUpdate) {
    mClearValuesOnUpdate = pClearValuesOnUpdate;
  }

  public boolean getClearValuesOnUpdate() {
    return mClearValuesOnUpdate;
  }

  //-------------------------------------
  // property: clearValuesOnCreate
  public boolean mClearValuesOnCreate;
  public void setClearValuesOnCreate(boolean pClearValuesOnCreate) {
    mClearValuesOnCreate = pClearValuesOnCreate;
  }

  public boolean getClearValuesOnCreate() {
    return mClearValuesOnCreate;
  }

  //-------------------------------------
  // property: clearValuesOnLogin
  public boolean mClearValuesOnLogin;
  public void setClearValuesOnLogin(boolean pClearValuesOnLogin) {
    mClearValuesOnLogin = pClearValuesOnLogin;
  }

  public boolean getClearValuesOnLogin() {
    return mClearValuesOnLogin;
  }

  /**
   * With the given id find the associated user and set the Profile's
   * datasource to that person.   <b>Warning:</b> You should not set this
   * property from a dynamo server page since it
   * will allow users to arbitrarily modify their profile id by modifying
   * the state of the request.
   *
   * @param pId the id of the user that the Profile should become
   */
  public void setRepositoryId(String pId) {
    if(isLoggingDebug())
      logDebug("setRepositoryId(" + pId + ")");

    if(pId == null) {
      getProfile().setDataSource(null);
      return;
    }

    try {
      MutableRepository repository = getProfileTools().getProfileRepository();
      //Get The RepositoryItem
      RepositoryItem user = repository.getItem(pId, getCreateProfileType());

      if (user != null) {

        // we want to be able to modify a user's properties
        if (! (user instanceof MutableRepositoryItem)) {
          user = repository.getItemForUpdate(pId, getCreateProfileType());
        }

        if(isLoggingDebug())
          logDebug("setting Profile.dataSource to " + user);

        // Now update the current Profile to point to this item
        getProfile().setDataSource(user);
      }
    } catch (RepositoryException exc) {
      addFormException(new DropletException("Unable to set Profile", exc, "errorSettingProfile"));
      if (isLoggingError())
        logError(exc);
    }
  }

  //--------- Property: UpdateRepositoryId -----------
  String mUpdateRepositoryId;
  /**
   * Sets the property UpdateRepositoryId.  If this property is set, the
   * handleUpdate method will only update the repository id specified here.
   * When writing a profile update form, you should attach a hidden tag
   * to this property so that you end up saving the current profile id
   * when the form is rendered, and then set that profile id as this
   * property when the form is submitted.  If the ids don't match, then
   * the user has changed the current profile id either because the
   * session expired, or they went off and logged out/in in a different
   * window in the same session.
   * <p>
   * Your input tag should look like:
   *  &lt;input type="hidden" bean="ProfileFormHandler.updateRepositoryId"
   *            value="bean:ProfileFormHandler.repositoryId&gt;
   * @beaninfo description: Used to ensure that this form only updates
   * the correct profile.  Guard against session expiration and/or someone
   * logging out/in as a different user and then using the back button to
   * go to a stale form.
   */
  public void setUpdateRepositoryId(String pUpdateRepositoryId) {
    mUpdateRepositoryId = pUpdateRepositoryId;
  }
  /**
   * @return The value of the property UpdateRepositoryId.
   */
  public String getUpdateRepositoryId() {
    return mUpdateRepositoryId;
  }


  //-------------------------------------
  // property: CreateNewUser
  boolean mCreateNewUser = false;

  /**
   * Sets property CreateNewUser. If this property is true then a new
   * profile is created when the user registers.
   * @beaninfo description: flag indicating whether form handler should create a profile
   * if none exists yet.
   **/
  public void setCreateNewUser(boolean pCreateNewUser) {
    mCreateNewUser = pCreateNewUser;
  }

  /**
   * Returns property CreateNewUser
   **/
  public boolean isCreateNewUser() {
    return mCreateNewUser;
  }

  //--------- Property: ProfileUpdateEvent -----------
  ProfileUpdateEvent mProfileUpdateEvent = null;

  /**
   * Sets the property ProfileUpdateEvent.
   * @beaninfo description: the event containing the details about updates made to a profile.
   */
  public void setProfileUpdateEvent(ProfileUpdateEvent pProfileUpdateEvent) {
    mProfileUpdateEvent = pProfileUpdateEvent;
  }

  /**
   * Returns the value of the property ProfileUpdateEvent.
   */
  public ProfileUpdateEvent getProfileUpdateEvent() {
    return mProfileUpdateEvent;
  }

  //--------- Property: ProfileUpdateEvent -----------
  ProfileUpdateTrigger mProfileUpdateTrigger;

  /**
   * Returns the ProfileUpdateTrigger property
   **/
  public ProfileUpdateTrigger getProfileUpdateTrigger() {
    return mProfileUpdateTrigger;
  }

  /**
   * Set the ProfileUpdateTrigger property
   **/
  public void setProfileUpdateTrigger(ProfileUpdateTrigger pProfileUpdateTrigger) {
    mProfileUpdateTrigger = pProfileUpdateTrigger;
  }

  //-------------------------------------
  // property: GenerateProfileUpdateEvents
  /** indicates if update events are to be sent at all */
  boolean mGenerateProfileUpdateEvents=false;

  /**
   * Set property GenerateProfileUpdateEvents
   *
   * @beaninfo description: indicates if profile update events are desired
   * @param pGenerateProfileUpdateEvents new value to set
   **/
  public void setGenerateProfileUpdateEvents(boolean pGenerateProfileUpdateEvents) {
    mGenerateProfileUpdateEvents = pGenerateProfileUpdateEvents;
  }

  /**
   * Get property GenerateProfileUpdateEvents
   * @return GenerateProfileUpdateEvents
   */
  public boolean isGenerateProfileUpdateEvents() {
    return mGenerateProfileUpdateEvents;
  }

  
  //--------- Property: ExpiredPasswordService -----------
  ExpiredPasswordService mExpiredPasswordService; 

  /**
   * Sets the ExpiredPasswordService component
   */
  public void setExpiredPasswordService(ExpiredPasswordService pExpiredPasswordService) {
    mExpiredPasswordService = pExpiredPasswordService;
  }

  /**
   * Returns the ExpiredPasswordService component
   */
  public ExpiredPasswordService getExpiredPasswordService() {
    return mExpiredPasswordService;
  }
  
  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs a new ProfileFormHandler.  This class is typically constructed
   * as a nucleus component using a .properties file.  It can be
   * a request scoped or session scoped component.  Use session scope if you
   * have a form that is split across several pages.
   */
  public ProfileFormHandler () {
  }

  //-------------------------------------
  // Profile Form overrides
  //-------------------------------------

  /**
   * Syncronize on the current Profile. See
   * <code> public boolean ProfileForm.handleCreate(DynamoHttpServletRequest,DynamoHttpServletResponse pResponse)</code>
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleCreate(DynamoHttpServletRequest pRequest,
            DynamoHttpServletResponse pResponse)
      throws ServletException, IOException
  {
    synchronized(getProfile()) {
      return super.handleCreate(pRequest,pResponse);
    }
  }

  //----------- Submit: Update ----------------
  /**
   * Takes the current set of values set as members of the value property
   * and, if there were no errors in submitting the form, modifies the
   * profile described by the RepositoryId property with these values.
   * <p>
   * This version of this method adds an optional consistency check to make
   * sure that the current users's profile matches the updateRepositoryId
   * property (if that property is set to a non-null value).
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
    if (getUpdateRepositoryId() != null &&
        !getUpdateRepositoryId().equals(getRepositoryId()))
      addFormException(new DropletException(formatUserMessage(MSG_ERR_UPDATING_PROFILE, pRequest),
                                            MSG_ERR_UPDATING_PROFILE));

    return super.handleUpdate(pRequest, pResponse);
  }

  /**
   * This method is called from createUser to create the MutableRepositoryItem
   * to use in the creation of a user. If the property createNewUser is true
   * we instantiate a new profile else we use the current profile.
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
    MutableRepository repository = getProfileTools().getProfileRepository();
    RepositoryItem currentUser = getProfile().getDataSource();
    MutableRepositoryItem mutableItem = null;

    try {
      
      if(shouldCreateNewUser(repository, currentUser)) {
        currentUser = repository.createItem(getCreateProfileType());
      }

      if (! (currentUser instanceof MutableRepositoryItem)) {
        mutableItem = repository.getItemForUpdate
          (currentUser.getRepositoryId(), 
           getCreateProfileType());
      }
      else {
        mutableItem = (MutableRepositoryItem) currentUser;
      }
    } catch (RepositoryException e) {
      addFormException(new DropletException(formatUserMessage(MSG_ERR_CREATING_PROFILE, pRequest),
                                            e, MSG_ERR_CREATING_PROFILE));
      if (isLoggingError())
        logError(e);
    }
    return mutableItem;
  }

  /**
   * Profile Cookies are rebroadcast as necessary and a Profile Event
   * is fired to indicate a user was created.  Also, the securityStatus
   * property gets set.
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
    Boolean isCreated = (Boolean)pRequest.getObjectParameter(HANDLE_CREATE_PARAM);
    if ((isCreated != null) && (isCreated.booleanValue())) {
      if (isLoggingDebug())
        logDebug("postCreateUser: Sending profile cookies as needed");
      CookieManager cookieManager = getProfileTools().getCookieManager();
      if ((cookieManager != null) && (cookieManager.isSendProfileCookies()) 
	&& isSendCookie(getProfile()))
        cookieManager.forceProfileCookies(getProfile(), pRequest, pResponse);

      // log in the user from the security system's standpoint
      assumeSecurityIdentity(pRequest);

      // all done, send some events
      if (isLoggingDebug())
        logDebug("postCreateUser: sending a register event");
      if (getProfileTools().getProfileEventTrigger() != null)
        getProfileTools().getProfileEventTrigger().sendRegisterEvent(getProfile(), pRequest);
    }

    if (getProfileTools().isEnableSecurityStatus()) {
      try {
        getProfileTools().setLoginSecurityStatus(getProfile(), pRequest);
      }
      catch (RepositoryException e) {
        addFormException(new DropletException(sResourceBundle.getString("errorUpdatingSecurityStatus"), e));
        if (isLoggingError())
          logError(e);
      }
    }

    propagateLocale(pRequest,pResponse);  

    if (getClearValuesOnCreate()) {
        // Clear out form data
        handleClear(pRequest, pResponse);
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
    // if the there's already a user logged in, see if this is the same user
    Profile profile = getProfile();

    // also make sure that we're not using persistent anonymous profiles.
    // in that case, assume that we have a transient profile, since we
    // can't actually tell.
    ProfileRequestServlet prs = (ProfileRequestServlet) pRequest.resolveName("/atg/userprofiling/ProfileRequestServlet", false);

    if (!profile.isTransient() && prs != null && ! prs.isPersistentAnonymousProfiles()) {
      ProfileTools prTools = getProfileTools();
      PropertyManager pmgr = prTools.getPropertyManager();

      // get the submitted form's login/pwd and hash the pwd
      String loginPropertyName = pmgr.getLoginPropertyName();
      String login = getStringValueProperty(loginPropertyName);
      if(login != null)
        login = login.trim();
      String pwdPropertyName = pmgr.getPasswordPropertyName();
      String pwd = getStringValueProperty(pwdPropertyName);
      if (pwd != null)
        pwd = pwd.trim();
      Object prLoginObj = profile.getPropertyValue(loginPropertyName);
      String prLogin = null;
      if (prLoginObj != null)
        prLogin = prLoginObj.toString();

      boolean authSuccessful;
      IdentityManager identityManager = mUserLoginManager.getIdentityManager(pRequest);
      try {
        authSuccessful = identityManager.checkAuthenticationByPassword(login, pwd);
      }
      catch (SecurityException e) {
        authSuccessful = false;
      }

      if (authSuccessful && login.equals(prLogin)) {
        boolean addError = true;
        // user matches, check the security status,
        // if there's a security status property
        if (prTools.isEnableSecurityStatus()){
          int securityStatus = -1;
          String securityStatusPropertyName = pmgr.getSecurityStatusPropertyName();
          Object ss = profile.getPropertyValue(securityStatusPropertyName);
          if (ss != null){
            securityStatus = ((Integer)ss).intValue();
          }

          // See if the user is auto-logged in. If so, this could indicate
          // that this form is re-authenticating the user for
          // access to sensitive content
          addError =
            !getProfileTools().isAutoLoginSecurityStatus(securityStatus);
          // try to reset to securityLogin if it's not set already
          try {
            if (securityStatus != pmgr.getSecurityStatusLogin())
              prTools.setLoginSecurityStatus(profile, pRequest);
            
          }catch(RepositoryException exc){
            addFormException(new DropletException
                             (sResourceBundle.getString("errorUpdatingSecurityStatus")));
            if (isLoggingError())
              logError(exc);
          }
        }

        // Only do this if the user is not auto-logged in
        if(addError) {
          // in any event, user's already logged in...
          Object[] args = new Object[] {login};
          addFormException(new DropletException
                           (MessageFormat.format
                            (sResourceBundle.getString
                             ("invalidLoginAlreadyLoggedIn"), args)));
        }
      }else{ //submitted login/pwd do NOT match those of existing session,
        //log out and set an error

        // log out the user
        // this is the same this as handleLogout, but without the redirects
        TransactionManager tm = getTransactionManager();
        TransactionDemarcation td = getTransactionDemarcation();
        try {
          if (tm != null) td.begin(tm, TransactionDemarcation.REQUIRED);
          
          // logout without expiring the session, so that the
          // profile is replaced
          boolean expire = getExpireSessionOnLogout();
          setExpireSessionOnLogout(false);
          
          preLogoutUser(pRequest, pResponse);
  
          // don't allow the possibility of redirect here.  A failed logout
          // at this point is converted into a failed login attempt
          int status = checkFormError(null, pRequest, pResponse);
          if (status != STATUS_SUCCESS) return;
          
          postLogoutUser(pRequest, pResponse);

          // restore expire flag
          setExpireSessionOnLogout(expire);
          
          // log an error message and add form error 
          String msg = MessageFormat.format(sResourceBundle.getString
                                            ("invalidLoginDifferentUser"), (Object)null);
          DropletException dropletExc = new DropletException(msg);
    
          if (isLoggingError()){
            logError(msg);
          }

          // add an error to the form
          addFormException(dropletExc);        

         }
         catch (TransactionDemarcationException e) {
           throw new ServletException(e);
         }
         finally {
           try { if (tm != null) td.end(); }
           catch (TransactionDemarcationException e) { }
         }
      }
    }
  }

  /**
   * Profile Cookies are rebroadcast as necessary and a Profile Event
   * is fired to indicate a user was logged in.  Also, the securityStatus
   * property gets set.
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
    Boolean isLoggedIn = (Boolean)pRequest.getObjectParameter(HANDLE_LOGIN_PARAM);
    if ((isLoggedIn != null) && (isLoggedIn.booleanValue())) {
      if (isLoggingDebug())
        logDebug("handleLogin: Sending profile cookies as needed");
      CookieManager cookieManager = getProfileTools().getCookieManager();
      if ((cookieManager != null) && (cookieManager.isSendProfileCookies()) &&
          isSendCookie(getProfile()))
        cookieManager.forceProfileCookies(getProfile(), pRequest, pResponse);
      
      assumeSecurityIdentity(pRequest);
      
      if (getProfileTools().isEnableSecurityStatus()) {
        try {
          getProfileTools().setLoginSecurityStatus(getProfile(), pRequest);
        }
        catch (RepositoryException e) {
          addFormException(new DropletException("Unable to update security status", e));
          if (isLoggingError())
            logError(e);
        }
      }
      
      if (getExpiredPasswordService()!=null && getExpiredPasswordService().isEnabled()) {
        
        boolean passwordExpired = getExpiredPasswordService().isPasswordExpired(getProfile());
        
        pRequest.getSession().setAttribute("passwordexpired", new Boolean(passwordExpired));
        
        if (isLoggingDebug())
          logDebug("handleLogin: Session object is " + pRequest.getSession().toString() 
              + " " + pRequest.getSession().hashCode());
        
      }
    }
    propagateLocale(pRequest,pResponse);
    
    // don't send login event if the user did not successfully login
    if ((isLoggedIn != null) && (isLoggedIn.booleanValue())) {
      if (isLoggingDebug())
        logDebug("handleLogin: sending login event");
      
      getProfileTools().getProfileEventTrigger().sendLoginEvent
      (getProfile(), pRequest);
    }
    
    if (getClearValuesOnLogin()) {
      // Clear out form data
      handleClear(pRequest, pResponse);
    }
  }

  /**
   * A Profile Event is fired to indicate a user logged out.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void preLogoutUser(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    if (isLoggingDebug())
      logDebug("handleLogout: attempting to fire off Logout Event");

    // We'll only send a logout event in here if we're doing D4 style
    // events, because it requires the profile. Otherwise, we'll do
    // the correct thing and save the repository id so that if
    // the user DOES successfully logout, we'll fire the new-style
    // JMS event then
    if (getProfileTools().getProfileEventTrigger() != null &&
	pRequest.getNucleus().getSendD4StyleEvents()) {
      getProfileTools().getProfileEventTrigger().
	sendLogoutEvent(getProfile(), pRequest);
      // Set this to null just in case it was set originally and then
      // that user failed to logout
      mLogoutRepositoryId = null;
    }
    else
      mLogoutRepositoryId = getRepositoryId();

    revokeSecurityIdentity(pRequest);
  } // end preLogoutUser

  /**
   * Called after a user successfully logs out. This fires a 
   * logout event if the <code>logoutProfileId</code> property is 
   * set
   * @param pRequest the current request
   * @param pResponse the current response
   * @exception ServletException if an error in the code execution occurs
   * @exception IOException if there was an error with servlet IO
   **/
  public void postLogoutUser(DynamoHttpServletRequest pRequest, 
			     DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    // Only fire a logout event if the user successfully logged out,
    // and the logoutProfileId is set
    if(mLogoutRepositoryId != null && 
       getProfileTools().getProfileEventTrigger() != null) {
      if (isLoggingDebug())
	logDebug("handleLogout: attempting to fire off Logout Event");
      getProfileTools().getProfileEventTrigger().sendLogoutEvent
	(mLogoutRepositoryId,pRequest);
    }
    mLogoutRepositoryId = null;
    // After we fire the logout event, call ProfileForm which will invalidate
    // the session, since we want the logout event to occur before the session
    // ends event. This is a special case, and users should ALWAYS call
    // the super method before any custom code. 
    super.postLogoutUser(pRequest, pResponse);
  }

  /**
   * Make sure we have a Profile object, then check to see if there were errors
   * during the submit operation.  If so, redirect to the supplied error url if possible.
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
  protected int checkFormError(String pSpecificURL,
                               DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    if (getProfile() == null) {
      addFormException(new DropletException(formatUserMessage(MSG_MISSING_PROFILE, pRequest)));
    }

    return super.checkFormError(pSpecificURL,pRequest,pResponse);
  }

  /**
   * Propagate any change in the locale property to the
   * RequestLocale component.
   * @param pRequest the current request
   * @param pResponse the current response
   */
  protected void propagateLocale(DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    getProfileTools().propagateLocale
      (getProfile(), getRequestLocale(), pRequest, pResponse);
  }

  //-------------------------------------
  // property: requestLocale
  RequestLocale mRequestLocale;
  public void setRequestLocale(RequestLocale pRequestLocale) {
    mRequestLocale = pRequestLocale;
  }

  public RequestLocale getRequestLocale() {
    return mRequestLocale;
  }

  //----------- Submit: Clear ----------------
  /**
   * Resets the current form data contained in <code>value</code> that hasn't been 
   * submitted yet. Especially useful when using a session scoped form handler.
   * 
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   **/
  public boolean handleClear(DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {

    Dictionary valueDict = getValue();
    for (Enumeration e = valueDict.keys(); e.hasMoreElements();) {
        
        String propertyName = (String) e.nextElement();
        valueDict.remove(propertyName);
    }

    return super.handleCancel(pRequest, pResponse);

  }

  

  /**
   * Operation called just before the user's profile is updated which will do the following:
   * Create a new ProfileUpdateEvent object stored in mProfileUpdateEvent property
   * Collect the old values from the current repository before update occurs
   * Collect the new values from the form submitted
   * Go through each property and if value changed, save old/new values for use in ProfileUpdateEvent
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
    protected void preUpdateUser(DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse)
      throws ServletException, IOException
    {

      if(isLoggingDebug())
        logDebug("preUpdateUser");

      /* If we are supposed to be sending ProfileUpdateEvents, prepare to do so now */
      if (isGenerateProfileUpdateEvents()) {
        /* Grab the old value from the repository */
        RepositoryItem item = getProfileItem();

          /* Create a place to store the properties as we find them */
        Vector propertyUpdatesVector = new Vector();

        /* Now get the new values from the form submitted */
        Dictionary valueDict = getValue();

        /* See if we have a ProfileUpdateTrigger that is active, and fill in the event info */
	try {
	  getProfileTools().buildUpdateMessage(item, valueDict, propertyUpdatesVector, pRequest, pResponse);
	}
	catch (RepositoryException e) {
	  if (isLoggingError())
	    logError(e);
	}

        /* Stick the array of updates into the ProfileUpdateEvent object only if we found changed properties */
        if (propertyUpdatesVector.size() > 0) {
          ProfilePropertyUpdate [] propertyUpdates = new ProfilePropertyUpdate[propertyUpdatesVector.size()];
          propertyUpdatesVector.copyInto(propertyUpdates);
          mProfileUpdateEvent = new ProfileUpdateEvent();
          mProfileUpdateEvent.setPropertyUpdates(propertyUpdates);
        }
      }
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
    propagateLocale(pRequest,pResponse);  

    /* See if we have ProfileUpdateEvents turned on */
    if (isGenerateProfileUpdateEvents()) {
    /* Send the Event if we found updates */
      if (mProfileUpdateEvent != null) {
          /** Grab a copy of the updated profile to be used in the event */
          RepositoryItem pProfile = getProfileItem();
        mProfileUpdateEvent.setProfile(pProfile);
        sendProfileUpdateEvent(mProfileUpdateEvent);
      }
    }

    if (getClearValuesOnUpdate()) {
        // Clear out form data
        handleClear(pRequest, pResponse);
    }

    // Also expire any cookies if the user has changed the 
    // autoLogin property to false
    CookieManager cookieManager = getProfileTools().getCookieManager();
    if(cookieManager != null &&
       cookieManager.isSendProfileCookies() &&
       !isSendCookie(getProfile())) {
      if(isLoggingDebug())
	logDebug("User's autoLogin property is false. Expiring any cookies");
      cookieManager.expireProfileCookies(getProfile(), pRequest, pResponse);
    }
  }

  /**
   * Sends a Profile Update event to each of the current listeners passing the pEvent.
   * @param pEvent the ProfileUpdateEvent object containing the list of profile updates
   **/
  public void sendProfileUpdateEvent(ProfileUpdateEvent pEvent) {
    if (mProfileUpdateListeners != null) {
      int size = mProfileUpdateListeners.size();
      for (int i = 0; i < size; i++)
      ((ProfileUpdateListener) mProfileUpdateListeners.elementAt(i)).profileUpdate(pEvent);
    }
  }

  //----------- Submit: verifyPassword ----------------
  /**
   * Compares the value of testPassword to the profile's password
   * and redirects to the successUrl on success and gives messages
   * on failure.
   */
  public boolean handleVerifyPassword(DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    if (getTestPassword() == null || getTestPassword().trim().length() == 0) {
      addFormException(new DropletException(formatUserMessage(MSG_MISSING_PASSWORD, pRequest)));
      return true;
    }

    String loginPropertyName = 
      getProfileTools().getPropertyManager().getLoginPropertyName();
    String passwordPropertyName = 
      getProfileTools().getPropertyManager().getPasswordPropertyName();
    String login = null;
    String password;
    
    try {
      login = (String) DynamicBeans.getSubPropertyValue(getProfile(), loginPropertyName);
    }
    catch (PropertyNotFoundException e) {
      String msg = formatUserMessage(MSG_NO_SUCH_PROFILE_PROPERTY, loginPropertyName, pRequest);
      String propertyPath = generatePropertyPath(loginPropertyName);
      addFormException(new DropletFormException(msg, propertyPath, MSG_NO_SUCH_PROFILE_PROPERTY));
      if (isLoggingError())
	logError(e);      
    }

    try {
      password = (String) DynamicBeans.getSubPropertyValue(getProfile(), passwordPropertyName);
    }
    catch (PropertyNotFoundException e) {
      String msg = formatUserMessage(MSG_NO_SUCH_PROFILE_PROPERTY, passwordPropertyName, pRequest);
      String propertyPath = generatePropertyPath(passwordPropertyName);
      addFormException(new DropletFormException(msg, propertyPath, MSG_NO_SUCH_PROFILE_PROPERTY));
      if (isLoggingError())
	logError(e);      
      return true;
    }

    String testPasswordHash =
      getProfileTools().getPropertyManager().generatePassword(login, getTestPassword().trim());

    if (!testPasswordHash.equals(password)) {
      addFormException(new DropletException(formatUserMessage(MSG_INVALID_PASSWORD, pRequest)));
      return true;
    }

    pResponse.sendLocalRedirect(getVerifyPasswordSuccessURL(), pRequest);
    if (getProfileTools().isEnableSecurityStatus()) {
      try {
        getProfileTools().setLoginSecurityStatus(getProfile(),
                                                 pRequest);
      }
      catch (RepositoryException e) {
        addFormException(new DropletException(formatUserMessage(MSG_ERR_UPDATING_PROFILE,
                                                                pRequest)));
        if (isLoggingError())
          logError(e);      
      }
    }
    return false;
  }
  
  //--------------------------------------------
  /**
   * This method redirects to the value of the cancelURL property, if
   * that property is set to a non-null value.  You should override this
   * if you want to clear the form values when the form is cancelled for
   * a session scoped form handler.
   */
  public boolean handleCancel(DynamoHttpServletRequest pRequest,
              DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {

	resetFormExceptions();
	String cancel = getCancelURL();
    if (!StringUtils.isBlank(cancel)) {
      redirectOrForward(pRequest, pResponse, cancel);
      return false;
    }
    return true;
  }


  /**
   * Adds a listener to the list of "profile update" listeners.
   **/
  public synchronized
  void addProfileUpdateListener(ProfileUpdateListener pListener) {
    if (pListener != null) {
      if (mProfileUpdateListeners == null)
        mProfileUpdateListeners = new Vector();
      mProfileUpdateListeners.addElement(pListener);
    }
  }

  /**
   * Removes a listener from the list of "profile update" listeners.
   **/
  public synchronized
  void removeProfileUpdateListener(ProfileUpdateListener pListener) {
    if (mProfileUpdateListeners != null)
      mProfileUpdateListeners.removeElement(pListener);
  }

  /**
   * Assumes the user identity within the security system.  This
   * presumes that the profile attribute of the form is already
   * configured.
   */
  void assumeSecurityIdentity(DynamoHttpServletRequest pRequest)
  {
    IdentityManager identityManager =
      mUserLoginManager.getIdentityManager(pRequest);
    getProfileTools().assumeSecurityIdentity(getProfile(), identityManager);
  }

  /**
   * Performs a security system identity revocation.
   */
  void revokeSecurityIdentity(DynamoHttpServletRequest pRequest)
  {
    // see securityLoginUser() for commentary on why we don't just
    // use mUserLoginManager.logout().
    IdentityManager identityManager =
      mUserLoginManager.getIdentityManager(pRequest);
    getProfileTools().revokeSecurityIdentity(identityManager);
  }

  /**
   * Returns the value of the profile's autoLogin property
   * @param pProfile the profile to examine
   * @return true if the Profile should auto login, false otherwise
   **/
  public boolean isSendCookie(Profile pProfile) {
    ProfileTools profileTools = getProfileTools();
    if(profileTools != null) {
      Boolean autoLogin = null;
      try {
        autoLogin = profileTools.getAutoLogin(pProfile);
      }
      catch(PropertyNotFoundException exc) {
        if(isLoggingError())
          logError(exc);
      }

      if(autoLogin == null)
        return profileTools.getCookieManager().isSendProfileCookies();

      if(isLoggingDebug())
        logDebug("isSendCookie returning " +
                 autoLogin.booleanValue());
      return autoLogin.booleanValue();
    }
    return false;
  }

  
  /**
   * Method that determines if a new profile should be created
   * @param pProfileRepository the profile repository that would create
   * the new profile
   * @param pCurrentUser the current profile in the session
   * @return true if a new profile item should be created, false otherwise
   * @exception RepositoryException if there's an error determining if
   * a new user should be created
   **/
  protected boolean shouldCreateNewUser
    (MutableRepository pProfileRepository,
     RepositoryItem pCurrentUser)
    throws RepositoryException
  {
    if(isCreateNewUser() ||
       pCurrentUser == null)
      return true;

    // Also create a new user if our current user isn't the same type
    // as our createProfileType
    RepositoryItemDescriptor createProfileDesc = 
      pProfileRepository.getItemDescriptor(getCreateProfileType());

    // If our create descriptor is null, then we can't very well
    // do our comparison
    if(createProfileDesc == null)
      return false;

    // Use isInstance() if we can, otherwise just use equals()
    if(createProfileDesc instanceof ItemDescriptorImpl)
      return !((ItemDescriptorImpl)createProfileDesc).isInstance(pCurrentUser);
    return !createProfileDesc.equals(pCurrentUser.getItemDescriptor());
  }

}


