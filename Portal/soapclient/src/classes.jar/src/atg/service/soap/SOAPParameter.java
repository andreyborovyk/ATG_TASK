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

// DAS classes

// DPS classes

// DSS classes

// DCS classes

/**
 * This class encapsulates all of the Java runtime information necessary
 * in order to generate a parameter to be passed to a SOAP implementation. 
 *
 * The {@link SOAPTools<code>SOAPTools</code>} class expects a Collection of
 * these to be passed to the various service invocation methods.
 *
 * @author Ashley J. Streb
 * @version $Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/service/soap/SOAPParameter.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class SOAPParameter
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/service/soap/SOAPParameter.java#2 $$Change: 651448 $";
    
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
  // property: parameterName
  String mParameterName;

  /**
   * The name of the parameter, this corresponds to the parameter name
   * in a given method.  
   *
   * @return the parameter name
   */
  public String getParameterName() {
    return mParameterName;
  }

  /**
   * Set the parameterName property.
   * @param pParameterName
   */
  public void setParameterName(String pParameterName) {
    mParameterName = pParameterName;
  }

  
  //---------------------------------------------------------------------
  // property: parameterClassType
  Class mParameterClassType;

  /**
   * The class type of the parameter.  This corresponds to the expected
   * class type for a given method.
   *
   * @return the class type
   */
  public Class getParameterClassType() {
    return mParameterClassType;
  }

  /**
   * Set the parameterClassType property.
   * @param pParameterClassType
   */
  public void setParameterClassType(Class pParameterClassType) {
    mParameterClassType = pParameterClassType;
  }

  
  //---------------------------------------------------------------------
  // property: ParameterValue
  Object mParameterValue;

  /**
   * The runtime value for a given SOAP parameter that is to be passed
   * to a particular RPC invocation.
   *
   * @return
   */
  public Object getParameterValue() {
    return mParameterValue;
  }

  /**
   * Set the ParameterValue property.
   * @param pParameterValue
   */
  public void setParameterValue(Object pParameterValue) {
    mParameterValue = pParameterValue;
  }

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------
  
  /**
   * Empty constructor
   *
   */
  public SOAPParameter() {
  }
  
  /**
   * Convenience method to create a SOAPParameter.
   *
   * @param pParameterName the name of the parameter for the method
   * @param pParameterClassType the class type of the parameter
   * @param pParameterValue the runtime value to be passed to the method
   */
  public SOAPParameter(String pParameterName, Class pParameterClassType,
                       Object pParameterValue)
  {
    setParameterValue(pParameterValue);
    setParameterName(pParameterName);
    setParameterClassType(pParameterClassType);
  }

  /**
   * Convenience method to create a SOAPParameter.
   *
   * @param pParameterName the name of the parameter for the method
   * @param pParameterClassType the class type of the parameter
   */
  public SOAPParameter(String pParameterName, Class pParameterClassType)
  {
    setParameterName(pParameterName);
    setParameterClassType(pParameterClassType);
  }

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

}   // end of class



