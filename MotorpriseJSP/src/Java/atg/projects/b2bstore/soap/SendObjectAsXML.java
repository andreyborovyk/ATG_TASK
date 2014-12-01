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
import atg.apache.soap.rpc.Parameter;
import atg.apache.soap.Constants;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.util.Map;
import java.util.Vector;

// DAS classes
import atg.xml.service.marshal.ObjectMarshallerDispatcher;
import atg.xml.tools.marshal.MarshalResult;
import atg.nucleus.Nucleus;

// DPS classes

// DSS classes
import atg.process.action.ActionImpl;
import atg.process.ProcessException;
import atg.process.ProcessExecutionContext;


// DCS classes

/**
 * This action is used to send objects out via a SOAP RPC call.
 * In order to do this, two thing must happen.  First, a supplied Object
 * parameter must be marshalled to XML.  This marshalling is done by
 * a configured ObjectMarshallerDispatcher service.  After the object
 * has been converted to an XML document, the document must be sent
 * out via a SOAP request.  This is done by a SimpleSOAPClient.  To
 * understand how these various methods are invoked, consult the 
 * {@link #executeAction(ProcessExecutionContext)<code>executeAction()</code>}
 * method.
 *
 * This action requires a single parameter: <em>marshalObject</em>.
 *
 * @see atg.xml.service.ObjectMarshallerDispatcher
 * @see atg.process.action.ActionImpl
 * @see SimpleSOAPClient
 * @author Ashley J. Streb
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/soap/SendObjectAsXML.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class SendObjectAsXML
  extends ActionImpl
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/soap/SendObjectAsXML.java#2 $$Change: 651448 $";
    
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
  // property: MarshalService
  ObjectMarshallerDispatcher mMarshalService;

  /**
   * This service is used to marshal the supplied object parameter
   * into an XML document.  This property gets set from the passed in
   * SendObjectAsXMLConfiguration object in the {@link
   * #initialize(Map)<code>configure()</code>} method.
   *
   * @return the ObjectMarshallerDispatcher that will be used to marshal
   * object.  
   */
  public ObjectMarshallerDispatcher getMarshalService() {
    return mMarshalService;
  }

  /**
   * Set the MarshalService property. This is the service used to
   * marshal object to XML.
   *
   * @param pMarshalService the service marshal the object
   */
  public void setMarshalService(ObjectMarshallerDispatcher pMarshalService) {
    mMarshalService = pMarshalService;
  }

  
  //---------------------------------------------------------------------
  // property: SimpleSOAPClient
  SimpleSOAPClient mSimpleSOAPClient;

  /**
   * This is the SOAP client that will be used to send the XML
   * document out via a SOAP call.  This property gets set from the
   * passed in SendObjectAsXMLConfiguration object in the {@link
   * #initialize(Map)<code>configure()</code>} method.
   * @return the SimpleSOAPClient 
   */
  public SimpleSOAPClient getSimpleSOAPClient() {
    return mSimpleSOAPClient;
  }

  /**
   * Set the SimpleSOAPClient property.  This gets set in the
   * <code>configure</code> method.
   * @param pSimpleSOAPClient the simple SOAP client to use to
   * send messages
   */
  public void setSimpleSOAPClient(SimpleSOAPClient pSimpleSOAPClient) {
    mSimpleSOAPClient = pSimpleSOAPClient;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------
  
  //-------------------------------------
  /**
   * Configures the action using the given configuration object, of
   * type SendObjectAsXMLConfiguration.  This sets up the following
   * properties of this class, by copying them out of the
   * configuration object:
   * 
   * <UL>
   *  <LI>ObjectMarshallerDispatcher - The component that will determine the
   *  correct marshaller processor to serialize the <code>marshalObject</code>.
   *  <LI>SimpleSOAPClient - The component that send the XML representation
   *  of the object out via a SOAP request.
   * </UL>
   *
   * @exception ProcessException if the action could not be configured
   * - for example, because some of the required properties are
   * missing from the configuration
   **/
  public void configure(Object pConfiguration)
    throws ProcessException
  {
    SendObjectAsXMLConfiguration config = 
      (SendObjectAsXMLConfiguration) pConfiguration;

    // make sure we get a reference to the marshaller service
    setMarshalService(config.getMarshalService());
    if (getMarshalService() == null)
      throw new ProcessException(SOAPConstants.ERR_NO_MARSHAL_SERVICE);

    // make sure we get a reference to the soap client
    setSimpleSOAPClient(config.getSimpleSOAPClient());
    if (getSimpleSOAPClient() == null)
      throw new ProcessException(SOAPConstants.ERR_NO_SOAP_CLIENT);
  }

  /**
   * The initialize method is used to <i>initialize</i> this
   * Scenario action before first time use.  It sets up
   * several properties of this class:
   *
   * <UL>
   *  <LI>marshalObject - This parameter is required in order for the
   *  action to sucessfully run.  The object is the object that will
   *  be serialized to XML before being sent out via a SOAP request.
   *  <LI>marshalKey - The key that will be used to indicate which marshaler
   *  to use when marshalling the <code>marshalObject</code> parameter. 
   *  This key is handed to the {@link atg.xml.service.ObjectMarshallerDispatcher
   *  <code>ObjectMarshallerDispatcher</code>} class.  This parameter
   *  is <b>optional</b>.
   * </UL>
   *
   * @param pParameters map of parameters that were handed to this scenario
   * @exception ProcessException if an error occurs
   */
  public void initialize(Map pParameters) 
    throws ProcessException
  {
    storeRequiredParameter(pParameters, SOAPConstants.PARAM_MARSHAL_OBJECT, Object.class);
    storeOptionalParameter(pParameters, SOAPConstants.PARAM_MARSHAL_KEY, String.class);
  }


  /**
   * This method peforms the work for the action.  It will
   * call several methods contained in this class to do
   * the work.  First, it will get the XML representation of
   * the object.  It will do this by obtaining the marshal
   * object as well as the marshal key parameter and call
   * the {@link #getObjectAsXML(Object, Object)<code>getObjectAsXML</code>}.
   * Once it has the XML representation, it will create a 
   * SOAP parameter object by calling the <code>createParameter</code>
   * method.  Finally, it will send the XML document out via a SOAP
   * request by calling the invoke method on the 
   * {@link atg.projects.b2bstore.soap.SimpleSOAPClient<code>SimpleSOAPClient
   * </code>} class.
   *
   * @param pContext a <code>ProcessExecutionContext</code> value
   * @exception ProcessException if an error occurs
   */
  protected void executeAction(ProcessExecutionContext pContext)
    throws ProcessException
  {
    Element objectAsXML = 
      getObjectAsXML(getParameterValue(SOAPConstants.PARAM_MARSHAL_OBJECT,
                                       pContext),
                     getParameterValue(SOAPConstants.PARAM_MARSHAL_KEY,
                                       pContext));
    
    
    Parameter param = createParameter(objectAsXML);
    
    // create vector of params and add marshalled object
    Vector params = new Vector(1);
    params.add(param);
    getSimpleSOAPClient().invoke(params);
  }
  
  /**
   * Return the value of <code>SOAPConstants.SOAP_PARAMETER_NAME</code>
   * This defaults to <em>xmlDocument</em>
   *
   * @return the name to use to identify the xml document
   * parameter
   */
  protected String getSOAPParameterName() {
    return SOAPConstants.SOAP_PARAMETER_NAME;
  }

  /**
   * Create the Parameter that will be used to invoke
   * the SOAP client.  This Parameter is an instance
   * of the <code>atg.apache.soap.rpc.Parameter</code>
   * class.  The name used for the parameter is obtained via the
   * {@link #getSOAPParameterName<code>getSOAPParameterName</code>}
   * method.
   *
   * @param pObjectAsXML the XML element that will be sent
   * via the SOAP request.
   * @return the Parameter object to invoke the SOAP client
   * with.
   */
  protected Parameter createParameter(Element pObjectAsXML) {
    return new Parameter(getSOAPParameterName(), Element.class, 
                         pObjectAsXML, 
                         atg.apache.soap.Constants.NS_URI_LITERAL_XML);
  }
  
  /**
   * This method will obtain the XML representation
   * of the supplied object.  This is done by querying
   * the configured <code>ObjectMarshallerDispatcher</code>
   * to marshal the object.  To see how the 
   * ObjectMarshallerDispatcher service gets configured,
   * consult the {@link #initialize(Map) <code>initialize</code>}
   * method.
   *
   * @param pObjectToMarshal the object that will be marshalled to XML
   * @param pKey the key that will be passed to the 
   * ObjectMarshallerDispatcher service
   * @return the XML representation of the object.
   */
  protected Element getObjectAsXML(Object pObjectToMarshal,
                                   Object pKey) 
    throws ProcessException
  {
    try {
      MarshalResult result =
        getMarshalService().marshalObject(pObjectToMarshal,
                                          null,
                                          pKey);
      Document doc =  result.getDocumentResult();
      if (doc != null) {
        return doc.getDocumentElement();
      }
      else {
        return null;
      }
    }
    catch (Exception e) {
      throw new ProcessException(e);
    }
  }

}   // end of class




