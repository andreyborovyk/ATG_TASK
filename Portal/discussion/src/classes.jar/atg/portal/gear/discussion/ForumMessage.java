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

package atg.portal.gear.discussion;

import java.lang.String;

import atg.portal.alert.GearMessage;
import atg.portal.framework.GearEnvironment;
import atg.repository.RepositoryItem;


/**
 *
 * <p>This custom JMS message is a serializable Java Object that will contain
 * the following:
 *  New Forum ID
 *  New Forum Name
 *
 * It will be fired by the global nucleus component, GearMessagePublisher.
 * The Scenario Manager will be configured to receive this custom JMS message.
 *
 * @author Jeff Banister
 * @version $Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/ForumMessage.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class ForumMessage extends GearMessage
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/discussion/src/atg/portal/gear/discussion/ForumMessage.java#2 $$Change: 651448 $";

  //-------------------------------------------
  // Constants
  //-------------------------------------------

  /** Used by serialization to determine if bean can be deserialized */
  static final long serialVersionUID = 7876161380979304193L;

  //-------------------------------------------
  // Member variables specific to Forum Messages
  //-------------------------------------------

  /**  Forum ID **/
  private String mForumId;

  /**  Forum Name **/
  private String mForumName;


  //-------------------------------------
  // Constructors
  //-------------------------------------

  public ForumMessage()
  {
  }

  public ForumMessage(GearEnvironment pGearEnv, String pMessageType)
  {
    super(pGearEnv, pMessageType);
  }

  //-------------------------------------
  // Property Accessors
  //-------------------------------------

  public String getForumId()
  {
    return mForumId;
  }

  public void setForumId(String pForumId)
  {
    mForumId = pForumId;
  }

  public String getForumName()
  {
    return mForumName;
  }

  public void setForumName(String pForumName)
  {
    mForumName = pForumName;
  }

}


