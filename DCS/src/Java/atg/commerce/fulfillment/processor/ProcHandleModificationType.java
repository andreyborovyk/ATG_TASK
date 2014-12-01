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
 * This processor will route the modification execution based on the modification
`* operation type like add, remove, update
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcHandleModificationType.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcHandleModificationType implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcHandleModificationType.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int ADD_MODIFICATION = 1;
  private final int REMOVE_MODIFICATION = 2;
  private final int UPDATE_MODIFICATION = 3;
   
  //-----------------------------------------------
  public ProcHandleModificationType() {
  }

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The processor calls to the add operation
   * 2 - The processor calls to the remove operation
   * 3 - The processor calls to the update operation
   * 4 - The processor calls to the unsupported operation
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = { ADD_MODIFICATION, REMOVE_MODIFICATION,
                  UPDATE_MODIFICATION};
    return ret;
  } 

  //-----------------------------------------------
  /**
   * This processor will route the modification execution based on the modification
   * operation type like add, remove, update
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
    Modification pModification = (Modification) map.get(PipelineConstants.MODIFICATION);

    if (pModification == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidModificationParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));

    switch(pModification.getModificationType())
    {
    case Modification.ADD :
      {
        return ADD_MODIFICATION;
      }
    case Modification.REMOVE:
      {
        return REMOVE_MODIFICATION;
      }
    }
    
    return UPDATE_MODIFICATION;
  }
    
}
