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

package atg.service.soap;

// Java classes
import java.util.*;

// DAS classes

// DPS classes

// DSS classes

// DCS classes

/**
 * All Constant messages that are to be used for server level
 * messages.
 *
 * @author Ashley J. Streb
 * @version $Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/service/soap/SOAPConstants.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class SOAPConstants
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/service/soap/SOAPConstants.java#2 $$Change: 651448 $";


  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  /**
   * The Resource Bundle to use in obtaining messages
   */
  static final String RESOURCE_BUNDLE=
    "atg.service.soap.SOAPResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = 
    java.util.ResourceBundle.getBundle(RESOURCE_BUNDLE, 
                                       java.util.Locale.getDefault());

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  public static final String NO_TARGET_URI =
    getStringResource("NoTargetURI");

  public static final String NO_TARGET_URL =
    getStringResource("NoTargetURL");

  public static final String NO_TARGET_METHOD_NAME =
    getStringResource("NoTargetMethodName");

  public static final String EXPECTED_SOAP_PARAM_CLASS =
    getStringResource("ExpectedSOAPParamClass");

  public static final String ERROR_INVOKING_SERVICE =
    getStringResource("ErrorInvokingService");

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------


  /**
   * Get a string resource for a given String.
   *
   * @param pResourceName the String resource to get
   * @return the localized Message
   * @exception MissingResourceException if an error occurs
   */
  public static String getStringResource (String pResourceName)
    throws MissingResourceException
  {
    try {
      String ret = sResourceBundle.getString (pResourceName);
      if (ret == null) {
        String str = "ERROR: Unable to load resource " + pResourceName;
        throw new MissingResourceException 
          (str, 
           "atg.service.soap.SOAPConstants",
           pResourceName);
      }
      else {
        return ret;
      }
    }
    catch (MissingResourceException exc) {
      throw exc;
    }
  }

  /**
   * Get an Integer Resource
   *
   * @param pResourceName the resource value to get
   * @return an <code>int</code> value
   * @exception MissingResourceException if an error occurs
   */
  public static int getIntResource (String pResourceName)
    throws MissingResourceException
  {
    String sval = getStringResource (pResourceName);
    return Integer.valueOf (sval).intValue ();
  }

}   // end of class
