/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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

package atg.commerce.payment;

import atg.payment.tax.*;
import java.util.Date;

/**
 * Dummy tax status for use only with a DummyTaxProcessor.
 *
 * @author Graham Mather
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/DummyTaxStatus.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class DummyTaxStatus implements TaxStatus
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/DummyTaxStatus.java#2 $$Change: 651448 $";


  //-------------------------------------
  /**
   * PaymentStatus implementation
   */

  //-------------------------------------
  // property: TransactionId
  /** transaction id */
  String mTransactionId;

  /**
   * Set property TransactionId
   * @beaninfo description: transaction id
   * @param pTransactionId new value to set
   */
  public void setTransactionId(String pTransactionId)
  {mTransactionId = pTransactionId;}

  /**
   * Get property TransactionId
   * @beaninfo description: transaction id
   * @return TransactionId
   */
  public String getTransactionId()
  {return mTransactionId;}
  

  //-------------------------------------
  // property: TransactionSuccess
  /** transaction success */
  boolean mTransactionSuccess;

  /**
   * Set property TransactionSuccess
   * @beaninfo description: transaction success
   * @param pTransactionSuccess new value to set
   */
  public void setTransactionSuccess(boolean pTransactionSuccess)
  {mTransactionSuccess = pTransactionSuccess;}

  /**
   * Get property TransactionSuccess
   * @beaninfo description: transaction success
   * @return TransactionSuccess
   */
  public boolean getTransactionSuccess()
  {return mTransactionSuccess;}

  /**
   * Test property TransactionSuccess
   * @beaninfo description: transaction success
   * @return TransactionSuccess
   */
  public boolean isTransactionSuccess()
  {return mTransactionSuccess;}
  

  //-------------------------------------
  // property: ErrorMessage
  /** describes transaction failure */
  String mErrorMessage;

  /**
   * Set property ErrorMessage
   * @beaninfo description: describes transaction failure
   * @param pErrorMessage new value to set
   */
  public void setErrorMessage(String pErrorMessage)
  {mErrorMessage = pErrorMessage;}

  /**
   * Get property ErrorMessage
   * @beaninfo description: describes transaction failure
   * @return ErrorMessage
   */
  public String getErrorMessage()
  {return mErrorMessage;}
  

  //-------------------------------------
  // property: TransactionTimestamp
  /** when transaction was initiated */
  Date mTransactionTimestamp;

  /**
   * Set property TransactionTimestamp
   * @beaninfo description: when transaction was initiated
   * @param pTransactionTimestamp new value to set
   */
  public void setTransactionTimestamp(Date pTransactionTimestamp)
  {mTransactionTimestamp = pTransactionTimestamp;}

  /**
   * Get property TransactionTimestamp
   * @beaninfo description: when transaction was initiated
   * @return TransactionTimestamp
   */
  public Date getTransactionTimestamp()
  {return mTransactionTimestamp;}
  
  


  //-------------------------------------
  /**
   * TaxStatus implementation
   */


  //-------------------------------------
  // property: Amount
  /** total tax amount */
  double mAmount = 0.0;

  /**
   * Set property Amount
   * @beaninfo description: total tax amount
   * @param pAmount new value to set
   */
  public void setAmount(double pAmount)
  {mAmount = pAmount;}

  /**
   * Get property Amount
   * @beaninfo description: total tax amount
   * @return Amount
   */
  public double getAmount()
  {return mAmount;}
  

  //-------------------------------------
  // property: CityTax
  /** city tax */
  double mCityTax = 0.0;

  /**
   * Set property CityTax
   * @beaninfo description: city tax
   * @param pCityTax new value to set
   */
  public void setCityTax(double pCityTax)
  {mCityTax = pCityTax;}

  /**
   * Get property CityTax
   * @beaninfo description: city tax
   * @return CityTax
   */
  public double getCityTax()
  {return mCityTax;}
  

  //-------------------------------------
  // property: StateTax
  /** state tax */
  double mStateTax = 0.0;

  /**
   * Set property StateTax
   * @beaninfo description: state tax
   * @param pStateTax new value to set
   */
  public void setStateTax(double pStateTax)
  {mStateTax = pStateTax;}

  /**
   * Get property StateTax
   * @beaninfo description: state tax
   * @return StateTax
   */
  public double getStateTax()
  {return mStateTax;}
  

  //-------------------------------------
  // property: CountyTax
  /** county tax */
  double mCountyTax = 0.0;

  /**
   * Set property CountyTax
   * @beaninfo description: county tax
   * @param pCountyTax new value to set
   */
  public void setCountyTax(double pCountyTax)
  {mCountyTax = pCountyTax;}

  /**
   * Get property CountyTax
   * @beaninfo description: county tax
   * @return CountyTax
   */
  public double getCountyTax()
  {return mCountyTax;}
  

  //-------------------------------------
  // property: ProvinceTax
  /** province tax */
  double mCountryTax = 0.0;

  /**
   * Set property CountryTax
   * @beaninfo description: country tax
   * @param pCountryTax new value to set
   */
  public void setCountryTax(double pCountryTax)
  {mCountryTax = pCountryTax;}

  /**
   * Get property CountryTax
   * @beaninfo description: country tax
   * @return CountryTax
   */
  public double getCountryTax()
  {return mCountryTax;}
  

  //-------------------------------------
  // property: DistrictTax
  /** district tax */
  double mDistrictTax = 0.0;

  /**
   * Set property DistrictTax
   * @beaninfo description: district tax
   * @param pDistrictTax new value to set
   */
  public void setDistrictTax(double pDistrictTax)
  {mDistrictTax = pDistrictTax;}

  /**
   * Get property DistrictTax
   * @beaninfo description: district tax
   * @return DistrictTax
   */
  public double getDistrictTax()
  {return mDistrictTax;}
  

} // end of class


