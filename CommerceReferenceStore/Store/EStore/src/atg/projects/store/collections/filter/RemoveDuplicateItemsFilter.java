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

package atg.projects.store.collections.filter;

import atg.service.collections.filter.FilterException;
import atg.service.collections.filter.CachedCollectionFilter;
import atg.repository.RepositoryItem;

import java.util.Collection;
import java.util.ArrayList;

/**
 * This filter removes duplicates from collection
 *
 * @see atg.service.collections.filter.CachedCollectionFilter
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/collections/filter/RemoveDuplicateItemsFilter.java#2 $$Change: 651448 $
 *
 */
public class RemoveDuplicateItemsFilter extends CachedCollectionFilter
{

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/collections/filter/RemoveDuplicateItemsFilter.java#2 $$Change: 651448 $";

  /**
   * {@inheritDoc}
   */
  public Object generateContextKey(Collection pCollection, String pStr, RepositoryItem pRepositoryItem) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  protected Collection generateFilteredCollection(Collection pCollection, String pStr, RepositoryItem pRepositoryItem) throws FilterException {
    return removeDuplicateValues(pCollection);
  }

  /**
   * {@inheritDoc}
   */
  public boolean shouldApplyFilter(Collection pCollection, String pStr, RepositoryItem pRepositoryItem) {
    return true;
  }

  /**
   * Returns collection with no duplicates
   * @param pCollection collection to be filtered
   * @return collection with no duplicates
   */
  protected Collection removeDuplicateValues(Collection pCollection){
    Collection collectionResult = new ArrayList();
    if (pCollection != null && !pCollection.isEmpty()) {
      for (Object item : pCollection){
        if (!isItemInCollection(item, collectionResult)){
          collectionResult.add(item);
        }
      }
    }
    return collectionResult;
  }

  /**
   * Returns true if object is in collection and vice versa
   * @param pItem repository item
   * @param pCollection collection of repository items
   * @return true if object is in collection and vice versa
   */
  private boolean isItemInCollection(Object pItem, Collection pCollection) {
    for (Object objFromCollection : pCollection) {
      if (((RepositoryItem)pItem).getRepositoryId().equals(((RepositoryItem)objFromCollection).getRepositoryId())) {
        return Boolean.TRUE;
      }
    }
    return Boolean.FALSE;    
  }


}
