 /**<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
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

import atg.repository.*;
import atg.repository.content.*;

// import atg.targeting.RepositoryTargeter;
// import atg.targeting.TargetedContentTrigger;

import atg.servlet.*;

import atg.service.event.EventDistributor;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This servlet serves up the content of the ContentRepositoryItem from the specified ContentRepository and writes it out to the browser.
 *
 * <p>The input paramters for this servlet are:
 *
 * <dl>
 * <dt>repository
 * <dd>The repository within which to look for the item.
 *
 * <dt>item
 * <dd>The item whose contents we're interested in.
 *
 * <dt>rendition
 * <dd>The content key to use when getting the item's content.  If null, the first contentKey
 * returned by the item's <code>contentKeys</code> property will be used.
 *
 * <dt>bytes
 * <dd>If specified, approximately this many bytes will be written to the browser, from
 * the start of the item's content.  If this figure is above 1Kb, it will be rounded to
 * the nearest Kb.
 *
 * </dl>
 *
 * <p> This servlet sets the mime-type of the response object, using the facilities of the specified
 * MimeTyper.  No other content should be written to the output stream before invoking this servlet,
 * not even a whitespace.
 *
 * @author Joe Varadi
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/ContentDroplet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ContentDroplet extends DynamoServlet
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/ContentDroplet.java#2 $$Change: 651448 $";

  static final String REPOSITORY_PARAM = "repository";
  static final String ITEM_PARAM = "item";
  static final String RENDITION_PARAM = "rendition";
  static final String BYTES_PARAM = "bytes";
  static final String MIMETYPE_PARAM = "mimeType";
  static final String OUTPUT_PARAM = "output";



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

  /** Holds value of property mMimeTyper. */
  private MimeTyper mMimeTyper;
  /**
   *
   * The EventDistributor to send fired events to.
   **/
  public EventDistributor getDistributor ()
  {
    return mDistributor;
  }

  //-------------------------------------
  /** The full name of the Profile component for every request. **/
  String mProfileName;
  /**
   *
   * The full name of the Profile component for every request.
   **/
  public void setProfilePath (String pValue)
  {
    mProfileName = pValue;
  }
  /**
   *
   * The full name of the Profile component for every request.
   **/
  public String getProfilePath ()
  {
    return mProfileName;
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
      ContentRepository rep = (ContentRepository)pRequest.getObjectParameter(REPOSITORY_PARAM);
      ContentRepositoryItem item = (ContentRepositoryItem)pRequest.getObjectParameter(ITEM_PARAM);
      String rendition = pRequest.getParameter(RENDITION_PARAM);
      String bytesParam = pRequest.getParameter(BYTES_PARAM);

      InputStream in = null;

      if (rep != null && item != null) {
        try {
          String[] keys = item.getContentKeys();

          for( int i = 0; i < keys.length; i++ ) {
            String key = keys[i];
            if (rendition != null && !rendition.equalsIgnoreCase(key) )
              continue;

            in = item.getContentByKey(key);
            break;
          }

		  // this check will bypass all the content rendering if bytes=0 is specified

		  if(bytesParam == null || Integer.parseInt(bytesParam) != 0)
		  {
			  if( in != null ) {

					// set output's mime type
					//
					String mimeType = getMimeTyper().getMimeType( item.getItemPath() );
					pResponse.setContentType( mimeType );

					if (isLoggingDebug()) 
					  logDebug("setting mime type to " + mimeType);

					pRequest.setParameter(MIMETYPE_PARAM, mimeType);
					pRequest.serviceParameter(OUTPUT_PARAM, pRequest, pResponse);

					// raise hell if jhtml or other internal type, since we cannot
					// page-compile this content right now
					//


					// write out content
					OutputStream out = pResponse.getOutputStream();

					byte[] buf = new byte[1024];
					int numread = 0;
						int totalread = 0;

							if( bytesParam != null ) {
					int byteCount = Integer.parseInt(bytesParam);
					if (isLoggingDebug()) logDebug("...need bytes:"+byteCount);
					if(byteCount < 1024)
						 buf = new byte [byteCount];

					while((numread = in.read(buf)) > 0 && totalread < byteCount){
					  if (isLoggingDebug()) logDebug("...reading "+buf.length);
					  out.write (buf, 0, numread);
					  totalread += numread;
					}
				}
				else {
						while ((numread = in.read (buf)) > 0) {
							out.write (buf, 0, numread);
							totalread += numread;
						}
					}

			  }
			  else {
			    if (isLoggingDebug())
			      if(rendition == null)
						logDebug("The item contains no content");
			      else
						logDebug("The item has no content for rendition " + rendition);
			  }
			}

            // do that event firing stuff

//              if ((getDistributor() != null)) {
//                if (fireContentTypeEvent)
//                  TargetedContentTrigger.fireContentTypeEvent(getDistributor(), pRequest, null, item, daProfile, this);
//                if (fireContentEvent)
//                  TargetedContentTrigger.fireContentEvent(getDistributor(), pRequest, null, item, daProfile, this);
//              }

        }
        catch (RepositoryException re) {
          if (isLoggingError()) logError("Unable to find item " + item + " in repository " + rep.getRepositoryName(), re);
        }
      }
      else if (isLoggingDebug()) {
        logDebug("Must specify a content repository and item");
      }
    }
    catch (ClassCastException cce) {
      if (isLoggingError()) logError("Need a ContentRepository and ContentRepositoryItem", cce);
    }

  }
  /** Getter for property mMimeTyper.
   *@return Value of property mMimeTyper.
   */
  public MimeTyper getMimeTyper() {
    return mMimeTyper;
  }
  /** Setter for property mMimeTyper.
   *@param mMimeTyper New value of property mMimeTyper.
   */
  public void setMimeTyper(MimeTyper mMimeTyper) {
    this.mMimeTyper = mMimeTyper;
  }
} // end of class
