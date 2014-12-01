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

import atg.portal.gear.calendar.EventFormHandler;
import atg.repository.*;
import atg.droplet.DropletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import java.util.*;

/**
  * This class has additional event properties specific to a detailed
  * calendar event (detailEvent item).
  * It also contains methods to set those properties
  * @author Andy Powers, Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/calendar/classes.jar/src/atg/portal/gear/calendar/DetailEventFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  */


public class DetailEventFormHandler extends EventFormHandler
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/calendar/classes.jar/src/atg/portal/gear/calendar/DetailEventFormHandler.java#2 $$Change: 651448 $";


/**
 * The HTTP protocol string "http://"
 */
public static final String HTTP_PREFIX = "http://";

// properties specific to the detailed event


  protected String mAddress1;
  public String getAddress1()
  {
    return mAddress1;
  }
  public void setAddress1(String pAddress1)
  {
    mAddress1 = pAddress1;
  }

  protected String mAddress2;
  public String getAddress2()
  {
    return mAddress2;
  }
  public void setAddress2(String pAddress2)
  {
    mAddress2 = pAddress2;
  }

  protected String mCity;
  public String getCity()
  {
    return mCity;
  }
  public void setCity(String pCity)
  {
    mCity = pCity;
  }

  protected String mState;
  public String getState()
  {
    return mState;
  }
  public void setState(String pState)
  {
    mState = pState;
  }

  protected String mPostalCode;
  public String getPostalCode()
  {
    return mPostalCode;
  }
  public void setPostalCode(String pPostalCode)
  {
    mPostalCode = pPostalCode;
  }

  protected String mCountry;
  public String getCountry()
  {
    return mCountry;
  }
  public void setCountry(String pCountry)
  {
    mCountry = pCountry;
  }

  protected String mContactName;
  public String getContactName()
  {
    return mContactName;
  }
  public void setContactName(String pContactName)
  {
    mContactName = pContactName;
  }

  protected String mContactPhone;
  public String getContactPhone()
  {
    return mContactPhone;
  }
  public void setContactPhone(String pContactPhone)
  {
    mContactPhone = pContactPhone;
  }

  protected String mContactEmail;
  public String getContactEmail()
  {
    return mContactEmail;
  }
  public void setContactEmail(String pContactEmail)
  {
    mContactEmail = pContactEmail;
  }

  protected String mRelatedUrl;
  public String getRelatedUrl()
  {
    return mRelatedUrl;
  }
  public void setRelatedUrl(String pRelatedUrl)
  {
    mRelatedUrl = pRelatedUrl;
  }


  protected String[] mProtocolTypes;
  public String[] getProtocolTypes()
  {
      return mProtocolTypes;
  }

  public void setProtocolTypes(String[] pProtocolTypes)
  {
      mProtocolTypes = pProtocolTypes;
  }

  // Methods

  /*
  *initialize the form data
  */
  public void initializeData()
  {
    super.initializeData();
    // setCountry((String)sResources.getString(DEFAULT_COUNTRY));
  }

  /*
  * get the item type for a training event
  */
  protected String getItemType()
  {
    return DETAIL_EVENT_ITEM_TYPE;
  }

   //-------------------------------------
   /**
    * Checks to see if pLink starts with either HTTP_PREFIX or FTP_PREFIX.  If
    * true, returns pLink unchanged.  If false, returns pLink prepended with
    * HTTP_PREFIX.
    */
   public String checkLinkHasPrefix(String pLink)
   {
     // This is not the place to add the http prefix
     // to a link. If we add the prefix we prevent
     // a user from linking back to the portal server
     // without hardcoding in a hostname.
     //      if (true) return pLink;
       // First, get rid of any blank space around the link.
       String link = pLink.trim();

       if ( isLinkHasPrefix(pLink) || isLocalLink(pLink) )
       {
           return link;
       } else
       {
           return HTTP_PREFIX + link;
       }
   }
