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

/** <p>The ZipResult class represents the result of a request for zip
 * verification information. It contains, among other things,
 * an array of ZipResultItems.
 *
 * <p>It also contains an index which represents the user's
 * choice from the array. 
 *
 * <p>See the verazip documentation for a detailed description of what
 * the various fields mean in practice.
 *
 * @see ZipRequest
 * @see ZipResultItem
 * @see VeraZipCaller
 * @see FieldSet
 * @see OutputRecordDef
 * @author cmore
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/ZipResult.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */


public class ZipResult {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/ZipResult.java#2 $$Change: 651448 $";


  /** Our array of zip result items */
  protected ZipResultItem[] m_rgResultItems;

  /** Error code returned by request */
  protected int m_iErrorCode;

  /** Error string returned by request */
  protected String m_strErrorMessage;

  /** A bit verctor representing the fields which vary accross return items. */
  protected byte m_flagsVary;  // which field vary across the result

  /** The index of the chosen ZipResultItem. Starts out is -1. */
  protected int m_idxChosen;     // chosen index

  /** Bit represeting variation among zip codes of the zip result items. */
  final static byte ZIP_VARIES_BIT = 1 << 0;
  
  /** Bit represeting variation among city names of the zip result items. */
  final static byte CITY_VARIES_BIT = 1 << 1;
  
  /** Bit represeting variation among city limits of the zip result items. */
  final static byte CITY_LIM_VARIES_BIT = 1 << 2;

  /** Bit represeting variation among counties of the zip result items. */
  final static byte COUNTY_VARIES_BIT = 1 << 3;
  
  /** Bit represeting variation among state codes of the zip result items. */
  final static byte STATE_VARIES_BIT = 1 << 4;

  final static String VERAZIP_WITHIN_CITYLIMIT = "0";
  final static String VERAZIP_OUTSIDE_CITYLIMIT = "1";
  final static String VERAZIP_POLICE_JURISDICATION = "2";
  
  /** The city name from the request which generated this result. Used
   * by matchesCityStateZip() to determine if we are valid for
   * a given ZipRequest. */
  String mRequestCity;

  /** The state code from the request which generated this result. Used
   * by matchesCityStateZip() to determine if we are valid for
   * a given ZipRequest. */
  String mRequestStateCode;

  /** The zip code from the request which generated this result. Used
   * by matchesCityStateZip() to determine if we are valid for
   * a given ZipRequest. */
  String mRequestZip;

  /** The array of strings describing the zip result items. There should
   * be one item for each ZipResultItem in our zip result item array. */
  String[] m_rgstrDescriptions;

  /** Create a ZipResult object. Used internally. */
  ZipResult(ZipRequest zrequest,
            int iErrorCode,
            String strErrorMessage,
            ZipResultItem[] resultItemArray) {
    mRequestCity = zrequest.getCityName();
    mRequestStateCode = zrequest.getStateCode();
    mRequestZip = zrequest.getZipCode();
    m_iErrorCode = iErrorCode;
    m_rgResultItems = resultItemArray;
    m_strErrorMessage = strErrorMessage;
    m_idxChosen = -1;

    if (null != m_rgResultItems) {
      int cCount = m_rgResultItems.length;
      m_rgstrDescriptions = new String[cCount];
      byte flags = 0;

      ZipResultItem zri0 = m_rgResultItems[0];

      // first, let's figure out which aspects vary
      for (int i = 1; i < cCount; i++) {
        ZipResultItem zriCur = m_rgResultItems[i];
        flags = (byte)(flags | zri0.compareItem(zriCur));
      }

      m_flagsVary = flags;

      // since the descriptions depend upon which aspects vary,
      // we have to do a separate loop for them.
      for (int i = 0; i < cCount; i++) {
        ZipResultItem zriCur = m_rgResultItems[i];
        m_rgstrDescriptions[i] = getDescriptionForItem(zriCur);
      }

      m_flagsVary = flags;
      
    }
    // do nothing, for the nonce
  }

  /** Returns the ZipResultItem at the specified index. */
  public ZipResultItem getResultItemAt(int idx) {
    if (null == m_rgResultItems)
      return(null);

    return(m_rgResultItems[idx]);
  }

  /** Returns the array of descriptions. The descriptions are
   * display text which allow the user to identify the various
   * options.
   */
  public String[] getDescriptions() {
    return(m_rgstrDescriptions);
  }

