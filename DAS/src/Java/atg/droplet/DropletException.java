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

import atg.core.exception.ContainerException;
import javax.servlet.*;

/**
 * <p>Represents an exception within Droplet
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletException.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public
class DropletException extends ServletException {
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletException.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Member variables

  /** 
   * The message code representing the error.  If it is not set, we return
   * the name of the class of this exception 
   */
  String mErrorCode = null;

  //-------------------------------------
  /**
   * Constructs a new DropletException with the given explanation
   **/
  public DropletException (String pStr) 
  {
    super (pStr);
  }

  //-------------------------------------
  /**
   * Constructs a new DropletException with the given explanation
   * and root exception.
   **/
  public DropletException (String pStr,
                           Throwable pRootCause) 
  {
    super (pStr, pRootCause);
  }

  //-------------------------------------
  /**
   * Constructs a new DropletException with the given message and
   * detailed message.  For exceptions that can be caused by "user error"
   * you can include a ErrorCode in addition to the text message for
   * the exception.  This allows the jhtml pages to switch off of this
   * code to customize the output.
   */
  public DropletException (String pMessage, String pErrorCode) {
    super(pMessage);
    mErrorCode = pErrorCode;
  }

  //-------------------------------------
  /**
   * Constructs a new DropletException with the given explanation,
   * root exception, and the message code.
   **/
  public DropletException (String pStr, Throwable pRootCause, 
  			   String pErrorCode) 
  {
    super (pStr, pRootCause);
    mErrorCode = pErrorCode;
  }

  //------------------------------------
  /** 
   * Returns a the coded message for this error.
   */
  public String getErrorCode() {
    if (mErrorCode == null) {
      if (getRootCause() != null) return getRootCause().getClass().getName();
      return getClass().getName();
    }
    return mErrorCode;
  }

  /**
   * Returns a description of this DropletException object.
   */
  public String toString() {
    String code = getErrorCode();
    String message = getMessage();
    if (message == null && getRootCause() != null)
      message = getRootCause().getMessage();
    return (message != null) ? (code + ": " + message) : code;
  }
  //-------------------------------------
}

