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
import java.io.*;
import java.net.*;
import java.util.*;

// SOAP classes
import atg.apache.soap.util.xml.*;
import atg.apache.soap.*;
import atg.apache.soap.rpc.*;

// DAS classes
import atg.nucleus.GenericService;

// DPS classes

// DSS classes

// DCS classes

/**
 * This service can be used to send a series of objects via
 * a SOAP request.  The method that should be used to send
 * a request out via SOAP is the {@link #invoke(Vector)<code>invoke</code>}
 * method.
 *
 * @author Ashley J. Streb
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/soap/SimpleSOAPClient.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class SimpleSOAPClient
  extends GenericService
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/soap/SimpleSOAPClient.java#2 $$Change: 651448 $";

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
  // property: SOAPServerURL
  URL mSOAPServerURL;

  /**
   * The URL that this SOAP client should make a request to.
   * This is used as an argument to the <code>Call.invoke()</code> method.
   * An example would be: <i>http://localhost:8080/soap/servlet/rpcrouter</i>
   * @return the Server URL
   */
  public String getSOAPServerURL() {
    if (mSOAPServerURL != null)
      return mSOAPServerURL.toString();
    else
      return null;
  }

  /**
   * Set the SOAPServerURL property.  When the SOAPServerURL is set,
   * it will attempt to create a new URL object and assign this to
   * a member variable.  An error will be logged if a <code>
   * MalformedURLException</code> is thrown.
   * @param pSOAPServerURL
   */
  public void setSOAPServerURL(String pSOAPServerURL)
    throws MalformedURLException
  {
    try {
      mSOAPServerURL = new URL(pSOAPServerURL);
    }
    catch (MalformedURLException mfue) {
      if (isLoggingError())
        logError(mfue);
      throw mfue;
    }
  }


  //---------------------------------------------------------------------
  // property: SOAPActionURI
  String mSOAPActionURI;

  /**
   * Used by the <code>Call.invoke()</code> method.
   * @return SOAPActionURI property
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



  //---------------------------------------------------------------------
  // property: checkForFault
  boolean mCheckForFault;

  /**
   * If this component is configured to look for faults in the <code>Response
   * </code> object, then the {@link processFaults<code>processFaults</code>}
   * method will be invoked.
   * @return whether or not the Response object should be checked for faults
   */
  public boolean isCheckForFault() {
    return mCheckForFault;
  }

  /**
   * Set the checkForFault property.
   * @param pCheckForFault
   */
  public void setCheckForFault(boolean pCheckForFault) {
    mCheckForFault = pCheckForFault;
  }


  //---------------------------------------------------------------------
  // property: checkForReturnValue
  boolean mCheckForReturnValue;

  /**
   * If this component is configured to look for a response from the
   * server in the <code>Response</code> object, then
   * the {@link processFaults<code>processReturnValue</code>}
   * method will be invoked.
   * @return whether or not the Response object should be checked for return
   * values
   */
  public boolean isCheckForReturnValue() {
    return mCheckForReturnValue;
  }

  /**
   * Set the checkForReturnValue property.
   * @param pCheckForReturnValue
   */
  public void setCheckForReturnValue(boolean pCheckForReturnValue) {
    mCheckForReturnValue = pCheckForReturnValue;
  }


  //---------------------------------------------------------------------
  // property: TargetObjectURI
  String mTargetObjectURI;

  /**
   * The targetObjectURI that is set on the <code>Call</code> object.
   * This is what the SOAP server uses to route incoming request
   * to a particular service.
   * @return the targetObjectURI
   */
  public String getTargetObjectURI() {
    return mTargetObjectURI;
  }

  /**
   * Set the TargetObjectURI property.
   * @param pTargetObjectURI
   */
  public void setTargetObjectURI(String pTargetObjectURI) {
    mTargetObjectURI = pTargetObjectURI;
  }


  //---------------------------------------------------------------------
  // property: methodName
  String mMethodName;

  /**
   * The method name that is set on the <code>Call</code> object.
   * @return
   */
  public String getMethodName() {
    return mMethodName;
  }

  /**
   * Set the methodName property.
   * @param pMethodName
   */
  public void setMethodName(String pMethodName) {
    mMethodName = pMethodName;
  }


  //---------------------------------------------------------------------
  // property: encodingStyleURI
  String mEncodingStyleURI = Constants.NS_URI_SOAP_ENC;

  /**
   * The encoding style that should be set on the <code>Call</code>
   * object.  This defaults to {@link atg.apache.soap.Constants<code>
   * Constants.NS_URI_SOAP_ENC</code>}
   * @return the encoding sytle
   */
  public String getEncodingStyleURI() {
    return mEncodingStyleURI;
  }

  /**
   * Set the encodingStyleURI property.
   * @param pEncodingStyleURI
   */
  public void setEncodingStyleURI(String pEncodingStyleURI) {
    mEncodingStyleURI = pEncodingStyleURI;
  }


  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  //--------------------------------------------------
  // Methods
  //--------------------------------------------------

  /**
   * This method returns a <code>Call</code> object.  This creates
   * a new <code>Call</code> object and sets the following properties
   * on it.
   *
   * <ul>
   *    <li><i>targetObjectURI</i>
   *    <li><i>methodName</i>
   *    <li><i>encodingStyleURI</i>
   * </ul>
   *
   * this method can be overriden if the Call method needed to be
   * constructed in a different manner.
   * @return a <code>Call</code> object
   */
  protected Call getCallObject() {
    if (isLoggingDebug())
      logDebug("Going to create Call object with: targetObjectURI: " + getTargetObjectURI()
               + " methodName:" + getMethodName() + " encodingStyleURI:" +
               getEncodingStyleURI());

    Call call = new Call();
    call.setTargetObjectURI (getTargetObjectURI());
    call.setMethodName (getMethodName());
    call.setEncodingStyleURI(getEncodingStyleURI());
    return call;
  }

  /**
   * This provides a simple implementation.  It extracts the faultCode
   * and faultString from the response object and writes it to the
   * error log.
   *
   * @param pResponse the <code>Response</code> object returned
   * from an <code>invoke</code> call.
   */
  protected void processFaults(Response pResponse) {
    if (pResponse.generatedFault() && isLoggingError()) {
      Fault fault = pResponse.getFault();
      logError(fault.getFaultCode());
      logError(fault.getFaultString());
    }
  }

  /**
   * This method is just an adapter method.  It does not do anything.
   * If it is invoked, it will throw a 
   * {@link java.lang.UnsupportedOperationException<code>
   * UnsupportedOperationException</code>}.
   *
   * @param the response from the SOAP invocation
   */
  public void processReturnValue(Response pResponse) {
    throw new UnsupportedOperationException();
  }

  /**
   * This method processes the <code>Response</code> object
   * from the <code>invoke</code> method on a call object.
   * It determines if the <code>Response</code> object should
   * be checked for faults and/or returnValues.  It does this
   * via the <code>isCheckForFault</code> and <code>isCheckForReturnValue
   * </code> methods respectively.
   *
   * <p>
   *
   * If either of the above items should be checked then it will
   * call <code>processFaults</code> and/or <code>processReturnValue</code>
   *
   * @param pResponse a <code>Response</code> value
   */
  protected void processResponse(Response pResponse) {
    if (isCheckForFault()) {
      processFaults(pResponse);
    }

    if (isCheckForReturnValue()) {
      processReturnValue(pResponse);
    }
  }

  /**
   * Add the supplied parameter list to the <code>Call</code>
   * object.  The contents of the <code>pParameters</code>
   * list <em>must</em> be a list of <code>atg.apache.soap.rpc.Parameter</code>
   * objects.
   *
   * @param pCall the call object that is used to invoke SOAP service
   * @param pParameters list of vectors to pass as parameters to the 
   * Call object.  This list must be a list of <code>atg.apache.soap.rpc.Parameter</code>
   * objects.
   */
  protected void addParameters(Call pCall, Vector pParameters) {
    pCall.setParams(pParameters);
  }
  

  /**
   * This is the expected method that will be called to send
   * a Vector of <code>atg.apache.soap.Parameter</code> objects
   * via a SOAP request.
   * 
   * <P>It will create a call object by calling <code>getCallObject</code>
   * and then add the parameters to the call object via the
   * <code>addParameters</code>.   Finally, it will cal
   * invoke on the <code>Call</code> object and determine if any
   * processing needs to be done on the <code>Response</code>
   * object by calling <code>processResponse</code>
   *
   * @param pParameters the Object that contains parameters to be sent in the
   * SOAP rpc call.  This Vector <em>must</em> contain Paramter objects.
   */
  public void invoke(Vector pParameters)
  {
    if (isLoggingDebug())
      logDebug("In send method with object: " + pParameters.toString());

    Call call = getCallObject();
    addParameters(call, pParameters);

    try {
      if (isLoggingDebug())
        logDebug("About to call invoke on Call object");

      Response response = call.invoke(mSOAPServerURL,
                                      getSOAPActionURI());
      processResponse(response);
    }
    catch (SOAPException soap) {
      if (isLoggingError())
        logError(soap);
    }
  }

}   // end of class
