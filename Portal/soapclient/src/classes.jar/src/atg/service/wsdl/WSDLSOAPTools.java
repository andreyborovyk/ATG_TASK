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
import org.xml.sax.*;
import javax.xml.parsers.*;
import java.io.*;

// DAS classes
import atg.nucleus.*;
import java.util.*;

// DPS classes

// DSS classes

// DCS classes

/**
 * This class allows a user to ask several questions about a particular
 * WSDL document.  The user can learn:
 *
 * <UL>
 *   <LI>targetMethodName - the name of the method that a service represents
 *   <LI>targetNamespaceURI - the namespace that the target object lives in
 *   <LI>targetServiceURL - the HTTP endpoint for a given service invocation
 *   <LI>serviceParameters - a Map containing all parameters expected by a 
 *       service
 *   <LI>SOAPActionURI - the soapAction URI that some services require to
 *   appear in the HTTP header
 * </UL>
 *
 * <P>This tools package currently enforces the following conventions
 * <UL>
 *   <LI>The WSDL document must conform to the WSDL 1.1 XML Schema
 *   <LI>There can only be a single operation declared in the WSDL document
 *   <LI>The types of the parameters supplied to a particular service 
 *       invocation must conform to the java primitives.  They must
 *       be specified using xml schema types; xsd:string etc.
 * </UL>
 *
 * @author Ashley J. Streb
 * @version $Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/service/wsdl/WSDLSOAPTools.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class WSDLSOAPTools
  extends GenericService
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/service/wsdl/WSDLSOAPTools.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------
  
  /**
   * Cache of WSDL String documents to WSDLSOAPInfo Objects.  The
   * WSDLCacheObjects contain information specific to a particular
   * WSDL document.
   */
  protected static Map sWSDLCache =
    new HashMap(89);

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
  
  
  /**
   * This method creates a new {@link WSDLSOAPInfo<code>WSDLSOAPInfo</code>}
   * object and populates it with the information extracted from the 
   * parameter pWSDLDocument.  This WSDLSOAPInfo object will then be placed
   * into the internal cache of this class.
   *
   *
   * @param pWSDLDocument string representing a XML WSDL document
   * @return the WSDLSOAPInfo object encapsulating all information
   * @exception InvalidNumberMethodsException if the supplied WSDL
   * document defines more than one method
   * @exception InvalidPropertyType Exception if the parameter types
   * for the service invocation are not of type Java primitives
   * @exception WSDLException if an error occurs
   */
  WSDLSOAPInfo populateCache(String pWSDLDocument) 
    throws InvalidNumberMethodsException,
           InvalidPropertyTypeException,
           WSDLException
  {
    if (isLoggingDebug()) {
      logDebug("Going to parse WSDL Document with value: " + pWSDLDocument);
    }
    try {
      // get the parser
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(true);
      factory.setFeature("http://xml.org/sax/features/namespace-prefixes",
                         true);
      SAXParser parser = factory.newSAXParser();
      
      // set up our InputSource
      InputSource wsdlInput= new InputSource(new StringReader(pWSDLDocument));
      
      // get a new instance of our content handler
      WSDLSOAPHandler handler = new WSDLSOAPHandler();
      
      parser.parse(wsdlInput, handler);
      ValidatingWSDLSOAPInfo cacheObject = handler.getServiceInfo();
      cacheObject.validate();
      sWSDLCache.put(pWSDLDocument, cacheObject);
      return cacheObject;
    }
    catch (SAXException se) {
      throw new WSDLException(se);
    }
    catch (ParserConfigurationException pce) {
      throw new WSDLException(pce);
    }
    catch (IOException ioe) {
      throw new WSDLException(ioe);
    }
  }

  /**
   * This method will attempt to retrieve a cache object
   * for the given WSDL document that is passed in.
   * If the cache does not contain an entry for that WSDL
   * document, null is returned.
   *
   * @param pWSDLDocument the WSDL document
   * @return a <code>WSDLSOAPInfo</code> value
   */
  WSDLSOAPInfo getCacheObject(String pWSDLDocument) {
    if (sWSDLCache.containsKey(pWSDLDocument)) {
      return (WSDLSOAPInfo) sWSDLCache.get(pWSDLDocument);
    }
    else {
      return null;
    }
  }
  
  /**
   * Return an object that contains all of the information about a
   * given WSDL document.  See the 
   * {@link WSDLSOAPInfo<code>WSDLSOAPInfo</code>} class for more information
   * on what information might be returned by a given method.
   *
   * @param pWSDLDocument the WSDL document in string form
   * @return WSDLSOAPInfo object
   * @exception InvalidPropertyTypeException if the property type is not
   * understood by the parsing mechanism.  Currently only Java primitives
   * and their wrapper objects are supported
   * @exception InvalidNumberMethodsException if there is an invalid number
   * of services declared.  there can only be a single method defined per
   * document
   * @exception WSDLException if an error occurs
   */
  public WSDLSOAPInfo getWSDLInfo(String pWSDLDocument) 
    throws InvalidPropertyTypeException,
           InvalidNumberMethodsException,
           WSDLException
  {
    WSDLSOAPInfo returnObject;
    
    returnObject = getCacheObject(pWSDLDocument);
    if (returnObject != null) {
      return returnObject;
    }
    else {
      return populateCache(pWSDLDocument);
    }
  }

}   // end of class


