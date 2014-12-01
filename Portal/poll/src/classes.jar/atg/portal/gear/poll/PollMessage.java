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
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.portal.gear.poll;

import java.lang.String;
import java.util.Locale;

import atg.portal.nucleus.NucleusComponents;

import atg.portal.alert.GearMessage;
import atg.portal.framework.GearEnvironment;

import atg.repository.Repository;
import atg.repository.RepositoryItem;


/**
 *
 * <p>This custom JMS message is a serializable Java Object that will contain
 * the following:
 *  Profile of the person who submitted the vote
 *  Poll Id
 *  Poll Selection -  (which response was selected)
 *
 * It will be fired by the global nucleus component, GearMessagePublisher.
 * The Scenario Manager will be configured to recieve this custom JMS message.
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/PollMessage.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class PollMessage extends GearMessage
{
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/poll/src/atg/portal/gear/poll/PollMessage.java#2 $$Change: 651448 $";

  //-------------------------------------------
  // Constants
  //-------------------------------------------

  /** Used by serialization to determine if bean can be deserialized */
  static final long serialVersionUID = -5589875725365569583L;

  //-------------------------------------------
  // Member variables specific to Poll Messages
  //-------------------------------------------

  /**  Poll ID **/
  private String mPollId;

  /**  Poll Selection **/
  private String mPollSelection;

  /**  Poll Selection Text**/
  private String mResponseText;

  /** Poll Name */
  transient private String mPollName;

  static private Repository mPollRepository;
  
  //-------------------------------------
  // Constructors
  //-------------------------------------

  static
  {
    try
    {
      // Get the PollRepository
      mPollRepository = (Repository) NucleusComponents.lookup("dynamo:/atg/portal/gear/poll/PollRepository");
    }
    catch(javax.naming.NamingException e)
    {
    }
  }
  
  public PollMessage()
  {
  }

  public PollMessage(GearEnvironment pGearEnv, String pMessageType)
  {
    super(pGearEnv, pMessageType);
  }

  //-------------------------------------
  // Property Accessors
  //-------------------------------------

  public String getPollId()
  {
    return mPollId;
  }

  public void setPollId(String pPollId)
  {
    mPollId = pPollId;
  }

  //-------------------------------------
  public String getPollSelection()
  {
    return mPollSelection;
  }

  public void setPollSelection(String pPollSelection)
  {
    mPollSelection = pPollSelection;
  }

  //-------------------------------------
  public String getResponseText()
  {
    return mResponseText;
  }

  public void setResponseText(String pResponseText)
  {
    mResponseText = pResponseText;
  }

  //--------------------------------------
  public String getPollName()
  {
    return mPollName;
  }

  public void setPollName(String pPollName)
  {
    mPollName = pPollName;
  }

  public void preFormatMessage(Locale pLocale) throws Exception
  {
    super.preFormatMessage(pLocale);
    String pollName = null;
    RepositoryItem pollItem = mPollRepository.getItem(getPollId(),"poll");
    pollName = (String) pollItem.getPropertyValue("title");
    setPollName(pollName);
  }

}


