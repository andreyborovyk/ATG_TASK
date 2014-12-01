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
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * <p>A FieldDefinition defines one field, defining its name, length, and
 *    offset from the beginning of the record containing it. A
 *    FieldDefinition also defines the FieldType for the field, which
 *    determines how it is parsed, validated, and written.
 *
 * @see FieldType
 * @author cmore
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/FieldDefinition.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

class FieldDefinition {
  //-------------------------------------
  // Class version string

  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/FieldDefinition.java#2 $$Change: 651448 $";

  /** integer type */
  public static final FieldType INT_FTYPE = new IntegerFieldType();

  /** string type */
  public static final FieldType STR_FTYPE = new StringFieldType();

  /** boolean (' ' == false, '1' == true type */
  public static final FieldType BOOL_SPACE_OR_ONE_FTYPE = new BooleanFieldType(" ", "1");
  
  /** boolean ('0' == false, '1' == true type */
  public static final FieldType BOOL_ZERO_OR_ONE_FTYPE = new BooleanFieldType("0", "1");

  /** boolean (' ' == false, '*' == true type */
  public static final FieldType BOOL_SPACE_OR_ASK_FTYPE = new BooleanFieldType(" ", "*");
  
  /** Date (YYYYMMDD) type */
  public static final FieldType DATE_FTYPE = new DateFieldType();

  /** SMTXAMT_TYPE (2 digits to the right of the decimal) --
   * instance of the DoubleFieldType. */
  public static final FieldType SMTXAMT_FTYPE = new DoubleFieldType(2);

  /** NEWRATE_TYPE (6 digits to the right of the decimal) --
   * instance of the DoubleFieldType. */
  public static final FieldType NEWRATE_FTYPE = new DoubleFieldType(6);
  
  /** BASISPERC_TYPE (7 digits to the right of the decimal) --
   * instance of the DoubleFieldType. */
  public static final FieldType BASISPERC_FTYPE = new DoubleFieldType(7);
  

  protected int m_offset;       // field offset into current record
  protected int m_length;       // field length
  protected FieldType m_type;         // field type
  protected String m_strName;

  //-------------------------------------
  /**
   * Create a new field definition.
   */
  public FieldDefinition(String strName, int offset,
                         int length, FieldType type) {
    m_strName = strName;
    m_offset = offset;
    m_length = length;
    m_type = type;
  }

  /** DoubleFieldType represents a double. TaxWare represents doubles as
   * left zero-padded integers with some number of digits intepreted as
   * being to the right of the decimal.
   *
   * The DoubleFieldType actually stores it's values as longs. If a long
   * value is request, the raw, unshifted integer-value will be returned. If
   * a double value is requested, the the value returned reflects the
   * place-corrected value.
   */
  static class DoubleFieldType extends IntegerFieldType {
    double m_dMult;       // number of digits to leftshift

    protected final long getLongedValue(Double dValue) {
      return((long)(dValue.doubleValue() * m_dMult));
    }
    
    DoubleFieldType(int pLeftShipPlaces) {
      m_dMult = Math.pow(10, pLeftShipPlaces);
    }

    public void validate(String strName, Object objValue, int length) {
      if (objValue instanceof Double) {
        long lValue = getLongedValue((Double)objValue);
        
        if (lValue < 0)
          throw new IllegalArgumentException("Negative integer for " +
                                             strName); 
      }
      else
        super.validate(strName, objValue, length);
    }

    public void setInBuffer(StringBuffer strbuf, int offset,
                            int length, Object objValue) {
      if (objValue instanceof Double) {
        long lValue = getLongedValue((Double)objValue);
        writeLongToBuffer(strbuf, lValue, offset, length);
      } else {
        super.setInBuffer(strbuf, offset, length, objValue);
      }
    }

