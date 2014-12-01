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

package atg.commerce.order;

import java.io.Serializable;
import atg.repository.RepositoryItem;
import atg.commerce.fulfillment.scenario.ScenarioEvent;

/**
 * This class is a message object which is sent when an item is added to an Order or
 * an item's quantity is increased.
 *
 * @author Sam Perman
 * @beaninfo 
 *          description: ItemAddedToOrder is a message that gets generated when a new item
 *                       is added to an existing order.
 *          displayname: ItemAddedToOrder
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/ItemAddedToOrder.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ItemAddedToOrder extends ScenarioEvent {
    //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/ItemAddedToOrder.java#2 $$Change: 651448 $";

  public static final String TYPE =
      "atg.commerce.order.ItemAddedToOrder";



  //---------------------------------------------------------------------------
  // property:Id
  //---------------------------------------------------------------------------

  private String mId;
  public void setId(String pId) {
    mId = pId;
  }

  /**
   * This is the Id of this message.
   * @beaninfo 
   *          description: This is the Id of this message.
   *          displayname: Id
   **/
  public String getId() {
    return mId;
  }

  //---------------------------------------------------------------------------
  // property:Order
  //---------------------------------------------------------------------------

  private Order mOrder;
  public void setOrder(Order pOrder) {
    mOrder = pOrder;
  }

  /**
   * The order that the item was added to
   * @beaninfo 
   *          description: The order that the item was added to
   *          displayname: Order
   **/
  public Order getOrder() {
    return mOrder;
  }


  //---------------------------------------------------------------------------
  // property:CatalogRef
  //---------------------------------------------------------------------------

  private Object mCatalogRef;
  public void setCatalogRef(Object pCatalogRef) {
    mCatalogRef = pCatalogRef;
  }

  /**
   * The catalogRef that is associated with the item.
   * @beaninfo 
   *            description: The catalogRef that is associated with the item.
   *            displayname: Sku
   **/
  public Object getCatalogRef() {
    return mCatalogRef;
  }

  //---------------------------------------------------------------------------
  // property:Product
  //---------------------------------------------------------------------------

  private Object mProduct;
  public void setProduct(Object pProduct) {
    mProduct = pProduct;
  }

  /**
   * The product that is associated with the item.
   * @beaninfo 
   *          description: The product that is associated with the item.
   *          displayname: Product
   **/
  public Object getProduct() {
    return mProduct;
  }


  //---------------------------------------------------------------------------
  // property:CommerceItem
  //---------------------------------------------------------------------------

  private CommerceItem mCommerceItem;
  public void setCommerceItem(CommerceItem pCommerceItem) {
    mCommerceItem = pCommerceItem; 
    setQuantity(pCommerceItem.getQuantity());
    if(pCommerceItem.getPriceInfo() != null)
      setAmount(pCommerceItem.getPriceInfo().getAmount());
  }

  /**
   * The recently added or updated item
   * @beaninfo 
   *          description: The recently added or updated item
   *          displayname: Commerce item
   **/
  public CommerceItem getCommerceItem() {
    return mCommerceItem;
  } 


  //---------------------------------------------------------------------------
  // property:Quantity
  //---------------------------------------------------------------------------

  private long mQuantity;
  public void setQuantity(long pQuantity) {
    mQuantity = pQuantity;
  }

  /**
   * The quantity added to the item
   * @beaninfo
   *          description: The quantity added to the item
   *          displayname: Quantity
   **/
  public long getQuantity() {
    return mQuantity;
  }


  //---------------------------------------------------------------------------
  // property:Amount
  //---------------------------------------------------------------------------

  private double mAmount;
  public void setAmount(double pAmount) {
    mAmount = pAmount;
  }

  /**
   * The price of the new or updated item
   * @beaninfo 
   *          description: the price of the new or updated item
   *          displayname: Amount
   **/
  public double getAmount() {
    return mAmount;
  }

  /**
   * the type of this event.
   * @beaninfo 
   *          description: the type of this event.
   *          displayname: Type
   **/
  public String getType() {
    return TYPE;
  }

  public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append("ItemAddedToOrder:\n");

      if (getOrder() != null)
        sb.append("    order  = " + getOrder().getId() + "\n");
      else
        sb.append("    order  = " + getOrder() + "\n");
      
      if (getCommerceItem() != null)
        sb.append("    item   = " + getCommerceItem().getId() + "\n");
      else
        sb.append("    item   = " + getCommerceItem() + "\n");
      
      return appendBaseToString(sb);
  }
    
} // ItemAddedToOrder

