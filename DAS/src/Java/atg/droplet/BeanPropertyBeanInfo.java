/*<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
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

import java.beans.*;
import atg.servlet.DynamoServlet;

/**
 * <p>BeanInfo for the BeanProperty droplet.
 *
 * @author Matt Landau
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/BeanPropertyBeanInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class BeanPropertyBeanInfo extends DropletBeanInfo
{
  //-------------------------------------
  // CONSTANTS
  //-------------------------------------
  public static String CLASS_VERSION = 
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/BeanPropertyBeanInfo.java#2 $$Change: 651448 $";

  public final static String FUNCTIONAL_CATEGORY = "Servlet Beans";
  public final static String FEATURE_CATEGORY = "Product Comparison";

  //-------------------------------------
  // FIELDS
  //-------------------------------------

  private final static ParamDescriptor[] sOutputDescriptors = {
    new ParamDescriptor("propertyValue", "The value of the named property.",  Object.class, false, false),
  };

  private final static ParamDescriptor[] sParamDescriptors = {
    new ParamDescriptor("bean", "The Java bean whose property is being set or returned.",
                        String.class, false, false),
    new ParamDescriptor("propertyName", "The name of the property to set or return.",
                        String.class, false, false),
    new ParamDescriptor("propertyValue", "The new value for the property, if the servlet bean is being used to set the value.",
                        Object.class, false, false),

    new ParamDescriptor("output", "Rendered after the property value is set or retrieved.",
                        DynamoServlet.class, true, true, sOutputDescriptors),
  };

  private final static BeanDescriptor sBeanDescriptor = createBeanDescriptor(
    BeanProperty.class, 
    null,
    "This servlet sets or gets the value of a named property for a specified Java bean.",
    sParamDescriptors,
    FUNCTIONAL_CATEGORY,
    FEATURE_CATEGORY);

  /**
   * Returns the BeanDescriptor for this bean, which will in turn 
   * contain ParamDescriptors for the droplet.
   **/
  
  public BeanDescriptor getBeanDescriptor() {
    return sBeanDescriptor;
  }
}
