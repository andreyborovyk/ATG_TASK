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

package atg.commerce.inventory;

import atg.servlet.*;
import atg.nucleus.naming.ParameterName;
import atg.core.util.NumberTable;
import atg.droplet.DropletException;

import java.util.Date;
import java.text.MessageFormat;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import atg.nucleus.logging.ApplicationLogging;

/**
 * This Dynamo Servlet Bean is an interface into the Inventory management services.
 * A single global instance is defined with the Nucleus name 
 * <code>/atg/commerce/inventory/InventoryLookup</code>. This service is intended to 
 * be configured with both a caching and non-caching InventoryManager. Therefore with
 * this one service a developer can easily show both cached and uncached inventory 
 * information. The <code>useCache</code> property defines the default InventoryManager
 * to use when the servlet is invoked.  By default this value is true. This can be overrode
 * by setting the servlet param <i>useCache</i> to <code>true</code> or <code>false</code>.
 * <P>
 * This servlet requires a single parameter to be set, <i>itemId</i>. This should be the
 * identifier that will be passed through to the InventoryManager services.  Typically this would
 * be a SKU id.
 * <P>
 * Using this id the servlet uses an InventoryManager to set an inventory information object
 * that includes several properties.  It is inside the
 * <i>output</i> oparam.  The parameters are as follows:
 * <DL>
 *
 * <DT> inventoryInfo
 * <DD> An information object that includes property values for the inventory item
 * with the given id.
 * <DL>
 * <DT>inventoryInfo.availabilityStatus
 * <DD>A numeric status code that defines the current inventory state/
 * The Dynamo inventory system defines the following codes:
 * <UL>
 * <LI>1000 <i>constant InventoryManager.AVAILABILITY_STATUS_IN_STOCK</i> -> In Stock
 * <LI>1001 <i>constant InventoryManager.AVAILABILITY_STATUS_OUT_OF_STOCK</i> -> Out of Stock
 * <LI>1002 <i>constant InventoryManager.AVAILABILITY_STATUS_PREORDERABLE</i> -> Preorderable
 * <LI>1003 <i>constant InventoryManager.AVAILABILITY_STATUS_BACKORDERABLE</i> -> Backorderable
 * <LI>1005 <i>constant InventoryManager.AVAILABILITY_STATUS_DISCONTINUED</i> -> Discontinued
 * </UL>
 *
 * <DT>inventoryInfo.availabilityStatusMsg
 * <DD>A texual representation that maps to the numeric <i>availabilityStatus</i> code. By default
 * this mapping is as follows:
 * <UL>
 * <LI>1000 -> "INSTOCK"
 * <LI>1001 -> "OUTOFSTOCK"
 * <LI>1002 -> "PREORDERABLE"
 * <LI>1003 -> "BACKORDERABLE"
 * <LI>1005 -> "DISCONTINUED"
 * </UL>
 * 
 * <DT>inventoryInfo.availabilityDate
 * <DD>If the product is not in stock, then this indicates a date when the item will be available
 *
 * <DT>inventoryInfo.stockLevel
 * <DD>The total number of units that are currently in stock for the item
 *
 * <DT>inventoryInfo.preorderLevel
 * <DD>The total number of units that are available for preorder
 *
 * <DT>inventoryInfo.backorderLevel
 * <DD>The total number of units that are available for backorder
 *
 * <DT>inventoryInfo.stockThreshold
 * <DD>The threshold for the stock level.
 *
 * <DT>inventoryInfo.preorderThreshold
 * <DD>The threshold for the preorder level.
 *
 * <DT>inventoryInfo.backorderThreshold
 * <DD>The threshold for the backorder level.
 * </DL>
 * <DT>error
 * <DD>Any exception that may have occured while looking up the inventory information
 *
 * </DL>
 *
 * @see InventoryManager
 * @author Bob Mason
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/inventory/InventoryDroplet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public 
class InventoryDroplet
extends DynamoServlet
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/inventory/InventoryDroplet.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------

  static final ParameterName ITEM_ID = ParameterName.getParameterName("itemId");
  static final ParameterName USECACHE = ParameterName.getParameterName("useCache");

  static final ParameterName OUTPUT = ParameterName.getParameterName("output");
  static final String INVENTORY_INFO = "inventoryInfo";

  static final String ERROR = "error";

  //-------------------------------------
  // Member Variables
  //-------------------------------------

  //-------------------------------------
  // Properties
  //-------------------------------------


  //---------------------------------------------------------------------------
  // property: logMissingInventoryExceptionsAsError

  /** 
   * Whether inventory exceptions should be logged as an error.
   * If they are not logged as error, they will be logged on debug
   * level.  This is used typically to avoid missing items in the inventory
   * from being logged as errors.  Set to false by default.
   */
  boolean mLogMissingInventoryExceptionsAsError = false;

  /**
   * Set the logMissingInventoryExceptionsAsError property.
   */
  public void setLogMissingInventoryExceptionsAsError(boolean pLogMissingInventoryExceptionsAsError) {
    mLogMissingInventoryExceptionsAsError = pLogMissingInventoryExceptionsAsError;
  }

  /**
   * Return the logMissingInventoryExceptionsAsError property.
   */
  public boolean isLogMissingInventoryExceptionsAsError() {
    return mLogMissingInventoryExceptionsAsError;
  }

  //-------------------------------------
  // property: UseCache
  boolean mUseCache = true;

  /**
   * Sets property UseCache
   **/
  public void setUseCache(boolean pUseCache) {
    mUseCache = pUseCache;
  }

  /**
   * Returns property UseCache
   **/
  public boolean isUseCache() {
    return mUseCache;
  }
  
  //-------------------------------------
  // property: CachingInventoryManager
  InventoryManager mCachingInventoryManager;

  /**
   * Sets property CachingInventoryManager
   **/
  public void setCachingInventoryManager(InventoryManager pCachingInventoryManager) {
    mCachingInventoryManager = pCachingInventoryManager;
  }

  /**
   * Returns property CachingInventoryManager
   **/
  public InventoryManager getCachingInventoryManager() {
    return mCachingInventoryManager;
  }
  
  //-------------------------------------
  // property: InventoryManager
  InventoryManager mInventoryManager;

  /**
   * Sets property InventoryManager
   **/
  public void setInventoryManager(InventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }

  /**
   * Returns property InventoryManager
   **/
  public InventoryManager getInventoryManager() {
    return mInventoryManager;
  }
  

  //---------------------------------------------------------------------------
  // property:UseLocalizedResourceStrings
  //---------------------------------------------------------------------------

  private boolean mUseLocalizedResourceStrings = false;
  public void setUseLocalizedResourceStrings(boolean pUseLocalizedResourceStrings) {
    mUseLocalizedResourceStrings = pUseLocalizedResourceStrings;
  }

  /**
   * If this is true, then availability status messages are localized.
   * This will have a slight performance hit so if you are only
   * providing inventory status in a single language, this should be
   * false.
   **/
  public boolean isUseLocalizedResourceStrings() {
    return mUseLocalizedResourceStrings;
  }

  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instanceof InventoryDroplet
   */
  public InventoryDroplet() {
  }

  public void service(DynamoHttpServletRequest pRequest, 
                      DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    InventoryManager inventoryManager = getInventoryManager();

    String cached = pRequest.getParameter(USECACHE);
    if (((cached == null) && (isUseCache())) ||
        ((cached != null) && (cached.equalsIgnoreCase("true")))) {
      inventoryManager = getCachingInventoryManager();
      if (isLoggingDebug())
        logDebug("Using CachingInventoryManager");
    }

    if (inventoryManager != null) { 
      Exception error = null;
      String id = pRequest.getParameter(ITEM_ID);
      if (isLoggingDebug())
        logDebug("Looking up inventory info for \"" + id + "\"");
      if (id != null) {
        if (isLogMissingInventoryExceptionsAsError()) {
          try {
            // just to make sure the item exists.
            long stockLevel = inventoryManager.queryStockLevel(id);
            InventoryInfo info = createInventoryInfo(id, inventoryManager, this);
            info.setLogMissingInventoryExceptionsAsError(true);
            pRequest.setParameter(INVENTORY_INFO, info);
          }
          catch (InventoryException exc) {
            error = exc;
          }
        }
        else {
          InventoryInfo info = createInventoryInfo(id, inventoryManager, this);
          info.setLogMissingInventoryExceptionsAsError(false);
          pRequest.setParameter(INVENTORY_INFO, info);
        }
      }
      else {
        String msg = MessageFormat.format(Constants.NO_SUCH_ITEM,new Object[] {id});
        error = new MissingInventoryItemException(msg);
      }

      if ((error != null) && (isLoggingError()))
        logError(error);

      pRequest.setParameter(ERROR, error);
      pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }
  }

  /**
   * Create a new InventoryInfo object
   **/
  private InventoryInfo createInventoryInfo(String pInventoryId, 
                                            InventoryManager pInventoryManager,
                                            ApplicationLogging pLogging)
  {
    if(isUseLocalizedResourceStrings())
      return new LocalizedInventoryInfo(pInventoryId, pInventoryManager, pLogging);
    else
      return new InventoryInfo(pInventoryId, pInventoryManager, pLogging);
  }
} // end of class
