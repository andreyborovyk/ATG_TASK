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

import atg.repository.RepositoryItemImpl;

import java.util.Date;

/**
 * This is a special property descriptor used for date property types.
 * It supports some special data type conversion for date properties and
 * allows you to specify "now" as the default value using the special
 * useNowForDefault feature descriptor attribute.
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/DatePropertyDescriptor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class DatePropertyDescriptor extends GSAPropertyDescriptor {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/adapter/gsa/DatePropertyDescriptor.java#2 $$Change: 651448 $";

  //-----------------------------------
  /**
   * The attribute name we use for setting whether our value is
   * exposed as either the code value, or the string underneath the code.
   */
  static final String USE_NOW_FOR_DEFAULT = "useNowForDefault";

  //-------------------------------------
  /**
   * Empty constructor
   */
  public DatePropertyDescriptor()
  {
    super();
  }

  //-------------------------------------
  /**
   * We accept dates both in the canonical form of java.sql.Date and
   * also the special form java.util.Date.
   **/
  public boolean isValidValue(Object pPropertyValue) {
    if (super.isValidValue(pPropertyValue)) {
      return true;
    }
    else if (pPropertyValue instanceof java.util.Date)
      return true;
    return false;
  }

  /**
   * Make sure we have not specified conflicting definitions for the default
   * value for this property.
   */
  public void setDefaultValueString(String pDefaultValueString)
    throws IllegalArgumentException
  {
    super.setDefaultValueString(pDefaultValueString);
    if (mUseNowForDefault && pDefaultValueString != null &&
        !pDefaultValueString.equals(""))
      throw new IllegalArgumentException("Cannot specify default value when useNowForDefault is set to true");
  }

  /**
   * Sets property DefaultValue
   **/
  public void setDefaultValue(Object pDefaultValue) {
    super.setDefaultValue(convertPropertyValue(pDefaultValue));
  }

  //-------------------------------------
  /**
   * Translate a property value to a raw value. The real value is what
   * applications use. The raw value is what is stored in the DB.  Here
   * we just need to convert java.util.Date to the underlying java.sql.Date
   * if we don't have one already.
   * @param pRealValue for a property
   * @return raw value for storing this property in the DB
   **/
  public Object realToRaw(Object pRealValue)
  {
    pRealValue = super.realToRaw(pRealValue);

    // Don't continue if we don't have a java.util.Date
    if(!(pRealValue instanceof java.util.Date))
      return pRealValue;

    // We have to cast anyway...might as well do it now and make the
    // code look less cluttered
    java.util.Date date = (java.util.Date)pRealValue;
    if (getDataType() == DATE) {
      if(pRealValue instanceof java.sql.Date) return pRealValue;
      return new java.sql.Date(date.getTime());
    }
    if (getDataType() == TIMESTAMP) {
      if(pRealValue instanceof java.sql.Timestamp) return pRealValue;
      return new java.sql.Timestamp(date.getTime());
    }

    // In this case, we have an array
    Class elementType = getComponentPropertyType();
    // Or not...just do what the old code did
    if(elementType == null) {
      // If we were given a timestamp, just return it
      if(pRealValue instanceof java.sql.Timestamp) return pRealValue;
      // Otherwise, we don't know what the type is supposed to be,
      // all we know is we've got a java.util.Date passed to use
      return new java.sql.Date(date.getTime());
    }

    // If the component type is a timestamp...
    if(java.sql.Timestamp.class.isAssignableFrom(elementType)) {
      // Just return it if it's already in that form
      if(pRealValue instanceof java.sql.Timestamp) return pRealValue;
      return new java.sql.Timestamp(date.getTime());
    }
    // Otherwise, we assume it's a date...and if it's not, we shouldn't
    // have been passed in a java.util.Date in the first place
    return new java.sql.Date(date.getTime());
  }

  //-------------------------------------
  /**
   * Here we need to convert from the java.util.Date to the correct internal
   * form.  Otherwise we might end up returning a cached value of the wrong
   * type which would be confusing to various tools (such as the scenario
   * engine).
   */
  public void setPropertyValue(RepositoryItemImpl pItem, Object pValue) {
    super.setPropertyValue(pItem, convertPropertyValue(pValue));
  }


  /**
   * Handles auto-conversion of java.util.Date to the appropriate sql
   * type for this property
   */
  Object convertPropertyValue(Object pValue) {
    int type = getDataType();
    if (type == DATE) {
      if (!(pValue instanceof java.sql.Date) && 
          pValue instanceof java.util.Date)
        pValue = new java.sql.Date(((java.util.Date)pValue).getTime());
    }
    else if (type == TIMESTAMP) {
      if (!(pValue instanceof java.sql.Timestamp) && 
            pValue instanceof java.util.Date)
        pValue = new java.sql.Timestamp(((java.util.Date)pValue).getTime());
    }
    return pValue;
  }

  /**
   * Returns property DefaultValue
   **/
  public Object getDefaultValue() {
    int type = getDataType();
    if (mUseNowForDefault) {
      if (type == DATE)
        return new java.sql.Date(System.currentTimeMillis());
      else if (type == TIMESTAMP)
        return new java.sql.Timestamp(System.currentTimeMillis());

      return new Date();
    }
    return super.getDefaultValue();
  }

  //--------- Property: UseNowForDefault -----------
  // property: UseNowForDefault
  boolean mUseNowForDefault = false;
  /**
   * Sets the property UseNowForDefault.
   *
   * @beaninfo description:
   * @param pUseNowForDefault new value to set
   */
  public void setUseNowForDefault(boolean pUseNowForDefault) {
    mUseNowForDefault = pUseNowForDefault;
  }
  /**
   * @return The value of the property UseNowForDefault.
   */
  public boolean getUseNowForDefault() {
    return mUseNowForDefault;
  }


  /**
   * Catch the attribute values that we care about and store them in
   * member variables.
   */
  public void setValue(String pAttributeName, Object pValue) {
    super.setValue(pAttributeName, pValue);

    if (pValue == null || pAttributeName == null) return;
    if (pAttributeName.equalsIgnoreCase(USE_NOW_FOR_DEFAULT))
      setUseNowForDefault(pValue.toString().equalsIgnoreCase("true"));
  }


  //--------- Property: DataType -----------
  // property: DataType

  /**
   * @return The value of the property DataType.
   *  DatePropertyDescriptor will never be a composite
   */
  public int getDataType() {
    if (getDataTypes() != null)
      return getDataTypes()[0];
    else
      return INVALID_DATA_TYPE;
  }
}
