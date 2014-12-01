/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2010 Art Technology Group, Inc.
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

package atg.projects.store.catalog.comparison;

import atg.commerce.catalog.comparison.ProductComparisonList;
import atg.commerce.catalog.comparison.ProductListHandler;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import java.io.IOException;
import javax.servlet.ServletException;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;


/**
 * Extension to the atg.commerce.catalog.comparison.ProductListHandler to obtain property
 * values from the request parameter if not supplied via a form submission.
 *
 * @see atg.commerce.catalog.comparison.ProductListHandler
 *
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/catalog/comparison/StoreProductListHandler.java#3 $
 * @updated $ $$ $
 */
public class StoreProductListHandler extends ProductListHandler
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/catalog/comparison/StoreProductListHandler.java#3 $$Change: 635816 $";

  /**
   * Request parameter ID constant values
   */
  public static final String CATEGORY_ID = "categoryID";
  public static final String PRODUCT_ID = "productID";
  public static final String SKU_ID = "skuID";

  /**
   * Overrides ProductListHandler.preAddProduct()
   * This method is called just before adding a product to the list.
   * Attempts to retrieve category id/sku id/product id from the request parameter if
   * these properties are currently unset.
   *
   * @param pRequest the dynamo request object
   * @param pResponse the dynamo response object
   * @exception ServletException if something went wrong
   * @exception IOException if something went wrong
   **/
  public void preAddProduct(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
  throws ServletException, IOException {
    super.preAddProduct( pRequest, pResponse );
	
	vlogDebug("Passed productID is {0}.",  getProductID() );
	vlogDebug("Passed categoryID is {0}.",  getCategoryID() );
	vlogDebug("Passed skuID is {0}.",  getSkuID() );
	
	//If the product id is blank then put the category id or then the sku id in the product id
	if (StringUtils.isBlank(getProductID())) {
		if (!StringUtils.isBlank(getCategoryID())) {
			setProductID(getCategoryID());
		}

		if (!StringUtils.isBlank(getSkuID())) {
			setProductID(getSkuID());
        }
    }
  }


  /**
   * Add the product specified by <code>productID</code> to the product
   * comparison list, applying the optional category and sku information
   * in <code>categoryID</code> and <code>skuID</code>.
   *
   * @param pRequest the dynamo request object
   * @param pResponse the dynamo response object
   * @exception ServletException if something went wrong
   * @exception IOException if something went wrong
   */
  protected void addProduct(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    ProductComparisonList list = getProductList();
    String productID = getProductID();

    if (list == null) {
      String errorCode = "noProductList";
      addFormException(new DropletException(getUserMessage(errorCode, pRequest), errorCode));
    }
    if (StringUtils.isBlank(productID)) {
      String errorCode = "noProductId";
      addFormException(new DropletException(getUserMessage(errorCode, pRequest), errorCode));
    }

    if (! checkFormRedirect(null, getAddProductErrorURL(), pRequest, pResponse))
      return;

    try {
      list.add(productID, getCategoryID(), getSkuID(), getRepositoryKey(), getSiteID());
    }
    catch (RepositoryException re) {
      String errorCode = "errorAddingProduct";
      addFormException(new DropletException(getUserMessage(errorCode, pRequest), re, errorCode));
    }
  }

  /**
   * Overrides ProductListHandler.handleAddProduct()
   * Adds the product to the product comparison comparison list.
   *
   * @see ProductComparisonList#add(String,String,String,String) ProductComparisonList.add()
   *
   * @param pRequest the dynamo request object
   * @param pResponse the dynamo response object
   * @exception ServletException if something went wrong
   * @exception IOException if something went wrong
   **/
  public boolean handleAddProduct(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
      try{
      //If any form errors found, redirect to error URL:
      if (! checkFormRedirect(null, getAddProductErrorURL(), pRequest, pResponse))
        return false;

      preAddProduct(pRequest, pResponse);

      addProduct(pRequest, pResponse);

      postAddProduct(pRequest, pResponse);
    }
    catch (Exception exc) {
      String errorCode = "errorAddingProduct";
      addFormException(new DropletException(getUserMessage(errorCode, pRequest), exc, errorCode));
    }

    return checkFormRedirect (getAddProductSuccessURL(), getAddProductErrorURL(), pRequest, pResponse);
  }
}
