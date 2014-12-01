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

package atg.commerce.pricing;

import atg.commerce.order.*;
import atg.payment.tax.*;
import atg.repository.*;
import atg.core.util.Address;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;

import java.beans.IntrospectionException;
import java.util.*;
import java.text.*;

/**
 * Uses the atg.payment.tax package to calculate tax for an order.
 * This class is configured with an atg.payment.tax.TaxProcessor which is passed
 * all of the information it needs to compute tax, e.g. the Amount, Shipping Address,
 * Currency Code, etc.
 *
 * Note:  This calc looks at the  <code>averagePriceRoundingDecimalPlaces</code> property
 * of PricingTools.  As of this writing, DCS does not keep track of which units of a CommerceItem
 * are being shipped to what addresses.  Also, the ItemPricingEngine is capable of pricing
 * different units of a CommerceItem differently because of various promotions.  These
 * two facts mean that we can't be perfectly sure about the exact amount that is to be
 * taxed.  Instead, this calculator takes an average CommerceItem unit price and multiplies
 * it by the number of units in a particular shipping group.  All this dividing may
 * potentially leave us with a price that has an infinitely repeating number of decimals.
 * The <code>averagePriceRoundingDecimalPlaces</code> property dictates how to truncate
 * these prices before sending them to the TaxProcessor implementation.  This calculator
 * keeps track of any missing money and adds it to one of the totals.
 *
 * @beaninfo
 *   description: A TaxPricingCalculator that integrates into a tax processor
 *   attribute: functionalComponentCategory Pricing Calculators
 *   attribute: featureComponentCategory
 *
 * @author Graham Mather
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/TaxProcessorTaxCalculator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class TaxProcessorTaxCalculator extends GenericService implements TaxPricingCalculator
{
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/TaxProcessorTaxCalculator.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------

  public static final String DETAILED_ITEM_PRICE_TAX_PROPERTY = "tax";

  private static final String PERFORM_MONITOR_NAME="TaxProcessorTaxCalculator";

  //-------------------------------------
  /**
   * Properties
   */

  //-------------------------------------
  // property: TaxStatusProperty
  String mTaxStatusProperty;

  /**
   * Sets property TaxStatusProperty
   **/
  public void setTaxStatusProperty(String pTaxStatusProperty) {
    mTaxStatusProperty = pTaxStatusProperty;
  }

  /**
   * Returns property TaxStatusProperty
   **/
  public String getTaxStatusProperty() {
    return mTaxStatusProperty;
  }

  //-------------------------------------
  // property: TaxProcessor
  /** the tax processor which does the tax calculation */
  TaxProcessor mTaxProcessor;

  /**
   * Set property TaxProcessor
   * @beaninfo description: the tax processor which does the tax calculation
   * @param pTaxProcessor new value to set
   */
  public void setTaxProcessor(TaxProcessor pTaxProcessor)
  {mTaxProcessor = pTaxProcessor;}

  /**
   * Get property TaxProcessor
   * @beaninfo description: the tax processor which does the tax calculation
   * @return TaxProcessor
   */
  public TaxProcessor getTaxProcessor()
  {return mTaxProcessor;}


  //-------------------------------------
  // property: OrderManager
  /** the order manager which this class uses to work with item relationships */
  OrderManager mOrderManager;

  /**
   * Set property OrderManager
   * @beaninfo description: the order manager which this class uses to work with item relationships
   * @param pOrderManager new value to set
   */
  public void setOrderManager(OrderManager pOrderManager)
  {mOrderManager = pOrderManager;}

  /**
   * Get property OrderManager
   * @beaninfo description: the order manager which this class uses to work with item relationships
   * @return OrderManager
   */
  public OrderManager getOrderManager()
  {return mOrderManager;}


  //-------------------------------------
  // property: PricingTools
  /** the PricingTools object which helps this calculator calculate tax' */
  PricingTools mPricingTools;

  /**
   * the PricingTools object which helps this calculator calculate tax
   * @beaninfo description: the PricingTools object which helps this calculator calculate tax
   * @param pPricingTools new value to set
   */
  public void setPricingTools(PricingTools pPricingTools)
  {mPricingTools = pPricingTools;}

  /**
   * the PricingTools object which helps this calculator calculate tax'
   * @beaninfo description: the PricingTools object which helps this calculator calculate tax
   * @return property PricingTools
   */
  public PricingTools getPricingTools()
  {return mPricingTools;}



  //-------------------------------------
  // property: VerifyAddresses
  /** flag that determines whether this calculator will attempt to verify addresses */
  boolean mVerifyAddresses;

  /**
   * flag that determines whether this calculator will attempt to verify addresses
   * @beaninfo description: flag that determines whether this calculator will attempt to verify addresses
   * @param pVerifyAddresses new value to set
   */
  public void setVerifyAddresses(boolean pVerifyAddresses)
  {mVerifyAddresses = pVerifyAddresses;}

  /**
   * Test property VerifyAddresses
   * @beaninfo description: flag that determines whether this calculator will attempt to verify addresses
   * @return VerifyAddresses
   */
  public boolean isVerifyAddresses()
  {return mVerifyAddresses;}


  //-------------------------------------
  // property: CalculateTaxByShipping
  /** flag that determines whether this calculator should calculate tax on a per-shippingGroup basis. */
  boolean mCalculateTaxByShipping = false;

  /**
   * flag that determines whether this calculator should calculate tax on a per-shippingGroup basis.
   * @beaninfo description: flag that determines whether this calculator should calculate tax on a per-shippingGroup basis.
   * @param pCalculateTaxByShipping new value to set
   */
  public void setCalculateTaxByShipping(boolean pCalculateTaxByShipping)
  {mCalculateTaxByShipping = pCalculateTaxByShipping;}

  /**
   * flag that determines whether this calculator should calculate tax on a per-shippingGroup basis.
   * @beaninfo description: flag that determines whether this calculator should calculate tax on a per-shippingGroup basis.
   * @return property CalculateTaxByShipping
   */
  public boolean getCalculateTaxByShipping()
  {return mCalculateTaxByShipping;}

  //-------------------------------------
  // property: billingAddressPropertyName
  //-------------------------------------
  private String mBillingAddressPropertyName = "billingAddress";

  /**
   * Returns the billingAddressPropertyName
   */
  public String getBillingAddressPropertyName() {
    return mBillingAddressPropertyName;
  }

  /**
   * Sets the billingAddressPropertyName
   */
  public void setBillingAddressPropertyName(String pBillingAddressPropertyName) {
    mBillingAddressPropertyName = pBillingAddressPropertyName;
  }

  //-------------------------------------
  /**
   * TaxPricingCalculator implementation
   */

  //-------------------------------------
  /**
   * Tax an order within a context.  This implementation consults the configured
   * TaxProcessor for the tax.  The steps taken here are:
   * <ul>
   *  <li>create and configure a TaxRequestInfo
   *  <li>pass the TaxRequestInfo to the TaxProcessor
   *  <li>interpret the TaxStatus on return
   *  <li>configure the output TaxPriceInfo with information gleaned from the TaxStatus
   * </ul>
   *
   * NOTE:
   *  The algorithm used by this method to determine the tax of the order finds
   *  an average price for each item and uses that price in determining the tax
   *  on every unit of that item.  The reason for this is that there is currently no
   *  way to specify which individual units of a CommerceItem belong in which shipping
   *  relationship.  Additionally, units as small as one of a CommerceItem
   *  can be priced differently from other units in the same CommerceItem.  These two
   *  situations mean that when taxing a CommerceItem, there is no way to know
   *  explicitly what the total price of a shipping relationship is.  Therefore, an
   *  average price is computed for all items, and that price is multiplied by the
   *  quantity of the CommerceItem that reside in the current relationship to find the
   *  total price of the relationship.
   *  <p>
   *  Ideally, we should change a shipping relationship to include an exact specification
   *  of which units of a CommerceItem belong to that relationship.
   *
   *
   * @param pPriceQuote TaxPriceInfo representing the tax quote for the order
   * @param pOrder The order to tax
   * @param pPricingModels A Collection of RepositoryItems representing PricingModels
   * @param pProfile The user's profile
   * @param pExtraParameters A Map of extra parameters to be used in the pricing, may be null
   */
  public void priceTax(TaxPriceInfo pPriceQuote, Order pOrder, RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
    throws PricingException 
  {
    String perfName = "priceTax";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    boolean perfCancelled = false;

    try {

      // got to have an order
      if (pOrder == null) {
        throw new PricingException(MessageFormat.format(Constants.PARAMETER_NOT_SET,
                                                        new Object[] {
                                                          "pOrder",
                                                          "priceTax"
                                                        }));
      }

      if (pPriceQuote == null) {
        throw new PricingException(MessageFormat.format(Constants.PARAMETER_NOT_SET,
                                                        new Object[] {"pPriceQuote", "priceTax"}));
      }

      // make sure that there's a price and that it's not zero
      if (pOrder.getPriceInfo() == null || pOrder.getPriceInfo().getAmount() == 0) {
        // return no tax
        clearTaxPriceInfo(pPriceQuote);
        return;
      }

      // configure the TaxRequestInfo
      TaxRequestInfoImpl tri = new TaxRequestInfoImpl();

      // set the order id
      tri.setOrderId(pOrder.getId());

      // set the order
      tri.setOrder(pOrder);
      
      /**
       * calculate billing address for all shipping groups ahead of time.
       * billing address may not be available.  Try to find the first PaymentGroup
       * of the order that has an address associated with it and use that as this
       * tax calculation's billing address.  If no payment group has an address,
       * there's no billing address.
       */
      List paymentGroups = pOrder.getPaymentGroups();
      if (paymentGroups != null) {
        Iterator paymentIterator = paymentGroups.iterator();
        while (paymentIterator.hasNext()) {
          PaymentGroup pg = (PaymentGroup) paymentIterator.next();
          try {
            if (DynamicBeans.getBeanInfo(pg).hasProperty(getBillingAddressPropertyName())) {
              Address billingAddress = (Address) DynamicBeans.getPropertyValue(pg, getBillingAddressPropertyName());
              if (billingAddress != null) {
                tri.setBillingAddress(billingAddress);
                break;
              }
            }
          }
          catch (IntrospectionException e) {
            // do nothing, just skip to the next one
          }
          catch (PropertyNotFoundException e) {
            // won't happen because we check for it first
          }
        }
      }

      // if, after all that, we still don't have a billing address, bail

      List shippingDestinations = new LinkedList();

      List shippingGroups = pOrder.getShippingGroups();
      Iterator groupIterator = shippingGroups.iterator();

      // remember how many of each item has been taxed.  If we're about to tax the
      // last of an item, add the remainder to it.
      Map taxedItems = new HashMap();
      // remember how much of each item is taxable.
      Map taxedItemAmounts = new HashMap();

      while (groupIterator.hasNext()) {
        ShippingGroup group = (ShippingGroup) groupIterator.next();

        ShippingDestinationImpl dest = new ShippingDestinationImpl();

        // currency code comes from the order
        dest.setCurrencyCode(pOrder.getPriceInfo().getCurrencyCode());
        dest.setShippingAmount(group.getPriceInfo().getAmount());

        Address shippingAddress = determineShippingAddress(group);
        dest.setShippingAddress(shippingAddress);


        // create a TaxableItem for all the items to be associated with this group
        List taxableItems = new LinkedList();

        // when creating taxableitems, if there are no relationships in this group,
        // use the order's items to create the taxableitems
        if (group.getCommerceItemRelationships() == null ||
            group.getCommerceItemRelationships().isEmpty() ) {

          List commerceItems = pOrder.getCommerceItems();
          Iterator commerceItemIterator = commerceItems.iterator();
          while (commerceItemIterator.hasNext()) {
            CommerceItem item = (CommerceItem) commerceItemIterator.next();

            TaxableItemImpl ti = new TaxableItemImpl();

            // just in case, we should round the taxableItem's amount.
            // this price should already be rounded
            if (isLoggingDebug()) {
              logDebug("rounding item: " + item.getId()+ " amount for taxing: " + item.getPriceInfo().getAmount());
            }

            //double roundedAmount = getPricingTools().round(item.getPriceInfo().getAmount());
            // get the taxable amount instead of just the amount
            double taxableAmount = getPricingTools().calculateTaxableAmount(item, pOrder, pLocale, 
                                                                            pProfile, pExtraParameters);
            double roundedAmount = getPricingTools().round(taxableAmount);
            
            if (isLoggingDebug()) {
              logDebug("rounded item price for taxing to : " + roundedAmount);
            }

            ti.setAmount(roundedAmount);
            ti.setCatalogRefId(item.getCatalogRefId());
            ti.setProductId(item.getAuxiliaryData().getProductId());

            ti.setQuantity(item.getQuantity());

	    if (getTaxStatusProperty() != null) {
              Object catalogRef = item.getAuxiliaryData().getCatalogRef();
	      if (catalogRef != null) {
                try {
                  Object taxStatus = DynamicBeans.getPropertyValue(catalogRef, getTaxStatusProperty());
		  
		  if (taxStatus instanceof String) {
		    if (isLoggingDebug())
                      logDebug("Setting taxStatus to " + (String)taxStatus);
		    ti.setTaxStatus((String)taxStatus);
		  }
		  else {
		    if (isLoggingDebug())
		      logDebug("Setting taxStatus to " + String.valueOf(taxStatus));
		    ti.setTaxStatus(String.valueOf(taxStatus));
		  }
                }
                catch (PropertyNotFoundException exc) {
                  if (isLoggingDebug())
                    logDebug("Could not find taxStatus property for " + catalogRef, exc);
                }
              }
            }

            taxableItems.add(ti);
            taxedItems.put(item, Long.valueOf(item.getQuantity()));
            taxedItemAmounts.put(item, Double.valueOf(roundedAmount));
          } // end for each item
        } // end if no relationships

        // else there is at least one relationship - create taxable items from relationships
        else {

          /**
           * create a TaxableItem for each relationship.  Since no unit of an item can exist in
           * two separate relationships, this should cover all items to be taxed.
           */

          Iterator relationshipIterator = group.getCommerceItemRelationships().iterator();
          while (relationshipIterator.hasNext()) {
            ShippingGroupCommerceItemRelationship rel = (ShippingGroupCommerceItemRelationship) relationshipIterator.next();
            TaxableItemImpl ti;
            boolean useAverage = false;
            double totalTaxAmount = 0.0;

            if((getPricingTools().isShippingSubtotalUsesAverageItemPrice()) ||
               (rel.getRange() == null))
              useAverage = true;

            if(useAverage)
              ti = (TaxableItemImpl) createTaxableItemForRelationshipByAverage(rel, tri,
                                                                               pOrder, pLocale,
                                                                               pProfile, pExtraParameters);
            else
              ti = (TaxableItemImpl) createTaxableItemForRelationship(rel, tri,
                                                                      pOrder, pLocale,
                                                                      pProfile, pExtraParameters);

            if (ti == null) {
              // Nothing to do for the current relationship
              continue;
            }

            long quantityTaxed = 0;
            totalTaxAmount += ti.getAmount();

            if(useAverage)
              quantityTaxed = checkTaxableItemByAverage(ti, rel, taxedItems, taxedItemAmounts,
                                                        pOrder, pLocale, pProfile,pExtraParameters);
            else
              quantityTaxed = checkTaxableItem(ti, rel, taxedItems, taxedItemAmounts, totalTaxAmount,
                                               pOrder, pLocale, pProfile, pExtraParameters);

            if(quantityTaxed > 0) {
              taxableItems.add(ti);            
              taxedItems.put(rel.getCommerceItem(), Long.valueOf(quantityTaxed));
              Double currentTaxedAmount=(Double)taxedItemAmounts.get(rel.getCommerceItem());
              double currentax=0.0;
              if(currentTaxedAmount != null)
                currentax= currentTaxedAmount.doubleValue();
              taxedItemAmounts.put(rel.getCommerceItem(), Double.valueOf(currentax + ti.getAmount()));
            }

          } // end for each relationship

        } // end if there are relationships

        // the shipping group subtotal is the total amount of the taxable items (from the
        // group subtotal price info)
        double shippingGroupSubtotal = 0.0;

        if (group.getId() == null) {
          if (isLoggingDebug()) {
            logDebug("DANGER: group ID is null");
          }
        } else {

          OrderPriceInfo pi = (OrderPriceInfo) pOrder.getPriceInfo().getTaxableShippingItemsSubtotalPriceInfos().get(group.getId());

          if (pi == null) {
            if (isLoggingDebug()) {
              logDebug("WARNING: no entry in order's group taxable subtotal map found for group ID: " + group.getId());
            }
          } else {
            shippingGroupSubtotal = pi.getAmount();
          }
        } // end else group ID not null

        if (isLoggingDebug()) {
          logDebug("shipping group subtotal for taxing: " + shippingGroupSubtotal);
        }

        // set taxable item amount to be group's taxable subtotal
        dest.setTaxableItemAmount(shippingGroupSubtotal);

        // set the taxableitems
        TaxableItem [] taxableItemArray = new TaxableItem [taxableItems.size()];
        taxableItemArray = (TaxableItem []) taxableItems.toArray(taxableItemArray);
        dest.setTaxableItems(taxableItemArray);

        // if the destination's taxable amount is zero, don't even add it
        if (dest.getTaxableItemAmount() > 0) {
          shippingDestinations.add(dest);
        }

      } // end for each group

      ShippingDestination [] shippingDestinationArray = new ShippingDestination[shippingDestinations.size()];
      shippingDestinationArray = (ShippingDestination [])shippingDestinations.toArray(shippingDestinationArray);
      tri.setShippingDestinations(shippingDestinationArray);

      // if there are no destinations, don't even bother calculating tax
      if (shippingDestinations.size() > 0) {

        if (getCalculateTaxByShipping()) {
          calculateTaxByShipping(tri, pPriceQuote, pOrder, pPricingModel, pLocale, pProfile, pExtraParameters);
        } else {
          calculateTax(tri, pPriceQuote, pOrder, pPricingModel, pLocale, pProfile, pExtraParameters);
        }
      }

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


  } // end priceTax


  /**
   * Protected methods
   */
  /**
   * Given the input TaxRequestInfo object, modify the input pPriceQuote object
   * to reflect the current tax.  This method differs from calculateTax in that
   * it calls through to the 'calculateTaxByShipping' tax API call.
   *
   * @param pTRI the TaxRequestInfo object which represents a request from a TaxProcessor
   *        for a tax calculation.  Required.
   * @param pPriceQuote the TaxPriceInfo to modify to reflect the calculated tax.  Required.
   * @param pOrder the order for which tax is being calculated.  Optional.
   * @param pPricingModel the pricing model which is modifying the tax total.  Optional.
   *        Not used in DCS at this time.
   * @param pLocale the local in which the tax should be calculated. Optional.
   * @param pProfile the person for whom the tax is being calculated. Optional.
   * @param pExtraParameters any extra information needed to calculate tax. Optional.
   */
  protected void calculateTaxByShipping (TaxRequestInfo pTRI,
                                         TaxPriceInfo pPriceQuote,
                                         Order pOrder,
                                         RepositoryItem pPricingModel,
                                         Locale pLocale,
                                         RepositoryItem pProfile,
                                         Map pExtraParameters) throws PricingException {

    // price the shippinggroup
    TaxStatus [] statuses = getTaxProcessor().calculateTaxByShipping(pTRI);

    // sum the status amounts
    double totalAmount = 0.0;
    double totalCityTax = 0.0;
    double totalCountyTax = 0.0;
    double totalStateTax = 0.0;
    double totalDistrictTax = 0.0;
    double totalCountryTax = 0.0;

    List shippingGroupList = pOrder.getShippingGroups();
    Iterator shippingGroupIterator = shippingGroupList.iterator();

    Map shippingItemsTaxPriceInfos = pPriceQuote.getShippingItemsTaxPriceInfos();

    for (int i = 0; i < statuses.length; i++) {
      TaxStatus status = statuses[i];

      // make sure the status' transaction succeeded
      // NOTE: this is done because even if subsequent shippinggroups were taxed successfully,
      //       the order's tax couldn't be calculated.
      if (!status.getTransactionSuccess()) {
        throw new PricingException(status.getErrorMessage());
      }

      totalAmount += status.getAmount();
      totalCityTax += status.getCityTax();
      totalCountyTax += status.getCountyTax();
      totalStateTax += status.getStateTax();
      totalDistrictTax += status.getDistrictTax();
      totalCountryTax += status.getCountryTax();

      // register the status' tax price, if applicable
      TaxPriceInfo taxinfo = new TaxPriceInfo();

      if (isLoggingDebug()) {
        logDebug("rounding status tax amount: " + status.getAmount());
      }

      double rounded = getPricingTools().round(status.getAmount());

      if (isLoggingDebug()) {
        logDebug("rounded status amount to: " + rounded);
      }

      taxinfo.setAmount(rounded);
      taxinfo.setCityTax(status.getCityTax());
      taxinfo.setCountyTax(status.getCountyTax());
      taxinfo.setStateTax(status.getStateTax());
      taxinfo.setDistrictTax(status.getDistrictTax());
      taxinfo.setCountryTax(status.getCountryTax());

      // get the shipping group corresponding to this status

      while (shippingGroupIterator.hasNext())
      {
        ShippingGroup group = (ShippingGroup) shippingGroupIterator.next();
        OrderPriceInfo pi =
          (OrderPriceInfo)
          pOrder.getPriceInfo().getShippingItemsSubtotalPriceInfos().get(
                                                                         group.getId());
        if (pi == null ||
            pi.getAmount() == 0.0)
        {
          // Construct a TaxPriceInfo showing no tax
          TaxPriceInfo zeroTax = new TaxPriceInfo();
          zeroTax.setAmount(0.0);
          zeroTax.setCityTax(0.0);
          zeroTax.setCountyTax(0.0);
          zeroTax.setStateTax(0.0);
          zeroTax.setDistrictTax(0.0);
          zeroTax.setCountryTax(0.0);

          // map the shippinggroup ID to this taxInfo showing no tax
          shippingItemsTaxPriceInfos.put(group.getId(), zeroTax);
          // set the tax property in each item's detailedItemPriceInfos
          assignItemTaxAmounts(group, zeroTax, pOrder.getPriceInfo(), pi, status);
        }
        else
        {
          shippingItemsTaxPriceInfos.put(group.getId(), taxinfo);
          // set the tax property in each item's detailedItemPriceInfos
          assignItemTaxAmounts(group, taxinfo, pOrder.getPriceInfo(), pi, status);
          break;
        }
      }  // end search for shippingGroup to go with this tax status
    } // end for each returned status

    // Set zero-tax tax infos for any remaining groups
    while (shippingGroupIterator.hasNext())
    {
      ShippingGroup group = (ShippingGroup) shippingGroupIterator.next();
      OrderPriceInfo pi =
        (OrderPriceInfo)
        pOrder.getPriceInfo().getShippingItemsSubtotalPriceInfos().get(
                                                                       group.getId());
      // Construct a TaxPriceInfo showing no tax
      TaxPriceInfo zeroTax = new TaxPriceInfo();
      zeroTax.setAmount(0.0);
      zeroTax.setCityTax(0.0);
      zeroTax.setCountyTax(0.0);
      zeroTax.setStateTax(0.0);
      zeroTax.setDistrictTax(0.0);
      zeroTax.setCountryTax(0.0);

      // map the shippinggroup ID to this taxInfo showing no tax
      shippingItemsTaxPriceInfos.put(group.getId(), zeroTax);
      // set the tax property in each item's detailedItemPriceInfos
      // no need to do this if there is no price info
      // TBD Need to decide if this is really necessary.  Why assign zero amounts? :)
      if(pi != null)
        assignItemTaxAmounts(group, zeroTax, pOrder.getPriceInfo(), pi, null);
    }

    // register the tax in the taxPriceInfo

    if (isLoggingDebug()) {
      logDebug("rounding total tax amount: " + totalAmount);
    }

    double roundedAmount = getPricingTools().round(totalAmount);

    if (isLoggingDebug()) {
      logDebug("rounded total tax amount to : " + roundedAmount);
    }

    pPriceQuote.setAmount(roundedAmount);
    pPriceQuote.setCityTax(totalCityTax);
    pPriceQuote.setCountyTax(totalCountyTax);
    pPriceQuote.setStateTax(totalStateTax);
    pPriceQuote.setDistrictTax(totalDistrictTax);
    pPriceQuote.setCountryTax(totalCountryTax);
  } // end calculateTaxByShipping


  /**
   * Given the input TaxRequestInfo object, modify the input pPriceQuote object
   * to reflect the current tax.
   * @param pTRI the TaxRequestInfo object which represents a request from a TaxProcessor
   *        for a tax calculation. Required.
   * @param pPriceQuote the TaxPriceInfo to modify to reflect the calculated tax.  Required.
   * @param pOrder the order for which tax is being calculated.  Optional.
   * @param pPricingModel the pricing model which is modifying the tax total.  Optional.
   *        Not used in DCS at this time.
   * @param pLocale the local in which the tax should be calculated. Optional.
   * @param pProfile the person for whom the tax is being calculated. Optional.
   * @param pExtraParameters any extra information needed to calculate tax. Optional.
   */
  protected void calculateTax(TaxRequestInfo pTRI,
                              TaxPriceInfo pPriceQuote,
                              Order pOrder,
                              RepositoryItem pPricingModel,
                              Locale pLocale,
                              RepositoryItem pProfile,
                              Map pExtraParameters) throws PricingException {
    // price the shippinggroup
    TaxStatus status = getTaxProcessor().calculateTax(pTRI);

    if (status.getTransactionSuccess() == false)
      throw new PricingException(status.getErrorMessage());

    // register the tax in the taxPriceInfo
    if (isLoggingDebug()) {
      logDebug("rounding tax amount: " + status.getAmount());
    }

    double roundedAmount = getPricingTools().round(status.getAmount());

    if (isLoggingDebug()) {
      logDebug("rounded total tax amount to : " + roundedAmount);
    }

    pPriceQuote.setAmount(roundedAmount);
    pPriceQuote.setCityTax(status.getCityTax());
    pPriceQuote.setCountyTax(status.getCountyTax());
    pPriceQuote.setStateTax(status.getStateTax());
    pPriceQuote.setDistrictTax(status.getDistrictTax());
    pPriceQuote.setCountryTax(status.getCountryTax());

    // set the tax property in each item's detailedItemPriceInfos
    assignItemTaxAmounts(pOrder, pPriceQuote, status);
  }

  /**
     * Determines a shipping address based on a ShippingGroup
     */
  protected Address determineShippingAddress(ShippingGroup pGroup) {
    if (pGroup instanceof HardgoodShippingGroup) {
      return ((HardgoodShippingGroup) pGroup).getShippingAddress();
    } else {
      return null;
    }
  } // end determineShippingAddress


  /**
   * create a TaxableItem for the input relationship and TaxRequestInfo.  This implementation
   * finds the average price of a single unit of the relationship's CommerceItem and uses
   * that price as a basis for determining the total cost of the relationship.  This
   * in turn affects the amount of tax on this relationship.  Extending classes can override
   * this method to gain finer control over the manner in which units of a CommerceItem are
   * assigned to a CommerceItemRelationship.
   *
   * @param pRelationship the relationship for which a TaxableItem is needed.
   * @param pTaxRequestInfo the TaxRequestInfo object into which the returned TaxableItem will be
   *        inserted.
   *
   */
  protected TaxableItem createTaxableItemForRelationshipByAverage(CommerceItemRelationship pRelationship,
                                                                  TaxRequestInfo pTaxRequestInfo,
                                                                  Order pOrder,
                                                                  Locale pLocale,
                                                                  RepositoryItem pProfile,
                                                                  Map pExtraParameters)
    throws PricingException
    
  {

    CommerceItem commerceItem = pRelationship.getCommerceItem();

    // get the item and the quantity of the item that is in this relationship.
    // switch on type of relationship to get the actual quantity that the relationship
    // represents

    long relationshipQuantity = 0;

    if (pRelationship.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITY) {
      relationshipQuantity = pRelationship.getQuantity();
    } else if (pRelationship.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITYREMAINING) {
      relationshipQuantity = getOrderManager().getShippingGroupManager().getRemainingQuantityForShippingGroup(commerceItem);
    } else {
      throw new PricingException(MessageFormat.format(Constants.BAD_SHIPPING_GROUP_TYPE,
                                                      new Object [] {
                                                        Integer.valueOf(pRelationship.getRelationshipType())
                                                          }));
    }

    if (isLoggingDebug()) {
      logDebug("total quantity (from this relationship) to tax: " + relationshipQuantity);
    }

    // if there's no quantity to tax, continue
    if (relationshipQuantity == 0) {
      if (isLoggingDebug()) {
        logDebug("no quantity of this relationship to tax, continuing.");
      }
      return null;
    }
    double taxableAmount = 0.0;
    
    if(pOrder != null) {
      taxableAmount = 
        getPricingTools().calculateTaxableAmountByAverage(pRelationship, pOrder, pLocale, pProfile, pExtraParameters);
    } else {
      taxableAmount = 
        getPricingTools().round(getPricingTools().getAverageItemPrice(commerceItem) * relationshipQuantity);
    }

    TaxableItemImpl ti = new TaxableItemImpl();

    // quantity is the relationship quantity
    ti.setQuantity(relationshipQuantity);

    // amount is average item price times number of items in relationship
    ti.setAmount(taxableAmount);

    // catalogRefId is commerceItem's catalogRefId
    ti.setCatalogRefId(commerceItem.getCatalogRefId());

    // productId is CommerceItem's AuxiliaryData's productId
    ti.setProductId(commerceItem.getAuxiliaryData().getProductId());

    if (getTaxStatusProperty() != null) {
      Object catalogRef = commerceItem.getAuxiliaryData().getCatalogRef();
      if (catalogRef != null) {
        try {
          Object taxStatus = DynamicBeans.getPropertyValue(catalogRef, getTaxStatusProperty());
          if (taxStatus instanceof String) {
            if (isLoggingDebug())
              logDebug("Setting taxStatus to " + (String)taxStatus);
            ti.setTaxStatus((String)taxStatus);
          }
          else {
            if (isLoggingDebug())
              logDebug("Setting taxStatus to " + String.valueOf(taxStatus));
            ti.setTaxStatus(String.valueOf(taxStatus));
          }
        }
        catch (PropertyNotFoundException exc) {
          if (isLoggingDebug())
            logDebug("Could not find taxStatus property for " + catalogRef, exc);
        }
      }
    }

    return ti;
  }


  /**
   * create a TaxableItem for the input relationship and TaxRequestInfo.  This implementation
   * finds the average price of a single unit of the relationship's CommerceItem and uses
   * that price as a basis for determining the total cost of the relationship.  This
   * in turn affects the amount of tax on this relationship.  Extending classes can override
   * this method to gain finer control over the manner in which units of a CommerceItem are
   * assigned to a CommerceItemRelationship.
   *
   * @param pRelationship the relationship for which a TaxableItem is needed.
   * @param pTaxRequestInfo the TaxRequestInfo object into which the returned TaxableItem will be
   *        inserted.
   */
  protected TaxableItem createTaxableItemForRelationshipByAverage(CommerceItemRelationship pRelationship,
                                                                  TaxRequestInfo pTaxRequestInfo)
    throws PricingException
  {
    return createTaxableItemForRelationshipByAverage(pRelationship, pTaxRequestInfo, null, null, null, null);
  } // end createTaxableItemForRelationship


  /**
   * create a TaxableItem for the input relationship and TaxRequestInfo.  This implementation
   * finds the average price of a single unit of the relationship's CommerceItem and uses
   * that price as a basis for determining the total cost of the relationship.  
   *
   * @param pRelationship the relationship for which a TaxableItem is needed.
   * @param pTaxRequestInfo the TaxRequestInfo object into which the returned TaxableItem will be
   *        inserted.
   */
  protected TaxableItem createTaxableItemForRelationship(CommerceItemRelationship pRelationship,
                                                         TaxRequestInfo pTaxRequestInfo)
    throws PricingException
  {
    return createTaxableItemForRelationshipByAverage(pRelationship, pTaxRequestInfo);
  }

  
  /**
   * create a TaxableItem for the input relationship and
   * TaxRequestInfo.  This implementation finds the sum of the amounts
   * for each DetailedItemPriceInfo that apply to a Range that is
   * inside the Range for this Relationship Extending classes can
   * override this method to gain finer control over the manner in
   * which units of a CommerceItem are assigned to a
   * CommerceItemRelationship.
   *
   * @param pRelationship the relationship for which a TaxableItem is needed.
   * @param pTaxRequestInfo the TaxRequestInfo object into which the returned TaxableItem will be
   *        inserted.  
   **/
  protected TaxableItem createTaxableItemForRelationship(CommerceItemRelationship pRelationship,
                                                         TaxRequestInfo pTaxRequestInfo,
                                                         Order pOrder,
                                                         Locale pLocale,
                                                         RepositoryItem pProfile,
                                                         Map pExtraParameters)
    throws PricingException
  {
    if(!(pRelationship instanceof ShippingGroupCommerceItemRelationship))
      return createTaxableItemForRelationshipByAverage(pRelationship, pTaxRequestInfo, pOrder, 
                                                       pLocale, pProfile, pExtraParameters);
    
    ShippingGroupCommerceItemRelationship relationship = (ShippingGroupCommerceItemRelationship) pRelationship;

    // if this relationship doesn't have a range, go ahead and use the old method
    // since we can't get a reliable subtotal for this relationship without it
    if(relationship.getRange() == null)
      return createTaxableItemForRelationshipByAverage(pRelationship, pTaxRequestInfo, pOrder, 
                                                       pLocale, pProfile, pExtraParameters);

    CommerceItem commerceItem = relationship.getCommerceItem();

    // get the item and the quantity of the item that is in this relationship.
    // switch on type of relationship to get the actual quantity that the relationship
    // represents

    long relationshipQuantity = 0;

    if (relationship.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITY) {
      relationshipQuantity = relationship.getQuantity();
    } else if (relationship.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITYREMAINING) {
      relationshipQuantity = getOrderManager().getShippingGroupManager().getRemainingQuantityForShippingGroup(commerceItem);
    } else {
      throw new PricingException(MessageFormat.format(Constants.BAD_SHIPPING_GROUP_TYPE,
                                                      new Object [] {
                                                        Integer.valueOf(relationship.getRelationshipType())
                                                          }));
    }

    if (isLoggingDebug()) {
      logDebug("total quantity (from this relationship) to tax: " + relationshipQuantity);
    }

    // if there's no quantity to tax, continue
    if (relationshipQuantity == 0) {
      if (isLoggingDebug()) {
        logDebug("no quantity of this relationship to tax, continuing.");
      }
      return null;
    }

    /**
     * instead of finding the average item price (which is what used to happen)
     * sum the specific detail prices
     */

    double totalPrice = getPricingTools().calculateTaxableAmount(relationship, pOrder, pLocale, 
                                                                 pProfile, pExtraParameters);

    if(isLoggingDebug())
      logDebug("Taxable amount of relationship: " + totalPrice);

    TaxableItemImpl ti = new TaxableItemImpl();

    // quantity is the relationship quantity
    ti.setQuantity(relationshipQuantity);

    // is it necessary to round?  We'll take care of the leftovers
    double roundedAmount = getPricingTools().round(totalPrice);

    // amount is sum of details for range
    ti.setAmount(roundedAmount);

    // catalogRefId is commerceItem's catalogRefId
    ti.setCatalogRefId(commerceItem.getCatalogRefId());

    // productId is CommerceItem's AuxiliaryData's productId
    ti.setProductId(commerceItem.getAuxiliaryData().getProductId());

    if (getTaxStatusProperty() != null) {
      Object catalogRef = commerceItem.getAuxiliaryData().getCatalogRef();
      if (catalogRef != null) {
        try {
          Object taxStatus = DynamicBeans.getPropertyValue(catalogRef, getTaxStatusProperty());
          if (taxStatus instanceof String)
            ti.setTaxStatus((String)taxStatus);
        }
        catch (PropertyNotFoundException exc) {
          if (isLoggingDebug())
            logDebug("Could not find taxStatus property for " + catalogRef, exc);
        }
      }
    }

    return ti;

  } // end createTaxableItemForRelationship

  /**
   * This method verifies the amount for each relationship.  It assumes the amounts were set
   * by average the unit price times the quantity.  It takes care of leftovers that this rounding
   * may have missed.
   *
   * @param pTaxableItem The taxable item as returned by createTaxableItemForRelationship
   * @param pRelationship The relationship that the taxable item was created from
   * @param pTaxedItems This is a map from an item to the amount of the item that will be taxed
   *                    This map is used to determine any leftovers remaining due to rounding
   * @param pTaxedItemAmountss This is a map from an item to the amount of the item that will be taxable
   *                    This map is used to determine any leftovers remaining due to rounding.  This
   *                    parameter is included for consistency and is not used.  We use the average unit price
   *                    times the quantity instead.
   * @param pTaxableItems This is the list of taxable items.  Once all verification is finished, 
   *                      pTaxableItem is added to this list
   * @return the quantity of the relationship that will be taxed
   **/
  private long checkTaxableItemByAverage(TaxableItemImpl pTaxableItem, 
                                         ShippingGroupCommerceItemRelationship pRelationship, 
                                         Map pTaxedItems,
                                         Map pTaxedItemAmounts,
                                         Order pOrder,
                                         Locale pLocale,
                                         RepositoryItem pProfile,
                                         Map pExtraParameters)
    throws PricingException
  {
    if (pTaxableItem != null) {
      CommerceItem item = pRelationship.getCommerceItem();

      long relationshipQuantity = 0;
      if (pRelationship.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITY) {
        relationshipQuantity = pRelationship.getQuantity();
      } else if (pRelationship.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITYREMAINING) {
        relationshipQuantity = getOrderManager().getShippingGroupManager().getRemainingQuantityForShippingGroup(item);
      } else {
        throw new PricingException(MessageFormat.format(Constants.BAD_SHIPPING_GROUP_TYPE,
                                                        new Object [] {
                                                          Integer.valueOf(pRelationship.getRelationshipType())
                                                            }));
      }

      // if there's no quantity in this rel, skip it
      if (relationshipQuantity == 0) {
        if (isLoggingDebug()) {
          logDebug("no quantity of the item: " + item.getId() + " in this relationship to tax, continuing.");
        }
        return 0;
      }

      if (isLoggingDebug()) {
        logDebug("rounding the price of " + relationshipQuantity + " units of the item: " + item.getId() + " : " + pTaxableItem.getAmount());
      }

      // round the taxable item's price.
      // If these are the last units being priced, round and add the leftovers.
      double roundedAmount = getPricingTools().roundDown(pTaxableItem.getAmount());

      if (isLoggingDebug()) {
        logDebug("rounded the price of " + relationshipQuantity + " units of the item to: " + roundedAmount);
      }

      pTaxableItem.setAmount(roundedAmount);

      long quantityAlreadyTaxed = 0;
      Long quantityTaxed = (Long) pTaxedItems.get(item);
      if (quantityTaxed != null) {
        quantityAlreadyTaxed = quantityTaxed.longValue();
      }

      if (isLoggingDebug()) {
        logDebug("quantity of the item: " + item.getId() + " already taxed: " + quantityAlreadyTaxed);
      }

      if (isLoggingDebug()) {
        logDebug("quantity in this relationship: " + relationshipQuantity);
      }

      if (isLoggingDebug()) {
        logDebug("total quantity of the item: " + item.getQuantity());
      }

      // if we're about to tax the last of an item, add the leftovers to its amount
      if (quantityAlreadyTaxed + relationshipQuantity == item.getQuantity()) {

        if (isLoggingDebug()) {
          logDebug("calculating leftovers from rounding unit prices down");
        }

        // the leftovers are the remainders from rounding every unit of the CommerceItem
        double averageUnitPrice =
          item.getPriceInfo().getAmount() / item.getQuantity();

        // if we divided by zero... use zero instead
        if(Double.isNaN(averageUnitPrice) || Double.isInfinite(averageUnitPrice)) {
          if(isLoggingDebug()) {
            logDebug(MessageFormat.format(Constants.QUOTIENT_IS_NAN, "checkTaxableItemByAverage",
                Double.toString(item.getPriceInfo().getAmount()), 
                Double.toString(item.getQuantity()) ));
          }
          averageUnitPrice = 0.0;
        }

        if (isLoggingDebug()) {
          logDebug("average unit price: " + averageUnitPrice);
        }

        double roundedAveragePrice = getPricingTools().roundDown(averageUnitPrice);

        if (isLoggingDebug()) {
          logDebug("rounded average price: " + roundedAveragePrice);
        }

        if (isLoggingDebug()) {
          logDebug("total item amount: " + item.getPriceInfo().getAmount());
        }

        if (isLoggingDebug()) {
          logDebug("rounded unit price times quantity of: " + item.getQuantity() + " : " + ( roundedAveragePrice * item.getQuantity()));
        }


        double leftovers = item.getPriceInfo().getAmount() - (roundedAveragePrice * item.getQuantity());

        if (isLoggingDebug()) {
          logDebug("adding leftovers: " + leftovers);
        }

        double withLeftovers = pTaxableItem.getAmount() + leftovers;

        if (isLoggingDebug()) {
          logDebug("rounding taxable item amount with leftovers");
        }

        double rounded = getPricingTools().roundDown(withLeftovers);

        if (isLoggingDebug()) {
          logDebug("rounded with leftovers to: " + rounded);
        }

        pTaxableItem.setAmount(rounded);

      } // end if we need to add leftovers

      return quantityAlreadyTaxed + relationshipQuantity;
    } // end if taxableItem is not null
    else {
      return 0;
    }
  }

  /**
   * This method verifies the amount for each relationship.  It assumes the amounts
   * were set by summing the detailedItemPriceInfos.
   *
   * @param pTaxableItem The taxable item as returned by createTaxableItemForRelationship
   * @param pRelationship The relationship that the taxable item was created from
   * @param pTaxedItems This is a map from an item to the amount of the item that will be taxed
   *                    This map is used to determine any leftovers remaining due to rounding
   * @param pTaxedItemAmounts This is a map from an item to the amount of the item that will be taxable
   *                    This map is used to determine any leftovers remaining due to rounding
   * @param pTaxableItems This is the list of taxable items.  Once all verification is finished, 
   *                      pTaxableItem is added to this list
   * @param pTotalTaxAmount The total tax amount so far.  This is used for getting the leftovers
   * @return the quantity of the relationship that will be taxed
   **/
  private long checkTaxableItem(TaxableItemImpl pTaxableItem, 
                                ShippingGroupCommerceItemRelationship pRelationship, 
                                Map pTaxedItems,
                                Map pTaxedItemAmounts,
                                double pTotalTaxAmount,
                                Order pOrder,
                                Locale pLocale,
                                RepositoryItem pProfile,
                                Map pExtraParameters)
    throws PricingException
  {
    if (pTaxableItem != null) {
    
      // if this relationship doesn't have a range, go ahead and use the old method
      // since we could't get a reliable subtotal for this relationship without it
      if(pRelationship.getRange() == null)
        return checkTaxableItemByAverage(pTaxableItem, pRelationship, pTaxedItems, pTaxedItemAmounts,
                                         pOrder, pLocale, pProfile, pExtraParameters);
      
      CommerceItem item = pRelationship.getCommerceItem();

      long relationshipQuantity = 0;
      if (pRelationship.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITY) {
        relationshipQuantity = pRelationship.getQuantity();
      } else if (pRelationship.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITYREMAINING) {
        relationshipQuantity = getOrderManager().getShippingGroupManager().getRemainingQuantityForShippingGroup(item);
      } else {
        throw new PricingException(MessageFormat.format(Constants.BAD_SHIPPING_GROUP_TYPE,
                                                        new Object [] {
                                                          Integer.valueOf(pRelationship.getRelationshipType())
                                                            }));
      }

      // if there's no quantity in this rel, skip it
      if (relationshipQuantity == 0) {
        if (isLoggingDebug()) {
          logDebug("no quantity of the item: " + item.getId() + " in this relationship to tax, continuing.");
        }
        return 0;
      }

      // when checking by average we reset the price to a rounded amount.  Now
      // we just sum the details so this isn't necessary anymore.

      long quantityAlreadyTaxed = 0;
      Long quantityTaxed = (Long) pTaxedItems.get(item);
      if (quantityTaxed != null) {
        quantityAlreadyTaxed = quantityTaxed.longValue();
      }

      if (isLoggingDebug()) {
        logDebug("quantity of the item: " + item.getId() + " already taxed: " + quantityAlreadyTaxed);
        logDebug("quantity in this relationship: " + relationshipQuantity);
        logDebug("total quantity of the item: " + item.getQuantity());
      }

      // the average method had to worry about leftovers.  Now we just sum the details.
      // if we're about to tax the last of an item, add the leftovers to its amount
      if (quantityAlreadyTaxed + relationshipQuantity == item.getQuantity()) {
        ItemPriceInfo info = item.getPriceInfo();
        double totalTax = getPricingTools().calculateTaxableAmount(item, pOrder, pLocale,
                                                                   pProfile, pExtraParameters);
        Double amountTaxableSoFar = (Double) pTaxedItemAmounts.get(item);
        double taxableSoFar = 0.0;
        if(amountTaxableSoFar != null)
          taxableSoFar = amountTaxableSoFar.doubleValue();
        double roundedTotal = getPricingTools().round(totalTax);

        double leftovers = roundedTotal - pTotalTaxAmount - taxableSoFar;

        if(isLoggingDebug())
          logDebug("Calculate " + leftovers + " for the taxable amount.");

        double withLeftovers = pTaxableItem.getAmount() + leftovers;

        pTaxableItem.setAmount(getPricingTools().round(withLeftovers));

        if (isLoggingDebug()) {
          logDebug("calculating leftovers from rounding unit prices down");
        }
      }
      return quantityAlreadyTaxed + relationshipQuantity;
    } // end if taxableItem is not null
    else {
      return 0;
    }
  }

  //-----------------------------
  // Utility methods
  //-----------------------------

  /**
   * Assign each detail's <code>tax</code> for the given order.  This
   * method will assign a tax value for the detail proportional to the
   * details cost compared to the order cost.  For example, if the
   * order is $100, the detail is $20, and the tax is $10: the
   * detail's tax will be $2. This method is used if
   * <code>calculateTaxByShipping</code> is false.
   *
   * @param pOrder The order that was just taxed.
   * @param pOrderTax The orders tax.
   * @param pStatus This is the tax status that was returned from the TaxProcessor.  This is currently
   *                ignored.  If the status were to include the item level tax breakdown, this method
   *                could be extended to use that information.
   **/
  protected void assignItemTaxAmounts(Order pOrder, TaxPriceInfo pOrderTax, TaxStatus pStatus)
    throws PricingException
  {
    if(isLoggingDebug())
      logDebug("Assign tax using order tax");

    if(pOrder == null)
      return;
    if(pOrderTax == null)
      return;

    double orderPrice = pOrder.getPriceInfo().getAmount();
    List items = pOrder.getCommerceItems();
    
    // use these to remember leftovers from rounding
    double detailTotal = 0.0;
    double total = pOrderTax.getAmount();

    for(int c=0;c<items.size();c++) {
      CommerceItem item = (CommerceItem) items.get(c);
      ItemPriceInfo itemPrice = item.getPriceInfo();
      List details = getPricingTools().getDetailedItemPriceTools().getDetailsToReceiveDiscountShare(itemPrice,null);
      for(int d=0;d<details.size();d++) {
        DetailedItemPriceInfo detail = (DetailedItemPriceInfo)details.get(d);
        double ratio = detail.getAmount() / orderPrice;

        // if we divided by zero... use zero instead
        if(Double.isNaN(ratio) || Double.isInfinite(ratio)) {
          if(isLoggingDebug()) {
            logDebug(MessageFormat.format(Constants.QUOTIENT_IS_NAN, "assignItemTaxAmounts", Double.toString(detail.getAmount()), Double.toString(orderPrice)));
          }
          ratio = 0.0;
        }

        double detailTax = total * ratio;
        double rounded = getPricingTools().roundDown(detailTax);
        detailTotal += rounded;
        
        if(isLoggingDebug())
          logDebug("Setting details tax to " + detailTax + " (" + rounded + ", rounded)");
      
        if((d == (details.size() - 1)) &&
           (c == (items.size() - 1))) {
          double leftovers = total - detailTotal;

          if(isLoggingDebug())
            logDebug("Added " + leftovers + " leftovers to last detail.");

          double roundLeft = getPricingTools().roundDown(rounded + leftovers);
          detail.setTax(roundLeft);
        }
        else {
          detail.setTax(rounded);
        }
      }
    }
  }

  /**
   * Assign each detail's <code>tax</code> for the given order.  This
   * method will assign a tax value for the detail proportional to the
   * details cost compared to the shipping group subtotal.  For
   * example, if the shipping subtotal is $100, the detail is $20, and
   * the tax is $10: the detail's tax will be $2. This method is used
   * if <code>calculateTaxByShipping</code> is true.
   *
   * @param pShippingGroup The shipping group whose details will be update
   * @param pTaxPriceInfo The TaxPriceInfo for the shipping group
   * @param pOrderPrice The TaxPriceInfo for the entire order
   * @param pShippingSubtotal The price info containing the subtotal information for the shipping group
   * @param pStatus This is the tax status that was returned from the TaxProcessor.  This is currently
   *                ignored.  If the status were to include the item level tax breakdown, this method
   *                could be extended to use that information.
   **/
  protected void assignItemTaxAmounts(ShippingGroup pShippingGroup,
                                      TaxPriceInfo pTaxPriceInfo,
                                      OrderPriceInfo pOrderPrice,
                                      OrderPriceInfo pShippingSubtotal,
                                      TaxStatus pStatus)
    throws PricingException
  {
    if(isLoggingDebug())
      logDebug("Assign tax using tax by shipping");

    if(pShippingGroup == null) {
      if(isLoggingDebug())
        logDebug("Couldn't assign item tax amounts.  The shipping group was null.");
      return;
    }
    if(pTaxPriceInfo == null) {
      if(isLoggingDebug())
        logDebug("Couldn't assign item tax amounts.  The tax price info was null.");
      return;
    }
    List relationships = getRelationshipsToAssignTaxTo(pShippingGroup);

    // If the relationships are null or they are empty then there is nothing to distribute and
    // as such we should just return
    
    if (relationships == null || relationships.size() == 0)
      return;

    double shipTotal=pShippingSubtotal.getRawSubtotal();

    // use these to remember leftovers from rounding
    double detailsTotal = 0.0;
    double total = pTaxPriceInfo.getAmount();

    getPricingTools().getDetailedItemPriceTools().distributeAmountAcrossDetails(relationships, 
                                                                                shipTotal, 
                                                                                total, 
                                                                                DETAILED_ITEM_PRICE_TAX_PROPERTY);

  }

  /**
   * Returns the list of ShippingGroupCommerceItemRelationships that should be assigned tax.
   * By default, this returns all ShippingGroupCommerceItemRelationships within the given
   * shipping group.
   *
   * @param pShippingGroup the shipping group being taxed
   * @return List of ShippingGroupCommerceItemRelationships that should be assigned tax.
   */
  public List getRelationshipsToAssignTaxTo(ShippingGroup pShippingGroup) {
    return pShippingGroup.getCommerceItemRelationships();
  }
 
  /**
   * Sets all amount properties of the input TaxPriceInfo to zero
   * @param pTaxPriceInfo the TaxPriceInfo to clear
   */
  void clearTaxPriceInfo (TaxPriceInfo pPriceQuote) {
    pPriceQuote.setAmount(0.0);
    pPriceQuote.setCityTax(0.0);
    pPriceQuote.setCountyTax(0.0);
    pPriceQuote.setStateTax(0.0);
    pPriceQuote.setDistrictTax(0.0);
    pPriceQuote.setCountryTax(0.0);
  }

  //-----------------------------
  // GenericService overrides
  //-----------------------------

  public void doStartService() throws ServiceException {
    if (getPricingTools() == null) {
      throw new ServiceException(MessageFormat.format(Constants.PROPERTY_NOT_SET,
                                                      new Object [] {
                                                        "pricingTools"
                                                      }));
    }

    super.doStartService();

  } // end doStartService

  // -----------------------------------------------------------------
  // This huge commented section is not necessary anymore.  I didn't
  // want to delete it, just in case for some strange reason.  It will
  // distribute taxes across DetailedItemPriceInfos proportional to
  // the quantity.  It results in really weird tax distributions, if
  // you have drastic discounting going on.
  // -----------------------------------------------------------------

//    /**
//     * Assign each detail's <code>tax</code> for the given order.  This method will calculate
//     * the average item price, then assign each DetailItemPriceInfo a tax value of this
//     * average price, times the detailedItemPriceInfo's quantity.
//     *
//     * @param pOrder The order that was just taxed.
//     * @param pOrderTax The orders tax.
//     **/
//    protected void assignItemTaxAmountsByAverage(Order pOrder, TaxPriceInfo pOrderTax)
//    {
//      if(isLoggingDebug())
//        logDebug("Assign tax by average, using order tax");

//      if(pOrder == null)
//        return;
//      if(pOrderTax == null)
//        return;

//      PricingTools tools = getPricingTools();

//      // we have the orders tax
//      double orderTax = pOrderTax.getAmount();

//      // keep a running total to get leftovers
//      double runningTaxTotal = 0.0;

//      // we have the order subtotal
//      double orderSubtotal = pOrder.getPriceInfo().getAmount();
//      double roundedOrderSub = tools.roundDown(orderSubtotal);

//      List items = pOrder.getCommerceItems();
//      for(int i=0;i<items.size();i++) {
//        CommerceItem item = (CommerceItem) items.get(i);
//        // we need average item cost
//        ItemPriceInfo itemPrice = item.getPriceInfo();
//        double itemCost = itemPrice.getAmount();
//        double avgItemCost = itemCost / item.getQuantity();
//        double roundedAverage = tools.roundDown(avgItemCost);

//        // we can get each detail
//        List details = itemPrice.getCurrentPriceDetails();
//        for(int d=0;d<details.size();d++) {
//          DetailedItemPriceInfo detail = (DetailedItemPriceInfo) details.get(d);
//          long detailQuan = detail.getQuantity();
//          // total detail cost using average
//          double detailCost = roundedAverage * detailQuan;
//          double roundedDetailCost = tools.roundDown(detailCost);
        
//          // ratio of shipping group
//          double ratio = roundedDetailCost / roundedOrderSub;

//          // ration of tax
//          double detailTax = orderTax * ratio;
//          double roundedDetailTax = tools.roundDown(detailTax);
        
//          runningTaxTotal += roundedDetailTax;

//          if(isLoggingDebug())
//            logDebug("Setting details tax to " + detailTax + " (" + roundedDetailTax + ", rounded)");
      
//          if(d == (details.size() - 1)) {
//            double roundedRun = tools.roundDown(runningTaxTotal);
//            double leftovers = orderTax - roundedRun;

//            if(isLoggingDebug())
//              logDebug("Added " + leftovers + " leftovers to last detail.");

//            double tax = roundedDetailTax + leftovers;
//            detail.setTax(tools.roundDown(tax));
//          }
//          else {
//            detail.setTax(roundedDetailTax);
//          }
//        }
//      }
//    }

//    /**
//     * Assign each detail's <code>tax</code> for the given order.  This method will calculate
//     * the average item price, then assign each DetailedItemPriceInfo a tax value equal to this
//     * average price times the detail's quantity. 
//     *
//     * @param pShippingGroup The shipping group whose details will be update
//     * @param pTaxPriceInfo The TaxPriceInfo for the shipping group
//     * @param pOrderPrice The TaxPriceInfo for the entire order
//     * @param pShippingSubtotal The price info containing the subtotal information for the shipping group
//     **/
//    protected void assignItemTaxAmountsByAverage(ShippingGroup pShippingGroup,
//                                                 TaxPriceInfo pTaxPriceInfo,
//                                                 OrderPriceInfo pOrderPrice,
//                                                 OrderPriceInfo pSubtotal)
//    {
//      if(isLoggingDebug())
//        logDebug("Assign tax by average, using tax by shipping");

//      if(pShippingGroup == null)
//        return;
//      if(pTaxPriceInfo == null)
//        return;

//      PricingTools tools = getPricingTools();

//      // we have the shipping groups tax
//      double shippingTax = pTaxPriceInfo.getAmount();

//      // keep a running total to get leftovers
//      double runningTaxTotal = 0.0;

//      // we have the shipping group subtotal
//      double shippingSubtotal = pSubtotal.getAmount();
//      double roundedShipSub = tools.roundDown(shippingSubtotal);

//      List rels = pShippingGroup.getCommerceItemRelationships();
//      Set items = new HashSet();
//      for(int r=0;r<rels.size();r++) {
//        ShippingGroupCommerceItemRelationship sgcir = (ShippingGroupCommerceItemRelationship) rels.get(r);
//        CommerceItem item = sgcir.getCommerceItem();

//        // we need average item cost
//        ItemPriceInfo itemPrice = item.getPriceInfo();
//        double itemCost = itemPrice.getAmount();
//        double avgItemCost = itemCost / item.getQuantity();
//        double roundedAverage = tools.roundDown(avgItemCost);

//        // we can get each detail
//        List details = itemPrice.getCurrentPriceDetailsForRange(sgcir.getRange());
//        if(details != null) {
//          for(int d=0;d<details.size();d++) {
//            DetailedItemPriceInfo detail = (DetailedItemPriceInfo) details.get(d);
//            long detailQuan = detail.getQuantity();
//            // total detail cost using average
//            double detailCost = roundedAverage * detailQuan;
//            double roundedDetailCost = tools.roundDown(detailCost);
          
//            // ratio of shipping group
//            double ratio = roundedDetailCost / roundedShipSub;
          
//            // ration of tax
//            double detailTax = shippingTax * ratio;
//            double roundedDetailTax = tools.roundDown(detailTax);
          
//            runningTaxTotal += roundedDetailTax;
          
//            if(isLoggingDebug())
//              logDebug("Setting details tax to " + detailTax + " (" + roundedDetailTax + ", rounded)");

//            if(d == (details.size() - 1)) {
//              double leftovers = shippingTax - runningTaxTotal;

//              if(isLoggingDebug())
//                logDebug("Added " + leftovers + " leftovers to last detail.");

//              // use get tax, just in case this detail is in more than one shipping group
//              // this isn't possible with ranges, but maybe we don't have ranges for some strange
//              // reason
//              double newTax = roundedDetailTax + leftovers;
//              detail.setTax(tools.roundDown(newTax));
//            }
//            else {
//              detail.setTax(roundedDetailTax);
//            }
//          }
//        }
//        else {
//          // do it the complicated way (no ranges)
//          // this code should never be executed, but if for some reason
//          // someone is recalculating tax on an order that was originally priced
//          // before ranges were added (before Harpoon), this could conceivably happen
//          details = itemPrice.getCurrentPriceDetails();
//          long quantityTaxSoFar = 0;
//          long quantityToTax;
//          if (sgcir.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITY) {
//            quantityToTax = sgcir.getQuantity();
//          } else { // SHIPPINGQUANTITYREMAINING
//            quantityToTax = getOrderManager().getShippingGroupManager().getRemainingQuantityForShippingGroup(item);
//          }

//          for(int d=0;d<details.size();d++) {
//            DetailedItemPriceInfo detail = (DetailedItemPriceInfo) details.get(d);
//            long detailQuan = detail.getQuantity();         
//            if(detailQuan + quantityTaxSoFar > quantityToTax) {
//              detailQuan = quantityToTax - quantityTaxSoFar;
//            }
//            // total detail cost using average
//            double detailCost = roundedAverage * detailQuan;
//            double roundedDetailCost = tools.roundDown(detailCost);
          
//            // ratio of shipping group
//            double ratio = roundedDetailCost / roundedShipSub;
          
//            // ration of tax
//            double detailTax = shippingTax * ratio;
//            double roundedDetailTax = tools.roundDown(detailTax);
          
//            runningTaxTotal += roundedDetailTax;

//            if(isLoggingDebug())
//              logDebug("Setting details tax to " + detailTax + " (" + roundedDetailTax + ", rounded)");

//            if(d == (details.size() - 1)) {
//              double leftovers = shippingTax - runningTaxTotal;

//              if(isLoggingDebug())
//                logDebug("Added " + leftovers + " leftovers to last detail.");

//              // use get tax, just in case this detail is in more than one shipping group
//              // this isn't possible with ranges, but maybe we don't have ranges for some strange
//              // reason
//              double newTax = roundedDetailTax + leftovers + detail.getTax();            
//              detail.setTax(tools.roundDown(newTax));
//            }
//            else {
//              detail.setTax(roundedDetailTax + detail.getTax());
//            }
//          }
//        }
//      }
//    }

} // end of class

