/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.RequestLocale;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;

import java.util.Locale;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;


/**
 * A tag converter which is used to parse and format currency amounts.
 * <P>
 * An example of this is as follows:
 * <blockquote>
 * <code>&lt;valueof param="priceInfo.amount" currency&gt;no price&lt;/valueof&gt;</code>
 * </blockquote>
 * You can also specify a locale the currency should be rendered with:
 * <blockquote>
 * <code>&lt;valueof param="priceInfo.amount" currency locale="en_US"&gt;no price&lt;/valueof&gt;</code>
 * </blockquote>
 * If no locale is specified then the converter first looks for a request parameter named <i>locale</i>.
 * This parameter can either be a java.util.Locale object or a String which names a locale. If this
 * cannot be found then we fetch the locale from the RequestLocale.
 *
 * @author Bob Mason
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/CurrencyTagConverter.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public 
class CurrencyTagConverter
implements TagConverter
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/CurrencyTagConverter.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  public static final String NAME = "currency";
  static final String CURRENCY_ATTRIBUTE = NAME;
  static final String LOCALE_ATTRIBUTE = "locale";
  static final String LOCALE_PARAM = LOCALE_ATTRIBUTE;
  static final String MY_RESOURCE_NAME = "atg.droplet.DropletResources";
    

  //-------------------------------------
  // Member Variables
  //-------------------------------------

  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME);
  static Map sCurrencyFormatters = new HashMap();
  static Map sNumberFormatters = new HashMap();

  private final static TagAttributeDescriptor[] sTagAttributeDescriptors = {
    new TagAttributeDescriptor(CURRENCY_ATTRIBUTE, 
        "Indicates that the desired element should be formatted as a currency",
        false, true),
    new TagAttributeDescriptor(LOCALE_ATTRIBUTE, 
    	"The name of the date locale to use (if omitted, the default locale is used)",
        true, false),
    new TagAttributeDescriptor(RequiredTagConverter.REQUIRED_ATTRIBUTE, 
    	"If this attribute is present, this value is required",
        true, false)
  };

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // property: DefaultLocale
  Locale mDefaultLocale;

  /**
   * Sets property DefaultLocale
   **/
  public void setDefaultLocale(Locale pDefaultLocale) {
    mDefaultLocale = pDefaultLocale;
  }

  /**
   * Returns property DefaultLocale. If the property value is null, then
   * JVM's default locale is returned.
   **/
  public Locale getDefaultLocale() {
    if (mDefaultLocale != null)
      return mDefaultLocale;
    else
      return Locale.getDefault();
  }

  //-------------------------------------
  // property: UseRequestLocale
  boolean mUseRequestLocale = true;

  /**
   * Sets property UseRequestLocale
   **/
  public void setUseRequestLocale(boolean pUseRequestLocale) {
    mUseRequestLocale = pUseRequestLocale;
  }

  /**
   * Returns property UseRequestLocale
   **/
  public boolean isUseRequestLocale() {
    return mUseRequestLocale;
  }  

  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instanceof CurrencyTagConverter
   */
  public CurrencyTagConverter() {
  }

  /**
   * Returns the unique name for the TagConverter.  This name can be used by 
   * the TagConverterManager to get a handle to this converter.  When you
   * specify the use of a TagConverter using the converter attribute in 
   * a tag, you use this name to refer to the TagConverter.  TagConverter
   * names are case insensitive (as is the convention in HTML).
   */
  public String getName() {
    return NAME;
  }

  /**
   * Returns the list of TagAttributeDescriptors which are used by this converter.  
   */
  public TagAttributeDescriptor [] getTagAttributeDescriptors() {
    return sTagAttributeDescriptors;
  }

  /**
   * Converts the supplied String value to an Object value.  This operation
   * is performed if a converter is used with an input tag during the
   * form submission process or if a converter attributes are specified
   * in the param tag when defining a new parameter.
   *
   * @param pRequest the request object for this conversion.  This object
   * can be null if the conversion is not performed in the context of 
   * a request.
   * @param pAttributes The list of attributes in the tag that is invoking 
   * this converter.  
   * @param pValue The original String value to be converted.
   * @throws TagConversionException if the conversion failed.  
   * @return the converted object value.
   */
  public Object convertStringToObject(DynamoHttpServletRequest pRequest, 
  				      String pValue, Properties pAttributes)
     throws TagConversionException
  {
    if (pValue != null) pValue = pValue.trim();

    if (pValue == null || pValue.length() == 0) {
      // Do we also have the "required" attribute in here - if this is an error
      if (pAttributes.getProperty(RequiredTagConverter.REQUIRED_ATTRIBUTE) == null) {
        return null;
      }
      else {
        String msg = ResourceUtils.getMsgResource(
                        "missingRequiredValue", MY_RESOURCE_NAME, 
                        sResourceBundle);
        throw new TagConversionException(msg, "missingRequiredValue");
      }
    }

    Locale locale = getCurrencyLocale(pRequest, pAttributes);
    TagConversionException tce = null;
    try {
      NumberFormat formatter = getCurrencyFormat(locale);
      synchronized (formatter) {
        Number currency = formatter.parse(pValue);
        if (currency != null) 
          return currency;
      }
      tce = new TagConversionException("Unable to parse currency from string: " + pValue + 
                                       " with locale=" + locale, "invalidCurrency");
    }
    catch (ParseException exc) {
      tce = new TagConversionException("Unable to parse currency from string: " + pValue + 
                                       " with locale=" + locale, exc, "invalidCurrency");
    }
    try {
      NumberFormat formatter = getNumberFormat(locale);
      synchronized (formatter) {
        Number number = formatter.parse(pValue);
        if (number != null) 
          return number;
      }
      // fall through to throw previous exception
    }
    catch (ParseException e) {
        // fall through to throw previous exception
    }
    throw tce;
  }

  /**
   * Converts the supplied Object value to a String value.  This operation
   * is performed when you are displaying a value using this converter.
   * This occurs when the converter is used in the valueof tag or 
   * when rendering the value attribute of an input tag.
   * @param pRequest the request this conversion applies to.  You can use
   * this request to obtain the RequestLocale to localize the conversion
   * process.  This can be null if the conversion is not performed in
   * the context of a request.
   * @param pValue the Object value to be converted to a String
   * @param pAttributes the set of attributes supplied in this tag
   * declaration.
   * @return the converted object value.
   */
  public String convertObjectToString(DynamoHttpServletRequest pRequest,
  				      Object pValue, Properties pAttributes)
     throws TagConversionException
  {
    if ((pValue == null) || (pValue instanceof String))
      return (String)pValue;

    Locale locale = getCurrencyLocale(pRequest, pAttributes);
    try {
      return formatCurrency(pValue, locale);
    }
    catch (IllegalArgumentException exc) {
      throw new TagConversionException("Unable to format currency from object: " + pValue + 
                                       " of class + " + pValue.getClass().getName() + " with locale=" + 
                                       locale, exc, "invalidCurrency");
    }
  }

  /**
   * Return a NumberFormat object which can be used to format currency based on the given Locale
   */
  public static NumberFormat getCurrencyFormat(Locale pLocale) {
    NumberFormat formatter = (NumberFormat)sCurrencyFormatters.get(pLocale);
    if (formatter == null) {
      //bug 44599, get around Netscape 4.7x problem
      if ((pLocale != null ) && pLocale.toString().equals("en"))
        pLocale = new Locale("en","us","");    
      formatter = NumberFormat.getCurrencyInstance(pLocale);
      sCurrencyFormatters.put(pLocale, formatter);
    }
    return formatter;
  }

  public static NumberFormat getNumberFormat(Locale pLocale) {
    NumberFormat formatter = (NumberFormat)sNumberFormatters.get(pLocale);
    if (formatter == null) {
      formatter = NumberFormat.getNumberInstance(pLocale);
      sNumberFormatters.put(pLocale, formatter);
    }
    return formatter;
  }

  /**
   * Return the supplied currency formatted in the given Locale as a String.
   */
  public static String formatCurrency(Object pCurrency, Locale pLocale) {
    NumberFormat formatter = getCurrencyFormat(pLocale); 
    synchronized (formatter) {
      return formatter.format(pCurrency);
    }
  }

  /**
   * Returns the locale associated with the request. The method first searches
   * for a request paramater named <code>locale</code>. This value can be
   * either a java.util.Locale object or a String which represents the locale. 
   * Next if the <code>useRequestLocale</code> property is true, then the locale
   * of the request will be returned. Finally, if the locale cannot be determined, 
   * the the <code>defaultLocale</code> property is used.
   */
  Locale getCurrencyLocale(DynamoHttpServletRequest pRequest, Properties pAttributes)
       throws TagConversionException
  {
    Object locale = pAttributes.get(LOCALE_ATTRIBUTE);
    String localeName = "";
    
    if (locale instanceof Locale) {
      return (Locale) locale;
    } else if (locale instanceof String) {
      localeName = (String) locale;
    }
    
    if (!StringUtils.isEmpty(localeName))
      return RequestLocale.getCachedLocale(localeName);
    else {
      Object obj = pRequest.getObjectParameter(LOCALE_PARAM);
      if (obj instanceof Locale)
        return (Locale)obj;
      else if (obj instanceof String)
        return RequestLocale.getCachedLocale((String)obj);
      else if (isUseRequestLocale()) {
        RequestLocale requestLocale = pRequest.getRequestLocale();
        if (requestLocale != null)
          return requestLocale.getLocale();
      }
    }

    return getDefaultLocale();
  }

} // end of class
