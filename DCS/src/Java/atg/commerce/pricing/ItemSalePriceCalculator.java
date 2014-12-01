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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryItem;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.service.perfmonitor.PerformanceMonitor;


/**
 * A calculator which determines the sale price of an item and discounts
 * the itemPriceInfo to that amount, while at the same time maintaining the audit trail of
 * the ItemPriceInfo.  There is no rule associate with this calculator.
 * If one of ItemSalePriceCalculator's pricing methods is invoked, all input items
 * are discounted to the sale price.
 * <P>
 * Properties:
 * <ul>
 *  <li><b>onSalePropertyName</b> is the boolean property of the price source that
 *      is used to determine if the price source is on sale. (in DCS, a price source is
 *      the <code>catalogRef</code> or <code>productRef</code> of a CommerceItem.
 * </ul>
 *
 * @see ItemPriceInfo
 *
 * @beaninfo
 *   description: An ItemPricingCalculator which determines if an item should be on sale
 *   attribute: functionalComponentCategory Pricing Calculators
 *   attribute: featureComponentCategory
 *
 * @author Graham Mather
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/ItemSalePriceCalculator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ItemSalePriceCalculator
extends ItemPriceCalculator
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/ItemSalePriceCalculator.java#2 $$Change: 651448 $";

  private static final String PERFORM_MONITOR_NAME="ItemSalePriceCalculator";

  //-------------------------------------
  // property: OnSalePropertyName
  String mOnSalePropertyName = "onSale";

  /**
   * Sets property OnSalePropertyName
   **/
  public void setOnSalePropertyName(String pOnSalePropertyName) {
    mOnSalePropertyName = pOnSalePropertyName;
  }

  /**
   * Returns property OnSalePropertyName
   **/
  public String getOnSalePropertyName() {
    return mOnSalePropertyName;
  }


  /**
   * Sets the sale price with the given determined price
   * @param pPrice the price as extracted from the item
   * @param pPriceQuote ItemPriceInfo representing the current price quote for the item
   * @param pItem The item to price
   * @param pPricingModel A RepositoryItem representing a PricingModel
   * @param pProfile The user's profile
   * @param pExtraParameters A Map of extra parameters to be used in the pricing, may be null
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

    pPriceQuote.setSalePrice(pPrice);
    pPriceQuote.setOnSale(true);
    // set the ItemPriceInfo's amount to the above price

    double oldAmount = pPriceQuote.getAmount();
    double newPrice = pItem.getQuantity() * pPrice;

    if (isLoggingDebug()) {
      logDebug("rounding item sale price: " + newPrice);
    }

    newPrice = getPricingTools().round(newPrice);

    if (isLoggingDebug()) {
      logDebug("rounded item sale price to: " + newPrice);
    }

    pPriceQuote.setAmount(newPrice);

    // register the adjustment at the ItemPriceInfo level
    double itemAdjustAmount = pPriceQuote.getAmount() - oldAmount;
    PricingAdjustment itemAdjustment =
      new PricingAdjustment(Constants.SALE_PRICE_ADJUSTMENT_DESCRIPTION,
          pPricingModel,
          getPricingTools().round(itemAdjustAmount),
          pItem.getQuantity());

    pPriceQuote.getAdjustments().add(itemAdjustment);

    // set the ItemPriceInfo's detailedAmountInfo for future discounting
    List detailsList = pPriceQuote.getCurrentPriceDetails();

    if (detailsList == null) {
      throw new PricingException(MessageFormat.format(Constants.ITEM_NOT_LIST_PRICED,
                  new Object[] {pItem.getId()}));
    }

    getPricingTools().getDetailedItemPriceTools().assignSalePriceToDetails(detailsList, pPrice,
                                                                           pPriceQuote, pItem,
                                                                           pPricingModel, pLocale, pProfile,
                                                                           pExtraParameters,
                                                                           Constants.SALE_PRICE_ADJUSTMENT_DESCRIPTION);

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

  /**
   * If the item should be priced as a sales item (method <code>onSale</code> return true) then go ahead an price it
   *
   * @param pPriceQuote ItemPriceInfo representing the current price quote for the item
   * @param pItem The item to price
   * @param pPricingModel A RepositoryItem representing a PricingModel
   * @param pProfile The user's profile
   * @param pExtraParameters A Map of extra parameters to be used in the pricing, may be null
   */
  public void priceItem(ItemPriceInfo pPriceQuote, CommerceItem pItem, RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
       throws PricingException
  {
    Object priceSource = getPriceSource(pPriceQuote,pItem,
        pPricingModel,pLocale,
        pProfile,pExtraParameters);
    
    if (onSale(priceSource))
      super.priceItem(pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters);
  }

  /**
   * Returns true if the commerce item to be priced is on sale
   */
  protected boolean onSale(Object pPriceSource)
       throws PricingException
  {
    if (getOnSalePropertyName() != null) {
      try {
        Object obj = DynamicBeans.getPropertyValue(pPriceSource, getOnSalePropertyName());
        if (obj != null)
          return ((Boolean)obj).booleanValue();
        else
          return false;
      }
      catch (ClassCastException exc) {
        throw new PricingException(exc);
      }
      catch (PropertyNotFoundException exc) {
        return false;
      }
    }
    else {
      return true;
    }
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
  /**
   * Populates the sale price property of the source based on the PricingAdjustment added by this calculator and the source's list price value. 
   * @return true if the sale price value is set. 
   */
  public boolean populateItemPriceSource(ItemPriceSource pItemPriceSource, Order pOrder, CommerceItem pCommerceItem, CommerceItem pParentCommerceItem, Map pExtraParameters)
  {
    if(isLoggingDebug())
      logDebug("populateItemPriceSource started" );

    boolean returnvalue=false;
    PricingAdjustment pricingAdjustment=null;
    List pricingAdjstments= getPricingTools().findAdjustmentsByDescription(pCommerceItem.getPriceInfo(),Constants.SALE_PRICE_ADJUSTMENT_DESCRIPTION);

    //at the time of this writing there was one one adjustment. If there are more, we assume the first one to be the one we want. 
    if(pricingAdjstments != null && pricingAdjstments.size() > 0)
      pricingAdjustment = (PricingAdjustment)pricingAdjstments.get(0);

    if(pricingAdjustment!=null)
    {
      if(isLoggingDebug())
        logDebug("populateItemPriceSource adjustment found: " + pricingAdjustment);
      PricingTools pricingTools = getPricingTools();
      double listPriceTotal = pricingTools.round(pItemPriceSource.getListPrice() * pricingAdjustment.getQuantityAdjusted());
      if(isLoggingDebug())
        logDebug("populateItemPriceSource list price total: " + listPriceTotal);
      
      pItemPriceSource.setSalePrice(pricingTools.round((listPriceTotal + pricingAdjustment.getTotalAdjustment()) / pricingAdjustment.getQuantityAdjusted()));
      pItemPriceSource.setOnSale(true);

      if(isLoggingDebug())
        logDebug("populateItemPriceSource setting source sale price: " + pItemPriceSource.getSalePrice() );
      
      returnvalue=true;
    }

    if(isLoggingDebug())
      logDebug("populateItemPriceSource ended " +returnvalue);
    return returnvalue;
  }
  /**
   * If the <code>onSale</code> property is false or <code>salePrice</code> property is null, 
   * false is returned. Otherwise true. 
   */
  public boolean shouldConsumeSource(ItemPriceSource pItemPriceSource)
  {
    if(!pItemPriceSource.isOnSale() || pItemPriceSource.getSalePrice() == null)
      return false;
    else
      return true;
  }

} // end of class
