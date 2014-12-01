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

package atg.commerce.profile;

import atg.dtm.*;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.*;

import java.io.IOException;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.transaction.*;

/**
 * This class is a subclass of the ProfileRequestServlet which performs any commerce specific
 * actions when a user is auto-logged in.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/profile/CommerceProfileRequestServlet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class CommerceProfileRequestServlet extends ProfileRequestServlet
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/profile/CommerceProfileRequestServlet.java#2 $$Change: 651448 $";

  //-------------------------------------
  public CommerceProfileRequestServlet() {
  }

  //-------------------------------------
  // property: loadOrdersOnAutoLogin
  //-------------------------------------
  private boolean mLoadOrdersOnAutoLogin = true;

  /**
   * Returns property loadOrdersOnAutoLogin
   *
   * @return returns property loadOrdersOnAutoLogin
   */
  public boolean getLoadOrdersOnAutoLogin() {
    return mLoadOrdersOnAutoLogin;
  }

  /**
   * Sets property loadOrdersOnAutoLogin
   *
   * @param pLoadOrdersOnAutoLogin the value to set for property loadOrdersOnAutoLogin
   */
  public void setLoadOrdersOnAutoLogin(boolean pLoadOrdersOnAutoLogin) {
    mLoadOrdersOnAutoLogin = pLoadOrdersOnAutoLogin;
  }

  /**
   * Load any shopping carts for the given user after they have gone through
   * auto-login.
   * @return true if the profile was initialized
   */
  protected boolean initProfileAfterAutoLogin(Profile pProfile,
                                              ProfileRequest pProfileRequest,
                                              DynamoHttpServletRequest pRequest,
                                              DynamoHttpServletResponse pResponse)
       throws IOException, ServletException
  { 
    CommerceProfileTools profileTools = (CommerceProfileTools) getProfileTools();
    boolean success=false;
    TransactionDemarcation td = new TransactionDemarcation();
    
    try {
      td.begin(profileTools.getOrderManager().getOrderTools().getTransactionManager(), TransactionDemarcation.REQUIRED);
      if (super.initProfileAfterAutoLogin(pProfile, pProfileRequest, pRequest, pResponse)) {
        if (getLoadOrdersOnAutoLogin()) {
          profileTools.loadUserShoppingCartForLogin(pProfile, pRequest, pResponse);
        }
        success=true;
      }
      else {
        success=false;
      }
      
    }
    catch(TransactionDemarcationException t) {
      if(isLoggingError())
        logError(t);
    }
    finally {
      try {
        td.end(); 
      }
      catch(TransactionDemarcationException tde) {
      	success=false;
        if(isLoggingError())
          logError(tde);
      }
    }
    return success;
  }
} // end of class
