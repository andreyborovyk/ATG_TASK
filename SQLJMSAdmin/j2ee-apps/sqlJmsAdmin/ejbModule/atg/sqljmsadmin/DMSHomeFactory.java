/*
 * <ATGCOPYRIGHT> Copyright (C) 1997-2011 Art Technology Group, Inc. All Rights
 * Reserved. No use, copying or distribution ofthis work may be made except in
 * accordance with a valid license agreement from Art Technology Group. This
 * notice must be included on all copies, modifications and derivatives of this
 * work. Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT
 * THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES. "Dynamo" is a trademark of Art Technology
 * Group, Inc. </ATGCOPYRIGHT>
 */

package atg.sqljmsadmin;

import atg.j2ee.idgen.IdGeneratorHome;

import java.io.Serializable;
import java.rmi.RemoteException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

/****************************************
 * This class provides an easy way for callers to get home interfaces for all
 * the entity beans for the sql jms admin ui.
 * 
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id:
 *          //product/DAS/main/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule
 *          /atg/sqljmsadmin/DMSHomeFactory.java#8 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSHomeFactory implements Serializable {
  // ----------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSHomeFactory.java#2 $$Change: 651448 $";

  // ----------------------------------------
  // Constants
  // ----------------------------------------

  // jndi name for admin home
  private final static String kAdminHomeName = "java:comp/env/ejb/DMSAdmin";

  // jndi name for client home
  private final static String kClientHomeName = "java:comp/env/ejb/DMSClient";

  // jndi name for queue home
  private final static String kQueueHomeName = "java:comp/env/ejb/DMSQueue";

  // jndi name for queue entry home
  private final static String kQueueEntryHomeName = "java:comp/env/ejb/DMSQueueEntry";

  // jndi name for topic home
  private final static String kTopicHomeName = "java:comp/env/ejb/DMSTopic";

  // jndi name for topic subscription home
  private final static String kTopicSubscriptionHomeName = "java:comp/env/ejb/DMSTopicSubscription";

  // jndi name for topic entry home
  private final static String kTopicEntryHomeName = "java:comp/env/ejb/DMSTopicEntry";

  // jndi name for message home
  private final static String kMessageHomeName = "java:comp/env/ejb/DMSMessage";

  // jndi name for message property home
  private final static String kMessagePropertyHomeName = "java:comp/env/ejb/DMSMessageProperty";

  // jndi name for id generator
  private final static String kIdGeneratorHomeName = "java:comp/env/ejb/IdGenerator";

  // ----------------------------------------
  // Member Variables
  // ----------------------------------------

  // ----------------------------------------
  // Properties
  // ----------------------------------------

  // ----------------------------------------
  // Constructors
  // ----------------------------------------

  // ----------------------------------------
  /**
   * Constructs an instanceof DMSHomeFactory
   */
  public DMSHomeFactory() {

  }

  // ----------------------------------------
  // Object Methods
  // ----------------------------------------

  // ----------------------------------------
  /**
   * get an Admin home
   */
  public DMSAdminHome getAdminHome() throws NamingException, RemoteException {
    Context initialContext = new InitialContext();
    Object objectRef = initialContext.lookup(kAdminHomeName);
    return (DMSAdminHome) PortableRemoteObject.narrow(objectRef,
        DMSAdminHome.class);
  }

  // ----------------------------------------
  /**
   * get a Client home
   */
  public DMSClientHome getClientHome() throws NamingException, RemoteException {
    Context initialContext = new InitialContext();
    Object objectRef = initialContext.lookup(kClientHomeName);
    return (DMSClientHome) PortableRemoteObject.narrow(objectRef,
        DMSClientHome.class);
  }

  // ----------------------------------------
  /**
   * get a Queue home
   */
  public DMSQueueHome getQueueHome() throws NamingException, RemoteException {
    Context initialContext = new InitialContext();
    Object objectRef = initialContext.lookup(kQueueHomeName);
    return (DMSQueueHome) PortableRemoteObject.narrow(objectRef,
        DMSQueueHome.class);
  }

  // ----------------------------------------
  /**
   * get a QueueEntry home
   */
  public DMSQueueEntryHome getQueueEntryHome() throws NamingException,
      RemoteException {
    Context initialContext = new InitialContext();
    Object objectRef = initialContext.lookup(kQueueEntryHomeName);
    return (DMSQueueEntryHome) PortableRemoteObject.narrow(objectRef,
        DMSQueueEntryHome.class);
  }

  // ----------------------------------------
  /**
   * get a Topic home
   */
  public DMSTopicHome getTopicHome() throws NamingException, RemoteException {
    Context initialContext = new InitialContext();
    Object objectRef = initialContext.lookup(kTopicHomeName);
    return (DMSTopicHome) PortableRemoteObject.narrow(objectRef,
        DMSTopicHome.class);
  }

  // ----------------------------------------
  /**
   * get a TopicSubscription home
   */
  public DMSTopicSubscriptionHome getTopicSubscriptionHome()
      throws NamingException, RemoteException {
    Context initialContext = new InitialContext();
    Object objectRef = initialContext.lookup(kTopicSubscriptionHomeName);
    return (DMSTopicSubscriptionHome) PortableRemoteObject.narrow(objectRef,
        DMSTopicSubscriptionHome.class);
  }

  // ----------------------------------------
  /**
   * get a TopicEntry home
   */
  public DMSTopicEntryHome getTopicEntryHome() throws NamingException,
      RemoteException {
    Context initialContext = new InitialContext();
    Object objectRef = initialContext.lookup(kTopicEntryHomeName);
    return (DMSTopicEntryHome) PortableRemoteObject.narrow(objectRef,
        DMSTopicEntryHome.class);
  }

  // ----------------------------------------
  /**
   * get a Message home
   */
  public DMSMessageHome getMessageHome() throws NamingException,
      RemoteException {
    Context initialContext = new InitialContext();
    Object objectRef = initialContext.lookup(kMessageHomeName);
    return (DMSMessageHome) PortableRemoteObject.narrow(objectRef,
        DMSMessageHome.class);
  }

  // ----------------------------------------
  /**
   * get a MessageProperty home
   */
  public DMSMessagePropertyHome getMessagePropertyHome()
      throws NamingException, RemoteException {
    Context initialContext = new InitialContext();
    Object objectRef = initialContext.lookup(kMessagePropertyHomeName);
    return (DMSMessagePropertyHome) PortableRemoteObject.narrow(objectRef,
        DMSMessagePropertyHome.class);
  }

  // ----------------------------------------
  /**
   * get an IdGenerator home
   */
  public IdGeneratorHome getIdGeneratorHome() throws NamingException,
      RemoteException {
    Context initialContext = new InitialContext();
    Object objectRef = initialContext.lookup(kIdGeneratorHomeName);
    return (IdGeneratorHome) PortableRemoteObject.narrow(objectRef,
        IdGeneratorHome.class);
  }

  // ----------------------------------------
  // Static Methods
  // ----------------------------------------

} // end of class
