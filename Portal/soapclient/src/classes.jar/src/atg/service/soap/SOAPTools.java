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
import atg.core.util.StringUtils;
import atg.core.util.Base64;
import java.util.*;
import java.net.URL;

// Apache SOAP Classes
import atg.apache.soap.rpc.Response;
import atg.apache.soap.rpc.Call;
import atg.apache.soap.rpc.Parameter;
import atg.apache.soap.Constants;
import atg.apache.soap.Fault;
import atg.apache.soap.transport.http.SOAPHTTPConnection;


// DAS classes
import atg.nucleus.*;


// DPS classes

// DSS classes

// DCS classes

/**
 * This class contains methods that allow a particular SOAP service
 * to be invoked.  While SOAP can flow over any type of wire protocol;
 * HTTP, SMTP this class provides access to a HTTP specific binding.
 * There are two types of soap services that can be invoked:
 *
 * <UL>
 *   <LI>{@link #invokeHTTPRPCCall<code>invokeHTTPRPCCall</code>} which
 *       allows a RPC SOAP based message to flow over the HTTP protocol
 *   <LI>{@link #invokeSecureHTTPRPCCall<code>invokeSecureHTTPRPCCall</code>} 
 *       which allows a RPC SOAP based message to flow over the HTTP protocol
 *       and enables basic-authentication to take place via HTTP headers.
 * </UL>
 *
 * @author Ashley J. Streb
 * @version $Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/service/soap/SOAPTools.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class SOAPTools
  extends GenericService
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  /**
   * Class version string
   */
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/service/soap/SOAPTools.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------
  boolean mEnableProxy = false;

  /**
   * Returns if proxy should enabled
   */
  public boolean isEnableProxy() 
  {
    return mEnableProxy;
  }


  /**
   * Sets value of enableProxy
   */
  public void setEnableProxy(boolean pEnableProxy) 
  {
    mEnableProxy = pEnableProxy;
  }
  
  String mProxyHost;

  /**
   * Returns the value or proxyHost
   */
  public String getProxyHost() 
  {
    return mProxyHost;
  }

  /**
   * Sets the proxyHost
   */
  public void setProxyHost(String pProxyHost) 
  {
    mProxyHost = pProxyHost;
  }


   String mProxyUserName;

  /**
   * Returns the value or proxyUserName
   */
  public String getProxyUserName() 
  {
    return mProxyUserName;
  }

  /**
   * Sets the proxyUserName
   */
  public void setProxyUserName(String pProxyUserName) 
  {
    mProxyUserName = pProxyUserName;
  }

  String mProxyPassword;

  /**
   * Returns the value or proxyPassword
   */
  public String getProxyPassword() 
  {
    return mProxyPassword;
  }

  /**
   * Sets the proxyPassword
   */
  public void setProxyPassword(String pProxyPassword) 
  {
    mProxyPassword = pProxyPassword;
  }
  int mProxyPort;

  /**
   * Returns the value of proxyPort
   */
  public int getProxyPort() 
  {
    return mProxyPort;
  }

  /**
   * Sets the value of proxyPort
   */
  public void setProxyPort(int pProxyPort) 
  {
    mProxyPort = pProxyPort;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------
  
  private Call getCallObject(String pTargetURI, String pTargetMethodName,
                             String pEncodingStyleURI) 
  {
    Call call = new Call();
    call.setTargetObjectURI(pTargetURI);
    call.setMethodName(pTargetMethodName);
    call.setEncodingStyleURI(pEncodingStyleURI);
    return call;
  }

  /**
   * Given a {@link SOAPParameter<code>SOAPParameter</code>} object,
   * this class will generate a parameter object that can be passed
   * to the particular SOAP vendor implementation that this tools
   * class is layered on.
   *
   * @param pParameter a <code>SOAPParameter</code> value
   * @return a value specific to Apaches SOAP implementation
   */
  private Parameter createSOAPParameter(SOAPParameter pParameter) {
    if (isLoggingDebug())
      logDebug("Encountered SOAPParameter with values: "
               + "\nparameterName: " + pParameter.getParameterName()
               + "\nparameterClassType: " + pParameter.getParameterClassType()
               + "\nparameterValue: " + pParameter.getParameterValue());

    return new Parameter(pParameter.getParameterName(), 
                         pParameter.getParameterClassType(), 
                         pParameter.getParameterValue(), 
                         Constants.NS_URI_SOAP_ENC);
  }

  
  /**
   * Given a collection of {@link SOAPParameter<code>SOAPParameter</code>}
   * objects, this method will manufacture a collection of objects
   * that correspond to parameter objects that the underlying SOAP
   * implementation knows how to consume.  This method will invoke
   * the {@link #createSOAPParameter<code>createSOAPParameter</code>}
   * method to generate each parameter object.
   *
   * @param pParamValues a collection of SOAPParameters
   * @return vector of Parameter objects specific to Apaches SOAP 
   * implementation
   */
  private Vector getSOAPParameters(Collection pParamValues) {
    SOAPParameter param;
    
    if (pParamValues == null || pParamValues.size() == 0) {
      return null;
    }
    
    Vector params = new Vector(pParamValues.size());
    
    Iterator paramIterator = pParamValues.iterator();
    while (paramIterator.hasNext()) {
      try {
        param = (SOAPParameter) paramIterator.next();
        if (param != null)
          params.add(createSOAPParameter(param));
      }
      catch (ClassCastException cce) {
        if (isLoggingError())
          logError(SOAPConstants.EXPECTED_SOAP_PARAM_CLASS);
      }
    }
    
    return params;
  }

  
  /**
   * This method actually performs the work of invoking a particular 
   * SOAP service.  It invokes a HTTP based SOAP service and can
   * optionally use http based authentication.
   *
   * @param pTargetURI the uri for the target component/service
   * @param pTargetMethodName the method name on the target object
   * @param pHeaders a hashtable of objects to be placed into the HTTP
   * @param pTargetServerURL service end point
   * @param pMethodParams collection of method params to pass to RPC call
   * @param pSoapActionURI value of the soapAction header to send to the server.
   * @return return value from the service invocation
   * @exception SOAPException if an error occurs
   */
  private Object invokeHTTPCall(String pTargetURI,
                                String pTargetMethodName,
                                String pUserName, String pPassword,
                                URL pTargetServerURL,
                                Collection pMethodParams,
                                String pSoapActionURI)
    throws SOAPException
  {
    Response response;

    if (StringUtils.isBlank(pTargetURI)) {
      throw new IllegalArgumentException(SOAPConstants.NO_TARGET_URI);
    }
    
    if (StringUtils.isBlank(pTargetMethodName)) {
      throw new IllegalArgumentException(SOAPConstants.NO_TARGET_METHOD_NAME);
    }
    
    if (pTargetServerURL == null) {
      throw new IllegalArgumentException(SOAPConstants.NO_TARGET_URL);
    }

    Call call = getCallObject(pTargetURI, pTargetMethodName, 
                              Constants.NS_URI_SOAP_ENC);

    SOAPHTTPConnection conn = new SOAPHTTPConnection();

   

    // set proxy settings if provided
    if (isEnableProxy()) {
      
      conn.setProxyHost(getProxyHost());
      conn.setProxyPort(getProxyPort());

      if (isLoggingDebug())
        logDebug("SOAP Proxy set to host:" + getProxyHost() + "/port:" + getProxyPort());

      if (getProxyUserName() != null) {
        conn.setProxyUserName(getProxyUserName());
        conn.setProxyPassword(getProxyPassword());

        if (isLoggingDebug())
          logDebug("SOAP Proxy user: " + getProxyUserName());
      }
            
      
      
    }
    
    // set basic auth username/password if not null
    if (pUserName != null) {
      conn.setUserName(pUserName);
      conn.setPassword(pPassword);
    }

    call.setSOAPTransport(conn);
    
    Vector params = getSOAPParameters(pMethodParams);
    call.setParams(params);

    try {
      response = call.invoke(pTargetServerURL, pSoapActionURI);
    }
    catch (atg.apache.soap.SOAPException se) {
      if (isLoggingError())
        logError(se);

      throw new SOAPException(se.getMessage());
    }
  
    // if we have a fault log an error and throw general SOAP exception
    if (response.generatedFault()) {
      Fault fault = response.getFault();
      if (isLoggingError())
        logError(fault.getFaultString());

      throw new SOAPException(fault.getFaultString());
    }
    else {
      return response.getReturnValue().getValue();
    }
  }


  /**
   * This method actually performs the work of invoking a particular 
   * SOAP service.  It invokes a HTTP based SOAP service and will
   * return the value returned from the service invocation.
   *
   * <P>The service invocation is considered a RPC invocation (as
   * opposed to document exchange) that flows over HTTP.  All encoding
   * styles for the SOAP invocation default to 
   * <em>http://schemas.xmlsoap.org/soap/encoding</em>
   *
   * <P>For security, you can use HTTP Basic-Authentication.  To
   * use this, use the 
   * {@link #invokeSecureHTTPRPCCall<code>invokeSecureHTTPRPCCall</code>}
   * method.
   *
   * @param pTargetURI the uri for the target component/service
   * @param pTargetMethodName the method name on the target object
   * @param pHeaders a hashtable of objects to be placed into the HTTP
   * @param pTargetServerURL service end point
   * @param pMethodParams collection of method params to pass to RPC call
   * @param pSOAPActionURI the value for the SOAPAction header to be sent to the server
   * @return return value from the service invocation
   * @exception SOAPException if an error occurs
   */
  public Object invokeHTTPRPCCall(String pTargetURI, 
                                  String pTargetMethodName,
                                  URL pTargetServerURL, 
                                  Collection pMethodParams,
                                  String pSOAPActionURI) 
    throws SOAPException
  {
    if (isLoggingDebug())
      logDebug("invokeHTTPRPCCall invoked with: "
               + "\ntargetURI: " + pTargetURI
               + "\ntargetMethodName: " + pTargetMethodName
               + "\nTargetServerURL: " + pTargetServerURL
               + "\nNumber of params: " + pMethodParams.size());
    return invokeHTTPCall(pTargetURI, pTargetMethodName, null, null,
                          pTargetServerURL, pMethodParams, pSOAPActionURI);
  }
  
  
  /**
   * This method actually performs the work of invoking a particular 
   * SOAP service.  It invokes a HTTP based SOAP service and will
   * return the value returned from the service invocation.  It
   * adds the ability to use HTTP based Basic-Authentication.  The username
   * password for Basic-Authentication are based upon the 
   * <em>pUserName</em> and <em>pPassword</em> parameters.  
   *
   * <P>The service invocation is considered a RPC invocation (as
   * opposed to document exchange) that flows over HTTP.  All encoding
   * styles for the SOAP invocation default to 
   * <em>http://schemas.xmlsoap.org/soap/encoding</em>
   *
   * <P>If you don't want  security, you can use 
   * the non HTTP Basic-Authentication service invocation.  To
   * use this, use the 
   * {@link #invokeHTTPRPCCall<code>invokeHTTPRPCCall</code>}
   * method.
   *
   * @param pTargetURI the uri for the target component/service
   * @param pTargetMethodName the method name on the target object
   * @param pUserName username to use for basic authentication
   * @param pPassword password to use for basic authentication
   * @param pTargetServerURL service end point
   * @param pMethodParams collection of method params to pass to RPC call
   * @param pSOAPActionURI the value for the SOAPAction header to be sent to the server 
   * @return return value from the service invocation
   * @exception SOAPException if an error occurs
   */
  public Object invokeSecureHTTPRPCCall(String pTargetURI, 
                                        String pTargetMethodName,
                                        String pUserName,
                                        String pPassword,
                                        URL pTargetServerURL, 
                                        Collection pMethodParams,
                                        String pSOAPActionURI) 
    throws SOAPException
  {
    
    if (isLoggingDebug())
      logDebug("invokeSecureHTTPRPCCall invoked with: "
               + "\ntargetURI: " + pTargetURI
               + "\ntargetMethodName: " + pTargetMethodName
               + "\nUserName: " + pUserName
               + "\nPassword: " + pPassword
               + "\nTargetServerURL: " + pTargetServerURL);

   
     

    return invokeHTTPCall(pTargetURI, pTargetMethodName, pUserName, pPassword,
                            pTargetServerURL, pMethodParams, pSOAPActionURI);
      
  }
  

}   // end of class



