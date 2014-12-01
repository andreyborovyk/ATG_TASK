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

/**
 * A simple bean with a message property
 *
 * @author Andrew Rickard
 * @version $Id: //app/portal/version/10.0.3/portlet/helloworld/classes/atg/portlet/helloworld/HelloWorld.java#2 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class HelloWorld
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/portlet/helloworld/classes/atg/portlet/helloworld/HelloWorld.java#2 $$Change: 651448 $";

  
  private String m_message;

  /**
   * Constructs an instance of HelloWorld
   */
  public HelloWorld() {
  }

  /**
   * Sets property message
   **/
  public void setMessage(String p_message) {
    m_message = p_message;
  }

  /**
   * Returns property message
   **/
  public String getMessage() {
    return m_message;
  }
  
} // end of class
