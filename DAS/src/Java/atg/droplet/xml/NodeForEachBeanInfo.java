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
 * <p>BeanInfo for the NodeForEach droplet.
 *
 * @author Allan Scott
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/xml/NodeForEachBeanInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class NodeForEachBeanInfo extends DropletBeanInfo {
  //-------------------------------------
  // CONSTANTS
  //-------------------------------------
  public static String CLASS_VERSION = 
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/xml/NodeForEachBeanInfo.java#2 $$Change: 651448 $";

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
    new ParamDescriptor("element", "current selected element",
                        org.w3c.dom.Node.class, false, false)
  };

  private final static ParamDescriptor[] sParamDescriptors = {  
    new ParamDescriptor("node", 
			"The DOM node to be passed to this droplet",
                        org.w3c.dom.Node.class, false, false),
    new ParamDescriptor("select",
			"The pattens to be used to select the nodes to be iterated over",
			String.class, false, false),
    new ParamDescriptor("sortProperties", 
	"Optionally specifies how elements are sorted.  If element is a String " +
	"or Number, '+' is ascending, '-' is decending order.  If element is a " +
	"bean, specify a list of properties to sort by for primary-sort,secondary-sort,... " +
	"(e.g. '+p1,+p2,-p3')",
                        String.class, true, false),
    new ParamDescriptor("reverseOrder",
			"if true will reverse the order of the input array.  Does not work with sortProperties",
			Boolean.class, true, false),
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
    createBeanDescriptor(NodeForEach.class,
			 null,
			 "Droplet which iterates over selected subnodes of an org.w3c.dom.Node",
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






