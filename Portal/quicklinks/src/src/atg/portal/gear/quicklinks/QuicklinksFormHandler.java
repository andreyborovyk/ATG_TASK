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

/**
 * @version $Id: //app/portal/version/10.0.3/quicklinks/src/atg/portal/gear/quicklinks/QuicklinksFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class QuicklinksFormHandler extends BookmarksFormHandler
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/quicklinks/src/atg/portal/gear/quicklinks/QuicklinksFormHandler.java#2 $$Change: 651448 $";


    /**
     * Gets the folder id from the gearEnvironment, using a gearInstanceParameter.
     */
    public String getFolderId(GearEnvironment pGearEnv)
    {
        if (pGearEnv == null)
        {
            String msg = Constants.format(Constants.NULL_PARAM, "pGearEnv");
            throw new IllegalArgumentException(msg);
        }

        String folderId =  pGearEnv.getGearInstanceParameter(QuicklinksForEach.FOLDER_ID_PARAM);
        if (isLoggingDebug())
        {
            String msg = "getting getGearInstanceParameter: " + QuicklinksForEach.FOLDER_ID_PARAM
                         + "=" + folderId;
            logDebug(msg);
        }
        return folderId;
    }
}