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

package atg.projects.b2bstore.purchaselists;

import atg.commerce.gifts.*;

/**
 *
 * <p><b>Bean name</b>: PurchaselistFormHandlerSuper.
 *
 * <p><table border><caption border><b>Properties</b></caption>
 * <tr><th>property name</th><th>type</th><th>description</th>
 * <th>default value</th><th>flags</th></tr>
 * <tr><td>purchaseListId</td><td>String</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>listName</td><td>String</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>createPurchaselistSuccessURL</td><td>String</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>createPurchaselistErrorURL</td><td>String</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>savePurchaselistSuccessURL</td><td>String</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>savePurchaselistErrorURL</td><td>String</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>updatePurchaselistSuccessURL</td><td>String</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>updatePurchaselistErrorURL</td><td>String</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>updatePurchaselistItemsSuccessURL</td><td>String</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>updatePurchaselistItemsErrorURL</td><td>String</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>deletePurchaselistSuccessURL</td><td>String</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>deletePurchaselistErrorURL</td><td>String</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>addItemToPurchaselistSuccessURL</td><td>String</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>addItemToPurchaselistErrorURL</td><td>String</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
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
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/purchaselists/PurchaselistFormHandlerSuper.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public
class PurchaselistFormHandlerSuper
extends GiftlistFormHandler
{
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/purchaselists/PurchaselistFormHandlerSuper.java#2 $$Change: 651448 $";

  //-------------------------------------
  // properties
  //-------------------------------------
  // property purchaseListId
  String mPurchaseListId;
  public String getPurchaseListId () {
    return mPurchaseListId;
  }
  public void setPurchaseListId (String pPurchaseListId) {
    if (mPurchaseListId != pPurchaseListId) {
      mPurchaseListId = pPurchaseListId;
    }
  }

  // property listName
  String mListName;
  public String getListName () {
    return mListName;
  }
  public void setListName (String pListName) {
    if (mListName != pListName) {
      mListName = pListName;
    }
  }

  // property createPurchaselistSuccessURL
  String mCreatePurchaselistSuccessURL;
  public String getCreatePurchaselistSuccessURL () {
    return mCreatePurchaselistSuccessURL;
  }
  public void setCreatePurchaselistSuccessURL (String pCreatePurchaselistSuccessURL) {
    if (mCreatePurchaselistSuccessURL != pCreatePurchaselistSuccessURL) {
      mCreatePurchaselistSuccessURL = pCreatePurchaselistSuccessURL;
    }
  }

  // property createPurchaselistErrorURL
  String mCreatePurchaselistErrorURL;
  public String getCreatePurchaselistErrorURL () {
    return mCreatePurchaselistErrorURL;
  }
  public void setCreatePurchaselistErrorURL (String pCreatePurchaselistErrorURL) {
    if (mCreatePurchaselistErrorURL != pCreatePurchaselistErrorURL) {
      mCreatePurchaselistErrorURL = pCreatePurchaselistErrorURL;
    }
  }

  // property savePurchaselistSuccessURL
  String mSavePurchaselistSuccessURL;
  public String getSavePurchaselistSuccessURL () {
    return mSavePurchaselistSuccessURL;
  }
  public void setSavePurchaselistSuccessURL (String pSavePurchaselistSuccessURL) {
    if (mSavePurchaselistSuccessURL != pSavePurchaselistSuccessURL) {
      mSavePurchaselistSuccessURL = pSavePurchaselistSuccessURL;
    }
  }

  // property savePurchaselistErrorURL
  String mSavePurchaselistErrorURL;
  public String getSavePurchaselistErrorURL () {
    return mSavePurchaselistErrorURL;
  }
  public void setSavePurchaselistErrorURL (String pSavePurchaselistErrorURL) {
    if (mSavePurchaselistErrorURL != pSavePurchaselistErrorURL) {
      mSavePurchaselistErrorURL = pSavePurchaselistErrorURL;
    }
  }

  // property updatePurchaselistSuccessURL
  String mUpdatePurchaselistSuccessURL;
  public String getUpdatePurchaselistSuccessURL () {
    return mUpdatePurchaselistSuccessURL;
  }
  public void setUpdatePurchaselistSuccessURL (String pUpdatePurchaselistSuccessURL) {
    if (mUpdatePurchaselistSuccessURL != pUpdatePurchaselistSuccessURL) {
      mUpdatePurchaselistSuccessURL = pUpdatePurchaselistSuccessURL;
    }
  }

  // property updatePurchaselistErrorURL
  String mUpdatePurchaselistErrorURL;
  public String getUpdatePurchaselistErrorURL () {
    return mUpdatePurchaselistErrorURL;
  }
  public void setUpdatePurchaselistErrorURL (String pUpdatePurchaselistErrorURL) {
    if (mUpdatePurchaselistErrorURL != pUpdatePurchaselistErrorURL) {
      mUpdatePurchaselistErrorURL = pUpdatePurchaselistErrorURL;
    }
  }

  // property updatePurchaselistItemsSuccessURL
  String mUpdatePurchaselistItemsSuccessURL;
  public String getUpdatePurchaselistItemsSuccessURL () {
    return mUpdatePurchaselistItemsSuccessURL;
  }
  public void setUpdatePurchaselistItemsSuccessURL (String pUpdatePurchaselistItemsSuccessURL) {
    if (mUpdatePurchaselistItemsSuccessURL != pUpdatePurchaselistItemsSuccessURL) {
      mUpdatePurchaselistItemsSuccessURL = pUpdatePurchaselistItemsSuccessURL;
    }
  }

  // property updatePurchaselistItemsErrorURL
  String mUpdatePurchaselistItemsErrorURL;
  public String getUpdatePurchaselistItemsErrorURL () {
    return mUpdatePurchaselistItemsErrorURL;
  }
  public void setUpdatePurchaselistItemsErrorURL (String pUpdatePurchaselistItemsErrorURL) {
    if (mUpdatePurchaselistItemsErrorURL != pUpdatePurchaselistItemsErrorURL) {
      mUpdatePurchaselistItemsErrorURL = pUpdatePurchaselistItemsErrorURL;
    }
  }

  // property deletePurchaselistSuccessURL
  String mDeletePurchaselistSuccessURL;
  public String getDeletePurchaselistSuccessURL () {
    return mDeletePurchaselistSuccessURL;
  }
  public void setDeletePurchaselistSuccessURL (String pDeletePurchaselistSuccessURL) {
    if (mDeletePurchaselistSuccessURL != pDeletePurchaselistSuccessURL) {
      mDeletePurchaselistSuccessURL = pDeletePurchaselistSuccessURL;
    }
  }

  // property deletePurchaselistErrorURL
  String mDeletePurchaselistErrorURL;
  public String getDeletePurchaselistErrorURL () {
    return mDeletePurchaselistErrorURL;
  }
  public void setDeletePurchaselistErrorURL (String pDeletePurchaselistErrorURL) {
    if (mDeletePurchaselistErrorURL != pDeletePurchaselistErrorURL) {
      mDeletePurchaselistErrorURL = pDeletePurchaselistErrorURL;
    }
  }

  // property addItemToPurchaselistSuccessURL
  String mAddItemToPurchaselistSuccessURL;
  public String getAddItemToPurchaselistSuccessURL () {
    return mAddItemToPurchaselistSuccessURL;
  }
  public void setAddItemToPurchaselistSuccessURL (String pAddItemToPurchaselistSuccessURL) {
    if (mAddItemToPurchaselistSuccessURL != pAddItemToPurchaselistSuccessURL) {
      mAddItemToPurchaselistSuccessURL = pAddItemToPurchaselistSuccessURL;
    }
  }

  // property addItemToPurchaselistErrorURL
  String mAddItemToPurchaselistErrorURL;
  public String getAddItemToPurchaselistErrorURL () {
    return mAddItemToPurchaselistErrorURL;
  }
  public void setAddItemToPurchaselistErrorURL (String pAddItemToPurchaselistErrorURL) {
    if (mAddItemToPurchaselistErrorURL != pAddItemToPurchaselistErrorURL) {
      mAddItemToPurchaselistErrorURL = pAddItemToPurchaselistErrorURL;
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
  public PurchaselistFormHandlerSuper ()
  {
  }

  //-------------------------------------
  // copyPropertiesTo method
  //-------------------------------------
  public void copyPropertiesTo (PurchaselistFormHandlerSuper target)
  {
    target.setPurchaseListId (getPurchaseListId ());
    target.setListName (getListName ());
    target.setCreatePurchaselistSuccessURL (getCreatePurchaselistSuccessURL ());
    target.setCreatePurchaselistErrorURL (getCreatePurchaselistErrorURL ());
    target.setSavePurchaselistSuccessURL (getSavePurchaselistSuccessURL ());
    target.setSavePurchaselistErrorURL (getSavePurchaselistErrorURL ());
    target.setUpdatePurchaselistSuccessURL (getUpdatePurchaselistSuccessURL ());
    target.setUpdatePurchaselistErrorURL (getUpdatePurchaselistErrorURL ());
    target.setUpdatePurchaselistItemsSuccessURL (getUpdatePurchaselistItemsSuccessURL ());
    target.setUpdatePurchaselistItemsErrorURL (getUpdatePurchaselistItemsErrorURL ());
    target.setDeletePurchaselistSuccessURL (getDeletePurchaselistSuccessURL ());
    target.setDeletePurchaselistErrorURL (getDeletePurchaselistErrorURL ());
    target.setAddItemToPurchaselistSuccessURL (getAddItemToPurchaselistSuccessURL ());
    target.setAddItemToPurchaselistErrorURL (getAddItemToPurchaselistErrorURL ());
  }

  //-------------------------------------
  // toString method
  //-------------------------------------
  public String toString ()
  {
    StringBuffer buf = new StringBuffer ();
    buf.append (getClass ().getName ());
    buf.append ("(");
    buf.append ("purchaseListId: " + getPurchaseListId() + "  ");
    buf.append ("listName: " + getListName() + "  ");
    buf.append ("createPurchaselistSuccessURL: " + getCreatePurchaselistSuccessURL() + "  ");
    buf.append ("createPurchaselistErrorURL: " + getCreatePurchaselistErrorURL() + "  ");
    buf.append ("savePurchaselistSuccessURL: " + getSavePurchaselistSuccessURL() + "  ");
    buf.append ("savePurchaselistErrorURL: " + getSavePurchaselistErrorURL() + "  ");
    buf.append ("updatePurchaselistSuccessURL: " + getUpdatePurchaselistSuccessURL() + "  ");
    buf.append ("updatePurchaselistErrorURL: " + getUpdatePurchaselistErrorURL() + "  ");
    buf.append ("updatePurchaselistItemsSuccessURL: " + getUpdatePurchaselistItemsSuccessURL() + "  ");
    buf.append ("updatePurchaselistItemsErrorURL: " + getUpdatePurchaselistItemsErrorURL() + "  ");
    buf.append ("deletePurchaselistSuccessURL: " + getDeletePurchaselistSuccessURL() + "  ");
    buf.append ("deletePurchaselistErrorURL: " + getDeletePurchaselistErrorURL() + "  ");
    buf.append ("addItemToPurchaselistSuccessURL: " + getAddItemToPurchaselistSuccessURL() + "  ");
    buf.append ("addItemToPurchaselistErrorURL: " + getAddItemToPurchaselistErrorURL() + "  ");
    buf.append (")");
    return buf.toString ();
  }

  //-------------------------------------
  // equals method
  //-------------------------------------
  public boolean equals (Object pObj)
  {
    if (!(pObj instanceof PurchaselistFormHandlerSuper)) return false;
    PurchaselistFormHandlerSuper obj = (PurchaselistFormHandlerSuper) pObj;

    if (this.getPurchaseListId () != obj.getPurchaseListId () &&
        (this.getPurchaseListId () == null ||
         obj.getPurchaseListId () == null ||
         !this.getPurchaseListId ().equals (obj.getPurchaseListId ()))) return false;
    if (this.getListName () != obj.getListName () &&
        (this.getListName () == null ||
         obj.getListName () == null ||
         !this.getListName ().equals (obj.getListName ()))) return false;
    if (this.getCreatePurchaselistSuccessURL () != obj.getCreatePurchaselistSuccessURL () &&
        (this.getCreatePurchaselistSuccessURL () == null ||
         obj.getCreatePurchaselistSuccessURL () == null ||
         !this.getCreatePurchaselistSuccessURL ().equals (obj.getCreatePurchaselistSuccessURL ()))) return false;
    if (this.getCreatePurchaselistErrorURL () != obj.getCreatePurchaselistErrorURL () &&
        (this.getCreatePurchaselistErrorURL () == null ||
         obj.getCreatePurchaselistErrorURL () == null ||
         !this.getCreatePurchaselistErrorURL ().equals (obj.getCreatePurchaselistErrorURL ()))) return false;
    if (this.getSavePurchaselistSuccessURL () != obj.getSavePurchaselistSuccessURL () &&
        (this.getSavePurchaselistSuccessURL () == null ||
         obj.getSavePurchaselistSuccessURL () == null ||
         !this.getSavePurchaselistSuccessURL ().equals (obj.getSavePurchaselistSuccessURL ()))) return false;
    if (this.getSavePurchaselistErrorURL () != obj.getSavePurchaselistErrorURL () &&
        (this.getSavePurchaselistErrorURL () == null ||
         obj.getSavePurchaselistErrorURL () == null ||
         !this.getSavePurchaselistErrorURL ().equals (obj.getSavePurchaselistErrorURL ()))) return false;
    if (this.getUpdatePurchaselistSuccessURL () != obj.getUpdatePurchaselistSuccessURL () &&
        (this.getUpdatePurchaselistSuccessURL () == null ||
         obj.getUpdatePurchaselistSuccessURL () == null ||
         !this.getUpdatePurchaselistSuccessURL ().equals (obj.getUpdatePurchaselistSuccessURL ()))) return false;
    if (this.getUpdatePurchaselistErrorURL () != obj.getUpdatePurchaselistErrorURL () &&
        (this.getUpdatePurchaselistErrorURL () == null ||
         obj.getUpdatePurchaselistErrorURL () == null ||
         !this.getUpdatePurchaselistErrorURL ().equals (obj.getUpdatePurchaselistErrorURL ()))) return false;
    if (this.getUpdatePurchaselistItemsSuccessURL () != obj.getUpdatePurchaselistItemsSuccessURL () &&
        (this.getUpdatePurchaselistItemsSuccessURL () == null ||
         obj.getUpdatePurchaselistItemsSuccessURL () == null ||
         !this.getUpdatePurchaselistItemsSuccessURL ().equals (obj.getUpdatePurchaselistItemsSuccessURL ()))) return false;
    if (this.getUpdatePurchaselistItemsErrorURL () != obj.getUpdatePurchaselistItemsErrorURL () &&
        (this.getUpdatePurchaselistItemsErrorURL () == null ||
         obj.getUpdatePurchaselistItemsErrorURL () == null ||
         !this.getUpdatePurchaselistItemsErrorURL ().equals (obj.getUpdatePurchaselistItemsErrorURL ()))) return false;
    if (this.getDeletePurchaselistSuccessURL () != obj.getDeletePurchaselistSuccessURL () &&
        (this.getDeletePurchaselistSuccessURL () == null ||
         obj.getDeletePurchaselistSuccessURL () == null ||
         !this.getDeletePurchaselistSuccessURL ().equals (obj.getDeletePurchaselistSuccessURL ()))) return false;
    if (this.getDeletePurchaselistErrorURL () != obj.getDeletePurchaselistErrorURL () &&
        (this.getDeletePurchaselistErrorURL () == null ||
         obj.getDeletePurchaselistErrorURL () == null ||
         !this.getDeletePurchaselistErrorURL ().equals (obj.getDeletePurchaselistErrorURL ()))) return false;
    if (this.getAddItemToPurchaselistSuccessURL () != obj.getAddItemToPurchaselistSuccessURL () &&
        (this.getAddItemToPurchaselistSuccessURL () == null ||
         obj.getAddItemToPurchaselistSuccessURL () == null ||
         !this.getAddItemToPurchaselistSuccessURL ().equals (obj.getAddItemToPurchaselistSuccessURL ()))) return false;
    if (this.getAddItemToPurchaselistErrorURL () != obj.getAddItemToPurchaselistErrorURL () &&
        (this.getAddItemToPurchaselistErrorURL () == null ||
         obj.getAddItemToPurchaselistErrorURL () == null ||
         !this.getAddItemToPurchaselistErrorURL ().equals (obj.getAddItemToPurchaselistErrorURL ()))) return false;
    return true;
  }

  //-------------------------------------
  // hashCode method
  //-------------------------------------
  public int hashCode ()
  {
    int ret = 0;
    ret *= 29;
    if (this.getPurchaseListId () != null)
      ret += this.getPurchaseListId ().hashCode ();
    ret *= 29;
    if (this.getListName () != null)
      ret += this.getListName ().hashCode ();
    ret *= 29;
    if (this.getCreatePurchaselistSuccessURL () != null)
      ret += this.getCreatePurchaselistSuccessURL ().hashCode ();
    ret *= 29;
    if (this.getCreatePurchaselistErrorURL () != null)
      ret += this.getCreatePurchaselistErrorURL ().hashCode ();
    ret *= 29;
    if (this.getSavePurchaselistSuccessURL () != null)
      ret += this.getSavePurchaselistSuccessURL ().hashCode ();
    ret *= 29;
    if (this.getSavePurchaselistErrorURL () != null)
      ret += this.getSavePurchaselistErrorURL ().hashCode ();
    ret *= 29;
    if (this.getUpdatePurchaselistSuccessURL () != null)
      ret += this.getUpdatePurchaselistSuccessURL ().hashCode ();
    ret *= 29;
    if (this.getUpdatePurchaselistErrorURL () != null)
      ret += this.getUpdatePurchaselistErrorURL ().hashCode ();
    ret *= 29;
    if (this.getUpdatePurchaselistItemsSuccessURL () != null)
      ret += this.getUpdatePurchaselistItemsSuccessURL ().hashCode ();
    ret *= 29;
    if (this.getUpdatePurchaselistItemsErrorURL () != null)
      ret += this.getUpdatePurchaselistItemsErrorURL ().hashCode ();
    ret *= 29;
    if (this.getDeletePurchaselistSuccessURL () != null)
      ret += this.getDeletePurchaselistSuccessURL ().hashCode ();
    ret *= 29;
    if (this.getDeletePurchaselistErrorURL () != null)
      ret += this.getDeletePurchaselistErrorURL ().hashCode ();
    ret *= 29;
    if (this.getAddItemToPurchaselistSuccessURL () != null)
      ret += this.getAddItemToPurchaselistSuccessURL ().hashCode ();
    ret *= 29;
    if (this.getAddItemToPurchaselistErrorURL () != null)
      ret += this.getAddItemToPurchaselistErrorURL ().hashCode ();
    return ret;
  }

}
