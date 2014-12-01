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
import atg.commerce.fulfillment.*;

import java.util.*;

/**
 * This processor iterates through the Hashmap object with orders whose shipping groups
 * are ready to ship. It will call to the chain to ship the groups.
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcHandleOrderPendingShipMap.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcHandleOrderPendingShipMap implements PipelineProcessor {
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcHandleOrderPendingShipMap.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final static int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcHandleOrderPendingShipMap() {
  }

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The processor completed
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = {SUCCESS};
    return ret;
  } 

    /**
   * The chain to run from within this processor
   **/
  private String mChainToRun;
  
  public String getChainToRun() {
      return mChainToRun;
  }

  public void setChainToRun(String pChainToRun) {
      mChainToRun = pChainToRun;
  }


  //-----------------------------------------------
  /**
   * Ships the shipping group by calling the corresponding chain.
   *
   * This method requires that an OrderFulfiller object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an OrderFulfiller object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    HardgoodFulfiller of = (HardgoodFulfiller) map.get(PipelineConstants.ORDERFULFILLER);
    HardgoodShipper hs = (HardgoodShipper) map.get(PipelineConstants.HARDGOODSHIPPER);
    HashMap ordersWithShips = (HashMap) map.get(PipelineConstants.ORDERPENDINGSHIPMAP);

    String orderId, shipGroupId = null;
    Iterator orderKeyIterator = null, shipIdIterator = null;

    orderKeyIterator = ordersWithShips.keySet().iterator();

    while (orderKeyIterator.hasNext()) {
        orderId = (String) orderKeyIterator.next();

       if(of.isLoggingDebug())
           of.logDebug("Got order " + orderId);      
      
       try {
           shipIdIterator = ((Collection) ordersWithShips.get(orderId)).iterator();
       }
       catch (NullPointerException npe ) {
           continue;
       }
       
       if (shipIdIterator == null) 
           continue;
       
       while (shipIdIterator.hasNext()) {
           shipGroupId = (String) shipIdIterator.next();
           
           if (shipGroupId == null)
               continue;
           
           if(of.isLoggingDebug())
               of.logDebug("Shipping " + shipGroupId + " in order " + orderId);
           
           HashMap pMap = new HashMap(4);
           pMap.put(PipelineConstants.ORDERFULFILLER, of);
           pMap.put(PipelineConstants.ORDERID, orderId);
           pMap.put(PipelineConstants.SHIPPINGGROUPID, shipGroupId);
           
           try {
               PipelineResult result = 
                   of.getFulfillmentPipelineManager().runProcess(getChainToRun(), pMap);
           }
           catch(RunProcessException e) {
               Throwable p = e.getSourceException();
               if(of.isLoggingError())
                   of.logError(p);  
           }
       }
       
    } // while
    
    return SUCCESS;
  }
    
}
