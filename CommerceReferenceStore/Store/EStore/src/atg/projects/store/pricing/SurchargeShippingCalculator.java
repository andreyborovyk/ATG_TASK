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

package atg.projects.store.pricing;

import atg.commerce.pricing.FixedPriceShippingCalculator;

/**
 * Overrides ATG's FixedPriceShippingCalculator. This will be
 * used to set the surcharge of $5 for shipping orders to 
 * Guam, U.S. Virgin Islands and Hawaii.
 *
 * @author ATG
 * @version $Revision: #2 $
 */
public class SurchargeShippingCalculator extends FixedPriceShippingCalculator{

  /** Class version string. */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/pricing/SurchargeShippingCalculator.java#2 $$Change: 651448 $";


    // Override one of the getAmount(..) methods.

}
