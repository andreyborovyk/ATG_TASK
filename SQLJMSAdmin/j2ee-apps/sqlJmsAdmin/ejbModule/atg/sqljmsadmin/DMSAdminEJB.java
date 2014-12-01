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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

/****************************************
 * implementation for DMSAdmin sesion bean
 * 
 * @author Stephen Abramson <stephena@atg.com>
 * @version $Id:
 *          //product/DAS/main/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule
 *          /atg/sqljmsadmin/DMSAdminEJB.java#8 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 ****************************************/

public class DMSAdminEJB implements SessionBean {
  /** serialVersionUID - long */
  private static final long serialVersionUID = 1L;

  // ----------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/ejbModule/atg/sqljmsadmin/DMSAdminEJB.java#2 $$Change: 651448 $";

  // ----------------------------------------
  // Constants
  // ----------------------------------------

  // ----------------------------------------
  // Member Variables
  // ----------------------------------------

  // session context
  private SessionContext mSessionContext;

  // home factory
  private DMSHomeFactory mHomeFactory;

  // ----------------------------------------
  // Properties
  // ----------------------------------------

  // ----------------------------------------
  // Constructors
  // ----------------------------------------

  // ----------------------------------------
  /**
   * Constructs an instanceof DMSAdminEJB
   */
  public DMSAdminEJB() {
    mHomeFactory = new DMSHomeFactory();
  }

  // ----------------------------------------
  // Object Methods
  // ----------------------------------------

  // ----------------------------------------
  /**
   * get all the clients currently in a jms system
   */
  public DMSClient[] getAllClients() throws RemoteException, FinderException,
      NamingException, DMSAdminException {
    DMSClientHome home = mHomeFactory.getClientHome();

    Collection<Object> clients = new ArrayList<Object>();
    for (Object object : home.findAll()) {
      try {
        clients.add(PortableRemoteObject.narrow(object, DMSClient.class));
      }
      catch (ClassCastException e) {
        e.printStackTrace();
      }
    }
    return clients.toArray(new DMSClient[0]);

  }

  // ----------------------------------------
  /**
   * get all the queues currently in a jms system
   */
  public DMSQueue[] getAllQueues() throws RemoteException, FinderException,
      NamingException, DMSAdminException {
    DMSQueueHome home = mHomeFactory.getQueueHome();

    Collection<Object> queues = new ArrayList<Object>();
    for (Object object : home.findAll()) {
      try {
        queues.add(PortableRemoteObject.narrow(object, DMSQueue.class));
      }
      catch (ClassCastException e) {
        e.printStackTrace();
      }
    }
    return queues.toArray(new DMSQueue[0]);
  }

  // ----------------------------------------
  /**
   * get all the topics currently in a jms system
   */
  public DMSTopic[] getAllTopics() throws RemoteException, FinderException,
      NamingException, DMSAdminException {
    DMSTopicHome home = mHomeFactory.getTopicHome();

    Collection<Object> topics = new ArrayList<Object>();
    for (Object object : home.findAll()) {
      try {
        topics.add(PortableRemoteObject.narrow(object, DMSTopic.class));
      }
      catch (ClassCastException e) {
        e.printStackTrace();
      }
    }

    return topics.toArray(new DMSTopic[0]);
  }

  // ----------------------------------------
  /**
   * get the home factory
   */
  public DMSHomeFactory getHomeFactory() {
    return mHomeFactory;
  }

  // ----------------------------------------
  /**
   * create
   */
  public void ejbCreate() {
    // Empty create method.
  }

  // ----------------------------------------
  /**
   * set the session context
   */
  public void setSessionContext(SessionContext pSessionContext)
      throws EJBException, RemoteException {
    mSessionContext = pSessionContext;
  }

  // ----------------------------------------
  /**
   * remove this session bean
   */
  public void ejbRemove() throws EJBException, RemoteException {
    // Empty Remove method.
  }

  // ----------------------------------------
  /**
   * activate this session bean
   */
  public void ejbActivate() throws EJBException, RemoteException {
    // Empty Activate Method
  }

  // ----------------------------------------
  /**
   * passivate this session bean
   */
  public void ejbPassivate() throws EJBException, RemoteException {
    // Empty Activate Method
  }

  // ----------------------------------------
  // Static Methods
  // ----------------------------------------

} // end of class
