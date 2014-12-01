/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2011 Art Technology Group, Inc.
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
package atg.projects.store.inventory;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletException;

import atg.core.util.ResourceUtils;
import atg.droplet.DropletFormException;
import atg.droplet.GenericFormHandler;
import atg.multisite.SiteContextManager;
import atg.projects.store.email.StoreEmailTools;
import atg.projects.store.profile.StorePropertyManager;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.service.localeservice.LocaleService;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;


/**
 * This form handler will take requests from users to be notified when an item
 * is back in stock.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/inventory/BackInStockFormHandler.java#2 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class BackInStockFormHandler extends GenericFormHandler {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/inventory/BackInStockFormHandler.java#2 $$Change: 651448 $";

  /**
   * Resource bundle name.
   */
  private static final String MY_RESOURCE_NAME = "atg.projects.store.inventory.UserMessage";

  /**
   * Resource bundle.
   */
  private static ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME,
      atg.service.dynamo.LangLicense.getLicensedDefault());

  /**
   * Invalid e-mail format message key.
   */
  protected static String MSG_INVALIDATE_EMAIL_FORMAT = "invalidateEmailFormat";

  /**
   * Catalog reference id.
   */
  private String mCatalogRefId;

  /**
   * E-mail address.
   */
  private String mEmailAddress;

  /**
   * Product id.
   */
  private String mProductId;

  /**
   * Profile repository.
   */
  private MutableRepository mProfileRepository;

  /**
   * Property manager.
   */
  private StorePropertyManager mPropertyManager;

  /**
   * Success redirect URL.
   */
  private String mSuccessURL;

  /**
   * Error redirect URL.
   */
  private String mErrorURL;

  private LocaleService mLocaleService;

  public LocaleService getLocaleService()
  {
    return mLocaleService;
  }

  public void setLocaleService(LocaleService pLocaleService)
  {
    mLocaleService = pLocaleService;
  }

  /**
   * @return the catalog reference id.
   */
  public String getCatalogRefId() {
    return mCatalogRefId;
  }

  /**
   * @param pCatalogRefId - the catalog reference id.
   */
  public void setCatalogRefId(String pCatalogRefId) {
    mCatalogRefId = pCatalogRefId;
  }

  /**
   * @return the e-mail address.
   */
  public String getEmailAddress() {
    return mEmailAddress;
  }

  /**
   * @param pEmailAddress - the e-mail address to set.
   */
  public void setEmailAddress(String pEmailAddress) {
    mEmailAddress = pEmailAddress;
  }

  /**
   * @return the product id.
   */
  public String getProductId() {
    return mProductId;
  }

  /**
   * @param pProductId - the product id to set.
   */
  public void setProductId(String pProductId) {
    mProductId = pProductId;
  }

  /**
   * @return the profile repository.
   */
  public MutableRepository getProfileRepository() {
    return mProfileRepository;
  }

  /**
   * @param pProfileRepository - the profile repository to set.
   */
  public void setProfileRepository(MutableRepository pProfileRepository) {
    mProfileRepository = pProfileRepository;
  }

  /**
   * @return the success redirect URL.
   */
  public String getSuccessURL() {
    return mSuccessURL;
  }

  /**
   * @param pSuccessURL - the success redirect URL to set.
   */
  public void setSuccessURL(String pSuccessURL) {
    mSuccessURL = pSuccessURL;
  }

  /**
   * @return the error redirect URL.
   */
  public String getErrorURL() {
    return mErrorURL;
  }

  /**
   * @param pErrorURL - the error redirect URL to set.
   */
  public void setErrorURL(String pErrorURL) {
    mErrorURL = pErrorURL;
  }

  //--------------------------------------------------
  // property: EmailTools
  private StoreEmailTools mEmailTools;

  /**
   * @return the StoreEmailTools
   */
  public StoreEmailTools getEmailTools() {
    return mEmailTools;
  }

  /**
   * @param EmailTools the StoreEmailTools to set
   */
  public void setEmailTools(StoreEmailTools pEmailTools) {
    mEmailTools = pEmailTools;
  }

  //--------------------------------------------------
  // property: InventoryManager
  private StoreInventoryManager mInventoryManager;

  /**
   * @return the StoreInventoryManager
   */
  public StoreInventoryManager getInventoryManager() {
    return mInventoryManager;
  }

  /**
   * @param InventoryManager the StoreInventoryManager to set
   */
  public void setInventoryManager(StoreInventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }

  //----------------------------------------------------
  // property: NoJavascriptSuccessURL
  /**
   * Success url set from a javascript free form
   */
  public String mNoJavascriptSuccessURL;

  /**
   * @return mNoJavascriptSuccessURL
   */
  public String getNoJavascriptSuccessURL(){
    return mNoJavascriptSuccessURL;
  }

  /**
   * @param pNoJavascriptSuccessURL
   */
  public void setNoJavascriptSuccessURL(String pNoJavascriptSuccessURL){
    mNoJavascriptSuccessURL = pNoJavascriptSuccessURL;
  }

  //----------------------------------------------------
  // property: NoJavascriptErrorURL
  /**
   * Error url set from a javascript free form
   */
  public String mNoJavascriptErrorURL;

  /**
   * @return mNoJavascriptErrorURL
   */
  public String getNoJavascriptErrorURL(){
    return mNoJavascriptErrorURL;
  }

  /**
   * @param pNoJavascriptErrorURL
   */
  public void setNoJavascriptErrorURL(String pNoJavascriptErrorURL){
    mNoJavascriptErrorURL = pNoJavascriptErrorURL;
  }

  /**
   * This method returns ResourceBundle object for specified locale.
   *
   * @param pLocale The locale used to retrieve the resource bundle. If <code>null</code>
   *                then the default resource bundle is returned.
   *
   * @return the resource bundle.
   */
  public ResourceBundle getResourceBundle(Locale pLocale) {
    if (pLocale == null) {
      return sResourceBundle;
    }

    ResourceBundle rb = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, pLocale);

    return rb;
  }

  /**
   * This method will handle "notify when back in stock" requests.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @throws ServletException if there was an error while executing the code
   * @throws IOException if there was an error with servlet io
   * @return true if success, false - otherwise
   */
  public boolean handleNotifyMe(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    if ((getCatalogRefId() == null) || (getCatalogRefId().length() < 1)) {
      if (isLoggingDebug()) {
        logDebug("catalogRefId is null. backInStockNotifyItem was not created.");
      }

      // When javascript is off if the skuId is not
      // set dont display the success message
      if(getNoJavascriptErrorURL() != null)
      {
        return checkFormRedirect(null, getNoJavascriptErrorURL(), pRequest, pResponse);
      }
      // Set in jsps when javascript is enabled
      return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
    }

    if ((getProductId() == null) || (getProductId().length() < 1)) {
      if (isLoggingDebug()) {
        logDebug("productId is null. backInStockNotifyItem was not created.");
      }

      // When javascript is off if the productId is
      /// not set dont display the success message
      if(getNoJavascriptErrorURL() != null)
      {
        return checkFormRedirect(null, getNoJavascriptErrorURL(), pRequest, pResponse);
      }
      return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
    }

    if (!getEmailTools().validateEmailAddress(getEmailAddress())) {
      String msg = ResourceUtils.getMsgResource(MSG_INVALIDATE_EMAIL_FORMAT, MY_RESOURCE_NAME, getResourceBundle(ServletUtil.getUserLocale()));
      addFormException(new DropletFormException(msg, null));

      // When javascript is off if the email address is not valid display
      // the same page with a warning message
      if(getNoJavascriptErrorURL() != null){
        return checkFormRedirect(null, getNoJavascriptErrorURL(), pRequest, pResponse);
      }
      return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);
    }

    try {
      boolean alreadyExists =
        getInventoryManager().isBackInStockItemExists(getProfileRepository(), getCatalogRefId(),
                                                  getEmailAddress(), getProductId());

      if (alreadyExists) {
        if (isLoggingDebug()) {
          logDebug("backInStockNotifyItem already exists for this combination of catalogRefId, email and productId.");
        }

        // When javascript is off if the notification
        // already exists display the success message
        if(getNoJavascriptSuccessURL() != null){
          return checkFormRedirect(getNoJavascriptSuccessURL(), null, pRequest, pResponse);
        }
        return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);
      }

      getInventoryManager().createBackInStockNotifyItem(getProfileRepository(), getCatalogRefId(),
                          getEmailAddress(), getProductId(), getLocaleService().getLocale().toString(), SiteContextManager.getCurrentSiteId());
    } catch (RepositoryException ex) {
      throw new ServletException(ex);
    }

    // Notification created successfully
    if((getNoJavascriptSuccessURL() != null) || (getNoJavascriptErrorURL() != null)){
      return checkFormRedirect(getNoJavascriptSuccessURL(), getNoJavascriptErrorURL(),
                               pRequest, pResponse);
    }
    return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
  }
}
