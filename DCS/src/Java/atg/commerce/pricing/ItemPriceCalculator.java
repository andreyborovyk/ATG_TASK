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
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.service.perfmonitor.PerformanceMonitor;

/**
 * Superclass for all CommerceItem calculators.  Consolidates 
 * functionality that some item price calculators have in common.  ItemPriceCalculator
 * extends <code>ApplicationLoggingImpl</code>, so classes that extend this one
 * have access to Nucleus logging services.  This class contains a single abstract 
 * method: <code>priceItem</code>.  Extending classes implement this method to leverage 
 * this class's other CommerceItem pricing methods.
 * <p>
 * Properties:
 * <ul>
 *  <li><b>loggingIdentifier</b> is the ID that this class uses to identify itself in logs.
 *  <li><b>pricePropertyName</b> is the name of the property of the <code>priceSource</code> which 
 *         represents an item's price.
 *  <li><b>requirePriceValue</b>: if true, an exception is thrown if the <code>priceSource</code>
 *         if the CommerceItem doesn't have its [<code>pricePropertyName</code>] property set.
 *  <li><b>priceFromCatalogRef</b>: if true, <code>getPriceSource</code> returns the 
 *         <code>catalogRef</code> property of the input CommerceItem.  If false,
 *         <code>getPriceSource</code> returns the <code>productRef</code> property.
 *  <li><b>getPriceSource</b> returns the <code>catalogRef</code> property of the input 
 *       CommerceItem if <code>priceFromCatalogRef</code> is true.  If
 *        <code>priceFromCatalogRef</code> is false, 
 *        <code>getPriceSource</code> returns the <code>productRef</code> property.
 *  </ul>
 *  
 *
 * @see ItemListPriceCalculator
 * @see ItemSalePriceCalculator
 * 
 * @author Graham Mather
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/ItemPriceCalculator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public abstract class ItemPriceCalculator 
extends GenericService
implements ItemPricingCalculator, ItemPriceSourceHandler
{
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/ItemPriceCalculator.java#2 $$Change: 651448 $";

  private static final String PERFORM_MONITOR_NAME="ItemPriceCalculator";

  PricingTools mPricingTools;

  /**
   * pricing tools to help with price calculation
   * @beaninfo description: pricing tools to help with price calculation
   * @param pPricingTools new value to set
   */
  public void setPricingTools(PricingTools pPricingTools)
  {mPricingTools = pPricingTools;}

  /**
   * pricing tools to help with price calculation
   * @beaninfo description: pricing tools to help with price calculation
   * @return property PricingTools
   */
  public PricingTools getPricingTools()
  {return mPricingTools;}

  //-------------------------------------
  // property: LoggingIdentifier
  /** the ID that this class uses to identify itself in logs */
  String mLoggingIdentifier;

  /**
   * the ID that this class uses to identify itself in logs
   * @beaninfo description: the ID that this class uses to identify itself in logs
   * @param pLoggingIdentifier new value to set
   */
  public void setLoggingIdentifier(String pLoggingIdentifier)
  {mLoggingIdentifier = pLoggingIdentifier;}

  /**
   * the ID that this class uses to identify itself in logs
   * @beaninfo description: the ID that this class uses to identify itself in logs
   * @return property LoggingIdentifier
   */
  public String getLoggingIdentifier()
  {return mLoggingIdentifier;}
  

  //---------------------------------------------------------------------------
  // property: PricePropertyName
  String mPricePropertyName = null;

  /**
   * Returns the name of the property of the catalogRef property
   * of CommerceItem which contains the price
   * @param pPricePropertyName the new value to set
   */
  public void setPricePropertyName(String pPricePropertyName) {
    mPricePropertyName = pPricePropertyName;
  }

  /**
   * Returns the name of the property of the catalogRef property
   * of CommerceItem which contains the price
   * @return the property pricePropertyName
   * @beaninfo description: Returns the name of the property of the
   * catalogRef property of CommerceItem which contains the price
   */
  public String getPricePropertyName() {
    return mPricePropertyName;
  }

  //-------------------------------------
  // property: RequirePriceValue
  boolean mRequirePriceValue = true;

  /**
   * Sets property RequirePriceValue
   **/
  public void setRequirePriceValue(boolean pRequirePriceValue) {
    mRequirePriceValue = pRequirePriceValue;
  }

  /**
   * Returns property RequirePriceValue
   **/
  public boolean isRequirePriceValue() {
    return mRequirePriceValue;
  }  

  //-------------------------------------
  // property: PriceFromCatalogRef
  boolean mPriceFromCatalogRef = true;

  /**
   * Sets property PriceFromCatalogRef
   **/
  public void setPriceFromCatalogRef(boolean pPriceFromCatalogRef) {
    mPriceFromCatalogRef = pPriceFromCatalogRef;
  }

  /**
   * Returns property PriceFromCatalogRef. 
   * @return true if the price should be extracted from the catalogRef (aka sku).
   * Otherwise extract the price from the product object
   **/
  public boolean isPriceFromCatalogRef() {
    return mPriceFromCatalogRef;
  }

  /**
   * Return the object which should be used as the source for pricing.
   * This method uses the <code>priceFromCatalogRef</code> property as
   * a switch to determine if the catalogRef or productRef should be returned
   * from the CommerceItem
   */
  protected Object getPriceSource(CommerceItem pItem) 
       throws PricingException
  {
    if (isPriceFromCatalogRef())
      return pItem.getAuxiliaryData().getCatalogRef();
    else
      return pItem.getAuxiliaryData().getProductRef();
  }

  /**
   * This method is called by the calculator to get the <code>ItemPriceSource</code> that 
   * should be used to price the given <code>CommerceItem</code>
   * <p>
   * This implemenation returns the item's matching source from the extra parameters 
   * @see PricingTools#getItemPriceSource(ItemPriceInfo, CommerceItem, RepositoryItem, Locale, RepositoryItem, Map)
   */
  public ItemPriceSource getItemPriceSource(ItemPriceInfo pPriceQuote, CommerceItem pItem, RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
  {
    return getPricingTools().getItemPriceSource(pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters);
  }
  
  /**
   * Returns the object that should be used as a price source for the
   * given commerce item. A price source object should have a property with the name
   * defined by the configurable property <code>pricePropertyName</code>. 
   * <p>
   * This implementation first checks for a <code>ItemPriceSource</code> and returns it, if 
   * found. Otherwise, it delegates to the <code>getPriceSource(CommerceItem)</code> method
   * @see #getPriceSource(CommerceItem)  
   */
  public Object getPriceSource(ItemPriceInfo pPriceQuote, 
      CommerceItem pItem, RepositoryItem pPricingModel, 
      Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
  throws PricingException
  {
    ItemPriceSource ps = getItemPriceSource (pPriceQuote, pItem,
        pPricingModel, pLocale, pProfile,pExtraParameters);

    if(ps != null)
      return ps;

    return getPriceSource(pItem);
  }

  /**
   * Return the price that should be used in pricing. Return -1 if no pricing should take place
   * for this object
   */
  protected double getPrice(Object pPriceSource) 
       throws PricingException
  {

    /*
      This should really be in here, but bad bytecode is getting generated.  Taken out.

    String perfName = "getPrice";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    boolean perfCancelled = false;
    try {
    */

    Object priceObj = null;

    try {
      priceObj = DynamicBeans.getPropertyValue(pPriceSource, getPricePropertyName());
    } catch (PropertyNotFoundException e) {

      throw new PricingException(e);
    }

    /*
    if (isLoggingDebug())
      logDebug("Price object for " + pPriceSource + "." + getPricePropertyName() + "=" + priceObj);
    */

    if (priceObj == null) {
      if (isRequirePriceValue()) {
        throw new PricingException(MessageFormat.format(Constants.PRICE_PROPERTY_NULL,
							new Object[] {
							  pPriceSource
							}));
      } else {
        return -1;
      }
    }

    try {
      return ((Number) priceObj).doubleValue();
    }
    catch (ClassCastException exc) {

      throw new PricingException(MessageFormat.format(Constants.PRICE_PROPERTY_NOT_NUMBER,
						      new Object[] {
							priceObj
						      }), exc);
    }


    /*
      bad bytecode: taken out.
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
    */

  }

  //-------------------------------------
  /**
   * Price a single item in a context
   * <p>
   * The list price is determined from price source object returned by <code>getPriceSource</code>
   * @see #getPriceSource(ItemPriceInfo, CommerceItem, RepositoryItem, Locale, RepositoryItem, Map)
   * @param pPriceQuote ItemPriceInfo representing the current price quote for the item
   * @param pItem The item to price
   * @param pPricingModel A RepositoryItem representing a PricingModel
   * @param pProfile The user's profile
   * @param pExtraParameters A Map of extra parameters to be used in the pricing, may be null
   */
  public void priceItem(ItemPriceInfo pPriceQuote, CommerceItem pItem, RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
       throws PricingException
  {

    String perfName = "priceItem";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    boolean perfCancelled = false;
    
    try {

    Object priceSource = getPriceSource(pPriceQuote,pItem,pPricingModel,pLocale,pProfile,pExtraParameters);
          
    if (isLoggingDebug())
      logDebug("Calculating price for " + priceSource);

    if (priceSource == null) {
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
      
      throw new PricingException(MessageFormat.format(Constants.NO_PRICE_SOURCE,
						      new Object[] {
							pItem
						      }));
    }

    //if the source is an source, check if the calculator should consume it
    if(priceSource instanceof ItemPriceSource)
      if(!shouldConsumeSource((ItemPriceSource) priceSource))
        return;

    double price = getPrice(priceSource);
    priceItem(price, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters);

    if (isLoggingDebug())
      logDebug("ItemPriceInfo for " + priceSource + "=" + pPriceQuote);

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
  
  /**
   * Set the price info given the determined price
   * @param pPrice the price as extracted from the item
   * @param pPriceQuote ItemPriceInfo representing the current price quote for the item
   * @param pItem The item to price
   * @param pPricingModel A RepositoryItem representing a PricingModel
   * @param pProfile The user's profile
   * @param pExtraParameters A Map of extra parameters to be used in the pricing, may be null
   */
  protected abstract void priceItem(double pPrice, ItemPriceInfo pPriceQuote, CommerceItem pItem, 
                                    RepositoryItem pPricingModel, Locale pLocale, 
                                    RepositoryItem pProfile, Map pExtraParameters) throws PricingException;


  //-------------------------------------
  /**
   * Price each of a List of items in a context
   *
   * @param pPriceQuotes List of ItemPriceInfo objects representing the current price quotes for each item
   * @param pItems The items to price (individually)
   * @param pPricingModel A RepositoryItem representing a PricingModel
   * @param pProfile The user's profile
   * @param pExtraParameters A Map of extra parameters to be used in the pricing, may be null
   */
  public void priceEachItem(List pPriceQuotes, List pItems, RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
       throws PricingException
  {
    String perfName = "priceEachItem";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    boolean perfCancelled = false;
    
    try {

    // sanity check
    // make sure both Lists have the same number of elements
    if (pPriceQuotes.size() != pItems.size()) {
      throw new PricingException
        (MessageFormat.format
         (Constants.BAD_LIST_PARAMS,
          new Object[]  {
           Integer.valueOf(pPriceQuotes.size()),
             Integer.valueOf(pItems.size())
             }));
    }
      
    ListIterator quoterator = pPriceQuotes.listIterator();
    ListIterator itemerator = pItems.listIterator();


    while (quoterator.hasNext() &&
           itemerator.hasNext() ) {

      ItemPriceInfo amount = (ItemPriceInfo) quoterator.next();
      CommerceItem item = (CommerceItem) itemerator.next();
	  
      priceItem(amount, item, pPricingModel, pLocale, pProfile, pExtraParameters);
	  
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

  }
  

  //-------------------------------------
  /**
   * Price a List of items together in a context
   *
   * @param pPriceQuotes List of ItemPriceInfo objects representing the current price quotes for the items
   * @param pItems The items to price
   * @param pPricingModels A RepositoryItem representing a PricingModel
   * @param pProfile The user's profile
   * @param pOrder The Order object of which the List of items are a part, may be null
   * @param pExtraParameters A Map of extra parameters to be used in the pricing, may be null
   */
  public void priceItems(List pPriceQuotes, List pItems, RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, Order pOrder, Map pExtraParameters)
       throws PricingException
  {
    priceEachItem(pPriceQuotes, pItems, pPricingModel, pLocale, pProfile, pExtraParameters);
  }

  /**
   * This is a no-op implementation that returns false. Subclasses override this method to provide calculator specific handling. 
   */
  public boolean populateItemPriceSource(ItemPriceSource pItemPriceSource, Order pOrder, CommerceItem pCommerceItem, CommerceItem pParentCommerceItem, Map pExtraParameters)
  {
    return false;
  }

  
  /**
   * Determines if the price source should be consumed by the calculator. 
   * A false return value will cause the calculator to complete bypass its processing
   * and return normally. 
   * <p>
   * This implementation always returns false. Subclasses override this method to make
   * a determination based on the contents of the source. 
   * @param pItemPriceSource the source that was given to price the item. 
   * @return true if the source should be consumed by the calculator. false if the calculator should 
   * bypass its processing and return normally.  
   */
  public boolean shouldConsumeSource(ItemPriceSource pItemPriceSource)
  {
    return false;
  }
  
} // end of class


