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

import atg.service.pipeline.*;
import atg.commerce.order.*;
import atg.repository.*;
import atg.commerce.promotion.PromotionTools;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;

import java.util.*;
import java.text.MessageFormat;

/**
 * This processor checks whether any expired promotions are being used or were used
 * during the pricing of any part of the order.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcCheckForExpiredPromotions.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcCheckForExpiredPromotions 
extends ApplicationLoggingImpl
implements PipelineProcessor 
{
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcCheckForExpiredPromotions.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcCheckForExpiredPromotions() {
  }

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The processor completed
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = {SUCCESS};
    return ret;
  } 

  //-------------------------------------
  // property: promotionTools
  //-------------------------------------
  private PromotionTools mPromotionTools = null;

  /**
   * Returns the promotionTools
   */
  public PromotionTools getPromotionTools() {
    return mPromotionTools;
  }

  /**
   * Sets the promotionTools
   */
  public void setPromotionTools(PromotionTools pPromotionTools) {
    mPromotionTools = pPromotionTools;
  }

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcCheckForExpiredPromotions";

  /**
   * Sets property LoggingIdentifier
   **/
  public void setLoggingIdentifier(String pLoggingIdentifier) {
    mLoggingIdentifier = pLoggingIdentifier;
  }

  /**
   * Returns property LoggingIdentifier
   **/
  public String getLoggingIdentifier() {
    return mLoggingIdentifier;
  }

  protected String generateErrorMessage(String pErrorKey, Locale pLocale, RepositoryItem pPromo) {
    ResourceBundle bundle = null;

    if (pLocale != null) {
      bundle = LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, pLocale);
    } else {
      bundle = sResourceBundle;
    }

    Object [] args = new Object[1];
    
    if (pPromo != null) 
      args[0] = pPromo.getRepositoryId();
    else 
      args[0] = "null";

    String bundleMsg = bundle.getString(pErrorKey);
    String msg = MessageFormat.format(bundleMsg, args);
    return msg;
  }

  //-----------------------------------------------
  /**
   * This method throws an exception if any of the AmountInfos contain any expired
   * promotions.
   *
   * This method requires that an Order object be supplied in pParam in a HashMap.
   * Use the PipelineConstants class' static members to key the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    
    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));

    ArrayList orderPromos = new ArrayList(10);
    ArrayList taxPromos = new ArrayList(10);
    ArrayList itemPromos = new ArrayList(10);
    ArrayList shippingPromos = new ArrayList(10);
    PromotionTools promotionTools = getPromotionTools();
    Date currentDate = new Date();

    promotionTools.getOrderPromotions(order, orderPromos, taxPromos, itemPromos, shippingPromos);

    if (! orderPromos.isEmpty()) {
      List promos = orderPromos;
      for (int c=0; c<promos.size(); c++) {
        RepositoryItem promo = (RepositoryItem) promos.get(c);
        if (isLoggingDebug())
          logDebug("Check order promotion expiration " + promo + "; now=" + currentDate);
        boolean expired = promotionTools.checkPromotionExpiration(promo, currentDate);
        if (expired) {
          Locale userLocale = (Locale) map.get(PipelineConstants.LOCALE);          
          String errorKey = "OrderPromotionExpired";
          String errorMessage = generateErrorMessage(errorKey, userLocale, promo);
          pResult.addError(errorKey, errorMessage);
        }
      }
    }

    if (! itemPromos.isEmpty()) {
      List promos = itemPromos;
      for (int c=0; c<promos.size(); c++) {
        RepositoryItem promo = (RepositoryItem) promos.get(c);
        if (isLoggingDebug())
          logDebug("Check item promotion expiration " + promo + "; now=" + currentDate);
        boolean expired = promotionTools.checkPromotionExpiration(promo, currentDate);
        if (expired) {
          Locale userLocale = (Locale) map.get(PipelineConstants.LOCALE);          
          String errorKey = "ItemPromotionExpired";
          String errorMessage = generateErrorMessage(errorKey, userLocale, promo);
          pResult.addError(errorKey, errorMessage);
        }
      }
    }

    if (! shippingPromos.isEmpty()) {
      List promos = shippingPromos;
      for (int c=0; c<promos.size(); c++) {
        RepositoryItem promo = (RepositoryItem) promos.get(c);
        if (isLoggingDebug())
          logDebug("Check shipping promotion expiration " + promo + "; now=" + currentDate);
        boolean expired = promotionTools.checkPromotionExpiration(promo, currentDate);
        if (expired) {
          Locale userLocale = (Locale) map.get(PipelineConstants.LOCALE);          
          String errorKey = "ShippingPromotionExpired";
          String errorMessage = generateErrorMessage(errorKey, userLocale, promo);
          pResult.addError(errorKey, errorMessage);
        }
      }
    }

    if (! taxPromos.isEmpty()) {
      List promos = taxPromos;
      for (int c=0; c<promos.size(); c++) {
        RepositoryItem promo = (RepositoryItem) promos.get(c);
        if (isLoggingDebug())
          logDebug("Check tax promotion expiration " + promo + "; now=" + currentDate);
        boolean expired = promotionTools.checkPromotionExpiration(promo, currentDate);
        if (expired) {
          Locale userLocale = (Locale) map.get(PipelineConstants.LOCALE);          
          String errorKey = "TaxPromotionExpired";
          String errorMessage = generateErrorMessage(errorKey, userLocale, promo);
          pResult.addError(errorKey, errorMessage);
        }
      }
    }

    if (pResult.hasErrors()) {
      return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
    }

    return SUCCESS;
  }
}
