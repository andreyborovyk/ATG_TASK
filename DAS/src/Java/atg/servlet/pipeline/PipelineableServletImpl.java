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
import javax.servlet.http.HttpServletRequest;

/**
 *
 * <p>A PipelineableServletImpl is an implementation of a
 * PipelineableServlet that also implements Servlet.
 *
 * @see PipelineableServlet
 * @author Nathan Abramson
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/servlet/pipeline/PipelineableServletImpl.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo
 *   description: A pipelineable servlet
 *   attribute: functionalComponentCategory Services
 *   attribute: featureComponentCategory Pipeline
 *   attribute: icon /atg/ui/common/images/pipelinecomp.gif
 **/

public
class PipelineableServletImpl
extends TimedOperationService
implements PipelineableServlet, Servlet, AdminableService
{
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/servlet/pipeline/PipelineableServletImpl.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants

  //-------------------------------------
  // Properties

  /** what kind of path getting we use **/
  boolean usePathInfo = false;

  /** The Servlet handling this Service's administration */
  Servlet mAdminServlet;

  /** The next Servlet in the pipeline */
  Servlet mNextServlet;

  /** The default ServletInfo String */
  String mServletInfo = "PipelineableServletImpl";

  //-------------------------------------
  // Member variables

  /** The ServletConfig for this Servlet */
  ServletConfig mServletConfig;

  //-------------------------------------
  /**
   * Sets the next Servlet in the pipeline, null if this is the end of
   * the pipeline.
   * @beaninfo
   *   description: The next servlet in the pipeline
   **/
  public void setNextServlet (Servlet pNextServlet)
  {
    mNextServlet = pNextServlet;
  }

  //-------------------------------------
  /**
   * Returns the next Servlet in the pipeline, null if this is the end
   * of the pipeline.
   **/
  public Servlet getNextServlet ()
  {
    return mNextServlet;
  }

  //-------------------------------------
  /**
  * Returns the kind of path getting
  **/
  public boolean isUsePathInfo() {
    return usePathInfo;
  }

  //-------------------------------------
  /**
  * Sets the kind of path getting
  **/
  public void setUsePathInfo(boolean usePathInfo) {
    this.usePathInfo = usePathInfo;
  }

  //-------------------------------------
  /**
   * Passes the specified request on to the next servlet in the pipeline
   * @exception ServletException if an error occurred while processing
   * the servlet request
   * @exception IOException if an error occurred while reading or writing
   * the servlet request
   **/
  public void passRequest (DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
       throws IOException, ServletException
  {
    if (mNextServlet != null) {
      /*
       * Go directly to the service method of the next servlet if
       * possible to avoid 3 method calls per entry in the pipline
       */
      if (mNextServlet instanceof PipelineableServletImpl) {
        ((PipelineableServletImpl) mNextServlet).service(pRequest, pResponse);
      }
      else {
        mNextServlet.service (pRequest, pResponse);
      }
    }
  }

  //-------------------------------------
  /**
   * Passes the specified request on to the next servlet in the pipeline
   * @exception ServletException if an error occurred while processing
   * the servlet request
   * @exception IOException if an error occurred while reading or writing
   * the servlet request
   **/
  public void passRequest (ServletRequest pRequest,
                           ServletResponse pResponse)
       throws IOException, ServletException
  {
    if (mNextServlet != null) {
      /*
       * Go directly to the service method of the next servlet if
       * possible to avoid 3 method calls per entry in the pipline
       */
      if (mNextServlet instanceof PipelineableServletImpl &&
          pRequest instanceof DynamoHttpServletRequest &&
          pResponse instanceof DynamoHttpServletResponse) {
        ((PipelineableServletImpl) mNextServlet).service(
                       (DynamoHttpServletRequest) pRequest,
                       (DynamoHttpServletResponse) pResponse);
      }
      else {
        mNextServlet.service (pRequest, pResponse);
      }
    }
  }

  //-------------------------------------
  // Servlet implementation
  //-------------------------------------
  /**
   * Initializes this Servlet with the specified configuration
   * @exception ServletException if there was a problem initializing the
   * Servlet
   **/
  public void init (ServletConfig pConfig) throws ServletException
  {
    mServletConfig = pConfig;
  }

  //-------------------------------------
  /**
   * Returns the configuration for this Servlet
   * @beaninfo
   *   description: The configuration object for this servlet
   **/
  public ServletConfig getServletConfig ()
  {
    return mServletConfig;
  }

  //-------------------------------------
  /**
   * Returns the information string for this servlet
   * @beaninfo
   *   description: Information about this servlet
   **/
  public String getServletInfo () {
    return mServletInfo;
  }

  //-------------------------------------
  /**
   * Sets the information string for this servlet
   **/
  public void setServletInfo (String pServletInfo) {
    mServletInfo = pServletInfo;
  }

  //-------------------------------------
  /**
   * Called when the servlet is to be destroyed
   * @beaninfo
   *   description: Destroys this servlet (WARNING: DANGEROUS!)
   *   expert: true
   **/
  public void destroy ()
  {
  }

  //-------------------------------------
  /**
   * If the request is an instanceof HttpServletRequest, then the service
   * method is passed onto the service handler for the HttpServletRequest
   * object. Otherwise passes the request to the next servlet in the
   * pipeline.
   * @exception ServletException if an error occurred while processing
   * the servlet request
   * @exception IOException if an error occurred while reading or writing
   * the servlet request
   **/
  public void service (ServletRequest pRequest,
                       ServletResponse pResponse)
       throws IOException, ServletException
  {
    long startTime = getRequestStartTime ();
    try {
      if (pRequest instanceof DynamoHttpServletRequest &&
          pResponse instanceof DynamoHttpServletResponse) {
        service ((DynamoHttpServletRequest) pRequest,
                 (DynamoHttpServletResponse) pResponse);
      }
      else if (pRequest instanceof HttpServletRequest &&
         pResponse instanceof HttpServletResponse) {
        service ((HttpServletRequest) pRequest,
                 (HttpServletResponse) pResponse);
      }
      else {
        passRequest (pRequest, pResponse);
      }
    }
    finally {
      notifyHandledRequest (startTime);
    }
  }

  //-------------------------------------
  /**
   *
   * Services an HttpServletRequest/Response pair
   * @exception ServletException if an error occurred while processing
   * the servlet request
   * @exception IOException if an error occurred while reading or writing
   * the servlet request
   **/
  public void service (HttpServletRequest pRequest,
                       HttpServletResponse pResponse)
       throws IOException, ServletException
  {
    if (pRequest instanceof DynamoHttpServletRequest &&
        pResponse instanceof DynamoHttpServletResponse) {
      service ((DynamoHttpServletRequest) pRequest,
               (DynamoHttpServletResponse) pResponse);
    }
    else {
      passRequest (pRequest, pResponse);
    }
  }

  //-------------------------------------
  /**
   *
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
    passRequest (pRequest, pResponse);
  }

  //-------------------------------------
  // AdminableService methods
  //-------------------------------------

  /**
   * Returns the Servlet that will handle requests directed at this
   * service.  This will first check to see if the servlet is already
   * created, and if not it will create it by calling
   * createAdminServlet.
   * @see #createAdminServlet
   **/
  public synchronized Servlet getAdminServlet ()
  {
    if (mAdminServlet == null) {
      mAdminServlet = createAdminServlet ();
    }
    return mAdminServlet;
  }

  /**
   * Creates and returns a new Servlet that will administer this
   * service.  By default, this creates a ServiceAdminServlet, but
   * subclasses may create their own servlets.
   **/
  protected Servlet createAdminServlet ()
  {
    return new PipelineableServletAdminServlet(this, getNucleus());
  }

  //-------------------------------------
}
