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

package atg.projects.store.order;

import atg.commerce.order.OrderManager;

import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import atg.repository.rql.RqlStatement;


/**
 * This class is only used during development. It contains no-arg
 * void methods to clear orders from the order repository. These
 * methods can be called from the component browser of the
 * component. Unless you want to wipe all the orders from the
 * database, do not use this in any environment other than
 * development.
 *
 * @author ATG
 */
public class OrderRepositoryHelper {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/OrderRepositoryHelper.java#3 $$Change: 635816 $";

  /**
   * Order manager.
   */
  private OrderManager mOrderManager;

  /**
   * Method to remove orders from the repository.
   * @param orderType - order type
   */
  public void deleteOrders(String orderType) {
    if (orderType == null) {
      orderType = "order";
    }

    try {
      Repository repository = getOrderManager().getOrderTools().getOrderRepository();
      String itemDescriptor = orderType;
      RepositoryView view = repository.getView(itemDescriptor);

      Object[] params = {  };
      RqlStatement statement = RqlStatement.parseRqlStatement("ALL");

      RepositoryItem[] items = statement.executeQuery(view, params);

      for (int i = 0; i < items.length; i++) {
        getOrderManager().removeOrder(items[i].getRepositoryId());
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Delete all orders.
   */
  public void deleteAllOrders() {
    deleteOrders("order");
  }

  /**
   * @return order manager.
   */
  public OrderManager getOrderManager() {
    return mOrderManager;
  }

  /**
   * @param manager - order manager.
   */
  public void setOrderManager(OrderManager manager) {
    mOrderManager = manager;
  }
}
