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
package atg.projects.store.inventory;


/**
 * <p>
 * This class is a simple bean representing the discrepancy data that is written to
 * file when an incoming inventory level is different from the inventory
 * currently found in the inventory repository. The InventoryDiscrepancyFileWriter
 * uses these beans to write the file.
 *
 * <p>
 * The class consists of a constructor, and getters and setters for the bean
 * properties.
 *
 * @see atg.projects.store.inventory.InventoryDiscrepancyFileWriter

 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/inventory/InventoryDiscrepancyMessage.java#3 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class InventoryDiscrepancyMessage {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/inventory/InventoryDiscrepancyMessage.java#3 $$Change: 635816 $";

  /**
   * Sku id.
   */
  protected String mSkuId;

  /**
   * Description.
   */
  protected String mDescription;

  /**
   * New quantity.
   */
  protected long mNewQuantity;

  /**
   * Old quantity.
   */
  protected long mOldQuantity;

  /**
   * Constructor for creating a new InventoryDiscrepancyMessage bean
   * containing a skuId, description, newQuantity and oldQuantity.
   *
   * @param pSkuId id of repository item
   * @param pDescription description for message
   * @param pNewQuantity new quantity
   * @param pOldQuantity old quantity
   */
  public InventoryDiscrepancyMessage(String pSkuId, String pDescription, long pNewQuantity, long pOldQuantity) {
    mSkuId = pSkuId;
    mDescription = pDescription;
    mNewQuantity = pNewQuantity;
    mOldQuantity = pOldQuantity;
  }

  /**
  * @return the skuId.
  */
  public String getSkuId() {
    return mSkuId;
  }

  /**
   * @param pSkuId - the skuId to set.
   */
  public void setSkuId(String pSkuId) {
    mSkuId = pSkuId;
  }

  /**
   * @return the description.
   */
  public String getDescription() {
    return mDescription;
  }

  /**
   * @param pDescription - the description to set.
   */
  public void setDescription(String pDescription) {
    mDescription = pDescription;
  }

  /**
     * @return the new quantity.
     */
  public long getNewQuantity() {
    return mNewQuantity;
  }

  /**
   * @param pNewQuantity - the new quantity to set.
   */
  public void setNewQuantity(long pNewQuantity) {
    mNewQuantity = pNewQuantity;
  }

  /**
   * @return the old quantity.
   */
  public long getOldQuantity() {
    return mOldQuantity;
  }

  /**
   * @param pOldQuantity - the old quantity to set.
   */
  public void setOldQuantity(long pOldQuantity) {
    mOldQuantity = pOldQuantity;
  }
}
