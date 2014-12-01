/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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
 * Dynamo is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.portal.gear.calendar;

import atg.portal.alert.GearMessage;
import atg.portal.framework.GearEnvironment;

/**
 * @version $Id: //app/portal/version/10.0.3/calendar/classes.jar/src/atg/portal/gear/calendar/EventCreatedMessage.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class EventCreatedMessage extends EventMessage {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/calendar/classes.jar/src/atg/portal/gear/calendar/EventCreatedMessage.java#2 $$Change: 651448 $";


  public final static String    MESSAGE_TYPE = "atg.portal.gear.calendar.EventCreatedMessage";

  static final long serialVersionUID = 200252331687469825L;

  public EventCreatedMessage() {
  }
  
  public EventCreatedMessage(GearEnvironment pGearEnv) {
    super(pGearEnv, MESSAGE_TYPE, "created");
  }

}

