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

package atg.commerce.fulfillment.processor;

import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;
import atg.commerce.*;
import atg.commerce.fulfillment.*;
import atg.commerce.messaging.*;
import atg.commerce.order.Order;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;

import java.util.*;
import javax.jms.*;
import java.text.*;

/**
 * This processor routes the execution of the id target modification. It will call to different
 * chains based on the modification target type (order, shipping group, relationship, commerce item)
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcHandleModificationTargetType.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcHandleModificationTargetType implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcHandleModificationTargetType.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int ORDER_MODIFICATION = 1;
  private final int SHIPPING_GROUP_MODIFICATION = 2;
  private final int ITEM_MODIFICATION = 3;
  private final int RELATIONSHIP_MODIFICATION = 4;
  private final int MODIFICATION_NOT_SUPPORTED = 5;

  //-----------------------------------------------
  public ProcHandleModificationTargetType() {
  }

  

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The order targeted modification
   * 2 - The shipping groups targeted modification
   * 3 - The item targeted modification
   * 4 - The relationship modification
   * 5 - The modification is not supported
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = {ORDER_MODIFICATION, SHIPPING_GROUP_MODIFICATION, 
                 ITEM_MODIFICATION, RELATIONSHIP_MODIFICATION,
                 MODIFICATION_NOT_SUPPORTED};
    return ret;
  } 

  //-----------------------------------------------
  /**
   * Routes the modification execution based on the modification target type.
   *
   * This method requires that a Modification object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain a Modification object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Modification modification = (Modification) map.get(PipelineConstants.MODIFICATION);
    
    if (modification == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidModificationParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));

    IdTargetModification pModification = (IdTargetModification) modification;

    switch(pModification.getTargetType())
    {
    case Modification.TARGET_ORDER:
      {
        return ORDER_MODIFICATION;
      }
    case Modification.TARGET_SHIPPING_GROUP:
      {
        return SHIPPING_GROUP_MODIFICATION;
      }
    case Modification.TARGET_ITEM:
      {
        return ITEM_MODIFICATION;
      }
    case Modification.TARGET_RELATIONSHIP:
      {
        return RELATIONSHIP_MODIFICATION;
      }
    }
    
    return MODIFICATION_NOT_SUPPORTED;
    
  }
}
