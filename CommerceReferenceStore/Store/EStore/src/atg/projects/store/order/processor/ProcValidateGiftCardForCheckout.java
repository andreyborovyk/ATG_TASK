/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2011 Art Technology Group, Inc.
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

import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PipelineConstants;

import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;

import atg.nucleus.logging.ApplicationLoggingImpl;

import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.GiftWrapCommerceItem;

import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;


/**
 * This processor validates the order has more than just gift cards.
 *
 * @author ATG
 * @version $Revision: #2 $
 */
public class ProcValidateGiftCardForCheckout extends ApplicationLoggingImpl implements PipelineProcessor {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/processor/ProcValidateGiftCardForCheckout.java#2 $$Change: 651448 $";

  /**
   * Resource bundle name.
   */
  protected static final String RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /**
   * Resource bundle name for user messages.
   */
  protected static final String USER_MSGS_RES_NAME = "atg.commerce.order.UserMessages";

  //-------------------------------------
  // Constants
  //-------------------------------------
  // Resource message keys
  public static final String MSG_INVALID_ORDER_PARAMETER = "InvalidOrderParameter";

  /**
   * Resource bundle.
   */
  protected static ResourceBundle sResourceBundle = LayeredResourceBundle.getBundle(RESOURCE_NAME,
      atg.service.dynamo.LangLicense.getLicensedDefault());

  /**
   * User resource bundle.
   */
  protected static ResourceBundle sUserResourceBundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME,
      java.util.Locale.getDefault());

  /**
   * Gift cards only error resource key.
   */
  protected static final String GIFT_CARDS_ONLY_ERROR = "giftCardsOnlyError";

  /**
   * Success constant.
   */
  protected final int SUCCESS = 1;

  /**
   * This method does the work of this processor Make sure the order has more
   * than just gift cards in it.
   *
   * @param pParam - Pipeline params
   * @param pResult - Pipeline result
   * @return int (SUCCESS or STOP_CHAIN_EXECUTION_AND_ROLLBACK)
   * @throws Exception if an error in handling pipeline process
   */
  public int runProcess(Object pParam, PipelineResult pResult)
    throws Exception {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);

    java.util.Locale locale = (java.util.Locale) map.get(PipelineConstants.LOCALE);
    java.util.ResourceBundle resourceBundle;

    if (locale == null) {
      resourceBundle = sUserResourceBundle;
    } else {
      resourceBundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, locale);
    }

    // check for null parameters
    if (order == null) {
      throw new InvalidParameterException(ResourceUtils.getMsgResource(MSG_INVALID_ORDER_PARAMETER, RESOURCE_NAME,
          sResourceBundle));
    }

    if (!(order instanceof StoreOrderImpl)) {
      return SUCCESS;
    }

    List items = order.getCommerceItems();
   
    if (validateItemsInOrder(items)) {
      return SUCCESS;
    }

    // After looping through all commerce items, we didn't find
    // any that weren't samples. Add error.
    String msg = sUserResourceBundle.getString(GIFT_CARDS_ONLY_ERROR);
    pResult.addError(GIFT_CARDS_ONLY_ERROR, msg);

    return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
  }

  /**
   * This is a utility method that takes a list of CommerceItems and
   * checks to see if there is a non-GiftWrapCommerceItem in the list.
   *
   * @param pItems is a list of CommerceItems
   * @return true if list contains a non-GiftWrapCommerceItem
   */
  public boolean validateItemsInOrder(List pItems) {
    CommerceItem item = null;

    for (int i = 0; i < pItems.size(); i++) {
      item = (CommerceItem) pItems.get(i);

      if (!(item instanceof GiftWrapCommerceItem)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns the valid return codes 1 - The processor completed.
   *
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes() {
    int[] ret = { SUCCESS };

    return ret;
  }
}
