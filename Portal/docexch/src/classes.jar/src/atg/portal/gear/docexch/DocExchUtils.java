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

import atg.repository.*;
import atg.portal.framework.*;


/**
 * This assembles an RQL Query for getting a list of documents from the db
 * using spaghetti logic specific to the document exchange gear and then
 * gives the query to the RQLQueryRange droplet.
 *
 * @author Cynthia Harris
 * @version $Id: //app/portal/version/10.0.3/docexch/classes.jar/src/atg/portal/gear/docexch/DocExchUtils.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class DocExchUtils
{
    /** Class version string */
    public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/docexch/classes.jar/src/atg/portal/gear/docexch/DocExchUtils.java#2 $$Change: 651448 $";
    
    /**
     * @return true if doc is available in this gear 
     */
    public static boolean isDocInGear(RepositoryItem pDocument, GearEnvironment pGearEnv) {
        String gearIdProp = pGearEnv.getGearInstanceParameter("gearIdPropertyName");
        String gearId = pGearEnv.getGear().getId();
        return isDocInGear(pDocument, gearIdProp, gearId);
    }
    
    public static boolean isDocInGear(RepositoryItem pDocument, String pGearIdProp, String pGearId) {

        // if there is no gear ref id for this gear then the doc is in the gear
        if (isEmptyString(pGearIdProp))
            return true;
        
        String gearIdOfDocument = (String)pDocument.getPropertyValue(pGearIdProp);

        // if the document has no value for gear ref id then it is not available.
        if (isEmptyString(gearIdOfDocument))
            return false;
        if (gearIdOfDocument.equals(pGearId))
            return true;
        else
            return false;
    }
    
    private static boolean isEmptyString(String pTest) {
        return (pTest == null || pTest.trim().length() == 0);
    }
}

