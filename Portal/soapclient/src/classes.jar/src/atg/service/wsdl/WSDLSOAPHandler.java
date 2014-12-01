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
import javax.xml.parsers.*;
import org.xml.sax.helpers.*;
import java.util.*;
import org.xml.sax.*;

// DAS classes
import atg.core.util.*;

// DPS classes

// DSS classes

// DCS classes

/**
 * This class acts as a SAX handler for a particular WSDL document.
 * It catches the element types that it considers "interesting" in
 * order to create a new WSDLSOAPInfo object.
 *
 * <P>After parsing of an XML document is done, an object that has a
 * a reference to this object should be able to request the
 * WSDLSOAPInfo object.
 *
 * @author Ashley J. Streb
 * @version $Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/service/wsdl/WSDLSOAPHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class WSDLSOAPHandler
  extends DefaultHandler
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/service/wsdl/WSDLSOAPHandler.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------
  
  private static final Map sSchemaToJavaMap;

  static {
    sSchemaToJavaMap = new HashMap();
    sSchemaToJavaMap.put("string", "java.lang.String");
    sSchemaToJavaMap.put("integer", "java.lang.Integer");
    sSchemaToJavaMap.put("byte", "java.lang.Byte");
    sSchemaToJavaMap.put("short", "java.lang.Short");
    sSchemaToJavaMap.put("boolean", "java.lang.Boolean");
    sSchemaToJavaMap.put("long", "java.lang.Long");
    sSchemaToJavaMap.put("float", "java.lang.Float");
    sSchemaToJavaMap.put("double", "java.lang.Double");
  }

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------
  

  //---------------------------------------------------------------------
  // property: schemaNSPrefix
  String mSchemaNSPrefix;

  /**
   * Return the schemaNSPrefix property.
   * @return
   */
  public String getSchemaNSPrefix() {
    return mSchemaNSPrefix;
  }

  /**
   * Set the schemaNSPrefix property.
   * @param pSchemaNSPrefix
   */
  public void setSchemaNSPrefix(String pSchemaNSPrefix) { 
    mSchemaNSPrefix = pSchemaNSPrefix;
  }

  
  //---------------------------------------------------------------------
  // property: ServiceInfoClassName
  String mServiceInfoClassName = "atg.service.wsdl.ValidatingWSDLSOAPInfo";

  /**
   * The String representation of the class name that should be used
   * to store service information.  It defaults to
   * <code>atg.service.wsdl.ValidatingWSDLSOAPInfo but can be extended
   * if somebody needed to have a class that provided additional validating
   * information.
   * 
   * @return the class name
   */
  public String getServiceInfoClassName() {
    return mServiceInfoClassName;
  }

  /**
   * Set the ServiceInfoClassName property.
   * @param pServiceInfoClassName
   */
  public void setServiceInfoClassName(String pServiceInfoClassName) {
    mServiceInfoClassName = pServiceInfoClassName;
  }
  
  
  //---------------------------------------------------------------------
  // property: ServiceInfo
  ValidatingWSDLSOAPInfo mServiceInfo;

  /**
   * Return the ServiceInfo property.  This object encapsulate all
   * of the interesting information from a particular WSDL document
   * @return
   */
  public ValidatingWSDLSOAPInfo getServiceInfo() {
    return mServiceInfo;
  }

  /**
   * Set the ServiceInfo property.
   * @param pServiceInfo
   */
  public void setServiceInfo(ValidatingWSDLSOAPInfo pServiceInfo) {
    mServiceInfo = pServiceInfo;
  }

  
  //---------------------------------------------------------------------
  // property: MessageToPartMap
  Map mMessageToPartMap;

  /**
   * Return the MessageToPartMap property.
   * @return
   */
  public Map getMessageToPartMap() {
    return mMessageToPartMap;
  }

  /**
   * Set the MessageToPartMap property.
   * @param pMessageToPartMap
   */
  public void setMessageToPartMap(Map pMessageToPartMap) {
    mMessageToPartMap = pMessageToPartMap;
  }

  //---------------------------------------------------------------------
  // property: inInputTag
  boolean mInInputTag;

  /**
   * Return the inInputTag property.
   * @return
   */
  public boolean isInInputTag() {
    return mInInputTag;
  }

  /**
   * Set the inInputTag property.
   * @param pInInputTag
   */
  public void setInInputTag(boolean pInInputTag) {
    mInInputTag = pInInputTag;
  }

  //---------------------------------------------------------------------
  // property: inBindingTag
  boolean mInBindingTag;

  /**
   * Return the inBindingTag property.
   * @return
   */
  public boolean isInBindingTag() {
    return mInBindingTag;
  }

  /**
   * Set the inBindingTag property.
   * @param pInBindingTag
   */
  public void setInBindingTag(boolean pInBindingTag) {
    mInBindingTag = pInBindingTag;
  }

  //---------------------------------------------------------------------
  // property: inOperationTag
  boolean mInOperationTag;

  /**
   * Return the inOperationTag property.
   * @return
   */
  public boolean isInOperationTag() {
    return mInOperationTag;
  }

  /**
   * Set the inOperationTag property.
   * @param pInOperationTag
   */
  public void setInOperationTag(boolean pInOperationTag) {
    mInOperationTag = pInOperationTag;
  }
  
  //---------------------------------------------------------------------
  // property: currentMessageTagName
  String mCurrentMessageTagName;

  /**
   * Return the currentMessageTagName property.
   * @return
   */
  public String getCurrentMessageTagName() {
    return mCurrentMessageTagName;
  }

  /**
   * Set the currentMessageTagName property.
   * @param pCurrentMessageTagName
   */
  public void setCurrentMessageTagName(String pCurrentMessageTagName) {
    mCurrentMessageTagName = pCurrentMessageTagName;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------
  
  public WSDLSOAPHandler() {
    try {
      Class serviceInfoClass = Class.forName(getServiceInfoClassName());
      setServiceInfo((ValidatingWSDLSOAPInfo)serviceInfoClass.newInstance());
    }
    catch (ClassNotFoundException cnfe) {
      throw new IllegalArgumentException(cnfe.toString());
    }
    catch (InstantiationException ie) {
      throw new IllegalArgumentException(ie.toString());
    }
    catch (IllegalAccessException iae) {
      throw new IllegalArgumentException(iae.toString());
    }

    setMessageToPartMap(new HashMap(11));
  }

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------
  
  
  public boolean isDefinitionsTag(String pURI, String pLocalName) {
    if (pURI.equalsIgnoreCase(WSDLConstants.WSDL_NS_URI) &&
        pLocalName.equalsIgnoreCase(WSDLConstants.DEFINITION_TAG)) {
      return true;
    }
    return false;
  }

  /**
   * Determine if the current element is an
   * input tag in the WSDL namespace
   *
   * @param pURI URI of the namespace
   * @param pLocalName local element name
   * @return true if input tag
   */
  public boolean isInputTag(String pURI, String pLocalName) {
    if (pURI.equals(WSDLConstants.WSDL_NS_URI) && 
        pLocalName.equals(WSDLConstants.INPUT_TAG)) {
      return true;
    }
    return false;
  }

  /**
   * Determine if the current element is an
   * binding tag in the WSDL namespace
   *
   * @param pURI URI of the namespace
   * @param pLocalName local element name
   * @return true if binding tag
   */  
  public boolean isBindingTag(String pURI, String pLocalName) {
    if (pURI.equals(WSDLConstants.WSDL_NS_URI) && 
        pLocalName.equals(WSDLConstants.BINDING_TAG)) {
      return true;
    }
    return false;
  }

  /**
   * Determine if the current element is an
   * message tag in the WSDL namespace
   *
   * @param pURI URI of the namespace
   * @param pLocalName local element name
   * @return true if message tag
   */
  public boolean isMessageTag(String pURI, String pLocalName) {
    if (pURI.equals(WSDLConstants.WSDL_NS_URI) && 
        pLocalName.equals(WSDLConstants.MESSAGE_TAG)) {
      return true;
    }
    return false;
  }

  /**
   * Determine if the current element is an
   * part tag in the WSDL namespace
   *
   * @param pURI URI of the namespace
   * @param pLocalName local element name
   * @return true if part tag
   */
  public boolean isPartTag(String pURI, String pLocalName) {
    if (pURI.equals(WSDLConstants.WSDL_NS_URI) && 
        pLocalName.equals(WSDLConstants.PART_TAG)) {
      return true;
    }
    return false;
  }

  /**
   * Determine if the current element is an
   * body tag in the SOAP namespace
   *
   * @param pURI URI of the namespace
   * @param pLocalName local element name
   * @return true if body tag
   */
  public boolean isSOAPBodyTag(String pURI, String pLocalName) {
    if (pURI.equals(WSDLConstants.SOAP_NS_URI) && 
        pLocalName.equals(WSDLConstants.BODY_TAG)) {
      return true;
    }
    return false;
  }
  
 /**
   * Determine if the current element is an
   * operation tag in the SOAP namespace
   *
   * @param pURI URI of the namespace
   * @param pLocalName local element name
   * @return true if operation tag
   */
  public boolean isSOAPOperationTag(String pURI, String pLocalName) {
    if (pURI.equals(WSDLConstants.SOAP_NS_URI) && 
        pLocalName.equals(WSDLConstants.OPERATION_TAG)) {
      return true;
    }
    return false;
  }  

  /**
   * Determine if the current element is an
   * element tag in the WSDL namespace
   *
   * @param pURI URI of the namespace
   * @param pLocalName local element name
   * @return true if method tag
   */
  public boolean isOperationTag(String pURI, String pLocalName) {
    if (pLocalName.equals(WSDLConstants.OPERATION_TAG) 
        && pURI.equals(WSDLConstants.WSDL_NS_URI))
      return true;
    else
      return false;
  }

  /**
   * Determine if the current element is an
   * address tag in the SOAP namespace
   *
   * @param pURI URI of the namespace
   * @param pLocalName local element name
   * @return true if address tag
   */
  public boolean isURLEndpoint(String pURI, String pLocalName) {
    if (pURI.equals(WSDLConstants.SOAP_NS_URI) && 
        pLocalName.equals(WSDLConstants.ADDRESS_TAG)) {
      return true;
   }
   else 
      return false;
  }
  
  /**
   * Called by parser when an end element reached.  This method
   * is interested in three types of tags: binding, message and
   * input tags.  For each of these, it will perform some
   * housecleaning.
   *
   * <P>If the tag is a binding tag, then the <code>inBindingTag</code>
   * proeprty is set to false.
   *
   * <P>If the tag is a message tag then the <code>currentMessageTagName</code>
   * is set to null.
   *
   * <P>If the tag is an input tag then set the <code>inInputTag</code>
   * to false
   *
   * @param pURI the namespace URI of the current element
   * @param pLocalName local name for the current element
   * @param pQName a <code>String</code> value
   * @exception SAXException if an error occurs
   */
  public void endElement(String pURI, String pLocalName, String pQName)
    throws SAXException
  {
    if (isBindingTag(pURI, pLocalName)) {
      setInBindingTag(false);
    }
    
    if (isMessageTag(pURI, pLocalName)) {
      setCurrentMessageTagName(null);
    }

    if (isInputTag(pURI, pLocalName)) {
      setInInputTag(false);
    }

    if (isOperationTag(pURI, pLocalName)) {
      setInOperationTag(false);
    }    
    
  }

  protected void setSchemaNamespacePrefix(Attributes pAttributes) {
    String attributeValue;
    if (pAttributes == null || pAttributes.getLength() == 0) {
      setSchemaNSPrefix(WSDLConstants.DEFAULT_SCHEMA_NS_PREFIX);
      return;
    }
    
    for (int i=0; i<pAttributes.getLength(); i++) {
      // see if its a namespace declaration
      attributeValue = pAttributes.getValue(i);
      
      
      if (isSchemaURI(attributeValue)) {

        
        String nsPrefix = pAttributes.getLocalName(i);
        if (nsPrefix != null && !nsPrefix.equals("")) 
          setSchemaNSPrefix(pAttributes.getLocalName(i));
        // temp fix. since this seems to return a null
        else
          setSchemaNSPrefix(WSDLConstants.DEFAULT_SCHEMA_NS_PREFIX);
        
        return;
      }
    }
  }
  
  protected boolean isSchemaURI(String pValue) {
    for (int i=0; i<WSDLConstants.SCHEMA_URI_ARRAY.length; i++) {
      if (WSDLConstants.SCHEMA_URI_ARRAY[i].equalsIgnoreCase(pValue)) {
        return true;
      }
    }
    return false;
  }

  /**
   * This method filters out interesting tags and processes them.
   *
   * @param pURI namespace URI
   * @param pLocalName localname of element
   * @param pQName a <code>String</code> value
   * @param pAttributes an <code>Attributes</code> value
   * @exception SAXException if an error occurs
   */
  public void startElement(String pURI, String pLocalName, String pQName,
                           Attributes pAttributes) 
    throws SAXException
  {
    WSDLSOAPParameter parameter;
    Collection params;
    if (isDefinitionsTag(pURI, pLocalName)) {
      setSchemaNamespacePrefix(pAttributes);
    }

    // if it is a message tag, remember the name of the message
    if (isMessageTag(pURI, pLocalName)) {
      setCurrentMessageTagName(pAttributes.getValue(WSDLConstants.NAME_TAG));
    }

    // if it is binding tag, mark we are in a binding tag
    if (isBindingTag(pURI, pLocalName)) {
      setInBindingTag(true);
    }
    
    // if it is operation tag, mark we are in a operation tag
    if (isOperationTag(pURI, pLocalName)) {
      setInOperationTag(true);
    }    

    // if body tag in an input tag, then we can figure out our namespace
    // for the target method
    if (isSOAPBodyTag(pURI, pLocalName) &&
        isInInputTag()) {
      String namespace = pAttributes.getValue(WSDLConstants.NAMESPACE_TAG);
      getServiceInfo().setTargetNamespaceURI(namespace);
    }

    // tells us the URL that this SOAP client should connect to over HTTP
    if (isURLEndpoint(pURI, pLocalName)) {
      String url = pAttributes.getValue(WSDLConstants.LOCATION_TAG);
      getServiceInfo().setTargetServiceURL(url);
    }

    // tells us the name of the method exposed by the service
    // find it in the operation tag that appears within a binding
    if (isOperationTag(pURI, pLocalName) &&
        isInBindingTag()) {
      String methodName = pAttributes.getValue(WSDLConstants.NAME_TAG);
      getServiceInfo().setTargetMethodName(methodName);
    }
    
    if (isSOAPOperationTag(pURI, pLocalName) &&
        isInOperationTag()) {
      String uri = pAttributes.getValue(WSDLConstants.SOAP_ACTION_ATTRIBUTE_NAME);
      getServiceInfo().setSOAPActionURI(uri);
    }    
    
    // WSDL has named parts in each service. The meaning of these parts
    // varies depending on the type of WSDL document that is being exposed.
    // for RPC services, the parts are parameters to a method invocation.
    // they expose a name/type
    // also, convert from xsd type to java type
    if (isPartTag(pURI, pLocalName)) {
      if (getMessageToPartMap().containsKey(getCurrentMessageTagName())) {
        params = (Collection) 
          getMessageToPartMap().get(getCurrentMessageTagName());
        String javaType = 
          getJavaTypeFromXSDType(pAttributes.getValue(WSDLConstants.TYPE_TAG));
        String paramName = pAttributes.getValue(WSDLConstants.NAME_TAG);
        parameter = new WSDLSOAPParameter(paramName, javaType);
        params.add(parameter);
      }
      else {
        params = new ArrayList(11);
        String javaType = 
          getJavaTypeFromXSDType(pAttributes.getValue(WSDLConstants.TYPE_TAG));
        String paramName = pAttributes.getValue(WSDLConstants.NAME_TAG);
        parameter = new WSDLSOAPParameter(paramName, javaType);
        params.add(parameter);
        getMessageToPartMap().put(getCurrentMessageTagName(), params);
      }
    }

    // a input to the service is identified by a message type
    // we hvae stored a map of message names to parameters
    if (isInputTag(pURI, pLocalName)) {
      setInInputTag(true);
      String message = pAttributes.getValue(WSDLConstants.MESSAGE_TAG);
      if (message != null) {
        String messagePartName;
        // check for namespace separation
        if (message.indexOf(WSDLConstants.COLON_CHAR) != -1) {
          messagePartName = 
            StringUtils.splitStringAtCharacter(message, 
                                               WSDLConstants.COLON_CHAR)[1];
        }
        else {
          messagePartName = message;
        }
        
        params =
          (Collection)getMessageToPartMap().get(messagePartName);
        getServiceInfo().setServiceParameters(params);
      }
    }
  }
  
  /**
   * This method will obtain the String representation of the java class
   * that corresponds to some schema type.  For example, if a 
   * type is of <code>xsd:string</code>, then this method would return
   * the string <code>java.lang.String</code>.
   *
   * @param pXSDType a schema type, represented by its string value: 
   * @return java class name that corresponds to the schema type
   */
  private String getJavaTypeFromXSDType(String pXSDType) {
    String xsdType;
    String nsPrefix;
    String[] values;

    if (pXSDType.indexOf(WSDLConstants.COLON_CHAR) != -1) {
      values = StringUtils.splitStringAtCharacter(pXSDType, 
                                                  WSDLConstants.COLON_CHAR);
      nsPrefix = values[0];
      xsdType = values[1];

      // check to see if its a schema defined type
      if (! nsPrefix.equalsIgnoreCase(getSchemaNSPrefix())) {
        return pXSDType;
      }
      String javaType = (String) sSchemaToJavaMap.get(xsdType.toLowerCase());
    
      if (! (StringUtils.isBlank(javaType))) {
        return javaType;
      }
      else {
        return pXSDType;
      }
    }
    else {
      return pXSDType;
    }
  }
}   // end of class



