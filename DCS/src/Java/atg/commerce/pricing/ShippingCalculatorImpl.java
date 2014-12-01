/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.Order;
import atg.repository.RepositoryItem;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;

import java.util.*;
import java.text.MessageFormat;

/**
 * This abstract class is a starting point for some general functionality on calculating
 * pricing for shipping groups. The implementation of priceShippingGroup checks to make
 * sure there are items in the shipping group. If there are no items then we reset the
 * price quote back to zero. If there are items to price for shipping, then we confirm
 * through the <i>performPricing</i> method that the items in the group should be priced.
 * For example softgoods, like gift certificates, should not be priced for shipping. The 
 * developer who extends this class should implement the <i>getAmount</i> method which
 * will be used as the base shipping cost in this calculator. Finally the amount returned
 * is set into the ShippingPriceInfo. If the <code>addAmount</code> property is true, then
 * we take the amount returned and add it to the current ShippingPriceInfo.amount. This allows
 * surcharges to be tacked on.
 *
 * <P>
 *
 * The <code>shippingMethod</code> property should be set to the name of a particular delivery process.
 * For example: UPS Ground, UPS 2-day or UPS Next Day.
 * 
 * <P>
 *
 * If the <code>ignoreShippingMethod</code> property is true, then this calculator does not
 * expose a shipping method name (through getAvailableMethods). In addition this calculator will
 * always attempt to perform pricing. This option is available if the user is not given a choice
 * of different shipping methods.
 *
 * @author Bob Mason
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/ShippingCalculatorImpl.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public abstract
class ShippingCalculatorImpl
extends GenericService
implements ShippingPricingCalculator
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/ShippingCalculatorImpl.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------

  //-------------------------------------
  // Member Variables
  //-------------------------------------

  //-------------------------------------
  // Properties
  //-------------------------------------


  //-------------------------------------
  // property: PricingTools
  /** pricing tools to help in calculating prices */
  PricingTools mPricingTools;

  /**
   * pricing tools to help in calculating prices
   * @beaninfo description: pricing tools to help in calculating prices
   * @param pPricingTools new value to set
   */
  public void setPricingTools(PricingTools pPricingTools)
  {mPricingTools = pPricingTools;}

  /**
   * pricing tools to help in calculating prices
   * @beaninfo description: pricing tools to help in calculating prices
   * @return property PricingTools
   */
  public PricingTools getPricingTools()
  {return mPricingTools;}
  

  //-------------------------------------
  // property: ShippingMethod
  String mShippingMethod;

  /**
   * Sets property ShippingMethod
   **/
  public void setShippingMethod(String pShippingMethod) {
    mShippingMethod = pShippingMethod;
  }

  /**
   * Returns property ShippingMethod
   * @beaninfo description: The name of a particular delivery process
   **/
  public String getShippingMethod() {
    return mShippingMethod;
  }

  //-------------------------------------
  // property: IgnoreShippingMethod
  boolean mIgnoreShippingMethod = false;

  /**
   * Sets property IgnoreShippingMethod
   **/
  public void setIgnoreShippingMethod(boolean pIgnoreShippingMethod) {
    mIgnoreShippingMethod = pIgnoreShippingMethod;
  }

  /**
   * Returns property IgnoreShippingMethod
   * @beaninfo description: True if this calculator shouldn't expose the shipping method name.
   **/
  public boolean isIgnoreShippingMethod() {
    return mIgnoreShippingMethod;
  }

  //-------------------------------------
  // property: AddAmount
  boolean mAddAmount = false;

  /**
   * Sets property AddAmount
   **/
  public void setAddAmount(boolean pAddAmount) {
    mAddAmount = pAddAmount;
  }

  /**
   * Returns property AddAmount
   * @beaninfo description: True if this calculator should add the amount to the current amount in the price quote.
   **/
  public boolean isAddAmount() {
    return mAddAmount;
  }
  

  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instanceof ShippingCalculatorImpl
   */
  public ShippingCalculatorImpl() {
  }

  /**
   * Price a shipment within a context
   *
   * @param pOrder the Order in the context of which pShipment is being priced.
   * @param pPriceQuote ShippingPriceInfo representing the price quote for the shipment
   * @param pShippingGroup The shipment to price
   * @param pPricingModels A Collection of RepositoryItems representing PricingModels
   * @param pProfile The user's profile
   * @param pExtraParameters A Map of extra parameters to be used in the pricing, may be null
   */
  public void priceShippingGroup(Order pOrder, ShippingPriceInfo pPriceQuote, ShippingGroup pShippingGroup, 
                                 RepositoryItem pPricingModel, Locale pLocale, RepositoryItem pProfile, 
                                 Map pExtraParameters)
       throws PricingException
  {
    if (! haveItemsToShip(pShippingGroup)) {
      resetShippingPriceInfo(pPriceQuote);
    }
    else {
      boolean doPricing = performPricing(pShippingGroup);
      if (isLoggingDebug())
        logDebug("performPricing=" + doPricing);    
      if (doPricing) {
        double amount = getAmount(pOrder,pPriceQuote, pShippingGroup, pPricingModel, pLocale, pProfile, pExtraParameters);
        priceShippingPriceInfo(amount, pPriceQuote);
      }
    }
  }

  /**
   * Return the amount which should be used for the pricing of this shipping group
   * The default implementation just returns the subtotal of the shipping group,
   * or zero if the subtotal cannot be found
   */
  protected double getAmount(Order pOrder,ShippingPriceInfo pPriceQuote, ShippingGroup pShippingGroup, 
                             RepositoryItem pPricingModel, Locale pLocale, 
                             RepositoryItem pProfile, Map pExtraParameters) throws PricingException 
  {
    if(pOrder == null)
      return 0.0;

    if(pShippingGroup == null)
      return 0.0;

    OrderPriceInfo orderPrice = pOrder.getPriceInfo();
    if(orderPrice == null)
      return 0.0;

    Map subtotals = orderPrice.getShippingItemsSubtotalPriceInfos();
    if(subtotals == null)
      return 0.0;

    String sgId = pShippingGroup.getId();
    OrderPriceInfo subtotalPrice = (OrderPriceInfo) subtotals.get(sgId);

    if(subtotalPrice == null)
      return 0.0;

    return subtotalPrice.getAmount();
  }

  /**
   * Return the amount which should be used for the pricing of this shipping group
   * @deprecated
   */
  protected double getAmount(ShippingPriceInfo pPriceQuote, ShippingGroup pShippingGroup, 
                             RepositoryItem pPricingModel, Locale pLocale, 
                             RepositoryItem pProfile, Map pExtraParameters) throws PricingException {
    return getAmount(null,pPriceQuote,pShippingGroup,pPricingModel,pLocale,pProfile,pExtraParameters);
  }

  /**
   * Reset the price quote back to zero
   */
  protected void resetShippingPriceInfo(ShippingPriceInfo pPriceQuote) {
    if (isLoggingDebug())
      logDebug("no items to ship so reset shipping costs to 0.0");
    pPriceQuote.setAmount(0.0);
    pPriceQuote.setRawShipping(0.0);
  
    // reset the adjustments list
    List adjustments = pPriceQuote.getAdjustments();
    
    if (adjustments.size() > 0) {
      adjustments.clear();
    }
  }
  
  /**
   * Set the amount that is given into the price quote. This method looks at the <code>addAmount</code> 
   * property, and if it is true, then we add the base amount to the current amount.
   */
  protected void priceShippingPriceInfo(double pAmount, ShippingPriceInfo pPriceQuote) {
    double amount = pAmount;
    if (isAddAmount())
      amount = pPriceQuote.getAmount() + amount;
    if (isLoggingDebug())
      logDebug("shipping amount=" + amount);
    
    if (isLoggingDebug()) {
      logDebug("rounding shipping amount.");
    }
    
    amount = getPricingTools().round(amount);

    if (isLoggingDebug()) {
      logDebug("rounded shipping amount to: " + amount);
    }

    double oldAmount = pPriceQuote.getAmount();
    pPriceQuote.setAmount(amount);
    pPriceQuote.setRawShipping(amount);

    List adjustments = pPriceQuote.getAdjustments();
    
    // maintain the adjustments list
    double adjustAmount = pPriceQuote.getAmount() - oldAmount;
    pPriceQuote.getAdjustments().add(new PricingAdjustment(Constants.SHIPPING_PRICE_ADJUSTMENT_DESCRIPTION,
							   null,
							   getPricingTools().round(adjustAmount),
							   1));

  }

  /**
   * Return true if there are any items in the shipping group to price.
   * If there are no items then the shipping amount will be reset to 0.
   */
  protected boolean haveItemsToShip(ShippingGroup pShippingGroup) {
    if (pShippingGroup != null) {
      int num = pShippingGroup.getCommerceItemRelationshipCount();
      return (num > 0);
    }
    return false;   
  }
  
  /**
   * Return true if we should price the shipping group
   */
  protected boolean performPricing(ShippingGroup pShippingGroup) {
    if (isIgnoreShippingMethod())
      return true;
    else if (pShippingGroup instanceof HardgoodShippingGroup) {
      HardgoodShippingGroup sg = (HardgoodShippingGroup)pShippingGroup;
      String shippingMethod = sg.getShippingMethod();
      if (isLoggingDebug())
        logDebug("this.shippingMethod=" + getShippingMethod() + "; ShippingGroup.shippingMethod=" + shippingMethod);
      return ((shippingMethod != null) && (shippingMethod.equals(getShippingMethod())));
    }

    return false;
  }

  //-------------------------------------
  /**
   * Get shipping methods available to deliver the shipping group.  Always add ourselves to the list of methods.
   * @param pMethods List of Strings representing shipping methods
   * @param pShippingGroup The shipping group to deliver
   * @param pPricingModels A Collection of RepositoryItems representing PricingModels
   * @param pProfile The user's profile
   * @param pExtraParameters A Map of extra parameters to be used in the pricing, may be null
   */
  public void getAvailableMethods(List pMethods, ShippingGroup pShippingGroup, 
                                  RepositoryItem pPricingModel, Locale pLocale, 
                                  RepositoryItem pProfile, Map pExtraParameters)
       throws PricingException
  {
    if ((! isIgnoreShippingMethod()) && (getShippingMethod() != null) && (pMethods != null))
      pMethods.add(getShippingMethod());
  }

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

    super.doStartService();

  } // end doStartService

} // end of class
