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

import atg.commerce.order.ShippingGroup;
import atg.repository.RepositoryItem;
import atg.commerce.order.Order;

import java.util.*;


/**
 * A shipping calculator that sets the shipping amount to a fixed price.
 * <P>
 * If the property <code>addAmount</code> is true then instead of setting the price quote amount to the value
 * of the <code>amount</code> property, the calculator adds the amount to the current amount in the
 * price quote. This can be used to configure a "surcharge" calculator, which increases the shipping
 * price.
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
 * @beaninfo
 *   description: A shipping calculator that sets the shipping amount to a fixed price.
 *   attribute: functionalComponentCategory Pricing Calculators
 *   attribute: featureComponentCategory
 *
 * @author Bob Mason
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/FixedPriceShippingCalculator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public
class FixedPriceShippingCalculator
extends ShippingCalculatorImpl
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/FixedPriceShippingCalculator.java#2 $$Change: 651448 $";

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
  // property: Amount
  /** The fixed price that every ShippingGroup processes by this calculator receives */
  double mAmount;

  /**
   * The fixed price that every ShippingGroup processes by this calculator receives
   * @beaninfo description: The fixed price that every ShippingGroup processes by this calculator receives
   * @param pAmount new value to set
   */
  public void setAmount(double pAmount)
  {mAmount = pAmount;}

  /**
   * The fixed price that every ShippingGroup processes by this calculator receives
   * @beaninfo description: The fixed price that every ShippingGroup processes by this calculator receives
   * @return property Amount
   */
  public double getAmount()
  {return mAmount;}


  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instanceof FixedPriceShippingCalculator
   */
  public FixedPriceShippingCalculator() {
  }

    /**
     * Returns the amount which should be used as the price for this shipping group
     *
     * @param pOrder the Order
     * @param pPriceQuote the price of the input shipping group
     * @param pShippingGroup the shipping group for which an amount is needed
     * @param pPricingModel a discount which could affect the shipping group's price
     * @param pLocale the locale in which the price is calculated
     * @param pProfile the profile of the person for whom the amount in being generated.
     * @param pExtraParameters any extra parameters that might affect the amount calculation
     * @return the amount for pricing the input pShippingGroup
     * @exception PricingException if there is a problem getting the amount (price) for the input shipping group
     */
  protected double getAmount(Order pOrder,ShippingPriceInfo pPriceQuote, ShippingGroup pShippingGroup,
                             RepositoryItem pPricingModel, Locale pLocale,
                             RepositoryItem pProfile, Map pExtraParameters)
       throws PricingException
  {
    return getAmount();
  }

} // end of class