    public Object convertToClass(String strFieldName, Object objOrig,
                                 Class classTarget) {
      if (classTarget == Double.class) {
        if (!(objOrig instanceof Long)) {
          Object[] args = {objOrig, strFieldName, classTarget};

          throw new ClassCastException(SalesTaxService.msg.format(
                                       "TXWRFieldDefCannotConvert", args));
        }

        double dValue = (((Long)objOrig).longValue() + 0.0) / m_dMult;
        return(new Double(dValue));
      } else {
        return(super.convertToClass(strFieldName, objOrig, classTarget));
      }
    }
  }

  /**
   * BaseFieldType is used so that all the FieldDefinitions can
   * inherent an (almost) do-nothing definiton of BaseFieldType
   */
  static class BaseFieldType {
    public Object convertToClass(String strFieldName, Object objOrig,
                                 Class classTarget) {
      if (classTarget.isInstance(objOrig)) {
        return(objOrig);
      } if (classTarget == String.class) {
        return(objOrig.toString());
      }

      Object[] args = {objOrig, strFieldName, classTarget};

      throw new ClassCastException(SalesTaxService.msg.format("TXWRFieldDefClassCannotConvert", 
                                                          args));
    }
  }
  

  /** IntegerFieldType represents a positive integer, left padded with zeros
   * for parsing and rendering. IntegerFieldType accept a Long or an Int
   * object value, and parses to a Long.
   */
  static class IntegerFieldType extends BaseFieldType implements FieldType {
    public void validate(String strName, Object objValue, int length) {
      // not worrying about the length for the moment
      long lValue;
      if (objValue instanceof Integer)
        lValue = ((Integer)objValue).longValue();
      else
        lValue = ((Long)objValue).longValue();

      if (lValue < 0)
        throw new IllegalArgumentException(SalesTaxService.msg.format(
                                           "TXWRIllegalArgument", strName));
    }

    public void setInBuffer(StringBuffer strbuf, int offset,
                            int length, Object objValue) {
      long lValue;
      if (objValue instanceof Integer)
        lValue = ((Integer)objValue).longValue();
      else
        lValue = ((Long)objValue).longValue();

      writeLongToBuffer(strbuf, lValue, offset, length);
    }

    public Object parseFromString(String strFieldName, String strValueOrig) {
      String strValue = strValueOrig.trim();

      if (0 == strValue.length()) {
        return(null);
      }
      else {
        if (Character.isSpaceChar(strValueOrig.charAt(0))) {
          System.out.println(SalesTaxService.msg.format(
                             "TXWRFieldDefNumberBeginSpace", strValueOrig));
        }

        return(new Long(Long.parseLong(strValue)));
      }
    }

    public Object convertToClass(String strFieldName, Object objOrig,
                                 Class classTarget) {
      // if we get here, we should have to worry about it already
      // being the right class

      // try to be nice about being flexible about integer/long
      // values
      if (classTarget == Integer.class) {
        if (objOrig instanceof Long) {
          long lValue = ((Long)objOrig).longValue();
          if ((lValue <= Integer.MAX_VALUE) &&
              (lValue >= Integer.MIN_VALUE)) {
            return(new Integer((int)lValue));
          }
        } 
      } else if (classTarget == Long.class) {
        if (objOrig instanceof Integer) {
          long lValue = ((Integer)objOrig).longValue();
          return(new Long(lValue));
        }
      }

      // if we fall through, just pass to the default implementation
      // (and likely throw an error)
      return(super.convertToClass(strFieldName, objOrig, classTarget));
    }
  }

  /** StringFieldType represents a string, right padded with spaces for
   * parsing and rendering. StringFieldType parses to and expects values of
   * class String.
   */
  static class StringFieldType extends BaseFieldType implements FieldType {
    public void validate(String strName, Object objValue, int length) {

      String strValue = (String)objValue;

      if (strValue != null) {
        if (strValue.length() > length) {
          Object[] args = {strValue, strName};
          throw new IllegalArgumentException(SalesTaxService.msg.format(
                                             "TXWRValueTooLong", args));
        }
      }
    }

    public void setInBuffer(StringBuffer strbuf, int offset,
                            int length, Object objValue) {
      String strValue = (String) objValue;
      
      writeStringToBuffer(strbuf, strValue, offset, length);
    }

