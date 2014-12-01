/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2011 Art Technology Group, Inc.
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
package atg.projects.store.order.purchase;

import atg.droplet.GenericFormHandler;

import atg.userprofiling.email.TemplateEmailSender;


/**
 * This form handler manages the tell a friend functionality.
 * @author ATG
 * @version $Revision: #2 $
 */
public class TellAFriendFormHandler extends GenericFormHandler {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/purchase/TellAFriendFormHandler.java#2 $$Change: 651448 $";

  /**
   * Template e-mail sender.
   */
  private TemplateEmailSender mTemplateEmailSender;

  /**
   * @return template e-mail sender.
   */
  public TemplateEmailSender getTemplateEmailSender() {
    return mTemplateEmailSender;
  }

  /**
   * @param pTemplateEmailSender - template e-mail sender.
   */
  public void setTemplateEmailSender(TemplateEmailSender pTemplateEmailSender) {
    mTemplateEmailSender = pTemplateEmailSender;
  }
}
