/*<ATGCOPYRIGHT>
 * Copyright (C) 2000-2011 Art Technology Group, Inc.
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

package atg.projects.b2bstore.userprofiling;

import atg.commerce.profile.CommercePropertyManager;
import atg.b2bcommerce.profile.B2BCommercePropertyManager;

/**
 *
 * <p><b>Bean name</b>: B2BPropertyManager.
 *
 * <p><table border><caption border><b>Properties</b></caption>
 * <tr><th>property name</th><th>type</th><th>description</th>
 * <th>default value</th><th>flags</th></tr>
 * <tr><td>organizationNamePropertyName</td><td>String</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>organizationIdPropertyName</td><td>String</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>parentOrganizationPropertyName</td><td>String</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * </table>
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
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/userprofiling/B2BPropertyManager.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public
class B2BPropertyManager
extends B2BCommercePropertyManager
{
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/userprofiling/B2BPropertyManager.java#2 $$Change: 651448 $";

  //-------------------------------------
  // properties
  //-------------------------------------


  //---------------------------------------------------------------------
  // property: approvalCheckRequiredPropertyName
  String mApprovalCheckRequiredPropertyName;

  /**
   * Return the approvalCheckRequiredPropertyName property.
   * @return
   */
  public String getApprovalCheckRequiredPropertyName() {
    return mApprovalCheckRequiredPropertyName;
  }

  /**
   * Set the approvalCheckRequiredPropertyName property.
   * @param pApprovalCheckRequiredPropertyName
   */
  public void setApprovalCheckRequiredPropertyName(String pApprovalCheckRequiredPropertyName) {
    mApprovalCheckRequiredPropertyName = pApprovalCheckRequiredPropertyName;
  }

  //---------------------------------------------------------------------
  // property: orderLimitPropertyName
  String mOrderLimitPropertyName;

  /**
   * Return the orderLimitPropertyName property.
   * @return
   */
  public String getOrderLimitPropertyName() {
    return mOrderLimitPropertyName;
  }

  /**
   * Set the orderLimitPropertyName property.
   * @param pOrderLimitPropertyName
   */
  public void setOrderLimitPropertyName(String pOrderLimitPropertyName) {
    mOrderLimitPropertyName = pOrderLimitPropertyName;
  }

  
  // property organizationNamePropertyName
  String mOrganizationNamePropertyName;
  public String getOrganizationNamePropertyName () {
    return mOrganizationNamePropertyName;
  }
  public void setOrganizationNamePropertyName (String pOrganizationNamePropertyName) {
    if (mOrganizationNamePropertyName != pOrganizationNamePropertyName) {
      mOrganizationNamePropertyName = pOrganizationNamePropertyName;
    }
  }

  // property organizationIdPropertyName
  String mOrganizationIdPropertyName;
  public String getOrganizationIdPropertyName () {
    return mOrganizationIdPropertyName;
  }
  public void setOrganizationIdPropertyName (String pOrganizationIdPropertyName) {
    if (mOrganizationIdPropertyName != pOrganizationIdPropertyName) {
      mOrganizationIdPropertyName = pOrganizationIdPropertyName;
    }
  }

  // property parentOrganizationPropertyName
  String mParentOrganizationPropertyName;
  public String getParentOrganizationPropertyName () {
    return mParentOrganizationPropertyName;
  }
  public void setParentOrganizationPropertyName (String pParentOrganizationPropertyName) {
    if (mParentOrganizationPropertyName != pParentOrganizationPropertyName) {
      mParentOrganizationPropertyName = pParentOrganizationPropertyName;
    }
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
  public B2BPropertyManager ()
  {
  }

  //-------------------------------------
  // copyPropertiesTo method
  //-------------------------------------
  public void copyPropertiesTo (B2BPropertyManager target)
  {
    target.setOrganizationNamePropertyName (getOrganizationNamePropertyName ());
    target.setOrganizationIdPropertyName (getOrganizationIdPropertyName ());
    target.setParentOrganizationPropertyName (getParentOrganizationPropertyName ());
  }

  //-------------------------------------
  // toString method
  //-------------------------------------
  public String toString ()
  {
    StringBuffer buf = new StringBuffer ();
    buf.append (getClass ().getName ());
    buf.append ("(");
    buf.append ("organizationNamePropertyName: " + getOrganizationNamePropertyName() + "  ");
    buf.append ("organizationIdPropertyName: " + getOrganizationIdPropertyName() + "  ");
    buf.append ("parentOrganizationPropertyName: " + getParentOrganizationPropertyName() + "  ");
    buf.append (")");
    return buf.toString ();
  }

  //-------------------------------------
  // equals method
  //-------------------------------------
  public boolean equals (Object pObj)
  {
    if (!(pObj instanceof B2BPropertyManager)) return false;
    B2BPropertyManager obj = (B2BPropertyManager) pObj;

    if (this.getOrganizationNamePropertyName () != obj.getOrganizationNamePropertyName () &&
        (this.getOrganizationNamePropertyName () == null ||
         obj.getOrganizationNamePropertyName () == null ||
         !this.getOrganizationNamePropertyName ().equals (obj.getOrganizationNamePropertyName ()))) return false;
    if (this.getOrganizationIdPropertyName () != obj.getOrganizationIdPropertyName () &&
        (this.getOrganizationIdPropertyName () == null ||
         obj.getOrganizationIdPropertyName () == null ||
         !this.getOrganizationIdPropertyName ().equals (obj.getOrganizationIdPropertyName ()))) return false;
    if (this.getParentOrganizationPropertyName () != obj.getParentOrganizationPropertyName () &&
        (this.getParentOrganizationPropertyName () == null ||
         obj.getParentOrganizationPropertyName () == null ||
         !this.getParentOrganizationPropertyName ().equals (obj.getParentOrganizationPropertyName ()))) return false;
    return true;
  }

  //-------------------------------------
  // hashCode method
  //-------------------------------------
  public int hashCode ()
  {
    int ret = 0;
    ret *= 29;
    if (this.getOrganizationNamePropertyName () != null)
      ret += this.getOrganizationNamePropertyName ().hashCode ();
    ret *= 29;
    if (this.getOrganizationIdPropertyName () != null)
      ret += this.getOrganizationIdPropertyName ().hashCode ();
    ret *= 29;
    if (this.getParentOrganizationPropertyName () != null)
      ret += this.getParentOrganizationPropertyName ().hashCode ();
    return ret;
  }

}
