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

package atg.commerce.order.processor;

// dcs
import atg.commerce.order.*;
import atg.commerce.gifts.*;
import atg.commerce.*;

// atg
import atg.repository.*;
import atg.service.pipeline.*;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.dtm.*;
import atg.beans.*;
import atg.nucleus.logging.ApplicationLoggingImpl;

// java
import java.util.*;
import javax.transaction.*;
import java.text.*;

/**
 * Check the shipping groups of an order for Handlers.  Update the
 * gift repository where appropriate.
 *
 * @author Sam Perman
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcUpdateGiftRepository.java#2 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class ProcUpdateGiftRepository extends ApplicationLoggingImpl implements PipelineProcessor {
  
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcUpdateGiftRepository.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";
  static String DISPLAY_NAME_PROPERTY = "displayName";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;

  //-------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The processor completed
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] codes = {SUCCESS};
    return codes;
  }

  //---------------------------------------------------------------------------
  // property:GiftlistManager
  //---------------------------------------------------------------------------
  private GiftlistManager mGiftlistManager = null;

  public void setGiftlistManager(GiftlistManager pGiftlistManager) {
    mGiftlistManager = pGiftlistManager;
  }

  /**
   * All Giftlist manipulations are done with this.
   **/
  public GiftlistManager getGiftlistManager() {
    return mGiftlistManager;
  }


  //---------------------------------------------------------------------------
  // property:OrderManager
  //---------------------------------------------------------------------------

  private OrderManager mOrderManager;
  public void setOrderManager(OrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  /**
   * The class responsible for managing changes to orders
   **/
  public OrderManager getOrderManager() {
    return mOrderManager;
  }


  //---------------------------------------------------------------------------
  // property:TransactionManager
  //---------------------------------------------------------------------------

  private TransactionManager mTransactionManager;
  public void setTransactionManager(TransactionManager pTransactionManager) {
    mTransactionManager = pTransactionManager;
  }

  /**
   * Component that controls management of the transactions
   **/
  public TransactionManager getTransactionManager() {
    return mTransactionManager;
  }
  
  //---------------------------------------------------------------------------
  // property:CatalogRefDisplayNameProperty
  //---------------------------------------------------------------------------

  private String mCatalogRefDisplayNameProperty = DISPLAY_NAME_PROPERTY;
  public void setCatalogRefDisplayNameProperty(String pCatalogRefDisplayNameProperty) {
    mCatalogRefDisplayNameProperty = pCatalogRefDisplayNameProperty;
  }

  /**
   * The property of a catalog ref item that is the display name
   **/
  public String getCatalogRefDisplayNameProperty() {
    return mCatalogRefDisplayNameProperty;
  }

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcUpdateGiftRepository";

  /**
   * Sets property LoggingIdentifier
   **/
  public void setLoggingIdentifier(String pLoggingIdentifier) {
    mLoggingIdentifier = pLoggingIdentifier;
  }

  /**
   * Returns property LoggingIdentifier
   **/
  public String getLoggingIdentifier() {
    return mLoggingIdentifier;
  }

  /**
   * This method runs the actual processor and does the work. pParam is user data and
   * pResult is an object which contains result data. The return value of the method is
   * what determines what the next link to be executed is. 
   *
   * This method requires that an Order and optionally a Locale object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * If the giftlist has been removed, this method adds an exception to the result
   * and removes the offending HandlingInstruction.
   *
   * @param pParam a HashMap which must contain an Order and optionally a Locale object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    List shippingGroupList = order.getShippingGroups();
    ShippingGroup sg = null;
    List handlingList = null;

    if(shippingGroupList == null)
      return SUCCESS;

    ResourceBundle bundle = null;
    Locale resourceLocale = (Locale) map.get(PipelineConstants.LOCALE);
    if (resourceLocale != null) {
      bundle = LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, resourceLocale);
    } else {
      bundle = sResourceBundle;
    }

    Iterator shippingGroups = shippingGroupList.iterator();
    while(shippingGroups.hasNext()) {
      sg = (ShippingGroup) shippingGroups.next();
      handlingList = sg.getHandlingInstructions();
      if((handlingList == null) || (handlingList.size() == 0)) {
        continue;
      }
      else {
        Iterator handlings = handlingList.iterator();
        HandlingInstruction hi = null;
        int code;
        while(handlings.hasNext()) {
          hi = (HandlingInstruction) handlings.next();

          if(hi instanceof GiftlistHandlingInstruction) {
            // we only care about giftlists
            GiftlistHandlingInstruction ghi = (GiftlistHandlingInstruction) hi;
            CommerceItem ci = order.getCommerceItem(ghi.getCommerceItemId());
            if(ci == null) {
              // <TBD> error?
              continue;
            }

            code = updateGiftlistItem(order, ghi.getId(), sg.getId(), ghi.getGiftlistId(), ghi.getGiftlistItemId(),
                                      ci, ghi.getQuantity(), bundle, pResult);
            if(code != SUCCESS)
              return code;
          }
          else {

          }
        }
      }
      
    }

    return SUCCESS;
  }

  /**
   * This method increments the quantityPurchased property for the
   * given item in the given Giftlist by some quantity.  This method is deprecated,
   * you should use the other updateGiftlistItem method instead.
   *
   * @param pOrder The order containing the gift.
   * @param pGiftlistId The id of the giftlist to be updated.
   * @param pGiftlistItemId The id of the giftlist item to be updated.
   * @param pCommerceItem The commerce item in the giftlist to be updated.   
   * @param pQuantity The quantity purchased
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @deprecated
   **/
  protected void updateGiftlistItem(Order pOrder, String pGiftlistId, String pGiftlistItemId,
                                    CommerceItem pCommerceItem, long pQuantity, PipelineResult pResult)
  {
    GiftlistManager giftlistManager = getGiftlistManager();
    
    boolean success = giftlistManager.increaseGiftlistItemQuantityPurchased(pGiftlistId, pGiftlistItemId, pQuantity);
    
    if(!success) {
      String[] msgArgs = { pGiftlistItemId };
      String msg = ResourceUtils.getMsgResource("CouldNotUpdateGiftItemQuantityPurchased",
                                              MY_RESOURCE_NAME, sResourceBundle, msgArgs);
      pResult.addError("CouldNotUpdateGiftItemQuantityPurchased", msg);
    }      
  }

  /**
   * This method increments the quantityPurchased property for the
   * given item in the given Giftlist by some quantity.
   * This method will also remove the handling instruction, if the 
   * giftlist or giftlist item has been removed.  If the handling
   * instruction is removed, the method returns STOP_CHAIN_EXECUTION_AND_ROLLBACK
   *
   * @param pOrder The order containing the gift.
   * @param pGiftlistId The id of the giftlist to be updated.
   * @param pGiftlistItemId The id of the giftlist item to be updated.
   * @param pCommerceItem The commerce item in the giftlist to be updated.   
   * @param pQuantity The quantity purchased
   * @param pBundle resource bundle specific to users locale
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   **/
  protected int updateGiftlistItem(Order pOrder, String pHandlingInstructionId, String pShippingGroupId,
                                   String pGiftlistId, String pGiftlistItemId,
                                   CommerceItem pCommerceItem, long pQuantity, ResourceBundle pBundle,
                                   PipelineResult pResult)
  {
    GiftlistManager giftlistManager = getGiftlistManager();
    
    try {
      Object giftlist = giftlistManager.getGiftlist(pGiftlistId);
      if(giftlist == null) {
        String msg = pBundle.getString("GiftlistDoesNotExist");
        pResult.addError("GiftlistDoesNotExist", msg);
        prepareToRemoveHandlingInstruction(pOrder, pShippingGroupId, pHandlingInstructionId, pResult);
        return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
      }
      else {
        Object giftlistItem = giftlistManager.getGiftitem(pGiftlistItemId);
        if(giftlistItem == null) {
          String displayName = "";
          try {
            Object catalogRef = pCommerceItem.getAuxiliaryData().getCatalogRef();
            displayName = (String) DynamicBeans.getPropertyValue(catalogRef, getCatalogRefDisplayNameProperty());
          }
          catch(Exception e) {
            // do nothing... use empty string
          }
       
          String msg = MessageFormat.format(pBundle.getString("GiftlistItemDoesNotExist"), displayName);
          pResult.addError("GiftlistItemDoesNotExist", msg);
          prepareToRemoveHandlingInstruction(pOrder, pShippingGroupId, pHandlingInstructionId, pResult);
          return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
        }
      }
    }
    catch(CommerceException c) {
      pResult.addError("CouldNotUpdateGiftItemQuantityPurchased", c.getMessage());
      return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
    }

    boolean success = giftlistManager.increaseGiftlistItemQuantityPurchased(pGiftlistId, pGiftlistItemId, pQuantity);
    
    if(!success) {
      String[] msgArgs = { pGiftlistItemId };
      String msg = ResourceUtils.getMsgResource("CouldNotUpdateGiftItemQuantityPurchased",
                                              MY_RESOURCE_NAME, sResourceBundle, msgArgs);
      pResult.addError("CouldNotUpdateGiftItemQuantityPurchased", msg);
    } 
    
    return SUCCESS;
  }

  /**
   * Register with the transaction.  We know it will be rolling back.  When it is finished, remove
   * the offending handling instruction so we don't have any errors.
   *
   * @param pOrder The order containing the handling instruction
   * @param pShippingGroupId The shipping group containing the handling instruction
   * @param pHandlingInstructionId The handling instruction that should be removed
   * @param pResult The pipeline result object
   **/
  protected void prepareToRemoveHandlingInstruction(Order pOrder, String pShippingGroupId, 
                                          String pHandlingInstructionId, PipelineResult pResult)
    throws CommerceException
  {
    try {
      Transaction t = getTransactionManager().getTransaction();
      if(t != null) {
        GiftRepositorySynchronizer grs = new GiftRepositorySynchronizer(t);
        grs.setOrder(pOrder);
        grs.setShippingGroupId(pShippingGroupId);
        grs.setHandlingInstructionId(pHandlingInstructionId);
        grs.setPipelineResult(pResult);
      }
    }
    catch(Exception e) {
      throw new CommerceException(e);
    }
  }

  /**
   * Remove the given handling instruction from the order.
   * The removal is done within the context of a new transaction.
   **/
  protected void removeHandlingInstruction(Order pOrder, String pShippingGroupId, String pHandlingInstructionId)
    throws CommerceException
  {
    OrderManager om = getOrderManager();

    if(om == null)
      throw new CommerceException(ResourceUtils.getMsgResource("NullOrderManager",
                                                               MY_RESOURCE_NAME, sResourceBundle));
    
    // start a new transaction to remove the handling instruction
    TransactionDemarcation td = new TransactionDemarcation();
    
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

      // remove the handling instruction
      om.getHandlingInstructionManager().removeHandlingInstructionFromShippingGroup(pOrder, pShippingGroupId, pHandlingInstructionId);
    }
    catch(TransactionDemarcationException tde) {
      throw new CommerceException(tde);
    }
    finally {
      try {
        td.end();
      }
      catch(TransactionDemarcationException tde) {
        throw new CommerceException(tde);
      }
    }
  }

  /**
   *
   **/
  private class GiftRepositorySynchronizer implements javax.transaction.Synchronization {
    static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

    //---------------------------------------------------------------------------
    // property:Order
    //---------------------------------------------------------------------------
    
    private Order mOrder;
    public void setOrder(Order pOrder) {
      mOrder = pOrder;
    }
    
    /**
     * The order containing the offending handling instruction
     **/
    public Order getOrder() {
      return mOrder;
    }


    //---------------------------------------------------------------------------
    // property:ShippingGroupId
    //---------------------------------------------------------------------------

    private String mShippingGroupId;
    public void setShippingGroupId(String pShippingGroupId) {
      mShippingGroupId = pShippingGroupId;
    }

    /**
     * The id of the shipping group containing the handling instruction
     **/
    public String getShippingGroupId() {
      return mShippingGroupId;
    }


    //---------------------------------------------------------------------------
    // property:HandlingInstructionId
    //---------------------------------------------------------------------------

    private String mHandlingInstructionId;
    public void setHandlingInstructionId(String pHandlingInstructionId) {
      mHandlingInstructionId = pHandlingInstructionId;
    }

    /**
     * The id of the offending handling instruction
     **/
    public String getHandlingInstructionId() {
      return mHandlingInstructionId;
    }

    

    //---------------------------------------------------------------------------
    // property:PipelineResult
    //---------------------------------------------------------------------------

    private PipelineResult mPipelineResult;
    public void setPipelineResult(PipelineResult pPipelineResult) {
      mPipelineResult = pPipelineResult;
    }

    /**
     *  The result for the pipeline
     **/
    public PipelineResult getPipelineResult() {
      return mPipelineResult;
    }

    private Transaction localTransaction;

    /**
     * This requires that a transaction is already in place.  If the transaction is
     * null, an exception is thrown
     *
     * @param pTransaction The current transaction
     **/
    public GiftRepositorySynchronizer(Transaction pTransaction) 
      throws CommerceException
    {
      // <TBD> different exception
      if(pTransaction == null) {
        throw new CommerceException(ResourceUtils.getMsgResource("invalidTransaction", 
                                                                 MY_RESOURCE_NAME,
                                                                 sResourceBundle));
      } 
      
      localTransaction = pTransaction;
      
      try {
        localTransaction.registerSynchronization(this);
      }
      catch (Exception e) {
        throw new CommerceException(e);
      }    
    }
    
    /**
     * This method is called by the transaction 
     * manager prior to the start of the transaction completion process.
     */
    public void beforeCompletion()
    {
    }
    
    /**
     * This method is called by the transaction 
   * manager after the transaction is committed or rolled back. 
   * 
   * @param pStatus The status of the transaction completion.
   */
    public void afterCompletion(int pStatus)
    {
      try {
        removeHandlingInstruction(getOrder(), getShippingGroupId(), getHandlingInstructionId());
      }
      catch(CommerceException c) {
        getPipelineResult().addError("CouldNotRemoveHandlingInstruction", c.getMessage());
      }
    }
  }
} // ProcUpdateGiftRepository

