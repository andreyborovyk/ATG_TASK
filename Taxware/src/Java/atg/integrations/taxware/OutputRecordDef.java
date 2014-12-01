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
 * <p>Defined the record def for the individual output record to define
 *    on individual output record of the TaxWare "taxcommon" API. Used
 *    by the TaxResult class.
 *
 * @see RecordDef
 * @see TaxResult
 * @author cmore
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/OutputRecordDef.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public
class OutputRecordDef extends RecordDef {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/OutputRecordDef.java#2 $$Change: 651448 $";

  /** Create a new output record definition */
  OutputRecordDef() {
    super(125);
  }

  /** initialize the field definition to define an TaxWare
   * "taxcommon" API individual output record.
   */
  protected void initializeFieldDefs() {
    // individual output record layout
    addFieldDef("SYSIND", 1);
    addFieldDef("GENCMPLCD", COMPCODESIZE, FieldDefinition.INT_FTYPE);
    addFieldDef("COUNTRYCMPLCD", COMPCODESIZE, FieldDefinition.INT_FTYPE);
    addFieldDef("TERRITORYCMPLCD", COMPCODESIZE, FieldDefinition.INT_FTYPE);
    addFieldDef("PROVINCECMPLCD", COMPCODESIZE, FieldDefinition.INT_FTYPE);
    addFieldDef("COUNTYCMPLCD", COMPCODESIZE, FieldDefinition.INT_FTYPE);
    addFieldDef("CITYCMPLCD", COMPCODESIZE, FieldDefinition.INT_FTYPE);
    addFieldDef("SECPROVINCECMPLCD", COMPCODESIZE, FieldDefinition.INT_FTYPE);
    addFieldDef("SECCOUNTYCMPLCD", COMPCODESIZE, FieldDefinition.INT_FTYPE);
    addFieldDef("SECCITYCMPLCD", COMPCODESIZE, FieldDefinition.INT_FTYPE);
    addFieldDef("DISTCMPLCD", COMPCODESIZE, FieldDefinition.INT_FTYPE);
    addFieldDef("EXTRACMPLCD1", COMPCODESIZE);
    addFieldDef("EXTRACMPLCD2", COMPCODESIZE);
    addFieldDef("EXTRACMPLCD3", COMPCODESIZE);
    addFieldDef("EXTRACMPLCD4", COMPCODESIZE);
    addFieldDef("SUSPCDCOUNTRY", SUSPCODESIZE);
    addFieldDef("SUSPCDPROVINCE", SUSPCODESIZE);
    addFieldDef("SUSPCDCOUNTY", SUSPCODESIZE);
    addFieldDef("SUSPCDCITY", SUSPCODESIZE);
    addFieldDef("GENCMPLCDDSC", NEWMESSAGESIZE);
    addFieldDef("SYSTEMDATE", DATESIZE, FieldDefinition.DATE_FTYPE);
    addFieldDef("TRANSACTIONNUMBER", TRANSIDSIZE);
    addFieldDef("LINEITEMID", LINEITEMSIZE);
    addFieldDef("MANUALTRANSID", CHARSIZE);
    addFieldDef("ZERORATEIND", CHARSIZE);
    addFieldDef("JURLOCTYPE", CHARSIZE);
    addFieldDef("JURCOUNTRY", COUNTRYCODESIZE);
    addFieldDef("JURTERRITORY", TERRITORYCODESIZE);
    addFieldDef("JURPROVINCE", PROVINCESIZE);
    addFieldDef("JURCOUNTY", CNTYCODESIZE);
    addFieldDef("JURCITY", CITYSIZE);
    addFieldDef("JURPOSTALCODE", POSTALCODESIZE);
    addFieldDef("JURZIPEXT", ZIPEXTSIZE);
    addFieldDef("JURGEO", GEOCODESIZE);
    addFieldDef("JURSECPROVINCE", PROVINCESIZE);
    addFieldDef("JURSECCOUNTY", CNTYCODESIZE);
    addFieldDef("JURSECCITY", CITYSIZE);
    addFieldDef("JURSECPOSTALCODE", POSTALCODESIZE);
    addFieldDef("JURSECZIPEXT", ZIPEXTSIZE);
    addFieldDef("JURSECGEO", GEOCODESIZE);
    addFieldDef("TYPCOUNTRY", CHARSIZE);
    addFieldDef("TYPPROVINCE", CHARSIZE);
    addFieldDef("TYPCOUNTY", CHARSIZE);
    addFieldDef("TYPCITY", CHARSIZE);
    addFieldDef("TYPSECPROVINCE", CHARSIZE);
    addFieldDef("TYPSECCOUNTY", CHARSIZE);
    addFieldDef("TYPSECCITY", CHARSIZE);
    addFieldDef("TYPDIST", CHARSIZE);
    addFieldDef("COUNTRYREASCODOUT", REASONCODESIZE);
    addFieldDef("PROVINCEREASCODOUT", REASONCODESIZE);
    addFieldDef("COUNTYREASCODOUT", REASONCODESIZE);
    addFieldDef("CITYREASCODOUT", REASONCODESIZE);
    addFieldDef("COUNTRYCERTNOUT", TAXCERTNOSIZE);
    addFieldDef("PROVINCECERTNOUT", TAXCERTNOSIZE);
    addFieldDef("COUNTYCERTNOUT", TAXCERTNOSIZE);
    addFieldDef("CITYCERTNOUT", TAXCERTNOSIZE);
    addFieldDef("COUNTRYSTATUSCODE", CHARSIZE);
    addFieldDef("COUNTRYCOMMENCODE", CHARSIZE);
    addFieldDef("PROVINCESTATUSCODE", CHARSIZE);
    addFieldDef("PROVINCECOMMENCODE", CHARSIZE);
    addFieldDef("COUNTYSTATUSCODE", CHARSIZE);
    addFieldDef("COUNTYCOMMENCODE", CHARSIZE);
    addFieldDef("CITYSTATUSCODE", CHARSIZE);
    addFieldDef("CITYCOMMENCODE", CHARSIZE);

    addFieldDef("JURCOUNTYNNAME", NAMESIZE);
    addFieldDef("JURSECCOUNTYNAME", NAMESIZE);
    addFieldDef("SELLERREGNUMOUT", AGENTREGNUMBERSIZE);
    
    addFieldDef("RESERV2OUT", 673);
    addFieldDef("LINEITEMAMOUNT", SMTXAMTSIZE, FieldDefinition.SMTXAMT_FTYPE);
    addFieldDef("CALCAMTCOUNTRY", SMTXAMTSIZE, FieldDefinition.SMTXAMT_FTYPE);
    addFieldDef("CALCAMTTERRITORY", SMTXAMTSIZE, FieldDefinition.SMTXAMT_FTYPE);
    addFieldDef("CALCAMTPROVINCE", SMTXAMTSIZE, FieldDefinition.SMTXAMT_FTYPE);
    addFieldDef("CALCAMTCOUNTY", SMTXAMTSIZE, FieldDefinition.SMTXAMT_FTYPE);
    addFieldDef("CALCAMTCITY", SMTXAMTSIZE, FieldDefinition.SMTXAMT_FTYPE);
    addFieldDef("SECPROVINCETXAMT", SMTXAMTSIZE, FieldDefinition.SMTXAMT_FTYPE);
    addFieldDef("SECCOUNTYTXAMT", SMTXAMTSIZE, FieldDefinition.SMTXAMT_FTYPE);
    addFieldDef("SECCITYTXAMT", SMTXAMTSIZE, FieldDefinition.SMTXAMT_FTYPE);
    addFieldDef("DISTTXAMT", SMTXAMTSIZE, FieldDefinition.SMTXAMT_FTYPE);
    addFieldDef("COUNTRYBASISAMT", SMTXAMTSIZE, FieldDefinition.SMTXAMT_FTYPE);
    addFieldDef("TERRITORYBASISAMT", SMTXAMTSIZE, FieldDefinition.SMTXAMT_FTYPE);
    addFieldDef("PROVINCEBASISAMT", SMTXAMTSIZE, FieldDefinition.SMTXAMT_FTYPE);
    addFieldDef("COUNTYBASISAMT", SMTXAMTSIZE, FieldDefinition.SMTXAMT_FTYPE);
    addFieldDef("CITYBASISAMT", SMTXAMTSIZE, FieldDefinition.SMTXAMT_FTYPE);
    addFieldDef("SECPROVINBASISAMT", SMTXAMTSIZE, FieldDefinition.SMTXAMT_FTYPE);
    addFieldDef("SECCOUNTYBASISAMT", SMTXAMTSIZE, FieldDefinition.SMTXAMT_FTYPE);
    addFieldDef("SECCITYBASISAMT", SMTXAMTSIZE, FieldDefinition.SMTXAMT_FTYPE);
    addFieldDef("DISTBASISAMT", SMTXAMTSIZE, FieldDefinition.SMTXAMT_FTYPE);
    addFieldDef("TAXRATECOUNTRY", NEWRATESIZE, FieldDefinition.NEWRATE_FTYPE);
    addFieldDef("TAXRATETERRITORY", NEWRATESIZE, FieldDefinition.NEWRATE_FTYPE);
    addFieldDef("TAXRATEPROVINCE", NEWRATESIZE, FieldDefinition.NEWRATE_FTYPE);
    addFieldDef("TAXRATECOUNTY", NEWRATESIZE, FieldDefinition.NEWRATE_FTYPE);
    addFieldDef("TAXRATECITY", NEWRATESIZE, FieldDefinition.NEWRATE_FTYPE);
    addFieldDef("SECPROVINTXRATE", NEWRATESIZE, FieldDefinition.NEWRATE_FTYPE);
    addFieldDef("SECCOUNTYTXRATE", NEWRATESIZE, FieldDefinition.NEWRATE_FTYPE);
    addFieldDef("SECCITYTXRATE", NEWRATESIZE, FieldDefinition.NEWRATE_FTYPE);
    addFieldDef("DISTTXRATE", NEWRATESIZE, FieldDefinition.NEWRATE_FTYPE);
    addFieldDef("CALCCOUNTRYRATE", NEWRATESIZE, FieldDefinition.NEWRATE_FTYPE);
    addFieldDef("CALCTERRITORYRATE", NEWRATESIZE, FieldDefinition.NEWRATE_FTYPE);
    addFieldDef("CALCPROVINCERATE", NEWRATESIZE, FieldDefinition.NEWRATE_FTYPE);
    addFieldDef("CALCCOUNTYRATE", NEWRATESIZE, FieldDefinition.NEWRATE_FTYPE);
    addFieldDef("CALCCITYRATE", NEWRATESIZE, FieldDefinition.NEWRATE_FTYPE);

    // !!! following is adjusted by one !!!
    addFieldDef("REPORTIND", REPORTINDSIZE, FieldDefinition.INT_FTYPE); 
    addFieldDef("TAXAMTPFCOUNTRY", VOLUMEEXPSIZE, FieldDefinition.INT_FTYPE);
    addFieldDef("TAXAMTPFTERRITORY", VOLUMEEXPSIZE, FieldDefinition.INT_FTYPE);
    addFieldDef("TAXAMTPFPROVINCE", VOLUMEEXPSIZE, FieldDefinition.INT_FTYPE);
    addFieldDef("TAXAMTPFCOUNTY", VOLUMEEXPSIZE, FieldDefinition.INT_FTYPE);
    addFieldDef("TAXAMTPFCITY", VOLUMEEXPSIZE, FieldDefinition.INT_FTYPE);
    // addFieldDef("AUDITRECORD", 1367); // ??? no idea
  }
}
