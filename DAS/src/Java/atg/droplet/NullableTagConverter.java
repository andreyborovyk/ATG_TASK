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

import java.util.*;
import java.text.*;

import atg.servlet.DynamoHttpServletRequest;

/**
 * This converter allows a property value to be set to null by returning
 * the special SET_TO_NULL value for an empty String.
 *
 * @author Matthew Sakai
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/NullableTagConverter.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class NullableTagConverter implements TagConverter {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/NullableTagConverter.java#2 $$Change: 651448 $";

  static final String NAME = "Nullable";
  static final String NULLABLE_ATTRIBUTE = "nullable";

  private final static TagAttributeDescriptor[] sTagAttributeDescriptors = {
    new TagAttributeDescriptor(NULLABLE_ATTRIBUTE, 
        "If this attribute is present, an empty String will cause the property value to be set to null",
        false, true),
    /*
     * It would be silly to mark a tag both nullable and required,
     * but the required tag is included here to resolve conflicts.
     */
    new TagAttributeDescriptor(RequiredTagConverter.REQUIRED_ATTRIBUTE, 
        "This property actually doesn't do anything",
        true, false),
  };

  public TagAttributeDescriptor [] getTagAttributeDescriptors() {
    return sTagAttributeDescriptors;
  }

  public String getName() {
    return NAME;
  }

  public Object convertStringToObject(DynamoHttpServletRequest pRequest,
  				      String pValue, Properties pAttributes) 
     throws TagConversionException {
    if (pValue == null || pValue.trim().length() == 0) {
      return TagConverterManager.SET_AS_NULL;
    }

    return pValue;
  }

  public String convertObjectToString(DynamoHttpServletRequest pRequest,
  				      Object pValue, Properties pAttributes)
     throws TagConversionException {

     // Be sure that if the Object is null, we pass back a valid String.
    if (pValue == null) return "";
    return pValue.toString();
  }
}
