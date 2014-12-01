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

import java.util.Date;

/**
 * Parent class for calendar event messages.
 *
 * @author Hayden Schultz
 * @version $Id: //app/portal/version/10.0.3/calendar/classes.jar/src/atg/portal/gear/calendar/EventMessage.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class EventMessage extends GearMessage {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/calendar/classes.jar/src/atg/portal/gear/calendar/EventMessage.java#2 $$Change: 651448 $";


  static final long serialVersionUID = -1488393113946058844L;

  String        mCalendarEventId;
  String        mEventName;
  String        mCommunityName;
  Date          mStartDate;
  String        mStartDateString;
  String        mCity;
  String        mState;

  String        mMessageAction;
  
  public EventMessage() {
  }
  
  
  public EventMessage(GearEnvironment pGearEnv, String pMessageType, String pAction) {
    super(pGearEnv, pMessageType);

    mCommunityName = "";
    if (pGearEnv.getCommunity()!=null) {
      mCommunityName = pGearEnv.getCommunity().getName();
    }
    mMessageAction = pAction;
  }

  public String getCalendarEventId() { return mCalendarEventId;}

  public void setCalendarEventId(String pCalendarEventId) {mCalendarEventId = pCalendarEventId;}

  public String getEventName() {return mEventName;}

  public void setEventName(String pName) {mEventName = pName;}

  public String getCommunityName() {return mCommunityName;}

  public void setCommunityName(String pName) {mCommunityName = pName;}

  public Date getStartDate() {return mStartDate;}

  public void setStartDate(Date pStart) {mStartDate = pStart;}

  public String getStartDateString() {return mStartDateString;}

  public void setStartDateString(String pStart) {mStartDateString = pStart;}

  public String getCity() {return mCity;}

  public void setCity(String pCity) {mCity = pCity;}

  public String getState() {return mState;}

  public void setState(String pState) {mState = pState;}

  public String getAction() {return mMessageAction;}

  public void setAction(String pAction) {mMessageAction = pAction;}
}

