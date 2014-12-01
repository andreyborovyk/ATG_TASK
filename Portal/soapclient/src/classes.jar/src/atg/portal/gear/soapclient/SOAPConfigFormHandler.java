/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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

package atg.portal.gear.soapclient;

// Java classes
import java.io.*;
import javax.servlet.ServletException;
import java.util.*;

// Portal classes
import atg.portal.framework.GearEnvironment;

// DAS classes
import atg.droplet.TransactionalFormHandler;
import atg.droplet.DropletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;
import atg.servlet.UploadedFile;
import atg.core.util.StringUtils;
import atg.repository.*;
import atg.service.wsdl.*;
import atg.userdirectory.User;
import atg.userprofiling.ProfileTools;
import atg.repository.RepositoryItem;
import atg.core.util.ResourceUtils;



// DPS classes

// DSS classes

// DCS classes

/**
 *
 * @author Ashley J. Streb
 * @version $Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/portal/gear/soapclient/SOAPConfigFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class SOAPConfigFormHandler
  extends TransactionalFormHandler
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/portal/gear/soapclient/SOAPConfigFormHandler.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  static final String CLIENT_RESOURCE_BUNDLE_NAME = 
    "atg.portal.gear.soapclient.UserResources";

  static final String SERVER_RESOURCE_BUNDLE_NAME = 
    "atg.portal.gear.soapclient.ServerResources";

  protected static ResourceBundle sServerResources =
    ResourceBundle.getBundle(SERVER_RESOURCE_BUNDLE_NAME, 
                             Locale.getDefault()); 

  /////////////////////////////////////////////////////////
  // Client Resource Keys                                //
  /////////////////////////////////////////////////////////
  static final String NULL_WSDL_DOCUMENT = "nullWSDLDocument";
  static final String UNABLE_TO_PROCESS_REQUEST = "unableToProcessRequest";
  static final String INVALID_PROPERTY_TYPE = "invalidPropertyType";
  static final String INVALID_NUMBER_METHODS = "invalidNumberMethods";
  static final String UNABLE_TO_PROCESS_WSDL= "unableToProcessWSDL";



  /////////////////////////////////////////////////////////
  // Server Resource Keys                                //
  /////////////////////////////////////////////////////////
  static final String INVALID_MODE_CONFIG = "invalidModeConfig";
  static final String NULL_GEAR_DEAF_ID = "nullGearDefId";
  static final String NULL_GEAR_ID = "nullGearId";
  static final String NULL_USER_ID = "nullUserId";
  static final String NO_INSTALL_CONFIG = "noInstallConfig";
  static final String NO_SERV_PARAMS = "noServParams";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------


  //---------------------------------------------------------------------
  // property: propertyManager
  SOAPPropertyManager mPropertyManager;

  /**
   * Return the propertyManager property.
   * @return
   */
  public SOAPPropertyManager getPropertyManager() {
    return mPropertyManager;
  }

  /**
   * Set the propertyManager property.
   * @param pPropertyManager
   */
  public void setPropertyManager(SOAPPropertyManager pPropertyManager) {
    mPropertyManager = pPropertyManager;
  }

  //-------------------------------------
  // property: values
  Map mParameterValues;

  public void setParameterValues(Map pParameterValues) {
    mParameterValues = pParameterValues;
  }
  public Map getParameterValues() {
    if (mParameterValues == null)
      mParameterValues = new HashMap(23);
    return mParameterValues;
  }

  //---------------------------------------------------------------------
  // property: setServiceParametersSuccessURL
  String mSetServiceParametersSuccessURL;

  /**
   * Return the setServiceParametersSuccessURL property.
   * @return
   */
  public String getSetServiceParametersSuccessURL() {
    return mSetServiceParametersSuccessURL;
  }

  /**
   * Set the setServiceParametersSuccessURL property.
   * @param pSetServiceParametersSuccessURL
   */
  public void setSetServiceParametersSuccessURL(String pSetServiceParametersSuccessURL) {
    mSetServiceParametersSuccessURL = pSetServiceParametersSuccessURL;
  }

  
  //---------------------------------------------------------------------
  // property: setServiceParametersErrorURL
  String mSetServiceParametersErrorURL;

  /**
   * Return the setServiceParametersErrorURL property.
   * @return
   */
  public String getSetServiceParametersErrorURL() {
    return mSetServiceParametersErrorURL;
  }

  /**
   * Set the setServiceParametersErrorURL property.
   * @param pSetServiceParametersErrorURL
   */
  public void setSetServiceParametersErrorURL(String pSetServiceParametersErrorURL) {
    mSetServiceParametersErrorURL = pSetServiceParametersErrorURL;
  }

  

  //---------------------------------------------------------------------
  // property: userId
  String mUserId;

  /**
   * Return the userId property.
   * @return
   */
  public String getUserId() {
    if (mUserId != null)
        return mUserId;
    else {	      
      if (mUserLogin == null) {
        return null;
      } // end of if ()
	  
      RepositoryItem profileItem = (RepositoryItem)getProfileTools().getItem(mUserLogin, null);
      if ( profileItem != null) {
        return profileItem.getRepositoryId();
      } // end of if ()
      return null;  
    }
  }

  /**
   * Set the userId property.
   * @param pUserId
   */
  public void setUserId(String pUserId) {
    mUserId = pUserId;
  }


  //---------------------------------------------------------------------
  // property: user
  String mUserLogin;

  /**
   * Return the user property.
   * @return
   */
  public String getUserLogin() {
    return mUserLogin;
  }

  /**
   * Set the user property.
   * @param pUser
   */
  public void setUserLogin(String pUserLogin) {
      
    mUserLogin = pUserLogin;
   
  }

  /** Profile tools */
  ProfileTools mProfileTools;

  //-------------------------------------
  /**
   * Sets Profile tools
   **/
  public void setProfileTools(ProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }

  //-------------------------------------
  /**
   * Returns Profile tools
   **/
  public ProfileTools getProfileTools() {
    return mProfileTools;
  }


  //---------------------------------------------------------------------
  // property: gearId
  String mGearId;

  /**
   * Return the gearId property.
   * @return
   */
  public String getGearId() {
    return mGearId;
  }

  /**
   * Set the gearId property.
   * @param pGearId
   */
  public void setGearId(String pGearId) {
    mGearId = pGearId;
  }

  
  //---------------------------------------------------------------------
  // property: gearDefId
  String mGearDefId;

  /**
   * Return the gearDefId property.
   * @return
   */
  public String getGearDefId() {
    return mGearDefId;
  }

  /**
   * Set the gearDefId property.
   * @param pGearDefId
   */
  public void setGearDefId(String pGearDefId) {
    mGearDefId = pGearDefId;
  }

  
  
  //---------------------------------------------------------------------
  // property: WSDLTools
  WSDLSOAPTools mWSDLTools;

  /**
   * Return the WSDLTools property.
   * @return
   */
  public WSDLSOAPTools getWSDLTools() {
    return mWSDLTools;
  }

  /**
   * Set the WSDLTools property.
   * @param pWSDLTools
   */
  public void setWSDLTools(WSDLSOAPTools pWSDLTools) {
    mWSDLTools = pWSDLTools;
  }

  

  //---------------------------------------------------------------------
  // property: WSDLFile
  UploadedFile mWSDLFile;

  /**
   * Return the WSDLFile property.
   * @return
   */
  public UploadedFile getWSDLFile() {
    return mWSDLFile;
  }

  /**
   * Set the WSDLFile property.
   * @param pWSDLFile
   */
  public void setWSDLFile(UploadedFile pWSDLFile) {
    mWSDLFile = pWSDLFile;
  }

  
  //---------------------------------------------------------------------
  // property: Repository
  MutableRepository mRepository;

  /**
   * Return the Repository property.
   * @return
   */
  public MutableRepository getRepository() {
    return mRepository;
  }

  /**
   * Set the Repository property.
   * @param pRepository
   */
  public void setRepository(MutableRepository pRepository) {
    mRepository = pRepository;
  }

  
  //---------------------------------------------------------------------
  // property: RepositoryUtils
  SOAPRepositoryUtils mRepositoryUtils;

  /**
   * Return the RepositoryUtils property.
   * @return
   */
  public SOAPRepositoryUtils getRepositoryUtils() {
    return mRepositoryUtils;
  }

  /**
   * Set the RepositoryUtils property.
   * @param pRepositoryUtils
   */
  public void setRepositoryUtils(SOAPRepositoryUtils pRepositoryUtils) {
    mRepositoryUtils = pRepositoryUtils;
  }

  
  
  //---------------------------------------------------------------------
  // property: username
  String mUsername;

  /**
   * Return the username property.
   * @return
   */
  public String getUsername() {
    return mUsername;
  }

  /**
   * Set the username property.
   * @param pUsername
   */
  public void setUsername(String pUsername) {
    mUsername = pUsername;
  }

  
  //---------------------------------------------------------------------
  // property: password
  String mPassword;

  /**
   * Return the password property.
   * @return
   */
  public String getPassword() {
    return mPassword;
  }

  /**
   * Set the password property.
   * @param pPassword
   */
  public void setPassword(String pPassword) {
    mPassword = pPassword;
  }

  
  //---------------------------------------------------------------------
  // property: instanceConfiguration
  boolean mInstanceConfiguration = false;

  /**
   * Return the instanceConfiguration property.
   * @return
   */
  public boolean isInstanceConfiguration() {
    return mInstanceConfiguration;
  }

  /**
   * Set the instanceConfiguration property.
   * @param pInstanceConfiguration
   */
  public void setInstanceConfiguration(boolean pInstanceConfiguration) {
    mInstanceConfiguration = pInstanceConfiguration;
  }

  
  //---------------------------------------------------------------------
  // property: installConfiguration
  boolean mInstallConfiguration = false;

  /**
   * Return the installConfiguration property.
   * @return
   */
  public boolean isInstallConfiguration() {
    return mInstallConfiguration;
  }

  /**
   * Set the installConfiguration property.
   * @param pInstallConfiguration
   */
  public void setInstallConfiguration(boolean pInstallConfiguration) {
    mInstallConfiguration = pInstallConfiguration;
  }

  
  //---------------------------------------------------------------------
  // property: userConfiguration
  boolean mUserConfiguration = false;

  /**
   * Return the userConfiguration property.
   * @return
   */
  public boolean isUserConfiguration() {
    return mUserConfiguration;
  }

  /**
   * Set the userConfiguration property.
   * @param pUserConfiguration
   */
  public void setUserConfiguration(boolean pUserConfiguration) {
    mUserConfiguration = pUserConfiguration;
  }

  
  //---------------------------------------------------------------------
  // property: uploadWSDLSuccessURL
  String mUploadWSDLSuccessURL;

  /**
   * Return the uploadWSDLSuccessURL property.
   * @return
   */
  public String getUploadWSDLSuccessURL() {
    return mUploadWSDLSuccessURL;
  }

  /**
   * Set the uploadWSDLSuccessURL property.
   * @param pUploadWSDLSuccessURL
   */
  public void setUploadWSDLSuccessURL(String pUploadWSDLSuccessURL) {
    mUploadWSDLSuccessURL = pUploadWSDLSuccessURL;
  }

  
  //---------------------------------------------------------------------
  // property: uploadWSDLErrorURL
  String mUploadWSDLErrorURL;

  /**
   * Return the uploadWSDLErrorURL property.
   * @return
   */
  public String getUploadWSDLErrorURL() {
    return mUploadWSDLErrorURL;
  }

  /**
   * Set the uploadWSDLErrorURL property.
   * @param pUploadWSDLErrorURL
   */
  public void setUploadWSDLErrorURL(String pUploadWSDLErrorURL) {
    mUploadWSDLErrorURL = pUploadWSDLErrorURL;
  }

  

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  public void preSetServiceParameters(DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
  }

  public void postSetServiceParameters(DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
  }

  public boolean handleSetServiceParameters(DynamoHttpServletRequest pRequest,
                                            DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
    if (isLoggingDebug())
      logDebug("In handleSetServiceParameters method");

    if (!checkFormRedirect(null, getSetServiceParametersErrorURL(), 
                           pRequest, pResponse))
      return false;
    
    preSetServiceParameters(pRequest, pResponse);

    if (!checkFormRedirect(null, getSetServiceParametersErrorURL(), 
                           pRequest, pResponse))
      return false;
    
    setServiceParameters(pRequest, pResponse);

    if (!checkFormRedirect(null, getSetServiceParametersErrorURL(), 
                           pRequest, pResponse))
      return false;
    
    postSetServiceParameters(pRequest, pResponse);

    return checkFormRedirect (getSetServiceParametersSuccessURL(), 
                              getSetServiceParametersErrorURL(), 
                              pRequest, pResponse);
  }

  protected void setServiceParameters(DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse)
  {
    try {
      if (! isValidModeConfiguration()) {
        if (isLoggingError()) {
          String msg = ResourceUtils.getMsgResource(INVALID_MODE_CONFIG,
                                                    SERVER_RESOURCE_BUNDLE_NAME,
                                                    sServerResources);
          logError(msg);
        }
        addFormException(UNABLE_TO_PROCESS_REQUEST, pRequest, pResponse);
        return;
      }
      
      if (getParameterValues() == null || getParameterValues().size() == 0) {
        return;
      }
      
      if (isUserConfiguration()) {
        String gearDefId = getGearDefId();
        String userId = getUserId();
        String gearId = getGearId();
        if (StringUtils.isBlank(gearDefId)) {
          if (isLoggingError()) {
            String msg = ResourceUtils.getMsgResource(NULL_GEAR_DEAF_ID,
                                                      SERVER_RESOURCE_BUNDLE_NAME,
                                                      sServerResources);
            logError(msg);
          }
          addFormException(UNABLE_TO_PROCESS_REQUEST, pRequest, pResponse);
          return;
        }
        if (StringUtils.isBlank(userId)) {
          if (isLoggingError()) {
            String msg = ResourceUtils.getMsgResource(NULL_USER_ID,
                                                      SERVER_RESOURCE_BUNDLE_NAME,
                                                      sServerResources);
            logError(msg);
          }
          addFormException(UNABLE_TO_PROCESS_REQUEST, pRequest, pResponse);
          return;
        }
        if (StringUtils.isBlank(gearId)) {
          if (isLoggingError()) {
            String msg = ResourceUtils.getMsgResource(NULL_GEAR_ID,
                                                      SERVER_RESOURCE_BUNDLE_NAME,
                                                      sServerResources);
            logError(msg);
          }
          addFormException(UNABLE_TO_PROCESS_REQUEST, pRequest, pResponse);
          return;
        }
        setUserServiceParams(getParameterValues(), gearDefId, gearId, userId,
                             pRequest, pResponse);
      }
    }
    catch (RepositoryException re) {
      if (isLoggingError())
        logError(re);
      addFormException(UNABLE_TO_PROCESS_REQUEST, pRequest, pResponse);
    }
  }
  
  protected void setUserServiceParams(Map pServiceParamValues,
                                      String pGearDefId,
                                      String pGearId,
                                      String pUserId,
                                      DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse) 
    throws RepositoryException
  {
    SOAPPropertyManager propMgr = getPropertyManager();

    RepositoryItem userConfig = 
      getRepositoryUtils().getUserServiceConfig(pGearDefId, pGearId, pUserId);
    
    if (userConfig == null) {
      // create the new userServiceConfig item from the existing 
      // installserviceConfigItem.
      userConfig = createUserServiceConfigFromInstallServiceConfig(pGearDefId,
                                                                   pGearId,
                                                                   pUserId,
                                                                   pServiceParamValues,
                                                                   pRequest,
                                                                   pResponse);
    }

    MutableRepositoryItem serviceConfig = (MutableRepositoryItem)
      userConfig.getPropertyValue(propMgr.getServiceConfigPropertyName());
    Collection parameters = (Collection)
      serviceConfig.getPropertyValue(propMgr.getServiceParametersPropertyName());
    setServiceParams(parameters, pServiceParamValues);
  }
  
  protected void setServiceParams(Collection pParameters,
                                  Map pServiceParamValues)
    throws RepositoryException
  {
    String paramName;
    SOAPPropertyManager propMgr = getPropertyManager();

    if (pParameters == null || pParameters.size() == 0 ||
        pServiceParamValues == null || pServiceParamValues.size() == 0) {
      return;
    }
    
    Iterator paramIterator = pParameters.iterator();
    while (paramIterator.hasNext()) {
      MutableRepositoryItem param = (MutableRepositoryItem)
        paramIterator.next();
      paramName  = (String)
        param.getPropertyValue(propMgr.getParamNamePropertyName());
      if (pServiceParamValues.containsKey(paramName)) {
        param.setPropertyValue(propMgr.getParamValuePropertyName(),
                               pServiceParamValues.get(paramName));
        pServiceParamValues.remove(paramName);
        getRepository().updateItem(param);
      }
    }
  }
  
  public void preUploadWSDL(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
  }

  public void postUploadWSDL(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
  }

  public boolean handleUploadWSDL(DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
    if (isLoggingDebug())
      logDebug("In handleUploadWSDL method");

    if (!checkFormRedirect(null, getUploadWSDLErrorURL(), 
                           pRequest, pResponse))
      return false;
    
    preUploadWSDL(pRequest, pResponse);

    if (!checkFormRedirect(null, getUploadWSDLErrorURL(), 
                           pRequest, pResponse))
      return false;
    
    uploadWSDL(pRequest, pResponse);

    if (!checkFormRedirect(null, getUploadWSDLErrorURL(), 
                           pRequest, pResponse))
      return false;
    
    postUploadWSDL(pRequest, pResponse);

    return checkFormRedirect (getUploadWSDLSuccessURL(), 
                              getUploadWSDLErrorURL(), 
                              pRequest, pResponse);
  }
  
  protected void uploadWSDL(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
    throws IOException
  {
    String wsdlFile;
    
    if (getWSDLFile() == null) {
      addFormException(NULL_WSDL_DOCUMENT, pRequest, pResponse);
      return;
    }

    if (getGearDefId() == null) {
      if (isLoggingError()) {
        String msg = ResourceUtils.getMsgResource(NULL_GEAR_DEAF_ID,
                                                  SERVER_RESOURCE_BUNDLE_NAME,
                                                  sServerResources);
        logError(msg);
      }
      addFormException(UNABLE_TO_PROCESS_REQUEST, pRequest, pResponse);
      return;
    }

    //BEA Workaround
    //See bug 66024
    pRequest.setAttribute(atg.portal.framework.RequestAttributes.GEAR_DEFINITION,getGearDefId());

    if (! isValidModeConfiguration()) {
      if (isLoggingError()) {
        String msg = ResourceUtils.getMsgResource(INVALID_MODE_CONFIG,
                                                  SERVER_RESOURCE_BUNDLE_NAME,
                                                  sServerResources);
        logError(msg);
      }
      addFormException(UNABLE_TO_PROCESS_REQUEST, pRequest, pResponse);
      return;
    }

    try {
      if (isExistingWSDLDocument()) {
        if ( isLoggingDebug()) {
          logDebug("WSDL doc already exists for gear: " + getGearDefId() );
        } 

        getRepositoryUtils().removeInstallServiceConfiguration(getGearDefId());
        getRepositoryUtils().deleteUserServiceParameters(getGearDefId());
      }
      
      wsdlFile = getWSDLFileAsString();
      if (wsdlFile == null) {
        addFormException(NULL_WSDL_DOCUMENT, pRequest, pResponse);
        return;
      }
      WSDLSOAPInfo wsdlInfo = getWSDLTools().getWSDLInfo(wsdlFile);
      
      if (isInstallConfiguration()) {
        String gearDefId = getGearDefId();
        createInstallConfiguration(wsdlInfo, gearDefId);
      }
    }
    catch (InvalidPropertyTypeException ipte) {
      addFormException(INVALID_PROPERTY_TYPE, pRequest, pResponse);
      return;
    }
    catch (InvalidNumberMethodsException inme) {
      addFormException(INVALID_NUMBER_METHODS, pRequest, pResponse);
      return;
    }
    catch (WSDLException we) {
      if (isLoggingError())
        logError(we);

      addFormException(UNABLE_TO_PROCESS_WSDL, pRequest, pResponse);
      return;
    }
    catch (RepositoryException re) {
      if (isLoggingError())
        logError(re);

      addFormException(UNABLE_TO_PROCESS_REQUEST, pRequest, pResponse);      
      return;
    }
  }

  
  protected void createInstallConfiguration(WSDLSOAPInfo pInfo,
                                            String pGearDefId)
    throws RepositoryException
  {

    SOAPRepositoryUtils utils = getRepositoryUtils();

    // check for necessary gear info, if not there programmer error
    // get all info out of it, call into RepositoryUtils
    RepositoryItem item = 
      utils.createInstallServiceConfig(pGearDefId,
                                       getUsername(),
                                       getPassword(),
                                       pInfo.getTargetServiceURL(),
                                       pInfo.getTargetMethodName(),
                                       pInfo.getTargetNamespaceURI(),
                                       pInfo.getSOAPActionURI(),
                                       pInfo.getServiceParameters());
  }
  
  protected void createInstanceConfiguration(WSDLSOAPInfo pInfo,
                                             String pGearId)
    throws RepositoryException
  {
    SOAPRepositoryUtils utils = getRepositoryUtils();

    // check for necessary gear info, if not there programmer error
    // get all info out of it, call into RepositoryUtils
    RepositoryItem item = 
      utils.createInstanceServiceConfig(pGearId,
                                        getUsername(),
                                        getPassword(),
                                        pInfo.getTargetServiceURL(),
                                        pInfo.getTargetMethodName(),
                                        pInfo.getTargetNamespaceURI(),
                                        pInfo.getSOAPActionURI(),
                                        pInfo.getServiceParameters());
  }
  protected void createUserConfiguration(WSDLSOAPInfo pInfo,
                                         String pGearDefId,
                                         String pGearId,
                                         String pUserId)
    throws RepositoryException
  {
    SOAPRepositoryUtils utils = getRepositoryUtils();
    
    // check for necessary gear info, if not there programmer error
    // get all info out of it, call into RepositoryUtils
    RepositoryItem item = 
      utils.createUserServiceConfig(pGearDefId,
                                    pGearId,
                                    pUserId,
                                    getUsername(),
                                    getPassword(),
                                    pInfo.getTargetServiceURL(),
                                    pInfo.getTargetMethodName(),
                                    pInfo.getTargetNamespaceURI(),
                                    pInfo.getSOAPActionURI(),
                                    pInfo.getServiceParameters());
  }

  protected boolean isExistingWSDLDocument() 
    throws RepositoryException
  {
    if ( isLoggingDebug()) {
      logDebug("in isExistingWSDLDocument()");
    } 

    // if there is a installServiceConfigItem existing already with the given
    // gearDefId then the wsdl document exists already, otherwise it is not.
    RepositoryItem installServiceConfigItem = null;
    installServiceConfigItem = 
      getRepositoryUtils().getInstallServiceConfig(getGearDefId());

    if ( installServiceConfigItem != null) {
      return true;
    } // end of if ()
    
    return false;
  }
  
  /**
   * This formhandler supports three different "types"
   * of gear configurations; install, instance and user.
   * This gear is made aware of what mode it is in by a setting
   * of one of its boolean "mode" properties.
   *
   * <P>This method ensure only one mode is set, else a configuration
   * error exists on the part of the page programmer and we tell 
   * them so.
   *
   * @return true if one, and only one, mode configuration exists
   */
  protected boolean isValidModeConfiguration() {
    int total = 0;
    if (isInstallConfiguration())
      total += 1;
    if (isInstanceConfiguration())
      total += 1;
    if (isUserConfiguration())
      total += 1;
    
    if (total == 1)
      return true;
    else
      return false;
  }

  protected String getWSDLFileAsString() 
    throws IOException
  {
    UploadedFile file = getWSDLFile();
    InputStream is;
    String wsdlString;

    if (file == null || file.getFileSize() == 0) {
      return null;
    }
    
    wsdlString = new String(file.toByteArray());
    return wsdlString;
  }

  /*
   * This method copies the installServiceConfig.serviceConfig into userServiceConfig
   *
   */
  protected RepositoryItem createUserServiceConfigFromInstallServiceConfig(String pGearDefId,
                                                                           String pGearId,
                                                                           String pUserId,
                                                                           Map pServiceParameters,
                                                                           DynamoHttpServletRequest pRequest,
                                                                           DynamoHttpServletResponse pResponse)
  throws RepositoryException
  {
    if ( isLoggingDebug()) {
      logDebug("in createUserServiceConfigFromInstallServiceConfig()");
    } 
    
    SOAPPropertyManager propMgr = getPropertyManager();
    // get the install service configuration 
    // corresponding to the give gear def id.
    RepositoryItem installServiceConfigItem = 
      getRepositoryUtils().getInstallServiceConfig(pGearDefId);

    if (installServiceConfigItem == null) {
      if (isLoggingError()) {
        String msg = ResourceUtils.getMsgResource(NO_INSTALL_CONFIG,
                                                  SERVER_RESOURCE_BUNDLE_NAME,
                                                  sServerResources);
        logError(msg);
      }
      addFormException(UNABLE_TO_PROCESS_REQUEST, pRequest, pResponse);
      return null;
    } 
    
    // get the serviceConfig item from the installServiceconfigItem
    RepositoryItem serviceConfig = 
      (RepositoryItem)installServiceConfigItem.getPropertyValue(propMgr.getServiceConfigPropertyName());
    
    // get all the serviceParams item list from the serviceConfig item
    Collection serviceParamsCollection = 
      (Collection) serviceConfig.getPropertyValue(propMgr.getServiceParametersPropertyName());

    if ( serviceParamsCollection == null) {
      if (isLoggingError()) {
        String msg = ResourceUtils.getMsgResource(NO_SERV_PARAMS,
                                                  SERVER_RESOURCE_BUNDLE_NAME,
                                                  sServerResources);
        logError(msg);
      }
      addFormException(UNABLE_TO_PROCESS_REQUEST, pRequest, pResponse);
      return null;
    } 

    // now iterate through the serviceparams item list and create a 
    // map with param name and type
    Iterator serviceParamsIterator = serviceParamsCollection.iterator();
    WSDLSOAPParameter param;
    Collection params =  new ArrayList(11);
    RepositoryItem serviceParamItem = null;
    String paramName = null, paramType = null;
    
    //iterate through serviceParams repositoryItems
    while ( serviceParamsIterator.hasNext()) {
      serviceParamItem = (RepositoryItem)serviceParamsIterator.next();
      paramName = (String)serviceParamItem.getPropertyValue(propMgr.getParamNamePropertyName());
      if ( paramName != null) {
        paramType = (String)serviceParamItem.getPropertyValue(propMgr.getParamTypePropertyName());
        param = new WSDLSOAPParameter(paramName, paramType);
        params.add(param);
      } // end of if ()
      
    } // end of while ()


    // we have all properties to create new userServiceConfigItem, so create it.
    String userName = (String)serviceConfig.getPropertyValue(propMgr.getUsernamePropertyName());
    String password = (String)serviceConfig.getPropertyValue(propMgr.getPasswordPropertyName());
    String targetURL = (String)serviceConfig.getPropertyValue(propMgr.getTargetURLPropertyName());
    String methodName = (String)serviceConfig.getPropertyValue(propMgr.getTargetMethodNamePropertyName());
    String namespace = (String)serviceConfig.getPropertyValue(propMgr.getTargetNamespacePropertyName());
    String soapActionURI = (String)serviceConfig.getPropertyValue(propMgr.getSOAPActionURIPropertyName());

                            
    RepositoryItem newUserServiceConfigItem =
      getRepositoryUtils().createUserServiceConfig(pGearDefId,
                                                   pGearId,
                                                   pUserId,
                                                   userName,
                                                   password,
                                                   targetURL,
                                                   methodName,
                                                   namespace,
                                                   soapActionURI,
                                                   params);
    return newUserServiceConfigItem;
                                                                                           
  }

  /**
   * Adds a form exception to the formhandler.  The exception
   * that is generated is created using the <code>pErrorKey</code>
   * to obtain a resource using the {@link #getStringResource(String, Locale)
   * <code>getStringResource</code>} method.
   *
   * @param pErrorKey the resource key
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   */
  protected void addFormException(String pErrorKey, 
                                  DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse)
  {
    String msg = getStringResource(pErrorKey, getLocale(pRequest, pResponse));
    addFormException(new DropletException(msg, null, pErrorKey));
  }

  // Utility Methods 

  /**
   * Returns either the Locale from the Request object (if it isn't NULL),
   * or the Locale from the JVM.
   * @param pRequest the servlet's request
   * @return Either the Locale from the Request object, or the Locale 
   * from the JVM.
   */
  protected Locale getLocale (DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse) 
  {
    if (pRequest.getRequestLocale() != null) {
      return pRequest.getRequestLocale().getLocale();
    }
    else {
      return Locale.getDefault();
    }
  }

  /**
   * This method acts as a utility method to obtain a given
   * resource for a particular key.  This is used to lookup error
   * messages for the users locale.
   *
   * @param pResourceName the resource key used to obtain the resource
   * @param pLocale the users locale for which the resource bundle will be 
   *                obtained.
   * @return The String resource
   */
  public String getStringResource (String pResourceName,
                                   Locale pLocale)
  {
    try {
      return getClientStringResource(pResourceName, pLocale);
    } 
    catch (MissingResourceException exc) {
      if (isLoggingError())
        logError(exc);
    }
    return pResourceName;
  }
  
  /**
   * Gets a resource from the CLIENT_RESOURCE_NAME resources
   * @param pResourceName the name of the resource to get
   * @param pLocale the locale to use when getting the resource
   * @return the appropriate encoded resource
   * @exception MissingResourceException if the resource can't
   * be found
   **/
  public static String getClientStringResource (String pResourceName,
						Locale pLocale)
    throws MissingResourceException
  {
    ResourceBundle clientBundle = 
      ResourceUtils.getBundle(CLIENT_RESOURCE_BUNDLE_NAME, pLocale);
    return getStringResource
      (clientBundle, CLIENT_RESOURCE_BUNDLE_NAME, pResourceName);
  }

  /**
   * Gets a string resource using the given bundle, bundle name
   * and resource name
   * @param pBundle the bundle to get the resource from
   * @param pBundleName the name of the bundle
   * @param pResourceName the resource to retrieve
   * @return a formatted string resource
   * @exception MissingResourceException if the resource
   * cannot be found
   **/
  static String getStringResource(ResourceBundle pBundle,
				  String pBundleName,
				  String pResourceName)
    throws MissingResourceException
  {
    return ResourceUtils.getMsgResource
      (pResourceName, pBundleName, pBundle);
  }

}   // end of class

