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

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import atg.core.util.ResourceUtils;
import atg.nucleus.ServiceException;
import atg.service.filecache.FileAttributes;
import atg.service.filecache.FileAttributesCache;
import atg.servlet.AttributeFactory;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.PathTranslator;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;
import atg.servlet.minimal.WebApplicationInterface;

/**
 * <p>This pipeline servlet will set the pathTranslated by appending
 * the pathInfo to the document root (set as a property).  If the
 * pathInfo maps to a directory, then this will attempt to find an
 * index file in that directory as specified by the "indexFiles"
 * property, which lists the index files to try to find in order.  If
 * the pathTranslated is modified, then pathInfo and requestURI is
 * also modified to keep the requestURI=contextPath+servletPath+pathInfo+queryArgs
 * equation true.
 *
 * <p>If none of the index files are found for a directory reference,
 * and "shouldListDirectory" is true, then the directory listing will
 * be returned as the result of the request and the request will not
 * be passed on.
 *
 * <p>If the pathInfo cannot map to any file or directory listing,
 * then a 404 error is returned and the request is not passed on.
 *
 * <p>If the pathInfo maps to a directory but does not end with a "/",
 * then a redirect is issued with the "/" and the request is not
 * passed on.
 *
 * <p>If the pathTranslated is already set (is non-null), then all of
 * the above functionality is skipped.
 *
 * @author Nathan Abramson
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/servlet/pipeline/FileFinderPipelineServlet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo
 *   description: Servlet that maps the document path to the filesystem
 *                path, setting the pathTranslated property
 *   attribute: functionalComponentCategory Services
 *   attribute: featureComponentCategory Pipeline
 *   attribute: icon /atg/ui/common/images/pipelinecomp.gif
 **/

