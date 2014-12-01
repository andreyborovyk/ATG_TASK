/*<ATGCOPYRIGHT>
 * Copyright (C) 2008-2011 Art Technology Group, Inc.
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

package atg.projects.store.search;

import atg.service.util.CurrentDate;

/**
 * Store implementation of CurrentDate.
 * <p>
 * This class is extended in order to provide the Epoch date value. 
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/Search/Query/src/atg/projects/store/search/StoreCurrentDate.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class StoreCurrentDate extends CurrentDate {

  //-------------------------------------
  // Class version string
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/Search/Query/src/atg/projects/store/search/StoreCurrentDate.java#2 $$Change: 651448 $";

  /**
   * Returns the Epoch time value in String format. The time value is divided by 1000 in 
   * order to get the Epoch (seconds) time value, which is compatible with Search's date format.
   *
   * @return the time value in String format 
   */
  public String getEpochTime() {
    return String.valueOf((getTime()/1000));
  }
}
