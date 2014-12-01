/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2011 Art Technology Group, Inc.
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

package atg.projects.store.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * This droplet is escaped apostrophe with \'. 
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/droplet/EscapeJS.java#2 $
 */
public class EscapeJS extends DynamoServlet {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/droplet/EscapeJS.java#2 $$Change: 651448 $";

  /** Input parameter name value. */
  public static final ParameterName VALUE = ParameterName.getParameterName("value");
  
  public static final String OUTPUT_VALUE = "escapedValue";
  
  /** The oparam name rendered once during processing.*/
  public static final String OPARAM_OUTPUT = "output";

  /**
   *  Replaced all occurrences of apostrophe with '\
   */
  public void service(DynamoHttpServletRequest pReq, DynamoHttpServletResponse pRes) throws ServletException, IOException {
    Object value = pReq.getObjectParameter(VALUE);
    String escapedValue = null;
    
    if(value instanceof String) {
      escapedValue = ((String)value).replaceAll("\'", "\\\\'");
    } 
    
    pReq.setParameter(OUTPUT_VALUE, escapedValue);
    pReq.serviceLocalParameter(OPARAM_OUTPUT, pReq, pRes);
  }
}
