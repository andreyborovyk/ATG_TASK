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

package atg.commerce.order.purchase;

import atg.servlet.DynamoServlet;
import atg.droplet.DropletBeanInfo;
import atg.droplet.ParamDescriptor;
import java.beans.BeanDescriptor;

/**
 * This BeanInfo describes the <tt>RepriceOrder</tt> droplet.<p>
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/RepriceOrderBeanInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class RepriceOrderBeanInfo extends DropletBeanInfo
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/RepriceOrderBeanInfo.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------
  public final static String FUNCTIONAL_COMPONENT_CATEGORY = "Servlet Beans";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  private final static ParamDescriptor [] sOutputDescriptors = {
    new ParamDescriptor("pipelineResult", "the pipelineResult from chain execution", String.class, true, false),
    new ParamDescriptor("exception", "the exception object if an exception occurs", Exception.class, true, false)
      };

  private final static ParamDescriptor [] sParamDescriptors = {
    new ParamDescriptor("success", "rendered when chain successfully executes", DynamoServlet.class, true, true, sOutputDescriptors),
    new ParamDescriptor("successWithErrors", "rendered when chain successfully executes, but the PipelineResult object contains errors", DynamoServlet.class, true, true, sOutputDescriptors),
    new ParamDescriptor("failure", "rendered when chain execution fails", DynamoServlet.class, true, true),
    new ParamDescriptor("pipelineManager", "optionally provides the PipelineManager to use for chain exeuction", atg.service.pipeline.PipelineManager.class, true, false),
    new ParamDescriptor("chainId", "optionally provides the id of the chain to execute", String.class, true, false),
    new ParamDescriptor("pricingOp", "the pricing operation to use", String.class, false, false)
      };

  private final static BeanDescriptor sBeanDescriptor =
    createBeanDescriptor(RepriceOrder.class,
                         null,
                         "This Dynamo Servlet Bean is used to execute a PipelineChain that reprices the Order.",
                         sParamDescriptors);
  
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  /**
   * Returns the BeanDescriptor for this bean, which will in turn 
   * contain ParamDescriptors for the droplet.
   **/
  public BeanDescriptor getBeanDescriptor() {
    return sBeanDescriptor;
  }
}   // end of class
