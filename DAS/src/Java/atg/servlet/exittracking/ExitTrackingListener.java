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

import java.util.*;

/**
 *
 * <p>An ExitTrackingListener is an object that can be notified when
 * exit tracking occurs.
 *
 * @author Robert Brazile
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/servlet/exittracking/ExitTrackingListener.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public
interface ExitTrackingListener
extends EventListener
{
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/servlet/exittracking/ExitTrackingListener.java#2 $$Change: 651448 $";

  //-------------------------------------
  /**
   *
   * This is called to notify the listener that exit tracking
   * was invoked.
   **/
  public void performExitTracking(ExitTrackingEvent pEvent);
}
