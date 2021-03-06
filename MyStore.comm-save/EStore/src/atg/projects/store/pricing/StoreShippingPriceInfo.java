/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2010 Art Technology Group, Inc.
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
package atg.projects.store.pricing;

import atg.commerce.pricing.ShippingPriceInfo;
import atg.commerce.pricing.TaxPriceInfo;

/**
 * Extends the base class to include a taxPriceInfo property.
 * 
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/pricing/StoreShippingPriceInfo.java#3 $$Change: 635816 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */

public class StoreShippingPriceInfo extends ShippingPriceInfo {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/pricing/StoreShippingPriceInfo.java#3 $$Change: 635816 $";
  
  /**
   * Tax price information.
   */
  private TaxPriceInfo mTaxPriceInfo;

  /**
   * @return tax price information.
   */
  public TaxPriceInfo getTaxPriceInfo() {
    if (mTaxPriceInfo == null) {
      mTaxPriceInfo = new TaxPriceInfo();
    }

    return mTaxPriceInfo;
  }

  /**
   * @param pTaxPriceInfo - tax price information.
   */
  public void setTaxPriceInfo(TaxPriceInfo pTaxPriceInfo) {
    mTaxPriceInfo = pTaxPriceInfo;
  }
  
  /**
   * Overrides base method to include taxPriceInfo.
   */
  public String toString() {
    StringBuffer buf = new StringBuffer(super.toString());
    buf.append("; ").append("taxPriceInfo: ").append(getTaxPriceInfo());
    return buf.toString();    
  }

}
