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

import java.beans.*;
import atg.servlet.DynamoServlet;

/**
 * <p>BeanInfo for the Format droplet.
 *
 * @author Norris Boyd
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/FormatBeanInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class FormatBeanInfo extends DropletBeanInfo {
  //-------------------------------------
  // CONSTANTS
  //-------------------------------------
  public static String CLASS_VERSION = 
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/FormatBeanInfo.java#2 $$Change: 651448 $";

  //-------------------------------------
  // FIELDS
  //-------------------------------------

  private final static ParamDescriptor[] sOutputDescriptors = {
    new ParamDescriptor("message", "the result of the format substitution",
                        String.class, false, false)
  };

  private final static ParamDescriptor[] sParamDescriptors = {
    new ParamDescriptor("format", "format string containing parameters" +
                         " enclosed in {...}",
                        String.class, false, false),
    // The ParamDescriptor doesn't really work for parameters that 
    // are arbitrarily named like the parameters for the Format droplet.
    new ParamDescriptor("<named parameters>", "parameters of name chosen by" +
                         " the page author to match names inside curly braces" +
                         " in the format argument",
                        Object.class, false, false),
    new ParamDescriptor("locale", "Locale used for formatting",
                        java.util.Locale.class, true, false),
    new ParamDescriptor("output", "rendered with the result of the format" +
                         " substitution in the variable 'message'",
                        String.class, false, false)
  };

  private final static BeanDescriptor sBeanDescriptor = 
  createBeanDescriptor(Format.class, 
		       null,
		       "This servlet uses java.text.MessageFormat to set a message" +
                " string parameter that can be used when the output parameter" +
                " is rendered.",
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
