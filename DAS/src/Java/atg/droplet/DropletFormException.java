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

/**
 * <p>Represents an exception that might occur when delivering a droplet
 * event.
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletFormException.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public
class DropletFormException extends DropletException {
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/DropletFormException.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Member variables

  String mPropertyPath;

  //-------------------------------------
  /**
   * Constructs a new DropletFormException with the given explanation
   *
   * @param pStr explanation for form exception
   * @param pPath full path to the form handler property associated
   *                with the exception, e.g. /foo/bar.name
   **/
  public DropletFormException (String pStr, String pPath) 
  {
    super (pStr);
    mPropertyPath = pPath;
  }

  //-------------------------------------
  /**
   * Constructs a new DropletFormException with the given explanation.  You
   * can pass in an optional messageCode field.  This is a one word
   * description of the error.  If you omit this description, the class name
   * of the exception is used instead.
   *
   * @param pStr explanation for form exception
   * @param pPath full path to the form handler property associated
   *                with the exception, e.g. /foo/bar.name
   * @param pMessageCode message code for this exception
   **/
  public DropletFormException (String pStr, String pPath, String pMessageCode) 
  {
    super (pStr, pMessageCode);
    mPropertyPath = pPath;
  }

  //-------------------------------------
  /**
   * Constructs a new DropletFormException with the given explanation
   * and root exception.
   *
   * @param pStr explanation for form exception
   * @param pRootCause root exception
   * @param pPath full path to the form handler property associated
   *                with the exception, e.g. /foo/bar.name
   **/
  public DropletFormException (String pStr,
                               Throwable pRootCause, String pPath) 
  {
    super (pStr, pRootCause);
    mPropertyPath = pPath;
  }

  /**
   * Constructs a new DropletFormException with the given explanation,
   * root exception, path name to the property that caused the exception,
   * and the messageCode for the exception.
   *
   * @param pStr explanation for form exception
   * @param pRootCause root exception
   * @param pPath full path to the form handler property associated
   *                with the exception, e.g. /foo/bar.name
   * @param pMessageCode message code for this exception
   **/
  public DropletFormException (String pStr, Throwable pRootCause, 
  			       String pPath, String pMessageCode) 
  {
    super (pStr, pRootCause, pMessageCode);
    mPropertyPath = pPath;
  }

  //-------------------------------------
  /**
   * Returns the path name of the property that generated this exception
   */
  public String getPropertyPath() {
    return mPropertyPath;
  }

  /**
   * Returns the name of the property that generated this exception
   */
  public String getPropertyName() {
    if (mPropertyPath == null) return null;
    return DropletNames.getPropertyName(mPropertyPath);
  }

  public String toString() {
    return super.toString() + " property=" + getPropertyPath() + " exception=" + 
           getRootCause(); 
  }

  //-------------------------------------
}

