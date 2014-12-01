/*<ATGCOPYRIGHT>
 * Copyright (C) 2005-2011 Art Technology Group, Inc.
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

package atg.web.assetmanager.editor.profile;

import java.io.IOException;
import java.util.Dictionary;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
*
* This is the formhandler for store user assets. 
*
* @author David Stewart
* @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/web/assetmanager/editor/profile/StoreUserFormHandler.java#2 $$Change: 651448 $
* @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
*/
public class StoreUserFormHandler extends UserFormHandler {

  //-------------------------------------
  // CONSTANTS
  //-------------------------------------

  public static String CLASS_VERSION = 
	"$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/web/assetmanager/editor/profile/StoreUserFormHandler.java#2 $$Change: 651448 $";

  
  //-------------------------------------
  // METHODS
  //-------------------------------------
  
  //--------------------------------------------------------------------
  /**
   * Converts the users login to lower case, if not already.
   */
  public void convertLoginToLowerCase() {
    // Get dictionary of profile values. 
    Dictionary valuesDict = getValue();
    // Get the user login.
    String login = (String) valuesDict.get("LOGIN");
    
    // Ensure user login exists.
    if (!StringUtils.isEmpty(login)) {
      if (isLoggingDebug()) {
        logDebug("Converting user login " + 
            login + " to lower case, if not already.");
      }
      // Change the user login value to lower case.
      valuesDict.put("LOGIN", login.toLowerCase());
    }
  }
  
  
  //-------------------------------------
  // OVERRIDES
  //-------------------------------------
	
  //--------------------------------------------------------------------- 
  /**
   * This method is called just before update item. If the user login has 
   * uppercase characters, they will be converted to lowercase.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing
   * the code
   * @exception IOException if there was an error with servlet io
   **/
  @Override
  protected void preUpdateItem(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    convertLoginToLowerCase();
    super.preUpdateItem(pRequest, pResponse);
  }
  
  //---------------------------------------------------------------------
  /**
   * This method is called just before the item adding process is
   * started. If the user login has uppercase characters, they 
   * will be converted to lowercase.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing
   * the code
   * @exception IOException if there was an error with servlet io
   **/
  @Override
  protected void preAddItem(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
  	throws ServletException, IOException
  {
    convertLoginToLowerCase();
    super.preAddItem(pRequest, pResponse);
  }
}
