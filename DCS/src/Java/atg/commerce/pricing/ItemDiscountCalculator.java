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

import atg.commerce.pricing.definition.DiscountStructure;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryItem;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.core.util.Range;

import java.util.*;
import java.text.*;

import atg.core.util.RangeComparator;

/**
 *  Calculates the new price for an item or items based on a given pricing model.
 *  The pricing model must be a RepositoryItem of type item-discount (that is,
 *  it must be a RepositoryItem with all the same properties as the DCS item-discount
 *  item descriptor). 
 *  <p>
 *  The calculator has two methods of operation: 
 *  1. If the extra parameters map contains pre-qualified items and a discount structure then 
 *  the calculator will just apply the discount to those items. QualifiedItem objects should be stored
 *  in a Collection with the map key Constants.QUALIFIED_ITEMS. The DiscountStructure object should
 *  be stored with the map key Constants.DISCOUNT_STRUCTURE. 
 *  2. The calculator will inspect the pricing model repository item that it is given and, based
 *  on the <code>discountType</code> and <code>adjuster</code> properties,
 *  applies the discount to the price in the ItemPriceInfo corresponding to each
 *  CommerceItem that's passed in.  It does this by consulting the Qualifier service
 *  for a list of items which should receive the input discount.  It calls
 *  <code>Qualifier.findQualifyingItems</code> to do this.
 *  <p>
 *  The ItemDiscountCalculator inherits properties from DiscountCalculatorService.
 *
 * @beaninfo
 *   description: An ItemPricingCalculator that knows how to calculate item promotion discounts
 *   attribute: functionalComponentCategory Pricing Calculators
 *   attribute: featureComponentCategory
 *
 * @see Qualifier
 * @see DiscountCalculatorService
 *
 * @author Graham Mather
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/ItemDiscountCalculator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
@SuppressWarnings("unchecked")
public class ItemDiscountCalculator extends DiscountCalculatorService implements ItemPricingCalculator
{
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/ItemDiscountCalculator.java#2 $$Change: 651448 $";

  private static final String PERFORM_MONITOR_NAME="ItemDiscountCalculator";

  /**
   * The name used for specifying a qualifier service through the extra parameters map passed into
   * the pricing operation. 
   */
  public static final String EXTRA_PARAM_QUALIFIERSERVICE = "QualiferService";
  //-------------------------------------
  // properties
  //-------------------------------------


  //-------------------------------------
  // property: PricingTools
  /** pricing tools to help with price generation */
  PricingTools mPricingTools;

  /**
   * pricing tools to help with price generation
   * @beaninfo description: pricing tools to help with price generation
   * @param pPricingTools new value to set
   */
  public void setPricingTools(PricingTools pPricingTools)
  {mPricingTools = pPricingTools;}

  /**
   * pricing tools to help with price generation
   * @beaninfo description: pricing tools to help with price generation
   * @return property PricingTools
   */
  public PricingTools getPricingTools()
  {return mPricingTools;}



  //---------------------------------------------------------------------------
  // property:RangeComparator
  //---------------------------------------------------------------------------

  private RangeComparator mRangeComparator = new RangeComparator();
  public void setRangeComparator(RangeComparator pRangeComparator) {
    mRangeComparator = pRangeComparator;
  }

  /**
   * The object that is used to compare ranges
   * @beaninfo description: The object that is used to compare ranges
   **/
  public RangeComparator getRangeComparator() {
    return mRangeComparator;
  }

  //-------------------------------------
  // methods
  //-------------------------------------

  //-------------------------------------
  /**
   * give a single item the configured discount, if it is eligible.
   * eligibility is determined by consulting a configured qualifierService
   *
   * @param pPriceQuote ItemPriceInfo representing the current price quote for the item
   * @param pItem The item to price
   * @param pPricingModel A RepositoryItem representing a PricingModel
   * @param pLocale the locale for this pricing
   * @param pProfile The user's profile
   * @param pExtraParameters A Map of extra parameters to be used in the pricing, may be null
   *
   * @exception PricingException if there was a problem in determining an item's price
   */
  public void priceItem(ItemPriceInfo pPriceQuote, CommerceItem pItem, RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
  throws PricingException
  {

    String perfName = "priceItem";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    boolean perfCancelled = false;

    try {

      List priceQuotes = new ArrayList(1);
      List items = new ArrayList(1);

      priceQuotes.add(pPriceQuote);
      items.add(pItem);

      priceItems(priceQuotes, items, pPricingModel, pLocale, pProfile, null, pExtraParameters);

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
  }

  //-------------------------------------
  /**
   * give each item the configured discount, if it qualifies
   *
   * @param pPriceQuotes List of ItemPriceInfo objects representing the current price quotes for each item
   * @param pItems The items to price (individually)
   * @param pPricingModel A RepositoryItem representing a PricingModel
   * @param pLocale the locale for this pricing
   * @param pProfile The user's profile
   * @param pExtraParameters A Map of extra parameters to be used in the pricing, may be null
   *
   * @exception PricingException if there was a problem in determining an item's price
   */

  public void priceEachItem(List pPriceQuotes, List pItems, RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
  throws PricingException
  {
    String perfName = "priceEachItem";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    boolean perfCancelled = false;

    try {


      // sanity checking
      // make sure both lists have the same number of elements
      if (pPriceQuotes.size() != pItems.size()) {
        try {
          if(!perfCancelled) {
            PerformanceMonitor.cancelOperation(PERFORM_MONITOR_NAME, perfName);
            perfCancelled = true;
          }
        }
        catch(PerfStackMismatchException psm) {
          if(isLoggingWarning())
            logWarning(psm);
        }        

        throw new PricingException
        (MessageFormat.format
            (Constants.BAD_LIST_PARAMS,
                new Object[]  {
                Integer.valueOf(pPriceQuotes.size()),
                Integer.valueOf(pItems.size())
            }));
      }

      // go through each element of the pricequotes and items
      // and price each pair individually
      // TBD: don't construct lists every time! just call set on subsequent iterations.

      List items = new ArrayList(1);
      List priceQuotes = new ArrayList(1);

      for (int i = 0; i < pPriceQuotes.size(); i++) {

        priceQuotes.add(pPriceQuotes.get(i));
        items.add(pItems.get(i));

        priceItems(priceQuotes, items, pPricingModel, pLocale, pProfile, null, pExtraParameters);

        items.clear();
        priceQuotes.clear();

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

  } // end priceEachItem

  //-------------------------------------
  /**
   * Price a List of items together in a context
   *
   * when it comes right down to it, we're only discounting single items :
   * the only reason we need to price items in a context is to allow complex
   * qualification statements.
   *
   *
   * @param pPriceQuotes List of ItemPriceInfo objects representing the current price quotes for the items
   * @param pItems The items to price
   * @param pPricingModel A RepositoryItem representing a PricingModel
   * @param pProfile The user's profile
   * @param pLocale the locale for this pricing
   * @param pOrder The Order object of which the List of items are a part, may be null
   * @param pExtraParameters A Map of extra parameters to be used in the pricing, may be null
   *
   * @exception PricingException if there was a problem in determining an item's price
   */
  public void priceItems(List pPriceQuotes, List pItems, RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, Order pOrder, Map pExtraParameters)
  throws PricingException
  {

    String perfName = "priceItems";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    boolean perfCancelled = false;

    try {
      if (isLoggingDebug()) {
        logDebug("Entered PriceItems");
      }

      Collection qualifyingItems = findQualifyingItems(pPriceQuotes,
          pItems,
          pPricingModel,
          pProfile,
          pLocale,
          pOrder,
          pExtraParameters );

      // if no items qualified, don't discount anything
      if (qualifyingItems == null ||
          qualifyingItems.size() == 0) {
        return;
      }

      if (isLoggingDebug()) {
        logDebug("ItemDiscountCalculator got the following qualifying items:");
        Iterator it = qualifyingItems.iterator();
        while (it.hasNext()) {
          logDebug(((QualifiedItem) it.next()).toString());
        }
      }

      priceQualifyingItems(qualifyingItems,
          pPriceQuotes,
          pItems,
          pPricingModel,
          pProfile,
          pLocale,
          pOrder,
          pExtraParameters );



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

    // we're done

  } // end priceItems

  /**
   * Determines if the qualifier service is valid.
   * @return true if the qualifier service is non null.
   */ 
  protected boolean validateQualifierService(){
    boolean isValid = true;
    if (getQualifierService(null, null) == null){
      isValid = false;
    }

    return (isValid);
  }

  //-------------------------------------
  // GenericService overrides
  //-------------------------------------

  /**
   * Checks to see if property <code>qualifierService</code> is set
   * @exception ServiceException if there was a problem initializing this service
   */
  public void doStartService() throws ServiceException {
    String perfName = "doStartService";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    boolean perfCancelled = false;

    try {

      super.doStartService();

      if (validateQualifierService() == false) {
        try {
          if(!perfCancelled) {
            PerformanceMonitor.cancelOperation(PERFORM_MONITOR_NAME, perfName);
            perfCancelled = true;
          }
        }
        catch(PerfStackMismatchException psm) {
          if(isLoggingWarning())
            logWarning(psm);
        }

        throw new ServiceException(MessageFormat.format(Constants.PROPERTY_NOT_SET,
            new Object [] {
            "qualifierService"
        }));
      }

      if (getPricingTools() == null) {
        try {
          if(!perfCancelled) {
            PerformanceMonitor.cancelOperation(PERFORM_MONITOR_NAME, perfName);
            perfCancelled = true;
          }
        }
        catch(PerfStackMismatchException psm) {
          if(isLoggingWarning())
            logWarning(psm);
        }

        throw new ServiceException(MessageFormat.format(Constants.PROPERTY_NOT_SET,
            new Object [] {
            "pricingTools"
        }));
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

  } // end doStartService

  //-------------------------------------
  // Protected methods
  //-------------------------------------

  /**
   * Produces a collection of QualifiedItem objects, each of which contains a CommerceItem
   * and a map of detailed item price info to the ranges to be discounted.
   * The collection of qualified items can be passed in via the extra parameters map using the
   * key Constants.QUALIFIED_ITEMS otherwise they are determined using a qualifier service.
   * The qualifier service to use is determined using the PromotionTools.getQualifierService method.
   * <p>
   * Note: in the lists pPriceQuotes and pItems, there is a 1:1
   * correspondence between ItemPriceInfos and items.  That is to say, element 1 of
   * pPriceQuotes is the ItemPriceInfo for the item at element 1 of pItems, and so on.
   * <p>
   * Side Effects:
   * <p>
   * In each ItemPriceInfo for each qualifying item, if its corresponding item acted as a qualifier
   * in determining the qualifying items, the appropriate quantity is marked as having acted
   * as a qualifier.  This marking takes place only if the ItemPriceInfo is an instance
   * of ItemPriceInfo.
   *
   *
   * @param pPriceQuotes the input list of item prices
   * @param pItems the total set of items from which qualifying items will be selected
   * @param pPricingModel the discount which defines which items qualify
   * @param pProfile the user for whom this calculation is being performed
   * @param pLocale the locale in which this calculation is being performed
   * @param pOrder the order in which the qualifying items reside
   * @param pExtraParameters any extra information that this calculator might need to determine
   *        which items qualify for a discount.
   *
   * @return a Collection of QualifiedItem objects
   * @exception PricingException if anything went wrong while determining the QualifiedItems
   * @see PriceItems
   */
  protected Collection findQualifyingItems (List pPriceQuotes,
      List pItems,
      RepositoryItem pPricingModel,
      RepositoryItem pProfile,
      Locale pLocale,
      Order pOrder,
      Map pExtraParameters ) throws PricingException {
    String perfName = "findQualifyingItems";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    boolean perfCancelled = false;

    try {
      Collection qualifiedItems = null;

      Qualifier qualService=null;
      if (pExtraParameters != null){
        qualifiedItems = (Collection) pExtraParameters.get(Constants.QUALIFIED_ITEMS);
      }

      if (qualifiedItems == null){
        qualService = getQualifierService(pPricingModel, pExtraParameters);
        // get a list of the qualified items
        List wrappedItems = qualService.wrapCommerceItems(pItems, pPriceQuotes);
        PricingContext pricingContext = getPricingTools().getPricingContextFactory().createPricingContext(
            wrappedItems, pPricingModel, pProfile, pLocale, pOrder, null);

        qualifiedItems = qualService.findQualifyingItems( pricingContext, pExtraParameters );
      }

      return (qualifiedItems);
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

  } // end findQualifyingItems

  /**
   * Loops through each QualifiedItem object in pQualifyingItems, determining and setting
   * the price for each QualifiedItem's CommerceItem'sItemPriceInfo.  The default
   * implementation does this by calling priceQualifyingItem.
   * <p>
   * Note: the Collection of QualifiedItem's (and the DiscountStructure if available) is stored 
   * in the extra parameters map so that they can be accessed within the getAdjuster method since 
   * this is needed by some extensions of this calculator, e.g. BulkItemDiscountCalculator.
   * The qualified items are stored with the key Constants.QUALIFIED_ITEMS and the discount
   * structure is stored with the key Constants.DISCOUNT_STRUCTURE.
   * <p>
   * Side Effects:
   * Each ItemPriceInfo in pPriceQuotes that is at the same index as that at which the
   * CommerceItem in pItems that is the QualifiedItem's CommerceItem resides
   * has its price set to the proper price.
   *
   * @param pQualifyingItems a collection of QualifiedItem objects that contains information about
   *        which and how many items should be discounted.
   * @param pPriceQuotes the price objects corresponding to pItems
   * @param pItems the items whose prices are contained in pPriceQuotes
   * @param pPricingModel the discount which states how the qualifying items should be priced
   * @param pProfile the person for whom the items are to be discounted
   * @param pLocale the locale in which the items are to be discounted
   * @param pOrder the order in which the discounted items have been placed
   * @param pExtraParameters any extra information that this method might need to set the
   *        prices of a number of qualifying items
   *
   * @exception PricingException if anything went wrong while pricing the QualifiedItems
   * @see PriceItems
   */

  protected void priceQualifyingItems(Collection pQualifyingItems,
      List pPriceQuotes,
      List pItems,
      RepositoryItem pPricingModel,
      RepositoryItem pProfile,
      Locale pLocale,
      Order pOrder,
      Map pExtraParameters ) throws PricingException {

    String perfName = "priceQualifyingItems";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    boolean perfCancelled = false;

    try {
      // First put qualifying items into map and determine the adjuster
      if (pExtraParameters == null){
        pExtraParameters = new HashMap();
      }
      pExtraParameters.put(Constants.QUALIFIED_ITEMS, pQualifyingItems);
      if ((pQualifyingItems != null) && (pQualifyingItems.size() > 0)){
        pExtraParameters.put(Constants.DISCOUNT_STRUCTURE,(
            (QualifiedItem) pQualifyingItems.iterator().next()).getDiscount());
      }
      
      getAdjuster(pPricingModel, pExtraParameters);

      // Iterate over the qualified items and process each in turn
      Iterator qualifyingItemIterator = pQualifyingItems.iterator();
      while (qualifyingItemIterator.hasNext()) {
        QualifiedItem qi = (QualifiedItem) qualifyingItemIterator.next();

        priceQualifyingItem(qi,
            pPriceQuotes,
            pItems,
            pPricingModel,
            pProfile,
            pLocale,
            pOrder,
            pExtraParameters);
      }

    }
    finally {
      if (pExtraParameters != null){
        pExtraParameters.remove(Constants.QUALIFIED_ITEMS);
        pExtraParameters.remove(Constants.DISCOUNT_STRUCTURE);
      }
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

  } // end priceQualifyingItems

  /**
   * Determines and sets the amount of the ItemPriceInfo in pPriceQuotes at the index
   * corresponding to the CommerceItem in pItems that is contained in pQualifiedItem.
   * <P>
   * Side Effects:
   * <P>
   * The default implementation of this method assumes that the ItemPriceInfo whose
   * Amount this method is maintaining has a detailedCurrentPrice property which
   * represents a breakdown of the price into smaller quantities. These
   * DetailedItemPriceInfo objects have their amounts maintained as well, through
   * calls to priceDetailedItemPriceInfo. The ItemPriceInfo has its Amount, Adjustments,
   * and QuantityDiscounted properties maintained.
   * <P>
   * @param pQualifiedItem the object stating which item, and how many of that item, qualified
   *        for the discount defined by pPricingModel.  It is expected that each QualifiedItem
   *        is a FilteredCommerceItem
   * @param pPriceQuotes the price objects corresponding to pItems
   * @param pItems the items whose prices are contained in pPriceQuotes
   * @param pPricingModel the discount which states how the qualifying items should be priced
   * @param pProfile the person for whom the items are to be discounted
   * @param pLocale the locale in which the items are to be discounted
   * @param pOrder the order in which the discounted items have been placed
   * @param pExtraParameters any extra information that this method might need to set the
   *        prices of a number of the qualifying item
   *
   * @exception PricingException if anything went wrong while pricing the QualifiedItem
   * @see PriceItems
   */

  protected void priceQualifyingItem(QualifiedItem pQualifiedItem,
      List pPriceQuotes,
      List pItems,
      RepositoryItem pPricingModel,
      RepositoryItem pProfile,
      Locale pLocale,
      Order pOrder,
      Map pExtraParameters ) throws PricingException {

    String perfName = "priceQualifyingItem";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    boolean perfCancelled = false;

    try {

      if (isLoggingDebug()){
        logDebug("Entered priceQualifyingItem");
      }
      
      // if there's nothing to price, don't do anything
      if (pQualifiedItem == null ||
          pQualifiedItem.getItem() == null) {
        if (isLoggingDebug()){
          logDebug("Nothing to price so leaving.");
        }
        return;
      }

      // If available then put the discount into the extra parameters map for later use
      DiscountStructure discount = pQualifiedItem.getDiscount();

      if (discount != null){
        if (pExtraParameters == null){
          pExtraParameters = new HashMap(1);
        }
        pExtraParameters.put(Constants.DISCOUNT_STRUCTURE, discount);
      }
    
      // before discounting, remember the previous price
      FilteredCommerceItem item = (FilteredCommerceItem) pQualifiedItem.getItem();
      CommerceItem wrappedCommerceItem = item.getWrappedItem();
      ItemPriceInfo ipi = (ItemPriceInfo) pPriceQuotes.get(pItems.indexOf(wrappedCommerceItem));
      double oldPrice = ipi.getAmount();

      // remember this so we correctly calculate the "quantityAdjusted" in the adjustment
      long currentQuantityDiscounted = ipi.getQuantityDiscounted();
      
      // the item's price info
      List details = ipi.getCurrentPriceDetails();

      // if there's no price to discount, error
      if (details == null ||
          details.size() < 1) {
        // FIXME
        throw new PricingException(Constants.ITEM_NOT_LIST_PRICED);
      }
      
      // For multiple discounts in a single promotion it is possible that a previous discount could
      // have split some detailed item price info.  However the QualifiedItem was created before this
      // split took place and so some qualifying ranges may still be pointing to the original unsplit
      // detailed item price info.  So we need to fix any such references to point to the correct dipi.
      if (pExtraParameters != null) {
        if (pExtraParameters.containsKey(Constants.UPDATE_QUALIFYING_DETAILS)) {
          updateQualifyingDetails(pQualifiedItem, ipi);
        }
      }     
      
      // Check if there is any adjustment to apply, e.g. for banded discounts
      // it may not fall into a valid band and therefore no discount will apply
      if (Double.isNaN(getAdjuster(pPricingModel, pExtraParameters))) {
        if (isLoggingDebug()) {
          logDebug("No adjustment to apply so leaving.");
        }
        
        return;
      }

      ListIterator detailsIterator = details.listIterator();
      long totalDiscounted = 0;
      long totalDiscountedByThisCalc = 0;
      double totalPrice = 0.0;

      // for each detailed price, apply the discount to as many of it as
      // pQualifiedItem specifies
      while ( detailsIterator.hasNext() ) {
        DetailedItemPriceInfo dpi = (DetailedItemPriceInfo) detailsIterator.next();

        if(isLoggingDebug())
          logDebug("processing detail " + dpi.getRange() + ": " + dpi);

        long dpiDiscount = determineDiscountNumber(dpi,pQualifiedItem,pPricingModel);
        totalDiscountedByThisCalc += dpiDiscount;
        
        // log
        if (isLoggingDebug()) {
          if (dpiDiscount <= 0) {
            if(pPricingModel != null) {
              logDebug("none of the detail qualified for the discount specified by promotion : " + pPricingModel.getRepositoryId());
            }
            else {
              logDebug("none of the detail qualified for the discount");
            }
          }   
          else {
            logDebug("number of this detail's (quantity: " + dpi.getQuantity() + ") that we're going to try to discount: " + dpiDiscount);
          }
        }
        
        List newdpis =
          updateDetailedPriceInfos(dpi,
              pQualifiedItem,
              pPriceQuotes,
              pItems,
              pPricingModel,
              pProfile,
              pLocale,
              pOrder,
              pExtraParameters );

        if(newdpis != null) {
          // this will be the total number of items receiving the discount
          for(int i=0; i < newdpis.size(); i++) {
            DetailedItemPriceInfo newdpi = (DetailedItemPriceInfo) newdpis.get(i);

            if(isLoggingDebug()) {
              if (dpi == newdpi) {
                logDebug("updated original detail " + newdpi.getRange() + ": " + newdpi);
              }
              else {
                logDebug("new detail " + newdpi.getRange() + ": " + newdpi);
              }
            }

            // if we get nothing back, none of this details was discounted
            // try again with the next details
            if (newdpi != null ){

              // if the object returned isn't the one passed in, that means
              // that we have a new dpi (undiscounted by this calc) to add to
              // the list of DetailedItemPriceInfos. Add it, and add its price to the totals.
              if (newdpi != dpi) {
                // add the new one to the details list
                detailsIterator.add(newdpi);

                // add the amount to the total price
                totalPrice += newdpi.getAmount();

                // add the quantity to the quantity discounted, if appropriate
                if (newdpi.isDiscounted()) {
                  totalDiscounted += newdpi.getQuantity();
                }

              } // end if we got a new one

            } // end if we got something back
          }
        }
        if (dpi.isDiscounted()) {
          totalDiscounted += dpi.getQuantity();
        }

        // always take into account the old details
        totalPrice += dpi.getAmount();

        if (isLoggingDebug()){
          logDebug("total price so far: " + totalPrice);
          logDebug("totalDiscounted: " + totalDiscounted);
          logDebug("totalDiscounted before this promotion: " + currentQuantityDiscounted);
        }

      } // end for each details

      // maintain each itempriceinfo's amount, appliedadjustments, etc
      // only if we've discounted something

      if (totalDiscountedByThisCalc > 0) {
        // register the adjustment made: new price minus old price
        double adjustAmount = totalPrice - oldPrice;
        PricingAdjustment adjustment = new PricingAdjustment(Constants.ITEM_DISCOUNT_PRICE_ADJUSTMENT_DESCRIPTION,
            pPricingModel,
            getPricingTools().round(adjustAmount),
            totalDiscountedByThisCalc);

        ipi.getAdjustments().add(adjustment);
        ipi.setDiscounted(true);
      }
      // else
      //  if(isLoggingDebug())
      //    logDebug("testing");

      // the quantity discounted
      ipi.setQuantityDiscounted(totalDiscounted);

      //set the amount
      if (totalPrice < 0.0) {
        if (isNegativeAmountException()) {
          throw new PricingException(MessageFormat.format(Constants.NEGATIVE_PRICE,
              new Object[] {pQualifiedItem.getItem().getId()}));
        }
        else if (isLoggingWarning()) {
          logWarning(MessageFormat.format(Constants.NEGATIVE_PRICE,
              new Object[] {pQualifiedItem.getItem().getId()}));
          totalPrice = 0.0;
        }

      }

      if (isLoggingDebug()) {
        logDebug("before rounding, totalPrice = " + totalPrice);
      }

      totalPrice = getPricingTools().round(totalPrice);

      if (isLoggingDebug()) {
        logDebug("after rounding, totalPrice = " + totalPrice);
      }

      ipi.setAmount(totalPrice);

      if (isLoggingDebug()){
        logDebug("Leaving priceQualifyingItem.");
      }
    }
    finally {
      // cleanup
      if (pExtraParameters != null){
        pExtraParameters.remove(Constants.DISCOUNT_STRUCTURE);
      }

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

  } // end priceQualifyingItems


  /**
   * @see #updateDetailedItemPriceInfos
   * @deprecated This method is deprecated.  Call updateDetailedItemPriceInfos instead.
   */
  protected DetailedItemPriceInfo priceDetailedItemPriceInfo (DetailedItemPriceInfo pDetailedItemPriceInfo,
      QualifiedItem pQualifiedItem,
      List pPriceQuotes,
      List pItems,
      RepositoryItem pPricingModel,
      RepositoryItem pProfile,
      Locale pLocale,
      Order pOrder,
      Map pExtraParameters ) throws PricingException 
      {
    // Do this just so it will still work
    List dpis = updateDetailedPriceInfos(pDetailedItemPriceInfo, pQualifiedItem, pPriceQuotes, pItems,
        pPricingModel, pProfile, pLocale, pOrder, pExtraParameters,
        false);
    if(dpis == null)
      return null;
    DetailedItemPriceInfo detail = null;
    for(int i=0;i < dpis.size(); i++) {
      detail = (DetailedItemPriceInfo) dpis.get(i);
      if(detail != pDetailedItemPriceInfo)
        return detail;
    }
    return detail;
      }

  /**
   * Maintains the Amount, Adjustments, and HasBeenDiscounted properties of
   * pDetailedItemPriceInfo
   * <P>
   * Side Effects:
   * <P>
   * Each DetailedItemPriceInfo has its Amount, Adjustments, and HasBeenDiscounted
   * properties maintained.
   * <P>
   *
   * @param pDetailedItemPriceInfo the price object which should receive the discount specified
   *        by pPricingModel
   * @param pQualifiedItem the object stating which item, and how many of that item, qualified
   *        for the discount defined by pPricingModel.  It is expected that each QualifiedItem
   *        is a FilteredCommerceItem
   * @param pPriceQuotes the price objects corresponding to pItems
   * @param pItems the items whose prices are contained in pPriceQuotes
   * @param pPricingModel the discount which states how the qualifying items should be priced
   * @param pProfile the person for whom the items are to be discounted
   * @param pLocale the locale in which the items are to be discounted
   * @param pOrder the order in which the discounted items have been placed
   * @param pExtraParameters any extra information that this method might need to set the
   *        prices of a number of the qualifying item
   *
   * @return the DetailedItemPriceInfos, original and any newly created ones
   * @exception PricingException if anything went wrong while pricing the
   *  DetailedItemPriceInfo
   * @see priceItems
   * @see priceQualfyingItem
   */
  protected List updateDetailedPriceInfos (DetailedItemPriceInfo pDetailedItemPriceInfo,
      QualifiedItem pQualifiedItem,
      List pPriceQuotes,
      List pItems,
      RepositoryItem pPricingModel,
      RepositoryItem pProfile,
      Locale pLocale,
      Order pOrder,
      Map pExtraParameters ) throws PricingException {
    return updateDetailedPriceInfos(pDetailedItemPriceInfo, pQualifiedItem, pPriceQuotes, pItems,
        pPricingModel, pProfile, pLocale, pOrder, pExtraParameters,
        true);
  }

  /** 
   * This private method is only necessary for backwards
   * compatability.  The last boolean parameter controls this.  If
   * true, use the new approach (split details so the ranges are
   * accurate)
   * If false, use the old approach where there is only one new detail
   * (at most) created.
   **/
  private List updateDetailedPriceInfos (DetailedItemPriceInfo pDetailedItemPriceInfo,
      QualifiedItem pQualifiedItem,
      List pPriceQuotes,
      List pItems,
      RepositoryItem pPricingModel,
      RepositoryItem pProfile,
      Locale pLocale,
      Order pOrder,
      Map pExtraParameters,
      boolean pSplitDetailByRanges) throws PricingException {

    String perfName = "updateDetailedPriceInfos";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    boolean perfCancelled = false;

    try {

      DetailedItemPriceInfo dpi = pDetailedItemPriceInfo;

      // if there's no price to maintain, return
      if (pDetailedItemPriceInfo == null || pDetailedItemPriceInfo.getAmount() < 0.0) {
        if (isLoggingDebug()) {
          logDebug(Constants.NO_INPUT_PRICE);
        }
        return null;
      }

      // these are the ranges that will receive the discount
      List rangesOfDetailsToDiscount = (List) pQualifiedItem.getQualifyingDetailsMap().get(dpi);

      if (rangesOfDetailsToDiscount == null ||
          rangesOfDetailsToDiscount.isEmpty() ) {
        if (isLoggingDebug()) {
          if(pPricingModel != null)
            logDebug("none of the detail qualified for the discount specified by promotion : " + pPricingModel.getRepositoryId());
          else
            logDebug("none of the detail qualified for the discount");
        }
        return null;
      }
      
      // this will be the total number of items receiving the discount
      long discountNumber = determineDiscountNumber(pDetailedItemPriceInfo,pQualifiedItem,pPricingModel);
      if(discountNumber == 0)
        return null;
      // if we can discount all of the available units in this details
      // there's no need to split this details
      if (discountNumber == dpi.getQuantity()) {

        // adjust the price of the the full quantity 
        adjustEntireDetailedPrice(dpi, pPriceQuotes, pItems, pPricingModel, 
            pProfile, pLocale, pOrder, pExtraParameters);

        // we need to return the detail
        List newOnes = new ArrayList();
        newOnes.add(dpi);
        return newOnes;

      } else {

        return  adjustPartialDetailedPrice(dpi, rangesOfDetailsToDiscount, discountNumber,
            pQualifiedItem, pSplitDetailByRanges, pPriceQuotes, pItems, 
            pPricingModel, pProfile, 
            pLocale, pOrder, pExtraParameters);

      } // end else break apart the details

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

  } // end updateDetailedPriceInfos

  /**
   * determines the quantity to be discounted in the given detailed item price info
   * @param pDetailedItemPriceInfo
   * @param pQualifiedItem
   * @param pPricingModel
   * @return
   * @throws PricingException
   */
  protected long determineDiscountNumber(DetailedItemPriceInfo pDetailedItemPriceInfo,
      QualifiedItem pQualifiedItem,RepositoryItem pPricingModel)
  throws PricingException
  {
    // these are the ranges that will receive the discount
    DetailedItemPriceInfo dpi = pDetailedItemPriceInfo;
    // this will be the total number of items receiving the discount
    long discountNumber = 0;
    // these are the ranges that will receive the discount
    List rangesOfDetailsToDiscount = (List) pQualifiedItem.getQualifyingDetailsMap().get(dpi);

    if (rangesOfDetailsToDiscount == null ||
        rangesOfDetailsToDiscount.isEmpty() ) {
      
      return discountNumber;
    }

    Iterator rangeIterator = rangesOfDetailsToDiscount.iterator();
    while (rangeIterator.hasNext()) {
      Range range = (Range) rangeIterator.next();
      discountNumber += ( range.getHighBound() - range.getLowBound() + 1);
    }

    if (discountNumber > dpi.getQuantity() ||
        discountNumber < 0 ){
      throw new PricingException("Qualifier says that " + discountNumber + " of this details qualify for discount when there are only " + dpi.getQuantity() + " items in this details.");
    }

    return discountNumber;

  }

  /**
   * This will discount the entire quantity of the given DetailedItemPriceInfo.  It assumes that the 
   * entire quantity is eligible to receive the discount.
   *
   * @param pDetailedItemPriceInfo the price object which should receive the discount specified
   *        by pPricingModel
   * @param pPriceQuotes the price objects corresponding to pItems
   * @param pItems the items whose prices are contained in pPriceQuotes (item of pOrder)
   * @param pPricingModel the discount which states how the qualifying items should be priced
   * @param pProfile the person for whom the items are to be discounted
   * @param pLocale the locale in which the items are to be discounted
   * @param pOrder the order in which the discounted items have been placed
   * @param pExtraParameters any extra information that this method might need to set the
   *        prices of a number of the qualifying item
   *
   * @exception PricingException if anything went wrong while pricing the
   *  DetailedItemPriceInfo
   **/
  private void adjustEntireDetailedPrice(DetailedItemPriceInfo pDetailedItemPriceInfo, List pPriceQuotes, 
      List pItems, RepositoryItem pPricingModel, RepositoryItem pProfile, 
      Locale pLocale, Order pOrder, Map pExtraParameters)
  throws PricingException
  {
    if (isLoggingDebug()) {
      logDebug("discount this whole details, quantity: " + pDetailedItemPriceInfo.getQuantity());
    }

    double currentAmount = pDetailedItemPriceInfo.getAmount();

    // findAdjustedPrice actually just finds the adjusted unit price
    double oldUnitPrice = pDetailedItemPriceInfo.getDetailedUnitPrice();
    double newUnitPrice =
      findAdjustedPrice(pDetailedItemPriceInfo,
          pPriceQuotes,
          pItems,
          pPricingModel,
          pProfile,
          pLocale,
          pOrder,
          pExtraParameters);

    if (Double.isNaN(newUnitPrice)) {
      // No discount to apply
      return;
    }
    
    // multiply the difference in unit prices to find the new discount
    double totalDiscount = (oldUnitPrice - newUnitPrice) * pDetailedItemPriceInfo.getQuantity();

    if (isLoggingDebug()) {
      logDebug("starting currentAmount: " + currentAmount + " and totalDiscount: " + totalDiscount);
    }

    // set the new detailed price to the newPrice
    double newPrice = currentAmount - totalDiscount;

    if (isLoggingDebug()) {
      logDebug("before rounding, newPrice = " + newPrice);
    }

    newPrice = getPricingTools().round(newPrice);

    if (isLoggingDebug()) {
      logDebug("after rounding, newPrice = " + newPrice);
    }

    pDetailedItemPriceInfo.setAmount(newPrice);

    // add a record of this adjustment to the detailed itempriceinfo
    double newAdjustAmount = newPrice - currentAmount;

    if (isLoggingDebug()) {
      logDebug("before rounding, newAdjustAmount = " + newAdjustAmount);
    }

    newAdjustAmount = getPricingTools().round(newAdjustAmount);

    if (isLoggingDebug()) {
      logDebug("after rounding, newAdjustAmount = " + newAdjustAmount);
    }

    PricingAdjustment newAdjustment = new PricingAdjustment (Constants.ITEM_DISCOUNT_PRICE_ADJUSTMENT_DESCRIPTION,
        pPricingModel,
        newAdjustAmount,
        pDetailedItemPriceInfo.getQuantity());

    pDetailedItemPriceInfo.getAdjustments().add(newAdjustment);

    // register that this details has received a discount of some kind
    pDetailedItemPriceInfo.setDiscounted(true);

    return;
  }        


  /**
   * This will discount the given ranges of the given DetailedItemPriceInfo.  It will split
   * the detail accordingly.
   * We might create multiple new detailedItemPriceInfos.  Say the current range is 0-4 and
   * the discount applies to 2-2.  We will have three Details for the 0-4 range then.
   * The first is 0-1 and is undiscounted.  The second is 2-2 and is discounted.  The last
   * is 3-4 and is undiscounted.  The first and third will actually have all the same information
   * except for the range.
   *
   * @param pDetailedItemPriceInfo the price object which should receive the discount specified
   *        by pPricingModel
   * @param pRangesToDiscount The list of ranges that qualify for the discount
   * @param pNumberToDiscount The total quantity of pDetailedItemPriceInfo to discount.  This should equal
   *                          the sum of the sizes of all the ranges in pRangesToDiscount
   * @param pQualifiedItem the object stating which item, and how many of that item, qualified
   *        for the discount defined by pPricingModel.  (Returned by the Qualifier)  It is expected 
   *        that each QualifiedItem is a FilteredCommerceItem
   * @param pSplitDetailByRanges If this is true, then the details will be split so that each 
   *                             detail refers to a range that is priced in exactly the same way 
   *                             (see the javadoc for this method).  If this is false, then
   *                             this method will return exactly two details.  One for the discounted
   *                             portion and one for the undiscounted portion.  This parameter
   *                             is here for backwards compatability only.  There is no reason
   *                             to ever pass in false.
   * @param pPriceQuotes the price objects corresponding to pItems
   * @param pItems the items whose prices are contained in pPriceQuotes (item of pOrder)
   * @param pPricingModel the discount which states how the qualifying items should be priced
   * @param pProfile the person for whom the items are to be discounted
   * @param pLocale the locale in which the items are to be discounted
   * @param pOrder the order in which the discounted items have been placed
   * @param pExtraParameters any extra information that this method might need to set the
   *        prices of a number of the qualifying item
   *
   * @return the DetailedItemPriceInfos, original and any newly created ones
   * @exception PricingException if anything went wrong while pricing the
   *  DetailedItemPriceInfo
   **/
  private List adjustPartialDetailedPrice(DetailedItemPriceInfo pDetailedItemPriceInfo, List pRangesToDiscount,
      long pNumberToDiscount, QualifiedItem pQualifiedItem, 
      boolean pSplitDetailByRanges,
      List pPriceQuotes, List pItems, 
      RepositoryItem pPricingModel, RepositoryItem pProfile, 
      Locale pLocale, Order pOrder, 
      Map pExtraParameters)
  throws PricingException
  {
    if (isLoggingDebug()) {
      logDebug("splitting details of quantity: " + pDetailedItemPriceInfo.getQuantity());
    }

    // we must discount what we can, and split off the rest

    long oldQuantity = pDetailedItemPriceInfo.getQuantity();
    double oldUnitPrice = pDetailedItemPriceInfo.getDetailedUnitPrice();
    double oldAmount = pDetailedItemPriceInfo.getAmount();
    //long oldQuantityAsQualifier = pDetailedItemPriceInfo.getQuantityAsQualifier();
    
    if (isLoggingDebug()) {
      logDebug("old details quantity: " + oldQuantity);
      logDebug("old details unitprice: " + oldUnitPrice);
      logDebug("old details amount: " + oldAmount);
      //logDebug("old details quantity as qualifier: " + oldQuantityAsQualifier);
    }

    // we might create multiple new detailedItemPriceInfos.  Say the current range is 0-4 and
    // the discount applies to 2-2.  We will have three Details for the 0-4 range then.
    // The first is 0-1 and is undiscounted.  The second is 2-2 and is discounted.  The last
    // is 3-4 and is undiscounted.  The first and third will actually have all the same information
    // except for the range.
    // is it necessary to sort?
    List discountRanges = Range.union(pRangesToDiscount);
    List undiscountedRanges = Range.findComplementRanges(discountRanges, pDetailedItemPriceInfo.getRange());

    // at the end we will split the details according to these ranges

    // create a new DetailedItemPriceInfo to represent the piece that we couldn't discount
    DetailedItemPriceInfo undiscountedDpi = getPricingTools().createDetailedItemPriceInfo(pDetailedItemPriceInfo);

    // set the itemPriceInfo
    undiscountedDpi.setItemPriceInfo(pDetailedItemPriceInfo.getItemPriceInfo());

    // set the quantity to the number that's not going to be discounted
    undiscountedDpi.setQuantity(pDetailedItemPriceInfo.getQuantity() - pNumberToDiscount);

    // set the quantity of the one that's going to be discounted
    long difference = pDetailedItemPriceInfo.getQuantity() - pNumberToDiscount;
    pDetailedItemPriceInfo.setQuantity(pNumberToDiscount);

    // This is new with the addition of ranges to DetailedItemPriceInfo
    // update the range
    pDetailedItemPriceInfo.getRange().setHighBound(pDetailedItemPriceInfo.getRange().getHighBound() - difference);
    long newLowBound = pDetailedItemPriceInfo.getRange().getHighBound() + 1;

    Range newRange = new Range(newLowBound, newLowBound + undiscountedDpi.getQuantity() - 1);
    undiscountedDpi.setRange(newRange);
    
    // we're essentially condensing all of the ranges to be discounted down into one
    // new details.  We need to find the intersection of the ranges to be discounted (pre-split)
    // and the ranges that are acting as qualifiers.  Then when we condense the ranges, we know
    /* how many to mark as acting as qualifier

    List quantityReceivingDiscountAndActingAsQualifier =
      Range.intersection(pRangesToDiscount, quantityAsQualifierRanges);

    // add up the number that will be both receiving the discount and
    // acting as qualifier
    long dpiQuantityAsQualifier = 0;

    if (quantityReceivingDiscountAndActingAsQualifier != null) {
      Iterator intersectionIterator = quantityReceivingDiscountAndActingAsQualifier.iterator();
      while (intersectionIterator.hasNext()) {
        Range range = (Range) intersectionIterator.next();
        dpiQuantityAsQualifier += (range.getHighBound() - range.getLowBound() + 1);
      }
    } // end if there's anything both receiving the discount and acting as qualifier

    long undiscountedDpiQuantityAsQualifier = oldQuantityAsQualifier - dpiQuantityAsQualifier;

    if (isLoggingDebug()) {
      logDebug("this details' quantity that both receives discount and acted as qualifier: " + dpiQuantityAsQualifier);
      logDebug("this details' quantity that does not receive discount and acted as qualifier: " + undiscountedDpiQuantityAsQualifier);
    }

    pDetailedItemPriceInfo.setQuantityAsQualifier(dpiQuantityAsQualifier);
    undiscountedDpi.setQuantityAsQualifier(undiscountedDpiQuantityAsQualifier);
*/
    // set the non-discounted one's price to be its quantity times the old unit price
    double undiscountedAmount = oldUnitPrice * undiscountedDpi.getQuantity();

    if (isLoggingDebug()) {
      logDebug("rounding non-discounted detail amount: " + undiscountedAmount);
    }

    undiscountedDpi.setAmount(undiscountedAmount);

    double discountedAmount = oldUnitPrice * pDetailedItemPriceInfo.getQuantity();

    if (isLoggingDebug()) {
      logDebug("before rounding, discountedAmount = " + discountedAmount);
    }

    // set the old one's price
    pDetailedItemPriceInfo.setAmount(discountedAmount);

    if (isLoggingDebug()) {
      logDebug("checking if non-discounted detail + discounted detail == total amount");
    }

    //Calculate the total of the non-discounted and discounted dipi amounts
    double totalAmount = pDetailedItemPriceInfo.getAmount() + undiscountedDpi.getAmount();

    if (isLoggingDebug()) {
      logDebug("before rounding, totalAmount = " + totalAmount);
    }

    totalAmount = getPricingTools().round(totalAmount);

    if (isLoggingDebug()) {
      logDebug("after round, totalAmount = " + totalAmount);
    }

    if (totalAmount != oldAmount) {
      try {
        PerformanceMonitor.cancelOperation("ItemDiscountCalculator_priceDetailedItemPriceInfo");
      } catch (Exception x) {
        if (isLoggingError()) {
          logError(x.toString());
        }
      }

      throw new PricingException(Constants.BAD_DETAILS_SPLIT);
    }

    
    undiscountedAmount = getPricingTools().round(undiscountedAmount);

    if (isLoggingDebug()) {
      logDebug("rounded undiscounted detail amount to: " + undiscountedAmount);
    }

    // set the undiscounted amount to the rounded amount now that we checked for proper splitting
    undiscountedDpi.setAmount(undiscountedAmount);

    discountedAmount = oldAmount - undiscountedAmount; // Gives us the actual discounted amount

    if (isLoggingDebug()) {
      logDebug("recalculating discounted detail amount after rounding undiscounted amount, new value is: " + discountedAmount);
    }
    
    //round the discounted amount
    discountedAmount = getPricingTools().round(discountedAmount);
    
    if (isLoggingDebug()) {
      logDebug("rounded discounted detail amount to: " + discountedAmount);
    }
    
    //Set the discounted detail to the real discounted amount
    pDetailedItemPriceInfo.setAmount(discountedAmount);
    
    // discount the old one, leave the new one alone
    
    double currentAmount = pDetailedItemPriceInfo.getAmount();

    // findAdjustedPrice actually just finds the adjusted unit price
    double newUnitPrice =
      findAdjustedPrice(pDetailedItemPriceInfo,
          pPriceQuotes,
          pItems,
          pPricingModel,
          pProfile,
          pLocale,
          pOrder,
          pExtraParameters);

    if (Double.isNaN(newUnitPrice)) {
      // No discount to apply
      return null;
    }
    
    // multiply the difference in unit prices to find the new discount
    double totalDiscount = (oldUnitPrice - newUnitPrice) * pDetailedItemPriceInfo.getQuantity();


    if (isLoggingDebug()) {
      logDebug("starting currentAmount: " + currentAmount + " and totalDiscount: " + totalDiscount);
    }

    // set the new detailed price to the newPrice
    double newPrice = currentAmount - totalDiscount;

    if (isLoggingDebug()) {
      logDebug("before rounding, newPrice = " + newPrice);
    }

    newPrice = getPricingTools().round(newPrice);

    if (isLoggingDebug()) {
      logDebug("after rounding, newPrice = " + newPrice);
    }

    pDetailedItemPriceInfo.setAmount(newPrice);

    // add a record of this adjustment to the new details
    double newAdjustAmount = newPrice - currentAmount;

    if (isLoggingDebug()) {
      logDebug("before rounding, newAdjustAmount = " + newAdjustAmount);
    }

    newAdjustAmount = getPricingTools().round(newAdjustAmount);

    if (isLoggingDebug()) {
      logDebug("after rounding, newAdjustAmount = " + newAdjustAmount);
    }

    PricingAdjustment newAdjustment = new PricingAdjustment (Constants.ITEM_DISCOUNT_PRICE_ADJUSTMENT_DESCRIPTION,
        pPricingModel,
        newAdjustAmount,
        pDetailedItemPriceInfo.getQuantity());
    pDetailedItemPriceInfo.getAdjustments().add(newAdjustment);

    // register that the new details has received a discount of some kind
    pDetailedItemPriceInfo.setDiscounted(true);

    if (isLoggingDebug()) {
      logDebug("Now have two details, old one with quantity : " + pDetailedItemPriceInfo.getQuantity() + " and new one with quantity : " + undiscountedDpi.getQuantity());
    }
    // return the undiscounted one, to show that it was split
    List dpis = null;
    if(pSplitDetailByRanges) {
      DetailedItemPriceTools detailTools = getPricingTools().getDetailedItemPriceTools();
      List discountedDPIs = detailTools.splitDetailsAccordingToRanges(pDetailedItemPriceInfo, discountRanges);
      dpis = detailTools.splitDetailsAccordingToRanges(undiscountedDpi, undiscountedRanges);
      dpis.addAll(discountedDPIs);
    }
    else {
      dpis = new ArrayList(2);
      dpis.add(pDetailedItemPriceInfo);
      dpis.add(undiscountedDpi);
    }
    
    // Need to update the quantity as qualifier DIPI details
    FilteredCommerceItem filteredCommerceItem = null;
    if (pQualifiedItem.getItem() instanceof FilteredCommerceItem) {
      filteredCommerceItem = (FilteredCommerceItem)pQualifiedItem.getItem();
      updateQuantityAsQualifier(filteredCommerceItem, dpis, 
          (List<Range>)filteredCommerceItem.getQuantityAsQualifierDetails().get(pDetailedItemPriceInfo));
    }
  
    return dpis;

  }

  /**
   * After a partial adjustment a detail may have been split into multiple details.
   * This method ensures that the quantityAsQualifier ranges mapped against the original
   * detail are also split accordingly.
   * 
   * @param pFilteredCommerceItem FilteredCommerceItem to update
   * @param pDetails List of DetailedItemPriceInfo after the split, includes the original detail too.
   * @param pQuantityAsQualifierRanges List of Range of the original detail quantity as qualifiers.
   * @throws PricingException for errors.
   */
  protected void updateQuantityAsQualifier(
      FilteredCommerceItem pFilteredCommerceItem,
      List<DetailedItemPriceInfo> pDetails, 
      List<Range> pQuantityAsQualifierRanges) throws PricingException
  {
    if (isLoggingDebug()){
      logDebug("Entered updateQuantityAsQualifier");
      logDebug("Original detail pQuantityAsQualifierRanges:" + pQuantityAsQualifierRanges);
    }
    
    if (pFilteredCommerceItem == null) {
      throw new PricingException("Filtered commerce item is null.");
    }
    
    if ((pDetails == null) || (pDetails.size() <= 0)) {
      // No details to update
      if (isLoggingDebug()){
        logDebug("Leaving since there are no details to update.");
      }
      return;
    }
    
    if ((pQuantityAsQualifierRanges == null) || (pQuantityAsQualifierRanges.size() <= 0)) {
      // No qualifiers marked on the original detail so there won't be any on the split details either
      if (isLoggingDebug()){
        logDebug("Leaving since the orignal detail has no qualifiers marked.");
      }
      return;
    }
    
    // Get the current commerce item map
    Map<DetailedItemPriceInfo,List<Range>> detailQualifierRangesMap = 
      pFilteredCommerceItem.getQuantityAsQualifierDetails();
    
    // Loop through all details and update them as needed
    List<Range> intersectingRanges = null;
    List<Range> detailRanges = new ArrayList<Range>();
    Range detailRange = null;
    for (DetailedItemPriceInfo detail : pDetails) {
      detailRange = detail.getRange();
      if (detailRange == null) {
        detailRange = new Range(0, detail.getQuantity()-1);
      }
      detailRanges.clear();
      detailRanges.add(detailRange);
      
      intersectingRanges = Range.intersection(detailRanges, pQuantityAsQualifierRanges);
      
      if ((intersectingRanges != null) && (intersectingRanges.size() > 0)) {
        long detailQuantityAsQualifier = 0L;
        for (Range intersectingRange : intersectingRanges){
          detailQuantityAsQualifier += intersectingRange.getSize();
        }
        detail.setQuantityAsQualifier(detailQuantityAsQualifier);
        detailQualifierRangesMap.put(detail, intersectingRanges);
        
        if (isLoggingDebug()){
          logDebug("detail " + detail.getRange() + " has qualifiers: " + intersectingRanges);
        }
      }
      else {
        // No quantity as qualifier ranges in this detail
        detail.setQuantityAsQualifier(0L);
        detailQualifierRangesMap.remove(detail);
      }
    }
    
    if (isLoggingDebug()){
      logDebug("Leaving updateQuantityAsQualifier");
    }
    
    return;
  }
  
  /**
   * Calls the DiscountCalculatorService's adjust method to determine the new price for
   * the input pDetailedItemPriceInfo.
   *
   * @param pDetailedItemPriceInfo the price object whose price is to be modified
   * @param pPriceQuotes the price objects corresponding to pItems
   * @param pItems the items whose prices are contained in pPriceQuotes
   * @param pPricingModel the discount which states how the qualifying items should be priced
   * @param pProfile the person for whom the items are to be discounted
   * @param pLocale the locale in which the items are to be discounted
   * @param pOrder the order in which the discounted items have been placed
   * @param pExtraParameters any extra information that this method might need to set the
   *        prices of a number of the qualifying item
   *
   * @return the adjusted price for the input pDetailedItemPriceInfo
   * @exception PricingException if there was a problem in determining the adjusted price
   */
  public double findAdjustedPrice(DetailedItemPriceInfo pDetailedItemPriceInfo,
      List pPriceQuotes,
      List pItems,
      RepositoryItem pPricingModel,
      RepositoryItem pProfile,
      Locale pLocale,
      Order pOrder,
      Map pExtraParameters) throws PricingException {

    String perfName = "findAdjustedPrice";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    boolean perfCancelled = false;

    try {

      // find out what to do
      String discountType = null;
      double adjuster = 0D;
      double currentUnitPrice = pDetailedItemPriceInfo.getDetailedUnitPrice();;

      adjuster = getAdjuster(pPricingModel, pExtraParameters);
      if (Double.isNaN(adjuster)) {
        // No valid discount found so don't adjust price
        return adjuster;
      }
      discountType = getDiscountType(pPricingModel, pExtraParameters);

      return adjust(currentUnitPrice, adjuster, discountType, pPricingModel.getRepositoryId());

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
  } // end findAdjustedPrice

  /**
   * This will return the amount that will eventually be discounted.  This will 
   * be priceQuote.amount by default (and there will usually be no need to change this.)
   *
   * @param pOrder The order containing the shipping group
   * @param pPriceQuote ShippingPriceInfo representing the current price quote for the shipping group
   * @param pShippingGroup The shipping group that will be discounted (ignored by default)
   * @param pPricingModel A RepositoryItems representing a PricingModel (ignored by default)
   * @param pProfile The user's profile (ignored by default)
   * @param pExtraParameters A Map of extra parameters to be used in the pricing, may be null (ignored by default)
   * @return The amount to discount... defaults to pPriceQuote.getAmount()
   **/
  protected double getAmountToDiscount(DetailedItemPriceInfo pDetailedItemPriceInfo,
      List pPriceQuotes,
      List pItems,
      RepositoryItem pPricingModel,
      RepositoryItem pProfile,
      Locale pLocale,
      Order pOrder,
      Map pExtraParameters) 
  throws PricingException 
  {
    return pDetailedItemPriceInfo.getDetailedUnitPrice();
  }
  
  /**
   * This method allows a QualifiedItem qualifyingDetailsMap to be updated.
   * This can be necessary if the map created by the Qualifier service is not correct.
   * <p>
   * For example it is possible for multiple discounts in a single promotion that a previous discount 
   * could have split some detailed item price info.  
   * However the QualifiedItem was created before this split took place and so some qualifying ranges 
   * may still be pointing to the original unsplit detailed item price info.  
   * So we need to fix any such references to point to the correct dipi.
   * This implementation will update the QualifiedItem's qualifyingDetailsMap to ensure the latest
   * detailed item price info references are used.
   * 
   * @param pQualifiedItem the QualifiedItem to check and fix if needed
   * @param pPriceQuote the item price info 
   * @throws PricingException if the qualifying ranges can't be updated
   **/
   protected void updateQualifyingDetails(QualifiedItem pQualifiedItem, ItemPriceInfo pPriceQuote) 
     throws PricingException
   {
     
     if (isLoggingDebug()){
       logDebug("Entered updateQualifyingDetails");
       logDebug("pQualifiedItem:" + pQualifiedItem);
     }
     
     Map qualifyingDetailsMap = pQualifiedItem.getQualifyingDetailsMap();
     
     DetailedItemPriceInfo actualDipi = null;
     List ranges = null;
     Range range = null;
     Range dipiRange = null;
     List actualDipisForRange = null;
     Map updatedQualifyingDetailsMap = new HashMap();
     long lowBound = 0L;
     long highBound = 0L;
     List allRanges = new ArrayList();
     
     // Get a union of all the ranges to receive the discount
     for (Object rangesObj : qualifyingDetailsMap.values()) {
       ranges = (List)rangesObj;
       allRanges.addAll(ranges);
     }
     allRanges = Range.union(allRanges);
     
     for (Object rangeObj : allRanges) {
       range = (Range)rangeObj;
       actualDipisForRange = pPriceQuote.getCurrentPriceDetailsForRange(range);
       
       for (Object actualDipiObj : actualDipisForRange) {
         // Get the actual dipi and it's current updated ranges
         actualDipi = (DetailedItemPriceInfo)actualDipiObj;
         ranges = (List)updatedQualifyingDetailsMap.get(actualDipi);
         if (ranges == null) {
           ranges = new ArrayList();
         }
         
         // Which part of the range is for this actual dipi
         dipiRange = actualDipi.getRange();
         lowBound = dipiRange.getLowBound();
         if (dipiRange.getLowBound() < range.getLowBound()) {
           lowBound = range.getLowBound();
         }
         
         highBound = dipiRange.getHighBound();
         if (dipiRange.getHighBound() > range.getHighBound()) {
           highBound = range.getHighBound();
         }
         dipiRange = new Range(lowBound, highBound);
         
         // Add in this part of the range and update the map
         ranges.add(dipiRange);     
         ranges = Range.union(ranges);
         updatedQualifyingDetailsMap.put(actualDipi, ranges);         
       }
     }
     
     // Replace the real map
     pQualifiedItem.setQualifyingDetailsMap(updatedQualifyingDetailsMap);
     
     if (isLoggingDebug()){
       logDebug("Leaving updateQualifyingDetails");
       logDebug("pQualifiedItem:" + pQualifiedItem);
     }
   }
   
} // end of class



