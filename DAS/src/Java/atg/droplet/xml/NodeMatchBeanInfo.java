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

package atg.droplet.xml;

import java.beans.*;
import atg.droplet.*;
import atg.servlet.DynamoServlet;

/**
 * <p>BeanInfo for the NodeMatch droplet.
 *
 * @author Allan Scott
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/xml/NodeMatchBeanInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class NodeMatchBeanInfo extends DropletBeanInfo {
  //-------------------------------------
  // CONSTANTS
  //-------------------------------------
  public static String CLASS_VERSION = 
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/xml/NodeMatchBeanInfo.java#2 $$Change: 651448 $";


  //-------------------------------------
  // FIELDS
  //-------------------------------------

  private final static ParamDescriptor[] sOutputDescriptors = {
    new ParamDescriptor("matched", "Node matched",
                        org.w3c.dom.Node.class, false, false)
  };

  private final static ParamDescriptor[] sParamDescriptors = {
    new ParamDescriptor("node", 
			"The DOM node to be passed to this droplet",
                        org.w3c.dom.Node.class, false, false),

    new ParamDescriptor("match", 
			"The XSL expression to be used to select a node",
                        String.class, false, false),

    new ParamDescriptor("unset", 
			"The oparam to use when the node parameter not set",
                        String.class, true, false),

    new ParamDescriptor("output", 
			"The oparam used when a subnode was matched",
                        DynamoServlet.class, false, true, 
			sOutputDescriptors)
  };
  
  private final static BeanDescriptor sBeanDescriptor = 
    createBeanDescriptor(NodeMatch.class, 
			 null,
			 "Droplet which matches subnodes of an org.w3c.dom.Node",
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
