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
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/
package atg.droplet;

import java.util.*;
import java.text.*;

import atg.servlet.DynamoHttpServletRequest;
import atg.core.util.ResourceUtils;

/**
 * Performs conversion to and from Number objects in jhtml tags.
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/NumberTagConverter.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class NumberTagConverter implements TagConverter {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/NumberTagConverter.java#2 $$Change: 651448 $";

  static final String NAME = "Number";
  static final String NUMBER_ATTRIBUTE = "number";
  static final String LOCALE_ATTRIBUTE = "locale";
  static final String MY_RESOURCE_NAME = "atg.droplet.DropletResources";
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME);

  /*
   * Caches number formatters for this class.  The key combines the number
   * format string with the locale name.
   */
  static Hashtable sNumberFormatCache = new Hashtable();

  private final static TagAttributeDescriptor[] sTagAttributeDescriptors = {
    new TagAttributeDescriptor(NUMBER_ATTRIBUTE,
        "A string that defines the format of the number (e.g. m/d/y)",
        false, true),
    new TagAttributeDescriptor(LOCALE_ATTRIBUTE,
      "The name of the locale to use (e.g. 'en_US', or just 'en') (if omitted, the default locale is used)",
        true, false),
    new TagAttributeDescriptor(NullableTagConverter.NULLABLE_ATTRIBUTE,
      "If this attribute is present, this value may be set to null",
        true, false),
    new TagAttributeDescriptor(RequiredTagConverter.REQUIRED_ATTRIBUTE,
      "If this attribute is present, this value is required",
        true, false)
  };

  public TagAttributeDescriptor [] getTagAttributeDescriptors() {
    return sTagAttributeDescriptors;
  }

  public String getName() {
    return NAME;
  }

  /**
   * Coverts a String which represents a number to a Number using
   * a NumberFormatter.
   *  @param pAttributes contains a <i>number</i> entry which is used
   * to specify the number format string.  An optional <i>locale</i>
   * attribute can be used to specify the locale to use to define the
   * number formatter.
   */
  public Object convertStringToObject(DynamoHttpServletRequest pReq,
                String pValue, Properties pAttributes)
     throws TagConversionException {
      
    boolean debugMode = false;
    if (pReq != null){
      debugMode = pReq.isLoggingDebug();
    }

    if(debugMode)
  pReq.logDebug("NumberTagConverter: attempting to convert " +
        pValue + " to a number");

    if (pValue != null) pValue = pValue.trim();

    if (pValue == null || pValue.length() == 0) {
      /* Should we set this property to null? */
      if (pAttributes.getProperty(NullableTagConverter.NULLABLE_ATTRIBUTE) != null) {
  return TagConverterManager.SET_AS_NULL;
      }
      /*
       * Do we also have the "required" attribute in here - if this is
       * an error
       */
      else if (pAttributes.getProperty(RequiredTagConverter.REQUIRED_ATTRIBUTE) == null) {
  if(debugMode)
      pReq.logDebug("NumberTagConverter: Value " + pValue
            + " is null or 0 length. Returning null");
        return null;
      }
      else {
        String msg = ResourceUtils.getMsgResource(
                        "missingRequiredValue", MY_RESOURCE_NAME, 
                        sResourceBundle);
        throw new TagConversionException(msg, "missingRequiredValue");
      }
    }

    NumberFormat nf = getNumberFormat(pReq, pAttributes);
    try {
      String t = pValue.trim();
      if (t.length() == 0){
  if(debugMode)
      pReq.logDebug("NumberTagConverter: Value " + pValue
            + " was able to be trimmed to 0 length. " +
            "Returning null");
  return null;
      }
      synchronized (nf) {
        Number n = nf.parse(pValue);
        if (n == null)
          throw new TagConversionException("Unable to parse number from string: " + pValue +
                                       " with format=" + pAttributes.get(NUMBER_ATTRIBUTE) +
                                       " and locale=" + pAttributes.get(LOCALE_ATTRIBUTE), "invalidNumber");
        return n;
      }
    }
    catch (ParseException exc) {
      throw new TagConversionException("Unable to parse number from string: " + pValue +
                                       " with format=" + pAttributes.get(NUMBER_ATTRIBUTE) +
                                       " and locale=" + pAttributes.get(LOCALE_ATTRIBUTE), exc, "invalidNumber");
    }

  }

  /**
   * Coverts a Number to a String using a number formatter pattern.
   */
  public String convertObjectToString(DynamoHttpServletRequest pReq,
                Object pValue, Properties pAttributes)
     throws TagConversionException {

    boolean debugMode = pReq.isLoggingDebug();

    NumberFormat nf = getNumberFormat(pReq, pAttributes);
    if (pValue == null){
      if (pAttributes.getProperty(NullableTagConverter.NULLABLE_ATTRIBUTE) != null) {
  return "";
      }
      if(debugMode)
    pReq.logDebug("NumberTagConverter: Attempting to convert null" +
          " Object to String. Returning null");
      return null;
    }
    if (pValue instanceof Number) {
      if(debugMode)
    pReq.logDebug("NumberTagConverter: Attempting to convert " +
          "Number" + pValue.toString() +
          " to a String");
      Number n = (Number) pValue;
      synchronized (nf) {
        return nf.format(n);
      }
    }
    /*
     * If it is already a String, the user may be trying to re-format the
     * existing value.  If so, let's run it through the ringer here and
     * get a value that has been formatted.
     */
    else if (pValue instanceof String)
      return convertObjectToString(pReq, convertStringToObject(pReq, (String)pValue, pAttributes), pAttributes);
    throw new TagConversionException("Cannot convert: " + pValue + " into a Number", "invalidNumber");
  }

  /**
   * Returns a number format to use for this set of attributes.  Caching
   * is performed.  We use the request object to obtain the locale to
   * use for the number formatter.
   */
  NumberFormat getNumberFormat(DynamoHttpServletRequest pReq, Properties pAttributes)
      throws TagConversionException {

    String numberPattern = pAttributes.getProperty(NUMBER_ATTRIBUTE);

    String localeName = pAttributes.getProperty(LOCALE_ATTRIBUTE);

    //determine locale
    Locale locale = null;

    if (localeName == null) {
      if (pReq != null && pReq.getRequestLocale() != null) {
        locale = pReq.getRequestLocale().getLocale();
        localeName = locale.toString();
      }
    }
    else {
      int ix = localeName.indexOf('_');
      String language, country;
      if (ix == -1) {
        language = localeName;
        country = "";
      }
      else {
        language = localeName.substring(0,ix);
        country = localeName.substring(ix+1);
      }
      locale = new Locale( language, country );
    }

    // get numberPattern
    if (numberPattern == null || numberPattern.length() == 0) {
      ResourceBundle b;
      if (locale == null) {
        b = ResourceBundle.getBundle("atg.droplet.TagResources", atg.service.dynamo.LangLicense.getLicensedDefault());
      } else {
        b = ResourceBundle.getBundle("atg.droplet.TagResources", locale);
      }
      try {
        numberPattern = b.getString("defaultNumberFormat");
      }
      catch (MissingResourceException exc) {}
      if (numberPattern == null)
        throw new TagConversionException("Missing required 'number' attribute value for number converter");
    }

    // figure out key
    String key;
    if (locale == null) {
      key = numberPattern;
    } else {
      key = numberPattern + localeName;
    }

    // get NumberFormat based on key
    NumberFormat nf = (NumberFormat) sNumberFormatCache.get(key);
    if (nf == null) {
      if (locale == null) {
        nf = new DecimalFormat(numberPattern);
      } else {
        nf = new DecimalFormat(numberPattern, new DecimalFormatSymbols(locale));
      }
      sNumberFormatCache.put(key, nf);
    }

    return nf;
  }

}
