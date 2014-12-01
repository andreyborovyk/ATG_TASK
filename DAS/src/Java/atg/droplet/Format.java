/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.text.MessageFormat;

import javax.servlet.*;
import javax.servlet.http.*;

import atg.servlet.*;
import atg.nucleus.ServiceEvent;
import atg.nucleus.ServiceException;
import atg.nucleus.logging.LogListener;
import atg.nucleus.naming.ParameterName;
import atg.core.util.ResourceUtils;
import atg.core.exception.ItemNotFoundException;

/**
 * This servlet uses <code>java.text.MessageFormat</code> to set a message 
 * string parameter that can be used when the <i>output</i> parameter is
 * rendered.
 * 
 * <p>A complete description of the parameters to the Format droplet is:
 * 
 * <dl>
 * 
 * <dt>format
 * <dd>The format string used to construct the 
 * <code>java.text.MessageFormat</code> object. Typically this is just
 * a string containing parameter names in curly braces showing where the 
 * arguments should be placed in the resulting message string, although many
 * more advanced features and options are available. The syntax of the 
 * format is the same as that of <code>java.text.MessageFormat</code>,
 * except that named parameters are used instead of numeric ones.
 * See the 
 * <code>java.text.MessageFormat</code> javadoc for more details.
 * <p>
 * 
 * <dt><i>named parameters</i>
 * <dd>
 * The arguments to be formatted. These may be objects of any type, but 
 * are typically strings. The name of each parameter should correspond to
 * a name inside curly braces in the <i>format</i> parameter.
 * <p>
 * 
 * <dt>locale
 * <dd>The <code>java.util.Locale</code> object passed to the constructed
 * MessageFormat object using the <code>setLocale</code> method. If this 
 * parameter is omitted, the locale of the request is used.
 * 
 * </dl>
 * 
 * Examples:
 * <p>
 * A simple example that concatentates two strings.
 * <pre>
 * &lt;droplet bean="/atg/dynamo/droplet/Format">
 *   &lt;param name="format" value="{first}{last}">
 *   &lt;param name="first" value="bean:/MyComponent.firstString">
 *   &lt;param name="last" value="bean:/MyComponent.secondString">
 *   &lt;oparam name="output">
 *     The concatenated value is &lt;valueof param="message"/>.
 *   &lt;/oparam>
 * &lt;/droplet> 
 * </pre>
 * <p>
 * This example is adapted from the MessageFormat javadoc:
 * <p>
 * <pre>
 * &lt;droplet bean="/atg/dynamo/droplet/Format">
 *   &lt;param name="format"
 *     value="At {when,time} on {when,date}, there was {what} on planet {where,number,integer}.">
 *   &lt;param name="where" value="bean:/MyComponent.location">
 *   &lt;param name="when" value="bean:/MyComponent.date">
 *   &lt;param name="what" value="bean:/MyComponent.event">
 *   &lt;oparam name="output">
 *     &lt;valueof param="message"/>
 *   &lt;/oparam>
 * &lt;/droplet> 
 * </pre>
 * <p>
 * If the "/MyComponent" component
 * has properties "location", "date", and "event" with values "new Integer(7)",
 * "new Date(System.currentTimeMillis())", and "a disturbance in the Force", the
 * output might be 
 * <pre>
 * At 12:30 PM on Jul 3, 2053, there was a disturbance in the Force on planet 7.
 * </pre>
 * <p>
 * @author Norris Boyd
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/Format.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see java.text.MessageFormat
 */
public class Format extends DynamoServlet {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/Format.java#2 $$Change: 651448 $";

  final static String FORMAT = "format";
  final static String LOCALE = "locale";
  final static String MESSAGE = "message";
  final static ParameterName OUTPUT = ParameterName.getParameterName("output");

  static final String MY_RESOURCE_NAME = "atg.droplet.DropletResources";
  private static java.util.ResourceBundle sResourceBundle = 
    java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  // Property CacheSize: the number of format strings to cache
  int mCacheSize = 11;
  /**
   * Return the number of format strings to cache.
   */
  public int getCacheSize() {
    return mCacheSize;
  }
  /**
   * Set the number of format strings to cache.  
   */
  public void setCacheSize(int pCacheSize) {
    mCacheSize = pCacheSize;
  }
  
