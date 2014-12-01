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

package atg.nucleus;

import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import atg.beans.DynamicBeanInfo;
import atg.beans.DynamicBeans;
import atg.beans.DynamicPropertyDescriptor;
import atg.beans.PropertyNotFoundException;
import atg.core.exception.StackTraceUtils;
import atg.core.net.URLUtils;
import atg.core.util.BeanUtils;
import atg.core.util.StringUtils;
import atg.naming.NameContext;
import atg.naming.NameResolver;
import atg.nucleus.AdminServletConfiguration.AdminContextPlugin;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.CollectingLoggingSupport;
import atg.nucleus.logging.LogEvent;
import atg.nucleus.logging.LoggingPropertied;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.PostRequestProcessor;
import atg.servlet.ServletUtil;

@SuppressWarnings("serial")
/**
 *
 * <p>A ServiceAdminServlet is the base class for services that wish
 * to provide an HTTP interface.  Such a service should implement
 * AdminableService, and are expected to provide a Servlet that will
 * implement that interface.  For convenience, that Servlet may extend
 * ServiceAdminServlet.
 *
 * @author Nathan Abramson
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/nucleus/ServiceAdminServlet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public
class ServiceAdminServlet
extends HttpServlet
implements PropertyValueFormatter, atg.beans.NotSerializable
{
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/nucleus/ServiceAdminServlet.java#2 $$Change: 651448 $";

  private static java.util.ResourceBundle sAdminResourceBundle = null;

  //-------------------------------------
  // Constants
  Object [] NULL_ARGS = new Object [0];
  @SuppressWarnings("unchecked")
  Class [] NULL_PARAMETER_TYPES = new Class[0];

  //-------------------------------------
  // Member variables

  /** The Service for whom this interface is operating */
  transient protected Object mService;

  /** The Nucleus controlling the system */
  transient protected Nucleus mNucleus;
  
  /** The service as returned from Nucleus' resolveName method */
  transient protected Object mResolvedService;

  /**
   * The request attribute name that hosts the list of
   * PostRequestProcessors to be invoked at the end of
   * the HTML admin servlet rendering. 
   */
  protected static final String ATTR_AMIN_SERVLET_POST_REQUEST_PROCESSORS =
    "atg.nucleus.ServiceAdminServlet.postRequestProcessors"; 

  //-------------------------------------
  /**
   *
   * Constructs a new ServiceAdminServlet
   * @param pService the service object to be manipulated by this servlet
   * @param pNucleus the Nucleus controlling the service hierarchy
   **/
  public ServiceAdminServlet (Object pService,
                              Nucleus pNucleus)
  {
    mService = pService;
    mNucleus = pNucleus;
    // mNucleus is sometimes NULL in DAF
    // this is a hack to work around the real problem
    if ( mNucleus == null ) mNucleus = Nucleus.getGlobalNucleus();
  }

  //-------------------------------------
  // Service method
  //-------------------------------------
  /**
   * Handles requests
   * @param pRequest the current request
   * @param pReponse the current response
   * @exception ServletException if an error occurred while processing
   * the servlet request
   * @exception IOException if an error occurred while reading or writing
   * the servlet request
   *
   */
  public void service (HttpServletRequest pRequest,
                       HttpServletResponse pResponse)
    throws ServletException, IOException
  {
    if (pRequest.getRequestURI() != null && !pRequest.getRequestURI().endsWith("/")) {
      StringBuffer url = pRequest.getRequestURL();
      url.append ('/');
      String queryString = pRequest.getQueryString();
      if (queryString != null && queryString.length() > 0) {
        url.append ('?');
        url.append (queryString);
      }
      pResponse.sendRedirect(pResponse.encodeURL(url.toString()));
      return;
    }

    /*
     * Need to set the default content type for display.   If necessary,
     * a servlet can reset this to something else as long as they do it
     * before the content has been written.
     */
    pResponse.setContentType("text/html");

    /*
     * Make sure to disable caching
     */
    pResponse.setHeader("Pragma", "no-cache");
    pResponse.setHeader("Expires", "Tue, 04 Dec 1993 21:29:02 GMT");

    // Dispatch on the incoming parameters

    {
      String value = pRequest.getParameter("cancelMethodInvocation");
      if (value != null) {
        printService (pRequest, pResponse);
        return;
      }
    }

    {
      String value = pRequest.getParameter ("propertyName");
      if (value != null) {
        printProperty (pRequest, pResponse, value);
        return;
      }
    }

    {
      String value = pRequest.getParameter ("eventSetName");
      if (value != null) {
        printEventSet (pRequest, pResponse, value);
        return;
      }
    }

    {
      String value = pRequest.getParameter("invokeMethod");
      if (value != null) {
        printMethodInvocation(pRequest, pResponse, value);
        return;
      }
    }

    {
      String value = pRequest.getParameter("shouldInvokeMethod");
      if (value != null) {
        printMethodInvocationVerification(pRequest, pResponse, value);
        return;
      }
    }

    {
      String value = pRequest.getParameter( "reloadComponent" );
      if ( value != null ) {
        reloadComponent( pRequest, pResponse );
        return;
      }
    }

    printService (pRequest, pResponse);
  }

  //-------------------------------------
  /**
   * Reload a class generated from a InstanceFactory instance.
   * @param pRequest the current request
   * @param pReponse the current response
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void reloadComponent( HttpServletRequest pRequest,
    HttpServletResponse pResponse ) throws ServletException, IOException
  {
    String serviceName = pRequest.getPathInfo();
    if ( StringUtils.isBlank(serviceName) )
      return;
    String srp = getAbsoluteNameOf( mService );
    if ( ! StringUtils.isBlank(srp) ) {
      srp = getScopeRelativePath( srp );
      if ( ! StringUtils.isBlank(srp) )
        serviceName = srp;
    }
    if (serviceName.endsWith ("/"))
      serviceName = serviceName.substring( 0, serviceName.length () - 1 );
    if ( ! StringUtils.isBlank(serviceName) ) {
      try {
        NameResolver resolver = null;
        if ( pRequest instanceof NameResolver ) {
          resolver = (NameResolver) pRequest;
        }
        if ( mNucleus.restartInstanceFactoryComponent(serviceName,mService,resolver) ) {
          pResponse.sendRedirect( pRequest.getRequestURL().toString() );
          return;
        }
      }
      catch ( InstanceFactoryException ife ) {
        ife.printStackTrace();
      }
    }
  }

  //-------------------------------------
  /**
   * Prints the formatted information for a service, including the name,
   * directory listing, and properties.
   *   
   * @param pRequest the current request
   * @param pReponse the current response
   * @exception ServletException if an error occurred while processing
   * the servlet request
   * @exception IOException if an error occurred while reading or writing
   * the servlet request
   *
   */
  public void printService (HttpServletRequest pRequest,
                            HttpServletResponse pResponse)
    throws ServletException, IOException
  {
    try {
      ServletOutputStream out = pResponse.getOutputStream ();
      out.println ("<html><head><title>");
      printHeaderTitle (pRequest, pResponse, out);
      out.println ("</title>");
      insertStyle(pRequest, pResponse, out);
      out.println("</head>");
      printBodyTag (pRequest, pResponse, out);

      printTitle (pRequest, pResponse, out);
      printServiceInfo (pRequest, pResponse, out);
      printDirectory (pRequest, pResponse, out);
      printAdmin (pRequest, pResponse, out);
      printPropertyValues (pRequest, pResponse, out);
      printEventSets (pRequest, pResponse, out);
      printMethods(pRequest, pResponse, out);
      printStringValue (pRequest, pResponse, out);


      out.println ("</body></html>");
      
    } finally {
      executeAdminServletPostRequestProcessors(pRequest, pResponse);
    }
  }


  /**
   * Insert any style/css tags... inserted into the header.
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pOut the output stream to write to
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void insertStyle(HttpServletRequest pRequest,
                           HttpServletResponse pResponse,
                           ServletOutputStream pOut) throws ServletException, IOException {
    int iStyle = 0;
    if ((mNucleus != null) &&
        (mNucleus.getAdminServletConfiguration() != null)) {
      iStyle = mNucleus.getAdminServletConfiguration().getDefaultStyleIndex();
    }

    String strContextRoot = pRequest.getContextPath();

    if (strContextRoot.equals("/"))
      strContextRoot = "";

    pOut.println("<link rel=\"" + 
                 ((0 == iStyle) ? "" : "alternate ") +
                 "stylesheet\" type=\"text/css\" href=\"" +
                 pResponse.encodeURL(
                   strContextRoot + 
                   "/atg/dynamo/admin/admin.css") + "\" title=\"Standard\">");

    pOut.println("<link rel=\"" +
                 ((1 == iStyle) ? "" : "alternate ") + 
                 "stylesheet\" type=\"text/css\" href=\""  +
                 pResponse.encodeURL(
                   strContextRoot +
                   "/atg/dynamo/admin/admin-retro.css") +
                   "\" title=\"Retro\"\">");

    pOut.println("<script type=\"text/javascript\" src=\""  + 
                 pResponse.encodeURL(strContextRoot +
                                      "/atg/dynamo/admin/admin.js") +
                 "\"></script>");
  }


  //-------------------------------------
  /**
   * Prints the title of the page's header.  By default, this will
   * print the name of the service with links to the various parts of
   * the name.
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pOut the output stream to write to
   * @exception ServletException if an error occurred
   * @exception IOException if an error occurred
   *
   */
  protected void printHeaderTitle (HttpServletRequest pRequest,
                                   HttpServletResponse pResponse,
                                   ServletOutputStream pOut)
       throws ServletException, IOException
  {
    String serviceName = pRequest.getPathInfo ();
    //Decode url
    serviceName = URLUtils.unescapeUrlString(serviceName);
    pOut.println(getResourceString("serviceAdminServletService") +
                 serviceName);
  }

  


  //-------------------------------------
  /**
   * Prints the title of the page.  By default, this will print the
   * name of the service.
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pOut the output stream to write to
   * @exception ServletException if an error occurred
   * @exception IOException if an error occurred
   *
   */
  protected void printTitle (HttpServletRequest pRequest,
                             HttpServletResponse pResponse,
                             ServletOutputStream pOut)
       throws ServletException, IOException
  {
    pOut.print ("<h1>");

    // Get the service name, without trailing "/"
    String serviceName = pRequest.getPathInfo ();
    if (serviceName == null) 
      serviceName = "";
    if (serviceName.endsWith ("/"))
      serviceName = serviceName.substring (0, serviceName.length () - 1);
    
    //Decode URL
    serviceName = URLUtils.unescapeUrlString(serviceName);

    String [] nameParts = 
      StringUtils.splitStringAtCharacter (serviceName, '/');

    // Form individual links for the parts
    for (int i = 0; i < nameParts.length; i++) {
      StringBuilder partialName = new StringBuilder();
      for (int j = 0; j <= i; j++) {
        partialName.append (nameParts [j]);
        partialName.append ('/');
      }
      pOut.print (formatServiceLink (partialName.toString (), pRequest));
      /*
       * Make James happy and make the / link clickable 
       */
      if (i == 0)
        pOut.print(getResourceString("serviceAdminServletService"));
      pOut.print (nameParts [i]);
      pOut.print ('/');
      pOut.print ("</a>");
    }

    pOut.println ("</h1>");
    
    pOut.println (formatObject (mService.getClass (),
                                pRequest));
    String strPath =  getAbsoluteNameOf(mService);

    if (!serviceName.equals("") && (strPath != null)  && !serviceName.equals(strPath)) {
      pOut.println("<br>" +
                   getResourceString("serviceAdminServletCanonical") +
                   formatServiceLink(strPath, pRequest) + strPath + "<a>");
    }

    // The path to use in getInstanceFactoryForService() call below
    String instanceFactoryPath = serviceName;

    String strScopeRelative = getScopeRelativePath(strPath);
    if ((strScopeRelative != null) &&
        !strScopeRelative.equals(serviceName)) {
      instanceFactoryPath = strScopeRelative;
      pOut.println("<br>" +
                   getResourceString("serviceAdminServletScopeRelative") +
                   formatServiceLink(strScopeRelative, pRequest) + strScopeRelative + "<a>");      
    }

    
    String nameForClass = "null";
    if (mService != null)
      nameForClass = mService.getClass().getName();
    if (mResolvedService != null && mResolvedService instanceof ReloadableService) {
      pOut.print("&nbsp;&nbsp;Reload&nbsp;");
      pOut.print (" <a href=\"" +
          pResponse.encodeURL(
              formatServiceName(pRequest.getPathInfo (),
                  pRequest) +
          "?shouldInvokeMethod=reload") +
          "\">" + nameForClass +
      "</a>");
    }
    else {
      InstanceFactory il = mNucleus.getInstanceFactoryForService(
        instanceFactoryPath);
      if (il != null && il.isReloadable(instanceFactoryPath)) {
        pOut.print( "&nbsp;&nbsp;" );
        pOut.print( " <a href=\"" + pResponse.encodeURL(
            formatServiceName(pRequest.getPathInfo(), pRequest) +
            "?reloadComponent=true" ) + "\">Reload</a>" );
      }
    }

    pOut.println ("<hr>");
  }

  //-------------------------------------
  /**
   * Prints the serviceInfo property of the service.
   *
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pOut the output stream to write to
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void printServiceInfo (HttpServletRequest pRequest,
                                   HttpServletResponse pResponse,
                                   ServletOutputStream pOut)
       throws ServletException, IOException
  {
    String serviceName = formatServiceName(pRequest.getPathInfo (),
                                             pRequest);      
    pOut.println("<a href=\"" +
                 serviceName + "?propertyName=serviceConfiguration\">" +
                 getResourceString("serviceAdminServletViewConfig") +
                 "</a>");

    try {
      PropertyDescriptor pd = getPropertyDescriptor (mService.getClass (),
                                                     "serviceInfo");
      if (pd != null) {
        Method m = pd.getReadMethod ();
        if (m != null) {
          Object val = m.invoke (mService, new Object [0]);
          if (val != null) {
            pOut.println(getResourceString("serviceAdminServletServiceInfoHdr"));
            pOut.println (formatObject (val, pRequest));
          }
        }
      }
    }
    catch (IntrospectionException exc) {}
    catch (IllegalAccessException exc) {}
    catch (IllegalArgumentException exc) {}
    catch (InvocationTargetException exc) {}
  }

  //-------------------------------------
  /**
   * Prints the string value (toString) of the service.
   *
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pOut the output stream to write to
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void printStringValue (HttpServletRequest pRequest,
                                   HttpServletResponse pResponse,
                                   ServletOutputStream pOut)
       throws ServletException, IOException
  {
    String val = mService.toString ();
    if (val != null) {
      pOut.println(getResourceString("serviceAdminServletStringValueHdr"));
      pOut.println (val);
    }
  }

  //-------------------------------------
  /**
   * Prints the opening body tag.  By default, this prints out no
   * background color.
   *
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pOut the output stream to write to
   * @exception ServletException if an error occurred 
   * @exception IOException if an error occurred
   *
   */
  protected void printBodyTag (HttpServletRequest pRequest,
                               HttpServletResponse pResponse,
                               ServletOutputStream pOut)
       throws ServletException, IOException
  {
    pOut.println ("<body text=00214A bgcolor=ffffff link=E87F02 vlink=637DA6>");

      String prefix = "";
      if (mNucleus.getAdminServerPrefix() != null) 
        prefix = mNucleus.getAdminServerPrefix();
      if (!prefix.endsWith("/"))
        prefix = prefix + "/";
      
      pOut.println ("<img src=\"" + prefix + "atg/dynamo/admin/images/comp-banner.gif\" alt=\"Dynamo Component Browser\" align=top width=585 height=37 border=0><p>");

      pOut.println("<p style=\"display: inline;\"><a href=\"" + pResponse.encodeURL(prefix) + "\">" + getResourceString("serviceAdminServletAdminLink"));
    

    /*
     * This is a hack - if the nucleus is the main page, we can't reference
     * any other files cause probably this server does not support them.
     * If we have a servlet path, then we can use the banner and admin link
     */
    if (mNucleus.getAdminServerPrefix() != null || 
        (pRequest.getServletPath() != null && pRequest.getServletPath().length() > 0)) {

      if ((mNucleus.getAdminServletConfiguration() != null) &&
          (mNucleus.getAdminServletConfiguration().getBodyHeaderAdminContentPlugins() != null)) {
        for (AdminContextPlugin inserterCur :
               mNucleus.getAdminServletConfiguration().getBodyHeaderAdminContentPlugins()) {
          // make extra-sure that our inserter doesn't
          // block the rest of the HTML admin
          try {
            inserterCur.insertContent(pRequest, pResponse,
                                      pOut, this, mService);
          } catch (ThreadDeath e) {
            throw e;
          }
          catch (Exception e) {
            ApplicationLogging logger = null;
            if (inserterCur instanceof ApplicationLogging) {
              logger = (ApplicationLogging)inserterCur;
            }
            else {
              logger = mNucleus;
            }
            if (logger.isLoggingError()) {
              logger.logError(e);
            }
          }
        }
      }
    }
  }

  //-------------------------------------
  @SuppressWarnings("unchecked")
  /**
   * Prints a directory listing of the elements in the context, if the
   * service is a NameContext.
   *
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pOut the output stream to write to
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void printDirectory (HttpServletRequest pRequest,
                                 HttpServletResponse pResponse,
                                 ServletOutputStream pOut)
       throws ServletException, IOException
  {
    if (!(mService instanceof NameContext)) return;
    pOut.println(getResourceString("serviceAdminServletDirListingHdr"));
    pOut.println ("<ul>");

    NameContext n = (NameContext) mService;
    Enumeration e = sortStringEnumeration(n.listElementNames());
    while (e.hasMoreElements ()) {
      String name = (String) e.nextElement ();
      Object obj = n.getElement (name);
      boolean isNameContext = (obj instanceof NameContext);
      pOut.print ("<h3><a href=\"");
      pOut.print (formatServiceName(name, pRequest));
      pOut.print ("\">");
      pOut.print (name);
      if (isNameContext) pOut.print ('/');
      pOut.print ("</a></h3>");

      // Print one level deeper
      if (obj instanceof NameContext) {
        NameContext n2 = (NameContext) obj;
        pOut.println ("<ul>");
        int i = 0;
        Enumeration e2 = sortStringEnumeration(n2.listElementNames ());
        for (; e2.hasMoreElements () && i < 5; i++) {
          String name2 = (String) e2.nextElement ();
          Object obj2 = n2.getElement (name2);
          boolean isNameContext2 = (obj2 instanceof NameContext);
          pOut.print ("<a href=\"");
          pOut.print (formatServiceName(name, name2 + "/", pRequest));
          pOut.print ("\">");
          pOut.print (name2);
          if (isNameContext2) pOut.print ('/');
          pOut.print ("</a><br>");
        }
        if (e2.hasMoreElements ())
          pOut.println(getResourceString("serviceAdminServletDirListingMore"));
        pOut.println ("</ul>");
      }
    }

    pOut.println ("</ul>");
  }

  @SuppressWarnings("unchecked")
  /*
   * Sorts an enumeration of strings
   */
  Enumeration sortStringEnumeration(Enumeration pE) {
    Vector v = new Vector();
    while (pE.hasMoreElements()) {
      v.addElement(pE.nextElement());
    }
    int size = v.size();
    int i, j;

    /*
     * Insertion sort - preserved existing order of the elements if they
     * are already sorted
     */
    for (i = 1; i < size; i++) {
      String value = (String) v.elementAt(i);

      for (j = i - 1; j >= 0 && ((String)v.elementAt(j)).compareTo(value) > 0;
           j--) {
        v.setElementAt(v.elementAt(j), j+1);
      }
      if (i != j+1) v.setElementAt(value, j+1);
    }
    return v.elements();
  }

  //-------------------------------------
  /**
   * Sorts the list of property descriptors by property name and
   * returns a new list of the sorted descriptors.
   *
   * @param pDescriptors the array of property descriptors to sort
   * @return the shorted property descriptors
   */
  protected PropertyDescriptor [] sortPropertyDescriptors (PropertyDescriptor [] pDescriptors)
  {
    PropertyDescriptor [] ret = new PropertyDescriptor [pDescriptors.length];
    sortFeatureDescriptors(pDescriptors, ret);
    return ret;
  }

  //-------------------------------------
  /**
   * Sorts the list of method descriptors by method name and
   * returns a new list of the sorted descriptors.
   *
   * @param pDescriptors the method descriptors to sort.
   * @return the sorted method descriptors.
   */
  protected MethodDescriptor [] sortMethodDescriptors (MethodDescriptor [] pDescriptors)
  {
    MethodDescriptor [] ret = new MethodDescriptor [pDescriptors.length];
    sortFeatureDescriptors(pDescriptors, ret);
    return ret;
  }

  //-------------------------------------
  /**
   *
   * Sorts the pSrcDescriptors list of feature descriptors by name and
   * places the sorted list of descriptors into the pDstDescriptors array
   **/
  void sortFeatureDescriptors (FeatureDescriptor [] pSrcDescriptors,
                               FeatureDescriptor [] pDstDescriptors)
  {
    for (int i = 0; i < pSrcDescriptors.length; i++) {
      FeatureDescriptor pd = pSrcDescriptors [i];
      String name = pd.getName ();

      // Find the position
      int min = -1;
      int max = i;
      while (min + 1 < max) {
        int mid = (min + max) / 2;
        int cmp = name.compareTo (pDstDescriptors [mid].getName ());
        if (cmp > 0) min = mid;
        else max = mid;
      }
      
      // Position is in max
      if (max < i) {
        System.arraycopy (pDstDescriptors, max, pDstDescriptors, max+1, i-max);
      }
      pDstDescriptors [max] = pd;
    }
  }

  //-------------------------------------
  @SuppressWarnings("unchecked")
  /**
   * Returns true if the supplied Method has parameter types
   */
  boolean hasParameterTypes(Method pMethod) {
    if (pMethod == null)
      return false;

    Class [] parameterTypes = pMethod.getParameterTypes();
    if (parameterTypes == null)
      return false;
    else if (parameterTypes.length == 0)
      return false;
    else
      return true;
  }

  //-------------------------------------
  @SuppressWarnings("unchecked")
  /**
   * Returns true if the supplied Method has a return type
   */
  boolean hasReturnType(Method pMethod) {
    if (pMethod == null)
      return false;

    Class returnType = pMethod.getReturnType();
    if (returnType == null)
      return false;
    else if (returnType.equals(Void.TYPE))
      return false;
    else
      return true;
  }

  //-------------------------------------
  /**
   * Returns true if the method is used for a property
   */
  boolean isPropertyMethod(String pMethodName) {
    if (pMethodName == null)
      return false;

    if ((pMethodName.startsWith("get")) || (pMethodName.startsWith("is")))
      return true;
    else
      return false;
  }
  

  //-------------------------------------
  @SuppressWarnings("unchecked")
  /**
   * Prints a listing of all the service's properties and values.
   *
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pOut the output stream to write to
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void printMethods (HttpServletRequest pRequest,
                               HttpServletResponse pResponse,
                               ServletOutputStream pOut)
       throws ServletException, IOException
  {
    pOut.println (getResourceString("serviceAdminServletMethodsHdr"));

    Class cl = mService.getClass ();
    try {
      String serviceName = formatServiceName (pRequest.getPathInfo (),
                                              pRequest);      

      pOut.println ("<table border>");
      pOut.println(getResourceString("serviceAdminServletMethodsTableHdr"));

      BeanInfo info = BeanUtils.getBeanInfo (cl);
      MethodDescriptor [] mds = info.getMethodDescriptors();
      mds = sortMethodDescriptors (mds);

      int rowCount = 0;
      
      for (int i = 0; i < mds.length; i++) {
        MethodDescriptor md = mds [i];
        Method method = md.getMethod ();
        String name = md.getName ();

        // if we have no parameters and this method is not used
        // as a property accessor then we can invoke these methods
        if ((! hasParameterTypes(method)) &&
            (! isPropertyMethod(name))) {
          pOut.print ("<tr class=\"" +
                      (((rowCount++ % 2) == 0) ? "even" : "odd") +
                      "\"><td>");
          pOut.print ("<a href=\"" +
                      pResponse.encodeURL(
                      serviceName +
                      "?shouldInvokeMethod=" +
                      name) +
                      "\">" +
                      name +
                      "</a>");
          pOut.print ("</td><td>");
          ClassPropertyValueFormatter f = new ClassPropertyValueFormatter ();
          pOut.print (f.formatPropertyValue (method.getReturnType(), 
                                             pRequest, f));
          pOut.print ("</td><td>");
          pOut.print (f.formatPropertyValue (method.getDeclaringClass(), 
                                             pRequest, f));
          pOut.print ("</td></tr>");
        }        
      }
      pOut.println ("</table>");
    }
    catch (IntrospectionException exc) {
      pOut.println(getResourceString("serviceAdminServletNoBeanInfo") +
                    exc.toString ());
    }
  }

  //-------------------------------------
  @SuppressWarnings("unchecked")
  /**
   * Prints a listing of all the service's properties and values.
   *
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pOut the output stream to write to
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void printPropertyValues (HttpServletRequest pRequest,
                                      HttpServletResponse pResponse,
                                      ServletOutputStream pOut)
       throws ServletException, IOException
  {
    pOut.println (getResourceString("serviceAdminServletPropertiesHdr"));

    Class cl = mService.getClass ();
    try {
      BeanInfo info = Introspector.getBeanInfo (cl);
      PropertyDescriptor [] pds = info.getPropertyDescriptors ();
      printPropertyValues(pRequest, pResponse, pOut, "", mService, pds);
    }
    catch (IntrospectionException exc) {
      pOut.println(getResourceString("serviceAdminServletNoBeanInfo") +
                    exc.toString ());
    }


    if (mService instanceof LoggingPropertied) {
      pOut.println (getResourceString("serviceAdminServletLoggingPropertiesHdr"));

      ApplicationLogging logging =
        ((LoggingPropertied)mService).getLogging();
    
      Class clLogging = logging.getClass ();
      try {
        BeanInfo info = Introspector.getBeanInfo (clLogging);
        PropertyDescriptor [] pds = info.getPropertyDescriptors ();
        printPropertyValues(pRequest, pResponse, pOut, 
                            "logging.", logging, pds);
      }
      catch (IntrospectionException exc) {
        pOut.println(getResourceString("serviceAdminServletNoBeanInfo") +
                     exc.toString ());
      }
    }
    
  }

  

  //-------------------------------------
  /**
   * Prints a listing of all the service's properties and values.
   *
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pOut the output stream to write to
   * @param pPropertyPrefix the property prefix path, typically
   *    the path usd to get to pObject with a "." appended.
   * @param pObject the object whose property values to print out.
   * @param pPropDescs the array of property descriptors.
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void printPropertyValues (HttpServletRequest pRequest,
                                      HttpServletResponse pResponse,
                                      ServletOutputStream pOut,
                                      String pPropertyPrefix,
                                      Object pObject,
                                      PropertyDescriptor [] pPropDescs)
       throws ServletException, IOException
  {
    pOut.println ("<table border>");
    pOut.println(getResourceString("serviceAdminServletPropertiesTableHdr"));

    String serviceName = formatServiceName (pRequest.getPathInfo (),
                                            pRequest);      

    PropertyDescriptor [] pds = pPropDescs;
    pds = sortPropertyDescriptors (pds);
    for (int i = 0; i < pds.length; i++) {
      PropertyDescriptor pd = pds [i];
      String name = pd.getName ();
      Class type = pd.getPropertyType ();
      Method m = pd.getReadMethod ();
      
      // added check to filter out getting java.sql.Connection properties,
      // since it can cause a connection leak
      if ((m != null) && !java.sql.Connection.class.isAssignableFrom(type)) {
        try {
          Object val = m.invoke (pObject, NULL_ARGS);
          pOut.print ("<tr class=\"" +
                      (((i % 2) == 0) ? "even" : "odd") + 
                      "\"><td>");
          pOut.print ("<a href=\"" +
                      pResponse.encodeURL(
                        serviceName +
                        "?propertyName=" + pPropertyPrefix + name) +
                      "\">" +
                      name +
                      "</a>");
          pOut.print ("</td><td>");
          pOut.print (formatObject (val, pRequest));
          pOut.print ("</td><td>");
          pOut.print (type.getName ());
          pOut.println ("</td></tr>");
        }
        catch (IllegalAccessException exc) {}
        catch (IllegalArgumentException exc) {}
        catch (InvocationTargetException exc) {}
      }
    }

    pOut.println ("</table>");
  }



  //-------------------------------------
  @SuppressWarnings("unchecked")
  /**
   * Prints a listing of all the service's properties and values.
   *
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pOut the output stream to write to
   * @param pObject the collection object to print out
   * @param pPrefix the property prefix path, typically
   *    the path usd to get to pObject with a "." appended.
   * @return true if output was printed, false otherwise
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected boolean printCollectionValues (HttpServletRequest pRequest,
                                           HttpServletResponse pResponse,
                                           ServletOutputStream pOut,
                                           Object pObject,
                                           String pPrefix)
       throws ServletException, IOException
  {
    if (pObject == null) {
      return false;
    }

    try {
      Object obj = pObject;
      boolean allowLinking = true;

      if (obj.getClass().isArray()) {
        int len = Array.getLength(obj);
        List list = new ArrayList(len);
        for (int j = 0; j < len; j++) {
          list.add(Array.get(obj, j));
        }
        obj = list;
      }
      else if (obj instanceof Enumeration) {
        allowLinking = false;
        List list = new ArrayList();
        Enumeration enumCur = (Enumeration)obj;
        while (enumCur.hasMoreElements()) {
          list.add(enumCur.nextElement());
        }
        obj = list;
      }
      else if (obj instanceof Iterator) {
        allowLinking = false;
        List list = new ArrayList();
        Iterator iter = (Iterator)obj;
        while (iter.hasNext()) {
          list.add(iter.next());
        }
        obj = list;
      }
      else if (obj instanceof Vector) {
        Vector vecCur = (Vector)obj;
        int len = vecCur.size();
        ArrayList list = new ArrayList(len);
        for (int j = 0; j < len; j++) {
          list.add(vecCur.elementAt(j));
        }
        obj = list;
      }

      if (obj instanceof Collection) {

        pOut.println (getResourceString("serviceAdminServletCollectionHdr")); 
        // make a map whose keys are index ints and values. We
        // want an ordered map, so that the properties come out
        // in numeric order.
        Map<String,Object> map = new LinkedHashMap<String,Object>();
        int i = 0;
        for (Object objCur: ((Collection)obj)) {
          map.put("[" + Integer.toString(i++) + "]", objCur);
        }
        String strPrefix = pPrefix;
        if (strPrefix.endsWith(".")) {
          strPrefix = strPrefix.substring(0, strPrefix.length() - 1);
        }
        DynamicBeanInfo info = DynamicBeans.getBeanInfo (map);
        DynamicPropertyDescriptor[] pds = info.getPropertyDescriptors ();
        printDynamicPropertyValues(pRequest, pResponse, pOut, strPrefix, map, pds,
                                   false, allowLinking); 
      }
      return true;
    }
    catch (IntrospectionException exc) {
      pOut.println(getResourceString("serviceAdminServletNoBeanInfo") +
                    exc.toString ());
    }
    return false;
  }
  


  //-------------------------------------
  /**
   * Prints a listing of all the service's properties and values.
   *
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pOut the output stream to write to
   * @param pObject the object whose dynamic properties to print
   * @param pPrefix the property prefix path, typically
   *    the path usd to get to pObject with a "." appended.
   * @param pIncludeCollection whether to include collection properties.
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void printDynamicPropertyValues (HttpServletRequest pRequest,
                                             HttpServletResponse pResponse,
                                             ServletOutputStream pOut,
                                             Object pObject,
                                             String pPrefix,
                                             boolean pIncludeCollection)
       throws ServletException, IOException
  {
    if (pObject == null) {
      return;
    }

    pOut.println (getResourceString("serviceAdminServletPropertiesHdr"));

    try {
      DynamicBeanInfo info = DynamicBeans.getBeanInfo (pObject);
      DynamicPropertyDescriptor[] pds = info.getPropertyDescriptors ();

      printDynamicPropertyValues(pRequest, pResponse, pOut, pPrefix, pObject, pds, true, true);

      if (pIncludeCollection) {
        printCollectionValues(pRequest, pResponse,
                              pOut, pObject, pPrefix);
      }
    }
    catch (IntrospectionException exc) {
      pOut.println(getResourceString("serviceAdminServletNoBeanInfo") +
                    exc.toString ());
    }
  }
  


  //-------------------------------------
  /**
   * Prints a listing of all the service's properties and values.
   *
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pOut the output stream to write to
   * @param pPropertyPrefix the property prefix path, typically
   *    the path usd to get to pObject with a "." appended.
   * @param pObject the object whose dynamic property values to print
   * @param pPropDescs the array of dynamic property descriptors
   * @param pSortProperties whethers the properties should be sorted
   * @param pAllowLinks whether to allow links when printing property values
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void printDynamicPropertyValues (HttpServletRequest pRequest,
                                             HttpServletResponse pResponse,
                                             ServletOutputStream pOut,
                                             String pPropertyPrefix,
                                             Object pObject,
                                             DynamicPropertyDescriptor [] pPropDescs,
                                             boolean pSortProperties,
                                             boolean pAllowLinks)
       throws ServletException, IOException
  {
    pOut.println ("<table border>");
    pOut.println(getResourceString("serviceAdminServletPropertiesTableHdr"));

    String serviceName = formatServiceName (pRequest.getPathInfo (),
                                            pRequest);      

    DynamicPropertyDescriptor [] pds = pPropDescs;
    if (pSortProperties) {
      pds = new DynamicPropertyDescriptor[pPropDescs.length];
      sortFeatureDescriptors(pPropDescs, pds);
    }

    for (int i = 0; i < pds.length; i++) {
      DynamicPropertyDescriptor pd = pds [i];
      String name = (pd != null) ? pd.getName () : null;
      Class type = (pd != null) ? pd.getPropertyType() : null;
      
      // added check to filter out getting java.sql.Connection properties,
      // since it can cause a connection leak
      if (pd != null && pd.isReadable() && !java.sql.Connection.class.isAssignableFrom(type)) {
        Object val;
        try {
          val = getDynamicPropertyValue(pObject, pd.getName(), pPropertyPrefix);
        }
        catch (PropertyNotFoundException exc) {
          continue;
        }
        catch (RuntimeException e) {
          val = "<i>" + getResourceString("serviceAdminServletExceptionGettingValue") + e + "</i>";
        }

        pOut.print ("<tr class=\"" +
                    (((i % 2) == 0) ? "even" : "odd") + 
                    "\"><td>");
        if (!allowPropertyValueLink(pAllowLinks, pObject, pd.getName(), pPropertyPrefix)) {
          pOut.print(name);
        }
        else {
          pOut.print ("<a href=\"" +
                      pResponse.encodeURL(
                        serviceName +
                        "?propertyName=" + pPropertyPrefix + name) +
                      "\">" +
                      name +
                      "</a>");

        }

        pOut.print ("</td><td>");
        pOut.print (formatObject (val, pRequest));
        pOut.print ("</td><td>");
        pOut.print (type.getName ());
        pOut.println ("</td></tr>");
      }
    }

    pOut.println ("</table>");
  }
  
  //----------------------------------------
  /**
   * get a dynamic property value. break this out into
   * its own method to allow subclasses to modify this
   * behavior if the standard algorithm for getting a property
   * value will choke on the property name, which can be the
   * case if the property name contains periods, but shouldn't
   * be interpreted as dot notation
   * @param pObject the object containing the dynamic property values
   * @param pPropertyName the name of the property
   * @param pPropertyPrefix the property prefix
   * @return the property value
   * @exception PropertyNotFoundException if there was an error finding
   * the property value
   */
  protected Object getDynamicPropertyValue(Object pObject, 
                                           String pPropertyName,
                                           String pPropertyPrefix)
    throws PropertyNotFoundException
  {
    return DynamicBeans.getSubPropertyValue (pObject, pPropertyName);
  }

  //----------------------------------------
  /**
   * determine whether the given property value should
   * have an html link
   * @param pAllowLinks the current value for allowing links or not
   * @param pObject the property value object
   * @param pPropertyName the property name
   * @param pPropertyPrefix the property name prefix
   * @return true if we should allow links for this property value,
   * false if not
   */
  protected boolean allowPropertyValueLink(boolean pAllowLinks,
                                           Object pObject,
                                           String pPropertyName,
                                           String pPropertyPrefix)
  {
    return pAllowLinks;
  }

  //-------------------------------------
  @SuppressWarnings("unchecked")
  /**
   * Returns the property descriptor for the given property of the
   * given class, or null if none is found.
   *
   * @param pClass the whole whose property descriptor to get
   * @param pPropertyName the name of the property to get
   * @return the found property descriptor, or null
   * @exception IntrospectionException if an error occurs
   */
  public PropertyDescriptor getPropertyDescriptor (Class pClass,
                                                   String pPropertyName)
       throws IntrospectionException
  {
    return BeanUtils.getPropertyDescriptor(pClass, pPropertyName);
  }

  //-------------------------------------
  /**
   * Prints the formatted information for a verification screen
   * before invoking a method
   *   
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pMethodName the name of the method whose invocation should be
   * printed.
   * @exception ServletException if an error occurred while processing
   * the servlet request
   * @exception IOException if an error occurred while reading or writing
   * the servlet request
   *
   */
  public void printMethodInvocationVerification (HttpServletRequest pRequest,
                                                 HttpServletResponse pResponse,
                                                 String pMethodName)
    throws ServletException, IOException
  {
    ServletOutputStream out = pResponse.getOutputStream ();
    out.println ("<html><title>");
    printHeaderTitle (pRequest, pResponse, out);
    out.println ("</title>");
    insertStyle(pRequest, pResponse, out);
    printBodyTag (pRequest, pResponse, out);

    printTitle (pRequest, pResponse, out);

    Object[] args = {pMethodName};
    out.println(getResourceString("serviceAdminServletInvokeMethodHdr",args));

    out.print ("<form action=\"");
    out.print (formatServiceName (pRequest.getPathInfo (),
                                  pRequest));
    out.println ("\" method=POST>");
    out.print ("<input type=hidden name=\"invokeMethod\" value=\"");
    out.print (pMethodName);
    out.println ("\">");
    out.println ("<input type=submit name=submit value=\"" +
                 getResourceString("serviceAdminServletInvokeMethodButton") +
                 "\">");
    out.println ("<input type=submit name=cancelMethodInvocation value=\"" +
                 getResourceString("serviceAdminServletCancelButton") +
                 "\">");
    out.println("</form>");
  }

  //-------------------------------------
  /**
   * Prints the formatted information of a method invocation on a service
   *   
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pMethodName the name of the method whose invocation link to print.
   * @exception ServletException if an error occurred while processing
   * the servlet request
   * @exception IOException if an error occurred while reading or writing
   * the servlet request
   *
   */
  public void printMethodInvocation (HttpServletRequest pRequest,
                                     HttpServletResponse pResponse,
                                     String pMethodName)
    throws ServletException, IOException
  {
    ServletOutputStream out = pResponse.getOutputStream ();
    out.println ("<html><title>");
    printHeaderTitle (pRequest, pResponse, out);
    out.println ("</title>");
    insertStyle(pRequest, pResponse, out);    
    printBodyTag (pRequest, pResponse, out);

    printTitle (pRequest, pResponse, out);

    Object[] args = {pMethodName};
    out.println(getResourceString("serviceAdminServletMethodInvokedHdr",
                                  args));

    Object val = null;
    try {
        Object target = mService;
        if (mResolvedService != null)
          target = mResolvedService;
        Method method = target.getClass().getMethod(pMethodName, 
                                                      NULL_PARAMETER_TYPES);
        val = method.invoke(target, NULL_ARGS);
    }
    catch (InvocationTargetException exc) {

      handleInvocationException(pRequest, pResponse, pMethodName, exc);
      return;
    }
    catch (Throwable exc) {
      handleInvocationException(pRequest, pResponse, pMethodName, exc);
      return;
    }

    printMethodInvocationResult(pRequest, pResponse, pMethodName, val);

  }


  /**
   * Print out the invocation method result. By default, a
   * table containing the returned value and class.
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pMethodName the name of the method whose result to print
   * @param pResult the result value to print
   * @exception IOException if an error occurs
   */
  protected void printMethodInvocationResult(HttpServletRequest pRequest,
                                             HttpServletResponse pResponse,
                                             String pMethodName,
                                             Object pResult) throws IOException {

    ServletOutputStream out = pResponse.getOutputStream ();    
    out.println(getResourceString("serviceAdminServletReturnedObjectHdr"));
    out.println ("<table border>");
    out.println(getResourceString("serviceAdminServletReturnedObjectTableHdr"));
    out.print ("<tr><td>");
    if (pResult != null) {
      out.print (formatObject (pResult, pRequest));
      out.print ("</td><td>");
      out.print (pResult.getClass().getName());
    }
    else {
      out.print("null</td><td>null");
    }
    out.println ("</td></tr>");    
    out.println ("</table>");
  }


  /**
   * Handle an invocation exception. By default, just print out
   * a stack trace.
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pMethodName the name of the method that threw the exception
   * @param pException the exception that was thrown
   * @exception IOException if an error occurs
   */
  protected void handleInvocationException(HttpServletRequest pRequest,
                                           HttpServletResponse pResponse,
                                           String pMethodName,
                                           Throwable pException) throws IOException {

    ServletOutputStream out = pResponse.getOutputStream ();

    out.println(getResourceString("serviceAdminServletInvocationFailureHdr"));
    out.println("<PRE>");
    ServletUtil.printStackTrace(pException, out);
    out.println("</PRE>");
  }


  /** Find the configuration for the specified component, if any. */
  Configuration findConfiguration(String pPath) {
    if (pPath.endsWith("/")) {
      pPath = pPath.substring(pPath.length() - 1);
    }
    int idxLastSlash = pPath.lastIndexOf("/");
    String strParent = "/";
    String strName = pPath;
    if (idxLastSlash != -1) {
      strParent = pPath.substring(0, idxLastSlash);
      strName = pPath.substring(idxLastSlash + 1);
    }

    // get the dictionary of configurations for this component
    java.util.Dictionary dictConfiguration =
      mNucleus.getConfigurations(strParent);
    if (dictConfiguration != null) {
      Configuration config = (Configuration)dictConfiguration.get(strName);
      if (config instanceof PropertyConfiguration) {
        String strServiceName = ((PropertyConfiguration)config).getServiceName();
        if (pPath.equals(strServiceName)) {
          return config;
        }
      }
    }

    // okay, we didn't find it. Let's go looking for a scope manager.
    String strScopeRelative = getScopeRelativePath(pPath);
    if ((strScopeRelative != null) &&
        !strScopeRelative.equals(pPath)) {
      return findConfiguration(strScopeRelative);
    }

    
    return null;
  }

  /**
   * Return the scope relative path for a given component.
   * If the component is not the child of a scope manager,
   * returns null.
   * @param pPath the path to calculate relative to the current scope.
   * @return the scope-reltaive (rather than the absolute) path.
   */
  public String getScopeRelativePath(String pPath) {
    if (pPath == null) {
      return null;
    }

    String strCur = pPath;
    while (strCur.length() > 2) {
      if (strCur != pPath) {
        // skip the first time through to avoid
        // resolving in wrong scope warning
        Object objParent = mNucleus.resolveName(strCur);
        if (objParent instanceof ScopeManager) {
          int idxNext = pPath.indexOf("/", strCur.length() + 2);
          if (idxNext != -1) {
            String strNewServiceName = pPath.substring(idxNext);
            return strNewServiceName;
          }
        }
      }

      int idxLastSlash = strCur.lastIndexOf("/");
      if (idxLastSlash == -1) {
        strCur = "";
      }
      else {
        strCur = strCur.substring(0, idxLastSlash);
      }
    }
    return null;
  }

  //-------------------------------------
  /**
   * Prints the formatted information for a service, including the name,
   * directory listing, and properties.
   *   
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pPropertyName the name of the property to print
   * @exception ServletException if an error occurred while processing
   * the servlet request
   * @exception IOException if an error occurred while reading or writing
   * the servlet request
   *
   */
  public void printProperty (HttpServletRequest pRequest,
                             HttpServletResponse pResponse,
                             String pPropertyName)
    throws ServletException, IOException
  {
    String serviceName = pRequest.getPathInfo ();
    if (serviceName == null) 
      serviceName = "";
    if (serviceName.endsWith ("/"))
      serviceName = serviceName.substring (0, serviceName.length () - 1);

    ServletOutputStream out = pResponse.getOutputStream ();
    out.println ("<html><title>");
    printHeaderTitle (pRequest, pResponse, out);
    out.println ("</title>");
    insertStyle(pRequest, pResponse, out); 
    printBodyTag (pRequest, pResponse, out);

    printTitle (pRequest, pResponse, out);

    Object objParent;
    String strPropertyName;
    PropertyDescriptor pd = null;
    DynamicPropertyDescriptor  dpd = null;
    
    int idxPeriod = pPropertyName.lastIndexOf(".");
    int idxBracket = pPropertyName.lastIndexOf("[");
    if ((idxPeriod == -1) && (idxBracket == -1)) {
      objParent = mService;
      strPropertyName = pPropertyName;
      try {      
        pd = getPropertyDescriptor (objParent.getClass (),
                                    strPropertyName);
        if (pd != null) {
          dpd = new DynamicPropertyDescriptor(pd);
        }
      }
      catch (IntrospectionException e) {
        throw new ServletException(e);
      }
    }
    else {
      // sub-property, so use dynamic beans
      try {      
        if ((idxPeriod != -1) && !pPropertyName.startsWith("serviceConfiguration.")) {
          // don't try to resolve serviceConfiguration sub-properties, just in case, if
          // we don't have a real serviceConfiguration
          String strParentProp = pPropertyName.substring(0, idxPeriod);
          objParent = DynamicBeans.getSubPropertyValue(mService, strParentProp);
          strPropertyName = pPropertyName.substring(idxPeriod + 1);
        }
        else {
          // actually the sub-property name... since we don't have a parent prop,
          // don't bother
          strPropertyName = null;
          objParent = null;
        }
        if (!pPropertyName.startsWith("serviceConfiguration.")) {
          dpd = DynamicBeans.getSubPropertyDescriptor(mService, pPropertyName);
        }
      }
      catch (PropertyNotFoundException e) {
        throw new ServletException(e);
      }
      catch (IntrospectionException e) {
        throw new ServletException(e);
      }
    }


    Object[] args = {pPropertyName};
    out.println (getResourceString("serviceAdminServletPropertyHdr", args));

    try {

      if ((dpd != null) && dpd.isWritable()) {
        String newValue = pRequest.getParameter ("newValue");
        
        if (pd != null) {
          // Get the write method and set the new value if there is one
          if (newValue != null) {
            Properties p = new Properties ();
            p.put (strPropertyName, newValue);
            BeanConfigurator bc = mNucleus.getBeanConfigurator ();
            NameResolver nr = null;
            if (objParent instanceof NameContext) {
              nr = new NucleusNameResolver (mNucleus,
                                            (NameContext) objParent,
                                            true);
            }
            else {
              // JoeB 7/8/98: If this service isn't a name context, just resolve
              // absolute pathnames.  That's better than bailing.
              nr = new NucleusNameResolver (mNucleus,
                                            mNucleus,
                                            true);
            }

            CollectingLoggingSupport loggerCollecting =
              new CollectingLoggingSupport(mNucleus);

            loggerCollecting.setCollectingLogErrors(true);
            bc.configureBean (pRequest.getPathInfo (),
                              objParent, p, p, nr, loggerCollecting);

            if (loggerCollecting.getCollectedErrorsCount() > 0) {
              String strError = getPropertyConfigurationErrorString(
                pRequest, pResponse, pPropertyName, newValue, loggerCollecting);

              if (strError != null) {
                if (pResponse.isCommitted()) {
                  out.print(strError);
                }
                else {
                  pResponse.reset();
                  pResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                      strError);
                  return;
                }
              }
            }
          }
        }
        else {
          if (newValue != null) {
            // not a traditional property descriptor, but still writable
            try {
              DynamicBeans.setSubPropertyValueFromString(mService, pPropertyName, newValue);
            }
            catch (IllegalArgumentException e) {
              // fall back to trying to resolve to a Nucleus component, if possible.
              Object objFound;
              if (pRequest instanceof NameResolver) {
                objFound = ((NameResolver)pRequest).resolveName(newValue);
              }
              else {
                objFound = mNucleus.resolveName(newValue);
              }

              if (objFound != null) {
                try {
                  DynamicBeans.setSubPropertyValue(mService, pPropertyName, objFound); 
                
                } catch (PropertyNotFoundException exc) {
                  if (pResponse.isCommitted()) {
                    out.print(exc.getMessage());
                  }
                  else {
                    pResponse.reset();
                    pResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                        exc.getMessage());
                    return;
                  }
                }
              }
              else {
                throw e;
              }
            }
            catch (PropertyNotFoundException exc) {
              if (pResponse.isCommitted()) {
                out.print(exc.getMessage());
              }
              else {
                pResponse.reset();
                pResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                    exc.getMessage());
                return;
              }
            }
          }
        }
      }

      if (dpd != null) {
	out.println ("<table border>");
        out.println ("<tr class=\"even\"><td>" +
                     getResourceString("serviceAdminServletDisplayName") +
                     "</td><td>" +
                     dpd.getDisplayName () +
                     "</td></tr>");
        out.println ("<tr class=\"odd\"><td>" +
                     getResourceString("serviceAdminServletExpert") +
                     "</td><td>" +
                     dpd.isExpert () +
                     "</td></tr>");
        out.println ("<tr class=\"even\"><td>" +
                     getResourceString("serviceAdminServletHidden") +
                     "</td><td>" +
                     dpd.isHidden () +
                    "</td></tr>");
        out.println ("<tr class=\"odd\"><td>" +
                     getResourceString("serviceAdminServletPropertyType") +
                     "</td><td>" +
                     dpd.getPropertyType () +
                     "</td></tr>");
        out.println ("</table>");
      }

      if (dpd != null) {
        out.println (getResourceString("serviceAdminServletShortDescrHdr"));
        if (dpd != null) {
          out.println (dpd.getShortDescription ());
        }
      }

      
      Object val = null;
      try {
        Method m = null;
        if (pd != null)
          m = pd.getReadMethod ();
        if (m != null) {
          val = m.invoke (objParent, new Object [0]);
        }
        else if (pPropertyName.equals("serviceConfiguration")) {
          val = findConfiguration(serviceName);
        }
        else if (pPropertyName.startsWith("serviceConfiguration.")) {
          val = findConfiguration(serviceName);          
          if (val != null) {
            String strRest = pPropertyName.substring(pPropertyName.indexOf(".") + 1);
            val = DynamicBeans.getSubPropertyValue(val, strRest);
          }
        }
        else {
          val = DynamicBeans.getSubPropertyValue(mService, pPropertyName);
        }
        
        out.println (getResourceString("serviceAdminServletValueHdr"));
        out.println (formatLongObject (val, pRequest));
      }
      catch (IllegalAccessException exc) {}
      catch (IllegalArgumentException exc) {}
      catch (InvocationTargetException exc) {}
      catch (PropertyNotFoundException exc) {
        out.println(exc.getMessage());
      }

      if (dpd != null) {
        if (dpd.isWritable()) {
          out.println (getResourceString("serviceAdminServletNewValueHdr"));
          out.print ("<form action=\"");
          out.print (formatServiceName (pRequest.getPathInfo (),
                                        pRequest));
          out.println ("\" method=POST>");
          out.print ("<input type=hidden name=\"propertyName\" value=\"");
          out.print (pPropertyName);
          out.println ("\">");
          if (val instanceof Boolean)
            printTrueFalseSelection(out, (Boolean)val);
          else {
            out.println ("<textarea rows=3 cols=40 name=\"newValue\">");
            out.println ("</textarea>");
          }
          out.print ("<p><input type=submit name=\"change\"");
          out.println (" value=\"" +
                       getResourceString("serviceAdminServletChangeValueButton") +
                       "\">");
          out.println(getResourceString("serviceAdminServletChangeValueNote"));
          out.println ("</form>");
        }
      }
      
      if (val != null) {
        String strPath = getAbsoluteNameOf(val);
        if ((strPath == null) &&
            (!val.getClass().isPrimitive()) && !(val instanceof String)) {
          out.println("<p>");
          printDynamicPropertyValues(pRequest, pResponse, out, val, pPropertyName + ".",
                                     true);
        }
      }
    }
