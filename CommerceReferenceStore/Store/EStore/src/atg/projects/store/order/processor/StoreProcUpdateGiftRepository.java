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
package atg.projects.store.order.processor;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.processor.ProcUpdateGiftRepository;

import atg.projects.store.order.GiftWrapCommerceItem;

import atg.service.pipeline.PipelineResult;

import java.util.ResourceBundle;


/**
 * This extension of ProcUpdateGiftRepository will check if the given commerce item is a gift wrap
 * item, so it doesn't attempt to remove gift wrap from the giftlist.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/processor/StoreProcUpdateGiftRepository.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class StoreProcUpdateGiftRepository extends ProcUpdateGiftRepository {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/processor/StoreProcUpdateGiftRepository.java#2 $$Change: 651448 $";

  /**
   * Success constant.
   */
  private final int SUCCESS = 1;

  /**
   * This method increments the quantityPurchased property for the
   * given item in the given Giftlist by some quantity.
   * This method will also remove the handling instruction, if the
   * giftlist or giftlist item has been removed.  If the handling
   * instruction is removed, the method returns STOP_CHAIN_EXECUTION_AND_ROLLBACK
   *
   * @param pOrder The order containing the gift.
   * @param pHandlingInstructionId - handling inxtruction id
   * @param pShippingGroupId - shipping group id
   * @param pGiftlistId The id of the giftlist to be updated.
   * @param pGiftlistItemId The id of the giftlist item to be updated.
   * @param pCommerceItem The commerce item in the giftlist to be updated.
   * @param pQuantity The quantity purchased
   * @param pBundle resource bundle specific to users locale
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   **/
  protected int updateGiftlistItem(Order pOrder, String pHandlingInstructionId, String pShippingGroupId,
    String pGiftlistId, String pGiftlistItemId, CommerceItem pCommerceItem, long pQuantity, ResourceBundle pBundle,
    PipelineResult pResult) {
    if (pCommerceItem instanceof GiftWrapCommerceItem) {
      return SUCCESS;
    }

    return super.updateGiftlistItem(pOrder, pHandlingInstructionId, pShippingGroupId, pGiftlistId, pGiftlistItemId,
      pCommerceItem, pQuantity, pBundle, pResult);
  }
}
