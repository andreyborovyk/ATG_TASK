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

package atg.commerce.order.purchase;

import java.util.*;
import java.text.*;
import atg.core.i18n.LayeredResourceBundle;

/**
 * <p>This class encapsulates messages, configurable string constants
 *  and message formatting conventions.  This class is used for client
 *  messages.  These are messages displayed to a user when something 
 *  is not correct.  i.e. The user incorrectly types in the wrong
 *  information on a form.  
 *
 * <P>This class obtains resource bundles based on a <code>java.util.Locale</code> object.  
 *
 * <P>Usage:
 * <UL>
 *  <li>to obtain a string from a resource bundle:
 *      <br><code>PurchaseUserMessage.getString("MSG1", Locale)</code>
 *  <li>to obtain a formatted message from a resource bundle
 *      <br><code>Object[] params = {"one", "two"};</code>
 *      <br><code>PurchaseUserMessage.format("MSG2", params, Locale)</code>
 * </UL>
 *
 * @author Bob Mason
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/PurchaseUserMessage.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class PurchaseUserMessage
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/PurchaseUserMessage.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  public static String RESOURCE_BUNDLE = "atg.commerce.order.purchase.UserMessages";

  //-------------------------------------
  // Member Variables
  //-------------------------------------
  /** static cache of MessageFormat objects */
  //static Hashtable sFormats = new Hashtable(89);

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // Constructors
  //-------------------------------------

  // No constructor, all static 

  /**
   * Returns a ResourceBundle specific for the given locale
   * @param pLocale the locale of the user
   * @return a <code>ResourceBundle</code> value
   * @exception MissingResourceException if an error occurs
   */
  public static ResourceBundle getResourceBundle (Locale pLocale)
    throws MissingResourceException
  {
    return (LayeredResourceBundle.getBundle(RESOURCE_BUNDLE, pLocale));
  }

  /**
   * Return a String message specific for the given locale
   * @param pKey the identifier for the message to retrieve out of the ResourceBundle
   * @param pLocale the locale of the user
   * @return the localized message
   */
  public static String getString(String pKey, Locale pLocale) {
    return(getResourceBundle(pLocale).getString(pKey));
  }
 
  /**
   * Return a MessageFormat from the resource bundle, for the given locale
   * @param pLocale the locale of the user
   * @param pKey the identifier for the message to retrieve out of the ResourceBundle
   * @return the localized message format
   */
  public static MessageFormat getFormat(String pKey, Locale pLocale) {
    return new MessageFormat(getString(pKey, pLocale));
  }

  /**
   * Return a String message specific for the given locale. <BR>
   * note: functionaly equivalent to <code>getString</code>
   * @param pKey the identifier for the message to retrieve out of the ResourceBundle
   * @param pLocale the locale of the user
   * @return the localized message
   */
  public static String format (String pKey, Locale pLocale) {
    return getString(pKey, pLocale);
  }

  /**
   * Format a single parameter into a message for a given locale
   * @param pKey the identifier for the message to retrieve out of the ResourceBundle
   * @param pParam a single parameter to use in formatting
   * @param pLocale the locale of the user
   * @return the localized and formatted message
   */
  public static String format (String pKey, Object pParam, Locale pLocale) {
    Object[] params = { pParam };
    return getFormat(pKey, pLocale).format(params);
  }
    
  /**
   * Format a set of parameters into a message for a given locale
   * @param pKey the identifier for the message to retrieve out of the ResourceBundle
   * @param pParams a set of parameters to use in the formatting
   * @param pLocale the locale of the user
   * @return the localized and formatted message
   */
  public static String format (String pKey, Object[] pParams, Locale pLocale) {
    return getFormat(pKey, pLocale).format(pParams);
  }

} // end of class
