/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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

package atg.droplet;

import javax.servlet.ServletException;

/**
 * <p>This exception is thrown by TagConverters when they encounter
 * an error during the conversion process.
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/TagConversionException.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public
class TagConversionException extends ServletException {
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/TagConversionException.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Member variables

  /** The root exception that caused this exception (or null) */
  Throwable mRootCause = null;

  //-------------------------------------
  /**
   * Constructs a new TagConversionException with the given explanation.
   * @param pStr the explanation
   **/
  public TagConversionException (String pStr) 
  {
    super (pStr);
  }

  //-------------------------------------
  /**
   * Constructs a new TagConversionException with the given explanation and error code.
   * @param pStr the explanation
   * @param pErrorCode the error code
   **/
  public TagConversionException (String pStr, String pErrorCode) 
  {
    super (pStr);
    mErrorCode = pErrorCode;
  }

  //-------------------------------------
  /**
   * Constructs a new TagConversionException with the given explanation
   * and root exception.
   * @param pStr the explanation
   * @param pRootCause the root cause
   * @param pErrorCode the error code
   **/
  public TagConversionException (String pStr,
                           Throwable pRootCause,
			   String pErrorCode) 
  {
    super (pStr);
    mRootCause = pRootCause;
    mErrorCode = pErrorCode;
  }

  //-------------------------------------
  /**
   * Returns the root exception that caused this exception
   **/
  public Throwable getRootCause () 
  {
    return mRootCause;
  }

  //--------- Property: ErrorCode -----------
  String mErrorCode;
  /**
   * Sets the property ErrorCode.
   */
  public void setErrorCode(String pErrorCode) {
    mErrorCode = pErrorCode;
  }
  /**
   * @return The value of the property ErrorCode.
   */
  public String getErrorCode() {
    if (mErrorCode == null) {
      if (mRootCause != null) return mRootCause.getClass().getName();
      return getClass().getName();
    }
    return mErrorCode;
  }


  //-------------------------------------
}

