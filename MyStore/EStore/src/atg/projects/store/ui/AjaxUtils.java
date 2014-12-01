/*<ATGCOPYRIGHT>
* Copyright (C) 1997-2010 Art Technology Group, Inc.
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
package atg.projects.store.ui;

import atg.servlet.DynamoHttpServletRequest;


/**
 * Utility methods for AJAX request processing.
 *
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/ui/AjaxUtils.java#3 $$Change: 635816 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class AjaxUtils {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/ui/AjaxUtils.java#3 $$Change: 635816 $";

  /**
   * Class version string.
   */
  
  /**
   * Determine is this request an AJAX request. Any UI components that require an
   * alternate response for AJAX requests (with JSON data for example), should set
   * a request header "Accept:application/json".
   *
   * @param pRequest The HTTP request
   * @return <code>true</code> if this is an AJAX request, <code>false</code> otherwise
   */
  public static boolean isAjaxRequest(DynamoHttpServletRequest pRequest) {
    String acceptHeader = pRequest.getHeader("Accept");

    if (acceptHeader == null) {
      return false;
    }

    acceptHeader = acceptHeader.toLowerCase();

    boolean isAjax = (acceptHeader.indexOf("text/json") != -1) || (acceptHeader.indexOf("application/json") != -1);

    return isAjax;
  }
}
