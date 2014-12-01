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

import java.io.IOException;

/**
 * <p>Defined the record def for the individual verazip output record.
 *
 * @see RecordDef
 * @author cmore
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/ZipOutRecDef.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public
class ZipOutRecDef extends RecordDef {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/ZipOutRecDef.java#2 $$Change: 651448 $";

  static final int NUMRECSIZE=6;    // from NETVERZP.H
  static final int CCSIZE = 2;

  /** Create a ZipOutRecDef */
  ZipOutRecDef() {
    super(8);
  }

  /** Initialize our array of field definitions. */
  protected void initializeFieldDefs() {
    // individual output record layout
    addFieldDef("STATECODE", STATECODESIZE);
    addFieldDef("FIRSTZIPCODE", ZIPCODESIZE);
    addFieldDef("SECONDZIPCODE", ZIPCODESIZE);
    addFieldDef("GEOCODE", GEOCODESIZE);
    addFieldDef("CITYNAME", LOCNAMESIZE);
    addFieldDef("COUNTYCODE", CNTYCODESIZE);
    addFieldDef("COUNTYNAME", LOCNAMESIZE);
    addFieldDef("OUTCITYLIM", CHARSIZE);
    // addFieldDef("AUDITRECORD", 1367); // ??? no idea
  }
}

