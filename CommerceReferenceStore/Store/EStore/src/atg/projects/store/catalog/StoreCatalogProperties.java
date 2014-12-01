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

package atg.projects.store.catalog;

import atg.commerce.catalog.custom.CatalogProperties;


/**
 * <p>This class provides a mechanism to access the property names for
 * catalog item descriptors modified for ATG.
 * For example, if a calling class needed the property name of the
 * sku that provides the start date, the
 * getStartDatePropertyName() method will return the string value for the
 * property name as used in the repository definition.
 * </p>
 *
 * @author ATG
 * @version $Revision: #2 $
 */
public class StoreCatalogProperties extends CatalogProperties {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/catalog/StoreCatalogProperties.java#2 $$Change: 651448 $";

  /**
   * Sku type property name.
   */
  private String mSkuTypePropertyName = "type";

  /**
   * Start date property name.
   */
  private String mStartDatePropertyName;

  /**
   * End date property name.
   */
  private String mEndDatePropertyName;

  /**
   * Color property name.
   */
  private String mColorPropertyName = "color";

  /**
   * Size property name.
   */
  private String mSizePropertyName = "size";

  /**
   * Color swatch name.
   */
  private String mColorSwatchName = "colorSwatch";

  /**
   * Wood finish property name.
   */
  private String mWoodFinishPropertyName = "woodFinish";
  /**
   * Gift wrapt eligiple property name.
   */
  private String mGiftWrapEligiblePropertyName = "giftWrapEligible";

  /**
   * Preordarable property name.
   */
  private String mPreorderablePropertyName;

  /**
   * Preorder end date property name.
   */
  private String mPreorderEndDatePropertyName;

  /**
   * Use inventory for preorder property name.
   */
  private String mUseInventoryForPreorderPropertyName;

  /**
   * <p>The start date property name.
   * @param pStartDatePropertyName - start date property name
   */
  public void setStartDatePropertyName(String pStartDatePropertyName) {
    mStartDatePropertyName = pStartDatePropertyName;
  }

  /**
   * <p>The start date property name.
   * @return start date property name
   */
  public String getStartDatePropertyName() {
    return mStartDatePropertyName;
  }

  /**
   * <p>The end date property name.
   * @param pEndDatePropertyName - end date property name
   */
  public void setEndDatePropertyName(String pEndDatePropertyName) {
    mEndDatePropertyName = pEndDatePropertyName;
  }

  /**
   * <p>The end date property name.
   * @return end date property name
   */
  public String getEndDatePropertyName() {
    return mEndDatePropertyName;
  }

  /**
   * <p>The sku type property name.
   * @return sku type property name
   */
  public String getSkuTypePropertyName() {
    return mSkuTypePropertyName;
  }

  /**
   * <p>The sku type property name.
   * @param pSkuTypePropertyName - sku type property name
   */
  public void setSkuTypePropertyName(String pSkuTypePropertyName) {
    mSkuTypePropertyName = pSkuTypePropertyName;
  }

  /**
   * <p>The name of the property used to indicate an item as 'giftwrappable'.
   * @param pGiftWrapEligiblePropertyName - name of the property used to indicate an item as 'giftwrappable'
   */
  public void setGiftWrapEligiblePropertyName(String pGiftWrapEligiblePropertyName) {
    mGiftWrapEligiblePropertyName = pGiftWrapEligiblePropertyName;
  }

  /**
   * <p>The name of the property used to indicate an item as 'giftwrappable'.
   * @return name of the property used to indicate an item as 'giftwrappable'
   */
  public String getGiftWrapEligiblePropertyName() {
    return mGiftWrapEligiblePropertyName;
  }

  /**
   * @return Returns the colorPropertyName.
   */
  public String getColorPropertyName() {
    return mColorPropertyName;
  }

  /**
   * @param pColorPropertyName The colorPropertyName to set.
   */
  public void setColorPropertyName(String pColorPropertyName) {
    mColorPropertyName = pColorPropertyName;
  }

  /**
   * @return Returns the sizePropertyName.
   */
  public String getSizePropertyName() {
    return mSizePropertyName;
  }

  /**
   * @param pSizePropertyName - The sizePropertyName to set.
   */
  public void setSizePropertyName(String pSizePropertyName) {
    mSizePropertyName = pSizePropertyName;
  }

  /**
   * @return the woodTypePropertyName
   */
  public String getWoodFinishPropertyName()
  {
    return mWoodFinishPropertyName;
  }

  /**
   * @param pWoodFinishPropertyName the woodFinishPropertyName to set
   */
  public void setWoodFinishPropertyName(String pWoodFinishPropertyName)
  {
    mWoodFinishPropertyName = pWoodFinishPropertyName;
  }

  /**
   * @return Returns the colorSwatchName.
   */
  public String getColorSwatchName() {
    return mColorSwatchName;
  }

  /**
   * @param pColorSwatchName - The colorSwatchName to set.
   */
  public void setColorSwatchName(String pColorSwatchName) {
    mColorSwatchName = pColorSwatchName;
  }

  /**
   * <p>The name of the property used to indicate an item as 'preorderable'.
   * @param pPreorderablePropertyName The preorderablePropertyName to set.
   */
  public void setPreorderablePropertyName(String pPreorderablePropertyName) {
    mPreorderablePropertyName = pPreorderablePropertyName;
  }

  /**
   * <p>The name of the property used to indicate an item as 'preorderable'.
   * @return Returns the preorderablePropertyName.
   */
  public String getPreorderablePropertyName() {
    return mPreorderablePropertyName;
  }

  /**
   * <p>The name of the property used to indicate the 'preorderEndDate' of
   * an item that is preorderable.
   * @param pPreorderEndDatePropertyName The preorderEndDatePropertyName to set.
   */
  public void setPreorderEndDatePropertyName(String pPreorderEndDatePropertyName) {
    mPreorderEndDatePropertyName = pPreorderEndDatePropertyName;
  }

  /**
   * <p>The name of the property used to indicate the 'preorderEndDate' of
   * an item that is preorderable.
   * @return Returns the preorderEndDatePropertyName.
   */
  public String getPreorderEndDatePropertyName() {
    return mPreorderEndDatePropertyName;
  }

  /**
   * <p>The name of the property used to indicate the 'useInventoryForPreorder' of
   * an item that is preorderable.
   * @param pUseInventoryForPreorderPropertyName The useInventoryForPreorderPropertyName to set.
   */
  public void setUseInventoryForPreorderPropertyName(String pUseInventoryForPreorderPropertyName) {
    mUseInventoryForPreorderPropertyName = pUseInventoryForPreorderPropertyName;
  }

  /**
   * <p>The name of the property used to indicate the 'useInventoryForPreorder' of
   * an item that is preorderable.
   * @return Returns the useInventoryForPreorderPropertyName.
   */
  public String getUseInventoryForPreorderPropertyName() {
    return mUseInventoryForPreorderPropertyName;
  }
}
