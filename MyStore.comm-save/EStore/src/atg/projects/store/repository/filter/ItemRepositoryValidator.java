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

package atg.projects.store.repository.filter;

import java.util.Collection;
import java.util.Iterator;

import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;
import atg.service.collections.filter.CachedCollectionFilter;
import atg.service.collections.filter.FilterException;
import atg.service.collections.validator.CollectionObjectValidator;

/**
 * This validator validates that a repository item exists or is removed from the repository.
 *
 * @author ATG
 * @version $Revision:
 */
public class ItemRepositoryValidator extends GenericService implements CollectionObjectValidator {

  /**
   * Class version string.
   */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/repository/filter/ItemRepositoryValidator.java#3 $$Change: 635816 $";

  /**
   * This method validates that a repository item exists or is removed from the repository.
   *
   * @param pObject - Object to validate
   * @return true if the repository item exists, false - otherwise
   */
  public boolean validateObject(Object pObject) {

    if (!(pObject instanceof RepositoryItem) ) {
      return false;
    }
    
    RepositoryItem product = (RepositoryItem) pObject;
    
    boolean valid = true;
    try {
      RepositoryItem item = product.getRepository().getItem(product.getRepositoryId(), product.getItemDescriptor().getItemDescriptorName());
      if (item == null) {
        valid = false;
      }
    }
    catch (Exception ex) {
      valid = false;
    }
    if (isLoggingDebug()) {
      logDebug("RepositoryItem " + product + " in "+ product.getRepository() + " = " + valid);
    }
    return valid;
  }
}
