/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;

import atg.adapter.gsa.GSAItem;
import atg.commerce.catalog.comparison.ProductComparisonList;
import atg.droplet.ForEach;
import atg.nucleus.naming.ParameterName;
import atg.projects.store.catalog.StoreCatalogTools;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * This droplet is used in Product Comparison pages in order to determine 
 * whether there is at least one object in <i>items</i> containing not-null 
 * value for the specified property or the rendering of this property should be omitted. 
 * <p>
 *
 * @author Vitali Kazlouski
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/droplet/ComparisonRowExistsDroplet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ComparisonRowExistsDroplet extends ForEach {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/droplet/ComparisonRowExistsDroplet.java#2 $$Change: 651448 $";

  /**
   * Items parameter name.
   */
  public static final ParameterName ITEMS_PARAM = ParameterName.getParameterName("items");

  /**
   * Property name parameter name.
   */
  public static final ParameterName PROPERTY_NAME_PARAM = ParameterName.getParameterName("propertyName");

  /**
   * Source type parameter name.
   * There are two main values for this field, which define where the property 
   * name specified by {@link #PROPERTY_NAME_PARAM} is supposed to exist:
   * <li><i>sku</i> -  in childSKUs
   * <li><i>product</i> - in the product itself 
   */
  public static final ParameterName SOURCE_TYPE_PARAM = ParameterName.getParameterName("sourceType");

  /**
   * Values parameter name.
   */
  public static final String VALUES_PARAM = "values";

  /**
   * Output parameter name.
   */
  public static final String OUTPUT_OPARAM = "output";

  /**
   * Empty parameter name.
   */
  public static final String EMPTY_OPARAM = "empty";

  /**
   * Error parameter name.
   */
  public static final String ERROR_OPARAM = "error";

  /**
   * Product property name.
   */
  public static final String PRODUCT_PROPERTY_NAME = "product";

  /**
   * Source type "sku" 
   */
  public static final String TYPE_SKU = "sku";

  /**
   * Source type "product" 
   */
  public static final String TYPE_PRODUCT = "product";
  
  /**
   * Catalog tools.
   */
  protected StoreCatalogTools mCatalogTools = null;

  /**
   * @return the catalog tools.
   */
  public StoreCatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  /**
   * Sets the catalogTools.
   * @param pCatalogTools - catalog tools.
   */
  public void setCatalogTools(StoreCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  //-------------------------------------
  // Methods
  //-------------------------------------

  /**
   * Renders the <code>output</code> oparam if 1 or more objects have a non-null value for a certain property, 
   * which name is transferred using <code>PROPERTY_NAME_PARAM</code> paramter.<p>
   * Renders the <code>empty</code> oparam if all objects in <i>items</i> have null or empty value for that property. 
   * parameter wasn't found in any of the items.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    try {
      String propertyName = pRequest.getParameter(PROPERTY_NAME_PARAM);
      String propertyType = pRequest.getParameter(SOURCE_TYPE_PARAM);
      List<ProductComparisonList.Entry> items = (List) pRequest.getObjectParameter(ITEMS_PARAM);
  
      boolean notNullValuesExist = false;
 
      if(TYPE_SKU.equals(propertyType)){
        //the property is in childSKUs 
        for (ProductComparisonList.Entry object : items) {
    
          GSAItem gsaItem = (GSAItem) (object.get(PRODUCT_PROPERTY_NAME));
          if(gsaItem != null){
            List skus = (List) gsaItem.getPropertyValue("childSKUs");
            if(skus != null && !skus.isEmpty() && ((GSAItem)skus.get(0)).getItemDescriptor().hasProperty(propertyName)){
              Collection values = getCatalogTools().getPossibleValuesForSkus(skus, propertyName);  
              if (values != null && !values.isEmpty()) {
                notNullValuesExist = true;
                break;
              }
            }
          }
        }
      }else if(TYPE_PRODUCT.equals(propertyType)){
        //the property is in the product
        for (ProductComparisonList.Entry object : items) {
          
          GSAItem gsaItem = (GSAItem) (object.get(PRODUCT_PROPERTY_NAME));
          if(gsaItem != null){
            if(gsaItem.getItemDescriptor().hasProperty(propertyName)){
              Collection values = (Collection) gsaItem.getPropertyValue(propertyName);  
              if (values != null && !values.isEmpty()) {
                notNullValuesExist = true;
                break;
              }
            }
          }
        }
      }
  
      String renderParam;
  
      if (notNullValuesExist) {
        renderParam = OUTPUT_OPARAM;
      } else {
        renderParam = EMPTY_OPARAM;
      }
  
      pRequest.serviceLocalParameter(renderParam, pRequest, pResponse);
  
    } catch (Exception exc) {
      pRequest.serviceLocalParameter(ERROR_OPARAM, pRequest, pResponse);
    }
  }


}