    public Object parseFromString(String strFieldName, String strValueOrig) {
      String strValue = strValueOrig.trim();
      if (strValue.length() == 0)
        return(null);

      if (Character.isSpaceChar(strValueOrig.charAt(0))) {
        System.out.println(SalesTaxService.msg.format(
                           "TXWRFieldDefStringBeginSpace", strValueOrig));
      }
      return(strValue);
    }
  }
  
  /** BooleanFieldType represents a boolean value, which specifies the
   * string (typically one character long) used for rendering and
   * parsing. BooleanFieldType parses to and expects values of class
   * Boolean.
   */
  static class BooleanFieldType extends BaseFieldType implements FieldType {
    protected String m_strFalse;
    protected String m_strTrue;

    BooleanFieldType(String strFalse, String strTrue) {
      m_strTrue = strTrue;
      m_strFalse = strFalse;
    }
    
    public void validate(String strName, Object objValue, int length) {
      // just make sure it's a Boolean
      boolean bValue = ((Boolean)objValue).booleanValue();
    }

    public void setInBuffer(StringBuffer strbuf, int offset,
                            int length, Object objValue) {
      boolean bValue = ((Boolean)objValue).booleanValue();

      writeStringToBuffer(strbuf, bValue ? m_strTrue : m_strFalse,
                          offset, length);
    }

    public Object parseFromString(String strFieldName, String strValue) {

      boolean bValue = false;
      if (m_strTrue.equals(strValue)) {
        bValue = true;
      }
      else if (m_strFalse.equals(strValue)) {
        bValue = false;
      }
      else {
        throw new IllegalArgumentException(SalesTaxService.msg.format(
                                           "TXWRNoBoolean", strFieldName));
      }

      return(Boolean.valueOf(bValue));
    }
    
  }


  /** DateFieldType represents a date value, which specifies the date in
   * "YYYYMMDD" format, although a Date object is used for the internal
   * representation.
   */
  static class DateFieldType extends BaseFieldType implements FieldType {
    public void validate(String strName, Object objValue, int length) {
      // not worrying about the length for the moment
      Date date = (Date)objValue;
      if (length != 8) {
        Object[] args = {strName, Integer.valueOf(length)};
        throw new IllegalArgumentException(SalesTaxService.msg.format(
                                           "TXWRBadDateLength", args));
      }
    }

    public void setInBuffer(StringBuffer strbuf, int offset,
                            int length, Object objValue) {
      if (8 != length) {
        Object[] args = {strbuf, Integer.valueOf(length)};  
        throw new IllegalArgumentException(SalesTaxService.msg.format(
                                           "TXWRBadDateLength", args));
      }
      
      Date dateValue = (Date)objValue;
      // set the date
      Calendar cal = new GregorianCalendar();
      cal.setTime(dateValue);

      int iYear = cal.get(Calendar.YEAR);
      int iMonth = cal.get(Calendar.MONTH);
      int iDay = cal.get(Calendar.DAY_OF_MONTH);
      
      // create YYYYMMDD 
      String strDate = zeroPad(iYear, 4) +
        zeroPad(iMonth + 1, 2) + // correct from zero-based month
        zeroPad(iDay, 2);

      writeStringToBuffer(strbuf, strDate, offset, length);
    }

    public Object parseFromString(String strFieldName, String strValue) {
      strValue = strValue.trim();

      if (strValue.length() == 0)
        return(null);

      if (strValue.length() != 8) {
        Object[] args = {strValue, strFieldName};
        throw new IllegalArgumentException(SalesTaxService.msg.format(
                                           "TXWRCannotParseDate", args));
      }

      String strYear = strValue.substring(0, 3);
      String strMonth = strValue.substring(4, 5);
      String strDay = strValue.substring(6, 7);

      int iYear = Integer.parseInt(strYear);
      
      // correct for zero-based month       
      int iMonth = Integer.parseInt(strMonth) - 1; 
      int iDay = Integer.parseInt(strDay);

      Calendar cal = new GregorianCalendar();
      cal.set(iYear, iMonth, iDay);

      return(cal.getTime());
    }
  }
  
