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
 * <p>BeanInfo for the ErrorMessageForEach droplet.
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/ErrorMessageForEachBeanInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class ErrorMessageForEachBeanInfo extends DropletBeanInfo {
  //-------------------------------------
  // CONSTANTS
  //-------------------------------------
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/ErrorMessageForEachBeanInfo.java#2 $$Change: 651448 $";

  //-------------------------------------
  // FIELDS
  //-------------------------------------

  private final static ParamDescriptor[] sOutputDescriptors = {
    new ParamDescriptor("index", "loop index (0-based)",
                        Integer.class, false, false),
    new ParamDescriptor("count", "loop count (1-based)",
                        Integer.class, false, false),
    /*
     * The "element" output parameter is defined with the same type as the
     * "array" input parameter with this construct.
     */
    new ParamDescriptor("message", "text of the (possibly translated) error message to display",
                        Object.class, false, false),
    new ParamDescriptor("propertyName", "name of the property (if any) that caused this exception",
                        Object.class, false, false)
  };

  private final static ParamDescriptor[] sParamDescriptors = {
    new ParamDescriptor("exceptions", 
			"array, Collection, Enumeration, Iterator, Map or Dictionary to iterate through",
                        Object.class, true, false),
    new ParamDescriptor("messageTable", "A list of mappings between errorCode values and string messages to substitute for this error code",
	 		String.class, true, false),
    new ParamDescriptor("propertyNameTable", "A list of mappings between propertyName values and names to substitute for this property name",
	 		String.class, true, false),
    new ParamDescriptor("outputStart", 
			"rendered before any output tags, if array is not empty",
                        DynamoServlet.class, true, true),
    new ParamDescriptor("outputEnd", 
			"rendered after any output tags, if array is not empty",
                        DynamoServlet.class, true, true),
    new ParamDescriptor("output", "rendered once for each array element",
                        DynamoServlet.class, false, true, sOutputDescriptors),
    new ParamDescriptor("empty", "rendered if array is empty",
                        DynamoServlet.class, true, true)
  };

  private final static BeanDescriptor sBeanDescriptor = 
  createBeanDescriptor(ErrorMessageForEach.class, null, 
          "This servlet displays the list of errors that occurred during the most recent form submission",
	  sParamDescriptors);

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