/**
 * Returns true if the link is one that points back to
 * the original server.
 * This method assumes that links starting with
 * a '/' or './' are local links.
 * @param pLink , the link to test
 **/
public boolean isLocalLink (String pLink) {
  if (pLink == null) return false;
  if (pLink.trim().startsWith("/")) return true;
  if (pLink.trim().startsWith("./")) return true;
  if (pLink.trim().startsWith("../")) return true;
  return false;
}
  public boolean isLinkHasPrefix(String pLink)
  {
      String[] types = getProtocolTypes();
      if (types != null)
      {
          for (int i = 0; i < types.length; i++)
          {
              if ( pLink.startsWith(types[i]) )
              {
                  return true;
              }
          }
      }
      return false;
  }


  /*
  * validate that the detail event specific information is valid
  *@return boolean return true if the form is valid
  */
  protected boolean validate()
  {
    boolean result = super.validate();
    return result;
  }

  /*
  * set the detail event-specific properties
  *@param MutableRepositoryItem pItem the current repository item being added
  *@param String profileId the profile id of the user for the event that is being added
  */
  protected void setEventProperties(MutableRepositoryItem pItem, String profileId)
  {
    super.setEventProperties(pItem, profileId);


    if (! emptyOrNull(getAddress1()))
      pItem.setPropertyValue(EVENT_ADDRESS1, getAddress1());
    if (! emptyOrNull(getAddress2()))
      pItem.setPropertyValue(EVENT_ADDRESS2, getAddress2());
    if (! emptyOrNull(getCity()))
      pItem.setPropertyValue(EVENT_CITY, getCity());
    if (! emptyOrNull(getState()))
      pItem.setPropertyValue(EVENT_STATE, getState());
    if (! emptyOrNull(getPostalCode()))
      pItem.setPropertyValue(EVENT_POSTAL_CODE, getPostalCode());
    if (! emptyOrNull(getCountry()))
      pItem.setPropertyValue(EVENT_COUNTRY, getCountry());
    if (! emptyOrNull(getContactName()))
      pItem.setPropertyValue(EVENT_CONTACT_NAME, getContactName());
    if (! emptyOrNull(getContactPhone()))
      pItem.setPropertyValue(EVENT_CONTACT_PHONE, getContactPhone());
    if (! emptyOrNull(getContactEmail()))
      pItem.setPropertyValue(EVENT_CONTACT_EMAIL, getContactEmail());
    if (! emptyOrNull(getRelatedUrl())) {
      String formattedURL=checkLinkHasPrefix(getRelatedUrl());
      pItem.setPropertyValue(EVENT_RELATED_URL, formattedURL);
    }
  }

  // populate the fields from the repository item for editing.
  //
  public void beforeGet(DynamoHttpServletRequest request,
                      DynamoHttpServletResponse response) {


     try {
       if (mEventId!=null) {
          RepositoryItem event = mCalendarRepository.getItem(getEventId(),DETAIL_EVENT_ITEM_TYPE);
	  if (event != null)
	    loadValues(event);
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

	  super.loadValues(event);
          setAddress1((String)event.getPropertyValue(EVENT_ADDRESS1));
          setAddress2((String)event.getPropertyValue(EVENT_ADDRESS2));
          setCity((String)event.getPropertyValue(EVENT_CITY));
          setState((String)event.getPropertyValue(EVENT_STATE));
          setPostalCode((String)event.getPropertyValue(EVENT_POSTAL_CODE));
          setCountry((String)event.getPropertyValue(EVENT_COUNTRY));
          setContactName((String)event.getPropertyValue(EVENT_CONTACT_NAME));
          setContactPhone((String)event.getPropertyValue(EVENT_CONTACT_PHONE));
          setContactEmail((String)event.getPropertyValue(EVENT_CONTACT_EMAIL));
          setRelatedUrl((String)event.getPropertyValue(EVENT_RELATED_URL));
   }

}
