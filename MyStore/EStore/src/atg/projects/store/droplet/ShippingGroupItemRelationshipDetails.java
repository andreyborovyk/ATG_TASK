/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2010 Art Technology Group, Inc.
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

package atg.projects.store.droplet;

import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;

import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.UnitPriceBean;

import atg.nucleus.naming.ParameterName;

import atg.projects.store.logging.LogUtils;
import atg.projects.store.pricing.StorePricingTools;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;

import java.util.*;

import javax.servlet.ServletException;


/**
 * This droplet generates UnitPriceBeans for each item in each shipping group of an order.
 *
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/droplet/ShippingGroupItemRelationshipDetails.java#3 $$Change: 635816 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class ShippingGroupItemRelationshipDetails extends DynamoServlet {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/droplet/ShippingGroupItemRelationshipDetails.java#3 $$Change: 635816 $";

  /**
   * Order parameter name.
   */
  public static final ParameterName ORDER = ParameterName.getParameterName("order");

  /**
   * Output shipping group parameter name.
   */
  public static final ParameterName OUTPUT_SHIPPINGGROUP = ParameterName.getParameterName("outputShippingGroup");

  /**
   * Output shipping group relationship parameter name.
   */
  public static final ParameterName OUTPUT_SHIPPINGGROUP_REL = ParameterName.getParameterName(
      "outputShippingGroupRelationship");

  /**
   * Shipping group parameter name.
   */
  public static final ParameterName SHIPPINGGROUP = ParameterName.getParameterName("shippingGroup");

  /**
   * Relationship total parameter name.
   */
  public static final ParameterName RELATIONSHIP_TOTAL = ParameterName.getParameterName("relationshipTotal");

  /**
   * Commerce item relationship parameter name.
   */
  public static final ParameterName COMMERCEITEMRELATIONSHIP = ParameterName.getParameterName(
      "commerceItemRelationShip");

  /**
   * Relationship unit price beans parameter name.
   */
  public static final ParameterName RELATIONSHIPUNITPRICEBEANS = ParameterName.getParameterName("relUnitPriceBeans");

  /**
   * Pricing tools.
   */
  protected PricingTools mPricingTools;

  /**
   * @return the pricingTools.
   */
  public PricingTools getPricingTools() {
    return mPricingTools;
  }

  /**
   * @param pPricingTools - the pricingTools to set.
   */
  public void setPricingTools(PricingTools pPricingTools) {
    mPricingTools = pPricingTools;
  }

  /**
   * See API definition.
   * @param pRequest - http request
   * @param pResponse - http response
   * @throws ServletException if an error occurs
   * @throws IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    Object orderparam = pRequest.getObjectParameter(ORDER);

    if ((orderparam != null) && (orderparam instanceof atg.commerce.order.Order)) {
      Order order = (Order) orderparam;
      StorePricingTools pTools = (StorePricingTools) getPricingTools();

      //build the unitprice beans for each item in the order. these are used to create the
      //new unitpricebeans for each shipping group relationship 
      Map unitpricebeans = pTools.generateUnitPriceBeans(order);

      List itemunitpricebeans;

      //for each hardgoodshipping group
      Collection shippingGroups = order.getShippingGroups();
      Iterator shippinggrouperator = shippingGroups.iterator();

      while (shippinggrouperator.hasNext()) {
        ShippingGroup sg = (ShippingGroup) shippinggrouperator.next();

        if ((sg.getCommerceItemRelationships() == null) || (sg.getCommerceItemRelationships().size() == 0)) {
          continue;
        }

        pRequest.setParameter(SHIPPINGGROUP.getName(), sg);
        pRequest.serviceLocalParameter(OUTPUT_SHIPPINGGROUP, pRequest, pResponse);

        //for each relationship
        Collection relationships = sg.getCommerceItemRelationships();
        Iterator relationshiperator = relationships.iterator();

        while (relationshiperator.hasNext()) {
          CommerceItemRelationship cirel = (CommerceItemRelationship) relationshiperator.next();
          pRequest.setParameter(COMMERCEITEMRELATIONSHIP.getName(), cirel);
          itemunitpricebeans = (List) unitpricebeans.get(cirel.getCommerceItem().getId());

          List relUnitPriceBeans = generateUnitPriceBeansForRelationship(cirel, itemunitpricebeans);
          double relAmount = calculateTotalForRelationshipBeans(relUnitPriceBeans);

          pRequest.setParameter(RELATIONSHIP_TOTAL.getName(), new Double(relAmount));
          pRequest.setParameter(RELATIONSHIPUNITPRICEBEANS.getName(), relUnitPriceBeans);
          pRequest.serviceLocalParameter(OUTPUT_SHIPPINGGROUP_REL, pRequest, pResponse);
        }
      }
    } else {
      if (isLoggingError()) {
        String err = "invalid parameter passed for parameter 'order' " + pRequest.getRequestURI();
        logError(LogUtils.formatMajor(err));
      }
    }
  }

  /**
   * Calculates total for relationship beans.
   *
   * @param pRelUnitPriceBeans - price beans
   *
   * @return calculated result
   */
  protected double calculateTotalForRelationshipBeans(List pRelUnitPriceBeans) {
    double amount = 0;
    Iterator unitpricerator = pRelUnitPriceBeans.iterator();

    while (unitpricerator.hasNext()) {
      UnitPriceBean upb = (UnitPriceBean) unitpricerator.next();
      amount += (upb.getUnitPrice() * upb.getQuantity());
    }

    return amount;
  }

  /**
   * For a given commerce item relationship, generate the shipping item infos using the UnitPriceBeans provided.
   * @param pCommerceItemRelationship - commerce item relationship
   * @param pUnitPriceBeans - unit price beans
   * @return List of UnitPriceBeans for the relationship
   */
  public List generateUnitPriceBeansForRelationship(CommerceItemRelationship pCommerceItemRelationship,
    List pUnitPriceBeans) {
    List relUnitPriceBeans = new ArrayList();
    UnitPriceBean relUnitPriceBean = null;

    //the total quantity of this item to be accounted for. 
    long relQuantity = pCommerceItemRelationship.getQuantity();

    //iterator ver the unit price beans until we fill the quantity of the relationship
    Iterator unitpricebeanerator = pUnitPriceBeans.iterator();

    while (unitpricebeanerator.hasNext() && (relQuantity > 0)) {
      //spawn a new UnitPriceBean
      relUnitPriceBean = new UnitPriceBean();

      //get the unitpricebeans for the item in question.
      UnitPriceBean unitpricebean = (UnitPriceBean) unitpricebeanerator.next();

      long unitpricebeanquantity = unitpricebean.getQuantity();

      //if quantity is zero, skip it. 
      if (unitpricebeanquantity == 0) {
        continue;
      }

      relUnitPriceBean.setPricingModels(unitpricebean.getPricingModels());

      //unit price bean covers the entire remaining quantity of the relationship
      if (unitpricebeanquantity >= relQuantity) {
        relUnitPriceBean.setQuantity(relQuantity);

        //decrement the quantity from the orig unit price bean so it won't get accounted for again later
        unitpricebean.setQuantity(unitpricebean.getQuantity() - relQuantity);
        relUnitPriceBean.setUnitPrice(unitpricebean.getUnitPrice());
        relQuantity = 0;
      } else {
        //set the quantity and unit price in the relationship unit price bean from the item unit price bean
        relUnitPriceBean.setQuantity(unitpricebean.getQuantity());
        relUnitPriceBean.setUnitPrice(unitpricebean.getUnitPrice());

        relQuantity -= unitpricebean.getQuantity();
        unitpricebean.setQuantity(0);
      }

      relUnitPriceBeans.add(relUnitPriceBean);
    }

    return relUnitPriceBeans;
  }
}
