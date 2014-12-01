/*<ATGCOPYRIGHT>
 * Copyright (C) 2004-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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
package atg.portlet.helloworld;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletException;
import java.io.IOException;

/**
 * Form handler used to modify the message property of a HelloWorld bean.
 * 
 * @author Andrew Rickard
 * @version $Id:
 *	//app/portal/main/portlet/helloworld/classes/atg/portlet/helloworld/HelloWorldFormHandler.java#4 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class HelloWorldFormHandler
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/portlet/helloworld/classes/atg/portlet/helloworld/HelloWorldFormHandler.java#2 $$Change: 651448 $";

    private HelloWorld m_helloWorld;
  private String m_message;

  /**
   * Constructs an instance of HelloWorldFormHandler
   */
  public HelloWorldFormHandler()
  {
  }

  /**
   * Handle the message update
   * 
   * @param pRequest 
   * 	the portlet's request
   * @param pResponse 
   * 	the portlet's response
   * @exception 
   * 	ServletException if there was an error while executing the code
   * @exception 
   * 	IOException if there was an error with servlet io
   */
  public boolean handleUpdate(PortletRequest pRequest, PortletResponse pResponse)
  	throws PortletException, IOException
  {
    HelloWorld helloWorld = getHelloWorld();
    if (helloWorld != null)
    {
      helloWorld.setMessage(getMessage());
    }
    return true;
  }

  /**
   * Sets property helloWorld
   */
  public void setHelloWorld(HelloWorld p_helloWorld)
  {
    m_helloWorld = p_helloWorld;
  }

  /**
   * Returns property HelloWorld
   */
  public HelloWorld getHelloWorld()
  {
    return m_helloWorld;
  }

  /**
   * Sets property Message
   */
  public void setMessage(String p_message)
  {
    m_message = p_message;
  }

  /**
   * Returns property Message
   */
  public String getMessage()
  {
    return m_message;
  }
} // end of class
