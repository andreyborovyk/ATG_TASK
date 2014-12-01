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
import java.util.ResourceBundle;
import javax.transaction.*;

// DAS classes
import atg.core.util.ResourceUtils;
import atg.service.wsdl.WSDLSOAPParameter;
import atg.repository.*;
import atg.repository.rql.*;
import atg.nucleus.*;
import atg.dtm.*;

// DPS classes

// DSS classes

// DCS classes

/**
 * This class provides helper functions for accessing various information
 * from the SOAP Repository.  (Configured through the Repository property).
 *
 * <P>Its main function is to obtain repository items from the repository
 * through various queries. (RQL)
 *
 * @author Ashley J. Streb
 * @version $Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/portal/gear/soapclient/SOAPRepositoryUtils.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class SOAPRepositoryUtils
  extends GenericService
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/portal/gear/soapclient/SOAPRepositoryUtils.java#2 $$Change: 651448 $";

  //-------------------------------------
  // ResourceBundle 
  static final String RESOURCE_BUNDLE_NAME = 
    "atg.portal.gear.soapclient.ServerResources";

  static ResourceBundle sResourceBundle = 
    ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);  

  /**
   * More than one repository item was returned by a query,
   * when only one should be
   */
  public static final String MULTIPLE_REPOSITORY_ITEMS = 
    "multipleRepositoryItems";
  
  /**
   * Gear Definition is null
   */
  public static final String NULL_GEAR_DEF_ID = 
    "nullGearDefId";
  
  public static final String NULL_USER_ID = 
    "nullUserId";
  
  public static final String NULL_GEAR_INSTANCE_ID = 
    "nullGearInstanceId";


  //---------------------------------------------------------------------
  // property: TransactionManager
  TransactionManager mTransactionManager;

  /**
   * Return the TransactionManager property.
   * @return
   */
  public TransactionManager getTransactionManager() {
    return mTransactionManager;
  }

  /**
   * Set the TransactionManager property.
   * @param pTransactionManager
   */
  public void setTransactionManager(TransactionManager pTransactionManager) {
    mTransactionManager = pTransactionManager;
  }

  /**
   * RQL statement to find an InstallServiceConfiguration for
   * a given user.
   */
  static RqlStatement sInstallServiceStatement;

  /**
   * RQL statement to find an InstanceServiceConfiguration for
   * a given user.
   */
  static RqlStatement sInstanceServiceStatement;

  /**
   * RQL statement to find an UserServiceConfiguration for
   * a given user.
   */
  static RqlStatement sUserServiceStatement;

  /**
   * RQL statement to find all UserServiceConfigurations for
   * a given gear definition id.
   */
  static RqlStatement sGearDefinitionPropertyInUser;

  /**
   * Initialize the installService, instanceService and userService
   * RQL statements.
   *
   * @exception ServiceException if an error occurs
   */
  public void doStartService() 
    throws ServiceException
  {
    try {
      sInstallServiceStatement = 
        RqlStatement.parseRqlStatement("gearDefinitionId = ?0");

      sInstanceServiceStatement = 
        RqlStatement.parseRqlStatement("gearInstanceId = ?0");

      sUserServiceStatement = 
        RqlStatement.parseRqlStatement("gearDefinitionId = ?0 and gearInstanceId = ?1 and userId = ?2");

      sGearDefinitionPropertyInUser = 
        RqlStatement.parseRqlStatement("gearDefinitionId = ?0");

    }
    catch(RepositoryException e) {
      if (isLoggingError())
        logError(e);
    }
  }

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------
  

  //---------------------------------------------------------------------
  // property: Repository
  MutableRepository mRepository;

  /**
   * Repository where SOAP configuration items live
   * @return a repository
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

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------
  
  /**
   * Empty constructor.
   *
   */
  public SOAPRepositoryUtils() {
  }

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------
  
  /**
   * Given a string representing a gear definition id,
   * this will return the InstallServiceConfiguration
   * RepositoryItem.
   *
   * <P>Null will be returned if no repository item could be found
   *
   * @param pGearDefId the gear definition id
   * @return a RepositoryItem
   * @exception RepositoryException if an error occurs, or if more than one 
   * matching RepositoryItem whose gearDefinitionId matches the parameter
   */
  public RepositoryItem getInstallServiceConfig(String pGearDefId) 
    throws RepositoryException,
           IllegalArgumentException
           
  {
    // make sure we got a valid parameter
    if (pGearDefId == null) {
      String errorMsg = ResourceUtils.getMsgResource(NULL_GEAR_DEF_ID,
                                                     RESOURCE_BUNDLE_NAME,
                                                     sResourceBundle);
      throw new IllegalArgumentException(errorMsg);
    }

    Object[] params = new Object[1];
    params[0] = pGearDefId;
    
    RepositoryView installServiceView = 
      getRepository().getView(getPropertyManager().getInstallServiceItemDescriptorName());

    RepositoryItem[] items = 
      sInstallServiceStatement.executeQuery(installServiceView, params);

    if (items == null || items.length == 0) {
      return null;
    }
    else if (items.length == 1) {
      return items[0];
    }
    else {
      String errorMsg = ResourceUtils.getMsgResource(MULTIPLE_REPOSITORY_ITEMS,
                                                     RESOURCE_BUNDLE_NAME,
                                                     sResourceBundle);
      throw new RepositoryException(errorMsg);
    }
  }
  
  /**
   * Given a string representing a gear instance id,
   * this will return the InstanceServiceConfiguration
   * RepositoryItem that is associated with the gear instance id.
   *
   * <P>Null will be returned if no repository item could be found
   *
   * @param pGearInstanceId gearInstance id
   * @return the InstanceServiceConfiguration repository item
   * @exception RepositoryException if an error occurs
   */
  public RepositoryItem getInstanceServiceConfig(String pGearInstanceId)
    throws RepositoryException,
           IllegalArgumentException
  {
    // make sure we got a valid parameter
    if (pGearInstanceId == null) {
      String errorMsg = ResourceUtils.getMsgResource(NULL_GEAR_INSTANCE_ID,
                                                     RESOURCE_BUNDLE_NAME,
                                                     sResourceBundle);
      throw new IllegalArgumentException(errorMsg);
    }

    Object[] params = new Object[1];
    params[0] = pGearInstanceId;
    
    RepositoryView instanceServiceView = 
      getRepository().getView(getPropertyManager().getInstanceServiceItemDescriptorName());

    RepositoryItem[] items = 
      sInstanceServiceStatement.executeQuery(instanceServiceView, params);

    if (items == null || items.length == 0) {
      return null;
    }
    else if (items.length == 1) {
      return items[0];
    }
    else {
      String errorMsg = ResourceUtils.getMsgResource(MULTIPLE_REPOSITORY_ITEMS,
                                                     RESOURCE_BUNDLE_NAME,
                                                     sResourceBundle);
      throw new RepositoryException(errorMsg);
    }
  }
  
  /**
   * A gear instance id and user id uniquely identify
   * a user level configuration of a particular gear.
   * Given these two parameters, this will obtain the corresponding
   * UserServiceConfiguration repository item.
   *
   * <P>Null will be returned if no repository item could be found
   *
   * @param pGearInstanceId a <code>String</code> value
   * @param pUserId a <code>String</code> value
   * @return a <code>RepositoryItem</code> value
   * @exception RepositoryException if an error occurs
   */
  public RepositoryItem getUserServiceConfig(String pGearDefId,
                                             String pGearInstanceId,
                                             String pUserId) 
    throws RepositoryException,
           IllegalArgumentException
  {
    // make sure we got a valid parameter
    if (pGearInstanceId == null) {
      String errorMsg = ResourceUtils.getMsgResource(NULL_GEAR_INSTANCE_ID,
                                                     RESOURCE_BUNDLE_NAME,
                                                     sResourceBundle);
      throw new IllegalArgumentException(errorMsg);
    }

    if (pUserId == null) {
      String errorMsg = ResourceUtils.getMsgResource(NULL_USER_ID,
                                                     RESOURCE_BUNDLE_NAME,
                                                     sResourceBundle);
      throw new IllegalArgumentException(errorMsg);
    }

    if (pGearDefId == null) {
      String errorMsg = ResourceUtils.getMsgResource(NULL_GEAR_DEF_ID,
                                                     RESOURCE_BUNDLE_NAME,
                                                     sResourceBundle);
      throw new IllegalArgumentException(errorMsg);
    }

    Object[] params = new Object[3];
    params[0] = pGearDefId;
    params[1] = pGearInstanceId;
    params[2] = pUserId;
    
    RepositoryView userServiceView = 
      getRepository().getView(getPropertyManager().getUserServiceItemDescriptorName());

    RepositoryItem[] items = 
      sUserServiceStatement.executeQuery(userServiceView, params);

    if (items == null || items.length == 0) {
      return null;
    }
    else if (items.length == 1) {
      return items[0];
    }
    else {
      String errorMsg = ResourceUtils.getMsgResource(MULTIPLE_REPOSITORY_ITEMS,
                                                     RESOURCE_BUNDLE_NAME,
                                                     sResourceBundle);
      throw new RepositoryException(errorMsg);
    }
  }
  
  /**
   * This will create a InstallServiceConfiguration repository
   * item in the repository obtained from the <code>getRepository()</code>
   * method.
   *
   * @param pUsername a username to populate the repository item with
   * @param pPassword a password to populate the repository item with
   * @param pTargetServiceURL end point url
   * @param pTargetMethodName target method name
   * @param pTargetNamespaceURI namespace the method lives in
   * @param pSOAPActionURI action header for soap request
   * @param pServiceParameters parameter name to class type map
   * @return a repository item which has been added to the repository
   * @exception RepositoryException if an error occurs
   */
  public RepositoryItem createInstallServiceConfig(String pGearDefId,
                                                   String pUsername,
                                                   String pPassword,
                                                   String pTargetServiceURL,
                                                   String pTargetMethodName,
                                                   String pTargetNamespaceURI,
                                                   String pSOAPActionURI,
                                                   Collection pServiceParameters)
    throws RepositoryException
  {
    try {
      SOAPPropertyManager propMgr = getPropertyManager();
      
      // only property we need is the gearDefId
      if (pGearDefId == null) {
        String errorMsg = ResourceUtils.getMsgResource(NULL_GEAR_DEF_ID,
                                                       RESOURCE_BUNDLE_NAME,
                                                       sResourceBundle);
        throw new IllegalArgumentException(errorMsg);
      }
      
      TransactionDemarcation td = new TransactionDemarcation();
      try {
        td.begin(getTransactionManager());

        MutableRepositoryItem item = 
          getRepository().createItem(propMgr.getInstallServiceItemDescriptorName());
        // set the property unique to installServiceConfig
        item.setPropertyValue(propMgr.getGearDefIdPropertyName(), pGearDefId);
        
        MutableRepositoryItem serviceConfig = 
          getRepository().createItem(propMgr.getServiceConfigurationItemDescriptorName());
        
        item.setPropertyValue(propMgr.getServiceConfigPropertyName(),
                              serviceConfig);
        
        // set common properties
        setServiceConfigProperties(serviceConfig, pUsername, pPassword, 
                                   pTargetServiceURL, pTargetMethodName, 
                                   pTargetNamespaceURI,
                                   pSOAPActionURI, pServiceParameters);
        
        
        return getRepository().addItem(item);  
      }
      finally {
        td.end();
      }
    }
    catch (TransactionDemarcationException tde) {
      if (isLoggingError()) {
        logError(tde);
      }
      return null;
    }
  }

  /**
   * This will create a InstanceServiceConfiguration repository
   * item in the repository obtained from the <code>getRepository()</code>
   * method.
   *
   * @param pUsername a username to populate the repository item with
   * @param pPassword a password to populate the repository item with
   * @param pTargetServiceURL end point url
   * @param pTargetMethodName target method name
   * @param pTargetNamespaceURI namespace the method lives in
   * @param pSOAPActionURI action header for soap request
   * @param pServiceParameters parameter name to class type map
   * @return a repository item
   * @exception RepositoryException if an error occurs
   */
  public RepositoryItem createInstanceServiceConfig(String pGearInstanceId,
                                                    String pUsername,
                                                    String pPassword,
                                                    String pTargetServiceURL,
                                                    String pTargetMethodName,
                                                    String pTargetNamespaceURI,
                                                    String pSOAPActionURI,
                                                    Collection pServiceParameters)
    throws RepositoryException
  {
    try {
      SOAPPropertyManager propMgr = getPropertyManager();
      
      // only property we need is the gearDefId
      if (pGearInstanceId == null) {
        String errorMsg = ResourceUtils.getMsgResource(NULL_GEAR_INSTANCE_ID,
                                                       RESOURCE_BUNDLE_NAME,
                                                       sResourceBundle);
        throw new IllegalArgumentException(errorMsg);
      }
      
      TransactionDemarcation td = new TransactionDemarcation();
      try {
        td.begin(getTransactionManager());

        MutableRepositoryItem item = 
          getRepository().createItem(propMgr.getInstanceServiceItemDescriptorName());
        // set the property unique to installServiceConfig
        item.setPropertyValue(propMgr.getGearInstancePropertyName(), 
                              pGearInstanceId);
        
        MutableRepositoryItem serviceConfig = 
          getRepository().createItem(propMgr.getServiceConfigurationItemDescriptorName());
        
        item.setPropertyValue(propMgr.getServiceConfigPropertyName(),
                              serviceConfig);
        
        // set common properties
        setServiceConfigProperties(serviceConfig, pUsername, pPassword, 
                                   pTargetServiceURL,
                                   pTargetMethodName, pTargetNamespaceURI,
                                   pSOAPActionURI, pServiceParameters);
        
      
        return getRepository().addItem(item);  
      }
      finally {
        td.end();
      }
    }
    catch (TransactionDemarcationException tde) {
      if (isLoggingError()) {
        logError(tde);
      }

      return null;
    }
  }

  /**
   * This will create a UserServiceConfiguration repository
   * item in the repository obtained from the <code>getRepository()</code>
   * method.
   *
   * @param pUsername a username to populate the repository item with
   * @param pPassword a password to populate the repository item with
   * @param pTargetServiceURL end point url
   * @param pTargetMethodName target method name
   * @param pTargetNamespaceURI namespace the method lives in
   * @param pSOAPActionURI action header for soap request
   * @param pServiceParameters parameter name to class type map
   * @return a repository item
   * @exception RepositoryException if an error occurs
   */
  public RepositoryItem createUserServiceConfig(String pGearDefId,
                                                String pGearInstanceId,
                                                String pUserId,
                                                String pUsername,
                                                String pPassword,
                                                String pTargetServiceURL,
                                                String pTargetMethodName,
                                                String pTargetNamespaceURI,
                                                String pSOAPActionURI,
                                                Collection pServiceParameters)
    throws RepositoryException
  {
    try {
      SOAPPropertyManager propMgr = getPropertyManager();
      
      // only property we need is the gearDefId
      if (pGearInstanceId == null) {
        String errorMsg = ResourceUtils.getMsgResource(NULL_GEAR_INSTANCE_ID,
                                                       RESOURCE_BUNDLE_NAME,
                                                       sResourceBundle);
        throw new IllegalArgumentException(errorMsg);
      }
      
      // only property we need is the gearDefId
      if (pUserId == null) {
        String errorMsg = ResourceUtils.getMsgResource(NULL_USER_ID,
                                                       RESOURCE_BUNDLE_NAME,
                                                       sResourceBundle);
        throw new IllegalArgumentException(errorMsg);
      }

      if (pGearDefId == null) {
        String errorMsg = ResourceUtils.getMsgResource(NULL_GEAR_DEF_ID,
                                                       RESOURCE_BUNDLE_NAME,
                                                       sResourceBundle);
        throw new IllegalArgumentException(errorMsg);
      }

      TransactionDemarcation td = new TransactionDemarcation();
      try {
        td.begin(getTransactionManager());

        MutableRepositoryItem item = 
          getRepository().createItem(propMgr.getUserServiceItemDescriptorName());
        // set the property unique to installServiceConfig
        item.setPropertyValue(propMgr.getGearInstancePropertyName(), 
                              pGearInstanceId);
        item.setPropertyValue(propMgr.getUserIdPropertyName(), 
                              pUserId);
        item.setPropertyValue(propMgr.getGearDefIdPropertyName(), 
                              pGearDefId);
        
        MutableRepositoryItem serviceConfig = 
          getRepository().createItem(propMgr.getServiceConfigurationItemDescriptorName());
        
        
        item.setPropertyValue(propMgr.getServiceConfigPropertyName(),
                              serviceConfig);
        
        // set common properties
        setServiceConfigProperties(serviceConfig, pUsername, pPassword, 
                                   pTargetServiceURL,
                                   pTargetMethodName, pTargetNamespaceURI,
                                   pSOAPActionURI, pServiceParameters);
        
        
        return getRepository().addItem(item);  
      }
      finally {
        td.end();
      }
    }
    catch (TransactionDemarcationException tde) {
      if (isLoggingError()) {
        logError(tde);
      }
      
      return null;
    }
  }

  /**
   * All methods that create and populate information into either
   * a installconfig, instanceconfig or userconfig share a common
   * set of properties.  The setting of these common properties
   * has been factored into this method.
   *
   * @param pItem a <code>MutableRepositoryItem</code> value
   * @param pUserId a <code>String</code> value
   * @param pUsername a <code>String</code> value
   * @param pPassword a <code>String</code> value
   * @param pTargetServiceURL a <code>String</code> value
   * @param pTargetMethodName a <code>String</code> value
   * @param pTargetNamespaceURI a <code>String</code> value
   * @param pSOAPActionURI a <code>String</code> value
   * @param pServiceParameters a <code>Collection</code> value
   * @exception RepositoryException if an error occurs
   */
  protected void setServiceConfigProperties(MutableRepositoryItem pItem,
                                            String pUsername,
                                            String pPassword,
                                            String pTargetServiceURL,
                                            String pTargetMethodName,
                                            String pTargetNamespaceURI,
                                            String pSOAPActionURI,
                                            Collection pServiceParameters)
    throws RepositoryException
  {
    String propName;
    String propType;
    Collection params;
    WSDLSOAPParameter param;
    SOAPPropertyManager propMgr = getPropertyManager();

    pItem.setPropertyValue(propMgr.getUsernamePropertyName(), pUsername);
    pItem.setPropertyValue(propMgr.getPasswordPropertyName(), pPassword);
    pItem.setPropertyValue(propMgr.getTargetURLPropertyName(), 
                           pTargetServiceURL);
    pItem.setPropertyValue(propMgr.getTargetMethodNamePropertyName(),
                           pTargetMethodName);
    pItem.setPropertyValue(propMgr.getTargetNamespacePropertyName(),
                           pTargetNamespaceURI);
    pItem.setPropertyValue(propMgr.getSOAPActionURIPropertyName(),
                           pSOAPActionURI);

    // set any serviceParameter values if we have them
    if (pServiceParameters != null && pServiceParameters.size() > 0) {
      Iterator propNameIterator = pServiceParameters.iterator();
      while (propNameIterator.hasNext()) {
        param = (WSDLSOAPParameter) propNameIterator.next();
        propName = param.getParameterName();
        propType = param.getParameterClassType();
        RepositoryItem serviceParamItem = createServiceParamItem(propName,
                                                                 propType);
        params = (Collection)
          pItem.getPropertyValue(propMgr.getServiceParametersPropertyName());
        params.add(serviceParamItem);
      }
    }
  }
                                                   
  /**
   * Create a new serviceParam repository item and set its
   * paramName and paramType properties to the passed in parameters
   *
   * @param pParamName the name of the parameter
   * @param pParamType the class type of the parameter
   * @return a <code>RepositoryItem</code> value
   * @exception RepositoryException if an error occurs
   */
  protected RepositoryItem createServiceParamItem(String pParamName,
                                                  String pParamType)
    throws RepositoryException
  {
    SOAPPropertyManager propMgr = getPropertyManager();

    MutableRepositoryItem serviceParamItem = 
      getRepository().createItem(propMgr.getServiceParamItemDescriptorName());
    
    serviceParamItem.setPropertyValue(propMgr.getParamNamePropertyName(),
                                      pParamName);
    serviceParamItem.setPropertyValue(propMgr.getParamTypePropertyName(),
                                      pParamType);
    
    return getRepository().addItem(serviceParamItem);
  }

  /*
   * This method removes serviceParameter items from all the user items
   *  which have the given gear definition id.
   * @param pGearDefId the gear definition id for which we have to remove serviceParams
   * @return void
   * @throws RepositoryException if it fails removing serviceParameter items
   */
  public void deleteUserServiceParameters(String pGearDefId)
    throws RepositoryException
  {
    if ( isLoggingDebug())
      logDebug("in deleteUserServiceParameters(pGearDefId) with geardefid = " + pGearDefId);
    
    // make sure we got a valid parameter
    if (pGearDefId == null) {
      String errorMsg = ResourceUtils.getMsgResource(NULL_GEAR_DEF_ID,
                                                     RESOURCE_BUNDLE_NAME,
                                                     sResourceBundle);
      throw new IllegalArgumentException(errorMsg);
    }

    Object[] params = new Object[1];
    params[0] = pGearDefId;
    TransactionDemarcation td = new TransactionDemarcation();
    try {
      try{
        td.begin(getTransactionManager());

        RepositoryView userServiceView = 
          getRepository().getView(getPropertyManager().getUserServiceItemDescriptorName());

        RepositoryItem[] items = 
          sGearDefinitionPropertyInUser.executeQuery(userServiceView, params);

        if (items == null || items.length == 0) {
          if ( isLoggingDebug()) {
            logDebug("No User Items found for gear definition Id:" + pGearDefId);
          } // end of if ()
          return;
        }

        RepositoryItem serviceConfig = null;
        // iterate through userService Repository Items.
        for ( int i = 0; i < items.length; i++) {

          getRepository().removeItem(items[i].getRepositoryId(),
                               getPropertyManager().getUserServiceItemDescriptorName());

        } // end of for ()
      }
      finally {
        td.end();
      }
    }
    catch (TransactionDemarcationException tde) {
      if (isLoggingError()) {
        logError(tde);
      }
    }

  }

  /*
   * This method deletes the installServiceConfiguration item corresponding to the
   * given gearDefId.
   * @param pGearDefId the id of gear definition
   * @return void
   * @throws RepositoryException if there is any error in removing the item.
   *
   */
  public void removeInstallServiceConfiguration(String pGearDefId)
  throws RepositoryException
  {
    TransactionDemarcation td = new TransactionDemarcation();
    try {
      try{
        td.begin(getTransactionManager());

        RepositoryItem installServiceConfigItem = getInstallServiceConfig(pGearDefId);
        if ( installServiceConfigItem == null) {
          return;
        } // end of if ()
    
        getRepository().removeItem(installServiceConfigItem.getRepositoryId(),
                                   getPropertyManager().getInstallServiceItemDescriptorName());
      }
      finally {
        td.end();
      }
    }
    catch (TransactionDemarcationException tde) {
      if (isLoggingError()) {
        logError(tde);
      }
    }

  }
}   // end of class






