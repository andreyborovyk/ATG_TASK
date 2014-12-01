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
b </ATGCOPYRIGHT>*/

package atg.integrations.taxware;

/**
 * <p> A class that provides simplified access for
 * getting the correct order item off of an order.
 *
 * @author cmore
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/VeraZipAccess.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class VeraZipAccess {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/VeraZipAccess.java#2 $$Change: 651448 $";


  /** getZipResultItemForAddrName gets the chosen ZipResultItem for
   * the address associated with pName (probably
   * VeraZipable.SHIP_TO_ADDRESS_NAME or VeraZipable.BILL_TO_ADDRESS_NAME).
   * and returns it, if it is available and in sync with the
   * City/State/Zip of the order.
   */
  public static ZipResultItem getZipResultItemForAddrName(VeraZipable zOrder,
                                                          String pName) {
    ZipResultItem zriResult = null;
    
    int indexAddr = zOrder.getIndexForAddressName(pName);

    if (indexAddr != -1) {
      ZipResult zresult = zOrder.getZipResultAt(indexAddr);
      
      int idxChoice = (zresult == null) ? -1 : zresult.getChosenIndex();
      
      if (-1 != idxChoice) {
        // use the selected choice, if there is one.
          zriResult = zresult.getResultItemAt(idxChoice);
      } else if (null != zresult && zresult.getErrorCode() == 0 &&
                 1 == zresult.getResultItemCount()) {
          // if there was no error and there is only
          // one choice, use that one.
          zriResult = zresult.getResultItemAt(0);
      }
    }
    
    if (zriResult != null) {
        if (VeraZipable.BILLING_ADDRESS_NAME.equals(pName)) {
            BillingShipping billOrder = (BillingShipping)zOrder;
            if (!zriResult.matchesCityStateZip(TrimData.trimCity(billOrder.getBillCity()),
                                               billOrder.getBillState(),
                                               TrimData.trimZip(billOrder.getBillZip()))) {
                // if city/state/zip don't match, then set the result to null
                //System.out.println(SalesTaxService.msg.format("VZIPMismatch", pName));
                zriResult = null;
            }
        } else if (VeraZipable.SHIP_TO_ADDRESS_NAME.equals(pName)) {
            BillingShipping shipOrder = (BillingShipping)zOrder;
            if (!zriResult.matchesCityStateZip(TrimData.trimCity(shipOrder.getShipToCity()),
                                               shipOrder.getShipToState(),
                                               TrimData.trimZip(shipOrder.getShipToZip()))) {
                // if city/state/zip don't match, then set the result to null
                //System.out.println(SalesTaxService.msg.format("VZIPMismatch", pName));
                zriResult = null;
            }
        }
    }
    return(zriResult);
  }
}
