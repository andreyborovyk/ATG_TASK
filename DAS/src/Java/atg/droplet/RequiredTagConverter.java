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
import atg.core.util.ResourceUtils;

/**
 * Verifies that all converted values have values that are not all
 * "whitespace".  If they are just whitespace, an exception is thrown.
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/RequiredTagConverter.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class RequiredTagConverter implements TagConverter {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/RequiredTagConverter.java#2 $$Change: 651448 $";

  static final String NAME = "Required";
  static final String REQUIRED_ATTRIBUTE = "required";
  static final String MY_RESOURCE_NAME = "atg.droplet.DropletResources";
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME);

  private final static TagAttributeDescriptor[] sTagAttributeDescriptors = {
    new TagAttributeDescriptor(REQUIRED_ATTRIBUTE, 
        "An attribute specified no value if the this value must be contain non-white space to be valid",
        false, true),
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
      String msg = ResourceUtils.getMsgResource(
                      "missingRequiredValue", MY_RESOURCE_NAME, 
                      sResourceBundle);
      throw new TagConversionException(msg, "missingRequiredValue");
    }
    return pValue;
  }

  public String convertObjectToString(DynamoHttpServletRequest pRequest,
  				      Object pValue, Properties pAttributes)
     throws TagConversionException {
     /*
      * XXX - This makes it difficult to build input tags that hook onto 
      * properties that just haven't been set yet.
    if (pValue == null || (pValue instanceof String && ((String)pValue).trim().length()==0))
      throw new TagConversionException("Missing or empty value supplied for a required field", "missingRequiredValue");
    */
    if (pValue == null) return null;
    return pValue.toString();
  }
}
