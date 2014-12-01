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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.ResourceBundle;
import javax.servlet.*;
import java.util.List;
import java.io.IOException;

// DAS classes
import atg.core.util.ResourceUtils;
import atg.repository.*;
import atg.nucleus.*;
import atg.servlet.*;
import atg.userdirectory.User;
import atg.nucleus.naming.ParameterName;
// DPS classes
import atg.userprofiling.ProfileTools;
// DSS classes

// Portal classes
import atg.portal.framework.*;

/**
 * This droplet is used to get all the service parameter names in any of the three
 * user,instance & installServiceConfiguration items depending upon how the component
 * on this class is configured. while fetching the service parameters this droplet
 * gets the parameters in hierchical manner.
 * <p>If the component is configured for userServiceConfiguration then this droplet
 * checks for the userId, gearId in gearEnvironment to get the userServiceConfiguration,
 * and if userId is null then the droplet uses just gearId to get instanceServiceConfiguration,
 * and if gearId is also null then it gets installServiceConfiguration.
 * Also if there is not userServiceConfiguration for the give parameters it returns the
 * installServiceConfiguration.
 *
 * It takes the following input parameters.
 * <dl>
 *
 * <dt>gearEnvironment
 *
 * <dd>the gear environment which provides the gearDefinitionId/gearId/userId.
 *
 * </dl>
 *
 * It renders the following oparams:
 * <dl>
 * 
 * <dt>output
 * <dd>The oparam output is rendered once and the array of paramNames are set
 *     in result parameter.
 *
 * <dt>count
 * <dd>This will contain the size of array containing paramNames.
 *
 *
 *    

 *
 *
 * @author Manoj Potturu
 * @version $Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/portal/gear/soapclient/SOAPServiceParametersDroplet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class SOAPServiceParametersDroplet extends DynamoServlet
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/portal/gear/soapclient/SOAPServiceParametersDroplet.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  //-------------------------------------
  // ResourceBundle 
  static final String RESOURCE_BUNDLE_NAME = 
    "atg.portal.gear.soapclient.ServerResources";

  static ResourceBundle sResourceBundle = 
    ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);  

  public static String ERROR = "error";
  public static String ERRORMESSAGE = "errorMessage";  
  public static String EMPTY = "empty";
  public static String OUTPUT = "output";
  public static String RESULT = "result";
  public static String COUNT = "COUNT";  


  public static String NULL_GEAR_ENVIRONMENT = "nullGearEnvironment";
  
  // Input Paramters
  static final ParameterName GEAR_ENVIRONMENT_PARAM = ParameterName.getParameterName("gearEnvironment");
  
  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------


  /** Soap repository utils to access Repository API */
  SOAPRepositoryUtils mSOAPRepositoryUtils;

  //-------------------------------------
  /**
   * Sets Soap repository utils to access Repository API
   **/
  public void setSOAPRepositoryUtils(SOAPRepositoryUtils pSOAPRepositoryUtils) {
    mSOAPRepositoryUtils = pSOAPRepositoryUtils;
  }

  //-------------------------------------
  /**
   * Returns Soap repository utils to access Repository API
   **/
  public SOAPRepositoryUtils getSOAPRepositoryUtils() {
    return mSOAPRepositoryUtils;
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
  // property: PropertyManager
  SOAPPropertyManager mPropertyManager;

  /**
   * Component that keeps track of various properties for repository
   * items, property names etc.
   * @return
   */
  public SOAPPropertyManager getPropertyManager() {
    return mPropertyManager;
  }

  /**
   * Set the PropertyManager property.
   * @param pPropertyManager
   */
  public void setPropertyManager(SOAPPropertyManager pPropertyManager) {
    mPropertyManager = pPropertyManager;
  }
  
  /** property to indicate whethere the current instance of the droplet is for UserServiceConfig */
  boolean mUserConfiguration = false;

  //-------------------------------------
  /**
   * Sets property to indicate whethere the current instance of the droplet is for UserServiceConfig
   **/
  public void setUserConfiguration(boolean pUserConfiguration) {
    mUserConfiguration = pUserConfiguration;
  }

  //-------------------------------------
  /**
   * Returns property to indicate whethere the current instance of the droplet is for UserServiceConfig
   **/
  public boolean isUserConfiguration() {
    return mUserConfiguration;
  }


  /** property to indicate whethere the current instance of the droplet is for InstanceserviceConfig */
  boolean mInstanceConfiguration = false;

  //-------------------------------------
  /**
   * Sets property to indicate whethere the current instance of the droplet is for InstanceserviceConfig
   **/
  public void setInstanceConfiguration(boolean pInstanceConfiguration) {
    mInstanceConfiguration = pInstanceConfiguration;
  }

  //-------------------------------------
  /**
   * Returns property to indicate whethere the current instance of the droplet is for InstanceserviceConfig
   **/
  public boolean isInstanceConfiguration() {
    return mInstanceConfiguration;
  }


  /** property to indicate whethere the current instance of the droplet is for InstallServiceConfiguration */
  boolean mInstallConfiguration = false;

  //-------------------------------------
  /**
   * Sets property to indicate whethere the current instance of the droplet is for InstallServiceConfiguration
   **/
  public void setInstallConfiguration(boolean pInstallConfiguration) {
    mInstallConfiguration = pInstallConfiguration;
  }

  //-------------------------------------
  /**
   * Returns property to indicate whethere the current instance of the droplet is for InstallServiceConfiguration
   **/
  public boolean isInstallConfiguration() {
    return mInstallConfiguration;
  }

  
  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  public SOAPServiceParametersDroplet(){
  }
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------


  //-------------------------------------
  /**
   * This is the method which services the request of this droplet.
   */
  public void service(DynamoHttpServletRequest pRequest,
                      DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {

    GearEnvironment gearEnv = null;

    Object obj = pRequest.getObjectParameter(GEAR_ENVIRONMENT_PARAM);
    if (obj instanceof GearEnvironment ) {
      gearEnv = (GearEnvironment) obj;
    } // end of if ()
    if ( obj == null) {
      String errorMsg = ResourceUtils.getMsgResource(NULL_GEAR_ENVIRONMENT,
                                                     RESOURCE_BUNDLE_NAME,
                                                     sResourceBundle);
      pRequest.setParameter(ERRORMESSAGE, errorMsg);
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);


    } // end of if ()

    if (isUserConfiguration() ) {
      serviceUserConfiguration(gearEnv, pRequest, pResponse);

    } else if (isInstanceConfiguration() ) {
      serviceInstanceConfiguration(gearEnv, pRequest, pResponse);

    } else if (isInstallConfiguration() ) {
      serviceInstallConfiguration(gearEnv, pRequest, pResponse);

    } // end of if ()

    return;
  }


  /*
   * This method checks for userId, gearId and gets the service parameters from the 
   *
   */
  protected void serviceUserConfiguration(GearEnvironment pGearEnv,
                                          DynamoHttpServletRequest pRequest,
                                          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    Gear gear = pGearEnv.getGear();
    String gearId = null, userId = null;
    User user = null;
    if ( isLoggingDebug()) {
      logDebug("in serviceUserConfiguration():");
    } // end of if ()
    
    //    if gear or gear id is null, just jump to get install service config since
    // no use of just userId w/o instance id.
    if ( gear != null && (gearId = gear.getId()) != null) {

      user = UserUtilities.getCurrentUser();
      userId = getUserId(user);

      // if user or user id is null, jump to get instance service config
      if ( userId != null ) {

        // Get the gearDefinitionId from gear Environment
    GearDefinition gearDefinition = pGearEnv.getGearDefinition();
    String gearDefinitionId = null;
    if ( gearDefinition == null || (gearDefinitionId = gearDefinition.getId()) == null) {

      String errorMsg = ResourceUtils.getMsgResource(NULL_GEAR_ENVIRONMENT,
                                                     RESOURCE_BUNDLE_NAME,
                                                     sResourceBundle);
      pRequest.setParameter(ERRORMESSAGE, errorMsg);
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
      return;
    } // end of if ()

          RepositoryItem userServiceConfigItem = null;
        // get the userServiceConfig for the user 
        try {
          // try getting the userServiceConfigItem, and fetch service parameters later.
          userServiceConfigItem = getSOAPRepositoryUtils().getUserServiceConfig(gearDefinitionId,  gearId, userId);
        }catch(IllegalArgumentException iae) {
          if ( isLoggingError()) 
            logError(iae);

          pRequest.setParameter(ERRORMESSAGE, iae.getMessage());
          pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);

        }
        catch(RepositoryException re){
          if ( isLoggingError()) 
            logError(re);
          pRequest.setParameter(ERRORMESSAGE, re.getMessage());
          pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);

        }
        // if userServiceConfigItem is null means there is no item for the user with
        // this gear, so try to get installserviceconfiguration for the same gear.
        if ( userServiceConfigItem == null) {
          serviceInstallConfiguration(pGearEnv, pRequest, pResponse);
        } else {
          // fetch the serviceparameters from userSreiceConfig and display them
          outputServiceParameters(getServiceParameters(userServiceConfigItem), pRequest, pResponse);
        } // end of else
      } else {
        serviceInstanceConfiguration(pGearEnv, pRequest, pResponse);
      } // end of else

    }else {
      serviceInstallConfiguration(pGearEnv, pRequest, pResponse);            
    } // end of else

  }

  /*
   *
   *
   */
  protected String getUserId(User pUser)
  {

    String login = null;
    if ( pUser == null || (login = pUser.getLogin()) == null) {
      return null;
    } // end of if ()

    RepositoryItem profileItem = (RepositoryItem)getProfileTools().getItem(login, null);
    if ( profileItem != null) {
      return profileItem.getRepositoryId();
    } // end of if ()
    return null;
  }

  /*
   *
   *
   */
  protected void serviceInstanceConfiguration(GearEnvironment pGearEnv,
                                              DynamoHttpServletRequest pRequest,
                                              DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {

    Gear gear = pGearEnv.getGear();
    String gearId = null;
    if ( isLoggingDebug()) {
      logDebug("in serviceInstanceConfiguration():");
    } // end of if ()
    

    // if gear or gear id is null, just jump to get install service config
    if ( gear != null && (gearId = gear.getId()) != null) {
      RepositoryItem instanceServiceConfigItem = null;

      try {
        instanceServiceConfigItem = getSOAPRepositoryUtils().getInstanceServiceConfig(gearId);
      }catch(IllegalArgumentException iae) {
        if ( isLoggingError())
          logError(iae);

        pRequest.setParameter(ERRORMESSAGE, iae.getMessage());
        pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
      }
      catch(RepositoryException re){
        if ( isLoggingError())
          logError(re);

        pRequest.setParameter(ERRORMESSAGE, re.getMessage());
        pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);

      }
      outputServiceParameters(getServiceParameters(instanceServiceConfigItem), pRequest, pResponse);
    }else {
      serviceInstallConfiguration(pGearEnv, pRequest, pResponse);            
    } // end of else
  }

  /*
   *
   *
   */
  protected void serviceInstallConfiguration(GearEnvironment pGearEnv,
                                             DynamoHttpServletRequest pRequest,
                                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    GearDefinition gearDefinition = pGearEnv.getGearDefinition();
    String gearDefinitionId = null;
    if ( isLoggingDebug()) {
      logDebug("in serviceInstallConfiguration():");
    } // end of if ()
    

    if ( gearDefinition == null || (gearDefinitionId = gearDefinition.getId()) == null) {

      String errorMsg = ResourceUtils.getMsgResource(NULL_GEAR_ENVIRONMENT,
                                                     RESOURCE_BUNDLE_NAME,
                                                     sResourceBundle);
      pRequest.setParameter(ERRORMESSAGE, errorMsg);
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
      return;
    } // end of if ()
    RepositoryItem installServiceConfigItem = null;
    try {
      installServiceConfigItem = getSOAPRepositoryUtils().getInstallServiceConfig(gearDefinitionId);
    }catch(IllegalArgumentException iae) {
      if ( isLoggingError())
        logError(iae);

      pRequest.setParameter(ERRORMESSAGE, iae.getMessage());
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
    }
    catch(RepositoryException re){
      if ( isLoggingError())
        logError(re);

      pRequest.setParameter(ERRORMESSAGE, re.getMessage());
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);

    }
    outputServiceParameters(getServiceParameters(installServiceConfigItem), pRequest, pResponse);
    return;    

  }


  /* This method renders the droplet oparams taking list of serviceParameters. the oparam it renders
   * depends on the list. if list is null or of size 0, then EMPTY oparam is
   * rendered otherwise OUTPUT oparam is rendered.
   *
   *
   */
   
  protected void outputServiceParameters(Map pServiceParamMap,
                                         DynamoHttpServletRequest pRequest,
                                         DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    if ( isLoggingDebug()) {
      logDebug("in outputServiceParameters():");
    } // end of if ()
    

    // check if the serviceParams are null or empty, if true then render
    // EMPTY oparam, otherwise render OUTPUT oparam setting result & count params
    if ( pServiceParamMap ==null || pServiceParamMap.size() == 0) {
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
      return;
    } // end of if ()

    pRequest.setParameter(COUNT, new Integer(pServiceParamMap.size()));
    pRequest.setParameter(RESULT, pServiceParamMap);
    pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
  }

  /*
   *
   *
   */
  protected Map getServiceParameters(RepositoryItem pServiceConfigItem)
    throws ServletException, IOException
  {
    if ( isLoggingDebug()) {
      logDebug("in getServiceParameters():");
    } // end of if ()
    

    if ( pServiceConfigItem == null ) {
      return null;
    } // end of if ()
    
    RepositoryItem serviceConfigItem = (RepositoryItem)pServiceConfigItem.getPropertyValue(getPropertyManager().getServiceConfigPropertyName());
    if ( serviceConfigItem == null) {
      return null;  
    } // end of if ()
    List serviceParamList = (List)serviceConfigItem.getPropertyValue(getPropertyManager().getServiceParametersPropertyName());
    if ( serviceParamList == null ) {
      return null;
    } // end of if ()

    Iterator serviceParamIterator = serviceParamList.iterator();
    RepositoryItem serviceParamItem = null;
    String parameterName = null, parameterValue = null;
    Map parameterNameMap = new HashMap();
    while ( serviceParamIterator.hasNext()) {
      serviceParamItem = (RepositoryItem)serviceParamIterator.next();
      parameterName = (String)serviceParamItem.getPropertyValue(getPropertyManager().getParamNamePropertyName());
      parameterValue = (String)serviceParamItem.getPropertyValue(getPropertyManager().getParamValuePropertyName());
      if ( parameterName != null) {
        parameterNameMap.put(parameterName, parameterValue);        
      } // end of if
      
    } // end of while ()
    return parameterNameMap;

  }
  
}   // end of class






          
  
