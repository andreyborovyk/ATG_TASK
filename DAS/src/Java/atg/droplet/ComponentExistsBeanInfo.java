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
 * <p>BeanInfo for the ComponentExists droplet.
 *
 * @author Matt Landau
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/ComponentExistsBeanInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class ComponentExistsBeanInfo extends DropletBeanInfo
{
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/ComponentExistsBeanInfo.java#2 $$Change: 651448 $";

  //-------------------------------------
  // FIELDS
  //-------------------------------------

  private final static ParamDescriptor[] sParamDescriptors = {
    new ParamDescriptor("path", "the path being examined",
                        String.class, false, false),
    new ParamDescriptor("true", "rendered if path refers to a non-null object",
                        DynamoServlet.class, false, false),
    new ParamDescriptor("false", "rendered if path refers to an object that does not exist",
                        DynamoServlet.class, false, false)
  };

  private final static BeanDescriptor sBeanDescriptor = createBeanDescriptor(
    ComponentExists.class, 
    null,
    "This servlet tests whether a Nucleus path refers to a non-null object.",
    sParamDescriptors);


  /**
   * Returns the BeanDescriptor for this bean, which will in turn 
   * contain ParamDescriptors for the droplet.
   **/

  public BeanDescriptor getBeanDescriptor() {
    return sBeanDescriptor;
  }
}
