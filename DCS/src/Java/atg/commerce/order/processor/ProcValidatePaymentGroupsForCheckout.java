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

package atg.commerce.order.processor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupCommerceItemRelationship;
import atg.commerce.order.PaymentGroupOrderRelationship;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.RelationshipTypes;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

/**
 * This processor validates PaymentGroups before checking an Order out. The two main
 * things which are checked for are that all CommerceItems, shipping costs, and tax in
 * the Order are assigned to PaymentGroups and that all the required fields in all the
 * PaymentGroups, regardless of type, are not null or empty String.
 *
 * The first check, that all CommerceItems, shipping costs, and tax are assigned to
 * PaymentGroups, first checks to see if there is only one PaymentGroup and it has no

 * Relationships. If so, then all CommerceItems, shipping costs, and tax are implicitly
 * assigned to that PaymentGroup. If there is more than one PaymentGroup or the PaymentGroup
 * has at least one Relationship, then all CommerceItems, ShippingGroups, and the Order must
 * be assigned to one or more PaymentGroups.
 *
 * The second check, that required fields are provided, iterates over all the PaymentGroups
 * in the order and invokes a separate pipeline chain for each payment group.  The first
 * processor in that chain is responsible for examining the type of the payment group and
 * dispatching to an appropriate validation processor or processor chain that knows which
 * fields are required for each payment method.
 *
 * @author Manny Parasirakis, Matt Landau
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidatePaymentGroupsForCheckout.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcValidatePaymentGroupsForCheckout extends ApplicationLoggingImpl implements PipelineProcessor {  
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidatePaymentGroupsForCheckout.java#2 $$Change: 651448 $";

  static final String RESOURCE_NAME = "atg.commerce.order.OrderResources";
  static final String USER_MSGS_RES_NAME = "atg.commerce.order.UserMessages";

  /** Resource Bundle **/
  private static ResourceBundle sResourceBundle =
      LayeredResourceBundle.getBundle(RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  private static ResourceBundle sUserResourceBundle =
    LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, Locale.getDefault());

  //---------------------------------------------------------------------------
  // property: validatePaymentGroupChainName
  //---------------------------------------------------------------------------

  String mValidatePaymentGroupChain = "validatePaymentGroup";

  /**
   * Set the name of the pipeline chain to run to validate each individual
   * payment group.
   **/
  
  public void setValidatePaymentGroupChain(String pValidatePaymentGroupChain) {
    mValidatePaymentGroupChain = pValidatePaymentGroupChain;
  }

  /**
   * Get the name of the pipeline chain to run to validate each individual
   * payment group.  The default chain name is "validatePaymentGroup".
   **/
  
  public String getValidatePaymentGroupChain() {
    return mValidatePaymentGroupChain;
  }

  //---------------------------------------------------------------------------

  /**
   * Returns the valid return codes.  This processor always returns
   * a status of 1 indicating successful completion.
   **/

  private static final int SUCCESS = 1;

  protected int[] mRetCodes = { SUCCESS };
  
  public int[] getRetCodes()
  {
    return mRetCodes;
  } 

  //---------------------------------------------------------------------------

  String mLoggingIdentifier = "ProcValidatePaymentGroupsForCheckout";

  /** Sets property LoggingIdentifier. **/
  
  public void setLoggingIdentifier(String pLoggingIdentifier) {
    mLoggingIdentifier = pLoggingIdentifier;
  }

  /** Returns property LoggingIdentifier. **/
  
  public String getLoggingIdentifier() {
    return mLoggingIdentifier;
  }

  //-----------------------------------------------
  /**
   * This method performs two checks on all the PaymentGroups in the given Order.
   * The first check is to see if all the CommerceItems, shipping costs, and tax
   * in the Order are assigned to PaymentGroups. The second check is to determine
   * if all the required properties are not null or empty String. The properties
   * which are checked are as follows:
   *
   * This method requires that an Order, an OrderManager, and optionally a Locale
   * object be supplied in pParam in a HashMap. Use the PipelineConstants class
   * static members to key the objects in the HashMap.
   *
   * @param pParam
   *    a HashMap which must contain an Order, an OrderManager, and optionally
   *    a Locale object
   * @param pResult
   *    a PipelineResult object which stores any information which must
   *    be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   **/
  
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    OrderManager om = (OrderManager)map.get(PipelineConstants.ORDERMANAGER);
    Locale locale = (Locale) map.get(PipelineConstants.LOCALE);
    ResourceBundle resourceBundle;

    if (locale == null)
      resourceBundle = sUserResourceBundle;
    else
      resourceBundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, locale);

    if (order == null)
      throw new InvalidParameterException(
        ResourceUtils.getMsgResource("InvalidOrderParameter", RESOURCE_NAME, sResourceBundle));

    PaymentGroupCommerceItemRelationship rel;
    CommerceItem item;
    PaymentGroup pg;
    ArrayList badItems = new ArrayList(7);
    Iterator iter, iter2;
    long relTotalQty;
    double relTotalAmt, itemAmt, orderAmountAmount;
