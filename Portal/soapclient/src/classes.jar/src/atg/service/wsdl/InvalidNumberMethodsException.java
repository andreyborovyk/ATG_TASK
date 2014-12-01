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

// DAS classes
import atg.core.exception.ContainerException;

// DPS classes

// DSS classes

// DCS classes

/**
 * Indication that the number of methods defined in a particular
 * WSDL document can not be understood by this system.  Currently,
 * only a single method can be declared per WSDL document.
 * 
 * <P>It is an exception container so it might wrap another exception.
 *
 * @author Ashley J. Streb
 * @version $Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/service/wsdl/InvalidNumberMethodsException.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class InvalidNumberMethodsException
  extends ContainerException
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/soapclient/classes.jar/src/atg/service/wsdl/InvalidNumberMethodsException.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Constructs a new InvalidNumberMethodsException.
   **/
  public InvalidNumberMethodsException() {
    super();
  }


  /**
   * Constructs a new InvalidNumberMethodsException with the given 
   * explanation.
   **/
  public InvalidNumberMethodsException(String pStr) {
    super(pStr);
  }

  /**
   * Constructs a new InvalidNumberMethodsException.
   * @param pSourceException the initial exception which was the root
   * cause of the problem
   **/
  public InvalidNumberMethodsException(Throwable pSourceException) {
    super();
    setSourceException(pSourceException);
  }


  /**
   * Constructs a new InvalidNumberMethodsException with the given explanation.
   * @param pSourceException the initial exception which was the root
   * cause of the problem
   **/
  public InvalidNumberMethodsException(String pStr, 
                                       Throwable pSourceException) 
  {
    super(pStr);
    setSourceException(pSourceException);
  }

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

}   // end of class
