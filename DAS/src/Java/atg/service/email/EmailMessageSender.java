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

import javax.mail.Message;
import javax.mail.Transport;

/**
 * An interface for sending javax.mail.Message objects.
 * 
 * @author Natalya Cohen
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/service/email/EmailMessageSender.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public interface EmailMessageSender {
  //-------------------------------------
  // Constants
  //-------------------------------------

  /** Class version string **/
  public static String CLASS_VERSION = 
  "$Id: //product/DAS/version/10.0.3/Java/atg/service/email/EmailMessageSender.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Methods
  //-------------------------------------

  //-------------------------------------
  /**
   * Opens a connection to the mail server.
   * 
   * @return Transport object representing the open connection
   * @exception EmailException if the connection could not be
   * opened
   **/
  public Transport openConnection() throws EmailException;

  //-------------------------------------
  /**
   * Sends an email message.  The parameters of the email are 
   * specified within the Message object.  The Transport object
   * identifies the connection over which the message will be 
   * sent; the connection must have already been opened.
   * 
   * @exception EmailException if the connection is not open, 
   * or if the message could not be sent
   **/
  public void sendEmailMessage(Message pMessage, Transport pTransport) 
       throws EmailException;

  //-------------------------------------
  /**
   * Sends an email message.  The parameters of the email are 
   * specified within the Message object.  A new connection to
   * the mail server will be opened, the message sent, and the
   * connection closed.
   * 
   * @exception EmailException if the message could not be sent
   **/
  public void sendEmailMessage(Message pMessage) 
       throws EmailException;

  //-------------------------------------
  /**
   * Closes the connection identified by the Transport object.
   * 
   * @exception EmailException if the connection could not be
   * closed
   **/
  public void closeConnection(Transport pTransport) 
       throws EmailException;

  //-------------------------------------
}
