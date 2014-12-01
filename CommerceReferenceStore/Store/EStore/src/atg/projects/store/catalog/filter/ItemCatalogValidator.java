/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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

package atg.projects.store.catalog.filter;

import atg.commerce.catalog.custom.CustomCatalogTools;

import atg.nucleus.GenericService;

import atg.repository.RepositoryItem;
import atg.service.collections.validator.CollectionObjectValidator;


/**
 * This validator validates if a product is in the user's catalog.
 *
 * @author ATG
 * @version $Revision: #2 $
 */
public class ItemCatalogValidator extends GenericService implements CollectionObjectValidator {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/catalog/filter/ItemCatalogValidator.java#2 $$Change: 651448 $";

  /**
   * Catalog tools.
   */
  private CustomCatalogTools mCatalogTools;

  /**
   * @param pCatalogTools - catalog tools.
   */
  public void setCatalogTools(CustomCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  /**
   * @return catalog tools.
   */
  public CustomCatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  /**
   * Uses CustomCatalogTools to ensure the product is in the current user's
   * catalog.
   *
   * @param pObject - object to validate
   * @return true if the product is in the current user's
   * catalog, false - otherwise
   */
  public boolean validateObject(Object pObject) {
    if (!(pObject instanceof RepositoryItem) ) {
      return false;
    }
    
    RepositoryItem product = (RepositoryItem) pObject;
    CustomCatalogTools ct = getCatalogTools();

    boolean valid = ct.verifyCatalog(product);

    if (isLoggingDebug()) {
      logDebug("Product " + product + " in catalog = " + valid);
    }

    return valid;
  }
}
