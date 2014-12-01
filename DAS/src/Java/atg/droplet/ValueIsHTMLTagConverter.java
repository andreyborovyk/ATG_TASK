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
 * This is a dummy converter which passes through values without any
 * conversion.  It is used to bypass the EscapeHTMLTagConverter which is
 * the default for valueof tags.
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/ValueIsHTMLTagConverter.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ValueIsHTMLTagConverter implements TagConverter {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/ValueIsHTMLTagConverter.java#2 $$Change: 651448 $";

  static final String NAME = "ValueIsHTML";
  static final String VALUEISHTML_ATTRIBUTE = "valueishtml";

  private final static TagAttributeDescriptor[] sTagAttributeDescriptors = {
    new TagAttributeDescriptor(VALUEISHTML_ATTRIBUTE, 
        "Performs object to string conversion without escaping HTML special characters (i.e. a pass-through converter)",
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
    return pValue;
  }

  public String convertObjectToString(DynamoHttpServletRequest pRequest,
  				      Object pValue, Properties pAttributes)
     throws TagConversionException {
    if (pValue == null) return null;
    return pValue.toString();
  }
}
