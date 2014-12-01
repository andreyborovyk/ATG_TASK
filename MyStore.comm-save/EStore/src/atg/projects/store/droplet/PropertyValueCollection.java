/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2010 Art Technology Group, Inc.
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

import atg.nucleus.naming.ParameterName;

import atg.projects.store.catalog.StoreCatalogTools;

import atg.service.collections.filter.CachedCollectionFilter;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;

import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;


/**
 * Creates a collection of property values for a collection of repository items.
 * An optional CollectionFilter can be applied to the values to filter and/or sort them.
 *
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/droplet/PropertyValueCollection.java#3 $
 */
public class PropertyValueCollection extends DynamoServlet {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/droplet/PropertyValueCollection.java#3 $$Change: 635816 $";

  /**
   * Items parameter name.
   */
  public static final ParameterName ITEMS_PARAM = ParameterName.getParameterName("items");

  /**
   * Proeprty name parameter name.
   */
  public static final ParameterName PROPERTY_NAME_PARAM = ParameterName.getParameterName("propertyName");

  /**
   * Filter parameter name.
   */
  public static final ParameterName FILTER_PARAM = ParameterName.getParameterName("filter");

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
   * Catalog tools.
   */
  protected StoreCatalogTools mCatalogTools = null;

  /**
   * Filter.
   */
  protected CachedCollectionFilter mFilter;

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

  /**
  * Sets the the default filter to execute.
   * @param pFilter - filter
  */
  public void setFilter(CachedCollectionFilter pFilter) {
    mFilter = pFilter;
  }

  /**
  * @return the default filter to execute.
  */
  public CachedCollectionFilter getFilter() {
    return mFilter;
  }

  //-------------------------------------
  // Methods
  //-------------------------------------

  /**
   * Renders the <code>output</code> oparam with a collection of optionally filtered/sorted values.
   * Renders the <code>empty</code> oparam if the collection of items or values is empty.
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
      List items = (List) pRequest.getObjectParameter(ITEMS_PARAM);

      Collection values = getCatalogTools().getPossibleValuesForSkus(items, propertyName);

      String renderParam;

      if (values.isEmpty()) {
        renderParam = EMPTY_OPARAM;
      } else {
        CachedCollectionFilter filter = getFilter(pRequest);

        if (filter != null) {
          // Apply the filter without caching
          values = filter.filterCollection(values, null, null, false, false);
        }

        renderParam = OUTPUT_OPARAM;
        pRequest.setParameter(VALUES_PARAM, values);
      }

      pRequest.serviceLocalParameter(renderParam, pRequest, pResponse);
    } catch (Exception exc) {
      // TODO error message
      pRequest.serviceLocalParameter(ERROR_OPARAM, pRequest, pResponse);
    }
  }

  /**
  *
  * @param pRequest DynamoHttpServletRequest value
  * @return the <code>filter</code> param from the request. if the param was not provided,
  * the configured <code>filter</code> is returned.
  */
  protected CachedCollectionFilter getFilter(DynamoHttpServletRequest pRequest) {
    CachedCollectionFilter filter = (CachedCollectionFilter) pRequest.getObjectParameter(FILTER_PARAM);

    if (filter == null) {
      filter = getFilter();
    }

    return filter;
  }
}
