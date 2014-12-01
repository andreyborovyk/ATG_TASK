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

package atg.droplet.xml;

import java.beans.*;
import atg.droplet.*;
import atg.servlet.DynamoServlet;

/**
 * <p>BeanInfo for the XMLToDOM droplet.
 *
 * @author Allan Scott
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/xml/XMLToDOMBeanInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class XMLToDOMBeanInfo extends DropletBeanInfo {
  //-------------------------------------
  // CONSTANTS
  //-------------------------------------
  public static String CLASS_VERSION = 
  "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/xml/XMLToDOMBeanInfo.java#2 $$Change: 651448 $";

  //-------------------------------------
  // FIELDS
  //-------------------------------------
  
  private final static ParamDescriptor[] sErrorDescriptors = {
    new ParamDescriptor("errors",
			"This parameter will be bound to an Enumeration of Exceptions if" +
			" there were failures when parsing or retrieving the XML" +
			" document",
			java.util.Enumeration.class, false, false)
  };

  private final static ParamDescriptor[] sOutputDescriptors = {
    new ParamDescriptor("document",
			"This parameter will be bound to a DOM document if the" +
			" XML document was successfully retrieved and parsed",
			org.w3c.dom.Document.class, false, false)
  };

  private final static ParamDescriptor[] sParamDescriptors = {
    
     new ParamDescriptor("input",
			 "The XML document to be parsed. This can be either" +
			 " an absolute or relative URL for an XML document",
			 String.class, false, false),

     new ParamDescriptor("failure", 
			 "This oparam will be used to format output when there was a" +
			 " failure to parse or retrieve the XML document",
			 DynamoServlet.class, false, true,
			 sErrorDescriptors),

     new ParamDescriptor("validate",
			 "If the XML document specified by input parameter has" +
			 " a DTD specified, should the parser validate according" +
			 " to that DTD? Validation will be slower but may help prevent" + 
			 "problems later when accessing parts of the XML document",
			 Object.class, true, false),

     new ParamDescriptor("output", 
			 "The oparam used when a parse was successful",
			 DynamoServlet.class, false, true, 
			 sOutputDescriptors)
  };

  private final static BeanDescriptor sBeanDescriptor = 
    createBeanDescriptor(XMLToDOM.class, 
			 null,
			 "Parse an XML document into an org.w3c.dom.Document",
			 sParamDescriptors);

  //-------------------------------------
  // METHODS
  //-------------------------------------

  //-------------------------------------
  /**
   * Returns the BeanDescriptor for this bean, which will in turn 
   * contain ParamDescriptors for the droplet.
   **/
  public BeanDescriptor getBeanDescriptor() {
    return sBeanDescriptor;
  }

  //----------------------------------------
}



