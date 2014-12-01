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

package atg.portal.gear.docexch;

import javax.jms.*;
import java.io.*;
import java.util.*;

import atg.portal.framework.*;
import atg.portal.alert.*;
import atg.repository.RepositoryItem;


/**
 *
 * <p>This custom JMS message is a serializable Java Object that will contain
 * the following:
 *  Community Id
 *  Document Id
 *  Profile of the person who initiated the doc event
 *  Page
 *
 * It will be fired by the global nucleus component, GearMessagePublisher.
 * The Scenario Manager will be configured to recieve this custom JMS message.
 *
 * @author Neal Hartmann
 * @version $Id: //app/portal/version/10.0.3/docexch/classes.jar/src/atg/portal/gear/docexch/DocExchMessage.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class DocExchMessage extends GearMessage
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/docexch/classes.jar/src/atg/portal/gear/docexch/DocExchMessage.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------

  /** Used by serialization to determine if bean can be deserialized */
  static final long serialVersionUID = -5021566377139652403L;

  //-------------------------------------------
  // Member variables specific to DocExch Messages
  //-------------------------------------------

  /** Document Id **/
  private String mDocumentId;

  /** Document Name **/
  private String mDocumentName;
  
  /** Page Identifier **/
  private String mPageId;

  /** Documents Author **/
  private String mAuthor;

  /** Current User **/
  private String mCurrentUser;

  /** Page Name **/
  private RepositoryItem mPage;

  //-------------------------------------
  // Constructors
  //-------------------------------------

  public DocExchMessage()
  {
  }

  public DocExchMessage(GearEnvironment pGearEnv, String pMessageType)
  {
    super(pGearEnv, pMessageType);
  }

  //-------------------------------------
  // Property Accessors
  //-------------------------------------

  public String getDocumentId()
  {
    return mDocumentId;
  }

  public void setDocumentId(String pDocumentId)
  {
    mDocumentId = pDocumentId;
  }

  public String getDocumentName()
  {
    return mDocumentName;
  }

  public void setDocumentName(String pName)
  {
    mDocumentName = pName;
  }

  public String getPageId()
  {
    return mPageId;
  }

  public void setPageId(String pPageId)
  {
    mPageId = pPageId;
  }

  public String getAuthorName()
  {
    return mAuthor;
  }

  public void setAuthorName(String pAuthor)
  {
    mAuthor = pAuthor;
  }

  public String getCurrentUserName()
  {
    return mCurrentUser;
  }

  public void setCurrentUserName(String pCurrentUser)
  {
    mCurrentUser = pCurrentUser;
  }
}


