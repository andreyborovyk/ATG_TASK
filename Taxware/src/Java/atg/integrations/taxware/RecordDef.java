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
import java.util.Hashtable;
import java.lang.*;

/**
 * <p>RecordDef defines a record, which is composed of a series of field
 *    definitions. A RecordDef defines a set of fields for a FieldSet
 *    (perhaps FieldSet would be better called a Record).
 *
 * <p>A RecordDef represents one of those awful, kobol-like fix width
 * records in which position is everything and empty fields are represented
 * by spaces of the field length.
 *
 * <p>Subclasses should redefine initializeFieldDefs to call addFieldDef
 *    for each field, in order, the Record contains.
 *
 * @see FieldDefinition
 * @see FieldSet
 * @author cmore
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/RecordDef.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public abstract
class RecordDef {
  //-------------------------------------
  // Class version string

  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/RecordDef.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Member variables

  /** array of field definitions */
  FieldDefinition m_rgfieldDefs[];
  
  /** last index */
  int m_cLastFieldIndex;

  /** last field offset -- ends up being the size of the record */
  int m_cLastFieldOffset;
  
  /** maximum number of fields -- the size of the field array */
  int m_cFieldMax;
  
  /** Hash table which maps field names to FieldDefinitions */
  Hashtable m_hashFieldNames;         // hasthtable of names to def
  
  RecordDef(int cMaxFields) {
      m_rgfieldDefs = new FieldDefinition[cMaxFields];
      m_cLastFieldIndex=0;
      m_cLastFieldOffset=0;
      m_hashFieldNames = new Hashtable();
      initializeFieldDefs();
  }

  //-------------------------------------
  /**
   * Dump the contents of the fields to System.out. Used
   * for debugging
   */
  void dumpFieldDefs() {
    if (null == m_rgfieldDefs) {
      System.out.println(SalesTaxService.msg.getString("TXWRRecDefNullMRGField"));
    }
    else {
      for (int i = 0; i < m_cLastFieldIndex; i++) {
        FieldDefinition fielddefCur = m_rgfieldDefs[i];

        Integer tRecordNumber = Integer.valueOf(i);
        String tName = fielddefCur.getName();
        Integer tOffset = Integer.valueOf(fielddefCur.getOffset());
        Integer tLength = Integer.valueOf(fielddefCur.getLength());

        Object[] dArgs = {tRecordNumber, tName, tOffset, tLength};
        System.out.println(SalesTaxService.msg.format("TXWRRecDefDumpFields", dArgs));
      }
    }
  }
  
  protected abstract void initializeFieldDefs();

  // add a field definition of the specified size, default to String type
  void addFieldDef(String strName, int size) {
    addFieldDef(strName, size, FieldDefinition.STR_FTYPE);
  }

  // add a field definition of the specified size and type
  void addFieldDef(String strName, int size, FieldType ftype) {

    FieldDefinition fielddefNew = new FieldDefinition(
      strName, m_cLastFieldOffset, size, ftype);
    
    m_rgfieldDefs[m_cLastFieldIndex++] = fielddefNew;

    m_hashFieldNames.put(strName, fielddefNew);

    m_cLastFieldOffset += size;
  }
  

  public FieldDefinition getFieldDefByName(String strFieldDef) {
    return((FieldDefinition)m_hashFieldNames.get(strFieldDef));
  }

  public FieldDefinition getFieldDefByIndex(int idx) {
    return(m_rgfieldDefs[idx]);
  }

  public int getRecordLength() { return(m_cLastFieldOffset); }
  public int getFieldCount() { return(m_cLastFieldIndex); }

  
  
  final static int NUMLINEITEMSIZE=6;
  final static int CHARSIZE=1;
  final static int LANGCODESIZE=3;
  final static int POSTALCODECODESIZE=9;
  final static int PORTOFLOADSIZE=5;
  final static int NEWRATESIZE=7;
  final static int SYSTEMINDSIZE=2;
  final static int NAMESIZE=26;
  final static int NEWMESSAGESIZE=200;
  final static int POSTALCODESIZE=9;
  final static int SMTXAMTSIZE=14;
  final static int NEWNITEMSIZE=7;
  final static int NEWDIVCODESIZE=20;
  final static int COMPIDSIZE=20;
  final static int COUNTRYCODESIZE=3;
  final static int TERRITORYCODESIZE=3;
  final static int PROVINCESIZE=26;
  final static int LOCNAMESIZE=26;
  final static int CNTYCODESIZE=3;
  final static int CITYSIZE=26;
  final static int ZIPEXTSIZE=4;
  final static int GEOCODESIZE=2;
  final static int DATESIZE=8;
  final static int MODEOFTRANSSIZE=2;
  final static int NEWPRODCODESIZE=25;
  final static int CODESIZE=2;
  final static int REASONCODESIZE=2;
  final static int TAXCERTNOSIZE=25;
  final static int DOCNUMBERSIZE=20;
  final static int CURRENCYCODESIZE=3;
  final static int CUSTOMERSIZE=20;
  final static int UNITOFMEASURESIZE=15;
  final static int DESCRIPTIONSIZE=100;
  final static int CONTRACTSIZE=10;
  final static int COSTCENTERSIZE=10;
  final static int LOCATCODESIZE=13;
  final static int ACCTREFSIZE=15;
  final static int DOCTYPESIZE=2;
  final static int REGIONORIGINSIZE=2;
  final static int DELIVERYTERMSSIZE=5;
  final static int AGENTIDSIZE=3;

  final static int AGENTREGNUMBERSIZE=25;
  final static int BASISPERCSIZE=7;
  final static int WORKORDSIZE=26;
  final static int IDEXTSIZE=5;
  final static int DOCEXTSIZE=3;
  final static int LICNOSIZE=3;
  final static int ODOMETERSIZE=7;
  final static int NAME2SIZE=9;
  final static int TRANTIMESIZE=4;
  final static int GROSSVOLSIZE=15;
  
  final static int NOTCSIZE=2;
  final static int STATPROCEDURESIZE=6;
  final static int SUPPLUNITSSIZE=11;
  final static int PARTNUMSIZE=20;
  final static int NEWMISCINFOSIZE=50;
  final static int CUSTOMERNAMESIZE=20;
  final static int INVLINENOSIZE=5;
  final static int CUSTOMERIDSIZE=20;
  final static int CURRCONFACTSIZE=15;
  final static int NETMASSSIZE=15;
  final static int VOLUMEEXPSIZE=3;
  final static int COMPCODESIZE=4;
  final static int SUSPCODESIZE=4;
  final static int TRANSIDSIZE=10;
  final static int LINEITEMSIZE=5;
  final static int REPORTINDSIZE=6;


  // following for NETIO.H

  final static int STATECODESIZE = 2;
  final static int ZIPCODESIZE = 5;

  // following from NETVERZP.H
  final static int POSTCODESIZE = 6;

}
