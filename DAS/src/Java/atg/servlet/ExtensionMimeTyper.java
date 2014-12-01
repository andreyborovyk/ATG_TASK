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

package atg.servlet;

import java.util.*;
import java.io.*;
import atg.nucleus.*;
import javax.servlet.*;
import javax.servlet.http.*;
import atg.core.util.*;

/**
 * <p>This MimeTyper determines a MIME type from the extension on a
 * file name.  The mapping from extension to mime type is specified as
 * a String array property.
 *
 * <p>If a given extension is not found, then the MIME type will
 * default to the value of the "defaultMimeType" property.
 *
 * @see atg.server.tcp.RequestServer
 * @author Nathan Abramson
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/servlet/ExtensionMimeTyper.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo
 *   description: Determines a MIME type from the extension on a file name
 *   attribute: functionalComponentCategory Services
 *   attribute: featureComponentCategory Request Management
 *   attribute: icon /atg/ui/common/images/servletcomp.gif
 **/

public
class ExtensionMimeTyper
extends TimedOperationService
  implements DefaultMimeTyper, Serializable
{
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/servlet/ExtensionMimeTyper.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Properties

  /** The Strings of extension to mime-type mappings */
  String [] mExtensionToMimeType;

  /** Flag if this is case sensitive */
  boolean mCaseSensitive = false;

  /** The default MIME type */
  String mDefaultMimeType = "text/plain";

  /**
   * Is this the mime typer that is set in Nucleus to implement the
   * ServletContext methods for all servlets defined in the system.
   */
  boolean mDefaultMimeTyper = true;

  //-------------------------------------
  // Member variables

  /** The table mapping extension to MIME-type */
  Dictionary mTable;

  //-------------------------------------
  /**
   * Constructs a new ExtensionMimeTyper
   **/
  public ExtensionMimeTyper ()
  {
  }

  //-------------------------------------
  /**
   *
   * Processes the extensionToMimeType pairings into a Dictionary, capitalizing the extensions if case-sensitivity is off.
   **/
  void processMimeTypes ()
  {
    mTable = new UnsynchronizedHashtable ();
    String [] mapping = getExtensionToMimeType ();
    boolean caseSensitive = getCaseSensitive ();
    for (int i = 0; i + 1 < mapping.length; i += 2) {
      String extension = mapping [i];
      String mimeType = mapping [i + 1];
      /*
       * Avoid i18n bogosity in toUpperCase operation
       */
      if (!caseSensitive) extension = StringUtils.toUpperCase(extension);
      mTable.put (extension, mimeType);
    }
  }

  //-------------------------------------
  // MimeTyper methods
  //-------------------------------------
  /**
   * Returns the MIME type for the specified file name, or null if
   * the MIME type cannot be determined.
   **/
  public String getMimeType (String pFileName)
  {
    long startTime = getRequestStartTime ();

    try {
      // Find the extension
      String extension;

      // since, in production, most pages handled by dynamo should
      // be jsp,jspf, jhtml or html, let's check for those first.
      if (pFileName.endsWith("jsp")) {
        extension = "jsp";
      } 
      else if (pFileName.endsWith(".jspf")) {
        extension = "jspf";
      }
      else if (pFileName.endsWith(".jhtml")) {
        extension = "jhtml";
      }
      else if (pFileName.endsWith(".html")) {
        extension = "html";
      }
      else {
        int ix = pFileName.lastIndexOf ('.');
        if (ix < 0) return null;
        extension = pFileName.substring (ix + 1);
      }


      String ret = (String) mTable.get(extension);
      if(ret == null) {
      if (!getCaseSensitive ()) {
          String upperExtension = StringUtils.toUpperCase(extension);

          ret = (String) mTable.get(upperExtension);

          //cache it for next time
          if(ret != null) {
          mTable.put(extension,ret);
          }
      }
      }
      return (ret == null) ? null : ret;
    }
    finally {
      notifyHandledRequest (startTime);
    }
  }

  //-------------------------------------
  // Properties
  //-------------------------------------
  /**
   * Sets the flag indicating whether or not the MIME typer takes case
   * into account when matching a file extension to the MIME types
   * file.
   * @beaninfo
   *   description: True if the MIME typer should be case sensitive
   **/
  public void setCaseSensitive (boolean pCaseSensitive)
  {
    mCaseSensitive = pCaseSensitive;
  }

  //-------------------------------------
  /**
   * Returns the flag indicating whether or not the MIME typer takes
   * case into account when matching a file extension to the MIME
   * types file.
   **/
  public boolean getCaseSensitive ()
  {
    return mCaseSensitive;
  }

  /**
   * Sets the property that say that this is this the mime typer that
   * defines mime types for the ServletContext
   * implemented by the GenericContext class (i.e. the one that nucleus
   * implements).
   * @beaninfo
   *   description: True if this is the MIME typer used by Nucleus
   **/
  public void setDefaultMimeTyper (boolean pDefaultMimeTyper)
  {
    mDefaultMimeTyper = pDefaultMimeTyper;
  }

  //-------------------------------------
  /**
   * Gets the property that says that this is this the mime typer that
   * defines mime types for the ServletContext
   * implemented by the GenericContext class (i.e. the one that nucleus
   * implements).
   **/
  public boolean isDefaultMimeTyper ()
  {
    return mDefaultMimeTyper;
  }

  //-------------------------------------
  /**
   * Sets the default mime type to be used if an extension is not found
   * @beaninfo
   *   description: The MIME type used if no extension mapping is found
   **/
  public void setDefaultMimeType (String pDefaultMimeType)
  {
    mDefaultMimeType = pDefaultMimeType;
  }

  //-------------------------------------
  /**
   * Returns the default mime type to be used if an extension is not found
   **/
  public String getDefaultMimeType ()
  {
    return mDefaultMimeType;
  }

  //-------------------------------------
  /**
   * Returns the array of Strings mapping extension to mime type.  The
   * extension-&lt;mime type mappings are done in pairs, so the
   * even-numbered elements should be extensions, and the odd-numbered
   * elements should be mime types.
   * @beaninfo
   *   description: The mapping between extensions and MIME types
   **/
  public String [] getExtensionToMimeType ()
  {
    return mExtensionToMimeType;
  }

  //-------------------------------------
  /**
   * Sets the array of Strings mapping extension to mime type.  The
   * extension-&lt;mime type mappings are done in pairs, so the
   * even-numbered elements should be extensions, and the odd-numbered
   * elements should be mime types.
   **/
  public void setExtensionToMimeType (String [] pExtensionToMimeType)
  {
    mExtensionToMimeType = pExtensionToMimeType;
  }

  //-------------------------------------
  // Service methods
  //-------------------------------------
  /**
   *
   * This is called after the service has been created, added to the
   * Registry, and configured.  This method should start any processes
   * required to run the service.
   * @exception ServiceException if there was a problem with the operation
   **/
  public void doStartService ()
    throws ServiceException
  {
    if (isDefaultMimeTyper()) {
      /*
       * Automatically define this class as the servlet in the system
       */
      getNucleus().setMimeTyper(this);
    }

    processMimeTypes ();
  }

  //-------------------------------------
  // AdminableService methods
  //-------------------------------------
  /**
   *
   * Creates and returns a new Servlet that will administer this
   * service.  By default, this creates a ServiceAdminServlet, but
   * subclasses may create their own servlets.
   **/
  protected Servlet createAdminServlet ()
  {
    class AdminServlet extends ServiceAdminServlet {
      public AdminServlet (Object pService) {
        super (pService, ExtensionMimeTyper.this.getNucleus ());
      }

      protected void printAdmin (HttpServletRequest pRequest,
                                 HttpServletResponse pResponse,
                                 ServletOutputStream pOut)
           throws ServletException, IOException
      {
        pOut.println ("<h2>MIME type mapping</h2>");
        pOut.println ("<table border>");
        pOut.println ("<tr><th>extension</th><th>MIME type</th></tr>");
        for (Enumeration e = mTable.keys (); e.hasMoreElements (); ) {
          String ext = (String) e.nextElement ();
          pOut.print ("<tr><td>");
          pOut.print (ext);
          pOut.print ("</td><td>");
          pOut.print ((String) (mTable.get (ext)));
          pOut.println ("</td></tr>");
        }
        pOut.println ("</table>");
      }
    }

    return new AdminServlet (this);
  }

  //-------------------------------------
}
