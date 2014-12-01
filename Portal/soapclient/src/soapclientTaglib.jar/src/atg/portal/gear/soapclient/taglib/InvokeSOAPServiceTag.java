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

package atg.portal.gear.soapclient.taglib;

// Java classes
import javax.servlet.jsp.tagext.TagSupport;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.*;
import javax.servlet.jsp.*;
import javax.naming.*;

// PAF classes
import atg.portal.nucleus.NucleusComponents;
import atg.portal.framework.GearEnvironment;
import atg.portal.gear.soapclient.*;

// DAS classes
import atg.core.util.StringUtils;
import atg.service.soap.*;
import atg.service.wsdl.*;
import atg.core.util.ResourceUtils;
import atg.core.util.BeanUtils;
import atg.repository.*;
import atg.servlet.ServletUtil;
import atg.core.exception.*;

// DPS classes

// DSS classes

// DCS classes

/**
 *
 * @author Ashley J. Streb
 * @version $Id: //app/portal/version/10.0.3/soapclient/soapclientTaglib.jar/src/atg/portal/gear/soapclient/taglib/InvokeSOAPServiceTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class InvokeSOAPServiceTag
  extends TagSupport
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/soapclient/soapclientTaglib.jar/src/atg/portal/gear/soapclient/taglib/InvokeSOAPServiceTag.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------
  
  /**
   * Indicate that the WSDL document that this taglib is able to
   * obtain is invalid
   */
  public static final String UNABLE_TO_GET_CONFIG_FROM_DATABASE = 
    "UnableToGetConfigFromDatabase";

  public static final String MALFORMED_URL = 
    "malformedURL";

    public static final String UNABLE_TO_INVOKE_SERVICE = 
    "UnableToInvokeService";

  public static final String UNABLE_TO_FIND_CLASS = 
    "UnableToFindClass";

  public static final String UNABLE_TO_ACCESS_SOAP = 
    "UnableToAccessSoap";

  public static final String UNABLE_TO_GET_USER_ITEM = 
    "UnableToGetUserItem";

  public static final String USER_ITEM_NOT_FOUND = 
    "UserItemNotFound";

  public static final String INSTALL_ITEM_NOT_FOUND = 
    "InstallItemNotFound";

  public static final String SOAPTOOLS_JNDI_NAME = 
    "dynamo:/atg/dynamo/service/soap/SOAPTools";

  public static final String WSDLTOOLS_JNDI_NAME = 
    "dynamo:/atg/dynamo/service/wsdl/WSDLTools";

  public static final String REPOSITORY_UTILS_JNDI_NAME = 
    "dynamo:/atg/portal/gear/soapclient/SOAPRepositoryUtils";

  public static final String PROP_MGR_JNDI_NAME = 
    "dynamo:/atg/portal/gear/soapclient/SOAPPropertyManager";

  private static SOAPTools sSOAPTools;

  private static WSDLSOAPTools sWSDLTools;
  
  private static SOAPRepositoryUtils sRepositoryUtils;

  private static SOAPPropertyManager sPropMgr;

  /**
   * Class will maintain a URL cache.  When attempting to get
   * a URL object (represented by a String) try to get it out of a cache
   * instead of constantly creating new objects
   */
  private static final Map sURLCache = 
    new HashMap(98);

  //-------------------------------------
  // ResourceBundle 
  static final String RESOURCE_BUNDLE_NAME = 
    "atg.portal.gear.soapclient.taglib.ServerResources";

  static ResourceBundle sResourceBundle = 
    ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);  

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------
  
  //--------------------------------------------------
  // Properties with standard accessor get/set methods
  //--------------------------------------------------
  
  //---------------------------------------------------------------------
  // property: Id
  String mId;

  /**
   * Return the Id property.
   * @return
   */
  public String getId() {
    return mId;
  }

  /**
   * Set the Id property.
   * @param pId
   */
  public void setId(String pId) {
    mId = pId;
  }

  
  //---------------------------------------------------------------------
  // property: gearEnv
  GearEnvironment mGearEnv;

  /**
   * Return the gearEnv property.
   * @return
   */
  public GearEnvironment getGearEnv() {
    return mGearEnv;
  }

  /**
   * Set the gearEnv property.
   * @param pGearEnv
   */
  public void setGearEnv(GearEnvironment pGearEnv) {
    mGearEnv = pGearEnv;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------
  
  /**
   * Creates a new <code>InvokeSOAPServiceTag</code> instance.
   * Calls the super methods constructor method
   *
   */
  public InvokeSOAPServiceTag() {
    super();
  }

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  static {
    try {
      sWSDLTools = (WSDLSOAPTools) 
        NucleusComponents.lookup(WSDLTOOLS_JNDI_NAME);

      sSOAPTools = (SOAPTools) 
        NucleusComponents.lookup(SOAPTOOLS_JNDI_NAME);

      sRepositoryUtils = (SOAPRepositoryUtils) 
        NucleusComponents.lookup(REPOSITORY_UTILS_JNDI_NAME);

      sPropMgr = (SOAPPropertyManager) 
        NucleusComponents.lookup(PROP_MGR_JNDI_NAME);
    }
    catch (NamingException ne) {

      ne.printStackTrace();
    }
  }

  // assume they are in user level
  protected Collection getUserServiceParams() 
    throws RepositoryException
  {
    String gearInstanceId = getGearEnv().getGear().getId();
    String gearDefId = getGearEnv().getGearDefinition().getId();
    String userId = ServletUtil.getCurrentUserProfile().getRepositoryId();

    RepositoryItem config = 
      sRepositoryUtils.getUserServiceConfig(gearDefId,
                                            gearInstanceId,
                                            userId);
    if (config != null) {
      config = (RepositoryItem)
        config.getPropertyValue(sPropMgr.getServiceConfigPropertyName());
      if (config != null) {
        Collection params = (Collection)
          config.getPropertyValue(sPropMgr.getServiceParametersPropertyName());
        return params;
      }
    }
    
      return null;
  }

  /**
   * This method will initialize the SOAPParameters variable
   * to all of the parameters that the service to be invoked (the service
   * associated with the WSDL document that is associated with this tag)
   * enumerates.
   *
   * <P>The SOAPParameters variable is a collection of SOAPParameters.
   * This method will initialize each SOAPParameter so that the
   * the parameter name and class type is populated into each SOAPParameter
   * object.  The information to populate the objects with this information
   * is obtained from the WSDLTools component.
   *
   * <P>The values for each parameter are not populated at this time.
   *
   * @exception InvalidWSDLDocumentException if the wsdl associated
   * with this tag is either null or an empty string.
   * @exception ClassNotFoundException if one of the parameter
   * types specified as a parameter type is unknown
   */
  protected Collection getSOAPParameters() 
    throws ClassNotFoundException,
           RepositoryException,
           ItemNotFoundException
  {
    SOAPParameter soapParam;         
    Map soapParamMap;
    String paramName;
    String paramClassType;
    Collection soapParams;
    Class paramClass;
    Object paramValue;

    // get "correct" serviceparams
    Collection serviceParams = getUserServiceParams();
    
    if (serviceParams != null && serviceParams.size() > 0) {
      soapParams = new ArrayList(serviceParams.size());
      Iterator paramIterator = serviceParams.iterator();
      while (paramIterator.hasNext()) {
        RepositoryItem param = (RepositoryItem)paramIterator.next();
        paramName = (String)
          param.getPropertyValue(sPropMgr.getParamNamePropertyName());
        paramClassType = (String)
          param.getPropertyValue(sPropMgr.getParamTypePropertyName());
        paramClass = Class.forName(paramClassType);
        paramValue = getObjectValue(paramClass, 
                                    (String)param.getPropertyValue(sPropMgr.getParamValuePropertyName()));
        soapParam = new SOAPParameter(paramName, paramClass, paramValue);
        soapParams.add(soapParam);
      }
      return soapParams;
    }
    else {
      throw new  ItemNotFoundException(USER_ITEM_NOT_FOUND);
    }
  }

  /**
   * This method obtains a java primitive wrapper object whose value
   * corresponds to that of pValue.  Additionally, the wrapper object
   * is of type pClass.  This is used when the runtime value of a particular
   * parameter is passed to us as a String value, but needs to be in a 
   * different object type. (Integer, Long etc.)
   *
   * @param pClass string name of the expected parameter type
   * @param pValue value for the expected parameter
   * @return an <code>Object</code> value
   */
  protected static Object getObjectValue(Class pClass, String pValue) 
  {
    Class clazz;

    // string is a special case
    if (pClass == String.class)
      return pValue;

    clazz = BeanUtils.getWrapperType(pClass);
    if (clazz == Character.class)
      return new Character(pValue.charAt(0));
    else if (clazz == Boolean.class)
      return Boolean.valueOf(pValue);
    else if (clazz == Integer.class)
      return Integer.valueOf(pValue);
    else if (clazz == Byte.class)
      return Byte.valueOf(pValue);
    else if (clazz == Short.class)
      return Short.valueOf(pValue);
    else if (clazz == Long.class)
      return Long.valueOf(pValue);
    else if (clazz == Float.class)
      return Float.valueOf(pValue);
    else if (clazz == Double.class)
      return Double.valueOf(pValue);
    else
      throw new IllegalArgumentException();
  }

  /**
   * Helper method to allow us to cache URLs.
   *
   * @param pURL a <code>String</code> value
   * @return an <code>URL</code> value
   */
  private static URL getURL(String pURL) 
    throws MalformedURLException
  {
    URL url = (URL) sURLCache.get(pURL);
    if (url != null) {
      return url;
    }
    else {
      url = new URL(pURL);
      sURLCache.put(pURL, url);
      return url;
    }
  }

  protected RepositoryItem getInstallServiceConfiguration(String pGearDefId)
    throws RepositoryException
  {
    String serviceConfigName = 
      sPropMgr.getServiceConfigPropertyName();

    RepositoryItem installConfig = 
      sRepositoryUtils.getInstallServiceConfig(pGearDefId);
    
    if (installConfig != null) {
      return (RepositoryItem)
        installConfig.getPropertyValue(serviceConfigName);
    }
    
    return null;
  }

  /**
   * This method is expected to be called by a page developer to get access
   * to a SOAP RPC service.  This tag makes itself available to the page
   * programmer as a script variable identified by the attribute value Id.
   * Page programmers can then make use of the 
   *
   * @return a <code>SOAPServiceTagResult</code> value
   */
  protected Object invokeService() 
    throws MalformedURLException,
           RepositoryException,
           ClassNotFoundException,
           SOAPException,
           ItemNotFoundException
  {
    Object result;
    String gearDefId = getGearEnv().getGearDefinition().getId();
    // get connection level information
    RepositoryItem serviceConfiguration = 
      getInstallServiceConfiguration(gearDefId);

    // if we don't get install config, throw exception
    // this may happen when we install the gear but haven't upload the wsdl doc for the
    // the first time.
    if ( serviceConfiguration == null) {
      throw new RepositoryException(INSTALL_ITEM_NOT_FOUND);
    } // end of if ()
    
    String targetURI = (String)
      serviceConfiguration.getPropertyValue(sPropMgr.getTargetNamespacePropertyName());
    String targetMethodName = (String)
      serviceConfiguration.getPropertyValue(sPropMgr.getTargetMethodNamePropertyName());
    String serverURL = (String)
      serviceConfiguration.getPropertyValue(sPropMgr.getTargetURLPropertyName());
    String username = (String)
      serviceConfiguration.getPropertyValue(sPropMgr.getUsernamePropertyName());
    String password = (String)
      serviceConfiguration.getPropertyValue(sPropMgr.getPasswordPropertyName());
   String soapActionURI = (String)
      serviceConfiguration.getPropertyValue(sPropMgr.getSOAPActionURIPropertyName());
    // get runtime param values      
    // get runtime param values

    // check installServiceConfig for service params
    Collection serviceParams = null;
    if (isServiceParamsExistInInstallConfig()) {
       serviceParams = getSOAPParameters();      
    } // end of if ()

    if (StringUtils.isBlank(username) &&
        StringUtils.isBlank(password)) {
      result = sSOAPTools.invokeHTTPRPCCall(targetURI, targetMethodName, 
                                            getURL(serverURL), serviceParams, 
                                            soapActionURI);
    }
    else {
      result = sSOAPTools.invokeSecureHTTPRPCCall(targetURI, targetMethodName,
                                                  username, password,
                                                  getURL(serverURL), 
                                                  serviceParams,
                                                  soapActionURI);
    }
    
    return result;
  }



  /**
   * This method invokes the 
   * {@link #initializeSOAPParameters<code>initializeSOAPParameters</code>}
   * method, sets itself as the script variable and then returns
   * EVAL_BODY_INCLUDE.  
   *
   * <P>If the initializeSOAPParameters method throws an exception,
   * then an error is logged and the tag returns SKIP_BODY.
   *
   * @return an <code>int</code> value
   * @exception JspException if an error occurs
   */
  public int doStartTag() 
    throws JspException
  {
    SOAPServiceTagResult tagResult = new SOAPServiceTagResult();
    
    try {
      Object result = invokeService();
      tagResult.setResult(result);
      return SKIP_BODY;
    }
    catch (RepositoryException re) {
      String errorMsg = ResourceUtils.getMsgResource(UNABLE_TO_GET_CONFIG_FROM_DATABASE,
                                                     RESOURCE_BUNDLE_NAME,
                                                     sResourceBundle);
      logError(errorMsg, re);
      
      String userMsg = ResourceUtils.getUserMsgResource(UNABLE_TO_INVOKE_SERVICE,
                                                    RESOURCE_BUNDLE_NAME,
                                                    sResourceBundle);
      tagResult.addErrorObject(UNABLE_TO_INVOKE_SERVICE, userMsg, null);
      return SKIP_BODY;
    }
    catch (MalformedURLException me) {
      String errorMsg = ResourceUtils.getMsgResource(MALFORMED_URL,
                                                     RESOURCE_BUNDLE_NAME,
                                                     sResourceBundle);
      logError(errorMsg, me);

      String userMsg = ResourceUtils.getUserMsgResource(UNABLE_TO_INVOKE_SERVICE,
                                                    RESOURCE_BUNDLE_NAME,
                                                    sResourceBundle);
      tagResult.addErrorObject(UNABLE_TO_INVOKE_SERVICE, userMsg, null);
      return SKIP_BODY;
    }
    catch (ClassNotFoundException cnfe) {
      String errorMsg = ResourceUtils.getMsgResource(UNABLE_TO_FIND_CLASS,
                                                     RESOURCE_BUNDLE_NAME,
                                                     sResourceBundle);
      logError(errorMsg, cnfe);

      String userMsg = ResourceUtils.getUserMsgResource(UNABLE_TO_INVOKE_SERVICE,
                                                    RESOURCE_BUNDLE_NAME,
                                                    sResourceBundle);
      tagResult.addErrorObject(UNABLE_TO_INVOKE_SERVICE, userMsg, null);
      return SKIP_BODY;
    }
    catch (SOAPException se) {
      String errorMsg = ResourceUtils.getMsgResource(UNABLE_TO_ACCESS_SOAP,
                                                     RESOURCE_BUNDLE_NAME,
                                                     sResourceBundle);
      logError(errorMsg, se);
      String userMsg = ResourceUtils.getUserMsgResource(UNABLE_TO_INVOKE_SERVICE,
                                                    RESOURCE_BUNDLE_NAME,
                                                    sResourceBundle);
      tagResult.addErrorObject(UNABLE_TO_INVOKE_SERVICE, userMsg, null);
      return SKIP_BODY;
    }
    catch (ItemNotFoundException infe) {
      String userMsg = ResourceUtils.getUserMsgResource(UNABLE_TO_GET_USER_ITEM,
                                                     RESOURCE_BUNDLE_NAME,
                                                     sResourceBundle);

      tagResult.addErrorObject(UNABLE_TO_GET_USER_ITEM, userMsg, null);
      return SKIP_BODY;
    }
    finally{
      pageContext.setAttribute(getId(), tagResult);
    }
  }
  
  /**
   * This method simply allows the rest of the page to be evaluated by
   * returning EVAL_PAGE.
   *
   * @return an <code>int</code> value
   */
  public int doEndTag() {
    return EVAL_PAGE;
  }
  
  public void release() {
    mId = null;
    mGearEnv = null;
  }

  /**
   * Provides logging of error facilities for this tag.  These are errors
   * that are destined to be consumed by a developer on the site and not
   * by an end user.  This means that the nature of these errors is that
   * they were not caused by some end-user error, but rather a misconfiguration
   * of the site, or some other general runtime failure.
   *
   * @param pMessage the message to be recorded in the server log
   * @param pException a <code>Throwable</code> value
   */
  protected void logError(String pMessage, Throwable pException) {
    pageContext.getServletContext().log(pMessage, pException);
  }

  /**
   * Checks whether serviceParams exist in installServiceConfig or not.
   * @return true if exist, otherwise false
   * @throws RepositoryException if not able to get installServiceConfiguration
   **/
  protected boolean isServiceParamsExistInInstallConfig()
    throws RepositoryException{

    String gearDefId = getGearEnv().getGearDefinition().getId();

    //get installserviceconfig
    RepositoryItem serviceConfig = getInstallServiceConfiguration(gearDefId);

    if ( serviceConfig != null) {

      //get serviceparams list from serviceConfig
      List serviceParamsList = (List)
        serviceConfig.getPropertyValue(sPropMgr.getServiceParametersPropertyName());

      // if serviceParams list is null or of size 0 then there are no
      // service  params in install service config, so return false
      if ( serviceParamsList == null || serviceParamsList.size() == 0) {
        return false;
      } // end of if ()

    } else {
      // serviceConfig is null, so return false
      return false;
    } // end of else
    return true;
  }
}   // end of class





