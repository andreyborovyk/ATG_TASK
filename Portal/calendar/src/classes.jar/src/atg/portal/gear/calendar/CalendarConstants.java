/*<ATGCOPYRIGHT>
 * Copyright (C) 2002-2011 Art Technology Group, Inc.
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

/**
 * @version $Id: //app/portal/version/10.0.3/calendar/classes.jar/src/atg/portal/gear/calendar/CalendarConstants.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public interface CalendarConstants
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/calendar/classes.jar/src/atg/portal/gear/calendar/CalendarConstants.java#2 $$Change: 651448 $";


    // Constants representing different degrees of overlap between an
    // event and a time range

    // No overlap
    public final int RANGE_NONE = 1;

    // Event begins before the time range but ends within the time range
    public final int RANGE_END = 2;

    // Event begins within the time range but ends after the time range
    public final int RANGE_START = 3;

    // Event both begins and ends within the time range
    public final int RANGE_START_END = 4;

    // Event begins before the time range and ends after the time range
    public final int RANGE_MIDDLE = 5;

    public final String defaultStartDatePropertyName = "startTime";
    public final String defaultEndDatePropertyName = "endTime";
    public final String defaultUserPropertyName = "userId";

    // These are the values of the "eventType" property of the event itemDescriptor
    // and are used as parameter values to determine type of event to create, display, etc

    final static String BASE_EVENT_TYPE = "base-event";
    final static String DETAIL_EVENT_TYPE = "detail-event";

    // Role names for calendar access levels
    // The roles give the following permissions:
    // reader - may view calendar events
    // writer - can create/edit/delete private events
    // admin - can create/edit/delete public events
    public final String READER_ROLENAME="calReader";
    public final String WRITER_ROLENAME="calWriter";
    public final String ADMIN_ROLENAME="calAdmin";
    

}
