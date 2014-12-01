/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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

package atg.commerce.order.purchase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.RelationshipTypes;

/**
 * This helper object represents the association between a CommerceItem and its
 * shipping information.
 *
 * @author Charles Chen
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CommerceItemShippingInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class CommerceItemShippingInfo implements Serializable
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CommerceItemShippingInfo.java#2 $$Change: 651448 $";

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  public static final String QUANTITY_TYPE = RelationshipTypes.SHIPPINGQUANTITY_STR;
  public static final String QUANTITYREMAINING_TYPE = RelationshipTypes.SHIPPINGQUANTITYREMAINING_STR;

  
  //---------------------------------------------------------------------------
  // property: CommerceItem
  //---------------------------------------------------------------------------
  CommerceItem mCommerceItem;
 
  /**
   * Set the CommerceItem property.
   * @param pCommerceItem a <code>CommerceItem</code> value
   */
  public void setCommerceItem(CommerceItem pCommerceItem) {
    mCommerceItem = pCommerceItem;
  }

  /**
   * Return the CommerceItem property.
   * @return a <code>CommerceItem</code> value
   */
  public CommerceItem getCommerceItem() {
    return mCommerceItem;
  }

  //---------------------------------------------------------------------------
  // property: ShippingMethod -- The method to ship the CommerceItem such
  //                             as Ground, 2nd Day, etc 
  //---------------------------------------------------------------------------

  String mShippingMethod;

  /**
   * Set the ShippingMethod property.
   * @param pShippingMethod a <code>String</code> value
   */
  public void setShippingMethod(String pShippingMethod) {
    mShippingMethod = pShippingMethod; 
  }

  /**
   * Return the ShippingMethod property.
   * @return a <code>String</code> value
   */
  public String getShippingMethod() {
    return mShippingMethod;
  }

  //------------------------------------------------------------------------------------
  // property: ShippingGroupName -- The ShippingGroup Name to identify the ShippingGroup
  //------------------------------------------------------------------------------------

  String mShippingGroupName;

  /**
   * Set the ShippingGroupName property.
   * @param pShippingGroupName a <code>String</code> value
   */
  public void setShippingGroupName(String pShippingGroupName) {
    mShippingGroupName = pShippingGroupName; 
  }

  /**
   * Return the ShippingGroupName property.
   * @return a <code>String</code> value
   */
  public String getShippingGroupName() {
    return mShippingGroupName;
  }

  //-------------------------------------------------------------------------------
  // property: RelationshipType -- The relationship between the CommerceItem
  // and the ShippingGroup. Possible value includes ShippingQuantity which indicates
  // a specified quantity to be shipped; shippingQuantityRemaining which indicates
  // the quantities that have not been shipped to any ShippingGroups will be shipped
  //--------------------------------------------------------------------------------
  String mRelationshipType;

  /**
   * Set the RelationshipType property.
   * @param pRelationshipType a <code>String</code> value
   */
  public void setRelationshipType(String pRelationshipType) {
    mRelationshipType = pRelationshipType;
  }  

  /**
   * Return the RelationshipType property.
   * @return a <code>String</code> value
   */
  public String getRelationshipType() {
    return mRelationshipType; 
  }

  //---------------------------------------------------------------------------
  // property: Quantity -- The quantity to be shipped to the ShippingGroup
  //---------------------------------------------------------------------------

  long mQuantity;

  /**
   * Set the Quantity property.
   * @param pQuantity a <code>long</code> value
   */
  public void setQuantity(long pQuantity) {
    mQuantity = pQuantity;
  }

  /**
   * Return the Quantity property.
   * @return a <code>long</code> value
   */
  public long getQuantity() {
    return mQuantity;
  } 

  //---------------------------------------------------------------------------
  // property: SplitQuantity
  //---------------------------------------------------------------------------
  long mSplitQuantity;

  /**
   * Set the SplitQuantity property.
   * @param pSplitQuantity a <code>long</code> value
   */
  public void setSplitQuantity(long pSplitQuantity) {
    mSplitQuantity = pSplitQuantity;
  }

  /**
   * Return the SplitQuantity property.
   * @return a <code>long</code> value
   */
  public long getSplitQuantity() {
    return mSplitQuantity;
  }

  //---------------------------------------------------------------------------
  // property: SplitShippingGroupName
  //---------------------------------------------------------------------------
  String mSplitShippingGroupName;

  /**
   * Set the SplitShippingGroupName property.
   * @param pSplitShippingGroupName a <code>String</code> value
   */
  public void setSplitShippingGroupName(String pSplitShippingGroupName) {
    mSplitShippingGroupName = pSplitShippingGroupName;
  }

  /**
   * Return the SplitShippingGroupName property.
   * @return a <code>String</code> value
   */
  public String getSplitShippingGroupName() {
    return mSplitShippingGroupName;
  }

  //-------------------------------------
  // Methods
  //-------------------------------------

  /**
   * <code>getQuantityType</code> is used to return the quantity relationship type String.
   *
   * @return a <code>String</code> value
   */
  public String getQuantityType () {
    return QUANTITY_TYPE;
  }

  /**
   * <code>getQuantityRemainingType</code> is used to return the quantity remaining
   * relationship type String.
   *
   * @return a <code>String</code> value
   */
  public String getQuantityRemainingType () {
    return QUANTITYREMAINING_TYPE;
  }
  
  protected List<HandlingInstructionInfo> mHandlingInstructionInfos=new ArrayList<HandlingInstructionInfo>();
  public List<HandlingInstructionInfo> getHandlingInstructionInfos()
  {
    return mHandlingInstructionInfos;
  }

  public void setHandlingInstructionInfos(List<HandlingInstructionInfo> pHandlingInstructions)
  {
    mHandlingInstructionInfos = pHandlingInstructions;
  }
  
  /**
   * Adds a HandlingInstructionInfo  
   * @param pHandlingInstructionInfo
   */
  public void addHandlingInstructionInfo(HandlingInstructionInfo pHandlingInstructionInfo)
  {
    getHandlingInstructionInfos().add(pHandlingInstructionInfo);
  }

  /**
   * Removes a HandlingInstructionInfo  
   * @param pHandlingInstructionInfo
   */
  public void removeHandlingInstructionInfo(HandlingInstructionInfo pHandlingInstructionInfo)
  {
    getHandlingInstructionInfos().remove(pHandlingInstructionInfo);
  }
  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Creates a new <code>CommerceItemShippingInfo</code> instance.
   *
   */
  public CommerceItemShippingInfo ()  {

  }

  //-------------------------------------
  /**
   * Method that renders the general order information in a readable string format
   */
  public String toString() {
    StringBuilder sb = new StringBuilder("CommerceItemShippingInfo[ ");
    sb.append("commerceItem:").append(getCommerceItem()).append("; ");
    sb.append("shippingMethod:").append(getShippingMethod()).append("; ");
    sb.append("shippingGroupName:").append(getShippingGroupName()).append("; ");
    sb.append("relationshipType:").append(getRelationshipType()).append("; ");
    sb.append("quantity:").append(getQuantity()).append("; ");
    sb.append("splitQuantity:").append(getSplitQuantity()).append("; ");
    sb.append("splitShippingGroupName:").append(getSplitShippingGroupName()).append("; ");
    sb.append("]");
    
    return sb.toString();
  }


}//CommerceItemShippingInfo.java
