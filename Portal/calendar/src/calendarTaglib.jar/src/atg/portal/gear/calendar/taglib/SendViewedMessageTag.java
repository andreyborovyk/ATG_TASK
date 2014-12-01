/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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


package atg.portal.gear.calendar.taglib;

import atg.portal.gear.calendar.*;
import atg.portal.alert.GearMessagePublisher;
import atg.portal.framework.*;
import atg.portal.nucleus.NucleusComponents;
import atg.dtm.*;

import java.util.Date;
import java.text.DateFormat;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.transaction.*;
import javax.naming.*;

/**
 * 
 * This tag will send an EventViewedMessage, and is used by showEvent.jsp
 * 
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/calendar/calendarTaglib.jar/src/atg/portal/gear/calendar/taglib/SendViewedMessageTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class SendViewedMessageTag  extends TagSupport
{
    //----------------------------------------
    /** Class version string */
    public static String CLASS_VERSION =
        "$Id: //app/portal/version/10.0.3/calendar/calendarTaglib.jar/src/atg/portal/gear/calendar/taglib/SendViewedMessageTag.java#2 $$Change: 651448 $";

    final static String MESSAGE_PUBLISHER = "dynamo:/atg/portal/alert/GearMessagePublisher";

    //----------------------------------------
    // Properties
    //----------------------------------------

    //----------------------------------------
    // property: env
    private GearEnvironment mEnv;

    public void setEnv(GearEnvironment pEnv)
     { mEnv = pEnv; }

    public GearEnvironment getEnv()
     { return mEnv; }

    //----------------------------------------
    // property: eventId
    private String mEventId;

    public void setEventId(String pEventId) 
	{ mEventId = pEventId; }

    public String getEventId() 
	{ return mEventId; }

    //----------------------------------------
    // property: eventName
    private String mEventName;

    public void setEventName(String pEventName) 
	{ mEventName = pEventName; }

    public String getEventName() 
	{ return mEventName; }

    //----------------------------------------
    // property: startDate
    private Date mStartDate;

    public void setStartDate(Date pStartDate) 
	{ mStartDate = pStartDate; }

    public Date getStartDate() 
	{ return mStartDate; }

    //----------------------------------------
    // property: ignoreTime
    private boolean mIgnoreTime;

    public void setIgnoreTime(boolean pIgnoreTime) 
	{ mIgnoreTime = pIgnoreTime; }

    public boolean getIgnoreTime() 
	{ return mIgnoreTime; }

   //-------------------------------------
   // property: publisher
   private static GearMessagePublisher mPublisher;

   private void setPublisher(GearMessagePublisher pPublisher) {
     mPublisher = pPublisher;
   }
   private GearMessagePublisher getPublisher()
     throws javax.naming.NamingException
   {
     if (mPublisher == null)
     {
         mPublisher = (GearMessagePublisher) NucleusComponents.lookup(MESSAGE_PUBLISHER);
     }
     return mPublisher;
   }

    //----------------------------------------
    // Object Methods 
    //----------------------------------------

   public void sendMessage(EventMessage pMessage)
   {
     try {

         // lookup the publisher service
         GearMessagePublisher publisher = getPublisher();

         if (publisher != null)
         {
           publisher.writeMessage(pMessage);
         }
     } catch( Exception e ) {
       pageContext.getServletContext().log("SendViewedMessage tag - exception sending message: ",e);
     }


    }

  public String getFormattedStartDate() {

    DateFormat df = null;

    if ( getIgnoreTime() ) {
       df = DateFormat.getDateInstance(DateFormat.MEDIUM, java.util.Locale.getDefault());
    } else {
       df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, java.util.Locale.getDefault());
    }

       return df.format(getStartDate());
  }

  // Methods
    //----------------------------------------
    /**
     * start executing this tag
     * @return EVAL_BODY_INCLUDE so that the body contents gets evaluated once
     * @exception JspException if there was a jsp error
     */
    public int doStartTag()
	throws JspException
    {

         //
         // Create the New event message, set its properties, and send it.
         //
         EventViewedMessage message = new EventViewedMessage( getEnv() );
         message.setCalendarEventId(getEventId());
         message.setEventName(getEventName());
         message.setStartDate(getStartDate());
	 message.setStartDateString(getFormattedStartDate());
         sendMessage(message);
	
	return EVAL_BODY_INCLUDE;
    }

    //----------------------------------------
    /**
     * release the tag
     */
    public void release()
    {
	super.release();
	setEventId(null);
	setEventName(null);
	setStartDate(null);
	setIgnoreTime(false);
    }

} // end of class
