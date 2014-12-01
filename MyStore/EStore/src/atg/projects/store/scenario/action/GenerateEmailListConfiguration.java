/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2010 Art Technology Group, Inc.
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
package atg.projects.store.scenario.action;

import atg.projects.store.io.EmailListWriter;


/**
 * Configuration file for the GenerateEmailList action.
 *
 * @author ATG
 * @version 1.1
 */
public class GenerateEmailListConfiguration {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/scenario/action/GenerateEmailListConfiguration.java#3 $$Change: 635816 $";

  /**
   * E-mail list writer.
   */
  private EmailListWriter mWriter;

  /**
   * Retrieves the e-mail list writer.
   *
   * @return the writer.
   */
  public EmailListWriter getWriter() {
    return mWriter;
  }

  /**
   * Sets the e-mail list writer.
   *
   * @param pWriter - the writer to set.
   */
  public void setWriter(EmailListWriter pWriter) {
    mWriter = pWriter;
  }
}
