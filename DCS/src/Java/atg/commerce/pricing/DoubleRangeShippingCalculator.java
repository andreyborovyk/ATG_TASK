/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.core.util.StringUtils;
import atg.repository.RepositoryItem;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.service.perfmonitor.PerfStackMismatchException;

import java.util.Locale;
import java.util.Map;
import java.util.List;

/**
 * An abstract shipping calculator that determines the shipping price based on comparing a value from the
 * ShippingGroup to a series of ranges. The service is configured through the <code>ranges</code> property.
 * With the given array of price range configurations (format: <i>low</i>:<i>high</i>:<i>price</i>)
 * the service parses the values into their double format for use in calculating shipping costs.
 * <P>
 * For example:
 * <PRE>
 * ranges=00.00:15.99:4.50,\
 *        16.00:30.99:6.00,\
 *        31.00:40.99:7.25,\
 *        41.00:<i>MAX_VALUE</i>:10.00
 * </PRE>
 * <strong>Note: the keyword <i>MAX_VALUE</i> indicates a top end</strong>
 * <P>
 *
 * If the property <code>addAmount</code> is true then instead of setting the price quote amount to the value
 * of the <code>amount</code> property, the calculator adds the amount to the current amount in the 
 * price quote. This can be used to configure a "surcharge" calculator, which increases the shipping
 * price.
 *
 * <P>
 *
 * The <code>shippingMethod</code> property should be set to the name of a particular delivery process.
 * For example: UPS Ground, UPS 2-day or UPS Next Day.
 * 
 * <P>
 *
 * If the <code>ignoreShippingMethod</code> property is true, then this calculator does not
 * expose a shipping method name (through getAvailableMethods). In addition this calculator will
 * always attempt to perform pricing. This option is available if the user is not given a choice
 * of different shipping methods.
 *
 * @author Bob Mason
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/DoubleRangeShippingCalculator.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public abstract
  class DoubleRangeShippingCalculator
  extends ShippingCalculatorImpl
  {
    //-------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
      "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/pricing/DoubleRangeShippingCalculator.java#2 $$Change: 651448 $";
  
  //-------------------------------------
  // Constants
  //-------------------------------------
    /** The separator token which delineates values within the <code>ranges</code> property */
    public static final char TOKEN = ':';

    private static final String PERFORM_MONITOR_NAME="DoubleRangeShippingCalculator";
    /** Special value which represents the maximum value in a number set. Used so that ranges don't have to include the numeric represenation of a maximum value.  They can use this key instead. */
    public static final String MAX_VALUE_KEY = "MAX_VALUE";

  //-------------------------------------
  // Member Variables
  //-------------------------------------
    /** containsthe minimum value of each range in <code>ranges</code> */
    protected double [] mLowRanges = null;

    /** contains the maximum value of each range in <code>ranges</code>  */
    protected double [] mHighRanges = null;

    /** contains the shipping amounts for each range in <code>ranges</code> */
    protected double [] mAmounts = null;

    //-------------------------------------
    // Properties
    //-------------------------------------



    //-------------------------------------
    // property: Ranges
    /** Price ranges and shipping costs associated with those ranges. */
    String [] mRanges;

    /**
     * Price ranges and shipping costs associated with those ranges.
     * @beaninfo description: Price ranges and shipping costs associated with those ranges.
     * @param pRanges new value to set
     */
    public void setRanges(String [] pRanges)
    {mRanges = pRanges;}

    /**
     * Price ranges and shipping costs associated with those ranges.
     * @beaninfo description: Price ranges and shipping costs associated with those ranges.
     * @return property Ranges
     */
    public String [] getRanges()
    {return mRanges;}
    
    
    //-------------------------------------
    // Constructors
    //-------------------------------------

    /**
     * Constructs an instanceof DoubleRangeShippingCalculator
     */
    public DoubleRangeShippingCalculator() {
    }

    /**
     * Initialize the price ranges for the calculator
     * @exception ServiceException if there was a problem initializing the price ranges for the calculator
     */
    public void doStartService()
      throws ServiceException
    {
      String perfName = "doStartService";
      PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
      boolean perfCancelled = false;
    
      try {
	initializeRanges();
      }
      catch (PricingException exc) {
        try {
          if(!perfCancelled) {
            PerformanceMonitor.cancelOperation(PERFORM_MONITOR_NAME, perfName);
            perfCancelled = true;
          }
        }
        catch(PerfStackMismatchException psm) {
          if(isLoggingWarning())
            logWarning(psm);
        }
        
        throw new ServiceException(exc);

      }
      finally {
        try {
          if(!perfCancelled) {
            PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfName);
            perfCancelled = true;
          }
        } catch (PerfStackMismatchException e) {
          if (isLoggingWarning()) {
            logWarning(e);
          }
        }
      }// end finally

    } // end doStartService

    /**
     * With the given array of price range configurations (format: <i>low</i>:<i>high</i>:<i>price</i>)
     * parse the values into their double format for use in calculating shipping costs
     * @exception PricingException if there was an error while parsing the configuration
     */
    public synchronized void initializeRanges() 
      throws PricingException
    {
      String perfName = "initializeRanges";
      PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
      boolean perfCancelled = false;
    
      try {

	boolean modified = false;
	try {
	  if (getRanges() != null) {
	    int length = getRanges().length;
	    double [] lowRanges = new double[length];
	    double [] highRanges = new double[length];
	    double [] amounts = new double[length];
	    for (int c=0; c<length; c++) {
	      String [] subParts = StringUtils.splitStringAtCharacter(getRanges()[c], TOKEN);
	      if (subParts.length != 3) {
          try {
            if(!perfCancelled) {
              PerformanceMonitor.cancelOperation(PERFORM_MONITOR_NAME, perfName);
              perfCancelled = true;
            }
          }
          catch(PerfStackMismatchException psm) {
            if(isLoggingWarning())
              logWarning(psm);
          }

          String msg = "Price range is incorrectly formatted. " + getRanges()[c];
          throw new PricingException(msg);
	      }
	      try {
		lowRanges[c] = Double.parseDouble(subParts[0]);
		if (! subParts[1].equalsIgnoreCase(MAX_VALUE_KEY))
		  highRanges[c] = Double.parseDouble(subParts[1]);
		else 
		  highRanges[c] = Double.MAX_VALUE;
		amounts[c] = Double.parseDouble(subParts[2]);
	      }
	      catch (NumberFormatException exc) {
          try {
            if(!perfCancelled) {
              PerformanceMonitor.cancelOperation(PERFORM_MONITOR_NAME, perfName);
              perfCancelled = true;
            }
          }
          catch(PerfStackMismatchException psm) {
            if(isLoggingWarning())
              logWarning(psm);
          }

          throw new PricingException(exc);
	      }
	    }
	    mLowRanges = lowRanges;
	    mHighRanges = highRanges;
	    mAmounts = amounts;
	    modified = true;
	  }
	}
	finally {
	  if (! modified) {
	    mLowRanges = null;
	    mHighRanges = null;
	    mAmounts = null;
	  }
	}

      }
      finally {
        try {
          if(!perfCancelled) {
            PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfName);
            perfCancelled = true;
          }
        } catch (PerfStackMismatchException e) {
          if (isLoggingWarning()) {
            logWarning(e);
          }
        }
      }// end finally

    } // end initializeRanges

    /**
     * Return the value which should be used as a comparison between the range values
     * @param pShippingGroup the group that should be inspected for a value which falls
     *   between configured range values.
     * @return the value for which a matching range must be found.
     * @exception PricingException if there was a problem discovering the range comparison value
     * @deprecated
     */
    protected abstract double getRangeComparisonValue(ShippingGroup pShippingGroup) throws PricingException;

    
    /**
     * Return the value which should be used as a comparison between the range values
     * @param pShippingGroup the group that should be inspected for a value which falls
     *   between configured range values.
     * @return the value for which a matching range must be found.
     * @exception PricingException if there was a problem discovering the range comparison value
     */
    protected double getRangeComparisonValue(Order pOrder,ShippingGroup pShippingGroup) throws PricingException
    {
      return getRangeComparisonValue(pShippingGroup);
    }

    
    /**
     * Returns the amount which should be used as the price for this shipping group
     *
     * @param pPriceQuote the price of the input shipping group
     * @param pShippingGroup the shipping group for which an amount is needed
     * @param pPricingModel a discount which could affect the shipping group's price
     * @param pLocale the locale in which the price is calculated
     * @param pProfile the profile of the person for whom the amount in being generated.
     * @param pExtraParameters any extra parameters that might affect the amount calculation
     * @return the amount for pricing the input pShippingGroup
     * @exception PricingException if there is a problem getting the amount (price) for the input shipping group 
     */
    protected double getAmount(Order pOrder,ShippingPriceInfo pPriceQuote, ShippingGroup pShippingGroup, 
			       RepositoryItem pPricingModel, Locale pLocale, 
			       RepositoryItem pProfile, Map pExtraParameters)
      throws PricingException
    {
      String perfName = "getAmount";
      PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
      boolean perfCancelled = false;
    
      try {

	double value = getRangeComparisonValue(pOrder,pShippingGroup);
	int length = mLowRanges.length;
	for (int c=0; c<length; c++) {
	  if ((value >= mLowRanges[c]) && (value <= mHighRanges[c])) {
	    return mAmounts[c];
	  }
	}
  try {
    if(!perfCancelled) {
      PerformanceMonitor.cancelOperation(PERFORM_MONITOR_NAME, perfName);
      perfCancelled = true;
    }
  }
  catch(PerfStackMismatchException psm) {
    if(isLoggingWarning())
      logWarning(psm);
  }
  
	String msg = "No price could be determined for shipping. (range comparison value=" + value + ")";
	throw new PricingException(msg);

      }
      finally {
        try {
          if(!perfCancelled) {
            PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfName);
            perfCancelled = true;
          }
        } catch (PerfStackMismatchException e) {
          if (isLoggingWarning()) {
            logWarning(e);
          }
        }
      }// end finally

    }

  } // end of class