  final void writeToBuffer(StringBuffer strbuf, int offset, Object objValue) {
    m_type.setInBuffer(strbuf, m_offset + offset, m_length, objValue);
  }

  final Object getValueAs(Object objValue, Class classTarget) {
    if (objValue == null)
      return(null);
    else if (classTarget.isInstance(objValue))
      return(objValue);
    
    return(m_type.convertToClass(m_strName, objValue, classTarget));
  }

  
  final Object parseFromString(String strValues, int offset) {
    
    String strValue = strValues.substring(m_offset + offset,
                                          m_offset + offset + m_length);
    
    return(m_type.parseFromString(m_strName, strValue));
  }

  //-------------------------------------
  /**
   * Utility function to get a string value, left-padded with zeros.
   */
  protected static final String zeroPad(int iValue, int cWidth) {
    String strValue = Integer.toString(iValue);

    if (strValue.length() > cWidth) {
      Object[] args = {Integer.valueOf(cWidth)};
      throw new IllegalArgumentException(SalesTaxService.msg.format(
                                         "TXWRIntegerTooBig", args));
    }

    else if (cWidth == strValue.length())
      return(strValue);

    StringBuffer strNew = new StringBuffer("0");
    int cZeros = cWidth - strValue.length();
    
    for (int i = 1; i < cZeros; i++)
      strNew.append("0");

    strNew.append(strValue);

    return(strNew.toString());
  }
  
  String getName() { return (m_strName); }
  int getOffset() { return (m_offset); }
  int getLength() { return (m_length); }
  FieldType getType() { return (m_type); }


  public static void writeStringToBuffer(StringBuffer strbuf,
                                         String strValue,
                                         int cPos, int cLength) {
    writeFieldToBuffer(strbuf, strValue,
                       cPos, cLength, false);
  }

  //----------------------
  // utility methods
  protected static final void writeIntToBuffer(StringBuffer strbuf, int iValue,
                                      int cPos, int cLength) {
    writeFieldToBuffer(strbuf, String.valueOf(iValue),
                       cPos, cLength, true);
  }

  
  protected static final void writeLongToBuffer(StringBuffer strbuf,
                                                long lValue,
                                                int cPos, int cLength)  {
    writeFieldToBuffer(strbuf, String.valueOf(lValue),
                       cPos, cLength, true);
  }


  static char m_rgcharZeroes[] = { '0' };
  
  private static final void addZeroes(StringBuffer strbuf, int cZeroes) {
    if (m_rgcharZeroes.length < cZeroes) {
      m_rgcharZeroes = new char[cZeroes];

      for (int i = 0; i < cZeroes; i++) {
        m_rgcharZeroes[i] = '0';
      }
    }

    strbuf.append(m_rgcharZeroes, 0, cZeroes);
  }
  

  /** write the value strValue out to the StringBuffer
   * at the specified position. Note that strbuf
   * should be pre-filled with spaces. */
  public static final void writeFieldToBuffer(StringBuffer strbuf,
                                              String strValue,
                                              int cPos, int cLength,
                                              boolean bNumericPad) {
    if (strValue.length() > cLength) {
      throw new IllegalArgumentException(SalesTaxService.msg.format(
                                         "TXWRFieldValueTooLong", strValue));
    } else if (bNumericPad && (strValue.length() < cLength)) {

      StringBuffer strbufNew = new StringBuffer(cLength);

      int cPad = cLength - strValue.length();

      int idxNew = 0;

      addZeroes(strbufNew, cPad);

      strbufNew.append(strValue);

      strValue = strbufNew.toString();
    }

    int idxDestination = cPos;

    for (int i = 0; i < strValue.length(); i++) {
      strbuf.setCharAt(idxDestination++, strValue.charAt(i));
    }
  }
}
