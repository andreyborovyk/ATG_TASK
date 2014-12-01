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
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.CommerceItem;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.service.perfmonitor.PerfStackMismatchException;

import java.util.*;
import atg.commerce.order.Order;
import java.util.Map;
import atg.commerce.order.OrderManager;
import atg.commerce.CommerceException;
import atg.repository.RepositoryException;

/**
 * A shipping calculator that determines the shipping price based on the subtotal of all the items
 * in the shipping group. The service is configured through the <code>ranges</code> property.
 * With the given array of price range configurations (format: <i>low</i>:<i>high</i>:<i>price</i>)
 * the service parses the values into their double format for use in calculating shipping costs.
 * <P>
 * For example:
 * <PRE>
 * ranges=00.00:15.99:4.50,\
 *        16.00:30.99:6.00,\
 *        31.00:40.99:7.25,\
 *        41.00:<code>MAX_VALUE</code>:10.00
 * </PRE>
 * <strong>Note: the keyword <code>MAX_VALUE</code> indicates a top end</strong>
 * <P>
 *
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
 *   description: A shipping calculator that determines the shipping price based on the subtotal of
 *                all the items in the shipping group.
 *   attribute: functionalComponentCategory Pricing Calculators
 *   attribute: featureComponentCategory
 *
 * @author Bob Mason
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/PriceRangeShippingCalculator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public
  class PriceRangeShippingCalculator
  extends DoubleRangeShippingCalculator
  {
    //-------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
      "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/PriceRangeShippingCalculator.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  private static final String PERFORM_MONITOR_NAME="PriceRangeShippingCalculator";

  //-------------------------------------
  // Member Variables
  //-------------------------------------

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instanceof PriceRangeShippingCalculator
   */
    public PriceRangeShippingCalculator() {
    }

    /**
   * Return the value which should be used as a comparison between the range values
   * @return the subtotal for all the items in the shipping group
   * @deprecated
   */
    protected double getRangeComparisonValue(ShippingGroup pShippingGroup)
      throws PricingException
    {
      return getRangeComparisonValue(null, pShippingGroup);
    }

    /**
   * Return the value which should be used as a comparison between the range values
   * @return the subtotal for all the items in the shipping group
   */
    protected double getRangeComparisonValue(Order pOrder,ShippingGroup pShippingGroup)
      throws PricingException
    {
      return getSubTotal(pOrder,pShippingGroup);
    }

  /**
   * Return the sub total of all the items in the shipping group.
   * If <code>order</code> is null, then the subtotal is calculated
   * by summing the ItemPriceInfo values for the items in the shipping group.
   * (Warning: This will not take order level discounts into consideration)
   * If <code>order</code> is not null, then it is used to get the 
   * given shipping group's subtotal from the order's OrderPriceInfo and
   * used as the sub total.  (This DOES take order level discounts into
   * consideration.)
   */
    protected double getSubTotal(Order pOrder,ShippingGroup pShippingGroup) {
      String perfName = "getSubTotal";
      PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
      boolean perfCancelled = false;

      try {

        double subTotal = 0.0;

        // if we have any problems getting the sub total from the order
        // then just calculate it
        boolean calculate = false;

        if(pOrder == null) {
          calculate = true;
        }
        else {
          
          OrderPriceInfo orderPrice = pOrder.getPriceInfo();

          if(orderPrice == null) {
            calculate = true;
          }
          else {
            
            Map shipSubtotals = orderPrice.getShippingItemsSubtotalPriceInfos();
            if(shipSubtotals == null) {
              calculate = true;
            }
            else {
                            
              OrderPriceInfo shipSub = (OrderPriceInfo) shipSubtotals.get(pShippingGroup.getId());
              if(shipSub == null) {
                calculate = true;
              }
              else {
                subTotal = shipSub.getAmount();
                
              }
            }
          }
        }

        if(calculate) {
          
          List relationships = pShippingGroup.getCommerceItemRelationships();
          if (relationships != null) {
            int num = relationships.size();
            for (int c=0; c<num; c++) {
              CommerceItemRelationship relationship = (CommerceItemRelationship)relationships.get(c);
              if (relationship != null) {
                CommerceItem item = relationship.getCommerceItem();
                if (item != null) {
                  ItemPriceInfo info = item.getPriceInfo();
                  if (info != null)
                    subTotal += info.getAmount();
                }
              }
            }
          }
        }

        return subTotal;
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

  } // end of class
