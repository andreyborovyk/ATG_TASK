/*<ATGCOPYRIGHT>
 * Copyright (C) 2010-2011 Art Technology Group, Inc.
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

package atg.projects.store.email;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import atg.projects.store.profile.StorePropertyManager;
import atg.repository.RepositoryItem;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailSender;

/**
 * This is a Store extention of DPS' TemplateEmailSender component.
 * This extention passes current locale (if set) into the e-mail template to be sent.
 * @see TemplateEmailSender
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/email/StoreTemplateEmailSender.java#2 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class StoreTemplateEmailSender extends TemplateEmailSender
{
  /**
   * Class version
   */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/email/StoreTemplateEmailSender.java#2 $$Change: 651448 $";
  
  /**
   * If this parameter is set to 'true' on HTTP request, then request is issued to render an email body.
   */
  public static final String PARAMETER_NAME_EMAIL_REQUEST = "isEmailRequest";
  
  private StorePropertyManager mPropertyManager;

  /**
   * Link to a user properties manager to be used by this component.
   * @return
   */
  public StorePropertyManager getPropertyManager() {
    return mPropertyManager;
  }

  public void setPropertyManager(StorePropertyManager pPropertyManager) {
    mPropertyManager = pPropertyManager;
  }

  /**
   * This implementation does the following:
   * <ul>
   *  <li>obtain current locale from recipient specified by pRecipients parameter (if there is only one recipient)
   *  <li>if emailInfo's template parameters doesn't contain locale parameter and locale is defined, put locale into template parameters
   *  <li>add template parameter to mark request as email 
   *  <li>send email with super-method
   * </ul>
   */
  @SuppressWarnings("unchecked") //ok, cause we use <Object, Object> generic map
  @Override
  public void sendEmailMessage(TemplateEmailInfo pEmailInfo, Collection pRecipients, boolean pRunInSeparateThread, boolean pPersist)
      throws TemplateEmailException
  {
    Map<Object, Object> templateParameters = pEmailInfo.getTemplateParameters();
    if (templateParameters == null)
    {
      templateParameters = new HashMap<Object, Object>();
    }
    // We should set locale parameter for individual email senders only, otherwise we can't determine proper locale for all recipients.
    // Do not set locale parameter if it's specified by emailInfo already.
    if (pRecipients.size() == 1 && !templateParameters.containsKey(getLocalePropertyName()))
    {
      String locale = (String) ((RepositoryItem) pRecipients.iterator().next()).getPropertyValue(getPropertyManager().getLocalePropertyName());
      templateParameters.put(getLocalePropertyName(), locale);
    }
    // Mark future request as email, this will be used by SiteDefaultLocaleProcessor.
    templateParameters.put(PARAMETER_NAME_EMAIL_REQUEST, Boolean.TRUE);
    pEmailInfo.setTemplateParameters(templateParameters);
    
    super.sendEmailMessage(pEmailInfo, pRecipients, pRunInSeparateThread, pPersist);
  }
}
