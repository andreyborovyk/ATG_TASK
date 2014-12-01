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
package atg.projects.store.catalog;

import atg.projects.store.email.GenericEmailSenderFormHandler;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

/**
 * Form handler for sending email from the ATG Store website. <br/> The JSP form
 * that accepts the email can directly set the From, Subject and To fields or
 * use the defaults as named in the configuration of the DefaultEmailInfo. <br/>
 * When the form submits the parameters the template is used to format the email
 * and then the EmailSender sends the email. The names for the From, Subject, To
 * and Profile parameters that are sent to the Email Template are set in the
 * configuration and must match the email template. <br/>
 * 
 * @author ATG
 * @version $Id:
 *          //hosting-store/Store/main/estore/src/atg/projects/store/catalog/EmailAFriendFormHandler.java#15
 *          $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class EmailAFriendFormHandler extends GenericEmailSenderFormHandler {
  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/catalog/EmailAFriendFormHandler.java#2 $$Change: 651448 $";

  /**
   * Locale name.
   */
  private String mLocale = null;

  /**
   * Product Id.
   */
  private String mProductId = null;

  /**
   * Product id parameter name.
   */
  private String mProductIdParamName = null;

  /**
   * Subject parameter name.
   */
  private String mSubjectParamName = null;

  /**
   * Locale parameter name - it represents the name of locale parameter to be
   * used in Email template.
   */
  private String mLocaleParamName = "locale";

  
  
  /**
   * Gets the name of the parameter used for the ProductId: field. This is
   * configured in the component property file.
   * 
   * @return the name of the parameter used for the ProductId: field.
   */
  public String getProductIdParamName() {
    return mProductIdParamName;
  }

  /**
   * Sets the name of the parameter used for the ProductId: field. This is
   * configured in the component property file.
   * 
   * @param pProductIdParamName -
   *          the name of the parameter used for the ProductId: field.
   */
  public void setProductIdParamName(String pProductIdParamName) {
    mProductIdParamName = pProductIdParamName;
  }

  /**
   * Gets the name of the parameter used for the Subject: field. This is
   * configured in the component property file.
   * 
   * @return the name of the parameter used for the Subject: field.
   */
  public String getSubjectParamName() {
    return mSubjectParamName;
  }

  /**
   * Sets the name of the parameter used for the Subject: field. This is
   * configured in the component property file.
   * 
   * @param pSubjectParamName -
   *          the name of the parameter used for the Subject: field.
   */
  public void setSubjectParamName(String pSubjectParamName) {
    mSubjectParamName = pSubjectParamName;
  }

  /**
   * @param pLocaleParamName -
   *          locale parameter name.
   */
  public void setLocaleParamName(String pLocaleParamName) {
    mLocaleParamName = pLocaleParamName;
  }

  /**
   * @return the value of property getEmailParamName.
   */
  public String getLocaleParamName() {
    return mLocaleParamName;
  }

  /**
   * Gets the value of the Locale: field.
   * 
   * @return the value of the locale: field.
   */
  public String getLocale() {
    return mLocale;
  }

  /**
   * Sets the value of the locale: field.
   * 
   * @param pLocale -
   *          the value of the locale: field.
   */
  public void setLocale(String pLocale) {
    mLocale = pLocale;
  }

   
  /**
   * Gets the value of the ProductId: field.
   * 
   * @return the value of the ProductId: field.
   */
  public String getProductId() {
    return mProductId;
  }

  /**
   * Sets the value of the ProductId: field.
   * 
   * @param pProductId -
   *          the value of the ProductId: field.
   */
  public void setProductId(String pProductId) {
    mProductId = pProductId;
  }

 


  // property: StoreIdParamName -----------
  private String mStoreIdParamName="storeId";

  /**
  * The storeId parameter name - it represents the Store from which the email is dispatched
  * parameter to be used in Email template
  * @param pStoreIdParamName storeId parameter name
  */
  public void setStoreIdParamName(String pStoreIdParamName)
  {
    mStoreIdParamName = pStoreIdParamName;
  }

  /**
  * @return the value of property StoreIdParamName
  */
  public String getStoreIdParamName()
  {
    return mStoreIdParamName;
  }

  //-------------------------------------

  //property: StoreId -----------
  private String mStoreId=null;

  /**
   * Gets the value of the StoreId: field.
   * @return
   *  The value of the StoreId: field.
   */
  public String getStoreId() {
    return mStoreId;
  }

  /**
   * Sets the value of the StoreId: field.
   * @param pStoreId
   *  The value of the StoreId: field.
   */
  public void setStoreId(String pStoreId) {
    mStoreId = pStoreId;
  }
  
  protected Map collectParams() {
    // collect params from form handler to map and pass them into tools class
    Map emailParams = super.collectParams();
    // replace with new subject
    emailParams.put(getProductIdParamName(), getProductId());
    emailParams.put(getStoreIdParamName(), getStoreId());
    return emailParams;
  }

  // -------------------------------------
  protected void redirectOrForward(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse, String pURL) throws IOException,
      ServletException {
    pResponse.sendLocalRedirect(pURL, pRequest);
  }
  
  /**
   * @return the URL of the success page.
   */
  public String getSuccessURL() {
    return super.getSuccessURL() + "&recipientName="
                + getRecipientName().trim() + "&recipientEmail="
                + getRecipientEmail().trim();
  }

}
