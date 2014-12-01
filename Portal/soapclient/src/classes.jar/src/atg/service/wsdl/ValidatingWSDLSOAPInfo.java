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
import java.util.*;

// DAS classes
import atg.core.util.*;

// DPS classes

// DSS classes

// DCS classes

/**
 * This class layers on functionality ontop of the WSDLSOAPInfo
 * object by allowing the object to know if its <em>valid</em>.
 * Valid can mean different things depending on what the system
 * is capable of handling.  This class is intended to encapsulate
 * the semantics of <em>valid</em>.
 *
 * <P>Currently, there are two rules that are capable of being enforced
 * by this class:
 *
 * <UL>
 *   <LI>Single Method Defined - Only one method can be defined by
 *       a particular WSDL document.  If more then one method is
 *       defined we can't map between particular parameters and that
 *       method.
 *   <LI>Java Primitive (and wrapper types) - Only supported method
 *       arguments are primitive types and java.lang.String.  
 *       There is no system in place
 *       to take complex data structures.
 * </UL>
 *
 * @author Ashley J. Streb
 * @version $Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/service/wsdl/ValidatingWSDLSOAPInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ValidatingWSDLSOAPInfo
  extends WSDLSOAPInfo
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/service/wsdl/ValidatingWSDLSOAPInfo.java#2 $$Change: 651448 $";

  private static final String[] sPrimitiveTypes =
  {"java.lang.String", "java.lang.Integer", "java.lang.Byte", "java.lang.Short", "java.lang.Boolean", "java.lang.Character", "java.lang.Long", "java.lang.Float", "java.lang.Double"};

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  private boolean methodNameSetMultipleTimes = false;

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------
  
  /**
   * Overrides the <code>setTargetMethodName</code> method from the
   * super class so that it knows if multiple methodNames are
   * attempted to be set. It "knows" this based upon if the method
   * name is attempted to be set multiple times.
   *
   * @param pTargetMethodName a <code>String</code> value
   */
  public void setTargetMethodName(String pTargetMethodName) {
    if (! StringUtils.isBlank(getTargetMethodName())) {
      methodNameSetMultipleTimes = true;
    }

    super.setTargetMethodName(pTargetMethodName);
  }

  
  /**
   * This is a helper method that determines if a given
   * class type (represented as a String name) is either a Java primitive
   * or a corresponding wrapper class or a string.
   *
   * @param pClassType a <code>String</code> value
   * @return a <code>boolean</code> value
   */
  protected boolean isTypePrimitive(String pClassType) {
    for (int i=0; i<sPrimitiveTypes.length; i++) {
      if (sPrimitiveTypes[i].equalsIgnoreCase(pClassType)) {
        return true;
      }
    }
    return false;
  }


  /**
   * Validates that the parameterTypes for a given method can be
   * understood by the system.  It enforces that each parameterType
   * supplied is a Java primitive (or its wrapper class) or a String
   *
   * @exception InvalidPropertyTypeException if an error occurs
   */
  protected void validateParameterTypes() 
    throws InvalidPropertyTypeException
  {
    WSDLSOAPParameter param;
    Collection params = getServiceParameters();
    if (params != null && params.size() > 0) {
      Iterator paramTypes = params.iterator();
      while (paramTypes.hasNext()) {
        param = (WSDLSOAPParameter) paramTypes.next();
        String classType = (String) param.getParameterClassType();
        if (! isTypePrimitive(classType)) {
          throw new InvalidPropertyTypeException();
        }
      }
    }
  }
  
  /**
   * Validates that only a single method name was attempted to be set
   *
   * @exception InvalidNumberMethodsException if an error occurs
   */
  protected void validateNumberMethods() 
    throws InvalidNumberMethodsException
  {
    if (methodNameSetMultipleTimes == true) {
      throw new InvalidNumberMethodsException();
    }
  }
  

  /**
   * Checks for a valid WSDLSOAPInfo object by invoking the
   * validateNumberMethods and validateParameterTypes methods.
   *
   * @exception InvalidPropertyTypeException if an error occurs
   * @exception InvalidNumberMethodsException if an error occurs
   * @exception WSDLException if an error occurs
   */
  public void validate() 
    throws InvalidPropertyTypeException,
           InvalidNumberMethodsException,
           WSDLException
  {
    validateNumberMethods();
    validateParameterTypes();
  }
  
}   // end of class
