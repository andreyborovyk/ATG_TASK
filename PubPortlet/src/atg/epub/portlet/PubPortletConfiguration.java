/*<ATGCOPYRIGHT>
 * Copyright (C) 2003-2011 Art Technology Group, Inc.
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

package atg.epub.portlet;

/**
 * A globally-scoped Nucleus component that stores application
 * configuration info.
 *
 * @author dlee
 * @version $Id: //product/PubPortlet/version/10.0.3/classes.jar/atg/epub/portlet/PubPortletConfiguration.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class PubPortletConfiguration {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //product/PubPortlet/version/10.0.3/classes.jar/atg/epub/portlet/PubPortletConfiguration.java#2 $$Change: 651448 $";


  
  //--------------------------------------------------------------------------//
  // Property accessors
  //--------------------------------------------------------------------------//

  //--------------------------------------------------------------------------//
  /** Property contextRoot */
  private String mContextRoot;
  
  /**
   * Set the context root of the application.
   * 
   * @param pContextRoot  The context root of the application
   */
  public void setContextRoot(String pContextRoot) {
    mContextRoot = pContextRoot;
  }
  
  /**
   * Get the context root of the application.
   * 
   * @return  The context root of the application
   */
  public String getContextRoot() {
    return mContextRoot;
  }
  	
}
