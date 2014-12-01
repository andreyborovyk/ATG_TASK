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

/** <p>The ZipResultItem class represents the result of a request for zip
 * verification information. It defines a few convenience functions for
 * accessing commonly used properties, but allows the generic accessor
 * functions for FieldSet to be used for other properties.
 *
 * <p>See the verazip documentation for a detailed description of what
 * the various fields mean in practice.
 *
 * @see ZipRequest
 * @see ZipResult
 * @see VeraZipCaller
 * @see FieldSet
 * @see OutputRecordDef
 * @author cmore
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/ZipResultItem.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */


public class ZipResultItem extends FieldSet {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/ZipResultItem.java#2 $$Change: 651448 $";

  final static String VERAZIP_WITHIN_CITYLIMIT = "0";
  
  /** Construct a new ZipResultItem. Used internally. */
  ZipResultItem() {
    // do nothing, for the nonce
  }

  /** Holds the pointer to our static RecordDef object. */
  protected static RecordDef m_recordDef;

  /** Create our recordDef object. */
  protected synchronized void createRecordDef() {
    if (null != m_recordDef)
      return;
    
    m_recordDef = new ZipOutRecDef();
  }

  /** Get our static RecordDef. */
  RecordDef getRecordDef() {
    if (null == m_recordDef) {
      createRecordDef();
    }
    return(m_recordDef);
  }
  

  /** Dump out our fielddefs. */
  public static void main(String rgArgs[]) {
    ZipResultItem zipresultItem = new ZipResultItem();
    zipresultItem.dumpFieldDefs();
  }


  //---------- tax rates ------------
  
  /** Return the state code. */
  public String  getStateCode() {
    return(getStringFieldValue("STATECODE"));
  }

  /** Return the first zip code. */
  public String getFirstZipCode () {
    return(getStringFieldValue("FIRSTZIPCODE"));
  }

  /** Return the second zip code. */
  public String getSecondZipCode () {
    return(getStringFieldValue("SECONDZIPCODE"));
  }
  
  /** Return the GEOCODE. */
  public String getGeoCode() {
    return(getStringFieldValue("GEOCODE"));
  }
  

  /** Return the city name. */
  public String getCityName() {
    return(getStringFieldValue("CITYNAME"));
  }
  

  /** Return the county code. */
  public String getCountyCode() {
    return(getStringFieldValue("COUNTYCODE"));
  }


  /** Return the county name. */
  public String getCountyName() {
    return(getStringFieldValue("COUNTYNAME"));
  }
  
  /** Return the county code.
   * @deprecated This method is deprecated use <code>getOutsideCityLimits()</code>
   *
   * The possible values of this field have been extended to include another field, 2,
   * which indicates location is at police jurisdication. But since this method assumes
   * that the field contains only two values,  this method has been modified to return
   * false even if the location is at police jurisdication to provide backward compatibility.
   * To use this field use the method <code>getOutsideCityLimits()</code> which gives the accurate
   * result.
   * 
   */

  public boolean isOutsideCityLimits() {

    return !VERAZIP_WITHIN_CITYLIMIT.equals(getStringFieldValue("OUTCITYLIM"));

  }

  /**
   * The possible return values of this method are..
   * 0, -- indicating that this location is inside city limit
   * 1, -- indicating that this location is outside city limit
   * 2, -- indicating that this location is at police jurisdiction
   **/

   public String getOutsideCityLimits(){
    return(getStringFieldValue("OUTCITYLIM"));
  }

  /** Helper functions which returns a null-safe string equals.
   * Not and empty string does not count as null for equality
   * purposes.
   */
  static final boolean nullSafeStringEquals(String strOne, String strTwo) {
    if (strOne == null) {
      if (strTwo == null)
        return(true);
      
      return(false);
    } else if (strTwo == null)
      return(false);

    return(strOne.equalsIgnoreCase(strTwo));
  }


  /** Returns an integer representing which fields differ. */
  public byte compareItem(ZipResultItem itemCompare) {
    byte bResult = 0;

    if (!nullSafeStringEquals(getStateCode(),
                               itemCompare.getStateCode()))
      bResult = (byte)(bResult | ZipResult.STATE_VARIES_BIT);

    if (!nullSafeStringEquals(getFirstZipCode(),
                              itemCompare.getFirstZipCode()))
      bResult = (byte) (bResult | ZipResult.ZIP_VARIES_BIT);

    if (!nullSafeStringEquals(getCityName(),
                              itemCompare.getCityName()))
      bResult = (byte) (bResult | ZipResult.CITY_VARIES_BIT);

    if (!nullSafeStringEquals(getOutsideCityLimits(),
                              itemCompare.getOutsideCityLimits())) {
      bResult = (byte) (bResult | ZipResult.CITY_LIM_VARIES_BIT);
    } // end of if ()
    
    if (!nullSafeStringEquals(getCountyCode(),
                              itemCompare.getCountyCode()))
      bResult = (byte) (bResult | ZipResult.COUNTY_VARIES_BIT);
    
    return(bResult);
  }


  /** Return true if pass in City, StateCode and ZIP
   * match the contents of the result item.
   */
  public boolean matchesCityStateZip(String pCity, String pStateCode,
                                     String pZip) {
    if (!nullSafeStringEquals(getStateCode(), pStateCode))
      return(false);
      
    if (!nullSafeStringEquals(getFirstZipCode(), pZip))
      return(false);

    if (!nullSafeStringEquals(getCityName(), pCity))
      return(false);

    return(true);
  }
    
}
