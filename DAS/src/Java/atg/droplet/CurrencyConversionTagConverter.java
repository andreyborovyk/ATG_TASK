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

import atg.nucleus.ServiceListener;
import atg.nucleus.ServiceException;
import atg.nucleus.ServiceEvent;

import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Enumeration;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;

/**
 * A tag converter which is used to parse, format currency amounts and to
 * convert them between an default currency and a locale-specific currency.
 * <P>
 * This converter is a subclass of CurrencyTagConverter, and relies upon
 * CurrencyTagConverter to do parsing and formatting.
 * <P>
 * The exchange rates are specified in a properties file that uses 2-letter
 * country codes as the property names.  The corresponding value is the number
 * in that country's local currency that corresponds to one unit of the default
 * currency.
 * <P>
 * If the country of the user's locale (or of the locale specified as an attribute
 * with this TagConverter) does not have a configured exchange rate, then this
 * TagConverter returns null, which causes a &lt;VALUEOF&gt; tag to use its default
 * value.
 * <P>
 * An example of the use of this converter is as follows:
 * <blockquote>
 * <code>&lt;valueof param="priceInfo.amount" currencyConversion&gt;no price&lt;/valueof&gt;</code>
 * </blockquote>
 * You can also specify a locale as follows:
 * <blockquote>
 * <code>&lt;valueof param="priceInfo.amount" currencyConversion locale="en_US"&gt;no price&lt;/valueof&gt;</code>
 * </blockquote>
 * As with CurrencyTagConverter, if no locale is specified then the converter first looks for a request parameter named <i>locale</i>.
 * This parameter can either be a java.util.Locale object or a String which names a locale. If this
 * cannot be found then we fetch the locale from the RequestLocale.
 *
 * @author Lew Lasher
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/CurrencyConversionTagConverter.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public
class CurrencyConversionTagConverter
extends CurrencyTagConverter
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/CurrencyConversionTagConverter.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  public static final String NAME = "currencyConversion";
  static final String CURRENCY_ATTRIBUTE = NAME;
  static final String LOCALE_ATTRIBUTE = "locale";
  static final String EXCHANGE_RATES_BUNDLE_NAME = "atg.droplet.ExchangeRates";
  static final String REVERSE_ATTRIBUTE = "reverse";
  static final String EURO = "EURO";
  static final String SYMBOL_ATTRIBUTE = "symbol";
  static final char EURO_CHARACTER = '\u20AC';

  //-------------------------------------
  // Member Variables
  //-------------------------------------

  private final static TagAttributeDescriptor[] sTagAttributeDescriptors = {

    new TagAttributeDescriptor(CURRENCY_ATTRIBUTE,
        "Indicates that the desired element should be formatted as a currency and converted in amount",
        false, true),
    new TagAttributeDescriptor(LOCALE_ATTRIBUTE,
    	"The name of the locale to use (if omitted, the default locale is used)",
        true, false),
    new TagAttributeDescriptor(RequiredTagConverter.REQUIRED_ATTRIBUTE,
    	"If this attribute is present, this value is required",
        true, false),
    new TagAttributeDescriptor(REVERSE_ATTRIBUTE,
      "If this attribute is present, reverse the conversion",
        true, false),
    new TagAttributeDescriptor(SYMBOL_ATTRIBUTE,
    	"A string which, if present, will be substituted for the euro symbol",
        true, false)
  };

  private boolean mIsInitialized = false;

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // property: exchangeRates
  protected Map mExchangeRates;

  /**
   * Sets property exchangeRates
   * <P>
   * The value of this property is a Map that
   * has an entry for each supported currency conversion.
   * The name for each entry is a 2-letter country code.  The value for
   * each entry is a double that indicates
   * how many of the locale-specific currency are equivalent
   * to one (1) unit of the default currency.
   **/
  public void setExchangeRates(Map pExchangeRates) {
    mExchangeRates = pExchangeRates;
  }

  /**
   * Returns property exchangeRates, and, as a side-effect,
   * initializes it if it has not already been initialized.
   * @see initializeIfNecessary
   **/
  public Map getExchangeRates() {
    initializeIfNecessary();
    return mExchangeRates;
  }

  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instance of CurrencyConversionTagConverter
   */
  public CurrencyConversionTagConverter() {
  }

  //-------------------------------------
  // Methods
  //-------------------------------------

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
    Object parsedValue = super.convertStringToObject(pRequest, pValue, pAttributes);
    if (parsedValue == null) return parsedValue;
    if (! (parsedValue instanceof Number)) return parsedValue;

    Locale locale = getCurrencyLocale(pRequest, pAttributes);

    if (pAttributes.getProperty(REVERSE_ATTRIBUTE) == null) {
			return convertCurrency((Number) parsedValue, locale, false);
		} else {
			if (!EURO.equals(locale.getVariant()))
			  locale = new Locale( locale.getLanguage(), locale.getCountry(), EURO);
			return convertCurrency((Number) parsedValue, locale, true);
	  }
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
    if ((pValue == null) || (pValue instanceof String) || ! (pValue instanceof Number))
      return super.convertObjectToString(pRequest, pValue, pAttributes);

    Locale locale = getCurrencyLocale(pRequest, pAttributes);

    Object convertedValue;
    if (pAttributes.getProperty(REVERSE_ATTRIBUTE)  == null) {
			convertedValue = convertCurrency((Number) pValue, locale, true);
	  } else {
      if (!EURO.equals(locale.getVariant()))
			  locale = new Locale( locale.getLanguage(), locale.getCountry(), EURO);
      convertedValue = convertCurrency((Number) pValue, locale, false);
	  }

    if (convertedValue==null)
      return super.convertObjectToString(pRequest, convertedValue, pAttributes);

    String result = formatCurrency(convertedValue, locale);

    String euroReplacementString = pAttributes.getProperty(SYMBOL_ATTRIBUTE);
    if (locale.getVariant().equals(EURO) &&
        result != null && result.length() != 0 &&
        euroReplacementString != null &&
        euroReplacementString.length() != 0)
    {
        result = replaceEURO(result, euroReplacementString);
    }

    return result;
  }

  /**
   * Do the initialization in <i>initializeConversionMap</i> on a
   * just-in-time basis.  Formerly this was done in the constructor,
   * causing a deferrable start-up cost for this class.
   * @see initializeConversionMap
   * @see getExchangeRates
  */

  protected void initializeIfNecessary()
  {
    if (! mIsInitialized)
    {
      initializeConversionMap();
      mIsInitialized = true;
    }
  }


  /**
   * Constructs a Map of locale names to currency exchange rates, based
   * on the entries in a ResourceBundle
   **/

  public void initializeConversionMap()
  {
    Map map = new HashMap();
    setExchangeRates(map);

    ResourceBundle b = ResourceBundle.getBundle(EXCHANGE_RATES_BUNDLE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

    Enumeration localeEnumerator = b.getKeys();
    while (localeEnumerator.hasMoreElements())
    {
      String countryName = (String) localeEnumerator.nextElement();
      String exchangeRateString = b.getString(countryName);
      double exchangeRate = 0.0;

      try
      {
        exchangeRate = Double.parseDouble(exchangeRateString);
      }
      catch (NumberFormatException e)
      {
        System.out.println("Could not parse exchange rate for " + countryName);
        continue;
      }

      map.put(countryName, new Double(exchangeRate));
    }
  }

  /**
   * Convert an amount of money between the default currency and the
   * currency of a specified locale
   * <BR>
   * Returns null if the locale does not have a configured exchange rate
   * @param pOriginalValue the amount of money to convert
   * @param pLocale the locale indicating the currency from/to which to convert
   * @param toLocale true to convert from default to locale-specific currency,
   * false to convert from locale-specific currency to default currency
   **/
  public Number convertCurrency(Number pOriginalValue, Locale pLocale, boolean toLocale)
  {
    String lookupKey = getLookupKey(pLocale);
    Double exchangeRateObject =
      (Double) getExchangeRates().get(lookupKey);
    if (exchangeRateObject == null)
      return null;

    double exchangeRate = exchangeRateObject.doubleValue();
    if (toLocale) // converting to locale-specific
    {
      int numDecimals = getCurrencyFormat(pLocale).getMaximumFractionDigits();
      return new Double(
        round(
          ((Number) pOriginalValue).doubleValue() * exchangeRate,
          numDecimals));
    }
    else // converting to EURO
    {
      int numDecimals = EuroTagConverter.getCurrencyFormat(pLocale).getMaximumFractionDigits();
      return new Double(
        round(
          ((Number) pOriginalValue).doubleValue() / exchangeRate,
          numDecimals));
    }
  }

  /**
   * Obtain the string to use as a look-up key to determine the exchange rate.
   * The implementation here uses the locale's country name.
   * @param pLocale the user's locale
   **/
  public String getLookupKey(Locale pLocale)
  {
    return pLocale.getCountry();
  }

  /**
   * Round a double-precision floating-point number to N places to the right
   * of the decimal point.
   * @param pValue the number to round
   * @param pNumDecimals the number of places to the right of the decimal point
   **/
  double round(double pValue, int pNumDecimals)
  {
    // Convert to decimal representation, so that there is no round-off error
    // in multiplying by 10**n, or, more crucially, in dividing by 10**n after
    // rounding to the nearest integer

    BigDecimal temp = new BigDecimal(pValue);
    temp = temp.movePointRight(pNumDecimals);

    // Round to the nearest integer by using Math.floor, rather than Math.round,
    // because Math.round documentation doesn't clarify whether 0.5 is rounded up

    temp = new BigDecimal(Math.floor(temp.doubleValue() + 0.5));
    temp = temp.movePointLeft(pNumDecimals);
    return temp.doubleValue();
  }

  /**
   * Returns a string with the Euro character replaced with the supplied string.
   */
  protected static String replaceEURO(String pCurrency, String pReplacement) {
      int index;
      while ((index = pCurrency.indexOf(EURO_CHARACTER)) != -1)
      {
        pCurrency = pCurrency.substring(0, index) + pReplacement +
           pCurrency.substring(index + 1);
      }
      return pCurrency;
  }

} // end of class
