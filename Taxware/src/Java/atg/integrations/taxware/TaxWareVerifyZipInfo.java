/*<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
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

package atg.integrations.taxware;

import atg.nucleus.*;
import java.util.Date;
import atg.payment.avs.AddressVerificationInfo;

/**
 * <p> This orderprocessing object verifies zipcode.
 * 
 *
 * @author michaelt (cmore)
 * @see VeraZipable
 * @see VeraZipOrderImpl
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/TaxWareVerifyZipInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class TaxWareVerifyZipInfo extends GenericService {

  //-------------------------------------
  // Class version string
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/TaxWareVerifyZipInfo.java#2 $$Change: 651448 $";

  /** Stop processing on tax calculation error?  Defaults to true. */
  protected boolean mStopProcessingOnError = true;

  /** Set whether processOrder will return false if an exception occurs */
  public void setStopProcessingOnError(boolean pStopProcessingOnError) {
    mStopProcessingOnError = pStopProcessingOnError;
  }

  /** Get whether processOrder will return false if an exception occurs. */
  public boolean getStopProcessingOnError() { return(mStopProcessingOnError); }

  protected String mErrorMessage;

  /**
   * @deprecated This method is not thread safe.  Inspect the AddressVerificationInfo for
   *              error messages
   **/
  public void setErrorMessage(String pErrorMessage) {
      mErrorMessage = pErrorMessage;
  }

  /**
   * @deprecated This method is not thread safe.  Inspect the AddressVerificationInfo for
   *              error messages
   **/
  public String getErrorMessage() {
      return mErrorMessage;
  }

  /** Constructor. */
  public TaxWareVerifyZipInfo() {
  }


  /** Sets the zip error string associated with the order,
   * and returns the status for processOrder. */
  protected boolean setErrorAndGetReturnValue(String strError, String address, 
                                              ZipRequest zrequest) 
    {   
        
    Object[] array = {address.toUpperCase(), zrequest.getCityName(),
                      zrequest.getStateCode(), zrequest.getZipCode(), strError};
    String logErrorString = SalesTaxService.msg.format("TXWRErrorVerifyZip", array);
    
    setErrorMessage(logErrorString);
    
    if (mStopProcessingOnError)
        return(false);
    
    return(true);
  }

  /** Modifies the request before submitting it to TaxWare. */
  protected void modifyRequest(AddressVerificationInfo pOrder,
                               ZipRequest request, boolean isBilling) {
      // do nothing -- can be overridden by subclasses
  }
    
  /** Returns whether or not we need to reverify shipTo info. */
  protected static boolean shouldReverify(ZipRequest zipRequest,
                                          ZipResult zipResult) {
    return(!zipResult.matchesRequest(zipRequest));
  }


  //--------------------------------------
  /** This method gets the shipping cost from the ShippingMethod class. */
  public boolean verifyZip(AddressVerificationInfo pOrder) {
    
    boolean bResult = true;
    
    VeraZipable zipOrder = (VeraZipable) pOrder;

    int cAddresses = zipOrder.getMaxCountOfZipAddresses();

    for (int i = 0; i < cAddresses; i++) {
        
      ZipResult zipresultOld = zipOrder.getZipResultAt(i);
      
      ZipRequest ziprequest = zipOrder.getZipRequestForAddress(i);
      if (ziprequest != null && isLoggingDebug())
          logDebug(ziprequest.toString());
        
      boolean bShouldVerify = true;

      // if getZipRequestForAddress returned NULL,
      // that means this address should not be checked
      if (null == ziprequest) {
        // okay, don't check this sucker
          bShouldVerify = false;
          // blow the old result away
          if (zipresultOld != null)
              zipOrder.setZipResultAt(i, null);
      } else if (null != zipresultOld) {
          bShouldVerify = shouldReverify(ziprequest, zipresultOld);
          
          if (!bShouldVerify) {
              // probably should add error again, 
              // if we haven't chosen a valid option
              if (zipresultOld.getErrorCode() != 0 && zipresultOld.getErrorCode() != 29 &&
                  zipresultOld.getChosenIndex() == -1) {
                  bResult = setErrorAndGetReturnValue(zipresultOld.getErrorMessage(),
                                                      zipOrder.getAddressNameForIndex(i),
                                                      ziprequest)
                      && bResult;
              }
          }
      }
      
      if (bShouldVerify) {
          
          ZipResult zipresult = null;
          boolean bCalculated = false;
          
          try {
              zipresult = VeraZipCaller.calculate(ziprequest);
              bCalculated = true;
          } catch (TaxwareException except) {
              if (isLoggingError())
                  logError(SalesTaxService.msg.format("TXWRErrorWithExceptVerifyZip",
                                                      except.toString()));
              bCalculated = setErrorAndGetReturnValue(except.toString(),
                                                      zipOrder.getAddressNameForIndex(i),
                                                      ziprequest);
          }
          
          bResult = bResult && bCalculated;
          // always reset the zip result, so the old
          // one will be blown away in case of failure.
          zipOrder.setZipResultAt(i, zipresult);
          
          if (bCalculated && (zipresult != null)) {
              
              // what to do the code of 29 ? There is no way to pass the county 
              // name from the web interface. Also, even if the county is passed in the
              // ziprequest object, this code is returned anyways...
              if (zipresult.getErrorCode() != 0 && zipresult.getErrorCode() != 29) {
                  bCalculated = setErrorAndGetReturnValue(zipresult.getErrorMessage(),
                                                          zipOrder.getAddressNameForIndex(i),
                                                          ziprequest);
                  bResult = bResult && bCalculated;
              }
              
          }
          
          if (isLoggingDebug()) {
              if ((null != zipresult) && (zipresult.getResultItemCount() > 0)) {
                  int numRecs = zipresult.getResultItemCount();
                  for (int j=0; j < numRecs; j++) {
                      System.out.println(zipresult.getDescriptionAt(j));
                  }
              }
          }

      }
    }
    
    if (mStopProcessingOnError)
        return(bResult);
    else
        return(true);
  }
    
}
