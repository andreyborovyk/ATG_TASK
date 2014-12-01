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
 * This processor sets the product references into the CommerceItems in the order. When an
 * Order is saved its productRef is not persisted, but its id is. When the Order is
 * loaded, the productRefId is loaded, but the productRef remains null. This processor
 * looks up the productRef in the ProductCatalog using the productRefId.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSetProductRefs.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcSetProductRefs extends ApplicationLoggingImpl implements PipelineProcessor {  
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSetProductRefs.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcSetProductRefs() {
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
  String mLoggingIdentifier = "ProcSetProductRefs";

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

  //-------------------------------------
  // property: errorOnNullProductRef
  //-------------------------------------
  private boolean mErrorOnNullProductRef = false;

  /**
   * Sets property ErrorOnNullProductRef
   **/
  public void setErrorOnNullProductRef(boolean pErrorOnNullProductRef) {
    mErrorOnNullProductRef = pErrorOnNullProductRef;
  }

  /**
   * Returns property ErrorOnNullProductRef
   **/
  public boolean getErrorOnNullProductRef() {
    return mErrorOnNullProductRef;
  }


  //---------------------------------------------------------------------------
  // property: SubstituteRemovedProduct
  //---------------------------------------------------------------------------

  boolean mSubstituteRemovedProduct;

  public void setSubstituteRemovedProduct(boolean pSubstituteRemovedProduct) {
    mSubstituteRemovedProduct = pSubstituteRemovedProduct;
  }

  /**
   * The property is checked to validate whether a CommerceItem that contains a product which
   * has since been removed from the repository should be substituted with another product that
   * represented the deleted product.
   *
   * @return true if the processor should deal with the removed product, false otherwise.
   **/
  public boolean isSubstituteRemovedProduct() {
    return mSubstituteRemovedProduct;
  }


  //---------------------------------------------------------------------------
  // property: SubstituteDeletedProductId
  //---------------------------------------------------------------------------

  String mSubstituteDeletedProductId;

  public void setSubstituteDeletedProductId(String pSubstituteDeletedProductId) {
    mSubstituteDeletedProductId = pSubstituteDeletedProductId;
  }

  /**
   * This property specifies the id of the product that will represent all deleted products.
   * @return the product id representing the deleted products
   **/
  public String getSubstituteDeletedProductId() {
    return mSubstituteDeletedProductId;
  }
  
  //-----------------------------------------------
  /**
   * This method sets the product references into the CommerceItems in the order. When an
   * Order is saved its productRef is not persisted, but its id is. When the Order is
   * loaded, the productRefId is loaded, but the productRef remains null. This processor
   * takes the looks up the productRef in the ProductCatalog using the productRefId.
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
      
    loadProductRef(order, catalogTools);

    return SUCCESS;
  }


  /**
   * This method will return the RepositoryItem that represents all deleted products.  It uses @see
   * SubstituteDeletedProductId to lookup the product which should be used.
   * @param pCatalogTools the catalog tools that contain the catalog repository
   * @param pCatalogKey the catalog key that was being used to lookup the original product id.
   * @param pOriginalProductId the product id for the item that was initially removed.
   * @return the RepositoryItem which contains information explaining that a product has been
   * deleted, null if the product is not found.
   **/
  public RepositoryItem retrieveDeletedProductItem(CatalogTools pCatalogTools,
                                                   String pCatalogKey,
                                                   String pOriginalProductId) {
    RepositoryItem ri = null;
    try {
      if (isLoggingDebug())
        logDebug("Looking for deleted product for item: " + pOriginalProductId);
      
      // If we are dealing with a custom catalog there is a slightly different call to be made
      
      if (pCatalogTools instanceof atg.commerce.catalog.custom.CustomCatalogTools) {
        ri = ((atg.commerce.catalog.custom.CustomCatalogTools) pCatalogTools).
          findProduct(getSubstituteDeletedProductId(), pCatalogKey, false);

        // If we didn't find it initially, it is possible we were looking in the wrong catalog,
        // try again with the default catalog.
        if (ri == null) {
          if (isLoggingDebug()) 
            logDebug("No item was returned with CatalogKey: " + pCatalogKey);

          // Try it again with a null catalog key
          ri = ((atg.commerce.catalog.custom.CustomCatalogTools) pCatalogTools).
            findProduct(getSubstituteDeletedProductId(), null, false);
          
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
        
        ri = pCatalogTools.findProduct(getSubstituteDeletedProductId(), pCatalogKey);
        
        if (ri == null) {
          if (isLoggingDebug()) 
            logDebug("No item was returned with CatalogKey: " + pCatalogKey);

          // Try it again with a null key (default catalog) since it is possible it is not in
          // the catalog key specified initially.
          
          ri = pCatalogTools.findProduct(getSubstituteDeletedProductId(), null);
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

  protected void loadProductRef(CommerceItemContainer order, CatalogTools catalogTools)
      throws RepositoryException,CommerceException
  {
    RepositoryItem item;
    CommerceItem ci;
      
    Iterator iter = order.getCommerceItems().iterator();
    while (iter.hasNext()) {
      ci = (CommerceItem) iter.next();
      if (catalogTools instanceof atg.commerce.catalog.custom.CustomCatalogTools)
        item = ((atg.commerce.catalog.custom.CustomCatalogTools) catalogTools).findProduct(ci.getAuxiliaryData().getProductId(), ci.getCatalogKey(), false);
      else
        item = catalogTools.findProduct(ci.getAuxiliaryData().getProductId(), ci.getCatalogKey());

      if (item == null && isSubstituteRemovedProduct())
        item = retrieveDeletedProductItem(catalogTools, ci.getCatalogKey(), ci.getAuxiliaryData().getProductId());
      
      if (getErrorOnNullProductRef() && item == null) {
        String[] msgArgs = { ci.getId(), ci.getAuxiliaryData().getProductId() };
        throw new CommerceException(ResourceUtils.getMsgResource("CantFindAllProducts",
                                      MY_RESOURCE_NAME, sResourceBundle, msgArgs));
      }
      if (item == null && isLoggingDebug())
        logDebug("ProductRef for product " + ci.getAuxiliaryData().getProductId() + " is null");
      ci.getAuxiliaryData().setProductRef(item);

      if(ci instanceof CommerceItemContainer)
	  {
	      if (isLoggingDebug())
		logDebug("Proc Set Product Refs loading aux data for"+ci);
	      loadProductRef((CommerceItemContainer)ci,catalogTools);
	  }

    }
  }
}
