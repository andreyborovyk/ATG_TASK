/*<ATGCOPYRIGHT>
 * Copyright (C) 2000-2011 Art Technology Group, Inc.
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

package atg.commerce.promotion;

import atg.nucleus.naming.*;
import atg.repository.*;
import atg.servlet.*;
import atg.servlet.pipeline.InsertableServletImpl;

import java.io.IOException;
import javax.servlet.ServletException;
import java.text.*;

/**
 * The PromotionServlet allows a promotion to be added to a users profile
 * via a URL.  It must be in the servlet pipeline for this to work.  If it 
 * is in the servlet pipeline and <code>enabled</code> is <code>true</code>
 * then the following example demonstrates how to use PromotionServlet:
 * <p>
 * <pre>
 * &lt;a href="../../whatever.jhtml" encode="true"&gt; 
 *   &lt;param name="PROMO" value="promo10102"&gt; 
 *   Click here to get 20% discount on shirts... 
 * &lt;/a&gt; 
 * </pre>
 * <p>
 * Note: 
 * First, <code>encode=true</code> is required otherwise the parameter is added as
 * a query parameter and not a url parameter and it won't work 
 * Second, value for the param tag is the promotion id in the
 * /atg/commerce/pricing/Promotions repository for item descriptor(s) given 
 * in property promotionItemDescriptorNames. 
 *
 * @beaninfo description: This servlet will add a promotion to a profile, via a URL
 *
 * @author Joshua Spiewak
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/promotion/PromotionServlet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class PromotionServlet extends InsertableServletImpl
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/promotion/PromotionServlet.java#2 $$Change: 651448 $";

  //---------------------------------------------------------------------------
  // property: profile
  ComponentName mProfileComponentName = ComponentName.getComponentName("/atg/userprofiling/Profile");

  public void setProfile(String pProfile) {
    if (pProfile != null) {
      mProfileComponentName = ComponentName.getComponentName(pProfile);
    }
    else {
      mProfileComponentName = null;
    }
  }

  /**
   * The profile that the promotion will be added to.  By default this is
   * the profile located at /atg/userprofiling/Profile
   *
   * @beaninfo description: The profile that the promotion will be added to
   **/
  public String getProfile() {
    if (mProfileComponentName != null) {
      return mProfileComponentName.getName();
    }
    else {
      return null;
    }
  }

  //---------------------------------------------------------------------------
  // property: promotionParameter
  protected String mPromotionParameter = "PROMO";

  public void setPromotionParameter(String pPromotionParameter) {
    mPromotionParameter = pPromotionParameter;
  }

  /**
   * This is the name of the request parameter than contains the promotion.
   * By default this is <code>PROMO</code>
   *
   * @beaninfo description: This is the name of the request parameter than contains the promotion.
   **/
  public String getPromotionParameter() {
    return mPromotionParameter;
  }

  //---------------------------------------------------------------------------
  // property: promotionRepository
  Repository mPromotionRepository;

  public void setPromotionRepository(Repository pPromotionRepository) {
    mPromotionRepository = pPromotionRepository;
  }

  /**
   * This is the repository that contains the promotions
   *
   * @beaninfo description: This is the repository that contains the promotions
   **/
  public Repository getPromotionRepository() {
    return mPromotionRepository;
  }

  //---------------------------------------------------------------------------
  // property: promotionItemDescriptorNames
  String [] mPromotionItemDescriptorNames;

  public void setPromotionItemDescriptorNames(String [] pPromotionItemDescriptorNames) {
    mPromotionItemDescriptorNames = pPromotionItemDescriptorNames;
  }

  /**
   * These are the names of the valid promotion item descriptors
   *
   * @beaninfo description: These are the names of the valid promotion item descriptors
   **/
  public String [] getPromotionItemDescriptorNames() {
    return mPromotionItemDescriptorNames;
  }

  //---------------------------------------------------------------------------
  // property: promotionTools
  PromotionTools mPromotionTools;

  public void setPromotionTools(PromotionTools pPromotionTools) {
    mPromotionTools = pPromotionTools;
  }

  /**
   * This is a helper class for manipulating promotions
   *
   * @beaninfo description: This is a helper class for manipulating promotions
   **/
  public PromotionTools getPromotionTools() {
    return mPromotionTools;
  }


  //-------------------------------------
  // property: Enabled
  boolean mEnabled = false;

  public void setEnabled(boolean pEnabled) {
    mEnabled = pEnabled;
  }

  /**
   * If <code>enabled</code> is true, then this servlet is "active" in that
   * it will look for a PROMO parameter to add to the current profile.
   *
   * @beaninfo description: If "enabled" is true, then this servlet 
   *                        is active in that it will look for a PROMO 
   *                        parameter to add to the current profile.
   **/
  public boolean isEnabled() {
    return mEnabled;
  }
  

  //-------------------------------------
  /**
   * The service method that looks for the PROMO parameter and adds it to
   * the profile, before pass the request to the next servlet in the pipeline.
   */
  public void service(DynamoHttpServletRequest pRequest,
                      DynamoHttpServletResponse pResponse)
       throws IOException, ServletException 
  {
    if (!isEnabled()) {
      passRequest(pRequest, pResponse);
      return;
    }

    // Look for promotion in query parameters
    String promotionId = pRequest.getURLParameter(mPromotionParameter);
    
    if (promotionId != null) {
      if (isLoggingDebug())
        logDebug("Got a promotion param: " + promotionId);

      RepositoryItem promotion = null;

      // Need to go through the list of item descriptors since the promotion 
      // could be of any type
      try {
        for (int i = 0; i < mPromotionItemDescriptorNames.length; i++) {
          promotion = mPromotionRepository.getItem(promotionId, mPromotionItemDescriptorNames[i]);

          if (promotion != null) {
            break;
          }
        }
      }
      catch (RepositoryException e) {
        if (isLoggingError())
          logError(MessageFormat.format(PromotionConstants.getStringResource(PromotionConstants.MSG_NO_PROMOTION_FOUND_WITH_ID),
          		promotionId), e);
      }

      // If there, resolve profile
      if (promotion != null) {
        if (isLoggingDebug())
          logDebug("Found promotion in repository");

        MutableRepositoryItem profile = (MutableRepositoryItem) pRequest.resolveName(mProfileComponentName);

        // If the promotion requires profile to be persistent, check profile
        // Actually doing it in reverse because it should be more efficient to
        // check the profile if it is transient
        // TBD use the PricingModelProperties service to get this! 
        if (!profile.isTransient() || ((Boolean) promotion.getPropertyValue("giveToAnonymousProfiles")).booleanValue()) {
          // Add promotion to profile
          mPromotionTools.addPromotion(profile, promotion);
          mPromotionTools.initializePricingModels(pRequest, pResponse);
        }
        else {
          if (isLoggingDebug())
            logDebug("Did not give the promotion to the user because they were anonymous and the promotion cannot be given to anonymous users");
        }
      }
      else {
        if (isLoggingError())
          logError(MessageFormat.format(PromotionConstants.getStringResource(PromotionConstants.MSG_NO_PROMOTION_FOUND_WITH_ID),
          		promotionId));
      }
    }

    passRequest(pRequest, pResponse);
  }
  
} // end of class