  /** Generates the string used to describe a given item. Always
   * specifies city, state and zip, but only specified
   * county or within/outside city limits when necessary. Should
   * be called only after the varies bits have been set.
   */
  protected String getDescriptionForItem(ZipResultItem item) {
    String strDescription = item.getCityName() + ", " +
      item.getStateCode() + "  " + item.getFirstZipCode();

    if (getCountiesVary())
      strDescription = strDescription + " - " +
        item.getCountyName() + " " + SalesTaxService.msg.getString("VERAZIP_COUNTY");
    
    if (getCityLimitsVary())

      if ( item.getOutsideCityLimits().equals(VERAZIP_WITHIN_CITYLIMIT)) {
                  strDescription = strDescription + SalesTaxService.msg.getString("VERAZIP_WITHIN_CITY_LIMITS");
      } else if (item.getOutsideCityLimits().equals(VERAZIP_OUTSIDE_CITYLIMIT)) {
            strDescription = strDescription + SalesTaxService.msg.getString("VERAZIP_OUTSIDE_CITY_LIMITS");        
      } else if ( item.getOutsideCityLimits().equals(VERAZIP_POLICE_JURISDICATION)) {
          strDescription = strDescription + SalesTaxService.msg.getString("VERAZIP_POLICE_JURISDICATION");
      } else {
        strDescription = strDescription + SalesTaxService.msg.getString("VERAZIP_RETURNED_UNKNOWN_VALUE")
                         + item.getOutsideCityLimits();
      } // end of else
    

    return(strDescription);
  }


  /** Returns the description for the item at index idx. */
  public String getDescriptionAt(int idx) {
    String [] rgDescriptions = getDescriptions();
    if (null == rgDescriptions)
      return(null);

    return(rgDescriptions[idx]);
  }

  /** Returns a count of the result items. */
  public int getResultItemCount() {
    if (null == m_rgResultItems)
      return(0);

    return(m_rgResultItems.length);
  }

  /** Returns the error code associated with the ZipResult. */
  public int getErrorCode() {
    return(m_iErrorCode);
  }

  /** Gets the error message associated with the zip result. */
  public String getErrorMessage() {
    return(m_strErrorMessage);
  }

  /** Returns true if FirstZipCode varies amoung the result items. */
  public boolean getZipsVary() {
    return(0 != (ZIP_VARIES_BIT & m_flagsVary));
  }

  /** Returns true if city varies amoung the result items. */
  public boolean getCitiesVary() {
    return(0 != (CITY_VARIES_BIT & m_flagsVary));
  }

  /** Returns true if city limits vary amoung the result items. */
  public boolean getCityLimitsVary() {
    return(0 != (CITY_LIM_VARIES_BIT & m_flagsVary));
  }

  /** Returns true if county names vary amoung the result items. */
  public boolean getCountiesVary() {
    return(0 != (COUNTY_VARIES_BIT & m_flagsVary));
  }

  /** Returns true if state codes vary amoung the result items. */
  public boolean getStatesVary() {
    return(0 != (STATE_VARIES_BIT & m_flagsVary));
  }

  /** Returns true if the value stored in this ZipResult
   * matches the specifiers in the ZipRequest passed in.<p>
   *
   * Used to determine if we need to go back to VeraZip
   * to generate a new ZipResult.
   */
  public boolean matchesRequest(ZipRequest zrequest) {
    return(matchesCityStateZip(zrequest.getCityName(),
                               zrequest.getStateCode(),
                               zrequest.getZipCode()));
  }
  

  /** Returns true if the value stored in this ZipResult
   * matches the specifiers in the ZipRequest passed in.<p>
   *
   * Used to determine if we need to go back to VeraZip
   * to generate a new ZipResult.
   */
  public boolean matchesCityStateZip(String strCity, String strState,
                                     String strZip) {
    
    if (!ZipResultItem.nullSafeStringEquals(mRequestZip,
                                            strZip))
      return(false);
    
    if (!ZipResultItem.nullSafeStringEquals(mRequestStateCode,
                                            strState))
      return(false);
    if (!ZipResultItem.nullSafeStringEquals(mRequestCity,
                                            strCity))
      return(false);

    return(true);
  }
  

  //---------------------------------------------------------------------------
  // property: chosenIndex
  //---------------------------------------------------------------------------

  /** Set the chosen index. The chosen index specifies
   * the user's choice of ZipOptions.
   */
  public void setChosenIndex(int pChosenIndex) {
    m_idxChosen = pChosenIndex;
  }
  
  /** Get the chosen index. The chosen index specifies
   * user's choice of ZipOptions. Returns -1 if the
   * user's choice has not been set.
   */
  public int getChosenIndex() {
    return m_idxChosen;
  }
}
