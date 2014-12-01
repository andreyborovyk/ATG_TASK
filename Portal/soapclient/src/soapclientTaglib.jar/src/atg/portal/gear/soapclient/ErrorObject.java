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

package atg.portal.gear.soapclient;

// Java classes

// DAS classes

// DPS classes

// DSS classes

// DCS classes

/**
 * This is the Error Object that will be populated with the error message,
 * error code and parameters for the message. 
 *
 * @author Manoj Potturu
 * @version $Id: //app/portal/version/10.0.3/soapclient/soapclientTaglib.jar/src/atg/portal/gear/soapclient/ErrorObject.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ErrorObject
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/soapclient/soapclientTaglib.jar/src/atg/portal/gear/soapclient/ErrorObject.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------


  /** This error code can then be used by  the page programmer
   *  to determine how to lookup their corresponding resources.
   **/
  String mErrorCode;

  //-------------------------------------
  /**
   * Sets The error code of this ErrorObject
   **/
  public void setErrorCode(String pErrorCode) {
    mErrorCode = pErrorCode;
  }

  //-------------------------------------
  /**
   * Returns The error code of this ErrorObject
   **/
  public String getErrorCode() {
    return mErrorCode;
  }


  /** the message of this ErrorObject */
  String mMessage;

  //-------------------------------------
  /**
   * Sets the message of this ErrorObject
   **/
  public void setMessage(String pMessage) {
    mMessage = pMessage;
  }

  //-------------------------------------
  /**
   * Returns the message of this ErrorObject
   **/
  public String getMessage() {
    return mMessage;
  }


  /** the parameter values to be passed to include in messages */
  Object[] mParameterValues;

  //-------------------------------------
  /**
   * Sets the parameter values to be passed to include in messages
   **/
  public void setParameterValues(Object[] pParameterValues) {
    mParameterValues = pParameterValues;
  }

  //-------------------------------------
  /**
   * Returns the parameter values to be passed to include in messages
   **/
  public Object[] getParameterValues() {
    return mParameterValues;
  }

  
  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------
  public ErrorObject() {
  }
  
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

}   // end of class
