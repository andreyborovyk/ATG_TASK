/*<ATGCOPYRIGHT>
 * Copyright (C) 2008-2010 Art Technology Group, Inc.
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
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Search/Query/src/atg/projects/store/search/StoreCurrentDate.java#3 $$Change: 635816 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class StoreCurrentDate extends CurrentDate {

  //-------------------------------------
  // Class version string
  public static final String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Search/Query/src/atg/projects/store/search/StoreCurrentDate.java#3 $$Change: 635816 $";

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
