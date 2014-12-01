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

package atg.commerce.order.processor;

import atg.commerce.order.*;
import atg.commerce.payment.*;
import atg.service.pipeline.*;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.payment.PaymentStatus;
import atg.nucleus.logging.ApplicationLoggingImpl;

import java.util.*;
import java.text.*;

/**
 * This processor authorizes payment for an Order in the form of a PaymentGroup.
 * It calls the authorize() method in the PaymentManager passing it a list of
 * PaymentGroups.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcAuthorizePayment.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.commerce.payment.PaymentManager
 */
public class ProcAuthorizePayment extends ApplicationLoggingImpl implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcAuthorizePayment.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcAuthorizePayment() {
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

  //-------------------------------------
  // property: paymentManager
  //-------------------------------------
  private PaymentManager mPaymentManager = null;

  /**
   * Returns the paymentManager
   */
  public PaymentManager getPaymentManager() {
    return mPaymentManager;
  }

  /**
   * Sets the paymentManager
   */
  public void setPaymentManager(PaymentManager pPaymentManager) {
    mPaymentManager = pPaymentManager;
  }

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcAuthorizePayment";

  /**
   * Sets property LoggingIdentifier
   **/
  public void setLoggingIdentifier(String pLoggingIdentifier) {
    mLoggingIdentifier = pLoggingIdentifier;
  }

  /**
   * Returns property LoggingIdentifier
   **/
  public String getLoggingIdentifier() {
    return mLoggingIdentifier;
  }

  //-----------------------------------------------
  /**
   * This method authorizes payment for an Order in the form of a PaymentGroup.
   * It calls the authorize() method in the PaymentManager passing it a list of
   * PaymentGroups.
   *
   * For each PaymentGroup that failed to authorize, extract out the message from
   * the PaymentResult object and call addErrorToPipelineResult.
   *
   * This method requires that an Order object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   *
   * @param pParam a HashMap which must contain an Order object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   * @see atg.commerce.payment.PaymentManager#authorize(List)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    List failed = null;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    String id;

    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                                                       MY_RESOURCE_NAME, sResourceBundle));

    if (isLoggingDebug())
      logDebug("Authorizing PaymentGroups: " + order.getPaymentGroups());

    failed = getPaymentManager().authorize(order, order.getPaymentGroups());

    if (failed.size() > 0) {
      if (isLoggingDebug())
        logDebug("The following payment groups failed authorization: " + failed);

      ResourceBundle bundle = null;
      Locale resourceLocale = (Locale) map.get(PipelineConstants.LOCALE);
      if (resourceLocale != null) {
        bundle = LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, resourceLocale);
      } else {
        bundle = sResourceBundle;
      }

      // for each failed group, generate a message
      for (Iterator iter = failed.iterator(); iter.hasNext(); ) {
        id = (String) iter.next();
        PaymentGroup failedPaymentGroup = order.getPaymentGroup(id);

        String statusMessage = null;

        List authList = order.getPaymentGroup(id).getAuthorizationStatus();
        if (authList != null) {
          PaymentStatus stat = (PaymentStatus) authList.get(authList.size() - 1);
          if (stat != null)
            statusMessage = stat.getErrorMessage();
        }
        if (statusMessage == null)
          statusMessage = "";

        addErrorToPipelineResult(failedPaymentGroup, statusMessage, pResult, bundle);

      } // end for each failed group

    } // end if there were failures

    if (pResult.hasErrors()) {
      return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
    }

    return SUCCESS;
  }


  /**
   * This method handles calling the appropriate method to place errors into
   * the Pipeline Result object.  It will do an instanceof check on the pFailedPaymentGroup
   * object and call the appropriate method.  The current mapping is as follows:
   *
   * <BR>
   *
   * <UL>
   *    <LI> GiftCertificate - call is made to addGiftCertificateError
   *    <LI> CreditCard - call is made to addCreditCardError
   *    <LI> PaymentGroup - if payment group has not been handled yet, then call
   *         addPaymentGroupError.
   * </UL>
   *
   * <BR>
   *
   * If a new PaymentGroup is added and the error messages need to be handled in a special
   * way, then this class should be extended and this method overriden.  The overriden method
   * would make instanceof checks.  If the PaymentGroup is not handled by any of the instanceof
   * checks in that class then call the super method.
   *
   * @param pFailedPaymentGroup the payment group that failed to authorize
   * @param pStatusMessage message indicating why the payment group failed to authorize
   * @param pResult the pipeline result object.
   * @param pBundle resource bundle specific to users locale
   */
  protected void addErrorToPipelineResult(PaymentGroup pFailedPaymentGroup,
                                          String pStatusMessage,
                                          PipelineResult pResult,
                                          ResourceBundle pBundle)
  {
    if (pFailedPaymentGroup instanceof GiftCertificate) {
      addGiftCertificateError(pFailedPaymentGroup, pStatusMessage, pResult, pBundle);
    }
    else if (pFailedPaymentGroup instanceof CreditCard) {
      addCreditCardError(pFailedPaymentGroup, pStatusMessage, pResult, pBundle);
    }
    else {
      addPaymentGroupError(pFailedPaymentGroup, pStatusMessage, pResult, pBundle);
    }
  }


