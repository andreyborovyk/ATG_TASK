/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2011 Art Technology Group, Inc.
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
package atg.projects.store.pricing;

import atg.commerce.order.Order;

import atg.commerce.pricing.ItemDiscountCalculator;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.Qualifier;

import atg.repository.RepositoryItem;

import atg.service.perfmonitor.PerfStackMismatchException;
import atg.service.perfmonitor.PerformanceMonitor;

import java.util.*;


/**
 * Extends the original to allow the pricing model to determine what
 * qualifier service to use.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/pricing/StoreItemDiscountCalculator.java#2 $
 */
public class StoreItemDiscountCalculator extends ItemDiscountCalculator {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/pricing/StoreItemDiscountCalculator.java#2 $$Change: 651448 $";

  /**
   * Perform monitor name.
   */
  private static final String PERFORM_MONITOR_NAME = "StoreDiscountCalculator";

  /**
   * Map to store qualifiers in so we don't keep lookin' em up.
   */
  private HashMap mQualifierMap = new HashMap();

  /**
   * @return qualifier map.
   */
  protected HashMap getQualifierMap() {
    return mQualifierMap;
  }

  /**
   * Get the qualifier from the pricing model.  If none found use the
   * default qualifier delivered by <code>qualifierService</code>
   * @param pPricingModel pricing model repository item
   * @return qualifier from the pricing model
   * @throws atg.commerce.pricing.PricingException if unable to find qualifier for the given name
   */
  protected Qualifier getQualifierService(RepositoryItem pPricingModel)
    throws PricingException {
    Qualifier q = getQualifierService();

    if (pPricingModel != null) {
      StorePricingModelProperties cp = (StorePricingModelProperties) getPricingModelProperties();

      String qualifierName = (String) pPricingModel.getPropertyValue(cp.getQualifierPropertyName());

      if (isLoggingDebug()) {
        logDebug("getQualifierService():  looking for qualifier " + qualifierName + " for promotion " + pPricingModel);
      }

      // see if its the default value
      if (qualifierName.equals(cp.getDefaultQualifierValue())) {
        return q;
      }

      // otherwise look it up
      Object qualifier = getQualifierMap().get(qualifierName);

      // if no qualifier then its not in the map
      if (qualifier == null) {
        qualifier = resolveName(qualifierName);
        // put it in the map so we don't have to resolve it all the time
        getQualifierMap().put(qualifierName, qualifier);
      }

      if (qualifier instanceof Qualifier) {
        q = (Qualifier) qualifier;
      }

      if (q == null) {
        throw new PricingException("Unable to find qualifier for name " + qualifierName);
      }
    }

    return q;
  }

  /**
   * Overrides the original to retrieve the qualifier service from the
   * pricing model's optional 'qualifier' property.
   * {@inheritDoc}
   */
  protected Collection findQualifyingItems(List pPriceQuotes, List pItems, RepositoryItem pPricingModel,
    RepositoryItem pProfile, Locale pLocale, Order pOrder, Map pExtraParameters)
    throws PricingException {
    String perfName = "findQualifyingItems";
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);

    boolean perfCancelled = false;

    try {
      // get the qualifier service.
      Qualifier q = getQualifierService(pPricingModel);

      // get a list of the qualified items
      return q.findQualifyingItems(pPriceQuotes, pItems, pPricingModel, pProfile, pLocale, pOrder, null, null, null,
        pExtraParameters);
    } finally {
      try {
        if (!perfCancelled) {
          PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfName);
          perfCancelled = true;
        }
      } catch (PerfStackMismatchException e) {
        if (isLoggingWarning()) {
          logWarning(e);
        }
      }
    } // end finally    
  }
}
