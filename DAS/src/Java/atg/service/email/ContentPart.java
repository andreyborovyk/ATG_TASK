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

package atg.service.email;

/** 
 * A data structure corresponding to a body part in a 
 * javax.mail.Message.  Contains the content of the body part,
 * as well as the MIME type of the content.
 * 
 * @see MimeMessageUtils
 * @author Natalya Cohen
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/service/email/ContentPart.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class ContentPart {
  //-------------------------------------
  // Constants
  //-------------------------------------

  /** Class version string **/
  public static String CLASS_VERSION = 
  "$Id: //product/DAS/version/10.0.3/Java/atg/service/email/ContentPart.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Member variables
  //-------------------------------------

  /** Object containing the content **/
  Object mContent;

  /** The MIME type of the content **/
  String mContentType;

  //-------------------------------------
  // Constructors
  //-------------------------------------

  //-------------------------------------
  /**
   * Creates a new ContentPart object.
   **/
  public ContentPart() {
  }

  //-------------------------------------
  /**
   * Creates a new ContentPart object with the given content 
   * and MIME type.
   **/
  public ContentPart(Object pContent, String pContentType) {
    setContent(pContent);
    setContentType(pContentType);
  }

  //-------------------------------------
  // Methods
  //-------------------------------------

  //-------------------------------------
  /**
   * Returns the content.
   **/
  public Object getContent() {
    return mContent;
  }

  //-------------------------------------
  /**
   * Sets the content.
   **/
  public void setContent(Object pContent) {
    mContent = pContent;
  }

  //-------------------------------------
  /**
   * Returns the MIME type of the content.
   **/
  public String getContentType() {
    return mContentType;
  }

  //-------------------------------------
  /**
   * Sets the MIME type of the content.
   **/
  public void setContentType(String pContentType) {
    mContentType = pContentType;
  }

  //-------------------------------------
}
