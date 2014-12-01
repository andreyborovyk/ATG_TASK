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
import java.util.Locale;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * This droplet can be used to format a numeric amount, within a particular Locale, into a specific currency.
 *
 * <P>
 *
 * Example:
 * <PRE>
 * &lt;importbean bean="/atg/dynamo/droplet/CurrencyFormatter"&gt;
 * &lt;droplet bean="CurrencyFormatter"&gt;
 *   &lt;param name="currency" value="param:amount"&gt;
 *   &lt;param name="locale" value="bean:/OriginatingRequest.requestLocale.locale"&gt;
 *   &lt;oparam name="output"&gt;
 *     &lt;valueof param="formattedCurrency"&gt;no price&lt;/valueof&gt;
 *   &lt;/oparam&gt;
 * &lt;/droplet&gt;
 * </PRE>
 *
 * Usage:
 * <dl>
 * <dt>currency
 * <dd>A number, expressed as either a java.lang.Number or String, which is the value to format
 *
 * <dt>locale
 * <dd>The locale which should be used to format the currency amount. This value can
 * be either a java.util.Locale object or a String which represents a locale. (e.g. en_US)
 *
 * <dt>output
 * <dd>This parameter is serviced if the instance of this class is used as a droplet
 *
 * <dt>formattedCurrency
 * <dd>Available from within the <code>output</code> parameter, this is the formatted currency
 * </dl>
 *
 * @author Bob Mason
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/CurrencyFormatter.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public
class CurrencyFormatter
extends DynamoServlet
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/CurrencyFormatter.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  static final ParameterName CURRENCY_PARAM = ParameterName.getParameterName("currency");
  static final ParameterName LOCALE_PARAM = ParameterName.getParameterName("locale");
  static final ParameterName EURO_SYMBOL_PARAM = ParameterName.getParameterName("euroSymbol");
  static final ParameterName OUTPUT_PARAM = ParameterName.getParameterName("output");
  static final String FORMATTED_CURRENCY_PARAM = "formattedCurrency";
  static final Double ZERO = new Double(0.0);

  static final String EURO = "EURO";
  static final char EURO_CHARACTER = '\u20AC';

  //-------------------------------------
  // Member Variables
  //-------------------------------------

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
   * Constructs an instanceof CurrencyFormatter
   */
  public CurrencyFormatter() {
  }

  /**
   * Returns the currency value from the request.  The method first searches for a request
   * parameter named <code>currency</code>, and if that cannot be found then return 0.0
   */
  protected Number getCurrency(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    Object obj = pRequest.getObjectParameter(CURRENCY_PARAM);
    if (obj instanceof Number)
      return (Number)obj;
    else if (obj instanceof String)
      return new Double(Double.parseDouble((String)obj));
    else if (isLoggingDebug()) {
      if (obj != null)
        logDebug("Currency obj=" + obj + "; class=" + obj.getClass());
      else
        logDebug("Currency obj=null");
    }
    return ZERO;
  }

  /**
   * Returns the locale associated with the request. The method first searches
   * for a request paramater named <code>locale</code>. This value can be
   * either a java.util.Locale object or a String which represents the locale.
   * Next if the <code>useRequestLocale</code> property is true, then the locale
   * of the request will be returned. Finally, if the locale cannot be determined,
   * the the <code>defaultLocale</code> property is used.
   */
  protected Locale getCurrencyLocale(DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
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
   * Return the currency formatted in the given Locale as a String. The currency
   * and locale are extracted from the request using the <code>getCurrency(request, response)</code>
   * and <code>getCurrencyLocale(request, response)</code> methods.
   */
  protected String formatCurrency(DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    Number currency = getCurrency(pRequest, pResponse);
    Locale locale = getCurrencyLocale(pRequest, pResponse);

    String formattedCurrency = null;

    String variant = locale.getVariant();

    
    if(variant.equals(EURO)) {
      formattedCurrency = EuroTagConverter.formatCurrency(currency, locale);
    }
    else {
      formattedCurrency = CurrencyTagConverter.formatCurrency(currency, locale);
    }

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

    if (isLoggingDebug())
      logDebug("currency="+currency+"; locale="+locale+"; formatted currency="+formattedCurrency);

    return formattedCurrency;
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
