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

package atg.projects.b2bstore.purchaselists;

import atg.commerce.gifts.*;
import atg.nucleus.naming.*;
import atg.servlet.*;
import java.io.*;
import javax.servlet.*;

/*
 * This class takes the place of the GiftitemDroplet so that the JHTML page parameter
 * nomenclature can be kept consistent with a purchase list (rather than gift list)
 * paradigm.
 *
 * @author <a href="mailto:jlang@atg.com">Jeremy Lang</a>, ATG Dynamo Innovations
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/purchaselists/PurchaselistItemDroplet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class PurchaselistItemDroplet extends GiftitemDroplet {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/purchaselists/PurchaselistItemDroplet.java#2 $$Change: 651448 $";


  //----------------------------------------------------------
  //Constants

  static final ParameterName PURCHASE_LIST_ID = ParameterName.getParameterName("purchaselistId");
  static final ParameterName PURCHASE_ITEM_ID = ParameterName.getParameterName("purchaseitemId");

  //parameters from GiftitemDroplet
  static final String GIFTLISTID = "giftlistId";
  static final String GIFTITEMID = "giftId";

  public void service(  DynamoHttpServletRequest pRequest,
                        DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    // get parameter values
    Object pPurchaselistId = pRequest.getObjectParameter(PURCHASE_LIST_ID);
    Object pPurchaseitemId = pRequest.getObjectParameter(PURCHASE_ITEM_ID);

    if (pPurchaselistId == null) {
      if (isLoggingError()) logError("no Purchaselist Id parameter");
      return;
    }

    if (pPurchaseitemId == null) {
      if (isLoggingError()) logError("no Giftitem Id parameter");
      return;
    }

    pRequest.setParameter(GIFTLISTID, pPurchaselistId);
    pRequest.setParameter(GIFTITEMID, pPurchaseitemId);

    super.service(pRequest, pResponse);
  }
}