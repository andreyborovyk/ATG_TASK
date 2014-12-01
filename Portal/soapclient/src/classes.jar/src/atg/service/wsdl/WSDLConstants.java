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

package atg.service.wsdl;

// Java classes

// DAS classes

// DPS classes

// DSS classes

// DCS classes

/**
 *
 * @author Ashley J. Streb
 * @version $Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/service/wsdl/WSDLConstants.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class WSDLConstants
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/service/wsdl/WSDLConstants.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  /**
   * The namespace URI that is used by WSDL specifcation to indicate
   * SOAP specific bindings.
   */
  public static final String SOAP_NS_URI = 
    "http://schemas.xmlsoap.org/wsdl/soap/";

  /**
   * The namespace URI that is used by WSDL specification to indicate
   * WSDL namespace
   */
  public static final String WSDL_NS_URI = "http://schemas.xmlsoap.org/wsdl/";
  
  /**
   * The input tag syntax
   */
  public static final String INPUT_TAG = "input";

  /**
   * The binding tag syntax
   */
  public static final String BINDING_TAG = "binding";

  /**
   * The message tag syntax
   */
  public static final String MESSAGE_TAG = "message";

  /**
   * The body tag syntax
   */
  public static final String BODY_TAG = "body";

  /**
   * The part tag syntax
   */
  public static final String PART_TAG = "part";

  /**
   * The name tag syntax
   */
  public static final String NAME_TAG = "name";

  /**
   * The type tag syntax
   */
  public static final String TYPE_TAG = "type";

  /**
   * The operation tag syntax
   */
  public static final String OPERATION_TAG = "operation";

  /**
   * The namespace tag syntax
   */
  public static final String NAMESPACE_TAG = "namespace";

  /**
   * The location tag syntax
   */
  public static final String LOCATION_TAG = "location";

  /**
   * The address tag syntax
   */
  public static final String ADDRESS_TAG = "address";

 /**
  * The soapAction attribute
  */
  
  public static final String SOAP_ACTION_ATTRIBUTE_NAME = "soapAction";
  /**
   * Colon character
   */
  public static final char COLON_CHAR = ':';
  
  public static final String DEFINITION_TAG = "definitions";

  public static final String DEFAULT_SCHEMA_NS_PREFIX = "xsd";
  
  public static final String[] SCHEMA_URI_ARRAY = {"http://www.w3.org/2001/XMLSchema", "http://www.w3.org/2000/10/XMLSchema", "http://www.w3.org/1999/XMLSchema"}; 

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

}   // end of class
