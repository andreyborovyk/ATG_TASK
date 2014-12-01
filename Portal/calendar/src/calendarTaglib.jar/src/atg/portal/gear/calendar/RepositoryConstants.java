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

/**
 * Interface that defines constant names for the Dynamo repository
 * components and property names that are used by the Calendar tags.
 *
 * @author Hayden Schultz
 * @version $Id: //app/portal/version/10.0.3/calendar/calendarTaglib.jar/src/atg/portal/gear/calendar/RepositoryConstants.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public interface RepositoryConstants {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/calendar/calendarTaglib.jar/src/atg/portal/gear/calendar/RepositoryConstants.java#2 $$Change: 651448 $";

  final static String CALENDAR_REPOSITORY = "dynamo:/atg/portal/gear/calendar/CalendarRepository";
  final static String UNSECURED_CALENDAR_REPOSITORY = "dynamo:/atg/portal/gear/calendar/UnsecuredCalendarRepository";
  /** JNDI name of the Gear Messager Publisher component */
  final static String MESSAGE_PUBLISHER = "dynamo:/atg/portal/alert/GearMessagePublisher";
  final static String USER_DIR = "dynamo:/atg/userprofiling/ProfileUserDirectory";

  final static String EVENT_ITEM_TYPE = "event";
  final static String EVENT_DATE_ITEM_TYPE = "eventDate";

  final static String EVENT_NAME = "name";
  final static String EVENT_DESCRIPTION = "description";
  final static String EVENT_URL = "url";
  final static String EVENT_DATES = "dates";
  final static String EVENT_IGNORE_TIME = "ignoreTime";
  final static String EVENT_GEAR_ID = "gearId";

  final static int    EVENT_DESCRIPTION_SIZE = 255;

  final static String EVENT_START_TIME = "startTime";
  final static String EVENT_END_TIME = "endTime";

  final static String EVENT_ADDRESS1 = "address1";
  final static String EVENT_ADDRESS2 = "address2";
  final static String EVENT_ADDRESS3 = "address3";
  final static String EVENT_CITY = "city";
  final static String EVENT_STATE = "state";
  final static String EVENT_COUNTRY = "country";
  final static String EVENT_OWNER = "owner";

  final static String EVENT_CATEGORIES = "categories";

  final static String GEAR_EVERYONE_VIEW = "everyoneView";
  final static String GEAR_EVERYONE_CREATE = "everyoneCreate";
  final static String GEAR_EVERYONE_EDIT = "everyoneEdit";
  final static String GEAR_EVERYONE_DELETE = "everyoneDelete";
  final static String GEAR_WEEKDAYS_ONLY = "weekdaysOnly";

  final static String GEAR_INSTANCE_ITEM_DESCRIPTOR = "gearInstanceParams";
  /** Property name of the Roles allowed to view this gear */
  final static String GEAR_INSTANCE_VIEW_ROLES = "viewRoles";
  /** Property name of the Roles allowed to edit events for this gear */
  final static String GEAR_INSTANCE_EDIT_ROLES = "editRoles";
  /** Property name of the Roles allowed to delete events for this gear */
  final static String GEAR_INSTANCE_DELETE_ROLES = "deleteRoles";
  /** Property name of the Roles allowed to create events for this gear */
  final static String GEAR_INSTANCE_CREATE_ROLES = "createRoles";
  final static String GEAR_INSTANCE_CATEGORIES = "categories";
  final static String GEAR_INSTANCE_OWNER_EDIT = "ownerEdit";
  final static String GEAR_INSTANCE_OWNER_DELETE = "ownerDelete";
  final static String GEAR_INSTANCE_ID = "id";

  final static String COMMUNITY_NAME = "name";

  final static String CATEGORY_ITEM_TYPE = "category";
  final static String CATEGORY_NAME = "name";
  final static String CATEGORY_DESCRIPTION = "description";

  final static String PRINCIPAL_ITEM_TYPE = "accessPrincipal";
  final static String PRINCIPAL_TYPE = "princType";
  final static String PRINCIPAL_ROLE_TYPE = "Role";
  final static String PRINCIPAL_ID = "princId";

  final static String ADMIN_USER_DIR = "dynamo:/atg/dynamo/security/AdminUserDirectory";
  final static String ADMIN_USER_AUTH = "dynamo:/atg/dynamo/security/AdminUserAuthority";
  /** This is a hack and needs to be configurable */
  final static String ADMIN_USER_NAME = "admin";

  /**
   * This isn't really a repository constant. It's the name
   * of a Session attribute.
   */
  final static String TIMEZONE = "timeZone";
  final static String ALREADY_ASKED_ZONE = "calAskedZone";
}

