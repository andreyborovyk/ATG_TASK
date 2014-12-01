/* <ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
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
 * "Dynamo" is a trademark of Art Technology Group, Inc. </ATGCOPYRIGHT>
 */

package atg.integrations.taxware;

import atg.payment.avs.*;
import atg.nucleus.logging.ApplicationLogging;

/**
 *
 * <p>This class exists to enable easily adding verazip support
 * to OrderImpl. This class works with the TaxWareCalculateSalesTax
 * order processing item.
 *
 * <p>Most of the code here attempts to implements the VeraZipable
 * interface, mostly through an array of ZipResult objects.
 *
 * <p>Much of the other code deals with providing access functions
 * so that it can be hooked up to a web page.
 *
 * <p>A final chunk of code attempts to set whatever can be set
 * from the current ZipResult object.
 *
 *
 * @see OrderImpl
 * @see VeraZipable
 * @see ZipResult
 * @see ZipResultItem
 * @see ZipRequest
 * @author Charles Morehead
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/VeraZipOrderImpl.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class VeraZipOrderImpl extends BillingShipping implements VeraZipable {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/VeraZipOrderImpl.java#2 $$Change: 651448 $";


  /** The index of SHIP_TO information in our ZipResults array. */
  static final int SHIP_TO_INDEX = 0;
  /** The index of BILL_TO information in our ZipResults array. */
  static final int BILL_TO_INDEX = 1;

  ApplicationLogging log;

  public VeraZipOrderImpl(ApplicationLogging log) {
      this.log = log;
  }
      
  /** Returns the maxinumber of addressed to check. */
  public int getMaxCountOfZipAddresses() { return(2); }
  
  /** The zip result returned from verazip. */
  protected ZipResult[] mZipResultArray;
  
  /** The index into the array associated with the zip result. */
  protected int[] mZipIndexArray;

  /** Returns the array index for the address with
   * the given name. */
  public int getIndexForAddressName(String pName) {
    if (VeraZipable.SHIP_TO_ADDRESS_NAME.equals(pName)) {
      if (getUseBillAddress())
        return(BILL_TO_INDEX);
      else
        return(SHIP_TO_INDEX);
    }
    else if (VeraZipable.BILLING_ADDRESS_NAME.equals(pName))
      return(BILL_TO_INDEX);

    return(-1);
  }

  public String getAddressNameForIndex(int index) {
      if (index == SHIP_TO_INDEX)
          if (getUseBillAddress())
              return(VeraZipable.BILLING_ADDRESS_NAME);
          else
              return(VeraZipable.SHIP_TO_ADDRESS_NAME);
      else
          if (index == BILL_TO_INDEX)
              return(VeraZipable.BILLING_ADDRESS_NAME);
      
      return null;
  }
      
  /** Attempts to set whatever information is possible from
   * the specified zip result. If a choice has yet to be chosen,
   * tries to set missing information from the information
   * which is the same for all of the options (like state name
   * or city name, for example). <p>
   *
   * If a choice has been set, copies any non-matching info
   * from the choice.
   */
  protected void setShipToInfoFromZipResult(ZipResult pZipResult) {
    // first, we need to make sure that the ZipResult
    // applies to our current city/state/zip combo
    
    if ((pZipResult.getResultItemCount() > 0) &&
        pZipResult.matchesCityStateZip(TrimData.trimCity(getShipToCity()),
                                       getShipToState(),
                                       TrimData.trimZip(getShipToZip()))) {

      if (-1 == pZipResult.getChosenIndex()) {
        if (log.isLoggingDebug()) {
          log.logDebug("VeraZipOrderImpl: setShipToInfoFromZipResult, Checking ship to info for default.");
        }

        // okay, the philosophy here is fill out whatever we
        // can for our user, so if all the result items
        // don't vary for city, state or zip, and our
        // user hasn't filled out out, then set it.
        
        ZipResultItem zitemDefault = pZipResult.getResultItemAt(0);

        // first, try to fill in state, city or zip info, if lacking.
        if (nullOrEmptyString(getShipToState()) &&
            !pZipResult.getStatesVary())
          setShipToState(zitemDefault.getStateCode());
        
        if (nullOrEmptyString(getShipToCity()) &&
            !pZipResult.getCitiesVary())
          setShipToCity(zitemDefault.getCityName());

        if (nullOrEmptyString(getShipToZip()) &&
            !pZipResult.getZipsVary())
          setShipToZip(zitemDefault.getFirstZipCode());
            
      } else {
        if (log.isLoggingDebug()) {
          log.logDebug("VeraZipOrderImpl: setShipToInfoFromZipResult, Setting ship to info from chosen option.");
        }
        
        // the user has chosen an option, set all the field from it.
        ZipResultItem zitem = pZipResult.getResultItemAt(
          pZipResult.getChosenIndex());

        if (log.isLoggingDebug()) {
          log.logDebug("VeraZipOrderImpl: setShipToInfoFromZipResult, Comparing city name: " + zitem.getCityName() + " to ship to city: " + getShipToCity());
        }
        
        // set city
        if (!ZipResultItem.nullSafeStringEquals(TrimData.trimCity(getShipToCity()),
                                                zitem.getCityName()))
          setShipToCity(zitem.getCityName());
          
        // set state
        if (!ZipResultItem.nullSafeStringEquals(getShipToState(),
                                                zitem.getStateCode()))
          setShipToState(zitem.getStateCode());
        
        // set zip
        if (!ZipResultItem.nullSafeStringEquals(TrimData.trimZip(getShipToZip()),
                                                zitem.getFirstZipCode()))
          setShipToZip(zitem.getFirstZipCode());
      }
    }
  }

  /** Attempts to set whatever information is possible from
   * the specified zip result. If a choice has yet to be chosen,
   * tries to set missing information from the information
   * which is the same for all of the options (like state name
   * or city name, for example). <p>
   *
   * If a choice has been set, copies any non-matching info
   * from the choice.
   */
  protected void setBillInfoFromZipResult(ZipResult pZipResult) {
    if ((pZipResult.getResultItemCount() > 0) &&
          pZipResult.matchesCityStateZip(TrimData.trimCity(getBillCity()),
                                         getBillState(),
                                         TrimData.trimZip(getBillZip()))) {

      if (-1 == pZipResult.getChosenIndex()) {

        if (log.isLoggingDebug()) {
          log.logDebug("VeraZipOrderImpl: setBillInfoFromZipResult, Checking default Bill address values.");
        }

        // okay, the philosophy here is fill out whatever we
        // can for our user, so if all the result items
        // don't vary for city, state or zip, and our
        // user hasn't filled out out, then set it.
        
        ZipResultItem zitemDefault = pZipResult.getResultItemAt(0);

        // first, try to fill in state, city or zip info, if lacking.
        if (nullOrEmptyString(getBillState()) &&
            !pZipResult.getStatesVary())
          setBillState(zitemDefault.getStateCode());
        
        if (nullOrEmptyString(getBillCity()) &&
            !pZipResult.getCitiesVary())
          setBillCity(zitemDefault.getCityName());

        if (nullOrEmptyString(getBillZip()) &&
            !pZipResult.getZipsVary())
          setBillZip(zitemDefault.getFirstZipCode());
           
      } else {
        if (log.isLoggingDebug()) {
          log.logDebug("VeraZipOrderImpl: setBillInfoFromZipResult, Setting bill info from chosen option.");
        }

        // the user has chosen an option, set all the field from it.
        ZipResultItem zitem = pZipResult.getResultItemAt(
          pZipResult.getChosenIndex());

          if (log.isLoggingDebug()) {
            log.logDebug("VeraZipOrderImpl: setBillInfoFromZipResult, Comparing city name: " + zitem.getCityName() + " to bill to city: " + getBillCity());
          }
          
        // set city
        if (!ZipResultItem.nullSafeStringEquals(TrimData.trimCity(getBillCity()),
                                                zitem.getCityName())) {
          if (log.isLoggingDebug()) {
            log.logDebug("VeraZipOrderImpl: setBillInfoFromZipResult, Setting bill city to " + zitem.getCityName());
          }
          setBillCity(zitem.getCityName());
        }
        
        // set state
        if (!ZipResultItem.nullSafeStringEquals(getBillState(),
                                                zitem.getStateCode()))
          setBillState(zitem.getStateCode());
        
        // set zip
        if (!ZipResultItem.nullSafeStringEquals(TrimData.trimZip(getBillZip()),
                                                zitem.getFirstZipCode()))
          setBillZip(zitem.getFirstZipCode());
      }
    }
  }
  

  /** A helper function which checks to see if
   * a string is null or empty.
   */
  protected static boolean nullOrEmptyString(String pString) {
    return((null == pString) || (0 == pString.length()));
  }

  /** Set the ZipResult object at the specified index.<p>
   * Note that we go ahead and call the setXXXXInfoFromZipResult
   * method as well, so that defaults will be there the next
   * time the page is rendered.<p>
   *
   * Set ZipResut always does real set, regardless of whether
   * mUseBillAddress is set.
   */
  public void setZipResultAt(int idx, ZipResult pZipResult) {
    if (null == mZipResultArray)
      mZipResultArray = new ZipResult[getMaxCountOfZipAddresses()];

    if (null != pZipResult) {
      if (SHIP_TO_INDEX == idx) {
        setShipToInfoFromZipResult(pZipResult);
      }
      else if (BILL_TO_INDEX == idx) {
        setBillInfoFromZipResult(pZipResult);
      }
    }
    
    mZipResultArray[idx] = pZipResult;
  }

  /** Get the ZipResult object. */
  public ZipResult getZipResultAt(int idx) {
    if (mZipResultArray == null)
      return(null);
    return(mZipResultArray[idx]);
  }




  /* Returns the count of ZipResultItem associated with the SHIP_TO address. */
  public int getShipToZipOptionCount() {
    ZipResult zresult = getZipResultAt(SHIP_TO_INDEX);

    if (zresult == null)
      return(0);

    return(zresult.getResultItemCount());
  }


  /** Returns the string array describing the current billing zip options. */
  public String[] getShipToZipOptions() {
    return(getZipResultAt(SHIP_TO_INDEX).getDescriptions());
  }

  /**
   * Sets the index for the currently chosen ship-to zip option. -1 means
   * nothing chosen yet.
   */
  public void setShipToZipOptionChoice(int pIndex) {
    ZipResult zresult = getZipResultAt(SHIP_TO_INDEX);
    if (zresult == null) return;
     
    if (log.isLoggingDebug()) {
      log.logDebug("VeraZipOrderImpl: setShipToZipOptionChoice, Setting ship to zip option choice to " + pIndex);
    }
    zresult.setChosenIndex(pIndex);
  }
  
  /**
   * gets the index for the currently chosen ship-to zip option. -1 means
   * nothing chosen yet.
   */
  public int getShipToZipOptionChoice() {
    ZipResult zresult = getZipResultAt(SHIP_TO_INDEX);

    if (zresult == null)
      return(-1);

    return(zresult.getChosenIndex());
  }
  
  /** Returns the count of possible string options. */
  public int getBillZipOptionCount() {
    ZipResult zresult = getZipResultAt(BILL_TO_INDEX);

    if (zresult == null)
      return(0);

    return(zresult.getResultItemCount());
  }

  /** Returns the string array describing the current billing zip options. */
  public String[] getBillZipOptions() {
    return(getZipResultAt(BILL_TO_INDEX).getDescriptions());
  }

  /**
   * Sets the index for the currently chosen zip option. -1 means
   * nothing chosen yet. Called by UI.
   */
  public void setBillZipOptionChoice(int pIndex) {
    ZipResult zresult = getZipResultAt(BILL_TO_INDEX);
    if (zresult == null) return;
    
    if (log.isLoggingDebug()) {
      log.logDebug("VeraZipOrderImpl: setBillZipOptionChoice, Setting bill to zip option choice to " + pIndex);
    }
    zresult.setChosenIndex(pIndex);
  }

    
  /**
   * gets the index for the currently chosen billing zip option. -1 means
   * nothing chosen yet.
   */
  public int getBillZipOptionChoice() {
    ZipResult zresult = getZipResultAt(BILL_TO_INDEX);

    if (zresult == null)
      return(-1);

    return(zresult.getChosenIndex());
  }


  /** Get the request for the specified address object.
   *
   * If this function returns null, then
   * that means don't check the specified address.
   */
  public ZipRequest getZipRequestForAddress(int idxAddress) {
    if (SHIP_TO_INDEX == idxAddress) {
      // if we're using the billing address, don't check this one separately
      if (getUseBillAddress())
        return(null);
      return(createShipToZipRequest());
    } else if (BILL_TO_INDEX == idxAddress)
      return(createBillZipRequest());

    return(null);
  }

  /** Create a ZipRequest from the current ship-to information.
   * returns NULL if no validation is needed.
   */
  ZipRequest createShipToZipRequest() {
    
    // make sure the ship-to information is up to date
    // with the user's choice
    if (null != getZipResultAt(SHIP_TO_INDEX))
      setShipToInfoFromZipResult(getZipResultAt(SHIP_TO_INDEX));

    String strShipToCountry = getShipToCountry();
    
    if ((strShipToCountry == null) || (strShipToCountry.equals("USA")))
      strShipToCountry = "US";
    else if (strShipToCountry.equals("CAN"))
      strShipToCountry = "CA";

    // Can't handle non US and Canada addresses
    if (!(strShipToCountry.equalsIgnoreCase("US") ||
          strShipToCountry.equalsIgnoreCase("CA"))) {
      if (log.isLoggingError()) {
        log.logError(SalesTaxService.msg.format("VERAZIPBadShipToCountry", strShipToCountry));
      }
      return(null);
    }
   
    String StateCode = getShipToState();
    String CityName = getShipToCity();
    String PostalCode = null;
    if (strShipToCountry.toUpperCase().equals("CA")) {
        CityName = StateCode;
        StateCode = "CN";
        PostalCode = getShipToZip();
    }
    
    ZipRequest ziprequest = new ZipRequest(StateCode,
                                           TrimData.trimZip(getShipToZip()),
                                           TrimData.trimCity(CityName),
                                           TrimData.trimPostal(PostalCode), null, null);

    return(ziprequest);       
  }


  /** Create a ZipRequest from the current bill to information.
   * returns NULL if no validation is needed.
   */
  ZipRequest createBillZipRequest() {

    // make sure our request information is in sync with
    // the users choice and any default info
    if (null != getZipResultAt(BILL_TO_INDEX))    
      setBillInfoFromZipResult(getZipResultAt(BILL_TO_INDEX));
      
    String strBillCountry = getBillCountry();

    if ((strBillCountry == null) || (strBillCountry.equals("USA")))
      strBillCountry = "US";
    else if (strBillCountry.equals("CAN"))
      strBillCountry = "CA";

    // Can't handle non US and Canada addresses
    if (!(strBillCountry.equalsIgnoreCase("US") ||
          strBillCountry.equalsIgnoreCase("CA"))) {
      if (log.isLoggingError()) {
        log.logError(SalesTaxService.msg.format("VERAZIPBadBillCountry", strBillCountry));
      }
      return(null);
    }
    
    String StateCode = getBillState();
    String CityName = getBillCity();
    String PostalCode = null;
    if (strBillCountry.toUpperCase().equals("CA")) {
        CityName = StateCode;
        StateCode = "CN";
        PostalCode = getBillZip();
    }
    
    ZipRequest ziprequest = new ZipRequest(StateCode,
                                           TrimData.trimZip(getBillZip()),
                                           TrimData.trimCity(CityName),
                                           TrimData.trimPostal(PostalCode), null, null);
        
    return(ziprequest);

  }

  //-------------------------------------
  // property: zipErrorString

  String mZipErrorString = null;  
  /** Sets the current zip error string. */
  public void setZipErrorString(String pError) {
      mZipErrorString = pError;
  }
  
  /** Sets the current zip error string. */
  public String getZipErrorString() {
      return mZipErrorString;
  }
 
  //-------------------------------------
  // property: zipError

  boolean mZipError = false;
  /** Sets a zip error -- probably not a good idea to use. */
  public void setZipError(boolean pIsError) {
      mZipError = pIsError;
  }
  
  /** Returns true if there is currently a zip order. */
  public boolean isZipError() {
      return mZipError;
  }
  
}

