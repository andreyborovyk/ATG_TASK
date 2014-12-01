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

package atg.userprofiling;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;

import atg.core.util.ArraySorter;
import atg.core.util.ResourceUtils;
import atg.core.util.SortOrder;
import atg.core.util.StringUtils;
import atg.nucleus.ServiceException;
import atg.nucleus.ServiceMap;
import atg.nucleus.naming.ComponentName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.VirtualContextRootService;
import atg.servlet.pipeline.PipelineableServletImpl;

/**
 * This pipeline servlet performs access control for a set of 
 * request URL paths, based on the Profile object associated with 
 * the request.  The servlet is configured with a set of
 * URL-to-AccessController mappings; if the URL being accessed is
 * "guarded" by one of the AccessControllers, the request's 
 * Profile object is passed to the AccessController, which 
 * decides whether or not access should be allowed.  If access is
 * allowed, the request is passed on; if access is denied, the
 * user is redirected to a specified deniedAccessURL.
 *
 * <p>This servlet comes with a list of AccessAllowedListeners and 
 * a list of AccessDeniedListeners, to allow logging or other 
 * functions to be performed when access is granted or denied.
 * 
 * @see AccessController
 * @author Natalya Cohen
 * @version $Id: //product/DPS/version/10.0.3/Java/atg/userprofiling/AccessControlServlet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class AccessControlServlet extends PipelineableServletImpl {
  //-------------------------------------
  // Constants
  //-------------------------------------

  /** Class version string **/
  public static String CLASS_VERSION = 
  "$Id: //product/DPS/version/10.0.3/Java/atg/userprofiling/AccessControlServlet.java#2 $$Change: 651448 $";

  /** Resource bundle **/
  static final String MY_RESOURCE_NAME = "atg.userprofiling.ProfileResources";

  private static java.util.ResourceBundle sResourceBundle = 
  java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  /** Indicates the absence of an access controller in the path cache **/
  public static final String NO_ACCESS_CONTROLLER = "no_ac";

  //-------------------------------------
  // Member variables
  //-------------------------------------

  ComponentName mProfileComponentName = null;

  /** Is access control enabled? */
  boolean mEnabled = true;

  /** Should we cache results? */
  boolean mCacheResults = false;

  /** Nucleus path of the Profile object **/
  String mProfilePath = null;

  /** Cache of path/AccessController mappings **/
  Dictionary mPathCache = new Hashtable();
  
  /** List of AccessControllers used to control access to paths **/
  ServiceMap mAccessControllers;

  /** Should path comparison be case insensitive? **/
  boolean mIgnoreCaseInPath = false;

  /** Internal flag indicating whether we need to calculate mPaths **/
  boolean mRecalculatePaths = true;

  /** Compatability flag for the old way of getting path info **/
  boolean mUseContextPath = true;
  
  /** Service to resolve real path when virtual context roots are using */
  VirtualContextRootService mVirtualContextRootService;

  /**
   * Object to store the Access objects and related paths in one object
   * This is done to make comparasons with request strings simpler and work
   * with both case sensitive and non-case sensitive comparasons. The list
   * of paths are sorted by length (longest to shortest).
   */
  ControllerArrayMember [] mControllerArray;

  /** List of objects listening for "access allowed" events **/
  Vector mAccessAllowedListeners;

  /** List of objects listening for "access denied" events **/
  Vector mAccessDeniedListeners;
  
  /** 
   * Default deniedAccessURL to use if an AccessController doesn't 
   * supply one
   **/
  String mDeniedAccessURL;

  //-------------------------------------
  // Methods
  //-------------------------------------

  //----------------------------------------
  /**
   * Set property <code>UseContextPath</code>
   * @param pUseContextPath if true, use the request's context path
   * as part of the access control path. Default is true
   **/
  public void setUseContextPath(boolean pUseContextPath) {
    mUseContextPath = pUseContextPath;
  }

  /**
   * Get property <code>UseContextPath</code>
   * @return  if true, use the request's context path
   * as part of the access control path. Default is true
   **/
  public boolean isUseContextPath() {
    return mUseContextPath;
  }

  //-------------------------------------
  /**
   * Is access control enabled?
   **/
  public boolean isEnabled() {
    return mEnabled;
  }

  //-------------------------------------
  /**
   * Sets whether or not access control is enabled.
   **/
  public void setEnabled(boolean pEnabled) {
    mEnabled = pEnabled;
  }

  //-------------------------------------
  /**
   * Do we cache results when looking up AccessControllers?
   **/
  public boolean getCacheResults() {
    return mCacheResults;
  }

  //-------------------------------------
  /** 
   * Sets whether to cache results when looking up 
   * AccessControllers.
   **/
  public void setCacheResults(boolean pCacheResults) {
    mCacheResults = pCacheResults;
  }

  //-------------------------------------
  /**
   * Gets the Nucleus path of the Profile object.
   **/
  public String getProfilePath() {
    return mProfilePath;
  }

  //-------------------------------------
  /** 
   * Sets the Nucleus path of the Profile object.
   **/
  public void setProfilePath(String pProfilePath) {
    mProfilePath = pProfilePath;
    if (mProfilePath != null)
      mProfileComponentName = ComponentName.getComponentName(mProfilePath);
    else
      mProfileComponentName = null;
  }

  //-------------------------------------
  /**
   * Returns the AccessControllers used to control access to paths.
   * When a path is processed, the list of access controllers will 
   * be searched for the longest match between the access controller 
   * key and the requested path, and the corresponding controller 
   * will be used.
   **/
  public ServiceMap getAccessControllers() {
    return mAccessControllers;
  }

  //-------------------------------------
  /**
   * Sets the AccessControllers used to control access to paths.
   * When a path is processed, the list of access controllers will 
   * be searched for the longest match between the access controller 
   * key and the requested path, and the corresponding controller 
   * will be used.
   **/
  public void setAccessControllers(ServiceMap pAccessControllers) {
    mAccessControllers = pAccessControllers;
    mRecalculatePaths = true;
  }
  
  //-------------------------------------
  
  /**
   * Gets the VirtualContextRootService
   */
  public VirtualContextRootService getVirtualContextRootService() {
    return mVirtualContextRootService;
  }

  /**
   * Sets the VirtualContextRootService 
   */
  public void setVirtualContextRootService(VirtualContextRootService pVirtualContextRootService) {
    mVirtualContextRootService = pVirtualContextRootService;
  }

  //-------------------------------------
  /**
   * 
   **/
  void recalculatePaths() {
    // Grab a copy of the member variable; only update the member 
    // when we're done. This way other threads can continue access
    // while we are processing here.
    ControllerArrayMember[] controllerArray = mControllerArray;
    if (mAccessControllers == null)
      controllerArray = null;
    else {
      controllerArray = new ControllerArrayMember[mAccessControllers.size()];

      Enumeration e = mAccessControllers.keys(); 
      int i = 0;
      while (e.hasMoreElements()) {
	String path = (String) e.nextElement();
	controllerArray[i]= new ControllerArrayMember();
	controllerArray[i].setControllerObject((AccessController)mAccessControllers.get(path));
	// if we're ignoring the case in path, lower case it
	if (mIgnoreCaseInPath) 
	{
	  path = StringUtils.toLowerCase(path);
	}
	controllerArray[i].setPathName(path);
	++i;
      }

      // sort the path array from longest to shortest
      ArraySorter.sort(controllerArray, new SortOrder() {
	public int order(Object obj1, Object obj2) {
	  return ((ControllerArrayMember) obj2).mPathName.length()
		  - ((ControllerArrayMember) obj1).mPathName.length();
	}
      });
    }
    // update the member variable
    mControllerArray = controllerArray;
  }
  
  //-------------------------------------
  /**
   * Returns the AccessController which should be used to perform
   * access control for the given path.
   **/
  public AccessController getAccessController(String pRequestPath) {

    AccessController ac = null;

    // recalculate paths if necessary
    if (mRecalculatePaths) { // avoid sync for the common case
      synchronized (this) {
        if (mRecalculatePaths) { // check again now that we're synced
          recalculatePaths();
          mRecalculatePaths = false;
        }
      }
    }

    // if we're ignoring the case in path, lower case it
    if (mIgnoreCaseInPath) 
      pRequestPath = StringUtils.toLowerCase(pRequestPath);
    
    // see if the path is already in the cache
    if (mCacheResults) {
      Object obj = mPathCache.get(pRequestPath);
      if (obj != null) {
	if (obj.equals(NO_ACCESS_CONTROLLER)) 
	  return null;
	ac = (AccessController) obj;
        if (isLoggingDebug())
          logDebug("Returning cached access controller for " + pRequestPath);
        return ac;
      }
    }
    
    // not found, so go through the paths and find the access 
    // controller corresponding to the longest parent path
    String prefix = null;
    ControllerArrayMember[] controllerArray = mControllerArray;
    if (controllerArray != null) {
      ac = getAccessController(controllerArray, pRequestPath);
      
      // not found, use VirtualContextRootService to obtain real path
      if(ac == null) {
        String realPath = 
          getVirtualContextRootService().getRealServletPath(pRequestPath);
        
        // if pRequestPath is not virtual,
        // VirtualContextRootService just returns pRequestPath
        if(!realPath.equals(pRequestPath)) {
          ac = getAccessController(controllerArray, realPath);
        }
      }
    }
    
    if (isLoggingDebug()) {
      if (ac == null)
        logDebug("No access controller for " + pRequestPath);
      else
        logDebug("Found access controller for " + pRequestPath + 
		 " based on " + prefix);
    }
    
    // cache the results, if requested
    if (mCacheResults) {
      if (ac == null) {
	if (isLoggingDebug())
	  logDebug("Caching the absence of access controller for " + pRequestPath);
	mPathCache.put(pRequestPath, NO_ACCESS_CONTROLLER);
      } else {
	if (isLoggingDebug())
	  logDebug("Caching access controller for " + pRequestPath);
	mPathCache.put(pRequestPath, ac);
      }
    }

    return ac;
  }
  
  /**
   * Returns the AccessController which should be used to perform
   * access control for the given path.
   * 
   * @param pControllerArray array of ControllerArrayMember
   * @param pRequestPath request path
   * @return AccessController if found, otherwise null
   */
  public AccessController getAccessController(ControllerArrayMember[] pControllerArray, String pRequestPath) {
    AccessController ac = null;
    String prefix = null;
    for (int i = 0; i < pControllerArray.length; i++) {
      prefix = pControllerArray[i].getPathName();
      if (pRequestPath.startsWith(prefix)) {
        ac = pControllerArray[i].getControllerObject();
        break;
      }
    }
    
    return ac;
  }

  //-------------------------------------
  /**
   * Returns true if the case should be ignored when the path info is
   * compared to an access controlled path, false otherwise.
   **/
  public boolean getIgnoreCaseInPath() {
    return mIgnoreCaseInPath;
  }

  //-------------------------------------
  /**
   * Sets the flag indicating whether case should be ignored when the
   * path info is compared to an access controlled path.
   **/
  public void setIgnoreCaseInPath(boolean pIgnoreCaseInPath) {
    if (pIgnoreCaseInPath != mIgnoreCaseInPath) {
      mIgnoreCaseInPath = pIgnoreCaseInPath;
      mRecalculatePaths = true;
    }
  }

  //-------------------------------------
  /**
   * Adds a listener to the list of "access allowed" listeners.
   **/
  public synchronized 
  void addAccessAllowedListener(AccessAllowedListener pListener) {
    if (pListener != null) {
      if (mAccessAllowedListeners == null) 
	mAccessAllowedListeners = new Vector();
      mAccessAllowedListeners.addElement(pListener);
    }
  }

  //-------------------------------------
  /**
   * Removes a listener from the list of "access allowed" listeners.
   **/
  public synchronized 
  void removeAccessAllowedListener(AccessAllowedListener pListener) {
    if (mAccessAllowedListeners != null)
      mAccessAllowedListeners.removeElement(pListener);
  }

  //-------------------------------------
  /**
   * Returns the array of "access allowed" listeners.
   **/
  public synchronized AccessAllowedListener[] getAccessAllowedListeners() {
    if (mAccessAllowedListeners == null)
      return new AccessAllowedListener[0];

    AccessAllowedListener[] listeners = 
      new AccessAllowedListener[mAccessAllowedListeners.size()];
    mAccessAllowedListeners.copyInto(listeners);
    return listeners;
  }

  //-------------------------------------
  /**
   * Returns the number of "access allowed" listeners.
   **/
  public synchronized int getAccessAllowedListenerCount() {
    return ((mAccessAllowedListeners == null) ? 0 : 
	    mAccessAllowedListeners.size());
  }

  //-------------------------------------
  /**
   * Sends an "access allowed" event to all the "access allowed" 
   * listeners.
   **/
  public synchronized 
  void sendAccessAllowedEvent(AccessAllowedEvent pEvent) {
    if (mAccessAllowedListeners != null) {
      int size = mAccessAllowedListeners.size();
      for (int i = 0; i < size; i++)
	((AccessAllowedListener) mAccessAllowedListeners.elementAt(i)).
	  accessAllowed(pEvent);
    }
  }

  //-------------------------------------
  /**
   * Adds a listener to the list of "access denied" listeners.
   **/
  public synchronized 
  void addAccessDeniedListener(AccessDeniedListener pListener) {
    if (pListener != null) {
      if (mAccessDeniedListeners == null) 
	mAccessDeniedListeners = new Vector();
      mAccessDeniedListeners.addElement(pListener);
    }
  }

  //-------------------------------------
  /**
   * Removes a listener from the list of "access denied" listeners.
   **/
  public synchronized 
  void removeAccessDeniedListener(AccessDeniedListener pListener) {
    if (mAccessDeniedListeners != null)
      mAccessDeniedListeners.removeElement(pListener);
  }

  //-------------------------------------
  /**
   * Returns the array of "access denied" listeners.
   **/
  public synchronized AccessDeniedListener[] getAccessDeniedListeners() {
    if (mAccessDeniedListeners == null)
      return new AccessDeniedListener[0];

    AccessDeniedListener[] listeners = 
      new AccessDeniedListener[mAccessDeniedListeners.size()];
    mAccessDeniedListeners.copyInto(listeners);
    return listeners;
  }

  //-------------------------------------
  /**
   * Returns the number of "access denied" listeners.
   **/
  public synchronized int getAccessDeniedListenerCount() {
    return ((mAccessDeniedListeners == null) ? 0 : 
	    mAccessDeniedListeners.size());
  }

  //-------------------------------------
  /**
   * Sends an "access denied" event to all the "access denied" 
   * listeners.
   **/
  public synchronized 
  void sendAccessDeniedEvent(AccessDeniedEvent pEvent) {
    if (mAccessDeniedListeners != null) {
      int size = mAccessDeniedListeners.size();
      for (int i = 0; i < size; i++)
	((AccessDeniedListener) mAccessDeniedListeners.elementAt(i)).
	  accessDenied(pEvent);
    }
  }

  //-------------------------------------
  /** 
   * Returns the default URL to redirect to if access is denied.  
   * If an AccessController does not specify its own 
   * deniedAccessURL, this default one is used.
   **/
  public String getDeniedAccessURL() {
    return mDeniedAccessURL;
  }

  //-------------------------------------
  /** 
   * Sets the default URL to redirect to if access is denied.  If
   * an AccessController does not specify its own deniedAccessURL, 
   * this default one is used.
   **/
  public void setDeniedAccessURL(String pDeniedAccessURL) {
    mDeniedAccessURL = pDeniedAccessURL;
  }

  //-------------------------------------
  // PipelineableServletImpl overrides
  //-------------------------------------

  //-------------------------------------
  /**
   * Services a DynamoHttpServletRequest/Response pair.
   * @exception ServletException if an error occurred while 
   * processing the servlet request
   * @exception IOException if an error occurred while reading or 
   * writing the servlet request
   **/
  public void service(DynamoHttpServletRequest pRequest,
		      DynamoHttpServletResponse pResponse)
       throws IOException, ServletException 
  {
    String path = "";

    // Only use the context path if we have this flag set, since we
    // don't want to cause a behavior change for existing users
    if(mUseContextPath)
      path = pRequest.getContextPath();

    // BEA sets the servlet path instead of path info, so make sure that it's
    // part of the equation
    if(pRequest.getServletPath() != null)
      path = path + pRequest.getServletPath();
    if(pRequest.getPathInfo() != null)
      path = path + pRequest.getPathInfo();    
      
    AccessController ac = null;
    Profile profile = null;
    String profileId = null;
    boolean allowed = false;

    if (isLoggingDebug())
      logDebug("Servicing " + path);
    
    // determine whether access should be allowed or denied
    if (!mEnabled)
      allowed = true;
    else {
      if (isLoggingDebug())
        logDebug("Performing access control on " + path);

      ac = getAccessController(path);

      // if path is not protected, all users are allowed access
      if (ac == null) {
	if (isLoggingDebug())
	  logDebug("Access to " + path + " allowed to all users");
	allowed = true;
      } 

      // otherwise, perform access control based on the Profile
      else {
        Object obj = pRequest.resolveName(mProfileComponentName);
        if (!(obj instanceof Profile)) {
          if (obj == null) {
            // The profile will be null if the path is for a mime type that is
            // ignored by the pipeline servlet that starts new sessions.
            // Assume that's what happened and don't log an error.
            if (isLoggingDebug()) {
              logDebug("Profile resolved to null");
            }
          }
          else if (isLoggingError()) {
            Object[] args = { obj };
            logError(ResourceUtils.getMsgResource("illegalProfile", MY_RESOURCE_NAME, 
                                                  sResourceBundle, args));
          }
          allowed = false;
        } else {
	  profile = (Profile) obj;
	  if (isLoggingDebug()) {
	    profileId = profile.getRepositoryId();
	    logDebug("Performing access control on " + path + 
		     " for user " + profileId);
	  }
	  allowed = ac.allowAccess(profile, pRequest);
	}
      }
    }
    
    // access allowed: pass the request through
    if (allowed) {
      if (isLoggingDebug()) {
	if (profileId == null)
	  logDebug("Allowing access to " + path);
	else 
	  logDebug("Allowing access to " + path + " for user " + profileId);
      }

      if (mAccessAllowedListeners != null) {
        if (profile == null)
          profile = (Profile) pRequest.resolveName(mProfileComponentName);
	sendAccessAllowedEvent(new AccessAllowedEvent(path, profile));
      }

      passRequest(pRequest, pResponse);
    } 

    // access denied: redirect to the deniedAccessURL
    else {
      if (isLoggingDebug()) {
        if (profileId == null) 
          logDebug("Denying access to " + path);
        else 
          logDebug("Denying access to " + path + " for user " + profileId);
      }

      // if deniedAccessURL cannot be found, log the error and 
      // redirect back to the top of the site
      String url = null;
      // (we know ac is not null, since allowed = false)
      if (profile != null) {
        url = ac.getDeniedAccessURL(profile);
      }
      if (url == null) {
        url = mDeniedAccessURL;
      }
      if (url == null) {
        url = "/";
        if (profile == null) {
          if (isLoggingDebug()) {
            logDebug(ResourceUtils.getMsgResource("noDeniedAccessURL", 
                                                   MY_RESOURCE_NAME, sResourceBundle));
          }
        }
        else if (isLoggingError()) {
          logError(ResourceUtils.getMsgResource("noDeniedAccessURL", 
                                                MY_RESOURCE_NAME, sResourceBundle));
        }
      }

      if (mAccessDeniedListeners != null) {
        if (profile == null)
          profile = (Profile) pRequest.resolveName(mProfileComponentName);
        sendAccessDeniedEvent(new AccessDeniedEvent(path, profile, url));
      }
      
      // transform denied URL into virtual URL
      String transformedURL = 
        getVirtualContextRootService().transformRealURLToVirtualURL(path, url);

      if (transformedURL!=null) 
        url = transformedURL;

      if (url.startsWith("http:") || url.startsWith("https:"))
        pResponse.sendRedirect(url);
      else
        pResponse.sendLocalRedirect(url, pRequest);
    }
  }

  //-------------------------------------
  // GenericService overrides
  //-------------------------------------

  //-------------------------------------
  /**
   * Called after the service has been created, placed into the 
   * naming hierarchy, and initialized with its configured property
   * values.  Makes sure all the required properties have been set 
   * in the .properties file.
   *
   * @exception ServiceException if the service had a problem 
   * starting up
   **/
  public void doStartService() throws ServiceException {
    if (mProfilePath == null) 
      throw new ServiceException
	(ResourceUtils.getMsgResource("noProfilePath", MY_RESOURCE_NAME, 
				      sResourceBundle));
  }

  //-------------------------------------

  private static class ControllerArrayMember
  {
    //MEMBER FIELDS
    /**
     * Name of the path that is controlled by the AccessControler member
     **/
    private String mPathName=null;

    /**
     * The AccesssControler member that protects the path named in the
     * mPathName member
     **/
    private AccessController mControllerObject=null;

    ///METHODS
    /**
     * Constructor for this class.  This constructor just sets the internal refrences
     * using the paramers passed to it.
     */
    ControllerArrayMember(){}

    /**
     * Get the AccessController member
     */
    AccessController getControllerObject()
    {
      return mControllerObject;
    }

    /**
     * Get the path name member
     */
    String getPathName()
    {
      return mPathName;
    }

    /**
     * Set the path name member
     */
    void setPathName(String pPathName)
    {
      mPathName=pPathName;
    }

    /**
     * Set the AccesssController member
     */
    void setControllerObject(AccessController pControllerObject)
    {
      mControllerObject=pControllerObject;
    }
  }

}
