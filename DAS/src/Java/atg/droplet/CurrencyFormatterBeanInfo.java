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
 * <p>BeanInfo for the CurrencyFormatter droplet.
 *
 * @author Sam Perman
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/CurrencyFormatterBeanInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class CurrencyFormatterBeanInfo extends DropletBeanInfo {
  //-------------------------------------
  // CONSTANTS
  //-------------------------------------
  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/CurrencyFormatterBeanInfo.java#2 $$Change: 651448 $";

  public static final String FUNCTIONAL_COMPONENT_CATEGORY = "Services";

  //-------------------------------------
  // FIELDS
  //-------------------------------------

  private final static ParamDescriptor ERROR_PARAM = new ParamDescriptor("error", "Any exception that may have occured while formatting the currency", Exception.class, true, true);

  private final static ParamDescriptor[] sOutputDescriptors = {
    new ParamDescriptor("formattedCurrency", "The formatted currency.", String.class, true, true),
    ERROR_PARAM
  };

  private final static ParamDescriptor[] sParamDescriptors = {
    new ParamDescriptor("currency", "A number, expressed as either a java.lang.Number or String, which is the value to format", String.class, false, false),
    new ParamDescriptor("locale", "The locale which should be used to format the currency amount. This value can be either a java.util.Locale object or a String which represents a locale. (e.g. en_US)", String.class, true, false),

    new ParamDescriptor("output", "Contains the formatted text.", DynamoServlet.class, true, true, sOutputDescriptors),
  };

  private final static BeanDescriptor sBeanDescriptor =
  createBeanDescriptor(CurrencyFormatter.class,
           null,
           "This Dynamo Servlet Bean formats a numeric amount, within a particular Locale, into a specific currency.",
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
