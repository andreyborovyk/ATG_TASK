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
import atg.commerce.order.OrderManager;
import atg.commerce.order.Order;
import atg.commerce.order.RelationshipTypes;
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.service.perfmonitor.PerfStackMismatchException;

import java.util.List;

/**
 * A shipping calculator that determines the shipping price based on the summing the values of
 * a specific property of all the items in the shipping group. The service is configured through
 * the <code>ranges</code> property. With the given array of price range configurations (format:
 * <i>low</i>:<i>high</i>:<i>price</i>) the service parses the values into their double format
 * for use in calculating shipping costs.
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
 * <P>
 *
 * Set the <code>propertyName</code> property to the name of the property that you want to sum
 * across all items. (e.g. "weight" - which would then calculate the total weight of all the items in the order)
 * The property value will be extracted from the catalogRef of the CommerceItem (aka SKU) if the
 * <code>useCatalogRef</code> property is set to true, otherwise the product will be used as the source.
 *
 * @beaninfo
 *   description: A shipping calculator that determines the shipping price based on the summing the
 *                values of a specific property of all the items in the shipping group.
 *   attribute: functionalComponentCategory Pricing Calculators
 *   attribute: featureComponentCategory
 *
 * @author Bob Mason
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/PropertyRangeShippingCalculator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public
class PropertyRangeShippingCalculator
extends DoubleRangeShippingCalculator
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
    "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/PropertyRangeShippingCalculator.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  private static final String PERFORM_MONITOR_NAME="PropertyRangeShippingCalculator";

  //-------------------------------------
  // Member Variables
  //-------------------------------------

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // property: UseCatalogRef
  boolean mUseCatalogRef = true;

  /**
   * Sets property UseCatalogRef
   **/
  public void setUseCatalogRef(boolean pUseCatalogRef) {
    mUseCatalogRef = pUseCatalogRef;
  }

  /**
   * Returns property UseCatalogRef.
   * @return true if the property value should be extracted from the catalogRef (aka sku).
   * Otherwise extract the information from the product object
   **/
  public boolean isUseCatalogRef() {
    return mUseCatalogRef;
  }

  //-------------------------------------
  // property: PropertyName
  String mPropertyName;

  /**
   * Sets property PropertyName
   **/
  public void setPropertyName(String pPropertyName) {
    mPropertyName = pPropertyName;
  }

  /**
   * Returns property PropertyName
   * @beaninfo description: The name of the property that you want to add across all items
   **/
  public String getPropertyName() {
    return mPropertyName;
  }

  //---------------------------------------------------------------------------
  // property: OrderManager
  OrderManager mOrderManager;

  /**
   * Set the OrderManager property.
   */
  public void setOrderManager(OrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  /**
   * Return the OrderManager property.
   */
  public OrderManager getOrderManager() {
    return mOrderManager;
  }

  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instanceof PropertyRangeShippingCalculator
   */
  public PropertyRangeShippingCalculator() {
  }

  /**
   * Return the value which should be used as a comparison between the range values
   * @return the sum of all the Number values fetched from the configured property
   * @deprecated
   */
  protected double getRangeComparisonValue(ShippingGroup pShippingGroup)
    throws PricingException
  {
    return getRangeComparisonValue(null, pShippingGroup);
  }

  /**
   * Return the value which should be used as a comparison between the range values
   * @return the sum of all the Number values fetched from the configured property
   */
  protected double getRangeComparisonValue(Order pOrder,ShippingGroup pShippingGroup)
    throws PricingException
  {
    String perfName = "getRangeComparisonValue";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    boolean perfCancelled = false;

    try {

    double sum = 0.0;

    List relationships = pShippingGroup.getCommerceItemRelationships();
    if (relationships != null) {
      int num = relationships.size();
      for (int c=0; c<num; c++) {
        CommerceItemRelationship relationship = (CommerceItemRelationship)relationships.get(c);
        if (relationship != null) {
          CommerceItem item = relationship.getCommerceItem();
          if (item != null) {
            Object source = null;
            if (isUseCatalogRef())
              source = item.getAuxiliaryData().getCatalogRef();
            else
              source = item.getAuxiliaryData().getProductRef();
            if (source != null) {
              try {
                Object value = DynamicBeans.getPropertyValue(source, getPropertyName());
                if (value instanceof Number) {
                  // find out how many items to allocate from inventory
                  long quantity = 1;

                  switch(relationship.getRelationshipType())
                  {
                  case RelationshipTypes.SHIPPINGQUANTITY:
                    {

                      quantity = relationship.getQuantity();
                      break;
                    }
                  case RelationshipTypes.SHIPPINGQUANTITYREMAINING:
                    {
                      // calculate the quantity
                      quantity = getOrderManager().getShippingGroupManager().getRemainingQuantityForShippingGroup(relationship.getCommerceItem());
                      break;
                    }
                  } //switch
                  sum  += ((Number)value).doubleValue() * quantity;
                }
              }
              catch (PropertyNotFoundException exc) {
                continue;
              }
            }
          }
        }
      }
    }
    return sum;
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




