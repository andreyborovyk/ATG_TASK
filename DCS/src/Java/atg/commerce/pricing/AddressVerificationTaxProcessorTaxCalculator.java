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

package atg.commerce.pricing;

import atg.beans.*;
import atg.payment.tax.*;
import atg.core.util.Address;
import java.util.*;

import atg.commerce.order.Order;
import atg.repository.RepositoryItem;

/**
 * An extension of the <code>TaxProcessorTaxCalculator</code> which optionally verifies
 * the shipping address about to be taxed, the billing address about to be taxed,
 * or both.  In determining whether shipping and billing addresses are valid,
 * The <code>AddressVerificationTaxProcessorTaxCalculator</code> tests to see whether there is any
 * billing/shipping address at all, and then whether any of the required properties
 * (as defined in <code>requiredBillingAddressProperties</code> and
 * <code>requiredShippingAddressProperties</code>) is null.
 * <p>
 * An AddressVerificationTaxProcessorTaxCalculator defines the following properties:
 *
 * <ul>
 *  <li> <b>VerifyBillingAddress</b>: flag that determines whether the billing address
 *       that's about to be passed to the TaxProcessor is valid.
 *  <li> <b>VerifyShippingAddress</b>: flag that determines whether the shipping address
 *       that's about to be passed to the TaxProcessor is valid.
 *  <li> <b>throwVerificationException</b>: flag that determines whether this class should
 *       throw a PricingException when it encounters an invalid billing or shipping address (if true),
 *       or simply return a 'no tax' TaxPriceInfo that has zeroes for all of its property values (if false).
 *  <li> <b>requiredShippingAddressProperties</b>: an array of property names.  Each property of the
 *       shipping address to very that is identified in this list must not be null in order for the
 *       shipping address as a whole to be valid.
 *  <li> <b>requiredBillingAddressProperties</b>: an array of property names.  Each property of the
 *       billing address to very that is identified in this list must not be null in order for the
 *       billing address as a whole to be valid.
 * </ul>
 *
 * @beaninfo
 *   description: A TaxPricingCalculator which extends the TaxProcessorTaxCalculator to optionally
 *                verify shipping and billing data before attempting to calculate tax
 *                based on that information.
 *   attribute: functionalComponentCategory Pricing Calculators
 *   attribute: featureComponentCategory
 *
 * @author Graham Mather
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/AddressVerificationTaxProcessorTaxCalculator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class AddressVerificationTaxProcessorTaxCalculator extends TaxProcessorTaxCalculator
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
    "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/AddressVerificationTaxProcessorTaxCalculator.java#2 $$Change: 651448 $";

  //-------------------------------------
  /**
   * Properties
   */

  //-------------------------------------
  // property: VerifyBillingAddress
  /** flag that determines whether this calculator should verify the billing address before asking the taxprocessor to tax it. */
  boolean mVerifyBillingAddress = true;

  /**
   * flag that determines whether this calculator should verify the billing address before asking the taxprocessor to tax it.
   * @beaninfo description: flag that determines whether this calculator should verify the billing address before asking the taxprocessor to tax it.
   * @param pVerifyBillingAddress new value to set
   */
  public void setVerifyBillingAddress(boolean pVerifyBillingAddress)
  {mVerifyBillingAddress = pVerifyBillingAddress;}

  /**
   * Test property VerifyBillingAddress
   * @beaninfo description: flag that determines whether this calculator should verify the billing address before asking the taxprocessor to tax it.
   * @return VerifyBillingAddress
   */
  public boolean isVerifyBillingAddress()
  {return mVerifyBillingAddress;}


  //-------------------------------------
  // property: VerifyShippingAddress
  /** flag that determines whether this calculator should verify the shipping address before asking the taxprocessor to tax it. */
  boolean mVerifyShippingAddress = false;

  /**
   * flag that determines whether this calculator should verify the shipping address before asking the taxprocessor to tax it.
   * @beaninfo description: flag that determines whether this calculator should verify the shipping address before asking the taxprocessor to tax it.
   * @param pVerifyShippingAddress new value to set
   */
  public void setVerifyShippingAddress(boolean pVerifyShippingAddress)
  {mVerifyShippingAddress = pVerifyShippingAddress;}

  /**
   * Test property VerifyShippingAddress
   * @beaninfo description: flag that determines whether this calculator should verify the shipping address before asking the taxprocessor to tax it.
   * @return VerifyShippingAddress
   */
  public boolean isVerifyShippingAddress()
  {return mVerifyShippingAddress;}


  //-------------------------------------
  // property: ThrowVerificationException
  /** flag that determines whether this calculator should throw a descriptive exception upon address verification failure.  If false, a failed address verification means that the tax calculated will always be zero. */
  boolean mThrowVerificationException = false;

  /**
   * flag that determines whether this calculator should throw a descriptive exception upon address verification failure.  If false, a failed address verification means that the tax calculated will always be zero.
   * @beaninfo description: flag that determines whether this calculator should throw a descriptive exception upon address verification failure.  If false, a failed address verification means that the tax calculated will always be zero.
   * @param pThrowVerificationException new value to set
   */
  public void setThrowVerificationException(boolean pThrowVerificationException)
  {mThrowVerificationException = pThrowVerificationException;}

  /**
   * flag that determines whether this calculator should throw a descriptive exception upon address verification failure.  If false, a failed address verification means that the tax calculated will always be zero.
   * @beaninfo description: flag that determines whether this calculator should throw a descriptive exception upon address verification failure.  If false, a failed address verification means that the tax calculated will always be zero.
   * @return property ThrowVerificationException
   */
  public boolean getThrowVerificationException()
  {return mThrowVerificationException;}

  /**
   * Test property ThrowVerificationException
   * @beaninfo description: flag that determines whether this calculator should throw a descriptive exception upon address verification failure.  If false, a failed address verification means that the tax calculated will always be zero.
   * @return ThrowVerificationException
   */
  public boolean isThrowVerificationException()
  {return mThrowVerificationException;}



  //-------------------------------------
  // property: RequiredBillingAddressProperties
  /** the properties of a billing address which are required to not be null */
  String [] mRequiredBillingAddressProperties;

  /**
   * the properties of a billing address which are required to not be null
   * @beaninfo description: the properties of a billing address which are required to not be null
   * @param pRequiredBillingAddressProperties new value to set
   */
  public void setRequiredBillingAddressProperties(String [] pRequiredBillingAddressProperties)
  {mRequiredBillingAddressProperties = pRequiredBillingAddressProperties;}

  /**
   * the properties of a billing address which are required to not be null
   * @beaninfo description: the properties of a billing address which are required to not be null
   * @return property RequiredBillingAddressProperties
   */
  public String [] getRequiredBillingAddressProperties()
  {return mRequiredBillingAddressProperties;}


  //-------------------------------------
  // property: RequiredShippingAddressProperties
  /** the properties of a shipping address which are required to not be null */
  String [] mRequiredShippingAddressProperties;

  /**
   * the properties of a shipping address which are required to not be null
   * @beaninfo description: the properties of a shipping address which are required to not be null
   * @param pRequiredShippingAddressProperties new value to set
   */
  public void setRequiredShippingAddressProperties(String [] pRequiredShippingAddressProperties)
  {mRequiredShippingAddressProperties = pRequiredShippingAddressProperties;}

  /**
   * the properties of a shipping address which are required to not be null
   * @beaninfo description: the properties of a shipping address which are required to not be null
   * @return property RequiredShippingAddressProperties
   */
  public String [] getRequiredShippingAddressProperties()
  {return mRequiredShippingAddressProperties;}



  /**
   * Given the input TaxRequestInfo object, modify the input pPriceQuote object
   * to reflect the current tax.  Takes the following steps to achieve this:
   * <ul>
   *  <li> optionally verifies shipping address
   *  <li> optionally verifies billing address
   *  <li> calls <code>super.calculateTax()</code>
   * </ul>
   *
   * @param pTRI the TaxRequestInfo object which represents a request from a TaxProcessor
   *        for a tax calculation. Required.
   * @param pPriceQuote the TaxPriceInfo to modify to reflect the calculated tax.  Required.
   * @param pOrder the order for which tax is being calculated.  Optional.
   * @param pPricingModel the pricing model which is modifying the tax total.  Optional.
   *        Not used in DCS at this time.
   * @param pLocale the local in which the tax should be calculated. Optional.
   * @param pProfile the person for whom the tax is being calculated. Optional.
   * @param pExtraParameters any extra information needed to calculate tax. Optional.
   * @exception PricingException if there was a problem determining the tax
   */
  protected void calculateTax(TaxRequestInfo pTRI,
            TaxPriceInfo pPriceQuote,
            Order pOrder,
            RepositoryItem pPricingModel,
            Locale pLocale,
            RepositoryItem pProfile,
            Map pExtraParameters) throws PricingException {

    if (isVerifyShippingAddress()) {
      if (verifyShippingAddress(pTRI) == false) {
  clearTaxPriceInfo(pPriceQuote);
  return;
      }
    }

    if (isVerifyBillingAddress()) {
      if (verifyBillingAddress(pTRI) == false) {
  clearTaxPriceInfo(pPriceQuote);
  return;
      }
    }

    if (isLoggingDebug()) {
      logDebug("All verifications passed.");
    }

    super.calculateTax(pTRI, pPriceQuote, pOrder, pPricingModel, pLocale, pProfile, pExtraParameters);
  } // end calculateTax


  /**
   * Given the input TaxRequestInfo object, modify the input pPriceQuote object
   * to reflect the current tax.  This method differs from calculateTax in that
   * it calls through to the 'calculateTaxByShipping' tax API call.  Takes the following
   * steps to achieve this:
   * <ul>
   *  <li> optionally verifies shipping address
   *  <li> optionally verifies billing address
   *  <li> calls <code>super.calculateTaxByShipping()</code>
   * </ul>
   *
   * @param pTRI the TaxRequestInfo object which represents a request from a TaxProcessor
   *        for a tax calculation.  Required.
   * @param pPriceQuote the TaxPriceInfo to modify to reflect the calculated tax.  Required.
   * @param pOrder the order for which tax is being calculated.  Optional.
   * @param pPricingModel the pricing model which is modifying the tax total.  Optional.
   *        Not used in DCS at this time.
   * @param pLocale the local in which the tax should be calculated. Optional.
   * @param pProfile the person for whom the tax is being calculated. Optional.
   * @param pExtraParameters any extra information needed to calculate tax. Optional.   */
  protected void calculateTaxByShipping(TaxRequestInfo pTRI,
            TaxPriceInfo pPriceQuote,
            Order pOrder,
            RepositoryItem pPricingModel,
            Locale pLocale,
            RepositoryItem pProfile,
            Map pExtraParameters) throws PricingException {

    if (isVerifyShippingAddress()) {
      if (verifyShippingAddress(pTRI) == false) {
        clearTaxPriceInfo(pPriceQuote);
        return;
      }
    }

    if (isVerifyBillingAddress()) {
      if (verifyBillingAddress(pTRI) == false) {
        clearTaxPriceInfo(pPriceQuote);
        return;
      }
    }

    if (isLoggingDebug()) {
      logDebug("All verifications passed.");
    }
                  
    super.calculateTaxByShipping(pTRI, pPriceQuote, pOrder, pPricingModel, pLocale, pProfile, pExtraParameters);
  } // calculateTaxByShipping


  /**
   * verifies that the billing address is not null, and that no
   * crucial properties of the billing address are null.
   * The crucial properties are defined in the RequiredBillingAddressProperties property
   *
   * @param pTRI the TaxRequestInfo containing the billing address to verify
   * @return true if the address is OK, false if the address has problems
   * @exception PricingException if there was a problem verifying the billing address
   */
  protected boolean verifyBillingAddress(TaxRequestInfo pTRI) throws PricingException {

    if (isLoggingDebug()) {
      logDebug("Verifying billing address");
    }

    Address billingAddress = pTRI.getBillingAddress();

    if (isLoggingDebug()) {
      logDebug("BIlling address: " + billingAddress);
    }

    // make sure the billing address itself isn't null
    if (billingAddress == null) {
      if (isThrowVerificationException()) {
  throw new PricingException (Constants.BILLING_ADDRESS_NULL);
      } else {
  return false;
      }
    }

    ArrayList problemList = new ArrayList(getRequiredBillingAddressProperties().length);

    // remember all bad properties
    for (int i = 0; i < getRequiredBillingAddressProperties().length; i++) {
      String requiredProperty = getRequiredBillingAddressProperties()[i];
      Object value = null;
      try {
  value = DynamicBeans.getSubPropertyValue(billingAddress, requiredProperty);

  if (isLoggingDebug()) {
    logDebug("Value for required prop " + requiredProperty + " is : " + value);
  }

      } catch (PropertyNotFoundException e) {
  throw new PricingException(e);
      }

      if (value == null) {
  problemList.add(requiredProperty);
      }
    } // end for

    // if there were no problems, return true
    if (problemList.size() == 0) {
      return true;
    } else {
      // either throw or return false
      if (isThrowVerificationException()) {
  StringBuffer buf = new StringBuffer();
  buf.append(Constants.BILLING_ADDRESS_VERIFICATION_PROBLEM);
  Iterator problemIterator = problemList.iterator();
  while (problemIterator.hasNext()) {
    String problemString = (String) problemIterator.next();
    buf.append(problemString);
    if (problemIterator.hasNext()) {
      buf.append(", ");
    }
  }

  throw new PricingException(buf.toString());
      } else {
  return false;
      }
    } // end else there's a problem

  } // end verifyBillingAddress

  /**
   * verifies that each shipping address is not null, and that no
   * crucial properties of any shipping address are null.
   * The crucial properties are defined in the RequiredShippingAddressProperties property
   *
   * @param pTRI the TaxRequestInfo containing the shipping address to verify
   * @return true if the address is OK, false if the address has problems
   * @exception PricingException if there was a problem verifying the shipping address
   */
  protected boolean verifyShippingAddress(TaxRequestInfo pTRI) throws PricingException {
    ShippingDestination [] shippingDestinations = pTRI.getShippingDestinations();

    for (int x = 0; x < shippingDestinations.length; x++) {


      if (isLoggingDebug()) {
  logDebug("Verifying shipping address");
      }

      Address shippingAddress = shippingDestinations[x].getShippingAddress();

      if (isLoggingDebug()) {
  logDebug("Shipping address: " + shippingAddress);
      }


      // make sure the shipping address itself isn't null
      if (shippingAddress == null) {
  if (isThrowVerificationException()) {
    throw new PricingException (Constants.SHIPPING_ADDRESS_NULL);
  } else {
    return false;
  }
      }

      ArrayList problemList = new ArrayList(getRequiredShippingAddressProperties().length);

      // remember all bad properties
      for (int i = 0; i < getRequiredShippingAddressProperties().length; i++) {
  String requiredProperty = getRequiredShippingAddressProperties()[i];
  Object value = null;
  try {
    value = DynamicBeans.getSubPropertyValue(shippingAddress, requiredProperty);

    if (isLoggingDebug()) {
      logDebug("Value for required prop " + requiredProperty + " is : " + value);
    }

  } catch (PropertyNotFoundException e) {
    throw new PricingException(e);
  }
  if (value == null) {
    problemList.add(requiredProperty);
  }
      } // end for

      // if there were no problems, return true
      if (problemList.size() == 0) {
  return true;
      } else {
  // either throw or return false
  if (isThrowVerificationException()) {
    StringBuffer buf = new StringBuffer();
    buf.append(Constants.SHIPPING_ADDRESS_VERIFICATION_PROBLEM);
    Iterator problemIterator = problemList.iterator();
    while (problemIterator.hasNext()) {
      String problemString = (String) problemIterator.next();
      buf.append(problemString);
      if (problemIterator.hasNext()) {
        buf.append(", ");
      }
    }

    throw new PricingException(buf.toString());
  } else {
    return false;
  }
      } // end else there's a problem
    } // end for each shipping destination

    return true;

  } // end verifyShippingAddress

} // end of class
