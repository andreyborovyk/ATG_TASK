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

/** <p>The TaxResult class represents the result of a request for tax
 * information. It defines a few convenience functions for accessing
 * commonly used properties, but allows the generic accessor functions for
 * FieldSet to be used for other properties.
 *
 * <p>See the taxware documentation for a detailed description of what
 * the various fields mean in practice.
 *
 * <p> Tax rate results are returned from taxware as strings with 6 decimal
 * places, so one should divide by 1,000,000 to get the actual tax rate.
 *
 * @see TaxRequest
 * @see SalesTaxCaller
 * @see FieldSet
 * @see OutputRecordDef
 * @author cmore
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/TaxResult.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */


public class TaxResult extends FieldSet {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/TaxResult.java#2 $$Change: 651448 $";


  /** Should be used internally, only. */
  TaxResult() {
    // do nothing, for the nonce
  }


  /** Hold our record definition. */
  protected static RecordDef m_recordDef;

  /** Create out record definition */
  protected synchronized void createRecordDef() {
    if (null != m_recordDef)
      return;
    m_recordDef = new OutputRecordDef();
  }

  /** Get our record definition */
  RecordDef getRecordDef() {
    if (null == m_recordDef) {
      createRecordDef();
    }
    return(m_recordDef);
  }
  

  /** Dump out field definitions */
  public static void main(String rgArgs[]) {
    TaxResult taxresult = new TaxResult();
    taxresult.dumpFieldDefs();
  }


  //---------- tax rates ------------
  
  /** Return the country tax rate. */
  public double getCountryTaxRate () {
    return(getDoubleFieldValue("TAXRATECOUNTRY"));
  }

  /** Return the territory tax rate. */
  public double getTerritoryTaxRate () {
    return(getDoubleFieldValue("TAXRATETERRITORY"));
  }

  /** Return the province(state) tax rate. */
  public double getProvinceTaxRate() {
    return(getDoubleFieldValue("TAXRATEPROVINCE"));
  }
  

  /** Return the county tax rate. */
  public double getCountyTaxRate() {
    return(getDoubleFieldValue("TAXRATECOUNTY"));
  }
  

  /** Return the city tax rate as an int.. */
  public double getCityTaxRate() {
    return(getDoubleFieldValue("TAXRATECITY"));
  }


  //---------- secondary tax rates ------------
  /** Return the secondary province(state) tax rate. */
  public double getSecProvinceTaxRate() {
    return(getDoubleFieldValue("SECPROVINTXRATE"));
  }
  

  /** Return the secondary county tax rate. */
  public double getSecCountyTaxRate() {
    return(getDoubleFieldValue("SECCOUNTYTXRATE"));
  }
  

  /** Return the secondary city tax rate as an int. */
  public double getSecCityTaxRate() {
    return(getDoubleFieldValue("SECCITYTXRATE"));
  }

  

  //---------- calculated tax amounts ------------
  
  /** Return the country tax amount. */
  public double getCountryTaxAmount () {
    return(getDoubleFieldValue("CALCAMTCOUNTRY"));
  }
  
  /** Return the territory tax amount. */
    public double getTerritoryTaxAmount() {
    return(getDoubleFieldValue("CALCAMTTERRITORY"));
  }


  /** Return the province(state) tax amount. */
  public double getProvinceTaxAmount() {
    return(getDoubleFieldValue("CALCAMTPROVINCE"));
  }

  /** Return the county tax amount. */
  public double getCountyTaxAmount() {
    return(getDoubleFieldValue("CALCAMTCOUNTY"));
  }

  /** Return the city tax amount as an int. */
  public double getCityTaxAmount() {
    return(getDoubleFieldValue("CALCAMTCITY"));
  }


  //---------- secondary calculated tax amounts ------------
  
  /** Return the secondary province(state) tax amount. */
  public double getSecProvinceTaxAmount() {
    return(getDoubleFieldValue("SECPROVINCETXAMT"));
  }

  /** Return the secondary county tax amount. */
  public double getSecCountyTaxAmount() {
    return(getDoubleFieldValue("SECCOUNTYTXAMT"));
  }

  /** Return the secondary city tax amount as an int. */
  public double getSecCityTaxAmount() {
    return(getDoubleFieldValue("SECCITYTXAMT"));
  }
  

  //---------- tax basis Amounts ------------
  
  /** Return the country tax basis amount. */
  public double getCountryTaxBasisAmount() {
    return(getDoubleFieldValue("COUNTRYBASISAMT"));
  }
 
  /** Return the territory tax basis amount. */
  public double getTerritoryTaxBasisAmount() {
    return(getDoubleFieldValue("TERRITORYBASISAMT"));
  }

  /** Return the province(state) tax basis amount. */
  public double getProvinceTaxBasisAmount() {
    return(getDoubleFieldValue("PROVINCEBASISAMT"));
  }

  /** Return the county tax basis amount. */
  public double getCountyTaxBasisAmount() {
    return(getDoubleFieldValue("COUNTYBASISAMT"));
  }

  /** Return the city tax basis amount as an int. */
  public double getCityTaxBasisAmount() {
    return(getDoubleFieldValue("CITYBASISAMT"));
  }


  //---------- secondary tax basis Amounts ------------
  
  /** Return the secondary province(state) tax basis amount. */
  public double getSecProvinceTaxBasisAmount() {
    return(getDoubleFieldValue("SECPROVINBASISAMT"));
  }

  /** Return the secondary county tax basis amount. */
  public double getSecCountyTaxBasisAmount() {
    return(getDoubleFieldValue("SECCOUNTYBASISAMT"));
  }

  /** Return the secondary city tax basis amount as an int. */
  public double getSecCityTaxBasisAmount() {
    return(getDoubleFieldValue("SECCITYBASISAMT"));
  }
  

  // ------ completion codes -----

  /** Return the general completion code. */
  public long getGeneralCompletionCode() {
    return(getLongFieldValue("GENCMPLCD"));
  }

  /** Return the general completion code description. */
  public String getGeneralCompletionCodeDescription() {
    return(getStringFieldValue("GENCMPLCDDSC"));
  }

  public String toString() {
      
      double taxAmount = 0;
      double countryTax = 0;
      double territoryTax = 0;
      double provinceTax = 0;
      double countyTax = 0;
      double cityTax = 0;
      
      countryTax = getCountryTaxAmount();
      territoryTax = getTerritoryTaxAmount();
      provinceTax = getProvinceTaxAmount() + getSecProvinceTaxAmount();
      countyTax = getCountyTaxAmount() + getSecCountyTaxAmount();
      cityTax = getCityTaxAmount() + getSecCityTaxAmount();
      taxAmount = countryTax + territoryTax + 
          provinceTax + countyTax + cityTax;
      
      StringBuffer string = new StringBuffer();
      string.append("--------- A response from taxware --------- \n");
      string.append("Country Tax: " + countryTax + "\n");
      string.append("Territory Tax: " + territoryTax + "\n");
      string.append("State/Province Tax: " + provinceTax + "\n");
      string.append("County Tax: " + countyTax + "\n");
      string.append("City Tax: " + cityTax + "\n");
      string.append("Total Tax: " + taxAmount);
      return string.toString();
      
  }
    
}
