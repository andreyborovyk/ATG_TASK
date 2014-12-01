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

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;

import atg.commerce.CommerceException;

import atg.commerce.order.CommerceIdentifier;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.processor.ProcSavePriceInfoObjects;

import atg.commerce.pricing.AmountInfo;
import atg.commerce.pricing.TaxPriceInfo;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;

import java.beans.IntrospectionException;


/**
 * Extended ATG base class in order to save TaxPriceInfo of commerce items
 * and shipping groups.
 * Commerce item's TaxPriceInfo objects are saved to the CommerceItem's
 * ItemPriceInfo. Shipping group's TaxPriceInfo objects are saved to the ShippingGroup's
 * ShippingPriceInfo.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/processor/StoreProcSavePriceInfoObjects.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class StoreProcSavePriceInfoObjects extends ProcSavePriceInfoObjects {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/processor/StoreProcSavePriceInfoObjects.java#2 $$Change: 651448 $";  
  
  /**
   * Overrides the base savePriceInfo method to save taxPriceInfo into parent
   * price info repository item. 
   */
  protected MutableRepositoryItem savePriceInfo(Order order, CommerceIdentifier ci, 
                                                MutableRepositoryItem piRepItem, String repItemPropName, 
                                                AmountInfo pPriceInfo, MutableRepository mutRep,
                                                OrderManager orderManager) 
    throws RepositoryException, IntrospectionException, PropertyNotFoundException, CommerceException {

    piRepItem = super.savePriceInfo(order, ci, piRepItem, repItemPropName, pPriceInfo,
                                    mutRep, orderManager);
    
    if (piRepItem != null && piRepItem.getItemDescriptor().hasProperty(getTaxPriceInfoProperty()))
      saveTaxPriceInfo(order, ci, pPriceInfo, piRepItem, mutRep, orderManager);
    
    return piRepItem;
  }
  
  /**
   * This method is used to save the price info's tax price info. Can be used to save commerce item's or
   * shipping group's priceInfo's taxPriceInfo.
   *
   * @param pOrder - order
   * @param ci -  commerce identifier
   * @param pPriceInfo - price info object from where the tax price info should be saved
   * @param pPriceInfoRepItem - price info repository item to where tax price info should be saved
   * @param pRepository - repository
   * @param pOrderManager - order manager
   * 
   * @throws RepositoryException if repository error occurs
   * @throws IntrospectionException if introspection error occurs
   * @throws PropertyNotFoundException if property was not found
   * @throws CommerceException if commerce error occurs
   */
  protected void saveTaxPriceInfo(Order pOrder, CommerceIdentifier ci, AmountInfo pPriceInfo, 
                                  MutableRepositoryItem pPriceInfoRepItem, MutableRepository pRepository, 
                                  OrderManager pOrderManager)
    throws RepositoryException, IntrospectionException, PropertyNotFoundException, CommerceException {
    
    if (pPriceInfoRepItem != null){
      MutableRepositoryItem tpiRepItem = 
        (MutableRepositoryItem) pPriceInfoRepItem.getPropertyValue(getTaxPriceInfoProperty());;
      TaxPriceInfo taxPriceInfo = 
        (TaxPriceInfo)DynamicBeans.getPropertyValue(pPriceInfo, getTaxPriceInfoProperty());
  
      tpiRepItem = savePriceInfo(pOrder, ci, tpiRepItem, null, taxPriceInfo,
                                 pRepository, pOrderManager);
  
      pPriceInfoRepItem.setPropertyValue(getTaxPriceInfoProperty(), tpiRepItem);
    }
  }
}
