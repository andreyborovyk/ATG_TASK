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

package atg.projects.store.order.purchase;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.order.purchase.CancelOrderFormHandler;
import atg.projects.store.profile.StoreProfile;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * 
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/purchase/StoreCancelOrderFormHandler.java#3 $$Change: 635816 $ 
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class StoreCancelOrderFormHandler extends CancelOrderFormHandler {
  /**
   * Class version
   */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/purchase/StoreCancelOrderFormHandler.java#3 $$Change: 635816 $";

  @Override
  public void postCancelOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    // Remove personal data from transient profile (for security reasons)
    StoreProfile profile = (StoreProfile) getProfile();
    if (profile.isTransient()) {
      profile.clearPersonalData();
    }
    
    super.postCancelOrder(pRequest, pResponse);
  }
}
