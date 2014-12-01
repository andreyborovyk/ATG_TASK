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

package atg.commerce.payment;

import atg.payment.tax.*;
import atg.nucleus.GenericService;

/**
 * Placeholder tax processor for testing tax processing APIs.  Always returns 
 * no tax.
 *
 * @author Graham Mather
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/DummyTaxProcessor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class DummyTaxProcessor extends GenericService implements TaxProcessor
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/DummyTaxProcessor.java#2 $$Change: 651448 $";

  //-------------------------------------
  /**
   * TaxProcessor implementation
   */
  public TaxStatus calculateTax(TaxRequestInfo pTaxInfo) {

    double taxableAmount = 0.0;
      
      if (isLoggingDebug()) {    
          for (int i = 0; i < pTaxInfo.getShippingDestinations().length; i++) {
              ShippingDestination dest = pTaxInfo.getShippingDestinations()[i];
              logDebug("DUMMY TAX PROCESSOR taxing a GROUP with amount : " + dest.getTaxableItemAmount());
	      
	      taxableAmount += dest.getTaxableItemAmount();
	  }
      }
      
      if (isLoggingDebug()) {
	logDebug("taxable amount: " + taxableAmount);
      }


      DummyTaxStatus ret = new DummyTaxStatus();

      ret.setTransactionSuccess(true);
 
      return ret;
  }
    
  /**
   * Calculate tax on the information specified in TaxRequestInfo.  Unlike calculateTax,
   * however, this method returns tax information on a per-shipping group basis.  That is
   * to say, it returns tax information for items grouped around their shipping address.
   * This method is unsupported in the DummyTaxProcessor and returns null.
   *
   * @param pTaxInfo the TaxInfo reference which contains all the tax calculation data
   * @return an array of TaxStatus objects detailing the results of the tax calculation
   */
    public TaxStatus [] calculateTaxByShipping(TaxRequestInfo pTaxInfo) {
      
      DummyTaxStatus[] ret = new DummyTaxStatus[pTaxInfo.getShippingDestinations().length];
      
      for (int i = 0; i < pTaxInfo.getShippingDestinations().length; i++) {
        if (isLoggingDebug()) {    
          ShippingDestination dest = pTaxInfo.getShippingDestinations()[i];
          logDebug("DUMMY TAX PROCESSOR taxing a GROUP with amount : " + dest.getTaxableItemAmount());
        }
        ret[i] = new DummyTaxStatus();
        ret[i].setTransactionSuccess(true);
      }
      
      return ret;
    }
    
} // end of class
