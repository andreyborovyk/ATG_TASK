/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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

package atg.servlet.pipeline;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import atg.nucleus.*;
import atg.servlet.*;

/**
 * <p>This will redirect a request to one of several servlets based on
 * the mime-type of the request.  The mime type must be set in
 * MimeTyperPipelineServlet.ATTRIBUTE_NAME, which means that the
 * MimeTyperPipelineServlet must appear in the pipeline before this
 * servlet.
 *
 * <p>The mimeTypeMap property determines the mapping from mime type
 * to servlet.  If the mime type does not match any of the mime types,
 * then the request is passed on to the next servlet in the pipeline.
 *
 * @author Nathan Abramson
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/servlet/pipeline/MimeTypeDispatcherPipelineServlet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo
 *   description: Redirects a request to one of several servlets based
 *                on the request's MIME type
 *   attribute: functionalComponentCategory Services
 *   attribute: featureComponentCategory Pipeline
 *   attribute: icon /atg/ui/common/images/pipelinecomp.gif
 **/

public
class MimeTypeDispatcherPipelineServlet
extends DispatcherPipelineServletImpl
{
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/servlet/pipeline/MimeTypeDispatcherPipelineServlet.java#2 $$Change: 651448 $";

  //-------------------------------------
  /**
   * Constructs a new MimeTypeDispatcherPipelineServlet
   **/
  public MimeTypeDispatcherPipelineServlet ()
  {
  }

  //-------------------------------------
  /**
   * Returns the Mime Type of the request, this is the String attribute
   * that is used to determine if the request should be dispatched to
   * another servlet.
   */
  public Object getDispatchingAttribute(DynamoHttpServletRequest pRequest) {
    return pRequest.getAttribute (MimeTyperPipelineServlet.ATTRIBUTE_NAME);
  }


  //-------------------------------------
}
