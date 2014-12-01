/*<ATGCOPYRIGHT>
 * Copyright (C) 1999-2010 Art Technology Group, Inc.
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
package atg.projects.store.order.processor;

import atg.commerce.CommerceException;

import atg.commerce.order.*;
import atg.commerce.order.processor.ProcVerifyOrderAddresses;

import atg.core.util.Address;
import atg.core.util.ResourceUtils;

import atg.service.pipeline.PipelineResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * This class extends ProcVerifyOrderAddresses to verify only shipping-group addresses.  This
 * is so shipping address verification can take place immediately after the user specifies the
 * shipping addresses, eliminating the UI awkwardness that takes place when a user is informed
 * of an invalid shipping address after specifying billing information.
 *
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/processor/ProcVerifyShippingAddresses.java#3 $$Change: 635816 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class ProcVerifyShippingAddresses extends ProcVerifyOrderAddresses {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/processor/ProcVerifyShippingAddresses.java#3 $$Change: 635816 $";

  /**
   * Resource bundle name.
   */
  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  //-------------------------------------
  // Constants
  //-------------------------------------
  // Resource message keys
  public static final String MSG_INVALID_ORDER_PARAMETER = "InvalidOrderParameter";
  public static final String MSG_INVALID_ORDER_MANAGER_PARAMETER = "InvalidOrderManagerParameter";
  public static final String MSG_INVALID_ADDRESS = "InvalidAddress";

  /** Resource Bundle. **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME,
      atg.service.dynamo.LangLicense.getLicensedDefault());

  /**
   * Success constant.
   */
  private final int SUCCESS = 1;

  /**
   * Creates a new ProcVerifyShippingAddresses object.
   */
  public ProcVerifyShippingAddresses() {
  }

  /**
   * This method executes the address verification. It searches through the
   * ShippingGroup and PaymentGroup lists in the Order for HardgoodShippingGroup
   * and CreditCard objects. It then gets the address from them and calls
   * verifyAddress().
   *
   * This method requires that an Order and an OrderManager object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order and OrderManager object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult)
    throws Exception {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    OrderManager orderManager = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);

    // check for null parameters
    if (order == null) {
      throw new InvalidParameterException(ResourceUtils.getMsgResource(MSG_INVALID_ORDER_PARAMETER, MY_RESOURCE_NAME,
          sResourceBundle));
    }

    if (orderManager == null) {
      throw new InvalidParameterException(ResourceUtils.getMsgResource(MSG_INVALID_ORDER_MANAGER_PARAMETER,
          MY_RESOURCE_NAME, sResourceBundle));
    }

    List shippingGroups = order.getShippingGroups();
    int sgcount = shippingGroups.size();
    List hardgoodSGList = new ArrayList(sgcount);
    HardgoodShippingGroup hgsg;
    Address addr1;
    Address addr2;
    String addrId;
    Object o;

    // find all the HardgoodShippingGroup objects
    sgcount = 0;

    Iterator iter = shippingGroups.iterator();

    while (iter.hasNext()) {
      o = iter.next();

      if (o instanceof HardgoodShippingGroup) {
        // skip empty shipping groups
        if (((HardgoodShippingGroup)o).getCommerceItemRelationshipCount() == 0)
          continue;
        
        hardgoodSGList.add(o);
        sgcount++;
      }
    }

    for (int i = 0; i < sgcount; i++) {
      hgsg = (HardgoodShippingGroup) hardgoodSGList.get(i);

      if (hgsg != null) {
        addr1 = hgsg.getShippingAddress();
        addr2 = null;
        addrId = hgsg.getId();
      } else { // should never happen
        throw new CommerceException(ResourceUtils.getMsgResource(MSG_INVALID_ADDRESS, MY_RESOURCE_NAME, sResourceBundle));
      }

      verifyAddress(addrId, addr1, addr2, pResult, orderManager);
    }

    if (pResult.hasErrors()) {
      return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
    }

    return SUCCESS;
  }
}
