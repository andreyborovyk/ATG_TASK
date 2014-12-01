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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Element;
import java.io.OutputStream;
import javax.servlet.ServletContext;

// SOAP Classes
import atg.server.soap.DynamoRPCRouterServlet;
import atg.apache.soap.server.RPCRouter;
import atg.apache.soap.server.DeploymentDescriptor;
import atg.apache.soap.server.ServiceManager;
import atg.apache.soap.SOAPException;

// DAS classes
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;

// DPS classes

// DSS classes

// DCS classes

/**
 * This is a simple service which will receive an <code>org.w3c.dom.Element
 * </code> object and print out the DOM associated wth it.
 * This is a simple utility class which is intended to be used with
 * a SOAP server.  The SOAP server can register this service to receive
 * a single <code>org.w3c.dom.Element</code> object and print it out.
 *
 * @author Ashley J. Streb
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/soap/DOMWriterService.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class DOMWriterService
  extends GenericService
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/soap/DOMWriterService.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  public static final String RPC_ROUTER_ID = "rpcRouter";

  /**
   * Code to return if we were able to serialize the DOM/Element object
   * to the output stream.
   */
  public static final String SUCCESS = "success";

  /**
   * Code to return if we were unable to serialize the DOM/Element object
   * to the output stream.
   */
  public static final String FAILURE = "failure";

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------


  //---------------------------------------------------------------------
  // property: ServiceId
  String mServiceId;

  /**
   * Return the ServiceId property.
   * @return
   */
  public String getServiceId() {
    return mServiceId;
  }

  /**
   * Set the ServiceId property.
   * @param pServiceId
   */
  public void setServiceId(String pServiceId) {
    mServiceId = pServiceId;
  }


  //---------------------------------------------------------------------
  // property: methods
  String[] mMethods;

  /**
   * Return the methods property.
   * @return
   */
  public String[] getMethods() {
    return mMethods;
  }

  /**
   * Set the methods property.
   * @param pMethods
   */
  public void setMethods(String[] pMethods) {
    mMethods = pMethods;
  }

  //---------------------------------------------------------------------
  // property: IsIndent
  boolean mIndent = true;

  /**
   * If this value is set to true, then the OutputKeys.INDENT property
   * on the transformer will be set to true.  This will cause the
   * rendered XML file to be indented as appropriate.
   *
   * <p>This defaults to true.
   * @return true if the XML document should be serialized.
   */
  public boolean IsIndent() {
    return mIndent;
  }

  /**
   * Set the IsIndent property.
   * @param pIndent
   */
  public void setIndent(boolean pIndent) {
    mIndent = pIndent;
  }

  
  //---------------------------------------------------------------------
  // property: OutputMethod
  String mOutputMethod = "xml";

  /**
   * The output method that gets set on the transformer.  This defaults
   * to xml.
   * @return the output method
   */
  public String getOutputMethod() {
    return mOutputMethod;
  }

  /**
   * Set the OutputMethod property.
   * @param pOutputMethod
   */
  public void setOutputMethod(String pOutputMethod) {
    mOutputMethod = pOutputMethod;
  }


  //---------------------------------------------------------------------
  // property: OutputStream
  OutputStream mOutputStream = System.out;

  /**
   * The outputstream that the Document/Element should be rendered
   * to.  This defaults to <code>System.out</code>
   * @return the output stream 
   */
  public OutputStream getOutputStream() {
    return mOutputStream;
  }

  /**
   * Set the OutputStream property.
   * @param pOutputStream
   */
  public void setOutputStream(OutputStream pOutputStream) {
    mOutputStream = pOutputStream;
  }


  //---------------------------------------------------------------------
  // property: RPCRouterServlet
  DynamoRPCRouterServlet mRPCRouterServlet;

  /**
   * Return the RPCRouterServlet property.
   * @return
   */
  public DynamoRPCRouterServlet getRPCRouterServlet() {
    return mRPCRouterServlet;
  }

  /**
   * Set the RPCRouterServlet property.
   * @param pRPCRouterServlet
   */
  public void setRPCRouterServlet(DynamoRPCRouterServlet pRPCRouterServlet) {
    mRPCRouterServlet = pRPCRouterServlet;
  }

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------
  
  
  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------
  
  public DOMWriterService() {

  }

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  /**
   * This method will allow this service to register itself with
   * a ServiceManager.  The ServiceManager that it registers itself with
   * is the one that is associated with the <code>RPCRouterServlet</code>
   * of this class.
   *
   * <P>It registers itself by creating a <code>DeploymentDescriptor</code>
   * and populating that DeploymentDescriptor with the appropriate values.
   * It set the Id property to that configured in this classes
   * <code>serviceId</code> property.  It will also set the 
   * <code>methods</code> property to be that from the <code>getMethods</code>
   * method.
   *
   * @exception ServiceException if an error occurs
   */
  public void doStartService() 
    throws ServiceException
  {
    DynamoRPCRouterServlet rpcServlet = getRPCRouterServlet();
    
    ServiceManager sm = rpcServlet.getServiceManager();

    if (sm == null) {
      throw new ServiceException(SOAPConstants.NO_SERVICE_MANAGER_FOUND);
    }
    
    // build up deployment descriptor
    DeploymentDescriptor dd = new DeploymentDescriptor();
    
    dd.setID(getServiceId()); 
    dd.setProviderClass(this.getClass().getName());
    dd.setProviderType(DeploymentDescriptor.PROVIDER_JAVA); 
    dd.setIsStatic(false);
    dd.setScope(DeploymentDescriptor.SCOPE_APPLICATION);
    dd.setMethods(getMethods());
    try {
      sm.deploy(dd);
    }
    catch (SOAPException se) {
      throw new ServiceException(se);
    }
  }

  /**
   * This is the main method that is exposed to the SOAP
   * server.  It will receive an <code>org.w3c.dom.Element</code>
   * object and print the tree structur of the element object.
   *
   * <P>This printing is performed by using the <code>
   * javax.xml.transform</code> package to create an 
   * identity transformer and render the output.
   *
   * <P>When rendering the output, the output will be rendered
   * to a created <code>StreamResult</code> object.  The 
   * <code>StreamResult</code> object will be created with
   * the OutputStream returned by <code>getOutputStream</code>/
   *
   * @param pElement the element whose tree structure will be 
   * rendered.
   * @return success if transformation was successful, else failure
   */
  public String receiveDocument(Element pElement) {
    if (isLoggingDebug())
      logDebug("Received document");

    try {

      TransformerFactory factory = TransformerFactory.newInstance();
      
      // Create an identity transformer (i.e., no stylesheet will be applied)
      Transformer transformer = factory.newTransformer();
    
      // Set various output properties
      transformer.setOutputProperty(OutputKeys.INDENT, String.valueOf(IsIndent()));
      transformer.setOutputProperty(OutputKeys.METHOD, getOutputMethod());

      // Use the Transformer to transform the document and send the
      //    output to pWriter.
      transformer.transform(new DOMSource(pElement), 
                            new StreamResult(getOutputStream()));
    }
    catch (Exception e) {
      if (isLoggingError())
        logError(e);
      return FAILURE;
    }
    
    return SUCCESS;
  }

}   // end of class
