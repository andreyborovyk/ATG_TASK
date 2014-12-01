/*<ATGCOPYRIGHT>
 * Copyright (C) 2008-2011 Art Technology Group, Inc.
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
package atg.projects.store.multisite;

/**
 * Properties manager for SiteRepository.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/International/src/atg/projects/store/multisite/InternationalStoreSitePropertiesManager.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class InternationalStoreSitePropertiesManager extends
    StoreSitePropertiesManager {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/International/src/atg/projects/store/multisite/InternationalStoreSitePropertiesManager.java#2 $$Change: 651448 $";


  
  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // property: DefaultLanguagePropertyName
  //-------------------------------------
  protected String mDefaultLanguagePropertyName = "defaultLanguage";

  /**
   * @return the String
   */
  public String getDefaultLanguagePropertyName() {
    return mDefaultLanguagePropertyName;
  }

  /**
   * @param DefaultLanguagePropertyName the String to set
   */
  public void setDefaultLanguagePropertyName(String pDefaultLanguagePropertyName) {
    mDefaultLanguagePropertyName = pDefaultLanguagePropertyName;
  }
  
  //-------------------------------------
  // property: LanguagesPropertyName
  //-------------------------------------
  protected String mLanguagesPropertyName = "languages";

  /**
   * @return the String
   */
  public String getLanguagesPropertyName() {
    return mLanguagesPropertyName;
  }

  /**
   * @param LanguagesPropertyName the String to set
   */
  public void setLanguagesPropertyName(String pLanguagesPropertyName) {
    mLanguagesPropertyName = pLanguagesPropertyName;
  }
}
