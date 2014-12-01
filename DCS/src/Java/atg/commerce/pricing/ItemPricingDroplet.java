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

import atg.core.util.StringUtils;
import atg.servlet.*;
import atg.repository.RepositoryItem;
import atg.nucleus.naming.ParameterName;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.AuxiliaryData;
import atg.commerce.order.OrderTools;
import atg.commerce.CommerceException;
import atg.nucleus.naming.ComponentName;

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

/**
 * An abstract class which is used as the foundation for pricing items and displaying
 * the results to the user. People who extend this class must override the 
 * <code>performPricing</code> method to return the CommerceItem(s) that have been priced. 
 * These items are then bound into the <i>output</i> oparam with the default name <i>element.</i> 
 * One can change this parameter name by setting the <i>elementName</i> parameter.
 * <P>
 * This droplet can handle the following optional input parameters:
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
 *
 * @author Bob Mason
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/ItemPricingDroplet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public abstract
class ItemPricingDroplet
extends DynamoServlet
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/ItemPricingDroplet.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
    // Input parameters
  static final ParameterName PRICING_MODELS_PARAM = ParameterName.getParameterName("pricingModels");
  static final ParameterName LOCALE_PARAM = ParameterName.getParameterName("locale");
  static final ParameterName PROFILE_PARAM = ParameterName.getParameterName("profile");
  static final ParameterName PRODUCT_PARAM = ParameterName.getParameterName("product");
  static final ParameterName ELEMENT_NAME_PARAM = ParameterName.getParameterName("elementName");

    // Output parameters
  static final ParameterName OUTPUT = ParameterName.getParameterName("output");
  static final String ELEMENT_PARAM = "element";

  //-------------------------------------
  // Member Variables
  //-------------------------------------

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // property: PricingTools
  PricingTools mPricingTools;
  
  /**
   * Sets property PricingTools
   **/
  public void setPricingTools(PricingTools pPricingTools) {
    mPricingTools = pPricingTools;
  }
  
  /**
   * Returns property PricingTools
   **/
  public PricingTools getPricingTools() {
    return mPricingTools;
  }
  
  //-------------------------------------
  // property: OrderTools
  OrderTools mOrderTools;
  
  /**
   * Sets property OrderTools
   **/
  public void setOrderTools(OrderTools pOrderTools) {
    mOrderTools = pOrderTools;
  }
  
  /**
   * Returns property OrderTools
   **/
  public OrderTools getOrderTools() {
    return mOrderTools;
  }
  
  //-------------------------------------
  // property: DefaultLocale
  Locale mDefaultLocale;

  /**
   * Sets property DefaultLocale
   **/
  public void setDefaultLocale(Locale pDefaultLocale) {
    mDefaultLocale = pDefaultLocale;
  }

  /**
   * Returns property DefaultLocale. If the property value is null, then
   * JVM's default locale is returned.
   **/
  public Locale getDefaultLocale() {
    if (mDefaultLocale != null)
      return mDefaultLocale;
    else
      return Locale.getDefault();
  }

  //-------------------------------------
  // property: UseRequestLocale
  boolean mUseRequestLocale = true;

  /**
   * Sets property UseRequestLocale
   **/
  public void setUseRequestLocale(boolean pUseRequestLocale) {
    mUseRequestLocale = pUseRequestLocale;
  }

  /**
   * Returns property UseRequestLocale
   **/
  public boolean isUseRequestLocale() {
    return mUseRequestLocale;
  }  

  //-------------------------------------
  // property: ProfilePath
  protected ComponentName mProfilePath;
  
  /**
   * Sets property ProfilePath
   **/
  public void setProfilePath(String pProfilePath) {
    if (pProfilePath != null)
      mProfilePath = ComponentName.getComponentName(pProfilePath);
    else
      mProfilePath = null;
  }
  
  /**
   * Returns property ProfilePath
   **/
  public String getProfilePath() {
    if (mProfilePath != null)
      return mProfilePath.getName();
    else
      return null;
  }
  
  //-------------------------------------
  // property: UserPricingModelsPath
  protected ComponentName mUserPricingModelsPath;
  
  /**
   * Sets property UserPricingModelsPath
   **/
  public void setUserPricingModelsPath(String pUserPricingModelsPath) {
    if (pUserPricingModelsPath != null)
      mUserPricingModelsPath = ComponentName.getComponentName(pUserPricingModelsPath);
    else
      mUserPricingModelsPath = null;
  }
  
  /**
   * Returns property UserPricingModelsPath
   **/
  public String getUserPricingModelsPath() {
    if (mUserPricingModelsPath != null)
      return mUserPricingModelsPath.getName();
    else
      return null;
  }
    
  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instanceof ItemPricingDroplet
   */
  public ItemPricingDroplet() {
  }

  /**
   * Fetch the objects returned from <code>performPricing</code> and bind them to the
   * set element name and render the <i>output</i> oparam.   
   */
  public void service(DynamoHttpServletRequest pRequest, 
                      DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    Object obj = performPricing(pRequest, pResponse);
    if (obj != null) {
      String elementName = pRequest.getParameter(ELEMENT_NAME_PARAM);
      if (StringUtils.isEmpty(elementName)){
        elementName = ELEMENT_PARAM;
      }
      pRequest.setParameter(elementName, obj);
      pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
      return;
    }
  }

  /**
   * Return the object (CommerceItem or List of CommerceItems) that has been priced
   */
  protected abstract Object performPricing(DynamoHttpServletRequest pRequest, 
                                           DynamoHttpServletResponse pResponse) throws ServletException, IOException;
       
  
  /**
   * Return the collection of pricing models to use for pricing.
   */
  protected Collection getPricingModels(DynamoHttpServletRequest pRequest, 
                                        DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    Collection models = (Collection)pRequest.getObjectParameter(PRICING_MODELS_PARAM);
    if ((models == null) && (mUserPricingModelsPath != null)) {
      PricingModelHolder holder = (PricingModelHolder)pRequest.resolveName(mUserPricingModelsPath);
      if (holder != null) {
        models = holder.getItemPricingModels();
	pRequest.setParameter(PRICING_MODELS_PARAM.getName(), models);
      }
      
    }
    return models;
  }
  
  /**
   * Return the profile of the user requesting the price information
   */
  protected RepositoryItem getProfile(DynamoHttpServletRequest pRequest, 
                                      DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    RepositoryItem profile = (RepositoryItem)pRequest.getObjectParameter(PROFILE_PARAM);
    if ((profile == null) && (mProfilePath != null)) {
      profile = (RepositoryItem)pRequest.resolveName(mProfilePath);
      pRequest.setParameter(PROFILE_PARAM.getName(), profile);
    }

    return profile;
  }
  
  /**
   * Return the product which encompasses the items to be priced.
   */
  protected Object getProduct(DynamoHttpServletRequest pRequest, 
                              DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    return pRequest.getObjectParameter(PRODUCT_PARAM);
  }   
  
  /**
   * Return the product id of the supplied product object
   */
  protected String getProductId(Object pProduct) {    
    if (pProduct instanceof RepositoryItem) {
      String productId = ((RepositoryItem)pProduct).getRepositoryId();
      if (isLoggingDebug())
        logDebug("getProductId for " + pProduct + "=" + productId);
      return productId;
    }
    else {
      if ((isLoggingDebug()) && (pProduct != null))
        logDebug("Cannot get productId for object of class " + pProduct.getClass());
    }
      
    return null;
  } 

  /**
   * Return the id of the supplied catalogRef (aka sku) object
   */
  protected String getCatalogRefId(Object pCatalogRef) {
    if (pCatalogRef instanceof RepositoryItem)
      return ((RepositoryItem)pCatalogRef).getRepositoryId();
    else
      return null;
  } 

  /**
   * Returns the locale associated with the request. The method first searches
   * for a request paramater named <code>locale</code>. This value can be
   * either a java.util.Locale object or a String which represents the locale. 
   * Next if the <code>useRequestLocale</code> property is true, then the locale
   * of the request will be returned. Finally, if the locale cannot be determined, 
   * the the <code>defaultLocale</code> property is used.
   */
  protected Locale getUserLocale(DynamoHttpServletRequest pRequest, 
                                 DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    Object obj = pRequest.getObjectParameter(LOCALE_PARAM);
    if (obj instanceof Locale)
      return (Locale)obj;
    else if (obj instanceof String) {
      Locale localeCopy = RequestLocale.getCachedLocale((String)obj);
      pRequest.setParameter(LOCALE_PARAM.getName(), localeCopy);
      return localeCopy;
    }
    else if (isUseRequestLocale()) {
      RequestLocale requestLocale = pRequest.getRequestLocale();
      if (requestLocale != null) {
	Locale localeCopy = requestLocale.getLocale();
	pRequest.setParameter(LOCALE_PARAM.getName(), localeCopy);
        return localeCopy;
      }
    }
    return getDefaultLocale();
  }

  /**
   * With the given parameters create a new CommerceItem that will be used for pricing
   */
  protected CommerceItem createCommerceItem(String pCatalogRefId, Object pCatalogRef, 
                                            String pProductId, Object pProductRef, 
                                            long pQuantity) 
       throws CommerceException
  {
    if (isLoggingDebug())
      logDebug("creating CommerceItem: pCatalogRefId=" + pCatalogRefId + "; pCatalogRef=" + pCatalogRef + 
               "; pProductId=" + pProductId + "; pProductRef=" + pProductRef + "; pQuantity=" + pQuantity);

    PricingCommerceItem item = new PricingCommerceItem();
    item.setCatalogRefId(pCatalogRefId);
    item.setQuantity(pQuantity);
    try {
      item.setPriceInfo((ItemPriceInfo)getOrderTools().getDefaultItemPriceInfoClass().newInstance());
    }catch (IllegalAccessException e) {
      throw new CommerceException(e);
    }
    catch (InstantiationException e) {
      throw new CommerceException(e);
    } // end of try-catch

    AuxiliaryData aux = item.getAuxiliaryData();
    aux.setCatalogRef(pCatalogRef);
    aux.setProductId(pProductId);
    aux.setProductRef(pProductRef);

    return item;
  }

} // end of class





