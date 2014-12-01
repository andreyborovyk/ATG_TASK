/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2011 Art Technology Group, Inc.
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
package atg.projects.store.collections.filter;

import atg.projects.store.catalog.StoreCatalogTools;

import atg.repository.RepositoryItem;

import atg.service.collections.filter.CachedCollectionFilter;
import atg.service.collections.filter.FilterException;

import java.util.Collection;
import java.util.List;


/**
 * This filter sorts a collection of color names.
 * <p>
 * Caching should not be enabled for this filter.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/collections/filter/ColorSorter.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ColorSorter extends CachedCollectionFilter {
  /**
   * Class version string.
   */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/collections/filter/ColorSorter.java#2 $$Change: 651448 $";

  /**
   * Catalog tools.
   */
  protected StoreCatalogTools mCatalogTools;

  /**
   * @return catalog tools.
   */
  public StoreCatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  /**
   * @param pCatalogTools - catalog tools.
   */
  public void setCatalogTools(StoreCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  /**
  * Generates a context key for this filter. This method always returns <code>null</code>
  * with the assumption that caching is turned off.
  *
  * @param pUnfilteredCollection the collection to be filtered
  * @param pCollectionIdentifierKey the key that uniquely identifies the unfiltered collection
  * @param pProfile the user profile
  * @return the context key object
  */
  public Object generateContextKey(Collection pUnfilteredCollection, String pCollectionIdentifierKey,
    RepositoryItem pProfile) {
    return null;
  }

  /**
  * {@inheritDoc}
  * @return true
  */
  public boolean shouldApplyFilter(Collection pUnfilteredCollection, String pKey, RepositoryItem pProfile) {
    return true;
  }

  /**
  * Generates a sorted collection.
  *
  * @param pUnfilteredCollection the unfiltered collection
  * @param pCollectionIdentifierKey the key the uniquely identifies the unfiltered collection (ignored)
  * @param pProfile the user profile
  * @return the sorted collection
  * @throws FilterException This exception indicates that a severe error occurs while
  * performing sortColors method
  */
  protected Collection generateFilteredCollection(Collection pUnfilteredCollection, String pCollectionIdentifierKey,
    RepositoryItem pProfile) throws FilterException {
    if (pUnfilteredCollection instanceof List) {
      return getCatalogTools().sortColors((List) pUnfilteredCollection);
    }

    return pUnfilteredCollection;
  }
}
