/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2010 Art Technology Group, Inc.
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
import atg.commerce.inventory.InventoryManager;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.projects.store.catalog.StoreCatalogTools;
import atg.projects.store.inventory.StoreInventoryManager;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;

import java.util.*;

import javax.servlet.ServletException;

/**
 * This droplet is used by the color and size picker to generate the available colors
 * and sizes and their current state.
 * <p>
 * It creates a list containing one Color object for each color defined by the skus and
 * a list containing one Size object for each size defined by the skus.
 * <p>
 * Each Color and Size object contains a state property, which identifies the display state of the
 * item. There are four display states:
 * <p>
 * <b>Color and Size display states:<p></b>
 * <dl>
 * <dt>selected
 * <dd>the color or size is currently selected. The currently selected size or color will have this state.
 * The droplet has two input parameters, selectedColor and selectedSize, for specifying which ones are currently
 * selected, if any.
 * <dt>available
 * <dd>the color or size is available for selection. All sizes and colors are initially created with
 * this state. Once a color or size is selected, the other colors and sizes may take on a different state.
 * <dt>outofstock
 * <dd>the color or size is currently out of stock with the its selected counter part. For instance, if size 10
 * was out of stock in black, size 10 would have this state when black was selected.
 * <dt>backorderable
 * <dd>the color or size is currently out of stock with the its selected counter part, but backorders are allowed.
 * <dt>preorderable
 * <dd>the color or size is not yet available with the its selected counter part, but preorders are allowed. For instance, if size 10
 * was not yet available in black, but was preorderable, size 10 would have this state when black was selected.
 * <dt>notoffered
 * <dd>the color or size is not offered with the its selected counter part. For instance, if size 10 was not offered
 * in black, size 10 would have this state when black was selected.
 * </dl>
 * <p>
 * <b>Input Params:<p></b>
 * <dl>
 * <dt>selectedColor
 * <dd>the currently selected color
 * <dt>selectedSize
 * <dd>the currently selected size
 * <dt>skus
 * <dd>a collection of product skus for which to generate the colors and sizes
 * <dt>product
 * <dd>an optional product repository item the skus are children of
 * </dl>
 * <b>Output params:<p></b>
 * <dl>
 * <dt>selectedSku
 * <dd>if both a size and color is currently selected, the param will contain the associated sku.
 * <dt>colors
 * <dd>a list of Color objects
 * <dt>sizes
 * <dd>a list of Size objects
 * </dl>
 * <b>Oparams:<p></b>
 * <dl>
 * <dt>output
 * <dd>rendered once upon successful completion
 * </dl>
 * <b>Color object properties:<p></b>
 * <dl>
 * <dt>name
 * <dd>the name of the color
 * <dt>state
 * <dd>the current display state of the color
 * <dt>swatch
 * <dd>the swatch media item
 * </dl>
 * <b>Size object properties:<p></b>
 * <dl>
 * <dt>name
 * <dd>the name of the size
 * <dt>state
 * <dd>the current display state for the size
 * </dl>
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/droplet/ColorSizeDroplet.java#3 $$Change: 635816 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class ColorSizeDroplet extends DynamoServlet {
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/droplet/ColorSizeDroplet.java#3 $$Change: 635816 $";

  /** The input parameter name for the currently selected color. */
  public static final ParameterName SELECTEDCOLOR = ParameterName.getParameterName("selectedColor");

  /** The input parameter name for the currently selected size. */
  public static final ParameterName SELECTEDSIZE = ParameterName.getParameterName("selectedSize");

  /** The input parameter name for the skus. */
  public static final ParameterName SKUS = ParameterName.getParameterName("skus");

  /** The input parameter name for the product item. */
  public static final ParameterName PRODUCT = ParameterName.getParameterName("product");

  /** The oparam name rendered once during processing.*/
  public static final String OPARAM_OUTPUT = "output";

  /** The output parameter that contains the current selected sku (based on the color and size selection).*/
  public static final String OUTPUT_SELECTED_SKU = "selectedSku";

  /** The output parameter that contains the list of color objects.*/
  public static final String OUTPUT_COLORS = "colors";

  /** The output parameter that contains the list of size objects.*/
  public static final String OUTPUT_SIZES = "sizes";

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
   * Generates Color and Size objects for a collection of product skus.
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
    String selectedcolor = pRequest.getParameter(SELECTEDCOLOR);
    String selectedsize = pRequest.getParameter(SELECTEDSIZE);
    List colors = catalogTools.getPossibleColors((Collection) skusparam);
    List<String> sizes = catalogTools.getSortedSizes((Collection<RepositoryItem>) skusparam);

    //if there's only one color and nothings been selected, pre-select the color
    if (StringUtils.isBlank(selectedcolor)) {
      if (colors.size() == 1) {
        selectedcolor = (String) colors.get(0);
        pRequest.setParameter(SELECTEDCOLOR.getName(), selectedcolor);
      }
    }

    //if there's only one size and nothings been selected, pre-select the size
    if (StringUtils.isBlank(selectedsize)) {
      if (sizes.size() == 1) {
        selectedsize = sizes.iterator().next();
        pRequest.setParameter(SELECTEDSIZE.getName(), selectedsize);
      }
    }

    //if both a color and size are selected, set the selected sku output param 
    if (!StringUtils.isBlank(selectedcolor) && !StringUtils.isBlank(selectedsize)) {
      RepositoryItem selectedSku = catalogTools.findSku((Collection) skusparam, selectedcolor, selectedsize);

      if (selectedSku != null) {
        pRequest.setParameter(OUTPUT_SELECTED_SKU, selectedSku);
      }
    }

    colors = catalogTools.sortColors(colors);

    Map colorSwatches = catalogTools.getPossibleColorSwatches((Collection) skusparam);

    //iterate through the colors
    Iterator colorerator = colors.iterator();
    String color;
    String colorState;
    ArrayList allcolors = new ArrayList(colors.size());

    while (colorerator.hasNext()) {
      color = (String) colorerator.next();
      colorState = getStateForColor((Collection) skusparam, (RepositoryItem) productparam, color, selectedcolor,
          selectedsize);
      allcolors.add(new Color(color, (RepositoryItem) colorSwatches.get(color), colorState));
      pRequest.setParameter(OUTPUT_COLORS, allcolors);
    }

    //iterator through the sizes
    ArrayList<Size> allSizes = new ArrayList<Size> (sizes.size());
    for (String size: sizes)
    {
      String sizeState = getStateForSize((Collection) skusparam, (RepositoryItem) productparam, size, selectedcolor, selectedsize);
      allSizes.add(new Size(size, sizeState));
    }
    pRequest.setParameter(OUTPUT_SIZES, allSizes);

    pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
  }

  /**
   * Returns the display state for a color.
   * @param pChildSkus the product's child skus
   * @param pColor the size
   * @param pCurrentSelectedColor the color that is currently selected
   * @param pCurrentSelectedSize the size that is current selected
   * @param pProduct - product
   * @see #STATE_AVAILABLE
   * @see #STATE_NOTOFFERED
   * @see #STATE_PREORDERABLE
   * @see #STATE_OUTOFSTOCK
   * @see #STATE_BACKORDERABLE
   * @see #STATE_SELECTED
   * @return a display state
   */
  protected String getStateForColor(Collection pChildSkus, RepositoryItem pProduct, String pColor,
    String pCurrentSelectedColor, String pCurrentSelectedSize) {
    //everything is available unless determined otherwise 
    String colorState = STATE_AVAILABLE;

    if (!StringUtils.isBlank(pCurrentSelectedColor) && pCurrentSelectedColor.equals(pColor)) {
      colorState = STATE_SELECTED;
    }
    //if we have a selectedsize, then the state must be determined based on that
    else if (!StringUtils.isBlank(pCurrentSelectedSize)) {
      //we must get the sku and check inventory
      RepositoryItem sku = getCatalogTools().findSku(pChildSkus, pColor, pCurrentSelectedSize);

      if (sku == null) {
        colorState = STATE_NOTOFFERED;
      } else {
        colorState = getStateBasedOnInventory(sku.getRepositoryId(), pProduct);
      }
    }

    return colorState;
  }

  /**
   * Returns the display state for a size based on the currently selected size and color.
   * @param pChildSkus the product's child skus
   * @param pSize the size
   * @param pCurrentSelectedColor the color that is currently selected
   * @param pCurrentSelectedSize the size that is current selected
   * @param pProduct - product
   * @see #STATE_AVAILABLE
   * @see #STATE_NOTOFFERED
   * @see #STATE_PREORDERABLE
   * @see #STATE_OUTOFSTOCK
   * @see #STATE_BACKORDERABLE
   * @see #STATE_SELECTED
   * @return a display state
   */
  protected String getStateForSize(Collection pChildSkus, RepositoryItem pProduct, String pSize,
    String pCurrentSelectedColor, String pCurrentSelectedSize) {
    //  everything is available unless determined otherwise 
    String sizeState = STATE_AVAILABLE;

    if (!StringUtils.isBlank(pCurrentSelectedSize) && pCurrentSelectedSize.equals(pSize)) {
      sizeState = STATE_SELECTED;
    } else if (!StringUtils.isBlank(pCurrentSelectedColor)) {
      RepositoryItem sku = getCatalogTools().findSku(pChildSkus, pCurrentSelectedColor, pSize);

      if (sku == null) {
        sizeState = STATE_NOTOFFERED;
      } else {
        sizeState = getStateBasedOnInventory(sku.getRepositoryId(), pProduct);
      }
    }

    return sizeState;
  }

  /**
   * Return the availabilty state based on the inventory state of the sku.
   * <p>
   * This is called whenever setting the state of a color or a size based on the
   * state of the sku.
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
   * Class that represents a color state.
   *
   */
  public class Color {
    String mState;
    RepositoryItem mSwatch;
    String mName;

    /**
     * Constructor.
     * @param name - color name
     * @param swatch - swatch
     * @param state - state
     */
    Color(String name, RepositoryItem swatch, String state) {
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

  /**
   * Class that represents a size state.
   */
  public class Size {
    String mState;
    String mName;

    /**
     * Constructor.
     * @param name - size name
     * @param state - state
     */
    Size(String name, String state) {
      mName = name;
      mState = state;
    }

    /**
     * @return Returns the state.
     */
    public String getState() {
      return mState;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
      return mName;
    }
  }
}
