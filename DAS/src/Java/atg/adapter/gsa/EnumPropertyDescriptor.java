/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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
 * Dynamo is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.adapter.gsa;

import atg.beans.TaggedPropertyEditor;
import atg.core.jdbc.ResultSetGetter;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This is a special property descriptor used for enumerated property types.
 * It can represent an enumerated value either that represents its value
 * as an integer code or a string value.
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/EnumPropertyDescriptor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class EnumPropertyDescriptor extends GSAPropertyDescriptor {

  private static final long serialVersionUID = -5187612562121723738L;

  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/EnumPropertyDescriptor.java#2 $$Change: 651448 $";

  //-----------------------------------
  /**
   * The attribute name we use for setting whether our value is
   * exposed as either the code value, or the string underneath the code.
   */
  static final String USE_CODE_FOR_VALUE = "useCodeForValue";

  //-------------------------------------
  /**
   * Empty constructor
   */
  public EnumPropertyDescriptor()
  {
    super();
  }

  //-------------------------------------
  /**
   * Returns true if the given property value is a valid value for
   * this property, false otherwise.  Uses the super classes implementation
   * to verify that the value matches the property type, then if we have
   * an item type, we must make sure that the value is of the correct type.
   **/
  public boolean isValidValue(Object pPropertyValue) {
    if (super.isValidValue(pPropertyValue)) {
      if (mUseCodeForValue)
        return isValidEnumeratedCode(pPropertyValue);
      return isValidEnumeratedValue(pPropertyValue);
    }
    return false;
  }

  /**
   * Override this method to do enumerated String to integer conversion
   */
  public void setDefaultValueString(String pDefaultValueString)
    throws IllegalArgumentException
  {
    if (mHasResources) {
      for (int i = 0; i < mEnumResources.length; i++) {
        if (mEnumResources[i] != null &&
            mEnumResources[i].equals(pDefaultValueString)) {
          if (mUseCodeForValue)  setDefaultValue(mEnumCodes[i]);
          else setDefaultValue(mEnumValues[i]);
          return;
        }
      }
    }
    super.setDefaultValueString(pDefaultValueString);
    if (mUseCodeForValue)
      setDefaultValue(convertValueToCode(pDefaultValueString));
  }

  //---------------------------------
  public Class getPropertyType() {
    return mUseCodeForValue ? Integer.class : String.class;
  }

  //---------------------------------
  // Enumerated data type stuff

  Map mEnumValueToCode;
  Map mEnumCodeToValue;
  String [] mEnumValues = new String[0];
  Integer [] mEnumCodes = new Integer[0];
  String [] mEnumResources = new String[0];

  /** The enumerated options array. This is created on demand. */
  EnumeratedOption[] mEnumOptions;

  boolean isValidEnumeratedValue(Object pValue) {
    /* null/required test is already done in the base class */
    if (pValue == null) return true;

    /* No valid options specified - all are invalid */
    if (mEnumValueToCode == null) return false;

    return mEnumValueToCode.get(pValue) != null;
  }

  boolean isValidEnumeratedCode(Object pCode) {
    /* null/required test is already done in the base class */
    if (pCode == null) return true;

    /* No valid options specified - all are invalid */
    if (mEnumCodeToValue == null) return false;

    return mEnumCodeToValue.get(pCode) != null;
  }

  public Integer convertValueToCode(Object pValue) throws IllegalArgumentException {
    Integer code;
    if (pValue == null) return null;

    // check if we have a map of enum values to codes,
    // e.g. "hockey" -> 1, "soccer" -> 2
    if (mEnumValueToCode == null)
       throw new IllegalArgumentException("invalid enum value: " + pValue + " for property: " + getName());

    // look up code using this enum value
    code = (Integer) mEnumValueToCode.get(pValue);
    if ( null == code ) {

      // if we did not find a code check to see if this enum value is
      // actually a code itself (bug 71327-1)
      if (!(pValue instanceof String))
        throw new IllegalArgumentException("invalid enum value: " + pValue + " for property: " + getName());

      try {
        code = new Integer((String) pValue);
      }
      catch (NumberFormatException exc) {
        // code is not an integer
        throw new IllegalArgumentException("invalid enum value: " + pValue + " for property: " + getName());
      }

      // check if code is in map
      if (!mEnumValueToCode.containsValue(code)) {
        throw new IllegalArgumentException("invalid enum value: " + pValue + " for property: " + getName());
      }
    }

    // success
    return code;
  }

  public String convertCodeToValue(Integer pCode) {
    if (mEnumCodeToValue == null) return null;
    return (String) mEnumCodeToValue.get(pCode);
  }

  /**
   * Keep track if we use any resources for our values - if so, the default
   * value can be specified as a resource in addition to using a value
   */
  boolean mHasResources = false;

  public synchronized void addEnumeratedOption(String pValue, int pCode,
                                               String pResource) {

    if (pResource != null) mHasResources = true;
    if (pResource != null && pValue == null) {

      ResourceBundle b = getResourceBundle();
      if (b == null)
        throw new
          IllegalArgumentException("No resource bundle found for property: " +
                                   getName() +
                                   " and resource " + pResource +
                                   " bundle name is " +
                                   getResourceBundleName());
      pValue = getResourceBundle().getString(pResource);
    }

    if (mEnumCodeToValue == null) {
      mEnumCodeToValue = new HashMap(16);
      mEnumValueToCode = new HashMap(16);
    }
    Integer code = Integer.valueOf(pCode);
    Integer oldCode = (Integer) mEnumValueToCode.put(pValue, code);
    if (oldCode != null && !code.equals(oldCode))
      throw new IllegalArgumentException("Conflicting codes for value: " + pValue + " new: " + code + " old: " + oldCode);
    String oldValue = (String) mEnumCodeToValue.put(code, pValue);
    if (oldValue != null && !oldValue.equals(pValue))
      throw new IllegalArgumentException("Conflicting values for code: " + code + " new value=" + pValue + " old value=" + oldValue);
    String [] values = new String[mEnumValues.length+1];
    String [] resources = new String[mEnumValues.length+1];
    Integer [] codes = new Integer[mEnumCodes.length+1];
    int i;
    for (i = 0; i < mEnumValues.length; i++) {
      values[i] = mEnumValues[i];
      codes[i] = mEnumCodes[i];
      resources[i] = mEnumResources[i];
    }
    values[i] = pValue;
    codes[i] = Integer.valueOf(pCode);
    resources[i] = pResource;

    mEnumValues = values;
    mEnumCodes = codes;
    mEnumResources = resources;
    mEnumOptions = null;
  }
  

  public String [] getEnumeratedValues() {
    return mEnumValues;
  }

  public Integer[] getEnumeratedCodes() {
    return mEnumCodes;
  }
  
  public String [] getEnumeratedResources() {
    return mEnumResources;
  }

  /** Return the enumerated options as an array of objects. */
  public synchronized EnumeratedOption[] getEnumeratedOptions() {
    if (mEnumOptions == null) {
      EnumeratedOption[] options = new EnumeratedOption[
        mEnumValues.length];
      for (int i = 0; i < options.length; i++) {
        options[i] = new EnumeratedOption(
          mEnumValues[i], mEnumCodes[i],  mEnumResources[i]);
      }
      mEnumOptions = options;
    }
    return mEnumOptions;
  }

  /**
   * Returns a new PropertyEditor instance that can edit this property.
   *
   * <P> For dynamic properties this approach is preferable to
   * getPropertyEditorClass(), the corresponding method in
   * java.beans.PropertyDescriptor, since the conversion methods and
   * allowed values of dynamic properties may need to be determined at
   * runtime.
   *
   * <P>This default implementation simply uses PropertyEditorManager
   * to create a PropertyEditor based on the type.  Subclass
   * DynamicPropertyDescriptor if the PropertyEditor depends on other
   * information at runtime.
   **/
  public PropertyEditor createPropertyEditor() {
    /*
     * If we supply the EnumValues for both, we end up not doing any
     * translation in the TaggedPropertyEditor.
     */
    return new TaggedPropertyEditor(mEnumValues,
                                    mUseCodeForValue ? (Object[])mEnumCodes :
                                                       (Object[])mEnumValues);
  }

  /**
   * Returns a new localized PropertyEditor instance that can edit this property.
   *
   * <P> This differs from the above method in that the
   * locale-specific editor can vary according to the given locale.
   *
   * @param the Locale for which the localized editor should be
   * created, or null for the default locale.
   **/
  public PropertyEditor createLocalePropertyEditor(Locale pLocale) {
    /* We should not be localizing enum values? */
    return createPropertyEditor();
  }

  //-------------------------------------
  /**
   * This is a property editor which always converts values to/from the
   * database version of the property.
   */
  public PropertyEditor createDBPropertyEditor() {
    return new TaggedPropertyEditor(mEnumValues, mEnumCodes);
  }

  //-------------------------------------
  /**
   * Translate a raw property value to a real value. The real value is what
   * applications use. The raw value is what is stored in the DB. For enums
   * this coverts the integer code to the String. For item properties this
   * converts the item id to the actual item. For all other kinds of
   * properties the original value is returned.
   * @param pRawValue for a property
   * @return real value to use in applications for the property
   **/
  public Object rawToReal(Object pRawValue)
  {
    if (mUseCodeForValue) return pRawValue;
    if (pRawValue instanceof Integer)
      return convertCodeToValue((Integer)pRawValue);
    /*
     * This case may not be necessary anymore since I fixed a problem
     * with the getter.
     */
    else if (pRawValue instanceof String)
      return convertCodeToValue(new Integer((String) pRawValue));
    else
      return pRawValue; /* Should not happen? */
  }

  //-------------------------------------
  /**
   * Translate a property value to a raw value. The real value is what
   * applications use. The raw value is what is stored in the DB. For enums
   * this coverts the String to the integer code.
   * @param pRealValue for a property
   * @return raw value for storing this property in the DB
   **/
  public Object realToRaw(Object pRealValue)
  {
    if (pRealValue == null || pRealValue == GSAItem.NULL_OBJECT)
      return null;

    // convert the real value if it is a string. the raw value is always an
    // int, independent of the setting for useCodeForValue. for this reason
    // we don't consult that property here.
    if (pRealValue instanceof String)
      return convertValueToCode(pRealValue);
    else
      return pRealValue;
  }

  //--------- Property: UseCodeForValue -----------
  // property: UseCodeForValue
  boolean mUseCodeForValue = true;
  /**
   * Sets the property UseCodeForValue.
   *
   * @beaninfo description:
   * @param pUseCodeForValue new value to set
   */
  public void setUseCodeForValue(boolean pUseCodeForValue) {
    mUseCodeForValue = pUseCodeForValue;
  }
  /**
   * @return The value of the property UseCodeForValue.
   */
  public boolean getUseCodeForValue() {
    return mUseCodeForValue;
  }

  /**
   * Catch the attribute values that we care about and store them in
   * member variables.
   */
  public void setValue(String pAttributeName, Object pValue) {
    super.setValue(pAttributeName, pValue);

    if (pValue == null || pAttributeName == null) return;
    if (pAttributeName.equalsIgnoreCase(USE_CODE_FOR_VALUE))
      setUseCodeForValue(pValue.toString().equalsIgnoreCase("true"));
  }

  //---------- Property: (read-only) resultSetGetter ----------
  /**
   * Get property <code>resultSetGetters</code>. Derived property
   * for now.
   *
   * @beaninfo description: Getter to use for reading this property
   * from an SQL ResultSet
   * @return <code>resultSetGetter</code>
   **/
  public ResultSetGetter[] getResultSetGetters()
  {
    /*
     * We always store the values in the database as integers
     * regardless of the type
     */
    ResultSetGetter[] getters = new ResultSetGetter[1];
    getters[0] = ResultSetGetter.getGetter(Integer.class);
    return getters;
  }

  //-------------------------------------
  /**
   * We want to choose the default read object class
   */
  private void readObject(ObjectInputStream pStream)  throws IOException, ClassNotFoundException {
    pStream.defaultReadObject();
    ResourceBundle b = getResourceBundle();
    if (b != null) {
      for (int i = 0; i < mEnumResources.length; i++) {
        if (mEnumResources[i] != null) {
          try {
            mEnumValues[i] = b.getString(mEnumResources[i]);
          }
          catch (MissingResourceException exc) {}
        }
      }
      /*
       * Do this again so that we can get the default value localized
       * for this locale (in case the locale changed between when we
       * serialized and de-serialized the descriptor)
       */
      if (mHasResources && !mUseCodeForValue && getDefaultValueString() != null)
        setDefaultValueString(getDefaultValueString());
    }
  }

  //-------------------------------------------------------
  /** The object representing single enumeration option. */
  public class EnumeratedOption {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/EnumPropertyDescriptor.java#2 $$Change: 651448 $";

    String mValue;
    int mCode;
    String mResource;

    /** Create a new enumeration option object. */
    public EnumeratedOption(String pValue, int pCode, String pResource) {
      mValue = pValue;
      mCode = pCode;
      mResource = pResource;
    }

    /** Get the string value that represents this option. */
    public String getValue() {
      return mValue;
    }

    /** Get the integer code that represents this option. */
    public int getCode() {
      return mCode;
    }

    /** Get the resource for this option. */
    public String getResource() {
      return mResource;
    }

    /** Whether we are using code for value. */
    public boolean getUseCodeForValue() {
      return mUseCodeForValue;
    }

    /** Return the settable value. If useCodeForValue
     * is true, return the code, otherwise return the value. */
    public Object getSettableValue() {
      if (getUseCodeForValue()) {
        return getCode();
      }
      return getValue();
    }
  } // end inner-class EnumeratedOption

}
