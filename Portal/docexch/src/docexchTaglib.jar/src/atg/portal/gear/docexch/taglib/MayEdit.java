// IsAuthorCheck.java
//
//<ATGCOPYRIGHT>
// Copyright (C) 2002-2011 Art Technology Group, Inc.
// All Rights Reserved.  No use, copying or distribution of this
// work may be made except in accordance with a valid license
// agreement from Art Technology Group.  This notice must be
// included on all copies, modifications and derivatives of this
// work.
//
// Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES 
// ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, 
// INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
// LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
// MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
//
// "Dynamo" is a trademark of Art Technology Group, Inc.
//</ATGCOPYRIGHT>*/

package atg.portal.gear.docexch.taglib;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.tagext.*;
import atg.portal.framework.*;
import atg.userprofiling.*;
import atg.repository.*;
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;

/**
 * A JSP tag that renders its body if the current user is the author of 
 * the given document.<P>
 *
 * @author Cynthia Harris
 * @version $Id: //app/portal/version/10.0.3/docexch/docexchTaglib.jar/src/atg/portal/gear/docexch/taglib/MayEdit.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class MayEdit
  extends TagSupport
{
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/docexch/docexchTaglib.jar/src/atg/portal/gear/docexch/taglib/MayEdit.java#2 $$Change: 651448 $";

  /////////////
  // Members //
  /////////////

  /**
   * If true, identity test succeeded.
   */
  private boolean mHasIdentity;

  /**
   * PAF environment object.
   */
  protected GearEnvironment mEnvironment;

  private boolean mEnvironmentNeedsToBeInitialized = true;

  /**
   * The community ID that this tag is working with if set specifically
   * by the user.
   */
  private String mCommunityId;

  /**
   * The community tool used to look up various community information.
   */
  protected Community mCommunity;

  /** the document for which we must check authorship */
  RepositoryItem mDocument;

  ////////////////////////
  // Property accessors //
  ////////////////////////

  /**
   * Returns the community ID that we should be checking for.
   */
  public String getCommunity()

  {
    return mCommunityId;
  }

  /**
   * Changes the community ID that we should be checking for.
   */
  public void setCommunity(String pCommunityId)
  {
    mCommunityId = pCommunityId;
  }


  /**
   * Sets the document for which we must check authorship
   **/
  public void setDocument(RepositoryItem pDocument) {
    mDocument = pDocument;
  }

  //-------------------------------------
  /**
   * Returns the Document for which we must check authorship
   **/
  public RepositoryItem getDocument() {
    return mDocument;
  }


  /////////////////////////////////
  // Superclass method overrides //
  /////////////////////////////////

  public int doStartTag() {

    HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
    HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
    
    if(mEnvironmentNeedsToBeInitialized) {
      if(initializeEnvironment(request,response) == SKIP_BODY) {
        return SKIP_BODY;
      }
      mEnvironmentNeedsToBeInitialized = false;
    }
       
    // calculate the community object
    CommunityTool communityTool = new CommunityTool(request, response);

    // figure out which community we should be checking against
    if ((mCommunityId != null) && (mCommunityId.length() > 0)) // specified in tag
      mCommunity = communityTool.getCommunity(mCommunityId);
    else // not specified, look for argument
      mCommunity = communityTool.getCommunity();

    // if the community is STILL null, get it out of the environment.
    if (mCommunity == null)
      mCommunity = mEnvironment.getCommunity();

    // look up the user profile
    Profile profile = Utilities.getProfile(request);

    // see if user is logged in at all.  if not, send them away
    if (profile == null || profile.isTransient()) {
      return SKIP_BODY;
    }

    mHasIdentity = hasIdentity();

    if (mHasIdentity)
      return EVAL_BODY_INCLUDE;
    else
      return SKIP_BODY;
  }

  protected int initializeEnvironment(HttpServletRequest pRequest,
                                      HttpServletResponse pResponse) {
    mEnvironment = null;
    try {

      mEnvironment = EnvironmentFactory.getGearEnvironment(pRequest, pResponse);

    }
    catch (EnvironmentException e) {
      try {
        pResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      } catch(IOException ioe) {}
      return SKIP_BODY;
    }
    return EVAL_BODY_INCLUDE;
  }


  public int doEndTag()
  {
    return EVAL_BODY_INCLUDE;
  }

  public void release()
  {
    super.release();

    mHasIdentity = false;
    mEnvironment = null;
    mEnvironmentNeedsToBeInitialized = true;

    mCommunity = null;
    mCommunityId = null;

    mDocument = null;
  }



  /**
   * Called to determine if the current user is the community leader or matches the author of the document.
   */
  protected boolean hasIdentity()
  {
    // if community leader, return true, otherwise, continue to determine if author.
    if(mCommunity == null)
        return false;

    
    if (mCommunity.isLeader(mEnvironment))
        return true;
    

    // many steps to get the author of the current doc
    if (mDocument == null) 
        return false;

    String authorPropName = mEnvironment.getGearInstanceParameter("authorPropertyName");
    if (authorPropName == null)
        return false;

    RepositoryItem author = null;
    try {
        author = (RepositoryItem)DynamicBeans.getPropertyValue(mDocument, authorPropName);
    }
    catch (PropertyNotFoundException e) {
        pageContext.getServletContext().log(e.toString());
        return false;
    }

    if (author == null)
        return false;

    String authorid = author.getRepositoryId();

    // get the current profile from the request
    HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
    Profile profile = Utilities.getProfile(request);

    // if they match return true
    if (profile.getRepositoryId().equals(authorid))
        return true;
    return false;
  }
}
