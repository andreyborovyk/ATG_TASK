/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2005 Art Technology Group, Inc.
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

package atg.projects.store.profile;

import atg.userprofiling.Profile;

/**
 * Store extension of {@link Profile} component implementation.
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/profile/StoreProfile.java#3 $$Change: 635816 $ 
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class StoreProfile extends Profile {
  /**
   * Class version
   */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/profile/StoreProfile.java#3 $$Change: 635816 $";

  /**
   * This method clears all data stored in the following <code>Profile</code>'s properties:
   * <li><code>secondaryAddresses</code>
   * <li><code>shippingAddress</code>
   * <li><code>billingAddress</code>
   */
  public void clearPersonalData() {
    // Remove all secondary addresses
    String propertyName = ((StorePropertyManager) getProfileTools().getPropertyManager()).getSecondaryAddressPropertyName();
    setPropertyValue(propertyName, null);
    // Remove shipping address
    propertyName = ((StorePropertyManager) getProfileTools().getPropertyManager()).getShippingAddressPropertyName();
    setPropertyValue(propertyName, null);
    // Remove billing address
    propertyName = ((StorePropertyManager) getProfileTools().getPropertyManager()).getBillingAddressPropertyName();
    setPropertyValue(propertyName, null);
  }
}
