/*<ATGCOPYRIGHT>
 * Copyright (C) 1999-2011 Art Technology Group, Inc.
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

package atg.commerce.fulfillment.processor;

import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;
import atg.commerce.fulfillment.*;
import atg.commerce.order.Order;
import atg.commerce.order.ElectronicShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryException;
import atg.userprofiling.email.*;

import java.util.*;

/**
 * This processor delivers an electronic item by the email template
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcDeliverElectronicGoodByTemplate.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcDeliverElectronicGoodByTemplate implements PipelineProcessor {
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcDeliverElectronicGoodByTemplate.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private static final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcDeliverElectronicGoodByTemplate() {
  }

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The processor completed
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = {SUCCESS};
    return ret;
  } 

  //-----------------------------------------------
  /**
   * This processor delivers an electronic item by the email template
   *
   * This method requires that an Order and OrderFulfiller object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order and OrderFulfiller object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Order pOrder = (Order) map.get(PipelineConstants.ORDER);
    ElectronicFulfiller of = (ElectronicFulfiller) map.get(PipelineConstants.ORDERFULFILLER);
    ElectronicShippingGroup pShippingGroup = (ElectronicShippingGroup) map.get(PipelineConstants.SHIPPINGGROUP);
    RepositoryItem pElectronicClaimable = (RepositoryItem) map.get(PipelineConstants.ELECTRONICGOOD);

    if (pOrder == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    
    String emailAddress = pShippingGroup.getEmailAddress();

    if (of.isLoggingDebug())
        of.logDebug("Using template email sender to deliver email to " + emailAddress);

    RepositoryItem purchaser = getProfileForOrder(of, pOrder);
    TemplateEmailInfo emailInfo = createTemplateEmailInfo(of, purchaser, pElectronicClaimable, 
                                                          1, emailAddress);
    List recipients = new ArrayList();
    recipients.add(emailAddress);
    
      // send the email
    of.getTemplateEmailSender().sendEmailMessage(emailInfo, recipients, 
                                                 of.isSeparateEmailThread(), 
                                                 of.isPersistEmails());
  
    return SUCCESS;
  }

  
  //----------------------------------------
  /**
   * Creates and returns the TemplateEmailInfo to use for sending
   * email within this action.  
   *
   * @param pGiftCertificate The gift certificate being emailed
   * @param pQuantity The number of gift certificates
   **/
  protected TemplateEmailInfo createTemplateEmailInfo(ElectronicFulfiller of,
                                                      RepositoryItem pPurchaser,
                                                      RepositoryItem pGiftCertificate,
                                                      long pQuantity,
                                                      String pRecipientEmail)
    {
        // make a copy of the default email info
        TemplateEmailInfo emailInfo = of.getDefaultTemplateEmailInfo().copy();
        
        // set the template url
        String templateName = of.getGiftCertificateEmailTemplate();
        emailInfo.setTemplateURL(templateName);
        
        Map params = new HashMap();
        params.put(of.getPurchaserParamName(), pPurchaser);
        params.put(of.getGiftCertificateParamName(), pGiftCertificate);
        params.put(of.getQuantityParamName(), Long.valueOf(pQuantity));
        params.put(of.getRecipientEmailParamName(), pRecipientEmail);
        emailInfo.setTemplateParameters(params);
        
        return emailInfo;
    }

  /**
   * Uses the profile repository to get the profile RepositoryItem
   * for the profileId in the order.
   **/
  protected RepositoryItem getProfileForOrder(ElectronicFulfiller of, Order pOrder)
      throws RepositoryException
    {
        if(pOrder == null)
            return null;
        Repository profileRep = of.getProfileRepository();
        String profileId = pOrder.getProfileId();
        RepositoryItem profile = profileRep.getItem(profileId, of.getDefaultProfileType());
        
        return profile;
    }
    
}
