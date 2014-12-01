/*<ATGCOPYRIGHT>
 * Copyright (C) 2010-2011 Art Technology Group, Inc.
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.inventory.InventoryException;
import atg.commerce.inventory.InventoryManager;
import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.projects.store.catalog.StoreCatalogProperties;
import atg.projects.store.catalog.StoreCatalogTools;
import atg.projects.store.inventory.StoreInventoryManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class WoodFinishDroplet extends DynamoServlet {
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/droplet/WoodFinishDroplet.java#2 $$Change: 651448 $";

  /** The input parameter name for the currently selected wood finish. */
  public static final ParameterName SELECTEDWOODFINISH = ParameterName.getParameterName("selectedWoodFinish");

  /** The input parameter name for the skus. */
  public static final ParameterName SKUS = ParameterName.getParameterName("skus");

  /** The input parameter name for the product item. */
  public static final ParameterName PRODUCT = ParameterName.getParameterName("product");

  /** The oparam name rendered once during processing.*/
  public static final String OPARAM_OUTPUT = "output";

  /** The output parameter that contains the current selected sku (based on the wood finish selection).*/
  public static final String OUTPUT_SELECTED_SKU = "selectedSku";

  /** The output parameter that contains the list of wood finish objects.*/
  public static final String OUTPUT_WOODFINISHES = "woodFinishes";

  /** The state value that means the item is currently selected. */
  public static final String STATE_SELECTED = "selected";

  /** The state value that means the item is currently available. */
  public static final String STATE_AVAILABLE = "available";

  /** The state value that means the item is currently out of stock. */
  public static final String STATE_OUTOFSTOCK = "outofstock";

  /** The state value that means the item is currently out of stock, but can be backordered. */
  public static final String STATE_BACKORDERABLE = "backorderable";

  /** The state value that means the item is currently not offered. */
  public static final String STATE_NOTOFFERED = "notoffered";

  /** The state value that means the item is currently not offered, but can be preordered. */
  public static final String STATE_PREORDERABLE = "preorderable";

  /**
   * Inventory manager.
   */
  InventoryManager mInventoryManager = null;

  /**
   * Catalog tools.
   */
  StoreCatalogTools mCatalogTools;

  /**
   * @return the InventoryManager.
   */
  public InventoryManager getInventoryManager() {
    return mInventoryManager;
  }

  /**
   * Sets the InventoryManager.
   * @param pInventoryManager - inventory manager
   */
  public void setInventoryManager(InventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }

  /**
   * @return the catalog tools.
   */
  public StoreCatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  /**
   * @param pCatalogTools - The catalogTools to set.
   */
  public void setCatalogTools(StoreCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  /**
   * Generates WoodFinish objects for a collection of product skus.
   * @param pRequest - http request
   * @param pResponse - http response
   * @throws ServletException if an error occurs
   * @throws IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    Object skusparam = pRequest.getObjectParameter(SKUS);

    if ((skusparam == null) || !(skusparam instanceof Collection)) {
      if (isLoggingDebug()) {
        logDebug("MISSING PARAM: no skus repository item supplied");
      }

      return;
    }

    Object productparam = pRequest.getObjectParameter(PRODUCT);

    if ((productparam != null) && !(productparam instanceof RepositoryItem)) {
      if (isLoggingDebug()) {
        logDebug("INCORRECT PARAM: product argument not a repository item");
      }

      return;
    }

    StoreCatalogTools catalogTools = getCatalogTools();
    String selectedWoodFinish = pRequest.getParameter(SELECTEDWOODFINISH);
    StoreCatalogProperties catalogProps = (StoreCatalogProperties) catalogTools.getCatalogProperties();
    List woodFinishes = catalogTools.getPossibleValuesForSkus((Collection) skusparam, catalogProps.getWoodFinishPropertyName());
    


    //if there's only one woodFinish and nothing is selected, pre-select the woodFinish
    if (StringUtils.isBlank(selectedWoodFinish)) {
      if (woodFinishes.size() == 1) {
        selectedWoodFinish = (String) woodFinishes.get(0);
        pRequest.setParameter(SELECTEDWOODFINISH.getName(), selectedWoodFinish);
      }
    }

    if (!StringUtils.isBlank(selectedWoodFinish)) {
      RepositoryItem selectedSku = catalogTools.findSku((Collection) skusparam, selectedWoodFinish);

      if (selectedSku != null) {
        pRequest.setParameter(OUTPUT_SELECTED_SKU, selectedSku);
      }
    }

    woodFinishes = catalogTools.sortWoodFinishes(woodFinishes);

    Map colorSwatches = catalogTools.getPossibleWoodColorSwatches((Collection) skusparam);

    //iterate through the woodFinishes
    Iterator woodFinishator = woodFinishes.iterator();
    String woodFinish;
    String woodFinishesState;
    ArrayList allWoodFinishes = new ArrayList(woodFinishes.size());

    while (woodFinishator.hasNext()) {
      woodFinish = (String) woodFinishator.next();
      woodFinishesState = getStateForWoodFinish((Collection) skusparam, (RepositoryItem) productparam, woodFinish, selectedWoodFinish);
      allWoodFinishes.add(new WoodFinish(woodFinish, (RepositoryItem) colorSwatches.get(woodFinish), woodFinishesState));
    }
    pRequest.setParameter(OUTPUT_WOODFINISHES, allWoodFinishes);


    pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
  }

  /**
   * Returns the display state for wood finish.
   * @param pChildSkus the product's child skus
   * @param pWoodFinish the size
   * @param pCurrentSelectedWoodFinish the size that is current selected
   * @param pProduct - product
   * @see #STATE_AVAILABLE
   * @see #STATE_NOTOFFERED
   * @see #STATE_PREORDERABLE
   * @see #STATE_OUTOFSTOCK
   * @see #STATE_BACKORDERABLE
   * @see #STATE_SELECTED
   * @return a display state
   */
  protected String getStateForWoodFinish(Collection pChildSkus, RepositoryItem pProduct, String pWoodFinish,
    String pCurrentSelectedWoodFinish) {
    //everything is available unless determined otherwise 
    String materialState = STATE_AVAILABLE;

    if (!StringUtils.isBlank(pCurrentSelectedWoodFinish) && pCurrentSelectedWoodFinish.equals(pWoodFinish)) {
      materialState = STATE_SELECTED;
    }else if (!StringUtils.isBlank(pWoodFinish)) {
      //we must get the sku and check inventory
      RepositoryItem sku = getCatalogTools().findSku(pChildSkus, pWoodFinish);

      if (sku == null) {
        materialState = STATE_NOTOFFERED;
      } else {
        materialState = getStateBasedOnInventory(sku.getRepositoryId(), pProduct);
      }
    }

    return materialState;
  }


  /**
   * Return the availabilty state based on the inventory state of the sku.
   * @param pSkuId - sku id
   * @param pProduct - product
   * @return STATE_AVAILABLE is item is in stock. Otherwise, STATE_OUTOFSTOCK,
   * STATE_PREORDERABLE or STATE_BACKORDERABLE.
   */
  protected String getStateBasedOnInventory(String pSkuId, RepositoryItem pProduct) {
    try {
      int availabilityStatus;
      InventoryManager invManager = getInventoryManager();

      if ((invManager instanceof StoreInventoryManager) && (pProduct != null)) {
        availabilityStatus = ((StoreInventoryManager) invManager).queryAvailabilityStatus(pProduct, pSkuId);
      } else {
        availabilityStatus = invManager.queryAvailabilityStatus(pSkuId);
      }

      if (availabilityStatus == InventoryManager.AVAILABILITY_STATUS_BACKORDERABLE) {
        return STATE_BACKORDERABLE;
      } else if (availabilityStatus == InventoryManager.AVAILABILITY_STATUS_PREORDERABLE) {
        return STATE_PREORDERABLE;
      } else if (availabilityStatus != InventoryManager.AVAILABILITY_STATUS_IN_STOCK) {
        return STATE_OUTOFSTOCK;
      }
    } catch (InventoryException e) {
      if (isLoggingError()) {
        logError(e);
      }
    }

    return STATE_AVAILABLE;
  }

  /**
   * Class that represents a wood finish state.
   *
   */
  public class WoodFinish {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/droplet/WoodFinishDroplet.java#2 $$Change: 651448 $";

    String mState;
    RepositoryItem mSwatch;
    String mName;

    /**
     * Constructor.
     * @param name - wood finish name
     * @param swatch - swatch
     * @param state - state
     */
    WoodFinish(String name, RepositoryItem swatch, String state) {
      mState = state;
      mSwatch = swatch;
      mName = name;
    }

    /**
     * @return the state.
     */
    public String getState() {
      return mState;
    }

    /**
     * @return the swatch.
     */
    public RepositoryItem getSwatch() {
      return mSwatch;
    }

    /**
     * @return the name.
     */
    public String getName() {
      return mName;
    }
  }
}