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

import atg.servlet.*;
import atg.repository.RepositoryItem;
import atg.nucleus.naming.ParameterName;
import atg.commerce.order.CommerceItem;
import atg.commerce.CommerceException;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.core.util.DictionaryThreadKey;

import java.util.HashMap;
import java.util.Locale;
import java.util.Collection;
import java.util.Map;
import javax.servlet.*;

import java.io.IOException;

/**
 * This Dynamo Servlet Bean is used to price a single item. The only required parameter is named <i>item</i>.
 * This can be either a RepositoryItem, which represents the item to be priced, or a CommerceItem which can be 
 * directly priced. If the item supplied is a RepositoryItem then a new CommerceItem is created to be priced.
 * <P>
 * The following parameters are optional:
 * <DL>
 * <DT>pricingModels
 * <DD>A collection of pricing models that should be used to price the items. 
 * If this value if not supplied then by default a collection of pricing models are
 * used from the user's PricingModelHolder component. This component is resolved through the
 * <code>userPricingModelsPath</code> property.
 *
 * <DT>locale
 * <DD>The locale the the pricing should take place within
 *
 * <DT>profile
 * <DD>The user for whom pricing is performed. If this parameter is null, then the profile
 * is resolved through the property <code>profilePath</code>.
 *
 * <DT>product
 * <DD>The object which represents the product definition of the item to price. Typically
 * the items which are priced are skus. In that case this is the product which encompasses all the skus.
 *
 * <DT>elementName
 * <DD>The name to use as the parameter set within the <i>output</i> oparam.
 *
 * <DT>quantity
 * <DD>The Long quantity of the input product which should be priced. Used when constructing a 
 *     CommerceItem out of the supplied information.
 *
 * </DL>
 * <P>
 *
 * With these parameters the supplied or constructed CommerceItem, the user's current promotions, their profile
 * and locale are passed to the <code>PricingTools.priceItem</code> method. This then calls into the ItemPricingEngine. 
 * The CommerceItem, which has now been priced, is made available as a parameter in the <code>output</code> oparam. 
 * By default the name of this CommerceItem parameter is <i>element</i>, however as described above this can be changed 
 * through the <code>elementName</code> input parameter.
 *
 *<P>
 * For example:<BR>
 * NOTE: the promotions, locale and profile are extracted from the request, since they are not supplied as parameters.
 *
 * <PRE>
 * &lt;droplet bean="/atg/commerce/pricing/PriceItem"&gt;
 * &lt;param name="item" value="param:sku"&gt;
 * &lt;param name="product" value="param:product"&gt;
 * &lt;param name="quantity" value="param:quantity"&gt;
 * &lt;param name="elementName" value="pricedItem"&gt;
 * &lt;oparam name="output"&gt;
 *    &lt;valueof param="pricedItem.priceInfo.amount" currency&gt;no price&lt;/valueof&gt;
 * &lt;/oparam&gt;
 * &lt;/droplet&gt;
 * </PRE>
 *
 * @author Bob Mason
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/PriceItemDroplet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public 
  class PriceItemDroplet
  extends ItemPricingDroplet
  {
    //-------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
      "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/PriceItemDroplet.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
    static final ParameterName ITEM_PARAM = ParameterName.getParameterName("item");

    static final ParameterName ITEM_QUANTITY_PARAM = ParameterName.getParameterName("quantity");

    private static final String PERFORM_MONITOR_NAME="PriceItemDroplet";
    //-------------------------------------
    // Member Variables
    //-------------------------------------

    /** The key into the DictionaryThread for the the Dynamo request */
    class CommerceItemKey implements DictionaryThreadKey {
      public Object createObject () { 
        return new PricingCommerceItem(); 
      }
    }
    
    DictionaryThreadKey mCommerceItemKey = new CommerceItemKey();

  //-------------------------------------
  // Properties
  //-------------------------------------
    protected Qualifier mPricingQualifierService;
    /**
     * Returns the qualifier service that is used to price promotions
     * during the pricing operation. 
     * @return
     */
    public Qualifier getPricingQualifierService()
    {
      return mPricingQualifierService;
    }

    public void setPricingQualifierService(Qualifier pPricingQualifierService)
    {
      mPricingQualifierService = pPricingQualifierService;
    } 

  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instanceof PriceItemDroplet
   */
    public PriceItemDroplet() {
    }

  /**
   * With the given parameters create a new CommerceItem that will be used for pricing. 
   */
    protected CommerceItem createCommerceItem(String pCatalogRefId, Object pCatalogRef, 
                                              String pProductId, Object pProductRef, 
                                              long pQuantity) 
         throws CommerceException
    {
		return getPricingTools().createPricingCommerceItem(pCatalogRefId, pProductId, pQuantity);
    }
    
    /**
     * Returns the CommerceItem which has been priced. This method gathers the user's promotions, locale and profile
   * and calls into the PricingTools to price the item.
   */
    protected Object performPricing(DynamoHttpServletRequest pRequest, 
				    DynamoHttpServletResponse pResponse) 
      throws ServletException, IOException 
    { 
      String perfName = "performPricing";
      PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
      boolean perfCancelled = false;

      try {

	    try {
	      Collection pricingModels = getPricingModels(pRequest, pResponse);
	      Locale locale = getUserLocale(pRequest, pResponse);
	      RepositoryItem profile = getProfile(pRequest, pResponse);
	      CommerceItem item = getItemToPrice(pRequest, pResponse);
	      Map extraParameters = getExtraParams(pRequest, pResponse);
  
	      if (item != null) {        
	        getPricingTools().priceItem(item, pricingModels, locale, profile, extraParameters);
	      return item;
	    }
	  }
	  catch (CommerceException exc) {
	    if (isLoggingError())
	      logError(exc);
	    }
	    
	    return null;

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
   * Return the CommerceItem, which should be priced (pricing has not occured yet). This method extracts the
   * <i>item</i> object parameter. If this object is an instanceof CommerceItem then we simply return the object. 
   * If however this object is a RepositoryItem, which represents a SKU, then we extract the catalogRefId, product object, 
   * product id and create a CommerceItem for use in pricing. 
   */
    CommerceItem getItemToPrice(DynamoHttpServletRequest pRequest, 
				DynamoHttpServletResponse pResponse) 
      throws CommerceException, ServletException, IOException 
    {
      String perfName = "getItemToPrice";
      PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
      boolean perfCancelled = false;

      try {

		Object obj = pRequest.getObjectParameter(ITEM_PARAM);
		if (obj instanceof CommerceItem) {
	  	  return (CommerceItem)obj;
		}
		else if (obj instanceof RepositoryItem) {
	  	  RepositoryItem sku = (RepositoryItem)obj;
	  	  String skuId = getCatalogRefId(sku);
	      Object product = getProduct(pRequest, pResponse);
	      String productId = getProductId(product);
	  
	      long quantity = 1;
	      Long requestQuantity = getProductQuantity(pRequest, pResponse);
	      if (requestQuantity != null) {
	        quantity = requestQuantity.longValue();
	      } 
	  
	      return createCommerceItem(skuId, sku, productId, product, quantity);
	      
	    }
	    return null;
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
     * Intended to be used to build a map of extra parameters for passing in to ItemPricingEngines
     * for their use in pricing the input item.  
     * <p>
     * By default, this method will create a parameter map containing the configured qualifier service
     * to be used in the pricing operation. Otherwise, it returns null. 
     * 
     * Note: override this method if you've created a new implementation of ItemPricingEngine
     * which uses the 'ExtraParameters' param that's passed in to its pricing methods.
     * 
     * @param pRequest the request object which holds the extra pararameters map in its parameters table
     * @param pResponse the response object
     * @exception ServletException if there was a problem getting the request parameter value
     * @return the Map in pRequest's "extra parameters" parameter, if any
     */
    protected Map getExtraParams (DynamoHttpServletRequest pRequest,
        DynamoHttpServletResponse pResponse) 
        throws ServletException 
      {

        if(getPricingQualifierService() != null)
        {
          Map params = new HashMap();
          params.put(ItemDiscountCalculator.EXTRA_PARAM_QUALIFIERSERVICE,getPricingQualifierService());
          return params;
        }
        else return null;
      } // end getExtraParams


    /**
     * Produces an Intger value for the quantity of items that this droplet should price
     * by looking up a Long object in the <i>quantity</i> parameter of the Request object.
     * The object in that request parameter must be of type Long, or else null is returned.
     * 
     * @param pRequest the request object which holds the extra pararameters that need to be extracted
     * @param pResponse the response object
     * @exception ServletException if there was a problem getting the request parameter value
     * @return the Long in pRequest's <i>quantity</i> parameter, if any
     */
    Long getProductQuantity (DynamoHttpServletRequest pRequest,
			     DynamoHttpServletResponse pResponse) 
      throws ServletException 
    {
      String perfName = "getProductQuantity";
      PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
      boolean perfCancelled = false;

      try {

	    Object obj = pRequest.getObjectParameter(ITEM_QUANTITY_PARAM);

        if (obj instanceof String) {
          try {
            Long l = new Long((String)obj);
            return l;
          }
          catch(NumberFormatException nfe) {
            if(isLoggingError())
              logError(nfe);
            return null;
          }
        }


	    return null;
	
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
      
    } // end getProductQuantity
    
    
    /**
     * Intended to be used to build a map of extra parameters for passing in to ItemPricingEngines
     * for their use in pricing the input item.  
     * <p>
     * By default, this method will create a parameter map containing the configured qualifier service
     * to be used in the pricing operation. Otherwise, it returns null. 
     * 
     * Note: override this method if you've created a new implementation of ItemPricingEngine
     * which uses the 'ExtraParameters' param that's passed in to its pricing methods.
     * 
     * @param pRequest the request object which holds the extra pararameters map in its parameters table
     * @param pResponse the response object
     * @exception ServletException if there was a problem getting the request parameter value
     * @return the Map in pRequest's "extra parameters" parameter, if any
     */
    

  } // end of class

