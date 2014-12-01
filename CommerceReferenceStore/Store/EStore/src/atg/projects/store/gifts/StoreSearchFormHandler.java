/*<ATGCOPYRIGHT>
 * Copyright (C) 2010-2011 Art Technology Group, Inc.
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

package atg.projects.store.gifts;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletException;

import atg.commerce.gifts.SearchFormHandler;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;

/**
 * Estore version of DCS' GiftlistSearch form handler.
 * Resets form errors when necessary. 
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/gifts/StoreSearchFormHandler.java#2 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class StoreSearchFormHandler extends SearchFormHandler
{
  /**
   * Class version
   */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/gifts/StoreSearchFormHandler.java#2 $$Change: 651448 $";
  
  private static final String FIRST_NAME_KEY = "firstName";
  private static final String LAST_NAME_KEY = "lastName";
  
  /**
   * Resource bundle name.
   */
  private static final String MY_RESOURCE_NAME = "atg.commerce.gifts.UserMessages";

  /**
   * Invalid e-mail format message key.
   */
  protected static String MSG_EMPTY_FIRST_LAST_NAME = "emptyFirstLastName";

  private boolean mResetFormErrors;

  public boolean isResetFormErrors()
  {
    return mResetFormErrors;
  }

  /**
   * Change <i>resetFormErrors</i> property to reset all form errors.
   * @param pResetFormErrors - any value.
   */
  public void setResetFormErrors(boolean pResetFormErrors)
  {
    mResetFormErrors = pResetFormErrors;
  }
  
  /**
   * This handler is triggered after a <i>resetFormErrors</i> property had been set.
   * Removes all form errors.
   * @param pRequest - current HTTP request.
   * @param pResponse - current HTTP response.
   * @return always <code>true</code>
   * @throws ServletException if something goes wrong.
   * @throws IOException if something goes wrong.
   */
  public boolean handleResetFormErrors(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException
  {
    resetFormExceptions();
    return true;
  }
  
  /**
   * Validates entered first and last name.
   */
  public void preSearch(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    if (!validateSearchInput()){
      addFormException(new DropletException(formatUserMessage(MSG_EMPTY_FIRST_LAST_NAME, pRequest, pResponse), MSG_EMPTY_FIRST_LAST_NAME));
      return;
    }
    
    super.preSearch(pRequest, pResponse);
    
    
  }
  
  /**
   * Ensures that at least one of the first name or last name fields is not empty.
   * @return true if at least one of the name's fields is not empty, false otherwise
   */
  public boolean validateSearchInput(){
    String firstName = (String) getPropertyValues().get(FIRST_NAME_KEY);
    String lastName = (String) getPropertyValues().get(LAST_NAME_KEY);
    // if both first name and last name are empty return false
    if (StringUtils.isEmpty(firstName) && StringUtils.isEmpty(lastName)){
      return false;
    }
    return true;
  }
  
  /**
   * Retrieves a message from default resource bundle. Resource bundle is defined with {@link #MY_RESOURCE_NAME} field.
   * @param pResourceKey - key to be searched within a resource bundle.
   * @param pRequest - current HTTP request.
   * @param pResponse - current HTTP response
   * @return string obtained from the resource bundle.
   */
  protected String formatUserMessage(String pResourceKey, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
    RequestLocale requestLocale = pRequest.getRequestLocale();
    Locale currentLocale = requestLocale == null ? Locale.getDefault() : requestLocale.getLocale();
    ResourceBundle bundle = LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, currentLocale);
    return bundle.getString(pResourceKey);
  }
}
