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
import java.util.*;

/** * <p>A FieldSet represents a set of fields and values for those
 *    fields. The definition for the types, length, and offsets of contained
 *    in the RecordDef class (accessed via getRecordDef()).  A recordDef
 *    object is shared by all instances of a fieldSet subclass.
 *
 * <p>A FieldSet is used for both setting values (via a TaxRequest object)
 *    and for getting values (via a TaxResult object). Conceptually, it is
 *    similar to a 'C' structure, with the ability to read to or write from
 *    a fixed-length character array.
 *
 * <p>The fields values in a FieldSet may be accessed by field definition,
 *    fieldName or field index.
 *
 *
 * @see RecordDef
 * @see TaxRequest
 * @see TaxResult
 * @see ZipRequest
 * @see ZipResultItem
 * @author cmore
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/FieldSet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public
abstract class FieldSet {
  //-------------------------------------
  // Class version string

  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/FieldSet.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Member variables

  /** a hash table in which keys are field definitions
   * and values are objects
   */
  Hashtable m_hashValues;

  /** abstract function which should return a shared RecordDef for instances
    of the subclass. */
  abstract RecordDef getRecordDef();
  
  //-------------------------------------
  /**
   * Constructs an empty FieldSet
   *
   */
  public FieldSet() {
    m_hashValues = new Hashtable();
  }

  /** returns the field definition for a given name */
  FieldDefinition getFieldDefByName(String strFieldName) {
    return(getRecordDef().getFieldDefByName(strFieldName));
  }

  /** returns the field definition for at a given index */
  FieldDefinition getFieldDefByIndex(int idx) {
    return(getRecordDef().getFieldDefByIndex(idx));
  }
  

  // basic object setting routines

  /**
   * Sets the value for the specified field
   * @param fieldDef the field definition used as a key
   * @param objValue the object value
   */
  public void setFieldValue(FieldDefinition fieldDef,
                            Object objValue) {
    fieldDef.getType().validate(fieldDef.getName(),
                                objValue, fieldDef.getLength());
    m_hashValues.put(fieldDef, objValue); 
  }

  /**
   * Sets the value for the specified field given a field anme
   * @param strField the name of the field
   * @param objValue the object value
   */
  public void setFieldValue(String strField,
                            Object objValue) {
    setFieldValue(getFieldDefByName(strField), objValue);
  }

  /**
   * Sets the value for the specified field given a index.
   * @param idx the index of the field to set
   * @param objValue the value 
   */
  public void setFieldValue(int idx, Object objValue) {
    setFieldValue(getFieldDefByIndex(idx), objValue);
  }


  /**
   * Sets the value for the specified field given a index to a given string.
   * @param idx the index of the field to set
   * @param strValue the string value 
   */
  public void setFieldValue(FieldDefinition fieldDef,
                            String strValue) {
    setFieldValue(fieldDef, (Object)strValue); 
  }

  /** Attempts to return the field value of the field named <i>strField</i>
  * as a String value.
  *
  * @param strField the name of the field whose value should be returned.
  */
  public void setFieldValue(String strField,
                            String strValue) {
    setFieldValue(strField, (Object)strValue); 
  }

  /**
   * Sets the value for the specified field <i>strValue</i> to
   * to the string <i>strValue</i>.
   * @param idx the index of the field to set.
   * @param strValue the new string value of the field.
   */
  public void setFieldValue(int idx, String strValue) {
    setFieldValue(idx, (Object)strValue); 
  }

  // integer set routines
  /**
   * Sets the value for the specified field <i>fieldDef</i> to
   * to the integer <i>iValue</i>.
   * @param fieldDef the FieldDefinition of the field to set.
   * @param iValue the new integer value of the field.
   */
  public void setFieldValue(FieldDefinition fieldDef,
                            int iValue) {
    setFieldValue(fieldDef, Integer.valueOf(iValue)); 
  }

  
  /** Sets the value for the specified field named <i>strField</i> to to the
   * integer <i>iValue</i>.
   *
   * @param strField the name of the field to set.
   * @param iValue the new integer value of the field.
   */
  public void setFieldValue(String strField,
                            int iValue) {
    setFieldValue(strField, Integer.valueOf(iValue)); 
  }

  /** Sets the value for the specified field with index <i>idx</i> to to the
   * integer <i>iValue</i>.
   *
   * @param idx the index of the field to set.
   * @param iValue the new integer value of the field.
   */
  public void setFieldValue(int idx, int iValue) {
    setFieldValue(idx, Integer.valueOf(iValue)); 
  }


  // long set routines
  /**
   * Sets the value for the specified field <i>fieldDef</i> to
   * to the long <i>lValue</i>.
   * @param fieldDef the FieldDefinition of the field to set.
   * @param lValue the new long value of the field.
   */
  public void setFieldValue(FieldDefinition fieldDef,
                            long lValue) {
    setFieldValue(fieldDef, Long.valueOf(lValue)); 
  }

  /** Sets the value for the specified field named <i>strField</i> to to the
   * long <i>lValue</i>.
   *
   * @param strField the name of the field to set.
   * @param lValue the new long value of the field.
   */
  public void setFieldValue(String strField,
                            long lValue) {
    setFieldValue(strField, Long.valueOf(lValue)); 
  }
  
  /** Sets the value for the specified field with index <i>idx</i> to to the
   * long <i>lValue</i>.
   *
   * @param idx the index of the field to set.
   * @param lValue the new long value of the field.
   */
  public void setFieldValue(int idx, long lValue) {
    setFieldValue(idx, Long.valueOf(lValue)); 
  }

  // double set routines
  /**
   * Sets the value for the specified field <i>fieldDef</i> to
   * to the double <i>dValue</i>.
   * @param fieldDef the FieldDefinition of the field to set.
   * @param dValue the new double value of the field.
   */
  public void setFieldValue(FieldDefinition fieldDef,
                            double dValue) {
    setFieldValue(fieldDef, Double.valueOf(dValue)); 
  }

  /** Sets the value for the specified field named <i>strField</i> to to the
   * double <i>dValue</i>.
   *
   * @param strField the name of the field to set.
   * @param dValue the new double value of the field.
   */
  public void setFieldValue(String strField,
                            double dValue) {
    setFieldValue(strField, Double.valueOf(dValue)); 
  }

  /** Sets the value for the specified field with index <i>idx</i> to to the
   * double <i>dValue</i>.
   *
   * @param idx the index of the field to set.
   * @param dValue the new double value of the field.
   */
  public void setFieldValue(int idx, double dValue) {
    setFieldValue(idx, Double.valueOf(dValue)); 
  }
  
  // boolean set routines
  /**
   * Sets the value for the specified field <i>fieldDef</i> to
   * to the boolean <i>bValue</i>.
   * @param fieldDef the FieldDefinition of the field to set.
   * @param bValue the new boolean value of the field.
   */
  public void setFieldValue(FieldDefinition fieldDef,
                            boolean bValue) {
    setFieldValue(fieldDef, Boolean.valueOf(bValue)); 
  }

  /** Sets the value for the specified field named <i>strField</i> to to the
   * boolean <i>bValue</i>.
   *
   * @param strField the name of the field to set.
   * @param bValue the new boolean value of the field.
   */
  public void setFieldValue(String strField,
                            boolean bValue) {
    setFieldValue(strField, Boolean.valueOf(bValue)); 
  }

  /** Sets the value for the specified field with index <i>idx</i> to to the
   * boolean <i>bValue</i>.
   *
   * @param idx the index of the field to set.
   * @param bValue the new boolean value of the field.
   */
  public void setFieldValue(int idx, boolean bValue) {
    setFieldValue(idx, Boolean.valueOf(bValue)); 
  }


  // date set routines
  /**
   * Sets the value for the specified field <i>fieldDef</i> to
   * to the Date <i>date</i>.
   * @param fieldDef the FieldDefinition of the field to set.
   * @param date the new Date value of the field.
   */
  public void setFieldValue(FieldDefinition fieldDef,
                            Date date) {
    setFieldValue(fieldDef, (Object)date); 
  }

  /** Sets the value for the specified field named <i>strField</i> to to the
   * Date <i>date</i>.
   *
   * @param strField the name of the field to set.
   * @param date the new Date value of the field.
   */
  public void setFieldValue(String strField,
                            Date date) {
    setFieldValue(strField, (Object)date); 
  }

  /** Sets the value for the specified field with index <i>idx</i> to to the
   * Date <i>date</i>.
   *
   * @param idx the index of the field to set.
   * @param date the new Date value of the field.
   */
  public void setFieldValue(int idx, Date date) {
    setFieldValue(idx, (Object)date); 
  }
  

  // ---Object get routines---

  // --Object getAs routines--
  /** Attempts to return the field value of the field named <i>strField</i>
  * as an instance of the class <i>classTarget</i>.
  *
  * @param fieldDef the FieldDefinition for the field whose value should be
  * returned.
  *
  * @param classTarget the class to try to coerce the associated value into
  * (if it is not of that class already)
  *
  */
  public final Object getFieldValueAs(FieldDefinition fieldDef,
                                      Class classTarget) {
    return(fieldDef.getValueAs(m_hashValues.get(fieldDef), classTarget));
  }

  /** Attempts to return the field value of the field named <i>strField</i>
  * as an instance of the class <i>classTarget</i>.
  *
  * @param strField the name of the field whose value should be returned.
  * @param classTarget the class to try to coerce the associated value into
  *                    (if it is not of that class already)
  *
  */
  public final Object getFieldValueAs(String strField, Class classTarget) {
    FieldDefinition fieldDef = getFieldDefByName(strField);
    if (null == fieldDef) return(null);
    return(fieldDef.getValueAs(m_hashValues.get(fieldDef), classTarget));
  }

  /** Attempts to return the field value of the field with the index
  * <i>idx</i> as an instance of the class <i>classTarget</i>.
  *
  * @param idx the index of the field whose value should be returned.
  * @param classTarget the class to try to coerce the associated value into
  *                    (if it is not of that class already)
  *
  */
  public final Object getFieldValueAs(int idx, Class classTarget) {
    FieldDefinition fieldDef = getFieldDefByIndex(idx);
    if (null == fieldDef) return(null);
    return(fieldDef.getValueAs(m_hashValues.get(fieldDef), classTarget));
  }

  // Object get routines
  /** Returns the field value of the field <i>fieldDef</i>
   * as an Object.
   *
   * @param fieldDef the FieldDefinition for the field whose value should be
   * returned.
   */
  public Object getFieldValue(FieldDefinition fieldDef) {
    return(m_hashValues.get(fieldDef));
  }

  /** Returns the field value of the field named <i>strField</i>
   * as an Object.
   *
   * @param strField the name of the field whose value should be
   * returned.
   */
  public Object getFieldValue(String strField) {
    return(m_hashValues.get(getFieldDefByName(strField)));
  }

  /** Returns the field value of the field with the index <i>idx</i> as an
   * Object.
   *
   * @param idx the index of the field whose value should be
   * returned.
   */
  public Object getFieldValue(int idx) {
    return(m_hashValues.get(getFieldDefByIndex(idx)));
  }

  // String get routines
  /** Attempts to return the field value of the field <i>fieldDef</i>
  * as a String.
  *
  * @param fieldDef the FieldDefinition for the field whose value should be
  * returned.
  */
  public String getStringFieldValue(FieldDefinition fieldDef) {
    return((String)getFieldValueAs(fieldDef, String.class));
  }

  /** Attempts to return the field value of the field named <i>strField</i>
  * as a String.
  *
  * @param strField the name of the field whose value should be
  * returned.
  */
  public String getStringFieldValue(String strField) {
    return((String)getFieldValueAs(strField, String.class));
  }

  /** Attempts to return the field value of the field with an index of
  * <i>idx</i> as a String.
  *
  * @param idx the index of the field whose value should be
  * returned.
  */
  public String getStringFieldValue(int idx) {
    return((String)getFieldValueAs(idx, String.class));
  }
  
  // int get routines
  /** Attempts to return the field value of the field <i>fieldDef</i>
  * as an integer.
  *
  * @param fieldDef the FieldDefinition for the field whose value should be
  * returned.
  */
  public int getIntegerFieldValue(FieldDefinition fieldDef) {
    return ((Integer)getFieldValueAs(fieldDef,
                                     Integer.class)).intValue();
  }

  /** Attempts to return the field value of the field named <i>strField</i>
  * as an integer.
  *
  * @param strField the name of the field whose value should be
  * returned.
  */
  public int getIntegerFieldValue(String strField) {
    return ((Integer)getFieldValueAs(strField, Integer.class)).intValue();
  }

  /** Attempts to return the field value of the field with an index of
  * <i>idx</i> as an integer.
  *
  * @param idx the index of the field whose value should be
  * returned.
  */
  public int getIntegerFieldValue(int idx) {
    return ((Integer)getFieldValueAs(idx, Integer.class)).intValue();
  }


  // long get routines
  /** Attempts to return the field value of the field <i>fieldDef</i>
  * as a long.
  *
  * @param fieldDef the FieldDefinition for the field whose value should be
  * returned.
  */
  public long getLongFieldValue(FieldDefinition fieldDef) {
    return ((Long)getFieldValueAs(fieldDef, Long.class)).longValue();
  }

  /** Attempts to return the field value of the field named <i>strField</i>
  * as a long.
  *
  * @param strField the name of the field whose value should be
  * returned.
  */
  public long getLongFieldValue(String strField) {
      return ((Long)getFieldValueAs(strField, Long.class)).longValue();
  }

  /** Attempts to return the field value of the field with an index of
  * <i>idx</i> as a long.
  *
  * @param idx the index of the field whose value should be
  * returned.
  */
  public long getLongFieldValue(int idx) {
    return ((Long)getFieldValueAs(idx, Long.class)).longValue();
  }


  // Double get routines
  /** Attempts to return the field value of the field <i>fieldDef</i>
  * as a double.
  *
  * @param fieldDef the FieldDefinition for the field whose value should be
  * returned.
  */
  public double getDoubleFieldValue(FieldDefinition fieldDef) {
    return ((Double)getFieldValueAs(fieldDef, Double.class)).doubleValue();
  }

  /** Attempts to return the field value of the field named <i>strField</i>
  * as a double.
  *
  * @param strField the name of the field whose value should be
  * returned.
  */
  public double getDoubleFieldValue(String strField) {
    return ((Double)getFieldValueAs(strField, Double.class)).doubleValue();
  }

  /** Attempts to return the field value of the field with an index of
  * <i>idx</i> as a double.
  *
  * @param idx the index of the field whose value should be
  * returned.
  */
  public double getDoubleFieldValue(int idx) {
    return ((Double)getFieldValueAs(idx, Double.class)).doubleValue();
  }
  
  /** Attempts to return the value associated with the field <i>fieldDef</i>
   * as a boolean.
   *
   * @param fieldDef the FieldDefinition specifying the field whose value
   * should be returned.
   */
  public boolean getBooleanFieldValue(FieldDefinition fieldDef) {
    return ((Boolean)getFieldValueAs(fieldDef, Boolean.class)).booleanValue();
  }
  
  /** Attempts to return the field value of the field named <i>strField</i>
  * as a boolean.
  *
  * @param strField the name of the field whose value should be returned.
  */
  public boolean getBooleanFieldValue(String strField) {
    return ((Boolean)getFieldValueAs(strField, Boolean.class)).booleanValue();
  }

  /** Attempts to return the field value of the field with the index of
  * <i>idx</i> as a boolean.
  *
  * @param idx the index of the field whose value should be returned.
  */
  public boolean getBooleanFieldValue(int idx) {
    return ((Boolean)getFieldValueAs(idx, Boolean.class)).booleanValue();
  }

  // date get routines
  /** Attempts to return the field value of the field <i>fieldDef</i>
  * as a Date.
  *
  * @param fieldDef the FieldDefinition for the field whose value should be
  * returned.
  */
  public Date getDateFieldValue(FieldDefinition fieldDef) {
    return((Date)getFieldValueAs(fieldDef, Date.class));
  }

  /** Attempts to return the field value of the field named <i>strField</i>
  * as a Date.
  *
  * @param strField the name of the field whose value should be
  * returned.
  */
  public Date getDateFieldValue(String strField) {
    return((Date)getFieldValueAs(strField, Date.class));
  }

  /** Attempts to return the field value of the field with an index of
  * <i>idx</i> as a Date.
  *
  * @param idx the index of the field whose value should be
  * returned.
  */
  public Date getDateFieldValue(int idx) {
    return((Date)getFieldValueAs(idx, Date.class));
  }

  /** write the field values associated with this field set into
   * the StringBuffer <i>strbuf</i> starting at the offset <i>idxOffset</i>.
   *
   * @returns the number of records written.
   */
  public int writeFields(StringBuffer strbuf, int idxOffset) {
    Enumeration enumKey = m_hashValues.keys();

    int iWritten = 0;

    while (enumKey.hasMoreElements()) {
      FieldDefinition fdefCur = (FieldDefinition)enumKey.nextElement();
      fdefCur.writeToBuffer(strbuf, idxOffset, getFieldValue(fdefCur));
      iWritten++;
    }

    return(iWritten);
  }

  /** parse the field values associated with this field set from
   * the String <i>strValues</i> starting at the offset <i>idxOffset</i>.
   *
   * @returns the number of records written.
   */
  public int parseFields(String strValues, int idxOffset) {
    RecordDef rdef = getRecordDef();
    int idxMax = rdef.getFieldCount();
    int idxNonNull = 0;
    
    for (int i = 0; i < idxMax; i++) {
      FieldDefinition fdef=getFieldDefByIndex(i);

      // only bother to set non-null values

      Object objResult = fdef.parseFromString(strValues, idxOffset);

      if (null != objResult) {
        setFieldValue(fdef, objResult);
        idxNonNull++;
      }
    }

    return(idxNonNull);
  }

  //-------------------------------------
  /**
   * Dump the field definitions for the fields to System.out. Used
   * for debugging.
   */
  public void dumpFieldDefs() {
    getRecordDef().dumpFieldDefs();
  }

  /**
   * Dump the contents of the fields to System.out. Used
   * for debugging.
   */
  public void dumpFields() {
    Enumeration enumKey = m_hashValues.keys();

    while (enumKey.hasMoreElements()) {
      FieldDefinition fdef = (FieldDefinition)enumKey.nextElement();
      
      Object[] args = {fdef.getName(),
                       getFieldValue(fdef)};

      System.out.println(SalesTaxService.msg.format("TXWRFieldSetDumpFields", 
                                                             args));
    }
  }
}
