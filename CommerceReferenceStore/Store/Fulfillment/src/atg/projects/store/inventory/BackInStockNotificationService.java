/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2011 Art Technology Group, Inc.
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

import atg.commerce.fulfillment.UpdateInventory;
import atg.dms.patchbay.MessageSink;
import atg.nucleus.GenericService;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.profile.StorePropertyManager;
import atg.repository.MutableRepository;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailSender;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;


/**
 * This class will be used to notify users when an item is back in stock. It
 * will subscribe to a queue to listen for "UpdateInventory" messages which are
 * only fired when an item goes from Out of Stock to In Stock.
 *
 * @author ATG
 * @version $Id: BackInStockNotificationService.java,v 1.6 2004/07/24 20:39:40
 *          twoodward Exp $
 */
public class BackInStockNotificationService extends GenericService implements MessageSink {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/Fulfillment/src/atg/projects/store/inventory/BackInStockNotificationService.java#2 $$Change: 651448 $";

  /**
   * Sku id parameter name.
   */
  public static final String PARAM_SKU_ID = "skuId";

  /**
   * Product id parameter name.
   */
  public static final String PARAM_PRODUCT_ID = "productId";

  /**
   * RQL query to find items.
   */
  protected static String RQL_QUERY_FIND_BISN_ITEMS = "catalogRefId = ?0";

  /**
   * Template e-mail sender.
   */
  protected TemplateEmailSender mTemplateEmailSender;

  /**
   * Repository.
   */
  protected Repository mProfileRepository;

  /**
   * Property manager.
   */
  protected StorePropertyManager mPropertyManager;

  /**
   * Template e-mail information.
   */
  protected TemplateEmailInfo mTemplateEmailInfo;

  /**
   * Initialize service in this method.
   */
  public void doStartService() {
  }

  /**
   * The method called when a message is delivered.
   *
   * @param pPortName - the message port
   * @param pMessage - the JMS message being received
   * @throws JMSException if message error occurs
   *
   */
  public void receiveMessage(String pPortName, Message pMessage)
    throws JMSException {
    String messageType = pMessage.getJMSType();

    if (isLoggingDebug()) {
      logDebug("Received message of type " + messageType + "  " + pMessage);
    }

    if (messageType.equals(UpdateInventory.TYPE)) {
      if (pMessage instanceof ObjectMessage) {
        UpdateInventory message = (UpdateInventory) ((ObjectMessage) pMessage).getObject();
        sendBackInStockNotifications(message);
      }
    }
  }

  /**
   * Notify users when an item is back in stock.
   *
   * @param pMessage - message to send
   */
  protected void sendBackInStockNotifications(UpdateInventory pMessage) {
    String[] skuIds = pMessage.getItemIds();
    String emailProp = getPropertyManager().getBisnEmailPropertyName();

    try {
      for (int i = 0; i < skuIds.length; i++) {
        String skuId = skuIds[i];

        RepositoryItem[] items = retrieveBackInStockNotifyItems(skuId);

        if (items != null) {
          sendEmail(items);
          deleteItemsFromRepository(items);
        }
      }
    } catch (Exception ex) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("There was a problem in sending back in stock notifications."), ex);
      }
    }
  }

  /**
   * Helper method to delete repository items.
   *
   * @param pItems - items to delete
   * @throws RepositoryException if repository error occurs
   */
  protected void deleteItemsFromRepository(RepositoryItem[] pItems)
    throws RepositoryException {
    MutableRepository repository = (MutableRepository) getProfileRepository();
    String itemDescriptor = getPropertyManager().getBackInStockNotifyItemDescriptorName();

    for (int i = 0; i < pItems.length; i++) {
      repository.removeItem(pItems[i].getRepositoryId(), itemDescriptor);
    }
  }

  /**
   * Helper method to do the actual email sending.
   *
   * @param pItems - items
   * @throws TemplateEmailException If an exception occurs while fetching the 
   *                                template for the emial to be send.
   */
  protected void sendEmail(RepositoryItem[] pItems) throws TemplateEmailException {
    TemplateEmailInfo templateInfo = getTemplateEmailInfo();
    StorePropertyManager pM = getPropertyManager();

    for (int i = 0; i < pItems.length; i++) {
      String skuId = (String) pItems[i].getPropertyValue(pM.getBisnSkuIdPropertyName());
      String productId = (String) pItems[i].getPropertyValue(pM.getBisnProductIdPropertyName());
      Object[] email = { pItems[i].getPropertyValue(pM.getBisnEmailPropertyName()) };

      Map params = new HashMap();
      params.put(PARAM_SKU_ID, skuId);
      params.put(PARAM_PRODUCT_ID, productId);
      params.put(pM.getLocalePropertyName(), pItems[i].getPropertyValue(pM.getLocalePropertyName()));

      TemplateEmailInfo info = templateInfo.copy();
      info.setTemplateParameters(params);
      info.setSiteId((String) pItems[i].getPropertyValue(StoreInventoryManager.PARAM_SITE_ID));
      getTemplateEmailSender().sendEmailMessage(info, email);
    }

    if (isLoggingDebug()) {
      logDebug("Done sending back in stock notification emails.");
    }
  }

  /**
   * Perform the query to retrieve appropriate back in stock repository items.
   *
   * @param pSkuId - sku ids
   * @return repository items
   * @throws RepositoryException if repository error occurs
   */
  protected RepositoryItem[] retrieveBackInStockNotifyItems(String pSkuId)
    throws RepositoryException {
    Repository repository = getProfileRepository();
    String itemDescriptor = getPropertyManager().getBackInStockNotifyItemDescriptorName();
    RepositoryView view = repository.getView(itemDescriptor);

    Object[] params = new Object[] { pSkuId };

    RqlStatement statement = RqlStatement.parseRqlStatement(RQL_QUERY_FIND_BISN_ITEMS);

    RepositoryItem[] items = statement.executeQuery(view, params);

    return items;
  }

  /**
   * @return template e-mail semder information.
   */
  public TemplateEmailSender getTemplateEmailSender() {
    return mTemplateEmailSender;
  }

  /**
   * @param sender - template e-mail sender information.
   */
  public void setTemplateEmailSender(TemplateEmailSender sender) {
    mTemplateEmailSender = sender;
  }

  /**
   * @return profile repository.
   */
  public Repository getProfileRepository() {
    return mProfileRepository;
  }

  /**
   * @param repository - profile repository.
   */
  public void setProfileRepository(Repository repository) {
    mProfileRepository = repository;
  }

  /**
   * @return property manager.
   */
  public StorePropertyManager getPropertyManager() {
    return mPropertyManager;
  }

  /**
   * @param manager - property manager.
   */
  public void setPropertyManager(StorePropertyManager manager) {
    mPropertyManager = manager;
  }

  /**
   * @return template e-mail information.
   */
  public TemplateEmailInfo getTemplateEmailInfo() {
    return mTemplateEmailInfo;
  }

  /**
   * @param info - template e-mail information.
   */
  public void setTemplateEmailInfo(TemplateEmailInfo info) {
    mTemplateEmailInfo = info;
  }
}
