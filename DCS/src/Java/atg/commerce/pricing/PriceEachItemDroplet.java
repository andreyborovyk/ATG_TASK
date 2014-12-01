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
import atg.commerce.order.OrderManager;
import atg.commerce.CommerceException;
import atg.core.util.ResourceUtils;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.service.perfmonitor.PerfStackMismatchException;

import java.util.Date;
import java.util.Locale;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.text.MessageFormat;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;

/**
 * This Dynamo Servlet Bean is used to price a collection of items. The only required parameter is named <i>items</i>.
 * This can be either a collection of RepositoryItems, which represents the item to be priced, or CommerceItems which can be 
 * directly priced. If the items supplied are RepositoryItems then a new collection of CommerceItems is created to be priced.
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
 * </DL>
 * <P>
 *
 * With these parameters the supplied or constructed CommerceItems, the user's current promotions, their profile
 * and locale are passed to the <code>PricingTools.priceEachItem</code> method. This then calls into the ItemPricingEngine. 
 * The CommerceItems, which have now been priced, are made available as a parameter in the <code>output</code> oparam. 
 * By default the name of this collection parameter is <i>element</i>, however as described above this can be changed 
 * through the <code>elementName</code> input parameter.
 *
 *<P>
 * For example:<BR>
 * NOTE: the promotions, locale and profile are extracted from the request, since they are not supplied as parameters.
 *
 * <PRE>
 * &lt;droplet bean="/atg/commerce/pricing/PriceEachItem"&gt;
 * &lt;param name="items" value="param:product.childSKUs"&gt;
 * &lt;!-- the product param is already defined in this scope so we do not need to set it --&gt;
 * &lt;oparam name="output"&gt;
 *   &lt;!-- Now iterate over each of the CommerceItems to display the prices --&gt;
 *   &lt;droplet bean="/atg/dynamo/droplet/ForEach"&gt;
 *   &lt;param name="array" value="param:element"&gt;
 *   &lt;param name="elementName" value="pricedItem"&gt;
 *   &lt;oparam name="output"&gt;
 *     &lt;valueof param="pricedItem.auxiliaryData.catalogRef.displayName"&gt;&lt;/valueof&gt; - 
 *     &lt;!-- Toggle a different display depending if the item is on sale or not --&gt;
 *     &lt;droplet bean="Switch"&gt;
 *     &lt;param name="value" value="param:pricedItem.priceInfo.onSale"&gt;
 *     &lt;oparam name="false"&gt;
 *       &lt;valueof param="pricedItem.priceInfo.amount" currency&gt;no price&lt;/valueof&gt;
 *     &lt;/oparam&gt;
 *     &lt;oparam name="true"&gt;
 *       List price for &lt;valueof param="pricedItem.priceInfo.listPrice" currency&gt;no price&lt;/valueof&gt;
 *       on sale for &lt;valueof param="pricedItem.priceInfo.salePrice" currency&gt;&lt;/valueof&gt;!
 *     &lt;/oparam&gt;
 *     &lt;/droplet&gt;&lt;BR&gt;
 *   &lt;/oparam&gt;   
 *   &lt;/droplet&gt;
 * &lt;/oparam&gt;
 * &lt;/droplet&gt;
 * </PRE>
 *
 * @author Bob Mason
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/PriceEachItemDroplet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public 
  class PriceEachItemDroplet
  extends ItemPricingDroplet
  {
    //-------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
      "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/PriceEachItemDroplet.java#2 $$Change: 651448 $";

    static final String MY_RESOURCE_NAME = "atg.commerce.pricing.Resources";

    /** Resource Bundle **/
    public static final java.util.ResourceBundle sResourceBundle =
      java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  //-------------------------------------
  // Constants
  //-------------------------------------
    static final ParameterName ITEMS_PARAM = ParameterName.getParameterName("items");
    private static final String PERFORM_MONITOR_NAME="PriceEachItemDroplet";

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
   * Constructs an instanceof PriceEachItemDroplet
   */
    public PriceEachItemDroplet() {
    }

    /**
   * Returns the list of CommerceItems which have been priced. This method gathers the user's promotions, locale and profile
   * and calls into the PricingTools to price each item.
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
	  List items = getItemsToPrice(pRequest, pResponse);
	  Map extraParameters = getExtraParams(pRequest, pResponse);

	  if (isLoggingDebug())
	    logDebug("pricingModels="+pricingModels+"; locale="+locale+"; profile="+profile+"; items="+items);
	  if (items != null) {
	    getPricingTools().priceEachItem(items, pricingModels, locale, profile, extraParameters);
	    return items;
	  }
	  else
	  {
	      String args[] = { ITEMS_PARAM.toString() };
	      if (isLoggingError())
	          logError(ResourceUtils.getMsgResource(
	                  "missingRequiredInputParam", MY_RESOURCE_NAME,
	                  sResourceBundle, args));
         
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
   * Return the list of CommerceItems to price (pricing has not occured yet).  This method extracts the
   * <i>items</i> object parameter. If this object is a collection of CommerceItems then we simply return the collection. 
   * If however the object is a collection of RepositoryItems, which represent SKUs, then we extract the catalogRefId, 
   * product object,  product id and create a CommerceItem for each item for use in pricing. 
   */
    protected List getItemsToPrice(DynamoHttpServletRequest pRequest, 
				   DynamoHttpServletResponse pResponse) 
      throws CommerceException, ServletException, IOException 
    {
      String perfName = "getItemsToPrice";
      PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
      boolean perfCancelled = false;

      try {

	Object obj = pRequest.getObjectParameter(ITEMS_PARAM);      
        
	if (obj instanceof List) {
	  return convertItemsToPrice((List)obj, pRequest, pResponse);
	} 
	else if (obj instanceof Collection) {
	  Collection collection = (Collection)obj;
	  List list = new ArrayList(collection);
	  return convertItemsToPrice(list, pRequest, pResponse);
	} 
	else if (obj instanceof RepositoryItem []) {
	  return convertItemsToPrice((RepositoryItem [])obj, pRequest, pResponse);
	}
	else if (obj instanceof CommerceItem []) {
	  return convertItemsToPrice((CommerceItem [])obj, pRequest, pResponse);          
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
     * Intended to be used to extract a map of extra parameters for passing in to ItemPricingEngines
     * for their use in pricing the input item.  DCS pricing engines don't use the extraParameters,
     * So this method is here merely for people to override in subclasses of PriceItemDroplet.
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
      return null;
    } // end getExtraParams

    /**
   * Convert, as needed, each element of the list into a CommerceItem and then return the new list
   */
    protected List convertItemsToPrice(List pItems, 
				       DynamoHttpServletRequest pRequest, 
				       DynamoHttpServletResponse pResponse) 
      throws CommerceException, ServletException, IOException 
    {                              

      String perfName = "convertItemsToPrice";
      PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
      boolean perfCancelled = false;

      try {

	if ((pItems != null) && (! pItems.isEmpty())) {
	  // Since at this moment we only know if we have a list, we need to see if the
	  // elements of that list are CommerceItems or RepositoryItems
	  Object itemElement = pItems.get(0);
	  if (itemElement instanceof CommerceItem) {
	    return pItems;
	  }
	  else if (itemElement instanceof RepositoryItem) {
	    Object product = getProduct(pRequest, pResponse);
	    String productId = getProductId(product);
        
	    // Since we have RepositoryItems we need to "convert" them into CommerceItems, for the pricing engine
	    int size = pItems.size();
	    ArrayList arrayList = new ArrayList(size);
	    for (int c=0; c<size; c++) {
	      RepositoryItem sku = (RepositoryItem)pItems.get(c);
	      String skuId = getCatalogRefId(sku);
	      CommerceItem commerceItem = createCommerceItem(skuId, sku, productId, product, 1);
	      arrayList.add(c, commerceItem);
	    }
	    return arrayList;
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

    }
  
    /**
   * Convert each supplied RepositoryItem into a CommerceItem and return the new list
   */
    protected List convertItemsToPrice(RepositoryItem [] pItems,
				       DynamoHttpServletRequest pRequest, 
				       DynamoHttpServletResponse pResponse) 
      throws CommerceException, ServletException, IOException 
    {                              

      String perfName = "convertItemsToPrice2";
      PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
      boolean perfCancelled = false;

      try {

	if (pItems != null) {
	  Object product = getProduct(pRequest, pResponse);
	  String productId = getProductId(product);
      
	  int size = pItems.length;
	  ArrayList arrayList = new ArrayList(size);
	  for (int c=0; c<size; c++) {
	    RepositoryItem sku = pItems[c];
	    String skuId = getCatalogRefId(sku);
	    CommerceItem commerceItem = createCommerceItem(skuId, sku, productId, product, 1);
	    arrayList.add(c, commerceItem);
	  }
	  return arrayList;
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
   * Return the array of CommerceItems as a list
   */
    protected List convertItemsToPrice(CommerceItem [] pItems,
				       DynamoHttpServletRequest pRequest, 
				       DynamoHttpServletResponse pResponse) 
      throws CommerceException, ServletException, IOException 
    {                              
      String perfName = "convertItemsToPrice3";
      PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
      boolean perfCancelled = false;

      try {

	if (pItems != null) {
	  return Arrays.asList(pItems);
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

  } // end of class
