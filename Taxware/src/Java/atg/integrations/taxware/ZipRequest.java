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

/** <p>The ZipRequest class defines a request for zip verification
 * information. It defines a number of convenience functions for accessing
 * commonly used properties, but allows the generic accessor functions for
 * FieldSet to be used for other properties.
 *
 * <p>See the verazip documentation for a detailed description of what
 * the various fields mean in practice.
 *
 * @see ZipResult
 * @see VeraZipCaller
 * @see FieldSet
 * @see ZipInRecDef
 * @author cmore
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/ZipRequest.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ZipRequest extends FieldSet {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/ZipRequest.java#2 $$Change: 651448 $";


  protected static RecordDef m_recordDef;

  protected synchronized void createRecordDef() {
    if (null != m_recordDef)
      return;
    m_recordDef = new ZipInRecDef();
  }

  RecordDef getRecordDef() {
    if (null == m_recordDef) {
      createRecordDef();
    }
    return(m_recordDef);
  }

  static FieldDefinition fieldDefs[] = null;

  // not really intended for external use
  ZipRequest() {}

  /**
   * Create a tax request setting the commonly used fields.<p>
   *
   * Defaults to credit transaction.
   */
  public ZipRequest(String strStateCode, String strZipCode,
                    String strCityName, String strPostalCode, 
                    String strCountyName, String strCountyCode) {
                    
    if (null != strStateCode) setStateCode(strStateCode);
    if (null != strZipCode) setZipCode(strZipCode);
    if (null != strCityName) setCityName(strCityName);
    if (null != strPostalCode) setPostalCode(strPostalCode);
    if (null != strCountyName) setCountyName(strCountyName);
    if (null != strCountyCode) setCountyCode(strCountyCode);
    
  }


  public static void main(String rgArgs[]) {
    ZipRequest zipreq = new ZipRequest();
    zipreq.dumpFieldDefs();
  }


  // -------------------------------------------------------
  // set StateCode
  public void setStateCode(String strStateCode) {
    setFieldValue("STATECODE", strStateCode);
  }

  // get StateCode
  public String getStateCode() {
    return(getStringFieldValue("STATECODE"));
  }

  
  // -------------------------------------------------------
  // set ZipCode
  public void setZipCode(String strZipCode) {
    setFieldValue("ZIPCODE", strZipCode);
  }

  // get ZipCode
  public String getZipCode() {
    return(getStringFieldValue("ZIPCODE"));
  }

  // -------------------------------------------------------
  // set CityName
  public void setCityName(String strCityName) {
    setFieldValue("CITYNAME", strCityName);
  }

  // get CityName
  public String getCityName() {
    return(getStringFieldValue("CITYNAME"));
  }
  
  // -------------------------------------------------------
  // set PostalCode
  public void setPostalCode(String strPostalCode) {
    setFieldValue("POSTCODE", strPostalCode);
  }

  // get PostalCode
  public String getPostalCode() {
    return(getStringFieldValue("POSTCODE"));
  }

  // -------------------------------------------------------
  // set CountyName
  public void setCountyName(String strCountyName) {
    setFieldValue("COUNTYNAME", strCountyName);
  }

  // get CountyName
  public String getCountyName() {
    return(getStringFieldValue("COUNTYNAME"));
  }

  // -------------------------------------------------------
  // set CountyCode
  public void setCountyCode(String strCountyCode) {
    setFieldValue("COUNTYCODE", strCountyCode);
  }

  // get CountyCode
  public String getCountyCode() {
    return(getStringFieldValue("COUNTYCODE"));
  }

  // A string representation of this objec
  public String toString() {
      StringBuffer string = new StringBuffer();
      string.append("StateCode: " + getStateCode() + "\n");
      string.append("ZipCode: " + getZipCode() + "\n");
      string.append("CityName: " + getCityName() + "\n");
      string.append("PostalCode: " + getPostalCode());
      return string.toString();
  }

}

