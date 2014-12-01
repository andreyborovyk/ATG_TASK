/*<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of thisadd
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

package atg.portal.gear.repview;

/**
* Form handler which does validation on values before saving them as gear 
* config parameters for the RepView Gear instanceConfig form
* 
* @author Andy Jacobs
 * @version $Id: //app/portal/version/10.0.3/repview/classes.jar/src/atg/portal/gear/repview/RepViewGearConfigFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
*/

import java.util.Map;

import atg.repository.*;
import atg.targeting.DynamicContentTargeter;
import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import atg.portal.framework.GearConfigFormHandler;
import atg.portal.nucleus.NucleusComponents;


public class RepViewGearConfigFormHandler extends GearConfigFormHandler
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/repview/classes.jar/src/atg/portal/gear/repview/RepViewGearConfigFormHandler.java#2 $$Change: 651448 $";


  public static final String REPOSITORY_MODE = "repository";
  public static final String DELEGATE_MODE = "delegate";
  public static final String RESOURCE_BUNDLE_MODE = "resourceBundle";
  public static final String FEATURED_ITEM_MODE = "featuredItem";
  public static final String SHORT_LIST_MODE = "shortList";
  public static final String FULL_LIST_MODE = "fullList";
  public static final String DISPLAY_ITEM_MODE = "displayItem";
  public static final String APPEARANCE_MODE = "appearance";
  public static final String FUNCTIONALITY_MODE = "functionality";
  public static final String USER_CONFIG_MODE = "userconfig";

  private static final String FEATURED_ITEM_DISPLAY_PROPERTY_NAMES = "featuredItemDisplayPropertyNames";
  private static final String FULL_LIST_DISPLAY_PROPERTY_NAMES = "fullListDisplayPropertyNames";
  private static final String SHORT_LIST_DISPLAY_PROPERTY_NAMES = "shortListDisplayPropertyNames";
  private static final String DETAIL_DISPLAY_PROPERTY_NAMES = "detailDisplayPropertyNames";
  private static final String REPOSITORY_PATH = "repositoryPath";
  private static final String ITEM_DESCRIPTOR_NAME = "itemDescriptorName";
  private static final String SHORT_LIST_TARGETER = "shortListTargeter";
  private static final String FEATURED_ITEM_TARGETER = "featuredItemTargeter";
  private static final String FULL_LIST_TARGETER = "fullListTargeter";

  // I18n Resource specific constants.
  private static final String REPVIEW_CONFIG_RESOURCE_FILE = "atg.portal.gear.repview.RepViewConfigFormResources";

  private static final String ERR_INVALID_MODE = "invalidMode";
  private static final String ERR_INVALID_TARGETER = "invalidTargeter";
  private static final String ERR_INVALID_PROPERTY_LIST = "invalidPropertyList";
  
  private static final String ERR_INVALID_REPOSITORY = "invalidRepository";
  private static final String ERR_INVALID_ITEM_DESCRIPTOR = "invalidItemDescriptor";

  private static final String ERR_SHORTLISTSIZEFORMATEXCEPTION = "errShortListSizeFormatException";
  private static final String ERR_FULLLISTSIZEFORMATEXCEPTION = "errFullListSizeFormatException";
  
  private static final String RESET_FEATURED_ITEM_LIST = "resetFeaturedItemList";
  private static final String RESET_FULL_LIST = "resetFullList";
  private static final String RESET_SHORT_LIST = "resetShortList";
  private static final String RESET_DETAIL_LIST = "resetDetailList";
  
  //-------------------------------------
  // properties
  //-------------------------------------
  private String mMode;

  public void setMode(String pMode)
  {
    mMode = pMode;
  }

  public String getMode()
  {
    return mMode;
  }
  
  
  //-------------------------------------
  // methods
  //-------------------------------------

  public void preConfirm(DynamoHttpServletRequest pRequest,
                         DynamoHttpServletResponse pResponse)
    throws javax.servlet.ServletException, java.io.IOException
  {
    /** Overriding this atg.portal.framework.GearConfigFormHandler method*/

    // Make sure everything is cleared out.
    //resetFormExceptions();
    //getFailureMessageProcessor().clear();
    //getSuccessMessageProcessor().clear();

    if(getMode() == null)
    {
      if(isLoggingDebug())
        logDebug("NULL Mode provided");
      
      // Invalid Mode provided return an error,  Note if given this then
      // typically a page change needs to be made.
      addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_INVALID_MODE,null);
    }
    else if(getMode().equals(FUNCTIONALITY_MODE))
    {
      // No validation required
    }
    else if(getMode().equals(APPEARANCE_MODE))
    {
      // No validation needed here,  altough we could validate the resource
      // bundle here by trying to make use of it.
    }
    else if(getMode().equals(REPOSITORY_MODE))
    {
      handleRepositoryMode(pRequest, pResponse);
    }
    else if(getMode().equals(DELEGATE_MODE))
    {
      handleDelegateMode(pRequest, pResponse);
    }
    else if(getMode().equals(RESOURCE_BUNDLE_MODE))
    {
      // No validation needed here
    }
    else if(getMode().equals(FEATURED_ITEM_MODE))
    {
      handleFeaturedItemMode(pRequest, pResponse);
    }
    else if(getMode().equals(SHORT_LIST_MODE))
    {
      handleShortListMode(pRequest, pResponse);
    }
    else if(getMode().equals(FULL_LIST_MODE))
    {
      handleFullListMode(pRequest, pResponse);
    }
    else if(getMode().equals(DISPLAY_ITEM_MODE))
    {
      handleDetailListMode(pRequest, pResponse);
    }
    else if(getMode().equals(USER_CONFIG_MODE))
    {
      handleUserConfigMode(pRequest,pResponse);
    }
    else
    {
      if(isLoggingDebug())
        logDebug("Invalid Mode provided");
      
      // Invalid Mode provided return an error,  Note if given this then
      // typically a page change needs to be made.
      addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_INVALID_MODE,null);
    }
  }

  /**
   *  Handle the processing of the user config mode.
   */
  private void handleUserConfigMode(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
    throws javax.servlet.ServletException, java.io.IOException
  {
    Map values = getValues();
    if(values != null) {
      //FullListSize
      try { 
        String fullListSizeValue = (String)values.get("fullListSize"); 
        if(fullListSizeValue != null) {
          Integer fullListSize = Integer.valueOf(fullListSizeValue);
        }
      } catch(NumberFormatException nfe) {
        addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_FULLLISTSIZEFORMATEXCEPTION,null);
      }
      //ShortListSize
      try { 
        String shortListSizeValue = (String)values.get("shortListSize"); 
        if(shortListSizeValue != null) {
          Integer shortListSize = Integer.valueOf(shortListSizeValue);
        }
      } catch(NumberFormatException nfe) {
        addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_SHORTLISTSIZEFORMATEXCEPTION,null);
      }
      
    }     
  }

  /**
   *  Handle the processing of the Repository mode.
   */
  private void handleRepositoryMode(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
    throws javax.servlet.ServletException, java.io.IOException
  {
    Map values = getValues();
    
    String repositoryPath = (String) getValues().get("repositoryPath");
    String itemDescriptorName = (String) getValues().get("itemDescriptorName");
    
    if(isLoggingDebug())
    {
      logDebug("RepositoryPath = " + repositoryPath);
      logDebug("ItemDescriptorName = " + itemDescriptorName);
    }
    
    Repository repository = getRepository(repositoryPath);
    RepositoryItemDescriptor itemDescriptor = getItemDescriptor(repository, itemDescriptorName);
    
    if(repository == null)
    {
      if(isLoggingDebug())
        logDebug("Failed to validate repository " + repositoryPath);
      
      // Throw an error and return to the page.
      addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_INVALID_REPOSITORY,null);
    }
    
    if(itemDescriptor == null)
    {
      if(isLoggingDebug())
        logDebug("Failed to validate item descriptor " + itemDescriptorName);
      
      // Throw an error and return to the page.
      addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_INVALID_ITEM_DESCRIPTOR,null);
    }
    
    // Ok now that we have validated the repository and the item descriptor
    // check the property lists to see if they are valid or not.  If they
    // are not then set them to ALL and report the fact that they have been
    // updated.
    String featuredItemList = null;
    String fullList = null;
    String shortList = null;
    String detailList = null;
    
    if(getParamType().equals("instance") && isSettingDefaultValues())
    {
      // Get the parameters needed from the GearDefintion.
      featuredItemList = getGearEnvironment(pRequest,pResponse).getGearInstanceDefaultValue(FEATURED_ITEM_DISPLAY_PROPERTY_NAMES);
      fullList = getGearEnvironment(pRequest,pResponse).getGearInstanceDefaultValue(FULL_LIST_DISPLAY_PROPERTY_NAMES);
      shortList = getGearEnvironment(pRequest,pResponse).getGearInstanceDefaultValue(SHORT_LIST_DISPLAY_PROPERTY_NAMES);
      detailList = getGearEnvironment(pRequest,pResponse).getGearInstanceDefaultValue(DETAIL_DISPLAY_PROPERTY_NAMES);
      
      
      if(isLoggingDebug())
      {
        logDebug("Mode is instance mode,  updating GearDefinition");
        logDebug("FeaturedItemList = " + featuredItemList);
        logDebug("FullList = " + fullList);
        logDebug("ShortList = " + shortList);
        logDebug("DetailList = " + detailList);
      }

      if(!validatePropertyList(itemDescriptor, featuredItemList))
      {
        if(isLoggingDebug())
          logDebug("Updating featured item list to ALL");
        
        values.put(FEATURED_ITEM_DISPLAY_PROPERTY_NAMES, "ALL");
        addSuccessMessage(REPVIEW_CONFIG_RESOURCE_FILE,RESET_FEATURED_ITEM_LIST,null);
      }
      
      if(!validatePropertyList(itemDescriptor, fullList))
      {
        if(isLoggingDebug())
          logDebug("Updating full list to ALL");
        
        values.put(FULL_LIST_DISPLAY_PROPERTY_NAMES, "ALL");
        addSuccessMessage(REPVIEW_CONFIG_RESOURCE_FILE,RESET_FULL_LIST,null);
      }
      
      if(!validatePropertyList(itemDescriptor, shortList))
      {
        if(isLoggingDebug())
          logDebug("Updating short list to ALL");
        
        values.put(SHORT_LIST_DISPLAY_PROPERTY_NAMES, "ALL");
        addSuccessMessage(REPVIEW_CONFIG_RESOURCE_FILE,RESET_SHORT_LIST,null);
      }
      
      if(!validatePropertyList(itemDescriptor, detailList))
      {
        if(isLoggingDebug())
          logDebug("Updating detail list to ALL");
        
        values.put(DETAIL_DISPLAY_PROPERTY_NAMES, "ALL");
        addSuccessMessage(REPVIEW_CONFIG_RESOURCE_FILE,RESET_DETAIL_LIST,null);
      }
    }
    else
    {
      // Get the parameters needed from the Gear.
      featuredItemList = getGearEnvironment(pRequest,pResponse).getGearInstanceParameter(FEATURED_ITEM_DISPLAY_PROPERTY_NAMES);
      fullList = getGearEnvironment(pRequest,pResponse).getGearInstanceParameter(FULL_LIST_DISPLAY_PROPERTY_NAMES);
      shortList = getGearEnvironment(pRequest,pResponse).getGearInstanceParameter(SHORT_LIST_DISPLAY_PROPERTY_NAMES);
      detailList = getGearEnvironment(pRequest,pResponse).getGearInstanceParameter(DETAIL_DISPLAY_PROPERTY_NAMES);
      
      if(isLoggingDebug())
      {
        logDebug("Mode is instance mode,  updating Gear");
        logDebug("FeaturedItemList = " + featuredItemList);
        logDebug("FullList = " + fullList);
        logDebug("ShortList = " + shortList);
        logDebug("DetailList = " + detailList);
      }

      // Note we should set the customFeaturedItemDisplayPage to null
      values.put("customFeaturedItemDisplayPage", null);
      
      if(!validatePropertyList(itemDescriptor, featuredItemList))
      {
        if(isLoggingDebug())
          logDebug("Updating featured item list to ALL");
        
        values.put(FEATURED_ITEM_DISPLAY_PROPERTY_NAMES, "ALL");
        addSuccessMessage(REPVIEW_CONFIG_RESOURCE_FILE,RESET_FEATURED_ITEM_LIST,null);
      }
      
      if(!validatePropertyList(itemDescriptor, fullList))
      {
        if(isLoggingDebug())
          logDebug("Updating full item list to ALL");
        
        values.put(FULL_LIST_DISPLAY_PROPERTY_NAMES, "ALL");
        addSuccessMessage(REPVIEW_CONFIG_RESOURCE_FILE,RESET_FULL_LIST,null);
      }
      
      if(!validatePropertyList(itemDescriptor, shortList))
      {
        if(isLoggingDebug())
          logDebug("Updating short item list to ALL");
        
        values.put(SHORT_LIST_DISPLAY_PROPERTY_NAMES, "ALL");
        addSuccessMessage(REPVIEW_CONFIG_RESOURCE_FILE,RESET_SHORT_LIST,null);
      }

      // Note we are changing the Repository so we should set the customItemDisplayPage to null
      values.put("customItemDisplayPage", null);
      
      if(!validatePropertyList(itemDescriptor, detailList))
      {
        if(isLoggingDebug())
          logDebug("Updating detail item list to ALL");
        
        values.put(DETAIL_DISPLAY_PROPERTY_NAMES, "ALL");
        addSuccessMessage(REPVIEW_CONFIG_RESOURCE_FILE,RESET_DETAIL_LIST,null);
      }
    }
    // Ok make sure the new values are added.
    setValues(values);
  }

  /**
   *  
   */
  private void handleDelegateMode(DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse)
    throws javax.servlet.ServletException, java.io.IOException
  {
    // Check to see if we are modifying a Gear instance.  If we are then
    // we need to revert back to the GearDefinition settings.  Turning off
    // delegation at this level reverts everything back to the gearDef.
    // Note to get back to this mode the gearDef must have delegation turned on
    if(getParamType().equals("instance") && !isSettingDefaultValues())
    {
      // If we are turning Delegate Mode off then we need to make sure that we
      // reset all the parameters for the gear instance that override the
      // gearDefinition.
      String delegateConfig = (String) getValues().get("delegateConfig");

      // We are going to check on the side of error if is is anything other
      // than true we are going to assume false.
      if(!delegateConfig.equalsIgnoreCase("true"))
      {
        Map values = getValues();
        
        // Reset the property values of the gear
        values.put(REPOSITORY_PATH, null);
        values.put(ITEM_DESCRIPTOR_NAME, null);
        values.put(SHORT_LIST_DISPLAY_PROPERTY_NAMES, null);
        values.put(FULL_LIST_DISPLAY_PROPERTY_NAMES, null);
        values.put(DETAIL_DISPLAY_PROPERTY_NAMES, null);
        values.put(FEATURED_ITEM_DISPLAY_PROPERTY_NAMES, null);
        values.put(SHORT_LIST_TARGETER, null);
        values.put(FEATURED_ITEM_TARGETER, null);
        values.put(FULL_LIST_TARGETER, null);
        values.put("customItemDisplayPage", null);
        values.put("customFeaturedItemDisplayPage", null);

        setValues(values);
      }
    }
  }
  
  /**
   *  Handle the processing of the Repository mode.
   *  validate the repository, item descriptor targeter and property list.
   */
  private void handleFeaturedItemMode(DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse)
    throws javax.servlet.ServletException, java.io.IOException
  {
    String repositoryPath;
    String itemDescriptorName;

    String propertyList = (String) getValues().get("featuredItemDisplayPropertyNames");
    String targeterName = (String) getValues().get("featuredItemTargeter");

    // Are we tying to set the GearDefinition or the GearInstance
    if(getParamType().equals("instance") && isSettingDefaultValues())
    {
      // Get the parameters needed from the GearDefintion.
      repositoryPath = getGearEnvironment(pRequest,pResponse).getGearInstanceDefaultValue(REPOSITORY_PATH);
      itemDescriptorName = getGearEnvironment(pRequest,pResponse).getGearInstanceDefaultValue(ITEM_DESCRIPTOR_NAME);
    }
    else
    {
      // Get the parameters needed from the Gear.
      repositoryPath = getGearEnvironment(pRequest,pResponse).getGearInstanceParameter(REPOSITORY_PATH);
      itemDescriptorName = getGearEnvironment(pRequest,pResponse).getGearInstanceParameter(ITEM_DESCRIPTOR_NAME);
    }

    if(isLoggingDebug())
    {
      logDebug("RepositoryPath = " + repositoryPath);
      logDebug("ItemDescriptorName = " + itemDescriptorName);
    }
    
    Repository repository = getRepository(repositoryPath);
    RepositoryItemDescriptor itemDescriptor = getItemDescriptor(repository, itemDescriptorName);

    // Check to make sure we got a valid Repository
    if(repository == null)
    {
      if(isLoggingDebug())
        logDebug("Failed to validate propertyList " + propertyList);
      
      // Throw an error and return to the page.
      addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_INVALID_REPOSITORY,null);
    }

    // Check to make sure we have a valid itemDescriptor
    if(itemDescriptor == null)
    {
      if(isLoggingDebug())
        logDebug("Invalid item Descriptor " + itemDescriptor);
      
      // Throw an error and return to the page.
      addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_INVALID_ITEM_DESCRIPTOR,null);
    }

    // Validate the featured item property list
    if(!validatePropertyList(itemDescriptor,propertyList))
    {
      if(isLoggingDebug())
        logDebug("Failed to validate propertyList " + propertyList);
      
      // Throw an error and return to the page.
      addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_INVALID_PROPERTY_LIST,null);
    }

    // Validate the featured item targeter
    if(!validateTargeter(propertyList,repository,itemDescriptorName,targeterName))
    {
      if(isLoggingDebug())
        logDebug("Failed to validate targeter " + targeterName);
      
      // Throw an error and return to the page.
      addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_INVALID_TARGETER,null);
    }
  }

  
  /**
   *  Handle the processing of the Short List mode.
   *  validate the repository, item descriptor targeter and property list.
   */
  private void handleShortListMode(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
    throws javax.servlet.ServletException, java.io.IOException
  {
    String repositoryPath;
    String itemDescriptorName;

    String propertyList = (String) getValues().get("shortListDisplayPropertyNames");
    String targeterName = (String) getValues().get("shortListTargeter");

    // Are we tying to set the GearDefinition or the GearInstance
    if(getParamType().equals("instance") && isSettingDefaultValues())
    {
      // Get the parameters needed from the GearDefintion.
      repositoryPath = getGearEnvironment(pRequest,pResponse).getGearInstanceDefaultValue(REPOSITORY_PATH);
      itemDescriptorName = getGearEnvironment(pRequest,pResponse).getGearInstanceDefaultValue(ITEM_DESCRIPTOR_NAME);
    }
    else
    {
      // Get the parameters needed from the Gear.
      repositoryPath = getGearEnvironment(pRequest,pResponse).getGearInstanceParameter(REPOSITORY_PATH);
      itemDescriptorName = getGearEnvironment(pRequest,pResponse).getGearInstanceParameter(ITEM_DESCRIPTOR_NAME);
    }

    if(isLoggingDebug())
    {
      logDebug("RepositoryPath = " + repositoryPath);
      logDebug("ItemDescriptorName = " + itemDescriptorName);
    }
    
    Repository repository = getRepository(repositoryPath);
    RepositoryItemDescriptor itemDescriptor = getItemDescriptor(repository, itemDescriptorName);

    // Check to make sure we got a valid Repository
    if(repository == null)
    {
      if(isLoggingDebug())
        logDebug("Failed to validate propertyList " + propertyList);
      
      // Throw an error and return to the page.
      addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_INVALID_REPOSITORY,null);
    }

    // Check to make sure we have a valid itemDescriptor
    if(itemDescriptor == null)
    {
      if(isLoggingDebug())
        logDebug("Invalid item Descriptor " + itemDescriptor);
      
      // Throw an error and return to the page.
      addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_INVALID_ITEM_DESCRIPTOR,null);
    }

    // Validate the featured item property list
    if(!validatePropertyList(itemDescriptor,propertyList))
    {
      if(isLoggingDebug())
        logDebug("Failed to validate propertyList " + propertyList);
      
      // Throw an error and return to the page.
      addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_INVALID_PROPERTY_LIST,null);
    }

    // Validate the featured item targeter
    if(!validateTargeter(propertyList,repository,itemDescriptorName,targeterName))
    {
      if(isLoggingDebug())
        logDebug("Failed to validate targeter " + targeterName);
      
      // Throw an error and return to the page.
      addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_INVALID_TARGETER,null);
    }
  }

  /**
   *  Handle the processing of the Full List  mode.
   *  validate the repository, item descriptor targeter and property list.
   */
  private void handleFullListMode(DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse)
    throws javax.servlet.ServletException, java.io.IOException
  {
    String repositoryPath;
    String itemDescriptorName;

    String propertyList = (String) getValues().get("fullListDisplayPropertyNames");
    String targeterName = (String) getValues().get("fullListTargeter");

    // Are we tying to set the GearDefinition or the GearInstance
    if(getParamType().equals("instance") && isSettingDefaultValues())
    {
      // Get the parameters needed from the GearDefintion.
      repositoryPath = getGearEnvironment(pRequest,pResponse).getGearInstanceDefaultValue(REPOSITORY_PATH);
      itemDescriptorName = getGearEnvironment(pRequest,pResponse).getGearInstanceDefaultValue(ITEM_DESCRIPTOR_NAME);
    }
    else
    {
      // Get the parameters needed from the Gear.
      repositoryPath = getGearEnvironment(pRequest,pResponse).getGearInstanceParameter(REPOSITORY_PATH);
      itemDescriptorName = getGearEnvironment(pRequest,pResponse).getGearInstanceParameter(ITEM_DESCRIPTOR_NAME);
    }

    if(isLoggingDebug())
    {
      logDebug("RepositoryPath = " + repositoryPath);
      logDebug("ItemDescriptorName = " + itemDescriptorName);
    }
    
    Repository repository = getRepository(repositoryPath);
    RepositoryItemDescriptor itemDescriptor = getItemDescriptor(repository, itemDescriptorName);

    // Check to make sure we got a valid Repository
    if(repository == null)
    {
      if(isLoggingDebug())
        logDebug("Failed to validate propertyList " + propertyList);
      
      // Throw an error and return to the page.
      addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_INVALID_REPOSITORY,null);
    }

    // Check to make sure we have a valid itemDescriptor
    if(itemDescriptor == null)
    {
      if(isLoggingDebug())
        logDebug("Invalid item Descriptor " + itemDescriptor);
      
      // Throw an error and return to the page.
      addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_INVALID_ITEM_DESCRIPTOR,null);
    }

    // Validate the featured item property list
    if(!validatePropertyList(itemDescriptor,propertyList))
    {
      if(isLoggingDebug())
        logDebug("Failed to validate propertyList " + propertyList);
      
      // Throw an error and return to the page.
      addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_INVALID_PROPERTY_LIST,null);
    }

    // Validate the featured item targeter
    if(!validateTargeter(propertyList,repository,itemDescriptorName,targeterName))
    {
      if(isLoggingDebug())
        logDebug("Failed to validate targeter " + targeterName);
      
      // Throw an error and return to the page.
      addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_INVALID_TARGETER,null);
    }
  }

 /**
   *  Handle the processing of the Full List  mode.
   *  validate the repository, item descriptor targeter and property list.
   */
  private void handleDetailListMode(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
    throws javax.servlet.ServletException, java.io.IOException
  {
    String repositoryPath;
    String itemDescriptorName;

    String propertyList = (String) getValues().get("detailDisplayPropertyNames");

    // Are we tying to set the GearDefinition or the GearInstance
    if(getParamType().equals("instance") && isSettingDefaultValues())
    {
      // Get the parameters needed from the GearDefintion.
      repositoryPath = getGearEnvironment(pRequest,pResponse).getGearInstanceDefaultValue(REPOSITORY_PATH);
      itemDescriptorName = getGearEnvironment(pRequest,pResponse).getGearInstanceDefaultValue(ITEM_DESCRIPTOR_NAME);
    }
    else
    {
      // Get the parameters needed from the Gear.
      repositoryPath = getGearEnvironment(pRequest,pResponse).getGearInstanceParameter(REPOSITORY_PATH);
      itemDescriptorName = getGearEnvironment(pRequest,pResponse).getGearInstanceParameter(ITEM_DESCRIPTOR_NAME);
    }

    if(isLoggingDebug())
    {
      logDebug("RepositoryPath = " + repositoryPath);
      logDebug("ItemDescriptorName = " + itemDescriptorName);
    }
    
    Repository repository = getRepository(repositoryPath);
    RepositoryItemDescriptor itemDescriptor = getItemDescriptor(repository, itemDescriptorName);

    // Check to make sure we got a valid Repository
    if(repository == null)
    {
      if(isLoggingDebug())
        logDebug("Failed to validate propertyList " + propertyList);
      
      // Throw an error and return to the page.
      addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_INVALID_REPOSITORY,null);
    }

    // Check to make sure we have a valid itemDescriptor
    if(itemDescriptor == null)
    {
      if(isLoggingDebug())
        logDebug("Invalid item Descriptor " + itemDescriptor);
      
      // Throw an error and return to the page.
      addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_INVALID_ITEM_DESCRIPTOR,null);
    }

    // Validate the featured item property list
    if(!validatePropertyList(itemDescriptor,propertyList))
    {
      if(isLoggingDebug())
        logDebug("Failed to validate propertyList " + propertyList);
      
      // Throw an error and return to the page.
      addFailureMessage(REPVIEW_CONFIG_RESOURCE_FILE,ERR_INVALID_PROPERTY_LIST,null);
    }
  }

  /**
   *  Obtain a repository based upon a repository path.
   */
  private Repository getRepository(String pRepositoryPath)
  {
    try
    {
      return (Repository) NucleusComponents.lookup("dynamo:" + pRepositoryPath);
    }
    catch (javax.naming.NamingException e)
    {
    }
    return null;
  }


  /**
   * returns the item descriptor, initializing it if necessary.
   **/
  private RepositoryItemDescriptor getItemDescriptor(Repository pRepository,
                                                     String pItemDescriptorName)
  {
    try
    {
      return pRepository.getItemDescriptor(pItemDescriptorName);
    }
    catch (RepositoryException e)
    {
    }
    return null;
  }
  

  /**
   * if the property list is not related to the given repository and item desc
   * , then remove it.
   */
  private boolean validatePropertyList(RepositoryItemDescriptor pItemDesc,
                                       String                   pPropertyList)
  {
    // Quick check to make sure they gave us a property list.
    if(pPropertyList == null)
      return false;

    // Quick check to see if it is set to display all properties
    if (pPropertyList.equalsIgnoreCase("ALL"))
      return true;
    

    String [] properties = StringUtils.splitStringAtCharacter(StringUtils.normalizeWhitespace(pPropertyList.trim()),' ');
    boolean passed = true;
    
    for (int ii=0; ii < properties.length; ii++)
    {
      if(isLoggingDebug())
        logDebug("Validating property = " + properties[ii]);
      
      if (!pItemDesc.hasProperty(properties[ii]))
      {
        passed = false;
      }
    }
    return passed;
  } 


  /**
   * if the targeter is not related to the given repository and item desc, then
   * remove it.
   */
  private boolean validateTargeter(String     pParamName,
                                   Repository pRepository,
                                   String     pItemDescName,
                                   String     pTargeterName)
  {
    try
    {
      if (pTargeterName != null)
      {
        DynamicContentTargeter targeter = (DynamicContentTargeter) NucleusComponents.lookup("dynamo:" + pTargeterName);
        if (targeter != null &&
            targeter.getRepository().getRepositoryName().equals(pRepository.getRepositoryName()) &&
            targeter.getRepositoryView().getItemDescriptor().getItemDescriptorName().equals(pItemDescName))
        {
          return true;
        }
        else
        {
          // the tageter failed validation
          return false;
        }
      }
      // because the targeter is null we must have a targeter
      return false;
      
    }
    catch (RepositoryException e)
    {
      return false;
      
      //pageContext.getServletContext().log(" RepView Gear: Unable to validate targeters due to repository exception");
    }
    catch (javax.naming.NamingException e)
    {
      return false;
      
      //pageContext.getServletContext().log(" RepView Gear: Unable to validate targeters due to naming exception");

    }
  }
  
  /**
   * Overriding this method to set the gear environment.
   */
  
  public void beforeGet(DynamoHttpServletRequest  pRequest, 
  		        DynamoHttpServletResponse pResponse)
  {
    //mGearEnv = getGearEnvironment(pRequest, pResponse);
    super.beforeGet(pRequest,pResponse);
  }
  
  //-------------------------------------
  // Constructors
  //-------------------------------------
  public RepViewGearConfigFormHandler ()
  {
  }

}
