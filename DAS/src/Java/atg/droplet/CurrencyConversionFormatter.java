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

import atg.servlet.*;
import atg.nucleus.naming.ParameterName;
import atg.droplet.DropletException;

import java.text.NumberFormat;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * This droplet can be used to convert and format a numeric amount,
 * within a particular Locale, from a specific currency into a specific currency.
 *
 * <P>
 *
 * Example:
 * <PRE>
 * &lt;importbean bean="/atg/dynamo/droplet/CurrencyConversionFormatter"&gt;
 * &lt;droplet bean="CurrencyConversionFormatter"&gt;
 *   &lt;param name="currency" value="param:amount"&gt;
 *   &lt;param name="locale" value="en_US"&gt;
 *   &lt;param name="targetLocale" value="bean:/OriginatingRequest.requestLocale.locale"&gt;
 *   &lt;param name="euroSymbol" value="&euro"&gt;
 *   &lt;oparam name="output"&gt;
 *     &lt;valueof param="formattedCurrency" valueishtml&gt;no price&lt;/valueof&gt;
 *   &lt;/oparam&gt;
 * &lt;/droplet&gt;
 * </PRE>
 *
 * Usage:
 * <dl>
 * <dt>currency
 * <dd>A number, expressed as either a java.lang.Number or String, which is the value to format
 *
 * <dt>locale <dd>The locale that "currency" is currently accurate for.  For example,
 * if sourceLocale is "en_US" then currency is expressed in US dollars. This value can be
 * either a java.util.Locale object or a String which represents a locale. (e.g. en_US)
 * If you want this to use Euro's, the variant of the locale must be EURO.
 * This parameter is optional, if it is not provided then the default locale is assumed.
 *
 * <dt>targetLocale
 * <dd>The locale which should be used to convert format the currency amount. This value can
 * If you want this to use Euro's, the variant of the locale must be EURO.
 * be either a java.util.Locale object or a String which represents a locale. (e.g. en_US)
 *
 * <dt>euroSymbol <dd>This optional parameter will be used if the target locale is using Euro's.
 * If this is provided, it will be used as the symbol for Euros. This is useful because the
 * commonly-used character set ISO Latin-1 (ISO 8859-1) does not include this character.
 *
 * <dt>output
 * <dd>This parameter is serviced if the instance of this class is used as a droplet
 *
 * <dt>formattedCurrency
 * <dd>Available from within the <code>output</code> parameter, this is the formatted currency
 * </dl>
 *
 * @author Sam Perman
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/CurrencyConversionFormatter.java#2 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $*/

