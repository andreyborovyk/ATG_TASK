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
import atg.servlet.*;
import atg.servlet.minimal.WebApplicationInterface;

/**
 * <p>This pipeline servlet will add an attribute factory for the
 * attribute MimeTyperPipelineServlet.ATTRIBUTE_NAME.  The first time
 * this attribute is accessed, the request's mime-type will be
 * calculated and added as an attribute.  The mime-type is determined
 * from the pathTranslated property of the request.  If pathTranslated
 * is null, then the mime-type is determined by the pathInfo of the
 * request.
 *
 * @author Nathan Abramson
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/servlet/pipeline/MimeTyperPipelineServlet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo
 *   description: Servlet that creates MIME type attribute factories for
 *                a request
 *   attribute: functionalComponentCategory Services
 *   attribute: featureComponentCategory Pipeline
 *   attribute: icon /atg/ui/common/images/pipelinecomp.gif
 **/

public
class MimeTyperPipelineServlet
extends PipelineableServletImpl
{
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/servlet/pipeline/MimeTyperPipelineServlet.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Attribute Factory

  // The AttributeFactory that creates the mime type attribute
  class MimeTypeAttributeFactory implements AttributeFactory {
    DynamoHttpServletRequest mRequest;
    MimeTyperPipelineServlet mServlet;

    public MimeTypeAttributeFactory () {}
    public void setRequest (DynamoHttpServletRequest pRequest)
    { mRequest = pRequest; }
    public void setServlet (MimeTyperPipelineServlet pServlet)
    { mServlet = pServlet; }
    public Object createAttributeValue ()
    { return mServlet.getMimeType (mRequest); }
  }

  // The key for the permanent attribute holding the above factory
  class FactoryFactory implements AttributeFactory {
    public Object createAttributeValue ()
    { return new MimeTypeAttributeFactory (); }
  }
  AttributeFactory mFactoryKey = new FactoryFactory ();

  //-------------------------------------
  // Constants

  /** The name of the attribute holding the mime type */
  public static final String ATTRIBUTE_NAME =
  "atg.servlet.pipeline.MimeType";

  //-------------------------------------
  // Properties

  /** The MimeTyper */
  MimeTyper mMimeTyper;

  //-------------------------------------
  /**
   *
   * Constructs a new MimeTyperPipelineServlet
   **/
  public MimeTyperPipelineServlet ()
  {
  }

  //-------------------------------------
  // Properties
  //-------------------------------------
  /**
   * Returns the MimeTyper used to determine the mime type of a
   * request.
   * @beaninfo
   *   description: The MIME typer used to determine the MIME type of
   *                a request
   **/
  public MimeTyper getMimeTyper ()
  {
    return mMimeTyper;
  }

  //-------------------------------------
  /**
   * Sets the MimeTyper used to determine the mime type of a request.
   **/
  public void setMimeTyper (MimeTyper pMimeTyper)
  {
    mMimeTyper = pMimeTyper;
  }

  //-------------------------------------
  /**
   * Returns the mime type of the given request
   **/
  public String getMimeType (DynamoHttpServletRequest pRequest)
  {
    WebApplicationInterface webApp = pRequest.getWebApplication();

    boolean bInInclude = ServletUtil.inInclude(pRequest);
    String pathTranslated =
      ServletUtil.getCurrentPathTranslated(pRequest, bInInclude);


    if (pathTranslated == null)
      pathTranslated = ServletUtil.getCurrentPathInfo(pRequest, bInInclude);


    if (!ServletUtil.isDynamoJ2EEServer()) {
      if ( pathTranslated == null || pathTranslated.length() == 0) 
        pathTranslated = ServletUtil.getCurrentServletPath(pRequest, bInInclude);
    }
    if (isLoggingDebug()) 
      logDebug ("pathTranslated=" + pathTranslated);
    
    if (pathTranslated == null)
      return null;
    
    String mimeType = null;
    
    if (webApp != null) {
      MimeTyper webAppMimeTyper = webApp.getMimeTyper();
      mimeType = webAppMimeTyper.getMimeType(pathTranslated);
    }

    // Use this mime typer as the default if there is no WebApplicationInterface
    // associated with this request.
    if (mimeType == null) {
      MimeTyper mimeTyper = getMimeTyper();

      if (pathTranslated != null)
        mimeType = mimeTyper.getMimeType (pathTranslated);

      if (mimeType == null && mimeTyper instanceof DefaultMimeTyper)
        mimeType = ((DefaultMimeTyper) mimeTyper).getDefaultMimeType();
    }

    if (isLoggingDebug ()) {
      logDebug ("AttributeFactory computes mime type of " +
                pathTranslated + " = " + mimeType);
    }

    return mimeType;
  }

  //-------------------------------------
  // PipelineableServletImpl methods
  //-------------------------------------
  /**
   * Services a DynamoHttpServletRequest/Response pair
   * @exception ServletException if an error occurred while processing
   * the servlet request
   * @exception IOException if an error occurred while reading or writing
   * the servlet request
   **/
  public void service (DynamoHttpServletRequest pRequest,
                       DynamoHttpServletResponse pResponse)
       throws IOException, ServletException
  {
    // Install the attribute factory
    MimeTypeAttributeFactory f = (MimeTypeAttributeFactory)
      pRequest.getPermanentAttribute (mFactoryKey);
    f.setRequest (pRequest);
    f.setServlet (this);
    pRequest.setAttributeFactory (ATTRIBUTE_NAME, f);
    Object objRestore = pRequest.getAttribute(ATTRIBUTE_NAME);
    if (objRestore != null) {
      pRequest.removeAttribute(ATTRIBUTE_NAME);
    }
    try {
      passRequest (pRequest, pResponse);
    }
    finally {
      if (objRestore == null) {
        pRequest.removeAttribute(ATTRIBUTE_NAME);
      }
      else {
        pRequest.setAttribute(ATTRIBUTE_NAME, objRestore);
      }
    }

  }

  //-------------------------------------
}
