/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2010 Art Technology Group, Inc.
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

package atg.projects.store.order.purchase;

import atg.commerce.order.purchase.PurchaseProcessFormHandler;

import atg.projects.store.StoreConfiguration;

import java.util.Map;

/**
 * Form Handler for handling purchase processes in Store
 *
 * @author ATG
 * @version $Version$
 */

public class StorePurchaseProcessFormHandler extends PurchaseProcessFormHandler {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/purchase/StorePurchaseProcessFormHandler.java#3 $$Change: 635816 $";

  /**
   * Property names map.
   */
  private Map mPropertyNameMap;

  /**
   * Store configuration.
   */
  protected StoreConfiguration mStoreConfiguration;

  /**
   * @return property names map.
   */
  public Map getPropertyNameMap() {
    return mPropertyNameMap;
  }

  /**
   * @param pPropertyNameMap - property names map.
   */
  public void setPropertyNameMap(Map pPropertyNameMap) {
    mPropertyNameMap = pPropertyNameMap;
  }

  /**
   * @return the store configuration.
   */
  public StoreConfiguration getStoreConfiguration() {
    return mStoreConfiguration;
  }

  /**
   * @param pStoreConfiguration - the store configuration to set.
   */
  public void setStoreConfiguration(StoreConfiguration pStoreConfiguration) {
    mStoreConfiguration = pStoreConfiguration;
  }

}
