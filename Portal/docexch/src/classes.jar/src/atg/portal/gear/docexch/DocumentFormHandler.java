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

import java.io.*;
import java.util.*;
import javax.jms.*;
import javax.naming.InitialContext;
import javax.transaction.*;

import atg.servlet.*;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import javax.servlet.ServletException;
import atg.repository.RepositoryItem;
import atg.repository.*;
import atg.nucleus.Nucleus;
import atg.userprofiling.Profile;
import atg.portal.framework.*;
import atg.portal.alert.*;
import atg.beans.*;
import atg.portal.nucleus.NucleusComponents;
import atg.core.util.StringUtils;

import atg.repository.servlet.RepositoryFormHandler;

/**
 * This is a form handler for manipulating arbitrary repository items.  In
 * addition to the basic repository item create/edit/delete functionality provided
 * by the superclass RepositoryFormHandler, this form handler can take an uploaded
 * file object and put the file's data, size, mimetype, and author into the
 * properties that are configured to hold that information and compare the
 * file's size to a maximum allowable size, rejecting the file if it is too big.
 * <P>
 * Many properties of this form handler are optional.  Leaving the properties
 * unset will result in reduced functionality which may be appropriate for some
 * repositories.  Although the document exchange gear is really all about storing
 * documents, this form handler can be used with non-document repository items.
 * <P>
 * Properties:
 * <DL>
 * <DT> uploadedFile
 * <DD> This is the object to be uploaded via a form field like this:
 *      &lt;dsp:input type="file" bean="DocumentFormHandler.uploadedFile"/&gt;
 *      If this property is null, then the properties indicated by
 *      fileDataPropertyName, fileSizePropertyName, and mimeTypePropertyName
 *      will not be set.
 *
 * <DT> profile
 * <DD> This is the profile of the current user.  In general this will be set
 *      to /atg/userprofiling/Profile.  The value of this property will be
 *      filled into the repository item's property indicated by authorPropertyName.
 *      If this property is unset, then there will be no attempt to set the
 *      author property of the repository item.  Note that profile and
 *      authorPropetyName must both be set in order for the automatic setting
 *      of author to the current profile to work.
 *
 * <DT> maxFileSize
 * <DD> The maximum allowed file size for uploaded files. The file will be
 *      uploaded before we can check the size, but files that are too large
 *      will be rejected before they are stored in the repository.  The
 *      default value is -1 which indicates that there is no size limit imposed.
 *
 * <DT> fileSizePropertyName
 * <DD> The name of the property that holds the file size in the repository
 *      item being managed by this form handler.  The data type of the
 *      property should be "int".  If this property is null, then the file
 *      size property of the repository item will not be set.
 *
 * <DT> fileDataPropertyName
 * <DD> The name of the property that holds the file data in the repository
 *      item being managed by this form handler.  The data type of the property
 *      should be "binary".  If this property is null, then the file data
 *      property will not be set.
 *
 * <DT> mimeTypePropertyName
 * <DD> The name of the property that holds the mimetype of the file in the
 *      repository item being managed by this form handler.  The data type of
 *      the property should be "string".  If this property is null, then the
 *      mimetype property will not be set.
 *
 * <DT> authorPropertyName
 * <DD> The name of the property that holds the author of the file in the
 *      repository item being managed by this form handler.  The data type of
 *      the property should be "repositoryItem".  If this property is null,
 *      the author property will not be set.
 *
 * <DT> filenamePropertyName
 * <DD> The name of the property that holds the filename in the
 *      repository item being managed by this form handler.  The data type of
 *      the property should be "string".  If this property is null,
 *      the filename property will not be set.
 *
 * @see atg.repository.servlet.RepositoryFormHandler
 * @author Cynthia Harris
 * @version $Id: //app/portal/version/10.0.3/docexch/classes.jar/src/atg/portal/gear/docexch/DocumentFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class DocumentFormHandler
extends RepositoryFormHandler
{
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/docexch/classes.jar/src/atg/portal/gear/docexch/DocumentFormHandler.java#2 $$Change: 651448 $";

  /** The repository ID of the forum associated with the document. */
  protected String mForumId = null;

  /** The deleted message.  Used in preDeleteItem(). */
  protected DocumentDeletedMessage mDeletedMessage = null;

  /**
   * This property holds the uploaded file.
   */
  private UploadedFile mUploadedFile = null;
  public void setUploadedFile( UploadedFile pUploadedFile )
  {
    if (isLoggingDebug())
      logDebug("in set uploaded file!");
    mUploadedFile = pUploadedFile;
  }
  public UploadedFile getUploadedFile()
  {
    return mUploadedFile;
  }

  //-------------------------------------
  // property: profile
  Profile mProfile;
  public void setProfile(Profile pProfile) {
    mProfile = pProfile;
  }
  public Profile getProfile() {
    return mProfile;
  }

  //-------------------------------------
  // property: fileSizePropertyName
  String mFileSizePropertyName;
  public void setFileSizePropertyName(String pFileSizePropertyName) {
    mFileSizePropertyName = pFileSizePropertyName;
  }
  public String getFileSizePropertyName() {
    return mFileSizePropertyName;
  }

  //-------------------------------------
  // property: fileDataPropertyName
  String mFileDataPropertyName;
  public void setFileDataPropertyName(String pFileDataPropertyName) {
    mFileDataPropertyName = pFileDataPropertyName;
  }
  public String getFileDataPropertyName() {
    return mFileDataPropertyName;
  }

  //-------------------------------------
  // property: mimeTypePropertyName
  String mMimeTypePropertyName;
  public void setMimeTypePropertyName(String pMimeTypePropertyName) {
    mMimeTypePropertyName = pMimeTypePropertyName;
  }
  public String getMimeTypePropertyName() {
    return mMimeTypePropertyName;
  }

  //-------------------------------------
  // property: authorPropertyName
  String mAuthorPropertyName;
  public void setAuthorPropertyName(String pAuthorPropertyName) {
    mAuthorPropertyName = pAuthorPropertyName;
  }
  public String getAuthorPropertyName() {
    return mAuthorPropertyName;
  }

  //-------------------------------------

  String mAnnotationRefPropertyName;
  public String getAnnotationRefPropertyName()
  {
      return mAnnotationRefPropertyName;
  }

  public void setAnnotationRefPropertyName(String pAnnotationRefPropertyName)
  {
      mAnnotationRefPropertyName = pAnnotationRefPropertyName;
  }

  //-------------------------------------
  // property: filenamePropertyName
  String mFilenamePropertyName;
  public void setFilenamePropertyName(String pFilenamePropertyName) {
    mFilenamePropertyName = pFilenamePropertyName;
  }
  public String getFilenamePropertyName() {
    return mFilenamePropertyName;
  }

  //-------------------------------------
  // property: maxFileSizeBytes
  int mMaxFileSizeBytes = -1;
  public void setMaxFileSizeBytes(int pMaxFileSizeBytes) {
    mMaxFileSizeBytes = pMaxFileSizeBytes;
  }
  public int getMaxFileSizeBytes() {
    return mMaxFileSizeBytes;
  }

    /** maximum num characters allowed in the description field */
    int mMaxDescriptionLength;

    //-------------------------------------
    /**
     * Sets maximum num characters allowed in the description field
     **/
    public void setMaxDescriptionLength(int pMaxDescriptionLength) {
        mMaxDescriptionLength = pMaxDescriptionLength;
    }

    //-------------------------------------
    /**
     * Returns maximum num characters allowed in the description field
     **/
    public int getMaxDescriptionLength() {
        return mMaxDescriptionLength;
    }

  //-------------------------------------
  // property: discussionManager

  DiscussionManager mDiscussionManager;
  public DiscussionManager getDiscussionManager()
  {
      return mDiscussionManager;
  }

  public void setDiscussionManager(DiscussionManager pDiscussionManager)
  {
      mDiscussionManager = pDiscussionManager;
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
		mPublisher = (GearMessagePublisher) NucleusComponents.lookup("dynamo:/atg/portal/alert/GearMessagePublisher");
	}
    return mPublisher;
  }


  //-------------------------------------
  /**
   * This method is called just after the updateItemProperties is completed.
   * In this method, we set the value of the property indicated by
   * getAuthorPropertyName() to the current user's profile.
   * <P>
   * If either the authorPropertyName or profile properties of this class
   * is null, then the method will do nothing.
   *
   * @param pItem the item being updated
   * @exception ServletException if there was an error while executing
   * the code
   * @exception IOException if there was an error with servlet io
   **/
  protected void postUpdateItemProperties(MutableRepositoryItem pItem)
      throws ServletException, IOException
  {
    if (getAuthorPropertyName() != null && getProfile() != null)
      pItem.setPropertyValue(getAuthorPropertyName(), (RepositoryItem)(getProfile().getDataSource()));
  }

  //--------------------------------------------
  /**
   * This method sets url params such that when you cancel out of a delete, you
   * get back to the place you came from rather than going to the full list.
   *
   */
  public boolean handleCancelDelete(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {

    pRequest.setParameter("documentid", getRepositoryId());
    pRequest.setParameter("dexmode", "oneItem");
    return true;
  }

  /**
   * Operation called just before the item creation process is started.
   * This will take information from the uploadedFile object and put
   * it into the properties of the superclass's value Dictionary property.
   * <P>
   * It will also check that the file does not exceed the maximum size.
   * <P>
   * If there is no uploaded file, this method will do nothing.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void preCreateItem(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    // don't allow anonymous document creation.
    if (getProfile() == null || getProfile().isTransient()) {
      addFormException( new DropletException(getAbsoluteName() + ".value.author", "NO_ANONYMOUS_CREATION"));
    }
    
    checkDescriptionLength();


    if (!getFormError())        
        getDataFromUploadedFile(true);

  }

  /**
   * Operation called just before the item update process is started.
   * This will take information from the uploadedFile object and put
   * it into the properties of the superclass's value Dictionary property.
   *
   * It will also check that the file does not exceed the maximum size.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void preUpdateItem(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    checkDescriptionLength();
    if (!getFormError())        
       getDataFromUploadedFile(false);
  }

  /**
   * Operation called just before the item is deleted.  Gets the discussion id
   * of the document (if any) and stores that for deletion.
   *
   * Gets the needed values for a deleted message and stores that for postDeleteItem
   */
  protected void preDeleteItem(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    String forumPropertyName = getAnnotationRefPropertyName();
    mForumId = (String) getValueProperty(forumPropertyName);

    //
    // Create the document deleted message, set its properties, and send it.
    //
    GearEnvironment gearEnv = getGearEnvironment();
    DocumentDeletedMessage message = new DocumentDeletedMessage(gearEnv);

    message.setDocumentId(getRepositoryId());
    message.setDocumentName(getDocumentName(gearEnv));
    message.setProfileId(getProfileRepositoryId());
    message.setPageId(getPageRepositoryId(gearEnv));
    message.setCurrentUserName(getProfileDisplayName());

    mDeletedMessage = message;

    // delete any references to this item
    try {
        atg.adapter.util.RepositoryUtils.removeReferencesToItem(getRepositoryItem());
    }
    catch (RepositoryException e) {
        if (isLoggingError())
            logError(e);
        addFormException( new DropletException(e.getMessage(), "errorDeletingItem"));
      
    }
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
    	    logDebug("sendDocExchMessage: type=" + pMessage.getMessageType());
            logDebug("gear item = " + pMessage.getGearId());
            logDebug("community item = " + pMessage.getCommunityId());
          }

    	  publisher.writeMessage(pMessage);
    	} else
    	{
    	    if (isLoggingError())
    	        logError("Unable to locate GearMessagePublisher component - send alert will not be possible");
    	}
    } catch( JMSException jmse ) {
            if (isLoggingError())
                logError(jmse);
            addFormException( new DropletException( jmse.getMessage(), "JMSE"));
    } catch ( javax.naming.NamingException namex) {
            if (isLoggingError())
                logError(namex);
    }

  }

  //-------------------------------------
  /**
   * This method is called after the superclass completes the
   * creation of a new document. It will be responsible for
   * sending messages.
   */
  public void postCreateItem(DynamoHttpServletRequest pReq,
                             DynamoHttpServletResponse pRes)
  {
    if (!getFormError()) {
        if (isLoggingDebug())
            logDebug("This is postCreateItem. Messages will be generated here.");
        
        GearEnvironment gearEnv = getGearEnvironment();
        
        //
        // Create the document created message, set its properties, and send it.
        //
        DocumentCreatedMessage message = new DocumentCreatedMessage(gearEnv);
        
        message.setDocumentName(getDocumentName(gearEnv));
        message.setProfileId(getProfileRepositoryId());
        message.setPageId(getPageRepositoryId(gearEnv));
        message.setAuthorName(getProfileDisplayName());
        
        sendDocExchMessage(message);
        endTransaction();
    }
  }

  //-------------------------------------
  /**
   * This method is called after the superclass completes the
   * update of an existing document. It will be responsible for
   * sending messages.
   */
  public void postUpdateItem(DynamoHttpServletRequest pReq,
                             DynamoHttpServletResponse pRes)
  {
    if (!getFormError()) {
      if (isLoggingDebug())
          logDebug("This is postUpdateItem. Messages will be generated here.");

      GearEnvironment gearEnv = getGearEnvironment();
    
      if (isLoggingDebug()) {
          logDebug("community = " + gearEnv.getCommunity().getName());
          logDebug("page = " + gearEnv.getPage().getName());
      }
      
      //
      // Create the document updated message, set its properties, and send it.
      //
      DocumentUpdatedMessage message = new DocumentUpdatedMessage(gearEnv);
      
      message.setDocumentId(getRepositoryId());
      message.setDocumentName(getDocumentName(gearEnv));
      message.setProfileId(getProfileRepositoryId());
      message.setPageId(getPageRepositoryId(gearEnv));
      message.setCurrentUserName(getProfileDisplayName());
      
      sendDocExchMessage(message);
      endTransaction();
    }
  }

  //-------------------------------------
  /**
   * Deletes the forum associated with the deleted document using
   * DiscussionManager, and sends a DocumentDeletedMessage using values
   * garnered from preDeleteItem.
   */
  public void postDeleteItem(DynamoHttpServletRequest pReq,
                             DynamoHttpServletResponse pRes)
  {
    if (!getFormError()) {
      if (isLoggingDebug())
          logDebug("This is postDeleteItem. Messages will be generated here.");

      GearEnvironment gearEnv = getGearEnvironment();
      
      // Look up the forums associated with this document.
      if (isLoggingDebug()) {
          logDebug("community = " + gearEnv.getCommunity().getName());
          logDebug("page = " + gearEnv.getPage().getName());
          logDebug("forum id = " + mForumId);
      }
      
      if (mForumId != null) {
          DiscussionManager discManager = getDiscussionManager();
          if (discManager != null)
          {
              discManager.deleteForum(mForumId);
          } else // log a warning if we can't get the discussion manager.
          {
              if (isLoggingDebug())
              {
                  String msg = "Cannot find discussion manager, skipping deleteForum...";
                  logDebug(msg);
              }
          }
      }
      
      // Send a deleted message (this is called earlier by preDeleteItem).
      if (mDeletedMessage != null)
      {
          sendDocExchMessage(mDeletedMessage);
      }
      endTransaction();
    }
  }

  /**
   * Called when a form is rendered that references this bean.  This call
   * is made before the service method of the page is invoked.
   */
    public boolean beforeSet(DynamoHttpServletRequest request,
                             DynamoHttpServletResponse response)
        throws DropletFormException {
        
        initGearEnvironment(request, response);
        super.beforeGet(request, response);
        return true;
    }
    

    /** a file name supplied by the user that will override the actual uploaded filename */
    String mOverrideFileName;

    //-------------------------------------
    /**
     * Sets a file name supplied by the user that will override the actual uploaded filename
     **/
    public void setOverrideFileName(String pOverrideFileName) {
        mOverrideFileName = pOverrideFileName;
    }

    //-------------------------------------
    /**
     * Returns a file name supplied by the user that will override the actual uploaded filename
     **/
    public String getOverrideFileName() {
        return mOverrideFileName;
    }

    
  //------------------------------------------------------
  //   Protected & Private methods
  //------------------------------------------------------
  /**
   * Operation called just before the item creation process is started.
   * This will take information from the uploadedFile object and put
   * it into the properties of the superclass's value Dictionary property.
   * <P>
   * It will also check that the file does not exceed the maximum size.
   * <P>
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void getDataFromUploadedFile(boolean pCreate)
       throws ServletException, IOException
  {

    boolean wasNewFileUploaded = true;
    if (getUploadedFile() == null || getUploadedFile().getFileSize() == 0)
        wasNewFileUploaded = false;
    
    String filename = null;

    // File upload may be required for new document creation
    if (pCreate && 
        !wasNewFileUploaded && 
        Boolean.valueOf(getGearEnvironment().getGearInstanceParameter("attachmentRequired")).booleanValue()) {
        addFormException( new DropletException(getAbsoluteName() + ".value." + "uploadedFile", "FILE_REQUIRED"));
        return;        
    }

    if (wasNewFileUploaded) {
        // if size of uploaded file is 0, act like no file was uploaded.
        int fileSize = getUploadedFile().getFileSize();
        if (fileSize == 0) {
            wasNewFileUploaded = false;
            if (pCreate) {
                addFormException( new DropletException(getAbsoluteName() + ".value." + "uploadedFile", "FILE_REQUIRED"));
                return;
            }
        }
        else {

            // check if file is too big
            if (isLoggingDebug())
                logDebug("File size = " + fileSize + " max size (MB)= " + getMaxFileSizeBytes());
            if (getMaxFileSizeBytes() != -1 && fileSize > (getMaxFileSizeBytes() * 1000000)) {
                if (isLoggingDebug())
                    logDebug("File larger than max size.  Rejecting");
                addFormException( new DropletException(getAbsoluteName() + ".value." + "uploadedFile", "FILE_TOO_BIG"));
                return;
            }
            
            // check if there is any "undesirable" content in the file 
            validateFileContents(getUploadedFile());
            if (getFormError())
                return;

            String fileSizePropertyName = getFileSizePropertyName();
            if ( ! StringUtils.isBlank(fileSizePropertyName) )
                setValueProperty(fileSizePropertyName, new Integer( fileSize));
            
            String mimeTypePropertyName = getMimeTypePropertyName();
            if ( ! StringUtils.isBlank(mimeTypePropertyName) )
                setValueProperty(mimeTypePropertyName, getMimeType(getUploadedFile()));
            
            String fileDataPropertyName = getFileDataPropertyName();
            if ( ! StringUtils.isBlank(fileDataPropertyName) ) {
                // reading the file data can throw an IOException
                try {
                    byte[] fileData =  mUploadedFile.toByteArray();
                    setValueProperty(fileDataPropertyName, fileData);
                }
                catch( IOException ioe ) {
                    addFormException( new DropletException(getAbsoluteName() + ".value." + "uploadedFile",
                                                           "IOE"));
                }
            }
        }
    }

    // fill in the filename property with the actual filename or override filename
    String filenamePropertyName = getFilenamePropertyName();
    if ( ! StringUtils.isBlank(filenamePropertyName) ) {
        
        // if an override file name was specified, use that.
        filename = getOverrideFileName();
        
        // no override file name?  then get it from the uploaded file
        if (wasNewFileUploaded && (filename == null || filename.trim().length() == 0)) {
            String fullfilename = mUploadedFile.getFilename();
            
            // strip off the path (do both \ and / because browser & server can have different OSs)
            int ix = fullfilename.lastIndexOf('\\');
            filename = fullfilename;
            if (ix != -1)
                filename = fullfilename.substring(ix + 1);
            
            ix = filename.lastIndexOf('/');
            if (ix != -1)
                filename = filename.substring(ix + 1);
        }
        if (!StringUtils.isBlank(filename))
            setValueProperty(filenamePropertyName, filename);
    }
        
    // fill in the title with the filename if no title is given
    String titleProperty = getGearEnvironment().getGearInstanceParameter("titlePropertyName");
    String title = (String)getValueProperty(titleProperty);
    if (StringUtils.isBlank(title) && !StringUtils.isBlank(filename)) {
        setValueProperty(titleProperty, filename);            
    }


  }

    /**
     * Extending classes may override this method to reject an uploaded document based on any criteria.  
     * Examples include virus scanning, looking for "dirty words", or whatever the application requires. 
     * To reject a document add an exception to the form, as in this example:
     * addFormException( new DropletException(getAbsoluteName() + ".value." + "uploadedFile", "FILE_CONTENT_REJECTED"));
     */
    public void validateFileContents(UploadedFile pUploadedFile) {
        // no op
    }

    void checkDescriptionLength() {
        String descProperty = getGearEnvironment().getGearInstanceParameter("descriptionPropertyName");
        if (descProperty != null) {
            String description = (String)getValueProperty(descProperty);
            if (description != null && description.length() > getMaxDescriptionLength())
                addFormException( new DropletException(getAbsoluteName() + ".value." + 
                   descProperty,"DESCRIPTION_TOO_LONG"));
        }
    }


  /**
   * This method gets the mime-type from the UploadedFile. If uploaded file
   * does not know its mime-type then application/octet-stream is returned.
   */
  String getMimeType( UploadedFile pUploadedFile )
  {
    String mimeType = pUploadedFile.getContentType();
    if ( mimeType != null ) {
      return mimeType;
    }
    else {
      if (isLoggingDebug())
          logDebug( "Encountered a file that does not know its content type.  Setting mime type to application/octet-stream.");
      return "application/octet-stream";
    }
  }


    /** the gear environment */
    GearEnvironment mGearEnvironment;

    //-------------------------------------
    /**
     * Sets the gear environment
     **/
    public void setGearEnvironment(GearEnvironment pGearEnvironment) {
        mGearEnvironment = pGearEnvironment;
    }

    //-------------------------------------
    /**
     * Returns the gear environment
     **/
    public GearEnvironment getGearEnvironment() {
        return mGearEnvironment;
    }

    /**
     * Gets the gear environment from a servlet request.
     */
    void initGearEnvironment (DynamoHttpServletRequest pReq,
                              DynamoHttpServletResponse pResp)
    {
        pReq.setAttribute(RequestAttributes.GEAR, pReq.getParameter("paf_gear_id"));
        pReq.setAttribute(RequestAttributes.COMMUNITY, pReq.getParameter("paf_community_id"));
        pReq.setAttribute(RequestAttributes.PAGE, pReq.getParameter("paf_page_id"));
        pReq.setAttribute(RequestAttributes.PORTAL_REPOSITORY_LOCATION, "dynamo:/atg/portal/framework/PortalRepository");

        GearEnvironment env = null;
        
        try {
            setGearEnvironment(EnvironmentFactory.getGearEnvironment(pReq, pResp));
        }
        catch (EnvironmentException e) {
            if (isLoggingError())
                logError(e);
        }
        
    }
    
  /**
   * Utility method to return the Profile's Repository Id. Returns null
   * if the Profile is null.
   */
  private String getProfileRepositoryId()
  {
    if (getProfile() != null)
      return ((RepositoryItem)getProfile()).getRepositoryId();

    return null;
  }

  /**
   * Utility method to retrieve the uploaded file's name. Returns null
   * if uploaded file is null.
   */
  private String getDocumentName(GearEnvironment pGearEnv)
  {
    return getPropertyValue(pGearEnv, "titlePropertyName");
  }

  /**
   * Utility method to retrieve a property's value as a string.
   * @param pPropertyName the name of the property to retrieve from getValue().
   * @param pGearEnv the gear environment.
   */
  private String getPropertyValue(GearEnvironment pGearEnv, String pPropertyName)
  {
     try {
        String propertyName = pGearEnv.getGearInstanceParameter(pPropertyName);
        Object value = DynamicBeans.getSubPropertyValue(getValue(), propertyName);
        if (value == null)
            return null;
        return value.toString();
     }
     catch (PropertyNotFoundException e) {
         return null;
     }
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

  /**
   * Utility method to return the Profile's display name which is
   * typically the user id.
   */
  private String getProfileDisplayName()
  {
    if (getProfile() != null)
      return ((RepositoryItem)getProfile()).getItemDisplayName();

    return null;
  }

  /**
   * Since each portal page is wrapped in a transaction, we have to bring
   * our transactions to an artificial close.
   */
  void endTransaction()
  {
    try {
      if (getRepository() instanceof RepositoryImpl)
        ((RepositoryImpl) getRepository()).getTransactionManager().getTransaction().commit();
    }
    catch (RollbackException exc) {}
    catch (HeuristicMixedException exc) {}
    catch (HeuristicRollbackException exc) {}
    catch (SystemException exc) {
        if (isLoggingError())
            logError(exc);
    }
  }

}