  /**
   * A preparsed format, suitable for caching.
   */
  static final class ParsedFormat {
    String[] mArgNames;
    MessageFormat mMessageFormat;
        
    ParsedFormat(String pFormat, Locale pLocale) {
      ArrayList argNames = new ArrayList();
      String parsed = parseFormat(pFormat, argNames);
      mArgNames = new String[argNames.size()];
      argNames.toArray(mArgNames);
      mMessageFormat = new MessageFormat(parsed);
      mMessageFormat.setLocale(pLocale);
    }
  
    /**
     * Parse the format string (which may have parameters
     * of the form "{argName}"), and return a new format
     * string suitable for use by MessageFormat (i.e., with "{0}",
     * "{1}, etc.) The argNames ArrayList should be empty on entry. 
     * On exit it will be set to contain the list of argument names 
     * in order.
     */
    String parseFormat(String format, ArrayList argNames) {
      StringBuffer result = new StringBuffer(format.length());
      boolean inQuotes = false; // Don't process {'s inside single quotes
      for (int i=0; ; i++) {
        if (i == format.length())
          return result.toString();
        char c = format.charAt(i);
        result.append(c);
        if (c == '\'')
          inQuotes = !inQuotes;
        if (c != '{' || inQuotes)
          continue;
        StringBuffer arg = new StringBuffer();
        for (;;) {
          if (++i == format.length()) {
            result.append(arg.toString());
            return result.toString();
          }
          c = format.charAt(i);
          if (c == '}' || c == ',') {
            result.append(argNames.size());
            result.append(c);
            argNames.add(arg.toString());
            break;
          }
          arg.append(c);
        }
      }
    }
  }

  /**
   * A key for ParsedFormat elements in the cache. 
   */
  static final class ParsedFormatKey {
    ParsedFormatKey(String pFormat, Locale pLocale) {
      mFormat = pFormat;
      mLocale = pLocale;
    }
    public boolean equals(Object pOther) {
      ParsedFormatKey other = (ParsedFormatKey) pOther;
      return other.mFormat.equals(mFormat) && other.mLocale.equals(mLocale);
    }
    public int hashCode() {
      return mFormat.hashCode() ^ mLocale.hashCode();
    }
    final String mFormat;
    final Locale mLocale;
  }
  
  /**
   * The LRU cache for ParsedFormat elements.
   */
  final class FormatCache extends atg.core.cache.LRUCache {
    FormatCache(int pEntries) {
      super(pEntries);
    }
    
    public Object miss(Object pKey) 
      throws ItemNotFoundException
    {
      ParsedFormatKey key = (ParsedFormatKey) pKey;
      return new ParsedFormat(key.mFormat, key.mLocale);
    }  
  }

  // a LRU cache of formats
  FormatCache mFormatCache;

  /**
   * Evaluate the droplet.
   */
  public void service(DynamoHttpServletRequest pRequest, 
                      DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException 
  {
    String format = pRequest.getParameter(FORMAT);
    if (format == null) {
      throw new ServletException(ResourceUtils.getMsgResource("formatParameterMissing", 
                  MY_RESOURCE_NAME, sResourceBundle));
    }
    
    // Get a ParsedFormat object from the cache. If it isn't in the cache,
    // it will be created.
    if (mFormatCache == null)
      mFormatCache = new FormatCache(getCacheSize());
    Locale locale;
    Object localeObj = pRequest.getObjectParameter(LOCALE);
    if (localeObj != null && localeObj instanceof Locale)
      locale = (Locale) localeObj;
    else
      locale = pRequest.getLocale();
    ParsedFormatKey key = new ParsedFormatKey(format, locale);
    ParsedFormat parsed;
    try {
      parsed = (ParsedFormat) mFormatCache.get(key);
    } catch (ItemNotFoundException e) {
      // we never throw this in our miss() method; should never occur
      throw new ServletException(e.toString());
    }
    
    // Now use the ParsedFormat to retrieve the named arguments
    // and format them.
    Object[] args = new Object[parsed.mArgNames.length];
    for (int i=0; i < args.length; i++) {
      args[i] = pRequest.getObjectParameter(parsed.mArgNames[i]);
    }
    StringBuffer message = new StringBuffer();
    message = parsed.mMessageFormat.format(args, message, null);
    
    // Render output with the "message" parameter set to the formatted result.
    pRequest.setParameter(MESSAGE, message.toString());
    pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
  }
}