//    catch (IntrospectionException exc) {
//      out.println(getResourceString("serviceAdminServletNoBeanInfo") +
//                   exc.toString ());
//    }
    finally {
      
    }


    out.println ("</body></html>");
  }


  @SuppressWarnings("unchecked")
  /**
   * Return a string to represent property configuration errors.
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pPropertyName the name of the property that was misconfigured
   * @param pNewValue the new value
   * @param pLoggerCollecting the error collecting logger.
   * @return the formatted error string
   */
  protected String getPropertyConfigurationErrorString(
    HttpServletRequest pRequest,
    HttpServletResponse pResponse,
    String pPropertyName,
    String pNewValue,
    CollectingLoggingSupport pLoggerCollecting) {

    Enumeration enumErrors = pLoggerCollecting.getCollectedErrors();

    if (enumErrors != null) {
      StringBuffer strbuf = new StringBuffer();
      
      strbuf.append(
        getResourceString("serviceAdminServletSetPropertyFailureHdr"));
      
      strbuf.append("<PRE>");
	  
      while (enumErrors.hasMoreElements()) {
        LogEvent event = (LogEvent)enumErrors.nextElement();

        if (event.getMessage() != null) {
          strbuf.append(event.getMessage());
          strbuf.append("\n\n");

        }
        if (event.getThrowable() != null) {
          StackTraceUtils.buildCroppedStackTrace(event.getThrowable(),
                                                 strbuf, 30, -1);
          strbuf.append("\n\n");
        }

        return strbuf.toString();
      }
	      
      strbuf.append("</PRE>");

      return strbuf.toString();      
    }


    return null;
  }

  void printTrueFalseSelection(ServletOutputStream pOut, Boolean pValue)
       throws ServletException, IOException
  {    
    if (pValue != null)
      printTrueFalseSelection(pOut, pValue.booleanValue());
    else
      printTrueFalseSelection(pOut, false);
  }

  void printTrueFalseSelection(ServletOutputStream pOut, boolean pValue)
       throws ServletException, IOException
  {
    if (pValue) {
      pOut.println("<input type=radio name=\"newValue\" value=\"true\" checked>" +
                   getResourceString("serviceAdminServletTrue") + "<BR>");
      pOut.println("<input type=radio name=\"newValue\" value=\"false\">" +
                   getResourceString("serviceAdminServletFalse"));
    }
    else {
      pOut.println("<input type=radio name=\"newValue\" value=\"true\">" +
                   getResourceString("serviceAdminServletTrue") + "<BR>");
      pOut.println("<input type=radio name=\"newValue\" value=\"false\" checked>" +
                   getResourceString("serviceAdminServletFalse"));
    }
  }

  //-------------------------------------
  /**
   * Sorts the list of eventSet descriptors by eventSet name and
   * returns a new list of the sorted descriptors.
   *
   * @param pDescriptors the descriptors to sort
   * @return the sorted descriptors
   */
  protected EventSetDescriptor [] 
  sortEventSetDescriptors (EventSetDescriptor [] pDescriptors)
  {
    EventSetDescriptor [] ret = new EventSetDescriptor [pDescriptors.length];
    sortFeatureDescriptors(pDescriptors, ret);
    return ret;
  }

  //-------------------------------------
  @SuppressWarnings("unchecked")
  /**
   * Prints a listing of all the service's event sets
   *
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pOut the output stream to write to
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void printEventSets (HttpServletRequest pRequest,
                                 HttpServletResponse pResponse,
                                 ServletOutputStream pOut)
       throws ServletException, IOException
  {
    pOut.println (getResourceString("serviceAdminServletEventSetsHdr"));

    Class cl = mService.getClass ();
    try {
      BeanInfo info = Introspector.getBeanInfo (cl);

      pOut.println ("<table border>");
      pOut.println(getResourceString("serviceAdminServletEventSetsTableHdr"));

      String serviceName = formatServiceName (pRequest.getPathInfo (),
                                              pRequest);

      EventSetDescriptor [] eds = info.getEventSetDescriptors ();
      eds = sortEventSetDescriptors (eds);
      ClassPropertyValueFormatter f = new ClassPropertyValueFormatter ();
      for (int i = 0; i < eds.length; i++) {

        EventSetDescriptor ed = eds [i];
        String name = ed.getName ();
        Class type = ed.getListenerType ();

        pOut.print ("<tr class=\"" +
                    (((i % 2) == 0) ? "even" : "odd") +
                    "\"><td>");
        pOut.print ("<a href=\"" +
                    pResponse.encodeURL(
                      serviceName +
                      "?eventSetName=" +
                      name) +
                    "\">" +
                    name +
                    "</a>");
        pOut.print ("</td><td>");
        pOut.print (f.formatPropertyValue (type, pRequest, f));
        pOut.println ("</td></tr>");
      }
      pOut.println ("</table>");
    }
    catch (IntrospectionException exc) {
      pOut.println(getResourceString("serviceAdminServletNoBeanInfo") +
                   exc.toString ());
    }
  }

  //-------------------------------------
  /**
   *
   * Returns the eventSet descriptor for the given eventSet of the
   * given class, or null if none is found.
   **/
  EventSetDescriptor getEventSetDescriptor (Class pClass,
                                            String pEventSetName)
       throws IntrospectionException
  {
    BeanInfo info = Introspector.getBeanInfo (pClass);
    EventSetDescriptor [] pds = info.getEventSetDescriptors ();
    //unused EventSetDescriptor pd = null;
    for (int i = 0; i < pds.length; i++) {
      if (pds [i].getName ().equals (pEventSetName)) {
        return pds [i];
      }
    }
    return null;
  }

  //-------------------------------------
  /**
   * Prints an event set
   *   
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pEventSetName the name of the event set to print
   * @exception ServletException if an error occurred while processing
   * the servlet request
   * @exception IOException if an error occurred while reading or writing
   * the servlet request
   *
   */
  public void printEventSet (HttpServletRequest pRequest,
                             HttpServletResponse pResponse,
                             String pEventSetName)
    throws ServletException, IOException
  {
    ServletOutputStream out = pResponse.getOutputStream ();
    out.println ("<html><title>");
    printHeaderTitle (pRequest, pResponse, out);
    out.println ("</title>");
    insertStyle(pRequest, pResponse, out);     
    printBodyTag (pRequest, pResponse, out);

    printTitle (pRequest, pResponse, out);

    Object[] strArgs = {pEventSetName};
    out.println (getResourceString("serviceAdminServletEventSetHdr",
                                   strArgs));
    try {
      EventSetDescriptor ed = getEventSetDescriptor (mService.getClass (),
                                                     pEventSetName);
      if (ed != null) {
        // See if a service was added or removed
        String listenerName = pRequest.getParameter ("listenerName");
        if (listenerName != null) {
          boolean add = pRequest.getParameter ("add") != null;
          if (add)
            out.println(getResourceString("serviceAdminServletAddingListenerHdr"));
          else
            out.println(getResourceString("serviceAdminServletRemovingListenerHdr"));
          String absoluteName = 
            formatServiceName (pRequest.getPathInfo (), "/../" + listenerName,
                               pRequest);

          Object listener = mNucleus.resolveName (absoluteName, 
                                                  mNucleus,
                                                  true);
          if (listener != null) {
            if (ed.getListenerType ().isAssignableFrom 
                (listener.getClass ())) {
              Method m = add ? 
                ed.getAddListenerMethod ():
                ed.getRemoveListenerMethod ();
              if (m != null) {
                Object [] args = { listener };
                try {
                  m.invoke (mService, args);
                  if (add)
                    out.println(getResourceString("serviceAdminServletSuccessAdded"));
                  else
                    out.println(getResourceString("serviceAdminServletSuccessRemoved"));
                }
                catch (IllegalAccessException exc) {
                  out.println(getResourceString("serviceAdminServletListenerUnableToAdd") +
                              exc);
                }
                catch (IllegalArgumentException exc) {
                  out.println(getResourceString("serviceAdminServletListenerUnableToAdd") +
                              exc);
                }
                catch (InvocationTargetException exc) {
                  out.println(getResourceString("serviceAdminServletListenerUnableToAdd") +
                              exc.getTargetException());
                }
              }
              else {
                Object[] txtArgs = {ed.getListenerType().getName()};
                if (add)
                  out.println(getResourceString("serviceAdminServletNoAdd",
                                                txtArgs));
                else
                  out.println(getResourceString("serviceAdminServletNoRemove",
                                                txtArgs));
              }
            }
            else {
              Object[] txtArgs = {listenerName,
                                  listener.getClass().getName(),
                                  ed.getListenerType().getName()};
              out.println(getResourceString("serviceAdminServletNotAssignable",
                                            txtArgs));
            }
          }
          else {
            out.println(getResourceString("serviceAdminServletCannotFind") +
                        listenerName);
          }
        }

        // Display general bean feature properties
        out.println (getResourceString("serviceAdminServletGeneralEventSetHdr"));
        out.println ("<table border>");
        out.println ("<tr class=\"odd\"><td>" +
                     getResourceString("serviceAdminServletDisplayName") +
                     "</td><td>" +
                     ed.getDisplayName () +
                     "</td></tr>");
        out.println ("<tr class=\"even\"><td>" +
                     getResourceString("serviceAdminServletExpert") +
                     "</td><td>" +
                     ed.isExpert () +
                     "</td></tr>");
        out.println ("<tr class=\"odd\"><td>" +
                     getResourceString("serviceAdminServletHidden") +
                     "</td><td>" +
                     ed.isHidden () +
                     "</td></tr>");
        ClassPropertyValueFormatter f = new ClassPropertyValueFormatter ();
        out.println ("<tr  class=\"even\"><td>" +
                     getResourceString("serviceAdminServletListenerType") +
                     "</td><td>" +
                     f.formatPropertyValue (ed.getListenerType (), 
                                            pRequest, 
                                            f) +
                     "</td></tr>");
        out.println ("<tr  class=\"odd\"><td>" +
                     getResourceString("serviceAdminServletDefaultEventSet") +
                     "</td><td>" +
                     ed.isInDefaultEventSet () +
                     "</td></tr>");
        out.println ("<tr class=\"even\"><td>" +
                     getResourceString("serviceAdminServletUnicast") +
                     "</td><td>" +
                     ed.isUnicast () +
                     "</td></tr>");
        out.println ("</table>");
        out.println (getResourceString("serviceAdminServletShortDescrHdr"));
        out.println (ed.getShortDescription ());

        // Display the listeners
        out.println (getResourceString("serviceAdminServletListenersHdr"));
        String propertyName = pEventSetName + "Listeners";
        PropertyDescriptor pd = getPropertyDescriptor (mService.getClass (),
                                                       propertyName);
        Method m = null;
        if (pd != null) m = pd.getReadMethod ();
        Object [] listeners = null;
        if (m != null) {
          try {
            Object val = m.invoke (mService, new Object [0]);
            if (val instanceof Object []) listeners = (Object []) val;
          }
          catch (IllegalAccessException exc) {}
          catch (IllegalArgumentException exc) {}
          catch (InvocationTargetException exc) {}
        }
        if (listeners != null) {
          out.println ("<table border>");
          for (int i = 0; i < listeners.length; i++) {
            
            out.println ("<tr class=\"" +
                         (((i % 2) == 0) ? "even" : "odd") +                          
                         "\"><td>" + 
                          formatObject (listeners [i], pRequest) +
                          "</td></tr>");
          }
          out.println ("</table>");
        }
        else {
          Object[] args = {propertyName, ed.getListenerType().getName()};
          out.println(getResourceString("serviceAdminServletNoProperty",
                                        args));
        }

        // Provide a form to add or remove listeners
        out.println(getResourceString("serviceAdminServletAddOrRemoveHdr"));
        out.print ("<form action=\"");
        out.print (formatServiceName (pRequest.getPathInfo (),
                                      pRequest));
        out.println ("\" method=POST>");
        out.print ("<input type=hidden name=\"eventSetName\" value=\"");
        out.print (pEventSetName);
        out.println ("\">");
        out.println (getResourceString("serviceAdminServletEnterName"));
        out.println ("<p><input type=text size=40 name=\"listenerName\">");
        out.print ("<p><input type=submit name=\"add\"");
        out.println (" value=\"" +
                     getResourceString("serviceAdminServletAddButton") +
                     "\">");
        out.print ("<input type=submit name=\"remove\"");
        out.println (" value=\"" +
                     getResourceString("serviceAdminServletRemoveButton") +
                     "\">");
        out.println(getResourceString("serviceAdminServletChangeValueNote"));
        out.println ("</form>");
      }
    }
    catch (IntrospectionException exc) {
      out.println (getResourceString("serviceAdminServletNoBeanInfo") +
                   exc.toString ());
    }

    out.println ("</body></html>");
  }

  //-------------------------------------
  /**
   * Prints the administrative information specific to this service.
   * By default, this does nothing.  Subclasses should override this
   * to print service-specific information.
   *
   * @param pRequest the current request
   * @param pReponse the current response
   * @param pOut the output stream to write to
   * @exception ServletException if an error occurred 
   * @exception IOException if an error occurred
   *
   */
  protected void printAdmin (HttpServletRequest pRequest,
                             HttpServletResponse pResponse,
                             ServletOutputStream pOut)
       throws ServletException, IOException
  {
  }

  //-------------------------------------
  /**
   * Formats the specified object, turning it into a String
   * representation of its value.  If the object is a
   * NameContextElement, then a link to the service is returned.
   *
   * @param pObject the object to sort
   * @param pRequest the request to use for the formatting
   * @return the format value
   */
  protected String formatObject (Object pObject,
                                 HttpServletRequest pRequest)
  {
    // Get the formatter
    PropertyValueFormatter formatter =
      mNucleus.getPropertyValueFormatters ().
      getFormatter (pObject);

    if (formatter != null) {
      return formatter.formatPropertyValue (pObject, pRequest, this);
    }
    else {
      String absoluteName = getAbsoluteNameOf(pObject);
      if (absoluteName != null) {
        return 
          formatServiceLink (absoluteName, pRequest) +
          absoluteName +
          "</a>";
      }
      else if (pObject instanceof ValueFormatter) {
        return ((ValueFormatter) pObject).formatValue ();
      }
      else {
        return getResourceString("serviceAdminServletInstanceOf") +
               pObject.getClass().getName();
      }
    }
  }

  //-------------------------------------
  /**
   * Formats the specified object, turning it into the "long form"
   * String representation of its value.  If the object is a
   * NameContextElement, then a link to the service is returned.
   *
   * @param pObject the object whole long form to return
   * @param pRequest the current request
   * @return the long form representation of the object
   */
  protected String formatLongObject (Object pObject,
                                     HttpServletRequest pRequest)
  {
    // Get the formatter
    PropertyValueFormatter formatter =
      mNucleus.getPropertyValueFormatters ().
      getFormatter (pObject);

    if (formatter != null) {
      return formatter.formatLongPropertyValue (pObject, pRequest, this);
    }
    else {
      String strPath = getAbsoluteNameOf(pObject);
      if (strPath != null) {
        return 
          formatServiceLink (strPath, pRequest) +
          strPath +
          "</a>";
      }
      else if (pObject instanceof ValueFormatter) {
        return ((ValueFormatter) pObject).formatLongValue ();
      }
      else {
        return "<span style='white-space:pre'>" +
          StringUtils.escapeHtmlString(pObject.toString ()) + "</span>";
      }
    }
  }


  //-------------------------------------
  /**
   * Returns the specified service name in a manner that is usable as
   * a link, by adding the servlet path and appending a "/" if
   * necessary.
   *
   * @param pServiceName the name of the service
   * @param pRequest the current request
   * @return the formatted service name
   */
  protected String formatServiceName (String pServiceName,
                                      HttpServletRequest pRequest)
  {
    return formatServiceName(pServiceName, null, pRequest);
  }
  

  //-------------------------------------
  /**
   * Returns the specified service name in a manner that is usable as
   * a link, by adding the servlet path and appending a "/" if
   * necessary.
   *
   * @param pServiceName the name of the service to format
   * @param pSuffix the suffix to append to the service string
   * @param pRequest the current request
   * @return the formatted service name
   */
  protected String formatServiceName (String pServiceName,
                                      String pSuffix,
                                      HttpServletRequest pRequest)
  {
    if (pServiceName == null) pServiceName = "/";
    /* In case the name has spaces in it */
    pServiceName = URLUtils.escapeUrlString(pServiceName);
    String contextPath = pRequest.getContextPath();
    String servletPath = pRequest.getServletPath ();
    if (pServiceName.startsWith("/")) {
      if (servletPath != null)
        pServiceName = servletPath + pServiceName;
      /*
       * Relative names should be just left relative to the current pathInfo
       */
      if (contextPath != null && contextPath.startsWith("/") && contextPath.length() > 1)
        pServiceName = contextPath + pServiceName;
    }
    
    if (((pSuffix == null) || !pSuffix.startsWith("/")) &&
        !pServiceName.endsWith ("/")) {
      pServiceName += '/';
    }

    if (pSuffix != null)
      pServiceName = pServiceName + pSuffix;

    if (!pServiceName.endsWith ("/")) pServiceName += '/';    

    pServiceName =
      ServletUtil.getDynamoRequest(pRequest).encodeURL(pServiceName);
    
    return pServiceName;
  }

  //-------------------------------------
  /**
   * Returns the opening "href" tag that will lead back to the
   * specified absolute service name
   *
   * @param pServiceName the service to format a link to
   * @param pRequest the current request
   * @return the formatted beginning of the link tag
   */
  protected String formatServiceLink (String pServiceName,
                                      HttpServletRequest pRequest)
  {
    StringBuilder buf = new StringBuilder ("<a href=\"");
    buf.append (formatServiceName (pServiceName, pRequest));
    buf.append ("\">");
    return buf.toString ();
  }

  //-------------------------------------
  // PropertyValueFormatter methods
  //-------------------------------------
  /**
   * Formats the specified value into a String, using the "short form"
   * of the value.
   *
   * @param pValue the property value to format
   * @param pRequest the current request
   * @param pFormatter the formatter to use for formatting
   * @return the formatted property value
   */
  public String formatPropertyValue (Object pValue, 
                                     HttpServletRequest pRequest,
                                     PropertyValueFormatter pFormatter)
  {
    return formatObject (pValue, pRequest);
  }

  //-------------------------------------
  /**
   * Formats the specified value into a String, using the "long form"
   * of the value.
   *
   * @param pValue the property value to format
   * @param pRequest the current request
   * @param pFormatter the formatter to use for formatting
   * @return the formatted long property value
   */
  public String formatLongPropertyValue (Object pValue, 
                                         HttpServletRequest pRequest,
                                         PropertyValueFormatter pFormatter)
  {
    return formatLongObject (pValue, pRequest);
  }

  //-------------------------------------

  /**
   * Loads the resource bundle, if not already loaded, and returns the
   requested resource string.
   * @param pPattern the resource key of the resource to return
   * @return the corresponding resource string
   */
  protected String getResourceString(String pPattern)
  {
    return getResourceString(pPattern, new String[0]);
  }

  /**
   * Loads the resource bundle, if not already loaded, and returns the
   requested resource string with any args inserted.
   * @param pPattern resource key for the pattern
   * @param pPatternArgs the format arguments
   * @return the formatted resource string.
   */
  protected String getResourceString(String pPattern, Object[] pPatternArgs)
  {
    if (sAdminResourceBundle == null)
      sAdminResourceBundle =
        java.util.ResourceBundle.getBundle("atg.nucleus.ServiceAdminResources", atg.service.dynamo.LangLicense.getLicensedDefault());

    String formattedString = "";
    
    if (pPatternArgs == null)
      formattedString = sAdminResourceBundle.getString(pPattern);
    else
      formattedString =
        MessageFormat.format(sAdminResourceBundle.getString(pPattern),
                             pPatternArgs);
    return formattedString;
  }

  /**
   * Public form of getResourceString that supports
   * variable number of arguments.
   * @param pPattern resource key for the pattern
   * @param pArgs the format arguments
   * @return the formatted resource string.
   */
  public String getResourceString(String pPattern, String ... pArgs) {
    return getResourceString(pPattern, (Object[])pArgs);
  }


  //-------------------------------------
  /**
   * The ResourceBundle to use with this AdminServlet.
   * Override this method to use a different resource bundle
   * @return the resource bundle
   */
  protected ResourceBundle getResourceBundle()
  {
    return sAdminResourceBundle;
  }
  
  //-------------------------------------
  /**
   * Sets the service component as resolved by Nucleus.
   * In some cases the object resolved from Nucleus is a 
   * proxy to the real one, so the admin servlet may
   * require a reference to the proxy object.
   * @param pService the re-resolved service
   */
  public void setResolvedService(Object pService) {
    mResolvedService = pService;
  }


  /** Return the absolute name of pComponent. Attempts to get
   * the absolute path of the component via unresolveName
   *
   * @param pComponent the component to get the absolute name of
   *
   * @return the absolute name of the specified object, or null if
   * the absolute name could not be found (because the
   * component could not be found by unresolveName and
   * it's not a name context element).
   * 
   */
  protected String getAbsoluteNameOf(Object pComponent) {
    return mNucleus.getAbsoluteNameOf(pComponent);
  }
  


  /** Holds a name and a PostRequestProcessor. Used by
   * addAdminPostRequestProcessor and executeAdminPostRequestProcessor.
   */
  private static class NameAndPostRequestProcessor {
    String mName;
    PostRequestProcessor mProcessor;
    /** Create a new NameAndPostRequestProcessor.
     * @param pName the name to later use in
     *   ServletUtil.addPostRequestProcessor
     * @param pProcessor the post request processor
     */
    NameAndPostRequestProcessor(String pName, PostRequestProcessor pProcessor) {
      mName = pName;
      mProcessor = pProcessor;
    }
    /** The name to later use as the owner for
     * ServletUtil.addPostRequestProcessor. */
    public String getName() {
      return mName;
    }
    /** The processor to later execute. */
    public PostRequestProcessor getProcessor() {
      return mProcessor;
    }
  }

  @SuppressWarnings("unchecked")
  /**
   * Add an admin servlet post request processor to
   * the list attribute on the request. Will be actually
   * execute/enqueued by executeAdminServletPostRequestProcessors
   * in a finally clause after this admin servlet is rendered. Only
   * for use during an admin servlet's service() method.
   *
   * @param pRequest the current request
   * @param pProcessor the post request processor to add
   * @param pOwnerName the owner name
   */
  public void addAdminServletPostRequestProcessor(
    HttpServletRequest pRequest, PostRequestProcessor pProcessor,
    String pOwnerName) {

    List<NameAndPostRequestProcessor> processors =
      (List<NameAndPostRequestProcessor>)
      pRequest.getAttribute(ATTR_AMIN_SERVLET_POST_REQUEST_PROCESSORS);

    if (processors == null) {
      processors = new ArrayList<NameAndPostRequestProcessor>();
      pRequest.setAttribute(ATTR_AMIN_SERVLET_POST_REQUEST_PROCESSORS,
                            processors); 
    }
    processors.add(new NameAndPostRequestProcessor(pOwnerName, pProcessor));
  }

  @SuppressWarnings("unchecked")
  /**
   * Execute (actually, just call ServletUtil.addPostRequestProcessor())
   * on any PostRequestProcessors added by addAdminServletPostRequestProcessors.
   *
   * @param pRequest the current request
   * @param pResponse the current response
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void executeAdminServletPostRequestProcessors(
    HttpServletRequest pRequest, HttpServletResponse pResponse) throws ServletException, IOException {
    List<NameAndPostRequestProcessor> processors = (List<NameAndPostRequestProcessor>)
      pRequest.getAttribute(ATTR_AMIN_SERVLET_POST_REQUEST_PROCESSORS);
    if (processors != null) {
      DynamoHttpServletRequest request =
        ServletUtil.getDynamoRequest(pRequest);
      DynamoHttpServletResponse response =
        ServletUtil.getDynamoResponse(request, pResponse);
      ServletException seFirst = null;
      for (NameAndPostRequestProcessor processor:  processors) {
        try {
          ServletUtil.addPostRequestProcessor(
            request, response, processor.getProcessor(),
            processor.getName());
        } catch (ServletException e) {
          // keep going, but remember the first exeception... we still
          // want to do as much clean-up as possible.
          if (seFirst != null) {
            seFirst = e;
          }
        }
      }
      if (seFirst != null) {
        throw seFirst;
      }
    }
  }
  
} // end class

