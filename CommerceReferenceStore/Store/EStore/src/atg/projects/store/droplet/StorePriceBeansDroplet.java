/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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

package atg.projects.store.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.UnitPriceBean;
import atg.core.util.Range;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * This droplet generates UnitPriceBeans for the whole order, or for the shipping-group-commerce-item relationship.
 * If an order is specified, there would be generated price beans with quantity equal to 1 (always) for each item in the order specified.
 * If a relationship is specified, price beans for this relationship would be generated, there also would be calculated total amount
 * for the relationthip specified.
 * <br/>
 * This droplet always serves 'output' oparam.
 * If there is 'order' input parameter specified, it would set 'priceBeansMap' output parameter
 * calculated with {@link #generatePriceBeansForOrder(Order)} method.
 * If there is 'relationship' input parameter set, it would set 'priceBeans', 'priceBeansQuantity' and 'priceBeansAmount' output parameters,
 * see {@link #generatePriceBeansForRelationship(ShippingGroupCommerceItemRelationship)} for details.
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/droplet/StorePriceBeansDroplet.java#2 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class StorePriceBeansDroplet extends DynamoServlet {
  private static final String OPARAM_OUTPUT = "output";
  private static final String PARAM_PRICE_BEANS_MAP = "priceBeansMap";
  private static final String PARAM_QUANTITY = "priceBeansQuantity";
  private static final String PARAM_AMOUNT = "priceBeansAmount";
  private static final String PARAM_PRICE_BEANS = "priceBeans";
  private static final String PARAM_RELATIONSHIP = "relationship";
  private static final String PARAM_ORDER = "order";
  /**
   * Class version
   */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/droplet/StorePriceBeansDroplet.java#2 $$Change: 651448 $";
  
  /**
   * Pricing tools.
   */
  protected PricingTools mPricingTools;

  /**
   * @return the pricing tools.
   */
  public PricingTools getPricingTools() {
    return mPricingTools;
  }

  /**
   * @param pPricingTools - the pricing tools to set.
   */
  public void setPricingTools(PricingTools pPricingTools) {
    mPricingTools = pPricingTools;
  }
  
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    Order order = (Order) pRequest.getObjectParameter(PARAM_ORDER);
    ShippingGroupCommerceItemRelationship sgciRelationship =
        (ShippingGroupCommerceItemRelationship) pRequest.getObjectParameter(PARAM_RELATIONSHIP);
    // If there is a relationship parameter, build price beans for it 
    if (sgciRelationship != null) {
      List<UnitPriceBean> priceBeans = generatePriceBeansForRelationship(sgciRelationship);
      pRequest.setParameter(PARAM_PRICE_BEANS, priceBeans);
      double totalAmount = 0;
      long totalQuantity = 0;
      for (UnitPriceBean priceBean: priceBeans) {
        totalAmount += priceBean.getUnitPrice() * priceBean.getQuantity();
        totalQuantity += priceBean.getQuantity();
      }
      pRequest.setParameter(PARAM_AMOUNT, totalAmount);
      pRequest.setParameter(PARAM_QUANTITY, totalQuantity);
    }
    // If there is an order speicifed, build price beans for it
    if (order != null) {
      pRequest.setParameter(PARAM_PRICE_BEANS_MAP, generatePriceBeansForOrder(order));
    }
    pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
  }
  
  /**
   * Generates price beans for a relationship specified.
   * This method takes all commerce item's price infos with range located within relationship's range.
   * For each price info it creates a price bean.
   * @param pRelationship - specifies a shipping-group-commerce-item relationsip to build price beans from.
   * @return list of price beans for the relationship specified.
   */
  @SuppressWarnings("unchecked") // Ok, we know collections we're working with
  protected List<UnitPriceBean> generatePriceBeansForRelationship(ShippingGroupCommerceItemRelationship pRelationship) {
    Range relationshipRange = pRelationship.getRange();
    
    // Only price infos with proper ranges should be used
    return getPricingTools().generatePriceBeans((List<DetailedItemPriceInfo>)pRelationship.getCommerceItem().getPriceInfo().getCurrentPriceDetailsForRange(relationshipRange));
  }
  
  /**
   * Generates price beans for an order specified.
   * This method iterates over all order's commerce items and generates price beans for each item.
   * Each price bean will have a quantity of 1, that is for commerce item with quantity 2 will be created 2 price beans.
   * @param pOrder - order to build price beans from.
   * @return list of price beans mapped by apropriate commerce item ID.
   */
  @SuppressWarnings("unchecked")
  protected Map<String, UnitPriceBean> generatePriceBeansForOrder(Order pOrder) {
    FifoMultiMap result = new FifoMultiMap();
    for (CommerceItem commerceItem: (List<CommerceItem>) pOrder.getCommerceItems()) {
      List<UnitPriceBean> priceBeans = (List<UnitPriceBean>) getPricingTools().generatePriceBeans(commerceItem);
      for (UnitPriceBean priceBean : priceBeans) {
        // split price beans by quantity 1
        long initialQuantity = priceBean.getQuantity();
        priceBean.setQuantity(1);
        for (long i = 0; i < initialQuantity; i++) {
          result.put(commerceItem.getId(), priceBean);
        }
      }
    }
    return result;
  }
  
  /**
   * This HashMap implmementation can contain several values per single key.
   * When a new value is passed into {@link #put(String, UnitPriceBean)} method, it saves this value into an ArrayList mapped by key specified.
   * When some object is obtained from {@link #get(Object)} method, it retrieves an ArrayList mapped by the key specified,
   * returns first object from it and then removes it from the list.
   * @see ArrayList
   */
  @SuppressWarnings("serial")
  private class FifoMultiMap extends HashMap<String, UnitPriceBean> {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/droplet/StorePriceBeansDroplet.java#2 $$Change: 651448 $";

    private Map<String, ArrayList<UnitPriceBean>> mInnerMap = new HashMap<String, ArrayList<UnitPriceBean>>();
    
    @Override
    public UnitPriceBean get(Object pKey) {
      return getOrCreateList((String) pKey).remove(0);
    }

    @Override
    public UnitPriceBean put(String pKey, UnitPriceBean pValue) {
      getOrCreateList(pKey).add(pValue);
      return null;
    }
    
    /**
     * Tries to find an ArrayList inside the inner HashMap, if nothing found, it creates a new instance and puts it into inner Map.
     * @param pKey - key to be used.
     * @return stored ArrayList (if any) or new instance.
     */
    private ArrayList<UnitPriceBean> getOrCreateList(String pKey)
    {
      ArrayList<UnitPriceBean> collection = mInnerMap.get(pKey);
      if (collection == null) {
        collection = new ArrayList<UnitPriceBean>();
        mInnerMap.put(pKey, collection);
      }
      return collection;
    }
  }
}
