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

package atg.commerce.profile;

import atg.userprofiling.ProfileSessionFailService;

import atg.repository.RepositoryException;
import java.util.List;

/**
 * This service extends the @see atg.userprofiling.ProfileSessionFailService to send along the
 * activePromotions property of the profile. Note: This implementation will not send over the
 * usedPromotions property.
 *
 * @author Tareef Kawaf
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/profile/CommerceProfileFailService.java#2 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $*/
public class CommerceProfileFailService extends ProfileSessionFailService
{
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/profile/CommerceProfileFailService.java#2 $$Change: 651448 $";

  protected String mActivePromotionPropertyName;

  public void setActivePromotionPropertyName(String pActivePromotionPropertyName) {
    mActivePromotionPropertyName = pActivePromotionPropertyName;
  }

  /**
   * The profile's property name for active promotions.  If the profile's property is changed
   * in the xml file this property should be set to the new value.
   **/
  public String getActivePromotionPropertyName() {
    return mActivePromotionPropertyName;
  }

  /** The Profile's active promotions */
  protected List mActivePromotions = null;

  public void setActivePromotions(List pActivePromotions) {
    if (isLoggingDebug()) {
      if (pActivePromotions != null)
        logDebug("Active promotions are being set with size: " + pActivePromotions.size());
      else
        logDebug("Active promotions are being reset");
    }
    mActivePromotions = pActivePromotions;
  }

  public List getActivePromotions() {
    try {
      if (getProfile() != null) 
      {
        if (getProfile().isTransient()) 
        {
          if (getProfile().getItemDescriptor() != null && getProfile().getItemDescriptor().hasProperty(getActivePromotionPropertyName())) 
          {
            setActivePromotions((List) getProfile().getPropertyValue(getActivePromotionPropertyName()));
      
            if (isLoggingDebug()) {
              logDebug("The active promotions list is being retrieved, size=" +
                   mActivePromotions.size());
            }

            return mActivePromotions;
          }
        }
      }
    }
    catch(RepositoryException re) {
      if (isLoggingError())
        logError(re);
    }

    return null;
  }

  /**
   * The restoring method which will load the activePromotions into the profile.  This method
   * calls the ProfileFailService's sessionRestored method and then restores the
   * activePromotions from the member variable if the profile is transient and there are any
   * active promotions.
   **/
  public void sessionRestored() {
    super.sessionRestored();
    if ((getProfile().isTransient()) && (mActivePromotions != null)) {
      if (isLoggingDebug())
        logDebug("Setting the active promotions list");
      getProfile().setPropertyValue(getActivePromotionPropertyName(), mActivePromotions);
    }
    setActivePromotions(null);
  }
  
    
} // end of class
