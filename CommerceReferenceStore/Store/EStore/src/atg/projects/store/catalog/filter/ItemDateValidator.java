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

import atg.projects.store.catalog.StoreCatalogProperties;

import atg.repository.RepositoryItem;

import atg.service.collections.validator.CollectionObjectValidator;
import atg.service.util.CurrentDate;

import java.text.SimpleDateFormat;

import java.util.Date;


/**
 * <p>
 * This validator validates all products by start and end date.
 * </p>
 * <p>If today's date falls on or between the start and end dates
 * then the product is valid.
 * <p>If the start date is null then the product is valid
 * as long as the has not passed.
 * <p>If the end date is null then the product is valid if the startDate
 * has passed.
 * <p>If both the start and end dates are null then the product is valid
 *
 * @author ATG
 * @version $Revision: #2 $
 */
public class ItemDateValidator extends GenericService implements CollectionObjectValidator {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/catalog/filter/ItemDateValidator.java#2 $$Change: 651448 $";

  /**
   * Catalog tools.
   */
  private CustomCatalogTools mCatalogTools;

  /**
   * Current date.
   */
  private CurrentDate mCurrentDate;

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
   * @param pCurrentDate - current date.
   */
  public void setCurrentDate(CurrentDate pCurrentDate) {
    mCurrentDate = pCurrentDate;
  }

  /**
   * @return current date.
   */
  public CurrentDate getCurrentDate() {
    return mCurrentDate;
  }

  /**
   * <p>See the API doc for the logic performed by this validation method.
   * @param pObject - object to validate
   * @return See the API doc for the logic performed by this validation method
   */
  public boolean validateObject(Object pObject) {
    if (!(pObject instanceof RepositoryItem) ) {
      return false;
    }
    
    RepositoryItem product = (RepositoryItem) pObject;
    
    CurrentDate cd = getCurrentDate();
    CustomCatalogTools ct = getCatalogTools();
    StoreCatalogProperties cp = (StoreCatalogProperties) ct.getCatalogProperties();

    boolean startOK = true;
    boolean endOK = true;

    Date current = cd.getDateAsDate();
    Date start = (Date) product.getPropertyValue(cp.getStartDatePropertyName());
    Date end = (Date) product.getPropertyValue(cp.getEndDatePropertyName());

    if (start != null) {
      // if it starts after today then its not visible
      if (start.after(current)) {
        startOK = false;
      }
    }

    if (end != null) {
      // if it ends before today then its not visible
      if (end.before(current)) {
        endOK = false;
      }
    }

    if (isLoggingDebug()) {
      SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
      String currentFormat = format.format(current);
      String startFormat = (start == null) ? "NULL" : format.format(start);
      String endFormat = (end == null) ? "NULL" : format.format(end);

      String msg = " product " + product.getRepositoryId() + " has  start = " + startFormat + " end = " + endFormat +
        " current = " + currentFormat + " valid = " + (startOK && endOK);

      logDebug(msg);
    }

    return startOK && endOK;
  }
}
