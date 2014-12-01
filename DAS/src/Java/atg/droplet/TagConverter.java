/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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

import java.util.Properties;
import atg.servlet.DynamoHttpServletRequest;

/**
 * This interface is implemented by objects that perform String to Object and
 * Object to String conversion using attributes in JHTML tags (valueof, input,
 * etc.).  To add your own TagConverter (like the date, number, required,
 * valueishtml converters), you write a class which implements this interface
 * and then register that class with the TagConverterManager.
 * <p>
 * TagConverters play an important role in both validating and converting
 * values submitted through HTML forms.  They can also be used to flexibly 
 * format values of various types when converting object values such as 
 * dates, numbers, prices, etc. into values displayed to the user.
 * The use of TagConverters can greatly improve the reuse and maintainability
 * of your code and to avoid duplicating logic in each of your Java beans.
 * It is useful to view TagConverters as playing the role of the controllers
 * in the model/view/controller programming paradigm.
 * <p>
 * For more details on writing a converter @see atg.droplet.TagConverterManager.
 * 
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/TagConverter.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public interface TagConverter {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/TagConverter.java#2 $$Change: 651448 $";

  /**
   * Returns the unique name for the TagConverter.  This name can be used by 
   * the TagConverterManager to get a handle to this converter.  When you
   * specify the use of a TagConverter using the converter attribute in 
   * a tag, you use this name to refer to the TagConverter.  TagConverter
   * names are case insensitive (as is the convention in HTML).
   */
  public String getName();

  /**
   * Returns the list of TagAttributeDescriptors which are used by this
   * converter.  Each TagAttributeDescriptor describes an attribute which
   * can be used by this TagConverter.  Attributes can be optional and they
   * can also be automatic.  An automatic attribute implies the use of
   * this converter if that attribute appears in the tag (e.g. date=".."
   * implies the use of the DateTagConverter).
   */
  public TagAttributeDescriptor [] getTagAttributeDescriptors();

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
     throws TagConversionException;

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
     throws TagConversionException;
}
