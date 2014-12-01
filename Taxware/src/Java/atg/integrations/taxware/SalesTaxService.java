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


/** <p>SalesTaxService provides a simpler interface for creating TaxRequest
 *     objects for domestic requests. Each instance of the SalesTaxService
 *     represents an origin location (based on tax nexus)/ ship-from location pair.
 *
 *  <p>The origin address fields represent the location of the seller.
 *
 *  <p>The shipFrom address fields represent location from
 *     which the goods will be shipped.
 *
 * @see TaxRequest
 * @see SalesTaxCaller
 * @see TaxResult
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/SalesTaxService.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class SalesTaxService extends TaxService {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/SalesTaxService.java#2 $$Change: 651448 $";

  protected String mTaxSelParm;

  protected String mShipFromCity;
  protected String mShipFromState;   // state/province
  protected String mShipFromZip;
  protected String mShipFromGeoCode; // from taxware
  
  protected String mOriginCountry;
  protected String mOriginCity;
  protected String mOriginState;   // state/province
  protected String mOriginZip;
  protected String mOriginGeoCode; // from taxware
  
  public SalesTaxService() {
  }
    
  // ShipFromCity
  /** Set ShipFromCity used for tax calculations.*/
  public void setShipFromCity(String pShipFromCity) {
    mShipFromCity = pShipFromCity;
  }

  /** Get ShipFromCity used for tax calculations.*/
  public String getShipFromCity() { return(mShipFromCity); }
  

  // ShipFromState
  /** Set ShipFromState/Province used for tax calculations.*/
  public void setShipFromState(String pShipFromState) {
    mShipFromState = pShipFromState;
  }

  /** Get ShipFromState used for tax calculations.*/
  public String getShipFromState() { return(mShipFromState); }
  

  // ShipFromZip
  /** Set ShipFromZip/PostalCode used for tax calculations.*/
  public void setShipFromZip(String pShipFromZip) {
    mShipFromZip = pShipFromZip;
  }

  /** Get ShipFromZip/PostalCode used for tax calculations.*/
  public String getShipFromZip() { return(mShipFromZip); }

  // ShipFromGeoCode
  /** Set ShipFrom GeoCode used for tax calculations.*/
  public void setShipFromGeoCode(String pShipFromGeoCode) {
    mShipFromGeoCode = pShipFromGeoCode;
  }
  
  /** Get OriginZip used for tax calculations.*/
  public String getShipFromGeoCode() { return(mOriginGeoCode); }
  
  
  
  // OriginCountry
  /** Set 2 character OriginCountry used for tax calculations.*/
  public void setOriginCountry(String pOriginCountry) {
    mOriginCountry = pOriginCountry;
  }

  /** Get OriginCountry used for tax calculations.*/
  public String getOriginCountry() { return(mOriginCountry); }

  
  // OriginCity
  /** Set OriginCity used for tax calculations.*/
  public void setOriginCity(String pOriginCity) {
    mOriginCity = pOriginCity;
  }

  /** Get OriginCity used for tax calculations.*/
  public String getOriginCity() { return(mOriginCity); }
  

  // OriginState
  /** Set OriginState/Province used for tax calculations.*/
  public void setOriginState(String pOriginState) {
    mOriginState = pOriginState;
  }

  /** Get OriginState used for tax calculations.*/
  public String getOriginState() { return(mOriginState); }
  

  // OriginZip
  /** Set OriginZip/PostalCode used for tax calculations.*/
  public void setOriginZip(String pOriginZip) {
    mOriginZip = pOriginZip;
  }
  
  /** Get OriginZip used for tax calculations. */
  public String getOriginZip() { return(mOriginZip); }


  // OriginGeoCode
  /** Set Origin GeoCode used for tax calculations. */
  public void setOriginGeoCode(String pOriginGeoCode) {
    mOriginGeoCode = pOriginGeoCode;
  }
  
  /** Get OriginZip used for tax calculations. */
  public String getOriginGeoCode() { return(mOriginGeoCode); }
  
  // TaxSel Parm
  // 1 - determine jurisdiction
  // 2 or space - calculate taxes only
  // 3 - 1 and 2 together 
  public void setTaxSelParm(String pTaxSelParm) {
      mTaxSelParm = pTaxSelParm;
  }

  public String getTaxSelParm() {
      return mTaxSelParm;
  }

  
  /** 
   * Create a new TaxRequest, filling in Origin, Ship-From, Destination and POA 
   * information from our member variables.
   */
  public TaxRequest createRequest(String strDstCountry, String strDstCity,
                                  String strDstProvince, String strDstZip,
                                  String strPOACountry, String strPOACity,
                                  String strPOAProvince, String strPOAZip,
                                  String strCurrencyCode, long centsPrice, 
                                  long centsDiscountPrice, long centsFreight, 
                                  java.util.Date dateTax) {

      TaxRequest taxrequest = new TaxRequest(getShipFromCountry(), getShipFromCity(),
                                             getShipFromState(), getShipFromZip(),
                                             strDstCountry, strDstCity,
                                             strDstProvince, strDstZip,
                                             getOriginCountry(), getOriginCity(),
                                             getOriginState(), getOriginZip(),
                                             strPOACountry, strPOACity,
                                             strPOAProvince, strPOAZip,
                                             centsPrice, centsDiscountPrice,
                                             centsFreight, dateTax, getCompanyId());
      
      // additional fields come from the SalesTaxService properties
      // this parameter defines if we want to calculate taxes, determine
      // jurisdiction or both.
      if (null != getTaxSelParm()) 
          taxrequest.setFieldValue("TAXSELPARM", getTaxSelParm());
      
      // set GeoCode for a precise calculation
      if (null != getShipFromGeoCode())
          taxrequest.setShipFromGeoCode(getShipFromGeoCode());
      
      if (null != getOriginGeoCode())
          taxrequest.setOrgnGeoCode(getOriginGeoCode());
      
      return(taxrequest);
      
  }
    
    
}

