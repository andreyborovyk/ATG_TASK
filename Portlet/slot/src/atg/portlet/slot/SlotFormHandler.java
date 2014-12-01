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

package atg.portlet.slot;


import atg.servlet.*;
import atg.nucleus.Nucleus;
import atg.nucleus.GenericService;
import atg.core.util.ResourceUtils;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletException;
import javax.portlet.ValidatorException;
import javax.portlet.ReadOnlyException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * Form handler used to modify the portlet settings for the 
 * the slot portlet concrete instance
 *
 * @author Andrew Waltman
 * @version $Id: //app/portal/version/10.0.3/portlet/slot/classes/atg/portlet/slot/SlotFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class SlotFormHandler 
  extends GenericService
{

  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION =
  "$Id: //app/portal/version/10.0.3/portlet/slot/classes/atg/portlet/slot/SlotFormHandler.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  public static final String PORTLET_SLOT_NAME_PROPERTY = "slotComponent";
  public static final String PORTLET_PRESENTATION_PAGE_PROPERTY = "presentationPage";

  // Constants used for logged error and debug messages
  protected static final String DEBUG_SAVED_PORTLET_SETTINGS = "savedPortletSettings";
  protected static final String DEBUG_SLOT_REGISTRY_IS_EMPTY = "slotRegistryIsEmpty";
  protected static final String ERROR_FINDING_SLOT_REGISTRY = "errorFindingSlotRegistry";
  protected static final String ERROR_SAVING_PORTLET_SETTINGS = "errorSavingPortletSettings";
  protected static final String ERROR_SETTING_PORTLET_SETTINGS = "errorSettingPortletSettings";

  private static final String RESOURCE_BUNDLE_NAME = "atg.portlet.slot.SlotResources";
  private static ResourceBundle sResourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);

  //-------------------------------------
  // Member Variables
  //-------------------------------------

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // property: SlotName
  String mSlotName;

  /**
   * Sets property SlotName
   **/
  public void setSlotName(String pSlotName) {
    mSlotName = pSlotName;
  }

  /**
   * Returns property SlotName
   **/
  public String getSlotName() {
    return mSlotName;
  }


  //-------------------------------------
  // property: SlotRegistries
  ArrayList mSlotRegistries;

  /**
   * Sets property SlotRegistries
   **/
  public void setSlotRegistries(ArrayList pSlotRegistries) {
    mSlotRegistries = pSlotRegistries;
  }

  /**
   * Returns property SlotRegistries
   **/
  public ArrayList getSlotRegistries() {
    return mSlotRegistries;
  }

  /**
   * Returns list of available components found in paths listed in 
   * SlotRegistries property.
   */
  public ArrayList getAvailableSlots() {
    ArrayList availableSlotNames = new ArrayList();
    Nucleus nucleus = getNucleus();
    ArrayList slotRegistries = getSlotRegistries();

    // for each item in slot registries do:
    for (int i = 0; i < slotRegistries.size(); i++) {
      String registry = (String)slotRegistries.get(i);
      Dictionary slotDictionary = nucleus.getConfigurations(registry);
      if (slotDictionary != null) {
        if (slotDictionary.isEmpty()) {
          if (isLoggingDebug()) {
            logDebug(ResourceUtils.getMsgResource(DEBUG_SLOT_REGISTRY_IS_EMPTY,
                                                  getResourceBundleName(), 
                                                  getResourceBundle()) +
                     ": " + registry);
         }
        }
        else {
          Enumeration slotKeys = slotDictionary.keys();
          while (slotKeys.hasMoreElements()) {
            Object key = slotKeys.nextElement();
            if (!registry.endsWith("/"))
              registry += "/";

            String fullSlotPath = registry + (String)key;
            availableSlotNames.add(fullSlotPath);
          }
        }
      }
      else {
        if (isLoggingError()) {
          logError(ResourceUtils.getMsgResource(ERROR_FINDING_SLOT_REGISTRY,
                                                getResourceBundleName(), 
                                                getResourceBundle()) + 
                   ": " + registry);
        }
      }
    }
    // end for each

    return availableSlotNames;
  }
  

  //-------------------------------------
  // property: PresentationPage
  String mPresentationPage;

  /**
   * Sets property PresentationPage
   **/
  public void setPresentationPage(String pPresentationPage) {
    mPresentationPage = pPresentationPage;
  }

  /**
   * Returns property PresentationPage
   **/
  public String getPresentationPage() {
    return mPresentationPage;
  }

  //-------------------------------------
  // ResourceBundle support
  //-------------------------------------

  /**
   * Returns the error message ResourceBundle
   */
  protected ResourceBundle getResourceBundle() {
    return sResourceBundle;
  }

  /**
   * Returns the name of the error message ResourceBundle
   */
  protected String getResourceBundleName() {
    return RESOURCE_BUNDLE_NAME;
  }

  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instanceof SlotFormHandler
   */
  public SlotFormHandler() {
  }

  /**
   * Handle the slot update
   * @param pRequest the portletrequest
   * @param pResponse the portlet response
   * @exception PortletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleUpdate(PortletRequest pRequest,
                              PortletResponse pResponse)
    throws PortletException, java.io.IOException
  {
    PortletPreferences preferences = pRequest.getPreferences();

    String slotName = getSlotName();
    String presentationPage = getPresentationPage();

    try {
      preferences.setValue(PORTLET_SLOT_NAME_PROPERTY, slotName);
      preferences.setValue(PORTLET_PRESENTATION_PAGE_PROPERTY, presentationPage);
    }
    catch(ReadOnlyException e) {
      if (isLoggingError())
        logError(ResourceUtils.getMsgResource(ERROR_SETTING_PORTLET_SETTINGS,
                                              getResourceBundleName(), 
                                              getResourceBundle()) + ": " + e);
      // ACW: FIXME: should set form error
      throw new PortletException(e);
    }

    try {
      preferences.store();

      if (isLoggingDebug()) {
        logDebug(ResourceUtils.getMsgResource(DEBUG_SAVED_PORTLET_SETTINGS,
                                              getResourceBundleName(), 
                                              getResourceBundle()) + ": ");
        logDebug("  " + PORTLET_SLOT_NAME_PROPERTY + ": " + slotName);
        logDebug("  " + PORTLET_PRESENTATION_PAGE_PROPERTY + ": " + presentationPage);
      }
    } 
    catch (ValidatorException e) {
      if (isLoggingError())
        logError(ResourceUtils.getMsgResource(ERROR_SAVING_PORTLET_SETTINGS,
                                              getResourceBundleName(), 
                                              getResourceBundle()) + ": " + e);
      // ACW: FIXME: should set form error
      throw new PortletException(e);
    }

    return true;
  }

  /**
   * Handle the slot cancel
   * @param pRequest the portlet request
   * @param pResponse the portlet response
   * @exception PortletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleCancel(PortletRequest pRequest,
                              PortletResponse pResponse)
    throws PortletException, java.io.IOException
  {
    return true;
  }

} // end of class
