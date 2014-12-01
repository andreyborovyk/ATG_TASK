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

import atg.payment.avs.*;
import atg.core.util.Address;
import atg.commerce.order.*;
import atg.service.cache.Cache;
import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;
import atg.commerce.CommerceException;
import atg.nucleus.logging.ApplicationLoggingImpl;

import java.util.*;

/**
 * This class verifies addresses for ShippingGroups and PaymentGroups. Specifically
 * HardgoodShippingGroup and CreditCard. For each HardgoodShippingGroup and CreditCard
 * class the address is verified against the configured address verification processor.
 * Note that the AddressVerificationProcessor in no way is related to the PipelineProcessor
 * interface which this class implements. The AddressVerificationProcessor is a class
 * which implements the atg.payment.avs.AddressVerificationProcessor interface. By
 * default the configured AddressVerificationProcessor is
 * atg.commerce.payment.DummyAddressVerificationProcessor whose implementation is a
 * noop.
 *
 * To extend this class to verify addresses in other class types, override the runProcess()
 * method to call verifyAddress() for each address object in the desired classes then call
 * this class' runProcess() method to verify the addresses in the HardgoodShippingGroup and
 * CreditCard classes.
 *
 * To change the way addresses are verified override the verifyAddress() method. By default,
 * verifyAddress() method calls verifyAddress() in the addressVerificationProcessor passing
 * it a GenericAddressVerificationInfo object which it constructs using the parameter data
 * passed into the method.
 *
 * @see atg.payment.avs.AddressVerificationProcessor
 * @see atg.commerce.payment.DummyAddressVerificationProcessor
 * @see atg.commerce.order.HardgoodShippingGroup
 * @see atg.commerce.order.CreditCard
 * @see atg.service.pipeline.PipelineProcessor
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcVerifyOrderAddresses.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcVerifyOrderAddresses extends ApplicationLoggingImpl implements PipelineProcessor {  
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcVerifyOrderAddresses.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcVerifyOrderAddresses() {
  }

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The processor completed
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = {SUCCESS};
    return ret;
  } 

  //-------------------------------------
  // property: addressVerificationProcessor
  //-------------------------------------
  private AddressVerificationProcessor mAddressVerificationProcessor = null;

  /**
   * Returns the addressVerificationProcessor
   */
  public AddressVerificationProcessor getAddressVerificationProcessor() {
    return mAddressVerificationProcessor;
  }

  /**
   * Sets the addressVerificationProcessor
   */
  public void setAddressVerificationProcessor(AddressVerificationProcessor pAddressVerificationProcessor) {
    mAddressVerificationProcessor = pAddressVerificationProcessor;
  }

  //-------------------------------------
  // property: addressVerificationProcessor
  //-------------------------------------
  private boolean mUseCache;
  
  /**
   * Sets whether or not the results from verifying an address should be cached
   * @param pUseCache true if results should be cached
   */
  public void setUseCache(boolean pUseCache) {
    mUseCache = pUseCache;
  }
  
  /**
   * Returns whether or not address verification results are being cached
   * @return true if the results are being cached
   */
  public boolean isUseCache() {
    return mUseCache;
  }
  
  //-------------------------------------
  // property: addressVerificationCache
  //-------------------------------------
  private Cache mAddressVerificationCache;
  
  /**
   * Sets the addressVerificationCache to be used to cache the results of verifying an address
   * @param pAddressVerificationCache cache to use
   */
  public void setAddressVerificationCache(Cache pAddressVerificationCache) {
    mAddressVerificationCache = pAddressVerificationCache;
  }
  
  /**
   * Returns the addressVerificationCache
   * @return the addressVerificationCache
   */
  public Cache getAddressVerificationCache() {
    return mAddressVerificationCache;
  }
  
  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcVerifyOrderAddresses";

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

  //-----------------------------------------------
  /**
   * This method executes the address verification. It searches through the
   * ShippingGroup and PaymentGroup lists in the Order for HardgoodShippingGroup
   * and CreditCard objects. It then gets the address from them and calls
   * verifyAddress().
   *
   * This method requires that an Order and an OrderManager object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order and OrderManager object
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
    OrderManager orderManager = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);

    // check for null parameters
    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (orderManager == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderManagerParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));

    List paymentGroups = order.getPaymentGroups();
    List shippingGroups = order.getShippingGroups();
    int pgcount = paymentGroups.size();
    int sgcount = shippingGroups.size();
    List hardgoodSGList = new ArrayList(sgcount);
    List creditcardList = new ArrayList(pgcount);
    CreditCard cc;
    HardgoodShippingGroup hgsg;
    Address addr1 = null, addr2 = null;
    String addrId = null;
    Object o;
    
    // find all the CreditCard objects
    pgcount = 0;
    Iterator iter = paymentGroups.iterator();
    while (iter.hasNext()) {
      o = iter.next();
      if (o instanceof CreditCard) {
        creditcardList.add(o);
        pgcount++;
      }
    }

    // find all the HardgoodShippingGroup objects
    sgcount = 0;
    iter = shippingGroups.iterator(); 
    while (iter.hasNext()) {
      o = iter.next();
      if (o instanceof HardgoodShippingGroup) {
        hardgoodSGList.add(o);
        sgcount++;
      }
    }

    //Verify all payment groups
    for(int i = 0; i < pgcount; i++) {
      cc = (CreditCard) creditcardList.get(i);
      
      if(addr1 == null) {
        //If the address hasn't be verified already then we need to verify it,
        //but will first try to get a pair of addresses to verify
        if(!hasBeenVerified(cc.getBillingAddress(), cc.getId(), pResult)) {
          addr1 = cc.getBillingAddress();
          addrId = cc.getId();
        }
      } else {
        if(!hasBeenVerified(cc.getBillingAddress(), cc.getId(), pResult)) {
          //We have a pair of addresses that need to be verified.
          addr2 = cc.getBillingAddress();
          verifyAddress(addrId, addr1, addr2, pResult, orderManager);
        
          addr1 = null; //Null the first address so we can make another pair
        }
      }
    }
    
    //Verify all shipping groups
    for(int i = 0; i < sgcount; i++) {
      hgsg = (HardgoodShippingGroup)hardgoodSGList.get(i);
      
      if(addr1 == null) {
        //If the address hasn't be verified already then we need to verify it,
        //but will first try to get a pair of addresses to verify
        if(!hasBeenVerified(hgsg.getShippingAddress(), hgsg.getId(), pResult)) {
          addr1 = hgsg.getShippingAddress();
          addrId = hgsg.getId();
        }
      } else {
        if(!hasBeenVerified(hgsg.getShippingAddress(), hgsg.getId(), pResult)) {
          //We have a pair of addresses that need to be verified.
          addr2 = hgsg.getShippingAddress();
          verifyAddress(addrId, addr1, addr2, pResult, orderManager);
          
          addr1 = null; //Null the first address so we can make another pair
        }
      }
    }
    
    //Handle if there are an odd number of addresses waiting to be verified
    if(addr1 != null) {
      verifyAddress(addrId, addr1, null, pResult, orderManager);
    }

    return SUCCESS;
  }

  //-----------------------------------------------
  /**
   * This method verifies the addresses by going against the address verification
   * system. It creates and instance of AddressVerificationInfo and sets the data
   * supplied in the method parameters into that object. Then using that object
   * calls verifyAddress() on the addressVerificationProcessor. If an address
   * is invalid the error is added to the PipelineResult object.
   *
   * Override this method to change the way address verification is done.
   *
   * @param pAddressId the id to reference this address verification with
   * @param pAddress1 the first address to verify. Cannot be null.
   * @param pAddress2 the second address to verify. Can be null.
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @param orderManager an instance of the OrderManager
   * @return true if the addresses were correct, false otherwise
   * @exception InvalidParameterException if pAddressId or pBillingAddress is null
   * @see atg.payment.avs.AddressVerificationProcessor#verifyAddress(AddressVerificationInfo)
   */
  protected boolean verifyAddress(String pAddressId, Address pAddress1, Address pAddress2,
                          PipelineResult pResult, OrderManager orderManager) throws CommerceException
  {
    if (pAddressId == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidAddressId",
                                      MY_RESOURCE_NAME, sResourceBundle));
    if (pAddress1 == null && pAddress2 == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidAddress",
                                      MY_RESOURCE_NAME, sResourceBundle));
    
    GenericAddressVerificationInfo avi = new GenericAddressVerificationInfo();
    AddressVerificationStatus status;
    
    avi.setPaymentId(pAddressId);
    avi.setAddress1(pAddress1);
    if (pAddress2 != null)
      avi.setAddress2(pAddress2);
      
    status = getAddressVerificationProcessor().verifyAddress(avi);
    if (isLoggingDebug())
      logDebug("Verified CreditCard Address " + pAddressId + ". Results[" + status.getTransactionId() +
            ", " + status.getTransactionSuccess() + "," + status.getErrorMessage() + "," +
            status.getTransactionTimestamp() + "]");
      
    if (! status.getTransactionSuccess()) {
      addHashedError(pResult, PipelineConstants.INVALIDADDRESS, pAddressId, status.getErrorMessage());
      
      if(isUseCache()) {
        //If only 1 address was verified, we can cache that address as a failure.
        //Otherwise we can't be sure which address failed validation so neither can be
        //cached as a failure.
        if(pAddress1 != null && pAddress2 == null) {
          cacheAddress(pAddress1, status);
        }
        if(pAddress1 == null && pAddress2 != null) {
          cacheAddress(pAddress2, status);
        }
      }
      return false;
    }
    
    if(isUseCache()) {
      //Cache the successful verification
      if(pAddress1 != null) {
        cacheAddress(pAddress1, status);
      }
    
      if(pAddress2 != null) {
        cacheAddress(pAddress2, status);
      }
    }
    return true;
  }  

  //--------------------------------------
  /**
   * This method adds an error to the PipelineResult object. This method, rather than
   * just storing a single error object in pResult, stores a Map of errors. This allows more
   * than one error to be stored using the same key in the pResult object. pKey is
   * used to reference a HashMap of errors in pResult. So, calling
   * pResult.getError(pKey) will return an object which should be cast to a Map.
   * Each entry within the map is keyed by pId and its value is pError.
   *
   * @param pResult the PipelineResult object supplied in runProcess()
   * @param pKey the key to use to store the HashMap in the PipelineResult object
   * @param pId the key to use to store the error message within the HashMap in the
   *            PipelineResult object
   * @param pError the error object to store in the HashMap
   * @see atg.service.pipeline.PipelineResult
   * @see #runProcess(Object, PipelineResult)
   */
  protected void addHashedError(PipelineResult pResult, String pKey, String pId, Object pError)
  {
    Object error = pResult.getError(pKey);
    if (error == null) {
      HashMap map = new HashMap(5);
      pResult.addError(pKey, map);
      map.put(pId, pError);
    }
    else if (error instanceof Map) {
      Map map = (Map) error;
      map.put(pId, pError);
    }
  }
  
  /**
   * Cache the given address' verification status
   * @param pAddress address whose verification status is being cached
   * @param pVerificationStatus verification status to cache
   */
  protected synchronized void cacheAddress(Address pAddress, AddressVerificationStatus pVerificationStatus) {
    if(pAddress == null) {
      return;
    }
    getAddressVerificationCache().put(new AddressVerificationCacheKey(pAddress), pVerificationStatus);
  }
  
  /**
   * Attempts to get the verification status of an address from the cache
   * @param pAddress Address to retrieve
   * @return the verification status if it was cached, otherwise null.
   * @throws Exception
   */
  protected synchronized AddressVerificationStatus getCachedAddress(Address pAddress) throws Exception{
    if(pAddress == null) {
      return null;
    }
    return (AddressVerificationStatus)getAddressVerificationCache().get(new AddressVerificationCacheKey(pAddress));
  }
  
  /**
   * Determines whether or not an address has already been verified.
   * If it has been verified, but that verification was false then an error is added to the Pipeline Result
   * @param pAddress Address to verify
   * @param pAddrId Id of the address to verify
   * @param pResult pipeline result
   * @return true if the address has already been verified (regardless of the verification result), false
   * otherwise.
   */
  protected boolean hasBeenVerified(Address pAddress, String pAddrId, PipelineResult pResult) {
    try {
      if(isUseCache()) {
        AddressVerificationStatus status = getCachedAddress(pAddress);
        if(status != null) {
          if(!status.getTransactionSuccess()) {
            addHashedError(pResult, PipelineConstants.INVALIDADDRESS, pAddrId, status.getErrorMessage());
          }
          return true;
        }
      }
    } catch (Exception e) {
      if(isLoggingError()) {
        logError("Error retreiving address: " + pAddrId + " from cache.");
        logError(e);
      }
    }
    return false;
  }
}
