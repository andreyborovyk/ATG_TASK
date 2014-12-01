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
 * <p>BeanInfo for the TableForEach droplet.
 *
 * @author Natalya Cohen
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/TableForEachBeanInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class TableForEachBeanInfo extends DropletBeanInfo {
  //-------------------------------------
  // CONSTANTS
  //-------------------------------------
  public static String CLASS_VERSION = 
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/TableForEachBeanInfo.java#2 $$Change: 651448 $";

  //-------------------------------------
  // FIELDS
  //-------------------------------------

  private final static ParamDescriptor[] sOutputDescriptorsIter = {
    new ParamDescriptor("index", "loop index (0-based)",
                        Integer.class, false, false),
    new ParamDescriptor("count", "loop count (1-based)",
                        Integer.class, false, false),
    new ParamDescriptor("columnIndex", "column index (0-based)",
                        Integer.class, false, false),
    /*
     * The "element" output parameter is defined with the same type as the
     * "array" input parameter with this construct.
     */
    new ArrayElementParamDescriptor("element", "current array element",
                        Object.class, false, false, "array"),
    new ParamDescriptor("key", "current key if the array is a Dictionary",
                        Object.class, true, false)
  };

  private final static ParamDescriptor[] sOutputDescriptorsRowStartEnd = {
    new ParamDescriptor("rowIndex", "row index (0-based)",
			Integer.class, false, false)
  };

  private final static ParamDescriptor[] sOutputDescriptorsAll = 
  merge(sOutputDescriptorsIter, sOutputDescriptorsRowStartEnd);

  private final static ParamDescriptor[] sParamDescriptors = {
    new ParamDescriptor("array", "array, Collection, Enumeration, Iterator, Map or Dictionary to iterate through",
                        Object.class, false, false),
    new ParamDescriptor("numColumns", "number of columns in the table",
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
    new ParamDescriptor("outputStart", "rendered before any output tags, if array is not empty",
                        DynamoServlet.class, true, true),
    new ParamDescriptor("outputEnd", "rendered after any output tags, if array is not empty",
                        DynamoServlet.class, true, true),
    new ParamDescriptor("outputRowStart", "rendered before any output tags for each row",
                        DynamoServlet.class, true, true, sOutputDescriptorsRowStartEnd),
    new ParamDescriptor("outputRowEnd", "rendered after any output tags for each row",
                        DynamoServlet.class, true, true, sOutputDescriptorsRowStartEnd),
    new ParamDescriptor("output", "rendered once for each array element",
                        DynamoServlet.class, false, true, sOutputDescriptorsAll),
    new ParamDescriptor("empty", "rendered if array is empty",
                        DynamoServlet.class, true, true)
  };

  private final static BeanDescriptor sBeanDescriptor = 
  createBeanDescriptor(TableForEach.class, 
		       "atg.ui.document.wizard.TableForEachDropletWizard",
		       "This servlet renders an output for each element " + 
		       "in the specified array, in table-like format.",
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
