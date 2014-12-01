/*<ATGCOPYRIGHT>
 * Copyright (C) 1999-2011 Art Technology Group, Inc.
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

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import atg.servlet.DynamoHttpServletRequest;

/**
 * This class contains a set of static methods which you can use to
 * register and use TagConverters.  TagConverters are used to perform
 * String to Object and Object to String conversion in the valueof, input
 * and param tags.
 * <p>
 * To add your own converter, follow these steps:
 * <ul>
 * <li>Create a new class which implements the TagConverter interface.
 * This class must implement four methods: 
 *   <ul>
 *   <li><pre>getName</pre>
 *   Returns the name of your converter.  This is the
 *   name that is used as an argument to the converter attribute in a tag.  For
 *   example, if your getName method returned "currency", you'd define a
 *   tag like:
 *   <ul>
 *     <pre>&lt;valueof param="myPrice" converter="currency" &gt;</pre>
 *   </ul>
 *   <li><pre>getTagAttributeDescriptors</pre>Returns the list of attribute
 *   arguments that this converter takes.  These attributes are just listed
 *   along with the "converter" attribute in your valueof, input, or param
 *   tag.  These attributes can mark themselves as optional if they are not
 *   required.  For example, the currency converter above might take a locale
 *   attribute which is optional (the default behavior if this attribute is
 *   missing would be to use the locale associated with the current request).  
 *   <p>
 *   A TagAttributeDescriptor also can mark itself as "automatic" which means 
 *   that this attribute implies the use of this converter, even if the 
 *   converter attribute is not specified.  For example, if the currency converter
 *   defined a "currency" attribute that was automatic, you can use this
 *   converter in your page with: 
 *   <ul>
 *     <pre>&lt;valueof param="myPrice" currency &gt;</pre>
 *   </ul>
 *   It is an error to have more than one converter registered with the 
 *   same automatic attribute.  You can replace an existing converter by
 *   registering a new one with the same name (after the existing converter
 *   has already been registered).
 *   <p>
 *   It is legal to define a TagConverter which takes the same attribute as
 *   another TagConverter as long as it is not ambiguous which converter to 
 *   use for a given tag.  The "required" attribute is an automatic attribute 
 *   for the RequiredTagConverter.  The DateTagConverter has "date" as an
 *   automatic attribute and "required" as an optional attribute.  If you use
 *   both "date" and "required" in the same tag, the DateTagConverter is
 *   used.  If you use just "required", the RequiredTagConverter is used.
 *   <li><pre>convertStringToObject</pre>
 *   This method is called in two 
 *   circumstances.  If you are using an input tag, when that form value is
 *   submitted, this method is called before the setX method of your bean is
 *   called.  It is the job of your convertStringToObject method to generate
 *   the Object value for use in the setX method.  It can throw a
 *   TagConversionException if an error occurs during the conversion process.
 *   This method is also called if you use this converter with a param tag
 *   when the value of the parameter is defined.
 *   <li><pre>convertObjectToString</pre>
 *   This method is called in two
 *   situations.  When you use a converter in a valueof tag, this method is
 *   used to convert the Object value into a String value before displaying
 *   it.  Also, when you use this tag in an input tag with a bean attribute
 *   and no existing value attribute, this method is called to fill in the
 *   value attribute with the current value of the bean property.
 * </ul>
 * 
 * <li>Call the TagConverterManager.registerTagConverter method with an
 * instance of this class.  Note that this call must be made before your
 * converter is used by a jhtml page.  Make sure that you add
 * this code to a class which gets initialized during the startup process
 * of your server (i.e. is in the Initial services list).
 * <li>Add a &lt;valueof&gt;, &lt;input&gt;, or &lt;param&gt; tag to the
 * page which uses this converter.
 * </ul>
 *
 * @see atg.droplet.TagConverter
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/TagConverterManager.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class TagConverterManager {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/TagConverterManager.java#2 $$Change: 651448 $";

  // This is a special object to tell EventSenders to set a property
  // value to null, since a null value already means do not set.
  public static final Object SET_AS_NULL = new Object();

  static Dictionary sConvertersByName = new Hashtable();
  static Dictionary sConvertersByAttributeName = new Hashtable();

  /**
   * Register the default converters
   */
  static {
    registerTagConverter(new DateTagConverter());
    registerTagConverter(new NumberTagConverter());
    registerTagConverter(new NullableTagConverter());
    registerTagConverter(new CurrencyTagConverter());
    registerTagConverter(new RequiredTagConverter());
    registerTagConverter(new EscapeHTMLTagConverter());
    registerTagConverter(new ValueIsHTMLTagConverter());
    registerTagConverter(new CreditCardTagConverter());
    registerTagConverter(new CurrencyConversionTagConverter());
    registerTagConverter(new EuroTagConverter());
    registerTagConverter(new MapTagConverter());
    registerTagConverter(new ArrayTagConverter());
    registerTagConverter(new IntArrayTagConverter());
  }

  /**
   * Adds a new TagConverter to the global list of converters.  Converters
   * are registered by name as specified in their getName method.  If 
   * a converter of an existing name is already registered, this mechanism
   * replaces that converter with this new converter.
   */
  public static void registerTagConverter(TagConverter pConverter) {
    sConvertersByName.put(pConverter.getName().toLowerCase(), pConverter);

    TagAttributeDescriptor [] descs = pConverter.getTagAttributeDescriptors();
    for (int i = 0; i < descs.length; i++) {
      if (descs[i].isAutomatic()) {
        TagConverter oldt = (TagConverter) 
           sConvertersByAttributeName.put(descs[i].getName().toLowerCase(), pConverter);
        if (oldt != null) { 
          System.err.println("Replaced automatic TagConverter: " + oldt.getName() + " with: " +
          	    pConverter.getName() + " for attribute: " + 
          	    descs[i].getName());
        }
      }
    }
  }

  /**
   * Returns the TagConverter for a specified set of attributes.  If no
   * applicable converter is found, null is returned.
   */
  public static TagConverter getTagConverterByName(String pConverterName) {
    return (TagConverter) sConvertersByName.get(pConverterName.toLowerCase());
  }

  /**
   * Returns the TagConverter registered for a specific attribute name.
   * If no converter is registered for that attribute, null is returned.
   */
  public static TagConverter getTagConverterByAttribute(String pAttributeName) {
    return (TagConverter) sConvertersByAttributeName.get(pAttributeName.toLowerCase());
  }

  /**
   * Returns true if this is an attribute (either required or optional)
   * for this converter.
   */
  public static boolean isTagAttribute(TagConverter pCvt, String pAttributeName) {
    TagAttributeDescriptor [] ds = pCvt.getTagAttributeDescriptors();
    for (int i = 0; i < ds.length; i++) {
      if (ds[i].getName().equalsIgnoreCase(pAttributeName)) return true;
    }
    return false;
  }

  /**
   * This method converts Strings to Objects swallowing the conversion
   * exception so that it can be used in a class constructor.  It is only
   * intended to be used in code generated by the PageCompiler.
   * <p>
   * The pReq argument can be null if there is no request with which
   * to implement the conversion.  If you are using the request to 
   * access the locale with which to perform the conversion, use the
   * default locale in this case.
   */
  public static Object convertStringToObject(TagConverter pCvt, 
  					     DynamoHttpServletRequest pReq,
  					     String pValue,
  					     Properties pAttributes) {
    try {
      return pCvt.convertStringToObject(pReq, pValue, pAttributes);
    }
    catch (TagConversionException exc) {
      return exc.toString();
    }
  }

}
