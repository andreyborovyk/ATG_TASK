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
import atg.commerce.order.ElectronicShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.repository.RepositoryItem;
import atg.service.email.*;
import atg.userprofiling.email.*;

import java.util.*;

/**
 * This processor delivers an electronic item by the email Listener action
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcDeliverElectronicGoodByListener.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcDeliverElectronicGoodByListener implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcDeliverElectronicGoodByListener.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcDeliverElectronicGoodByListener() {
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
   * Delivers an electronic item by the email Listener.
   *
   * This method requires that an OrderFulfiller object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an OrderFulfiller object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    ElectronicFulfiller of = (ElectronicFulfiller) map.get(PipelineConstants.ORDERFULFILLER);
    ElectronicShippingGroup pShippingGroup = (ElectronicShippingGroup) map.get(PipelineConstants.SHIPPINGGROUP);
    RepositoryItem pElectronicClaimable = (RepositoryItem) map.get(PipelineConstants.ELECTRONICGOOD);

    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));

    String emailAddress = pShippingGroup.getEmailAddress();

    if (of.isLoggingDebug())
        of.logDebug("Using email listener to deliver email to " + emailAddress);
    
    EmailListener emailListener;
    EmailEvent email;
    // email the newly created object
    if (of.getEmailListeners() != null && of.getEmailListeners().size() != 0) {
        // create email object
        email = createEmailObject(of, pShippingGroup.getEmailAddress(), 
                                  pElectronicClaimable.getRepositoryId());
        int listSize = of.getEmailListeners().size();
        for (int i=0; i<listSize; i++) {
            emailListener = (EmailListener) of.getEmailListeners().get(i);
            emailListener.sendEmailEvent(email);
        }
    } else {
        throw new EmailException(ResourceUtils.getMsgResource("NoEmailListeners",
                                                              MY_RESOURCE_NAME, sResourceBundle));
    }

    return SUCCESS;
  }

  
  /**
   * This method will create an EmailEvent that corresponds to the 
   * message to send out.  It will send the message to the supplied
   * email address.  Additionally, it can take a claim code which is
   * tied to the claimable repository.  That is, by default the claim
   * code can be used to obtain something from the claimable repository.
   *
   * The EmailEvent is created with the message body that is
   * returned by the createEmailMessageBody method.  Also, the
   * from address is set to the defaultFromAddress property
   * of this class.
   *
   * @param pRecipientEmailAddress email address that this email will be sent to
   * @param pClaimCode code to claim something from the ClaimableRepository
   * @return a value of type 'EmailEvent'
   */
  protected EmailEvent createEmailObject(ElectronicFulfiller of,
                                         String pRecipientEmailAddress,
                                         String pClaimCode) 
  {
    EmailEvent email;
    
    if (pRecipientEmailAddress == null) {
        if(of.isLoggingError())          
          of.logError(Constants.RECIPIENT_EMAIL_ADDRESS_NULL);
    }
    
    String messageBody = createEmailMessageBody(of, pClaimCode);
    if (of.isLoggingDebug())
        of.logDebug("Sending an email event with from address: " +
                    of.getDefaultFromAddress() + " recipientAddress: " +
                    pRecipientEmailAddress + " subject: " +
                    of.getDefaultSubject() + " body: " + messageBody);
    
    email = new EmailEvent(of.getDefaultFromAddress(),
                           pRecipientEmailAddress,
                           of.getDefaultSubject(),
                           messageBody);
    return email;
  }
  

  /**
   * Creates the email message body that will be put into the email.  By default
   * it just appends the claim code onto the defaultMessageBody property.
   *
   * @param pClaimCode code that is used to claim things from the ClaimbleRepository
   * @return the email message body
   */
  protected String createEmailMessageBody(ElectronicFulfiller of, String pClaimCode) {
    return of.getDefaultMessageBody() + ": " + pClaimCode;
  }

}
