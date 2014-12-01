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
 * A tag converter which will parse and format a currency amounts if and only
 * if the user's locale indicates a country/language that supports the Euro.
 * <BR>
 * <B>N.B.</B> the user's locale does not, itself, need to have the
 * "EURO" variant.  For example, if the user's locale is <I>fr_FR</I>, then the currency
 * amount will be formatted as though the locale were "fr_FR_EURO".
 * <P>
 * If the user's locale indicates a country that does not use use the Euro
 * (e.g., <I>en_US</I>), then the tag converter returns null, so that a &lt;VALUEOF&gt;
 * tag will use its default value.
 * <P>
 * This converter is a subclass of CurrencyTagConverter, and relies upon
 * CurrencyTagConverter to do parsing and formatting.
 * <P>
 * An example of the use of this converter is as follows:
 * <blockquote>
 * <code>&lt;valueof param="priceInfo.amount" euro&gt;no price&lt;/valueof&gt;</code>
 * </blockquote>
 * You can also specify a locale as follows:
 * <blockquote>
 * <code>&lt;valueof param="priceInfo.amount" euro locale="en_US"&gt;no price&lt;/valueof&gt;</code>
 * </blockquote>
 * As with CurrencyTagConverter, if no locale is specified then the converter first looks for a request parameter named <i>locale</i>.
 * This parameter can either be a java.util.Locale object or a String which names a locale. If this
 * cannot be found then we fetch the locale from the RequestLocale.
 * <P>
 * You can optionally specify a string to substitute for the euro currency symbol.
 * This is useful because the commonly-used character set ISO Latin-1 (ISO 8859-1)
 * does not include this character.  For example, you can specify:
 * <blockquote>
 * <code>&lt;valueof param="priceInfo.amount" euro symbol="&euro;" &gt;no price&lt;/valueof&gt;</code>
 * </blockquote>
 * This substitutes the HTML entity &amp;euro; in place of the euro currency symbol.
 * Most browsers correctly display this HTML entity as the currency symbol, even when
 * using ISO Latin-1.
 *
 * @author Lew Lasher
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/EuroTagConverter.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public 
class EuroTagConverter
extends CurrencyTagConverter
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/EuroTagConverter.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  public static final String NAME = "euro";
  static final String EURO_ATTRIBUTE = NAME;
  static final String LOCALE_ATTRIBUTE = "locale";
  static final String SYMBOL_ATTRIBUTE = "symbol";
  static final char EURO_CHARACTER = '\u20AC';	// The euro currency symbol

  //-------------------------------------
  // Member Variables
  //-------------------------------------

  private final static TagAttributeDescriptor[] sTagAttributeDescriptors = {

    new TagAttributeDescriptor(EURO_ATTRIBUTE, 
        "Indicates that the desired element should be formatted as an amount in the Euro currency if the locale indicates a Euro country",
        false, true),
    new TagAttributeDescriptor(LOCALE_ATTRIBUTE, 
    	"The name of the locale to use (if omitted, the default locale is used)",
        true, false),
    new TagAttributeDescriptor(SYMBOL_ATTRIBUTE, 
    	"A string which, if present, will be substituted for the euro symbol",
        true, false),
    new TagAttributeDescriptor(RequiredTagConverter.REQUIRED_ATTRIBUTE, 
    	"If this attribute is present, this value is required",
        true, false)
  };

  private static boolean mIsInitialized = false;

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // property: formatMap
  static Map mFormatMap;

  /**
   * Sets property formatMap
   * <P>
   * The value of this property is a Map that
   * has an entry for each euro-compatible locale.
   * The name for each entry is a language/country locale name.
   * The value for each entry is the NumberFormat to use to format
   * currency for that locale.
   **/

  public static void setFormatMap(Map pFormatMap) {
    mFormatMap = pFormatMap;
  }

  /**
   * Returns property formatMap, and, as a side-effect,
   * initializes it if it has not already been initialized.
   * @see initializeIfNecessary
   **/
  public static Map getFormatMap() {
    initializeIfNecessary();
    return mFormatMap;
  }  


  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instance of EuroTagConverter
   */
  public EuroTagConverter() {
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
   * Converts the supplied Object value to a String value.  This operation
   * is performed when you are displaying a value using this converter.
   * This occurs when the converter is used in the valueof tag or 
   * when rendering the value attribute of an input tag.
   * <P>
   * The implementation here is copied from the superclass CurrencyTagConverter,
   * but calls this class' override of static method formatCurrency.
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
      String result = formatCurrency(pValue, locale);
      String euroReplacementString = pAttributes.getProperty(SYMBOL_ATTRIBUTE);
      if (result != null && result.length() != 0 &&
          euroReplacementString != null &&
          euroReplacementString.length() != 0)
      {
        int index;
        while ((index = result.indexOf(EURO_CHARACTER)) != -1)
        {
          result = result.substring(0, index) + euroReplacementString +
             result.substring(index + 1);
        }
      }
      return result;
    }
    catch (IllegalArgumentException exc) {
      throw new TagConversionException("Unable to format currency from object: " + pValue + 
                                       " of class + " + pValue.getClass().getName() + " with locale=" + 
                                       locale, exc, "invalidCurrency");
    }
  }


  /**
   * Return the supplied currency formatted in the given Locale as a String.
   * <P>
   * The implementation here differs from that in the superclass, CurrencyTagConverter,
   * in that, here, if there is no (euro-oriented) NumberFormat available, then
   * this method returns null.
   */
  public static String formatCurrency(Object pCurrency, Locale pLocale) {
    NumberFormat formatter = getCurrencyFormat(pLocale);
    if (formatter == null)
      return (String) null;
    else
      return formatter.format(pCurrency);
  }

  /**
   * Return a NumberFormat object which can be used to format currency as an amount in euros.
   * <BR>
   * If the user's locale is not euro-compatible, then this method returns null.
   */
  public static NumberFormat getCurrencyFormat(Locale pLocale) {

    /**
     * Make sure the list of euro-compatible locales is available
     **/

    String lookupKey = pLocale.getLanguage() + "_" + pLocale.getCountry();
    NumberFormat formatter =
      (NumberFormat) getFormatMap().get(lookupKey);
    return formatter;
  }

  /**
   * Do the initialization in <i>initializeFormatMap</i> on a just-in-time
   * basis.  Formerly this was done in a static initalizer, causing a
   * big start-up cost for this class.
   * @see initializeFormatMap
   * @see getFormatMap
  */

  protected static void initializeIfNecessary()
  {
    if (! mIsInitialized)
    {
      initializeFormatMap();
      mIsInitialized = true;
    }
  }

  /**
  * Find all available euro-compatible locales, and construct a Map that associates
  * their language/country with the number format for formatting amounts of money
  * as an amount in Euros.
  */

  protected static void initializeFormatMap()
  {
    mFormatMap = new HashMap();
    Locale [] locales = NumberFormat.getAvailableLocales();
    for (int i=0; i<locales.length; i++)
    {
      Locale locale = locales[i];
         NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String lookupKey = language + "_" + country;
        mFormatMap.put(lookupKey, nf);
    }
  }

} // end of class
