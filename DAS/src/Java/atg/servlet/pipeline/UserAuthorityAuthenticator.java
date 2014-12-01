// UserAuthorityAuthenticator.java
//
// <ATGCOPYRIGHT>
// Copyright (C) 1999-2011 Art Technology Group, Inc.
// All Rights Reserved.  No use, copying or distribution of this
// work may be made except in accordance with a valid license
// agreement from Art Technology Group.  This notice must be
// included on all copies, modifications and derivatives of this
// work.
// </ATGCOPYRIGHT>

package atg.servlet.pipeline;

import java.text.MessageFormat;
import java.util.*;
import atg.nucleus.*;
import atg.security.*;
import atg.servlet.*;

/**
 * An authenticator that authenticates a user via a Login User Authority
 * service and sets up the session User object appropriately.<p>
 *
 * This will also bind the User object (either from the session or
 * the temporary that is allocated if no User object is specified
 * by the "user" property) to the current thread.  This emulates the
 * job of atg.servlet.security.ThreadUserBinderServlet, binding
 * the User object used for authentication to the thread.<p>
 *
 * This servlet depends on its caller (usually
 * atg.servlet.pipeline.BasicAuthenticationPipelineServlet) to unbind
 * the User object from the thread, as it is unable to do so upon
 * completion of the request.<p>
 *
 * If lazyAuthentication is set to true, this servlet will avoid
 * re-authentication (ie ignore the name/password information) if the
 * existing session identity is allowed access.  This may be useful
 * if the authenticator is slow.<p>
 *
 * @see atg.security.UserAuthority
 * @see atg.security.LoginUserAuthority
 * @see atg.servlet.pipeline.BasicAuthenticationPipelineServlet
 *
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/servlet/pipeline/UserAuthorityAuthenticator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class UserAuthorityAuthenticator
  extends GenericService
  implements Authenticator
{
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/servlet/pipeline/UserAuthorityAuthenticator.java#2 $$Change: 651448 $";

  static final ResourceBundle sResources = ResourceBundle.getBundle("atg.servlet.pipeline.UserAuthorityAuthenticatorResources", atg.service.dynamo.LangLicense.getLicensedDefault());

  static final String BAD_ACCOUNT = sResources.getString("badAccount");
  static final String ALLOWED_ACCOUNT = sResources.getString("allowedAccount");

  /**
   * The user authority used for authentication.
   */
  private LoginUserAuthority mUserAuthority;


  /**
   * A list of URI prefixes, for which this servlet will not attempt
   * to look up mUserPath in nucleus.
   *
   * <p> This is used to avoid the Nucleus user lookup creating a session 
   * when downloading, for example, ACC components, which can use up all of 
   * the sesssion when using eval licenses.
   */
  private String[] mNoNucleusUserUriPrefixes = null;

  /**
   * Set of account names that are allowed access.  This will be converted
   * into a persona set on first authentication attempt.
   */
  private String[] mAllowedAccountNames;

  /**
   * True if the set of allowed account names has changed.
   */
  private boolean mDirty = true;

  /**
   * Set of personae (user, group, or privilege accounts) that are
   * allowed access.
   */
  private Persona[] mAllowedPersonae;

  /**
   * The Nucleus path to the session User object.
   */
  private String mUserPath;

  /**
   * If true, reauthentication is done every time.  If false,
   * authentication is only performed if it hasn't been done or if
   * it was done but the identity is not allowed access.
   */
  private boolean mLazyAuthentication;

  ////////////////////////
  // Property accessors //
  ////////////////////////

  /**
   * Gets the list of URI prefixes, for which this servlet will not attempt
   * to look up mUserPath in nucleus.
   */
  public String[] getNoNucleusUserUriPrefixes() {
    return mNoNucleusUserUriPrefixes;
  }

  /**
   * Sets the list of URI prefixes, for which this servlet will not attempt
   * to look up mUserPath in nucleus.
   */
  public void setNoNucleusUserUriPrefixes(String[] pVal) {
    mNoNucleusUserUriPrefixes = pVal;
  }

  /**
   * Returns the list of accounts (users, groups, or privilege groups) that
   * are allowed access.
   */
  public String[] getAllowedAccounts()
  {
    return mAllowedAccountNames;
  }

  /**
   * Changes the list of accounts (users, groups, or privilege groups) that
   * are allowed access.
   */
  public void setAllowedAccounts(String[] pAccountNames)
  {
    mAllowedAccountNames = pAccountNames;
    mDirty = true;
  }

  /**
   * Returns true if authentication is performed only if the session
   * identity information does not already allow access.  This is
   * useful if the authentication process is slow.
   */
  public boolean getLazyAuthentication()
  {
    return mLazyAuthentication;
  }

  /**
   * Set to true if you would like authentication to be performed only
   * if the session identity information does not already allow access.
   * This is useful if the authentication process is slow.
   */
  public void setLazyAuthentication(boolean pLazyAuthentication)
  {
    mLazyAuthentication = pLazyAuthentication;
  }

  /**
   * Returns the Nucleus path to the User object that will contain
   * the identity information once authenticated.  This path should
   * normally point to the User object from the session.
   */
  public String getUserPath()
  {
    return mUserPath;
  }

  /**
   * Changes the Nucleus path to the User object that will contain
   * the identity information once authenticated.  This path should
   * normally point to the User object from the session.
   */
  public void setUserPath(String pUserPath)
  {
    mUserPath = pUserPath;
  }

  /**
   * Returns the user authority to be used for name/password verification.
   */
  public LoginUserAuthority getUserAuthority()
  {
    return mUserAuthority;
  }

  /**
   * Changes the user authority to be used for name/password verification.
   */
  public void setUserAuthority(LoginUserAuthority pNewAuthority)
  {
    mUserAuthority = pNewAuthority;
  }

  /////////////
  // Methods //
  /////////////

  private synchronized void updatePersonae()
  {
    if (mAllowedAccountNames == null) {
      mAllowedPersonae = null;
      mDirty = false;
      return;
    }

    // look up each of the personae that were specified
    ArrayList personaeList = new ArrayList(7);
    boolean setDirty = false;
    for (int i = 0; i < mAllowedAccountNames.length; i++) {
      String allowedAccount = mAllowedAccountNames[i];
      Persona persona = mUserAuthority.getPersona(allowedAccount);
      if (persona == null) {
        if (isLoggingError())
          logError(MessageFormat.format(BAD_ACCOUNT, allowedAccount));
        /*
         * Don't cache this information if we received any invalid values.
         * This may mean the database is down so if we cache this info, 
         * we'll never figure out it is OK again when the database returns.
         *
         * We probably ought to not check on every request since if the
         * database is down, this will be a very slow test.  For now, though,
         * this will fix the bug, but may cause things to be really slow
         * if the database is down.
         */
        setDirty = true;
        continue;
      }
      if (isLoggingInfo())
        logInfo(MessageFormat.format(ALLOWED_ACCOUNT, allowedAccount));
      personaeList.add(persona);
    }

    // convert to an array for faster lookup later
    mAllowedPersonae = new Persona[personaeList.size()];
    personaeList.toArray(mAllowedPersonae);

    // ready to rock and roll
    mDirty = setDirty;
  }

  /**
   * Returns true if the user has one of the identities that is allowed
   * access.
   */
  protected boolean checkAccess(User pUser)
  {
    if (pUser == null) // no user object, no access
      return false;

    if (mDirty) // update the cache of personae if necessary
      updatePersonae();

    // see if the user is allowed to administer Dynamo
    Persona[] userPersonae = pUser.getPersonae(mUserAuthority);
    if (mAllowedPersonae == null) // no restrictions
      return true;
    for (int i = 0; i < userPersonae.length; i++) {
      for (int j = 0; j < mAllowedPersonae.length; j++) {
        if (userPersonae[i].hasPersona(mAllowedPersonae[j]))
          return true;
      }
    }
    return false; // nope
  }

  ////////////////////////////////////////////
  // Authenticator interface implementation //
  ////////////////////////////////////////////

  /**
   * Returns true if the specified userid and password are an
   * authentic combination, false if not.
   */
  public boolean authenticate (String pUserId,
                               String pPassword)
  {
    if (mUserAuthority == null)
      return false; // nobody to ask?  sorry, charlie.

    User user = null;
    // attempt to resolve the session User object
    if (mUserPath != null) {
      boolean bSkip = false;
      if ( mNoNucleusUserUriPrefixes != null ) {
        String requestURI = ServletUtil.getCurrentRequest().getRequestURI();
        for (int i=0; i<mNoNucleusUserUriPrefixes.length; i++) {
          if (requestURI.startsWith(mNoNucleusUserUriPrefixes[i])) {
            bSkip = true;
            break;
          }
        }
      }

      if (!bSkip) {
        user = (User)ServletUtil.getCurrentRequest().resolveName(mUserPath);
        if (isLoggingDebug())
          logDebug((user == null ? "No" : "Resolved") + " User object from " + mUserPath);
      }
    }
    
    // for backwards compatibility, or for perfect adherence
    // to the HTTP authentication spec, if there is no specified
    // User object then we authenticate against a dummy object
    // with every request rather than remembering the
    // old identity
    if (user == null)
      user = new User();

    // bind identity object to thread
    ThreadSecurityManager.setThreadUser(user);

    // Lazy authentication: If the user has already been authenticated, and
    // we don't care to re-authenticate every time, let
    // them pass.
    if (mLazyAuthentication) {
      if (checkAccess(user)) {
        if (isLoggingDebug())
          logDebug("Lazily authenticated user");
        return true;
      }
      else if (isLoggingDebug())
        logDebug("Lazy authentication enabled, but failed");
    }
    else { // clear out any previous auth info
      try {
        user.setPersonae(mUserAuthority, null);
      }
      catch (InvalidPersonaException e) {} // can't happen
    }

    // Perform a User Authority login procedure.
    PasswordHasher hasher = mUserAuthority.getPasswordHasher();
    String hashedPassword;
    if (hasher instanceof PasswordHasher2)
      hashedPassword = ((PasswordHasher2)hasher).hashPasswordForLogin(pUserId, pPassword);
    else
      hashedPassword = hasher.hashPasswordForLogin(pPassword);
    boolean success = mUserAuthority.login(user, pUserId, hashedPassword,
                                           hasher.getPasswordHashKey());
    if (!success) // authentication failed outright
      return false;

    return checkAccess(user);
  }
}
