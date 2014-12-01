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

import javax.servlet.*;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * Associates required param "promotion" with optional param "profile".
 * If "profile" is not supplied or is not an instance of Profile, it is
 * resolved from Nucleus and this is the profile with which the promotion
 * is associated.
 *
 * @author Joshua Spiewak
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/promotion/PromotionDroplet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class PromotionDroplet extends DynamoServlet
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/promotion/PromotionDroplet.java#2 $$Change: 651448 $";

  //--------------------------------------
  // Constants
  static final ParameterName PROMOTION = ParameterName.getParameterName("promotion");
  static final ParameterName PROFILE = ParameterName.getParameterName("profile");
  static final String ERROR = "error";

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

  public String getProfile() {
    if (mProfileComponentName != null) {
      return mProfileComponentName.getName();
    }
    else {
      return null;
    }
  }

  //---------------------------------------------------------------------------
  // property: promotionTools
  PromotionTools mPromotionTools;

  public void setPromotionTools(PromotionTools pPromotionTools) {
    mPromotionTools = pPromotionTools;
  }

  public PromotionTools getPromotionTools() {
    return mPromotionTools;
  }

  //---------------------------------------------------------------------------
  // property: promotionRepository
  Repository mPromotionRepository;

  public void setPromotionRepository(Repository pPromotionRepository) {
    mPromotionRepository = pPromotionRepository;
  }

  public Repository getPromotionRepository() {
    return mPromotionRepository;
  }

  //---------------------------------------------------------------------------
  // property: promotionItemDescriptorName
  String mPromotionItemDescriptorName = "Promotion";

  public void setPromotionItemDescriptorName(String pPromotionItemDescriptorName) {
    mPromotionItemDescriptorName = pPromotionItemDescriptorName;
    mPromotionItemDescriptor = null;
  }

  public String getPromotionItemDescriptorName() {
    return mPromotionItemDescriptorName;
  }

  protected RepositoryItemDescriptor mPromotionItemDescriptor = null;

  //-------------------------------------
  /**
   * 
   */
  public void service(DynamoHttpServletRequest pRequest,
                      DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    Object promotionObject = pRequest.getObjectParameter(PROMOTION);
    boolean error = false;

    if (promotionObject == null) {
      String msg = PromotionConstants.getStringResource(PromotionConstants.NO_PROMOTION_PARAMETER);
      if (isLoggingError())
        logError(msg);
      pRequest.setParameter(ERROR, msg);      
      error = true;
    }
    else if (!(promotionObject instanceof RepositoryItem)) {
      String msg = PromotionConstants.getStringResource(PromotionConstants.PROMOTION_NOT_REPOSITORY_ITEM);
      if (isLoggingError())
        logError(msg);
      pRequest.setParameter(ERROR, msg);
      error = true;
    }
    else {
      RepositoryItem promotion = (RepositoryItem) promotionObject;

      synchronized (mPromotionItemDescriptorName) {
        if (mPromotionItemDescriptor == null) {
          try {
            mPromotionItemDescriptor = mPromotionRepository.getItemDescriptor(mPromotionItemDescriptorName);
          }
          catch (RepositoryException e) {
            if (isLoggingError())
              logError(e);
            pRequest.setParameter(ERROR, e);
            error = true;
          }
        }
      }

      if(!error) {
        RepositoryItemDescriptor promotionItemDescriptor = null;
        
        try {
          promotionItemDescriptor = promotion.getItemDescriptor();
        }
        catch (RepositoryException e) {
          if(isLoggingError())
            logError(e);
          pRequest.setParameter(ERROR, e);
          error = true;
        }
        
        if (promotionItemDescriptor == null) {
          logError(PromotionConstants.getStringResource(PromotionConstants.CANNOT_GET_PROMOTION_ITEM_DESCRIPTOR));
        }
        //       else if (!promotionItemDescriptor.isInstance(mPromotionItemDescriptor)) {
        if (!mPromotionItemDescriptor.isInstance(promotion)) {
          String msg = MessageFormat.format(PromotionConstants.getStringResource(PromotionConstants.WRONG_PROMOTION_ITEM_DESCRIPTOR), mPromotionItemDescriptor.getItemDescriptorName(), 
              promotionItemDescriptor.getItemDescriptorName());
          if (isLoggingError()) {
            logError(msg);
          }
          pRequest.setParameter(ERROR, msg);
          error = true;
        }
        else {
          Object profileObject = pRequest.getObjectParameter(PROFILE);
          MutableRepositoryItem profile = null;
          
          if (profileObject == null) {
            profile = (MutableRepositoryItem) pRequest.resolveName(mProfileComponentName);
          }
          else if (!(profileObject instanceof MutableRepositoryItem)) {
            if (isLoggingWarning())
              logWarning(PromotionConstants.getStringResource(PromotionConstants.USING_NUCLEUS_PROFILE));
            
            profile = (MutableRepositoryItem) pRequest.resolveName(mProfileComponentName);
          }
          else {
            profile = (MutableRepositoryItem) profileObject;
          }
          
          mPromotionTools.addPromotion(profile, promotion);
          mPromotionTools.initializePricingModels(pRequest, pResponse);
        }
      }
    }
    if(error) {
      pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
    }
  }
  
  //-------------------------------------
  /**
   * 
   */

} // end of class
