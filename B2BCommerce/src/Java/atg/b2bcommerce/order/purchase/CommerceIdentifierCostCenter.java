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

package atg.b2bcommerce.order.purchase;

import atg.b2bcommerce.order.*;
import atg.commerce.order.CommerceIdentifier;

/**
 *
 * <p><b>Bean name</b>: CommerceIdentifierCostCenter.
 *
 * <p><table border><caption border><b>Properties</b></caption>
 * <tr><th>property name</th><th>type</th><th>description</th>
 * <th>default value</th><th>flags</th></tr>
 * <tr><td>CommerceIdentifier</td><td>CommerceIdentifier</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>CostCenter</td><td>String</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>RelationshipType</td><td>String</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>Amount</td><td>double</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>SplitAmount</td><td>double</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>Quantity</td><td>long</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>SplitQuantity</td><td>long</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>SplitCostCenterName</td><td>String</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr></table>
 *
 * <p><table border><caption border><b>EventSets</b></caption>
 * <tr><th>eventset name</th><th>listener type</th><th>description</th>
 * <th>flags</th></tr>
 * </table>
 *
 * <p>This class was generated mechanically by
 * atg.beanmaker.BeanMaker, and should not be modified.
 *
 * 
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/purchase/CommerceIdentifierCostCenter.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class CommerceIdentifierCostCenter
{
  //-------------------------------------
  // Class version string
  //-------------------------------------
  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/purchase/CommerceIdentifierCostCenter.java#2 $$Change: 651448 $";

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  //-------------------------------------
  // properties
  //-------------------------------------

  //---------------------------------------------------------------------------
  // property: CommerceIdentifier
  //---------------------------------------------------------------------------
  CommerceIdentifier mCommerceIdentifier;

  /**
   * Set the CommerceIdentifier property.
   * @param pCommerceIdentifier a <code>CommerceIdentifier</code> value
   */
  public void setCommerceIdentifier(CommerceIdentifier pCommerceIdentifier) {
    mCommerceIdentifier = pCommerceIdentifier;
  }

  /**
   * Return the CommerceIdentifier property.
   * @return a <code>CommerceIdentifier</code> value
   */
  public CommerceIdentifier getCommerceIdentifier() {
    return mCommerceIdentifier;
  }

  //---------------------------------------------------------------------------
  // property: CostCenterName
  //---------------------------------------------------------------------------
  String mCostCenterName;

  /**
   * Set the CostCenterName property.
   * @param pCostCenterName a <code>String</code> value
   */
  public void setCostCenterName(String pCostCenterName) {
    mCostCenterName = pCostCenterName;
  }

  /**
   * Return the CostCenterName property.
   * @return a <code>String</code> value
   */
  public String getCostCenterName() {
    return mCostCenterName;
  }

  //---------------------------------------------------------------------------
  // property: RelationshipType
  //---------------------------------------------------------------------------
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
  // property: Amount
  //---------------------------------------------------------------------------
  double mAmount;

  /**
   * Set the Amount property.
   * @param pAmount a <code>double</code> value
   */
  public void setAmount(double pAmount) {
    mAmount = pAmount;
  }

  /**
   * Return the Amount property.
   * @return a <code>double</code> value
   */
  public double getAmount() {
    return mAmount;
  }

  //--------------------------------------------------------------------------
  // property SplitAmount
  //--------------------------------------------------------------------------
  double mSplitAmount;
  
  /**
   * Set the SplitAmount property.
   * @param pSplitAmount a <code>double</code> value
   */
  public void setSplitAmount (double pSplitAmount) {
    if (mSplitAmount != pSplitAmount) {
      mSplitAmount = pSplitAmount;
    }
  }

  /** 
   * Return the SplitAmount property.
   * @return a <code>double</code> value
   */
  public double getSplitAmount () {
    return mSplitAmount;
  }

  //---------------------------------------------------------------------------
  // property: Quantity
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
  // property: SplitCostCenterName
  //---------------------------------------------------------------------------
  String mSplitCostCenterName;

  /**
   * Set the SplitCostCenterName property.
   * @param pSplitCostCenterName a <code>String</code> value
   */
  public void setSplitCostCenterName(String pSplitCostCenterName) {
    mSplitCostCenterName = pSplitCostCenterName;
  }

  /**
   * Return the SplitCostCenterName property.
   * @return a <code>String</code> value
   */
  public String getSplitCostCenterName() {
    return mSplitCostCenterName;
  }

  //-------------------------------------
  // methods
  //-------------------------------------
  //-------------------------------------
  // EventSets
  //-------------------------------------
  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Creates a new <code>CommerceIdentifierCostCenter</code> instance.
   *
   */
  public CommerceIdentifierCostCenter ()
  {
  }

  /**
   * Creates a new <code>CommerceIdentifierCostCenter</code> instance.
   *
   * @param pCommerceIdentifier a <code>CommerceIdentifier</code> value
   */
  public CommerceIdentifierCostCenter (CommerceIdentifier pCommerceIdentifier) {
    mCommerceIdentifier = pCommerceIdentifier;
  }

  //-------------------------------------
  // copyPropertiesTo method
  //-------------------------------------
  public void copyPropertiesTo (CommerceIdentifierCostCenter target)
  {
    target.setCommerceIdentifier (getCommerceIdentifier ());
    target.setCostCenterName (getCostCenterName ());
    target.setRelationshipType (getRelationshipType ());
    target.setAmount (getAmount ());
    target.setSplitAmount (getSplitAmount ());
    target.setQuantity (getQuantity ());
    target.setSplitQuantity (getSplitQuantity ());
  }

  //-------------------------------------
  // toString method
  //-------------------------------------

  public String toString ()
  {
    StringBuffer buf = new StringBuffer ();
    buf.append (getClass ().getName ());
    buf.append ("(");
    buf.append ("CommerceIdentifier: " + getCommerceIdentifier() + "  ");
    buf.append ("CostCenterName: " + getCostCenterName() + "  ");
    buf.append ("RelationshipType: " + getRelationshipType() + "  ");
    buf.append ("Amount: " + getAmount() + "  ");
    buf.append ("SplitAmount: " + getSplitAmount() + "  ");
    buf.append ("Quantity: " + getQuantity() + "  ");
    buf.append ("SplitQuantity: " + getSplitQuantity() + "  ");
    buf.append (")");
    return buf.toString ();
  }

  //-------------------------------------
  // equals method
  //-------------------------------------

  public boolean equals (Object pObj)
  {
    if (!(pObj instanceof CommerceIdentifierCostCenter)) return false;
    CommerceIdentifierCostCenter obj = (CommerceIdentifierCostCenter) pObj;

    if (this.getAmount () != obj.mAmount) return false;
    if (this.getSplitAmount () != obj.mSplitAmount) return false;
    if (this.getQuantity () != obj.mQuantity) return false;
    if (this.getSplitQuantity () != obj.mSplitQuantity) return false;
    if (this.getCommerceIdentifier () != obj.getCommerceIdentifier () &&
        (this.getCommerceIdentifier () == null ||
         obj.getCommerceIdentifier () == null ||
         !this.getCommerceIdentifier ().equals (obj.getCommerceIdentifier ()))) return false;
    if (this.getCostCenterName () != obj.getCostCenterName () &&
        (this.getCostCenterName () == null ||
         obj.getCostCenterName () == null ||
         !this.getCostCenterName ().equals (obj.getCostCenterName ()))) return false;
    if (this.getRelationshipType () != obj.getRelationshipType () &&
        (this.getRelationshipType () == null ||
         obj.getRelationshipType () == null ||
         !this.getRelationshipType ().equals (obj.getRelationshipType ()))) return false;
    return true;
  }

  //-------------------------------------
  // hashCode method
  //-------------------------------------

  public int hashCode ()
  {
    int ret = 0;
    ret = (ret * 23) + (int) (this.getAmount ());
    ret = (ret * 23) + (int) (this.getSplitAmount ());
    ret = (ret * 23) + (int) (this.getQuantity ());
    ret = (ret * 23) + (int) (this.getSplitQuantity ());
    ret *= 29;
    if (this.getCommerceIdentifier () != null)
      ret += this.getCommerceIdentifier ().hashCode ();
    ret *= 29;
    if (this.getCostCenterName () != null)
      ret += this.getCostCenterName ().hashCode ();
    ret *= 29;
    if (this.getRelationshipType () != null)
      ret += this.getRelationshipType ().hashCode ();
    return ret;
  }

}
