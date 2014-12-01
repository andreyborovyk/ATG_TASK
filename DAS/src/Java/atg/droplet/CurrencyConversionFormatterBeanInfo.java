/*<ATGCOPYRIGHT>
 * Copyright (C) 2000-2011 Art Technology Group, Inc.
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

import atg.servlet.DynamoServlet;
import atg.droplet.DropletBeanInfo;
import atg.droplet.ParamDescriptor;

import java.beans.*;
import java.util.Date;

/**
 * <p>BeanInfo for the CurrencyConversionFormatter droplet.
 *
 * @author Sam Perman
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/CurrencyConversionFormatterBeanInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class CurrencyConversionFormatterBeanInfo extends DropletBeanInfo {
  //-------------------------------------
  // CONSTANTS
  //-------------------------------------
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/CurrencyConversionFormatterBeanInfo.java#2 $$Change: 651448 $";

  public static final String FUNCTIONAL_COMPONENT_CATEGORY = "Services";

  //-------------------------------------
  // FIELDS
  //-------------------------------------

  private final static ParamDescriptor ERROR_PARAM = new ParamDescriptor("error", "Any exception that may have occured while converting or formatting the currency", Exception.class, true, true);

  private final static ParamDescriptor[] sOutputDescriptors = {
    new ParamDescriptor("formattedCurrency", "The converted and formatted currency.", String.class, true, true),
    ERROR_PARAM
  };

  private final static ParamDescriptor[] sParamDescriptors = {
    new ParamDescriptor("currency", "A number, expressed as either a java.lang.Number or String, which is the value to format", String.class, false, false),
    new ParamDescriptor("locale", "The locale that currency is expressed in.  This is the locale that the number will be converted from. This value can be either a java.util.Locale object or a String which represents a locale. (e.g. en_US)", String.class, true, false),
    new ParamDescriptor("targetLocale", "The locale that the number will be converted to and formatted for. This value can be either a java.util.Locale object or a String which represents a locale. (e.g. en_US)", String.class, true, false),
    new ParamDescriptor("euroSymbol", "If the targetLocale has a variant of EURO, then this symbol will replace the Euro currency symbol", String.class, true, false),

    new ParamDescriptor("output", "Contains the formatted text.", DynamoServlet.class, true, true, sOutputDescriptors),
  };

  private final static BeanDescriptor sBeanDescriptor =
  createBeanDescriptor(CurrencyConversionFormatter.class,
           null,
           "This Dynamo Servlet Bean converts a numeric amount from one currency to another, then formats the result into the target Locale's currency.",
           sParamDescriptors,
           FUNCTIONAL_COMPONENT_CATEGORY);

  //-------------------------------------
  // METHODS
  //-------------------------------------

  //-------------------------------------
  /**
   * Returns the BeanDescriptor for this bean, which will in turn
   * contain ParamDescriptors for the droplet.
   **/
  public BeanDescriptor getBeanDescriptor() {
    return sBeanDescriptor;
  }

  //----------------------------------------
}
