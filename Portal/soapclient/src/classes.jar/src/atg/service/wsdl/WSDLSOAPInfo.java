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
import java.util.ArrayList;
import java.util.Collection;

// DAS classes

// DPS classes

// DSS classes

// DCS classes

/**
 * This class encapsulates all of the information necessary to invoke a SOAP service.
 * The following information is made available:
 *
 * <UL>
 *   <LI>serviceParameters
 *   <LI>targetServiceURL
 *   <LI>targetMethodName
 *   <LI>targetURI
 *   <LI>SOAPActionURI
 * </UL>
 *
 * 
 * @author Ashley J. Streb
 * @version $Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/service/wsdl/WSDLSOAPInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class WSDLSOAPInfo
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/service/wsdl/WSDLSOAPInfo.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  
  //---------------------------------------------------------------------
  // property: serviceParameters
  Collection mServiceParameters;

  /**
   * A collection of parameters that are expected by a particular service 
   * invocation. It is a map of parameter names to parameter types
   * @return the service parameter map
   */
  public Collection getServiceParameters() {
    return mServiceParameters;
  }

  /**
   * Set the serviceParameters property.
   * @param pServiceParameters
   */
  public void setServiceParameters(Collection pServiceParameters) {
    mServiceParameters = pServiceParameters;
  }

  
  //---------------------------------------------------------------------
  // property: targetServiceURL
  String mTargetServiceURL;

  /**
   * The URL that a SOAP invocation flowing over HTTP is expected to connect to.
   * @return the URL endpoint for a service invocation
   */
  public String getTargetServiceURL() {
    return mTargetServiceURL;
  }

  /**
   * Set the targetServiceURL property.
   * @param pTargetServiceURL
   */
  public void setTargetServiceURL(String pTargetServiceURL) {
    mTargetServiceURL = pTargetServiceURL;
  }

  
  //---------------------------------------------------------------------
  // property: targetMethodName
  String mTargetMethodName;

  /**
   * The method that is being invoked by the SOAP request.
   * @return the method name
   */
  public String getTargetMethodName() {
    return mTargetMethodName;
  }

  /**
   * Set the targetMethodName property.
   * @param pTargetMethodName
   */
  public void setTargetMethodName(String pTargetMethodName) {
    mTargetMethodName = pTargetMethodName;
  }

  
  //---------------------------------------------------------------------
  // property: targetURI
  String mTargetNamespaceURI;

  /**
   * The namespace that the target method live in.
   * @return the URI of the target method
   */
  public String getTargetNamespaceURI() {
    return mTargetNamespaceURI;
  }

  /**
   * Set the targetURI property.
   * @param pTargetNamespaceURI
   */
  public void setTargetNamespaceURI(String pTargetNamespaceURI) {
    mTargetNamespaceURI = pTargetNamespaceURI;
  }

  
  //---------------------------------------------------------------------
  // property: SOAPActionURI
  String mSOAPActionURI;

  /**
   * The SOAPAction URI that some SOAP services expect in the Header
   * @return
   */
  public String getSOAPActionURI() {
    return mSOAPActionURI;
  }

  /**
   * Set the SOAPActionURI property.
   * @param pSOAPActionURI
   */
  public void setSOAPActionURI(String pSOAPActionURI) {
    mSOAPActionURI = pSOAPActionURI;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------
  public WSDLSOAPInfo() {
    setServiceParameters(new ArrayList(11));
  }

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

}   // end of class
