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


package atg.b2bcommerce.order.processor;

import atg.b2bcommerce.order.CostCenter;
import atg.b2bcommerce.order.B2BPipelineConstants;
import atg.commerce.order.processor.ValidationPipelineArgs;
import java.util.HashMap;
import java.util.Locale;

/**
 * ValidateCostCenterPipelineArgs provides a dictionary for storing
 * arguments to the pipeline processors that validate cost centers,
 * and includes convenience methods for looking up well-known names
 * within that dictionary.
 *
 * @author Matt Landau
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/processor/ValidateCostCenterPipelineArgs.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
 
public class ValidateCostCenterPipelineArgs
  extends ValidationPipelineArgs
{
  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/processor/ValidateCostCenterPipelineArgs.java#2 $$Change: 651448 $";

  //---------------------------------------------------------------------------
  // property: paymentGroup
  //---------------------------------------------------------------------------

  /** Set the cost center being validated. **/
  
  public void setCostCenter(CostCenter pCostCenter) {
    put(B2BPipelineConstants.COST_CENTER, pCostCenter);
  }

  /** Return the cost center being validated. **/
  
  public CostCenter getCostCenter() {
    return (CostCenter)get(B2BPipelineConstants.COST_CENTER);
  }
}
