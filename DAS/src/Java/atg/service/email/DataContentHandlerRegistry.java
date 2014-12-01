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

import java.util.Properties;
import java.util.Enumeration;
import javax.activation.DataContentHandler;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import atg.nucleus.GenericService;

/** 
 * A service which serves as a registry of 
 * <code>javax.activation.DataContentHandler</code> 
 * implementations corresponding to particular MIME types.  
 * Whenever the service's <code>dataContentHandlerMap</code> 
 * property is set, the default JAF <code>CommandMap</code> is 
 * updated to include the additional content handlers.
 * 
 * <p>Thus, this service is basically a programmatic extension
 * of a mailcap file (see JAF documentation, particularly the
 * <code>javax.activation.MailcapCommandMap</code> class, for
 * more information).  
 * 
 * @see javax.activation.DataContentHandler
 * @see javax.activation.MailcapCommandMap
 * @author Natalya Cohen
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/service/email/DataContentHandlerRegistry.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class DataContentHandlerRegistry extends GenericService {
  //-------------------------------------
  // Constants
  //-------------------------------------

  /** Class version string **/
  public static String CLASS_VERSION = 
  "$Id: //product/DAS/version/10.0.3/Java/atg/service/email/DataContentHandlerRegistry.java#2 $$Change: 651448 $";

  /** Resource bundle **/
  private static java.util.ResourceBundle sResourceBundle = 
  java.util.ResourceBundle.getBundle("atg.service.ServiceResources", atg.service.dynamo.LangLicense.getLicensedDefault());

  /** String used to create a content handler entry in the mailcap **/
  static final String MAILCAP_STRING = "; ; x-java-content-handler=";

  //-------------------------------------
  // Member variables
  //-------------------------------------

  /** Maps MIME types to DataContentHandler class names **/
  Properties mDataContentHandlerMap = null;

  //-------------------------------------
  // Methods
  //-------------------------------------

  //-------------------------------------
  /** 
   * Returns the mapping between MIME types and class names of
   * <code>javax.activation.DataContentHandler</code> 
   * implementations.  For each mapping in this map, an 
   * association will be made between the MIME type and the 
   * <code>DataContentHandler</code> which should be used to 
   * transfer the content of that type.  For example, if a mapping 
   * exists between "text/html" and 
   * <code>HtmlDataContentHandler</code>, then the 
   * <code>HtmlDataContentHandler</code> class will be used when 
   * operating on content of type "text/html."
   * 
   * @see javax.activation.DataContentHandler
   **/
  public Properties getDataContentHandlerMap() {
    return mDataContentHandlerMap;
  }

  //-------------------------------------
  /** 
   * Sets the mapping between MIME types and class names of
   * <code>javax.activation.DataContentHandler</code> 
   * implementations.  For each mapping in this map, an 
   * association will be made between the MIME type and the 
   * <code>DataContentHandler</code> which should be used to 
   * transfer the content of that type.  For example, if a mapping
   * exists between "text/html" and 
   * <code>HtmlDataContentHandler</code>, then the 
   * <code>HtmlDataContentHandler</code> class will be used when 
   * operating on content of type "text/html."
   * 
   * @see javax.activation.DataContentHandler
   **/
  public void setDataContentHandlerMap(Properties pDataContentHandlerMap) {
    if (pDataContentHandlerMap == null) {
      if (mDataContentHandlerMap != null) {
	mDataContentHandlerMap = null;
	// reset to the original JAF default command map
	CommandMap.setDefaultCommandMap(null);
      }
    } else {
      mDataContentHandlerMap = pDataContentHandlerMap;
      // update the default command map with the given entries
      MailcapCommandMap commandMap = new MailcapCommandMap();
      Enumeration e = mDataContentHandlerMap.keys(); 
      while (e.hasMoreElements()) {
	String mimeType = (String) e.nextElement();
	String className = (String) mDataContentHandlerMap.get(mimeType);
	String mailcap = mimeType + MAILCAP_STRING + className;
	commandMap.addMailcap(mailcap);
      }
      CommandMap.setDefaultCommandMap(commandMap);
    }
  }

  //-------------------------------------
}


