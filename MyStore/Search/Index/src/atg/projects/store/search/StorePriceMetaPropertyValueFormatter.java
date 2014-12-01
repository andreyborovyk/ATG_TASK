/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2010 Art Technology Group, Inc.
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

package atg.projects.store.search;

import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;

import atg.commerce.search.PriceMetaPropertyValueFormatter;
import atg.repository.search.MetaProperty;

/**
 * Store enhancement of DCS price formatter. Store's formatter doesn't display fractional part of price (i.e. pennies) to user.
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Search/Index/src/atg/projects/store/search/StorePriceMetaPropertyValueFormatter.java#3 $$Change: 635816 $ 
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class StorePriceMetaPropertyValueFormatter extends PriceMetaPropertyValueFormatter {
  /**
   * Class version
   */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Search/Index/src/atg/projects/store/search/StorePriceMetaPropertyValueFormatter.java#3 $$Change: 635816 $";

  /**
   * @throws NumberFormatException if wrong decimal value passed through <code>pValue</code> parameter.
   */
  @Override
  public String formatDisplayValue(MetaProperty pProperty, Locale pLocale, String pValue) {
    return getCurrencyFormat(pProperty, pLocale).format(new Double(pValue));
  }
  
  /**
   * Creates a {@link Format} instance to be used. This <code>Format</code> instance will be used by
   * {@link #formatDisplayValue(MetaProperty, Locale, String)} method to convert facet value returned by Search to the value
   * displayed to user.
   * <br/>
   * Override this method in order to define different price format for facets. If overridden, <b>do not return <code>null</code>!</b>
   * This will cause the <code>formatDisplayValue(MetaProperty, Locale, String)</code> method to crash.
   * @param pProperty - <code>price</code> meta-property
   * @param pLocale - current locale; if this parameter is <code>null</code>, default <code>Locale</code> will be used.
   * @return <code>Format</code> instance to be used when converting search price value to the displayable value. 
   */
  protected Format getCurrencyFormat(MetaProperty pProperty, Locale pLocale) {
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(pLocale == null ? Locale.getDefault() : pLocale);
    // Do not display cents to user
    currencyFormat.setMaximumFractionDigits(0);
    return currencyFormat;
  }
}
