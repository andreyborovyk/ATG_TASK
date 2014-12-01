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

import atg.portal.framework.*;
import atg.portal.gear.calendar.*;
import atg.portal.alert.GearMessagePublisher;
import atg.repository.*;
import atg.droplet.TransactionalFormHandler;
import atg.droplet.DropletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import java.text.DateFormat;
import java.io.IOException;
import javax.servlet.ServletException;
import atg.userprofiling.Profile;
import java.util.*;


/**
 * A base class for calendar event handling.  This class handles the base "event" repository
 * item, and can be extended to support other event types.  (see DetailEventFormHandler.java)
 *
 * @author Andy Powers, Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/calendar/classes.jar/src/atg/portal/gear/calendar/EventFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class EventFormHandler
    extends TransactionalFormHandler implements RepositoryConstants
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/calendar/classes.jar/src/atg/portal/gear/calendar/EventFormHandler.java#2 $$Change: 651448 $";


  /** Name of the Resource Bundle.  This file contains all error messages*/
	static final String MY_RESOURCE_NAME = "atg.portal.gear.calendar.FormResources";


  /** Number of milliseconds that it's OK for the end time to be before the start time */
    protected long START_END_TOLERANCE = 0;


  /** Resource Bundle.  Used to fetch all error messages... */
	static protected ResourceBundle sResources =
      ResourceBundle.getBundle (MY_RESOURCE_NAME, java.util.Locale.getDefault());


