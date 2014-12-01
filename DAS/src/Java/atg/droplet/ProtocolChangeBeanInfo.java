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
 * <p>BeanInfo for the ProtocolChange droplet.
 *
 * @author Cynthia Harris
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/ProtocolChangeBeanInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class ProtocolChangeBeanInfo extends DropletBeanInfo {
  //-------------------------------------
  // CONSTANTS
  //-------------------------------------
  public static String CLASS_VERSION = 
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/ProtocolChangeBeanInfo.java#2 $$Change: 651448 $";

  //-------------------------------------
  // FIELDS
  //-------------------------------------

  private final static ParamDescriptor[] sOutputDescriptors = {
    new ParamDescriptor("secureUrl", "The inUrl converted to secure protocol",
                        String.class, false, false),
    new ParamDescriptor("nonSecureUrl", "The inUrl converted to http protocol",
                        String.class, false, false),
  };

  private final static ParamDescriptor[] sParamDescriptors = {
    new ParamDescriptor("inUrl", 
                        "The url (relative or not, must be local) for which you would like to convert the protocol.",
                        String.class, true, false),
    new ParamDescriptor("output", "rendered to when the secureUrl and nonSecureUrls are set",
                        DynamoServlet.class, false, true, sOutputDescriptors),
  };

  private final static BeanDescriptor sBeanDescriptor = 
  createBeanDescriptor(ProtocolChange.class, 
		       null,
		       "This servlet renders the input url converted to secure and non-secure protocols.",
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