public
class CurrencyConversionFormatter
extends CurrencyFormatter
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/CurrencyConversionFormatter.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  static final ParameterName CURRENCY_PARAM = ParameterName.getParameterName("currency");
  static final ParameterName LOCALE_PARAM = ParameterName.getParameterName("locale");
  static final ParameterName TARGET_LOCALE_PARAM = ParameterName.getParameterName("targetLocale");
  static final ParameterName EURO_SYMBOL_PARAM = ParameterName.getParameterName("euroSymbol");
  static final ParameterName YEN_SYMBOL_PARAM = ParameterName.getParameterName("yenSymbol");
  static final ParameterName OUTPUT_PARAM = ParameterName.getParameterName("output");
  static final String FORMATTED_CURRENCY_PARAM = "formattedCurrency";
  static final Double ZERO = new Double(0.0);
  static final String EXCHANGE_RATES_BUNDLE_NAME = "atg.droplet.ExchangeRates";

  static final String EURO = "EURO";
  static final String EURO_KEY = "EURO";
  static final char EURO_CHARACTER = '\u20AC';

  static final String YEN_KEY = "ja";
  static final char YEN_CHARACTER = '\uFFE5'; //half byte unicode for yen

  //-------------------------------------
  // Member Variables
  //-------------------------------------

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
   * Constructs an instanceof CurrencyConversionFormatter
   */
  public CurrencyConversionFormatter() {
  }

  /**
   * Returns the locale associated with the request. The method first searches
   * for a request paramater named <code>sourceLocale</code>. This value can be
   * either a java.util.Locale object or a String which represents the locale.
   * Next if the <code>useRequestLocale</code> property is true, then the locale
   * of the request will be returned. Finally, if the locale cannot be determined,
   * the the <code>defaultLocale</code> property is used.
   */
  protected Locale getCurrencyTargetLocale(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    Object obj = pRequest.getObjectParameter(TARGET_LOCALE_PARAM);
    if (obj instanceof Locale)
      return (Locale)obj;
    else if (obj instanceof String)
      return RequestLocale.getCachedLocale((String)obj);
    else if (isUseRequestLocale()) {
      RequestLocale requestLocale = pRequest.getRequestLocale();
      if (requestLocale != null)
        return requestLocale.getLocale();
    }

    return getDefaultLocale();
  }

  /**
   * Returns the symbol to use when formatting a price in Euros.  This is
   * optional.
   */
  protected String getEuroSymbol(DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    return (String) pRequest.getObjectParameter(EURO_SYMBOL_PARAM);
  }


  /**
   * Returns the symbol to use when formatting a price in Yen.  
   */
  protected String getYenSymbol(DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    return (String) pRequest.getObjectParameter(YEN_SYMBOL_PARAM);
  }

  /**
   * Return the currency formatted in the given Locale as a
   * String. The currency and locales are extracted from the request
   * using the <code>getCurrency(request, response)</code>
   * <code>getCurrencyLocale(request, response)</code> and
   * <code>getCurrencyTargetLocale(request, response)</code> methods.
   */
  protected String formatCurrency(DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    Number currency = getCurrency(pRequest, pResponse);
    Locale sourceLocale = getCurrencyLocale(pRequest, pResponse);
    Locale targetLocale = getCurrencyTargetLocale(pRequest, pResponse);
    Number convertedCurrency = convertCurrency(currency, sourceLocale, targetLocale);
    String targetLang = targetLocale.getLanguage();

    if(convertedCurrency == null)
      return null;

    String formattedCurrency = null;

    String variant = targetLocale.getVariant();
    if(variant.equals(EURO)) {
      formattedCurrency = EuroTagConverter.formatCurrency(convertedCurrency, targetLocale);
      String euroReplacementString = getEuroSymbol(pRequest, pResponse);
      if (formattedCurrency != null && formattedCurrency.length() != 0 &&
          euroReplacementString != null &&
          euroReplacementString.length() != 0)
      {
        int index;
        while ((index = formattedCurrency.indexOf(EURO_CHARACTER)) != -1)
        {
          formattedCurrency = formattedCurrency.substring(0, index) + euroReplacementString +
             formattedCurrency.substring(index + 1);
        }
      }
    }
    else if (targetLang.equals(YEN_KEY)){
      formattedCurrency = CurrencyTagConverter.formatCurrency(convertedCurrency, targetLocale);

      String yenReplacementString = getYenSymbol(pRequest,pResponse);
      if (formattedCurrency != null && formattedCurrency.length() != 0 &&
          yenReplacementString != null &&
          yenReplacementString.length() != 0)
      {
        int index;
        while ((index = formattedCurrency.indexOf(YEN_CHARACTER)) != -1)
        {
          formattedCurrency = formattedCurrency.substring(0, index) + yenReplacementString +
             formattedCurrency.substring(index + 1);
        }

      }     
    }
    else {
      formattedCurrency = CurrencyTagConverter.formatCurrency(convertedCurrency, targetLocale);
    }

    if (isLoggingDebug())
      logDebug("currency="+currency+"; locale="+targetLocale+"; formatted currency="+formattedCurrency);
   
    return formattedCurrency;
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
   * @exception ServiceException if an entry could not be parsed as a Double
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
   * @param pSourceLocale the locale indicating the currency from which to convert
   * @param pTargetLocale the locale indicating the currency to which to convert
   **/
  public Number convertCurrency(Number pOriginalValue, Locale pSourceLocale, Locale pTargetLocale)
  {
    String sourceLookupKey = getLookupKey(pSourceLocale);
    String targetLookupKey = getLookupKey(pTargetLocale);
    // unused String defaultLookupKey = getLookupKey(getDefaultLocale());

    if(sourceLookupKey.equals(targetLookupKey))
       return pOriginalValue;

    Double sourceExchangeRateObject =
      (Double) getExchangeRates().get(sourceLookupKey);
    Double targetExchangeRateObject =
      (Double) getExchangeRates().get(targetLookupKey);

    if (sourceExchangeRateObject == null || targetExchangeRateObject == null)
      return null;

    double exchangeRate=0.0;
    exchangeRate = targetExchangeRateObject.doubleValue() / sourceExchangeRateObject.doubleValue();

    int numDecimals = CurrencyTagConverter.getCurrencyFormat(pTargetLocale).getMaximumFractionDigits();
    return new Double( round( ((Number) pOriginalValue).doubleValue() * exchangeRate,
                              numDecimals));
  }

  /**
   * Obtain the string to use as a look-up key to determine the exchange rate.
   * The implementation here uses the locale's country name.
   * @param pLocale the user's locale
   **/
  public String getLookupKey(Locale pLocale)
  {
    String variant = pLocale.getVariant();
    if(variant.equals(EURO))
      return EURO_KEY;
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
   * Takes the formatted currency String and sets a request parameter named
   * <code>formattedCurrency</code>, then services the <code>output</code>
   * parameter.
   */
  public void service(DynamoHttpServletRequest pRequest,
                      DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    String formattedCurrency = formatCurrency(pRequest, pResponse);
    pRequest.setParameter(FORMATTED_CURRENCY_PARAM, formattedCurrency);
    pRequest.serviceLocalParameter(OUTPUT_PARAM, pRequest, pResponse);
  }

} // end of class
