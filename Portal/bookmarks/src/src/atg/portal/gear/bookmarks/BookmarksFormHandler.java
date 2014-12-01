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
 * Dynamo is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.portal.gear.bookmarks;

import atg.portal.framework.*;

import atg.dtm.*;
import atg.adapter.gsa.*;
import atg.repository.*;
import atg.repository.rql.*;

import atg.servlet.*;
import atg.droplet.*;
import atg.userprofiling.*;
import atg.core.util.StringUtils;

import java.util.*;
import java.io.IOException;
import javax.servlet.ServletException;

/**
 * This form handler manages bookmarks.  It handles adds, edits and deletes
 * for all the bookmarks in a specific gear.
 *
 * @author Will Sargent
 * @version $Id: //app/portal/version/10.0.3/bookmarks/src/atg/portal/gear/bookmarks/BookmarksFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class BookmarksFormHandler extends TransactionalFormHandler
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/bookmarks/src/atg/portal/gear/bookmarks/BookmarksFormHandler.java#2 $$Change: 651448 $";

    //-------------------------------------
    // Constants
    //-------------------------------------

    /** The name of the gear user parameter for the root folder. */
    private static final String FOLDER_ID_PARAM = "bmgFolderId";

    /**
     * The location of the portal repository.
     */
    public static String PORTAL_REPOSITORY =
    "dynamo:/atg/portal/framework/PortalRepository";

    /** Bookmark folder repository item. */
    private static final String FOLDER_ITEM = "bookmark-folder";

    /** Bookmark repository item.  */
    private static final String BOOKMARK_ITEM = "bookmark";

    /*
     * Properties of the items defined above.
     */
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String LINK = "link";
    private static final String CHILDREN = "children";
    private static final String GEAR_ID = "gearId";

    /**
     * The HTTP protocol string "http://"
     */
    public static final String HTTP_PREFIX = "http://";

    /**
     * The RQL statement which determines the folder id.
     */
    protected static RqlStatement mFolderStatement;
    static {
      try {
	mFolderStatement = RqlStatement.parseRqlStatement("id = ?0");
      }
      catch(RepositoryException e) {
      }
    }

    //-------------------------------------
    // property: names
    private List mNames = new ArrayList(10);

    public String getNames(int pIndex)
    {
        return (String) mNames.get(pIndex);
    }

    public void setNames(int pIndex, String pName)
    {
        mNames.add(pIndex, pName);
    }

    //-------------------------------------
    // property: links
    private List mLinks = new ArrayList(10);

    public void setLinks(int pIndex, String pLink)
    {
        mLinks.add(pIndex, pLink);
    }

    public String getLinks(int pIndex)
    {
        return (String)mLinks.get(pIndex);
    }

    //-------------------------------------
    // property: LinkIds
    private List mIds = new ArrayList(10);

    public void setLinkIds(int pIndex, String pId)
    {
        mIds.add(pIndex, pId);
    }

    public String getLinkIds(int pIndex)
    {
        return (String)mIds.get(pIndex);
    }

    //-------------------------------------
    // property: repository

    Repository mRepository;
    public Repository getRepository()
    {
        return mRepository;
    }

    public void setRepository(Repository pRepository)
    {
        mRepository = pRepository;
    }

    //-------------------------------------

    String mFailureURL;
    /**
     * Gets the url to redirect to if there was an error editing the form.
     */
    public String getFailureURL()
    {
        return mFailureURL;
    }

    /**
     * Sets the URL to redirect to if there was an error editing the form.
     */
    public void setFailureURL(String pFailureURL)
    {
        mFailureURL = pFailureURL;
    }

    //---------------------------------------

    String mSuccessURL;
    /**
     * Gets the url to redirect when the user clicks on submit button of the form.
     */
    public String getSuccessURL()
    {
        return mSuccessURL;
    }

    /**
     * Sets the URL to redirect to on success.
     */
    public void setSuccessURL(String pSuccessURL)
    {
        mSuccessURL = pSuccessURL;
    }

    //---------------------------------------

    private String mMoreURL;
    /**
     * Sets the url to redirect when the user wants more links.
     */
    public void setMoreURL( String pMoreURL )
    {
        mMoreURL = pMoreURL;
    }

    /**
     * Gets the url to redirect when the user wants more links.
     */
    public String getMoreURL()
    {
        return mMoreURL;
    }

    /**
     * Calls setEnsureTransaction to make sure a transaction is enabled on
     * this form.
     */
    public void doStartService()
    {
        setEnsureTransaction(true);
    }

    //-------------------------------------
    // property:  formMessages
    List mFormMessages = new Vector();
    
    /**
     * Sets property formMessages
     * @param pFormMessages collection of form messages to be displayed by form.
     * @beaninfo description:  collection of form messages to be displayed by form.
     **/
    public void setFormMessages(List pFormMessages) {
        mFormMessages = pFormMessages;
    }
    
    /**
     * Returns property formMessages
     * @return The value of the property formMessages.
     **/
    public List getFormMessages() {
        return mFormMessages;
    }
    
    //-------------------------------------
    // property: formInfo
    boolean mFormInfo = false;
    
    /**
     * Sets property formInfo
     * @param pFormInfo boolean to specify whether information messages are present.
     * @beaninfo description:  boolean to specify whether information messages are present.
     **/
    public void setFormInfo(boolean pFormInfo) {
        mFormInfo = pFormInfo;
    }
    
    /**
     * Returns property formInfo
     * @return The value of the property formInfo.
     **/
    public boolean getFormInfo() {
        return mFormInfo;
    }

    //-------------------------------------
    /**
     * Compares the entries in the forms with the repository items in the
     * database and syncs the repository to the values in the forms.
     */
    public boolean handleSubmit(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse)
        throws IOException, ServletException
    {
        try
        {
            syncBookmarks(pRequest, pResponse);
            success(Constants.EDIT_SUCCESS);
        } catch (RepositoryException re)
        {
            error(re);
        }

        if (!checkFormRedirect(getSuccessURL(), getFailureURL(), pRequest, pResponse))
            return false;

        return true;
    }

    //-------------------------------------
    /**
     * Handles more bookmarks.
     */
    public boolean handleMore(DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse)
        throws ServletException, IOException
    {
        // First, let's save all the URLs which currently exist in the form.
        try
        {
            syncBookmarks(pRequest, pResponse);
        } catch (RepositoryException re)
        {
            error(re);
        }

        // Then redirect to a page which has five more bookmarks.
        if (!checkFormRedirect(getMoreURL(), null, pRequest, pResponse))
            return false;

        return true;
    }

    //-------------------------------------
    /**
    We have three arrays: ids, names, and links.  All the arrays
    are set by the form handler on entry, and are indexed together.
    <p>
    The first thing to do is match up the repository ids in the form
    with the ids in the database.
    <p>
    If the id is blank, then we know the most we have to do is a create.
    If the id is NOT blank, then we'll have to check if we're editing the
    bookmark or deleting it altogether.
    <p>
    Despite this being a list, if the bookmark is created after several
    "blank" form entries, it will get added directly behind the last
    bookmark entry.

    @throws
    RepositoryException if data in the form could not be synced
    with the repository.
    */

    protected void syncBookmarks(DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse)
        throws RepositoryException
    {
        GearEnvironment gearEnv = getGearEnvironment(pRequest, pResponse);
        String folderId = getFolderId(gearEnv);

        if (StringUtils.isBlank(folderId))
        {
            if (isLoggingError())
            {
                String msg = Constants.format(Constants.NULL_FOLDER_ID, folderId);
                logError(msg);
            }
            return;
        }

        RepositoryItem[] folders = getFolders(folderId);
        if (folders == null || folders.length == 0)
        {
            String msg = Constants.format(Constants.NULL_FOLDER_ID, folderId);
            error(msg);
            return;
        }

        RepositoryItem root = folders[0];
        List children = (List) root.getPropertyValue(CHILDREN);

        String[] ids = (String[]) mIds.toArray(new String[0]);
        String[] links = (String[]) mLinks.toArray(new String[0]);
        String[] names = (String[]) mNames.toArray(new String[0]);

        if (isLoggingDebug())
           logDebug("There are " + ids.length + " rows.");

        for (int i = 0; i < ids.length; i++)
        {
            String id = ids[i];
            String link = links[i];

            if (StringUtils.isBlank(id))
            {
                // We have no id.  If the link field in this row is valued,
                // we create a new entry in the list.
                if (! StringUtils.isBlank(links[i]) )
                {
                    RepositoryItem bookmark = createBookmark(names[i], link);
                    children.add(bookmark);
                }
            } else // we have an id which may have an associated link.
            {
                // Let's see what the value of the link is.  If the link is
                // empty, then this is a message to remove the bookmark from
                // the list (and remove it from its repository view).

                if ( StringUtils.isBlank(link))
                {
                    removeBookmark(id, children, root);
                } else
                {
                    updateBookmark(id, names[i], link);
                }
            }
        }

    }

    //-------------------------------------
    /**
     * Creates a bookmarks repository item and adds it to the repository
     * before returning it.
     */
    public RepositoryItem createBookmark(String pName, String pLink)
           throws RepositoryException
    {
        String link = checkLinkHasPrefix(pLink);
        if (isLoggingDebug())
           logDebug("creating bookmark: [" + pName + "], [" + link + "]");

        MutableRepository mr = (MutableRepository) getRepository();
        MutableRepositoryItem bookmark = mr.createItem(BOOKMARK_ITEM);

        // If there is no name, then the default is to make the bookmark
        // name equal the link.
        String name = pName;
        if (StringUtils.isBlank(name))
        {
          name = pLink;
          if (name.length() > 100)
          {
            name = name.substring(0,100);
          }
        }

        bookmark.setPropertyValue(NAME, name);
        bookmark.setPropertyValue(LINK, link);

        // Add the bookmark.
        mr.addItem(bookmark);

        return bookmark;
    }

    //-------------------------------------
    /**
     * Updates the bookmark with the given name and link.
     */
    public void updateBookmark(String pId, String pName, String pLink)
           throws RepositoryException
    {
        String link = checkLinkHasPrefix(pLink);
        if (isLoggingDebug())
           logDebug("updating bookmark: [" + pId + "], [" + pName + "], " + link);

        MutableRepository mr = (MutableRepository) getRepository();
        MutableRepositoryItem bookmark = mr.getItemForUpdate(pId, BOOKMARK_ITEM);

        bookmark.setPropertyValue(NAME, pName);
        bookmark.setPropertyValue(LINK, link);

        mr.updateItem(bookmark);
    }

    //-------------------------------------
    /**
     * Removes the bookmark from the list and from the repository itself.
     * @throws RepositoryException if an error occurs.
     */
    public void removeBookmark(String pId, List pChildren, RepositoryItem pRoot) throws RepositoryException
    {
        if (isLoggingDebug())
           logDebug("removing bookmark: " + pId);

        // First, we get the bookmark itself, so we can remove it from the list.
        MutableRepository mr = (MutableRepository) getRepository();
        MutableRepositoryItem bookmark = mr.getItemForUpdate(pId, BOOKMARK_ITEM);

        // remove any references to the bookmark 
        atg.adapter.util.RepositoryUtils.removeReferencesToItem(bookmark);

        // Remove the bookmark reference from the list of bookmarks.
        pChildren.remove(bookmark);

        MutableRepositoryItem updatableRoot = ((MutableRepository)getRepository()).getItemForUpdate(pRoot.getRepositoryId(), FOLDER_ITEM);
        updatableRoot.setPropertyValue(CHILDREN, pChildren);
        ((MutableRepository) getRepository()).updateItem(updatableRoot);
        
        // Remove the bookmark item itself.
        mr.removeItem(pId, BOOKMARK_ITEM);
    }

    //-------------------------------------
    /**
     * Gets the folders matching pId.
     */
    RepositoryItem[] getFolders(String pId) throws RepositoryException
    {
        Repository r = getRepository();
        String viewName = FOLDER_ITEM;
        RepositoryView view = r.getView(viewName);
        Object[] args = { pId };
        return mFolderStatement.executeQuery(view, args);
    }

    //-------------------------------------
    // Utility methods
    //-------------------------------------

    /**
     * Gets the folder id from the gearEnvironment.
     */
    public String getFolderId(GearEnvironment pGearEnv)
    {
        if (pGearEnv == null)
        {
            String msg = Constants.format(Constants.NULL_PARAM, "pGearEnv");
            throw new IllegalArgumentException(msg);
        }

        String folderId = pGearEnv.getGearUserParameter(BookmarksForEach.FOLDER_ID_PARAM);
        if (isLoggingDebug())
        {
            String msg = "getting gearUserParameter: "
                         + BookmarksForEach.FOLDER_ID_PARAM
                         + "=" + folderId;
            logDebug(msg);
        }

        return folderId;
    }

    /**
    * Gets the gear environment from a servlet request.
    */
    GearEnvironment getGearEnvironment(DynamoHttpServletRequest pReq,
                                       DynamoHttpServletResponse pResp)
    {
        pReq.setAttribute("atg.paf.Gear", pReq.getParameter("paf_gear_id"));
        GearEnvironment env = null;
        try
        {
	  env = EnvironmentFactory.getGearEnvironment(pReq, pResp);
        } catch (EnvironmentException e)
        {
            if (isLoggingError())
                logError(e);
        }

        return env;
    }

    //-------------------------------------
    /**
     * Checks to see if pLink starts with either HTTP_PREFIX or FTP_PREFIX.  If
     * true, returns pLink unchanged.  If false, returns pLink prepended with
     * HTTP_PREFIX.
     */
    public String checkLinkHasPrefix(String pLink)
    {
      // This is not the place to add the http prefix
      // to a link. If we add the prefix we prevent
      // a user from linking back to the portal server
      // without hardcoding in a hostname.
      //      if (true) return pLink;
        // First, get rid of any blank space around the link.
        String link = pLink.trim();

        if ( isLinkHasPrefix(pLink) || isLocalLink(pLink) )
        {
            return link;
        } else
        {
            return HTTP_PREFIX + link;
        }
    }

  /**
   * Returns true if the link is one that points back to 
   * the original server.
   * This method assumes that links starting with
   * a '/' or './' are local links.
   * @param pLink , the link to test
   **/
  public boolean isLocalLink (String pLink) {
    if (pLink == null) return false;
    if (pLink.trim().startsWith("/")) return true;
    if (pLink.trim().startsWith("./")) return true;
    if (pLink.trim().startsWith("../")) return true;
    return false;
  }
    public boolean isLinkHasPrefix(String pLink)
    {
        String[] types = getProtocolTypes();
        if (types != null)
        {
            for (int i = 0; i < types.length; i++)
            {
                if ( pLink.startsWith(types[i]) )
                {
                    return true;
                }
            }
        } else
        {
            if (isLoggingWarning())
            {
                String msg = Constants.format(Constants.NO_PROTOCOL_TYPES);
                logWarning(msg);
            }
        }
        return false;
    }

    //-------------------------------------

    String[] mProtocolTypes;
    public String[] getProtocolTypes()
    {
        return mProtocolTypes;
    }

    public void setProtocolTypes(String[] pProtocolTypes)
    {
        mProtocolTypes = pProtocolTypes;
    }

    //-------------------------------------
    /**
     * Logs the exception, creates a new DropletFormException adds the
     * form exception to the form and sets rollbackTransaction as true.
     */
    void error(Exception pException)
    {
        if (isLoggingError())
           logError(pException);

        addFormException(new DropletException ("NO_SYNC", pException, "NO_SYNC"));
        setRollbackTransaction(true);
    }

    //-------------------------------------
    /**
     * Logs the message, creates a new DropletFormException out of the message,
     * adds the message to the form, and sets rollbackTransaction as true.
     */
    void error(String pRootCauseMessage)
    {
        if (isLoggingError())
           logError(pRootCauseMessage);
        addFormException(new DropletException ("NO_SYNC", "NO_SYNC"));
        setRollbackTransaction(true);
    }

  //-------------------------------------
  // success
  //-------------------------------------
  /**
   * Add an information message to the formMessages to be displayed by the
   * form and sets formInfo to true.
   * @param pMsgId the message id
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public void success(String pMsgId)
      throws ServletException, IOException
    {
        //String msg = Constants.format(pMsgId);
        mFormMessages.add(pMsgId);
        mFormInfo = true;
        if(isLoggingDebug())
            logDebug("Adding form message : "+pMsgId);
    }

  
   //----------------------------------------------
  /**
   * Clears out all form messages
   * @beaninfo
   *   description: Clears out all form messages
   */
  public void resetFormMessages() {
    if (mFormMessages != null)
       mFormMessages.clear();
    mFormInfo = false;
  }
  
  //----------------------------------------------
  /**
   * Clears out all form messages
   * @beaninfo
   *   description: Clears out all form messages
   */
  public boolean handleResetFormMessages(DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {   
      resetFormMessages();
      return true;      
  }
    
    //----------------------------------------------
    /**
     * Clears out all form exceptions
     * @beaninfo
     *   description: Clears out all form exceptions
     */
    public boolean handleResetFormExceptions(DynamoHttpServletRequest pRequest,
                                             DynamoHttpServletResponse pResponse)
        throws ServletException, IOException {        
        resetFormExceptions();
        return true;        
    }  
    
    
}
