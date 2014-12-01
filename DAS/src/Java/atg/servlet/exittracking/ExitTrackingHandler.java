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

package atg.servlet.exittracking;

import javax.servlet.Servlet;

/**
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/servlet/exittracking/ExitTrackingHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public interface ExitTrackingHandler {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/servlet/exittracking/ExitTrackingHandler.java#2 $$Change: 651448 $";

  
  /**
   * Returns an encoded URL that can be decoded by the exit tracking handler
   */
  public String performExitTracking(String pURL);

  /**
   * Returns true if the supplied URL should be exit tracked
   * @param pURL The URL to check if it should be exit tracked
   */
  public boolean shouldExitTrack(String pURL);

  /**
   * Returns the parameter name to use for storing the URL to redirect to
   * as part of the exit tracking process
   */
  public String getExitTrackingParameterName();
}
