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

import atg.adapter.gsa.*;
import atg.repository.*;
import atg.repository.rql.*;

import atg.servlet.*;
import atg.droplet.*;

import atg.userprofiling.*;

import atg.dtm.*;
import atg.core.util.StringUtils;

import java.util.*;
import javax.transaction.*;

import java.io.IOException;
import javax.servlet.ServletException;

/**
 * This servlet retrieves bookmark information and displays in the output
 * parameter.  It is intended to be used with the BookmarksFormHandler.
 * <p>
 * Note: if a root bookmark folder is not found, the servlet will create a root
 * bookmark and add it to the repository.

 Open Parameters:

 <dl>
   <dt>output
   <dd>Services the output.
   <dt>more
   <dd>Offers to create more links.
 </dl>

 The output oparam contains the following output parameters:

 <dl>
   <dt>id
   <dd>The id of the bookmark node in the repository.
   <dt>name
   <dd>The title of the bookmark node.
   <dt>link
   <dd>The href parameter of the node, if it is a bookmark.  Otherwise null.
   <dt>index
   <dd>The 0-based index of the array.
 </dl>

 The more oparam contains the following output parameters:

 <dl>
   <dt>totalRows
   <dd>The total number of rows that were displayed.
 </dl>

 *
 * @version $Id: //app/portal/version/10.0.3/bookmarks/src/atg/portal/gear/bookmarks/BookmarksForEach.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class BookmarksForEach extends DynamoServlet
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/bookmarks/src/atg/portal/gear/bookmarks/BookmarksForEach.java#2 $$Change: 651448 $";

    // parameters from the request.
    public static final String FOLDER_ID = "folderId";
    public static final String EXTRA = "extra";

    /**
     * The name of the gear user parameter containing the root folder id.
     */
    public static String FOLDER_ID_PARAM = "bmgFolderId";

    // bookmark folder item.
    private static final String FOLDER_ITEM = "bookmark-folder";
    // folder property.
    public static final String CHILDREN = "children";

    // bookmarks properties.
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String LINK = "link";

    public static final String PORTAL_REPOSITORY_LOCATION_ATTR
    = "atg.paf.PortalRepositoryLocation";
    public static final String PORTAL_REPOSITORY_LOCATION_ATTRNAME
    = "dynamo:/atg/portal/framework/PortalRepository";

    public static final String GEAR_ATTR = "atg.paf.Gear";
    public static final String GEAR_ATTRVALUE = "paf_gear_id";

    /**
     * The RQL statement which determines the folder id.
     * make it static to improve performance
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
    // property: MinimumRows
    int mMinimumRows;

    /**
     * Sets the minimum number of rows to render.
     */
    public void setMinimumRows(int pMinimumRows)
    {
        mMinimumRows = pMinimumRows;
    }

    /**
     * Gets the minimum number of rows to render.
     */
    public int getMinimumRows()
    {
        return mMinimumRows;
    }

    //-------------------------------------
    // property: repository

    Repository mRepository;
    public void setRepository(Repository pRepository)
    {
        mRepository = pRepository;
    }
    public Repository getRepository()
    {
        return mRepository;
    }

    //-------------------------------------
    // property: profilePath
    String mProfilePath = "/atg/userprofiling/Profile";
    public void setProfilePath(String pProfilePath)
    {
        mProfilePath = pProfilePath;
    }
    public String getProfilePath()
    {
        return mProfilePath;
    }

    //-------------------------------------
    /**
     * Creates a transaction, checks for required properties and calls
     * getBookmarks().
     */
    public void service(DynamoHttpServletRequest pRequest,
                        DynamoHttpServletResponse pResponse)
        throws IOException, ServletException
    {
        String strExtra = (String) pRequest.getLocalParameter(EXTRA);
        Integer extra = null;
        if (strExtra != null)
        {
            extra = new Integer(strExtra);
        }

        // Do everything in a single transaction.
        TransactionDemarcation td = null;
        GSARepository gr = (GSARepository) getRepository();
        TransactionManager transManager = gr.getTransactionManager();
        try
        {
            td = new TransactionDemarcation();
            td.begin(transManager, td.REQUIRES_NEW);
            String folderId = getRootFolder(pRequest, pResponse);
            if (isLoggingDebug())
            {
                String msg = "Setting request parameter folderId = " + folderId;
                logDebug(msg);
            }
            pRequest.setParameter("folderId", folderId);
            getBookmarks(extra, folderId, pRequest, pResponse);
        } catch (RepositoryException re)
        {
            error(re);
            rollback(transManager);
        } catch (TransactionDemarcationException tde)
        {
            error(tde);
            rollback(transManager);
        } catch (EnvironmentException ee)
        {
            error(ee);
            rollback(transManager);
        } finally
        {
            try
            {
                if (td != null) td.end();
            } catch (TransactionDemarcationException exc)
            {
                error(exc);
                rollback(transManager);
            }
        }
    }

    //-------------------------------------
    /**
     * Gets the bookmarks from the repository and services the output parameters.
     */
    private void getBookmarks(Integer pExtra, String pFolderId,
                              DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse)
        throws RepositoryException, IOException, ServletException
    {
        String folderId = pFolderId;
        RepositoryItem[] folders = getFolders(folderId);

        RepositoryItem rootFolder = null;

        // Get the children of the root folder.
        List bookmarks = null;
        if (folders == null)
        {
            bookmarks = Collections.EMPTY_LIST;
        } else
        {
            rootFolder = folders[0];
            bookmarks = (List) rootFolder.getPropertyValue(CHILDREN);
        }

        int numOfBookmarks = bookmarks.size();
        int minRows = getMinimumRows();

        // print at least the minimum number of rows.  If there are more bookmarks
        // than rows, print out all the bookmarks.
        int normalRows = (numOfBookmarks <= minRows) ? minRows : numOfBookmarks;
        // print some extra blank rows if the extra parameter is there.
        int extraRows = (pExtra == null) ? 0 : pExtra.intValue();
        int totalRows = normalRows + extraRows;

        if (isLoggingDebug())
        {
            String msg = "There are " + numOfBookmarks + " bookmarks found.";
            logDebug(msg);
            String msg2 = "Printing " + totalRows + " rows.";
            logDebug(msg2);
        }

        pRequest.serviceLocalParameter("outputStart", pRequest, pResponse);

        // Produce the rows, even if they don't have a matching bookmark.
        for (int i = 0; i < totalRows; i++)
        {
            String id = "";
            String name = "";
            String link = "";

            if (i < numOfBookmarks)
            {
                RepositoryItem bookmark = (RepositoryItem) bookmarks.get(i);
                id = (String) bookmark.getPropertyValue(ID);
                name = (String) bookmark.getPropertyValue(NAME);
                link = (String) bookmark.getPropertyValue(LINK);
            }

            pRequest.setParameter("id", id);
            pRequest.setParameter("name", name);
            pRequest.setParameter("link", link);
            pRequest.setParameter("index", new Integer(i));

            pRequest.serviceLocalParameter("output", pRequest, pResponse);
        }

        pRequest.serviceLocalParameter("outputEnd", pRequest, pResponse);

        // Set an oparam with options that can be used.
        pRequest.setParameter("totalRows", new Integer(totalRows));
        pRequest.serviceLocalParameter("more", pRequest, pResponse);
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
    /**
     * Gets the existing root folder, creating it if it does not already exist.
     * @param pRequest the request with the gear environment.
     */
    public String getRootFolder(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse)
        throws RepositoryException, EnvironmentException
    {
        GearEnvironment gearEnv = getGearEnvironment(pRequest, pResponse);
        String folderId = gearEnv.getGearUserParameter(FOLDER_ID_PARAM);
        if (! StringUtils.isEmpty(folderId))
        {
            return folderId;
        }

        // Okay, we have to create a root folder.
        // Get a nice name.
        Profile profile = getProfile(pRequest);
        PropertyManager propManager = profile.getProfileTools().getPropertyManager();
        String firstNamePropertyName = propManager.getFirstNamePropertyName();
        String lastNamePropertyName = propManager.getLastNamePropertyName();
        String firstName = (String) profile.getPropertyValue(firstNamePropertyName);
        String lastName = (String) profile.getPropertyValue(lastNamePropertyName);
        String name = ((firstName == null) ? "" : firstName)
                      + " "
                      + ((lastName == null) ? "" : lastName);
        RepositoryItem folder = createRootFolder(name);
        String id = folder.getRepositoryId();

        // Set this gear user parameter to the value of the root id.
        if (isLoggingDebug())
        {
            String msg = "setting " + FOLDER_ID_PARAM + " to " + id;
            logDebug(msg);
        }
        gearEnv.setGearUserParameter(FOLDER_ID_PARAM, id);
        return id;
    }

    //-------------------------------------
    /**
     * Creates a root folder and adds it to the repository.
     * @param pRootName the name of the root folder.
     * @throws RepositoryException if an error occurs.
     */
    public RepositoryItem createRootFolder(String pRootName)
       throws RepositoryException
    {
        if (isLoggingDebug())
           logDebug("creating root folder: " + pRootName);

        MutableRepository mr = (MutableRepository) getRepository();
        MutableRepositoryItem folder = mr.createItem(FOLDER_ITEM);

        folder.setPropertyValue(NAME, pRootName);

        // Add the folder to the repository.
        mr.addItem(folder);

        return folder;
    }

    //-------------------------------------
    /**
     * Gets the profile from the request.
     */
    Profile getProfile(DynamoHttpServletRequest pRequest)
    {
        return ((Profile)pRequest.resolveName(getProfilePath()));
    }

    //-------------------------------------
    /**
     * Gets the gear environment from the request.
     */
    protected GearEnvironment getGearEnvironment(DynamoHttpServletRequest pReq,
                                                 DynamoHttpServletResponse pResp)
        throws EnvironmentException
    {
        String gearParameter = pReq.getParameter(GEAR_ATTRVALUE);
        pReq.setAttribute(GEAR_ATTR, gearParameter);

        pReq.setAttribute(PORTAL_REPOSITORY_LOCATION_ATTR,
                          PORTAL_REPOSITORY_LOCATION_ATTRNAME);
        return EnvironmentFactory.getGearEnvironment(pReq, pResp);
    }

    //-------------------------------------

    void error(Exception pException)
    {
        if (isLoggingError())
            logError(pException);
    }

    void error(String pMessage)
    {
        if (isLoggingError())
           logError(pMessage);
    }

    //-------------------------------------
    /**
     * Mark the current transaction to be rolled
     * back, logging any checked exceptions
     **/
    void rollback(TransactionManager pManager)
    {
        if (isLoggingDebug()) logDebug("rollback()");

        try
        {
            if (pManager != null)
                pManager.setRollbackOnly();
        } catch (SystemException se)
        {
            error(se);
        }
    }

}
