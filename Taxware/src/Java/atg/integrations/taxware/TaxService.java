/* <ATGCOPYRIGHT>
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
 * "Dynamo" is a trademark of Art Technology Group, Inc. </ATGCOPYRIGHT>
 */

package atg.integrations.taxware;

import java.util.*;

import atg.payment.Message;
import atg.nucleus.*;


/** <p>TaxService provides a simpler interface for creating TaxRequest
 *     objects. This class will be subclassed by an actual service (domestic
 *     or international) which will provide the impl to the abstract createRequest
 *     method and additional properties for making a valid taxware request
 *
 * @see TaxRequest
 * @see SalesTaxCaller
 * @see TaxResult
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/TaxService.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public abstract class TaxService extends GenericService {
  //-------------------------------------
  // Class version string

  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/TaxService.java#2 $$Change: 651448 $";

  public static final String Resource_Name = "atg.integrations.taxware.MessageResources";  

  public static Message msg = new Message(Resource_Name);
    
  protected boolean mFailedToInitialize;
  
  // id of company for whom taxes will be requested
  protected String mCompanyId;
  // the country goods are shipped from
  protected String mShipFromCountry;
    
  //---------------------------------------------------------------------------
  // readonly property: failedToInitialize
  //---------------------------------------------------------------------------

  /** Return whether the service failed to initialize */
  public boolean getFailedToInitialize() {
      return mFailedToInitialize;
  }

  // nonFatalCompletionCodes
  protected Set mNonFatalCompCodes = new HashSet(17);

  /** Set the list of non-fatal general completion codes. */
  public void setNonFatalCompletionCodes(long [] pNonFatalCompCodes) {
      mNonFatalCompCodes.clear();
      for (int i = 0; i < pNonFatalCompCodes.length; i++)
	mNonFatalCompCodes.add(Long.valueOf(pNonFatalCompCodes[i]));
      // ensure that a completion code of 0 is never fatal
      mNonFatalCompCodes.add(Long.valueOf(0L));
  }

  /** Get the list of non-fatal general completion codes. */
  public long [] getNonFatalCompletionCodes() {
     long [] ret = new long [mNonFatalCompCodes.size()];
     Object [] objs = mNonFatalCompCodes.toArray();
     for (int i = 0; i < ret.length; i++)
       ret[i] = ((Long) objs[i]).longValue();
     return(ret);
  }

  /*
  //Look at the Set of non-fatal general completion codes.
  public Set getNonFatalCompletionCodesSet() {
      return(mNonFatalCompCodes);
  }
  */

  // CompanyId
  /** Set CompanyId for whom the taxes will be calculated. */
  public void setCompanyId(String pCompanyId) {
      mCompanyId = pCompanyId;
  }

  /** Get CompanyId for whom the taxes will be calculated. */
  public String getCompanyId() { return(mCompanyId); }
  

  // ShipFromCountry
  /** Set 2 character ShipFromCountry used for tax calculations.*/
  public void setShipFromCountry(String pShipFromCountry) {
    mShipFromCountry = pShipFromCountry;
  }

  /** Get ShipFromCountry used for tax calculations.*/
  public String getShipFromCountry() { return(mShipFromCountry); }

  
  /**
   * Initialize data structures when service is started.
   *
   * @exception ServiceException throws if we cannot open TaxWare.
   */
  public void doStartService () throws ServiceException {
      boolean bServiceStarted = false;
      try {
	  // ensure that a completion code of 0 is never fatal
	  mNonFatalCompCodes.add(Long.valueOf(0L));
          SalesTaxCaller.openTaxWare();
          mFailedToInitialize = false;
      }
      catch (TaxwareCriticalException except) {
          if (isLoggingError()) {
              logError(msg.getString("TXWRCriticalServiceNotStarted"), except);
          }
          mFailedToInitialize = true;
          throw new ServiceException(except.toString());
      }
      catch (TaxwareMinorException except) {
          if (isLoggingError()) {
              logError(msg.getString("TXWRMinorServiceNotStarted"), except);
          }
          mFailedToInitialize = true;
          throw new ServiceException(except.toString());
      }
      
      if (!mFailedToInitialize) {
          if (isLoggingInfo()) {
              logInfo(msg.getString("TXWRSalesTaxServiceStart"));
          }
      }
  }

  /** 
   * Create a new TaxRequest, filling in Destination and Point of Acceptance information
   * from our member variables.
   */
  protected abstract TaxRequest createRequest(String strDstCountry, String strDstCity,
                                              String strDstProvince, String strDstZip,
                                              String strPOACountry, String strPOACity,
                                              String strPOAProvince, String strPOAZip,
                                              String strCurrencyCode, long centsPrice, 
                                              long centsDiscountPrice, long centsFreight, 
                                              java.util.Date dateTax);


  /** Actually calculate the sales tax.
   * @exception TaxwareCriticalException Thrown if installation problem.
   * @exception TaxwareMinorException Thrown on bad data or other problem.
   */
  public TaxResult[] calculateSalesTax(TaxRequest[] request)
    throws TaxwareMinorException, TaxwareCriticalException {
      TaxResult[] results = SalesTaxCaller.calculateTax(request, mNonFatalCompCodes);	
      //for (int i = 0; i < results.length; i++) {
      //  TaxResult result = results[i];
      //  if (result.getGeneralCompletionCode() != 0 &&
      //      isLoggingWarning())
      //    logWarning(result.getGeneralCompletionCodeDescription());
      //}
      return(results);
  }

  /**
   * Un-initialize data structures when service is stopped.
   *
   * @exception ServiceException throws if we cannot shutdown TaxWare.
   */
  public void doStopService () throws ServiceException {
      try {
          SalesTaxCaller.closeTaxWare();
          if (isLoggingInfo()) {
              logInfo(msg.getString("TXWRSalesTaxServiceStop"));
          }
      } catch (TaxwareMinorException except) {
          throw new ServiceException(except.toString());
      } catch (TaxwareCriticalException except) {
          throw new ServiceException(except.toString());
      }
  }
  
}

