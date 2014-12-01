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

package atg.commerce.order.processor;

// Commerce classes
import atg.commerce.order.*;

// Dynamo classes
import atg.dms.patchbay.*;
import atg.service.pipeline.*;
import atg.service.idgen.*;
import atg.nucleus.logging.ApplicationLoggingImpl;

// Java classes
import javax.jms.*;
import java.util.Map;
import java.io.Serializable;

/**
 * This is a base class that should be extended by other processors that wish to send events
 * from within a chain. @see ProcSendFulfillmentMessage is an example of just such a processor.
 *
 * @author Tareef Kawaf
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/EventSender.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class EventSender extends ApplicationLoggingImpl implements PipelineProcessor, MessageSource
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/EventSender.java#2 $$Change: 651448 $";

  protected final int SUCCESS = 1;
  protected final int FAILURE = 2;
   
  //---------------------------------------------------------------------------
  // property:MessageIdGenerator
  //---------------------------------------------------------------------------
  private IdGenerator mMessageIdGenerator;

  public void setMessageIdGenerator(IdGenerator pMessageIdGenerator) {
    mMessageIdGenerator = pMessageIdGenerator;
  }

  /**
   * The service that generates Ids for all messages.
   **/
  public IdGenerator getMessageIdGenerator() {
    return mMessageIdGenerator;
  }

  //---------------------------------------------------------------------------
  // property:MessageIdSpaceName
  //---------------------------------------------------------------------------
  private String mMessageIdSpaceName;

  public void setMessageIdSpaceName(String pMessageIdSpaceName) {
    mMessageIdSpaceName = pMessageIdSpaceName;
  }

  /**
   * The name of the idspace to get our message ids from
   **/
  public String getMessageIdSpaceName() {
    return mMessageIdSpaceName;
  }

  //-------------------------------------
  /**
   * An empty constructor.
   **/
  public EventSender() {
  }

  /**
   * Uses the id generator to get the next message id.
   **/
  public String getNextMessageId()
  {
    IdGenerator idGen = getMessageIdGenerator();
    String space = getMessageIdSpaceName();
    if (idGen == null)
      return null;

    // generate an id in our id space and return it
    try
    {
      return idGen.generateStringId(space);
    }
    catch (IdGeneratorException ie)
    {
      return null;
    }
  }

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The processor executed successfully
   * 2 - The processor failed to execute properly
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = {SUCCESS, FAILURE};
    return ret;
  }

  //-------------------------------------
  /**
   * This method creates and returns the event to be sent by this processor.  The default
   * implementation returns null which is interpreted as a failed result by the runProcess call.
   **/
  public Serializable createEventToSend(Object pParam, PipelineResult pResult) throws Exception
  {
    return null;
  }

  //-----------------------------------------------
  /**
   * This method will call @see #createEventToSend which will return the event object to be
   * sent.  @see #sendObjectMessage will be called with the event to be sent, along with the
   * @see #getEventType and the @see #getPortName.
   *
   * @param pParam a HashMap which must contains parameters which are added to the message
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invocation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    Serializable eventToSend = createEventToSend(pParam, pResult);

    if (eventToSend == null)
      return FAILURE;
    
    sendObjectMessage(eventToSend, getEventType(eventToSend), getPortName());

    return SUCCESS;
  }


  //-------------------------------------
  /**
   * Sends an object message of type pType, out on port pPortName.  Note, that it is critical
   * that messages being sent are in a transaction.
   *
   * @beaninfo
   *          description: This method sends an object message
   * @param pObjectMessage the object to be placed in the object message.
   * @param pType the type of the message being sent.
   * @param pPortName the port on which this message is going to be sent.
   * @exception JMSException is thrown when a failure to send the message occurs.  This is
   * generally a configuration issue.
   **/

  public void sendObjectMessage(java.io.Serializable pObjectMessage,
				String pType,
				String pPortName)
    throws JMSException
  {
    if (isAllowMessageSending()) {
      //System.out.println("About to create a message");
      ObjectMessage om = getMessageSourceContext().createObjectMessage(pPortName);
      om.setObject(pObjectMessage);
      om.setJMSType(pType);
      //System.out.println("About to send the message of type: " + pType);
      getMessageSourceContext().sendMessage(pPortName, om);
      if (isLoggingDebug())
        logDebug("Sent message: " + om);
    }
    else {
      if (isLoggingWarning())
        logWarning("Attempting to send message with allowMessageSending set to false.");
    }
  }


    //-------------------------------------

  MessageSourceContext mMessageSourceContext;

  /**
   * Set the message source context for this source.  @see atg.dms.patchbay.MessageSource
   **/
  public void setMessageSourceContext(MessageSourceContext pMessageSourceContext) {
    mMessageSourceContext = pMessageSourceContext;
  }

  /**
   * Return the message source context for this source.
   *
   * @beaninfo description: The message source context for this message source.
   **/
  public MessageSourceContext getMessageSourceContext() {
    return mMessageSourceContext;
  }

  //---------------------------------------------------------------------------
  // property: AllowMessageSending determines whether outgoing messages can be sent
  //---------------------------------------------------------------------------
  boolean mAllowMessageSending = false;

  public void setAllowMessageSending(boolean pAllowMessageSending) {
    mAllowMessageSending = pAllowMessageSending;
  }

  /**
   * This class cannot send messages if this is false.  Usually set
   * during parsing of the configuration file.
   *
   * @beaninfo
   *          description: If this is true, this class may send messages.
   **/
  public boolean isAllowMessageSending() {
    return mAllowMessageSending;
  }

  //-------------------------------------
  /**
   * This is called to tell the MessageSource that it may begin
   * sending messages.
   *
   * @beaninfo
   *          description: This method is called to tell the MessageSource that it may begin
   *                       sending messages.
   **/
  public void startMessageSource() {
    mAllowMessageSending=true;
  }

  //-------------------------------------
  /**
   *
   * This is called to tell the MessageSource that it should stop
   * sending messages.
   *
   * @beaninfo
   *          description: This method is called to tell the MessageSource that it
   *                       should stop sending messages.
   **/
  public void stopMessageSource () {
    mAllowMessageSending=false;
  }


  //---------------------------------------------------------------------------
  // property: EventType
  //---------------------------------------------------------------------------

  String mEventType;

  public void setEventType(String pEventType) {
    mEventType = pEventType;
  }

  /**
   * The type of the event, this should be set to such things as: atg.commerce.order.Order if
   * that is what is being sent.
   * @deprecated Subclasses of this (such as ProcSendScenarioEvent) may dynamically determine the type of event.
   *              meaning getEventType may not be thread safe (since the type is set in "createEventToSend" and
   *              and checked when calling "sendObjectMessage").  Use the getEventType method that takes an Object
   *              as a parameter instead.
   **/
  public String getEventType() {
    return mEventType;
  }

  /**
   * This will return the event type defined on the class (same behavior as
   * calling <code>getEventType()</code>.  Subclass of this class should extend 
   * this method if they call <code>setEventType</code> during the call
   * to <code>createEventToSend</code>.  Instead of just returning the property
   * it should inspect pEventToSend to see what the type is.
   * 
   * @param pEventToSend
   * @return The event type
   */
  public String getEventType(Object pEventToSend)
  {
    return mEventType;
  }

  //---------------------------------------------------------------------------
  // property: PortName
  //---------------------------------------------------------------------------

  String mPortName;

  public void setPortName(String pPortName) {
    mPortName = pPortName;
  }

  /**
   * The port on which this message is sent.
   **/
  public String getPortName() {
    return mPortName;
  }  
    
  /**
   * Retrieves site id from pipeline parameters 
   * @param pParams pipeline parameters
   * @return site id retrieved from pipeline parameters
   */
  protected String getSiteId(Object pParams){
    //retrieve site from pipeline parameters
    return (String)((Map) pParams).get(PipelineConstants.SITEID);    
  }


} // end of class
