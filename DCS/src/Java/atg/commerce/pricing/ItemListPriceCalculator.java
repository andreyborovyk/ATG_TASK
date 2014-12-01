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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryItem;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.service.perfmonitor.PerformanceMonitor;

/**
 * A calculator which determines the list price of an item and sets
 * the itemPriceInfo to be that amount.  This is typically the first
 * in a series of calulations.  This calculator provides a starting price
 * for other calculators.
 * 
 * <p>
 * The ItemListPriceCalculator sets the input pPriceQuote's <code>listPrice</code>
 * property to the input <code>pPrice</code>.
 *
 * @see ItemPriceInfo
 *
 * @beaninfo
 *   description: An ItemPricingCalculator that determines the list price
 *   attribute: functionalComponentCategory Pricing Calculators
 *   attribute: featureComponentCategory
 *
 * @author Graham Mather
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/ItemListPriceCalculator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ItemListPriceCalculator
  extends ItemPriceCalculator
{

  private static final String PERFORM_MONITOR_NAME="ItemListPriceCalculator";

  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/ItemListPriceCalculator.java#2 $$Change: 651448 $";

  /**
   * Sets the list price with the given determined price
   * @param pPrice the price as extracted from the item
   * @param pPriceQuote ItemPriceInfo representing the current price quote for the item
   * @param pItem The item to price
   * @param pPricingModel A RepositoryItem representing a PricingModel
   * @param pLocale the locale in which this item should be priced
   * @param pProfile The user's profile
   * @param pExtraParameters A Map of extra parameters to be used in the pricing, may be null
   * @exception PricingException if there was a problem pricing the input pItem
   */
  protected void priceItem(double pPrice, ItemPriceInfo pPriceQuote, CommerceItem pItem,
                           RepositoryItem pPricingModel, Locale pLocale,
                           RepositoryItem pProfile, Map pExtraParameters)
    throws PricingException
  {

    String perfName = "priceItem";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    boolean perfCancelled = false;

    try {

      double totalPrice = pPrice * pItem.getQuantity();
      double listPrice = pPrice;

      if (isLoggingDebug()) {
        logDebug("input price: " + pPrice);
        logDebug("quantity: " + pItem.getQuantity());
        logDebug("item price: " + totalPrice);
        logDebug("rounding item price.");
      }

      totalPrice = getPricingTools().round(totalPrice);
      listPrice = getPricingTools().round(listPrice);

      if (isLoggingDebug()) {
	logDebug("rounded total price to: " + totalPrice);
	logDebug("rounded item list price to: " + listPrice);
      }

      // set the total price of the item before discounts, this will be unaffected by later calcs
      pPriceQuote.setRawTotalPrice(totalPrice);

      // set the itempriceInfo's list price attribute to the input price
      pPriceQuote.setListPrice(listPrice);

      // set the ItemPriceInfo's amount to the above price
      pPriceQuote.setAmount(totalPrice);

      // set the ItemPriceInfo's detailedAmountInfo for future discounting
      List detailsList = pPriceQuote.getCurrentPriceDetails();

      if (detailsList == null) {
        detailsList = new ArrayList(1);
      }

      List newDetails = getPricingTools().getDetailedItemPriceTools().createInitialDetailedItemPriceInfos(totalPrice,
                                                            pPriceQuote,
                                                            pItem,
                                                            pPricingModel,
                                                            pLocale,
                                                            pProfile,
                                                            pExtraParameters,
                                                            Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION);

      detailsList.addAll(newDetails);

      // add the adjustment at the ItemPriceInfo level too
      List adjustments = pPriceQuote.getAdjustments();
      
      if (adjustments.size() > 0) {
        adjustments.clear();
      }

      // set the first (list price) adjustment
      long quantity = pItem.getQuantity();
      double adjustAmount = listPrice * quantity;
      PricingAdjustment adjustment =
        new PricingAdjustment(Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION,
                              null,
                              getPricingTools().round(adjustAmount),
                              quantity);
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

  } // end priceItem

    //----------------------------
  // GenericService overrides
  //----------------------------

  public void doStartService() throws ServiceException {
    if (getPricingTools() == null) {
      throw new ServiceException(MessageFormat.format(Constants.PROPERTY_NOT_SET,
         new Object [] {
           "pricingTools"
         }));
    }

  } // end doStartService

  /**
   * Populates the list price property of the source based on the PricingAdjustment added by this calculator. 
   * @return true if the list price value is set. 
   */
  public boolean populateItemPriceSource(ItemPriceSource pItemPriceSource, Order pOrder, CommerceItem pCommerceItem, CommerceItem pParentCommerceItem, Map pExtraParameters)
  {
    if(isLoggingDebug())
      logDebug("populateItemPriceSource started" );

    boolean returnvalue=false;
    PricingAdjustment pricingAdjustment=null;
    List pricingAdjstments= getPricingTools().findAdjustmentsByDescription(pCommerceItem.getPriceInfo(),Constants.LIST_PRICE_ADJUSTMENT_DESCRIPTION);

    //at the time of this writing there was one one adjustment. If there are more, we assume the first one to be the one we want. 
    if(pricingAdjstments != null && pricingAdjstments.size() > 0)
      pricingAdjustment = (PricingAdjustment)pricingAdjstments.get(0);

    if(pricingAdjustment!=null)
    {
      if(isLoggingDebug())
        logDebug("populateItemPriceSource adjustment found: " + pricingAdjustment);
      PricingTools pricingTools = getPricingTools();
      double listPrice= pricingTools.round(pricingAdjustment.getTotalAdjustment() / pricingAdjustment.getQuantityAdjusted());
      
      pItemPriceSource.setListPrice(listPrice);

      if(isLoggingDebug())
        logDebug("populateItemPriceSource setting source list price: " + listPrice );
      
      returnvalue=true;
    }

    if(isLoggingDebug())
      logDebug("populateItemPriceSource ended " +returnvalue);
    return returnvalue;
  }

  /**
   * If the <code>listPrice</code> property is null, false is returned. Otherwise
   * true. 
   */
  public boolean shouldConsumeSource(ItemPriceSource pItemPriceSource)
  {
    if(pItemPriceSource.getListPrice() == null)
      return false;
    else
      return true;
  }

} // end of class


