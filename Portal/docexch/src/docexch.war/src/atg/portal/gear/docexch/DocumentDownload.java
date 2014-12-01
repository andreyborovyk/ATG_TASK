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

import atg.portal.framework.*;
import atg.portal.alert.*;
import atg.nucleus.naming.ParameterName;
import atg.repository.*;
import atg.repository.servlet.ItemLookupDroplet;
import atg.portal.nucleus.NucleusComponents;
import atg.nucleus.Nucleus;
import atg.security.SecurityException;
import atg.servlet.DynamoServlet;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.naming.NameContext;

import java.util.*;
import java.io.*;

import javax.jms.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.transaction.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * This servlet will deliver the value of one of a repository item's properties  
 * to the response's output stream. This servlet expects the following attributes 
 * on the request: 
 *
 * <P>
 * <DL>
 * 
 * <DT>docexchDocumentId
 * <DD>The id of the item to lookup
 * 
 * <DT>docexchGearId
 * <DD>The id of the current gear
 * 
 * <DT>docexchCommunityId
 * <DD>The id of the current community
 * 
 * <DT>docexchPageId
 * <DD>The id of the current page
 * 
 * </DL>
 * <P>
 * If the document is not successfully retrieved, then we will redirect 
 * to the accessDenied which should be configured in the web.xml file. 
 * <P>
 * This servlet is mapped to the /gear/docexch/download url in web.xml.
 * <P>
 *
 * 
 * @author Cynthia Harris
 * @version $Id: //app/portal/version/10.0.3/docexch/docexch.war/src/atg/portal/gear/docexch/DocumentDownload.java#2 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class DocumentDownload
  extends DynamoServlet 
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = 
    "$Id: //app/portal/version/10.0.3/docexch/docexch.war/src/atg/portal/gear/docexch/DocumentDownload.java#2 $$Change: 651448 $";
  //-------------------------------------
  // Constants
  //------------------------------------

  public static final String REPOS_PATH_PARAM = "repositoryPath";
  public static final String ITEM_DESC_NAME_PARAM = "itemDescriptorName";
  public static final String FILEDATA_PROP_PARAM = "fileDataPropertyName";
  public static final String MIMETYPE_PROP_PARAM = "mimeTypePropertyName";
  public static final String FILESIZE_PROP_PARAM = "fileSizePropertyName";
  public static final String FILENAME_PROP_PARAM = "filenamePropertyName";
   
  public static final String ID_PARAM = "id";

  public static final String DOC_DOWNLOAD_PARAMS_PATH = 
    "/atg/portal/gear/docexch/DocumentDownloadParams";

  //---------------------------------------------------------------
  /**
   * Service the request.
   *
   * This method will deliver the item of the spec'd item type
   * from the spec'd repository specified by the id parameter.
   * It will correctly set the MIME type of the response before writing 
   * any data to the output stream.
   */
  public void doGet(DynamoHttpServletRequest pRequest, 
                    DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    if (isLoggingDebug())
      log("in DocumentRetrievalDroplet.  about to check permissions");
    
    DocumentDownloadParams downloadParams = 
      (DocumentDownloadParams) pRequest.resolveName(DOC_DOWNLOAD_PARAMS_PATH);

    GearEnvironment gearEnv = getGearEnvironment(pRequest, pResponse, downloadParams);

    String currentUserName = getCurrentUserName (pRequest);

    if (!(mayRead(gearEnv))) {
        if (isLoggingDebug()) {
            StringBuffer msg = new StringBuffer("Attempted security breach.");
            if (gearEnv != null)
                msg.append("The user ").
                    append(currentUserName).
                    append(" has attempted to view a document in gear id ").
                    append( gearEnv.getGear().getId()).
                    append(".");        
            log(msg.toString());
        }
      if (gearEnv != null)
          pResponse.sendRedirect(pResponse.encodeRedirectURL(gearEnv.getAccessDeniedURI()));
      return;
    }
    
    if (isLoggingDebug())
      log("Its ok to read in this gear.  carry on.");



    // Collect all the parameter values
    RepositoryItem item = null;
    Repository rep = null;

    String reppath = gearEnv.getGearInstanceParameter(REPOS_PATH_PARAM);
    String itemDescriptorName = gearEnv.getGearInstanceParameter(ITEM_DESC_NAME_PARAM);
    String fileDataPropertyName = gearEnv.getGearInstanceParameter(FILEDATA_PROP_PARAM);
    String mimeTypePropertyName = gearEnv.getGearInstanceParameter(MIMETYPE_PROP_PARAM);
    String fileSizePropertyName = gearEnv.getGearInstanceParameter(FILESIZE_PROP_PARAM);

      if (isLoggingDebug())
        log("reppath = " + reppath);
      if (reppath != null) {
        // perform JNDI lookup
        try {
          rep = (Repository) NucleusComponents.lookup("dynamo:" + reppath);
        }
        catch (javax.naming.NamingException e) {
          throw new UnavailableException(e.toString(), 0);
        }
        if (isLoggingDebug()) {
          if (rep != null)
            log("Using repository " + rep.getRepositoryName());
          else 
            log("rep is null");
          log("Use item descriptor parameter " + itemDescriptorName);
          log("Use parameter file data property name " + fileDataPropertyName);
          log("Use parameter mimeTypePropertyName " + mimeTypePropertyName);
          log("Use parameter fileSizePropertyName " + fileSizePropertyName);
        }
      }

      String id = downloadParams.getDocexchDocumentId();
      if (id == null ) {
        if(isLoggingDebug())
          log("document id is null.");
        pResponse.sendRedirect(pResponse.encodeRedirectURL(getErrorPage()));
        return;
      } 

      // CHECK FOR NULL VALUES THAT MUST COME FROM THE GEARENV
      if (rep == null || 
          itemDescriptorName == null || 
          fileDataPropertyName == null ||
          mimeTypePropertyName == null ||
          fileSizePropertyName == null ) {
        if (isLoggingDebug())
          log("one of the needed gear parameters is null");
        pResponse.sendRedirect(pResponse.encodeRedirectURL(getErrorPage()));
        return;
      }

      // The action begins here
      try {
        // get the repository item
        if (isLoggingDebug())
          log("Find item: id=" + id + "; type=" + itemDescriptorName);
        item = rep.getItem(id, itemDescriptorName);
        
        if (item == null) {
          pResponse.sendRedirect(pResponse.encodeRedirectURL(getErrorPage()));
          return;
        }
        // One last security check: Is the requested document really in the gear?
        else if (!DocExchUtils.isDocInGear(item, gearEnv)){
          pResponse.sendRedirect(pResponse.encodeRedirectURL(gearEnv.getAccessDeniedURI()));
          return;
        }    
        else {
          if (isLoggingDebug()) 
            log("item is not null");
          
          // get the values out of the repository item
          String mimeType = (String)item.getPropertyValue(mimeTypePropertyName);
          int fileLength = ((Integer)item.getPropertyValue(fileSizePropertyName)).intValue();
          byte [] data = (byte[])item.getPropertyValue(fileDataPropertyName);

          // determine and set the mimetype of the data to be delivered
          if(isLoggingDebug())
            log("mimeType = " + mimeType);
          if (mimeType == null || mimeType.trim().length() == 0)
            mimeType="text/html";

          if (pResponse == null) {
            if (isLoggingDebug())
              log("pResponse is null!  Cannot continue.");            
            pResponse.sendRedirect(pResponse.encodeRedirectURL(getErrorPage()));
            return;
          }
          pResponse.setContentType(mimeType);
          
          // determine and set the size of the document to be delivered
          if (isLoggingDebug())
            log("fileLength = " + fileLength);
          pResponse.setContentLength(fileLength);
          
          // deliver the data or empty if no data exists.
          OutputStream outputStream = pResponse.getOutputStream();
          if (data == null) {
            pResponse.sendRedirect(pResponse.encodeRedirectURL(getErrorPage()));
            return;
          }
          //pRequest.setServletPath("/thisdoc.foo");
          outputStream.write(data);

          //
          // Create the document viewed message, set its properties, and send it.
          //
          DocumentViewedMessage message = new DocumentViewedMessage(gearEnv);
          
          message.setDocumentId(id);
          message.setDocumentName(getDocumentName(gearEnv, item));
          message.setProfileId(Utilities.getProfile(pRequest).getRepositoryId());
          message.setPageId(getPageRepositoryId(gearEnv));
          message.setCurrentUserName(currentUserName);
          
          sendDocExchMessage((DocExchMessage)message);          
        }
      }
      catch (RepositoryException exc) {
        if (isLoggingError())
          log("Can't get document", exc);
        pResponse.sendRedirect(pResponse.encodeRedirectURL(getErrorPage()));
      }
  }

  /** 
   * return true if the user may read.
   */ 
    public boolean mayRead(GearEnvironment gearEnv) {     
      if (gearEnv == null) {
        if(isLoggingDebug())
          log("no gear environment to test if user may read");
        return false;
      }
      // if gear doesn't exist or we don't have access to it, then
      // deny the read
      try {
        Gear gear = gearEnv.getGear();
        if ((gear == null) || !gear.hasAccess(PortalAccessRights.READ))
          return false;

        // gear access ok, lets check community
        Community community = gearEnv.getCommunity();
        if ((community == null) || !community.hasAccess(PortalAccessRights.READ))
          return false;

        // gear and community access ok, lets check page
        Page page = gearEnv.getPage();
        if ((page == null) || !page.hasAccess(PortalAccessRights.READ))
          return false;
      }
      catch (SecurityException e) {
        Utilities.logError(e);
        return false;
      }

      // looks ok
      return true;
    }

   /**
   * Override super class method to allow browsers to use HEAD http 
   * method to request information about a document.
   */
  public void service (DynamoHttpServletRequest req,
    DynamoHttpServletResponse res) 
    throws ServletException, IOException 
  { 
    String method = req.getMethod (); 
    if (method.equals ("GET")) { 
      doGet(req, res); 
    } 
    else if (method.equals ("HEAD")) { 
      doGet(req, res); 
    } 
    else if (method.equals ("POST")) { 
      doPost(req, res); 
    } 
    else if (method.equals ("PUT")) { 
      doPut(req, res); 
    } 
    else if (method.equals ("DELETE")) { 
      doDelete(req, res); 
    } 
    else { 
      res.sendError (HttpServletResponse.SC_BAD_REQUEST, 
   "Method '" + method + "' is not supported by this URL"); 
    } 
  }

  /**
   * use the request to get the current gear environment
   */
  GearEnvironment getGearEnvironment (DynamoHttpServletRequest pReq,
                                      DynamoHttpServletResponse pResp,
				      DocumentDownloadParams pDownloadParams) {
    if (isLoggingDebug())
      log("in getGearEnvironment. paf_gear_id = " + pReq.getParameter("paf_gear_id")); 

    pReq.setAttribute(RequestAttributes.GEAR, pDownloadParams.getDocexchGearId());
    pReq.setAttribute(RequestAttributes.COMMUNITY, pDownloadParams.getDocexchCommunityId());
    pReq.setAttribute(RequestAttributes.PAGE, pDownloadParams.getDocexchPageId());

    GearEnvironment env = null;
    try {
      env = EnvironmentFactory.getGearEnvironment(pReq, pResp);
    }
    catch (EnvironmentException e) {
      if (isLoggingError())
        log("can't initialize environment", e);
    }
    return env;
  }

  //-------------------------------------
  // property: errorPage
  String mErrorPage;
  public void setErrorPage(String pErrorPage) {
    mErrorPage = pErrorPage;
  }
  public String getErrorPage() {
    return mErrorPage;
  }

  //-------------------------------------
  // property: loggingDebug
  boolean mLoggingDebug;
  public void setLoggingDebug(boolean pLoggingDebug) {
    mLoggingDebug = pLoggingDebug;
  }
  public boolean isLoggingDebug() {
    return mLoggingDebug;
  }  

  //-------------------------------------
  // property: loggingError
  boolean mLoggingError;
  public void setLoggingError(boolean pLoggingError) {
    mLoggingError = pLoggingError;
  }
  public boolean isLoggingError() {
    return mLoggingError;
  }

  final public void init(ServletConfig config)
    throws ServletException
  {
    super.init(config);

    mErrorPage = getServletConfig().getInitParameter("ErrorPage");
    String loggingDebug = getServletConfig().getInitParameter("LoggingDebug");
    setLoggingDebug((loggingDebug != null && loggingDebug.equals("true")));
    String loggingError = getServletConfig().getInitParameter("LoggingError");
    setLoggingError((loggingError != null && loggingError.equals("true")));

    setPublisherName(getServletConfig().getInitParameter("PublisherName"));
  }


    //  Private methods  
    
  /**
   * Utility method to retrieve the uploaded file's name. Returns null
   * if uploaded file is null.
   */
  private String getDocumentName(GearEnvironment pGearEnv, RepositoryItem pItem)
  {
    return getPropertyValue(pItem, pGearEnv, "titlePropertyName").toString();
  }

  /**
   * Utility method to retrieve the uploaded file's name. Returns null
   * if uploaded file is null.
   */
  private String getCurrentUserName(DynamoHttpServletRequest pRequest)
  {
    Profile currentUser = Utilities.getProfile(pRequest);
    if (currentUser == null)
      return null;
    String name = currentUser.getItemDisplayName();
    if (name == null)
        return " ";
    else return name;
  }

  /**
   * Utility method to retrieve a property's value as a string.
   * @param pPropertyName the name of the property to retrieve from getValue().
   * @param pGearEnv the gear environment.
   */
  private Object getPropertyValue(RepositoryItem pItem, GearEnvironment pGearEnv, String pPropertyName)
  {
      String propertyName = pGearEnv.getGearInstanceParameter(pPropertyName);
      Object value = pItem.getPropertyValue(propertyName);
      if (value == null)
          return null;
      return value;

  }

  /**
   * Utility method to return the Page's Repository Id. Returns null
   * if the Gear environment's Page is null.
   */
  private String getPageRepositoryId(GearEnvironment pGearEnv)
  {
    Page page = pGearEnv.getPage();
    if (page != null)
      return page.getId();

    return null;
  }

  //------------------------------------------------------
  //   methods for sending messages (alerts)
  //------------------------------------------------------

  public void sendDocExchMessage(DocExchMessage pMessage)
  {
    try {

        // lookup the publisher service
       	GearMessagePublisher publisher = getPublisher();

        if (publisher != null)
    	{
    	  if (isLoggingDebug())
    	  {
    	    log("sendDocExchMessage: type=" + pMessage.getMessageType());
            log("gear item = " + pMessage.getGearId());
            log("community item = " + pMessage.getCommunityId());
          }

    	  publisher.writeMessage(pMessage);
    	}
    } catch( JMSException jmse ) {
            if (isLoggingError())
                jmse.printStackTrace();
    } catch( javax.naming.NamingException namex ) {
            if (isLoggingError())
                namex.printStackTrace();
    }

  }

  //-------------------------------------
  // property: publisherName
  String mPublisherName;
  public void setPublisherName(String pPublisherName) {
    mPublisherName = pPublisherName;
  }
  public String getPublisherName() {
    return mPublisherName;
  }

  //-------------------------------------
  // property: publisher
  private static GearMessagePublisher mPublisher;
  private void setPublisher(GearMessagePublisher pPublisher) {
    mPublisher = pPublisher;
  }
  private GearMessagePublisher getPublisher()
    throws javax.naming.NamingException
  {
    if (mPublisher == null)
    {
        mPublisher = (GearMessagePublisher) NucleusComponents.lookup(getPublisherName());
    }
    return mPublisher;
  }



    //----------------------------------------
    /**
     * log a string
     */
    public void log(String pMessage)
    {
	getServletConfig().getServletContext().log(pMessage);
    }

    //----------------------------------------
    /**
     * log a string and exception
     */
    public void log(String pMessage, Throwable pThrowable)
    {
	getServletConfig().getServletContext().log(pMessage, pThrowable);
    }

} // end of class

