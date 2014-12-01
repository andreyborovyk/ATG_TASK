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
 * Performs conversion to and from Date objects in jhtml tags.
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/DateTagConverter.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class DateTagConverter implements TagConverter {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/DateTagConverter.java#2 $$Change: 651448 $";

  static final String NAME = "Date";
  static final String DATE_ATTRIBUTE = "date";
  static final String LOCALE_ATTRIBUTE = "locale";
  static final String MAXDATE_ATTRIBUTE = "maxdate";
  static final String MINDATE_ATTRIBUTE = "mindate";
  static final String LENIENT_ATTRIBUTE = "lenient";
  static final String MY_RESOURCE_NAME = "atg.droplet.DropletResources";
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME);

  /*
   * Caches date formatters for this class.  The key combines the date
   * format string with the locale name, the value is the DateFormat.
   */
  static Hashtable sDateFormatCache = new Hashtable();

  private final static TagAttributeDescriptor[] sTagAttributeDescriptors = {
    new TagAttributeDescriptor(DATE_ATTRIBUTE,
        "A string that defines the date format (e.g. M/d/y)",
        false, true),
    new TagAttributeDescriptor(LOCALE_ATTRIBUTE,
      "The name of the date locale to use (if omitted, the default locale is used)",
        true, false),
    new TagAttributeDescriptor(MAXDATE_ATTRIBUTE,
      "This defines the maximum date that can be entered into the field. The format must be in the same format as the 'date' attribute",
        false, true),
    new TagAttributeDescriptor(MINDATE_ATTRIBUTE,
      "This defines the minimum date that can be entered into the field. The format must be in the same format as the 'date' attribute",
        false, true),
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

  public Object convertStringToObject(DynamoHttpServletRequest pRequest,
                String pValue, Properties pAttributes)
     throws TagConversionException {

    boolean debugMode = pRequest != null && pRequest.isLoggingDebug();

    if(debugMode)
      pRequest.logDebug("DateTagConverter: attempting to convert " + pValue + " to a date");
    if (pValue != null) pValue = pValue.trim();

    if (pValue != null && pValue.startsWith("$now$")){
      if(debugMode)
        pRequest.logDebug("DateTagConverter: returning Date object for "
          + "current time");
      return new Date();
    }

    if (pValue == null || pValue.length() == 0) {
      /* Should we set this property to null? */
      if (pAttributes.getProperty(NullableTagConverter.NULLABLE_ATTRIBUTE) != null) {
        return TagConverterManager.SET_AS_NULL;
      }
      /*
       * Do we also have the "required" attribute in here - if this is
       * an error
       */
      if (pAttributes.getProperty(RequiredTagConverter.REQUIRED_ATTRIBUTE) == null) {
        if(debugMode)
          pRequest.logDebug("DateTagConverter: Value " + pValue + 
                            " is null or 0 length. Returning null");
        return null;
      }
      else {
        String msg = ResourceUtils.getMsgResource(
                        "missingRequiredValue", MY_RESOURCE_NAME, 
                        sResourceBundle);
        throw new TagConversionException(msg, "missingRequiredValue");
      }
    }

    DateFormat df = getDateFormat(pRequest, pAttributes);
    try {
      Date d;
      synchronized (df) {  // date formatters are not thread-safe: 37533
        df.setLenient(getLenient(pAttributes));
        d = df.parse(pValue);
      }

      if (d == null)
        throw new TagConversionException("Unable to parse date from string: " + pValue +
                                     " with format=" + pAttributes.get(DATE_ATTRIBUTE) +
                                     " and locale=" + pAttributes.get(LOCALE_ATTRIBUTE),
             "invalidDate");

      Date retDate = dateCompare(d, pAttributes, pValue, df);

      if(debugMode)
        pRequest.logDebug("DateTagConverter: returning date object for date " + retDate.toString());

      return retDate;
    }
    catch (ParseException exc) {
      throw new TagConversionException("Unable to parse date from string: " + pValue +
                                       " with format=" + pAttributes.get(DATE_ATTRIBUTE) +
                                       " and locale=" + pAttributes.get(LOCALE_ATTRIBUTE), exc,
               "invalidDate");
    }

  }

  public String convertObjectToString(DynamoHttpServletRequest pRequest,
                Object pValue, Properties pAttributes)
     throws TagConversionException {

    DateFormat df = getDateFormat(pRequest, pAttributes);

    boolean debugMode = pRequest.isLoggingDebug();

    if (pValue == null){
      if (pAttributes.getProperty(NullableTagConverter.NULLABLE_ATTRIBUTE) != null) {
        return "";
      }
      if(debugMode)
        pRequest.logDebug("DateTagConverter: Attempting to convert null" +
          " Object to String. Returning null");

      return null;
    }

    String result;
    if (pValue instanceof Date) {
      if(debugMode)
        pRequest.logDebug("DateTagConverter: Attempting to convert Date object for date " + pValue.toString() + "into a String");
      Date d = (Date) pValue;
      synchronized (df) { // date formatters are not thread-safe: 123210 
        result = df.format(d);
      }
      return result;
    }
    /*
     * Assume that "long" values are timestamps
     */
    else if (pValue instanceof Long) {
      Date d = new Date(((Long) pValue).longValue());
      if(debugMode)
        pRequest.logDebug("DateTagConverter: Attempting to convert Long object for date " + pValue.toString() + "into a String");
      synchronized (df) { // date formatters are not thread-safe: 123210 
        result = df.format(d);
      }
      return result;
    }
    else if (pValue instanceof String) {
      if(debugMode)
        pRequest.logDebug("DateTagConverter: Input Object is an instance "
          + "of String. Returning " + pValue);
      return (String) pValue;
    }
    throw new TagConversionException("Cannot convert: " + pValue + " into a Date", "invalidDate");
  }

  boolean getLenient(Properties pAttributes) {
    String leneientAttr = pAttributes.getProperty(LENIENT_ATTRIBUTE);
    if(leneientAttr == null)
      return true;
    return Boolean.valueOf(leneientAttr).booleanValue();
  }
  
  /**
   */
  DateFormat getDateFormat(DynamoHttpServletRequest pReq, Properties pAttributes)
     throws TagConversionException {

    String datePattern = pAttributes.getProperty(DATE_ATTRIBUTE);

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

    // get datePattern
    if (datePattern == null || datePattern.length() == 0) {
      ResourceBundle b;
      if (locale == null) {
        b = ResourceBundle.getBundle("atg.droplet.TagResources", atg.service.dynamo.LangLicense.getLicensedDefault());
      } else {
        b = ResourceBundle.getBundle("atg.droplet.TagResources", locale);
      }
      try {
        datePattern = b.getString("defaultDateFormat");
      }
      catch (MissingResourceException exc) {}
      if (datePattern == null)
        throw new TagConversionException("Missing required 'date' attribute for date converter");
    }

    // figure out key
    String key;
    if (locale == null) {
      key = datePattern;
    } else {
      key = datePattern + localeName;
    }

    // get DateFormat based on key
    DateFormat df = (DateFormat) sDateFormatCache.get(key);
    if (df == null) {
      if (locale == null) {
        df = new SimpleDateFormat(datePattern);
      } else {
        df = new SimpleDateFormat(datePattern, locale);
      }
      sDateFormatCache.put(key, df);
    }

    return df;
  }

  /**
   * Utility method for comparing the date between 1 or 2 boundries
   */

  private Date dateCompare(Date d, Properties pAttributes, String pValue, DateFormat df)
       throws TagConversionException
  {
    // check mindate first
    if(pAttributes.get(MINDATE_ATTRIBUTE) != null){

      Date minDate = null;

      try{
        // gotta parse it first for the comparison
        synchronized (df) { // date formatters are not thread-safe: 37533
          df.setLenient(getLenient(pAttributes));
          minDate = df.parse((String)pAttributes.get(MINDATE_ATTRIBUTE));
        }
      }
      catch(ParseException exc){
        throw new TagConversionException("Unable to parse minDate from string: " + pAttributes.get(MINDATE_ATTRIBUTE) +
                 " with format=" + pAttributes.get(DATE_ATTRIBUTE) +
                 " and locale=" + pAttributes.get(LOCALE_ATTRIBUTE), exc,
                 "invalidDate");
      }

      if(minDate != null && !d.after(minDate)){
        // don't need to do anymore processing, we're done
        throw new TagConversionException("Date is outside specified bounds: " +
                 pValue +
                 " with format=" + pAttributes.get(DATE_ATTRIBUTE) +
                 " and locale=" + pAttributes.get(LOCALE_ATTRIBUTE),
                 "invalidDate");
      }

    }

    // now check the maxdate
    if(pAttributes.get(MAXDATE_ATTRIBUTE) != null){

      Date maxDate = null;
      try{
        synchronized (df) { // date formatters are not thread-safe: 37533
          df.setLenient(getLenient(pAttributes));
          maxDate = df.parse((String)pAttributes.get(MAXDATE_ATTRIBUTE));
        }
      }
      catch(ParseException exc){
          throw new TagConversionException("Unable to parse maxDate from string: " + pAttributes.get(MAXDATE_ATTRIBUTE) +
                   " with format=" + pAttributes.get(DATE_ATTRIBUTE) +
                   " and locale=" + pAttributes.get(LOCALE_ATTRIBUTE), exc,
                   "invalidDate");
      }
      if(maxDate != null && !d.before(maxDate)){
          // don't need to do anymore processing, we're done
          throw new TagConversionException("Date is outside specified bounds: " +
                   pValue +
                   " with format=" + pAttributes.get(DATE_ATTRIBUTE) +
                   " and locale=" + pAttributes.get(LOCALE_ATTRIBUTE),
                   "invalidDate");
      }

    }

    // date must be valid, so return it
    return d;
  }
}