//  double relTotalPct;
    int relType;
    boolean accountedFor = false;
    boolean detailCheck = true;

    // the special case, if there is only one paymentGroup that has no Relationships,
    // then all the items, tax, and shipping costs implicitly belong to that paymentGroup
    if (order.getPaymentGroupCount() == 1) {
      pg = (PaymentGroup) order.getPaymentGroups().get(0);
      if (pg.getCommerceItemRelationshipCount() == 0 &&
          pg.getShippingGroupRelationshipCount() == 0 &&
          pg.getOrderRelationshipCount() == 0) {
          detailCheck = false;
      }
    }

    // if there is a PaymentGroupOrderRelationship then check to see if it covers
    // the amount of the entire order for payment
    if (detailCheck && order.getPaymentGroupRelationshipCount() > 0) {
      PaymentGroupOrderRelationship orel;
      orderAmountAmount = 0.0;
      for (iter = order.getPaymentGroupRelationships().iterator(); iter.hasNext(); ) {
        orel = (PaymentGroupOrderRelationship) iter.next();
        switch (orel.getRelationshipType()) {
          case RelationshipTypes.ORDERAMOUNTREMAINING:
            detailCheck = false;
          break;

          case RelationshipTypes.ORDERAMOUNT:
            orderAmountAmount += orel.getAmount();
            orderAmountAmount = round(orderAmountAmount, 3);
            if (orderAmountAmount >= order.getPriceInfo().getAmount())
              detailCheck = false;
          break;
        }

        if (detailCheck == false)
          break;
      }
    }

    if (detailCheck)
    {
      // the standard case, if any Relationships exist in any paymentGroups then we must check to see
      // if all the items are accounted for
      iter = order.getCommerceItems().iterator();
      while (iter.hasNext()) {
        item = (CommerceItem) iter.next();
        accountedFor = false;
        relTotalQty = 0;
        relTotalAmt = 0.0;
  //    relTotalPct = 0.0;
        itemAmt = item.getPriceInfo().getAmount();

        iter2 = item.getPaymentGroupRelationships().iterator();
        while (iter2.hasNext()) {
          rel = (PaymentGroupCommerceItemRelationship) iter2.next();
          relType = rel.getRelationshipType();

          switch (relType) {
            case RelationshipTypes.PAYMENTAMOUNTREMAINING:
              // if this is a remaining relationship then this item is accounted for
              accountedFor = true;
            break;
            case RelationshipTypes.PAYMENTAMOUNT:
              // if this is an amount relationship then add the rel's amount to the counter and
              // check to see if the entire amount is accounted for
              relTotalAmt += rel.getAmount();
              relTotalAmt = round(relTotalAmt, 3);
              if (relTotalAmt >= itemAmt)
                accountedFor = true;
            break;
  //          case RelationshipTypes.PAYMENTPERCENTAGE:
              // if this is a percent relationship then add the rel's pct to the counter and
              // check to see if the entire pct is accounted for
  //            relTotalPct += rel.getPercentage();
  //            if (relTotalPct >= 100.0)
  //              accountedFor = true;
  //          break;
  //          case RelationshipTypes.PAYMENTQUANTITY:
              // if this is a quantity relationship then add the rel's qty to the counter and
              // check to see if the entire qty is accounted for
  //            relTotalQty += rel.getQuantity();
  //            if (relTotalQty >= item.getQuantity())
  //              accountedFor = true;
  //          break;
          } // switch

          if (accountedFor)
            break;
        } // while

        if (accountedFor == false) {
	  if (item.getAuxiliaryData() != null &&
	      item.getAuxiliaryData().getCatalogRef() != null) {
	    RepositoryItem sku = (RepositoryItem)
	      item.getAuxiliaryData().getCatalogRef();  
	    String displayName = (String) sku.getPropertyValue("displayName");
	    if (displayName != null)
	      badItems.add(displayName);
	    else
	      badItems.add(item.getId());
	  } else {
	    badItems.add(item.getId());
	  }
	}
      } // while

      if (badItems.size() > 0) {
        StringBuffer arg = new StringBuffer();
        boolean first = true;
        for (iter = badItems.iterator(); iter.hasNext(); ) {
          if (first)
            first = false;
          else
            arg.append(", ");
          arg.append((String) iter.next());
        }
        String msg = resourceBundle.getString("PayInfoNotGivenForAllItems");
        pResult.addError(PipelineConstants.VALIDATEPAYMENTGROUPSFAILED, MessageFormat.format(msg, arg.toString()));
      }
    }

    // This section iterates over all payment groups and invokes a pipeline
    // chain to validate each payment group in turn.

    Iterator paymentGroups = order.getPaymentGroups().iterator();
    ValidatePaymentGroupPipelineArgs param = new ValidatePaymentGroupPipelineArgs();
    PipelineResult newResult;
    
    param.setOrder(order);
    param.setOrderManager(om);
    if (locale != null)
      param.setLocale(locale);

    while (paymentGroups.hasNext())
    {
      pg = (PaymentGroup) paymentGroups.next();

      // Skip empty payment groups

      if(!om.getPaymentGroupManager().isPaymentGroupUsed(order, pg))
        continue;

      param.setPaymentGroup(pg);

      newResult = om.getPipelineManager().runProcess(getValidatePaymentGroupChain(), param);
      if (newResult.hasErrors())
      {
        pResult.copyInto(newResult);
        return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
      }
    }
    return SUCCESS;
  }


  //---------------------------------------------------------------------------

  /**
   * This method adds an error to the PipelineResult object. This method, rather than
   * just storing a single error object in pResult, stores a Map of errors. This allows more
   * than one error to be stored using the same key in the pResult object. pKey is
   * used to reference a HashMap of errors in pResult. So, calling
   * pResult.getError(pKey) will return an object which should be cast to a Map.
   * Each entry within the map is keyed by pId and its value is pError.
   *
   * @param pResult the PipelineResult object supplied in runProcess()
   * @param pKey the key to use to store the HashMap in the PipelineResult object
   * @param pId the key to use to store the error message within the HashMap in the
   *            PipelineResult object
   * @param pError the error object to store in the HashMap
   * @see atg.service.pipeline.PipelineResult
   * @see #runProcess(Object, PipelineResult)
   */
  protected void addHashedError(PipelineResult pResult, String pKey, String pId, Object pError)
  {
    Object error = pResult.getError(pKey);
    if (error == null) {
      HashMap map = new HashMap(5);
      pResult.addError(pKey, map);
      map.put(pId, pError);
    }
    else if (error instanceof Map) {
      Map map = (Map) error;
      map.put(pId, pError);
    }
  }

  /**
   * Rounds the input number to the number of decimal places specified by the 
   * roundingDecimalPlaces argument.  Rounds 1 through 4 down, 
   * and 5 through 9 up.
   *
   * @param pNumber the number to be rounded
   * @param pRoundingDecimalPlaces the number of decimal places to use in rounding
   * @return pNumber, rounded to the nearest [roundingDecimalPlaces] decimal points
   */
  protected double round (double pNumber, int pRoundingDecimalPlaces) {
    return (Long.valueOf(Math.round(pNumber * (Math.pow(10, pRoundingDecimalPlaces))))).doubleValue() / Math.pow(10, pRoundingDecimalPlaces);
  }
}
