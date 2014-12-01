/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2010 Art Technology Group, Inc.
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
package atg.projects.store;

import atg.nucleus.GenericService;

import java.util.List;

/**
 * This class is used for store wide configurations.
 *
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/StoreConfiguration.java#3 $$Change: 635816 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class StoreConfiguration extends GenericService {
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/StoreConfiguration.java#3 $$Change: 635816 $";

  /**
   * Is ATG search installed.
   */
  protected boolean mAtgSearchInstalled = false;

  protected boolean mUseSearchForSubcategoryProductList = false;

  /**
   * Is credit card verification required.
   */
  protected boolean mRequireCreditCardVerification = true;

  /**
   * Dojo URL.
   */
  protected String mDojoUrl;

  /**
   * Is Dojo debug turned on.
   */
  protected boolean mDojoDebug = false;

  /**
   * Mandatory state country list.
   */
  protected java.util.List mMandatoryStateCountryList;

  /**
   * @return true if ATG search is installed,
   * false - otherwise.
   */
  public boolean getAtgSearchInstalled() {
    return mAtgSearchInstalled;
  }

  /**
   * @param pAtgSearchInstalled - true if ATG search is installed,
   * false - otherwise.
   */
  public void setAtgSearchInstalled(boolean pAtgSearchInstalled) {
    mAtgSearchInstalled = pAtgSearchInstalled;
  }

  /**
   * @return true if search for subcategory product list
   * should be used, false - otherwise.
   */
  public boolean getUseSearchForSubcategoryProductList() {
    return mUseSearchForSubcategoryProductList;
  }

  /**
   * @param  pUseSearchForSubcategoryProductList - true if search for
   * subcategory product list should be used.
   */
  public void setUseSearchForSubcategoryProductList(boolean pUseSearchForSubcategoryProductList) {
    mUseSearchForSubcategoryProductList = pUseSearchForSubcategoryProductList;
  }

  /**
   * This property determines if credit card verification numbers are required by the application.
   * <p>
   * Many credit cards have a card
   * verification number printed, not embossed, on the card.   This number is never transferred
   * during card swipes and should be known only by the cardholder.  Each card association has
   * its own name for this number. Visa calls it the Card Verification Value (CVV2), and
   * MasterCard calls it the Card Validation Code (CVC2). Visa and MasterCard print the number
   * on the back of the card. American Express and Discover call it the Card
   * Identification Digits (CID).
   * @return Returns the requireCreditCardVerification.
   */
  public boolean isRequireCreditCardVerification() {
    return mRequireCreditCardVerification;
  }

  /**
   * @param pRequireCreditCardVerification - true if credit card
   * verification is required, false - otherwise.
   */
  public void setRequireCreditCardVerification(boolean pRequireCreditCardVerification) {
    mRequireCreditCardVerification = pRequireCreditCardVerification;
  }

  /**
   * @return the Dojo Url.
   */
  public String getDojoUrl() {
    return mDojoUrl;
  }

  /**
   * Sets the DojoUrl - this is the full url that will be used as the src= attribute to include
   * all dojo Javascript within the app.
   *
   * @param pDojoUrl - the DojoUrl to set.
   */
  public void setDojoUrl(String pDojoUrl) {
    mDojoUrl = pDojoUrl;
  }

  /**
   * Enable dojo debugging?
   * @return <code>true</code> to enable dojo debugging, <code>false</code> otherwise
   */
  public boolean isDojoDebug() {
    return mDojoDebug;
  }

  /**
   * Enables Dojo debug.
   *
   * @param pDojoDebug - the Dojo debug to set
   */
  public void setDojoDebug(boolean pDojoDebug) {
    mDojoDebug = pDojoDebug;
  }

  /**
   * @return the list of code of mandatory states.
   */
  public List getMandatoryStateCountryList() {
    return mMandatoryStateCountryList;
  }

  /**
   * @param pMandatoryStateCountryList - the List of code of mandatory states.
   */
  public void setMandatoryStateCountryList(List pMandatoryStateCountryList) {
    mMandatoryStateCountryList = pMandatoryStateCountryList;
  }

  //--------- Property: SiteHttpServerName -----------
  String mSiteHttpServerName;

  /**
   * Sets the name of the HTTP server.
   */
  public void setSiteHttpServerName(String pSiteHttpServerName) {
    mSiteHttpServerName = pSiteHttpServerName;
  }
  /**
   * @return The name of the HTTP server.
   */
  public String getSiteHttpServerName() {
    return mSiteHttpServerName;
  }

  //--------- Property: SiteHttpServerPort -----------
  int mSiteHttpServerPort;
  /**
   * Sets the port of the HTTP server.
   */
  public void setSiteHttpServerPort(int pSiteHttpServerPort) {
    mSiteHttpServerPort = pSiteHttpServerPort;
  }
  /**
   * @return The port of the HTTP server.
   */
  public int getSiteHttpServerPort() {
    return mSiteHttpServerPort;
  }

  //--------- Property: defaultResourceBundle -----------
  String mDefaultResourceBundle = "";
  /**
   * Sets the location of the default resource bundle.
   */
  public void setDefaultResourceBundle(String pDefaultResourceBundle) {
    mDefaultResourceBundle = pDefaultResourceBundle;
  }
  /**
   * @return The location of the default resource bundle.
   */
  public String getDefaultResourceBundle() {
    return mDefaultResourceBundle;
  }

  //--------- Property: defaultCssFile -----------
  String mDefaultCssFile = "";
  /**
   * Sets the location of the default css file.
   */
  public void setDefaultCssFile(String pDefaultCssFile) {
    mDefaultCssFile = pDefaultCssFile;
  }
  /**
   * @return The location of the default css file.
   */
  public String getDefaultCssFile() {
    return mDefaultCssFile;
  }
}
