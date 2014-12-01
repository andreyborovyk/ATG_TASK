/*<ATGCOPYRIGHT>
 * Copyright (C) 2000-2011 Art Technology Group, Inc.
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

import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupImpl;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.RelationshipTypes;
import atg.commerce.order.OrderManager;
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;

import java.util.*;

/**
 * This calculator will calculate shipping costs based upon the total weight of the items in a
 * shipping group.  So, the total weight of a shipping group will be calculated, then the range of weight
 * values will be consulted to determine the cost.
 *
 * If the property <code>addAmount</code> is true then instead of setting the price
 * quote amount to the value of the <code>amount</code> property, the calculator adds
 * the amount to the current amount in the
 * price quote. This can be used to configure a "surcharge" calculator, which increases
 * the shipping price.
 *
 * <P>
 *
 * The <code>shippingMethod</code> property should be set to the name of a particular
 * delivery process.
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
 *   description: A calculator that determines shipping costs based on the weight of items in a shipping group
 *   attribute: functionalComponentCategory Pricing Calculators
 *   attribute: featureComponentCategory
 *
 * @see atg.commerce.pricing.DoubleRangeShippingCalculator
 * @author Ashley J. Streb
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/WeightRangeShippingCalculator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class WeightRangeShippingCalculator
  extends DoubleRangeShippingCalculator
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/WeightRangeShippingCalculator.java#2 $$Change: 651448 $";


  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //---------------------------------------------------------------------------
  // property: weightProperty

  /** Property in CatalogRef object that contains the weight */
  String mWeightProperty;

  /**
   * Set the weightProperty property.
   * @beaninfo 
   *   description: Set the weightProperty property.
   */
  public void setWeightProperty(String pWeightProperty) {
    mWeightProperty = pWeightProperty;
  }

  /**
   * Return the weightProperty property.
   * @beaninfo 
   *   description: Return the weightProperty property.
   */
  public String getWeightProperty() {
    return mWeightProperty;
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



  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  //--------------------------------------------------
  // Methods
  //--------------------------------------------------

  /**
   * Get the value that will be used in the range comparison.  Make a
   * call to getWeightTotal to actually compute the total.
   *
   * @param pShippingGroup the shipping group
   * @return the total weight
   * @exception PricingException if an error occurs
   * @deprecated
   */
  protected double getRangeComparisonValue(ShippingGroup pShippingGroup)
    throws PricingException
  {
    return getWeightTotal(pShippingGroup);
  }

  /**
   * Get the value that will be used in the range comparison.  Make a
   * call to getWeightTotal to actually compute the total.
   *
   * @param pShippingGroup the shipping group
   * @return the total weight
   * @exception PricingException if an error occurs
   */
  protected double getRangeComparisonValue(Order pOrder,ShippingGroup pShippingGroup)
    throws PricingException
  {
    return getWeightTotal(pShippingGroup);
  }

  /**
   * Calculate the total weight of the items being shipped to the
   * specified ShippingGroup.  Iterate through all the items being
   * shipped and extract out the number of items being shipped and
   * each items weight.  This it added to the running total of
   * weight.
   *
   * <P>
   *
   * <code>getWeight</code> is called to extract the weight for
   * each item.
   *
   * @param pShippingGroup a value of type 'ShippingGroup'
   * @return a value of type 'double'
   */
  // possible that quantity is all remaining?
  // what happens if weight is null
  protected double getWeightTotal(ShippingGroup pShippingGroup)
    throws PricingException
  {
    double weightTotal = 0.0;
    int listSize;
    double itemQuantity, itemWeight;
    ShippingGroupCommerceItemRelationship currentCommerceItemRelationship;

    List CIRelationships = ((ShippingGroupImpl)pShippingGroup).getCommerceItemRelationships();
    if (CIRelationships != null && CIRelationships.size() > 0) {
      listSize = CIRelationships.size();
      for (int i=0; i<listSize; i++) {
        currentCommerceItemRelationship = ((ShippingGroupCommerceItemRelationship)CIRelationships.get(i));
        itemQuantity = getQuantity(currentCommerceItemRelationship);
        itemWeight   = getWeight(currentCommerceItemRelationship.getCommerceItem());
        weightTotal += (itemQuantity * itemWeight);
      }
    }
    return weightTotal;
  }

  /**
   * Responsible for getting the weight associated with an instance of a commerce item.
   * This is performed by getting the CatalogRef object from auxiliaryData object and
   * extracting the property named by mWeightProperty out of it.  This is returned.
   *
   * <P>
   *
   * This method can be overriden if the weight is to be obtained from another location.
   *
   * @param pCommerceItem the commerce item whose weight is to be extracted
   * @return the weight of a single item
   */
  // catch exception
  protected double getWeight(CommerceItem pCommerceItem)
    throws PricingException
  {
    double weight = 0.0;
    Object weightObject;

    if (pCommerceItem != null) {
      try {
        weightObject = DynamicBeans.getPropertyValue(pCommerceItem.getAuxiliaryData().getCatalogRef(),
                                                     mWeightProperty);
        if (weightObject != null)
          weight = ((Number) weightObject).doubleValue();
      } catch (PropertyNotFoundException pnfe) {
        weight = 0;
      } catch (ClassCastException cce) {
        throw new PricingException("The weight property of the object is not a Number, unable to get the weight.", cce);
      }
    }
    return weight;
  }

  /**
   * Get the quantity of a particular item that are being shipped by this
   * this shipping group.  There are two cases that are accounted for.  Either
   * the Relationship is one of RelationshipTypes.SHIPPINGQUANTITY or its one
   * of RelationshipTypes.SHIPPINGQUANTITYREMAINING.  The quantity is extracted
   * from the correct place and returned as a double.
   *
   * @param pCommerceItemRelationship a value of type 'CommerceItemRelationship'
   * @return a value of type 'double'
   */
  protected double getQuantity(ShippingGroupCommerceItemRelationship pCommerceItemRelationship) {
    double quantity = 0.0;

    if (pCommerceItemRelationship != null) {
      if (pCommerceItemRelationship.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITY) {
        quantity = pCommerceItemRelationship.getQuantity();
      } else {
        quantity = getOrderManager().getShippingGroupManager().getRemainingQuantityForShippingGroup(pCommerceItemRelationship.getCommerceItem());
      }
    }
    return quantity;
  }


}   // end of class