  /**
   * This method handles adding authorization error messages to the pipeline result
   * object.  It creates an errorMessage and errorKey and adds these to the ResultObject.
   *
   * @param pFailedPaymentGroup the payment group that failed to authorize
   * @param pStatusMessage message indicating why the payment group failed to authorize
   * @param pResult the pipeline result object.
   * @param pBundle resource bundle specific to users locale
   */
  protected void addGiftCertificateError(PaymentGroup pFailedPaymentGroup, String pStatusMessage,
                                         PipelineResult pResult, ResourceBundle pBundle)
  {
    String errorMessage;
    String errorKey;

    errorMessage = MessageFormat.format(pBundle.getString("FailedGiftCertificateAuthorization"),
                                        new Object [] {
                                          ((GiftCertificate)pFailedPaymentGroup).getGiftCertificateNumber(),
                                          pStatusMessage
                                        });
    errorKey = "FailedGiftCertAuth:" + pFailedPaymentGroup.getId();
    pResult.addError(errorKey, errorMessage);
  }


  /**
   * This method handles adding authorization error messages to the pipeline result
   * object.  It creates an errorMessage and errorKey and adds these to the ResultObject.
   *
   * @param pFailedPaymentGroup the payment group that failed to authorize
   * @param pStatusMessage message indicating why the payment group failed to authorize
   * @param pResult the pipeline result object.
   * @param pBundle resource bundle specific to users locale
   */
  protected void addCreditCardError(PaymentGroup pFailedPaymentGroup, String pStatusMessage,
                                    PipelineResult pResult, ResourceBundle pBundle)
  {
    String errorMessage;
    String errorKey;

    errorMessage = MessageFormat.format(pBundle.getString("FailedCreditCardAuthorization"),
                                        new Object [] {
                                          ((CreditCard)pFailedPaymentGroup).getCreditCardType(),
                                          ((CreditCard)pFailedPaymentGroup).getCreditCardNumber(),
                                          pStatusMessage
                                        });
    errorKey = "FailedCreditCardAuth:" + pFailedPaymentGroup.getId();
    pResult.addError(errorKey, errorMessage);
  }


  /**
   * This method handles adding authorization error messages to the pipeline result
   * object.  It creates an errorMessage and errorKey and adds these to the ResultObject.
   *
   * @param pFailedPaymentGroup the payment group that failed to authorize
   * @param pStatusMessage message indicating why the payment group failed to authorize
   * @param pResult the pipeline result object.
   * @param pBundle resource bundle specific to users locale
   */
  protected void addPaymentGroupError(PaymentGroup pFailedPaymentGroup, String pStatusMessage,
                                      PipelineResult pResult, ResourceBundle pBundle)
  {
    String errorMessage;
    String errorKey;

    errorMessage = MessageFormat.format(pBundle.getString("FailedPaymentGroupAuthorization"),
                                        new Object [] {
                                          pFailedPaymentGroup.getId(),
                                          pStatusMessage
                                        });
    errorKey = "FailedPaymentGroupAuth:" + pFailedPaymentGroup.getId();
    pResult.addError(errorKey, errorMessage);
  }
}
