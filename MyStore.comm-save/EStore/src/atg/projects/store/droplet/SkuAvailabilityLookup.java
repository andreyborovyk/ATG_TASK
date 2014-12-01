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

import atg.commerce.inventory.InventoryException;

import atg.nucleus.naming.ParameterName;

import atg.projects.store.inventory.StoreInventoryManager;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;

import java.util.Date;

import javax.servlet.ServletException;


/**
 * <P>
 * This droplet looks at a single sku and the product referencing it and determines if it is
 * available, preorderable, backorderable or unavailable.
 *
 * <P>
 * The <code>inventoryManager</code> property must be configured for this droplet.
 *
 * <P>
 * This droplet takes the following input parameters:
 *
 * <UL>
 * <LI>
 * product - The product repository item that is to be checked
 * </LI>
 * <LI>
 * skuId - The sku repository item id that is to be checked
 * </LI>
 * </UL>
 *
 * <P>
 * This droplet renders the following open parameters:
 *
 * <UL>
 * <LI>
 * available - rendered if the sku is available
 * </LI>
 * <LI>
 * preorderable - rendered if the sku is preorderable
 * </LI>
 * <LI>
 * backorderable - rendered if the sku is backorderable
 * </LI>
 * <LI>
 * unavailable - rendered if the sku is unavailable
 * </LI>
 * <LI>
 * error - rendered if there is an error looking up the availability data for the sku
 * </LI>
 * <LI>
 * default - rendered if there is no open parameter coorespoding to the result of the lookup
 * </LI>
 * </UL>
 *
 * <P>
 * This droplet sets the following output parameters when rendering the available and
 * backorderable open parameters.
 *
 * <UL>
 * <LI>
 * availabilityDate - a Date object representing the date the item is supposed to become
 * available (may be null)
 * </LI>
 * </UL>
 *
 * <P>
 * Example:
 * <PRE>
 *
 * &lt;dsp:droplet name="/atg/store/droplet/SkuAvailabilityLookup"&gt;
 *   &lt;dsp:param name="product" param="product"/&gt;
 *   &lt;dsp:param name="skuId" param="product.childSKUs[0].repositoryId"/&gt;
 *   &lt;dsp:oparam name="available"&gt;
 *     Is available
 *   &lt;/dsp:oparam&gt;
 *   &lt;dsp:oparam name="preorderable"&gt;
 *     Can be preordered
 *     &lt;dsp:droplet name="/atg/dynamo/droplet/IsEmpty"&gt;
 *       &lt;dsp:param name="value" param="availabilityDate"/&gt;
 *       &lt;dsp:oparam name="false"&gt;
 *         &lt;br&gt;
 *         Will be available
 *         &lt;dsp:valueof param="availabilityDate" converter="date" format="M/d/yy"/&gt;
 *       &lt;/dsp:oparam&gt;
 *     &lt;/dsp:droplet&gt;
 *   &lt;/dsp:oparam&gt;
 *   &lt;dsp:oparam name="backorderable"&gt;
 *     Can be backordered
 *   &lt;/dsp:oparam&gt;
 *   &lt;dsp:oparam name="unavailable"&gt;
 *     Is unavailable
 *   &lt;/dsp:oparam&gt;
 * &lt;/dsp:droplet&gt;
 *
 * </PRE>
 *
 * <P>
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/droplet/SkuAvailabilityLookup.java#3 $$Change: 635816 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class SkuAvailabilityLookup extends DynamoServlet {
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/droplet/SkuAvailabilityLookup.java#3 $$Change: 635816 $";

  /** The input parameter name for the product and skuId to check. */
  public static final ParameterName PRODUCT = ParameterName.getParameterName("product");

  /**
   * Sku id parameter name.
   */
  public static final ParameterName SKU_ID = ParameterName.getParameterName("skuId");

  /** The output parameter name for the availabilityDate to set. */
  public static final String AVAILABILITY_DATE = "availabilityDate";

  /** The oparam name rendered once if the item is a preorderable item. */
  public static final String OPARAM_OUTPUT_PREORDERABLE = "preorderable";

  /** The oparam name rendered once if the item is not preorderable and is in stock. */
  public static final String OPARAM_OUTPUT_AVAILABLE = "available";

  /** The oparam name rendered once if the item is not preorderable, is not in stock  and is backorderable. */
  public static final String OPARAM_OUTPUT_BACKORDERABLE = "backorderable";

  /** The oparam name rendered once if the item is none of the above. */
  public static final String OPARAM_OUTPUT_UNAVAILABLE = "unavailable";

  /** The oparam name rendered once if the provided skuId can not be looked up in the inventory repository. */
  public static final String OPARAM_OUTPUT_ERROR = "error";

  /** The oparam name rendered once if none of the above open parameters exists. */
  public static final String OPARAM_OUTPUT_DEFAULT = "default";

  /**
   * Inventory manager.
   */
  protected StoreInventoryManager mInventoryManager;

  /**
   * @return the inventoryManager.
   */
  public StoreInventoryManager getInventoryManager() {
    return mInventoryManager;
  }

  /**
   * @param pInventoryManager - the inventoryManager to set.
   */
  public void setInventoryManager(StoreInventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }

  /**
   * Determines if the item is preorderable.
   * @param pRequest - http request
   * @param pResponse - http response
   * @throws ServletException if an error occurs
   * @throws IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    boolean handled = false;
    Object productParam = pRequest.getObjectParameter(PRODUCT);
    Object skuIdParam = pRequest.getObjectParameter(SKU_ID);

    // Check for valid input
    if (productParam == null) {
      if (isLoggingDebug()) {
        logDebug("MISSING PARAM: no product repository item supplied");
      }

      return;
    } else if (!(productParam instanceof RepositoryItem)) {
      if (isLoggingDebug()) {
        logDebug("INCORRECT PARAM: product argument not a repository item");
      }

      return;
    }

    if (skuIdParam == null) {
      if (isLoggingDebug()) {
        logDebug("MISSING PARAM: no sku id supplied");
      }

      return;
    } else if (!(skuIdParam instanceof String)) {
      if (isLoggingDebug()) {
        logDebug("INCORRECT PARAM: sku id argument is not a string");
      }

      return;
    }

    StoreInventoryManager invManager = getInventoryManager();

    // Call InventoryManager to do all the work and convert results into correctly rendered output
    try {
      int availability = invManager.queryAvailabilityStatus((RepositoryItem) productParam, (String) skuIdParam);

      if (availability == invManager.getAvailabilityStatusInStockValue()) {
        handled = pRequest.serviceLocalParameter(OPARAM_OUTPUT_AVAILABLE, pRequest, pResponse);
      } else if (availability == invManager.getAvailabilityStatusBackorderableValue()) {
        // For store, do not report a backorder availability date that has already passed
        Date backorderAvailabilityDate = invManager.getBackorderAvailabilityDate((String) skuIdParam);

        if (backorderAvailabilityDate != null) {
          if (backorderAvailabilityDate.before(new Date())) {
            backorderAvailabilityDate = null;
          }
        }

        pRequest.setParameter(AVAILABILITY_DATE, backorderAvailabilityDate);
        handled = pRequest.serviceLocalParameter(OPARAM_OUTPUT_BACKORDERABLE, pRequest, pResponse);
      } else if (availability == invManager.getAvailabilityStatusPreorderableValue()) {
        pRequest.setParameter(AVAILABILITY_DATE, invManager.getPreorderAvailabilityDate((RepositoryItem) productParam));
        handled = pRequest.serviceLocalParameter(OPARAM_OUTPUT_PREORDERABLE, pRequest, pResponse);
      } else {
        // For store, default everything else to unavailable
        handled = pRequest.serviceLocalParameter(OPARAM_OUTPUT_UNAVAILABLE, pRequest, pResponse);
      }
    } catch (InventoryException ie) {
      handled = pRequest.serviceLocalParameter(OPARAM_OUTPUT_ERROR, pRequest, pResponse);
    }

    if (!handled) {
      handled = pRequest.serviceLocalParameter(OPARAM_OUTPUT_DEFAULT, pRequest, pResponse);
    }
  }
}
