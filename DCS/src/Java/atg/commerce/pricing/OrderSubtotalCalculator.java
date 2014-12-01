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

package atg.commerce.pricing;

import atg.commerce.order.*;
import atg.repository.RepositoryItem;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;

import java.util.*;
import java.text.MessageFormat;

/**
 * Computes the <code>rawSubtotal</code> and <code>amount</code> of an OrderPriceInfo
 * that corresponds to
 * the input Order.  Unlike the many discount calculators, there is no rule that
 * determines whether the subtotal should be calculated.  The Order's subtotal is
 * always calculated by adding up the <code>amount</code>s of the ItemPriceInfos that
 * the order contains.
 * If a pricing model (discount) is passed in, it is ignored.
 *
 * @beaninfo
 *   description: An OrderPricingCalculator that calculates the subtotal of an order
 *   attribute: functionalComponentCategory Pricing Calculators
 *   attribute: featureComponentCategory
 *
 * @author Joshua Spiewak
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/OrderSubtotalCalculator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class OrderSubtotalCalculator extends GenericService implements OrderPricingCalculator
{
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/OrderSubtotalCalculator.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  public static final String DETAILED_ITEM_PRICE_AMOUNT_PROPERTY = "amount";
  private static final String PERFORM_MONITOR_NAME="OrderSubtotalCalculator";

  //-------------------------------------
  /** Properties */


  //-------------------------------------
  // property: PricingTools
  /** the PricingTools service to consult for pricing help */
  PricingTools mPricingTools;

  /**
   * the PricingTools service to consult for pricing help
   * @beaninfo description: the PricingTools service to consult for pricing help
   * @param pPricingTools new value to set
   */
  public void setPricingTools(PricingTools pPricingTools)
  {mPricingTools = pPricingTools;}

  /**
   * the PricingTools service to consult for pricing help
   * @beaninfo description: the PricingTools service to consult for pricing help
   * @return property PricingTools
   */
  public PricingTools getPricingTools()
  {return mPricingTools;}


  //-------------------------------------
  // property: OrderManager
  /** order manager to consult for various order-related functions */
  OrderManager mOrderManager;

  /**
   * order manager to consult for various order-related functions
   * @beaninfo description: order manager to consult for various order-related functions
   * @param pOrderManager new value to set
   */
  public void setOrderManager(OrderManager pOrderManager)
  {mOrderManager = pOrderManager;}

  /**
   * order manager to consult for various order-related functions
   * @beaninfo description: order manager to consult for various order-related functions
   * @return property OrderManager
   */
  public OrderManager getOrderManager()
  {return mOrderManager;}


  public OrderSubtotalCalculator() {
  }

  //-------------------------------------
  /**
   * Price a single order within a context.
   * This implementation simply calculates the subtotal of the order by adding
   * the cost of each item in the order.
   *
   * @see atg.commerce.pricing.PricingTools.#isShippingSubtotalUsesAverageItemPrice
   *
   * @param pPriceQuote OrderPriceInfo representing the current price quote for the order
   * @param pOrder The order to price
   * @param pPricingModel A RepositoryItems representing a PricingModel
   * @param pProfile The user's profile
   * @param pExtraParameters A Map of extra parameters to be used in the pricing, may be null
   */
  public void priceOrder(OrderPriceInfo pPriceQuote, Order pOrder, RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
    throws PricingException
  {
    String perfName = "priceOrder";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    boolean perfCancelled = false;

    
    
    try {

      double subTotal = 0.0;

      // go through each shipping group, calculating the cost of the items in each
      // add those numbers to get the order's overall total.

      synchronized (pOrder) {

        // if there are no shippingGroups, compute the Order's subtotal the old way
        if (pOrder.getShippingGroups() == null || pOrder.getShippingGroups().isEmpty()) {

          if (isLoggingDebug()) {
            logDebug("No shipping groups. Computing order subtotal via its items");
          }

          subTotal = calculatePriceFromOrderItems(pOrder);

        }
        // if there are no relationships in any shippinggroup, compute the order's subtotal
        // the old way
        else if (! checkRelationships(pOrder)) {
          subTotal = calculatePriceFromOrderItems(pOrder);
        }
        else {

          // otherwise, go through each shippinggroup and add up the cost of each
          // group's items.  Remember the subtotal cost of each group.

          List shippingGroups = pOrder.getShippingGroups();
          Iterator groupIterator = shippingGroups.iterator();

          if (isLoggingDebug()) {
            logDebug("Order has : " + shippingGroups.size() + " shippinggroups");
          }

          // remember how many of each item has been processed,
          // so that we can add remainders
          Map processedItems = new HashMap();
          // remember the amounts so we can get leftovers
          Map runningTotals = new HashMap();

          while (groupIterator.hasNext()) {
            ShippingGroup group = (ShippingGroup) groupIterator.next();
            double groupSubtotal = calculateShippingGroupSubtotal(pPriceQuote, pOrder, group, processedItems, runningTotals, pLocale, pProfile, pExtraParameters);
            // add the group total to the order's total
            subTotal += groupSubtotal;
    
          } // end for each group

        } // end else compute order via shipping

      } // end synch

      subTotal = getPricingTools().round(subTotal);
      
      if (isLoggingDebug()) {
        logDebug("Order's subtotal : " + subTotal);
      }

      pPriceQuote.setRawSubtotal(subTotal);
      pPriceQuote.setAmount(subTotal);

      // initialize the adjustments list
      List adjustments = pPriceQuote.getAdjustments();

      if (adjustments.size() > 0) {
        adjustments.clear();
      }

      // the first adjustment
      PricingAdjustment adjustment =
        new PricingAdjustment(Constants.ORDER_SUBTOTAL_PRICE_ADJUSTMENT_DESCRIPTION,
                              null,
                              subTotal,
                              1);

      adjustments.add(adjustment);

    }
    finally {
      try {
        if(!perfCancelled) {
          PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfName);
          perfCancelled = true;
        }
      } catch (PerfStackMismatchException e) {
        if (isLoggingWarning()) {
          logWarning(e);
        }
      }
    }// end finally

  } // end priceOrder


  /**
   * calculates a price from the sum of the order's item prices
   * @param pOrder the order whose item prices will be summed
   * @return the sum of the input order's item prices
   */
  protected double calculatePriceFromOrderItems(Order pOrder) {

    if (isLoggingDebug()) {
      logDebug("calculating order price from item total for order : " + pOrder.getId());
    }

    double price = 0.0;

    if (pOrder == null) {
      return price;
    }

    List commerceItems = pOrder.getCommerceItems();
    if (commerceItems != null) {
      for (int c=0; c<commerceItems.size(); c++) {
        CommerceItem item = (CommerceItem)commerceItems.get(c);
        if (item != null) {
          ItemPriceInfo info = item.getPriceInfo();
          if (info != null) {
            price += info.getAmount();
          }
        }
      }
    }

    if (isLoggingDebug()) {
      logDebug("Order price from item total is : " + price);
    }

    return price;

  } // end calculatePriceFromOrderItems

  /**
   * checks to make sure that there is at least one shippinggroup with at least one relationship
   * @param pOrder the order whose shippinggroups are to be checked
   * @return true if there is at least one ShippingGroup with at least one relationship, false
   *         if there is not.
   */
  private boolean checkRelationships(Order pOrder) {
    if (isLoggingDebug()) {
      logDebug("Checking relationships inside shippinggroups of order: " + pOrder.getId());
    }

    List shippingGroups = pOrder.getShippingGroups();
    Iterator groupIterator = shippingGroups.iterator();
    boolean groupHasRelationship = false;

    while (groupIterator.hasNext()) {
      ShippingGroup group = (ShippingGroup) groupIterator.next();

      if (group.getCommerceItemRelationships() == null ||
          group.getCommerceItemRelationships().isEmpty()) {
        if (isLoggingDebug()) {
          logDebug("shipping group : " + group.getId() + " has no relationships");
        }
      } else {
        if (isLoggingDebug()) {
          logDebug("shippingGroup : " + group.getId() + " has " + group.getCommerceItemRelationships().size() + " relationships");
        }

        groupHasRelationship = true;
        break;
      }
    }

    return groupHasRelationship;

  } // end checkRelationships

  /**
   * This method calculates the subtotal of the shipping group.
   * This method will return the subtotal of the items contained within this shipping group.
   * In addition it will modify the given pPriceQuote (the Order's price info)
   * so the following properties include a new entry for pShippingGroup:
   * <pre>
   *   shippingItemsSubtotalPriceInfos
   *   taxableShippingItemsSubtotalPriceInfos
   *   nonTaxableShippingItemsSubtotalPriceInfos
   * </pre>
   * 
   * @param pPriceQuote The price info for the order
   * @param pOrder The order containing the shipping group
   * @param pShippingGroup The shipping group being priced
   * @param pItemQuantities A map mapping a commerce item to a quantity.  The incoming quantity will be the total
   *                         quantity of that item that has been included in the subtotal of other shipping groups.
   *                         The quantity will be increased by the quantity contained in pShippingGroup
   * @param pItemPrices A map mapping a commerce item to a price.  The price is the total price of the item that has been included
   *                     in the subtotals of other shipping groups.  Once the pItemQuantities for a given item is reached
   *                     this value is used to resolve any potential rounding errors.  The pItemPrices for a particular item
   *                     will be increased by the price of the item that pShippingGroup's subtotal used. 
   * @param pLocale The locale in which all pricing operations should be performed
   * @param pProfile The owner of pOrder
   * @param pExtraParameters Any extra processing information.  Unused by this method.
   * @return The subtotal of pShippingGroup (this is the sum of the prices of the items contained within the shipping group)
   * @throws PricingException
   */
  protected double calculateShippingGroupSubtotal(OrderPriceInfo pPriceQuote, Order pOrder, ShippingGroup pShippingGroup, 
                                                    Map pItemQuantities, Map pItemPrices,
                                                    Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
    throws PricingException
  {
    String groupId = pShippingGroup.getId();
    double groupSubtotal = 0.0;
    double groupTaxableSubtotal = 0.0;
    double groupNonTaxableSubtotal = 0.0;
    double taxableAmount;
    double nonTaxableAmount;
    long shippingGroupQuantity = 0;
    long taxableShippingGroupQuantity = 0;
    long nonTaxableShippingGroupQuantity = 0;

    // get all the relationships in this group
    List relationships = pShippingGroup.getCommerceItemRelationships();

    // if there are no relationships in this group, there must be a relationship
    // in some other group, because we've already verified that fact using
    // checkRelationships
    if (relationships == null || relationships.isEmpty()) {
      
      // prepare the taxable subtotal
      OrderPriceInfo taxableShippingItemsInfo = new OrderPriceInfo();
      taxableShippingItemsInfo.setRawSubtotal(0.0);
      taxableShippingItemsInfo.setAmount(0.0);
      pPriceQuote.getTaxableShippingItemsSubtotalPriceInfos().put(groupId, taxableShippingItemsInfo);

      // prepare the non-taxable subtotal
      OrderPriceInfo nonTaxableShippingItemsInfo = new OrderPriceInfo();
      nonTaxableShippingItemsInfo.setRawSubtotal(0.0);
      nonTaxableShippingItemsInfo.setAmount(0.0);
      pPriceQuote.getNonTaxableShippingItemsSubtotalPriceInfos().put(groupId, nonTaxableShippingItemsInfo);
     
      // prepare the overall subtotal
      OrderPriceInfo shippingItemsInfo = new OrderPriceInfo();
      shippingItemsInfo.setRawSubtotal(groupSubtotal);
      shippingItemsInfo.setAmount(groupSubtotal);
      pPriceQuote.getShippingItemsSubtotalPriceInfos().put(groupId, shippingItemsInfo);
      
      return 0.0;
    } // end if no relationships
    // else there is at least one relationship
    else {

      if (isLoggingDebug()) {
        logDebug("Shipping group : " + groupId + " has : " + relationships.size() + " relationships");
      }

      Iterator relationshipIterator = relationships.iterator();
      while (relationshipIterator.hasNext()) {

        ShippingGroupCommerceItemRelationship rel = (ShippingGroupCommerceItemRelationship) relationshipIterator.next();

        // get the item and the quantity of the item that is in this relationship.
        // switch on type of relationship to get the actual quantity that the relationship
        // represents

        long relationshipQuantity = 0;
        CommerceItem item = rel.getCommerceItem();

        if (rel.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITY) {
          relationshipQuantity = rel.getQuantity();
        } else if (rel.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITYREMAINING) {
          relationshipQuantity = getOrderManager().getShippingGroupManager().getRemainingQuantityForShippingGroup(item);
        } else {
          throw new PricingException(MessageFormat.format(Constants.BAD_SHIPPING_GROUP_TYPE,
                                                          new Object [] {
                                                            Integer.valueOf(rel.getRelationshipType())
                                                              }));
        }

        if (isLoggingDebug()) {
          logDebug("total quantity of item: " + item.getId() + " to add to order total: " + relationshipQuantity);
        }

        // if there's no quantity in this rel, skip it
        if (relationshipQuantity == 0) {
          if (isLoggingDebug()) {
            logDebug("no quantity of this relationship to add, continuing.");
          }
          continue;
        }

        shippingGroupQuantity += relationshipQuantity;

        /**
         * find the average item price for order summing purposes, since all items will
         * be accounted for (we just need a breakdown for the group level)
         * see javadoc for explanation.
         * average price is total price for the CommerceItem divided by the quantity of the
         * item.  This code also floors the average price to a configurable number of decimal
         * places, and tacks any remainder on to a single relationship amount.
         * The remainder itself should automatically be a max of N decimal places,
         * because item prices get rounded too.  Therefore, an item price with a max of N
         * places minus (average price rounded to N places  - times - CommerceItem quantity)
         * can yield a number with a maximum of N decimal places:
         *
         *   2 places times 0 places => 2 places
         *   2 places minus 2 places => 2 places
         */

        // add to the group subtotal the cost of this relationship
        // Now that relationships and details have ranges, we don't need to use
        // averages
        double relationshipSubtotal = 0.0;

        double roundedAverage = 0.0;
        if((getPricingTools().isShippingSubtotalUsesAverageItemPrice()) ||
           (rel.getRange() == null)) {
                  
          double averagePrice = getPricingTools().getAverageItemPrice(item);
          roundedAverage = getPricingTools().round(averagePrice);

          if (isLoggingDebug()) {
            logDebug("Average price of commerceitem: " + item.getId() + " across all relationships : " + averagePrice);
            logDebug("rounding average price");
            logDebug("rounded average price to: " + roundedAverage);
          }
                  
          relationshipSubtotal = relationshipQuantity * roundedAverage;
        }
        else {
          relationshipSubtotal = getPricingTools().getShipItemRelPriceTotal(rel,DETAILED_ITEM_PRICE_AMOUNT_PROPERTY);
        }

        // used when determining leftovers (when not using averaging method)
        double runningTotal = 0.0;
        Double oldRunningTotal = (Double) pItemPrices.get(item);
        if(oldRunningTotal != null)
          runningTotal += oldRunningTotal.doubleValue();

        runningTotal += relationshipSubtotal;

        if (isLoggingDebug()) {
          logDebug("subtotal of this relationship for group: " + groupId + " : " + relationshipSubtotal);
        }

        long quantityAlreadyProcessed = 0;
        Long quantityProcessed = (Long) pItemQuantities.get(item);
        if (quantityProcessed != null) {
          quantityAlreadyProcessed = quantityProcessed.longValue();
        }

        if (isLoggingDebug()) {
          logDebug("quantity of the item: " + item.getId() + " already processed: " + quantityAlreadyProcessed);
          logDebug("quantity in this relationship: " + relationshipQuantity);
          logDebug("total quantity of the item: " + item.getQuantity());
        }

        // if we're attributing the final bit of an item to a group's subtotal,
        // add any leftovers
        if (quantityAlreadyProcessed + relationshipQuantity == item.getQuantity()) {
          if (isLoggingDebug()) {
            logDebug("calculating leftovers");
          }

          if(getPricingTools().isShippingSubtotalUsesAverageItemPrice()) {
            double leftovers = item.getPriceInfo().getAmount() - (roundedAverage * item.getQuantity());
            // the leftovers are the remainders from rounding every unit of the CommerceItem
            if (isLoggingDebug()) {
              logDebug("total item amount: " + item.getPriceInfo().getAmount());
              logDebug("item quantity: " + item.getQuantity());
              logDebug("rounded average price: " + roundedAverage);
              logDebug("rounded unit price times quantity of: " + item.getQuantity() + " : " + ( roundedAverage * item.getQuantity()));
              logDebug("adding leftovers: " + leftovers);
            }
                    
            relationshipSubtotal += leftovers;
          }
          else {
            double leftovers = item.getPriceInfo().getAmount() - runningTotal;
            // the leftovers are the remainders from rounding every unit of the CommerceItem
            if (isLoggingDebug()){
              logDebug("total item amount: " + item.getPriceInfo().getAmount());
              logDebug("total so far: " + runningTotal);
              logDebug("adding leftovers: " + leftovers);
            }
                    
            relationshipSubtotal += leftovers;
          }
        } // end if we're processing the last of an item

        // add the relationship subtotal to the group subtotal
        groupSubtotal += relationshipSubtotal;

        /**
         * sometimes double math produces 'equivalent', yet not exactly identical
         * numbers.  For example, it appears that the numbers:
         *   .03
         * and
         *   .030000000000001137
         * are numerically equivalent as far as double calculations are concerned.
         * Double calculations sometimes produce these strange numbers despite the
         * fact that the number of decimal places can't possibly be that large.
         * We're going to round the products of double math to N places, with the
         * understanding that all numbers in DCS are going to be rounded to N places.
         */

        groupSubtotal = getPricingTools().round(groupSubtotal);

        if (isLoggingDebug()) {
          logDebug("after rounding, group subtotal: " + groupSubtotal);
        }

        // if the current item is taxable, add it to the taxable subtotal
        if (getPricingTools().isTaxable(item,
                                        item.getPriceInfo(),
                                        pPriceQuote,
                                        pOrder,
                                        pShippingGroup.getPriceInfo(),
                                        pShippingGroup,
                                        pLocale,
                                        pProfile,
                                        pExtraParameters)) {
          // see if the item is partially taxable or not
                  
          if(getPricingTools().isShippingSubtotalUsesAverageItemPrice())
            taxableAmount = getPricingTools().calculateTaxableAmountByAverage(rel, pOrder, pLocale, pProfile, pExtraParameters);
          else
            taxableAmount = getPricingTools().calculateTaxableAmount(rel, pOrder, pLocale, pProfile, pExtraParameters);

	  // make sure that our averaging doesn't end up making it so the
	  // taxable amount is more than the total amount.
	  if (taxableAmount > relationshipSubtotal) {
	    taxableAmount = relationshipSubtotal;
	  }

          nonTaxableAmount = relationshipSubtotal - taxableAmount;
                  
          taxableShippingGroupQuantity += relationshipQuantity;
          groupTaxableSubtotal += taxableAmount;

          groupTaxableSubtotal = getPricingTools().round(groupTaxableSubtotal);
                  
          if (isLoggingDebug()) {
            logDebug("relationship's subtotal is TAXABLE");
            logDebug("after rounding, group taxable subtotal: " + groupTaxableSubtotal);
          }

          nonTaxableAmount = getPricingTools().round(nonTaxableAmount);
                  
          if(nonTaxableAmount > 0.0) {
            // if we are here, then the item was "partially" taxable
            // each quantity of the item has a taxable part and a non taxable part
            nonTaxableShippingGroupQuantity += relationshipQuantity;
            groupNonTaxableSubtotal += nonTaxableAmount;
            groupNonTaxableSubtotal = getPricingTools().round(groupNonTaxableSubtotal);
            if (isLoggingDebug()) {
              logDebug("It was only partially taxable though");
              logDebug("after rounding, group non taxable subtotal: " + groupNonTaxableSubtotal);
            }
          }
                  
        }
        // if it's not taxable, add it to the nontaxable subtotal
        else {
          nonTaxableShippingGroupQuantity += relationshipQuantity;
          groupNonTaxableSubtotal += relationshipSubtotal;

          groupNonTaxableSubtotal = getPricingTools().round(groupNonTaxableSubtotal);

          if (isLoggingDebug()) {
            logDebug("relationship's subtotal is NON-TAXABLE");
            logDebug("after rounding, group non taxable subtotal is: " + groupNonTaxableSubtotal);
          }

        } // end if not taxable

        // register the item as processed
        pItemQuantities.put(item, Long.valueOf(relationshipQuantity + quantityAlreadyProcessed));
        // remember amount for leftovers
        pItemPrices.put(item, Double.valueOf(runningTotal));

      } // end for each relationship

      // we should now have all relationships in the group accounted for.

    } // end else there are relationships

    if (isLoggingDebug()) {
      logDebug("group nontaxable subtotal: " + groupNonTaxableSubtotal);
      logDebug("group taxable subtotal: " + groupTaxableSubtotal);
      logDebug("group subtotal: " + groupSubtotal);
    }


    // sanity
    double sanityCheck = getPricingTools().round(groupNonTaxableSubtotal + groupTaxableSubtotal);
    if (sanityCheck != groupSubtotal) {
      throw new PricingException("The sum of the taxable and non-taxable items in this shippingGroup does not equal to the overall subtotal");
    }

    // prepare the taxable subtotal
    OrderPriceInfo taxableShippingItemsInfo = new OrderPriceInfo();
    taxableShippingItemsInfo.setRawSubtotal(groupTaxableSubtotal);
    taxableShippingItemsInfo.setAmount(groupTaxableSubtotal);

    // initialize the adjustments list
    List adjustments = taxableShippingItemsInfo.getAdjustments();

    if (adjustments.size() > 0) {
      adjustments.clear();
    }

    // the first adjustment
    PricingAdjustment taxableAdjustment =
      new PricingAdjustment(Constants.ORDER_TAXABLE_SHIPPING_ITEMS_SUBTOTAL_PRICE_ADJUSTMENT_DESCRIPTION,
                            null,
                            groupTaxableSubtotal,
                            taxableShippingGroupQuantity);

    adjustments.add(taxableAdjustment);


    // add a new entry into the order's TaxableShippingItemsSubtotalPriceInfos
    pPriceQuote.getTaxableShippingItemsSubtotalPriceInfos().put(groupId, taxableShippingItemsInfo);


    if (isLoggingDebug()) {
      logDebug("The taxable subtotal for shipping group: " + groupId + " is: " + groupTaxableSubtotal);
    }


    // prepare the non-taxable subtotal

    OrderPriceInfo nonTaxableShippingItemsInfo = new OrderPriceInfo();
    nonTaxableShippingItemsInfo.setRawSubtotal(groupNonTaxableSubtotal);
    nonTaxableShippingItemsInfo.setAmount(groupNonTaxableSubtotal);

    // initialize the adjustments list
    adjustments = nonTaxableShippingItemsInfo.getAdjustments();

    if (adjustments.size() > 0) {
      adjustments.clear();
    }

    // the first adjustment
    PricingAdjustment nonTaxableAdjustment =
      new PricingAdjustment(Constants.ORDER_NONTAXABLE_SHIPPING_ITEMS_SUBTOTAL_PRICE_ADJUSTMENT_DESCRIPTION,
                            null,
                            groupNonTaxableSubtotal,
                            nonTaxableShippingGroupQuantity);

    adjustments.add(nonTaxableAdjustment);


    // add a new entry into the order's ShippingItemsSubtotalPriceInfos
    pPriceQuote.getNonTaxableShippingItemsSubtotalPriceInfos().put(groupId, nonTaxableShippingItemsInfo);


    if (isLoggingDebug()) {
      logDebug("The non-taxable subtotal for shipping group: " + groupId + " is: " + groupNonTaxableSubtotal);
    }

    // prepare the overall subtotal
    OrderPriceInfo shippingItemsInfo = new OrderPriceInfo();
    shippingItemsInfo.setRawSubtotal(groupSubtotal);
    shippingItemsInfo.setAmount(groupSubtotal);

    // initialize the adjustments list
    adjustments = shippingItemsInfo.getAdjustments();

    if (adjustments.size() > 0) {
      adjustments.clear();
    }

    // the first adjustment
    PricingAdjustment adjustment =
      new PricingAdjustment(Constants.ORDER_SHIPPING_ITEMS_SUBTOTAL_PRICE_ADJUSTMENT_DESCRIPTION,
                            null,
                            groupSubtotal,
                            shippingGroupQuantity);

    adjustments.add(adjustment);

    // add a new entry into the order's ShippingItemsSubtotalPriceInfos
    pPriceQuote.getShippingItemsSubtotalPriceInfos().put(groupId, shippingItemsInfo);
            


    return groupSubtotal;
  }


  /**
   * genericservice overrides
   */
  /**
   * Overrides GenericService's doStartService method to check for null properties
   * @exception ServiceException if a required property is null.
   */
  public void doStartService () throws ServiceException {
    if (mPricingTools == null) {
      throw new ServiceException (MessageFormat.format(Constants.PROPERTY_NOT_SET,
                                                       new Object [] {
                                                         "pricingTools"
                                                       }));
    }

    if (mOrderManager == null) {
      throw new ServiceException (MessageFormat.format(Constants.NULL_PROPERTY,
                                                       new Object [] {
                                                         "orderManager"
                                                       }));
    }

  } // end doStartService

} // end of class
