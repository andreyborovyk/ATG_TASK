/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

package atg.projects.store.crypto;

import atg.core.exception.ContainerException;

/**
 * This exception indicates that a severe error occured while performing a
 * cryptograpy operation.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/crypto/EncryptorException.java#2 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class EncryptorException extends ContainerException
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/crypto/EncryptorException.java#2 $$Change: 651448 $";


  //-------------------------------------
  // Constants
  //-------------------------------------

  //-------------------------------------
  // Member Variables
  //-------------------------------------

  //-------------------------------------
  // Properties
  //-------------------------------------
    
  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs a new EncryptorException.
   **/
  public EncryptorException() {
    super();
  }

  /**
   * Constructs a new EncryptorException with the given 
   * explanation.
   *
   * @param pStr String that describes exception
   * */
  public EncryptorException(String pStr) {
    super(pStr);
  }

  /**
   * Constructs a new EncryptorException.
   * @param pSourceException the initial exception which was the root
   * cause of the problem
   **/
  public EncryptorException(Throwable pSourceException) {
    super(pSourceException);
  }

  /**
   * Constructs a new EncryptorException with the given 
   * explanation.
   * @param pStr String that describes exception
   *
   * @param pSourceException the initial exception which was the root
   * cause of the problem
   **/
  public EncryptorException(String pStr, Throwable pSourceException) {
    super(pStr, pSourceException);
  }
} // end of class
