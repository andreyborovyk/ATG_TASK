 /**<ATGCOPYRIGHT>
 * Copyright (C) 2000-2011 Art Technology Group, Inc.
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

//package atg.repository.content;
package atg.droplet;

import atg.repository.content.*;
import atg.repository.*;

//import atg.targeting.RepositoryTargeter;
//import atg.targeting.TargetedContentTrigger;

import atg.servlet.*;

import atg.service.event.EventDistributor;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.IOException;

/**
 * This servlet allows browsing of the specified content folder
 *
 * <p>The input paramters for this servlet are:
 *
 * <dl>
 *
 * <dt>repository
 * <dd>The content repository
 *
 * <dt>folder
 * <dd>A content folder to browse
 *
 * <dt>getContent
 * <dd>A boolean to indicate whether or not content items should be set in the output
 * parameters.
 *
 * <p>The output paramters for this servlet are:
 *
 * <dl>
 *
 * <dt>subFolders
 * <dd>The array of folders that are immediate children of the input folder
 *
 * <dt>contentItems
 * <dd>The array of content items that are immediate children of the input folder.
 * These are set only if the 'getContent' input parameter is true.
 *
 * <dt>output
 * <dd>This parameter gets rendered once.  Child folders and content items can be
 * referenced in its scope as arrays.
 *
 * <dt>error
 * <dd>If either the repository or the folder were incorrectly specified.
 *
 * </dl>
 *
 *
 * @author Joe Varadi
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/ContentFolderView.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 *
 * @see ContentDroplet
 * @see atg.adapter.servlet.RepositoryBrowserServlet
 */
public class ContentFolderView extends DynamoServlet
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/ContentFolderView.java#2 $$Change: 651448 $";

  static final String REPOSITORY_PARAM = "repository";
  static final String FOLDER_PARAM = "folder";
  static final String GETCONTENT_PARAM = "getContent";
  static final String SUBFOLDERS_PARAM = "subFolders";
  static final String CONTENTITEMS_PARAM = "contentItems";
  static final String OUTPUT_PARAM = "output";
  static final String ERROR_PARAM = "error";
  static final String ACCESSOR_PARAM = "accessor";


  //-------------------------------------
  /** The EventDistributor to send fired events to. **/
  EventDistributor mDistributor;
  /**
   *
   * The EventDistributor to send fired events to.
   **/
  public void setDistributor (EventDistributor pValue)
  {
    mDistributor = pValue;
  }

  /**
   *
   * The EventDistributor to send fired events to.
   **/
  public EventDistributor getDistributor ()
  {
    return mDistributor;
  }

  //-------------------------------------
  // Methods
  //-------------------------------------

  //-------------------------------------
  /**
   * Take the repository and id parameters and try to find
   * the repository item.  If found, service output.
   * Otherwise service empty.
   */
  public void service(DynamoHttpServletRequest pRequest,
                      DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    RepositoryItem item = null;

    // We must get variables needed to fire off content events.
    // Get if we aren't suppose to fire one of the events.
    // unused String fireContentTypeEventParam = pRequest.getParameter("fireContentTypeEvent");
    // unused String fireContentEventParam = pRequest.getParameter("fireContentEvent");
    // unused boolean fireContentTypeEvent = fireContentTypeEventParam == null || fireContentTypeEventParam.equalsIgnoreCase("true");
    // unused boolean fireContentEvent = fireContentEventParam == null || fireContentEventParam.equalsIgnoreCase("true");
//    RepositoryTargeter daTargeter = null;
    // unused RepositoryItem daProfile = null;


    // Handle a ClassCastException that might be thrown if the
    // repository  and item are not of the content variety.
    try {
      ContentRepository rep = (ContentRepository) pRequest.getObjectParameter(REPOSITORY_PARAM);
      Object folderParam = pRequest.getObjectParameter(FOLDER_PARAM);

      String contentParam  = pRequest.getParameter(GETCONTENT_PARAM);


      if (rep != null) {
        try {

              //ContentAccessorBean accessor = new ContentAccessorBean();
	      ContentRepositoryFolder folder = null;

	      if(folderParam == null)
		folder = rep.getRootFolder();
	      else
		folder = (ContentRepositoryFolder) folderParam;

	      if(folder == null)
		throw new RepositoryException("bad news: this repository has no root");

		String[] subFolderIds = folder.getChildFolderIds();
		ContentRepositoryFolder[] subFolders = rep.getFolders(subFolderIds);
		pRequest.setParameter(SUBFOLDERS_PARAM, subFolders);

		if(contentParam != null && contentParam.equals("true")) {
			String[] contentPaths = folder.getChildContentPaths();
			RepositoryItem[] contentItems = rep.getItemsByPath(contentPaths);
			pRequest.setParameter(CONTENTITEMS_PARAM, contentItems);
		}

		//pRequest.setParameter(ACCESSOR_PARAM, accessor);
		pRequest.serviceParameter(OUTPUT_PARAM, pRequest, pResponse);

            // We want to make sure we are only firing events for pieces of content
            // someone could use this targeter to target against people repositories too...
//              if ((getDistributor() != null) && (item instanceof ContentRepositoryItem)) {
//                if (fireContentTypeEvent)
//                  TargetedContentTrigger.fireContentTypeEvent(getDistributor(), pRequest, null,
//                                                              (ContentRepositoryItem) item, daProfile, this);
//                if (fireContentEvent)
//                  TargetedContentTrigger.fireContentEvent(getDistributor(), pRequest, null,
//                                                          (ContentRepositoryItem) item, daProfile, this);
//              }

        }
        catch (RepositoryException re) {
          if (isLoggingError()) logError("Unable to find item " + item + " in repository " + rep.getRepositoryName(), re);
        }
      }
      else if (isLoggingDebug()) {
        logDebug("Must specify a content repository");
      }
    }
    catch (ClassCastException cce) {
      if (isLoggingError()) logError("Need a ContentRepository and ContentRepositoryItem", cce);
    }

  }
} // end of class
