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

package atg.portal.gear.quicklinks;

import atg.portal.gear.bookmarks.*;

import atg.portal.framework.*;

import atg.core.util.StringUtils;

import atg.repository.*;
import atg.repository.rql.*;
import atg.dtm.*;
import atg.adapter.gsa.*;
import javax.transaction.*;

import atg.servlet.*;
import atg.droplet.*;

import java.util.*;

import java.io.IOException;
import javax.servlet.ServletException;

/**
 * This droplet retrieves bookmark information and displays in the output
 * parameter.  It is intended to be used with the BookmarksFormHandler.  Note
 * that this droplet differs from the BookmarksForEach droplet in that it will
 * create a root folder and set the root folder id as a gear instance parameter.
 *
 * @author Will Sargent
 * @version $Id: //app/portal/version/10.0.3/quicklinks/src/atg/portal/gear/quicklinks/QuicklinksForEach.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class QuicklinksForEach extends BookmarksForEach
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/quicklinks/src/atg/portal/gear/quicklinks/QuicklinksForEach.java#2 $$Change: 651448 $";

    /**
     * The name of the gear instance parameter containing the root folder id.
     */
    public static String FOLDER_ID_PARAM = "linksFolderId";


    //-------------------------------------
    /**
     * Gets the existing root folder.  If a root folder does not exist,
     * create it and set the gear instance parameter to FOLDER_ID_PARAM;
     * @param pRequest the request with the gear environment.
     */
    public String getRootFolder(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse)
        throws RepositoryException, EnvironmentException
    {
        GearEnvironment gearEnv = getGearEnvironment(pRequest, pResponse);
        // Make a stab at getting the instance parameter.  If it doesn't
        // exist, then make a new one.
        String folderId = gearEnv.getGearInstanceParameter(FOLDER_ID_PARAM);
        if (StringUtils.isEmpty(folderId))
        {
            String name = gearEnv.getGear().getId();
            RepositoryItem folder = createRootFolder(name);
            folderId = folder.getRepositoryId();

            // Set this gear instance parameter to the value of the root id.
            if (isLoggingDebug())
            {
                String msg = "setting instance param " + FOLDER_ID_PARAM + " = " + folderId;
                logDebug(msg);
            }
            gearEnv.setGearInstanceParameter(FOLDER_ID_PARAM, folderId);
        }
        return folderId;
    }

}
