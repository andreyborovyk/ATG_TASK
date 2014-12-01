/*<ATGCOPYRIGHT>
 * Copyright (C) 1999-2011 Art Technology Group, Inc.
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

package atg.commerce.order.processor;

import atg.repository.*;
import atg.commerce.order.*;
import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;
import atg.commerce.CommerceException;
import atg.commerce.catalog.CatalogTools;
import atg.nucleus.logging.ApplicationLoggingImpl;

import java.util.*;

/**
 * This processor sets the catalog references into the CommerceItems in the order. When an
 * Order is saved its catalogRef is not persisted, but its id is. When the Order is
 * loaded, the catalogRefId is loaded, but the catalogRef remains null. This processor
 * looks up the catalogRef in the ProductCatalog using the catalogRefId.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSetCatalogRefs.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcSetCatalogRefs extends ApplicationLoggingImpl implements PipelineProcessor {  
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSetCatalogRefs.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcSetCatalogRefs() {
  }

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The processor completed
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = {SUCCESS};
    return ret;
  } 

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcSetCatalogRefs";

  /**
   * Sets property LoggingIdentifier
   **/
  public void setLoggingIdentifier(String pLoggingIdentifier) {
    mLoggingIdentifier = pLoggingIdentifier;
  }

  /**
   * Returns property LoggingIdentifier
   **/
  public String getLoggingIdentifier() {
    return mLoggingIdentifier;
  }

  //-----------------------------------------------
  /**
   * This method sets the catalog references into the CommerceItems in the order. When an
   * Order is saved its catalogRef is not persisted, but its id is. When the Order is
   * loaded, the catalogRefId is loaded, but the catalogRef remains null. This processor
   * takes the looks up the catalogRef in the ProductCatalog using the catalogRefId.
   *
   * This method requires that an Order and a CatalogTools object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order and a CatalogTools object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    CatalogTools catalogTools = (CatalogTools) map.get(PipelineConstants.CATALOGTOOLS);

    // check for null parameters
    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (catalogTools == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidCatalogToolsParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));

    if (order.getCommerceItemCount() == 0)
      return SUCCESS;
      
    loadCatalogRef(order,catalogTools);
    
    return SUCCESS;
  }

  private boolean mSubstituteRemovedSku = true;
  
  public void setSubstituteRemovedSku(boolean pSubstituteRemovedSku) {
    mSubstituteRemovedSku = pSubstituteRemovedSku;
  }

  /**
   * The property is checked to validate whether a CommerceItem that contains a sku which has
   * since been removed from the repository should be substituted with another sku that
   * represents the deleted sku..
   *
   * @return true if the processor should deal with removed skus, false otherwise.
   **/
  public boolean isSubstituteRemovedSku() {
    return mSubstituteRemovedSku;
  }

  //---------------------------------------------------------------------------
  // property: SubstituteDeletedSkuId
  //---------------------------------------------------------------------------

  String mSubstituteDeletedSkuId=null;

  public void setSubstituteDeletedSkuId(String pSubstituteDeletedSkuId) {
    mSubstituteDeletedSkuId = pSubstituteDeletedSkuId;
  }

  /**
   * This property specifies the id of the sku that will represent all deleted skus.
   * @return the sku id representing deleted skus
   **/
  public String getSubstituteDeletedSkuId() {
    return mSubstituteDeletedSkuId;
  }


  /**
   * This method will return the RepositoryItem that represents all deleted skus.  It uses @see
   * SubstituteDeletedSkuId to lookup the sku which should be used.
   * @param pCatalogTools the catalog tools that contain the catalog repository
   * @param pCatalogKey the catalog key that was being used to lookup the original sku id.
   * @param pOriginalSkuId the sku id for the item that was initially removed.
   * @return the RepositoryItem which contains information explaining that a sku has been
   * deleted, null if the sku is not found.
   **/
  public RepositoryItem retrieveDeletedSkuItem(CatalogTools pCatalogTools,
                                               String pCatalogKey,
                                               String pOriginalSkuId) {
    RepositoryItem ri = null;
    try {
      if (isLoggingDebug())
        logDebug("Looking for deleted sku: " + getSubstituteDeletedSkuId());
      
      // If we are dealing with a custom catalog there is a slightly different call to be made
      
      if (pCatalogTools instanceof atg.commerce.catalog.custom.CustomCatalogTools) {
        ri = ((atg.commerce.catalog.custom.CustomCatalogTools) pCatalogTools).
          findSKU(getSubstituteDeletedSkuId(), pCatalogKey, false);

        // If we didn't find it initially, it is possible we were looking in the wrong catalog,
        // try again with the default catalog.
        if (ri == null) {
          if (isLoggingDebug()) 
            logDebug("No item was returned with CatalogKey: " + pCatalogKey);

          // Try it again with a null catalog key
          ri = ((atg.commerce.catalog.custom.CustomCatalogTools) pCatalogTools).
            findSKU(getSubstituteDeletedSkuId(), null, false);
          
          if (isLoggingDebug()) {
            if (ri == null) {
              logDebug("No item was returned with null CatalogKey ");
            }
            else {
              logDebug("An item was returned");
            }
          }
        }
        else {
          if (isLoggingDebug())
            logDebug("An item was returned");
        }
        
      }
      else {
        // Searching in the standard catalog.
        
        ri = pCatalogTools.findSKU(getSubstituteDeletedSkuId(), pCatalogKey);
        
        if (ri == null) {
          if (isLoggingDebug()) 
            logDebug("No item was returned with CatalogKey: " + pCatalogKey);

          // Try it again with a null key (default catalog) since it is possible it is not in
          // the catalog key specified initially.
          
          ri = pCatalogTools.findSKU(getSubstituteDeletedSkuId(), null);
          if (isLoggingDebug()) {
            if (ri == null) {
              logDebug("No item was returned with null CatalogKey ");
            }
            else {
              logDebug("An item was returned");
            }
          }
        }
        else {
          if (isLoggingDebug())
            logDebug("An item was returned");
        }
        
      }


      return ri;
    }
    catch (RepositoryException re) {
      logError(re);
    }
    return null;
  }
  
  protected void loadCatalogRef(CommerceItemContainer order, CatalogTools catalogTools)
      throws RepositoryException,CommerceException
  {
    RepositoryItem item;
    CommerceItem ci;

    Iterator iter = order.getCommerceItems().iterator();
    while (iter.hasNext()) {
      ci = (CommerceItem) iter.next();
      if (catalogTools instanceof atg.commerce.catalog.custom.CustomCatalogTools)
        item = ((atg.commerce.catalog.custom.CustomCatalogTools) catalogTools).findSKU(ci.getCatalogRefId(), ci.getCatalogKey(), false);
      else
        item = catalogTools.findSKU(ci.getCatalogRefId(), ci.getCatalogKey());

      if (item == null && isSubstituteRemovedSku())
        item = retrieveDeletedSkuItem(catalogTools, ci.getCatalogKey(), ci.getCatalogRefId());
      
      if (item == null) {
        String[] msgArgs = { ci.getId(), ci.getCatalogRefId() };
        throw new CommerceException(ResourceUtils.getMsgResource("CantFindAllProductSKUs",
                                      MY_RESOURCE_NAME, sResourceBundle, msgArgs));
      }
      ci.getAuxiliaryData().setCatalogRef(item);
	        
      if(ci instanceof CommerceItemContainer)
	  {
	      if (isLoggingDebug())
		logDebug("Proc Set Catalog Refs loading aux data for"+ci);
	      loadCatalogRef((CommerceItemContainer)ci, catalogTools);
	  }
    }
  }
}
