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

/**
 * <p> Routines to silently trim field lengths down to something
 * VeraZip and TaxWare can handle
 *
 * @author cmore
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/TrimData.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/


public class TrimData {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/TrimData.java#2 $$Change: 651448 $";

  /** Trim the zipcode down to 5 digits... silently */
  public final static String trimZip(String pZip) {
    if (pZip == null)
      return(null);
    String stringZip = pZip.trim();

    StringBuffer str = new StringBuffer();
    for (int i=0; i < stringZip.length(); i++) {
        while (stringZip.charAt(i) == ' ')
            i++;
        str.append(stringZip.charAt(i));
    }

    String strZip = str.toString();
    if (strZip.length() > 5)
      return(strZip.substring(0, 5));
    
    return(strZip);
  }

  /** Trim the canadian postalcode down to 6 digits... silently */
  public final static String trimPostal(String pPostal) {
    if (pPostal == null)
        return(null);
    String stringPostal = pPostal.trim();
    
    StringBuffer str = new StringBuffer();
    for (int i=0; i < stringPostal.length(); i++) {
        while (stringPostal.charAt(i) == ' ')
            i++;
        str.append(stringPostal.charAt(i));
    }
    
    String strPostal = str.toString();
    
    if (strPostal.length() > 6)
        return(strPostal.substring(0, 6));
    
    return(strPostal);
  }


  /** Trim the city-length down to LOCNAMESIZE. */
  public final static String trimCity(String pCity) {
    if (pCity == null)
      return(null);
    String strCity = pCity.trim();
    if (strCity.length() > RecordDef.LOCNAMESIZE)
      return(strCity.substring(0, RecordDef.LOCNAMESIZE));
    
    return(strCity);
  }  
}
