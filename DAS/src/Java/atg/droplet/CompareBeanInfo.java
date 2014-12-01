/*<ATGCOPYRIGHT>
 * Copyright (C) 2000-2011 Art Technology Group, Inc.
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
 * <p>BeanInfo for the Compare droplet.
 *
 * @author Natalya Cohen
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/CompareBeanInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class CompareBeanInfo extends DropletBeanInfo {
  //-------------------------------------
  // CONSTANTS
  //-------------------------------------
  public static String CLASS_VERSION = 
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/CompareBeanInfo.java#2 $$Change: 651448 $";

  //-------------------------------------
  // FIELDS
  //-------------------------------------

  private final static ParamDescriptor[] sOutputDescriptors = {
  };

  private final static ParamDescriptor[] sParamDescriptors = {
    new ParamDescriptor("obj1", "the first object to compare",
                        Comparable.class, false, false),
    new ParamDescriptor("obj2", "the second object to compare",
                        Comparable.class, false, false),
    new ParamDescriptor("greaterthan", "rendered if the value of obj1 is greater than obj2",
                        DynamoServlet.class, true, true),
    new ParamDescriptor("lessthan", "rendered if the value of obj1 is less than obj2",
                        DynamoServlet.class, true, true),
    new ParamDescriptor("equal", "rendered if the value of obj1 is equal to obj2",
                        DynamoServlet.class, true, true),
    new ParamDescriptor("noncomparable", "rendered if obj1 and obj2 cannot be compared",
                        DynamoServlet.class, true, true),
    new ParamDescriptor("default", 
			"rendered if any of the other oparams is not rendered",
                        DynamoServlet.class, true, true)
  };

  private final static BeanDescriptor sBeanDescriptor = 
  createBeanDescriptor(Compare.class, 
		       null,
		       "This servlet conditionally renders one of its " + 
		       "parameters based on the relationship between the two input params.",
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
