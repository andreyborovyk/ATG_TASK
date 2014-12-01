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

package atg.projects.b2bstore.soap;

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
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/soap/SOAPConstants.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class SOAPConstants
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/soap/SOAPConstants.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  /**
   * The Resource Bundle to use in obtaining messages
   */
  static final String RESOURCE_BUNDLE=
    "atg.projects.b2bstore.soap.SOAPResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = 
    java.util.ResourceBundle.getBundle(RESOURCE_BUNDLE, 
                                       java.util.Locale.getDefault());

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  public static final String NO_ROUTER_FOUND =
    getStringResource("NoRouterFound");

  public static final String NO_SERVICE_MANAGER_FOUND =
    getStringResource("NoServiceManagerFound");

  public static final String PARAM_MARSHAL_OBJECT =
    getStringResource("ParamMarshalObject");

  public static final String PARAM_MARSHAL_KEY =
    getStringResource("ParamMarshalKey");

  public static final String SOAP_PARAMETER_NAME =
    getStringResource("SOAPParameterName");

  public static final String ERR_NO_MARSHAL_SERVICE = 
    getStringResource("NoMarshalService");  

  public static final String ERR_NO_SOAP_CLIENT = 
    getStringResource("NoSOAPClient");  

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
           "atg.commerce.util.Constants",
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