// Properties

  protected MutableRepository mCalendarRepository;
  public MutableRepository getCalendarRepository()
  {
	return mCalendarRepository;
  }
  public void setCalendarRepository(MutableRepository pCalendarRepository)
  {
	mCalendarRepository = pCalendarRepository;
  }

  protected Repository mProfileRepository;
  public Repository getProfileRepository()
  {
	return mProfileRepository;
  }
  public void setProfileRepository(Repository pProfileRepository)
  {
	mProfileRepository = pProfileRepository;
  }

  protected Profile mCreatorProfile;
  public Profile getCreatorProfile()
  {
	return mCreatorProfile;
  }
  public void setCreatorProfile(Profile pCreatorProfile)
  {
	mCreatorProfile = pCreatorProfile;
  }

  protected String mSuccessUrl;
  public String getSuccessUrl()
  {
	return mSuccessUrl;
  }
  public void setSuccessUrl(String pSuccessUrl)
  {
	mSuccessUrl = pSuccessUrl;
  }

  protected String mFailureUrl;
  public String getFailureUrl()
  {
	return mFailureUrl;
  }
  public void setFailureUrl(String pFailureUrl)
  {
	mFailureUrl = pFailureUrl;
  }

  protected String mLoginName;
  public String getLoginName()
  {
	return mLoginName;
  }
  public void setLoginName(String pLoginName)
  {
	mLoginName = pLoginName;
  }

  protected String mEventName;
  public String getEventName()
  {
	return mEventName;
  }
  public void setEventName(String pEventName)
  {
	mEventName = pEventName;
  }

  protected String mDescription;
  public String getDescription()
  {
	return mDescription;
  }
  public void setDescription(String pDescription)
  {
	mDescription = pDescription;
  }

  protected Date mEventStartDate;
  public Date getEventStartDate()
  {
	return mEventStartDate;
  }
  public void setEventStartDate(Date pEventStartDate)
  {
	mEventStartDate = pEventStartDate;
  }

  protected Date mEventEndDate;
  public Date getEventEndDate()
  {
	return mEventEndDate;
  }
  public void setEventEndDate(Date pEventEndDate)
  {
	mEventEndDate = pEventEndDate;
  }


  protected Integer mStartHour;
  public Integer getStartHour()
  {
	return mStartHour;
  }
  public void setStartHour(Integer pStartHour)
  {
	mStartHour = pStartHour;
  }

  protected Integer mStartMinute;
  public Integer getStartMinute()
  {
	return mStartMinute;
  }
  public void setStartMinute(Integer pStartMinute)
  {
	mStartMinute = pStartMinute;
  }

  protected String mStartAmPm;
  public String getStartAmPm()
  {
	return mStartAmPm;
  }
  public void setStartAmPm(String pStartAmPm)
  {
	mStartAmPm = pStartAmPm;
  }

  protected Integer mEndHour;
  public Integer getEndHour()
  {
	return mEndHour;
  }
  public void setEndHour(Integer pEndHour)
  {
	mEndHour = pEndHour;
  }

  protected Integer mEndMinute;
  public Integer getEndMinute()
  {
	return mEndMinute;
  }
  public void setEndMinute(Integer pEndMinute)
  {
	mEndMinute = pEndMinute;
  }

  protected String mEndAmPm;
  public String getEndAmPm()
  {
	return mEndAmPm;
  }
  public void setEndAmPm(String pEndAmPm)
  {
	mEndAmPm = pEndAmPm;
  }

  protected String mTimeZone;
  public String getTimeZone()
  {
	return mTimeZone;
  }
  public void setTimeZone(String pTimeZone)
  {
	mTimeZone = pTimeZone;
  }

  protected String mEventId;
  public String getEventId()
  {
	return mEventId;
  }
  public void setEventId(String pEventId)
  {
	mEventId = pEventId;
  }

  protected String mEventType;
  public String getEventType()
  {
	return mEventType;
  }
  public void setEventType(String pEventType)
  {
	mEventType = pEventType;
  }

  protected String mGearId;
  public String getGearId()
  {
	return mGearId;
  }
  public void setGearId(String pGearId)
  {
	mGearId = pGearId;
  }

  protected Boolean mPublicEvent = Boolean.FALSE;
  public boolean getPublicEvent()
  {
	return mPublicEvent.booleanValue();
  }
  public void setPublicEvent(boolean pPublicEvent)
  {
	if(pPublicEvent)
	  mPublicEvent = Boolean.TRUE;
	else
	  mPublicEvent = Boolean.FALSE;
  }

  protected Boolean mIgnoreTime = Boolean.FALSE;
  public boolean getIgnoreTime()
  {
	return mIgnoreTime.booleanValue();
  }
  public void setIgnoreTime(boolean pIgnoreTime)
  {
	if(pIgnoreTime)
	  mIgnoreTime = Boolean.TRUE;
	else
	  mIgnoreTime = Boolean.FALSE;
  }

  // Add this to allow creating/editing events in the past
  // set to true/false in handleCreate and handleUpdate
  private boolean mAllowPast=false;

  //---------------------------------------------
  // Stuff needed for creating and sending JMS events
  private static GearMessagePublisher mPublisher;

  public void setPublisher(GearMessagePublisher pPublisher) {
     mPublisher = pPublisher;
  }
  public GearMessagePublisher getPublisher() {
     return mPublisher;
  }
    /** the gear environment */
    GearEnvironment mGearEnvironment;

    //-------------------------------------
    /**
     * Sets the gear environment
     **/
    public void setGearEnvironment(GearEnvironment pGearEnvironment) {
        mGearEnvironment = pGearEnvironment;
    }

    //-------------------------------------
    /**
     * Returns the gear environment
     **/
    public GearEnvironment getGearEnvironment() {
        return mGearEnvironment;
    }

    /**
     * Gets the gear environment from a servlet request.
     */
    void initGearEnvironment (DynamoHttpServletRequest pReq,
                              DynamoHttpServletResponse pResp)
    {
        pReq.setAttribute("atg.paf.Gear", pReq.getParameter("paf_gear_id"));
	pReq.setAttribute("atg.paf.Community", pReq.getParameter("paf_community_id"));
	pReq.setAttribute("atg.paf.Page", pReq.getParameter("paf_page_id"));
        pReq.setAttribute("atg.paf.PortalRepositoryLocation", "dynamo:/atg/portal/framework/PortalRepository");
        GearEnvironment env = null;

        try {
            setGearEnvironment(EnvironmentFactory.getGearEnvironment(pReq, pResp));
        }
        catch (EnvironmentException e) {
            if (isLoggingError())
                logError(e);
        }

    }

    public void sendMessage(EventMessage pMessage)
    {
      try {

          if (getPublisher() != null)
          {
	    pMessage.setCalendarEventId(getEventId());
	    pMessage.setEventName(getEventName());
	    if (getIgnoreTime()) {
	       pMessage.setStartDate(getEventStartDate());
	    } else {
	       pMessage.setStartDate(getStartTime());
	    }
	    pMessage.setStartDateString(getFormattedStartDate());
	    //   pMessage.setUserID( mCreatorProfile.getRepositoryId());

       if (isLoggingDebug()) {
	  logDebug("EventFormHandler sending message: " + pMessage);
	  logDebug("eventId: " + pMessage.getCalendarEventId());
	  logDebug("name: " + pMessage.getEventName());
	  logDebug("start time: " + pMessage.getStartDate());
	  logDebug("start date string: " + pMessage.getStartDateString());
       }
            getPublisher().writeMessage(pMessage);
          }
      } catch( Exception e ) {
       if (isLoggingError()) {
         logError(sResources.getString("send-message-exception"),e);
       }
      }
     }

  public String getFormattedStartDate() {

    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, java.util.Locale.getDefault());

    if (getIgnoreTime()) {
       return df.format(getEventStartDate());
    } else {
       return df.format(getStartTime());
    }

  }

  // Methods

  public void doStartService()
  {
    initializeData();
  }

  public void initializeData()
  {
  }

  //specifies the type of calendar event
  //
  protected String getItemType() {
    return BASE_EVENT_ITEM_TYPE;
  }

 /**
  * Called when a form is rendered that references this bean.  This call
  * is made before the service method of the page is invoked.
  */
   public boolean beforeSet(DynamoHttpServletRequest request,
                            DynamoHttpServletResponse response)
   {

       initGearEnvironment(request, response);
       return true;
   }

  // populate the fields from the repository item for editing.
  //
  public void beforeGet(DynamoHttpServletRequest request,
                      DynamoHttpServletResponse response) {

     try {
       if (mEventId!=null) {
          RepositoryItem event = mCalendarRepository.getItem(getEventId(),BASE_EVENT_ITEM_TYPE);
	  if (event!=null) {
	     loadValues(event);
	  } else {
	    if (isLoggingDebug())
	         logDebug("Could not find eventId: "+ getEventId());
	  }
       } else {
	 if (getStartHour()==null) {
          // set default time to current time
	  Calendar now = new GregorianCalendar();
	  int hourValue=now.get(Calendar.HOUR);
	  if (hourValue==0) {
	     setStartHour(new Integer(12));
	     setEndHour(new Integer(12));
	  } else {
	     setStartHour(new Integer(hourValue));
	     setEndHour(new Integer(hourValue));
	  }
	  if ( now.get(Calendar.AM_PM)==Calendar.AM ) {
	     setStartAmPm(sResources.getString("amValue"));
	     setEndAmPm(sResources.getString("amValue"));
	  } else {
	     setStartAmPm(sResources.getString("pmValue"));
	     setEndAmPm(sResources.getString("pmValue"));
	  }
         }
       }
     }
     catch (atg.repository.RepositoryException e) {
       logError("EventFormHandler:beforeGet - Unable to get event item: " + getEventId(), e);
       addFormException( new DropletException(sResources.getString("repository-error-update")));
     }
   }

   protected void loadValues (RepositoryItem event) {

          setEventName((String)event.getPropertyValue(EVENT_NAME));
          setDescription((String)event.getPropertyValue(EVENT_DESCRIPTION));
          mPublicEvent=(Boolean) event.getPropertyValue(PUBLIC_EVENT);
	  setIgnoreTime(((Boolean) event.getPropertyValue(EVENT_IGNORE_TIME)).booleanValue());

	  Calendar startDate=Calendar.getInstance();
	  Date eventDate=(Date) event.getPropertyValue(EVENT_START_TIME);
	  if (eventDate !=null) {
	    startDate.setTime(eventDate);
	  }
	  setEventStartDate((Date) event.getPropertyValue(EVENT_START_DATE));
	  int hourValue=startDate.get(Calendar.HOUR);
	  if (hourValue==0) {
	     setStartHour(new Integer(12));
	  } else {
	     setStartHour(new Integer(hourValue));
	  }
	  setStartMinute(new Integer(startDate.get(Calendar.MINUTE)));
	  if ( startDate.get(Calendar.AM_PM)==Calendar.AM ) {
	     setStartAmPm(sResources.getString("amValue"));
	  } else {
	     setStartAmPm(sResources.getString("pmValue"));
	  }

	  Calendar endDate=Calendar.getInstance();
	  Date eventEndDate=(Date) event.getPropertyValue(EVENT_END_TIME);
	  if (eventEndDate !=null) {
	     endDate.setTime(eventEndDate);
	  }
	  setEventEndDate((Date) event.getPropertyValue(EVENT_END_DATE));
	  hourValue=endDate.get(Calendar.HOUR);
	  if (hourValue==0) {
	     setEndHour(new Integer(12));
	  } else {
	     setEndHour(new Integer(hourValue));
	  }
	  setEndMinute(new Integer(endDate.get(Calendar.MINUTE)));
	  if ( endDate.get(Calendar.AM_PM)==Calendar.AM ) {
	     setEndAmPm(sResources.getString("amValue"));
	  } else {
	     setEndAmPm(sResources.getString("pmValue"));
	  }
   }

  /*
  *If the user clicks the handle button, redirect to the cancel url
  */
  public boolean handleCancel(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
		  throws ServletException,
		  IOException
  {
	  if (isLoggingDebug())
		  logDebug("Cancelling the event");

	  if (isLoggingDebug())
		  logDebug("The cancel url is '" + getCancelURL() + "'");

	  //if cancel url is set, redirect to the cancelUrl
	  if (getCancelURL() != null && getCancelURL().length() > 0)
	  {
		  if (isLoggingDebug())
			  logDebug("Cancel, redirecting with url" + getCancelURL());
		  pResponse.sendLocalRedirect(getCancelURL(), pRequest);
		  return false;
	  }
	  return true;
  }

  /*
  * When the user submits an event creation form, validate the users input
  * and create the event if everything is good
  */
  public boolean handleCreate(
      DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException,
       IOException
  {
	  if (isLoggingDebug()) {
			logDebug("In handle create method");
			logDebug("startDate=" + getEventStartDate());
			logDebug("endDate=" + getEventEndDate());
	  }

	  // check for form errors first (i.e. required fields)
	  if (getFormError()) {
		if (getFailureUrl() != null && getFailureUrl().length() > 0)
		{
		  if (isLoggingDebug())
			 logDebug("Failure 1, redirecting with url" + getFailureUrl());
			 pResponse.sendLocalRedirect(getFailureUrl(), pRequest);
			 return false;
		  }else{
			 return  false;
		  }
	  }
	  // Do not allow creation of events in the past
	  mAllowPast=false;

	  //validate the form input, if validate() fails, redirect to the failure url
	  if (! validate())
	  {
		  if (isLoggingDebug())
			  logDebug("Validate has failed");
		  if (getFailureUrl() != null && getFailureUrl().length() > 0)
		  {
			  if (isLoggingDebug())
				  logDebug("Failure 1, redirecting with url" + getFailureUrl());
			  pResponse.sendLocalRedirect(getFailureUrl(), pRequest);
			  return false;
		  }else{
			  return true;
		  }
	  }

	  try
	  {
		  if (isLoggingDebug())
			  logDebug("Creating the event");
		  createEvent();
	  }
	  catch (RepositoryException e)
	  {
		  if (isLoggingError())
			  logError("Exception when creating event: " + e);

		  addFormException(new DropletException(sResources.getString("repository-error-create")));

		  if (getFailureUrl() != null && getFailureUrl().length() > 0)
		  {
			  if (isLoggingDebug())
				  logDebug("Failure, redirecting with url" + getFailureUrl());
			  pResponse.sendLocalRedirect(getFailureUrl(), pRequest);
		  }
		  return false;
	  }

	  // send EventCreatedMessage if this is a public Event
	  if ( getPublicEvent() ) {
	    EventCreatedMessage message = new EventCreatedMessage(getGearEnvironment());
	    sendMessage(message);
	  }

	  if (getSuccessUrl() != null && getSuccessUrl().length() > 0)
	  {
		  pResponse.sendLocalRedirect(getSuccessUrl(), pRequest);
		  return false;
	  }

	  return true;
  }

public boolean handleUpdate(DynamoHttpServletRequest pRequest,
                          DynamoHttpServletResponse pResponse)
  throws ServletException, IOException
  {

	// Allow editing of events in the past
	mAllowPast=true;

	if (isLoggingDebug())
	        logDebug("in handleUpdate - eventID:" + getEventId() + ", eventType: " + getItemType());

	  // check for form errors first (i.e. required fields)
	  if (getFormError()) {
		if (getFailureUrl() != null && getFailureUrl().length() > 0)
		{
		  if (isLoggingDebug())
			 logDebug("Failure 1, redirecting with url" + getFailureUrl());
			 pResponse.sendLocalRedirect(getFailureUrl(), pRequest);
			 return false;
		  }else{
			 return  false;
		  }
	  }
	  //validate the form input, if validate() fails, redirect to the failure url
	  if (! validate())
	  {
		if (isLoggingDebug())
		  logDebug("Validate has failed");
		if (getFailureUrl() != null && getFailureUrl().length() > 0)
		{
		  if (isLoggingDebug())
			  logDebug("Failure 1, redirecting with url" + getFailureUrl());
		  pResponse.sendLocalRedirect(getFailureUrl(), pRequest);
		  return false;
		} else {
		  return true;
		}
	  }

	  try
	  {
             MutableRepositoryItem eventItem =
	     		getCalendarRepository().getItemForUpdate(getEventId(),getItemType());
             setEventProperties(eventItem, mCreatorProfile.getRepositoryId());
             getCalendarRepository().updateItem(eventItem);
	  }
	  catch (RepositoryException e)
	  {
		  if (isLoggingError())
			  logError("Exception when updating event: " + e);

		  addFormException(new DropletException(sResources.getString("repository-error-update")));

		  if (getFailureUrl() != null && getFailureUrl().length() > 0)
		  {
			  if (isLoggingDebug())
				  logDebug("Failure, redirecting with url" + getFailureUrl());
			  pResponse.sendLocalRedirect(getFailureUrl(), pRequest);
		  }
		  return false;
	  }

	  // send EventEditedMessage if this is a public event
	  if ( getPublicEvent() ) {
	    EventEditedMessage message = new EventEditedMessage(getGearEnvironment());
	    sendMessage(message);
	  }

	  if (getSuccessUrl() != null && getSuccessUrl().length() > 0)
	  {
		  pResponse.sendLocalRedirect(getSuccessUrl(), pRequest);
		  return false;
	  }

  	return true;
  }

public boolean handleDelete (DynamoHttpServletRequest pRequest,
                          DynamoHttpServletResponse pResponse)
  throws ServletException, IOException
  {

     if (isLoggingDebug())
	logDebug("handleDelete - eventId: " + getEventId() + ", eventType: " + getEventType());

     // Set itemDescriptor type based on EventType, since this form handler will be
     // used to delete any type of event.
     String thisItemType=BASE_EVENT_ITEM_TYPE;
     if (DETAIL_EVENT_TYPE.equals(getEventType())) {
       thisItemType=DETAIL_EVENT_ITEM_TYPE;
     }

     try
     {
       // get item first so we can populate fields to send JMS message
       RepositoryItem event = getCalendarRepository().getItem(getEventId(),thisItemType);
       loadValues(event);

       getCalendarRepository().removeItem(getEventId(),thisItemType);
     }
     catch (RepositoryException e)
     {
	if (isLoggingError())
		  logError("Exception when deleting event: " + e);
	addFormException(new DropletException(sResources.getString("repository-error-delete")));

	if (getFailureUrl() != null && getFailureUrl().length() > 0)
	{
		  if (isLoggingDebug())
			  logDebug("Failure, redirecting with url" + getFailureUrl());
	  pResponse.sendLocalRedirect(getFailureUrl(), pRequest);
	}
	return false;
     }
     // send EventDeletedMessage
     if ( getPublicEvent() ) {
        EventDeletedMessage message = new EventDeletedMessage(getGearEnvironment());
        sendMessage(message);
     }

     if (getSuccessUrl() != null && getSuccessUrl().length() > 0)
     {
	  pResponse.sendLocalRedirect(getSuccessUrl(), pRequest);
	  return false;
     }

     return true;
  }
  /*
   *check form input
   *@return boolean return true if input is correct, false if there are errors
   */
  protected boolean validate()
  {
	  boolean result = true;

	  //check all times to be correct
	  result &= validateTimes();

	  return result;
  }

  /*
  *check for conflicts for each user for the time period of the event
  *@return boolean return true if there are no conflicts with any of the users, otherwise return false
  */

  /*
  *validate that times entered for the event are usable
  *@return boolean true if the times for the event are correct
  */
  protected boolean validateTimes()
  {
	  boolean result = true;

	  if (getEventStartDate() == null)
	  {
	     result = false;
	     addFormException( new DropletException(sResources.getString("startDateError")));
	     if (isLoggingDebug())
		  logDebug("startDate is NULL");
	  }
	  if (getEventEndDate() == null)
	  {
	     result = false;
	     addFormException( new DropletException(sResources.getString("endDateError")));
	     if (isLoggingDebug())
		  logDebug("endDate is NULL");
	  }

       if (!getIgnoreTime()) {
	  if (getStartHour() == null)
	  {
		  result = false;
	  }
	  else
		  result &= validateRange(getStartHour(), 1, 12, "startHourRangeError");

	  result &= validateRequiredNumber(getStartMinute(), "startMinute", "startMinuteRequiredError");

	  result &= validateRequiredNumber(getEndMinute(), "endMinute", "endMinuteRequiredError");

	  if (getStartMinute() != null)
		  result &= validateRange(getStartMinute(), 0, 59, "startMinuteRangeError");

	  result &= validateAmPm(getStartAmPm(), "startAmPmError");

	  if (getEndHour() == null)
	  {
		  result = false;
	  }
	  else
		  result &= validateRange(getEndHour(), 1, 12, "endHourRangeError");

	  if (getEndMinute() != null)
		  result &= validateRange(getEndMinute(), 0, 59, "endMinuteRangeError");

	  result &= validateAmPm(getEndAmPm(), "endAmPmError");

	  if (!result) {
	     if (isLoggingDebug())
		  logDebug("bad result after am/pm validation");
	  }

	  /* If the start and end times pass the syntactical tests, then check that
	  the end time is after the start time and that both the start and end time are
	  after the current time */

	  if (result)
	  {
		  Date startTime = getStartTime();
		  Date endTime = getEndTime();
		  Date now = new Date(System.currentTimeMillis());
		  if (isLoggingDebug()) {
			  logDebug("Checking before/after stuff");
			  logDebug("startTime:" + startTime);
			  logDebug("endTime:" + endTime);
		  }

		  //check if the start time and end time are after the current time
		  // if we don't allow editing or creation of events in the past
		  if ( !mAllowPast ) {
		    if (startTime.before(now) || endTime.before(now))
		    {
			  if (isLoggingDebug())
				  logDebug("Time entered is before current time - currentTime: "+ now + " startTime: " + startTime + " endTime: " + endTime);
			  addFormException(new DropletException((String)sResources.getString(IN_THE_PAST)));
			  result = false;
		    }
		  }
		  //check to make sure the end time is after the start time
		  if (endTime.before(startTime))
		  {
			  long millisecondsBefore = startTime.getTime() - endTime.getTime();
			  if (millisecondsBefore > START_END_TOLERANCE)
			  {
				  if (isLoggingDebug())
					  logDebug("The end time is before the start time");
				  addFormException(new DropletException((String)sResources.getString(END_BEFORE_START)));
				  result = false;
			  }
		  }
	  }
	}

	  if (isLoggingDebug())
		  logDebug("The result is " + result);
	  return result;
  }

  protected boolean validateRequiredNumber(Object pValue, String pPropertyName, String pError)
  {
	  if (pValue != null)
		  return true;

	     if (isLoggingDebug())
		  logDebug("error in rdequiredNumber validation: " + pError);
	  /*  bug 51886
	   */


	  if (! alreadyHasException(pPropertyName))
	  {
		  addFormException(new DropletException((String)sResources.getString(pError)));
	  }

	  return false;
  }

  protected boolean alreadyHasException(String pPropertyName)
  {
	  Dictionary priorExceptions = getPropertyExceptions();
	  if (priorExceptions == null)
		  return false;

	  return (priorExceptions.get(pPropertyName) != null);
  }

  protected boolean validateRange(Integer pValue, int pMin, int pMax, String pError)
  {
	  int value = pValue.intValue();
	  if (value >= pMin && value <= pMax)
		  return true;

	  addFormException(new DropletException((String)sResources.getString(pError)));
	     if (isLoggingDebug())
		  logDebug("error in range validation: " + pError);


	  return false;
  }

  /*
  *check to make sure the time zone is valid
  *@return boolean return true if the time zone is valid
  */
  protected boolean validateTimeZone(){
	  boolean found = false;
	  //get all available time zone ids and check to make sure the time zone exists
	  String[] ids = TimeZone.getAvailableIDs();
	  if (isLoggingDebug())
		  logDebug("verifying time zone");

	  if (ids != null){
		  for (int i=0; i<ids.length; i++){
			  if (ids[i].equals(getTimeZone())){
				  found = true;
			  }
		  }
	  }
	  if (found == false){
		  if (isLoggingDebug())
			  logDebug("Returning false from time zone verification");
		  addFormException(new DropletException((String)sResources.getString(INVALID_TIMEZONE)));
		  return false;
	  }else{
		  return true;
	  }
  }

  protected boolean validateAmPm(String pAmPm, String pError)
  {
	  String amString = sResources.getString("amValue");
	  String pmString = sResources.getString("pmValue");
	  if (pAmPm != null &&
		  (pAmPm.equalsIgnoreCase(amString) || pAmPm.equalsIgnoreCase(pmString)))
		  return true;

	  addFormException(new DropletException((String)sResources.getString(pError)));

	     if (isLoggingDebug())
		  logDebug("error in am/pm validation: " + pError);
	  return false;
  }

  protected boolean validateMinimum(Integer pValue, int pMin, String pError)
  {
	  int value = pValue.intValue();
	  if (value >= pMin)
		  return true;

	  addFormException(new DropletException((String)sResources.getString(pError)));
	     if (isLoggingDebug())
		  logDebug("error in Minimum validation: " + pError);

	  return false;
  }

  protected boolean validateMaxLength(String pValue, int pMaxLength, String pError)
  {
	  if (pValue == null)
		  return true;

	  if (pValue.length() <= pMaxLength)
		  return true;

	  addFormException(new DropletException((String)sResources.getString(pError)));
	     if (isLoggingDebug())
		  logDebug("error in maxLength validation: " + pError);

	  return false;
  }

  /*
  *Create an event for each user assigned to the event
  */
  protected void createEvent()
		  throws RepositoryException
  {

      if (isLoggingDebug()) {
          logDebug("in createEvent - repository name: " + getCalendarRepository().getRepositoryName());
          logDebug("repository itemDescriptors: " + getCalendarRepository().getItemDescriptorNames());
      }

      MutableRepositoryItem newEventItem = getCalendarRepository().createItem(getItemType());
      setEventProperties(newEventItem, mCreatorProfile.getRepositoryId());
      getCalendarRepository().addItem(newEventItem);
      // set this to be used to send EventCreated message
      setEventId(newEventItem.getRepositoryId());
  }


  /*
  *Set event properties for the event repository item for the current profile
  *@param MutableRepositoryItem pItem the current item being added
  *@param String pProfileId the profile id of the current event
  */
  protected void setEventProperties(MutableRepositoryItem pItem, String profileId)
  {
	  if (! emptyOrNull(getEventName()))
		  pItem.setPropertyValue(EVENT_NAME, getEventName());
	  if (! emptyOrNull(getDescription()))
		  pItem.setPropertyValue(EVENT_DESCRIPTION, getDescription());
	  pItem.setPropertyValue(PUBLIC_EVENT, mPublicEvent);
	  pItem.setPropertyValue(EVENT_GEAR_ID, getGearId());

	  //get the user repository item based on the profile id
	  pItem.setPropertyValue(EVENT_OWNER, profileId);
	  //set all times for the event
	  setTimes(pItem);

  }

  protected static boolean emptyOrNull(Object pString) {
	  return ((pString == null) || (! (pString instanceof String)) || "".equals(pString));
  }

  /*
  *Set all times for the event
  * note: times are set to default value even if ignoreTime=true to avoid db/null field
  *       problems
  *@param MutableRepositoryItem pItem the current item being added
  */
  protected void setTimes(MutableRepositoryItem pItem)
  {
	  pItem.setPropertyValue(EVENT_IGNORE_TIME, mIgnoreTime);
	  pItem.setPropertyValue(EVENT_START_TIME, getStartTime());
	  pItem.setPropertyValue(EVENT_START_DATE, getEventStartDate());
	  pItem.setPropertyValue(EVENT_END_TIME, getEndTime());
	  pItem.setPropertyValue(EVENT_END_DATE, getEventEndDate());
	  if (isLoggingDebug())
		  logDebug("Setting local times");
	  pItem.setPropertyValue(LOCAL_END_TIME, getLocalEndTime());
	  pItem.setPropertyValue(LOCAL_START_TIME, getLocalStartTime());
	  // pItem.setPropertyValue(TIMEZONE, getTimeZone());
  }

  protected Date getStartTime()
  {
	  return getTime( getEventStartDate(), getStartHour(), getStartMinute(), getStartAmPm());
  }

  protected Date getLocalStartTime()
  {
	  return getLocalTime(getEventStartDate(), getStartHour(), getStartMinute(), getStartAmPm());
  }

  protected Date getEndTime()
  {
	  return getTime(getEventEndDate(), getEndHour(), getEndMinute(), getEndAmPm());
  }

  protected Date getLocalEndTime()
  {
	  return getLocalTime( getEventEndDate(), getEndHour(), getEndMinute(), getEndAmPm());
  }

  /*
  *get the date object in the default time
  *Constructing the inputted date/time from an explicitly specified time zone to the default time zone
  *@return Date the inputted date/time represented in the default time zone
  */
  protected Date getTime(Date pDate, Integer pHour, Integer pMinute, String pAmPm)
  {

	 GregorianCalendar cal = null;
	 if (getTimeZone() != null){
		  TimeZone tz = TimeZone.getTimeZone(getTimeZone());
		  cal = new GregorianCalendar(tz);
         } else {
		  cal = new GregorianCalendar();
	 }

	 cal.setTime(pDate);
         cal.set(cal.HOUR,hourFixup(pHour.intValue(), pAmPm));
         cal.set(cal.MINUTE, pMinute.intValue());
         cal.set(cal.SECOND, 0);
         cal.set(cal.MILLISECOND, 0);
	  return cal.getTime();
  }

  /*
  *Constructs a date/time from the inputted data w/o converting to the default time zone
  */

  protected Date getLocalTime(Date pDate, Integer pHour, Integer pMinute, String pAmPm)
  {
	 GregorianCalendar cal = new GregorianCalendar();
	 cal.setTime(pDate);
         cal.set(cal.HOUR,hourFixup(pHour.intValue(), pAmPm));
         cal.set(cal.MINUTE, pMinute.intValue());
         cal.set(cal.SECOND, 0);
	 cal.set(cal.MILLISECOND, 0);
	 return cal.getTime();
  }


  /*
  *change the hour to 24 hour time
  */
  protected int hourFixup(int pHour, String pAmPm)
  {
	  boolean pm = pAmPm.equalsIgnoreCase("PM");

	  if (pm)
	  {
		  if (pHour == 12)
			  return pHour;
		  else
			  return pHour + 12;
	  }
	  else
	  {
		  if (pHour == 12)
			  return 0;
		  else
			  return pHour;
	  }
  }

  /*
  *lookup the user repository item based on the login name
  *@param String pLoginName the name of the user for the event
  *@return RepositoryItem the repository item for the user
  */
  public RepositoryItem lookupLoginName(String pLoginName)
  {
	  try
	  {
		  Repository profileRepository = getProfileRepository();
		  String viewName = profileRepository.getDefaultViewName();
		  RepositoryView view = profileRepository.getView(viewName);
		  QueryBuilder queryBuilder = view.getQueryBuilder();
		  QueryExpression login = queryBuilder.createPropertyQueryExpression(PROFILE_LOGIN);
		  QueryExpression name = queryBuilder.createConstantQueryExpression(pLoginName);
		  Query query = queryBuilder.createComparisonQuery(login, name, QueryBuilder.EQUALS);
		  RepositoryItem [] items = view.executeQuery(query);
		  if (items == null || items.length == 0)
			  return null;
		  else
			  return items[0];
	  }
	  catch (RepositoryException e)
	  {
		  if (isLoggingError())
			  logError("Error attempting to look up login name in Profile repository" + e.toString());
		  return null;
	  }
  }

  /*
  *lookup a user based on profileId
  *@param String id the id of the user in the repository
  *@return RepositoryItem the repository item for the user
  */
  public RepositoryItem lookupUser(String id)
  {
	  if (isLoggingDebug())
		  logDebug("The id in lookup user is " + id);

	  try
	  {
		  return getProfileRepository().getItem(id, USER_ITEM_DESCRIPTOR);
	  }
	  catch (RepositoryException e)
	  {
		  if (isLoggingError())
			  logError("Error attempting to look up the user in Profile repository" + e.toString());
		  return null;
	  }
  }

}
