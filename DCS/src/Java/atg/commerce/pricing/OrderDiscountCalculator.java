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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.definition.MatchingObject;
import atg.core.util.Range;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryItem;
/**
 *
 * Calculates OrderPriceInfos for Orders which the calculator is given.
 * This calculator can either be passed in a MatchingObject via the extra parameters map with key Constants.MATCHING_OBJECT
 * containing the order that qualified for the promotion
 * or it can consult the Qualifier service, looking for the Order to be priced.
 * It calls <code>Qualifier.findQualifyingOrder</code> for this purpose.  
 * <p>
 * If it obtains an Order that qualified then it can determine the discount either via a DiscountStructure object being
 * passed into the calculator via the extra parameters map with key Constants.DISCOUNT_STRUCTURE or by getting the
 * <code>discountType</code> and <code>adjuster</code> properties of the input PricingModel (RepositoryItem).
 *
 * @see Qualifier
 *
 * @beaninfo
 *   description: An OrderPricingCalcultor that knows how to discount orders
 *   attribute: functionalComponentCategory Pricing Calculators
 *   attribute: featureComponentCategory
 *
 * @author Joshua Spiewak
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/OrderDiscountCalculator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
@SuppressWarnings("unchecked")
public class OrderDiscountCalculator extends DiscountCalculatorService implements OrderPricingCalculator
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/OrderDiscountCalculator.java#2 $$Change: 651448 $";


  //-------------------------------------
  // property: PricingTools
  /** pricing tools to help with calculating prices */
  PricingTools mPricingTools;

  /**
   * pricing tools to help with calculating prices
   * @beaninfo description: pricing tools to help with calculating prices
   * @param pPricingTools new value to set
   */
  public void setPricingTools(PricingTools pPricingTools)
  {mPricingTools = pPricingTools;}

  /**
   * pricing tools to help with calculating prices
   * @beaninfo description: pricing tools to help with calculating prices
   * @return property PricingTools
   */
  public PricingTools getPricingTools()
  {return mPricingTools;}


  //---------------------------------------------------------------------------
  // property:SaveItemsOrderDiscountShare
  //---------------------------------------------------------------------------

  private boolean mSaveItemsOrderDiscountShare = true;
  public void setSaveItemsOrderDiscountShare(boolean pSaveItemsOrderDiscountShare) {
    mSaveItemsOrderDiscountShare = pSaveItemsOrderDiscountShare;
  }

  /**
   * If this is true, update itemPriceInfo.orderDiscountShare
   **/
  public boolean isSaveItemsOrderDiscountShare() {
    return mSaveItemsOrderDiscountShare;
  }

  //---------------------------------------------------------------------------
  // property:SaveDetailsOrderDiscountShare
  //---------------------------------------------------------------------------

  private boolean mSaveDetailsOrderDiscountShare = true;
  public void setSaveDetailsOrderDiscountShare(boolean pSaveDetailsOrderDiscountShare) {
    mSaveDetailsOrderDiscountShare = pSaveDetailsOrderDiscountShare;
  }

  /**
   * If this is true, update itemPriceInfo.orderDiscountShare
   **/
  public boolean isSaveDetailsOrderDiscountShare() {
    return mSaveDetailsOrderDiscountShare;
  }

  //-------------------------------------
  /**
   * Price a single order within a context defined by the input parameters.  The order's
   * price is adjusted as well as the shipping group subtotals.
   * <p>
   * The order can be passed in within a MatchingObject via the pExtraParameters map with key Constants.MATCHING_OBJECT.
   * If this is not the case then it will call findMatchingObject to try and obtain the MatchingObject from the qualifier service.
   * 
   * @see #updateShippingItemsSubtotalMaps
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

    if (isLoggingDebug()) {
      logDebug("Pricing the Order : " + pOrder.getId());
    }

    // pull items out of order
    List commerceItems = pOrder.getCommerceItems();

    // discover the items' current prices
    List priceQuotes = new ArrayList(commerceItems.size());
    Iterator itemIterator = commerceItems.iterator();
    while (itemIterator.hasNext()) {
      CommerceItem item = (CommerceItem) itemIterator.next();
      priceQuotes.add(item.getPriceInfo());
    }

    MatchingObject ret = null;

    // Check extra parameters to see if discount was in the PMDL
    ret = (MatchingObject)pExtraParameters.get(Constants.MATCHING_OBJECT);

    if (ret == null){
      // Discount is in the pricing model item so still need to try and qualify promotion
      ret = findMatchingObject(pPriceQuote,priceQuotes,
          commerceItems,
          pPricingModel,
          pProfile,
          pLocale,
          pOrder,
          pExtraParameters);

      // if there's no qualifying order, simply return
      if (ret == null) {
        if (isLoggingDebug()) {
          logDebug("order : " + pOrder.getId()+ " didn't qualify.");
        }
        return;
      }
    }

    // the qualifier was met. discount the order

    if (isLoggingDebug()) {
      logDebug("discounting the order : " + pOrder.getId());
    }

    double orderSubTotal = 0.0;

    /**
     * If the discounttype is percentoff, apply the adjuster to each group.  If it's
     * amount off, divide the adjuster by the number of groups and apply [adjuster/n] to
     * each group.  If it's fixed amount, divide that amount by number of groups, and
     * set each group's price to [adjuster/n].
     */    
    double adjuster = getAdjuster(pPricingModel, pExtraParameters);
    if (Double.isNaN(adjuster)) {
      // No discount to apply
      return;
    }
    
    String discountType = getDiscountType(pPricingModel, pExtraParameters);

    // if there is no shippinggroup-by-shippinggroup subtotal breakdown, compute the
    // order's discounted price via the order's amount
    double oldOrderAmount = pPriceQuote.getAmount();
    //  if (pPriceQuote.getShippingItemsSubtotalPriceInfos() == null ||
    //  pPriceQuote.getShippingItemsSubtotalPriceInfos().isEmpty()) {

    // adjust order subtotal as many times as specified
    //orderSubTotal = pPriceQuote.getAmount();
    orderSubTotal = getAmountToDiscount(pPriceQuote, pOrder, pPricingModel, pLocale, pProfile, pExtraParameters);

    for (int i = 0; i < ret.getQuantity(); i++) {
      orderSubTotal = adjustOrderSubTotal(orderSubTotal,adjuster, discountType, pOrder.getId(), pPricingModel,pExtraParameters);
    }

    //  } else {
    orderSubTotal = updateShippingItemsSubtotalMaps(orderSubTotal, oldOrderAmount, ret, adjuster, discountType, 
        pPriceQuote, pOrder, pPricingModel, pExtraParameters);

    // amountInfo stuff

    if (isLoggingDebug()) {
      logDebug("order's amount before discounting: " + pPriceQuote.getAmount());
    }

    double currentAmount = pPriceQuote.getAmount();

    if (isLoggingDebug()) {
      logDebug("rounding order subtotal: " + orderSubTotal);
    }

    orderSubTotal = getPricingTools().round(orderSubTotal);

    pPriceQuote.setAmount(orderSubTotal);

    updateItemsDiscountShare(pOrder, currentAmount, getPricingTools().round(currentAmount - orderSubTotal), adjuster, 
        discountType, ret.getQuantity());

    if (isLoggingDebug()) {
      logDebug("order's amount after discounting and rounding: " + pPriceQuote.getAmount());
    }

    pPriceQuote.setDiscounted(true);

    // add this adjustment to the list of adjustments
    double adjustAmount = pPriceQuote.getAmount() - currentAmount;
    pPriceQuote.getAdjustments().add(new PricingAdjustment(Constants.ORDER_DISCOUNT_PRICE_ADJUSTMENT_DESCRIPTION,
        pPricingModel,
        getPricingTools().round(adjustAmount),
        1));

  } // end priceOrder


  /**
   * Computes a shippinggroup subtotal based on the type of discount given.
   * The logic is as follows:
   * <p>
   * <ul>
   *  <li>If the discount is a percent off, the percent adjuster is applied to
   *      the group's price.
   *  <li>If the discount is an amount off, the adjuster is divided by the number of groups,
   *      and [adjuster/n] is applied to the group price.
   *  <li>If the discount is a fixed amount, the adjuster is divided by the number of groups,
   *      and the group price is set to [adjuster/n]
   * </ul>
   *
   * If functionality other than that described above, this method can be overridden to
   * do that which is necessary.  For example, rather than spreading out amount off discounts
   * across all shippingGroups, some implementations might want the first ShippingGroup to
   * get the full benefit of the discount.
   *
   * @param pTimesToDiscount the number of times we should apply the configured discount
   * @param pNumberOfGroups the number of groups in the order, including the one for which
   *        this method is computing a subtotal
   * @param pGroupSubTotal is the subtotal that needs adjusting
   * @param pAdjuster is the amount by which to adjust pGroupSubTotal
   * @param pDiscountType the way in which pAdjuster is applied to pGroupSubTotal.  May be one of:
   *        "Fixed Price", "Amount Off", or "Percent Off".
   * @param pOrderId an ID to associate with this adjustment for error reporting
   * @param pOldOrderAmount the order subtotal before this discount was applied at the order level
   * @param pNewOrderAmount the order subtotal after this discount was applied at the order level
   * @param pPricingModel the order promotion that's being applied
   * @param pExtraParameters the extra parameter map originally passed into the pricing operation. 
   * @exception PricingException if there was a problem computing the group subtotal
   * @return the adjusted subtotal for the input pGroupSubTotal
   */
  protected double computeGroupSubtotal(long pTimesToDiscount,
      int pNumberOfGroups,
      double pGroupSubTotal,
      double pAdjuster,
      String pDiscountType,
      String pOrderId,
      double pOldOrderAmount,
      double pNewOrderAmount, 
      RepositoryItem pPricingModel, 
      Map pExtraParameters) 
  throws PricingException 
  {


    if (isLoggingDebug()) {
      logDebug("Computing group subtotal for discount type: " + pDiscountType);
      logDebug("Times to discount: " + pTimesToDiscount);
      logDebug("Number of groups: " + pNumberOfGroups);
      logDebug("Un-adjusted adjuster is: " + pAdjuster);
    }

    double adjustedAdjuster = pAdjuster;
    double ratio = pGroupSubTotal / pOldOrderAmount;

    // if we divided by zero... use zero instead
    if(Double.isNaN(ratio) || Double.isInfinite(ratio)) {
      if(isLoggingDebug()) {
        logDebug(MessageFormat.format(Constants.QUOTIENT_IS_NAN, "computeGroupSubtotal", Double.toString(pGroupSubTotal), Double.toString(pOldOrderAmount)));
      }
      ratio = 0.0;
    }

    double groupSubTotal = pGroupSubTotal;

    // adjust group subtotal as many times as specified
    for (int i = 0; i < pTimesToDiscount; i++) {
      if (Constants.FIXED_PRICE.equalsIgnoreCase(pDiscountType) ||
          Constants.AMOUNT_OFF.equalsIgnoreCase(pDiscountType) ) {
        adjustedAdjuster = pAdjuster * ratio;
        adjustedAdjuster = getPricingTools().roundDown(adjustedAdjuster);
      }

      if (isLoggingDebug()) {
        logDebug("Adjusted adjuster is: " + adjustedAdjuster);
      }

      groupSubTotal = adjust(groupSubTotal, adjustedAdjuster, pDiscountType, pOrderId);
    }

    if (isLoggingDebug()) {
      logDebug("rounding group subtotal: " + groupSubTotal);
    }

    // round the group subtotal to N places
    groupSubTotal = getPricingTools().round(groupSubTotal);

    return groupSubTotal;

  }

  /**
   * Applies the given adjuster to the given order subtotal.
   * 
   * @see DiscountCalculatorService#adjust(double, double, String, String)
   * @param pOrderSubTotal the order's current subtotal
   * @param pAdjuster the pricing model's adjuster
   * @param pDiscountType the pricing models discount type
   * @param pOrderId the id of the order to which the adjustment is beig made
   * @param pPricingModel the order promotion repository item
   * @param pExtraParameters the original extra parameter map passed to the pricing operation. 
   * @return the new, calculated order subtotal. 
   * @throws PricingException
   */
  protected double adjustOrderSubTotal(double pOrderSubTotal, double pAdjuster, 
      String pDiscountType, String pOrderId, 
      RepositoryItem pPricingModel, Map pExtraParameters)
  throws PricingException
  {
    return adjust(pOrderSubTotal, pAdjuster, pDiscountType,pOrderId);

  }

  /**
   * This method determines if the order qualifies for the given 
   * order promotion. 
   * <p>
   * It determines this by calling the <code>QualifierService</code>
   * @see Qualifier#findQualifyingOrder(List, List, RepositoryItem, RepositoryItem, Locale, Order, OrderPriceInfo, ShippingGroup, ShippingPriceInfo, Map)
   * @param pPriceQuote
   * @param pPriceQuotes
   * @param pItems
   * @param pPricingModel
   * @param pProfile
   * @param pLocale
   * @param pOrder
   * @param pExtraParameters
   * @return
   * @throws PricingException
   */
  protected MatchingObject findMatchingObject(OrderPriceInfo pPriceQuote, List pPriceQuotes,
      List pItems,
      RepositoryItem pPricingModel,
      RepositoryItem pProfile,
      Locale pLocale,
      Order pOrder,
      Map pExtraParameters)
  throws PricingException
  {
    MatchingObject ret = null;
    Qualifier qualifierService = getQualifierService(pPricingModel, pExtraParameters);
    
    if (qualifierService != null) {
      List wrappedItems = qualifierService.wrapCommerceItems(pItems, pPriceQuotes);
      PricingContext pricingContext = getPricingTools().getPricingContextFactory().createPricingContext(
          wrappedItems, pPricingModel, pProfile, pLocale, pOrder, null);
      pricingContext.setOrderPriceQuote(pPriceQuote);

      ret = qualifierService.findQualifyingOrder(pricingContext, pExtraParameters);

      // if there's no qualifying order, simply return
      if (ret == null) {
        if (isLoggingDebug()) {
          logDebug("order : " + pOrder.getId()+ " didn't qualify.");
        }
      }

    } else {
      throw new PricingException(Constants.QUALIFIER_SERVICE_NOT_SET);
    }
    return ret;

  }
  /**
   * These method will iterate through each of the maps in the OrderPriceInfo that contain
   * shipping group subtotal information.  These include:
   * <ul>
   *  <li><code>taxableShippingItemsSubtotalPriceInfos</code>
   *  <li><code>nonTaxableShippingItemsSubtotalPriceInfos</code>
   *  <li><code>shippingItemsSubtotalPriceInfos</code>
   * </ul>
   * For each of entry in each of these maps, <code>computeGroupSubtotal</code> is called.  The amount
   * of the price info in the entry is set to this value, the info is marked discounted, and 
   * a new adjustement is added to the info
   *
   * @param pOrderSubTotal The new subtotal of the order
   * @param pOldOrderAmount The previous subtotal of the order
   * @param pQualifier The MatchingObject as returned by Qualifier.evaluateQualifier
   * @param pAdjuster The adjuster of the promotion
   * @param pDiscountType The type of discount being given
   * @param pPriceQuote The order's price info
   * @param pOrder The order that was discounted
   * @param pPricingModel The promotion that changed the price of the order
   * @param pExtraParameters Any extra parameters
   * @return The new subtotal of the order.  This is the sum of the shipping group subtotals
   *         and should match pOrderSubTotal
   * @exception PricingException
   **/
  protected double updateShippingItemsSubtotalMaps(double pOrderSubTotal, double pOldOrderAmount,
      MatchingObject pQualifier,
      double pAdjuster, String pDiscountType,
      OrderPriceInfo pPriceQuote, 
      Order pOrder, RepositoryItem pPricingModel, 
      Map pExtraParameters)
  throws PricingException
  {
    double orderSubTotal = pOrderSubTotal;
    PricingTools tools = getPricingTools();

    if (pPriceQuote.getShippingItemsSubtotalPriceInfos() != null &&
        !pPriceQuote.getShippingItemsSubtotalPriceInfos().isEmpty()) {
      double leftoverCheck = orderSubTotal;

      //calculate the eventual total of shipping group price infos
      double ratio = pOrderSubTotal / pOldOrderAmount;
      
      // if we divided by zero... use zero instead
      if(Double.isNaN(ratio) || Double.isInfinite(ratio)) {
        if(isLoggingDebug()) {
          logDebug(MessageFormat.format(Constants.QUOTIENT_IS_NAN, "updateShippingItemsSubtotalMaps", 
              Double.toString(pOrderSubTotal), Double.toString(pOldOrderAmount)));
        }
        ratio = 0.0;
      }
      
      double nonTaxableLeftoverCheck=getNonTaxableShippingGroupTotal(pPriceQuote);
      nonTaxableLeftoverCheck = getPricingTools().round(nonTaxableLeftoverCheck * ratio);
      double taxableLeftoverCheck=getTaxableShippingGroupTotal(pPriceQuote);
      taxableLeftoverCheck = getPricingTools().round(taxableLeftoverCheck * ratio);
      orderSubTotal = 0.0;
      // go through each shipping group subtotal, if there are any, and discount them.
      // then add all the shipping group subtotals to get the order's discounted subtotal

      // since the OrderSubtotalCalculator creates a taxable and non-taxable entry for each
      // shipping group (including times when the taxable or non-taxable subtotal is zero),
      // we can just iterate through all 3 lists at the same time, discounting the entries

      Set taxableEntries = pPriceQuote.getTaxableShippingItemsSubtotalPriceInfos().entrySet();
      Set nonTaxableEntries = pPriceQuote.getNonTaxableShippingItemsSubtotalPriceInfos().entrySet();
      Set entries = pPriceQuote.getShippingItemsSubtotalPriceInfos().entrySet();

      // sanity check
      if (taxableEntries.size() != nonTaxableEntries.size() ||
          taxableEntries.size() != entries.size() ||
          nonTaxableEntries.size() != entries.size()) {
        throw new PricingException("The taxableShippingItemsSubtotalPriceInfos and nonTaxableShippingItemsSubtotalPriceInfos lists in an OrderPriceInfo must each contain one entry per shippingGroup, even if that shippingGroup's subtotal is zero.");
      }

      double subtotal = 0.0;
      double taxablesubtotal = 0.0;
      double nontaxablesubtotal = 0.0;

      int numberOfGroups = entries.size();

      int groupsSeenSoFar = 0;
      long qualQuantity = pQualifier.getQuantity();

      List shippingGroups = pOrder.getShippingGroups();
      Iterator shippingGrouperator = shippingGroups.iterator();
      ShippingGroup shippingGroup=null;
      while(shippingGrouperator.hasNext())
      {
        shippingGroup = (ShippingGroup)shippingGrouperator.next();
        groupsSeenSoFar++;

        // discount the taxable group subtotals

        String shippingGroupId = shippingGroup.getId();
        OrderPriceInfo taxableShippingGroupSubtotalInfo = (OrderPriceInfo) pPriceQuote.getTaxableShippingItemsSubtotalPriceInfos().get(shippingGroupId);

        double taxableGroupSubTotal = taxableShippingGroupSubtotalInfo.getAmount();
        double taxableCurrentGroupTotal = taxableGroupSubTotal;

        taxableGroupSubTotal = computeGroupSubtotal(qualQuantity,
            numberOfGroups,
            taxableGroupSubTotal,
            pAdjuster,
            pDiscountType,
            pOrder.getId(), pOldOrderAmount, 
            pOrderSubTotal,pPricingModel, pExtraParameters);

        if (isLoggingDebug()) {
          logDebug("group : " + shippingGroupId + " has old taxable subtotal of : " + taxableShippingGroupSubtotalInfo.getAmount());
        }

        taxablesubtotal += taxableGroupSubTotal;
        // get leftovers
        if(groupsSeenSoFar == numberOfGroups) {
          double leftovers = taxableLeftoverCheck - taxablesubtotal;
          if(isLoggingDebug()) {
            logDebug("Got leftovers: " + taxableLeftoverCheck + " - " + taxablesubtotal + " = " + leftovers);
            logDebug("Old group sub: " + taxableGroupSubTotal);
          }

          if(isLoggingDebug())
            logDebug("Added " + leftovers + " leftovers to subtotal");

          taxableGroupSubTotal += leftovers;

          if(isLoggingDebug())
            logDebug("New group sub: " + taxableGroupSubTotal);

          taxableGroupSubTotal = getPricingTools().round(taxableGroupSubTotal);

          if(isLoggingDebug())
            logDebug("Rounded group sub: " + taxableGroupSubTotal);
        }

        // set the group subtotal info's amount, and register the adjustment
        taxableShippingGroupSubtotalInfo.setAmount(taxableGroupSubTotal);

        if (isLoggingDebug()) {
          logDebug("group : " + shippingGroupId + " has new taxable subtotal of : " + taxableShippingGroupSubtotalInfo.getAmount());
        }


        double taxAdjustAmount = taxableGroupSubTotal - taxableCurrentGroupTotal;
        taxableShippingGroupSubtotalInfo.getAdjustments().add(new PricingAdjustment (Constants.ORDER_TAXABLE_SHIPPING_ITEMS_SUBTOTAL_DISCOUNT_PRICE_ADJUSTMENT_DESCRIPTION,
            pPricingModel,
            tools.round(taxAdjustAmount),
            1));

        taxableShippingGroupSubtotalInfo.setDiscounted(true);

        // discount the non-taxable group subtotal
        OrderPriceInfo nonTaxableShippingGroupSubtotalInfo = (OrderPriceInfo) pPriceQuote.getNonTaxableShippingItemsSubtotalPriceInfos().get(shippingGroupId);

        double nonTaxableGroupSubTotal = nonTaxableShippingGroupSubtotalInfo.getAmount();
        double nonTaxableCurrentGroupTotal = nonTaxableGroupSubTotal;

        nonTaxableGroupSubTotal = computeGroupSubtotal(qualQuantity,
            numberOfGroups,
            nonTaxableGroupSubTotal,
            pAdjuster,
            pDiscountType,
            pOrder.getId(), pOldOrderAmount,
            pOrderSubTotal,pPricingModel, pExtraParameters);

        if (isLoggingDebug()) {
          logDebug("group : " + shippingGroupId + " has old non-taxable subtotal of : " + nonTaxableShippingGroupSubtotalInfo.getAmount());
        }
        nontaxablesubtotal += nonTaxableGroupSubTotal;
        // get leftovers
        if(groupsSeenSoFar == numberOfGroups) {
          double leftovers = nonTaxableLeftoverCheck - nontaxablesubtotal;
          if(isLoggingDebug()) {
            logDebug("Got leftovers: " + nonTaxableLeftoverCheck + " - " + nontaxablesubtotal + " = " + leftovers);
            logDebug("Old group sub: " + nonTaxableGroupSubTotal);
          }

          if(isLoggingDebug())
            logDebug("Added " + leftovers + " leftovers to subtotal");

          nonTaxableGroupSubTotal += leftovers;

          if(isLoggingDebug())
            logDebug("New group sub: " + nonTaxableGroupSubTotal);

          nonTaxableGroupSubTotal = getPricingTools().round(nonTaxableGroupSubTotal);

          if(isLoggingDebug())
            logDebug("Rounded group sub: " + nonTaxableGroupSubTotal);
        }

        // set the group subtotal info's amount, and register the adjustment
        nonTaxableShippingGroupSubtotalInfo.setAmount(nonTaxableGroupSubTotal);

        if (isLoggingDebug()) {
          logDebug("group : " + shippingGroupId + " has new non-taxable subtotal of : " + nonTaxableShippingGroupSubtotalInfo.getAmount());
        }

        double nonTaxAdjustAmount = nonTaxableGroupSubTotal - nonTaxableCurrentGroupTotal;
        nonTaxableShippingGroupSubtotalInfo.getAdjustments().add(new PricingAdjustment (Constants.ORDER_NONTAXABLE_SHIPPING_ITEMS_SUBTOTAL_DISCOUNT_PRICE_ADJUSTMENT_DESCRIPTION,
            pPricingModel,
            tools.round(nonTaxAdjustAmount),
            1));

        nonTaxableShippingGroupSubtotalInfo.setDiscounted(true);


        // discount the overall group subtotal
        OrderPriceInfo shippingGroupSubtotalInfo = (OrderPriceInfo) pPriceQuote.getShippingItemsSubtotalPriceInfos().get(shippingGroupId);

        double groupSubTotal = shippingGroupSubtotalInfo.getAmount();
        double currentGroupTotal = groupSubTotal;

        groupSubTotal = computeGroupSubtotal(qualQuantity,
            numberOfGroups,
            groupSubTotal,
            pAdjuster,
            pDiscountType,
            pOrder.getId(), pOldOrderAmount,
            pOrderSubTotal,pPricingModel, pExtraParameters);
        subtotal += groupSubTotal;

        // get leftovers
        if(groupsSeenSoFar == numberOfGroups) {
          double leftovers = leftoverCheck - subtotal;
          if(isLoggingDebug()) {
            logDebug("Got leftovers: " + leftoverCheck + " - " + subtotal + " = " + leftovers);
            logDebug("Old group sub: " + groupSubTotal);
          }

          if(isLoggingDebug())
            logDebug("Added " + leftovers + " leftovers to subtotal");

          groupSubTotal += leftovers;

          if(isLoggingDebug())
            logDebug("New group sub: " + groupSubTotal);

          groupSubTotal = getPricingTools().round(groupSubTotal);

          if(isLoggingDebug())
            logDebug("Rounded group sub: " + groupSubTotal);
        }

        if (isLoggingDebug()) {
          logDebug("group : " + shippingGroupId + " has old subtotal of : " + shippingGroupSubtotalInfo.getAmount());
        }

        // set the group subtotal info's amount, and register the adjustment
        shippingGroupSubtotalInfo.setAmount(groupSubTotal);


        if (isLoggingDebug()) {
          logDebug("group : " + shippingGroupId + " has new subtotal of : " + shippingGroupSubtotalInfo.getAmount());
        }

        double shipAdjustAmount = groupSubTotal - currentGroupTotal;
        shippingGroupSubtotalInfo.getAdjustments().add(new PricingAdjustment (Constants.ORDER_SHIPPING_ITEMS_SUBTOTAL_DISCOUNT_PRICE_ADJUSTMENT_DESCRIPTION,
            pPricingModel,
            tools.round(shipAdjustAmount),
            1));

        shippingGroupSubtotalInfo.setDiscounted(true);

        orderSubTotal += groupSubTotal;

      } // end for each group subtotal

    } // end else there is at least one shippinggroup
    return orderSubTotal;
  }

  /**
   * Computes a shippinggroup subtotal based on the type of discount given.
   * The logic is as follows:
   * <p>
   * <ul>
   *  <li>If the discount is a percent off, the percent adjuster is applied to
   *      the group's price.
   *  <li>If the discount is an amount off, the adjuster is divided by the number of groups,
   *      and [adjuster/n] is applied to the group price.
   *  <li>If the discount is a fixed amount, the adjuster is divided by the number of groups,
   *      and the group price is set to [adjuster/n]
   * </ul>
   *
   * If functionality other than that described above, this method can be overridden to
   * do that which is necessary.  For example, rather than spreading out amount off discounts
   * across all shippingGroups, some implementations might want the first ShippingGroup to
   * get the full benefit of the discount.
   *
   * @param pTimesToDiscount the number of times we should apply the configured discount
   * @param pNumberOfGroups the number of groups in the order, including the one for which
   *        this method is computing a subtotal
   * @param pGroupSubTotal is the subtotal that needs adjusting
   * @param pAdjuster is the amount by which to adjust pGroupSubTotal
   * @param pDiscountType the way in which pAdjuster is applied to pGroupSubTotal.  May be one of:
   *        "Fixed Price", "Amount Off", or "Percent Off".
   * @param pId an ID to associate with this adjustment for error reporting
   * @exception PricingException if there was a problem computing the group subtotal
   * @return the adjusted subtotal for the input pGroupSubTotal
   */
  protected double computeGroupSubtotal(long pTimesToDiscount,
      int pNumberOfGroups,
      double pGroupSubTotal,
      double pAdjuster,
      String pDiscountType,
      String pId) 
  throws PricingException 
  {

    if (isLoggingDebug()) {
      logDebug("Computing group subtotal for discount type: " + pDiscountType);
      logDebug("Times to discount: " + pTimesToDiscount);
      logDebug("Number of groups: " + pNumberOfGroups);
      logDebug("Un-adjusted adjuster is: " + pAdjuster);
    }

    double adjustedAdjuster = pAdjuster;

    if (Constants.FIXED_PRICE.equalsIgnoreCase(pDiscountType) ||
        Constants.AMOUNT_OFF.equalsIgnoreCase(pDiscountType) ) {
      adjustedAdjuster = pAdjuster / pNumberOfGroups;
      // if we divided by zero... use zero instead; log an error here because
      // we don't expect to be trying to adjust the price of an order with no
      // shipping groups.
      if(Double.isNaN(adjustedAdjuster) || Double.isInfinite(adjustedAdjuster)) {
        if(isLoggingError()) {
          logError(MessageFormat.format(Constants.QUOTIENT_IS_NAN, "computeGroupSubtotal", Double.toString(pAdjuster), Double.toString(pNumberOfGroups)));
        }
        adjustedAdjuster = 0.0;
      }
    }

    if (isLoggingDebug()) {
      logDebug("Adjusted adjuster is: " + adjustedAdjuster);
    }

    double groupSubTotal = pGroupSubTotal;
    // adjust group subtotal as many times as specified
    for (int i = 0; i < pTimesToDiscount; i++) {
      groupSubTotal = adjust(groupSubTotal, adjustedAdjuster, pDiscountType, pId);
    }

    if (isLoggingDebug()) {
      logDebug("rounding group subtotal: " + groupSubTotal);
    }

    // round the group subtotal to N places
    groupSubTotal = getPricingTools().round(groupSubTotal);

    return groupSubTotal;

  } // end computeGroupSubtotal

  /**
   * Returns the total of the TaxableShippingItemsSubtotalPriceInfos in the given OrderPriceInfo
   * 
   * @param pOrderPriceInfo
   * @return total
   */
  protected double getTaxableShippingGroupTotal(OrderPriceInfo pOrderPriceInfo)
  {
    Map taxables = pOrderPriceInfo.getTaxableShippingItemsSubtotalPriceInfos();
    return getTotalForShippingGroups(taxables);
  }
  /**
   * Returns the total of the NonTaxableShippingItemsSubtotalPriceInfos in the given OrderPriceInfo
   * 
   * @param pOrderPriceInfo
   * @return total
   */
  protected double getNonTaxableShippingGroupTotal(OrderPriceInfo pOrderPriceInfo)
  {
    Map nontaxables = pOrderPriceInfo.getNonTaxableShippingItemsSubtotalPriceInfos();
    return getTotalForShippingGroups(nontaxables);
  }
  /**
   * Returns the total of the OrderPriceInfos in the map
   * @param pShippingGroupPriceInfos
   * @return
   */protected double getTotalForShippingGroups(Map pOrderPriceInfos)
   {
     double total = 0.0;
     Collection priceInfos = pOrderPriceInfos.values();
     Iterator priceInfoerator = priceInfos.iterator();
     OrderPriceInfo orderPriceInfo;
     while(priceInfoerator.hasNext())
     {
       orderPriceInfo = (OrderPriceInfo) priceInfoerator.next();
       total += orderPriceInfo.getAmount();
     }
     return total;
   }

   /**
    * @deprecated 
    * @see #computeGroupSubtotal(long, int, double, double, String, String, double, double, RepositoryItem, Map)
    */
   protected double computeGroupSubtotal(long pTimesToDiscount,
       int pNumberOfGroups,
       double pGroupSubTotal,
       double pAdjuster,
       String pDiscountType,
       String pOrderId,
       double pOldOrderAmount) 
   throws PricingException 
   {

     return computeGroupSubtotal(pTimesToDiscount,pNumberOfGroups,pGroupSubTotal,pAdjuster,pDiscountType,pOrderId,pOldOrderAmount,pOldOrderAmount,null, null);
   } // end computeGroupSubtotal

   /**
    * This method will set the orderDiscountShare property of each
    * CommerceItem's priceInfo.  This happens if <code>saveItemsOrderDiscountShare</code>
    * is true.  If <code>saveDetailsOrderDiscountShare</code> is true, then the 
    * orderDiscountShare property of each DetailedItemPriceInfo is also updated.
    *
    * @param pOrder the order that was discounted
    * @param pUnadjustedPrice The original pre-discount price of the order
    * @param pTotalAdjustment This is the total change to the order total, for the current promotion
    * @param pAdjuster The size of the discount
    * @param pDiscountType the way in which pAdjuster is applied to pGroupSubTotal.  May be one of:
    *        "Fixed Price", "Amount Off", or "Percent Off".
    * @param pTimesToDiscount The number of times to apply the given discount
    **/
   protected void updateItemsDiscountShare(Order pOrder, double pUnadjustedPrice, double pTotalAdjustment,
       double pAdjuster, String pDiscountType, long pTimesToDiscount)
   throws PricingException
   {
     List items = getItemsToReceiveDiscountShare(pOrder);
     List relationships = new ArrayList();

     for(int i=0;i<items.size();i++) {
       CommerceItem item = (CommerceItem) items.get(i);
       relationships.addAll(item.getShippingGroupRelationships());
     }

     if(isSaveItemsOrderDiscountShare()) {
       double shareSoFar = 0.0;
       for(int c=0;c<items.size();c++) {
         CommerceItem item = (CommerceItem) items.get(c);
         ItemPriceInfo price = item.getPriceInfo();
         double oldShare = price.getOrderDiscountShare();
         double itemAmount = price.getAmount() - oldShare;
         double ratio = itemAmount / pUnadjustedPrice;

         // if we divided by zero... use zero instead
         if(Double.isNaN(ratio) || Double.isInfinite(ratio)) {
           if(isLoggingDebug()) {
             logDebug(MessageFormat.format(Constants.QUOTIENT_IS_NAN, "updateItemDiscountShare", Double.toString(itemAmount), Double.toString(pUnadjustedPrice)));
           }
           ratio = 0.0;
         }

         double share = pTotalAdjustment * ratio;
         double roundedShare = getPricingTools().round(share);

         shareSoFar += roundedShare;
         // check leftovers
         if(c == (items.size() - 1)) {
           double leftovers = pTotalAdjustment - shareSoFar;

           if(isLoggingDebug())
             logDebug("Adding " + leftovers + " leftovers to items order discount share");

           roundedShare += leftovers;
           roundedShare = getPricingTools().round(roundedShare);
         }
         if(isLoggingDebug())
           logDebug("Setting items order discount share to: " + roundedShare + " + " + oldShare);
         price.setOrderDiscountShare(getPricingTools().round(roundedShare + oldShare));


         // now do the details
         if(isSaveDetailsOrderDiscountShare()) {
           List details = getDetailsToReceiveDiscountShare(price);
           double itemDiscShare = roundedShare;
           double detailShareSoFar = 0.0;
           for(int d=0;d<details.size();d++) {
             DetailedItemPriceInfo detail = (DetailedItemPriceInfo) details.get(d);
             double detailOldShare = detail.getOrderDiscountShare();            
             double detailRatio = 0.0;
             // if the detail amount is zero, then it's possible the price amount is
             // zero.  If we do the division the answer will be NaN
             if(detail.getAmount() > 0.0)
               detailRatio = detail.getAmount() / price.getAmount();

             // if we divided by zero... use zero instead
             if(Double.isNaN(detailRatio) || Double.isInfinite(detailRatio)) {
               if(isLoggingDebug()) {
                 logDebug(MessageFormat.format(Constants.QUOTIENT_IS_NAN, "updateItemDiscountShare", Double.toString(detail.getAmount()), 
                     Double.toString(price.getAmount())));
               }
               detailRatio = 0.0;
             }

             double detailShare = detailRatio * roundedShare;
             double detailRoundedShare = getPricingTools().round(detailShare);

             detailShareSoFar += detailRoundedShare;
             if(d == (details.size() - 1)){
               double detailLeftovers = itemDiscShare - detailShareSoFar;

               if(isLoggingDebug())
                 logDebug("Adding " + detailLeftovers + " leftovers to details order discount share");

               detailRoundedShare += detailLeftovers;
               detailRoundedShare = getPricingTools().round(detailRoundedShare);        
             }
             if(isLoggingDebug())
               logDebug("Setting details order discount share to: " + detailRoundedShare + " + " + detailOldShare);
             detail.setOrderDiscountShare(getPricingTools().round(detailRoundedShare + detailOldShare));
           }
         }
       }
     }
     else {
       // we didn't do it this way in the if block since we want the details share to add up to the
       // item share.  If we distribute completely independently, this might not be the case.
       if(isSaveDetailsOrderDiscountShare())
         getPricingTools().getDetailedItemPriceTools().distributeAmountAcrossDetails(relationships,
             pUnadjustedPrice,
             pTotalAdjustment,
             "orderDiscountShare",
             true);
     }
   }

   /**
    * This will return the amount that will eventually be discounted.  This will 
    * be priceQuote.amount by default (and there will usually be no need to change this.)
    *
    * @param pPriceQuote OrderPriceInfo representing the current price quote for the order
    * @param pOrder The order that will be discounted (ignored by default)
    * @param pPricingModel A RepositoryItems representing a PricingModel (ignored by default)
    * @param pProfile The user's profile (ignored by default)
    * @param pExtraParameters A Map of extra parameters to be used in the pricing, may be null (ignored by default)
    * @return The amount to discount... defaults to pPriceQuote.getAmount()
    **/
   protected double getAmountToDiscount(OrderPriceInfo pPriceQuote, Order pOrder, 
       RepositoryItem pPricingModel, Locale pLocale, 
       RepositoryItem pProfile, Map pExtraParameters)
   throws PricingException
   {
     return pPriceQuote.getAmount();
   }

   /**
    * This will return the price details in an item price info that will be marked as receiving a share of the 
    * order discount.  This will be all price details with a non-zero amount.
    * @see DetailedItemPriceTools#getDetailsToReceiveDiscountShare(ItemPriceInfo, Range)
    * @param pItemPriceInfo The item price info
    * @return The list of items to be marked for receiving a share of the order discount
    **/
   protected List getDetailsToReceiveDiscountShare(ItemPriceInfo pItemPriceInfo) {
     return getPricingTools().getDetailedItemPriceTools().getDetailsToReceiveDiscountShare(pItemPriceInfo,null);
   }

   /**
    * This will return the items in an order that will be marked as receiving a share of the 
    * order discount.  This will be all items in the order with a non-zero price.
    *
    * @param pOrder The order that will be discounted 
    * @return The list of items to be marked for receiving a share of the order discount
    **/
   protected List getItemsToReceiveDiscountShare(Order pOrder) {
     List items = pOrder.getCommerceItems();
     if (items == null) return null;

     boolean foundFreeItem = false;
     for(int i=0; i<items.size(); i++) {
       CommerceItem item = (CommerceItem) items.get(i);
       ItemPriceInfo amountInfo = item.getPriceInfo();
       if (Math.abs(amountInfo.getAmount()) <= 0.00001) {
         if (!foundFreeItem) {
           foundFreeItem = true;
           items = new ArrayList(items);
         }
         items.remove(i);
         i--;
       }
     }

     return items;
   }

   //--------------------------------
   // GenericService overrides
   //--------------------------------

   public void doStartService() throws ServiceException {
     if (getPricingTools() == null) {
       throw new ServiceException(MessageFormat.format(Constants.PROPERTY_NOT_SET,
           new Object [] {
           "pricingTools"
       }));
     }
   } // end doStartService
} // end of class