public
class FileFinderPipelineServlet
extends PipelineableServletImpl implements PathTranslator
{
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/servlet/pipeline/FileFinderPipelineServlet.java#2 $$Change: 651448 $";


  //-------------------------------------
  // Permanent Attributes

  /** Resource bundles **/
  static final String MY_USER_RESOURCES_NAME 
  = "atg.servlet.pipeline.FileFinderPipelineServletUserResources";
  /** Resource bundles **/
  static final String MY_RESOURCES_NAME 
  = "atg.servlet.pipeline.FileFinderPipelineServletResources";
  private static java.util.ResourceBundle sResourceBundle =
    java.util.ResourceBundle.getBundle(MY_RESOURCES_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  class CharArrayFactory implements AttributeFactory {
    public Object createAttributeValue ()
    { return new char [1024]; }
  }
  AttributeFactory mCharArrayKey = new CharArrayFactory ();

  String[] mVirtualDirectoryList;
  String[] mActualDirectoryList;

  //-------------------------------------
  // Properties

  /** The virtual directory mappings */
  Properties mVirtualDirectoryMap;

  /** The listing of index files */
  String [] mIndexFiles;

  /** Flag indicating if directories should be listed */
  boolean mShouldListDirectory = true;

  /** An ordered list of document roots to search */
  File [] mDocumentRootPath;

  /** The flag indicating if any existing path translated should be ignored */
  boolean mAlwaysTranslate;

  /** The flag that indicates whether we should try to process index files
    * on translated paths */
  boolean mProcessIndexFiles;

  /**
   * An optional URL to redirect the browser to if an error occurs.
   * Examples of errors are the requested file cannot be found or the
   * path info of the request file contains double periods ("..").
   */
  String mErrorURL;

  /** 
    * Do we report an error when we can't find the requested file or just
    * pass it to the next servlet in the chain? 
    */
  boolean mReportFileNotFound = true;

  /**
   * The prefix to use for nucleus components in the doc root
   * This gets stuck in the request so that this pipeline knows how to 
   * lookup components in the pipeline.
   */
  String mDocRootServicePrefix = "/docroot"; 

  //-------------------------------------
  // Statistics

  /** The number of files not found by this */
  int mFileNotFoundCount;

  /** The number of index files served by this */
  int mIndexFilesServedCount;

  /** The number of directory listings served by this */
  int mDirectoryListingsServedCount;


  FileAttributesCache mFileAttributesCache;

  //-------------------------------------
  /**
   *
   * Constructs a new FileFinderPipelineServlet
   **/
  public FileFinderPipelineServlet ()
  {
  }

  //-------------------------------------
  // Properties
  //-------------------------------------

  /**
   * Sets the virtual directory map.
   * @beaninfo
   *   description: The virtual directory map
   */
  public void setVirtualDirectoryMap(Properties pMap)
  {
    mVirtualDirectoryMap = pMap;
  }

  /**
    Gets the virtual directory map.
    */
  public Properties getVirtualDirectoryMap()
  {
    return mVirtualDirectoryMap;
  }
  
  /**
   * Sets the document root from which path references will be
   * resolved.  This is an ordered list of directories that are searched
   * for files.
   * @beaninfo
   *   description: The document roots from which path references will
   *                be resolved
   **/
  public void setDocumentRootPath (File [] pDocumentRootPath)
  {
    mDocumentRootPath = pDocumentRootPath;
  }

  //-------------------------------------
  /**
   * Returns the document root from which path references will be
   * resolved.  This is an ordered list of directories that are 
   * searched for files.
   **/
  public File [] getDocumentRootPath ()
  {
    return mDocumentRootPath;
  }

  /**
   * For convenience, users can also set the DocumentRoot property as a
   * single file if documentRootPath has not already been set.  This just
   * gets mapped into a path with one entry.
   * @beaninfo
   *       description: Single document root, if only one is needed
   **/
  public void setDocumentRoot (File pDocumentRoot)
  {
     if (mDocumentRootPath==null || mDocumentRootPath.length==0){
       mDocumentRootPath = new File[1];
       mDocumentRootPath[0] = pDocumentRoot;
     }else{
       if (isLoggingWarning())
         logWarning(ResourceUtils.getMsgResource
                    ("fileFinderPipelineServletDocumentRootPathAlreadySet",
                     MY_RESOURCES_NAME, sResourceBundle));
     }
  }

  //-------------------------------------
  /**
   * Returns the first entry in the document root path. 
   **/
  public File getDocumentRoot ()
  {
    if (mDocumentRootPath == null || mDocumentRootPath.length == 0)
      return null;
    return mDocumentRootPath[0];
  }

  //-------------------------------------
  /**
   * Sets the list of files to be searched in order when a request is
   * sent referring to a directory.
   * @beaninfo
   *   description: Name of index files to look for when a directory is
   *                requested
   **/
  public void setIndexFiles (String [] pIndexFiles) 
  {
    mIndexFiles = pIndexFiles;
  }

  //-------------------------------------
  /**
   * Returns the list of files to be searched in order when a request
   * is sent referring to a directory.
   **/
  public String [] getIndexFiles ()
  {
    return mIndexFiles;
  }

  //-------------------------------------
  /**
   * Sets the flag indicating whether a directory should be listed in
   * response to a directory reference in which no index files can be
   * found.
   * @beaninfo
   *   description: True if the directory should be listed if no
   *                index files are found
   **/
  public void setShouldListDirectory (boolean pShouldListDirectory)
  {
    mShouldListDirectory = pShouldListDirectory;
  }

  //-------------------------------------
  /**
   * Returns the flag indicating whether a directory should be listed
   * in response to a directory reference in which no index files can
   * be found.
   **/
  public boolean getShouldListDirectory ()
  {
    return mShouldListDirectory;
  }

  //-------------------------------------
  /**
   * Should we report file not found errors?
   * @beaninfo
   *   description: True if we should report file not found errors
   **/
  public void setReportFileNotFound (boolean pReportFileNotFound)
  {
    mReportFileNotFound = pReportFileNotFound;
  }

  //-------------------------------------
  /**
   * Gets the flag that tells whether or not we report file not found 
   * errors. 
   **/
  public boolean getReportFileNotFound ()
  {
    return mReportFileNotFound;
  }

  //-------------------------------------
  /**
   * Sets the property ErrorURL.  If this is not null, it indicates a
   * URL to redirect the browser to if an error occurs.
   *
   * @beaninfo
   *   description: The URL to redirect the browser to if an error occurs
   *   displayName: ErrorURL
   * @param pErrorURL new Error URL
   **/
  public void setErrorURL(String pErrorURL) {
    mErrorURL = pErrorURL;
  }

  /**
   * Returns the value of the property ErrorURL.
   *
   * @return value of the property ErrorURL
   **/
  public String getErrorURL() {
    return mErrorURL;
  }

  //-------------------------------------
  /**
   * Returns the flag indicating if this should always translate
   * files, even if the request comes in with a pathTranslated.
   * @beaninfo
   *   description: True if translation should be done even if
   *                pathTranslated is already set
   **/
  public boolean isAlwaysTranslate ()
  {
    return mAlwaysTranslate;
  }

  //-------------------------------------
  /**
   * Sets the flag indicating if this should always translate
   * files, even if the request comes in with a pathTranslated.
   **/
  public void setAlwaysTranslate (boolean pAlwaysTranslate)
  {
    mAlwaysTranslate = pAlwaysTranslate;
  }

  //-------------------------------------
  /**
   * Should we try to process index files on already translated paths?
   *
   * This option should be used for servers that translate the paths, 
   * but do not do index file translating (such as the Java Web Server).
   * @beaninfo
   *   description: True if we should look for index files even if
   *                pathTranslated is already set
   **/
  public void setProcessIndexFiles (boolean pProcessIndexFiles)
  {
    mProcessIndexFiles = pProcessIndexFiles;
  }

  //-------------------------------------
  /**
   * Returns the flag that indicates whether we 
   * should we try to process index files on already translated paths.
   *
   * This option should be used for servers that translate the paths, 
   * but do not do index file translating (such as the Java Web Server).
   *
   **/
  public boolean getProcessIndexFiles ()
  {
    return mProcessIndexFiles;
  }

  /**
   * Returns the property that specifies the path to find services
   * in the nucleus component hierarchy that are in the doc root.
   * @beaninfo
   *   description: The Nucleus path for finding services that are
   *                in the document root
   */
  public String getDocRootServicePrefix() 
  {
    return mDocRootServicePrefix;
  }

  /**
   * Sets the property that specifies the path to find services
   * in the nucleus component hierarchy that are in the doc root.
   */
  public void setDocRootServicePrefix(String pDocRootServicePrefix) 
  {
    mDocRootServicePrefix = pDocRootServicePrefix;
  }

  //-------------------------------------
  // Statistics
  //-------------------------------------
  /**
   * Increments the number of files not found by this
   **/
  synchronized void incrementFileNotFoundCount ()
  {
    mFileNotFoundCount++;
  }

  //-------------------------------------
  /**
   * Returns the number of files not found by this
   * @beaninfo
   *   description: The number of requests for files that could not be found
   **/
  public int getFileNotFoundCount ()
  {
    return mFileNotFoundCount;
  }

  //-------------------------------------
  /**
   * Increments the number of index files served by this
   **/
  synchronized void incrementIndexFilesServedCount ()
  {
    mIndexFilesServedCount++;
  }

  //-------------------------------------
  /**
   * Returns the number of index files served by this
   * @beaninfo
   *   description: The number of index files that have been served
   **/
  public int getIndexFilesServedCount ()
  {
    return mIndexFilesServedCount;
  }

  //-------------------------------------
  /**
   * Increments the number of directory listings served by this
   **/
  synchronized void incrementDirectoryListingsServedCount ()
  {
    mDirectoryListingsServedCount++;
  }

  //-------------------------------------
  /**
   * Returns the number of directory listings served by this
   **/
  public int getDirectoryListingsServedCount ()
  {
    return mDirectoryListingsServedCount;
  }


  //---------------------------------------------------------------------------
  // property: fileAttributesCache
  //---------------------------------------------------------------------------

  /**
   * Set the file attributes cache
   *
   */
  public void setFileAttributesCache(FileAttributesCache pFileAttributesCache) {
    mFileAttributesCache = pFileAttributesCache;
  }

  /**
   * Return the file attributes cache
   * @beaninfo
   *   description: The file attributes cache
   */
  public FileAttributesCache getFileAttributesCache() {
    return mFileAttributesCache;
  }
  

  public void doStartService() throws ServiceException
  {
    /*
     * Remove all .jar and .zip files from the document root path
     */
    File [] docRoots = getDocumentRootPath();
    if (docRoots == null)
      logError("No document roots defined!");
    else {
      int ct = 0;
      for (int i = 0; i < docRoots.length; i++) {
	if (docRoots[i] == null) {
	  if (isLoggingError())
	    logError("Null document root encountered!");
	}
	if (docRoots[i] == null ||
	    docRoots[i].getName().endsWith(".jar") ||
	    docRoots[i].getName().endsWith(".zip")) 
	  ct++;
      }
      if (ct > 0) {
	File [] tmpRoots = new File[docRoots.length - ct];
	int j = 0;
	for (int i = 0; i < docRoots.length; i++) {
	  if (docRoots[i] != null &&
	      !docRoots[i].getName().endsWith(".jar") &&
	      !docRoots[i].getName().endsWith(".zip")) 
	    tmpRoots[j++] = docRoots[i];
	}
	setDocumentRootPath(tmpRoots);
      }
    }

    // Build a virtual directory list with all the virtual directories
    // (less any trailing slashes) and a corresponding actual
    // directory list from the virtual directory map.  Then, while
    // servicing requests, we can iterate over these lists, which is
    // more efficient than building an enumeration from the map with
    // each request.

    if (mVirtualDirectoryMap == null)
      return;  // no virtual directories so forget it

    Enumeration vdirs = mVirtualDirectoryMap.propertyNames();
    int count = 0;

    // First count the virtual directories
    while (vdirs.hasMoreElements())
    {
      String vdir = (String) vdirs.nextElement();
      if (!vdir.equals("/")){
        // don't count "/" vdirs
        count++;
      }
    }

    mVirtualDirectoryList = new String[count];
    mActualDirectoryList  = new String[count];

    vdirs = mVirtualDirectoryMap.propertyNames();  // reset (of sorts)
    PropertyEditor fileEditor = PropertyEditorManager.findEditor(File.class);

    for (int i = 0; i < count; i++)
    {
      String vdir = (String) vdirs.nextElement();

      //Do not allow "/" to override the doc root via
      //virtual directory
      if (!vdir.equals("/")){ //skip over and throw error

      // Find the index of the last character which is not a slash.
      int j = vdir.length() - 1;
      while (vdir.charAt(j) == '/')
        j--;

      mVirtualDirectoryList[i] = vdir.substring(0, j + 1); 

      String adir = mVirtualDirectoryMap.getProperty(vdir);

      // convert the specified directory name using variable
      // substitution as per Nucleus property editors.
      if (fileEditor != null)
      {
        fileEditor.setAsText(adir);
        adir = ((File) fileEditor.getValue()).getAbsolutePath(); 
      }

      // Find the index of the last character which is not a slash.
      int k = adir.length() - 1;
      while (adir.charAt(k) == '/' || adir.charAt(k) == File.separatorChar)
        k--;

      mActualDirectoryList[i] = adir.substring(0, k + 1); 
      }
      else {// attempted to override "/" through vdir
	if (isLoggingError())
         logError(ResourceUtils.getMsgResource
                    ("unableToOverideDocRoot",
                     MY_RESOURCES_NAME, sResourceBundle));	
      }//end of if(!vdir.equals("/"))
      
    }

    if (isLoggingDebug())
    {
      // log the mappings of virtual directories to actual directories
      if (count != 0) {

        StringBuffer buf = new StringBuffer();
        int ii=0;

        while (ii < count-1) {
          buf.append(mVirtualDirectoryList[ii] + " --> " +
                     mActualDirectoryList[ii] + "\n");
          ii++;
        }
        buf.append(mVirtualDirectoryList[ii] + " --> " +
                   mActualDirectoryList[ii]);

        logDebug(buf.toString());
      }
    }

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
    /*
     * On websphere (and perhaps other app servers with "strict output 
     * access", this can cause an error if a subsequent call, such as
     * sendError, calls getWriter().  We need this on dynamo though in 
     * case someone is still using the "DynamoLog" function of the CM.
     */
    if (ServletUtil.isDynamoJ2EEServer() && pResponse.getOutputStream() == null) {
      if (isLoggingDebug())
        logDebug ("Halting service for logRequest: " + pRequest.getRequestURL());
      return;
    }
    
    if (isLoggingDebug ()) {
      logDebug ("Received request for " + pRequest.getRequestURL());
      logDebug ("path info= \"" + pRequest.getPathInfo() + "\"");
      logDebug ("path translated= \"" + pRequest.getPathTranslated() + "\"");
    } 

    // Just in case a web app request ever makes here
    // (should never happen in a correctly configured system).
    WebApplicationInterface webApp = pRequest.getWebApplication();

    if (webApp != null) {
      if (isLoggingError()) {
	logError("Web Application request was sent to FileFinderServlet");
      }
      sendError(pResponse, HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    pRequest.setAttribute(DynamoHttpServletRequest.PATH_TRANSLATOR, this);

    String includePathInfo = 
	(String) pRequest.getAttribute(DynamoHttpServletRequest.INCLUDE_PATH_INFO_ATTRIBUTE);

    String includePathTranslated = 
	(String) pRequest.getAttribute(DynamoHttpServletRequest.INCLUDE_PATH_TRANSLATED_ATTRIBUTE);

    if (isAlwaysTranslate () ||
	(includePathInfo != null  &&
	 includePathTranslated == null) ||
        pRequest.getPathTranslated () == null ||
        pRequest.getPathTranslated ().equals ("")) {
      String pathInfo = pRequest.getPathInfo ();
      
      if (isLoggingDebug())
	logDebug("includePathInfo = " + includePathInfo);

      if (includePathInfo != null) {
	if (! translatePath(pRequest, pResponse, includePathInfo, true))
	  return;
      }
      else if (pathInfo != null) {
        if (!translatePath (pRequest, pResponse, pathInfo, false)) return;
      }
    }
    else { 
      if (getProcessIndexFiles())
	if (!translateIndexFiles (pRequest, pResponse)) return;
    }

    if (mDocRootServicePrefix != null)
      pRequest.setDocRootServicePrefix(mDocRootServicePrefix);

    passRequest (pRequest, pResponse);
  }

  /**
   * This implements the PathTranslator method.  It is called from the
   * DynamoHttpServletRequest.getRealPath method by looking up the 
   * PATH_TRANSLATOR attribute.
   */
  public String getRealPath(DynamoHttpServletRequest pRequest, 
  			    String pPathInfo) 
  {
    File [] docRoots = getDocumentRootPath();
    FileAttributes f = null;

    for (int p = 0; p < docRoots.length && (f == null || !f.canRead()); p++) {
      f = getBaseFile (pRequest, pPathInfo, docRoots[p]);
    }
    if (f == null) return null;
    return f.getPath();
  }


  /** Return the file attributes for the specified file. Use
   * the FileAttributesCache, if set. */
  FileAttributes getFileAttributes(File pFile,
                                   boolean pCacheNonExistent) {
    if (mFileAttributesCache == null)
      return new FileAttributes(pFile);
    else
      return mFileAttributesCache.getFileAttributes(pFile.getPath(),
                                                    pCacheNonExistent);
  }
  


  /** Return the file attributes for the specified file. Use
   * the FileAttributesCache, if set. */
  FileAttributes getFileAttributes(File pFile) {
    if (mFileAttributesCache == null)
      return new FileAttributes(pFile);
    else
      return mFileAttributesCache.getFileAttributes(pFile.getPath());
  }


  /** Return the file attributes for the specified file. Use
   * the FileAttributesCache, if set. */
  FileAttributes getFileAttributes(String pPath) {
    if (mFileAttributesCache == null)
      return new FileAttributes(new File(pPath));
    else
      return mFileAttributesCache.getFileAttributes(pPath);
  }
  

  //-------------------------------------
  /**
   * Translates the path.  Returns true if the request is to be passed
   * on, false if not.
   **/
  boolean translatePath (DynamoHttpServletRequest pRequest,
                         DynamoHttpServletResponse pResponse,
                         String pPathInfo,
			 boolean pInclude)
       throws IOException, ServletException
  {
    File [] docRoots = getDocumentRootPath();
    FileAttributes f = null;
    FileAttributes directory = null;

    for (int p = 0; p < docRoots.length; p++) {
      f = getBaseFile (pRequest, pPathInfo, docRoots[p]);

      // Make sure the file exists
      //if (!f.canRead ()) f = null;

      if(f != null &&
	 f.isFile()) {
         if(pPathInfo != null && pPathInfo.endsWith(File.separator)) {
            //we were expecting a directory and found a file 
            sendError(pResponse, HttpServletResponse.SC_NOT_FOUND);
            return false;
         }
	  //got a valid file!
	  break;
      } else if (f != null &&
		 f.isDirectory ()) {

        if (pPathInfo.indexOf("..") != -1) {
          sendError(pResponse, 
                    HttpServletResponse.SC_FORBIDDEN, "Cannot serve URLs with ..'s in them");
          return false;
	}

        if (pPathInfo.indexOf(0) != -1) {
          sendError(pResponse, 
                    HttpServletResponse.SC_FORBIDDEN, "Cannot serve URLs with nulls in them");
          return false;
	}

        // Make sure the pathInfo ends with a "/"
	if (pInclude) {
	  if (! pPathInfo.endsWith ("/")) 
	    pPathInfo += "/";
	}
        else if (redirectForSlash (pRequest, pResponse, pPathInfo)) 
	  return false;

        // Search through the index files
        FileAttributes indexFile = null;
	if (getIndexFiles() != null)
	  for (int i = 0; i < getIndexFiles ().length; i++) {
	    FileAttributes ff = getFileAttributes(new File (f.getFile(), getIndexFiles () [i]), true);
	    if ((ff != null) && ff.canRead () && ff.isFile ()) {
	      indexFile = ff;
	      if (pInclude) {  
		pPathInfo += getIndexFiles () [i];
		
		StringBuffer uribuf = new StringBuffer ();
		
		String contextPath = 
		  (String) pRequest.getAttribute(DynamoHttpServletRequest.INCLUDE_CONTEXT_PATH_ATTRIBUTE);
		if (contextPath != null)
		  uribuf.append (contextPath);
		
		String servletPath = 
		  (String) pRequest.getAttribute(DynamoHttpServletRequest.INCLUDE_SERVLET_PATH_ATTRIBUTE);
		
		if (servletPath != null)
		  uribuf.append(servletPath);
		
		uribuf.append(pPathInfo);
		
		String queryString = 
		  (String) pRequest.getAttribute(DynamoHttpServletRequest.INCLUDE_QUERY_STRING_ATTRIBUTE);
		if (queryString != null) {
		  uribuf.append ('?');
		  uribuf.append (queryString); 
		}
		
		pRequest.setAttribute(DynamoHttpServletRequest.INCLUDE_PATH_INFO_ATTRIBUTE,
				      pPathInfo);
		
		pRequest.setAttribute(DynamoHttpServletRequest.INCLUDE_REQUEST_URI_ATTRIBUTE,
				      uribuf.toString());
		break;
	      }
	      else {
		// Set the pathInfo argument
		pRequest.setPathInfo (pRequest.getPathInfo () +
				      getIndexFiles () [i]);
		
		// Set the requestURI argument
		StringBuffer uribuf = new StringBuffer ();
		if (pRequest.getContextPath () != null) 
		  uribuf.append (pRequest.getContextPath ());
		if (pRequest.getServletPath () != null) 
		  uribuf.append (pRequest.getServletPath ());
		if (pRequest.getPathInfo () != null)
		  uribuf.append (pRequest.getPathInfo ());
		pRequest.setRequestURI (uribuf.toString ());
		
		break;
	      }
	    }
	  }
	
        // See if found
        if (indexFile != null) {
          f = indexFile;
        }
        // See if directory listings are turned on
        else if (getShouldListDirectory ()) {
          /* 
           * Stash this directory - if it is not overridden by an index file
           * we'll use it
           */
          if (directory == null) {
            directory = f;
          }
          f = null;
        }
        else f = null;
      } else {
	  //not file or dir
	  f = null;
      }

      /*
       * Found a valid file - get out
       */
      if (f != null) break;
    }

    if (directory != null && f == null) {
      incrementDirectoryListingsServedCount ();

      if (isLoggingDebug ()) {
        logDebug ("Listing directory " + directory);
      }

      listDirectory (directory.getFile(), pRequest, pResponse);
      return false;
    }

    if (f == null) {
      if (getReportFileNotFound()) {
        incrementFileNotFoundCount ();

        if (isLoggingDebug ()) {
          logDebug ("File not found");
        }

        sendError(pResponse, HttpServletResponse.SC_NOT_FOUND);
        return false;
      }
    }
    else {
      if (isLoggingDebug ()) {
        logDebug ("PathTranslated = " + f.getPath ());
      }

      if (pInclude) 
	pRequest.setAttribute (DynamoHttpServletRequest.INCLUDE_PATH_TRANSLATED_ATTRIBUTE,
			       f.getPath());
      else pRequest.setPathTranslated (f.getPath ());
    }

    return true;
  }

  //-------------------------------------
  /**
   * Translates the path.  Returns true if the request is to be passed
   * on, false if not.
   **/
  boolean translateIndexFiles (DynamoHttpServletRequest pRequest,
                         DynamoHttpServletResponse pResponse)
       throws IOException, ServletException
  {
    String path = pRequest.getPathTranslated();

    if (path == null) 
      return true;

    FileAttributes f = getFileAttributes(path);

    // Make sure the file exists
    if (!f.canRead ()) {
      if (getReportFileNotFound()) {
        incrementFileNotFoundCount ();

        if (isLoggingDebug ()) {
          logDebug ("File not found");
        }

        sendError (pResponse, HttpServletResponse.SC_NOT_FOUND);
        return false;
      }
      /*
       * Just pass it on to the next servlet without any errors...
       */
      else return true;
    }

    if (f.isDirectory ()) {
      String pathInfo = pRequest.getPathInfo();
      if (pathInfo.indexOf("..") != -1) {
        sendError(pResponse,
                  HttpServletResponse.SC_FORBIDDEN, "Cannot serve URLs with ..'s in them");
        return false;
      }

      if (pathInfo.indexOf(0) != -1) {
        sendError(pResponse,
                  HttpServletResponse.SC_FORBIDDEN, "Cannot serve URLs with nulls in them");
        return false;
      }

      // Make sure the pathInfo ends with a "/"
      if (redirectForSlash (pRequest, pResponse, pRequest.getPathInfo())) return false;

      // Search through the index files
      // unused File indexFile = null;
      if (getIndexFiles() != null) 
	for (int i = 0; i < getIndexFiles ().length; i++) {
	  FileAttributes ff =
            getFileAttributes(new File (f.getFile(), getIndexFiles () [i]), true);
	  if (ff != null && ff.canRead () && ff.isFile ()) {
	    // Set the pathInfo + path argument
	    pRequest.setPathTranslated (pRequest.getPathTranslated () +
					getIndexFiles () [i]);
	    pRequest.setPathInfo (pRequest.getPathInfo () +
				  getIndexFiles () [i]);
	    
	    // Set the requestURI argument
	    StringBuffer uribuf = new StringBuffer ();
	    if (pRequest.getContextPath () != null) 
	      uribuf.append (pRequest.getContextPath ());
	    if (pRequest.getServletPath () != null) 
	      uribuf.append (pRequest.getServletPath ());
	    if (pRequest.getPathInfo () != null)
	      uribuf.append (pRequest.getPathInfo ());
	    if (pRequest.getQueryString () != null) {
	      uribuf.append ('?');
	      uribuf.append (pRequest.getQueryString ());
	    }
	    pRequest.setRequestURI (uribuf.toString ());
	    
	    return true;
	  }
	}
      
      // See if directory listings are turned on
      if (getShouldListDirectory ()) {
        if (isLoggingDebug ()) {
          logDebug ("Listing directory " + f);
        }

        listDirectory (f.getFile(), pRequest, pResponse);

        incrementDirectoryListingsServedCount ();
        return false;
      }
    }
    return true;
  }

  //-------------------------------------
  /**
   * Returns the basic file obtained by appending path info to doc root
   **/
  FileAttributes getBaseFile (DynamoHttpServletRequest pRequest,
                              String pPathInfo,
                              File pDocRootFile)
  {
    // fix for WebSphere oddity in which pathInfos don't begin
    // with /, when dyn.ear's contextRoot is "/" running under DAF.
    if (!pPathInfo.startsWith("/"))
      pPathInfo = "/" + pPathInfo;
    
    // Get the char array for doing string manipulations
    char [] carray = (char [])
      pRequest.getPermanentAttribute (mCharArrayKey);

    int len;

    // Check for any virtual directory in the pathInfo.
    int vdirIndex = getVDIndex(pPathInfo);

    if (vdirIndex != -1)
    {
      // Get the virtual directory and corresponding actual directory.
      String vdir = mVirtualDirectoryList[vdirIndex];
      String adir = mActualDirectoryList[vdirIndex];

      len = adir.length() + pPathInfo.length() - vdir.length();
      if (len > carray.length)
        carray = new char[len];

      // Append everything following the virtual directory in pathInfo
      // to the corresponding actual directory and put it in carray.
      adir.getChars(0, adir.length(), carray, 0);
      pPathInfo.getChars(vdir.length(), pPathInfo.length(),
                         carray, adir.length());

      if (isLoggingDebug())
        logDebug(pPathInfo + " mapped to " + new String(carray, 0, len));
    }
    else
    {
      // Get the document root
      String docRoot = pDocRootFile.getPath ();
      
      len = pPathInfo.length () + docRoot.length ();
      if (len > carray.length) carray = new char [len];

      // Append the pathInfo to the doc root
      // This assumes that the pathInfo starts with a "/"
      docRoot.getChars (0, docRoot.length (), carray, 0);
      pPathInfo.getChars (0, pPathInfo.length (), carray, docRoot.length ());
    }


    // Convert all "/" to platform-specific
    if (File.separatorChar != '/') {
      for (int i = 0; i < len; i++)
        if (carray [i] == '/') carray [i] = File.separatorChar;
    }

    // Get the file

    String strPath = new String (carray, 0, len);

    return getFileAttributes(strPath);
  }



  /**
    If the given pathInfo starts with a virtual directory, returns
    the index into the virtual directory list.  Otherwise, returns -1.
    */

  int getVDIndex(String pPathInfo)
  {
    if (mVirtualDirectoryList == null)
      return -1;

    // Loop through all the virtual directories, checking for a match
    // with pathInfo.
    for (int i = 0; i < mVirtualDirectoryList.length; i++)
    {

      String vdir = mVirtualDirectoryList[i];
      int vdirLen = vdir.length();

      // We have a virtual directory match in pathInfo only if
      // pathInfo starts with vdir AND it ends at a directory
      // delimiter (i.e., either the end of the string or a slash).
      // This avoids incorrectly identifying a scenario such as
      // vdir=/toys and pathInfo=/toystory/foo, as a virtual path match.
      if (pPathInfo.startsWith(vdir)  &&
          (pPathInfo.length() == vdirLen ||
           pPathInfo.charAt(vdirLen) == '/'))
        return i;

    }


    // If we make it this far, no virtual directories matched.
    return -1;
    
  }
 

  //-------------------------------------
  /**
   * Makes sure that the requested directory ends with a "/".  If not,
   * then a redirect is issued with the slash at the end.  Returns
   * true if a redirect was issued, false if not.
   **/
  boolean redirectForSlash (DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse,
                            String pPathInfo)
       throws IOException, ServletException
  {
    if (pPathInfo.endsWith ("/")) return false;

    // Reconstruct the URI, with a "/"
    StringBuffer url = new StringBuffer ();
    if (pRequest.getContextPath () != null)
      url.append (pRequest.getContextPath());
    if (pRequest.getServletPath () != null) 
      url.append (pRequest.getServletPath ());
    if (pRequest.getPathInfo () != null) 
      url.append (pRequest.getPathInfo ());
    url.append ("/");

    // Tack on any query parameters if they are there.  What about URL
    // parameters?
    String queryString = pRequest.getQueryString();
    if (queryString != null && queryString.length() > 0) {
      url.append('?');
      url.append(queryString);
    }

    if (isLoggingDebug ()) {
      logDebug ("Redirecting locally to \"" + url.toString () + "\"");
    }

    pResponse.sendLocalRedirect (url.toString (), pRequest);
    return true;
  }


 
  //-------------------------------------
  /**
   * Lists the directory from the specified file to the response
   **/
  void listDirectory (File pFile,
                      DynamoHttpServletRequest pRequest,
                      DynamoHttpServletResponse pResponse)
    throws IOException
  {
    /* Get the directory path. */
    String contextPath = pRequest.getContextPath();
    String pathInfo = pRequest.getPathInfo ();
    String dirPath;
    if (contextPath == null) {
      dirPath = pathInfo;
    }
    else {
      dirPath = contextPath + pathInfo;
    }

    /* Based on the browser set the output MIME type 
       and list the directory in either HTML or WML. */

    // URL encoding is hardcoded to ISO-8859-1 (see URLUtils)
    // Should this really be the platform default for filenames ?

    String encoding = "iso-8859-1"; 
    String content;

    if (pRequest.isBrowserType("WML")) {
      content = "text/vnd.wap.wml; charset=" + encoding;
    }
    else {
      content  = "text/html; charset=" + encoding;
    }

    pResponse.setContentType( content );

    PrintWriter out = ((pResponse.getWrapper() != null)
                       ? pResponse.getWrapper().getWriter()
                       : pResponse.getWriter());

    if (pRequest.isBrowserType ("WML")) {
      listDirectoryWMLHeader (out, dirPath, encoding);
      listDirectoryFiles (out, pFile, pRequest, "<p><a href=\"", "</a></p>");
      listDirectoryWMLFooter (out);
    }
    else { 
      ResourceBundle userResourceBundle = getUserResourceBundle (pRequest);
      String title = userResourceBundle.getString ("title");
      if (title != null) {
        Object [] titleArgs = {dirPath};
        title = java.text.MessageFormat.format(title, titleArgs);
      }
      else {
        title = dirPath;
      }
      listDirectoryHTMLHeader (out, title);
      listDirectoryFiles (out, pFile, pRequest, "<a href=\"", "</a>");
      listDirectoryHTMLFooter (out);
    }
  }

  //-------------------------------------
  /** 
   * Get the resource bundle that corresponds to the user's
   * locale.
   **/
  ResourceBundle getUserResourceBundle (DynamoHttpServletRequest pRequest)
  {
    RequestLocale reqLocale = pRequest.getRequestLocale ();
    java.util.Locale userLocale = null;
    if (reqLocale != null)
      userLocale = reqLocale.getLocale ();
    else
      userLocale = java.util.Locale.getDefault ();
    ResourceBundle userResourceBundle
      = atg.core.util.ResourceUtils.getBundle (MY_USER_RESOURCES_NAME, 
                                               userLocale);
    return userResourceBundle;
  }

  //-------------------------------------
  /**
   * Output the HTML header for the directory.
   **/
  void listDirectoryHTMLHeader( PrintWriter out,
                                String      title )
    throws IOException
  {
    out.println ("<html><head>");
    out.println ("<title>" + title + "</title></head>");
    out.println ("<body><h1>" + title + "</h1>");
    out.println ("<pre>");
  }


  //-------------------------------------
  /**
   * Output the WML header for the directory.
   **/
  void listDirectoryWMLHeader (PrintWriter out,
                               String      title,
                               String      encoding )
    throws IOException
  {
    out.println ("<?xml version=\"1.0\" encoding=\" + encoding + \"?>");
    out.println ("<!DOCTYPE wml PUBLIC \"-//WAPFORUM//DTD WML 1.1//EN\" \"http://www.wapforum.org/DTD/wml_1.1.xml\">");
    out.println("<wml>");
    out.println("<card id=\"dir\" title=\"" + title + "\">");
  }


  //-------------------------------------
  /**
   * Output the files for the directory.
   **/
  void listDirectoryFiles (PrintWriter out,
                           File pFile,
                           DynamoHttpServletRequest pRequest,
                           String linkBegin,
                           String linkEnd)
    throws IOException
  {
    // Walk through the file listings
    String [] files = pFile.list ();
    if (files == null) return;
    for (int i = 0; i < files.length; i++) {
      File f = new File (pFile, files [i]);
      String name = files [i];
      if (f.isDirectory ()) name += '/';
      String fullname = pRequest.getPathInfo ();
      if (pRequest.getServletPath() != null)
        fullname = pRequest.getServletPath() + fullname;
      if (pRequest.getContextPath() != null)
        fullname = pRequest.getContextPath() + fullname;
      if (!fullname.endsWith ("/")) fullname = fullname + '/' + name;
      else fullname = fullname + name;

      // explicitly escape path here to keep changes local for now
      // the encodeURL routines should really escape all paths
      // if they do, then must remove this, cannot escape twice
      fullname = atg.core.net.URLUtils.escapeUrlString( fullname );

      if (isLoggingDebug())
        logDebug (fullname);
      out.println (linkBegin + pRequest.encodeURL(fullname) + 
                   "\">" + name + linkEnd);
    }
  }

  //-------------------------------------
  /**
   * Output the HTML footer for the directory.
   **/
  void listDirectoryHTMLFooter (PrintWriter out)
    throws IOException
  {
    out.println ("</pre>");
    out.println ("</body></html>");
  }

  //-------------------------------------
  /**
   * Output the WML footer for the directory.
   **/
  void listDirectoryWMLFooter (PrintWriter out)
    throws IOException
  {
    out.println ("</card>");
    out.println ("</wml>");
  }

  private final void sendError(DynamoHttpServletResponse pResponse, int pCode)
    throws IOException
  {
    String errorURL = getErrorURL ();
    if (errorURL != null)
      pResponse.sendRedirect (errorURL);
    else
      pResponse.sendError (pCode);
  }

  private final void sendError(DynamoHttpServletResponse pResponse,
                               int pCode, String pMessage)
    throws IOException
  {
    String errorURL = getErrorURL ();
    if (errorURL != null)
      pResponse.sendRedirect (errorURL);
    else
      pResponse.sendError (pCode, pMessage);
  }

  //-------------------------------------
}
