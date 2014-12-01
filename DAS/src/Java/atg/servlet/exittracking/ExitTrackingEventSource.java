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

/**
 *
 * <p>This is an interface that causes Listeners to be
 * notified when exit tracking occurs.
 *
 * @author Robert Brazile
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/servlet/exittracking/ExitTrackingEventSource.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public
interface ExitTrackingEventSource
{
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/servlet/exittracking/ExitTrackingEventSource.java#2 $$Change: 651448 $";

  //-------------------------------------
  /**
   *
   * Adds the specified listener to the list of listeners that will be
   * notified whenever exit tracking occurs.
   **/
  public void 
  addExitTrackingListener (ExitTrackingListener pListener);

  //-------------------------------------
  /**
   *
   * Removes the specified listener from the list of listeners that
   * will be notified whenever exit tracking occurs
   **/
  public void
  removeExitTrackingListener (ExitTrackingListener pListener);
}
