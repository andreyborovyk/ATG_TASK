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

import atg.core.util.ResourceUtils;

import java.util.ResourceBundle;
import java.util.MissingResourceException;

/**
 *
 * <p>This contains all of the non-public constants, including
 * messsage strings read from the resource file.
 *
 * @author Will Sargent
 * @version $Id: //app/portal/version/10.0.3/quicklinks/src/atg/portal/gear/quicklinks/Constants.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
class Constants
{
    //-------------------------------------
    /** Class version string */
    public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/quicklinks/src/atg/portal/gear/quicklinks/Constants.java#2 $$Change: 651448 $";

    //-------------------------------------
    // Resources

    static ResourceBundle sResourceBundle;

    //-------------------------------------
    // Constants
    //-------------------------------------

    public static final String RESOURCE_BUNDLE_NAME = Constants.class.getName();

    //-------------------------------------
    // Messages from the resource bundle
    //-------------------------------------

    public static final String NULL_PARAM = "NULL_PARAM";

    public static final String NO_ROOT_FOLDER = "NO_ROOT_FOLDER";

    //-------------------------------------
    // Getting resources
    //-------------------------------------

    //-------------------------------------
    // Formatting methods
    //-------------------------------------

    /**
     * Utility for formatting predefined messages using the current
     * resource bundle.
     * @param pMsgKey message id to format
     * @param pArgs arguments to fill in in message
     */
    static public String format(String pMsgKey, Object pArgs[])
    {
        try
        {
            ResourceBundle b = getBundle();
            String msg =
            ResourceUtils.getMsgResource(pMsgKey,
            getResourceBundleName(),
            b,
            pArgs);
            return msg;
        }
        catch (MissingResourceException mre)
        {
            // at least let them know what the error code was
            return "no text, ERR_CODE=" + pMsgKey;
        }
    }

    //-------------------------------------
    /**
     * Utility for formatting messages with no arguments
     */
    static public String format(String pMsgKey)
    {
        return format(pMsgKey, null);
    }

    //-------------------------------------
    /**
     * Utility for formatting messages with a single argument
     */
    static public String format(String pMsgKey, Object pArg)
    {
        Object a[] = { pArg };
        return format(pMsgKey, a);
    }

    //-------------------------------------
    /**
     * Get name of resource bundle. Subclasses should
     * override this to use their own resource bundles
     * @return the fully qualified name of the bundle, suitable
     * for loading as a system resource.
     */
    static public String getResourceBundleName()
    {
        return RESOURCE_BUNDLE_NAME;
    }

    //-------------------------------------
    /**
     * Get the resource bundle for this class to use.
     * @return resource bundle
     */
    static protected ResourceBundle getBundle()
    {
        // load the bundle if need be
        if (sResourceBundle == null)
            sResourceBundle = ResourceUtils.getBundle(getResourceBundleName());

        // return it
        return sResourceBundle;
    }

}