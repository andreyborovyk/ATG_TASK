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
import java.io.*;
import java.util.*;

/** <p>The TaxRequest class defines a request for tax information. It
 * defines a number of convenience functions for accessing commonly used
 * properties, but allows the generic accessor functions for FieldSet to be
 * used for other properties.
 *
 * <p>See the taxware documentation for a detailed description of what
 * the various fields mean in practice.
 *
 * <p>The price fields are currently set and returned as longs representing
 * price in cents.
 *
 * @see TaxResult
 * @see SalesTaxCaller
 * @see FieldSet
 * @see InputRecordDef
 * @author cmore
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/TaxRequest.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class TaxRequest extends FieldSet {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/TaxRequest.java#2 $$Change: 651448 $";


  /** Holds the RecordDef. */
  protected static RecordDef m_recordDef;

  /** Create the InputRecordDef. Needed for FieldSet. */
  protected synchronized void createRecordDef() {
    if (null != m_recordDef)
      return;
    m_recordDef = new InputRecordDef();
  }

  /** Get the static InputRecordDef */
  RecordDef getRecordDef() {
    if (null == m_recordDef) {
      createRecordDef();
    }
    return(m_recordDef);
  }

  static FieldDefinition fieldDefs[] = null;

  /* for internal use */
  TaxRequest() {}

  /**
   * Create a tax request setting the commonly used fields.<p>
   *
   * Defaults to credit transaction.
   */
  public TaxRequest(String strShipFromCountry, String strShipFromCity,
                    String strShipFromProvince, String strShipFromZip,
                    String strDstCountry, String strDstCity,
                    String strDstProvince, String strDstZip,
                    String strOrgnCountry, String strOrgnCity,
                    String strOrgnProvince, String strOrgnZip,
                    String strPOACountry, String strPOACity,
                    String strPOAProvince, String strPOAZip,
                    long centsPrice, long centsDiscountPrice,
                    long centsFreight, Date dateTax,
                    String strCompany) {
    if (null != strShipFromCountry) setShipFromCountry(strShipFromCountry);
    if (null != strShipFromCity) setShipFromCity(strShipFromCity);
    if (null != strShipFromProvince) setShipFromProvince(strShipFromProvince);
    if (null != strShipFromZip) setShipFromZip(strShipFromZip);
    if (null != strDstCountry) setDstCountry(strDstCountry);
    if (null != strDstCity) setDstCity(strDstCity);
    if (null != strDstProvince) setDstProvince(strDstProvince);
    if (null != strDstZip) setDstZip(strDstZip);
    if (null != strOrgnCountry) setOrgnCountry(strOrgnCountry);
    if (null != strOrgnCity) setOrgnCity(strOrgnCity);
    if (null != strOrgnProvince) setOrgnProvince(strOrgnProvince);
    if (null != strOrgnZip) setOrgnZip(strOrgnZip);
    if (null != strPOACountry) setPOACountry(strPOACountry);
    if (null != strPOACity) setPOACity(strPOACity);
    if (null != strPOAProvince) setPOAProvince(strPOAProvince);
    if (null != strPOAZip) setPOAZip(strPOAZip);

    setPointOfTitlePassage("D"); // defaults to "D"
    
    setPrice(centsPrice);
    setFreight(centsFreight);

    if (null != dateTax) setDate(dateTax);
    if (null != strCompany) setCompany(strCompany);
  
  }



  /** Dump out input fields. */
  public static void main(String rgArgs[]) {
    TaxRequest taxreq = new TaxRequest();
    taxreq.dumpFieldDefs();
  }
  


  // -------------------------------------------------------
  // set ShipFromCountry
  public void setShipFromCountry(String strShipFromCountry) {
    setFieldValue("SHIPFROMCOUNTRY", strShipFromCountry);
  }
  
  // get ShipFromCountry
  public String getShipFromCountry() {
    return(getStringFieldValue("SHIPFROMCOUNTRY"));
  }

  
  // -------------------------------------------------------
  // set ShipFromCity
  public void setShipFromCity(String strShipFromCity) {
    setFieldValue("SHIPFROMCITY", strShipFromCity);
  }
  
  // get ShipFromCity
  public String getShipFromCity() {
    return(getStringFieldValue("SHIPFROMCITY"));
  }  

  // -------------------------------------------------------
  // set ShipFromProvince
  public void setShipFromProvince(String strShipFromProvince) {
    setFieldValue("SHIPFROMPROVINCE", strShipFromProvince);
  }
  
  // get ShipFromProvince
  public String getShipFromProvince() {
    return(getStringFieldValue("SHIPFROMPROVINCE"));
  }

  
  // -------------------------------------------------------
  // set ShipFromZip
  public void setShipFromZip(String strShipFromZip) {
    setFieldValue("SHIPFROMPOSTALCODE", strShipFromZip);
  }
  
  // get ShipFromZip
  public String getShipFromZip() {
    return(getStringFieldValue("SHIPFROMPOSTALCODE"));
  }


  // -------------------------------------------------------
  // set ShipFromGeoCode
  public void setShipFromGeoCode(String strShipFromGeoCode) {
    setFieldValue("SHIPFROMGEO", strShipFromGeoCode);
  }
  
  // get ShipFromGeoCode
  public String getShipFromGeoCode() {
    return(getStringFieldValue("SHIPFROMGEO"));
  }

  
  // -------------------------------------------------------
  // set DstCountry
  public void setDstCountry(String strDstCountry) {
    setFieldValue("DSTCOUNTRY", strDstCountry);
  }

  // get DstCountry
  public String getDstCountry() {
    return(getStringFieldValue("DSTCOUNTRY"));
  }

  // -------------------------------------------------------
  // set DstCity
  public void setDstCity(String strDstCity) {
    setFieldValue("DSTCITY", strDstCity);
  }

  // get DstCity
  public String getDstCity() {
    return(getStringFieldValue("DSTCITY"));
  }


  // -------------------------------------------------------
  // set DstProvince
  public void setDstProvince(String strDstProvince) {
    setFieldValue("DSTPROVINCE", strDstProvince);
  }

  // get DstProvince
  public String getDstProvince() {
    return(getStringFieldValue("DSTPROVINCE"));
  }

  // -------------------------------------------------------
  // set DstZip
  public void setDstZip(String strDstZip) {
    setFieldValue("DSTPOSTALCODE", strDstZip);
  }

  // get DstZip
  public String getDstZip() {
    return(getStringFieldValue("DSTPOSTALCODE"));
  }


  // -------------------------------------------------------
  // set DstGeoCode
  public void setDstGeoCode(String strDstGeo) {
    setFieldValue("DSTGEO", strDstGeo);
  }

  // get DstGeoCode
  public String getDstGeoCode() {
    return(getStringFieldValue("DSTGEO"));
  }
  
  // -------------------------------------------------------
  // set OrgnCountry
  public void setOrgnCountry(String strOrgnCountry) {
    setFieldValue("ORGNCOUNTRY", strOrgnCountry);
  }

  // get OrgnCountry
  public String getOrgnCountry() {
    return(getStringFieldValue("ORGNCOUNTRY"));
  }

  // -------------------------------------------------------
  // set OrgnCity
  public void setOrgnCity(String strOrgnCity) {
    setFieldValue("ORGNCITY", strOrgnCity);
  }

  // get OrgnCity
  public String getOrgnCity() {
    return(getStringFieldValue("ORGNCITY"));
  }

  // -------------------------------------------------------
  // set OrgnProvince
  public void setOrgnProvince(String strOrgnProvince) {
    setFieldValue("ORGNPROVINCE", strOrgnProvince);
  }

  // get OrgnProvince
  public String getOrgnProvince() {
    return(getStringFieldValue("ORGNPROVINCE"));
  }

  // -------------------------------------------------------
  // set OrgnZip
  public void setOrgnZip(String strOrgnZip) {
    setFieldValue("ORGNPOSTALCODE", strOrgnZip);
  }

  // get OrgnZip
  public String getOrgnZip() {
    return(getStringFieldValue("ORGNPOSTALCODE"));
  }


  // -------------------------------------------------------
  // set OrgnGeo
  public void setOrgnGeoCode(String strOrgnGeoCode) {
    setFieldValue("ORGNGEO", strOrgnGeoCode);
  }

  // get OrgnGeo
  public String getOrgnGeoCode() {
    return(getStringFieldValue("ORGNGEO"));
  }
  
  // -------------------------------------------------------
  // set POACountry
  public void setPOACountry(String strPOACountry) {
    setFieldValue("POACOUNTRY", strPOACountry);
  }

  // get POACountry
  public String getPOACountry() {
    return(getStringFieldValue("POACOUNTRY"));
  }

  // -------------------------------------------------------
  // set POACity
  public void setPOACity(String strPOACity) {
    setFieldValue("POACITY", strPOACity);
  }

  // get POACity
  public String getPOACity() {
    return(getStringFieldValue("POACITY"));
  }

  // -------------------------------------------------------
  // set POAProvince
  public void setPOAProvince(String strPOAProvince) {
    setFieldValue("POAPROVINCE", strPOAProvince);
  }

  // get POAProvince
  public String getPOAProvince() {
    return(getStringFieldValue("POAPROVINCE"));
  }

  // -------------------------------------------------------
  // set POAZip
  public void setPOAZip(String strPOAZip) {
    setFieldValue("POAPOSTALCODE", strPOAZip);
  }

  // get POAZip
  public String getPOAZip() {
    return(getStringFieldValue("POAPOSTALCODE"));
  }


  // -------------------------------------------------------
  // set POAGeoCode
  public void setPOAGeoCode(String strPOAGeoCode) {
    setFieldValue("POAGEO", strPOAGeoCode);
  }

  // get POAGeoCode
  public String getPOAGeoCode() {
    return(getStringFieldValue("POAGEO"));
  }
  
  
  // -------------------------------------------------------
  // set PointOfTitlePassage
  public void setPointOfTitlePassage(String strPointOfTitlePassage) {
    setFieldValue("POT", strPointOfTitlePassage);
  }

  // get PointOfTitlePassage
  public String getPointOfTitlePassage() {
    return(getStringFieldValue("POT"));
  }

  // -------------------------------------------------------
  // set Company
  public void setCompany(String strCompany) {
    setFieldValue("COMPANYID", strCompany);
  }

  // get Company
  public String getCompany() {
    return(getStringFieldValue("COMPANYID"));
  }
  

  // -------------------------------------------------------
  // set price
  public void setPrice(long centsPrice) {
    setFieldValue("LINEITEMAMT", centsPrice);
  }

  // get Price
  public long getPrice() {
    return(getLongFieldValue("LINEITEMAMT"));
  }

  // -------------------------------------------------------
  // set discountPrice
  public void setDiscountPrice(long centsDiscountPrice) {
    setFieldValue("DISCOUNTAMT", centsDiscountPrice);
  }

  // get discountPrice
  public long getDiscountPrice() {
    return(getLongFieldValue("DISCOUNTAMT"));
  }

  // -------------------------------------------------------
  // set freight
  public void setFreight(long centsFreight) {
    setFieldValue("FRGHTAMT", centsFreight);
  }

  // get freight
  public long getFreight() {
    return(getLongFieldValue("FRGHTAMT"));
  }
  

  // -------------------------------------------------------
  // set date
  public void setDate(Date dateTax) {
    setFieldValue("TAXPNT", dateTax);
  }

  // get date
  public Date getDate() {
    return(getDateFieldValue("TAXPNT")) ;
  }
  

  // -------------------------------------------------------
  // set credit transaction
  public void setCreditTransaction(boolean bCreditTransaction) {
    setFieldValue("CREDITINDICATOR", bCreditTransaction);
  }

  // get credit transaction
  public boolean getCreditTransaction() {
    return(getBooleanFieldValue("CREDITINDICATOR"));
  }


  // -------------------------------------------------------
  // set CommodityServiceCode
  public void setCommodityServiceCode(String strCommodityServiceCode) {
    setFieldValue("COMMODCODE", strCommodityServiceCode);
  }

  // get CommodityServiceCode
  public String getCommodityServiceCode() {
    return(getStringFieldValue("COMMODCODE"));
  }
 
  // A string representation of this object
  public String toString() {
  
    StringBuffer string = new StringBuffer();
    string.append("-------- A request to taxware ---------- \n");
    string.append("ShipFromCountry: " + getShipFromCountry() + "\n");
    string.append("ShipFromCity: " + getShipFromCity() + "\n");
    string.append("ShipFromProvince: " + getShipFromProvince() + "\n");
    string.append("ShipFromZip: " + getShipFromZip() + "\n");
    string.append("DstCountry: " + getDstCountry() + "\n");
    string.append("DstCity: " + getDstCity() + "\n");
    string.append("DstProvince: " + getDstProvince() + "\n");
    string.append("DstZip: " + getDstZip() + "\n");
    string.append("OrgnCountry: " + getOrgnCountry() + "\n");
    string.append("OrgnCity: " + getOrgnCity() + "\n");
    string.append("OrgnProvince: " + getOrgnProvince() + "\n");
    string.append("OrgnZip: " + getOrgnZip() + "\n");
    string.append("POACountry: " + getPOACountry() + "\n");
    string.append("POACity: " + getPOACity() + "\n");
    string.append("POAProvince: " + getPOAProvince() + "\n");
    string.append("POAZip: " + getPOAZip() + "\n");
    string.append("centsPrice: " + getPrice() + "\n");
    string.append("centsFreight: " + getFreight() + "\n");
    string.append("companyName: " + getCompany() + "\n");
    
    return string.toString();
  }
      

}

