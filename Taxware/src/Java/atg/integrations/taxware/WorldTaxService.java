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


/** <p>WorldTaxService provides a simpler interface for creating TaxRequest
 *     objects for international requests.
 *     
 * @see TaxRequest
 * @see SalesTaxCaller
 * @see TaxResult
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/WorldTaxService.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class WorldTaxService extends TaxService {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/WorldTaxService.java#2 $$Change: 651448 $";
 
  // required parameters for a worldtax taxing
  protected String mDocumentNumber;
  protected String mTransactionType;
  protected String mSellerRegNumber;
  protected String mTaxCode;
  protected String mCommodityCode;
 
  public void setDocumentNumber(String pDocumentNumber) {
      mDocumentNumber = pDocumentNumber;
  }

  public String getDocumentNumber() {
      return mDocumentNumber;
  }

  public void setTransactionType(String pTransactionType) {
      mTransactionType = pTransactionType;
  }

  public String getTransactionType() {
      return mTransactionType;
  }
  
  public void setSellerRegNumber(String pSellerRegNumber) {
      mSellerRegNumber = pSellerRegNumber;
  }

  public String getSellerRegNumber() {
      return mSellerRegNumber;
  }

  public void setTaxCode(String pTaxCode) {
      mTaxCode = pTaxCode;
  }

  public String getTaxCode() {
      return mTaxCode;
  }

  public void setCommodityCode(String pCommodityCode) {
      mCommodityCode = pCommodityCode;
  }

  public String getCommodityCode() {
      return mCommodityCode;
  }

 
  /** Create a new TaxRequest, filling in Destination and POA information
   * from our member variables.
   */
  public TaxRequest createRequest(String strDstCountry, String strDstCity,
                                  String strDstProvince, String strDstZip,
                                  String strPOACountry, String strPOACity,
                                  String strPOAProvince, String strPOAZip,
                                  String strCurrencyCode, long centsPrice, 
                                  long centsDiscountPrice, long centsFreight, 
                                  java.util.Date dateTax) {
      
      TaxRequest request = new TaxRequest(getShipFromCountry(), 
                                          null, null, null,
                                          strDstCountry, strDstCity,
                                          strDstProvince, strDstZip,
                                          null, null, null, null,
                                          strPOACountry, strPOACity,
                                          strPOAProvince, strPOAZip,
                                          centsPrice, centsDiscountPrice,
                                          centsFreight, dateTax, 
                                          getCompanyId());
      
      // required params for the worldtax taxing
      if (getDocumentNumber() != null)
          request.setFieldValue("DOCUMENTNUMBER", getDocumentNumber());
      if (getTransactionType() != null)
          request.setFieldValue("TRANSACTIONTYPE", getTransactionType());
      if (getSellerRegNumber() != null)
          request.setFieldValue("SELLERREGNUM", getSellerRegNumber());
      if (getCommodityCode() != null)
          request.setFieldValue("COMMODCODE", getCommodityCode());
      if (getTaxCode() != null)
          request.setFieldValue("WTAXCODE", getTaxCode());
      if (strCurrencyCode != null)
          request.setFieldValue("CURRENCYCODE1", strCurrencyCode);
      
      return(request);
  
  }

}

